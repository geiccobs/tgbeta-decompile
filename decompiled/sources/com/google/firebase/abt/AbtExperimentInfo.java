package com.google.firebase.abt;

import android.text.TextUtils;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes3.dex */
public class AbtExperimentInfo {
    static final String EXPERIMENT_ID_KEY = "experimentId";
    static final String TRIGGER_EVENT_KEY = "triggerEvent";
    static final String VARIANT_ID_KEY = "variantId";
    private final String experimentId;
    private final Date experimentStartTime;
    private final long timeToLiveInMillis;
    private final String triggerEventName;
    private final long triggerTimeoutInMillis;
    private final String variantId;
    static final String EXPERIMENT_START_TIME_KEY = "experimentStartTime";
    static final String TIME_TO_LIVE_KEY = "timeToLiveMillis";
    static final String TRIGGER_TIMEOUT_KEY = "triggerTimeoutMillis";
    private static final String[] ALL_REQUIRED_KEYS = {"experimentId", EXPERIMENT_START_TIME_KEY, TIME_TO_LIVE_KEY, TRIGGER_TIMEOUT_KEY, "variantId"};
    static final DateFormat protoTimestampStringParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

    public AbtExperimentInfo(String experimentId, String variantId, String triggerEventName, Date experimentStartTime, long triggerTimeoutInMillis, long timeToLiveInMillis) {
        this.experimentId = experimentId;
        this.variantId = variantId;
        this.triggerEventName = triggerEventName;
        this.experimentStartTime = experimentStartTime;
        this.triggerTimeoutInMillis = triggerTimeoutInMillis;
        this.timeToLiveInMillis = timeToLiveInMillis;
    }

    public static AbtExperimentInfo fromMap(Map<String, String> experimentInfoMap) throws AbtException {
        String str;
        validateExperimentInfoMap(experimentInfoMap);
        try {
            Date experimentStartTime = protoTimestampStringParser.parse(experimentInfoMap.get(EXPERIMENT_START_TIME_KEY));
            long triggerTimeoutInMillis = Long.parseLong(experimentInfoMap.get(TRIGGER_TIMEOUT_KEY));
            long timeToLiveInMillis = Long.parseLong(experimentInfoMap.get(TIME_TO_LIVE_KEY));
            String str2 = experimentInfoMap.get("experimentId");
            String str3 = experimentInfoMap.get("variantId");
            if (experimentInfoMap.containsKey(TRIGGER_EVENT_KEY)) {
                str = experimentInfoMap.get(TRIGGER_EVENT_KEY);
            } else {
                str = "";
            }
            return new AbtExperimentInfo(str2, str3, str, experimentStartTime, triggerTimeoutInMillis, timeToLiveInMillis);
        } catch (NumberFormatException e) {
            throw new AbtException("Could not process experiment: one of the durations could not be converted into a long.", e);
        } catch (ParseException e2) {
            throw new AbtException("Could not process experiment: parsing experiment start time failed.", e2);
        }
    }

    public String getExperimentId() {
        return this.experimentId;
    }

    String getVariantId() {
        return this.variantId;
    }

    String getTriggerEventName() {
        return this.triggerEventName;
    }

    long getStartTimeInMillisSinceEpoch() {
        return this.experimentStartTime.getTime();
    }

    long getTriggerTimeoutInMillis() {
        return this.triggerTimeoutInMillis;
    }

    long getTimeToLiveInMillis() {
        return this.timeToLiveInMillis;
    }

    private static void validateExperimentInfoMap(Map<String, String> experimentInfoMap) throws AbtException {
        String[] strArr;
        List<String> missingKeys = new ArrayList<>();
        for (String key : ALL_REQUIRED_KEYS) {
            if (!experimentInfoMap.containsKey(key)) {
                missingKeys.add(key);
            }
        }
        if (!missingKeys.isEmpty()) {
            throw new AbtException(String.format("The following keys are missing from the experiment info map: %s", missingKeys));
        }
    }

    public static void validateAbtExperimentInfo(AbtExperimentInfo experimentInfo) throws AbtException {
        validateExperimentInfoMap(experimentInfo.toStringMap());
    }

    public Map<String, String> toStringMap() {
        Map<String, String> experimentInfoMap = new HashMap<>();
        experimentInfoMap.put("experimentId", this.experimentId);
        experimentInfoMap.put("variantId", this.variantId);
        experimentInfoMap.put(TRIGGER_EVENT_KEY, this.triggerEventName);
        experimentInfoMap.put(EXPERIMENT_START_TIME_KEY, protoTimestampStringParser.format(this.experimentStartTime));
        experimentInfoMap.put(TRIGGER_TIMEOUT_KEY, Long.toString(this.triggerTimeoutInMillis));
        experimentInfoMap.put(TIME_TO_LIVE_KEY, Long.toString(this.timeToLiveInMillis));
        return experimentInfoMap;
    }

    public AnalyticsConnector.ConditionalUserProperty toConditionalUserProperty(String originService) {
        AnalyticsConnector.ConditionalUserProperty conditionalUserProperty = new AnalyticsConnector.ConditionalUserProperty();
        conditionalUserProperty.origin = originService;
        conditionalUserProperty.creationTimestamp = getStartTimeInMillisSinceEpoch();
        conditionalUserProperty.name = this.experimentId;
        conditionalUserProperty.value = this.variantId;
        conditionalUserProperty.triggerEventName = TextUtils.isEmpty(this.triggerEventName) ? null : this.triggerEventName;
        conditionalUserProperty.triggerTimeout = this.triggerTimeoutInMillis;
        conditionalUserProperty.timeToLive = this.timeToLiveInMillis;
        return conditionalUserProperty;
    }

    public static AbtExperimentInfo fromConditionalUserProperty(AnalyticsConnector.ConditionalUserProperty conditionalUserProperty) {
        String triggerEventName = "";
        if (conditionalUserProperty.triggerEventName != null) {
            triggerEventName = conditionalUserProperty.triggerEventName;
        }
        return new AbtExperimentInfo(conditionalUserProperty.name, String.valueOf(conditionalUserProperty.value), triggerEventName, new Date(conditionalUserProperty.creationTimestamp), conditionalUserProperty.triggerTimeout, conditionalUserProperty.timeToLive);
    }
}
