package org.webrtc;

import com.microsoft.appcenter.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.webrtc.DataChannel;
import org.webrtc.MediaStreamTrack;
import org.webrtc.RtpTransceiver;
/* loaded from: classes5.dex */
public class PeerConnection {
    private final List<MediaStream> localStreams;
    private final long nativePeerConnection;
    private List<RtpReceiver> receivers;
    private List<RtpSender> senders;
    private List<RtpTransceiver> transceivers;

    /* loaded from: classes5.dex */
    public enum BundlePolicy {
        BALANCED,
        MAXBUNDLE,
        MAXCOMPAT
    }

    /* loaded from: classes5.dex */
    public enum CandidateNetworkPolicy {
        ALL,
        LOW_COST
    }

    /* loaded from: classes5.dex */
    public enum ContinualGatheringPolicy {
        GATHER_ONCE,
        GATHER_CONTINUALLY
    }

    /* loaded from: classes5.dex */
    public enum IceTransportsType {
        NONE,
        RELAY,
        NOHOST,
        ALL
    }

    /* loaded from: classes5.dex */
    public enum KeyType {
        RSA,
        ECDSA
    }

    /* loaded from: classes5.dex */
    public enum PortPrunePolicy {
        NO_PRUNE,
        PRUNE_BASED_ON_PRIORITY,
        KEEP_FIRST_READY
    }

    /* loaded from: classes5.dex */
    public enum RtcpMuxPolicy {
        NEGOTIATE,
        REQUIRE
    }

    /* loaded from: classes5.dex */
    public enum SdpSemantics {
        PLAN_B,
        UNIFIED_PLAN
    }

    /* loaded from: classes5.dex */
    public enum TcpCandidatePolicy {
        ENABLED,
        DISABLED
    }

    /* loaded from: classes5.dex */
    public enum TlsCertPolicy {
        TLS_CERT_POLICY_SECURE,
        TLS_CERT_POLICY_INSECURE_NO_CHECK
    }

    private native boolean nativeAddIceCandidate(String str, int i, String str2);

    private native boolean nativeAddLocalStream(long j);

    private native RtpSender nativeAddTrack(long j, List<String> list);

    private native RtpTransceiver nativeAddTransceiverOfType(MediaStreamTrack.MediaType mediaType, RtpTransceiver.RtpTransceiverInit rtpTransceiverInit);

    private native RtpTransceiver nativeAddTransceiverWithTrack(long j, RtpTransceiver.RtpTransceiverInit rtpTransceiverInit);

    private native void nativeClose();

    private native PeerConnectionState nativeConnectionState();

    private native void nativeCreateAnswer(SdpObserver sdpObserver, MediaConstraints mediaConstraints);

    private native DataChannel nativeCreateDataChannel(String str, DataChannel.Init init);

    private native void nativeCreateOffer(SdpObserver sdpObserver, MediaConstraints mediaConstraints);

    private static native long nativeCreatePeerConnectionObserver(Observer observer);

    private native RtpSender nativeCreateSender(String str, String str2);

    private static native void nativeFreeOwnedPeerConnection(long j);

    private native RtcCertificatePem nativeGetCertificate();

    private native SessionDescription nativeGetLocalDescription();

    private native long nativeGetNativePeerConnection();

    private native List<RtpReceiver> nativeGetReceivers();

    private native SessionDescription nativeGetRemoteDescription();

    private native List<RtpSender> nativeGetSenders();

    private native List<RtpTransceiver> nativeGetTransceivers();

    private native IceConnectionState nativeIceConnectionState();

    private native IceGatheringState nativeIceGatheringState();

    private native void nativeNewGetStats(RTCStatsCollectorCallback rTCStatsCollectorCallback);

    private native boolean nativeOldGetStats(StatsObserver statsObserver, long j);

    private native boolean nativeRemoveIceCandidates(IceCandidate[] iceCandidateArr);

    private native void nativeRemoveLocalStream(long j);

    private native boolean nativeRemoveTrack(long j);

    private native void nativeSetAudioPlayout(boolean z);

    private native void nativeSetAudioRecording(boolean z);

