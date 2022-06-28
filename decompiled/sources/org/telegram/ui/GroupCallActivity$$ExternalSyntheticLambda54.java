package org.telegram.ui;

import org.telegram.ui.Components.NumberPicker;
/* loaded from: classes4.dex */
public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda54 implements NumberPicker.Formatter {
    public static final /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda54 INSTANCE = new GroupCallActivity$$ExternalSyntheticLambda54();

    private /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda54() {
    }

    @Override // org.telegram.ui.Components.NumberPicker.Formatter
    public final String format(int i) {
        String format;
        format = String.format("%02d", Integer.valueOf(i));
        return format;
    }
}
