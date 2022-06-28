package com.google.android.gms.wearable;

import java.io.IOException;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
/* loaded from: classes3.dex */
public class ChannelIOException extends IOException {
    private final int zza;
    private final int zzb;

    public ChannelIOException(String message, int closeReason, int appSpecificErrorCode) {
        super(message);
        this.zza = closeReason;
        this.zzb = appSpecificErrorCode;
    }

    public int getAppSpecificErrorCode() {
        return this.zzb;
    }

    public int getCloseReason() {
        return this.zza;
    }
}
