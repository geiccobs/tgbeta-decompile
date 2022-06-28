package com.google.android.gms.internal.icing;

import com.google.android.gms.internal.icing.zzbr;
import com.google.android.gms.internal.icing.zzbs;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public abstract class zzbr<MessageType extends zzbs<MessageType, BuilderType>, BuilderType extends zzbr<MessageType, BuilderType>> implements zzed {
    /* renamed from: zzd */
    public abstract BuilderType clone();

    protected abstract BuilderType zze(MessageType messagetype);

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.internal.icing.zzed
    public final /* bridge */ /* synthetic */ zzed zzf(zzee zzeeVar) {
        if (!zzm().getClass().isInstance(zzeeVar)) {
            throw new IllegalArgumentException("mergeFrom(MessageLite) can only merge messages of the same type.");
        }
        return zze((zzbs) zzeeVar);
    }
}
