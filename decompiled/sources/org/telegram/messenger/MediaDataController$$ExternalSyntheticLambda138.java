package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$MessageEntity;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda138 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda138 INSTANCE = new MediaDataController$$ExternalSyntheticLambda138();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda138() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getTextStyleRuns$150;
        lambda$getTextStyleRuns$150 = MediaDataController.lambda$getTextStyleRuns$150((TLRPC$MessageEntity) obj, (TLRPC$MessageEntity) obj2);
        return lambda$getTextStyleRuns$150;
    }
}
