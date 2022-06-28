package com.microsoft.appcenter.http;

import com.microsoft.appcenter.Constants;
import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes3.dex */
public abstract class AbstractAppCallTemplate implements HttpClient.CallTemplate {
    @Override // com.microsoft.appcenter.http.HttpClient.CallTemplate
    public void onBeforeCalling(URL url, Map<String, String> headers) {
        if (AppCenterLog.getLogLevel() <= 2) {
            AppCenterLog.verbose("AppCenter", "Calling " + url + "...");
            Map<String, String> logHeaders = new HashMap<>(headers);
            String appSecret = logHeaders.get(Constants.APP_SECRET);
            if (appSecret != null) {
                logHeaders.put(Constants.APP_SECRET, HttpUtils.hideSecret(appSecret));
            }
            AppCenterLog.verbose("AppCenter", "Headers: " + logHeaders);
        }
    }
}
