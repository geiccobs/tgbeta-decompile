package com.google.android.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.mp4.TrackEncryptionBox;
import com.google.android.exoplayer2.offline.FilterableManifest;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
/* loaded from: classes3.dex */
public class SsManifest implements FilterableManifest<SsManifest> {
    public static final int UNSET_LOOKAHEAD = -1;
    public final long durationUs;
    public final long dvrWindowLengthUs;
    public final boolean isLive;
    public final int lookAheadCount;
    public final int majorVersion;
    public final int minorVersion;
    public final ProtectionElement protectionElement;
    public final StreamElement[] streamElements;

    /* loaded from: classes3.dex */
    public static class ProtectionElement {
        public final byte[] data;
        public final TrackEncryptionBox[] trackEncryptionBoxes;
        public final UUID uuid;

        public ProtectionElement(UUID uuid, byte[] data, TrackEncryptionBox[] trackEncryptionBoxes) {
            this.uuid = uuid;
            this.data = data;
            this.trackEncryptionBoxes = trackEncryptionBoxes;
        }
    }

    /* loaded from: classes3.dex */
    public static class StreamElement {
        private static final String URL_PLACEHOLDER_BITRATE_1 = "{bitrate}";
        private static final String URL_PLACEHOLDER_BITRATE_2 = "{Bitrate}";
        private static final String URL_PLACEHOLDER_START_TIME_1 = "{start time}";
        private static final String URL_PLACEHOLDER_START_TIME_2 = "{start_time}";
        private final String baseUri;
        public final int chunkCount;
        private final List<Long> chunkStartTimes;
        private final long[] chunkStartTimesUs;
        private final String chunkTemplate;
        public final int displayHeight;
        public final int displayWidth;
        public final Format[] formats;
        public final String language;
        private final long lastChunkDurationUs;
        public final int maxHeight;
        public final int maxWidth;
        public final String name;
        public final String subType;
        public final long timescale;
        public final int type;

        public StreamElement(String baseUri, String chunkTemplate, int type, String subType, long timescale, String name, int maxWidth, int maxHeight, int displayWidth, int displayHeight, String language, Format[] formats, List<Long> chunkStartTimes, long lastChunkDuration) {
            this(baseUri, chunkTemplate, type, subType, timescale, name, maxWidth, maxHeight, displayWidth, displayHeight, language, formats, chunkStartTimes, Util.scaleLargeTimestamps(chunkStartTimes, 1000000L, timescale), Util.scaleLargeTimestamp(lastChunkDuration, 1000000L, timescale));
        }

        private StreamElement(String baseUri, String chunkTemplate, int type, String subType, long timescale, String name, int maxWidth, int maxHeight, int displayWidth, int displayHeight, String language, Format[] formats, List<Long> chunkStartTimes, long[] chunkStartTimesUs, long lastChunkDurationUs) {
            this.baseUri = baseUri;
            this.chunkTemplate = chunkTemplate;
            this.type = type;
            this.subType = subType;
            this.timescale = timescale;
            this.name = name;
            this.maxWidth = maxWidth;
            this.maxHeight = maxHeight;
            this.displayWidth = displayWidth;
            this.displayHeight = displayHeight;
            this.language = language;
            this.formats = formats;
            this.chunkStartTimes = chunkStartTimes;
            this.chunkStartTimesUs = chunkStartTimesUs;
            this.lastChunkDurationUs = lastChunkDurationUs;
            this.chunkCount = chunkStartTimes.size();
        }

        public StreamElement copy(Format[] formats) {
            return new StreamElement(this.baseUri, this.chunkTemplate, this.type, this.subType, this.timescale, this.name, this.maxWidth, this.maxHeight, this.displayWidth, this.displayHeight, this.language, formats, this.chunkStartTimes, this.chunkStartTimesUs, this.lastChunkDurationUs);
        }

        public int getChunkIndex(long timeUs) {
            return Util.binarySearchFloor(this.chunkStartTimesUs, timeUs, true, true);
        }

        public long getStartTimeUs(int chunkIndex) {
            return this.chunkStartTimesUs[chunkIndex];
        }

        public long getChunkDurationUs(int chunkIndex) {
            if (chunkIndex == this.chunkCount - 1) {
                return this.lastChunkDurationUs;
            }
            long[] jArr = this.chunkStartTimesUs;
            return jArr[chunkIndex + 1] - jArr[chunkIndex];
        }

