package j$.util.function;
/* loaded from: classes2.dex */
public interface LongConsumer {
    void accept(long j);

    LongConsumer andThen(LongConsumer longConsumer);

    /* renamed from: j$.util.function.LongConsumer$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static /* synthetic */ void lambda$andThen$0(LongConsumer _this, LongConsumer after, long t) {
            _this.accept(t);
            after.accept(t);
        }
    }
}
