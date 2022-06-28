package com.google.android.gms.internal.vision;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;
import androidx.collection.ArrayMap;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzau implements zzay {
    private static final Map<Uri, zzau> zza = new ArrayMap();
    private static final String[] zzh = {"key", CommonProperties.VALUE};
    private final ContentResolver zzb;
    private final Uri zzc;
    private final ContentObserver zzd;
    private volatile Map<String, String> zzf;
    private final Object zze = new Object();
    private final List<zzaz> zzg = new ArrayList();

    private zzau(ContentResolver contentResolver, Uri uri) {
        zzaw zzawVar = new zzaw(this, null);
        this.zzd = zzawVar;
        zzde.zza(contentResolver);
        zzde.zza(uri);
        this.zzb = contentResolver;
        this.zzc = uri;
        contentResolver.registerContentObserver(uri, false, zzawVar);
    }

    public static zzau zza(ContentResolver contentResolver, Uri uri) {
        zzau zzauVar;
        synchronized (zzau.class) {
            Map<Uri, zzau> map = zza;
            zzauVar = map.get(uri);
            if (zzauVar == null) {
                try {
                    zzau zzauVar2 = new zzau(contentResolver, uri);
                    try {
                        map.put(uri, zzauVar2);
                        zzauVar = zzauVar2;
                    } catch (SecurityException e) {
                        zzauVar = zzauVar2;
                    }
                } catch (SecurityException e2) {
                }
            }
        }
        return zzauVar;
    }

    private final Map<String, String> zzd() {
        Map<String, String> map = this.zzf;
        if (map == null) {
            synchronized (this.zze) {
                map = this.zzf;
                if (map == null) {
                    map = zze();
                    this.zzf = map;
                }
            }
        }
        return map != null ? map : Collections.emptyMap();
    }

    public final void zza() {
        synchronized (this.zze) {
            this.zzf = null;
            zzbi.zza();
        }
        synchronized (this) {
            for (zzaz zzazVar : this.zzg) {
                zzazVar.zza();
            }
        }
    }

    private final Map<String, String> zze() {
        StrictMode.ThreadPolicy allowThreadDiskReads = StrictMode.allowThreadDiskReads();
        try {
            try {
                return (Map) zzbb.zza(new zzba(this) { // from class: com.google.android.gms.internal.vision.zzax
                    private final zzau zza;

                    /* JADX INFO: Access modifiers changed from: package-private */
                    {
                        this.zza = this;
                    }

                    @Override // com.google.android.gms.internal.vision.zzba
                    public final Object zza() {
                        return this.zza.zzc();
                    }
                });
            } finally {
                StrictMode.setThreadPolicy(allowThreadDiskReads);
            }
        } catch (SQLiteException | IllegalStateException | SecurityException e) {
            Log.e("ConfigurationContentLoader", "PhenotypeFlag unable to load ContentProvider, using default values");
            StrictMode.setThreadPolicy(allowThreadDiskReads);
            return null;
        }
    }

    public static synchronized void zzb() {
        synchronized (zzau.class) {
            for (zzau zzauVar : zza.values()) {
                zzauVar.zzb.unregisterContentObserver(zzauVar.zzd);
            }
            zza.clear();
        }
    }

    @Override // com.google.android.gms.internal.vision.zzay
    public final /* synthetic */ Object zza(String str) {
        return zzd().get(str);
    }

    public final /* synthetic */ Map zzc() {
        Map map;
        Cursor query = this.zzb.query(this.zzc, zzh, null, null, null);
        if (query == null) {
            return Collections.emptyMap();
        }
        try {
            int count = query.getCount();
            if (count == 0) {
                return Collections.emptyMap();
            }
            if (count <= 256) {
                map = new ArrayMap(count);
            } else {
                map = new HashMap(count, 1.0f);
            }
            while (query.moveToNext()) {
                map.put(query.getString(0), query.getString(1));
            }
            return map;
        } finally {
            query.close();
        }
    }
}
