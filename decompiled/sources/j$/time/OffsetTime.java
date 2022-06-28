package j$.time;

import com.google.android.exoplayer2.C;
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
import j$.time.zone.ZoneRules;
import j$.util.Objects;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.Serializable;
/* loaded from: classes2.dex */
public final class OffsetTime implements Temporal, TemporalAdjuster, Comparable<OffsetTime>, Serializable {
    private static final long serialVersionUID = 7264499704384272492L;
    private final ZoneOffset offset;
    private final LocalTime time;
    public static final OffsetTime MIN = LocalTime.MIN.atOffset(ZoneOffset.MAX);
    public static final OffsetTime MAX = LocalTime.MAX.atOffset(ZoneOffset.MIN);

    public static OffsetTime now() {
        return now(Clock.systemDefaultZone());
    }

    public static OffsetTime now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static OffsetTime now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        Instant now = clock.instant();
        return ofInstant(now, clock.getZone().getRules().getOffset(now));
    }

    public static OffsetTime of(LocalTime time, ZoneOffset offset) {
        return new OffsetTime(time, offset);
    }

    public static OffsetTime of(int hour, int minute, int second, int nanoOfSecond, ZoneOffset offset) {
        return new OffsetTime(LocalTime.of(hour, minute, second, nanoOfSecond), offset);
    }

    public static OffsetTime ofInstant(Instant instant, ZoneId zone) {
        Objects.requireNonNull(instant, "instant");
        Objects.requireNonNull(zone, "zone");
        ZoneRules rules = zone.getRules();
        ZoneOffset offset = rules.getOffset(instant);
        long localSecond = instant.getEpochSecond() + offset.getTotalSeconds();
        int secsOfDay = (int) Clock$TickClock$$ExternalSyntheticBackport0.m(localSecond, 86400L);
        LocalTime time = LocalTime.ofNanoOfDay((secsOfDay * C.NANOS_PER_SECOND) + instant.getNano());
        return new OffsetTime(time, offset);
    }

    public static OffsetTime from(TemporalAccessor temporal) {
        if (temporal instanceof OffsetTime) {
            return (OffsetTime) temporal;
        }
        try {
            LocalTime time = LocalTime.from(temporal);
            ZoneOffset offset = ZoneOffset.from(temporal);
            return new OffsetTime(time, offset);
        } catch (DateTimeException ex) {
            throw new DateTimeException("Unable to obtain OffsetTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName(), ex);
        }
    }

    public static OffsetTime parse(CharSequence text) {
        return parse(text, DateTimeFormatter.ISO_OFFSET_TIME);
    }

    public static OffsetTime parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return (OffsetTime) formatter.parse(text, OffsetTime$$ExternalSyntheticLambda0.INSTANCE);
    }

    private OffsetTime(LocalTime time, ZoneOffset offset) {
        this.time = (LocalTime) Objects.requireNonNull(time, "time");
        this.offset = (ZoneOffset) Objects.requireNonNull(offset, "offset");
    }

    private OffsetTime with(LocalTime time, ZoneOffset offset) {
        if (this.time == time && this.offset.equals(offset)) {
            return this;
        }
        return new OffsetTime(time, offset);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public boolean isSupported(TemporalField field) {
        return field instanceof ChronoField ? field.isTimeBased() || field == ChronoField.OFFSET_SECONDS : field != null && field.isSupportedBy(this);
    }

    @Override // j$.time.temporal.Temporal
    public boolean isSupported(TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            return unit.isTimeBased();
        }
        return unit != null && unit.isSupportedBy(this);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public ValueRange range(TemporalField field) {
        if (field instanceof ChronoField) {
            if (field == ChronoField.OFFSET_SECONDS) {
                return field.range();
            }
            return this.time.range(field);
        }
        return field.rangeRefinedBy(this);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public int get(TemporalField field) {
        return TemporalAccessor.CC.$default$get(this, field);
    }

    @Override // j$.time.temporal.TemporalAccessor
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField) {
            if (field == ChronoField.OFFSET_SECONDS) {
                return this.offset.getTotalSeconds();
            }
            return this.time.getLong(field);
        }
        return field.getFrom(this);
    }

    public ZoneOffset getOffset() {
        return this.offset;
    }

    public OffsetTime withOffsetSameLocal(ZoneOffset offset) {
        return (offset == null || !offset.equals(this.offset)) ? new OffsetTime(this.time, offset) : this;
    }

    public OffsetTime withOffsetSameInstant(ZoneOffset offset) {
        if (offset.equals(this.offset)) {
            return this;
        }
        int difference = offset.getTotalSeconds() - this.offset.getTotalSeconds();
        LocalTime adjusted = this.time.plusSeconds(difference);
        return new OffsetTime(adjusted, offset);
    }

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
    public OffsetTime with(TemporalAdjuster adjuster) {
        if (adjuster instanceof LocalTime) {
            return with((LocalTime) adjuster, this.offset);
        }
        if (adjuster instanceof ZoneOffset) {
            return with(this.time, (ZoneOffset) adjuster);
        }
        if (adjuster instanceof OffsetTime) {
            return (OffsetTime) adjuster;
        }
        return (OffsetTime) adjuster.adjustInto(this);
    }

    @Override // j$.time.temporal.Temporal
    public OffsetTime with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            if (field == ChronoField.OFFSET_SECONDS) {
                ChronoField f = (ChronoField) field;
                return with(this.time, ZoneOffset.ofTotalSeconds(f.checkValidIntValue(newValue)));
            }
            return with(this.time.with(field, newValue), this.offset);
        }
        return (OffsetTime) field.adjustInto(this, newValue);
    }

    public OffsetTime withHour(int hour) {
        return with(this.time.withHour(hour), this.offset);
    }

    public OffsetTime withMinute(int minute) {
        return with(this.time.withMinute(minute), this.offset);
    }

    public OffsetTime withSecond(int second) {
        return with(this.time.withSecond(second), this.offset);
    }

    public OffsetTime withNano(int nanoOfSecond) {
        return with(this.time.withNano(nanoOfSecond), this.offset);
    }

    public OffsetTime truncatedTo(TemporalUnit unit) {
        return with(this.time.truncatedTo(unit), this.offset);
    }

    @Override // j$.time.temporal.Temporal
    public OffsetTime plus(TemporalAmount amountToAdd) {
        return (OffsetTime) amountToAdd.addTo(this);
    }

    @Override // j$.time.temporal.Temporal
    public OffsetTime plus(long amountToAdd, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            return with(this.time.plus(amountToAdd, unit), this.offset);
        }
        return (OffsetTime) unit.addTo(this, amountToAdd);
    }

    public OffsetTime plusHours(long hours) {
        return with(this.time.plusHours(hours), this.offset);
    }

    public OffsetTime plusMinutes(long minutes) {
        return with(this.time.plusMinutes(minutes), this.offset);
    }

    public OffsetTime plusSeconds(long seconds) {
        return with(this.time.plusSeconds(seconds), this.offset);
    }

    public OffsetTime plusNanos(long nanos) {
        return with(this.time.plusNanos(nanos), this.offset);
    }

    @Override // j$.time.temporal.Temporal
    public OffsetTime minus(TemporalAmount amountToSubtract) {
        return (OffsetTime) amountToSubtract.subtractFrom(this);
    }

    @Override // j$.time.temporal.Temporal
    public OffsetTime minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1L, unit) : plus(-amountToSubtract, unit);
    }

    public OffsetTime minusHours(long hours) {
        return with(this.time.minusHours(hours), this.offset);
    }

    public OffsetTime minusMinutes(long minutes) {
        return with(this.time.minusMinutes(minutes), this.offset);
    }

    public OffsetTime minusSeconds(long seconds) {
        return with(this.time.minusSeconds(seconds), this.offset);
    }

    public OffsetTime minusNanos(long nanos) {
        return with(this.time.minusNanos(nanos), this.offset);
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
    @Override // j$.time.temporal.TemporalAccessor
    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.offset() || temporalQuery == TemporalQueries.zone()) {
            return (R) this.offset;
        }
        boolean z = true;
        boolean z2 = temporalQuery == TemporalQueries.zoneId();
        if (temporalQuery != TemporalQueries.chronology()) {
            z = false;
        }
        if ((z2 | z) || temporalQuery == TemporalQueries.localDate()) {
            return null;
        }
        if (temporalQuery == TemporalQueries.localTime()) {
            return (R) this.time;
        }
        if (temporalQuery == TemporalQueries.precision()) {
            return (R) ChronoUnit.NANOS;
        }
        return temporalQuery.queryFrom(this);
    }

    @Override // j$.time.temporal.TemporalAdjuster
    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.NANO_OF_DAY, this.time.toNanoOfDay()).with(ChronoField.OFFSET_SECONDS, this.offset.getTotalSeconds());
    }

    @Override // j$.time.temporal.Temporal
    public long until(Temporal endExclusive, TemporalUnit unit) {
        OffsetTime end = from(endExclusive);
        if (unit instanceof ChronoUnit) {
            long nanosUntil = end.toEpochNano() - toEpochNano();
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
                case 1:
                    return nanosUntil;
                case 2:
                    return nanosUntil / 1000;
                case 3:
                    return nanosUntil / 1000000;
                case 4:
                    return nanosUntil / C.NANOS_PER_SECOND;
                case 5:
                    return nanosUntil / 60000000000L;
                case 6:
                    return nanosUntil / 3600000000000L;
                case 7:
                    return nanosUntil / 43200000000000L;
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return unit.between(this, end);
    }

    /* renamed from: j$.time.OffsetTime$1 */
    /* loaded from: classes2.dex */
    static /* synthetic */ class AnonymousClass1 {
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

    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    public OffsetDateTime atDate(LocalDate date) {
        return OffsetDateTime.of(date, this.time, this.offset);
    }

    private long toEpochNano() {
        long nod = this.time.toNanoOfDay();
        long offsetNanos = this.offset.getTotalSeconds() * C.NANOS_PER_SECOND;
        return nod - offsetNanos;
    }

    public int compareTo(OffsetTime other) {
        if (this.offset.equals(other.offset)) {
            return this.time.compareTo(other.time);
        }
        int compare = (toEpochNano() > other.toEpochNano() ? 1 : (toEpochNano() == other.toEpochNano() ? 0 : -1));
        if (compare == 0) {
            return this.time.compareTo(other.time);
        }
        return compare;
    }

    public boolean isAfter(OffsetTime other) {
        return toEpochNano() > other.toEpochNano();
    }

    public boolean isBefore(OffsetTime other) {
        return toEpochNano() < other.toEpochNano();
    }

    public boolean isEqual(OffsetTime other) {
        return toEpochNano() == other.toEpochNano();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OffsetTime)) {
            return false;
        }
        OffsetTime other = (OffsetTime) obj;
        return this.time.equals(other.time) && this.offset.equals(other.offset);
    }

    public int hashCode() {
        return this.time.hashCode() ^ this.offset.hashCode();
    }

    public String toString() {
        return this.time.toString() + this.offset.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 9, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    public void writeExternal(ObjectOutput out) {
        this.time.writeExternal(out);
        this.offset.writeExternal(out);
    }

    public static OffsetTime readExternal(ObjectInput in) {
        LocalTime time = LocalTime.readExternal(in);
        ZoneOffset offset = ZoneOffset.readExternal(in);
        return of(time, offset);
    }
}
