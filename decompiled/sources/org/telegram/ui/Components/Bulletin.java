package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Property;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.util.Consumer;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.DialogsActivity;
/* loaded from: classes5.dex */
public class Bulletin {
    public static final int DURATION_LONG = 2750;
    public static final int DURATION_SHORT = 1500;
    public static final int TYPE_APP_ICON = 5;
    public static final int TYPE_BIO_CHANGED = 2;
    public static final int TYPE_ERROR = 1;
    public static final int TYPE_ERROR_SUBTITLE = 4;
    public static final int TYPE_NAME_CHANGED = 3;
    public static final int TYPE_STICKER = 0;
    private static final HashMap<FrameLayout, Delegate> delegates = new HashMap<>();
    private static Bulletin visibleBulletin;
    private boolean canHide;
    private final FrameLayout containerLayout;
    public int currentBottomOffset;
    private Delegate currentDelegate;
    private int duration;
    public int hash;
    private final Runnable hideRunnable;
    private final Layout layout;
    private Layout.Transition layoutTransition;
    private final ParentLayout parentLayout;
    private boolean showing;
    public int tag;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface GravityDef {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface WidthDef {
    }

    public static Bulletin make(FrameLayout containerLayout, Layout contentLayout, int duration) {
        return new Bulletin(containerLayout, contentLayout, duration);
    }

    public static Bulletin make(BaseFragment fragment, Layout contentLayout, int duration) {
        if (!(fragment instanceof ChatActivity)) {
            if (fragment instanceof DialogsActivity) {
                contentLayout.setWideScreenParams(-1, 0);
            }
        } else {
            contentLayout.setWideScreenParams(-2, 5);
        }
        return new Bulletin(fragment.getLayoutContainer(), contentLayout, duration);
    }

    public static Bulletin find(FrameLayout containerLayout) {
        int size = containerLayout.getChildCount();
        for (int i = 0; i < size; i++) {
            View view = containerLayout.getChildAt(i);
            if (view instanceof Layout) {
                return ((Layout) view).bulletin;
            }
        }
        return null;
    }

    public static void hide(FrameLayout containerLayout) {
        hide(containerLayout, true);
    }

    public static void hide(FrameLayout containerLayout, boolean animated) {
        Bulletin bulletin = find(containerLayout);
        if (bulletin != null) {
            bulletin.hide(animated && isTransitionsEnabled(), 0L);
        }
    }

    private Bulletin() {
        this.hideRunnable = new Runnable() { // from class: org.telegram.ui.Components.Bulletin$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                Bulletin.this.hide();
            }
        };
        this.layout = null;
        this.parentLayout = null;
        this.containerLayout = null;
    }

