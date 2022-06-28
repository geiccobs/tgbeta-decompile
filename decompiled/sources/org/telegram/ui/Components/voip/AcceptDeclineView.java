package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.widget.Button;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class AcceptDeclineView extends View {
    private FabBackgroundDrawable acceptDrawable;
    private StaticLayout acceptLayout;
    private AcceptDeclineAccessibilityNodeProvider accessibilityNodeProvider;
    Drawable arrowDrawable;
    float arrowProgress;
    float bigRadius;
    private Drawable callDrawable;
    private Drawable cancelDrawable;
    boolean captured;
    long capturedTime;
    private FabBackgroundDrawable declineDrawable;
    private StaticLayout declineLayout;
    Animator leftAnimator;
    boolean leftDrag;
    float leftOffsetX;
    Listener listener;
    float maxOffset;
    private StaticLayout retryLayout;
    boolean retryMod;
    Animator rightAnimator;
    float rigthOffsetX;
    Drawable rippleDrawable;
    private boolean screenWasWakeup;
    float smallRadius;
    boolean startDrag;
    float startX;
    float startY;
    float touchSlop;
    private Paint acceptCirclePaint = new Paint(1);
    boolean expandSmallRadius = true;
    boolean expandBigRadius = true;
    Rect acceptRect = new Rect();
    Rect declineRect = new Rect();
    Paint linePaint = new Paint(1);
    private int buttonWidth = AndroidUtilities.dp(60.0f);

    /* loaded from: classes5.dex */
    public interface Listener {
        void onAccept();

        void onDecline();
    }

    public AcceptDeclineView(Context context) {
        super(context);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        FabBackgroundDrawable fabBackgroundDrawable = new FabBackgroundDrawable();
        this.acceptDrawable = fabBackgroundDrawable;
        fabBackgroundDrawable.setColor(-12531895);
        FabBackgroundDrawable fabBackgroundDrawable2 = new FabBackgroundDrawable();
        this.declineDrawable = fabBackgroundDrawable2;
        fabBackgroundDrawable2.setColor(-1041108);
        FabBackgroundDrawable fabBackgroundDrawable3 = this.declineDrawable;
        int i = this.buttonWidth;
        fabBackgroundDrawable3.setBounds(0, 0, i, i);
        FabBackgroundDrawable fabBackgroundDrawable4 = this.acceptDrawable;
        int i2 = this.buttonWidth;
        fabBackgroundDrawable4.setBounds(0, 0, i2, i2);
        TextPaint textPaint = new TextPaint(1);
        textPaint.setTextSize(AndroidUtilities.dp(11.0f));
        textPaint.setColor(-1);
        String acceptStr = LocaleController.getString("AcceptCall", R.string.AcceptCall);
        String declineStr = LocaleController.getString("DeclineCall", R.string.DeclineCall);
        String retryStr = LocaleController.getString("RetryCall", R.string.RetryCall);
        this.acceptLayout = new StaticLayout(acceptStr, textPaint, (int) textPaint.measureText(acceptStr), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.declineLayout = new StaticLayout(declineStr, textPaint, (int) textPaint.measureText(declineStr), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.retryLayout = new StaticLayout(retryStr, textPaint, (int) textPaint.measureText(retryStr), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        this.callDrawable = ContextCompat.getDrawable(context, R.drawable.calls_decline).mutate();
        Drawable mutate = ContextCompat.getDrawable(context, R.drawable.ic_close_white).mutate();
        this.cancelDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        this.acceptCirclePaint.setColor(1061534797);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(52.0f), 0, ColorUtils.setAlphaComponent(-1, 76));
        this.rippleDrawable = createSimpleSelectorCircleDrawable;
        createSimpleSelectorCircleDrawable.setCallback(this);
        this.arrowDrawable = ContextCompat.getDrawable(context, R.drawable.call_arrow_right);
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.maxOffset = (getMeasuredWidth() / 2.0f) - ((this.buttonWidth / 2.0f) + AndroidUtilities.dp(46.0f));
        int padding = (this.buttonWidth - AndroidUtilities.dp(28.0f)) / 2;
        this.callDrawable.setBounds(padding, padding, AndroidUtilities.dp(28.0f) + padding, AndroidUtilities.dp(28.0f) + padding);
        this.cancelDrawable.setBounds(padding, padding, AndroidUtilities.dp(28.0f) + padding, AndroidUtilities.dp(28.0f) + padding);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(3.0f));
        this.linePaint.setColor(-1);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case 0:
                this.startX = event.getX();
                this.startY = event.getY();
                if (this.leftAnimator == null && this.declineRect.contains((int) event.getX(), (int) event.getY())) {
                    this.rippleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(52.0f), 0, -51130);
                    this.captured = true;
                    this.leftDrag = true;
                    setPressed(true);
                    return true;
                } else if (this.rightAnimator == null && this.acceptRect.contains((int) event.getX(), (int) event.getY())) {
                    this.rippleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(52.0f), 0, -11677354);
                    this.captured = true;
                    this.leftDrag = false;
                    setPressed(true);
                    Animator animator = this.rightAnimator;
                    if (animator != null) {
                        animator.cancel();
                    }
                    return true;
                }
                break;
            case 1:
            case 3:
                float dy = event.getY() - this.startY;
                if (this.captured) {
                    if (this.leftDrag) {
                        ValueAnimator animator2 = ValueAnimator.ofFloat(this.leftOffsetX, 0.0f);
                        animator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.AcceptDeclineView$$ExternalSyntheticLambda0
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                AcceptDeclineView.this.m3227x4fc0e726(valueAnimator);
                            }
                        });
                        animator2.start();
                        this.leftAnimator = animator2;
                        if (this.listener != null && ((!this.startDrag && Math.abs(dy) < this.touchSlop && !this.screenWasWakeup) || this.leftOffsetX > this.maxOffset * 0.8f)) {
                            this.listener.onDecline();
                        }
                    } else {
                        ValueAnimator animator3 = ValueAnimator.ofFloat(this.rigthOffsetX, 0.0f);
                        animator3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.AcceptDeclineView$$ExternalSyntheticLambda1
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                AcceptDeclineView.this.m3228x934c04e7(valueAnimator);
                            }
                        });
                        animator3.start();
                        this.rightAnimator = animator3;
                        if (this.listener != null && ((!this.startDrag && Math.abs(dy) < this.touchSlop && !this.screenWasWakeup) || (-this.rigthOffsetX) > this.maxOffset * 0.8f)) {
                            this.listener.onAccept();
                        }
                    }
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                this.captured = false;
                this.startDrag = false;
                setPressed(false);
                break;
            case 2:
                if (this.captured) {
                    float dx = event.getX() - this.startX;
                    if (!this.startDrag && Math.abs(dx) > this.touchSlop) {
                        if (!this.retryMod) {
                            this.startX = event.getX();
                            dx = 0.0f;
                            this.startDrag = true;
                            setPressed(false);
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } else {
                            setPressed(false);
                            this.captured = false;
                        }
                    }
                    if (this.startDrag) {
                        if (this.leftDrag) {
                            this.leftOffsetX = dx;
                            if (dx < 0.0f) {
                                this.leftOffsetX = 0.0f;
                            } else {
                                float f = this.maxOffset;
                                if (dx > f) {
                                    this.leftOffsetX = f;
                                    dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, 0.0f, 0.0f, 0));
                                }
                            }
                        } else {
                            this.rigthOffsetX = dx;
                            if (dx > 0.0f) {
                                this.rigthOffsetX = 0.0f;
                            } else {
                                float f2 = this.maxOffset;
                                if (dx < (-f2)) {
                                    this.rigthOffsetX = -f2;
                                    dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 1, 0.0f, 0.0f, 0));
                                }
                            }
                        }
                    }
                    return true;
                }
                break;
        }
        return false;
    }

    /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-Components-voip-AcceptDeclineView */
    public /* synthetic */ void m3227x4fc0e726(ValueAnimator valueAnimator) {
        this.leftOffsetX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        this.leftAnimator = null;
    }

    /* renamed from: lambda$onTouchEvent$1$org-telegram-ui-Components-voip-AcceptDeclineView */
    public /* synthetic */ void m3228x934c04e7(ValueAnimator valueAnimator) {
        this.rigthOffsetX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        this.rightAnimator = null;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float startX;
        if (!this.retryMod) {
            if (this.expandSmallRadius) {
                float dp = this.smallRadius + (AndroidUtilities.dp(2.0f) * 0.04f);
                this.smallRadius = dp;
                if (dp > AndroidUtilities.dp(4.0f)) {
                    this.smallRadius = AndroidUtilities.dp(4.0f);
                    this.expandSmallRadius = false;
                }
            } else {
                float dp2 = this.smallRadius - (AndroidUtilities.dp(2.0f) * 0.04f);
                this.smallRadius = dp2;
                if (dp2 < 0.0f) {
                    this.smallRadius = 0.0f;
                    this.expandSmallRadius = true;
                }
            }
            if (this.expandBigRadius) {
                float dp3 = this.bigRadius + (AndroidUtilities.dp(4.0f) * 0.03f);
                this.bigRadius = dp3;
                if (dp3 > AndroidUtilities.dp(10.0f)) {
                    this.bigRadius = AndroidUtilities.dp(10.0f);
                    this.expandBigRadius = false;
                }
            } else {
                float dp4 = this.bigRadius - (AndroidUtilities.dp(5.0f) * 0.03f);
                this.bigRadius = dp4;
                if (dp4 < AndroidUtilities.dp(5.0f)) {
                    this.bigRadius = AndroidUtilities.dp(5.0f);
                    this.expandBigRadius = true;
                }
            }
            invalidate();
        }
        float f = 1.0f;
        if (this.screenWasWakeup && !this.retryMod) {
            float f2 = this.arrowProgress + 0.010666667f;
            this.arrowProgress = f2;
            if (f2 > 1.0f) {
                this.arrowProgress = 0.0f;
            }
            int cY = (int) (AndroidUtilities.dp(40.0f) + (this.buttonWidth / 2.0f));
            float startX2 = AndroidUtilities.dp(46.0f) + this.buttonWidth + AndroidUtilities.dp(8.0f);
            float endX = (getMeasuredWidth() / 2.0f) - AndroidUtilities.dp(8.0f);
            float lineLength = AndroidUtilities.dp(10.0f);
            float stepProgress = (1.0f - 0.6f) / 3.0f;
            int i = 0;
            while (i < 3) {
                int x = (int) (((((endX - startX2) - lineLength) / 3.0f) * i) + startX2);
                float alpha = 0.5f;
                float startAlphaFrom = i * stepProgress;
                float f3 = this.arrowProgress;
                if (f3 > startAlphaFrom && f3 < startAlphaFrom + 0.6f) {
                    float p = (f3 - startAlphaFrom) / 0.6f;
                    startX = startX2;
                    if (p > 0.5d) {
                        p = f - p;
                    }
                    alpha = p + 0.5f;
                } else {
                    startX = startX2;
                }
                canvas.save();
                canvas.clipRect(this.leftOffsetX + AndroidUtilities.dp(46.0f) + (this.buttonWidth / 2), 0.0f, getMeasuredHeight(), getMeasuredWidth() >> 1);
                this.arrowDrawable.setAlpha((int) (255.0f * alpha));
                Drawable drawable = this.arrowDrawable;
                drawable.setBounds(x, cY - (drawable.getIntrinsicHeight() / 2), this.arrowDrawable.getIntrinsicWidth() + x, (this.arrowDrawable.getIntrinsicHeight() / 2) + cY);
                this.arrowDrawable.draw(canvas);
                canvas.restore();
                int x2 = (int) (getMeasuredWidth() - (startX + ((((endX - startX) - lineLength) / 3.0f) * i)));
                canvas.save();
                canvas.clipRect(getMeasuredWidth() >> 1, 0.0f, ((this.rigthOffsetX + getMeasuredWidth()) - AndroidUtilities.dp(46.0f)) - (this.buttonWidth / 2), getMeasuredHeight());
                canvas.rotate(180.0f, x2 - (this.arrowDrawable.getIntrinsicWidth() / 2.0f), cY);
                Drawable drawable2 = this.arrowDrawable;
                drawable2.setBounds(x2 - drawable2.getIntrinsicWidth(), cY - (this.arrowDrawable.getIntrinsicHeight() / 2), x2, (this.arrowDrawable.getIntrinsicHeight() / 2) + cY);
                this.arrowDrawable.draw(canvas);
                canvas.restore();
                i++;
                startX2 = startX;
                f = 1.0f;
            }
            invalidate();
        }
        this.bigRadius += AndroidUtilities.dp(8.0f) * 0.005f;
        canvas.save();
        canvas.translate(0.0f, AndroidUtilities.dp(40.0f));
        canvas.save();
        canvas.translate(this.leftOffsetX + AndroidUtilities.dp(46.0f), 0.0f);
        this.declineDrawable.draw(canvas);
        canvas.save();
        canvas.translate((this.buttonWidth / 2.0f) - (this.declineLayout.getWidth() / 2.0f), this.buttonWidth + AndroidUtilities.dp(8.0f));
        this.declineLayout.draw(canvas);
        this.declineRect.set(AndroidUtilities.dp(46.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(46.0f) + this.buttonWidth, AndroidUtilities.dp(40.0f) + this.buttonWidth);
        canvas.restore();
        if (this.retryMod) {
            this.cancelDrawable.draw(canvas);
        } else {
            this.callDrawable.draw(canvas);
        }
        if (this.leftDrag) {
            this.rippleDrawable.setBounds(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f));
            this.rippleDrawable.draw(canvas);
        }
        canvas.restore();
        canvas.save();
        canvas.translate(((this.rigthOffsetX + getMeasuredWidth()) - AndroidUtilities.dp(46.0f)) - this.buttonWidth, 0.0f);
        if (!this.retryMod) {
            int i2 = this.buttonWidth;
            canvas.drawCircle(i2 / 2.0f, i2 / 2.0f, ((i2 / 2.0f) - AndroidUtilities.dp(4.0f)) + this.bigRadius, this.acceptCirclePaint);
            int i3 = this.buttonWidth;
            canvas.drawCircle(i3 / 2.0f, i3 / 2.0f, ((i3 / 2.0f) - AndroidUtilities.dp(4.0f)) + this.smallRadius, this.acceptCirclePaint);
        }
        this.acceptDrawable.draw(canvas);
        this.acceptRect.set((getMeasuredWidth() - AndroidUtilities.dp(46.0f)) - this.buttonWidth, AndroidUtilities.dp(40.0f), getMeasuredWidth() - AndroidUtilities.dp(46.0f), AndroidUtilities.dp(40.0f) + this.buttonWidth);
        if (this.retryMod) {
            canvas.save();
            canvas.translate((this.buttonWidth / 2.0f) - (this.retryLayout.getWidth() / 2.0f), this.buttonWidth + AndroidUtilities.dp(8.0f));
            this.retryLayout.draw(canvas);
            canvas.restore();
        } else {
            canvas.save();
            canvas.translate((this.buttonWidth / 2.0f) - (this.acceptLayout.getWidth() / 2.0f), this.buttonWidth + AndroidUtilities.dp(8.0f));
            this.acceptLayout.draw(canvas);
            canvas.restore();
        }
        canvas.save();
        canvas.translate(-AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
        canvas.rotate(-135.0f, this.callDrawable.getBounds().centerX(), this.callDrawable.getBounds().centerY());
        this.callDrawable.draw(canvas);
        canvas.restore();
        if (!this.leftDrag) {
            this.rippleDrawable.setBounds(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f));
            this.rippleDrawable.draw(canvas);
        }
        canvas.restore();
        canvas.restore();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setRetryMod(boolean retryMod) {
        this.retryMod = retryMod;
        if (retryMod) {
            this.declineDrawable.setColor(-1);
            this.screenWasWakeup = false;
            return;
        }
        this.declineDrawable.setColor(-1696188);
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.rippleDrawable.setState(getDrawableState());
    }

    @Override // android.view.View
    public boolean verifyDrawable(Drawable drawable) {
        return this.rippleDrawable == drawable || super.verifyDrawable(drawable);
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent event) {
        AcceptDeclineAccessibilityNodeProvider acceptDeclineAccessibilityNodeProvider = this.accessibilityNodeProvider;
        if (acceptDeclineAccessibilityNodeProvider != null && acceptDeclineAccessibilityNodeProvider.onHoverEvent(event)) {
            return true;
        }
        return super.onHoverEvent(event);
    }

    @Override // android.view.View
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        if (this.accessibilityNodeProvider == null) {
            this.accessibilityNodeProvider = new AcceptDeclineAccessibilityNodeProvider(this, 2) { // from class: org.telegram.ui.Components.voip.AcceptDeclineView.1
                private static final int ACCEPT_VIEW_ID = 0;
                private static final int DECLINE_VIEW_ID = 1;
                private final int[] coords = {0, 0};

                @Override // org.telegram.ui.Components.voip.AcceptDeclineView.AcceptDeclineAccessibilityNodeProvider
                protected CharSequence getVirtualViewText(int virtualViewId) {
                    if (virtualViewId == 0) {
                        if (AcceptDeclineView.this.retryMod) {
                            if (AcceptDeclineView.this.retryLayout != null) {
                                return AcceptDeclineView.this.retryLayout.getText();
                            }
                            return null;
                        } else if (AcceptDeclineView.this.acceptLayout != null) {
                            return AcceptDeclineView.this.acceptLayout.getText();
                        } else {
                            return null;
                        }
                    } else if (virtualViewId == 1 && AcceptDeclineView.this.declineLayout != null) {
                        return AcceptDeclineView.this.declineLayout.getText();
                    } else {
                        return null;
                    }
                }

                @Override // org.telegram.ui.Components.voip.AcceptDeclineView.AcceptDeclineAccessibilityNodeProvider
                protected void getVirtualViewBoundsInScreen(int virtualViewId, Rect outRect) {
                    getVirtualViewBoundsInParent(virtualViewId, outRect);
                    AcceptDeclineView.this.getLocationOnScreen(this.coords);
                    int[] iArr = this.coords;
                    outRect.offset(iArr[0], iArr[1]);
                }

                @Override // org.telegram.ui.Components.voip.AcceptDeclineView.AcceptDeclineAccessibilityNodeProvider
                protected void getVirtualViewBoundsInParent(int virtualViewId, Rect outRect) {
                    if (virtualViewId == 0) {
                        outRect.set(AcceptDeclineView.this.acceptRect);
                    } else if (virtualViewId == 1) {
                        outRect.set(AcceptDeclineView.this.declineRect);
                    } else {
                        outRect.setEmpty();
                    }
                }

                @Override // org.telegram.ui.Components.voip.AcceptDeclineView.AcceptDeclineAccessibilityNodeProvider
                protected void onVirtualViewClick(int virtualViewId) {
                    if (AcceptDeclineView.this.listener != null) {
                        if (virtualViewId == 0) {
                            AcceptDeclineView.this.listener.onAccept();
                        } else if (virtualViewId == 1) {
                            AcceptDeclineView.this.listener.onDecline();
                        }
                    }
                }
            };
        }
        return this.accessibilityNodeProvider;
    }

    public void setScreenWasWakeup(boolean screenWasWakeup) {
        this.screenWasWakeup = screenWasWakeup;
    }

    /* loaded from: classes5.dex */
    public static abstract class AcceptDeclineAccessibilityNodeProvider extends AccessibilityNodeProvider {
        private final AccessibilityManager accessibilityManager;
        private final View hostView;
        private final int virtualViewsCount;
        private final Rect rect = new Rect();
        private int currentFocusedVirtualViewId = -1;

        protected abstract void getVirtualViewBoundsInParent(int i, Rect rect);

        protected abstract void getVirtualViewBoundsInScreen(int i, Rect rect);

        protected abstract CharSequence getVirtualViewText(int i);

        protected abstract void onVirtualViewClick(int i);

        protected AcceptDeclineAccessibilityNodeProvider(View hostView, int virtualViewsCount) {
            this.hostView = hostView;
            this.virtualViewsCount = virtualViewsCount;
            this.accessibilityManager = (AccessibilityManager) ContextCompat.getSystemService(hostView.getContext(), AccessibilityManager.class);
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public AccessibilityNodeInfo createAccessibilityNodeInfo(int virtualViewId) {
            AccessibilityNodeInfo nodeInfo;
            if (virtualViewId == -1) {
                nodeInfo = AccessibilityNodeInfo.obtain(this.hostView);
                nodeInfo.setPackageName(this.hostView.getContext().getPackageName());
                for (int i = 0; i < this.virtualViewsCount; i++) {
                    nodeInfo.addChild(this.hostView, i);
                }
            } else {
                nodeInfo = AccessibilityNodeInfo.obtain(this.hostView, virtualViewId);
                nodeInfo.setPackageName(this.hostView.getContext().getPackageName());
                if (Build.VERSION.SDK_INT >= 21) {
                    nodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                }
                nodeInfo.setText(getVirtualViewText(virtualViewId));
                nodeInfo.setClassName(Button.class.getName());
                if (Build.VERSION.SDK_INT >= 24) {
                    nodeInfo.setImportantForAccessibility(true);
                }
                nodeInfo.setVisibleToUser(true);
                nodeInfo.setClickable(true);
                nodeInfo.setEnabled(true);
                nodeInfo.setParent(this.hostView);
                getVirtualViewBoundsInScreen(virtualViewId, this.rect);
                nodeInfo.setBoundsInScreen(this.rect);
            }
            return nodeInfo;
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public boolean performAction(int virtualViewId, int action, Bundle arguments) {
            if (virtualViewId == -1) {
                return this.hostView.performAccessibilityAction(action, arguments);
            }
            if (action == 64) {
                sendAccessibilityEventForVirtualView(virtualViewId, 32768);
                return false;
            } else if (action == 16) {
                onVirtualViewClick(virtualViewId);
                return true;
            } else {
                return false;
            }
        }

        public boolean onHoverEvent(MotionEvent event) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (event.getAction() == 9 || event.getAction() == 7) {
                for (int i = 0; i < this.virtualViewsCount; i++) {
                    getVirtualViewBoundsInParent(i, this.rect);
                    if (this.rect.contains(x, y)) {
                        if (i != this.currentFocusedVirtualViewId) {
                            this.currentFocusedVirtualViewId = i;
                            sendAccessibilityEventForVirtualView(i, 32768);
                        }
                        return true;
                    }
                }
                return false;
            } else if (event.getAction() == 10 && this.currentFocusedVirtualViewId != -1) {
                this.currentFocusedVirtualViewId = -1;
                return true;
            } else {
                return false;
            }
        }

        private void sendAccessibilityEventForVirtualView(int virtualViewId, int eventType) {
            ViewParent parent;
            if (this.accessibilityManager.isTouchExplorationEnabled() && (parent = this.hostView.getParent()) != null) {
                AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
                event.setPackageName(this.hostView.getContext().getPackageName());
                event.setSource(this.hostView, virtualViewId);
                parent.requestSendAccessibilityEvent(this.hostView, event);
            }
        }
    }
}
