package j$.time.chrono;

import j$.time.Clock;
import j$.time.LocalDate;
import j$.time.LocalTime;
import j$.time.Period;
import j$.time.ZoneId;
import j$.time.temporal.ChronoField;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
/* loaded from: classes2.dex */
public final class ThaiBuddhistDate extends ChronoLocalDateImpl<ThaiBuddhistDate> implements ChronoLocalDate, Serializable {
    private static final long serialVersionUID = -8722293800195731463L;
    private final transient LocalDate isoDate;

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public /* bridge */ /* synthetic */ long until(Temporal temporal, TemporalUnit temporalUnit) {
        return super.until(temporal, temporalUnit);
    }

    public static ThaiBuddhistDate now() {
        return now(Clock.systemDefaultZone());
    }

    public static ThaiBuddhistDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static ThaiBuddhistDate now(Clock clock) {
        return new ThaiBuddhistDate(LocalDate.now(clock));
    }

    public static ThaiBuddhistDate of(int prolepticYear, int month, int dayOfMonth) {
        return new ThaiBuddhistDate(LocalDate.of(prolepticYear - 543, month, dayOfMonth));
    }

    public static ThaiBuddhistDate from(TemporalAccessor temporal) {
        return ThaiBuddhistChronology.INSTANCE.date(temporal);
    }

    public ThaiBuddhistDate(LocalDate isoDate) {
        Objects.requireNonNull(isoDate, "isoDate");
        this.isoDate = isoDate;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public ThaiBuddhistChronology getChronology() {
        return ThaiBuddhistChronology.INSTANCE;
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public ThaiBuddhistEra getEra() {
        return getProlepticYear() >= 1 ? ThaiBuddhistEra.BE : ThaiBuddhistEra.BEFORE_BE;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public int lengthOfMonth() {
        return this.isoDate.lengthOfMonth();
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.temporal.TemporalAccessor
    public ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            if (isSupported(field)) {
                ChronoField f = (ChronoField) field;
                switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
                    case 1:
                    case 2:
                    case 3:
                        return this.isoDate.range(field);
                    case 4:
                        ValueRange range = ChronoField.YEAR.range();
                        long max = getProlepticYear() <= 0 ? (-(range.getMinimum() + 543)) + 1 : 543 + range.getMaximum();
                        return ValueRange.of(1L, max);
                    default:
                        return getChronology().range(f);
                }
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.rangeRefinedBy(this);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            int i = 1;
            switch ((ChronoField) field) {
                case YEAR_OF_ERA:
                    int prolepticYear = getProlepticYear();
                    return prolepticYear >= 1 ? prolepticYear : 1 - prolepticYear;
                case PROLEPTIC_MONTH:
                    return getProlepticMonth();
                case YEAR:
                    return getProlepticYear();
                case ERA:
                    if (getProlepticYear() < 1) {
                        i = 0;
                    }
                    return i;
                default:
                    return this.isoDate.getLong(field);
            }
        }
        return field.getFrom(this);
    }

    private long getProlepticMonth() {
        return ((getProlepticYear() * 12) + this.isoDate.getMonthValue()) - 1;
    }

    private int getProlepticYear() {
        return this.isoDate.getYear() + 543;
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public ThaiBuddhistDate with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            if (getLong(f) == newValue) {
                return this;
            }
            switch (f) {
                case PROLEPTIC_MONTH:
                    getChronology().range(f).checkValidValue(newValue, f);
                    return plusMonths(newValue - getProlepticMonth());
                case YEAR_OF_ERA:
                case YEAR:
                case ERA:
                    int nvalue = getChronology().range(f).checkValidIntValue(newValue, f);
                    switch (f) {
                        case YEAR_OF_ERA:
                            return with(this.isoDate.withYear((getProlepticYear() >= 1 ? nvalue : 1 - nvalue) - 543));
                        case YEAR:
                            return with(this.isoDate.withYear(nvalue - 543));
                        case ERA:
                            return with(this.isoDate.withYear((1 - getProlepticYear()) - 543));
                    }
            }
            return with(this.isoDate.with(field, newValue));
        }
        return (ThaiBuddhistDate) super.with(field, newValue);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public ThaiBuddhistDate with(TemporalAdjuster adjuster) {
        return (ThaiBuddhistDate) super.with(adjuster);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public ThaiBuddhistDate plus(TemporalAmount amount) {
        return (ThaiBuddhistDate) super.plus(amount);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public ThaiBuddhistDate minus(TemporalAmount amount) {
        return (ThaiBuddhistDate) super.minus(amount);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public ThaiBuddhistDate plusYears(long years) {
        return with(this.isoDate.plusYears(years));
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public ThaiBuddhistDate plusMonths(long months) {
        return with(this.isoDate.plusMonths(months));
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public ThaiBuddhistDate plusWeeks(long weeksToAdd) {
        return (ThaiBuddhistDate) super.plusWeeks(weeksToAdd);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public ThaiBuddhistDate plusDays(long days) {
        return with(this.isoDate.plusDays(days));
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public ThaiBuddhistDate plus(long amountToAdd, TemporalUnit unit) {
        return (ThaiBuddhistDate) super.plus(amountToAdd, unit);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public ThaiBuddhistDate minus(long amountToAdd, TemporalUnit unit) {
        return (ThaiBuddhistDate) super.minus(amountToAdd, unit);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public ThaiBuddhistDate minusYears(long yearsToSubtract) {
        return (ThaiBuddhistDate) super.minusYears(yearsToSubtract);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public ThaiBuddhistDate minusMonths(long monthsToSubtract) {
        return (ThaiBuddhistDate) super.minusMonths(monthsToSubtract);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public ThaiBuddhistDate minusWeeks(long weeksToSubtract) {
        return (ThaiBuddhistDate) super.minusWeeks(weeksToSubtract);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public ThaiBuddhistDate minusDays(long daysToSubtract) {
        return (ThaiBuddhistDate) super.minusDays(daysToSubtract);
    }

    private ThaiBuddhistDate with(LocalDate newDate) {
        return newDate.equals(this.isoDate) ? this : new ThaiBuddhistDate(newDate);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public final ChronoLocalDateTime<ThaiBuddhistDate> atTime(LocalTime localTime) {
        return super.atTime(localTime);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public ChronoPeriod until(ChronoLocalDate endDate) {
        Period period = this.isoDate.until(endDate);
        return getChronology().period(period.getYears(), period.getMonths(), period.getDays());
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public long toEpochDay() {
        return this.isoDate.toEpochDay();
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ThaiBuddhistDate) {
            ThaiBuddhistDate otherDate = (ThaiBuddhistDate) obj;
            return this.isoDate.equals(otherDate.isoDate);
        }
        return false;
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public int hashCode() {
        return getChronology().getId().hashCode() ^ this.isoDate.hashCode();
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new Ser((byte) 8, this);
    }

    public void writeExternal(DataOutput out) {
        out.writeInt(get(ChronoField.YEAR));
        out.writeByte(get(ChronoField.MONTH_OF_YEAR));
        out.writeByte(get(ChronoField.DAY_OF_MONTH));
    }

    public static ThaiBuddhistDate readExternal(DataInput in) {
        int year = in.readInt();
        int month = in.readByte();
        int dayOfMonth = in.readByte();
        return ThaiBuddhistChronology.INSTANCE.date(year, month, dayOfMonth);
    }
}
