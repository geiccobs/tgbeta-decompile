package com.google.android.gms.wearable;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.common.internal.Asserts;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public class PutDataMapRequest {
    private final PutDataRequest zza;
    private final DataMap zzb;

    private PutDataMapRequest(PutDataRequest putDataRequest, DataMap dataMap) {
        this.zza = putDataRequest;
        DataMap dataMap2 = new DataMap();
        this.zzb = dataMap2;
        if (dataMap != null) {
            dataMap2.putAll(dataMap);
        }
    }

    public static PutDataMapRequest create(String path) {
        Asserts.checkNotNull(path, "path must not be null");
        return new PutDataMapRequest(PutDataRequest.create(path), null);
    }

    public static PutDataMapRequest createFromDataMapItem(DataMapItem source) {
        Asserts.checkNotNull(source, "source must not be null");
        return new PutDataMapRequest(PutDataRequest.zza(source.getUri()), source.getDataMap());
    }

    public static PutDataMapRequest createWithAutoAppendedId(String pathPrefix) {
        Asserts.checkNotNull(pathPrefix, "pathPrefix must not be null");
        return new PutDataMapRequest(PutDataRequest.createWithAutoAppendedId(pathPrefix), null);
    }

    public PutDataRequest asPutDataRequest() {
        com.google.android.gms.internal.wearable.zzj zza = com.google.android.gms.internal.wearable.zzk.zza(this.zzb);
        this.zza.setData(zza.zza.zzI());
        int size = zza.zzb.size();
        for (int i = 0; i < size; i++) {
            String num = Integer.toString(i);
            Asset asset = zza.zzb.get(i);
            if (num != null) {
                if (asset == null) {
                    throw new IllegalStateException(num.length() != 0 ? "asset cannot be null: key=".concat(num) : new String("asset cannot be null: key="));
                }
                if (Log.isLoggable(DataMap.TAG, 3)) {
                    String valueOf = String.valueOf(asset);
                    StringBuilder sb = new StringBuilder(num.length() + 33 + String.valueOf(valueOf).length());
                    sb.append("asPutDataRequest: adding asset: ");
                    sb.append(num);
                    sb.append(" ");
                    sb.append(valueOf);
                    Log.d(DataMap.TAG, sb.toString());
                }
                this.zza.putAsset(num, asset);
            } else {
                String valueOf2 = String.valueOf(asset);
                StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf2).length() + 26);
                sb2.append("asset key cannot be null: ");
                sb2.append(valueOf2);
                throw new IllegalStateException(sb2.toString());
            }
        }
        return this.zza;
    }

    public DataMap getDataMap() {
        return this.zzb;
    }

    public Uri getUri() {
        return this.zza.getUri();
    }

    public boolean isUrgent() {
        return this.zza.isUrgent();
    }

    public PutDataMapRequest setUrgent() {
        this.zza.setUrgent();
        return this;
    }
}