    private native boolean nativeSetBitrate(Integer num, Integer num2, Integer num3);

    private native boolean nativeSetConfiguration(RTCConfiguration rTCConfiguration);

    private native void nativeSetLocalDescription(SdpObserver sdpObserver, SessionDescription sessionDescription);

    private native void nativeSetRemoteDescription(SdpObserver sdpObserver, SessionDescription sessionDescription);

    private native SignalingState nativeSignalingState();

    private native boolean nativeStartRtcEventLog(int i, int i2);

    private native void nativeStopRtcEventLog();

    /* loaded from: classes5.dex */
    public enum IceGatheringState {
        NEW,
        GATHERING,
        COMPLETE;

        static IceGatheringState fromNativeIndex(int nativeIndex) {
            return values()[nativeIndex];
        }
    }

    /* loaded from: classes5.dex */
    public enum IceConnectionState {
        NEW,
        CHECKING,
        CONNECTED,
        COMPLETED,
        FAILED,
        DISCONNECTED,
        CLOSED;

        static IceConnectionState fromNativeIndex(int nativeIndex) {
            return values()[nativeIndex];
        }
    }

    /* loaded from: classes5.dex */
    public enum PeerConnectionState {
        NEW,
        CONNECTING,
        CONNECTED,
        DISCONNECTED,
        FAILED,
        CLOSED;

        static PeerConnectionState fromNativeIndex(int nativeIndex) {
            return values()[nativeIndex];
        }
    }

    /* loaded from: classes5.dex */
    public enum SignalingState {
        STABLE,
        HAVE_LOCAL_OFFER,
        HAVE_LOCAL_PRANSWER,
        HAVE_REMOTE_OFFER,
        HAVE_REMOTE_PRANSWER,
        CLOSED;

        static SignalingState fromNativeIndex(int nativeIndex) {
            return values()[nativeIndex];
        }
    }

    /* loaded from: classes5.dex */
    public interface Observer {
        void onAddStream(MediaStream mediaStream);

        void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreamArr);

        void onConnectionChange(PeerConnectionState peerConnectionState);

        void onDataChannel(DataChannel dataChannel);

        void onIceCandidate(IceCandidate iceCandidate);

        void onIceCandidatesRemoved(IceCandidate[] iceCandidateArr);

        void onIceConnectionChange(IceConnectionState iceConnectionState);

        void onIceConnectionReceivingChange(boolean z);

        void onIceGatheringChange(IceGatheringState iceGatheringState);

        void onRemoveStream(MediaStream mediaStream);

        void onRenegotiationNeeded();

        void onSelectedCandidatePairChanged(CandidatePairChangeEvent candidatePairChangeEvent);

        void onSignalingChange(SignalingState signalingState);

        void onStandardizedIceConnectionChange(IceConnectionState iceConnectionState);

        void onTrack(RtpTransceiver rtpTransceiver);

        /* renamed from: org.webrtc.PeerConnection$Observer$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$onStandardizedIceConnectionChange(Observer _this, IceConnectionState newState) {
            }

            public static void $default$onConnectionChange(Observer _this, PeerConnectionState newState) {
            }

            public static void $default$onSelectedCandidatePairChanged(Observer _this, CandidatePairChangeEvent event) {
            }

            public static void $default$onTrack(Observer _this, RtpTransceiver transceiver) {
            }
        }
    }

    /* loaded from: classes5.dex */
    public static class IceServer {
        public final String hostname;
        public final String password;
        public final List<String> tlsAlpnProtocols;
        public final TlsCertPolicy tlsCertPolicy;
        public final List<String> tlsEllipticCurves;
        @Deprecated
        public final String uri;
        public final List<String> urls;
        public final String username;

        @Deprecated
        public IceServer(String uri) {
            this(uri, "", "");
        }

        @Deprecated
        public IceServer(String uri, String username, String password) {
            this(uri, username, password, TlsCertPolicy.TLS_CERT_POLICY_SECURE);
        }

        @Deprecated
        public IceServer(String uri, String username, String password, TlsCertPolicy tlsCertPolicy) {
            this(uri, username, password, tlsCertPolicy, "");
        }

