package j$.time.chrono;

import j$.time.Clock$OffsetClock$$ExternalSyntheticBackport0;
import j$.time.Clock$TickClock$$ExternalSyntheticBackport0;
import j$.time.Duration$$ExternalSyntheticBackport0;
import j$.time.Duration$$ExternalSyntheticBackport1;
import j$.time.Instant;
import j$.time.LocalTime;
import j$.time.ZoneId;
import j$.time.ZoneOffset;
import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.ChronoLocalDateTime;
import j$.time.format.DateTimeFormatter;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
/* loaded from: classes2.dex */
public final class ChronoLocalDateTimeImpl<D extends ChronoLocalDate> implements ChronoLocalDateTime<D>, Temporal, TemporalAdjuster, Serializable {
    static final int HOURS_PER_DAY = 24;
    static final long MICROS_PER_DAY = 86400000000L;
    static final long MILLIS_PER_DAY = 86400000;
    static final int MINUTES_PER_DAY = 1440;
    static final int MINUTES_PER_HOUR = 60;
    static final long NANOS_PER_DAY = 86400000000000L;
    static final long NANOS_PER_HOUR = 3600000000000L;
    static final long NANOS_PER_MINUTE = 60000000000L;
    static final long NANOS_PER_SECOND = 1000000000;
    static final int SECONDS_PER_DAY = 86400;
    static final int SECONDS_PER_HOUR = 3600;
    static final int SECONDS_PER_MINUTE = 60;
    private static final long serialVersionUID = 4556003607393004514L;
    private final transient D date;
    private final transient LocalTime time;

