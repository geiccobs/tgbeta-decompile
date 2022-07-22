package j$.util.stream;

import j$.util.AbstractC0038a;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
/* renamed from: j$.util.stream.k2 */
/* loaded from: classes2.dex */
abstract class AbstractC0116k2 implements j$.util.u {
    A1 a;
    int b;
    j$.util.u c;
    j$.util.u d;
    Deque e;

    public AbstractC0116k2(A1 a1) {
        this.a = a1;
    }

    public final A1 a(Deque deque) {
        while (true) {
            A1 a1 = (A1) deque.pollFirst();
            if (a1 != null) {
                if (a1.p() != 0) {
                    for (int p = a1.p() - 1; p >= 0; p--) {
                        deque.addFirst(a1.b(p));
                    }
                } else if (a1.count() > 0) {
                    return a1;
                }
            } else {
                return null;
            }
        }
    }

    @Override // j$.util.u
    public final int characteristics() {
        return 64;
    }

    @Override // j$.util.u
    public final long estimateSize() {
        long j = 0;
        if (this.a == null) {
            return 0L;
        }
        j$.util.u uVar = this.c;
        if (uVar != null) {
            return uVar.estimateSize();
        }
        for (int i = this.b; i < this.a.p(); i++) {
            j += this.a.b(i).count();
        }
        return j;
    }

    public final Deque f() {
        ArrayDeque arrayDeque = new ArrayDeque(8);
        int p = this.a.p();
        while (true) {
            p--;
            if (p >= this.b) {
                arrayDeque.addFirst(this.a.b(p));
            } else {
                return arrayDeque;
            }
        }
    }

    @Override // j$.util.u
    public Comparator getComparator() {
        throw new IllegalStateException();
    }

    @Override // j$.util.u
    public /* synthetic */ long getExactSizeIfKnown() {
        return AbstractC0038a.e(this);
    }

    public final boolean h() {
        if (this.a == null) {
            return false;
        }
        if (this.d != null) {
            return true;
        }
        j$.util.u uVar = this.c;
        if (uVar == null) {
            Deque f = f();
            this.e = f;
            A1 a = a(f);
            if (a == null) {
                this.a = null;
                return false;
            }
            uVar = a.mo69spliterator();
        }
        this.d = uVar;
        return true;
    }

    @Override // j$.util.u
    public /* synthetic */ boolean hasCharacteristics(int i) {
        return AbstractC0038a.f(this, i);
    }

    @Override // j$.util.u
    public final j$.util.u trySplit() {
        A1 a1 = this.a;
        if (a1 == null || this.d != null) {
            return null;
        }
        j$.util.u uVar = this.c;
        if (uVar != null) {
            return uVar.trySplit();
        }
        if (this.b < a1.p() - 1) {
            A1 a12 = this.a;
            int i = this.b;
            this.b = i + 1;
            return a12.b(i).mo69spliterator();
        }
        A1 b = this.a.b(this.b);
        this.a = b;
        if (b.p() == 0) {
            j$.util.u mo69spliterator = this.a.mo69spliterator();
            this.c = mo69spliterator;
            return mo69spliterator.trySplit();
        }
        this.b = 0;
        A1 a13 = this.a;
        this.b = 1;
        return a13.b(0).mo69spliterator();
    }
}
