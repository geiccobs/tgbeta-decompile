package j$.time.chrono;

import j$.time.Clock;
import j$.time.DateTimeException;
import j$.time.LocalDate;
import j$.time.LocalTime;
import j$.time.Period;
import j$.time.Year;
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
public final class JapaneseDate extends ChronoLocalDateImpl<JapaneseDate> implements ChronoLocalDate, Serializable {
    static final LocalDate MEIJI_6_ISODATE = LocalDate.of(1873, 1, 1);
    private static final long serialVersionUID = -305327627230580483L;
    private transient JapaneseEra era;
    private final transient LocalDate isoDate;
    private transient int yearOfEra;

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public /* bridge */ /* synthetic */ long until(Temporal temporal, TemporalUnit temporalUnit) {
        return super.until(temporal, temporalUnit);
    }

    public static JapaneseDate now() {
        return now(Clock.systemDefaultZone());
    }

    public static JapaneseDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static JapaneseDate now(Clock clock) {
        return new JapaneseDate(LocalDate.now(clock));
    }

    public static JapaneseDate of(JapaneseEra era, int yearOfEra, int month, int dayOfMonth) {
        Objects.requireNonNull(era, "era");
        LocalDate date = LocalDate.of((era.getSince().getYear() + yearOfEra) - 1, month, dayOfMonth);
        if (date.isBefore(era.getSince()) || era != JapaneseEra.from(date)) {
            throw new DateTimeException("year, month, and day not valid for Era");
        }
        return new JapaneseDate(era, yearOfEra, date);
    }

    public static JapaneseDate of(int prolepticYear, int month, int dayOfMonth) {
        return new JapaneseDate(LocalDate.of(prolepticYear, month, dayOfMonth));
    }

    public static JapaneseDate ofYearDay(JapaneseEra era, int yearOfEra, int dayOfYear) {
        LocalDate localdate;
        Objects.requireNonNull(era, "era");
        if (yearOfEra != 1) {
            localdate = LocalDate.ofYearDay((era.getSince().getYear() + yearOfEra) - 1, dayOfYear);
        } else {
            localdate = LocalDate.ofYearDay(era.getSince().getYear(), (era.getSince().getDayOfYear() + dayOfYear) - 1);
        }
        if (localdate.isBefore(era.getSince()) || era != JapaneseEra.from(localdate)) {
            throw new DateTimeException("Invalid parameters");
        }
        return new JapaneseDate(era, yearOfEra, localdate);
    }

    public static JapaneseDate from(TemporalAccessor temporal) {
        return JapaneseChronology.INSTANCE.date(temporal);
    }

    public JapaneseDate(LocalDate isoDate) {
        if (isoDate.isBefore(MEIJI_6_ISODATE)) {
            throw new DateTimeException("JapaneseDate before Meiji 6 is not supported");
        }
        this.era = JapaneseEra.from(isoDate);
        this.yearOfEra = (isoDate.getYear() - this.era.getSince().getYear()) + 1;
        this.isoDate = isoDate;
    }

