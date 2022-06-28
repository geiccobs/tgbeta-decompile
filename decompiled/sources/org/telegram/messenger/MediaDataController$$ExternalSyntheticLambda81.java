package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda81 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda81 INSTANCE = new MediaDataController$$ExternalSyntheticLambda81();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda81() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$removeInline$113(tLObject, tL_error);
    }
}
