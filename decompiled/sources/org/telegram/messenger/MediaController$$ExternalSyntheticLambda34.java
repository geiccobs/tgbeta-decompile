package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MediaController;
/* loaded from: classes4.dex */
public final /* synthetic */ class MediaController$$ExternalSyntheticLambda34 implements Comparator {
    public static final /* synthetic */ MediaController$$ExternalSyntheticLambda34 INSTANCE = new MediaController$$ExternalSyntheticLambda34();

    private /* synthetic */ MediaController$$ExternalSyntheticLambda34() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return MediaController.lambda$loadGalleryPhotosAlbums$39((MediaController.PhotoEntry) obj, (MediaController.PhotoEntry) obj2);
    }
}
