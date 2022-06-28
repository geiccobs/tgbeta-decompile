package com.google.android.gms.signin;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.signin.internal.SignInClientImpl;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class zab {
    public static final Api.AbstractClientBuilder<SignInClientImpl, SignInOptions> zaa;
    public static final Api<SignInOptions> zab;
    private static final Api.ClientKey<SignInClientImpl> zac;
    private static final Api.ClientKey<SignInClientImpl> zad;
    private static final Api.AbstractClientBuilder<SignInClientImpl, zac> zae;
    private static final Scope zaf = new Scope(Scopes.PROFILE);
    private static final Scope zag = new Scope("email");
    private static final Api<zac> zah;

    static {
        Api.ClientKey<SignInClientImpl> clientKey = new Api.ClientKey<>();
        zac = clientKey;
        Api.ClientKey<SignInClientImpl> clientKey2 = new Api.ClientKey<>();
        zad = clientKey2;
        zaa zaaVar = new zaa();
        zaa = zaaVar;
        zad zadVar = new zad();
        zae = zadVar;
        zab = new Api<>("SignIn.API", zaaVar, clientKey);
        zah = new Api<>("SignIn.INTERNAL_API", zadVar, clientKey2);
    }
}
