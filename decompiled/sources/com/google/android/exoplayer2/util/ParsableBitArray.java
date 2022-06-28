package com.google.android.exoplayer2.util;

import androidx.core.view.MotionEventCompat;
/* loaded from: classes3.dex */
public final class ParsableBitArray {
    private int bitOffset;
    private int byteLimit;
    private int byteOffset;
    public byte[] data;

    public ParsableBitArray() {
        this.data = Util.EMPTY_BYTE_ARRAY;
    }

    public ParsableBitArray(byte[] data) {
        this(data, data.length);
    }

    public ParsableBitArray(byte[] data, int limit) {
        this.data = data;
        this.byteLimit = limit;
    }

    public void reset(byte[] data) {
        reset(data, data.length);
    }

    public void reset(ParsableByteArray parsableByteArray) {
        reset(parsableByteArray.data, parsableByteArray.limit());
        setPosition(parsableByteArray.getPosition() * 8);
    }

    public void reset(byte[] data, int limit) {
        this.data = data;
        this.byteOffset = 0;
        this.bitOffset = 0;
        this.byteLimit = limit;
    }

    public int bitsLeft() {
        return ((this.byteLimit - this.byteOffset) * 8) - this.bitOffset;
    }

    public int getPosition() {
        return (this.byteOffset * 8) + this.bitOffset;
    }

    public int getBytePosition() {
        Assertions.checkState(this.bitOffset == 0);
        return this.byteOffset;
    }

    public void setPosition(int position) {
        int i = position / 8;
        this.byteOffset = i;
        this.bitOffset = position - (i * 8);
        assertValidOffset();
    }

    public void skipBit() {
        int i = this.bitOffset + 1;
        this.bitOffset = i;
        if (i == 8) {
            this.bitOffset = 0;
            this.byteOffset++;
        }
        assertValidOffset();
    }

    public void skipBits(int numBits) {
        int numBytes = numBits / 8;
        int i = this.byteOffset + numBytes;
        this.byteOffset = i;
        int i2 = this.bitOffset + (numBits - (numBytes * 8));
        this.bitOffset = i2;
        if (i2 > 7) {
            this.byteOffset = i + 1;
            this.bitOffset = i2 - 8;
        }
        assertValidOffset();
    }

    public boolean readBit() {
        boolean returnValue = (this.data[this.byteOffset] & (128 >> this.bitOffset)) != 0;
        skipBit();
        return returnValue;
    }

    public int readBits(int numBits) {
        int i;
        if (numBits == 0) {
            return 0;
        }
        int returnValue = 0;
        this.bitOffset += numBits;
        while (true) {
            i = this.bitOffset;
            if (i <= 8) {
                break;
            }
            int i2 = i - 8;
            this.bitOffset = i2;
            byte[] bArr = this.data;
            int i3 = this.byteOffset;
            this.byteOffset = i3 + 1;
            returnValue |= (bArr[i3] & 255) << i2;
        }
        byte[] bArr2 = this.data;
        int i4 = this.byteOffset;
        int returnValue2 = (returnValue | ((bArr2[i4] & 255) >> (8 - i))) & ((-1) >>> (32 - numBits));
        if (i == 8) {
            this.bitOffset = 0;
            this.byteOffset = i4 + 1;
        }
        assertValidOffset();
        return returnValue2;
    }

    public long readBitsToLong(int numBits) {
        if (numBits > 32) {
            return Util.toLong(readBits(numBits - 32), readBits(32));
        }
        return Util.toUnsignedLong(readBits(numBits));
    }

    public void readBits(byte[] buffer, int offset, int numBits) {
        int to = (numBits >> 3) + offset;
        for (int i = offset; i < to; i++) {
            byte[] bArr = this.data;
            int i2 = this.byteOffset;
            int i3 = i2 + 1;
            this.byteOffset = i3;
            byte b = bArr[i2];
            int i4 = this.bitOffset;
            buffer[i] = (byte) (b << i4);
            buffer[i] = (byte) (((255 & bArr[i3]) >> (8 - i4)) | buffer[i]);
        }
        int i5 = numBits & 7;
        if (i5 == 0) {
            return;
        }
        buffer[to] = (byte) (buffer[to] & (255 >> i5));
        int i6 = this.bitOffset;
        if (i6 + i5 > 8) {
            int i7 = buffer[to];
            byte[] bArr2 = this.data;
            int i8 = this.byteOffset;
            this.byteOffset = i8 + 1;
            buffer[to] = (byte) (i7 | ((bArr2[i8] & 255) << i6));
            this.bitOffset = i6 - 8;
        }
        int i9 = this.bitOffset + i5;
        this.bitOffset = i9;
        byte[] bArr3 = this.data;
        int i10 = this.byteOffset;
        int lastDataByteTrailingBits = (255 & bArr3[i10]) >> (8 - i9);
        buffer[to] = (byte) (buffer[to] | ((byte) (lastDataByteTrailingBits << (8 - i5))));
        if (i9 == 8) {
            this.bitOffset = 0;
            this.byteOffset = i10 + 1;
        }
        assertValidOffset();
    }

    public void byteAlign() {
        if (this.bitOffset == 0) {
            return;
        }
        this.bitOffset = 0;
        this.byteOffset++;
        assertValidOffset();
    }

    public void readBytes(byte[] buffer, int offset, int length) {
        Assertions.checkState(this.bitOffset == 0);
        System.arraycopy(this.data, this.byteOffset, buffer, offset, length);
        this.byteOffset += length;
        assertValidOffset();
    }

    public void skipBytes(int length) {
        Assertions.checkState(this.bitOffset == 0);
        this.byteOffset += length;
        assertValidOffset();
    }

    public void putInt(int value, int numBits) {
        if (numBits < 32) {
            value &= (1 << numBits) - 1;
        }
        int firstByteReadSize = Math.min(8 - this.bitOffset, numBits);
        int i = this.bitOffset;
        int firstByteRightPaddingSize = (8 - i) - firstByteReadSize;
        int firstByteBitmask = (MotionEventCompat.ACTION_POINTER_INDEX_MASK >> i) | ((1 << firstByteRightPaddingSize) - 1);
        byte[] bArr = this.data;
        int i2 = this.byteOffset;
        bArr[i2] = (byte) (bArr[i2] & firstByteBitmask);
        int firstByteInputBits = value >>> (numBits - firstByteReadSize);
        bArr[i2] = (byte) (bArr[i2] | (firstByteInputBits << firstByteRightPaddingSize));
        int remainingBitsToRead = numBits - firstByteReadSize;
        int currentByteIndex = i2 + 1;
        while (remainingBitsToRead > 8) {
            this.data[currentByteIndex] = (byte) (value >>> (remainingBitsToRead - 8));
            remainingBitsToRead -= 8;
            currentByteIndex++;
        }
        int lastByteRightPaddingSize = 8 - remainingBitsToRead;
        byte[] bArr2 = this.data;
        bArr2[currentByteIndex] = (byte) (bArr2[currentByteIndex] & ((1 << lastByteRightPaddingSize) - 1));
        int lastByteInput = value & ((1 << remainingBitsToRead) - 1);
        bArr2[currentByteIndex] = (byte) (bArr2[currentByteIndex] | (lastByteInput << lastByteRightPaddingSize));
        skipBits(numBits);
        assertValidOffset();
    }

    private void assertValidOffset() {
        int i;
        int i2 = this.byteOffset;
        Assertions.checkState(i2 >= 0 && (i2 < (i = this.byteLimit) || (i2 == i && this.bitOffset == 0)));
    }
}
