package j$.util;

import j$.util.function.Consumer;
/* renamed from: j$.util.m */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0047m implements j$.util.function.f {
    public final /* synthetic */ Consumer a;

    @Override // j$.util.function.f
    public final void accept(double d) {
        this.a.accept(Double.valueOf(d));
    }

    @Override // j$.util.function.f
    public j$.util.function.f j(j$.util.function.f fVar) {
        fVar.getClass();
        return new j$.util.function.e(this, fVar);
    }
}
