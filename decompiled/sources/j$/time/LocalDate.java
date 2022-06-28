package j$.time;

import com.google.android.exoplayer2.C;
import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.IsoChronology;
import j$.time.chrono.IsoEra;
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
import j$.time.zone.ZoneOffsetTransition;
import j$.time.zone.ZoneRules;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
/* loaded from: classes2.dex */
public final class LocalDate implements Temporal, TemporalAdjuster, ChronoLocalDate, Serializable {
    static final long DAYS_0000_TO_1970 = 719528;
    private static final int DAYS_PER_CYCLE = 146097;
    private static final long serialVersionUID = 2942565459149668126L;
    private final short day;
    private final short month;
    private final int year;
    public static final LocalDate MIN = of((int) Year.MIN_VALUE, 1, 1);
    public static final LocalDate MAX = of((int) Year.MAX_VALUE, 12, 31);

    public static LocalDate now() {
        return now(Clock.systemDefaultZone());
    }

    public static LocalDate now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static LocalDate now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        Instant now = clock.instant();
        ZoneOffset offset = clock.getZone().getRules().getOffset(now);
        long epochSec = now.getEpochSecond() + offset.getTotalSeconds();
        long epochDay = Duration$$ExternalSyntheticBackport0.m(epochSec, 86400L);
        return ofEpochDay(epochDay);
    }

    public static LocalDate of(int year, Month month, int dayOfMonth) {
        ChronoField.YEAR.checkValidValue(year);
        Objects.requireNonNull(month, "month");
        ChronoField.DAY_OF_MONTH.checkValidValue(dayOfMonth);
        return create(year, month.getValue(), dayOfMonth);
    }

    public static LocalDate of(int year, int month, int dayOfMonth) {
        ChronoField.YEAR.checkValidValue(year);
        ChronoField.MONTH_OF_YEAR.checkValidValue(month);
        ChronoField.DAY_OF_MONTH.checkValidValue(dayOfMonth);
        return create(year, month, dayOfMonth);
    }

    public static LocalDate ofYearDay(int year, int dayOfYear) {
        ChronoField.YEAR.checkValidValue(year);
        ChronoField.DAY_OF_YEAR.checkValidValue(dayOfYear);
        boolean leap = IsoChronology.INSTANCE.isLeapYear(year);
        if (dayOfYear == 366 && !leap) {
            throw new DateTimeException("Invalid date 'DayOfYear 366' as '" + year + "' is not a leap year");
        }
        Month moy = Month.of(((dayOfYear - 1) / 31) + 1);
        int monthEnd = (moy.firstDayOfYear(leap) + moy.length(leap)) - 1;
        if (dayOfYear > monthEnd) {
            moy = moy.plus(1L);
        }
        int dom = (dayOfYear - moy.firstDayOfYear(leap)) + 1;
        return new LocalDate(year, moy.getValue(), dom);
    }

    public static LocalDate ofEpochDay(long epochDay) {
        long zeroDay = (epochDay + DAYS_0000_TO_1970) - 60;
        long adjust = 0;
        if (zeroDay < 0) {
            long adjustCycles = ((zeroDay + 1) / 146097) - 1;
            adjust = adjustCycles * 400;
            zeroDay += (-adjustCycles) * 146097;
        }
        long yearEst = ((zeroDay * 400) + 591) / 146097;
        long doyEst = zeroDay - ((((yearEst * 365) + (yearEst / 4)) - (yearEst / 100)) + (yearEst / 400));
        if (doyEst < 0) {
            yearEst--;
            doyEst = zeroDay - ((((365 * yearEst) + (yearEst / 4)) - (yearEst / 100)) + (yearEst / 400));
        }
        int marchDoy0 = (int) doyEst;
        int marchMonth0 = ((marchDoy0 * 5) + 2) / 153;
        int month = ((marchMonth0 + 2) % 12) + 1;
        int dom = (marchDoy0 - (((marchMonth0 * 306) + 5) / 10)) + 1;
        int year = ChronoField.YEAR.checkValidIntValue(yearEst + adjust + (marchMonth0 / 10));
        return new LocalDate(year, month, dom);
    }

    public static LocalDate from(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        LocalDate date = (LocalDate) temporal.query(TemporalQueries.localDate());
        if (date == null) {
            throw new DateTimeException("Unable to obtain LocalDate from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName());
        }
        return date;
    }

    public static LocalDate parse(CharSequence text) {
        return parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static LocalDate parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return (LocalDate) formatter.parse(text, LocalDate$$ExternalSyntheticLambda1.INSTANCE);
    }

    private static LocalDate create(int year, int month, int dayOfMonth) {
        int i = 28;
        if (dayOfMonth > 28) {
            int dom = 31;
            switch (month) {
                case 2:
                    if (IsoChronology.INSTANCE.isLeapYear(year)) {
                        i = 29;
                    }
                    dom = i;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    dom = 30;
                    break;
            }
            if (dayOfMonth > dom) {
                if (dayOfMonth == 29) {
                    throw new DateTimeException("Invalid date 'February 29' as '" + year + "' is not a leap year");
                }
                throw new DateTimeException("Invalid date '" + Month.of(month).name() + " " + dayOfMonth + "'");
            }
        }
        return new LocalDate(year, month, dayOfMonth);
    }

    private static LocalDate resolvePreviousValid(int year, int month, int day) {
        switch (month) {
            case 2:
                day = Math.min(day, IsoChronology.INSTANCE.isLeapYear((long) year) ? 29 : 28);
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                day = Math.min(day, 30);
                break;
        }
        return new LocalDate(year, month, day);
    }

    private LocalDate(int year, int month, int dayOfMonth) {
        this.year = year;
        this.month = (short) month;
        this.day = (short) dayOfMonth;
    }

    @Override // j$.time.temporal.TemporalAccessor
    public boolean isSupported(TemporalField field) {
        return ChronoLocalDate.CC.$default$isSupported(this, field);
    }

    @Override // j$.time.temporal.Temporal
    public boolean isSupported(TemporalUnit unit) {
        return ChronoLocalDate.CC.$default$isSupported(this, unit);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            if (f.isDateBased()) {
                switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
                    case 1:
                        return ValueRange.of(1L, lengthOfMonth());
                    case 2:
                        return ValueRange.of(1L, lengthOfYear());
                    case 3:
                        return ValueRange.of(1L, (getMonth() != Month.FEBRUARY || isLeapYear()) ? 5L : 4L);
                    case 4:
                        return ValueRange.of(1L, getYear() <= 0 ? C.NANOS_PER_SECOND : 999999999L);
                    default:
                        return field.range();
                }
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.rangeRefinedBy(this);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public int get(TemporalField field) {
        if (field instanceof ChronoField) {
            return get0(field);
        }
        return TemporalAccessor.CC.$default$get(this, field);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            if (field == ChronoField.EPOCH_DAY) {
                return toEpochDay();
            }
            if (field == ChronoField.PROLEPTIC_MONTH) {
                return getProlepticMonth();
            }
            return get0(field);
        }
        return field.getFrom(this);
    }

    private int get0(TemporalField field) {
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
            case 1:
                return this.day;
            case 2:
                return getDayOfYear();
            case 3:
                return ((this.day - 1) / 7) + 1;
            case 4:
                int i = this.year;
                return i >= 1 ? i : 1 - i;
            case 5:
                return getDayOfWeek().getValue();
            case 6:
                return ((this.day - 1) % 7) + 1;
            case 7:
                return ((getDayOfYear() - 1) % 7) + 1;
            case 8:
                throw new UnsupportedTemporalTypeException("Invalid field 'EpochDay' for get() method, use getLong() instead");
            case 9:
                return ((getDayOfYear() - 1) / 7) + 1;
            case 10:
                return this.month;
            case 11:
                throw new UnsupportedTemporalTypeException("Invalid field 'ProlepticMonth' for get() method, use getLong() instead");
            case 12:
                return this.year;
            case 13:
                return this.year >= 1 ? 1 : 0;
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    private long getProlepticMonth() {
        return ((this.year * 12) + this.month) - 1;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public IsoChronology getChronology() {
        return IsoChronology.INSTANCE;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public IsoEra getEra() {
        return getYear() >= 1 ? IsoEra.CE : IsoEra.BCE;
    }

    public int getYear() {
        return this.year;
    }

    public int getMonthValue() {
        return this.month;
    }

    public Month getMonth() {
        return Month.of(this.month);
    }

    public int getDayOfMonth() {
        return this.day;
    }

    public int getDayOfYear() {
        return (getMonth().firstDayOfYear(isLeapYear()) + this.day) - 1;
    }

    public DayOfWeek getDayOfWeek() {
        int dow0 = (int) Clock$TickClock$$ExternalSyntheticBackport0.m(toEpochDay() + 3, 7L);
        return DayOfWeek.of(dow0 + 1);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public boolean isLeapYear() {
        return IsoChronology.INSTANCE.isLeapYear(this.year);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public int lengthOfMonth() {
        switch (this.month) {
            case 2:
                return isLeapYear() ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public int lengthOfYear() {
        return isLeapYear() ? 366 : 365;
    }

    @Override // j$.time.temporal.Temporal
    public LocalDate with(TemporalAdjuster adjuster) {
        if (adjuster instanceof LocalDate) {
            return (LocalDate) adjuster;
        }
        return (LocalDate) adjuster.adjustInto(this);
    }

    @Override // j$.time.temporal.Temporal
    public LocalDate with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            f.checkValidValue(newValue);
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
                case 1:
                    return withDayOfMonth((int) newValue);
                case 2:
                    return withDayOfYear((int) newValue);
                case 3:
                    return plusWeeks(newValue - getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
                case 4:
                    return withYear((int) (this.year >= 1 ? newValue : 1 - newValue));
                case 5:
                    return plusDays(newValue - getDayOfWeek().getValue());
                case 6:
                    return plusDays(newValue - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
                case 7:
                    return plusDays(newValue - getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
                case 8:
                    return ofEpochDay(newValue);
                case 9:
                    return plusWeeks(newValue - getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
                case 10:
                    return withMonth((int) newValue);
                case 11:
                    return plusMonths(newValue - getProlepticMonth());
                case 12:
                    return withYear((int) newValue);
                case 13:
                    return getLong(ChronoField.ERA) == newValue ? this : withYear(1 - this.year);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        }
        return (LocalDate) field.adjustInto(this, newValue);
    }

    public LocalDate withYear(int year) {
        if (this.year == year) {
            return this;
        }
        ChronoField.YEAR.checkValidValue(year);
        return resolvePreviousValid(year, this.month, this.day);
    }

    public LocalDate withMonth(int month) {
        if (this.month == month) {
            return this;
        }
        ChronoField.MONTH_OF_YEAR.checkValidValue(month);
        return resolvePreviousValid(this.year, month, this.day);
    }

    public LocalDate withDayOfMonth(int dayOfMonth) {
        if (this.day == dayOfMonth) {
            return this;
        }
        return of(this.year, this.month, dayOfMonth);
    }

    public LocalDate withDayOfYear(int dayOfYear) {
        if (getDayOfYear() == dayOfYear) {
            return this;
        }
        return ofYearDay(this.year, dayOfYear);
    }

    @Override // j$.time.temporal.Temporal
    public LocalDate plus(TemporalAmount amountToAdd) {
        if (amountToAdd instanceof Period) {
            Period periodToAdd = (Period) amountToAdd;
            return plusMonths(periodToAdd.toTotalMonths()).plusDays(periodToAdd.getDays());
        }
        Objects.requireNonNull(amountToAdd, "amountToAdd");
        return (LocalDate) amountToAdd.addTo(this);
    }

    @Override // j$.time.temporal.Temporal
    public LocalDate plus(long amountToAdd, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            ChronoUnit f = (ChronoUnit) unit;
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[f.ordinal()]) {
                case 1:
                    return plusDays(amountToAdd);
                case 2:
                    return plusWeeks(amountToAdd);
                case 3:
                    return plusMonths(amountToAdd);
                case 4:
                    return plusYears(amountToAdd);
                case 5:
                    return plusYears(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 10L));
                case 6:
                    return plusYears(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 100L));
                case 7:
                    return plusYears(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 1000L));
                case 8:
                    return with((TemporalField) ChronoField.ERA, Clock$OffsetClock$$ExternalSyntheticBackport0.m(getLong(ChronoField.ERA), amountToAdd));
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return (LocalDate) unit.addTo(this, amountToAdd);
    }

    /* renamed from: j$.time.LocalDate$1 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoUnit;

        static {
            int[] iArr = new int[ChronoUnit.values().length];
            $SwitchMap$java$time$temporal$ChronoUnit = iArr;
            try {
                iArr[ChronoUnit.DAYS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.WEEKS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MONTHS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.YEARS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.DECADES.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.CENTURIES.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MILLENNIA.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.ERAS.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            int[] iArr2 = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr2;
            try {
                iArr2[ChronoField.DAY_OF_MONTH.ordinal()] = 1;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.DAY_OF_YEAR.ordinal()] = 2;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_WEEK_OF_MONTH.ordinal()] = 3;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR_OF_ERA.ordinal()] = 4;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.DAY_OF_WEEK.ordinal()] = 5;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH.ordinal()] = 6;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR.ordinal()] = 7;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.EPOCH_DAY.ordinal()] = 8;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ALIGNED_WEEK_OF_YEAR.ordinal()] = 9;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MONTH_OF_YEAR.ordinal()] = 10;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.PROLEPTIC_MONTH.ordinal()] = 11;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.YEAR.ordinal()] = 12;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.ERA.ordinal()] = 13;
            } catch (NoSuchFieldError e21) {
            }
        }
    }

    public LocalDate plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        int newYear = ChronoField.YEAR.checkValidIntValue(this.year + yearsToAdd);
        return resolvePreviousValid(newYear, this.month, this.day);
    }

    public LocalDate plusMonths(long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }
        long monthCount = (this.year * 12) + (this.month - 1);
        long calcMonths = monthCount + monthsToAdd;
        int newYear = ChronoField.YEAR.checkValidIntValue(Duration$$ExternalSyntheticBackport0.m(calcMonths, 12L));
        int newMonth = ((int) Clock$TickClock$$ExternalSyntheticBackport0.m(calcMonths, 12L)) + 1;
        return resolvePreviousValid(newYear, newMonth, this.day);
    }

    public LocalDate plusWeeks(long weeksToAdd) {
        return plusDays(Duration$$ExternalSyntheticBackport1.m(weeksToAdd, 7L));
    }

    public LocalDate plusDays(long daysToAdd) {
        if (daysToAdd == 0) {
            return this;
        }
        long mjDay = Clock$OffsetClock$$ExternalSyntheticBackport0.m(toEpochDay(), daysToAdd);
        return ofEpochDay(mjDay);
    }

    @Override // j$.time.temporal.Temporal
    public LocalDate minus(TemporalAmount amountToSubtract) {
        if (amountToSubtract instanceof Period) {
            Period periodToSubtract = (Period) amountToSubtract;
            return minusMonths(periodToSubtract.toTotalMonths()).minusDays(periodToSubtract.getDays());
        }
        Objects.requireNonNull(amountToSubtract, "amountToSubtract");
        return (LocalDate) amountToSubtract.subtractFrom(this);
    }

    @Override // j$.time.temporal.Temporal
    public LocalDate minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1L, unit) : plus(-amountToSubtract, unit);
    }

    public LocalDate minusYears(long yearsToSubtract) {
        return yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1L) : plusYears(-yearsToSubtract);
    }

    public LocalDate minusMonths(long monthsToSubtract) {
        return monthsToSubtract == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1L) : plusMonths(-monthsToSubtract);
    }

    public LocalDate minusWeeks(long weeksToSubtract) {
        return weeksToSubtract == Long.MIN_VALUE ? plusWeeks(Long.MAX_VALUE).plusWeeks(1L) : plusWeeks(-weeksToSubtract);
    }

    public LocalDate minusDays(long daysToSubtract) {
        return daysToSubtract == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1L) : plusDays(-daysToSubtract);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // j$.time.temporal.TemporalAccessor
    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.localDate()) {
            return this;
        }
        return (R) ChronoLocalDate.CC.$default$query(this, temporalQuery);
    }

    @Override // j$.time.temporal.TemporalAdjuster
    public Temporal adjustInto(Temporal temporal) {
        Temporal with;
        with = temporal.with(ChronoField.EPOCH_DAY, toEpochDay());
        return with;
    }

    @Override // j$.time.temporal.Temporal
    public long until(Temporal endExclusive, TemporalUnit unit) {
        LocalDate end = from(endExclusive);
        if (unit instanceof ChronoUnit) {
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
                case 1:
                    return daysUntil(end);
                case 2:
                    return daysUntil(end) / 7;
                case 3:
                    return monthsUntil(end);
                case 4:
                    return monthsUntil(end) / 12;
                case 5:
                    return monthsUntil(end) / 120;
                case 6:
                    return monthsUntil(end) / 1200;
                case 7:
                    return monthsUntil(end) / 12000;
                case 8:
                    return end.getLong(ChronoField.ERA) - getLong(ChronoField.ERA);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return unit.between(this, end);
    }

    public long daysUntil(LocalDate end) {
        return end.toEpochDay() - toEpochDay();
    }

    private long monthsUntil(LocalDate end) {
        long packed1 = (getProlepticMonth() * 32) + getDayOfMonth();
        long packed2 = (end.getProlepticMonth() * 32) + end.getDayOfMonth();
        return (packed2 - packed1) / 32;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public Period until(ChronoLocalDate endDateExclusive) {
        LocalDate end = from(endDateExclusive);
        long totalMonths = end.getProlepticMonth() - getProlepticMonth();
        int days = end.day - this.day;
        if (totalMonths > 0 && days < 0) {
            totalMonths--;
            LocalDate calcDate = plusMonths(totalMonths);
            days = (int) (end.toEpochDay() - calcDate.toEpochDay());
        } else if (totalMonths < 0 && days > 0) {
            totalMonths++;
            days -= end.lengthOfMonth();
        }
        long years = totalMonths / 12;
        int months = (int) (totalMonths % 12);
        return Period.of(LocalDate$$ExternalSyntheticBackport0.m(years), months, days);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public LocalDateTime atTime(LocalTime time) {
        return LocalDateTime.of(this, time);
    }

    public LocalDateTime atTime(int hour, int minute) {
        return atTime(LocalTime.of(hour, minute));
    }

    public LocalDateTime atTime(int hour, int minute, int second) {
        return atTime(LocalTime.of(hour, minute, second));
    }

    public LocalDateTime atTime(int hour, int minute, int second, int nanoOfSecond) {
        return atTime(LocalTime.of(hour, minute, second, nanoOfSecond));
    }

    public OffsetDateTime atTime(OffsetTime time) {
        return OffsetDateTime.of(LocalDateTime.of(this, time.toLocalTime()), time.getOffset());
    }

    public LocalDateTime atStartOfDay() {
        return LocalDateTime.of(this, LocalTime.MIDNIGHT);
    }

    public ZonedDateTime atStartOfDay(ZoneId zone) {
        Objects.requireNonNull(zone, "zone");
        LocalDateTime ldt = atTime(LocalTime.MIDNIGHT);
        if (!(zone instanceof ZoneOffset)) {
            ZoneRules rules = zone.getRules();
            ZoneOffsetTransition trans = rules.getTransition(ldt);
            if (trans != null && trans.isGap()) {
                ldt = trans.getDateTimeAfter();
            }
        }
        return ZonedDateTime.of(ldt, zone);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public long toEpochDay() {
        long total;
        long y = this.year;
        long m = this.month;
        long total2 = 0 + (365 * y);
        if (y >= 0) {
            total = total2 + (((3 + y) / 4) - ((99 + y) / 100)) + ((399 + y) / 400);
        } else {
            total = total2 - (((y / (-4)) - (y / (-100))) + (y / (-400)));
        }
        long total3 = total + (((367 * m) - 362) / 12) + (this.day - 1);
        if (m > 2) {
            total3--;
            if (!isLeapYear()) {
                total3--;
            }
        }
        return total3 - DAYS_0000_TO_1970;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public int compareTo(ChronoLocalDate other) {
        if (other instanceof LocalDate) {
            return compareTo0((LocalDate) other);
        }
        return ChronoLocalDate.CC.$default$compareTo((ChronoLocalDate) this, other);
    }

    public int compareTo0(LocalDate otherDate) {
        int cmp = this.year - otherDate.year;
        if (cmp == 0) {
            int cmp2 = this.month - otherDate.month;
            if (cmp2 == 0) {
                return this.day - otherDate.day;
            }
            return cmp2;
        }
        return cmp;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public boolean isAfter(ChronoLocalDate other) {
        if (other instanceof LocalDate) {
            return compareTo0((LocalDate) other) > 0;
        }
        return ChronoLocalDate.CC.$default$isAfter(this, other);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public boolean isBefore(ChronoLocalDate other) {
        if (other instanceof LocalDate) {
            return compareTo0((LocalDate) other) < 0;
        }
        return ChronoLocalDate.CC.$default$isBefore(this, other);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public boolean isEqual(ChronoLocalDate other) {
        if (other instanceof LocalDate) {
            return compareTo0((LocalDate) other) == 0;
        }
        return ChronoLocalDate.CC.$default$isEqual(this, other);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof LocalDate) && compareTo0((LocalDate) obj) == 0;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public int hashCode() {
        int yearValue = this.year;
        int monthValue = this.month;
        int dayValue = this.day;
        return (yearValue & (-2048)) ^ (((yearValue << 11) + (monthValue << 6)) + dayValue);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public String toString() {
        int yearValue = this.year;
        int monthValue = this.month;
        int dayValue = this.day;
        int absYear = Math.abs(yearValue);
        StringBuilder buf = new StringBuilder(10);
        if (absYear < 1000) {
            if (yearValue < 0) {
                buf.append(yearValue - 10000);
                buf.deleteCharAt(1);
            } else {
                buf.append(yearValue + 10000);
                buf.deleteCharAt(0);
            }
        } else {
            if (yearValue > 9999) {
                buf.append('+');
            }
            buf.append(yearValue);
        }
        String str = "-0";
        buf.append(monthValue < 10 ? str : "-");
        buf.append(monthValue);
        if (dayValue >= 10) {
            str = "-";
        }
        buf.append(str);
        buf.append(dayValue);
        return buf.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 3, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    public void writeExternal(DataOutput out) {
        out.writeInt(this.year);
        out.writeByte(this.month);
        out.writeByte(this.day);
    }

    public static LocalDate readExternal(DataInput in) {
        int year = in.readInt();
        int month = in.readByte();
        int dayOfMonth = in.readByte();
        return of(year, month, dayOfMonth);
    }
}
