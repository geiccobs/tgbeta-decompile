package j$.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
/* loaded from: classes2.dex */
public class s extends J {
    final /* synthetic */ SortedSet f;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public s(SortedSet sortedSet, Collection collection, int i) {
        super(collection, i);
        this.f = sortedSet;
    }

    @Override // j$.util.J, j$.util.u
    public Comparator getComparator() {
        return this.f.comparator();
    }
}
