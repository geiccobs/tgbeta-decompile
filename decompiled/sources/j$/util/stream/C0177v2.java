package j$.util.stream;
/* renamed from: j$.util.stream.v2 */
/* loaded from: classes2.dex */
class C0177v2 extends AbstractC0182w2 {
    public final /* synthetic */ int c;
    private final Object d;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0177v2(AbstractC0196z1 abstractC0196z1, Object obj, int i) {
        super(abstractC0196z1, i);
        this.c = 0;
        this.d = obj;
    }

    @Override // j$.util.stream.AbstractC0182w2
    void a() {
        switch (this.c) {
            case 0:
                ((AbstractC0196z1) this.a).d(this.d, this.b);
                return;
            default:
                this.a.i((Object[]) this.d, this.b);
                return;
        }
    }

    @Override // j$.util.stream.AbstractC0182w2
    AbstractC0182w2 b(int i, int i2) {
        switch (this.c) {
            case 0:
                return new C0177v2(this, ((AbstractC0196z1) this.a).b(i), i2);
            default:
                return new C0177v2(this, this.a.b(i), i2);
        }
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ C0177v2(AbstractC0196z1 abstractC0196z1, Object obj, int i, B1 b1) {
        this(abstractC0196z1, obj, i);
        this.c = 0;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0177v2(A1 a1, Object[] objArr, int i, B1 b1) {
        super(a1, i);
        this.c = 1;
        this.c = 1;
        this.d = objArr;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0177v2(C0177v2 c0177v2, AbstractC0196z1 abstractC0196z1, int i) {
        super(c0177v2, abstractC0196z1, i);
        this.c = 0;
        this.d = c0177v2.d;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public C0177v2(C0177v2 c0177v2, A1 a1, int i) {
        super(c0177v2, a1, i);
        this.c = 1;
        this.d = (Object[]) c0177v2.d;
    }
}
