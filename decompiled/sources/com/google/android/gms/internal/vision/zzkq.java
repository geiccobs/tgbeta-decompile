package com.google.android.gms.internal.vision;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
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
        if (this.zzc) {
            return this.zzd.zza(t).equals(this.zzd.zza(t2));
        }
        return true;
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final int zza(T t) {
        int hashCode = this.zzb.zzb(t).hashCode();
        if (this.zzc) {
            return (hashCode * 53) + this.zzd.zza(t).hashCode();
        }
        return hashCode;
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

    /* JADX WARN: Removed duplicated region for block: B:28:0x00be  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00c6  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x00c3 A[SYNTHETIC] */
    @Override // com.google.android.gms.internal.vision.zzlc
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void zza(T r10, byte[] r11, int r12, int r13, com.google.android.gms.internal.vision.zzhn r14) throws java.io.IOException {
        /*
            r9 = this;
            r0 = r10
            com.google.android.gms.internal.vision.zzjb r0 = (com.google.android.gms.internal.vision.zzjb) r0
            com.google.android.gms.internal.vision.zzlx r1 = r0.zzb
            com.google.android.gms.internal.vision.zzlx r2 = com.google.android.gms.internal.vision.zzlx.zza()
            if (r1 != r2) goto L11
            com.google.android.gms.internal.vision.zzlx r1 = com.google.android.gms.internal.vision.zzlx.zzb()
            r0.zzb = r1
        L11:
            com.google.android.gms.internal.vision.zzjb$zzc r10 = (com.google.android.gms.internal.vision.zzjb.zzc) r10
            com.google.android.gms.internal.vision.zziu r10 = r10.zza()
            r0 = 0
            r2 = r0
        L19:
            if (r12 >= r13) goto Ld0
            int r4 = com.google.android.gms.internal.vision.zzhl.zza(r11, r12, r14)
            int r12 = r14.zza
            r3 = 11
            r5 = 2
            if (r12 == r3) goto L68
        L27:
            r3 = r12 & 7
            if (r3 != r5) goto L63
            com.google.android.gms.internal.vision.zziq<?> r2 = r9.zzd
            com.google.android.gms.internal.vision.zzio r3 = r14.zzd
            com.google.android.gms.internal.vision.zzkk r5 = r9.zza
            int r6 = r12 >>> 3
            java.lang.Object r2 = r2.zza(r3, r5, r6)
            r8 = r2
            com.google.android.gms.internal.vision.zzjb$zze r8 = (com.google.android.gms.internal.vision.zzjb.zze) r8
            if (r8 == 0) goto L57
            com.google.android.gms.internal.vision.zzky r12 = com.google.android.gms.internal.vision.zzky.zza()
            com.google.android.gms.internal.vision.zzkk r2 = r8.zzc
            java.lang.Class r2 = r2.getClass()
            com.google.android.gms.internal.vision.zzlc r12 = r12.zza(r2)
            int r12 = com.google.android.gms.internal.vision.zzhl.zza(r12, r11, r4, r13, r14)
            com.google.android.gms.internal.vision.zzjb$zzf r2 = r8.zzd
            java.lang.Object r3 = r14.zzc
            r10.zza(r2, r3)
            r2 = r8
            goto L19
        L57:
            r2 = r12
            r3 = r11
            r5 = r13
            r6 = r1
            r7 = r14
            int r12 = com.google.android.gms.internal.vision.zzhl.zza(r2, r3, r4, r5, r6, r7)
            r2 = r8
            goto L19
        L63:
            int r12 = com.google.android.gms.internal.vision.zzhl.zza(r12, r11, r4, r13, r14)
            goto L19
        L68:
            r12 = 0
            r3 = r0
        L6a:
            if (r4 >= r13) goto Lc3
            int r4 = com.google.android.gms.internal.vision.zzhl.zza(r11, r4, r14)
            int r6 = r14.zza
            int r7 = r6 >>> 3
            r8 = r6 & 7
            switch(r7) {
                case 2: goto La5;
                case 3: goto L7e;
                default: goto L7d;
            }
        L7d:
            goto Lba
        L7e:
            if (r2 == 0) goto L9a
            com.google.android.gms.internal.vision.zzky r6 = com.google.android.gms.internal.vision.zzky.zza()
            com.google.android.gms.internal.vision.zzkk r7 = r2.zzc
            java.lang.Class r7 = r7.getClass()
            com.google.android.gms.internal.vision.zzlc r6 = r6.zza(r7)
            int r4 = com.google.android.gms.internal.vision.zzhl.zza(r6, r11, r4, r13, r14)
            com.google.android.gms.internal.vision.zzjb$zzf r6 = r2.zzd
            java.lang.Object r7 = r14.zzc
            r10.zza(r6, r7)
            goto L6a
        L9a:
            if (r8 != r5) goto Lba
            int r4 = com.google.android.gms.internal.vision.zzhl.zze(r11, r4, r14)
            java.lang.Object r3 = r14.zzc
            com.google.android.gms.internal.vision.zzht r3 = (com.google.android.gms.internal.vision.zzht) r3
            goto L6a
        La5:
            if (r8 != 0) goto Lba
            int r4 = com.google.android.gms.internal.vision.zzhl.zza(r11, r4, r14)
            int r12 = r14.zza
            com.google.android.gms.internal.vision.zziq<?> r2 = r9.zzd
            com.google.android.gms.internal.vision.zzio r6 = r14.zzd
            com.google.android.gms.internal.vision.zzkk r7 = r9.zza
            java.lang.Object r2 = r2.zza(r6, r7, r12)
            com.google.android.gms.internal.vision.zzjb$zze r2 = (com.google.android.gms.internal.vision.zzjb.zze) r2
            goto L6a
        Lba:
            r7 = 12
            if (r6 == r7) goto Lc3
            int r4 = com.google.android.gms.internal.vision.zzhl.zza(r6, r11, r4, r13, r14)
            goto L6a
        Lc3:
            if (r3 == 0) goto Lcd
        Lc6:
            int r12 = r12 << 3
            r12 = r12 | r5
            r1.zza(r12, r3)
        Lcd:
            r12 = r4
            goto L19
        Ld0:
            if (r12 != r13) goto Ld3
            return
        Ld3:
            com.google.android.gms.internal.vision.zzjk r10 = com.google.android.gms.internal.vision.zzjk.zzg()
            goto Ld9
        Ld8:
            throw r10
        Ld9:
            goto Ld8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzkq.zza(java.lang.Object, byte[], int, int, com.google.android.gms.internal.vision.zzhn):void");
    }

    @Override // com.google.android.gms.internal.vision.zzlc
    public final void zza(T t, zzld zzldVar, zzio zzioVar) throws IOException {
        boolean z;
        zzlu<?, ?> zzluVar = this.zzb;
        zziq<?> zziqVar = this.zzd;
        Object zzc = zzluVar.zzc(t);
        zziu<?> zzb = zziqVar.zzb(t);
        do {
            try {
                if (zzldVar.zza() == Integer.MAX_VALUE) {
                    return;
                }
                int zzb2 = zzldVar.zzb();
                if (zzb2 != 11) {
                    if ((zzb2 & 7) == 2) {
                        Object zza = zziqVar.zza(zzioVar, this.zza, zzb2 >>> 3);
                        if (zza != null) {
                            zziqVar.zza(zzldVar, zza, zzioVar, zzb);
                        } else {
                            z = zzluVar.zza((zzlu<?, ?>) zzc, zzldVar);
                            continue;
                        }
                    } else {
                        z = zzldVar.zzc();
                        continue;
                    }
                } else {
                    int i = 0;
                    Object obj = null;
                    zzht zzhtVar = null;
                    while (zzldVar.zza() != Integer.MAX_VALUE) {
                        int zzb3 = zzldVar.zzb();
                        if (zzb3 == 16) {
                            i = zzldVar.zzo();
                            obj = zziqVar.zza(zzioVar, this.zza, i);
                        } else if (zzb3 == 26) {
                            if (obj != null) {
                                zziqVar.zza(zzldVar, obj, zzioVar, zzb);
                            } else {
                                zzhtVar = zzldVar.zzn();
                            }
                        } else if (!zzldVar.zzc()) {
                            break;
                        }
                    }
                    if (zzldVar.zzb() != 12) {
                        throw zzjk.zze();
                    } else if (zzhtVar != null) {
                        if (obj != null) {
                            zziqVar.zza(zzhtVar, obj, zzioVar, zzb);
                        } else {
                            zzluVar.zza((zzlu<?, ?>) zzc, i, zzhtVar);
                        }
                    }
                }
                z = true;
                continue;
            } finally {
                zzluVar.zzb((Object) t, (T) zzc);
            }
        } while (z);
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
        if (this.zzc) {
            return zze + this.zzd.zza(t).zzg();
        }
        return zze;
    }
}
