package com.google.android.gms.internal.vision;

import android.util.Base64;
import android.util.Log;
import java.io.IOException;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzbm extends zzbi<T> {
    private final /* synthetic */ zzbp zza;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zzbm(zzbo zzboVar, String str, Object obj, boolean z, zzbp zzbpVar) {
        super(zzboVar, str, obj, true, null);
        this.zza = zzbpVar;
    }

    /* JADX WARN: Type inference failed for: r4v3, types: [T, java.lang.Object] */
    @Override // com.google.android.gms.internal.vision.zzbi
    final T zza(Object obj) {
        if (obj instanceof String) {
            try {
                return this.zza.zza(Base64.decode((String) obj, 3));
            } catch (IOException e) {
            } catch (IllegalArgumentException e2) {
            }
        }
        String zzb = super.zzb();
        String valueOf = String.valueOf(obj);
        StringBuilder sb = new StringBuilder(String.valueOf(zzb).length() + 27 + String.valueOf(valueOf).length());
        sb.append("Invalid byte[] value for ");
        sb.append(zzb);
        sb.append(": ");
        sb.append(valueOf);
        Log.e("PhenotypeFlag", sb.toString());
        return null;
    }
}
