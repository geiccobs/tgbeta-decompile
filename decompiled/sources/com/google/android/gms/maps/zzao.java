package com.google.android.gms.maps;

import android.app.Activity;
import android.app.Fragment;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.dynamic.DeferredLifecycleHelper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.dynamic.OnDelegateCreatedListener;
import com.google.android.gms.maps.internal.zzca;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
final class zzao extends DeferredLifecycleHelper<zzan> {
    protected OnDelegateCreatedListener<zzan> zza;
    private final Fragment zzb;
    private Activity zzc;
    private final List<OnStreetViewPanoramaReadyCallback> zzd = new ArrayList();

    public zzao(Fragment fragment) {
        this.zzb = fragment;
    }

    public static /* synthetic */ void zzc(zzao zzaoVar, Activity activity) {
        zzaoVar.zzc = activity;
        zzaoVar.zza();
    }

    @Override // com.google.android.gms.dynamic.DeferredLifecycleHelper
    protected final void createDelegate(OnDelegateCreatedListener<zzan> onDelegateCreatedListener) {
        this.zza = onDelegateCreatedListener;
        zza();
    }

    public final void zza() {
        if (this.zzc != null && this.zza != null && getDelegate() == null) {
            try {
                MapsInitializer.initialize(this.zzc);
                this.zza.onDelegateCreated(new zzan(this.zzb, zzca.zza(this.zzc).zzj(ObjectWrapper.wrap(this.zzc))));
                for (OnStreetViewPanoramaReadyCallback onStreetViewPanoramaReadyCallback : this.zzd) {
                    getDelegate().getStreetViewPanoramaAsync(onStreetViewPanoramaReadyCallback);
                }
                this.zzd.clear();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            } catch (GooglePlayServicesNotAvailableException e2) {
            }
        }
    }

    public final void zzb(OnStreetViewPanoramaReadyCallback onStreetViewPanoramaReadyCallback) {
        if (getDelegate() != null) {
            getDelegate().getStreetViewPanoramaAsync(onStreetViewPanoramaReadyCallback);
        } else {
            this.zzd.add(onStreetViewPanoramaReadyCallback);
        }
    }
}
