package j$.util;

import j$.util.function.Consumer;
import j$.util.function.LongConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class PrimitiveIterator$OfLong$$ExternalSyntheticLambda0 implements LongConsumer {
    public final /* synthetic */ Consumer f$0;

    @Override // j$.util.function.LongConsumer
    public final void accept(long j) {
        this.f$0.accept(Long.valueOf(j));
    }

    @Override // j$.util.function.LongConsumer
    public /* synthetic */ LongConsumer andThen(LongConsumer longConsumer) {
        return longConsumer.getClass();
    }
}
