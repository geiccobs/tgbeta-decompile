package com.google.android.gms.internal.mlkit_common;

import com.google.android.gms.internal.mlkit_common.zzeg;
import com.google.android.gms.internal.mlkit_common.zzej;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public abstract class zzej<MessageType extends zzeg<MessageType, BuilderType>, BuilderType extends zzej<MessageType, BuilderType>> implements zzha {
    protected abstract BuilderType zza(MessageType messagetype);

    /* renamed from: zzb */
    public abstract BuilderType clone();

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.internal.mlkit_common.zzha
    public final /* synthetic */ zzha zza(zzhb zzhbVar) {
        if (!zzn().getClass().isInstance(zzhbVar)) {
            throw new IllegalArgumentException("mergeFrom(MessageLite) can only merge messages of the same type.");
        }
        return zza((zzej<MessageType, BuilderType>) ((zzeg) zzhbVar));
    }
}
