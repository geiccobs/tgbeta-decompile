package com.googlecode.mp4parser.util;
/* loaded from: classes3.dex */
public class CastUtils {
    public static int l2i(long l) {
        if (l > 2147483647L || l < -2147483648L) {
            throw new RuntimeException("A cast to int has gone wrong. Please contact the mp4parser discussion group (" + l + ")");
        }
        return (int) l;
    }
}
