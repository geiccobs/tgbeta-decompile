package j$.util;

import java.io.Serializable;
import java.util.Map;
/* loaded from: classes2.dex */
public final /* synthetic */ class Map$Entry$$ExternalSyntheticLambda3 implements java.util.Comparator, Serializable {
    public static final /* synthetic */ Map$Entry$$ExternalSyntheticLambda3 INSTANCE = new Map$Entry$$ExternalSyntheticLambda3();

    private /* synthetic */ Map$Entry$$ExternalSyntheticLambda3() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compareTo;
        compareTo = ((Comparable) ((Map.Entry) obj).getValue()).compareTo(((Map.Entry) obj2).getValue());
        return compareTo;
    }
}
