package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
/* loaded from: classes5.dex */
public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda45 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda45 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda45();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda45() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
