package com.huawei.hms.adapter.sysobs;

import android.content.Intent;
/* loaded from: classes.dex */
public interface SystemObserver {
    boolean onNoticeResult(int i);

    boolean onSolutionResult(Intent intent, String str);

    boolean onUpdateResult(int i);
}
