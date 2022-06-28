package com.google.android.gms.internal.clearcut;

import com.google.android.gms.internal.clearcut.zzas;
import com.google.android.gms.internal.clearcut.zzat;
/* loaded from: classes3.dex */
public abstract class zzat<MessageType extends zzas<MessageType, BuilderType>, BuilderType extends zzat<MessageType, BuilderType>> implements zzdp {
    protected abstract BuilderType zza(MessageType messagetype);

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.internal.clearcut.zzdp
    public final /* synthetic */ zzdp zza(zzdo zzdoVar) {
        if (zzbe().getClass().isInstance(zzdoVar)) {
            return zza((zzat<MessageType, BuilderType>) ((zzas) zzdoVar));
        }
        throw new IllegalArgumentException("mergeFrom(MessageLite) can only merge messages of the same type.");
    }

    /* renamed from: zzt */
    public abstract BuilderType clone();
}
