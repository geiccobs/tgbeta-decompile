package org.telegram.ui.Components;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes5.dex */
public final /* synthetic */ class TranslateAlert$$ExternalSyntheticLambda3 implements RequestDelegate {
    public static final /* synthetic */ TranslateAlert$$ExternalSyntheticLambda3 INSTANCE = new TranslateAlert$$ExternalSyntheticLambda3();

    private /* synthetic */ TranslateAlert$$ExternalSyntheticLambda3() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        TranslateAlert.lambda$translateText$13(tLObject, tL_error);
    }
}
