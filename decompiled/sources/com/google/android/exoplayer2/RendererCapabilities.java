package com.google.android.exoplayer2;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public interface RendererCapabilities {
    public static final int ADAPTIVE_NOT_SEAMLESS = 8;
    public static final int ADAPTIVE_NOT_SUPPORTED = 0;
    public static final int ADAPTIVE_SEAMLESS = 16;
    public static final int ADAPTIVE_SUPPORT_MASK = 24;
    public static final int FORMAT_EXCEEDS_CAPABILITIES = 3;
    public static final int FORMAT_HANDLED = 4;
    public static final int FORMAT_SUPPORT_MASK = 7;
    public static final int FORMAT_UNSUPPORTED_DRM = 2;
    public static final int FORMAT_UNSUPPORTED_SUBTYPE = 1;
    public static final int FORMAT_UNSUPPORTED_TYPE = 0;
    public static final int TUNNELING_NOT_SUPPORTED = 0;
    public static final int TUNNELING_SUPPORTED = 32;
    public static final int TUNNELING_SUPPORT_MASK = 32;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface AdaptiveSupport {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Capabilities {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FormatSupport {
    }

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface TunnelingSupport {
    }

    int getTrackType();

    int supportsFormat(Format format) throws ExoPlaybackException;

    int supportsMixedMimeTypeAdaptation() throws ExoPlaybackException;

    /* renamed from: com.google.android.exoplayer2.RendererCapabilities$-CC */
    /* loaded from: classes3.dex */
    public final /* synthetic */ class CC {
        public static int create(int formatSupport) {
            return create(formatSupport, 0, 0);
        }

        public static int create(int formatSupport, int adaptiveSupport, int tunnelingSupport) {
            return formatSupport | adaptiveSupport | tunnelingSupport;
        }

        public static int getFormatSupport(int supportFlags) {
            return supportFlags & 7;
        }

        public static int getAdaptiveSupport(int supportFlags) {
            return supportFlags & 24;
        }

        public static int getTunnelingSupport(int supportFlags) {
            return supportFlags & 32;
        }

        public static String getFormatSupportString(int formatSupport) {
            switch (formatSupport) {
                case 0:
                    return "NO";
                case 1:
                    return "NO_UNSUPPORTED_TYPE";
                case 2:
                    return "NO_UNSUPPORTED_DRM";
                case 3:
                    return "NO_EXCEEDS_CAPABILITIES";
                case 4:
                    return "YES";
                default:
                    throw new IllegalStateException();
            }
        }
    }
}
