package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import android.util.Log;
import androidx.loader.content.AsyncTaskLoader;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.SignInConnectionListener;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public final class zbc extends AsyncTaskLoader<Void> implements SignInConnectionListener {
    private final Semaphore zba = new Semaphore(0);
    private final Set<GoogleApiClient> zbb;

    public zbc(Context context, Set<GoogleApiClient> set) {
        super(context);
        this.zbb = set;
    }

    @Override // androidx.loader.content.AsyncTaskLoader
    public final /* bridge */ /* synthetic */ Void loadInBackground() {
        int i = 0;
        for (GoogleApiClient googleApiClient : this.zbb) {
            if (googleApiClient.maybeSignIn(this)) {
                i++;
            }
        }
        try {
            this.zba.tryAcquire(i, 5L, TimeUnit.SECONDS);
            return null;
        } catch (InterruptedException e) {
            Log.i("GACSignInLoader", "Unexpected InterruptedException", e);
            Thread.currentThread().interrupt();
            return null;
        }
    }

    @Override // com.google.android.gms.common.api.internal.SignInConnectionListener
    public final void onComplete() {
        this.zba.release();
    }

    @Override // androidx.loader.content.Loader
    protected final void onStartLoading() {
        this.zba.drainPermits();
        forceLoad();
    }
}
