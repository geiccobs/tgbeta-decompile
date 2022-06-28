package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda269 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda269 INSTANCE = new MessagesController$$ExternalSyntheticLambda269();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda269() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$completeReadTask$197(tLObject, tL_error);
    }
}
