package com.google.android.gms.internal.icing;

import com.google.android.gms.internal.icing.zzcx;
import com.google.android.gms.internal.icing.zzda;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public class zzcx<MessageType extends zzda<MessageType, BuilderType>, BuilderType extends zzcx<MessageType, BuilderType>> extends zzbr<MessageType, BuilderType> {
    protected MessageType zza;
    protected boolean zzb = false;
    private final MessageType zzc;

    public zzcx(MessageType messagetype) {
        this.zzc = messagetype;
        this.zza = (MessageType) messagetype.zzf(4, null, null);
    }

    private static final void zza(MessageType messagetype, MessageType messagetype2) {
        zzem.zza().zzb(messagetype.getClass()).zzc(messagetype, messagetype2);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.google.android.gms.internal.icing.zzbr
    protected final /* bridge */ /* synthetic */ zzbr zze(zzbs zzbsVar) {
        zzk((zzda) zzbsVar);
        return this;
    }

    public void zzg() {
        MessageType messagetype = (MessageType) this.zza.zzf(4, null, null);
        zza(messagetype, this.zza);
        this.zza = messagetype;
    }

    /* renamed from: zzh */
    public final BuilderType zzd() {
        BuilderType buildertype = (BuilderType) this.zzc.zzf(5, null, null);
        buildertype.zzk(zzl());
        return buildertype;
    }

    /* renamed from: zzi */
    public MessageType zzl() {
        if (this.zzb) {
            return this.zza;
        }
        MessageType messagetype = this.zza;
        zzem.zza().zzb(messagetype.getClass()).zze(messagetype);
        this.zzb = true;
        return this.zza;
    }

    public final MessageType zzj() {
        MessageType zzl = zzl();
        boolean booleanValue = Boolean.TRUE.booleanValue();
        boolean z = true;
        byte byteValue = ((Byte) zzl.zzf(1, null, null)).byteValue();
        if (byteValue != 1) {
            if (byteValue == 0) {
                z = false;
            } else {
                boolean zzf = zzem.zza().zzb(zzl.getClass()).zzf(zzl);
                if (booleanValue) {
                    zzl.zzf(2, true != zzf ? null : zzl, null);
                }
                z = zzf;
            }
        }
        if (z) {
            return zzl;
        }
        throw new zzfc(zzl);
    }

    public final BuilderType zzk(MessageType messagetype) {
        if (this.zzb) {
            zzg();
            this.zzb = false;
        }
        zza(this.zza, messagetype);
        return this;
    }

    @Override // com.google.android.gms.internal.icing.zzef
    public final /* bridge */ /* synthetic */ zzee zzm() {
        return this.zzc;
    }
}
