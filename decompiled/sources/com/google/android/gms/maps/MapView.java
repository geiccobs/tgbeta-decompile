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
public class MapView extends FrameLayout {
    private final zzah zza;

    public MapView(Context context) {
        super(context);
        this.zza = new zzah(this, context, null);
        setClickable(true);
    }

    public void getMapAsync(OnMapReadyCallback callback) {
        Preconditions.checkMainThread("getMapAsync() must be called on the main thread");
        Preconditions.checkNotNull(callback, "callback must not be null.");
        this.zza.zzb(callback);
    }

    public void onCreate(Bundle savedInstanceState) {
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

    public void onEnterAmbient(Bundle ambientDetails) {
        Preconditions.checkMainThread("onEnterAmbient() must be called on the main thread");
        zzah zzahVar = this.zza;
        if (zzahVar.getDelegate() != null) {
            zzahVar.getDelegate().zza(ambientDetails);
        }
    }

    public void onExitAmbient() {
        Preconditions.checkMainThread("onExitAmbient() must be called on the main thread");
        zzah zzahVar = this.zza;
        if (zzahVar.getDelegate() != null) {
            zzahVar.getDelegate().zzb();
        }
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

    public void onSaveInstanceState(Bundle outState) {
        this.zza.onSaveInstanceState(outState);
    }

    public void onStart() {
        this.zza.onStart();
    }

    public void onStop() {
        this.zza.onStop();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.zza = new zzah(this, context, GoogleMapOptions.createFromAttributes(context, attrs));
        setClickable(true);
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.zza = new zzah(this, context, GoogleMapOptions.createFromAttributes(context, attrs));
        setClickable(true);
    }

    public MapView(Context context, GoogleMapOptions options) {
        super(context);
        this.zza = new zzah(this, context, options);
        setClickable(true);
    }
}
