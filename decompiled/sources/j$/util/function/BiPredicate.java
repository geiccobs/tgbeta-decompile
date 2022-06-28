package j$.util.function;

import j$.util.function.BiPredicate;
/* loaded from: classes2.dex */
public interface BiPredicate<T, U> {
    BiPredicate<T, U> and(BiPredicate<? super T, ? super U> biPredicate);

    BiPredicate<T, U> negate();

    BiPredicate<T, U> or(BiPredicate<? super T, ? super U> biPredicate);

    boolean test(T t, U u);

    /* renamed from: j$.util.function.BiPredicate$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static /* synthetic */ boolean lambda$and$0(BiPredicate _this, BiPredicate other, Object t, Object u) {
            return _this.test(t, u) && other.test(t, u);
        }

        public static BiPredicate $default$negate(final BiPredicate _this) {
            return new BiPredicate() { // from class: j$.util.function.BiPredicate$$ExternalSyntheticLambda0
                @Override // j$.util.function.BiPredicate
                public /* synthetic */ BiPredicate and(BiPredicate biPredicate) {
                    return biPredicate.getClass();
                }

                @Override // j$.util.function.BiPredicate
                public /* synthetic */ BiPredicate negate() {
                    return BiPredicate.CC.$default$negate(this);
                }

                @Override // j$.util.function.BiPredicate
                public /* synthetic */ BiPredicate or(BiPredicate biPredicate) {
                    return biPredicate.getClass();
                }

                @Override // j$.util.function.BiPredicate
                public final boolean test(Object obj, Object obj2) {
                    return BiPredicate.CC.lambda$negate$1(BiPredicate.this, obj, obj2);
                }
            };
        }

        public static /* synthetic */ boolean lambda$negate$1(BiPredicate _this, Object t, Object u) {
            return !_this.test(t, u);
        }

        public static /* synthetic */ boolean lambda$or$2(BiPredicate _this, BiPredicate other, Object t, Object u) {
            return _this.test(t, u) || other.test(t, u);
        }
    }
}
