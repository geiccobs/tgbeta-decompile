package com.google.android.gms.wearable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.wearable.internal.DataItemAssetParcelable;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public class PutDataRequest extends AbstractSafeParcelable {
    public static final String WEAR_URI_SCHEME = "wear";
    private final Uri zzc;
    private final Bundle zzd;
    private byte[] zze;
    private long zzf;
    public static final Parcelable.Creator<PutDataRequest> CREATOR = new zzg();
    private static final long zza = TimeUnit.MINUTES.toMillis(30);
    private static final Random zzb = new SecureRandom();

    private PutDataRequest(Uri uri) {
        this(uri, new Bundle(), null, zza);
    }

    public static PutDataRequest create(String path) {
        Preconditions.checkNotNull(path, "path must not be null");
        return zza(zzb(path));
    }

    public static PutDataRequest createFromDataItem(DataItem source) {
        Preconditions.checkNotNull(source, "source must not be null");
        PutDataRequest zza2 = zza(source.getUri());
        for (Map.Entry<String, DataItemAsset> entry : source.getAssets().entrySet()) {
            if (entry.getValue().getId() != null) {
                zza2.putAsset(entry.getKey(), Asset.createFromRef(entry.getValue().getId()));
            } else {
                String valueOf = String.valueOf(entry.getKey());
                throw new IllegalStateException(valueOf.length() != 0 ? "Cannot create an asset for a put request without a digest: ".concat(valueOf) : new String("Cannot create an asset for a put request without a digest: "));
            }
        }
        zza2.setData(source.getData());
        return zza2;
    }

    public static PutDataRequest createWithAutoAppendedId(String pathPrefix) {
        Preconditions.checkNotNull(pathPrefix, "pathPrefix must not be null");
        StringBuilder sb = new StringBuilder(pathPrefix);
        if (!pathPrefix.endsWith("/")) {
            sb.append("/");
        }
        sb.append("PN");
        sb.append(zzb.nextLong());
        return new PutDataRequest(zzb(sb.toString()));
    }

    public static PutDataRequest zza(Uri uri) {
        Preconditions.checkNotNull(uri, "uri must not be null");
        return new PutDataRequest(uri);
    }

    private static Uri zzb(String str) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("An empty path was supplied.");
        }
        if (!str.startsWith("/")) {
            throw new IllegalArgumentException("A path must start with a single / .");
        }
        if (str.startsWith("//")) {
            throw new IllegalArgumentException("A path must start with a single / .");
        }
        return new Uri.Builder().scheme(WEAR_URI_SCHEME).path(str).build();
    }

    public Asset getAsset(String key) {
        Preconditions.checkNotNull(key, "key must not be null");
        return (Asset) this.zzd.getParcelable(key);
    }

    public Map<String, Asset> getAssets() {
        HashMap hashMap = new HashMap();
        for (String str : this.zzd.keySet()) {
            hashMap.put(str, (Asset) this.zzd.getParcelable(str));
        }
        return Collections.unmodifiableMap(hashMap);
    }

    public byte[] getData() {
        return this.zze;
    }

    public Uri getUri() {
        return this.zzc;
    }

    public boolean hasAsset(String key) {
        Preconditions.checkNotNull(key, "key must not be null");
        return this.zzd.containsKey(key);
    }

    public boolean isUrgent() {
        return this.zzf == 0;
    }

    public PutDataRequest putAsset(String key, Asset value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        this.zzd.putParcelable(key, value);
        return this;
    }

    public PutDataRequest removeAsset(String key) {
        Preconditions.checkNotNull(key, "key must not be null");
        this.zzd.remove(key);
        return this;
    }

    public PutDataRequest setData(byte[] bArr) {
        this.zze = bArr;
        return this;
    }

    public PutDataRequest setUrgent() {
        this.zzf = 0L;
        return this;
    }

    public String toString() {
        return toString(Log.isLoggable(DataMap.TAG, 3));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        Preconditions.checkNotNull(dest, "dest must not be null");
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(dest);
        SafeParcelWriter.writeParcelable(dest, 2, getUri(), flags, false);
        SafeParcelWriter.writeBundle(dest, 4, this.zzd, false);
        SafeParcelWriter.writeByteArray(dest, 5, getData(), false);
        SafeParcelWriter.writeLong(dest, 6, this.zzf);
        SafeParcelWriter.finishObjectHeader(dest, beginObjectHeader);
    }

    public PutDataRequest(Uri uri, Bundle bundle, byte[] bArr, long j) {
        this.zzc = uri;
        this.zzd = bundle;
        bundle.setClassLoader((ClassLoader) Preconditions.checkNotNull(DataItemAssetParcelable.class.getClassLoader()));
        this.zze = bArr;
        this.zzf = j;
    }

    public String toString(boolean verbose) {
        StringBuilder sb = new StringBuilder("PutDataRequest[");
        byte[] bArr = this.zze;
        String valueOf = String.valueOf(bArr == null ? "null" : Integer.valueOf(bArr.length));
        StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf).length() + 7);
        sb2.append("dataSz=");
        sb2.append(valueOf);
        sb.append(sb2.toString());
        int size = this.zzd.size();
        StringBuilder sb3 = new StringBuilder(23);
        sb3.append(", numAssets=");
        sb3.append(size);
        sb.append(sb3.toString());
        String valueOf2 = String.valueOf(this.zzc);
        StringBuilder sb4 = new StringBuilder(String.valueOf(valueOf2).length() + 6);
        sb4.append(", uri=");
        sb4.append(valueOf2);
        sb.append(sb4.toString());
        long j = this.zzf;
        StringBuilder sb5 = new StringBuilder(35);
        sb5.append(", syncDeadline=");
        sb5.append(j);
        sb.append(sb5.toString());
        if (!verbose) {
            sb.append("]");
            return sb.toString();
        }
        sb.append("]\n  assets: ");
        for (String str : this.zzd.keySet()) {
            String valueOf3 = String.valueOf(this.zzd.getParcelable(str));
            StringBuilder sb6 = new StringBuilder(String.valueOf(str).length() + 7 + String.valueOf(valueOf3).length());
            sb6.append("\n    ");
            sb6.append(str);
            sb6.append(": ");
            sb6.append(valueOf3);
            sb.append(sb6.toString());
        }
        sb.append("\n  ]");
        return sb.toString();
    }
}
