package com.mp4parser.iso14496.part15;

import com.coremedia.iso.IsoTypeReader;
import com.coremedia.iso.IsoTypeWriter;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry;
import java.nio.ByteBuffer;
/* loaded from: classes3.dex */
public class SyncSampleEntry extends GroupEntry {
    public static final String TYPE = "sync";
    int nalUnitType;
    int reserved;

    @Override // com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry
    public void parse(ByteBuffer byteBuffer) {
        int a = IsoTypeReader.readUInt8(byteBuffer);
        this.reserved = (a & PsExtractor.AUDIO_STREAM) >> 6;
        this.nalUnitType = a & 63;
    }

    @Override // com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry
    public ByteBuffer get() {
        ByteBuffer b = ByteBuffer.allocate(1);
        IsoTypeWriter.writeUInt8(b, this.nalUnitType + (this.reserved << 6));
        return (ByteBuffer) b.rewind();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SyncSampleEntry that = (SyncSampleEntry) o;
        if (this.nalUnitType == that.nalUnitType && this.reserved == that.reserved) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = this.reserved;
        return (result * 31) + this.nalUnitType;
    }

    public int getReserved() {
        return this.reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public int getNalUnitType() {
        return this.nalUnitType;
    }

    public void setNalUnitType(int nalUnitType) {
        this.nalUnitType = nalUnitType;
    }

    @Override // com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry
    public String getType() {
        return TYPE;
    }

    public String toString() {
        return "SyncSampleEntry{reserved=" + this.reserved + ", nalUnitType=" + this.nalUnitType + '}';
    }
}
