package com.google.mlkit.common;

import com.google.android.gms.common.internal.Preconditions;
/* compiled from: com.google.mlkit:common@@17.0.0 */
/* loaded from: classes.dex */
public class MlKitException extends Exception {
    public MlKitException(String str, int i) {
        super(Preconditions.checkNotEmpty(str, "Provided message must not be empty."));
    }

    public MlKitException(String str, int i, Throwable th) {
        super(Preconditions.checkNotEmpty(str, "Provided message must not be empty."), th);
    }
}
