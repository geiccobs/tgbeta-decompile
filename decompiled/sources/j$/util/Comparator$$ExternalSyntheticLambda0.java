package j$.util;

import java.io.Serializable;
/* loaded from: classes2.dex */
public final /* synthetic */ class Comparator$$ExternalSyntheticLambda0 implements java.util.Comparator, Serializable {
    public final /* synthetic */ java.util.Comparator f$0;
    public final /* synthetic */ java.util.Comparator f$1;

    public /* synthetic */ Comparator$$ExternalSyntheticLambda0(java.util.Comparator comparator, java.util.Comparator comparator2) {
        this.f$0 = comparator;
        this.f$1 = comparator2;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        java.util.Comparator comparator = this.f$0;
        java.util.Comparator comparator2 = this.f$1;
        return comparator.compare(obj, obj2);
    }
}
