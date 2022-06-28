package com.stripe.android.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
/* loaded from: classes3.dex */
public class StripeSSLSocketFactory extends SSLSocketFactory {
    private static final String TLSv11Proto = "TLSv1.1";
    private static final String TLSv12Proto = "TLSv1.2";
    private final boolean tlsv11Supported;
    private final boolean tlsv12Supported;
    private final SSLSocketFactory under = HttpsURLConnection.getDefaultSSLSocketFactory();

    public StripeSSLSocketFactory() {
        String[] supportedProtocols;
        boolean tlsv11Supported = false;
        boolean tlsv12Supported = false;
        try {
            supportedProtocols = SSLContext.getDefault().getSupportedSSLParameters().getProtocols();
        } catch (NoSuchAlgorithmException e) {
            supportedProtocols = new String[0];
        }
        for (String proto : supportedProtocols) {
            if (proto.equals(TLSv11Proto)) {
                tlsv11Supported = true;
            } else if (proto.equals(TLSv12Proto)) {
                tlsv12Supported = true;
            }
        }
        this.tlsv11Supported = tlsv11Supported;
        this.tlsv12Supported = tlsv12Supported;
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public String[] getDefaultCipherSuites() {
        return this.under.getDefaultCipherSuites();
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public String[] getSupportedCipherSuites() {
        return this.under.getSupportedCipherSuites();
    }

    @Override // javax.net.ssl.SSLSocketFactory
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return fixupSocket(this.under.createSocket(s, host, port, autoClose));
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(String host, int port) throws IOException {
        return fixupSocket(this.under.createSocket(host, port));
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return fixupSocket(this.under.createSocket(host, port, localHost, localPort));
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return fixupSocket(this.under.createSocket(host, port));
    }

    @Override // javax.net.SocketFactory
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return fixupSocket(this.under.createSocket(address, port, localAddress, localPort));
    }

    private Socket fixupSocket(Socket sock) {
        if (!(sock instanceof SSLSocket)) {
            return sock;
        }
        SSLSocket sslSock = (SSLSocket) sock;
        Set<String> protos = new HashSet<>(Arrays.asList(sslSock.getEnabledProtocols()));
        if (this.tlsv11Supported) {
            protos.add(TLSv11Proto);
        }
        if (this.tlsv12Supported) {
            protos.add(TLSv12Proto);
        }
        sslSock.setEnabledProtocols((String[]) protos.toArray(new String[0]));
        return sslSock;
    }
}
