package org.telegram.messenger;

import android.media.SoundPool;
/* loaded from: classes4.dex */
public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda33 implements SoundPool.OnLoadCompleteListener {
    public static final /* synthetic */ NotificationsController$$ExternalSyntheticLambda33 INSTANCE = new NotificationsController$$ExternalSyntheticLambda33();

    private /* synthetic */ NotificationsController$$ExternalSyntheticLambda33() {
    }

    @Override // android.media.SoundPool.OnLoadCompleteListener
    public final void onLoadComplete(SoundPool soundPool, int i, int i2) {
        NotificationsController.lambda$playOutChatSound$37(soundPool, i, i2);
    }
}