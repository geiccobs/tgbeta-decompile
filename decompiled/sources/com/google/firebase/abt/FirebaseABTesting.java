package com.google.firebase.abt;

import android.content.Context;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import com.google.firebase.inject.Provider;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes3.dex */
public class FirebaseABTesting {
    static final String ABT_PREFERENCES = "com.google.firebase.abt";
    static final String ORIGIN_LAST_KNOWN_START_TIME_KEY_FORMAT = "%s_lastKnownExperimentStartTime";
    private final Provider<AnalyticsConnector> analyticsConnector;
    private Integer maxUserProperties = null;
    private final String originService;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface OriginService {
        public static final String INAPP_MESSAGING = "fiam";
        public static final String REMOTE_CONFIG = "frc";
    }

    public FirebaseABTesting(Context unusedAppContext, Provider<AnalyticsConnector> analyticsConnector, String originService) {
        this.analyticsConnector = analyticsConnector;
        this.originService = originService;
    }

    public void replaceAllExperiments(List<Map<String, String>> replacementExperiments) throws AbtException {
        throwAbtExceptionIfAnalyticsIsNull();
        if (replacementExperiments == null) {
            throw new IllegalArgumentException("The replacementExperiments list is null.");
        }
        replaceAllExperimentsWith(convertMapsToExperimentInfos(replacementExperiments));
    }

    public void removeAllExperiments() throws AbtException {
        throwAbtExceptionIfAnalyticsIsNull();
        removeExperiments(getAllExperimentsInAnalytics());
    }

    public List<AbtExperimentInfo> getAllExperiments() throws AbtException {
        throwAbtExceptionIfAnalyticsIsNull();
        List<AnalyticsConnector.ConditionalUserProperty> experimentsInAnalytics = getAllExperimentsInAnalytics();
        List<AbtExperimentInfo> experimentInfos = new ArrayList<>();
        for (AnalyticsConnector.ConditionalUserProperty experimentInAnalytics : experimentsInAnalytics) {
            experimentInfos.add(AbtExperimentInfo.fromConditionalUserProperty(experimentInAnalytics));
        }
        return experimentInfos;
    }

    public void reportActiveExperiment(AbtExperimentInfo activeExperiment) throws AbtException {
        throwAbtExceptionIfAnalyticsIsNull();
        AbtExperimentInfo.validateAbtExperimentInfo(activeExperiment);
        ArrayList<AbtExperimentInfo> activeExperimentList = new ArrayList<>();
        Map<String, String> activeExperimentMap = activeExperiment.toStringMap();
        activeExperimentMap.remove("triggerEvent");
        activeExperimentList.add(AbtExperimentInfo.fromMap(activeExperimentMap));
        addExperiments(activeExperimentList);
    }

    public void validateRunningExperiments(List<AbtExperimentInfo> runningExperiments) throws AbtException {
        throwAbtExceptionIfAnalyticsIsNull();
        Set<String> runningExperimentIds = new HashSet<>();
        for (AbtExperimentInfo runningExperiment : runningExperiments) {
            runningExperimentIds.add(runningExperiment.getExperimentId());
        }
        List<AnalyticsConnector.ConditionalUserProperty> experimentsToRemove = getExperimentsToRemove(getAllExperimentsInAnalytics(), runningExperimentIds);
        removeExperiments(experimentsToRemove);
    }

    private void replaceAllExperimentsWith(List<AbtExperimentInfo> replacementExperiments) throws AbtException {
        if (replacementExperiments.isEmpty()) {
            removeAllExperiments();
            return;
        }
        Set<String> replacementExperimentIds = new HashSet<>();
        for (AbtExperimentInfo replacementExperiment : replacementExperiments) {
            replacementExperimentIds.add(replacementExperiment.getExperimentId());
        }
        List<AnalyticsConnector.ConditionalUserProperty> experimentsInAnalytics = getAllExperimentsInAnalytics();
        Set<String> idsOfExperimentsInAnalytics = new HashSet<>();
        for (AnalyticsConnector.ConditionalUserProperty experimentInAnalytics : experimentsInAnalytics) {
            idsOfExperimentsInAnalytics.add(experimentInAnalytics.name);
        }
        List<AnalyticsConnector.ConditionalUserProperty> experimentsToRemove = getExperimentsToRemove(experimentsInAnalytics, replacementExperimentIds);
        removeExperiments(experimentsToRemove);
        List<AbtExperimentInfo> experimentsToAdd = getExperimentsToAdd(replacementExperiments, idsOfExperimentsInAnalytics);
        addExperiments(experimentsToAdd);
    }

