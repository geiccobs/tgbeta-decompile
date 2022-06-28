package org.telegram.ui.Components;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.NumberPicker;
/* loaded from: classes5.dex */
public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda18 implements NumberPicker.Formatter {
    public static final /* synthetic */ AlertsCreator$$ExternalSyntheticLambda18 INSTANCE = new AlertsCreator$$ExternalSyntheticLambda18();

    private /* synthetic */ AlertsCreator$$ExternalSyntheticLambda18() {
    }

    @Override // org.telegram.ui.Components.NumberPicker.Formatter
    public final String format(int i) {
        String string;
        string = LocaleController.getString("NotificationsFrequencyDivider", R.string.NotificationsFrequencyDivider);
        return string;
    }
}
