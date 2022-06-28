package org.telegram.messenger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import java.util.ArrayList;
/* loaded from: classes4.dex */
public class DispatchQueueMainThreadSync extends Thread {
    private static int indexPointer = 0;
    private volatile Handler handler;
    public final int index;
    private boolean isRecycled;
    private boolean isRunning;
    private long lastTaskTime;
    private ArrayList<PostponedTask> postponedTasks;

    public DispatchQueueMainThreadSync(String threadName) {
        this(threadName, true);
    }

    public DispatchQueueMainThreadSync(String threadName, boolean start) {
        this.handler = null;
        int i = indexPointer;
        indexPointer = i + 1;
        this.index = i;
        this.postponedTasks = new ArrayList<>();
        setName(threadName);
        if (start) {
            start();
        }
    }

    public void sendMessage(Message msg, int delay) {
        checkThread();
        if (this.isRecycled) {
            return;
        }
        if (!this.isRunning) {
            this.postponedTasks.add(new PostponedTask(msg, delay));
        } else if (delay <= 0) {
            this.handler.sendMessage(msg);
        } else {
            this.handler.sendMessageDelayed(msg, delay);
        }
    }

    private void checkThread() {
        if (BuildVars.DEBUG_PRIVATE_VERSION && Thread.currentThread() != ApplicationLoader.applicationHandler.getLooper().getThread()) {
            throw new IllegalStateException("Disaptch thread");
        }
    }

    public void cancelRunnable(Runnable runnable) {
        checkThread();
        if (this.isRunning) {
            this.handler.removeCallbacks(runnable);
            return;
        }
        int i = 0;
        while (i < this.postponedTasks.size()) {
            if (this.postponedTasks.get(i).runnable == runnable) {
                this.postponedTasks.remove(i);
                i--;
            }
            i++;
        }
    }

    public void cancelRunnables(Runnable[] runnables) {
        checkThread();
        for (Runnable runnable : runnables) {
            cancelRunnable(runnable);
        }
    }

    public boolean postRunnable(Runnable runnable) {
        checkThread();
        this.lastTaskTime = SystemClock.elapsedRealtime();
        return postRunnable(runnable, 0L);
    }

    public boolean postRunnable(Runnable runnable, long delay) {
        checkThread();
        if (this.isRecycled) {
            return false;
        }
        if (!this.isRunning) {
            this.postponedTasks.add(new PostponedTask(runnable, delay));
            return true;
        } else if (delay <= 0) {
            return this.handler.post(runnable);
        } else {
            return this.handler.postDelayed(runnable, delay);
        }
    }

    public void cleanupQueue() {
        checkThread();
        this.postponedTasks.clear();
        this.handler.removeCallbacksAndMessages(null);
    }

    public void handleMessage(Message inputMessage) {
    }

    public long getLastTaskTime() {
        return this.lastTaskTime;
    }

    public void recycle() {
        checkThread();
        postRunnable(new Runnable() { // from class: org.telegram.messenger.DispatchQueueMainThreadSync$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DispatchQueueMainThreadSync.this.m185x30a1b77a();
            }
        });
        this.isRecycled = true;
    }

    /* renamed from: lambda$recycle$0$org-telegram-messenger-DispatchQueueMainThreadSync */
    public /* synthetic */ void m185x30a1b77a() {
        this.handler.getLooper().quit();
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        Looper.prepare();
        this.handler = new Handler(Looper.myLooper(), new Handler.Callback() { // from class: org.telegram.messenger.DispatchQueueMainThreadSync$$ExternalSyntheticLambda0
            @Override // android.os.Handler.Callback
            public final boolean handleMessage(Message message) {
                return DispatchQueueMainThreadSync.this.m186lambda$run$1$orgtelegrammessengerDispatchQueueMainThreadSync(message);
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.DispatchQueueMainThreadSync.1
            @Override // java.lang.Runnable
            public void run() {
                DispatchQueueMainThreadSync.this.isRunning = true;
                for (int i = 0; i < DispatchQueueMainThreadSync.this.postponedTasks.size(); i++) {
                    ((PostponedTask) DispatchQueueMainThreadSync.this.postponedTasks.get(i)).run();
                }
                DispatchQueueMainThreadSync.this.postponedTasks.clear();
            }
        });
        Looper.loop();
    }

    /* renamed from: lambda$run$1$org-telegram-messenger-DispatchQueueMainThreadSync */
    public /* synthetic */ boolean m186lambda$run$1$orgtelegrammessengerDispatchQueueMainThreadSync(Message msg) {
        handleMessage(msg);
        return true;
    }

    public boolean isReady() {
        return this.isRunning;
    }

    public Handler getHandler() {
        return this.handler;
    }

    /* loaded from: classes4.dex */
    public class PostponedTask {
        long delay;
        Message message;
        Runnable runnable;

        public PostponedTask(Message msg, int delay) {
            DispatchQueueMainThreadSync.this = r3;
            this.message = msg;
            this.delay = delay;
        }

        public PostponedTask(Runnable runnable, long delay) {
            DispatchQueueMainThreadSync.this = r1;
            this.runnable = runnable;
            this.delay = delay;
        }

        public void run() {
            Runnable runnable = this.runnable;
            if (runnable != null) {
                DispatchQueueMainThreadSync.this.postRunnable(runnable, this.delay);
            } else {
                DispatchQueueMainThreadSync.this.sendMessage(this.message, (int) this.delay);
            }
        }
    }
}
