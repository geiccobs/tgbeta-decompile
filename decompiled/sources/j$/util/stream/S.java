package j$.util.stream;
/* loaded from: classes2.dex */
public abstract class S extends T {
    public S(AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i) {
        super(abstractC0060c, i);
    }

    @Override // j$.util.stream.AbstractC0060c
    final boolean G0() {
        return false;
    }

    @Override // j$.util.stream.AbstractC0060c, j$.util.stream.AbstractC0084g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ U parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0060c, j$.util.stream.AbstractC0084g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ U sequential() {
        sequential();
        return this;
    }
}
