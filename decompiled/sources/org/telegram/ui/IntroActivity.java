package org.telegram.ui;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.os.Looper;
import android.os.Parcelable;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.EmuDetector;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.Intro;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Components.BottomPagesView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.IntroActivity;
/* loaded from: classes4.dex */
public class IntroActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int ICON_HEIGHT_DP = 150;
    private static final int ICON_WIDTH_DP = 200;
    private BottomPagesView bottomPages;
    private long currentDate;
    private int currentViewPagerPage;
    private RLottieDrawable darkThemeDrawable;
    private boolean destroyed;
    private boolean dragging;
    private EGLThread eglThread;
    private FrameLayout frameContainerView;
    private FrameLayout frameLayout2;
    private boolean isOnLogout;
    private boolean justEndDragging;
    private LocaleController.LocaleInfo localeInfo;
    private String[] messages;
    private int startDragX;
    private TextView startMessagingButton;
    private TextView switchLanguageTextView;
    private String[] titles;
    private ViewPager viewPager;
    private final Object pagerHeaderTag = new Object();
    private final Object pagerMessageTag = new Object();
    private int currentAccount = UserConfig.selectedAccount;
    private int lastPage = 0;
    private boolean justCreated = false;
    private boolean startPressed = false;

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", System.currentTimeMillis()).apply();
        this.titles = new String[]{LocaleController.getString("Page1Title", R.string.Page1Title), LocaleController.getString("Page2Title", R.string.Page2Title), LocaleController.getString("Page3Title", R.string.Page3Title), LocaleController.getString("Page5Title", R.string.Page5Title), LocaleController.getString("Page4Title", R.string.Page4Title), LocaleController.getString("Page6Title", R.string.Page6Title)};
        this.messages = new String[]{LocaleController.getString("Page1Message", R.string.Page1Message), LocaleController.getString("Page2Message", R.string.Page2Message), LocaleController.getString("Page3Message", R.string.Page3Message), LocaleController.getString("Page5Message", R.string.Page5Message), LocaleController.getString("Page4Message", R.string.Page4Message), LocaleController.getString("Page6Message", R.string.Page6Message)};
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setAddToContainer(false);
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        final RLottieImageView themeIconView = new RLottieImageView(context);
        final FrameLayout themeFrameLayout = new FrameLayout(context);
        themeFrameLayout.addView(themeIconView, LayoutHelper.createFrame(28, 28, 17));
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.IntroActivity.1
            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int oneFourth = (bottom - top) / 4;
                int y = ((oneFourth * 3) - AndroidUtilities.dp(275.0f)) / 2;
                int i = 0;
                IntroActivity.this.frameLayout2.layout(0, y, IntroActivity.this.frameLayout2.getMeasuredWidth(), IntroActivity.this.frameLayout2.getMeasuredHeight() + y);
                int y2 = y + AndroidUtilities.dp(150.0f) + AndroidUtilities.dp(122.0f);
                int x = (getMeasuredWidth() - IntroActivity.this.bottomPages.getMeasuredWidth()) / 2;
                IntroActivity.this.bottomPages.layout(x, y2, IntroActivity.this.bottomPages.getMeasuredWidth() + x, IntroActivity.this.bottomPages.getMeasuredHeight() + y2);
                IntroActivity.this.viewPager.layout(0, 0, IntroActivity.this.viewPager.getMeasuredWidth(), IntroActivity.this.viewPager.getMeasuredHeight());
                int y3 = (oneFourth * 3) + ((oneFourth - IntroActivity.this.startMessagingButton.getMeasuredHeight()) / 2);
                int x2 = (getMeasuredWidth() - IntroActivity.this.startMessagingButton.getMeasuredWidth()) / 2;
                IntroActivity.this.startMessagingButton.layout(x2, y3, IntroActivity.this.startMessagingButton.getMeasuredWidth() + x2, IntroActivity.this.startMessagingButton.getMeasuredHeight() + y3);
                int y4 = y3 - AndroidUtilities.dp(30.0f);
                int x3 = (getMeasuredWidth() - IntroActivity.this.switchLanguageTextView.getMeasuredWidth()) / 2;
                IntroActivity.this.switchLanguageTextView.layout(x3, y4 - IntroActivity.this.switchLanguageTextView.getMeasuredHeight(), IntroActivity.this.switchLanguageTextView.getMeasuredWidth() + x3, y4);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) themeFrameLayout.getLayoutParams();
                int dp = AndroidUtilities.dp(r4);
                if (!AndroidUtilities.isTablet()) {
                    i = AndroidUtilities.statusBarHeight;
                }
                int newTopMargin = dp + i;
                if (marginLayoutParams.topMargin != newTopMargin) {
                    marginLayoutParams.topMargin = newTopMargin;
                    themeFrameLayout.requestLayout();
                }
            }
        };
        this.frameContainerView = frameLayout;
        scrollView.addView(frameLayout, LayoutHelper.createScroll(-1, -2, 51));
        RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.sun, String.valueOf((int) R.raw.sun), AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, null);
        this.darkThemeDrawable = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        this.darkThemeDrawable.beginApplyLayerColors();
        this.darkThemeDrawable.commitApplyLayerColors();
        this.darkThemeDrawable.setCustomEndFrame(Theme.getCurrentTheme().isDark() ? this.darkThemeDrawable.getFramesCount() - 1 : 0);
        this.darkThemeDrawable.setCurrentFrame(Theme.getCurrentTheme().isDark() ? this.darkThemeDrawable.getFramesCount() - 1 : 0, false);
        Theme.getCurrentTheme().isDark();
        themeIconView.setContentDescription(LocaleController.getString((int) R.string.AccDescrSwitchToDayTheme));
        themeIconView.setAnimation(this.darkThemeDrawable);
        themeFrameLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.IntroActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                IntroActivity.this.m3565lambda$createView$0$orgtelegramuiIntroActivity(themeIconView, view);
            }
        });
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.frameLayout2 = frameLayout2;
        this.frameContainerView.addView(frameLayout2, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 78.0f, 0.0f, 0.0f));
        TextureView textureView = new TextureView(context);
        this.frameLayout2.addView(textureView, LayoutHelper.createFrame(200, 150, 17));
        textureView.setSurfaceTextureListener(new AnonymousClass2());
        ViewPager viewPager = new ViewPager(context);
        this.viewPager = viewPager;
        viewPager.setAdapter(new IntroAdapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        this.frameContainerView.addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.IntroActivity.3
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                IntroActivity.this.bottomPages.setPageOffset(position, positionOffset);
                float width = IntroActivity.this.viewPager.getMeasuredWidth();
                if (width != 0.0f) {
                    float offset = (((position * width) + positionOffsetPixels) - (IntroActivity.this.currentViewPagerPage * width)) / width;
                    Intro.setScrollOffset(offset);
                }
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int i) {
                IntroActivity.this.currentViewPagerPage = i;
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int i) {
                if (i == 1) {
                    IntroActivity.this.dragging = true;
                    IntroActivity introActivity = IntroActivity.this;
                    introActivity.startDragX = introActivity.viewPager.getCurrentItem() * IntroActivity.this.viewPager.getMeasuredWidth();
                } else if (i == 0 || i == 2) {
                    if (IntroActivity.this.dragging) {
                        IntroActivity.this.justEndDragging = true;
                        IntroActivity.this.dragging = false;
                    }
                    if (IntroActivity.this.lastPage != IntroActivity.this.viewPager.getCurrentItem()) {
                        IntroActivity introActivity2 = IntroActivity.this;
                        introActivity2.lastPage = introActivity2.viewPager.getCurrentItem();
                    }
                }
            }
        });
        TextView textView = new TextView(context) { // from class: org.telegram.ui.IntroActivity.4
            CellFlickerDrawable cellFlickerDrawable;

            @Override // android.widget.TextView, android.view.View
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

            @Override // android.widget.TextView, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int size = View.MeasureSpec.getSize(widthMeasureSpec);
                if (size > AndroidUtilities.dp(260.0f)) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(320.0f), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
                } else {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                }
            }
        };
        this.startMessagingButton = textView;
        textView.setText(LocaleController.getString("StartMessaging", R.string.StartMessaging));
        this.startMessagingButton.setGravity(17);
        this.startMessagingButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.startMessagingButton.setTextSize(1, 15.0f);
        this.startMessagingButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.frameContainerView.addView(this.startMessagingButton, LayoutHelper.createFrame(-1, 50.0f, 81, 16.0f, 0.0f, 16.0f, 76.0f));
        this.startMessagingButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.IntroActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                IntroActivity.this.m3566lambda$createView$1$orgtelegramuiIntroActivity(view);
            }
        });
        BottomPagesView bottomPagesView = new BottomPagesView(context, this.viewPager, 6);
        this.bottomPages = bottomPagesView;
        this.frameContainerView.addView(bottomPagesView, LayoutHelper.createFrame(66, 5.0f, 49, 0.0f, 350.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.switchLanguageTextView = textView2;
        textView2.setGravity(17);
        this.switchLanguageTextView.setTextSize(1, 16.0f);
        this.frameContainerView.addView(this.switchLanguageTextView, LayoutHelper.createFrame(-2, 30.0f, 81, 0.0f, 0.0f, 0.0f, 20.0f));
        this.switchLanguageTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.IntroActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                IntroActivity.this.m3567lambda$createView$2$orgtelegramuiIntroActivity(view);
            }
        });
        this.frameContainerView.addView(themeFrameLayout, LayoutHelper.createFrame(64, 64.0f, 53, 0.0f, 4, 4, 0.0f));
        this.fragmentView = scrollView;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.suggestedLangpack);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.configLoaded);
        ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        checkContinueText();
        this.justCreated = true;
        updateColors(false);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-IntroActivity */
    public /* synthetic */ void m3565lambda$createView$0$orgtelegramuiIntroActivity(RLottieImageView themeIconView, View v) {
        Theme.ThemeInfo themeInfo;
        if (DrawerProfileCell.switchingTheme) {
            return;
        }
        DrawerProfileCell.switchingTheme = true;
        boolean toDark = !Theme.isCurrentThemeDark();
        if (toDark) {
            themeInfo = Theme.getTheme("Night");
        } else {
            themeInfo = Theme.getTheme("Blue");
        }
        Theme.selectedAutoNightType = 0;
        Theme.saveAutoNightThemeConfig();
        Theme.cancelAutoNightThemeCallbacks();
        RLottieDrawable rLottieDrawable = this.darkThemeDrawable;
        rLottieDrawable.setCustomEndFrame(toDark ? rLottieDrawable.getFramesCount() - 1 : 0);
        themeIconView.playAnimation();
        themeIconView.getLocationInWindow(pos);
        int[] pos = {pos[0] + (themeIconView.getMeasuredWidth() / 2), pos[1] + (themeIconView.getMeasuredHeight() / 2)};
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, false, pos, -1, Boolean.valueOf(toDark), themeIconView);
        themeIconView.setContentDescription(LocaleController.getString((int) R.string.AccDescrSwitchToDayTheme));
    }

    /* renamed from: org.telegram.ui.IntroActivity$2 */
    /* loaded from: classes4.dex */
    public class AnonymousClass2 implements TextureView.SurfaceTextureListener {
        AnonymousClass2() {
            IntroActivity.this = this$0;
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            if (IntroActivity.this.eglThread == null && surface != null) {
                IntroActivity.this.eglThread = new EGLThread(surface);
                IntroActivity.this.eglThread.setSurfaceTextureSize(width, height);
                IntroActivity.this.eglThread.postRunnable(new Runnable() { // from class: org.telegram.ui.IntroActivity$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        IntroActivity.AnonymousClass2.this.m3570x770c765a();
                    }
                });
                IntroActivity.this.eglThread.postRunnable(IntroActivity.this.eglThread.drawRunnable);
            }
        }

        /* renamed from: lambda$onSurfaceTextureAvailable$0$org-telegram-ui-IntroActivity$2 */
        public /* synthetic */ void m3570x770c765a() {
            float time = ((float) (System.currentTimeMillis() - IntroActivity.this.currentDate)) / 1000.0f;
            Intro.setPage(IntroActivity.this.currentViewPagerPage);
            Intro.setDate(time);
            Intro.onDrawFrame(0);
            if (IntroActivity.this.eglThread == null || !IntroActivity.this.eglThread.isAlive() || IntroActivity.this.eglThread.eglDisplay == null || IntroActivity.this.eglThread.eglSurface == null) {
                return;
            }
            try {
                IntroActivity.this.eglThread.egl10.eglSwapBuffers(IntroActivity.this.eglThread.eglDisplay, IntroActivity.this.eglThread.eglSurface);
            } catch (Exception e) {
            }
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            if (IntroActivity.this.eglThread != null) {
                IntroActivity.this.eglThread.setSurfaceTextureSize(width, height);
            }
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (IntroActivity.this.eglThread != null) {
                IntroActivity.this.eglThread.shutdown();
                IntroActivity.this.eglThread = null;
                return true;
            }
            return true;
        }

        @Override // android.view.TextureView.SurfaceTextureListener
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-IntroActivity */
    public /* synthetic */ void m3566lambda$createView$1$orgtelegramuiIntroActivity(View view) {
        if (this.startPressed) {
            return;
        }
        this.startPressed = true;
        presentFragment(new LoginActivity().setIntroView(this.frameContainerView, this.startMessagingButton), true);
        this.destroyed = true;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-IntroActivity */
    public /* synthetic */ void m3567lambda$createView$2$orgtelegramuiIntroActivity(View v) {
        if (this.startPressed || this.localeInfo == null) {
            return;
        }
        this.startPressed = true;
        AlertDialog loaderDialog = new AlertDialog(v.getContext(), 3);
        loaderDialog.setCanCancel(false);
        loaderDialog.showDelayed(1000L);
        NotificationCenter.getGlobalInstance().addObserver(new AnonymousClass5(loaderDialog), NotificationCenter.reloadInterface);
        LocaleController.getInstance().applyLanguage(this.localeInfo, true, false, this.currentAccount);
    }

    /* renamed from: org.telegram.ui.IntroActivity$5 */
    /* loaded from: classes4.dex */
    public class AnonymousClass5 implements NotificationCenter.NotificationCenterDelegate {
        final /* synthetic */ AlertDialog val$loaderDialog;

        AnonymousClass5(AlertDialog alertDialog) {
            IntroActivity.this = this$0;
            this.val$loaderDialog = alertDialog;
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.reloadInterface) {
                this.val$loaderDialog.dismiss();
                NotificationCenter.getGlobalInstance().removeObserver(this, id);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.IntroActivity$5$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        IntroActivity.AnonymousClass5.this.m3571lambda$didReceivedNotification$0$orgtelegramuiIntroActivity$5();
                    }
                }, 100L);
            }
        }

        /* renamed from: lambda$didReceivedNotification$0$org-telegram-ui-IntroActivity$5 */
        public /* synthetic */ void m3571lambda$didReceivedNotification$0$orgtelegramuiIntroActivity$5() {
            IntroActivity.this.presentFragment(new LoginActivity().setIntroView(IntroActivity.this.frameContainerView, IntroActivity.this.startMessagingButton), true);
            IntroActivity.this.destroyed = true;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        Activity activity;
        super.onResume();
        if (this.justCreated) {
            if (LocaleController.isRTL) {
                this.viewPager.setCurrentItem(6);
                this.lastPage = 6;
            } else {
                this.viewPager.setCurrentItem(0);
                this.lastPage = 0;
            }
            this.justCreated = false;
        }
        if (!AndroidUtilities.isTablet() && (activity = getParentActivity()) != null) {
            activity.setRequestedOrientation(1);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        Activity activity;
        super.onPause();
        if (!AndroidUtilities.isTablet() && (activity = getParentActivity()) != null) {
            activity.setRequestedOrientation(-1);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean hasForceLightStatusBar() {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        this.destroyed = true;
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.suggestedLangpack);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.configLoaded);
        MessagesController.getGlobalMainSettings().edit().putLong("intro_crashed_time", 0L).apply();
    }

    private void checkContinueText() {
        LocaleController.LocaleInfo englishInfo = null;
        LocaleController.LocaleInfo systemInfo = null;
        LocaleController.LocaleInfo currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo();
        String systemLang = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
        if ((systemLang == null || (systemLang.equals("en") && LocaleController.getInstance().getSystemDefaultLocale().getLanguage() != null && !LocaleController.getInstance().getSystemDefaultLocale().getLanguage().equals("en"))) && (systemLang = LocaleController.getInstance().getSystemDefaultLocale().getLanguage()) == null) {
            systemLang = "en";
        }
        String arg = systemLang.contains("-") ? systemLang.split("-")[0] : systemLang;
        String alias = LocaleController.getLocaleAlias(arg);
        for (int a = 0; a < LocaleController.getInstance().languages.size(); a++) {
            LocaleController.LocaleInfo info = LocaleController.getInstance().languages.get(a);
            if (info.shortName.equals("en")) {
                englishInfo = info;
            }
            if (info.shortName.replace("_", "-").equals(systemLang) || info.shortName.equals(arg) || info.shortName.equals(alias)) {
                systemInfo = info;
            }
            if (englishInfo != null && systemInfo != null) {
                break;
            }
        }
        if (englishInfo == null || systemInfo == null || englishInfo == systemInfo) {
            return;
        }
        TLRPC.TL_langpack_getStrings req = new TLRPC.TL_langpack_getStrings();
        if (systemInfo != currentLocaleInfo) {
            req.lang_code = systemInfo.getLangCode();
            this.localeInfo = systemInfo;
        } else {
            req.lang_code = englishInfo.getLangCode();
            this.localeInfo = englishInfo;
        }
        req.keys.add("ContinueOnThisLanguage");
        final String finalSystemLang = systemLang;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.IntroActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                IntroActivity.this.m3564lambda$checkContinueText$4$orgtelegramuiIntroActivity(finalSystemLang, tLObject, tL_error);
            }
        }, 8);
    }

    /* renamed from: lambda$checkContinueText$4$org-telegram-ui-IntroActivity */
    public /* synthetic */ void m3564lambda$checkContinueText$4$orgtelegramuiIntroActivity(final String finalSystemLang, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            if (vector.objects.isEmpty()) {
                return;
            }
            final TLRPC.LangPackString string = (TLRPC.LangPackString) vector.objects.get(0);
            if (string instanceof TLRPC.TL_langPackString) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.IntroActivity$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        IntroActivity.this.m3563lambda$checkContinueText$3$orgtelegramuiIntroActivity(string, finalSystemLang);
                    }
                });
            }
        }
    }

    /* renamed from: lambda$checkContinueText$3$org-telegram-ui-IntroActivity */
    public /* synthetic */ void m3563lambda$checkContinueText$3$orgtelegramuiIntroActivity(TLRPC.LangPackString string, String finalSystemLang) {
        if (!this.destroyed) {
            this.switchLanguageTextView.setText(string.value);
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            preferences.edit().putString("language_showed2", finalSystemLang.toLowerCase()).apply();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.suggestedLangpack || id == NotificationCenter.configLoaded) {
            checkContinueText();
        }
    }

    public IntroActivity setOnLogout() {
        this.isOnLogout = true;
        return this;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public AnimatorSet onCustomTransitionAnimation(boolean isOpen, Runnable callback) {
        if (this.isOnLogout) {
            AnimatorSet set = new AnimatorSet().setDuration(50L);
            set.playTogether(ValueAnimator.ofFloat(new float[0]));
            return set;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class IntroAdapter extends PagerAdapter {
        private IntroAdapter() {
            IntroActivity.this = r1;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return IntroActivity.this.titles.length;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(ViewGroup container, int position) {
            final TextView headerTextView = new TextView(container.getContext());
            headerTextView.setTag(IntroActivity.this.pagerHeaderTag);
            final TextView messageTextView = new TextView(container.getContext());
            messageTextView.setTag(IntroActivity.this.pagerMessageTag);
            FrameLayout frameLayout = new FrameLayout(container.getContext()) { // from class: org.telegram.ui.IntroActivity.IntroAdapter.1
                @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int oneFourth = (bottom - top) / 4;
                    int y = (((oneFourth * 3) - AndroidUtilities.dp(275.0f)) / 2) + AndroidUtilities.dp(150.0f) + AndroidUtilities.dp(16.0f);
                    int x = AndroidUtilities.dp(18.0f);
                    TextView textView = headerTextView;
                    textView.layout(x, y, textView.getMeasuredWidth() + x, headerTextView.getMeasuredHeight() + y);
                    int y2 = ((int) (y + headerTextView.getTextSize())) + AndroidUtilities.dp(16.0f);
                    int x2 = AndroidUtilities.dp(16.0f);
                    TextView textView2 = messageTextView;
                    textView2.layout(x2, y2, textView2.getMeasuredWidth() + x2, messageTextView.getMeasuredHeight() + y2);
                }
            };
            headerTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            headerTextView.setTextSize(1, 26.0f);
            headerTextView.setGravity(17);
            frameLayout.addView(headerTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 18.0f, 244.0f, 18.0f, 0.0f));
            messageTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            messageTextView.setTextSize(1, 15.0f);
            messageTextView.setGravity(17);
            frameLayout.addView(messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 16.0f, 286.0f, 16.0f, 0.0f));
            container.addView(frameLayout, 0);
            headerTextView.setText(IntroActivity.this.titles[position]);
            messageTextView.setText(AndroidUtilities.replaceTags(IntroActivity.this.messages[position]));
            return frameLayout;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            IntroActivity.this.bottomPages.setCurrentPage(position);
            IntroActivity.this.currentViewPagerPage = position;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Parcelable saveState() {
            return null;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class EGLThread extends DispatchQueue {
        private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
        private static final int EGL_OPENGL_ES2_BIT = 4;
        private EGL10 egl10;
        private EGLConfig eglConfig;
        private EGLContext eglContext;
        private EGLDisplay eglDisplay;
        private EGLSurface eglSurface;
        private boolean initied;
        private long lastDrawFrame;
        private float maxRefreshRate;
        private SurfaceTexture surfaceTexture;
        private int[] textures = new int[24];
        private GenericProvider<Void, Bitmap> telegramMaskProvider = IntroActivity$EGLThread$$ExternalSyntheticLambda2.INSTANCE;
        private Runnable drawRunnable = new Runnable() { // from class: org.telegram.ui.IntroActivity.EGLThread.1
            @Override // java.lang.Runnable
            public void run() {
                if (!EGLThread.this.initied) {
                    return;
                }
                long current = System.currentTimeMillis();
                if ((EGLThread.this.eglContext.equals(EGLThread.this.egl10.eglGetCurrentContext()) && EGLThread.this.eglSurface.equals(EGLThread.this.egl10.eglGetCurrentSurface(12377))) || EGLThread.this.egl10.eglMakeCurrent(EGLThread.this.eglDisplay, EGLThread.this.eglSurface, EGLThread.this.eglSurface, EGLThread.this.eglContext)) {
                    int deltaDrawMs = (int) Math.min(current - EGLThread.this.lastDrawFrame, 16L);
                    float time = ((float) (current - IntroActivity.this.currentDate)) / 1000.0f;
                    Intro.setPage(IntroActivity.this.currentViewPagerPage);
                    Intro.setDate(time);
                    Intro.onDrawFrame(deltaDrawMs);
                    EGLThread.this.egl10.eglSwapBuffers(EGLThread.this.eglDisplay, EGLThread.this.eglSurface);
                    EGLThread.this.lastDrawFrame = current;
                    if (EGLThread.this.maxRefreshRate == 0.0f) {
                        if (Build.VERSION.SDK_INT < 21) {
                            EGLThread.this.maxRefreshRate = 60.0f;
                        } else {
                            WindowManager wm = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
                            Display display = wm.getDefaultDisplay();
                            float[] rates = display.getSupportedRefreshRates();
                            float maxRate = 0.0f;
                            for (float rate : rates) {
                                if (rate > maxRate) {
                                    maxRate = rate;
                                }
                            }
                            EGLThread.this.maxRefreshRate = maxRate;
                        }
                    }
                    long drawMs = System.currentTimeMillis() - current;
                    EGLThread eGLThread = EGLThread.this;
                    eGLThread.postRunnable(eGLThread.drawRunnable, Math.max((1000.0f / EGLThread.this.maxRefreshRate) - drawMs, 0L));
                } else if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGLThread.this.egl10.eglGetError()));
                }
            }
        };

        public static /* synthetic */ Bitmap lambda$new$0(Void v) {
            int size = AndroidUtilities.dp(150.0f);
            Bitmap bm = Bitmap.createBitmap(AndroidUtilities.dp(200.0f), size, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            c.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            Paint paint = new Paint(1);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            c.drawCircle(bm.getWidth() / 2.0f, bm.getHeight() / 2.0f, size / 2.0f, paint);
            return bm;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public EGLThread(SurfaceTexture surface) {
            super("EGLThread");
            IntroActivity.this = this$0;
            this.surfaceTexture = surface;
        }

        private boolean initGL() {
            int[] configSpec;
            EGL10 egl10 = (EGL10) EGLContext.getEGL();
            this.egl10 = egl10;
            EGLDisplay eglGetDisplay = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            this.eglDisplay = eglGetDisplay;
            if (eglGetDisplay == EGL10.EGL_NO_DISPLAY) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglGetDisplay failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] version = new int[2];
            if (!this.egl10.eglInitialize(this.eglDisplay, version)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglInitialize failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            }
            int[] configsCount = new int[1];
            EGLConfig[] configs = new EGLConfig[1];
            if (EmuDetector.with(IntroActivity.this.getParentActivity()).detect()) {
                configSpec = new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12344};
            } else {
                int[] configSpec2 = {12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12325, 24, 12326, 0, 12338, 1, 12337, 2, 12344};
                configSpec = configSpec2;
            }
            if (!this.egl10.eglChooseConfig(this.eglDisplay, configSpec, configs, 1, configsCount)) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglChooseConfig failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                }
                finish();
                return false;
            } else if (configsCount[0] > 0) {
                EGLConfig eGLConfig = configs[0];
                this.eglConfig = eGLConfig;
                int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, 12344};
                EGLContext eglCreateContext = this.egl10.eglCreateContext(this.eglDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
                this.eglContext = eglCreateContext;
                if (eglCreateContext == null) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.e("eglCreateContext failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                    }
                    finish();
                    return false;
                }
                SurfaceTexture surfaceTexture = this.surfaceTexture;
                if (surfaceTexture instanceof SurfaceTexture) {
                    EGLSurface eglCreateWindowSurface = this.egl10.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surfaceTexture, null);
                    this.eglSurface = eglCreateWindowSurface;
                    if (eglCreateWindowSurface == null || eglCreateWindowSurface == EGL10.EGL_NO_SURFACE) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("createWindowSurface failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    }
                    EGL10 egl102 = this.egl10;
                    EGLDisplay eGLDisplay = this.eglDisplay;
                    EGLSurface eGLSurface = this.eglSurface;
                    if (!egl102.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e("eglMakeCurrent failed " + GLUtils.getEGLErrorString(this.egl10.eglGetError()));
                        }
                        finish();
                        return false;
                    }
                    GLES20.glGenTextures(23, this.textures, 0);
                    loadTexture(R.drawable.intro_fast_arrow_shadow, 0);
                    loadTexture(R.drawable.intro_fast_arrow, 1);
                    loadTexture(R.drawable.intro_fast_body, 2);
                    loadTexture(R.drawable.intro_fast_spiral, 3);
                    loadTexture(R.drawable.intro_ic_bubble_dot, 4);
                    loadTexture(R.drawable.intro_ic_bubble, 5);
                    loadTexture(R.drawable.intro_ic_cam_lens, 6);
                    loadTexture(R.drawable.intro_ic_cam, 7);
                    loadTexture(R.drawable.intro_ic_pencil, 8);
                    loadTexture(R.drawable.intro_ic_pin, 9);
                    loadTexture(R.drawable.intro_ic_smile_eye, 10);
                    loadTexture(R.drawable.intro_ic_smile, 11);
                    loadTexture(R.drawable.intro_ic_videocam, 12);
                    loadTexture(R.drawable.intro_knot_down, 13);
                    loadTexture(R.drawable.intro_knot_up, 14);
                    loadTexture(R.drawable.intro_powerful_infinity_white, 15);
                    loadTexture(R.drawable.intro_powerful_infinity, 16);
                    loadTexture(R.drawable.intro_powerful_mask, 17, Theme.getColor(Theme.key_windowBackgroundWhite), false);
                    loadTexture(R.drawable.intro_powerful_star, 18);
                    loadTexture(R.drawable.intro_private_door, 19);
                    loadTexture(R.drawable.intro_private_screw, 20);
                    loadTexture(R.drawable.intro_tg_plane, 21);
                    loadTexture(IntroActivity$EGLThread$$ExternalSyntheticLambda1.INSTANCE, 22);
                    loadTexture(this.telegramMaskProvider, 23);
                    updateTelegramTextures();
                    updatePowerfulTextures();
                    int[] iArr = this.textures;
                    Intro.setPrivateTextures(iArr[19], iArr[20]);
                    int[] iArr2 = this.textures;
                    Intro.setFreeTextures(iArr2[14], iArr2[13]);
                    int[] iArr3 = this.textures;
                    Intro.setFastTextures(iArr3[2], iArr3[3], iArr3[1], iArr3[0]);
                    int[] iArr4 = this.textures;
                    Intro.setIcTextures(iArr4[4], iArr4[5], iArr4[6], iArr4[7], iArr4[8], iArr4[9], iArr4[10], iArr4[11], iArr4[12]);
                    Intro.onSurfaceCreated();
                    IntroActivity.this.currentDate = System.currentTimeMillis() - 1000;
                    return true;
                }
                finish();
                return false;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("eglConfig not initialized");
                }
                finish();
                return false;
            }
        }

        public static /* synthetic */ Bitmap lambda$initGL$1(Void v) {
            Paint paint = new Paint(1);
            paint.setColor(-13851168);
            int size = AndroidUtilities.dp(150.0f);
            Bitmap bm = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            c.drawCircle(size / 2.0f, size / 2.0f, size / 2.0f, paint);
            return bm;
        }

        public void updateTelegramTextures() {
            int[] iArr = this.textures;
            Intro.setTelegramTextures(iArr[22], iArr[21], iArr[23]);
        }

        public void updatePowerfulTextures() {
            int[] iArr = this.textures;
            Intro.setPowerfulTextures(iArr[17], iArr[18], iArr[16], iArr[15]);
        }

        public void finish() {
            if (this.eglSurface != null) {
                this.egl10.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
                this.egl10.eglDestroySurface(this.eglDisplay, this.eglSurface);
                this.eglSurface = null;
            }
            EGLContext eGLContext = this.eglContext;
            if (eGLContext != null) {
                this.egl10.eglDestroyContext(this.eglDisplay, eGLContext);
                this.eglContext = null;
            }
            EGLDisplay eGLDisplay = this.eglDisplay;
            if (eGLDisplay != null) {
                this.egl10.eglTerminate(eGLDisplay);
                this.eglDisplay = null;
            }
        }

        private void loadTexture(GenericProvider<Void, Bitmap> bitmapProvider, int index) {
            loadTexture(bitmapProvider, index, false);
        }

        public void loadTexture(GenericProvider<Void, Bitmap> bitmapProvider, int index, boolean rebind) {
            if (rebind) {
                GLES20.glDeleteTextures(1, this.textures, index);
                GLES20.glGenTextures(1, this.textures, index);
            }
            Bitmap bm = bitmapProvider.provide(null);
            GLES20.glBindTexture(3553, this.textures[index]);
            GLES20.glTexParameteri(3553, 10241, 9729);
            GLES20.glTexParameteri(3553, 10240, 9729);
            GLES20.glTexParameteri(3553, 10242, 33071);
            GLES20.glTexParameteri(3553, 10243, 33071);
            GLUtils.texImage2D(3553, 0, bm, 0);
            bm.recycle();
        }

        private void loadTexture(int resId, int index) {
            loadTexture(resId, index, 0, false);
        }

        public void loadTexture(int resId, int index, int tintColor, boolean rebind) {
            Drawable drawable = IntroActivity.this.getParentActivity().getResources().getDrawable(resId);
            if (drawable instanceof BitmapDrawable) {
                if (rebind) {
                    GLES20.glDeleteTextures(1, this.textures, index);
                    GLES20.glGenTextures(1, this.textures, index);
                }
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                GLES20.glBindTexture(3553, this.textures[index]);
                GLES20.glTexParameteri(3553, 10241, 9729);
                GLES20.glTexParameteri(3553, 10240, 9729);
                GLES20.glTexParameteri(3553, 10242, 33071);
                GLES20.glTexParameteri(3553, 10243, 33071);
                if (tintColor != 0) {
                    Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(tempBitmap);
                    Paint tempPaint = new Paint(5);
                    tempPaint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, tempPaint);
                    GLUtils.texImage2D(3553, 0, tempBitmap, 0);
                    tempBitmap.recycle();
                    return;
                }
                GLUtils.texImage2D(3553, 0, bitmap, 0);
            }
        }

        public void shutdown() {
            postRunnable(new Runnable() { // from class: org.telegram.ui.IntroActivity$EGLThread$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    IntroActivity.EGLThread.this.m3572lambda$shutdown$2$orgtelegramuiIntroActivity$EGLThread();
                }
            });
        }

        /* renamed from: lambda$shutdown$2$org-telegram-ui-IntroActivity$EGLThread */
        public /* synthetic */ void m3572lambda$shutdown$2$orgtelegramuiIntroActivity$EGLThread() {
            finish();
            Looper looper = Looper.myLooper();
            if (looper != null) {
                looper.quit();
            }
        }

        public void setSurfaceTextureSize(int width, int height) {
            Intro.onSurfaceChanged(width, height, Math.min(width / 150.0f, height / 150.0f), 0);
        }

        @Override // org.telegram.messenger.DispatchQueue, java.lang.Thread, java.lang.Runnable
        public void run() {
            this.initied = initGL();
            super.run();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.IntroActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                IntroActivity.this.m3568lambda$getThemeDescriptions$5$orgtelegramuiIntroActivity();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhiteBlueText4, Theme.key_chats_actionBackground, Theme.key_chats_actionPressedBackground, Theme.key_featuredStickers_buttonText, Theme.key_windowBackgroundWhiteBlackText, Theme.key_windowBackgroundWhiteGrayText3);
    }

    /* renamed from: lambda$getThemeDescriptions$5$org-telegram-ui-IntroActivity */
    public /* synthetic */ void m3568lambda$getThemeDescriptions$5$orgtelegramuiIntroActivity() {
        updateColors(true);
    }

    private void updateColors(boolean fromTheme) {
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.switchLanguageTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
        this.startMessagingButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.startMessagingButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_changephoneinfo_image2), Theme.getColor(Theme.key_chats_actionPressedBackground)));
        this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_changephoneinfo_image2), PorterDuff.Mode.SRC_IN));
        this.bottomPages.invalidate();
        if (fromTheme) {
            EGLThread eGLThread = this.eglThread;
            if (eGLThread != null) {
                eGLThread.postRunnable(new Runnable() { // from class: org.telegram.ui.IntroActivity$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        IntroActivity.this.m3569lambda$updateColors$6$orgtelegramuiIntroActivity();
                    }
                });
            }
            for (int i = 0; i < this.viewPager.getChildCount(); i++) {
                View ch = this.viewPager.getChildAt(i);
                TextView headerTextView = (TextView) ch.findViewWithTag(this.pagerHeaderTag);
                headerTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                TextView messageTextView = (TextView) ch.findViewWithTag(this.pagerMessageTag);
                messageTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
            }
            return;
        }
        Intro.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
    }

    /* renamed from: lambda$updateColors$6$org-telegram-ui-IntroActivity */
    public /* synthetic */ void m3569lambda$updateColors$6$orgtelegramuiIntroActivity() {
        this.eglThread.loadTexture(R.drawable.intro_powerful_mask, 17, Theme.getColor(Theme.key_windowBackgroundWhite), true);
        this.eglThread.updatePowerfulTextures();
        EGLThread eGLThread = this.eglThread;
        eGLThread.loadTexture(eGLThread.telegramMaskProvider, 23, true);
        this.eglThread.updateTelegramTextures();
        Intro.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }
}
