package com.google.firebase.encoders.proto;

import com.google.firebase.encoders.EncodingException;
import com.google.firebase.encoders.ObjectEncoder;
import com.google.firebase.encoders.ObjectEncoderContext;
import com.google.firebase.encoders.ValueEncoder;
import com.google.firebase.encoders.config.Configurator;
import com.google.firebase.encoders.config.EncoderConfig;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes3.dex */
public class ProtobufEncoder {
    private final ObjectEncoder<Object> fallbackEncoder;
    private final Map<Class<?>, ObjectEncoder<?>> objectEncoders;
    private final Map<Class<?>, ValueEncoder<?>> valueEncoders;

    ProtobufEncoder(Map<Class<?>, ObjectEncoder<?>> objectEncoders, Map<Class<?>, ValueEncoder<?>> valueEncoders, ObjectEncoder<Object> fallbackEncoder) {
        this.objectEncoders = objectEncoders;
        this.valueEncoders = valueEncoders;
        this.fallbackEncoder = fallbackEncoder;
    }

    public void encode(Object value, OutputStream outputStream) throws IOException {
        ProtobufDataEncoderContext context = new ProtobufDataEncoderContext(outputStream, this.objectEncoders, this.valueEncoders, this.fallbackEncoder);
        context.encode(value);
    }

    public byte[] encode(Object value) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            encode(value, output);
        } catch (IOException e) {
        }
        return output.toByteArray();
    }

    public static Builder builder() {
        return new Builder();
    }

    /* loaded from: classes3.dex */
    public static final class Builder implements EncoderConfig<Builder> {
        private static final ObjectEncoder<Object> DEFAULT_FALLBACK_ENCODER = ProtobufEncoder$Builder$$ExternalSyntheticLambda0.INSTANCE;
        private final Map<Class<?>, ObjectEncoder<?>> objectEncoders = new HashMap();
        private final Map<Class<?>, ValueEncoder<?>> valueEncoders = new HashMap();
        private ObjectEncoder<Object> fallbackEncoder = DEFAULT_FALLBACK_ENCODER;

        public static /* synthetic */ void lambda$static$0(Object o, ObjectEncoderContext ctx) throws IOException {
            throw new EncodingException("Couldn't find encoder for type " + o.getClass().getCanonicalName());
        }

        @Override // com.google.firebase.encoders.config.EncoderConfig
        public <U> Builder registerEncoder(Class<U> type, ObjectEncoder<? super U> encoder) {
            this.objectEncoders.put(type, encoder);
            this.valueEncoders.remove(type);
            return this;
        }

        @Override // com.google.firebase.encoders.config.EncoderConfig
        public <U> Builder registerEncoder(Class<U> type, ValueEncoder<? super U> encoder) {
            this.valueEncoders.put(type, encoder);
            this.objectEncoders.remove(type);
            return this;
        }

        public Builder registerFallbackEncoder(ObjectEncoder<Object> fallbackEncoder) {
            this.fallbackEncoder = fallbackEncoder;
            return this;
        }

        public Builder configureWith(Configurator config) {
            config.configure(this);
            return this;
        }

        public ProtobufEncoder build() {
            return new ProtobufEncoder(new HashMap(this.objectEncoders), new HashMap(this.valueEncoders), this.fallbackEncoder);
        }
    }
}
