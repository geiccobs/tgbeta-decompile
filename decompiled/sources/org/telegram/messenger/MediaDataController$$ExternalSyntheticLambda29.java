package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;
/* loaded from: classes4.dex */
public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda29 implements Comparator {
    public static final /* synthetic */ MediaDataController$$ExternalSyntheticLambda29 INSTANCE = new MediaDataController$$ExternalSyntheticLambda29();

    private /* synthetic */ MediaDataController$$ExternalSyntheticLambda29() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return MediaDataController.lambda$static$120((TLRPC.MessageEntity) obj, (TLRPC.MessageEntity) obj2);
    }
}