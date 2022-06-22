package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$MessageEntity;
/* loaded from: classes.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda125 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda125 INSTANCE = new MediaDataController$$ExternalSyntheticLambda125();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda125() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$getTextStyleRuns$138;
        lambda$getTextStyleRuns$138 = MediaDataController.lambda$getTextStyleRuns$138((TLRPC$MessageEntity) obj, (TLRPC$MessageEntity) obj2);
        return lambda$getTextStyleRuns$138;
    }
}
