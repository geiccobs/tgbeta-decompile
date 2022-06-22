package com.android.billingclient.api;

import android.os.Bundle;
import com.android.billingclient.api.BillingResult;
import com.google.android.gms.internal.play_billing.zzb;
import java.util.ArrayList;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes.dex */
public final class zzbi {
    public static BillingResult zza(Bundle bundle, String str, String str2) {
        BillingResult billingResult = zzbb.zzj;
        if (bundle == null) {
            zzb.zzo("BillingClient", String.format("%s got null owned items list", str2));
            return billingResult;
        }
        int zzb = zzb.zzb(bundle, "BillingClient");
        String zzk = zzb.zzk(bundle, "BillingClient");
        BillingResult.Builder newBuilder = BillingResult.newBuilder();
        newBuilder.setResponseCode(zzb);
        newBuilder.setDebugMessage(zzk);
        BillingResult build = newBuilder.build();
        if (zzb != 0) {
            zzb.zzo("BillingClient", String.format("%s failed. Response code: %s", str2, Integer.valueOf(zzb)));
            return build;
        } else if (!bundle.containsKey("INAPP_PURCHASE_ITEM_LIST") || !bundle.containsKey("INAPP_PURCHASE_DATA_LIST") || !bundle.containsKey("INAPP_DATA_SIGNATURE_LIST")) {
            zzb.zzo("BillingClient", String.format("Bundle returned from %s doesn't contain required fields.", str2));
            return billingResult;
        } else {
            ArrayList<String> stringArrayList = bundle.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
            ArrayList<String> stringArrayList2 = bundle.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
            ArrayList<String> stringArrayList3 = bundle.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
            if (stringArrayList == null) {
                zzb.zzo("BillingClient", String.format("Bundle returned from %s contains null SKUs list.", str2));
                return billingResult;
            } else if (stringArrayList2 == null) {
                zzb.zzo("BillingClient", String.format("Bundle returned from %s contains null purchases list.", str2));
                return billingResult;
            } else if (stringArrayList3 != null) {
                return zzbb.zzl;
            } else {
                zzb.zzo("BillingClient", String.format("Bundle returned from %s contains null signatures list.", str2));
                return billingResult;
            }
        }
    }
}
