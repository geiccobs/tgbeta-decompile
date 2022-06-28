package com.google.android.exoplayer2.source;

import android.os.Looper;
import com.google.android.exoplayer2.C;
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
/* loaded from: classes3.dex */
public class SampleQueue implements TrackOutput {
    static final int SAMPLE_CAPACITY_INCREMENT = 1000;
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

    /* loaded from: classes3.dex */
    public interface UpstreamFormatChangedListener {
        void onUpstreamFormatChanged(Format format);
    }

    public SampleQueue(Allocator allocator, Looper playbackLooper, DrmSessionManager<?> drmSessionManager) {
        this.sampleDataQueue = new SampleDataQueue(allocator);
        this.playbackLooper = playbackLooper;
        this.drmSessionManager = drmSessionManager;
    }

    public void release() {
        reset(true);
        releaseDrmSessionReferences();
    }

    public final void reset() {
        reset(false);
    }

    public void reset(boolean resetUpstreamFormat) {
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
        if (resetUpstreamFormat) {
            this.unadjustedUpstreamFormat = null;
            this.upstreamFormat = null;
            this.upstreamFormatRequired = true;
        }
    }

    public final void sourceId(int sourceId) {
        this.upstreamSourceId = sourceId;
    }

    public final void splice() {
        this.pendingSplice = true;
    }

    public final int getWriteIndex() {
        return this.absoluteFirstIndex + this.length;
    }

    public final void discardUpstreamSamples(int discardFromIndex) {
        this.sampleDataQueue.discardUpstreamSampleBytes(discardUpstreamSampleMetadata(discardFromIndex));
    }

    public void preRelease() {
        discardToEnd();
        releaseDrmSessionReferences();
    }

    public void maybeThrowError() throws IOException {
        DrmSession<?> drmSession = this.currentDrmSession;
        if (drmSession != null && drmSession.getState() == 1) {
            throw ((DrmSession.DrmSessionException) Assertions.checkNotNull(this.currentDrmSession.getError()));
        }
    }

    public final int getFirstIndex() {
        return this.absoluteFirstIndex;
    }

    public final int getReadIndex() {
        return this.absoluteFirstIndex + this.readPosition;
    }

