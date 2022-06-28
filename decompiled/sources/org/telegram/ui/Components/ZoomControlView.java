package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.Components.AnimationProperties;
/* loaded from: classes5.dex */
public class ZoomControlView extends View {
    public final Property<ZoomControlView, Float> ZOOM_PROPERTY = new AnimationProperties.FloatProperty<ZoomControlView>("clipProgress") { // from class: org.telegram.ui.Components.ZoomControlView.1
        public void setValue(ZoomControlView object, float value) {
            ZoomControlView.this.zoom = value;
            if (ZoomControlView.this.delegate != null) {
                ZoomControlView.this.delegate.didSetZoom(ZoomControlView.this.zoom);
            }
            ZoomControlView.this.invalidate();
        }

        public Float get(ZoomControlView object) {
            return Float.valueOf(ZoomControlView.this.zoom);
        }
    };
    private float animatingToZoom;
    private AnimatorSet animatorSet;
    private ZoomControlViewDelegate delegate;
    private Drawable filledProgressDrawable;
    private Drawable knobDrawable;
    private boolean knobPressed;
    private float knobStartX;
    private float knobStartY;
    private int minusCx;
    private int minusCy;
    private Drawable minusDrawable;
    private int plusCx;
    private int plusCy;
    private Drawable plusDrawable;
    private boolean pressed;
    private Drawable pressedKnobDrawable;
    private Drawable progressDrawable;
    private int progressEndX;
    private int progressEndY;
    private int progressStartX;
    private int progressStartY;
    private float zoom;

    /* loaded from: classes5.dex */
    public interface ZoomControlViewDelegate {
        void didSetZoom(float f);
    }

    public ZoomControlView(Context context) {
        super(context);
        this.minusDrawable = context.getResources().getDrawable(R.drawable.zoom_minus);
        this.plusDrawable = context.getResources().getDrawable(R.drawable.zoom_plus);
        this.progressDrawable = context.getResources().getDrawable(R.drawable.zoom_slide);
        this.filledProgressDrawable = context.getResources().getDrawable(R.drawable.zoom_slide_a);
        this.knobDrawable = context.getResources().getDrawable(R.drawable.zoom_round);
        this.pressedKnobDrawable = context.getResources().getDrawable(R.drawable.zoom_round_b);
    }

    public float getZoom() {
        if (this.animatorSet != null) {
            return this.animatingToZoom;
        }
        return this.zoom;
    }

    public void setZoom(float value, boolean notify) {
        ZoomControlViewDelegate zoomControlViewDelegate;
        if (value == this.zoom) {
            return;
        }
        if (value < 0.0f) {
            value = 0.0f;
        } else if (value > 1.0f) {
            value = 1.0f;
        }
        this.zoom = value;
        if (notify && (zoomControlViewDelegate = this.delegate) != null) {
            zoomControlViewDelegate.didSetZoom(value);
        }
        invalidate();
    }

    public void setDelegate(ZoomControlViewDelegate zoomControlViewDelegate) {
        this.delegate = zoomControlViewDelegate;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        boolean handled = false;
        boolean isPortrait = getMeasuredWidth() > getMeasuredHeight();
        int i = this.progressStartX;
        int i2 = this.progressEndX;
        float f = this.zoom;
        int knobX = (int) (i + ((i2 - i) * f));
        int i3 = this.progressStartY;
        int i4 = this.progressEndY;
        int knobY = (int) (i3 + ((i4 - i3) * f));
        if (action == 1 || action == 0) {
            if (x >= knobX - AndroidUtilities.dp(20.0f) && x <= AndroidUtilities.dp(20.0f) + knobX && y >= knobY - AndroidUtilities.dp(25.0f) && y <= AndroidUtilities.dp(25.0f) + knobY) {
                if (action == 0) {
                    this.knobPressed = true;
                    this.knobStartX = x - knobX;
                    this.knobStartY = y - knobY;
                    invalidate();
                }
                handled = true;
            } else if (x >= this.minusCx - AndroidUtilities.dp(16.0f) && x <= this.minusCx + AndroidUtilities.dp(16.0f) && y >= this.minusCy - AndroidUtilities.dp(16.0f) && y <= this.minusCy + AndroidUtilities.dp(16.0f)) {
                if (action == 1 && animateToZoom((((float) Math.floor(getZoom() / 0.25f)) * 0.25f) - 0.25f)) {
                    performHapticFeedback(3);
                } else {
                    this.pressed = true;
                }
                handled = true;
            } else if (x >= this.plusCx - AndroidUtilities.dp(16.0f) && x <= this.plusCx + AndroidUtilities.dp(16.0f) && y >= this.plusCy - AndroidUtilities.dp(16.0f) && y <= this.plusCy + AndroidUtilities.dp(16.0f)) {
                if (action == 1 && animateToZoom((((float) Math.floor(getZoom() / 0.25f)) * 0.25f) + 0.25f)) {
                    performHapticFeedback(3);
                } else {
                    this.pressed = true;
                }
                handled = true;
            } else if (isPortrait) {
                if (x >= this.progressStartX && x <= this.progressEndX) {
                    if (action != 0) {
                        if (Math.abs(this.knobStartX - x) <= AndroidUtilities.dp(10.0f)) {
                            int i5 = this.progressStartX;
                            float f2 = (x - i5) / (this.progressEndX - i5);
                            this.zoom = f2;
                            ZoomControlViewDelegate zoomControlViewDelegate = this.delegate;
                            if (zoomControlViewDelegate != null) {
                                zoomControlViewDelegate.didSetZoom(f2);
                            }
                            invalidate();
                        }
                    } else {
                        this.knobStartX = x;
                        this.pressed = true;
                    }
                    handled = true;
                }
            } else if (y >= this.progressStartY && y <= this.progressEndY) {
                if (action != 1) {
                    if (Math.abs(this.knobStartY - y) <= AndroidUtilities.dp(10.0f)) {
                        int i6 = this.progressStartY;
                        float f3 = (y - i6) / (this.progressEndY - i6);
                        this.zoom = f3;
                        ZoomControlViewDelegate zoomControlViewDelegate2 = this.delegate;
                        if (zoomControlViewDelegate2 != null) {
                            zoomControlViewDelegate2.didSetZoom(f3);
                        }
                        invalidate();
                    }
                } else {
                    this.knobStartY = y;
                    this.pressed = true;
                }
                handled = true;
            }
        } else if (action == 2 && this.knobPressed) {
            if (isPortrait) {
                this.zoom = ((this.knobStartX + x) - i) / (i2 - i);
            } else {
                this.zoom = ((this.knobStartY + y) - i3) / (i4 - i3);
            }
            float f4 = this.zoom;
            if (f4 < 0.0f) {
                this.zoom = 0.0f;
            } else if (f4 > 1.0f) {
                this.zoom = 1.0f;
            }
            ZoomControlViewDelegate zoomControlViewDelegate3 = this.delegate;
            if (zoomControlViewDelegate3 != null) {
                zoomControlViewDelegate3.didSetZoom(this.zoom);
            }
            invalidate();
        }
        if (action == 1) {
            this.pressed = false;
            this.knobPressed = false;
            invalidate();
        }
        return handled || this.pressed || this.knobPressed || super.onTouchEvent(event);
    }

