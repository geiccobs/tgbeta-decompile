package com.google.android.gms.common.api;

import android.accounts.Account;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RecentlyNonNull;
import androidx.annotation.RecentlyNullable;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.ApiOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ApiExceptionMapper;
import com.google.android.gms.common.api.internal.ApiKey;
import com.google.android.gms.common.api.internal.BaseImplementation$ApiMethodImpl;
import com.google.android.gms.common.api.internal.GoogleApiManager;
import com.google.android.gms.common.api.internal.NonGmsServiceBrokerClient;
import com.google.android.gms.common.api.internal.StatusExceptionMapper;
import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.android.gms.common.api.internal.zabl;
import com.google.android.gms.common.api.internal.zace;
import com.google.android.gms.common.internal.BaseGmsClient;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.PlatformVersion;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Set;
import org.checkerframework.checker.initialization.qual.NotOnlyInitialized;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public class GoogleApi<O extends Api.ApiOptions> {
    private final Context zaa;
    private final String zab;
    private final Api<O> zac;
    private final O zad;
    private final ApiKey<O> zae;
    private final Looper zaf;
    private final int zag;
    @NotOnlyInitialized
    private final GoogleApiClient zah;
    private final StatusExceptionMapper zai;
    private final GoogleApiManager zaj;

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public static class Settings {
        @RecentlyNonNull
        public static final Settings DEFAULT_SETTINGS = new Builder().build();
        @RecentlyNonNull
        public final StatusExceptionMapper zaa;
        @RecentlyNonNull
        public final Looper zab;

        /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
        /* loaded from: classes.dex */
        public static class Builder {
            private StatusExceptionMapper zaa;
            private Looper zab;

            @RecentlyNonNull
            public Builder setMapper(@RecentlyNonNull StatusExceptionMapper statusExceptionMapper) {
                Preconditions.checkNotNull(statusExceptionMapper, "StatusExceptionMapper must not be null.");
                this.zaa = statusExceptionMapper;
                return this;
            }

            @RecentlyNonNull
            public Settings build() {
                if (this.zaa == null) {
                    this.zaa = new ApiExceptionMapper();
                }
                if (this.zab == null) {
                    this.zab = Looper.getMainLooper();
                }
                return new Settings(this.zaa, this.zab);
            }
        }

        private Settings(StatusExceptionMapper statusExceptionMapper, Account account, Looper looper) {
            this.zaa = statusExceptionMapper;
            this.zab = looper;
        }
    }

    public GoogleApi(@RecentlyNonNull Context context, @RecentlyNonNull Api<O> api, @RecentlyNonNull O o, @RecentlyNonNull Settings settings) {
        Preconditions.checkNotNull(context, "Null context is not permitted.");
        Preconditions.checkNotNull(api, "Api must not be null.");
        Preconditions.checkNotNull(settings, "Settings must not be null; use Settings.DEFAULT_SETTINGS instead.");
        Context applicationContext = context.getApplicationContext();
        this.zaa = applicationContext;
        String zaa = zaa(context);
        this.zab = zaa;
        this.zac = api;
        this.zad = o;
        this.zaf = settings.zab;
        this.zae = ApiKey.zaa(api, o, zaa);
        this.zah = new zabl(this);
        GoogleApiManager zaa2 = GoogleApiManager.zaa(applicationContext);
        this.zaj = zaa2;
        this.zag = zaa2.zab();
        this.zai = settings.zaa;
        zaa2.zaa((GoogleApi<?>) this);
    }

    @Deprecated
    public GoogleApi(@RecentlyNonNull Context context, @RecentlyNonNull Api<O> api, @RecentlyNonNull O o, @RecentlyNonNull StatusExceptionMapper statusExceptionMapper) {
        this(context, api, o, new Settings.Builder().setMapper(statusExceptionMapper).build());
    }

    private final <A extends Api.AnyClient, T extends BaseImplementation$ApiMethodImpl<? extends Result, A>> T zaa(int i, T t) {
        t.zab();
        this.zaj.zaa(this, i, (BaseImplementation$ApiMethodImpl<? extends Result, Api.AnyClient>) t);
        return t;
    }

    private final <TResult, A extends Api.AnyClient> Task<TResult> zaa(int i, TaskApiCall<A, TResult> taskApiCall) {
        TaskCompletionSource taskCompletionSource = new TaskCompletionSource();
        this.zaj.zaa(this, i, taskApiCall, taskCompletionSource, this.zai);
        return taskCompletionSource.getTask();
    }

    @RecentlyNonNull
    public <A extends Api.AnyClient, T extends BaseImplementation$ApiMethodImpl<? extends Result, A>> T doRead(@RecentlyNonNull T t) {
        return (T) zaa(0, (int) t);
    }

    @RecentlyNonNull
    public <TResult, A extends Api.AnyClient> Task<TResult> doRead(@RecentlyNonNull TaskApiCall<A, TResult> taskApiCall) {
        return zaa(0, taskApiCall);
    }

    @RecentlyNonNull
    public <A extends Api.AnyClient, T extends BaseImplementation$ApiMethodImpl<? extends Result, A>> T doWrite(@RecentlyNonNull T t) {
        return (T) zaa(1, (int) t);
    }

    @RecentlyNonNull
    public <TResult, A extends Api.AnyClient> Task<TResult> doWrite(@RecentlyNonNull TaskApiCall<A, TResult> taskApiCall) {
        return zaa(1, taskApiCall);
    }

    @RecentlyNonNull
    public <A extends Api.AnyClient, T extends BaseImplementation$ApiMethodImpl<? extends Result, A>> T doBestEffortWrite(@RecentlyNonNull T t) {
        return (T) zaa(2, (int) t);
    }

    @RecentlyNonNull
    public <TResult, A extends Api.AnyClient> Task<TResult> doBestEffortWrite(@RecentlyNonNull TaskApiCall<A, TResult> taskApiCall) {
        return zaa(2, taskApiCall);
    }

    public final Api.Client zaa(Looper looper, GoogleApiManager.zaa<O> zaaVar) {
        Api.Client buildClient = ((Api.AbstractClientBuilder) Preconditions.checkNotNull(this.zac.zab())).buildClient(this.zaa, looper, createClientSettingsBuilder().build(), (ClientSettings) this.zad, (GoogleApiClient.ConnectionCallbacks) zaaVar, (GoogleApiClient.OnConnectionFailedListener) zaaVar);
        String contextAttributionTag = getContextAttributionTag();
        if (contextAttributionTag != null && (buildClient instanceof BaseGmsClient)) {
            ((BaseGmsClient) buildClient).setAttributionTag(contextAttributionTag);
        }
        if (contextAttributionTag != null && (buildClient instanceof NonGmsServiceBrokerClient)) {
            ((NonGmsServiceBrokerClient) buildClient).zaa(contextAttributionTag);
        }
        return buildClient;
    }

    @RecentlyNonNull
    public ApiKey<O> getApiKey() {
        return this.zae;
    }

    public final int zaa() {
        return this.zag;
    }

    @RecentlyNonNull
    public GoogleApiClient asGoogleApiClient() {
        return this.zah;
    }

    @RecentlyNonNull
    public Looper getLooper() {
        return this.zaf;
    }

    @RecentlyNonNull
    public Context getApplicationContext() {
        return this.zaa;
    }

    @RecentlyNullable
    protected String getContextAttributionTag() {
        return this.zab;
    }

    @RecentlyNonNull
    protected ClientSettings.Builder createClientSettingsBuilder() {
        Account account;
        Set<Scope> set;
        GoogleSignInAccount googleSignInAccount;
        GoogleSignInAccount googleSignInAccount2;
        ClientSettings.Builder builder = new ClientSettings.Builder();
        O o = this.zad;
        if ((o instanceof Api.ApiOptions.HasGoogleSignInAccountOptions) && (googleSignInAccount2 = ((Api.ApiOptions.HasGoogleSignInAccountOptions) o).getGoogleSignInAccount()) != null) {
            account = googleSignInAccount2.getAccount();
        } else {
            O o2 = this.zad;
            account = o2 instanceof Api.ApiOptions.HasAccountOptions ? ((Api.ApiOptions.HasAccountOptions) o2).getAccount() : null;
        }
        ClientSettings.Builder zaa = builder.zaa(account);
        O o3 = this.zad;
        if ((o3 instanceof Api.ApiOptions.HasGoogleSignInAccountOptions) && (googleSignInAccount = ((Api.ApiOptions.HasGoogleSignInAccountOptions) o3).getGoogleSignInAccount()) != null) {
            set = googleSignInAccount.getRequestedScopes();
        } else {
            set = Collections.emptySet();
        }
        return zaa.zaa(set).zaa(this.zaa.getClass().getName()).setRealClientPackageName(this.zaa.getPackageName());
    }

    private static String zaa(Object obj) {
        if (PlatformVersion.isAtLeastR()) {
            try {
                return (String) Context.class.getMethod("getAttributionTag", new Class[0]).invoke(obj, new Object[0]);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException unused) {
                return null;
            }
        }
        return null;
    }

    public final zace zaa(Context context, Handler handler) {
        return new zace(context, handler, createClientSettingsBuilder().build());
    }
}
