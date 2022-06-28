package j$.time;

import com.microsoft.appcenter.Constants;
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
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import org.telegram.messenger.MediaController;
/* loaded from: classes2.dex */
public final class LocalTime implements Temporal, TemporalAdjuster, Comparable<LocalTime>, Serializable {
    private static final LocalTime[] HOURS = new LocalTime[24];
    static final int HOURS_PER_DAY = 24;
    public static final LocalTime MAX;
    static final long MICROS_PER_DAY = 86400000000L;
    public static final LocalTime MIDNIGHT;
    static final long MILLIS_PER_DAY = 86400000;
    public static final LocalTime MIN;
    static final int MINUTES_PER_DAY = 1440;
    static final int MINUTES_PER_HOUR = 60;
    static final long NANOS_PER_DAY = 86400000000000L;
    static final long NANOS_PER_HOUR = 3600000000000L;
    static final long NANOS_PER_MINUTE = 60000000000L;
    static final long NANOS_PER_SECOND = 1000000000;
    public static final LocalTime NOON;
    static final int SECONDS_PER_DAY = 86400;
    static final int SECONDS_PER_HOUR = 3600;
    static final int SECONDS_PER_MINUTE = 60;
    private static final long serialVersionUID = 6414437269572265201L;
    private final byte hour;
    private final byte minute;
    private final int nano;
    private final byte second;

    static {
        int i = 0;
        while (true) {
            LocalTime[] localTimeArr = HOURS;
            if (i < localTimeArr.length) {
                localTimeArr[i] = new LocalTime(i, 0, 0, 0);
                i++;
            } else {
                MIDNIGHT = localTimeArr[0];
                NOON = localTimeArr[12];
                MIN = localTimeArr[0];
                MAX = new LocalTime(23, 59, 59, Year.MAX_VALUE);
                return;
            }
        }
    }

    public static LocalTime now() {
        return now(Clock.systemDefaultZone());
    }

    public static LocalTime now(ZoneId zone) {
        return now(Clock.system(zone));
    }

    public static LocalTime now(Clock clock) {
        Objects.requireNonNull(clock, "clock");
        Instant now = clock.instant();
        ZoneOffset offset = clock.getZone().getRules().getOffset(now);
        long localSecond = now.getEpochSecond() + offset.getTotalSeconds();
        int secsOfDay = (int) Clock$TickClock$$ExternalSyntheticBackport0.m(localSecond, 86400L);
        return ofNanoOfDay((secsOfDay * 1000000000) + now.getNano());
    }

    public static LocalTime of(int hour, int minute) {
        ChronoField.HOUR_OF_DAY.checkValidValue(hour);
        if (minute == 0) {
            return HOURS[hour];
        }
        ChronoField.MINUTE_OF_HOUR.checkValidValue(minute);
        return new LocalTime(hour, minute, 0, 0);
    }

    public static LocalTime of(int hour, int minute, int second) {
        ChronoField.HOUR_OF_DAY.checkValidValue(hour);
        if ((minute | second) == 0) {
            return HOURS[hour];
        }
        ChronoField.MINUTE_OF_HOUR.checkValidValue(minute);
        ChronoField.SECOND_OF_MINUTE.checkValidValue(second);
        return new LocalTime(hour, minute, second, 0);
    }

    public static LocalTime of(int hour, int minute, int second, int nanoOfSecond) {
        ChronoField.HOUR_OF_DAY.checkValidValue(hour);
        ChronoField.MINUTE_OF_HOUR.checkValidValue(minute);
        ChronoField.SECOND_OF_MINUTE.checkValidValue(second);
        ChronoField.NANO_OF_SECOND.checkValidValue(nanoOfSecond);
        return create(hour, minute, second, nanoOfSecond);
    }

    public static LocalTime ofSecondOfDay(long secondOfDay) {
        ChronoField.SECOND_OF_DAY.checkValidValue(secondOfDay);
        int hours = (int) (secondOfDay / 3600);
        long secondOfDay2 = secondOfDay - (hours * SECONDS_PER_HOUR);
        int minutes = (int) (secondOfDay2 / 60);
        return create(hours, minutes, (int) (secondOfDay2 - (minutes * 60)), 0);
    }

