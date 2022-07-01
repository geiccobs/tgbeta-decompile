package com.coremedia.iso.boxes;

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
/* loaded from: classes.dex */
public interface Box {
    void getBox(WritableByteChannel writableByteChannel) throws IOException;

    long getSize();

    void setParent(Container container);
}
