package j$.util.stream;

import j$.util.function.BiFunction;
import j$.util.function.BinaryOperator;
import j$.util.function.Function;
import j$.util.stream.Node;
import j$.util.stream.Nodes;
/* loaded from: classes2.dex */
public final /* synthetic */ class Nodes$CollectorTask$OfDouble$$ExternalSyntheticLambda0 implements BinaryOperator {
    public static final /* synthetic */ Nodes$CollectorTask$OfDouble$$ExternalSyntheticLambda0 INSTANCE = new Nodes$CollectorTask$OfDouble$$ExternalSyntheticLambda0();

    private /* synthetic */ Nodes$CollectorTask$OfDouble$$ExternalSyntheticLambda0() {
    }

    @Override // j$.util.function.BiFunction
    public /* synthetic */ BiFunction andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.BiFunction
    public final Object apply(Object obj, Object obj2) {
        return new Nodes.ConcNode.OfDouble((Node.OfDouble) obj, (Node.OfDouble) obj2);
    }
}