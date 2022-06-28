package org.telegram.messenger;

import android.graphics.Canvas;
import android.graphics.PostProcessor;
/* loaded from: classes4.dex */
public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda11 implements PostProcessor {
    public static final /* synthetic */ NotificationsController$$ExternalSyntheticLambda11 INSTANCE = new NotificationsController$$ExternalSyntheticLambda11();

    private /* synthetic */ NotificationsController$$ExternalSyntheticLambda11() {
    }

    @Override // android.graphics.PostProcessor
    public final int onPostProcess(Canvas canvas) {
        return NotificationsController.lambda$loadRoundAvatar$35(canvas);
    }
}
