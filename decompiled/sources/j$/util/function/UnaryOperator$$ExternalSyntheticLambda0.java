package j$.util.function;

import j$.util.function.UnaryOperator;
/* loaded from: classes2.dex */
public final /* synthetic */ class UnaryOperator$$ExternalSyntheticLambda0 implements UnaryOperator {
    public static final /* synthetic */ UnaryOperator$$ExternalSyntheticLambda0 INSTANCE = new UnaryOperator$$ExternalSyntheticLambda0();

    private /* synthetic */ UnaryOperator$$ExternalSyntheticLambda0() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        return UnaryOperator.CC.lambda$identity$0(obj);
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
