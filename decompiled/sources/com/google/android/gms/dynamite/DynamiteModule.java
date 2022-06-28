package com.google.android.gms.dynamite;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.CrashUtils;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.microsoft.appcenter.Constants;
import dalvik.system.DelegateLastClassLoader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
/* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
/* loaded from: classes3.dex */
public final class DynamiteModule {
    private static Boolean zza;
    private static zzl zzb;
    private static zzn zzc;
    private static String zzd;
    private final Context zzj;
    private static int zze = -1;
    private static final ThreadLocal<zza> zzf = new ThreadLocal<>();
    private static final ThreadLocal<Long> zzg = new com.google.android.gms.dynamite.zza();
    private static final VersionPolicy.zzb zzh = new com.google.android.gms.dynamite.zzb();
    public static final VersionPolicy PREFER_REMOTE = new zze();
    public static final VersionPolicy PREFER_LOCAL = new zzd();
    public static final VersionPolicy PREFER_REMOTE_VERSION_NO_FORCE_STAGING = new zzg();
    public static final VersionPolicy PREFER_HIGHEST_OR_LOCAL_VERSION = new zzf();
    public static final VersionPolicy PREFER_HIGHEST_OR_LOCAL_VERSION_NO_FORCE_STAGING = new zzi();
    public static final VersionPolicy PREFER_HIGHEST_OR_REMOTE_VERSION = new zzh();
    private static final VersionPolicy zzi = new zzj();

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
    public static class DynamiteLoaderClassLoader {
        public static ClassLoader sClassLoader;
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
    public interface VersionPolicy {

        /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
        /* loaded from: classes3.dex */
        public static class zza {
            public int zza = 0;
            public int zzb = 0;
            public int zzc = 0;
        }

        /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
        /* loaded from: classes3.dex */
        public interface zzb {
            int zza(Context context, String str);

            int zza(Context context, String str, boolean z) throws LoadingException;
        }

        zza zza(Context context, String str, zzb zzbVar) throws LoadingException;
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
    public static class zza {
        public Cursor zza;

        private zza() {
        }

        /* synthetic */ zza(com.google.android.gms.dynamite.zza zzaVar) {
            this();
        }
    }

    public static DynamiteModule load(Context context, VersionPolicy versionPolicy, String str) throws LoadingException {
        ThreadLocal<zza> threadLocal = zzf;
        zza zzaVar = threadLocal.get();
        zza zzaVar2 = new zza(null);
        threadLocal.set(zzaVar2);
        ThreadLocal<Long> threadLocal2 = zzg;
        long longValue = threadLocal2.get().longValue();
        try {
            threadLocal2.set(Long.valueOf(SystemClock.elapsedRealtime()));
            VersionPolicy.zza zza2 = versionPolicy.zza(context, str, zzh);
            int i = zza2.zza;
            int i2 = zza2.zzb;
            StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 68 + String.valueOf(str).length());
            sb.append("Considering local module ");
            sb.append(str);
            sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
            sb.append(i);
            sb.append(" and remote module ");
            sb.append(str);
            sb.append(Constants.COMMON_SCHEMA_PREFIX_SEPARATOR);
            sb.append(i2);
            Log.i("DynamiteModule", sb.toString());
            if (zza2.zzc == 0 || ((zza2.zzc == -1 && zza2.zza == 0) || (zza2.zzc == 1 && zza2.zzb == 0))) {
                int i3 = zza2.zza;
                int i4 = zza2.zzb;
                StringBuilder sb2 = new StringBuilder(91);
                sb2.append("No acceptable module found. Local version is ");
                sb2.append(i3);
                sb2.append(" and remote version is ");
                sb2.append(i4);
                sb2.append(".");
                throw new LoadingException(sb2.toString(), (com.google.android.gms.dynamite.zza) null);
            } else if (zza2.zzc == -1) {
                DynamiteModule zza3 = zza(context, str);
                if (longValue == 0) {
                    threadLocal2.remove();
                } else {
                    threadLocal2.set(Long.valueOf(longValue));
                }
                if (zzaVar2.zza != null) {
                    zzaVar2.zza.close();
                }
                threadLocal.set(zzaVar);
                return zza3;
            } else if (zza2.zzc == 1) {
                try {
                    DynamiteModule zza4 = zza(context, str, zza2.zzb);
                    if (longValue == 0) {
                        threadLocal2.remove();
                    } else {
                        threadLocal2.set(Long.valueOf(longValue));
                    }
                    if (zzaVar2.zza != null) {
                        zzaVar2.zza.close();
                    }
                    threadLocal.set(zzaVar);
                    return zza4;
                } catch (LoadingException e) {
                    String valueOf = String.valueOf(e.getMessage());
                    Log.w("DynamiteModule", valueOf.length() != 0 ? "Failed to load remote module: ".concat(valueOf) : new String("Failed to load remote module: "));
                    if (zza2.zza != 0 && versionPolicy.zza(context, str, new zzb(zza2.zza, 0)).zzc == -1) {
                        DynamiteModule zza5 = zza(context, str);
                        if (longValue == 0) {
                            zzg.remove();
                        } else {
                            zzg.set(Long.valueOf(longValue));
                        }
                        if (zzaVar2.zza != null) {
                            zzaVar2.zza.close();
                        }
                        zzf.set(zzaVar);
                        return zza5;
                    }
                    throw new LoadingException("Remote load failed. No local fallback found.", e, null);
                }
            } else {
                int i5 = zza2.zzc;
                StringBuilder sb3 = new StringBuilder(47);
                sb3.append("VersionPolicy returned invalid code:");
                sb3.append(i5);
                throw new LoadingException(sb3.toString(), (com.google.android.gms.dynamite.zza) null);
            }
        } catch (Throwable th) {
            if (longValue == 0) {
                zzg.remove();
            } else {
                zzg.set(Long.valueOf(longValue));
            }
            if (zzaVar2.zza != null) {
                zzaVar2.zza.close();
            }
            zzf.set(zzaVar);
            throw th;
        }
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
    public static class LoadingException extends Exception {
        private LoadingException(String str) {
            super(str);
        }

