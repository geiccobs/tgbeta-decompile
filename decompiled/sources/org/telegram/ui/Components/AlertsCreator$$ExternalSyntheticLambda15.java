package org.telegram.ui.Components;

import org.telegram.ui.Components.NumberPicker;
/* loaded from: classes5.dex */
public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda15 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda15 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda15();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda15() {
    }

    @Override // org.telegram.ui.Components.NumberPicker.Formatter
    public final String format(int i) {
        String format;
        format = String.format("%02d", Integer.valueOf(i));
        return format;
    }
}
