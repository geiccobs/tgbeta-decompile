package org.telegram.ui.Components;

import android.view.View;
import android.view.WindowInsets;
/* loaded from: classes5.dex */
public final /* synthetic */ class BotWebViewSheet$$ExternalSyntheticLambda11 implements View.OnApplyWindowInsetsListener {
    public static final /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda11 INSTANCE = new BotWebViewSheet$$ExternalSyntheticLambda11();

    private /* synthetic */ BotWebViewSheet$$ExternalSyntheticLambda11() {
    }

    @Override // android.view.View.OnApplyWindowInsetsListener
    public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
        return view.setPadding(0, 0, 0, windowInsets.getSystemWindowInsetBottom());
    }
}
