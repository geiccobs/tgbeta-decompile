package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.SnowflakesEffect;
import org.telegram.ui.ThemeActivity;
/* loaded from: classes3.dex */
public class DrawerProfileCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public static boolean switchingTheme;
    private boolean accountsShown;
    private ImageView arrowView;
    private BackupImageView avatarImageView;
    private Integer currentColor;
    private Integer currentMoonColor;
    private RLottieImageView darkThemeView;
    public boolean drawPremium;
    public float drawPremiumProgress;
    PremiumGradient.GradientTools gradientTools;
    private TextView nameTextView;
    private TextView phoneTextView;
    private ImageView shadowView;
    private SnowflakesEffect snowflakesEffect;
    StarParticlesView.Drawable starParticlesDrawable;
    private Rect srcRect = new Rect();
    private Rect destRect = new Rect();
    private Paint paint = new Paint();
    private RLottieDrawable sunDrawable = new RLottieDrawable(R.raw.sun, "2131558537", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, null);

    public DrawerProfileCell(Context context, final DrawerLayoutContainer drawerLayoutContainer) {
        super(context);
        new Paint(1);
        ImageView imageView = new ImageView(context);
        this.shadowView = imageView;
        imageView.setVisibility(4);
        this.shadowView.setScaleType(ImageView.ScaleType.FIT_XY);
        this.shadowView.setImageResource(R.drawable.bottom_shadow);
        addView(this.shadowView, LayoutHelper.createFrame(-1, 70, 83));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(32.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(64, 64.0f, 83, 16.0f, 0.0f, 0.0f, 67.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setGravity(3);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 76.0f, 28.0f));
        TextView textView2 = new TextView(context);
        this.phoneTextView = textView2;
        textView2.setTextSize(1, 13.0f);
        this.phoneTextView.setLines(1);
        this.phoneTextView.setMaxLines(1);
        this.phoneTextView.setSingleLine(true);
        this.phoneTextView.setGravity(3);
        addView(this.phoneTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 16.0f, 0.0f, 76.0f, 9.0f));
        ImageView imageView2 = new ImageView(context);
        this.arrowView = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.arrowView.setImageResource(R.drawable.msg_expand);
        addView(this.arrowView, LayoutHelper.createFrame(59, 59, 85));
        setArrowState(false);
        if (Theme.isCurrentThemeDay()) {
            this.sunDrawable.setCustomEndFrame(36);
        } else {
            this.sunDrawable.setCustomEndFrame(0);
            this.sunDrawable.setCurrentFrame(36);
        }
        this.sunDrawable.setPlayInDirectionOfCustomEndFrame(true);
        RLottieImageView rLottieImageView = new RLottieImageView(this, context) { // from class: org.telegram.ui.Cells.DrawerProfileCell.1
            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                if (Theme.isCurrentThemeDark()) {
                    accessibilityNodeInfo.setText(LocaleController.getString("AccDescrSwitchToDayTheme", R.string.AccDescrSwitchToDayTheme));
                } else {
                    accessibilityNodeInfo.setText(LocaleController.getString("AccDescrSwitchToNightTheme", R.string.AccDescrSwitchToNightTheme));
                }
            }
        };
        this.darkThemeView = rLottieImageView;
        rLottieImageView.setFocusable(true);
        this.darkThemeView.setBackground(Theme.createCircleSelectorDrawable(Theme.getColor("dialogButtonSelector"), 0, 0));
        this.sunDrawable.beginApplyLayerColors();
        int color = Theme.getColor("chats_menuName");
        this.sunDrawable.setLayerColor("Sunny.**", color);
        this.sunDrawable.setLayerColor("Path 6.**", color);
        this.sunDrawable.setLayerColor("Path.**", color);
        this.sunDrawable.setLayerColor("Path 5.**", color);
        this.sunDrawable.commitApplyLayerColors();
        this.darkThemeView.setScaleType(ImageView.ScaleType.CENTER);
        this.darkThemeView.setAnimation(this.sunDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            this.darkThemeView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 1, AndroidUtilities.dp(17.0f)));
            Theme.setRippleDrawableForceSoftware((RippleDrawable) this.darkThemeView.getBackground());
        }
        this.darkThemeView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.DrawerProfileCell$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DrawerProfileCell.this.lambda$new$0(view);
            }
        });
        this.darkThemeView.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Cells.DrawerProfileCell$$ExternalSyntheticLambda1
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                boolean lambda$new$1;
                lambda$new$1 = DrawerProfileCell.lambda$new$1(DrawerLayoutContainer.this, view);
                return lambda$new$1;
            }
        });
        addView(this.darkThemeView, LayoutHelper.createFrame(48, 48.0f, 85, 0.0f, 0.0f, 6.0f, 90.0f));
        if (Theme.getEventType() == 0) {
            SnowflakesEffect snowflakesEffect = new SnowflakesEffect(0);
            this.snowflakesEffect = snowflakesEffect;
            snowflakesEffect.setColorKey("chats_menuName");
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x006f  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x007b  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x008d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$new$0(android.view.View r7) {
        /*
            r6 = this;
            boolean r7 = org.telegram.ui.Cells.DrawerProfileCell.switchingTheme
            if (r7 == 0) goto L5
            return
        L5:
            r7 = 1
            org.telegram.ui.Cells.DrawerProfileCell.switchingTheme = r7
            android.content.Context r7 = org.telegram.messenger.ApplicationLoader.applicationContext
            java.lang.String r0 = "themeconfig"
            r1 = 0
            android.content.SharedPreferences r7 = r7.getSharedPreferences(r0, r1)
            java.lang.String r0 = "lastDayTheme"
            java.lang.String r2 = "Blue"
            java.lang.String r0 = r7.getString(r0, r2)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r0)
            if (r3 == 0) goto L29
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r0)
            boolean r3 = r3.isDark()
            if (r3 == 0) goto L2a
        L29:
            r0 = r2
        L2a:
            java.lang.String r3 = "lastDarkTheme"
            java.lang.String r4 = "Dark Blue"
            java.lang.String r7 = r7.getString(r3, r4)
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
            if (r3 == 0) goto L42
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
            boolean r3 = r3.isDark()
            if (r3 != 0) goto L43
        L42:
            r7 = r4
        L43:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
            boolean r5 = r0.equals(r7)
            if (r5 == 0) goto L63
            boolean r5 = r3.isDark()
            if (r5 != 0) goto L61
            boolean r5 = r0.equals(r4)
            if (r5 != 0) goto L61
            java.lang.String r5 = "Night"
            boolean r5 = r0.equals(r5)
            if (r5 == 0) goto L64
        L61:
            r4 = r7
            goto L65
        L63:
            r4 = r7
        L64:
            r2 = r0
        L65:
            java.lang.String r7 = r3.getKey()
            boolean r7 = r2.equals(r7)
            if (r7 == 0) goto L7b
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getTheme(r4)
            org.telegram.ui.Components.RLottieDrawable r2 = r6.sunDrawable
            r3 = 36
            r2.setCustomEndFrame(r3)
            goto L84
        L7b:
            org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = org.telegram.ui.ActionBar.Theme.getTheme(r2)
            org.telegram.ui.Components.RLottieDrawable r2 = r6.sunDrawable
            r2.setCustomEndFrame(r1)
        L84:
            org.telegram.ui.Components.RLottieImageView r2 = r6.darkThemeView
            r2.playAnimation()
            int r2 = org.telegram.ui.ActionBar.Theme.selectedAutoNightType
            if (r2 == 0) goto La9
            android.content.Context r2 = r6.getContext()
            r3 = 2131624608(0x7f0e02a0, float:1.88764E38)
            java.lang.String r4 = "AutoNightModeOff"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            android.widget.Toast r2 = android.widget.Toast.makeText(r2, r3, r1)
            r2.show()
            org.telegram.ui.ActionBar.Theme.selectedAutoNightType = r1
            org.telegram.ui.ActionBar.Theme.saveAutoNightThemeConfig()
            org.telegram.ui.ActionBar.Theme.cancelAutoNightThemeCallbacks()
        La9:
            r6.switchTheme(r0, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.lambda$new$0(android.view.View):void");
    }

    public static /* synthetic */ boolean lambda$new$1(DrawerLayoutContainer drawerLayoutContainer, View view) {
        if (drawerLayoutContainer != null) {
            drawerLayoutContainer.presentFragment(new ThemeActivity(0));
            return true;
        }
        return false;
    }

    private void switchTheme(Theme.ThemeInfo themeInfo, boolean z) {
        this.darkThemeView.getLocationInWindow(r1);
        int[] iArr = {iArr[0] + (this.darkThemeView.getMeasuredWidth() / 2), iArr[1] + (this.darkThemeView.getMeasuredHeight() / 2)};
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, Boolean.FALSE, iArr, -1, Boolean.valueOf(z), this.darkThemeView);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateColors();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        if (Build.VERSION.SDK_INT >= 21) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f) + AndroidUtilities.statusBarHeight, 1073741824));
            return;
        }
        try {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), 1073741824));
        } catch (Exception e) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(148.0f));
            FileLog.e(e);
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.drawPremium) {
            if (this.starParticlesDrawable == null) {
                StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(15);
                this.starParticlesDrawable = drawable;
                drawable.init();
                StarParticlesView.Drawable drawable2 = this.starParticlesDrawable;
                drawable2.speedScale = 0.8f;
                drawable2.minLifeTime = 3000L;
            }
            this.starParticlesDrawable.rect.set(this.avatarImageView.getLeft(), this.avatarImageView.getTop(), this.avatarImageView.getRight(), this.avatarImageView.getBottom());
            this.starParticlesDrawable.rect.inset(-AndroidUtilities.dp(20.0f), -AndroidUtilities.dp(20.0f));
            this.starParticlesDrawable.resetPositions();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:68:0x01b7  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x021e  */
    /* JADX WARN: Removed duplicated region for block: B:81:? A[RETURN, SYNTHETIC] */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onDraw(android.graphics.Canvas r12) {
        /*
            Method dump skipped, instructions count: 546
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.DrawerProfileCell.onDraw(android.graphics.Canvas):void");
    }

    public boolean isInAvatar(float f, float f2) {
        return f >= ((float) this.avatarImageView.getLeft()) && f <= ((float) this.avatarImageView.getRight()) && f2 >= ((float) this.avatarImageView.getTop()) && f2 <= ((float) this.avatarImageView.getBottom());
    }

    public boolean hasAvatar() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    public void setAccountsShown(boolean z, boolean z2) {
        if (this.accountsShown == z) {
            return;
        }
        this.accountsShown = z;
        setArrowState(z2);
    }

    public void setUser(TLRPC$User tLRPC$User, boolean z) {
        if (tLRPC$User == null) {
            return;
        }
        this.accountsShown = z;
        setArrowState(false);
        CharSequence userName = UserObject.getUserName(tLRPC$User);
        try {
            userName = Emoji.replaceEmoji(userName, this.nameTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(22.0f), false);
        } catch (Exception unused) {
        }
        this.drawPremium = false;
        this.nameTextView.setText(userName);
        TextView textView = this.phoneTextView;
        PhoneFormat phoneFormat = PhoneFormat.getInstance();
        textView.setText(phoneFormat.format("+" + tLRPC$User.phone));
        AvatarDrawable avatarDrawable = new AvatarDrawable(tLRPC$User);
        avatarDrawable.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
        this.avatarImageView.setForUserOrChat(tLRPC$User, avatarDrawable);
        applyBackground(true);
    }

    public String applyBackground(boolean z) {
        String str = (String) getTag();
        String str2 = "chats_menuTopBackground";
        if (!Theme.hasThemeKey(str2) || Theme.getColor(str2) == 0) {
            str2 = "chats_menuTopBackgroundCats";
        }
        if (z || !str2.equals(str)) {
            setBackgroundColor(Theme.getColor(str2));
            setTag(str2);
        }
        return str2;
    }

    public void updateColors() {
        SnowflakesEffect snowflakesEffect = this.snowflakesEffect;
        if (snowflakesEffect != null) {
            snowflakesEffect.updateColors();
        }
    }

    private void setArrowState(boolean z) {
        String str;
        int i;
        float f = this.accountsShown ? 180.0f : 0.0f;
        if (z) {
            this.arrowView.animate().rotation(f).setDuration(220L).setInterpolator(CubicBezierInterpolator.EASE_OUT).start();
        } else {
            this.arrowView.animate().cancel();
            this.arrowView.setRotation(f);
        }
        ImageView imageView = this.arrowView;
        if (this.accountsShown) {
            i = R.string.AccDescrHideAccounts;
            str = "AccDescrHideAccounts";
        } else {
            i = R.string.AccDescrShowAccounts;
            str = "AccDescrShowAccounts";
        }
        imageView.setContentDescription(LocaleController.getString(str, i));
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            this.nameTextView.invalidate();
        }
    }
}
