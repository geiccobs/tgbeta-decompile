package com.google.android.gms.internal.clearcut;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.UserManager;
import android.util.Log;
import androidx.core.content.PermissionChecker;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public abstract class zzae<T> {
    private static final Object zzdn = new Object();
    private static volatile Boolean zzdp;
    private static volatile Boolean zzdq;
    @SuppressLint({"StaticFieldLeak"})
    private static Context zzh;
    private final zzao zzdr;
    final String zzds;
    private final String zzdt;
    private final T zzdu;
    private volatile zzab zzdw;
    private volatile SharedPreferences zzdx;

    private zzae(zzao zzaoVar, String str, T t) {
        String str2;
        String str3;
        String str4;
        String str5;
        Uri uri;
        Uri uri2;
        this.zzdw = null;
        this.zzdx = null;
        str2 = zzaoVar.zzef;
        if (str2 == null) {
            uri2 = zzaoVar.zzeg;
            if (uri2 == null) {
                throw new IllegalArgumentException("Must pass a valid SharedPreferences file name or ContentProvider URI");
            }
        }
        str3 = zzaoVar.zzef;
        if (str3 != null) {
            uri = zzaoVar.zzeg;
            if (uri != null) {
                throw new IllegalArgumentException("Must pass one of SharedPreferences file name or ContentProvider URI");
            }
        }
        this.zzdr = zzaoVar;
        str4 = zzaoVar.zzeh;
        String valueOf = String.valueOf(str4);
        String valueOf2 = String.valueOf(str);
        this.zzdt = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        str5 = zzaoVar.zzei;
        String valueOf3 = String.valueOf(str5);
        String valueOf4 = String.valueOf(str);
        this.zzds = valueOf4.length() != 0 ? valueOf3.concat(valueOf4) : new String(valueOf3);
        this.zzdu = t;
    }

    public /* synthetic */ zzae(zzao zzaoVar, String str, Object obj, zzai zzaiVar) {
        this(zzaoVar, str, obj);
    }

    public static void maybeInit(Context context) {
        Context applicationContext;
        if (zzh == null) {
            synchronized (zzdn) {
                if ((Build.VERSION.SDK_INT < 24 || !context.isDeviceProtectedStorage()) && (applicationContext = context.getApplicationContext()) != null) {
                    context = applicationContext;
                }
                if (zzh != context) {
                    zzdp = null;
                }
                zzh = context;
            }
        }
    }

    public static <T> zzae<T> zza(zzao zzaoVar, String str, T t, zzan<T> zzanVar) {
        return new zzal(zzaoVar, str, t, zzanVar);
    }

    public static zzae<String> zza(zzao zzaoVar, String str, String str2) {
        return new zzak(zzaoVar, str, str2);
    }

    public static zzae<Boolean> zza(zzao zzaoVar, String str, boolean z) {
        return new zzaj(zzaoVar, str, Boolean.valueOf(z));
    }

    private static <V> V zza(zzam<V> zzamVar) {
        try {
            return zzamVar.zzp();
        } catch (SecurityException unused) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return zzamVar.zzp();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public static boolean zza(String str, boolean z) {
        if (zzn()) {
            return ((Boolean) zza(new zzam(str, false) { // from class: com.google.android.gms.internal.clearcut.zzah
                private final String zzea;
                private final boolean zzeb = false;

                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    this.zzea = str;
                }

                @Override // com.google.android.gms.internal.clearcut.zzam
                public final Object zzp() {
                    Boolean valueOf;
                    valueOf = Boolean.valueOf(zzy.zza(zzae.zzh.getContentResolver(), this.zzea, this.zzeb));
                    return valueOf;
                }
            })).booleanValue();
        }
        return false;
    }

    @Nullable
    @TargetApi(24)
    private final T zzl() {
        Uri uri;
        String str;
        boolean z;
        String str2;
        Uri uri2;
        if (!zza("gms:phenotype:phenotype_flag:debug_bypass_phenotype", false)) {
            uri = this.zzdr.zzeg;
            if (uri != null) {
                if (this.zzdw == null) {
                    ContentResolver contentResolver = zzh.getContentResolver();
                    uri2 = this.zzdr.zzeg;
                    this.zzdw = zzab.zza(contentResolver, uri2);
                }
                String str3 = (String) zza(new zzam(this, this.zzdw) { // from class: com.google.android.gms.internal.clearcut.zzaf
                    private final zzae zzdy;
                    private final zzab zzdz;

                    /* JADX INFO: Access modifiers changed from: package-private */
                    {
                        this.zzdy = this;
                        this.zzdz = r2;
                    }

                    @Override // com.google.android.gms.internal.clearcut.zzam
                    public final Object zzp() {
                        return this.zzdz.zzg().get(this.zzdy.zzds);
                    }
                });
                if (str3 != null) {
                    return zzb(str3);
                }
            } else {
                str = this.zzdr.zzef;
                if (str != null) {
                    if (Build.VERSION.SDK_INT < 24 || zzh.isDeviceProtectedStorage()) {
                        z = true;
                    } else {
                        if (zzdq == null || !zzdq.booleanValue()) {
                            zzdq = Boolean.valueOf(((UserManager) zzh.getSystemService(UserManager.class)).isUserUnlocked());
                        }
                        z = zzdq.booleanValue();
                    }
                    if (!z) {
                        return null;
                    }
                    if (this.zzdx == null) {
                        Context context = zzh;
                        str2 = this.zzdr.zzef;
                        this.zzdx = context.getSharedPreferences(str2, 0);
                    }
                    SharedPreferences sharedPreferences = this.zzdx;
                    if (sharedPreferences.contains(this.zzds)) {
                        return zza(sharedPreferences);
                    }
                }
            }
        } else {
            String valueOf = String.valueOf(this.zzds);
            Log.w("PhenotypeFlag", valueOf.length() != 0 ? "Bypass reading Phenotype values for flag: ".concat(valueOf) : new String("Bypass reading Phenotype values for flag: "));
        }
        return null;
    }

    @Nullable
    private final T zzm() {
        boolean z;
        String str;
        z = this.zzdr.zzej;
        if (z || !zzn() || (str = (String) zza(new zzam(this) { // from class: com.google.android.gms.internal.clearcut.zzag
            private final zzae zzdy;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zzdy = this;
            }

            @Override // com.google.android.gms.internal.clearcut.zzam
            public final Object zzp() {
                return this.zzdy.zzo();
            }
        })) == null) {
            return null;
        }
        return zzb(str);
    }

    private static boolean zzn() {
        if (zzdp == null) {
            Context context = zzh;
            boolean z = false;
            if (context == null) {
                return false;
            }
            if (PermissionChecker.checkCallingOrSelfPermission(context, "com.google.android.providers.gsf.permission.READ_GSERVICES") == 0) {
                z = true;
            }
            zzdp = Boolean.valueOf(z);
        }
        return zzdp.booleanValue();
    }

    public final T get() {
        boolean z;
        if (zzh != null) {
            z = this.zzdr.zzek;
            if (z) {
                T zzm = zzm();
                if (zzm != null) {
                    return zzm;
                }
                T zzl = zzl();
                if (zzl != null) {
                    return zzl;
                }
            } else {
                T zzl2 = zzl();
                if (zzl2 != null) {
                    return zzl2;
                }
                T zzm2 = zzm();
                if (zzm2 != null) {
                    return zzm2;
                }
            }
            return this.zzdu;
        }
        throw new IllegalStateException("Must call PhenotypeFlag.init() first");
    }

    protected abstract T zza(SharedPreferences sharedPreferences);

    public abstract T zzb(String str);

    public final /* synthetic */ String zzo() {
        return zzy.zza(zzh.getContentResolver(), this.zzdt, (String) null);
    }
}
