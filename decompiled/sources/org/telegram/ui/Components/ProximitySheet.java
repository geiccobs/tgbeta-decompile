package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.ItemTouchHelper;
import com.google.android.exoplayer2.C;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.ProximitySheet;
/* loaded from: classes5.dex */
public class ProximitySheet extends FrameLayout {
    private int backgroundPaddingLeft;
    private int backgroundPaddingTop;
    private TextView buttonTextView;
    private ViewGroup containerView;
    private AnimatorSet currentSheetAnimation;
    private int currentSheetAnimationType;
    private TLRPC.User currentUser;
    private LinearLayout customView;
    private boolean dismissed;
    private TextView infoTextView;
    private NumberPicker kmPicker;
    private NumberPicker mPicker;
    private Runnable onDismissCallback;
    private onRadiusPickerChange onRadiusChange;
    private boolean radiusSet;
    private int startedTrackingX;
    private int startedTrackingY;
    private int totalWidth;
    private int touchSlop;
    private boolean useFastDismiss;
    private VelocityTracker velocityTracker = null;
    private int startedTrackingPointerId = -1;
    private boolean maybeStartTracking = false;
    private boolean startedTracking = false;
    private AnimatorSet currentAnimation = null;
    private android.graphics.Rect rect = new android.graphics.Rect();
    private Paint backgroundPaint = new Paint();
    private boolean useHardwareLayer = true;
    private Interpolator openInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
    private boolean useImperialSystem = LocaleController.getUseImperialSystemType();

    /* loaded from: classes5.dex */
    public interface onRadiusPickerChange {
        boolean run(boolean z, int i);
    }

