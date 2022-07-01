package com.microsoft.appcenter.http;

import android.os.Handler;
import android.os.Looper;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
/* loaded from: classes.dex */
public class HttpClientRetryer extends HttpClientDecorator {
    static final long[] RETRY_INTERVALS;
    private final Handler mHandler;
    private final Random mRandom;

    static {
        TimeUnit timeUnit = TimeUnit.MINUTES;
        RETRY_INTERVALS = new long[]{TimeUnit.SECONDS.toMillis(10L), timeUnit.toMillis(5L), timeUnit.toMillis(20L)};
    }

    public HttpClientRetryer(HttpClient httpClient) {
        this(httpClient, new Handler(Looper.getMainLooper()));
    }

    HttpClientRetryer(HttpClient httpClient, Handler handler) {
        super(httpClient);
        this.mRandom = new Random();
        this.mHandler = handler;
    }

    @Override // com.microsoft.appcenter.http.HttpClient
    public ServiceCall callAsync(String str, String str2, Map<String, String> map, HttpClient.CallTemplate callTemplate, ServiceCallback serviceCallback) {
        RetryableCall retryableCall = new RetryableCall(this.mDecoratedApi, str, str2, map, callTemplate, serviceCallback);
        retryableCall.run();
        return retryableCall;
    }

    /* loaded from: classes.dex */
    private class RetryableCall extends HttpClientCallDecorator {
        private int mRetryCount;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        RetryableCall(HttpClient httpClient, String str, String str2, Map<String, String> map, HttpClient.CallTemplate callTemplate, ServiceCallback serviceCallback) {
            super(httpClient, str, str2, map, callTemplate, serviceCallback);
            HttpClientRetryer.this = r8;
        }

        @Override // com.microsoft.appcenter.http.HttpClientCallDecorator, com.microsoft.appcenter.http.ServiceCall
        public synchronized void cancel() {
            HttpClientRetryer.this.mHandler.removeCallbacks(this);
            super.cancel();
        }

        @Override // com.microsoft.appcenter.http.HttpClientCallDecorator, com.microsoft.appcenter.http.ServiceCallback
        public void onCallFailed(Exception exc) {
            long j;
            String str;
            int i = this.mRetryCount;
            long[] jArr = HttpClientRetryer.RETRY_INTERVALS;
            if (i < jArr.length && HttpUtils.isRecoverableError(exc)) {
                long parseLong = (!(exc instanceof HttpException) || (str = ((HttpException) exc).getHttpResponse().getHeaders().get("x-ms-retry-after-ms")) == null) ? 0L : Long.parseLong(str);
                if (parseLong == 0) {
                    int i2 = this.mRetryCount;
                    this.mRetryCount = i2 + 1;
                    parseLong = (jArr[i2] / 2) + HttpClientRetryer.this.mRandom.nextInt((int) j);
                }
                String str2 = "Try #" + this.mRetryCount + " failed and will be retried in " + parseLong + " ms";
                if (exc instanceof UnknownHostException) {
                    str2 = str2 + " (UnknownHostException)";
                }
                AppCenterLog.warn("AppCenter", str2, exc);
                HttpClientRetryer.this.mHandler.postDelayed(this, parseLong);
                return;
            }
            this.mServiceCallback.onCallFailed(exc);
        }
    }
}
