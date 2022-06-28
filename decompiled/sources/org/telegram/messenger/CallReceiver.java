package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import com.google.firebase.remoteconfig.RemoteConfigConstants;
import org.telegram.PhoneFormat.PhoneFormat;
/* loaded from: classes4.dex */
public class CallReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String phoneState = intent.getStringExtra(RemoteConfigConstants.ResponseFieldKey.STATE);
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(phoneState)) {
                String phoneNumber = intent.getStringExtra("incoming_number");
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReceiveCall, PhoneFormat.stripExceptNumbers(phoneNumber));
            }
        }
    }
}
