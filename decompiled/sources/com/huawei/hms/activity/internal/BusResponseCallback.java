package com.huawei.hms.activity.internal;

import android.app.Activity;
import android.content.Intent;
/* loaded from: classes.dex */
public interface BusResponseCallback {
    BusResponseResult innerError(Activity activity, int i, String str);

    BusResponseResult succeedReturn(Activity activity, int i, Intent intent);
}
