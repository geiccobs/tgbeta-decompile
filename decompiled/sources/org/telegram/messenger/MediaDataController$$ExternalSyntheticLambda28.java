package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda28 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda28 INSTANCE = new MediaDataController$$ExternalSyntheticLambda28();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda28() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$getTextStyleRuns$138((TLRPC.MessageEntity) obj, (TLRPC.MessageEntity) obj2);
    }
}