    public final synchronized int peekSourceId() {
        int relativeReadIndex;
        relativeReadIndex = getRelativeIndex(this.readPosition);
        return hasNextSample() ? this.sourceIds[relativeReadIndex] : this.upstreamSourceId;
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

    public synchronized boolean isReady(boolean loadingFinished) {
        Format format;
        boolean z = true;
        if (!hasNextSample()) {
            if (!loadingFinished && !this.isLastSampleQueued && ((format = this.upstreamFormat) == null || format == this.downstreamFormat)) {
                z = false;
            }
            return z;
        }
        int relativeReadIndex = getRelativeIndex(this.readPosition);
        if (this.formats[relativeReadIndex] == this.downstreamFormat) {
            return mayReadSample(relativeReadIndex);
        }
        return true;
    }

    public int read(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired, boolean loadingFinished, long decodeOnlyUntilUs) {
        int result = readSampleMetadata(formatHolder, buffer, formatRequired, loadingFinished, decodeOnlyUntilUs, this.extrasHolder);
        if (result == -4 && !buffer.isEndOfStream() && !buffer.isFlagsOnly()) {
            this.sampleDataQueue.readToBuffer(buffer, this.extrasHolder);
        }
        return result;
    }

    public final synchronized boolean seekTo(int sampleIndex) {
        rewind();
        int i = this.absoluteFirstIndex;
        if (sampleIndex >= i && sampleIndex <= this.length + i) {
            this.readPosition = sampleIndex - i;
            return true;
        }
        return false;
    }

    public final synchronized boolean seekTo(long timeUs, boolean allowTimeBeyondBuffer) {
        rewind();
        int relativeReadIndex = getRelativeIndex(this.readPosition);
        if (hasNextSample() && timeUs >= this.timesUs[relativeReadIndex] && (timeUs <= this.largestQueuedTimestampUs || allowTimeBeyondBuffer)) {
            int offset = findSampleBefore(relativeReadIndex, this.length - this.readPosition, timeUs, true);
            if (offset == -1) {
                return false;
            }
            this.readPosition += offset;
            return true;
        }
        return false;
    }

    public final synchronized int advanceTo(long timeUs) {
        int relativeReadIndex = getRelativeIndex(this.readPosition);
        if (hasNextSample() && timeUs >= this.timesUs[relativeReadIndex]) {
            int offset = findSampleBefore(relativeReadIndex, this.length - this.readPosition, timeUs, true);
            if (offset == -1) {
                return 0;
            }
            this.readPosition += offset;
            return offset;
        }
        return 0;
    }

    public final synchronized int advanceToEnd() {
        int skipCount;
        int i = this.length;
        skipCount = i - this.readPosition;
        this.readPosition = i;
        return skipCount;
    }

    public final void discardTo(long timeUs, boolean toKeyframe, boolean stopAtReadPosition) {
        this.sampleDataQueue.discardDownstreamTo(discardSampleMetadataTo(timeUs, toKeyframe, stopAtReadPosition));
    }

    public final void discardToRead() {
        this.sampleDataQueue.discardDownstreamTo(discardSampleMetadataToRead());
    }

    public final void discardToEnd() {
        this.sampleDataQueue.discardDownstreamTo(discardSampleMetadataToEnd());
    }

    public final void setSampleOffsetUs(long sampleOffsetUs) {
        if (this.sampleOffsetUs != sampleOffsetUs) {
            this.sampleOffsetUs = sampleOffsetUs;
            invalidateUpstreamFormatAdjustment();
        }
    }

    public final void setUpstreamFormatChangeListener(UpstreamFormatChangedListener listener) {
        this.upstreamFormatChangeListener = listener;
    }

    @Override // com.google.android.exoplayer2.extractor.TrackOutput
    public final void format(Format unadjustedUpstreamFormat) {
        Format adjustedUpstreamFormat = getAdjustedUpstreamFormat(unadjustedUpstreamFormat);
        this.pendingUpstreamFormatAdjustment = false;
        this.unadjustedUpstreamFormat = unadjustedUpstreamFormat;
        boolean upstreamFormatChanged = setUpstreamFormat(adjustedUpstreamFormat);
        UpstreamFormatChangedListener upstreamFormatChangedListener = this.upstreamFormatChangeListener;
        if (upstreamFormatChangedListener != null && upstreamFormatChanged) {
            upstreamFormatChangedListener.onUpstreamFormatChanged(adjustedUpstreamFormat);
        }
    }

    @Override // com.google.android.exoplayer2.extractor.TrackOutput
    public final int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        return this.sampleDataQueue.sampleData(input, length, allowEndOfInput);
    }

    @Override // com.google.android.exoplayer2.extractor.TrackOutput
    public final void sampleData(ParsableByteArray buffer, int length) {
        this.sampleDataQueue.sampleData(buffer, length);
    }

    @Override // com.google.android.exoplayer2.extractor.TrackOutput
    public final void sampleMetadata(long timeUs, int flags, int size, int offset, TrackOutput.CryptoData cryptoData) {
        if (this.pendingUpstreamFormatAdjustment) {
            format(this.unadjustedUpstreamFormat);
        }
        long timeUs2 = timeUs + this.sampleOffsetUs;
        if (this.pendingSplice) {
            if ((flags & 1) != 0 && attemptSplice(timeUs2)) {
                this.pendingSplice = false;
            } else {
                return;
            }
        }
        long absoluteOffset = (this.sampleDataQueue.getTotalBytesWritten() - size) - offset;
        commitSample(timeUs2, flags, absoluteOffset, size, cryptoData);
    }

    public final void invalidateUpstreamFormatAdjustment() {
        this.pendingUpstreamFormatAdjustment = true;
    }

    public Format getAdjustedUpstreamFormat(Format format) {
        if (this.sampleOffsetUs != 0 && format.subsampleOffsetUs != Long.MAX_VALUE) {
            return format.copyWithSubsampleOffsetUs(format.subsampleOffsetUs + this.sampleOffsetUs);
        }
        return format;
    }

    private synchronized void rewind() {
        this.readPosition = 0;
        this.sampleDataQueue.rewind();
    }

