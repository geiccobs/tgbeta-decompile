package j$.time;

import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalQuery;
/* loaded from: classes2.dex */
public final /* synthetic */ class LocalDateTime$$ExternalSyntheticLambda0 implements TemporalQuery {
    public static final /* synthetic */ LocalDateTime$$ExternalSyntheticLambda0 INSTANCE = new LocalDateTime$$ExternalSyntheticLambda0();

    private /* synthetic */ LocalDateTime$$ExternalSyntheticLambda0() {
    }

    @Override // j$.time.temporal.TemporalQuery
    public final Object queryFrom(TemporalAccessor temporalAccessor) {
        return LocalDateTime.from(temporalAccessor);
    }
}
