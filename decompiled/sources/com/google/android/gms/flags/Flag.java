package com.google.android.gms.flags;

import android.os.RemoteException;
@Deprecated
/* loaded from: classes3.dex */
public abstract class Flag<T> {
    private final String mKey;
    private final int zze;
    private final T zzf;

    private Flag(int i, String str, T t) {
        this.zze = i;
        this.mKey = str;
        this.zzf = t;
        Singletons.flagRegistry().zza(this);
    }

    public abstract T zza(zzc zzcVar);

    @Deprecated
    /* loaded from: classes3.dex */
    public static class BooleanFlag extends Flag<Boolean> {
        public BooleanFlag(int i, String str, Boolean bool) {
            super(i, str, bool);
        }

        /* renamed from: zzb */
        public final Boolean zza(zzc zzcVar) {
            try {
                return Boolean.valueOf(zzcVar.getBooleanFlagValue(getKey(), zzb().booleanValue(), getSource()));
            } catch (RemoteException e) {
                return zzb();
            }
        }
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public static class IntegerFlag extends Flag<Integer> {
        public IntegerFlag(int i, String str, Integer num) {
            super(i, str, num);
        }

        /* renamed from: zzc */
        public final Integer zza(zzc zzcVar) {
            try {
                return Integer.valueOf(zzcVar.getIntFlagValue(getKey(), zzb().intValue(), getSource()));
            } catch (RemoteException e) {
                return zzb();
            }
        }
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public static class LongFlag extends Flag<Long> {
        public LongFlag(int i, String str, Long l) {
            super(i, str, l);
        }

        /* renamed from: zzd */
        public final Long zza(zzc zzcVar) {
            try {
                return Long.valueOf(zzcVar.getLongFlagValue(getKey(), zzb().longValue(), getSource()));
            } catch (RemoteException e) {
                return zzb();
            }
        }
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public static class StringFlag extends Flag<String> {
        public StringFlag(int i, String str, String str2) {
            super(i, str, str2);
        }

        /* renamed from: zze */
        public final String zza(zzc zzcVar) {
            try {
                return zzcVar.getStringFlagValue(getKey(), zzb(), getSource());
            } catch (RemoteException e) {
                return zzb();
            }
        }
    }

    public final String getKey() {
        return this.mKey;
    }

    public final T zzb() {
        return this.zzf;
    }

    public T get() {
        return (T) Singletons.zzd().zzb(this);
    }

    @Deprecated
    public static BooleanFlag define(int i, String str, Boolean bool) {
        return new BooleanFlag(i, str, bool);
    }

    @Deprecated
    public static IntegerFlag define(int i, String str, int i2) {
        return new IntegerFlag(i, str, Integer.valueOf(i2));
    }

    @Deprecated
    public static LongFlag define(int i, String str, long j) {
        return new LongFlag(i, str, Long.valueOf(j));
    }

    @Deprecated
    public static StringFlag define(int i, String str, String str2) {
        return new StringFlag(i, str, str2);
    }

    @Deprecated
    public final int getSource() {
        return this.zze;
    }
}
