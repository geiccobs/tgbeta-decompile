package com.google.android.gms.maps;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.internal.Preconditions;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
public class MapFragment extends Fragment {
    private final zzae zza = new zzae(this);

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    public void getMapAsync(OnMapReadyCallback callback) {
        Preconditions.checkMainThread("getMapAsync must be called on the main thread.");
        Preconditions.checkNotNull(callback, "callback must not be null.");
        this.zza.zzb(callback);
    }

    @Override // android.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        ClassLoader classLoader = MapFragment.class.getClassLoader();
        if (savedInstanceState != null && classLoader != null) {
            savedInstanceState.setClassLoader(classLoader);
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override // android.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        zzae.zzc(this.zza, activity);
    }

    @Override // android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.zza.onCreate(savedInstanceState);
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View onCreateView = this.zza.onCreateView(inflater, container, savedInstanceState);
        onCreateView.setClickable(true);
        return onCreateView;
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        this.zza.onDestroy();
        super.onDestroy();
    }

    @Override // android.app.Fragment
    public void onDestroyView() {
        this.zza.onDestroyView();
        super.onDestroyView();
    }

    public final void onEnterAmbient(Bundle ambientDetails) {
        Preconditions.checkMainThread("onEnterAmbient must be called on the main thread.");
        zzae zzaeVar = this.zza;
        if (zzaeVar.getDelegate() != null) {
            zzaeVar.getDelegate().zza(ambientDetails);
        }
    }

    public final void onExitAmbient() {
        Preconditions.checkMainThread("onExitAmbient must be called on the main thread.");
        zzae zzaeVar = this.zza;
        if (zzaeVar.getDelegate() != null) {
            zzaeVar.getDelegate().zzb();
        }
    }

    @Override // android.app.Fragment
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        StrictMode.ThreadPolicy threadPolicy = StrictMode.getThreadPolicy();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(threadPolicy).permitAll().build());
        try {
            super.onInflate(activity, attrs, savedInstanceState);
            zzae.zzc(this.zza, activity);
            GoogleMapOptions createFromAttributes = GoogleMapOptions.createFromAttributes(activity, attrs);
            Bundle bundle = new Bundle();
            bundle.putParcelable("MapOptions", createFromAttributes);
            this.zza.onInflate(activity, bundle, savedInstanceState);
        } finally {
            StrictMode.setThreadPolicy(threadPolicy);
        }
    }

    @Override // android.app.Fragment, android.content.ComponentCallbacks
    public void onLowMemory() {
        this.zza.onLowMemory();
        super.onLowMemory();
    }

    @Override // android.app.Fragment
    public void onPause() {
        this.zza.onPause();
        super.onPause();
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        this.zza.onResume();
    }

    @Override // android.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        ClassLoader classLoader = MapFragment.class.getClassLoader();
        if (outState != null && classLoader != null) {
            outState.setClassLoader(classLoader);
        }
        super.onSaveInstanceState(outState);
        this.zza.onSaveInstanceState(outState);
    }

    @Override // android.app.Fragment
    public void onStart() {
        super.onStart();
        this.zza.onStart();
    }

    @Override // android.app.Fragment
    public void onStop() {
        this.zza.onStop();
        super.onStop();
    }

    @Override // android.app.Fragment
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    public static MapFragment newInstance(GoogleMapOptions options) {
        MapFragment mapFragment = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("MapOptions", options);
        mapFragment.setArguments(bundle);
        return mapFragment;
    }
}
