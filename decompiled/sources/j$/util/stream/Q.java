package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class Q extends T {
    public Q(AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i) {
        super(abstractC0065c, i);
    }

    @Override // j$.util.stream.AbstractC0065c
    final boolean G0() {
        return true;
    }

    @Override // j$.util.stream.AbstractC0065c, j$.util.stream.AbstractC0089g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ U parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0065c, j$.util.stream.AbstractC0089g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ U sequential() {
        sequential();
        return this;
    }
}
