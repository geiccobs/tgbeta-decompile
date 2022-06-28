package com.googlecode.mp4parser.authoring.samples;

import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.SampleSizeBox;
import com.coremedia.iso.boxes.SampleToChunkBox;
import com.coremedia.iso.boxes.TrackBox;
import com.googlecode.mp4parser.authoring.Sample;
import com.googlecode.mp4parser.util.CastUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class DefaultMp4SampleList extends AbstractList<Sample> {
    private static final long MAX_MAP_SIZE = 268435456;
    ByteBuffer[][] cache;
    int[] chunkNumsStartSampleNum;
    long[] chunkOffsets;
    long[] chunkSizes;
    int lastChunk = 0;
    long[][] sampleOffsetsWithinChunks;
    SampleSizeBox ssb;
    Container topLevel;
    TrackBox trackBox;

    public DefaultMp4SampleList(long track, Container topLevel) {
        int currentChunkNo;
        long j = track;
        this.trackBox = null;
        this.cache = null;
        this.topLevel = topLevel;
        MovieBox movieBox = (MovieBox) topLevel.getBoxes(MovieBox.class).get(0);
        List<TrackBox> trackBoxes = movieBox.getBoxes(TrackBox.class);
        for (TrackBox tb : trackBoxes) {
            MovieBox movieBox2 = movieBox;
            List<TrackBox> trackBoxes2 = trackBoxes;
            long j2 = j;
            if (tb.getTrackHeaderBox().getTrackId() != j2) {
                j = j2;
                movieBox = movieBox2;
                trackBoxes = trackBoxes2;
            } else {
                this.trackBox = tb;
                j = j2;
                movieBox = movieBox2;
                trackBoxes = trackBoxes2;
            }
        }
        TrackBox trackBox = this.trackBox;
        if (trackBox == null) {
            throw new RuntimeException("This MP4 does not contain track " + track);
        }
        long[] chunkOffsets = trackBox.getSampleTableBox().getChunkOffsetBox().getChunkOffsets();
        this.chunkOffsets = chunkOffsets;
        this.chunkSizes = new long[chunkOffsets.length];
        this.cache = new ByteBuffer[chunkOffsets.length];
        this.sampleOffsetsWithinChunks = new long[chunkOffsets.length];
        this.ssb = this.trackBox.getSampleTableBox().getSampleSizeBox();
        List<SampleToChunkBox.Entry> s2chunkEntries = this.trackBox.getSampleTableBox().getSampleToChunkBox().getEntries();
        SampleToChunkBox.Entry[] entries = (SampleToChunkBox.Entry[]) s2chunkEntries.toArray(new SampleToChunkBox.Entry[s2chunkEntries.size()]);
        int nextSamplePerChunk = 0 + 1;
        SampleToChunkBox.Entry next = entries[0];
        int currentChunkNo2 = 0;
        int currentSamplePerChunk = 0;
        long nextFirstChunk = next.getFirstChunk();
        int nextSamplePerChunk2 = CastUtils.l2i(next.getSamplesPerChunk());
        int currentSampleNo = 1;
        int lastSampleNo = size();
        while (true) {
            currentChunkNo2++;
            MovieBox movieBox3 = movieBox;
            List<TrackBox> trackBoxes3 = trackBoxes;
            if (currentChunkNo2 == nextFirstChunk) {
                int currentSamplePerChunk2 = nextSamplePerChunk2;
                if (entries.length > nextSamplePerChunk) {
                    int s2cIndex = nextSamplePerChunk + 1;
                    SampleToChunkBox.Entry next2 = entries[nextSamplePerChunk];
                    int nextSamplePerChunk3 = CastUtils.l2i(next2.getSamplesPerChunk());
                    long nextFirstChunk2 = next2.getFirstChunk();
                    nextSamplePerChunk2 = nextSamplePerChunk3;
                    nextFirstChunk = nextFirstChunk2;
                    currentSamplePerChunk = currentSamplePerChunk2;
                    nextSamplePerChunk = s2cIndex;
                } else {
                    nextSamplePerChunk2 = -1;
                    nextFirstChunk = Long.MAX_VALUE;
                    currentSamplePerChunk = currentSamplePerChunk2;
                }
            }
            List<SampleToChunkBox.Entry> s2chunkEntries2 = s2chunkEntries;
            this.sampleOffsetsWithinChunks[currentChunkNo2 - 1] = new long[currentSamplePerChunk];
            int i = currentSampleNo + currentSamplePerChunk;
            currentSampleNo = i;
            if (i > lastSampleNo) {
                break;
            }
            movieBox = movieBox3;
            trackBoxes = trackBoxes3;
            s2chunkEntries = s2chunkEntries2;
        }
        this.chunkNumsStartSampleNum = new int[currentChunkNo2 + 1];
        int nextSamplePerChunk4 = 0 + 1;
        SampleToChunkBox.Entry next3 = entries[0];
        int currentChunkNo3 = 0;
        int currentSamplePerChunk3 = 0;
        long nextFirstChunk3 = next3.getFirstChunk();
        int nextSamplePerChunk5 = CastUtils.l2i(next3.getSamplesPerChunk());
        int currentSampleNo2 = 1;
        while (true) {
            currentChunkNo = currentChunkNo3 + 1;
            this.chunkNumsStartSampleNum[currentChunkNo3] = currentSampleNo2;
            if (currentChunkNo == nextFirstChunk3) {
                int currentSamplePerChunk4 = nextSamplePerChunk5;
                if (entries.length > nextSamplePerChunk4) {
                    int s2cIndex2 = nextSamplePerChunk4 + 1;
                    next3 = entries[nextSamplePerChunk4];
                    int nextSamplePerChunk6 = CastUtils.l2i(next3.getSamplesPerChunk());
                    long nextFirstChunk4 = next3.getFirstChunk();
                    nextSamplePerChunk5 = nextSamplePerChunk6;
                    nextFirstChunk3 = nextFirstChunk4;
                    currentSamplePerChunk3 = currentSamplePerChunk4;
                    nextSamplePerChunk4 = s2cIndex2;
                } else {
                    nextSamplePerChunk5 = -1;
                    nextFirstChunk3 = Long.MAX_VALUE;
                    currentSamplePerChunk3 = currentSamplePerChunk4;
                }
            }
            int currentSamplePerChunk5 = currentSampleNo2 + currentSamplePerChunk3;
            currentSampleNo2 = currentSamplePerChunk5;
            if (currentSamplePerChunk5 > lastSampleNo) {
                break;
            }
            currentChunkNo3 = currentChunkNo;
        }
        this.chunkNumsStartSampleNum[currentChunkNo] = Integer.MAX_VALUE;
        int currentChunkNo4 = 0;
        long sampleSum = 0;
        int i2 = 1;
        while (true) {
            int lastSampleNo2 = lastSampleNo;
            SampleToChunkBox.Entry next4 = next3;
            int currentChunkNo5 = currentChunkNo4;
            if (i2 <= this.ssb.getSampleCount()) {
                currentChunkNo4 = currentChunkNo5;
                while (i2 == this.chunkNumsStartSampleNum[currentChunkNo4]) {
                    currentChunkNo4++;
                    sampleSum = 0;
                }
                long[] jArr = this.chunkSizes;
                int i3 = currentChunkNo4 - 1;
                int s2cIndex3 = nextSamplePerChunk4;
                jArr[i3] = jArr[i3] + this.ssb.getSampleSizeAtIndex(i2 - 1);
                long[] sampleOffsetsWithinChunkscurrentChunkNo = this.sampleOffsetsWithinChunks[currentChunkNo4 - 1];
                int chunkNumsStartSampleNumcurrentChunkNo = this.chunkNumsStartSampleNum[currentChunkNo4 - 1];
                sampleOffsetsWithinChunkscurrentChunkNo[i2 - chunkNumsStartSampleNumcurrentChunkNo] = sampleSum;
                sampleSum += this.ssb.getSampleSizeAtIndex(i2 - 1);
                i2++;
                next3 = next4;
                nextSamplePerChunk4 = s2cIndex3;
                lastSampleNo = lastSampleNo2;
            } else {
                return;
            }
        }
    }

    synchronized int getChunkForSample(int index) {
        int sampleNum = index + 1;
        int[] iArr = this.chunkNumsStartSampleNum;
        int i = this.lastChunk;
        if (sampleNum < iArr[i] || sampleNum >= iArr[i + 1]) {
            if (sampleNum < iArr[i]) {
                this.lastChunk = 0;
                while (true) {
                    int[] iArr2 = this.chunkNumsStartSampleNum;
                    int i2 = this.lastChunk;
                    if (iArr2[i2 + 1] > sampleNum) {
                        return i2;
                    }
                    this.lastChunk = i2 + 1;
                }
            } else {
                this.lastChunk = i + 1;
                while (true) {
                    int[] iArr3 = this.chunkNumsStartSampleNum;
                    int i3 = this.lastChunk;
                    if (iArr3[i3 + 1] > sampleNum) {
                        return i3;
                    }
                    this.lastChunk = i3 + 1;
                }
            }
        } else {
            return i;
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public Sample get(int index) {
        long offsetWithInChunk;
        ByteBuffer[] chunkBuffers;
        ByteBuffer correctPartOfChunk;
        IOException e;
        ByteBuffer[] chunkBuffers2;
        ByteBuffer[] chunkBuffers3;
        long offsetWithInChunk2;
        long chunkOffset;
        if (index >= this.ssb.getSampleCount()) {
            throw new IndexOutOfBoundsException();
        }
        int chunkNumber = getChunkForSample(index);
        int chunkStartSample = this.chunkNumsStartSampleNum[chunkNumber] - 1;
        long chunkOffset2 = this.chunkOffsets[CastUtils.l2i(chunkNumber)];
        int sampleInChunk = index - chunkStartSample;
        long[] sampleOffsetsWithinChunk = this.sampleOffsetsWithinChunks[CastUtils.l2i(chunkNumber)];
        long offsetWithInChunk3 = sampleOffsetsWithinChunk[sampleInChunk];
        ByteBuffer[] chunkBuffers4 = this.cache[CastUtils.l2i(chunkNumber)];
        if (chunkBuffers4 != null) {
            offsetWithInChunk = offsetWithInChunk3;
            chunkBuffers = chunkBuffers4;
        } else {
            List<ByteBuffer> _chunkBuffers = new ArrayList<>();
            long currentStart = 0;
            int i = 0;
            while (i < sampleOffsetsWithinChunk.length) {
                try {
                    offsetWithInChunk2 = offsetWithInChunk3;
                    chunkBuffers2 = chunkBuffers4;
                    chunkOffset = chunkOffset2;
                } catch (IOException e2) {
                    e = e2;
                }
                try {
                    if ((sampleOffsetsWithinChunk[i] + this.ssb.getSampleSizeAtIndex(i + chunkStartSample)) - currentStart > MAX_MAP_SIZE) {
                        _chunkBuffers.add(this.topLevel.getByteBuffer(chunkOffset + currentStart, sampleOffsetsWithinChunk[i] - currentStart));
                        currentStart = sampleOffsetsWithinChunk[i];
                    }
                    i++;
                    offsetWithInChunk3 = offsetWithInChunk2;
                    chunkOffset2 = chunkOffset;
                    chunkBuffers4 = chunkBuffers2;
                } catch (IOException e3) {
                    e = e3;
                    throw new IndexOutOfBoundsException(e.getMessage());
                }
            }
            offsetWithInChunk = offsetWithInChunk3;
            long offsetWithInChunk4 = chunkOffset2 + currentStart;
            long chunkOffset3 = -currentStart;
            try {
                chunkBuffers2 = chunkBuffers4;
                _chunkBuffers.add(this.topLevel.getByteBuffer(offsetWithInChunk4, chunkOffset3 + sampleOffsetsWithinChunk[sampleOffsetsWithinChunk.length - 1] + this.ssb.getSampleSizeAtIndex((sampleOffsetsWithinChunk.length + chunkStartSample) - 1)));
                chunkBuffers3 = (ByteBuffer[]) _chunkBuffers.toArray(new ByteBuffer[_chunkBuffers.size()]);
            } catch (IOException e4) {
                e = e4;
            }
            try {
                this.cache[CastUtils.l2i(chunkNumber)] = chunkBuffers3;
                chunkBuffers = chunkBuffers3;
            } catch (IOException e5) {
                e = e5;
                throw new IndexOutOfBoundsException(e.getMessage());
            }
        }
        int length = chunkBuffers.length;
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                correctPartOfChunk = null;
                break;
            }
            ByteBuffer chunkBuffer = chunkBuffers[i2];
            if (offsetWithInChunk < chunkBuffer.limit()) {
                correctPartOfChunk = chunkBuffer;
                break;
            }
            offsetWithInChunk -= chunkBuffer.limit();
            i2++;
        }
        final long sampleSize = this.ssb.getSampleSizeAtIndex(index);
        final ByteBuffer finalCorrectPartOfChunk = correctPartOfChunk;
        final long finalOffsetWithInChunk = offsetWithInChunk;
        return new Sample() { // from class: com.googlecode.mp4parser.authoring.samples.DefaultMp4SampleList.1
            @Override // com.googlecode.mp4parser.authoring.Sample
            public void writeTo(WritableByteChannel channel) throws IOException {
                channel.write(asByteBuffer());
            }

            @Override // com.googlecode.mp4parser.authoring.Sample
            public long getSize() {
                return sampleSize;
            }

            @Override // com.googlecode.mp4parser.authoring.Sample
            public ByteBuffer asByteBuffer() {
                return (ByteBuffer) ((ByteBuffer) finalCorrectPartOfChunk.position(CastUtils.l2i(finalOffsetWithInChunk))).slice().limit(CastUtils.l2i(sampleSize));
            }

            public String toString() {
                return "DefaultMp4Sample(size:" + sampleSize + ")";
            }
        };
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return CastUtils.l2i(this.trackBox.getSampleTableBox().getSampleSizeBox().getSampleCount());
    }
}
