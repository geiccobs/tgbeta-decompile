package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.UUID;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Rect;
/* loaded from: classes5.dex */
public class EntityView extends FrameLayout {
    private EntityViewDelegate delegate;
    private GestureDetector gestureDetector;
    protected Point position;
    private float previousLocationX;
    private float previousLocationY;
    protected SelectionView selectionView;
    private boolean hasPanned = false;
    private boolean hasReleased = false;
    private boolean hasTransformed = false;
    private boolean announcedSelection = false;
    private boolean recognizedLongPress = false;
    private UUID uuid = UUID.randomUUID();

    /* loaded from: classes5.dex */
    public interface EntityViewDelegate {
        boolean allowInteraction(EntityView entityView);

        int[] getCenterLocation(EntityView entityView);

        float getCropRotation();

        float[] getTransformedTouch(float f, float f2);

        boolean onEntityLongClicked(EntityView entityView);

        boolean onEntitySelected(EntityView entityView);
    }

    public EntityView(Context context, Point pos) {
        super(context);
        this.position = pos;
        this.gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() { // from class: org.telegram.ui.Components.Paint.Views.EntityView.1
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onLongPress(MotionEvent e) {
                if (!EntityView.this.hasPanned && !EntityView.this.hasTransformed && !EntityView.this.hasReleased) {
                    EntityView.this.recognizedLongPress = true;
                    if (EntityView.this.delegate != null) {
                        EntityView.this.performHapticFeedback(0);
                        EntityView.this.delegate.onEntityLongClicked(EntityView.this);
                    }
                }
            }
        });
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Point getPosition() {
        return this.position;
    }

    public void setPosition(Point value) {
        this.position = value;
        updatePosition();
    }

    public float getScale() {
        return getScaleX();
    }

    public void setScale(float scale) {
        setScaleX(scale);
        setScaleY(scale);
    }

    public void setDelegate(EntityViewDelegate entityViewDelegate) {
        this.delegate = entityViewDelegate;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.delegate.allowInteraction(this);
    }

    public boolean onTouchMove(float x, float y) {
        float scale = ((View) getParent()).getScaleX();
        float tx = (x - this.previousLocationX) / scale;
        float ty = (y - this.previousLocationY) / scale;
        float distance = (float) Math.hypot(tx, ty);
        float minDistance = this.hasPanned ? 6.0f : 16.0f;
        if (distance > minDistance) {
            pan(tx, ty);
            this.previousLocationX = x;
            this.previousLocationY = y;
            this.hasPanned = true;
            return true;
        }
        return false;
    }

    public void onTouchUp() {
        EntityViewDelegate entityViewDelegate;
        if (!this.recognizedLongPress && !this.hasPanned && !this.hasTransformed && !this.announcedSelection && (entityViewDelegate = this.delegate) != null) {
            entityViewDelegate.onEntitySelected(this);
        }
        this.recognizedLongPress = false;
        this.hasPanned = false;
        this.hasTransformed = false;
        this.hasReleased = true;
        this.announcedSelection = false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        EntityViewDelegate entityViewDelegate;
        if (event.getPointerCount() > 1 || !this.delegate.allowInteraction(this)) {
            return false;
        }
        float[] xy = this.delegate.getTransformedTouch(event.getRawX(), event.getRawY());
        int action = event.getActionMasked();
        boolean handled = false;
        switch (action) {
            case 0:
            case 5:
                if (!isSelected() && (entityViewDelegate = this.delegate) != null) {
                    entityViewDelegate.onEntitySelected(this);
                    this.announcedSelection = true;
                }
                this.previousLocationX = xy[0];
                this.previousLocationY = xy[1];
                handled = true;
                this.hasReleased = false;
                break;
            case 1:
            case 3:
            case 6:
                onTouchUp();
                handled = true;
                break;
            case 2:
                handled = onTouchMove(xy[0], xy[1]);
                break;
        }
        this.gestureDetector.onTouchEvent(event);
        return handled;
    }

    public void pan(float tx, float ty) {
        this.position.x += tx;
        this.position.y += ty;
        updatePosition();
    }

    public void updatePosition() {
        float halfWidth = getMeasuredWidth() / 2.0f;
        float halfHeight = getMeasuredHeight() / 2.0f;
        setX(this.position.x - halfWidth);
        setY(this.position.y - halfHeight);
        updateSelectionView();
    }

    public void scale(float scale) {
        float newScale = Math.max(getScale() * scale, 0.1f);
        setScale(newScale);
        updateSelectionView();
    }

    public void rotate(float angle) {
        setRotation(angle);
        updateSelectionView();
    }

