package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.VoIPNotificationsLayout;
/* loaded from: classes5.dex */
public class VoIPNotificationsLayout extends LinearLayout {
    boolean lockAnimation;
    Runnable onViewsUpdated;
    TransitionSet transitionSet;
    boolean wasChanged;
    HashMap<String, NotificationView> viewsByTag = new HashMap<>();
    ArrayList<NotificationView> viewToAdd = new ArrayList<>();
    ArrayList<NotificationView> viewToRemove = new ArrayList<>();

    /* JADX WARN: Type inference failed for: r1v5, types: [org.telegram.ui.Components.voip.VoIPNotificationsLayout$1] */
    public VoIPNotificationsLayout(Context context) {
        super(context);
        setOrientation(1);
        if (Build.VERSION.SDK_INT >= 19) {
            TransitionSet transitionSet = new TransitionSet();
            this.transitionSet = transitionSet;
            transitionSet.addTransition(new Fade(2).setDuration(150L)).addTransition(new ChangeBounds().setDuration(200L)).addTransition(new Visibility() { // from class: org.telegram.ui.Components.voip.VoIPNotificationsLayout.1
                @Override // android.transition.Visibility
                public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
                    AnimatorSet set = new AnimatorSet();
                    view.setAlpha(0.0f);
                    set.playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, view.getMeasuredHeight(), 0.0f));
                    set.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    return set;
                }
            }.setDuration(200L));
            this.transitionSet.setOrdering(0);
        }
    }

    public void addNotification(int iconRes, String text, String tag, boolean animated) {
        if (this.viewsByTag.get(tag) != null) {
            return;
        }
        NotificationView view = new NotificationView(getContext());
        view.tag = tag;
        view.iconView.setImageResource(iconRes);
        view.textView.setText(text);
        this.viewsByTag.put(tag, view);
        if (animated) {
            view.startAnimation();
        }
        if (this.lockAnimation) {
            this.viewToAdd.add(view);
            return;
        }
        this.wasChanged = true;
        addView(view, LayoutHelper.createLinear(-2, -2, 1, 4, 0, 0, 4));
    }

    public void removeNotification(String tag) {
        NotificationView view = this.viewsByTag.remove(tag);
        if (view != null) {
            if (this.lockAnimation) {
                if (this.viewToAdd.remove(view)) {
                    return;
                }
                this.viewToRemove.add(view);
                return;
            }
            this.wasChanged = true;
            removeView(view);
        }
    }

    private void lock() {
        this.lockAnimation = true;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPNotificationsLayout$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VoIPNotificationsLayout.this.m3260x22e335d6();
            }
        }, 700L);
    }

    /* renamed from: lambda$lock$0$org-telegram-ui-Components-voip-VoIPNotificationsLayout */
    public /* synthetic */ void m3260x22e335d6() {
        this.lockAnimation = false;
        runDelayed();
    }

    private void runDelayed() {
        if (this.viewToAdd.isEmpty() && this.viewToRemove.isEmpty()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            ViewParent parent = getParent();
            if (parent != null) {
                TransitionManager.beginDelayedTransition(this, this.transitionSet);
            }
        }
        int i = 0;
        while (i < this.viewToAdd.size()) {
            NotificationView view = this.viewToAdd.get(i);
            int j = 0;
            while (true) {
                if (j < this.viewToRemove.size()) {
                    if (!view.tag.equals(this.viewToRemove.get(j).tag)) {
                        j++;
                    } else {
                        this.viewToAdd.remove(i);
                        this.viewToRemove.remove(j);
                        i--;
                        break;
                    }
                } else {
                    break;
                }
            }
            i++;
        }
        for (int i2 = 0; i2 < this.viewToAdd.size(); i2++) {
            addView(this.viewToAdd.get(i2), LayoutHelper.createLinear(-2, -2, 1, 4, 0, 0, 4));
        }
        for (int i3 = 0; i3 < this.viewToRemove.size(); i3++) {
            removeView(this.viewToRemove.get(i3));
        }
        this.viewsByTag.clear();
        for (int i4 = 0; i4 < getChildCount(); i4++) {
            NotificationView v = (NotificationView) getChildAt(i4);
            this.viewsByTag.put(v.tag, v);
        }
        this.viewToAdd.clear();
        this.viewToRemove.clear();
        lock();
        Runnable runnable = this.onViewsUpdated;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void beforeLayoutChanges() {
        this.wasChanged = false;
        if (!this.lockAnimation && Build.VERSION.SDK_INT >= 19) {
            ViewParent parent = getParent();
            if (parent != null) {
                TransitionManager.beginDelayedTransition(this, this.transitionSet);
            }
        }
    }

    public void animateLayoutChanges() {
        if (this.wasChanged) {
            lock();
        }
        this.wasChanged = false;
    }

    public int getChildsHight() {
        int n = getChildCount();
        return (n > 0 ? AndroidUtilities.dp(16.0f) : 0) + (AndroidUtilities.dp(32.0f) * n);
    }

    /* loaded from: classes5.dex */
    public static class NotificationView extends FrameLayout {
        ImageView iconView;
        public String tag;
        TextView textView;

        public NotificationView(Context context) {
            super(context);
            setFocusable(true);
            setFocusableInTouchMode(true);
            this.iconView = new ImageView(context);
            setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(16.0f), ColorUtils.setAlphaComponent(-16777216, 102)));
            addView(this.iconView, LayoutHelper.createFrame(24, 24.0f, 0, 10.0f, 4.0f, 10.0f, 4.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(-1);
            this.textView.setTextSize(1, 14.0f);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 16, 44.0f, 4.0f, 16.0f, 4.0f));
        }

        public void startAnimation() {
            this.textView.setVisibility(8);
            postDelayed(new Runnable() { // from class: org.telegram.ui.Components.voip.VoIPNotificationsLayout$NotificationView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    VoIPNotificationsLayout.NotificationView.this.m3261x4c597b27();
                }
            }, 400L);
        }

        /* renamed from: lambda$startAnimation$0$org-telegram-ui-Components-voip-VoIPNotificationsLayout$NotificationView */
        public /* synthetic */ void m3261x4c597b27() {
            if (Build.VERSION.SDK_INT >= 19) {
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.addTransition(new Fade(1).setDuration(150L)).addTransition(new ChangeBounds().setDuration(200L));
                transitionSet.setOrdering(0);
                ViewParent parent = getParent();
                if (parent != null) {
                    TransitionManager.beginDelayedTransition((ViewGroup) parent, transitionSet);
                }
            }
            this.textView.setVisibility(0);
        }
    }

    public void setOnViewsUpdated(Runnable onViewsUpdated) {
        this.onViewsUpdated = onViewsUpdated;
    }
}
