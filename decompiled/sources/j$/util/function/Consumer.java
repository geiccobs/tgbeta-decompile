package j$.util.function;
/* loaded from: classes2.dex */
public interface Consumer<T> {
    void accept(T t);

    Consumer<T> andThen(Consumer<? super T> consumer);

    /* renamed from: j$.util.function.Consumer$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static /* synthetic */ void lambda$andThen$0(Consumer _this, Consumer after, Object t) {
            _this.accept(t);
            after.accept(t);
        }
    }
}
