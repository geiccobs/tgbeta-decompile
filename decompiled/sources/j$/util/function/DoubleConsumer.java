package j$.util.function;
/* loaded from: classes2.dex */
public interface DoubleConsumer {
    void accept(double d);

    DoubleConsumer andThen(DoubleConsumer doubleConsumer);

    /* renamed from: j$.util.function.DoubleConsumer$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static /* synthetic */ void lambda$andThen$0(DoubleConsumer _this, DoubleConsumer after, double t) {
            _this.accept(t);
            after.accept(t);
        }
    }
}
