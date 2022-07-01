package com.google.android.exoplayer2.extractor.ogg;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.EOFException;
import java.io.IOException;
/* loaded from: classes.dex */
final class OggPageHeader {
    public int bodySize;
    public long granulePosition;
    public int headerSize;
    public int pageSegmentCount;
    public int revision;
    public int type;
    public final int[] laces = new int[255];
    private final ParsableByteArray scratch = new ParsableByteArray(255);

    public void reset() {
        this.revision = 0;
        this.type = 0;
        this.granulePosition = 0L;
        this.pageSegmentCount = 0;
        this.headerSize = 0;
        this.bodySize = 0;
    }

    public boolean populate(ExtractorInput extractorInput, boolean z) throws IOException, InterruptedException {
        this.scratch.reset();
        reset();
        if (!(extractorInput.getLength() == -1 || extractorInput.getLength() - extractorInput.getPeekPosition() >= 27) || !extractorInput.peekFully(this.scratch.data, 0, 27, true)) {
            if (!z) {
                throw new EOFException();
            }
            return false;
        } else if (this.scratch.readUnsignedInt() != 1332176723) {
            if (!z) {
                throw new ParserException("expected OggS capture pattern at begin of page");
            }
            return false;
        } else {
            int readUnsignedByte = this.scratch.readUnsignedByte();
            this.revision = readUnsignedByte;
            if (readUnsignedByte != 0) {
                if (!z) {
                    throw new ParserException("unsupported bit stream revision");
                }
                return false;
            }
            this.type = this.scratch.readUnsignedByte();
            this.granulePosition = this.scratch.readLittleEndianLong();
            this.scratch.readLittleEndianUnsignedInt();
            this.scratch.readLittleEndianUnsignedInt();
            this.scratch.readLittleEndianUnsignedInt();
            int readUnsignedByte2 = this.scratch.readUnsignedByte();
            this.pageSegmentCount = readUnsignedByte2;
            this.headerSize = readUnsignedByte2 + 27;
            this.scratch.reset();
            extractorInput.peekFully(this.scratch.data, 0, this.pageSegmentCount);
            for (int i = 0; i < this.pageSegmentCount; i++) {
                this.laces[i] = this.scratch.readUnsignedByte();
                this.bodySize += this.laces[i];
            }
            return true;
        }
    }
}
