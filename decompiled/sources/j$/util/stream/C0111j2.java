package j$.util.stream;

import j$.util.function.Consumer;
import java.util.Deque;
/* renamed from: j$.util.stream.j2 */
/* loaded from: classes2.dex */
final class C0111j2 extends AbstractC0117k2 {
    public C0111j2(A1 a1) {
        super(a1);
    }

    @Override // j$.util.u
    public boolean b(Consumer consumer) {
        A1 a;
        if (!h()) {
            return false;
        }
        boolean b = this.d.b(consumer);
        if (!b) {
            if (this.c == null && (a = a(this.e)) != null) {
                j$.util.u mo69spliterator = a.mo69spliterator();
                this.d = mo69spliterator;
                return mo69spliterator.b(consumer);
            }
            this.a = null;
        }
        return b;
    }

    @Override // j$.util.u
    public void forEachRemaining(Consumer consumer) {
        if (this.a == null) {
            return;
        }
        if (this.d != null) {
            do {
            } while (b(consumer));
            return;
        }
        j$.util.u uVar = this.c;
        if (uVar != null) {
            uVar.forEachRemaining(consumer);
            return;
        }
        Deque f = f();
        while (true) {
            A1 a = a(f);
            if (a == null) {
                this.a = null;
                return;
            }
            a.forEach(consumer);
        }
    }
}
