package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes3.dex */
public final class SilenceMediaSource extends BaseMediaSource {
    private static final int CHANNEL_COUNT = 2;
    private static final int ENCODING = 2;
    private final long durationUs;
    private final Object tag;
    private static final int SAMPLE_RATE_HZ = 44100;
    private static final Format FORMAT = Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, -1, -1, 2, SAMPLE_RATE_HZ, 2, null, null, 0, null);
    private static final byte[] SILENCE_SAMPLE = new byte[Util.getPcmFrameSize(2, 2) * 1024];

    /* loaded from: classes3.dex */
    public static final class Factory {
        private long durationUs;
        private Object tag;

        public Factory setDurationUs(long durationUs) {
            this.durationUs = durationUs;
            return this;
        }

        public Factory setTag(Object tag) {
            this.tag = tag;
            return this;
        }

        public SilenceMediaSource createMediaSource() {
            return new SilenceMediaSource(this.durationUs, this.tag);
        }
    }

    public SilenceMediaSource(long durationUs) {
        this(durationUs, null);
    }

    private SilenceMediaSource(long durationUs, Object tag) {
        Assertions.checkArgument(durationUs >= 0);
        this.durationUs = durationUs;
        this.tag = tag;
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource
    protected void prepareSourceInternal(TransferListener mediaTransferListener) {
        refreshSourceInfo(new SinglePeriodTimeline(this.durationUs, true, false, false, null, this.tag));
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void maybeThrowSourceInfoRefreshError() {
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public MediaPeriod createPeriod(MediaSource.MediaPeriodId id, Allocator allocator, long startPositionUs) {
        return new SilenceMediaPeriod(this.durationUs);
    }

    @Override // com.google.android.exoplayer2.source.MediaSource
    public void releasePeriod(MediaPeriod mediaPeriod) {
    }

    @Override // com.google.android.exoplayer2.source.BaseMediaSource
    protected void releaseSourceInternal() {
    }

    /* loaded from: classes3.dex */
    private static final class SilenceMediaPeriod implements MediaPeriod {
        private static final TrackGroupArray TRACKS = new TrackGroupArray(new TrackGroup(SilenceMediaSource.FORMAT));
        private final long durationUs;
        private final ArrayList<SampleStream> sampleStreams = new ArrayList<>();

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public /* synthetic */ List getStreamKeys(List list) {
            List emptyList;
            emptyList = Collections.emptyList();
            return emptyList;
        }

        public SilenceMediaPeriod(long durationUs) {
            this.durationUs = durationUs;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public void prepare(MediaPeriod.Callback callback, long positionUs) {
            callback.onPrepared(this);
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public void maybeThrowPrepareError() {
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public TrackGroupArray getTrackGroups() {
            return TRACKS;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public long selectTracks(TrackSelection[] selections, boolean[] mayRetainStreamFlags, SampleStream[] streams, boolean[] streamResetFlags, long positionUs) {
            long positionUs2 = constrainSeekPosition(positionUs);
            for (int i = 0; i < selections.length; i++) {
                if (streams[i] != null && (selections[i] == null || !mayRetainStreamFlags[i])) {
                    this.sampleStreams.remove(streams[i]);
                    streams[i] = null;
                }
                if (streams[i] == null && selections[i] != null) {
                    SilenceSampleStream stream = new SilenceSampleStream(this.durationUs);
                    stream.seekTo(positionUs2);
                    this.sampleStreams.add(stream);
                    streams[i] = stream;
                    streamResetFlags[i] = true;
                }
            }
            return positionUs2;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public void discardBuffer(long positionUs, boolean toKeyframe) {
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public long readDiscontinuity() {
            return C.TIME_UNSET;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public long seekToUs(long positionUs) {
            long positionUs2 = constrainSeekPosition(positionUs);
            for (int i = 0; i < this.sampleStreams.size(); i++) {
                ((SilenceSampleStream) this.sampleStreams.get(i)).seekTo(positionUs2);
            }
            return positionUs2;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public long getAdjustedSeekPositionUs(long positionUs, SeekParameters seekParameters) {
            return constrainSeekPosition(positionUs);
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public long getBufferedPositionUs() {
            return Long.MIN_VALUE;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public long getNextLoadPositionUs() {
            return Long.MIN_VALUE;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public boolean continueLoading(long positionUs) {
            return false;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public boolean isLoading() {
            return false;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public void reevaluateBuffer(long positionUs) {
        }

        private long constrainSeekPosition(long positionUs) {
            return Util.constrainValue(positionUs, 0L, this.durationUs);
        }
    }

    /* loaded from: classes3.dex */
    private static final class SilenceSampleStream implements SampleStream {
        private final long durationBytes;
        private long positionBytes;
        private boolean sentFormat;

        public SilenceSampleStream(long durationUs) {
            this.durationBytes = SilenceMediaSource.getAudioByteCount(durationUs);
            seekTo(0L);
        }

        public void seekTo(long positionUs) {
            this.positionBytes = Util.constrainValue(SilenceMediaSource.getAudioByteCount(positionUs), 0L, this.durationBytes);
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public boolean isReady() {
            return true;
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public void maybeThrowError() {
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
            if (!this.sentFormat || formatRequired) {
                formatHolder.format = SilenceMediaSource.FORMAT;
                this.sentFormat = true;
                return -5;
            }
            long bytesRemaining = this.durationBytes - this.positionBytes;
            if (bytesRemaining != 0) {
                int bytesToWrite = (int) Math.min(SilenceMediaSource.SILENCE_SAMPLE.length, bytesRemaining);
                buffer.ensureSpaceForWrite(bytesToWrite);
                buffer.data.put(SilenceMediaSource.SILENCE_SAMPLE, 0, bytesToWrite);
                buffer.timeUs = SilenceMediaSource.getAudioPositionUs(this.positionBytes);
                buffer.addFlag(1);
                this.positionBytes += bytesToWrite;
                return -4;
            }
            buffer.addFlag(4);
            return -4;
        }

        @Override // com.google.android.exoplayer2.source.SampleStream
        public int skipData(long positionUs) {
            long oldPositionBytes = this.positionBytes;
            seekTo(positionUs);
            return (int) ((this.positionBytes - oldPositionBytes) / SilenceMediaSource.SILENCE_SAMPLE.length);
        }
    }

    public static long getAudioByteCount(long durationUs) {
        long audioSampleCount = (44100 * durationUs) / 1000000;
        return Util.getPcmFrameSize(2, 2) * audioSampleCount;
    }

    public static long getAudioPositionUs(long bytes) {
        long audioSampleCount = bytes / Util.getPcmFrameSize(2, 2);
        return (1000000 * audioSampleCount) / 44100;
    }
}
