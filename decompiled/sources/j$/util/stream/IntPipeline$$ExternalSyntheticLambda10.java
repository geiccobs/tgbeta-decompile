package j$.util.stream;

import j$.util.IntSummaryStatistics;
import j$.util.function.ObjIntConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class IntPipeline$$ExternalSyntheticLambda10 implements ObjIntConsumer {
    public static final /* synthetic */ IntPipeline$$ExternalSyntheticLambda10 INSTANCE = new IntPipeline$$ExternalSyntheticLambda10();

    private /* synthetic */ IntPipeline$$ExternalSyntheticLambda10() {
    }

    @Override // j$.util.function.ObjIntConsumer
    public final void accept(Object obj, int i) {
        ((IntSummaryStatistics) obj).accept(i);
    }
}
