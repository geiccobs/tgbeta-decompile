package j$.time.temporal;
/* loaded from: classes2.dex */
public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda7 implements TemporalAdjuster {
    public static final /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda7 INSTANCE = new TemporalAdjusters$$ExternalSyntheticLambda7();

    private /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda7() {
    }

    @Override // j$.time.temporal.TemporalAdjuster
    public final Temporal adjustInto(Temporal temporal) {
        Temporal with;
        with = temporal.with(ChronoField.DAY_OF_MONTH, 1L);
        return with;
    }
}
