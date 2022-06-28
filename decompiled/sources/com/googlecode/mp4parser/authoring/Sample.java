package com.googlecode.mp4parser.authoring;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
/* loaded from: classes3.dex */
public interface Sample {
    ByteBuffer asByteBuffer();

    long getSize();

    void writeTo(WritableByteChannel writableByteChannel) throws IOException;
}
