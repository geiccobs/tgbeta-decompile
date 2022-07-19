package j$.util.stream;

import j$.util.C0045k;
/* renamed from: j$.util.stream.f0 */
/* loaded from: classes2.dex */
final class C0080f0 extends AbstractC0098i0 implements AbstractC0113k3 {
    @Override // j$.util.stream.AbstractC0098i0, j$.util.stream.AbstractC0125m3
    public void accept(int i) {
        accept(Integer.valueOf(i));
    }

    @Override // j$.util.function.y
    public Object get() {
        if (this.a) {
            return C0045k.d(((Integer) this.b).intValue());
        }
        return null;
    }

    @Override // j$.util.function.l
    public j$.util.function.l l(j$.util.function.l lVar) {
        lVar.getClass();
        return new j$.util.function.k(this, lVar);
    }
}
