package org.telegram.ui;

import android.view.MotionEvent;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda247 implements RecyclerListView.OnInterceptTouchListener {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda247 INSTANCE = new ChatActivity$$ExternalSyntheticLambda247();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda247() {
    }

    @Override // org.telegram.ui.Components.RecyclerListView.OnInterceptTouchListener
    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean lambda$showChatThemeBottomSheet$249;
        lambda$showChatThemeBottomSheet$249 = ChatActivity.lambda$showChatThemeBottomSheet$249(motionEvent);
        return lambda$showChatThemeBottomSheet$249;
    }
}
