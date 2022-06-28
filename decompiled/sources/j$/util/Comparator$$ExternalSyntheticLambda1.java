package j$.util;

import j$.util.function.Function;
import java.io.Serializable;
/* loaded from: classes2.dex */
public final /* synthetic */ class Comparator$$ExternalSyntheticLambda1 implements java.util.Comparator, Serializable {
    public final /* synthetic */ java.util.Comparator f$0;
    public final /* synthetic */ Function f$1;

    public /* synthetic */ Comparator$$ExternalSyntheticLambda1(java.util.Comparator comparator, Function function) {
        this.f$0 = comparator;
        this.f$1 = function;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compare;
        compare = this.f$0.compare(r1.apply(obj), this.f$1.apply(obj2));
        return compare;
    }
}
