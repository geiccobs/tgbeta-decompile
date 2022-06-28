package j$.time.temporal;
/* loaded from: classes2.dex */
public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda11 implements TemporalAdjuster {
    public static final /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda11 INSTANCE = new TemporalAdjusters$$ExternalSyntheticLambda11();

    private /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda11() {
    }

    @Override // j$.time.temporal.TemporalAdjuster
    public final Temporal adjustInto(Temporal temporal) {
        Temporal with;
        with = temporal.with(ChronoField.DAY_OF_MONTH, temporal.range(ChronoField.DAY_OF_MONTH).getMaximum());
        return with;
    }
}
