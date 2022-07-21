package j$.util.stream;

import java.util.Comparator;
/* loaded from: classes2.dex */
abstract class F3 extends AbstractC0106i3 {
    protected final Comparator b;
    protected boolean c;

    public F3(AbstractC0130m3 abstractC0130m3, Comparator comparator) {
        super(abstractC0130m3);
        this.b = comparator;
    }

    @Override // j$.util.stream.AbstractC0106i3, j$.util.stream.AbstractC0130m3
    public final boolean o() {
        this.c = true;
        return false;
    }
}
