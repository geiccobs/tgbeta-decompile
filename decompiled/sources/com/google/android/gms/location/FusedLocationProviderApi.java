package com.google.android.gms.location;

import android.location.Location;
import androidx.annotation.RecentlyNonNull;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
/* compiled from: com.google.android.gms:play-services-location@@18.0.0 */
@Deprecated
/* loaded from: classes.dex */
public interface FusedLocationProviderApi {
    @RecentlyNonNull
    Location getLastLocation(@RecentlyNonNull GoogleApiClient googleApiClient);

    @RecentlyNonNull
    PendingResult<Status> removeLocationUpdates(@RecentlyNonNull GoogleApiClient googleApiClient, @RecentlyNonNull LocationListener locationListener);

    @RecentlyNonNull
    PendingResult<Status> requestLocationUpdates(@RecentlyNonNull GoogleApiClient googleApiClient, @RecentlyNonNull LocationRequest locationRequest, @RecentlyNonNull LocationListener locationListener);
}
