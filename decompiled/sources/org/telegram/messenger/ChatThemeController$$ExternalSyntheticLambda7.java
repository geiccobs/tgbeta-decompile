package org.telegram.messenger;

import android.util.Pair;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class ChatThemeController$$ExternalSyntheticLambda7 implements ResultCallback {
    public static final /* synthetic */ ChatThemeController$$ExternalSyntheticLambda7 INSTANCE = new ChatThemeController$$ExternalSyntheticLambda7();

    private /* synthetic */ ChatThemeController$$ExternalSyntheticLambda7() {
    }

    @Override // org.telegram.tgnet.ResultCallback
    public final void onComplete(Object obj) {
        ChatThemeController.lambda$preloadAllWallpaperThumbs$4((Pair) obj);
    }

    @Override // org.telegram.tgnet.ResultCallback
    public /* synthetic */ void onError(Throwable th) {
        ResultCallback.CC.$default$onError(this, th);
    }

    @Override // org.telegram.tgnet.ResultCallback
    public /* synthetic */ void onError(TLRPC.TL_error tL_error) {
        ResultCallback.CC.$default$onError(this, tL_error);
    }
}
