package com.google.android.gms.common.api.internal;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.util.concurrent.atomic.AtomicReference;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public abstract class zal extends LifecycleCallback implements DialogInterface.OnCancelListener {
    protected volatile boolean zaa;
    protected final AtomicReference<zak> zab;
    protected final GoogleApiAvailability zac;
    private final Handler zad;

    public zal(LifecycleFragment lifecycleFragment) {
        this(lifecycleFragment, GoogleApiAvailability.getInstance());
    }

    protected abstract void zaa();

    public abstract void zaa(ConnectionResult connectionResult, int i);

    zal(LifecycleFragment lifecycleFragment, GoogleApiAvailability googleApiAvailability) {
        super(lifecycleFragment);
        this.zab = new AtomicReference<>(null);
        this.zad = new com.google.android.gms.internal.base.zas(Looper.getMainLooper());
        this.zac = googleApiAvailability;
    }

    @Override // android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        zaa(new ConnectionResult(13, null), zaa(this.zab.get()));
        zab();
    }

    @Override // com.google.android.gms.common.api.internal.LifecycleCallback
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            this.zab.set(bundle.getBoolean("resolving_error", false) ? new zak(new ConnectionResult(bundle.getInt("failed_status"), (PendingIntent) bundle.getParcelable("failed_resolution")), bundle.getInt("failed_client_id", -1)) : null);
        }
    }

    @Override // com.google.android.gms.common.api.internal.LifecycleCallback
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        zak zakVar = this.zab.get();
        if (zakVar != null) {
            bundle.putBoolean("resolving_error", true);
            bundle.putInt("failed_client_id", zakVar.zaa());
            bundle.putInt("failed_status", zakVar.zab().getErrorCode());
            bundle.putParcelable("failed_resolution", zakVar.zab().getResolution());
        }
    }

    @Override // com.google.android.gms.common.api.internal.LifecycleCallback
    public void onStart() {
        super.onStart();
        this.zaa = true;
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x0063  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0067  */
    @Override // com.google.android.gms.common.api.internal.LifecycleCallback
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onActivityResult(int r5, int r6, android.content.Intent r7) {
        /*
            r4 = this;
            java.util.concurrent.atomic.AtomicReference<com.google.android.gms.common.api.internal.zak> r0 = r4.zab
            java.lang.Object r0 = r0.get()
            com.google.android.gms.common.api.internal.zak r0 = (com.google.android.gms.common.api.internal.zak) r0
            r1 = 1
            r2 = 0
            if (r5 == r1) goto L30
            r6 = 2
            if (r5 == r6) goto L10
            goto L60
        L10:
            com.google.android.gms.common.GoogleApiAvailability r5 = r4.zac
            android.app.Activity r6 = r4.getActivity()
            int r5 = r5.isGooglePlayServicesAvailable(r6)
            if (r5 != 0) goto L1d
            goto L1e
        L1d:
            r1 = 0
        L1e:
            if (r0 != 0) goto L21
            return
        L21:
            com.google.android.gms.common.ConnectionResult r6 = r0.zab()
            int r6 = r6.getErrorCode()
            r7 = 18
            if (r6 != r7) goto L61
            if (r5 != r7) goto L61
            return
        L30:
            r5 = -1
            if (r6 != r5) goto L34
            goto L61
        L34:
            if (r6 != 0) goto L60
            if (r0 != 0) goto L39
            return
        L39:
            r5 = 13
            if (r7 == 0) goto L43
            java.lang.String r6 = "<<ResolutionFailureErrorDetail>>"
            int r5 = r7.getIntExtra(r6, r5)
        L43:
            com.google.android.gms.common.api.internal.zak r6 = new com.google.android.gms.common.api.internal.zak
            com.google.android.gms.common.ConnectionResult r7 = new com.google.android.gms.common.ConnectionResult
            r1 = 0
            com.google.android.gms.common.ConnectionResult r3 = r0.zab()
            java.lang.String r3 = r3.toString()
            r7.<init>(r5, r1, r3)
            int r5 = zaa(r0)
            r6.<init>(r7, r5)
            java.util.concurrent.atomic.AtomicReference<com.google.android.gms.common.api.internal.zak> r5 = r4.zab
            r5.set(r6)
            r0 = r6
        L60:
            r1 = 0
        L61:
            if (r1 == 0) goto L67
            r4.zab()
            return
        L67:
            if (r0 == 0) goto L74
            com.google.android.gms.common.ConnectionResult r5 = r0.zab()
            int r6 = r0.zaa()
            r4.zaa(r5, r6)
        L74:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.common.api.internal.zal.onActivityResult(int, int, android.content.Intent):void");
    }

    @Override // com.google.android.gms.common.api.internal.LifecycleCallback
    public void onStop() {
        super.onStop();
        this.zaa = false;
    }

    public final void zab() {
        this.zab.set(null);
        zaa();
    }

    public final void zab(ConnectionResult connectionResult, int i) {
        zak zakVar = new zak(connectionResult, i);
        if (this.zab.compareAndSet(null, zakVar)) {
            this.zad.post(new zan(this, zakVar));
        }
    }

    private static int zaa(zak zakVar) {
        if (zakVar == null) {
            return -1;
        }
        return zakVar.zaa();
    }
}
