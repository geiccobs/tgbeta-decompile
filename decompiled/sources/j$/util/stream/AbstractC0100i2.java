package j$.util.stream;

import java.util.Deque;
/* renamed from: j$.util.stream.i2 */
/* loaded from: classes2.dex */
abstract class AbstractC0100i2 extends AbstractC0112k2 implements j$.util.w {
    public AbstractC0100i2(AbstractC0192z1 abstractC0192z1) {
        super(abstractC0192z1);
    }

    @Override // j$.util.w
    /* renamed from: forEachRemaining */
    public void e(Object obj) {
        if (this.a == null) {
            return;
        }
        if (this.d != null) {
            do {
            } while (k(obj));
            return;
        }
        j$.util.u uVar = this.c;
        if (uVar != null) {
            ((j$.util.w) uVar).forEachRemaining(obj);
            return;
        }
        Deque f = f();
        while (true) {
            AbstractC0192z1 abstractC0192z1 = (AbstractC0192z1) a(f);
            if (abstractC0192z1 == null) {
                this.a = null;
                return;
            }
            abstractC0192z1.g(obj);
        }
    }

    @Override // j$.util.w
    /* renamed from: tryAdvance */
    public boolean k(Object obj) {
        AbstractC0192z1 abstractC0192z1;
        if (!h()) {
            return false;
        }
        boolean tryAdvance = ((j$.util.w) this.d).tryAdvance(obj);
        if (!tryAdvance) {
            if (this.c == null && (abstractC0192z1 = (AbstractC0192z1) a(this.e)) != null) {
                j$.util.w mo69spliterator = abstractC0192z1.mo69spliterator();
                this.d = mo69spliterator;
                return mo69spliterator.tryAdvance(obj);
            }
            this.a = null;
        }
        return tryAdvance;
    }
}
