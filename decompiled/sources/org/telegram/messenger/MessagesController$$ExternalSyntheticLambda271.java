package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda271 implements RequestDelegate {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda271 INSTANCE = new MessagesController$$ExternalSyntheticLambda271();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda271() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$deleteParticipantFromChat$246(tLObject, tL_error);
    }
}