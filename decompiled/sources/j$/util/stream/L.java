package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
/* loaded from: classes2.dex */
public class L extends AbstractC0071d3 {
    public final /* synthetic */ int l = 1;
    final /* synthetic */ Object m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(T t, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, j$.util.function.g gVar) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = gVar;
    }

    @Override // j$.util.stream.AbstractC0061c
    public AbstractC0125m3 H0(int i, AbstractC0125m3 abstractC0125m3) {
        switch (this.l) {
            case 0:
                return new J(this, abstractC0125m3);
            case 1:
                return new F0(this, abstractC0125m3);
            case 2:
                return new Z0(this, abstractC0125m3);
            case 3:
                return new Y2(this, abstractC0125m3);
            default:
                return new Y2(this, abstractC0125m3, (j$.lang.a) null);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(L0 l0, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, j$.util.function.m mVar) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = mVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(AbstractC0069d1 abstractC0069d1, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, j$.util.function.r rVar) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = rVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(AbstractC0077e3 abstractC0077e3, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, Consumer consumer) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = consumer;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(AbstractC0077e3 abstractC0077e3, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, Predicate predicate) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = predicate;
    }
}
