package j$.time;

import androidx.exifinterface.media.ExifInterface;
import com.google.android.exoplayer2.C;
import j$.time.format.DateTimeParseException;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.MediaController;
/* loaded from: classes2.dex */
public final class Duration implements TemporalAmount, Comparable<Duration>, Serializable {
    private static final long serialVersionUID = 3078945930695997490L;
    private final int nanos;
    private final long seconds;
    public static final Duration ZERO = new Duration(0, 0);
    private static final BigInteger BI_NANOS_PER_SECOND = BigInteger.valueOf(C.NANOS_PER_SECOND);
    private static final Pattern PATTERN = Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)D)?(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?", 2);

    public static Duration ofDays(long days) {
        return create(Duration$$ExternalSyntheticBackport1.m(days, 86400L), 0);
    }

    public static Duration ofHours(long hours) {
        return create(Duration$$ExternalSyntheticBackport1.m(hours, 3600L), 0);
    }

    public static Duration ofMinutes(long minutes) {
        return create(Duration$$ExternalSyntheticBackport1.m(minutes, 60L), 0);
    }

    public static Duration ofSeconds(long seconds) {
        return create(seconds, 0);
    }

    public static Duration ofSeconds(long seconds, long nanoAdjustment) {
        long secs = Clock$OffsetClock$$ExternalSyntheticBackport0.m(seconds, Duration$$ExternalSyntheticBackport0.m(nanoAdjustment, C.NANOS_PER_SECOND));
        int nos = (int) Clock$TickClock$$ExternalSyntheticBackport0.m(nanoAdjustment, C.NANOS_PER_SECOND);
        return create(secs, nos);
    }

    public static Duration ofMillis(long millis) {
        long secs = millis / 1000;
        int mos = (int) (millis % 1000);
        if (mos < 0) {
            mos += 1000;
            secs--;
        }
        return create(secs, MediaController.VIDEO_BITRATE_480 * mos);
    }

    public static Duration ofNanos(long nanos) {
        long secs = nanos / C.NANOS_PER_SECOND;
        int nos = (int) (nanos % C.NANOS_PER_SECOND);
        if (nos < 0) {
            nos = (int) (nos + C.NANOS_PER_SECOND);
            secs--;
        }
        return create(secs, nos);
    }

    public static Duration of(long amount, TemporalUnit unit) {
        return ZERO.plus(amount, unit);
    }

    public static Duration from(TemporalAmount amount) {
        Objects.requireNonNull(amount, "amount");
        Duration duration = ZERO;
        for (TemporalUnit unit : amount.getUnits()) {
            duration = duration.plus(amount.get(unit), unit);
        }
        return duration;
    }

    public static Duration parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.matches() && !ExifInterface.GPS_DIRECTION_TRUE.equals(matcher.group(3))) {
            int i = 1;
            boolean negate = "-".equals(matcher.group(1));
            String dayMatch = matcher.group(2);
            String hourMatch = matcher.group(4);
            String minuteMatch = matcher.group(5);
            String secondMatch = matcher.group(6);
            String fractionMatch = matcher.group(7);
            if (dayMatch != null || hourMatch != null || minuteMatch != null || secondMatch != null) {
                long daysAsSecs = parseNumber(text, dayMatch, 86400, "days");
                long hoursAsSecs = parseNumber(text, hourMatch, 3600, "hours");
                long minsAsSecs = parseNumber(text, minuteMatch, 60, "minutes");
                long seconds = parseNumber(text, secondMatch, 1, "seconds");
                if (seconds < 0) {
                    i = -1;
                }
                int nanos = parseFraction(text, fractionMatch, i);
                try {
                    return create(negate, daysAsSecs, hoursAsSecs, minsAsSecs, seconds, nanos);
                } catch (ArithmeticException ex) {
                    throw ((DateTimeParseException) new DateTimeParseException("Text cannot be parsed to a Duration: overflow", text, 0).initCause(ex));
                }
            }
        }
        throw new DateTimeParseException("Text cannot be parsed to a Duration", text, 0);
    }

    private static long parseNumber(CharSequence text, String parsed, int multiplier, String errorText) {
        if (parsed == null) {
            return 0L;
        }
        if (parsed.charAt(0) == '+') {
            parsed = parsed.substring(1);
        }
        try {
            long val = Long.parseLong(parsed);
            return Duration$$ExternalSyntheticBackport1.m(val, multiplier);
        } catch (ArithmeticException | NumberFormatException ex) {
            throw ((DateTimeParseException) new DateTimeParseException("Text cannot be parsed to a Duration: " + errorText, text, 0).initCause(ex));
        }
    }

    private static int parseFraction(CharSequence text, String parsed, int negate) {
        if (parsed == null || parsed.length() == 0) {
            return 0;
        }
        try {
            return Integer.parseInt((parsed + "000000000").substring(0, 9)) * negate;
        } catch (ArithmeticException | NumberFormatException ex) {
            throw ((DateTimeParseException) new DateTimeParseException("Text cannot be parsed to a Duration: fraction", text, 0).initCause(ex));
        }
    }

    private static Duration create(boolean negate, long daysAsSecs, long hoursAsSecs, long minsAsSecs, long secs, int nanos) {
        long seconds = Clock$OffsetClock$$ExternalSyntheticBackport0.m(daysAsSecs, Clock$OffsetClock$$ExternalSyntheticBackport0.m(hoursAsSecs, Clock$OffsetClock$$ExternalSyntheticBackport0.m(minsAsSecs, secs)));
        if (negate) {
            return ofSeconds(seconds, nanos).negated();
        }
        return ofSeconds(seconds, nanos);
    }

    public static Duration between(Temporal startInclusive, Temporal endExclusive) {
        long nanos;
        try {
            return ofNanos(startInclusive.until(endExclusive, ChronoUnit.NANOS));
        } catch (DateTimeException | ArithmeticException e) {
            long secs = startInclusive.until(endExclusive, ChronoUnit.SECONDS);
            try {
                nanos = endExclusive.getLong(ChronoField.NANO_OF_SECOND) - startInclusive.getLong(ChronoField.NANO_OF_SECOND);
                if (secs > 0 && nanos < 0) {
                    secs++;
                } else if (secs < 0 && nanos > 0) {
                    secs--;
                }
            } catch (DateTimeException e2) {
                nanos = 0;
            }
            return ofSeconds(secs, nanos);
        }
    }

    private static Duration create(long seconds, int nanoAdjustment) {
        if ((nanoAdjustment | seconds) == 0) {
            return ZERO;
        }
        return new Duration(seconds, nanoAdjustment);
    }

    private Duration(long seconds, int nanos) {
        this.seconds = seconds;
        this.nanos = nanos;
    }

    @Override // j$.time.temporal.TemporalAmount
    public long get(TemporalUnit unit) {
        if (unit == ChronoUnit.SECONDS) {
            return this.seconds;
        }
        if (unit == ChronoUnit.NANOS) {
            return this.nanos;
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    @Override // j$.time.temporal.TemporalAmount
    public List<TemporalUnit> getUnits() {
        return DurationUnits.UNITS;
    }

    /* loaded from: classes2.dex */
    private static class DurationUnits {
        static final List<TemporalUnit> UNITS = Collections.unmodifiableList(Arrays.asList(ChronoUnit.SECONDS, ChronoUnit.NANOS));

        private DurationUnits() {
        }
    }

    public boolean isZero() {
        return (this.seconds | ((long) this.nanos)) == 0;
    }

    public boolean isNegative() {
        return this.seconds < 0;
    }

    public long getSeconds() {
        return this.seconds;
    }

    public int getNano() {
        return this.nanos;
    }

    public Duration withSeconds(long seconds) {
        return create(seconds, this.nanos);
    }

    public Duration withNanos(int nanoOfSecond) {
        ChronoField.NANO_OF_SECOND.checkValidIntValue(nanoOfSecond);
        return create(this.seconds, nanoOfSecond);
    }

    public Duration plus(Duration duration) {
        return plus(duration.getSeconds(), duration.getNano());
    }

    public Duration plus(long amountToAdd, TemporalUnit unit) {
        Objects.requireNonNull(unit, "unit");
        if (unit == ChronoUnit.DAYS) {
            return plus(Duration$$ExternalSyntheticBackport1.m(amountToAdd, 86400L), 0L);
        }
        if (unit.isDurationEstimated()) {
            throw new UnsupportedTemporalTypeException("Unit must not have an estimated duration");
        }
        if (amountToAdd == 0) {
            return this;
        }
        if (unit instanceof ChronoUnit) {
            switch (AnonymousClass1.$SwitchMap$java$time$temporal$ChronoUnit[((ChronoUnit) unit).ordinal()]) {
                case 1:
                    return plusNanos(amountToAdd);
                case 2:
                    return plusSeconds((amountToAdd / C.NANOS_PER_SECOND) * 1000).plusNanos((amountToAdd % C.NANOS_PER_SECOND) * 1000);
                case 3:
                    return plusMillis(amountToAdd);
                case 4:
                    return plusSeconds(amountToAdd);
                default:
                    return plusSeconds(Duration$$ExternalSyntheticBackport1.m(unit.getDuration().seconds, amountToAdd));
            }
        }
        Duration duration = unit.getDuration().multipliedBy(amountToAdd);
        return plusSeconds(duration.getSeconds()).plusNanos(duration.getNano());
    }

    /* renamed from: j$.time.Duration$1 */
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
        }
    }

    public Duration plusDays(long daysToAdd) {
        return plus(Duration$$ExternalSyntheticBackport1.m(daysToAdd, 86400L), 0L);
    }

    public Duration plusHours(long hoursToAdd) {
        return plus(Duration$$ExternalSyntheticBackport1.m(hoursToAdd, 3600L), 0L);
    }

    public Duration plusMinutes(long minutesToAdd) {
        return plus(Duration$$ExternalSyntheticBackport1.m(minutesToAdd, 60L), 0L);
    }

    public Duration plusSeconds(long secondsToAdd) {
        return plus(secondsToAdd, 0L);
    }

    public Duration plusMillis(long millisToAdd) {
        return plus(millisToAdd / 1000, (millisToAdd % 1000) * 1000000);
    }

    public Duration plusNanos(long nanosToAdd) {
        return plus(0L, nanosToAdd);
    }

    private Duration plus(long secondsToAdd, long nanosToAdd) {
        if ((secondsToAdd | nanosToAdd) == 0) {
            return this;
        }
        long epochSec = Clock$OffsetClock$$ExternalSyntheticBackport0.m(this.seconds, secondsToAdd);
        long epochSec2 = Clock$OffsetClock$$ExternalSyntheticBackport0.m(epochSec, nanosToAdd / C.NANOS_PER_SECOND);
        long nanoAdjustment = this.nanos + (nanosToAdd % C.NANOS_PER_SECOND);
        return ofSeconds(epochSec2, nanoAdjustment);
    }

    public Duration minus(Duration duration) {
        long secsToSubtract = duration.getSeconds();
        int nanosToSubtract = duration.getNano();
        if (secsToSubtract == Long.MIN_VALUE) {
            return plus(Long.MAX_VALUE, -nanosToSubtract).plus(1L, 0L);
        }
        return plus(-secsToSubtract, -nanosToSubtract);
    }

    public Duration minus(long amountToSubtract, TemporalUnit unit) {
        return amountToSubtract == Long.MIN_VALUE ? plus(Long.MAX_VALUE, unit).plus(1L, unit) : plus(-amountToSubtract, unit);
    }

    public Duration minusDays(long daysToSubtract) {
        return daysToSubtract == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1L) : plusDays(-daysToSubtract);
    }

    public Duration minusHours(long hoursToSubtract) {
        return hoursToSubtract == Long.MIN_VALUE ? plusHours(Long.MAX_VALUE).plusHours(1L) : plusHours(-hoursToSubtract);
    }

    public Duration minusMinutes(long minutesToSubtract) {
        return minutesToSubtract == Long.MIN_VALUE ? plusMinutes(Long.MAX_VALUE).plusMinutes(1L) : plusMinutes(-minutesToSubtract);
    }

    public Duration minusSeconds(long secondsToSubtract) {
        return secondsToSubtract == Long.MIN_VALUE ? plusSeconds(Long.MAX_VALUE).plusSeconds(1L) : plusSeconds(-secondsToSubtract);
    }

    public Duration minusMillis(long millisToSubtract) {
        return millisToSubtract == Long.MIN_VALUE ? plusMillis(Long.MAX_VALUE).plusMillis(1L) : plusMillis(-millisToSubtract);
    }

    public Duration minusNanos(long nanosToSubtract) {
        return nanosToSubtract == Long.MIN_VALUE ? plusNanos(Long.MAX_VALUE).plusNanos(1L) : plusNanos(-nanosToSubtract);
    }

    public Duration multipliedBy(long multiplicand) {
        if (multiplicand == 0) {
            return ZERO;
        }
        if (multiplicand == 1) {
            return this;
        }
        return create(toSeconds().multiply(BigDecimal.valueOf(multiplicand)));
    }

    public Duration dividedBy(long divisor) {
        if (divisor == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        }
        if (divisor == 1) {
            return this;
        }
        return create(toSeconds().divide(BigDecimal.valueOf(divisor), RoundingMode.DOWN));
    }

    private BigDecimal toSeconds() {
        return BigDecimal.valueOf(this.seconds).add(BigDecimal.valueOf(this.nanos, 9));
    }

    private static Duration create(BigDecimal seconds) {
        BigInteger nanos = seconds.movePointRight(9).toBigIntegerExact();
        BigInteger[] divRem = nanos.divideAndRemainder(BI_NANOS_PER_SECOND);
        if (divRem[0].bitLength() > 63) {
            throw new ArithmeticException("Exceeds capacity of Duration: " + nanos);
        }
        return ofSeconds(divRem[0].longValue(), divRem[1].intValue());
    }

    public Duration negated() {
        return multipliedBy(-1L);
    }

    public Duration abs() {
        return isNegative() ? negated() : this;
    }

    @Override // j$.time.temporal.TemporalAmount
    public Temporal addTo(Temporal temporal) {
        long j = this.seconds;
        if (j != 0) {
            temporal = temporal.plus(j, ChronoUnit.SECONDS);
        }
        int i = this.nanos;
        if (i != 0) {
            return temporal.plus(i, ChronoUnit.NANOS);
        }
        return temporal;
    }

    @Override // j$.time.temporal.TemporalAmount
    public Temporal subtractFrom(Temporal temporal) {
        long j = this.seconds;
        if (j != 0) {
            temporal = temporal.minus(j, ChronoUnit.SECONDS);
        }
        int i = this.nanos;
        if (i != 0) {
            return temporal.minus(i, ChronoUnit.NANOS);
        }
        return temporal;
    }

    public long toDays() {
        return this.seconds / 86400;
    }

    public long toHours() {
        return this.seconds / 3600;
    }

    public long toMinutes() {
        return this.seconds / 60;
    }

    public long toMillis() {
        long millis = Duration$$ExternalSyntheticBackport1.m(this.seconds, 1000L);
        return Clock$OffsetClock$$ExternalSyntheticBackport0.m(millis, this.nanos / MediaController.VIDEO_BITRATE_480);
    }

    public long toNanos() {
        long totalNanos = Duration$$ExternalSyntheticBackport1.m(this.seconds, C.NANOS_PER_SECOND);
        return Clock$OffsetClock$$ExternalSyntheticBackport0.m(totalNanos, this.nanos);
    }

    public int compareTo(Duration otherDuration) {
        int cmp = (this.seconds > otherDuration.seconds ? 1 : (this.seconds == otherDuration.seconds ? 0 : -1));
        if (cmp != 0) {
            return cmp;
        }
        return this.nanos - otherDuration.nanos;
    }

    public boolean equals(Object otherDuration) {
        if (this == otherDuration) {
            return true;
        }
        if (!(otherDuration instanceof Duration)) {
            return false;
        }
        Duration other = (Duration) otherDuration;
        return this.seconds == other.seconds && this.nanos == other.nanos;
    }

    public int hashCode() {
        long j = this.seconds;
        return ((int) (j ^ (j >>> 32))) + (this.nanos * 51);
    }

    public String toString() {
        if (this == ZERO) {
            return "PT0S";
        }
        long j = this.seconds;
        long hours = j / 3600;
        int minutes = (int) ((j % 3600) / 60);
        int secs = (int) (j % 60);
        StringBuilder buf = new StringBuilder(24);
        buf.append("PT");
        if (hours != 0) {
            buf.append(hours);
            buf.append('H');
        }
        if (minutes != 0) {
            buf.append(minutes);
            buf.append('M');
        }
        if (secs == 0 && this.nanos == 0 && buf.length() > 2) {
            return buf.toString();
        }
        if (secs < 0 && this.nanos > 0) {
            if (secs == -1) {
                buf.append("-0");
            } else {
                buf.append(secs + 1);
            }
        } else {
            buf.append(secs);
        }
        if (this.nanos > 0) {
            int pos = buf.length();
            if (secs < 0) {
                buf.append(2000000000 - this.nanos);
            } else {
                buf.append(this.nanos + C.NANOS_PER_SECOND);
            }
            while (buf.charAt(buf.length() - 1) == '0') {
                buf.setLength(buf.length() - 1);
            }
            buf.setCharAt(pos, '.');
        }
        buf.append('S');
        return buf.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 1, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    public void writeExternal(DataOutput out) {
        out.writeLong(this.seconds);
        out.writeInt(this.nanos);
    }

    public static Duration readExternal(DataInput in) {
        long seconds = in.readLong();
        int nanos = in.readInt();
        return ofSeconds(seconds, nanos);
    }
}
