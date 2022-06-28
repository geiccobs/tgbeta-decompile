package j$.util.stream;

import j$.util.function.BiConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class DoublePipeline$$ExternalSyntheticLambda2 implements BiConsumer {
    public static final /* synthetic */ DoublePipeline$$ExternalSyntheticLambda2 INSTANCE = new DoublePipeline$$ExternalSyntheticLambda2();

    private /* synthetic */ DoublePipeline$$ExternalSyntheticLambda2() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        DoublePipeline.lambda$sum$3((double[]) obj, (double[]) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return biConsumer.getClass();
    }
}
