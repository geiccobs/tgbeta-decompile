package j$.util.stream;

import j$.util.function.LongFunction;
/* loaded from: classes2.dex */
public final /* synthetic */ class Nodes$CollectorTask$OfInt$$ExternalSyntheticLambda1 implements LongFunction {
    public static final /* synthetic */ Nodes$CollectorTask$OfInt$$ExternalSyntheticLambda1 INSTANCE = new Nodes$CollectorTask$OfInt$$ExternalSyntheticLambda1();

    private /* synthetic */ Nodes$CollectorTask$OfInt$$ExternalSyntheticLambda1() {
    }

    @Override // j$.util.function.LongFunction
    public final Object apply(long j) {
        return Nodes.intBuilder(j);
    }
}
