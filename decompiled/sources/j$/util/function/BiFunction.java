package j$.util.function;
/* loaded from: classes2.dex */
public interface BiFunction<T, U, R> {
    <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> function);

    R apply(T t, U u);

    /* renamed from: j$.util.function.BiFunction$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
    }
}
