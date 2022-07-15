package j$.util.stream;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class R1 extends C1 implements AbstractC0192z1 {
    public R1(AbstractC0192z1 abstractC0192z1, AbstractC0192z1 abstractC0192z12) {
        super(abstractC0192z1, abstractC0192z12);
    }

    @Override // j$.util.stream.AbstractC0192z1
    public void d(Object obj, int i) {
        ((AbstractC0192z1) this.a).d(obj, i);
        ((AbstractC0192z1) this.b).d(obj, i + ((int) ((AbstractC0192z1) this.a).count()));
    }

    @Override // j$.util.stream.AbstractC0192z1
    public Object e() {
        long count = count();
        if (count < 2147483639) {
            Object c = c((int) count);
            d(c, 0);
            return c;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }

    @Override // j$.util.stream.AbstractC0192z1
    public void g(Object obj) {
        ((AbstractC0192z1) this.a).g(obj);
        ((AbstractC0192z1) this.b).g(obj);
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ Object[] q(j$.util.function.m mVar) {
        return AbstractC0135o1.g(this, mVar);
    }

    public String toString() {
        return count() < 32 ? String.format("%s[%s.%s]", getClass().getName(), this.a, this.b) : String.format("%s[size=%d]", getClass().getName(), Long.valueOf(count()));
    }
}
