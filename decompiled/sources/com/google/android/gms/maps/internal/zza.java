package com.google.android.gms.maps.internal;
/* compiled from: com.google.android.gms:play-services-maps@@17.0.1 */
/* loaded from: classes.dex */
public final class zza {
    public static Boolean zza(byte b) {
        if (b != 0) {
            if (b == 1) {
                return Boolean.TRUE;
            }
            return null;
        }
        return Boolean.FALSE;
    }

    public static byte zzb(Boolean bool) {
        if (bool != null) {
            return !bool.booleanValue() ? (byte) 0 : (byte) 1;
        }
        return (byte) -1;
    }
}
