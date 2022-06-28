package com.google.android.datatransport.runtime;

import com.google.android.datatransport.runtime.firebase.transport.ClientMetrics;
import com.google.firebase.encoders.annotations.Encodable;
import com.google.firebase.encoders.proto.ProtobufEncoder;
import java.io.IOException;
import java.io.OutputStream;
@Encodable
/* loaded from: classes3.dex */
public abstract class ProtoEncoderDoNotUse {
    private static final ProtobufEncoder ENCODER = ProtobufEncoder.builder().configureWith(AutoProtoEncoderDoNotUseEncoder.CONFIG).build();

    public abstract ClientMetrics getClientMetrics();

    private ProtoEncoderDoNotUse() {
    }

    public static byte[] encode(Object value) {
        return ENCODER.encode(value);
    }

    public static void encode(Object value, OutputStream output) throws IOException {
        ENCODER.encode(value, output);
    }
}
