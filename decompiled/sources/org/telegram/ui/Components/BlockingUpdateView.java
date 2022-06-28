package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import com.google.android.exoplayer2.C;
import java.io.File;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
/* loaded from: classes5.dex */
public class BlockingUpdateView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private FrameLayout acceptButton;
    private TextView acceptTextView;
    private int accountNum;
    private TLRPC.TL_help_appUpdate appUpdate;
    private String fileName;
    private int pressCount;
    private AnimatorSet progressAnimation;
    private RadialProgress radialProgress;
    private FrameLayout radialProgressView;
    private ScrollView scrollView;
    private TextView textView;
    Drawable gradientDrawableTop = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{Theme.getColor(Theme.key_windowBackgroundWhite), 0});
    Drawable gradientDrawableBottom = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{Theme.getColor(Theme.key_windowBackgroundWhite), 0});

    public BlockingUpdateView(Context context) {
        super(context);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        int top = Build.VERSION.SDK_INT >= 21 ? (int) (AndroidUtilities.statusBarHeight / AndroidUtilities.density) : 0;
        FrameLayout view = new FrameLayout(context);
        addView(view, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(176.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
        RLottieImageView imageView = new RLottieImageView(context);
        imageView.setAnimation(R.raw.qr_code_logo, 108, 108);
        imageView.playAnimation();
        imageView.getAnimatedDrawable().setAutoRepeat(1);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setPadding(0, 0, 0, AndroidUtilities.dp(14.0f));
        view.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, top, 0.0f, 0.0f));
        imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.BlockingUpdateView$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                BlockingUpdateView.this.m2218lambda$new$0$orgtelegramuiComponentsBlockingUpdateView(view2);
            }
        });
        FrameLayout container = new FrameLayout(context);
        ScrollView scrollView = new ScrollView(context);
        this.scrollView = scrollView;
        AndroidUtilities.setScrollViewEdgeEffectColor(scrollView, Theme.getColor(Theme.key_actionBarDefault));
        this.scrollView.setPadding(0, AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f));
        this.scrollView.setClipToPadding(false);
        addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 27.0f, top + 178, 27.0f, 130.0f));
        this.scrollView.addView(container);
        TextView titleTextView = new TextView(context);
        titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleTextView.setTextSize(1, 20.0f);
        titleTextView.setGravity(49);
        titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        titleTextView.setText(LocaleController.getString("UpdateTelegram", R.string.UpdateTelegram));
        container.addView(titleTextView, LayoutHelper.createFrame(-2, -2, 49));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.textView.setGravity(49);
        this.textView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        container.addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 44.0f, 0.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.BlockingUpdateView.1
            CellFlickerDrawable cellFlickerDrawable;

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (this.cellFlickerDrawable == null) {
                    CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
                    this.cellFlickerDrawable = cellFlickerDrawable;
                    cellFlickerDrawable.drawFrame = false;
                    this.cellFlickerDrawable.repeatProgress = 2.0f;
                }
                this.cellFlickerDrawable.setParentWidth(getMeasuredWidth());
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.cellFlickerDrawable.draw(canvas, AndroidUtilities.rectTmp, AndroidUtilities.dp(4.0f), null);
                invalidate();
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int size = View.MeasureSpec.getSize(widthMeasureSpec);
                if (size > AndroidUtilities.dp(260.0f)) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(320.0f), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                } else {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }
        };
        this.acceptButton = frameLayout;
        frameLayout.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.acceptButton.setBackgroundDrawable(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
        this.acceptButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        addView(this.acceptButton, LayoutHelper.createFrame(-2, 46.0f, 81, 0.0f, 0.0f, 0.0f, 45.0f));
        this.acceptButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.BlockingUpdateView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                BlockingUpdateView.this.m2219lambda$new$1$orgtelegramuiComponentsBlockingUpdateView(view2);
            }
        });
        TextView textView2 = new TextView(context);
        this.acceptTextView = textView2;
        textView2.setGravity(17);
        this.acceptTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.acceptTextView.setTextColor(-1);
        this.acceptTextView.setTextSize(1, 14.0f);
        this.acceptButton.addView(this.acceptTextView, LayoutHelper.createFrame(-2, -2, 17));
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.Components.BlockingUpdateView.2
            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top2, int right, int bottom) {
                super.onLayout(changed, left, top2, right, bottom);
                int width = right - left;
                int height = bottom - top2;
                int w = AndroidUtilities.dp(36.0f);
                int l = (width - w) / 2;
                int t = (height - w) / 2;
                BlockingUpdateView.this.radialProgress.setProgressRect(l, t, l + w, t + w);
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                BlockingUpdateView.this.radialProgress.draw(canvas);
            }
        };
        this.radialProgressView = frameLayout2;
        frameLayout2.setWillNotDraw(false);
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(4);
        RadialProgress radialProgress = new RadialProgress(this.radialProgressView);
        this.radialProgress = radialProgress;
        radialProgress.setBackground(null, true, false);
        this.radialProgress.setProgressColor(-1);
        this.acceptButton.addView(this.radialProgressView, LayoutHelper.createFrame(36, 36, 17));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-BlockingUpdateView */
    public /* synthetic */ void m2218lambda$new$0$orgtelegramuiComponentsBlockingUpdateView(View v) {
        int i = this.pressCount + 1;
        this.pressCount = i;
        if (i >= 10) {
            setVisibility(8);
            SharedConfig.pendingAppUpdate = null;
            SharedConfig.saveConfig();
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-BlockingUpdateView */
    public /* synthetic */ void m2219lambda$new$1$orgtelegramuiComponentsBlockingUpdateView(View view1) {
        if (!checkApkInstallPermissions(getContext())) {
            return;
        }
        if (this.appUpdate.document instanceof TLRPC.TL_document) {
            if (!openApkInstall((Activity) getContext(), this.appUpdate.document)) {
                FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 2, 1);
                showProgress(true);
            }
        } else if (this.appUpdate.url != null) {
            Browser.openUrl(getContext(), this.appUpdate.url);
        }
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == 8) {
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileLoadFailed);
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileLoaded) {
            String location = (String) args[0];
            String str = this.fileName;
            if (str != null && str.equals(location)) {
                showProgress(false);
                openApkInstall((Activity) getContext(), this.appUpdate.document);
            }
        } else if (id == NotificationCenter.fileLoadFailed) {
            String location2 = (String) args[0];
            String str2 = this.fileName;
            if (str2 != null && str2.equals(location2)) {
                showProgress(false);
            }
        } else if (id == NotificationCenter.fileLoadProgressChanged) {
            String location3 = (String) args[0];
            String str3 = this.fileName;
            if (str3 != null && str3.equals(location3)) {
                Long loadedSize = (Long) args[1];
                Long totalSize = (Long) args[2];
                float progress = Math.min(1.0f, ((float) loadedSize.longValue()) / ((float) totalSize.longValue()));
                this.radialProgress.setProgress(progress, true);
            }
        }
    }

    public static boolean checkApkInstallPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= 26 && !ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls()) {
            AlertsCreator.createApkRestrictedDialog(context, null).show();
            return false;
        }
        return true;
    }

    public static boolean openApkInstall(Activity activity, TLRPC.Document document) {
        boolean exists = false;
        try {
            FileLoader.getAttachFileName(document);
            File f = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true);
            boolean exists2 = f.exists();
            exists = exists2;
            if (exists2) {
                Intent intent = new Intent("android.intent.action.VIEW");
                intent.setFlags(1);
                if (Build.VERSION.SDK_INT >= 24) {
                    intent.setDataAndType(FileProvider.getUriForFile(activity, "org.telegram.messenger.beta.provider", f), "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
                }
                try {
                    activity.startActivityForResult(intent, 500);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        return exists;
    }

    private void showProgress(final boolean show) {
        AnimatorSet animatorSet = this.progressAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.progressAnimation = new AnimatorSet();
        if (show) {
            this.radialProgressView.setVisibility(0);
            this.acceptButton.setEnabled(false);
            this.progressAnimation.playTogether(ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, 1.0f));
        } else {
            this.acceptTextView.setVisibility(0);
            this.acceptButton.setEnabled(true);
            this.progressAnimation.playTogether(ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_X, 0.1f), ObjectAnimator.ofFloat(this.radialProgressView, View.SCALE_Y, 0.1f), ObjectAnimator.ofFloat(this.radialProgressView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.acceptTextView, View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.acceptTextView, View.ALPHA, 1.0f));
        }
        this.progressAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.BlockingUpdateView.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (BlockingUpdateView.this.progressAnimation != null && BlockingUpdateView.this.progressAnimation.equals(animation)) {
                    if (!show) {
                        BlockingUpdateView.this.radialProgressView.setVisibility(4);
                    } else {
                        BlockingUpdateView.this.acceptTextView.setVisibility(4);
                    }
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                if (BlockingUpdateView.this.progressAnimation != null && BlockingUpdateView.this.progressAnimation.equals(animation)) {
                    BlockingUpdateView.this.progressAnimation = null;
                }
            }
        });
        this.progressAnimation.setDuration(150L);
        this.progressAnimation.start();
    }

    public void show(int account, TLRPC.TL_help_appUpdate update, boolean check) {
        this.pressCount = 0;
        this.appUpdate = update;
        this.accountNum = account;
        if (update.document instanceof TLRPC.TL_document) {
            this.fileName = FileLoader.getAttachFileName(update.document);
        }
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(update.text);
        MessageObject.addEntitiesToText(builder, update.entities, false, false, false, false);
        this.textView.setText(builder);
        if (update.document instanceof TLRPC.TL_document) {
            TextView textView = this.acceptTextView;
            textView.setText(LocaleController.getString("Update", R.string.Update) + String.format(Locale.US, " (%1$s)", AndroidUtilities.formatFileSize(update.document.size)));
        } else {
            this.acceptTextView.setText(LocaleController.getString("Update", R.string.Update));
        }
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileLoadFailed);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        if (check) {
            TLRPC.TL_help_getAppUpdate req = new TLRPC.TL_help_getAppUpdate();
            try {
                req.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
            } catch (Exception e) {
            }
            if (req.source == null) {
                req.source = "";
            }
            ConnectionsManager.getInstance(this.accountNum).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.BlockingUpdateView$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BlockingUpdateView.this.m2221lambda$show$3$orgtelegramuiComponentsBlockingUpdateView(tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$show$3$org-telegram-ui-Components-BlockingUpdateView */
    public /* synthetic */ void m2221lambda$show$3$orgtelegramuiComponentsBlockingUpdateView(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.BlockingUpdateView$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                BlockingUpdateView.this.m2220lambda$show$2$orgtelegramuiComponentsBlockingUpdateView(response);
            }
        });
    }

    /* renamed from: lambda$show$2$org-telegram-ui-Components-BlockingUpdateView */
    public /* synthetic */ void m2220lambda$show$2$orgtelegramuiComponentsBlockingUpdateView(TLObject response) {
        if (response instanceof TLRPC.TL_help_appUpdate) {
            TLRPC.TL_help_appUpdate res = (TLRPC.TL_help_appUpdate) response;
            if (!res.can_not_skip) {
                setVisibility(8);
                SharedConfig.pendingAppUpdate = null;
                SharedConfig.saveConfig();
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        this.gradientDrawableTop.setBounds(this.scrollView.getLeft(), this.scrollView.getTop(), this.scrollView.getRight(), this.scrollView.getTop() + AndroidUtilities.dp(16.0f));
        this.gradientDrawableTop.draw(canvas);
        this.gradientDrawableBottom.setBounds(this.scrollView.getLeft(), this.scrollView.getBottom() - AndroidUtilities.dp(18.0f), this.scrollView.getRight(), this.scrollView.getBottom());
        this.gradientDrawableBottom.draw(canvas);
    }
}