        @Deprecated
        public IceServer(String uri, String username, String password, TlsCertPolicy tlsCertPolicy, String hostname) {
            this(uri, Collections.singletonList(uri), username, password, tlsCertPolicy, hostname, null, null);
        }

        private IceServer(String uri, List<String> urls, String username, String password, TlsCertPolicy tlsCertPolicy, String hostname, List<String> tlsAlpnProtocols, List<String> tlsEllipticCurves) {
            if (uri == null || urls == null || urls.isEmpty()) {
                throw new IllegalArgumentException("uri == null || urls == null || urls.isEmpty()");
            }
            for (String it : urls) {
                if (it == null) {
                    throw new IllegalArgumentException("urls element is null: " + urls);
                }
            }
            if (username == null) {
                throw new IllegalArgumentException("username == null");
            }
            if (password == null) {
                throw new IllegalArgumentException("password == null");
            }
            if (hostname == null) {
                throw new IllegalArgumentException("hostname == null");
            }
            this.uri = uri;
            this.urls = urls;
            this.username = username;
            this.password = password;
            this.tlsCertPolicy = tlsCertPolicy;
            this.hostname = hostname;
            this.tlsAlpnProtocols = tlsAlpnProtocols;
            this.tlsEllipticCurves = tlsEllipticCurves;
        }

