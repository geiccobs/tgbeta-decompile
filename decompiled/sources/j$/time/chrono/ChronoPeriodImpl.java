package j$.time.chrono;

import j$.time.DateTimeException;
import j$.time.LocalDate$$ExternalSyntheticBackport0;
import j$.time.Period$$ExternalSyntheticBackport0;
import j$.time.Period$$ExternalSyntheticBackport1;
import j$.time.Period$$ExternalSyntheticBackport2;
import j$.time.chrono.Chronology;
import j$.time.temporal.ChronoField;
import j$.time.temporal.ChronoUnit;
import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalQueries;
import j$.time.temporal.TemporalUnit;
import j$.time.temporal.UnsupportedTemporalTypeException;
import j$.time.temporal.ValueRange;
import j$.util.Objects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
/* loaded from: classes2.dex */
public final class ChronoPeriodImpl implements ChronoPeriod, Serializable {
    private static final List<TemporalUnit> SUPPORTED_UNITS = Collections.unmodifiableList(Arrays.asList(ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS));
    private static final long serialVersionUID = 57387258289L;
    private final Chronology chrono;
    final int days;
    final int months;
    final int years;

    @Override // j$.time.chrono.ChronoPeriod
    public /* synthetic */ ChronoPeriod negated() {
        ChronoPeriod multipliedBy;
        multipliedBy = multipliedBy(-1);
        return multipliedBy;
    }

    public ChronoPeriodImpl(Chronology chrono, int years, int months, int days) {
        Objects.requireNonNull(chrono, "chrono");
        this.chrono = chrono;
        this.years = years;
        this.months = months;
        this.days = days;
    }

