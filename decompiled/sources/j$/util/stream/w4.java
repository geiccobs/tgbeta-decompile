package j$.util.stream;

import j$.util.AbstractC0039a;
import j$.util.function.Consumer;
import j$.util.u;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class w4 extends z4 implements u.a {
    public w4(u.a aVar, long j, long j2) {
        super(aVar, j, j2);
    }

    w4(u.a aVar, long j, long j2, long j3, long j4) {
        super(aVar, j, j2, j3, j4, null);
    }

    @Override // j$.util.stream.D4
    protected j$.util.u a(j$.util.u uVar, long j, long j2, long j3, long j4) {
        return new w4((u.a) uVar, j, j2, j3, j4);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean b(Consumer consumer) {
        return AbstractC0039a.k(this, consumer);
    }

    @Override // j$.util.stream.z4
    protected /* bridge */ /* synthetic */ Object f() {
        return v4.a;
    }

    @Override // j$.util.u
    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        AbstractC0039a.c(this, consumer);
    }
}
