package j$.util.stream;

import j$.util.function.Function;
/* renamed from: j$.util.stream.a3 */
/* loaded from: classes2.dex */
class C0053a3 extends AbstractC0071d3 {
    public final /* synthetic */ int l;
    final /* synthetic */ Function m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0053a3(AbstractC0077e3 abstractC0077e3, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, Function function, int i2) {
        super(abstractC0061c, enumC0078e4, i);
        this.l = i2;
        if (i2 != 1) {
            this.m = function;
            return;
        }
        this.m = function;
        super(abstractC0061c, enumC0078e4, i);
    }

    @Override // j$.util.stream.AbstractC0061c
    public AbstractC0125m3 H0(int i, AbstractC0125m3 abstractC0125m3) {
        switch (this.l) {
            case 0:
                return new Y2(this, abstractC0125m3);
            default:
                return new Y2(this, abstractC0125m3, (j$.lang.a) null);
        }
    }
}
