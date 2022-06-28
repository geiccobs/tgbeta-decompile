package j$.util.function;

import j$.util.function.DoublePredicate;
/* loaded from: classes2.dex */
public interface DoublePredicate {
    DoublePredicate and(DoublePredicate doublePredicate);

    DoublePredicate negate();

    DoublePredicate or(DoublePredicate doublePredicate);

    boolean test(double d);

    /* renamed from: j$.util.function.DoublePredicate$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static /* synthetic */ boolean lambda$and$0(DoublePredicate _this, DoublePredicate other, double value) {
            return _this.test(value) && other.test(value);
        }

        public static DoublePredicate $default$negate(final DoublePredicate _this) {
            return new DoublePredicate() { // from class: j$.util.function.DoublePredicate$$ExternalSyntheticLambda0
                @Override // j$.util.function.DoublePredicate
                public /* synthetic */ DoublePredicate and(DoublePredicate doublePredicate) {
                    return doublePredicate.getClass();
                }

                @Override // j$.util.function.DoublePredicate
                public /* synthetic */ DoublePredicate negate() {
                    return DoublePredicate.CC.$default$negate(this);
                }

                @Override // j$.util.function.DoublePredicate
                public /* synthetic */ DoublePredicate or(DoublePredicate doublePredicate) {
                    return doublePredicate.getClass();
                }

                @Override // j$.util.function.DoublePredicate
                public final boolean test(double d) {
                    return DoublePredicate.CC.lambda$negate$1(DoublePredicate.this, d);
                }
            };
        }

        public static /* synthetic */ boolean lambda$negate$1(DoublePredicate _this, double value) {
            return !_this.test(value);
        }

        public static /* synthetic */ boolean lambda$or$2(DoublePredicate _this, DoublePredicate other, double value) {
            return _this.test(value) || other.test(value);
        }
    }
}
