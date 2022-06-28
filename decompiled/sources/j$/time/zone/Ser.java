package j$.time.zone;

import j$.time.ZoneOffset;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.Externalizable;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;
/* loaded from: classes2.dex */
public final class Ser implements Externalizable {
    static final byte TZRULES = 100;
    static final byte ZOT = 2;
    static final byte ZOTRULE = 3;
    static final byte ZRULES = 1;
    private static final long serialVersionUID = -8885321777449118786L;
    private Object object;
    private byte type;

    public Ser() {
    }

    public Ser(byte type, Object object) {
        this.type = type;
        this.object = object;
    }

    @Override // java.io.Externalizable
    public void writeExternal(ObjectOutput out) {
        writeInternal(this.type, this.object, out);
    }

    static void write(Object object, DataOutput out) {
        writeInternal((byte) 1, object, out);
    }

    private static void writeInternal(byte type, Object object, DataOutput out) {
        out.writeByte(type);
        switch (type) {
            case 1:
                ((ZoneRules) object).writeExternal(out);
                return;
            case 2:
                ((ZoneOffsetTransition) object).writeExternal(out);
                return;
            case 3:
                ((ZoneOffsetTransitionRule) object).writeExternal(out);
                return;
            case 100:
                ((ZoneRules) object).writeExternalTimeZone(out);
                return;
            default:
                throw new InvalidClassException("Unknown serialized type");
        }
    }

    @Override // java.io.Externalizable
    public void readExternal(ObjectInput in) {
        byte readByte = in.readByte();
        this.type = readByte;
        this.object = readInternal(readByte, in);
    }

    public static Object read(DataInput in) {
        byte type = in.readByte();
        return readInternal(type, in);
    }

    private static Object readInternal(byte type, DataInput in) {
        switch (type) {
            case 1:
                return ZoneRules.readExternal(in);
            case 2:
                return ZoneOffsetTransition.readExternal(in);
            case 3:
                return ZoneOffsetTransitionRule.readExternal(in);
            case 100:
                return ZoneRules.readExternalTimeZone(in);
            default:
                throw new StreamCorruptedException("Unknown serialized type");
        }
    }

    private Object readResolve() {
        return this.object;
    }

    public static void writeOffset(ZoneOffset offset, DataOutput out) {
        int offsetSecs = offset.getTotalSeconds();
        int offsetByte = offsetSecs % 900 == 0 ? offsetSecs / 900 : 127;
        out.writeByte(offsetByte);
        if (offsetByte == 127) {
            out.writeInt(offsetSecs);
        }
    }

    public static ZoneOffset readOffset(DataInput in) {
        int offsetByte = in.readByte();
        return ZoneOffset.ofTotalSeconds(offsetByte == 127 ? in.readInt() : offsetByte * 900);
    }

    public static void writeEpochSec(long epochSec, DataOutput out) {
        if (epochSec >= -4575744000L && epochSec < 10413792000L && epochSec % 900 == 0) {
            int store = (int) ((4575744000L + epochSec) / 900);
            out.writeByte((store >>> 16) & 255);
            out.writeByte(255 & (store >>> 8));
            out.writeByte(store & 255);
            return;
        }
        out.writeByte(255);
        out.writeLong(epochSec);
    }

    public static long readEpochSec(DataInput in) {
        int hiByte = in.readByte() & 255;
        if (hiByte == 255) {
            return in.readLong();
        }
        int midByte = in.readByte() & 255;
        int loByte = 255 & in.readByte();
        long tot = (hiByte << 16) + (midByte << 8) + loByte;
        return (900 * tot) - 4575744000L;
    }
}
