package com.huawei.hmf.tasks;

import com.huawei.hmf.tasks.a.j;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
/* loaded from: classes.dex */
public class Tasks {
    private static j IMPL = new j();

    public static <TResult> TResult await(Task<TResult> task) throws ExecutionException, InterruptedException {
        j.a("await must not be called on the UI thread");
        if (task.isComplete()) {
            return (TResult) j.a(task);
        }
        j.a aVar = new j.a();
        task.addOnSuccessListener(aVar).addOnFailureListener(aVar);
        aVar.a.await();
        return (TResult) j.a(task);
    }

    public static <TResult> Task<TResult> callInBackground(Callable<TResult> callable) {
        return IMPL.a(TaskExecutors.background(), callable);
    }
}
