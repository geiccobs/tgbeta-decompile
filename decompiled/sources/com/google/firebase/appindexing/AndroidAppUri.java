package com.google.firebase.appindexing;

import android.net.Uri;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Objects;
import java.util.List;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public final class AndroidAppUri {
    private final Uri zza;

    private AndroidAppUri(Uri uri) {
        this.zza = uri;
    }

    public static AndroidAppUri newAndroidAppUri(Uri uri) {
        AndroidAppUri androidAppUri = new AndroidAppUri(uri);
        if (!"android-app".equals(androidAppUri.zza.getScheme())) {
            throw new IllegalArgumentException("android-app scheme is required.");
        }
        if (TextUtils.isEmpty(androidAppUri.getPackageName())) {
            throw new IllegalArgumentException("Package name is empty.");
        }
        return androidAppUri;
    }

    public boolean equals(Object object) {
        if (object instanceof AndroidAppUri) {
            return this.zza.equals(((AndroidAppUri) object).zza);
        }
        return false;
    }

    public Uri getDeepLinkUri() {
        List<String> pathSegments = this.zza.getPathSegments();
        if (pathSegments.size() > 0) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme(pathSegments.get(0));
            if (pathSegments.size() > 1) {
                builder.authority(pathSegments.get(1));
                for (int i = 2; i < pathSegments.size(); i++) {
                    builder.appendPath(pathSegments.get(i));
                }
            }
            builder.encodedQuery(this.zza.getEncodedQuery());
            builder.encodedFragment(this.zza.getEncodedFragment());
            return builder.build();
        }
        return null;
    }

    public String getPackageName() {
        return this.zza.getAuthority();
    }

    public int hashCode() {
        return Objects.hashCode(this.zza);
    }

    public String toString() {
        return this.zza.toString();
    }
}
