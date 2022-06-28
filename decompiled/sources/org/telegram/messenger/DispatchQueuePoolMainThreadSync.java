package org.telegram.messenger;

import android.os.SystemClock;
import android.util.SparseIntArray;
import java.util.LinkedList;
/* loaded from: classes4.dex */
public class DispatchQueuePoolMainThreadSync {
    private boolean cleanupScheduled;
    private int createdCount;
    private int maxCount;
    private int totalTasksCount;
    private LinkedList<DispatchQueueMainThreadSync> queues = new LinkedList<>();
    private SparseIntArray busyQueuesMap = new SparseIntArray();
    private LinkedList<DispatchQueueMainThreadSync> busyQueues = new LinkedList<>();
    private Runnable cleanupRunnable = new Runnable() { // from class: org.telegram.messenger.DispatchQueuePoolMainThreadSync.1
        @Override // java.lang.Runnable
        public void run() {
            if (!DispatchQueuePoolMainThreadSync.this.queues.isEmpty()) {
                long currentTime = SystemClock.elapsedRealtime();
                int a = 0;
                int N = DispatchQueuePoolMainThreadSync.this.queues.size();
                while (a < N) {
                    DispatchQueueMainThreadSync queue = (DispatchQueueMainThreadSync) DispatchQueuePoolMainThreadSync.this.queues.get(a);
                    if (queue.getLastTaskTime() < currentTime - 30000) {
                        queue.recycle();
                        DispatchQueuePoolMainThreadSync.this.queues.remove(a);
                        DispatchQueuePoolMainThreadSync.access$110(DispatchQueuePoolMainThreadSync.this);
                        a--;
                        N--;
                    }
                    a++;
                }
            }
            if (DispatchQueuePoolMainThreadSync.this.queues.isEmpty() && DispatchQueuePoolMainThreadSync.this.busyQueues.isEmpty()) {
                DispatchQueuePoolMainThreadSync.this.cleanupScheduled = false;
                return;
            }
            AndroidUtilities.runOnUIThread(this, 30000L);
            DispatchQueuePoolMainThreadSync.this.cleanupScheduled = true;
        }
    };
    private int guid = Utilities.random.nextInt();

    static /* synthetic */ int access$110(DispatchQueuePoolMainThreadSync x0) {
        int i = x0.createdCount;
        x0.createdCount = i - 1;
        return i;
    }

    public DispatchQueuePoolMainThreadSync(int count) {
        this.maxCount = count;
    }

    public void execute(final Runnable runnable) {
        final DispatchQueueMainThreadSync queue;
        if (!this.busyQueues.isEmpty() && (this.totalTasksCount / 2 <= this.busyQueues.size() || (this.queues.isEmpty() && this.createdCount >= this.maxCount))) {
            queue = this.busyQueues.remove(0);
        } else if (this.queues.isEmpty()) {
            queue = new DispatchQueueMainThreadSync("DispatchQueuePool" + this.guid + "_" + Utilities.random.nextInt());
            queue.setPriority(10);
            this.createdCount = this.createdCount + 1;
        } else {
            queue = this.queues.remove(0);
        }
        if (!this.cleanupScheduled) {
            AndroidUtilities.runOnUIThread(this.cleanupRunnable, 30000L);
            this.cleanupScheduled = true;
        }
        this.totalTasksCount++;
        this.busyQueues.add(queue);
        int count = this.busyQueuesMap.get(queue.index, 0);
        this.busyQueuesMap.put(queue.index, count + 1);
        queue.postRunnable(new Runnable() { // from class: org.telegram.messenger.DispatchQueuePoolMainThreadSync$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DispatchQueuePoolMainThreadSync.this.m190xa66884f3(runnable, queue);
            }
        });
    }

    /* renamed from: lambda$execute$1$org-telegram-messenger-DispatchQueuePoolMainThreadSync */
    public /* synthetic */ void m190xa66884f3(Runnable runnable, final DispatchQueueMainThreadSync queue) {
        runnable.run();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DispatchQueuePoolMainThreadSync$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DispatchQueuePoolMainThreadSync.this.m189xa5323214(queue);
            }
        });
    }

    /* renamed from: lambda$execute$0$org-telegram-messenger-DispatchQueuePoolMainThreadSync */
    public /* synthetic */ void m189xa5323214(DispatchQueueMainThreadSync queue) {
        this.totalTasksCount--;
        int remainingTasksCount = this.busyQueuesMap.get(queue.index) - 1;
        if (remainingTasksCount == 0) {
            this.busyQueuesMap.delete(queue.index);
            this.busyQueues.remove(queue);
            this.queues.add(queue);
            return;
        }
        this.busyQueuesMap.put(queue.index, remainingTasksCount);
    }
}
