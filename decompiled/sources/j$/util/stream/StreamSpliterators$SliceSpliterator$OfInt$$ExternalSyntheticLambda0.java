package j$.util.stream;

import j$.util.function.IntConsumer;
import j$.util.stream.StreamSpliterators;
/* loaded from: classes2.dex */
public final /* synthetic */ class StreamSpliterators$SliceSpliterator$OfInt$$ExternalSyntheticLambda0 implements IntConsumer {
    public static final /* synthetic */ StreamSpliterators$SliceSpliterator$OfInt$$ExternalSyntheticLambda0 INSTANCE = new StreamSpliterators$SliceSpliterator$OfInt$$ExternalSyntheticLambda0();

    private /* synthetic */ StreamSpliterators$SliceSpliterator$OfInt$$ExternalSyntheticLambda0() {
    }

    @Override // j$.util.function.IntConsumer
    public final void accept(int i) {
        StreamSpliterators.SliceSpliterator.OfInt.lambda$emptyConsumer$0(i);
    }

    @Override // j$.util.function.IntConsumer
    public /* synthetic */ IntConsumer andThen(IntConsumer intConsumer) {
        return intConsumer.getClass();
    }
}