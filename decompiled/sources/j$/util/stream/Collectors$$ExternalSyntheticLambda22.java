package j$.util.stream;

import j$.util.StringJoiner;
import j$.util.function.BiConsumer;
/* loaded from: classes2.dex */
public final /* synthetic */ class Collectors$$ExternalSyntheticLambda22 implements BiConsumer {
    public static final /* synthetic */ Collectors$$ExternalSyntheticLambda22 INSTANCE = new Collectors$$ExternalSyntheticLambda22();

    private /* synthetic */ Collectors$$ExternalSyntheticLambda22() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((StringJoiner) obj).add((CharSequence) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return biConsumer.getClass();
    }
}
