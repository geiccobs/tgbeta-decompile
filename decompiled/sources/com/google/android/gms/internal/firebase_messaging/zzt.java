package com.google.android.gms.internal.firebase_messaging;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes3.dex */
public final class zzt {
    static final zzn zza;

    /* JADX WARN: Removed duplicated region for block: B:20:0x0083 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0084  */
    static {
        /*
            r0 = 0
            java.lang.String r1 = "android.os.Build$VERSION"
            java.lang.Class r1 = java.lang.Class.forName(r1)     // Catch: java.lang.Throwable -> L15 java.lang.Exception -> L17
            java.lang.String r2 = "SDK_INT"
            java.lang.reflect.Field r1 = r1.getField(r2)     // Catch: java.lang.Throwable -> L15 java.lang.Exception -> L17
            java.lang.Object r1 = r1.get(r0)     // Catch: java.lang.Throwable -> L15 java.lang.Exception -> L17
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch: java.lang.Throwable -> L15 java.lang.Exception -> L17
            r0 = r1
            goto L24
        L15:
            r1 = move-exception
            goto L49
        L17:
            r1 = move-exception
            java.io.PrintStream r2 = java.lang.System.err     // Catch: java.lang.Throwable -> L15
            java.lang.String r3 = "Failed to retrieve value from android.os.Build$VERSION.SDK_INT due to the following exception."
            r2.println(r3)     // Catch: java.lang.Throwable -> L15
            java.io.PrintStream r2 = java.lang.System.err     // Catch: java.lang.Throwable -> L15
            r1.printStackTrace(r2)     // Catch: java.lang.Throwable -> L15
        L24:
            if (r0 == 0) goto L34
            int r1 = r0.intValue()     // Catch: java.lang.Throwable -> L48
            r2 = 19
            if (r1 < r2) goto L34
            com.google.android.gms.internal.firebase_messaging.zzs r1 = new com.google.android.gms.internal.firebase_messaging.zzs     // Catch: java.lang.Throwable -> L48
            r1.<init>()     // Catch: java.lang.Throwable -> L48
            goto L47
        L34:
            java.lang.String r1 = "com.google.devtools.build.android.desugar.runtime.twr_disable_mimic"
            boolean r1 = java.lang.Boolean.getBoolean(r1)     // Catch: java.lang.Throwable -> L48
            if (r1 != 0) goto L42
            com.google.android.gms.internal.firebase_messaging.zzq r1 = new com.google.android.gms.internal.firebase_messaging.zzq     // Catch: java.lang.Throwable -> L48
            r1.<init>()     // Catch: java.lang.Throwable -> L48
            goto L47
        L42:
            com.google.android.gms.internal.firebase_messaging.zzr r1 = new com.google.android.gms.internal.firebase_messaging.zzr     // Catch: java.lang.Throwable -> L48
            r1.<init>()     // Catch: java.lang.Throwable -> L48
        L47:
            goto L7f
        L48:
            r1 = move-exception
        L49:
            java.io.PrintStream r2 = java.lang.System.err
            java.lang.Class<com.google.android.gms.internal.firebase_messaging.zzr> r3 = com.google.android.gms.internal.firebase_messaging.zzr.class
            java.lang.String r3 = r3.getName()
            java.lang.String r4 = java.lang.String.valueOf(r3)
            int r4 = r4.length()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            int r4 = r4 + 133
            r5.<init>(r4)
            java.lang.String r4 = "An error has occurred when initializing the try-with-resources desuguring strategy. The default strategy "
            r5.append(r4)
            r5.append(r3)
            java.lang.String r3 = "will be used. The error is: "
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            r2.println(r3)
            java.io.PrintStream r2 = java.lang.System.err
            r1.printStackTrace(r2)
            com.google.android.gms.internal.firebase_messaging.zzr r1 = new com.google.android.gms.internal.firebase_messaging.zzr
            r1.<init>()
        L7f:
            com.google.android.gms.internal.firebase_messaging.zzt.zza = r1
            if (r0 != 0) goto L84
            return
        L84:
            r0.intValue()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.firebase_messaging.zzt.<clinit>():void");
    }

    public static void zza(Throwable th, Throwable th2) {
        zza.zza(th, th2);
    }
}
