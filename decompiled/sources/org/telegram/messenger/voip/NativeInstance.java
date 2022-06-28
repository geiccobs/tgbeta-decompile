package org.telegram.messenger.voip;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.voip.Instance;
import org.webrtc.ContextUtils;
import org.webrtc.VideoSink;
/* loaded from: classes4.dex */
public class NativeInstance {
    private AudioLevelsCallback audioLevelsCallback;
    private RequestBroadcastPartCallback cancelRequestBroadcastPartCallback;
    private Instance.FinalState finalState;
    private boolean isGroup;
    private long nativePtr;
    private Instance.OnRemoteMediaStateUpdatedListener onRemoteMediaStateUpdatedListener;
    private Instance.OnSignalBarsUpdatedListener onSignalBarsUpdatedListener;
    private Instance.OnSignalingDataListener onSignalDataListener;
    private Instance.OnStateUpdatedListener onStateUpdatedListener;
    private PayloadCallback payloadCallback;
    private String persistentStateFilePath;
    private RequestBroadcastPartCallback requestBroadcastPartCallback;
    private RequestCurrentTimeCallback requestCurrentTimeCallback;
    private CountDownLatch stopBarrier;
    private float[] temp = new float[1];
    private VideoSourcesCallback unknownParticipantsCallback;

    /* loaded from: classes4.dex */
    public interface AudioLevelsCallback {
        void run(int[] iArr, float[] fArr, boolean[] zArr);
    }

    /* loaded from: classes4.dex */
    public interface PayloadCallback {
        void run(int i, String str);
    }

    /* loaded from: classes4.dex */
    public interface RequestBroadcastPartCallback {
        void run(long j, long j2, int i, int i2);
    }

    /* loaded from: classes4.dex */
    public interface RequestCurrentTimeCallback {
        void run(long j);
    }

    /* loaded from: classes4.dex */
    public static class SsrcGroup {
        public String semantics;
        public int[] ssrcs;
    }

    /* loaded from: classes4.dex */
    public interface VideoSourcesCallback {
        void run(long j, int[] iArr);
    }

    public static native long createVideoCapturer(VideoSink videoSink, int i);

    public static native void destroyVideoCapturer(long j);

    private static native long makeGroupNativeInstance(NativeInstance nativeInstance, String str, boolean z, long j, boolean z2, boolean z3);

    private static native long makeNativeInstance(String str, NativeInstance nativeInstance, Instance.Config config, String str2, Instance.Endpoint[] endpointArr, Instance.Proxy proxy, int i, Instance.EncryptionKey encryptionKey, VideoSink videoSink, long j, float f);

    public static native void setVideoStateCapturer(long j, int i);

    private native void stopGroupNative();

    private native void stopNative();

    public static native void switchCameraCapturer(long j, boolean z);

    public native void activateVideoCapturer(long j);

    public native long addIncomingVideoOutput(int i, String str, SsrcGroup[] ssrcGroupArr, VideoSink videoSink);

    public native void clearVideoCapturer();

    public native String getDebugInfo();

    public native String getLastError();

    public native byte[] getPersistentState();

    public native long getPreferredRelayId();

    public native Instance.TrafficStats getTrafficStats();

    public native String getVersion();

    public native boolean hasVideoCapturer();

    public native void onMediaDescriptionAvailable(long j, int[] iArr);

    public native void onRequestTimeComplete(long j, long j2);

    public native void onSignalingDataReceive(byte[] bArr);

    public native void onStreamPartAvailable(long j, ByteBuffer byteBuffer, int i, long j2, int i2, int i3);

    public native void prepareForStream(boolean z);

    public native void removeIncomingVideoOutput(long j);

    public native void resetGroupInstance(boolean z, boolean z2);

    public native void setAudioOutputGainControlEnabled(boolean z);

    public native void setBufferSize(int i);

    public native void setEchoCancellationStrength(int i);

    public native void setGlobalServerConfig(String str);

    public native void setJoinResponsePayload(String str);

    public native void setMuteMicrophone(boolean z);

    public native void setNetworkType(int i);

    public native void setNoiseSuppressionEnabled(boolean z);

    public native void setVideoEndpointQuality(String str, int i);

    public native void setVideoState(int i);

    public native void setVolume(int i, double d);

    public native void setupOutgoingVideo(VideoSink videoSink, int i);

    public native void setupOutgoingVideoCreated(long j);

    public native void switchCamera(boolean z);

