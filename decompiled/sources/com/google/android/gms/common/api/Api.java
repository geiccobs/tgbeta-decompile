package com.google.android.gms.common.api;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import androidx.annotation.RecentlyNonNull;
import androidx.annotation.RecentlyNullable;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.common.internal.BaseGmsClient;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.common.internal.Preconditions;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class Api<O extends ApiOptions> {
    private final AbstractClientBuilder<?, O> zaa;
    private final ClientKey<?> zab;
    private final String zac;

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public static abstract class AbstractClientBuilder<T extends Client, O> extends BaseClientBuilder<T, O> {
        @RecentlyNonNull
        @Deprecated
        public T buildClient(@RecentlyNonNull Context context, @RecentlyNonNull Looper looper, @RecentlyNonNull ClientSettings clientSettings, @RecentlyNonNull O o, @RecentlyNonNull GoogleApiClient.ConnectionCallbacks connectionCallbacks, @RecentlyNonNull GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
            return buildClient(context, looper, clientSettings, (ClientSettings) o, (ConnectionCallbacks) connectionCallbacks, (OnConnectionFailedListener) onConnectionFailedListener);
        }

        @RecentlyNonNull
        public T buildClient(@RecentlyNonNull Context context, @RecentlyNonNull Looper looper, @RecentlyNonNull ClientSettings clientSettings, @RecentlyNonNull O o, @RecentlyNonNull ConnectionCallbacks connectionCallbacks, @RecentlyNonNull OnConnectionFailedListener onConnectionFailedListener) {
            throw new UnsupportedOperationException("buildClient must be implemented");
        }
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public interface AnyClient {
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public static class AnyClientKey<C extends AnyClient> {
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public interface ApiOptions {
        @RecentlyNonNull
        public static final NoOptions NO_OPTIONS = new NoOptions();

        /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
        /* loaded from: classes.dex */
        public interface HasAccountOptions extends ApiOptions {
            @RecentlyNonNull
            Account getAccount();
        }

        /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
        /* loaded from: classes.dex */
        public interface HasGoogleSignInAccountOptions extends ApiOptions {
            @RecentlyNullable
            GoogleSignInAccount getGoogleSignInAccount();
        }

        /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
        /* loaded from: classes.dex */
        public static final class NoOptions implements ApiOptions {
            private NoOptions() {
            }
        }
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public interface Client extends AnyClient {
        void connect(@RecentlyNonNull BaseGmsClient.ConnectionProgressReportCallbacks connectionProgressReportCallbacks);

        void disconnect();

        void disconnect(@RecentlyNonNull String str);

        void dump(@RecentlyNonNull String str, FileDescriptor fileDescriptor, @RecentlyNonNull PrintWriter printWriter, String[] strArr);

        @RecentlyNonNull
        Feature[] getAvailableFeatures();

        @RecentlyNonNull
        String getEndpointPackageName();

        @RecentlyNullable
        String getLastDisconnectMessage();

        int getMinApkVersion();

        void getRemoteService(IAccountAccessor iAccountAccessor, Set<Scope> set);

        Set<Scope> getScopesForConnectionlessNonSignIn();

        @RecentlyNonNull
        Intent getSignInIntent();

        boolean isConnected();

        boolean isConnecting();

        void onUserSignOut(@RecentlyNonNull BaseGmsClient.SignOutCallbacks signOutCallbacks);

        boolean providesSignIn();

        boolean requiresGooglePlayServices();

        boolean requiresSignIn();
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public static final class ClientKey<C extends Client> extends AnyClientKey<C> {
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <C extends Client> Api(@RecentlyNonNull String str, @RecentlyNonNull AbstractClientBuilder<C, O> abstractClientBuilder, @RecentlyNonNull ClientKey<C> clientKey) {
        Preconditions.checkNotNull(abstractClientBuilder, "Cannot construct an Api with a null ClientBuilder");
        Preconditions.checkNotNull(clientKey, "Cannot construct an Api with a null ClientKey");
        this.zac = str;
        this.zaa = abstractClientBuilder;
        this.zab = clientKey;
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public static abstract class BaseClientBuilder<T extends AnyClient, O> {
        public int getPriority() {
            return Integer.MAX_VALUE;
        }

        @RecentlyNonNull
        public List<Scope> getImpliedScopes(O o) {
            return Collections.emptyList();
        }
    }

    @RecentlyNonNull
    public final BaseClientBuilder<?, O> zaa() {
        return this.zaa;
    }

    @RecentlyNonNull
    public final AbstractClientBuilder<?, O> zab() {
        return this.zaa;
    }

    @RecentlyNonNull
    public final AnyClientKey<?> zac() {
        return this.zab;
    }

    @RecentlyNonNull
    public final String zad() {
        return this.zac;
    }
}
