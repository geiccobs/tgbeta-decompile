package com.stripe.android.exception;
/* loaded from: classes3.dex */
public class APIConnectionException extends StripeException {
    public APIConnectionException(String message) {
        super(message, null, 0);
    }

    public APIConnectionException(String message, Throwable e) {
        super(message, null, 0, e);
    }
}
