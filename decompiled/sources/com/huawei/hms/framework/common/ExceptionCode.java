package com.huawei.hms.framework.common;

import android.text.TextUtils;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLProtocolException;
/* loaded from: classes.dex */
public class ExceptionCode {
    public static final int CANCEL = 10000100;
    private static final String CONNECT = "connect";
    public static final int CONNECTION_ABORT = 10000402;
    public static final int CONNECTION_REFUSED = 10000404;
    public static final int CONNECTION_RESET = 10000401;
    public static final int CONNECT_FAILED = 10000403;
    public static final int CRASH_EXCEPTION = 10000000;
    public static final int INTERRUPT_CONNECT_CLOSE = 10000405;
    public static final int INTERRUPT_EXCEPTION = 10000804;
    public static final int NETWORK_CHANGED = 10000201;
    public static final int NETWORK_IO_EXCEPTION = 10000802;
    public static final int NETWORK_TIMEOUT = 10000101;
    public static final int NETWORK_UNREACHABLE = 10000200;
    public static final int NETWORK_UNSUPPORTED = 10000102;
    public static final int PROTOCOL_ERROR = 10000801;
    private static final String READ = "read";
    public static final int READ_ERROR = 10000601;
    public static final int ROUTE_FAILED = 10000301;
    public static final int SHUTDOWN_EXCEPTION = 10000202;
    public static final int SOCKET_CLOSE = 10000406;
    public static final int SOCKET_CONNECT_TIMEOUT = 10000400;
    public static final int SOCKET_READ_TIMEOUT = 10000600;
    public static final int SOCKET_TIMEOUT = 10000803;
    public static final int SOCKET_WRITE_TIMEOUT = 10000700;
    public static final int SSL_HANDSHAKE_EXCEPTION = 10000501;
    public static final int SSL_PEERUNVERIFIED_EXCEPTION = 10000502;
    public static final int SSL_PROTOCOL_EXCEPTION = 10000500;
    public static final int UNABLE_TO_RESOLVE_HOST = 10000300;
    public static final int UNEXPECTED_EOF = 10000800;
    private static final String WRITE = "write";

    public static int getErrorCodeFromException(Exception exc) {
        if (exc == null) {
            return NETWORK_IO_EXCEPTION;
        }
        if (!(exc instanceof IOException)) {
            return CRASH_EXCEPTION;
        }
        String message = exc.getMessage();
        if (message == null) {
            return NETWORK_IO_EXCEPTION;
        }
        String lowerCase = StringUtils.toLowerCase(message);
        int errorCodeFromMsg = getErrorCodeFromMsg(lowerCase);
        if (errorCodeFromMsg != 10000802) {
            return errorCodeFromMsg;
        }
        if (exc instanceof SocketTimeoutException) {
            return getErrorCodeSocketTimeout(exc);
        }
        return exc instanceof ConnectException ? CONNECT_FAILED : exc instanceof NoRouteToHostException ? ROUTE_FAILED : exc instanceof SSLProtocolException ? SSL_PROTOCOL_EXCEPTION : exc instanceof SSLHandshakeException ? SSL_HANDSHAKE_EXCEPTION : exc instanceof SSLPeerUnverifiedException ? SSL_PEERUNVERIFIED_EXCEPTION : exc instanceof UnknownHostException ? UNABLE_TO_RESOLVE_HOST : exc instanceof InterruptedIOException ? lowerCase.contains("connection has been shut down") ? INTERRUPT_CONNECT_CLOSE : INTERRUPT_EXCEPTION : exc instanceof ProtocolException ? PROTOCOL_ERROR : errorCodeFromMsg;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0039, code lost:
        if (r8.equals(com.huawei.hms.framework.common.ExceptionCode.READ) == false) goto L4;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static int getErrorCodeSocketTimeout(java.lang.Exception r8) {
        /*
            r0 = 3
            java.lang.String[] r0 = new java.lang.String[r0]
            r1 = 0
            java.lang.String r2 = "connect"
            r0[r1] = r2
            r3 = 1
            java.lang.String r4 = "read"
            r0[r3] = r4
            r5 = 2
            java.lang.String r6 = "write"
            r0[r5] = r6
            java.lang.String r8 = checkExceptionContainsKey(r8, r0)
            r8.hashCode()
            int r0 = r8.hashCode()
            r7 = -1
            switch(r0) {
                case 3496342: goto L35;
                case 113399775: goto L2c;
                case 951351530: goto L23;
                default: goto L21;
            }
        L21:
            r1 = -1
            goto L3c
        L23:
            boolean r8 = r8.equals(r2)
            if (r8 != 0) goto L2a
            goto L21
        L2a:
            r1 = 2
            goto L3c
        L2c:
            boolean r8 = r8.equals(r6)
            if (r8 != 0) goto L33
            goto L21
        L33:
            r1 = 1
            goto L3c
        L35:
            boolean r8 = r8.equals(r4)
            if (r8 != 0) goto L3c
            goto L21
        L3c:
            switch(r1) {
                case 0: goto L4b;
                case 1: goto L47;
                case 2: goto L43;
                default: goto L3f;
            }
        L3f:
            r8 = 10000803(0x9899a3, float:1.401411E-38)
            return r8
        L43:
            r8 = 10000400(0x989810, float:1.4013545E-38)
            return r8
        L47:
            r8 = 10000700(0x98993c, float:1.4013966E-38)
            return r8
        L4b:
            r8 = 10000600(0x9898d8, float:1.4013825E-38)
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.huawei.hms.framework.common.ExceptionCode.getErrorCodeSocketTimeout(java.lang.Exception):int");
    }

    private static int getErrorCodeFromMsg(String str) {
        return str.contains("unexpected end of stream") ? UNEXPECTED_EOF : str.contains("unable to resolve host") ? UNABLE_TO_RESOLVE_HOST : str.contains("read error") ? READ_ERROR : str.contains("connection reset") ? CONNECTION_RESET : str.contains("software caused connection abort") ? CONNECTION_ABORT : str.contains("failed to connect to") ? CONNECT_FAILED : str.contains("connection refused") ? CONNECTION_REFUSED : str.contains("connection timed out") ? SOCKET_CONNECT_TIMEOUT : str.contains("no route to host") ? ROUTE_FAILED : str.contains("network is unreachable") ? NETWORK_UNREACHABLE : str.contains("socket closed") ? SOCKET_CLOSE : NETWORK_IO_EXCEPTION;
    }

    private static String checkExceptionContainsKey(Exception exc, String... strArr) {
        return checkStrContainsKey(StringUtils.toLowerCase(exc.getMessage()), strArr);
    }

    private static String checkStrContainsKey(String str, String... strArr) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        for (String str2 : strArr) {
            if (str.contains(str2)) {
                return str2;
            }
        }
        return "";
    }
}
