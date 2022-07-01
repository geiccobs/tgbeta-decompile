package com.google.android.exoplayer2.drm;

import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import java.io.IOException;
import java.util.Map;
/* loaded from: classes.dex */
public interface DrmSession<T extends ExoMediaCrypto> {
    void acquire();

    DrmSessionException getError();

    T getMediaCrypto();

    int getState();

    boolean playClearSamplesWithoutKeys();

    Map<String, String> queryKeyStatus();

    void release();

    /* renamed from: com.google.android.exoplayer2.drm.DrmSession$-CC */
    /* loaded from: classes.dex */
    public final /* synthetic */ class CC {
        public static <T extends ExoMediaCrypto> void replaceSession(DrmSession<T> drmSession, DrmSession<T> drmSession2) {
            if (drmSession == drmSession2) {
                return;
            }
            if (drmSession2 != null) {
                drmSession2.acquire();
            }
            if (drmSession == null) {
                return;
            }
            drmSession.release();
        }
    }

    /* loaded from: classes.dex */
    public static class DrmSessionException extends IOException {
        public DrmSessionException(Throwable th) {
            super(th);
        }
    }
}
