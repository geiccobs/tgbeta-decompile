package j$.time;

import j$.time.chrono.ChronoLocalDate;
import j$.time.chrono.ChronoPeriod;
import j$.time.chrono.Chronology;
import j$.time.chrono.IsoChronology;
import j$.time.format.DateTimeParseException;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes2.dex */
public final class Period implements ChronoPeriod, Serializable {
    private static final long serialVersionUID = -3587258372562876L;
    private final int days;
    private final int months;
    private final int years;
    public static final Period ZERO = new Period(0, 0, 0);
    private static final Pattern PATTERN = Pattern.compile("([-+]?)P(?:([-+]?[0-9]+)Y)?(?:([-+]?[0-9]+)M)?(?:([-+]?[0-9]+)W)?(?:([-+]?[0-9]+)D)?", 2);
    private static final List<TemporalUnit> SUPPORTED_UNITS = Collections.unmodifiableList(Arrays.asList(ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS));

    public static Period ofYears(int years) {
        return create(years, 0, 0);
    }

    public static Period ofMonths(int months) {
        return create(0, months, 0);
    }

    public static Period ofWeeks(int weeks) {
        return create(0, 0, Period$$ExternalSyntheticBackport2.m(weeks, 7));
    }

    public static Period ofDays(int days) {
        return create(0, 0, days);
    }

    public static Period of(int years, int months, int days) {
        return create(years, months, days);
    }

    public static Period from(TemporalAmount amount) {
        if (amount instanceof Period) {
            return (Period) amount;
        }
        if ((amount instanceof ChronoPeriod) && !IsoChronology.INSTANCE.equals(((ChronoPeriod) amount).getChronology())) {
            throw new DateTimeException("Period requires ISO chronology: " + amount);
        }
        Objects.requireNonNull(amount, "amount");
        int years = 0;
        int months = 0;
        int days = 0;
        for (TemporalUnit unit : amount.getUnits()) {
            long unitAmount = amount.get(unit);
            if (unit == ChronoUnit.YEARS) {
                years = LocalDate$$ExternalSyntheticBackport0.m(unitAmount);
            } else if (unit == ChronoUnit.MONTHS) {
                months = LocalDate$$ExternalSyntheticBackport0.m(unitAmount);
            } else if (unit == ChronoUnit.DAYS) {
                days = LocalDate$$ExternalSyntheticBackport0.m(unitAmount);
            } else {
                throw new DateTimeException("Unit must be Years, Months or Days, but was " + unit);
            }
        }
        return create(years, months, days);
    }

    public static Period parse(CharSequence text) {
        Objects.requireNonNull(text, "text");
        Matcher matcher = PATTERN.matcher(text);
        if (matcher.matches()) {
            int negate = 1;
            if ("-".equals(matcher.group(1))) {
                negate = -1;
            }
            String yearMatch = matcher.group(2);
            String monthMatch = matcher.group(3);
            String weekMatch = matcher.group(4);
            String dayMatch = matcher.group(5);
            if (yearMatch != null || monthMatch != null || dayMatch != null || weekMatch != null) {
                try {
                    int years = parseNumber(text, yearMatch, negate);
                    int months = parseNumber(text, monthMatch, negate);
                    int weeks = parseNumber(text, weekMatch, negate);
                    int days = parseNumber(text, dayMatch, negate);
                    return create(years, months, Period$$ExternalSyntheticBackport0.m(days, Period$$ExternalSyntheticBackport2.m(weeks, 7)));
                } catch (NumberFormatException ex) {
                    throw new DateTimeParseException("Text cannot be parsed to a Period", text, 0, ex);
                }
            }
        }
        throw new DateTimeParseException("Text cannot be parsed to a Period", text, 0);
    }

    private static int parseNumber(CharSequence text, String str, int negate) {
        if (str == null) {
            return 0;
        }
        if (str.charAt(0) == '+') {
            str = str.substring(1);
        }
        int val = Integer.parseInt(str);
        try {
            return Period$$ExternalSyntheticBackport2.m(val, negate);
        } catch (ArithmeticException ex) {
            throw new DateTimeParseException("Text cannot be parsed to a Period", text, 0, ex);
        }
    }

