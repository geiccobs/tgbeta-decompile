package com.stripe.android.exception;
/* loaded from: classes.dex */
public abstract class StripeException extends Exception {
    private String requestId;

    public StripeException(String str, String str2, Integer num) {
        super(str, null);
        this.requestId = str2;
    }

    public StripeException(String str, String str2, Integer num, Throwable th) {
        super(str, th);
        this.requestId = str2;
    }

    @Override // java.lang.Throwable
    public String toString() {
        String str;
        if (this.requestId != null) {
            str = "; request-id: " + this.requestId;
        } else {
            str = "";
        }
        return super.toString() + str;
    }
}
