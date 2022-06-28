package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
/* loaded from: classes4.dex */
public class AlertDialog extends Dialog implements Drawable.Callback {
    private float aspectRatio;
    private Rect backgroundPaddings;
    protected ViewGroup buttonsLayout;
    private boolean canCacnel;
    private AlertDialog cancelDialog;
    private boolean checkFocusable;
    private ScrollView contentScrollView;
    private int currentProgress;
    private View customView;
    private int customViewHeight;
    private int customViewOffset;
    private String dialogButtonColorKey;
    private float dimAlpha;
    private boolean dimEnabled;
    private boolean dismissDialogByButtons;
    private Runnable dismissRunnable;
    private boolean drawBackground;
    private boolean focusable;
    private int[] itemIcons;
    private ArrayList<AlertDialogCell> itemViews;
    private CharSequence[] items;
    private int lastScreenWidth;
    private LineProgressView lineProgressView;
    private TextView lineProgressViewPercent;
    private CharSequence message;
    private TextView messageTextView;
    private boolean messageTextViewClickable;
    private DialogInterface.OnClickListener negativeButtonListener;
    private CharSequence negativeButtonText;
    private DialogInterface.OnClickListener neutralButtonListener;
    private CharSequence neutralButtonText;
    private boolean notDrawBackgroundOnTopView;
    private DialogInterface.OnClickListener onBackButtonListener;
    private DialogInterface.OnCancelListener onCancelListener;
    private DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onDismissListener;
    private ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;
    private DialogInterface.OnClickListener positiveButtonListener;
    private CharSequence positiveButtonText;
    private FrameLayout progressViewContainer;
    private int progressViewStyle;
    private TextView progressViewTextView;
    private final Theme.ResourcesProvider resourcesProvider;
    private LinearLayout scrollContainer;
    private CharSequence secondTitle;
    private TextView secondTitleTextView;
    private BitmapDrawable[] shadow;
    private AnimatorSet[] shadowAnimation;
    private Drawable shadowDrawable;
    private boolean[] shadowVisibility;
    private Runnable showRunnable;
    private CharSequence subtitle;
    private TextView subtitleTextView;
    private CharSequence title;
    private FrameLayout titleContainer;
    private TextView titleTextView;
    private boolean topAnimationAutoRepeat;
    private int topAnimationId;
    private int topAnimationSize;
    private int topBackgroundColor;
    private Drawable topDrawable;
    private int topHeight;
    private RLottieImageView topImageView;
    private int topResId;
    private View topView;
    private boolean verticalButtons;

    /* renamed from: lambda$new$0$org-telegram-ui-ActionBar-AlertDialog */
    public /* synthetic */ void m1403lambda$new$0$orgtelegramuiActionBarAlertDialog() {
        if (isShowing()) {
            return;
        }
        try {
            show();
        } catch (Exception e) {
        }
    }

    /* loaded from: classes4.dex */
    public static class AlertDialogCell extends FrameLayout {
        private ImageView imageView;
        private final Theme.ResourcesProvider resourcesProvider;
        private TextView textView;

