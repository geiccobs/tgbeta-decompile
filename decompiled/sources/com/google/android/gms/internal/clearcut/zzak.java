package com.google.android.gms.internal.clearcut;

import android.content.SharedPreferences;
import android.util.Log;
/* loaded from: classes.dex */
public final class zzak extends zzae<String> {
    public zzak(zzao zzaoVar, String str, String str2) {
        super(zzaoVar, str, str2, null);
    }

    /* renamed from: zzc */
    public final String zza(SharedPreferences sharedPreferences) {
        try {
            return sharedPreferences.getString(this.zzds, null);
        } catch (ClassCastException e) {
            String valueOf = String.valueOf(this.zzds);
            Log.e("PhenotypeFlag", valueOf.length() != 0 ? "Invalid string value in SharedPreferences for ".concat(valueOf) : new String("Invalid string value in SharedPreferences for "), e);
            return null;
        }
    }

    @Override // com.google.android.gms.internal.clearcut.zzae
    public final /* synthetic */ String zzb(String str) {
        return str;
    }
}
