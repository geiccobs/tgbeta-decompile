package j$.time.chrono;

import j$.time.DateTimeException;
import j$.time.Instant;
import j$.time.LocalTime;
import j$.time.ZoneId;
import j$.time.ZoneOffset;
import j$.time.chrono.ChronoLocalDate;
import j$.time.format.DateTimeFormatter;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAdjuster;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.TemporalUnit;
import j$.util.Objects;
import java.util.Comparator;
/* loaded from: classes2.dex */
public interface ChronoLocalDateTime<D extends ChronoLocalDate> extends Temporal, TemporalAdjuster, Comparable<ChronoLocalDateTime<?>> {
    @Override // j$.time.temporal.TemporalAdjuster
    Temporal adjustInto(Temporal temporal);

    ChronoZonedDateTime<D> atZone(ZoneId zoneId);

    int compareTo(ChronoLocalDateTime<?> chronoLocalDateTime);

    boolean equals(Object obj);

    String format(DateTimeFormatter dateTimeFormatter);

    Chronology getChronology();

    int hashCode();

    boolean isAfter(ChronoLocalDateTime<?> chronoLocalDateTime);

    boolean isBefore(ChronoLocalDateTime<?> chronoLocalDateTime);

    boolean isEqual(ChronoLocalDateTime<?> chronoLocalDateTime);

    @Override // j$.time.temporal.TemporalAccessor
    boolean isSupported(TemporalField temporalField);

    @Override // j$.time.temporal.Temporal
    boolean isSupported(TemporalUnit temporalUnit);

    @Override // j$.time.temporal.Temporal
    ChronoLocalDateTime<D> minus(long j, TemporalUnit temporalUnit);

    @Override // j$.time.temporal.Temporal
    ChronoLocalDateTime<D> minus(TemporalAmount temporalAmount);

    @Override // j$.time.temporal.Temporal
    ChronoLocalDateTime<D> plus(long j, TemporalUnit temporalUnit);

    @Override // j$.time.temporal.Temporal
    ChronoLocalDateTime<D> plus(TemporalAmount temporalAmount);

    @Override // j$.time.temporal.TemporalAccessor
    <R> R query(TemporalQuery<R> temporalQuery);

    long toEpochSecond(ZoneOffset zoneOffset);

    Instant toInstant(ZoneOffset zoneOffset);

    D toLocalDate();

    LocalTime toLocalTime();

    String toString();

    @Override // j$.time.temporal.Temporal
    ChronoLocalDateTime<D> with(TemporalAdjuster temporalAdjuster);

    @Override // j$.time.temporal.Temporal
    ChronoLocalDateTime<D> with(TemporalField temporalField, long j);

