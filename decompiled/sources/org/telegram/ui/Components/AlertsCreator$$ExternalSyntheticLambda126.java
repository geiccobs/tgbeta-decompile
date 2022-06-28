package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes5.dex */
public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda126 implements RequestDelegate {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda126 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda126();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda126() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AlertsCreator.lambda$createDeleteMessagesAlert$117(tLObject, tL_error);
    }
}
