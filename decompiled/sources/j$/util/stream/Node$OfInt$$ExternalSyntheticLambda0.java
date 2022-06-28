package j$.util.stream;

import j$.util.function.IntConsumer;
import j$.util.stream.Node;
/* loaded from: classes2.dex */
public final /* synthetic */ class Node$OfInt$$ExternalSyntheticLambda0 implements IntConsumer {
    public static final /* synthetic */ Node$OfInt$$ExternalSyntheticLambda0 INSTANCE = new Node$OfInt$$ExternalSyntheticLambda0();

    private /* synthetic */ Node$OfInt$$ExternalSyntheticLambda0() {
    }

    @Override // j$.util.function.IntConsumer
    public final void accept(int i) {
        Node.OfInt.CC.lambda$truncate$0(i);
    }

    @Override // j$.util.function.IntConsumer
    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return intConsumer.getClass();
    }
}