        public Uri buildRequestUri(int track, int chunkIndex) {
            boolean z = true;
            Assertions.checkState(this.formats != null);
            Assertions.checkState(this.chunkStartTimes != null);
            if (chunkIndex >= this.chunkStartTimes.size()) {
                z = false;
            }
            Assertions.checkState(z);
            String bitrateString = Integer.toString(this.formats[track].bitrate);
            String startTimeString = this.chunkStartTimes.get(chunkIndex).toString();
            String chunkUrl = this.chunkTemplate.replace(URL_PLACEHOLDER_BITRATE_1, bitrateString).replace(URL_PLACEHOLDER_BITRATE_2, bitrateString).replace(URL_PLACEHOLDER_START_TIME_1, startTimeString).replace(URL_PLACEHOLDER_START_TIME_2, startTimeString);
            return UriUtil.resolveToUri(this.baseUri, chunkUrl);
        }
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public SsManifest(int r18, int r19, long r20, long r22, long r24, int r26, boolean r27, com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.ProtectionElement r28, com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement[] r29) {
        /*
            r17 = this;
            r0 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            r2 = 0
            int r4 = (r22 > r2 ? 1 : (r22 == r2 ? 0 : -1))
            if (r4 != 0) goto Le
            r9 = r0
            goto L1a
        Le:
            r6 = 1000000(0xf4240, double:4.940656E-318)
            r4 = r22
            r8 = r20
            long r4 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r4, r6, r8)
            r9 = r4
        L1a:
            int r4 = (r24 > r2 ? 1 : (r24 == r2 ? 0 : -1))
            if (r4 != 0) goto L20
            r11 = r0
            goto L2c
        L20:
            r13 = 1000000(0xf4240, double:4.940656E-318)
            r11 = r24
            r15 = r20
            long r0 = com.google.android.exoplayer2.util.Util.scaleLargeTimestamp(r11, r13, r15)
            r11 = r0
        L2c:
            r6 = r17
            r7 = r18
            r8 = r19
            r13 = r26
            r14 = r27
            r15 = r28
            r16 = r29
            r6.<init>(r7, r8, r9, r11, r13, r14, r15, r16)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest.<init>(int, int, long, long, long, int, boolean, com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest$ProtectionElement, com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest$StreamElement[]):void");
    }

    private SsManifest(int majorVersion, int minorVersion, long durationUs, long dvrWindowLengthUs, int lookAheadCount, boolean isLive, ProtectionElement protectionElement, StreamElement[] streamElements) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.durationUs = durationUs;
        this.dvrWindowLengthUs = dvrWindowLengthUs;
        this.lookAheadCount = lookAheadCount;
        this.isLive = isLive;
        this.protectionElement = protectionElement;
        this.streamElements = streamElements;
    }

    @Override // com.google.android.exoplayer2.offline.FilterableManifest
    public final SsManifest copy(List<StreamKey> streamKeys) {
        ArrayList<StreamKey> sortedKeys = new ArrayList<>(streamKeys);
        Collections.sort(sortedKeys);
        StreamElement currentStreamElement = null;
        List<StreamElement> copiedStreamElements = new ArrayList<>();
        List<Format> copiedFormats = new ArrayList<>();
        for (int i = 0; i < sortedKeys.size(); i++) {
            StreamKey key = sortedKeys.get(i);
            StreamElement streamElement = this.streamElements[key.groupIndex];
            if (streamElement != currentStreamElement && currentStreamElement != null) {
                copiedStreamElements.add(currentStreamElement.copy((Format[]) copiedFormats.toArray(new Format[0])));
                copiedFormats.clear();
            }
            currentStreamElement = streamElement;
            copiedFormats.add(streamElement.formats[key.trackIndex]);
        }
        if (currentStreamElement != null) {
            copiedStreamElements.add(currentStreamElement.copy((Format[]) copiedFormats.toArray(new Format[0])));
        }
        StreamElement[] copiedStreamElementsArray = (StreamElement[]) copiedStreamElements.toArray(new StreamElement[0]);
        return new SsManifest(this.majorVersion, this.minorVersion, this.durationUs, this.dvrWindowLengthUs, this.lookAheadCount, this.isLive, this.protectionElement, copiedStreamElementsArray);
    }
}
