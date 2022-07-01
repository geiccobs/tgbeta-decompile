package com.google.android.gms.common.api.internal;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Result;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public interface zabo {
    <A extends Api.AnyClient, R extends Result, T extends BaseImplementation$ApiMethodImpl<R, A>> T zaa(T t);

    void zaa();

    void zaa(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr);

    boolean zaa(SignInConnectionListener signInConnectionListener);

    <A extends Api.AnyClient, T extends BaseImplementation$ApiMethodImpl<? extends Result, A>> T zab(T t);

    void zac();

    boolean zad();

    void zaf();

    void zag();
}
