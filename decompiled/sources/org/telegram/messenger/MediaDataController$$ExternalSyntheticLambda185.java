package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda185 implements RequestDelegate {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda185 INSTANCE = new MediaDataController$$ExternalSyntheticLambda185();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda185() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removeInline$117(tLObject, tLRPC$TL_error);
    }
}
