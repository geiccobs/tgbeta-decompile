package org.telegram.ui.Components;

import org.telegram.messenger.MediaController;
import org.telegram.ui.Components.AlertsCreator;
/* loaded from: classes5.dex */
public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda50 implements AlertsCreator.ScheduleDatePickerDelegate {
    public static final /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda50 INSTANCE = new ChatActivityEnterView$$ExternalSyntheticLambda50();

    private /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda50() {
    }

    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
    public final void didSelectDate(boolean z, int i) {
        MediaController.getInstance().stopRecording(1, z, i);
    }
}
