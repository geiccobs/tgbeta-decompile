package com.google.android.exoplayer2.drm;

import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
/* loaded from: classes3.dex */
public interface DrmSession<T extends ExoMediaCrypto> {
    public static final int STATE_ERROR = 1;
    public static final int STATE_OPENED = 3;
    public static final int STATE_OPENED_WITH_KEYS = 4;
    public static final int STATE_OPENING = 2;
    public static final int STATE_RELEASED = 0;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface State {
    }

    void acquire();

    DrmSessionException getError();

    T getMediaCrypto();

    byte[] getOfflineLicenseKeySetId();

    int getState();

    boolean playClearSamplesWithoutKeys();

    Map<String, String> queryKeyStatus();

    void release();

    /* renamed from: com.google.android.exoplayer2.drm.DrmSession$-CC */
    /* loaded from: classes3.dex */
    public final /* synthetic */ class CC {
        public static <T extends ExoMediaCrypto> void replaceSession(DrmSession<T> previousSession, DrmSession<T> newSession) {
            if (previousSession == newSession) {
                return;
            }
            if (newSession != null) {
                newSession.acquire();
            }
            if (previousSession != null) {
                previousSession.release();
            }
        }

        public static boolean $default$playClearSamplesWithoutKeys(DrmSession drmSession) {
            return false;
        }
    }

    /* loaded from: classes3.dex */
    public static class DrmSessionException extends IOException {
        public DrmSessionException(Throwable cause) {
            super(cause);
        }
    }
}
