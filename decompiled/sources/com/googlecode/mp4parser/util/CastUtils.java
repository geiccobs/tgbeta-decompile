package com.googlecode.mp4parser.util;
/* loaded from: classes.dex */
public class CastUtils {
    public static int l2i(long j) {
        if (j > 2147483647L || j < -2147483648L) {
            throw new RuntimeException("A cast to int has gone wrong. Please contact the mp4parser discussion group (" + j + ")");
        }
        return (int) j;
    }
}
