package com.googlecode.mp4parser.h264.model;

import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes3.dex */
public abstract class BitstreamElement {
    public abstract void write(OutputStream outputStream) throws IOException;
}