    JapaneseDate(JapaneseEra era, int year, LocalDate isoDate) {
        if (isoDate.isBefore(MEIJI_6_ISODATE)) {
            throw new DateTimeException("JapaneseDate before Meiji 6 is not supported");
        }
        this.era = era;
        this.yearOfEra = year;
        this.isoDate = isoDate;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public JapaneseChronology getChronology() {
        return JapaneseChronology.INSTANCE;
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public JapaneseEra getEra() {
        return this.era;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public int lengthOfMonth() {
        return this.isoDate.lengthOfMonth();
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public int lengthOfYear() {
        int remaining;
        JapaneseEra nextEra = this.era.next();
        if (nextEra != null && nextEra.getSince().getYear() == this.isoDate.getYear()) {
            remaining = nextEra.getSince().getDayOfYear() - 1;
        } else {
            remaining = this.isoDate.lengthOfYear();
        }
        if (this.yearOfEra == 1) {
            return remaining - (this.era.getSince().getDayOfYear() - 1);
        }
        return remaining;
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.TemporalAccessor
    public boolean isSupported(TemporalField field) {
        if (field == ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH || field == ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR || field == ChronoField.ALIGNED_WEEK_OF_MONTH || field == ChronoField.ALIGNED_WEEK_OF_YEAR) {
            return false;
        }
        if (field instanceof ChronoField) {
            return field.isDateBased();
        }
        return field != null && field.isSupportedBy(this);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.temporal.TemporalAccessor
    public ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            if (isSupported(field)) {
                ChronoField f = (ChronoField) field;
                switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
                    case 1:
                        return ValueRange.of(1L, lengthOfMonth());
                    case 2:
                        return ValueRange.of(1L, lengthOfYear());
                    case 3:
                        int startYear = this.era.getSince().getYear();
                        JapaneseEra nextEra = this.era.next();
                        if (nextEra != null) {
                            return ValueRange.of(1L, (nextEra.getSince().getYear() - startYear) + 1);
                        }
                        return ValueRange.of(1L, Year.MAX_VALUE - startYear);
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
            switch ((ChronoField) field) {
                case DAY_OF_YEAR:
                    if (this.yearOfEra == 1) {
                        return (this.isoDate.getDayOfYear() - this.era.getSince().getDayOfYear()) + 1;
                    }
                    return this.isoDate.getDayOfYear();
                case YEAR_OF_ERA:
                    return this.yearOfEra;
                case ALIGNED_DAY_OF_WEEK_IN_MONTH:
                case ALIGNED_DAY_OF_WEEK_IN_YEAR:
                case ALIGNED_WEEK_OF_MONTH:
                case ALIGNED_WEEK_OF_YEAR:
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
                case ERA:
                    return this.era.getValue();
                default:
                    return this.isoDate.getLong(field);
            }
        }
        return field.getFrom(this);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public JapaneseDate with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            if (getLong(f) == newValue) {
                return this;
            }
            switch (f) {
                case YEAR_OF_ERA:
                case ERA:
                case YEAR:
                    int nvalue = getChronology().range(f).checkValidIntValue(newValue, f);
                    switch (f) {
                        case YEAR_OF_ERA:
                            return withYear(nvalue);
                        case ERA:
                            return withYear(JapaneseEra.of(nvalue), this.yearOfEra);
                        case YEAR:
                            return with(this.isoDate.withYear(nvalue));
                    }
            }
            return with(this.isoDate.with(field, newValue));
        }
        return (JapaneseDate) super.with(field, newValue);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public JapaneseDate with(TemporalAdjuster adjuster) {
        return (JapaneseDate) super.with(adjuster);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public JapaneseDate plus(TemporalAmount amount) {
        return (JapaneseDate) super.plus(amount);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public JapaneseDate minus(TemporalAmount amount) {
        return (JapaneseDate) super.minus(amount);
    }

    private JapaneseDate withYear(JapaneseEra era, int yearOfEra) {
        int year = JapaneseChronology.INSTANCE.prolepticYear(era, yearOfEra);
        return with(this.isoDate.withYear(year));
    }

    private JapaneseDate withYear(int year) {
        return withYear(getEra(), year);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public JapaneseDate plusYears(long years) {
        return with(this.isoDate.plusYears(years));
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public JapaneseDate plusMonths(long months) {
        return with(this.isoDate.plusMonths(months));
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public JapaneseDate plusWeeks(long weeksToAdd) {
        return with(this.isoDate.plusWeeks(weeksToAdd));
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public JapaneseDate plusDays(long days) {
        return with(this.isoDate.plusDays(days));
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public JapaneseDate plus(long amountToAdd, TemporalUnit unit) {
        return (JapaneseDate) super.plus(amountToAdd, unit);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public JapaneseDate minus(long amountToAdd, TemporalUnit unit) {
        return (JapaneseDate) super.minus(amountToAdd, unit);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public JapaneseDate minusYears(long yearsToSubtract) {
        return (JapaneseDate) super.minusYears(yearsToSubtract);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public JapaneseDate minusMonths(long monthsToSubtract) {
        return (JapaneseDate) super.minusMonths(monthsToSubtract);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public JapaneseDate minusWeeks(long weeksToSubtract) {
        return (JapaneseDate) super.minusWeeks(weeksToSubtract);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public JapaneseDate minusDays(long daysToSubtract) {
        return (JapaneseDate) super.minusDays(daysToSubtract);
    }

    private JapaneseDate with(LocalDate newDate) {
        return newDate.equals(this.isoDate) ? this : new JapaneseDate(newDate);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public final ChronoLocalDateTime<JapaneseDate> atTime(LocalTime localTime) {
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
        if (obj instanceof JapaneseDate) {
            JapaneseDate otherDate = (JapaneseDate) obj;
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
        return new Ser((byte) 4, this);
    }

    public void writeExternal(DataOutput out) {
        out.writeInt(get(ChronoField.YEAR));
        out.writeByte(get(ChronoField.MONTH_OF_YEAR));
        out.writeByte(get(ChronoField.DAY_OF_MONTH));
    }

    public static JapaneseDate readExternal(DataInput in) {
        int year = in.readInt();
        int month = in.readByte();
        int dayOfMonth = in.readByte();
        return JapaneseChronology.INSTANCE.date(year, month, dayOfMonth);
    }
}
