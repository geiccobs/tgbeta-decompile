package com.google.android.exoplayer2.ext.opus;

import com.google.android.exoplayer2.ExoPlayerLibraryInfo;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.util.Util;
/* loaded from: classes3.dex */
public final class OpusLibrary {
    private static Class<? extends ExoMediaCrypto> exoMediaCryptoType;

    public static native String opusGetVersion();

    public static native boolean opusIsSecureDecodeSupported();

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.opus");
    }

    private OpusLibrary() {
    }

    public static void setLibraries(Class<? extends ExoMediaCrypto> exoMediaCryptoType2, String... libraries) {
        exoMediaCryptoType = exoMediaCryptoType2;
    }

    public static String getVersion() {
        return opusGetVersion();
    }

    public static boolean matchesExpectedExoMediaCryptoType(Class<? extends ExoMediaCrypto> exoMediaCryptoType2) {
        return Util.areEqual(exoMediaCryptoType, exoMediaCryptoType2);
    }
}
