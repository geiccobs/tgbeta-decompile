package j$.util.stream;

import j$.util.LongSummaryStatistics;
import j$.util.function.ObjLongConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class LongPipeline$$ExternalSyntheticLambda10 implements ObjLongConsumer {
    public static final /* synthetic */ LongPipeline$$ExternalSyntheticLambda10 INSTANCE = new LongPipeline$$ExternalSyntheticLambda10();

    private /* synthetic */ LongPipeline$$ExternalSyntheticLambda10() {
    }

    @Override // j$.util.function.ObjLongConsumer
    public final void accept(Object obj, long j) {
        ((LongSummaryStatistics) obj).accept(j);
    }
}
