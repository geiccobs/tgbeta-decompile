package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.o0 */
/* loaded from: classes2.dex */
abstract class AbstractC0133o0 implements N4, O4 {
    private final boolean a;

    public AbstractC0133o0(boolean z) {
        this.a = z;
    }

    public /* synthetic */ void accept(double d) {
        AbstractC0134o1.f(this);
        throw null;
    }

    public /* synthetic */ void accept(int i) {
        AbstractC0134o1.d(this);
        throw null;
    }

    public /* synthetic */ void accept(long j) {
        AbstractC0134o1.e(this);
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
        return EnumC0071d4.r;
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        (this.a ? new C0145q0(abstractC0187y2, uVar, this) : new C0150r0(abstractC0187y2, uVar, abstractC0187y2.v0(this))).invoke();
        return null;
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        AbstractC0060c abstractC0060c = (AbstractC0060c) abstractC0187y2;
        abstractC0060c.n0(abstractC0060c.v0(this), uVar);
        return null;
    }

    @Override // j$.util.function.y
    public /* bridge */ /* synthetic */ Object get() {
        return null;
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void n(long j) {
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ boolean o() {
        return false;
    }
}
