package j$.util.stream;

import j$.util.function.Function;
import j$.wrappers.C0218j0;
/* loaded from: classes2.dex */
public class N extends AbstractC0067c1 {
    public final /* synthetic */ int l = 1;
    final /* synthetic */ Object m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(T t, AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i, j$.util.function.h hVar) {
        super(abstractC0065c, enumC0082e4, i);
        this.m = hVar;
    }

    @Override // j$.util.stream.AbstractC0065c
    public AbstractC0129m3 H0(int i, AbstractC0129m3 abstractC0129m3) {
        switch (this.l) {
            case 0:
                return new J(this, abstractC0129m3);
            case 1:
                return new F0(this, abstractC0129m3);
            case 2:
                return new Z0(this, abstractC0129m3);
            case 3:
                return new Z0(this, abstractC0129m3, (j$.lang.a) null);
            case 4:
                return new Z0(this, abstractC0129m3, (j$.lang.b) null);
            case 5:
                return new Z0(this, abstractC0129m3, (j$.lang.c) null);
            case 6:
                return new r(this, abstractC0129m3);
            default:
                return new Y2(this, abstractC0129m3);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(L0 l0, AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i, j$.util.function.n nVar) {
        super(abstractC0065c, enumC0082e4, i);
        this.m = nVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(AbstractC0073d1 abstractC0073d1, AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i, j$.util.function.q qVar) {
        super(abstractC0065c, enumC0082e4, i);
        this.m = qVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(AbstractC0073d1 abstractC0073d1, AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i, j$.util.function.r rVar) {
        super(abstractC0065c, enumC0082e4, i);
        this.m = rVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(AbstractC0073d1 abstractC0073d1, AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i, j$.util.function.t tVar) {
        super(abstractC0065c, enumC0082e4, i);
        this.m = tVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(AbstractC0073d1 abstractC0073d1, AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i, C0218j0 c0218j0) {
        super(abstractC0065c, enumC0082e4, i);
        this.m = c0218j0;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(AbstractC0081e3 abstractC0081e3, AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i, Function function) {
        super(abstractC0065c, enumC0082e4, i);
        this.m = function;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public N(AbstractC0081e3 abstractC0081e3, AbstractC0065c abstractC0065c, EnumC0082e4 enumC0082e4, int i, j$.util.function.A a) {
        super(abstractC0065c, enumC0082e4, i);
        this.m = a;
    }
}
