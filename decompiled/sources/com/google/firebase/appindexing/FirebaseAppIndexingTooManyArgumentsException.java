package com.google.firebase.appindexing;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public class FirebaseAppIndexingTooManyArgumentsException extends FirebaseAppIndexingException {
    public FirebaseAppIndexingTooManyArgumentsException() {
        super("Too many Indexables provided. Try splitting them in batches.");
    }

    public FirebaseAppIndexingTooManyArgumentsException(String message) {
        super(message);
    }

    public FirebaseAppIndexingTooManyArgumentsException(String message, Throwable cause) {
        super(message, cause);
    }
}
