package com.google.mlkit.nl.languageid.internal;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.os.SystemClock;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.mlkit_language_id.zzai;
import com.google.android.gms.internal.mlkit_language_id.zzaj;
import com.google.android.gms.internal.mlkit_language_id.zzcv;
import com.google.android.gms.internal.mlkit_language_id.zzq;
import com.google.android.gms.internal.mlkit_language_id.zzy$zzad;
import com.google.android.gms.internal.mlkit_language_id.zzy$zzaf;
import com.google.android.gms.internal.mlkit_language_id.zzy$zzau;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.sdkinternal.ModelResource;
import com.google.mlkit.nl.languageid.IdentifiedLanguage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
/* compiled from: com.google.mlkit:language-id@@16.1.1 */
/* loaded from: classes.dex */
public class LanguageIdentificationJni extends ModelResource {
    private static boolean zza;
    private final Context zzb;
    private final zzcv zzc;
    private MappedByteBuffer zzd;
    private long zze;

    public LanguageIdentificationJni(Context context, zzcv zzcvVar) {
        this.zzb = context;
        this.zzc = zzcvVar;
    }

    private native void nativeDestroy(long j);

    private native String nativeIdentifyLanguage(long j, byte[] bArr, float f);

    private native IdentifiedLanguage[] nativeIdentifyPossibleLanguages(long j, byte[] bArr, float f);

    private native long nativeInit(MappedByteBuffer mappedByteBuffer, long j);

    public final <T> Task<T> zza(Executor executor, Callable<T> callable, CancellationToken cancellationToken) {
        AtomicReference atomicReference = new AtomicReference(Thread.currentThread());
        Task<T> callAfterLoad = callAfterLoad(new Executor(this, atomicReference, executor) { // from class: com.google.mlkit.nl.languageid.internal.zzb
            private final LanguageIdentificationJni zza;
            private final AtomicReference zzb;
            private final Executor zzc;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = this;
                this.zzb = atomicReference;
                this.zzc = executor;
            }

            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                LanguageIdentificationJni languageIdentificationJni = this.zza;
                AtomicReference atomicReference2 = this.zzb;
                Executor executor2 = this.zzc;
                if (Thread.currentThread().equals(atomicReference2.get()) && languageIdentificationJni.isLoaded()) {
                    runnable.run();
                } else {
                    executor2.execute(runnable);
                }
            }
        }, callable, cancellationToken);
        atomicReference.set(null);
        return callAfterLoad;
    }

    public final String zza(String str, float f) {
        Preconditions.checkState(this.zze != 0);
        return nativeIdentifyLanguage(this.zze, str.getBytes(com.google.android.gms.internal.mlkit_language_id.zzb.zza), f);
    }

    public final List<IdentifiedLanguage> zzb(String str, float f) {
        Preconditions.checkState(this.zze != 0);
        return Arrays.asList(nativeIdentifyPossibleLanguages(this.zze, str.getBytes(com.google.android.gms.internal.mlkit_language_id.zzb.zza), f));
    }

    @Override // com.google.mlkit.common.sdkinternal.ModelResource
    public void load() throws MlKitException {
        this.taskQueue.checkIsRunningOnCurrentThread();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        try {
            Preconditions.checkState(this.zze == 0);
            zzb();
            try {
                AssetFileDescriptor openFd = this.zzb.getAssets().openFd("langid_model.smfb.jpg");
                try {
                    MappedByteBuffer map = new FileInputStream(openFd.getFileDescriptor()).getChannel().map(FileChannel.MapMode.READ_ONLY, openFd.getStartOffset(), openFd.getDeclaredLength());
                    this.zzd = map;
                    long nativeInit = nativeInit(map, openFd.getDeclaredLength());
                    this.zze = nativeInit;
                    if (nativeInit == 0) {
                        throw new MlKitException("Couldn't load language detection model", 13);
                    }
                    openFd.close();
                } catch (Throwable th) {
                    if (openFd != null) {
                        try {
                            openFd.close();
                        } catch (Throwable th2) {
                            zzq.zza(th, th2);
                        }
                    }
                    throw th;
                }
            } catch (IOException e) {
                throw new MlKitException("Couldn't open language detection model file", 13, e);
            }
        } catch (MlKitException e2) {
            this.zzc.zza(new zzcv.zza(SystemClock.elapsedRealtime() - elapsedRealtime) { // from class: com.google.mlkit.nl.languageid.internal.zza
                private final long zza;

                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    this.zza = elapsedRealtime2;
                }

                @Override // com.google.android.gms.internal.mlkit_language_id.zzcv.zza
                public final zzy$zzad.zza zza() {
                    return zzy$zzad.zzb().zza(zzy$zzau.zza().zza(zzy$zzaf.zza().zza(this.zza).zza(zzai.UNKNOWN_ERROR)));
                }
            }, zzaj.ON_DEVICE_LANGUAGE_IDENTIFICATION_LOAD);
            throw e2;
        }
    }

    @Override // com.google.mlkit.common.sdkinternal.ModelResource
    public void release() {
        this.taskQueue.checkIsRunningOnCurrentThread();
        long j = this.zze;
        if (j == 0) {
            return;
        }
        nativeDestroy(j);
        this.zze = 0L;
        this.zzd = null;
    }

    private static synchronized void zzb() throws MlKitException {
        synchronized (LanguageIdentificationJni.class) {
            if (zza) {
                return;
            }
            try {
                System.loadLibrary("language_id_jni");
                zza = true;
            } catch (UnsatisfiedLinkError e) {
                throw new MlKitException("Couldn't load language detection library.", 12, e);
            }
        }
    }
}
