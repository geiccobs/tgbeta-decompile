package com.google.android.gms.common.api.internal;

import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zacp extends com.google.android.gms.internal.base.zas {
    private final /* synthetic */ zacn zaa;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zacp(zacn zacnVar, Looper looper) {
        super(looper);
        this.zaa = zacnVar;
    }

    @Override // android.os.Handler
    public final void handleMessage(Message message) {
        Object obj;
        zacn zacnVar;
        switch (message.what) {
            case 0:
                PendingResult<?> pendingResult = (PendingResult) message.obj;
                obj = this.zaa.zae;
                synchronized (obj) {
                    zacnVar = this.zaa.zab;
                    zacn zacnVar2 = (zacn) Preconditions.checkNotNull(zacnVar);
                    if (pendingResult != null) {
                        if (!(pendingResult instanceof zacc)) {
                            zacnVar2.zaa(pendingResult);
                        } else {
                            zacnVar2.zaa(((zacc) pendingResult).zaa());
                        }
                    } else {
                        zacnVar2.zaa(new Status(13, "Transform returned null"));
                    }
                }
                return;
            case 1:
                RuntimeException runtimeException = (RuntimeException) message.obj;
                String valueOf = String.valueOf(runtimeException.getMessage());
                Log.e("TransformedResultImpl", valueOf.length() != 0 ? "Runtime exception on the transformation worker thread: ".concat(valueOf) : new String("Runtime exception on the transformation worker thread: "));
                throw runtimeException;
            default:
                int i = message.what;
                StringBuilder sb = new StringBuilder(70);
                sb.append("TransformationResultHandler received unknown message type: ");
                sb.append(i);
                Log.e("TransformedResultImpl", sb.toString());
                return;
        }
    }
}
