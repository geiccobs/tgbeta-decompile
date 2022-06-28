package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda273 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda273 INSTANCE = new MessagesController$$ExternalSyntheticLambda273();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda273() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$hidePeerSettingsBar$54(tLObject, tL_error);
    }
}
