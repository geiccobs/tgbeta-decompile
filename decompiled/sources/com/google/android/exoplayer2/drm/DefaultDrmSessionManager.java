package com.google.android.exoplayer2.drm;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.DefaultDrmSession;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSession;
import com.google.android.exoplayer2.drm.ExoMediaCrypto;
import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.EventDispatcher;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
/* loaded from: classes3.dex */
public class DefaultDrmSessionManager<T extends ExoMediaCrypto> implements DrmSessionManager<T> {
    public static final int INITIAL_DRM_REQUEST_RETRY_COUNT = 3;
    public static final int MODE_DOWNLOAD = 2;
    public static final int MODE_PLAYBACK = 0;
    public static final int MODE_QUERY = 1;
    public static final int MODE_RELEASE = 3;
    public static final String PLAYREADY_CUSTOM_DATA_KEY = "PRCustomData";
    private static final String TAG = "DefaultDrmSessionMgr";
    private final MediaDrmCallback callback;
    private final EventDispatcher<DefaultDrmSessionEventListener> eventDispatcher;
    private ExoMediaDrm<T> exoMediaDrm;
    private final ExoMediaDrm.Provider<T> exoMediaDrmProvider;
    private final HashMap<String, String> keyRequestParameters;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    volatile DefaultDrmSessionManager<T>.MediaDrmHandler mediaDrmHandler;
    private int mode;
    private final boolean multiSession;
    private DefaultDrmSession<T> noMultiSessionDrmSession;
    private byte[] offlineLicenseKeySetId;
    private DefaultDrmSession<T> placeholderDrmSession;
    private final boolean playClearSamplesWithoutKeys;
    private Looper playbackLooper;
    private int prepareCallsCount;
    private final DefaultDrmSessionManager<T>.ProvisioningManagerImpl provisioningManagerImpl;
    private final List<DefaultDrmSession<T>> provisioningSessions;
    private final List<DefaultDrmSession<T>> sessions;
    private final int[] useDrmSessionsForClearContentTrackTypes;
    private final UUID uuid;

    @Documented
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Mode {
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private boolean multiSession;
        private boolean playClearSamplesWithoutKeys;
        private final HashMap<String, String> keyRequestParameters = new HashMap<>();
        private UUID uuid = C.WIDEVINE_UUID;
        private ExoMediaDrm.Provider<ExoMediaCrypto> exoMediaDrmProvider = FrameworkMediaDrm.DEFAULT_PROVIDER;
        private LoadErrorHandlingPolicy loadErrorHandlingPolicy = new DefaultLoadErrorHandlingPolicy();
        private int[] useDrmSessionsForClearContentTrackTypes = new int[0];

        public Builder setKeyRequestParameters(Map<String, String> keyRequestParameters) {
            this.keyRequestParameters.clear();
            this.keyRequestParameters.putAll((Map) Assertions.checkNotNull(keyRequestParameters));
            return this;
        }

        public Builder setUuidAndExoMediaDrmProvider(UUID uuid, ExoMediaDrm.Provider exoMediaDrmProvider) {
            this.uuid = (UUID) Assertions.checkNotNull(uuid);
            this.exoMediaDrmProvider = (ExoMediaDrm.Provider) Assertions.checkNotNull(exoMediaDrmProvider);
            return this;
        }

        public Builder setMultiSession(boolean multiSession) {
            this.multiSession = multiSession;
            return this;
        }

        public Builder setUseDrmSessionsForClearContent(int... useDrmSessionsForClearContentTrackTypes) {
            for (int trackType : useDrmSessionsForClearContentTrackTypes) {
                boolean z = true;
                if (trackType != 2 && trackType != 1) {
                    z = false;
                }
                Assertions.checkArgument(z);
            }
            this.useDrmSessionsForClearContentTrackTypes = (int[]) useDrmSessionsForClearContentTrackTypes.clone();
            return this;
        }

        public Builder setPlayClearSamplesWithoutKeys(boolean playClearSamplesWithoutKeys) {
            this.playClearSamplesWithoutKeys = playClearSamplesWithoutKeys;
            return this;
        }

        public Builder setLoadErrorHandlingPolicy(LoadErrorHandlingPolicy loadErrorHandlingPolicy) {
            this.loadErrorHandlingPolicy = (LoadErrorHandlingPolicy) Assertions.checkNotNull(loadErrorHandlingPolicy);
            return this;
        }

        public DefaultDrmSessionManager<ExoMediaCrypto> build(MediaDrmCallback mediaDrmCallback) {
            return new DefaultDrmSessionManager<>(this.uuid, this.exoMediaDrmProvider, mediaDrmCallback, this.keyRequestParameters, this.multiSession, this.useDrmSessionsForClearContentTrackTypes, this.playClearSamplesWithoutKeys, this.loadErrorHandlingPolicy);
        }
    }

