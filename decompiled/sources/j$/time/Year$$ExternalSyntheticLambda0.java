package j$.time;

import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalQuery;
/* loaded from: classes2.dex */
public final /* synthetic */ class Year$$ExternalSyntheticLambda0 implements TemporalQuery {
    public static final /* synthetic */ Year$$ExternalSyntheticLambda0 INSTANCE = new Year$$ExternalSyntheticLambda0();

    private /* synthetic */ Year$$ExternalSyntheticLambda0() {
    }

    @Override // j$.time.temporal.TemporalQuery
    public final Object queryFrom(TemporalAccessor temporalAccessor) {
        return Year.from(temporalAccessor);
    }
}
