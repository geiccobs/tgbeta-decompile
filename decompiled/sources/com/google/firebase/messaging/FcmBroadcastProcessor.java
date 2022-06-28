package com.google.firebase.messaging;

import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.util.PlatformVersion;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.messaging.Constants;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes3.dex */
public class FcmBroadcastProcessor {
    private static WithinAppServiceConnection fcmServiceConn;
    private static final Object lock = new Object();
    private final Context context;
    private final Executor executor;

    public FcmBroadcastProcessor(Context context) {
        this.context = context;
        this.executor = FcmBroadcastProcessor$$Lambda$0.$instance;
    }

    public FcmBroadcastProcessor(Context context, ExecutorService executorService) {
        this.context = context;
        this.executor = executorService;
    }

    private static Task<Integer> bindToMessagingService(Context context, Intent intent) {
        if (Log.isLoggable(Constants.TAG, 3)) {
            Log.d(Constants.TAG, "Binding to service");
        }
        return getServiceConnection(context, "com.google.firebase.MESSAGING_EVENT").sendIntent(intent).continueWith(FcmBroadcastProcessor$$Lambda$3.$instance, FcmBroadcastProcessor$$Lambda$4.$instance);
    }

    private static WithinAppServiceConnection getServiceConnection(Context context, String str) {
        WithinAppServiceConnection withinAppServiceConnection;
        synchronized (lock) {
            if (fcmServiceConn == null) {
                fcmServiceConn = new WithinAppServiceConnection(context, "com.google.firebase.MESSAGING_EVENT");
            }
            withinAppServiceConnection = fcmServiceConn;
        }
        return withinAppServiceConnection;
    }

    public static final /* synthetic */ Integer lambda$bindToMessagingService$3$FcmBroadcastProcessor(Task task) throws Exception {
        return -1;
    }

    public static final /* synthetic */ Integer lambda$startMessagingService$1$FcmBroadcastProcessor(Task task) throws Exception {
        return 403;
    }

    public static final /* synthetic */ Task lambda$startMessagingService$2$FcmBroadcastProcessor(Context context, Intent intent, Task task) throws Exception {
        if (!PlatformVersion.isAtLeastO() || ((Integer) task.getResult()).intValue() != 402) {
            return task;
        }
        return bindToMessagingService(context, intent).continueWith(FcmBroadcastProcessor$$Lambda$5.$instance, FcmBroadcastProcessor$$Lambda$6.$instance);
    }

    public static void reset() {
        synchronized (lock) {
            fcmServiceConn = null;
        }
    }

    public Task<Integer> process(Intent intent) {
        String stringExtra = intent.getStringExtra("gcm.rawData64");
        if (stringExtra != null) {
            intent.putExtra(Constants.MessagePayloadKeys.RAW_DATA, Base64.decode(stringExtra, 0));
            intent.removeExtra("gcm.rawData64");
        }
        return startMessagingService(this.context, intent);
    }

    public Task<Integer> startMessagingService(Context context, Intent intent) {
        boolean z = false;
        if (PlatformVersion.isAtLeastO() && context.getApplicationInfo().targetSdkVersion >= 26) {
            z = true;
        }
        int flags = intent.getFlags() & 268435456;
        if (!z || flags != 0) {
            return Tasks.call(this.executor, new Callable(context, intent) { // from class: com.google.firebase.messaging.FcmBroadcastProcessor$$Lambda$1
                private final Context arg$1;
                private final Intent arg$2;

                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    this.arg$1 = context;
                    this.arg$2 = intent;
                }

                @Override // java.util.concurrent.Callable
                public Object call() {
                    Integer valueOf;
                    valueOf = Integer.valueOf(ServiceStarter.getInstance().startMessagingService(this.arg$1, this.arg$2));
                    return valueOf;
                }
            }).continueWithTask(this.executor, new Continuation(context, intent) { // from class: com.google.firebase.messaging.FcmBroadcastProcessor$$Lambda$2
                private final Context arg$1;
                private final Intent arg$2;

                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    this.arg$1 = context;
                    this.arg$2 = intent;
                }

                @Override // com.google.android.gms.tasks.Continuation
                public Object then(Task task) {
                    return FcmBroadcastProcessor.lambda$startMessagingService$2$FcmBroadcastProcessor(this.arg$1, this.arg$2, task);
                }
            });
        }
        return bindToMessagingService(context, intent);
    }
}
