package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$TL_topPeer;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda141 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda141 INSTANCE = new MediaDataController$$ExternalSyntheticLambda141();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda141() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$increasePeerRaiting$127;
        lambda$increasePeerRaiting$127 = MediaDataController.lambda$increasePeerRaiting$127((TLRPC$TL_topPeer) obj, (TLRPC$TL_topPeer) obj2);
        return lambda$increasePeerRaiting$127;
    }
}
