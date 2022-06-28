package j$.util.stream;

import j$.util.function.DoubleConsumer;
import j$.util.stream.Node;
/* loaded from: classes2.dex */
public final /* synthetic */ class Node$OfDouble$$ExternalSyntheticLambda0 implements DoubleConsumer {
    public static final /* synthetic */ Node$OfDouble$$ExternalSyntheticLambda0 INSTANCE = new Node$OfDouble$$ExternalSyntheticLambda0();

    private /* synthetic */ Node$OfDouble$$ExternalSyntheticLambda0() {
    }

    @Override // j$.util.function.DoubleConsumer
    public final void accept(double d) {
        Node.OfDouble.CC.lambda$truncate$0(d);
    }

    @Override // j$.util.function.DoubleConsumer
    public /* synthetic */ DoubleConsumer andThen(DoubleConsumer doubleConsumer) {
        return doubleConsumer.getClass();
    }
}
