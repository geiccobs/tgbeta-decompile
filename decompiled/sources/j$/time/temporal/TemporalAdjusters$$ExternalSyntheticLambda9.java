package j$.time.temporal;
/* loaded from: classes2.dex */
public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda9 implements TemporalAdjuster {
    public static final /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda9 INSTANCE = new TemporalAdjusters$$ExternalSyntheticLambda9();

    private /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda9() {
    }

    @Override // j$.time.temporal.TemporalAdjuster
    public final Temporal adjustInto(Temporal temporal) {
        Temporal plus;
        plus = temporal.with(ChronoField.DAY_OF_YEAR, 1L).plus(1L, ChronoUnit.YEARS);
        return plus;
    }
}
