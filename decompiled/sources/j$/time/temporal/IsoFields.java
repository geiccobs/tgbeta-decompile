package j$.time.temporal;

import j$.time.Clock$OffsetClock$$ExternalSyntheticBackport0;
import j$.time.DateTimeException;
import j$.time.DayOfWeek;
import j$.time.Duration;
import j$.time.Duration$$ExternalSyntheticBackport1;
import j$.time.Instant$$ExternalSyntheticBackport0;
import j$.time.LocalDate;
import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.format.ResolverStyle;
import j$.time.temporal.TemporalField;
import j$.util.Objects;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes2.dex */
public final class IsoFields {
    public static final TemporalField DAY_OF_QUARTER = Field.DAY_OF_QUARTER;
    public static final TemporalField QUARTER_OF_YEAR = Field.QUARTER_OF_YEAR;
    public static final TemporalField WEEK_OF_WEEK_BASED_YEAR = Field.WEEK_OF_WEEK_BASED_YEAR;
    public static final TemporalField WEEK_BASED_YEAR = Field.WEEK_BASED_YEAR;
    public static final TemporalUnit WEEK_BASED_YEARS = Unit.WEEK_BASED_YEARS;
    public static final TemporalUnit QUARTER_YEARS = Unit.QUARTER_YEARS;

    private IsoFields() {
        throw new AssertionError("Not instantiable");
    }

    /* loaded from: classes2.dex */
    public enum Field implements TemporalField {
        DAY_OF_QUARTER { // from class: j$.time.temporal.IsoFields.Field.1
            @Override // j$.time.temporal.TemporalField
            public TemporalUnit getBaseUnit() {
                return ChronoUnit.DAYS;
            }

            @Override // j$.time.temporal.TemporalField
            public TemporalUnit getRangeUnit() {
                return IsoFields.QUARTER_YEARS;
            }

            @Override // j$.time.temporal.TemporalField
            public ValueRange range() {
                return ValueRange.of(1L, 90L, 92L);
            }

            @Override // j$.time.temporal.TemporalField
            public boolean isSupportedBy(TemporalAccessor temporal) {
                return temporal.isSupported(ChronoField.DAY_OF_YEAR) && temporal.isSupported(ChronoField.MONTH_OF_YEAR) && temporal.isSupported(ChronoField.YEAR) && Field.isIso(temporal);
            }

            @Override // j$.time.temporal.IsoFields.Field, j$.time.temporal.TemporalField
            public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
                if (!isSupportedBy(temporal)) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: DayOfQuarter");
                }
                long qoy = temporal.getLong(QUARTER_OF_YEAR);
                long j = 91;
                if (qoy == 1) {
                    long year = temporal.getLong(ChronoField.YEAR);
                    if (!IsoChronology.INSTANCE.isLeapYear(year)) {
                        j = 90;
                    }
                    return ValueRange.of(1L, j);
                } else if (qoy == 2) {
                    return ValueRange.of(1L, 91L);
                } else {
                    if (qoy == 3 || qoy == 4) {
                        return ValueRange.of(1L, 92L);
                    }
                    return range();
                }
            }

