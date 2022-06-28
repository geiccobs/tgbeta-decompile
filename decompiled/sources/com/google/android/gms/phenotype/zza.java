package com.google.android.gms.phenotype;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes3.dex */
public final class zza {
    private static final ConcurrentHashMap<Uri, zza> zzg = new ConcurrentHashMap<>();
    private static final String[] zzl = {"key", CommonProperties.VALUE};
    private final Uri uri;
    private final ContentResolver zzh;
    private volatile Map<String, String> zzk;
    private final Object zzj = new Object();
    private final ContentObserver zzi = new zzb(this, null);

    private zza(ContentResolver contentResolver, Uri uri) {
        this.zzh = contentResolver;
        this.uri = uri;
    }

    public static zza zza(ContentResolver contentResolver, Uri uri) {
        ConcurrentHashMap<Uri, zza> concurrentHashMap = zzg;
        zza zzaVar = concurrentHashMap.get(uri);
        if (zzaVar == null) {
            zza zzaVar2 = new zza(contentResolver, uri);
            zza putIfAbsent = concurrentHashMap.putIfAbsent(uri, zzaVar2);
            if (putIfAbsent != null) {
                return putIfAbsent;
            }
            zzaVar2.zzh.registerContentObserver(zzaVar2.uri, false, zzaVar2.zzi);
            return zzaVar2;
        }
        return zzaVar;
    }

    private final Map<String, String> zzc() {
        HashMap hashMap = new HashMap();
        Cursor query = this.zzh.query(this.uri, zzl, null, null, null);
        if (query != null) {
            while (query.moveToNext()) {
                try {
                    hashMap.put(query.getString(0), query.getString(1));
                } finally {
                    query.close();
                }
            }
        }
        return hashMap;
    }

    public final Map<String, String> zza() {
        Map<String, String> zzc = PhenotypeFlag.zza("gms:phenotype:phenotype_flag:debug_disable_caching", false) ? zzc() : this.zzk;
        if (zzc == null) {
            synchronized (this.zzj) {
                zzc = this.zzk;
                if (zzc == null) {
                    zzc = zzc();
                    this.zzk = zzc;
                }
            }
        }
        return zzc;
    }

    public final void zzb() {
        synchronized (this.zzj) {
            this.zzk = null;
        }
    }
}
