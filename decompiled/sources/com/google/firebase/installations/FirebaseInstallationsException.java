package com.google.firebase.installations;

import com.google.firebase.FirebaseException;
/* loaded from: classes.dex */
public class FirebaseInstallationsException extends FirebaseException {

    /* loaded from: classes.dex */
    public enum Status {
        BAD_CONFIG,
        UNAVAILABLE,
        TOO_MANY_REQUESTS
    }

    public FirebaseInstallationsException(Status status) {
    }

    public FirebaseInstallationsException(String str, Status status) {
        super(str);
    }
}
