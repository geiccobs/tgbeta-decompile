package org.telegram.ui.Components;

import android.view.View;
/* loaded from: classes5.dex */
public final /* synthetic */ class PipVideoOverlay$$ExternalSyntheticLambda8 implements View.OnClickListener {
    public static final /* synthetic */ PipVideoOverlay$$ExternalSyntheticLambda8 INSTANCE = new PipVideoOverlay$$ExternalSyntheticLambda8();

    private /* synthetic */ PipVideoOverlay$$ExternalSyntheticLambda8() {
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        PipVideoOverlay.dimissAndDestroy();
    }
}
