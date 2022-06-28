package j$.util.stream;

import j$.util.DoubleSummaryStatistics;
import j$.util.function.BiConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class DoublePipeline$$ExternalSyntheticLambda0 implements BiConsumer {
    public static final /* synthetic */ DoublePipeline$$ExternalSyntheticLambda0 INSTANCE = new DoublePipeline$$ExternalSyntheticLambda0();

    private /* synthetic */ DoublePipeline$$ExternalSyntheticLambda0() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((DoubleSummaryStatistics) obj).combine((DoubleSummaryStatistics) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return biConsumer.getClass();
    }
}
