package com.google.firebase.remoteconfig.internal;

import android.os.Bundle;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import com.google.firebase.inject.Provider;
import j$.util.DesugarCollections;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class Personalization {
    private final Provider<AnalyticsConnector> analyticsConnector;
    private final Map<String, String> loggedChoiceIds = DesugarCollections.synchronizedMap(new HashMap());

    public Personalization(Provider<AnalyticsConnector> provider) {
        this.analyticsConnector = provider;
    }

    public void logArmActive(String str, ConfigContainer configContainer) {
        JSONObject optJSONObject;
        AnalyticsConnector analyticsConnector = this.analyticsConnector.get();
        if (analyticsConnector == null) {
            return;
        }
        JSONObject personalizationMetadata = configContainer.getPersonalizationMetadata();
        if (personalizationMetadata.length() < 1) {
            return;
        }
        JSONObject configs = configContainer.getConfigs();
        if (configs.length() < 1 || (optJSONObject = personalizationMetadata.optJSONObject(str)) == null) {
            return;
        }
        String optString = optJSONObject.optString("choiceId");
        if (optString.isEmpty()) {
            return;
        }
        synchronized (this.loggedChoiceIds) {
            if (optString.equals(this.loggedChoiceIds.get(str))) {
                return;
            }
            this.loggedChoiceIds.put(str, optString);
            Bundle bundle = new Bundle();
            bundle.putString("arm_key", str);
            bundle.putString("arm_value", configs.optString(str));
            bundle.putString("personalization_id", optJSONObject.optString("personalizationId"));
            bundle.putInt("arm_index", optJSONObject.optInt("armIndex", -1));
            bundle.putString("group", optJSONObject.optString("group"));
            analyticsConnector.logEvent("fp", "personalization_assignment", bundle);
            Bundle bundle2 = new Bundle();
            bundle2.putString("_fpid", optString);
            analyticsConnector.logEvent("fp", "_fpc", bundle2);
        }
    }
}
