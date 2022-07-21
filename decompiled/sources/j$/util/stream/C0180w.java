package j$.util.stream;
/* renamed from: j$.util.stream.w */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0180w implements j$.util.function.u {
    public static final /* synthetic */ C0180w a = new C0180w();

    private /* synthetic */ C0180w() {
    }

    @Override // j$.util.function.u
    public final void accept(Object obj, double d) {
        double[] dArr = (double[]) obj;
        AbstractC0120l.b(dArr, d);
        dArr[2] = dArr[2] + d;
    }
}
