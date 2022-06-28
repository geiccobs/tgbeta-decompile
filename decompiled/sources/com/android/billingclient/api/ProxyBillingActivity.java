package com.android.billingclient.api;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.ResultReceiver;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: classes3.dex */
public class ProxyBillingActivity extends Activity {
    static final String KEY_IN_APP_MESSAGE_RESULT_RECEIVER = "in_app_message_result_receiver";
    static final String KEY_PRICE_CHANGE_RESULT_RECEIVER = "result_receiver";
    private static final String KEY_SEND_CANCELLED_BROADCAST_IF_FINISHED = "send_cancelled_broadcast_if_finished";
    private static final int REQUEST_CODE_IN_APP_MESSAGE_FLOW = 101;
    private static final int REQUEST_CODE_LAUNCH_ACTIVITY = 100;
    private static final String TAG = "ProxyBillingActivity";
    private ResultReceiver inAppMessageResultReceiver;
    private ResultReceiver priceChangeResultReceiver;
    private boolean sendCancelledBroadcastIfFinished;

    private Intent makeAlternativeBillingIntent(String userChoicedata) {
        Intent intent = new Intent("com.android.vending.billing.ALTERNATIVE_BILLING");
        intent.setPackage(getApplicationContext().getPackageName());
        intent.putExtra("ALTERNATIVE_BILLING_USER_CHOICE_DATA", userChoicedata);
        return intent;
    }

