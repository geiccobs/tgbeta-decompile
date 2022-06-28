package com.google.android.exoplayer2.drm;

import com.google.android.exoplayer2.util.Util;
import java.util.UUID;
/* loaded from: classes3.dex */
public final class FrameworkMediaCrypto implements ExoMediaCrypto {
    public static final boolean WORKAROUND_DEVICE_NEEDS_KEYS_TO_CONFIGURE_CODEC;
    public final boolean forceAllowInsecureDecoderComponents;
    public final byte[] sessionId;
    public final UUID uuid;

    static {
        WORKAROUND_DEVICE_NEEDS_KEYS_TO_CONFIGURE_CODEC = "Amazon".equals(Util.MANUFACTURER) && ("AFTM".equals(Util.MODEL) || "AFTB".equals(Util.MODEL));
    }

    public FrameworkMediaCrypto(UUID uuid, byte[] sessionId, boolean forceAllowInsecureDecoderComponents) {
        this.uuid = uuid;
        this.sessionId = sessionId;
        this.forceAllowInsecureDecoderComponents = forceAllowInsecureDecoderComponents;
    }
}
