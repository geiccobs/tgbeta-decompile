package com.microsoft.appcenter.http;

import com.microsoft.appcenter.http.HttpClient;
import java.util.Map;
/* loaded from: classes.dex */
abstract class HttpClientCallDecorator implements Runnable, ServiceCall, ServiceCallback {
    private final HttpClient.CallTemplate mCallTemplate;
    private final HttpClient mDecoratedApi;
    private final Map<String, String> mHeaders;
    private final String mMethod;
    ServiceCall mServiceCall;
    final ServiceCallback mServiceCallback;
    private final String mUrl;

    public HttpClientCallDecorator(HttpClient httpClient, String str, String str2, Map<String, String> map, HttpClient.CallTemplate callTemplate, ServiceCallback serviceCallback) {
        this.mDecoratedApi = httpClient;
        this.mUrl = str;
        this.mMethod = str2;
        this.mHeaders = map;
        this.mCallTemplate = callTemplate;
        this.mServiceCallback = serviceCallback;
    }

    @Override // com.microsoft.appcenter.http.ServiceCall
    public synchronized void cancel() {
        this.mServiceCall.cancel();
    }

    @Override // java.lang.Runnable
    public synchronized void run() {
        this.mServiceCall = this.mDecoratedApi.callAsync(this.mUrl, this.mMethod, this.mHeaders, this.mCallTemplate, this);
    }

    @Override // com.microsoft.appcenter.http.ServiceCallback
    public void onCallSucceeded(HttpResponse httpResponse) {
        this.mServiceCallback.onCallSucceeded(httpResponse);
    }

    @Override // com.microsoft.appcenter.http.ServiceCallback
    public void onCallFailed(Exception exc) {
        this.mServiceCallback.onCallFailed(exc);
    }
}
