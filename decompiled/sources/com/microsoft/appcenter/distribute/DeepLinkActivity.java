package com.microsoft.appcenter.distribute;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.microsoft.appcenter.utils.AppCenterLog;
/* loaded from: classes.dex */
public class DeepLinkActivity extends Activity {
    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        Intent launchIntentForPackage;
        super.onCreate(bundle);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("request_id");
        String stringExtra2 = intent.getStringExtra("distribution_group_id");
        String stringExtra3 = intent.getStringExtra("update_token");
        String stringExtra4 = intent.getStringExtra("update_setup_failed");
        String stringExtra5 = intent.getStringExtra("tester_app_update_setup_failed");
        AppCenterLog.debug("AppCenterDistribute", getLocalClassName() + ".getIntent()=" + intent);
        StringBuilder sb = new StringBuilder();
        sb.append("Intent requestId=");
        sb.append(stringExtra);
        AppCenterLog.debug("AppCenterDistribute", sb.toString());
        AppCenterLog.debug("AppCenterDistribute", "Intent distributionGroupId=" + stringExtra2);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Intent updateToken passed=");
        boolean z = true;
        sb2.append(stringExtra3 != null);
        AppCenterLog.debug("AppCenterDistribute", sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Intent updateSetupFailed passed=");
        sb3.append(stringExtra4 != null);
        AppCenterLog.debug("AppCenterDistribute", sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("Intent testerAppUpdateSetupFailed passed=");
        if (stringExtra5 == null) {
            z = false;
        }
        sb4.append(z);
        AppCenterLog.debug("AppCenterDistribute", sb4.toString());
        if (stringExtra != null && stringExtra2 != null) {
            Distribute.getInstance().storeRedirectionParameters(stringExtra, stringExtra2, stringExtra3);
        } else if (stringExtra != null && stringExtra4 != null) {
            Distribute.getInstance().storeUpdateSetupFailedParameter(stringExtra, stringExtra4);
        }
        if (stringExtra != null && stringExtra5 != null) {
            Distribute.getInstance().storeTesterAppUpdateSetupFailedParameter(stringExtra, stringExtra5);
        }
        finish();
        if ((getIntent().getFlags() & 268435456) != 268435456) {
            AppCenterLog.debug("AppCenterDistribute", "Using restart work around to correctly resume app.");
            startActivity(intent.cloneFilter().addFlags(268435456));
        } else if (!isTaskRoot() || (launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(getPackageName())) == null) {
        } else {
            startActivity(launchIntentForPackage);
        }
    }
}
