package com.google.android.gms.wearable;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.Wearable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public abstract class CapabilityClient extends GoogleApi<Wearable.WearableOptions> {
    public static final String ACTION_CAPABILITY_CHANGED = "com.google.android.gms.wearable.CAPABILITY_CHANGED";
    public static final int FILTER_ALL = 0;
    public static final int FILTER_LITERAL = 0;
    public static final int FILTER_PREFIX = 1;
    public static final int FILTER_REACHABLE = 1;

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface CapabilityFilterType {
    }

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface NodeFilterType {
    }

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    /* loaded from: classes3.dex */
    public interface OnCapabilityChangedListener extends CapabilityApi.CapabilityListener {
        @Override // com.google.android.gms.wearable.CapabilityApi.CapabilityListener
        void onCapabilityChanged(CapabilityInfo capabilityInfo);
    }

    public CapabilityClient(Activity activity, GoogleApi.Settings settings) {
        super(activity, Wearable.API, Wearable.WearableOptions.zza, settings);
    }

    public abstract Task<Void> addListener(OnCapabilityChangedListener onCapabilityChangedListener, Uri uri, int i);

    public abstract Task<Void> addListener(OnCapabilityChangedListener onCapabilityChangedListener, String str);

    public abstract Task<Void> addLocalCapability(String str);

    public abstract Task<Map<String, CapabilityInfo>> getAllCapabilities(int i);

    public abstract Task<CapabilityInfo> getCapability(String str, int i);

    public abstract Task<Boolean> removeListener(OnCapabilityChangedListener onCapabilityChangedListener);

    public abstract Task<Boolean> removeListener(OnCapabilityChangedListener onCapabilityChangedListener, String str);

    public abstract Task<Void> removeLocalCapability(String str);

    public CapabilityClient(Context context, GoogleApi.Settings settings) {
        super(context, Wearable.API, Wearable.WearableOptions.zza, settings);
    }
}