    @Override // j$.time.chrono.ChronoLocalDateTime, j$.time.temporal.TemporalAdjuster
    public /* synthetic */ Temporal adjustInto(Temporal temporal) {
        Temporal with;
        with = temporal.with(ChronoField.EPOCH_DAY, toLocalDate().toEpochDay()).with(ChronoField.NANO_OF_DAY, toLocalTime().toNanoOfDay());
        return with;
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public /* synthetic */ int compareTo(ChronoLocalDateTime chronoLocalDateTime) {
        return ChronoLocalDateTime.CC.$default$compareTo((ChronoLocalDateTime) this, chronoLocalDateTime);
    }

    @Override // java.lang.Comparable
    public /* bridge */ /* synthetic */ int compareTo(ChronoLocalDateTime<?> chronoLocalDateTime) {
        int compareTo;
        compareTo = compareTo((ChronoLocalDateTime) chronoLocalDateTime);
        return compareTo;
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public /* synthetic */ String format(DateTimeFormatter dateTimeFormatter) {
        return Objects.requireNonNull(dateTimeFormatter, "formatter");
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public /* synthetic */ Chronology getChronology() {
        Chronology chronology;
        chronology = toLocalDate().getChronology();
        return chronology;
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public /* synthetic */ boolean isAfter(ChronoLocalDateTime chronoLocalDateTime) {
        return ChronoLocalDateTime.CC.$default$isAfter(this, chronoLocalDateTime);
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public /* synthetic */ boolean isBefore(ChronoLocalDateTime chronoLocalDateTime) {
        return ChronoLocalDateTime.CC.$default$isBefore(this, chronoLocalDateTime);
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public /* synthetic */ boolean isEqual(ChronoLocalDateTime chronoLocalDateTime) {
        return ChronoLocalDateTime.CC.$default$isEqual(this, chronoLocalDateTime);
    }

    @Override // j$.time.chrono.ChronoLocalDateTime, j$.time.temporal.Temporal
    public /* synthetic */ boolean isSupported(TemporalUnit temporalUnit) {
        return ChronoLocalDateTime.CC.$default$isSupported(this, temporalUnit);
    }

    @Override // j$.time.chrono.ChronoLocalDateTime, j$.time.temporal.TemporalAccessor
    public /* synthetic */ Object query(TemporalQuery temporalQuery) {
        return ChronoLocalDateTime.CC.$default$query(this, temporalQuery);
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public /* synthetic */ long toEpochSecond(ZoneOffset zoneOffset) {
        return ChronoLocalDateTime.CC.$default$toEpochSecond(this, zoneOffset);
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public /* synthetic */ Instant toInstant(ZoneOffset zoneOffset) {
        Instant ofEpochSecond;
        ofEpochSecond = Instant.ofEpochSecond(toEpochSecond(zoneOffset), toLocalTime().getNano());
        return ofEpochSecond;
    }

    public static <R extends ChronoLocalDate> ChronoLocalDateTimeImpl<R> of(R date, LocalTime time) {
        return new ChronoLocalDateTimeImpl<>(date, time);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<R extends j$.time.chrono.ChronoLocalDate> */
    public static <R extends ChronoLocalDate> ChronoLocalDateTimeImpl<R> ensureValid(Chronology chrono, Temporal temporal) {
        ChronoLocalDateTimeImpl<R> chronoLocalDateTimeImpl = (ChronoLocalDateTimeImpl) temporal;
        if (!chrono.equals(chronoLocalDateTimeImpl.getChronology())) {
            throw new ClassCastException("Chronology mismatch, required: " + chrono.getId() + ", actual: " + chronoLocalDateTimeImpl.getChronology().getId());
        }
        return chronoLocalDateTimeImpl;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    private ChronoLocalDateTimeImpl(D date, LocalTime time) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(time, "time");
        this.date = date;
        this.time = time;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    private ChronoLocalDateTimeImpl<D> with(Temporal newDate, LocalTime newTime) {
        D d = this.date;
        if (d == newDate && this.time == newTime) {
            return this;
        }
        return new ChronoLocalDateTimeImpl<>(ChronoLocalDateImpl.ensureValid(d.getChronology(), newDate), newTime);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDateTime
    public D toLocalDate() {
        return this.date;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDateTime
    public LocalTime toLocalTime() {
        return this.time;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDateTime, j$.time.temporal.TemporalAccessor
    public boolean isSupported(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field != null && field.isSupportedBy(this);
        }
        ChronoField f = (ChronoField) field;
        return f.isDateBased() || f.isTimeBased();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.temporal.TemporalAccessor
    public ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return f.isTimeBased() ? this.time.range(field) : this.date.range(field);
        }
        return field.rangeRefinedBy(this);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.temporal.TemporalAccessor
    public int get(TemporalField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return f.isTimeBased() ? this.time.get(field) : this.date.get(field);
        }
        return range(field).checkValidIntValue(getLong(field), field);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.temporal.TemporalAccessor
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return f.isTimeBased() ? this.time.getLong(field) : this.date.getLong(field);
        }
        return field.getFrom(this);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDateTime, j$.time.temporal.Temporal
    public ChronoLocalDateTimeImpl<D> with(TemporalAdjuster adjuster) {
        if (adjuster instanceof ChronoLocalDate) {
            return with((ChronoLocalDate) adjuster, this.time);
        }
        if (adjuster instanceof LocalTime) {
            return with(this.date, (LocalTime) adjuster);
        }
        if (adjuster instanceof ChronoLocalDateTimeImpl) {
            return ensureValid(this.date.getChronology(), (ChronoLocalDateTimeImpl) adjuster);
        }
        return ensureValid(this.date.getChronology(), (ChronoLocalDateTimeImpl) adjuster.adjustInto(this));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDateTime, j$.time.temporal.Temporal
    public ChronoLocalDateTimeImpl<D> with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            if (f.isTimeBased()) {
                return with(this.date, this.time.with(field, newValue));
            }
            return with(this.date.with(field, newValue), this.time);
        }
        return ensureValid(this.date.getChronology(), field.adjustInto(this, newValue));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDateTime, j$.time.temporal.Temporal
    public ChronoLocalDateTimeImpl<D> plus(long amountToAdd, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            ChronoUnit f = (ChronoUnit) unit;
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[f.ordinal()]) {
                case 1:
                    return plusNanos(amountToAdd);
                case 2:
                    return plusDays(amountToAdd / MICROS_PER_DAY).plusNanos((amountToAdd % MICROS_PER_DAY) * 1000);
                case 3:
                    return plusDays(amountToAdd / MILLIS_PER_DAY).plusNanos((amountToAdd % MILLIS_PER_DAY) * 1000000);
                case 4:
                    return plusSeconds(amountToAdd);
                case 5:
                    return plusMinutes(amountToAdd);
                case 6:
                    return plusHours(amountToAdd);
                case 7:
                    return plusDays(amountToAdd / 256).plusHours((amountToAdd % 256) * 12);
                default:
                    return with(this.date.plus(amountToAdd, unit), this.time);
            }
        }
        return ensureValid(this.date.getChronology(), unit.addTo(this, amountToAdd));
    }

    /* renamed from: j$.time.chrono.ChronoLocalDateTimeImpl$1 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
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
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    private ChronoLocalDateTimeImpl<D> plusDays(long days) {
        return with(this.date.plus(days, ChronoUnit.DAYS), this.time);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    private ChronoLocalDateTimeImpl<D> plusHours(long hours) {
        return plusWithOverflow(this.date, hours, 0L, 0L, 0L);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    private ChronoLocalDateTimeImpl<D> plusMinutes(long minutes) {
        return plusWithOverflow(this.date, 0L, minutes, 0L, 0L);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    public ChronoLocalDateTimeImpl<D> plusSeconds(long seconds) {
        return plusWithOverflow(this.date, 0L, 0L, seconds, 0L);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    private ChronoLocalDateTimeImpl<D> plusNanos(long nanos) {
        return plusWithOverflow(this.date, 0L, 0L, 0L, nanos);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    private ChronoLocalDateTimeImpl<D> plusWithOverflow(D newDate, long hours, long minutes, long seconds, long nanos) {
        if ((hours | minutes | seconds | nanos) == 0) {
            return with(newDate, this.time);
        }
        long totDays = (nanos / NANOS_PER_DAY) + (seconds / 86400) + (minutes / 1440) + (hours / 24);
        long totNanos = (nanos % NANOS_PER_DAY) + ((seconds % 86400) * 1000000000) + ((minutes % 1440) * NANOS_PER_MINUTE) + ((hours % 24) * NANOS_PER_HOUR);
        long curNoD = this.time.toNanoOfDay();
        long totNanos2 = totNanos + curNoD;
        long totDays2 = totDays + Duration$$ExternalSyntheticBackport0.m(totNanos2, NANOS_PER_DAY);
        long newNoD = Clock$TickClock$$ExternalSyntheticBackport0.m(totNanos2, NANOS_PER_DAY);
        LocalTime newTime = newNoD == curNoD ? this.time : LocalTime.ofNanoOfDay(newNoD);
        return with(newDate.plus(totDays2, ChronoUnit.DAYS), newTime);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDateTime
    public ChronoZonedDateTime<D> atZone(ZoneId zone) {
        return ChronoZonedDateTimeImpl.ofBest(this, zone, null);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTime != java.time.chrono.ChronoLocalDateTime<D extends j$.time.chrono.ChronoLocalDate> */
    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.temporal.Temporal
    public long until(Temporal endExclusive, TemporalUnit unit) {
        Objects.requireNonNull(endExclusive, "endExclusive");
        ChronoLocalDateTime<? extends ChronoLocalDate> localDateTime = getChronology().localDateTime(endExclusive);
        if (unit instanceof ChronoUnit) {
            if (unit.isTimeBased()) {
                long amount = localDateTime.getLong(ChronoField.EPOCH_DAY) - this.date.getLong(ChronoField.EPOCH_DAY);
                switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
                    case 1:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, NANOS_PER_DAY);
                        break;
                    case 2:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, MICROS_PER_DAY);
                        break;
                    case 3:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, MILLIS_PER_DAY);
                        break;
                    case 4:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, 86400L);
                        break;
                    case 5:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, 1440L);
                        break;
                    case 6:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, 24L);
                        break;
                    case 7:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, 2L);
                        break;
                }
                return Clock$OffsetClock$$ExternalSyntheticBackport0.m(amount, this.time.until(localDateTime.toLocalTime(), unit));
            }
            ChronoLocalDate endDate = localDateTime.toLocalDate();
            if (localDateTime.toLocalTime().isBefore(this.time)) {
                endDate = endDate.minus(1L, (TemporalUnit) ChronoUnit.DAYS);
            }
            return this.date.until(endDate, unit);
        }
        Objects.requireNonNull(unit, "unit");
        return unit.between(this, localDateTime);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    private Object writeReplace() {
        return new Ser((byte) 2, this);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    public void writeExternal(ObjectOutput out) {
        out.writeObject(this.date);
        out.writeObject(this.time);
    }

    public static ChronoLocalDateTime<?> readExternal(ObjectInput in) {
        ChronoLocalDate date = (ChronoLocalDate) in.readObject();
        LocalTime time = (LocalTime) in.readObject();
        return date.atTime(time);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDateTime
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof ChronoLocalDateTime) && compareTo((ChronoLocalDateTime) obj) == 0;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDateTime
    public int hashCode() {
        return toLocalDate().hashCode() ^ toLocalTime().hashCode();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDateTime
    public String toString() {
        return toLocalDate().toString() + 'T' + toLocalTime().toString();
    }
}
