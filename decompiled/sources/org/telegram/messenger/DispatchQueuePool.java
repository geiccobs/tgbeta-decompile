package org.telegram.messenger;

import android.os.SystemClock;
import android.util.SparseIntArray;
import java.util.LinkedList;
/* loaded from: classes4.dex */
public class DispatchQueuePool {
    private boolean cleanupScheduled;
    private int createdCount;
    private int maxCount;
    private int totalTasksCount;
    private LinkedList<DispatchQueue> queues = new LinkedList<>();
    private SparseIntArray busyQueuesMap = new SparseIntArray();
    private LinkedList<DispatchQueue> busyQueues = new LinkedList<>();
    private Runnable cleanupRunnable = new Runnable() { // from class: org.telegram.messenger.DispatchQueuePool.1
        @Override // java.lang.Runnable
        public void run() {
            if (!DispatchQueuePool.this.queues.isEmpty()) {
                long currentTime = SystemClock.elapsedRealtime();
                int a = 0;
                int N = DispatchQueuePool.this.queues.size();
                while (a < N) {
                    DispatchQueue queue = (DispatchQueue) DispatchQueuePool.this.queues.get(a);
                    if (queue.getLastTaskTime() < currentTime - 30000) {
                        queue.recycle();
                        DispatchQueuePool.this.queues.remove(a);
                        DispatchQueuePool.access$110(DispatchQueuePool.this);
                        a--;
                        N--;
                    }
                    a++;
                }
            }
            if (DispatchQueuePool.this.queues.isEmpty() && DispatchQueuePool.this.busyQueues.isEmpty()) {
                DispatchQueuePool.this.cleanupScheduled = false;
                return;
            }
            AndroidUtilities.runOnUIThread(this, 30000L);
            DispatchQueuePool.this.cleanupScheduled = true;
        }
    };
    private int guid = Utilities.random.nextInt();

    static /* synthetic */ int access$110(DispatchQueuePool x0) {
        int i = x0.createdCount;
        x0.createdCount = i - 1;
        return i;
    }

    public DispatchQueuePool(int count) {
        this.maxCount = count;
    }

    public void execute(final Runnable runnable) {
        final DispatchQueue queue;
        if (!this.busyQueues.isEmpty() && (this.totalTasksCount / 2 <= this.busyQueues.size() || (this.queues.isEmpty() && this.createdCount >= this.maxCount))) {
            queue = this.busyQueues.remove(0);
        } else if (this.queues.isEmpty()) {
            queue = new DispatchQueue("DispatchQueuePool" + this.guid + "_" + Utilities.random.nextInt());
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
        queue.postRunnable(new Runnable() { // from class: org.telegram.messenger.DispatchQueuePool$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DispatchQueuePool.this.m188lambda$execute$1$orgtelegrammessengerDispatchQueuePool(runnable, queue);
            }
        });
    }

    /* renamed from: lambda$execute$1$org-telegram-messenger-DispatchQueuePool */
    public /* synthetic */ void m188lambda$execute$1$orgtelegrammessengerDispatchQueuePool(Runnable runnable, final DispatchQueue queue) {
        runnable.run();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DispatchQueuePool$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DispatchQueuePool.this.m187lambda$execute$0$orgtelegrammessengerDispatchQueuePool(queue);
            }
        });
    }

    /* renamed from: lambda$execute$0$org-telegram-messenger-DispatchQueuePool */
    public /* synthetic */ void m187lambda$execute$0$orgtelegrammessengerDispatchQueuePool(DispatchQueue queue) {
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