    private ArrayList<AnalyticsConnector.ConditionalUserProperty> getExperimentsToRemove(List<AnalyticsConnector.ConditionalUserProperty> experimentsInAnalytics, Set<String> replacementExperimentIds) {
        ArrayList<AnalyticsConnector.ConditionalUserProperty> experimentsToRemove = new ArrayList<>();
        for (AnalyticsConnector.ConditionalUserProperty experimentInAnalytics : experimentsInAnalytics) {
            if (!replacementExperimentIds.contains(experimentInAnalytics.name)) {
                experimentsToRemove.add(experimentInAnalytics);
            }
        }
        return experimentsToRemove;
    }

    private ArrayList<AbtExperimentInfo> getExperimentsToAdd(List<AbtExperimentInfo> replacementExperiments, Set<String> idsOfExperimentsInAnalytics) {
        ArrayList<AbtExperimentInfo> experimentsToAdd = new ArrayList<>();
        for (AbtExperimentInfo replacementExperiment : replacementExperiments) {
            if (!idsOfExperimentsInAnalytics.contains(replacementExperiment.getExperimentId())) {
                experimentsToAdd.add(replacementExperiment);
            }
        }
        return experimentsToAdd;
    }

    private void addExperiments(List<AbtExperimentInfo> experimentsToAdd) {
        Deque<AnalyticsConnector.ConditionalUserProperty> dequeOfExperimentsInAnalytics = new ArrayDeque<>(getAllExperimentsInAnalytics());
        int fetchedMaxUserProperties = getMaxUserPropertiesInAnalytics();
        for (AbtExperimentInfo experimentToAdd : experimentsToAdd) {
            while (dequeOfExperimentsInAnalytics.size() >= fetchedMaxUserProperties) {
                removeExperimentFromAnalytics(dequeOfExperimentsInAnalytics.pollFirst().name);
            }
            AnalyticsConnector.ConditionalUserProperty experiment = experimentToAdd.toConditionalUserProperty(this.originService);
            addExperimentToAnalytics(experiment);
            dequeOfExperimentsInAnalytics.offer(experiment);
        }
    }

    private void removeExperiments(Collection<AnalyticsConnector.ConditionalUserProperty> experiments) {
        for (AnalyticsConnector.ConditionalUserProperty experiment : experiments) {
            removeExperimentFromAnalytics(experiment.name);
        }
    }

    private static List<AbtExperimentInfo> convertMapsToExperimentInfos(List<Map<String, String>> replacementExperimentsMaps) throws AbtException {
        List<AbtExperimentInfo> replacementExperimentInfos = new ArrayList<>();
        for (Map<String, String> replacementExperimentMap : replacementExperimentsMaps) {
            replacementExperimentInfos.add(AbtExperimentInfo.fromMap(replacementExperimentMap));
        }
        return replacementExperimentInfos;
    }

    private void addExperimentToAnalytics(AnalyticsConnector.ConditionalUserProperty experiment) {
        this.analyticsConnector.get().setConditionalUserProperty(experiment);
    }

    private void throwAbtExceptionIfAnalyticsIsNull() throws AbtException {
        if (this.analyticsConnector.get() == null) {
            throw new AbtException("The Analytics SDK is not available. Please check that the Analytics SDK is included in your app dependencies.");
        }
    }

    private void removeExperimentFromAnalytics(String experimentId) {
        this.analyticsConnector.get().clearConditionalUserProperty(experimentId, null, null);
    }

    private int getMaxUserPropertiesInAnalytics() {
        if (this.maxUserProperties == null) {
            this.maxUserProperties = Integer.valueOf(this.analyticsConnector.get().getMaxUserProperties(this.originService));
        }
        return this.maxUserProperties.intValue();
    }

    private List<AnalyticsConnector.ConditionalUserProperty> getAllExperimentsInAnalytics() {
        return this.analyticsConnector.get().getConditionalUserProperties(this.originService, "");
    }
}
