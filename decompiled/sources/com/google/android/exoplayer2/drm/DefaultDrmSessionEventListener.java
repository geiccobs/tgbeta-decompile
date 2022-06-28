package com.google.android.exoplayer2.drm;
/* loaded from: classes3.dex */
public interface DefaultDrmSessionEventListener {
    void onDrmKeysLoaded();

    void onDrmKeysRemoved();

    void onDrmKeysRestored();

    void onDrmSessionAcquired();

    void onDrmSessionManagerError(Exception exc);

    void onDrmSessionReleased();

    /* renamed from: com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener$-CC */
    /* loaded from: classes3.dex */
    public final /* synthetic */ class CC {
        public static void $default$onDrmSessionAcquired(DefaultDrmSessionEventListener _this) {
        }

        public static void $default$onDrmKeysLoaded(DefaultDrmSessionEventListener _this) {
        }

        public static void $default$onDrmSessionManagerError(DefaultDrmSessionEventListener _this, Exception error) {
        }

        public static void $default$onDrmKeysRestored(DefaultDrmSessionEventListener _this) {
        }

        public static void $default$onDrmKeysRemoved(DefaultDrmSessionEventListener _this) {
        }

        public static void $default$onDrmSessionReleased(DefaultDrmSessionEventListener _this) {
        }
    }
}
