package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
/* loaded from: classes.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda201 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda201 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda201();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda201() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$localSearch$198;
        lambda$localSearch$198 = MessagesStorage.lambda$localSearch$198((DialogsSearchAdapter.DialogSearchResult) obj, (DialogsSearchAdapter.DialogSearchResult) obj2);
        return lambda$localSearch$198;
    }
}
