package j$.util.stream;

import j$.util.function.Consumer;
import java.util.Arrays;
/* renamed from: j$.util.stream.m2 */
/* loaded from: classes2.dex */
public final class C0123m2 extends C0117l2 implements AbstractC0151r1 {
    public C0123m2(long j) {
        super(j);
    }

    @Override // j$.util.stream.AbstractC0151r1, j$.util.stream.AbstractC0156s1
    /* renamed from: a */
    public AbstractC0186y1 mo69a() {
        if (this.b >= this.a.length) {
            return this;
        }
        throw new IllegalStateException(String.format("Current size %d is less than fixed size %d", Integer.valueOf(this.b), Integer.valueOf(this.a.length)));
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void accept(double d) {
        AbstractC0134o1.f(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ void accept(int i) {
        AbstractC0134o1.d(this);
        throw null;
    }

    @Override // j$.util.stream.AbstractC0124m3, j$.util.stream.AbstractC0118l3, j$.util.function.q
    public void accept(long j) {
        int i = this.b;
        long[] jArr = this.a;
        if (i < jArr.length) {
            this.b = i + 1;
            jArr[i] = j;
            return;
        }
        throw new IllegalStateException(String.format("Accept exceeded fixed size of %d", Integer.valueOf(this.a.length)));
    }

    @Override // j$.util.function.Consumer
    public /* synthetic */ Consumer andThen(Consumer consumer) {
        return consumer.getClass();
    }

    @Override // j$.util.function.q
    public j$.util.function.q f(j$.util.function.q qVar) {
        qVar.getClass();
        return new j$.util.function.p(this, qVar);
    }

    /* renamed from: l */
    public /* synthetic */ void accept(Long l) {
        AbstractC0134o1.c(this, l);
    }

    @Override // j$.util.stream.AbstractC0124m3
    public void m() {
        if (this.b >= this.a.length) {
            return;
        }
        throw new IllegalStateException(String.format("End size %d is less than fixed size %d", Integer.valueOf(this.b), Integer.valueOf(this.a.length)));
    }

    @Override // j$.util.stream.AbstractC0124m3
    public void n(long j) {
        if (j == this.a.length) {
            this.b = 0;
            return;
        }
        throw new IllegalStateException(String.format("Begin size %d is not equal to fixed size %d", Long.valueOf(j), Integer.valueOf(this.a.length)));
    }

    @Override // j$.util.stream.AbstractC0124m3
    public /* synthetic */ boolean o() {
        return false;
    }

    @Override // j$.util.stream.C0117l2
    public String toString() {
        return String.format("LongFixedNodeBuilder[%d][%s]", Integer.valueOf(this.a.length - this.b), Arrays.toString(this.a));
    }
}
