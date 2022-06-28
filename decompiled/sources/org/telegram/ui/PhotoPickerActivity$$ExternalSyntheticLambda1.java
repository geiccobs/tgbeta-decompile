package org.telegram.ui;

import org.telegram.ui.Components.AlertsCreator;
/* loaded from: classes4.dex */
public final /* synthetic */ class PhotoPickerActivity$$ExternalSyntheticLambda1 implements AlertsCreator.ScheduleDatePickerDelegate {
    public final /* synthetic */ PhotoPickerActivity f$0;

    public /* synthetic */ PhotoPickerActivity$$ExternalSyntheticLambda1(PhotoPickerActivity photoPickerActivity) {
        this.f$0 = photoPickerActivity;
    }

    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
    public final void didSelectDate(boolean z, int i) {
        this.f$0.sendSelectedPhotos(z, i);
    }
}
