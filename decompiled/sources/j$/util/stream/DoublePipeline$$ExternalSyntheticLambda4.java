package j$.util.stream;

import j$.util.function.DoubleBinaryOperator;
/* loaded from: classes2.dex */
public final /* synthetic */ class DoublePipeline$$ExternalSyntheticLambda4 implements DoubleBinaryOperator {
    public static final /* synthetic */ DoublePipeline$$ExternalSyntheticLambda4 INSTANCE = new DoublePipeline$$ExternalSyntheticLambda4();

    private /* synthetic */ DoublePipeline$$ExternalSyntheticLambda4() {
    }

    @Override // j$.util.function.DoubleBinaryOperator
    public final double applyAsDouble(double d, double d2) {
        return Math.max(d, d2);
    }
}
