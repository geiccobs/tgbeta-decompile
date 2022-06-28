package j$.util.function;

import j$.util.Objects;
import j$.util.function.Predicate;
/* loaded from: classes2.dex */
public final /* synthetic */ class Predicate$$ExternalSyntheticLambda4 implements Predicate {
    public static final /* synthetic */ Predicate$$ExternalSyntheticLambda4 INSTANCE = new Predicate$$ExternalSyntheticLambda4();

    private /* synthetic */ Predicate$$ExternalSyntheticLambda4() {
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
        return Objects.isNull(obj);
    }
}