            @Override // j$.time.temporal.TemporalField
            public long getFrom(TemporalAccessor temporal) {
                if (!isSupportedBy(temporal)) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: DayOfQuarter");
                }
                int doy = temporal.get(ChronoField.DAY_OF_YEAR);
                int moy = temporal.get(ChronoField.MONTH_OF_YEAR);
                long year = temporal.getLong(ChronoField.YEAR);
                return doy - Field.QUARTER_DAYS[((moy - 1) / 3) + (IsoChronology.INSTANCE.isLeapYear(year) ? 4 : 0)];
            }

            @Override // j$.time.temporal.TemporalField
            public <R extends Temporal> R adjustInto(R temporal, long newValue) {
                long curValue = getFrom(temporal);
                range().checkValidValue(newValue, this);
                return (R) temporal.with(ChronoField.DAY_OF_YEAR, temporal.getLong(ChronoField.DAY_OF_YEAR) + (newValue - curValue));
            }

            @Override // j$.time.temporal.IsoFields.Field, j$.time.temporal.TemporalField
            public ChronoLocalDate resolve(Map<TemporalField, Long> map, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
                LocalDate date;
                long doq;
                Long yearLong = map.get(ChronoField.YEAR);
                Long qoyLong = map.get(QUARTER_OF_YEAR);
                if (yearLong == null || qoyLong == null) {
                    return null;
                }
                int y = ChronoField.YEAR.checkValidIntValue(yearLong.longValue());
                long doq2 = map.get(DAY_OF_QUARTER).longValue();
                Field.ensureIso(partialTemporal);
                if (resolverStyle == ResolverStyle.LENIENT) {
                    date = LocalDate.of(y, 1, 1).plusMonths(Duration$$ExternalSyntheticBackport1.m(Instant$$ExternalSyntheticBackport0.m(qoyLong.longValue(), 1L), 3L));
                    doq = Instant$$ExternalSyntheticBackport0.m(doq2, 1L);
                } else {
                    int qoy = QUARTER_OF_YEAR.range().checkValidIntValue(qoyLong.longValue(), QUARTER_OF_YEAR);
                    date = LocalDate.of(y, ((qoy - 1) * 3) + 1, 1);
                    if (doq2 < 1 || doq2 > 90) {
                        if (resolverStyle == ResolverStyle.STRICT) {
                            rangeRefinedBy(date).checkValidValue(doq2, this);
                        } else {
                            range().checkValidValue(doq2, this);
                        }
                    }
                    doq = doq2 - 1;
                }
                map.remove(this);
                map.remove(ChronoField.YEAR);
                map.remove(QUARTER_OF_YEAR);
                return date.plusDays(doq);
            }

            @Override // java.lang.Enum, j$.time.temporal.TemporalField
            public String toString() {
                return "DayOfQuarter";
            }
        },
        QUARTER_OF_YEAR { // from class: j$.time.temporal.IsoFields.Field.2
            @Override // j$.time.temporal.TemporalField
            public TemporalUnit getBaseUnit() {
                return IsoFields.QUARTER_YEARS;
            }

            @Override // j$.time.temporal.TemporalField
            public TemporalUnit getRangeUnit() {
                return ChronoUnit.YEARS;
            }

            @Override // j$.time.temporal.TemporalField
            public ValueRange range() {
                return ValueRange.of(1L, 4L);
            }

            @Override // j$.time.temporal.TemporalField
            public boolean isSupportedBy(TemporalAccessor temporal) {
                return temporal.isSupported(ChronoField.MONTH_OF_YEAR) && Field.isIso(temporal);
            }

            @Override // j$.time.temporal.TemporalField
            public long getFrom(TemporalAccessor temporal) {
                if (!isSupportedBy(temporal)) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: QuarterOfYear");
                }
                long moy = temporal.getLong(ChronoField.MONTH_OF_YEAR);
                return (2 + moy) / 3;
            }

            @Override // j$.time.temporal.TemporalField
            public <R extends Temporal> R adjustInto(R temporal, long newValue) {
                long curValue = getFrom(temporal);
                range().checkValidValue(newValue, this);
                return (R) temporal.with(ChronoField.MONTH_OF_YEAR, temporal.getLong(ChronoField.MONTH_OF_YEAR) + ((newValue - curValue) * 3));
            }

            @Override // java.lang.Enum, j$.time.temporal.TemporalField
            public String toString() {
                return "QuarterOfYear";
            }
        },
        WEEK_OF_WEEK_BASED_YEAR { // from class: j$.time.temporal.IsoFields.Field.3
            @Override // j$.time.temporal.IsoFields.Field, j$.time.temporal.TemporalField
            public String getDisplayName(Locale locale) {
                Objects.requireNonNull(locale, "locale");
                return "Week";
            }

            @Override // j$.time.temporal.TemporalField
            public TemporalUnit getBaseUnit() {
                return ChronoUnit.WEEKS;
            }

            @Override // j$.time.temporal.TemporalField
            public TemporalUnit getRangeUnit() {
                return IsoFields.WEEK_BASED_YEARS;
            }

            @Override // j$.time.temporal.TemporalField
            public ValueRange range() {
                return ValueRange.of(1L, 52L, 53L);
            }

            @Override // j$.time.temporal.TemporalField
            public boolean isSupportedBy(TemporalAccessor temporal) {
                return temporal.isSupported(ChronoField.EPOCH_DAY) && Field.isIso(temporal);
            }

            @Override // j$.time.temporal.IsoFields.Field, j$.time.temporal.TemporalField
            public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
                if (isSupportedBy(temporal)) {
                    return Field.getWeekRange(LocalDate.from(temporal));
                }
                throw new UnsupportedTemporalTypeException("Unsupported field: WeekOfWeekBasedYear");
            }

            @Override // j$.time.temporal.TemporalField
            public long getFrom(TemporalAccessor temporal) {
                if (isSupportedBy(temporal)) {
                    return Field.getWeek(LocalDate.from(temporal));
                }
                throw new UnsupportedTemporalTypeException("Unsupported field: WeekOfWeekBasedYear");
            }

            @Override // j$.time.temporal.TemporalField
            public <R extends Temporal> R adjustInto(R temporal, long newValue) {
                range().checkValidValue(newValue, this);
                return (R) temporal.plus(Instant$$ExternalSyntheticBackport0.m(newValue, getFrom(temporal)), ChronoUnit.WEEKS);
            }

            @Override // j$.time.temporal.IsoFields.Field, j$.time.temporal.TemporalField
            public ChronoLocalDate resolve(Map<TemporalField, Long> map, TemporalAccessor partialTemporal, ResolverStyle resolverStyle) {
                LocalDate date;
                long j;
                Long wbyLong = map.get(WEEK_BASED_YEAR);
                Long dowLong = map.get(ChronoField.DAY_OF_WEEK);
                if (wbyLong == null || dowLong == null) {
                    return null;
                }
                int wby = WEEK_BASED_YEAR.range().checkValidIntValue(wbyLong.longValue(), WEEK_BASED_YEAR);
                long wowby = map.get(WEEK_OF_WEEK_BASED_YEAR).longValue();
                Field.ensureIso(partialTemporal);
                LocalDate date2 = LocalDate.of(wby, 1, 4);
                if (resolverStyle == ResolverStyle.LENIENT) {
                    long dow = dowLong.longValue();
                    if (dow > 7) {
                        date2 = date2.plusWeeks((dow - 1) / 7);
                        dow = ((dow - 1) % 7) + 1;
                        j = 1;
                    } else if (dow < 1) {
                        date2 = date2.plusWeeks(Instant$$ExternalSyntheticBackport0.m(dow, 7L) / 7);
                        j = 1;
                        dow = ((6 + dow) % 7) + 1;
                    } else {
                        j = 1;
                    }
                    date = date2.plusWeeks(Instant$$ExternalSyntheticBackport0.m(wowby, j)).with((TemporalField) ChronoField.DAY_OF_WEEK, dow);
                } else {
                    int dow2 = ChronoField.DAY_OF_WEEK.checkValidIntValue(dowLong.longValue());
                    if (wowby < 1 || wowby > 52) {
                        if (resolverStyle == ResolverStyle.STRICT) {
                            Field.getWeekRange(date2).checkValidValue(wowby, this);
                        } else {
                            range().checkValidValue(wowby, this);
                        }
                    }
                    date = date2.plusWeeks(wowby - 1).with((TemporalField) ChronoField.DAY_OF_WEEK, dow2);
                }
                map.remove(this);
                map.remove(WEEK_BASED_YEAR);
                map.remove(ChronoField.DAY_OF_WEEK);
                return date;
            }

            @Override // java.lang.Enum, j$.time.temporal.TemporalField
            public String toString() {
                return "WeekOfWeekBasedYear";
            }
        },
        WEEK_BASED_YEAR { // from class: j$.time.temporal.IsoFields.Field.4
            @Override // j$.time.temporal.TemporalField
            public TemporalUnit getBaseUnit() {
                return IsoFields.WEEK_BASED_YEARS;
            }

            @Override // j$.time.temporal.TemporalField
            public TemporalUnit getRangeUnit() {
                return ChronoUnit.FOREVER;
            }

            @Override // j$.time.temporal.TemporalField
            public ValueRange range() {
                return ChronoField.YEAR.range();
            }

            @Override // j$.time.temporal.TemporalField
            public boolean isSupportedBy(TemporalAccessor temporal) {
                return temporal.isSupported(ChronoField.EPOCH_DAY) && Field.isIso(temporal);
            }

            @Override // j$.time.temporal.TemporalField
            public long getFrom(TemporalAccessor temporal) {
                if (isSupportedBy(temporal)) {
                    return Field.getWeekBasedYear(LocalDate.from(temporal));
                }
                throw new UnsupportedTemporalTypeException("Unsupported field: WeekBasedYear");
            }

            @Override // j$.time.temporal.TemporalField
            public <R extends Temporal> R adjustInto(R temporal, long newValue) {
                if (!isSupportedBy(temporal)) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: WeekBasedYear");
                }
                int newWby = range().checkValidIntValue(newValue, WEEK_BASED_YEAR);
                LocalDate date = LocalDate.from(temporal);
                int dow = date.get(ChronoField.DAY_OF_WEEK);
                int week = Field.getWeek(date);
                if (week == 53 && Field.getWeekRange(newWby) == 52) {
                    week = 52;
                }
                LocalDate resolved = LocalDate.of(newWby, 1, 4);
                int days = (dow - resolved.get(ChronoField.DAY_OF_WEEK)) + ((week - 1) * 7);
                return (R) temporal.with(resolved.plusDays(days));
            }

            @Override // java.lang.Enum, j$.time.temporal.TemporalField
            public String toString() {
                return "WeekBasedYear";
            }
        };
        
        private static final int[] QUARTER_DAYS = {0, 90, 181, 273, 0, 91, 182, 274};

        @Override // j$.time.temporal.TemporalField
        public /* synthetic */ String getDisplayName(Locale locale) {
            return Objects.requireNonNull(locale, "locale");
        }

        @Override // j$.time.temporal.TemporalField
        public /* synthetic */ TemporalAccessor resolve(Map map, TemporalAccessor temporalAccessor, ResolverStyle resolverStyle) {
            return TemporalField.CC.$default$resolve(this, map, temporalAccessor, resolverStyle);
        }

        /* synthetic */ Field(AnonymousClass1 x2) {
            this();
        }

        @Override // j$.time.temporal.TemporalField
        public boolean isDateBased() {
            return true;
        }

        @Override // j$.time.temporal.TemporalField
        public boolean isTimeBased() {
            return false;
        }

        @Override // j$.time.temporal.TemporalField
        public ValueRange rangeRefinedBy(TemporalAccessor temporal) {
            return range();
        }

        public static boolean isIso(TemporalAccessor temporal) {
            return Chronology.CC.from(temporal).equals(IsoChronology.INSTANCE);
        }

        public static void ensureIso(TemporalAccessor temporal) {
            if (!isIso(temporal)) {
                throw new DateTimeException("Resolve requires IsoChronology");
            }
        }

        public static ValueRange getWeekRange(LocalDate date) {
            int wby = getWeekBasedYear(date);
            return ValueRange.of(1L, getWeekRange(wby));
        }

        public static int getWeekRange(int wby) {
            LocalDate date = LocalDate.of(wby, 1, 1);
            if (date.getDayOfWeek() != DayOfWeek.THURSDAY) {
                if (date.getDayOfWeek() == DayOfWeek.WEDNESDAY && date.isLeapYear()) {
                    return 53;
                }
                return 52;
            }
            return 53;
        }

        public static int getWeek(LocalDate date) {
            int dow0 = date.getDayOfWeek().ordinal();
            boolean z = true;
            int doy0 = date.getDayOfYear() - 1;
            int doyThu0 = (3 - dow0) + doy0;
            int alignedWeek = doyThu0 / 7;
            int firstThuDoy0 = doyThu0 - (alignedWeek * 7);
            int firstMonDoy0 = firstThuDoy0 - 3;
            if (firstMonDoy0 < -3) {
                firstMonDoy0 += 7;
            }
            if (doy0 < firstMonDoy0) {
                return (int) getWeekRange(date.withDayOfYear(180).minusYears(1L)).getMaximum();
            }
            int week = ((doy0 - firstMonDoy0) / 7) + 1;
            if (week == 53) {
                if (firstMonDoy0 != -3 && (firstMonDoy0 != -2 || !date.isLeapYear())) {
                    z = false;
                }
                if (!z) {
                    return 1;
                }
                return week;
            }
            return week;
        }

        public static int getWeekBasedYear(LocalDate date) {
            int year = date.getYear();
            int doy = date.getDayOfYear();
            if (doy <= 3) {
                int dow = date.getDayOfWeek().ordinal();
                if (doy - dow < -2) {
                    return year - 1;
                }
                return year;
            } else if (doy >= 363) {
                int dow2 = date.getDayOfWeek().ordinal();
                if (((doy - 363) - (date.isLeapYear() ? 1 : 0)) - dow2 >= 0) {
                    return year + 1;
                }
                return year;
            } else {
                return year;
            }
        }
    }

    /* loaded from: classes2.dex */
    private enum Unit implements TemporalUnit {
        WEEK_BASED_YEARS("WeekBasedYears", Duration.ofSeconds(31556952)),
        QUARTER_YEARS("QuarterYears", Duration.ofSeconds(7889238));
        
        private final Duration duration;
        private final String name;

        Unit(String name, Duration estimatedDuration) {
            this.name = name;
            this.duration = estimatedDuration;
        }

        @Override // j$.time.temporal.TemporalUnit
        public Duration getDuration() {
            return this.duration;
        }

        @Override // j$.time.temporal.TemporalUnit
        public boolean isDurationEstimated() {
            return true;
        }

        @Override // j$.time.temporal.TemporalUnit
        public boolean isDateBased() {
            return true;
        }

        @Override // j$.time.temporal.TemporalUnit
        public boolean isTimeBased() {
            return false;
        }

        @Override // j$.time.temporal.TemporalUnit
        public boolean isSupportedBy(Temporal temporal) {
            return temporal.isSupported(ChronoField.EPOCH_DAY);
        }

        @Override // j$.time.temporal.TemporalUnit
        public <R extends Temporal> R addTo(R temporal, long amount) {
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$IsoFields$Unit[ordinal()]) {
                case 1:
                    return (R) temporal.with(IsoFields.WEEK_BASED_YEAR, Clock$OffsetClock$$ExternalSyntheticBackport0.m(temporal.get(IsoFields.WEEK_BASED_YEAR), amount));
                case 2:
                    return (R) temporal.plus(amount / 256, ChronoUnit.YEARS).plus((amount % 256) * 3, ChronoUnit.MONTHS);
                default:
                    throw new IllegalStateException("Unreachable");
            }
        }

        @Override // j$.time.temporal.TemporalUnit
        public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
            if (temporal1Inclusive.getClass() != temporal2Exclusive.getClass()) {
                return temporal1Inclusive.until(temporal2Exclusive, this);
            }
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$IsoFields$Unit[ordinal()]) {
                case 1:
                    return Instant$$ExternalSyntheticBackport0.m(temporal2Exclusive.getLong(IsoFields.WEEK_BASED_YEAR), temporal1Inclusive.getLong(IsoFields.WEEK_BASED_YEAR));
                case 2:
                    return temporal1Inclusive.until(temporal2Exclusive, ChronoUnit.MONTHS) / 3;
                default:
                    throw new IllegalStateException("Unreachable");
            }
        }

        @Override // java.lang.Enum, j$.time.temporal.TemporalUnit
        public String toString() {
            return this.name;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: j$.time.temporal.IsoFields$1 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$IsoFields$Unit;

        static {
            int[] iArr = new int[Unit.values().length];
            $SwitchMap$java$time$temporal$IsoFields$Unit = iArr;
            try {
                iArr[Unit.WEEK_BASED_YEARS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$IsoFields$Unit[Unit.QUARTER_YEARS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }
}
