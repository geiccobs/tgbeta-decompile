package j$.time.chrono;

import j$.time.DateTimeException;
import j$.time.LocalTime;
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
import j$.util.Objects;
import java.util.Comparator;
/* loaded from: classes2.dex */
public interface ChronoLocalDate extends Temporal, TemporalAdjuster, Comparable<ChronoLocalDate> {
    @Override // j$.time.temporal.TemporalAdjuster
    Temporal adjustInto(Temporal temporal);

    ChronoLocalDateTime<?> atTime(LocalTime localTime);

    int compareTo(ChronoLocalDate chronoLocalDate);

    boolean equals(Object obj);

    String format(DateTimeFormatter dateTimeFormatter);

    Chronology getChronology();

    Era getEra();

    int hashCode();

    boolean isAfter(ChronoLocalDate chronoLocalDate);

    boolean isBefore(ChronoLocalDate chronoLocalDate);

    boolean isEqual(ChronoLocalDate chronoLocalDate);

    boolean isLeapYear();

    @Override // j$.time.temporal.TemporalAccessor
    boolean isSupported(TemporalField temporalField);

    @Override // j$.time.temporal.Temporal
    boolean isSupported(TemporalUnit temporalUnit);

    int lengthOfMonth();

    int lengthOfYear();

    @Override // j$.time.temporal.Temporal
    ChronoLocalDate minus(long j, TemporalUnit temporalUnit);

    @Override // j$.time.temporal.Temporal
    ChronoLocalDate minus(TemporalAmount temporalAmount);

    @Override // j$.time.temporal.Temporal
    ChronoLocalDate plus(long j, TemporalUnit temporalUnit);

    @Override // j$.time.temporal.Temporal
    ChronoLocalDate plus(TemporalAmount temporalAmount);

    @Override // j$.time.temporal.TemporalAccessor
    <R> R query(TemporalQuery<R> temporalQuery);

    long toEpochDay();

    String toString();

    @Override // j$.time.temporal.Temporal
    long until(Temporal temporal, TemporalUnit temporalUnit);

    ChronoPeriod until(ChronoLocalDate chronoLocalDate);

    @Override // j$.time.temporal.Temporal
    ChronoLocalDate with(TemporalAdjuster temporalAdjuster);

    @Override // j$.time.temporal.Temporal
    ChronoLocalDate with(TemporalField temporalField, long j);

    /* renamed from: j$.time.chrono.ChronoLocalDate$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static Comparator<ChronoLocalDate> timeLineOrder() {
            return AbstractChronology.DATE_ORDER;
        }

        public static ChronoLocalDate from(TemporalAccessor temporal) {
            if (temporal instanceof ChronoLocalDate) {
                return (ChronoLocalDate) temporal;
            }
            Objects.requireNonNull(temporal, "temporal");
            Chronology chrono = (Chronology) temporal.query(TemporalQueries.chronology());
            if (chrono == null) {
                throw new DateTimeException("Unable to obtain ChronoLocalDate from TemporalAccessor: " + temporal.getClass());
            }
            return chrono.date(temporal);
        }

        public static int $default$lengthOfYear(ChronoLocalDate _this) {
            return _this.isLeapYear() ? 366 : 365;
        }

        public static boolean $default$isSupported(ChronoLocalDate _this, TemporalField field) {
            if (field instanceof ChronoField) {
                return field.isDateBased();
            }
            return field != null && field.isSupportedBy(_this);
        }

        public static boolean $default$isSupported(ChronoLocalDate _this, TemporalUnit unit) {
            if (unit instanceof ChronoUnit) {
                return unit.isDateBased();
            }
            return unit != null && unit.isSupportedBy(_this);
        }

        public static ChronoLocalDate $default$with(ChronoLocalDate _this, TemporalAdjuster adjuster) {
            Temporal adjustInto;
            Chronology chronology = _this.getChronology();
            adjustInto = adjuster.adjustInto(_this);
            return ChronoLocalDateImpl.ensureValid(chronology, adjustInto);
        }

        public static ChronoLocalDate $default$with(ChronoLocalDate _this, TemporalField field, long newValue) {
            if (field instanceof ChronoField) {
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
            return ChronoLocalDateImpl.ensureValid(_this.getChronology(), field.adjustInto(_this, newValue));
        }

        public static ChronoLocalDate $default$plus(ChronoLocalDate _this, TemporalAmount amount) {
            Temporal addTo;
            Chronology chronology = _this.getChronology();
            addTo = amount.addTo(_this);
            return ChronoLocalDateImpl.ensureValid(chronology, addTo);
        }

        public static ChronoLocalDate $default$plus(ChronoLocalDate _this, long amountToAdd, TemporalUnit unit) {
            if (unit instanceof ChronoUnit) {
                throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
            return ChronoLocalDateImpl.ensureValid(_this.getChronology(), unit.addTo(_this, amountToAdd));
        }

        public static ChronoLocalDate $default$minus(ChronoLocalDate _this, TemporalAmount amount) {
            Temporal subtractFrom;
            Chronology chronology = _this.getChronology();
            subtractFrom = amount.subtractFrom(_this);
            return ChronoLocalDateImpl.ensureValid(chronology, subtractFrom);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
        public static Object $default$query(ChronoLocalDate _this, TemporalQuery temporalQuery) {
            if (temporalQuery == TemporalQueries.zoneId() || temporalQuery == TemporalQueries.zone() || temporalQuery == TemporalQueries.offset() || temporalQuery == TemporalQueries.localTime()) {
                return null;
            }
            if (temporalQuery == TemporalQueries.chronology()) {
                return _this.getChronology();
            }
            if (temporalQuery == TemporalQueries.precision()) {
                return ChronoUnit.DAYS;
            }
            return temporalQuery.queryFrom(_this);
        }

        public static int $default$compareTo(ChronoLocalDate _this, ChronoLocalDate other) {
            int cmp = (_this.toEpochDay() > other.toEpochDay() ? 1 : (_this.toEpochDay() == other.toEpochDay() ? 0 : -1));
            if (cmp == 0) {
                return _this.getChronology().compareTo(other.getChronology());
            }
            return cmp;
        }

        public static boolean $default$isAfter(ChronoLocalDate _this, ChronoLocalDate other) {
            return _this.toEpochDay() > other.toEpochDay();
        }

        public static boolean $default$isBefore(ChronoLocalDate _this, ChronoLocalDate other) {
            return _this.toEpochDay() < other.toEpochDay();
        }

        public static boolean $default$isEqual(ChronoLocalDate _this, ChronoLocalDate other) {
            return _this.toEpochDay() == other.toEpochDay();
        }
    }
}
