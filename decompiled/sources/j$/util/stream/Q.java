package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class Q extends T {
    public Q(AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i) {
        super(abstractC0066c, i);
    }

    @Override // j$.util.stream.AbstractC0066c
    final boolean G0() {
        return true;
    }

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ U parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ U sequential() {
        sequential();
        return this;
    }
}
