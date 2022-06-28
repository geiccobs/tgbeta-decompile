package com.google.android.gms.internal.mlkit_common;

import android.util.Log;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes3.dex */
public final class zzn {
    private static final Method zza;
    private static final Method zzb;
    private static final Method zzc;
    private static final Field zzd;
    private static final Field zze;
    private static final Field zzf;
    private static final Object zzg;
    private static final Throwable zzh;

    public static zzj zza(String str) throws IOException {
        return (zzj) zza((Callable<Object>) new Callable(str) { // from class: com.google.android.gms.internal.mlkit_common.zzm
            private final String zza;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = str;
            }

            @Override // java.util.concurrent.Callable
            public final Object call() {
                zzj zza2;
                zza2 = zzn.zza(zzn.zzb.invoke(zzn.zzg, this.zza));
                return zza2;
            }
        });
    }

    public static zzj zza(FileDescriptor fileDescriptor) throws IOException {
        return (zzj) zza((Callable<Object>) new Callable(fileDescriptor) { // from class: com.google.android.gms.internal.mlkit_common.zzp
            private final FileDescriptor zza;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zza = fileDescriptor;
            }

            @Override // java.util.concurrent.Callable
            public final Object call() {
                zzj zza2;
                zza2 = zzn.zza(zzn.zzc.invoke(zzn.zzg, this.zza));
                return zza2;
            }
        });
    }

    public static zzj zza(Object obj) throws Exception {
        return new zzj(((Long) zzd.get(obj)).longValue(), ((Long) zze.get(obj)).longValue(), ((Boolean) zza.invoke(null, Integer.valueOf(((Integer) zzf.get(obj)).intValue()))).booleanValue());
    }

    private static <T> T zza(Callable<T> callable) throws IOException {
        try {
            th = zzh;
            if (th != null) {
                throw new IOException(th);
            }
            return callable.call();
        } finally {
            IOException iOException = new IOException(th);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    static {
        Field field;
        Field field2;
        Throwable th;
        Method method;
        Method method2;
        Field field3;
        Object obj;
        Throwable th2;
        Throwable th3;
        Throwable th4;
        Throwable th5;
        Throwable th6;
        Throwable th7 = null;
        try {
            Class<?> cls = Class.forName("libcore.io.Libcore");
            Class<?> cls2 = Class.forName("libcore.io.StructStat");
            Class<?> cls3 = Class.forName("libcore.io.OsConstants");
            Class<?> cls4 = Class.forName("libcore.io.ForwardingOs");
            Method method3 = cls3.getDeclaredMethod("S_ISLNK", Integer.TYPE);
            try {
                method3.setAccessible(true);
                method = cls4.getDeclaredMethod("lstat", String.class);
                try {
                    method2 = cls4.getDeclaredMethod("fstat", FileDescriptor.class);
                    try {
                        Field declaredField = cls.getDeclaredField("os");
                        declaredField.setAccessible(true);
                        obj = declaredField.get(cls);
                        try {
                            field2 = cls2.getField("st_dev");
                            try {
                                field = cls2.getField("st_ino");
                                try {
                                    field3 = cls2.getField("st_mode");
                                    try {
                                        field2.setAccessible(true);
                                        field.setAccessible(true);
                                        field3.setAccessible(true);
                                    } catch (Throwable th8) {
                                        th = th8;
                                        try {
                                            Log.d("StructStatHelper", "Reflection failed", th);
                                        } finally {
                                            zza = method3;
                                            zzb = method;
                                            zzc = method2;
                                            zzd = field2;
                                            zze = field;
                                            zzf = field3;
                                            zzg = obj;
                                            zzh = th7;
                                        }
                                    }
                                } catch (Throwable th9) {
                                    th = th9;
                                    field3 = th7;
                                }
                            } catch (Throwable th10) {
                                th = th10;
                                field3 = th7;
                                field = field3;
                            }
                        } catch (Throwable th11) {
                            th = th11;
                            th6 = th7;
                            field2 = th6;
                            obj = obj;
                            field3 = th6;
                            method3 = method3;
                            method2 = method2;
                            method = method;
                            field = field2;
                            Log.d("StructStatHelper", "Reflection failed", th);
                        }
                    } catch (Throwable th12) {
                        th = th12;
                        obj = th7;
                        th6 = obj;
                    }
                } catch (Throwable th13) {
                    th = th13;
                    Throwable th14 = th7;
                    Throwable th15 = th14;
                    Throwable th16 = th15;
                    field2 = th16;
                    obj = th14;
                    field3 = th15;
                    method3 = method3;
                    method2 = th16;
                    method = method;
                }
            } catch (Throwable th17) {
                th = th17;
                Throwable th18 = th7;
                Throwable th19 = th18;
                th2 = th19;
                th5 = th18;
                th4 = th19;
                th3 = method3;
                Throwable th20 = th2;
                field2 = th20;
                obj = th5;
                field3 = th4;
                method3 = th3;
                method2 = th2;
                method = th20;
                field = field2;
                Log.d("StructStatHelper", "Reflection failed", th);
            }
        } catch (Throwable th21) {
            th = th21;
            Throwable th22 = th7;
            Throwable th23 = th22;
            Throwable th24 = th23;
            th2 = th24;
            th5 = th22;
            th4 = th23;
            th3 = th24;
        }
    }
}
