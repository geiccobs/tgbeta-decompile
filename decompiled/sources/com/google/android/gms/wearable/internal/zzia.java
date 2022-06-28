package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import java.util.List;
import javax.annotation.Nullable;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes.dex */
public final class zzia<T> extends zzes {
    private ListenerHolder<? extends DataApi.DataListener> zza;
    private ListenerHolder<? extends MessageApi.MessageListener> zzb;
    private ListenerHolder<? extends ChannelApi.ChannelListener> zzc;
    private ListenerHolder<? extends CapabilityApi.CapabilityListener> zzd;
    private final IntentFilter[] zze;
    @Nullable
    private final String zzf;

    private zzia(IntentFilter[] intentFilterArr, @Nullable String str) {
        this.zze = (IntentFilter[]) Preconditions.checkNotNull(intentFilterArr);
        this.zzf = str;
    }

    public static zzia<DataApi.DataListener> zzl(ListenerHolder<? extends DataApi.DataListener> listenerHolder, IntentFilter[] intentFilterArr) {
        zzia<DataApi.DataListener> zziaVar = new zzia<>(intentFilterArr, null);
        ((zzia) zziaVar).zza = (ListenerHolder) Preconditions.checkNotNull(listenerHolder);
        return zziaVar;
    }

    public static zzia<MessageApi.MessageListener> zzm(ListenerHolder<? extends MessageApi.MessageListener> listenerHolder, IntentFilter[] intentFilterArr) {
        zzia<MessageApi.MessageListener> zziaVar = new zzia<>(intentFilterArr, null);
        ((zzia) zziaVar).zzb = (ListenerHolder) Preconditions.checkNotNull(listenerHolder);
        return zziaVar;
    }

    public static zzia<ChannelApi.ChannelListener> zzn(ListenerHolder<? extends ChannelApi.ChannelListener> listenerHolder, IntentFilter[] intentFilterArr) {
        zzia<ChannelApi.ChannelListener> zziaVar = new zzia<>(intentFilterArr, null);
        ((zzia) zziaVar).zzc = (ListenerHolder) Preconditions.checkNotNull(listenerHolder);
        return zziaVar;
    }

    public static zzia<ChannelApi.ChannelListener> zzo(ListenerHolder<? extends ChannelApi.ChannelListener> listenerHolder, String str, IntentFilter[] intentFilterArr) {
        zzia<ChannelApi.ChannelListener> zziaVar = new zzia<>(intentFilterArr, (String) Preconditions.checkNotNull(str));
        ((zzia) zziaVar).zzc = (ListenerHolder) Preconditions.checkNotNull(listenerHolder);
        return zziaVar;
    }

    public static zzia<CapabilityApi.CapabilityListener> zzp(ListenerHolder<? extends CapabilityApi.CapabilityListener> listenerHolder, IntentFilter[] intentFilterArr) {
        zzia<CapabilityApi.CapabilityListener> zziaVar = new zzia<>(intentFilterArr, null);
        ((zzia) zziaVar).zzd = (ListenerHolder) Preconditions.checkNotNull(listenerHolder);
        return zziaVar;
    }

    private static void zzt(ListenerHolder<?> listenerHolder) {
        if (listenerHolder != null) {
            listenerHolder.clear();
        }
    }

    @Override // com.google.android.gms.wearable.internal.zzet
    public final void zzb(DataHolder dataHolder) {
        ListenerHolder<? extends DataApi.DataListener> listenerHolder = this.zza;
        if (listenerHolder != null) {
            listenerHolder.notifyListener(new zzhx(dataHolder));
        } else {
            dataHolder.close();
        }
    }

    @Override // com.google.android.gms.wearable.internal.zzet
    public final void zzc(zzfj zzfjVar) {
        ListenerHolder<? extends MessageApi.MessageListener> listenerHolder = this.zzb;
        if (listenerHolder != null) {
            listenerHolder.notifyListener(new zzhy(zzfjVar));
        }
    }

    @Override // com.google.android.gms.wearable.internal.zzet
    public final void zzd(zzfw zzfwVar) {
    }

    @Override // com.google.android.gms.wearable.internal.zzet
    public final void zze(zzfw zzfwVar) {
    }

    @Override // com.google.android.gms.wearable.internal.zzet
    public final void zzf(List<zzfw> list) {
    }

    @Override // com.google.android.gms.wearable.internal.zzet
    public final void zzg(zzag zzagVar) {
        ListenerHolder<? extends CapabilityApi.CapabilityListener> listenerHolder = this.zzd;
        if (listenerHolder != null) {
            listenerHolder.notifyListener(new zzhw(zzagVar));
        }
    }

    @Override // com.google.android.gms.wearable.internal.zzet
    public final void zzh(zzl zzlVar) {
    }

    @Override // com.google.android.gms.wearable.internal.zzet
    public final void zzi(zzi zziVar) {
    }

    @Override // com.google.android.gms.wearable.internal.zzet
    public final void zzj(zzax zzaxVar) {
        ListenerHolder<? extends ChannelApi.ChannelListener> listenerHolder = this.zzc;
        if (listenerHolder != null) {
            listenerHolder.notifyListener(new zzhz(zzaxVar));
        }
    }

    @Override // com.google.android.gms.wearable.internal.zzet
    public final void zzk(zzfj zzfjVar, zzeo zzeoVar) {
    }

    public final void zzq() {
        zzt(this.zza);
        this.zza = null;
        zzt(this.zzb);
        this.zzb = null;
        zzt(this.zzc);
        this.zzc = null;
        zzt(this.zzd);
        this.zzd = null;
    }

    public final IntentFilter[] zzr() {
        return this.zze;
    }

    @Nullable
    public final String zzs() {
        return this.zzf;
    }
}
