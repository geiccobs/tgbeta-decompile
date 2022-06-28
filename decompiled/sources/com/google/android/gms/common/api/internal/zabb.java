package com.google.android.gms.common.api.internal;

import android.os.Looper;
import android.os.Message;
import android.util.Log;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zabb extends com.google.android.gms.internal.base.zas {
    private final /* synthetic */ zaaz zaa;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zabb(zaaz zaazVar, Looper looper) {
        super(looper);
        this.zaa = zaazVar;
    }

    @Override // android.os.Handler
    public final void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                ((zaay) message.obj).zaa(this.zaa);
                return;
            case 2:
                throw ((RuntimeException) message.obj);
            default:
                int i = message.what;
                StringBuilder sb = new StringBuilder(31);
                sb.append("Unknown message id: ");
                sb.append(i);
                Log.w("GACStateManager", sb.toString());
                return;
        }
    }
}