    public static Period between(LocalDate startDateInclusive, LocalDate endDateExclusive) {
        return startDateInclusive.until((ChronoLocalDate) endDateExclusive);
    }

    private static Period create(int years, int months, int days) {
        if ((years | months | days) == 0) {
            return ZERO;
        }
        return new Period(years, months, days);
    }

    private Period(int years, int months, int days) {
        this.years = years;
        this.months = months;
        this.days = days;
    }

    @Override // j$.time.chrono.ChronoPeriod, j$.time.temporal.TemporalAmount
    public long get(TemporalUnit unit) {
        if (unit == ChronoUnit.YEARS) {
            return getYears();
        }
        if (unit == ChronoUnit.MONTHS) {
            return getMonths();
        }
        if (unit == ChronoUnit.DAYS) {
            return getDays();
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    @Override // j$.time.chrono.ChronoPeriod, j$.time.temporal.TemporalAmount
    public List<TemporalUnit> getUnits() {
        return SUPPORTED_UNITS;
    }

    @Override // j$.time.chrono.ChronoPeriod
    public IsoChronology getChronology() {
        return IsoChronology.INSTANCE;
    }

    @Override // j$.time.chrono.ChronoPeriod
    public boolean isZero() {
        return this == ZERO;
    }

    @Override // j$.time.chrono.ChronoPeriod
    public boolean isNegative() {
        return this.years < 0 || this.months < 0 || this.days < 0;
    }

    public int getYears() {
        return this.years;
    }

    public int getMonths() {
        return this.months;
    }

    public int getDays() {
        return this.days;
    }

    public Period withYears(int years) {
        if (years == this.years) {
            return this;
        }
        return create(years, this.months, this.days);
    }

    public Period withMonths(int months) {
        if (months == this.months) {
            return this;
        }
        return create(this.years, months, this.days);
    }

    public Period withDays(int days) {
        if (days == this.days) {
            return this;
        }
        return create(this.years, this.months, days);
    }

    @Override // j$.time.chrono.ChronoPeriod
    public Period plus(TemporalAmount amountToAdd) {
        Period isoAmount = from(amountToAdd);
        return create(Period$$ExternalSyntheticBackport0.m(this.years, isoAmount.years), Period$$ExternalSyntheticBackport0.m(this.months, isoAmount.months), Period$$ExternalSyntheticBackport0.m(this.days, isoAmount.days));
    }

    public Period plusYears(long yearsToAdd) {
        if (yearsToAdd == 0) {
            return this;
        }
        return create(LocalDate$$ExternalSyntheticBackport0.m(Clock$OffsetClock$$ExternalSyntheticBackport0.m(this.years, yearsToAdd)), this.months, this.days);
    }

    public Period plusMonths(long monthsToAdd) {
        if (monthsToAdd == 0) {
            return this;
        }
        return create(this.years, LocalDate$$ExternalSyntheticBackport0.m(Clock$OffsetClock$$ExternalSyntheticBackport0.m(this.months, monthsToAdd)), this.days);
    }

    public Period plusDays(long daysToAdd) {
        if (daysToAdd == 0) {
            return this;
        }
        return create(this.years, this.months, LocalDate$$ExternalSyntheticBackport0.m(Clock$OffsetClock$$ExternalSyntheticBackport0.m(this.days, daysToAdd)));
    }

    @Override // j$.time.chrono.ChronoPeriod
    public Period minus(TemporalAmount amountToSubtract) {
        Period isoAmount = from(amountToSubtract);
        return create(Period$$ExternalSyntheticBackport1.m(this.years, isoAmount.years), Period$$ExternalSyntheticBackport1.m(this.months, isoAmount.months), Period$$ExternalSyntheticBackport1.m(this.days, isoAmount.days));
    }

    public Period minusYears(long yearsToSubtract) {
        return yearsToSubtract == Long.MIN_VALUE ? plusYears(Long.MAX_VALUE).plusYears(1L) : plusYears(-yearsToSubtract);
    }

    public Period minusMonths(long monthsToSubtract) {
        return monthsToSubtract == Long.MIN_VALUE ? plusMonths(Long.MAX_VALUE).plusMonths(1L) : plusMonths(-monthsToSubtract);
    }

    public Period minusDays(long daysToSubtract) {
        return daysToSubtract == Long.MIN_VALUE ? plusDays(Long.MAX_VALUE).plusDays(1L) : plusDays(-daysToSubtract);
    }

    @Override // j$.time.chrono.ChronoPeriod
    public Period multipliedBy(int scalar) {
        if (this == ZERO || scalar == 1) {
            return this;
        }
        return create(Period$$ExternalSyntheticBackport2.m(this.years, scalar), Period$$ExternalSyntheticBackport2.m(this.months, scalar), Period$$ExternalSyntheticBackport2.m(this.days, scalar));
    }

    @Override // j$.time.chrono.ChronoPeriod
    public Period negated() {
        return multipliedBy(-1);
    }

    @Override // j$.time.chrono.ChronoPeriod
    public Period normalized() {
        long totalMonths = toTotalMonths();
        long splitYears = totalMonths / 12;
        int splitMonths = (int) (totalMonths % 12);
        if (splitYears == this.years && splitMonths == this.months) {
            return this;
        }
        return create(LocalDate$$ExternalSyntheticBackport0.m(splitYears), splitMonths, this.days);
    }

    public long toTotalMonths() {
        return (this.years * 12) + this.months;
    }

    @Override // j$.time.chrono.ChronoPeriod, j$.time.temporal.TemporalAmount
    public Temporal addTo(Temporal temporal) {
        validateChrono(temporal);
        if (this.months == 0) {
            int i = this.years;
            if (i != 0) {
                temporal = temporal.plus(i, ChronoUnit.YEARS);
            }
        } else {
            long totalMonths = toTotalMonths();
            if (totalMonths != 0) {
                temporal = temporal.plus(totalMonths, ChronoUnit.MONTHS);
            }
        }
        int i2 = this.days;
        if (i2 != 0) {
            return temporal.plus(i2, ChronoUnit.DAYS);
        }
        return temporal;
    }

    @Override // j$.time.chrono.ChronoPeriod, j$.time.temporal.TemporalAmount
    public Temporal subtractFrom(Temporal temporal) {
        validateChrono(temporal);
        if (this.months == 0) {
            int i = this.years;
            if (i != 0) {
                temporal = temporal.minus(i, ChronoUnit.YEARS);
            }
        } else {
            long totalMonths = toTotalMonths();
            if (totalMonths != 0) {
                temporal = temporal.minus(totalMonths, ChronoUnit.MONTHS);
            }
        }
        int i2 = this.days;
        if (i2 != 0) {
            return temporal.minus(i2, ChronoUnit.DAYS);
        }
        return temporal;
    }

    private void validateChrono(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        Chronology temporalChrono = (Chronology) temporal.query(TemporalQueries.chronology());
        if (temporalChrono != null && !IsoChronology.INSTANCE.equals(temporalChrono)) {
            throw new DateTimeException("Chronology mismatch, expected: ISO, actual: " + temporalChrono.getId());
        }
    }

    @Override // j$.time.chrono.ChronoPeriod
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Period)) {
            return false;
        }
        Period other = (Period) obj;
        return this.years == other.years && this.months == other.months && this.days == other.days;
    }

    @Override // j$.time.chrono.ChronoPeriod
    public int hashCode() {
        return this.years + Integer.rotateLeft(this.months, 8) + Integer.rotateLeft(this.days, 16);
    }

    @Override // j$.time.chrono.ChronoPeriod
    public String toString() {
        if (this == ZERO) {
            return "P0D";
        }
        StringBuilder buf = new StringBuilder();
        buf.append('P');
        int i = this.years;
        if (i != 0) {
            buf.append(i);
            buf.append('Y');
        }
        int i2 = this.months;
        if (i2 != 0) {
            buf.append(i2);
            buf.append('M');
        }
        int i3 = this.days;
        if (i3 != 0) {
            buf.append(i3);
            buf.append('D');
        }
        return buf.toString();
    }

    private Object writeReplace() {
        return new Ser((byte) 14, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    public void writeExternal(DataOutput out) {
        out.writeInt(this.years);
        out.writeInt(this.months);
        out.writeInt(this.days);
    }

    public static Period readExternal(DataInput in) {
        int years = in.readInt();
        int months = in.readInt();
        int days = in.readInt();
        return of(years, months, days);
    }
}
