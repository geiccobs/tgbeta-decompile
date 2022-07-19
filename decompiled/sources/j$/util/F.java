package j$.util;

import j$.util.function.Consumer;
import java.util.Comparator;
/* loaded from: classes2.dex */
final class F extends H implements v {
    @Override // j$.util.v, j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0034a.l(this, consumer);
    }

    @Override // j$.util.v
    public void d(j$.util.function.q qVar) {
        qVar.getClass();
    }

    @Override // j$.util.v, j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0034a.d(this, consumer);
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

    @Override // j$.util.v
    public boolean i(j$.util.function.q qVar) {
        qVar.getClass();
        return false;
    }
}
