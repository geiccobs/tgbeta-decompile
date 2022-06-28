package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ReplacementSpan;
import android.util.Base64;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.core.net.MailTo;
import androidx.dynamicanimation.animation.DynamicAnimation;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.extractor.ts.TsExtractor;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import j$.util.Comparator;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedPhoneNumberEditText;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TextViewSwitcher;
import org.telegram.ui.Components.TransformableLoginButtonView;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.CountrySelectActivity;
import org.telegram.ui.LoginActivity;
/* loaded from: classes4.dex */
public class LoginActivity extends BaseFragment {
    public static final int AUTH_TYPE_CALL = 4;
    public static final int AUTH_TYPE_FLASH_CALL = 3;
    public static final int AUTH_TYPE_MESSAGE = 1;
    public static final int AUTH_TYPE_MISSED_CALL = 11;
    public static final int AUTH_TYPE_SMS = 2;
    private static final int COUNTRY_STATE_EMPTY = 1;
    private static final int COUNTRY_STATE_INVALID = 2;
    private static final int COUNTRY_STATE_NOT_SET_OR_VALID = 0;
    private static final int DONE_TYPE_ACTION = 1;
    private static final int DONE_TYPE_FLOATING = 0;
    private static final int MODE_CANCEL_ACCOUNT_DELETION = 1;
    private static final int MODE_CHANGE_PHONE_NUMBER = 2;
    private static final int MODE_LOGIN = 0;
    private static final int SHOW_DELAY;
    private static final int VIEW_CODE_CALL = 4;
    private static final int VIEW_CODE_FLASH_CALL = 3;
    private static final int VIEW_CODE_MESSAGE = 1;
    private static final int VIEW_CODE_MISSED_CALL = 11;
    private static final int VIEW_CODE_SMS = 2;
    private static final int VIEW_NEW_PASSWORD_STAGE_1 = 9;
    private static final int VIEW_NEW_PASSWORD_STAGE_2 = 10;
    private static final int VIEW_PASSWORD = 6;
    private static final int VIEW_PHONE_INPUT = 0;
    private static final int VIEW_RECOVER = 7;
    private static final int VIEW_REGISTER = 5;
    private static final int VIEW_RESET_WAIT = 8;
    private static final int done_button = 1;
    private int activityMode;
    private Runnable animationFinishCallback;
    private ImageView backButtonView;
    private AlertDialog cancelDeleteProgressDialog;
    private TLRPC.TL_auth_sentCode cancelDeletionCode;
    private Bundle cancelDeletionParams;
    private String cancelDeletionPhone;
    private boolean checkPermissions;
    private boolean checkShowPermissions;
    private int currentDoneType;
    private TLRPC.TL_help_termsOfService currentTermsOfService;
    private int currentViewNum;
    private boolean customKeyboardWasVisible;
    private boolean[] doneButtonVisible;
    private AnimatorSet doneItemAnimation;
    private boolean[] doneProgressVisible;
    private Runnable[] editDoneCallback;
    private VerticalPositionAutoAnimator floatingAutoAnimator;
    private FrameLayout floatingButtonContainer;
    private TransformableLoginButtonView floatingButtonIcon;
    private RadialProgressView floatingProgressView;
    private View introView;
    private boolean isAnimatingIntro;
    private Runnable keyboardHideCallback;
    private LinearLayout keyboardLinearLayout;
    private CustomPhoneKeyboardView keyboardView;
    private boolean needRequestPermissions;
    private boolean newAccount;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    private Dialog permissionsShowDialog;
    private ArrayList<String> permissionsShowItems;
    private PhoneNumberConfirmView phoneNumberConfirmView;
    private boolean[] postedEditDoneCallback;
    private int progressRequestId;
    private RadialProgressView radialProgressView;
    private boolean restoringState;
    private AnimatorSet[] showDoneAnimation;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private FrameLayout slideViewsContainer;
    private TextView startMessagingButton;
    private boolean syncContacts;
    private boolean testBackend;
    private SlideView[] views;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ActivityMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface AuthType {
    }

    /* loaded from: classes4.dex */
    private @interface CountryState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    private @interface ViewNumber {
    }

    static {
        SHOW_DELAY = SharedConfig.getDevicePerformanceClass() <= 1 ? 150 : 100;
    }

    /* loaded from: classes4.dex */
    public static class ProgressView extends View {
        private boolean animating;
        private long duration;
        private final Paint paint;
        private final Paint paint2;
        private float radius;
        private long startTime;
        private final Path path = new Path();
        private final RectF rect = new RectF();
        private final RectF boundsRect = new RectF();

        public ProgressView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.paint = paint;
            Paint paint2 = new Paint(1);
            this.paint2 = paint2;
            paint.setColor(Theme.getColor(Theme.key_login_progressInner));
            paint2.setColor(Theme.getColor(Theme.key_login_progressOuter));
        }

        public void startProgressAnimation(long duration) {
            this.animating = true;
            this.duration = duration;
            this.startTime = System.currentTimeMillis();
            invalidate();
        }

        public void resetProgressAnimation() {
            this.duration = 0L;
            this.startTime = 0L;
            this.animating = false;
            invalidate();
        }

