package j$.time.format;

import j$.time.temporal.TemporalAccessor;
import j$.time.temporal.TemporalQuery;
/* loaded from: classes2.dex */
public final /* synthetic */ class DateTimeFormatter$$ExternalSyntheticLambda1 implements TemporalQuery {
    public static final /* synthetic */ DateTimeFormatter$$ExternalSyntheticLambda1 INSTANCE = new DateTimeFormatter$$ExternalSyntheticLambda1();

    private /* synthetic */ DateTimeFormatter$$ExternalSyntheticLambda1() {
    }

    @Override // j$.time.temporal.TemporalQuery
    public final Object queryFrom(TemporalAccessor temporalAccessor) {
        return DateTimeFormatter.lambda$static$1(temporalAccessor);
    }
}