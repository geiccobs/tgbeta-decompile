package j$.util.stream;

import j$.util.function.Consumer;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class L4 extends AbstractC0084f4 {
    public L4(AbstractC0188y2 abstractC0188y2, j$.util.function.y yVar, boolean z) {
        super(abstractC0188y2, yVar, z);
    }

    L4(AbstractC0188y2 abstractC0188y2, j$.util.u uVar, boolean z) {
        super(abstractC0188y2, uVar, z);
    }

    @Override // j$.util.u
    public boolean b(Consumer consumer) {
        Object obj;
        consumer.getClass();
        boolean a = a();
        if (a) {
            C0054a4 c0054a4 = (C0054a4) this.h;
            long j = this.g;
            if (c0054a4.c != 0) {
                if (j >= c0054a4.count()) {
                    throw new IndexOutOfBoundsException(Long.toString(j));
                }
                for (int i = 0; i <= c0054a4.c; i++) {
                    long[] jArr = c0054a4.d;
                    long j2 = jArr[i];
                    Object[][] objArr = c0054a4.f;
                    if (j < j2 + objArr[i].length) {
                        obj = objArr[i][(int) (j - jArr[i])];
                    }
                }
                throw new IndexOutOfBoundsException(Long.toString(j));
            } else if (j >= c0054a4.b) {
                throw new IndexOutOfBoundsException(Long.toString(j));
            } else {
                obj = c0054a4.e[(int) j];
            }
            consumer.accept(obj);
        }
        return a;
    }

    @Override // j$.util.u
    public void forEachRemaining(Consumer consumer) {
        if (this.h != null || this.i) {
            do {
            } while (b(consumer));
            return;
        }
        consumer.getClass();
        h();
        this.b.u0(new K4(consumer), this.d);
        this.i = true;
    }

    @Override // j$.util.stream.AbstractC0084f4
    void j() {
        C0054a4 c0054a4 = new C0054a4();
        this.h = c0054a4;
        this.e = this.b.v0(new K4(c0054a4));
        this.f = new C0055b(this);
    }

    @Override // j$.util.stream.AbstractC0084f4
    AbstractC0084f4 l(j$.util.u uVar) {
        return new L4(this.b, uVar, this.a);
    }
}
