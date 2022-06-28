package org.webrtc;

import org.webrtc.PeerConnection;
/* loaded from: classes5.dex */
public final class PeerConnectionDependencies {
    private final PeerConnection.Observer observer;
    private final SSLCertificateVerifier sslCertificateVerifier;

    /* loaded from: classes5.dex */
    public static class Builder {
        private PeerConnection.Observer observer;
        private SSLCertificateVerifier sslCertificateVerifier;

        private Builder(PeerConnection.Observer observer) {
            this.observer = observer;
        }

        public Builder setSSLCertificateVerifier(SSLCertificateVerifier sslCertificateVerifier) {
            this.sslCertificateVerifier = sslCertificateVerifier;
            return this;
        }

        public PeerConnectionDependencies createPeerConnectionDependencies() {
            return new PeerConnectionDependencies(this.observer, this.sslCertificateVerifier);
        }
    }

    public static Builder builder(PeerConnection.Observer observer) {
        return new Builder(observer);
    }

    public PeerConnection.Observer getObserver() {
        return this.observer;
    }

    public SSLCertificateVerifier getSSLCertificateVerifier() {
        return this.sslCertificateVerifier;
    }

    private PeerConnectionDependencies(PeerConnection.Observer observer, SSLCertificateVerifier sslCertificateVerifier) {
        this.observer = observer;
        this.sslCertificateVerifier = sslCertificateVerifier;
    }
}
