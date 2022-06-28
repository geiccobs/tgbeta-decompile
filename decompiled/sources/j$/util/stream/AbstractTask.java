package j$.util.stream;

import j$.util.Spliterator;
import j$.util.stream.AbstractTask;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
/* loaded from: classes2.dex */
abstract class AbstractTask<P_IN, P_OUT, R, K extends AbstractTask<P_IN, P_OUT, R, K>> extends CountedCompleter<R> {
    static final int LEAF_TARGET = ForkJoinPool.getCommonPoolParallelism() << 2;
    protected final PipelineHelper<P_OUT> helper;
    protected K leftChild;
    private R localResult;
    protected K rightChild;
    protected Spliterator<P_IN> spliterator;
    protected long targetSize;

    public abstract R doLeaf();

    public abstract K makeChild(Spliterator<P_IN> spliterator);

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.PipelineHelper != java.util.stream.PipelineHelper<P_OUT> */
    public AbstractTask(PipelineHelper<P_OUT> pipelineHelper, Spliterator<P_IN> spliterator) {
        super(null);
        this.helper = pipelineHelper;
        this.spliterator = spliterator;
        this.targetSize = 0L;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    public AbstractTask(K parent, Spliterator<P_IN> spliterator) {
        super(parent);
        this.spliterator = spliterator;
        this.helper = parent.helper;
        this.targetSize = parent.targetSize;
    }

    public static long suggestTargetSize(long sizeEstimate) {
        long est = sizeEstimate / LEAF_TARGET;
        if (est > 0) {
            return est;
        }
        return 1L;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    public final long getTargetSize(long sizeEstimate) {
        long s = this.targetSize;
        if (s != 0) {
            return s;
        }
        long suggestTargetSize = suggestTargetSize(sizeEstimate);
        this.targetSize = suggestTargetSize;
        return suggestTargetSize;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    @Override // java.util.concurrent.CountedCompleter, java.util.concurrent.ForkJoinTask
    public R getRawResult() {
        return this.localResult;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    @Override // java.util.concurrent.CountedCompleter, java.util.concurrent.ForkJoinTask
    protected void setRawResult(R result) {
        if (result != null) {
            throw new IllegalStateException();
        }
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    public R getLocalResult() {
        return this.localResult;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    public void setLocalResult(R localResult) {
        this.localResult = localResult;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    public boolean isLeaf() {
        return this.leftChild == null;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    public boolean isRoot() {
        return getParent() == null;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    public K getParent() {
        return (K) getCompleter();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.Spliterator != java.util.Spliterator<P_IN> */
    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    @Override // java.util.concurrent.CountedCompleter
    public void compute() {
        Spliterator<P_IN> trySplit;
        K taskToFork;
        Spliterator<P_IN> spliterator = this.spliterator;
        long sizeEstimate = spliterator.estimateSize();
        long sizeThreshold = getTargetSize(sizeEstimate);
        boolean forkRight = false;
        K task = this;
        while (sizeEstimate > sizeThreshold && (trySplit = spliterator.trySplit()) != null) {
            K leftChild = task.makeChild(trySplit);
            task.leftChild = leftChild;
            K rightChild = task.makeChild(spliterator);
            task.rightChild = rightChild;
            task.setPendingCount(1);
            if (forkRight) {
                forkRight = false;
                spliterator = trySplit;
                task = leftChild;
                taskToFork = rightChild;
            } else {
                forkRight = true;
                task = rightChild;
                taskToFork = leftChild;
            }
            taskToFork.fork();
            sizeEstimate = spliterator.estimateSize();
        }
        task.setLocalResult(task.doLeaf());
        task.tryComplete();
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    @Override // java.util.concurrent.CountedCompleter
    public void onCompletion(CountedCompleter<?> caller) {
        this.spliterator = null;
        this.rightChild = null;
        this.leftChild = null;
    }

    /* JADX WARN: Generic types in debug info not equals: j$.util.stream.AbstractTask != java.util.stream.AbstractTask<P_IN, P_OUT, R, K extends j$.util.stream.AbstractTask<P_IN, P_OUT, R, K>> */
    public boolean isLeftmostNode() {
        K node = this;
        while (node != null) {
            K parent = node.getParent();
            if (parent != null && parent.leftChild != node) {
                return false;
            }
            node = parent;
        }
        return true;
    }
}
