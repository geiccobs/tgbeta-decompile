package com.coremedia.iso;

import com.googlecode.mp4parser.BasicContainer;
import com.googlecode.mp4parser.util.Logger;
import java.io.IOException;
/* loaded from: classes.dex */
public class IsoFile extends BasicContainer {
    static {
        Logger.getLogger(IsoFile.class);
    }

    public static byte[] fourCCtoBytes(String str) {
        byte[] bArr = new byte[4];
        if (str != null) {
            for (int i = 0; i < Math.min(4, str.length()); i++) {
                bArr[i] = (byte) str.charAt(i);
            }
        }
        return bArr;
    }

    @Override // com.googlecode.mp4parser.BasicContainer, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.dataSource.close();
    }

    @Override // com.googlecode.mp4parser.BasicContainer
    public String toString() {
        return "model(" + this.dataSource.toString() + ")";
    }
}
