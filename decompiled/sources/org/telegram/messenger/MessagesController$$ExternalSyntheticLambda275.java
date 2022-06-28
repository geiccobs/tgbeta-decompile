package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda275 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda275 INSTANCE = new MessagesController$$ExternalSyntheticLambda275();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda275() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$installTheme$96(tLObject, tL_error);
    }
}
