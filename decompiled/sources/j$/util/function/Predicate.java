package j$.util.function;

import j$.util.function.Predicate;
/* loaded from: classes2.dex */
public interface Predicate<T> {
    Predicate<T> and(Predicate<? super T> predicate);

    Predicate<T> negate();

    Predicate<T> or(Predicate<? super T> predicate);

    boolean test(T t);

    /* renamed from: j$.util.function.Predicate$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static /* synthetic */ boolean lambda$and$0(Predicate _this, Predicate other, Object t) {
            return _this.test(t) && other.test(t);
        }

        public static Predicate $default$negate(final Predicate _this) {
            return new Predicate() { // from class: j$.util.function.Predicate$$ExternalSyntheticLambda1
                @Override // j$.util.function.Predicate
                public /* synthetic */ Predicate and(Predicate predicate) {
                    return predicate.getClass();
                }

                @Override // j$.util.function.Predicate
                public /* synthetic */ Predicate negate() {
                    return Predicate.CC.$default$negate(this);
                }

                @Override // j$.util.function.Predicate
                public /* synthetic */ Predicate or(Predicate predicate) {
                    return predicate.getClass();
                }

                @Override // j$.util.function.Predicate
                public final boolean test(Object obj) {
                    return Predicate.CC.lambda$negate$1(Predicate.this, obj);
                }
            };
        }

        public static /* synthetic */ boolean lambda$negate$1(Predicate _this, Object t) {
            return !_this.test(t);
        }

        public static /* synthetic */ boolean lambda$or$2(Predicate _this, Predicate other, Object t) {
            return _this.test(t) || other.test(t);
        }

        public static <T> Predicate<T> isEqual(final Object targetRef) {
            if (targetRef == null) {
                return Predicate$$ExternalSyntheticLambda4.INSTANCE;
            }
            return new Predicate() { // from class: j$.util.function.Predicate$$ExternalSyntheticLambda0
                @Override // j$.util.function.Predicate
                public /* synthetic */ Predicate and(Predicate predicate) {
                    return predicate.getClass();
                }

                @Override // j$.util.function.Predicate
                public /* synthetic */ Predicate negate() {
                    return Predicate.CC.$default$negate(this);
                }

                @Override // j$.util.function.Predicate
                public /* synthetic */ Predicate or(Predicate predicate) {
                    return predicate.getClass();
                }

                @Override // j$.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean equals;
                    equals = targetRef.equals(obj);
                    return equals;
                }
            };
        }
    }
}
