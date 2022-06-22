package com.google.android.gms.internal.icing;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.BaseImplementation$ResultHolder;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes.dex */
public final class zzak extends zzad<Status> {
    public zzak(BaseImplementation$ResultHolder<Status> baseImplementation$ResultHolder) {
        super(baseImplementation$ResultHolder);
    }

    @Override // com.google.android.gms.internal.icing.zzac
    public final void zzb(Status status) {
        this.zza.setResult(status);
    }
}
