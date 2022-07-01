package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda199 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda199 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda199();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda199() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$processLoadedFilterPeersInternal$44;
        lambda$processLoadedFilterPeersInternal$44 = MessagesStorage.lambda$processLoadedFilterPeersInternal$44((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
        return lambda$processLoadedFilterPeersInternal$44;
    }
}
