package com.google.android.gms.auth.api.accounttransfer;

import com.google.android.gms.auth.api.accounttransfer.AccountTransferClient;
/* loaded from: classes3.dex */
final class zzf extends AccountTransferClient.zza<byte[]> {
    private final /* synthetic */ zze zzaq;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public zzf(zze zzeVar, AccountTransferClient.zzb zzbVar) {
        super(zzbVar);
        this.zzaq = zzeVar;
    }

    @Override // com.google.android.gms.internal.auth.zzs, com.google.android.gms.internal.auth.zzx
    public final void zza(byte[] bArr) {
        this.zzaq.setResult(bArr);
    }
}
