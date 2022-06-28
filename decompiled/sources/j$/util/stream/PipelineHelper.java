package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.IntFunction;
import j$.util.stream.Node;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public abstract class PipelineHelper<P_OUT> {
    public abstract <P_IN> void copyInto(Sink<P_IN> sink, Spliterator<P_IN> spliterator);

    public abstract <P_IN> void copyIntoWithCancel(Sink<P_IN> sink, Spliterator<P_IN> spliterator);

    public abstract <P_IN> Node<P_OUT> evaluate(Spliterator<P_IN> spliterator, boolean z, IntFunction<P_OUT[]> intFunction);

    public abstract <P_IN> long exactOutputSizeIfKnown(Spliterator<P_IN> spliterator);

    public abstract StreamShape getSourceShape();

    public abstract int getStreamAndOpFlags();

    public abstract Node.Builder<P_OUT> makeNodeBuilder(long j, IntFunction<P_OUT[]> intFunction);

    public abstract <P_IN, S extends Sink<P_OUT>> S wrapAndCopyInto(S s, Spliterator<P_IN> spliterator);

    public abstract <P_IN> Sink<P_IN> wrapSink(Sink<P_OUT> sink);

    public abstract <P_IN> Spliterator<P_OUT> wrapSpliterator(Spliterator<P_IN> spliterator);
}
