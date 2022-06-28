package j$.util.function;
/* loaded from: classes2.dex */
public interface IntUnaryOperator {
    IntUnaryOperator andThen(IntUnaryOperator intUnaryOperator);

    int applyAsInt(int i);

    IntUnaryOperator compose(IntUnaryOperator intUnaryOperator);

    /* renamed from: j$.util.function.IntUnaryOperator$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static IntUnaryOperator identity() {
            return IntUnaryOperator$$ExternalSyntheticLambda2.INSTANCE;
        }

        public static /* synthetic */ int lambda$identity$2(int t) {
            return t;
        }
    }
}
