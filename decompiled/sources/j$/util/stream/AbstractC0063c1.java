package j$.util.stream;
/* renamed from: j$.util.stream.c1 */
/* loaded from: classes2.dex */
public abstract class AbstractC0063c1 extends AbstractC0069d1 {
    public AbstractC0063c1(AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i) {
        super(abstractC0061c, i);
    }

    @Override // j$.util.stream.AbstractC0061c
    final boolean G0() {
        return false;
    }

    @Override // j$.util.stream.AbstractC0061c, j$.util.stream.AbstractC0085g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ AbstractC0075e1 parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0061c, j$.util.stream.AbstractC0085g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ AbstractC0075e1 sequential() {
        sequential();
        return this;
    }
}
