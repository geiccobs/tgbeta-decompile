package com.google.android.gms.common.api.internal;

import android.os.RemoteException;
import androidx.annotation.RecentlyNonNull;
import androidx.annotation.RecentlyNullable;
import com.google.android.gms.common.Feature;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.TaskCompletionSource;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public abstract class TaskApiCall<A extends Api.AnyClient, ResultT> {
    private final Feature[] zaa;
    private final boolean zab;
    private final int zac;

    @Deprecated
    public TaskApiCall() {
        this.zaa = null;
        this.zab = false;
        this.zac = 0;
    }

    public abstract void doExecute(@RecentlyNonNull A a, @RecentlyNonNull TaskCompletionSource<ResultT> taskCompletionSource) throws RemoteException;

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public static class Builder<A extends Api.AnyClient, ResultT> {
        private RemoteCall<A, TaskCompletionSource<ResultT>> zaa;
        private boolean zab;
        private Feature[] zac;
        private int zad;

        private Builder() {
            this.zab = true;
            this.zad = 0;
        }

        @RecentlyNonNull
        public Builder<A, ResultT> run(@RecentlyNonNull RemoteCall<A, TaskCompletionSource<ResultT>> remoteCall) {
            this.zaa = remoteCall;
            return this;
        }

        @RecentlyNonNull
        public Builder<A, ResultT> setFeatures(@RecentlyNonNull Feature... featureArr) {
            this.zac = featureArr;
            return this;
        }

        @RecentlyNonNull
        public Builder<A, ResultT> setAutoResolveMissingFeatures(boolean z) {
            this.zab = z;
            return this;
        }

        @RecentlyNonNull
        public Builder<A, ResultT> setMethodKey(int i) {
            this.zad = i;
            return this;
        }

        @RecentlyNonNull
        public TaskApiCall<A, ResultT> build() {
            Preconditions.checkArgument(this.zaa != null, "execute parameter required");
            return new zacj(this, this.zac, this.zab, this.zad);
        }
    }

    public TaskApiCall(@RecentlyNonNull Feature[] featureArr, boolean z, int i) {
        this.zaa = featureArr;
        this.zab = featureArr != null && z;
        this.zac = i;
    }

    @RecentlyNullable
    public final Feature[] zaa() {
        return this.zaa;
    }

    public boolean shouldAutoResolveMissingFeatures() {
        return this.zab;
    }

    public final int zab() {
        return this.zac;
    }

    @RecentlyNonNull
    public static <A extends Api.AnyClient, ResultT> Builder<A, ResultT> builder() {
        return new Builder<>();
    }
}
