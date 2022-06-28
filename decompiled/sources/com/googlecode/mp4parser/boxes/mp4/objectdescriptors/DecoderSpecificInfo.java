package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.Hex;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
@Descriptor(tags = {5})
/* loaded from: classes3.dex */
public class DecoderSpecificInfo extends BaseDescriptor {
    byte[] bytes;

    @Override // com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor
    public void parseDetail(ByteBuffer bb) throws IOException {
        if (this.sizeOfInstance > 0) {
            byte[] bArr = new byte[this.sizeOfInstance];
            this.bytes = bArr;
            bb.get(bArr);
        }
    }

    public int serializedSize() {
        return this.bytes.length;
    }

    public ByteBuffer serialize() {
        ByteBuffer out = ByteBuffer.wrap(this.bytes);
        return out;
    }

    @Override // com.googlecode.mp4parser.boxes.mp4.objectdescriptors.BaseDescriptor
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DecoderSpecificInfo");
        sb.append("{bytes=");
        byte[] bArr = this.bytes;
        sb.append(bArr == null ? "null" : Hex.encodeHex(bArr));
        sb.append('}');
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DecoderSpecificInfo that = (DecoderSpecificInfo) o;
        if (Arrays.equals(this.bytes, that.bytes)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        byte[] bArr = this.bytes;
        if (bArr != null) {
            return Arrays.hashCode(bArr);
        }
        return 0;
    }
}
