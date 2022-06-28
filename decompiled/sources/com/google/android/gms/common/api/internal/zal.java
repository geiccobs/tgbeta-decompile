package com.google.android.gms.common.api.internal;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import java.util.concurrent.atomic.AtomicReference;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
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

    public zal(LifecycleFragment lifecycleFragment, GoogleApiAvailability googleApiAvailability) {
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
        zak zakVar;
        super.onCreate(bundle);
        if (bundle != null) {
            AtomicReference<zak> atomicReference = this.zab;
            if (bundle.getBoolean("resolving_error", false)) {
                zakVar = new zak(new ConnectionResult(bundle.getInt("failed_status"), (PendingIntent) bundle.getParcelable("failed_resolution")), bundle.getInt("failed_client_id", -1));
            } else {
                zakVar = null;
            }
            atomicReference.set(zakVar);
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

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.google.android.gms.common.api.internal.LifecycleCallback
    public void onActivityResult(int i, int i2, Intent intent) {
        zak zakVar = this.zab.get();
        boolean z = true;
        switch (i) {
            case 1:
                if (i2 != -1) {
                    if (i2 == 0) {
                        if (zakVar == null) {
                            return;
                        }
                        int i3 = 13;
                        if (intent != null) {
                            i3 = intent.getIntExtra("<<ResolutionFailureErrorDetail>>", 13);
                        }
                        zak zakVar2 = new zak(new ConnectionResult(i3, null, zakVar.zab().toString()), zaa(zakVar));
                        this.zab.set(zakVar2);
                        zakVar = zakVar2;
                        z = false;
                        break;
                    }
                    z = false;
                    break;
                }
                break;
            case 2:
                int isGooglePlayServicesAvailable = this.zac.isGooglePlayServicesAvailable(getActivity());
                if (isGooglePlayServicesAvailable != 0) {
                    z = false;
                }
                if (zakVar == null) {
                    return;
                }
                if (zakVar.zab().getErrorCode() == 18 && isGooglePlayServicesAvailable == 18) {
                    return;
                }
                break;
            default:
                z = false;
                break;
        }
        if (z) {
            zab();
        } else if (zakVar != null) {
            zaa(zakVar.zab(), zakVar.zaa());
        }
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
