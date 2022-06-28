package j$.util.stream;

import j$.util.StringJoiner;
import j$.util.function.Function;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda56 implements Function {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda56 INSTANCE = new Collectors$$ExternalSyntheticLambda56();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda56() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        return ((StringJoiner) obj).toString();
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
