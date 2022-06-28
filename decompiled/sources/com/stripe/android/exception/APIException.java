package com.stripe.android.exception;
/* loaded from: classes3.dex */
public class APIException extends StripeException {
    public APIException(String message, String requestId, Integer statusCode, Throwable e) {
        super(message, requestId, statusCode, e);
    }
}
