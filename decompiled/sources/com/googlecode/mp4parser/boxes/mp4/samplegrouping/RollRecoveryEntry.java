package com.googlecode.mp4parser.boxes.mp4.samplegrouping;

import java.nio.ByteBuffer;
/* loaded from: classes3.dex */
public class RollRecoveryEntry extends GroupEntry {
    public static final String TYPE = "roll";
    private short rollDistance;

    @Override // com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry
    public String getType() {
        return TYPE;
    }

    public short getRollDistance() {
        return this.rollDistance;
    }

    public void setRollDistance(short rollDistance) {
        this.rollDistance = rollDistance;
    }

    @Override // com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry
    public void parse(ByteBuffer byteBuffer) {
        this.rollDistance = byteBuffer.getShort();
    }

    @Override // com.googlecode.mp4parser.boxes.mp4.samplegrouping.GroupEntry
    public ByteBuffer get() {
        ByteBuffer content = ByteBuffer.allocate(2);
        content.putShort(this.rollDistance);
        content.rewind();
        return content;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RollRecoveryEntry entry = (RollRecoveryEntry) o;
        if (this.rollDistance == entry.rollDistance) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.rollDistance;
    }
}
