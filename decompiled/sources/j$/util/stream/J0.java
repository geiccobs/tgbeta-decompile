package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class J0 extends L0 {
    public J0(AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i) {
        super(abstractC0061c, i);
    }

    @Override // j$.util.stream.AbstractC0061c
    final boolean G0() {
        return true;
    }

    @Override // j$.util.stream.AbstractC0061c, j$.util.stream.AbstractC0085g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ IntStream parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0061c, j$.util.stream.AbstractC0085g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ IntStream sequential() {
        sequential();
        return this;
    }
}
