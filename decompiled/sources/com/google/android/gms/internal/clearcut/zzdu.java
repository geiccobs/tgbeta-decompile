package com.google.android.gms.internal.clearcut;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
/* loaded from: classes3.dex */
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

    /* JADX WARN: Removed duplicated region for block: B:23:0x005a  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0061  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x005f A[SYNTHETIC] */
    @Override // com.google.android.gms.internal.clearcut.zzef
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void zza(T r7, byte[] r8, int r9, int r10, com.google.android.gms.internal.clearcut.zzay r11) throws java.io.IOException {
        /*
            r6 = this;
            com.google.android.gms.internal.clearcut.zzcg r7 = (com.google.android.gms.internal.clearcut.zzcg) r7
            com.google.android.gms.internal.clearcut.zzey r0 = r7.zzjp
            com.google.android.gms.internal.clearcut.zzey r1 = com.google.android.gms.internal.clearcut.zzey.zzea()
            if (r0 != r1) goto L10
            com.google.android.gms.internal.clearcut.zzey r0 = com.google.android.gms.internal.clearcut.zzey.zzeb()
            r7.zzjp = r0
        L10:
            r7 = r0
        L11:
            if (r9 >= r10) goto L69
            int r2 = com.google.android.gms.internal.clearcut.zzax.zza(r8, r9, r11)
            int r0 = r11.zzfd
            r9 = 11
            r1 = 2
            if (r0 == r9) goto L30
            r9 = r0 & 7
            if (r9 != r1) goto L2b
            r1 = r8
            r3 = r10
            r4 = r7
            r5 = r11
            int r9 = com.google.android.gms.internal.clearcut.zzax.zza(r0, r1, r2, r3, r4, r5)
            goto L11
        L2b:
            int r9 = com.google.android.gms.internal.clearcut.zzax.zza(r0, r8, r2, r10, r11)
            goto L11
        L30:
            r9 = 0
            r0 = 0
        L32:
            if (r2 >= r10) goto L5f
            int r2 = com.google.android.gms.internal.clearcut.zzax.zza(r8, r2, r11)
            int r3 = r11.zzfd
            int r4 = r3 >>> 3
            r5 = r3 & 7
            switch(r4) {
                case 2: goto L4d;
                case 3: goto L42;
                default: goto L41;
            }
        L41:
            goto L56
        L42:
            if (r5 != r1) goto L56
            int r2 = com.google.android.gms.internal.clearcut.zzax.zze(r8, r2, r11)
            java.lang.Object r0 = r11.zzff
            com.google.android.gms.internal.clearcut.zzbb r0 = (com.google.android.gms.internal.clearcut.zzbb) r0
            goto L32
        L4d:
            if (r5 != 0) goto L56
            int r2 = com.google.android.gms.internal.clearcut.zzax.zza(r8, r2, r11)
            int r9 = r11.zzfd
            goto L32
        L56:
            r4 = 12
            if (r3 == r4) goto L5f
            int r2 = com.google.android.gms.internal.clearcut.zzax.zza(r3, r8, r2, r10, r11)
            goto L32
        L5f:
            if (r0 == 0) goto L67
            int r9 = r9 << 3
            r9 = r9 | r1
            r7.zzb(r9, r0)
        L67:
            r9 = r2
            goto L11
        L69:
            if (r9 != r10) goto L6c
            return
        L6c:
            com.google.android.gms.internal.clearcut.zzco r7 = com.google.android.gms.internal.clearcut.zzco.zzbo()
            goto L72
        L71:
            throw r7
        L72:
            goto L71
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
