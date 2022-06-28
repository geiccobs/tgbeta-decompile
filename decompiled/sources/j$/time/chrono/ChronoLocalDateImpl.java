package j$.time.chrono;

import j$.time.Clock$OffsetClock$$ExternalSyntheticBackport0;
import j$.time.Duration$$ExternalSyntheticBackport1;
import j$.time.LocalTime;
import j$.time.chrono.ChronoLocalDate;
import j$.time.format.DateTimeFormatter;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.io.Serializable;
/* loaded from: classes2.dex */
public abstract class ChronoLocalDateImpl<D extends ChronoLocalDate> implements ChronoLocalDate, Temporal, TemporalAdjuster, Serializable {
    private static final long serialVersionUID = 6282433883239719096L;

    @Override // j$.time.chrono.ChronoLocalDate, j$.time.temporal.TemporalAdjuster
    public /* synthetic */ Temporal adjustInto(Temporal temporal) {
        Temporal with;
        with = temporal.with(ChronoField.EPOCH_DAY, toEpochDay());
        return with;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public /* synthetic */ ChronoLocalDateTime atTime(LocalTime localTime) {
        ChronoLocalDateTime of;
        of = ChronoLocalDateTimeImpl.of(this, localTime);
        return of;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public /* synthetic */ int compareTo(ChronoLocalDate chronoLocalDate) {
        return ChronoLocalDate.CC.$default$compareTo((ChronoLocalDate) this, chronoLocalDate);
    }

    @Override // java.lang.Comparable
    public /* bridge */ /* synthetic */ int compareTo(ChronoLocalDate chronoLocalDate) {
        int compareTo;
        compareTo = compareTo((ChronoLocalDate) chronoLocalDate);
        return compareTo;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public /* synthetic */ String format(DateTimeFormatter dateTimeFormatter) {
        return Objects.requireNonNull(dateTimeFormatter, "formatter");
    }

    @Override // j$.time.temporal.TemporalAccessor
    public /* synthetic */ int get(TemporalField temporalField) {
        return TemporalAccessor.CC.$default$get(this, temporalField);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public /* synthetic */ Era getEra() {
        Era eraOf;
        eraOf = getChronology().eraOf(get(ChronoField.ERA));
        return eraOf;
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public /* synthetic */ boolean isAfter(ChronoLocalDate chronoLocalDate) {
        return ChronoLocalDate.CC.$default$isAfter(this, chronoLocalDate);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public /* synthetic */ boolean isBefore(ChronoLocalDate chronoLocalDate) {
        return ChronoLocalDate.CC.$default$isBefore(this, chronoLocalDate);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public /* synthetic */ boolean isEqual(ChronoLocalDate chronoLocalDate) {
        return ChronoLocalDate.CC.$default$isEqual(this, chronoLocalDate);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public /* synthetic */ boolean isLeapYear() {
        boolean isLeapYear;
        isLeapYear = getChronology().isLeapYear(getLong(ChronoField.YEAR));
        return isLeapYear;
    }

    @Override // j$.time.chrono.ChronoLocalDate, j$.time.temporal.TemporalAccessor
    public /* synthetic */ boolean isSupported(TemporalField temporalField) {
        return ChronoLocalDate.CC.$default$isSupported(this, temporalField);
    }

    @Override // j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public /* synthetic */ boolean isSupported(TemporalUnit temporalUnit) {
        return ChronoLocalDate.CC.$default$isSupported(this, temporalUnit);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public /* synthetic */ int lengthOfYear() {
        return ChronoLocalDate.CC.$default$lengthOfYear(this);
    }

    abstract D plusDays(long j);

    abstract D plusMonths(long j);

    abstract D plusYears(long j);

    @Override // j$.time.chrono.ChronoLocalDate, j$.time.temporal.TemporalAccessor
    public /* synthetic */ Object query(TemporalQuery temporalQuery) {
        return ChronoLocalDate.CC.$default$query(this, temporalQuery);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public /* synthetic */ ValueRange range(TemporalField temporalField) {
        return TemporalAccessor.CC.$default$range(this, temporalField);
    }

    @Override // j$.time.chrono.ChronoLocalDate
    public /* synthetic */ long toEpochDay() {
        long j;
        j = getLong(ChronoField.EPOCH_DAY);
        return j;
    }

    public static <D extends ChronoLocalDate> D ensureValid(Chronology chrono, Temporal temporal) {
        D other = (D) temporal;
        if (!chrono.equals(other.getChronology())) {
            throw new ClassCastException("Chronology mismatch, expected: " + chrono.getId() + ", actual: " + other.getChronology().getId());
        }
        return other;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public D with(TemporalAdjuster adjuster) {
        return (D) ChronoLocalDate.CC.$default$with((ChronoLocalDate) this, adjuster);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public D with(TemporalField field, long value) {
        return (D) ChronoLocalDate.CC.$default$with((ChronoLocalDate) this, field, value);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public D plus(TemporalAmount amount) {
        return (D) ChronoLocalDate.CC.$default$plus((ChronoLocalDate) this, amount);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public D plus(long amountToAdd, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            ChronoUnit f = (ChronoUnit) unit;
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[f.ordinal()]) {
                case 1:
                    return plusDays(amountToAdd);
                case 2:
                    return plusDays(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 7L));
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
        return (D) ChronoLocalDate.CC.$default$plus((ChronoLocalDate) this, amountToAdd, unit);
    }

    /* renamed from: j$.time.chrono.ChronoLocalDateImpl$1 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
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
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public D minus(TemporalAmount amount) {
        return (D) ChronoLocalDate.CC.$default$minus((ChronoLocalDate) this, amount);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public D minus(long amountToSubtract, TemporalUnit unit) {
        ChronoLocalDate ensureValid;
        ensureValid = ensureValid(getChronology(), Temporal.CC.$default$minus(this, amountToSubtract, unit));
        return (D) ensureValid;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    public D plusWeeks(long weeksToAdd) {
        return plusDays(Duration$$ExternalSyntheticBackport1.m(weeksToAdd, 7L));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    public D minusYears(long yearsToSubtract) {
        return yearsToSubtract == Long.MIN_VALUE ? (D) ((ChronoLocalDateImpl) plusYears(Long.MAX_VALUE)).plusYears(1L) : plusYears(-yearsToSubtract);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    public D minusMonths(long monthsToSubtract) {
        return monthsToSubtract == Long.MIN_VALUE ? (D) ((ChronoLocalDateImpl) plusMonths(Long.MAX_VALUE)).plusMonths(1L) : plusMonths(-monthsToSubtract);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    public D minusWeeks(long weeksToSubtract) {
        return weeksToSubtract == Long.MIN_VALUE ? (D) ((ChronoLocalDateImpl) plusWeeks(Long.MAX_VALUE)).plusWeeks(1L) : plusWeeks(-weeksToSubtract);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    public D minusDays(long daysToSubtract) {
        return daysToSubtract == Long.MIN_VALUE ? (D) ((ChronoLocalDateImpl) plusDays(Long.MAX_VALUE)).plusDays(1L) : plusDays(-daysToSubtract);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDate, j$.time.temporal.Temporal
    public long until(Temporal endExclusive, TemporalUnit unit) {
        Objects.requireNonNull(endExclusive, "endExclusive");
        ChronoLocalDate end = getChronology().date(endExclusive);
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
        Objects.requireNonNull(unit, "unit");
        return unit.between(this, end);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    private long daysUntil(ChronoLocalDate end) {
        return end.toEpochDay() - toEpochDay();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    private long monthsUntil(ChronoLocalDate end) {
        ValueRange range = getChronology().range(ChronoField.MONTH_OF_YEAR);
        if (range.getMaximum() != 12) {
            throw new IllegalStateException("ChronoLocalDateImpl only supports Chronologies with 12 months per year");
        }
        long packed1 = (getLong(ChronoField.PROLEPTIC_MONTH) * 32) + get(ChronoField.DAY_OF_MONTH);
        long packed2 = (end.getLong(ChronoField.PROLEPTIC_MONTH) * 32) + end.get(ChronoField.DAY_OF_MONTH);
        return (packed2 - packed1) / 32;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDate
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof ChronoLocalDate) && compareTo((ChronoLocalDate) obj) == 0;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDate
    public int hashCode() {
        long epDay = toEpochDay();
        return getChronology().hashCode() ^ ((int) ((epDay >>> 32) ^ epDay));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateImpl != java.time.chrono.ChronoLocalDateImpl<D extends j$.time.chrono.ChronoLocalDate> */
    @Override // j$.time.chrono.ChronoLocalDate
    public String toString() {
        long yoe = getLong(ChronoField.YEAR_OF_ERA);
        long moy = getLong(ChronoField.MONTH_OF_YEAR);
        long dom = getLong(ChronoField.DAY_OF_MONTH);
        StringBuilder buf = new StringBuilder(30);
        buf.append(getChronology().toString());
        buf.append(" ");
        buf.append(getEra());
        buf.append(" ");
        buf.append(yoe);
        String str = "-0";
        buf.append(moy < 10 ? str : "-");
        buf.append(moy);
        if (dom >= 10) {
            str = "-";
        }
        buf.append(str);
        buf.append(dom);
        return buf.toString();
    }
}
