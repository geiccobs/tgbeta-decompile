package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* renamed from: j$.util.stream.b1 */
/* loaded from: classes2.dex */
public abstract class AbstractC0062b1 extends AbstractC0074d1 {
    public AbstractC0062b1(AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i) {
        super(abstractC0066c, i);
    }

    @Override // j$.util.stream.AbstractC0066c
    final boolean G0() {
        return true;
    }

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ AbstractC0080e1 parallel() {
        parallel();
        return this;
    }

    @Override // j$.util.stream.AbstractC0066c, j$.util.stream.AbstractC0090g, j$.util.stream.IntStream
    public /* bridge */ /* synthetic */ AbstractC0080e1 sequential() {
        sequential();
        return this;
    }
}
