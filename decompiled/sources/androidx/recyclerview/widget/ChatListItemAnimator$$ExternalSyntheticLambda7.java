package androidx.recyclerview.widget;

import androidx.recyclerview.widget.RecyclerView;
import java.util.Comparator;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatListItemAnimator$$ExternalSyntheticLambda7 implements Comparator {
    public static final /* synthetic */ ChatListItemAnimator$$ExternalSyntheticLambda7 INSTANCE = new ChatListItemAnimator$$ExternalSyntheticLambda7();

    private /* synthetic */ ChatListItemAnimator$$ExternalSyntheticLambda7() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return ChatListItemAnimator.lambda$runAlphaEnterTransition$1((RecyclerView.ViewHolder) obj, (RecyclerView.ViewHolder) obj2);
    }
}
