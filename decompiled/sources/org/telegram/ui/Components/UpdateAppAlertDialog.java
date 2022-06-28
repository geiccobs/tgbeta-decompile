package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class UpdateAppAlertDialog extends BottomSheet {
    private int accountNum;
    private boolean animationInProgress;
    private TLRPC.TL_help_appUpdate appUpdate;
    private boolean ignoreLayout;
    private LinearLayout linearLayout;
    private int[] location = new int[2];
    private TextView messageTextView;
    private AnimatorSet progressAnimation;
    private RadialProgress radialProgress;
    private FrameLayout radialProgressView;
    private int scrollOffsetY;
    private NestedScrollView scrollView;
    private View shadow;
    private AnimatorSet shadowAnimation;
    private Drawable shadowDrawable;
    private TextView textView;

    /* loaded from: classes5.dex */
    public class BottomSheetCell extends FrameLayout {
        private View background;
        private boolean hasBackground;
        private TextView[] textView = new TextView[2];

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public BottomSheetCell(Context context, boolean withoutBackground) {
            super(context);
            UpdateAppAlertDialog.this = this$0;
            this.hasBackground = !withoutBackground;
            setBackground(null);
            View view = new View(context);
            this.background = view;
            if (this.hasBackground) {
                view.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
            }
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, withoutBackground ? 0.0f : 16.0f, 16.0f, 16.0f));
            for (int a = 0; a < 2; a++) {
                this.textView[a] = new TextView(context);
                this.textView[a].setLines(1);
                this.textView[a].setSingleLine(true);
                this.textView[a].setGravity(1);
                this.textView[a].setEllipsize(TextUtils.TruncateAt.END);
                this.textView[a].setGravity(17);
                if (this.hasBackground) {
                    this.textView[a].setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
                    this.textView[a].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                } else {
                    this.textView[a].setTextColor(Theme.getColor(Theme.key_featuredStickers_addButton));
                }
                this.textView[a].setTextSize(1, 14.0f);
                this.textView[a].setPadding(0, 0, 0, this.hasBackground ? 0 : AndroidUtilities.dp(13.0f));
                addView(this.textView[a], LayoutHelper.createFrame(-2, -2, 17));
                if (a == 1) {
                    this.textView[a].setAlpha(0.0f);
                }
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.hasBackground ? 80.0f : 50.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setText(CharSequence text, boolean animated) {
            if (!animated) {
                this.textView[0].setText(text);
                return;
            }
            this.textView[1].setText(text);
            UpdateAppAlertDialog.this.animationInProgress = true;
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(180L);
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.textView[0], View.ALPHA, 1.0f, 0.0f), ObjectAnimator.ofFloat(this.textView[0], View.TRANSLATION_Y, 0.0f, -AndroidUtilities.dp(10.0f)), ObjectAnimator.ofFloat(this.textView[1], View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.textView[1], View.TRANSLATION_Y, AndroidUtilities.dp(10.0f), 0.0f));
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.UpdateAppAlertDialog.BottomSheetCell.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    UpdateAppAlertDialog.this.animationInProgress = false;
                    TextView temp = BottomSheetCell.this.textView[0];
                    BottomSheetCell.this.textView[0] = BottomSheetCell.this.textView[1];
                    BottomSheetCell.this.textView[1] = temp;
                }
            });
            animatorSet.start();
        }
    }

    public UpdateAppAlertDialog(Context context, TLRPC.TL_help_appUpdate update, int account) {
        super(context, false);
        this.appUpdate = update;
        this.accountNum = account;
        setCanceledOnTouchOutside(false);
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        FrameLayout container = new FrameLayout(context) { // from class: org.telegram.ui.Components.UpdateAppAlertDialog.1
            @Override // android.view.View
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                UpdateAppAlertDialog.this.updateLayout();
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() == 0 && UpdateAppAlertDialog.this.scrollOffsetY != 0 && ev.getY() < UpdateAppAlertDialog.this.scrollOffsetY) {
                    UpdateAppAlertDialog.this.dismiss();
                    return true;
                }
                return super.onInterceptTouchEvent(ev);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                return !UpdateAppAlertDialog.this.isDismissed() && super.onTouchEvent(e);
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                int top = (int) ((UpdateAppAlertDialog.this.scrollOffsetY - UpdateAppAlertDialog.this.backgroundPaddingTop) - getTranslationY());
                UpdateAppAlertDialog.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), getMeasuredHeight());
                UpdateAppAlertDialog.this.shadowDrawable.draw(canvas);
            }
        };
        container.setWillNotDraw(false);
        this.containerView = container;
        NestedScrollView nestedScrollView = new NestedScrollView(context) { // from class: org.telegram.ui.Components.UpdateAppAlertDialog.2
            private boolean ignoreLayout;

            @Override // androidx.core.widget.NestedScrollView, android.widget.FrameLayout, android.view.View
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                measureChildWithMargins(UpdateAppAlertDialog.this.linearLayout, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int contentHeight = UpdateAppAlertDialog.this.linearLayout.getMeasuredHeight();
                int padding = (height / 5) * 2;
                int visiblePart = height - padding;
                if (contentHeight - visiblePart < AndroidUtilities.dp(90.0f) || contentHeight < (height / 2) + AndroidUtilities.dp(90.0f)) {
                    padding = height - contentHeight;
                }
                if (padding < 0) {
                    padding = 0;
                }
                if (getPaddingTop() != padding) {
                    this.ignoreLayout = true;
                    setPadding(0, padding, 0, 0);
                    this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // androidx.core.widget.NestedScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                UpdateAppAlertDialog.this.updateLayout();
            }

            @Override // androidx.core.widget.NestedScrollView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // androidx.core.widget.NestedScrollView, android.view.View
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                super.onScrollChanged(l, t, oldl, oldt);
                UpdateAppAlertDialog.this.updateLayout();
            }
        };
        this.scrollView = nestedScrollView;
        nestedScrollView.setFillViewport(true);
        this.scrollView.setWillNotDraw(false);
        this.scrollView.setClipToPadding(false);
        this.scrollView.setVerticalScrollBarEnabled(false);
        container.addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 130.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.linearLayout = linearLayout;
        linearLayout.setOrientation(1);
        this.scrollView.addView(this.linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        if (this.appUpdate.sticker != null) {
            BackupImageView imageView = new BackupImageView(context);
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(this.appUpdate.sticker.thumbs, Theme.key_windowBackgroundGray, 1.0f);
            TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(this.appUpdate.sticker.thumbs, 90);
            ImageLocation imageLocation = ImageLocation.getForDocument(thumb, this.appUpdate.sticker);
            if (svgThumb != null) {
                imageView.setImage(ImageLocation.getForDocument(this.appUpdate.sticker), "250_250", svgThumb, 0, "update");
            } else {
                imageView.setImage(ImageLocation.getForDocument(this.appUpdate.sticker), "250_250", imageLocation, (String) null, 0, "update");
            }
            this.linearLayout.addView(imageView, LayoutHelper.createLinear(160, 160, 49, 17, 8, 17, 0));
        }
        TextView textView = new TextView(context);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setTextSize(1, 20.0f);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(LocaleController.getString("AppUpdate", R.string.AppUpdate));
        this.linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 49, 23, 16, 23, 0));
        TextView messageTextView = new TextView(getContext());
        messageTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
        messageTextView.setTextSize(1, 14.0f);
        messageTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        messageTextView.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
        messageTextView.setText(LocaleController.formatString("AppUpdateVersionAndSize", R.string.AppUpdateVersionAndSize, this.appUpdate.version, AndroidUtilities.formatFileSize(this.appUpdate.document.size)));
        messageTextView.setGravity(49);
        this.linearLayout.addView(messageTextView, LayoutHelper.createLinear(-2, -2, 49, 23, 0, 23, 5));
        TextView changelogTextView = new TextView(getContext());
        changelogTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        changelogTextView.setTextSize(1, 14.0f);
        changelogTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        changelogTextView.setLinkTextColor(Theme.getColor(Theme.key_dialogTextLink));
        if (TextUtils.isEmpty(this.appUpdate.text)) {
            changelogTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString("AppUpdateChangelogEmpty", R.string.AppUpdateChangelogEmpty)));
        } else {
            SpannableStringBuilder builder = new SpannableStringBuilder(this.appUpdate.text);
            MessageObject.addEntitiesToText(builder, update.entities, false, false, false, false);
            changelogTextView.setText(builder);
        }
        changelogTextView.setGravity(51);
        this.linearLayout.addView(changelogTextView, LayoutHelper.createLinear(-2, -2, 51, 23, 15, 23, 0));
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        frameLayoutParams.bottomMargin = AndroidUtilities.dp(130.0f);
        View view = new View(context);
        this.shadow = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        this.shadow.setAlpha(0.0f);
        this.shadow.setTag(1);
        container.addView(this.shadow, frameLayoutParams);
        BottomSheetCell doneButton = new BottomSheetCell(context, false);
        doneButton.setText(LocaleController.formatString("AppUpdateDownloadNow", R.string.AppUpdateDownloadNow, new Object[0]), false);
        doneButton.background.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.UpdateAppAlertDialog$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                UpdateAppAlertDialog.this.m3192lambda$new$0$orgtelegramuiComponentsUpdateAppAlertDialog(view2);
            }
        });
        container.addView(doneButton, LayoutHelper.createFrame(-1, 50.0f, 83, 0.0f, 0.0f, 0.0f, 50.0f));
        BottomSheetCell scheduleButton = new BottomSheetCell(context, true);
        scheduleButton.setText(LocaleController.getString("AppUpdateRemindMeLater", R.string.AppUpdateRemindMeLater), false);
        scheduleButton.background.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.UpdateAppAlertDialog$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                UpdateAppAlertDialog.this.m3193lambda$new$1$orgtelegramuiComponentsUpdateAppAlertDialog(view2);
            }
        });
        container.addView(scheduleButton, LayoutHelper.createFrame(-1, 50.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-UpdateAppAlertDialog */
    public /* synthetic */ void m3192lambda$new$0$orgtelegramuiComponentsUpdateAppAlertDialog(View v) {
        FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 1, 1);
        dismiss();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-UpdateAppAlertDialog */
    public /* synthetic */ void m3193lambda$new$1$orgtelegramuiComponentsUpdateAppAlertDialog(View v) {
        dismiss();
    }

    private void runShadowAnimation(int num, final boolean show) {
        if ((show && this.shadow.getTag() != null) || (!show && this.shadow.getTag() == null)) {
            this.shadow.setTag(show ? null : 1);
            if (show) {
                this.shadow.setVisibility(0);
            }
            AnimatorSet animatorSet = this.shadowAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.shadowAnimation = animatorSet2;
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            this.shadowAnimation.setDuration(150L);
            this.shadowAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.UpdateAppAlertDialog.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (UpdateAppAlertDialog.this.shadowAnimation != null && UpdateAppAlertDialog.this.shadowAnimation.equals(animation)) {
                        if (!show) {
                            UpdateAppAlertDialog.this.shadow.setVisibility(4);
                        }
                        UpdateAppAlertDialog.this.shadowAnimation = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (UpdateAppAlertDialog.this.shadowAnimation != null && UpdateAppAlertDialog.this.shadowAnimation.equals(animation)) {
                        UpdateAppAlertDialog.this.shadowAnimation = null;
                    }
                }
            });
            this.shadowAnimation.start();
        }
    }

    public void updateLayout() {
        View child = this.linearLayout.getChildAt(0);
        child.getLocationInWindow(this.location);
        int top = this.location[1] - AndroidUtilities.dp(24.0f);
        int newOffset = Math.max(top, 0);
        if (this.location[1] + this.linearLayout.getMeasuredHeight() <= (this.container.getMeasuredHeight() - AndroidUtilities.dp(113.0f)) + this.containerView.getTranslationY()) {
            runShadowAnimation(0, false);
        } else {
            runShadowAnimation(0, true);
        }
        if (this.scrollOffsetY != newOffset) {
            this.scrollOffsetY = newOffset;
            this.scrollView.invalidate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }
}
