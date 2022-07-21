package com.huawei.hms.support.api.push;

import android.app.Activity;
import android.os.Bundle;
import com.huawei.android.hms.push.R$layout;
import com.huawei.hms.push.i;
import org.telegram.tgnet.ConnectionsManager;
/* loaded from: classes.dex */
public class TransActivity extends Activity {
    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R$layout.hwpush_trans_activity);
        getWindow().addFlags(ConnectionsManager.FileTypeFile);
        i.a(this, getIntent());
        finish();
    }
}
