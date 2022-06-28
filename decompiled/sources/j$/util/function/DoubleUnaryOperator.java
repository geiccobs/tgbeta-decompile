package j$.util.function;
/* loaded from: classes2.dex */
public interface DoubleUnaryOperator {
    DoubleUnaryOperator andThen(DoubleUnaryOperator doubleUnaryOperator);

    double applyAsDouble(double d);

    DoubleUnaryOperator compose(DoubleUnaryOperator doubleUnaryOperator);

    /* renamed from: j$.util.function.DoubleUnaryOperator$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static DoubleUnaryOperator identity() {
            return DoubleUnaryOperator$$ExternalSyntheticLambda2.INSTANCE;
        }

        public static /* synthetic */ double lambda$identity$2(double t) {
            return t;
        }
    }
}
