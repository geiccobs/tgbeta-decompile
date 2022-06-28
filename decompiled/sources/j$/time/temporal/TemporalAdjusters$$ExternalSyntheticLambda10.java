package j$.time.temporal;
/* loaded from: classes2.dex */
public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda10 implements TemporalAdjuster {
    public static final /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda10 INSTANCE = new TemporalAdjusters$$ExternalSyntheticLambda10();

    private /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda10() {
    }

    @Override // j$.time.temporal.TemporalAdjuster
    public final Temporal adjustInto(Temporal temporal) {
        Temporal with;
        with = temporal.with(ChronoField.DAY_OF_YEAR, 1L);
        return with;
    }
}
