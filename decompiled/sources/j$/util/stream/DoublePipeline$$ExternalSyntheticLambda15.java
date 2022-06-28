package j$.util.stream;

import j$.util.function.ToDoubleFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class DoublePipeline$$ExternalSyntheticLambda15 implements ToDoubleFunction {
    public static final /* synthetic */ DoublePipeline$$ExternalSyntheticLambda15 INSTANCE = new DoublePipeline$$ExternalSyntheticLambda15();

    private /* synthetic */ DoublePipeline$$ExternalSyntheticLambda15() {
    }

    @Override // j$.util.function.ToDoubleFunction
    public final double applyAsDouble(Object obj) {
        double doubleValue;
        doubleValue = ((Double) obj).doubleValue();
        return doubleValue;
    }
}
