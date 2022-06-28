package j$.util.stream;

import j$.util.function.BiConsumer;
import java.util.Set;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda21 implements BiConsumer {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda21 INSTANCE = new Collectors$$ExternalSyntheticLambda21();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda21() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((Set) obj).add(obj2);
    }

    @Override // j$.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return biConsumer.getClass();
    }
}
