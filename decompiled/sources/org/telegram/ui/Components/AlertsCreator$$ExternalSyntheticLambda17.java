package org.telegram.ui.Components;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.NumberPicker;
/* loaded from: classes5.dex */
public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda17 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda17 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda17();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda17() {
    }

    @Override // org.telegram.ui.Components.NumberPicker.Formatter
    public final String format(int i) {
        String formatPluralString;
        formatPluralString = LocaleController.formatPluralString("Minutes", i + 1, new Object[0]);
        return formatPluralString;
    }
}