    /* renamed from: j$.time.chrono.ChronoLocalDateTime$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static Comparator<ChronoLocalDateTime<?>> timeLineOrder() {
            return AbstractChronology.DATE_TIME_ORDER;
        }

        public static ChronoLocalDateTime<?> from(TemporalAccessor temporal) {
            if (temporal instanceof ChronoLocalDateTime) {
                return (ChronoLocalDateTime) temporal;
            }
            Objects.requireNonNull(temporal, "temporal");
            Chronology chrono = (Chronology) temporal.query(TemporalQueries.chronology());
            if (chrono == null) {
                throw new DateTimeException("Unable to obtain ChronoLocalDateTime from TemporalAccessor: " + temporal.getClass());
            }
            return chrono.localDateTime(temporal);
        }

        public static boolean $default$isSupported(ChronoLocalDateTime _this, TemporalUnit unit) {
            return unit instanceof ChronoUnit ? unit != ChronoUnit.FOREVER : unit != null && unit.isSupportedBy(_this);
        }

        public static ChronoLocalDateTime $default$with(ChronoLocalDateTime _this, TemporalAdjuster adjuster) {
            Temporal adjustInto;
            Chronology chronology = _this.getChronology();
            adjustInto = adjuster.adjustInto(_this);
            return ChronoLocalDateTimeImpl.ensureValid(chronology, adjustInto);
        }

        public static ChronoLocalDateTime $default$plus(ChronoLocalDateTime _this, TemporalAmount amount) {
            Temporal addTo;
            Chronology chronology = _this.getChronology();
            addTo = amount.addTo(_this);
            return ChronoLocalDateTimeImpl.ensureValid(chronology, addTo);
        }

        public static ChronoLocalDateTime $default$minus(ChronoLocalDateTime _this, TemporalAmount amount) {
            Temporal subtractFrom;
            Chronology chronology = _this.getChronology();
            subtractFrom = amount.subtractFrom(_this);
            return ChronoLocalDateTimeImpl.ensureValid(chronology, subtractFrom);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
        /* JADX WARN: Unknown type variable: R in type: java.time.temporal.TemporalQuery<R> */
        public static Object $default$query(ChronoLocalDateTime _this, TemporalQuery temporalQuery) {
            if (temporalQuery == TemporalQueries.zoneId() || temporalQuery == TemporalQueries.zone() || temporalQuery == TemporalQueries.offset()) {
                return null;
            }
            if (temporalQuery == TemporalQueries.localTime()) {
                return _this.toLocalTime();
            }
            if (temporalQuery == TemporalQueries.chronology()) {
                return _this.getChronology();
            }
            if (temporalQuery == TemporalQueries.precision()) {
                return ChronoUnit.NANOS;
            }
            return temporalQuery.queryFrom(_this);
        }

        public static long $default$toEpochSecond(ChronoLocalDateTime _this, ZoneOffset offset) {
            Objects.requireNonNull(offset, "offset");
            long epochDay = _this.toLocalDate().toEpochDay();
            long secs = (86400 * epochDay) + _this.toLocalTime().toSecondOfDay();
            return secs - offset.getTotalSeconds();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTime != java.time.chrono.ChronoLocalDateTime<?> */
        public static int $default$compareTo(ChronoLocalDateTime _this, ChronoLocalDateTime chronoLocalDateTime) {
            int cmp = _this.toLocalDate().compareTo(chronoLocalDateTime.toLocalDate());
            if (cmp == 0) {
                int cmp2 = _this.toLocalTime().compareTo(chronoLocalDateTime.toLocalTime());
                if (cmp2 == 0) {
                    return _this.getChronology().compareTo(chronoLocalDateTime.getChronology());
                }
                return cmp2;
            }
            return cmp;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTime != java.time.chrono.ChronoLocalDateTime<?> */
        public static boolean $default$isAfter(ChronoLocalDateTime _this, ChronoLocalDateTime chronoLocalDateTime) {
            long thisEpDay = _this.toLocalDate().toEpochDay();
            long otherEpDay = chronoLocalDateTime.toLocalDate().toEpochDay();
            return thisEpDay > otherEpDay || (thisEpDay == otherEpDay && _this.toLocalTime().toNanoOfDay() > chronoLocalDateTime.toLocalTime().toNanoOfDay());
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTime != java.time.chrono.ChronoLocalDateTime<?> */
        public static boolean $default$isBefore(ChronoLocalDateTime _this, ChronoLocalDateTime chronoLocalDateTime) {
            long thisEpDay = _this.toLocalDate().toEpochDay();
            long otherEpDay = chronoLocalDateTime.toLocalDate().toEpochDay();
            return thisEpDay < otherEpDay || (thisEpDay == otherEpDay && _this.toLocalTime().toNanoOfDay() < chronoLocalDateTime.toLocalTime().toNanoOfDay());
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTime != java.time.chrono.ChronoLocalDateTime<?> */
        public static boolean $default$isEqual(ChronoLocalDateTime _this, ChronoLocalDateTime chronoLocalDateTime) {
            return _this.toLocalTime().toNanoOfDay() == chronoLocalDateTime.toLocalTime().toNanoOfDay() && _this.toLocalDate().toEpochDay() == chronoLocalDateTime.toLocalDate().toEpochDay();
        }
    }
}
