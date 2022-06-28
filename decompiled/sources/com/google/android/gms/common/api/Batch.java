package com.google.android.gms.common.api;

import com.google.android.gms.common.api.internal.BasePendingResult;
import java.util.ArrayList;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class Batch extends BasePendingResult<BatchResult> {
    private int zab;
    private boolean zac;
    private boolean zad;
    private final PendingResult<?>[] zae;
    private final Object zaf;

    private Batch(List<PendingResult<?>> list, GoogleApiClient googleApiClient) {
        super(googleApiClient);
        this.zaf = new Object();
        int size = list.size();
        this.zab = size;
        PendingResult<?>[] pendingResultArr = new PendingResult[size];
        this.zae = pendingResultArr;
        if (list.isEmpty()) {
            setResult(new BatchResult(Status.RESULT_SUCCESS, pendingResultArr));
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            PendingResult<?> pendingResult = list.get(i);
            this.zae[i] = pendingResult;
            pendingResult.addStatusListener(new zab(this));
        }
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    public static final class Builder {
        private List<PendingResult<?>> zaa = new ArrayList();
        private GoogleApiClient zab;

        public Builder(GoogleApiClient googleApiClient) {
            this.zab = googleApiClient;
        }

        public final <R extends Result> BatchResultToken<R> add(PendingResult<R> pendingResult) {
            BatchResultToken<R> batchResultToken = new BatchResultToken<>(this.zaa.size());
            this.zaa.add(pendingResult);
            return batchResultToken;
        }

        public final Batch build() {
            return new Batch(this.zaa, this.zab, null);
        }
    }

    @Override // com.google.android.gms.common.api.internal.BasePendingResult, com.google.android.gms.common.api.PendingResult
    public final void cancel() {
        super.cancel();
        for (PendingResult<?> pendingResult : this.zae) {
            pendingResult.cancel();
        }
    }

    @Override // com.google.android.gms.common.api.internal.BasePendingResult
    public final BatchResult createFailedResult(Status status) {
        return new BatchResult(status, this.zae);
    }

    /* synthetic */ Batch(List list, GoogleApiClient googleApiClient, zab zabVar) {
        this(list, googleApiClient);
    }

    public static /* synthetic */ int zab(Batch batch) {
        int i = batch.zab;
        batch.zab = i - 1;
        return i;
    }
}
