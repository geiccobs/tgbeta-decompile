package j$.util.stream;

import j$.util.OptionalInt;
import j$.util.function.Predicate;
/* loaded from: classes2.dex */
public final /* synthetic */ class FindOps$$ExternalSyntheticLambda2 implements Predicate {
    public static final /* synthetic */ FindOps$$ExternalSyntheticLambda2 INSTANCE = new FindOps$$ExternalSyntheticLambda2();

    private /* synthetic */ FindOps$$ExternalSyntheticLambda2() {
    }

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
        return ((OptionalInt) obj).isPresent();
    }
}
