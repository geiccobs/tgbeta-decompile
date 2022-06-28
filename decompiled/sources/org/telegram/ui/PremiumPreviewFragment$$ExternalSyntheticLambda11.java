package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda11 implements RequestDelegate {
    public static final /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda11 INSTANCE = new PremiumPreviewFragment$$ExternalSyntheticLambda11();

    private /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda11() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        PremiumPreviewFragment.lambda$sentShowScreenStat$7(tLObject, tL_error);
    }
}
