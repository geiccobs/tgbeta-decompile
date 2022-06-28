package com.google.android.gms.maps;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.DeferredLifecycleHelper;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public class StreetViewPanoramaView extends FrameLayout {
    private final zzas zza;

    public StreetViewPanoramaView(Context context) {
        super((Context) Preconditions.checkNotNull(context, "context must not be null"));
        this.zza = new zzas(this, context, null);
    }

    public void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback callback) {
        Preconditions.checkNotNull(callback, "callback must not be null");
        Preconditions.checkMainThread("getStreetViewPanoramaAsync() must be called on the main thread");
        this.zza.zzb(callback);
    }

    public final void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(threadPolicy).permitAll().build());
        try {
            this.zza.onCreate(savedInstanceState);
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

    public final void onLowMemory() {
        this.zza.onLowMemory();
    }

    public final void onPause() {
        this.zza.onPause();
    }

    public void onResume() {
        this.zza.onResume();
    }

    public final void onSaveInstanceState(Bundle outState) {
        this.zza.onSaveInstanceState(outState);
    }

    public void onStart() {
        this.zza.onStart();
    }

    public void onStop() {
        this.zza.onStop();
    }

    public StreetViewPanoramaView(Context context, AttributeSet attrs) {
        super((Context) Preconditions.checkNotNull(context, "context must not be null"), attrs);
        this.zza = new zzas(this, context, null);
    }

    public StreetViewPanoramaView(Context context, AttributeSet attrs, int defStyle) {
        super((Context) Preconditions.checkNotNull(context, "context must not be null"), attrs, defStyle);
        this.zza = new zzas(this, context, null);
    }

    public StreetViewPanoramaView(Context context, StreetViewPanoramaOptions options) {
        super((Context) Preconditions.checkNotNull(context, "context must not be null"));
        this.zza = new zzas(this, context, options);
    }
}
