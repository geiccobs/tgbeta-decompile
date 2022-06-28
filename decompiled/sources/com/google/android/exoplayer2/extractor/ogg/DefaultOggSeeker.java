package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.SeekPoint;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
/* loaded from: classes3.dex */
public final class DefaultOggSeeker implements OggSeeker {
    private static final int DEFAULT_OFFSET = 30000;
    private static final int MATCH_BYTE_RANGE = 100000;
    private static final int MATCH_RANGE = 72000;
    private static final int STATE_IDLE = 4;
    private static final int STATE_READ_LAST_PAGE = 1;
    private static final int STATE_SEEK = 2;
    private static final int STATE_SEEK_TO_END = 0;
    private static final int STATE_SKIP = 3;
    private long end;
    private long endGranule;
    private final OggPageHeader pageHeader = new OggPageHeader();
    private final long payloadEndPosition;
    private final long payloadStartPosition;
    private long positionBeforeSeekToEnd;
    private long start;
    private long startGranule;
    private int state;
    private final StreamReader streamReader;
    private long targetGranule;
    private long totalGranules;

    public DefaultOggSeeker(StreamReader streamReader, long payloadStartPosition, long payloadEndPosition, long firstPayloadPageSize, long firstPayloadPageGranulePosition, boolean firstPayloadPageIsLastPage) {
        Assertions.checkArgument(payloadStartPosition >= 0 && payloadEndPosition > payloadStartPosition);
        this.streamReader = streamReader;
        this.payloadStartPosition = payloadStartPosition;
        this.payloadEndPosition = payloadEndPosition;
        if (firstPayloadPageSize == payloadEndPosition - payloadStartPosition || firstPayloadPageIsLastPage) {
            this.totalGranules = firstPayloadPageGranulePosition;
            this.state = 4;
            return;
        }
        this.state = 0;
    }

    @Override // com.google.android.exoplayer2.extractor.ogg.OggSeeker
    public long read(ExtractorInput input) throws IOException, InterruptedException {
        switch (this.state) {
            case 0:
                long position = input.getPosition();
                this.positionBeforeSeekToEnd = position;
                this.state = 1;
                long lastPageSearchPosition = this.payloadEndPosition - 65307;
                if (lastPageSearchPosition > position) {
                    return lastPageSearchPosition;
                }
                this.totalGranules = readGranuleOfLastPage(input);
                this.state = 4;
                return this.positionBeforeSeekToEnd;
            case 1:
                this.totalGranules = readGranuleOfLastPage(input);
                this.state = 4;
                return this.positionBeforeSeekToEnd;
            case 2:
                long position2 = getNextSeekPosition(input);
                if (position2 != -1) {
                    return position2;
                }
                this.state = 3;
                skipToPageOfTargetGranule(input);
                this.state = 4;
                return -(this.startGranule + 2);
            case 3:
                skipToPageOfTargetGranule(input);
                this.state = 4;
                return -(this.startGranule + 2);
            case 4:
                return -1L;
            default:
                throw new IllegalStateException();
        }
    }

    @Override // com.google.android.exoplayer2.extractor.ogg.OggSeeker
    public OggSeekMap createSeekMap() {
        if (this.totalGranules != 0) {
            return new OggSeekMap();
        }
        return null;
    }

    @Override // com.google.android.exoplayer2.extractor.ogg.OggSeeker
    public void startSeek(long targetGranule) {
        this.targetGranule = Util.constrainValue(targetGranule, 0L, this.totalGranules - 1);
        this.state = 2;
        this.start = this.payloadStartPosition;
        this.end = this.payloadEndPosition;
        this.startGranule = 0L;
        this.endGranule = this.totalGranules;
    }

