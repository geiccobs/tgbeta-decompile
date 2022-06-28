package com.google.android.gms.phenotype;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.UserManager;
import android.util.Log;
import androidx.core.content.PermissionChecker;
import javax.annotation.Nullable;
@Deprecated
/* loaded from: classes.dex */
public abstract class PhenotypeFlag<T> {
    private static final Object zzak = new Object();
    private static Context zzal = null;
    private static boolean zzam = false;
    private static Boolean zzan = null;
    private final Factory zzao;
    final String zzap;
    private final String zzaq;
    private final T zzar;
    private T zzas;

    /* loaded from: classes3.dex */
    public static class Factory {
        private final String zzax;
        private final Uri zzay;
        private final String zzaz;
        private final String zzba;
        private final boolean zzbb;
        private final boolean zzbc;

        public Factory(Uri uri) {
            this(null, uri, "", "", false, false);
        }

        private Factory(String str, Uri uri, String str2, String str3, boolean z, boolean z2) {
            this.zzax = str;
            this.zzay = uri;
            this.zzaz = str2;
            this.zzba = str3;
            this.zzbb = z;
            this.zzbc = z2;
        }

        public PhenotypeFlag<String> createFlag(String str, String str2) {
            return PhenotypeFlag.zza(this, str, str2);
        }

        public Factory withGservicePrefix(String str) {
            boolean z = this.zzbb;
            if (!z) {
                return new Factory(this.zzax, this.zzay, str, this.zzba, z, this.zzbc);
            }
            throw new IllegalStateException("Cannot set GServices prefix and skip GServices");
        }

        public Factory withPhenotypePrefix(String str) {
            return new Factory(this.zzax, this.zzay, this.zzaz, str, this.zzbb, this.zzbc);
        }
    }

    /* loaded from: classes3.dex */
    public interface zza<V> {
        V zzh();
    }

    private PhenotypeFlag(Factory factory, String str, T t) {
        this.zzas = null;
        if (factory.zzax == null && factory.zzay == null) {
            throw new IllegalArgumentException("Must pass a valid SharedPreferences file name or ContentProvider URI");
        }
        if (factory.zzax != null && factory.zzay != null) {
            throw new IllegalArgumentException("Must pass one of SharedPreferences file name or ContentProvider URI");
        }
        this.zzao = factory;
        String valueOf = String.valueOf(factory.zzaz);
        String valueOf2 = String.valueOf(str);
        this.zzaq = valueOf2.length() != 0 ? valueOf.concat(valueOf2) : new String(valueOf);
        String valueOf3 = String.valueOf(factory.zzba);
        String valueOf4 = String.valueOf(str);
        this.zzap = valueOf4.length() != 0 ? valueOf3.concat(valueOf4) : new String(valueOf3);
        this.zzar = t;
    }

    public /* synthetic */ PhenotypeFlag(Factory factory, String str, Object obj, zzr zzrVar) {
        this(factory, str, obj);
    }

    public static void maybeInit(Context context) {
        Context applicationContext;
        com.google.android.gms.internal.phenotype.zzh.maybeInit(context);
        if (zzal == null) {
            com.google.android.gms.internal.phenotype.zzh.init(context);
            synchronized (zzak) {
                if ((Build.VERSION.SDK_INT < 24 || !context.isDeviceProtectedStorage()) && (applicationContext = context.getApplicationContext()) != null) {
                    context = applicationContext;
                }
                if (zzal != context) {
                    zzan = null;
                }
                zzal = context;
            }
            zzam = false;
        }
    }

    public static PhenotypeFlag<String> zza(Factory factory, String str, String str2) {
        return new zzs(factory, str, str2);
    }

    private static <V> V zza(zza<V> zzaVar) {
        try {
            return zzaVar.zzh();
        } catch (SecurityException e) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                return zzaVar.zzh();
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }

