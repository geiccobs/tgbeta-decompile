package org.telegram.messenger;

import android.graphics.ImageDecoder;
/* loaded from: classes4.dex */
public final /* synthetic */ class NotificationsController$$ExternalSyntheticLambda0 implements ImageDecoder.OnHeaderDecodedListener {
    public static final /* synthetic */ NotificationsController$$ExternalSyntheticLambda0 INSTANCE = new NotificationsController$$ExternalSyntheticLambda0();

    private /* synthetic */ NotificationsController$$ExternalSyntheticLambda0() {
    }

    @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
        imageDecoder.setPostProcessor(NotificationsController$$ExternalSyntheticLambda11.INSTANCE);
    }
}
