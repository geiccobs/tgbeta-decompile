package com.microsoft.appcenter.http;

import android.os.AsyncTask;
import com.microsoft.appcenter.http.DefaultHttpClientCallTask;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.HandlerUtils;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
/* loaded from: classes.dex */
public class DefaultHttpClient implements HttpClient, DefaultHttpClientCallTask.Tracker {
    private final boolean mCompressionEnabled;
    private final Set<DefaultHttpClientCallTask> mTasks = new HashSet();

    @Override // com.microsoft.appcenter.http.HttpClient
    public void reopen() {
    }

    public DefaultHttpClient(boolean z) {
        this.mCompressionEnabled = z;
    }

    @Override // com.microsoft.appcenter.http.HttpClient
    public ServiceCall callAsync(String str, String str2, Map<String, String> map, HttpClient.CallTemplate callTemplate, final ServiceCallback serviceCallback) {
        final DefaultHttpClientCallTask defaultHttpClientCallTask = new DefaultHttpClientCallTask(str, str2, map, callTemplate, serviceCallback, this, this.mCompressionEnabled);
        try {
            defaultHttpClientCallTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        } catch (RejectedExecutionException e) {
            HandlerUtils.runOnUiThread(new Runnable(this) { // from class: com.microsoft.appcenter.http.DefaultHttpClient.1
                @Override // java.lang.Runnable
                public void run() {
                    serviceCallback.onCallFailed(e);
                }
            });
        }
        return new ServiceCall(this) { // from class: com.microsoft.appcenter.http.DefaultHttpClient.2
            @Override // com.microsoft.appcenter.http.ServiceCall
            public void cancel() {
                defaultHttpClientCallTask.cancel(true);
            }
        };
    }

    @Override // com.microsoft.appcenter.http.DefaultHttpClientCallTask.Tracker
    public synchronized void onStart(DefaultHttpClientCallTask defaultHttpClientCallTask) {
        this.mTasks.add(defaultHttpClientCallTask);
    }

    @Override // com.microsoft.appcenter.http.DefaultHttpClientCallTask.Tracker
    public synchronized void onFinish(DefaultHttpClientCallTask defaultHttpClientCallTask) {
        this.mTasks.remove(defaultHttpClientCallTask);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() {
        if (this.mTasks.size() > 0) {
            AppCenterLog.debug("AppCenter", "Cancelling " + this.mTasks.size() + " network call(s).");
            for (DefaultHttpClientCallTask defaultHttpClientCallTask : this.mTasks) {
                defaultHttpClientCallTask.cancel(true);
            }
            this.mTasks.clear();
        }
    }
}
