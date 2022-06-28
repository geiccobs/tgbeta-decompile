package j$.util.stream;

import j$.util.Spliterator;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public interface TerminalOp<E_IN, R> {
    <P_IN> R evaluateParallel(PipelineHelper<E_IN> pipelineHelper, Spliterator<P_IN> spliterator);

    <P_IN> R evaluateSequential(PipelineHelper<E_IN> pipelineHelper, Spliterator<P_IN> spliterator);

    int getOpFlags();

    StreamShape inputShape();

    /* renamed from: j$.util.stream.TerminalOp$-CC */
    /* loaded from: classes2.dex */
    public final /* synthetic */ class CC {
        public static int $default$getOpFlags(TerminalOp _this) {
            return 0;
        }

        /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
        /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<E_IN> */
        public static Object $default$evaluateParallel(TerminalOp _this, PipelineHelper pipelineHelper, Spliterator spliterator) {
            if (Tripwire.ENABLED) {
                Tripwire.trip(_this.getClass(), "{0} triggering TerminalOp.evaluateParallel serial default");
            }
            return _this.evaluateSequential(pipelineHelper, spliterator);
        }
    }
}
