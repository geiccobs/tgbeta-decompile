package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.ChatThemeBottomSheet;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ThemeSmallPreviewView;
/* loaded from: classes4.dex */
public class DefaultThemesPreviewCell extends LinearLayout {
    private final ChatThemeBottomSheet.Adapter adapter;
    TextCell browseThemesCell;
    int currentType;
    RLottieDrawable darkThemeDrawable;
    TextCell dayNightCell;
    private ValueAnimator navBarAnimator;
    private int navBarColor;
    BaseFragment parentFragment;
    private final FlickerLoadingView progressView;
    private final RecyclerListView recyclerView;
    int themeIndex;
    private LinearLayoutManager layoutManager = null;
    private int selectedPosition = -1;
    private Boolean wasPortrait = null;

    public DefaultThemesPreviewCell(final Context context, final BaseFragment parentFragment, int type) {
        super(context);
        LinearLayoutManager linearLayoutManager;
        this.currentType = type;
        this.parentFragment = parentFragment;
        setOrientation(1);
        FrameLayout frameLayout = new FrameLayout(context);
        addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f));
        int i = 0;
        ChatThemeBottomSheet.Adapter adapter = new ChatThemeBottomSheet.Adapter(parentFragment.getCurrentAccount(), null, this.currentType == 0 ? 0 : 1);
        this.adapter = adapter;
        RecyclerListView recyclerListView = new RecyclerListView(getContext());
        this.recyclerView = recyclerListView;
        recyclerListView.setAdapter(adapter);
        recyclerListView.setSelectorDrawableColor(0);
        recyclerListView.setClipChildren(false);
        recyclerListView.setClipToPadding(false);
        recyclerListView.setHasFixedSize(true);
        recyclerListView.setItemAnimator(null);
        recyclerListView.setNestedScrollingEnabled(false);
        updateLayoutManager();
        recyclerListView.setFocusable(false);
        recyclerListView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.DefaultThemesPreviewCell$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                DefaultThemesPreviewCell.this.m3313lambda$new$0$orgtelegramuiDefaultThemesPreviewCell(parentFragment, view, i2);
            }
        });
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(getContext(), null);
        this.progressView = flickerLoadingView;
        flickerLoadingView.setViewType(14);
        flickerLoadingView.setVisibility(0);
        if (this.currentType == 0) {
            frameLayout.addView(flickerLoadingView, LayoutHelper.createFrame(-1, 104.0f, GravityCompat.START, 0.0f, 8.0f, 0.0f, 8.0f));
            frameLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, 104.0f, GravityCompat.START, 0.0f, 8.0f, 0.0f, 8.0f));
        } else {
            frameLayout.addView(flickerLoadingView, LayoutHelper.createFrame(-1, 104.0f, GravityCompat.START, 0.0f, 8.0f, 0.0f, 8.0f));
            frameLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, -2.0f, GravityCompat.START, 0.0f, 8.0f, 0.0f, 8.0f));
        }
        recyclerListView.setEmptyView(flickerLoadingView);
        recyclerListView.setAnimateEmptyView(true, 0);
        if (this.currentType == 0) {
            RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.sun_outline, "2131558541", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, null);
            this.darkThemeDrawable = rLottieDrawable;
            rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
            this.darkThemeDrawable.beginApplyLayerColors();
            this.darkThemeDrawable.commitApplyLayerColors();
            TextCell textCell = new TextCell(context);
            this.dayNightCell = textCell;
            textCell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
            this.dayNightCell.imageLeft = 21;
            addView(this.dayNightCell, LayoutHelper.createFrame(-1, -2.0f));
            TextCell textCell2 = new TextCell(context);
            this.browseThemesCell = textCell2;
            textCell2.setTextAndIcon(LocaleController.getString("SettingsBrowseThemes", R.string.SettingsBrowseThemes), R.drawable.msg_colors, false);
            addView(this.browseThemesCell, LayoutHelper.createFrame(-1, -2.0f));
            this.dayNightCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DefaultThemesPreviewCell.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    String nightThemeName;
                    String dayThemeName;
                    Theme.ThemeInfo themeInfo;
                    float navBarOldColor;
                    if (DrawerProfileCell.switchingTheme) {
                        return;
                    }
                    final int iconOldColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4);
                    float navBarOldColor2 = Theme.getColor(Theme.key_windowBackgroundGray);
                    DrawerProfileCell.switchingTheme = true;
                    SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
                    String dayThemeName2 = preferences.getString("lastDayTheme", "Blue");
                    if (Theme.getTheme(dayThemeName2) == null || Theme.getTheme(dayThemeName2).isDark()) {
                        dayThemeName2 = "Blue";
                    }
                    String nightThemeName2 = preferences.getString("lastDarkTheme", "Dark Blue");
                    if (Theme.getTheme(nightThemeName2) == null || !Theme.getTheme(nightThemeName2).isDark()) {
                        nightThemeName2 = "Dark Blue";
                    }
                    Theme.ThemeInfo themeInfo2 = Theme.getActiveTheme();
                    if (!dayThemeName2.equals(nightThemeName2)) {
                        dayThemeName = dayThemeName2;
                        nightThemeName = nightThemeName2;
                    } else if (themeInfo2.isDark() || dayThemeName2.equals("Dark Blue") || dayThemeName2.equals("Night")) {
                        dayThemeName = "Blue";
                        nightThemeName = nightThemeName2;
                    } else {
                        dayThemeName = dayThemeName2;
                        nightThemeName = "Dark Blue";
                    }
                    boolean toDark = !Theme.isCurrentThemeDark();
                    if (toDark) {
                        themeInfo = Theme.getTheme(nightThemeName);
                    } else {
                        Theme.ThemeInfo themeInfo3 = Theme.getTheme(dayThemeName);
                        themeInfo = themeInfo3;
                    }
                    DefaultThemesPreviewCell.this.darkThemeDrawable.setCustomEndFrame(toDark ? DefaultThemesPreviewCell.this.darkThemeDrawable.getFramesCount() - 1 : 0);
                    DefaultThemesPreviewCell.this.dayNightCell.getImageView().playAnimation();
                    DefaultThemesPreviewCell.this.dayNightCell.getImageView().getLocationInWindow(pos);
                    int[] pos = {pos[0] + (DefaultThemesPreviewCell.this.dayNightCell.getImageView().getMeasuredWidth() / 2), pos[1] + (DefaultThemesPreviewCell.this.dayNightCell.getImageView().getMeasuredHeight() / 2) + AndroidUtilities.dp(3.0f)};
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, false, pos, -1, Boolean.valueOf(toDark), DefaultThemesPreviewCell.this.dayNightCell.getImageView(), DefaultThemesPreviewCell.this.dayNightCell);
                    DefaultThemesPreviewCell.this.updateDayNightMode();
                    DefaultThemesPreviewCell.this.updateSelectedPosition();
                    final int iconNewColor = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4);
                    DefaultThemesPreviewCell.this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(iconNewColor, PorterDuff.Mode.SRC_IN));
                    ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.DefaultThemesPreviewCell.1.1
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            int iconColor = ColorUtils.blendARGB(iconOldColor, iconNewColor, ((Float) valueAnimator2.getAnimatedValue()).floatValue());
                            DefaultThemesPreviewCell.this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(iconColor, PorterDuff.Mode.SRC_IN));
                        }
                    });
                    valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DefaultThemesPreviewCell.1.2
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            DefaultThemesPreviewCell.this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(iconNewColor, PorterDuff.Mode.SRC_IN));
                            super.onAnimationEnd(animation);
                        }
                    });
                    valueAnimator.setDuration(350L);
                    valueAnimator.start();
                    final int navBarNewColor = Theme.getColor(Theme.key_windowBackgroundGray);
                    Context context2 = context;
                    Window window = context2 instanceof Activity ? ((Activity) context2).getWindow() : null;
                    if (window != null) {
                        if (DefaultThemesPreviewCell.this.navBarAnimator != null && DefaultThemesPreviewCell.this.navBarAnimator.isRunning()) {
                            int navBarOldColor3 = DefaultThemesPreviewCell.this.navBarColor;
                            DefaultThemesPreviewCell.this.navBarAnimator.cancel();
                            navBarOldColor = navBarOldColor3;
                        } else {
                            navBarOldColor = navBarOldColor2;
                        }
                        final int navBarFromColor = navBarOldColor;
                        DefaultThemesPreviewCell.this.navBarAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                        final float startDelay = toDark ? 50.0f : 200.0f;
                        final Window window2 = window;
                        DefaultThemesPreviewCell.this.navBarAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.DefaultThemesPreviewCell.1.3
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                                float t = Math.max(0.0f, Math.min(1.0f, ((((Float) valueAnimator2.getAnimatedValue()).floatValue() * 350.0f) - startDelay) / 150.0f));
                                DefaultThemesPreviewCell.this.navBarColor = ColorUtils.blendARGB(navBarFromColor, navBarNewColor, t);
                                boolean z = false;
                                AndroidUtilities.setNavigationBarColor(window2, DefaultThemesPreviewCell.this.navBarColor, false);
                                Window window3 = window2;
                                if (AndroidUtilities.computePerceivedBrightness(DefaultThemesPreviewCell.this.navBarColor) >= 0.721f) {
                                    z = true;
                                }
                                AndroidUtilities.setLightNavigationBar(window3, z);
                            }
                        });
                        DefaultThemesPreviewCell.this.navBarAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DefaultThemesPreviewCell.1.4
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                boolean z = false;
                                AndroidUtilities.setNavigationBarColor(window2, navBarNewColor, false);
                                Window window3 = window2;
                                if (AndroidUtilities.computePerceivedBrightness(navBarNewColor) >= 0.721f) {
                                    z = true;
                                }
                                AndroidUtilities.setLightNavigationBar(window3, z);
                            }
                        });
                        DefaultThemesPreviewCell.this.navBarAnimator.setDuration(350L);
                        DefaultThemesPreviewCell.this.navBarAnimator.start();
                    }
                    if (Theme.isCurrentThemeDay()) {
                        DefaultThemesPreviewCell.this.dayNightCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToNightMode", R.string.SettingsSwitchToNightMode), (Drawable) DefaultThemesPreviewCell.this.darkThemeDrawable, true);
                    } else {
                        DefaultThemesPreviewCell.this.dayNightCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToDayMode", R.string.SettingsSwitchToDayMode), (Drawable) DefaultThemesPreviewCell.this.darkThemeDrawable, true);
                    }
                }
            });
            this.darkThemeDrawable.setPlayInDirectionOfCustomEndFrame(true);
            this.browseThemesCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DefaultThemesPreviewCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    BaseFragment.this.presentFragment(new ThemeActivity(3));
                }
            });
            if (Theme.isCurrentThemeDay()) {
                this.dayNightCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToNightMode", R.string.SettingsSwitchToNightMode), (Drawable) this.darkThemeDrawable, true);
            } else {
                RLottieDrawable rLottieDrawable2 = this.darkThemeDrawable;
                rLottieDrawable2.setCurrentFrame(rLottieDrawable2.getFramesCount() - 1);
                this.dayNightCell.setTextAndIcon(LocaleController.getString("SettingsSwitchToDayMode", R.string.SettingsSwitchToDayMode), (Drawable) this.darkThemeDrawable, true);
            }
        }
        if (!MediaDataController.getInstance(parentFragment.getCurrentAccount()).defaultEmojiThemes.isEmpty()) {
            ArrayList<ChatThemeBottomSheet.ChatThemeItem> themes = new ArrayList<>(MediaDataController.getInstance(parentFragment.getCurrentAccount()).defaultEmojiThemes);
            if (this.currentType == 0) {
                EmojiThemes chatTheme = EmojiThemes.createPreviewCustom();
                chatTheme.loadPreviewColors(parentFragment.getCurrentAccount());
                ChatThemeBottomSheet.ChatThemeItem item = new ChatThemeBottomSheet.ChatThemeItem(chatTheme);
                item.themeIndex = !Theme.isCurrentThemeDay() ? 2 : i;
                themes.add(item);
            }
            adapter.setItems(themes);
        }
        updateDayNightMode();
        updateSelectedPosition();
        updateColors();
        int i2 = this.selectedPosition;
        if (i2 >= 0 && (linearLayoutManager = this.layoutManager) != null) {
            linearLayoutManager.scrollToPositionWithOffset(i2, AndroidUtilities.dp(16.0f));
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-DefaultThemesPreviewCell */
    public /* synthetic */ void m3313lambda$new$0$orgtelegramuiDefaultThemesPreviewCell(BaseFragment parentFragment, View view, int position) {
        ChatThemeBottomSheet.ChatThemeItem chatTheme = this.adapter.items.get(position);
        Theme.ThemeInfo info = chatTheme.chatTheme.getThemeInfo(this.themeIndex);
        int accentId = -1;
        if (chatTheme.chatTheme.getEmoticon().equals("🏠") || chatTheme.chatTheme.getEmoticon().equals("🎨")) {
            accentId = chatTheme.chatTheme.getAccentId(this.themeIndex);
        }
        if (info == null) {
            TLRPC.TL_theme theme = chatTheme.chatTheme.getTlTheme(this.themeIndex);
            int settingsIndex = chatTheme.chatTheme.getSettingsIndex(this.themeIndex);
            TLRPC.ThemeSettings settings = theme.settings.get(settingsIndex);
            String key = Theme.getBaseThemeKey(settings);
            info = Theme.getTheme(key);
            if (info != null) {
                Theme.ThemeAccent accent = info.accentsByThemeId.get(theme.id);
                if (accent == null) {
                    accent = info.createNewAccent(theme, parentFragment.getCurrentAccount());
                }
                accentId = accent.id;
                info.setCurrentAccentId(accentId);
            }
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, info, false, null, Integer.valueOf(accentId));
        this.selectedPosition = position;
        int i = 0;
        while (i < this.adapter.items.size()) {
            this.adapter.items.get(i).isSelected = i == this.selectedPosition;
            i++;
        }
        this.adapter.setSelectedItem(this.selectedPosition);
        for (int i2 = 0; i2 < this.recyclerView.getChildCount(); i2++) {
            ThemeSmallPreviewView child = (ThemeSmallPreviewView) this.recyclerView.getChildAt(i2);
            if (child != view) {
                child.cancelAnimation();
            }
        }
        ((ThemeSmallPreviewView) view).playEmojiAnimation();
        if (info != null) {
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
            editor.putString((this.currentType == 1 || info.isDark()) ? "lastDarkTheme" : "lastDayTheme", info.getKey());
            editor.commit();
        }
    }

    public void updateLayoutManager() {
        boolean isPortrait = AndroidUtilities.displaySize.y > AndroidUtilities.displaySize.x;
        Boolean bool = this.wasPortrait;
        if (bool != null && bool.booleanValue() == isPortrait) {
            return;
        }
        if (this.currentType == 0) {
            if (this.layoutManager == null) {
                RecyclerListView recyclerListView = this.recyclerView;
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), 0, false);
                this.layoutManager = linearLayoutManager;
                recyclerListView.setLayoutManager(linearLayoutManager);
            }
        } else {
            int spanCount = isPortrait ? 3 : 9;
            LinearLayoutManager linearLayoutManager2 = this.layoutManager;
            if (linearLayoutManager2 instanceof GridLayoutManager) {
                ((GridLayoutManager) linearLayoutManager2).setSpanCount(spanCount);
            } else {
                this.recyclerView.setHasFixedSize(false);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.DefaultThemesPreviewCell.2
                    @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
                    public int getSpanSize(int position) {
                        return 1;
                    }
                });
                RecyclerListView recyclerListView2 = this.recyclerView;
                this.layoutManager = gridLayoutManager;
                recyclerListView2.setLayoutManager(gridLayoutManager);
            }
        }
        this.wasPortrait = Boolean.valueOf(isPortrait);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        updateLayoutManager();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void updateDayNightMode() {
        int i;
        int i2;
        int i3 = 2;
        if (this.currentType == 0) {
            if (Theme.isCurrentThemeDay()) {
                i3 = 0;
            }
            this.themeIndex = i3;
        } else if (Theme.getActiveTheme().getKey().equals("Blue")) {
            this.themeIndex = 0;
        } else if (Theme.getActiveTheme().getKey().equals("Day")) {
            this.themeIndex = 1;
        } else if (Theme.getActiveTheme().getKey().equals("Night")) {
            this.themeIndex = 2;
        } else if (Theme.getActiveTheme().getKey().equals("Dark Blue")) {
            this.themeIndex = 3;
        } else {
            if (Theme.isCurrentThemeDay() && ((i2 = this.themeIndex) == 2 || i2 == 3)) {
                this.themeIndex = 0;
            }
            if (!Theme.isCurrentThemeDay() && ((i = this.themeIndex) == 0 || i == 1)) {
                this.themeIndex = 2;
            }
        }
        if (this.adapter.items != null) {
            for (int i4 = 0; i4 < this.adapter.items.size(); i4++) {
                this.adapter.items.get(i4).themeIndex = this.themeIndex;
            }
            ChatThemeBottomSheet.Adapter adapter = this.adapter;
            adapter.notifyItemRangeChanged(0, adapter.items.size());
        }
        updateSelectedPosition();
    }

    public void updateSelectedPosition() {
        if (this.adapter.items == null) {
            return;
        }
        this.selectedPosition = -1;
        int i = 0;
        while (true) {
            if (i >= this.adapter.items.size()) {
                break;
            }
            TLRPC.TL_theme theme = this.adapter.items.get(i).chatTheme.getTlTheme(this.themeIndex);
            Theme.ThemeInfo themeInfo = this.adapter.items.get(i).chatTheme.getThemeInfo(this.themeIndex);
            if (theme != null) {
                int settingsIndex = this.adapter.items.get(i).chatTheme.getSettingsIndex(this.themeIndex);
                String key = Theme.getBaseThemeKey(theme.settings.get(settingsIndex));
                if (Theme.getActiveTheme().name.equals(key)) {
                    if (Theme.getActiveTheme().accentsByThemeId == null) {
                        this.selectedPosition = i;
                        break;
                    }
                    Theme.ThemeAccent accent = Theme.getActiveTheme().accentsByThemeId.get(theme.id);
                    if (accent != null && accent.id == Theme.getActiveTheme().currentAccentId) {
                        this.selectedPosition = i;
                        break;
                    }
                } else {
                    continue;
                }
                i++;
            } else {
                if (themeInfo != null) {
                    String key2 = themeInfo.getKey();
                    if (Theme.getActiveTheme().name.equals(key2) && this.adapter.items.get(i).chatTheme.getAccentId(this.themeIndex) == Theme.getActiveTheme().currentAccentId) {
                        this.selectedPosition = i;
                        break;
                    }
                } else {
                    continue;
                }
                i++;
            }
        }
        int i2 = this.selectedPosition;
        if (i2 == -1 && this.currentType != 3) {
            this.selectedPosition = this.adapter.items.size() - 1;
        }
        int i3 = 0;
        while (i3 < this.adapter.items.size()) {
            this.adapter.items.get(i3).isSelected = i3 == this.selectedPosition;
            i3++;
        }
        this.adapter.setSelectedItem(this.selectedPosition);
    }

    public void selectTheme(Theme.ThemeInfo themeInfo) {
        if (themeInfo.info != null && !themeInfo.themeLoaded) {
            return;
        }
        if (!TextUtils.isEmpty(themeInfo.assetName)) {
            Theme.PatternsLoader.createLoader(false);
        }
        if (this.currentType != 2) {
            SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
            editor.putString((this.currentType == 1 || themeInfo.isDark()) ? "lastDarkTheme" : "lastDayTheme", themeInfo.getKey());
            editor.commit();
        }
        if (this.currentType == 1) {
            if (themeInfo == Theme.getCurrentNightTheme()) {
                return;
            }
            Theme.setCurrentNightTheme(themeInfo);
        } else if (themeInfo == Theme.getActiveTheme()) {
            return;
        } else {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, false, null, -1);
        }
        getChildCount();
    }

    public void updateColors() {
        if (this.currentType == 0) {
            this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4), PorterDuff.Mode.SRC_IN));
            Theme.setSelectorDrawableColor(this.dayNightCell.getBackground(), Theme.getColor(Theme.key_listSelector), true);
            this.browseThemesCell.setBackground(Theme.createSelectorWithBackgroundDrawable(Theme.getColor(Theme.key_windowBackgroundWhite), Theme.getColor(Theme.key_listSelector)));
            this.dayNightCell.setColors(null, Theme.key_windowBackgroundWhiteBlueText4);
            this.browseThemesCell.setColors(Theme.key_windowBackgroundWhiteBlueText4, Theme.key_windowBackgroundWhiteBlueText4);
        }
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        updateColors();
    }
}
