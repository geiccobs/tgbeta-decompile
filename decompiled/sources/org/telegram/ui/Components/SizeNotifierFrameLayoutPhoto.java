package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public class SizeNotifierFrameLayoutPhoto extends FrameLayout {
    private SizeNotifierFrameLayoutPhotoDelegate delegate;
    private int keyboardHeight;
    private android.graphics.Rect rect = new android.graphics.Rect();
    private boolean useSmoothKeyboard;
    private WindowManager windowManager;
    private boolean withoutWindow;

    /* loaded from: classes5.dex */
    public interface SizeNotifierFrameLayoutPhotoDelegate {
        void onSizeChanged(int i, boolean z);
    }

    public SizeNotifierFrameLayoutPhoto(Context context, boolean smoothKeyboard) {
        super(context);
        this.useSmoothKeyboard = smoothKeyboard;
    }

    public void setDelegate(SizeNotifierFrameLayoutPhotoDelegate sizeNotifierFrameLayoutPhotoDelegate) {
        this.delegate = sizeNotifierFrameLayoutPhotoDelegate;
    }

    public void setWithoutWindow(boolean value) {
        this.withoutWindow = value;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        notifyHeightChanged();
    }

    public int getKeyboardHeight() {
        return this.keyboardHeight;
    }

    public int measureKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        if (this.withoutWindow) {
            int usableViewHeight = (rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView);
            return usableViewHeight - (this.rect.bottom - this.rect.top);
        }
        int size = (((Activity) rootView.getContext()).getWindow().getDecorView().getHeight() - AndroidUtilities.getViewInset(rootView)) - rootView.getBottom();
        if (size <= Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.statusBarHeight)) {
            return 0;
        }
        return size;
    }

    public void notifyHeightChanged() {
        if (this.delegate != null) {
            this.keyboardHeight = measureKeyboardHeight();
            final boolean isWidthGreater = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;
            post(new Runnable() { // from class: org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SizeNotifierFrameLayoutPhoto.this.m3064xb9d79cf2(isWidthGreater);
                }
            });
        }
    }

    /* renamed from: lambda$notifyHeightChanged$0$org-telegram-ui-Components-SizeNotifierFrameLayoutPhoto */
    public /* synthetic */ void m3064xb9d79cf2(boolean isWidthGreater) {
        SizeNotifierFrameLayoutPhotoDelegate sizeNotifierFrameLayoutPhotoDelegate = this.delegate;
        if (sizeNotifierFrameLayoutPhotoDelegate != null) {
            sizeNotifierFrameLayoutPhotoDelegate.onSizeChanged(this.keyboardHeight, isWidthGreater);
        }
    }
}
