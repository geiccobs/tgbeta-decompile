package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;
/* loaded from: classes5.dex */
public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda51 implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda51 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda51();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda51() {
    }

    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
