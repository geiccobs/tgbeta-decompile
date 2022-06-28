package j$.util.stream;

import j$.util.function.BiConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class DoublePipeline$$ExternalSyntheticLambda1 implements BiConsumer {
    public static final /* synthetic */ DoublePipeline$$ExternalSyntheticLambda1 INSTANCE = new DoublePipeline$$ExternalSyntheticLambda1();

    private /* synthetic */ DoublePipeline$$ExternalSyntheticLambda1() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        DoublePipeline.lambda$average$6((double[]) obj, (double[]) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return biConsumer.getClass();
    }
}
