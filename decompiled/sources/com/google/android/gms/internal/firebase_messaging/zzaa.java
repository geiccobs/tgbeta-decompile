package com.google.android.gms.internal.firebase_messaging;

import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ObjectEncoderContext;
import java.util.Map;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes3.dex */
final /* synthetic */ class zzaa implements ObjectEncoder {
    static final ObjectEncoder zza = new zzaa();

    private zzaa() {
    }

    @Override // com.google.firebase.encoders.Encoder
    public final void encode(Object obj, ObjectEncoderContext objectEncoderContext) {
        zzab.zzg((Map.Entry) obj, objectEncoderContext);
    }
}
