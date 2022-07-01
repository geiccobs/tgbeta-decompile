package j$.util.stream;

import j$.util.function.Consumer;
import j$.util.function.Predicate;
/* loaded from: classes2.dex */
public class L extends AbstractC0070d3 {
    public final /* synthetic */ int l = 1;
    final /* synthetic */ Object m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(T t, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, j$.util.function.g gVar) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = gVar;
    }

    @Override // j$.util.stream.AbstractC0060c
    public AbstractC0124m3 H0(int i, AbstractC0124m3 abstractC0124m3) {
        switch (this.l) {
            case 0:
                return new J(this, abstractC0124m3);
            case 1:
                return new F0(this, abstractC0124m3);
            case 2:
                return new Z0(this, abstractC0124m3);
            case 3:
                return new Y2(this, abstractC0124m3);
            default:
                return new Y2(this, abstractC0124m3, (j$.lang.a) null);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(L0 l0, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, j$.util.function.m mVar) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = mVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(AbstractC0068d1 abstractC0068d1, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, j$.util.function.r rVar) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = rVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(AbstractC0076e3 abstractC0076e3, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, Consumer consumer) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = consumer;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public L(AbstractC0076e3 abstractC0076e3, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, Predicate predicate) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = predicate;
    }
}
