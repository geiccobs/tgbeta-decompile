package org.telegram.ui.Components;

import org.telegram.messenger.LocaleController;
import org.telegram.ui.Components.NumberPicker;
/* loaded from: classes5.dex */
public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda16 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda16 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda16();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda16() {
    }

    @Override // org.telegram.ui.Components.NumberPicker.Formatter
    public final String format(int i) {
        String formatPluralString;
        formatPluralString = LocaleController.formatPluralString("Times", i + 1, new Object[0]);
        return formatPluralString;
    }
}
