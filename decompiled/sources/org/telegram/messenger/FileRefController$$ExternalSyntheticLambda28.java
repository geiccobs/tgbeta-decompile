package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda28 implements RequestDelegate {
    public static final /* synthetic */ FileRefController$$ExternalSyntheticLambda28 INSTANCE = new FileRefController$$ExternalSyntheticLambda28();

    private /* synthetic */ FileRefController$$ExternalSyntheticLambda28() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        FileRefController.lambda$onUpdateObjectReference$28(tLObject, tL_error);
    }
}
