package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda179 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda179 INSTANCE = new MediaDataController$$ExternalSyntheticLambda179();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda179() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removeInline$117(tLObject, tLRPC$TL_error);
    }
}
