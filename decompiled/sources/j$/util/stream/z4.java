package j$.util.stream;

import j$.util.AbstractC0034a;
import java.util.Comparator;
/* loaded from: classes2.dex */
public abstract class z4 extends D4 implements j$.util.w {
    public z4(j$.util.w wVar, long j, long j2) {
        super(wVar, j, j2, 0L, Math.min(wVar.estimateSize(), j2));
    }

    protected abstract Object f();

    @Override // j$.util.w
    /* renamed from: forEachRemaining */
    public void e(Object obj) {
        obj.getClass();
        long j = this.a;
        long j2 = this.e;
        if (j >= j2) {
            return;
        }
        long j3 = this.d;
        if (j3 >= j2) {
            return;
        }
        if (j3 >= j && ((j$.util.w) this.c).estimateSize() + j3 <= this.b) {
            ((j$.util.w) this.c).forEachRemaining(obj);
            this.d = this.e;
            return;
        }
        while (this.a > this.d) {
            ((j$.util.w) this.c).tryAdvance(f());
            this.d++;
        }
        while (this.d < this.e) {
            ((j$.util.w) this.c).tryAdvance(obj);
            this.d++;
        }
    }

    @Override // j$.util.u
    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override // j$.util.u
    public /* synthetic */ long getExactSizeIfKnown() {
        return AbstractC0034a.e(this);
    }

    @Override // j$.util.u
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return AbstractC0034a.f(this, i);
    }

    @Override // j$.util.w
    /* renamed from: tryAdvance */
    public boolean k(Object obj) {
        long j;
        obj.getClass();
        if (this.a >= this.e) {
            return false;
        }
        while (true) {
            long j2 = this.a;
            j = this.d;
            if (j2 <= j) {
                break;
            }
            ((j$.util.w) this.c).tryAdvance(f());
            this.d++;
        }
        if (j >= this.e) {
            return false;
        }
        this.d = j + 1;
        return ((j$.util.w) this.c).tryAdvance(obj);
    }

    public z4(j$.util.w wVar, long j, long j2, long j3, long j4, AbstractC0135o1 abstractC0135o1) {
        super(wVar, j, j2, j3, j4);
    }
}
