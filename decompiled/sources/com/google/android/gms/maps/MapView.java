package com.google.android.gms.maps;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.FrameLayout;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.DeferredLifecycleHelper;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public class MapView extends FrameLayout {
    private final zzah zza;

    public MapView(@RecentlyNonNull Context context) {
        super(context);
        this.zza = new zzah(this, context, null);
        setClickable(true);
    }

    public void getMapAsync(@RecentlyNonNull OnMapReadyCallback onMapReadyCallback) {
        Preconditions.checkMainThread("getMapAsync() must be called on the main thread");
        Preconditions.checkNotNull(onMapReadyCallback, "callback must not be null.");
        this.zza.zzb(onMapReadyCallback);
    }

    public void onCreate(Bundle bundle) {
        StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(threadPolicy).permitAll().build());
        try {
            this.zza.onCreate(bundle);
            if (this.zza.getDelegate() == null) {
                DeferredLifecycleHelper.showGooglePlayUnavailableMessage(this);
            }
        } finally {
            StrictMode.setThreadPolicy(threadPolicy);
        }
    }

    public void onDestroy() {
        this.zza.onDestroy();
    }

    public void onLowMemory() {
        this.zza.onLowMemory();
    }

    public void onPause() {
        this.zza.onPause();
    }

    public void onResume() {
        this.zza.onResume();
    }
}
