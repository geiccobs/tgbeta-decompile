package com.google.android.exoplayer2.drm;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Pair;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Assertions;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
/* loaded from: classes3.dex */
public final class OfflineLicenseHelper<T extends ExoMediaCrypto> {
    private static final DrmInitData DUMMY_DRM_INIT_DATA = new DrmInitData(new DrmInitData.SchemeData[0]);
    private final ConditionVariable conditionVariable = new ConditionVariable();
    private final DefaultDrmSessionManager<T> drmSessionManager;
    private final HandlerThread handlerThread;

    public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(String defaultLicenseUrl, HttpDataSource.Factory httpDataSourceFactory) throws UnsupportedDrmException {
        return newWidevineInstance(defaultLicenseUrl, false, httpDataSourceFactory, null);
    }

    public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(String defaultLicenseUrl, boolean forceDefaultLicenseUrl, HttpDataSource.Factory httpDataSourceFactory) throws UnsupportedDrmException {
        return newWidevineInstance(defaultLicenseUrl, forceDefaultLicenseUrl, httpDataSourceFactory, null);
    }

    public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(String defaultLicenseUrl, boolean forceDefaultLicenseUrl, HttpDataSource.Factory httpDataSourceFactory, Map<String, String> optionalKeyRequestParameters) throws UnsupportedDrmException {
        return new OfflineLicenseHelper<>(C.WIDEVINE_UUID, FrameworkMediaDrm.DEFAULT_PROVIDER, new HttpMediaDrmCallback(defaultLicenseUrl, forceDefaultLicenseUrl, httpDataSourceFactory), optionalKeyRequestParameters);
    }

    public OfflineLicenseHelper(UUID uuid, ExoMediaDrm.Provider<T> mediaDrmProvider, MediaDrmCallback callback, Map<String, String> optionalKeyRequestParameters) {
        HandlerThread handlerThread = new HandlerThread("OfflineLicenseHelper");
        this.handlerThread = handlerThread;
        handlerThread.start();
        DefaultDrmSessionEventListener eventListener = new DefaultDrmSessionEventListener() { // from class: com.google.android.exoplayer2.drm.OfflineLicenseHelper.1
            @Override // com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener
            public /* synthetic */ void onDrmSessionAcquired() {
                DefaultDrmSessionEventListener.CC.$default$onDrmSessionAcquired(this);
            }

            @Override // com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener
            public /* synthetic */ void onDrmSessionReleased() {
                DefaultDrmSessionEventListener.CC.$default$onDrmSessionReleased(this);
            }

            @Override // com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener
            public void onDrmKeysLoaded() {
                OfflineLicenseHelper.this.conditionVariable.open();
            }

            @Override // com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener
            public void onDrmSessionManagerError(Exception e) {
                OfflineLicenseHelper.this.conditionVariable.open();
            }

            @Override // com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener
            public void onDrmKeysRestored() {
                OfflineLicenseHelper.this.conditionVariable.open();
            }

            @Override // com.google.android.exoplayer2.drm.DefaultDrmSessionEventListener
            public void onDrmKeysRemoved() {
                OfflineLicenseHelper.this.conditionVariable.open();
            }
        };
        DefaultDrmSessionManager<T> defaultDrmSessionManager = (DefaultDrmSessionManager<T>) new DefaultDrmSessionManager.Builder().setUuidAndExoMediaDrmProvider(uuid, mediaDrmProvider).setKeyRequestParameters(optionalKeyRequestParameters == null ? Collections.emptyMap() : optionalKeyRequestParameters).build(callback);
        this.drmSessionManager = defaultDrmSessionManager;
        defaultDrmSessionManager.addListener(new Handler(handlerThread.getLooper()), eventListener);
    }

    public synchronized byte[] downloadLicense(DrmInitData drmInitData) throws DrmSession.DrmSessionException {
        Assertions.checkArgument(drmInitData != null);
        return blockingKeyRequest(2, null, drmInitData);
    }

    public synchronized byte[] renewLicense(byte[] offlineLicenseKeySetId) throws DrmSession.DrmSessionException {
        Assertions.checkNotNull(offlineLicenseKeySetId);
        return blockingKeyRequest(2, offlineLicenseKeySetId, DUMMY_DRM_INIT_DATA);
    }

    public synchronized void releaseLicense(byte[] offlineLicenseKeySetId) throws DrmSession.DrmSessionException {
        Assertions.checkNotNull(offlineLicenseKeySetId);
        blockingKeyRequest(3, offlineLicenseKeySetId, DUMMY_DRM_INIT_DATA);
    }

    public synchronized Pair<Long, Long> getLicenseDurationRemainingSec(byte[] offlineLicenseKeySetId) throws DrmSession.DrmSessionException {
        Assertions.checkNotNull(offlineLicenseKeySetId);
        this.drmSessionManager.prepare();
        DrmSession<T> drmSession = openBlockingKeyRequest(1, offlineLicenseKeySetId, DUMMY_DRM_INIT_DATA);
        DrmSession.DrmSessionException error = drmSession.getError();
        Pair<Long, Long> licenseDurationRemainingSec = WidevineUtil.getLicenseDurationRemainingSec(drmSession);
        drmSession.release();
        this.drmSessionManager.release();
        if (error != null) {
            if (error.getCause() instanceof KeysExpiredException) {
                return Pair.create(0L, 0L);
            }
            throw error;
        }
        return (Pair) Assertions.checkNotNull(licenseDurationRemainingSec);
    }

    public void release() {
        this.handlerThread.quit();
    }

    private byte[] blockingKeyRequest(int licenseMode, byte[] offlineLicenseKeySetId, DrmInitData drmInitData) throws DrmSession.DrmSessionException {
        this.drmSessionManager.prepare();
        DrmSession<T> drmSession = openBlockingKeyRequest(licenseMode, offlineLicenseKeySetId, drmInitData);
        DrmSession.DrmSessionException error = drmSession.getError();
        byte[] keySetId = drmSession.getOfflineLicenseKeySetId();
        drmSession.release();
        this.drmSessionManager.release();
        if (error != null) {
            throw error;
        }
        return (byte[]) Assertions.checkNotNull(keySetId);
    }

    private DrmSession<T> openBlockingKeyRequest(int licenseMode, byte[] offlineLicenseKeySetId, DrmInitData drmInitData) {
        this.drmSessionManager.setMode(licenseMode, offlineLicenseKeySetId);
        this.conditionVariable.close();
        DrmSession<T> drmSession = this.drmSessionManager.acquireSession(this.handlerThread.getLooper(), drmInitData);
        this.conditionVariable.block();
        return drmSession;
    }
}
