package com.google.android.gms.internal.firebase_messaging;

import com.google.firebase.encoders.annotations.Encodable;
import com.google.firebase.messaging.reporting.MessagingClientEventExtension;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
@Encodable
/* loaded from: classes3.dex */
public abstract class zze {
    private static final zzae zza;

    static {
        zzad zzadVar = new zzad();
        zzd.zza.configure(zzadVar);
        zza = zzadVar.zza();
    }

    private zze() {
    }

    public static byte[] zza(Object obj) {
        zzae zzaeVar = zza;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            zzaeVar.zza(obj, byteArrayOutputStream);
        } catch (IOException e) {
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static void zzb(Object obj, OutputStream outputStream) throws IOException {
        zza.zza(obj, outputStream);
    }

    public abstract MessagingClientEventExtension zzc();
}
