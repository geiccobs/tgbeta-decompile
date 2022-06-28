package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import java.nio.ByteBuffer;
import org.telegram.tgnet.ConnectionsManager;
/* loaded from: classes3.dex */
public class BitReaderBuffer {
    private ByteBuffer buffer;
    int initialPos;
    int position;

    public BitReaderBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
        this.initialPos = buffer.position();
    }

    public boolean readBool() {
        return readBits(1) == 1;
    }

    public int readBits(int i) {
        int then;
        byte b = this.buffer.get(this.initialPos + (this.position / 8));
        int v = b < 0 ? b + ConnectionsManager.USE_IPV4_ONLY : b;
        int i2 = this.position;
        int left = 8 - (i2 % 8);
        if (i <= left) {
            then = ((v << (i2 % 8)) & 255) >> ((i2 % 8) + (left - i));
            this.position = i2 + i;
        } else {
            int then2 = i - left;
            int rc = readBits(left);
            then = (rc << then2) + readBits(then2);
        }
        ByteBuffer byteBuffer = this.buffer;
        int i3 = this.initialPos;
        double d = this.position;
        Double.isNaN(d);
        byteBuffer.position(i3 + ((int) Math.ceil(d / 8.0d)));
        return then;
    }

    public int getPosition() {
        return this.position;
    }

    public int byteSync() {
        int left = 8 - (this.position % 8);
        if (left == 8) {
            left = 0;
        }
        readBits(left);
        return left;
    }

    public int remainingBits() {
        return (this.buffer.limit() * 8) - this.position;
    }
}
