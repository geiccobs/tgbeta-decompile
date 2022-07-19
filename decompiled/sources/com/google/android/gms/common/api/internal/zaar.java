package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.ClientLibraryUtils;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.signin.zae;
import com.huawei.hms.framework.network.grs.GrsBaseInfo;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import javax.annotation.concurrent.GuardedBy;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zaar extends GoogleApiClient implements zabn {
    final Map<Api.AnyClientKey<?>, Api.Client> zab;
    Set<Scope> zac;
    Set<zacn> zad;
    final zaco zae;
    private final Lock zaf;
    private final com.google.android.gms.common.internal.zah zag;
    private final int zai;
    private final Context zaj;
    private final Looper zak;
    private volatile boolean zal;
    private long zam;
    private long zan;
    private final zaau zao;
    private final GoogleApiAvailability zap;
    private zabk zaq;
    private final ClientSettings zar;
    private final Map<Api<?>, Boolean> zas;
    private final Api.AbstractClientBuilder<? extends zae, SignInOptions> zat;
    private final ListenerHolders zau;
    private final ArrayList<zaq> zav;
    private Integer zaw;
    private final com.google.android.gms.common.internal.zak zax;
    private zabo zah = null;
    final Queue<BaseImplementation$ApiMethodImpl<?, ?>> zaa = new LinkedList();

    public zaar(Context context, Lock lock, Looper looper, ClientSettings clientSettings, GoogleApiAvailability googleApiAvailability, Api.AbstractClientBuilder<? extends zae, SignInOptions> abstractClientBuilder, Map<Api<?>, Boolean> map, List<GoogleApiClient.ConnectionCallbacks> list, List<GoogleApiClient.OnConnectionFailedListener> list2, Map<Api.AnyClientKey<?>, Api.Client> map2, int i, int i2, ArrayList<zaq> arrayList) {
        this.zam = ClientLibraryUtils.isPackageSide() ? 10000L : 120000L;
        this.zan = 5000L;
        this.zac = new HashSet();
        this.zau = new ListenerHolders();
        this.zaw = null;
        this.zad = null;
        zaaq zaaqVar = new zaaq(this);
        this.zax = zaaqVar;
        this.zaj = context;
        this.zaf = lock;
        this.zag = new com.google.android.gms.common.internal.zah(looper, zaaqVar);
        this.zak = looper;
        this.zao = new zaau(this, looper);
        this.zap = googleApiAvailability;
        this.zai = i;
        if (i >= 0) {
            this.zaw = Integer.valueOf(i2);
        }
        this.zas = map;
        this.zab = map2;
        this.zav = arrayList;
        this.zae = new zaco();
        for (GoogleApiClient.ConnectionCallbacks connectionCallbacks : list) {
            this.zag.zaa(connectionCallbacks);
        }
        for (GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener : list2) {
            this.zag.zaa(onConnectionFailedListener);
        }
        this.zar = clientSettings;
        this.zat = abstractClientBuilder;
    }

    private static String zab(int i) {
        return i != 1 ? i != 2 ? i != 3 ? GrsBaseInfo.CountryCodeSource.UNKNOWN : "SIGN_IN_MODE_NONE" : "SIGN_IN_MODE_OPTIONAL" : "SIGN_IN_MODE_REQUIRED";
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final <A extends Api.AnyClient, R extends Result, T extends BaseImplementation$ApiMethodImpl<R, A>> T enqueue(T t) {
        Api<?> api = t.getApi();
        boolean containsKey = this.zab.containsKey(t.getClientKey());
        String zad = api != null ? api.zad() : "the API";
        StringBuilder sb = new StringBuilder(String.valueOf(zad).length() + 65);
        sb.append("GoogleApiClient is not configured to use ");
        sb.append(zad);
        sb.append(" required for this call.");
        Preconditions.checkArgument(containsKey, sb.toString());
        this.zaf.lock();
        try {
            zabo zaboVar = this.zah;
            if (zaboVar == null) {
                this.zaa.add(t);
                return t;
            }
            return (T) zaboVar.zaa((zabo) t);
        } finally {
            this.zaf.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final <A extends Api.AnyClient, T extends BaseImplementation$ApiMethodImpl<? extends Result, A>> T execute(T t) {
        Api<?> api = t.getApi();
        boolean containsKey = this.zab.containsKey(t.getClientKey());
        String zad = api != null ? api.zad() : "the API";
        StringBuilder sb = new StringBuilder(String.valueOf(zad).length() + 65);
        sb.append("GoogleApiClient is not configured to use ");
        sb.append(zad);
        sb.append(" required for this call.");
        Preconditions.checkArgument(containsKey, sb.toString());
        this.zaf.lock();
        try {
            zabo zaboVar = this.zah;
            if (zaboVar == null) {
                throw new IllegalStateException("GoogleApiClient is not connected yet.");
            }
            if (this.zal) {
                this.zaa.add(t);
                while (!this.zaa.isEmpty()) {
                    BaseImplementation$ApiMethodImpl<?, ?> remove = this.zaa.remove();
                    this.zae.zaa(remove);
                    remove.setFailedResult(Status.RESULT_INTERNAL_ERROR);
                }
                return t;
            }
            return (T) zaboVar.zab(t);
        } finally {
            this.zaf.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final <C extends Api.Client> C getClient(Api.AnyClientKey<C> anyClientKey) {
        C c = (C) this.zab.get(anyClientKey);
        Preconditions.checkNotNull(c, "Appropriate Api was not requested.");
        return c;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void connect() {
        this.zaf.lock();
        try {
            boolean z = false;
            if (this.zai >= 0) {
                if (this.zaw != null) {
                    z = true;
                }
                Preconditions.checkState(z, "Sign-in mode should have been set explicitly by auto-manage.");
            } else {
                Integer num = this.zaw;
                if (num == null) {
                    this.zaw = Integer.valueOf(zaa((Iterable<Api.Client>) this.zab.values(), false));
                } else if (num.intValue() == 2) {
                    throw new IllegalStateException("Cannot call connect() when SignInMode is set to SIGN_IN_MODE_OPTIONAL. Call connect(SIGN_IN_MODE_OPTIONAL) instead.");
                }
            }
            connect(((Integer) Preconditions.checkNotNull(this.zaw)).intValue());
        } finally {
            this.zaf.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void connect(int i) {
        this.zaf.lock();
        boolean z = true;
        if (i != 3 && i != 1 && i != 2) {
            z = false;
        }
        try {
            StringBuilder sb = new StringBuilder(33);
            sb.append("Illegal sign-in mode: ");
            sb.append(i);
            Preconditions.checkArgument(z, sb.toString());
            zaa(i);
            zad();
        } finally {
            this.zaf.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void disconnect() {
        this.zaf.lock();
        try {
            this.zae.zaa();
            zabo zaboVar = this.zah;
            if (zaboVar != null) {
                zaboVar.zac();
            }
            this.zau.zaa();
            for (BaseImplementation$ApiMethodImpl<?, ?> baseImplementation$ApiMethodImpl : this.zaa) {
                baseImplementation$ApiMethodImpl.zaa((zacq) null);
                baseImplementation$ApiMethodImpl.cancel();
            }
            this.zaa.clear();
            if (this.zah == null) {
                return;
            }
            zab();
            this.zag.zaa();
        } finally {
            this.zaf.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final boolean isConnected() {
        zabo zaboVar = this.zah;
        return zaboVar != null && zaboVar.zad();
    }

    private final void zaa(int i) {
        Integer num = this.zaw;
        if (num == null) {
            this.zaw = Integer.valueOf(i);
        } else if (num.intValue() != i) {
            String zab = zab(i);
            String zab2 = zab(this.zaw.intValue());
            StringBuilder sb = new StringBuilder(String.valueOf(zab).length() + 51 + String.valueOf(zab2).length());
            sb.append("Cannot use sign-in mode: ");
            sb.append(zab);
            sb.append(". Mode was already set to ");
            sb.append(zab2);
            throw new IllegalStateException(sb.toString());
        }
        if (this.zah != null) {
            return;
        }
        boolean z = false;
        boolean z2 = false;
        for (Api.Client client : this.zab.values()) {
            if (client.requiresSignIn()) {
                z = true;
            }
            if (client.providesSignIn()) {
                z2 = true;
            }
        }
        int intValue = this.zaw.intValue();
        if (intValue != 1) {
            if (intValue == 2 && z) {
                this.zah = zas.zaa(this.zaj, this, this.zaf, this.zak, this.zap, this.zab, this.zar, this.zas, this.zat, this.zav);
                return;
            }
        } else if (!z) {
            throw new IllegalStateException("SIGN_IN_MODE_REQUIRED cannot be used on a GoogleApiClient that does not contain any authenticated APIs. Use connect() instead.");
        } else {
            if (z2) {
                throw new IllegalStateException("Cannot use SIGN_IN_MODE_REQUIRED with GOOGLE_SIGN_IN_API. Use connect(SIGN_IN_MODE_OPTIONAL) instead.");
            }
        }
        this.zah = new zaaz(this.zaj, this, this.zaf, this.zak, this.zap, this.zab, this.zar, this.zas, this.zat, this.zav, this);
    }

    @GuardedBy("mLock")
    private final void zad() {
        this.zag.zab();
        ((zabo) Preconditions.checkNotNull(this.zah)).zaa();
    }

    public final void zae() {
        this.zaf.lock();
        try {
            if (this.zal) {
                zad();
            }
        } finally {
            this.zaf.unlock();
        }
    }

    public final void zaf() {
        this.zaf.lock();
        try {
            if (zab()) {
                zad();
            }
        } finally {
            this.zaf.unlock();
        }
    }

    @GuardedBy("mLock")
    public final boolean zab() {
        if (!this.zal) {
            return false;
        }
        this.zal = false;
        this.zao.removeMessages(2);
        this.zao.removeMessages(1);
        zabk zabkVar = this.zaq;
        if (zabkVar != null) {
            zabkVar.zaa();
            this.zaq = null;
        }
        return true;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void registerConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        this.zag.zaa(onConnectionFailedListener);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void unregisterConnectionFailedListener(GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener) {
        this.zag.zac(onConnectionFailedListener);
    }

    @Override // com.google.android.gms.common.api.internal.zabn
    @GuardedBy("mLock")
    public final void zaa(Bundle bundle) {
        while (!this.zaa.isEmpty()) {
            execute(this.zaa.remove());
        }
        this.zag.zaa(bundle);
    }

    @Override // com.google.android.gms.common.api.internal.zabn
    @GuardedBy("mLock")
    public final void zaa(ConnectionResult connectionResult) {
        if (!this.zap.isPlayServicesPossiblyUpdating(this.zaj, connectionResult.getErrorCode())) {
            zab();
        }
        if (!this.zal) {
            this.zag.zaa(connectionResult);
            this.zag.zaa();
        }
    }

    @Override // com.google.android.gms.common.api.internal.zabn
    @GuardedBy("mLock")
    public final void zaa(int i, boolean z) {
        if (i == 1 && !z && !this.zal) {
            this.zal = true;
            if (this.zaq == null && !ClientLibraryUtils.isPackageSide()) {
                try {
                    this.zaq = this.zap.zaa(this.zaj.getApplicationContext(), new zaax(this));
                } catch (SecurityException unused) {
                }
            }
            zaau zaauVar = this.zao;
            zaauVar.sendMessageDelayed(zaauVar.obtainMessage(1), this.zam);
            zaau zaauVar2 = this.zao;
            zaauVar2.sendMessageDelayed(zaauVar2.obtainMessage(2), this.zan);
        }
        for (BasePendingResult basePendingResult : (BasePendingResult[]) this.zae.zab.toArray(new BasePendingResult[0])) {
            basePendingResult.forceFailureUnlessReady(zaco.zaa);
        }
        this.zag.zaa(i);
        this.zag.zaa();
        if (i == 2) {
            zad();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final Context getContext() {
        return this.zaj;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final Looper getLooper() {
        return this.zak;
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final boolean maybeSignIn(SignInConnectionListener signInConnectionListener) {
        zabo zaboVar = this.zah;
        return zaboVar != null && zaboVar.zaa(signInConnectionListener);
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void maybeSignOut() {
        zabo zaboVar = this.zah;
        if (zaboVar != null) {
            zaboVar.zag();
        }
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void zab(zacn zacnVar) {
        zabo zaboVar;
        this.zaf.lock();
        try {
            Set<zacn> set = this.zad;
            if (set == null) {
                Log.wtf("GoogleApiClientImpl", "Attempted to remove pending transform when no transforms are registered.", new Exception());
            } else if (!set.remove(zacnVar)) {
                Log.wtf("GoogleApiClientImpl", "Failed to remove pending transform - this may lead to memory leaks!", new Exception());
            } else if (!zag() && (zaboVar = this.zah) != null) {
                zaboVar.zaf();
            }
        } finally {
            this.zaf.unlock();
        }
    }

    private final boolean zag() {
        this.zaf.lock();
        try {
            Set<zacn> set = this.zad;
            if (set == null) {
                this.zaf.unlock();
                return false;
            }
            return !set.isEmpty();
        } finally {
            this.zaf.unlock();
        }
    }

    public final String zac() {
        StringWriter stringWriter = new StringWriter();
        dump("", null, new PrintWriter(stringWriter), null);
        return stringWriter.toString();
    }

    @Override // com.google.android.gms.common.api.GoogleApiClient
    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.append((CharSequence) str).append("mContext=").println(this.zaj);
        printWriter.append((CharSequence) str).append("mResuming=").print(this.zal);
        printWriter.append(" mWorkQueue.size()=").print(this.zaa.size());
        printWriter.append(" mUnconsumedApiCalls.size()=").println(this.zae.zab.size());
        zabo zaboVar = this.zah;
        if (zaboVar != null) {
            zaboVar.zaa(str, fileDescriptor, printWriter, strArr);
        }
    }

    public static int zaa(Iterable<Api.Client> iterable, boolean z) {
        boolean z2 = false;
        boolean z3 = false;
        for (Api.Client client : iterable) {
            if (client.requiresSignIn()) {
                z2 = true;
            }
            if (client.providesSignIn()) {
                z3 = true;
            }
        }
        if (z2) {
            return (!z3 || !z) ? 1 : 2;
        }
        return 3;
    }
}