    public static boolean zza(String str, boolean z) {
        if (zzf()) {
            return ((Boolean) zza(new zza(str, false) { // from class: com.google.android.gms.phenotype.zzq
                private final String zzav;
                private final boolean zzaw = false;

                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    this.zzav = str;
                }

                @Override // com.google.android.gms.phenotype.PhenotypeFlag.zza
                public final Object zzh() {
                    Boolean valueOf;
                    valueOf = Boolean.valueOf(com.google.android.gms.internal.phenotype.zzf.zza(PhenotypeFlag.zzal.getContentResolver(), this.zzav, this.zzaw));
                    return valueOf;
                }
            })).booleanValue();
        }
        return false;
    }

    @Nullable
    private final T zzd() {
        if (zza("gms:phenotype:phenotype_flag:debug_bypass_phenotype", false)) {
            String valueOf = String.valueOf(this.zzap);
            Log.w("PhenotypeFlag", valueOf.length() != 0 ? "Bypass reading Phenotype values for flag: ".concat(valueOf) : new String("Bypass reading Phenotype values for flag: "));
        } else if (this.zzao.zzay != null) {
            String str = (String) zza(new zza(this, com.google.android.gms.phenotype.zza.zza(zzal.getContentResolver(), this.zzao.zzay)) { // from class: com.google.android.gms.phenotype.zzo
                private final PhenotypeFlag zzat;
                private final zza zzau;

                /* JADX INFO: Access modifiers changed from: package-private */
                {
                    this.zzat = this;
                    this.zzau = zza2;
                }

                @Override // com.google.android.gms.phenotype.PhenotypeFlag.zza
                public final Object zzh() {
                    return this.zzau.zza().get(this.zzat.zzap);
                }
            });
            if (str != null) {
                return zza(str);
            }
        } else if (this.zzao.zzax == null || (Build.VERSION.SDK_INT >= 24 && !zzal.isDeviceProtectedStorage() && !((UserManager) zzal.getSystemService(UserManager.class)).isUserUnlocked())) {
            return null;
        } else {
            SharedPreferences sharedPreferences = zzal.getSharedPreferences(this.zzao.zzax, 0);
            if (sharedPreferences.contains(this.zzap)) {
                return zza(sharedPreferences);
            }
        }
        return null;
    }

    @Nullable
    private final T zze() {
        String str;
        if (this.zzao.zzbb || !zzf() || (str = (String) zza(new zza(this) { // from class: com.google.android.gms.phenotype.zzp
            private final PhenotypeFlag zzat;

            /* JADX INFO: Access modifiers changed from: package-private */
            {
                this.zzat = this;
            }

            @Override // com.google.android.gms.phenotype.PhenotypeFlag.zza
            public final Object zzh() {
                return this.zzat.zzg();
            }
        })) == null) {
            return null;
        }
        return zza(str);
    }

    private static boolean zzf() {
        if (zzan == null) {
            Context context = zzal;
            boolean z = false;
            if (context == null) {
                return false;
            }
            if (PermissionChecker.checkCallingOrSelfPermission(context, "com.google.android.providers.gsf.permission.READ_GSERVICES") == 0) {
                z = true;
            }
            zzan = Boolean.valueOf(z);
        }
        return zzan.booleanValue();
    }

    public T get() {
        if (zzal != null) {
            if (this.zzao.zzbc) {
                T zze = zze();
                if (zze != null) {
                    return zze;
                }
                T zzd = zzd();
                if (zzd != null) {
                    return zzd;
                }
            } else {
                T zzd2 = zzd();
                if (zzd2 != null) {
                    return zzd2;
                }
                T zze2 = zze();
                if (zze2 != null) {
                    return zze2;
                }
            }
            return this.zzar;
        }
        throw new IllegalStateException("Must call PhenotypeFlag.init() first");
    }

    public abstract T zza(SharedPreferences sharedPreferences);

    public abstract T zza(String str);

    public final /* synthetic */ String zzg() {
        return com.google.android.gms.internal.phenotype.zzf.zza(zzal.getContentResolver(), this.zzaq, (String) null);
    }
}
