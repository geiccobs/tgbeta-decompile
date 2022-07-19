package j$.util.stream;
/* renamed from: j$.util.stream.c1 */
/* loaded from: classes2.dex */
public abstract class AbstractC0068c1 extends AbstractC0074d1 {
    public AbstractC0068c1(AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i) {
        super(abstractC0066c, i);
    }

    @Override // j$.util.stream.AbstractC0066c
    final boolean G0() {
        return false;
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
