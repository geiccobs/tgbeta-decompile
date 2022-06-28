package j$.time;

import com.google.android.exoplayer2.C;
import j$.time.format.DateTimeFormatter;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.telegram.messenger.MediaController;
/* loaded from: classes2.dex */
public final class Instant implements Temporal, TemporalAdjuster, Comparable<Instant>, Serializable {
    private static final long serialVersionUID = -665713676816604388L;
    private final int nanos;
    private final long seconds;
    public static final Instant EPOCH = new Instant(0, 0);
    private static final long MIN_SECOND = -31557014167219200L;
    public static final Instant MIN = ofEpochSecond(MIN_SECOND, 0);
    private static final long MAX_SECOND = 31556889864403199L;
    public static final Instant MAX = ofEpochSecond(MAX_SECOND, 999999999);

    public static Instant now() {
        return Clock.systemUTC().instant();
    }

    public static Instant now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        return clock.instant();
    }

    public static Instant ofEpochSecond(long epochSecond) {
        return create(epochSecond, 0);
    }

    public static Instant ofEpochSecond(long epochSecond, long nanoAdjustment) {
        long secs = Clock$OffsetClock$$ExternalSyntheticBackport0.m(epochSecond, Duration$$ExternalSyntheticBackport0.m(nanoAdjustment, C.NANOS_PER_SECOND));
        int nos = (int) Clock$TickClock$$ExternalSyntheticBackport0.m(nanoAdjustment, C.NANOS_PER_SECOND);
        return create(secs, nos);
    }

    public static Instant ofEpochMilli(long epochMilli) {
        long secs = Duration$$ExternalSyntheticBackport0.m(epochMilli, 1000L);
        int mos = (int) Clock$TickClock$$ExternalSyntheticBackport0.m(epochMilli, 1000L);
        return create(secs, MediaController.VIDEO_BITRATE_480 * mos);
    }

    public static Instant from(TemporalAccessor temporal) {
        if (temporal instanceof Instant) {
            return (Instant) temporal;
        }
        Objects.requireNonNull(temporal, "temporal");
        try {
            long instantSecs = temporal.getLong(ChronoField.INSTANT_SECONDS);
            int nanoOfSecond = temporal.get(ChronoField.NANO_OF_SECOND);
            return ofEpochSecond(instantSecs, nanoOfSecond);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain Instant from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public static Instant parse(CharSequence text) {
        return (Instant) DateTimeFormatter.ISO_INSTANT.parse(text, Instant$$ExternalSyntheticLambda1.INSTANCE);
    }

    private static Instant create(long seconds, int nanoOfSecond) {
        if ((nanoOfSecond | seconds) == 0) {
            return EPOCH;
        }
        if (seconds < MIN_SECOND || seconds > MAX_SECOND) {
            throw new DateTimeException("Instant exceeds minimum or maximum instant");
        }
        return new Instant(seconds, nanoOfSecond);
    }

    private Instant(long epochSecond, int nanos) {
        this.seconds = epochSecond;
        this.nanos = nanos;
    }

    @Override // j$.time.temporal.TemporalAccessor
    public boolean isSupported(TemporalField field) {
        return field instanceof ChronoField ? field == ChronoField.INSTANT_SECONDS || field == ChronoField.NANO_OF_SECOND || field == ChronoField.MICRO_OF_SECOND || field == ChronoField.MILLI_OF_SECOND : field != null && field.isSupportedBy(this);
    }

    @Override // j$.time.temporal.Temporal
    public boolean isSupported(TemporalUnit unit) {
        return unit instanceof ChronoUnit ? unit.isTimeBased() || unit == ChronoUnit.DAYS : unit != null && unit.isSupportedBy(this);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public ValueRange range(TemporalField field) {
        return TemporalAccessor.CC.$default$range(this, field);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // j$.time.temporal.TemporalAccessor
    public int get(TemporalField field) {
        if (field instanceof ChronoField) {
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
                case 1:
                    return this.nanos;
                case 2:
                    return this.nanos / 1000;
                case 3:
                    return this.nanos / MediaController.VIDEO_BITRATE_480;
                case 4:
                    ChronoField.INSTANT_SECONDS.checkValidIntValue(this.seconds);
                    break;
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return range(field).checkValidIntValue(field.getFrom(this), field);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
                case 1:
                    return this.nanos;
                case 2:
                    return this.nanos / 1000;
                case 3:
                    return this.nanos / MediaController.VIDEO_BITRATE_480;
                case 4:
                    return this.seconds;
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        }
        return field.getFrom(this);
    }

    public long getEpochSecond() {
        return this.seconds;
    }

    public int getNano() {
        return this.nanos;
    }

    @Override // j$.time.temporal.Temporal
    public Instant with(TemporalAdjuster adjuster) {
        return (Instant) adjuster.adjustInto(this);
    }

    @Override // j$.time.temporal.Temporal
    public Instant with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            f.checkValidValue(newValue);
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
                case 1:
                    int nval = this.nanos;
                    return newValue != ((long) nval) ? create(this.seconds, (int) newValue) : this;
                case 2:
                    int nval2 = (int) newValue;
                    int nval3 = nval2 * 1000;
                    return nval3 != this.nanos ? create(this.seconds, nval3) : this;
                case 3:
                    int nval4 = ((int) newValue) * MediaController.VIDEO_BITRATE_480;
                    return nval4 != this.nanos ? create(this.seconds, nval4) : this;
                case 4:
                    return newValue != this.seconds ? create(newValue, this.nanos) : this;
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        }
        return (Instant) field.adjustInto(this, newValue);
    }

    public Instant truncatedTo(TemporalUnit unit) {
        if (unit == ChronoUnit.NANOS) {
            return this;
        }
        Duration unitDur = unit.getDuration();
        if (unitDur.getSeconds() > 86400) {
            throw new UnsupportedTemporalTypeException("Unit is too large to be used for truncation");
        }
        long dur = unitDur.toNanos();
        if (86400000000000L % dur != 0) {
            throw new UnsupportedTemporalTypeException("Unit must divide into a standard day without remainder");
        }
        long nod = ((this.seconds % 86400) * C.NANOS_PER_SECOND) + this.nanos;
        long result = (nod / dur) * dur;
        return plusNanos(result - nod);
    }

    @Override // j$.time.temporal.Temporal
    public Instant plus(TemporalAmount amountToAdd) {
        return (Instant) amountToAdd.addTo(this);
    }

    /* renamed from: j$.time.Instant$1 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoUnit;

        static {
            int[] iArr = new int[ChronoUnit.values().length];
            $SwitchMap$java$time$temporal$ChronoUnit = iArr;
            try {
                iArr[ChronoUnit.NANOS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MICROS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MILLIS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.SECONDS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MINUTES.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.HOURS.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.HALF_DAYS.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.DAYS.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            int[] iArr2 = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr2;
            try {
                iArr2[ChronoField.NANO_OF_SECOND.ordinal()] = 1;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MICRO_OF_SECOND.ordinal()] = 2;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MILLI_OF_SECOND.ordinal()] = 3;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.INSTANT_SECONDS.ordinal()] = 4;
            } catch (NoSuchFieldError e12) {
            }
        }
    }

    @Override // j$.time.temporal.Temporal
    public Instant plus(long amountToAdd, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
                case 1:
                    return plusNanos(amountToAdd);
                case 2:
                    return plus(amountToAdd / 1000000, (amountToAdd % 1000000) * 1000);
                case 3:
                    return plusMillis(amountToAdd);
                case 4:
                    return plusSeconds(amountToAdd);
                case 5:
                    return plusSeconds(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 60L));
                case 6:
                    return plusSeconds(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 3600L));
                case 7:
                    return plusSeconds(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 43200L));
                case 8:
                    return plusSeconds(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 86400L));
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return (Instant) unit.addTo(this, amountToAdd);
    }

    public Instant plusSeconds(long secondsToAdd) {
        return plus(secondsToAdd, 0L);
    }

    public Instant plusMillis(long millisToAdd) {
        return plus(millisToAdd / 1000, (millisToAdd % 1000) * 1000000);
    }

    public Instant plusNanos(long nanosToAdd) {
        return plus(0L, nanosToAdd);
    }

    private Instant plus(long secondsToAdd, long nanosToAdd) {
        if ((secondsToAdd | nanosToAdd) == 0) {
            return this;
        }
        long epochSec = Clock$OffsetClock$$ExternalSyntheticBackport0.m(this.seconds, secondsToAdd);
        long epochSec2 = Clock$OffsetClock$$ExternalSyntheticBackport0.m(epochSec, nanosToAdd / C.NANOS_PER_SECOND);
        long nanoAdjustment = this.nanos + (nanosToAdd % C.NANOS_PER_SECOND);
        return ofEpochSecond(epochSec2, nanoAdjustment);
    }

    @Override // j$.time.temporal.Temporal
    public Instant minus(TemporalAmount amountToSubtract) {
        return (Instant) amountToSubtract.subtractFrom(this);
    }

    @Override // j$.time.temporal.Temporal
    public Instant minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1L, unit) : plus(-amountToSubtract, unit);
    }

    public Instant minusSeconds(long secondsToSubtract) {
        if (secondsToSubtract == Long.MIN_VALUE) {
            return plusSeconds(Long.MAX_VALUE).plusSeconds(1L);
        }
        return plusSeconds(-secondsToSubtract);
    }

    public Instant minusMillis(long millisToSubtract) {
        if (millisToSubtract == Long.MIN_VALUE) {
            return plusMillis(Long.MAX_VALUE).plusMillis(1L);
        }
        return plusMillis(-millisToSubtract);
    }

    public Instant minusNanos(long nanosToSubtract) {
        if (nanosToSubtract == Long.MIN_VALUE) {
            return plusNanos(Long.MAX_VALUE).plusNanos(1L);
        }
        return plusNanos(-nanosToSubtract);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
    @Override // j$.time.temporal.TemporalAccessor
    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.precision()) {
            return (R) ChronoUnit.NANOS;
        }
        if (temporalQuery == TemporalQueries.chronology() || temporalQuery == TemporalQueries.zoneId() || temporalQuery == TemporalQueries.zone() || temporalQuery == TemporalQueries.offset() || temporalQuery == TemporalQueries.localDate() || temporalQuery == TemporalQueries.localTime()) {
            return null;
        }
        return temporalQuery.queryFrom(this);
    }

    @Override // j$.time.temporal.TemporalAdjuster
    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.INSTANT_SECONDS, this.seconds).with(ChronoField.NANO_OF_SECOND, this.nanos);
    }

    @Override // j$.time.temporal.Temporal
    public long until(Temporal endExclusive, TemporalUnit unit) {
        Instant end = from(endExclusive);
        if (unit instanceof ChronoUnit) {
            ChronoUnit f = (ChronoUnit) unit;
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[f.ordinal()]) {
                case 1:
                    return nanosUntil(end);
                case 2:
                    return nanosUntil(end) / 1000;
                case 3:
                    return Instant$$ExternalSyntheticBackport0.m(end.toEpochMilli(), toEpochMilli());
                case 4:
                    return secondsUntil(end);
                case 5:
                    return secondsUntil(end) / 60;
                case 6:
                    return secondsUntil(end) / 3600;
                case 7:
                    return secondsUntil(end) / 43200;
                case 8:
                    return secondsUntil(end) / 86400;
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return unit.between(this, end);
    }

    private long nanosUntil(Instant end) {
        long secsDiff = Instant$$ExternalSyntheticBackport0.m(end.seconds, this.seconds);
        long totalNanos = Duration$$ExternalSyntheticBackport1.m(secsDiff, C.NANOS_PER_SECOND);
        return Clock$OffsetClock$$ExternalSyntheticBackport0.m(totalNanos, end.nanos - this.nanos);
    }

    private long secondsUntil(Instant end) {
        long secsDiff = Instant$$ExternalSyntheticBackport0.m(end.seconds, this.seconds);
        long nanosDiff = end.nanos - this.nanos;
        if (secsDiff > 0 && nanosDiff < 0) {
            return secsDiff - 1;
        }
        if (secsDiff < 0 && nanosDiff > 0) {
            return secsDiff + 1;
        }
        return secsDiff;
    }

    public OffsetDateTime atOffset(ZoneOffset offset) {
        return OffsetDateTime.ofInstant(this, offset);
    }

    public ZonedDateTime atZone(ZoneId zone) {
        return ZonedDateTime.ofInstant(this, zone);
    }

    public long toEpochMilli() {
        long millis = this.seconds;
        if (millis < 0 && this.nanos > 0) {
            long adjustment = (this.nanos / MediaController.VIDEO_BITRATE_480) - 1000;
            return Clock$OffsetClock$$ExternalSyntheticBackport0.m(Duration$$ExternalSyntheticBackport1.m(millis + 1, 1000L), adjustment);
        }
        return Clock$OffsetClock$$ExternalSyntheticBackport0.m(Duration$$ExternalSyntheticBackport1.m(millis, 1000L), this.nanos / MediaController.VIDEO_BITRATE_480);
    }

    public int compareTo(Instant otherInstant) {
        int cmp = (this.seconds > otherInstant.seconds ? 1 : (this.seconds == otherInstant.seconds ? 0 : -1));
        if (cmp != 0) {
            return cmp;
        }
        return this.nanos - otherInstant.nanos;
    }

    public boolean isAfter(Instant otherInstant) {
        return compareTo(otherInstant) > 0;
    }

    public boolean isBefore(Instant otherInstant) {
        return compareTo(otherInstant) < 0;
    }

    public boolean equals(Object otherInstant) {
        if (this == otherInstant) {
            return true;
        }
        if (!(otherInstant instanceof Instant)) {
            return false;
        }
        Instant other = (Instant) otherInstant;
        return this.seconds == other.seconds && this.nanos == other.nanos;
    }

    public int hashCode() {
        long j = this.seconds;
        return ((int) (j ^ (j >>> 32))) + (this.nanos * 51);
    }

    public String toString() {
        return DateTimeFormatter.ISO_INSTANT.format(this);
    }

    private Object writeReplace() {
        return new Ser((byte) 2, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    public void writeExternal(DataOutput out) {
        out.writeLong(this.seconds);
        out.writeInt(this.nanos);
    }

    public static Instant readExternal(DataInput in) {
        long seconds = in.readLong();
        int nanos = in.readInt();
        return ofEpochSecond(seconds, nanos);
    }
}
