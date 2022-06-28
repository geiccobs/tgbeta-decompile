package com.google.firebase.abt.component;

import android.content.Context;
import com.google.firebase.abt.FirebaseABTesting;
import com.google.firebase.analytics.connector.AnalyticsConnector;
import com.google.firebase.inject.Provider;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes3.dex */
public class AbtComponent {
    private final Map<String, FirebaseABTesting> abtOriginInstances = new HashMap();
    private final Provider<AnalyticsConnector> analyticsConnector;
    private final Context appContext;

    public AbtComponent(Context appContext, Provider<AnalyticsConnector> analyticsConnector) {
        this.appContext = appContext;
        this.analyticsConnector = analyticsConnector;
    }

    public synchronized FirebaseABTesting get(String originService) {
        if (!this.abtOriginInstances.containsKey(originService)) {
            this.abtOriginInstances.put(originService, createAbtInstance(originService));
        }
        return this.abtOriginInstances.get(originService);
    }

    protected FirebaseABTesting createAbtInstance(String originService) {
        return new FirebaseABTesting(this.appContext, this.analyticsConnector, originService);
    }
}
