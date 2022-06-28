package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class PrivacySettingsActivity$$ExternalSyntheticLambda5 implements RequestDelegate {
    public static final /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda5 INSTANCE = new PrivacySettingsActivity$$ExternalSyntheticLambda5();

    private /* synthetic */ PrivacySettingsActivity$$ExternalSyntheticLambda5() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        PrivacySettingsActivity.lambda$createView$12(tLObject, tL_error);
    }
}
