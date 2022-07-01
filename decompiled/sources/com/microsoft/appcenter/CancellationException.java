package com.microsoft.appcenter;
/* loaded from: classes.dex */
public class CancellationException extends Exception {
    public CancellationException() {
        super("Request cancelled because Channel is disabled.");
    }
}
