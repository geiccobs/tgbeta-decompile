package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda284 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda284 INSTANCE = new MessagesController$$ExternalSyntheticLambda284();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda284() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$reportSpam$55(tLObject, tL_error);
    }
}