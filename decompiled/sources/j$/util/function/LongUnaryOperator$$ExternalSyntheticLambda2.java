package j$.util.function;

import j$.util.function.LongUnaryOperator;
/* loaded from: classes2.dex */
public final /* synthetic */ class LongUnaryOperator$$ExternalSyntheticLambda2 implements LongUnaryOperator {
    public static final /* synthetic */ LongUnaryOperator$$ExternalSyntheticLambda2 INSTANCE = new LongUnaryOperator$$ExternalSyntheticLambda2();

    private /* synthetic */ LongUnaryOperator$$ExternalSyntheticLambda2() {
    }

    @Override // j$.util.function.LongUnaryOperator
    public /* synthetic */ LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator) {
        return longUnaryOperator.getClass();
    }

    @Override // j$.util.function.LongUnaryOperator
    public final long applyAsLong(long j) {
        return LongUnaryOperator.CC.lambda$identity$2(j);
    }

    @Override // j$.util.function.LongUnaryOperator
    public /* synthetic */ LongUnaryOperator compose(LongUnaryOperator longUnaryOperator) {
        return longUnaryOperator.getClass();
    }
}
