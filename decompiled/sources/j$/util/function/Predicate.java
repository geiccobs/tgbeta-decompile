package j$.util.function;
/* loaded from: classes2.dex */
public interface Predicate<T> {

    /* renamed from: j$.util.function.Predicate$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static Predicate $default$negate(Predicate predicate) {
            return new C0045a(predicate);
        }
    }

    Predicate<T> and(Predicate<? super T> predicate);

    Predicate<T> negate();

    Predicate<T> or(Predicate<? super T> predicate);

    boolean test(T t);
}
