package j$.util.function;
/* loaded from: classes2.dex */
public interface Function<T, R> {
    <V> Function<T, V> andThen(Function<? super R, ? extends V> function);

    R apply(T t);

    <V> Function<V, R> compose(Function<? super V, ? extends T> function);

    /* renamed from: j$.util.function.Function$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static <T> Function<T, T> identity() {
            return Function$$ExternalSyntheticLambda2.INSTANCE;
        }

        public static /* synthetic */ Object lambda$identity$2(Object t) {
            return t;
        }
    }
}
