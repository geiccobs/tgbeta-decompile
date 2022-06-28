package com.google.android.gms.common;

import java.util.concurrent.Callable;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
final class zzt extends zzs {
    private final Callable<String> zzb;

    /* JADX INFO: Access modifiers changed from: private */
    public zzt(Callable<String> callable) {
        super(false, null, null);
        this.zzb = callable;
    }

    @Override // com.google.android.gms.common.zzs
    final String zzb() {
        try {
            return this.zzb.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
