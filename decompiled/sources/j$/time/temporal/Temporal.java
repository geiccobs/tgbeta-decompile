package j$.time.temporal;
/* loaded from: classes2.dex */
public interface Temporal extends TemporalAccessor {
    boolean isSupported(TemporalUnit temporalUnit);

    Temporal minus(long j, TemporalUnit temporalUnit);

    Temporal minus(TemporalAmount temporalAmount);

    Temporal plus(long j, TemporalUnit temporalUnit);

    Temporal plus(TemporalAmount temporalAmount);

    long until(Temporal temporal, TemporalUnit temporalUnit);

    Temporal with(TemporalAdjuster temporalAdjuster);

    Temporal with(TemporalField temporalField, long j);

    /* renamed from: j$.time.temporal.Temporal$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static Temporal $default$minus(Temporal _this, long amountToSubtract, TemporalUnit unit) {
            return amountToSubtract == Long.MIN_VALUE ? _this.plus(Long.MAX_VALUE, unit).plus(1L, unit) : _this.plus(-amountToSubtract, unit);
        }
    }
}
