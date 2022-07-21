package com.huawei.hms.common.internal;

import com.huawei.hms.core.aidl.IMessageEntity;
/* loaded from: classes.dex */
public interface AnyClient {

    /* loaded from: classes.dex */
    public interface CallBack {
        void onCallback(IMessageEntity iMessageEntity, String str);
    }

    void connect(int i);

    void connect(int i, boolean z);

    void disconnect();

    String getSessionId();

    boolean isConnected();

    boolean isConnecting();

    void post(IMessageEntity iMessageEntity, String str, CallBack callBack);
}
