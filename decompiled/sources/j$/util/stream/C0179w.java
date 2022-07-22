package j$.util.stream;
/* renamed from: j$.util.stream.w */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0179w implements j$.util.function.u {
    public static final /* synthetic */ C0179w a = new C0179w();

    private /* synthetic */ C0179w() {
    }

    @Override // j$.util.function.u
    public final void accept(Object obj, double d) {
        double[] dArr = (double[]) obj;
        AbstractC0119l.b(dArr, d);
        dArr[2] = dArr[2] + d;
    }
}
