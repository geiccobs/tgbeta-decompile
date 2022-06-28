package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.stream.StreamSpliterators;
/* loaded from: classes2.dex */
public final /* synthetic */ class StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda0 implements Consumer {
    public static final /* synthetic */ StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda0 INSTANCE = new StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda0();

    private /* synthetic */ StreamSpliterators$SliceSpliterator$OfRef$$ExternalSyntheticLambda0() {
    }

    @Override // j$.util.function.Consumer
    public final void accept(Object obj) {
        StreamSpliterators.SliceSpliterator.OfRef.lambda$forEachRemaining$1(obj);
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }
}