    private synchronized int readSampleMetadata(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired, boolean loadingFinished, long decodeOnlyUntilUs, SampleExtrasHolder extrasHolder) {
        boolean hasNextSample;
        buffer.waitingForKeys = false;
        int relativeReadIndex = -1;
        while (true) {
            hasNextSample = hasNextSample();
            if (!hasNextSample) {
                break;
            }
            relativeReadIndex = getRelativeIndex(this.readPosition);
            long timeUs = this.timesUs[relativeReadIndex];
            if (timeUs >= decodeOnlyUntilUs || !MimeTypes.allSamplesAreSyncSamples(this.formats[relativeReadIndex].sampleMimeType)) {
                break;
            }
            this.readPosition++;
        }
        if (!hasNextSample) {
            if (!loadingFinished && !this.isLastSampleQueued) {
                Format format = this.upstreamFormat;
                if (format == null || (!formatRequired && format == this.downstreamFormat)) {
                    return -3;
                }
                onFormatResult((Format) Assertions.checkNotNull(format), formatHolder);
                return -5;
            }
            buffer.setFlags(4);
            return -4;
        }
        if (!formatRequired && this.formats[relativeReadIndex] == this.downstreamFormat) {
            if (!mayReadSample(relativeReadIndex)) {
                buffer.waitingForKeys = true;
                return -3;
            }
            buffer.setFlags(this.flags[relativeReadIndex]);
            buffer.timeUs = this.timesUs[relativeReadIndex];
            if (buffer.timeUs < decodeOnlyUntilUs) {
                buffer.addFlag(Integer.MIN_VALUE);
            }
            if (buffer.isFlagsOnly()) {
                return -4;
            }
            extrasHolder.size = this.sizes[relativeReadIndex];
            extrasHolder.offset = this.offsets[relativeReadIndex];
            extrasHolder.cryptoData = this.cryptoDatas[relativeReadIndex];
            this.readPosition++;
            return -4;
        }
        onFormatResult(this.formats[relativeReadIndex], formatHolder);
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

    private synchronized long discardSampleMetadataTo(long timeUs, boolean toKeyframe, boolean stopAtReadPosition) {
        int i;
        int i2 = this.length;
        if (i2 != 0) {
            long[] jArr = this.timesUs;
            int i3 = this.relativeFirstIndex;
            if (timeUs >= jArr[i3]) {
                int searchLength = (!stopAtReadPosition || (i = this.readPosition) == i2) ? i2 : i + 1;
                int discardCount = findSampleBefore(i3, searchLength, timeUs, toKeyframe);
                if (discardCount != -1) {
                    return discardSamples(discardCount);
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

    private synchronized void commitSample(long timeUs, int sampleFlags, long offset, int size, TrackOutput.CryptoData cryptoData) {
        if (this.upstreamKeyframeRequired) {
            if ((sampleFlags & 1) == 0) {
                return;
            }
            this.upstreamKeyframeRequired = false;
        }
        Assertions.checkState(!this.upstreamFormatRequired);
        this.isLastSampleQueued = (sampleFlags & 536870912) != 0;
        this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, timeUs);
        int relativeEndIndex = getRelativeIndex(this.length);
        this.timesUs[relativeEndIndex] = timeUs;
        long[] jArr = this.offsets;
        jArr[relativeEndIndex] = offset;
        this.sizes[relativeEndIndex] = size;
        this.flags[relativeEndIndex] = sampleFlags;
        this.cryptoDatas[relativeEndIndex] = cryptoData;
        Format[] formatArr = this.formats;
        Format format = this.upstreamFormat;
        formatArr[relativeEndIndex] = format;
        this.sourceIds[relativeEndIndex] = this.upstreamSourceId;
        this.upstreamCommittedFormat = format;
        int i = this.length + 1;
        this.length = i;
        int i2 = this.capacity;
        if (i == i2) {
            int newCapacity = i2 + 1000;
            int[] newSourceIds = new int[newCapacity];
            long[] newOffsets = new long[newCapacity];
            long[] newTimesUs = new long[newCapacity];
            int[] newFlags = new int[newCapacity];
            int[] newSizes = new int[newCapacity];
            TrackOutput.CryptoData[] newCryptoDatas = new TrackOutput.CryptoData[newCapacity];
            Format[] newFormats = new Format[newCapacity];
            int i3 = this.relativeFirstIndex;
            int beforeWrap = i2 - i3;
            System.arraycopy(jArr, i3, newOffsets, 0, beforeWrap);
            System.arraycopy(this.timesUs, this.relativeFirstIndex, newTimesUs, 0, beforeWrap);
            System.arraycopy(this.flags, this.relativeFirstIndex, newFlags, 0, beforeWrap);
            System.arraycopy(this.sizes, this.relativeFirstIndex, newSizes, 0, beforeWrap);
            System.arraycopy(this.cryptoDatas, this.relativeFirstIndex, newCryptoDatas, 0, beforeWrap);
            System.arraycopy(this.formats, this.relativeFirstIndex, newFormats, 0, beforeWrap);
            System.arraycopy(this.sourceIds, this.relativeFirstIndex, newSourceIds, 0, beforeWrap);
            int afterWrap = this.relativeFirstIndex;
            System.arraycopy(this.offsets, 0, newOffsets, beforeWrap, afterWrap);
            System.arraycopy(this.timesUs, 0, newTimesUs, beforeWrap, afterWrap);
            System.arraycopy(this.flags, 0, newFlags, beforeWrap, afterWrap);
            System.arraycopy(this.sizes, 0, newSizes, beforeWrap, afterWrap);
            System.arraycopy(this.cryptoDatas, 0, newCryptoDatas, beforeWrap, afterWrap);
            System.arraycopy(this.formats, 0, newFormats, beforeWrap, afterWrap);
            System.arraycopy(this.sourceIds, 0, newSourceIds, beforeWrap, afterWrap);
            this.offsets = newOffsets;
            this.timesUs = newTimesUs;
            this.flags = newFlags;
            this.sizes = newSizes;
            this.cryptoDatas = newCryptoDatas;
            this.formats = newFormats;
            this.sourceIds = newSourceIds;
            this.relativeFirstIndex = 0;
            this.capacity = newCapacity;
        }
    }

    private synchronized boolean attemptSplice(long timeUs) {
        boolean z = false;
        if (this.length == 0) {
            if (timeUs > this.largestDiscardedTimestampUs) {
                z = true;
            }
            return z;
        }
        long largestReadTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(this.readPosition));
        if (largestReadTimestampUs >= timeUs) {
            return false;
        }
        int i = this.length;
        int retainCount = i;
        int relativeSampleIndex = getRelativeIndex(i - 1);
        while (retainCount > this.readPosition && this.timesUs[relativeSampleIndex] >= timeUs) {
            retainCount--;
            relativeSampleIndex--;
            if (relativeSampleIndex == -1) {
                relativeSampleIndex = this.capacity - 1;
            }
        }
        discardUpstreamSampleMetadata(this.absoluteFirstIndex + retainCount);
        return true;
    }

    private long discardUpstreamSampleMetadata(int discardFromIndex) {
        int discardCount = getWriteIndex() - discardFromIndex;
        boolean z = false;
        Assertions.checkArgument(discardCount >= 0 && discardCount <= this.length - this.readPosition);
        int i = this.length - discardCount;
        this.length = i;
        this.largestQueuedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(i));
        if (discardCount == 0 && this.isLastSampleQueued) {
            z = true;
        }
        this.isLastSampleQueued = z;
        int i2 = this.length;
        if (i2 != 0) {
            int relativeLastWriteIndex = getRelativeIndex(i2 - 1);
            return this.offsets[relativeLastWriteIndex] + this.sizes[relativeLastWriteIndex];
        }
        return 0L;
    }

    private boolean hasNextSample() {
        return this.readPosition != this.length;
    }

    private void onFormatResult(Format newFormat, FormatHolder outputFormatHolder) {
        DrmSession<?> drmSession;
        outputFormatHolder.format = newFormat;
        Format format = this.downstreamFormat;
        boolean isFirstFormat = format == null;
        DrmInitData oldDrmInitData = isFirstFormat ? null : format.drmInitData;
        this.downstreamFormat = newFormat;
        if (this.drmSessionManager == DrmSessionManager.DUMMY) {
            return;
        }
        DrmInitData newDrmInitData = newFormat.drmInitData;
        outputFormatHolder.includesDrmSession = true;
        outputFormatHolder.drmSession = this.currentDrmSession;
        if (!isFirstFormat && Util.areEqual(oldDrmInitData, newDrmInitData)) {
            return;
        }
        DrmSession previousSession = this.currentDrmSession;
        if (newDrmInitData != null) {
            drmSession = this.drmSessionManager.acquireSession(this.playbackLooper, newDrmInitData);
        } else {
            drmSession = this.drmSessionManager.acquirePlaceholderSession(this.playbackLooper, MimeTypes.getTrackType(newFormat.sampleMimeType));
        }
        this.currentDrmSession = drmSession;
        outputFormatHolder.drmSession = drmSession;
        if (previousSession != null) {
            previousSession.release();
        }
    }

    private boolean mayReadSample(int relativeReadIndex) {
        DrmSession<?> drmSession;
        if (this.drmSessionManager == DrmSessionManager.DUMMY || (drmSession = this.currentDrmSession) == null || drmSession.getState() == 4) {
            return true;
        }
        return (this.flags[relativeReadIndex] & C.BUFFER_FLAG_ENCRYPTED) == 0 && this.currentDrmSession.playClearSamplesWithoutKeys();
    }

    private int findSampleBefore(int relativeStartIndex, int length, long timeUs, boolean keyframe) {
        int sampleCountToTarget = -1;
        int searchIndex = relativeStartIndex;
        for (int i = 0; i < length && this.timesUs[searchIndex] <= timeUs; i++) {
            if (!keyframe || (this.flags[searchIndex] & 1) != 0) {
                sampleCountToTarget = i;
            }
            searchIndex++;
            if (searchIndex == this.capacity) {
                searchIndex = 0;
            }
        }
        return sampleCountToTarget;
    }

    private long discardSamples(int discardCount) {
        this.largestDiscardedTimestampUs = Math.max(this.largestDiscardedTimestampUs, getLargestTimestamp(discardCount));
        int i = this.length - discardCount;
        this.length = i;
        this.absoluteFirstIndex += discardCount;
        int i2 = this.relativeFirstIndex + discardCount;
        this.relativeFirstIndex = i2;
        int i3 = this.capacity;
        if (i2 >= i3) {
            this.relativeFirstIndex = i2 - i3;
        }
        int i4 = this.readPosition - discardCount;
        this.readPosition = i4;
        if (i4 < 0) {
            this.readPosition = 0;
        }
        if (i == 0) {
            int i5 = this.relativeFirstIndex;
            if (i5 != 0) {
                i3 = i5;
            }
            int relativeLastDiscardIndex = i3 - 1;
            return this.offsets[relativeLastDiscardIndex] + this.sizes[relativeLastDiscardIndex];
        }
        return this.offsets[this.relativeFirstIndex];
    }

    private long getLargestTimestamp(int length) {
        if (length == 0) {
            return Long.MIN_VALUE;
        }
        long largestTimestampUs = Long.MIN_VALUE;
        int relativeSampleIndex = getRelativeIndex(length - 1);
        for (int i = 0; i < length; i++) {
            largestTimestampUs = Math.max(largestTimestampUs, this.timesUs[relativeSampleIndex]);
            if ((this.flags[relativeSampleIndex] & 1) != 0) {
                break;
            }
            relativeSampleIndex--;
            if (relativeSampleIndex == -1) {
                relativeSampleIndex = this.capacity - 1;
            }
        }
        return largestTimestampUs;
    }

    private int getRelativeIndex(int offset) {
        int relativeIndex = this.relativeFirstIndex + offset;
        int i = this.capacity;
        return relativeIndex < i ? relativeIndex : relativeIndex - i;
    }

    /* loaded from: classes3.dex */
    public static final class SampleExtrasHolder {
        public TrackOutput.CryptoData cryptoData;
        public long offset;
        public int size;

        SampleExtrasHolder() {
        }
    }
}
