package com.google.android.gms.internal.vision;

import java.io.IOException;
/* compiled from: com.google.android.gms:play-services-vision-common@@19.1.3 */
/* loaded from: classes3.dex */
public class zzjk extends IOException {
    private zzkk zza = null;

    public zzjk(String str) {
        super(str);
    }

    public final zzjk zza(zzkk zzkkVar) {
        this.zza = zzkkVar;
        return this;
    }

    public static zzjk zza() {
        return new zzjk("While parsing a protocol message, the input ended unexpectedly in the middle of a field.  This could mean either that the input has been truncated or that an embedded message misreported its own length.");
    }

    public static zzjk zzb() {
        return new zzjk("CodedInputStream encountered an embedded string or message which claimed to have negative size.");
    }

    public static zzjk zzc() {
        return new zzjk("CodedInputStream encountered a malformed varint.");
    }

    public static zzjk zzd() {
        return new zzjk("Protocol message contained an invalid tag (zero).");
    }

    public static zzjk zze() {
        return new zzjk("Protocol message end-group tag did not match expected tag.");
    }

    public static zzjn zzf() {
        return new zzjn("Protocol message tag had invalid wire type.");
    }

    public static zzjk zzg() {
        return new zzjk("Failed to parse the message.");
    }

    public static zzjk zzh() {
        return new zzjk("Protocol message had invalid UTF-8.");
    }
}
