package com.google.android.gms.wearable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
@Deprecated
/* loaded from: classes3.dex */
public interface NodeApi {

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public interface GetConnectedNodesResult extends Result {
        List<Node> getNodes();
    }

    /* compiled from: com.google.android.gms:play-services-wearable@@17.1.0 */
    @Deprecated
    /* loaded from: classes3.dex */
    public interface GetLocalNodeResult extends Result {
        Node getNode();
    }

    PendingResult<GetConnectedNodesResult> getConnectedNodes(GoogleApiClient googleApiClient);

    PendingResult<GetLocalNodeResult> getLocalNode(GoogleApiClient googleApiClient);
}