    public ProximitySheet(Context context, TLRPC.User user, onRadiusPickerChange onRadius, final onRadiusPickerChange onFinish, Runnable onDismiss) {
        super(context);
        setWillNotDraw(false);
        this.onDismissCallback = onDismiss;
        ViewConfiguration vc = ViewConfiguration.get(context);
        this.touchSlop = vc.getScaledTouchSlop();
        android.graphics.Rect padding = new android.graphics.Rect();
        Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        shadowDrawable.getPadding(padding);
        this.backgroundPaddingLeft = padding.left;
        FrameLayout frameLayout = new FrameLayout(getContext()) { // from class: org.telegram.ui.Components.ProximitySheet.1
            @Override // android.view.View
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.containerView = frameLayout;
        frameLayout.setBackgroundDrawable(shadowDrawable);
        this.containerView.setPadding(this.backgroundPaddingLeft, (AndroidUtilities.dp(8.0f) + padding.top) - 1, this.backgroundPaddingLeft, 0);
        this.containerView.setVisibility(4);
        addView(this.containerView, 0, LayoutHelper.createFrame(-1, -2, 80));
        this.currentUser = user;
        this.onRadiusChange = onRadius;
        NumberPicker numberPicker = new NumberPicker(context);
        this.kmPicker = numberPicker;
        numberPicker.setTextOffset(AndroidUtilities.dp(10.0f));
        this.kmPicker.setItemCount(5);
        NumberPicker numberPicker2 = new NumberPicker(context);
        this.mPicker = numberPicker2;
        numberPicker2.setItemCount(5);
        this.mPicker.setTextOffset(-AndroidUtilities.dp(10.0f));
        LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Components.ProximitySheet.2
            boolean ignoreLayout = false;

            @Override // android.widget.LinearLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int count;
                this.ignoreLayout = true;
                if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                    count = 3;
                } else {
                    count = 5;
                }
                ProximitySheet.this.kmPicker.setItemCount(count);
                ProximitySheet.this.mPicker.setItemCount(count);
                ProximitySheet.this.kmPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                ProximitySheet.this.mPicker.getLayoutParams().height = AndroidUtilities.dp(54.0f) * count;
                this.ignoreLayout = false;
                ProximitySheet.this.totalWidth = View.MeasureSpec.getSize(widthMeasureSpec);
                if (0 != ProximitySheet.this.totalWidth) {
                    ProximitySheet.this.updateText(false, false);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.customView = linearLayout;
        linearLayout.setOrientation(1);
        FrameLayout titleLayout = new FrameLayout(context);
        this.customView.addView(titleLayout, LayoutHelper.createLinear(-1, -2, 51, 22, 0, 0, 4));
        TextView titleView = new TextView(context);
        titleView.setText(LocaleController.getString("LocationNotifiation", R.string.LocationNotifiation));
        titleView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        titleView.setTextSize(1, 20.0f);
        titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        titleLayout.addView(titleView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 12.0f, 0.0f, 0.0f));
        titleView.setOnTouchListener(ProximitySheet$$ExternalSyntheticLambda1.INSTANCE);
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(0);
        linearLayout2.setWeightSum(1.0f);
        this.customView.addView(linearLayout2, LayoutHelper.createLinear(-1, -2));
        System.currentTimeMillis();
        FrameLayout buttonContainer = new FrameLayout(context);
        this.infoTextView = new TextView(context);
        this.buttonTextView = new TextView(context) { // from class: org.telegram.ui.Components.ProximitySheet.3
            @Override // android.widget.TextView, android.view.View
            public CharSequence getAccessibilityClassName() {
                return Button.class.getName();
            }
        };
        linearLayout2.addView(this.kmPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        this.kmPicker.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.Components.ProximitySheet$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.NumberPicker.Formatter
            public final String format(int i) {
                return ProximitySheet.this.m2911lambda$new$1$orgtelegramuiComponentsProximitySheet(i);
            }
        });
        this.kmPicker.setMinValue(0);
        this.kmPicker.setMaxValue(10);
        this.kmPicker.setWrapSelectorWheel(false);
        this.kmPicker.setTextOffset(AndroidUtilities.dp(20.0f));
        NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() { // from class: org.telegram.ui.Components.ProximitySheet$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.NumberPicker.OnValueChangeListener
            public final void onValueChange(NumberPicker numberPicker3, int i, int i2) {
                ProximitySheet.this.m2912lambda$new$2$orgtelegramuiComponentsProximitySheet(numberPicker3, i, i2);
            }
        };
        this.kmPicker.setOnValueChangedListener(onValueChangeListener);
        this.mPicker.setMinValue(0);
        this.mPicker.setMaxValue(10);
        this.mPicker.setWrapSelectorWheel(false);
        this.mPicker.setTextOffset(-AndroidUtilities.dp(20.0f));
        linearLayout2.addView(this.mPicker, LayoutHelper.createLinear(0, 270, 0.5f));
        this.mPicker.setFormatter(new NumberPicker.Formatter() { // from class: org.telegram.ui.Components.ProximitySheet$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.NumberPicker.Formatter
            public final String format(int i) {
                return ProximitySheet.this.m2913lambda$new$3$orgtelegramuiComponentsProximitySheet(i);
            }
        });
        this.mPicker.setOnValueChangedListener(onValueChangeListener);
        this.kmPicker.setValue(0);
        this.mPicker.setValue(6);
        this.customView.addView(buttonContainer, LayoutHelper.createLinear(-1, 48, 83, 16, 15, 16, 16));
        this.buttonTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setMaxLines(2);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.buttonTextView.setBackgroundDrawable(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
        buttonContainer.addView(this.buttonTextView, LayoutHelper.createFrame(-1, 48.0f));
        this.buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ProximitySheet$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ProximitySheet.this.m2914lambda$new$4$orgtelegramuiComponentsProximitySheet(onFinish, view);
            }
        });
        this.infoTextView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.infoTextView.setGravity(17);
        this.infoTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
        this.infoTextView.setTextSize(1, 14.0f);
        this.infoTextView.setAlpha(0.0f);
        this.infoTextView.setScaleX(0.5f);
        this.infoTextView.setScaleY(0.5f);
        buttonContainer.addView(this.infoTextView, LayoutHelper.createFrame(-1, 48.0f));
        this.containerView.addView(this.customView, LayoutHelper.createFrame(-1, -2, 51));
    }

    public static /* synthetic */ boolean lambda$new$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ProximitySheet */
    public /* synthetic */ String m2911lambda$new$1$orgtelegramuiComponentsProximitySheet(int value) {
        if (this.useImperialSystem) {
            return LocaleController.formatString("MilesShort", R.string.MilesShort, Integer.valueOf(value));
        }
        return LocaleController.formatString("KMetersShort", R.string.KMetersShort, Integer.valueOf(value));
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ProximitySheet */
    public /* synthetic */ void m2912lambda$new$2$orgtelegramuiComponentsProximitySheet(NumberPicker picker, int oldVal, int newVal) {
        try {
            performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        updateText(true, true);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ProximitySheet */
    public /* synthetic */ String m2913lambda$new$3$orgtelegramuiComponentsProximitySheet(int value) {
        if (this.useImperialSystem) {
            if (value == 1) {
                return LocaleController.formatString("FootsShort", R.string.FootsShort, Integer.valueOf((int) ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION));
            }
            if (value > 1) {
                value--;
            }
            return String.format(Locale.US, ".%d", Integer.valueOf(value));
        } else if (value == 1) {
            return LocaleController.formatString("MetersShort", R.string.MetersShort, 50);
        } else {
            if (value > 1) {
                value--;
            }
            return LocaleController.formatString("MetersShort", R.string.MetersShort, Integer.valueOf(value * 100));
        }
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ProximitySheet */
    public /* synthetic */ void m2914lambda$new$4$orgtelegramuiComponentsProximitySheet(onRadiusPickerChange onFinish, View v) {
        if (this.buttonTextView.getTag() != null) {
            return;
        }
        float value = getValue();
        if (onFinish.run(true, (int) Math.max(1.0f, value))) {
            dismiss();
        }
    }

    public View getCustomView() {
        return this.customView;
    }

    public float getValue() {
        float value;
        float value2 = this.kmPicker.getValue() * 1000;
        int second = this.mPicker.getValue();
        boolean z = this.useImperialSystem;
        if (z) {
            if (second == 1) {
                value = value2 + 47.349f;
            } else {
                if (second > 1) {
                    second--;
                }
                value = value2 + (second * 100);
            }
        } else if (second == 1) {
            value = value2 + 50.0f;
        } else {
            if (second > 1) {
                second--;
            }
            value = value2 + (second * 100);
        }
        if (z) {
            return value * 1.60934f;
        }
        return value;
    }

    public boolean getRadiusSet() {
        return this.radiusSet;
    }

    public void setRadiusSet() {
        this.radiusSet = true;
    }

    public void updateText(boolean move, boolean animated) {
        float value = getValue();
        String distance = LocaleController.formatDistance(value, 2, Boolean.valueOf(this.useImperialSystem));
        if (this.onRadiusChange.run(move, (int) value) || this.currentUser == null) {
            if (this.currentUser == null) {
                this.buttonTextView.setText(LocaleController.formatString("LocationNotifiationButtonGroup", R.string.LocationNotifiationButtonGroup, distance));
            } else {
                String format = LocaleController.getString("LocationNotifiationButtonUser", R.string.LocationNotifiationButtonUser);
                int width = (int) Math.ceil(this.buttonTextView.getPaint().measureText(format));
                int restWidth = (int) (((this.totalWidth - AndroidUtilities.dp(94.0f)) * 1.5f) - width);
                CharSequence name = TextUtils.ellipsize(UserObject.getFirstName(this.currentUser), this.buttonTextView.getPaint(), Math.max(AndroidUtilities.dp(10.0f), restWidth), TextUtils.TruncateAt.END);
                this.buttonTextView.setText(LocaleController.formatString("LocationNotifiationButtonUser", R.string.LocationNotifiationButtonUser, name, distance));
            }
            if (this.buttonTextView.getTag() != null) {
                this.buttonTextView.setTag(null);
                this.buttonTextView.animate().setDuration(180L).alpha(1.0f).scaleX(1.0f).scaleY(1.0f).start();
                this.infoTextView.animate().setDuration(180L).alpha(0.0f).scaleX(0.5f).scaleY(0.5f).start();
                return;
            }
            return;
        }
        this.infoTextView.setText(LocaleController.formatString("LocationNotifiationCloser", R.string.LocationNotifiationCloser, distance));
        if (this.buttonTextView.getTag() == null) {
            this.buttonTextView.setTag(1);
            this.buttonTextView.animate().setDuration(180L).alpha(0.0f).scaleX(0.5f).scaleY(0.5f).start();
            this.infoTextView.animate().setDuration(180L).alpha(1.0f).scaleX(1.0f).scaleY(1.0f).start();
        }
    }

    private void checkDismiss(float velX, float velY) {
        float translationY = this.containerView.getTranslationY();
        boolean backAnimation = (translationY < AndroidUtilities.getPixelsInCM(0.8f, false) && (velY < 3500.0f || Math.abs(velY) < Math.abs(velX))) || (velY < 0.0f && Math.abs(velY) >= 3500.0f);
        if (!backAnimation) {
            this.useFastDismiss = true;
            dismiss();
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        this.currentAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, 0.0f));
        this.currentAnimation.setDuration((int) ((Math.max(0.0f, translationY) / AndroidUtilities.getPixelsInCM(0.8f, false)) * 150.0f));
        this.currentAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.currentAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ProximitySheet.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ProximitySheet.this.currentAnimation != null && ProximitySheet.this.currentAnimation.equals(animation)) {
                    ProximitySheet.this.currentAnimation = null;
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
            }
        });
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentAnimation.start();
    }

    private void cancelCurrentAnimation() {
        AnimatorSet animatorSet = this.currentAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentAnimation = null;
        }
    }

    boolean processTouchEvent(MotionEvent ev, boolean intercept) {
        if (this.dismissed) {
            return false;
        }
        if (ev == null || ((ev.getAction() != 0 && ev.getAction() != 2) || this.startedTracking || this.maybeStartTracking || ev.getPointerCount() != 1)) {
            if (ev != null && ev.getAction() == 2 && ev.getPointerId(0) == this.startedTrackingPointerId) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                float dx = Math.abs((int) (ev.getX() - this.startedTrackingX));
                float dy = ((int) ev.getY()) - this.startedTrackingY;
                this.velocityTracker.addMovement(ev);
                if (this.maybeStartTracking && !this.startedTracking && dy > 0.0f && dy / 3.0f > Math.abs(dx) && Math.abs(dy) >= this.touchSlop) {
                    this.startedTrackingY = (int) ev.getY();
                    this.maybeStartTracking = false;
                    this.startedTracking = true;
                    requestDisallowInterceptTouchEvent(true);
                } else if (this.startedTracking) {
                    float translationY = this.containerView.getTranslationY();
                    float translationY2 = translationY + dy;
                    if (translationY2 < 0.0f) {
                        translationY2 = 0.0f;
                    }
                    this.containerView.setTranslationY(translationY2);
                    this.startedTrackingY = (int) ev.getY();
                }
            } else if (ev == null || (ev.getPointerId(0) == this.startedTrackingPointerId && (ev.getAction() == 3 || ev.getAction() == 1 || ev.getAction() == 6))) {
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.computeCurrentVelocity(1000);
                float translationY3 = this.containerView.getTranslationY();
                if (this.startedTracking || translationY3 != 0.0f) {
                    checkDismiss(this.velocityTracker.getXVelocity(), this.velocityTracker.getYVelocity());
                    this.startedTracking = false;
                } else {
                    this.maybeStartTracking = false;
                    this.startedTracking = false;
                }
                VelocityTracker velocityTracker = this.velocityTracker;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    this.velocityTracker = null;
                }
                this.startedTrackingPointerId = -1;
            }
        } else {
            this.startedTrackingX = (int) ev.getX();
            int y = (int) ev.getY();
            this.startedTrackingY = y;
            if (y < this.containerView.getTop() || this.startedTrackingX < this.containerView.getLeft() || this.startedTrackingX > this.containerView.getRight()) {
                requestDisallowInterceptTouchEvent(true);
                dismiss();
                return true;
            }
            this.startedTrackingPointerId = ev.getPointerId(0);
            this.maybeStartTracking = true;
            cancelCurrentAnimation();
            VelocityTracker velocityTracker2 = this.velocityTracker;
            if (velocityTracker2 != null) {
                velocityTracker2.clear();
            }
        }
        return (!intercept && this.maybeStartTracking) || this.startedTracking;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        return this.dismissed || processTouchEvent(ev, false);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        setMeasuredDimension(width, height);
        this.containerView.measure(View.MeasureSpec.makeMeasureSpec((this.backgroundPaddingLeft * 2) + width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, Integer.MIN_VALUE));
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8 && child != this.containerView) {
                measureChildWithMargins(child, View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), 0, View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED), 0);
            }
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childLeft;
        int childTop;
        int t = (bottom - top) - this.containerView.getMeasuredHeight();
        int l = ((right - left) - this.containerView.getMeasuredWidth()) / 2;
        ViewGroup viewGroup = this.containerView;
        viewGroup.layout(l, t, viewGroup.getMeasuredWidth() + l, this.containerView.getMeasuredHeight() + t);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8 && child != this.containerView) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                int gravity = lp.gravity;
                if (gravity == -1) {
                    gravity = 51;
                }
                int absoluteGravity = gravity & 7;
                int verticalGravity = gravity & 112;
                switch (absoluteGravity & 7) {
                    case 1:
                        childLeft = ((((right - left) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                        break;
                    case 5:
                        childLeft = (right - width) - lp.rightMargin;
                        break;
                    default:
                        childLeft = lp.leftMargin;
                        break;
                }
                switch (verticalGravity) {
                    case 16:
                        childTop = ((((bottom - top) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                        break;
                    case UndoView.ACTION_EMAIL_COPIED /* 80 */:
                        childTop = ((bottom - top) - height) - lp.bottomMargin;
                        break;
                    default:
                        childTop = lp.topMargin;
                        break;
                }
                child.layout(childLeft, childTop, childLeft + width, childTop + height);
            }
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.dismissed || processTouchEvent(event, true);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (this.maybeStartTracking && !this.startedTracking) {
            onTouchEvent(null);
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public void show() {
        this.dismissed = false;
        cancelSheetAnimation();
        this.containerView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x + (this.backgroundPaddingLeft * 2), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        startOpenAnimation();
        updateText(true, false);
    }

    private void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.currentSheetAnimation = null;
            this.currentSheetAnimationType = 0;
        }
    }

    private void startOpenAnimation() {
        if (this.dismissed) {
            return;
        }
        this.containerView.setVisibility(0);
        if (Build.VERSION.SDK_INT >= 20 && this.useHardwareLayer) {
            setLayerType(2, null);
        }
        ViewGroup viewGroup = this.containerView;
        viewGroup.setTranslationY(viewGroup.getMeasuredHeight());
        this.currentSheetAnimationType = 1;
        AnimatorSet animatorSet = new AnimatorSet();
        this.currentSheetAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, 0.0f));
        this.currentSheetAnimation.setDuration(400L);
        this.currentSheetAnimation.setStartDelay(20L);
        this.currentSheetAnimation.setInterpolator(this.openInterpolator);
        this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ProximitySheet.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animation)) {
                    ProximitySheet.this.currentSheetAnimation = null;
                    ProximitySheet.this.currentSheetAnimationType = 0;
                    if (ProximitySheet.this.useHardwareLayer) {
                        ProximitySheet.this.setLayerType(0, null);
                    }
                }
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animation)) {
                    ProximitySheet.this.currentSheetAnimation = null;
                    ProximitySheet.this.currentSheetAnimationType = 0;
                }
            }
        });
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentSheetAnimation.start();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.dismissed) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void dismiss() {
        if (this.dismissed) {
            return;
        }
        this.dismissed = true;
        cancelSheetAnimation();
        this.currentSheetAnimationType = 2;
        AnimatorSet animatorSet = new AnimatorSet();
        this.currentSheetAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.containerView, View.TRANSLATION_Y, this.containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f)));
        if (this.useFastDismiss) {
            int height = this.containerView.getMeasuredHeight();
            this.currentSheetAnimation.setDuration(Math.max(60, (int) (((height - this.containerView.getTranslationY()) * 250.0f) / height)));
            this.useFastDismiss = false;
        } else {
            this.currentSheetAnimation.setDuration(250L);
        }
        this.currentSheetAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.currentSheetAnimation.addListener(new AnonymousClass6());
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentSheetAnimation.start();
    }

    /* renamed from: org.telegram.ui.Components.ProximitySheet$6 */
    /* loaded from: classes5.dex */
    public class AnonymousClass6 extends AnimatorListenerAdapter {
        AnonymousClass6() {
            ProximitySheet.this = this$0;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animation) {
            if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animation)) {
                ProximitySheet.this.currentSheetAnimation = null;
                ProximitySheet.this.currentSheetAnimationType = 0;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ProximitySheet$6$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ProximitySheet.AnonymousClass6.this.m2915xb0bca8e();
                    }
                });
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
        }

        /* renamed from: lambda$onAnimationEnd$0$org-telegram-ui-Components-ProximitySheet$6 */
        public /* synthetic */ void m2915xb0bca8e() {
            try {
                ProximitySheet.this.dismissInternal();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animation) {
            if (ProximitySheet.this.currentSheetAnimation != null && ProximitySheet.this.currentSheetAnimation.equals(animation)) {
                ProximitySheet.this.currentSheetAnimation = null;
                ProximitySheet.this.currentSheetAnimationType = 0;
            }
        }
    }

    public void dismissInternal() {
        if (getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) getParent();
            parent.removeView(this);
        }
        this.onDismissCallback.run();
    }
}
