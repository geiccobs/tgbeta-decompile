package j$.time;

import com.google.android.exoplayer2.C;
import j$.time.chrono.ChronoLocalDateTime;
import j$.time.chrono.ChronoZonedDateTime;
import j$.time.chrono.Chronology;
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
import j$.time.temporal.ValueRange;
import j$.time.zone.ZoneRules;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
/* loaded from: classes2.dex */
public final class LocalDateTime implements Temporal, TemporalAdjuster, ChronoLocalDateTime<LocalDate>, Serializable {
    private static final long serialVersionUID = 6207766400415563566L;
    private final LocalDate date;
    private final LocalTime time;
    public static final LocalDateTime MIN = of(LocalDate.MIN, LocalTime.MIN);
    public static final LocalDateTime MAX = of(LocalDate.MAX, LocalTime.MAX);

    @Override // j$.time.chrono.ChronoLocalDateTime
    public /* synthetic */ Chronology getChronology() {
        Chronology chronology;
        chronology = toLocalDate().getChronology();
        return chronology;
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public /* synthetic */ long toEpochSecond(ZoneOffset zoneOffset) {
        return ChronoLocalDateTime.CC.$default$toEpochSecond(this, zoneOffset);
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public /* synthetic */ Instant toInstant(ZoneOffset zoneOffset) {
        Instant ofEpochSecond;
        ofEpochSecond = Instant.ofEpochSecond(toEpochSecond(zoneOffset), toLocalTime().getNano());
        return ofEpochSecond;
    }

    public static LocalDateTime now() {
        return now(Clock.systemDefaultZone());
    }

    public static LocalDateTime now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static LocalDateTime now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        Instant now = clock.instant();
        ZoneOffset offset = clock.getZone().getRules().getOffset(now);
        return ofEpochSecond(now.getEpochSecond(), now.getNano(), offset);
    }

    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute);
        return new LocalDateTime(date, time);
    }

    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second);
        return new LocalDateTime(date, time);
    }

    public static LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second, nanoOfSecond);
        return new LocalDateTime(date, time);
    }

    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute);
        return new LocalDateTime(date, time);
    }

    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second);
        return new LocalDateTime(date, time);
    }

    public static LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond) {
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        LocalTime time = LocalTime.of(hour, minute, second, nanoOfSecond);
        return new LocalDateTime(date, time);
    }

    public static LocalDateTime of(LocalDate date, LocalTime time) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(time, "time");
        return new LocalDateTime(date, time);
    }

    public static LocalDateTime ofInstant(Instant instant, ZoneId zone) {
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(zone, "zone");
        ZoneRules rules = zone.getRules();
        ZoneOffset offset = rules.getOffset(instant);
        return ofEpochSecond(instant.getEpochSecond(), instant.getNano(), offset);
    }

    public static LocalDateTime ofEpochSecond(long epochSecond, int nanoOfSecond, ZoneOffset offset) {
        Objects.requireNonNull(offset, "offset");
        ChronoField.NANO_OF_SECOND.checkValidValue(nanoOfSecond);
        long localSecond = offset.getTotalSeconds() + epochSecond;
        long localEpochDay = Duration$$ExternalSyntheticBackport0.m(localSecond, 86400L);
        int secsOfDay = (int) Clock$TickClock$$ExternalSyntheticBackport0.m(localSecond, 86400L);
        LocalDate date = LocalDate.ofEpochDay(localEpochDay);
        LocalTime time = LocalTime.ofNanoOfDay((secsOfDay * C.NANOS_PER_SECOND) + nanoOfSecond);
        return new LocalDateTime(date, time);
    }

    /* JADX WARN: Type inference failed for: r0v10, types: [j$.time.LocalDateTime] */
    public static LocalDateTime from(TemporalAccessor temporal) {
        if (temporal instanceof LocalDateTime) {
            return (LocalDateTime) temporal;
        }
        if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toLocalDateTime2();
        }
        if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).toLocalDateTime();
        }
        try {
            LocalDate date = LocalDate.from(temporal);
            LocalTime time = LocalTime.from(temporal);
            return new LocalDateTime(date, time);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain LocalDateTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public static LocalDateTime parse(CharSequence text) {
        return parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static LocalDateTime parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return (LocalDateTime) formatter.parse(text, LocalDateTime$$ExternalSyntheticLambda0.INSTANCE);
    }

    private LocalDateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    private LocalDateTime with(LocalDate newDate, LocalTime newTime) {
        if (this.date == newDate && this.time == newTime) {
            return this;
        }
        return new LocalDateTime(newDate, newTime);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public boolean isSupported(TemporalField field) {
        if (!(field instanceof ChronoField)) {
            return field != null && field.isSupportedBy(this);
        }
        ChronoField f = (ChronoField) field;
        return f.isDateBased() || f.isTimeBased();
    }

    @Override // j$.time.temporal.Temporal
    public boolean isSupported(TemporalUnit unit) {
        return ChronoLocalDateTime.CC.$default$isSupported(this, unit);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return f.isTimeBased() ? this.time.range(field) : this.date.range(field);
        }
        return field.rangeRefinedBy(this);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public int get(TemporalField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return f.isTimeBased() ? this.time.get(field) : this.date.get(field);
        }
        return TemporalAccessor.CC.$default$get(this, field);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            return f.isTimeBased() ? this.time.getLong(field) : this.date.getLong(field);
        }
        return field.getFrom(this);
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public LocalDate toLocalDate() {
        return this.date;
    }

    public int getYear() {
        return this.date.getYear();
    }

    public int getMonthValue() {
        return this.date.getMonthValue();
    }

    public Month getMonth() {
        return this.date.getMonth();
    }

    public int getDayOfMonth() {
        return this.date.getDayOfMonth();
    }

    public int getDayOfYear() {
        return this.date.getDayOfYear();
    }

    public DayOfWeek getDayOfWeek() {
        return this.date.getDayOfWeek();
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public LocalTime toLocalTime() {
        return this.time;
    }

    public int getHour() {
        return this.time.getHour();
    }

    public int getMinute() {
        return this.time.getMinute();
    }

    public int getSecond() {
        return this.time.getSecond();
    }

    public int getNano() {
        return this.time.getNano();
    }

    @Override // j$.time.temporal.Temporal
    public LocalDateTime with(TemporalAdjuster adjuster) {
        if (adjuster instanceof LocalDate) {
            return with((LocalDate) adjuster, this.time);
        }
        if (adjuster instanceof LocalTime) {
            return with(this.date, (LocalTime) adjuster);
        }
        if (adjuster instanceof LocalDateTime) {
            return (LocalDateTime) adjuster;
        }
        return (LocalDateTime) adjuster.adjustInto(this);
    }

    @Override // j$.time.temporal.Temporal
    public LocalDateTime with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            if (f.isTimeBased()) {
                return with(this.date, this.time.with(field, newValue));
            }
            return with(this.date.with(field, newValue), this.time);
        }
        return (LocalDateTime) field.adjustInto(this, newValue);
    }

    public LocalDateTime withYear(int year) {
        return with(this.date.withYear(year), this.time);
    }

    public LocalDateTime withMonth(int month) {
        return with(this.date.withMonth(month), this.time);
    }

    public LocalDateTime withDayOfMonth(int dayOfMonth) {
        return with(this.date.withDayOfMonth(dayOfMonth), this.time);
    }

    public LocalDateTime withDayOfYear(int dayOfYear) {
        return with(this.date.withDayOfYear(dayOfYear), this.time);
    }

    public LocalDateTime withHour(int hour) {
        LocalTime newTime = this.time.withHour(hour);
        return with(this.date, newTime);
    }

    public LocalDateTime withMinute(int minute) {
        LocalTime newTime = this.time.withMinute(minute);
        return with(this.date, newTime);
    }

    public LocalDateTime withSecond(int second) {
        LocalTime newTime = this.time.withSecond(second);
        return with(this.date, newTime);
    }

    public LocalDateTime withNano(int nanoOfSecond) {
        LocalTime newTime = this.time.withNano(nanoOfSecond);
        return with(this.date, newTime);
    }

    public LocalDateTime truncatedTo(TemporalUnit unit) {
        return with(this.date, this.time.truncatedTo(unit));
    }

    @Override // j$.time.temporal.Temporal
    public LocalDateTime plus(TemporalAmount amountToAdd) {
        if (amountToAdd instanceof Period) {
            Period periodToAdd = (Period) amountToAdd;
            return with(this.date.plus((TemporalAmount) periodToAdd), this.time);
        }
        Objects.requireNonNull(amountToAdd, "amountToAdd");
        return (LocalDateTime) amountToAdd.addTo(this);
    }

    @Override // j$.time.temporal.Temporal
    public LocalDateTime plus(long amountToAdd, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            ChronoUnit f = (ChronoUnit) unit;
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[f.ordinal()]) {
                case 1:
                    return plusNanos(amountToAdd);
                case 2:
                    return plusDays(amountToAdd / 86400000000L).plusNanos((amountToAdd % 86400000000L) * 1000);
                case 3:
                    return plusDays(amountToAdd / 86400000).plusNanos((amountToAdd % 86400000) * 1000000);
                case 4:
                    return plusSeconds(amountToAdd);
                case 5:
                    return plusMinutes(amountToAdd);
                case 6:
                    return plusHours(amountToAdd);
                case 7:
                    return plusDays(amountToAdd / 256).plusHours((amountToAdd % 256) * 12);
                default:
                    return with(this.date.plus(amountToAdd, unit), this.time);
            }
        }
        return (LocalDateTime) unit.addTo(this, amountToAdd);
    }

    /* renamed from: j$.time.LocalDateTime$1 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoUnit;

        static {
            int[] iArr = new int[ChronoUnit.values().length];
            $SwitchMap$java$time$temporal$ChronoUnit = iArr;
            try {
                iArr[ChronoUnit.NANOS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MICROS.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MILLIS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.SECONDS.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.MINUTES.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.HOURS.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoUnit[ChronoUnit.HALF_DAYS.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public LocalDateTime plusYears(long years) {
        LocalDate newDate = this.date.plusYears(years);
        return with(newDate, this.time);
    }

    public LocalDateTime plusMonths(long months) {
        LocalDate newDate = this.date.plusMonths(months);
        return with(newDate, this.time);
    }

    public LocalDateTime plusWeeks(long weeks) {
        LocalDate newDate = this.date.plusWeeks(weeks);
        return with(newDate, this.time);
    }

    public LocalDateTime plusDays(long days) {
        LocalDate newDate = this.date.plusDays(days);
        return with(newDate, this.time);
    }

    public LocalDateTime plusHours(long hours) {
        return plusWithOverflow(this.date, hours, 0L, 0L, 0L, 1);
    }

    public LocalDateTime plusMinutes(long minutes) {
        return plusWithOverflow(this.date, 0L, minutes, 0L, 0L, 1);
    }

    public LocalDateTime plusSeconds(long seconds) {
        return plusWithOverflow(this.date, 0L, 0L, seconds, 0L, 1);
    }

    public LocalDateTime plusNanos(long nanos) {
        return plusWithOverflow(this.date, 0L, 0L, 0L, nanos, 1);
    }

    @Override // j$.time.temporal.Temporal
    public LocalDateTime minus(TemporalAmount amountToSubtract) {
        if (amountToSubtract instanceof Period) {
            Period periodToSubtract = (Period) amountToSubtract;
            return with(this.date.minus((TemporalAmount) periodToSubtract), this.time);
        }
        Objects.requireNonNull(amountToSubtract, "amountToSubtract");
        return (LocalDateTime) amountToSubtract.subtractFrom(this);
    }

    @Override // j$.time.temporal.Temporal
    public LocalDateTime minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1L, unit) : plus(-amountToSubtract, unit);
    }

    public LocalDateTime minusYears(long years) {
        return years == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1L) : plusYears(-years);
    }

    public LocalDateTime minusMonths(long months) {
        return months == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1L) : plusMonths(-months);
    }

    public LocalDateTime minusWeeks(long weeks) {
        return weeks == Long.MIN_VALUE ? plusWeeks(Long.MAX_VALUE).plusWeeks(1L) : plusWeeks(-weeks);
    }

    public LocalDateTime minusDays(long days) {
        return days == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1L) : plusDays(-days);
    }

    public LocalDateTime minusHours(long hours) {
        return plusWithOverflow(this.date, hours, 0L, 0L, 0L, -1);
    }

    public LocalDateTime minusMinutes(long minutes) {
        return plusWithOverflow(this.date, 0L, minutes, 0L, 0L, -1);
    }

    public LocalDateTime minusSeconds(long seconds) {
        return plusWithOverflow(this.date, 0L, 0L, seconds, 0L, -1);
    }

    public LocalDateTime minusNanos(long nanos) {
        return plusWithOverflow(this.date, 0L, 0L, 0L, nanos, -1);
    }

    private LocalDateTime plusWithOverflow(LocalDate newDate, long hours, long minutes, long seconds, long nanos, int sign) {
        if ((hours | minutes | seconds | nanos) == 0) {
            return with(newDate, this.time);
        }
        long totDays = (nanos / 86400000000000L) + (seconds / 86400) + (minutes / 1440) + (hours / 24);
        long totNanos = (nanos % 86400000000000L) + ((seconds % 86400) * C.NANOS_PER_SECOND) + ((minutes % 1440) * 60000000000L) + ((hours % 24) * 3600000000000L);
        long curNoD = this.time.toNanoOfDay();
        long totNanos2 = (sign * totNanos) + curNoD;
        long totDays2 = (totDays * sign) + Duration$$ExternalSyntheticBackport0.m(totNanos2, 86400000000000L);
        long newNoD = Clock$TickClock$$ExternalSyntheticBackport0.m(totNanos2, 86400000000000L);
        LocalTime newTime = newNoD == curNoD ? this.time : LocalTime.ofNanoOfDay(newNoD);
        return with(newDate.plusDays(totDays2), newTime);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
    @Override // j$.time.temporal.TemporalAccessor
    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.localDate()) {
            return (R) this.date;
        }
        return (R) ChronoLocalDateTime.CC.$default$query(this, temporalQuery);
    }

    @Override // j$.time.temporal.TemporalAdjuster
    public Temporal adjustInto(Temporal temporal) {
        Temporal with;
        with = temporal.with(ChronoField.EPOCH_DAY, toLocalDate().toEpochDay()).with(ChronoField.NANO_OF_DAY, toLocalTime().toNanoOfDay());
        return with;
    }

    @Override // j$.time.temporal.Temporal
    public long until(Temporal endExclusive, TemporalUnit unit) {
        long timePart;
        long amount;
        LocalDateTime end = from(endExclusive);
        if (unit instanceof ChronoUnit) {
            if (unit.isTimeBased()) {
                long amount2 = this.date.daysUntil(end.date);
                if (amount2 == 0) {
                    return this.time.until(end.time, unit);
                }
                long timePart2 = end.time.toNanoOfDay() - this.time.toNanoOfDay();
                if (amount2 > 0) {
                    amount = amount2 - 1;
                    timePart = timePart2 + 86400000000000L;
                } else {
                    amount = amount2 + 1;
                    timePart = timePart2 - 86400000000000L;
                }
                switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
                    case 1:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, 86400000000000L);
                        break;
                    case 2:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, 86400000000L);
                        timePart /= 1000;
                        break;
                    case 3:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, 86400000L);
                        timePart /= 1000000;
                        break;
                    case 4:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, 86400L);
                        timePart /= C.NANOS_PER_SECOND;
                        break;
                    case 5:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, 1440L);
                        timePart /= 60000000000L;
                        break;
                    case 6:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, 24L);
                        timePart /= 3600000000000L;
                        break;
                    case 7:
                        amount = Duration$$ExternalSyntheticBackport1.m(amount, 2L);
                        timePart /= 43200000000000L;
                        break;
                }
                return Clock$OffsetClock$$ExternalSyntheticBackport0.m(amount, timePart);
            }
            LocalDate endDate = end.date;
            if (endDate.isAfter(this.date) && end.time.isBefore(this.time)) {
                endDate = endDate.minusDays(1L);
            } else if (endDate.isBefore(this.date) && end.time.isAfter(this.time)) {
                endDate = endDate.plusDays(1L);
            }
            return this.date.until(endDate, unit);
        }
        return unit.between(this, end);
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    public OffsetDateTime atOffset(ZoneOffset offset) {
        return OffsetDateTime.of(this, offset);
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    /* renamed from: atZone */
    public ChronoZonedDateTime<LocalDate> atZone2(ZoneId zone) {
        return ZonedDateTime.of(this, zone);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTime != java.time.chrono.ChronoLocalDateTime<?> */
    @Override // j$.time.chrono.ChronoLocalDateTime
    public int compareTo(ChronoLocalDateTime<?> chronoLocalDateTime) {
        if (chronoLocalDateTime instanceof LocalDateTime) {
            return compareTo0((LocalDateTime) chronoLocalDateTime);
        }
        return ChronoLocalDateTime.CC.$default$compareTo((ChronoLocalDateTime) this, (ChronoLocalDateTime) chronoLocalDateTime);
    }

    private int compareTo0(LocalDateTime other) {
        int cmp = this.date.compareTo0(other.toLocalDate());
        if (cmp == 0) {
            return this.time.compareTo(other.toLocalTime());
        }
        return cmp;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTime != java.time.chrono.ChronoLocalDateTime<?> */
    @Override // j$.time.chrono.ChronoLocalDateTime
    public boolean isAfter(ChronoLocalDateTime<?> chronoLocalDateTime) {
        if (chronoLocalDateTime instanceof LocalDateTime) {
            return compareTo0((LocalDateTime) chronoLocalDateTime) > 0;
        }
        return ChronoLocalDateTime.CC.$default$isAfter(this, chronoLocalDateTime);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTime != java.time.chrono.ChronoLocalDateTime<?> */
    @Override // j$.time.chrono.ChronoLocalDateTime
    public boolean isBefore(ChronoLocalDateTime<?> chronoLocalDateTime) {
        if (chronoLocalDateTime instanceof LocalDateTime) {
            return compareTo0((LocalDateTime) chronoLocalDateTime) < 0;
        }
        return ChronoLocalDateTime.CC.$default$isBefore(this, chronoLocalDateTime);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.chrono.ChronoLocalDateTime != java.time.chrono.ChronoLocalDateTime<?> */
    @Override // j$.time.chrono.ChronoLocalDateTime
    public boolean isEqual(ChronoLocalDateTime<?> chronoLocalDateTime) {
        if (chronoLocalDateTime instanceof LocalDateTime) {
            return compareTo0((LocalDateTime) chronoLocalDateTime) == 0;
        }
        return ChronoLocalDateTime.CC.$default$isEqual(this, chronoLocalDateTime);
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocalDateTime)) {
            return false;
        }
        LocalDateTime other = (LocalDateTime) obj;
        return this.date.equals(other.date) && this.time.equals(other.time);
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public int hashCode() {
        return this.date.hashCode() ^ this.time.hashCode();
    }

    @Override // j$.time.chrono.ChronoLocalDateTime
    public String toString() {
        return this.date.toString() + 'T' + this.time.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 5, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    public void writeExternal(DataOutput out) {
        this.date.writeExternal(out);
        this.time.writeExternal(out);
    }

    public static LocalDateTime readExternal(DataInput in) {
        LocalDate date = LocalDate.readExternal(in);
        LocalTime time = LocalTime.readExternal(in);
        return of(date, time);
    }
}
