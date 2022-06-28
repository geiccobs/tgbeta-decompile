package com.microsoft.appcenter.http;

import android.os.Handler;
import android.os.Looper;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
/* loaded from: classes3.dex */
public class HttpClientRetryer extends HttpClientDecorator {
    static final long[] RETRY_INTERVALS = {TimeUnit.SECONDS.toMillis(10), TimeUnit.MINUTES.toMillis(5), TimeUnit.MINUTES.toMillis(20)};
    private final Handler mHandler;
    private final Random mRandom;

    public HttpClientRetryer(HttpClient decoratedApi) {
        this(decoratedApi, new Handler(Looper.getMainLooper()));
    }

    HttpClientRetryer(HttpClient decoratedApi, Handler handler) {
        super(decoratedApi);
        this.mRandom = new Random();
        this.mHandler = handler;
    }

    @Override // com.microsoft.appcenter.http.HttpClient
    public ServiceCall callAsync(String url, String method, Map<String, String> headers, HttpClient.CallTemplate callTemplate, ServiceCallback serviceCallback) {
        RetryableCall retryableCall = new RetryableCall(this.mDecoratedApi, url, method, headers, callTemplate, serviceCallback);
        retryableCall.run();
        return retryableCall;
    }

    /* loaded from: classes3.dex */
    private class RetryableCall extends HttpClientCallDecorator {
        private int mRetryCount;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        RetryableCall(HttpClient decoratedApi, String url, String method, Map<String, String> headers, HttpClient.CallTemplate callTemplate, ServiceCallback serviceCallback) {
            super(decoratedApi, url, method, headers, callTemplate, serviceCallback);
            HttpClientRetryer.this = r8;
        }

        @Override // com.microsoft.appcenter.http.HttpClientCallDecorator, com.microsoft.appcenter.http.ServiceCall
        public synchronized void cancel() {
            HttpClientRetryer.this.mHandler.removeCallbacks(this);
            super.cancel();
        }

        @Override // com.microsoft.appcenter.http.HttpClientCallDecorator, com.microsoft.appcenter.http.ServiceCallback
        public void onCallFailed(Exception e) {
            if (this.mRetryCount < HttpClientRetryer.RETRY_INTERVALS.length && HttpUtils.isRecoverableError(e)) {
                long delay = 0;
                if (e instanceof HttpException) {
                    HttpException httpException = (HttpException) e;
                    String retryAfterMs = httpException.getHttpResponse().getHeaders().get("x-ms-retry-after-ms");
                    if (retryAfterMs != null) {
                        delay = Long.parseLong(retryAfterMs);
                    }
                }
                if (delay == 0) {
                    long[] jArr = HttpClientRetryer.RETRY_INTERVALS;
                    int i = this.mRetryCount;
                    this.mRetryCount = i + 1;
                    long delay2 = jArr[i] / 2;
                    delay = HttpClientRetryer.this.mRandom.nextInt((int) delay2) + delay2;
                }
                String message = "Try #" + this.mRetryCount + " failed and will be retried in " + delay + " ms";
                if (e instanceof UnknownHostException) {
                    message = message + " (UnknownHostException)";
                }
                AppCenterLog.warn("AppCenter", message, e);
                HttpClientRetryer.this.mHandler.postDelayed(this, delay);
                return;
            }
            this.mServiceCallback.onCallFailed(e);
        }
    }
}
