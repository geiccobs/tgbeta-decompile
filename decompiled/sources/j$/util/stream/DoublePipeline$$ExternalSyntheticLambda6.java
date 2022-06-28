package j$.util.stream;

import j$.util.function.DoubleConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class DoublePipeline$$ExternalSyntheticLambda6 implements DoubleConsumer {
    public final /* synthetic */ Sink f$0;

    @Override // j$.util.function.DoubleConsumer
    public final void accept(double d) {
        this.f$0.accept(d);
    }

    @Override // j$.util.function.DoubleConsumer
    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return doubleConsumer.getClass();
    }
}
