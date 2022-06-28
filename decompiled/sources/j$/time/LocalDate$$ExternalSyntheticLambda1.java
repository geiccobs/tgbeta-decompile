package j$.time;

import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalQuery;
/* loaded from: classes2.dex */
public final /* synthetic */ class LocalDate$$ExternalSyntheticLambda1 implements TemporalQuery {
    public static final /* synthetic */ LocalDate$$ExternalSyntheticLambda1 INSTANCE = new LocalDate$$ExternalSyntheticLambda1();

    private /* synthetic */ LocalDate$$ExternalSyntheticLambda1() {
    }

    @Override // j$.time.temporal.TemporalQuery
    public final Object queryFrom(TemporalAccessor temporalAccessor) {
        return LocalDate.from(temporalAccessor);
    }
}
