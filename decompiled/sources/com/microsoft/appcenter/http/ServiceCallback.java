package com.microsoft.appcenter.http;
/* loaded from: classes3.dex */
public interface ServiceCallback {
    void onCallFailed(Exception exc);

    void onCallSucceeded(HttpResponse httpResponse);
}
