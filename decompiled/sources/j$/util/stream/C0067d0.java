package j$.util.stream;

import j$.util.function.Predicate;
/* renamed from: j$.util.stream.d0 */
/* loaded from: classes2.dex */
public final class C0067d0 implements N4 {
    private final EnumC0077e4 a;
    final boolean b;
    final Object c;
    final Predicate d;
    final j$.util.function.y e;

    public C0067d0(boolean z, EnumC0077e4 enumC0077e4, Object obj, Predicate predicate, j$.util.function.y yVar) {
        this.b = z;
        this.a = enumC0077e4;
        this.c = obj;
        this.d = predicate;
        this.e = yVar;
    }

    @Override // j$.util.stream.N4
    public int b() {
        return EnumC0071d4.u | (this.b ? 0 : EnumC0071d4.r);
    }

    @Override // j$.util.stream.N4
    public Object c(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        return new C0103j0(this, abstractC0187y2, uVar).invoke();
    }

    @Override // j$.util.stream.N4
    public Object d(AbstractC0187y2 abstractC0187y2, j$.util.u uVar) {
        O4 o4 = (O4) this.e.get();
        AbstractC0060c abstractC0060c = (AbstractC0060c) abstractC0187y2;
        o4.getClass();
        abstractC0060c.n0(abstractC0060c.v0(o4), uVar);
        Object obj = o4.get();
        return obj != null ? obj : this.c;
    }
}
