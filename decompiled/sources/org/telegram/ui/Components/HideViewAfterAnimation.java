package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
/* loaded from: classes5.dex */
public class HideViewAfterAnimation extends AnimatorListenerAdapter {
    private final View view;

    public HideViewAfterAnimation(View view) {
        this.view = view;
    }

    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
    public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        this.view.setVisibility(8);
    }
}
