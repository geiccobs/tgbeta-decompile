package j$.time;

import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalQuery;
/* loaded from: classes2.dex */
public final /* synthetic */ class ZonedDateTime$$ExternalSyntheticLambda0 implements TemporalQuery {
    public static final /* synthetic */ ZonedDateTime$$ExternalSyntheticLambda0 INSTANCE = new ZonedDateTime$$ExternalSyntheticLambda0();

    private /* synthetic */ ZonedDateTime$$ExternalSyntheticLambda0() {
    }

    @Override // j$.time.temporal.TemporalQuery
    public final Object queryFrom(TemporalAccessor temporalAccessor) {
        return ZonedDateTime.from(temporalAccessor);
    }
}
