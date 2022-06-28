package com.google.android.gms.common.api;

import android.os.Looper;
import com.google.android.gms.common.api.internal.BasePendingResult;
import com.google.android.gms.common.api.internal.OptionalPendingResultImpl;
import com.google.android.gms.common.api.internal.StatusPendingResult;
import com.google.android.gms.common.internal.Preconditions;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class PendingResults {
    public static PendingResult<Status> immediatePendingResult(Status status) {
        Preconditions.checkNotNull(status, "Result must not be null");
        StatusPendingResult statusPendingResult = new StatusPendingResult(Looper.getMainLooper());
        statusPendingResult.setResult(status);
        return statusPendingResult;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    public static final class zab<R extends Result> extends BasePendingResult<R> {
        public zab(GoogleApiClient googleApiClient) {
            super(googleApiClient);
        }

        @Override // com.google.android.gms.common.api.internal.BasePendingResult
        public final R createFailedResult(Status status) {
            throw new UnsupportedOperationException("Creating failed results is not supported");
        }
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    private static final class zaa<R extends Result> extends BasePendingResult<R> {
        private final R zab;

        public zaa(R r) {
            super(Looper.getMainLooper());
            this.zab = r;
        }

        @Override // com.google.android.gms.common.api.internal.BasePendingResult
        public final R createFailedResult(Status status) {
            if (status.getStatusCode() != this.zab.getStatus().getStatusCode()) {
                throw new UnsupportedOperationException("Creating failed results is not supported");
            }
            return this.zab;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
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

    public static PendingResult<Status> immediatePendingResult(Status status, GoogleApiClient googleApiClient) {
        Preconditions.checkNotNull(status, "Result must not be null");
        StatusPendingResult statusPendingResult = new StatusPendingResult(googleApiClient);
        statusPendingResult.setResult(status);
        return statusPendingResult;
    }

    public static <R extends Result> PendingResult<R> immediateFailedResult(R r, GoogleApiClient googleApiClient) {
        Preconditions.checkNotNull(r, "Result must not be null");
        Preconditions.checkArgument(!r.getStatus().isSuccess(), "Status code must not be SUCCESS");
        zac zacVar = new zac(googleApiClient, r);
        zacVar.setResult(r);
        return zacVar;
    }

    public static <R extends Result> OptionalPendingResult<R> immediatePendingResult(R r) {
        Preconditions.checkNotNull(r, "Result must not be null");
        zab zabVar = new zab(null);
        zabVar.setResult(r);
        return new OptionalPendingResultImpl(zabVar);
    }

    public static <R extends Result> OptionalPendingResult<R> immediatePendingResult(R r, GoogleApiClient googleApiClient) {
        Preconditions.checkNotNull(r, "Result must not be null");
        zab zabVar = new zab(googleApiClient);
        zabVar.setResult(r);
        return new OptionalPendingResultImpl(zabVar);
    }

    public static PendingResult<Status> canceledPendingResult() {
        StatusPendingResult statusPendingResult = new StatusPendingResult(Looper.getMainLooper());
        statusPendingResult.cancel();
        return statusPendingResult;
    }

    public static <R extends Result> PendingResult<R> canceledPendingResult(R r) {
        Preconditions.checkNotNull(r, "Result must not be null");
        Preconditions.checkArgument(r.getStatus().getStatusCode() == 16, "Status code must be CommonStatusCodes.CANCELED");
        zaa zaaVar = new zaa(r);
        zaaVar.cancel();
        return zaaVar;
    }

    private PendingResults() {
    }
}