    private Intent makePurchasesUpdatedIntent() {
        Intent intent = new Intent("com.android.vending.billing.PURCHASES_UPDATED");
        intent.setPackage(getApplicationContext().getPackageName());
        return intent;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x003e  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x004a  */
    @Override // android.app.Activity
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onActivityResult(int r6, int r7, android.content.Intent r8) {
        /*
            r5 = this;
            super.onActivityResult(r6, r7, r8)
            r0 = 0
            r1 = 0
            java.lang.String r2 = "ProxyBillingActivity"
            r3 = 100
            if (r6 != r3) goto L8e
            com.android.billingclient.api.BillingResult r6 = com.google.android.gms.internal.play_billing.zzb.zzi(r8, r2)
            int r6 = r6.getResponseCode()
            r3 = -1
            if (r7 != r3) goto L1d
            if (r6 == 0) goto L1b
            r7 = -1
            goto L1e
        L1b:
            r6 = 0
            goto L3a
        L1d:
        L1e:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Activity finished with resultCode "
            r3.append(r4)
            r3.append(r7)
            java.lang.String r7 = " and billing's responseCode: "
            r3.append(r7)
            r3.append(r6)
            java.lang.String r7 = r3.toString()
            com.google.android.gms.internal.play_billing.zzb.zzo(r2, r7)
        L3a:
            android.os.ResultReceiver r7 = r5.priceChangeResultReceiver
            if (r7 == 0) goto L4a
            if (r8 != 0) goto L41
            goto L45
        L41:
            android.os.Bundle r0 = r8.getExtras()
        L45:
            r7.send(r6, r0)
            goto Lbe
        L4a:
            if (r8 == 0) goto L86
            android.os.Bundle r6 = r8.getExtras()
            if (r6 == 0) goto L6f
            android.os.Bundle r6 = r8.getExtras()
            java.lang.String r7 = "ALTERNATIVE_BILLING_USER_CHOICE_DATA"
            java.lang.String r6 = r6.getString(r7)
            if (r6 == 0) goto L63
            android.content.Intent r6 = r5.makeAlternativeBillingIntent(r6)
            goto L8a
        L63:
            android.content.Intent r6 = r5.makePurchasesUpdatedIntent()
            android.os.Bundle r7 = r8.getExtras()
            r6.putExtras(r7)
            goto L8a
        L6f:
            android.content.Intent r6 = r5.makePurchasesUpdatedIntent()
            java.lang.String r7 = "Got null bundle!"
            com.google.android.gms.internal.play_billing.zzb.zzo(r2, r7)
            r7 = 6
            java.lang.String r8 = "RESPONSE_CODE"
            r6.putExtra(r8, r7)
            java.lang.String r7 = "DEBUG_MESSAGE"
            java.lang.String r8 = "An internal error occurred."
            r6.putExtra(r7, r8)
            goto L8a
        L86:
            android.content.Intent r6 = r5.makePurchasesUpdatedIntent()
        L8a:
            r5.sendBroadcast(r6)
            goto Lbe
        L8e:
            r7 = 101(0x65, float:1.42E-43)
            if (r6 != r7) goto La5
            int r6 = com.google.android.gms.internal.play_billing.zzb.zza(r8, r2)
            android.os.ResultReceiver r7 = r5.inAppMessageResultReceiver
            if (r7 == 0) goto Lbe
            if (r8 != 0) goto L9d
            goto La1
        L9d:
            android.os.Bundle r0 = r8.getExtras()
        La1:
            r7.send(r6, r0)
            goto Lbe
        La5:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Got onActivityResult with wrong requestCode: "
            r7.append(r8)
            r7.append(r6)
            java.lang.String r6 = "; skipping..."
            r7.append(r6)
            java.lang.String r6 = r7.toString()
            com.google.android.gms.internal.play_billing.zzb.zzo(r2, r6)
        Lbe:
            r5.sendCancelledBroadcastIfFinished = r1
            r5.finish()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.billingclient.api.ProxyBillingActivity.onActivityResult(int, int, android.content.Intent):void");
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        PendingIntent pendingIntent;
        int i;
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            com.google.android.gms.internal.play_billing.zzb.zzn(TAG, "Launching Play Store billing flow");
            if (getIntent().hasExtra("BUY_INTENT")) {
                pendingIntent = (PendingIntent) getIntent().getParcelableExtra("BUY_INTENT");
                i = 100;
            } else if (getIntent().hasExtra("SUBS_MANAGEMENT_INTENT")) {
                pendingIntent = (PendingIntent) getIntent().getParcelableExtra("SUBS_MANAGEMENT_INTENT");
                this.priceChangeResultReceiver = (ResultReceiver) getIntent().getParcelableExtra(KEY_PRICE_CHANGE_RESULT_RECEIVER);
                i = 100;
            } else if (getIntent().hasExtra("IN_APP_MESSAGE_INTENT")) {
                pendingIntent = (PendingIntent) getIntent().getParcelableExtra("IN_APP_MESSAGE_INTENT");
                this.inAppMessageResultReceiver = (ResultReceiver) getIntent().getParcelableExtra(KEY_IN_APP_MESSAGE_RESULT_RECEIVER);
                i = 101;
            } else {
                pendingIntent = null;
                i = 100;
            }
            try {
                this.sendCancelledBroadcastIfFinished = true;
                startIntentSenderForResult(pendingIntent.getIntentSender(), i, new Intent(), 0, 0, 0);
                return;
            } catch (IntentSender.SendIntentException e) {
                com.google.android.gms.internal.play_billing.zzb.zzp(TAG, "Got exception while trying to start a purchase flow.", e);
                ResultReceiver resultReceiver = this.priceChangeResultReceiver;
                if (resultReceiver != null) {
                    resultReceiver.send(6, null);
                } else {
                    ResultReceiver resultReceiver2 = this.inAppMessageResultReceiver;
                    if (resultReceiver2 != null) {
                        resultReceiver2.send(0, null);
                    } else {
                        Intent makePurchasesUpdatedIntent = makePurchasesUpdatedIntent();
                        makePurchasesUpdatedIntent.putExtra("RESPONSE_CODE", 6);
                        makePurchasesUpdatedIntent.putExtra("DEBUG_MESSAGE", "An internal error occurred.");
                        sendBroadcast(makePurchasesUpdatedIntent);
                    }
                }
                this.sendCancelledBroadcastIfFinished = false;
                finish();
                return;
            }
        }
        com.google.android.gms.internal.play_billing.zzb.zzn(TAG, "Launching Play Store billing flow from savedInstanceState");
        this.sendCancelledBroadcastIfFinished = savedInstanceState.getBoolean(KEY_SEND_CANCELLED_BROADCAST_IF_FINISHED, false);
        if (savedInstanceState.containsKey(KEY_PRICE_CHANGE_RESULT_RECEIVER)) {
            this.priceChangeResultReceiver = (ResultReceiver) savedInstanceState.getParcelable(KEY_PRICE_CHANGE_RESULT_RECEIVER);
        } else if (!savedInstanceState.containsKey(KEY_IN_APP_MESSAGE_RESULT_RECEIVER)) {
        } else {
            this.inAppMessageResultReceiver = (ResultReceiver) savedInstanceState.getParcelable(KEY_IN_APP_MESSAGE_RESULT_RECEIVER);
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing() && this.sendCancelledBroadcastIfFinished) {
            Intent makePurchasesUpdatedIntent = makePurchasesUpdatedIntent();
            makePurchasesUpdatedIntent.putExtra("RESPONSE_CODE", 1);
            makePurchasesUpdatedIntent.putExtra("DEBUG_MESSAGE", "Billing dialog closed.");
            sendBroadcast(makePurchasesUpdatedIntent);
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        ResultReceiver resultReceiver = this.priceChangeResultReceiver;
        if (resultReceiver != null) {
            outState.putParcelable(KEY_PRICE_CHANGE_RESULT_RECEIVER, resultReceiver);
        }
        ResultReceiver resultReceiver2 = this.inAppMessageResultReceiver;
        if (resultReceiver2 != null) {
            outState.putParcelable(KEY_IN_APP_MESSAGE_RESULT_RECEIVER, resultReceiver2);
        }
        outState.putBoolean(KEY_SEND_CANCELLED_BROADCAST_IF_FINISHED, this.sendCancelledBroadcastIfFinished);
    }
}
