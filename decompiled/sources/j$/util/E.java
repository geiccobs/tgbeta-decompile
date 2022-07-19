package j$.util;

import j$.util.function.Consumer;
import j$.util.u;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class E extends H implements u.a {
    @Override // j$.util.u.a, j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0034a.k(this, consumer);
    }

    @Override // j$.util.u.a
    public void c(j$.util.function.l lVar) {
        lVar.getClass();
    }

    @Override // j$.util.u.a, j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0034a.c(this, consumer);
    }

    @Override // j$.util.u.a
    public boolean g(j$.util.function.l lVar) {
        lVar.getClass();
        return false;
    }

    @Override // j$.util.u
    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override // j$.util.u
    public /* synthetic */ long getExactSizeIfKnown() {
        return AbstractC0034a.e(this);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return AbstractC0034a.f(this, i);
    }
}
