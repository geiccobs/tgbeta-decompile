package j$.time.chrono;

import androidx.exifinterface.media.ExifInterface;
import j$.time.Clock;
import j$.time.Clock$TickClock$$ExternalSyntheticBackport0;
import j$.time.DateTimeException;
import j$.time.Duration$$ExternalSyntheticBackport0;
import j$.time.Instant;
import j$.time.Instant$$ExternalSyntheticBackport0;
import j$.time.LocalDate;
import j$.time.LocalDateTime;
import j$.time.Month;
import j$.time.Period;
import j$.time.Year;
import j$.time.ZoneId;
import j$.time.ZonedDateTime;
import j$.time.format.ResolverStyle;
import j$.time.temporal.ChronoField;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalField;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public final class IsoChronology extends AbstractChronology implements Serializable {
    public static final IsoChronology INSTANCE = new IsoChronology();
    private static final long serialVersionUID = -1440403870442975015L;

    private IsoChronology() {
    }

    @Override // j$.time.chrono.Chronology
    public String getId() {
        return ExifInterface.TAG_RW2_ISO;
    }

    @Override // j$.time.chrono.Chronology
    public String getCalendarType() {
        return "iso8601";
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public LocalDate date(Era era, int yearOfEra, int month, int dayOfMonth) {
        return date(prolepticYear(era, yearOfEra), month, dayOfMonth);
    }

    @Override // j$.time.chrono.Chronology
    public LocalDate date(int prolepticYear, int month, int dayOfMonth) {
        return LocalDate.of(prolepticYear, month, dayOfMonth);
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public LocalDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        return dateYearDay(prolepticYear(era, yearOfEra), dayOfYear);
    }

    @Override // j$.time.chrono.Chronology
    public LocalDate dateYearDay(int prolepticYear, int dayOfYear) {
        return LocalDate.ofYearDay(prolepticYear, dayOfYear);
    }

    @Override // j$.time.chrono.Chronology
    public LocalDate dateEpochDay(long epochDay) {
        return LocalDate.ofEpochDay(epochDay);
    }

    @Override // j$.time.chrono.Chronology
    public LocalDate date(TemporalAccessor temporal) {
        return LocalDate.from(temporal);
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public LocalDateTime localDateTime(TemporalAccessor temporal) {
        return LocalDateTime.from(temporal);
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public ZonedDateTime zonedDateTime(TemporalAccessor temporal) {
        return ZonedDateTime.from(temporal);
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public ZonedDateTime zonedDateTime(Instant instant, ZoneId zone) {
        return ZonedDateTime.ofInstant(instant, zone);
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public LocalDate dateNow() {
        return dateNow(Clock.systemDefaultZone());
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public LocalDate dateNow(ZoneId zone) {
        return dateNow(Clock.system(zone));
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public LocalDate dateNow(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        return date((TemporalAccessor) LocalDate.now(clock));
    }

    @Override // j$.time.chrono.Chronology
    public boolean isLeapYear(long prolepticYear) {
        return (3 & prolepticYear) == 0 && (prolepticYear % 100 != 0 || prolepticYear % 400 == 0);
    }

    @Override // j$.time.chrono.Chronology
    public int prolepticYear(Era era, int yearOfEra) {
        if (era instanceof IsoEra) {
            return era == IsoEra.CE ? yearOfEra : 1 - yearOfEra;
        }
        throw new ClassCastException("Era must be IsoEra");
    }

    @Override // j$.time.chrono.Chronology
    public IsoEra eraOf(int eraValue) {
        return IsoEra.of(eraValue);
    }

    @Override // j$.time.chrono.Chronology
    public List<Era> eras() {
        return Arrays.asList(IsoEra.values());
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public LocalDate resolveDate(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        return (LocalDate) super.resolveDate(map, resolverStyle);
    }

    @Override // j$.time.chrono.AbstractChronology
    void resolveProlepticMonth(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        Long pMonth = map.remove(ChronoField.PROLEPTIC_MONTH);
        if (pMonth != null) {
            if (resolverStyle != ResolverStyle.LENIENT) {
                ChronoField.PROLEPTIC_MONTH.checkValidValue(pMonth.longValue());
            }
            addFieldValue(map, ChronoField.MONTH_OF_YEAR, Clock$TickClock$$ExternalSyntheticBackport0.m(pMonth.longValue(), 12L) + 1);
            addFieldValue(map, ChronoField.YEAR, Duration$$ExternalSyntheticBackport0.m(pMonth.longValue(), 12L));
        }
    }

    @Override // j$.time.chrono.AbstractChronology
    LocalDate resolveYearOfEra(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        Long yoeLong = map.remove(ChronoField.YEAR_OF_ERA);
        if (yoeLong != null) {
            if (resolverStyle != ResolverStyle.LENIENT) {
                ChronoField.YEAR_OF_ERA.checkValidValue(yoeLong.longValue());
            }
            Long era = map.remove(ChronoField.ERA);
            if (era != null) {
                if (era.longValue() != 1) {
                    if (era.longValue() == 0) {
                        addFieldValue(map, ChronoField.YEAR, Instant$$ExternalSyntheticBackport0.m(1L, yoeLong.longValue()));
                        return null;
                    }
                    throw new DateTimeException("Invalid value for era: " + era);
                }
                addFieldValue(map, ChronoField.YEAR, yoeLong.longValue());
                return null;
            }
            Long year = map.get(ChronoField.YEAR);
            if (resolverStyle != ResolverStyle.STRICT) {
                addFieldValue(map, ChronoField.YEAR, (year == null || year.longValue() > 0) ? yoeLong.longValue() : Instant$$ExternalSyntheticBackport0.m(1L, yoeLong.longValue()));
                return null;
            } else if (year != null) {
                ChronoField chronoField = ChronoField.YEAR;
                int i = (year.longValue() > 0L ? 1 : (year.longValue() == 0L ? 0 : -1));
                long longValue = yoeLong.longValue();
                if (i <= 0) {
                    longValue = Instant$$ExternalSyntheticBackport0.m(1L, longValue);
                }
                addFieldValue(map, chronoField, longValue);
                return null;
            } else {
                map.put(ChronoField.YEAR_OF_ERA, yoeLong);
                return null;
            }
        } else if (map.containsKey(ChronoField.ERA)) {
            ChronoField.ERA.checkValidValue(map.get(ChronoField.ERA).longValue());
            return null;
        } else {
            return null;
        }
    }

    @Override // j$.time.chrono.AbstractChronology
    LocalDate resolveYMD(Map<TemporalField, Long> map, ResolverStyle resolverStyle) {
        int y = ChronoField.YEAR.checkValidIntValue(map.remove(ChronoField.YEAR).longValue());
        if (resolverStyle == ResolverStyle.LENIENT) {
            long months = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.MONTH_OF_YEAR).longValue(), 1L);
            long days = Instant$$ExternalSyntheticBackport0.m(map.remove(ChronoField.DAY_OF_MONTH).longValue(), 1L);
            return LocalDate.of(y, 1, 1).plusMonths(months).plusDays(days);
        }
        int moy = ChronoField.MONTH_OF_YEAR.checkValidIntValue(map.remove(ChronoField.MONTH_OF_YEAR).longValue());
        int dom = ChronoField.DAY_OF_MONTH.checkValidIntValue(map.remove(ChronoField.DAY_OF_MONTH).longValue());
        if (resolverStyle == ResolverStyle.SMART) {
            if (moy == 4 || moy == 6 || moy == 9 || moy == 11) {
                dom = Math.min(dom, 30);
            } else if (moy == 2) {
                dom = Math.min(dom, Month.FEBRUARY.length(Year.isLeap(y)));
            }
        }
        return LocalDate.of(y, moy, dom);
    }

    @Override // j$.time.chrono.Chronology
    public ValueRange range(ChronoField field) {
        return field.range();
    }

    @Override // j$.time.chrono.AbstractChronology, j$.time.chrono.Chronology
    public Period period(int years, int months, int days) {
        return Period.of(years, months, days);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // j$.time.chrono.AbstractChronology
    public Object writeReplace() {
        return super.writeReplace();
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }
}
