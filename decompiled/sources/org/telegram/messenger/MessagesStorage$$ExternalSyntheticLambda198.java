package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.MessagesController;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda198 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda198 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda198();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda198() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$loadDialogFilters$40;
        lambda$loadDialogFilters$40 = MessagesStorage.lambda$loadDialogFilters$40((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
        return lambda$loadDialogFilters$40;
    }
}
