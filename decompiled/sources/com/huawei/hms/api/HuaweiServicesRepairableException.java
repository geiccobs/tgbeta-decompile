package com.huawei.hms.api;

import android.content.Intent;
/* loaded from: classes.dex */
public class HuaweiServicesRepairableException extends UserRecoverableException {
    private final int statusCode;

    public HuaweiServicesRepairableException(int i, String str, Intent intent) {
        super(str, intent);
        this.statusCode = i;
    }

    public int getConnectionStatusCode() {
        return this.statusCode;
    }
}
