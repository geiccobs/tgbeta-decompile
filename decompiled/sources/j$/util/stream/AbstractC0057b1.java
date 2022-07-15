package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.b1 */
/* loaded from: classes2.dex */
public abstract class AbstractC0057b1 extends AbstractC0069d1 {
    public AbstractC0057b1(AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i) {
        super(abstractC0061c, i);
    }

    @Override // j$.util.stream.AbstractC0061c
    final boolean G0() {
        return true;
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
