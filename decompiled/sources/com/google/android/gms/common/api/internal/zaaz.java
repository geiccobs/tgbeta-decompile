package com.google.android.gms.common.api.internal;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.internal.ClientSettings;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.signin.SignInOptions;
import com.google.android.gms.signin.zae;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import javax.annotation.concurrent.GuardedBy;
import org.checkerframework.checker.initialization.qual.NotOnlyInitialized;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class zaaz implements zabo, zap {
    final Map<Api.AnyClientKey<?>, Api.Client> zaa;
    final Map<Api.AnyClientKey<?>, ConnectionResult> zab = new HashMap();
    int zac;
    final zaar zad;
    final zabn zae;
    private final Lock zaf;
    private final Condition zag;
    private final Context zah;
    private final GoogleApiAvailabilityLight zai;
    private final zabb zaj;
    private final ClientSettings zak;
    private final Map<Api<?>, Boolean> zal;
    private final Api.AbstractClientBuilder<? extends zae, SignInOptions> zam;
    @NotOnlyInitialized
    private volatile zaaw zan;

    public zaaz(Context context, zaar zaarVar, Lock lock, Looper looper, GoogleApiAvailabilityLight googleApiAvailabilityLight, Map<Api.AnyClientKey<?>, Api.Client> map, ClientSettings clientSettings, Map<Api<?>, Boolean> map2, Api.AbstractClientBuilder<? extends zae, SignInOptions> abstractClientBuilder, ArrayList<zaq> arrayList, zabn zabnVar) {
        this.zah = context;
        this.zaf = lock;
        this.zai = googleApiAvailabilityLight;
        this.zaa = map;
        this.zak = clientSettings;
        this.zal = map2;
        this.zam = abstractClientBuilder;
        this.zad = zaarVar;
        this.zae = zabnVar;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            zaq zaqVar = arrayList.get(i);
            i++;
            zaqVar.zaa(this);
        }
        this.zaj = new zabb(this, looper);
        this.zag = lock.newCondition();
        this.zan = new zaao(this);
    }

    @Override // com.google.android.gms.common.api.internal.zabo
    public final boolean zaa(SignInConnectionListener signInConnectionListener) {
        return false;
    }

    @Override // com.google.android.gms.common.api.internal.zabo
    public final void zag() {
    }

    @Override // com.google.android.gms.common.api.internal.zabo
    @GuardedBy("mLock")
    public final <A extends Api.AnyClient, R extends Result, T extends BaseImplementation$ApiMethodImpl<R, A>> T zaa(T t) {
        t.zab();
        return (T) this.zan.zaa((zaaw) t);
    }

    @Override // com.google.android.gms.common.api.internal.zabo
    @GuardedBy("mLock")
    public final <A extends Api.AnyClient, T extends BaseImplementation$ApiMethodImpl<? extends Result, A>> T zab(T t) {
        t.zab();
        return (T) this.zan.zab(t);
    }

    @Override // com.google.android.gms.common.api.internal.zabo
    @GuardedBy("mLock")
    public final void zaa() {
        this.zan.zac();
    }

    @Override // com.google.android.gms.common.api.internal.zabo
    @GuardedBy("mLock")
    public final void zac() {
        if (this.zan.zab()) {
            this.zab.clear();
        }
    }

    public final void zah() {
        this.zaf.lock();
        try {
            this.zan = new zaaf(this, this.zak, this.zal, this.zai, this.zam, this.zaf, this.zah);
            this.zan.zaa();
            this.zag.signalAll();
        } finally {
            this.zaf.unlock();
        }
    }

    public final void zai() {
        this.zaf.lock();
        try {
            this.zad.zab();
            this.zan = new zaaa(this);
            this.zan.zaa();
            this.zag.signalAll();
        } finally {
            this.zaf.unlock();
        }
    }

    public final void zaa(ConnectionResult connectionResult) {
        this.zaf.lock();
        try {
            this.zan = new zaao(this);
            this.zan.zaa();
            this.zag.signalAll();
        } finally {
            this.zaf.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.internal.zabo
    public final boolean zad() {
        return this.zan instanceof zaaa;
    }

    @Override // com.google.android.gms.common.api.internal.zabo
    @GuardedBy("mLock")
    public final void zaf() {
        if (zad()) {
            ((zaaa) this.zan).zad();
        }
    }

    @Override // com.google.android.gms.common.api.internal.zap
    public final void zaa(ConnectionResult connectionResult, Api<?> api, boolean z) {
        this.zaf.lock();
        try {
            this.zan.zaa(connectionResult, api, z);
        } finally {
            this.zaf.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.internal.ConnectionCallbacks
    public final void onConnected(Bundle bundle) {
        this.zaf.lock();
        try {
            this.zan.zaa(bundle);
        } finally {
            this.zaf.unlock();
        }
    }

    @Override // com.google.android.gms.common.api.internal.ConnectionCallbacks
    public final void onConnectionSuspended(int i) {
        this.zaf.lock();
        try {
            this.zan.zaa(i);
        } finally {
            this.zaf.unlock();
        }
    }

    public final void zaa(zaay zaayVar) {
        this.zaj.sendMessage(this.zaj.obtainMessage(1, zaayVar));
    }

    public final void zaa(RuntimeException runtimeException) {
        this.zaj.sendMessage(this.zaj.obtainMessage(2, runtimeException));
    }

    @Override // com.google.android.gms.common.api.internal.zabo
    public final void zaa(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String concat = String.valueOf(str).concat("  ");
        printWriter.append((CharSequence) str).append("mState=").println(this.zan);
        for (Api<?> api : this.zal.keySet()) {
            printWriter.append((CharSequence) str).append((CharSequence) api.zad()).println(":");
            ((Api.Client) Preconditions.checkNotNull(this.zaa.get(api.zac()))).dump(concat, fileDescriptor, printWriter, strArr);
        }
    }
}
