package com.google.android.gms.wearable;

import android.net.Uri;
import com.google.android.gms.common.data.Freezable;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public interface DataItem extends Freezable<DataItem> {
    Map<String, DataItemAsset> getAssets();

    byte[] getData();

    Uri getUri();

    DataItem setData(byte[] bArr);
}
