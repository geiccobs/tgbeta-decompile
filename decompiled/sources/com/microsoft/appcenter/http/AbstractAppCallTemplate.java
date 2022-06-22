package com.microsoft.appcenter.http;

import com.microsoft.appcenter.http.HttpClient;
import com.microsoft.appcenter.utils.AppCenterLog;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class AbstractAppCallTemplate implements HttpClient.CallTemplate {
    @Override // com.microsoft.appcenter.http.HttpClient.CallTemplate
    public void onBeforeCalling(URL url, Map<String, String> map) {
        if (AppCenterLog.getLogLevel() <= 2) {
            AppCenterLog.verbose("AppCenter", "Calling " + url + "...");
            HashMap hashMap = new HashMap(map);
            String str = (String) hashMap.get("App-Secret");
            if (str != null) {
                hashMap.put("App-Secret", HttpUtils.hideSecret(str));
            }
            AppCenterLog.verbose("AppCenter", "Headers: " + hashMap);
        }
    }
}
