package j$.time.temporal;
/* loaded from: classes2.dex */
public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda12 implements TemporalAdjuster {
    public static final /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda12 INSTANCE = new TemporalAdjusters$$ExternalSyntheticLambda12();

    private /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda12() {
    }

    @Override // j$.time.temporal.TemporalAdjuster
    public final Temporal adjustInto(Temporal temporal) {
        Temporal with;
        with = temporal.with(ChronoField.DAY_OF_YEAR, temporal.range(ChronoField.DAY_OF_YEAR).getMaximum());
        return with;
    }
}
