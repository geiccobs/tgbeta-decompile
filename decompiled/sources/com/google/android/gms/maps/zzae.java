package com.google.android.gms.maps;

import android.app.Activity;
import android.app.Fragment;
import android.os.RemoteException;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.dynamic.DeferredLifecycleHelper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.dynamic.OnDelegateCreatedListener;
import com.google.android.gms.maps.internal.IMapFragmentDelegate;
import com.google.android.gms.maps.internal.zzca;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes3.dex */
final class zzae extends DeferredLifecycleHelper<zzad> {
    protected OnDelegateCreatedListener<zzad> zza;
    private final Fragment zzb;
    private Activity zzc;
    private final List<OnMapReadyCallback> zzd = new ArrayList();

    public zzae(Fragment fragment) {
        this.zzb = fragment;
    }

    public static /* synthetic */ void zzc(zzae zzaeVar, Activity activity) {
        zzaeVar.zzc = activity;
        zzaeVar.zza();
    }

    @Override // com.google.android.gms.dynamic.DeferredLifecycleHelper
    protected final void createDelegate(OnDelegateCreatedListener<zzad> onDelegateCreatedListener) {
        this.zza = onDelegateCreatedListener;
        zza();
    }

    public final void zza() {
        if (this.zzc != null && this.zza != null && getDelegate() == null) {
            try {
                MapsInitializer.initialize(this.zzc);
                IMapFragmentDelegate zzd = zzca.zza(this.zzc).zzd(ObjectWrapper.wrap(this.zzc));
                if (zzd == null) {
                    return;
                }
                this.zza.onDelegateCreated(new zzad(this.zzb, zzd));
                for (OnMapReadyCallback onMapReadyCallback : this.zzd) {
                    getDelegate().getMapAsync(onMapReadyCallback);
                }
                this.zzd.clear();
            } catch (RemoteException e) {
                throw new RuntimeRemoteException(e);
            } catch (GooglePlayServicesNotAvailableException e2) {
            }
        }
    }

    public final void zzb(OnMapReadyCallback onMapReadyCallback) {
        if (getDelegate() != null) {
            getDelegate().getMapAsync(onMapReadyCallback);
        } else {
            this.zzd.add(onMapReadyCallback);
        }
    }
}
