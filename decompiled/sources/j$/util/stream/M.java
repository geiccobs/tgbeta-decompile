package j$.util.stream;

import j$.util.function.Function;
import j$.util.function.ToIntFunction;
import j$.wrappers.C0197b0;
import j$.wrappers.C0221n0;
/* loaded from: classes2.dex */
class M extends K0 {
    public final /* synthetic */ int l = 1;
    final /* synthetic */ Object m;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(T t, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, j$.wrappers.G g) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = g;
    }

    @Override // j$.util.stream.AbstractC0060c
    public AbstractC0124m3 H0(int i, AbstractC0124m3 abstractC0124m3) {
        switch (this.l) {
            case 0:
                return new J(this, abstractC0124m3);
            case 1:
                return new F0(this, abstractC0124m3);
            case 2:
                return new F0(this, abstractC0124m3, (j$.lang.a) null);
            case 3:
                return new F0(this, abstractC0124m3, (j$.lang.b) null);
            case 4:
                return new F0(this, abstractC0124m3, (j$.lang.c) null);
            case 5:
                return new Z0(this, abstractC0124m3);
            case 6:
                return new Y2(this, abstractC0124m3);
            default:
                return new r(this, abstractC0124m3);
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(L0 l0, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, j$.util.function.l lVar) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = lVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(L0 l0, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, j$.util.function.m mVar) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = mVar;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(L0 l0, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, j$.wrappers.V v) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = v;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(L0 l0, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, C0197b0 c0197b0) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = c0197b0;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(AbstractC0068d1 abstractC0068d1, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, C0221n0 c0221n0) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = c0221n0;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(AbstractC0076e3 abstractC0076e3, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, Function function) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = function;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public M(AbstractC0076e3 abstractC0076e3, AbstractC0060c abstractC0060c, EnumC0077e4 enumC0077e4, int i, ToIntFunction toIntFunction) {
        super(abstractC0060c, enumC0077e4, i);
        this.m = toIntFunction;
    }
}
