package org.telegram.ui.Components;

import org.telegram.ui.Components.NumberPicker;
/* loaded from: classes5.dex */
public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda12 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda12 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda12();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda12() {
    }

    @Override // org.telegram.ui.Components.NumberPicker.Formatter
    public final String format(int i) {
        String format;
        format = String.format("%02d", Integer.valueOf(i));
        return format;
    }
}