package com.huawei.hms.api;

import com.huawei.hms.support.log.HMSLog;
import j$.util.concurrent.ConcurrentHashMap;
import java.sql.Timestamp;
import java.util.Map;
/* loaded from: classes.dex */
public class FailedBinderCallBack {
    private static final long AGING_TIME = 10000;
    public static final String CALLER_ID = "callId";
    private static final String TAG = "FailedBinderCallBack";
    private static FailedBinderCallBack instance;
    private static Map<Long, BinderCallBack> binderCallBackMap = new ConcurrentHashMap();
    private static final Object LOCK_OBJECT = new Object();

    /* loaded from: classes.dex */
    public interface BinderCallBack {
        void binderCallBack(int i);
    }

    private FailedBinderCallBack() {
    }

    private void agingCheck() {
        long time = new Timestamp(System.currentTimeMillis()).getTime() - AGING_TIME;
        for (Long l : binderCallBackMap.keySet()) {
            if (time >= l.longValue()) {
                binderCallBackMap.remove(l);
            }
        }
    }

    public static FailedBinderCallBack getInstance() {
        synchronized (LOCK_OBJECT) {
            if (instance == null) {
                instance = new FailedBinderCallBack();
            }
        }
        return instance;
    }

    private void putCallBackInMap(Long l, BinderCallBack binderCallBack) {
        if (binderCallBackMap == null) {
            HMSLog.e(TAG, "binderCallBackMap is null");
            return;
        }
        agingCheck();
        binderCallBackMap.put(l, binderCallBack);
    }

    public BinderCallBack getCallBack(Long l) {
        Map<Long, BinderCallBack> map = binderCallBackMap;
        if (map == null) {
            HMSLog.e(TAG, "binderCallBackMap is null");
            return null;
        }
        return map.remove(l);
    }

    public void setCallBack(Long l, BinderCallBack binderCallBack) {
        putCallBackInMap(l, binderCallBack);
    }
}