    /* loaded from: classes3.dex */
    public static final class MissingSchemeDataException extends Exception {
        private MissingSchemeDataException(UUID uuid) {
            super("Media does not support uuid: " + uuid);
        }
    }

    @Deprecated
    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> exoMediaDrm, MediaDrmCallback callback, HashMap<String, String> keyRequestParameters) {
        this(uuid, exoMediaDrm, callback, keyRequestParameters == null ? new HashMap<>() : keyRequestParameters, false, 3);
    }

    @Deprecated
    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> exoMediaDrm, MediaDrmCallback callback, HashMap<String, String> keyRequestParameters, boolean multiSession) {
        this(uuid, exoMediaDrm, callback, keyRequestParameters == null ? new HashMap<>() : keyRequestParameters, multiSession, 3);
    }

    @Deprecated
    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> exoMediaDrm, MediaDrmCallback callback, HashMap<String, String> keyRequestParameters, boolean multiSession, int initialDrmRequestRetryCount) {
        this(uuid, new ExoMediaDrm.AppManagedProvider(exoMediaDrm), callback, keyRequestParameters == null ? new HashMap<>() : keyRequestParameters, multiSession, new int[0], false, new DefaultLoadErrorHandlingPolicy(initialDrmRequestRetryCount));
    }

    private DefaultDrmSessionManager(UUID uuid, ExoMediaDrm.Provider<T> exoMediaDrmProvider, MediaDrmCallback callback, HashMap<String, String> keyRequestParameters, boolean multiSession, int[] useDrmSessionsForClearContentTrackTypes, boolean playClearSamplesWithoutKeys, LoadErrorHandlingPolicy loadErrorHandlingPolicy) {
        Assertions.checkNotNull(uuid);
        Assertions.checkArgument(!C.COMMON_PSSH_UUID.equals(uuid), "Use C.CLEARKEY_UUID instead");
        this.uuid = uuid;
        this.exoMediaDrmProvider = exoMediaDrmProvider;
        this.callback = callback;
        this.keyRequestParameters = keyRequestParameters;
        this.eventDispatcher = new EventDispatcher<>();
        this.multiSession = multiSession;
        this.useDrmSessionsForClearContentTrackTypes = useDrmSessionsForClearContentTrackTypes;
        this.playClearSamplesWithoutKeys = playClearSamplesWithoutKeys;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.provisioningManagerImpl = new ProvisioningManagerImpl();
        this.mode = 0;
        this.sessions = new ArrayList();
        this.provisioningSessions = new ArrayList();
    }

    public final void addListener(Handler handler, DefaultDrmSessionEventListener eventListener) {
        this.eventDispatcher.addListener(handler, eventListener);
    }

    public final void removeListener(DefaultDrmSessionEventListener eventListener) {
        this.eventDispatcher.removeListener(eventListener);
    }

    public void setMode(int mode, byte[] offlineLicenseKeySetId) {
        Assertions.checkState(this.sessions.isEmpty());
        if (mode == 1 || mode == 3) {
            Assertions.checkNotNull(offlineLicenseKeySetId);
        }
        this.mode = mode;
        this.offlineLicenseKeySetId = offlineLicenseKeySetId;
    }

    @Override // com.google.android.exoplayer2.drm.DrmSessionManager
    public final void prepare() {
        int i = this.prepareCallsCount;
        this.prepareCallsCount = i + 1;
        if (i == 0) {
            Assertions.checkState(this.exoMediaDrm == null);
            ExoMediaDrm<T> acquireExoMediaDrm = this.exoMediaDrmProvider.acquireExoMediaDrm(this.uuid);
            this.exoMediaDrm = acquireExoMediaDrm;
            acquireExoMediaDrm.setOnEventListener(new MediaDrmEventListener());
        }
    }

    @Override // com.google.android.exoplayer2.drm.DrmSessionManager
    public final void release() {
        int i = this.prepareCallsCount - 1;
        this.prepareCallsCount = i;
        if (i == 0) {
            ((ExoMediaDrm) Assertions.checkNotNull(this.exoMediaDrm)).release();
            this.exoMediaDrm = null;
        }
    }

    @Override // com.google.android.exoplayer2.drm.DrmSessionManager
    public boolean canAcquireSession(DrmInitData drmInitData) {
        if (this.offlineLicenseKeySetId != null) {
            return true;
        }
        List<DrmInitData.SchemeData> schemeDatas = getSchemeDatas(drmInitData, this.uuid, true);
        if (schemeDatas.isEmpty()) {
            if (drmInitData.schemeDataCount != 1 || !drmInitData.get(0).matches(C.COMMON_PSSH_UUID)) {
                return false;
            }
            Log.w(TAG, "DrmInitData only contains common PSSH SchemeData. Assuming support for: " + this.uuid);
        }
        String schemeType = drmInitData.schemeType;
        if (schemeType == null || C.CENC_TYPE_cenc.equals(schemeType)) {
            return true;
        }
        return (!C.CENC_TYPE_cbc1.equals(schemeType) && !C.CENC_TYPE_cbcs.equals(schemeType) && !C.CENC_TYPE_cens.equals(schemeType)) || Util.SDK_INT >= 25;
    }

    @Override // com.google.android.exoplayer2.drm.DrmSessionManager
    public DrmSession<T> acquirePlaceholderSession(Looper playbackLooper, int trackType) {
        assertExpectedPlaybackLooper(playbackLooper);
        ExoMediaDrm<T> exoMediaDrm = (ExoMediaDrm) Assertions.checkNotNull(this.exoMediaDrm);
        boolean avoidPlaceholderDrmSessions = FrameworkMediaCrypto.class.equals(exoMediaDrm.getExoMediaCryptoType()) && FrameworkMediaCrypto.WORKAROUND_DEVICE_NEEDS_KEYS_TO_CONFIGURE_CODEC;
        if (avoidPlaceholderDrmSessions || Util.linearSearch(this.useDrmSessionsForClearContentTrackTypes, trackType) == -1 || exoMediaDrm.getExoMediaCryptoType() == null) {
            return null;
        }
        maybeCreateMediaDrmHandler(playbackLooper);
        if (this.placeholderDrmSession == null) {
            DefaultDrmSession<T> placeholderDrmSession = createNewDefaultSession(Collections.emptyList(), true);
            this.sessions.add(placeholderDrmSession);
            this.placeholderDrmSession = placeholderDrmSession;
        }
        this.placeholderDrmSession.acquire();
        return this.placeholderDrmSession;
    }

    @Override // com.google.android.exoplayer2.drm.DrmSessionManager
    public DrmSession<T> acquireSession(Looper playbackLooper, DrmInitData drmInitData) {
        DefaultDrmSession<T> session;
        assertExpectedPlaybackLooper(playbackLooper);
        maybeCreateMediaDrmHandler(playbackLooper);
        List<DrmInitData.SchemeData> schemeDatas = null;
        if (this.offlineLicenseKeySetId == null) {
            schemeDatas = getSchemeDatas(drmInitData, this.uuid, false);
            if (schemeDatas.isEmpty()) {
                final MissingSchemeDataException error = new MissingSchemeDataException(this.uuid);
                this.eventDispatcher.dispatch(new EventDispatcher.Event() { // from class: com.google.android.exoplayer2.drm.DefaultDrmSessionManager$$ExternalSyntheticLambda1
                    @Override // com.google.android.exoplayer2.util.EventDispatcher.Event
                    public final void sendTo(Object obj) {
                        ((DefaultDrmSessionEventListener) obj).onDrmSessionManagerError(DefaultDrmSessionManager.MissingSchemeDataException.this);
                    }
                });
                return new ErrorStateDrmSession(new DrmSession.DrmSessionException(error));
            }
        }
        if (!this.multiSession) {
            session = this.noMultiSessionDrmSession;
        } else {
            session = null;
            Iterator<DefaultDrmSession<T>> it = this.sessions.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                DefaultDrmSession<T> existingSession = it.next();
                if (Util.areEqual(existingSession.schemeDatas, schemeDatas)) {
                    session = existingSession;
                    break;
                }
            }
        }
        if (session == null) {
            session = createNewDefaultSession(schemeDatas, false);
            if (!this.multiSession) {
                this.noMultiSessionDrmSession = session;
            }
            this.sessions.add(session);
        }
        session.acquire();
        return session;
    }

    @Override // com.google.android.exoplayer2.drm.DrmSessionManager
    public Class<T> getExoMediaCryptoType(DrmInitData drmInitData) {
        if (canAcquireSession(drmInitData)) {
            return ((ExoMediaDrm) Assertions.checkNotNull(this.exoMediaDrm)).getExoMediaCryptoType();
        }
        return null;
    }

    private void assertExpectedPlaybackLooper(Looper playbackLooper) {
        Looper looper = this.playbackLooper;
        Assertions.checkState(looper == null || looper == playbackLooper);
        this.playbackLooper = playbackLooper;
    }

    private void maybeCreateMediaDrmHandler(Looper playbackLooper) {
        if (this.mediaDrmHandler == null) {
            this.mediaDrmHandler = new MediaDrmHandler(playbackLooper);
        }
    }

    private DefaultDrmSession<T> createNewDefaultSession(List<DrmInitData.SchemeData> schemeDatas, boolean isPlaceholderSession) {
        Assertions.checkNotNull(this.exoMediaDrm);
        boolean playClearSamplesWithoutKeys = this.playClearSamplesWithoutKeys | isPlaceholderSession;
        return new DefaultDrmSession<>(this.uuid, this.exoMediaDrm, this.provisioningManagerImpl, new DefaultDrmSession.ReleaseCallback() { // from class: com.google.android.exoplayer2.drm.DefaultDrmSessionManager$$ExternalSyntheticLambda0
            @Override // com.google.android.exoplayer2.drm.DefaultDrmSession.ReleaseCallback
            public final void onSessionReleased(DefaultDrmSession defaultDrmSession) {
                DefaultDrmSessionManager.this.onSessionReleased(defaultDrmSession);
            }
        }, schemeDatas, this.mode, playClearSamplesWithoutKeys, isPlaceholderSession, this.offlineLicenseKeySetId, this.keyRequestParameters, this.callback, (Looper) Assertions.checkNotNull(this.playbackLooper), this.eventDispatcher, this.loadErrorHandlingPolicy);
    }

    public void onSessionReleased(DefaultDrmSession<T> drmSession) {
        this.sessions.remove(drmSession);
        if (this.placeholderDrmSession == drmSession) {
            this.placeholderDrmSession = null;
        }
        if (this.noMultiSessionDrmSession == drmSession) {
            this.noMultiSessionDrmSession = null;
        }
        if (this.provisioningSessions.size() > 1 && this.provisioningSessions.get(0) == drmSession) {
            this.provisioningSessions.get(1).provision();
        }
        this.provisioningSessions.remove(drmSession);
    }

    private static List<DrmInitData.SchemeData> getSchemeDatas(DrmInitData drmInitData, UUID uuid, boolean allowMissingData) {
        List<DrmInitData.SchemeData> matchingSchemeDatas = new ArrayList<>(drmInitData.schemeDataCount);
        for (int i = 0; i < drmInitData.schemeDataCount; i++) {
            DrmInitData.SchemeData schemeData = drmInitData.get(i);
            boolean uuidMatches = schemeData.matches(uuid) || (C.CLEARKEY_UUID.equals(uuid) && schemeData.matches(C.COMMON_PSSH_UUID));
            if (uuidMatches && (schemeData.data != null || allowMissingData)) {
                matchingSchemeDatas.add(schemeData);
            }
        }
        return matchingSchemeDatas;
    }

    /* loaded from: classes3.dex */
    public class MediaDrmHandler extends Handler {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public MediaDrmHandler(Looper looper) {
            super(looper);
            DefaultDrmSessionManager.this = r1;
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            byte[] sessionId = (byte[]) msg.obj;
            if (sessionId != null) {
                for (DefaultDrmSession<T> session : DefaultDrmSessionManager.this.sessions) {
                    if (session.hasSessionId(sessionId)) {
                        session.onMediaDrmEvent(msg.what);
                        return;
                    }
                }
            }
        }
    }

    /* loaded from: classes3.dex */
    public class ProvisioningManagerImpl implements DefaultDrmSession.ProvisioningManager<T> {
        private ProvisioningManagerImpl() {
            DefaultDrmSessionManager.this = r1;
        }

        @Override // com.google.android.exoplayer2.drm.DefaultDrmSession.ProvisioningManager
        public void provisionRequired(DefaultDrmSession<T> session) {
            if (!DefaultDrmSessionManager.this.provisioningSessions.contains(session)) {
                DefaultDrmSessionManager.this.provisioningSessions.add(session);
                if (DefaultDrmSessionManager.this.provisioningSessions.size() == 1) {
                    session.provision();
                }
            }
        }

        @Override // com.google.android.exoplayer2.drm.DefaultDrmSession.ProvisioningManager
        public void onProvisionCompleted() {
            for (DefaultDrmSession<T> session : DefaultDrmSessionManager.this.provisioningSessions) {
                session.onProvisionCompleted();
            }
            DefaultDrmSessionManager.this.provisioningSessions.clear();
        }

        @Override // com.google.android.exoplayer2.drm.DefaultDrmSession.ProvisioningManager
        public void onProvisionError(Exception error) {
            for (DefaultDrmSession<T> session : DefaultDrmSessionManager.this.provisioningSessions) {
                session.onProvisionError(error);
            }
            DefaultDrmSessionManager.this.provisioningSessions.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class MediaDrmEventListener implements ExoMediaDrm.OnEventListener<T> {
        private MediaDrmEventListener() {
            DefaultDrmSessionManager.this = r1;
        }

        @Override // com.google.android.exoplayer2.drm.ExoMediaDrm.OnEventListener
        public void onEvent(ExoMediaDrm<? extends T> md, byte[] sessionId, int event, int extra, byte[] data) {
            ((MediaDrmHandler) Assertions.checkNotNull(DefaultDrmSessionManager.this.mediaDrmHandler)).obtainMessage(event, sessionId).sendToTarget();
        }
    }
}
