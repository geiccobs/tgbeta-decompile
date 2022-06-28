package j$.util.stream;

import j$.util.function.LongUnaryOperator;
/* loaded from: classes2.dex */
public final /* synthetic */ class LongPipeline$$ExternalSyntheticLambda9 implements LongUnaryOperator {
    public static final /* synthetic */ LongPipeline$$ExternalSyntheticLambda9 INSTANCE = new LongPipeline$$ExternalSyntheticLambda9();

    private /* synthetic */ LongPipeline$$ExternalSyntheticLambda9() {
    }

    @Override // j$.util.function.LongUnaryOperator
    public /* synthetic */ LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator) {
        return longUnaryOperator.getClass();
    }

    @Override // j$.util.function.LongUnaryOperator
    public final long applyAsLong(long j) {
        return LongPipeline.lambda$count$4(j);
    }

    @Override // j$.util.function.LongUnaryOperator
    public /* synthetic */ LongUnaryOperator compose(LongUnaryOperator longUnaryOperator) {
        return longUnaryOperator.getClass();
    }
}
