package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda285 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda285 INSTANCE = new MessagesController$$ExternalSyntheticLambda285();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda285() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$reportSpam$56(tLObject, tL_error);
    }
}