    private long getNextSeekPosition(ExtractorInput input) throws IOException, InterruptedException {
        if (this.start == this.end) {
            return -1L;
        }
        long currentPosition = input.getPosition();
        if (!skipToNextPage(input, this.end)) {
            long j = this.start;
            if (j == currentPosition) {
                throw new IOException("No ogg page can be found.");
            }
            return j;
        }
        this.pageHeader.populate(input, false);
        input.resetPeekPosition();
        long granuleDistance = this.targetGranule - this.pageHeader.granulePosition;
        int pageSize = this.pageHeader.headerSize + this.pageHeader.bodySize;
        if (0 <= granuleDistance && granuleDistance < 72000) {
            return -1L;
        }
        if (granuleDistance >= 0) {
            this.start = input.getPosition() + pageSize;
            this.startGranule = this.pageHeader.granulePosition;
        } else {
            this.end = currentPosition;
            this.endGranule = this.pageHeader.granulePosition;
        }
        long j2 = this.end;
        long j3 = this.start;
        if (j2 - j3 >= 100000) {
            long offset = pageSize * (granuleDistance <= 0 ? 2L : 1L);
            long j4 = this.end;
            long j5 = this.start;
            long nextPosition = (input.getPosition() - offset) + (((j4 - j5) * granuleDistance) / (this.endGranule - this.startGranule));
            return Util.constrainValue(nextPosition, j5, j4 - 1);
        }
        this.end = j3;
        return j3;
    }

    private void skipToPageOfTargetGranule(ExtractorInput input) throws IOException, InterruptedException {
        this.pageHeader.populate(input, false);
        while (this.pageHeader.granulePosition <= this.targetGranule) {
            input.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
            this.start = input.getPosition();
            this.startGranule = this.pageHeader.granulePosition;
            this.pageHeader.populate(input, false);
        }
        input.resetPeekPosition();
    }

    void skipToNextPage(ExtractorInput input) throws IOException, InterruptedException {
        if (!skipToNextPage(input, this.payloadEndPosition)) {
            throw new EOFException();
        }
    }

    private boolean skipToNextPage(ExtractorInput input, long limit) throws IOException, InterruptedException {
        long limit2 = Math.min(3 + limit, this.payloadEndPosition);
        byte[] buffer = new byte[2048];
        int peekLength = buffer.length;
        while (true) {
            if (input.getPosition() + peekLength <= limit2 || (peekLength = (int) (limit2 - input.getPosition())) >= 4) {
                input.peekFully(buffer, 0, peekLength, false);
                for (int i = 0; i < peekLength - 3; i++) {
                    if (buffer[i] == 79 && buffer[i + 1] == 103 && buffer[i + 2] == 103 && buffer[i + 3] == 83) {
                        input.skipFully(i);
                        return true;
                    }
                }
                int i2 = peekLength - 3;
                input.skipFully(i2);
            } else {
                return false;
            }
        }
    }

    long readGranuleOfLastPage(ExtractorInput input) throws IOException, InterruptedException {
        skipToNextPage(input);
        this.pageHeader.reset();
        while ((this.pageHeader.type & 4) != 4 && input.getPosition() < this.payloadEndPosition) {
            this.pageHeader.populate(input, false);
            input.skipFully(this.pageHeader.headerSize + this.pageHeader.bodySize);
        }
        return this.pageHeader.granulePosition;
    }

    /* loaded from: classes3.dex */
    public final class OggSeekMap implements SeekMap {
        private OggSeekMap() {
            DefaultOggSeeker.this = r1;
        }

        @Override // com.google.android.exoplayer2.extractor.SeekMap
        public boolean isSeekable() {
            return true;
        }

        @Override // com.google.android.exoplayer2.extractor.SeekMap
        public SeekMap.SeekPoints getSeekPoints(long timeUs) {
            long targetGranule = DefaultOggSeeker.this.streamReader.convertTimeToGranule(timeUs);
            long estimatedPosition = (DefaultOggSeeker.this.payloadStartPosition + (((DefaultOggSeeker.this.payloadEndPosition - DefaultOggSeeker.this.payloadStartPosition) * targetGranule) / DefaultOggSeeker.this.totalGranules)) - 30000;
            return new SeekMap.SeekPoints(new SeekPoint(timeUs, Util.constrainValue(estimatedPosition, DefaultOggSeeker.this.payloadStartPosition, DefaultOggSeeker.this.payloadEndPosition - 1)));
        }

        @Override // com.google.android.exoplayer2.extractor.SeekMap
        public long getDurationUs() {
            return DefaultOggSeeker.this.streamReader.convertGranuleToTime(DefaultOggSeeker.this.totalGranules);
        }
    }
}
