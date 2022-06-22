package com.google.android.gms.internal.vision;

import java.util.List;
import java.util.Map;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes.dex */
public final class zzkp {
    public static String zza(zzkk zzkkVar, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("# ");
        sb.append(str);
        zza(zzkkVar, sb, 0);
        return sb.toString();
    }

    /* JADX WARN: Code restructure failed: missing block: B:83:0x01e5, code lost:
        if (((java.lang.Boolean) r6).booleanValue() == false) goto L84;
     */
    /* JADX WARN: Code restructure failed: missing block: B:84:0x01e7, code lost:
        r4 = true;
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x01f8, code lost:
        if (((java.lang.Integer) r6).intValue() == 0) goto L84;
     */
    /* JADX WARN: Code restructure failed: missing block: B:93:0x0209, code lost:
        if (((java.lang.Float) r6).floatValue() == 0.0f) goto L84;
     */
    /* JADX WARN: Code restructure failed: missing block: B:97:0x021b, code lost:
        if (((java.lang.Double) r6).doubleValue() == 0.0d) goto L84;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void zza(com.google.android.gms.internal.vision.zzkk r13, java.lang.StringBuilder r14, int r15) {
        /*
            Method dump skipped, instructions count: 695
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.internal.vision.zzkp.zza(com.google.android.gms.internal.vision.zzkk, java.lang.StringBuilder, int):void");
    }

    public static final void zza(StringBuilder sb, int i, String str, Object obj) {
        if (obj instanceof List) {
            for (Object obj2 : (List) obj) {
                zza(sb, i, str, obj2);
            }
        } else if (obj instanceof Map) {
            for (Map.Entry entry : ((Map) obj).entrySet()) {
                zza(sb, i, str, entry);
            }
        } else {
            sb.append('\n');
            int i2 = 0;
            for (int i3 = 0; i3 < i; i3++) {
                sb.append(' ');
            }
            sb.append(str);
            if (obj instanceof String) {
                sb.append(": \"");
                sb.append(zzlq.zza(zzht.zza((String) obj)));
                sb.append('\"');
            } else if (obj instanceof zzht) {
                sb.append(": \"");
                sb.append(zzlq.zza((zzht) obj));
                sb.append('\"');
            } else if (obj instanceof zzjb) {
                sb.append(" {");
                zza((zzjb) obj, sb, i + 2);
                sb.append("\n");
                while (i2 < i) {
                    sb.append(' ');
                    i2++;
                }
                sb.append("}");
            } else if (obj instanceof Map.Entry) {
                sb.append(" {");
                Map.Entry entry2 = (Map.Entry) obj;
                int i4 = i + 2;
                zza(sb, i4, "key", entry2.getKey());
                zza(sb, i4, "value", entry2.getValue());
                sb.append("\n");
                while (i2 < i) {
                    sb.append(' ');
                    i2++;
                }
                sb.append("}");
            } else {
                sb.append(": ");
                sb.append(obj.toString());
            }
        }
    }

    private static final String zza(String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (Character.isUpperCase(charAt)) {
                sb.append("_");
            }
            sb.append(Character.toLowerCase(charAt));
        }
        return sb.toString();
    }
}
