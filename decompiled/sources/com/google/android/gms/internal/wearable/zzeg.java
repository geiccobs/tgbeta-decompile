package com.google.android.gms.internal.wearable;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.Unsafe;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
final class zzeg {
    static final long zza;
    static final boolean zzb;
    private static final Unsafe zzc;
    private static final Class<?> zzd = zzah.zza();
    private static final boolean zze;
    private static final boolean zzf;
    private static final zzef zzg;
    private static final boolean zzh;
    private static final boolean zzi;

    static {
        boolean z;
        boolean z2;
        zzef zzefVar;
        Unsafe zzq = zzq();
        zzc = zzq;
        boolean zzr = zzr(Long.TYPE);
        zze = zzr;
        boolean zzr2 = zzr(Integer.TYPE);
        zzf = zzr2;
        zzef zzefVar2 = null;
        if (zzq != null) {
            if (zzr) {
                zzefVar2 = new zzee(zzq);
            } else if (zzr2) {
                zzefVar2 = new zzed(zzq);
            }
        }
        zzg = zzefVar2;
        boolean z3 = true;
        if (zzefVar2 == null) {
            z = false;
        } else {
            Unsafe unsafe = zzefVar2.zza;
            if (unsafe == null) {
                z = false;
            } else {
                try {
                    Class<?> cls = unsafe.getClass();
                    cls.getMethod("objectFieldOffset", Field.class);
                    cls.getMethod("getLong", Object.class, Long.TYPE);
                    z = zzB() != null;
                } catch (Throwable th) {
                    zzs(th);
                    z = false;
                }
            }
        }
        zzh = z;
        zzef zzefVar3 = zzg;
        if (zzefVar3 == null) {
            z2 = false;
        } else {
            Unsafe unsafe2 = zzefVar3.zza;
            if (unsafe2 == null) {
                z2 = false;
            } else {
                try {
                    Class<?> cls2 = unsafe2.getClass();
                    cls2.getMethod("objectFieldOffset", Field.class);
                    cls2.getMethod("arrayBaseOffset", Class.class);
                    cls2.getMethod("arrayIndexScale", Class.class);
                    cls2.getMethod("getInt", Object.class, Long.TYPE);
                    cls2.getMethod("putInt", Object.class, Long.TYPE, Integer.TYPE);
                    cls2.getMethod("getLong", Object.class, Long.TYPE);
                    cls2.getMethod("putLong", Object.class, Long.TYPE, Long.TYPE);
                    cls2.getMethod("getObject", Object.class, Long.TYPE);
                    cls2.getMethod("putObject", Object.class, Long.TYPE, Object.class);
                    z2 = true;
                } catch (Throwable th2) {
                    zzs(th2);
                    z2 = false;
                }
            }
        }
        zzi = z2;
        zza = zzz(byte[].class);
        zzz(boolean[].class);
        zzA(boolean[].class);
        zzz(int[].class);
        zzA(int[].class);
        zzz(long[].class);
        zzA(long[].class);
        zzz(float[].class);
        zzA(float[].class);
        zzz(double[].class);
        zzA(double[].class);
        zzz(Object[].class);
        zzA(Object[].class);
        Field zzB = zzB();
        if (zzB != null && (zzefVar = zzg) != null) {
            zzefVar.zzh(zzB);
        }
        if (ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN) {
            z3 = false;
        }
        zzb = z3;
    }

    private zzeg() {
    }

    private static int zzA(Class<?> cls) {
        if (zzi) {
            return zzg.zzj(cls);
        }
        return -1;
    }

    private static Field zzB() {
        int i = zzah.zza;
        Field zzC = zzC(Buffer.class, "effectiveDirectAddress");
        if (zzC == null) {
            Field zzC2 = zzC(Buffer.class, "address");
            if (zzC2 != null && zzC2.getType() == Long.TYPE) {
                return zzC2;
            }
            return null;
        }
        return zzC;
    }

    private static Field zzC(Class<?> cls, String str) {
        try {
            return cls.getDeclaredField(str);
        } catch (Throwable th) {
            return null;
        }
    }

