package com.microsoft.appcenter.crashes.model;
/* loaded from: classes.dex */
public class NativeException extends RuntimeException {
    public NativeException() {
        super("Native exception read from a minidump file");
    }
}
