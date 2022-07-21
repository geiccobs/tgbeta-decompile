package com.huawei.hms.opendevice;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.api.opendevice.OdidResult;
/* loaded from: classes.dex */
public interface OpenDeviceClient {
    Task<OdidResult> getOdid();
}
