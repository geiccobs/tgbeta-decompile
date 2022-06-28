package j$.time.chrono;

import j$.time.DateTimeException;
import j$.time.Instant;
import j$.time.LocalTime;
import j$.time.ZoneId;
import j$.time.ZoneOffset;
import j$.time.chrono.ChronoLocalDate;
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
import java.util.Comparator;
/* loaded from: classes2.dex */
public interface ChronoZonedDateTime<D extends ChronoLocalDate> extends Temporal, Comparable<ChronoZonedDateTime<?>> {
    int compareTo(ChronoZonedDateTime<?> chronoZonedDateTime);

    boolean equals(Object obj);

    String format(DateTimeFormatter dateTimeFormatter);

    @Override // j$.time.temporal.TemporalAccessor
    int get(TemporalField temporalField);

    Chronology getChronology();

    @Override // j$.time.temporal.TemporalAccessor
    long getLong(TemporalField temporalField);

    ZoneOffset getOffset();

    ZoneId getZone();

    int hashCode();

    boolean isAfter(ChronoZonedDateTime<?> chronoZonedDateTime);

    boolean isBefore(ChronoZonedDateTime<?> chronoZonedDateTime);

    boolean isEqual(ChronoZonedDateTime<?> chronoZonedDateTime);

    @Override // j$.time.temporal.TemporalAccessor
    boolean isSupported(TemporalField temporalField);

    @Override // j$.time.temporal.Temporal
    boolean isSupported(TemporalUnit temporalUnit);

    @Override // j$.time.temporal.Temporal
    ChronoZonedDateTime<D> minus(long j, TemporalUnit temporalUnit);

    @Override // j$.time.temporal.Temporal
    ChronoZonedDateTime<D> minus(TemporalAmount temporalAmount);

    @Override // j$.time.temporal.Temporal
    ChronoZonedDateTime<D> plus(long j, TemporalUnit temporalUnit);

    @Override // j$.time.temporal.Temporal
    ChronoZonedDateTime<D> plus(TemporalAmount temporalAmount);

    @Override // j$.time.temporal.TemporalAccessor
    <R> R query(TemporalQuery<R> temporalQuery);

    @Override // j$.time.temporal.TemporalAccessor
    ValueRange range(TemporalField temporalField);

    long toEpochSecond();

    Instant toInstant();

    D toLocalDate();

    ChronoLocalDateTime<D> toLocalDateTime();

    LocalTime toLocalTime();

    String toString();

    @Override // j$.time.temporal.Temporal
    ChronoZonedDateTime<D> with(TemporalAdjuster temporalAdjuster);

    @Override // j$.time.temporal.Temporal
    ChronoZonedDateTime<D> with(TemporalField temporalField, long j);

    ChronoZonedDateTime<D> withEarlierOffsetAtOverlap();

    ChronoZonedDateTime<D> withLaterOffsetAtOverlap();

    ChronoZonedDateTime<D> withZoneSameInstant(ZoneId zoneId);

    ChronoZonedDateTime<D> withZoneSameLocal(ZoneId zoneId);

