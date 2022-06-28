package j$.util.function;
/* loaded from: classes2.dex */
public interface IntConsumer {
    void accept(int i);

    IntConsumer andThen(IntConsumer intConsumer);

    /* renamed from: j$.util.function.IntConsumer$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static /* synthetic */ void lambda$andThen$0(IntConsumer _this, IntConsumer after, int t) {
            _this.accept(t);
            after.accept(t);
        }
    }
}
