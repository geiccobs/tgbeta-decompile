package com.microsoft.appcenter;
/* loaded from: classes3.dex */
public class CancellationException extends Exception {
    public CancellationException() {
        super("Request cancelled because Channel is disabled.");
    }
}
