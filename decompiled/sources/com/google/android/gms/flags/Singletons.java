package com.google.android.gms.flags;
/* loaded from: classes.dex */
public final class Singletons {
    private static Singletons zzl;
    private final FlagRegistry zzm = new FlagRegistry();

    private Singletons() {
        new zzb();
    }

    private static Singletons zzc() {
        Singletons singletons;
        synchronized (Singletons.class) {
            singletons = zzl;
        }
        return singletons;
    }

    public static FlagRegistry flagRegistry() {
        return zzc().zzm;
    }

    static {
        Singletons singletons = new Singletons();
        synchronized (Singletons.class) {
            zzl = singletons;
        }
    }
}
