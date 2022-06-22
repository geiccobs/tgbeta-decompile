package com.google.android.exoplayer2.source;

import android.os.Looper;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
/* loaded from: classes.dex */
public class SampleQueue implements TrackOutput {
    private int absoluteFirstIndex;
    private DrmSession<?> currentDrmSession;
    private Format downstreamFormat;
    private final DrmSessionManager<?> drmSessionManager;
    private boolean isLastSampleQueued;
    private int length;
    private boolean pendingSplice;
    private boolean pendingUpstreamFormatAdjustment;
    private final Looper playbackLooper;
    private int readPosition;
    private int relativeFirstIndex;
    private final SampleDataQueue sampleDataQueue;
    private long sampleOffsetUs;
    private Format unadjustedUpstreamFormat;
    private Format upstreamCommittedFormat;
    private Format upstreamFormat;
    private UpstreamFormatChangedListener upstreamFormatChangeListener;
    private int upstreamSourceId;
    private final SampleExtrasHolder extrasHolder = new SampleExtrasHolder();
    private int capacity = 1000;
    private int[] sourceIds = new int[1000];
    private long[] offsets = new long[1000];
    private long[] timesUs = new long[1000];
    private int[] flags = new int[1000];
    private int[] sizes = new int[1000];
    private TrackOutput.CryptoData[] cryptoDatas = new TrackOutput.CryptoData[1000];
    private Format[] formats = new Format[1000];
    private long largestDiscardedTimestampUs = Long.MIN_VALUE;
    private long largestQueuedTimestampUs = Long.MIN_VALUE;
    private boolean upstreamFormatRequired = true;
    private boolean upstreamKeyframeRequired = true;

    /* loaded from: classes.dex */
    public interface UpstreamFormatChangedListener {
        void onUpstreamFormatChanged(Format format);
    }

    public SampleQueue(Allocator allocator, Looper looper, DrmSessionManager<?> drmSessionManager) {
        this.sampleDataQueue = new SampleDataQueue(allocator);
        this.playbackLooper = looper;
        this.drmSessionManager = drmSessionManager;
    }

    public void release() {
        reset(true);
        releaseDrmSessionReferences();
    }

    public final void reset() {
        reset(false);
    }

    public void reset(boolean z) {
        this.sampleDataQueue.reset();
        this.length = 0;
        this.absoluteFirstIndex = 0;
        this.relativeFirstIndex = 0;
        this.readPosition = 0;
        this.upstreamKeyframeRequired = true;
        this.largestDiscardedTimestampUs = Long.MIN_VALUE;
        this.largestQueuedTimestampUs = Long.MIN_VALUE;
        this.isLastSampleQueued = false;
        this.upstreamCommittedFormat = null;
        if (z) {
            this.unadjustedUpstreamFormat = null;
            this.upstreamFormat = null;
            this.upstreamFormatRequired = true;
        }
    }

    public final void sourceId(int i) {
        this.upstreamSourceId = i;
    }

    public final void splice() {
        this.pendingSplice = true;
    }

    public final int getWriteIndex() {
        return this.absoluteFirstIndex + this.length;
    }

    public final void discardUpstreamSamples(int i) {
        this.sampleDataQueue.discardUpstreamSampleBytes(discardUpstreamSampleMetadata(i));
    }

    public void preRelease() {
        discardToEnd();
        releaseDrmSessionReferences();
    }

    public void maybeThrowError() throws IOException {
        DrmSession<?> drmSession = this.currentDrmSession;
        if (drmSession == null || drmSession.getState() != 1) {
            return;
        }
        throw ((DrmSession.DrmSessionException) Assertions.checkNotNull(this.currentDrmSession.getError()));
    }

    public final int getFirstIndex() {
        return this.absoluteFirstIndex;
    }

    public final int getReadIndex() {
        return this.absoluteFirstIndex + this.readPosition;
    }

    public final synchronized int peekSourceId() {
        return hasNextSample() ? this.sourceIds[getRelativeIndex(this.readPosition)] : this.upstreamSourceId;
    }

    public final synchronized Format getUpstreamFormat() {
        return this.upstreamFormatRequired ? null : this.upstreamFormat;
    }

    public final synchronized long getLargestQueuedTimestampUs() {
        return this.largestQueuedTimestampUs;
    }

    public final synchronized boolean isLastSampleQueued() {
        return this.isLastSampleQueued;
    }

