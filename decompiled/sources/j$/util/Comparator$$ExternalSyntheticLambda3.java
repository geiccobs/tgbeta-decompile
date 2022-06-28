package j$.util;

import j$.util.function.ToDoubleFunction;
import java.io.Serializable;
/* loaded from: classes2.dex */
public final /* synthetic */ class Comparator$$ExternalSyntheticLambda3 implements java.util.Comparator, Serializable {
    public final /* synthetic */ ToDoubleFunction f$0;

    public /* synthetic */ Comparator$$ExternalSyntheticLambda3(ToDoubleFunction toDoubleFunction) {
        this.f$0 = toDoubleFunction;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compare;
        compare = Double.compare(r0.applyAsDouble(obj), this.f$0.applyAsDouble(obj2));
        return compare;
    }
}
