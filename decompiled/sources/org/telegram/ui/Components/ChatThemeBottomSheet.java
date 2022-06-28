package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.graphics.ColorUtils$$ExternalSyntheticBackport0;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ChatThemeBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class ChatThemeBottomSheet extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private final Adapter adapter;
    private final View applyButton;
    private TextView applyTextView;
    private View changeDayNightView;
    private ValueAnimator changeDayNightViewAnimator;
    private float changeDayNightViewProgress;
    private final ChatActivity chatActivity;
    private final RLottieDrawable darkThemeDrawable;
    private final RLottieImageView darkThemeView;
    HintView hintView;
    private boolean isApplyClicked;
    private boolean isLightDarkChangeAnimation;
    private final LinearLayoutManager layoutManager;
    private final EmojiThemes originalTheme;
    private final FlickerLoadingView progressView;
    private final RecyclerListView recyclerView;
    private TextView resetTextView;
    private FrameLayout rootLayout;
    private ChatThemeItem selectedItem;
    private final ChatActivity.ThemeDelegate themeDelegate;
    private final TextView titleView;
    private int prevSelectedPosition = -1;
    private final boolean originalIsDark = Theme.getActiveTheme().isDark();
    private boolean forceDark = !Theme.getActiveTheme().isDark();
    private final LinearSmoothScroller scroller = new LinearSmoothScroller(getContext()) { // from class: org.telegram.ui.Components.ChatThemeBottomSheet.2
        @Override // androidx.recyclerview.widget.LinearSmoothScroller
        public int calculateTimeForScrolling(int dx) {
            return super.calculateTimeForScrolling(dx) * 6;
        }
    };

    public ChatThemeBottomSheet(ChatActivity chatActivity, final ChatActivity.ThemeDelegate themeDelegate) {
        super(chatActivity.getParentActivity(), true, themeDelegate);
        String str;
        int i;
        this.chatActivity = chatActivity;
        this.themeDelegate = themeDelegate;
        this.originalTheme = themeDelegate.getCurrentTheme();
        Adapter adapter = new Adapter(this.currentAccount, themeDelegate, 0);
        this.adapter = adapter;
        setDimBehind(false);
        setCanDismissWithSwipe(false);
        setApplyBottomPadding(false);
        this.drawNavigationBar = true;
        FrameLayout frameLayout = new FrameLayout(getContext());
        this.rootLayout = frameLayout;
        setCustomView(frameLayout);
        TextView textView = new TextView(getContext());
        this.titleView = textView;
        textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        textView.setLines(1);
        textView.setSingleLine(true);
        textView.setText(LocaleController.getString("SelectTheme", R.string.SelectTheme));
        textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
        this.rootLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 0.0f, 62.0f, 0.0f));
        int drawableColor = getThemedColor(Theme.key_featuredStickers_addButton);
        int drawableSize = AndroidUtilities.dp(28.0f);
        RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.sun_outline, "2131558541", drawableSize, drawableSize, false, null);
        this.darkThemeDrawable = rLottieDrawable;
        setForceDark(Theme.getActiveTheme().isDark(), false);
        rLottieDrawable.setAllowDecodeSingleFrame(true);
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        rLottieDrawable.setColorFilter(new PorterDuffColorFilter(drawableColor, PorterDuff.Mode.MULTIPLY));
        RLottieImageView rLottieImageView = new RLottieImageView(getContext()) { // from class: org.telegram.ui.Components.ChatThemeBottomSheet.1
            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                if (ChatThemeBottomSheet.this.forceDark) {
                    info.setText(LocaleController.getString("AccDescrSwitchToDayTheme", R.string.AccDescrSwitchToDayTheme));
                } else {
                    info.setText(LocaleController.getString("AccDescrSwitchToNightTheme", R.string.AccDescrSwitchToNightTheme));
                }
            }
        };
        this.darkThemeView = rLottieImageView;
        rLottieImageView.setAnimation(rLottieDrawable);
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        rLottieImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatThemeBottomSheet.this.m2527lambda$new$0$orgtelegramuiComponentsChatThemeBottomSheet(view);
            }
        });
        this.rootLayout.addView(rLottieImageView, LayoutHelper.createFrame(44, 44.0f, 8388661, 0.0f, -2.0f, 7.0f, 0.0f));
        RecyclerListView recyclerListView = new RecyclerListView(getContext());
        this.recyclerView = recyclerListView;
        recyclerListView.setAdapter(adapter);
        recyclerListView.setClipChildren(false);
        recyclerListView.setClipToPadding(false);
        recyclerListView.setHasFixedSize(true);
        recyclerListView.setItemAnimator(null);
        recyclerListView.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), 0, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        recyclerListView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                ChatThemeBottomSheet.this.m2528lambda$new$1$orgtelegramuiComponentsChatThemeBottomSheet(themeDelegate, view, i2);
            }
        });
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(getContext(), this.resourcesProvider);
        this.progressView = flickerLoadingView;
        flickerLoadingView.setViewType(14);
        flickerLoadingView.setVisibility(0);
        this.rootLayout.addView(flickerLoadingView, LayoutHelper.createFrame(-1, 104.0f, GravityCompat.START, 0.0f, 44.0f, 0.0f, 0.0f));
        this.rootLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, 104.0f, GravityCompat.START, 0.0f, 44.0f, 0.0f, 0.0f));
        View view = new View(getContext());
        this.applyButton = view;
        view.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor(Theme.key_featuredStickers_addButton), getThemedColor(Theme.key_featuredStickers_addButtonPressed)));
        view.setEnabled(false);
        view.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatThemeBottomSheet.this.m2529lambda$new$2$orgtelegramuiComponentsChatThemeBottomSheet(view2);
            }
        });
        this.rootLayout.addView(view, LayoutHelper.createFrame(-1, 48.0f, GravityCompat.START, 16.0f, 162.0f, 16.0f, 16.0f));
        TextView textView2 = new TextView(getContext());
        this.resetTextView = textView2;
        textView2.setAlpha(0.0f);
        this.resetTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.resetTextView.setGravity(17);
        this.resetTextView.setLines(1);
        this.resetTextView.setSingleLine(true);
        TextView textView3 = this.resetTextView;
        if (themeDelegate.getCurrentTheme() == null) {
            i = R.string.DoNoSetTheme;
            str = "DoNoSetTheme";
        } else {
            i = R.string.ChatResetTheme;
            str = "ChatResetTheme";
        }
        textView3.setText(LocaleController.getString(str, i));
        this.resetTextView.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
        this.resetTextView.setTextSize(1, 15.0f);
        this.resetTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.resetTextView.setVisibility(4);
        this.rootLayout.addView(this.resetTextView, LayoutHelper.createFrame(-1, 48.0f, GravityCompat.START, 16.0f, 162.0f, 16.0f, 16.0f));
        TextView textView4 = new TextView(getContext());
        this.applyTextView = textView4;
        textView4.setEllipsize(TextUtils.TruncateAt.END);
        this.applyTextView.setGravity(17);
        this.applyTextView.setLines(1);
        this.applyTextView.setSingleLine(true);
        this.applyTextView.setText(LocaleController.getString("ChatApplyTheme", R.string.ChatApplyTheme));
        this.applyTextView.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
        this.applyTextView.setTextSize(1, 15.0f);
        this.applyTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.applyTextView.setVisibility(4);
        this.rootLayout.addView(this.applyTextView, LayoutHelper.createFrame(-1, 48.0f, GravityCompat.START, 16.0f, 162.0f, 16.0f, 16.0f));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatThemeBottomSheet */
    public /* synthetic */ void m2527lambda$new$0$orgtelegramuiComponentsChatThemeBottomSheet(View view) {
        if (this.changeDayNightViewAnimator != null) {
            return;
        }
        setupLightDarkTheme(!this.forceDark);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatThemeBottomSheet */
    public /* synthetic */ void m2528lambda$new$1$orgtelegramuiComponentsChatThemeBottomSheet(ChatActivity.ThemeDelegate themeDelegate, View view, final int position) {
        if (this.adapter.items.get(position) == this.selectedItem || this.changeDayNightView != null) {
            return;
        }
        ChatThemeItem chatThemeItem = this.adapter.items.get(position);
        this.selectedItem = chatThemeItem;
        this.isLightDarkChangeAnimation = false;
        if (chatThemeItem.chatTheme == null || this.selectedItem.chatTheme.showAsDefaultStub) {
            this.applyTextView.animate().alpha(0.0f).setDuration(300L).start();
            this.resetTextView.animate().alpha(1.0f).setDuration(300L).start();
        } else {
            this.resetTextView.animate().alpha(0.0f).setDuration(300L).start();
            this.applyTextView.animate().alpha(1.0f).setDuration(300L).start();
        }
        if (this.selectedItem.chatTheme.showAsDefaultStub) {
            themeDelegate.setCurrentTheme(null, true, Boolean.valueOf(this.forceDark));
        } else {
            themeDelegate.setCurrentTheme(this.selectedItem.chatTheme, true, Boolean.valueOf(this.forceDark));
        }
        this.adapter.setSelectedItem(position);
        this.containerView.postDelayed(new Runnable() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet.3
            @Override // java.lang.Runnable
            public void run() {
                int targetPosition;
                RecyclerView.LayoutManager layoutManager = ChatThemeBottomSheet.this.recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    if (position > ChatThemeBottomSheet.this.prevSelectedPosition) {
                        targetPosition = Math.min(position + 1, ChatThemeBottomSheet.this.adapter.items.size() - 1);
                    } else {
                        targetPosition = Math.max(position - 1, 0);
                    }
                    ChatThemeBottomSheet.this.scroller.setTargetPosition(targetPosition);
                    layoutManager.startSmoothScroll(ChatThemeBottomSheet.this.scroller);
                }
                ChatThemeBottomSheet.this.prevSelectedPosition = position;
            }
        }, 100L);
        for (int i = 0; i < this.recyclerView.getChildCount(); i++) {
            ThemeSmallPreviewView child = (ThemeSmallPreviewView) this.recyclerView.getChildAt(i);
            if (child != view) {
                child.cancelAnimation();
            }
        }
        if (!this.adapter.items.get(position).chatTheme.showAsDefaultStub) {
            ((ThemeSmallPreviewView) view).playEmojiAnimation();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatThemeBottomSheet */
    public /* synthetic */ void m2529lambda$new$2$orgtelegramuiComponentsChatThemeBottomSheet(View view) {
        applySelectedTheme();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChatThemeController.preloadAllWallpaperThumbs(true);
        ChatThemeController.preloadAllWallpaperThumbs(false);
        ChatThemeController.preloadAllWallpaperImages(true);
        ChatThemeController.preloadAllWallpaperImages(false);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.isApplyClicked = false;
        List<EmojiThemes> cachedThemes = this.themeDelegate.getCachedThemes();
        if (cachedThemes == null || cachedThemes.isEmpty()) {
            ChatThemeController.requestAllChatThemes(new ResultCallback<List<EmojiThemes>>() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet.4
                @Override // org.telegram.tgnet.ResultCallback
                public /* synthetic */ void onError(Throwable th) {
                    ResultCallback.CC.$default$onError(this, th);
                }

                public void onComplete(List<EmojiThemes> result) {
                    if (result != null && !result.isEmpty()) {
                        ChatThemeBottomSheet.this.themeDelegate.setCachedThemes(result);
                    }
                    ChatThemeBottomSheet.this.onDataLoaded(result);
                }

                @Override // org.telegram.tgnet.ResultCallback
                public void onError(TLRPC.TL_error error) {
                    Toast.makeText(ChatThemeBottomSheet.this.getContext(), error.text, 0).show();
                }
            }, true);
        } else {
            onDataLoaded(cachedThemes);
        }
        if (this.chatActivity.getCurrentUser() != null && SharedConfig.dayNightThemeSwitchHintCount > 0 && !this.chatActivity.getCurrentUser().self) {
            SharedConfig.updateDayNightThemeSwitchHintCount(SharedConfig.dayNightThemeSwitchHintCount - 1);
            HintView hintView = new HintView(getContext(), 9, this.chatActivity.getResourceProvider());
            this.hintView = hintView;
            hintView.setVisibility(4);
            this.hintView.setShowingDuration(DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
            this.hintView.setBottomOffset(-AndroidUtilities.dp(8.0f));
            this.hintView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ChatThemeDayNightSwitchTooltip", R.string.ChatThemeDayNightSwitchTooltip, this.chatActivity.getCurrentUser().first_name)));
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ChatThemeBottomSheet.this.m2530xc8d0fdc7();
                }
            }, 1500L);
            this.container.addView(this.hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
        }
    }

    /* renamed from: lambda$onCreate$3$org-telegram-ui-Components-ChatThemeBottomSheet */
    public /* synthetic */ void m2530xc8d0fdc7() {
        this.hintView.showForView(this.darkThemeView, true);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void onContainerTranslationYChanged(float y) {
        HintView hintView = this.hintView;
        if (hintView != null) {
            hintView.hide();
        }
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        close();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        super.dismiss();
        if (!this.isApplyClicked) {
            this.themeDelegate.setCurrentTheme(this.originalTheme, true, Boolean.valueOf(this.originalIsDark));
        }
    }

    public void close() {
        if (hasChanges()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), this.resourcesProvider);
            builder.setTitle(LocaleController.getString("ChatThemeSaveDialogTitle", R.string.ChatThemeSaveDialogTitle));
            builder.setSubtitle(LocaleController.getString("ChatThemeSaveDialogText", R.string.ChatThemeSaveDialogText));
            builder.setPositiveButton(LocaleController.getString("ChatThemeSaveDialogApply", R.string.ChatThemeSaveDialogApply), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatThemeBottomSheet.this.m2525lambda$close$4$orgtelegramuiComponentsChatThemeBottomSheet(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("ChatThemeSaveDialogDiscard", R.string.ChatThemeSaveDialogDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatThemeBottomSheet.this.m2526lambda$close$5$orgtelegramuiComponentsChatThemeBottomSheet(dialogInterface, i);
                }
            });
            builder.show();
            return;
        }
        dismiss();
    }

    /* renamed from: lambda$close$4$org-telegram-ui-Components-ChatThemeBottomSheet */
    public /* synthetic */ void m2525lambda$close$4$orgtelegramuiComponentsChatThemeBottomSheet(DialogInterface dialogInterface, int i) {
        applySelectedTheme();
    }

    /* renamed from: lambda$close$5$org-telegram-ui-Components-ChatThemeBottomSheet */
    public /* synthetic */ void m2526lambda$close$5$orgtelegramuiComponentsChatThemeBottomSheet(DialogInterface dialogInterface, int i) {
        dismiss();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.emojiLoaded) {
            this.adapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet.5
            private boolean isAnimationStarted = false;

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public void onAnimationProgress(float progress) {
                if (progress == 0.0f && !this.isAnimationStarted) {
                    ChatThemeBottomSheet.this.onAnimationStart();
                    this.isAnimationStarted = true;
                }
                ChatThemeBottomSheet.this.darkThemeDrawable.setColorFilter(new PorterDuffColorFilter(ChatThemeBottomSheet.this.getThemedColor(Theme.key_featuredStickers_addButton), PorterDuff.Mode.MULTIPLY));
                ChatThemeBottomSheet chatThemeBottomSheet = ChatThemeBottomSheet.this;
                chatThemeBottomSheet.setOverlayNavBarColor(chatThemeBottomSheet.getThemedColor(Theme.key_windowBackgroundGray));
                if (ChatThemeBottomSheet.this.isLightDarkChangeAnimation) {
                    ChatThemeBottomSheet.this.setItemsAnimationProgress(progress);
                }
                if (progress == 1.0f && this.isAnimationStarted) {
                    ChatThemeBottomSheet.this.isLightDarkChangeAnimation = false;
                    ChatThemeBottomSheet.this.onAnimationEnd();
                    this.isAnimationStarted = false;
                }
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public void didSetColor() {
            }
        };
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(null, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, new Drawable[]{this.shadowDrawable}, descriptionDelegate, Theme.key_dialogBackground));
        themeDescriptions.add(new ThemeDescription(this.titleView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription(this.recyclerView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ThemeSmallPreviewView.class}, null, null, null, Theme.key_dialogBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.applyButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_featuredStickers_addButton));
        themeDescriptions.add(new ThemeDescription(this.applyButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_featuredStickers_addButtonPressed));
        Iterator<ThemeDescription> it = themeDescriptions.iterator();
        while (it.hasNext()) {
            ThemeDescription description = it.next();
            description.resourcesProvider = this.themeDelegate;
        }
        return themeDescriptions;
    }

    public void setupLightDarkTheme(final boolean isDark) {
        ValueAnimator valueAnimator = this.changeDayNightViewAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        FrameLayout decorView1 = (FrameLayout) this.chatActivity.getParentActivity().getWindow().getDecorView();
        FrameLayout decorView2 = (FrameLayout) getWindow().getDecorView();
        final Bitmap bitmap = Bitmap.createBitmap(decorView2.getWidth(), decorView2.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas bitmapCanvas = new Canvas(bitmap);
        this.darkThemeView.setAlpha(0.0f);
        decorView1.draw(bitmapCanvas);
        decorView2.draw(bitmapCanvas);
        this.darkThemeView.setAlpha(1.0f);
        final Paint xRefPaint = new Paint(1);
        xRefPaint.setColor(-16777216);
        xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        final Paint bitmapPaint = new Paint(1);
        bitmapPaint.setFilterBitmap(true);
        int[] position = new int[2];
        this.darkThemeView.getLocationInWindow(position);
        final float x = position[0];
        final float y = position[1];
        final float cx = x + (this.darkThemeView.getMeasuredWidth() / 2.0f);
        final float cy = y + (this.darkThemeView.getMeasuredHeight() / 2.0f);
        final float r = Math.max(bitmap.getHeight(), bitmap.getWidth()) * 0.9f;
        Shader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapPaint.setShader(bitmapShader);
        this.changeDayNightView = new View(getContext()) { // from class: org.telegram.ui.Components.ChatThemeBottomSheet.6
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (isDark) {
                    if (ChatThemeBottomSheet.this.changeDayNightViewProgress > 0.0f) {
                        bitmapCanvas.drawCircle(cx, cy, r * ChatThemeBottomSheet.this.changeDayNightViewProgress, xRefPaint);
                    }
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, bitmapPaint);
                } else {
                    canvas.drawCircle(cx, cy, r * (1.0f - ChatThemeBottomSheet.this.changeDayNightViewProgress), bitmapPaint);
                }
                canvas.save();
                canvas.translate(x, y);
                ChatThemeBottomSheet.this.darkThemeView.draw(canvas);
                canvas.restore();
            }
        };
        this.changeDayNightViewProgress = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.changeDayNightViewAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet.7
            boolean changedNavigationBarColor = false;

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                ChatThemeBottomSheet.this.changeDayNightViewProgress = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                ChatThemeBottomSheet.this.changeDayNightView.invalidate();
                if (!this.changedNavigationBarColor && ChatThemeBottomSheet.this.changeDayNightViewProgress > 0.5f) {
                    this.changedNavigationBarColor = true;
                    AndroidUtilities.setLightNavigationBar(ChatThemeBottomSheet.this.getWindow(), true ^ isDark);
                    AndroidUtilities.setNavigationBarColor(ChatThemeBottomSheet.this.getWindow(), ChatThemeBottomSheet.this.getThemedColor(Theme.key_windowBackgroundGray));
                }
            }
        });
        this.changeDayNightViewAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet.8
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ChatThemeBottomSheet.this.changeDayNightView != null) {
                    if (ChatThemeBottomSheet.this.changeDayNightView.getParent() != null) {
                        ((ViewGroup) ChatThemeBottomSheet.this.changeDayNightView.getParent()).removeView(ChatThemeBottomSheet.this.changeDayNightView);
                    }
                    ChatThemeBottomSheet.this.changeDayNightView = null;
                }
                ChatThemeBottomSheet.this.changeDayNightViewAnimator = null;
                super.onAnimationEnd(animation);
            }
        });
        this.changeDayNightViewAnimator.setDuration(400L);
        this.changeDayNightViewAnimator.setInterpolator(Easings.easeInOutQuad);
        this.changeDayNightViewAnimator.start();
        decorView2.addView(this.changeDayNightView, new ViewGroup.LayoutParams(-1, -1));
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                ChatThemeBottomSheet.this.m2531xc4b3db8f(isDark);
            }
        });
    }

    /* renamed from: lambda$setupLightDarkTheme$6$org-telegram-ui-Components-ChatThemeBottomSheet */
    public /* synthetic */ void m2531xc4b3db8f(boolean isDark) {
        Adapter adapter = this.adapter;
        if (adapter == null || adapter.items == null) {
            return;
        }
        setForceDark(isDark, true);
        ChatThemeItem chatThemeItem = this.selectedItem;
        if (chatThemeItem != null) {
            this.isLightDarkChangeAnimation = true;
            if (chatThemeItem.chatTheme.showAsDefaultStub) {
                this.themeDelegate.setCurrentTheme(null, false, Boolean.valueOf(isDark));
            } else {
                this.themeDelegate.setCurrentTheme(this.selectedItem.chatTheme, false, Boolean.valueOf(isDark));
            }
        }
        Adapter adapter2 = this.adapter;
        if (adapter2 != null && adapter2.items != null) {
            for (int i = 0; i < this.adapter.items.size(); i++) {
                this.adapter.items.get(i).themeIndex = isDark ? 1 : 0;
            }
            this.adapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean onContainerTouchEvent(MotionEvent event) {
        if (event == null || !hasChanges()) {
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean touchInsideContainer = y >= this.containerView.getTop() && x >= this.containerView.getLeft() && x <= this.containerView.getRight();
        if (touchInsideContainer) {
            return false;
        }
        this.chatActivity.getFragmentView().dispatchTouchEvent(event);
        return true;
    }

    public void onDataLoaded(List<EmojiThemes> result) {
        if (result == null || result.isEmpty()) {
            return;
        }
        ChatThemeItem noThemeItem = new ChatThemeItem(result.get(0));
        List<ChatThemeItem> items = new ArrayList<>(result.size());
        EmojiThemes currentTheme = this.themeDelegate.getCurrentTheme();
        items.add(0, noThemeItem);
        this.selectedItem = noThemeItem;
        for (int i = 1; i < result.size(); i++) {
            EmojiThemes chatTheme = result.get(i);
            ChatThemeItem item = new ChatThemeItem(chatTheme);
            chatTheme.loadPreviewColors(this.currentAccount);
            item.themeIndex = this.forceDark ? 1 : 0;
            items.add(item);
        }
        this.adapter.setItems(items);
        this.applyButton.setEnabled(true);
        this.applyTextView.setAlpha(0.0f);
        this.resetTextView.setAlpha(0.0f);
        this.recyclerView.setAlpha(0.0f);
        this.applyTextView.setVisibility(0);
        this.resetTextView.setVisibility(0);
        this.darkThemeView.setVisibility(0);
        boolean showRestText = false;
        if (currentTheme != null) {
            int selectedPosition = -1;
            int i2 = 0;
            while (true) {
                if (i2 != items.size()) {
                    if (!items.get(i2).chatTheme.getEmoticon().equals(currentTheme.getEmoticon())) {
                        i2++;
                    } else {
                        this.selectedItem = items.get(i2);
                        selectedPosition = i2;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (selectedPosition != -1) {
                this.prevSelectedPosition = selectedPosition;
                this.adapter.setSelectedItem(selectedPosition);
                if (selectedPosition > 0 && selectedPosition < items.size() / 2) {
                    selectedPosition--;
                }
                int finalSelectedPosition = Math.min(selectedPosition, this.adapter.items.size() - 1);
                this.layoutManager.scrollToPositionWithOffset(finalSelectedPosition, 0);
            }
        } else {
            showRestText = true;
            this.adapter.setSelectedItem(0);
            this.layoutManager.scrollToPositionWithOffset(0, 0);
        }
        float f = 1.0f;
        this.recyclerView.animate().alpha(1.0f).setDuration(150L).start();
        this.resetTextView.animate().alpha(showRestText ? 1.0f : 0.0f).setDuration(150L).start();
        ViewPropertyAnimator animate = this.applyTextView.animate();
        if (showRestText) {
            f = 0.0f;
        }
        animate.alpha(f).setDuration(150L).start();
        this.progressView.animate().alpha(0.0f).setListener(new HideViewAfterAnimation(this.progressView)).setDuration(150L).start();
    }

    public void onAnimationStart() {
        Adapter adapter = this.adapter;
        if (adapter != null && adapter.items != null) {
            for (ChatThemeItem item : this.adapter.items) {
                item.themeIndex = this.forceDark ? 1 : 0;
            }
        }
        if (!this.isLightDarkChangeAnimation) {
            setItemsAnimationProgress(1.0f);
        }
    }

    public void onAnimationEnd() {
        this.isLightDarkChangeAnimation = false;
    }

    private void setDarkButtonColor(int color) {
        this.darkThemeDrawable.setLayerColor("Sunny.**", color);
        this.darkThemeDrawable.setLayerColor("Path.**", color);
        this.darkThemeDrawable.setLayerColor("Path 10.**", color);
        this.darkThemeDrawable.setLayerColor("Path 11.**", color);
    }

    private void setForceDark(boolean isDark, boolean playAnimation) {
        if (this.forceDark == isDark) {
            return;
        }
        this.forceDark = isDark;
        int i = 0;
        if (playAnimation) {
            RLottieDrawable rLottieDrawable = this.darkThemeDrawable;
            if (isDark) {
                i = rLottieDrawable.getFramesCount();
            }
            rLottieDrawable.setCustomEndFrame(i);
            RLottieImageView rLottieImageView = this.darkThemeView;
            if (rLottieImageView != null) {
                rLottieImageView.playAnimation();
                return;
            }
            return;
        }
        int frame = isDark ? this.darkThemeDrawable.getFramesCount() - 1 : 0;
        this.darkThemeDrawable.setCurrentFrame(frame, false, true);
        this.darkThemeDrawable.setCustomEndFrame(frame);
        RLottieImageView rLottieImageView2 = this.darkThemeView;
        if (rLottieImageView2 != null) {
            rLottieImageView2.invalidate();
        }
    }

    public void setItemsAnimationProgress(float progress) {
        for (int i = 0; i < this.adapter.getItemCount(); i++) {
            this.adapter.items.get(i).animationProgress = progress;
        }
    }

    private void applySelectedTheme() {
        Bulletin bulletin = null;
        EmojiThemes newTheme = this.selectedItem.chatTheme;
        if (newTheme.showAsDefaultStub) {
            newTheme = null;
        }
        ChatThemeItem chatThemeItem = this.selectedItem;
        if (chatThemeItem != null && newTheme != this.originalTheme) {
            EmojiThemes chatTheme = chatThemeItem.chatTheme;
            String emoticon = (chatTheme == null || chatTheme.showAsDefaultStub) ? null : chatTheme.getEmoticon();
            ChatThemeController.getInstance(this.currentAccount).setDialogTheme(this.chatActivity.getDialogId(), emoticon, true);
            if (chatTheme == null || chatTheme.showAsDefaultStub) {
                this.themeDelegate.setCurrentTheme(null, true, Boolean.valueOf(this.originalIsDark));
            } else {
                this.themeDelegate.setCurrentTheme(chatTheme, true, Boolean.valueOf(this.originalIsDark));
            }
            this.isApplyClicked = true;
            TLRPC.User user = this.chatActivity.getCurrentUser();
            if (user != null && !user.self) {
                boolean themeDisabled = false;
                if (TextUtils.isEmpty(emoticon)) {
                    themeDisabled = true;
                    emoticon = "❌";
                }
                TLRPC.Document document = emoticon != null ? MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(emoticon) : null;
                StickerSetBulletinLayout layout = new StickerSetBulletinLayout(getContext(), null, -1, document, this.chatActivity.getResourceProvider());
                layout.subtitleTextView.setVisibility(8);
                if (themeDisabled) {
                    layout.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ThemeAlsoDisabledForHint", R.string.ThemeAlsoDisabledForHint, user.first_name)));
                } else {
                    layout.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ThemeAlsoAppliedForHint", R.string.ThemeAlsoAppliedForHint, user.first_name)));
                }
                layout.titleTextView.setTypeface(null);
                bulletin = Bulletin.make(this.chatActivity, layout, (int) Bulletin.DURATION_LONG);
            }
        }
        dismiss();
        if (bulletin != null) {
            bulletin.show();
        }
    }

    private boolean hasChanges() {
        if (this.selectedItem == null) {
            return false;
        }
        EmojiThemes emojiThemes = this.originalTheme;
        String newEmoticon = null;
        String oldEmoticon = emojiThemes != null ? emojiThemes.getEmoticon() : null;
        if (TextUtils.isEmpty(oldEmoticon)) {
            oldEmoticon = "❌";
        }
        if (this.selectedItem.chatTheme != null) {
            newEmoticon = this.selectedItem.chatTheme.getEmoticon();
        }
        if (TextUtils.isEmpty(newEmoticon)) {
            newEmoticon = "❌";
        }
        return !ColorUtils$$ExternalSyntheticBackport0.m(oldEmoticon, newEmoticon);
    }

    /* loaded from: classes5.dex */
    public static class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final int currentAccount;
        private final int currentViewType;
        public List<ChatThemeItem> items;
        private final Theme.ResourcesProvider resourcesProvider;
        private WeakReference<ThemeSmallPreviewView> selectedViewRef;
        private int selectedItemPosition = -1;
        private HashMap<String, Theme.ThemeInfo> loadingThemes = new HashMap<>();
        private HashMap<Theme.ThemeInfo, String> loadingWallpapers = new HashMap<>();

        public Adapter(int currentAccount, Theme.ResourcesProvider resourcesProvider, int type) {
            this.currentViewType = type;
            this.resourcesProvider = resourcesProvider;
            this.currentAccount = currentAccount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new ThemeSmallPreviewView(parent.getContext(), this.currentAccount, this.resourcesProvider, this.currentViewType));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ThemeSmallPreviewView view = (ThemeSmallPreviewView) holder.itemView;
            Theme.ThemeInfo themeInfo = this.items.get(position).chatTheme.getThemeInfo(this.items.get(position).themeIndex);
            if (themeInfo != null && themeInfo.pathToFile != null && !themeInfo.previewParsed) {
                File file = new File(themeInfo.pathToFile);
                boolean fileExists = file.exists();
                if (fileExists) {
                    parseTheme(themeInfo);
                }
            }
            boolean animated = true;
            ChatThemeItem newItem = this.items.get(position);
            if (view.chatThemeItem == null || !view.chatThemeItem.chatTheme.getEmoticon().equals(newItem.chatTheme.getEmoticon()) || DrawerProfileCell.switchingTheme || view.lastThemeIndex != newItem.themeIndex) {
                animated = false;
            }
            boolean z = true;
            view.setFocusable(true);
            view.setEnabled(true);
            view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
            view.setItem(newItem, animated);
            if (position != this.selectedItemPosition) {
                z = false;
            }
            view.setSelected(z, animated);
            if (position == this.selectedItemPosition) {
                this.selectedViewRef = new WeakReference<>(view);
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:115:0x025d  */
        /* JADX WARN: Removed duplicated region for block: B:116:0x025e A[Catch: all -> 0x02a8, TryCatch #4 {all -> 0x02a8, blocks: (B:90:0x01f6, B:92:0x01fc, B:94:0x0202, B:96:0x0208, B:98:0x020e, B:102:0x0219, B:104:0x0227, B:107:0x0236, B:110:0x023e, B:112:0x024e, B:113:0x0256, B:114:0x025a, B:116:0x025e, B:119:0x0266, B:122:0x026e, B:125:0x0276, B:128:0x027e, B:131:0x0286, B:135:0x028f, B:137:0x0293, B:138:0x0296, B:139:0x0299, B:140:0x029c, B:141:0x02a0, B:142:0x02a4), top: B:194:0x01f6, inners: #9 }] */
        /* JADX WARN: Removed duplicated region for block: B:119:0x0266 A[Catch: all -> 0x02a8, TryCatch #4 {all -> 0x02a8, blocks: (B:90:0x01f6, B:92:0x01fc, B:94:0x0202, B:96:0x0208, B:98:0x020e, B:102:0x0219, B:104:0x0227, B:107:0x0236, B:110:0x023e, B:112:0x024e, B:113:0x0256, B:114:0x025a, B:116:0x025e, B:119:0x0266, B:122:0x026e, B:125:0x0276, B:128:0x027e, B:131:0x0286, B:135:0x028f, B:137:0x0293, B:138:0x0296, B:139:0x0299, B:140:0x029c, B:141:0x02a0, B:142:0x02a4), top: B:194:0x01f6, inners: #9 }] */
        /* JADX WARN: Removed duplicated region for block: B:122:0x026e A[Catch: all -> 0x02a8, TryCatch #4 {all -> 0x02a8, blocks: (B:90:0x01f6, B:92:0x01fc, B:94:0x0202, B:96:0x0208, B:98:0x020e, B:102:0x0219, B:104:0x0227, B:107:0x0236, B:110:0x023e, B:112:0x024e, B:113:0x0256, B:114:0x025a, B:116:0x025e, B:119:0x0266, B:122:0x026e, B:125:0x0276, B:128:0x027e, B:131:0x0286, B:135:0x028f, B:137:0x0293, B:138:0x0296, B:139:0x0299, B:140:0x029c, B:141:0x02a0, B:142:0x02a4), top: B:194:0x01f6, inners: #9 }] */
        /* JADX WARN: Removed duplicated region for block: B:125:0x0276 A[Catch: all -> 0x02a8, TryCatch #4 {all -> 0x02a8, blocks: (B:90:0x01f6, B:92:0x01fc, B:94:0x0202, B:96:0x0208, B:98:0x020e, B:102:0x0219, B:104:0x0227, B:107:0x0236, B:110:0x023e, B:112:0x024e, B:113:0x0256, B:114:0x025a, B:116:0x025e, B:119:0x0266, B:122:0x026e, B:125:0x0276, B:128:0x027e, B:131:0x0286, B:135:0x028f, B:137:0x0293, B:138:0x0296, B:139:0x0299, B:140:0x029c, B:141:0x02a0, B:142:0x02a4), top: B:194:0x01f6, inners: #9 }] */
        /* JADX WARN: Removed duplicated region for block: B:128:0x027e A[Catch: all -> 0x02a8, TryCatch #4 {all -> 0x02a8, blocks: (B:90:0x01f6, B:92:0x01fc, B:94:0x0202, B:96:0x0208, B:98:0x020e, B:102:0x0219, B:104:0x0227, B:107:0x0236, B:110:0x023e, B:112:0x024e, B:113:0x0256, B:114:0x025a, B:116:0x025e, B:119:0x0266, B:122:0x026e, B:125:0x0276, B:128:0x027e, B:131:0x0286, B:135:0x028f, B:137:0x0293, B:138:0x0296, B:139:0x0299, B:140:0x029c, B:141:0x02a0, B:142:0x02a4), top: B:194:0x01f6, inners: #9 }] */
        /* JADX WARN: Removed duplicated region for block: B:131:0x0286 A[Catch: all -> 0x02a8, TryCatch #4 {all -> 0x02a8, blocks: (B:90:0x01f6, B:92:0x01fc, B:94:0x0202, B:96:0x0208, B:98:0x020e, B:102:0x0219, B:104:0x0227, B:107:0x0236, B:110:0x023e, B:112:0x024e, B:113:0x0256, B:114:0x025a, B:116:0x025e, B:119:0x0266, B:122:0x026e, B:125:0x0276, B:128:0x027e, B:131:0x0286, B:135:0x028f, B:137:0x0293, B:138:0x0296, B:139:0x0299, B:140:0x029c, B:141:0x02a0, B:142:0x02a4), top: B:194:0x01f6, inners: #9 }] */
        /* JADX WARN: Removed duplicated region for block: B:136:0x0292  */
        /* JADX WARN: Removed duplicated region for block: B:137:0x0293 A[Catch: all -> 0x02a8, TryCatch #4 {all -> 0x02a8, blocks: (B:90:0x01f6, B:92:0x01fc, B:94:0x0202, B:96:0x0208, B:98:0x020e, B:102:0x0219, B:104:0x0227, B:107:0x0236, B:110:0x023e, B:112:0x024e, B:113:0x0256, B:114:0x025a, B:116:0x025e, B:119:0x0266, B:122:0x026e, B:125:0x0276, B:128:0x027e, B:131:0x0286, B:135:0x028f, B:137:0x0293, B:138:0x0296, B:139:0x0299, B:140:0x029c, B:141:0x02a0, B:142:0x02a4), top: B:194:0x01f6, inners: #9 }] */
        /* JADX WARN: Removed duplicated region for block: B:138:0x0296 A[Catch: all -> 0x02a8, TryCatch #4 {all -> 0x02a8, blocks: (B:90:0x01f6, B:92:0x01fc, B:94:0x0202, B:96:0x0208, B:98:0x020e, B:102:0x0219, B:104:0x0227, B:107:0x0236, B:110:0x023e, B:112:0x024e, B:113:0x0256, B:114:0x025a, B:116:0x025e, B:119:0x0266, B:122:0x026e, B:125:0x0276, B:128:0x027e, B:131:0x0286, B:135:0x028f, B:137:0x0293, B:138:0x0296, B:139:0x0299, B:140:0x029c, B:141:0x02a0, B:142:0x02a4), top: B:194:0x01f6, inners: #9 }] */
        /* JADX WARN: Removed duplicated region for block: B:139:0x0299 A[Catch: all -> 0x02a8, TryCatch #4 {all -> 0x02a8, blocks: (B:90:0x01f6, B:92:0x01fc, B:94:0x0202, B:96:0x0208, B:98:0x020e, B:102:0x0219, B:104:0x0227, B:107:0x0236, B:110:0x023e, B:112:0x024e, B:113:0x0256, B:114:0x025a, B:116:0x025e, B:119:0x0266, B:122:0x026e, B:125:0x0276, B:128:0x027e, B:131:0x0286, B:135:0x028f, B:137:0x0293, B:138:0x0296, B:139:0x0299, B:140:0x029c, B:141:0x02a0, B:142:0x02a4), top: B:194:0x01f6, inners: #9 }] */
        /* JADX WARN: Removed duplicated region for block: B:140:0x029c A[Catch: all -> 0x02a8, TryCatch #4 {all -> 0x02a8, blocks: (B:90:0x01f6, B:92:0x01fc, B:94:0x0202, B:96:0x0208, B:98:0x020e, B:102:0x0219, B:104:0x0227, B:107:0x0236, B:110:0x023e, B:112:0x024e, B:113:0x0256, B:114:0x025a, B:116:0x025e, B:119:0x0266, B:122:0x026e, B:125:0x0276, B:128:0x027e, B:131:0x0286, B:135:0x028f, B:137:0x0293, B:138:0x0296, B:139:0x0299, B:140:0x029c, B:141:0x02a0, B:142:0x02a4), top: B:194:0x01f6, inners: #9 }] */
        /* JADX WARN: Removed duplicated region for block: B:141:0x02a0 A[Catch: all -> 0x02a8, TryCatch #4 {all -> 0x02a8, blocks: (B:90:0x01f6, B:92:0x01fc, B:94:0x0202, B:96:0x0208, B:98:0x020e, B:102:0x0219, B:104:0x0227, B:107:0x0236, B:110:0x023e, B:112:0x024e, B:113:0x0256, B:114:0x025a, B:116:0x025e, B:119:0x0266, B:122:0x026e, B:125:0x0276, B:128:0x027e, B:131:0x0286, B:135:0x028f, B:137:0x0293, B:138:0x0296, B:139:0x0299, B:140:0x029c, B:141:0x02a0, B:142:0x02a4), top: B:194:0x01f6, inners: #9 }] */
        /* JADX WARN: Removed duplicated region for block: B:142:0x02a4 A[Catch: all -> 0x02a8, TRY_LEAVE, TryCatch #4 {all -> 0x02a8, blocks: (B:90:0x01f6, B:92:0x01fc, B:94:0x0202, B:96:0x0208, B:98:0x020e, B:102:0x0219, B:104:0x0227, B:107:0x0236, B:110:0x023e, B:112:0x024e, B:113:0x0256, B:114:0x025a, B:116:0x025e, B:119:0x0266, B:122:0x026e, B:125:0x0276, B:128:0x027e, B:131:0x0286, B:135:0x028f, B:137:0x0293, B:138:0x0296, B:139:0x0299, B:140:0x029c, B:141:0x02a0, B:142:0x02a4), top: B:194:0x01f6, inners: #9 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private boolean parseTheme(final org.telegram.ui.ActionBar.Theme.ThemeInfo r26) {
            /*
                Method dump skipped, instructions count: 938
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatThemeBottomSheet.Adapter.parseTheme(org.telegram.ui.ActionBar.Theme$ThemeInfo):boolean");
        }

        /* renamed from: lambda$parseTheme$1$org-telegram-ui-Components-ChatThemeBottomSheet$Adapter */
        public /* synthetic */ void m2533xe20fb76b(final Theme.ThemeInfo themeInfo, final TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatThemeBottomSheet$Adapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatThemeBottomSheet.Adapter.this.m2532xbc7bae6a(response, themeInfo);
                }
            });
        }

        /* renamed from: lambda$parseTheme$0$org-telegram-ui-Components-ChatThemeBottomSheet$Adapter */
        public /* synthetic */ void m2532xbc7bae6a(TLObject response, Theme.ThemeInfo themeInfo) {
            if (response instanceof TLRPC.TL_wallPaper) {
                TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) response;
                String name = FileLoader.getAttachFileName(wallPaper.document);
                if (!this.loadingThemes.containsKey(name)) {
                    this.loadingThemes.put(name, themeInfo);
                    FileLoader.getInstance(themeInfo.account).loadFile(wallPaper.document, wallPaper, 1, 1);
                    return;
                }
                return;
            }
            themeInfo.badWallpaper = true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            List<ChatThemeItem> list = this.items;
            if (list == null) {
                return 0;
            }
            return list.size();
        }

        public void setItems(List<ChatThemeItem> newItems) {
            this.items = newItems;
            notifyDataSetChanged();
        }

        public void setSelectedItem(int position) {
            int i = this.selectedItemPosition;
            if (i == position) {
                return;
            }
            if (i >= 0) {
                notifyItemChanged(i);
                WeakReference<ThemeSmallPreviewView> weakReference = this.selectedViewRef;
                ThemeSmallPreviewView view = weakReference == null ? null : weakReference.get();
                if (view != null) {
                    view.setSelected(false);
                }
            }
            this.selectedItemPosition = position;
            notifyItemChanged(position);
        }
    }

    /* loaded from: classes5.dex */
    public static class ChatThemeItem {
        public float animationProgress = 1.0f;
        public final EmojiThemes chatTheme;
        public Bitmap icon;
        public boolean isSelected;
        public Drawable previewDrawable;
        public int themeIndex;

        public ChatThemeItem(EmojiThemes chatTheme) {
            this.chatTheme = chatTheme;
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        String str;
        int i;
        super.show();
        TextView textView = this.resetTextView;
        if (this.themeDelegate.getCurrentTheme() == null) {
            i = R.string.DoNoSetTheme;
            str = "DoNoSetTheme";
        } else {
            i = R.string.ChatResetTheme;
            str = "ChatResetTheme";
        }
        textView.setText(LocaleController.getString(str, i));
    }
}
