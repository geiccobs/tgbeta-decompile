package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda108 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda108 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda108();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda108() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$loadDialogFilters$40((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
    }
}