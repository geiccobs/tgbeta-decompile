package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.RemoteException;
import com.google.android.gms.common.internal.Preconditions;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public final class IndoorBuilding {
    private final com.google.android.gms.internal.maps.zzr zza;
    private final zze zzb;

    public IndoorBuilding(com.google.android.gms.internal.maps.zzr zzrVar) {
        zze zzeVar = zze.zza;
        this.zza = (com.google.android.gms.internal.maps.zzr) Preconditions.checkNotNull(zzrVar, "delegate");
        this.zzb = (zze) Preconditions.checkNotNull(zzeVar, "shim");
    }

    public boolean equals(@Nullable Object other) {
        if (!(other instanceof IndoorBuilding)) {
            return false;
        }
        try {
            return this.zza.zzh(((IndoorBuilding) other).zza);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public int getActiveLevelIndex() {
        try {
            return this.zza.zzd();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public int getDefaultLevelIndex() {
        try {
            return this.zza.zze();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public List<IndoorLevel> getLevels() {
        try {
            List<IBinder> zzf = this.zza.zzf();
            ArrayList arrayList = new ArrayList(zzf.size());
            for (IBinder iBinder : zzf) {
                arrayList.add(new IndoorLevel(com.google.android.gms.internal.maps.zzt.zzb(iBinder)));
            }
            return arrayList;
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public int hashCode() {
        try {
            return this.zza.zzi();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public boolean isUnderground() {
        try {
            return this.zza.zzg();
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
