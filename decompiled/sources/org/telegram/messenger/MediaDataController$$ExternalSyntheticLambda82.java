package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda82 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda82 INSTANCE = new MediaDataController$$ExternalSyntheticLambda82();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda82() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$removePeer$114(tLObject, tL_error);
    }
}
