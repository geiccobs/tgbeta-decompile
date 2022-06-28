package com.microsoft.appcenter.utils;

import android.os.AsyncTask;
import java.util.concurrent.RejectedExecutionException;
/* loaded from: classes3.dex */
public class AsyncTaskUtils {
    AsyncTaskUtils() {
    }

    @SafeVarargs
    public static <Params, Type extends AsyncTask<Params, ?, ?>> Type execute(String logTag, Type asyncTask, Params... params) {
        try {
            return (Type) asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } catch (RejectedExecutionException e) {
            AppCenterLog.warn(logTag, "THREAD_POOL_EXECUTOR saturated, fall back on SERIAL_EXECUTOR which has an unbounded queue", e);
            return (Type) asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, params);
        }
    }
}
