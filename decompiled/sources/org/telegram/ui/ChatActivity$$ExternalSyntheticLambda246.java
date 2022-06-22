package org.telegram.ui;

import android.view.MotionEvent;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda246 implements RecyclerListView.OnInterceptTouchListener {
    public static final /* synthetic */ ChatActivity$$ExternalSyntheticLambda246 INSTANCE = new ChatActivity$$ExternalSyntheticLambda246();

    private /* synthetic */ ChatActivity$$ExternalSyntheticLambda246() {
    }

    @Override // org.telegram.ui.Components.RecyclerListView.OnInterceptTouchListener
    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        boolean lambda$showChatThemeBottomSheet$248;
        lambda$showChatThemeBottomSheet$248 = ChatActivity.lambda$showChatThemeBottomSheet$248(motionEvent);
        return lambda$showChatThemeBottomSheet$248;
    }
}
