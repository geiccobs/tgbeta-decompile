package com.google.android.gms.common.api;

import android.accounts.Account;
import android.content.Context;
import android.os.Looper;
import android.view.View;
import androidx.annotation.RecentlyNonNull;
import androidx.collection.ArrayMap;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.internal.BaseImplementation$ApiMethodImpl;
import com.google.android.gms.common.api.internal.LifecycleActivity;
import com.google.android.gms.common.api.internal.SignInConnectionListener;
import com.google.android.gms.common.api.internal.zaar;
import com.google.android.gms.common.api.internal.zacn;
import com.google.android.gms.common.api.internal.zai;
import com.google.android.gms.common.api.internal.zaq;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.signin.zab;
import com.google.android.gms.signin.zae;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.concurrent.GuardedBy;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
@Deprecated
/* loaded from: classes.dex */
public abstract class GoogleApiClient {
    @GuardedBy("sAllClients")
    private static final Set<GoogleApiClient> zaa = Collections.newSetFromMap(new WeakHashMap());

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    @Deprecated
    /* loaded from: classes.dex */
    public interface ConnectionCallbacks extends com.google.android.gms.common.api.internal.ConnectionCallbacks {
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    @Deprecated
    /* loaded from: classes.dex */
    public interface OnConnectionFailedListener extends com.google.android.gms.common.api.internal.OnConnectionFailedListener {
    }

    public abstract void connect();

    public abstract void disconnect();

    public abstract void dump(@RecentlyNonNull String str, @RecentlyNonNull FileDescriptor fileDescriptor, @RecentlyNonNull PrintWriter printWriter, @RecentlyNonNull String[] strArr);

    public abstract boolean isConnected();

    public abstract void registerConnectionFailedListener(@RecentlyNonNull OnConnectionFailedListener onConnectionFailedListener);

    public abstract void unregisterConnectionFailedListener(@RecentlyNonNull OnConnectionFailedListener onConnectionFailedListener);

    @RecentlyNonNull
    public static Set<GoogleApiClient> getAllClients() {
        Set<GoogleApiClient> set = zaa;
        synchronized (set) {
        }
        return set;
    }

    @RecentlyNonNull
    public <A extends Api.AnyClient, R extends Result, T extends BaseImplementation$ApiMethodImpl<R, A>> T enqueue(@RecentlyNonNull T t) {
        throw new UnsupportedOperationException();
    }

    @RecentlyNonNull
    public <A extends Api.AnyClient, T extends BaseImplementation$ApiMethodImpl<? extends Result, A>> T execute(@RecentlyNonNull T t) {
        throw new UnsupportedOperationException();
    }

    public <C extends Api.Client> C getClient(@RecentlyNonNull Api.AnyClientKey<C> anyClientKey) {
        throw new UnsupportedOperationException();
    }

    @RecentlyNonNull
    public Context getContext() {
        throw new UnsupportedOperationException();
    }

    @RecentlyNonNull
    public Looper getLooper() {
        throw new UnsupportedOperationException();
    }

    public boolean maybeSignIn(@RecentlyNonNull SignInConnectionListener signInConnectionListener) {
        throw new UnsupportedOperationException();
    }

    public void maybeSignOut() {
        throw new UnsupportedOperationException();
    }

    public void connect(int i) {
        throw new UnsupportedOperationException();
    }

