package com.microsoft.appcenter.http;

import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.utils.AppCenterLog;
import com.microsoft.appcenter.utils.NetworkStateHelper;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class HttpClientNetworkStateHandler extends HttpClientDecorator implements NetworkStateHelper.Listener {
    private final Set<Call> mCalls = new HashSet();
    private final NetworkStateHelper mNetworkStateHelper;

    public HttpClientNetworkStateHandler(HttpClient httpClient, NetworkStateHelper networkStateHelper) {
        super(httpClient);
        this.mNetworkStateHelper = networkStateHelper;
        networkStateHelper.addListener(this);
    }

    @Override // com.microsoft.appcenter.http.HttpClient
    public synchronized ServiceCall callAsync(String str, String str2, Map<String, String> map, HttpClient.CallTemplate callTemplate, ServiceCallback serviceCallback) {
        Call call;
        call = new Call(this.mDecoratedApi, str, str2, map, callTemplate, serviceCallback);
        if (this.mNetworkStateHelper.isNetworkConnected()) {
            call.run();
        } else {
            this.mCalls.add(call);
            AppCenterLog.debug("AppCenter", "Call triggered with no network connectivity, waiting network to become available...");
        }
        return call;
    }

    @Override // com.microsoft.appcenter.http.HttpClientDecorator, java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() throws IOException {
        this.mNetworkStateHelper.removeListener(this);
        this.mCalls.clear();
        super.close();
    }

    @Override // com.microsoft.appcenter.http.HttpClientDecorator, com.microsoft.appcenter.http.HttpClient
    public void reopen() {
        this.mNetworkStateHelper.addListener(this);
        super.reopen();
    }

    @Override // com.microsoft.appcenter.utils.NetworkStateHelper.Listener
    public synchronized void onNetworkStateUpdated(boolean z) {
        if (z) {
            if (this.mCalls.size() > 0) {
                AppCenterLog.debug("AppCenter", "Network is available. " + this.mCalls.size() + " pending call(s) to submit now.");
                for (Call call : this.mCalls) {
                    call.run();
                }
                this.mCalls.clear();
            }
        }
    }

    public synchronized void cancelCall(Call call) {
        ServiceCall serviceCall = call.mServiceCall;
        if (serviceCall != null) {
            serviceCall.cancel();
        }
        this.mCalls.remove(call);
    }

    /* loaded from: classes.dex */
    public class Call extends HttpClientCallDecorator {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        Call(HttpClient httpClient, String str, String str2, Map<String, String> map, HttpClient.CallTemplate callTemplate, ServiceCallback serviceCallback) {
            super(httpClient, str, str2, map, callTemplate, serviceCallback);
            HttpClientNetworkStateHandler.this = r8;
        }

        @Override // com.microsoft.appcenter.http.HttpClientCallDecorator, com.microsoft.appcenter.http.ServiceCall
        public void cancel() {
            HttpClientNetworkStateHandler.this.cancelCall(this);
        }
    }
}
