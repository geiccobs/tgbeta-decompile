package com.google.android.exoplayer2.source.dash;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.metadata.emsg.EventMessageEncoder;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.dash.manifest.EventStream;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
/* loaded from: classes3.dex */
final class EventSampleStream implements SampleStream {
    private int currentIndex;
    private EventStream eventStream;
    private boolean eventStreamAppendable;
    private long[] eventTimesUs;
    private boolean isFormatSentDownstream;
    private final Format upstreamFormat;
    private final EventMessageEncoder eventMessageEncoder = new EventMessageEncoder();
    private long pendingSeekPositionUs = C.TIME_UNSET;

    public EventSampleStream(EventStream eventStream, Format upstreamFormat, boolean eventStreamAppendable) {
        this.upstreamFormat = upstreamFormat;
        this.eventStream = eventStream;
        this.eventTimesUs = eventStream.presentationTimesUs;
        updateEventStream(eventStream, eventStreamAppendable);
    }

    public String eventStreamId() {
        return this.eventStream.id();
    }

    public void updateEventStream(EventStream eventStream, boolean eventStreamAppendable) {
        int i = this.currentIndex;
        long lastReadPositionUs = i == 0 ? -9223372036854775807L : this.eventTimesUs[i - 1];
        this.eventStreamAppendable = eventStreamAppendable;
        this.eventStream = eventStream;
        long[] jArr = eventStream.presentationTimesUs;
        this.eventTimesUs = jArr;
        long j = this.pendingSeekPositionUs;
        if (j != C.TIME_UNSET) {
            seekToUs(j);
        } else if (lastReadPositionUs != C.TIME_UNSET) {
            this.currentIndex = Util.binarySearchCeil(jArr, lastReadPositionUs, false, false);
        }
    }

    public void seekToUs(long positionUs) {
        boolean z = true;
        int binarySearchCeil = Util.binarySearchCeil(this.eventTimesUs, positionUs, true, false);
        this.currentIndex = binarySearchCeil;
        if (!this.eventStreamAppendable || binarySearchCeil != this.eventTimesUs.length) {
            z = false;
        }
        boolean isPendingSeek = z;
        this.pendingSeekPositionUs = isPendingSeek ? positionUs : C.TIME_UNSET;
    }

    @Override // com.google.android.exoplayer2.source.SampleStream
    public boolean isReady() {
        return true;
    }

    @Override // com.google.android.exoplayer2.source.SampleStream
    public void maybeThrowError() throws IOException {
    }

    @Override // com.google.android.exoplayer2.source.SampleStream
    public int readData(FormatHolder formatHolder, DecoderInputBuffer buffer, boolean formatRequired) {
        if (formatRequired || !this.isFormatSentDownstream) {
            formatHolder.format = this.upstreamFormat;
            this.isFormatSentDownstream = true;
            return -5;
        }
        int sampleIndex = this.currentIndex;
        if (sampleIndex == this.eventTimesUs.length) {
            if (this.eventStreamAppendable) {
                return -3;
            }
            buffer.setFlags(4);
            return -4;
        }
        this.currentIndex = sampleIndex + 1;
        byte[] serializedEvent = this.eventMessageEncoder.encode(this.eventStream.events[sampleIndex]);
        if (serializedEvent == null) {
            return -3;
        }
        buffer.ensureSpaceForWrite(serializedEvent.length);
        buffer.data.put(serializedEvent);
        buffer.timeUs = this.eventTimesUs[sampleIndex];
        buffer.setFlags(1);
        return -4;
    }

    @Override // com.google.android.exoplayer2.source.SampleStream
    public int skipData(long positionUs) {
        int newIndex = Math.max(this.currentIndex, Util.binarySearchCeil(this.eventTimesUs, positionUs, true, false));
        int skipped = newIndex - this.currentIndex;
        this.currentIndex = newIndex;
        return skipped;
    }
}
