package com.google.android.gms.internal.icing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndexApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import java.util.List;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class zzal implements AppIndexApi, zzz {
    private static final String zza = zzal.class.getSimpleName();

    public static Intent zzb(String str, Uri uri) {
        zzc(str, uri);
        if (uri == null || !zzd(uri)) {
            if (uri == null || !zze(uri)) {
                String valueOf = String.valueOf(uri);
                StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 70);
                sb.append("appIndexingUri is neither an HTTP(S) URL nor an \"android-app://\" URL: ");
                sb.append(valueOf);
                throw new RuntimeException(sb.toString());
            }
            List<String> pathSegments = uri.getPathSegments();
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(pathSegments.get(0));
            if (pathSegments.size() > 1) {
                builder.authority(pathSegments.get(1));
                for (int i = 2; i < pathSegments.size(); i++) {
                    builder.appendPath(pathSegments.get(i));
                }
            } else {
                String str2 = zza;
                String valueOf2 = String.valueOf(uri);
                StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf2).length() + 88);
                sb2.append("The app URI must have the format: android-app://<package_name>/<scheme>/<path>. But got ");
                sb2.append(valueOf2);
                Log.e(str2, sb2.toString());
            }
            builder.encodedQuery(uri.getEncodedQuery());
            builder.encodedFragment(uri.getEncodedFragment());
            return new Intent("android.intent.action.VIEW", builder.build());
        }
        return new Intent("android.intent.action.VIEW", uri);
    }

    private static void zzc(String str, Uri uri) {
        if (uri == null || !zzd(uri)) {
            if (uri == null || !zze(uri)) {
                String valueOf = String.valueOf(uri);
                StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 176);
                sb.append("AppIndex: The URI scheme must either be 'http(s)' or 'android-app'. If the latter, it must follow the format 'android-app://<package_name>/<scheme>/<host_path>'. Provided URI: ");
                sb.append(valueOf);
                throw new IllegalArgumentException(sb.toString());
            } else if (str == null || str.equals(uri.getHost())) {
                List<String> pathSegments = uri.getPathSegments();
                if (!pathSegments.isEmpty() && !pathSegments.get(0).isEmpty()) {
                    return;
                }
                String valueOf2 = String.valueOf(uri);
                StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf2).length() + 128);
                sb2.append("AppIndex: The app URI scheme must exist and follow the format android-app://<package_name>/<scheme>/<host_path>). Provided URI: ");
                sb2.append(valueOf2);
                throw new IllegalArgumentException(sb2.toString());
            } else {
                String valueOf3 = String.valueOf(uri);
                StringBuilder sb3 = new StringBuilder(String.valueOf(valueOf3).length() + 150);
                sb3.append("AppIndex: The android-app URI host must match the package name and follow the format android-app://<package_name>/<scheme>/<host_path>. Provided URI: ");
                sb3.append(valueOf3);
                throw new IllegalArgumentException(sb3.toString());
            }
        }
        String host = uri.getHost();
        if (host == null || !host.isEmpty()) {
            return;
        }
        String valueOf4 = String.valueOf(uri);
        StringBuilder sb4 = new StringBuilder(String.valueOf(valueOf4).length() + 98);
        sb4.append("AppIndex: The web URL must have a host (follow the format http(s)://<host>/<path>). Provided URI: ");
        sb4.append(valueOf4);
        throw new IllegalArgumentException(sb4.toString());
    }

    private static boolean zzd(Uri uri) {
        String scheme = uri.getScheme();
        return "http".equals(scheme) || "https".equals(scheme);
    }

    private static boolean zze(Uri uri) {
        return "android-app".equals(uri.getScheme());
    }

    private final PendingResult<Status> zzf(GoogleApiClient googleApiClient, Action action, int i) {
        return zza(googleApiClient, zzaf.zza(action, System.currentTimeMillis(), googleApiClient.getContext().getPackageName(), i));
    }

    @Override // com.google.android.gms.appindexing.AppIndexApi
    public final AppIndexApi.ActionResult action(GoogleApiClient googleApiClient, Action action) {
        return new zzah(this, zzf(googleApiClient, action, 1), action);
    }

    @Override // com.google.android.gms.appindexing.AppIndexApi
    public final PendingResult<Status> end(GoogleApiClient googleApiClient, Action action) {
        return zzf(googleApiClient, action, 2);
    }

    @Override // com.google.android.gms.appindexing.AppIndexApi
    public final PendingResult<Status> start(GoogleApiClient googleApiClient, Action action) {
        return zzf(googleApiClient, action, 1);
    }

    @Override // com.google.android.gms.appindexing.AppIndexApi
    public final PendingResult<Status> view(GoogleApiClient googleApiClient, Activity activity, Intent intent, String str, Uri uri, List<AppIndexApi.AppIndexingLink> list) {
        String packageName = googleApiClient.getContext().getPackageName();
        if (list != null) {
            for (AppIndexApi.AppIndexingLink appIndexingLink : list) {
                zzc(null, appIndexingLink.appIndexingUrl);
            }
        }
        return zza(googleApiClient, new zzx(packageName, intent, str, uri, null, list, 1));
    }

    @Override // com.google.android.gms.appindexing.AppIndexApi
    public final PendingResult<Status> viewEnd(GoogleApiClient googleApiClient, Activity activity, Intent intent) {
        String packageName = googleApiClient.getContext().getPackageName();
        zzw zzwVar = new zzw();
        zzwVar.zza(zzx.zza(packageName, intent));
        zzwVar.zzb(System.currentTimeMillis());
        zzwVar.zzc(0);
        zzwVar.zzf(2);
        return zza(googleApiClient, zzwVar.zzg());
    }

    public final PendingResult<Status> zza(GoogleApiClient googleApiClient, zzx... zzxVarArr) {
        return googleApiClient.enqueue(new zzag(this, googleApiClient, zzxVarArr));
    }

    @Override // com.google.android.gms.appindexing.AppIndexApi
    public final PendingResult<Status> view(GoogleApiClient googleApiClient, Activity activity, Uri uri, String str, Uri uri2, List<AppIndexApi.AppIndexingLink> list) {
        String packageName = googleApiClient.getContext().getPackageName();
        zzc(packageName, uri);
        Intent zzb = zzb(packageName, uri);
        String packageName2 = googleApiClient.getContext().getPackageName();
        if (list != null) {
            for (AppIndexApi.AppIndexingLink appIndexingLink : list) {
                zzc(null, appIndexingLink.appIndexingUrl);
            }
        }
        return zza(googleApiClient, new zzx(packageName2, zzb, str, uri2, null, list, 1));
    }

    @Override // com.google.android.gms.appindexing.AppIndexApi
    public final PendingResult<Status> viewEnd(GoogleApiClient googleApiClient, Activity activity, Uri uri) {
        Intent zzb = zzb(googleApiClient.getContext().getPackageName(), uri);
        String packageName = googleApiClient.getContext().getPackageName();
        zzw zzwVar = new zzw();
        zzwVar.zza(zzx.zza(packageName, zzb));
        zzwVar.zzb(System.currentTimeMillis());
        zzwVar.zzc(0);
        zzwVar.zzf(2);
        return zza(googleApiClient, zzwVar.zzg());
    }
}
