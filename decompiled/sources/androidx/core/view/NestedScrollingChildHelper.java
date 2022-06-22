package androidx.core.view;

import android.view.View;
import android.view.ViewParent;
/* loaded from: classes.dex */
public class NestedScrollingChildHelper {
    private boolean mIsNestedScrollingEnabled;
    private ViewParent mNestedScrollingParentNonTouch;
    private ViewParent mNestedScrollingParentTouch;
    private int[] mTempNestedScrollConsumed;
    private final View mView;

    public NestedScrollingChildHelper(View view) {
        this.mView = view;
    }

    public void setNestedScrollingEnabled(boolean enabled) {
        if (this.mIsNestedScrollingEnabled) {
            ViewCompat.stopNestedScroll(this.mView);
        }
        this.mIsNestedScrollingEnabled = enabled;
    }

    public boolean isNestedScrollingEnabled() {
        return this.mIsNestedScrollingEnabled;
    }

    public boolean hasNestedScrollingParent() {
        return hasNestedScrollingParent(0);
    }

    public boolean hasNestedScrollingParent(int type) {
        return getNestedScrollingParentForType(type) != null;
    }

    public boolean startNestedScroll(int axes) {
        return startNestedScroll(axes, 0);
    }

    public boolean startNestedScroll(int axes, int type) {
        if (hasNestedScrollingParent(type)) {
            return true;
        }
        if (!isNestedScrollingEnabled()) {
            return false;
        }
        View view = this.mView;
        for (ViewParent parent = this.mView.getParent(); parent != null; parent = parent.getParent()) {
            if (ViewParentCompat.onStartNestedScroll(parent, view, this.mView, axes, type)) {
                setNestedScrollingParentForType(type, parent);
                ViewParentCompat.onNestedScrollAccepted(parent, view, this.mView, axes, type);
                return true;
            }
            if (parent instanceof View) {
                view = (View) parent;
            }
        }
        return false;
    }

    public void stopNestedScroll() {
        stopNestedScroll(0);
    }

    public void stopNestedScroll(int type) {
        ViewParent nestedScrollingParentForType = getNestedScrollingParentForType(type);
        if (nestedScrollingParentForType != null) {
            ViewParentCompat.onStopNestedScroll(nestedScrollingParentForType, this.mView, type);
            setNestedScrollingParentForType(type, null);
        }
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return dispatchNestedScrollInternal(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, 0, null);
    }

    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type, int[] consumed) {
        dispatchNestedScrollInternal(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
    }

    private boolean dispatchNestedScrollInternal(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow, int type, int[] consumed) {
        ViewParent nestedScrollingParentForType;
        int i;
        int i2;
        int[] iArr;
        if (!isNestedScrollingEnabled() || (nestedScrollingParentForType = getNestedScrollingParentForType(type)) == null) {
            return false;
        }
        if (dxConsumed == 0 && dyConsumed == 0 && dxUnconsumed == 0 && dyUnconsumed == 0) {
            if (offsetInWindow != null) {
                offsetInWindow[0] = 0;
                offsetInWindow[1] = 0;
            }
            return false;
        }
        if (offsetInWindow != null) {
            this.mView.getLocationInWindow(offsetInWindow);
            i2 = offsetInWindow[0];
            i = offsetInWindow[1];
        } else {
            i2 = 0;
            i = 0;
        }
        if (consumed == null) {
            int[] tempNestedScrollConsumed = getTempNestedScrollConsumed();
            tempNestedScrollConsumed[0] = 0;
            tempNestedScrollConsumed[1] = 0;
            iArr = tempNestedScrollConsumed;
        } else {
            iArr = consumed;
        }
        ViewParentCompat.onNestedScroll(nestedScrollingParentForType, this.mView, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, iArr);
        if (offsetInWindow != null) {
            this.mView.getLocationInWindow(offsetInWindow);
            offsetInWindow[0] = offsetInWindow[0] - i2;
            offsetInWindow[1] = offsetInWindow[1] - i;
        }
        return true;
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, 0);
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        ViewParent nestedScrollingParentForType;
        int i;
        int i2;
        if (!isNestedScrollingEnabled() || (nestedScrollingParentForType = getNestedScrollingParentForType(type)) == null) {
            return false;
        }
        if (dx == 0 && dy == 0) {
            if (offsetInWindow == null) {
                return false;
            }
            offsetInWindow[0] = 0;
            offsetInWindow[1] = 0;
            return false;
        }
        if (offsetInWindow != null) {
            this.mView.getLocationInWindow(offsetInWindow);
            i2 = offsetInWindow[0];
            i = offsetInWindow[1];
        } else {
            i2 = 0;
            i = 0;
        }
        if (consumed == null) {
            consumed = getTempNestedScrollConsumed();
        }
        consumed[0] = 0;
        consumed[1] = 0;
        ViewParentCompat.onNestedPreScroll(nestedScrollingParentForType, this.mView, dx, dy, consumed, type);
        if (offsetInWindow != null) {
            this.mView.getLocationInWindow(offsetInWindow);
            offsetInWindow[0] = offsetInWindow[0] - i2;
            offsetInWindow[1] = offsetInWindow[1] - i;
        }
        return (consumed[0] == 0 && consumed[1] == 0) ? false : true;
    }

    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        ViewParent nestedScrollingParentForType;
        if (!isNestedScrollingEnabled() || (nestedScrollingParentForType = getNestedScrollingParentForType(0)) == null) {
            return false;
        }
        return ViewParentCompat.onNestedFling(nestedScrollingParentForType, this.mView, velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        ViewParent nestedScrollingParentForType;
        if (!isNestedScrollingEnabled() || (nestedScrollingParentForType = getNestedScrollingParentForType(0)) == null) {
            return false;
        }
        return ViewParentCompat.onNestedPreFling(nestedScrollingParentForType, this.mView, velocityX, velocityY);
    }

    private ViewParent getNestedScrollingParentForType(int type) {
        if (type != 0) {
            if (type == 1) {
                return this.mNestedScrollingParentNonTouch;
            }
            return null;
        }
        return this.mNestedScrollingParentTouch;
    }

    private void setNestedScrollingParentForType(int type, ViewParent p) {
        if (type == 0) {
            this.mNestedScrollingParentTouch = p;
        } else if (type != 1) {
        } else {
            this.mNestedScrollingParentNonTouch = p;
        }
    }

    private int[] getTempNestedScrollConsumed() {
        if (this.mTempNestedScrollConsumed == null) {
            this.mTempNestedScrollConsumed = new int[2];
        }
        return this.mTempNestedScrollConsumed;
    }
}
