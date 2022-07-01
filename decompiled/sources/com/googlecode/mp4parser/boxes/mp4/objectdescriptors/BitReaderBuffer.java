package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import java.nio.ByteBuffer;
/* loaded from: classes.dex */
public class BitReaderBuffer {
    private ByteBuffer buffer;
    int initialPos;
    int position;

    public BitReaderBuffer(ByteBuffer byteBuffer) {
        this.buffer = byteBuffer;
        this.initialPos = byteBuffer.position();
    }

    public boolean readBool() {
        return readBits(1) == 1;
    }

    public int readBits(int i) {
        int i2;
        int i3 = this.buffer.get(this.initialPos + (this.position / 8));
        if (i3 < 0) {
            i3 += 256;
        }
        int i4 = this.position;
        int i5 = 8 - (i4 % 8);
        if (i <= i5) {
            i2 = ((i3 << (i4 % 8)) & 255) >> ((i4 % 8) + (i5 - i));
            this.position = i4 + i;
        } else {
            int i6 = i - i5;
            i2 = (readBits(i5) << i6) + readBits(i6);
        }
        ByteBuffer byteBuffer = this.buffer;
        int i7 = this.initialPos;
        double d = this.position;
        Double.isNaN(d);
        byteBuffer.position(i7 + ((int) Math.ceil(d / 8.0d)));
        return i2;
    }

    public int remainingBits() {
        return (this.buffer.limit() * 8) - this.position;
    }
}
