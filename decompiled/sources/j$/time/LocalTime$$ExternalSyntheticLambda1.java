package j$.time;

import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalQuery;
/* loaded from: classes2.dex */
public final /* synthetic */ class LocalTime$$ExternalSyntheticLambda1 implements TemporalQuery {
    public static final /* synthetic */ LocalTime$$ExternalSyntheticLambda1 INSTANCE = new LocalTime$$ExternalSyntheticLambda1();

    private /* synthetic */ LocalTime$$ExternalSyntheticLambda1() {
    }

    @Override // j$.time.temporal.TemporalQuery
    public final Object queryFrom(TemporalAccessor temporalAccessor) {
        return LocalTime.from(temporalAccessor);
    }
}
