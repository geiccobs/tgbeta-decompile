package j$.util.stream;

import j$.util.AbstractC0034a;
import j$.util.AbstractC0035b;
import j$.util.Collection$EL;
import j$.util.function.Consumer;
import java.util.Collection;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class E1 implements A1 {
    private final Collection a;

    public E1(Collection collection) {
        this.a = collection;
    }

    @Override // j$.util.stream.A1
    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override // j$.util.stream.A1
    public long count() {
        return this.a.size();
    }

    @Override // j$.util.stream.A1
    public void forEach(Consumer consumer) {
        Collection$EL.a(this.a, consumer);
    }

    @Override // j$.util.stream.A1
    public void i(Object[] objArr, int i) {
        for (Object obj : this.a) {
            objArr[i] = obj;
            i++;
        }
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ int p() {
        return 0;
    }

    @Override // j$.util.stream.A1
    public Object[] q(j$.util.function.m mVar) {
        Collection collection = this.a;
        return collection.toArray((Object[]) mVar.apply(collection.size()));
    }

    @Override // j$.util.stream.A1
    public /* synthetic */ A1 r(long j, long j2, j$.util.function.m mVar) {
        return AbstractC0135o1.q(this, j, j2, mVar);
    }

    @Override // j$.util.stream.A1
    /* renamed from: spliterator */
    public j$.util.u mo69spliterator() {
        Collection collection = this.a;
        return (collection instanceof AbstractC0035b ? ((AbstractC0035b) collection).stream() : AbstractC0034a.i(collection)).spliterator();
    }

    public String toString() {
        return String.format("CollectionNode[%d][%s]", Integer.valueOf(this.a.size()), this.a);
    }
}
