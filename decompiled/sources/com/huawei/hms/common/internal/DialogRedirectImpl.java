package com.huawei.hms.common.internal;

import android.app.Activity;
import android.content.Intent;
/* loaded from: classes.dex */
public class DialogRedirectImpl extends DialogRedirect {
    private final Activity activity;
    private final Intent intent;
    private final int requestCode;

    public DialogRedirectImpl(Intent intent, Activity activity, int i) {
        this.intent = intent;
        this.activity = activity;
        this.requestCode = i;
    }

    @Override // com.huawei.hms.common.internal.DialogRedirect
    public final void redirect() {
        Intent intent = this.intent;
        if (intent != null) {
            this.activity.startActivityForResult(intent, this.requestCode);
        }
    }
}
