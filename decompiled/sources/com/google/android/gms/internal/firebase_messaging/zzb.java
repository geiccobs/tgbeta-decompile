package com.google.android.gms.internal.firebase_messaging;

import com.google.firebase.encoders.FieldDescriptor;
import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ObjectEncoderContext;
import com.google.firebase.messaging.reporting.MessagingClientEventExtension;
import java.io.IOException;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes3.dex */
final class zzb implements ObjectEncoder<MessagingClientEventExtension> {
    static final zzb zza = new zzb();
    private static final FieldDescriptor zzb;

    static {
        FieldDescriptor.Builder builder = FieldDescriptor.builder("messagingClientEvent");
        zzv zzvVar = new zzv();
        zzvVar.zza(1);
        zzb = builder.withProperty(zzvVar.zzb()).build();
    }

    private zzb() {
    }

    @Override // com.google.firebase.encoders.Encoder
    public final /* bridge */ /* synthetic */ void encode(Object obj, ObjectEncoderContext objectEncoderContext) throws IOException {
        objectEncoderContext.add(zzb, ((MessagingClientEventExtension) obj).getMessagingClientEventInternal());
    }
}
