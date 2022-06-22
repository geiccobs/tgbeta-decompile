package com.google.android.gms.auth.api;

import android.os.Bundle;
import androidx.annotation.RecentlyNonNull;
import androidx.annotation.RecentlyNullable;
import com.google.android.gms.auth.api.proxy.ProxyApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.zbd;
import com.google.android.gms.auth.api.signin.internal.zbe;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.internal.p001authapi.zbl;
import com.google.android.gms.internal.p001authapi.zbo;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes.dex */
public final class Auth {
    @RecentlyNonNull
    public static final Api<GoogleSignInOptions> GOOGLE_SIGN_IN_API;
    @RecentlyNonNull
    public static final Api.ClientKey<zbo> zba;
    @RecentlyNonNull
    public static final Api.ClientKey<zbe> zbb;
    private static final Api.AbstractClientBuilder<zbo, AuthCredentialsOptions> zbc;
    private static final Api.AbstractClientBuilder<zbe, GoogleSignInOptions> zbd;

    /* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
    @Deprecated
    /* loaded from: classes.dex */
    public static class AuthCredentialsOptions implements Api.ApiOptions {
        @RecentlyNonNull
        public static final AuthCredentialsOptions zba = new AuthCredentialsOptions(new Builder());
        private final String zbb = null;
        private final boolean zbc;
        private final String zbd;

        /* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
        @Deprecated
        /* loaded from: classes.dex */
        public static class Builder {
            @RecentlyNonNull
            protected Boolean zba;
            @RecentlyNullable
            protected String zbb;

            public Builder() {
                this.zba = Boolean.FALSE;
            }

            @RecentlyNonNull
            public final Builder zba(@RecentlyNonNull String str) {
                this.zbb = str;
                return this;
            }

            public Builder(@RecentlyNonNull AuthCredentialsOptions authCredentialsOptions) {
                this.zba = Boolean.FALSE;
                String unused = authCredentialsOptions.zbb;
                this.zba = Boolean.valueOf(authCredentialsOptions.zbc);
                this.zbb = authCredentialsOptions.zbd;
            }
        }

        public AuthCredentialsOptions(@RecentlyNonNull Builder builder) {
            this.zbc = builder.zba.booleanValue();
            this.zbd = builder.zbb;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof AuthCredentialsOptions)) {
                return false;
            }
            AuthCredentialsOptions authCredentialsOptions = (AuthCredentialsOptions) obj;
            String str = authCredentialsOptions.zbb;
            return Objects.equal(null, null) && this.zbc == authCredentialsOptions.zbc && Objects.equal(this.zbd, authCredentialsOptions.zbd);
        }

        public int hashCode() {
            return Objects.hashCode(null, Boolean.valueOf(this.zbc), this.zbd);
        }

        @RecentlyNonNull
        public final Bundle zba() {
            Bundle bundle = new Bundle();
            bundle.putString("consumer_package", null);
            bundle.putBoolean("force_save_dialog", this.zbc);
            bundle.putString("log_session_id", this.zbd);
            return bundle;
        }
    }

    static {
        Api.ClientKey<zbo> clientKey = new Api.ClientKey<>();
        zba = clientKey;
        Api.ClientKey<zbe> clientKey2 = new Api.ClientKey<>();
        zbb = clientKey2;
        zba zbaVar = new zba();
        zbc = zbaVar;
        zbb zbbVar = new zbb();
        zbd = zbbVar;
        Api<AuthProxyOptions> api = AuthProxy.API;
        new Api("Auth.CREDENTIALS_API", zbaVar, clientKey);
        GOOGLE_SIGN_IN_API = new Api<>("Auth.GOOGLE_SIGN_IN_API", zbbVar, clientKey2);
        ProxyApi proxyApi = AuthProxy.ProxyApi;
        new zbl();
        new zbd();
    }
}
