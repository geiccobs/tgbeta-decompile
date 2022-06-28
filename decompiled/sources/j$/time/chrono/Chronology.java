package j$.time.chrono;

import j$.time.Clock;
import j$.time.DateTimeException;
import j$.time.Instant;
import j$.time.LocalTime;
import j$.time.ZoneId;
import j$.time.format.DateTimeFormatterBuilder;
import j$.time.format.ResolverStyle;
import j$.time.format.TextStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalQuery;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
/* loaded from: classes2.dex */
public interface Chronology extends Comparable<Chronology> {
    int compareTo(Chronology chronology);

    ChronoLocalDate date(int i, int i2, int i3);

    ChronoLocalDate date(Era era, int i, int i2, int i3);

    ChronoLocalDate date(TemporalAccessor temporalAccessor);

    ChronoLocalDate dateEpochDay(long j);

    ChronoLocalDate dateNow();

    ChronoLocalDate dateNow(Clock clock);

    ChronoLocalDate dateNow(ZoneId zoneId);

    ChronoLocalDate dateYearDay(int i, int i2);

    ChronoLocalDate dateYearDay(Era era, int i, int i2);

    boolean equals(Object obj);

    Era eraOf(int i);

    List<Era> eras();

    String getCalendarType();

    String getDisplayName(TextStyle textStyle, Locale locale);

    String getId();

    int hashCode();

    boolean isLeapYear(long j);

    ChronoLocalDateTime<? extends ChronoLocalDate> localDateTime(TemporalAccessor temporalAccessor);

    ChronoPeriod period(int i, int i2, int i3);

    int prolepticYear(Era era, int i);

    ValueRange range(ChronoField chronoField);

    ChronoLocalDate resolveDate(Map<TemporalField, Long> map, ResolverStyle resolverStyle);

    String toString();

    ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(Instant instant, ZoneId zoneId);

    ChronoZonedDateTime<? extends ChronoLocalDate> zonedDateTime(TemporalAccessor temporalAccessor);

    /* renamed from: j$.time.chrono.Chronology$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static Chronology from(TemporalAccessor temporal) {
            Objects.requireNonNull(temporal, "temporal");
            Chronology obj = (Chronology) temporal.query(TemporalQueries.chronology());
            return obj != null ? obj : IsoChronology.INSTANCE;
        }

        public static Chronology ofLocale(Locale locale) {
            return AbstractChronology.ofLocale(locale);
        }

        public static Chronology of(String id) {
            return AbstractChronology.of(id);
        }

        public static Set<Chronology> getAvailableChronologies() {
            return AbstractChronology.getAvailableChronologies();
        }

        public static ChronoLocalDateTime $default$localDateTime(Chronology _this, TemporalAccessor temporal) {
            try {
                return _this.date(temporal).atTime(LocalTime.from(temporal));
            } catch (DateTimeException ex) {
                throw new DateTimeException("Unable to obtain ChronoLocalDateTime from TemporalAccessor: " + temporal.getClass(), ex);
            }
        }

        /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTimeImpl != java.time.chrono.ChronoLocalDateTimeImpl<?> */
        public static ChronoZonedDateTime $default$zonedDateTime(Chronology _this, TemporalAccessor temporal) {
            try {
                ZoneId zone = ZoneId.from(temporal);
                try {
                    Instant instant = Instant.from(temporal);
                    return _this.zonedDateTime(instant, zone);
                } catch (DateTimeException e) {
                    return ChronoZonedDateTimeImpl.ofBest(ChronoLocalDateTimeImpl.ensureValid(_this, _this.localDateTime(temporal)), zone, null);
                }
            } catch (DateTimeException ex) {
                throw new DateTimeException("Unable to obtain ChronoZonedDateTime from TemporalAccessor: " + temporal.getClass(), ex);
            }
        }

        public static String $default$getDisplayName(final Chronology _this, TextStyle style, Locale locale) {
            TemporalAccessor temporal = new TemporalAccessor() { // from class: j$.time.chrono.Chronology.1
                @Override // j$.time.temporal.TemporalAccessor
                public /* synthetic */ int get(TemporalField temporalField) {
                    return TemporalAccessor.CC.$default$get(this, temporalField);
                }

                @Override // j$.time.temporal.TemporalAccessor
                public /* synthetic */ ValueRange range(TemporalField temporalField) {
                    return TemporalAccessor.CC.$default$range(this, temporalField);
                }

                @Override // j$.time.temporal.TemporalAccessor
                public boolean isSupported(TemporalField field) {
                    return false;
                }

                @Override // j$.time.temporal.TemporalAccessor
                public long getLong(TemporalField field) {
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
                }

                /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
                @Override // j$.time.temporal.TemporalAccessor
                public <R> R query(TemporalQuery<R> temporalQuery) {
                    if (temporalQuery == TemporalQueries.chronology()) {
                        return (R) _this;
                    }
                    return (R) TemporalAccessor.CC.$default$query(this, temporalQuery);
                }
            };
            return new DateTimeFormatterBuilder().appendChronologyText(style).toFormatter(locale).format(temporal);
        }

        public static ChronoPeriod $default$period(Chronology _this, int years, int months, int days) {
            return new ChronoPeriodImpl(_this, years, months, days);
        }
    }
}
