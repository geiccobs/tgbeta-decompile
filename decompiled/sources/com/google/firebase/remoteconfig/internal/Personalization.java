package com.google.firebase.remoteconfig.internal;

import android.os.Bundle;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import com.google.firebase.inject.Provider;
import j$.util.DesugarCollections;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
/* loaded from: classes3.dex */
public class Personalization {
    public static final String ANALYTICS_ORIGIN_PERSONALIZATION = "fp";
    public static final String ARM_INDEX = "armIndex";
    public static final String CHOICE_ID = "choiceId";
    public static final String EXTERNAL_ARM_INDEX_PARAM = "arm_index";
    public static final String EXTERNAL_ARM_VALUE_PARAM = "arm_value";
    public static final String EXTERNAL_EVENT = "personalization_assignment";
    public static final String EXTERNAL_GROUP_PARAM = "group";
    public static final String EXTERNAL_PERSONALIZATION_ID_PARAM = "personalization_id";
    public static final String EXTERNAL_RC_PARAMETER_PARAM = "arm_key";
    public static final String GROUP = "group";
    public static final String INTERNAL_CHOICE_ID_PARAM = "_fpid";
    public static final String INTERNAL_EVENT = "_fpc";
    public static final String PERSONALIZATION_ID = "personalizationId";
    private final Provider<AnalyticsConnector> analyticsConnector;
    private final Map<String, String> loggedChoiceIds = DesugarCollections.synchronizedMap(new HashMap());

    public Personalization(Provider<AnalyticsConnector> analyticsConnector) {
        this.analyticsConnector = analyticsConnector;
    }

    public void logArmActive(String rcParameter, ConfigContainer configContainer) {
        JSONObject metadata;
        AnalyticsConnector connector = this.analyticsConnector.get();
        if (connector == null) {
            return;
        }
        JSONObject ids = configContainer.getPersonalizationMetadata();
        if (ids.length() < 1) {
            return;
        }
        JSONObject values = configContainer.getConfigs();
        if (values.length() < 1 || (metadata = ids.optJSONObject(rcParameter)) == null) {
            return;
        }
        String choiceId = metadata.optString(CHOICE_ID);
        if (choiceId.isEmpty()) {
            return;
        }
        synchronized (this.loggedChoiceIds) {
            if (choiceId.equals(this.loggedChoiceIds.get(rcParameter))) {
                return;
            }
            this.loggedChoiceIds.put(rcParameter, choiceId);
            Bundle logParams = new Bundle();
            logParams.putString(EXTERNAL_RC_PARAMETER_PARAM, rcParameter);
            logParams.putString(EXTERNAL_ARM_VALUE_PARAM, values.optString(rcParameter));
            logParams.putString(EXTERNAL_PERSONALIZATION_ID_PARAM, metadata.optString(PERSONALIZATION_ID));
            logParams.putInt(EXTERNAL_ARM_INDEX_PARAM, metadata.optInt(ARM_INDEX, -1));
            logParams.putString("group", metadata.optString("group"));
            connector.logEvent(ANALYTICS_ORIGIN_PERSONALIZATION, EXTERNAL_EVENT, logParams);
            Bundle internalLogParams = new Bundle();
            internalLogParams.putString(INTERNAL_CHOICE_ID_PARAM, choiceId);
            connector.logEvent(ANALYTICS_ORIGIN_PERSONALIZATION, INTERNAL_EVENT, internalLogParams);
        }
    }
}
