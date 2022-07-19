package j$.util;

import j$.util.function.Consumer;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class D extends H implements t {
    @Override // j$.util.t, j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0039a.j(this, consumer);
    }

    @Override // j$.util.t
    public void e(j$.util.function.f fVar) {
        fVar.getClass();
    }

    @Override // j$.util.t, j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0039a.b(this, consumer);
    }

    @Override // j$.util.u
    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override // j$.util.u
    public /* synthetic */ long getExactSizeIfKnown() {
        return AbstractC0039a.e(this);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return AbstractC0039a.f(this, i);
    }

    @Override // j$.util.t
    public boolean k(j$.util.function.f fVar) {
        fVar.getClass();
        return false;
    }
}
