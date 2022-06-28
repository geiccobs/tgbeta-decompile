package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class DownloadController$$ExternalSyntheticLambda4 implements RequestDelegate {
    public static final /* synthetic */ DownloadController$$ExternalSyntheticLambda4 INSTANCE = new DownloadController$$ExternalSyntheticLambda4();

    private /* synthetic */ DownloadController$$ExternalSyntheticLambda4() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        DownloadController.lambda$savePresetToServer$3(tLObject, tL_error);
    }
}
