package com.google.firebase.encoders;

import java.io.IOException;
/* loaded from: classes3.dex */
public interface Encoder<TValue, TContext> {
    void encode(TValue tvalue, TContext tcontext) throws IOException;
}
