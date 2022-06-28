package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda278 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda278 INSTANCE = new MessagesController$$ExternalSyntheticLambda278();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda278() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$markMentionMessageAsRead$193(tLObject, tL_error);
    }
}
