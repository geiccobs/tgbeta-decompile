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
/* loaded from: classes3.dex */
public class DefaultHttpClient implements HttpClient, DefaultHttpClientCallTask.Tracker {
    static final String CHARSET_NAME = "UTF-8";
    static final String CONTENT_ENCODING_KEY = "Content-Encoding";
    static final String CONTENT_ENCODING_VALUE = "gzip";
    public static final String CONTENT_TYPE_KEY = "Content-Type";
    static final String CONTENT_TYPE_VALUE = "application/json";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    static final String X_MS_RETRY_AFTER_MS_HEADER = "x-ms-retry-after-ms";
    private final boolean mCompressionEnabled;
    private final Set<DefaultHttpClientCallTask> mTasks;

    public DefaultHttpClient() {
        this(true);
    }

    public DefaultHttpClient(boolean compressionEnabled) {
        this.mTasks = new HashSet();
        this.mCompressionEnabled = compressionEnabled;
    }

    Set<DefaultHttpClientCallTask> getTasks() {
        return this.mTasks;
    }

    @Override // com.microsoft.appcenter.http.HttpClient
    public ServiceCall callAsync(String url, String method, Map<String, String> headers, HttpClient.CallTemplate callTemplate, final ServiceCallback serviceCallback) {
        final DefaultHttpClientCallTask task = new DefaultHttpClientCallTask(url, method, headers, callTemplate, serviceCallback, this, this.mCompressionEnabled);
        try {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        } catch (RejectedExecutionException e) {
            HandlerUtils.runOnUiThread(new Runnable() { // from class: com.microsoft.appcenter.http.DefaultHttpClient.1
                @Override // java.lang.Runnable
                public void run() {
                    serviceCallback.onCallFailed(e);
                }
            });
        }
        return new ServiceCall() { // from class: com.microsoft.appcenter.http.DefaultHttpClient.2
            @Override // com.microsoft.appcenter.http.ServiceCall
            public void cancel() {
                task.cancel(true);
            }
        };
    }

    @Override // com.microsoft.appcenter.http.DefaultHttpClientCallTask.Tracker
    public synchronized void onStart(DefaultHttpClientCallTask task) {
        this.mTasks.add(task);
    }

    @Override // com.microsoft.appcenter.http.DefaultHttpClientCallTask.Tracker
    public synchronized void onFinish(DefaultHttpClientCallTask task) {
        this.mTasks.remove(task);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() {
        if (this.mTasks.size() > 0) {
            AppCenterLog.debug("AppCenter", "Cancelling " + this.mTasks.size() + " network call(s).");
            for (DefaultHttpClientCallTask task : this.mTasks) {
                task.cancel(true);
            }
            this.mTasks.clear();
        }
    }

    @Override // com.microsoft.appcenter.http.HttpClient
    public void reopen() {
    }

    boolean isCompressionEnabled() {
        return this.mCompressionEnabled;
    }
}