    public final synchronized long getFirstTimestampUs() {
        return this.length == 0 ? Long.MIN_VALUE : this.timesUs[this.relativeFirstIndex];
    }

    public synchronized boolean isReady(boolean z) {
        Format format;
        boolean z2 = true;
        if (!hasNextSample()) {
            if (!z && !this.isLastSampleQueued && ((format = this.upstreamFormat) == null || format == this.downstreamFormat)) {
                z2 = false;
            }
            return z2;
        }
        int relativeIndex = getRelativeIndex(this.readPosition);
        if (this.formats[relativeIndex] == this.downstreamFormat) {
            return mayReadSample(relativeIndex);
        }
        return true;
    }

    public int read(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z, boolean z2, long j) {
        int readSampleMetadata = readSampleMetadata(formatHolder, decoderInputBuffer, z, z2, j, this.extrasHolder);
        if (readSampleMetadata == -4 && !decoderInputBuffer.isEndOfStream() && !decoderInputBuffer.isFlagsOnly()) {
            this.sampleDataQueue.readToBuffer(decoderInputBuffer, this.extrasHolder);
        }
        return readSampleMetadata;
    }

    public final synchronized boolean seekTo(int i) {
        rewind();
        int i2 = this.absoluteFirstIndex;
        if (i >= i2 && i <= this.length + i2) {
            this.readPosition = i - i2;
            return true;
        }
        return false;
    }

    public final synchronized boolean seekTo(long j, boolean z) {
        rewind();
        int relativeIndex = getRelativeIndex(this.readPosition);
        if (hasNextSample() && j >= this.timesUs[relativeIndex] && (j <= this.largestQueuedTimestampUs || z)) {
            int findSampleBefore = findSampleBefore(relativeIndex, this.length - this.readPosition, j, true);
            if (findSampleBefore == -1) {
                return false;
            }
            this.readPosition += findSampleBefore;
            return true;
        }
        return false;
    }

    public final synchronized int advanceTo(long j) {
        int relativeIndex = getRelativeIndex(this.readPosition);
        if (hasNextSample() && j >= this.timesUs[relativeIndex]) {
            int findSampleBefore = findSampleBefore(relativeIndex, this.length - this.readPosition, j, true);
            if (findSampleBefore == -1) {
                return 0;
            }
            this.readPosition += findSampleBefore;
            return findSampleBefore;
        }
        return 0;
    }

    public final synchronized int advanceToEnd() {
        int i;
        int i2 = this.length;
        i = i2 - this.readPosition;
        this.readPosition = i2;
        return i;
    }

    public final void discardTo(long j, boolean z, boolean z2) {
        this.sampleDataQueue.discardDownstreamTo(discardSampleMetadataTo(j, z, z2));
    }

    public final void discardToRead() {
        this.sampleDataQueue.discardDownstreamTo(discardSampleMetadataToRead());
    }

    public final void discardToEnd() {
        this.sampleDataQueue.discardDownstreamTo(discardSampleMetadataToEnd());
    }

    public final void setSampleOffsetUs(long j) {
        if (this.sampleOffsetUs != j) {
            this.sampleOffsetUs = j;
            invalidateUpstreamFormatAdjustment();
        }
    }

    public final void setUpstreamFormatChangeListener(UpstreamFormatChangedListener upstreamFormatChangedListener) {
        this.upstreamFormatChangeListener = upstreamFormatChangedListener;
    }

    @Override // com.google.android.exoplayer2.extractor.TrackOutput
    public final void format(Format format) {
        Format adjustedUpstreamFormat = getAdjustedUpstreamFormat(format);
        this.pendingUpstreamFormatAdjustment = false;
        this.unadjustedUpstreamFormat = format;
        boolean upstreamFormat = setUpstreamFormat(adjustedUpstreamFormat);
        UpstreamFormatChangedListener upstreamFormatChangedListener = this.upstreamFormatChangeListener;
        if (upstreamFormatChangedListener == null || !upstreamFormat) {
            return;
        }
        upstreamFormatChangedListener.onUpstreamFormatChanged(adjustedUpstreamFormat);
    }

    @Override // com.google.android.exoplayer2.extractor.TrackOutput
    public final int sampleData(ExtractorInput extractorInput, int i, boolean z) throws IOException, InterruptedException {
        return this.sampleDataQueue.sampleData(extractorInput, i, z);
    }

    @Override // com.google.android.exoplayer2.extractor.TrackOutput
    public final void sampleData(ParsableByteArray parsableByteArray, int i) {
        this.sampleDataQueue.sampleData(parsableByteArray, i);
    }

