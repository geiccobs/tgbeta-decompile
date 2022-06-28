package com.googlecode.mp4parser.util;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
/* loaded from: classes3.dex */
public class ChannelHelper {
    private static ByteBuffer empty = ByteBuffer.allocate(0).asReadOnlyBuffer();

    public static void readFully(ReadableByteChannel channel, ByteBuffer buf) throws IOException {
        readFully(channel, buf, buf.remaining());
    }

    public static int readFully(ReadableByteChannel channel, ByteBuffer buf, int length) throws IOException {
        int n;
        int count = 0;
        do {
            n = channel.read(buf);
            if (-1 == n) {
                break;
            }
            count += n;
        } while (count != length);
        if (n == -1) {
            throw new EOFException("End of file. No more boxes.");
        }
        return count;
    }
}
