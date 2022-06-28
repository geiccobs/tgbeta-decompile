package j$.util;

import java.io.Serializable;
import java.util.Map;
/* loaded from: classes2.dex */
public final /* synthetic */ class Map$Entry$$ExternalSyntheticLambda2 implements java.util.Comparator, Serializable {
    public static final /* synthetic */ Map$Entry$$ExternalSyntheticLambda2 INSTANCE = new Map$Entry$$ExternalSyntheticLambda2();

    private /* synthetic */ Map$Entry$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compareTo;
        compareTo = ((Comparable) ((Map.Entry) obj).getKey()).compareTo(((Map.Entry) obj2).getKey());
        return compareTo;
    }
}
