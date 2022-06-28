package com.google.android.gms.wearable;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.internal.wearable.zzcc;
import java.util.ArrayList;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public class DataMapItem {
    private final Uri zza;
    private final DataMap zzb;

    private DataMapItem(DataItem dataItem) {
        DataMap dataMap;
        this.zza = dataItem.getUri();
        DataItem freeze = dataItem.freeze();
        if (freeze.getData() != null || freeze.getAssets().size() <= 0) {
            if (freeze.getData() == null) {
                dataMap = new DataMap();
            } else {
                try {
                    ArrayList arrayList = new ArrayList();
                    int size = freeze.getAssets().size();
                    for (int i = 0; i < size; i++) {
                        DataItemAsset dataItemAsset = freeze.getAssets().get(Integer.toString(i));
                        if (dataItemAsset != null) {
                            arrayList.add(Asset.createFromRef(dataItemAsset.getId()));
                        } else {
                            String valueOf = String.valueOf(freeze);
                            StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 64);
                            sb.append("Cannot find DataItemAsset referenced in data at ");
                            sb.append(i);
                            sb.append(" for ");
                            sb.append(valueOf);
                            throw new IllegalStateException(sb.toString());
                        }
                    }
                    dataMap = com.google.android.gms.internal.wearable.zzk.zzb(new com.google.android.gms.internal.wearable.zzj(com.google.android.gms.internal.wearable.zzw.zzb(freeze.getData()), arrayList));
                } catch (zzcc | NullPointerException e) {
                    String valueOf2 = String.valueOf(freeze.getUri());
                    String encodeToString = Base64.encodeToString(freeze.getData(), 0);
                    StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf2).length() + 50 + String.valueOf(encodeToString).length());
                    sb2.append("Unable to parse datamap from dataItem. uri=");
                    sb2.append(valueOf2);
                    sb2.append(", data=");
                    sb2.append(encodeToString);
                    Log.w("DataItem", sb2.toString());
                    String valueOf3 = String.valueOf(freeze.getUri());
                    StringBuilder sb3 = new StringBuilder(String.valueOf(valueOf3).length() + 44);
                    sb3.append("Unable to parse datamap from dataItem.  uri=");
                    sb3.append(valueOf3);
                    throw new IllegalStateException(sb3.toString(), e);
                }
            }
            this.zzb = dataMap;
            return;
        }
        throw new IllegalArgumentException("Cannot create DataMapItem from a DataItem  that wasn't made with DataMapItem.");
    }

    public static DataMapItem fromDataItem(DataItem dataItem) {
        Asserts.checkNotNull(dataItem, "dataItem must not be null");
        return new DataMapItem(dataItem);
    }

    public DataMap getDataMap() {
        return this.zzb;
    }

    public Uri getUri() {
        return this.zza;
    }
}
