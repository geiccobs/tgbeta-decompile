package com.google.android.gms.wearable;

import android.net.Uri;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
@Deprecated
/* loaded from: classes3.dex */
public interface CapabilityApi {
    public static final String ACTION_CAPABILITY_CHANGED = "com.google.android.gms.wearable.CAPABILITY_CHANGED";
    public static final int FILTER_ALL = 0;
    public static final int FILTER_LITERAL = 0;
    public static final int FILTER_PREFIX = 1;
    public static final int FILTER_REACHABLE = 1;

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public interface AddLocalCapabilityResult extends Result {
    }

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CapabilityFilterType {
    }

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public interface CapabilityListener {
        void onCapabilityChanged(CapabilityInfo capabilityInfo);
    }

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public interface GetAllCapabilitiesResult extends Result {
        Map<String, CapabilityInfo> getAllCapabilities();
    }

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public interface GetCapabilityResult extends Result {
        CapabilityInfo getCapability();
    }

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface NodeFilterType {
    }

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public interface RemoveLocalCapabilityResult extends Result {
    }

    PendingResult<Status> addCapabilityListener(GoogleApiClient googleApiClient, CapabilityListener capabilityListener, String str);

    PendingResult<Status> addListener(GoogleApiClient googleApiClient, CapabilityListener capabilityListener, Uri uri, int i);

    PendingResult<AddLocalCapabilityResult> addLocalCapability(GoogleApiClient googleApiClient, String str);

    PendingResult<GetAllCapabilitiesResult> getAllCapabilities(GoogleApiClient googleApiClient, int i);

    PendingResult<GetCapabilityResult> getCapability(GoogleApiClient googleApiClient, String str, int i);

    PendingResult<Status> removeCapabilityListener(GoogleApiClient googleApiClient, CapabilityListener capabilityListener, String str);

    PendingResult<Status> removeListener(GoogleApiClient googleApiClient, CapabilityListener capabilityListener);

    PendingResult<RemoveLocalCapabilityResult> removeLocalCapability(GoogleApiClient googleApiClient, String str);
}
