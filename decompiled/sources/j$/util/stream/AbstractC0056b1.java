package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.b1 */
/* loaded from: classes2.dex */
public abstract class AbstractC0056b1 extends AbstractC0068d1 {
    public AbstractC0056b1(AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i) {
        super(abstractC0060c, i);
    }

    @Override // j$.util.stream.AbstractC0060c
    final boolean G0() {
        return true;
    }

    @Override // j$.util.stream.AbstractC0060c, j$.util.stream.AbstractC0084g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ AbstractC0074e1 parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0060c, j$.util.stream.AbstractC0084g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ AbstractC0074e1 sequential() {
        sequential();
        return this;
    }
}
