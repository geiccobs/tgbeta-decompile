package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda287 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda287 INSTANCE = new MessagesController$$ExternalSyntheticLambda287();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda287() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$saveTheme$95(tLObject, tL_error);
    }
}
