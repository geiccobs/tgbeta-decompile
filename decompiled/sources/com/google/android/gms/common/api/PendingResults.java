package com.google.android.gms.common.api;

import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.api.internal.BasePendingResult;
import com.google.android.gms.common.api.internal.StatusPendingResult;
import com.google.android.gms.common.internal.Preconditions;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes.dex */
public final class PendingResults {

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes.dex */
    public static final class zac<R extends Result> extends BasePendingResult<R> {
        private final R zab;

        public zac(GoogleApiClient googleApiClient, R r) {
            super(googleApiClient);
            this.zab = r;
        }

        @Override // com.google.android.gms.common.api.internal.BasePendingResult
        public final R createFailedResult(Status status) {
            return this.zab;
        }
    }

    @RecentlyNonNull
    public static PendingResult<Status> immediatePendingResult(@RecentlyNonNull Status status, @RecentlyNonNull GoogleApiClient googleApiClient) {
        Preconditions.checkNotNull(status, "Result must not be null");
        StatusPendingResult statusPendingResult = new StatusPendingResult(googleApiClient);
        statusPendingResult.setResult(status);
        return statusPendingResult;
    }

    @RecentlyNonNull
    public static <R extends Result> PendingResult<R> immediateFailedResult(@RecentlyNonNull R r, @RecentlyNonNull GoogleApiClient googleApiClient) {
        Preconditions.checkNotNull(r, "Result must not be null");
        Preconditions.checkArgument(!r.getStatus().isSuccess(), "Status code must not be SUCCESS");
        zac zacVar = new zac(googleApiClient, r);
        zacVar.setResult(r);
        return zacVar;
    }
}
