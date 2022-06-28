package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.RangeInputStream;
/* loaded from: classes4.dex */
public class ID3v2FrameBody {
    static final ThreadLocal<Buffer> textBuffer = new ThreadLocal<Buffer>() { // from class: org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody.1
        @Override // java.lang.ThreadLocal
        public Buffer initialValue() {
            return new Buffer(4096);
        }
    };
    private final ID3v2DataInput data;
    private final ID3v2FrameHeader frameHeader;
    private final RangeInputStream input;
    private final ID3v2TagHeader tagHeader;

    /* loaded from: classes4.dex */
    public static final class Buffer {
        byte[] bytes;

        Buffer(int initialLength) {
            this.bytes = new byte[initialLength];
        }

        byte[] bytes(int minLength) {
            byte[] bArr = this.bytes;
            if (minLength > bArr.length) {
                int length = bArr.length * 2;
                while (minLength > length) {
                    length *= 2;
                }
                this.bytes = new byte[length];
            }
            return this.bytes;
        }
    }

    public ID3v2FrameBody(InputStream delegate, long position, int dataLength, ID3v2TagHeader tagHeader, ID3v2FrameHeader frameHeader) throws IOException {
        RangeInputStream rangeInputStream = new RangeInputStream(delegate, position, dataLength);
        this.input = rangeInputStream;
        this.data = new ID3v2DataInput(rangeInputStream);
        this.tagHeader = tagHeader;
        this.frameHeader = frameHeader;
    }

    public ID3v2DataInput getData() {
        return this.data;
    }

    public long getPosition() {
        return this.input.getPosition();
    }

    public long getRemainingLength() {
        return this.input.getRemainingLength();
    }

    public ID3v2TagHeader getTagHeader() {
        return this.tagHeader;
    }

    public ID3v2FrameHeader getFrameHeader() {
        return this.frameHeader;
    }

    private String extractString(byte[] bytes, int offset, int length, ID3v2Encoding encoding, boolean searchZeros) {
        if (searchZeros) {
            int zeros = 0;
            for (int i = 0; i < length; i++) {
                if (bytes[offset + i] == 0 && (encoding != ID3v2Encoding.UTF_16 || zeros != 0 || (offset + i) % 2 == 0)) {
                    zeros++;
                    if (zeros == encoding.getZeroBytes()) {
                        length = (i + 1) - encoding.getZeroBytes();
                        break;
                    }
                } else {
                    zeros = 0;
                }
            }
        }
        try {
            String string = new String(bytes, offset, length, encoding.getCharset().name());
            if (string.length() > 0 && string.charAt(0) == 65279) {
                return string.substring(1);
            }
            return string;
        } catch (Exception e) {
            return "";
        }
    }

    public String readZeroTerminatedString(int maxLength, ID3v2Encoding encoding) throws IOException, ID3v2Exception {
        int zeros = 0;
        int length = Math.min(maxLength, (int) getRemainingLength());
        byte[] bytes = textBuffer.get().bytes(length);
        for (int i = 0; i < length; i++) {
            byte readByte = this.data.readByte();
            bytes[i] = readByte;
            if (readByte == 0 && (encoding != ID3v2Encoding.UTF_16 || zeros != 0 || i % 2 == 0)) {
                zeros++;
                if (zeros == encoding.getZeroBytes()) {
                    return extractString(bytes, 0, (i + 1) - encoding.getZeroBytes(), encoding, false);
                }
            } else {
                zeros = 0;
            }
        }
        throw new ID3v2Exception("Could not read zero-termiated string");
    }

    public String readFixedLengthString(int length, ID3v2Encoding encoding) throws IOException, ID3v2Exception {
        if (length > getRemainingLength()) {
            throw new ID3v2Exception("Could not read fixed-length string of length: " + length);
        }
        byte[] bytes = textBuffer.get().bytes(length);
        this.data.readFully(bytes, 0, length);
        return extractString(bytes, 0, length, encoding, true);
    }

    public ID3v2Encoding readEncoding() throws IOException, ID3v2Exception {
        byte value = this.data.readByte();
        switch (value) {
            case 0:
                return ID3v2Encoding.ISO_8859_1;
            case 1:
                return ID3v2Encoding.UTF_16;
            case 2:
                return ID3v2Encoding.UTF_16BE;
            case 3:
                return ID3v2Encoding.UTF_8;
            default:
                throw new ID3v2Exception("Invalid encoding: " + ((int) value));
        }
    }

    public String toString() {
        return "id3v2frame[pos=" + getPosition() + ", " + getRemainingLength() + " left]";
    }
}
