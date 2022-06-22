package com.google.android.gms.internal.clearcut;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
/* loaded from: classes.dex */
final class zzdu<T> implements zzef<T> {
    private final zzdo zzmn;
    private final boolean zzmo;
    private final zzex<?, ?> zzmx;
    private final zzbu<?> zzmy;

    private zzdu(zzex<?, ?> zzexVar, zzbu<?> zzbuVar, zzdo zzdoVar) {
        this.zzmx = zzexVar;
        this.zzmo = zzbuVar.zze(zzdoVar);
        this.zzmy = zzbuVar;
        this.zzmn = zzdoVar;
    }

    public static <T> zzdu<T> zza(zzex<?, ?> zzexVar, zzbu<?> zzbuVar, zzdo zzdoVar) {
        return new zzdu<>(zzexVar, zzbuVar, zzdoVar);
    }

    @Override // com.google.android.gms.internal.clearcut.zzef
    public final boolean equals(T t, T t2) {
        if (!this.zzmx.zzq(t).equals(this.zzmx.zzq(t2))) {
            return false;
        }
        if (!this.zzmo) {
            return true;
        }
        return this.zzmy.zza(t).equals(this.zzmy.zza(t2));
    }

    @Override // com.google.android.gms.internal.clearcut.zzef
    public final int hashCode(T t) {
        int hashCode = this.zzmx.zzq(t).hashCode();
        return this.zzmo ? (hashCode * 53) + this.zzmy.zza(t).hashCode() : hashCode;
    }

    @Override // com.google.android.gms.internal.clearcut.zzef
    public final T newInstance() {
        return (T) this.zzmn.zzbd().zzbi();
    }

    @Override // com.google.android.gms.internal.clearcut.zzef
    public final void zza(T t, zzfr zzfrVar) throws IOException {
        Iterator<Map.Entry<?, Object>> it = this.zzmy.zza(t).iterator();
        while (it.hasNext()) {
            Map.Entry<?, Object> next = it.next();
            zzca zzcaVar = (zzca) next.getKey();
            if (zzcaVar.zzav() != zzfq.MESSAGE || zzcaVar.zzaw() || zzcaVar.zzax()) {
                throw new IllegalStateException("Found invalid MessageSet item.");
            }
            zzfrVar.zza(zzcaVar.zzc(), next instanceof zzct ? ((zzct) next).zzbs().zzr() : next.getValue());
        }
        zzex<?, ?> zzexVar = this.zzmx;
        zzexVar.zzc(zzexVar.zzq(t), zzfrVar);
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x005c  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x0061 A[EDGE_INSN: B:50:0x0061->B:26:0x0061 ?: BREAK  , SYNTHETIC] */
    @Override // com.google.android.gms.internal.clearcut.zzef
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void zza(T r8, byte[] r9, int r10, int r11, com.google.android.gms.internal.clearcut.zzay r12) throws java.io.IOException {
        /*
            r7 = this;
            com.google.android.gms.internal.clearcut.zzcg r8 = (com.google.android.gms.internal.clearcut.zzcg) r8
            com.google.android.gms.internal.clearcut.zzey r0 = r8.zzjp
            com.google.android.gms.internal.clearcut.zzey r1 = com.google.android.gms.internal.clearcut.zzey.zzea()
            if (r0 != r1) goto L10
            com.google.android.gms.internal.clearcut.zzey r0 = com.google.android.gms.internal.clearcut.zzey.zzeb()
            r8.zzjp = r0
        L10:
            r8 = r0
        L11:
            if (r10 >= r11) goto L6b
            int r2 = com.google.android.gms.internal.clearcut.zzax.zza(r9, r10, r12)
            int r0 = r12.zzfd
            r10 = 11
            r1 = 2
            if (r0 == r10) goto L30
            r10 = r0 & 7
            if (r10 != r1) goto L2b
            r1 = r9
            r3 = r11
            r4 = r8
            r5 = r12
            int r10 = com.google.android.gms.internal.clearcut.zzax.zza(r0, r1, r2, r3, r4, r5)
            goto L11
        L2b:
            int r10 = com.google.android.gms.internal.clearcut.zzax.zza(r0, r9, r2, r11, r12)
            goto L11
        L30:
            r10 = 0
            r0 = 0
        L32:
            if (r2 >= r11) goto L61
            int r2 = com.google.android.gms.internal.clearcut.zzax.zza(r9, r2, r12)
            int r3 = r12.zzfd
            int r4 = r3 >>> 3
            r5 = r3 & 7
            if (r4 == r1) goto L4f
            r6 = 3
            if (r4 == r6) goto L44
            goto L58
        L44:
            if (r5 != r1) goto L58
            int r2 = com.google.android.gms.internal.clearcut.zzax.zze(r9, r2, r12)
            java.lang.Object r0 = r12.zzff
            com.google.android.gms.internal.clearcut.zzbb r0 = (com.google.android.gms.internal.clearcut.zzbb) r0
            goto L32
        L4f:
            if (r5 != 0) goto L58
            int r2 = com.google.android.gms.internal.clearcut.zzax.zza(r9, r2, r12)
            int r10 = r12.zzfd
            goto L32
        L58:
            r4 = 12
            if (r3 == r4) goto L61
            int r2 = com.google.android.gms.internal.clearcut.zzax.zza(r3, r9, r2, r11, r12)
            goto L32
        L61:
            if (r0 == 0) goto L69
            int r10 = r10 << 3
            r10 = r10 | r1
            r8.zzb(r10, r0)
        L69:
            r10 = r2
            goto L11
        L6b:
            if (r10 != r11) goto L6e
            return
        L6e:
            com.google.android.gms.internal.clearcut.zzco r8 = com.google.android.gms.internal.clearcut.zzco.zzbo()
            goto L74
        L73:
            throw r8
        L74:
            goto L73
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.clearcut.zzdu.zza(java.lang.Object, byte[], int, int, com.google.android.gms.internal.clearcut.zzay):void");
    }

    @Override // com.google.android.gms.internal.clearcut.zzef
    public final void zzc(T t) {
        this.zzmx.zzc(t);
        this.zzmy.zzc(t);
    }

    @Override // com.google.android.gms.internal.clearcut.zzef
    public final void zzc(T t, T t2) {
        zzeh.zza(this.zzmx, t, t2);
        if (this.zzmo) {
            zzeh.zza(this.zzmy, t, t2);
        }
    }

    @Override // com.google.android.gms.internal.clearcut.zzef
    public final int zzm(T t) {
        zzex<?, ?> zzexVar = this.zzmx;
        int zzr = zzexVar.zzr(zzexVar.zzq(t)) + 0;
        return this.zzmo ? zzr + this.zzmy.zza(t).zzat() : zzr;
    }

    @Override // com.google.android.gms.internal.clearcut.zzef
    public final boolean zzo(T t) {
        return this.zzmy.zza(t).isInitialized();
    }
}
