package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda283 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda283 INSTANCE = new MessagesController$$ExternalSyntheticLambda283();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda283() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$removeSuggestion$22(tLObject, tL_error);
    }
}
