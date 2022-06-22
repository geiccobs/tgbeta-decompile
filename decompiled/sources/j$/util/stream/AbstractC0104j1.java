package j$.util.stream;

import j$.util.function.Consumer;
/* renamed from: j$.util.stream.j1 */
/* loaded from: classes2.dex */
abstract class AbstractC0104j1 implements AbstractC0124m3 {
    boolean a;
    boolean b;

    public AbstractC0104j1(EnumC0110k1 enumC0110k1) {
        boolean z;
        z = enumC0110k1.b;
        this.b = !z;
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void accept(double d) {
        AbstractC0134o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void accept(int i) {
        AbstractC0134o1.d(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0124m3, j$.util.stream.AbstractC0118l3, j$.util.function.q
    public /* synthetic */ void accept(long j) {
        AbstractC0134o1.e(this);
        throw null;
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void m() {
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void n(long j) {
    }

    @Override // j$.util.stream.AbstractC0124m3
    public boolean o() {
        return this.a;
    }
}
