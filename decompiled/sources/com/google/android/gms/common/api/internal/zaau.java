package com.google.android.gms.common.api.internal;

import android.os.Looper;
import android.os.Message;
import android.util.Log;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zaau extends com.google.android.gms.internal.base.zas {
    private final /* synthetic */ zaar zaa;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zaau(zaar zaarVar, Looper looper) {
        super(looper);
        this.zaa = zaarVar;
    }

    @Override // android.os.Handler
    public final void handleMessage(Message message) {
        int i = message.what;
        if (i == 1) {
            this.zaa.zaf();
        } else if (i != 2) {
            StringBuilder sb = new StringBuilder(31);
            sb.append("Unknown message id: ");
            sb.append(i);
            Log.w("GoogleApiClientImpl", sb.toString());
        } else {
            this.zaa.zae();
        }
    }
}
