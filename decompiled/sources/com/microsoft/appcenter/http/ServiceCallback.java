package com.microsoft.appcenter.http;
/* loaded from: classes.dex */
public interface ServiceCallback {
    void onCallFailed(Exception exc);

    void onCallSucceeded(HttpResponse httpResponse);
}
