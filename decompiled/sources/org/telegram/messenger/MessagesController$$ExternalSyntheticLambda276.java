package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda276 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda276 INSTANCE = new MessagesController$$ExternalSyntheticLambda276();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda276() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$installTheme$97(tLObject, tL_error);
    }
}
