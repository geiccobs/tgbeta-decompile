package j$.util.stream;

import j$.util.C0045l;
/* renamed from: j$.util.stream.g0 */
/* loaded from: classes2.dex */
final class C0085g0 extends AbstractC0097i0 implements AbstractC0118l3 {
    @Override // j$.util.stream.AbstractC0097i0, j$.util.stream.AbstractC0124m3, j$.util.stream.AbstractC0118l3, j$.util.function.q
    public void accept(long j) {
        accept(Long.valueOf(j));
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }

    @Override // j$.util.function.y
    public Object get() {
        if (this.a) {
            return C0045l.d(((Long) this.b).longValue());
        }
        return null;
    }
}
