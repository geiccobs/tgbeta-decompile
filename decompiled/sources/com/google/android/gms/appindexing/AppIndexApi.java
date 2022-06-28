package com.google.android.gms.appindexing;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import java.util.List;
/* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
@Deprecated
/* loaded from: classes3.dex */
public interface AppIndexApi {

    /* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public interface ActionResult {
        PendingResult<Status> end(GoogleApiClient googleApiClient);

        PendingResult<Status> getPendingResult();
    }

    /* compiled from: com.google.firebase:firebase-appindexing@@20.0.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public static final class AppIndexingLink {
        public final Uri appIndexingUrl;
        public final int viewId;
        public final Uri webUrl;

        public AppIndexingLink(Uri appUri, Uri webUrl, View view) {
            this.appIndexingUrl = appUri;
            this.webUrl = webUrl;
            this.viewId = view.getId();
        }

        public AppIndexingLink(Uri appUri, View view) {
            this(appUri, null, view);
        }
    }

    @Deprecated
    ActionResult action(GoogleApiClient googleApiClient, Action action);

    PendingResult<Status> end(GoogleApiClient googleApiClient, Action action);

    PendingResult<Status> start(GoogleApiClient googleApiClient, Action action);

    @Deprecated
    PendingResult<Status> view(GoogleApiClient googleApiClient, Activity activity, Intent intent, String str, Uri uri, List<AppIndexingLink> list);

    @Deprecated
    PendingResult<Status> view(GoogleApiClient googleApiClient, Activity activity, Uri uri, String str, Uri uri2, List<AppIndexingLink> list);

    @Deprecated
    PendingResult<Status> viewEnd(GoogleApiClient googleApiClient, Activity activity, Intent intent);

    @Deprecated
    PendingResult<Status> viewEnd(GoogleApiClient googleApiClient, Activity activity, Uri uri);
}
