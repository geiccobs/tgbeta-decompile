package j$.util;

import j$.util.function.Consumer;
import j$.util.function.DoubleConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class PrimitiveIterator$OfDouble$$ExternalSyntheticLambda0 implements DoubleConsumer {
    public final /* synthetic */ Consumer f$0;

    @Override // j$.util.function.DoubleConsumer
    public final void accept(double d) {
        this.f$0.accept(Double.valueOf(d));
    }

    @Override // j$.util.function.DoubleConsumer
    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return doubleConsumer.getClass();
    }
}