    public static LocalTime ofNanoOfDay(long nanoOfDay) {
        ChronoField.NANO_OF_DAY.checkValidValue(nanoOfDay);
        int hours = (int) (nanoOfDay / NANOS_PER_HOUR);
        long nanoOfDay2 = nanoOfDay - (hours * NANOS_PER_HOUR);
        int minutes = (int) (nanoOfDay2 / NANOS_PER_MINUTE);
        long nanoOfDay3 = nanoOfDay2 - (minutes * NANOS_PER_MINUTE);
        int seconds = (int) (nanoOfDay3 / 1000000000);
        return create(hours, minutes, seconds, (int) (nanoOfDay3 - (seconds * 1000000000)));
    }

    public static LocalTime from(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        LocalTime time = (LocalTime) temporal.query(TemporalQueries.localTime());
        if (time == null) {
            throw new DateTimeException("Unable to obtain LocalTime from TemporalAccessor: " + temporal + " of type " + temporal.getClass().getName());
        }
        return time;
    }

    public static LocalTime parse(CharSequence text) {
        return parse(text, DateTimeFormatter.ISO_LOCAL_TIME);
    }

    public static LocalTime parse(CharSequence text, DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return (LocalTime) formatter.parse(text, LocalTime$$ExternalSyntheticLambda1.INSTANCE);
    }

    private static LocalTime create(int hour, int minute, int second, int nanoOfSecond) {
        if ((minute | second | nanoOfSecond) == 0) {
            return HOURS[hour];
        }
        return new LocalTime(hour, minute, second, nanoOfSecond);
    }

    private LocalTime(int hour, int minute, int second, int nanoOfSecond) {
        this.hour = (byte) hour;
        this.minute = (byte) minute;
        this.second = (byte) second;
        this.nano = nanoOfSecond;
    }

