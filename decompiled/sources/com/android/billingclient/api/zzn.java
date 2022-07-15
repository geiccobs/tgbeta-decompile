package com.android.billingclient.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.google.android.gms.internal.play_billing.zzb;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes.dex */
public final class zzn extends BroadcastReceiver {
    final /* synthetic */ zzo zza;
    private final PurchasesUpdatedListener zzb;
    private final zzbe zzc;
    private final zzc zzd;
    private boolean zze;

    public /* synthetic */ zzn(zzo zzoVar, zzbe zzbeVar, zzm zzmVar) {
        this.zza = zzoVar;
        this.zzb = null;
        this.zzd = null;
        this.zzc = null;
    }

    @Override // android.content.BroadcastReceiver
    public final void onReceive(Context context, Intent intent) {
        BillingResult zzi = zzb.zzi(intent, "BillingBroadcastManager");
        String action = intent.getAction();
        if (action.equals("com.android.vending.billing.PURCHASES_UPDATED")) {
            this.zzb.onPurchasesUpdated(zzi, zzb.zzm(intent.getExtras()));
        } else if (action.equals("com.android.vending.billing.ALTERNATIVE_BILLING")) {
            Bundle extras = intent.getExtras();
            if (zzi.getResponseCode() != 0) {
                this.zzb.onPurchasesUpdated(zzi, com.google.android.gms.internal.play_billing.zzu.zzl());
            } else if (this.zzd == null) {
                zzb.zzo("BillingBroadcastManager", "AlternativeBillingListener is null.");
                this.zzb.onPurchasesUpdated(zzbb.zzj, com.google.android.gms.internal.play_billing.zzu.zzl());
            } else if (extras == null) {
                zzb.zzo("BillingBroadcastManager", "Bundle is null.");
                this.zzb.onPurchasesUpdated(zzbb.zzj, com.google.android.gms.internal.play_billing.zzu.zzl());
            } else {
                String string = extras.getString("ALTERNATIVE_BILLING_USER_CHOICE_DATA");
                if (string != null) {
                    try {
                        JSONArray optJSONArray = new JSONObject(string).optJSONArray("products");
                        ArrayList arrayList = new ArrayList();
                        if (optJSONArray != null) {
                            for (int i = 0; i < optJSONArray.length(); i++) {
                                JSONObject optJSONObject = optJSONArray.optJSONObject(i);
                                if (optJSONObject != null) {
                                    arrayList.add(new zze(optJSONObject, null));
                                }
                            }
                        }
                        this.zzd.zza();
                        return;
                    } catch (JSONException unused) {
                        zzb.zzo("BillingBroadcastManager", String.format("Error when parsing invalid alternative choice data: [%s]", string));
                        this.zzb.onPurchasesUpdated(zzbb.zzj, com.google.android.gms.internal.play_billing.zzu.zzl());
                        return;
                    }
                }
                zzb.zzo("BillingBroadcastManager", "Couldn't find alternative billing user choice data in bundle.");
                this.zzb.onPurchasesUpdated(zzbb.zzj, com.google.android.gms.internal.play_billing.zzu.zzl());
            }
        }
    }

    public final void zzc(Context context, IntentFilter intentFilter) {
        zzn zznVar;
        if (!this.zze) {
            zznVar = this.zza.zzb;
            context.registerReceiver(zznVar, intentFilter);
            this.zze = true;
        }
    }

    public /* synthetic */ zzn(zzo zzoVar, PurchasesUpdatedListener purchasesUpdatedListener, zzc zzcVar, zzm zzmVar) {
        this.zza = zzoVar;
        this.zzb = purchasesUpdatedListener;
        this.zzd = zzcVar;
        this.zzc = null;
    }
}
