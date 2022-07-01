package com.google.android.gms.internal.mlkit_language_id;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public abstract class zzdz {
    public static zzdz zza(byte[] bArr, int i, int i2, boolean z) {
        zzeb zzebVar = new zzeb(bArr, i2);
        try {
            zzebVar.zza(i2);
            return zzebVar;
        } catch (zzez e) {
            throw new IllegalArgumentException(e);
        }
    }

    public abstract int zza();

    public abstract int zza(int i) throws zzez;

    /* JADX INFO: Access modifiers changed from: private */
    public zzdz() {
    }
}
