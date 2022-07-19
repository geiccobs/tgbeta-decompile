package j$.util.stream;

import j$.util.function.Function;
import j$.wrappers.C0223l0;
/* loaded from: classes2.dex */
class K extends S {
    public final /* synthetic */ int l = 4;
    final /* synthetic */ Object m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public K(T t, AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i, j$.util.function.f fVar) {
        super(abstractC0066c, enumC0083e4, i);
        this.m = fVar;
    }

    @Override // j$.util.stream.AbstractC0066c
    public AbstractC0130m3 H0(int i, AbstractC0130m3 abstractC0130m3) {
        switch (this.l) {
            case 0:
                return new J(this, abstractC0130m3);
            case 1:
                return new J(this, abstractC0130m3, (j$.lang.a) null);
            case 2:
                return new J(this, abstractC0130m3, (j$.lang.b) null);
            case 3:
                return new J(this, abstractC0130m3, (j$.lang.c) null);
            case 4:
                return new F0(this, abstractC0130m3);
            case 5:
                return new Z0(this, abstractC0130m3);
            case 6:
                return new Y2(this, abstractC0130m3);
            default:
                return new r(this, abstractC0130m3);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public K(T t, AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i, j$.util.function.g gVar) {
        super(abstractC0066c, enumC0083e4, i);
        this.m = gVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public K(T t, AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i, j$.wrappers.E e) {
        super(abstractC0066c, enumC0083e4, i);
        this.m = e;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public K(T t, AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i, j$.wrappers.K k) {
        super(abstractC0066c, enumC0083e4, i);
        this.m = k;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public K(L0 l0, AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i, j$.wrappers.X x) {
        super(abstractC0066c, enumC0083e4, i);
        this.m = x;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public K(AbstractC0074d1 abstractC0074d1, AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i, C0223l0 c0223l0) {
        super(abstractC0066c, enumC0083e4, i);
        this.m = c0223l0;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public K(AbstractC0082e3 abstractC0082e3, AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i, Function function) {
        super(abstractC0066c, enumC0083e4, i);
        this.m = function;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public K(AbstractC0082e3 abstractC0082e3, AbstractC0066c abstractC0066c, EnumC0083e4 enumC0083e4, int i, j$.util.function.z zVar) {
        super(abstractC0066c, enumC0083e4, i);
        this.m = zVar;
    }
}
