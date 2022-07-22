package j$.util.stream;
/* renamed from: j$.util.stream.v */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0174v implements j$.util.function.u {
    public static final /* synthetic */ C0174v a = new C0174v();

    private /* synthetic */ C0174v() {
    }

    @Override // j$.util.function.u
    public final void accept(Object obj, double d) {
        double[] dArr = (double[]) obj;
        dArr[2] = dArr[2] + 1.0d;
        AbstractC0119l.b(dArr, d);
        dArr[3] = dArr[3] + d;
    }
}
