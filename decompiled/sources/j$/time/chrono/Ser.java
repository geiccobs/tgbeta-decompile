package j$.time.chrono;

import java.io.Externalizable;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.StreamCorruptedException;
/* loaded from: classes2.dex */
public final class Ser implements Externalizable {
    static final byte CHRONO_LOCAL_DATE_TIME_TYPE = 2;
    static final byte CHRONO_PERIOD_TYPE = 9;
    static final byte CHRONO_TYPE = 1;
    static final byte CHRONO_ZONE_DATE_TIME_TYPE = 3;
    static final byte HIJRAH_DATE_TYPE = 6;
    static final byte JAPANESE_DATE_TYPE = 4;
    static final byte JAPANESE_ERA_TYPE = 5;
    static final byte MINGUO_DATE_TYPE = 7;
    static final byte THAIBUDDHIST_DATE_TYPE = 8;
    private static final long serialVersionUID = -6103370247208168577L;
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

    private static void writeInternal(byte type, Object object, ObjectOutput out) {
        out.writeByte(type);
        switch (type) {
            case 1:
                ((AbstractChronology) object).writeExternal(out);
                return;
            case 2:
                ((ChronoLocalDateTimeImpl) object).writeExternal(out);
                return;
            case 3:
                ((ChronoZonedDateTimeImpl) object).writeExternal(out);
                return;
            case 4:
                ((JapaneseDate) object).writeExternal(out);
                return;
            case 5:
                ((JapaneseEra) object).writeExternal(out);
                return;
            case 6:
                ((HijrahDate) object).writeExternal(out);
                return;
            case 7:
                ((MinguoDate) object).writeExternal(out);
                return;
            case 8:
                ((ThaiBuddhistDate) object).writeExternal(out);
                return;
            case 9:
                ((ChronoPeriodImpl) object).writeExternal(out);
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

    static Object read(ObjectInput in) {
        byte type = in.readByte();
        return readInternal(type, in);
    }

    private static Object readInternal(byte type, ObjectInput in) {
        switch (type) {
            case 1:
                return AbstractChronology.readExternal(in);
            case 2:
                return ChronoLocalDateTimeImpl.readExternal(in);
            case 3:
                return ChronoZonedDateTimeImpl.readExternal(in);
            case 4:
                return JapaneseDate.readExternal(in);
            case 5:
                return JapaneseEra.readExternal(in);
            case 6:
                return HijrahDate.readExternal(in);
            case 7:
                return MinguoDate.readExternal(in);
            case 8:
                return ThaiBuddhistDate.readExternal(in);
            case 9:
                return ChronoPeriodImpl.readExternal(in);
            default:
                throw new StreamCorruptedException("Unknown serialized type");
        }
    }

    private Object readResolve() {
        return this.object;
    }
}
