package j$.util.stream;

import j$.util.C0050l;
/* renamed from: j$.util.stream.g0 */
/* loaded from: classes2.dex */
final class C0090g0 extends AbstractC0102i0 implements AbstractC0123l3 {
    @Override // j$.util.stream.AbstractC0102i0, j$.util.stream.AbstractC0129m3, j$.util.stream.AbstractC0123l3, j$.util.function.q
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
            return C0050l.d(((Long) this.b).longValue());
        }
        return null;
    }
}
