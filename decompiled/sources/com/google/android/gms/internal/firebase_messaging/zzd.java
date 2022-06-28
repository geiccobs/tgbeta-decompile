package com.google.android.gms.internal.firebase_messaging;

import com.google.firebase.encoders.config.Configurator;
import com.google.firebase.encoders.config.EncoderConfig;
import com.google.firebase.messaging.reporting.MessagingClientEvent;
import com.google.firebase.messaging.reporting.MessagingClientEventExtension;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes3.dex */
public final class zzd implements Configurator {
    public static final Configurator zza = new zzd();

    private zzd() {
    }

    @Override // com.google.firebase.encoders.config.Configurator
    public final void configure(EncoderConfig<?> encoderConfig) {
        encoderConfig.registerEncoder(zze.class, zzc.zza);
        encoderConfig.registerEncoder(MessagingClientEventExtension.class, zzb.zza);
        encoderConfig.registerEncoder(MessagingClientEvent.class, zza.zza);
    }
}
