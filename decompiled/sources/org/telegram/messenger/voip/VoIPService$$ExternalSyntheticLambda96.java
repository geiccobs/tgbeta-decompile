package org.telegram.messenger.voip;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class VoIPService$$ExternalSyntheticLambda96 implements RequestDelegate {
    public static final /* synthetic */ VoIPService$$ExternalSyntheticLambda96 INSTANCE = new VoIPService$$ExternalSyntheticLambda96();

    private /* synthetic */ VoIPService$$ExternalSyntheticLambda96() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        VoIPService.lambda$onTgVoipStop$75(tLObject, tL_error);
    }
}
