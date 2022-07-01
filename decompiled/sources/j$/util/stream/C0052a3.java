package j$.util.stream;

import j$.util.function.Function;
/* renamed from: j$.util.stream.a3 */
/* loaded from: classes2.dex */
class C0052a3 extends AbstractC0070d3 {
    public final /* synthetic */ int l;
    final /* synthetic */ Function m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0052a3(AbstractC0076e3 abstractC0076e3, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, Function function, int i2) {
        super(abstractC0060c, enumC0077e4, i);
        this.l = i2;
        if (i2 != 1) {
            this.m = function;
            return;
        }
        this.m = function;
        super(abstractC0060c, enumC0077e4, i);
    }

    @Override // j$.util.stream.AbstractC0060c
    public AbstractC0124m3 H0(int i, AbstractC0124m3 abstractC0124m3) {
        switch (this.l) {
            case 0:
                return new Y2(this, abstractC0124m3);
            default:
                return new Y2(this, abstractC0124m3, (j$.lang.a) null);
        }
    }
}
