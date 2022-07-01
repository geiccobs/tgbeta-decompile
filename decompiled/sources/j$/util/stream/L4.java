package j$.util.stream;

import j$.util.function.Consumer;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class L4 extends AbstractC0083f4 {
    public L4(AbstractC0187y2 abstractC0187y2, j$.util.function.y yVar, boolean z) {
        super(abstractC0187y2, yVar, z);
    }

    L4(AbstractC0187y2 abstractC0187y2, j$.util.u uVar, boolean z) {
        super(abstractC0187y2, uVar, z);
    }

    @Override // j$.util.u
    public boolean b(Consumer consumer) {
        Object obj;
        consumer.getClass();
        boolean a = a();
        if (a) {
            C0053a4 c0053a4 = (C0053a4) this.h;
            long j = this.g;
            if (c0053a4.c != 0) {
                if (j >= c0053a4.count()) {
                    throw new IndexOutOfBoundsException(Long.toString(j));
                }
                for (int i = 0; i <= c0053a4.c; i++) {
                    long[] jArr = c0053a4.d;
                    long j2 = jArr[i];
                    Object[][] objArr = c0053a4.f;
                    if (j < j2 + objArr[i].length) {
                        obj = objArr[i][(int) (j - jArr[i])];
                    }
                }
                throw new IndexOutOfBoundsException(Long.toString(j));
            } else if (j >= c0053a4.b) {
                throw new IndexOutOfBoundsException(Long.toString(j));
            } else {
                obj = c0053a4.e[(int) j];
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

    @Override // j$.util.stream.AbstractC0083f4
    void j() {
        C0053a4 c0053a4 = new C0053a4();
        this.h = c0053a4;
        this.e = this.b.v0(new K4(c0053a4));
        this.f = new C0054b(this);
    }

    @Override // j$.util.stream.AbstractC0083f4
    AbstractC0083f4 l(j$.util.u uVar) {
        return new L4(this.b, uVar, this.a);
    }
}
