package com.google.android.exoplayer2.drm;
/* loaded from: classes.dex */
public interface DefaultDrmSessionEventListener {
    void onDrmKeysLoaded();

    void onDrmKeysRestored();

    void onDrmSessionAcquired();

    void onDrmSessionManagerError(Exception exc);

    void onDrmSessionReleased();
}
