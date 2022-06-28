package j$.util;

import j$.util.function.Function;
import java.io.Serializable;
/* loaded from: classes2.dex */
public final /* synthetic */ class Comparator$$ExternalSyntheticLambda2 implements java.util.Comparator, Serializable {
    public final /* synthetic */ Function f$0;

    public /* synthetic */ Comparator$$ExternalSyntheticLambda2(Function function) {
        this.f$0 = function;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compareTo;
        compareTo = ((Comparable) r0.apply(obj)).compareTo(this.f$0.apply(obj2));
        return compareTo;
    }
}