    @Override // j$.time.temporal.TemporalAccessor
    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            return field.isTimeBased();
        }
        return field != null && field.isSupportedBy(this);
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
        return TemporalAccessor.CC.$default$range(this, field);
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
            if (field == ChronoField.NANO_OF_DAY) {
                return toNanoOfDay();
            }
            if (field == ChronoField.MICRO_OF_DAY) {
                return toNanoOfDay() / 1000;
            }
            return get0(field);
        }
        return field.getFrom(this);
    }

    private int get0(TemporalField field) {
        switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[((ChronoField) field).ordinal()]) {
            case 1:
                return this.nano;
            case 2:
                throw new UnsupportedTemporalTypeException("Invalid field 'NanoOfDay' for get() method, use getLong() instead");
            case 3:
                return this.nano / 1000;
            case 4:
                throw new UnsupportedTemporalTypeException("Invalid field 'MicroOfDay' for get() method, use getLong() instead");
            case 5:
                return this.nano / MediaController.VIDEO_BITRATE_480;
            case 6:
                return (int) (toNanoOfDay() / 1000000);
            case 7:
                return this.second;
            case 8:
                return toSecondOfDay();
            case 9:
                return this.minute;
            case 10:
                return (this.hour * 60) + this.minute;
            case 11:
                return this.hour % 12;
            case 12:
                int ham = this.hour % 12;
                if (ham % 12 != 0) {
                    return ham;
                }
                return 12;
            case 13:
                return this.hour;
            case 14:
                byte b = this.hour;
                if (b != 0) {
                    return b;
                }
                return 24;
            case 15:
                return this.hour / 12;
            default:
                throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
    }

    public int getHour() {
        return this.hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public int getSecond() {
        return this.second;
    }

    public int getNano() {
        return this.nano;
    }

    @Override // j$.time.temporal.Temporal
    public LocalTime with(TemporalAdjuster adjuster) {
        if (adjuster instanceof LocalTime) {
            return (LocalTime) adjuster;
        }
        return (LocalTime) adjuster.adjustInto(this);
    }

    @Override // j$.time.temporal.Temporal
    public LocalTime with(TemporalField field, long newValue) {
        if (field instanceof ChronoField) {
            ChronoField f = (ChronoField) field;
            f.checkValidValue(newValue);
            long j = 0;
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoField[f.ordinal()]) {
                case 1:
                    return withNano((int) newValue);
                case 2:
                    return ofNanoOfDay(newValue);
                case 3:
                    return withNano(((int) newValue) * 1000);
                case 4:
                    return ofNanoOfDay(1000 * newValue);
                case 5:
                    return withNano(((int) newValue) * MediaController.VIDEO_BITRATE_480);
                case 6:
                    return ofNanoOfDay(1000000 * newValue);
                case 7:
                    return withSecond((int) newValue);
                case 8:
                    return plusSeconds(newValue - toSecondOfDay());
                case 9:
                    return withMinute((int) newValue);
                case 10:
                    return plusMinutes(newValue - ((this.hour * 60) + this.minute));
                case 11:
                    return plusHours(newValue - (this.hour % 12));
                case 12:
                    if (newValue != 12) {
                        j = newValue;
                    }
                    return plusHours(j - (this.hour % 12));
                case 13:
                    return withHour((int) newValue);
                case 14:
                    if (newValue != 24) {
                        j = newValue;
                    }
                    return withHour((int) j);
                case 15:
                    return plusHours((newValue - (this.hour / 12)) * 12);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
            }
        }
        return (LocalTime) field.adjustInto(this, newValue);
    }

    public LocalTime withHour(int hour) {
        if (this.hour == hour) {
            return this;
        }
        ChronoField.HOUR_OF_DAY.checkValidValue(hour);
        return create(hour, this.minute, this.second, this.nano);
    }

    public LocalTime withMinute(int minute) {
        if (this.minute == minute) {
            return this;
        }
        ChronoField.MINUTE_OF_HOUR.checkValidValue(minute);
        return create(this.hour, minute, this.second, this.nano);
    }

    public LocalTime withSecond(int second) {
        if (this.second == second) {
            return this;
        }
        ChronoField.SECOND_OF_MINUTE.checkValidValue(second);
        return create(this.hour, this.minute, second, this.nano);
    }

    public LocalTime withNano(int nanoOfSecond) {
        if (this.nano == nanoOfSecond) {
            return this;
        }
        ChronoField.NANO_OF_SECOND.checkValidValue(nanoOfSecond);
        return create(this.hour, this.minute, this.second, nanoOfSecond);
    }

    public LocalTime truncatedTo(TemporalUnit unit) {
        if (unit == ChronoUnit.NANOS) {
            return this;
        }
        Duration unitDur = unit.getDuration();
        if (unitDur.getSeconds() > 86400) {
            throw new UnsupportedTemporalTypeException("Unit is too large to be used for truncation");
        }
        long dur = unitDur.toNanos();
        if (NANOS_PER_DAY % dur != 0) {
            throw new UnsupportedTemporalTypeException("Unit must divide into a standard day without remainder");
        }
        long nod = toNanoOfDay();
        return ofNanoOfDay((nod / dur) * dur);
    }

    @Override // j$.time.temporal.Temporal
    public LocalTime plus(TemporalAmount amountToAdd) {
        return (LocalTime) amountToAdd.addTo(this);
    }

    /* renamed from: j$.time.LocalTime$1 */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$java$time$temporal$ChronoField;
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
            int[] iArr2 = new int[ChronoField.values().length];
            $SwitchMap$java$time$temporal$ChronoField = iArr2;
            try {
                iArr2[ChronoField.NANO_OF_SECOND.ordinal()] = 1;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.NANO_OF_DAY.ordinal()] = 2;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MICRO_OF_SECOND.ordinal()] = 3;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MICRO_OF_DAY.ordinal()] = 4;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MILLI_OF_SECOND.ordinal()] = 5;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MILLI_OF_DAY.ordinal()] = 6;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.SECOND_OF_MINUTE.ordinal()] = 7;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.SECOND_OF_DAY.ordinal()] = 8;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MINUTE_OF_HOUR.ordinal()] = 9;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.MINUTE_OF_DAY.ordinal()] = 10;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.HOUR_OF_AMPM.ordinal()] = 11;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.CLOCK_HOUR_OF_AMPM.ordinal()] = 12;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.HOUR_OF_DAY.ordinal()] = 13;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.CLOCK_HOUR_OF_DAY.ordinal()] = 14;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$java$time$temporal$ChronoField[ChronoField.AMPM_OF_DAY.ordinal()] = 15;
            } catch (NoSuchFieldError e22) {
            }
        }
    }

    @Override // j$.time.temporal.Temporal
    public LocalTime plus(long amountToAdd, TemporalUnit unit) {
        if (unit instanceof ChronoUnit) {
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
                case 1:
                    return plusNanos(amountToAdd);
                case 2:
                    return plusNanos((amountToAdd % MICROS_PER_DAY) * 1000);
                case 3:
                    return plusNanos((amountToAdd % MILLIS_PER_DAY) * 1000000);
                case 4:
                    return plusSeconds(amountToAdd);
                case 5:
                    return plusMinutes(amountToAdd);
                case 6:
                    return plusHours(amountToAdd);
                case 7:
                    return plusHours((amountToAdd % 2) * 12);
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return (LocalTime) unit.addTo(this, amountToAdd);
    }

    public LocalTime plusHours(long hoursToAdd) {
        if (hoursToAdd == 0) {
            return this;
        }
        int newHour = ((((int) (hoursToAdd % 24)) + this.hour) + 24) % 24;
        return create(newHour, this.minute, this.second, this.nano);
    }

    public LocalTime plusMinutes(long minutesToAdd) {
        if (minutesToAdd == 0) {
            return this;
        }
        int mofd = (this.hour * 60) + this.minute;
        int newMofd = ((((int) (minutesToAdd % 1440)) + mofd) + MINUTES_PER_DAY) % MINUTES_PER_DAY;
        if (mofd == newMofd) {
            return this;
        }
        int newHour = newMofd / 60;
        int newMinute = newMofd % 60;
        return create(newHour, newMinute, this.second, this.nano);
    }

    public LocalTime plusSeconds(long secondstoAdd) {
        if (secondstoAdd == 0) {
            return this;
        }
        int sofd = (this.hour * 3600) + (this.minute * 60) + this.second;
        int newSofd = ((((int) (secondstoAdd % 86400)) + sofd) + SECONDS_PER_DAY) % SECONDS_PER_DAY;
        if (sofd == newSofd) {
            return this;
        }
        int newHour = newSofd / SECONDS_PER_HOUR;
        int newMinute = (newSofd / 60) % 60;
        int newSecond = newSofd % 60;
        return create(newHour, newMinute, newSecond, this.nano);
    }

    public LocalTime plusNanos(long nanosToAdd) {
        if (nanosToAdd == 0) {
            return this;
        }
        long nofd = toNanoOfDay();
        long newNofd = (((nanosToAdd % NANOS_PER_DAY) + nofd) + NANOS_PER_DAY) % NANOS_PER_DAY;
        if (nofd == newNofd) {
            return this;
        }
        int newHour = (int) (newNofd / NANOS_PER_HOUR);
        int newMinute = (int) ((newNofd / NANOS_PER_MINUTE) % 60);
        int newSecond = (int) ((newNofd / 1000000000) % 60);
        int newNano = (int) (newNofd % 1000000000);
        return create(newHour, newMinute, newSecond, newNano);
    }

    @Override // j$.time.temporal.Temporal
    public LocalTime minus(TemporalAmount amountToSubtract) {
        return (LocalTime) amountToSubtract.subtractFrom(this);
    }

    @Override // j$.time.temporal.Temporal
    public LocalTime minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1L, unit) : plus(-amountToSubtract, unit);
    }

    public LocalTime minusHours(long hoursToSubtract) {
        return plusHours(-(hoursToSubtract % 24));
    }

    public LocalTime minusMinutes(long minutesToSubtract) {
        return plusMinutes(-(minutesToSubtract % 1440));
    }

    public LocalTime minusSeconds(long secondsToSubtract) {
        return plusSeconds(-(secondsToSubtract % 86400));
    }

    public LocalTime minusNanos(long nanosToSubtract) {
        return plusNanos(-(nanosToSubtract % NANOS_PER_DAY));
    }

    /* JADX WARN: Generic types in debug info not equals: j$.time.temporal.TemporalQuery != java.time.temporal.TemporalQuery<R> */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // j$.time.temporal.TemporalAccessor
    public <R> R query(TemporalQuery<R> temporalQuery) {
        if (temporalQuery == TemporalQueries.chronology() || temporalQuery == TemporalQueries.zoneId() || temporalQuery == TemporalQueries.zone() || temporalQuery == TemporalQueries.offset()) {
            return null;
        }
        if (temporalQuery == TemporalQueries.localTime()) {
            return this;
        }
        if (temporalQuery == TemporalQueries.localDate()) {
            return null;
        }
        if (temporalQuery == TemporalQueries.precision()) {
            return (R) ChronoUnit.NANOS;
        }
        return temporalQuery.queryFrom(this);
    }

    @Override // j$.time.temporal.TemporalAdjuster
    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.NANO_OF_DAY, toNanoOfDay());
    }

    @Override // j$.time.temporal.Temporal
    public long until(Temporal endExclusive, TemporalUnit unit) {
        LocalTime end = from(endExclusive);
        if (unit instanceof ChronoUnit) {
            long nanosUntil = end.toNanoOfDay() - toNanoOfDay();
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
                case 1:
                    return nanosUntil;
                case 2:
                    return nanosUntil / 1000;
                case 3:
                    return nanosUntil / 1000000;
                case 4:
                    return nanosUntil / 1000000000;
                case 5:
                    return nanosUntil / NANOS_PER_MINUTE;
                case 6:
                    return nanosUntil / NANOS_PER_HOUR;
                case 7:
                    return nanosUntil / 43200000000000L;
                default:
                    throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
            }
        }
        return unit.between(this, end);
    }

    public String format(DateTimeFormatter formatter) {
        Objects.requireNonNull(formatter, "formatter");
        return formatter.format(this);
    }

    public LocalDateTime atDate(LocalDate date) {
        return LocalDateTime.of(date, this);
    }

    public OffsetTime atOffset(ZoneOffset offset) {
        return OffsetTime.of(this, offset);
    }

    public int toSecondOfDay() {
        int total = this.hour * 3600;
        return total + (this.minute * 60) + this.second;
    }

    public long toNanoOfDay() {
        long total = this.hour * NANOS_PER_HOUR;
        return total + (this.minute * NANOS_PER_MINUTE) + (this.second * 1000000000) + this.nano;
    }

    public int compareTo(LocalTime other) {
        int cmp = LocalTime$$ExternalSyntheticBackport0.m(this.hour, other.hour);
        if (cmp == 0) {
            int cmp2 = LocalTime$$ExternalSyntheticBackport0.m(this.minute, other.minute);
            if (cmp2 == 0) {
                int cmp3 = LocalTime$$ExternalSyntheticBackport0.m(this.second, other.second);
                if (cmp3 == 0) {
                    return LocalTime$$ExternalSyntheticBackport0.m(this.nano, other.nano);
                }
                return cmp3;
            }
            return cmp2;
        }
        return cmp;
    }

    public boolean isAfter(LocalTime other) {
        return compareTo(other) > 0;
    }

    public boolean isBefore(LocalTime other) {
        return compareTo(other) < 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof LocalTime)) {
            return false;
        }
        LocalTime other = (LocalTime) obj;
        return this.hour == other.hour && this.minute == other.minute && this.second == other.second && this.nano == other.nano;
    }

    public int hashCode() {
        long nod = toNanoOfDay();
        return (int) ((nod >>> 32) ^ nod);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(18);
        int hourValue = this.hour;
        int minuteValue = this.minute;
        int secondValue = this.second;
        int nanoValue = this.nano;
        buf.append(hourValue < 10 ? "0" : "");
        buf.append(hourValue);
        String str = ":0";
        buf.append(minuteValue < 10 ? str : Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
        buf.append(minuteValue);
        if (secondValue > 0 || nanoValue > 0) {
            if (secondValue >= 10) {
                str = Constants.COMMON_SCHEMA_PREFIX_SEPARATOR;
            }
            buf.append(str);
            buf.append(secondValue);
            if (nanoValue > 0) {
                buf.append('.');
                if (nanoValue % MediaController.VIDEO_BITRATE_480 == 0) {
                    buf.append(Integer.toString((nanoValue / MediaController.VIDEO_BITRATE_480) + 1000).substring(1));
                } else if (nanoValue % 1000 == 0) {
                    buf.append(Integer.toString((nanoValue / 1000) + MediaController.VIDEO_BITRATE_480).substring(1));
                } else {
                    buf.append(Integer.toString(1000000000 + nanoValue).substring(1));
                }
            }
        }
        return buf.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 4, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    public void writeExternal(DataOutput out) {
        if (this.nano == 0) {
            if (this.second == 0) {
                if (this.minute == 0) {
                    out.writeByte(this.hour ^ (-1));
                    return;
                }
                out.writeByte(this.hour);
                out.writeByte(this.minute ^ (-1));
                return;
            }
            out.writeByte(this.hour);
            out.writeByte(this.minute);
            out.writeByte(this.second ^ (-1));
            return;
        }
        out.writeByte(this.hour);
        out.writeByte(this.minute);
        out.writeByte(this.second);
        out.writeInt(this.nano);
    }

    public static LocalTime readExternal(DataInput in) {
        int hour = in.readByte();
        int minute = 0;
        int second = 0;
        int nano = 0;
        if (hour < 0) {
            hour ^= -1;
        } else {
            minute = in.readByte();
            if (minute < 0) {
                minute ^= -1;
            } else {
                second = in.readByte();
                if (second < 0) {
                    second ^= -1;
                } else {
                    nano = in.readInt();
                }
            }
        }
        return of(hour, minute, second, nano);
    }
}