    /* renamed from: j$.time.chrono.ChronoZonedDateTime$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static Comparator<ChronoZonedDateTime<?>> timeLineOrder() {
            return AbstractChronology.INSTANT_ORDER;
        }

        public static ChronoZonedDateTime<?> from(TemporalAccessor temporal) {
            if (temporal instanceof ChronoZonedDateTime) {
                return (ChronoZonedDateTime) temporal;
            }
            Objects.requireNonNull(temporal, "temporal");
            Chronology chrono = (Chronology) temporal.query(TemporalQueries.chronology());
            if (chrono == null) {
                throw new DateTimeException("Unable to obtain ChronoZonedDateTime from TemporalAccessor: " + temporal.getClass());
            }
            return chrono.zonedDateTime(temporal);
        }

        public static ValueRange $default$range(ChronoZonedDateTime _this, TemporalField field) {
            if (field instanceof ChronoField) {
                if (field == ChronoField.INSTANT_SECONDS || field == ChronoField.OFFSET_SECONDS) {
                    return field.range();
                }
                return _this.toLocalDateTime().range(field);
            }
            return field.rangeRefinedBy(_this);
        }

        public static int $default$get(ChronoZonedDateTime _this, TemporalField field) {
            if (field instanceof ChronoField) {
                switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
                    case 1:
                        throw new UnsupportedTemporalTypeException("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
                    case 2:
                        return _this.getOffset().getTotalSeconds();
                    default:
                        return _this.toLocalDateTime().get(field);
                }
            }
            return TemporalAccessor.CC.$default$get(_this, field);
        }

        public static long $default$getLong(ChronoZonedDateTime _this, TemporalField field) {
            if (field instanceof ChronoField) {
                switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
                    case 1:
                        return _this.toEpochSecond();
                    case 2:
                        return _this.getOffset().getTotalSeconds();
                    default:
                        return _this.toLocalDateTime().getLong(field);
                }
            }
            return field.getFrom(_this);
        }

        public static boolean $default$isSupported(ChronoZonedDateTime _this, TemporalUnit unit) {
            return unit instanceof ChronoUnit ? unit != ChronoUnit.FOREVER : unit != null && unit.isSupportedBy(_this);
        }

        public static ChronoZonedDateTime $default$with(ChronoZonedDateTime _this, TemporalAdjuster adjuster) {
            Temporal adjustInto;
            Chronology chronology = _this.getChronology();
            adjustInto = adjuster.adjustInto(_this);
            return ChronoZonedDateTimeImpl.ensureValid(chronology, adjustInto);
        }

        public static ChronoZonedDateTime $default$plus(ChronoZonedDateTime _this, TemporalAmount amount) {
            Temporal addTo;
            Chronology chronology = _this.getChronology();
            addTo = amount.addTo(_this);
            return ChronoZonedDateTimeImpl.ensureValid(chronology, addTo);
        }

        public static ChronoZonedDateTime $default$minus(ChronoZonedDateTime _this, TemporalAmount amount) {
            Temporal subtractFrom;
            Chronology chronology = _this.getChronology();
            subtractFrom = amount.subtractFrom(_this);
            return ChronoZonedDateTimeImpl.ensureValid(chronology, subtractFrom);
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
        /* JADX WARN: Unknown type variable: R in type: java.time.temporal.TemporalQuery<R> */
        public static Object $default$query(ChronoZonedDateTime _this, TemporalQuery temporalQuery) {
            if (temporalQuery == TemporalQueries.zone() || temporalQuery == TemporalQueries.zoneId()) {
                return _this.getZone();
            }
            if (temporalQuery == TemporalQueries.offset()) {
                return _this.getOffset();
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

        public static long $default$toEpochSecond(ChronoZonedDateTime _this) {
            long epochDay = _this.toLocalDate().toEpochDay();
            long secs = (86400 * epochDay) + _this.toLocalTime().toSecondOfDay();
            return secs - _this.getOffset().getTotalSeconds();
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoZonedDateTime != java.time.chrono.ChronoZonedDateTime<?> */
        public static int $default$compareTo(ChronoZonedDateTime _this, ChronoZonedDateTime chronoZonedDateTime) {
            int cmp = (_this.toEpochSecond() > chronoZonedDateTime.toEpochSecond() ? 1 : (_this.toEpochSecond() == chronoZonedDateTime.toEpochSecond() ? 0 : -1));
            if (cmp == 0) {
                int cmp2 = _this.toLocalTime().getNano() - chronoZonedDateTime.toLocalTime().getNano();
                if (cmp2 == 0) {
                    int cmp3 = _this.toLocalDateTime().compareTo((ChronoLocalDateTime<?>) chronoZonedDateTime.toLocalDateTime());
                    if (cmp3 == 0) {
                        int cmp4 = _this.getZone().getId().compareTo(chronoZonedDateTime.getZone().getId());
                        if (cmp4 == 0) {
                            return _this.getChronology().compareTo(chronoZonedDateTime.getChronology());
                        }
                        return cmp4;
                    }
                    return cmp3;
                }
                return cmp2;
            }
            return cmp;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoZonedDateTime != java.time.chrono.ChronoZonedDateTime<?> */
        public static boolean $default$isBefore(ChronoZonedDateTime _this, ChronoZonedDateTime chronoZonedDateTime) {
            long thisEpochSec = _this.toEpochSecond();
            long otherEpochSec = chronoZonedDateTime.toEpochSecond();
            return thisEpochSec < otherEpochSec || (thisEpochSec == otherEpochSec && _this.toLocalTime().getNano() < chronoZonedDateTime.toLocalTime().getNano());
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoZonedDateTime != java.time.chrono.ChronoZonedDateTime<?> */
        public static boolean $default$isAfter(ChronoZonedDateTime _this, ChronoZonedDateTime chronoZonedDateTime) {
            long thisEpochSec = _this.toEpochSecond();
            long otherEpochSec = chronoZonedDateTime.toEpochSecond();
            return thisEpochSec > otherEpochSec || (thisEpochSec == otherEpochSec && _this.toLocalTime().getNano() > chronoZonedDateTime.toLocalTime().getNano());
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoZonedDateTime != java.time.chrono.ChronoZonedDateTime<?> */
        public static boolean $default$isEqual(ChronoZonedDateTime _this, ChronoZonedDateTime chronoZonedDateTime) {
            return _this.toEpochSecond() == chronoZonedDateTime.toEpochSecond() && _this.toLocalTime().getNano() == chronoZonedDateTime.toLocalTime().getNano();
        }
    }

    /* renamed from: j$.time.chrono.ChronoZonedDateTime$1 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;

        static {
            int[] iArr = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr;
            try {
                iArr[ChronoField.INSTANT_SECONDS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.OFFSET_SECONDS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }
}
