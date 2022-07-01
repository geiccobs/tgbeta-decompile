package com.google.android.gms.internal.vision;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
final class zzkq<T> implements zzlc<T> {
    private final zzkk zza;
    private final zzlu<?, ?> zzb;
    private final boolean zzc;
    private final zziq<?> zzd;

    private zzkq(zzlu<?, ?> zzluVar, zziq<?> zziqVar, zzkk zzkkVar) {
        this.zzb = zzluVar;
        this.zzc = zziqVar.zza(zzkkVar);
        this.zzd = zziqVar;
        this.zza = zzkkVar;
    }

    public static <T> zzkq<T> zza(zzlu<?, ?> zzluVar, zziq<?> zziqVar, zzkk zzkkVar) {
        return new zzkq<>(zzluVar, zziqVar, zzkkVar);
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final T zza() {
        return (T) this.zza.zzq().zze();
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final boolean zza(T t, T t2) {
        if (!this.zzb.zzb(t).equals(this.zzb.zzb(t2))) {
            return false;
        }
        if (!this.zzc) {
            return true;
        }
        return this.zzd.zza(t).equals(this.zzd.zza(t2));
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final int zza(T t) {
        int hashCode = this.zzb.zzb(t).hashCode();
        return this.zzc ? (hashCode * 53) + this.zzd.zza(t).hashCode() : hashCode;
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final void zzb(T t, T t2) {
        zzle.zza(this.zzb, t, t2);
        if (this.zzc) {
            zzle.zza(this.zzd, t, t2);
        }
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final void zza(T t, zzmr zzmrVar) throws IOException {
        Iterator<Map.Entry<?, Object>> zzd = this.zzd.zza(t).zzd();
        while (zzd.hasNext()) {
            Map.Entry<?, Object> next = zzd.next();
            zziw zziwVar = (zziw) next.getKey();
            if (zziwVar.zzc() != zzmo.MESSAGE || zziwVar.zzd() || zziwVar.zze()) {
                throw new IllegalStateException("Found invalid MessageSet item.");
            }
            if (next instanceof zzjr) {
                zzmrVar.zza(zziwVar.zza(), (Object) ((zzjr) next).zza().zzc());
            } else {
                zzmrVar.zza(zziwVar.zza(), next.getValue());
            }
        }
        zzlu<?, ?> zzluVar = this.zzb;
        zzluVar.zzb((zzlu<?, ?>) zzluVar.zzb(t), zzmrVar);
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x00b9  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00be A[EDGE_INSN: B:58:0x00be->B:32:0x00be ?: BREAK  , SYNTHETIC] */
    @Override // com.google.android.gms.internal.vision.zzlc
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void zza(T r11, byte[] r12, int r13, int r14, com.google.android.gms.internal.vision.zzhn r15) throws java.io.IOException {
        /*
            r10 = this;
            r0 = r11
            com.google.android.gms.internal.vision.zzjb r0 = (com.google.android.gms.internal.vision.zzjb) r0
            com.google.android.gms.internal.vision.zzlx r1 = r0.zzb
            com.google.android.gms.internal.vision.zzlx r2 = com.google.android.gms.internal.vision.zzlx.zza()
            if (r1 != r2) goto L11
            com.google.android.gms.internal.vision.zzlx r1 = com.google.android.gms.internal.vision.zzlx.zzb()
            r0.zzb = r1
        L11:
            com.google.android.gms.internal.vision.zzjb$zzc r11 = (com.google.android.gms.internal.vision.zzjb.zzc) r11
            com.google.android.gms.internal.vision.zziu r11 = r11.zza()
            r0 = 0
            r2 = r0
        L19:
            if (r13 >= r14) goto Lc9
            int r4 = com.google.android.gms.internal.vision.zzhl.zza(r12, r13, r15)
            int r13 = r15.zza
            r3 = 11
            r5 = 2
            if (r13 == r3) goto L65
            r3 = r13 & 7
            if (r3 != r5) goto L60
            com.google.android.gms.internal.vision.zziq<?> r2 = r10.zzd
            com.google.android.gms.internal.vision.zzio r3 = r15.zzd
            com.google.android.gms.internal.vision.zzkk r5 = r10.zza
            int r6 = r13 >>> 3
            java.lang.Object r2 = r2.zza(r3, r5, r6)
            r8 = r2
            com.google.android.gms.internal.vision.zzjb$zze r8 = (com.google.android.gms.internal.vision.zzjb.zze) r8
            if (r8 == 0) goto L55
            com.google.android.gms.internal.vision.zzky r13 = com.google.android.gms.internal.vision.zzky.zza()
            com.google.android.gms.internal.vision.zzkk r2 = r8.zzc
            java.lang.Class r2 = r2.getClass()
            com.google.android.gms.internal.vision.zzlc r13 = r13.zza(r2)
            int r13 = com.google.android.gms.internal.vision.zzhl.zza(r13, r12, r4, r14, r15)
            com.google.android.gms.internal.vision.zzjb$zzf r2 = r8.zzd
            java.lang.Object r3 = r15.zzc
            r11.zza(r2, r3)
            goto L5e
        L55:
            r2 = r13
            r3 = r12
            r5 = r14
            r6 = r1
            r7 = r15
            int r13 = com.google.android.gms.internal.vision.zzhl.zza(r2, r3, r4, r5, r6, r7)
        L5e:
            r2 = r8
            goto L19
        L60:
            int r13 = com.google.android.gms.internal.vision.zzhl.zza(r13, r12, r4, r14, r15)
            goto L19
        L65:
            r13 = 0
            r3 = r0
        L67:
            if (r4 >= r14) goto Lbe
            int r4 = com.google.android.gms.internal.vision.zzhl.zza(r12, r4, r15)
            int r6 = r15.zza
            int r7 = r6 >>> 3
            r8 = r6 & 7
            if (r7 == r5) goto La0
            r9 = 3
            if (r7 == r9) goto L79
            goto Lb5
        L79:
            if (r2 == 0) goto L95
            com.google.android.gms.internal.vision.zzky r6 = com.google.android.gms.internal.vision.zzky.zza()
            com.google.android.gms.internal.vision.zzkk r7 = r2.zzc
            java.lang.Class r7 = r7.getClass()
            com.google.android.gms.internal.vision.zzlc r6 = r6.zza(r7)
            int r4 = com.google.android.gms.internal.vision.zzhl.zza(r6, r12, r4, r14, r15)
            com.google.android.gms.internal.vision.zzjb$zzf r6 = r2.zzd
            java.lang.Object r7 = r15.zzc
            r11.zza(r6, r7)
            goto L67
        L95:
            if (r8 != r5) goto Lb5
            int r4 = com.google.android.gms.internal.vision.zzhl.zze(r12, r4, r15)
            java.lang.Object r3 = r15.zzc
            com.google.android.gms.internal.vision.zzht r3 = (com.google.android.gms.internal.vision.zzht) r3
            goto L67
        La0:
            if (r8 != 0) goto Lb5
            int r4 = com.google.android.gms.internal.vision.zzhl.zza(r12, r4, r15)
            int r13 = r15.zza
            com.google.android.gms.internal.vision.zziq<?> r2 = r10.zzd
            com.google.android.gms.internal.vision.zzio r6 = r15.zzd
            com.google.android.gms.internal.vision.zzkk r7 = r10.zza
            java.lang.Object r2 = r2.zza(r6, r7, r13)
            com.google.android.gms.internal.vision.zzjb$zze r2 = (com.google.android.gms.internal.vision.zzjb.zze) r2
            goto L67
        Lb5:
            r7 = 12
            if (r6 == r7) goto Lbe
            int r4 = com.google.android.gms.internal.vision.zzhl.zza(r6, r12, r4, r14, r15)
            goto L67
        Lbe:
            if (r3 == 0) goto Lc6
            int r13 = r13 << 3
            r13 = r13 | r5
            r1.zza(r13, r3)
        Lc6:
            r13 = r4
            goto L19
        Lc9:
            if (r13 != r14) goto Lcc
            return
        Lcc:
            com.google.android.gms.internal.vision.zzjk r11 = com.google.android.gms.internal.vision.zzjk.zzg()
            goto Ld2
        Ld1:
            throw r11
        Ld2:
            goto Ld1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzkq.zza(java.lang.Object, byte[], int, int, com.google.android.gms.internal.vision.zzhn):void");
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final void zzc(T t) {
        this.zzb.zzd(t);
        this.zzd.zzc(t);
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final boolean zzd(T t) {
        return this.zzd.zza(t).zzf();
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final int zzb(T t) {
        zzlu<?, ?> zzluVar = this.zzb;
        int zze = zzluVar.zze(zzluVar.zzb(t)) + 0;
        return this.zzc ? zze + this.zzd.zza(t).zzg() : zze;
    }
}
