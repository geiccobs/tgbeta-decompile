package j$.util.stream;

import java.util.Comparator;
/* loaded from: classes2.dex */
abstract class F3 extends AbstractC0105i3 {
    protected final Comparator b;
    protected boolean c;

    public F3(AbstractC0129m3 abstractC0129m3, Comparator comparator) {
        super(abstractC0129m3);
        this.b = comparator;
    }

    @Override // j$.util.stream.AbstractC0105i3, j$.util.stream.AbstractC0129m3
    public final boolean o() {
        this.c = true;
        return false;
    }
}
