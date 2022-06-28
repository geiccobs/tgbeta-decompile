package j$.util.stream;

import j$.util.IntSummaryStatistics;
import j$.util.function.BiConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class IntPipeline$$ExternalSyntheticLambda0 implements BiConsumer {
    public static final /* synthetic */ IntPipeline$$ExternalSyntheticLambda0 INSTANCE = new IntPipeline$$ExternalSyntheticLambda0();

    private /* synthetic */ IntPipeline$$ExternalSyntheticLambda0() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((IntSummaryStatistics) obj).combine((IntSummaryStatistics) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return biConsumer.getClass();
    }
}
