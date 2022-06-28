package com.coremedia.iso;

import java.io.UnsupportedEncodingException;
/* loaded from: classes3.dex */
public final class Ascii {
    public static byte[] convert(String s) {
        if (s != null) {
            try {
                return s.getBytes("us-ascii");
            } catch (UnsupportedEncodingException e) {
                throw new Error(e);
            }
        }
        return null;
    }

    public static String convert(byte[] b) {
        if (b != null) {
            try {
                return new String(b, "us-ascii");
            } catch (UnsupportedEncodingException e) {
                throw new Error(e);
            }
        }
        return null;
    }
}
