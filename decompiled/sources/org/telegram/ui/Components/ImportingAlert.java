package org.telegram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
/* loaded from: classes5.dex */
public class ImportingAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private BottomSheetCell cell;
    private boolean completed;
    private RLottieDrawable completedDrawable;
    private RLottieImageView imageView;
    private TextView[] importCountTextView = new TextView[2];
    private TextView[] infoTextView = new TextView[2];
    private LineProgressView lineProgressView;
    private final Runnable onFinishCallback;
    private ChatActivity parentFragment;
    private TextView percentTextView;
    private String stickersShortName;

    /* loaded from: classes5.dex */
    public static class BottomSheetCell extends FrameLayout {
        private View background;
        private RLottieImageView imageView;
        private LinearLayout linearLayout;
        private Theme.ResourcesProvider resourcesProvider;
        private TextView textView;

        public BottomSheetCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), getThemedColor(Theme.key_featuredStickers_addButton), getThemedColor(Theme.key_featuredStickers_addButtonPressed)));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            this.linearLayout = linearLayout;
            linearLayout.setOrientation(0);
            addView(this.linearLayout, LayoutHelper.createFrame(-2, -2, 17));
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(20.0f), getThemedColor(Theme.key_featuredStickers_buttonText)));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_featuredStickers_addButton), PorterDuff.Mode.MULTIPLY));
            this.imageView.setAnimation(R.raw.import_check, 26, 26);
            this.imageView.setScaleX(0.8f);
            this.imageView.setScaleY(0.8f);
            this.linearLayout.addView(this.imageView, LayoutHelper.createLinear(20, 20, 16));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(17);
            this.textView.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 16, 10, 0, 0, 0));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setTextColor(int color) {
            this.textView.setTextColor(color);
        }

        public void setGravity(int gravity) {
            this.textView.setGravity(gravity);
        }

        public void setText(CharSequence text) {
            this.textView.setText(text);
        }

        private int getThemedColor(String key) {
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
            return color != null ? color.intValue() : Theme.getColor(key);
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ImportingAlert */
    public /* synthetic */ void m2679lambda$new$0$orgtelegramuiComponentsImportingAlert() {
        if (this.completed) {
            this.imageView.getAnimatedDrawable().setAutoRepeat(0);
            this.imageView.setAnimation(this.completedDrawable);
            this.imageView.playAnimation();
        }
    }

    public ImportingAlert(Context context, String shortName, ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ImportingAlert$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ImportingAlert.this.m2679lambda$new$0$orgtelegramuiComponentsImportingAlert();
            }
        };
        this.onFinishCallback = runnable;
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        this.parentFragment = chatActivity;
        this.stickersShortName = shortName;
        FrameLayout frameLayout = new FrameLayout(context);
        setCustomView(frameLayout);
        TextView textView = new TextView(context);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setTextSize(1, 20.0f);
        textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 17.0f, 20.0f, 17.0f, 0.0f));
        RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.import_finish, "2131558473", AndroidUtilities.dp(120.0f), AndroidUtilities.dp(120.0f), false, null);
        this.completedDrawable = rLottieDrawable;
        rLottieDrawable.setAllowDecodeSingleFrame(true);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setAutoRepeat(true);
        this.imageView.setAnimation(R.raw.import_loop, 120, 120);
        this.imageView.playAnimation();
        frameLayout.addView(this.imageView, LayoutHelper.createFrame(160, 160.0f, 49, 17.0f, 79.0f, 17.0f, 0.0f));
        this.imageView.getAnimatedDrawable().setOnFinishCallback(runnable, 178);
        TextView textView2 = new TextView(context);
        this.percentTextView = textView2;
        textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.percentTextView.setTextSize(1, 24.0f);
        this.percentTextView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        frameLayout.addView(this.percentTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 17.0f, 262.0f, 17.0f, 0.0f));
        LineProgressView lineProgressView = new LineProgressView(getContext());
        this.lineProgressView = lineProgressView;
        lineProgressView.setProgressColor(getThemedColor(Theme.key_featuredStickers_addButton));
        this.lineProgressView.setBackColor(getThemedColor(Theme.key_dialogLineProgressBackground));
        frameLayout.addView(this.lineProgressView, LayoutHelper.createFrame(-1, 4.0f, 51, 50.0f, 307.0f, 50.0f, 0.0f));
        BottomSheetCell bottomSheetCell = new BottomSheetCell(context, resourcesProvider);
        this.cell = bottomSheetCell;
        bottomSheetCell.setBackground(null);
        this.cell.setText(LocaleController.getString("ImportDone", R.string.ImportDone));
        this.cell.setVisibility(4);
        this.cell.background.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ImportingAlert$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ImportingAlert.this.m2680lambda$new$1$orgtelegramuiComponentsImportingAlert(view);
            }
        });
        this.cell.background.setPivotY(AndroidUtilities.dp(48.0f));
        this.cell.background.setScaleY(0.04f);
        frameLayout.addView(this.cell, LayoutHelper.createFrame(-1, 50.0f, 51, 34.0f, 247.0f, 34.0f, 0.0f));
        int a = 0;
        for (int i = 2; a < i; i = 2) {
            this.importCountTextView[a] = new TextView(context);
            this.importCountTextView[a].setTextSize(1, 16.0f);
            this.importCountTextView[a].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.importCountTextView[a].setTextColor(getThemedColor(Theme.key_dialogTextBlack));
            frameLayout.addView(this.importCountTextView[a], LayoutHelper.createFrame(-2, -2.0f, 49, 17.0f, 340.0f, 17.0f, 0.0f));
            this.infoTextView[a] = new TextView(context);
            this.infoTextView[a].setTextSize(1, 14.0f);
            this.infoTextView[a].setTextColor(getThemedColor(Theme.key_dialogTextGray3));
            this.infoTextView[a].setGravity(1);
            frameLayout.addView(this.infoTextView[a], LayoutHelper.createFrame(-2, -2.0f, 49, 30.0f, 368.0f, 30.0f, 44.0f));
            if (a == 0) {
                this.infoTextView[a].setText(LocaleController.getString("ImportImportingInfo", R.string.ImportImportingInfo));
            } else {
                this.infoTextView[a].setAlpha(0.0f);
                this.infoTextView[a].setTranslationY(AndroidUtilities.dp(10.0f));
                this.importCountTextView[a].setAlpha(0.0f);
                this.importCountTextView[a].setTranslationY(AndroidUtilities.dp(10.0f));
            }
            a++;
        }
        if (this.parentFragment != null) {
            textView.setText(LocaleController.getString("ImportImportingTitle", R.string.ImportImportingTitle));
            SendMessagesHelper.ImportingHistory importingHistory = this.parentFragment.getSendMessagesHelper().getImportingHistory(this.parentFragment.getDialogId());
            this.percentTextView.setText(String.format("%d%%", Integer.valueOf(importingHistory.uploadProgress)));
            this.lineProgressView.setProgress(importingHistory.uploadProgress / 100.0f, false);
            this.importCountTextView[0].setText(LocaleController.formatString("ImportCount", R.string.ImportCount, AndroidUtilities.formatFileSize(importingHistory.getUploadedCount()), AndroidUtilities.formatFileSize(importingHistory.getTotalCount())));
            this.infoTextView[1].setText(LocaleController.getString("ImportDoneInfo", R.string.ImportDoneInfo));
            this.importCountTextView[1].setText(LocaleController.getString("ImportDoneTitle", R.string.ImportDoneTitle));
            this.parentFragment.getNotificationCenter().addObserver(this, NotificationCenter.historyImportProgressChanged);
            return;
        }
        textView.setText(LocaleController.getString("ImportStickersImportingTitle", R.string.ImportStickersImportingTitle));
        SendMessagesHelper.ImportingStickers importingStickers = SendMessagesHelper.getInstance(this.currentAccount).getImportingStickers(shortName);
        this.percentTextView.setText(String.format("%d%%", Integer.valueOf(importingStickers.uploadProgress)));
        this.lineProgressView.setProgress(importingStickers.uploadProgress / 100.0f, false);
        this.importCountTextView[0].setText(LocaleController.formatString("ImportCount", R.string.ImportCount, AndroidUtilities.formatFileSize(importingStickers.getUploadedCount()), AndroidUtilities.formatFileSize(importingStickers.getTotalCount())));
        this.infoTextView[1].setText(LocaleController.getString("ImportStickersDoneInfo", R.string.ImportStickersDoneInfo));
        this.importCountTextView[1].setText(LocaleController.getString("ImportStickersDoneTitle", R.string.ImportStickersDoneTitle));
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersImportProgressChanged);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ImportingAlert */
    public /* synthetic */ void m2680lambda$new$1$orgtelegramuiComponentsImportingAlert(View v) {
        dismiss();
    }

    public void setCompleted() {
        this.completed = true;
        this.imageView.setAutoRepeat(false);
        this.cell.setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(250L);
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.percentTextView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.percentTextView, View.TRANSLATION_Y, -AndroidUtilities.dp(10.0f)), ObjectAnimator.ofFloat(this.infoTextView[0], View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.infoTextView[0], View.TRANSLATION_Y, -AndroidUtilities.dp(10.0f)), ObjectAnimator.ofFloat(this.importCountTextView[0], View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.importCountTextView[0], View.TRANSLATION_Y, -AndroidUtilities.dp(10.0f)), ObjectAnimator.ofFloat(this.infoTextView[1], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.infoTextView[1], View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(this.importCountTextView[1], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.importCountTextView[1], View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(this.lineProgressView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.cell.linearLayout, View.TRANSLATION_Y, AndroidUtilities.dp(8.0f), 0.0f));
        this.cell.background.animate().scaleY(1.0f).setInterpolator(new OvershootInterpolator(1.02f)).setDuration(250L).start();
        this.cell.imageView.animate().scaleY(1.0f).scaleX(1.0f).setInterpolator(new OvershootInterpolator(1.02f)).setDuration(250L).start();
        this.cell.imageView.playAnimation();
        animatorSet.start();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.historyImportProgressChanged) {
            if (args.length > 1) {
                dismiss();
                return;
            }
            long dialogId = this.parentFragment.getDialogId();
            SendMessagesHelper.ImportingHistory importingHistory = this.parentFragment.getSendMessagesHelper().getImportingHistory(dialogId);
            if (importingHistory == null) {
                setCompleted();
                return;
            }
            if (!this.completed) {
                double currentFrame = 180 - this.imageView.getAnimatedDrawable().getCurrentFrame();
                Double.isNaN(currentFrame);
                double timeToEndAnimation = (currentFrame * 16.6d) + 3000.0d;
                if (timeToEndAnimation >= importingHistory.timeUntilFinish) {
                    this.imageView.setAutoRepeat(false);
                    this.completed = true;
                }
            }
            this.percentTextView.setText(String.format("%d%%", Integer.valueOf(importingHistory.uploadProgress)));
            this.importCountTextView[0].setText(LocaleController.formatString("ImportCount", R.string.ImportCount, AndroidUtilities.formatFileSize(importingHistory.getUploadedCount()), AndroidUtilities.formatFileSize(importingHistory.getTotalCount())));
            this.lineProgressView.setProgress(importingHistory.uploadProgress / 100.0f, true);
        } else if (id == NotificationCenter.stickersImportProgressChanged) {
            if (args.length <= 1) {
                SendMessagesHelper.ImportingStickers importingStickers = SendMessagesHelper.getInstance(this.currentAccount).getImportingStickers(this.stickersShortName);
                if (importingStickers == null) {
                    setCompleted();
                    return;
                }
                if (!this.completed) {
                    double currentFrame2 = 180 - this.imageView.getAnimatedDrawable().getCurrentFrame();
                    Double.isNaN(currentFrame2);
                    double timeToEndAnimation2 = (currentFrame2 * 16.6d) + 3000.0d;
                    if (timeToEndAnimation2 >= importingStickers.timeUntilFinish) {
                        this.imageView.setAutoRepeat(false);
                        this.completed = true;
                    }
                }
                this.percentTextView.setText(String.format("%d%%", Integer.valueOf(importingStickers.uploadProgress)));
                this.importCountTextView[0].setText(LocaleController.formatString("ImportCount", R.string.ImportCount, AndroidUtilities.formatFileSize(importingStickers.getUploadedCount()), AndroidUtilities.formatFileSize(importingStickers.getTotalCount())));
                this.lineProgressView.setProgress(importingStickers.uploadProgress / 100.0f, true);
                return;
            }
            dismiss();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        super.dismissInternal();
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            chatActivity.getNotificationCenter().removeObserver(this, NotificationCenter.historyImportProgressChanged);
        } else {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersImportProgressChanged);
        }
    }
}
