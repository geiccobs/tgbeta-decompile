package com.google.android.gms.common.api.internal;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import androidx.annotation.RecentlyNonNull;
import androidx.collection.ArrayMap;
import androidx.collection.ArraySet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.UnsupportedApiCallException;
import com.google.android.gms.common.api.internal.ListenerHolder;
import com.google.android.gms.common.internal.BaseGmsClient;
import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.RootTelemetryConfigManager;
import com.google.android.gms.common.internal.RootTelemetryConfiguration;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.gms.common.util.DeviceProperties;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.concurrent.GuardedBy;
import org.checkerframework.checker.initialization.qual.NotOnlyInitialized;
import org.telegram.messenger.R;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public class GoogleApiManager implements Handler.Callback {
    @RecentlyNonNull
    public static final Status zaa = new Status(4, "Sign-out occurred while this API call was in progress.");
    private static final Status zab = new Status(4, "The user must be signed in to make this API call.");
    private static final Object zag = new Object();
    @GuardedBy("lock")
    private static GoogleApiManager zaj;
    private com.google.android.gms.common.internal.zaaa zah;
    private com.google.android.gms.common.internal.zaac zai;
    private final Context zak;
    private final GoogleApiAvailability zal;
    private final com.google.android.gms.common.internal.zaj zam;
    @GuardedBy("lock")
    private zay zaq;
    @NotOnlyInitialized
    private final Handler zat;
    private volatile boolean zau;
    private long zac = 5000;
    private long zad = 120000;
    private long zae = 10000;
    private boolean zaf = false;
    private final AtomicInteger zan = new AtomicInteger(1);
    private final AtomicInteger zao = new AtomicInteger(0);
    private final Map<ApiKey<?>, zaa<?>> zap = new ConcurrentHashMap(5, 0.75f, 1);
    @GuardedBy("lock")
    private final Set<ApiKey<?>> zar = new ArraySet();
    private final Set<ApiKey<?>> zas = new ArraySet();

    @RecentlyNonNull
    public static GoogleApiManager zaa(@RecentlyNonNull Context context) {
        GoogleApiManager googleApiManager;
        synchronized (zag) {
            if (zaj == null) {
                HandlerThread handlerThread = new HandlerThread("GoogleApiHandler", 9);
                handlerThread.start();
                zaj = new GoogleApiManager(context.getApplicationContext(), handlerThread.getLooper(), GoogleApiAvailability.getInstance());
            }
            googleApiManager = zaj;
        }
        return googleApiManager;
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public static class zab {
        private final ApiKey<?> zaa;
        private final Feature zab;

        private zab(ApiKey<?> apiKey, Feature feature) {
            this.zaa = apiKey;
            this.zab = feature;
        }

        public final boolean equals(Object obj) {
            if (obj != null && (obj instanceof zab)) {
                zab zabVar = (zab) obj;
                if (Objects.equal(this.zaa, zabVar.zaa) && Objects.equal(this.zab, zabVar.zab)) {
                    return true;
                }
            }
            return false;
        }

        public final int hashCode() {
            return Objects.hashCode(this.zaa, this.zab);
        }

        public final String toString() {
            return Objects.toStringHelper(this).add("key", this.zaa).add("feature", this.zab).toString();
        }

        /* synthetic */ zab(ApiKey apiKey, Feature feature, zabd zabdVar) {
            this(apiKey, feature);
        }
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public class zac implements zach, BaseGmsClient.ConnectionProgressReportCallbacks {
        private final Api.Client zab;
        private final ApiKey<?> zac;
        private IAccountAccessor zad = null;
        private Set<Scope> zae = null;
        private boolean zaf = false;

        public zac(Api.Client client, ApiKey<?> apiKey) {
            GoogleApiManager.this = r1;
            this.zab = client;
            this.zac = apiKey;
        }

        @Override // com.google.android.gms.common.internal.BaseGmsClient.ConnectionProgressReportCallbacks
        public final void onReportServiceBinding(ConnectionResult connectionResult) {
            GoogleApiManager.this.zat.post(new zabj(this, connectionResult));
        }

        @Override // com.google.android.gms.common.api.internal.zach
        public final void zaa(ConnectionResult connectionResult) {
            zaa zaaVar = (zaa) GoogleApiManager.this.zap.get(this.zac);
            if (zaaVar != null) {
                zaaVar.zaa(connectionResult);
            }
        }

        @Override // com.google.android.gms.common.api.internal.zach
        public final void zaa(IAccountAccessor iAccountAccessor, Set<Scope> set) {
            if (iAccountAccessor == null || set == null) {
                Log.wtf("GoogleApiManager", "Received null response from onSignInSuccess", new Exception());
                zaa(new ConnectionResult(4));
                return;
            }
            this.zad = iAccountAccessor;
            this.zae = set;
            zaa();
        }

        public final void zaa() {
            IAccountAccessor iAccountAccessor;
            if (!this.zaf || (iAccountAccessor = this.zad) == null) {
                return;
            }
            this.zab.getRemoteService(iAccountAccessor, this.zae);
        }
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public class zaa<O extends Api.ApiOptions> implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, zap {
        @NotOnlyInitialized
        private final Api.Client zac;
        private final ApiKey<O> zad;
        private final int zah;
        private final zace zai;
        private boolean zaj;
        private final Queue<com.google.android.gms.common.api.internal.zab> zab = new LinkedList();
        private final Set<zaj> zaf = new HashSet();
        private final Map<ListenerHolder.ListenerKey<?>, zabv> zag = new HashMap();
        private final List<zab> zak = new ArrayList();
        private ConnectionResult zal = null;
        private int zam = 0;
        private final zav zae = new zav();

        public zaa(GoogleApi<O> googleApi) {
            GoogleApiManager.this = r4;
            Api.Client zaa = googleApi.zaa(r4.zat.getLooper(), this);
            this.zac = zaa;
            this.zad = googleApi.getApiKey();
            this.zah = googleApi.zaa();
            if (!zaa.requiresSignIn()) {
                this.zai = null;
            } else {
                this.zai = googleApi.zaa(r4.zak, r4.zat);
            }
        }

        @Override // com.google.android.gms.common.api.internal.ConnectionCallbacks
        public final void onConnected(Bundle bundle) {
            if (Looper.myLooper() == GoogleApiManager.this.zat.getLooper()) {
                zao();
            } else {
                GoogleApiManager.this.zat.post(new zabf(this));
            }
        }

        public final void zao() {
            zad();
            zac(ConnectionResult.RESULT_SUCCESS);
            zaq();
            Iterator<zabv> it = this.zag.values().iterator();
            if (it.hasNext()) {
                RegisterListenerMethod<Api.AnyClient, ?> registerListenerMethod = it.next().zaa;
                throw null;
            }
            zap();
            zar();
        }

        @Override // com.google.android.gms.common.api.internal.ConnectionCallbacks
        public final void onConnectionSuspended(int i) {
            if (Looper.myLooper() == GoogleApiManager.this.zat.getLooper()) {
                zaa(i);
            } else {
                GoogleApiManager.this.zat.post(new zabe(this, i));
            }
        }

        public final void zaa(int i) {
            zad();
            this.zaj = true;
            this.zae.zaa(i, this.zac.getLastDisconnectMessage());
            GoogleApiManager.this.zat.sendMessageDelayed(Message.obtain(GoogleApiManager.this.zat, 9, this.zad), GoogleApiManager.this.zac);
            GoogleApiManager.this.zat.sendMessageDelayed(Message.obtain(GoogleApiManager.this.zat, 11, this.zad), GoogleApiManager.this.zad);
            GoogleApiManager.this.zam.zaa();
            for (zabv zabvVar : this.zag.values()) {
                zabvVar.zac.run();
            }
        }

        public final void zaa(ConnectionResult connectionResult) {
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            Api.Client client = this.zac;
            String name = client.getClass().getName();
            String valueOf = String.valueOf(connectionResult);
            StringBuilder sb = new StringBuilder(name.length() + 25 + valueOf.length());
            sb.append("onSignInFailed for ");
            sb.append(name);
            sb.append(" with ");
            sb.append(valueOf);
            client.disconnect(sb.toString());
            onConnectionFailed(connectionResult);
        }

        private final boolean zab(ConnectionResult connectionResult) {
            synchronized (GoogleApiManager.zag) {
                zay unused = GoogleApiManager.this.zaq;
            }
            return false;
        }

        @Override // com.google.android.gms.common.api.internal.zap
        public final void zaa(ConnectionResult connectionResult, Api<?> api, boolean z) {
            if (Looper.myLooper() == GoogleApiManager.this.zat.getLooper()) {
                onConnectionFailed(connectionResult);
            } else {
                GoogleApiManager.this.zat.post(new zabh(this, connectionResult));
            }
        }

        @Override // com.google.android.gms.common.api.internal.OnConnectionFailedListener
        public final void onConnectionFailed(ConnectionResult connectionResult) {
            zaa(connectionResult, (Exception) null);
        }

        private final void zaa(ConnectionResult connectionResult, Exception exc) {
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            zace zaceVar = this.zai;
            if (zaceVar != null) {
                zaceVar.zaa();
            }
            zad();
            GoogleApiManager.this.zam.zaa();
            zac(connectionResult);
            if (this.zac instanceof com.google.android.gms.common.internal.service.zar) {
                GoogleApiManager.this.zaf = true;
                GoogleApiManager.this.zat.sendMessageDelayed(GoogleApiManager.this.zat.obtainMessage(19), 300000L);
            }
            if (connectionResult.getErrorCode() != 4) {
                if (this.zab.isEmpty()) {
                    this.zal = connectionResult;
                    return;
                } else if (exc != null) {
                    Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
                    zaa((Status) null, exc, false);
                    return;
                } else if (!GoogleApiManager.this.zau) {
                    zaa(zad(connectionResult));
                    return;
                } else {
                    zaa(zad(connectionResult), (Exception) null, true);
                    if (this.zab.isEmpty() || zab(connectionResult) || GoogleApiManager.this.zaa(connectionResult, this.zah)) {
                        return;
                    }
                    if (connectionResult.getErrorCode() == 18) {
                        this.zaj = true;
                    }
                    if (this.zaj) {
                        GoogleApiManager.this.zat.sendMessageDelayed(Message.obtain(GoogleApiManager.this.zat, 9, this.zad), GoogleApiManager.this.zac);
                        return;
                    } else {
                        zaa(zad(connectionResult));
                        return;
                    }
                }
            }
            zaa(GoogleApiManager.zab);
        }

        private final void zap() {
            ArrayList arrayList = new ArrayList(this.zab);
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                com.google.android.gms.common.api.internal.zab zabVar = (com.google.android.gms.common.api.internal.zab) obj;
                if (!this.zac.isConnected()) {
                    return;
                }
                if (zab(zabVar)) {
                    this.zab.remove(zabVar);
                }
            }
        }

        public final void zaa(com.google.android.gms.common.api.internal.zab zabVar) {
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            if (this.zac.isConnected()) {
                if (zab(zabVar)) {
                    zar();
                    return;
                } else {
                    this.zab.add(zabVar);
                    return;
                }
            }
            this.zab.add(zabVar);
            ConnectionResult connectionResult = this.zal;
            if (connectionResult != null && connectionResult.hasResolution()) {
                onConnectionFailed(this.zal);
            } else {
                zai();
            }
        }

        public final void zaa() {
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            zaa(GoogleApiManager.zaa);
            this.zae.zab();
            for (ListenerHolder.ListenerKey listenerKey : (ListenerHolder.ListenerKey[]) this.zag.keySet().toArray(new ListenerHolder.ListenerKey[0])) {
                zaa(new zag(listenerKey, new TaskCompletionSource()));
            }
            zac(new ConnectionResult(4));
            if (this.zac.isConnected()) {
                this.zac.onUserSignOut(new zabg(this));
            }
        }

        public final Api.Client zab() {
            return this.zac;
        }

        public final Map<ListenerHolder.ListenerKey<?>, zabv> zac() {
            return this.zag;
        }

        public final void zad() {
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            this.zal = null;
        }

        public final ConnectionResult zae() {
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            return this.zal;
        }

        private final boolean zab(com.google.android.gms.common.api.internal.zab zabVar) {
            if (!(zabVar instanceof zad)) {
                zac(zabVar);
                return true;
            }
            zad zadVar = (zad) zabVar;
            Feature zaa = zaa(zadVar.zac(this));
            if (zaa == null) {
                zac(zabVar);
                return true;
            }
            String name = this.zac.getClass().getName();
            String name2 = zaa.getName();
            long version = zaa.getVersion();
            StringBuilder sb = new StringBuilder(name.length() + 77 + String.valueOf(name2).length());
            sb.append(name);
            sb.append(" could not execute call because it requires feature (");
            sb.append(name2);
            sb.append(", ");
            sb.append(version);
            sb.append(").");
            Log.w("GoogleApiManager", sb.toString());
            if (GoogleApiManager.this.zau && zadVar.zad(this)) {
                zab zabVar2 = new zab(this.zad, zaa, null);
                int indexOf = this.zak.indexOf(zabVar2);
                if (indexOf >= 0) {
                    zab zabVar3 = this.zak.get(indexOf);
                    GoogleApiManager.this.zat.removeMessages(15, zabVar3);
                    GoogleApiManager.this.zat.sendMessageDelayed(Message.obtain(GoogleApiManager.this.zat, 15, zabVar3), GoogleApiManager.this.zac);
                    return false;
                }
                this.zak.add(zabVar2);
                GoogleApiManager.this.zat.sendMessageDelayed(Message.obtain(GoogleApiManager.this.zat, 15, zabVar2), GoogleApiManager.this.zac);
                GoogleApiManager.this.zat.sendMessageDelayed(Message.obtain(GoogleApiManager.this.zat, 16, zabVar2), GoogleApiManager.this.zad);
                ConnectionResult connectionResult = new ConnectionResult(2, null);
                if (zab(connectionResult)) {
                    return false;
                }
                GoogleApiManager.this.zaa(connectionResult, this.zah);
                return false;
            }
            zadVar.zaa(new UnsupportedApiCallException(zaa));
            return true;
        }

        private final void zac(com.google.android.gms.common.api.internal.zab zabVar) {
            zabVar.zaa(this.zae, zak());
            try {
                zabVar.zaa((zaa<?>) this);
            } catch (DeadObjectException unused) {
                onConnectionSuspended(1);
                this.zac.disconnect("DeadObjectException thrown while running ApiCallRunner.");
            } catch (Throwable th) {
                throw new IllegalStateException(String.format("Error in GoogleApi implementation for client %s.", this.zac.getClass().getName()), th);
            }
        }

        private final void zaa(Status status, Exception exc, boolean z) {
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            boolean z2 = true;
            boolean z3 = status == null;
            if (exc != null) {
                z2 = false;
            }
            if (z3 == z2) {
                throw new IllegalArgumentException("Status XOR exception should be null");
            }
            Iterator<com.google.android.gms.common.api.internal.zab> it = this.zab.iterator();
            while (it.hasNext()) {
                com.google.android.gms.common.api.internal.zab next = it.next();
                if (!z || next.zaa == 2) {
                    if (status != null) {
                        next.zaa(status);
                    } else {
                        next.zaa(exc);
                    }
                    it.remove();
                }
            }
        }

        public final void zaa(Status status) {
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            zaa(status, (Exception) null, false);
        }

        public final void zaf() {
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            if (this.zaj) {
                zai();
            }
        }

        private final void zaq() {
            if (this.zaj) {
                GoogleApiManager.this.zat.removeMessages(11, this.zad);
                GoogleApiManager.this.zat.removeMessages(9, this.zad);
                this.zaj = false;
            }
        }

        public final void zag() {
            Status status;
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            if (this.zaj) {
                zaq();
                if (GoogleApiManager.this.zal.isGooglePlayServicesAvailable(GoogleApiManager.this.zak) == 18) {
                    status = new Status(21, "Connection timed out waiting for Google Play services update to complete.");
                } else {
                    status = new Status(22, "API failed to connect while resuming due to an unknown error.");
                }
                zaa(status);
                this.zac.disconnect("Timing out connection while resuming.");
            }
        }

        private final void zar() {
            GoogleApiManager.this.zat.removeMessages(12, this.zad);
            GoogleApiManager.this.zat.sendMessageDelayed(GoogleApiManager.this.zat.obtainMessage(12, this.zad), GoogleApiManager.this.zae);
        }

        public final boolean zah() {
            return zaa(true);
        }

        public final boolean zaa(boolean z) {
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            if (!this.zac.isConnected() || this.zag.size() != 0) {
                return false;
            }
            if (!this.zae.zaa()) {
                this.zac.disconnect("Timing out service connection.");
                return true;
            }
            if (z) {
                zar();
            }
            return false;
        }

        public final void zai() {
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            if (this.zac.isConnected() || this.zac.isConnecting()) {
                return;
            }
            try {
                int zaa = GoogleApiManager.this.zam.zaa(GoogleApiManager.this.zak, this.zac);
                if (zaa != 0) {
                    ConnectionResult connectionResult = new ConnectionResult(zaa, null);
                    String name = this.zac.getClass().getName();
                    String valueOf = String.valueOf(connectionResult);
                    StringBuilder sb = new StringBuilder(name.length() + 35 + valueOf.length());
                    sb.append("The service for ");
                    sb.append(name);
                    sb.append(" is not available: ");
                    sb.append(valueOf);
                    Log.w("GoogleApiManager", sb.toString());
                    onConnectionFailed(connectionResult);
                    return;
                }
                zac zacVar = new zac(this.zac, this.zad);
                if (this.zac.requiresSignIn()) {
                    ((zace) Preconditions.checkNotNull(this.zai)).zaa(zacVar);
                }
                try {
                    this.zac.connect(zacVar);
                } catch (SecurityException e) {
                    zaa(new ConnectionResult(10), e);
                }
            } catch (IllegalStateException e2) {
                zaa(new ConnectionResult(10), e2);
            }
        }

        public final void zaa(zaj zajVar) {
            Preconditions.checkHandlerThread(GoogleApiManager.this.zat);
            this.zaf.add(zajVar);
        }

        private final void zac(ConnectionResult connectionResult) {
            for (zaj zajVar : this.zaf) {
                String str = null;
                if (Objects.equal(connectionResult, ConnectionResult.RESULT_SUCCESS)) {
                    str = this.zac.getEndpointPackageName();
                }
                zajVar.zaa(this.zad, connectionResult, str);
            }
            this.zaf.clear();
        }

        final boolean zaj() {
            return this.zac.isConnected();
        }

        public final boolean zak() {
            return this.zac.requiresSignIn();
        }

        public final int zal() {
            return this.zah;
        }

        private final Feature zaa(Feature[] featureArr) {
            if (featureArr != null && featureArr.length != 0) {
                Feature[] availableFeatures = this.zac.getAvailableFeatures();
                if (availableFeatures == null) {
                    availableFeatures = new Feature[0];
                }
                ArrayMap arrayMap = new ArrayMap(availableFeatures.length);
                for (Feature feature : availableFeatures) {
                    arrayMap.put(feature.getName(), Long.valueOf(feature.getVersion()));
                }
                for (Feature feature2 : featureArr) {
                    Long l = (Long) arrayMap.get(feature2.getName());
                    if (l == null || l.longValue() < feature2.getVersion()) {
                        return feature2;
                    }
                }
            }
            return null;
        }

        public final void zaa(zab zabVar) {
            if (this.zak.contains(zabVar) && !this.zaj) {
                if (!this.zac.isConnected()) {
                    zai();
                } else {
                    zap();
                }
            }
        }

        public final void zab(zab zabVar) {
            Feature[] zac;
            if (this.zak.remove(zabVar)) {
                GoogleApiManager.this.zat.removeMessages(15, zabVar);
                GoogleApiManager.this.zat.removeMessages(16, zabVar);
                Feature feature = zabVar.zab;
                ArrayList arrayList = new ArrayList(this.zab.size());
                for (com.google.android.gms.common.api.internal.zab zabVar2 : this.zab) {
                    if ((zabVar2 instanceof zad) && (zac = ((zad) zabVar2).zac(this)) != null && ArrayUtils.contains(zac, feature)) {
                        arrayList.add(zabVar2);
                    }
                }
                int size = arrayList.size();
                int i = 0;
                while (i < size) {
                    Object obj = arrayList.get(i);
                    i++;
                    com.google.android.gms.common.api.internal.zab zabVar3 = (com.google.android.gms.common.api.internal.zab) obj;
                    this.zab.remove(zabVar3);
                    zabVar3.zaa(new UnsupportedApiCallException(feature));
                }
            }
        }

        public final int zam() {
            return this.zam;
        }

        public final void zan() {
            this.zam++;
        }

        private final Status zad(ConnectionResult connectionResult) {
            return GoogleApiManager.zab((ApiKey<?>) this.zad, connectionResult);
        }
    }

    public static void reportSignOut() {
        synchronized (zag) {
            GoogleApiManager googleApiManager = zaj;
            if (googleApiManager != null) {
                googleApiManager.zao.incrementAndGet();
                Handler handler = googleApiManager.zat;
                handler.sendMessageAtFrontOfQueue(handler.obtainMessage(10));
            }
        }
    }

    private GoogleApiManager(Context context, Looper looper, GoogleApiAvailability googleApiAvailability) {
        this.zau = true;
        this.zak = context;
        com.google.android.gms.internal.base.zas zasVar = new com.google.android.gms.internal.base.zas(looper, this);
        this.zat = zasVar;
        this.zal = googleApiAvailability;
        this.zam = new com.google.android.gms.common.internal.zaj(googleApiAvailability);
        if (DeviceProperties.isAuto(context)) {
            this.zau = false;
        }
        zasVar.sendMessage(zasVar.obtainMessage(6));
    }

    public final int zab() {
        return this.zan.getAndIncrement();
    }

    public final void zaa(@RecentlyNonNull GoogleApi<?> googleApi) {
        Handler handler = this.zat;
        handler.sendMessage(handler.obtainMessage(7, googleApi));
    }

    private final zaa<?> zac(GoogleApi<?> googleApi) {
        ApiKey<?> apiKey = googleApi.getApiKey();
        zaa<?> zaaVar = this.zap.get(apiKey);
        if (zaaVar == null) {
            zaaVar = new zaa<>(googleApi);
            this.zap.put(apiKey, zaaVar);
        }
        if (zaaVar.zak()) {
            this.zas.add(apiKey);
        }
        zaaVar.zai();
        return zaaVar;
    }

    public final zaa zaa(ApiKey<?> apiKey) {
        return this.zap.get(apiKey);
    }

    public final void zac() {
        Handler handler = this.zat;
        handler.sendMessage(handler.obtainMessage(3));
    }

    public final <O extends Api.ApiOptions> void zaa(@RecentlyNonNull GoogleApi<O> googleApi, int i, @RecentlyNonNull BaseImplementation$ApiMethodImpl<? extends Result, Api.AnyClient> baseImplementation$ApiMethodImpl) {
        zaf zafVar = new zaf(i, baseImplementation$ApiMethodImpl);
        Handler handler = this.zat;
        handler.sendMessage(handler.obtainMessage(4, new zabu(zafVar, this.zao.get(), googleApi)));
    }

    public final <O extends Api.ApiOptions, ResultT> void zaa(@RecentlyNonNull GoogleApi<O> googleApi, int i, @RecentlyNonNull TaskApiCall<Api.AnyClient, ResultT> taskApiCall, @RecentlyNonNull TaskCompletionSource<ResultT> taskCompletionSource, @RecentlyNonNull StatusExceptionMapper statusExceptionMapper) {
        zaa((TaskCompletionSource) taskCompletionSource, taskApiCall.zab(), (GoogleApi<?>) googleApi);
        zah zahVar = new zah(i, taskApiCall, taskCompletionSource, statusExceptionMapper);
        Handler handler = this.zat;
        handler.sendMessage(handler.obtainMessage(4, new zabu(zahVar, this.zao.get(), googleApi)));
    }

    public final boolean zad() {
        if (this.zaf) {
            return false;
        }
        RootTelemetryConfiguration config = RootTelemetryConfigManager.getInstance().getConfig();
        if (config != null && !config.getMethodInvocationTelemetryEnabled()) {
            return false;
        }
        int zaa2 = this.zam.zaa(this.zak, 203390000);
        return zaa2 == -1 || zaa2 == 0;
    }

    private final <T> void zaa(TaskCompletionSource<T> taskCompletionSource, int i, GoogleApi<?> googleApi) {
        zabr zaa2;
        if (i == 0 || (zaa2 = zabr.zaa(this, i, googleApi.getApiKey())) == null) {
            return;
        }
        Task<T> task = taskCompletionSource.getTask();
        Handler handler = this.zat;
        handler.getClass();
        task.addOnCompleteListener(zabc.zaa(handler), zaa2);
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(@RecentlyNonNull Message message) {
        int i = message.what;
        long j = 300000;
        zaa<?> zaaVar = null;
        switch (i) {
            case 1:
                if (((Boolean) message.obj).booleanValue()) {
                    j = 10000;
                }
                this.zae = j;
                this.zat.removeMessages(12);
                for (ApiKey<?> apiKey : this.zap.keySet()) {
                    Handler handler = this.zat;
                    handler.sendMessageDelayed(handler.obtainMessage(12, apiKey), this.zae);
                }
                break;
            case 2:
                zaj zajVar = (zaj) message.obj;
                Iterator<ApiKey<?>> it = zajVar.zaa().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    } else {
                        ApiKey<?> next = it.next();
                        zaa<?> zaaVar2 = this.zap.get(next);
                        if (zaaVar2 == null) {
                            zajVar.zaa(next, new ConnectionResult(13), null);
                            break;
                        } else if (zaaVar2.zaj()) {
                            zajVar.zaa(next, ConnectionResult.RESULT_SUCCESS, zaaVar2.zab().getEndpointPackageName());
                        } else {
                            ConnectionResult zae = zaaVar2.zae();
                            if (zae != null) {
                                zajVar.zaa(next, zae, null);
                            } else {
                                zaaVar2.zaa(zajVar);
                                zaaVar2.zai();
                            }
                        }
                    }
                }
            case 3:
                for (zaa<?> zaaVar3 : this.zap.values()) {
                    zaaVar3.zad();
                    zaaVar3.zai();
                }
                break;
            case 4:
            case 8:
            case 13:
                zabu zabuVar = (zabu) message.obj;
                zaa<?> zaaVar4 = this.zap.get(zabuVar.zac.getApiKey());
                if (zaaVar4 == null) {
                    zaaVar4 = zac(zabuVar.zac);
                }
                if (zaaVar4.zak() && this.zao.get() != zabuVar.zab) {
                    zabuVar.zaa.zaa(zaa);
                    zaaVar4.zaa();
                    break;
                } else {
                    zaaVar4.zaa(zabuVar.zaa);
                    break;
                }
                break;
            case 5:
                int i2 = message.arg1;
                ConnectionResult connectionResult = (ConnectionResult) message.obj;
                Iterator<zaa<?>> it2 = this.zap.values().iterator();
                while (true) {
                    if (it2.hasNext()) {
                        zaa<?> next2 = it2.next();
                        if (next2.zal() == i2) {
                            zaaVar = next2;
                        }
                    }
                }
                if (zaaVar != null) {
                    if (connectionResult.getErrorCode() == 13) {
                        String errorString = this.zal.getErrorString(connectionResult.getErrorCode());
                        String errorMessage = connectionResult.getErrorMessage();
                        StringBuilder sb = new StringBuilder(String.valueOf(errorString).length() + 69 + String.valueOf(errorMessage).length());
                        sb.append("Error resolution was canceled by the user, original error message: ");
                        sb.append(errorString);
                        sb.append(": ");
                        sb.append(errorMessage);
                        zaaVar.zaa(new Status(17, sb.toString()));
                        break;
                    } else {
                        zaaVar.zaa(zab(((zaa) zaaVar).zad, connectionResult));
                        break;
                    }
                } else {
                    StringBuilder sb2 = new StringBuilder(76);
                    sb2.append("Could not find API instance ");
                    sb2.append(i2);
                    sb2.append(" while trying to fail enqueued calls.");
                    Log.wtf("GoogleApiManager", sb2.toString(), new Exception());
                    break;
                }
            case 6:
                if (this.zak.getApplicationContext() instanceof Application) {
                    BackgroundDetector.initialize((Application) this.zak.getApplicationContext());
                    BackgroundDetector.getInstance().addListener(new zabd(this));
                    if (!BackgroundDetector.getInstance().readCurrentStateIfPossible(true)) {
                        this.zae = 300000L;
                        break;
                    }
                }
                break;
            case 7:
                zac((GoogleApi) message.obj);
                break;
            case 9:
                if (this.zap.containsKey(message.obj)) {
                    this.zap.get(message.obj).zaf();
                    break;
                }
                break;
            case 10:
                for (ApiKey<?> apiKey2 : this.zas) {
                    zaa<?> remove = this.zap.remove(apiKey2);
                    if (remove != null) {
                        remove.zaa();
                    }
                }
                this.zas.clear();
                break;
            case 11:
                if (this.zap.containsKey(message.obj)) {
                    this.zap.get(message.obj).zag();
                    break;
                }
                break;
            case 12:
                if (this.zap.containsKey(message.obj)) {
                    this.zap.get(message.obj).zah();
                    break;
                }
                break;
            case 14:
                zaz zazVar = (zaz) message.obj;
                ApiKey<?> zaa2 = zazVar.zaa();
                if (!this.zap.containsKey(zaa2)) {
                    zazVar.zab().setResult(Boolean.FALSE);
                    break;
                } else {
                    zazVar.zab().setResult(Boolean.valueOf(this.zap.get(zaa2).zaa(false)));
                    break;
                }
            case 15:
                zab zabVar = (zab) message.obj;
                if (this.zap.containsKey(zabVar.zaa)) {
                    this.zap.get(zabVar.zaa).zaa(zabVar);
                    break;
                }
                break;
            case 16:
                zab zabVar2 = (zab) message.obj;
                if (this.zap.containsKey(zabVar2.zaa)) {
                    this.zap.get(zabVar2.zaa).zab(zabVar2);
                    break;
                }
                break;
            case 17:
                zag();
                break;
            case 18:
                zabq zabqVar = (zabq) message.obj;
                if (zabqVar.zac == 0) {
                    zah().zaa(new com.google.android.gms.common.internal.zaaa(zabqVar.zab, Arrays.asList(zabqVar.zaa)));
                    break;
                } else {
                    com.google.android.gms.common.internal.zaaa zaaaVar = this.zah;
                    if (zaaaVar != null) {
                        List<com.google.android.gms.common.internal.zao> zab2 = zaaaVar.zab();
                        if (this.zah.zaa() != zabqVar.zab || (zab2 != null && zab2.size() >= zabqVar.zad)) {
                            this.zat.removeMessages(17);
                            zag();
                        } else {
                            this.zah.zaa(zabqVar.zaa);
                        }
                    }
                    if (this.zah == null) {
                        ArrayList arrayList = new ArrayList();
                        arrayList.add(zabqVar.zaa);
                        this.zah = new com.google.android.gms.common.internal.zaaa(zabqVar.zab, arrayList);
                        Handler handler2 = this.zat;
                        handler2.sendMessageDelayed(handler2.obtainMessage(17), zabqVar.zac);
                        break;
                    }
                }
                break;
            case R.styleable.MapAttrs_uiTiltGestures /* 19 */:
                this.zaf = false;
                break;
            default:
                StringBuilder sb3 = new StringBuilder(31);
                sb3.append("Unknown message id: ");
                sb3.append(i);
                Log.w("GoogleApiManager", sb3.toString());
                return false;
        }
        return true;
    }

    final boolean zaa(ConnectionResult connectionResult, int i) {
        return this.zal.zaa(this.zak, connectionResult, i);
    }

    public final void zab(@RecentlyNonNull ConnectionResult connectionResult, int i) {
        if (!zaa(connectionResult, i)) {
            Handler handler = this.zat;
            handler.sendMessage(handler.obtainMessage(5, i, 0, connectionResult));
        }
    }

    public static Status zab(ApiKey<?> apiKey, ConnectionResult connectionResult) {
        String zaa2 = apiKey.zaa();
        String valueOf = String.valueOf(connectionResult);
        StringBuilder sb = new StringBuilder(String.valueOf(zaa2).length() + 63 + valueOf.length());
        sb.append("API: ");
        sb.append(zaa2);
        sb.append(" is not available on this device. Connection failed with: ");
        sb.append(valueOf);
        return new Status(connectionResult, sb.toString());
    }

    public final void zaa(com.google.android.gms.common.internal.zao zaoVar, int i, long j, int i2) {
        Handler handler = this.zat;
        handler.sendMessage(handler.obtainMessage(18, new zabq(zaoVar, i, j, i2)));
    }

    private final void zag() {
        com.google.android.gms.common.internal.zaaa zaaaVar = this.zah;
        if (zaaaVar != null) {
            if (zaaaVar.zaa() > 0 || zad()) {
                zah().zaa(zaaaVar);
            }
            this.zah = null;
        }
    }

    private final com.google.android.gms.common.internal.zaac zah() {
        if (this.zai == null) {
            this.zai = new com.google.android.gms.common.internal.service.zaq(this.zak);
        }
        return this.zai;
    }
}
