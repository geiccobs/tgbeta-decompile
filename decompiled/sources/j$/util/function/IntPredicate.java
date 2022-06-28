package j$.util.function;

import j$.util.function.IntPredicate;
/* loaded from: classes2.dex */
public interface IntPredicate {
    IntPredicate and(IntPredicate intPredicate);

    IntPredicate negate();

    IntPredicate or(IntPredicate intPredicate);

    boolean test(int i);

    /* renamed from: j$.util.function.IntPredicate$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static /* synthetic */ boolean lambda$and$0(IntPredicate _this, IntPredicate other, int value) {
            return _this.test(value) && other.test(value);
        }

        public static IntPredicate $default$negate(final IntPredicate _this) {
            return new IntPredicate() { // from class: j$.util.function.IntPredicate$$ExternalSyntheticLambda0
                @Override // j$.util.function.IntPredicate
                public /* synthetic */ IntPredicate and(IntPredicate intPredicate) {
                    return intPredicate.getClass();
                }

                @Override // j$.util.function.IntPredicate
                public /* synthetic */ IntPredicate negate() {
                    return IntPredicate.CC.$default$negate(this);
                }

                @Override // j$.util.function.IntPredicate
                public /* synthetic */ IntPredicate or(IntPredicate intPredicate) {
                    return intPredicate.getClass();
                }

                @Override // j$.util.function.IntPredicate
                public final boolean test(int i) {
                    return IntPredicate.CC.lambda$negate$1(IntPredicate.this, i);
                }
            };
        }

        public static /* synthetic */ boolean lambda$negate$1(IntPredicate _this, int value) {
            return !_this.test(value);
        }

        public static /* synthetic */ boolean lambda$or$2(IntPredicate _this, IntPredicate other, int value) {
            return _this.test(value) || other.test(value);
        }
    }
}
