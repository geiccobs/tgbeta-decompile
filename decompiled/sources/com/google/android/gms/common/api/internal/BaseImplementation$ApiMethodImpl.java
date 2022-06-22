package com.google.android.gms.common.api.internal;

import android.app.PendingIntent;
import android.os.DeadObjectException;
import android.os.RemoteException;
import androidx.annotation.RecentlyNonNull;
import androidx.annotation.RecentlyNullable;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public abstract class BaseImplementation$ApiMethodImpl<R extends Result, A extends Api.AnyClient> extends BasePendingResult<R> implements BaseImplementation$ResultHolder<R> {
    private final Api<?> mApi;
    private final Api.AnyClientKey<A> mClientKey;

    protected abstract void doExecute(@RecentlyNonNull A a) throws RemoteException;

    protected void onSetFailedResult(@RecentlyNonNull R r) {
    }

    public BaseImplementation$ApiMethodImpl(@RecentlyNonNull Api<?> api, @RecentlyNonNull GoogleApiClient googleApiClient) {
        super((GoogleApiClient) Preconditions.checkNotNull(googleApiClient, "GoogleApiClient must not be null"));
        Preconditions.checkNotNull(api, "Api must not be null");
        this.mClientKey = (Api.AnyClientKey<A>) api.zac();
        this.mApi = api;
    }

    @RecentlyNonNull
    public final Api.AnyClientKey<A> getClientKey() {
        return this.mClientKey;
    }

    @RecentlyNullable
    public final Api<?> getApi() {
        return this.mApi;
    }

    public final void run(@RecentlyNonNull A a) throws DeadObjectException {
        try {
            doExecute(a);
        } catch (DeadObjectException e) {
            setFailedResult(e);
            throw e;
        } catch (RemoteException e2) {
            setFailedResult(e2);
        }
    }

    public final void setFailedResult(@RecentlyNonNull Status status) {
        Preconditions.checkArgument(!status.isSuccess(), "Failed result must not be success");
        R createFailedResult = createFailedResult(status);
        setResult((BaseImplementation$ApiMethodImpl<R, A>) createFailedResult);
        onSetFailedResult(createFailedResult);
    }

    private void setFailedResult(RemoteException remoteException) {
        setFailedResult(new Status(8, remoteException.getLocalizedMessage(), (PendingIntent) null));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public /* bridge */ /* synthetic */ void setResult(@RecentlyNonNull Object obj) {
        super.setResult((BaseImplementation$ApiMethodImpl<R, A>) ((Result) obj));
    }
}