    public static NativeInstance make(String version, Instance.Config config, String path, Instance.Endpoint[] endpoints, Instance.Proxy proxy, int networkType, Instance.EncryptionKey encryptionKey, VideoSink remoteSink, long videoCapturer, AudioLevelsCallback audioLevelsCallback) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("create new tgvoip instance, version " + version);
        }
        NativeInstance instance = new NativeInstance();
        instance.persistentStateFilePath = path;
        instance.audioLevelsCallback = audioLevelsCallback;
        float aspectRatio = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / Math.max(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
        instance.nativePtr = makeNativeInstance(version, instance, config, path, endpoints, proxy, networkType, encryptionKey, remoteSink, videoCapturer, aspectRatio);
        return instance;
    }

    public static NativeInstance makeGroup(String logPath, long videoCapturer, boolean screencast, boolean noiseSupression, PayloadCallback payloadCallback, AudioLevelsCallback audioLevelsCallback, VideoSourcesCallback unknownParticipantsCallback, RequestBroadcastPartCallback requestBroadcastPartCallback, RequestBroadcastPartCallback cancelRequestBroadcastPartCallback, RequestCurrentTimeCallback requestCurrentTimeCallback) {
        ContextUtils.initialize(ApplicationLoader.applicationContext);
        NativeInstance instance = new NativeInstance();
        instance.payloadCallback = payloadCallback;
        instance.audioLevelsCallback = audioLevelsCallback;
        instance.unknownParticipantsCallback = unknownParticipantsCallback;
        instance.requestBroadcastPartCallback = requestBroadcastPartCallback;
        instance.cancelRequestBroadcastPartCallback = cancelRequestBroadcastPartCallback;
        instance.requestCurrentTimeCallback = requestCurrentTimeCallback;
        instance.isGroup = true;
        instance.nativePtr = makeGroupNativeInstance(instance, logPath, SharedConfig.disableVoiceAudioEffects, videoCapturer, screencast, noiseSupression);
        return instance;
    }

    public int getPeerCapabilities() {
        return 0;
    }

    public boolean isGroup() {
        return this.isGroup;
    }

    public void setOnStateUpdatedListener(Instance.OnStateUpdatedListener listener) {
        this.onStateUpdatedListener = listener;
    }

    public void setOnSignalBarsUpdatedListener(Instance.OnSignalBarsUpdatedListener listener) {
        this.onSignalBarsUpdatedListener = listener;
    }

    public void setOnSignalDataListener(Instance.OnSignalingDataListener listener) {
        this.onSignalDataListener = listener;
    }

    public void setOnRemoteMediaStateUpdatedListener(Instance.OnRemoteMediaStateUpdatedListener listener) {
        this.onRemoteMediaStateUpdatedListener = listener;
    }

    private void onStateUpdated(int state) {
        Instance.OnStateUpdatedListener onStateUpdatedListener = this.onStateUpdatedListener;
        if (onStateUpdatedListener != null) {
            onStateUpdatedListener.onStateUpdated(state, false);
        }
    }

    private void onSignalBarsUpdated(int signalBars) {
        Instance.OnSignalBarsUpdatedListener onSignalBarsUpdatedListener = this.onSignalBarsUpdatedListener;
        if (onSignalBarsUpdatedListener != null) {
            onSignalBarsUpdatedListener.onSignalBarsUpdated(signalBars);
        }
    }

    private void onSignalingData(byte[] data) {
        Instance.OnSignalingDataListener onSignalingDataListener = this.onSignalDataListener;
        if (onSignalingDataListener != null) {
            onSignalingDataListener.onSignalingData(data);
        }
    }

    private void onRemoteMediaStateUpdated(int audioState, int videoState) {
        Instance.OnRemoteMediaStateUpdatedListener onRemoteMediaStateUpdatedListener = this.onRemoteMediaStateUpdatedListener;
        if (onRemoteMediaStateUpdatedListener != null) {
            onRemoteMediaStateUpdatedListener.onMediaStateUpdated(audioState, videoState);
        }
    }

    private void onNetworkStateUpdated(final boolean connected, final boolean inTransition) {
        if (this.onStateUpdatedListener != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.NativeInstance$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    NativeInstance.this.m1270xf9107118(connected, inTransition);
                }
            });
        }
    }

    /* renamed from: lambda$onNetworkStateUpdated$0$org-telegram-messenger-voip-NativeInstance */
    public /* synthetic */ void m1270xf9107118(boolean connected, boolean inTransition) {
        this.onStateUpdatedListener.onStateUpdated(connected ? 1 : 0, inTransition);
    }

    private void onAudioLevelsUpdated(final int[] uids, final float[] levels, final boolean[] voice) {
        if (this.isGroup && uids != null && uids.length == 0) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.NativeInstance$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                NativeInstance.this.m1268xc060a3e1(uids, levels, voice);
            }
        });
    }

    /* renamed from: lambda$onAudioLevelsUpdated$1$org-telegram-messenger-voip-NativeInstance */
    public /* synthetic */ void m1268xc060a3e1(int[] uids, float[] levels, boolean[] voice) {
        this.audioLevelsCallback.run(uids, levels, voice);
    }

    private void onParticipantDescriptionsRequired(final long taskPtr, final int[] ssrcs) {
        if (this.unknownParticipantsCallback == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.NativeInstance$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                NativeInstance.this.m1271xf92c4125(taskPtr, ssrcs);
            }
        });
    }

    /* renamed from: lambda$onParticipantDescriptionsRequired$2$org-telegram-messenger-voip-NativeInstance */
    public /* synthetic */ void m1271xf92c4125(long taskPtr, int[] ssrcs) {
        this.unknownParticipantsCallback.run(taskPtr, ssrcs);
    }

    private void onEmitJoinPayload(final String json, final int ssrc) {
        try {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.voip.NativeInstance$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    NativeInstance.this.m1269x9a75a4dc(ssrc, json);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$onEmitJoinPayload$3$org-telegram-messenger-voip-NativeInstance */
    public /* synthetic */ void m1269x9a75a4dc(int ssrc, String json) {
        this.payloadCallback.run(ssrc, json);
    }

    private void onRequestBroadcastPart(long timestamp, long duration, int videoChannel, int quality) {
        this.requestBroadcastPartCallback.run(timestamp, duration, videoChannel, quality);
    }

    private void onCancelRequestBroadcastPart(long timestamp, int videoChannel, int quality) {
        this.cancelRequestBroadcastPartCallback.run(timestamp, 0L, 0, 0);
    }

    private void requestCurrentTime(long taskPtr) {
        this.requestCurrentTimeCallback.run(taskPtr);
    }

    private void onStop(Instance.FinalState state) {
        this.finalState = state;
        CountDownLatch countDownLatch = this.stopBarrier;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public Instance.FinalState stop() {
        this.stopBarrier = new CountDownLatch(1);
        stopNative();
        try {
            this.stopBarrier.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return this.finalState;
    }

    public void stopGroup() {
        stopGroupNative();
    }
}
