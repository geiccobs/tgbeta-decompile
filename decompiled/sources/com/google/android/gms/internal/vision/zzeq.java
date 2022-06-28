package com.google.android.gms.internal.vision;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public final class zzeq {
    public static Object zza(Object obj, int i) {
        if (obj == null) {
            StringBuilder sb = new StringBuilder(20);
            sb.append("at index ");
            sb.append(i);
            throw new NullPointerException(sb.toString());
        }
        return obj;
    }
}
