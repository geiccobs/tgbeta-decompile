package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
/* loaded from: classes4.dex */
public final /* synthetic */ class MessagesStorage$$ExternalSyntheticLambda113 implements Comparator {
    public static final /* synthetic */ MessagesStorage$$ExternalSyntheticLambda113 INSTANCE = new MessagesStorage$$ExternalSyntheticLambda113();

    private /* synthetic */ MessagesStorage$$ExternalSyntheticLambda113() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$localSearch$198((DialogsSearchAdapter.DialogSearchResult) obj, (DialogsSearchAdapter.DialogSearchResult) obj2);
    }
}