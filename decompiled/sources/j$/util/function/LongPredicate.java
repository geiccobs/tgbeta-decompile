package j$.util.function;

import j$.util.function.LongPredicate;
/* loaded from: classes2.dex */
public interface LongPredicate {
    LongPredicate and(LongPredicate longPredicate);

    LongPredicate negate();

    LongPredicate or(LongPredicate longPredicate);

    boolean test(long j);

    /* renamed from: j$.util.function.LongPredicate$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static /* synthetic */ boolean lambda$and$0(LongPredicate _this, LongPredicate other, long value) {
            return _this.test(value) && other.test(value);
        }

        public static LongPredicate $default$negate(final LongPredicate _this) {
            return new LongPredicate() { // from class: j$.util.function.LongPredicate$$ExternalSyntheticLambda0
                @Override // j$.util.function.LongPredicate
                public /* synthetic */ LongPredicate and(LongPredicate longPredicate) {
                    return longPredicate.getClass();
                }

                @Override // j$.util.function.LongPredicate
                public /* synthetic */ LongPredicate negate() {
                    return LongPredicate.CC.$default$negate(this);
                }

                @Override // j$.util.function.LongPredicate
                public /* synthetic */ LongPredicate or(LongPredicate longPredicate) {
                    return longPredicate.getClass();
                }

                @Override // j$.util.function.LongPredicate
                public final boolean test(long j) {
                    return LongPredicate.CC.lambda$negate$1(LongPredicate.this, j);
                }
            };
        }

        public static /* synthetic */ boolean lambda$negate$1(LongPredicate _this, long value) {
            return !_this.test(value);
        }

        public static /* synthetic */ boolean lambda$or$2(LongPredicate _this, LongPredicate other, long value) {
            return _this.test(value) || other.test(value);
        }
    }
}
