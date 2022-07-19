package com.huawei.hms.api;

import android.os.RemoteException;
import android.text.TextUtils;
import com.huawei.hms.core.aidl.CodecLookup;
import com.huawei.hms.core.aidl.DataBuffer;
import com.huawei.hms.core.aidl.IAIDLCallback;
import com.huawei.hms.core.aidl.IMessageEntity;
import com.huawei.hms.core.aidl.MessageCodec;
import com.huawei.hms.core.aidl.ResponseHeader;
import com.huawei.hms.support.api.transport.DatagramTransport;
import com.huawei.hms.support.log.HMSLog;
/* loaded from: classes.dex */
public class IPCCallback extends IAIDLCallback.Stub {
    private static final String TAG = "IPCCallback";
    private final DatagramTransport.a mCallback;
    private final Class<? extends IMessageEntity> mResponseClass;

    public IPCCallback(Class<? extends IMessageEntity> cls, DatagramTransport.a aVar) {
        this.mResponseClass = cls;
        this.mCallback = aVar;
    }

    @Override // com.huawei.hms.core.aidl.IAIDLCallback
    public void call(DataBuffer dataBuffer) throws RemoteException {
        if (dataBuffer != null && !TextUtils.isEmpty(dataBuffer.URI)) {
            MessageCodec find = CodecLookup.find(dataBuffer.getProtocol());
            IMessageEntity iMessageEntity = null;
            if (dataBuffer.getBodySize() > 0 && (iMessageEntity = newResponseInstance()) != null) {
                find.decode(dataBuffer.getBody(), iMessageEntity);
            }
            if (dataBuffer.header != null) {
                ResponseHeader responseHeader = new ResponseHeader();
                find.decode(dataBuffer.header, responseHeader);
                this.mCallback.a(responseHeader.getStatusCode(), iMessageEntity);
                return;
            }
            this.mCallback.a(0, iMessageEntity);
            return;
        }
        HMSLog.e(TAG, "In call, URI cannot be empty.");
        throw new RemoteException();
    }

    protected IMessageEntity newResponseInstance() {
        Class<? extends IMessageEntity> cls = this.mResponseClass;
        if (cls != null) {
            try {
                return cls.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                HMSLog.e(TAG, "In newResponseInstance, instancing exception." + e.getMessage());
                return null;
            }
        }
        return null;
    }
}
