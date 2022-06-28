package com.google.android.exoplayer2.source.dash;

import android.os.Handler;
import android.os.Message;
import androidx.exifinterface.media.ExifInterface;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataInputBuffer;
import com.google.android.exoplayer2.metadata.emsg.EventMessage;
import com.google.android.exoplayer2.metadata.emsg.EventMessageDecoder;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.android.exoplayer2.source.SampleQueue;
import com.google.android.exoplayer2.source.chunk.Chunk;
import com.google.android.exoplayer2.source.dash.manifest.DashManifest;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
/* loaded from: classes3.dex */
public final class PlayerEmsgHandler implements Handler.Callback {
    private static final int EMSG_MANIFEST_EXPIRED = 1;
    private final Allocator allocator;
    private long expiredManifestPublishTimeUs;
    private boolean isWaitingForManifestRefresh;
    private DashManifest manifest;
    private final PlayerEmsgCallback playerEmsgCallback;
    private boolean released;
    private final TreeMap<Long, Long> manifestPublishTimeToExpiryTimeUs = new TreeMap<>();
    private final Handler handler = Util.createHandler(this);
    private final EventMessageDecoder decoder = new EventMessageDecoder();
    private long lastLoadedChunkEndTimeUs = C.TIME_UNSET;
    private long lastLoadedChunkEndTimeBeforeRefreshUs = C.TIME_UNSET;

    /* loaded from: classes3.dex */
    public interface PlayerEmsgCallback {
        void onDashManifestPublishTimeExpired(long j);

        void onDashManifestRefreshRequested();
    }

    public PlayerEmsgHandler(DashManifest manifest, PlayerEmsgCallback playerEmsgCallback, Allocator allocator) {
        this.manifest = manifest;
        this.playerEmsgCallback = playerEmsgCallback;
        this.allocator = allocator;
    }

    public void updateManifest(DashManifest newManifest) {
        this.isWaitingForManifestRefresh = false;
        this.expiredManifestPublishTimeUs = C.TIME_UNSET;
        this.manifest = newManifest;
        removePreviouslyExpiredManifestPublishTimeValues();
    }

    boolean maybeRefreshManifestBeforeLoadingNextChunk(long presentationPositionUs) {
        if (!this.manifest.dynamic) {
            return false;
        }
        if (this.isWaitingForManifestRefresh) {
            return true;
        }
        boolean manifestRefreshNeeded = false;
        Map.Entry<Long, Long> expiredEntry = ceilingExpiryEntryForPublishTime(this.manifest.publishTimeMs);
        if (expiredEntry != null) {
            long expiredPointUs = expiredEntry.getValue().longValue();
            if (expiredPointUs < presentationPositionUs) {
                this.expiredManifestPublishTimeUs = expiredEntry.getKey().longValue();
                notifyManifestPublishTimeExpired();
                manifestRefreshNeeded = true;
            }
        }
        if (manifestRefreshNeeded) {
            maybeNotifyDashManifestRefreshNeeded();
        }
        return manifestRefreshNeeded;
    }

    boolean maybeRefreshManifestOnLoadingError(Chunk chunk) {
        if (!this.manifest.dynamic) {
            return false;
        }
        if (this.isWaitingForManifestRefresh) {
            return true;
        }
        long j = this.lastLoadedChunkEndTimeUs;
        boolean isAfterForwardSeek = j != C.TIME_UNSET && j < chunk.startTimeUs;
        if (!isAfterForwardSeek) {
            return false;
        }
        maybeNotifyDashManifestRefreshNeeded();
        return true;
    }

    void onChunkLoadCompleted(Chunk chunk) {
        if (this.lastLoadedChunkEndTimeUs != C.TIME_UNSET || chunk.endTimeUs > this.lastLoadedChunkEndTimeUs) {
            this.lastLoadedChunkEndTimeUs = chunk.endTimeUs;
        }
    }

    public static boolean isPlayerEmsgEvent(String schemeIdUri, String value) {
        return "urn:mpeg:dash:event:2012".equals(schemeIdUri) && (IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE.equals(value) || ExifInterface.GPS_MEASUREMENT_2D.equals(value) || ExifInterface.GPS_MEASUREMENT_3D.equals(value));
    }

    public PlayerTrackEmsgHandler newPlayerTrackEmsgHandler() {
        return new PlayerTrackEmsgHandler(this.allocator);
    }

    public void release() {
        this.released = true;
        this.handler.removeCallbacksAndMessages(null);
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        if (this.released) {
            return true;
        }
        switch (message.what) {
            case 1:
                ManifestExpiryEventInfo messageObj = (ManifestExpiryEventInfo) message.obj;
                handleManifestExpiredMessage(messageObj.eventTimeUs, messageObj.manifestPublishTimeMsInEmsg);
                return true;
            default:
                return false;
        }
    }

    private void handleManifestExpiredMessage(long eventTimeUs, long manifestPublishTimeMsInEmsg) {
        Long previousExpiryTimeUs = this.manifestPublishTimeToExpiryTimeUs.get(Long.valueOf(manifestPublishTimeMsInEmsg));
        if (previousExpiryTimeUs == null) {
            this.manifestPublishTimeToExpiryTimeUs.put(Long.valueOf(manifestPublishTimeMsInEmsg), Long.valueOf(eventTimeUs));
        } else if (previousExpiryTimeUs.longValue() > eventTimeUs) {
            this.manifestPublishTimeToExpiryTimeUs.put(Long.valueOf(manifestPublishTimeMsInEmsg), Long.valueOf(eventTimeUs));
        }
    }

