package j$.time.chrono;

import j$.time.temporal.Temporal;
import j$.time.temporal.TemporalAmount;
import j$.time.temporal.TemporalUnit;
import j$.util.Objects;
import java.util.List;
/* loaded from: classes2.dex */
public interface ChronoPeriod extends TemporalAmount {
    @Override // j$.time.temporal.TemporalAmount
    Temporal addTo(Temporal temporal);

    boolean equals(Object obj);

    @Override // j$.time.temporal.TemporalAmount
    long get(TemporalUnit temporalUnit);

    Chronology getChronology();

    @Override // j$.time.temporal.TemporalAmount
    List<TemporalUnit> getUnits();

    int hashCode();

    boolean isNegative();

    boolean isZero();

    ChronoPeriod minus(TemporalAmount temporalAmount);

    ChronoPeriod multipliedBy(int i);

    ChronoPeriod negated();

    ChronoPeriod normalized();

    ChronoPeriod plus(TemporalAmount temporalAmount);

    @Override // j$.time.temporal.TemporalAmount
    Temporal subtractFrom(Temporal temporal);

    String toString();

    /* renamed from: j$.time.chrono.ChronoPeriod$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static ChronoPeriod between(ChronoLocalDate startDateInclusive, ChronoLocalDate endDateExclusive) {
            Objects.requireNonNull(startDateInclusive, "startDateInclusive");
            Objects.requireNonNull(endDateExclusive, "endDateExclusive");
            return startDateInclusive.until(endDateExclusive);
        }

        public static boolean $default$isZero(ChronoPeriod _this) {
            for (TemporalUnit unit : _this.getUnits()) {
                if (_this.get(unit) != 0) {
                    return false;
                }
            }
            return true;
        }

        public static boolean $default$isNegative(ChronoPeriod _this) {
            for (TemporalUnit unit : _this.getUnits()) {
                if (_this.get(unit) < 0) {
                    return true;
                }
            }
            return false;
        }
    }
}
