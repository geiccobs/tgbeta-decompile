package com.google.firebase.encoders;

import java.io.IOException;
import java.io.Writer;
/* loaded from: classes.dex */
public interface DataEncoder {
    void encode(Object obj, Writer writer) throws IOException;
}
