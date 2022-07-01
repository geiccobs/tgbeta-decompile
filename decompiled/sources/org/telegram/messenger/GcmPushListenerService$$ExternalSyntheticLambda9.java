package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class GcmPushListenerService$$ExternalSyntheticLambda9 implements RequestDelegate {
    public static final /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda9 INSTANCE = new GcmPushListenerService$$ExternalSyntheticLambda9();

    private /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda9() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        GcmPushListenerService.lambda$sendRegistrationToServer$7(tLObject, tLRPC$TL_error);
    }
}
