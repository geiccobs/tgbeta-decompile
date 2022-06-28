package j$.util.stream;

import j$.util.function.LongConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class LongPipeline$$ExternalSyntheticLambda7 implements LongConsumer {
    public final /* synthetic */ Sink f$0;

    @Override // j$.util.function.LongConsumer
    public final void accept(long j) {
        this.f$0.accept(j);
    }

    @Override // j$.util.function.LongConsumer
    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return longConsumer.getClass();
    }
}
