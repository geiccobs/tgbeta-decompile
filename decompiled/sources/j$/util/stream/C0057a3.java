package j$.util.stream;

import j$.util.function.Function;
/* renamed from: j$.util.stream.a3 */
/* loaded from: classes2.dex */
class C0057a3 extends AbstractC0075d3 {
    public final /* synthetic */ int l;
    final /* synthetic */ Function m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0057a3(AbstractC0081e3 abstractC0081e3, AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i, Function function, int i2) {
        super(abstractC0065c, enumC0082e4, i);
        this.l = i2;
        if (i2 != 1) {
            this.m = function;
            return;
        }
        this.m = function;
        super(abstractC0065c, enumC0082e4, i);
    }

    @Override // j$.util.stream.AbstractC0065c
    public AbstractC0129m3 H0(int i, AbstractC0129m3 abstractC0129m3) {
        switch (this.l) {
            case 0:
                return new Y2(this, abstractC0129m3);
            default:
                return new Y2(this, abstractC0129m3, (j$.lang.a) null);
        }
    }
}
