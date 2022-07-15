package j$.util.stream;

import j$.util.function.Function;
import j$.wrappers.C0214j0;
/* loaded from: classes2.dex */
public class N extends AbstractC0063c1 {
    public final /* synthetic */ int l = 1;
    final /* synthetic */ Object m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(T t, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, j$.util.function.h hVar) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = hVar;
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
                return new Z0(this, abstractC0125m3, (j$.lang.a) null);
            case 4:
                return new Z0(this, abstractC0125m3, (j$.lang.b) null);
            case 5:
                return new Z0(this, abstractC0125m3, (j$.lang.c) null);
            case 6:
                return new r(this, abstractC0125m3);
            default:
                return new Y2(this, abstractC0125m3);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(L0 l0, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, j$.util.function.n nVar) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = nVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(AbstractC0069d1 abstractC0069d1, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, j$.util.function.q qVar) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = qVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(AbstractC0069d1 abstractC0069d1, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, j$.util.function.r rVar) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = rVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(AbstractC0069d1 abstractC0069d1, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, j$.util.function.t tVar) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = tVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(AbstractC0069d1 abstractC0069d1, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, C0214j0 c0214j0) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = c0214j0;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(AbstractC0077e3 abstractC0077e3, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, Function function) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = function;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(AbstractC0077e3 abstractC0077e3, AbstractC0061c abstractC0061c, EnumC0078e4 enumC0078e4, int i, j$.util.function.A a) {
        super(abstractC0061c, enumC0078e4, i);
        this.m = a;
    }
}
