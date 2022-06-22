package j$.util.stream;

import j$.util.C0043j;
/* renamed from: j$.util.stream.e0 */
/* loaded from: classes2.dex */
final class C0073e0 extends AbstractC0097i0 implements AbstractC0106j3 {
    @Override // j$.util.stream.AbstractC0097i0, j$.util.stream.AbstractC0124m3
    public void accept(double d) {
        accept(Double.valueOf(d));
    }

    @Override // j$.util.function.y
    public Object get() {
        if (this.a) {
            return C0043j.d(((Double) this.b).doubleValue());
        }
        return null;
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }
}
