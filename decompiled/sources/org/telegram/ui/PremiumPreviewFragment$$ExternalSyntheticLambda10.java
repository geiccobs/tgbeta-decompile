package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda10 implements RequestDelegate {
    public static final /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda10 INSTANCE = new PremiumPreviewFragment$$ExternalSyntheticLambda10();

    private /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda10() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        PremiumPreviewFragment.lambda$sentShowFeaturePreview$10(tLObject, tL_error);
    }
}