    private Map.Entry<Long, Long> ceilingExpiryEntryForPublishTime(long publishTimeMs) {
        return this.manifestPublishTimeToExpiryTimeUs.ceilingEntry(Long.valueOf(publishTimeMs));
    }

    private void removePreviouslyExpiredManifestPublishTimeValues() {
        Iterator<Map.Entry<Long, Long>> it = this.manifestPublishTimeToExpiryTimeUs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Long> entry = it.next();
            long expiredManifestPublishTime = entry.getKey().longValue();
            if (expiredManifestPublishTime < this.manifest.publishTimeMs) {
                it.remove();
            }
        }
    }

    private void notifyManifestPublishTimeExpired() {
        this.playerEmsgCallback.onDashManifestPublishTimeExpired(this.expiredManifestPublishTimeUs);
    }

    private void maybeNotifyDashManifestRefreshNeeded() {
        long j = this.lastLoadedChunkEndTimeBeforeRefreshUs;
        if (j != C.TIME_UNSET && j == this.lastLoadedChunkEndTimeUs) {
            return;
        }
        this.isWaitingForManifestRefresh = true;
        this.lastLoadedChunkEndTimeBeforeRefreshUs = this.lastLoadedChunkEndTimeUs;
        this.playerEmsgCallback.onDashManifestRefreshRequested();
    }

    public static long getManifestPublishTimeMsInEmsg(EventMessage eventMessage) {
        try {
            return Util.parseXsDateTime(Util.fromUtf8Bytes(eventMessage.messageData));
        } catch (ParserException e) {
            return C.TIME_UNSET;
        }
    }

    /* loaded from: classes3.dex */
    public final class PlayerTrackEmsgHandler implements TrackOutput {
        private final SampleQueue sampleQueue;
        private final FormatHolder formatHolder = new FormatHolder();
        private final MetadataInputBuffer buffer = new MetadataInputBuffer();

        PlayerTrackEmsgHandler(Allocator allocator) {
            PlayerEmsgHandler.this = this$0;
            this.sampleQueue = new SampleQueue(allocator, this$0.handler.getLooper(), DrmSessionManager.CC.getDummyDrmSessionManager());
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public void format(Format format) {
            this.sampleQueue.format(format);
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
            return this.sampleQueue.sampleData(input, length, allowEndOfInput);
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public void sampleData(ParsableByteArray data, int length) {
            this.sampleQueue.sampleData(data, length);
        }

        @Override // com.google.android.exoplayer2.extractor.TrackOutput
        public void sampleMetadata(long timeUs, int flags, int size, int offset, TrackOutput.CryptoData encryptionData) {
            this.sampleQueue.sampleMetadata(timeUs, flags, size, offset, encryptionData);
            parseAndDiscardSamples();
        }

        public boolean maybeRefreshManifestBeforeLoadingNextChunk(long presentationPositionUs) {
            return PlayerEmsgHandler.this.maybeRefreshManifestBeforeLoadingNextChunk(presentationPositionUs);
        }

        public void onChunkLoadCompleted(Chunk chunk) {
            PlayerEmsgHandler.this.onChunkLoadCompleted(chunk);
        }

        public boolean maybeRefreshManifestOnLoadingError(Chunk chunk) {
            return PlayerEmsgHandler.this.maybeRefreshManifestOnLoadingError(chunk);
        }

        public void release() {
            this.sampleQueue.release();
        }

        private void parseAndDiscardSamples() {
            while (this.sampleQueue.isReady(false)) {
                MetadataInputBuffer inputBuffer = dequeueSample();
                if (inputBuffer != null) {
                    long eventTimeUs = inputBuffer.timeUs;
                    Metadata metadata = PlayerEmsgHandler.this.decoder.decode(inputBuffer);
                    EventMessage eventMessage = (EventMessage) metadata.get(0);
                    if (PlayerEmsgHandler.isPlayerEmsgEvent(eventMessage.schemeIdUri, eventMessage.value)) {
                        parsePlayerEmsgEvent(eventTimeUs, eventMessage);
                    }
                }
            }
            this.sampleQueue.discardToRead();
        }

        private MetadataInputBuffer dequeueSample() {
            this.buffer.clear();
            int result = this.sampleQueue.read(this.formatHolder, this.buffer, false, false, 0L);
            if (result == -4) {
                this.buffer.flip();
                return this.buffer;
            }
            return null;
        }

        private void parsePlayerEmsgEvent(long eventTimeUs, EventMessage eventMessage) {
            long manifestPublishTimeMsInEmsg = PlayerEmsgHandler.getManifestPublishTimeMsInEmsg(eventMessage);
            if (manifestPublishTimeMsInEmsg == C.TIME_UNSET) {
                return;
            }
            onManifestExpiredMessageEncountered(eventTimeUs, manifestPublishTimeMsInEmsg);
        }

        private void onManifestExpiredMessageEncountered(long eventTimeUs, long manifestPublishTimeMsInEmsg) {
            ManifestExpiryEventInfo manifestExpiryEventInfo = new ManifestExpiryEventInfo(eventTimeUs, manifestPublishTimeMsInEmsg);
            PlayerEmsgHandler.this.handler.sendMessage(PlayerEmsgHandler.this.handler.obtainMessage(1, manifestExpiryEventInfo));
        }
    }

    /* loaded from: classes3.dex */
    public static final class ManifestExpiryEventInfo {
        public final long eventTimeUs;
        public final long manifestPublishTimeMsInEmsg;

        public ManifestExpiryEventInfo(long eventTimeUs, long manifestPublishTimeMsInEmsg) {
            this.eventTimeUs = eventTimeUs;
            this.manifestPublishTimeMsInEmsg = manifestPublishTimeMsInEmsg;
        }
    }
}