    protected Rect getSelectionBounds() {
        return new Rect(0.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override // android.view.View
    public boolean isSelected() {
        return this.selectionView != null;
    }

    protected SelectionView createSelectionView() {
        return null;
    }

    public void updateSelectionView() {
        SelectionView selectionView = this.selectionView;
        if (selectionView != null) {
            selectionView.updatePosition();
        }
    }

    public void select(ViewGroup selectionContainer) {
        SelectionView selectionView = createSelectionView();
        this.selectionView = selectionView;
        selectionContainer.addView(selectionView);
        selectionView.updatePosition();
    }

    public void deselect() {
        SelectionView selectionView = this.selectionView;
        if (selectionView == null) {
            return;
        }
        if (selectionView.getParent() != null) {
            ((ViewGroup) this.selectionView.getParent()).removeView(this.selectionView);
        }
        this.selectionView = null;
    }

    public void setSelectionVisibility(boolean visible) {
        SelectionView selectionView = this.selectionView;
        if (selectionView == null) {
            return;
        }
        selectionView.setVisibility(visible ? 0 : 8);
    }

    /* loaded from: classes5.dex */
    public class SelectionView extends FrameLayout {
        public static final int SELECTION_LEFT_HANDLE = 1;
        public static final int SELECTION_RIGHT_HANDLE = 2;
        public static final int SELECTION_WHOLE_HANDLE = 3;
        private int currentHandle;
        protected Paint paint = new Paint(1);
        protected Paint dotPaint = new Paint(1);
        protected Paint dotStrokePaint = new Paint(1);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public SelectionView(Context context) {
            super(context);
            EntityView.this = this$0;
            setWillNotDraw(false);
            this.paint.setColor(-1);
            this.dotPaint.setColor(-12793105);
            this.dotStrokePaint.setColor(-1);
            this.dotStrokePaint.setStyle(Paint.Style.STROKE);
            this.dotStrokePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        }

        protected void updatePosition() {
            Rect bounds = EntityView.this.getSelectionBounds();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
            layoutParams.leftMargin = (int) bounds.x;
            layoutParams.topMargin = (int) bounds.y;
            layoutParams.width = (int) bounds.width;
            layoutParams.height = (int) bounds.height;
            setLayoutParams(layoutParams);
            setRotation(EntityView.this.getRotation());
        }

        protected int pointInsideHandle(float x, float y) {
            return 0;
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            boolean handled;
            boolean handled2;
            int action = event.getActionMasked();
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            float[] xy = EntityView.this.delegate.getTransformedTouch(rawX, rawY);
            float x = xy[0];
            float y = xy[1];
            switch (action) {
                case 0:
                case 5:
                    boolean handled3 = false;
                    int handle = pointInsideHandle(event.getX(), event.getY());
                    if (handle != 0) {
                        this.currentHandle = handle;
                        EntityView.this.previousLocationX = x;
                        EntityView.this.previousLocationY = y;
                        EntityView.this.hasReleased = false;
                        handled3 = true;
                    }
                    handled = handled3;
                    break;
                case 1:
                case 3:
                case 6:
                    EntityView.this.onTouchUp();
                    this.currentHandle = 0;
                    handled = true;
                    break;
                case 2:
                    int i = this.currentHandle;
                    if (i == 3) {
                        boolean handled4 = EntityView.this.onTouchMove(x, y);
                        handled = handled4;
                        break;
                    } else if (i != 0) {
                        float tx = x - EntityView.this.previousLocationX;
                        float ty = y - EntityView.this.previousLocationY;
                        if (EntityView.this.hasTransformed || Math.abs(tx) > AndroidUtilities.dp(2.0f) || Math.abs(ty) > AndroidUtilities.dp(2.0f)) {
                            EntityView.this.hasTransformed = true;
                            float radAngle = (float) Math.toRadians(getRotation());
                            double d = tx;
                            double cos = Math.cos(radAngle);
                            Double.isNaN(d);
                            double d2 = d * cos;
                            double d3 = ty;
                            double sin = Math.sin(radAngle);
                            Double.isNaN(d3);
                            float delta = (float) (d2 + (d3 * sin));
                            if (this.currentHandle == 1) {
                                delta *= -1.0f;
                            }
                            float scaleDelta = ((delta * 2.0f) / getMeasuredWidth()) + 1.0f;
                            EntityView.this.scale(scaleDelta);
                            int[] pos = EntityView.this.delegate.getCenterLocation(EntityView.this);
                            float angle = 0.0f;
                            int i2 = this.currentHandle;
                            if (i2 == 1) {
                                angle = (float) Math.atan2(pos[1] - rawY, pos[0] - rawX);
                            } else if (i2 == 2) {
                                angle = (float) Math.atan2(rawY - pos[1], rawX - pos[0]);
                            }
                            EntityView.this.rotate(((float) Math.toDegrees(angle)) - EntityView.this.delegate.getCropRotation());
                            EntityView.this.previousLocationX = x;
                            EntityView.this.previousLocationY = y;
                        }
                        handled = true;
                        break;
                    } else {
                        handled2 = false;
                        handled = handled2;
                        break;
                    }
                case 4:
                default:
                    handled2 = false;
                    handled = handled2;
                    break;
            }
            if (this.currentHandle == 3) {
                EntityView.this.gestureDetector.onTouchEvent(event);
            }
            return handled;
        }
    }
}
