package org.webrtc;
/* loaded from: classes5.dex */
public final class CandidatePairChangeEvent {
    public final int estimatedDisconnectedTimeMs;
    public final int lastDataReceivedMs;
    public final IceCandidate local;
    public final String reason;
    public final IceCandidate remote;

    CandidatePairChangeEvent(IceCandidate local, IceCandidate remote, int lastDataReceivedMs, String reason, int estimatedDisconnectedTimeMs) {
        this.local = local;
        this.remote = remote;
        this.lastDataReceivedMs = lastDataReceivedMs;
        this.reason = reason;
        this.estimatedDisconnectedTimeMs = estimatedDisconnectedTimeMs;
    }
}
