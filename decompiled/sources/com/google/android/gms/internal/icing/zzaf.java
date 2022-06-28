package com.google.android.gms.internal.icing;

import android.accounts.Account;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.common.internal.ImagesContract;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzaf {
    public static zzx zza(Action action, long j, String str, int i) {
        String str2;
        String str3;
        String str4;
        Uri uri;
        int i2;
        Bundle bundle = new Bundle();
        bundle.putAll(action.zza());
        Bundle bundle2 = bundle.getBundle("object");
        if (bundle2 != null) {
            String string = bundle2.getString("id");
            uri = string != null ? Uri.parse(string) : null;
            str4 = bundle2.getString(CommonProperties.NAME);
            str3 = bundle2.getString(CommonProperties.TYPE);
            str2 = bundle2.getString(ImagesContract.URL);
        } else {
            str2 = null;
            uri = null;
            str4 = null;
            str3 = null;
        }
        Intent zzb = zzal.zzb(str, str2 != null ? Uri.parse(str2) : null);
        zzf zzb2 = zzx.zzb(zzb, str4, uri, str3, null);
        byte[] byteArray = bundle.getByteArray(".private:ssbContext");
        if (byteArray != null) {
            zzb2.zza(zzk.zza(byteArray));
            bundle.remove(".private:ssbContext");
        }
        String string2 = bundle.getString(".private:accountName");
        if (string2 != null) {
            zzb2.zzd(new Account(string2, "com.google"));
            bundle.remove(".private:accountName");
        }
        boolean z = false;
        if (!bundle.containsKey(".private:isContextOnly") || !bundle.getBoolean(".private:isContextOnly")) {
            i2 = 0;
        } else {
            bundle.remove(".private:isContextOnly");
            i2 = 4;
        }
        if (bundle.containsKey(".private:isDeviceOnly")) {
            z = bundle.getBoolean(".private:isDeviceOnly", false);
            bundle.remove(".private:isDeviceOnly");
        }
        zzgf zzb3 = zzb(bundle);
        zzr zzrVar = new zzr(".private:action");
        zzrVar.zzb(true);
        zzrVar.zzd(".private:action");
        zzrVar.zza("blob");
        zzb2.zza(new zzk(zzb3.zzh(), zzrVar.zze()));
        zzw zzwVar = new zzw();
        zzwVar.zza(zzx.zza(str, zzb));
        zzwVar.zzb(j);
        zzwVar.zzc(i2);
        zzwVar.zzd(zzb2.zze());
        zzwVar.zze(z);
        zzwVar.zzf(i);
        return zzwVar.zzg();
    }

    public static zzgf zzb(Bundle bundle) {
        zzge zza = zzgf.zza();
        for (String str : bundle.keySet()) {
            Object obj = bundle.get(str);
            if (obj instanceof String) {
                zzgg zza2 = zzgh.zza();
                zza2.zzb((String) obj);
                zzgc zza3 = zzgd.zza();
                zza3.zza(str);
                zza3.zzb(zza2.zzj());
                zza.zzb(zza3.zzj());
            } else if (obj instanceof Bundle) {
                zzgg zza4 = zzgh.zza();
                zza4.zzc(zzb((Bundle) obj));
                zzgc zza5 = zzgd.zza();
                zza5.zza(str);
                zza5.zzb(zza4.zzj());
                zza.zzb(zza5.zzj());
            } else {
                int i = 0;
                if (obj instanceof String[]) {
                    String[] strArr = (String[]) obj;
                    int length = strArr.length;
                    while (i < length) {
                        String str2 = strArr[i];
                        if (str2 != null) {
                            zzgg zza6 = zzgh.zza();
                            zza6.zzb(str2);
                            zzgc zza7 = zzgd.zza();
                            zza7.zza(str);
                            zza7.zzb(zza6.zzj());
                            zza.zzb(zza7.zzj());
                        }
                        i++;
                    }
                } else if (obj instanceof Bundle[]) {
                    Bundle[] bundleArr = (Bundle[]) obj;
                    int length2 = bundleArr.length;
                    while (i < length2) {
                        Bundle bundle2 = bundleArr[i];
                        if (bundle2 != null) {
                            zzgg zza8 = zzgh.zza();
                            zza8.zzc(zzb(bundle2));
                            zzgc zza9 = zzgd.zza();
                            zza9.zza(str);
                            zza9.zzb(zza8.zzj());
                            zza.zzb(zza9.zzj());
                        }
                        i++;
                    }
                } else if (obj instanceof Boolean) {
                    zzgg zza10 = zzgh.zza();
                    zza10.zza(((Boolean) obj).booleanValue());
                    zzgc zza11 = zzgd.zza();
                    zza11.zza(str);
                    zza11.zzb(zza10.zzj());
                    zza.zzb(zza11.zzj());
                } else {
                    String valueOf = String.valueOf(obj);
                    StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 19);
                    sb.append("Unsupported value: ");
                    sb.append(valueOf);
                    Log.e("SearchIndex", sb.toString());
                }
            }
        }
        String string = bundle.getString(CommonProperties.TYPE);
        if (string != null) {
            zza.zza(string);
        }
        return zza.zzj();
    }
}
