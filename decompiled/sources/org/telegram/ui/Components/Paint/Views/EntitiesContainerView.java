package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.ui.Components.Paint.Views.RotationGestureDetector;
/* loaded from: classes5.dex */
public class EntitiesContainerView extends FrameLayout implements ScaleGestureDetector.OnScaleGestureListener, RotationGestureDetector.OnRotationGestureListener {
    private EntitiesContainerViewDelegate delegate;
    private ScaleGestureDetector gestureDetector;
    private boolean hasTransformed;
    private float previousAngle;
    private float previousScale = 1.0f;
    private RotationGestureDetector rotationGestureDetector = new RotationGestureDetector(this);

    /* loaded from: classes5.dex */
    public interface EntitiesContainerViewDelegate {
        void onEntityDeselect();

        EntityView onSelectedEntityRequest();

        boolean shouldReceiveTouches();
    }

    public EntitiesContainerView(Context context, EntitiesContainerViewDelegate entitiesContainerViewDelegate) {
        super(context);
        this.gestureDetector = new ScaleGestureDetector(context, this);
        this.delegate = entitiesContainerViewDelegate;
    }

    public int entitiesCount() {
        int count = 0;
        for (int index = 0; index < getChildCount(); index++) {
            View view = getChildAt(index);
            if (view instanceof EntityView) {
                count++;
            }
        }
        return count;
    }

    public void bringViewToFront(EntityView view) {
        if (indexOfChild(view) != getChildCount() - 1) {
            removeView(view);
            addView(view, getChildCount());
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return ev.getPointerCount() == 2 && this.delegate.shouldReceiveTouches();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        EntitiesContainerViewDelegate entitiesContainerViewDelegate;
        EntityView selectedEntity = this.delegate.onSelectedEntityRequest();
        if (selectedEntity == null) {
            return false;
        }
        if (event.getPointerCount() == 1) {
            int action = event.getActionMasked();
            if (action == 0) {
                this.hasTransformed = false;
            } else if (action == 1 || action == 2) {
                if (!this.hasTransformed && (entitiesContainerViewDelegate = this.delegate) != null) {
                    entitiesContainerViewDelegate.onEntityDeselect();
                }
                return false;
            }
        }
        this.gestureDetector.onTouchEvent(event);
        this.rotationGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
    public boolean onScale(ScaleGestureDetector detector) {
        float sf = detector.getScaleFactor();
        float newScale = sf / this.previousScale;
        EntityView view = this.delegate.onSelectedEntityRequest();
        view.scale(newScale);
        this.previousScale = sf;
        return false;
    }

    @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        this.previousScale = 1.0f;
        this.hasTransformed = true;
        return true;
    }

    @Override // android.view.ScaleGestureDetector.OnScaleGestureListener
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    @Override // org.telegram.ui.Components.Paint.Views.RotationGestureDetector.OnRotationGestureListener
    public void onRotationBegin(RotationGestureDetector rotationDetector) {
        this.previousAngle = rotationDetector.getStartAngle();
        this.hasTransformed = true;
    }

    @Override // org.telegram.ui.Components.Paint.Views.RotationGestureDetector.OnRotationGestureListener
    public void onRotation(RotationGestureDetector rotationDetector) {
        EntityView view = this.delegate.onSelectedEntityRequest();
        float angle = rotationDetector.getAngle();
        float delta = this.previousAngle - angle;
        view.rotate(view.getRotation() + delta);
        this.previousAngle = angle;
    }

    @Override // org.telegram.ui.Components.Paint.Views.RotationGestureDetector.OnRotationGestureListener
    public void onRotationEnd(RotationGestureDetector rotationDetector) {
    }

    @Override // android.view.ViewGroup
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if (child instanceof TextPaintView) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
            int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin + widthUsed, lp.width);
            child.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, 0));
            return;
        }
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }
}
