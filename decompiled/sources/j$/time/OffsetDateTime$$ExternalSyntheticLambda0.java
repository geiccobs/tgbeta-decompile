package j$.time;

import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalQuery;
/* loaded from: classes2.dex */
public final /* synthetic */ class OffsetDateTime$$ExternalSyntheticLambda0 implements TemporalQuery {
    public static final /* synthetic */ OffsetDateTime$$ExternalSyntheticLambda0 INSTANCE = new OffsetDateTime$$ExternalSyntheticLambda0();

    private /* synthetic */ OffsetDateTime$$ExternalSyntheticLambda0() {
    }

    @Override // j$.time.temporal.TemporalQuery
    public final Object queryFrom(TemporalAccessor temporalAccessor) {
        return OffsetDateTime.from(temporalAccessor);
    }
}
