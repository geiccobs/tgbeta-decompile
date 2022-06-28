package j$.time.chrono;

import j$.time.Clock;
import j$.time.Clock$TickClock$$ExternalSyntheticBackport0;
import j$.time.Duration$$ExternalSyntheticBackport0;
import j$.time.LocalDate;
import j$.time.LocalDate$$ExternalSyntheticBackport0;
import j$.time.LocalTime;
import j$.time.Period$$ExternalSyntheticBackport0;
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
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
/* loaded from: classes2.dex */
public final class HijrahDate extends ChronoLocalDateImpl<HijrahDate> implements ChronoLocalDate, Serializable {
    private static final long serialVersionUID = -5207853542612002020L;
    private final transient HijrahChronology chrono;
    private final transient int dayOfMonth;
    private final transient int monthOfYear;
    private final transient int prolepticYear;

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public /* bridge */ /* synthetic */ String toString() {
        return super.toString();
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public /* bridge */ /* synthetic */ long until(Temporal temporal, TemporalUnit temporalUnit) {
        return super.until(temporal, temporalUnit);
    }

    public static HijrahDate of(HijrahChronology chrono, int prolepticYear, int monthOfYear, int dayOfMonth) {
        return new HijrahDate(chrono, prolepticYear, monthOfYear, dayOfMonth);
    }

    public static HijrahDate ofEpochDay(HijrahChronology chrono, long epochDay) {
        return new HijrahDate(chrono, epochDay);
    }

    public static HijrahDate now() {
        return now(Clock.systemDefaultZone());
    }

    public static HijrahDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static HijrahDate now(Clock clock) {
        return ofEpochDay(HijrahChronology.INSTANCE, LocalDate.now(clock).toEpochDay());
    }

    public static HijrahDate of(int prolepticYear, int month, int dayOfMonth) {
        return HijrahChronology.INSTANCE.date(prolepticYear, month, dayOfMonth);
    }

    public static HijrahDate from(TemporalAccessor temporal) {
        return HijrahChronology.INSTANCE.date(temporal);
    }

    private HijrahDate(HijrahChronology chrono, int prolepticYear, int monthOfYear, int dayOfMonth) {
        chrono.getEpochDay(prolepticYear, monthOfYear, dayOfMonth);
        this.chrono = chrono;
        this.prolepticYear = prolepticYear;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
    }

    private HijrahDate(HijrahChronology chrono, long epochDay) {
        int[] dateInfo = chrono.getHijrahDateInfo((int) epochDay);
        this.chrono = chrono;
        this.prolepticYear = dateInfo[0];
        this.monthOfYear = dateInfo[1];
        this.dayOfMonth = dateInfo[2];
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public HijrahChronology getChronology() {
        return this.chrono;
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public HijrahEra getEra() {
        return HijrahEra.AH;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public int lengthOfMonth() {
        return this.chrono.getMonthLength(this.prolepticYear, this.monthOfYear);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public int lengthOfYear() {
        return this.chrono.getYearLength(this.prolepticYear);
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
                        return ValueRange.of(1L, 5L);
                    default:
                        return getChronology().range(f);
                }
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.rangeRefinedBy(this);
    }

    /* renamed from: j$.time.chrono.HijrahDate$1 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;

        static {
            int[] iArr = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr;
            try {
                iArr[ChronoField.DAY_OF_MONTH.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.DAY_OF_YEAR.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_WEEK_OF_MONTH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.DAY_OF_WEEK.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.EPOCH_DAY.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_WEEK_OF_YEAR.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MONTH_OF_YEAR.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.PROLEPTIC_MONTH.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR_OF_ERA.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ERA.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
        }
    }

    @Override // j$.time.temporal.TemporalAccessor
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
                case 1:
                    return this.dayOfMonth;
                case 2:
                    return getDayOfYear();
                case 3:
                    return ((this.dayOfMonth - 1) / 7) + 1;
                case 4:
                    return getDayOfWeek();
                case 5:
                    return ((getDayOfWeek() - 1) % 7) + 1;
                case 6:
                    return ((getDayOfYear() - 1) % 7) + 1;
                case 7:
                    return toEpochDay();
                case 8:
                    return ((getDayOfYear() - 1) / 7) + 1;
                case 9:
                    return this.monthOfYear;
                case 10:
                    return getProlepticMonth();
                case 11:
                    return this.prolepticYear;
                case 12:
                    return this.prolepticYear;
                case 13:
                    return getEraValue();
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        }
        return field.getFrom(this);
    }

    private long getProlepticMonth() {
        return ((this.prolepticYear * 12) + this.monthOfYear) - 1;
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public HijrahDate with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            this.chrono.range(f).checkValidValue(newValue, f);
            int nvalue = (int) newValue;
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
                case 1:
                    return resolvePreviousValid(this.prolepticYear, this.monthOfYear, nvalue);
                case 2:
                    return plusDays(Math.min(nvalue, lengthOfYear()) - getDayOfYear());
                case 3:
                    return plusDays((newValue - getLong(ChronoField.ALIGNED_WEEK_OF_MONTH)) * 7);
                case 4:
                    return plusDays(newValue - getDayOfWeek());
                case 5:
                    return plusDays(newValue - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
                case 6:
                    return plusDays(newValue - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
                case 7:
                    return new HijrahDate(this.chrono, newValue);
                case 8:
                    return plusDays((newValue - getLong(ChronoField.ALIGNED_WEEK_OF_YEAR)) * 7);
                case 9:
                    return resolvePreviousValid(this.prolepticYear, nvalue, this.dayOfMonth);
                case 10:
                    return plusMonths(newValue - getProlepticMonth());
                case 11:
                    return resolvePreviousValid(this.prolepticYear >= 1 ? nvalue : 1 - nvalue, this.monthOfYear, this.dayOfMonth);
                case 12:
                    return resolvePreviousValid(nvalue, this.monthOfYear, this.dayOfMonth);
                case 13:
                    return resolvePreviousValid(1 - this.prolepticYear, this.monthOfYear, this.dayOfMonth);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        }
        return (HijrahDate) super.with(field, newValue);
    }

    private HijrahDate resolvePreviousValid(int prolepticYear, int month, int day) {
        int monthDays = this.chrono.getMonthLength(prolepticYear, month);
        if (day > monthDays) {
            day = monthDays;
        }
        return of(this.chrono, prolepticYear, month, day);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public HijrahDate with(TemporalAdjuster adjuster) {
        return (HijrahDate) super.with(adjuster);
    }

    public HijrahDate withVariant(HijrahChronology chronology) {
        if (this.chrono == chronology) {
            return this;
        }
        int monthDays = chronology.getDayOfYear(this.prolepticYear, this.monthOfYear);
        int i = this.prolepticYear;
        int i2 = this.monthOfYear;
        int i3 = this.dayOfMonth;
        if (i3 > monthDays) {
            i3 = monthDays;
        }
        return of(chronology, i, i2, i3);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public HijrahDate plus(TemporalAmount amount) {
        return (HijrahDate) super.plus(amount);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public HijrahDate minus(TemporalAmount amount) {
        return (HijrahDate) super.minus(amount);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public long toEpochDay() {
        return this.chrono.getEpochDay(this.prolepticYear, this.monthOfYear, this.dayOfMonth);
    }

    private int getDayOfYear() {
        return this.chrono.getDayOfYear(this.prolepticYear, this.monthOfYear) + this.dayOfMonth;
    }

    private int getDayOfWeek() {
        int dow0 = (int) Clock$TickClock$$ExternalSyntheticBackport0.m(toEpochDay() + 3, 7L);
        return dow0 + 1;
    }

    private int getEraValue() {
        return this.prolepticYear > 1 ? 1 : 0;
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public boolean isLeapYear() {
        return this.chrono.isLeapYear(this.prolepticYear);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public HijrahDate plusYears(long years) {
        if (years == 0) {
            return this;
        }
        int newYear = Period$$ExternalSyntheticBackport0.m(this.prolepticYear, (int) years);
        return resolvePreviousValid(newYear, this.monthOfYear, this.dayOfMonth);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public HijrahDate plusMonths(long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }
        long monthCount = (this.prolepticYear * 12) + (this.monthOfYear - 1);
        long calcMonths = monthCount + monthsToAdd;
        int newYear = this.chrono.checkValidYear(Duration$$ExternalSyntheticBackport0.m(calcMonths, 12L));
        int newMonth = ((int) Clock$TickClock$$ExternalSyntheticBackport0.m(calcMonths, 12L)) + 1;
        return resolvePreviousValid(newYear, newMonth, this.dayOfMonth);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public HijrahDate plusWeeks(long weeksToAdd) {
        return (HijrahDate) super.plusWeeks(weeksToAdd);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public HijrahDate plusDays(long days) {
        return new HijrahDate(this.chrono, toEpochDay() + days);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public HijrahDate plus(long amountToAdd, TemporalUnit unit) {
        return (HijrahDate) super.plus(amountToAdd, unit);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public HijrahDate minus(long amountToSubtract, TemporalUnit unit) {
        return (HijrahDate) super.minus(amountToSubtract, unit);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public HijrahDate minusYears(long yearsToSubtract) {
        return (HijrahDate) super.minusYears(yearsToSubtract);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public HijrahDate minusMonths(long monthsToSubtract) {
        return (HijrahDate) super.minusMonths(monthsToSubtract);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public HijrahDate minusWeeks(long weeksToSubtract) {
        return (HijrahDate) super.minusWeeks(weeksToSubtract);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl
    public HijrahDate minusDays(long daysToSubtract) {
        return (HijrahDate) super.minusDays(daysToSubtract);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public final ChronoLocalDateTime<HijrahDate> atTime(LocalTime localTime) {
        return super.atTime(localTime);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public ChronoPeriod until(ChronoLocalDate endDate) {
        HijrahDate end = getChronology().date((TemporalAccessor) endDate);
        long totalMonths = ((end.prolepticYear - this.prolepticYear) * 12) + (end.monthOfYear - this.monthOfYear);
        int days = end.dayOfMonth - this.dayOfMonth;
        if (totalMonths > 0 && days < 0) {
            totalMonths--;
            HijrahDate calcDate = plusMonths(totalMonths);
            days = (int) (end.toEpochDay() - calcDate.toEpochDay());
        } else if (totalMonths < 0 && days > 0) {
            totalMonths++;
            days -= end.lengthOfMonth();
        }
        long years = totalMonths / 12;
        int months = (int) (totalMonths % 12);
        return getChronology().period(LocalDate$$ExternalSyntheticBackport0.m(years), months, days);
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof HijrahDate)) {
            return false;
        }
        HijrahDate otherDate = (HijrahDate) obj;
        return this.prolepticYear == otherDate.prolepticYear && this.monthOfYear == otherDate.monthOfYear && this.dayOfMonth == otherDate.dayOfMonth && getChronology().equals(otherDate.getChronology());
    }

    @Override // j$.time.chrono.ChronoLocalDateImpl, j$.time.chrono.ChronoLocalDate
    public int hashCode() {
        int yearValue = this.prolepticYear;
        int monthValue = this.monthOfYear;
        int dayValue = this.dayOfMonth;
        return (getChronology().getId().hashCode() ^ (yearValue & (-2048))) ^ (((yearValue << 11) + (monthValue << 6)) + dayValue);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    private Object writeReplace() {
        return new Ser((byte) 6, this);
    }

    public void writeExternal(ObjectOutput out) {
        out.writeObject(getChronology());
        out.writeInt(get(ChronoField.YEAR));
        out.writeByte(get(ChronoField.MONTH_OF_YEAR));
        out.writeByte(get(ChronoField.DAY_OF_MONTH));
    }

    public static HijrahDate readExternal(ObjectInput in) {
        HijrahChronology chrono = (HijrahChronology) in.readObject();
        int year = in.readInt();
        int month = in.readByte();
        int dayOfMonth = in.readByte();
        return chrono.date(year, month, dayOfMonth);
    }
}
