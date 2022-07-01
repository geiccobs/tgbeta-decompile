package j$.util.stream;
/* renamed from: j$.util.stream.w */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0174w implements j$.util.function.u {
    public static final /* synthetic */ C0174w a = new C0174w();

    private /* synthetic */ C0174w() {
    }

    @Override // j$.util.function.u
    public final void accept(Object obj, double d) {
        double[] dArr = (double[]) obj;
        AbstractC0114l.b(dArr, d);
        dArr[2] = dArr[2] + d;
    }
}
