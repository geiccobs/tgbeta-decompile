package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes4.dex */
public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda2 implements Comparator {
    public static final /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda2 INSTANCE = new AndroidUtilities$$ExternalSyntheticLambda2();

    private /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda2() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.lambda$pruneOverlaps$3((AndroidUtilities.LinkSpec) obj, (AndroidUtilities.LinkSpec) obj2);
    }
}
