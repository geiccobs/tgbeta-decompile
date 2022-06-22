package com.google.android.gms.tasks;
/* compiled from: com.google.android.gms:play-services-tasks@@17.2.0 */
/* loaded from: classes.dex */
public class CancellationTokenSource {
    private final zza zza = new zza();

    public CancellationToken getToken() {
        return this.zza;
    }

    public void cancel() {
        this.zza.zza();
    }
}