    private Bulletin(final FrameLayout containerLayout, Layout layout, int duration) {
        this.hideRunnable = new Runnable() { // from class: org.telegram.ui.Components.Bulletin$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                Bulletin.this.hide();
            }
        };
        this.layout = layout;
        this.parentLayout = new ParentLayout(layout) { // from class: org.telegram.ui.Components.Bulletin.1
            {
                Bulletin.this = this;
            }

            @Override // org.telegram.ui.Components.Bulletin.ParentLayout
            protected void onPressedStateChanged(boolean pressed) {
                Bulletin.this.setCanHide(!pressed);
                if (containerLayout.getParent() != null) {
                    containerLayout.getParent().requestDisallowInterceptTouchEvent(pressed);
                }
            }

            @Override // org.telegram.ui.Components.Bulletin.ParentLayout
            protected void onHide() {
                Bulletin.this.hide();
            }
        };
        this.containerLayout = containerLayout;
        this.duration = duration;
    }

    public static Bulletin getVisibleBulletin() {
        return visibleBulletin;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Bulletin show() {
        if (!this.showing && this.containerLayout != null) {
            this.showing = true;
            CharSequence text = this.layout.getAccessibilityText();
            if (text != null) {
                AndroidUtilities.makeAccessibilityAnnouncement(text);
            }
            if (this.layout.getParent() != this.parentLayout) {
                throw new IllegalStateException("Layout has incorrect parent");
            }
            Bulletin bulletin = visibleBulletin;
            if (bulletin != null) {
                bulletin.hide();
            }
            visibleBulletin = this;
            this.layout.onAttach(this);
            this.layout.addOnLayoutChangeListener(new AnonymousClass2());
            this.layout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: org.telegram.ui.Components.Bulletin.3
                {
                    Bulletin.this = this;
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View v) {
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View v) {
                    Bulletin.this.layout.removeOnAttachStateChangeListener(this);
                    Bulletin.this.hide(false, 0L);
                }
            });
            this.containerLayout.addView(this.parentLayout);
        }
        return this;
    }

    /* renamed from: org.telegram.ui.Components.Bulletin$2 */
    /* loaded from: classes5.dex */
    public class AnonymousClass2 implements View.OnLayoutChangeListener {
        AnonymousClass2() {
            Bulletin.this = this$0;
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
            Bulletin.this.layout.removeOnLayoutChangeListener(this);
            if (Bulletin.this.showing) {
                Bulletin.this.layout.onShow();
                Bulletin.this.currentDelegate = (Delegate) Bulletin.delegates.get(Bulletin.this.containerLayout);
                Bulletin bulletin = Bulletin.this;
                bulletin.currentBottomOffset = bulletin.currentDelegate != null ? Bulletin.this.currentDelegate.getBottomOffset(Bulletin.this.tag) : 0;
                if (Bulletin.this.currentDelegate != null) {
                    Bulletin.this.currentDelegate.onShow(Bulletin.this);
                }
                if (Bulletin.isTransitionsEnabled()) {
                    Bulletin.this.ensureLayoutTransitionCreated();
                    Bulletin.this.layout.transitionRunning = true;
                    Bulletin.this.layout.delegate = Bulletin.this.currentDelegate;
                    Bulletin.this.layout.invalidate();
                    Layout.Transition transition = Bulletin.this.layoutTransition;
                    Layout layout = Bulletin.this.layout;
                    final Layout layout2 = Bulletin.this.layout;
                    layout2.getClass();
                    transition.animateEnter(layout, new Runnable() { // from class: org.telegram.ui.Components.Bulletin$2$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            Bulletin.Layout.this.onEnterTransitionStart();
                        }
                    }, new Runnable() { // from class: org.telegram.ui.Components.Bulletin$2$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            Bulletin.AnonymousClass2.this.m2289lambda$onLayoutChange$0$orgtelegramuiComponentsBulletin$2();
                        }
                    }, new Consumer() { // from class: org.telegram.ui.Components.Bulletin$2$$ExternalSyntheticLambda0
                        @Override // androidx.core.util.Consumer
                        public final void accept(Object obj) {
                            Bulletin.AnonymousClass2.this.m2290lambda$onLayoutChange$1$orgtelegramuiComponentsBulletin$2((Float) obj);
                        }
                    }, Bulletin.this.currentBottomOffset);
                    return;
                }
                if (Bulletin.this.currentDelegate != null) {
                    Bulletin.this.currentDelegate.onOffsetChange(Bulletin.this.layout.getHeight() - Bulletin.this.currentBottomOffset);
                }
                Bulletin.this.updatePosition();
                Bulletin.this.layout.onEnterTransitionStart();
                Bulletin.this.layout.onEnterTransitionEnd();
                Bulletin.this.setCanHide(true);
            }
        }

        /* renamed from: lambda$onLayoutChange$0$org-telegram-ui-Components-Bulletin$2 */
        public /* synthetic */ void m2289lambda$onLayoutChange$0$orgtelegramuiComponentsBulletin$2() {
            Bulletin.this.layout.transitionRunning = false;
            Bulletin.this.layout.onEnterTransitionEnd();
            Bulletin.this.setCanHide(true);
        }

        /* renamed from: lambda$onLayoutChange$1$org-telegram-ui-Components-Bulletin$2 */
        public /* synthetic */ void m2290lambda$onLayoutChange$1$orgtelegramuiComponentsBulletin$2(Float offset) {
            if (Bulletin.this.currentDelegate != null) {
                Bulletin.this.currentDelegate.onOffsetChange(Bulletin.this.layout.getHeight() - offset.floatValue());
            }
        }
    }

    public void setCanHide(boolean canHide) {
        Layout layout;
        if (this.canHide != canHide && (layout = this.layout) != null) {
            this.canHide = canHide;
            if (canHide) {
                layout.postDelayed(this.hideRunnable, this.duration);
            } else {
                layout.removeCallbacks(this.hideRunnable);
            }
        }
    }

    public void ensureLayoutTransitionCreated() {
        Layout layout = this.layout;
        if (layout != null && this.layoutTransition == null) {
            this.layoutTransition = layout.createTransition();
        }
    }

    public void hide() {
        hide(isTransitionsEnabled(), 0L);
    }

    public void hide(long duration) {
        hide(isTransitionsEnabled(), duration);
    }

    public void hide(boolean animated, long duration) {
        Layout layout = this.layout;
        if (layout != null && this.showing) {
            this.showing = false;
            if (visibleBulletin == this) {
                visibleBulletin = null;
            }
            int bottomOffset = this.currentBottomOffset;
            this.currentBottomOffset = 0;
            if (ViewCompat.isLaidOut(layout)) {
                this.layout.removeCallbacks(this.hideRunnable);
                if (animated) {
                    this.layout.transitionRunning = true;
                    this.layout.delegate = this.currentDelegate;
                    this.layout.invalidate();
                    if (duration >= 0) {
                        Layout.DefaultTransition transition = new Layout.DefaultTransition();
                        transition.duration = duration;
                        this.layoutTransition = transition;
                    } else {
                        ensureLayoutTransitionCreated();
                    }
                    Layout.Transition transition2 = this.layoutTransition;
                    final Layout layout2 = this.layout;
                    layout2.getClass();
                    transition2.animateExit(layout2, new Runnable() { // from class: org.telegram.ui.Components.Bulletin$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            Bulletin.Layout.this.onExitTransitionStart();
                        }
                    }, new Runnable() { // from class: org.telegram.ui.Components.Bulletin$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            Bulletin.this.m2286lambda$hide$0$orgtelegramuiComponentsBulletin();
                        }
                    }, new Consumer() { // from class: org.telegram.ui.Components.Bulletin$$ExternalSyntheticLambda0
                        @Override // androidx.core.util.Consumer
                        public final void accept(Object obj) {
                            Bulletin.this.m2287lambda$hide$1$orgtelegramuiComponentsBulletin((Float) obj);
                        }
                    }, bottomOffset);
                    return;
                }
            }
            Delegate delegate = this.currentDelegate;
            if (delegate != null) {
                delegate.onOffsetChange(0.0f);
                this.currentDelegate.onHide(this);
            }
            this.layout.onExitTransitionStart();
            this.layout.onExitTransitionEnd();
            this.layout.onHide();
            if (this.containerLayout != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Bulletin$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        Bulletin.this.m2288lambda$hide$2$orgtelegramuiComponentsBulletin();
                    }
                });
            }
            this.layout.onDetach();
        }
    }

    /* renamed from: lambda$hide$0$org-telegram-ui-Components-Bulletin */
    public /* synthetic */ void m2286lambda$hide$0$orgtelegramuiComponentsBulletin() {
        Delegate delegate = this.currentDelegate;
        if (delegate != null) {
            delegate.onOffsetChange(0.0f);
            this.currentDelegate.onHide(this);
        }
        this.layout.transitionRunning = false;
        this.layout.onExitTransitionEnd();
        this.layout.onHide();
        this.containerLayout.removeView(this.parentLayout);
        this.layout.onDetach();
    }

    /* renamed from: lambda$hide$1$org-telegram-ui-Components-Bulletin */
    public /* synthetic */ void m2287lambda$hide$1$orgtelegramuiComponentsBulletin(Float offset) {
        Delegate delegate = this.currentDelegate;
        if (delegate != null) {
            delegate.onOffsetChange(this.layout.getHeight() - offset.floatValue());
        }
    }

    /* renamed from: lambda$hide$2$org-telegram-ui-Components-Bulletin */
    public /* synthetic */ void m2288lambda$hide$2$orgtelegramuiComponentsBulletin() {
        this.containerLayout.removeView(this.parentLayout);
    }

    public boolean isShowing() {
        return this.showing;
    }

    public Layout getLayout() {
        return this.layout;
    }

    public static boolean isTransitionsEnabled() {
        return MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) && Build.VERSION.SDK_INT >= 18;
    }

    public void updatePosition() {
        Layout layout = this.layout;
        if (layout != null) {
            layout.updatePosition();
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class ParentLayout extends FrameLayout {
        private final GestureDetector gestureDetector;
        private boolean hideAnimationRunning;
        private final Layout layout;
        private boolean needLeftAlphaAnimation;
        private boolean needRightAlphaAnimation;
        private boolean pressed;
        private final android.graphics.Rect rect = new android.graphics.Rect();
        private float translationX;

        protected abstract void onHide();

        protected abstract void onPressedStateChanged(boolean z);

        static /* synthetic */ float access$1424(ParentLayout x0, float x1) {
            float f = x0.translationX - x1;
            x0.translationX = f;
            return f;
        }

        public ParentLayout(Layout layout) {
            super(layout.getContext());
            this.layout = layout;
            GestureDetector gestureDetector = new GestureDetector(layout.getContext(), new AnonymousClass1(layout));
            this.gestureDetector = gestureDetector;
            gestureDetector.setIsLongpressEnabled(false);
            addView(layout);
        }

        /* renamed from: org.telegram.ui.Components.Bulletin$ParentLayout$1 */
        /* loaded from: classes5.dex */
        public class AnonymousClass1 extends GestureDetector.SimpleOnGestureListener {
            final /* synthetic */ Layout val$layout;

            AnonymousClass1(Layout layout) {
                ParentLayout.this = this$0;
                this.val$layout = layout;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent e) {
                if (!ParentLayout.this.hideAnimationRunning) {
                    ParentLayout.this.needLeftAlphaAnimation = this.val$layout.isNeedSwipeAlphaAnimation(true);
                    ParentLayout.this.needRightAlphaAnimation = this.val$layout.isNeedSwipeAlphaAnimation(false);
                    return true;
                }
                return false;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                this.val$layout.setTranslationX(ParentLayout.access$1424(ParentLayout.this, distanceX));
                if (ParentLayout.this.translationX == 0.0f || ((ParentLayout.this.translationX < 0.0f && ParentLayout.this.needLeftAlphaAnimation) || (ParentLayout.this.translationX > 0.0f && ParentLayout.this.needRightAlphaAnimation))) {
                    this.val$layout.setAlpha(1.0f - (Math.abs(ParentLayout.this.translationX) / this.val$layout.getWidth()));
                    return true;
                }
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean needAlphaAnimation = false;
                if (Math.abs(velocityX) > 2000.0f) {
                    if ((velocityX < 0.0f && ParentLayout.this.needLeftAlphaAnimation) || (velocityX > 0.0f && ParentLayout.this.needRightAlphaAnimation)) {
                        needAlphaAnimation = true;
                    }
                    SpringAnimation springAnimation = new SpringAnimation(this.val$layout, DynamicAnimation.TRANSLATION_X, Math.signum(velocityX) * this.val$layout.getWidth() * 2.0f);
                    if (!needAlphaAnimation) {
                        springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.Bulletin$ParentLayout$1$$ExternalSyntheticLambda0
                            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                                Bulletin.ParentLayout.AnonymousClass1.this.m2292x7bd22355(dynamicAnimation, z, f, f2);
                            }
                        });
                        final Layout layout = this.val$layout;
                        springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.Bulletin$ParentLayout$1$$ExternalSyntheticLambda2
                            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                                Bulletin.ParentLayout.AnonymousClass1.lambda$onFling$1(Bulletin.Layout.this, dynamicAnimation, f, f2);
                            }
                        });
                    }
                    springAnimation.getSpring().setDampingRatio(1.0f);
                    springAnimation.getSpring().setStiffness(100.0f);
                    springAnimation.setStartVelocity(velocityX);
                    springAnimation.start();
                    if (needAlphaAnimation) {
                        SpringAnimation springAnimation2 = new SpringAnimation(this.val$layout, DynamicAnimation.ALPHA, 0.0f);
                        springAnimation2.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.Bulletin$ParentLayout$1$$ExternalSyntheticLambda1
                            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                                Bulletin.ParentLayout.AnonymousClass1.this.m2293xd7835813(dynamicAnimation, z, f, f2);
                            }
                        });
                        springAnimation2.addUpdateListener(Bulletin$ParentLayout$1$$ExternalSyntheticLambda3.INSTANCE);
                        springAnimation.getSpring().setDampingRatio(1.0f);
                        springAnimation.getSpring().setStiffness(10.0f);
                        springAnimation.setStartVelocity(velocityX);
                        springAnimation2.start();
                    }
                    ParentLayout.this.hideAnimationRunning = true;
                    return true;
                }
                return false;
            }

            /* renamed from: lambda$onFling$0$org-telegram-ui-Components-Bulletin$ParentLayout$1 */
            public /* synthetic */ void m2292x7bd22355(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                ParentLayout.this.onHide();
            }

            public static /* synthetic */ void lambda$onFling$1(Layout layout, DynamicAnimation animation, float value, float velocity) {
                if (Math.abs(value) > layout.getWidth()) {
                    animation.cancel();
                }
            }

            /* renamed from: lambda$onFling$2$org-telegram-ui-Components-Bulletin$ParentLayout$1 */
            public /* synthetic */ void m2293xd7835813(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                ParentLayout.this.onHide();
            }

            public static /* synthetic */ void lambda$onFling$3(DynamicAnimation animation, float value, float velocity) {
                if (value <= 0.0f) {
                    animation.cancel();
                }
            }
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (this.pressed || inLayoutHitRect(event.getX(), event.getY())) {
                this.gestureDetector.onTouchEvent(event);
                int actionMasked = event.getActionMasked();
                if (actionMasked == 0) {
                    if (!this.pressed && !this.hideAnimationRunning) {
                        this.layout.animate().cancel();
                        this.translationX = this.layout.getTranslationX();
                        this.pressed = true;
                        onPressedStateChanged(true);
                    }
                } else if ((actionMasked == 1 || actionMasked == 3) && this.pressed) {
                    if (!this.hideAnimationRunning) {
                        float f = 1.0f;
                        if (Math.abs(this.translationX) > this.layout.getWidth() / 3.0f) {
                            final float tx = Math.signum(this.translationX) * this.layout.getWidth();
                            float f2 = this.translationX;
                            boolean needAlphaAnimation = (f2 < 0.0f && this.needLeftAlphaAnimation) || (f2 > 0.0f && this.needRightAlphaAnimation);
                            ViewPropertyAnimator translationX = this.layout.animate().translationX(tx);
                            if (needAlphaAnimation) {
                                f = 0.0f;
                            }
                            translationX.alpha(f).setDuration(200L).setInterpolator(AndroidUtilities.accelerateInterpolator).withEndAction(new Runnable() { // from class: org.telegram.ui.Components.Bulletin$ParentLayout$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    Bulletin.ParentLayout.this.m2291x9108f6b1(tx);
                                }
                            }).start();
                        } else {
                            this.layout.animate().translationX(0.0f).alpha(1.0f).setDuration(200L).start();
                        }
                    }
                    this.pressed = false;
                    onPressedStateChanged(false);
                }
                return true;
            }
            return false;
        }

        /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-Components-Bulletin$ParentLayout */
        public /* synthetic */ void m2291x9108f6b1(float tx) {
            if (this.layout.getTranslationX() == tx) {
                onHide();
            }
        }

        private boolean inLayoutHitRect(float x, float y) {
            this.layout.getHitRect(this.rect);
            return this.rect.contains((int) x, (int) y);
        }
    }

    public static void addDelegate(BaseFragment fragment, Delegate delegate) {
        FrameLayout containerLayout = fragment.getLayoutContainer();
        if (containerLayout != null) {
            addDelegate(containerLayout, delegate);
        }
    }

    public static void addDelegate(FrameLayout containerLayout, Delegate delegate) {
        delegates.put(containerLayout, delegate);
    }

    public static void removeDelegate(BaseFragment fragment) {
        FrameLayout containerLayout = fragment.getLayoutContainer();
        if (containerLayout != null) {
            removeDelegate(containerLayout);
        }
    }

    public static void removeDelegate(FrameLayout containerLayout) {
        delegates.remove(containerLayout);
    }

    /* loaded from: classes5.dex */
    public interface Delegate {
        int getBottomOffset(int i);

        void onHide(Bulletin bulletin);

        void onOffsetChange(float f);

        void onShow(Bulletin bulletin);

        /* renamed from: org.telegram.ui.Components.Bulletin$Delegate$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static int $default$getBottomOffset(Delegate _this, int tag) {
                return 0;
            }

            public static void $default$onOffsetChange(Delegate _this, float offset) {
            }

            public static void $default$onShow(Delegate _this, Bulletin bulletin) {
            }

            public static void $default$onHide(Delegate _this, Bulletin bulletin) {
            }
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class Layout extends FrameLayout {
        public static final FloatPropertyCompat<Layout> IN_OUT_OFFSET_Y = new FloatPropertyCompat<Layout>("offsetY") { // from class: org.telegram.ui.Components.Bulletin.Layout.1
            public float getValue(Layout object) {
                return object.inOutOffset;
            }

            public void setValue(Layout object, float value) {
                object.setInOutOffset(value);
            }
        };
        public static final Property<Layout, Float> IN_OUT_OFFSET_Y2 = new AnimationProperties.FloatProperty<Layout>("offsetY") { // from class: org.telegram.ui.Components.Bulletin.Layout.2
            public Float get(Layout layout) {
                return Float.valueOf(layout.inOutOffset);
            }

            public void setValue(Layout object, float value) {
                object.setInOutOffset(value);
            }
        };
        Drawable background;
        protected Bulletin bulletin;
        Delegate delegate;
        public float inOutOffset;
        private final Theme.ResourcesProvider resourcesProvider;
        public boolean transitionRunning;
        private final List<Callback> callbacks = new ArrayList();
        private int wideScreenWidth = -2;
        private int wideScreenGravity = 1;

        /* loaded from: classes5.dex */
        public interface Callback {
            void onAttach(Layout layout, Bulletin bulletin);

            void onDetach(Layout layout);

            void onEnterTransitionEnd(Layout layout);

            void onEnterTransitionStart(Layout layout);

            void onExitTransitionEnd(Layout layout);

            void onExitTransitionStart(Layout layout);

            void onHide(Layout layout);

            void onShow(Layout layout);
        }

        /* loaded from: classes5.dex */
        public interface Transition {
            void animateEnter(Layout layout, Runnable runnable, Runnable runnable2, Consumer<Float> consumer, int i);

            void animateExit(Layout layout, Runnable runnable, Runnable runnable2, Consumer<Float> consumer, int i);
        }

        public Layout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            setMinimumHeight(AndroidUtilities.dp(48.0f));
            setBackground(getThemedColor(Theme.key_undo_background));
            updateSize();
            setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            setWillNotDraw(false);
        }

        protected void setBackground(int color) {
            this.background = Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), color);
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            updateSize();
        }

        private void updateSize() {
            boolean isWideScreen = isWideScreen();
            int i = isWideScreen ? this.wideScreenWidth : -1;
            int i2 = 80;
            if (isWideScreen) {
                i2 = 80 | this.wideScreenGravity;
            }
            setLayoutParams(LayoutHelper.createFrame(i, -2, i2));
        }

        private boolean isWideScreen() {
            return AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x >= AndroidUtilities.displaySize.y;
        }

        public void setWideScreenParams(int width, int gravity) {
            boolean changed = false;
            if (this.wideScreenWidth != width) {
                this.wideScreenWidth = width;
                changed = true;
            }
            if (this.wideScreenGravity != gravity) {
                this.wideScreenGravity = gravity;
                changed = true;
            }
            if (isWideScreen() && changed) {
                updateSize();
            }
        }

        public boolean isNeedSwipeAlphaAnimation(boolean swipeLeft) {
            if (!isWideScreen() || this.wideScreenWidth == -1) {
                return false;
            }
            int i = this.wideScreenGravity;
            if (i == 1) {
                return true;
            }
            return swipeLeft ? i == 5 : i != 5;
        }

        protected CharSequence getAccessibilityText() {
            return null;
        }

        public Bulletin getBulletin() {
            return this.bulletin;
        }

        public boolean isAttachedToBulletin() {
            return this.bulletin != null;
        }

        protected void onAttach(Bulletin bulletin) {
            this.bulletin = bulletin;
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onAttach(this, bulletin);
            }
        }

        protected void onDetach() {
            this.bulletin = null;
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onDetach(this);
            }
        }

        protected void onShow() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onShow(this);
            }
        }

        protected void onHide() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onHide(this);
            }
        }

        public void onEnterTransitionStart() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onEnterTransitionStart(this);
            }
        }

        public void onEnterTransitionEnd() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onEnterTransitionEnd(this);
            }
        }

        public void onExitTransitionStart() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onExitTransitionStart(this);
            }
        }

        public void onExitTransitionEnd() {
            int size = this.callbacks.size();
            for (int i = 0; i < size; i++) {
                this.callbacks.get(i).onExitTransitionEnd(this);
            }
        }

        public void addCallback(Callback callback) {
            this.callbacks.add(callback);
        }

        public void removeCallback(Callback callback) {
            this.callbacks.remove(callback);
        }

        public void updatePosition() {
            float tranlsation = 0.0f;
            Delegate delegate = this.delegate;
            if (delegate != null) {
                Bulletin bulletin = this.bulletin;
                tranlsation = 0.0f + delegate.getBottomOffset(bulletin != null ? bulletin.tag : 0);
            }
            setTranslationY((-tranlsation) + this.inOutOffset);
        }

        public Transition createTransition() {
            return new SpringTransition();
        }

        /* loaded from: classes5.dex */
        public static class DefaultTransition implements Transition {
            long duration = 255;

            @Override // org.telegram.ui.Components.Bulletin.Layout.Transition
            public void animateEnter(final Layout layout, final Runnable startAction, final Runnable endAction, final Consumer<Float> onUpdate, int bottomOffset) {
                layout.setInOutOffset(layout.getMeasuredHeight());
                if (onUpdate != null) {
                    onUpdate.accept(Float.valueOf(layout.getTranslationY()));
                }
                ObjectAnimator animator = ObjectAnimator.ofFloat(layout, Layout.IN_OUT_OFFSET_Y2, 0.0f);
                animator.setDuration(this.duration);
                animator.setInterpolator(Easings.easeOutQuad);
                if (startAction != null || endAction != null) {
                    animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Bulletin.Layout.DefaultTransition.1
                        {
                            DefaultTransition.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animation) {
                            Runnable runnable = startAction;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            Runnable runnable = endAction;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                    });
                }
                if (onUpdate != null) {
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Bulletin$Layout$DefaultTransition$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            Consumer.this.accept(Float.valueOf(layout.getTranslationY()));
                        }
                    });
                }
                animator.start();
            }

            @Override // org.telegram.ui.Components.Bulletin.Layout.Transition
            public void animateExit(final Layout layout, final Runnable startAction, final Runnable endAction, final Consumer<Float> onUpdate, int bottomOffset) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(layout, Layout.IN_OUT_OFFSET_Y2, layout.getHeight());
                animator.setDuration(175L);
                animator.setInterpolator(Easings.easeInQuad);
                if (startAction != null || endAction != null) {
                    animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Bulletin.Layout.DefaultTransition.2
                        {
                            DefaultTransition.this = this;
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animation) {
                            Runnable runnable = startAction;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            Runnable runnable = endAction;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                    });
                }
                if (onUpdate != null) {
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Bulletin$Layout$DefaultTransition$$ExternalSyntheticLambda1
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            Consumer.this.accept(Float.valueOf(layout.getTranslationY()));
                        }
                    });
                }
                animator.start();
            }
        }

        /* loaded from: classes5.dex */
        public static class SpringTransition implements Transition {
            private static final float DAMPING_RATIO = 0.8f;
            private static final float STIFFNESS = 400.0f;

            @Override // org.telegram.ui.Components.Bulletin.Layout.Transition
            public void animateEnter(final Layout layout, Runnable startAction, final Runnable endAction, final Consumer<Float> onUpdate, int bottomOffset) {
                layout.setInOutOffset(layout.getMeasuredHeight());
                if (onUpdate != null) {
                    onUpdate.accept(Float.valueOf(layout.getTranslationY()));
                }
                SpringAnimation springAnimation = new SpringAnimation(layout, Layout.IN_OUT_OFFSET_Y, 0.0f);
                springAnimation.getSpring().setDampingRatio(DAMPING_RATIO);
                springAnimation.getSpring().setStiffness(STIFFNESS);
                if (endAction != null) {
                    springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda1
                        @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                        public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                            Bulletin.Layout.SpringTransition.lambda$animateEnter$0(Bulletin.Layout.this, endAction, dynamicAnimation, z, f, f2);
                        }
                    });
                }
                if (onUpdate != null) {
                    springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda2
                        @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                        public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                            Consumer.this.accept(Float.valueOf(layout.getTranslationY()));
                        }
                    });
                }
                springAnimation.start();
                if (startAction != null) {
                    startAction.run();
                }
            }

            public static /* synthetic */ void lambda$animateEnter$0(Layout layout, Runnable endAction, DynamicAnimation animation, boolean canceled, float value, float velocity) {
                layout.setInOutOffset(0.0f);
                if (!canceled) {
                    endAction.run();
                }
            }

            @Override // org.telegram.ui.Components.Bulletin.Layout.Transition
            public void animateExit(final Layout layout, Runnable startAction, final Runnable endAction, final Consumer<Float> onUpdate, int bottomOffset) {
                SpringAnimation springAnimation = new SpringAnimation(layout, Layout.IN_OUT_OFFSET_Y, layout.getHeight());
                springAnimation.getSpring().setDampingRatio(DAMPING_RATIO);
                springAnimation.getSpring().setStiffness(STIFFNESS);
                if (endAction != null) {
                    springAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda0
                        @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                        public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                            Bulletin.Layout.SpringTransition.lambda$animateExit$2(endAction, dynamicAnimation, z, f, f2);
                        }
                    });
                }
                if (onUpdate != null) {
                    springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.Components.Bulletin$Layout$SpringTransition$$ExternalSyntheticLambda3
                        @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                        public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                            Consumer.this.accept(Float.valueOf(layout.getTranslationY()));
                        }
                    });
                }
                springAnimation.start();
                if (startAction != null) {
                    startAction.run();
                }
            }

            public static /* synthetic */ void lambda$animateExit$2(Runnable endAction, DynamicAnimation animation, boolean canceled, float value, float velocity) {
                if (!canceled) {
                    endAction.run();
                }
            }
        }

        public void setInOutOffset(float offset) {
            this.inOutOffset = offset;
            updatePosition();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            this.background.setBounds(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), getMeasuredWidth() - AndroidUtilities.dp(8.0f), getMeasuredHeight() - AndroidUtilities.dp(8.0f));
            if (this.transitionRunning && this.delegate != null) {
                int clipBottom = ((View) getParent()).getMeasuredHeight() - this.delegate.getBottomOffset(this.bulletin.tag);
                int viewBottom = (int) (getY() + getMeasuredHeight());
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight() - (viewBottom - clipBottom));
                this.background.draw(canvas);
                super.dispatchDraw(canvas);
                canvas.restore();
                invalidate();
                return;
            }
            this.background.draw(canvas);
            super.dispatchDraw(canvas);
        }

        public int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    /* loaded from: classes5.dex */
    public static class ButtonLayout extends Layout {
        private Button button;
        private int childrenMeasuredWidth;

        public ButtonLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.childrenMeasuredWidth = 0;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (this.button != null && View.MeasureSpec.getMode(widthMeasureSpec) == Integer.MIN_VALUE) {
                setMeasuredDimension(this.childrenMeasuredWidth + this.button.getMeasuredWidth(), getMeasuredHeight());
            }
        }

        @Override // android.view.ViewGroup
        protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
            Button button = this.button;
            if (button != null && child != button) {
                widthUsed += button.getMeasuredWidth() - AndroidUtilities.dp(12.0f);
            }
            super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
            if (child != this.button) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) child.getLayoutParams();
                this.childrenMeasuredWidth = Math.max(this.childrenMeasuredWidth, lp.leftMargin + lp.rightMargin + child.getMeasuredWidth());
            }
        }

        public Button getButton() {
            return this.button;
        }

        public void setButton(Button button) {
            Button button2 = this.button;
            if (button2 != null) {
                removeCallback(button2);
                removeView(this.button);
            }
            this.button = button;
            if (button != null) {
                addCallback(button);
                addView(button, 0, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388629));
            }
        }
    }

    /* loaded from: classes5.dex */
    public static class SimpleLayout extends ButtonLayout {
        public final ImageView imageView;
        public final TextView textView;

        public SimpleLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            int undoInfoColor = getThemedColor(Theme.key_undo_infoColor);
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setColorFilter(new PorterDuffColorFilter(undoInfoColor, PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 16.0f, 12.0f, 16.0f, 12.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setSingleLine();
            textView.setTextColor(undoInfoColor);
            textView.setTypeface(Typeface.SANS_SERIF);
            textView.setTextSize(1, 15.0f);
            addView(textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        public CharSequence getAccessibilityText() {
            return this.textView.getText();
        }
    }

    /* loaded from: classes5.dex */
    public static class MultiLineLayout extends ButtonLayout {
        public final BackupImageView imageView;
        public final TextView textView;

        public MultiLineLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            BackupImageView backupImageView = new BackupImageView(getContext());
            this.imageView = backupImageView;
            TextView textView = new TextView(getContext());
            this.textView = textView;
            addView(backupImageView, LayoutHelper.createFrameRelatively(30.0f, 30.0f, 8388627, 12.0f, 8.0f, 12.0f, 8.0f));
            textView.setGravity(GravityCompat.START);
            textView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            textView.setTextColor(getThemedColor(Theme.key_undo_infoColor));
            textView.setTextSize(1, 15.0f);
            textView.setTypeface(Typeface.SANS_SERIF);
            addView(textView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        public CharSequence getAccessibilityText() {
            return this.textView.getText();
        }
    }

    /* loaded from: classes5.dex */
    public static class TwoLineLayout extends ButtonLayout {
        public final BackupImageView imageView;
        public final TextView subtitleTextView;
        public final TextView titleTextView;

        public TwoLineLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            int undoInfoColor = getThemedColor(Theme.key_undo_infoColor);
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrameRelatively(29.0f, 29.0f, 8388627, 12.0f, 12.0f, 12.0f, 12.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 54.0f, 8.0f, 12.0f, 8.0f));
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setSingleLine();
            textView.setTextColor(undoInfoColor);
            textView.setTextSize(1, 14.0f);
            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            linearLayout.addView(textView);
            TextView textView2 = new TextView(context);
            this.subtitleTextView = textView2;
            textView2.setMaxLines(2);
            textView2.setTextColor(undoInfoColor);
            textView2.setLinkTextColor(getThemedColor(Theme.key_undo_cancelColor));
            textView2.setMovementMethod(new LinkMovementMethod());
            textView2.setTypeface(Typeface.SANS_SERIF);
            textView2.setTextSize(1, 13.0f);
            linearLayout.addView(textView2);
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        public CharSequence getAccessibilityText() {
            return ((Object) this.titleTextView.getText()) + ".\n" + ((Object) this.subtitleTextView.getText());
        }
    }

    /* loaded from: classes5.dex */
    public static class TwoLineLottieLayout extends ButtonLayout {
        public final RLottieImageView imageView;
        public final TextView subtitleTextView;
        private final int textColor = getThemedColor(Theme.key_undo_infoColor);
        public final TextView titleTextView;

        public TwoLineLottieLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            setBackground(getThemedColor(Theme.key_undo_background));
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(rLottieImageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 8388627));
            int undoInfoColor = getThemedColor(Theme.key_undo_infoColor);
            int undoLinkColor = getThemedColor(Theme.key_voipgroup_overlayBlue1);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 8.0f, 12.0f, 8.0f));
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setSingleLine();
            textView.setTextColor(undoInfoColor);
            textView.setTextSize(1, 14.0f);
            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            linearLayout.addView(textView);
            TextView textView2 = new TextView(context);
            this.subtitleTextView = textView2;
            textView2.setTextColor(undoInfoColor);
            textView2.setLinkTextColor(undoLinkColor);
            textView2.setTypeface(Typeface.SANS_SERIF);
            textView2.setTextSize(1, 13.0f);
            linearLayout.addView(textView2);
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        protected void onShow() {
            super.onShow();
            this.imageView.playAnimation();
        }

        public void setAnimation(int resId, String... layers) {
            setAnimation(resId, 32, 32, layers);
        }

        public void setAnimation(int resId, int w, int h, String... layers) {
            this.imageView.setAnimation(resId, w, h);
            for (String layer : layers) {
                this.imageView.setLayerColor(layer + ".**", this.textColor);
            }
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        public CharSequence getAccessibilityText() {
            return ((Object) this.titleTextView.getText()) + ".\n" + ((Object) this.subtitleTextView.getText());
        }
    }

    /* loaded from: classes5.dex */
    public static class LottieLayout extends ButtonLayout {
        public RLottieImageView imageView;
        private int textColor;
        public TextView textView;

        public LottieLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 8388627));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setSingleLine();
            this.textView.setTypeface(Typeface.SANS_SERIF);
            this.textView.setTextSize(1, 15.0f);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
            addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 56.0f, 0.0f, 16.0f, 0.0f));
            setTextColor(getThemedColor(Theme.key_undo_infoColor));
            setBackground(getThemedColor(Theme.key_undo_background));
        }

        public LottieLayout(Context context, Theme.ResourcesProvider resourcesProvider, int backgroundColor, int textColor) {
            this(context, resourcesProvider);
            setBackground(backgroundColor);
            setTextColor(textColor);
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
            this.textView.setTextColor(textColor);
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        protected void onShow() {
            super.onShow();
            this.imageView.playAnimation();
        }

        public void setAnimation(int resId, String... layers) {
            setAnimation(resId, 32, 32, layers);
        }

        public void setAnimation(int resId, int w, int h, String... layers) {
            this.imageView.setAnimation(resId, w, h);
            for (String layer : layers) {
                this.imageView.setLayerColor(layer + ".**", this.textColor);
            }
        }

        public void setIconPaddingBottom(int paddingBottom) {
            this.imageView.setLayoutParams(LayoutHelper.createFrameRelatively(56.0f, 48 - paddingBottom, 8388627, 0.0f, 0.0f, 0.0f, paddingBottom));
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout
        public CharSequence getAccessibilityText() {
            return this.textView.getText();
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class Button extends FrameLayout implements Layout.Callback {
        public Button(Context context) {
            super(context);
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onAttach(Layout layout, Bulletin bulletin) {
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onDetach(Layout layout) {
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onShow(Layout layout) {
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onHide(Layout layout) {
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onEnterTransitionStart(Layout layout) {
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onEnterTransitionEnd(Layout layout) {
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onExitTransitionStart(Layout layout) {
        }

        @Override // org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onExitTransitionEnd(Layout layout) {
        }
    }

    /* loaded from: classes5.dex */
    public static final class UndoButton extends Button {
        private Bulletin bulletin;
        private Runnable delayedAction;
        private boolean isUndone;
        private final Theme.ResourcesProvider resourcesProvider;
        private Runnable undoAction;
        private TextView undoTextView;

        public UndoButton(Context context, boolean text) {
            this(context, text, null);
        }

        public UndoButton(Context context, boolean text, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            int undoCancelColor = getThemedColor(Theme.key_undo_cancelColor);
            if (!text) {
                ImageView undoImageView = new ImageView(getContext());
                undoImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Bulletin$UndoButton$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        Bulletin.UndoButton.this.m2295lambda$new$1$orgtelegramuiComponentsBulletin$UndoButton(view);
                    }
                });
                undoImageView.setImageResource(R.drawable.chats_undo);
                undoImageView.setColorFilter(new PorterDuffColorFilter(undoCancelColor, PorterDuff.Mode.MULTIPLY));
                undoImageView.setBackground(Theme.createSelectorDrawable(419430400 | (16777215 & undoCancelColor)));
                ViewHelper.setPaddingRelative(undoImageView, 0.0f, 12.0f, 0.0f, 12.0f);
                addView(undoImageView, LayoutHelper.createFrameRelatively(56.0f, 48.0f, 16));
                return;
            }
            TextView textView = new TextView(context);
            this.undoTextView = textView;
            textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Bulletin$UndoButton$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    Bulletin.UndoButton.this.m2294lambda$new$0$orgtelegramuiComponentsBulletin$UndoButton(view);
                }
            });
            int rightInset = 0;
            int leftInset = LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : 0;
            this.undoTextView.setBackground(Theme.createCircleSelectorDrawable(419430400 | (16777215 & undoCancelColor), leftInset, !LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : rightInset));
            this.undoTextView.setTextSize(1, 14.0f);
            this.undoTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.undoTextView.setTextColor(undoCancelColor);
            this.undoTextView.setText(LocaleController.getString("Undo", R.string.Undo));
            this.undoTextView.setGravity(16);
            ViewHelper.setPaddingRelative(this.undoTextView, 16.0f, 0.0f, 16.0f, 0.0f);
            addView(this.undoTextView, LayoutHelper.createFrameRelatively(-2.0f, 48.0f, 16, 8.0f, 0.0f, 0.0f, 0.0f));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-Components-Bulletin$UndoButton */
        public /* synthetic */ void m2294lambda$new$0$orgtelegramuiComponentsBulletin$UndoButton(View v) {
            undo();
        }

        /* renamed from: lambda$new$1$org-telegram-ui-Components-Bulletin$UndoButton */
        public /* synthetic */ void m2295lambda$new$1$orgtelegramuiComponentsBulletin$UndoButton(View v) {
            undo();
        }

        public UndoButton setText(CharSequence text) {
            TextView textView = this.undoTextView;
            if (textView != null) {
                textView.setText(text);
            }
            return this;
        }

        public void undo() {
            if (this.bulletin != null) {
                this.isUndone = true;
                Runnable runnable = this.undoAction;
                if (runnable != null) {
                    runnable.run();
                }
                this.bulletin.hide();
            }
        }

        @Override // org.telegram.ui.Components.Bulletin.Button, org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onAttach(Layout layout, Bulletin bulletin) {
            this.bulletin = bulletin;
        }

        @Override // org.telegram.ui.Components.Bulletin.Button, org.telegram.ui.Components.Bulletin.Layout.Callback
        public void onDetach(Layout layout) {
            this.bulletin = null;
            Runnable runnable = this.delayedAction;
            if (runnable != null && !this.isUndone) {
                runnable.run();
            }
        }

        public UndoButton setUndoAction(Runnable undoAction) {
            this.undoAction = undoAction;
            return this;
        }

        public UndoButton setDelayedAction(Runnable delayedAction) {
            this.delayedAction = delayedAction;
            return this;
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    /* loaded from: classes5.dex */
    public static class EmptyBulletin extends Bulletin {
        public EmptyBulletin() {
            super();
        }

        @Override // org.telegram.ui.Components.Bulletin
        public Bulletin show() {
            return this;
        }
    }
}
