package org.telegram.ui.Components;

import android.view.View;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import org.telegram.messenger.AndroidUtilities;
/* loaded from: classes5.dex */
public final class VerticalPositionAutoAnimator {
    private final AnimatorLayoutChangeListener animatorLayoutChangeListener;
    private SpringAnimation floatingButtonAnimator;
    private View floatingButtonView;
    private float offsetY;

    public static VerticalPositionAutoAnimator attach(View floatingButtonView) {
        return attach(floatingButtonView, 350.0f);
    }

    public static VerticalPositionAutoAnimator attach(View floatingButtonView, float springStiffness) {
        return new VerticalPositionAutoAnimator(floatingButtonView, springStiffness);
    }

    public void addUpdateListener(DynamicAnimation.OnAnimationUpdateListener onAnimationUpdateListener) {
        this.floatingButtonAnimator.addUpdateListener(onAnimationUpdateListener);
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
        if (this.floatingButtonAnimator.isRunning()) {
            this.floatingButtonAnimator.getSpring().setFinalPosition(offsetY);
        } else {
            this.floatingButtonView.setTranslationY(offsetY);
        }
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    private VerticalPositionAutoAnimator(View floatingButtonView, float springStiffness) {
        this.floatingButtonView = floatingButtonView;
        AnimatorLayoutChangeListener animatorLayoutChangeListener = new AnimatorLayoutChangeListener(floatingButtonView, springStiffness);
        this.animatorLayoutChangeListener = animatorLayoutChangeListener;
        floatingButtonView.addOnLayoutChangeListener(animatorLayoutChangeListener);
    }

    public void ignoreNextLayout() {
        this.animatorLayoutChangeListener.ignoreNextLayout = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class AnimatorLayoutChangeListener implements View.OnLayoutChangeListener {
        private boolean ignoreNextLayout;
        private Boolean orientation;

        public AnimatorLayoutChangeListener(View view, float springStiffness) {
            VerticalPositionAutoAnimator.this = r4;
            r4.floatingButtonAnimator = new SpringAnimation(view, DynamicAnimation.TRANSLATION_Y, r4.offsetY);
            r4.floatingButtonAnimator.getSpring().setDampingRatio(1.0f);
            r4.floatingButtonAnimator.getSpring().setStiffness(springStiffness);
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            checkOrientation();
            if (oldTop != 0 && oldTop != top && !this.ignoreNextLayout) {
                VerticalPositionAutoAnimator.this.floatingButtonAnimator.cancel();
                if (v.getVisibility() != 0) {
                    v.setTranslationY(VerticalPositionAutoAnimator.this.offsetY);
                    return;
                }
                VerticalPositionAutoAnimator.this.floatingButtonAnimator.getSpring().setFinalPosition(VerticalPositionAutoAnimator.this.offsetY);
                v.setTranslationY((oldTop - top) + VerticalPositionAutoAnimator.this.offsetY);
                VerticalPositionAutoAnimator.this.floatingButtonAnimator.start();
                return;
            }
            this.ignoreNextLayout = false;
        }

        private void checkOrientation() {
            boolean orientation = AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y;
            Boolean bool = this.orientation;
            if (bool == null || bool.booleanValue() != orientation) {
                this.orientation = Boolean.valueOf(orientation);
                this.ignoreNextLayout = true;
            }
        }
    }
}
