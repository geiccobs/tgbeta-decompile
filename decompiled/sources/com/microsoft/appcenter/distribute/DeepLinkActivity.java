package com.microsoft.appcenter.distribute;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.microsoft.appcenter.utils.AppCenterLog;
/* loaded from: classes3.dex */
public class DeepLinkActivity extends Activity {
    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        Intent launchIntentForPackage;
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String requestId = intent.getStringExtra("request_id");
        String distributionGroupId = intent.getStringExtra("distribution_group_id");
        String updateToken = intent.getStringExtra("update_token");
        String updateSetupFailed = intent.getStringExtra("update_setup_failed");
        String testerAppUpdateSetupFailed = intent.getStringExtra("tester_app_update_setup_failed");
        AppCenterLog.debug(DistributeConstants.LOG_TAG, getLocalClassName() + ".getIntent()=" + intent);
        StringBuilder sb = new StringBuilder();
        sb.append("Intent requestId=");
        sb.append(requestId);
        AppCenterLog.debug(DistributeConstants.LOG_TAG, sb.toString());
        AppCenterLog.debug(DistributeConstants.LOG_TAG, "Intent distributionGroupId=" + distributionGroupId);
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Intent updateToken passed=");
        boolean z = true;
        sb2.append(updateToken != null);
        AppCenterLog.debug(DistributeConstants.LOG_TAG, sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Intent updateSetupFailed passed=");
        sb3.append(updateSetupFailed != null);
        AppCenterLog.debug(DistributeConstants.LOG_TAG, sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("Intent testerAppUpdateSetupFailed passed=");
        if (testerAppUpdateSetupFailed == null) {
            z = false;
        }
        sb4.append(z);
        AppCenterLog.debug(DistributeConstants.LOG_TAG, sb4.toString());
        if (requestId != null && distributionGroupId != null) {
            Distribute.getInstance().storeRedirectionParameters(requestId, distributionGroupId, updateToken);
        } else if (requestId != null && updateSetupFailed != null) {
            Distribute.getInstance().storeUpdateSetupFailedParameter(requestId, updateSetupFailed);
        }
        if (requestId != null && testerAppUpdateSetupFailed != null) {
            Distribute.getInstance().storeTesterAppUpdateSetupFailedParameter(requestId, testerAppUpdateSetupFailed);
        }
        finish();
        if ((getIntent().getFlags() & 268435456) != 268435456) {
            AppCenterLog.debug(DistributeConstants.LOG_TAG, "Using restart work around to correctly resume app.");
            startActivity(intent.cloneFilter().addFlags(268435456));
        } else if (isTaskRoot() && (launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(getPackageName())) != null) {
            startActivity(launchIntentForPackage);
        }
    }
}
