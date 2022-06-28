package j$.util.function;
/* loaded from: classes2.dex */
public interface LongUnaryOperator {
    LongUnaryOperator andThen(LongUnaryOperator longUnaryOperator);

    long applyAsLong(long j);

    LongUnaryOperator compose(LongUnaryOperator longUnaryOperator);

    /* renamed from: j$.util.function.LongUnaryOperator$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static LongUnaryOperator identity() {
            return LongUnaryOperator$$ExternalSyntheticLambda2.INSTANCE;
        }

        public static /* synthetic */ long lambda$identity$2(long t) {
            return t;
        }
    }
}
