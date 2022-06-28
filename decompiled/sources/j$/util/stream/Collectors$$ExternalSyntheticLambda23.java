package j$.util.stream;

import j$.util.function.BiConsumer;
import j$.util.stream.Collectors;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda23 implements BiConsumer {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda23 INSTANCE = new Collectors$$ExternalSyntheticLambda23();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda23() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((Collectors.C1OptionalBox) obj).accept(obj2);
    }

    @Override // j$.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return biConsumer.getClass();
    }
}
