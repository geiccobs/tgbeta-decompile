package j$.util.function;
/* loaded from: classes2.dex */
public interface BiConsumer<T, U> {
    void accept(T t, U u);

    BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> biConsumer);

    /* renamed from: j$.util.function.BiConsumer$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static /* synthetic */ void lambda$andThen$0(BiConsumer _this, BiConsumer after, Object l, Object r) {
            _this.accept(l, r);
            after.accept(l, r);
        }
    }
}
