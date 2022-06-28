package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class GcmPushListenerService$$ExternalSyntheticLambda9 implements RequestDelegate {
    public static final /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda9 INSTANCE = new GcmPushListenerService$$ExternalSyntheticLambda9();

    private /* synthetic */ GcmPushListenerService$$ExternalSyntheticLambda9() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.GcmPushListenerService$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                GcmPushListenerService.lambda$sendRegistrationToServer$6(TLRPC.TL_error.this);
            }
        });
    }
}
