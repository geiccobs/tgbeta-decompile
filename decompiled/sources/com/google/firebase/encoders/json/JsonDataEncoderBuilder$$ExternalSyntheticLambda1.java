package com.google.firebase.encoders.json;

import com.google.firebase.encoders.ValueEncoder;
import com.google.firebase.encoders.ValueEncoderContext;
/* loaded from: classes3.dex */
public final /* synthetic */ class JsonDataEncoderBuilder$$ExternalSyntheticLambda1 implements ValueEncoder {
    public static final /* synthetic */ JsonDataEncoderBuilder$$ExternalSyntheticLambda1 INSTANCE = new JsonDataEncoderBuilder$$ExternalSyntheticLambda1();

    private /* synthetic */ JsonDataEncoderBuilder$$ExternalSyntheticLambda1() {
    }

    @Override // com.google.firebase.encoders.Encoder
    public final void encode(Object obj, ValueEncoderContext valueEncoderContext) {
        valueEncoderContext.add(((Boolean) obj).booleanValue());
    }
}