        public AlertDialogCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_dialogButtonSelector), 2));
            setPadding(AndroidUtilities.dp(23.0f), 0, AndroidUtilities.dp(23.0f), 0);
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogIcon), PorterDuff.Mode.MULTIPLY));
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(-2, 40, (LocaleController.isRTL ? 5 : 3) | 16));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
            this.textView.setTextSize(1, 16.0f);
            addView(this.textView, LayoutHelper.createFrame(-2, -2, (!LocaleController.isRTL ? 3 : i) | 16));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setTextColor(int color) {
            this.textView.setTextColor(color);
        }

        public void setGravity(int gravity) {
            this.textView.setGravity(gravity);
        }

        public void setTextAndIcon(CharSequence text, int icon) {
            this.textView.setText(text);
            if (icon != 0) {
                this.imageView.setImageResource(icon);
                this.imageView.setVisibility(0);
                this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(56.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(56.0f) : 0, 0);
                return;
            }
            this.imageView.setVisibility(4);
            this.textView.setPadding(0, 0, 0, 0);
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    public AlertDialog(Context context, int progressStyle) {
        this(context, progressStyle, null);
    }

    public AlertDialog(Context context, int progressStyle, Theme.ResourcesProvider resourcesProvider) {
        super(context, R.style.TransparentDialog);
        this.customViewHeight = -2;
        this.shadow = new BitmapDrawable[2];
        this.shadowVisibility = new boolean[2];
        this.shadowAnimation = new AnimatorSet[2];
        this.customViewOffset = 20;
        this.dialogButtonColorKey = Theme.key_dialogButton;
        this.topHeight = 132;
        this.messageTextViewClickable = true;
        this.canCacnel = true;
        this.dismissDialogByButtons = true;
        this.checkFocusable = true;
        this.dismissRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.AlertDialog$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                AlertDialog.this.dismiss();
            }
        };
        this.showRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.AlertDialog$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                AlertDialog.this.m1403lambda$new$0$orgtelegramuiActionBarAlertDialog();
            }
        };
        this.itemViews = new ArrayList<>();
        this.dimEnabled = true;
        this.dimAlpha = 0.6f;
        this.topAnimationAutoRepeat = true;
        this.resourcesProvider = resourcesProvider;
        this.backgroundPaddings = new Rect();
        if (progressStyle != 3) {
            Drawable mutate = context.getResources().getDrawable(R.drawable.popup_fixed_alert).mutate();
            this.shadowDrawable = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
            this.shadowDrawable.getPadding(this.backgroundPaddings);
        }
        this.progressViewStyle = progressStyle;
    }

    @Override // android.app.Dialog
    protected void onCreate(Bundle savedInstanceState) {
        int maxWidth;
        int i;
        super.onCreate(savedInstanceState);
        LinearLayout containerView = new AnonymousClass1(getContext());
        containerView.setOrientation(1);
        if (this.progressViewStyle == 3) {
            containerView.setBackgroundDrawable(null);
            containerView.setPadding(0, 0, 0, 0);
            this.drawBackground = false;
        } else if (this.notDrawBackgroundOnTopView) {
            Rect rect = new Rect();
            this.shadowDrawable.getPadding(rect);
            containerView.setPadding(rect.left, rect.top, rect.right, rect.bottom);
            this.drawBackground = true;
        } else {
            containerView.setBackgroundDrawable(null);
            containerView.setPadding(0, 0, 0, 0);
            containerView.setBackgroundDrawable(this.shadowDrawable);
            this.drawBackground = false;
        }
        containerView.setFitsSystemWindows(Build.VERSION.SDK_INT >= 21);
        setContentView(containerView);
        boolean hasButtons = (this.positiveButtonText == null && this.negativeButtonText == null && this.neutralButtonText == null) ? false : true;
        if (this.topResId != 0 || this.topAnimationId != 0 || this.topDrawable != null) {
            RLottieImageView rLottieImageView = new RLottieImageView(getContext());
            this.topImageView = rLottieImageView;
            Drawable drawable = this.topDrawable;
            if (drawable != null) {
                rLottieImageView.setImageDrawable(drawable);
            } else {
                int i2 = this.topResId;
                if (i2 != 0) {
                    rLottieImageView.setImageResource(i2);
                } else {
                    rLottieImageView.setAutoRepeat(this.topAnimationAutoRepeat);
                    RLottieImageView rLottieImageView2 = this.topImageView;
                    int i3 = this.topAnimationId;
                    int i4 = this.topAnimationSize;
                    rLottieImageView2.setAnimation(i3, i4, i4);
                    this.topImageView.playAnimation();
                }
            }
            this.topImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.topImageView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.popup_fixed_top));
            this.topImageView.getBackground().setColorFilter(new PorterDuffColorFilter(this.topBackgroundColor, PorterDuff.Mode.MULTIPLY));
            this.topImageView.setPadding(0, 0, 0, 0);
            containerView.addView(this.topImageView, LayoutHelper.createLinear(-1, this.topHeight, 51, -8, -8, 0, 0));
        } else {
            View view = this.topView;
            if (view != null) {
                view.setPadding(0, 0, 0, 0);
                containerView.addView(this.topView, LayoutHelper.createLinear(-1, this.topHeight, 51, 0, 0, 0, 0));
            }
        }
        if (this.title != null) {
            FrameLayout frameLayout = new FrameLayout(getContext());
            this.titleContainer = frameLayout;
            containerView.addView(frameLayout, LayoutHelper.createLinear(-2, -2, 24.0f, 0.0f, 24.0f, 0.0f));
            TextView textView = new TextView(getContext());
            this.titleTextView = textView;
            textView.setText(this.title);
            this.titleTextView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
            this.titleTextView.setTextSize(1, 20.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            FrameLayout frameLayout2 = this.titleContainer;
            TextView textView2 = this.titleTextView;
            int i5 = (LocaleController.isRTL ? 5 : 3) | 48;
            if (this.subtitle != null) {
                i = 2;
            } else {
                i = this.items != null ? 14 : 10;
            }
            frameLayout2.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, i5, 0.0f, 19.0f, 0.0f, i));
        }
        if (this.secondTitle != null && this.title != null) {
            TextView textView3 = new TextView(getContext());
            this.secondTitleTextView = textView3;
            textView3.setText(this.secondTitle);
            this.secondTitleTextView.setTextColor(getThemedColor(Theme.key_dialogTextGray3));
            this.secondTitleTextView.setTextSize(1, 18.0f);
            this.secondTitleTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
            this.titleContainer.addView(this.secondTitleTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, 0.0f, 21.0f, 0.0f, 0.0f));
        }
        if (this.subtitle != null) {
            TextView textView4 = new TextView(getContext());
            this.subtitleTextView = textView4;
            textView4.setText(this.subtitle);
            this.subtitleTextView.setTextColor(getThemedColor(Theme.key_dialogIcon));
            this.subtitleTextView.setTextSize(1, 14.0f);
            this.subtitleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            containerView.addView(this.subtitleTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, 0, 24, this.items != null ? 14 : 10));
        }
        if (this.progressViewStyle == 0) {
            this.shadow[0] = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.header_shadow).mutate();
            this.shadow[1] = (BitmapDrawable) getContext().getResources().getDrawable(R.drawable.header_shadow_reverse).mutate();
            this.shadow[0].setAlpha(0);
            this.shadow[1].setAlpha(0);
            this.shadow[0].setCallback(this);
            this.shadow[1].setCallback(this);
            ScrollView scrollView = new ScrollView(getContext()) { // from class: org.telegram.ui.ActionBar.AlertDialog.2
                @Override // android.view.ViewGroup
                protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                    boolean result = super.drawChild(canvas, child, drawingTime);
                    if (AlertDialog.this.shadow[0].getPaint().getAlpha() != 0) {
                        AlertDialog.this.shadow[0].setBounds(0, getScrollY(), getMeasuredWidth(), getScrollY() + AndroidUtilities.dp(3.0f));
                        AlertDialog.this.shadow[0].draw(canvas);
                    }
                    if (AlertDialog.this.shadow[1].getPaint().getAlpha() != 0) {
                        AlertDialog.this.shadow[1].setBounds(0, (getScrollY() + getMeasuredHeight()) - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getScrollY() + getMeasuredHeight());
                        AlertDialog.this.shadow[1].draw(canvas);
                    }
                    return result;
                }
            };
            this.contentScrollView = scrollView;
            scrollView.setVerticalScrollBarEnabled(false);
            AndroidUtilities.setScrollViewEdgeEffectColor(this.contentScrollView, getThemedColor(Theme.key_dialogScrollGlow));
            containerView.addView(this.contentScrollView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(getContext());
            this.scrollContainer = linearLayout;
            linearLayout.setOrientation(1);
            this.contentScrollView.addView(this.scrollContainer, new FrameLayout.LayoutParams(-1, -2));
        }
        SpoilersTextView spoilersTextView = new SpoilersTextView(getContext());
        this.messageTextView = spoilersTextView;
        spoilersTextView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        this.messageTextView.setTextSize(1, 16.0f);
        this.messageTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.messageTextView.setLinkTextColor(getThemedColor(Theme.key_dialogTextLink));
        if (!this.messageTextViewClickable) {
            this.messageTextView.setClickable(false);
            this.messageTextView.setEnabled(false);
        }
        this.messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        int i6 = this.progressViewStyle;
        if (i6 == 1) {
            FrameLayout frameLayout3 = new FrameLayout(getContext());
            this.progressViewContainer = frameLayout3;
            containerView.addView(frameLayout3, LayoutHelper.createLinear(-1, 44, 51, 23, this.title == null ? 24 : 0, 23, 24));
            RadialProgressView progressView = new RadialProgressView(getContext(), this.resourcesProvider);
            progressView.setProgressColor(getThemedColor(Theme.key_dialogProgressCircle));
            this.progressViewContainer.addView(progressView, LayoutHelper.createFrame(44, 44, (LocaleController.isRTL ? 5 : 3) | 48));
            this.messageTextView.setLines(1);
            this.messageTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.progressViewContainer.addView(this.messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0 : 62, 0.0f, LocaleController.isRTL ? 62 : 0, 0.0f));
        } else if (i6 == 2) {
            containerView.addView(this.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, this.title == null ? 19 : 0, 24, 20));
            LineProgressView lineProgressView = new LineProgressView(getContext());
            this.lineProgressView = lineProgressView;
            lineProgressView.setProgress(this.currentProgress / 100.0f, false);
            this.lineProgressView.setProgressColor(getThemedColor(Theme.key_dialogLineProgress));
            this.lineProgressView.setBackColor(getThemedColor(Theme.key_dialogLineProgressBackground));
            containerView.addView(this.lineProgressView, LayoutHelper.createLinear(-1, 4, 19, 24, 0, 24, 0));
            TextView textView5 = new TextView(getContext());
            this.lineProgressViewPercent = textView5;
            textView5.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.lineProgressViewPercent.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            this.lineProgressViewPercent.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
            this.lineProgressViewPercent.setTextSize(1, 14.0f);
            containerView.addView(this.lineProgressViewPercent, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 23, 4, 23, 24));
            updateLineProgressTextView();
        } else if (i6 != 3) {
            this.scrollContainer.addView(this.messageTextView, LayoutHelper.createLinear(-2, -2, (LocaleController.isRTL ? 5 : 3) | 48, 24, 0, 24, (this.customView == null && this.items == null) ? 0 : this.customViewOffset));
        } else {
            setCanceledOnTouchOutside(false);
            setCancelable(false);
            FrameLayout frameLayout4 = new FrameLayout(getContext());
            this.progressViewContainer = frameLayout4;
            frameLayout4.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), getThemedColor(Theme.key_dialog_inlineProgressBackground)));
            containerView.addView(this.progressViewContainer, LayoutHelper.createLinear(86, 86, 17));
            RadialProgressView progressView2 = new RadialProgressView(getContext(), this.resourcesProvider);
            progressView2.setProgressColor(getThemedColor(Theme.key_dialog_inlineProgress));
            this.progressViewContainer.addView(progressView2, LayoutHelper.createLinear(86, 86));
        }
        if (!TextUtils.isEmpty(this.message)) {
            this.messageTextView.setText(this.message);
            this.messageTextView.setVisibility(0);
        } else {
            this.messageTextView.setVisibility(8);
        }
        if (this.items != null) {
            int a = 0;
            while (true) {
                CharSequence[] charSequenceArr = this.items;
                if (a >= charSequenceArr.length) {
                    break;
                }
                if (charSequenceArr[a] != null) {
                    AlertDialogCell cell = new AlertDialogCell(getContext(), this.resourcesProvider);
                    CharSequence charSequence = this.items[a];
                    int[] iArr = this.itemIcons;
                    cell.setTextAndIcon(charSequence, iArr != null ? iArr[a] : 0);
                    cell.setTag(Integer.valueOf(a));
                    this.itemViews.add(cell);
                    this.scrollContainer.addView(cell, LayoutHelper.createLinear(-1, 50));
                    cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.AlertDialog$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            AlertDialog.this.m1404lambda$onCreate$1$orgtelegramuiActionBarAlertDialog(view2);
                        }
                    });
                }
                a++;
            }
        }
        View view2 = this.customView;
        if (view2 != null) {
            if (view2.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) this.customView.getParent();
                viewGroup.removeView(this.customView);
            }
            this.scrollContainer.addView(this.customView, LayoutHelper.createLinear(-1, this.customViewHeight));
        }
        if (hasButtons) {
            if (!this.verticalButtons) {
                int buttonsWidth = 0;
                TextPaint paint = new TextPaint();
                paint.setTextSize(AndroidUtilities.dp(14.0f));
                CharSequence charSequence2 = this.positiveButtonText;
                if (charSequence2 != null) {
                    buttonsWidth = (int) (0 + paint.measureText(charSequence2, 0, charSequence2.length()) + AndroidUtilities.dp(10.0f));
                }
                CharSequence charSequence3 = this.negativeButtonText;
                if (charSequence3 != null) {
                    buttonsWidth = (int) (buttonsWidth + paint.measureText(charSequence3, 0, charSequence3.length()) + AndroidUtilities.dp(10.0f));
                }
                CharSequence charSequence4 = this.neutralButtonText;
                if (charSequence4 != null) {
                    buttonsWidth = (int) (buttonsWidth + paint.measureText(charSequence4, 0, charSequence4.length()) + AndroidUtilities.dp(10.0f));
                }
                if (buttonsWidth > AndroidUtilities.displaySize.x - AndroidUtilities.dp(110.0f)) {
                    this.verticalButtons = true;
                }
            }
            if (!this.verticalButtons) {
                this.buttonsLayout = new FrameLayout(getContext()) { // from class: org.telegram.ui.ActionBar.AlertDialog.3
                    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                        int t;
                        int l;
                        int count = getChildCount();
                        View positiveButton = null;
                        int width = right - left;
                        for (int a2 = 0; a2 < count; a2++) {
                            View child = getChildAt(a2);
                            Integer tag = (Integer) child.getTag();
                            if (tag != null) {
                                if (tag.intValue() == -1) {
                                    positiveButton = child;
                                    if (LocaleController.isRTL) {
                                        child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                    } else {
                                        child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), width - getPaddingRight(), getPaddingTop() + child.getMeasuredHeight());
                                    }
                                } else if (tag.intValue() == -2) {
                                    if (LocaleController.isRTL) {
                                        int x = getPaddingLeft();
                                        if (positiveButton != null) {
                                            x += positiveButton.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                        }
                                        child.layout(x, getPaddingTop(), child.getMeasuredWidth() + x, getPaddingTop() + child.getMeasuredHeight());
                                    } else {
                                        int x2 = (width - getPaddingRight()) - child.getMeasuredWidth();
                                        if (positiveButton != null) {
                                            x2 -= positiveButton.getMeasuredWidth() + AndroidUtilities.dp(8.0f);
                                        }
                                        child.layout(x2, getPaddingTop(), child.getMeasuredWidth() + x2, getPaddingTop() + child.getMeasuredHeight());
                                    }
                                } else if (tag.intValue() == -3) {
                                    if (LocaleController.isRTL) {
                                        child.layout((width - getPaddingRight()) - child.getMeasuredWidth(), getPaddingTop(), width - getPaddingRight(), getPaddingTop() + child.getMeasuredHeight());
                                    } else {
                                        child.layout(getPaddingLeft(), getPaddingTop(), getPaddingLeft() + child.getMeasuredWidth(), getPaddingTop() + child.getMeasuredHeight());
                                    }
                                }
                            } else {
                                int w = child.getMeasuredWidth();
                                int h = child.getMeasuredHeight();
                                if (positiveButton != null) {
                                    l = positiveButton.getLeft() + ((positiveButton.getMeasuredWidth() - w) / 2);
                                    t = positiveButton.getTop() + ((positiveButton.getMeasuredHeight() - h) / 2);
                                } else {
                                    l = 0;
                                    t = 0;
                                }
                                child.layout(l, t, l + w, t + h);
                            }
                        }
                    }

                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        int totalWidth = 0;
                        int availableWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
                        int count = getChildCount();
                        for (int a2 = 0; a2 < count; a2++) {
                            View child = getChildAt(a2);
                            if ((child instanceof TextView) && child.getTag() != null) {
                                totalWidth += child.getMeasuredWidth();
                            }
                        }
                        if (totalWidth > availableWidth) {
                            View negative = findViewWithTag(-2);
                            View neutral = findViewWithTag(-3);
                            if (negative != null && neutral != null) {
                                if (negative.getMeasuredWidth() < neutral.getMeasuredWidth()) {
                                    neutral.measure(View.MeasureSpec.makeMeasureSpec(neutral.getMeasuredWidth() - (totalWidth - availableWidth), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(neutral.getMeasuredHeight(), C.BUFFER_FLAG_ENCRYPTED));
                                } else {
                                    negative.measure(View.MeasureSpec.makeMeasureSpec(negative.getMeasuredWidth() - (totalWidth - availableWidth), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(negative.getMeasuredHeight(), C.BUFFER_FLAG_ENCRYPTED));
                                }
                            }
                        }
                    }
                };
            } else {
                LinearLayout linearLayout2 = new LinearLayout(getContext());
                linearLayout2.setOrientation(1);
                this.buttonsLayout = linearLayout2;
            }
            this.buttonsLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            containerView.addView(this.buttonsLayout, LayoutHelper.createLinear(-1, 52));
            if (this.positiveButtonText != null) {
                TextView textView6 = new TextView(getContext()) { // from class: org.telegram.ui.ActionBar.AlertDialog.4
                    @Override // android.widget.TextView, android.view.View
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }

                    @Override // android.widget.TextView
                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(color));
                    }
                };
                textView6.setMinWidth(AndroidUtilities.dp(64.0f));
                textView6.setTag(-1);
                textView6.setTextSize(1, 14.0f);
                textView6.setTextColor(getThemedColor(this.dialogButtonColorKey));
                textView6.setGravity(17);
                textView6.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                textView6.setText(this.positiveButtonText.toString().toUpperCase());
                textView6.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemedColor(this.dialogButtonColorKey)));
                textView6.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                if (this.verticalButtons) {
                    this.buttonsLayout.addView(textView6, LayoutHelper.createLinear(-2, 36, LocaleController.isRTL ? 3 : 5));
                } else {
                    this.buttonsLayout.addView(textView6, LayoutHelper.createFrame(-2, 36, 53));
                }
                textView6.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.AlertDialog$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view3) {
                        AlertDialog.this.m1405lambda$onCreate$2$orgtelegramuiActionBarAlertDialog(view3);
                    }
                });
            }
            if (this.negativeButtonText != null) {
                TextView textView7 = new TextView(getContext()) { // from class: org.telegram.ui.ActionBar.AlertDialog.5
                    @Override // android.widget.TextView, android.view.View
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }

                    @Override // android.widget.TextView
                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(color));
                    }
                };
                textView7.setMinWidth(AndroidUtilities.dp(64.0f));
                textView7.setTag(-2);
                textView7.setTextSize(1, 14.0f);
                textView7.setTextColor(getThemedColor(this.dialogButtonColorKey));
                textView7.setGravity(17);
                textView7.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                textView7.setEllipsize(TextUtils.TruncateAt.END);
                textView7.setSingleLine(true);
                textView7.setText(this.negativeButtonText.toString().toUpperCase());
                textView7.setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(getThemedColor(this.dialogButtonColorKey)));
                textView7.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                if (this.verticalButtons) {
                    this.buttonsLayout.addView(textView7, 0, LayoutHelper.createLinear(-2, 36, LocaleController.isRTL ? 3 : 5));
                } else {
                    this.buttonsLayout.addView(textView7, LayoutHelper.createFrame(-2, 36, 53));
                }
                textView7.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.AlertDialog$$ExternalSyntheticLambda4
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view3) {
                        AlertDialog.this.m1406lambda$onCreate$3$orgtelegramuiActionBarAlertDialog(view3);
                    }
                });
            }
            if (this.neutralButtonText != null) {
                TextView textView8 = new TextView(getContext()) { // from class: org.telegram.ui.ActionBar.AlertDialog.6
                    @Override // android.widget.TextView, android.view.View
                    public void setEnabled(boolean enabled) {
                        super.setEnabled(enabled);
                        setAlpha(enabled ? 1.0f : 0.5f);
                    }

                    @Override // android.widget.TextView
                    public void setTextColor(int color) {
                        super.setTextColor(color);
                        setBackgroundDrawable(Theme.getRoundRectSelectorDrawable(color));
                    }
                };
                textView8.setMinWidth(AndroidUtilities.dp(64.0f));
                textView8.setTag(-3);
                textView8.setTextSize(1, 14.0f);
                textView8.setTextColor(getThemedColor(this.dialogButtonColorKey));
                textView8.setGravity(17);
                textView8.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                textView8.setEllipsize(TextUtils.TruncateAt.END);
                textView8.setSingleLine(true);
                textView8.setText(this.neutralButtonText.toString().toUpperCase());
                textView8.setBackground(Theme.getRoundRectSelectorDrawable(getThemedColor(this.dialogButtonColorKey)));
                textView8.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
                if (this.verticalButtons) {
                    this.buttonsLayout.addView(textView8, 1, LayoutHelper.createLinear(-2, 36, LocaleController.isRTL ? 3 : 5));
                } else {
                    this.buttonsLayout.addView(textView8, LayoutHelper.createFrame(-2, 36, 51));
                }
                textView8.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.AlertDialog$$ExternalSyntheticLambda5
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view3) {
                        AlertDialog.this.m1407lambda$onCreate$4$orgtelegramuiActionBarAlertDialog(view3);
                    }
                });
            }
            if (this.verticalButtons) {
                for (int i7 = 1; i7 < this.buttonsLayout.getChildCount(); i7++) {
                    ((ViewGroup.MarginLayoutParams) this.buttonsLayout.getChildAt(i7).getLayoutParams()).topMargin = AndroidUtilities.dp(6.0f);
                }
            }
        }
        Window window = getWindow();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.copyFrom(window.getAttributes());
        if (this.progressViewStyle == 3) {
            params.width = -1;
        } else {
            if (this.dimEnabled) {
                params.dimAmount = this.dimAlpha;
                params.flags |= 2;
            } else {
                params.dimAmount = 0.0f;
                params.flags ^= 2;
            }
            this.lastScreenWidth = AndroidUtilities.displaySize.x;
            int calculatedWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(48.0f);
            if (AndroidUtilities.isTablet()) {
                if (AndroidUtilities.isSmallTablet()) {
                    maxWidth = AndroidUtilities.dp(446.0f);
                } else {
                    maxWidth = AndroidUtilities.dp(496.0f);
                }
            } else {
                maxWidth = AndroidUtilities.dp(356.0f);
            }
            params.width = Math.min(maxWidth, calculatedWidth) + this.backgroundPaddings.left + this.backgroundPaddings.right;
        }
        View view3 = this.customView;
        if (view3 == null || !this.checkFocusable || !canTextInput(view3)) {
            params.flags |= 131072;
        } else {
            params.softInputMode = 4;
        }
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode = 0;
        }
        window.setAttributes(params);
    }

    /* renamed from: org.telegram.ui.ActionBar.AlertDialog$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 extends LinearLayout {
        private boolean inLayout;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass1(Context arg0) {
            super(arg0);
            AlertDialog.this = this$0;
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (AlertDialog.this.progressViewStyle == 3) {
                AlertDialog.this.showCancelAlert();
                return false;
            }
            return super.onTouchEvent(event);
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (AlertDialog.this.progressViewStyle == 3) {
                AlertDialog.this.showCancelAlert();
                return false;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int i;
            float f;
            int h;
            if (AlertDialog.this.progressViewStyle == 3) {
                AlertDialog.this.progressViewContainer.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), C.BUFFER_FLAG_ENCRYPTED));
                setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
                return;
            }
            this.inLayout = true;
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = View.MeasureSpec.getSize(heightMeasureSpec);
            int availableHeight = (height - getPaddingTop()) - getPaddingBottom();
            int availableWidth = (width - getPaddingLeft()) - getPaddingRight();
            int childWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(availableWidth - AndroidUtilities.dp(48.0f), C.BUFFER_FLAG_ENCRYPTED);
            int childFullWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(availableWidth, C.BUFFER_FLAG_ENCRYPTED);
            if (AlertDialog.this.buttonsLayout != null) {
                int count = AlertDialog.this.buttonsLayout.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = AlertDialog.this.buttonsLayout.getChildAt(a);
                    if (child instanceof TextView) {
                        TextView button = (TextView) child;
                        button.setMaxWidth(AndroidUtilities.dp((availableWidth - AndroidUtilities.dp(24.0f)) / 2));
                    }
                }
                AlertDialog.this.buttonsLayout.measure(childFullWidthMeasureSpec, heightMeasureSpec);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) AlertDialog.this.buttonsLayout.getLayoutParams();
                availableHeight -= (AlertDialog.this.buttonsLayout.getMeasuredHeight() + layoutParams.bottomMargin) + layoutParams.topMargin;
            }
            if (AlertDialog.this.secondTitleTextView != null) {
                AlertDialog.this.secondTitleTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(childWidthMeasureSpec), Integer.MIN_VALUE), heightMeasureSpec);
            }
            if (AlertDialog.this.titleTextView != null) {
                if (AlertDialog.this.secondTitleTextView != null) {
                    AlertDialog.this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(childWidthMeasureSpec) - AlertDialog.this.secondTitleTextView.getMeasuredWidth()) - AndroidUtilities.dp(8.0f), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                } else {
                    AlertDialog.this.titleTextView.measure(childWidthMeasureSpec, heightMeasureSpec);
                }
            }
            if (AlertDialog.this.titleContainer != null) {
                AlertDialog.this.titleContainer.measure(childWidthMeasureSpec, heightMeasureSpec);
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) AlertDialog.this.titleContainer.getLayoutParams();
                availableHeight -= (AlertDialog.this.titleContainer.getMeasuredHeight() + layoutParams2.bottomMargin) + layoutParams2.topMargin;
            }
            if (AlertDialog.this.subtitleTextView != null) {
                AlertDialog.this.subtitleTextView.measure(childWidthMeasureSpec, heightMeasureSpec);
                LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) AlertDialog.this.subtitleTextView.getLayoutParams();
                availableHeight -= (AlertDialog.this.subtitleTextView.getMeasuredHeight() + layoutParams3.bottomMargin) + layoutParams3.topMargin;
            }
            if (AlertDialog.this.topImageView != null) {
                AlertDialog.this.topImageView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(AlertDialog.this.topHeight), C.BUFFER_FLAG_ENCRYPTED));
                availableHeight -= AlertDialog.this.topImageView.getMeasuredHeight() - AndroidUtilities.dp(8.0f);
            }
            if (AlertDialog.this.topView != null) {
                int w = width - AndroidUtilities.dp(16.0f);
                if (AlertDialog.this.aspectRatio != 0.0f) {
                    h = (int) (w * AlertDialog.this.aspectRatio);
                } else {
                    float scale = w / 936.0f;
                    h = (int) (354.0f * scale);
                }
                AlertDialog.this.topView.measure(View.MeasureSpec.makeMeasureSpec(w, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(h, C.BUFFER_FLAG_ENCRYPTED));
                AlertDialog.this.topView.getLayoutParams().height = h;
                availableHeight -= AlertDialog.this.topView.getMeasuredHeight();
            }
            if (AlertDialog.this.progressViewStyle == 0) {
                LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) AlertDialog.this.contentScrollView.getLayoutParams();
                if (AlertDialog.this.customView != null) {
                    layoutParams4.topMargin = (AlertDialog.this.titleTextView == null && AlertDialog.this.messageTextView.getVisibility() == 8 && AlertDialog.this.items == null) ? AndroidUtilities.dp(16.0f) : 0;
                    layoutParams4.bottomMargin = AlertDialog.this.buttonsLayout == null ? AndroidUtilities.dp(8.0f) : 0;
                } else if (AlertDialog.this.items != null) {
                    if (AlertDialog.this.titleTextView == null && AlertDialog.this.messageTextView.getVisibility() == 8) {
                        f = 8.0f;
                        i = AndroidUtilities.dp(8.0f);
                    } else {
                        f = 8.0f;
                        i = 0;
                    }
                    layoutParams4.topMargin = i;
                    layoutParams4.bottomMargin = AndroidUtilities.dp(f);
                } else if (AlertDialog.this.messageTextView.getVisibility() == 0) {
                    layoutParams4.topMargin = AlertDialog.this.titleTextView == null ? AndroidUtilities.dp(19.0f) : 0;
                    layoutParams4.bottomMargin = AndroidUtilities.dp(20.0f);
                }
                int availableHeight2 = availableHeight - (layoutParams4.bottomMargin + layoutParams4.topMargin);
                AlertDialog.this.contentScrollView.measure(childFullWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(availableHeight2, Integer.MIN_VALUE));
                availableHeight = availableHeight2 - AlertDialog.this.contentScrollView.getMeasuredHeight();
            } else {
                if (AlertDialog.this.progressViewContainer != null) {
                    AlertDialog.this.progressViewContainer.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                    LinearLayout.LayoutParams layoutParams5 = (LinearLayout.LayoutParams) AlertDialog.this.progressViewContainer.getLayoutParams();
                    availableHeight -= (AlertDialog.this.progressViewContainer.getMeasuredHeight() + layoutParams5.bottomMargin) + layoutParams5.topMargin;
                } else if (AlertDialog.this.messageTextView != null) {
                    AlertDialog.this.messageTextView.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(availableHeight, Integer.MIN_VALUE));
                    if (AlertDialog.this.messageTextView.getVisibility() != 8) {
                        LinearLayout.LayoutParams layoutParams6 = (LinearLayout.LayoutParams) AlertDialog.this.messageTextView.getLayoutParams();
                        availableHeight -= (AlertDialog.this.messageTextView.getMeasuredHeight() + layoutParams6.bottomMargin) + layoutParams6.topMargin;
                    }
                }
                if (AlertDialog.this.lineProgressView != null) {
                    AlertDialog.this.lineProgressView.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(4.0f), C.BUFFER_FLAG_ENCRYPTED));
                    LinearLayout.LayoutParams layoutParams7 = (LinearLayout.LayoutParams) AlertDialog.this.lineProgressView.getLayoutParams();
                    int availableHeight3 = availableHeight - ((AlertDialog.this.lineProgressView.getMeasuredHeight() + layoutParams7.bottomMargin) + layoutParams7.topMargin);
                    AlertDialog.this.lineProgressViewPercent.measure(childWidthMeasureSpec, View.MeasureSpec.makeMeasureSpec(availableHeight3, Integer.MIN_VALUE));
                    LinearLayout.LayoutParams layoutParams8 = (LinearLayout.LayoutParams) AlertDialog.this.lineProgressViewPercent.getLayoutParams();
                    availableHeight = availableHeight3 - ((AlertDialog.this.lineProgressViewPercent.getMeasuredHeight() + layoutParams8.bottomMargin) + layoutParams8.topMargin);
                }
            }
            setMeasuredDimension(width, (availableHeight - availableHeight) + getPaddingTop() + getPaddingBottom());
            this.inLayout = false;
            if (AlertDialog.this.lastScreenWidth != AndroidUtilities.displaySize.x) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.AlertDialog$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        AlertDialog.AnonymousClass1.this.m1411lambda$onMeasure$0$orgtelegramuiActionBarAlertDialog$1();
                    }
                });
            }
        }

        /* renamed from: lambda$onMeasure$0$org-telegram-ui-ActionBar-AlertDialog$1 */
        public /* synthetic */ void m1411lambda$onMeasure$0$orgtelegramuiActionBarAlertDialog$1() {
            int maxWidth;
            AlertDialog.this.lastScreenWidth = AndroidUtilities.displaySize.x;
            int calculatedWidth = AndroidUtilities.displaySize.x - AndroidUtilities.dp(56.0f);
            if (AndroidUtilities.isTablet()) {
                if (AndroidUtilities.isSmallTablet()) {
                    maxWidth = AndroidUtilities.dp(446.0f);
                } else {
                    maxWidth = AndroidUtilities.dp(496.0f);
                }
            } else {
                maxWidth = AndroidUtilities.dp(356.0f);
            }
            Window window = AlertDialog.this.getWindow();
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(window.getAttributes());
            params.width = Math.min(maxWidth, calculatedWidth) + AlertDialog.this.backgroundPaddings.left + AlertDialog.this.backgroundPaddings.right;
            try {
                window.setAttributes(params);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }

        @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
            if (AlertDialog.this.progressViewStyle == 3) {
                int x = ((r - l) - AlertDialog.this.progressViewContainer.getMeasuredWidth()) / 2;
                int y = ((b - t) - AlertDialog.this.progressViewContainer.getMeasuredHeight()) / 2;
                AlertDialog.this.progressViewContainer.layout(x, y, AlertDialog.this.progressViewContainer.getMeasuredWidth() + x, AlertDialog.this.progressViewContainer.getMeasuredHeight() + y);
            } else if (AlertDialog.this.contentScrollView != null) {
                if (AlertDialog.this.onScrollChangedListener == null) {
                    AlertDialog.this.onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: org.telegram.ui.ActionBar.AlertDialog$1$$ExternalSyntheticLambda0
                        @Override // android.view.ViewTreeObserver.OnScrollChangedListener
                        public final void onScrollChanged() {
                            AlertDialog.AnonymousClass1.this.m1410lambda$onLayout$1$orgtelegramuiActionBarAlertDialog$1();
                        }
                    };
                    AlertDialog.this.contentScrollView.getViewTreeObserver().addOnScrollChangedListener(AlertDialog.this.onScrollChangedListener);
                }
                AlertDialog.this.onScrollChangedListener.onScrollChanged();
            }
        }

        /* renamed from: lambda$onLayout$1$org-telegram-ui-ActionBar-AlertDialog$1 */
        public /* synthetic */ void m1410lambda$onLayout$1$orgtelegramuiActionBarAlertDialog$1() {
            AlertDialog alertDialog = AlertDialog.this;
            boolean z = false;
            alertDialog.runShadowAnimation(0, alertDialog.titleTextView != null && AlertDialog.this.contentScrollView.getScrollY() > AlertDialog.this.scrollContainer.getTop());
            AlertDialog alertDialog2 = AlertDialog.this;
            if (alertDialog2.buttonsLayout != null && AlertDialog.this.contentScrollView.getScrollY() + AlertDialog.this.contentScrollView.getHeight() < AlertDialog.this.scrollContainer.getBottom()) {
                z = true;
            }
            alertDialog2.runShadowAnimation(1, z);
            AlertDialog.this.contentScrollView.invalidate();
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.inLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            if (AlertDialog.this.drawBackground) {
                AlertDialog.this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                if (AlertDialog.this.topView == null || !AlertDialog.this.notDrawBackgroundOnTopView) {
                    AlertDialog.this.shadowDrawable.draw(canvas);
                } else {
                    int clipTop = AlertDialog.this.topView.getBottom();
                    canvas.save();
                    canvas.clipRect(0, clipTop, getMeasuredWidth(), getMeasuredHeight());
                    AlertDialog.this.shadowDrawable.draw(canvas);
                    canvas.restore();
                }
            }
            super.dispatchDraw(canvas);
        }
    }

    /* renamed from: lambda$onCreate$1$org-telegram-ui-ActionBar-AlertDialog */
    public /* synthetic */ void m1404lambda$onCreate$1$orgtelegramuiActionBarAlertDialog(View v) {
        DialogInterface.OnClickListener onClickListener = this.onClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, ((Integer) v.getTag()).intValue());
        }
        dismiss();
    }

    /* renamed from: lambda$onCreate$2$org-telegram-ui-ActionBar-AlertDialog */
    public /* synthetic */ void m1405lambda$onCreate$2$orgtelegramuiActionBarAlertDialog(View v) {
        DialogInterface.OnClickListener onClickListener = this.positiveButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -1);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    /* renamed from: lambda$onCreate$3$org-telegram-ui-ActionBar-AlertDialog */
    public /* synthetic */ void m1406lambda$onCreate$3$orgtelegramuiActionBarAlertDialog(View v) {
        DialogInterface.OnClickListener onClickListener = this.negativeButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            cancel();
        }
    }

    /* renamed from: lambda$onCreate$4$org-telegram-ui-ActionBar-AlertDialog */
    public /* synthetic */ void m1407lambda$onCreate$4$orgtelegramuiActionBarAlertDialog(View v) {
        DialogInterface.OnClickListener onClickListener = this.neutralButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -2);
        }
        if (this.dismissDialogByButtons) {
            dismiss();
        }
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        super.onBackPressed();
        DialogInterface.OnClickListener onClickListener = this.onBackButtonListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, -2);
        }
    }

    public void setFocusable(boolean value) {
        if (this.focusable == value) {
            return;
        }
        this.focusable = value;
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (this.focusable) {
            params.softInputMode = 16;
            params.flags &= -131073;
        } else {
            params.softInputMode = 48;
            params.flags |= 131072;
        }
        window.setAttributes(params);
    }

    public void setBackgroundColor(int color) {
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
    }

    public void setTextColor(int color) {
        TextView textView = this.titleTextView;
        if (textView != null) {
            textView.setTextColor(color);
        }
        TextView textView2 = this.messageTextView;
        if (textView2 != null) {
            textView2.setTextColor(color);
        }
    }

    public void showCancelAlert() {
        if (!this.canCacnel || this.cancelDialog != null) {
            return;
        }
        Builder builder = new Builder(getContext());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("StopLoading", R.string.StopLoading));
        builder.setPositiveButton(LocaleController.getString("WaitMore", R.string.WaitMore), null);
        builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ActionBar.AlertDialog$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AlertDialog.this.m1408lambda$showCancelAlert$5$orgtelegramuiActionBarAlertDialog(dialogInterface, i);
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ActionBar.AlertDialog$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                AlertDialog.this.m1409lambda$showCancelAlert$6$orgtelegramuiActionBarAlertDialog(dialogInterface);
            }
        });
        try {
            this.cancelDialog = builder.show();
        } catch (Exception e) {
        }
    }

    /* renamed from: lambda$showCancelAlert$5$org-telegram-ui-ActionBar-AlertDialog */
    public /* synthetic */ void m1408lambda$showCancelAlert$5$orgtelegramuiActionBarAlertDialog(DialogInterface dialogInterface, int i) {
        DialogInterface.OnCancelListener onCancelListener = this.onCancelListener;
        if (onCancelListener != null) {
            onCancelListener.onCancel(this);
        }
        dismiss();
    }

    /* renamed from: lambda$showCancelAlert$6$org-telegram-ui-ActionBar-AlertDialog */
    public /* synthetic */ void m1409lambda$showCancelAlert$6$orgtelegramuiActionBarAlertDialog(DialogInterface dialog) {
        this.cancelDialog = null;
    }

    public void runShadowAnimation(final int num, boolean show) {
        if ((show && !this.shadowVisibility[num]) || (!show && this.shadowVisibility[num])) {
            this.shadowVisibility[num] = show;
            AnimatorSet[] animatorSetArr = this.shadowAnimation;
            if (animatorSetArr[num] != null) {
                animatorSetArr[num].cancel();
            }
            this.shadowAnimation[num] = new AnimatorSet();
            BitmapDrawable[] bitmapDrawableArr = this.shadow;
            if (bitmapDrawableArr[num] != null) {
                AnimatorSet animatorSet = this.shadowAnimation[num];
                Animator[] animatorArr = new Animator[1];
                BitmapDrawable bitmapDrawable = bitmapDrawableArr[num];
                int[] iArr = new int[1];
                iArr[0] = show ? 255 : 0;
                animatorArr[0] = ObjectAnimator.ofInt(bitmapDrawable, "alpha", iArr);
                animatorSet.playTogether(animatorArr);
            }
            this.shadowAnimation[num].setDuration(150L);
            this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.AlertDialog.7
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (AlertDialog.this.shadowAnimation[num] != null && AlertDialog.this.shadowAnimation[num].equals(animation)) {
                        AlertDialog.this.shadowAnimation[num] = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (AlertDialog.this.shadowAnimation[num] != null && AlertDialog.this.shadowAnimation[num].equals(animation)) {
                        AlertDialog.this.shadowAnimation[num] = null;
                    }
                }
            });
            try {
                this.shadowAnimation[num].start();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void setProgressStyle(int style) {
        this.progressViewStyle = style;
    }

    public void setDismissDialogByButtons(boolean value) {
        this.dismissDialogByButtons = value;
    }

    public void setProgress(int progress) {
        this.currentProgress = progress;
        LineProgressView lineProgressView = this.lineProgressView;
        if (lineProgressView != null) {
            lineProgressView.setProgress(progress / 100.0f, true);
            updateLineProgressTextView();
        }
    }

    private void updateLineProgressTextView() {
        this.lineProgressViewPercent.setText(String.format("%d%%", Integer.valueOf(this.currentProgress)));
    }

    public void setCanCancel(boolean value) {
        this.canCacnel = value;
    }

    private boolean canTextInput(View v) {
        if (v.onCheckIsTextEditor()) {
            return true;
        }
        if (!(v instanceof ViewGroup)) {
            return false;
        }
        ViewGroup vg = (ViewGroup) v;
        int i = vg.getChildCount();
        while (i > 0) {
            i--;
            if (canTextInput(vg.getChildAt(i))) {
                return true;
            }
        }
        return false;
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        DialogInterface.OnDismissListener onDismissListener = this.onDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss(this);
        }
        AlertDialog alertDialog = this.cancelDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        try {
            super.dismiss();
        } catch (Throwable th) {
        }
        AndroidUtilities.cancelRunOnUIThread(this.showRunnable);
    }

    @Override // android.app.Dialog
    public void setCanceledOnTouchOutside(boolean cancel) {
        super.setCanceledOnTouchOutside(cancel);
    }

    public void setTopImage(int resId, int backgroundColor) {
        this.topResId = resId;
        this.topBackgroundColor = backgroundColor;
    }

    public void setTopAnimation(int resId, int backgroundColor) {
        setTopAnimation(resId, 94, backgroundColor);
    }

    public void setTopAnimation(int resId, int size, int backgroundColor) {
        this.topAnimationId = resId;
        this.topAnimationSize = size;
        this.topBackgroundColor = backgroundColor;
    }

    public void setTopHeight(int value) {
        this.topHeight = value;
    }

    public void setTopImage(Drawable drawable, int backgroundColor) {
        this.topDrawable = drawable;
        this.topBackgroundColor = backgroundColor;
    }

    @Override // android.app.Dialog
    public void setTitle(CharSequence text) {
        this.title = text;
        TextView textView = this.titleTextView;
        if (textView != null) {
            textView.setText(text);
        }
    }

    public void setSecondTitle(CharSequence text) {
        this.secondTitle = text;
    }

    public void setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.positiveButtonText = text;
        this.positiveButtonListener = listener;
    }

    public void setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.negativeButtonText = text;
        this.negativeButtonListener = listener;
    }

    public void setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.neutralButtonText = text;
        this.neutralButtonListener = listener;
    }

    public void setItemColor(int item, int color, int icon) {
        if (item < 0 || item >= this.itemViews.size()) {
            return;
        }
        AlertDialogCell cell = this.itemViews.get(item);
        cell.textView.setTextColor(color);
        cell.imageView.setColorFilter(new PorterDuffColorFilter(icon, PorterDuff.Mode.MULTIPLY));
    }

    public int getItemsCount() {
        return this.itemViews.size();
    }

    public void setMessage(CharSequence text) {
        this.message = text;
        if (this.messageTextView != null) {
            if (!TextUtils.isEmpty(text)) {
                this.messageTextView.setText(this.message);
                this.messageTextView.setVisibility(0);
                return;
            }
            this.messageTextView.setVisibility(8);
        }
    }

    public void setMessageTextViewClickable(boolean value) {
        this.messageTextViewClickable = value;
    }

    public void setButton(int type, CharSequence text, DialogInterface.OnClickListener listener) {
        switch (type) {
            case -3:
                this.neutralButtonText = text;
                this.neutralButtonListener = listener;
                return;
            case -2:
                this.negativeButtonText = text;
                this.negativeButtonListener = listener;
                return;
            case -1:
                this.positiveButtonText = text;
                this.positiveButtonListener = listener;
                return;
            default:
                return;
        }
    }

    public View getButton(int type) {
        ViewGroup viewGroup = this.buttonsLayout;
        if (viewGroup != null) {
            return viewGroup.findViewWithTag(Integer.valueOf(type));
        }
        return null;
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable who) {
        this.contentScrollView.invalidate();
        this.scrollContainer.invalidate();
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        ScrollView scrollView = this.contentScrollView;
        if (scrollView != null) {
            scrollView.postDelayed(what, when);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable who, Runnable what) {
        ScrollView scrollView = this.contentScrollView;
        if (scrollView != null) {
            scrollView.removeCallbacks(what);
        }
    }

    @Override // android.app.Dialog
    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        this.onCancelListener = listener;
        super.setOnCancelListener(listener);
    }

    public void setPositiveButtonListener(DialogInterface.OnClickListener listener) {
        this.positiveButtonListener = listener;
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void showDelayed(long delay) {
        AndroidUtilities.cancelRunOnUIThread(this.showRunnable);
        AndroidUtilities.runOnUIThread(this.showRunnable, delay);
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return null;
    }

    public ViewGroup getButtonsLayout() {
        return this.buttonsLayout;
    }

    /* loaded from: classes4.dex */
    public static class Builder {
        private AlertDialog alertDialog;

        public Builder(AlertDialog alert) {
            this.alertDialog = alert;
        }

        public Builder(Context context) {
            this(context, null);
        }

        public Builder(Context context, Theme.ResourcesProvider resourcesProvider) {
            this(context, 0, resourcesProvider);
        }

        public Builder(Context context, int progressViewStyle, Theme.ResourcesProvider resourcesProvider) {
            this.alertDialog = new AlertDialog(context, progressViewStyle, resourcesProvider);
        }

        public Context getContext() {
            return this.alertDialog.getContext();
        }

        public Builder forceVerticalButtons() {
            this.alertDialog.verticalButtons = true;
            return this;
        }

        public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener onClickListener) {
            this.alertDialog.items = items;
            this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setCheckFocusable(boolean value) {
            this.alertDialog.checkFocusable = value;
            return this;
        }

        public Builder setItems(CharSequence[] items, int[] icons, DialogInterface.OnClickListener onClickListener) {
            this.alertDialog.items = items;
            this.alertDialog.itemIcons = icons;
            this.alertDialog.onClickListener = onClickListener;
            return this;
        }

        public Builder setView(View view) {
            return setView(view, -2);
        }

        public Builder setView(View view, int height) {
            this.alertDialog.customView = view;
            this.alertDialog.customViewHeight = height;
            return this;
        }

        public Builder setTitle(CharSequence title) {
            this.alertDialog.title = title;
            return this;
        }

        public Builder setSubtitle(CharSequence subtitle) {
            this.alertDialog.subtitle = subtitle;
            return this;
        }

        public Builder setTopImage(int resId, int backgroundColor) {
            this.alertDialog.topResId = resId;
            this.alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setTopView(View view) {
            this.alertDialog.topView = view;
            return this;
        }

        public Builder setDialogButtonColorKey(String key) {
            this.alertDialog.dialogButtonColorKey = key;
            return this;
        }

        public Builder setTopAnimation(int resId, int size, boolean autoRepeat, int backgroundColor) {
            this.alertDialog.topAnimationId = resId;
            this.alertDialog.topAnimationSize = size;
            this.alertDialog.topAnimationAutoRepeat = autoRepeat;
            this.alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setTopAnimation(int resId, int backgroundColor) {
            return setTopAnimation(resId, 94, true, backgroundColor);
        }

        public Builder setTopImage(Drawable drawable, int backgroundColor) {
            this.alertDialog.topDrawable = drawable;
            this.alertDialog.topBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            this.alertDialog.message = message;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
            this.alertDialog.positiveButtonText = text;
            this.alertDialog.positiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
            this.alertDialog.negativeButtonText = text;
            this.alertDialog.negativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text, DialogInterface.OnClickListener listener) {
            this.alertDialog.neutralButtonText = text;
            this.alertDialog.neutralButtonListener = listener;
            return this;
        }

        public Builder setOnBackButtonListener(DialogInterface.OnClickListener listener) {
            this.alertDialog.onBackButtonListener = listener;
            return this;
        }

        public Builder setOnCancelListener(DialogInterface.OnCancelListener listener) {
            this.alertDialog.setOnCancelListener(listener);
            return this;
        }

        public Builder setCustomViewOffset(int offset) {
            this.alertDialog.customViewOffset = offset;
            return this;
        }

        public Builder setMessageTextViewClickable(boolean value) {
            this.alertDialog.messageTextViewClickable = value;
            return this;
        }

        public AlertDialog create() {
            return this.alertDialog;
        }

        public AlertDialog show() {
            this.alertDialog.show();
            return this.alertDialog;
        }

        public Runnable getDismissRunnable() {
            return this.alertDialog.dismissRunnable;
        }

        public Builder setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.alertDialog.setOnDismissListener(onDismissListener);
            return this;
        }

        public void setTopViewAspectRatio(float aspectRatio) {
            this.alertDialog.aspectRatio = aspectRatio;
        }

        public Builder setDimEnabled(boolean dimEnabled) {
            this.alertDialog.dimEnabled = dimEnabled;
            return this;
        }

        public Builder setDimAlpha(float dimAlpha) {
            this.alertDialog.dimAlpha = dimAlpha;
            return this;
        }

        public void notDrawBackgroundOnTopView(boolean b) {
            this.alertDialog.notDrawBackgroundOnTopView = b;
        }

        public void setButtonsVertical(boolean vertical) {
            this.alertDialog.verticalButtons = vertical;
        }

        public Builder setOnPreDismissListener(DialogInterface.OnDismissListener onDismissListener) {
            this.alertDialog.onDismissListener = onDismissListener;
            return this;
        }
    }
}
