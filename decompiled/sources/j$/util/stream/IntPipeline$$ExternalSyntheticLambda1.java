package j$.util.stream;

import j$.util.function.BiConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class IntPipeline$$ExternalSyntheticLambda1 implements BiConsumer {
    public static final /* synthetic */ IntPipeline$$ExternalSyntheticLambda1 INSTANCE = new IntPipeline$$ExternalSyntheticLambda1();

    private /* synthetic */ IntPipeline$$ExternalSyntheticLambda1() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        IntPipeline.lambda$average$4((long[]) obj, (long[]) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return biConsumer.getClass();
    }
}