    @Override // com.google.android.exoplayer2.extractor.TrackOutput
    public final void sampleMetadata(long j, int i, int i2, int i3, TrackOutput.CryptoData cryptoData) {
        if (this.pendingUpstreamFormatAdjustment) {
            format(this.unadjustedUpstreamFormat);
        }
        long j2 = j + this.sampleOffsetUs;
        if (this.pendingSplice) {
            if ((i & 1) == 0 || !attemptSplice(j2)) {
                return;
            }
            this.pendingSplice = false;
        }
        commitSample(j2, i, (this.sampleDataQueue.getTotalBytesWritten() - i2) - i3, i2, cryptoData);
    }

    public final void invalidateUpstreamFormatAdjustment() {
        this.pendingUpstreamFormatAdjustment = true;
    }

    public Format getAdjustedUpstreamFormat(Format format) {
        long j = this.sampleOffsetUs;
        if (j != 0) {
            long j2 = format.subsampleOffsetUs;
            return j2 != Long.MAX_VALUE ? format.copyWithSubsampleOffsetUs(j2 + j) : format;
        }
        return format;
    }

    private synchronized void rewind() {
        this.readPosition = 0;
        this.sampleDataQueue.rewind();
    }

    private synchronized int readSampleMetadata(FormatHolder formatHolder, DecoderInputBuffer decoderInputBuffer, boolean z, boolean z2, long j, SampleExtrasHolder sampleExtrasHolder) {
        boolean hasNextSample;
        decoderInputBuffer.waitingForKeys = false;
        int i = -1;
        while (true) {
            hasNextSample = hasNextSample();
            if (!hasNextSample) {
                break;
            }
            i = getRelativeIndex(this.readPosition);
            if (this.timesUs[i] >= j || !MimeTypes.allSamplesAreSyncSamples(this.formats[i].sampleMimeType)) {
                break;
            }
            this.readPosition++;
        }
        if (!hasNextSample) {
            if (!z2 && !this.isLastSampleQueued) {
                Format format = this.upstreamFormat;
                if (format == null || (!z && format == this.downstreamFormat)) {
                    return -3;
                }
                onFormatResult((Format) Assertions.checkNotNull(format), formatHolder);
                return -5;
            }
            decoderInputBuffer.setFlags(4);
            return -4;
        }
        if (!z && this.formats[i] == this.downstreamFormat) {
            if (!mayReadSample(i)) {
                decoderInputBuffer.waitingForKeys = true;
                return -3;
            }
            decoderInputBuffer.setFlags(this.flags[i]);
            long j2 = this.timesUs[i];
            decoderInputBuffer.timeUs = j2;
            if (j2 < j) {
                decoderInputBuffer.addFlag(Integer.MIN_VALUE);
            }
            if (decoderInputBuffer.isFlagsOnly()) {
                return -4;
            }
            sampleExtrasHolder.size = this.sizes[i];
            sampleExtrasHolder.offset = this.offsets[i];
            sampleExtrasHolder.cryptoData = this.cryptoDatas[i];
            this.readPosition++;
            return -4;
        }
        onFormatResult(this.formats[i], formatHolder);
        return -5;
    }

    private synchronized boolean setUpstreamFormat(Format format) {
        if (format == null) {
            this.upstreamFormatRequired = true;
            return false;
        }
        this.upstreamFormatRequired = false;
        if (Util.areEqual(format, this.upstreamFormat)) {
            return false;
        }
        if (Util.areEqual(format, this.upstreamCommittedFormat)) {
            this.upstreamFormat = this.upstreamCommittedFormat;
            return true;
        }
        this.upstreamFormat = format;
        return true;
    }

    private synchronized long discardSampleMetadataTo(long j, boolean z, boolean z2) {
        int i;
        int i2 = this.length;
        if (i2 != 0) {
            long[] jArr = this.timesUs;
            int i3 = this.relativeFirstIndex;
            if (j >= jArr[i3]) {
                if (z2 && (i = this.readPosition) != i2) {
                    i2 = i + 1;
                }
                int findSampleBefore = findSampleBefore(i3, i2, j, z);
                if (findSampleBefore != -1) {
                    return discardSamples(findSampleBefore);
                }
                return -1L;
            }
        }
        return -1L;
    }

    public synchronized long discardSampleMetadataToRead() {
        int i = this.readPosition;
        if (i == 0) {
            return -1L;
        }
        return discardSamples(i);
    }

