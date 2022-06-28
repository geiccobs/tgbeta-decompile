package j$.util;

import j$.util.Comparator;
import j$.util.function.ToLongFunction;
import java.io.Serializable;
/* loaded from: classes2.dex */
public final /* synthetic */ class Comparator$$ExternalSyntheticLambda5 implements java.util.Comparator, Serializable {
    public final /* synthetic */ ToLongFunction f$0;

    public /* synthetic */ Comparator$$ExternalSyntheticLambda5(ToLongFunction toLongFunction) {
        this.f$0 = toLongFunction;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return Comparator.CC.lambda$comparingLong$6043328a$1(this.f$0, obj, obj2);
    }
}
