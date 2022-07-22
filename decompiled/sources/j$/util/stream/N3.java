package j$.util.stream;

import j$.util.AbstractC0038a;
import j$.util.Collection$EL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
/* loaded from: classes2.dex */
final class N3 extends F3 {
    private ArrayList d;

    public N3(AbstractC0129m3 abstractC0129m3, Comparator comparator) {
        super(abstractC0129m3, comparator);
    }

    @Override // j$.util.function.Consumer
    public void accept(Object obj) {
        this.d.add(obj);
    }

    @Override // j$.util.stream.AbstractC0105i3, j$.util.stream.AbstractC0129m3
    public void m() {
        AbstractC0038a.G(this.d, this.b);
        this.a.n(this.d.size());
        if (!this.c) {
            ArrayList arrayList = this.d;
            AbstractC0129m3 abstractC0129m3 = this.a;
            abstractC0129m3.getClass();
            Collection$EL.a(arrayList, new C0059b(abstractC0129m3));
        } else {
            Iterator it = this.d.iterator();
            while (it.hasNext()) {
                Object next = it.next();
                if (this.a.o()) {
                    break;
                }
                this.a.accept((AbstractC0129m3) next);
            }
        }
        this.a.m();
        this.d = null;
    }

    @Override // j$.util.stream.AbstractC0129m3
    public void n(long j) {
        if (j < 2147483639) {
            this.d = j >= 0 ? new ArrayList((int) j) : new ArrayList();
            return;
        }
        throw new IllegalArgumentException("Stream size exceeds max array size");
    }
}
