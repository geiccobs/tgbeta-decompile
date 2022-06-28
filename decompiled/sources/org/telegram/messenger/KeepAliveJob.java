package org.telegram.messenger;

import android.content.Intent;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.support.JobIntentService;
/* loaded from: classes4.dex */
public class KeepAliveJob extends JobIntentService {
    private static volatile CountDownLatch countDownLatch;
    private static volatile boolean startingJob;
    private static final Object sync = new Object();
    private static Runnable finishJobByTimeoutRunnable = KeepAliveJob$$ExternalSyntheticLambda0.INSTANCE;

    public static void startJob() {
        Utilities.globalQueue.postRunnable(KeepAliveJob$$ExternalSyntheticLambda1.INSTANCE);
    }

    public static /* synthetic */ void lambda$startJob$0() {
        if (startingJob || countDownLatch != null) {
            return;
        }
        try {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("starting keep-alive job");
            }
            synchronized (sync) {
                startingJob = true;
            }
            enqueueWork(ApplicationLoader.applicationContext, KeepAliveJob.class, 1000, new Intent());
        } catch (Exception e) {
        }
    }

    public static void finishJobInternal() {
        synchronized (sync) {
            if (countDownLatch != null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("finish keep-alive job");
                }
                countDownLatch.countDown();
            }
            if (startingJob) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("finish queued keep-alive job");
                }
                startingJob = false;
            }
        }
    }

    public static void finishJob() {
        Utilities.globalQueue.postRunnable(KeepAliveJob$$ExternalSyntheticLambda0.INSTANCE);
    }

    @Override // org.telegram.messenger.support.JobIntentService
    protected void onHandleWork(Intent intent) {
        synchronized (sync) {
            if (!startingJob) {
                return;
            }
            countDownLatch = new CountDownLatch(1);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("started keep-alive job");
            }
            Utilities.globalQueue.postRunnable(finishJobByTimeoutRunnable, DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
            try {
                countDownLatch.await();
            } catch (Throwable th) {
            }
            Utilities.globalQueue.cancelRunnable(finishJobByTimeoutRunnable);
            synchronized (sync) {
                countDownLatch = null;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("ended keep-alive job");
            }
        }
    }
}
