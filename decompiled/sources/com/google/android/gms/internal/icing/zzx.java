package com.google.android.gms.internal.icing;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.appindexing.AppIndexApi;
import com.google.android.gms.common.internal.ImagesContract;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.zip.CRC32;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzx extends AbstractSafeParcelable {
    public static final Parcelable.Creator<zzx> CREATOR = new zzy();
    final zzi zza;
    final long zzb;
    int zzc;
    public final String zzd;
    final zzg zze;
    final boolean zzf;
    int zzg;
    int zzh;
    public final String zzi;

    public zzx(zzi zziVar, long j, int i, String str, zzg zzgVar, boolean z, int i2, int i3, String str2) {
        this.zza = zziVar;
        this.zzb = j;
        this.zzc = i;
        this.zzd = str;
        this.zze = zzgVar;
        this.zzf = z;
        this.zzg = i2;
        this.zzh = i3;
        this.zzi = str2;
    }

    public static zzi zza(String str, Intent intent) {
        return zzc(str, zze(intent));
    }

    public static zzf zzb(Intent intent, String str, Uri uri, String str2, List<AppIndexApi.AppIndexingLink> list) {
        String string;
        zzf zzfVar = new zzf();
        if (str != null) {
            zzr zzrVar = new zzr("title");
            zzrVar.zzc(true);
            zzrVar.zzd(CommonProperties.NAME);
            zzfVar.zza(new zzk(str, zzrVar.zze(), zzq.zzb("text1"), null));
        }
        if (uri != null) {
            String uri2 = uri.toString();
            zzr zzrVar2 = new zzr("web_url");
            zzrVar2.zzb(true);
            zzrVar2.zzd(ImagesContract.URL);
            zzfVar.zza(new zzk(uri2, zzrVar2.zze(), zzk.zza, null));
        }
        if (list != null) {
            zzan zza = zzaq.zza();
            int size = list.size();
            zzap[] zzapVarArr = new zzap[size];
            for (int i = 0; i < size; i++) {
                zzao zza2 = zzap.zza();
                AppIndexApi.AppIndexingLink appIndexingLink = list.get(i);
                zza2.zza(appIndexingLink.appIndexingUrl.toString());
                zza2.zzc(appIndexingLink.viewId);
                Uri uri3 = appIndexingLink.webUrl;
                if (uri3 != null) {
                    zza2.zzb(uri3.toString());
                }
                zzapVarArr[i] = zza2.zzj();
            }
            zza.zza(Arrays.asList(zzapVarArr));
            byte[] zzh = zza.zzj().zzh();
            zzr zzrVar3 = new zzr("outlinks");
            zzrVar3.zzb(true);
            zzrVar3.zzd(".private:outLinks");
            zzrVar3.zza("blob");
            zzfVar.zza(new zzk(null, zzrVar3.zze(), zzk.zza, zzh));
        }
        String action = intent.getAction();
        if (action != null) {
            zzfVar.zza(zzd("intent_action", action));
        }
        String dataString = intent.getDataString();
        if (dataString != null) {
            zzfVar.zza(zzd("intent_data", dataString));
        }
        ComponentName component = intent.getComponent();
        if (component != null) {
            zzfVar.zza(zzd("intent_activity", component.getClassName()));
        }
        Bundle extras = intent.getExtras();
        if (extras != null && (string = extras.getString("intent_extra_data_key")) != null) {
            zzfVar.zza(zzd("intent_extra_data", string));
        }
        if (str2 != null) {
            zzfVar.zzb(str2);
        }
        zzfVar.zzc(true);
        return zzfVar;
    }

    private static zzi zzc(String str, String str2) {
        return new zzi(str, "", str2);
    }

    private static zzk zzd(String str, String str2) {
        zzr zzrVar = new zzr(str);
        zzrVar.zzb(true);
        return new zzk(str2, zzrVar.zze(), zzq.zzb(str), null);
    }

    private static String zze(Intent intent) {
        String uri = intent.toUri(1);
        CRC32 crc32 = new CRC32();
        try {
            crc32.update(uri.getBytes("UTF-8"));
            return Long.toHexString(crc32.getValue());
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public final String toString() {
        return String.format(Locale.US, "UsageInfo[documentId=%s, timestamp=%d, usageType=%d, status=%d]", this.zza, Long.valueOf(this.zzb), Integer.valueOf(this.zzc), Integer.valueOf(this.zzh));
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeParcelable(parcel, 1, this.zza, i, false);
        SafeParcelWriter.writeLong(parcel, 2, this.zzb);
        SafeParcelWriter.writeInt(parcel, 3, this.zzc);
        SafeParcelWriter.writeString(parcel, 4, this.zzd, false);
        SafeParcelWriter.writeParcelable(parcel, 5, this.zze, i, false);
        SafeParcelWriter.writeBoolean(parcel, 6, this.zzf);
        SafeParcelWriter.writeInt(parcel, 7, this.zzg);
        SafeParcelWriter.writeInt(parcel, 8, this.zzh);
        SafeParcelWriter.writeString(parcel, 9, this.zzi, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }

    public zzx(String str, Intent intent, String str2, Uri uri, String str3, List<AppIndexApi.AppIndexingLink> list, int i) {
        this(zzc(str, zze(intent)), System.currentTimeMillis(), 0, null, zzb(intent, str2, uri, null, list).zze(), false, -1, 1, null);
    }
}