    private boolean animateToZoom(float zoom) {
        if (zoom < 0.0f || zoom > 1.0f) {
            return false;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.animatingToZoom = zoom;
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.animatorSet = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this, this.ZOOM_PROPERTY, zoom));
        this.animatorSet.setDuration(180L);
        this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ZoomControlView.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ZoomControlView.this.animatorSet = null;
            }
        });
        this.animatorSet.start();
        return true;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int cx = getMeasuredWidth() / 2;
        int cy = getMeasuredHeight() / 2;
        boolean isPortrait = getMeasuredWidth() > getMeasuredHeight();
        if (isPortrait) {
            this.minusCx = AndroidUtilities.dp(41.0f);
            this.minusCy = cy;
            this.plusCx = getMeasuredWidth() - AndroidUtilities.dp(41.0f);
            this.plusCy = cy;
            this.progressStartX = this.minusCx + AndroidUtilities.dp(18.0f);
            this.progressStartY = cy;
            this.progressEndX = this.plusCx - AndroidUtilities.dp(18.0f);
            this.progressEndY = cy;
        } else {
            this.minusCx = cx;
            this.minusCy = AndroidUtilities.dp(41.0f);
            this.plusCx = cx;
            this.plusCy = getMeasuredHeight() - AndroidUtilities.dp(41.0f);
            this.progressStartX = cx;
            this.progressStartY = this.minusCy + AndroidUtilities.dp(18.0f);
            this.progressEndX = cx;
            this.progressEndY = this.plusCy - AndroidUtilities.dp(18.0f);
        }
        this.minusDrawable.setBounds(this.minusCx - AndroidUtilities.dp(7.0f), this.minusCy - AndroidUtilities.dp(7.0f), this.minusCx + AndroidUtilities.dp(7.0f), this.minusCy + AndroidUtilities.dp(7.0f));
        this.minusDrawable.draw(canvas);
        this.plusDrawable.setBounds(this.plusCx - AndroidUtilities.dp(7.0f), this.plusCy - AndroidUtilities.dp(7.0f), this.plusCx + AndroidUtilities.dp(7.0f), this.plusCy + AndroidUtilities.dp(7.0f));
        this.plusDrawable.draw(canvas);
        int i = this.progressEndX;
        int i2 = this.progressStartX;
        int totalX = i - i2;
        int i3 = this.progressEndY;
        int i4 = this.progressStartY;
        int totalY = i3 - i4;
        float f = this.zoom;
        int knobX = (int) (i2 + (totalX * f));
        int knobY = (int) (i4 + (totalY * f));
        if (isPortrait) {
            this.progressDrawable.setBounds(i2, i4 - AndroidUtilities.dp(3.0f), this.progressEndX, this.progressStartY + AndroidUtilities.dp(3.0f));
            this.filledProgressDrawable.setBounds(this.progressStartX, this.progressStartY - AndroidUtilities.dp(3.0f), knobX, this.progressStartY + AndroidUtilities.dp(3.0f));
        } else {
            this.progressDrawable.setBounds(i4, 0, i3, AndroidUtilities.dp(6.0f));
            this.filledProgressDrawable.setBounds(this.progressStartY, 0, knobY, AndroidUtilities.dp(6.0f));
            canvas.save();
            canvas.rotate(90.0f);
            canvas.translate(0.0f, (-this.progressStartX) - AndroidUtilities.dp(3.0f));
        }
        this.progressDrawable.draw(canvas);
        this.filledProgressDrawable.draw(canvas);
        if (!isPortrait) {
            canvas.restore();
        }
        Drawable drawable = this.knobPressed ? this.pressedKnobDrawable : this.knobDrawable;
        int size = drawable.getIntrinsicWidth();
        drawable.setBounds(knobX - (size / 2), knobY - (size / 2), (size / 2) + knobX, (size / 2) + knobY);
        drawable.draw(canvas);
    }
}
