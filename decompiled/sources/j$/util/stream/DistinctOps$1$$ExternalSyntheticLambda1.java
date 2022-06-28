package j$.util.stream;

import j$.util.function.BiConsumer;
import java.util.LinkedHashSet;
/* loaded from: classes2.dex */
public final /* synthetic */ class DistinctOps$1$$ExternalSyntheticLambda1 implements BiConsumer {
    public static final /* synthetic */ DistinctOps$1$$ExternalSyntheticLambda1 INSTANCE = new DistinctOps$1$$ExternalSyntheticLambda1();

    private /* synthetic */ DistinctOps$1$$ExternalSyntheticLambda1() {
    }

    @Override // j$.util.function.BiConsumer
    public final void accept(Object obj, Object obj2) {
        ((LinkedHashSet) obj).addAll((LinkedHashSet) obj2);
    }

    @Override // j$.util.function.BiConsumer
    public /* synthetic */ BiConsumer andThen(BiConsumer biConsumer) {
        return biConsumer.getClass();
    }
}