        public String toString() {
            return this.urls + " [" + this.username + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + this.password + "] [" + this.tlsCertPolicy + "] [" + this.hostname + "] [" + this.tlsAlpnProtocols + "] [" + this.tlsEllipticCurves + "]";
        }

        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof IceServer)) {
                return false;
            }
            IceServer other = (IceServer) obj;
            return this.uri.equals(other.uri) && this.urls.equals(other.urls) && this.username.equals(other.username) && this.password.equals(other.password) && this.tlsCertPolicy.equals(other.tlsCertPolicy) && this.hostname.equals(other.hostname) && this.tlsAlpnProtocols.equals(other.tlsAlpnProtocols) && this.tlsEllipticCurves.equals(other.tlsEllipticCurves);
        }

        public int hashCode() {
            Object[] values = {this.uri, this.urls, this.username, this.password, this.tlsCertPolicy, this.hostname, this.tlsAlpnProtocols, this.tlsEllipticCurves};
            return Arrays.hashCode(values);
        }

        public static Builder builder(String uri) {
            return new Builder(Collections.singletonList(uri));
        }

        public static Builder builder(List<String> urls) {
            return new Builder(urls);
        }

        /* loaded from: classes5.dex */
        public static class Builder {
            private String hostname;
            private String password;
            private List<String> tlsAlpnProtocols;
            private TlsCertPolicy tlsCertPolicy;
            private List<String> tlsEllipticCurves;
            private final List<String> urls;
            private String username;

            private Builder(List<String> urls) {
                this.username = "";
                this.password = "";
                this.tlsCertPolicy = TlsCertPolicy.TLS_CERT_POLICY_SECURE;
                this.hostname = "";
                if (urls == null || urls.isEmpty()) {
                    throw new IllegalArgumentException("urls == null || urls.isEmpty(): " + urls);
                }
                this.urls = urls;
            }

            public Builder setUsername(String username) {
                this.username = username;
                return this;
            }

            public Builder setPassword(String password) {
                this.password = password;
                return this;
            }

            public Builder setTlsCertPolicy(TlsCertPolicy tlsCertPolicy) {
                this.tlsCertPolicy = tlsCertPolicy;
                return this;
            }

            public Builder setHostname(String hostname) {
                this.hostname = hostname;
                return this;
            }

            public Builder setTlsAlpnProtocols(List<String> tlsAlpnProtocols) {
                this.tlsAlpnProtocols = tlsAlpnProtocols;
                return this;
            }

            public Builder setTlsEllipticCurves(List<String> tlsEllipticCurves) {
                this.tlsEllipticCurves = tlsEllipticCurves;
                return this;
            }

            public IceServer createIceServer() {
                return new IceServer(this.urls.get(0), this.urls, this.username, this.password, this.tlsCertPolicy, this.hostname, this.tlsAlpnProtocols, this.tlsEllipticCurves);
            }
        }

        List<String> getUrls() {
            return this.urls;
        }

        String getUsername() {
            return this.username;
        }

        String getPassword() {
            return this.password;
        }

        TlsCertPolicy getTlsCertPolicy() {
            return this.tlsCertPolicy;
        }

        String getHostname() {
            return this.hostname;
        }

        List<String> getTlsAlpnProtocols() {
            return this.tlsAlpnProtocols;
        }

        List<String> getTlsEllipticCurves() {
            return this.tlsEllipticCurves;
        }
    }

    /* loaded from: classes5.dex */
    public enum AdapterType {
        UNKNOWN(0),
        ETHERNET(1),
        WIFI(2),
        CELLULAR(4),
        VPN(8),
        LOOPBACK(16),
        ADAPTER_TYPE_ANY(32),
        CELLULAR_2G(64),
        CELLULAR_3G(128),
        CELLULAR_4G(256),
        CELLULAR_5G(512);
        
        private static final Map<Integer, AdapterType> BY_BITMASK = new HashMap();
        public final Integer bitMask;

        static {
            AdapterType[] values;
            for (AdapterType t : values()) {
                BY_BITMASK.put(t.bitMask, t);
            }
        }

        AdapterType(Integer bitMask) {
            this.bitMask = bitMask;
        }

        static AdapterType fromNativeIndex(int nativeIndex) {
            return BY_BITMASK.get(Integer.valueOf(nativeIndex));
        }
    }

    /* loaded from: classes5.dex */
    public static class RTCConfiguration {
        public RtcCertificatePem certificate;
        public List<IceServer> iceServers;
        public TurnCustomizer turnCustomizer;
        public IceTransportsType iceTransportsType = IceTransportsType.ALL;
        public BundlePolicy bundlePolicy = BundlePolicy.BALANCED;
        public RtcpMuxPolicy rtcpMuxPolicy = RtcpMuxPolicy.REQUIRE;
        public TcpCandidatePolicy tcpCandidatePolicy = TcpCandidatePolicy.ENABLED;
        public CandidateNetworkPolicy candidateNetworkPolicy = CandidateNetworkPolicy.ALL;
        public int audioJitterBufferMaxPackets = 50;
        public boolean audioJitterBufferFastAccelerate = false;
        public int iceConnectionReceivingTimeout = -1;
        public int iceBackupCandidatePairPingInterval = -1;
        public KeyType keyType = KeyType.ECDSA;
        public ContinualGatheringPolicy continualGatheringPolicy = ContinualGatheringPolicy.GATHER_ONCE;
        public int iceCandidatePoolSize = 0;
        @Deprecated
        public boolean pruneTurnPorts = false;
        public PortPrunePolicy turnPortPrunePolicy = PortPrunePolicy.NO_PRUNE;
        public boolean presumeWritableWhenFullyRelayed = false;
        public boolean surfaceIceCandidatesOnIceTransportTypeChanged = false;
        public Integer iceCheckIntervalStrongConnectivityMs = null;
        public Integer iceCheckIntervalWeakConnectivityMs = null;
        public Integer iceCheckMinInterval = null;
        public Integer iceUnwritableTimeMs = null;
        public Integer iceUnwritableMinChecks = null;
        public Integer stunCandidateKeepaliveIntervalMs = null;
        public boolean disableIPv6OnWifi = false;
        public int maxIPv6Networks = 5;
        public boolean disableIpv6 = false;
        public boolean enableDscp = false;
        public boolean enableCpuOveruseDetection = true;
        public boolean enableRtpDataChannel = false;
        public boolean suspendBelowMinBitrate = false;
        public Integer screencastMinBitrate = null;
        public Boolean combinedAudioVideoBwe = null;
        public Boolean enableDtlsSrtp = null;
        public AdapterType networkPreference = AdapterType.UNKNOWN;
        public SdpSemantics sdpSemantics = SdpSemantics.PLAN_B;
        public boolean activeResetSrtpParams = false;
        public CryptoOptions cryptoOptions = null;
        public String turnLoggingId = null;
        public Boolean allowCodecSwitching = null;

        public RTCConfiguration(List<IceServer> iceServers) {
            this.iceServers = iceServers;
        }

        IceTransportsType getIceTransportsType() {
            return this.iceTransportsType;
        }

        List<IceServer> getIceServers() {
            return this.iceServers;
        }

        BundlePolicy getBundlePolicy() {
            return this.bundlePolicy;
        }

        PortPrunePolicy getTurnPortPrunePolicy() {
            return this.turnPortPrunePolicy;
        }

        RtcCertificatePem getCertificate() {
            return this.certificate;
        }

        RtcpMuxPolicy getRtcpMuxPolicy() {
            return this.rtcpMuxPolicy;
        }

        TcpCandidatePolicy getTcpCandidatePolicy() {
            return this.tcpCandidatePolicy;
        }

        CandidateNetworkPolicy getCandidateNetworkPolicy() {
            return this.candidateNetworkPolicy;
        }

        int getAudioJitterBufferMaxPackets() {
            return this.audioJitterBufferMaxPackets;
        }

        boolean getAudioJitterBufferFastAccelerate() {
            return this.audioJitterBufferFastAccelerate;
        }

        int getIceConnectionReceivingTimeout() {
            return this.iceConnectionReceivingTimeout;
        }

        int getIceBackupCandidatePairPingInterval() {
            return this.iceBackupCandidatePairPingInterval;
        }

        KeyType getKeyType() {
            return this.keyType;
        }

        ContinualGatheringPolicy getContinualGatheringPolicy() {
            return this.continualGatheringPolicy;
        }

        int getIceCandidatePoolSize() {
            return this.iceCandidatePoolSize;
        }

        boolean getPruneTurnPorts() {
            return this.pruneTurnPorts;
        }

        boolean getPresumeWritableWhenFullyRelayed() {
            return this.presumeWritableWhenFullyRelayed;
        }

        boolean getSurfaceIceCandidatesOnIceTransportTypeChanged() {
            return this.surfaceIceCandidatesOnIceTransportTypeChanged;
        }

        Integer getIceCheckIntervalStrongConnectivity() {
            return this.iceCheckIntervalStrongConnectivityMs;
        }

        Integer getIceCheckIntervalWeakConnectivity() {
            return this.iceCheckIntervalWeakConnectivityMs;
        }

        Integer getIceCheckMinInterval() {
            return this.iceCheckMinInterval;
        }

        Integer getIceUnwritableTimeout() {
            return this.iceUnwritableTimeMs;
        }

        Integer getIceUnwritableMinChecks() {
            return this.iceUnwritableMinChecks;
        }

        Integer getStunCandidateKeepaliveInterval() {
            return this.stunCandidateKeepaliveIntervalMs;
        }

        boolean getDisableIPv6OnWifi() {
            return this.disableIPv6OnWifi;
        }

        int getMaxIPv6Networks() {
            return this.maxIPv6Networks;
        }

        TurnCustomizer getTurnCustomizer() {
            return this.turnCustomizer;
        }

        boolean getDisableIpv6() {
            return this.disableIpv6;
        }

        boolean getEnableDscp() {
            return this.enableDscp;
        }

        boolean getEnableCpuOveruseDetection() {
            return this.enableCpuOveruseDetection;
        }

        boolean getEnableRtpDataChannel() {
            return this.enableRtpDataChannel;
        }

        boolean getSuspendBelowMinBitrate() {
            return this.suspendBelowMinBitrate;
        }

        Integer getScreencastMinBitrate() {
            return this.screencastMinBitrate;
        }

        Boolean getCombinedAudioVideoBwe() {
            return this.combinedAudioVideoBwe;
        }

        Boolean getEnableDtlsSrtp() {
            return this.enableDtlsSrtp;
        }

        AdapterType getNetworkPreference() {
            return this.networkPreference;
        }

        SdpSemantics getSdpSemantics() {
            return this.sdpSemantics;
        }

        boolean getActiveResetSrtpParams() {
            return this.activeResetSrtpParams;
        }

        Boolean getAllowCodecSwitching() {
            return this.allowCodecSwitching;
        }

        CryptoOptions getCryptoOptions() {
            return this.cryptoOptions;
        }

        String getTurnLoggingId() {
            return this.turnLoggingId;
        }
    }

    public PeerConnection(NativePeerConnectionFactory factory) {
        this(factory.createNativePeerConnection());
    }

    public PeerConnection(long nativePeerConnection) {
        this.localStreams = new ArrayList();
        this.senders = new ArrayList();
        this.receivers = new ArrayList();
        this.transceivers = new ArrayList();
        this.nativePeerConnection = nativePeerConnection;
    }

    public SessionDescription getLocalDescription() {
        return nativeGetLocalDescription();
    }

    public SessionDescription getRemoteDescription() {
        return nativeGetRemoteDescription();
    }

    public RtcCertificatePem getCertificate() {
        return nativeGetCertificate();
    }

    public DataChannel createDataChannel(String label, DataChannel.Init init) {
        return nativeCreateDataChannel(label, init);
    }

    public void createOffer(SdpObserver observer, MediaConstraints constraints) {
        nativeCreateOffer(observer, constraints);
    }

    public void createAnswer(SdpObserver observer, MediaConstraints constraints) {
        nativeCreateAnswer(observer, constraints);
    }

    public void setLocalDescription(SdpObserver observer, SessionDescription sdp) {
        nativeSetLocalDescription(observer, sdp);
    }

    public void setRemoteDescription(SdpObserver observer, SessionDescription sdp) {
        nativeSetRemoteDescription(observer, sdp);
    }

    public void setAudioPlayout(boolean playout) {
        nativeSetAudioPlayout(playout);
    }

    public void setAudioRecording(boolean recording) {
        nativeSetAudioRecording(recording);
    }

    public boolean setConfiguration(RTCConfiguration config) {
        return nativeSetConfiguration(config);
    }

    public boolean addIceCandidate(IceCandidate candidate) {
        return nativeAddIceCandidate(candidate.sdpMid, candidate.sdpMLineIndex, candidate.sdp);
    }

    public boolean removeIceCandidates(IceCandidate[] candidates) {
        return nativeRemoveIceCandidates(candidates);
    }

    public boolean addStream(MediaStream stream) {
        boolean ret = nativeAddLocalStream(stream.getNativeMediaStream());
        if (!ret) {
            return false;
        }
        this.localStreams.add(stream);
        return true;
    }

    public void removeStream(MediaStream stream) {
        nativeRemoveLocalStream(stream.getNativeMediaStream());
        this.localStreams.remove(stream);
    }

    public RtpSender createSender(String kind, String stream_id) {
        RtpSender newSender = nativeCreateSender(kind, stream_id);
        if (newSender != null) {
            this.senders.add(newSender);
        }
        return newSender;
    }

    public List<RtpSender> getSenders() {
        for (RtpSender sender : this.senders) {
            sender.dispose();
        }
        List<RtpSender> nativeGetSenders = nativeGetSenders();
        this.senders = nativeGetSenders;
        return Collections.unmodifiableList(nativeGetSenders);
    }

    public List<RtpReceiver> getReceivers() {
        for (RtpReceiver receiver : this.receivers) {
            receiver.dispose();
        }
        List<RtpReceiver> nativeGetReceivers = nativeGetReceivers();
        this.receivers = nativeGetReceivers;
        return Collections.unmodifiableList(nativeGetReceivers);
    }

    public List<RtpTransceiver> getTransceivers() {
        for (RtpTransceiver transceiver : this.transceivers) {
            transceiver.dispose();
        }
        List<RtpTransceiver> nativeGetTransceivers = nativeGetTransceivers();
        this.transceivers = nativeGetTransceivers;
        return Collections.unmodifiableList(nativeGetTransceivers);
    }

    public RtpSender addTrack(MediaStreamTrack track) {
        return addTrack(track, Collections.emptyList());
    }

    public RtpSender addTrack(MediaStreamTrack track, List<String> streamIds) {
        if (track == null || streamIds == null) {
            throw new NullPointerException("No MediaStreamTrack specified in addTrack.");
        }
        RtpSender newSender = nativeAddTrack(track.getNativeMediaStreamTrack(), streamIds);
        if (newSender == null) {
            throw new IllegalStateException("C++ addTrack failed.");
        }
        this.senders.add(newSender);
        return newSender;
    }

    public boolean removeTrack(RtpSender sender) {
        if (sender == null) {
            throw new NullPointerException("No RtpSender specified for removeTrack.");
        }
        return nativeRemoveTrack(sender.getNativeRtpSender());
    }

    public RtpTransceiver addTransceiver(MediaStreamTrack track) {
        return addTransceiver(track, new RtpTransceiver.RtpTransceiverInit());
    }

    public RtpTransceiver addTransceiver(MediaStreamTrack track, RtpTransceiver.RtpTransceiverInit init) {
        if (track == null) {
            throw new NullPointerException("No MediaStreamTrack specified for addTransceiver.");
        }
        if (init == null) {
            init = new RtpTransceiver.RtpTransceiverInit();
        }
        RtpTransceiver newTransceiver = nativeAddTransceiverWithTrack(track.getNativeMediaStreamTrack(), init);
        if (newTransceiver == null) {
            throw new IllegalStateException("C++ addTransceiver failed.");
        }
        this.transceivers.add(newTransceiver);
        return newTransceiver;
    }

    public RtpTransceiver addTransceiver(MediaStreamTrack.MediaType mediaType) {
        return addTransceiver(mediaType, new RtpTransceiver.RtpTransceiverInit());
    }

    public RtpTransceiver addTransceiver(MediaStreamTrack.MediaType mediaType, RtpTransceiver.RtpTransceiverInit init) {
        if (mediaType == null) {
            throw new NullPointerException("No MediaType specified for addTransceiver.");
        }
        if (init == null) {
            init = new RtpTransceiver.RtpTransceiverInit();
        }
        RtpTransceiver newTransceiver = nativeAddTransceiverOfType(mediaType, init);
        if (newTransceiver == null) {
            throw new IllegalStateException("C++ addTransceiver failed.");
        }
        this.transceivers.add(newTransceiver);
        return newTransceiver;
    }

    @Deprecated
    public boolean getStats(StatsObserver observer, MediaStreamTrack track) {
        return nativeOldGetStats(observer, track == null ? 0L : track.getNativeMediaStreamTrack());
    }

    public void getStats(RTCStatsCollectorCallback callback) {
        nativeNewGetStats(callback);
    }

    public boolean setBitrate(Integer min, Integer current, Integer max) {
        return nativeSetBitrate(min, current, max);
    }

    public boolean startRtcEventLog(int file_descriptor, int max_size_bytes) {
        return nativeStartRtcEventLog(file_descriptor, max_size_bytes);
    }

    public void stopRtcEventLog() {
        nativeStopRtcEventLog();
    }

    public SignalingState signalingState() {
        return nativeSignalingState();
    }

    public IceConnectionState iceConnectionState() {
        return nativeIceConnectionState();
    }

    public PeerConnectionState connectionState() {
        return nativeConnectionState();
    }

    public IceGatheringState iceGatheringState() {
        return nativeIceGatheringState();
    }

    public void close() {
        nativeClose();
    }

    public void dispose() {
        close();
        for (MediaStream stream : this.localStreams) {
            nativeRemoveLocalStream(stream.getNativeMediaStream());
            stream.dispose();
        }
        this.localStreams.clear();
        for (RtpSender sender : this.senders) {
            sender.dispose();
        }
        this.senders.clear();
        for (RtpReceiver receiver : this.receivers) {
            receiver.dispose();
        }
        for (RtpTransceiver transceiver : this.transceivers) {
            transceiver.dispose();
        }
        this.transceivers.clear();
        this.receivers.clear();
        nativeFreeOwnedPeerConnection(this.nativePeerConnection);
    }

    public long getNativePeerConnection() {
        return nativeGetNativePeerConnection();
    }

    long getNativeOwnedPeerConnection() {
        return this.nativePeerConnection;
    }

    public static long createNativePeerConnectionObserver(Observer observer) {
        return nativeCreatePeerConnectionObserver(observer);
    }
}
