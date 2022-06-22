package com.google.android.gms.flags;
@Deprecated
/* loaded from: classes.dex */
public abstract class Flag<T> {
    private final T zzf;

    @Deprecated
    /* loaded from: classes.dex */
    public static class BooleanFlag extends Flag<Boolean> {
        public BooleanFlag(int i, String str, Boolean bool) {
            super(i, str, bool);
        }
    }

    private Flag(int i, String str, T t) {
        this.zzf = t;
        Singletons.flagRegistry().zza(this);
    }

    @Deprecated
    public static BooleanFlag define(int i, String str, Boolean bool) {
        return new BooleanFlag(i, str, bool);
    }
}
