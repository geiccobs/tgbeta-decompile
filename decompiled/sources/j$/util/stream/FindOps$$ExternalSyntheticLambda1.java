package j$.util.stream;

import j$.util.OptionalDouble;
import j$.util.function.Predicate;
/* loaded from: classes2.dex */
public final /* synthetic */ class FindOps$$ExternalSyntheticLambda1 implements Predicate {
    public static final /* synthetic */ FindOps$$ExternalSyntheticLambda1 INSTANCE = new FindOps$$ExternalSyntheticLambda1();

    private /* synthetic */ FindOps$$ExternalSyntheticLambda1() {
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
        return ((OptionalDouble) obj).isPresent();
    }
}
