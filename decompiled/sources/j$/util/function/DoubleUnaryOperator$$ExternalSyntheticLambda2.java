package j$.util.function;

import j$.util.function.DoubleUnaryOperator;
/* loaded from: classes2.dex */
public final /* synthetic */ class DoubleUnaryOperator$$ExternalSyntheticLambda2 implements DoubleUnaryOperator {
    public static final /* synthetic */ DoubleUnaryOperator$$ExternalSyntheticLambda2 INSTANCE = new DoubleUnaryOperator$$ExternalSyntheticLambda2();

    private /* synthetic */ DoubleUnaryOperator$$ExternalSyntheticLambda2() {
    }

    @Override // j$.util.function.DoubleUnaryOperator
    public /* synthetic */ DoubleUnaryOperator andThen(DoubleUnaryOperator doubleUnaryOperator) {
        return doubleUnaryOperator.getClass();
    }

    @Override // j$.util.function.DoubleUnaryOperator
    public final double applyAsDouble(double d) {
        return DoubleUnaryOperator.CC.lambda$identity$2(d);
    }

    @Override // j$.util.function.DoubleUnaryOperator
    public /* synthetic */ DoubleUnaryOperator compose(DoubleUnaryOperator doubleUnaryOperator) {
        return doubleUnaryOperator.getClass();
    }
}
