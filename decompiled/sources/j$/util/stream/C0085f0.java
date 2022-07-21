package j$.util.stream;

import j$.util.C0050k;
/* renamed from: j$.util.stream.f0 */
/* loaded from: classes2.dex */
final class C0085f0 extends AbstractC0103i0 implements AbstractC0118k3 {
    @Override // j$.util.stream.AbstractC0103i0, j$.util.stream.AbstractC0130m3
    public void accept(int i) {
        accept(Integer.valueOf(i));
    }

    @Override // j$.util.function.y
    public Object get() {
        if (this.a) {
            return C0050k.d(((Integer) this.b).intValue());
        }
        return null;
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }
}
