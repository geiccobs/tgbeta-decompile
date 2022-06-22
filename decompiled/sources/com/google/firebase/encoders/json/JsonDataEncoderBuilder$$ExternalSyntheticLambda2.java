package com.google.firebase.encoders.json;

import com.google.firebase.encoders.ValueEncoder;
import com.google.firebase.encoders.ValueEncoderContext;
/* loaded from: classes.dex */
public final /* synthetic */ class JsonDataEncoderBuilder$$ExternalSyntheticLambda2 implements ValueEncoder {
    public static final /* synthetic */ JsonDataEncoderBuilder$$ExternalSyntheticLambda2 INSTANCE = new JsonDataEncoderBuilder$$ExternalSyntheticLambda2();

    private /* synthetic */ JsonDataEncoderBuilder$$ExternalSyntheticLambda2() {
    }

    @Override // com.google.firebase.encoders.ValueEncoder
    public final void encode(Object obj, Object obj2) {
        ((ValueEncoderContext) obj2).add((String) obj);
    }
}
