package j$.util.stream;
/* loaded from: classes2.dex */
abstract class C1 implements A1 {
    protected final A1 a;
    protected final A1 b;
    private final long c;

    public C1(A1 a1, A1 a12) {
        this.a = a1;
        this.b = a12;
        this.c = a1.count() + a12.count();
    }

    @Override // j$.util.stream.A1
    public A1 b(int i) {
        if (i == 0) {
            return this.a;
        }
        if (i != 1) {
            throw new IndexOutOfBoundsException();
        }
        return this.b;
    }

    @Override // j$.util.stream.A1
    public long count() {
        return this.c;
    }

    @Override // j$.util.stream.A1
    public int p() {
        return 2;
    }
}
