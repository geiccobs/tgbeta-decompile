package j$.util.stream;
/* renamed from: j$.util.stream.v2 */
/* loaded from: classes2.dex */
class C0178v2 extends AbstractC0183w2 {
    public final /* synthetic */ int c;
    private final Object d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0178v2(AbstractC0197z1 abstractC0197z1, Object obj, int i) {
        super(abstractC0197z1, i);
        this.c = 0;
        this.d = obj;
    }

    @Override // j$.util.stream.AbstractC0183w2
    void a() {
        switch (this.c) {
            case 0:
                ((AbstractC0197z1) this.a).d(this.d, this.b);
                return;
            default:
                this.a.i((Object[]) this.d, this.b);
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0183w2
    AbstractC0183w2 b(int i, int i2) {
        switch (this.c) {
            case 0:
                return new C0178v2(this, ((AbstractC0197z1) this.a).b(i), i2);
            default:
                return new C0178v2(this, this.a.b(i), i2);
        }
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ C0178v2(AbstractC0197z1 abstractC0197z1, Object obj, int i, B1 b1) {
        this(abstractC0197z1, obj, i);
        this.c = 0;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0178v2(A1 a1, Object[] objArr, int i, B1 b1) {
        super(a1, i);
        this.c = 1;
        this.c = 1;
        this.d = objArr;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0178v2(C0178v2 c0178v2, AbstractC0197z1 abstractC0197z1, int i) {
        super(c0178v2, abstractC0197z1, i);
        this.c = 0;
        this.d = c0178v2.d;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0178v2(C0178v2 c0178v2, A1 a1, int i) {
        super(c0178v2, a1, i);
        this.c = 1;
        this.d = (Object[]) c0178v2.d;
    }
}