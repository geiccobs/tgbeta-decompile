package j$.util.stream;
/* renamed from: j$.util.stream.v */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0175v implements j$.util.function.u {
    public static final /* synthetic */ C0175v a = new C0175v();

    private /* synthetic */ C0175v() {
    }

    @Override // j$.util.function.u
    public final void accept(Object obj, double d) {
        double[] dArr = (double[]) obj;
        dArr[2] = dArr[2] + 1.0d;
        AbstractC0120l.b(dArr, d);
        dArr[3] = dArr[3] + d;
    }
}