    public static void zzD(Object obj, long j, byte b) {
        long j2 = (-4) & j;
        zzef zzefVar = zzg;
        int i = ((((int) j) ^ (-1)) & 3) << 3;
        zzefVar.zzl(obj, j2, ((255 & b) << i) | (zzefVar.zzk(obj, j2) & ((255 << i) ^ (-1))));
    }

    public static void zzE(Object obj, long j, byte b) {
        long j2 = (-4) & j;
        zzef zzefVar = zzg;
        int i = (((int) j) & 3) << 3;
        zzefVar.zzl(obj, j2, ((255 & b) << i) | (zzefVar.zzk(obj, j2) & ((255 << i) ^ (-1))));
    }

    public static boolean zza() {
        return zzi;
    }

    public static boolean zzb() {
        return zzh;
    }

    public static <T> T zzc(Class<T> cls) {
        try {
            return (T) zzc.allocateInstance(cls);
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    public static int zzd(Object obj, long j) {
        return zzg.zzk(obj, j);
    }

    public static void zze(Object obj, long j, int i) {
        zzg.zzl(obj, j, i);
    }

    public static long zzf(Object obj, long j) {
        return zzg.zzm(obj, j);
    }

    public static void zzg(Object obj, long j, long j2) {
        zzg.zzn(obj, j, j2);
    }

    public static boolean zzh(Object obj, long j) {
        return zzg.zzb(obj, j);
    }

    public static void zzi(Object obj, long j, boolean z) {
        zzg.zzc(obj, j, z);
    }

    public static float zzj(Object obj, long j) {
        return zzg.zzd(obj, j);
    }

    public static void zzk(Object obj, long j, float f) {
        zzg.zze(obj, j, f);
    }

    public static double zzl(Object obj, long j) {
        return zzg.zzf(obj, j);
    }

    public static void zzm(Object obj, long j, double d) {
        zzg.zzg(obj, j, d);
    }

    public static Object zzn(Object obj, long j) {
        return zzg.zzo(obj, j);
    }

    public static void zzo(Object obj, long j, Object obj2) {
        zzg.zzp(obj, j, obj2);
    }

    public static void zzp(byte[] bArr, long j, byte b) {
        zzg.zza(bArr, zza + j, b);
    }

    public static Unsafe zzq() {
        try {
            return (Unsafe) AccessController.doPrivileged(new zzec());
        } catch (Throwable th) {
            return null;
        }
    }

    static boolean zzr(Class<?> cls) {
        int i = zzah.zza;
        try {
            Class<?> cls2 = zzd;
            cls2.getMethod("peekLong", cls, Boolean.TYPE);
            cls2.getMethod("pokeLong", cls, Long.TYPE, Boolean.TYPE);
            cls2.getMethod("pokeInt", cls, Integer.TYPE, Boolean.TYPE);
            cls2.getMethod("peekInt", cls, Boolean.TYPE);
            cls2.getMethod("pokeByte", cls, Byte.TYPE);
            cls2.getMethod("peekByte", cls);
            cls2.getMethod("pokeByteArray", cls, byte[].class, Integer.TYPE, Integer.TYPE);
            cls2.getMethod("peekByteArray", cls, byte[].class, Integer.TYPE, Integer.TYPE);
            return true;
        } catch (Throwable th) {
            return false;
        }
    }

    static /* synthetic */ void zzs(Throwable th) {
        Logger logger = Logger.getLogger(zzeg.class.getName());
        Level level = Level.WARNING;
        String valueOf = String.valueOf(th);
        StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 71);
        sb.append("platform method missing - proto runtime falling back to safer methods: ");
        sb.append(valueOf);
        logger.logp(level, "com.google.protobuf.UnsafeUtil", "logMissingMethod", sb.toString());
    }

    public static /* synthetic */ boolean zzv(Object obj, long j) {
        return ((byte) ((zzg.zzk(obj, (-4) & j) >>> ((int) (((j ^ (-1)) & 3) << 3))) & 255)) != 0;
    }

    public static /* synthetic */ boolean zzw(Object obj, long j) {
        return ((byte) ((zzg.zzk(obj, (-4) & j) >>> ((int) ((j & 3) << 3))) & 255)) != 0;
    }

    private static int zzz(Class<?> cls) {
        if (zzi) {
            return zzg.zzi(cls);
        }
        return -1;
    }
}
