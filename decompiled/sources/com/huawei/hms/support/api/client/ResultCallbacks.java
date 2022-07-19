package com.huawei.hms.support.api.client;

import com.huawei.hms.common.api.Releasable;
import com.huawei.hms.support.api.client.Result;
import com.huawei.hms.support.log.HMSLog;
@Deprecated
/* loaded from: classes.dex */
public abstract class ResultCallbacks<R extends Result> implements ResultCallback<R> {
    private static final String TAG = "ResultCallbacks";

    public abstract void onFailure(Status status);

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.huawei.hms.support.api.client.ResultCallback
    public /* bridge */ /* synthetic */ void onResult(Object obj) {
        onResult((ResultCallbacks<R>) ((Result) obj));
    }

    public abstract void onSuccess(R r);

    public final void onResult(R r) {
        try {
            Status status = r.getStatus();
            if (status.isSuccess()) {
                onSuccess(r);
            } else {
                onFailure(status);
                if (r instanceof Releasable) {
                    ((Releasable) r).release();
                }
            }
        } catch (Exception e) {
            HMSLog.w(TAG, "Failed to release " + r + ", reason: " + e);
        }
    }
}