        private LoadingException(String str, Throwable th) {
            super(str, th);
        }

        /* synthetic */ LoadingException(String str, com.google.android.gms.dynamite.zza zzaVar) {
            this(str);
        }

        /* synthetic */ LoadingException(String str, Throwable th, com.google.android.gms.dynamite.zza zzaVar) {
            this(str, th);
        }
    }

    /* compiled from: com.google.android.gms:play-services-basement@@17.5.0 */
    /* loaded from: classes3.dex */
    public static class zzb implements VersionPolicy.zzb {
        private final int zza;
        private final int zzb = 0;

        public zzb(int i, int i2) {
            this.zza = i;
        }

        @Override // com.google.android.gms.dynamite.DynamiteModule.VersionPolicy.zzb
        public final int zza(Context context, String str, boolean z) {
            return 0;
        }

        @Override // com.google.android.gms.dynamite.DynamiteModule.VersionPolicy.zzb
        public final int zza(Context context, String str) {
            return this.zza;
        }
    }

    public static int getLocalVersion(Context context, String str) {
        try {
            ClassLoader classLoader = context.getApplicationContext().getClassLoader();
            StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 61);
            sb.append("com.google.android.gms.dynamite.descriptors.");
            sb.append(str);
            sb.append(".ModuleDescriptor");
            Class<?> loadClass = classLoader.loadClass(sb.toString());
            Field declaredField = loadClass.getDeclaredField("MODULE_ID");
            Field declaredField2 = loadClass.getDeclaredField("MODULE_VERSION");
            if (!Objects.equal(declaredField.get(null), str)) {
                String valueOf = String.valueOf(declaredField.get(null));
                StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf).length() + 51 + String.valueOf(str).length());
                sb2.append("Module descriptor id '");
                sb2.append(valueOf);
                sb2.append("' didn't match expected id '");
                sb2.append(str);
                sb2.append("'");
                Log.e("DynamiteModule", sb2.toString());
                return 0;
            }
            return declaredField2.getInt(null);
        } catch (ClassNotFoundException e) {
            StringBuilder sb3 = new StringBuilder(String.valueOf(str).length() + 45);
            sb3.append("Local module descriptor class for ");
            sb3.append(str);
            sb3.append(" not found.");
            Log.w("DynamiteModule", sb3.toString());
            return 0;
        } catch (Exception e2) {
            String valueOf2 = String.valueOf(e2.getMessage());
            Log.e("DynamiteModule", valueOf2.length() != 0 ? "Failed to load module descriptor class: ".concat(valueOf2) : new String("Failed to load module descriptor class: "));
            return 0;
        }
    }

    public static int zza(Context context, String str, boolean z) {
        Field declaredField;
        ClassLoader classLoader;
        try {
            synchronized (DynamiteModule.class) {
                Boolean bool = zza;
                if (bool == null) {
                    try {
                        declaredField = context.getApplicationContext().getClassLoader().loadClass(DynamiteLoaderClassLoader.class.getName()).getDeclaredField("sClassLoader");
                    } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
                        String valueOf = String.valueOf(e);
                        StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 30);
                        sb.append("Failed to load module via V2: ");
                        sb.append(valueOf);
                        Log.w("DynamiteModule", sb.toString());
                        bool = Boolean.FALSE;
                    }
                    synchronized (declaredField.getDeclaringClass()) {
                        ClassLoader classLoader2 = (ClassLoader) declaredField.get(null);
                        if (classLoader2 != null) {
                            if (classLoader2 == ClassLoader.getSystemClassLoader()) {
                                bool = Boolean.FALSE;
                            } else {
                                try {
                                    zza(classLoader2);
                                } catch (LoadingException e2) {
                                }
                                bool = Boolean.TRUE;
                            }
                        } else if ("com.google.android.gms".equals(context.getApplicationContext().getPackageName())) {
                            declaredField.set(null, ClassLoader.getSystemClassLoader());
                            bool = Boolean.FALSE;
                        } else {
                            try {
                                int zzc2 = zzc(context, str, z);
                                String str2 = zzd;
                                if (str2 != null && !str2.isEmpty()) {
                                    if (Build.VERSION.SDK_INT >= 29) {
                                        classLoader = new DelegateLastClassLoader((String) Preconditions.checkNotNull(zzd), ClassLoader.getSystemClassLoader());
                                    } else {
                                        classLoader = new zzc((String) Preconditions.checkNotNull(zzd), ClassLoader.getSystemClassLoader());
                                    }
                                    zza(classLoader);
                                    declaredField.set(null, classLoader);
                                    zza = Boolean.TRUE;
                                    return zzc2;
                                }
                                return zzc2;
                            } catch (LoadingException e3) {
                                declaredField.set(null, ClassLoader.getSystemClassLoader());
                                bool = Boolean.FALSE;
                            }
                        }
                        zza = bool;
                    }
                }
                if (bool.booleanValue()) {
                    try {
                        return zzc(context, str, z);
                    } catch (LoadingException e4) {
                        String valueOf2 = String.valueOf(e4.getMessage());
                        Log.w("DynamiteModule", valueOf2.length() != 0 ? "Failed to retrieve remote module version: ".concat(valueOf2) : new String("Failed to retrieve remote module version: "));
                        return 0;
                    }
                }
                return zzb(context, str, z);
            }
        } catch (Throwable th) {
            CrashUtils.addDynamiteErrorToDropBox(context, th);
            throw th;
        }
    }

    private static int zzb(Context context, String str, boolean z) {
        RemoteException e;
        zzl zza2 = zza(context);
        if (zza2 == null) {
            return 0;
        }
        Cursor cursor = null;
        try {
            try {
                int zzb2 = zza2.zzb();
                if (zzb2 >= 3) {
                    Cursor cursor2 = (Cursor) ObjectWrapper.unwrap(zza2.zza(ObjectWrapper.wrap(context), str, z, zzg.get().longValue()));
                    if (cursor2 != null) {
                        try {
                            if (cursor2.moveToFirst()) {
                                int i = cursor2.getInt(0);
                                if (i <= 0 || !zza(cursor2)) {
                                    cursor = cursor2;
                                }
                                if (cursor != null) {
                                    cursor.close();
                                }
                                return i;
                            }
                        } catch (RemoteException e2) {
                            e = e2;
                            cursor = cursor2;
                            String valueOf = String.valueOf(e.getMessage());
                            Log.w("DynamiteModule", valueOf.length() != 0 ? "Failed to retrieve remote module version: ".concat(valueOf) : new String("Failed to retrieve remote module version: "));
                            if (cursor != null) {
                                cursor.close();
                            }
                            return 0;
                        } catch (Throwable th) {
                            th = th;
                            cursor = cursor2;
                            if (cursor != null) {
                                cursor.close();
                            }
                            throw th;
                        }
                    }
                    Log.w("DynamiteModule", "Failed to retrieve remote module version.");
                    if (cursor2 != null) {
                        cursor2.close();
                    }
                    return 0;
                } else if (zzb2 == 2) {
                    Log.w("DynamiteModule", "IDynamite loader version = 2, no high precision latency measurement.");
                    return zza2.zzb(ObjectWrapper.wrap(context), str, z);
                } else {
                    Log.w("DynamiteModule", "IDynamite loader version < 2, falling back to getModuleVersion2");
                    return zza2.zza(ObjectWrapper.wrap(context), str, z);
                }
            } catch (RemoteException e3) {
                e = e3;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x0076, code lost:
        if (zza(r10) != false) goto L23;
     */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00b0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static int zzc(android.content.Context r10, java.lang.String r11, boolean r12) throws com.google.android.gms.dynamite.DynamiteModule.LoadingException {
        /*
            r0 = 0
            java.lang.ThreadLocal<java.lang.Long> r1 = com.google.android.gms.dynamite.DynamiteModule.zzg     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            java.lang.Object r1 = r1.get()     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            java.lang.Long r1 = (java.lang.Long) r1     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            long r1 = r1.longValue()     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            android.content.ContentResolver r3 = r10.getContentResolver()     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            if (r12 == 0) goto L18
            java.lang.String r10 = "api_force_staging"
            goto L1a
        L18:
            java.lang.String r10 = "api"
        L1a:
            android.net.Uri$Builder r12 = new android.net.Uri$Builder     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            r12.<init>()     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            java.lang.String r4 = "content"
            android.net.Uri$Builder r12 = r12.scheme(r4)     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            java.lang.String r4 = "com.google.android.gms.chimera"
            android.net.Uri$Builder r12 = r12.authority(r4)     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            android.net.Uri$Builder r10 = r12.path(r10)     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            android.net.Uri$Builder r10 = r10.appendPath(r11)     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            java.lang.String r11 = "requestStartTime"
            java.lang.String r12 = java.lang.String.valueOf(r1)     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            android.net.Uri$Builder r10 = r10.appendQueryParameter(r11, r12)     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            android.net.Uri r4 = r10.build()     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            android.database.Cursor r10 = r3.query(r4, r5, r6, r7, r8)     // Catch: java.lang.Throwable -> L9b java.lang.Exception -> L9d
            if (r10 == 0) goto L83
            boolean r11 = r10.moveToFirst()     // Catch: java.lang.Throwable -> L92 java.lang.Exception -> L96
            if (r11 == 0) goto L83
            r11 = 0
            int r11 = r10.getInt(r11)     // Catch: java.lang.Throwable -> L92 java.lang.Exception -> L96
            if (r11 <= 0) goto L7c
            java.lang.Class<com.google.android.gms.dynamite.DynamiteModule> r12 = com.google.android.gms.dynamite.DynamiteModule.class
            monitor-enter(r12)     // Catch: java.lang.Throwable -> L92 java.lang.Exception -> L96
            r1 = 2
            java.lang.String r1 = r10.getString(r1)     // Catch: java.lang.Throwable -> L79
            com.google.android.gms.dynamite.DynamiteModule.zzd = r1     // Catch: java.lang.Throwable -> L79
            java.lang.String r1 = "loaderVersion"
            int r1 = r10.getColumnIndex(r1)     // Catch: java.lang.Throwable -> L79
            if (r1 < 0) goto L71
            int r1 = r10.getInt(r1)     // Catch: java.lang.Throwable -> L79
            com.google.android.gms.dynamite.DynamiteModule.zze = r1     // Catch: java.lang.Throwable -> L79
        L71:
            monitor-exit(r12)     // Catch: java.lang.Throwable -> L79
            boolean r12 = zza(r10)     // Catch: java.lang.Throwable -> L92 java.lang.Exception -> L96
            if (r12 == 0) goto L7c
            goto L7d
        L79:
            r11 = move-exception
            monitor-exit(r12)     // Catch: java.lang.Throwable -> L79
            throw r11     // Catch: java.lang.Throwable -> L92 java.lang.Exception -> L96
        L7c:
            r0 = r10
        L7d:
            if (r0 == 0) goto L82
            r0.close()
        L82:
            return r11
        L83:
            java.lang.String r11 = "DynamiteModule"
            java.lang.String r12 = "Failed to retrieve remote module version."
            android.util.Log.w(r11, r12)     // Catch: java.lang.Throwable -> L92 java.lang.Exception -> L96
            com.google.android.gms.dynamite.DynamiteModule$LoadingException r11 = new com.google.android.gms.dynamite.DynamiteModule$LoadingException     // Catch: java.lang.Throwable -> L92 java.lang.Exception -> L96
            java.lang.String r12 = "Failed to connect to dynamite module ContentResolver."
            r11.<init>(r12, r0)     // Catch: java.lang.Throwable -> L92 java.lang.Exception -> L96
            throw r11     // Catch: java.lang.Throwable -> L92 java.lang.Exception -> L96
        L92:
            r11 = move-exception
            r0 = r10
            r10 = r11
            goto Lae
        L96:
            r11 = move-exception
            r9 = r11
            r11 = r10
            r10 = r9
            goto L9f
        L9b:
            r10 = move-exception
            goto Lae
        L9d:
            r10 = move-exception
            r11 = r0
        L9f:
            boolean r12 = r10 instanceof com.google.android.gms.dynamite.DynamiteModule.LoadingException     // Catch: java.lang.Throwable -> Lac
            if (r12 == 0) goto La4
            throw r10     // Catch: java.lang.Throwable -> Lac
        La4:
            com.google.android.gms.dynamite.DynamiteModule$LoadingException r12 = new com.google.android.gms.dynamite.DynamiteModule$LoadingException     // Catch: java.lang.Throwable -> Lac
            java.lang.String r1 = "V2 version check failed"
            r12.<init>(r1, r10, r0)     // Catch: java.lang.Throwable -> Lac
            throw r12     // Catch: java.lang.Throwable -> Lac
        Lac:
            r10 = move-exception
            r0 = r11
        Lae:
            if (r0 == 0) goto Lb3
            r0.close()
        Lb3:
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.dynamite.DynamiteModule.zzc(android.content.Context, java.lang.String, boolean):int");
    }

    private static boolean zza(Cursor cursor) {
        zza zzaVar = zzf.get();
        if (zzaVar != null && zzaVar.zza == null) {
            zzaVar.zza = cursor;
            return true;
        }
        return false;
    }

    public static int getRemoteVersion(Context context, String str) {
        return zza(context, str, false);
    }

    private static DynamiteModule zza(Context context, String str) {
        String valueOf = String.valueOf(str);
        Log.i("DynamiteModule", valueOf.length() != 0 ? "Selected local version of ".concat(valueOf) : new String("Selected local version of "));
        return new DynamiteModule(context.getApplicationContext());
    }

    private static DynamiteModule zza(Context context, String str, int i) throws LoadingException {
        Boolean bool;
        IObjectWrapper iObjectWrapper;
        try {
            synchronized (DynamiteModule.class) {
                bool = zza;
            }
            if (bool == null) {
                throw new LoadingException("Failed to determine which loading route to use.", (com.google.android.gms.dynamite.zza) null);
            }
            if (bool.booleanValue()) {
                return zzb(context, str, i);
            }
            StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 51);
            sb.append("Selected remote version of ");
            sb.append(str);
            sb.append(", version >= ");
            sb.append(i);
            Log.i("DynamiteModule", sb.toString());
            zzl zza2 = zza(context);
            if (zza2 == null) {
                throw new LoadingException("Failed to create IDynamiteLoader.", (com.google.android.gms.dynamite.zza) null);
            }
            int zzb2 = zza2.zzb();
            if (zzb2 >= 3) {
                zza zzaVar = zzf.get();
                if (zzaVar == null) {
                    throw new LoadingException("No cached result cursor holder", (com.google.android.gms.dynamite.zza) null);
                }
                iObjectWrapper = zza2.zza(ObjectWrapper.wrap(context), str, i, ObjectWrapper.wrap(zzaVar.zza));
            } else if (zzb2 == 2) {
                Log.w("DynamiteModule", "IDynamite loader version = 2");
                iObjectWrapper = zza2.zzb(ObjectWrapper.wrap(context), str, i);
            } else {
                Log.w("DynamiteModule", "Dynamite loader version < 2, falling back to createModuleContext");
                iObjectWrapper = zza2.zza(ObjectWrapper.wrap(context), str, i);
            }
            if (ObjectWrapper.unwrap(iObjectWrapper) == null) {
                throw new LoadingException("Failed to load remote module.", (com.google.android.gms.dynamite.zza) null);
            }
            return new DynamiteModule((Context) ObjectWrapper.unwrap(iObjectWrapper));
        } catch (RemoteException e) {
            throw new LoadingException("Failed to load remote module.", e, null);
        } catch (LoadingException e2) {
            throw e2;
        } catch (Throwable th) {
            CrashUtils.addDynamiteErrorToDropBox(context, th);
            throw new LoadingException("Failed to load remote module.", th, null);
        }
    }

    private static zzl zza(Context context) {
        zzl zzlVar;
        synchronized (DynamiteModule.class) {
            zzl zzlVar2 = zzb;
            if (zzlVar2 != null) {
                return zzlVar2;
            }
            try {
                IBinder iBinder = (IBinder) context.createPackageContext("com.google.android.gms", 3).getClassLoader().loadClass("com.google.android.gms.chimera.container.DynamiteLoaderImpl").newInstance();
                if (iBinder == null) {
                    zzlVar = null;
                } else {
                    IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.dynamite.IDynamiteLoader");
                    if (queryLocalInterface instanceof zzl) {
                        zzlVar = (zzl) queryLocalInterface;
                    } else {
                        zzlVar = new zzk(iBinder);
                    }
                }
            } catch (Exception e) {
                String valueOf = String.valueOf(e.getMessage());
                Log.e("DynamiteModule", valueOf.length() != 0 ? "Failed to load IDynamiteLoader from GmsCore: ".concat(valueOf) : new String("Failed to load IDynamiteLoader from GmsCore: "));
            }
            if (zzlVar == null) {
                return null;
            }
            zzb = zzlVar;
            return zzlVar;
        }
    }

    public final Context getModuleContext() {
        return this.zzj;
    }

    private static DynamiteModule zzb(Context context, String str, int i) throws LoadingException, RemoteException {
        zzn zznVar;
        IObjectWrapper iObjectWrapper;
        StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 51);
        sb.append("Selected remote version of ");
        sb.append(str);
        sb.append(", version >= ");
        sb.append(i);
        Log.i("DynamiteModule", sb.toString());
        synchronized (DynamiteModule.class) {
            zznVar = zzc;
        }
        if (zznVar == null) {
            throw new LoadingException("DynamiteLoaderV2 was not cached.", (com.google.android.gms.dynamite.zza) null);
        }
        zza zzaVar = zzf.get();
        if (zzaVar == null || zzaVar.zza == null) {
            throw new LoadingException("No result cursor", (com.google.android.gms.dynamite.zza) null);
        }
        Context applicationContext = context.getApplicationContext();
        Cursor cursor = zzaVar.zza;
        ObjectWrapper.wrap(null);
        if (zza().booleanValue()) {
            Log.v("DynamiteModule", "Dynamite loader version >= 2, using loadModule2NoCrashUtils");
            iObjectWrapper = zznVar.zzb(ObjectWrapper.wrap(applicationContext), str, i, ObjectWrapper.wrap(cursor));
        } else {
            Log.w("DynamiteModule", "Dynamite loader version < 2, falling back to loadModule2");
            iObjectWrapper = zznVar.zza(ObjectWrapper.wrap(applicationContext), str, i, ObjectWrapper.wrap(cursor));
        }
        Context context2 = (Context) ObjectWrapper.unwrap(iObjectWrapper);
        if (context2 == null) {
            throw new LoadingException("Failed to get module context", (com.google.android.gms.dynamite.zza) null);
        }
        return new DynamiteModule(context2);
    }

    private static Boolean zza() {
        Boolean valueOf;
        synchronized (DynamiteModule.class) {
            valueOf = Boolean.valueOf(zze >= 2);
        }
        return valueOf;
    }

    private static void zza(ClassLoader classLoader) throws LoadingException {
        zzn zznVar;
        try {
            IBinder iBinder = (IBinder) classLoader.loadClass("com.google.android.gms.dynamiteloader.DynamiteLoaderV2").getConstructor(new Class[0]).newInstance(new Object[0]);
            if (iBinder == null) {
                zznVar = null;
            } else {
                IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.dynamite.IDynamiteLoaderV2");
                if (queryLocalInterface instanceof zzn) {
                    zznVar = (zzn) queryLocalInterface;
                } else {
                    zznVar = new zzm(iBinder);
                }
            }
            zzc = zznVar;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new LoadingException("Failed to instantiate dynamite loader", e, null);
        }
    }

    public final IBinder instantiate(String str) throws LoadingException {
        try {
            return (IBinder) this.zzj.getClassLoader().loadClass(str).newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            String valueOf = String.valueOf(str);
            throw new LoadingException(valueOf.length() != 0 ? "Failed to instantiate module class: ".concat(valueOf) : new String("Failed to instantiate module class: "), e, null);
        }
    }

    private DynamiteModule(Context context) {
        this.zzj = (Context) Preconditions.checkNotNull(context);
    }
}
