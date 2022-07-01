package j$.util.stream;
/* renamed from: j$.util.stream.u0 */
/* loaded from: classes2.dex */
public final /* synthetic */ class C0165u0 implements j$.util.function.v {
    public static final /* synthetic */ C0165u0 a = new C0165u0();

    private /* synthetic */ C0165u0() {
    }

    @Override // j$.util.function.v
    public final void accept(Object obj, int i) {
        long[] jArr = (long[]) obj;
        jArr[0] = jArr[0] + 1;
        jArr[1] = jArr[1] + i;
    }
}
