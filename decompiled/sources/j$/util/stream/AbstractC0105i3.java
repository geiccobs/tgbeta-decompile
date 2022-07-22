package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.i3 */
/* loaded from: classes2.dex */
public abstract class AbstractC0105i3 implements AbstractC0129m3 {
    protected final AbstractC0129m3 a;

    public AbstractC0105i3(AbstractC0129m3 abstractC0129m3) {
        abstractC0129m3.getClass();
        this.a = abstractC0129m3;
    }

    @Override // j$.util.stream.AbstractC0129m3
    public /* synthetic */ void accept(double d) {
        AbstractC0139o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0129m3
    public /* synthetic */ void accept(int i) {
        AbstractC0139o1.d(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0129m3, j$.util.stream.AbstractC0123l3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        AbstractC0139o1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.stream.AbstractC0129m3
    public void m() {
        this.a.m();
    }

    @Override // j$.util.stream.AbstractC0129m3
    public boolean o() {
        return this.a.o();
    }
}
