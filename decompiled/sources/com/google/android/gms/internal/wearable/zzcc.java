package com.google.android.gms.internal.wearable;

import java.io.IOException;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public class zzcc extends IOException {
    private zzcx zza = null;

    public zzcc(IOException iOException) {
        super(iOException.getMessage(), iOException);
    }

    public static zzcc zzb() {
        return new zzcc("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either that the input has been truncated or that an embedded message misreported its own length.");
    }

    public static zzcc zzc() {
        return new zzcc("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
    }

    public static zzcc zzd() {
        return new zzcc("Protocol message contained an invalid tag (zero).");
    }

    public static zzcb zze() {
        return new zzcb("Protocol message tag had invalid wire type.");
    }

    public static zzcc zzf() {
        return new zzcc("Failed to parse the message.");
    }

    public static zzcc zzg() {
        return new zzcc("Protocol message had invalid UTF-8.");
    }

    public final zzcc zza(zzcx zzcxVar) {
        this.zza = zzcxVar;
        return this;
    }

    public zzcc(String str) {
        super(str);
    }
}
