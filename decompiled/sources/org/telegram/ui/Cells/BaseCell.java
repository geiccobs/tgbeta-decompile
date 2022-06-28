package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
/* loaded from: classes4.dex */
public abstract class BaseCell extends ViewGroup {
    private boolean checkingForLongPress = false;
    private CheckForLongPress pendingCheckForLongPress = null;
    private int pressCount = 0;
    private CheckForTap pendingCheckForTap = null;

    static /* synthetic */ int access$104(BaseCell x0) {
        int i = x0.pressCount + 1;
        x0.pressCount = i;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class CheckForTap implements Runnable {
        private CheckForTap() {
            BaseCell.this = r1;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (BaseCell.this.pendingCheckForLongPress == null) {
                BaseCell.this.pendingCheckForLongPress = new CheckForLongPress();
            }
            BaseCell.this.pendingCheckForLongPress.currentPressCount = BaseCell.access$104(BaseCell.this);
            BaseCell baseCell = BaseCell.this;
            baseCell.postDelayed(baseCell.pendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
            BaseCell.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (BaseCell.this.checkingForLongPress && BaseCell.this.getParent() != null && this.currentPressCount == BaseCell.this.pressCount) {
                BaseCell.this.checkingForLongPress = false;
                BaseCell.this.performHapticFeedback(0);
                if (BaseCell.this.onLongPress()) {
                    MotionEvent event = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                    BaseCell.this.onTouchEvent(event);
                    event.recycle();
                }
            }
        }
    }

    public BaseCell(Context context) {
        super(context);
        setWillNotDraw(false);
        setFocusable(true);
        setHapticFeedbackEnabled(true);
    }

    public static void setDrawableBounds(Drawable drawable, int x, int y) {
        setDrawableBounds(drawable, x, y, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static void setDrawableBounds(Drawable drawable, float x, float y) {
        setDrawableBounds(drawable, (int) x, (int) y, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }

    public static void setDrawableBounds(Drawable drawable, int x, int y, int w, int h) {
        if (drawable != null) {
            drawable.setBounds(x, y, x + w, y + h);
        }
    }

    public static void setDrawableBounds(Drawable drawable, float x, float y, int w, int h) {
        if (drawable != null) {
            drawable.setBounds((int) x, (int) y, ((int) x) + w, ((int) y) + h);
        }
    }

    public void startCheckLongPress() {
        if (this.checkingForLongPress) {
            return;
        }
        this.checkingForLongPress = true;
        if (this.pendingCheckForTap == null) {
            this.pendingCheckForTap = new CheckForTap();
        }
        postDelayed(this.pendingCheckForTap, ViewConfiguration.getTapTimeout());
    }

    public void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        CheckForLongPress checkForLongPress = this.pendingCheckForLongPress;
        if (checkForLongPress != null) {
            removeCallbacks(checkForLongPress);
        }
        CheckForTap checkForTap = this.pendingCheckForTap;
        if (checkForTap != null) {
            removeCallbacks(checkForTap);
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    protected boolean onLongPress() {
        return true;
    }
}
