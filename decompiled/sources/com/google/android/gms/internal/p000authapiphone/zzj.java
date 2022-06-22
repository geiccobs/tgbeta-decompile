package com.google.android.gms.internal.p000authapiphone;

import android.content.Context;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
/* renamed from: com.google.android.gms.internal.auth-api-phone.zzj */
/* loaded from: classes.dex */
public final class zzj extends SmsRetrieverClient {
    public zzj(Context context) {
        super(context);
    }

    @Override // com.google.android.gms.auth.api.phone.SmsRetrieverClient
    public final Task<Void> startSmsRetriever() {
        return doWrite(new zzk(this));
    }
}
