package com.google.android.gms.wallet.wobs;

import com.google.android.gms.maps.model.LatLng;
import java.util.Collection;
/* compiled from: com.google.android.gms:play-services-wallet@@18.1.3 */
/* loaded from: classes3.dex */
public final class zzb {
    final /* synthetic */ CommonWalletObject zza;

    public /* synthetic */ zzb(CommonWalletObject commonWalletObject, zza zzaVar) {
        this.zza = commonWalletObject;
    }

    public final zzb zza(UriData uriData) {
        this.zza.zzq.add(uriData);
        return this;
    }

    public final zzb zzb(Collection<UriData> collection) {
        this.zza.zzq.addAll(collection);
        return this;
    }

    public final zzb zzc(LabelValueRow labelValueRow) {
        this.zza.zzo.add(labelValueRow);
        return this;
    }

    public final zzb zzd(Collection<LabelValueRow> collection) {
        this.zza.zzo.addAll(collection);
        return this;
    }

    public final zzb zze(UriData uriData) {
        this.zza.zzs.add(uriData);
        return this;
    }

    public final zzb zzf(Collection<UriData> collection) {
        this.zza.zzs.addAll(collection);
        return this;
    }

    public final zzb zzg(LatLng latLng) {
        this.zza.zzl.add(latLng);
        return this;
    }

    public final zzb zzh(Collection<LatLng> collection) {
        this.zza.zzl.addAll(collection);
        return this;
    }

    public final zzb zzi(WalletObjectMessage walletObjectMessage) {
        this.zza.zzj.add(walletObjectMessage);
        return this;
    }

    public final zzb zzj(Collection<WalletObjectMessage> collection) {
        this.zza.zzj.addAll(collection);
        return this;
    }

    public final zzb zzk(TextModuleData textModuleData) {
        this.zza.zzr.add(textModuleData);
        return this;
    }

    public final zzb zzl(Collection<TextModuleData> collection) {
        this.zza.zzr.addAll(collection);
        return this;
    }

    public final zzb zzm(String str) {
        this.zza.zze = str;
        return this;
    }

    @Deprecated
    public final zzb zzn(String str) {
        this.zza.zzh = str;
        return this;
    }

    public final zzb zzo(String str) {
        this.zza.zzf = str;
        return this;
    }

    public final zzb zzp(String str) {
        this.zza.zzg = str;
        return this;
    }

    public final zzb zzq(String str) {
        this.zza.zzb = str;
        return this;
    }

    public final zzb zzr(String str) {
        this.zza.zza = str;
        return this;
    }

    @Deprecated
    public final zzb zzs(String str) {
        this.zza.zzn = str;
        return this;
    }

    @Deprecated
    public final zzb zzt(String str) {
        this.zza.zzm = str;
        return this;
    }

    public final zzb zzu(boolean z) {
        this.zza.zzp = z;
        return this;
    }

    public final zzb zzv(String str) {
        this.zza.zzd = str;
        return this;
    }

    public final zzb zzw(String str) {
        this.zza.zzc = str;
        return this;
    }

    public final zzb zzx(int i) {
        this.zza.zzi = i;
        return this;
    }

    public final zzb zzy(TimeInterval timeInterval) {
        this.zza.zzk = timeInterval;
        return this;
    }

    public final CommonWalletObject zzz() {
        return this.zza;
    }
}
