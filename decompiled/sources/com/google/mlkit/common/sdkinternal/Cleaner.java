package com.google.mlkit.common.sdkinternal;

import com.google.mlkit.common.sdkinternal.Cleaner;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public class Cleaner {
    private final ReferenceQueue<Object> zza = new ReferenceQueue<>();
    private final Set<zza> zzb = Collections.synchronizedSet(new HashSet());

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public interface Cleanable {
        void clean();
    }

    private Cleaner() {
    }

    /* compiled from: com.google.mlkit:common@@17.0.0 */
    /* loaded from: classes3.dex */
    public static class zza extends PhantomReference<Object> implements Cleanable {
        private final Set<zza> zza;
        private final Runnable zzb;

        private zza(Object obj, ReferenceQueue<? super Object> referenceQueue, Set<zza> set, Runnable runnable) {
            super(obj, referenceQueue);
            this.zza = set;
            this.zzb = runnable;
        }

        @Override // com.google.mlkit.common.sdkinternal.Cleaner.Cleanable
        public final void clean() {
            if (!this.zza.remove(this)) {
                return;
            }
            clear();
            this.zzb.run();
        }
    }

    public static Cleaner create() {
        ThreadFactory threadFactory = com.google.mlkit.common.sdkinternal.zza.zza;
        Cleaner cleaner = new Cleaner();
        cleaner.register(cleaner, zzc.zza);
        threadFactory.newThread(new Runnable(cleaner.zza, cleaner.zzb) { // from class: com.google.mlkit.common.sdkinternal.zzb
            private final ReferenceQueue zza;
            private final Set zzb;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = referenceQueue;
                this.zzb = set;
            }

            @Override // java.lang.Runnable
            public final void run() {
                ReferenceQueue referenceQueue2 = this.zza;
                Set set2 = this.zzb;
                while (!set2.isEmpty()) {
                    try {
                        ((Cleaner.zza) referenceQueue2.remove()).clean();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
        return cleaner;
    }

    public Cleanable register(Object obj, Runnable runnable) {
        zza zzaVar = new zza(obj, this.zza, this.zzb, runnable);
        this.zzb.add(zzaVar);
        return zzaVar;
    }
}
