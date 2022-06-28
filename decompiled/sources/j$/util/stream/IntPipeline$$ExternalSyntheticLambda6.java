package j$.util.stream;

import j$.util.function.IntConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class IntPipeline$$ExternalSyntheticLambda6 implements IntConsumer {
    public final /* synthetic */ Sink f$0;

    @Override // j$.util.function.IntConsumer
    public final void accept(int i) {
        this.f$0.accept(i);
    }

    @Override // j$.util.function.IntConsumer
    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return intConsumer.getClass();
    }
}
