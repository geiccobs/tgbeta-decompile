package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda83 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda83 INSTANCE = new MediaDataController$$ExternalSyntheticLambda83();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda83() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$saveDraft$146(tLObject, tL_error);
    }
}
