package com.google.android.gms.internal.vision;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
abstract class zzme {
    abstract int zza(int i, byte[] bArr, int i2, int i3);

    public abstract int zza(CharSequence charSequence, byte[] bArr, int i, int i2);

    public abstract String zzb(byte[] bArr, int i, int i2) throws zzjk;

    public final boolean zza(byte[] bArr, int i, int i2) {
        return zza(0, bArr, i, i2) == 0;
    }
}
