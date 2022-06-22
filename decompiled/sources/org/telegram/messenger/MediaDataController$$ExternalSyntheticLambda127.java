package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda127 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda127 INSTANCE = new MediaDataController$$ExternalSyntheticLambda127();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda127() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$increasePeerRaiting$115;
        lambda$increasePeerRaiting$115 = MediaDataController.lambda$increasePeerRaiting$115((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
        return lambda$increasePeerRaiting$115;
    }
}