    @Override // j$.time.chrono.ChronoPeriod, j$.time.temporal.TemporalAmount
    public long get(TemporalUnit unit) {
        if (unit == ChronoUnit.YEARS) {
            return this.years;
        }
        if (unit == ChronoUnit.MONTHS) {
            return this.months;
        }
        if (unit == ChronoUnit.DAYS) {
            return this.days;
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    @Override // j$.time.chrono.ChronoPeriod, j$.time.temporal.TemporalAmount
    public List<TemporalUnit> getUnits() {
        return SUPPORTED_UNITS;
    }

    @Override // j$.time.chrono.ChronoPeriod
    public Chronology getChronology() {
        return this.chrono;
    }

    @Override // j$.time.chrono.ChronoPeriod
    public boolean isZero() {
        return this.years == 0 && this.months == 0 && this.days == 0;
    }

    @Override // j$.time.chrono.ChronoPeriod
    public boolean isNegative() {
        return this.years < 0 || this.months < 0 || this.days < 0;
    }

    @Override // j$.time.chrono.ChronoPeriod
    public ChronoPeriod plus(TemporalAmount amountToAdd) {
        ChronoPeriodImpl amount = validateAmount(amountToAdd);
        return new ChronoPeriodImpl(this.chrono, Period$$ExternalSyntheticBackport0.m(this.years, amount.years), Period$$ExternalSyntheticBackport0.m(this.months, amount.months), Period$$ExternalSyntheticBackport0.m(this.days, amount.days));
    }

    @Override // j$.time.chrono.ChronoPeriod
    public ChronoPeriod minus(TemporalAmount amountToSubtract) {
        ChronoPeriodImpl amount = validateAmount(amountToSubtract);
        return new ChronoPeriodImpl(this.chrono, Period$$ExternalSyntheticBackport1.m(this.years, amount.years), Period$$ExternalSyntheticBackport1.m(this.months, amount.months), Period$$ExternalSyntheticBackport1.m(this.days, amount.days));
    }

    private ChronoPeriodImpl validateAmount(TemporalAmount amount) {
        Objects.requireNonNull(amount, "amount");
        if (!(amount instanceof ChronoPeriodImpl)) {
            throw new DateTimeException("Unable to obtain ChronoPeriod from TemporalAmount: " + amount.getClass());
        }
        ChronoPeriodImpl period = (ChronoPeriodImpl) amount;
        if (!this.chrono.equals(period.getChronology())) {
            throw new ClassCastException("Chronology mismatch, expected: " + this.chrono.getId() + ", actual: " + period.getChronology().getId());
        }
        return period;
    }

    @Override // j$.time.chrono.ChronoPeriod
    public ChronoPeriod multipliedBy(int scalar) {
        if (isZero() || scalar == 1) {
            return this;
        }
        return new ChronoPeriodImpl(this.chrono, Period$$ExternalSyntheticBackport2.m(this.years, scalar), Period$$ExternalSyntheticBackport2.m(this.months, scalar), Period$$ExternalSyntheticBackport2.m(this.days, scalar));
    }

    @Override // j$.time.chrono.ChronoPeriod
    public ChronoPeriod normalized() {
        long monthRange = monthRange();
        if (monthRange > 0) {
            long j = this.years;
            int i = this.months;
            long totalMonths = (j * monthRange) + i;
            long splitYears = totalMonths / monthRange;
            int splitMonths = (int) (totalMonths % monthRange);
            if (splitYears == j && splitMonths == i) {
                return this;
            }
            return new ChronoPeriodImpl(this.chrono, LocalDate$$ExternalSyntheticBackport0.m(splitYears), splitMonths, this.days);
        }
        return this;
    }

    private long monthRange() {
        ValueRange startRange = this.chrono.range(ChronoField.MONTH_OF_YEAR);
        if (startRange.isFixed() && startRange.isIntValue()) {
            return (startRange.getMaximum() - startRange.getMinimum()) + 1;
        }
        return -1L;
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
            long monthRange = monthRange();
            if (monthRange > 0) {
                temporal = temporal.plus((this.years * monthRange) + this.months, ChronoUnit.MONTHS);
            } else {
                int i2 = this.years;
                if (i2 != 0) {
                    temporal = temporal.plus(i2, ChronoUnit.YEARS);
                }
                temporal = temporal.plus(this.months, ChronoUnit.MONTHS);
            }
        }
        int i3 = this.days;
        if (i3 != 0) {
            return temporal.plus(i3, ChronoUnit.DAYS);
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
            long monthRange = monthRange();
            if (monthRange > 0) {
                temporal = temporal.minus((this.years * monthRange) + this.months, ChronoUnit.MONTHS);
            } else {
                int i2 = this.years;
                if (i2 != 0) {
                    temporal = temporal.minus(i2, ChronoUnit.YEARS);
                }
                temporal = temporal.minus(this.months, ChronoUnit.MONTHS);
            }
        }
        int i3 = this.days;
        if (i3 != 0) {
            return temporal.minus(i3, ChronoUnit.DAYS);
        }
        return temporal;
    }

    private void validateChrono(TemporalAccessor temporal) {
        Objects.requireNonNull(temporal, "temporal");
        Chronology temporalChrono = (Chronology) temporal.query(TemporalQueries.chronology());
        if (temporalChrono != null && !this.chrono.equals(temporalChrono)) {
            throw new DateTimeException("Chronology mismatch, expected: " + this.chrono.getId() + ", actual: " + temporalChrono.getId());
        }
    }

    @Override // j$.time.chrono.ChronoPeriod
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChronoPeriodImpl)) {
            return false;
        }
        ChronoPeriodImpl other = (ChronoPeriodImpl) obj;
        return this.years == other.years && this.months == other.months && this.days == other.days && this.chrono.equals(other.chrono);
    }

    @Override // j$.time.chrono.ChronoPeriod
    public int hashCode() {
        return ((this.years + Integer.rotateLeft(this.months, 8)) + Integer.rotateLeft(this.days, 16)) ^ this.chrono.hashCode();
    }

    @Override // j$.time.chrono.ChronoPeriod
    public String toString() {
        if (isZero()) {
            return getChronology().toString() + " P0D";
        }
        StringBuilder buf = new StringBuilder();
        buf.append(getChronology().toString());
        buf.append(' ');
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

    protected Object writeReplace() {
        return new Ser((byte) 9, this);
    }

    private void readObject(ObjectInputStream s) {
        throw new InvalidObjectException("Deserialization via serialization delegate");
    }

    public void writeExternal(DataOutput out) {
        out.writeUTF(this.chrono.getId());
        out.writeInt(this.years);
        out.writeInt(this.months);
        out.writeInt(this.days);
    }

    public static ChronoPeriodImpl readExternal(DataInput in) {
        Chronology chrono = Chronology.CC.of(in.readUTF());
        int years = in.readInt();
        int months = in.readInt();
        int days = in.readInt();
        return new ChronoPeriodImpl(chrono, years, months, days);
    }
}
