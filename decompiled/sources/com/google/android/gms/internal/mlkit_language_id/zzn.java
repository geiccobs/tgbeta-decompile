package com.google.android.gms.internal.mlkit_language_id;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public final class zzn<E> extends zzi<E> {
    private final zzk<E> zza;

    public zzn(zzk<E> zzkVar, int i) {
        super(zzkVar.size(), i);
        this.zza = zzkVar;
    }

    @Override // com.google.android.gms.internal.mlkit_language_id.zzi
    protected final E zza(int i) {
        return this.zza.get(i);
    }
}
