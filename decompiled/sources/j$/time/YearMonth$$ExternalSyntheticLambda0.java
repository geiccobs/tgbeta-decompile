package j$.time;

import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalQuery;
/* loaded from: classes2.dex */
public final /* synthetic */ class YearMonth$$ExternalSyntheticLambda0 implements TemporalQuery {
    public static final /* synthetic */ YearMonth$$ExternalSyntheticLambda0 INSTANCE = new YearMonth$$ExternalSyntheticLambda0();

    private /* synthetic */ YearMonth$$ExternalSyntheticLambda0() {
    }

    @Override // j$.time.temporal.TemporalQuery
    public final Object queryFrom(TemporalAccessor temporalAccessor) {
        return YearMonth.from(temporalAccessor);
    }
}