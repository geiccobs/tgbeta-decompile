package com.google.android.gms.search;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
/* loaded from: classes3.dex */
public interface SearchAuthApi {

    /* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
    /* loaded from: classes3.dex */
    public interface GoogleNowAuthResult extends Result {
        GoogleNowAuthState getGoogleNowAuthState();
    }

    PendingResult<Status> clearToken(GoogleApiClient googleApiClient, String str);

    PendingResult<GoogleNowAuthResult> getGoogleNowAuth(GoogleApiClient googleApiClient, String str);
}
