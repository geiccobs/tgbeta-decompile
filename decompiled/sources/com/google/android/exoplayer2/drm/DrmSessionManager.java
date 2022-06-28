package com.google.android.exoplayer2.drm;

import android.os.Looper;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
/* loaded from: classes3.dex */
public interface DrmSessionManager<T extends ExoMediaCrypto> {
    public static final DrmSessionManager<ExoMediaCrypto> DUMMY = new DrmSessionManager<ExoMediaCrypto>() { // from class: com.google.android.exoplayer2.drm.DrmSessionManager.1
        @Override // com.google.android.exoplayer2.drm.DrmSessionManager
        public /* synthetic */ DrmSession<ExoMediaCrypto> acquirePlaceholderSession(Looper looper, int i) {
            return CC.$default$acquirePlaceholderSession(this, looper, i);
        }

        @Override // com.google.android.exoplayer2.drm.DrmSessionManager
        public /* synthetic */ void prepare() {
            CC.$default$prepare(this);
        }

        @Override // com.google.android.exoplayer2.drm.DrmSessionManager
        public /* synthetic */ void release() {
            CC.$default$release(this);
        }

        @Override // com.google.android.exoplayer2.drm.DrmSessionManager
        public boolean canAcquireSession(DrmInitData drmInitData) {
            return false;
        }

        @Override // com.google.android.exoplayer2.drm.DrmSessionManager
        public DrmSession<ExoMediaCrypto> acquireSession(Looper playbackLooper, DrmInitData drmInitData) {
            return new ErrorStateDrmSession(new DrmSession.DrmSessionException(new UnsupportedDrmException(1)));
        }

        @Override // com.google.android.exoplayer2.drm.DrmSessionManager
        public Class<ExoMediaCrypto> getExoMediaCryptoType(DrmInitData drmInitData) {
            return null;
        }
    };

    DrmSession<T> acquirePlaceholderSession(Looper looper, int i);

    DrmSession<T> acquireSession(Looper looper, DrmInitData drmInitData);

    boolean canAcquireSession(DrmInitData drmInitData);

    Class<? extends ExoMediaCrypto> getExoMediaCryptoType(DrmInitData drmInitData);

    void prepare();

    void release();

    /* renamed from: com.google.android.exoplayer2.drm.DrmSessionManager$-CC */
    /* loaded from: classes3.dex */
    public final /* synthetic */ class CC {
        static {
            DrmSessionManager<ExoMediaCrypto> drmSessionManager = DrmSessionManager.DUMMY;
        }

        public static <T extends ExoMediaCrypto> DrmSessionManager<T> getDummyDrmSessionManager() {
            return (DrmSessionManager<T>) DrmSessionManager.DUMMY;
        }

        public static void $default$prepare(DrmSessionManager drmSessionManager) {
        }

        public static void $default$release(DrmSessionManager drmSessionManager) {
        }

        public static DrmSession $default$acquirePlaceholderSession(DrmSessionManager drmSessionManager, Looper playbackLooper, int trackType) {
            return null;
        }
    }
}
