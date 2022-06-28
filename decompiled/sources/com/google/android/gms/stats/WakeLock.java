package com.google.android.gms.stats;

import android.content.Context;
import android.os.PowerManager;
import android.os.WorkSource;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.providers.PooledExecutorsProvider;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.common.util.WorkSourceUtil;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes3.dex */
public class WakeLock {
    private static ScheduledExecutorService zzn;
    private static volatile zza zzo = new com.google.android.gms.stats.zza();
    private final Object zza;
    private final PowerManager.WakeLock zzb;
    private WorkSource zzc;
    private final int zzd;
    private final String zze;
    private final String zzf;
    private final String zzg;
    private final Context zzh;
    private boolean zzi;
    private final Map<String, Integer[]> zzj;
    private final Set<Future<?>> zzk;
    private int zzl;
    private AtomicInteger zzm;

    /* loaded from: classes3.dex */
    public interface zza {
    }

    public WakeLock(Context context, int i, String str) {
        this(context, i, str, null, context == null ? null : context.getPackageName());
    }

    private WakeLock(Context context, int i, String str, String str2, String str3) {
        this(context, i, str, null, str3, null);
    }

    private WakeLock(Context context, int i, String str, String str2, String str3, String str4) {
        this.zza = this;
        this.zzi = true;
        this.zzj = new HashMap();
        this.zzk = Collections.synchronizedSet(new HashSet());
        this.zzm = new AtomicInteger(0);
        Preconditions.checkNotNull(context, "WakeLock: context must not be null");
        Preconditions.checkNotEmpty(str, "WakeLock: wakeLockName must not be empty");
        this.zzd = i;
        this.zzf = null;
        this.zzg = null;
        Context applicationContext = context.getApplicationContext();
        this.zzh = applicationContext;
        if (!"com.google.android.gms".equals(context.getPackageName())) {
            String valueOf = String.valueOf("*gcore*:");
            String valueOf2 = String.valueOf(str);
            this.zze = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        } else {
            this.zze = str;
        }
        PowerManager.WakeLock newWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(i, str);
        this.zzb = newWakeLock;
        if (WorkSourceUtil.hasWorkSourcePermission(context)) {
            WorkSource fromPackage = WorkSourceUtil.fromPackage(context, Strings.isEmptyOrWhitespace(str3) ? context.getPackageName() : str3);
            this.zzc = fromPackage;
            if (fromPackage != null && WorkSourceUtil.hasWorkSourcePermission(applicationContext)) {
                WorkSource workSource = this.zzc;
                if (workSource != null) {
                    workSource.add(fromPackage);
                } else {
                    this.zzc = fromPackage;
                }
                try {
                    newWakeLock.setWorkSource(this.zzc);
                } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
                    Log.wtf("WakeLock", e.toString());
                }
            }
        }
        if (zzn == null) {
            zzn = PooledExecutorsProvider.getInstance().newSingleThreadScheduledExecutor();
        }
    }

    private final List<String> zza() {
        return WorkSourceUtil.getNames(this.zzc);
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0058, code lost:
        if (r2 == false) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0060, code lost:
        if (r13.zzl == 0) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0062, code lost:
        com.google.android.gms.common.stats.WakeLockTracker.getInstance().registerEvent(r13.zzh, com.google.android.gms.common.stats.StatsUtils.getEventKey(r13.zzb, r6), 7, r13.zze, r6, null, r13.zzd, zza(), r14);
        r13.zzl++;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void acquire(long r14) {
        /*
            r13 = this;
            java.util.concurrent.atomic.AtomicInteger r0 = r13.zzm
            r0.incrementAndGet()
            r0 = 0
            java.lang.String r6 = r13.zza(r0)
            java.lang.Object r0 = r13.zza
            monitor-enter(r0)
            java.util.Map<java.lang.String, java.lang.Integer[]> r1 = r13.zzj     // Catch: java.lang.Throwable -> L9b
            boolean r1 = r1.isEmpty()     // Catch: java.lang.Throwable -> L9b
            r2 = 0
            if (r1 == 0) goto L1d
            int r1 = r13.zzl     // Catch: java.lang.Throwable -> L9b
            if (r1 <= 0) goto L2c
        L1d:
            android.os.PowerManager$WakeLock r1 = r13.zzb     // Catch: java.lang.Throwable -> L9b
            boolean r1 = r1.isHeld()     // Catch: java.lang.Throwable -> L9b
            if (r1 != 0) goto L2c
            java.util.Map<java.lang.String, java.lang.Integer[]> r1 = r13.zzj     // Catch: java.lang.Throwable -> L9b
            r1.clear()     // Catch: java.lang.Throwable -> L9b
            r13.zzl = r2     // Catch: java.lang.Throwable -> L9b
        L2c:
            boolean r1 = r13.zzi     // Catch: java.lang.Throwable -> L9b
            r12 = 1
            if (r1 == 0) goto L5a
            java.util.Map<java.lang.String, java.lang.Integer[]> r1 = r13.zzj     // Catch: java.lang.Throwable -> L9b
            java.lang.Object r1 = r1.get(r6)     // Catch: java.lang.Throwable -> L9b
            java.lang.Integer[] r1 = (java.lang.Integer[]) r1     // Catch: java.lang.Throwable -> L9b
            if (r1 != 0) goto L4a
            java.util.Map<java.lang.String, java.lang.Integer[]> r1 = r13.zzj     // Catch: java.lang.Throwable -> L9b
            java.lang.Integer[] r3 = new java.lang.Integer[r12]     // Catch: java.lang.Throwable -> L9b
            java.lang.Integer r4 = java.lang.Integer.valueOf(r12)     // Catch: java.lang.Throwable -> L9b
            r3[r2] = r4     // Catch: java.lang.Throwable -> L9b
            r1.put(r6, r3)     // Catch: java.lang.Throwable -> L9b
            r2 = 1
            goto L58
        L4a:
            r3 = r1[r2]     // Catch: java.lang.Throwable -> L9b
            int r3 = r3.intValue()     // Catch: java.lang.Throwable -> L9b
            int r3 = r3 + r12
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch: java.lang.Throwable -> L9b
            r1[r2] = r3     // Catch: java.lang.Throwable -> L9b
        L58:
            if (r2 != 0) goto L62
        L5a:
            boolean r1 = r13.zzi     // Catch: java.lang.Throwable -> L9b
            if (r1 != 0) goto L81
            int r1 = r13.zzl     // Catch: java.lang.Throwable -> L9b
            if (r1 != 0) goto L81
        L62:
            com.google.android.gms.common.stats.WakeLockTracker r1 = com.google.android.gms.common.stats.WakeLockTracker.getInstance()     // Catch: java.lang.Throwable -> L9b
            android.content.Context r2 = r13.zzh     // Catch: java.lang.Throwable -> L9b
            android.os.PowerManager$WakeLock r3 = r13.zzb     // Catch: java.lang.Throwable -> L9b
            java.lang.String r3 = com.google.android.gms.common.stats.StatsUtils.getEventKey(r3, r6)     // Catch: java.lang.Throwable -> L9b
            r4 = 7
            java.lang.String r5 = r13.zze     // Catch: java.lang.Throwable -> L9b
            r7 = 0
            int r8 = r13.zzd     // Catch: java.lang.Throwable -> L9b
            java.util.List r9 = r13.zza()     // Catch: java.lang.Throwable -> L9b
            r10 = r14
            r1.registerEvent(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch: java.lang.Throwable -> L9b
            int r1 = r13.zzl     // Catch: java.lang.Throwable -> L9b
            int r1 = r1 + r12
            r13.zzl = r1     // Catch: java.lang.Throwable -> L9b
        L81:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L9b
            android.os.PowerManager$WakeLock r0 = r13.zzb
            r0.acquire()
            r0 = 0
            int r2 = (r14 > r0 ? 1 : (r14 == r0 ? 0 : -1))
            if (r2 <= 0) goto L9a
        L8e:
            java.util.concurrent.ScheduledExecutorService r0 = com.google.android.gms.stats.WakeLock.zzn
            com.google.android.gms.stats.zzb r1 = new com.google.android.gms.stats.zzb
            r1.<init>(r13)
            java.util.concurrent.TimeUnit r2 = java.util.concurrent.TimeUnit.MILLISECONDS
            r0.schedule(r1, r14, r2)
        L9a:
            return
        L9b:
            r14 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L9b
            throw r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.stats.WakeLock.acquire(long):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0053, code lost:
        if (r1 == false) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x005b, code lost:
        if (r12.zzl == 1) goto L21;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x005d, code lost:
        com.google.android.gms.common.stats.WakeLockTracker.getInstance().registerEvent(r12.zzh, com.google.android.gms.common.stats.StatsUtils.getEventKey(r12.zzb, r6), 8, r12.zze, r6, null, r12.zzd, zza());
        r12.zzl--;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void release() {
        /*
            r12 = this;
            java.util.concurrent.atomic.AtomicInteger r0 = r12.zzm
            int r0 = r0.decrementAndGet()
            if (r0 >= 0) goto L1a
            java.lang.String r0 = "WakeLock"
            java.lang.String r1 = r12.zze
            java.lang.String r1 = java.lang.String.valueOf(r1)
            java.lang.String r2 = " release without a matched acquire!"
            java.lang.String r1 = r1.concat(r2)
            android.util.Log.e(r0, r1)
        L1a:
            r0 = 0
            java.lang.String r6 = r12.zza(r0)
            java.lang.Object r0 = r12.zza
            monitor-enter(r0)
            boolean r1 = r12.zzi     // Catch: java.lang.Throwable -> L81
            r10 = 1
            r11 = 0
            if (r1 == 0) goto L55
            java.util.Map<java.lang.String, java.lang.Integer[]> r1 = r12.zzj     // Catch: java.lang.Throwable -> L81
            java.lang.Object r1 = r1.get(r6)     // Catch: java.lang.Throwable -> L81
            java.lang.Integer[] r1 = (java.lang.Integer[]) r1     // Catch: java.lang.Throwable -> L81
            if (r1 != 0) goto L36
            r1 = 0
            goto L53
        L36:
            r2 = r1[r11]     // Catch: java.lang.Throwable -> L81
            int r2 = r2.intValue()     // Catch: java.lang.Throwable -> L81
            if (r2 != r10) goto L45
            java.util.Map<java.lang.String, java.lang.Integer[]> r1 = r12.zzj     // Catch: java.lang.Throwable -> L81
            r1.remove(r6)     // Catch: java.lang.Throwable -> L81
            r1 = 1
            goto L53
        L45:
            r2 = r1[r11]     // Catch: java.lang.Throwable -> L81
            int r2 = r2.intValue()     // Catch: java.lang.Throwable -> L81
            int r2 = r2 - r10
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: java.lang.Throwable -> L81
            r1[r11] = r2     // Catch: java.lang.Throwable -> L81
            r1 = 0
        L53:
            if (r1 != 0) goto L5d
        L55:
            boolean r1 = r12.zzi     // Catch: java.lang.Throwable -> L81
            if (r1 != 0) goto L7c
            int r1 = r12.zzl     // Catch: java.lang.Throwable -> L81
            if (r1 != r10) goto L7c
        L5d:
            com.google.android.gms.common.stats.WakeLockTracker r1 = com.google.android.gms.common.stats.WakeLockTracker.getInstance()     // Catch: java.lang.Throwable -> L81
            android.content.Context r2 = r12.zzh     // Catch: java.lang.Throwable -> L81
            android.os.PowerManager$WakeLock r3 = r12.zzb     // Catch: java.lang.Throwable -> L81
            java.lang.String r3 = com.google.android.gms.common.stats.StatsUtils.getEventKey(r3, r6)     // Catch: java.lang.Throwable -> L81
            r4 = 8
            java.lang.String r5 = r12.zze     // Catch: java.lang.Throwable -> L81
            r7 = 0
            int r8 = r12.zzd     // Catch: java.lang.Throwable -> L81
            java.util.List r9 = r12.zza()     // Catch: java.lang.Throwable -> L81
            r1.registerEvent(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch: java.lang.Throwable -> L81
            int r1 = r12.zzl     // Catch: java.lang.Throwable -> L81
            int r1 = r1 - r10
            r12.zzl = r1     // Catch: java.lang.Throwable -> L81
        L7c:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L81
            r12.zza(r11)
            return
        L81:
            r1 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L81
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.stats.WakeLock.release():void");
    }

    public final void zza(int i) {
        if (this.zzb.isHeld()) {
            try {
                this.zzb.release();
            } catch (RuntimeException e) {
                if (e.getClass().equals(RuntimeException.class)) {
                    Log.e("WakeLock", String.valueOf(this.zze).concat(" was already released!"), e);
                } else {
                    throw e;
                }
            }
            this.zzb.isHeld();
        }
    }

    private final String zza(String str) {
        return (!this.zzi || TextUtils.isEmpty(str)) ? this.zzf : str;
    }

    public void setReferenceCounted(boolean z) {
        this.zzb.setReferenceCounted(z);
        this.zzi = z;
    }

    public boolean isHeld() {
        return this.zzb.isHeld();
    }
}
