package com.huawei.hmf.tasks;

import com.huawei.hmf.tasks.a.i;
/* loaded from: classes.dex */
public class TaskCompletionSource<TResult> {
    private final i<TResult> task = new i<>();

    public Task<TResult> getTask() {
        return this.task;
    }

    public void setException(Exception exc) {
        this.task.a(exc);
    }

    public void setResult(TResult tresult) {
        this.task.a((i<TResult>) tresult);
    }
}
