package j$.util.function;

import j$.util.function.IntUnaryOperator;
/* loaded from: classes2.dex */
public final /* synthetic */ class IntUnaryOperator$$ExternalSyntheticLambda2 implements IntUnaryOperator {
    public static final /* synthetic */ IntUnaryOperator$$ExternalSyntheticLambda2 INSTANCE = new IntUnaryOperator$$ExternalSyntheticLambda2();

    private /* synthetic */ IntUnaryOperator$$ExternalSyntheticLambda2() {
    }

    @Override // j$.util.function.IntUnaryOperator
    public /* synthetic */ IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator) {
        return intUnaryOperator.getClass();
    }

    @Override // j$.util.function.IntUnaryOperator
    public final int applyAsInt(int i) {
        return IntUnaryOperator.CC.lambda$identity$2(i);
    }

    @Override // j$.util.function.IntUnaryOperator
    public /* synthetic */ IntUnaryOperator compose(IntUnaryOperator intUnaryOperator) {
        return intUnaryOperator.getClass();
    }
}