    public void zab(zacn zacnVar) {
        throw new UnsupportedOperationException();
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    @Deprecated
    /* loaded from: classes.dex */
    public static final class Builder {
        private Account zaa;
        private int zad;
        private View zae;
        private String zaf;
        private String zag;
        private final Context zaj;
        private LifecycleActivity zal;
        private OnConnectionFailedListener zan;
        private Looper zao;
        private final Set<Scope> zab = new HashSet();
        private final Set<Scope> zac = new HashSet();
        private final Map<Api<?>, ClientSettings.zaa> zah = new ArrayMap();
        private final Map<Api<?>, Api.ApiOptions> zak = new ArrayMap();
        private int zam = -1;
        private GoogleApiAvailability zap = GoogleApiAvailability.getInstance();
        private Api.AbstractClientBuilder<? extends zae, SignInOptions> zaq = zab.zaa;
        private final ArrayList<ConnectionCallbacks> zar = new ArrayList<>();
        private final ArrayList<OnConnectionFailedListener> zas = new ArrayList<>();

        public Builder(@RecentlyNonNull Context context) {
            this.zaj = context;
            this.zao = context.getMainLooper();
            this.zaf = context.getPackageName();
            this.zag = context.getClass().getName();
        }

        @RecentlyNonNull
        public final Builder addConnectionCallbacks(@RecentlyNonNull ConnectionCallbacks connectionCallbacks) {
            Preconditions.checkNotNull(connectionCallbacks, "Listener must not be null");
            this.zar.add(connectionCallbacks);
            return this;
        }

        @RecentlyNonNull
        public final Builder addOnConnectionFailedListener(@RecentlyNonNull OnConnectionFailedListener onConnectionFailedListener) {
            Preconditions.checkNotNull(onConnectionFailedListener, "Listener must not be null");
            this.zas.add(onConnectionFailedListener);
            return this;
        }

        @RecentlyNonNull
        public final Builder addApi(@RecentlyNonNull Api<Object> api) {
            Preconditions.checkNotNull(api, "Api must not be null");
            this.zak.put(api, null);
            List<Scope> impliedScopes = ((Api.BaseClientBuilder) Preconditions.checkNotNull(api.zaa(), "Base client builder must not be null")).getImpliedScopes(null);
            this.zac.addAll(impliedScopes);
            this.zab.addAll(impliedScopes);
            return this;
        }

        @RecentlyNonNull
        public final ClientSettings buildClientSettings() {
            SignInOptions signInOptions = SignInOptions.zaa;
            Map<Api<?>, Api.ApiOptions> map = this.zak;
            Api<SignInOptions> api = zab.zab;
            if (map.containsKey(api)) {
                signInOptions = (SignInOptions) this.zak.get(api);
            }
            return new ClientSettings(this.zaa, this.zab, this.zah, this.zad, this.zae, this.zaf, this.zag, signInOptions, false);
        }

        @RecentlyNonNull
        public final GoogleApiClient build() {
            Preconditions.checkArgument(!this.zak.isEmpty(), "must call addApi() to add at least one API");
            ClientSettings buildClientSettings = buildClientSettings();
            Api<?> api = null;
            Map<Api<?>, ClientSettings.zaa> zaa = buildClientSettings.zaa();
            ArrayMap arrayMap = new ArrayMap();
            ArrayMap arrayMap2 = new ArrayMap();
            ArrayList arrayList = new ArrayList();
            boolean z = false;
            for (Api<?> api2 : this.zak.keySet()) {
                Api.ApiOptions apiOptions = this.zak.get(api2);
                boolean z2 = zaa.get(api2) != null;
                arrayMap.put(api2, Boolean.valueOf(z2));
                zaq zaqVar = new zaq(api2, z2);
                arrayList.add(zaqVar);
                Api.AbstractClientBuilder abstractClientBuilder = (Api.AbstractClientBuilder) Preconditions.checkNotNull(api2.zab());
                Api.Client buildClient = abstractClientBuilder.buildClient(this.zaj, this.zao, buildClientSettings, (ClientSettings) apiOptions, (ConnectionCallbacks) zaqVar, (OnConnectionFailedListener) zaqVar);
                arrayMap2.put(api2.zac(), buildClient);
                if (abstractClientBuilder.getPriority() == 1) {
                    z = apiOptions != null;
                }
                if (buildClient.providesSignIn()) {
                    if (api != null) {
                        String zad = api2.zad();
                        String zad2 = api.zad();
                        StringBuilder sb = new StringBuilder(String.valueOf(zad).length() + 21 + String.valueOf(zad2).length());
                        sb.append(zad);
                        sb.append(" cannot be used with ");
                        sb.append(zad2);
                        throw new IllegalStateException(sb.toString());
                    }
                    api = api2;
                }
            }
            if (api != null) {
                if (z) {
                    String zad3 = api.zad();
                    StringBuilder sb2 = new StringBuilder(String.valueOf(zad3).length() + 82);
                    sb2.append("With using ");
                    sb2.append(zad3);
                    sb2.append(", GamesOptions can only be specified within GoogleSignInOptions.Builder");
                    throw new IllegalStateException(sb2.toString());
                }
                Preconditions.checkState(this.zaa == null, "Must not set an account in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead", api.zad());
                Preconditions.checkState(this.zab.equals(this.zac), "Must not set scopes in GoogleApiClient.Builder when using %s. Set account in GoogleSignInOptions.Builder instead.", api.zad());
            }
            zaar zaarVar = new zaar(this.zaj, new ReentrantLock(), this.zao, buildClientSettings, this.zap, this.zaq, arrayMap, this.zar, this.zas, arrayMap2, this.zam, zaar.zaa((Iterable<Api.Client>) arrayMap2.values(), true), arrayList);
            synchronized (GoogleApiClient.zaa) {
                GoogleApiClient.zaa.add(zaarVar);
            }
            if (this.zam >= 0) {
                zai.zaa(this.zal).zaa(this.zam, zaarVar, this.zan);
            }
            return zaarVar;
        }
    }
}
