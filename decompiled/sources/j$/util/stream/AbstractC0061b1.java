package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.b1 */
/* loaded from: classes2.dex */
public abstract class AbstractC0061b1 extends AbstractC0073d1 {
    public AbstractC0061b1(AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i) {
        super(abstractC0065c, i);
    }

    @Override // j$.util.stream.AbstractC0065c
    final boolean G0() {
        return true;
    }

    @Override // j$.util.stream.AbstractC0065c, j$.util.stream.AbstractC0089g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ AbstractC0079e1 parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0065c, j$.util.stream.AbstractC0089g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ AbstractC0079e1 sequential() {
        sequential();
        return this;
    }
}
