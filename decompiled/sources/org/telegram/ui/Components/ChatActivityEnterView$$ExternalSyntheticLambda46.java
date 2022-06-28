package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
/* loaded from: classes5.dex */
public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda46 implements Runnable {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda46 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda46();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda46() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        MediaController.getInstance().stopRecording(0, false, 0);
    }
}
