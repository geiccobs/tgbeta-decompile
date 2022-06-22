package com.google.firebase.encoders;

import java.io.IOException;
/* loaded from: classes.dex */
public interface ValueEncoderContext {
    ValueEncoderContext add(String str) throws IOException;

    ValueEncoderContext add(boolean z) throws IOException;
}
