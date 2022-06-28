package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda279 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda279 INSTANCE = new MessagesController$$ExternalSyntheticLambda279();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda279() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$markMentionsAsRead$201(tLObject, tL_error);
    }
}
