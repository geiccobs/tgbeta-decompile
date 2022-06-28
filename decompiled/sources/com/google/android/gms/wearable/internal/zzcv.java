package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataClient;
import java.io.InputStream;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzcv extends DataClient.GetFdForAssetResponse implements Releasable {
    private final DataApi.GetFdForAssetResult zza;

    public zzcv(DataApi.GetFdForAssetResult getFdForAssetResult) {
        this.zza = getFdForAssetResult;
    }

    @Override // com.google.android.gms.wearable.DataClient.GetFdForAssetResponse
    public final ParcelFileDescriptor getFdForAsset() {
        return this.zza.getFd();
    }

    @Override // com.google.android.gms.wearable.DataClient.GetFdForAssetResponse
    public final InputStream getInputStream() {
        return this.zza.getInputStream();
    }

    @Override // com.google.android.gms.common.api.Releasable
    public final void release() {
        this.zza.release();
    }
}
