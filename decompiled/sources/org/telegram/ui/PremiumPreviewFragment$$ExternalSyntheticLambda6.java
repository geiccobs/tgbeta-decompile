package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes3.dex */
public final /* synthetic */ class PremiumPreviewFragment$$ExternalSyntheticLambda6 implements RequestDelegate {
    public static final /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda6 INSTANCE = new PremiumPreviewFragment$$ExternalSyntheticLambda6();

    private /* synthetic */ PremiumPreviewFragment$$ExternalSyntheticLambda6() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        PremiumPreviewFragment.lambda$sentShowFeaturePreview$10(tLObject, tLRPC$TL_error);
    }
}
