package com.google.firebase.messaging;

import java.util.Locale;
/* compiled from: com.google.firebase:firebase-messaging@@22.0.0 */
/* loaded from: classes.dex */
public final class SendException extends Exception {
    public SendException(String str) {
        super(str);
        parseErrorCode(str);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private int parseErrorCode(String str) {
        char c;
        if (str == null) {
            return 0;
        }
        String lowerCase = str.toLowerCase(Locale.US);
        switch (lowerCase.hashCode()) {
            case -1743242157:
                if (lowerCase.equals("service_not_available")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1290953729:
                if (lowerCase.equals("toomanymessages")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -920906446:
                if (lowerCase.equals("invalid_parameters")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -617027085:
                if (lowerCase.equals("messagetoobig")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -95047692:
                if (lowerCase.equals("missing_to")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        if (c == 0 || c == 1) {
            return 1;
        }
        if (c == 2) {
            return 2;
        }
        if (c == 3) {
            return 3;
        }
        return c != 4 ? 0 : 4;
    }
}
