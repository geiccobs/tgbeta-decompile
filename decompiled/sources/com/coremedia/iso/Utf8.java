package com.coremedia.iso;

import java.io.UnsupportedEncodingException;
/* loaded from: classes3.dex */
public final class Utf8 {
    public static byte[] convert(String s) {
        if (s != null) {
            try {
                return s.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new Error(e);
            }
        }
        return null;
    }

    public static String convert(byte[] b) {
        if (b != null) {
            try {
                return new String(b, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new Error(e);
            }
        }
        return null;
    }

    public static int utf8StringLengthInBytes(String utf8) {
        if (utf8 != null) {
            try {
                return utf8.getBytes("UTF-8").length;
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException();
            }
        }
        return 0;
    }
}