        public boolean isProgressAnimationRunning() {
            return this.animating;
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            this.path.rewind();
            this.radius = h / 2.0f;
            this.boundsRect.set(0.0f, 0.0f, w, h);
            this.rect.set(this.boundsRect);
            Path path = this.path;
            RectF rectF = this.boundsRect;
            float f = this.radius;
            path.addRoundRect(rectF, f, f, Path.Direction.CW);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float progress;
            if (this.duration > 0) {
                progress = Math.min(1.0f, ((float) (System.currentTimeMillis() - this.startTime)) / ((float) this.duration));
            } else {
                progress = 0.0f;
            }
            canvas.clipPath(this.path);
            RectF rectF = this.boundsRect;
            float f = this.radius;
            canvas.drawRoundRect(rectF, f, f, this.paint);
            this.rect.right = this.boundsRect.right * progress;
            RectF rectF2 = this.rect;
            float f2 = this.radius;
            canvas.drawRoundRect(rectF2, f2, f2, this.paint2);
            boolean z = this.animating & (this.duration > 0 && progress < 1.0f);
            this.animating = z;
            if (z) {
                postInvalidateOnAnimation();
            }
        }
    }

    public LoginActivity() {
        this.views = new SlideView[12];
        this.permissionsItems = new ArrayList<>();
        this.permissionsShowItems = new ArrayList<>();
        this.checkPermissions = true;
        this.checkShowPermissions = true;
        this.syncContacts = true;
        this.testBackend = false;
        this.activityMode = 0;
        this.showDoneAnimation = new AnimatorSet[2];
        this.doneButtonVisible = new boolean[]{true, false};
        this.customKeyboardWasVisible = false;
        this.doneProgressVisible = new boolean[2];
        this.editDoneCallback = new Runnable[2];
        this.postedEditDoneCallback = new boolean[2];
    }

    public LoginActivity(int account) {
        this.views = new SlideView[12];
        this.permissionsItems = new ArrayList<>();
        this.permissionsShowItems = new ArrayList<>();
        this.checkPermissions = true;
        this.checkShowPermissions = true;
        this.syncContacts = true;
        this.testBackend = false;
        this.activityMode = 0;
        this.showDoneAnimation = new AnimatorSet[2];
        this.doneButtonVisible = new boolean[]{true, false};
        this.customKeyboardWasVisible = false;
        this.doneProgressVisible = new boolean[2];
        this.editDoneCallback = new Runnable[2];
        this.postedEditDoneCallback = new boolean[2];
        this.currentAccount = account;
        this.newAccount = true;
    }

    public LoginActivity cancelAccountDeletion(String phone, Bundle params, TLRPC.TL_auth_sentCode sentCode) {
        this.cancelDeletionPhone = phone;
        this.cancelDeletionParams = params;
        this.cancelDeletionCode = sentCode;
        this.activityMode = 1;
        return this;
    }

    public LoginActivity changePhoneNumber() {
        this.activityMode = 2;
        return this;
    }

    public boolean isInCancelAccountDeletionMode() {
        return this.activityMode == 1;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        Runnable[] runnableArr;
        super.onFragmentDestroy();
        int a = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (a >= slideViewArr.length) {
                break;
            }
            if (slideViewArr[a] != null) {
                slideViewArr[a].onDestroyActivity();
            }
            a++;
        }
        AlertDialog alertDialog = this.cancelDeleteProgressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.cancelDeleteProgressDialog = null;
        }
        for (Runnable callback : this.editDoneCallback) {
            if (callback != null) {
                AndroidUtilities.cancelRunOnUIThread(callback);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:114:0x03f3  */
    /* JADX WARN: Removed duplicated region for block: B:123:0x0402  */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.view.View createView(android.content.Context r32) {
        /*
            Method dump skipped, instructions count: 1083
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LoginActivity.createView(android.content.Context):android.view.View");
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3731lambda$createView$0$orgtelegramuiLoginActivity(int keyboardHeight, boolean isWidthGreater) {
        Runnable runnable;
        if (keyboardHeight > AndroidUtilities.dp(20.0f) && isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
        }
        if (keyboardHeight <= AndroidUtilities.dp(20.0f) && (runnable = this.keyboardHideCallback) != null) {
            runnable.run();
            this.keyboardHideCallback = null;
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3732lambda$createView$1$orgtelegramuiLoginActivity(View view) {
        onDoneButtonPressed();
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3733lambda$createView$2$orgtelegramuiLoginActivity(DynamicAnimation animation, float value, float velocity) {
        PhoneNumberConfirmView phoneNumberConfirmView = this.phoneNumberConfirmView;
        if (phoneNumberConfirmView == null) {
            return;
        }
        phoneNumberConfirmView.updateFabPosition();
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3734lambda$createView$3$orgtelegramuiLoginActivity(View v) {
        if (onBackPressed()) {
            finishFragment();
        }
    }

    public boolean isCustomKeyboardForceDisabled() {
        return AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y || AndroidUtilities.isTablet() || AndroidUtilities.isAccessibilityTouchExplorationEnabled();
    }

    public boolean isCustomKeyboardVisible() {
        return this.views[this.currentViewNum].hasCustomKeyboard() && !isCustomKeyboardForceDisabled();
    }

    private void setCustomKeyboardVisible(boolean visible, boolean animate) {
        if (this.customKeyboardWasVisible != visible || !animate) {
            this.customKeyboardWasVisible = visible;
            if (isCustomKeyboardForceDisabled()) {
                visible = false;
            }
            if (visible) {
                AndroidUtilities.hideKeyboard(this.fragmentView);
                AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
                if (animate) {
                    ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
                    anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            LoginActivity.this.m3738lambda$setCustomKeyboardVisible$4$orgtelegramuiLoginActivity(valueAnimator);
                        }
                    });
                    anim.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.6
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animation) {
                            LoginActivity.this.keyboardView.setVisibility(0);
                        }
                    });
                    anim.start();
                    return;
                }
                this.keyboardView.setVisibility(0);
                return;
            }
            AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
            if (animate) {
                ValueAnimator anim2 = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(300L);
                anim2.setInterpolator(Easings.easeInOutQuad);
                anim2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda11
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        LoginActivity.this.m3739lambda$setCustomKeyboardVisible$5$orgtelegramuiLoginActivity(valueAnimator);
                    }
                });
                anim2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.7
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        LoginActivity.this.keyboardView.setVisibility(8);
                    }
                });
                anim2.start();
                return;
            }
            this.keyboardView.setVisibility(8);
        }
    }

    /* renamed from: lambda$setCustomKeyboardVisible$4$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3738lambda$setCustomKeyboardVisible$4$orgtelegramuiLoginActivity(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(val);
        this.keyboardView.setTranslationY((1.0f - val) * AndroidUtilities.dp(230.0f));
    }

    /* renamed from: lambda$setCustomKeyboardVisible$5$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3739lambda$setCustomKeyboardVisible$5$orgtelegramuiLoginActivity(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.keyboardView.setAlpha(val);
        this.keyboardView.setTranslationY((1.0f - val) * AndroidUtilities.dp(230.0f));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
        }
        AndroidUtilities.removeAltFocusable(getParentActivity(), this.classGuid);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        SlideView view;
        int time;
        super.onResume();
        if (this.newAccount) {
            ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        this.fragmentView.requestLayout();
        try {
            int i = this.currentViewNum;
            if (i >= 1 && i <= 4) {
                SlideView[] slideViewArr = this.views;
                if ((slideViewArr[i] instanceof LoginActivitySmsView) && (time = ((LoginActivitySmsView) slideViewArr[i]).openTime) != 0 && Math.abs((System.currentTimeMillis() / 1000) - time) >= 86400) {
                    this.views[this.currentViewNum].onBackPressed(true);
                    setPage(0, false, null, true);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        int i2 = this.currentViewNum;
        if (i2 == 0 && !this.needRequestPermissions && (view = this.views[i2]) != null) {
            view.onShow();
        }
        if (isCustomKeyboardVisible()) {
            AndroidUtilities.hideKeyboard(this.fragmentView);
            AndroidUtilities.requestAltFocusable(getParentActivity(), this.classGuid);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean hasForceLightStatusBar() {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration newConfig) {
        setCustomKeyboardVisible(this.views[this.currentViewNum].hasCustomKeyboard(), false);
        PhoneNumberConfirmView phoneNumberConfirmView = this.phoneNumberConfirmView;
        if (phoneNumberConfirmView == null) {
            return;
        }
        phoneNumberConfirmView.dismiss();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions.length == 0 || grantResults.length == 0) {
            return;
        }
        boolean granted = grantResults[0] == 0;
        if (requestCode == 6) {
            this.checkPermissions = false;
            int i = this.currentViewNum;
            if (i != 0) {
                return;
            }
            ((PhoneView) this.views[i]).confirmedNumber = true;
            this.views[this.currentViewNum].onNextPressed(null);
        } else if (requestCode == 7) {
            this.checkShowPermissions = false;
            int i2 = this.currentViewNum;
            if (i2 == 0) {
                ((PhoneView) this.views[i2]).fillNumber();
            }
        } else if (requestCode == 20) {
            if (!granted) {
                return;
            }
            ((LoginActivityRegisterView) this.views[5]).imageUpdater.openCamera();
        } else if (requestCode == 151 && granted) {
            final LoginActivityRegisterView registerView = (LoginActivityRegisterView) this.views[5];
            registerView.post(new Runnable() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.this.imageUpdater.openGallery();
                }
            });
        }
    }

    public static Bundle loadCurrentState(boolean newAccount) {
        if (newAccount) {
            return null;
        }
        try {
            Bundle bundle = new Bundle();
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0);
            Map<String, ?> params = preferences.getAll();
            for (Map.Entry<String, ?> entry : params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String[] args = key.split("_\\|_");
                if (args.length == 1) {
                    if (value instanceof String) {
                        bundle.putString(key, (String) value);
                    } else if (value instanceof Integer) {
                        bundle.putInt(key, ((Integer) value).intValue());
                    }
                } else if (args.length == 2) {
                    Bundle inner = bundle.getBundle(args[0]);
                    if (inner == null) {
                        inner = new Bundle();
                        bundle.putBundle(args[0], inner);
                    }
                    if (value instanceof String) {
                        inner.putString(args[1], (String) value);
                    } else if (value instanceof Integer) {
                        inner.putInt(args[1], ((Integer) value).intValue());
                    }
                }
            }
            return bundle;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private void clearCurrentState() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private void putBundleToEditor(Bundle bundle, SharedPreferences.Editor editor, String prefix) {
        Set<String> keys = bundle.keySet();
        for (String key : keys) {
            Object obj = bundle.get(key);
            if (obj instanceof String) {
                if (prefix != null) {
                    editor.putString(prefix + "_|_" + key, (String) obj);
                } else {
                    editor.putString(key, (String) obj);
                }
            } else if (obj instanceof Integer) {
                if (prefix != null) {
                    editor.putInt(prefix + "_|_" + key, ((Integer) obj).intValue());
                } else {
                    editor.putInt(key, ((Integer) obj).intValue());
                }
            } else if (obj instanceof Bundle) {
                putBundleToEditor((Bundle) obj, editor, key);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onDialogDismiss(Dialog dialog) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (dialog == this.permissionsDialog && !this.permissionsItems.isEmpty() && getParentActivity() != null) {
                try {
                    getParentActivity().requestPermissions((String[]) this.permissionsItems.toArray(new String[0]), 6);
                } catch (Exception e) {
                }
            } else if (dialog == this.permissionsShowDialog && !this.permissionsShowItems.isEmpty() && getParentActivity() != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.this.m3736lambda$onDialogDismiss$7$orgtelegramuiLoginActivity();
                    }
                }, 200L);
                try {
                    getParentActivity().requestPermissions((String[]) this.permissionsShowItems.toArray(new String[0]), 7);
                } catch (Exception e2) {
                }
            }
        }
    }

    /* renamed from: lambda$onDialogDismiss$7$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3736lambda$onDialogDismiss$7$orgtelegramuiLoginActivity() {
        this.needRequestPermissions = false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        int i = this.currentViewNum;
        if (i == 0) {
            int a = 0;
            while (true) {
                SlideView[] slideViewArr = this.views;
                if (a < slideViewArr.length) {
                    if (slideViewArr[a] != null) {
                        slideViewArr[a].onDestroyActivity();
                    }
                    a++;
                } else {
                    clearCurrentState();
                    return true;
                }
            }
        } else {
            if (i == 6) {
                this.views[i].onBackPressed(true);
                setPage(0, true, null, true);
            } else if (i == 7 || i == 8) {
                this.views[i].onBackPressed(true);
                setPage(6, true, null, true);
            } else if ((i >= 1 && i <= 4) || i == 11) {
                if (this.views[i].onBackPressed(false)) {
                    setPage(0, true, null, true);
                }
            } else if (i != 5) {
                if (i == 9) {
                    this.views[i].onBackPressed(true);
                    setPage(7, true, null, true);
                } else if (i == 10) {
                    this.views[i].onBackPressed(true);
                    setPage(9, true, null, true);
                }
            } else {
                ((LoginActivityRegisterView) this.views[i]).wrongNumber.callOnClick();
            }
            return false;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        LoginActivityRegisterView registerView = (LoginActivityRegisterView) this.views[5];
        if (registerView == null) {
            return;
        }
        registerView.imageUpdater.onActivityResult(requestCode, resultCode, data);
    }

    public void needShowAlert(String title, String text) {
        if (text == null || getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(title);
        builder.setMessage(text);
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        showDialog(builder.create());
    }

    public void onFieldError(final View view, boolean allowErrorSelection) {
        view.performHapticFeedback(3, 2);
        AndroidUtilities.shakeViewSpring(view, 3.5f);
        if (allowErrorSelection && (view instanceof OutlineTextContainerView)) {
            Runnable callback = (Runnable) view.getTag(R.id.timeout_callback);
            if (callback != null) {
                view.removeCallbacks(callback);
            }
            final OutlineTextContainerView outlineTextContainerView = (OutlineTextContainerView) view;
            AtomicReference<Runnable> timeoutCallbackRef = new AtomicReference<>();
            final EditText editText = outlineTextContainerView.getAttachedEditText();
            final TextWatcher textWatcher = new AnonymousClass8(editText, timeoutCallbackRef);
            outlineTextContainerView.animateError(1.0f);
            Runnable timeoutCallback = new Runnable() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.lambda$onFieldError$9(OutlineTextContainerView.this, view, editText, textWatcher);
                }
            };
            timeoutCallbackRef.set(timeoutCallback);
            view.postDelayed(timeoutCallback, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            view.setTag(R.id.timeout_callback, timeoutCallback);
            if (editText != null) {
                editText.addTextChangedListener(textWatcher);
            }
        }
    }

    /* renamed from: org.telegram.ui.LoginActivity$8 */
    /* loaded from: classes4.dex */
    public class AnonymousClass8 implements TextWatcher {
        final /* synthetic */ EditText val$editText;
        final /* synthetic */ AtomicReference val$timeoutCallbackRef;

        AnonymousClass8(EditText editText, AtomicReference atomicReference) {
            LoginActivity.this = this$0;
            this.val$editText = editText;
            this.val$timeoutCallbackRef = atomicReference;
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            final EditText editText = this.val$editText;
            final AtomicReference atomicReference = this.val$timeoutCallbackRef;
            editText.post(new Runnable() { // from class: org.telegram.ui.LoginActivity$8$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.AnonymousClass8.this.m3747lambda$beforeTextChanged$0$orgtelegramuiLoginActivity$8(editText, atomicReference);
                }
            });
        }

        /* renamed from: lambda$beforeTextChanged$0$org-telegram-ui-LoginActivity$8 */
        public /* synthetic */ void m3747lambda$beforeTextChanged$0$orgtelegramuiLoginActivity$8(EditText editText, AtomicReference timeoutCallbackRef) {
            editText.removeTextChangedListener(this);
            editText.removeCallbacks((Runnable) timeoutCallbackRef.get());
            ((Runnable) timeoutCallbackRef.get()).run();
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
        }
    }

    public static /* synthetic */ void lambda$onFieldError$9(OutlineTextContainerView outlineTextContainerView, View view, final EditText editText, final TextWatcher textWatcher) {
        outlineTextContainerView.animateError(0.0f);
        view.setTag(R.id.timeout_callback, null);
        if (editText != null) {
            editText.post(new Runnable() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    editText.removeTextChangedListener(textWatcher);
                }
            });
        }
    }

    public static void needShowInvalidAlert(BaseFragment fragment, String phoneNumber, boolean banned) {
        needShowInvalidAlert(fragment, phoneNumber, null, banned);
    }

    public static void needShowInvalidAlert(final BaseFragment fragment, final String phoneNumber, PhoneInputData inputData, final boolean banned) {
        if (fragment == null || fragment.getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity());
        if (banned) {
            builder.setTitle(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle));
            builder.setMessage(LocaleController.getString("BannedPhoneNumber", R.string.BannedPhoneNumber));
        } else if (inputData != null && inputData.patterns != null && !inputData.patterns.isEmpty() && inputData.country != null) {
            int patternLength = Integer.MAX_VALUE;
            for (String pattern : inputData.patterns) {
                int length = pattern.replace(" ", "").length();
                if (length < patternLength) {
                    patternLength = length;
                }
            }
            if (PhoneFormat.stripExceptNumbers(phoneNumber).length() - inputData.country.code.length() < patternLength) {
                builder.setTitle(LocaleController.getString((int) R.string.WrongNumberFormat));
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ShortNumberInfo", R.string.ShortNumberInfo, inputData.country.name, inputData.phoneNumber)));
            } else {
                builder.setTitle(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle));
                builder.setMessage(LocaleController.getString((int) R.string.InvalidPhoneNumber));
            }
        } else {
            builder.setTitle(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle));
            builder.setMessage(LocaleController.getString((int) R.string.InvalidPhoneNumber));
        }
        builder.setNeutralButton(LocaleController.getString("BotHelp", R.string.BotHelp), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda20
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LoginActivity.lambda$needShowInvalidAlert$10(banned, phoneNumber, fragment, dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        fragment.showDialog(builder.create());
    }

    public static /* synthetic */ void lambda$needShowInvalidAlert$10(boolean banned, String phoneNumber, BaseFragment fragment, DialogInterface dialog, int which) {
        try {
            PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            String version = String.format(Locale.US, "%s (%d)", pInfo.versionName, Integer.valueOf(pInfo.versionCode));
            Intent mailer = new Intent("android.intent.action.SENDTO");
            mailer.setData(Uri.parse(MailTo.MAILTO_SCHEME));
            String[] strArr = new String[1];
            strArr[0] = banned ? "recover@telegram.org" : "login@stel.com";
            mailer.putExtra("android.intent.extra.EMAIL", strArr);
            if (banned) {
                mailer.putExtra("android.intent.extra.SUBJECT", "Banned phone number: " + phoneNumber);
                mailer.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + phoneNumber + "\nBut Telegram says it's banned. Please help.\n\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            } else {
                mailer.putExtra("android.intent.extra.SUBJECT", "Invalid phone number: " + phoneNumber);
                mailer.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + phoneNumber + "\nBut Telegram says it's invalid. Please help.\n\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            }
            fragment.getParentActivity().startActivity(Intent.createChooser(mailer, "Send email..."));
        } catch (Exception e) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(fragment.getParentActivity());
            builder2.setTitle(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle));
            builder2.setMessage(LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            fragment.showDialog(builder2.create());
        }
    }

    public void showDoneButton(final boolean show, boolean animated) {
        Interpolator interpolator;
        int duration;
        int i = this.currentDoneType;
        final boolean floating = i == 0;
        if (this.doneButtonVisible[i] == show) {
            return;
        }
        AnimatorSet[] animatorSetArr = this.showDoneAnimation;
        if (animatorSetArr[i] != null) {
            if (animated) {
                animatorSetArr[i].removeAllListeners();
            }
            this.showDoneAnimation[this.currentDoneType].cancel();
        }
        boolean[] zArr = this.doneButtonVisible;
        int i2 = this.currentDoneType;
        zArr[i2] = show;
        if (animated) {
            this.showDoneAnimation[i2] = new AnimatorSet();
            if (show) {
                if (floating) {
                    if (this.floatingButtonContainer.getVisibility() != 0) {
                        this.floatingAutoAnimator.setOffsetY(AndroidUtilities.dpf2(70.0f));
                        this.floatingButtonContainer.setVisibility(0);
                    }
                    ValueAnimator offsetAnimator = ValueAnimator.ofFloat(this.floatingAutoAnimator.getOffsetY(), 0.0f);
                    offsetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda14
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            LoginActivity.this.m3740lambda$showDoneButton$11$orgtelegramuiLoginActivity(valueAnimator);
                        }
                    });
                    this.showDoneAnimation[this.currentDoneType].play(offsetAnimator);
                }
            } else if (floating) {
                ValueAnimator offsetAnimator2 = ValueAnimator.ofFloat(this.floatingAutoAnimator.getOffsetY(), AndroidUtilities.dpf2(70.0f));
                offsetAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda15
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        LoginActivity.this.m3741lambda$showDoneButton$12$orgtelegramuiLoginActivity(valueAnimator);
                    }
                });
                this.showDoneAnimation[this.currentDoneType].play(offsetAnimator2);
            }
            this.showDoneAnimation[this.currentDoneType].addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.9
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (LoginActivity.this.showDoneAnimation[!floating ? 1 : 0] != null && LoginActivity.this.showDoneAnimation[!floating ? 1 : 0].equals(animation) && !show) {
                        if (floating) {
                            LoginActivity.this.floatingButtonContainer.setVisibility(8);
                        }
                        if (floating && LoginActivity.this.floatingButtonIcon.getAlpha() != 1.0f) {
                            LoginActivity.this.floatingButtonIcon.setAlpha(1.0f);
                            LoginActivity.this.floatingButtonIcon.setScaleX(1.0f);
                            LoginActivity.this.floatingButtonIcon.setScaleY(1.0f);
                            LoginActivity.this.floatingButtonIcon.setVisibility(0);
                            LoginActivity.this.floatingButtonContainer.setEnabled(true);
                            LoginActivity.this.floatingProgressView.setAlpha(0.0f);
                            LoginActivity.this.floatingProgressView.setScaleX(0.1f);
                            LoginActivity.this.floatingProgressView.setScaleY(0.1f);
                            LoginActivity.this.floatingProgressView.setVisibility(4);
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (LoginActivity.this.showDoneAnimation[!floating ? 1 : 0] != null && LoginActivity.this.showDoneAnimation[!floating ? 1 : 0].equals(animation)) {
                        LoginActivity.this.showDoneAnimation[!floating ? 1 : 0] = null;
                    }
                }
            });
            if (floating) {
                if (show) {
                    duration = 200;
                    interpolator = AndroidUtilities.decelerateInterpolator;
                } else {
                    duration = 150;
                    interpolator = AndroidUtilities.accelerateInterpolator;
                }
            } else {
                duration = 150;
                interpolator = null;
            }
            this.showDoneAnimation[this.currentDoneType].setDuration(duration);
            this.showDoneAnimation[this.currentDoneType].setInterpolator(interpolator);
            this.showDoneAnimation[this.currentDoneType].start();
        } else if (show) {
            if (floating) {
                this.floatingButtonContainer.setVisibility(0);
                this.floatingAutoAnimator.setOffsetY(0.0f);
            }
        } else if (floating) {
            this.floatingButtonContainer.setVisibility(8);
            this.floatingAutoAnimator.setOffsetY(AndroidUtilities.dpf2(70.0f));
        }
    }

    /* renamed from: lambda$showDoneButton$11$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3740lambda$showDoneButton$11$orgtelegramuiLoginActivity(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(val);
        this.floatingButtonContainer.setAlpha(1.0f - (val / AndroidUtilities.dpf2(70.0f)));
    }

    /* renamed from: lambda$showDoneButton$12$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3741lambda$showDoneButton$12$orgtelegramuiLoginActivity(ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.floatingAutoAnimator.setOffsetY(val);
        this.floatingButtonContainer.setAlpha(1.0f - (val / AndroidUtilities.dpf2(70.0f)));
    }

    public void onDoneButtonPressed() {
        if (!this.doneButtonVisible[this.currentDoneType]) {
            return;
        }
        if (this.radialProgressView.getTag() != null) {
            if (getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString((int) R.string.AppName));
            builder.setMessage(LocaleController.getString("StopLoading", R.string.StopLoading));
            builder.setPositiveButton(LocaleController.getString("WaitMore", R.string.WaitMore), null);
            builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda18
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.this.m3737lambda$onDoneButtonPressed$13$orgtelegramuiLoginActivity(dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return;
        }
        this.views[this.currentViewNum].onNextPressed(null);
    }

    /* renamed from: lambda$onDoneButtonPressed$13$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3737lambda$onDoneButtonPressed$13$orgtelegramuiLoginActivity(DialogInterface dialogInterface, int i) {
        this.views[this.currentViewNum].onCancelPressed();
        needHideProgress(true);
    }

    private void showEditDoneProgress(boolean show, boolean animated) {
        showEditDoneProgress(show, animated, false);
    }

    private void showEditDoneProgress(final boolean show, final boolean animated, boolean fromCallback) {
        if (animated && this.doneProgressVisible[this.currentDoneType] == show && !fromCallback) {
            return;
        }
        int i = this.currentDoneType;
        final boolean floating = i == 0;
        if (!fromCallback && !floating) {
            this.doneProgressVisible[i] = show;
            final int doneType = this.currentDoneType;
            if (animated) {
                if (this.postedEditDoneCallback[i]) {
                    AndroidUtilities.cancelRunOnUIThread(this.editDoneCallback[i]);
                    this.postedEditDoneCallback[this.currentDoneType] = false;
                    return;
                } else if (show) {
                    Runnable[] runnableArr = this.editDoneCallback;
                    Runnable runnable = new Runnable() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda7
                        @Override // java.lang.Runnable
                        public final void run() {
                            LoginActivity.this.m3742lambda$showEditDoneProgress$14$orgtelegramuiLoginActivity(doneType, show, animated);
                        }
                    };
                    runnableArr[i] = runnable;
                    AndroidUtilities.runOnUIThread(runnable, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                    this.postedEditDoneCallback[this.currentDoneType] = true;
                    return;
                }
            }
        } else {
            this.postedEditDoneCallback[i] = false;
            this.doneProgressVisible[i] = show;
        }
        AnimatorSet animatorSet = this.doneItemAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        float f = 0.0f;
        if (animated) {
            this.doneItemAnimation = new AnimatorSet();
            float[] fArr = new float[2];
            fArr[0] = show ? 0.0f : 1.0f;
            if (show) {
                f = 1.0f;
            }
            fArr[1] = f;
            ValueAnimator animator = ValueAnimator.ofFloat(fArr);
            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.10
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    if (show) {
                        if (floating) {
                            LoginActivity.this.floatingButtonIcon.setVisibility(0);
                            LoginActivity.this.floatingProgressView.setVisibility(0);
                            LoginActivity.this.floatingButtonContainer.setEnabled(false);
                            return;
                        }
                        LoginActivity.this.radialProgressView.setVisibility(0);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (floating) {
                        if (!show) {
                            LoginActivity.this.floatingProgressView.setVisibility(4);
                            LoginActivity.this.floatingButtonIcon.setVisibility(0);
                            LoginActivity.this.floatingButtonContainer.setEnabled(true);
                        } else {
                            LoginActivity.this.floatingButtonIcon.setVisibility(4);
                            LoginActivity.this.floatingProgressView.setVisibility(0);
                        }
                    } else if (!show) {
                        LoginActivity.this.radialProgressView.setVisibility(4);
                    }
                    if (LoginActivity.this.doneItemAnimation != null && LoginActivity.this.doneItemAnimation.equals(animation)) {
                        LoginActivity.this.doneItemAnimation = null;
                    }
                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda17
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LoginActivity.this.m3743lambda$showEditDoneProgress$15$orgtelegramuiLoginActivity(floating, valueAnimator);
                }
            });
            this.doneItemAnimation.playTogether(animator);
            this.doneItemAnimation.setDuration(150L);
            this.doneItemAnimation.start();
        } else if (show) {
            if (floating) {
                this.floatingProgressView.setVisibility(0);
                this.floatingButtonIcon.setVisibility(4);
                this.floatingButtonContainer.setEnabled(false);
                this.floatingButtonIcon.setScaleX(0.1f);
                this.floatingButtonIcon.setScaleY(0.1f);
                this.floatingButtonIcon.setAlpha(0.0f);
                this.floatingProgressView.setScaleX(1.0f);
                this.floatingProgressView.setScaleY(1.0f);
                this.floatingProgressView.setAlpha(1.0f);
                return;
            }
            this.radialProgressView.setVisibility(0);
            this.radialProgressView.setScaleX(1.0f);
            this.radialProgressView.setScaleY(1.0f);
            this.radialProgressView.setAlpha(1.0f);
        } else {
            this.radialProgressView.setTag(null);
            if (floating) {
                this.floatingProgressView.setVisibility(4);
                this.floatingButtonIcon.setVisibility(0);
                this.floatingButtonContainer.setEnabled(true);
                this.floatingProgressView.setScaleX(0.1f);
                this.floatingProgressView.setScaleY(0.1f);
                this.floatingProgressView.setAlpha(0.0f);
                this.floatingButtonIcon.setScaleX(1.0f);
                this.floatingButtonIcon.setScaleY(1.0f);
                this.floatingButtonIcon.setAlpha(1.0f);
                return;
            }
            this.radialProgressView.setVisibility(4);
            this.radialProgressView.setScaleX(0.1f);
            this.radialProgressView.setScaleY(0.1f);
            this.radialProgressView.setAlpha(0.0f);
        }
    }

    /* renamed from: lambda$showEditDoneProgress$14$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3742lambda$showEditDoneProgress$14$orgtelegramuiLoginActivity(int doneType, boolean show, boolean animated) {
        int type = this.currentDoneType;
        this.currentDoneType = doneType;
        showEditDoneProgress(show, animated, true);
        this.currentDoneType = type;
    }

    /* renamed from: lambda$showEditDoneProgress$15$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3743lambda$showEditDoneProgress$15$orgtelegramuiLoginActivity(boolean floating, ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        if (floating) {
            float scale = ((1.0f - val) * 0.9f) + 0.1f;
            this.floatingButtonIcon.setScaleX(scale);
            this.floatingButtonIcon.setScaleY(scale);
            this.floatingButtonIcon.setAlpha(1.0f - val);
            float scale2 = (0.9f * val) + 0.1f;
            this.floatingProgressView.setScaleX(scale2);
            this.floatingProgressView.setScaleY(scale2);
            this.floatingProgressView.setAlpha(val);
            return;
        }
        float scale3 = (0.9f * val) + 0.1f;
        this.radialProgressView.setScaleX(scale3);
        this.radialProgressView.setScaleY(scale3);
        this.radialProgressView.setAlpha(val);
    }

    public void needShowProgress(int requestId) {
        needShowProgress(requestId, true);
    }

    public void needShowProgress(int requestId, boolean animated) {
        if (isInCancelAccountDeletionMode() && requestId == 0) {
            if (this.cancelDeleteProgressDialog != null || getParentActivity() == null || getParentActivity().isFinishing()) {
                return;
            }
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.cancelDeleteProgressDialog = alertDialog;
            alertDialog.setCanCancel(false);
            this.cancelDeleteProgressDialog.show();
            return;
        }
        this.progressRequestId = requestId;
        showEditDoneProgress(true, animated);
    }

    public void needHideProgress(boolean cancel) {
        needHideProgress(cancel, true);
    }

    public void needHideProgress(boolean cancel, boolean animated) {
        AlertDialog alertDialog;
        if (this.progressRequestId != 0) {
            if (cancel) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.progressRequestId, true);
            }
            this.progressRequestId = 0;
        }
        if (isInCancelAccountDeletionMode() && (alertDialog = this.cancelDeleteProgressDialog) != null) {
            alertDialog.dismiss();
            this.cancelDeleteProgressDialog = null;
        }
        showEditDoneProgress(false, animated);
    }

    public void setPage(int page, boolean animated, Bundle params, boolean back) {
        final boolean needFloatingButton = page == 0 || page == 5 || page == 6 || page == 9 || page == 10;
        int i = 8;
        if (needFloatingButton) {
            if (page == 0) {
                this.checkPermissions = true;
                this.checkShowPermissions = true;
            }
            this.currentDoneType = 1;
            showDoneButton(false, animated);
            showEditDoneProgress(false, animated);
            this.currentDoneType = 0;
            showEditDoneProgress(false, animated);
            if (!animated) {
                showDoneButton(true, false);
            }
        } else {
            this.currentDoneType = 0;
            showDoneButton(false, animated);
            showEditDoneProgress(false, animated);
            if (page != 8) {
                this.currentDoneType = 1;
            }
        }
        if (animated) {
            SlideView[] slideViewArr = this.views;
            final SlideView outView = slideViewArr[this.currentViewNum];
            SlideView newView = slideViewArr[page];
            this.currentViewNum = page;
            ImageView imageView = this.backButtonView;
            if (newView.needBackButton() || this.newAccount) {
                i = 0;
            }
            imageView.setVisibility(i);
            newView.setParams(params, false);
            setParentActivityTitle(newView.getHeaderName());
            newView.onShow();
            int i2 = AndroidUtilities.displaySize.x;
            if (back) {
                i2 = -i2;
            }
            newView.setX(i2);
            newView.setVisibility(0);
            AnimatorSet pagesAnimation = new AnimatorSet();
            pagesAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.11
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (LoginActivity.this.currentDoneType == 0 && needFloatingButton) {
                        LoginActivity.this.showDoneButton(true, true);
                    }
                    outView.setVisibility(8);
                    outView.setX(0.0f);
                }
            });
            Animator[] animatorArr = new Animator[2];
            Property property = View.TRANSLATION_X;
            float[] fArr = new float[1];
            int i3 = AndroidUtilities.displaySize.x;
            if (!back) {
                i3 = -i3;
            }
            fArr[0] = i3;
            animatorArr[0] = ObjectAnimator.ofFloat(outView, property, fArr);
            animatorArr[1] = ObjectAnimator.ofFloat(newView, View.TRANSLATION_X, 0.0f);
            pagesAnimation.playTogether(animatorArr);
            pagesAnimation.setDuration(300L);
            pagesAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            pagesAnimation.start();
            setCustomKeyboardVisible(newView.hasCustomKeyboard(), true);
            return;
        }
        this.backButtonView.setVisibility((this.views[page].needBackButton() || this.newAccount) ? 0 : 8);
        this.views[this.currentViewNum].setVisibility(8);
        this.currentViewNum = page;
        this.views[page].setParams(params, false);
        this.views[page].setVisibility(0);
        setParentActivityTitle(this.views[page].getHeaderName());
        this.views[page].onShow();
        setCustomKeyboardVisible(this.views[page].hasCustomKeyboard(), false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void saveSelfArgs(Bundle outState) {
        try {
            Bundle bundle = new Bundle();
            bundle.putInt("currentViewNum", this.currentViewNum);
            bundle.putInt("syncContacts", this.syncContacts ? 1 : 0);
            for (int a = 0; a <= this.currentViewNum; a++) {
                SlideView v = this.views[a];
                if (v != null) {
                    v.saveStateParams(bundle);
                }
            }
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            putBundleToEditor(bundle, editor, null);
            editor.commit();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void needFinishActivity(final boolean afterSignup, boolean showSetPasswordConfirm, int otherwiseRelogin) {
        if (getParentActivity() != null) {
            AndroidUtilities.setLightStatusBar(getParentActivity().getWindow(), false);
        }
        clearCurrentState();
        if (getParentActivity() instanceof LaunchActivity) {
            if (this.newAccount) {
                this.newAccount = false;
                ((LaunchActivity) getParentActivity()).switchToAccount(this.currentAccount, false, new GenericProvider() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda9
                    @Override // org.telegram.messenger.GenericProvider
                    public final Object provide(Object obj) {
                        return LoginActivity.lambda$needFinishActivity$16(afterSignup, (Void) obj);
                    }
                });
                finishFragment();
                return;
            }
            if (afterSignup && showSetPasswordConfirm) {
                TwoStepVerificationSetupActivity twoStepVerification = new TwoStepVerificationSetupActivity(6, null);
                twoStepVerification.setBlockingAlert(otherwiseRelogin);
                twoStepVerification.setFromRegistration(true);
                presentFragment(twoStepVerification, true);
            } else {
                Bundle args = new Bundle();
                args.putBoolean("afterSignup", afterSignup);
                DialogsActivity dialogsActivity = new DialogsActivity(args);
                presentFragment(dialogsActivity, true);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        } else if (getParentActivity() instanceof ExternalActionActivity) {
            ((ExternalActionActivity) getParentActivity()).onFinishLogin();
        }
    }

    public static /* synthetic */ DialogsActivity lambda$needFinishActivity$16(boolean afterSignup, Void obj) {
        Bundle args = new Bundle();
        args.putBoolean("afterSignup", afterSignup);
        return new DialogsActivity(args);
    }

    public void onAuthSuccess(TLRPC.TL_auth_authorization res) {
        onAuthSuccess(res, false);
    }

    public void onAuthSuccess(TLRPC.TL_auth_authorization res, boolean afterSignup) {
        MessagesController.getInstance(this.currentAccount).cleanup();
        ConnectionsManager.getInstance(this.currentAccount).setUserId(res.user.id);
        UserConfig.getInstance(this.currentAccount).clearConfig();
        MessagesController.getInstance(this.currentAccount).cleanup();
        UserConfig.getInstance(this.currentAccount).syncContacts = this.syncContacts;
        UserConfig.getInstance(this.currentAccount).setCurrentUser(res.user);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        MessagesStorage.getInstance(this.currentAccount).cleanup(true);
        ArrayList<TLRPC.User> users = new ArrayList<>();
        users.add(res.user);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, null, true, true);
        MessagesController.getInstance(this.currentAccount).putUser(res.user, false);
        ContactsController.getInstance(this.currentAccount).checkAppAccount();
        MessagesController.getInstance(this.currentAccount).checkPromoInfo(true);
        ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
        if (afterSignup) {
            MessagesController.getInstance(this.currentAccount).putDialogsEndReachedAfterRegistration();
        }
        MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName(AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME, false, true);
        needFinishActivity(afterSignup, res.setup_password_required, res.otherwise_relogin_days);
    }

    public void fillNextCodeParams(Bundle params, TLRPC.TL_auth_sentCode res) {
        fillNextCodeParams(params, res, true);
    }

    private void fillNextCodeParams(Bundle params, TLRPC.TL_auth_sentCode res, boolean animate) {
        params.putString("phoneHash", res.phone_code_hash);
        if (res.next_type instanceof TLRPC.TL_auth_codeTypeCall) {
            params.putInt("nextType", 4);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeFlashCall) {
            params.putInt("nextType", 3);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeSms) {
            params.putInt("nextType", 2);
        } else if (res.next_type instanceof TLRPC.TL_auth_codeTypeMissedCall) {
            params.putInt("nextType", 11);
        }
        if (res.type instanceof TLRPC.TL_auth_sentCodeTypeApp) {
            params.putInt(CommonProperties.TYPE, 1);
            params.putInt("length", res.type.length);
            setPage(1, animate, params, false);
            return;
        }
        if (res.timeout == 0) {
            res.timeout = 60;
        }
        params.putInt("timeout", res.timeout * 1000);
        if (res.type instanceof TLRPC.TL_auth_sentCodeTypeCall) {
            params.putInt(CommonProperties.TYPE, 4);
            params.putInt("length", res.type.length);
            setPage(4, animate, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeFlashCall) {
            params.putInt(CommonProperties.TYPE, 3);
            params.putString("pattern", res.type.pattern);
            setPage(3, animate, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeSms) {
            params.putInt(CommonProperties.TYPE, 2);
            params.putInt("length", res.type.length);
            setPage(2, animate, params, false);
        } else if (res.type instanceof TLRPC.TL_auth_sentCodeTypeMissedCall) {
            params.putInt(CommonProperties.TYPE, 11);
            params.putInt("length", res.type.length);
            params.putString("prefix", res.type.prefix);
            setPage(11, animate, params, false);
        }
    }

    /* loaded from: classes4.dex */
    public class PhoneView extends SlideView implements AdapterView.OnItemSelectedListener, NotificationCenter.NotificationCenterDelegate {
        private ImageView chevronRight;
        private View codeDividerView;
        private AnimatedPhoneNumberEditText codeField;
        private TextViewSwitcher countryButton;
        private OutlineTextContainerView countryOutlineView;
        private int countryState;
        private CountrySelectActivity.Country currentCountry;
        private boolean numberFilled;
        private AnimatedPhoneNumberEditText phoneField;
        private OutlineTextContainerView phoneOutlineView;
        private TextView plusTextView;
        private TextView subtitleView;
        private CheckBoxCell syncContactsBox;
        private CheckBoxCell testBackendCheckBox;
        private TextView titleView;
        private ArrayList<CountrySelectActivity.Country> countriesArray = new ArrayList<>();
        private HashMap<String, CountrySelectActivity.Country> codesMap = new HashMap<>();
        private HashMap<String, List<String>> phoneFormatMap = new HashMap<>();
        private boolean ignoreSelection = false;
        private boolean ignoreOnTextChange = false;
        private boolean ignoreOnPhoneChange = false;
        private boolean nextPressed = false;
        private boolean confirmedNumber = false;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public PhoneView(final Context context) {
            super(context);
            LoginActivity.this = this$0;
            this.countryState = 0;
            setOrientation(1);
            setGravity(17);
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 18.0f);
            this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            char c = 2;
            this.titleView.setText(LocaleController.getString(this$0.activityMode == 2 ? R.string.ChangePhoneNewNumber : R.string.YourNumber));
            this.titleView.setGravity(17);
            this.titleView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.titleView, LayoutHelper.createFrame(-1, -2.0f, 1, 32.0f, 0.0f, 32.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.subtitleView = textView2;
            textView2.setText(LocaleController.getString(this$0.activityMode == 2 ? R.string.ChangePhoneHelp : R.string.StartText));
            this.subtitleView.setTextSize(1, 14.0f);
            this.subtitleView.setGravity(17);
            this.subtitleView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.subtitleView, LayoutHelper.createLinear(-1, -2, 1, 32, 8, 32, 0));
            TextViewSwitcher textViewSwitcher = new TextViewSwitcher(context);
            this.countryButton = textViewSwitcher;
            textViewSwitcher.setFactory(new ViewSwitcher.ViewFactory() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda21
                @Override // android.widget.ViewSwitcher.ViewFactory
                public final View makeView() {
                    return LoginActivity.PhoneView.lambda$new$0(context);
                }
            });
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.text_in);
            anim.setInterpolator(Easings.easeInOutQuad);
            this.countryButton.setInAnimation(anim);
            ImageView imageView = new ImageView(context);
            this.chevronRight = imageView;
            imageView.setImageResource(R.drawable.msg_inputarrow);
            LinearLayout countryButtonLinearLayout = new LinearLayout(context);
            countryButtonLinearLayout.setOrientation(0);
            countryButtonLinearLayout.setGravity(16);
            countryButtonLinearLayout.addView(this.countryButton, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 0, 0));
            countryButtonLinearLayout.addView(this.chevronRight, LayoutHelper.createLinearRelatively(24.0f, 24.0f, 0, 0.0f, 0.0f, 14.0f, 0.0f));
            OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
            this.countryOutlineView = outlineTextContainerView;
            outlineTextContainerView.setText(LocaleController.getString((int) R.string.Country));
            this.countryOutlineView.addView(countryButtonLinearLayout, LayoutHelper.createFrame(-1, -1.0f, 48, 0.0f, 0.0f, 0.0f, 0.0f));
            this.countryOutlineView.setForceUseCenter(true);
            this.countryOutlineView.setFocusable(true);
            this.countryOutlineView.setContentDescription(LocaleController.getString((int) R.string.Country));
            this.countryOutlineView.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda18
                @Override // android.view.View.OnFocusChangeListener
                public final void onFocusChange(View view, boolean z) {
                    LoginActivity.PhoneView.this.m3859lambda$new$1$orgtelegramuiLoginActivity$PhoneView(view, z);
                }
            });
            addView(this.countryOutlineView, LayoutHelper.createLinear(-1, 58, 16.0f, 24.0f, 16.0f, 14.0f));
            this.countryOutlineView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda15
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LoginActivity.PhoneView.this.m3866lambda$new$4$orgtelegramuiLoginActivity$PhoneView(view);
                }
            });
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            OutlineTextContainerView outlineTextContainerView2 = new OutlineTextContainerView(context);
            this.phoneOutlineView = outlineTextContainerView2;
            outlineTextContainerView2.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 16, 16.0f, 8.0f, 16.0f, 8.0f));
            this.phoneOutlineView.setText(LocaleController.getString((int) R.string.PhoneNumber));
            addView(this.phoneOutlineView, LayoutHelper.createLinear(-1, 58, 16.0f, 8.0f, 16.0f, 8.0f));
            TextView textView3 = new TextView(context);
            this.plusTextView = textView3;
            textView3.setText("+");
            this.plusTextView.setTextSize(1, 16.0f);
            this.plusTextView.setFocusable(false);
            linearLayout.addView(this.plusTextView, LayoutHelper.createLinear(-2, -2));
            AnimatedPhoneNumberEditText animatedPhoneNumberEditText = new AnimatedPhoneNumberEditText(context) { // from class: org.telegram.ui.LoginActivity.PhoneView.1
                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
                    super.onFocusChanged(focused, direction, previouslyFocusedRect);
                    PhoneView.this.phoneOutlineView.animateSelection((focused || PhoneView.this.phoneField.isFocused()) ? 1.0f : 0.0f);
                    if (focused) {
                        LoginActivity.this.keyboardView.setEditText(this);
                    }
                }
            };
            this.codeField = animatedPhoneNumberEditText;
            animatedPhoneNumberEditText.setInputType(3);
            this.codeField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setPadding(AndroidUtilities.dp(10.0f), 0, 0, 0);
            this.codeField.setTextSize(1, 16.0f);
            this.codeField.setMaxLines(1);
            this.codeField.setGravity(19);
            this.codeField.setImeOptions(268435461);
            this.codeField.setBackground(null);
            if (Build.VERSION.SDK_INT >= 21) {
                this.codeField.setShowSoftInputOnFocus(!hasCustomKeyboard() || this$0.isCustomKeyboardForceDisabled());
            }
            this.codeField.setContentDescription(LocaleController.getString((int) R.string.LoginAccessibilityCountryCode));
            linearLayout.addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0f, 0.0f, 0.0f, 0.0f));
            this.codeField.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.LoginActivity.PhoneView.2
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                    if (!PhoneView.this.ignoreOnTextChange) {
                        PhoneView.this.ignoreOnTextChange = true;
                        String text = PhoneFormat.stripExceptNumbers(PhoneView.this.codeField.getText().toString());
                        PhoneView.this.codeField.setText(text);
                        if (text.length() == 0) {
                            PhoneView.this.setCountryButtonText(null);
                            PhoneView.this.phoneField.setHintText((String) null);
                            PhoneView.this.countryState = 1;
                        } else {
                            boolean ok = false;
                            String textToSet = null;
                            if (text.length() > 4) {
                                int a = 4;
                                while (true) {
                                    if (a < 1) {
                                        break;
                                    }
                                    String sub = text.substring(0, a);
                                    if (((CountrySelectActivity.Country) PhoneView.this.codesMap.get(sub)) != null) {
                                        ok = true;
                                        textToSet = text.substring(a) + PhoneView.this.phoneField.getText().toString();
                                        text = sub;
                                        PhoneView.this.codeField.setText(sub);
                                        break;
                                    }
                                    a--;
                                }
                                if (!ok) {
                                    textToSet = text.substring(1) + PhoneView.this.phoneField.getText().toString();
                                    AnimatedPhoneNumberEditText animatedPhoneNumberEditText2 = PhoneView.this.codeField;
                                    String substring = text.substring(0, 1);
                                    text = substring;
                                    animatedPhoneNumberEditText2.setText(substring);
                                }
                            }
                            CountrySelectActivity.Country lastMatchedCountry = null;
                            int matchedCountries = 0;
                            for (CountrySelectActivity.Country c2 : PhoneView.this.codesMap.values()) {
                                if (c2.code.startsWith(text)) {
                                    matchedCountries++;
                                    if (c2.code.equals(text)) {
                                        lastMatchedCountry = c2;
                                    }
                                }
                            }
                            if (matchedCountries == 1 && lastMatchedCountry != null && textToSet == null) {
                                textToSet = text.substring(lastMatchedCountry.code.length()) + PhoneView.this.phoneField.getText().toString();
                                AnimatedPhoneNumberEditText animatedPhoneNumberEditText3 = PhoneView.this.codeField;
                                String str = lastMatchedCountry.code;
                                text = str;
                                animatedPhoneNumberEditText3.setText(str);
                            }
                            CountrySelectActivity.Country country = (CountrySelectActivity.Country) PhoneView.this.codesMap.get(text);
                            if (country != null) {
                                PhoneView.this.ignoreSelection = true;
                                PhoneView.this.currentCountry = country;
                                PhoneView.this.setCountryHint(text, country);
                                PhoneView.this.countryState = 0;
                            } else {
                                PhoneView.this.setCountryButtonText(null);
                                PhoneView.this.phoneField.setHintText((String) null);
                                PhoneView.this.countryState = 2;
                            }
                            if (!ok) {
                                PhoneView.this.codeField.setSelection(PhoneView.this.codeField.getText().length());
                            }
                            if (textToSet != null) {
                                PhoneView.this.phoneField.requestFocus();
                                PhoneView.this.phoneField.setText(textToSet);
                                PhoneView.this.phoneField.setSelection(PhoneView.this.phoneField.length());
                            }
                        }
                        PhoneView.this.ignoreOnTextChange = false;
                    }
                }
            });
            this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda19
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView4, int i, KeyEvent keyEvent) {
                    return LoginActivity.PhoneView.this.m3867lambda$new$5$orgtelegramuiLoginActivity$PhoneView(textView4, i, keyEvent);
                }
            });
            this.codeDividerView = new View(context);
            LinearLayout.LayoutParams params = LayoutHelper.createLinear(0, -1, 4.0f, 8.0f, 12.0f, 8.0f);
            params.width = Math.max(2, AndroidUtilities.dp(0.5f));
            linearLayout.addView(this.codeDividerView, params);
            AnimatedPhoneNumberEditText animatedPhoneNumberEditText2 = new AnimatedPhoneNumberEditText(context) { // from class: org.telegram.ui.LoginActivity.PhoneView.3
                @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
                public boolean onKeyDown(int keyCode, KeyEvent event) {
                    if (keyCode == 67 && PhoneView.this.phoneField.length() == 0) {
                        PhoneView.this.codeField.requestFocus();
                        PhoneView.this.codeField.setSelection(PhoneView.this.codeField.length());
                        PhoneView.this.codeField.dispatchKeyEvent(event);
                    }
                    return super.onKeyDown(keyCode, event);
                }

                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public boolean onTouchEvent(MotionEvent event) {
                    if (event.getAction() == 0 && !LoginActivity.this.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                    return super.onTouchEvent(event);
                }

                @Override // org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
                public void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
                    super.onFocusChanged(focused, direction, previouslyFocusedRect);
                    PhoneView.this.phoneOutlineView.animateSelection((focused || PhoneView.this.codeField.isFocused()) ? 1.0f : 0.0f);
                    if (focused) {
                        LoginActivity.this.keyboardView.setEditText(this);
                        LoginActivity.this.keyboardView.setDispatchBackWhenEmpty(true);
                        if (PhoneView.this.countryState == 2) {
                            PhoneView.this.setCountryButtonText(LocaleController.getString((int) R.string.WrongCountry));
                        }
                    } else if (PhoneView.this.countryState == 2) {
                        PhoneView.this.setCountryButtonText(null);
                    }
                }
            };
            this.phoneField = animatedPhoneNumberEditText2;
            animatedPhoneNumberEditText2.setInputType(3);
            this.phoneField.setPadding(0, 0, 0, 0);
            this.phoneField.setCursorSize(AndroidUtilities.dp(20.0f));
            this.phoneField.setCursorWidth(1.5f);
            this.phoneField.setTextSize(1, 16.0f);
            this.phoneField.setMaxLines(1);
            this.phoneField.setGravity(19);
            this.phoneField.setImeOptions(268435461);
            this.phoneField.setBackground(null);
            if (Build.VERSION.SDK_INT >= 21) {
                this.phoneField.setShowSoftInputOnFocus(!hasCustomKeyboard() || this$0.isCustomKeyboardForceDisabled());
            }
            this.phoneField.setContentDescription(LocaleController.getString((int) R.string.PhoneNumber));
            linearLayout.addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0f));
            this.phoneField.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.LoginActivity.PhoneView.4
                private int actionPosition;
                private int characterAction = -1;

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (count == 0 && after == 1) {
                        this.characterAction = 1;
                    } else if (count == 1 && after == 0) {
                        if (s.charAt(start) == ' ' && start > 0) {
                            this.characterAction = 3;
                            this.actionPosition = start - 1;
                            return;
                        }
                        this.characterAction = 2;
                    } else {
                        this.characterAction = -1;
                    }
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    int i;
                    int i2;
                    if (!PhoneView.this.ignoreOnPhoneChange) {
                        int start = PhoneView.this.phoneField.getSelectionStart();
                        String str = PhoneView.this.phoneField.getText().toString();
                        if (this.characterAction == 3) {
                            str = str.substring(0, this.actionPosition) + str.substring(this.actionPosition + 1);
                            start--;
                        }
                        StringBuilder builder = new StringBuilder(str.length());
                        for (int a = 0; a < str.length(); a++) {
                            String ch = str.substring(a, a + 1);
                            if ("0123456789".contains(ch)) {
                                builder.append(ch);
                            }
                        }
                        PhoneView.this.ignoreOnPhoneChange = true;
                        String hint = PhoneView.this.phoneField.getHintText();
                        if (hint != null) {
                            int a2 = 0;
                            while (true) {
                                if (a2 >= builder.length()) {
                                    break;
                                } else if (a2 < hint.length()) {
                                    if (hint.charAt(a2) == ' ') {
                                        builder.insert(a2, ' ');
                                        a2++;
                                        if (start == a2 && (i2 = this.characterAction) != 2 && i2 != 3) {
                                            start++;
                                        }
                                    }
                                    a2++;
                                } else {
                                    builder.insert(a2, ' ');
                                    if (start == a2 + 1 && (i = this.characterAction) != 2 && i != 3) {
                                        start++;
                                    }
                                }
                            }
                        }
                        s.replace(0, s.length(), builder);
                        if (start >= 0) {
                            PhoneView.this.phoneField.setSelection(Math.min(start, PhoneView.this.phoneField.length()));
                        }
                        PhoneView.this.phoneField.onTextChange();
                        PhoneView.this.ignoreOnPhoneChange = false;
                    }
                }
            });
            this.phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda20
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView4, int i, KeyEvent keyEvent) {
                    return LoginActivity.PhoneView.this.m3868lambda$new$6$orgtelegramuiLoginActivity$PhoneView(textView4, i, keyEvent);
                }
            });
            int bottomMargin = 72;
            if (this$0.newAccount && this$0.activityMode == 0) {
                CheckBoxCell checkBoxCell = new CheckBoxCell(context, 2);
                this.syncContactsBox = checkBoxCell;
                checkBoxCell.setText(LocaleController.getString("SyncContacts", R.string.SyncContacts), "", this$0.syncContacts, false);
                addView(this.syncContactsBox, LayoutHelper.createLinear(-2, -1, 51, 16, 0, 16 + ((!LocaleController.isRTL || !AndroidUtilities.isSmallScreen()) ? 0 : Build.VERSION.SDK_INT >= 21 ? 56 : 60), 0));
                bottomMargin = 72 - 24;
                this.syncContactsBox.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda16
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        LoginActivity.PhoneView.this.m3869lambda$new$7$orgtelegramuiLoginActivity$PhoneView(view);
                    }
                });
            }
            if (BuildVars.DEBUG_PRIVATE_VERSION && this$0.activityMode == 0) {
                CheckBoxCell checkBoxCell2 = new CheckBoxCell(context, 2);
                this.testBackendCheckBox = checkBoxCell2;
                checkBoxCell2.setText("Test Backend", "", this$0.testBackend, false);
                addView(this.testBackendCheckBox, LayoutHelper.createLinear(-2, -1, 51, 16, 0, 16 + ((!LocaleController.isRTL || !AndroidUtilities.isSmallScreen()) ? 0 : Build.VERSION.SDK_INT >= 21 ? 56 : 60), 0));
                bottomMargin -= 24;
                this.testBackendCheckBox.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda17
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        LoginActivity.PhoneView.this.m3870lambda$new$8$orgtelegramuiLoginActivity$PhoneView(view);
                    }
                });
            }
            if (bottomMargin > 0 && !AndroidUtilities.isSmallScreen()) {
                Space bottomSpacer = new Space(context);
                bottomSpacer.setMinimumHeight(AndroidUtilities.dp(bottomMargin));
                addView(bottomSpacer, LayoutHelper.createLinear(-2, -2));
            }
            final HashMap<String, String> languageMap = new HashMap<>();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getResources().getAssets().open("countries.txt")));
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    String[] args = line.split(";");
                    CountrySelectActivity.Country countryWithCode = new CountrySelectActivity.Country();
                    countryWithCode.name = args[c];
                    countryWithCode.code = args[0];
                    countryWithCode.shortname = args[1];
                    this.countriesArray.add(0, countryWithCode);
                    this.codesMap.put(args[0], countryWithCode);
                    if (args.length > 3) {
                        this.phoneFormatMap.put(args[0], Collections.singletonList(args[3]));
                    }
                    languageMap.put(args[1], args[2]);
                    c = 2;
                }
                reader.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
            Collections.sort(this.countriesArray, Comparator.CC.comparing(LoginActivity$PhoneView$$ExternalSyntheticLambda8.INSTANCE));
            String country = null;
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                country = null;
            } catch (Exception e2) {
                FileLog.e(e2);
            }
            if (country != null) {
                setCountry(languageMap, country.toUpperCase());
            } else {
                this$0.getAccountInstance().getConnectionsManager().sendRequest(new TLRPC.TL_help_getNearestDc(), new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda13
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LoginActivity.PhoneView.this.m3861lambda$new$11$orgtelegramuiLoginActivity$PhoneView(languageMap, tLObject, tL_error);
                    }
                }, 10);
            }
            if (this.codeField.length() == 0) {
                setCountryButtonText(null);
                this.phoneField.setHintText((String) null);
                this.countryState = 1;
            }
            if (this.codeField.length() != 0) {
                this.phoneField.requestFocus();
                AnimatedPhoneNumberEditText animatedPhoneNumberEditText3 = this.phoneField;
                animatedPhoneNumberEditText3.setSelection(animatedPhoneNumberEditText3.length());
            } else {
                this.codeField.requestFocus();
            }
            TLRPC.TL_help_getCountriesList req = new TLRPC.TL_help_getCountriesList();
            req.lang_code = "";
            this$0.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda9
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LoginActivity.PhoneView.this.m3863lambda$new$13$orgtelegramuiLoginActivity$PhoneView(tLObject, tL_error);
                }
            }, 10);
        }

        public static /* synthetic */ View lambda$new$0(Context context) {
            TextView tv = new TextView(context);
            tv.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(12.0f));
            tv.setTextSize(1, 16.0f);
            tv.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            tv.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            tv.setMaxLines(1);
            tv.setSingleLine(true);
            tv.setEllipsize(TextUtils.TruncateAt.END);
            tv.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            return tv;
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3859lambda$new$1$orgtelegramuiLoginActivity$PhoneView(View v, boolean hasFocus) {
            this.countryOutlineView.animateSelection(hasFocus ? 1.0f : 0.0f);
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3866lambda$new$4$orgtelegramuiLoginActivity$PhoneView(View view) {
            CountrySelectActivity fragment = new CountrySelectActivity(true, this.countriesArray);
            fragment.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda14
                @Override // org.telegram.ui.CountrySelectActivity.CountrySelectActivityDelegate
                public final void didSelectCountry(CountrySelectActivity.Country country) {
                    LoginActivity.PhoneView.this.m3865lambda$new$3$orgtelegramuiLoginActivity$PhoneView(country);
                }
            });
            LoginActivity.this.presentFragment(fragment);
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3865lambda$new$3$orgtelegramuiLoginActivity$PhoneView(CountrySelectActivity.Country country) {
            selectCountry(country);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda22
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.m3864lambda$new$2$orgtelegramuiLoginActivity$PhoneView();
                }
            }, 300L);
            this.phoneField.requestFocus();
            AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
            animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3864lambda$new$2$orgtelegramuiLoginActivity$PhoneView() {
            LoginActivity.this.showKeyboard(this.phoneField);
        }

        /* renamed from: lambda$new$5$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ boolean m3867lambda$new$5$orgtelegramuiLoginActivity$PhoneView(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                this.phoneField.requestFocus();
                AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
                animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
                return true;
            }
            return false;
        }

        /* renamed from: lambda$new$6$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ boolean m3868lambda$new$6$orgtelegramuiLoginActivity$PhoneView(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                if (LoginActivity.this.phoneNumberConfirmView == null) {
                    m3871lambda$onNextPressed$14$orgtelegramuiLoginActivity$PhoneView(null);
                    return true;
                }
                LoginActivity.this.phoneNumberConfirmView.popupFabContainer.callOnClick();
                return true;
            }
            return false;
        }

        /* renamed from: lambda$new$7$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3869lambda$new$7$orgtelegramuiLoginActivity$PhoneView(View v) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            CheckBoxCell cell = (CheckBoxCell) v;
            LoginActivity loginActivity = LoginActivity.this;
            loginActivity.syncContacts = !loginActivity.syncContacts;
            cell.setChecked(LoginActivity.this.syncContacts, true);
            if (LoginActivity.this.syncContacts) {
                BulletinFactory.of(LoginActivity.this.slideViewsContainer, null).createSimpleBulletin(R.raw.contacts_sync_on, LocaleController.getString("SyncContactsOn", R.string.SyncContactsOn)).show();
            } else {
                BulletinFactory.of(LoginActivity.this.slideViewsContainer, null).createSimpleBulletin(R.raw.contacts_sync_off, LocaleController.getString("SyncContactsOff", R.string.SyncContactsOff)).show();
            }
        }

        /* renamed from: lambda$new$8$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3870lambda$new$8$orgtelegramuiLoginActivity$PhoneView(View v) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            CheckBoxCell cell = (CheckBoxCell) v;
            LoginActivity loginActivity = LoginActivity.this;
            loginActivity.testBackend = !loginActivity.testBackend;
            cell.setChecked(LoginActivity.this.testBackend, true);
        }

        /* renamed from: lambda$new$11$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3861lambda$new$11$orgtelegramuiLoginActivity$PhoneView(final HashMap languageMap, final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.m3860lambda$new$10$orgtelegramuiLoginActivity$PhoneView(response, languageMap);
                }
            });
        }

        /* renamed from: lambda$new$10$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3860lambda$new$10$orgtelegramuiLoginActivity$PhoneView(TLObject response, HashMap languageMap) {
            if (response == null) {
                return;
            }
            TLRPC.TL_nearestDc res = (TLRPC.TL_nearestDc) response;
            if (this.codeField.length() == 0) {
                setCountry(languageMap, res.country.toUpperCase());
            }
        }

        /* renamed from: lambda$new$13$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3863lambda$new$13$orgtelegramuiLoginActivity$PhoneView(final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.m3862lambda$new$12$orgtelegramuiLoginActivity$PhoneView(error, response);
                }
            });
        }

        /* renamed from: lambda$new$12$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3862lambda$new$12$orgtelegramuiLoginActivity$PhoneView(TLRPC.TL_error error, TLObject response) {
            if (error == null) {
                this.countriesArray.clear();
                this.codesMap.clear();
                this.phoneFormatMap.clear();
                TLRPC.TL_help_countriesList help_countriesList = (TLRPC.TL_help_countriesList) response;
                for (int i = 0; i < help_countriesList.countries.size(); i++) {
                    TLRPC.TL_help_country c = help_countriesList.countries.get(i);
                    for (int k = 0; k < c.country_codes.size(); k++) {
                        CountrySelectActivity.Country countryWithCode = new CountrySelectActivity.Country();
                        countryWithCode.name = c.default_name;
                        countryWithCode.code = c.country_codes.get(k).country_code;
                        countryWithCode.shortname = c.iso2;
                        this.countriesArray.add(countryWithCode);
                        this.codesMap.put(c.country_codes.get(k).country_code, countryWithCode);
                        if (c.country_codes.get(k).patterns.size() > 0) {
                            this.phoneFormatMap.put(c.country_codes.get(k).country_code, c.country_codes.get(k).patterns);
                        }
                    }
                }
                if (LoginActivity.this.activityMode == 2) {
                    String number = PhoneFormat.stripExceptNumbers(UserConfig.getInstance(LoginActivity.this.currentAccount).getClientPhone());
                    boolean ok = false;
                    if (!TextUtils.isEmpty(number) && number.length() > 4) {
                        int a = 4;
                        while (true) {
                            if (a < 1) {
                                break;
                            }
                            String sub = number.substring(0, a);
                            CountrySelectActivity.Country country2 = this.codesMap.get(sub);
                            if (country2 != null) {
                                ok = true;
                                this.codeField.setText(sub);
                                break;
                            }
                            a--;
                        }
                        if (!ok) {
                            this.codeField.setText(number.substring(0, 1));
                        }
                    }
                }
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.subtitleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            for (int i = 0; i < this.countryButton.getChildCount(); i++) {
                TextView textView = (TextView) this.countryButton.getChildAt(i);
                textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                textView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            }
            this.chevronRight.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.chevronRight.setBackground(Theme.createSelectorDrawable(LoginActivity.this.getThemedColor(Theme.key_listSelector), 1));
            this.plusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated));
            this.codeDividerView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputField));
            this.phoneField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.phoneField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.phoneField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated));
            CheckBoxCell checkBoxCell = this.syncContactsBox;
            if (checkBoxCell != null) {
                checkBoxCell.setSquareCheckBoxColor(Theme.key_checkboxSquareUnchecked, Theme.key_checkboxSquareBackground, Theme.key_checkboxSquareCheck);
                this.syncContactsBox.updateTextColor();
            }
            CheckBoxCell checkBoxCell2 = this.testBackendCheckBox;
            if (checkBoxCell2 != null) {
                checkBoxCell2.setSquareCheckBoxColor(Theme.key_checkboxSquareUnchecked, Theme.key_checkboxSquareBackground, Theme.key_checkboxSquareCheck);
                this.testBackendCheckBox.updateTextColor();
            }
            this.phoneOutlineView.updateColor();
            this.countryOutlineView.updateColor();
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean hasCustomKeyboard() {
            return true;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        }

        public void selectCountry(CountrySelectActivity.Country country) {
            this.ignoreOnTextChange = true;
            String code = country.code;
            this.codeField.setText(code);
            setCountryHint(code, country);
            this.currentCountry = country;
            this.countryState = 0;
            this.ignoreOnTextChange = false;
        }

        public void setCountryHint(String code, CountrySelectActivity.Country country) {
            SpannableStringBuilder sb = new SpannableStringBuilder();
            String flag = LocaleController.getLanguageFlag(country.shortname);
            if (flag != null) {
                sb.append((CharSequence) flag).append((CharSequence) " ");
                sb.setSpan(new ReplacementSpan() { // from class: org.telegram.ui.LoginActivity.PhoneView.5
                    @Override // android.text.style.ReplacementSpan
                    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
                        return AndroidUtilities.dp(16.0f);
                    }

                    @Override // android.text.style.ReplacementSpan
                    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
                    }
                }, flag.length(), flag.length() + 1, 0);
            }
            sb.append((CharSequence) country.name);
            setCountryButtonText(Emoji.replaceEmoji(sb, this.countryButton.getCurrentView().getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
            String str = null;
            if (this.phoneFormatMap.get(code) != null && !this.phoneFormatMap.get(code).isEmpty()) {
                String hint = this.phoneFormatMap.get(code).get(0);
                AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
                if (hint != null) {
                    str = hint.replace('X', '0');
                }
                animatedPhoneNumberEditText.setHintText(str);
                return;
            }
            this.phoneField.setHintText((String) null);
        }

        public void setCountryButtonText(CharSequence cs) {
            Animation anim = AnimationUtils.loadAnimation(ApplicationLoader.applicationContext, (this.countryButton.getCurrentView().getText() == null || cs != null) ? R.anim.text_out : R.anim.text_out_down);
            anim.setInterpolator(Easings.easeInOutQuad);
            this.countryButton.setOutAnimation(anim);
            CharSequence prevText = this.countryButton.getCurrentView().getText();
            this.countryButton.setText(cs, (!TextUtils.isEmpty(cs) || !TextUtils.isEmpty(prevText)) && !ColorUtils$$ExternalSyntheticBackport0.m(prevText, cs));
            this.countryOutlineView.animateSelection(cs != null ? 1.0f : 0.0f);
        }

        private void setCountry(HashMap<String, String> languageMap, String country) {
            String name = languageMap.get(country);
            if (name != null && this.countriesArray != null) {
                CountrySelectActivity.Country countryWithCode = null;
                int i = 0;
                while (true) {
                    if (i < this.countriesArray.size()) {
                        if (this.countriesArray.get(i) == null || !this.countriesArray.get(i).name.equals(country)) {
                            i++;
                        } else {
                            CountrySelectActivity.Country countryWithCode2 = this.countriesArray.get(i);
                            countryWithCode = countryWithCode2;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                if (countryWithCode != null) {
                    this.codeField.setText(countryWithCode.code);
                    this.countryState = 0;
                }
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (this.ignoreSelection) {
                this.ignoreSelection = false;
                return;
            }
            this.ignoreOnTextChange = true;
            CountrySelectActivity.Country countryWithCode = this.countriesArray.get(i);
            this.codeField.setText(countryWithCode.code);
            this.ignoreOnTextChange = false;
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // org.telegram.ui.Components.SlideView
        /* renamed from: onNextPressed */
        public void m3871lambda$onNextPressed$14$orgtelegramuiLoginActivity$PhoneView(final String code) {
            boolean allowCall;
            boolean allowCancelCall;
            TLRPC.TL_auth_sendCode tL_auth_sendCode;
            int resId;
            if (LoginActivity.this.getParentActivity() == null || this.nextPressed) {
                return;
            }
            TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
            if (BuildVars.DEBUG_VERSION) {
                FileLog.d("sim status = " + tm.getSimState());
            }
            if (this.codeField.length() != 0 && this.phoneField.length() != 0) {
                String phoneNumber = "+" + ((Object) this.codeField.getText()) + " " + ((Object) this.phoneField.getText());
                if (!this.confirmedNumber) {
                    if (AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y || LoginActivity.this.isCustomKeyboardVisible() || LoginActivity.this.sizeNotifierFrameLayout.measureKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                        LoginActivity.this.phoneNumberConfirmView = new PhoneNumberConfirmView(LoginActivity.this.fragmentView.getContext(), (ViewGroup) LoginActivity.this.fragmentView, LoginActivity.this.floatingButtonContainer, phoneNumber, new AnonymousClass6(code));
                        LoginActivity.this.phoneNumberConfirmView.show();
                        return;
                    }
                    LoginActivity.this.keyboardHideCallback = new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            LoginActivity.PhoneView.this.m3872lambda$onNextPressed$15$orgtelegramuiLoginActivity$PhoneView(code);
                        }
                    };
                    AndroidUtilities.hideKeyboard(LoginActivity.this.fragmentView);
                    return;
                }
                this.confirmedNumber = false;
                if (LoginActivity.this.phoneNumberConfirmView != null) {
                    LoginActivity.this.phoneNumberConfirmView.dismiss();
                }
                boolean simcardAvailable = AndroidUtilities.isSimAvailable();
                boolean allowReadCallLog = true;
                if (Build.VERSION.SDK_INT >= 23 && simcardAvailable) {
                    allowCall = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    boolean allowCancelCall2 = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.CALL_PHONE") == 0;
                    allowReadCallLog = Build.VERSION.SDK_INT < 28 || LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_CALL_LOG") == 0;
                    boolean allowReadPhoneNumbers = true;
                    if (Build.VERSION.SDK_INT >= 26) {
                        allowReadPhoneNumbers = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_NUMBERS") == 0;
                    }
                    if (LoginActivity.this.checkPermissions) {
                        LoginActivity.this.permissionsItems.clear();
                        if (!allowCall) {
                            LoginActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!allowCancelCall2) {
                            LoginActivity.this.permissionsItems.add("android.permission.CALL_PHONE");
                        }
                        if (!allowReadCallLog) {
                            LoginActivity.this.permissionsItems.add("android.permission.READ_CALL_LOG");
                        }
                        if (!allowReadPhoneNumbers && Build.VERSION.SDK_INT >= 26) {
                            LoginActivity.this.permissionsItems.add("android.permission.READ_PHONE_NUMBERS");
                        }
                        if (!LoginActivity.this.permissionsItems.isEmpty()) {
                            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                            if (!preferences.getBoolean("firstlogin", true) && !LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") && !LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_CALL_LOG")) {
                                try {
                                    LoginActivity.this.getParentActivity().requestPermissions((String[]) LoginActivity.this.permissionsItems.toArray(new String[0]), 6);
                                    return;
                                } catch (Exception e) {
                                    FileLog.e(e);
                                    return;
                                }
                            }
                            preferences.edit().putBoolean("firstlogin", false).commit();
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
                            builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), null);
                            if (!allowCall && (!allowCancelCall2 || !allowReadCallLog)) {
                                builder.setMessage(LocaleController.getString("AllowReadCallAndLog", R.string.AllowReadCallAndLog));
                                resId = R.raw.calls_log;
                            } else if (!allowCancelCall2 || !allowReadCallLog) {
                                builder.setMessage(LocaleController.getString("AllowReadCallLog", R.string.AllowReadCallLog));
                                resId = R.raw.calls_log;
                            } else {
                                builder.setMessage(LocaleController.getString("AllowReadCall", R.string.AllowReadCall));
                                resId = R.raw.incoming_calls;
                            }
                            builder.setTopAnimation(resId, 46, false, Theme.getColor(Theme.key_dialogTopBackground));
                            LoginActivity loginActivity = LoginActivity.this;
                            loginActivity.permissionsDialog = loginActivity.showDialog(builder.create());
                            this.confirmedNumber = true;
                            return;
                        }
                    }
                    allowCancelCall = allowCancelCall2;
                } else {
                    allowCall = true;
                    allowCancelCall = true;
                }
                int i = this.countryState;
                if (i == 1) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("ChooseCountry", R.string.ChooseCountry));
                    LoginActivity.this.needHideProgress(false);
                    return;
                } else if (i == 2 && !BuildVars.DEBUG_VERSION) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("WrongCountry", R.string.WrongCountry));
                    LoginActivity.this.needHideProgress(false);
                    return;
                } else {
                    final String phone = PhoneFormat.stripExceptNumbers("" + ((Object) this.codeField.getText()) + ((Object) this.phoneField.getText()));
                    if (LoginActivity.this.activityMode == 0) {
                        boolean testBackend = BuildVars.DEBUG_PRIVATE_VERSION && LoginActivity.this.getConnectionsManager().isTestBackend();
                        if (testBackend != LoginActivity.this.testBackend) {
                            LoginActivity.this.getConnectionsManager().switchBackend(false);
                            testBackend = LoginActivity.this.testBackend;
                        }
                        if (LoginActivity.this.getParentActivity() instanceof LaunchActivity) {
                            for (int a = 0; a < 4; a++) {
                                UserConfig userConfig = UserConfig.getInstance(a);
                                if (userConfig.isClientActivated()) {
                                    String userPhone = userConfig.getCurrentUser().phone;
                                    if (PhoneNumberUtils.compare(phone, userPhone) && ConnectionsManager.getInstance(a).isTestBackend() == testBackend) {
                                        final int num = a;
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
                                        builder2.setTitle(LocaleController.getString((int) R.string.AppName));
                                        builder2.setMessage(LocaleController.getString("AccountAlreadyLoggedIn", R.string.AccountAlreadyLoggedIn));
                                        builder2.setPositiveButton(LocaleController.getString("AccountSwitch", R.string.AccountSwitch), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda10
                                            @Override // android.content.DialogInterface.OnClickListener
                                            public final void onClick(DialogInterface dialogInterface, int i2) {
                                                LoginActivity.PhoneView.this.m3873lambda$onNextPressed$16$orgtelegramuiLoginActivity$PhoneView(num, dialogInterface, i2);
                                            }
                                        });
                                        builder2.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                                        LoginActivity.this.showDialog(builder2.create());
                                        LoginActivity.this.needHideProgress(false);
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    TLRPC.TL_codeSettings settings = new TLRPC.TL_codeSettings();
                    settings.allow_flashcall = simcardAvailable && allowCall && allowCancelCall && allowReadCallLog;
                    settings.allow_missed_call = simcardAvailable && allowCall;
                    settings.allow_app_hash = ApplicationLoader.hasPlayServices;
                    ArrayList<TLRPC.TL_auth_loggedOut> tokens = MessagesController.getSavedLogOutTokens();
                    if (tokens != null) {
                        int i2 = 0;
                        while (i2 < tokens.size()) {
                            if (settings.logout_tokens == null) {
                                settings.logout_tokens = new ArrayList<>();
                            }
                            settings.logout_tokens.add(tokens.get(i2).future_auth_token);
                            i2++;
                            allowCancelCall = allowCancelCall;
                        }
                        MessagesController.saveLogOutTokens(tokens);
                    }
                    if (settings.logout_tokens != null) {
                        settings.flags |= 64;
                    }
                    SharedPreferences preferences2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    if (settings.allow_app_hash) {
                        preferences2.edit().putString("sms_hash", BuildVars.SMS_HASH).apply();
                    } else {
                        preferences2.edit().remove("sms_hash").apply();
                    }
                    if (settings.allow_flashcall) {
                        try {
                            String number = tm.getLine1Number();
                            if (!TextUtils.isEmpty(number)) {
                                settings.current_number = PhoneNumberUtils.compare(phone, number);
                                if (!settings.current_number) {
                                    settings.allow_flashcall = false;
                                }
                            } else if (UserConfig.getActivatedAccountsCount() > 0) {
                                settings.allow_flashcall = false;
                            } else {
                                settings.current_number = false;
                            }
                        } catch (Exception e2) {
                            settings.allow_flashcall = false;
                            FileLog.e(e2);
                        }
                    }
                    if (LoginActivity.this.activityMode != 2) {
                        ConnectionsManager.getInstance(LoginActivity.this.currentAccount).cleanup(false);
                        TLRPC.TL_auth_sendCode sendCode = new TLRPC.TL_auth_sendCode();
                        sendCode.api_hash = BuildVars.APP_HASH;
                        sendCode.api_id = BuildVars.APP_ID;
                        sendCode.phone_number = phone;
                        sendCode.settings = settings;
                        tL_auth_sendCode = sendCode;
                    } else {
                        TLRPC.TL_account_sendChangePhoneCode changePhoneCode = new TLRPC.TL_account_sendChangePhoneCode();
                        changePhoneCode.phone_number = phone;
                        changePhoneCode.settings = settings;
                        tL_auth_sendCode = changePhoneCode;
                    }
                    final Bundle params = new Bundle();
                    params.putString("phone", "+" + ((Object) this.codeField.getText()) + " " + ((Object) this.phoneField.getText()));
                    try {
                        params.putString("ephone", "+" + PhoneFormat.stripExceptNumbers(this.codeField.getText().toString()) + " " + PhoneFormat.stripExceptNumbers(this.phoneField.getText().toString()));
                    } catch (Exception e3) {
                        FileLog.e(e3);
                        params.putString("ephone", "+" + phone);
                    }
                    params.putString("phoneFormated", phone);
                    this.nextPressed = true;
                    final PhoneInputData phoneInputData = new PhoneInputData();
                    phoneInputData.phoneNumber = "+" + ((Object) this.codeField.getText()) + " " + ((Object) this.phoneField.getText());
                    phoneInputData.country = this.currentCountry;
                    phoneInputData.patterns = this.phoneFormatMap.get(this.codeField.getText().toString());
                    final TLRPC.TL_auth_sendCode tL_auth_sendCode2 = tL_auth_sendCode;
                    int reqId = ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(tL_auth_sendCode, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda11
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LoginActivity.PhoneView.this.m3877lambda$onNextPressed$20$orgtelegramuiLoginActivity$PhoneView(params, phone, phoneInputData, tL_auth_sendCode2, tLObject, tL_error);
                        }
                    }, 27);
                    LoginActivity.this.needShowProgress(reqId);
                    return;
                }
            }
            LoginActivity.this.onFieldError(this.phoneOutlineView, false);
        }

        /* renamed from: lambda$onNextPressed$15$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3872lambda$onNextPressed$15$orgtelegramuiLoginActivity$PhoneView(final String code) {
            postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.m3871lambda$onNextPressed$14$orgtelegramuiLoginActivity$PhoneView(code);
                }
            }, 200L);
        }

        /* renamed from: org.telegram.ui.LoginActivity$PhoneView$6 */
        /* loaded from: classes4.dex */
        public class AnonymousClass6 implements PhoneNumberConfirmView.IConfirmDialogCallback {
            final /* synthetic */ String val$code;

            AnonymousClass6(String str) {
                PhoneView.this = this$1;
                this.val$code = str;
            }

            @Override // org.telegram.ui.LoginActivity.PhoneNumberConfirmView.IConfirmDialogCallback
            public void onFabPressed(PhoneNumberConfirmView confirmView, TransformableLoginButtonView fab) {
                onConfirm(confirmView);
            }

            @Override // org.telegram.ui.LoginActivity.PhoneNumberConfirmView.IConfirmDialogCallback
            public void onEditPressed(PhoneNumberConfirmView confirmView, TextView editTextView) {
                confirmView.dismiss();
            }

            @Override // org.telegram.ui.LoginActivity.PhoneNumberConfirmView.IConfirmDialogCallback
            public void onConfirmPressed(PhoneNumberConfirmView confirmView, TextView confirmTextView) {
                onConfirm(confirmView);
            }

            @Override // org.telegram.ui.LoginActivity.PhoneNumberConfirmView.IConfirmDialogCallback
            public void onDismiss(PhoneNumberConfirmView confirmView) {
                LoginActivity.this.phoneNumberConfirmView = null;
            }

            private void onConfirm(final PhoneNumberConfirmView confirmView) {
                int resId;
                PhoneView.this.confirmedNumber = true;
                LoginActivity.this.currentDoneType = 0;
                LoginActivity.this.needShowProgress(0, false);
                if (Build.VERSION.SDK_INT >= 23 && AndroidUtilities.isSimAvailable()) {
                    boolean allowCall = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                    boolean allowCancelCall = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.CALL_PHONE") == 0;
                    boolean allowReadCallLog = Build.VERSION.SDK_INT < 28 || LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_CALL_LOG") == 0;
                    boolean allowReadPhoneNumbers = Build.VERSION.SDK_INT < 26 || LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_NUMBERS") == 0;
                    if (LoginActivity.this.checkPermissions) {
                        LoginActivity.this.permissionsItems.clear();
                        if (!allowCall) {
                            LoginActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
                        }
                        if (!allowCancelCall) {
                            LoginActivity.this.permissionsItems.add("android.permission.CALL_PHONE");
                        }
                        if (!allowReadCallLog) {
                            LoginActivity.this.permissionsItems.add("android.permission.READ_CALL_LOG");
                        }
                        if (!allowReadPhoneNumbers && Build.VERSION.SDK_INT >= 26) {
                            LoginActivity.this.permissionsItems.add("android.permission.READ_PHONE_NUMBERS");
                        }
                        if (!LoginActivity.this.permissionsItems.isEmpty()) {
                            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                            if (!preferences.getBoolean("firstlogin", true) && !LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE") && !LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_CALL_LOG")) {
                                try {
                                    LoginActivity.this.getParentActivity().requestPermissions((String[]) LoginActivity.this.permissionsItems.toArray(new String[0]), 6);
                                    return;
                                } catch (Exception e) {
                                    FileLog.e(e);
                                    return;
                                }
                            }
                            preferences.edit().putBoolean("firstlogin", false).commit();
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
                            builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), null);
                            if (!allowCall && (!allowCancelCall || !allowReadCallLog)) {
                                builder.setMessage(LocaleController.getString("AllowReadCallAndLog", R.string.AllowReadCallAndLog));
                                resId = R.raw.calls_log;
                            } else if (!allowCancelCall || !allowReadCallLog) {
                                builder.setMessage(LocaleController.getString("AllowReadCallLog", R.string.AllowReadCallLog));
                                resId = R.raw.calls_log;
                            } else {
                                builder.setMessage(LocaleController.getString("AllowReadCall", R.string.AllowReadCall));
                                resId = R.raw.incoming_calls;
                            }
                            builder.setTopAnimation(resId, 46, false, Theme.getColor(Theme.key_dialogTopBackground));
                            LoginActivity.this.permissionsDialog = LoginActivity.this.showDialog(builder.create());
                            PhoneView.this.confirmedNumber = true;
                            return;
                        }
                    }
                }
                final String str = this.val$code;
                confirmView.animateProgress(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$6$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.PhoneView.AnonymousClass6.this.m3880lambda$onConfirm$1$orgtelegramuiLoginActivity$PhoneView$6(confirmView, str);
                    }
                });
            }

            /* renamed from: lambda$onConfirm$1$org-telegram-ui-LoginActivity$PhoneView$6 */
            public /* synthetic */ void m3880lambda$onConfirm$1$orgtelegramuiLoginActivity$PhoneView$6(final PhoneNumberConfirmView confirmView, final String code) {
                confirmView.dismiss();
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$6$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.PhoneView.AnonymousClass6.this.m3879lambda$onConfirm$0$orgtelegramuiLoginActivity$PhoneView$6(code, confirmView);
                    }
                }, 150L);
            }

            /* renamed from: lambda$onConfirm$0$org-telegram-ui-LoginActivity$PhoneView$6 */
            public /* synthetic */ void m3879lambda$onConfirm$0$orgtelegramuiLoginActivity$PhoneView$6(String code, PhoneNumberConfirmView confirmView) {
                PhoneView.this.m3871lambda$onNextPressed$14$orgtelegramuiLoginActivity$PhoneView(code);
                LoginActivity.this.floatingProgressView.sync(confirmView.floatingProgressView);
            }
        }

        /* renamed from: lambda$onNextPressed$16$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3873lambda$onNextPressed$16$orgtelegramuiLoginActivity$PhoneView(int num, DialogInterface dialog, int which) {
            if (UserConfig.selectedAccount != num) {
                ((LaunchActivity) LoginActivity.this.getParentActivity()).switchToAccount(num, false);
            }
            LoginActivity.this.finishFragment();
        }

        /* renamed from: lambda$onNextPressed$20$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3877lambda$onNextPressed$20$orgtelegramuiLoginActivity$PhoneView(final Bundle params, final String phone, final PhoneInputData phoneInputData, final TLObject req, final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.m3876lambda$onNextPressed$19$orgtelegramuiLoginActivity$PhoneView(error, params, response, phone, phoneInputData, req);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$19$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3876lambda$onNextPressed$19$orgtelegramuiLoginActivity$PhoneView(TLRPC.TL_error error, Bundle params, TLObject response, final String phone, PhoneInputData phoneInputData, TLObject req) {
            this.nextPressed = false;
            if (error == null) {
                LoginActivity.this.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else if (error.text != null) {
                if (error.text.contains("SESSION_PASSWORD_NEEDED")) {
                    TLRPC.TL_account_getPassword req2 = new TLRPC.TL_account_getPassword();
                    ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda12
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LoginActivity.PhoneView.this.m3875lambda$onNextPressed$18$orgtelegramuiLoginActivity$PhoneView(phone, tLObject, tL_error);
                        }
                    }, 10);
                } else if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    LoginActivity.needShowInvalidAlert(LoginActivity.this, phone, phoneInputData, false);
                } else if (error.text.contains("PHONE_PASSWORD_FLOOD")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("FloodWait", R.string.FloodWait));
                } else if (error.text.contains("PHONE_NUMBER_FLOOD")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("PhoneNumberFlood", R.string.PhoneNumberFlood));
                } else if (error.text.contains("PHONE_NUMBER_BANNED")) {
                    LoginActivity.needShowInvalidAlert(LoginActivity.this, phone, phoneInputData, true);
                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("FloodWait", R.string.FloodWait));
                } else if (error.code != -1000) {
                    AlertsCreator.processError(LoginActivity.this.currentAccount, error, LoginActivity.this, req, phoneInputData.phoneNumber);
                }
            }
            LoginActivity.this.needHideProgress(false);
        }

        /* renamed from: lambda$onNextPressed$18$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3875lambda$onNextPressed$18$orgtelegramuiLoginActivity$PhoneView(final String phone, final TLObject response1, final TLRPC.TL_error error1) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.m3874lambda$onNextPressed$17$orgtelegramuiLoginActivity$PhoneView(error1, response1, phone);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$17$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3874lambda$onNextPressed$17$orgtelegramuiLoginActivity$PhoneView(TLRPC.TL_error error1, TLObject response1, String phone) {
            this.nextPressed = false;
            LoginActivity.this.showDoneButton(false, true);
            if (error1 != null) {
                LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), error1.text);
                return;
            }
            TLRPC.TL_account_password password = (TLRPC.TL_account_password) response1;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(password, true)) {
                AlertsCreator.showUpdateAppAlert(LoginActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            Bundle bundle = new Bundle();
            SerializedData data = new SerializedData(password.getObjectSize());
            password.serializeToStream(data);
            bundle.putString("password", Utilities.bytesToHex(data.toByteArray()));
            bundle.putString("phoneFormated", phone);
            LoginActivity.this.setPage(6, true, bundle, false);
        }

        public void fillNumber() {
            if (this.numberFilled || LoginActivity.this.activityMode != 0) {
                return;
            }
            try {
                TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (AndroidUtilities.isSimAvailable()) {
                    boolean allowCall = true;
                    boolean allowReadPhoneNumbers = true;
                    if (Build.VERSION.SDK_INT >= 23) {
                        allowCall = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0;
                        if (Build.VERSION.SDK_INT >= 26) {
                            allowReadPhoneNumbers = LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_NUMBERS") == 0;
                        }
                        if (LoginActivity.this.checkShowPermissions && (!allowCall || !allowReadPhoneNumbers)) {
                            LoginActivity.this.permissionsShowItems.clear();
                            if (!allowCall) {
                                LoginActivity.this.permissionsShowItems.add("android.permission.READ_PHONE_STATE");
                            }
                            if (!allowReadPhoneNumbers && Build.VERSION.SDK_INT >= 26) {
                                LoginActivity.this.permissionsShowItems.add("android.permission.READ_PHONE_NUMBERS");
                            }
                            if (!LoginActivity.this.permissionsShowItems.isEmpty()) {
                                final List<String> callbackPermissionItems = new ArrayList<>(LoginActivity.this.permissionsShowItems);
                                Runnable r = new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda3
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        LoginActivity.PhoneView.this.m3858lambda$fillNumber$21$orgtelegramuiLoginActivity$PhoneView(callbackPermissionItems);
                                    }
                                };
                                if (LoginActivity.this.isAnimatingIntro) {
                                    LoginActivity.this.animationFinishCallback = r;
                                    return;
                                } else {
                                    r.run();
                                    return;
                                }
                            }
                            return;
                        }
                    }
                    this.numberFilled = true;
                    if (!LoginActivity.this.newAccount && allowCall && allowReadPhoneNumbers) {
                        this.codeField.setAlpha(0.0f);
                        this.phoneField.setAlpha(0.0f);
                        String number = PhoneFormat.stripExceptNumbers(tm.getLine1Number());
                        String textToSet = null;
                        boolean ok = false;
                        if (!TextUtils.isEmpty(number)) {
                            if (number.length() > 4) {
                                int a = 4;
                                while (true) {
                                    if (a < 1) {
                                        break;
                                    }
                                    String sub = number.substring(0, a);
                                    CountrySelectActivity.Country country = this.codesMap.get(sub);
                                    if (country == null) {
                                        a--;
                                    } else {
                                        ok = true;
                                        textToSet = number.substring(a);
                                        this.codeField.setText(sub);
                                        break;
                                    }
                                }
                                if (!ok) {
                                    textToSet = number.substring(1);
                                    this.codeField.setText(number.substring(0, 1));
                                }
                            }
                            if (textToSet != null) {
                                this.phoneField.requestFocus();
                                this.phoneField.setText(textToSet);
                                AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
                                animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
                            }
                        }
                        if (this.phoneField.length() > 0) {
                            AnimatorSet set = new AnimatorSet().setDuration(300L);
                            set.playTogether(ObjectAnimator.ofFloat(this.codeField, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.phoneField, View.ALPHA, 1.0f));
                            set.start();
                            this.confirmedNumber = true;
                            return;
                        }
                        this.codeField.setAlpha(1.0f);
                        this.phoneField.setAlpha(1.0f);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        /* renamed from: lambda$fillNumber$21$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3858lambda$fillNumber$21$orgtelegramuiLoginActivity$PhoneView(List callbackPermissionItems) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            if (preferences.getBoolean("firstloginshow", true) || LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) {
                preferences.edit().putBoolean("firstloginshow", false).commit();
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
                builder.setTopAnimation(R.raw.incoming_calls, 46, false, Theme.getColor(Theme.key_dialogTopBackground));
                builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), null);
                builder.setMessage(LocaleController.getString("AllowFillNumber", R.string.AllowFillNumber));
                LoginActivity loginActivity = LoginActivity.this;
                loginActivity.permissionsShowDialog = loginActivity.showDialog(builder.create(), true, null);
                LoginActivity.this.needRequestPermissions = true;
                return;
            }
            LoginActivity.this.getParentActivity().requestPermissions((String[]) callbackPermissionItems.toArray(new String[0]), 7);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            fillNumber();
            CheckBoxCell checkBoxCell = this.syncContactsBox;
            if (checkBoxCell != null) {
                checkBoxCell.setChecked(LoginActivity.this.syncContacts, false);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.PhoneView.this.m3878lambda$onShow$22$orgtelegramuiLoginActivity$PhoneView();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* renamed from: lambda$onShow$22$org-telegram-ui-LoginActivity$PhoneView */
        public /* synthetic */ void m3878lambda$onShow$22$orgtelegramuiLoginActivity$PhoneView() {
            if (this.phoneField != null) {
                if (LoginActivity.this.needRequestPermissions) {
                    this.codeField.clearFocus();
                    this.phoneField.clearFocus();
                } else if (this.codeField.length() != 0) {
                    this.phoneField.requestFocus();
                    if (!this.numberFilled) {
                        AnimatedPhoneNumberEditText animatedPhoneNumberEditText = this.phoneField;
                        animatedPhoneNumberEditText.setSelection(animatedPhoneNumberEditText.length());
                    }
                    LoginActivity.this.showKeyboard(this.phoneField);
                } else {
                    this.codeField.requestFocus();
                    LoginActivity.this.showKeyboard(this.codeField);
                }
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("YourPhone", R.string.YourPhone);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            String code = this.codeField.getText().toString();
            if (code.length() != 0) {
                bundle.putString("phoneview_code", code);
            }
            String phone = this.phoneField.getText().toString();
            if (phone.length() != 0) {
                bundle.putString("phoneview_phone", phone);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            String code = bundle.getString("phoneview_code");
            if (code != null) {
                this.codeField.setText(code);
            }
            String phone = bundle.getString("phoneview_phone");
            if (phone != null) {
                this.phoneField.setText(phone);
            }
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.emojiLoaded) {
                this.countryButton.getCurrentView().invalidate();
            }
        }
    }

    /* loaded from: classes4.dex */
    public class LoginActivitySmsView extends SlideView implements NotificationCenter.NotificationCenterDelegate {
        private ImageView blackImageView;
        private RLottieImageView blueImageView;
        private FrameLayout bottomContainer;
        private String catchedPhone;
        private CodeFieldContainer codeFieldContainer;
        private Timer codeTimer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private int currentType;
        private RLottieDrawable dotsDrawable;
        private RLottieDrawable dotsToStarsDrawable;
        private String emailPhone;
        private ViewSwitcher errorViewSwitcher;
        RLottieDrawable hintDrawable;
        private boolean ignoreOnTextChange;
        private boolean isDotsAnimationVisible;
        private double lastCodeTime;
        private double lastCurrentTime;
        private int length;
        private ImageView missedCallArrowIcon;
        private TextView missedCallDescriptionSubtitle;
        private ImageView missedCallPhoneIcon;
        private boolean nextPressed;
        private int nextType;
        private int openTime;
        private String phone;
        private String phoneHash;
        private boolean postedErrorColorTimeout;
        private TextView prefixTextView;
        private FrameLayout problemFrame;
        private TextView problemText;
        private ProgressView progressView;
        private String requestPhone;
        private RLottieDrawable starsToDotsDrawable;
        private TextView timeText;
        private Timer timeTimer;
        private TextView titleTextView;
        private boolean waitingForEvent;
        private TextView wrongCode;
        private final Object timerSync = new Object();
        private int time = 60000;
        private int codeTime = 15000;
        private String lastError = "";
        private String pattern = "*";
        private String prefix = "";
        private Runnable errorColorTimeout = new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                LoginActivity.LoginActivitySmsView.this.m3811lambda$new$0$orgtelegramuiLoginActivity$LoginActivitySmsView();
            }
        };

        static /* synthetic */ int access$8126(LoginActivitySmsView x0, double x1) {
            double d = x0.codeTime;
            Double.isNaN(d);
            int i = (int) (d - x1);
            x0.codeTime = i;
            return i;
        }

        static /* synthetic */ int access$8726(LoginActivitySmsView x0, double x1) {
            double d = x0.time;
            Double.isNaN(d);
            int i = (int) (d - x1);
            x0.time = i;
            return i;
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3811lambda$new$0$orgtelegramuiLoginActivity$LoginActivitySmsView() {
            this.postedErrorColorTimeout = false;
            for (int i = 0; i < this.codeFieldContainer.codeField.length; i++) {
                this.codeFieldContainer.codeField[i].animateErrorProgress(0.0f);
            }
            if (this.errorViewSwitcher.getCurrentView() != this.problemFrame) {
                this.errorViewSwitcher.showNext();
            }
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LoginActivitySmsView(final Context context, int type) {
            super(context);
            String overrideTitle;
            String str;
            LoginActivity.this = this$0;
            this.currentType = type;
            setOrientation(1);
            TextView textView = new TextView(context);
            this.confirmTextView = textView;
            textView.setTextSize(1, 14.0f);
            this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            TextView textView2 = new TextView(context);
            this.titleTextView = textView2;
            textView2.setTextSize(1, 18.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.titleTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.titleTextView.setGravity(49);
            switch (this$0.activityMode) {
                case 1:
                    overrideTitle = LocaleController.getString((int) R.string.CancelAccountReset);
                    break;
                default:
                    overrideTitle = null;
                    break;
            }
            FrameLayout centerContainer = null;
            int i = this.currentType;
            if (i == 11) {
                this.titleTextView.setText(overrideTitle != null ? overrideTitle : LocaleController.getString("MissedCallDescriptionTitle", R.string.MissedCallDescriptionTitle));
                FrameLayout frameLayout = new FrameLayout(context);
                this.missedCallArrowIcon = new ImageView(context);
                this.missedCallPhoneIcon = new ImageView(context);
                frameLayout.addView(this.missedCallArrowIcon);
                frameLayout.addView(this.missedCallPhoneIcon);
                this.missedCallArrowIcon.setImageResource(R.drawable.login_arrow1);
                this.missedCallPhoneIcon.setImageResource(R.drawable.login_phone1);
                addView(frameLayout, LayoutHelper.createLinear(64, 64, 1, 0, 16, 0, 0));
                addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 8, 0, 0));
                TextView textView3 = new TextView(context);
                this.missedCallDescriptionSubtitle = textView3;
                textView3.setTextSize(1, 14.0f);
                this.missedCallDescriptionSubtitle.setGravity(1);
                this.missedCallDescriptionSubtitle.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
                this.missedCallDescriptionSubtitle.setText(AndroidUtilities.replaceTags(LocaleController.getString("MissedCallDescriptionSubtitle", R.string.MissedCallDescriptionSubtitle)));
                addView(this.missedCallDescriptionSubtitle, LayoutHelper.createLinear(-1, -2, 49, 36, 16, 36, 0));
                this.codeFieldContainer = new CodeFieldContainer(context) { // from class: org.telegram.ui.LoginActivity.LoginActivitySmsView.1
                    @Override // org.telegram.ui.CodeFieldContainer
                    protected void processNextPressed() {
                        LoginActivitySmsView.this.onNextPressed(null);
                    }
                };
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(0);
                TextView textView4 = new TextView(context);
                this.prefixTextView = textView4;
                textView4.setTextSize(1, 20.0f);
                this.prefixTextView.setMaxLines(1);
                this.prefixTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.prefixTextView.setPadding(0, 0, 0, 0);
                this.prefixTextView.setGravity(16);
                linearLayout.addView(this.prefixTextView, LayoutHelper.createLinear(-2, -1, 16, 0, 0, 4, 0));
                linearLayout.addView(this.codeFieldContainer, LayoutHelper.createLinear(-2, -1));
                addView(linearLayout, LayoutHelper.createLinear(-2, 34, 1, 0, 28, 0, 0));
                TextView textView5 = new TextView(context);
                this.missedCallDescriptionSubtitle = textView5;
                textView5.setTextSize(1, 14.0f);
                this.missedCallDescriptionSubtitle.setGravity(1);
                this.missedCallDescriptionSubtitle.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
                this.missedCallDescriptionSubtitle.setText(AndroidUtilities.replaceTags(LocaleController.getString("MissedCallDescriptionSubtitle2", R.string.MissedCallDescriptionSubtitle2)));
                addView(this.missedCallDescriptionSubtitle, LayoutHelper.createLinear(-1, -2, 49, 36, 28, 36, 12));
            } else {
                int size = 64;
                if (i == 3) {
                    this.confirmTextView.setGravity(1);
                    centerContainer = new FrameLayout(context);
                    addView(centerContainer, LayoutHelper.createLinear(-1, 0, 1.0f));
                    LinearLayout innerLinearLayout = new LinearLayout(context);
                    innerLinearLayout.setOrientation(1);
                    innerLinearLayout.setGravity(1);
                    centerContainer.addView(innerLinearLayout, LayoutHelper.createFrame(-1, -2, 17));
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) innerLinearLayout.getLayoutParams();
                    layoutParams.bottomMargin = AndroidUtilities.isTablet() ? 0 : AndroidUtilities.statusBarHeight;
                    FrameLayout frameLayout2 = new FrameLayout(context);
                    innerLinearLayout.addView(frameLayout2, LayoutHelper.createFrame(-2, -2, 1));
                    this.blueImageView = new RLottieImageView(context);
                    RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.phone_flash_call, String.valueOf((int) R.raw.phone_flash_call), AndroidUtilities.dp(64.0f), AndroidUtilities.dp(64.0f), true, null);
                    this.hintDrawable = rLottieDrawable;
                    this.blueImageView.setAnimation(rLottieDrawable);
                    frameLayout2.addView(this.blueImageView, LayoutHelper.createFrame(64, 64.0f));
                    this.titleTextView.setText(overrideTitle != null ? overrideTitle : LocaleController.getString((int) R.string.YourCode));
                    innerLinearLayout.addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
                    innerLinearLayout.addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 8, 0, 0));
                } else {
                    this.confirmTextView.setGravity(49);
                    FrameLayout frameLayout3 = new FrameLayout(context);
                    addView(frameLayout3, LayoutHelper.createLinear(-2, -2, 49, 0, 16, 0, 0));
                    int i2 = this.currentType;
                    size = i2 == 1 ? 128 : size;
                    if (i2 == 1) {
                        this.hintDrawable = new RLottieDrawable(R.raw.code_laptop, String.valueOf((int) R.raw.code_laptop), AndroidUtilities.dp(size), AndroidUtilities.dp(size), true, null);
                    } else {
                        this.hintDrawable = new RLottieDrawable(R.raw.sms_incoming_info, String.valueOf((int) R.raw.sms_incoming_info), AndroidUtilities.dp(size), AndroidUtilities.dp(size), true, null);
                        this.starsToDotsDrawable = new RLottieDrawable(R.raw.phone_stars_to_dots, String.valueOf((int) R.raw.phone_stars_to_dots), AndroidUtilities.dp(size), AndroidUtilities.dp(size), true, null);
                        this.dotsDrawable = new RLottieDrawable(R.raw.phone_dots, String.valueOf((int) R.raw.phone_dots), AndroidUtilities.dp(size), AndroidUtilities.dp(size), true, null);
                        this.dotsToStarsDrawable = new RLottieDrawable(R.raw.phone_dots_to_stars, String.valueOf((int) R.raw.phone_dots_to_stars), AndroidUtilities.dp(size), AndroidUtilities.dp(size), true, null);
                    }
                    RLottieImageView rLottieImageView = new RLottieImageView(context);
                    this.blueImageView = rLottieImageView;
                    rLottieImageView.setAnimation(this.hintDrawable);
                    if (this.currentType == 1 && !AndroidUtilities.isSmallScreen()) {
                        this.blueImageView.setTranslationY(-AndroidUtilities.dp(24.0f));
                    }
                    frameLayout3.addView(this.blueImageView, LayoutHelper.createFrame(size, size, 51, 0.0f, 0.0f, 0.0f, (this.currentType != 1 || AndroidUtilities.isSmallScreen()) ? 0.0f : -AndroidUtilities.dp(16.0f)));
                    TextView textView6 = this.titleTextView;
                    if (overrideTitle != null) {
                        str = overrideTitle;
                    } else {
                        str = LocaleController.getString(this.currentType == 1 ? R.string.SentAppCodeTitle : R.string.SentSmsCodeTitle);
                    }
                    textView6.setText(str);
                    addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 18, 0, 0));
                    addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 17, 0, 0));
                }
            }
            int size2 = this.currentType;
            if (size2 != 11) {
                CodeFieldContainer codeFieldContainer = new CodeFieldContainer(context) { // from class: org.telegram.ui.LoginActivity.LoginActivitySmsView.2
                    @Override // org.telegram.ui.CodeFieldContainer
                    protected void processNextPressed() {
                        LoginActivitySmsView.this.onNextPressed(null);
                    }
                };
                this.codeFieldContainer = codeFieldContainer;
                addView(codeFieldContainer, LayoutHelper.createLinear(-2, 42, 1, 0, 32, 0, 0));
            }
            if (this.currentType == 3) {
                this.codeFieldContainer.setVisibility(8);
            }
            this.problemFrame = new FrameLayout(context);
            TextView textView7 = new TextView(context);
            this.timeText = textView7;
            textView7.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.timeText.setPadding(0, AndroidUtilities.dp(2.0f), 0, AndroidUtilities.dp(10.0f));
            this.timeText.setTextSize(1, 15.0f);
            this.timeText.setGravity(49);
            this.timeText.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda36
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LoginActivity.LoginActivitySmsView.this.m3815lambda$new$4$orgtelegramuiLoginActivity$LoginActivitySmsView(view);
                }
            });
            this.problemFrame.addView(this.timeText, LayoutHelper.createFrame(-2, -2, 49));
            this.errorViewSwitcher = new ViewSwitcher(context) { // from class: org.telegram.ui.LoginActivity.LoginActivitySmsView.3
                @Override // android.widget.FrameLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), Integer.MIN_VALUE));
                }
            };
            Animation anim = AnimationUtils.loadAnimation(context, R.anim.text_in);
            anim.setInterpolator(Easings.easeInOutQuad);
            this.errorViewSwitcher.setInAnimation(anim);
            Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.text_out);
            anim2.setInterpolator(Easings.easeInOutQuad);
            this.errorViewSwitcher.setOutAnimation(anim2);
            TextView textView8 = new TextView(context);
            this.problemText = textView8;
            textView8.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.problemText.setTextSize(1, 15.0f);
            this.problemText.setGravity(49);
            this.problemText.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
            this.problemFrame.addView(this.problemText, LayoutHelper.createFrame(-2, -2, 17));
            this.errorViewSwitcher.addView(this.problemFrame);
            TextView textView9 = new TextView(context);
            this.wrongCode = textView9;
            textView9.setText(LocaleController.getString("WrongCode", R.string.WrongCode));
            this.wrongCode.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.wrongCode.setTextSize(1, 15.0f);
            this.wrongCode.setGravity(49);
            this.wrongCode.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
            this.errorViewSwitcher.addView(this.wrongCode);
            if (this.currentType == 1) {
                int i3 = this.nextType;
                if (i3 == 3 || i3 == 4 || i3 == 11) {
                    this.problemText.setText(LocaleController.getString("DidNotGetTheCodePhone", R.string.DidNotGetTheCodePhone));
                } else {
                    this.problemText.setText(LocaleController.getString("DidNotGetTheCodeSms", R.string.DidNotGetTheCodeSms));
                }
            } else {
                this.problemText.setText(LocaleController.getString("DidNotGetTheCode", R.string.DidNotGetTheCode));
            }
            if (centerContainer == null) {
                FrameLayout frameLayout4 = new FrameLayout(context);
                this.bottomContainer = frameLayout4;
                frameLayout4.addView(this.errorViewSwitcher, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 32.0f));
                addView(this.bottomContainer, LayoutHelper.createLinear(-1, 0, 1.0f));
            } else {
                centerContainer.addView(this.errorViewSwitcher, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 32.0f));
            }
            VerticalPositionAutoAnimator.attach(this.errorViewSwitcher);
            this.problemText.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda37
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LoginActivity.LoginActivitySmsView.this.m3818lambda$new$7$orgtelegramuiLoginActivity$LoginActivitySmsView(context, view);
                }
            });
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3815lambda$new$4$orgtelegramuiLoginActivity$LoginActivitySmsView(View v) {
            int i = this.nextType;
            if (i == 4 || i == 2 || i == 11) {
                this.timeText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
                int i2 = this.nextType;
                if (i2 == 4 || i2 == 11) {
                    this.timeText.setText(LocaleController.getString("Calling", R.string.Calling));
                } else {
                    this.timeText.setText(LocaleController.getString("SendingSms", R.string.SendingSms));
                }
                final Bundle params = new Bundle();
                params.putString("phone", this.phone);
                params.putString("ephone", this.emailPhone);
                params.putString("phoneFormated", this.requestPhone);
                createCodeTimer();
                TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
                req.phone_number = this.requestPhone;
                req.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda28
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LoginActivity.LoginActivitySmsView.this.m3814lambda$new$3$orgtelegramuiLoginActivity$LoginActivitySmsView(params, tLObject, tL_error);
                    }
                }, 10);
            } else if (i == 3) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                this.waitingForEvent = false;
                destroyCodeTimer();
                resendCode();
            }
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3814lambda$new$3$orgtelegramuiLoginActivity$LoginActivitySmsView(final Bundle params, final TLObject response, final TLRPC.TL_error error) {
            if (response != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda17
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.this.m3812lambda$new$1$orgtelegramuiLoginActivity$LoginActivitySmsView(params, response);
                    }
                });
            } else if (error != null && error.text != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda20
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.this.m3813lambda$new$2$orgtelegramuiLoginActivity$LoginActivitySmsView(error);
                    }
                });
            }
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3812lambda$new$1$orgtelegramuiLoginActivity$LoginActivitySmsView(Bundle params, TLObject response) {
            LoginActivity.this.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3813lambda$new$2$orgtelegramuiLoginActivity$LoginActivitySmsView(TLRPC.TL_error error) {
            this.lastError = error.text;
        }

        /* renamed from: lambda$new$7$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3818lambda$new$7$orgtelegramuiLoginActivity$LoginActivitySmsView(Context context, View v) {
            if (this.nextPressed) {
                return;
            }
            boolean email = this.nextType == 0;
            if (!email) {
                if (LoginActivity.this.radialProgressView.getTag() != null) {
                    return;
                }
                resendCode();
                return;
            }
            new AlertDialog.Builder(context).setTitle(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("DidNotGetTheCodeInfo", R.string.DidNotGetTheCodeInfo, this.phone))).setNeutralButton(LocaleController.getString((int) R.string.DidNotGetTheCodeHelpButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivitySmsView.this.m3816lambda$new$5$orgtelegramuiLoginActivity$LoginActivitySmsView(dialogInterface, i);
                }
            }).setPositiveButton(LocaleController.getString((int) R.string.Close), null).setNegativeButton(LocaleController.getString((int) R.string.DidNotGetTheCodeEditNumberButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda11
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivitySmsView.this.m3817lambda$new$6$orgtelegramuiLoginActivity$LoginActivitySmsView(dialogInterface, i);
                }
            }).show();
        }

        /* renamed from: lambda$new$5$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3816lambda$new$5$orgtelegramuiLoginActivity$LoginActivitySmsView(DialogInterface dialog, int which) {
            try {
                PackageInfo pInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
                String version = String.format(Locale.US, "%s (%d)", pInfo.versionName, Integer.valueOf(pInfo.versionCode));
                Intent mailer = new Intent("android.intent.action.SENDTO");
                mailer.setData(Uri.parse(MailTo.MAILTO_SCHEME));
                mailer.putExtra("android.intent.extra.EMAIL", new String[]{"sms@telegram.org"});
                mailer.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + version + " " + this.emailPhone);
                mailer.putExtra("android.intent.extra.TEXT", "Phone: " + this.requestPhone + "\nApp version: " + version + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + this.lastError);
                getContext().startActivity(Intent.createChooser(mailer, "Send email..."));
            } catch (Exception e) {
                LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.AppName), LocaleController.getString("NoMailInstalled", R.string.NoMailInstalled));
            }
        }

        /* renamed from: lambda$new$6$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3817lambda$new$6$orgtelegramuiLoginActivity$LoginActivitySmsView(DialogInterface dialog, int which) {
            LoginActivity.this.setPage(0, true, null, true);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.confirmTextView.setTextColor(Theme.getColor(LoginActivity.this.isInCancelAccountDeletionMode() ? Theme.key_windowBackgroundWhiteBlackText : Theme.key_windowBackgroundWhiteGrayText6));
            this.confirmTextView.setLinkTextColor(Theme.getColor(Theme.key_chats_actionBackground));
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            if (this.currentType == 11) {
                this.missedCallDescriptionSubtitle.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                this.missedCallArrowIcon.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated), PorterDuff.Mode.SRC_IN));
                this.missedCallPhoneIcon.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), PorterDuff.Mode.SRC_IN));
                this.prefixTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            }
            applyLottieColors(this.hintDrawable);
            applyLottieColors(this.starsToDotsDrawable);
            applyLottieColors(this.dotsDrawable);
            applyLottieColors(this.dotsToStarsDrawable);
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            if (codeFieldContainer != null) {
                codeFieldContainer.invalidate();
            }
            String timeTextColorTag = (String) this.timeText.getTag();
            if (timeTextColorTag == null) {
                timeTextColorTag = Theme.key_windowBackgroundWhiteGrayText6;
            }
            this.timeText.setTextColor(Theme.getColor(timeTextColorTag));
            this.problemText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            this.wrongCode.setTextColor(Theme.getColor(Theme.key_dialogTextRed));
        }

        private void applyLottieColors(RLottieDrawable drawable) {
            if (drawable != null) {
                drawable.setLayerColor("Bubble.**", Theme.getColor(Theme.key_chats_actionBackground));
                drawable.setLayerColor("Phone.**", Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                drawable.setLayerColor("Note.**", Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean hasCustomKeyboard() {
            return this.currentType != 3;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        private void resendCode() {
            final Bundle params = new Bundle();
            params.putString("phone", this.phone);
            params.putString("ephone", this.emailPhone);
            params.putString("phoneFormated", this.requestPhone);
            this.nextPressed = true;
            TLRPC.TL_auth_resendCode req = new TLRPC.TL_auth_resendCode();
            req.phone_number = this.requestPhone;
            req.phone_code_hash = this.phoneHash;
            int reqId = ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda29
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LoginActivity.LoginActivitySmsView.this.m3837x3aef5a92(params, tLObject, tL_error);
                }
            }, 10);
            tryShowProgress(reqId);
        }

        /* renamed from: lambda$resendCode$9$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3837x3aef5a92(final Bundle params, final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda21
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3836xd16c033(error, params, response);
                }
            });
        }

        /* renamed from: lambda$resendCode$8$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3836xd16c033(TLRPC.TL_error error, Bundle params, TLObject response) {
            this.nextPressed = false;
            if (error == null) {
                LoginActivity.this.fillNextCodeParams(params, (TLRPC.TL_auth_sentCode) response);
            } else if (error.text != null) {
                if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    onBackPressed(true);
                    LoginActivity.this.setPage(0, true, null, true);
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("FloodWait", R.string.FloodWait));
                } else if (error.code != -1000) {
                    LoginActivity loginActivity = LoginActivity.this;
                    String string = LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle);
                    loginActivity.needShowAlert(string, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                }
            }
            tryHideProgress(false);
        }

        @Override // android.view.View
        protected void onConfigurationChanged(Configuration newConfig) {
            CodeNumberField[] codeNumberFieldArr;
            super.onConfigurationChanged(newConfig);
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            if (codeFieldContainer != null && codeFieldContainer.codeField != null) {
                for (CodeNumberField f : this.codeFieldContainer.codeField) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        f.setShowSoftInputOnFocusCompat(!hasCustomKeyboard() || LoginActivity.this.isCustomKeyboardForceDisabled());
                    }
                }
            }
        }

        private void tryShowProgress(int reqId) {
            m3845xa6490091(reqId, true);
        }

        /* renamed from: tryShowProgress */
        public void m3845xa6490091(final int reqId, final boolean animate) {
            if (this.starsToDotsDrawable == null) {
                LoginActivity.this.needShowProgress(reqId, animate);
            } else if (this.isDotsAnimationVisible) {
            } else {
                this.isDotsAnimationVisible = true;
                if (this.hintDrawable.getCurrentFrame() != this.hintDrawable.getFramesCount() - 1) {
                    this.hintDrawable.setOnAnimationEndListener(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda14
                        @Override // java.lang.Runnable
                        public final void run() {
                            LoginActivity.LoginActivitySmsView.this.m3846xd4219af0(reqId, animate);
                        }
                    });
                    return;
                }
                this.starsToDotsDrawable.setOnAnimationEndListener(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda10
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.this.m3848x2fd2cfae();
                    }
                });
                this.blueImageView.setAutoRepeat(false);
                this.starsToDotsDrawable.setCurrentFrame(0, false);
                this.blueImageView.setAnimation(this.starsToDotsDrawable);
                this.blueImageView.playAnimation();
            }
        }

        /* renamed from: lambda$tryShowProgress$11$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3846xd4219af0(final int reqId, final boolean animate) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3845xa6490091(reqId, animate);
                }
            });
        }

        /* renamed from: lambda$tryShowProgress$13$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3848x2fd2cfae() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3847x1fa354f();
                }
            });
        }

        /* renamed from: lambda$tryShowProgress$12$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3847x1fa354f() {
            this.blueImageView.setAutoRepeat(true);
            this.dotsDrawable.setCurrentFrame(0, false);
            this.dotsDrawable.setAutoRepeat(1);
            this.blueImageView.setAnimation(this.dotsDrawable);
            this.blueImageView.playAnimation();
        }

        private void tryHideProgress(boolean cancel) {
            tryHideProgress(cancel, true);
        }

        private void tryHideProgress(boolean cancel, boolean animate) {
            if (this.starsToDotsDrawable == null) {
                LoginActivity.this.needHideProgress(cancel, animate);
            } else if (!this.isDotsAnimationVisible) {
            } else {
                this.isDotsAnimationVisible = false;
                this.blueImageView.setAutoRepeat(false);
                this.dotsDrawable.setAutoRepeat(0);
                this.dotsDrawable.setOnFinishCallback(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.this.m3844x16a760af();
                    }
                }, this.dotsDrawable.getFramesCount() - 1);
            }
        }

        /* renamed from: lambda$tryHideProgress$17$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3844x16a760af() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3843xe8cec650();
                }
            });
        }

        /* renamed from: lambda$tryHideProgress$15$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3842xbaf62bf1() {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3841x8d1d9192();
                }
            });
        }

        /* renamed from: lambda$tryHideProgress$16$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3843xe8cec650() {
            this.dotsToStarsDrawable.setOnAnimationEndListener(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3842xbaf62bf1();
                }
            });
            this.blueImageView.setAutoRepeat(false);
            this.dotsToStarsDrawable.setCurrentFrame(0, false);
            this.blueImageView.setAnimation(this.dotsToStarsDrawable);
            this.blueImageView.playAnimation();
        }

        /* renamed from: lambda$tryHideProgress$14$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3841x8d1d9192() {
            this.blueImageView.setAutoRepeat(false);
            this.blueImageView.setAnimation(this.hintDrawable);
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            int i = this.currentType;
            if (i == 3 || i == 11) {
                return this.phone;
            }
            return LocaleController.getString("YourCode", R.string.YourCode);
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle params, boolean restore) {
            CodeNumberField[] codeNumberFieldArr;
            int i;
            int i2;
            if (params == null) {
                return;
            }
            boolean z = true;
            this.waitingForEvent = true;
            int i3 = this.currentType;
            if (i3 == 2) {
                AndroidUtilities.setWaitingForSms(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i3 == 3) {
                AndroidUtilities.setWaitingForCall(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
            }
            this.currentParams = params;
            this.phone = params.getString("phone");
            this.emailPhone = params.getString("ephone");
            this.requestPhone = params.getString("phoneFormated");
            this.phoneHash = params.getString("phoneHash");
            this.time = params.getInt("timeout");
            this.openTime = (int) (System.currentTimeMillis() / 1000);
            this.nextType = params.getInt("nextType");
            this.pattern = params.getString("pattern");
            this.prefix = params.getString("prefix");
            int i4 = params.getInt("length");
            this.length = i4;
            if (i4 == 0) {
                this.length = 5;
            }
            this.codeFieldContainer.setNumbersCount(this.length, this.currentType);
            for (CodeNumberField f : this.codeFieldContainer.codeField) {
                if (Build.VERSION.SDK_INT >= 21) {
                    f.setShowSoftInputOnFocusCompat(!hasCustomKeyboard() || LoginActivity.this.isCustomKeyboardForceDisabled());
                }
                f.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.LoginActivity.LoginActivitySmsView.4
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (LoginActivitySmsView.this.postedErrorColorTimeout) {
                            LoginActivitySmsView loginActivitySmsView = LoginActivitySmsView.this;
                            loginActivitySmsView.removeCallbacks(loginActivitySmsView.errorColorTimeout);
                            LoginActivitySmsView.this.errorColorTimeout.run();
                        }
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                    }
                });
                f.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda38
                    @Override // android.view.View.OnFocusChangeListener
                    public final void onFocusChange(View view, boolean z2) {
                        LoginActivity.LoginActivitySmsView.this.m3838x7465586c(view, z2);
                    }
                });
            }
            ProgressView progressView = this.progressView;
            if (progressView != null) {
                progressView.setVisibility(this.nextType != 0 ? 0 : 8);
            }
            if (this.phone != null) {
                String number = PhoneFormat.getInstance().format(this.phone);
                CharSequence str = "";
                if (LoginActivity.this.isInCancelAccountDeletionMode()) {
                    SpannableStringBuilder spanned = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("CancelAccountResetInfo2", R.string.CancelAccountResetInfo2, PhoneFormat.getInstance().format("+" + number))));
                    int startIndex = TextUtils.indexOf((CharSequence) spanned, '*');
                    int lastIndex = TextUtils.lastIndexOf(spanned, '*');
                    if (startIndex != -1 && lastIndex != -1 && startIndex != lastIndex) {
                        this.confirmTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
                        spanned.replace(lastIndex, lastIndex + 1, (CharSequence) "");
                        spanned.replace(startIndex, startIndex + 1, (CharSequence) "");
                        spanned.setSpan(new URLSpanNoUnderline("tg://settings/change_number"), startIndex, lastIndex - 1, 33);
                    }
                    str = spanned;
                } else {
                    int i5 = this.currentType;
                    if (i5 == 1) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentAppCodeWithPhone", R.string.SentAppCodeWithPhone, LocaleController.addNbsp(number)));
                    } else if (i5 == 2) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", R.string.SentSmsCode, LocaleController.addNbsp(number)));
                    } else if (i5 == 3) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", R.string.SentCallCode, LocaleController.addNbsp(number)));
                    } else if (i5 == 4) {
                        str = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", R.string.SentCallOnly, LocaleController.addNbsp(number)));
                    }
                }
                this.confirmTextView.setText(str);
                if (this.currentType != 3) {
                    LoginActivity.this.showKeyboard(this.codeFieldContainer.codeField[0]);
                    this.codeFieldContainer.codeField[0].requestFocus();
                } else {
                    AndroidUtilities.hideKeyboard(this.codeFieldContainer.codeField[0]);
                }
                destroyTimer();
                destroyCodeTimer();
                this.lastCurrentTime = System.currentTimeMillis();
                int i6 = this.currentType;
                if (i6 == 1) {
                    setProblemTextVisible(true);
                    this.timeText.setVisibility(8);
                } else if (i6 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) {
                    setProblemTextVisible(false);
                    this.timeText.setVisibility(0);
                    int i7 = this.nextType;
                    if (i7 == 4 || i7 == 11) {
                        this.timeText.setText(LocaleController.formatString("CallAvailableIn", R.string.CallAvailableIn, 1, 0));
                    } else if (i7 == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsAvailableIn", R.string.SmsAvailableIn, 1, 0));
                    }
                    String callLogNumber = restore ? AndroidUtilities.obtainLoginPhoneCall(this.pattern) : null;
                    if (callLogNumber != null) {
                        onNextPressed(callLogNumber);
                    } else {
                        String str2 = this.catchedPhone;
                        if (str2 != null) {
                            onNextPressed(str2);
                        } else {
                            createTimer();
                        }
                    }
                } else if (i6 != 2 || ((i = this.nextType) != 4 && i != 3)) {
                    if (i6 == 4 && this.nextType == 2) {
                        this.timeText.setText(LocaleController.formatString("SmsAvailableIn", R.string.SmsAvailableIn, 2, 0));
                        if (this.time >= 1000) {
                            z = false;
                        }
                        setProblemTextVisible(z);
                        this.timeText.setVisibility(this.time < 1000 ? 8 : 0);
                        createTimer();
                    } else {
                        this.timeText.setVisibility(8);
                        setProblemTextVisible(false);
                        createCodeTimer();
                    }
                } else {
                    this.timeText.setText(LocaleController.formatString("CallAvailableIn", R.string.CallAvailableIn, 2, 0));
                    setProblemTextVisible(this.time < 1000);
                    this.timeText.setVisibility(this.time < 1000 ? 8 : 0);
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                    String hash = preferences.getString("sms_hash", null);
                    String savedCode = null;
                    if (!TextUtils.isEmpty(hash)) {
                        String savedCode2 = preferences.getString("sms_hash_code", null);
                        if (savedCode2 != null) {
                            if (savedCode2.contains(hash + "|")) {
                                savedCode = savedCode2.substring(savedCode2.indexOf(124) + 1);
                            }
                        }
                        savedCode = null;
                    }
                    if (savedCode != null) {
                        this.codeFieldContainer.setCode(savedCode);
                        onNextPressed(null);
                    } else {
                        createTimer();
                    }
                }
                if (this.currentType == 11) {
                    String pref = this.prefix;
                    for (int i8 = 0; i8 < this.length; i8++) {
                        pref = pref + "0";
                    }
                    String pref2 = PhoneFormat.getInstance().format("+" + pref);
                    for (int i9 = 0; i9 < this.length; i9++) {
                        int index = pref2.lastIndexOf("0");
                        if (index >= 0) {
                            pref2 = pref2.substring(0, index);
                        }
                    }
                    this.prefixTextView.setText(pref2.replaceAll("\\)", "").replaceAll("\\(", ""));
                }
            }
        }

        /* renamed from: lambda$setParams$18$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3838x7465586c(View v, boolean hasFocus) {
            if (hasFocus) {
                LoginActivity.this.keyboardView.setEditText((EditText) v);
                LoginActivity.this.keyboardView.setDispatchBackWhenEmpty(true);
            }
        }

        public void setProblemTextVisible(boolean visible) {
            float newAlpha = visible ? 1.0f : 0.0f;
            if (this.problemText.getAlpha() != newAlpha) {
                this.problemText.animate().cancel();
                this.problemText.animate().alpha(newAlpha).setDuration(150L).start();
            }
        }

        private void createCodeTimer() {
            if (this.codeTimer != null) {
                return;
            }
            this.codeTime = 15000;
            this.codeTimer = new Timer();
            this.lastCodeTime = System.currentTimeMillis();
            this.codeTimer.schedule(new AnonymousClass5(), 0L, 1000L);
        }

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$5 */
        /* loaded from: classes4.dex */
        public class AnonymousClass5 extends TimerTask {
            AnonymousClass5() {
                LoginActivitySmsView.this = this$1;
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$5$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.AnonymousClass5.this.m3849xf184b283();
                    }
                });
            }

            /* renamed from: lambda$run$0$org-telegram-ui-LoginActivity$LoginActivitySmsView$5 */
            public /* synthetic */ void m3849xf184b283() {
                double currentTime = System.currentTimeMillis();
                double d = LoginActivitySmsView.this.lastCodeTime;
                Double.isNaN(currentTime);
                double diff = currentTime - d;
                LoginActivitySmsView.this.lastCodeTime = currentTime;
                LoginActivitySmsView.access$8126(LoginActivitySmsView.this, diff);
                if (LoginActivitySmsView.this.codeTime <= 1000) {
                    LoginActivitySmsView.this.setProblemTextVisible(true);
                    LoginActivitySmsView.this.timeText.setVisibility(8);
                    LoginActivitySmsView.this.destroyCodeTimer();
                }
            }
        }

        public void destroyCodeTimer() {
            try {
                synchronized (this.timerSync) {
                    Timer timer = this.codeTimer;
                    if (timer != null) {
                        timer.cancel();
                        this.codeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        private void createTimer() {
            if (this.timeTimer != null) {
                return;
            }
            this.timeText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.timeText.setTag(R.id.color_key_tag, Theme.key_windowBackgroundWhiteGrayText6);
            ProgressView progressView = this.progressView;
            if (progressView != null) {
                progressView.resetProgressAnimation();
            }
            Timer timer = new Timer();
            this.timeTimer = timer;
            timer.schedule(new AnonymousClass6(), 0L, 1000L);
        }

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivitySmsView$6 */
        /* loaded from: classes4.dex */
        public class AnonymousClass6 extends TimerTask {
            AnonymousClass6() {
                LoginActivitySmsView.this = this$1;
            }

            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                if (LoginActivitySmsView.this.timeTimer == null) {
                    return;
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$6$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.AnonymousClass6.this.m3850xf184b284();
                    }
                });
            }

            /* renamed from: lambda$run$0$org-telegram-ui-LoginActivity$LoginActivitySmsView$6 */
            public /* synthetic */ void m3850xf184b284() {
                double currentTime = System.currentTimeMillis();
                double d = LoginActivitySmsView.this.lastCurrentTime;
                Double.isNaN(currentTime);
                double diff = currentTime - d;
                LoginActivitySmsView.this.lastCurrentTime = currentTime;
                LoginActivitySmsView.access$8726(LoginActivitySmsView.this, diff);
                if (LoginActivitySmsView.this.time >= 1000) {
                    int minutes = (LoginActivitySmsView.this.time / 1000) / 60;
                    int seconds = (LoginActivitySmsView.this.time / 1000) - (minutes * 60);
                    if (LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 3 || LoginActivitySmsView.this.nextType == 11) {
                        LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallAvailableIn", R.string.CallAvailableIn, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                    } else if (LoginActivitySmsView.this.nextType == 2) {
                        LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsAvailableIn", R.string.SmsAvailableIn, Integer.valueOf(minutes), Integer.valueOf(seconds)));
                    }
                    if (LoginActivitySmsView.this.progressView != null && !LoginActivitySmsView.this.progressView.isProgressAnimationRunning()) {
                        LoginActivitySmsView.this.progressView.startProgressAnimation(LoginActivitySmsView.this.time - 1000);
                        return;
                    }
                    return;
                }
                LoginActivitySmsView.this.destroyTimer();
                if (LoginActivitySmsView.this.nextType == 3 || LoginActivitySmsView.this.nextType == 4 || LoginActivitySmsView.this.nextType == 2 || LoginActivitySmsView.this.nextType == 11) {
                    if (LoginActivitySmsView.this.nextType == 4) {
                        LoginActivitySmsView.this.timeText.setText(LocaleController.getString("RequestCallButton", R.string.RequestCallButton));
                    } else if (LoginActivitySmsView.this.nextType == 11 || LoginActivitySmsView.this.nextType == 3) {
                        LoginActivitySmsView.this.timeText.setText(LocaleController.getString("RequestMissedCall", R.string.RequestMissedCall));
                    } else {
                        LoginActivitySmsView.this.timeText.setText(LocaleController.getString("RequestSmsButton", R.string.RequestSmsButton));
                    }
                    LoginActivitySmsView.this.timeText.setTextColor(Theme.getColor(Theme.key_chats_actionBackground));
                    LoginActivitySmsView.this.timeText.setTag(R.id.color_key_tag, Theme.key_chats_actionBackground);
                }
            }
        }

        public void destroyTimer() {
            this.timeText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.timeText.setTag(R.id.color_key_tag, Theme.key_windowBackgroundWhiteGrayText6);
            try {
                synchronized (this.timerSync) {
                    Timer timer = this.timeTimer;
                    if (timer != null) {
                        timer.cancel();
                        this.timeTimer = null;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String code) {
            if (LoginActivity.this.currentViewNum == 11) {
                if (this.nextPressed) {
                    return;
                }
            } else if (this.nextPressed || LoginActivity.this.currentViewNum < 1 || LoginActivity.this.currentViewNum > 4) {
                return;
            }
            if (code == null) {
                code = this.codeFieldContainer.getCode();
            }
            int i = 0;
            if (TextUtils.isEmpty(code)) {
                LoginActivity.this.onFieldError(this.codeFieldContainer, false);
            } else if (LoginActivity.this.currentViewNum >= 1 && LoginActivity.this.currentViewNum <= 4 && this.codeFieldContainer.isFocusSuppressed) {
            } else {
                this.nextPressed = true;
                int i2 = this.currentType;
                if (i2 == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i2 == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                switch (LoginActivity.this.activityMode) {
                    case 1:
                        this.requestPhone = LoginActivity.this.cancelDeletionPhone;
                        final TLRPC.TL_account_confirmPhone req = new TLRPC.TL_account_confirmPhone();
                        req.phone_code = code;
                        req.phone_code_hash = this.phoneHash;
                        destroyTimer();
                        this.codeFieldContainer.isFocusSuppressed = true;
                        CodeNumberField[] codeNumberFieldArr = this.codeFieldContainer.codeField;
                        int length = codeNumberFieldArr.length;
                        while (i < length) {
                            CodeNumberField f = codeNumberFieldArr[i];
                            f.animateFocusedProgress(0.0f);
                            i++;
                        }
                        int reqId = ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda30
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                LoginActivity.LoginActivitySmsView.this.m3827xeb54af77(req, tLObject, tL_error);
                            }
                        }, 2);
                        tryShowProgress(reqId);
                        return;
                    case 2:
                        TLRPC.TL_account_changePhone req2 = new TLRPC.TL_account_changePhone();
                        req2.phone_number = this.requestPhone;
                        req2.phone_code = code;
                        req2.phone_code_hash = this.phoneHash;
                        destroyTimer();
                        this.codeFieldContainer.isFocusSuppressed = true;
                        CodeNumberField[] codeNumberFieldArr2 = this.codeFieldContainer.codeField;
                        int length2 = codeNumberFieldArr2.length;
                        while (i < length2) {
                            CodeNumberField f2 = codeNumberFieldArr2[i];
                            f2.animateFocusedProgress(0.0f);
                            i++;
                        }
                        int reqId2 = ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda27
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                LoginActivity.LoginActivitySmsView.this.m3823x33f245fb(tLObject, tL_error);
                            }
                        }, 2);
                        m3845xa6490091(reqId2, true);
                        LoginActivity.this.showDoneButton(true, true);
                        return;
                    default:
                        final TLRPC.TL_auth_signIn req3 = new TLRPC.TL_auth_signIn();
                        req3.phone_number = this.requestPhone;
                        req3.phone_code = code;
                        req3.phone_code_hash = this.phoneHash;
                        destroyTimer();
                        this.codeFieldContainer.isFocusSuppressed = true;
                        CodeNumberField[] codeNumberFieldArr3 = this.codeFieldContainer.codeField;
                        int length3 = codeNumberFieldArr3.length;
                        while (i < length3) {
                            CodeNumberField f3 = codeNumberFieldArr3[i];
                            f3.animateFocusedProgress(0.0f);
                            i++;
                        }
                        int reqId3 = ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req3, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda32
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                LoginActivity.LoginActivitySmsView.this.m3834xef0591db(req3, tLObject, tL_error);
                            }
                        }, 10);
                        m3845xa6490091(reqId3, true);
                        LoginActivity.this.showDoneButton(true, true);
                        return;
                }
            }
        }

        /* renamed from: lambda$onNextPressed$22$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3823x33f245fb(final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda23
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3822x619ab9c(error, response);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$21$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3822x619ab9c(TLRPC.TL_error error, TLObject response) {
            int i;
            int i2;
            tryHideProgress(false, true);
            this.nextPressed = false;
            if (error == null) {
                TLRPC.User user = (TLRPC.User) response;
                destroyTimer();
                destroyCodeTimer();
                UserConfig.getInstance(LoginActivity.this.currentAccount).setCurrentUser(user);
                UserConfig.getInstance(LoginActivity.this.currentAccount).saveConfig(true);
                ArrayList<TLRPC.User> users = new ArrayList<>();
                users.add(user);
                MessagesStorage.getInstance(LoginActivity.this.currentAccount).putUsersAndChats(users, null, true, true);
                MessagesController.getInstance(LoginActivity.this.currentAccount).putUser(user, false);
                NotificationCenter.getInstance(LoginActivity.this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                LoginActivity.this.getMessagesController().removeSuggestion(0L, "VALIDATE_PHONE_NUMBER");
                if (this.currentType == 3) {
                    AndroidUtilities.endIncomingCall();
                }
                animateSuccess(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda40
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.this.m3821xd841113d();
                    }
                });
                return;
            }
            this.lastError = error.text;
            this.nextPressed = false;
            LoginActivity.this.showDoneButton(false, true);
            int i3 = this.currentType;
            if ((i3 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) || ((i3 == 2 && ((i = this.nextType) == 4 || i == 3)) || (i3 == 4 && this.nextType == 2))) {
                createTimer();
            }
            int i4 = this.currentType;
            if (i4 == 2) {
                AndroidUtilities.setWaitingForSms(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i4 == 3) {
                AndroidUtilities.setWaitingForCall(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = true;
            if (this.currentType != 3) {
                boolean isWrongCode = false;
                if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    shakeWrongCode();
                    isWrongCode = true;
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    onBackPressed(true);
                    LoginActivity.this.setPage(0, true, null, true);
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                } else if (error.text.startsWith("FLOOD_WAIT")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("FloodWait", R.string.FloodWait));
                } else {
                    LoginActivity loginActivity = LoginActivity.this;
                    String string = LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle);
                    loginActivity.needShowAlert(string, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                }
                if (!isWrongCode) {
                    for (int a = 0; a < this.codeFieldContainer.codeField.length; a++) {
                        this.codeFieldContainer.codeField[a].setText("");
                    }
                    this.codeFieldContainer.isFocusSuppressed = false;
                    this.codeFieldContainer.codeField[0].requestFocus();
                }
            }
        }

        /* renamed from: lambda$onNextPressed$20$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3821xd841113d() {
            try {
                LoginActivity.this.fragmentView.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
            new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString((int) R.string.YourPasswordSuccess)).setMessage(LocaleController.getString((int) R.string.ChangePhoneNumberSuccess)).setPositiveButton(LocaleController.getString((int) R.string.OK), null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda33
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    LoginActivity.LoginActivitySmsView.this.m3820xe7a3cd13(dialogInterface);
                }
            }).show();
        }

        /* renamed from: lambda$onNextPressed$19$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3820xe7a3cd13(DialogInterface dialog) {
            LoginActivity.this.finishFragment();
        }

        /* renamed from: lambda$onNextPressed$26$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3827xeb54af77(final TLRPC.TL_account_confirmPhone req, TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3826xbd7c1518(error, req);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$25$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3826xbd7c1518(TLRPC.TL_error error, TLRPC.TL_account_confirmPhone req) {
            int i;
            int i2;
            tryHideProgress(false);
            this.nextPressed = false;
            if (error == null) {
                animateSuccess(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.this.m3825x8fa37ab9();
                    }
                });
                return;
            }
            this.lastError = error.text;
            int i3 = this.currentType;
            if ((i3 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) || ((i3 == 2 && ((i = this.nextType) == 4 || i == 3)) || (i3 == 4 && this.nextType == 2))) {
                createTimer();
            }
            int i4 = this.currentType;
            if (i4 == 2) {
                AndroidUtilities.setWaitingForSms(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i4 == 3) {
                AndroidUtilities.setWaitingForCall(true);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = true;
            if (this.currentType != 3) {
                AlertsCreator.processError(LoginActivity.this.currentAccount, error, LoginActivity.this, req, new Object[0]);
            }
            if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                shakeWrongCode();
            } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                onBackPressed(true);
                LoginActivity.this.setPage(0, true, null, true);
            }
        }

        /* renamed from: lambda$onNextPressed$24$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3825x8fa37ab9() {
            AlertDialog.Builder title = new AlertDialog.Builder(LoginActivity.this.getParentActivity()).setTitle(LocaleController.getString((int) R.string.CancelLinkSuccessTitle));
            PhoneFormat phoneFormat = PhoneFormat.getInstance();
            title.setMessage(LocaleController.formatString("CancelLinkSuccess", R.string.CancelLinkSuccess, phoneFormat.format("+" + this.phone))).setPositiveButton(LocaleController.getString((int) R.string.Close), null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda35
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    LoginActivity.LoginActivitySmsView.this.m3824x61cae05a(dialogInterface);
                }
            }).show();
        }

        /* renamed from: lambda$onNextPressed$23$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3824x61cae05a(DialogInterface dialog) {
            LoginActivity.this.finishFragment();
        }

        /* renamed from: lambda$onNextPressed$33$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3834xef0591db(final TLRPC.TL_auth_signIn req, final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3833xc12cf77c(error, response, req);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$32$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3833xc12cf77c(TLRPC.TL_error error, final TLObject response, final TLRPC.TL_auth_signIn req) {
            int i;
            int i2;
            tryHideProgress(false, true);
            boolean ok = false;
            if (error == null) {
                this.nextPressed = false;
                ok = true;
                LoginActivity.this.showDoneButton(false, true);
                destroyTimer();
                destroyCodeTimer();
                if (response instanceof TLRPC.TL_auth_authorizationSignUpRequired) {
                    TLRPC.TL_auth_authorizationSignUpRequired authorization = (TLRPC.TL_auth_authorizationSignUpRequired) response;
                    if (authorization.terms_of_service != null) {
                        LoginActivity.this.currentTermsOfService = authorization.terms_of_service;
                    }
                    final Bundle params = new Bundle();
                    params.putString("phoneFormated", this.requestPhone);
                    params.putString("phoneHash", this.phoneHash);
                    params.putString("code", req.phone_code);
                    animateSuccess(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda15
                        @Override // java.lang.Runnable
                        public final void run() {
                            LoginActivity.LoginActivitySmsView.this.m3828x192d49d6(params);
                        }
                    });
                } else {
                    animateSuccess(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda19
                        @Override // java.lang.Runnable
                        public final void run() {
                            LoginActivity.LoginActivitySmsView.this.m3829x4705e435(response);
                        }
                    });
                }
            } else {
                this.lastError = error.text;
                if (error.text.contains("SESSION_PASSWORD_NEEDED")) {
                    ok = true;
                    TLRPC.TL_account_getPassword req2 = new TLRPC.TL_account_getPassword();
                    ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda31
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LoginActivity.LoginActivitySmsView.this.m3832x93545d1d(req, tLObject, tL_error);
                        }
                    }, 10);
                    destroyTimer();
                    destroyCodeTimer();
                } else {
                    this.nextPressed = false;
                    LoginActivity.this.showDoneButton(false, true);
                    int i3 = this.currentType;
                    if ((i3 == 3 && ((i2 = this.nextType) == 4 || i2 == 2)) || ((i3 == 2 && ((i = this.nextType) == 4 || i == 3)) || (i3 == 4 && this.nextType == 2))) {
                        createTimer();
                    }
                    int i4 = this.currentType;
                    if (i4 == 2) {
                        AndroidUtilities.setWaitingForSms(true);
                        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
                    } else if (i4 == 3) {
                        AndroidUtilities.setWaitingForCall(true);
                        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReceiveCall);
                    }
                    this.waitingForEvent = true;
                    if (this.currentType != 3) {
                        boolean isWrongCode = false;
                        if (error.text.contains("PHONE_NUMBER_INVALID")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                        } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                            shakeWrongCode();
                            isWrongCode = true;
                        } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                            onBackPressed(true);
                            LoginActivity.this.setPage(0, true, null, true);
                            LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                        } else if (error.text.startsWith("FLOOD_WAIT")) {
                            LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("FloodWait", R.string.FloodWait));
                        } else {
                            LoginActivity loginActivity = LoginActivity.this;
                            String string = LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle);
                            loginActivity.needShowAlert(string, LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred) + "\n" + error.text);
                        }
                        if (!isWrongCode) {
                            for (int a = 0; a < this.codeFieldContainer.codeField.length; a++) {
                                this.codeFieldContainer.codeField[a].setText("");
                            }
                            this.codeFieldContainer.isFocusSuppressed = false;
                            this.codeFieldContainer.codeField[0].requestFocus();
                        }
                    }
                }
            }
            if (ok && this.currentType == 3) {
                AndroidUtilities.endIncomingCall();
            }
        }

        /* renamed from: lambda$onNextPressed$27$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3828x192d49d6(Bundle params) {
            LoginActivity.this.setPage(5, true, params, false);
        }

        /* renamed from: lambda$onNextPressed$28$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3829x4705e435(TLObject response) {
            LoginActivity.this.onAuthSuccess((TLRPC.TL_auth_authorization) response);
        }

        /* renamed from: lambda$onNextPressed$31$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3832x93545d1d(final TLRPC.TL_auth_signIn req, final TLObject response1, final TLRPC.TL_error error1) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3831x657bc2be(error1, response1, req);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$30$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3831x657bc2be(TLRPC.TL_error error1, TLObject response1, TLRPC.TL_auth_signIn req) {
            this.nextPressed = false;
            LoginActivity.this.showDoneButton(false, true);
            if (error1 != null) {
                LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), error1.text);
                return;
            }
            TLRPC.TL_account_password password = (TLRPC.TL_account_password) response1;
            if (!TwoStepVerificationActivity.canHandleCurrentPassword(password, true)) {
                AlertsCreator.showUpdateAppAlert(LoginActivity.this.getParentActivity(), LocaleController.getString("UpdateAppAlert", R.string.UpdateAppAlert), true);
                return;
            }
            final Bundle bundle = new Bundle();
            SerializedData data = new SerializedData(password.getObjectSize());
            password.serializeToStream(data);
            bundle.putString("password", Utilities.bytesToHex(data.toByteArray()));
            bundle.putString("phoneFormated", this.requestPhone);
            bundle.putString("phoneHash", this.phoneHash);
            bundle.putString("code", req.phone_code);
            animateSuccess(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3830x74de7e94(bundle);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$29$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3830x74de7e94(Bundle bundle) {
            LoginActivity.this.setPage(6, true, bundle, false);
        }

        private void animateSuccess(final Runnable callback) {
            for (int i = 0; i < this.codeFieldContainer.codeField.length; i++) {
                final int finalI = i;
                this.codeFieldContainer.postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda12
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivitySmsView.this.m3809x2868bf66(finalI);
                    }
                }, i * 75);
            }
            this.codeFieldContainer.postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3810x564159c5(callback);
                }
            }, (this.codeFieldContainer.codeField.length * 75) + 400);
        }

        /* renamed from: lambda$animateSuccess$34$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3809x2868bf66(int finalI) {
            this.codeFieldContainer.codeField[finalI].animateSuccessProgress(1.0f);
        }

        /* renamed from: lambda$animateSuccess$35$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3810x564159c5(Runnable callback) {
            for (int i = 0; i < this.codeFieldContainer.codeField.length; i++) {
                this.codeFieldContainer.codeField[i].animateSuccessProgress(0.0f);
            }
            callback.run();
            this.codeFieldContainer.isFocusSuppressed = false;
        }

        private void shakeWrongCode() {
            try {
                this.codeFieldContainer.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
            for (int a = 0; a < this.codeFieldContainer.codeField.length; a++) {
                this.codeFieldContainer.codeField[a].setText("");
                this.codeFieldContainer.codeField[a].animateErrorProgress(1.0f);
            }
            if (this.errorViewSwitcher.getCurrentView() == this.problemFrame) {
                this.errorViewSwitcher.showNext();
            }
            this.codeFieldContainer.codeField[0].requestFocus();
            AndroidUtilities.shakeViewSpring(this.codeFieldContainer, this.currentType == 11 ? 3.5f : 10.0f, new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3840x8a8c17f5();
                }
            });
            removeCallbacks(this.errorColorTimeout);
            postDelayed(this.errorColorTimeout, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            this.postedErrorColorTimeout = true;
        }

        /* renamed from: lambda$shakeWrongCode$37$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3840x8a8c17f5() {
            postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3839x5cb37d96();
                }
            }, 150L);
        }

        /* renamed from: lambda$shakeWrongCode$36$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3839x5cb37d96() {
            this.codeFieldContainer.isFocusSuppressed = false;
            this.codeFieldContainer.codeField[0].requestFocus();
            for (int a = 0; a < this.codeFieldContainer.codeField.length; a++) {
                this.codeFieldContainer.codeField[a].animateErrorProgress(0.0f);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            removeCallbacks(this.errorColorTimeout);
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean force) {
            if (LoginActivity.this.activityMode != 0) {
                LoginActivity.this.finishFragment();
                return false;
            } else if (!force) {
                LoginActivity.this.showDialog(new AlertDialog.Builder(LoginActivity.this.getParentActivity()).setTitle(LocaleController.getString((int) R.string.EditNumber)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("EditNumberInfo", R.string.EditNumberInfo, this.phone))).setPositiveButton(LocaleController.getString((int) R.string.Close), null).setNegativeButton(LocaleController.getString((int) R.string.Edit), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda22
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivity.LoginActivitySmsView.this.m3819x4cb2042(dialogInterface, i);
                    }
                }).create());
                return false;
            } else {
                this.nextPressed = false;
                tryHideProgress(true);
                TLRPC.TL_auth_cancelCode req = new TLRPC.TL_auth_cancelCode();
                req.phone_number = this.requestPhone;
                req.phone_code_hash = this.phoneHash;
                ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda34.INSTANCE, 10);
                destroyTimer();
                destroyCodeTimer();
                this.currentParams = null;
                int i = this.currentType;
                if (i == 2) {
                    AndroidUtilities.setWaitingForSms(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
                } else if (i == 3) {
                    AndroidUtilities.setWaitingForCall(false);
                    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                }
                this.waitingForEvent = false;
                return true;
            }
        }

        /* renamed from: lambda$onBackPressed$38$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3819x4cb2042(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            LoginActivity.this.setPage(0, true, null, true);
        }

        public static /* synthetic */ void lambda$onBackPressed$39(TLObject response, TLRPC.TL_error error) {
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onDestroyActivity() {
            super.onDestroyActivity();
            int i = this.currentType;
            if (i == 2) {
                AndroidUtilities.setWaitingForSms(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
            } else if (i == 3) {
                AndroidUtilities.setWaitingForCall(false);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReceiveCall);
            }
            this.waitingForEvent = false;
            destroyTimer();
            destroyCodeTimer();
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            RLottieDrawable rLottieDrawable = this.hintDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setCurrentFrame(0);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySmsView$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivitySmsView.this.m3835x92059c85();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* renamed from: lambda$onShow$40$org-telegram-ui-LoginActivity$LoginActivitySmsView */
        public /* synthetic */ void m3835x92059c85() {
            if (this.currentType != 3 && this.codeFieldContainer.codeField != null) {
                for (int a = this.codeFieldContainer.codeField.length - 1; a >= 0; a--) {
                    if (a == 0 || this.codeFieldContainer.codeField[a].length() != 0) {
                        this.codeFieldContainer.codeField[a].requestFocus();
                        this.codeFieldContainer.codeField[a].setSelection(this.codeFieldContainer.codeField[a].length());
                        LoginActivity.this.showKeyboard(this.codeFieldContainer.codeField[a]);
                        break;
                    }
                }
            }
            RLottieDrawable rLottieDrawable = this.hintDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.start();
            }
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int id, int account, Object... args) {
            if (!this.waitingForEvent || this.codeFieldContainer.codeField == null) {
                return;
            }
            if (id == NotificationCenter.didReceiveSmsCode) {
                this.codeFieldContainer.setText("" + args[0]);
                onNextPressed(null);
            } else if (id == NotificationCenter.didReceiveCall) {
                String num = "" + args[0];
                if (!AndroidUtilities.checkPhonePattern(this.pattern, num)) {
                    return;
                }
                if (!this.pattern.equals("*")) {
                    this.catchedPhone = num;
                    AndroidUtilities.endIncomingCall();
                }
                onNextPressed(num);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            String code = this.codeFieldContainer.getCode();
            if (code.length() != 0) {
                bundle.putString("smsview_code_" + this.currentType, code);
            }
            String str = this.catchedPhone;
            if (str != null) {
                bundle.putString("catchedPhone", str);
            }
            if (this.currentParams != null) {
                bundle.putBundle("smsview_params_" + this.currentType, this.currentParams);
            }
            int i = this.time;
            if (i != 0) {
                bundle.putInt("time", i);
            }
            int i2 = this.openTime;
            if (i2 != 0) {
                bundle.putInt("open", i2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("smsview_params_" + this.currentType);
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            String catched = bundle.getString("catchedPhone");
            if (catched != null) {
                this.catchedPhone = catched;
            }
            String code = bundle.getString("smsview_code_" + this.currentType);
            if (code != null && this.codeFieldContainer.codeField != null) {
                this.codeFieldContainer.setText(code);
            }
            int t = bundle.getInt("time");
            if (t != 0) {
                this.time = t;
            }
            int t2 = bundle.getInt("open");
            if (t2 != 0) {
                this.openTime = t2;
            }
        }
    }

    /* loaded from: classes4.dex */
    public class LoginActivityPasswordView extends SlideView {
        private TextView cancelButton;
        private EditTextBoldCursor codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private TLRPC.TL_account_password currentPassword;
        private RLottieImageView lockImageView;
        private boolean nextPressed;
        private OutlineTextContainerView outlineCodeField;
        private String passwordString;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        private TextView titleView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LoginActivityPasswordView(final Context context) {
            super(context);
            LoginActivity.this = this$0;
            setOrientation(1);
            FrameLayout lockFrameLayout = new FrameLayout(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.lockImageView = rLottieImageView;
            rLottieImageView.setAnimation(R.raw.tsv_setup_intro, 120, 120);
            this.lockImageView.setAutoRepeat(false);
            lockFrameLayout.addView(this.lockImageView, LayoutHelper.createFrame(120, 120, 1));
            lockFrameLayout.setVisibility((AndroidUtilities.isSmallScreen() || (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y && !AndroidUtilities.isTablet())) ? 8 : 0);
            addView(lockFrameLayout, LayoutHelper.createFrame(-1, -2, 1));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 18.0f);
            this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titleView.setText(LocaleController.getString((int) R.string.YourPasswordHeader));
            this.titleView.setGravity(17);
            this.titleView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.titleView, LayoutHelper.createFrame(-1, -2.0f, 1, 32.0f, 16.0f, 32.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.confirmTextView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.confirmTextView.setGravity(1);
            this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.confirmTextView.setText(LocaleController.getString((int) R.string.LoginPasswordTextShort));
            addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 1, 12, 8, 12, 0));
            OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
            this.outlineCodeField = outlineTextContainerView;
            outlineTextContainerView.setText(LocaleController.getString((int) R.string.EnterPassword));
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
            this.codeField = editTextBoldCursor;
            editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
            this.codeField.setCursorWidth(1.5f);
            this.codeField.setBackground(null);
            this.codeField.setImeOptions(268435461);
            this.codeField.setTextSize(1, 18.0f);
            this.codeField.setMaxLines(1);
            int padding = AndroidUtilities.dp(16.0f);
            this.codeField.setPadding(padding, padding, padding, padding);
            this.codeField.setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
            this.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            this.codeField.setTypeface(Typeface.DEFAULT);
            this.codeField.setGravity(LocaleController.isRTL ? 5 : 3);
            this.codeField.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda7
                @Override // android.view.View.OnFocusChangeListener
                public final void onFocusChange(View view, boolean z) {
                    LoginActivity.LoginActivityPasswordView.this.m3758x81c2d861(view, z);
                }
            });
            this.outlineCodeField.attachEditText(this.codeField);
            this.outlineCodeField.addView(this.codeField, LayoutHelper.createFrame(-1, -2, 48));
            this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda8
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView3, int i, KeyEvent keyEvent) {
                    return LoginActivity.LoginActivityPasswordView.this.m3759xa756e162(textView3, i, keyEvent);
                }
            });
            addView(this.outlineCodeField, LayoutHelper.createLinear(-1, -2, 1, 16, 32, 16, 0));
            TextView textView3 = new TextView(context);
            this.cancelButton = textView3;
            textView3.setGravity(19);
            this.cancelButton.setText(LocaleController.getString("ForgotPassword", R.string.ForgotPassword));
            this.cancelButton.setTextSize(1, 15.0f);
            this.cancelButton.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.cancelButton.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            FrameLayout bottomContainer = new FrameLayout(context);
            bottomContainer.addView(this.cancelButton, LayoutHelper.createFrame(-1, Build.VERSION.SDK_INT >= 21 ? 56 : 60, 80, 0.0f, 0.0f, 0.0f, 32.0f));
            addView(bottomContainer, LayoutHelper.createLinear(-1, -1, 80));
            VerticalPositionAutoAnimator.attach(this.cancelButton);
            this.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LoginActivity.LoginActivityPasswordView.this.m3764x633b0e67(context, view);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3758x81c2d861(View v, boolean hasFocus) {
            this.outlineCodeField.animateSelection(hasFocus ? 1.0f : 0.0f);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ boolean m3759xa756e162(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                onNextPressed(null);
                return true;
            }
            return false;
        }

        /* renamed from: lambda$new$6$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3764x633b0e67(Context context, View view) {
            if (LoginActivity.this.radialProgressView.getTag() != null) {
                return;
            }
            if (this.currentPassword.has_recovery) {
                LoginActivity.this.needShowProgress(0);
                TLRPC.TL_auth_requestPasswordRecovery req = new TLRPC.TL_auth_requestPasswordRecovery();
                ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda2
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LoginActivity.LoginActivityPasswordView.this.m3762x1812fc65(tLObject, tL_error);
                    }
                }, 10);
                return;
            }
            AndroidUtilities.hideKeyboard(this.codeField);
            new AlertDialog.Builder(context).setTitle(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle)).setMessage(LocaleController.getString((int) R.string.RestorePasswordNoEmailText)).setPositiveButton(LocaleController.getString((int) R.string.Close), null).setNegativeButton(LocaleController.getString((int) R.string.ResetAccount), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivityPasswordView.this.m3763x3da70566(dialogInterface, i);
                }
            }).show();
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3762x1812fc65(final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityPasswordView.this.m3761xf27ef364(error, response);
                }
            });
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3761xf27ef364(TLRPC.TL_error error, TLObject response) {
            String timeString;
            LoginActivity.this.needHideProgress(false);
            if (error == null) {
                final TLRPC.TL_auth_passwordRecovery res = (TLRPC.TL_auth_passwordRecovery) response;
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
                String rawPattern = res.email_pattern;
                SpannableStringBuilder emailPattern = SpannableStringBuilder.valueOf(rawPattern);
                int startIndex = rawPattern.indexOf(42);
                int endIndex = rawPattern.lastIndexOf(42);
                if (startIndex != endIndex && startIndex != -1 && endIndex != -1) {
                    TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                    run.flags |= 256;
                    run.start = startIndex;
                    run.end = endIndex + 1;
                    emailPattern.setSpan(new TextStyleSpan(run), startIndex, endIndex + 1, 0);
                }
                builder.setMessage(AndroidUtilities.formatSpannable(LocaleController.getString((int) R.string.RestoreEmailSent), emailPattern));
                builder.setTitle(LocaleController.getString("RestoreEmailSentTitle", R.string.RestoreEmailSentTitle));
                builder.setPositiveButton(LocaleController.getString((int) R.string.Continue), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda5
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivity.LoginActivityPasswordView.this.m3760xcceaea63(res, dialogInterface, i);
                    }
                });
                Dialog dialog = LoginActivity.this.showDialog(builder.create());
                if (dialog != null) {
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                }
            } else if (!error.text.startsWith("FLOOD_WAIT")) {
                LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), error.text);
            } else {
                int time = Utilities.parseInt((CharSequence) error.text).intValue();
                if (time < 60) {
                    timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
                } else {
                    timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
                }
                LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.WrongCodeTitle), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
            }
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3760xcceaea63(TLRPC.TL_auth_passwordRecovery res, DialogInterface dialogInterface, int i) {
            Bundle bundle = new Bundle();
            bundle.putString("email_unconfirmed_pattern", res.email_pattern);
            bundle.putString("password", this.passwordString);
            bundle.putString("requestPhone", this.requestPhone);
            bundle.putString("phoneHash", this.phoneHash);
            bundle.putString("phoneCode", this.phoneCode);
            LoginActivity.this.setPage(7, true, bundle, false);
        }

        /* renamed from: lambda$new$5$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3763x3da70566(DialogInterface dialog, int which) {
            LoginActivity.this.tryResetAccount(this.requestPhone, this.phoneHash, this.phoneCode);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.codeField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.codeField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.codeField.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.cancelButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            this.outlineCodeField.updateColor();
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", R.string.LoginPassword);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle params, boolean restore) {
            if (params == null) {
                return;
            }
            if (params.isEmpty()) {
                AndroidUtilities.hideKeyboard(this.codeField);
                return;
            }
            this.codeField.setText("");
            this.currentParams = params;
            String string = params.getString("password");
            this.passwordString = string;
            if (string != null) {
                SerializedData data = new SerializedData(Utilities.hexToBytes(this.passwordString));
                this.currentPassword = TLRPC.TL_account_password.TLdeserialize(data, data.readInt32(false), false);
            }
            this.requestPhone = params.getString("phoneFormated");
            this.phoneHash = params.getString("phoneHash");
            this.phoneCode = params.getString("code");
            TLRPC.TL_account_password tL_account_password = this.currentPassword;
            if (tL_account_password != null && !TextUtils.isEmpty(tL_account_password.hint)) {
                this.codeField.setHint(this.currentPassword.hint);
            } else {
                this.codeField.setHint((CharSequence) null);
            }
        }

        private void onPasscodeError(boolean clear) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            if (clear) {
                this.codeField.setText("");
            }
            LoginActivity.this.onFieldError(this.outlineCodeField, true);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String code) {
            if (this.nextPressed) {
                return;
            }
            final String oldPassword = this.codeField.getText().toString();
            if (oldPassword.length() == 0) {
                onPasscodeError(false);
                return;
            }
            this.nextPressed = true;
            LoginActivity.this.needShowProgress(0);
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityPasswordView.this.m3767xc777d084(oldPassword);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$12$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3767xc777d084(String oldPassword) {
            byte[] passwordBytes;
            TLRPC.PasswordKdfAlgo current_algo = this.currentPassword.current_algo;
            if (current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                byte[] passwordBytes2 = AndroidUtilities.getStringBytes(oldPassword);
                TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) current_algo;
                passwordBytes = SRPHelper.getX(passwordBytes2, algo);
            } else {
                passwordBytes = null;
            }
            TLRPC.TL_auth_checkPassword req = new TLRPC.TL_auth_checkPassword();
            RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LoginActivity.LoginActivityPasswordView.this.m3766xa1e3c783(tLObject, tL_error);
                }
            };
            if (current_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                req.password = SRPHelper.startCheck(passwordBytes, this.currentPassword.srp_id, this.currentPassword.srp_B, (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) current_algo);
                if (req.password != null) {
                    ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, requestDelegate, 10);
                    return;
                }
                TLRPC.TL_error error = new TLRPC.TL_error();
                error.text = "PASSWORD_HASH_INVALID";
                requestDelegate.run(null, error);
            }
        }

        /* renamed from: lambda$onNextPressed$11$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3766xa1e3c783(final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityPasswordView.this.m3765x7c4fbe82(error, response);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$10$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3765x7c4fbe82(TLRPC.TL_error error, final TLObject response) {
            String timeString;
            this.nextPressed = false;
            if (error != null && "SRP_ID_INVALID".equals(error.text)) {
                TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
                ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda4
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        LoginActivity.LoginActivityPasswordView.this.m3769xc3ea9559(tLObject, tL_error);
                    }
                }, 8);
            } else if (response instanceof TLRPC.TL_auth_authorization) {
                LoginActivity.this.showDoneButton(false, true);
                postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda11
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivityPasswordView.this.m3770xe97e9e5a(response);
                    }
                }, 150L);
            } else {
                LoginActivity.this.needHideProgress(false);
                if (error.text.equals("PASSWORD_HASH_INVALID")) {
                    onPasscodeError(true);
                } else if (!error.text.startsWith("FLOOD_WAIT")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), error.text);
                } else {
                    int time = Utilities.parseInt((CharSequence) error.text).intValue();
                    if (time >= 60) {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
                    } else {
                        timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
                    }
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                }
            }
        }

        /* renamed from: lambda$onNextPressed$8$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3769xc3ea9559(final TLObject response2, final TLRPC.TL_error error2) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityPasswordView.this.m3768x9e568c58(error2, response2);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$7$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3768x9e568c58(TLRPC.TL_error error2, TLObject response2) {
            if (error2 == null) {
                this.currentPassword = (TLRPC.TL_account_password) response2;
                onNextPressed(null);
            }
        }

        /* renamed from: lambda$onNextPressed$9$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3770xe97e9e5a(TLObject response) {
            LoginActivity.this.needHideProgress(false, false);
            AndroidUtilities.hideKeyboard(this.codeField);
            LoginActivity.this.onAuthSuccess((TLRPC.TL_auth_authorization) response);
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean force) {
            this.nextPressed = false;
            LoginActivity.this.needHideProgress(true);
            this.currentParams = null;
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPasswordView$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityPasswordView.this.m3771x4068ecff();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* renamed from: lambda$onShow$13$org-telegram-ui-LoginActivity$LoginActivityPasswordView */
        public /* synthetic */ void m3771x4068ecff() {
            EditTextBoldCursor editTextBoldCursor = this.codeField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.codeField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                LoginActivity.this.showKeyboard(this.codeField);
                this.lockImageView.getAnimatedDrawable().setCurrentFrame(0, false);
                this.lockImageView.playAnimation();
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            String code = this.codeField.getText().toString();
            if (code.length() != 0) {
                bundle.putString("passview_code", code);
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("passview_params", bundle2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("passview_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            String code = bundle.getString("passview_code");
            if (code != null) {
                this.codeField.setText(code);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class LoginActivityResetWaitView extends SlideView {
        private TextView confirmTextView;
        private Bundle currentParams;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        private TextView resetAccountButton;
        private TextView resetAccountText;
        private TextView resetAccountTime;
        private int startTime;
        private Runnable timeRunnable;
        private TextView titleView;
        private RLottieImageView waitImageView;
        private int waitTime;
        private Boolean wasResetButtonActive;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LoginActivityResetWaitView(Context context) {
            super(context);
            LoginActivity.this = this$0;
            setOrientation(1);
            LinearLayout innerLinearLayout = new LinearLayout(context);
            innerLinearLayout.setOrientation(1);
            innerLinearLayout.setGravity(17);
            FrameLayout waitFrameLayout = new FrameLayout(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.waitImageView = rLottieImageView;
            rLottieImageView.setAutoRepeat(true);
            this.waitImageView.setAnimation(R.raw.sandclock, 120, 120);
            waitFrameLayout.addView(this.waitImageView, LayoutHelper.createFrame(120, 120, 1));
            waitFrameLayout.setVisibility((AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y || AndroidUtilities.isTablet()) ? 0 : 8);
            innerLinearLayout.addView(waitFrameLayout, LayoutHelper.createFrame(-1, -2, 1));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 18.0f);
            this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titleView.setText(LocaleController.getString((int) R.string.ResetAccount));
            this.titleView.setGravity(17);
            this.titleView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            innerLinearLayout.addView(this.titleView, LayoutHelper.createFrame(-1, -2.0f, 1, 32.0f, 16.0f, 32.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.confirmTextView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.confirmTextView.setGravity(1);
            this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            innerLinearLayout.addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 1, 12, 8, 12, 0));
            addView(innerLinearLayout, LayoutHelper.createLinear(-1, 0, 1.0f));
            TextView textView3 = new TextView(context);
            this.resetAccountText = textView3;
            textView3.setGravity(1);
            this.resetAccountText.setText(LocaleController.getString("ResetAccountStatus", R.string.ResetAccountStatus));
            this.resetAccountText.setTextSize(1, 14.0f);
            this.resetAccountText.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.resetAccountText, LayoutHelper.createLinear(-2, -2, 49, 0, 24, 0, 0));
            TextView textView4 = new TextView(context);
            this.resetAccountTime = textView4;
            textView4.setGravity(1);
            this.resetAccountTime.setTextSize(1, 20.0f);
            this.resetAccountTime.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.resetAccountTime.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.resetAccountTime, LayoutHelper.createLinear(-2, -2, 1, 0, 8, 0, 0));
            TextView textView5 = new TextView(context);
            this.resetAccountButton = textView5;
            textView5.setGravity(17);
            this.resetAccountButton.setText(LocaleController.getString((int) R.string.ResetAccount));
            this.resetAccountButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.resetAccountButton.setTextSize(1, 15.0f);
            this.resetAccountButton.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.resetAccountButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            this.resetAccountButton.setTextColor(-1);
            addView(this.resetAccountButton, LayoutHelper.createLinear(-1, 50, 1, 16, 32, 16, 48));
            this.resetAccountButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LoginActivity.LoginActivityResetWaitView.this.m3808xa1ae125(view);
                }
            });
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$LoginActivityResetWaitView */
        public /* synthetic */ void m3808xa1ae125(View view) {
            if (LoginActivity.this.radialProgressView.getTag() != null) {
                return;
            }
            LoginActivity.this.showDialog(new AlertDialog.Builder(LoginActivity.this.getParentActivity()).setTitle(LocaleController.getString("ResetMyAccountWarning", R.string.ResetMyAccountWarning)).setMessage(LocaleController.getString("ResetMyAccountWarningText", R.string.ResetMyAccountWarningText)).setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", R.string.ResetMyAccountWarningReset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivityResetWaitView.this.m3807x7d2dca06(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).create());
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivityResetWaitView */
        public /* synthetic */ void m3807x7d2dca06(DialogInterface dialogInterface, int i) {
            LoginActivity.this.needShowProgress(0);
            TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
            req.reason = "Forgot password";
            ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda3
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LoginActivity.LoginActivityResetWaitView.this.m3806xf040b2e7(tLObject, tL_error);
                }
            }, 10);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivityResetWaitView */
        public /* synthetic */ void m3806xf040b2e7(TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityResetWaitView$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityResetWaitView.this.m3805x63539bc8(error);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivityResetWaitView */
        public /* synthetic */ void m3805x63539bc8(TLRPC.TL_error error) {
            LoginActivity.this.needHideProgress(false);
            if (error == null) {
                if (this.requestPhone == null || this.phoneHash == null || this.phoneCode == null) {
                    LoginActivity.this.setPage(0, true, null, true);
                    return;
                }
                Bundle params = new Bundle();
                params.putString("phoneFormated", this.requestPhone);
                params.putString("phoneHash", this.phoneHash);
                params.putString("code", this.phoneCode);
                LoginActivity.this.setPage(5, true, params, false);
            } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
                LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("ResetAccountCancelledAlert", R.string.ResetAccountCancelledAlert));
            } else {
                LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), error.text);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.resetAccountText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.resetAccountTime.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.resetAccountButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_changephoneinfo_image2), Theme.getColor(Theme.key_chats_actionPressedBackground)));
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("ResetAccount", R.string.ResetAccount);
        }

        public void updateTimeText() {
            int i = 0;
            int timeLeft = Math.max(0, this.waitTime - (ConnectionsManager.getInstance(LoginActivity.this.currentAccount).getCurrentTime() - this.startTime));
            int days = timeLeft / 86400;
            int daysRounded = Math.round(timeLeft / 86400.0f);
            int hours = timeLeft / 3600;
            int minutes = (timeLeft / 60) % 60;
            int seconds = timeLeft % 60;
            if (days >= 2) {
                this.resetAccountTime.setText(LocaleController.formatPluralString("Days", daysRounded, new Object[0]));
            } else {
                this.resetAccountTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", Integer.valueOf(hours), Integer.valueOf(minutes), Integer.valueOf(seconds)));
            }
            boolean isResetButtonActive = timeLeft == 0;
            Boolean bool = this.wasResetButtonActive;
            if (bool == null || bool.booleanValue() != isResetButtonActive) {
                if (!isResetButtonActive) {
                    this.waitImageView.setAutoRepeat(true);
                    if (!this.waitImageView.isPlaying()) {
                        this.waitImageView.playAnimation();
                    }
                } else {
                    this.waitImageView.getAnimatedDrawable().setAutoRepeat(0);
                }
                this.resetAccountTime.setVisibility(isResetButtonActive ? 4 : 0);
                this.resetAccountText.setVisibility(isResetButtonActive ? 4 : 0);
                TextView textView = this.resetAccountButton;
                if (!isResetButtonActive) {
                    i = 4;
                }
                textView.setVisibility(i);
                this.wasResetButtonActive = Boolean.valueOf(isResetButtonActive);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle params, boolean restore) {
            if (params == null) {
                return;
            }
            this.currentParams = params;
            this.requestPhone = params.getString("phoneFormated");
            this.phoneHash = params.getString("phoneHash");
            this.phoneCode = params.getString("code");
            this.startTime = params.getInt("startTime");
            this.waitTime = params.getInt("waitTime");
            TextView textView = this.confirmTextView;
            PhoneFormat phoneFormat = PhoneFormat.getInstance();
            textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", R.string.ResetAccountInfo, LocaleController.addNbsp(phoneFormat.format("+" + this.requestPhone)))));
            updateTimeText();
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.LoginActivity.LoginActivityResetWaitView.1
                @Override // java.lang.Runnable
                public void run() {
                    if (LoginActivityResetWaitView.this.timeRunnable == this) {
                        LoginActivityResetWaitView.this.updateTimeText();
                        AndroidUtilities.runOnUIThread(LoginActivityResetWaitView.this.timeRunnable, 1000L);
                    }
                }
            };
            this.timeRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 1000L);
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean force) {
            LoginActivity.this.needHideProgress(true);
            AndroidUtilities.cancelRunOnUIThread(this.timeRunnable);
            this.timeRunnable = null;
            this.currentParams = null;
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("resetview_params", bundle2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("resetview_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class LoginActivityRecoverView extends SlideView {
        private CodeFieldContainer codeFieldContainer;
        private TextView confirmTextView;
        private Bundle currentParams;
        private Runnable errorColorTimeout = new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                LoginActivity.LoginActivityRecoverView.this.m3772xcd144188();
            }
        };
        private RLottieImageView inboxImageView;
        private boolean nextPressed;
        private String passwordString;
        private String phoneCode;
        private String phoneHash;
        private boolean postedErrorColorTimeout;
        private String requestPhone;
        private TextView titleView;
        private TextView troubleButton;

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$LoginActivityRecoverView */
        public /* synthetic */ void m3772xcd144188() {
            this.postedErrorColorTimeout = false;
            for (int i = 0; i < this.codeFieldContainer.codeField.length; i++) {
                this.codeFieldContainer.codeField[i].animateErrorProgress(0.0f);
            }
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LoginActivityRecoverView(Context context) {
            super(context);
            CodeNumberField[] codeNumberFieldArr;
            LoginActivity.this = this$0;
            setOrientation(1);
            FrameLayout inboxFrameLayout = new FrameLayout(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.inboxImageView = rLottieImageView;
            rLottieImageView.setAnimation(R.raw.tsv_setup_mail, 120, 120);
            this.inboxImageView.setAutoRepeat(false);
            inboxFrameLayout.addView(this.inboxImageView, LayoutHelper.createFrame(120, 120, 1));
            inboxFrameLayout.setVisibility((AndroidUtilities.isSmallScreen() || (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y && !AndroidUtilities.isTablet())) ? 8 : 0);
            addView(inboxFrameLayout, LayoutHelper.createFrame(-1, -2, 1));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 18.0f);
            this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titleView.setText(LocaleController.getString((int) R.string.EnterCode));
            this.titleView.setGravity(17);
            this.titleView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.titleView, LayoutHelper.createFrame(-1, -2.0f, 1, 32.0f, 16.0f, 32.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.confirmTextView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.confirmTextView.setGravity(17);
            this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.confirmTextView.setText(LocaleController.getString((int) R.string.RestoreEmailSentInfo));
            addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 1, 12, 8, 12, 0));
            CodeFieldContainer codeFieldContainer = new CodeFieldContainer(context) { // from class: org.telegram.ui.LoginActivity.LoginActivityRecoverView.1
                @Override // org.telegram.ui.CodeFieldContainer
                protected void processNextPressed() {
                    LoginActivityRecoverView.this.onNextPressed(null);
                }
            };
            this.codeFieldContainer = codeFieldContainer;
            codeFieldContainer.setNumbersCount(6, 1);
            for (CodeNumberField f : this.codeFieldContainer.codeField) {
                f.setShowSoftInputOnFocusCompat(!hasCustomKeyboard() || this$0.isCustomKeyboardForceDisabled());
                f.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.LoginActivity.LoginActivityRecoverView.2
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (LoginActivityRecoverView.this.postedErrorColorTimeout) {
                            LoginActivityRecoverView loginActivityRecoverView = LoginActivityRecoverView.this;
                            loginActivityRecoverView.removeCallbacks(loginActivityRecoverView.errorColorTimeout);
                            LoginActivityRecoverView.this.errorColorTimeout.run();
                        }
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                    }
                });
                f.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda3
                    @Override // android.view.View.OnFocusChangeListener
                    public final void onFocusChange(View view, boolean z) {
                        LoginActivity.LoginActivityRecoverView.this.m3773xce4a9467(view, z);
                    }
                });
            }
            addView(this.codeFieldContainer, LayoutHelper.createLinear(-2, 42, 1, 0, 32, 0, 0));
            SpoilersTextView spoilersTextView = new SpoilersTextView(context);
            this.troubleButton = spoilersTextView;
            spoilersTextView.setGravity(17);
            this.troubleButton.setTextSize(1, 14.0f);
            this.troubleButton.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.troubleButton.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            this.troubleButton.setMaxLines(2);
            this.troubleButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LoginActivity.LoginActivityRecoverView.this.m3776xd1ed8d04(view);
                }
            });
            FrameLayout bottomContainer = new FrameLayout(context);
            bottomContainer.addView(this.troubleButton, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 32.0f));
            addView(bottomContainer, LayoutHelper.createLinear(-1, 0, 1.0f));
            VerticalPositionAutoAnimator.attach(this.troubleButton);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivityRecoverView */
        public /* synthetic */ void m3773xce4a9467(View v, boolean hasFocus) {
            if (hasFocus) {
                LoginActivity.this.keyboardView.setEditText((EditText) v);
                LoginActivity.this.keyboardView.setDispatchBackWhenEmpty(true);
            }
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$LoginActivityRecoverView */
        public /* synthetic */ void m3776xd1ed8d04(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity()).setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", R.string.RestorePasswordNoEmailTitle)).setMessage(LocaleController.getString("RestoreEmailTroubleText", R.string.RestoreEmailTroubleText)).setPositiveButton(LocaleController.getString((int) R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivityRecoverView.this.m3774xcf80e746(dialogInterface, i);
                }
            }).setNegativeButton(LocaleController.getString((int) R.string.ResetAccount), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivityRecoverView.this.m3775xd0b73a25(dialogInterface, i);
                }
            });
            Dialog dialog = LoginActivity.this.showDialog(builder.create());
            if (dialog != null) {
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
            }
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivityRecoverView */
        public /* synthetic */ void m3774xcf80e746(DialogInterface dialogInterface, int i) {
            LoginActivity.this.setPage(6, true, new Bundle(), true);
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$LoginActivityRecoverView */
        public /* synthetic */ void m3775xd0b73a25(DialogInterface dialog, int which) {
            LoginActivity.this.tryResetAccount(this.requestPhone, this.phoneHash, this.phoneCode);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.troubleButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            this.codeFieldContainer.invalidate();
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            removeCallbacks(this.errorColorTimeout);
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean hasCustomKeyboard() {
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("LoginPassword", R.string.LoginPassword);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle params, boolean restore) {
            if (params == null) {
                return;
            }
            this.codeFieldContainer.setText("");
            this.currentParams = params;
            this.passwordString = params.getString("password");
            this.requestPhone = this.currentParams.getString("requestPhone");
            this.phoneHash = this.currentParams.getString("phoneHash");
            this.phoneCode = this.currentParams.getString("phoneCode");
            String rawPattern = this.currentParams.getString("email_unconfirmed_pattern");
            SpannableStringBuilder unconfirmedPattern = SpannableStringBuilder.valueOf(rawPattern);
            int startIndex = rawPattern.indexOf(42);
            int endIndex = rawPattern.lastIndexOf(42);
            if (startIndex != endIndex && startIndex != -1 && endIndex != -1) {
                TextStyleSpan.TextStyleRun run = new TextStyleSpan.TextStyleRun();
                run.flags |= 256;
                run.start = startIndex;
                run.end = endIndex + 1;
                unconfirmedPattern.setSpan(new TextStyleSpan(run), startIndex, endIndex + 1, 0);
            }
            this.troubleButton.setText(AndroidUtilities.formatSpannable(LocaleController.getString((int) R.string.RestoreEmailNoAccess), unconfirmedPattern));
            LoginActivity.this.showKeyboard(this.codeFieldContainer);
            this.codeFieldContainer.requestFocus();
        }

        private void onPasscodeError(boolean clear) {
            CodeNumberField[] codeNumberFieldArr;
            CodeNumberField[] codeNumberFieldArr2;
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            try {
                this.codeFieldContainer.performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
            if (clear) {
                for (CodeNumberField f : this.codeFieldContainer.codeField) {
                    f.setText("");
                }
            }
            for (CodeNumberField f2 : this.codeFieldContainer.codeField) {
                f2.animateErrorProgress(1.0f);
            }
            this.codeFieldContainer.codeField[0].requestFocus();
            AndroidUtilities.shakeViewSpring(this.codeFieldContainer, new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRecoverView.this.m3780xbb061fb7();
                }
            });
        }

        /* renamed from: lambda$onPasscodeError$6$org-telegram-ui-LoginActivity$LoginActivityRecoverView */
        public /* synthetic */ void m3780xbb061fb7() {
            postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRecoverView.this.m3779xb9cfccd8();
                }
            }, 150L);
            removeCallbacks(this.errorColorTimeout);
            postDelayed(this.errorColorTimeout, 3000L);
            this.postedErrorColorTimeout = true;
        }

        /* renamed from: lambda$onPasscodeError$5$org-telegram-ui-LoginActivity$LoginActivityRecoverView */
        public /* synthetic */ void m3779xb9cfccd8() {
            this.codeFieldContainer.isFocusSuppressed = false;
            this.codeFieldContainer.codeField[0].requestFocus();
            for (int a = 0; a < this.codeFieldContainer.codeField.length; a++) {
                this.codeFieldContainer.codeField[a].animateErrorProgress(0.0f);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String code) {
            CodeNumberField[] codeNumberFieldArr;
            if (this.nextPressed) {
                return;
            }
            this.codeFieldContainer.isFocusSuppressed = true;
            for (CodeNumberField f : this.codeFieldContainer.codeField) {
                f.animateFocusedProgress(0.0f);
            }
            final String code2 = this.codeFieldContainer.getCode();
            if (code2.length() == 0) {
                onPasscodeError(false);
                return;
            }
            this.nextPressed = true;
            LoginActivity.this.needShowProgress(0);
            TLRPC.TL_auth_checkRecoveryPassword req = new TLRPC.TL_auth_checkRecoveryPassword();
            req.code = code2;
            ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda9
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LoginActivity.LoginActivityRecoverView.this.m3778x9050590(code2, tLObject, tL_error);
                }
            }, 10);
        }

        /* renamed from: lambda$onNextPressed$8$org-telegram-ui-LoginActivity$LoginActivityRecoverView */
        public /* synthetic */ void m3778x9050590(final String finalCode, final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRecoverView.this.m3777x7ceb2b1(response, finalCode, error);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$7$org-telegram-ui-LoginActivity$LoginActivityRecoverView */
        public /* synthetic */ void m3777x7ceb2b1(TLObject response, String finalCode, TLRPC.TL_error error) {
            String timeString;
            LoginActivity.this.needHideProgress(false);
            this.nextPressed = false;
            if (response instanceof TLRPC.TL_boolTrue) {
                Bundle params = new Bundle();
                params.putString("emailCode", finalCode);
                params.putString("password", this.passwordString);
                LoginActivity.this.setPage(9, true, params, false);
            } else if (error == null || error.text.startsWith("CODE_INVALID")) {
                onPasscodeError(true);
            } else if (!error.text.startsWith("FLOOD_WAIT")) {
                LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), error.text);
            } else {
                int time = Utilities.parseInt((CharSequence) error.text).intValue();
                if (time < 60) {
                    timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
                } else {
                    timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
                }
                LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean force) {
            LoginActivity.this.needHideProgress(true);
            this.currentParams = null;
            this.nextPressed = false;
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRecoverView$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRecoverView.this.m3781x6db0269();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* renamed from: lambda$onShow$9$org-telegram-ui-LoginActivity$LoginActivityRecoverView */
        public /* synthetic */ void m3781x6db0269() {
            this.inboxImageView.getAnimatedDrawable().setCurrentFrame(0, false);
            this.inboxImageView.playAnimation();
            CodeFieldContainer codeFieldContainer = this.codeFieldContainer;
            if (codeFieldContainer != null) {
                codeFieldContainer.codeField[0].requestFocus();
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            String code = this.codeFieldContainer.getCode();
            if (code != null && code.length() != 0) {
                bundle.putString("recoveryview_code", code);
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("recoveryview_params", bundle2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("recoveryview_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            String code = bundle.getString("recoveryview_code");
            if (code != null) {
                this.codeFieldContainer.setText(code);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class LoginActivityNewPasswordView extends SlideView {
        private TextView cancelButton;
        private EditTextBoldCursor[] codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private TLRPC.TL_account_password currentPassword;
        private int currentStage;
        private String emailCode;
        private boolean isPasswordVisible;
        private String newPassword;
        private boolean nextPressed;
        private OutlineTextContainerView[] outlineFields;
        private ImageView passwordButton;
        private String passwordString;
        private TextView titleTextView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LoginActivityNewPasswordView(Context context, int stage) {
            super(context);
            LoginActivity.this = this$0;
            this.currentStage = stage;
            setOrientation(1);
            EditTextBoldCursor[] editTextBoldCursorArr = new EditTextBoldCursor[stage == 1 ? 1 : 2];
            this.codeField = editTextBoldCursorArr;
            this.outlineFields = new OutlineTextContainerView[editTextBoldCursorArr.length];
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            float f = 18.0f;
            textView.setTextSize(1, 18.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titleTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.titleTextView.setGravity(49);
            this.titleTextView.setText(LocaleController.getString((int) R.string.SetNewPassword));
            addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 8, AndroidUtilities.isSmallScreen() ? 16 : 72, 8, 0));
            TextView textView2 = new TextView(context);
            this.confirmTextView = textView2;
            float f2 = 16.0f;
            textView2.setTextSize(1, 16.0f);
            this.confirmTextView.setGravity(1);
            this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.confirmTextView, LayoutHelper.createLinear(-2, -2, 1, 8, 6, 8, 16));
            int a = 0;
            while (a < this.codeField.length) {
                final OutlineTextContainerView outlineField = new OutlineTextContainerView(context);
                this.outlineFields[a] = outlineField;
                outlineField.setText(LocaleController.getString(stage == 0 ? a == 0 ? R.string.PleaseEnterNewFirstPasswordHint : R.string.PleaseEnterNewSecondPasswordHint : R.string.PasswordHintPlaceholder));
                this.codeField[a] = new EditTextBoldCursor(context);
                this.codeField[a].setCursorSize(AndroidUtilities.dp(20.0f));
                this.codeField[a].setCursorWidth(1.5f);
                this.codeField[a].setImeOptions(268435461);
                this.codeField[a].setTextSize(1, f);
                this.codeField[a].setMaxLines(1);
                this.codeField[a].setBackground(null);
                int padding = AndroidUtilities.dp(f2);
                this.codeField[a].setPadding(padding, padding, padding, padding);
                if (stage == 0) {
                    this.codeField[a].setInputType(TsExtractor.TS_STREAM_TYPE_AC3);
                    this.codeField[a].setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                this.codeField[a].setTypeface(Typeface.DEFAULT);
                this.codeField[a].setGravity(LocaleController.isRTL ? 5 : 3);
                EditText field = this.codeField[a];
                final boolean showPasswordButton = a == 0 && stage == 0;
                field.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.LoginActivity.LoginActivityNewPasswordView.1
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        if (showPasswordButton) {
                            if (LoginActivityNewPasswordView.this.passwordButton.getVisibility() == 0 || TextUtils.isEmpty(s)) {
                                if (LoginActivityNewPasswordView.this.passwordButton.getVisibility() != 8 && TextUtils.isEmpty(s)) {
                                    AndroidUtilities.updateViewVisibilityAnimated(LoginActivityNewPasswordView.this.passwordButton, false, 0.1f, true);
                                    return;
                                }
                                return;
                            }
                            if (LoginActivityNewPasswordView.this.isPasswordVisible) {
                                LoginActivityNewPasswordView.this.passwordButton.callOnClick();
                            }
                            AndroidUtilities.updateViewVisibilityAnimated(LoginActivityNewPasswordView.this.passwordButton, true, 0.1f, true);
                        }
                    }
                });
                this.codeField[a].setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda4
                    @Override // android.view.View.OnFocusChangeListener
                    public final void onFocusChange(View view, boolean z) {
                        OutlineTextContainerView.this.animateSelection(hasFocus ? 1.0f : 0.0f);
                    }
                });
                if (showPasswordButton) {
                    LinearLayout linearLayout = new LinearLayout(context);
                    linearLayout.setOrientation(0);
                    linearLayout.setGravity(16);
                    linearLayout.addView(this.codeField[a], LayoutHelper.createLinear(0, -2, 1.0f));
                    ImageView imageView = new ImageView(context);
                    this.passwordButton = imageView;
                    imageView.setImageResource(R.drawable.msg_message);
                    AndroidUtilities.updateViewVisibilityAnimated(this.passwordButton, true, 0.1f, false);
                    this.passwordButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            LoginActivity.LoginActivityNewPasswordView.this.m3748x8115ecde(view);
                        }
                    });
                    linearLayout.addView(this.passwordButton, LayoutHelper.createLinearRelatively(24.0f, 24.0f, 0, 0.0f, 0.0f, 14.0f, 0.0f));
                    outlineField.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f));
                } else {
                    outlineField.addView(this.codeField[a], LayoutHelper.createFrame(-1, -2.0f));
                }
                outlineField.attachEditText(this.codeField[a]);
                addView(outlineField, LayoutHelper.createLinear(-1, -2, 1, 16, 16, 16, 0));
                final int num = a;
                this.codeField[a].setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda5
                    @Override // android.widget.TextView.OnEditorActionListener
                    public final boolean onEditorAction(TextView textView3, int i, KeyEvent keyEvent) {
                        return LoginActivity.LoginActivityNewPasswordView.this.m3749x8719b83d(num, textView3, i, keyEvent);
                    }
                });
                a++;
                f = 18.0f;
                f2 = 16.0f;
            }
            if (stage == 0) {
                this.confirmTextView.setText(LocaleController.getString("PleaseEnterNewFirstPasswordLogin", R.string.PleaseEnterNewFirstPasswordLogin));
            } else {
                this.confirmTextView.setText(LocaleController.getString("PasswordHintTextLogin", R.string.PasswordHintTextLogin));
            }
            TextView textView3 = new TextView(context);
            this.cancelButton = textView3;
            textView3.setGravity(19);
            this.cancelButton.setTextSize(1, 15.0f);
            this.cancelButton.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.cancelButton.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            this.cancelButton.setText(LocaleController.getString((int) R.string.YourEmailSkip));
            FrameLayout bottomContainer = new FrameLayout(context);
            bottomContainer.addView(this.cancelButton, LayoutHelper.createFrame(-1, Build.VERSION.SDK_INT >= 21 ? 56 : 60, 80, 0.0f, 0.0f, 0.0f, 32.0f));
            addView(bottomContainer, LayoutHelper.createLinear(-1, -1, 80));
            VerticalPositionAutoAnimator.attach(this.cancelButton);
            this.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    LoginActivity.LoginActivityNewPasswordView.this.m3750x8d1d839c(view);
                }
            });
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView */
        public /* synthetic */ void m3748x8115ecde(View v) {
            this.isPasswordVisible = !this.isPasswordVisible;
            int i = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (i >= editTextBoldCursorArr.length) {
                    break;
                }
                int selectionStart = editTextBoldCursorArr[i].getSelectionStart();
                int selectionEnd = this.codeField[i].getSelectionEnd();
                this.codeField[i].setInputType((this.isPasswordVisible ? 144 : 128) | 1);
                this.codeField[i].setSelection(selectionStart, selectionEnd);
                i++;
            }
            this.passwordButton.setTag(Boolean.valueOf(this.isPasswordVisible));
            this.passwordButton.setColorFilter(Theme.getColor(this.isPasswordVisible ? Theme.key_windowBackgroundWhiteInputFieldActivated : Theme.key_windowBackgroundWhiteHintText));
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView */
        public /* synthetic */ boolean m3749x8719b83d(int num, TextView textView, int i, KeyEvent keyEvent) {
            if (num == 0) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (editTextBoldCursorArr.length == 2) {
                    editTextBoldCursorArr[1].requestFocus();
                    return true;
                }
            }
            if (i == 5) {
                onNextPressed(null);
                return true;
            }
            return false;
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView */
        public /* synthetic */ void m3750x8d1d839c(View view) {
            if (this.currentStage == 0) {
                recoverPassword(null, null);
            } else {
                recoverPassword(this.newPassword, null);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            String str;
            OutlineTextContainerView[] outlineTextContainerViewArr;
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            int length = editTextBoldCursorArr.length;
            int i = 0;
            while (true) {
                str = Theme.key_windowBackgroundWhiteInputFieldActivated;
                if (i >= length) {
                    break;
                }
                EditTextBoldCursor editText = editTextBoldCursorArr[i];
                editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                editText.setCursorColor(Theme.getColor(str));
                i++;
            }
            for (OutlineTextContainerView outlineField : this.outlineFields) {
                outlineField.updateColor();
            }
            this.cancelButton.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            ImageView imageView = this.passwordButton;
            if (imageView != null) {
                if (!this.isPasswordVisible) {
                    str = Theme.key_windowBackgroundWhiteHintText;
                }
                imageView.setColorFilter(Theme.getColor(str));
                this.passwordButton.setBackground(Theme.createSelectorDrawable(LoginActivity.this.getThemedColor(Theme.key_listSelector), 1));
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("NewPassword", R.string.NewPassword);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle params, boolean restore) {
            if (params == null) {
                return;
            }
            int a = 0;
            while (true) {
                EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
                if (a >= editTextBoldCursorArr.length) {
                    break;
                }
                editTextBoldCursorArr[a].setText("");
                a++;
            }
            this.currentParams = params;
            this.emailCode = params.getString("emailCode");
            String string = this.currentParams.getString("password");
            this.passwordString = string;
            if (string != null) {
                SerializedData data = new SerializedData(Utilities.hexToBytes(this.passwordString));
                TLRPC.TL_account_password TLdeserialize = TLRPC.TL_account_password.TLdeserialize(data, data.readInt32(false), false);
                this.currentPassword = TLdeserialize;
                TwoStepVerificationActivity.initPasswordNewAlgo(TLdeserialize);
            }
            this.newPassword = this.currentParams.getString("new_password");
            LoginActivity.this.showKeyboard(this.codeField[0]);
            this.codeField[0].requestFocus();
        }

        private void onPasscodeError(boolean clear, int num) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            try {
                this.codeField[num].performHapticFeedback(3, 2);
            } catch (Exception e) {
            }
            AndroidUtilities.shakeView(this.codeField[num], 2.0f, 0);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String code) {
            if (this.nextPressed) {
                return;
            }
            String code2 = this.codeField[0].getText().toString();
            if (code2.length() == 0) {
                onPasscodeError(false, 0);
            } else if (this.currentStage == 0) {
                if (!code2.equals(this.codeField[1].getText().toString())) {
                    onPasscodeError(false, 1);
                    return;
                }
                Bundle params = new Bundle();
                params.putString("emailCode", this.emailCode);
                params.putString("new_password", code2);
                params.putString("password", this.passwordString);
                LoginActivity.this.setPage(10, true, params, false);
            } else {
                this.nextPressed = true;
                LoginActivity.this.needShowProgress(0);
                recoverPassword(this.newPassword, code2);
            }
        }

        private void recoverPassword(final String password, final String hint) {
            final TLRPC.TL_auth_recoverPassword req = new TLRPC.TL_auth_recoverPassword();
            req.code = this.emailCode;
            if (!TextUtils.isEmpty(password)) {
                req.flags |= 1;
                req.new_settings = new TLRPC.TL_account_passwordInputSettings();
                req.new_settings.flags |= 1;
                req.new_settings.hint = hint != null ? hint : "";
                req.new_settings.new_algo = this.currentPassword.new_algo;
            }
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityNewPasswordView.this.m3757x672b7637(password, hint, req);
                }
            });
        }

        /* renamed from: lambda$recoverPassword$9$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView */
        public /* synthetic */ void m3757x672b7637(final String password, final String hint, TLRPC.TL_auth_recoverPassword req) {
            byte[] newPasswordBytes;
            if (password != null) {
                newPasswordBytes = AndroidUtilities.getStringBytes(password);
            } else {
                newPasswordBytes = null;
            }
            RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda1
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LoginActivity.LoginActivityNewPasswordView.this.m3756x6127aad8(password, hint, tLObject, tL_error);
                }
            };
            if (this.currentPassword.new_algo instanceof TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) {
                if (password != null) {
                    TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow algo = (TLRPC.TL_passwordKdfAlgoSHA256SHA256PBKDF2HMACSHA512iter100000SHA256ModPow) this.currentPassword.new_algo;
                    req.new_settings.new_password_hash = SRPHelper.getVBytes(newPasswordBytes, algo);
                    if (req.new_settings.new_password_hash == null) {
                        TLRPC.TL_error error = new TLRPC.TL_error();
                        error.text = "ALGO_INVALID";
                        requestDelegate.run(null, error);
                    }
                }
                ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, requestDelegate, 10);
                return;
            }
            TLRPC.TL_error error2 = new TLRPC.TL_error();
            error2.text = "PASSWORD_HASH_INVALID";
            requestDelegate.run(null, error2);
        }

        /* renamed from: lambda$recoverPassword$8$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView */
        public /* synthetic */ void m3756x6127aad8(final String password, final String hint, final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityNewPasswordView.this.m3755x5b23df79(error, password, hint, response);
                }
            });
        }

        /* renamed from: lambda$recoverPassword$7$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView */
        public /* synthetic */ void m3755x5b23df79(TLRPC.TL_error error, final String password, final String hint, final TLObject response) {
            String timeString;
            if (error == null || (!"SRP_ID_INVALID".equals(error.text) && !"NEW_SALT_INVALID".equals(error.text))) {
                LoginActivity.this.needHideProgress(false);
                if (response instanceof TLRPC.auth_Authorization) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
                    builder.setPositiveButton(LocaleController.getString((int) R.string.Continue), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            LoginActivity.LoginActivityNewPasswordView.this.m3754x5520141a(response, dialogInterface, i);
                        }
                    });
                    if (TextUtils.isEmpty(password)) {
                        builder.setMessage(LocaleController.getString((int) R.string.YourPasswordReset));
                    } else {
                        builder.setMessage(LocaleController.getString((int) R.string.YourPasswordChangedSuccessText));
                    }
                    builder.setTitle(LocaleController.getString((int) R.string.TwoStepVerificationTitle));
                    Dialog dialog = LoginActivity.this.showDialog(builder.create());
                    if (dialog != null) {
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setCancelable(false);
                        return;
                    }
                    return;
                } else if (error != null) {
                    this.nextPressed = false;
                    if (!error.text.startsWith("FLOOD_WAIT")) {
                        LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), error.text);
                        return;
                    }
                    int time = Utilities.parseInt((CharSequence) error.text).intValue();
                    if (time < 60) {
                        timeString = LocaleController.formatPluralString("Seconds", time, new Object[0]);
                    } else {
                        timeString = LocaleController.formatPluralString("Minutes", time / 60, new Object[0]);
                    }
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.formatString("FloodWaitTime", R.string.FloodWaitTime, timeString));
                    return;
                } else {
                    return;
                }
            }
            TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
            ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda10
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LoginActivity.LoginActivityNewPasswordView.this.m3753x4f1c48bb(password, hint, tLObject, tL_error);
                }
            }, 8);
        }

        /* renamed from: lambda$recoverPassword$5$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView */
        public /* synthetic */ void m3753x4f1c48bb(final String password, final String hint, final TLObject response2, final TLRPC.TL_error error2) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityNewPasswordView.this.m3752x49187d5c(error2, response2, password, hint);
                }
            });
        }

        /* renamed from: lambda$recoverPassword$4$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView */
        public /* synthetic */ void m3752x49187d5c(TLRPC.TL_error error2, TLObject response2, String password, String hint) {
            if (error2 == null) {
                TLRPC.TL_account_password tL_account_password = (TLRPC.TL_account_password) response2;
                this.currentPassword = tL_account_password;
                TwoStepVerificationActivity.initPasswordNewAlgo(tL_account_password);
                recoverPassword(password, hint);
            }
        }

        /* renamed from: lambda$recoverPassword$6$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView */
        public /* synthetic */ void m3754x5520141a(TLObject response, DialogInterface dialogInterface, int i) {
            LoginActivity.this.onAuthSuccess((TLRPC.TL_auth_authorization) response);
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean force) {
            LoginActivity.this.needHideProgress(true);
            this.currentParams = null;
            this.nextPressed = false;
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityNewPasswordView$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityNewPasswordView.this.m3751x69fffe04();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* renamed from: lambda$onShow$10$org-telegram-ui-LoginActivity$LoginActivityNewPasswordView */
        public /* synthetic */ void m3751x69fffe04() {
            EditTextBoldCursor[] editTextBoldCursorArr = this.codeField;
            if (editTextBoldCursorArr != null) {
                editTextBoldCursorArr[0].requestFocus();
                EditTextBoldCursor[] editTextBoldCursorArr2 = this.codeField;
                editTextBoldCursorArr2[0].setSelection(editTextBoldCursorArr2[0].length());
                AndroidUtilities.showKeyboard(this.codeField[0]);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            if (this.currentParams != null) {
                bundle.putBundle("recoveryview_params" + this.currentStage, this.currentParams);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            Bundle bundle2 = bundle.getBundle("recoveryview_params" + this.currentStage);
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class LoginActivityRegisterView extends SlideView implements ImageUpdater.ImageUpdaterDelegate {
        private TLRPC.FileLocation avatar;
        private AnimatorSet avatarAnimation;
        private TLRPC.FileLocation avatarBig;
        private RLottieImageView avatarEditor;
        private BackupImageView avatarImage;
        private View avatarOverlay;
        private RadialProgressView avatarProgressView;
        private boolean createAfterUpload;
        private Bundle currentParams;
        private TextView descriptionTextView;
        private FrameLayout editTextContainer;
        private EditTextBoldCursor firstNameField;
        private OutlineTextContainerView firstNameOutlineView;
        private ImageUpdater imageUpdater;
        private EditTextBoldCursor lastNameField;
        private OutlineTextContainerView lastNameOutlineView;
        private String phoneHash;
        private TextView privacyView;
        private String requestPhone;
        private TextView titleTextView;
        private TextView wrongNumber;
        private boolean nextPressed = false;
        private boolean isCameraWaitAnimationAllowed = true;
        private AvatarDrawable avatarDrawable = new AvatarDrawable();
        private RLottieDrawable cameraDrawable = new RLottieDrawable(R.raw.camera, String.valueOf((int) R.raw.camera), AndroidUtilities.dp(70.0f), AndroidUtilities.dp(70.0f), false, null);
        private RLottieDrawable cameraWaitDrawable = new RLottieDrawable(R.raw.camera_wait, String.valueOf((int) R.raw.camera_wait), AndroidUtilities.dp(70.0f), AndroidUtilities.dp(70.0f), false, null);

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public /* synthetic */ void didStartUpload(boolean z) {
            ImageUpdater.ImageUpdaterDelegate.CC.$default$didStartUpload(this, z);
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public /* synthetic */ String getInitialSearchString() {
            return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public /* synthetic */ void onUploadProgressChanged(float f) {
            ImageUpdater.ImageUpdaterDelegate.CC.$default$onUploadProgressChanged(this, f);
        }

        /* loaded from: classes4.dex */
        public class LinkSpan extends ClickableSpan {
            public LinkSpan() {
                LoginActivityRegisterView.this = this$1;
            }

            @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }

            @Override // android.text.style.ClickableSpan
            public void onClick(View widget) {
                LoginActivityRegisterView.this.showTermsOfService(false);
            }
        }

        public void showTermsOfService(boolean needAccept) {
            if (LoginActivity.this.currentTermsOfService == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString("TermsOfService", R.string.TermsOfService));
            if (needAccept) {
                builder.setPositiveButton(LocaleController.getString("Accept", R.string.Accept), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda11
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivity.LoginActivityRegisterView.this.m3798x9c20b17(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Decline", R.string.Decline), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda14
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        LoginActivity.LoginActivityRegisterView.this.m3801x7a7e261a(dialogInterface, i);
                    }
                });
            } else {
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
            }
            SpannableStringBuilder text = new SpannableStringBuilder(LoginActivity.this.currentTermsOfService.text);
            MessageObject.addEntitiesToText(text, LoginActivity.this.currentTermsOfService.entities, false, false, false, false);
            builder.setMessage(text);
            LoginActivity.this.showDialog(builder.create());
        }

        /* renamed from: lambda$showTermsOfService$0$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3798x9c20b17(DialogInterface dialog, int which) {
            LoginActivity.this.currentTermsOfService.popup = false;
            onNextPressed(null);
        }

        /* renamed from: lambda$showTermsOfService$3$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3801x7a7e261a(DialogInterface dialog, int which) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            builder1.setTitle(LocaleController.getString("TermsOfService", R.string.TermsOfService));
            builder1.setMessage(LocaleController.getString("TosDecline", R.string.TosDecline));
            builder1.setPositiveButton(LocaleController.getString("SignUp", R.string.SignUp), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda12
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivityRegisterView.this.m3799x2f561418(dialogInterface, i);
                }
            });
            builder1.setNegativeButton(LocaleController.getString("Decline", R.string.Decline), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda13
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivityRegisterView.this.m3800x54ea1d19(dialogInterface, i);
                }
            });
            LoginActivity.this.showDialog(builder1.create());
        }

        /* renamed from: lambda$showTermsOfService$1$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3799x2f561418(DialogInterface dialog1, int which1) {
            LoginActivity.this.currentTermsOfService.popup = false;
            onNextPressed(null);
        }

        /* renamed from: lambda$showTermsOfService$2$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3800x54ea1d19(DialogInterface dialog12, int which12) {
            onBackPressed(true);
            LoginActivity.this.setPage(0, true, null, true);
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LoginActivityRegisterView(Context context) {
            super(context);
            LoginActivity.this = this$0;
            setOrientation(1);
            ImageUpdater imageUpdater = new ImageUpdater(false);
            this.imageUpdater = imageUpdater;
            imageUpdater.setOpenWithFrontfaceCamera(true);
            this.imageUpdater.setSearchAvailable(false);
            this.imageUpdater.setUploadAfterSelect(false);
            this.imageUpdater.parentFragment = this$0;
            this.imageUpdater.setDelegate(this);
            FrameLayout avatarContainer = new FrameLayout(context);
            addView(avatarContainer, LayoutHelper.createLinear(78, 78, 1));
            BackupImageView backupImageView = new BackupImageView(context) { // from class: org.telegram.ui.LoginActivity.LoginActivityRegisterView.1
                @Override // android.view.View
                public void invalidate() {
                    if (LoginActivityRegisterView.this.avatarOverlay != null) {
                        LoginActivityRegisterView.this.avatarOverlay.invalidate();
                    }
                    super.invalidate();
                }

                @Override // android.view.View
                public void invalidate(int l, int t, int r, int b) {
                    if (LoginActivityRegisterView.this.avatarOverlay != null) {
                        LoginActivityRegisterView.this.avatarOverlay.invalidate();
                    }
                    super.invalidate(l, t, r, b);
                }
            };
            this.avatarImage = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(64.0f));
            this.avatarDrawable.setAvatarType(13);
            this.avatarDrawable.setInfo(5L, null, null);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            avatarContainer.addView(this.avatarImage, LayoutHelper.createFrame(-1, -1.0f));
            final Paint paint = new Paint(1);
            paint.setColor(1426063360);
            View view = new View(context) { // from class: org.telegram.ui.LoginActivity.LoginActivityRegisterView.2
                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    if (LoginActivityRegisterView.this.avatarImage != null && LoginActivityRegisterView.this.avatarProgressView.getVisibility() == 0) {
                        paint.setAlpha((int) (LoginActivityRegisterView.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f * LoginActivityRegisterView.this.avatarProgressView.getAlpha()));
                        canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredWidth() / 2.0f, paint);
                    }
                }
            };
            this.avatarOverlay = view;
            avatarContainer.addView(view, LayoutHelper.createFrame(-1, -1.0f));
            this.avatarOverlay.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda17
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    LoginActivity.LoginActivityRegisterView.this.m3789x1384770(view2);
                }
            });
            RLottieImageView rLottieImageView = new RLottieImageView(context) { // from class: org.telegram.ui.LoginActivity.LoginActivityRegisterView.3
                @Override // android.view.View
                public void invalidate(int l, int t, int r, int b) {
                    super.invalidate(l, t, r, b);
                    LoginActivityRegisterView.this.avatarOverlay.invalidate();
                }

                @Override // android.view.View
                public void invalidate() {
                    super.invalidate();
                    LoginActivityRegisterView.this.avatarOverlay.invalidate();
                }
            };
            this.avatarEditor = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.avatarEditor.setEnabled(false);
            this.avatarEditor.setClickable(false);
            avatarContainer.addView(this.avatarEditor, LayoutHelper.createFrame(-1, -1.0f));
            this.avatarEditor.addOnAttachStateChangeListener(new AnonymousClass4(this$0));
            RadialProgressView radialProgressView = new RadialProgressView(context) { // from class: org.telegram.ui.LoginActivity.LoginActivityRegisterView.5
                @Override // org.telegram.ui.Components.RadialProgressView, android.view.View
                public void setAlpha(float alpha) {
                    super.setAlpha(alpha);
                    LoginActivityRegisterView.this.avatarOverlay.invalidate();
                }
            };
            this.avatarProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            avatarContainer.addView(this.avatarProgressView, LayoutHelper.createFrame(-1, -1.0f));
            showAvatarProgress(false, false);
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setText(LocaleController.getString((int) R.string.RegistrationProfileInfo));
            this.titleTextView.setTextSize(1, 18.0f);
            this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.titleTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.titleTextView.setGravity(1);
            addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 8, 12, 8, 0));
            TextView textView2 = new TextView(context);
            this.descriptionTextView = textView2;
            textView2.setText(LocaleController.getString("RegisterText2", R.string.RegisterText2));
            this.descriptionTextView.setGravity(1);
            this.descriptionTextView.setTextSize(1, 14.0f);
            this.descriptionTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.descriptionTextView, LayoutHelper.createLinear(-2, -2, 1, 8, 6, 8, 0));
            FrameLayout frameLayout = new FrameLayout(context);
            this.editTextContainer = frameLayout;
            addView(frameLayout, LayoutHelper.createLinear(-1, -2, 8.0f, 21.0f, 8.0f, 0.0f));
            OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
            this.firstNameOutlineView = outlineTextContainerView;
            outlineTextContainerView.setText(LocaleController.getString((int) R.string.FirstName));
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
            this.firstNameField = editTextBoldCursor;
            editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
            this.firstNameField.setCursorWidth(1.5f);
            this.firstNameField.setImeOptions(268435461);
            this.firstNameField.setTextSize(1, 17.0f);
            this.firstNameField.setMaxLines(1);
            this.firstNameField.setInputType(8192);
            this.firstNameField.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda19
                @Override // android.view.View.OnFocusChangeListener
                public final void onFocusChange(View view2, boolean z) {
                    LoginActivity.LoginActivityRegisterView.this.m3790x26cc5071(view2, z);
                }
            });
            this.firstNameField.setBackground(null);
            this.firstNameField.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            this.firstNameOutlineView.attachEditText(this.firstNameField);
            this.firstNameOutlineView.addView(this.firstNameField, LayoutHelper.createFrame(-1, -2, 48));
            this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda2
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView3, int i, KeyEvent keyEvent) {
                    return LoginActivity.LoginActivityRegisterView.this.m3791x4c605972(textView3, i, keyEvent);
                }
            });
            OutlineTextContainerView outlineTextContainerView2 = new OutlineTextContainerView(context);
            this.lastNameOutlineView = outlineTextContainerView2;
            outlineTextContainerView2.setText(LocaleController.getString((int) R.string.LastName));
            EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context);
            this.lastNameField = editTextBoldCursor2;
            editTextBoldCursor2.setCursorSize(AndroidUtilities.dp(20.0f));
            this.lastNameField.setCursorWidth(1.5f);
            this.lastNameField.setImeOptions(268435462);
            this.lastNameField.setTextSize(1, 17.0f);
            this.lastNameField.setMaxLines(1);
            this.lastNameField.setInputType(8192);
            this.lastNameField.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda18
                @Override // android.view.View.OnFocusChangeListener
                public final void onFocusChange(View view2, boolean z) {
                    LoginActivity.LoginActivityRegisterView.this.m3783x5951c57a(view2, z);
                }
            });
            this.lastNameField.setBackground(null);
            this.lastNameField.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            this.lastNameOutlineView.attachEditText(this.lastNameField);
            this.lastNameOutlineView.addView(this.lastNameField, LayoutHelper.createFrame(-1, -2, 48));
            this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda1
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView3, int i, KeyEvent keyEvent) {
                    return LoginActivity.LoginActivityRegisterView.this.m3784x7ee5ce7b(textView3, i, keyEvent);
                }
            });
            buildEditTextLayout(AndroidUtilities.isSmallScreen());
            TextView textView3 = new TextView(context);
            this.wrongNumber = textView3;
            textView3.setText(LocaleController.getString("CancelRegistration", R.string.CancelRegistration));
            int i = 5;
            this.wrongNumber.setGravity((LocaleController.isRTL ? 5 : 3) | 1);
            this.wrongNumber.setTextSize(1, 14.0f);
            this.wrongNumber.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.wrongNumber.setPadding(0, AndroidUtilities.dp(24.0f), 0, 0);
            this.wrongNumber.setVisibility(8);
            addView(this.wrongNumber, LayoutHelper.createLinear(-2, -2, (!LocaleController.isRTL ? 3 : i) | 48, 0, 20, 0, 0));
            this.wrongNumber.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda16
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    LoginActivity.LoginActivityRegisterView.this.m3785xa479d77c(view2);
                }
            });
            FrameLayout privacyLayout = new FrameLayout(context);
            addView(privacyLayout, LayoutHelper.createLinear(-1, -1, 83));
            TextView textView4 = new TextView(context);
            this.privacyView = textView4;
            textView4.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
            this.privacyView.setTextSize(1, AndroidUtilities.isSmallScreen() ? 13.0f : 14.0f);
            this.privacyView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            this.privacyView.setGravity(16);
            privacyLayout.addView(this.privacyView, LayoutHelper.createFrame(-2, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 83, 14.0f, 0.0f, 70.0f, 32.0f));
            VerticalPositionAutoAnimator.attach(this.privacyView);
            String str = LocaleController.getString("TermsOfServiceLogin", R.string.TermsOfServiceLogin);
            SpannableStringBuilder text = new SpannableStringBuilder(str);
            int index1 = str.indexOf(42);
            int index2 = str.lastIndexOf(42);
            if (index1 != -1 && index2 != -1 && index1 != index2) {
                text.replace(index2, index2 + 1, (CharSequence) "");
                text.replace(index1, index1 + 1, (CharSequence) "");
                text.setSpan(new LinkSpan(), index1, index2 - 1, 33);
            }
            this.privacyView.setText(text);
        }

        /* renamed from: lambda$new$7$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3789x1384770(View view) {
            this.imageUpdater.openMenu(this.avatar != null, new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.this.m3786x907c2c6d();
                }
            }, new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda15
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    LoginActivity.LoginActivityRegisterView.this.m3788xdba43e6f(dialogInterface);
                }
            });
            this.isCameraWaitAnimationAllowed = false;
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0);
            this.cameraDrawable.setCustomEndFrame(43);
            this.avatarEditor.playAnimation();
        }

        /* renamed from: lambda$new$4$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3786x907c2c6d() {
            this.avatar = null;
            this.avatarBig = null;
            showAvatarProgress(false, true);
            this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (Object) null);
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0);
            this.isCameraWaitAnimationAllowed = true;
        }

        /* renamed from: lambda$new$6$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3788xdba43e6f(DialogInterface dialog) {
            if (!this.imageUpdater.isUploadingImage()) {
                this.avatarEditor.setAnimation(this.cameraDrawable);
                this.cameraDrawable.setCustomEndFrame(86);
                this.avatarEditor.setOnAnimationEndListener(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivityRegisterView.this.m3787xb610356e();
                    }
                });
                this.avatarEditor.playAnimation();
                return;
            }
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.cameraDrawable.setCurrentFrame(0, false);
            this.isCameraWaitAnimationAllowed = true;
        }

        /* renamed from: lambda$new$5$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3787xb610356e() {
            this.isCameraWaitAnimationAllowed = true;
        }

        /* renamed from: org.telegram.ui.LoginActivity$LoginActivityRegisterView$4 */
        /* loaded from: classes4.dex */
        public class AnonymousClass4 implements View.OnAttachStateChangeListener {
            private boolean isAttached;
            final /* synthetic */ LoginActivity val$this$0;
            private long lastRun = System.currentTimeMillis();
            private Runnable cameraWaitCallback = new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$4$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.AnonymousClass4.this.m3804x86137335();
                }
            };

            AnonymousClass4(LoginActivity loginActivity) {
                LoginActivityRegisterView.this = this$1;
                this.val$this$0 = loginActivity;
            }

            /* renamed from: lambda$$2$org-telegram-ui-LoginActivity$LoginActivityRegisterView$4 */
            public /* synthetic */ void m3804x86137335() {
                if (this.isAttached) {
                    if (LoginActivityRegisterView.this.isCameraWaitAnimationAllowed && System.currentTimeMillis() - this.lastRun >= 10000) {
                        LoginActivityRegisterView.this.avatarEditor.setAnimation(LoginActivityRegisterView.this.cameraWaitDrawable);
                        LoginActivityRegisterView.this.cameraWaitDrawable.setCurrentFrame(0, false);
                        LoginActivityRegisterView.this.cameraWaitDrawable.setOnAnimationEndListener(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$4$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                LoginActivity.LoginActivityRegisterView.AnonymousClass4.this.m3803x755da674();
                            }
                        });
                        LoginActivityRegisterView.this.avatarEditor.playAnimation();
                        this.lastRun = System.currentTimeMillis();
                    }
                    LoginActivityRegisterView.this.avatarEditor.postDelayed(this.cameraWaitCallback, 1000L);
                }
            }

            /* renamed from: lambda$$1$org-telegram-ui-LoginActivity$LoginActivityRegisterView$4 */
            public /* synthetic */ void m3803x755da674() {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$4$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivityRegisterView.AnonymousClass4.this.m3802x64a7d9b3();
                    }
                });
            }

            /* renamed from: lambda$$0$org-telegram-ui-LoginActivity$LoginActivityRegisterView$4 */
            public /* synthetic */ void m3802x64a7d9b3() {
                LoginActivityRegisterView.this.cameraDrawable.setCurrentFrame(0, false);
                LoginActivityRegisterView.this.avatarEditor.setAnimation(LoginActivityRegisterView.this.cameraDrawable);
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View v) {
                this.isAttached = true;
                v.post(this.cameraWaitCallback);
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View v) {
                this.isAttached = false;
                v.removeCallbacks(this.cameraWaitCallback);
            }
        }

        /* renamed from: lambda$new$8$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3790x26cc5071(View v, boolean hasFocus) {
            this.firstNameOutlineView.animateSelection(hasFocus ? 1.0f : 0.0f);
        }

        /* renamed from: lambda$new$9$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ boolean m3791x4c605972(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5) {
                this.lastNameField.requestFocus();
                return true;
            }
            return false;
        }

        /* renamed from: lambda$new$10$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3783x5951c57a(View v, boolean hasFocus) {
            this.lastNameOutlineView.animateSelection(hasFocus ? 1.0f : 0.0f);
        }

        /* renamed from: lambda$new$11$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ boolean m3784x7ee5ce7b(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 6 || i == 5) {
                onNextPressed(null);
                return true;
            }
            return false;
        }

        /* renamed from: lambda$new$12$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3785xa479d77c(View view) {
            if (LoginActivity.this.radialProgressView.getTag() != null) {
                return;
            }
            onBackPressed(false);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            this.avatarDrawable.invalidateSelf();
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.descriptionTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.firstNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.firstNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated));
            this.lastNameField.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.lastNameField.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteInputFieldActivated));
            this.wrongNumber.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            this.privacyView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.privacyView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
            this.firstNameOutlineView.updateColor();
            this.lastNameOutlineView.updateColor();
        }

        private void buildEditTextLayout(boolean small) {
            boolean firstHasFocus = this.firstNameField.hasFocus();
            boolean lastHasFocus = this.lastNameField.hasFocus();
            this.editTextContainer.removeAllViews();
            if (small) {
                LinearLayout linearLayout = new LinearLayout(LoginActivity.this.getParentActivity());
                linearLayout.setOrientation(0);
                this.firstNameOutlineView.setText(LocaleController.getString((int) R.string.FirstNameSmall));
                this.lastNameOutlineView.setText(LocaleController.getString((int) R.string.LastNameSmall));
                linearLayout.addView(this.firstNameOutlineView, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 8, 0));
                linearLayout.addView(this.lastNameOutlineView, LayoutHelper.createLinear(0, -2, 1.0f, 8, 0, 0, 0));
                this.editTextContainer.addView(linearLayout);
                if (firstHasFocus) {
                    this.firstNameField.requestFocus();
                    AndroidUtilities.showKeyboard(this.firstNameField);
                    return;
                } else if (lastHasFocus) {
                    this.lastNameField.requestFocus();
                    AndroidUtilities.showKeyboard(this.lastNameField);
                    return;
                } else {
                    return;
                }
            }
            this.firstNameOutlineView.setText(LocaleController.getString((int) R.string.FirstName));
            this.lastNameOutlineView.setText(LocaleController.getString((int) R.string.LastName));
            this.editTextContainer.addView(this.firstNameOutlineView, LayoutHelper.createFrame(-1, -2.0f, 48, 8.0f, 0.0f, 8.0f, 0.0f));
            this.editTextContainer.addView(this.lastNameOutlineView, LayoutHelper.createFrame(-1, -2.0f, 48, 8.0f, 82.0f, 8.0f, 0.0f));
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public void didUploadPhoto(TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, String videoPath, final TLRPC.PhotoSize bigSize, final TLRPC.PhotoSize smallSize) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.this.m3782x70a81f11(smallSize, bigSize);
                }
            });
        }

        /* renamed from: lambda$didUploadPhoto$13$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3782x70a81f11(TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
            this.avatar = smallSize.location;
            this.avatarBig = bigSize.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", this.avatarDrawable, (Object) null);
        }

        private void showAvatarProgress(final boolean show, boolean animated) {
            if (this.avatarEditor == null) {
                return;
            }
            AnimatorSet animatorSet = this.avatarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.avatarAnimation = null;
            }
            if (animated) {
                this.avatarAnimation = new AnimatorSet();
                if (show) {
                    this.avatarProgressView.setVisibility(0);
                    this.avatarAnimation.playTogether(ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 1.0f));
                } else {
                    this.avatarEditor.setVisibility(0);
                    this.avatarAnimation.playTogether(ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 0.0f));
                }
                this.avatarAnimation.setDuration(180L);
                this.avatarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.LoginActivityRegisterView.6
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (LoginActivityRegisterView.this.avatarAnimation == null || LoginActivityRegisterView.this.avatarEditor == null) {
                            return;
                        }
                        if (show) {
                            LoginActivityRegisterView.this.avatarEditor.setVisibility(4);
                        } else {
                            LoginActivityRegisterView.this.avatarProgressView.setVisibility(4);
                        }
                        LoginActivityRegisterView.this.avatarAnimation = null;
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animation) {
                        LoginActivityRegisterView.this.avatarAnimation = null;
                    }
                });
                this.avatarAnimation.start();
            } else if (show) {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(4);
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
            } else {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(0);
                this.avatarProgressView.setAlpha(0.0f);
                this.avatarProgressView.setVisibility(4);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean onBackPressed(boolean force) {
            if (force) {
                LoginActivity.this.needHideProgress(true);
                this.nextPressed = false;
                this.currentParams = null;
                return true;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString((int) R.string.Warning));
            builder.setMessage(LocaleController.getString("AreYouSureRegistration", R.string.AreYouSureRegistration));
            builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    LoginActivity.LoginActivityRegisterView.this.m3792x8c6b3f82(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("Continue", R.string.Continue), null);
            LoginActivity.this.showDialog(builder.create());
            return false;
        }

        /* renamed from: lambda$onBackPressed$14$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3792x8c6b3f82(DialogInterface dialogInterface, int i) {
            onBackPressed(true);
            LoginActivity.this.setPage(0, true, null, true);
            hidePrivacyView();
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("YourName", R.string.YourName);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onCancelPressed() {
            this.nextPressed = false;
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onShow() {
            super.onShow();
            if (this.privacyView != null) {
                if (LoginActivity.this.restoringState) {
                    this.privacyView.setAlpha(1.0f);
                } else {
                    this.privacyView.setAlpha(0.0f);
                    this.privacyView.animate().alpha(1.0f).setDuration(200L).setStartDelay(300L).setInterpolator(AndroidUtilities.decelerateInterpolator).start();
                }
            }
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                AndroidUtilities.showKeyboard(this.firstNameField);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.this.m3797x3fa2f09();
                }
            }, LoginActivity.SHOW_DELAY);
        }

        /* renamed from: lambda$onShow$15$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3797x3fa2f09() {
            EditTextBoldCursor editTextBoldCursor = this.firstNameField;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.requestFocus();
                EditTextBoldCursor editTextBoldCursor2 = this.firstNameField;
                editTextBoldCursor2.setSelection(editTextBoldCursor2.length());
                AndroidUtilities.showKeyboard(this.firstNameField);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle params, boolean restore) {
            if (params == null) {
                return;
            }
            this.firstNameField.setText("");
            this.lastNameField.setText("");
            this.requestPhone = params.getString("phoneFormated");
            this.phoneHash = params.getString("phoneHash");
            this.currentParams = params;
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onNextPressed(String code) {
            if (!this.nextPressed) {
                if (LoginActivity.this.currentTermsOfService != null && LoginActivity.this.currentTermsOfService.popup) {
                    showTermsOfService(true);
                } else if (this.firstNameField.length() == 0) {
                    LoginActivity.this.onFieldError(this.firstNameOutlineView, true);
                } else {
                    this.nextPressed = true;
                    TLRPC.TL_auth_signUp req = new TLRPC.TL_auth_signUp();
                    req.phone_code_hash = this.phoneHash;
                    req.phone_number = this.requestPhone;
                    req.first_name = this.firstNameField.getText().toString();
                    req.last_name = this.lastNameField.getText().toString();
                    LoginActivity.this.needShowProgress(0);
                    ConnectionsManager.getInstance(LoginActivity.this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda10
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            LoginActivity.LoginActivityRegisterView.this.m3796x46ed3f93(tLObject, tL_error);
                        }
                    }, 10);
                }
            }
        }

        /* renamed from: lambda$onNextPressed$19$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3796x46ed3f93(final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.this.m3795x21593692(response, error);
                }
            });
        }

        /* renamed from: lambda$onNextPressed$18$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3795x21593692(final TLObject response, TLRPC.TL_error error) {
            this.nextPressed = false;
            if (!(response instanceof TLRPC.TL_auth_authorization)) {
                LoginActivity.this.needHideProgress(false);
                if (error.text.contains("PHONE_NUMBER_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidPhoneNumber", R.string.InvalidPhoneNumber));
                    return;
                } else if (error.text.contains("PHONE_CODE_EMPTY") || error.text.contains("PHONE_CODE_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidCode", R.string.InvalidCode));
                    return;
                } else if (error.text.contains("PHONE_CODE_EXPIRED")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("CodeExpired", R.string.CodeExpired));
                    return;
                } else if (error.text.contains("FIRSTNAME_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidFirstName", R.string.InvalidFirstName));
                    return;
                } else if (error.text.contains("LASTNAME_INVALID")) {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("InvalidLastName", R.string.InvalidLastName));
                    return;
                } else {
                    LoginActivity.this.needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), error.text);
                    return;
                }
            }
            hidePrivacyView();
            LoginActivity.this.showDoneButton(false, true);
            postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    LoginActivity.LoginActivityRegisterView.this.m3794xfbc52d91(response);
                }
            }, 150L);
        }

        /* renamed from: lambda$onNextPressed$17$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3794xfbc52d91(TLObject response) {
            LoginActivity.this.needHideProgress(false, false);
            AndroidUtilities.hideKeyboard(LoginActivity.this.fragmentView.findFocus());
            LoginActivity.this.onAuthSuccess((TLRPC.TL_auth_authorization) response, true);
            if (this.avatarBig != null) {
                final TLRPC.FileLocation avatar = this.avatarBig;
                Utilities.cacheClearQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityRegisterView$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        LoginActivity.LoginActivityRegisterView.this.m3793xd6312490(avatar);
                    }
                });
            }
        }

        /* renamed from: lambda$onNextPressed$16$org-telegram-ui-LoginActivity$LoginActivityRegisterView */
        public /* synthetic */ void m3793xd6312490(TLRPC.FileLocation avatar) {
            MessagesController.getInstance(LoginActivity.this.currentAccount).uploadAndApplyUserAvatar(avatar);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void saveStateParams(Bundle bundle) {
            String first = this.firstNameField.getText().toString();
            if (first.length() != 0) {
                bundle.putString("registerview_first", first);
            }
            String last = this.lastNameField.getText().toString();
            if (last.length() != 0) {
                bundle.putString("registerview_last", last);
            }
            if (LoginActivity.this.currentTermsOfService != null) {
                SerializedData data = new SerializedData(LoginActivity.this.currentTermsOfService.getObjectSize());
                LoginActivity.this.currentTermsOfService.serializeToStream(data);
                String str = Base64.encodeToString(data.toByteArray(), 0);
                bundle.putString("terms", str);
                data.cleanup();
            }
            Bundle bundle2 = this.currentParams;
            if (bundle2 != null) {
                bundle.putBundle("registerview_params", bundle2);
            }
        }

        @Override // org.telegram.ui.Components.SlideView
        public void restoreStateParams(Bundle bundle) {
            byte[] arr;
            Bundle bundle2 = bundle.getBundle("registerview_params");
            this.currentParams = bundle2;
            if (bundle2 != null) {
                setParams(bundle2, true);
            }
            try {
                String terms = bundle.getString("terms");
                if (terms != null && (arr = Base64.decode(terms, 0)) != null) {
                    SerializedData data = new SerializedData(arr);
                    LoginActivity.this.currentTermsOfService = TLRPC.TL_help_termsOfService.TLdeserialize(data, data.readInt32(false), false);
                    data.cleanup();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            String first = bundle.getString("registerview_first");
            if (first != null) {
                this.firstNameField.setText(first);
            }
            String last = bundle.getString("registerview_last");
            if (last != null) {
                this.lastNameField.setText(last);
            }
        }

        private void hidePrivacyView() {
            this.privacyView.animate().alpha(0.0f).setDuration(150L).setStartDelay(0L).setInterpolator(AndroidUtilities.accelerateInterpolator).start();
        }
    }

    public boolean showKeyboard(View editText) {
        if (!isCustomKeyboardVisible()) {
            return AndroidUtilities.showKeyboard(editText);
        }
        return true;
    }

    public LoginActivity setIntroView(View intro, TextView startButton) {
        this.introView = intro;
        this.startMessagingButton = startButton;
        this.isAnimatingIntro = true;
        return this;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public AnimatorSet onCustomTransitionAnimation(boolean isOpen, final Runnable callback) {
        if (isOpen && this.introView != null) {
            final TransformableLoginButtonView transformButton = new TransformableLoginButtonView(this.fragmentView.getContext());
            transformButton.setButtonText(this.startMessagingButton.getPaint(), this.startMessagingButton.getText().toString());
            final int oldTransformWidth = this.startMessagingButton.getWidth();
            final int oldTransformHeight = this.startMessagingButton.getHeight();
            final int newTransformSize = this.floatingButtonIcon.getLayoutParams().width;
            final ViewGroup.MarginLayoutParams transformParams = new FrameLayout.LayoutParams(oldTransformWidth, oldTransformHeight);
            transformButton.setLayoutParams(transformParams);
            int[] loc = new int[2];
            this.fragmentView.getLocationInWindow(loc);
            int fragmentX = loc[0];
            int fragmentY = loc[1];
            this.startMessagingButton.getLocationInWindow(loc);
            final float fromX = loc[0] - fragmentX;
            final float fromY = loc[1] - fragmentY;
            transformButton.setTranslationX(fromX);
            transformButton.setTranslationY(fromY);
            final int toX = (((getParentLayout().getWidth() - this.floatingButtonIcon.getLayoutParams().width) - ((ViewGroup.MarginLayoutParams) this.floatingButtonContainer.getLayoutParams()).rightMargin) - getParentLayout().getPaddingLeft()) - getParentLayout().getPaddingRight();
            final int toY = ((((getParentLayout().getHeight() - this.floatingButtonIcon.getLayoutParams().height) - ((ViewGroup.MarginLayoutParams) this.floatingButtonContainer.getLayoutParams()).bottomMargin) - (isCustomKeyboardVisible() ? AndroidUtilities.dp(230.0f) : 0)) - getParentLayout().getPaddingTop()) - getParentLayout().getPaddingBottom();
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.12
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    LoginActivity.this.floatingButtonContainer.setVisibility(4);
                    LoginActivity.this.keyboardLinearLayout.setAlpha(0.0f);
                    LoginActivity.this.fragmentView.setBackgroundColor(0);
                    LoginActivity.this.startMessagingButton.setVisibility(4);
                    FrameLayout frameLayout = (FrameLayout) LoginActivity.this.fragmentView;
                    frameLayout.addView(transformButton);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    LoginActivity.this.keyboardLinearLayout.setAlpha(1.0f);
                    LoginActivity.this.startMessagingButton.setVisibility(0);
                    LoginActivity.this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    LoginActivity.this.floatingButtonContainer.setVisibility(0);
                    FrameLayout frameLayout = (FrameLayout) LoginActivity.this.fragmentView;
                    frameLayout.removeView(transformButton);
                    if (LoginActivity.this.animationFinishCallback != null) {
                        AndroidUtilities.runOnUIThread(LoginActivity.this.animationFinishCallback);
                        LoginActivity.this.animationFinishCallback = null;
                    }
                    LoginActivity.this.isAnimatingIntro = false;
                    callback.run();
                }
            });
            final int bgColor = Theme.getColor(Theme.key_windowBackgroundWhite);
            final int initialAlpha = Color.alpha(bgColor);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda16
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LoginActivity.this.m3735x6df70806(bgColor, initialAlpha, transformParams, oldTransformWidth, newTransformSize, oldTransformHeight, transformButton, fromX, toX, fromY, toY, valueAnimator);
                }
            });
            animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(300L);
            set.playTogether(animator);
            set.start();
            return set;
        }
        return null;
    }

    /* renamed from: lambda$onCustomTransitionAnimation$17$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3735x6df70806(int bgColor, int initialAlpha, ViewGroup.MarginLayoutParams transformParams, int oldTransformWidth, int newTransformSize, int oldTransformHeight, TransformableLoginButtonView transformButton, float fromX, int toX, float fromY, int toY, ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        this.keyboardLinearLayout.setAlpha(val);
        this.fragmentView.setBackgroundColor(ColorUtils.setAlphaComponent(bgColor, (int) (initialAlpha * val)));
        float inverted = 1.0f - val;
        this.slideViewsContainer.setTranslationY(AndroidUtilities.dp(20.0f) * inverted);
        if (!isCustomKeyboardForceDisabled()) {
            CustomPhoneKeyboardView customPhoneKeyboardView = this.keyboardView;
            customPhoneKeyboardView.setTranslationY(customPhoneKeyboardView.getLayoutParams().height * inverted);
            this.floatingButtonContainer.setTranslationY(this.keyboardView.getLayoutParams().height * inverted);
        }
        this.introView.setTranslationY((-AndroidUtilities.dp(20.0f)) * val);
        float sc = (0.05f * inverted) + 0.95f;
        this.introView.setScaleX(sc);
        this.introView.setScaleY(sc);
        transformParams.width = (int) (oldTransformWidth + ((newTransformSize - oldTransformWidth) * val));
        transformParams.height = (int) (oldTransformHeight + ((newTransformSize - oldTransformHeight) * val));
        transformButton.requestLayout();
        transformButton.setProgress(val);
        transformButton.setTranslationX(fromX + ((toX - fromX) * val));
        transformButton.setTranslationY(fromY + ((toY - fromY) * val));
    }

    public void updateColors() {
        SlideView[] slideViewArr;
        Context context = getParentActivity();
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButtonContainer.setBackground(drawable);
        this.backButtonView.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.backButtonView.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
        this.radialProgressView.setProgressColor(Theme.getColor(Theme.key_chats_actionBackground));
        this.floatingButtonIcon.setColor(Theme.getColor(Theme.key_chats_actionIcon));
        this.floatingButtonIcon.setBackgroundColor(Theme.getColor(Theme.key_chats_actionBackground));
        this.floatingProgressView.setProgressColor(Theme.getColor(Theme.key_chats_actionIcon));
        for (SlideView slideView : this.views) {
            slideView.updateColors();
        }
        this.keyboardView.updateColors();
        PhoneNumberConfirmView phoneNumberConfirmView = this.phoneNumberConfirmView;
        if (phoneNumberConfirmView == null) {
            return;
        }
        phoneNumberConfirmView.updateColors();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda12
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                LoginActivity.this.updateColors();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, Theme.key_windowBackgroundWhiteBlackText, Theme.key_windowBackgroundWhiteGrayText6, Theme.key_windowBackgroundWhiteHintText, Theme.key_listSelector, Theme.key_chats_actionBackground, Theme.key_chats_actionIcon, Theme.key_windowBackgroundWhiteInputField, Theme.key_windowBackgroundWhiteInputFieldActivated, Theme.key_windowBackgroundWhiteValueText, Theme.key_dialogTextRed, Theme.key_windowBackgroundWhiteGrayText, Theme.key_checkbox, Theme.key_windowBackgroundWhiteBlueText4, Theme.key_changephoneinfo_image2, Theme.key_chats_actionPressedBackground, Theme.key_windowBackgroundWhiteRedText2, Theme.key_windowBackgroundWhiteLinkText, Theme.key_checkboxSquareUnchecked, Theme.key_checkboxSquareBackground, Theme.key_checkboxSquareCheck, Theme.key_dialogBackground, Theme.key_dialogTextGray2, Theme.key_dialogTextBlack);
    }

    public void tryResetAccount(final String requestPhone, final String phoneHash, final String phoneCode) {
        if (this.radialProgressView.getTag() != null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.getString("ResetMyAccountWarningText", R.string.ResetMyAccountWarningText));
        builder.setTitle(LocaleController.getString("ResetMyAccountWarning", R.string.ResetMyAccountWarning));
        builder.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", R.string.ResetMyAccountWarningReset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda19
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LoginActivity.this.m3746lambda$tryResetAccount$20$orgtelegramuiLoginActivity(requestPhone, phoneHash, phoneCode, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    /* renamed from: lambda$tryResetAccount$20$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3746lambda$tryResetAccount$20$orgtelegramuiLoginActivity(final String requestPhone, final String phoneHash, final String phoneCode, DialogInterface dialogInterface, int i) {
        needShowProgress(0);
        TLRPC.TL_account_deleteAccount req = new TLRPC.TL_account_deleteAccount();
        req.reason = "Forgot password";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda10
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LoginActivity.this.m3745lambda$tryResetAccount$19$orgtelegramuiLoginActivity(requestPhone, phoneHash, phoneCode, tLObject, tL_error);
            }
        }, 10);
    }

    /* renamed from: lambda$tryResetAccount$19$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3745lambda$tryResetAccount$19$orgtelegramuiLoginActivity(final String requestPhone, final String phoneHash, final String phoneCode, TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                LoginActivity.this.m3744lambda$tryResetAccount$18$orgtelegramuiLoginActivity(error, requestPhone, phoneHash, phoneCode);
            }
        });
    }

    /* renamed from: lambda$tryResetAccount$18$org-telegram-ui-LoginActivity */
    public /* synthetic */ void m3744lambda$tryResetAccount$18$orgtelegramuiLoginActivity(TLRPC.TL_error error, String requestPhone, String phoneHash, String phoneCode) {
        needHideProgress(false);
        if (error == null) {
            if (requestPhone == null || phoneHash == null || phoneCode == null) {
                setPage(0, true, null, true);
                return;
            }
            Bundle params = new Bundle();
            params.putString("phoneFormated", requestPhone);
            params.putString("phoneHash", phoneHash);
            params.putString("code", phoneCode);
            setPage(5, true, params, false);
        } else if (error.text.equals("2FA_RECENT_CONFIRM")) {
            needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), LocaleController.getString("ResetAccountCancelledAlert", R.string.ResetAccountCancelledAlert));
        } else if (error.text.startsWith("2FA_CONFIRM_WAIT_")) {
            Bundle params2 = new Bundle();
            params2.putString("phoneFormated", requestPhone);
            params2.putString("phoneHash", phoneHash);
            params2.putString("code", phoneCode);
            params2.putInt("startTime", ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
            params2.putInt("waitTime", Utilities.parseInt((CharSequence) error.text.replace("2FA_CONFIRM_WAIT_", "")).intValue());
            setPage(8, true, params2, false);
        } else {
            needShowAlert(LocaleController.getString((int) R.string.RestorePasswordNoEmailTitle), error.text);
        }
    }

    /* loaded from: classes4.dex */
    public static final class PhoneNumberConfirmView extends FrameLayout {
        private View blurredView;
        private IConfirmDialogCallback callback;
        private TextView confirmMessageView;
        private TextView confirmTextView;
        private View dimmView;
        private boolean dismissed;
        private TextView editTextView;
        private View fabContainer;
        private TransformableLoginButtonView fabTransform;
        private RadialProgressView floatingProgressView;
        private ViewGroup fragmentView;
        private TextView numberView;
        private FrameLayout popupFabContainer;
        private FrameLayout popupLayout;

        /* loaded from: classes4.dex */
        public interface IConfirmDialogCallback {
            void onConfirmPressed(PhoneNumberConfirmView phoneNumberConfirmView, TextView textView);

            void onDismiss(PhoneNumberConfirmView phoneNumberConfirmView);

            void onEditPressed(PhoneNumberConfirmView phoneNumberConfirmView, TextView textView);

            void onFabPressed(PhoneNumberConfirmView phoneNumberConfirmView, TransformableLoginButtonView transformableLoginButtonView);
        }

        private PhoneNumberConfirmView(Context context, ViewGroup fragmentView, View fabContainer, String numberText, final IConfirmDialogCallback callback) {
            super(context);
            this.fragmentView = fragmentView;
            this.fabContainer = fabContainer;
            this.callback = callback;
            View view = new View(getContext());
            this.blurredView = view;
            view.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    LoginActivity.PhoneNumberConfirmView.this.m3853x2b64fc85(view2);
                }
            });
            addView(this.blurredView, LayoutHelper.createFrame(-1, -1.0f));
            View view2 = new View(getContext());
            this.dimmView = view2;
            view2.setBackgroundColor(C.BUFFER_FLAG_ENCRYPTED);
            this.dimmView.setAlpha(0.0f);
            addView(this.dimmView, LayoutHelper.createFrame(-1, -1.0f));
            TransformableLoginButtonView transformableLoginButtonView = new TransformableLoginButtonView(getContext());
            this.fabTransform = transformableLoginButtonView;
            transformableLoginButtonView.setTransformType(1);
            this.fabTransform.setDrawBackground(false);
            FrameLayout frameLayout = new FrameLayout(context);
            this.popupFabContainer = frameLayout;
            frameLayout.addView(this.fabTransform, LayoutHelper.createFrame(-1, -1.0f));
            this.popupFabContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    LoginActivity.PhoneNumberConfirmView.this.m3854x45807b24(callback, view3);
                }
            });
            RadialProgressView radialProgressView = new RadialProgressView(context);
            this.floatingProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(22.0f));
            this.floatingProgressView.setAlpha(0.0f);
            this.floatingProgressView.setScaleX(0.1f);
            this.floatingProgressView.setScaleY(0.1f);
            this.popupFabContainer.addView(this.floatingProgressView, LayoutHelper.createFrame(-1, -1.0f));
            this.popupFabContainer.setContentDescription(LocaleController.getString((int) R.string.Done));
            addView(this.popupFabContainer, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f));
            FrameLayout frameLayout2 = new FrameLayout(context);
            this.popupLayout = frameLayout2;
            addView(frameLayout2, LayoutHelper.createFrame(-1, 140.0f, 49, 24.0f, 0.0f, 24.0f, 0.0f));
            TextView textView = new TextView(context);
            this.confirmMessageView = textView;
            textView.setText(LocaleController.getString((int) R.string.ConfirmCorrectNumber));
            this.confirmMessageView.setTextSize(1, 14.0f);
            this.confirmMessageView.setSingleLine();
            this.popupLayout.addView(this.confirmMessageView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 24.0f, 20.0f, 24.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.numberView = textView2;
            textView2.setText(numberText);
            this.numberView.setTextSize(1, 18.0f);
            this.numberView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.numberView.setSingleLine();
            this.popupLayout.addView(this.numberView, LayoutHelper.createFrame(-1, -2.0f, LocaleController.isRTL ? 5 : 3, 24.0f, 48.0f, 24.0f, 0.0f));
            int buttonPadding = AndroidUtilities.dp(16.0f);
            TextView textView3 = new TextView(context);
            this.editTextView = textView3;
            textView3.setText(LocaleController.getString((int) R.string.Edit));
            this.editTextView.setSingleLine();
            this.editTextView.setTextSize(1, 16.0f);
            this.editTextView.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_changephoneinfo_image2)));
            this.editTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    LoginActivity.PhoneNumberConfirmView.this.m3855x5f9bf9c3(callback, view3);
                }
            });
            this.editTextView.setTypeface(Typeface.DEFAULT_BOLD);
            this.editTextView.setPadding(buttonPadding, buttonPadding / 2, buttonPadding, buttonPadding / 2);
            this.popupLayout.addView(this.editTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 80, 8, 8, 8, 8));
            TextView textView4 = new TextView(context);
            this.confirmTextView = textView4;
            textView4.setText(LocaleController.getString((int) R.string.CheckPhoneNumberYes));
            this.confirmTextView.setSingleLine();
            this.confirmTextView.setTextSize(1, 16.0f);
            this.confirmTextView.setBackground(Theme.getRoundRectSelectorDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_changephoneinfo_image2)));
            this.confirmTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    LoginActivity.PhoneNumberConfirmView.this.m3856x79b77862(callback, view3);
                }
            });
            this.confirmTextView.setTypeface(Typeface.DEFAULT_BOLD);
            this.confirmTextView.setPadding(buttonPadding, buttonPadding / 2, buttonPadding, buttonPadding / 2);
            this.popupLayout.addView(this.confirmTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 80, 8, 8, 8, 8));
            updateFabPosition();
            updateColors();
        }

        /* renamed from: lambda$new$0$org-telegram-ui-LoginActivity$PhoneNumberConfirmView */
        public /* synthetic */ void m3853x2b64fc85(View v) {
            dismiss();
        }

        /* renamed from: lambda$new$1$org-telegram-ui-LoginActivity$PhoneNumberConfirmView */
        public /* synthetic */ void m3854x45807b24(IConfirmDialogCallback callback, View v) {
            callback.onFabPressed(this, this.fabTransform);
        }

        /* renamed from: lambda$new$2$org-telegram-ui-LoginActivity$PhoneNumberConfirmView */
        public /* synthetic */ void m3855x5f9bf9c3(IConfirmDialogCallback callback, View v) {
            callback.onEditPressed(this, this.editTextView);
        }

        /* renamed from: lambda$new$3$org-telegram-ui-LoginActivity$PhoneNumberConfirmView */
        public /* synthetic */ void m3856x79b77862(IConfirmDialogCallback callback, View v) {
            callback.onConfirmPressed(this, this.confirmTextView);
        }

        public void updateFabPosition() {
            int[] loc = new int[2];
            this.fragmentView.getLocationInWindow(loc);
            int fragmentX = loc[0];
            int fragmentY = loc[1];
            this.fabContainer.getLocationInWindow(loc);
            this.popupFabContainer.setTranslationX(loc[0] - fragmentX);
            this.popupFabContainer.setTranslationY(loc[1] - fragmentY);
            requestLayout();
        }

        public void updateColors() {
            this.fabTransform.setColor(Theme.getColor(Theme.key_chats_actionIcon));
            this.fabTransform.setBackgroundColor(Theme.getColor(Theme.key_chats_actionBackground));
            this.popupLayout.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(12.0f), Theme.getColor(Theme.key_dialogBackground)));
            this.confirmMessageView.setTextColor(Theme.getColor(Theme.key_dialogTextGray2));
            this.numberView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            this.editTextView.setTextColor(Theme.getColor(Theme.key_changephoneinfo_image2));
            this.confirmTextView.setTextColor(Theme.getColor(Theme.key_changephoneinfo_image2));
            this.popupFabContainer.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground)));
            this.floatingProgressView.setProgressColor(Theme.getColor(Theme.key_chats_actionIcon));
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            int height = this.popupLayout.getMeasuredHeight();
            int popupBottom = (int) (this.popupFabContainer.getTranslationY() - AndroidUtilities.dp(32.0f));
            FrameLayout frameLayout = this.popupLayout;
            frameLayout.layout(frameLayout.getLeft(), popupBottom - height, this.popupLayout.getRight(), popupBottom);
        }

        public void show() {
            if (Build.VERSION.SDK_INT >= 21) {
                ObjectAnimator.ofFloat(this.fabContainer, View.TRANSLATION_Z, this.fabContainer.getTranslationZ(), 0.0f).setDuration(150L).start();
            }
            ValueAnimator anim = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(250L);
            anim.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.PhoneNumberConfirmView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    PhoneNumberConfirmView.this.fabContainer.setVisibility(8);
                    int w = (int) (PhoneNumberConfirmView.this.fragmentView.getMeasuredWidth() / 10.0f);
                    int h = (int) (PhoneNumberConfirmView.this.fragmentView.getMeasuredHeight() / 10.0f);
                    Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.scale(1.0f / 10.0f, 1.0f / 10.0f);
                    PhoneNumberConfirmView.this.fragmentView.draw(canvas);
                    Utilities.stackBlurBitmap(bitmap, Math.max(8, Math.max(w, h) / 150));
                    PhoneNumberConfirmView.this.blurredView.setBackground(new BitmapDrawable(PhoneNumberConfirmView.this.getContext().getResources(), bitmap));
                    PhoneNumberConfirmView.this.blurredView.setAlpha(0.0f);
                    PhoneNumberConfirmView.this.blurredView.setVisibility(0);
                    PhoneNumberConfirmView.this.fragmentView.addView(PhoneNumberConfirmView.this);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (AndroidUtilities.isAccessibilityTouchExplorationEnabled()) {
                        PhoneNumberConfirmView.this.popupFabContainer.requestFocus();
                    }
                }
            });
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LoginActivity.PhoneNumberConfirmView.this.m3857x5a9042aa(valueAnimator);
                }
            });
            anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
            anim.start();
        }

        /* renamed from: lambda$show$4$org-telegram-ui-LoginActivity$PhoneNumberConfirmView */
        public /* synthetic */ void m3857x5a9042aa(ValueAnimator animation) {
            float val = ((Float) animation.getAnimatedValue()).floatValue();
            this.fabTransform.setProgress(val);
            this.blurredView.setAlpha(val);
            this.dimmView.setAlpha(val);
            this.popupLayout.setAlpha(val);
            float scale = (val * 0.5f) + 0.5f;
            this.popupLayout.setScaleX(scale);
            this.popupLayout.setScaleY(scale);
        }

        public void animateProgress(final Runnable callback) {
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.PhoneNumberConfirmView.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    callback.run();
                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LoginActivity.PhoneNumberConfirmView.this.m3851xdd7ddbd2(valueAnimator);
                }
            });
            animator.setDuration(150L);
            animator.start();
        }

        /* renamed from: lambda$animateProgress$5$org-telegram-ui-LoginActivity$PhoneNumberConfirmView */
        public /* synthetic */ void m3851xdd7ddbd2(ValueAnimator animation) {
            float val = ((Float) animation.getAnimatedValue()).floatValue();
            float scale = ((1.0f - val) * 0.9f) + 0.1f;
            this.fabTransform.setScaleX(scale);
            this.fabTransform.setScaleY(scale);
            this.fabTransform.setAlpha(1.0f - val);
            float scale2 = (0.9f * val) + 0.1f;
            this.floatingProgressView.setScaleX(scale2);
            this.floatingProgressView.setScaleY(scale2);
            this.floatingProgressView.setAlpha(val);
        }

        public void dismiss() {
            if (this.dismissed) {
                return;
            }
            this.dismissed = true;
            this.callback.onDismiss(this);
            ValueAnimator anim = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(250L);
            anim.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LoginActivity.PhoneNumberConfirmView.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (PhoneNumberConfirmView.this.getParent() instanceof ViewGroup) {
                        ((ViewGroup) PhoneNumberConfirmView.this.getParent()).removeView(PhoneNumberConfirmView.this);
                    }
                    if (Build.VERSION.SDK_INT >= 21) {
                        ObjectAnimator.ofFloat(PhoneNumberConfirmView.this.fabContainer, View.TRANSLATION_Z, 0.0f, AndroidUtilities.dp(2.0f)).setDuration(150L).start();
                    }
                    PhoneNumberConfirmView.this.fabContainer.setVisibility(0);
                }
            });
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LoginActivity$PhoneNumberConfirmView$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LoginActivity.PhoneNumberConfirmView.this.m3852x774ce595(valueAnimator);
                }
            });
            anim.setInterpolator(CubicBezierInterpolator.DEFAULT);
            anim.start();
        }

        /* renamed from: lambda$dismiss$6$org-telegram-ui-LoginActivity$PhoneNumberConfirmView */
        public /* synthetic */ void m3852x774ce595(ValueAnimator animation) {
            float val = ((Float) animation.getAnimatedValue()).floatValue();
            this.blurredView.setAlpha(val);
            this.dimmView.setAlpha(val);
            this.fabTransform.setProgress(val);
            this.popupLayout.setAlpha(val);
            float scale = (val * 0.5f) + 0.5f;
            this.popupLayout.setScaleX(scale);
            this.popupLayout.setScaleY(scale);
        }
    }

    /* loaded from: classes4.dex */
    public static final class PhoneInputData {
        private CountrySelectActivity.Country country;
        private List<String> patterns;
        private String phoneNumber;

        private PhoneInputData() {
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }
}
