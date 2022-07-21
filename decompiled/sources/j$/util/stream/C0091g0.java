package j$.util.stream;

import j$.util.C0051l;
/* renamed from: j$.util.stream.g0 */
/* loaded from: classes2.dex */
final class C0091g0 extends AbstractC0103i0 implements AbstractC0124l3 {
    @Override // j$.util.stream.AbstractC0103i0, j$.util.stream.AbstractC0130m3, j$.util.stream.AbstractC0124l3, j$.util.function.q
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
            return C0051l.d(((Long) this.b).longValue());
        }
        return null;
    }
}
