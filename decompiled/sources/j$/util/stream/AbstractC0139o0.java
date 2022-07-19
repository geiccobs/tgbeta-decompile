package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.o0 */
/* loaded from: classes2.dex */
abstract class AbstractC0139o0 implements N4, O4 {
    private final boolean a;

    public AbstractC0139o0(boolean z) {
        this.a = z;
    }

    public /* synthetic */ void accept(double d) {
        AbstractC0140o1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        AbstractC0140o1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        AbstractC0140o1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.stream.N4
    public int b() {
        if (this.a) {
            return 0;
        }
        return EnumC0077d4.r;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractC0193y2 abstractC0193y2, j$.util.u uVar) {
        (this.a ? new C0151q0(abstractC0193y2, uVar, this) : new C0156r0(abstractC0193y2, uVar, abstractC0193y2.v0(this))).invoke();
        return null;
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractC0193y2 abstractC0193y2, j$.util.u uVar) {
        AbstractC0066c abstractC0066c = (AbstractC0066c) abstractC0193y2;
        abstractC0066c.n0(abstractC0066c.v0(this), uVar);
        return null;
    }

    @Override // j$.util.function.y
    public /* bridge */ /* synthetic */ Object get() {
        return null;
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ void n(long j) {
    }

    @Override // j$.util.stream.AbstractC0130m3
    public /* synthetic */ boolean o() {
        return false;
    }
}
