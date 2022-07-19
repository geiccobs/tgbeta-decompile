package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$MessageEntity;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda127 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda127 INSTANCE = new MediaDataController$$ExternalSyntheticLambda127();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda127() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getTextStyleRuns$142;
        lambda$getTextStyleRuns$142 = MediaDataController.lambda$getTextStyleRuns$142((TLRPC$MessageEntity) obj, (TLRPC$MessageEntity) obj2);
        return lambda$getTextStyleRuns$142;
    }
}
