package j$.util.stream;

import j$.util.function.Function;
/* renamed from: j$.util.stream.a3 */
/* loaded from: classes2.dex */
class C0058a3 extends AbstractC0076d3 {
    public final /* synthetic */ int l;
    final /* synthetic */ Function m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0058a3(AbstractC0082e3 abstractC0082e3, AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i, Function function, int i2) {
        super(abstractC0066c, enumC0083e4, i);
        this.l = i2;
        if (i2 != 1) {
            this.m = function;
            return;
        }
        this.m = function;
        super(abstractC0066c, enumC0083e4, i);
    }

    @Override // j$.util.stream.AbstractC0066c
    public AbstractC0130m3 H0(int i, AbstractC0130m3 abstractC0130m3) {
        switch (this.l) {
            case 0:
                return new Y2(this, abstractC0130m3);
            default:
                return new Y2(this, abstractC0130m3, (j$.lang.a) null);
        }
    }
}
