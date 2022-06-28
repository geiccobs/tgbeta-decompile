package j$.util.stream;

import j$.util.function.BiConsumer;
import java.util.Collection;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda19 implements BiConsumer {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda19 INSTANCE = new Collectors$$ExternalSyntheticLambda19();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda19() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((Collection) obj).add(obj2);
    }

    @Override // j$.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return biConsumer.getClass();
    }
}
