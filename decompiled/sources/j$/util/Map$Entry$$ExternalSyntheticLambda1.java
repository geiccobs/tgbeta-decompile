package j$.util;

import java.io.Serializable;
import java.util.Map;
/* loaded from: classes2.dex */
public final /* synthetic */ class Map$Entry$$ExternalSyntheticLambda1 implements java.util.Comparator, Serializable {
    public final /* synthetic */ java.util.Comparator f$0;

    public /* synthetic */ Map$Entry$$ExternalSyntheticLambda1(java.util.Comparator comparator) {
        this.f$0 = comparator;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compare;
        compare = this.f$0.compare(((Map.Entry) obj).getValue(), ((Map.Entry) obj2).getValue());
        return compare;
    }
}
