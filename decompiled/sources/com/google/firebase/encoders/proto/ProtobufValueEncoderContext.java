package com.google.firebase.encoders.proto;

import com.google.firebase.encoders.EncodingException;
import com.google.firebase.encoders.FieldDescriptor;
import com.google.firebase.encoders.ValueEncoderContext;
import java.io.IOException;
/* loaded from: classes3.dex */
public class ProtobufValueEncoderContext implements ValueEncoderContext {
    private FieldDescriptor field;
    private final ProtobufDataEncoderContext objEncoderCtx;
    private boolean encoded = false;
    private boolean skipDefault = false;

    public ProtobufValueEncoderContext(ProtobufDataEncoderContext objEncoderCtx) {
        this.objEncoderCtx = objEncoderCtx;
    }

    public void resetContext(FieldDescriptor field, boolean skipDefault) {
        this.encoded = false;
        this.field = field;
        this.skipDefault = skipDefault;
    }

    private void checkNotUsed() {
        if (this.encoded) {
            throw new EncodingException("Cannot encode a second value in the ValueEncoderContext");
        }
        this.encoded = true;
    }

    @Override // com.google.firebase.encoders.ValueEncoderContext
    public ValueEncoderContext add(String value) throws IOException {
        checkNotUsed();
        this.objEncoderCtx.add(this.field, value, this.skipDefault);
        return this;
    }

    @Override // com.google.firebase.encoders.ValueEncoderContext
    public ValueEncoderContext add(float value) throws IOException {
        checkNotUsed();
        this.objEncoderCtx.add(this.field, value, this.skipDefault);
        return this;
    }

    @Override // com.google.firebase.encoders.ValueEncoderContext
    public ValueEncoderContext add(double value) throws IOException {
        checkNotUsed();
        this.objEncoderCtx.add(this.field, value, this.skipDefault);
        return this;
    }

    @Override // com.google.firebase.encoders.ValueEncoderContext
    public ValueEncoderContext add(int value) throws IOException {
        checkNotUsed();
        this.objEncoderCtx.add(this.field, value, this.skipDefault);
        return this;
    }

    @Override // com.google.firebase.encoders.ValueEncoderContext
    public ValueEncoderContext add(long value) throws IOException {
        checkNotUsed();
        this.objEncoderCtx.add(this.field, value, this.skipDefault);
        return this;
    }

    @Override // com.google.firebase.encoders.ValueEncoderContext
    public ValueEncoderContext add(boolean value) throws IOException {
        checkNotUsed();
        this.objEncoderCtx.add(this.field, value, this.skipDefault);
        return this;
    }

    @Override // com.google.firebase.encoders.ValueEncoderContext
    public ValueEncoderContext add(byte[] bytes) throws IOException {
        checkNotUsed();
        this.objEncoderCtx.add(this.field, bytes, this.skipDefault);
        return this;
    }
}