    private synchronized long discardSampleMetadataToEnd() {
        int i = this.length;
        if (i == 0) {
            return -1L;
        }
        return discardSamples(i);
    }

    private void releaseDrmSessionReferences() {
        DrmSession<?> drmSession = this.currentDrmSession;
        if (drmSession != null) {
            drmSession.release();
            this.currentDrmSession = null;
            this.downstreamFormat = null;
        }
    }

    private synchronized void commitSample(long j, int i, long j2, int i2, TrackOutput.CryptoData cryptoData) {
        if (this.upstreamKeyframeRequired) {
            if ((i & 1) == 0) {
                return;
            }
            this.upstreamKeyframeRequired = false;
        }
        Assertions.checkState(!this.upstreamFormatRequired);
        this.isLastSampleQueued = (536870912 & i) != 0;
        this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, j);
        int relativeIndex = getRelativeIndex(this.length);
        this.timesUs[relativeIndex] = j;
        long[] jArr = this.offsets;
        jArr[relativeIndex] = j2;
        this.sizes[relativeIndex] = i2;
        this.flags[relativeIndex] = i;
        this.cryptoDatas[relativeIndex] = cryptoData;
        Format[] formatArr = this.formats;
        Format format = this.upstreamFormat;
        formatArr[relativeIndex] = format;
        this.sourceIds[relativeIndex] = this.upstreamSourceId;
        this.upstreamCommittedFormat = format;
        int i3 = this.length + 1;
        this.length = i3;
        int i4 = this.capacity;
        if (i3 == i4) {
            int i5 = i4 + 1000;
            int[] iArr = new int[i5];
            long[] jArr2 = new long[i5];
            long[] jArr3 = new long[i5];
            int[] iArr2 = new int[i5];
            int[] iArr3 = new int[i5];
            TrackOutput.CryptoData[] cryptoDataArr = new TrackOutput.CryptoData[i5];
            Format[] formatArr2 = new Format[i5];
            int i6 = this.relativeFirstIndex;
            int i7 = i4 - i6;
            System.arraycopy(jArr, i6, jArr2, 0, i7);
            System.arraycopy(this.timesUs, this.relativeFirstIndex, jArr3, 0, i7);
            System.arraycopy(this.flags, this.relativeFirstIndex, iArr2, 0, i7);
            System.arraycopy(this.sizes, this.relativeFirstIndex, iArr3, 0, i7);
            System.arraycopy(this.cryptoDatas, this.relativeFirstIndex, cryptoDataArr, 0, i7);
            System.arraycopy(this.formats, this.relativeFirstIndex, formatArr2, 0, i7);
            System.arraycopy(this.sourceIds, this.relativeFirstIndex, iArr, 0, i7);
            int i8 = this.relativeFirstIndex;
            System.arraycopy(this.offsets, 0, jArr2, i7, i8);
            System.arraycopy(this.timesUs, 0, jArr3, i7, i8);
            System.arraycopy(this.flags, 0, iArr2, i7, i8);
            System.arraycopy(this.sizes, 0, iArr3, i7, i8);
            System.arraycopy(this.cryptoDatas, 0, cryptoDataArr, i7, i8);
            System.arraycopy(this.formats, 0, formatArr2, i7, i8);
            System.arraycopy(this.sourceIds, 0, iArr, i7, i8);
            this.offsets = jArr2;
            this.timesUs = jArr3;
            this.flags = iArr2;
            this.sizes = iArr3;
            this.cryptoDatas = cryptoDataArr;
            this.formats = formatArr2;
            this.sourceIds = iArr;
            this.relativeFirstIndex = 0;
            this.capacity = i5;
        }
    }

    private synchronized boolean attemptSplice(long j) {
        boolean z = false;
        if (this.length == 0) {
            if (j > this.largestDiscardedTimestampUs) {
                z = true;
            }
            return z;
        } else if (Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(this.readPosition)) >= j) {
            return false;
        } else {
            int i = this.length;
            int relativeIndex = getRelativeIndex(i - 1);
            while (i > this.readPosition && this.timesUs[relativeIndex] >= j) {
                i--;
                relativeIndex--;
                if (relativeIndex == -1) {
                    relativeIndex = this.capacity - 1;
                }
            }
            discardUpstreamSampleMetadata(this.absoluteFirstIndex + i);
            return true;
        }
    }

    private long discardUpstreamSampleMetadata(int i) {
        int writeIndex = getWriteIndex() - i;
        boolean z = false;
        Assertions.checkArgument(writeIndex >= 0 && writeIndex <= this.length - this.readPosition);
        int i2 = this.length - writeIndex;
        this.length = i2;
        this.largestQueuedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(i2));
        if (writeIndex == 0 && this.isLastSampleQueued) {
            z = true;
        }
        this.isLastSampleQueued = z;
        int i3 = this.length;
        if (i3 != 0) {
            int relativeIndex = getRelativeIndex(i3 - 1);
            return this.offsets[relativeIndex] + this.sizes[relativeIndex];
        }
        return 0L;
    }

    private boolean hasNextSample() {
        return this.readPosition != this.length;
    }

    private void onFormatResult(Format format, FormatHolder formatHolder) {
        DrmSession<?> drmSession;
        formatHolder.format = format;
        Format format2 = this.downstreamFormat;
        boolean z = format2 == null;
        DrmInitData drmInitData = z ? null : format2.drmInitData;
        this.downstreamFormat = format;
        if (this.drmSessionManager == DrmSessionManager.DUMMY) {
            return;
        }
        DrmInitData drmInitData2 = format.drmInitData;
        formatHolder.includesDrmSession = true;
        formatHolder.drmSession = this.currentDrmSession;
        if (!z && Util.areEqual(drmInitData, drmInitData2)) {
            return;
        }
        DrmSession<?> drmSession2 = this.currentDrmSession;
        if (drmInitData2 != null) {
            drmSession = this.drmSessionManager.acquireSession(this.playbackLooper, drmInitData2);
        } else {
            drmSession = this.drmSessionManager.acquirePlaceholderSession(this.playbackLooper, MimeTypes.getTrackType(format.sampleMimeType));
        }
        this.currentDrmSession = drmSession;
        formatHolder.drmSession = drmSession;
        if (drmSession2 == null) {
            return;
        }
        drmSession2.release();
    }

    private boolean mayReadSample(int i) {
        DrmSession<?> drmSession;
        if (this.drmSessionManager == DrmSessionManager.DUMMY || (drmSession = this.currentDrmSession) == null || drmSession.getState() == 4) {
            return true;
        }
        return (this.flags[i] & 1073741824) == 0 && this.currentDrmSession.playClearSamplesWithoutKeys();
    }

    private int findSampleBefore(int i, int i2, long j, boolean z) {
        int i3 = -1;
        for (int i4 = 0; i4 < i2 && this.timesUs[i] <= j; i4++) {
            if (!z || (this.flags[i] & 1) != 0) {
                i3 = i4;
            }
            i++;
            if (i == this.capacity) {
                i = 0;
            }
        }
        return i3;
    }

    private long discardSamples(int i) {
        int i2;
        this.largestDiscardedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(i));
        int i3 = this.length - i;
        this.length = i3;
        this.absoluteFirstIndex += i;
        int i4 = this.relativeFirstIndex + i;
        this.relativeFirstIndex = i4;
        int i5 = this.capacity;
        if (i4 >= i5) {
            this.relativeFirstIndex = i4 - i5;
        }
        int i6 = this.readPosition - i;
        this.readPosition = i6;
        if (i6 < 0) {
            this.readPosition = 0;
        }
        if (i3 == 0) {
            int i7 = this.relativeFirstIndex;
            if (i7 != 0) {
                i5 = i7;
            }
            return this.offsets[i5 - 1] + this.sizes[i2];
        }
        return this.offsets[this.relativeFirstIndex];
    }

    private long getLargestTimestamp(int i) {
        long j = Long.MIN_VALUE;
        if (i == 0) {
            return Long.MIN_VALUE;
        }
        int relativeIndex = getRelativeIndex(i - 1);
        for (int i2 = 0; i2 < i; i2++) {
            j = Math.max(j, this.timesUs[relativeIndex]);
            if ((this.flags[relativeIndex] & 1) != 0) {
                break;
            }
            relativeIndex--;
            if (relativeIndex == -1) {
                relativeIndex = this.capacity - 1;
            }
        }
        return j;
    }

    private int getRelativeIndex(int i) {
        int i2 = this.relativeFirstIndex + i;
        int i3 = this.capacity;
        return i2 < i3 ? i2 : i2 - i3;
    }

    /* loaded from: classes.dex */
    public static final class SampleExtrasHolder {
        public TrackOutput.CryptoData cryptoData;
        public long offset;
        public int size;

        SampleExtrasHolder() {
        }
    }
}
