package j$.time.temporal;
/* loaded from: classes2.dex */
public final /* synthetic */ class TemporalAdjusters$$ExternalSyntheticLambda8 implements TemporalAdjuster {
    public static final /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda8 INSTANCE = new TemporalAdjusters$$ExternalSyntheticLambda8();

    private /* synthetic */ TemporalAdjusters$$ExternalSyntheticLambda8() {
    }

    @Override // j$.time.temporal.TemporalAdjuster
    public final Temporal adjustInto(Temporal temporal) {
        Temporal plus;
        plus = temporal.with(ChronoField.DAY_OF_MONTH, 1L).plus(1L, ChronoUnit.MONTHS);
        return plus;
    }
}
