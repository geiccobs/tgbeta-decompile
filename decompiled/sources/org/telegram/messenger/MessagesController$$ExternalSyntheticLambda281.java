package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda281 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda281 INSTANCE = new MessagesController$$ExternalSyntheticLambda281();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda281() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$markReactionsAsRead$336(tLObject, tL_error);
    }
}
