package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EllipsizeSpanAnimator;
import org.telegram.ui.Components.FireworksEffect;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SnowflakesEffect;
import org.telegram.ui.Components.UndoView;
/* loaded from: classes4.dex */
public class ActionBar extends FrameLayout {
    private int actionBarColor;
    public ActionBarMenuOnItemClick actionBarMenuOnItemClick;
    private ActionBarMenu actionMode;
    private AnimatorSet actionModeAnimation;
    private int actionModeColor;
    private View actionModeExtraView;
    private View[] actionModeHidingViews;
    private View actionModeShowingView;
    private String actionModeTag;
    private View actionModeTop;
    private View actionModeTranslationView;
    private boolean actionModeVisible;
    private boolean addToContainer;
    private SimpleTextView additionalSubtitleTextView;
    private boolean allowOverlayTitle;
    private Drawable backButtonDrawable;
    private ImageView backButtonImageView;
    public Paint blurScrimPaint;
    boolean blurredBackground;
    private boolean castShadows;
    private boolean centerScale;
    private boolean clipContent;
    SizeNotifierFrameLayout contentView;
    EllipsizeSpanAnimator ellipsizeSpanAnimator;
    private int extraHeight;
    private FireworksEffect fireworksEffect;
    private Paint.FontMetricsInt fontMetricsInt;
    private boolean forceSkipTouches;
    private boolean fromBottom;
    private boolean ignoreLayoutRequest;
    private View.OnTouchListener interceptTouchEventListener;
    private boolean interceptTouches;
    private boolean isBackOverlayVisible;
    private boolean isMenuOffsetSuppressed;
    protected boolean isSearchFieldVisible;
    protected int itemsActionModeBackgroundColor;
    protected int itemsActionModeColor;
    protected int itemsBackgroundColor;
    protected int itemsColor;
    private CharSequence lastOverlayTitle;
    private Runnable lastRunnable;
    private CharSequence lastTitle;
    private boolean manualStart;
    private ActionBarMenu menu;
    private boolean occupyStatusBar;
    private boolean overlayTitleAnimation;
    boolean overlayTitleAnimationInProgress;
    private Object[] overlayTitleToSet;
    protected BaseFragment parentFragment;
    private Rect rect;
    Rect rectTmp;
    private final Theme.ResourcesProvider resourcesProvider;
    AnimatorSet searchVisibleAnimator;
    private SnowflakesEffect snowflakesEffect;
    private CharSequence subtitle;
    private SimpleTextView subtitleTextView;
    private boolean supportsHolidayImage;
    private Runnable titleActionRunnable;
    private boolean titleAnimationRunning;
    private int titleColorToSet;
    private boolean titleOverlayShown;
    private int titleRightMargin;
    private SimpleTextView[] titleTextView;

    /* loaded from: classes4.dex */
    public static class ActionBarMenuOnItemClick {
        public void onItemClick(int id) {
        }

        public boolean canOpenMenu() {
            return true;
        }
    }

    public ActionBar(Context context) {
        this(context, null);
    }

    public ActionBar(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.titleTextView = new SimpleTextView[2];
        this.occupyStatusBar = Build.VERSION.SDK_INT >= 21;
        this.addToContainer = true;
        this.interceptTouches = true;
        this.overlayTitleToSet = new Object[3];
        this.castShadows = true;
        this.titleColorToSet = 0;
        this.blurScrimPaint = new Paint();
        this.rectTmp = new Rect();
        this.ellipsizeSpanAnimator = new EllipsizeSpanAnimator(this);
        this.resourcesProvider = resourcesProvider;
        setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBar$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ActionBar.this.m1379lambda$new$0$orgtelegramuiActionBarActionBar(view);
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-ActionBar-ActionBar */
    public /* synthetic */ void m1379lambda$new$0$orgtelegramuiActionBarActionBar(View v) {
        Runnable runnable;
        if (!isSearchFieldVisible() && (runnable = this.titleActionRunnable) != null) {
            runnable.run();
        }
    }

    private void createBackButtonImage() {
        if (this.backButtonImageView != null) {
            return;
        }
        ImageView imageView = new ImageView(getContext());
        this.backButtonImageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
        if (this.itemsColor != 0) {
            this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(this.itemsColor, PorterDuff.Mode.MULTIPLY));
        }
        this.backButtonImageView.setPadding(AndroidUtilities.dp(1.0f), 0, 0, 0);
        addView(this.backButtonImageView, LayoutHelper.createFrame(54, 54, 51));
        this.backButtonImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBar$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ActionBar.this.m1378x3228e969(view);
            }
        });
        this.backButtonImageView.setContentDescription(LocaleController.getString("AccDescrGoBack", R.string.AccDescrGoBack));
    }

    /* renamed from: lambda$createBackButtonImage$1$org-telegram-ui-ActionBar-ActionBar */
    public /* synthetic */ void m1378x3228e969(View v) {
        if (!this.actionModeVisible && this.isSearchFieldVisible) {
            closeSearchField();
            return;
        }
        ActionBarMenuOnItemClick actionBarMenuOnItemClick = this.actionBarMenuOnItemClick;
        if (actionBarMenuOnItemClick != null) {
            actionBarMenuOnItemClick.onItemClick(-1);
        }
    }

    public Drawable getBackButtonDrawable() {
        return this.backButtonDrawable;
    }

    public void setBackButtonDrawable(Drawable drawable) {
        if (this.backButtonImageView == null) {
            createBackButtonImage();
        }
        this.backButtonImageView.setVisibility(drawable == null ? 8 : 0);
        ImageView imageView = this.backButtonImageView;
        this.backButtonDrawable = drawable;
        imageView.setImageDrawable(drawable);
        if (drawable instanceof BackDrawable) {
            BackDrawable backDrawable = (BackDrawable) drawable;
            backDrawable.setRotation(isActionModeShowed() ? 1.0f : 0.0f, false);
            backDrawable.setRotatedColor(this.itemsActionModeColor);
            backDrawable.setColor(this.itemsColor);
        } else if (drawable instanceof MenuDrawable) {
            MenuDrawable menuDrawable = (MenuDrawable) drawable;
            menuDrawable.setBackColor(this.actionBarColor);
            menuDrawable.setIconColor(this.itemsColor);
        }
    }

    public void setBackButtonContentDescription(CharSequence description) {
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            imageView.setContentDescription(description);
        }
    }

    public void setSupportsHolidayImage(boolean value) {
        this.supportsHolidayImage = value;
        if (value) {
            this.fontMetricsInt = new Paint.FontMetricsInt();
            this.rect = new Rect();
        }
        invalidate();
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Drawable drawable;
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL && ev.getAction() == 0 && (drawable = Theme.getCurrentHolidayDrawable()) != null && drawable.getBounds().contains((int) ev.getX(), (int) ev.getY())) {
            this.manualStart = true;
            if (this.snowflakesEffect == null) {
                this.fireworksEffect = null;
                this.snowflakesEffect = new SnowflakesEffect(0);
                this.titleTextView[0].invalidate();
                invalidate();
            } else {
                this.snowflakesEffect = null;
                this.fireworksEffect = new FireworksEffect();
                this.titleTextView[0].invalidate();
                invalidate();
            }
        }
        View.OnTouchListener onTouchListener = this.interceptTouchEventListener;
        return (onTouchListener != null && onTouchListener.onTouch(this, ev)) || super.onInterceptTouchEvent(ev);
    }

    public boolean shouldClipChild(View child) {
        if (this.clipContent) {
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if (child == simpleTextViewArr[0] || child == simpleTextViewArr[1] || child == this.subtitleTextView || child == this.menu || child == this.backButtonImageView || child == this.additionalSubtitleTextView) {
                return true;
            }
        }
        return false;
    }

    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Drawable drawable;
        boolean clip = shouldClipChild(child);
        if (clip) {
            canvas.save();
            canvas.clipRect(0.0f, (-getTranslationY()) + (this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0), getMeasuredWidth(), getMeasuredHeight());
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL) {
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if ((child == simpleTextViewArr[0] || child == simpleTextViewArr[1]) && (drawable = Theme.getCurrentHolidayDrawable()) != null) {
                SimpleTextView titleView = (SimpleTextView) child;
                if (titleView.getVisibility() == 0 && (titleView.getText() instanceof String)) {
                    TextPaint textPaint = titleView.getTextPaint();
                    textPaint.getFontMetricsInt(this.fontMetricsInt);
                    textPaint.getTextBounds((String) titleView.getText(), 0, 1, this.rect);
                    int x = titleView.getTextStartX() + Theme.getCurrentHolidayDrawableXOffset() + ((this.rect.width() - (drawable.getIntrinsicWidth() + Theme.getCurrentHolidayDrawableXOffset())) / 2);
                    int y = titleView.getTextStartY() + Theme.getCurrentHolidayDrawableYOffset() + ((int) Math.ceil((titleView.getTextHeight() - this.rect.height()) / 2.0f));
                    drawable.setBounds(x, y - drawable.getIntrinsicHeight(), drawable.getIntrinsicWidth() + x, y);
                    drawable.setAlpha((int) (titleView.getAlpha() * 255.0f));
                    drawable.draw(canvas);
                    if (this.overlayTitleAnimationInProgress) {
                        child.invalidate();
                        invalidate();
                    }
                }
                if (Theme.canStartHolidayAnimation()) {
                    if (this.snowflakesEffect == null) {
                        this.snowflakesEffect = new SnowflakesEffect(0);
                    }
                } else if (!this.manualStart && this.snowflakesEffect != null) {
                    this.snowflakesEffect = null;
                }
                SnowflakesEffect snowflakesEffect = this.snowflakesEffect;
                if (snowflakesEffect != null) {
                    snowflakesEffect.onDraw(this, canvas);
                } else {
                    FireworksEffect fireworksEffect = this.fireworksEffect;
                    if (fireworksEffect != null) {
                        fireworksEffect.onDraw(this, canvas);
                    }
                }
            }
        }
        if (clip) {
            canvas.restore();
        }
        return result;
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        if (this.clipContent) {
            invalidate();
        }
    }

    public void setBackButtonImage(int resource) {
        if (this.backButtonImageView == null) {
            createBackButtonImage();
        }
        this.backButtonImageView.setVisibility(resource == 0 ? 8 : 0);
        this.backButtonImageView.setImageResource(resource);
    }

    private void createSubtitleTextView() {
        if (this.subtitleTextView != null) {
            return;
        }
        SimpleTextView simpleTextView = new SimpleTextView(getContext());
        this.subtitleTextView = simpleTextView;
        simpleTextView.setGravity(3);
        this.subtitleTextView.setVisibility(8);
        this.subtitleTextView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubtitle));
        addView(this.subtitleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
    }

    public void createAdditionalSubtitleTextView() {
        if (this.additionalSubtitleTextView != null) {
            return;
        }
        SimpleTextView simpleTextView = new SimpleTextView(getContext());
        this.additionalSubtitleTextView = simpleTextView;
        simpleTextView.setGravity(3);
        this.additionalSubtitleTextView.setVisibility(8);
        this.additionalSubtitleTextView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubtitle));
        addView(this.additionalSubtitleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
    }

    public SimpleTextView getAdditionalSubtitleTextView() {
        return this.additionalSubtitleTextView;
    }

    public void setAddToContainer(boolean value) {
        this.addToContainer = value;
    }

    public boolean shouldAddToContainer() {
        return this.addToContainer;
    }

    public void setClipContent(boolean value) {
        this.clipContent = value;
    }

    public void setSubtitle(CharSequence value) {
        if (value != null && this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        if (this.subtitleTextView != null) {
            boolean isEmpty = TextUtils.isEmpty(value);
            this.subtitleTextView.setVisibility((isEmpty || this.isSearchFieldVisible) ? 8 : 0);
            this.subtitleTextView.setAlpha(1.0f);
            if (!isEmpty) {
                this.subtitleTextView.setText(value);
            }
            this.subtitle = value;
        }
    }

    private void createTitleTextView(int i) {
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[i] != null) {
            return;
        }
        simpleTextViewArr[i] = new SimpleTextView(getContext());
        this.titleTextView[i].setGravity(3);
        int i2 = this.titleColorToSet;
        if (i2 != 0) {
            this.titleTextView[i].setTextColor(i2);
        } else {
            this.titleTextView[i].setTextColor(getThemedColor(Theme.key_actionBarDefaultTitle));
        }
        this.titleTextView[i].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        addView(this.titleTextView[i], 0, LayoutHelper.createFrame(-2, -2, 51));
    }

    public void setTitleRightMargin(int value) {
        this.titleRightMargin = value;
    }

    public void setTitle(CharSequence value) {
        if (value != null && this.titleTextView[0] == null) {
            createTitleTextView(0);
        }
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[0] != null) {
            this.lastTitle = value;
            simpleTextViewArr[0].setVisibility((value == null || this.isSearchFieldVisible) ? 4 : 0);
            this.titleTextView[0].setText(value);
        }
        this.fromBottom = false;
    }

    public void setTitleColor(int color) {
        if (this.titleTextView[0] == null) {
            createTitleTextView(0);
        }
        this.titleColorToSet = color;
        this.titleTextView[0].setTextColor(color);
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[1] != null) {
            simpleTextViewArr[1].setTextColor(color);
        }
    }

    public void setSubtitleColor(int color) {
        if (this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        this.subtitleTextView.setTextColor(color);
    }

    public void setTitleScrollNonFitText(boolean b) {
        this.titleTextView[0].setScrollNonFitText(b);
    }

    public void setPopupItemsColor(int color, boolean icon, boolean forActionMode) {
        ActionBarMenu actionBarMenu;
        ActionBarMenu actionBarMenu2;
        if (forActionMode && (actionBarMenu2 = this.actionMode) != null) {
            actionBarMenu2.setPopupItemsColor(color, icon);
        } else if (!forActionMode && (actionBarMenu = this.menu) != null) {
            actionBarMenu.setPopupItemsColor(color, icon);
        }
    }

    public void setPopupItemsSelectorColor(int color, boolean forActionMode) {
        ActionBarMenu actionBarMenu;
        ActionBarMenu actionBarMenu2;
        if (forActionMode && (actionBarMenu2 = this.actionMode) != null) {
            actionBarMenu2.setPopupItemsSelectorColor(color);
        } else if (!forActionMode && (actionBarMenu = this.menu) != null) {
            actionBarMenu.setPopupItemsSelectorColor(color);
        }
    }

    public void setPopupBackgroundColor(int color, boolean forActionMode) {
        ActionBarMenu actionBarMenu;
        ActionBarMenu actionBarMenu2;
        if (forActionMode && (actionBarMenu2 = this.actionMode) != null) {
            actionBarMenu2.redrawPopup(color);
        } else if (!forActionMode && (actionBarMenu = this.menu) != null) {
            actionBarMenu.redrawPopup(color);
        }
    }

    public SimpleTextView getSubtitleTextView() {
        return this.subtitleTextView;
    }

    public SimpleTextView getTitleTextView() {
        return this.titleTextView[0];
    }

    public String getTitle() {
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[0] == null) {
            return null;
        }
        return simpleTextViewArr[0].getText().toString();
    }

    public String getSubtitle() {
        CharSequence charSequence;
        if (this.subtitleTextView == null || (charSequence = this.subtitle) == null) {
            return null;
        }
        return charSequence.toString();
    }

    public ActionBarMenu createMenu() {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            return actionBarMenu;
        }
        ActionBarMenu actionBarMenu2 = new ActionBarMenu(getContext(), this);
        this.menu = actionBarMenu2;
        addView(actionBarMenu2, 0, LayoutHelper.createFrame(-2, -1, 5));
        return this.menu;
    }

    public void setActionBarMenuOnItemClick(ActionBarMenuOnItemClick listener) {
        this.actionBarMenuOnItemClick = listener;
    }

    public ActionBarMenuOnItemClick getActionBarMenuOnItemClick() {
        return this.actionBarMenuOnItemClick;
    }

    public ImageView getBackButton() {
        return this.backButtonImageView;
    }

    public ActionBarMenu createActionMode() {
        return createActionMode(true, null);
    }

    public boolean actionModeIsExist(String tag) {
        if (this.actionMode != null) {
            String str = this.actionModeTag;
            if (str == null && tag == null) {
                return true;
            }
            if (str != null && str.equals(tag)) {
                return true;
            }
            return false;
        }
        return false;
    }

    public ActionBarMenu createActionMode(boolean needTop, String tag) {
        if (actionModeIsExist(tag)) {
            return this.actionMode;
        }
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            removeView(actionBarMenu);
            this.actionMode = null;
        }
        this.actionModeTag = tag;
        ActionBarMenu actionBarMenu2 = new ActionBarMenu(getContext(), this) { // from class: org.telegram.ui.ActionBar.ActionBar.1
            @Override // android.view.View
            public void setBackgroundColor(int color) {
                ActionBar.this.actionModeColor = color;
                if (!ActionBar.this.blurredBackground) {
                    super.setBackgroundColor(ActionBar.this.actionModeColor);
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                if (ActionBar.this.blurredBackground && this.drawBlur) {
                    ActionBar.this.rectTmp.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    ActionBar.this.blurScrimPaint.setColor(ActionBar.this.actionModeColor);
                    ActionBar.this.contentView.drawBlurRect(canvas, 0.0f, ActionBar.this.rectTmp, ActionBar.this.blurScrimPaint, true);
                }
                super.dispatchDraw(canvas);
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                if (ActionBar.this.contentView != null) {
                    ActionBar.this.contentView.blurBehindViews.add(this);
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                if (ActionBar.this.contentView != null) {
                    ActionBar.this.contentView.blurBehindViews.remove(this);
                }
            }
        };
        this.actionMode = actionBarMenu2;
        actionBarMenu2.isActionMode = true;
        this.actionMode.setClickable(true);
        this.actionMode.setBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefault));
        addView(this.actionMode, indexOfChild(this.backButtonImageView));
        this.actionMode.setPadding(0, this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.actionMode.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -1;
        layoutParams.bottomMargin = this.extraHeight;
        layoutParams.gravity = 5;
        this.actionMode.setLayoutParams(layoutParams);
        this.actionMode.setVisibility(4);
        return this.actionMode;
    }

    public void showActionMode() {
        showActionMode(true, null, null, null, null, null, 0);
    }

    public void showActionMode(boolean animated) {
        showActionMode(animated, null, null, null, null, null, 0);
    }

    public void showActionMode(boolean animated, View extraView, View showingView, View[] hidingViews, final boolean[] hideView, View translationView, int translation) {
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null && !this.actionModeVisible) {
            this.actionModeVisible = true;
            if (animated) {
                ArrayList<Animator> animators = new ArrayList<>();
                animators.add(ObjectAnimator.ofFloat(this.actionMode, View.ALPHA, 0.0f, 1.0f));
                if (hidingViews != null) {
                    for (int a = 0; a < hidingViews.length; a++) {
                        if (hidingViews[a] != null) {
                            animators.add(ObjectAnimator.ofFloat(hidingViews[a], View.ALPHA, 1.0f, 0.0f));
                        }
                    }
                }
                if (showingView != null) {
                    animators.add(ObjectAnimator.ofFloat(showingView, View.ALPHA, 0.0f, 1.0f));
                }
                if (translationView != null) {
                    animators.add(ObjectAnimator.ofFloat(translationView, View.TRANSLATION_Y, translation));
                    this.actionModeTranslationView = translationView;
                }
                this.actionModeExtraView = extraView;
                this.actionModeShowingView = showingView;
                this.actionModeHidingViews = hidingViews;
                if (this.occupyStatusBar && this.actionModeTop != null && !SharedConfig.noStatusBar) {
                    animators.add(ObjectAnimator.ofFloat(this.actionModeTop, View.ALPHA, 0.0f, 1.0f));
                }
                if (SharedConfig.noStatusBar) {
                    if (ColorUtils.calculateLuminance(this.actionModeColor) >= 0.699999988079071d) {
                        AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), true);
                    } else {
                        AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), false);
                    }
                }
                AnimatorSet animatorSet = this.actionModeAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.actionModeAnimation = animatorSet2;
                animatorSet2.playTogether(animators);
                this.actionModeAnimation.setDuration(200L);
                this.actionModeAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationStart(Animator animation) {
                        ActionBar.this.actionMode.setVisibility(0);
                        if (ActionBar.this.occupyStatusBar && ActionBar.this.actionModeTop != null && !SharedConfig.noStatusBar) {
                            ActionBar.this.actionModeTop.setVisibility(0);
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        boolean[] zArr;
                        if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animation)) {
                            ActionBar.this.actionModeAnimation = null;
                            if (ActionBar.this.titleTextView[0] != null) {
                                ActionBar.this.titleTextView[0].setVisibility(4);
                            }
                            if (ActionBar.this.subtitleTextView != null && !TextUtils.isEmpty(ActionBar.this.subtitle)) {
                                ActionBar.this.subtitleTextView.setVisibility(4);
                            }
                            if (ActionBar.this.menu != null) {
                                ActionBar.this.menu.setVisibility(4);
                            }
                            if (ActionBar.this.actionModeHidingViews != null) {
                                for (int a2 = 0; a2 < ActionBar.this.actionModeHidingViews.length; a2++) {
                                    if (ActionBar.this.actionModeHidingViews[a2] != null && ((zArr = hideView) == null || a2 >= zArr.length || zArr[a2])) {
                                        ActionBar.this.actionModeHidingViews[a2].setVisibility(4);
                                    }
                                }
                            }
                        }
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animation) {
                        if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animation)) {
                            ActionBar.this.actionModeAnimation = null;
                        }
                    }
                });
                this.actionModeAnimation.start();
                ImageView imageView = this.backButtonImageView;
                if (imageView != null) {
                    Drawable drawable = imageView.getDrawable();
                    if (drawable instanceof BackDrawable) {
                        ((BackDrawable) drawable).setRotation(1.0f, true);
                    }
                    this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsActionModeBackgroundColor));
                    return;
                }
                return;
            }
            actionBarMenu.setAlpha(1.0f);
            if (hidingViews != null) {
                for (int a2 = 0; a2 < hidingViews.length; a2++) {
                    if (hidingViews[a2] != null) {
                        hidingViews[a2].setAlpha(0.0f);
                    }
                }
            }
            if (showingView != null) {
                showingView.setAlpha(1.0f);
            }
            if (translationView != null) {
                translationView.setTranslationY(translation);
                this.actionModeTranslationView = translationView;
            }
            this.actionModeExtraView = extraView;
            this.actionModeShowingView = showingView;
            this.actionModeHidingViews = hidingViews;
            if (this.occupyStatusBar && this.actionModeTop != null && !SharedConfig.noStatusBar) {
                this.actionModeTop.setAlpha(1.0f);
            }
            if (SharedConfig.noStatusBar) {
                if (ColorUtils.calculateLuminance(this.actionModeColor) >= 0.699999988079071d) {
                    AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), true);
                } else {
                    AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), false);
                }
            }
            this.actionMode.setVisibility(0);
            if (this.occupyStatusBar && this.actionModeTop != null && !SharedConfig.noStatusBar) {
                this.actionModeTop.setVisibility(0);
            }
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if (simpleTextViewArr[0] != null) {
                simpleTextViewArr[0].setVisibility(4);
            }
            if (this.subtitleTextView != null && !TextUtils.isEmpty(this.subtitle)) {
                this.subtitleTextView.setVisibility(4);
            }
            ActionBarMenu actionBarMenu2 = this.menu;
            if (actionBarMenu2 != null) {
                actionBarMenu2.setVisibility(4);
            }
            if (this.actionModeHidingViews != null) {
                int a3 = 0;
                while (true) {
                    View[] viewArr = this.actionModeHidingViews;
                    if (a3 >= viewArr.length) {
                        break;
                    }
                    if (viewArr[a3] != null && (hideView == null || a3 >= hideView.length || hideView[a3])) {
                        viewArr[a3].setVisibility(4);
                    }
                    a3++;
                }
            }
            ImageView imageView2 = this.backButtonImageView;
            if (imageView2 != null) {
                Drawable drawable2 = imageView2.getDrawable();
                if (drawable2 instanceof BackDrawable) {
                    ((BackDrawable) drawable2).setRotation(1.0f, false);
                }
                this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsActionModeBackgroundColor));
            }
        }
    }

    public void hideActionMode() {
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu == null || !this.actionModeVisible) {
            return;
        }
        actionBarMenu.hideAllPopupMenus();
        this.actionModeVisible = false;
        ArrayList<Animator> animators = new ArrayList<>();
        animators.add(ObjectAnimator.ofFloat(this.actionMode, View.ALPHA, 0.0f));
        if (this.actionModeHidingViews != null) {
            int a = 0;
            while (true) {
                View[] viewArr = this.actionModeHidingViews;
                if (a >= viewArr.length) {
                    break;
                }
                if (viewArr[a] != null) {
                    viewArr[a].setVisibility(0);
                    animators.add(ObjectAnimator.ofFloat(this.actionModeHidingViews[a], View.ALPHA, 1.0f));
                }
                a++;
            }
        }
        View view = this.actionModeTranslationView;
        if (view != null) {
            animators.add(ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0.0f));
            this.actionModeTranslationView = null;
        }
        View view2 = this.actionModeShowingView;
        if (view2 != null) {
            animators.add(ObjectAnimator.ofFloat(view2, View.ALPHA, 0.0f));
        }
        if (this.occupyStatusBar && this.actionModeTop != null && !SharedConfig.noStatusBar) {
            animators.add(ObjectAnimator.ofFloat(this.actionModeTop, View.ALPHA, 0.0f));
        }
        if (SharedConfig.noStatusBar) {
            int i = this.actionBarColor;
            if (i == 0) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
            } else if (ColorUtils.calculateLuminance(i) < 0.699999988079071d) {
                AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), false);
            } else {
                AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), true);
            }
        }
        AnimatorSet animatorSet = this.actionModeAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.actionModeAnimation = animatorSet2;
        animatorSet2.playTogether(animators);
        this.actionModeAnimation.setDuration(200L);
        this.actionModeAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animation)) {
                    ActionBar.this.actionModeAnimation = null;
                    ActionBar.this.actionMode.setVisibility(4);
                    if (ActionBar.this.occupyStatusBar && ActionBar.this.actionModeTop != null && !SharedConfig.noStatusBar) {
                        ActionBar.this.actionModeTop.setVisibility(4);
                    }
                    if (ActionBar.this.actionModeExtraView != null) {
                        ActionBar.this.actionModeExtraView.setVisibility(4);
                    }
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                if (ActionBar.this.actionModeAnimation != null && ActionBar.this.actionModeAnimation.equals(animation)) {
                    ActionBar.this.actionModeAnimation = null;
                }
            }
        });
        this.actionModeAnimation.start();
        if (!this.isSearchFieldVisible) {
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if (simpleTextViewArr[0] != null) {
                simpleTextViewArr[0].setVisibility(0);
            }
            if (this.subtitleTextView != null && !TextUtils.isEmpty(this.subtitle)) {
                this.subtitleTextView.setVisibility(0);
            }
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (actionBarMenu2 != null) {
            actionBarMenu2.setVisibility(0);
        }
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof BackDrawable) {
                ((BackDrawable) drawable).setRotation(0.0f, true);
            }
            this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
        }
    }

    public void showActionModeTop() {
        if (this.occupyStatusBar && this.actionModeTop == null) {
            View view = new View(getContext());
            this.actionModeTop = view;
            view.setBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefaultTop));
            addView(this.actionModeTop);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.actionModeTop.getLayoutParams();
            layoutParams.height = AndroidUtilities.statusBarHeight;
            layoutParams.width = -1;
            layoutParams.gravity = 51;
            this.actionModeTop.setLayoutParams(layoutParams);
        }
    }

    public void setActionModeTopColor(int color) {
        View view = this.actionModeTop;
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }

    public void setSearchTextColor(int color, boolean placeholder) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setSearchTextColor(color, placeholder);
        }
    }

    public void setSearchCursorColor(int color) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setSearchCursorColor(color);
        }
    }

    public void setActionModeColor(int color) {
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setBackgroundColor(color);
        }
    }

    public void setActionModeOverrideColor(int color) {
        this.actionModeColor = color;
    }

    @Override // android.view.View
    public void setBackgroundColor(int color) {
        this.actionBarColor = color;
        super.setBackgroundColor(color);
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof MenuDrawable) {
                ((MenuDrawable) drawable).setBackColor(color);
            }
        }
    }

    public boolean isActionModeShowed() {
        return this.actionMode != null && this.actionModeVisible;
    }

    public boolean isActionModeShowed(String tag) {
        String str;
        return this.actionMode != null && this.actionModeVisible && (((str = this.actionModeTag) == null && tag == null) || (str != null && str.equals(tag)));
    }

    public void onSearchFieldVisibilityChanged(final boolean visible) {
        float f;
        this.isSearchFieldVisible = visible;
        AnimatorSet animatorSet = this.searchVisibleAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.searchVisibleAnimator = new AnimatorSet();
        final ArrayList<View> viewsToHide = new ArrayList<>();
        View[] viewArr = this.titleTextView;
        if (viewArr[0] != null) {
            viewsToHide.add(viewArr[0]);
        }
        if (this.subtitleTextView != null && !TextUtils.isEmpty(this.subtitle)) {
            viewsToHide.add(this.subtitleTextView);
            this.subtitleTextView.setVisibility(visible ? 4 : 0);
        }
        int i = 0;
        while (true) {
            f = 0.0f;
            float f2 = 1.0f;
            if (i >= viewsToHide.size()) {
                break;
            }
            View view = viewsToHide.get(i);
            if (!visible) {
                view.setVisibility(0);
                view.setAlpha(0.0f);
                view.setScaleX(0.95f);
                view.setScaleY(0.95f);
            }
            AnimatorSet animatorSet2 = this.searchVisibleAnimator;
            Animator[] animatorArr = new Animator[1];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            if (!visible) {
                f = 1.0f;
            }
            fArr[0] = f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet2.playTogether(animatorArr);
            AnimatorSet animatorSet3 = this.searchVisibleAnimator;
            Animator[] animatorArr2 = new Animator[1];
            Property property2 = View.SCALE_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = visible ? 0.95f : 1.0f;
            animatorArr2[0] = ObjectAnimator.ofFloat(view, property2, fArr2);
            animatorSet3.playTogether(animatorArr2);
            AnimatorSet animatorSet4 = this.searchVisibleAnimator;
            Animator[] animatorArr3 = new Animator[1];
            Property property3 = View.SCALE_X;
            float[] fArr3 = new float[1];
            if (visible) {
                f2 = 0.95f;
            }
            fArr3[0] = f2;
            animatorArr3[0] = ObjectAnimator.ofFloat(view, property3, fArr3);
            animatorSet4.playTogether(animatorArr3);
            i++;
        }
        this.centerScale = true;
        requestLayout();
        this.searchVisibleAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                for (int i2 = 0; i2 < viewsToHide.size(); i2++) {
                    View view2 = (View) viewsToHide.get(i2);
                    if (visible) {
                        view2.setVisibility(4);
                        view2.setAlpha(0.0f);
                    } else {
                        view2.setAlpha(1.0f);
                    }
                }
                if (visible) {
                    if (ActionBar.this.titleTextView[0] != null) {
                        ActionBar.this.titleTextView[0].setVisibility(8);
                    }
                    if (ActionBar.this.titleTextView[1] != null) {
                        ActionBar.this.titleTextView[1].setVisibility(8);
                    }
                }
            }
        });
        this.searchVisibleAnimator.setDuration(150L).start();
        Drawable drawable = this.backButtonImageView.getDrawable();
        if (drawable instanceof MenuDrawable) {
            MenuDrawable menuDrawable = (MenuDrawable) drawable;
            menuDrawable.setRotateToBack(true);
            if (visible) {
                f = 1.0f;
            }
            menuDrawable.setRotation(f, true);
        }
    }

    public void setInterceptTouches(boolean value) {
        this.interceptTouches = value;
    }

    public void setInterceptTouchEventListener(View.OnTouchListener listener) {
        this.interceptTouchEventListener = listener;
    }

    public void setExtraHeight(int value) {
        this.extraHeight = value;
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) actionBarMenu.getLayoutParams();
            layoutParams.bottomMargin = this.extraHeight;
            this.actionMode.setLayoutParams(layoutParams);
        }
    }

    public void closeSearchField() {
        closeSearchField(true);
    }

    public void closeSearchField(boolean closeKeyboard) {
        ActionBarMenu actionBarMenu;
        if (!this.isSearchFieldVisible || (actionBarMenu = this.menu) == null) {
            return;
        }
        actionBarMenu.closeSearchField(closeKeyboard);
    }

    public void openSearchField(String text, boolean animated) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu == null || text == null) {
            return;
        }
        boolean z = this.isSearchFieldVisible;
        actionBarMenu.openSearchField(!z, !z, text, animated);
    }

    public void openSearchField(boolean animated) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu == null) {
            return;
        }
        actionBarMenu.openSearchField(!this.isSearchFieldVisible, false, "", animated);
    }

    public void setSearchFilter(FiltersView.MediaFilterData filter) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setFilter(filter);
        }
    }

    public void setSearchFieldText(String text) {
        this.menu.setSearchFieldText(text);
    }

    public void onSearchPressed() {
        this.menu.onSearchPressed();
    }

    @Override // android.view.View
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            imageView.setEnabled(enabled);
        }
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setEnabled(enabled);
        }
        ActionBarMenu actionBarMenu2 = this.actionMode;
        if (actionBarMenu2 != null) {
            actionBarMenu2.setEnabled(enabled);
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayoutRequest) {
            return;
        }
        super.requestLayout();
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int textLeft;
        SimpleTextView simpleTextView;
        SimpleTextView simpleTextView2;
        int menuWidth;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        View.MeasureSpec.getSize(heightMeasureSpec);
        int actionBarHeight = getCurrentActionBarHeight();
        int actionBarHeightSpec = View.MeasureSpec.makeMeasureSpec(actionBarHeight, C.BUFFER_FLAG_ENCRYPTED);
        this.ignoreLayoutRequest = true;
        View view = this.actionModeTop;
        if (view != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            layoutParams.height = AndroidUtilities.statusBarHeight;
        }
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setPadding(0, this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        }
        this.ignoreLayoutRequest = false;
        setMeasuredDimension(width, (this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0) + actionBarHeight + this.extraHeight);
        ImageView imageView = this.backButtonImageView;
        if (imageView != null && imageView.getVisibility() != 8) {
            this.backButtonImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0f), C.BUFFER_FLAG_ENCRYPTED), actionBarHeightSpec);
            textLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
        } else {
            textLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26.0f : 18.0f);
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        int i = Integer.MIN_VALUE;
        if (actionBarMenu2 != null && actionBarMenu2.getVisibility() != 8) {
            boolean searchFieldIsVisible = this.menu.searchFieldVisible();
            if (!searchFieldIsVisible || this.isSearchFieldVisible) {
                if (this.isSearchFieldVisible) {
                    menuWidth = View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f), C.BUFFER_FLAG_ENCRYPTED);
                    if (!this.isMenuOffsetSuppressed) {
                        this.menu.translateXItems(0.0f);
                    }
                } else {
                    menuWidth = View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE);
                    if (!this.isMenuOffsetSuppressed) {
                        this.menu.translateXItems(0.0f);
                    }
                }
            } else {
                int menuWidth2 = View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE);
                this.menu.measure(menuWidth2, actionBarHeightSpec);
                int itemsWidth = this.menu.getItemsMeasuredWidth();
                menuWidth = View.MeasureSpec.makeMeasureSpec((width - AndroidUtilities.dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f)) + this.menu.getItemsMeasuredWidth(), C.BUFFER_FLAG_ENCRYPTED);
                if (!this.isMenuOffsetSuppressed) {
                    this.menu.translateXItems(-itemsWidth);
                }
            }
            this.menu.measure(menuWidth, actionBarHeightSpec);
        }
        int i2 = 0;
        while (i2 < 2) {
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if ((simpleTextViewArr[0] != null && simpleTextViewArr[0].getVisibility() != 8) || ((simpleTextView2 = this.subtitleTextView) != null && simpleTextView2.getVisibility() != 8)) {
                ActionBarMenu actionBarMenu3 = this.menu;
                int availableWidth = (((width - (actionBarMenu3 != null ? actionBarMenu3.getMeasuredWidth() : 0)) - AndroidUtilities.dp(16.0f)) - textLeft) - this.titleRightMargin;
                boolean z = this.fromBottom;
                if (((z && i2 == 0) || (!z && i2 == 1)) && this.overlayTitleAnimation && this.titleAnimationRunning) {
                    this.titleTextView[i2].setTextSize((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 20 : 18);
                } else {
                    SimpleTextView[] simpleTextViewArr2 = this.titleTextView;
                    if (simpleTextViewArr2[0] != null && simpleTextViewArr2[0].getVisibility() != 8 && (simpleTextView = this.subtitleTextView) != null && simpleTextView.getVisibility() != 8) {
                        SimpleTextView[] simpleTextViewArr3 = this.titleTextView;
                        if (simpleTextViewArr3[i2] != null) {
                            simpleTextViewArr3[i2].setTextSize(AndroidUtilities.isTablet() ? 20 : 18);
                        }
                        this.subtitleTextView.setTextSize(AndroidUtilities.isTablet() ? 16 : 14);
                        SimpleTextView simpleTextView3 = this.additionalSubtitleTextView;
                        if (simpleTextView3 != null) {
                            simpleTextView3.setTextSize(AndroidUtilities.isTablet() ? 16 : 14);
                        }
                    } else {
                        SimpleTextView[] simpleTextViewArr4 = this.titleTextView;
                        if (simpleTextViewArr4[i2] != null && simpleTextViewArr4[i2].getVisibility() != 8) {
                            this.titleTextView[i2].setTextSize((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 20 : 18);
                        }
                        SimpleTextView simpleTextView4 = this.subtitleTextView;
                        if (simpleTextView4 != null && simpleTextView4.getVisibility() != 8) {
                            this.subtitleTextView.setTextSize((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 16 : 14);
                        }
                        SimpleTextView simpleTextView5 = this.additionalSubtitleTextView;
                        if (simpleTextView5 != null) {
                            simpleTextView5.setTextSize((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 16 : 14);
                        }
                    }
                }
                SimpleTextView[] simpleTextViewArr5 = this.titleTextView;
                if (simpleTextViewArr5[i2] != null && simpleTextViewArr5[i2].getVisibility() != 8) {
                    this.titleTextView[i2].measure(View.MeasureSpec.makeMeasureSpec(availableWidth, i), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f), i));
                    if (this.centerScale) {
                        CharSequence text = this.titleTextView[i2].getText();
                        SimpleTextView[] simpleTextViewArr6 = this.titleTextView;
                        simpleTextViewArr6[i2].setPivotX(simpleTextViewArr6[i2].getTextPaint().measureText(text, 0, text.length()) / 2.0f);
                        this.titleTextView[i2].setPivotY(AndroidUtilities.dp(24.0f) >> 1);
                    } else {
                        this.titleTextView[i2].setPivotX(0.0f);
                        this.titleTextView[i2].setPivotY(0.0f);
                    }
                }
                SimpleTextView simpleTextView6 = this.subtitleTextView;
                if (simpleTextView6 != null && simpleTextView6.getVisibility() != 8) {
                    this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
                }
                SimpleTextView simpleTextView7 = this.additionalSubtitleTextView;
                if (simpleTextView7 != null && simpleTextView7.getVisibility() != 8) {
                    this.additionalSubtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(availableWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
                }
            }
            i2++;
            i = Integer.MIN_VALUE;
        }
        int childCount = getChildCount();
        for (int i3 = 0; i3 < childCount; i3++) {
            View child = getChildAt(i3);
            if (child.getVisibility() != 8) {
                SimpleTextView[] simpleTextViewArr7 = this.titleTextView;
                if (child != simpleTextViewArr7[0]) {
                    if (child != simpleTextViewArr7[1] && child != this.subtitleTextView && child != this.menu && child != this.backButtonImageView) {
                        if (child != this.additionalSubtitleTextView) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), C.BUFFER_FLAG_ENCRYPTED), 0);
                        }
                    }
                }
            }
        }
    }

    public void setMenuOffsetSuppressed(boolean menuOffsetSuppressed) {
        this.isMenuOffsetSuppressed = menuOffsetSuppressed;
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int textLeft;
        char c;
        int i;
        int childLeft;
        int childTop;
        int textTop;
        int menuLeft;
        char c2 = 0;
        int additionalTop = this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0;
        ImageView imageView = this.backButtonImageView;
        int i2 = 8;
        if (imageView != null && imageView.getVisibility() != 8) {
            ImageView imageView2 = this.backButtonImageView;
            imageView2.layout(0, additionalTop, imageView2.getMeasuredWidth(), this.backButtonImageView.getMeasuredHeight() + additionalTop);
            textLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
        } else {
            textLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26.0f : 18.0f);
        }
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null && actionBarMenu.getVisibility() != 8) {
            if (this.menu.searchFieldVisible()) {
                menuLeft = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 74.0f : 66.0f);
            } else {
                menuLeft = (right - left) - this.menu.getMeasuredWidth();
            }
            ActionBarMenu actionBarMenu2 = this.menu;
            actionBarMenu2.layout(menuLeft, additionalTop, actionBarMenu2.getMeasuredWidth() + menuLeft, this.menu.getMeasuredHeight() + additionalTop);
        }
        int i3 = 0;
        while (true) {
            c = 1;
            i = 2;
            if (i3 >= 2) {
                break;
            }
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if (simpleTextViewArr[i3] != null && simpleTextViewArr[i3].getVisibility() != 8) {
                boolean z = this.fromBottom;
                if (((z && i3 == 0) || (!z && i3 == 1)) && this.overlayTitleAnimation && this.titleAnimationRunning) {
                    textTop = (getCurrentActionBarHeight() - this.titleTextView[i3].getTextHeight()) / 2;
                } else {
                    SimpleTextView simpleTextView = this.subtitleTextView;
                    if (simpleTextView == null || simpleTextView.getVisibility() == 8) {
                        textTop = (getCurrentActionBarHeight() - this.titleTextView[i3].getTextHeight()) / 2;
                    } else {
                        textTop = (((getCurrentActionBarHeight() / 2) - this.titleTextView[i3].getTextHeight()) / 2) + AndroidUtilities.dp((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 3.0f : 2.0f);
                    }
                }
                SimpleTextView[] simpleTextViewArr2 = this.titleTextView;
                simpleTextViewArr2[i3].layout(textLeft, additionalTop + textTop, simpleTextViewArr2[i3].getMeasuredWidth() + textLeft, additionalTop + textTop + this.titleTextView[i3].getTextHeight());
            }
            i3++;
        }
        SimpleTextView simpleTextView2 = this.subtitleTextView;
        if (simpleTextView2 != null && simpleTextView2.getVisibility() != 8) {
            int currentActionBarHeight = (getCurrentActionBarHeight() / 2) + (((getCurrentActionBarHeight() / 2) - this.subtitleTextView.getTextHeight()) / 2);
            if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation == 2) {
            }
            int textTop2 = currentActionBarHeight - AndroidUtilities.dp(1.0f);
            SimpleTextView simpleTextView3 = this.subtitleTextView;
            simpleTextView3.layout(textLeft, additionalTop + textTop2, simpleTextView3.getMeasuredWidth() + textLeft, additionalTop + textTop2 + this.subtitleTextView.getTextHeight());
        }
        SimpleTextView simpleTextView4 = this.additionalSubtitleTextView;
        if (simpleTextView4 != null && simpleTextView4.getVisibility() != 8) {
            int currentActionBarHeight2 = (getCurrentActionBarHeight() / 2) + (((getCurrentActionBarHeight() / 2) - this.additionalSubtitleTextView.getTextHeight()) / 2);
            if (AndroidUtilities.isTablet() || getResources().getConfiguration().orientation == 2) {
            }
            int textTop3 = currentActionBarHeight2 - AndroidUtilities.dp(1.0f);
            SimpleTextView simpleTextView5 = this.additionalSubtitleTextView;
            simpleTextView5.layout(textLeft, additionalTop + textTop3, simpleTextView5.getMeasuredWidth() + textLeft, additionalTop + textTop3 + this.additionalSubtitleTextView.getTextHeight());
        }
        int childCount = getChildCount();
        int i4 = 0;
        while (i4 < childCount) {
            View child = getChildAt(i4);
            if (child.getVisibility() != i2) {
                SimpleTextView[] simpleTextViewArr3 = this.titleTextView;
                if (child != simpleTextViewArr3[c2] && child != simpleTextViewArr3[c] && child != this.subtitleTextView && child != this.menu && child != this.backButtonImageView && child != this.additionalSubtitleTextView) {
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
                            childLeft = ((((right - left) - width) / i) + lp.leftMargin) - lp.rightMargin;
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
                            childTop = ((((bottom - top) - height) / i) + lp.topMargin) - lp.bottomMargin;
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
            i4++;
            c2 = 0;
            i2 = 8;
            c = 1;
            i = 2;
        }
    }

    public void onMenuButtonPressed() {
        ActionBarMenu actionBarMenu;
        if (!isActionModeShowed() && (actionBarMenu = this.menu) != null) {
            actionBarMenu.onMenuButtonPressed();
        }
    }

    public void onPause() {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.hideAllPopupMenus();
        }
    }

    public void setAllowOverlayTitle(boolean value) {
        this.allowOverlayTitle = value;
    }

    public void setTitleActionRunnable(Runnable action) {
        this.titleActionRunnable = action;
        this.lastRunnable = action;
    }

    /* JADX WARN: Removed duplicated region for block: B:62:0x0154  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0156  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void setTitleOverlayText(java.lang.String r11, int r12, java.lang.Runnable r13) {
        /*
            Method dump skipped, instructions count: 348
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBar.setTitleOverlayText(java.lang.String, int, java.lang.Runnable):void");
    }

    public boolean isSearchFieldVisible() {
        return this.isSearchFieldVisible;
    }

    public void setOccupyStatusBar(boolean value) {
        this.occupyStatusBar = value;
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setPadding(0, value ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        }
    }

    public boolean getOccupyStatusBar() {
        return this.occupyStatusBar;
    }

    public void setItemsBackgroundColor(int color, boolean isActionMode) {
        ImageView imageView;
        if (isActionMode) {
            this.itemsActionModeBackgroundColor = color;
            if (this.actionModeVisible && (imageView = this.backButtonImageView) != null) {
                imageView.setBackgroundDrawable(Theme.createSelectorDrawable(color));
            }
            ActionBarMenu actionBarMenu = this.actionMode;
            if (actionBarMenu != null) {
                actionBarMenu.updateItemsBackgroundColor();
                return;
            }
            return;
        }
        this.itemsBackgroundColor = color;
        ImageView imageView2 = this.backButtonImageView;
        if (imageView2 != null) {
            imageView2.setBackgroundDrawable(Theme.createSelectorDrawable(color));
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (actionBarMenu2 != null) {
            actionBarMenu2.updateItemsBackgroundColor();
        }
    }

    public void setItemsColor(int color, boolean isActionMode) {
        if (isActionMode) {
            this.itemsActionModeColor = color;
            ActionBarMenu actionBarMenu = this.actionMode;
            if (actionBarMenu != null) {
                actionBarMenu.updateItemsColor();
            }
            ImageView imageView = this.backButtonImageView;
            if (imageView != null) {
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotatedColor(color);
                    return;
                }
                return;
            }
            return;
        }
        this.itemsColor = color;
        ImageView imageView2 = this.backButtonImageView;
        if (imageView2 != null && color != 0) {
            imageView2.setColorFilter(new PorterDuffColorFilter(this.itemsColor, PorterDuff.Mode.MULTIPLY));
            Drawable drawable2 = this.backButtonImageView.getDrawable();
            if (drawable2 instanceof BackDrawable) {
                ((BackDrawable) drawable2).setColor(color);
            } else if (drawable2 instanceof MenuDrawable) {
                ((MenuDrawable) drawable2).setIconColor(color);
            }
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (actionBarMenu2 != null) {
            actionBarMenu2.updateItemsColor();
        }
    }

    public void setCastShadows(boolean value) {
        this.castShadows = value;
    }

    public boolean getCastShadows() {
        return this.castShadows;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.forceSkipTouches) {
            return false;
        }
        return super.onTouchEvent(event) || this.interceptTouches;
    }

    public static int getCurrentActionBarHeight() {
        if (AndroidUtilities.isTablet()) {
            return AndroidUtilities.dp(64.0f);
        }
        if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            return AndroidUtilities.dp(48.0f);
        }
        return AndroidUtilities.dp(56.0f);
    }

    public void setTitleAnimated(CharSequence title, final boolean fromBottom, long duration) {
        if (this.titleTextView[0] == null || title == null) {
            setTitle(title);
            return;
        }
        final boolean crossfade = this.overlayTitleAnimation && !TextUtils.isEmpty(this.subtitle);
        if (crossfade) {
            if (this.subtitleTextView.getVisibility() != 0) {
                this.subtitleTextView.setVisibility(0);
                this.subtitleTextView.setAlpha(0.0f);
            }
            this.subtitleTextView.animate().alpha(fromBottom ? 0.0f : 1.0f).setDuration(220L).start();
        }
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[1] != null) {
            if (simpleTextViewArr[1].getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) this.titleTextView[1].getParent();
                viewGroup.removeView(this.titleTextView[1]);
            }
            this.titleTextView[1] = null;
        }
        SimpleTextView[] simpleTextViewArr2 = this.titleTextView;
        simpleTextViewArr2[1] = simpleTextViewArr2[0];
        simpleTextViewArr2[0] = null;
        setTitle(title);
        this.fromBottom = fromBottom;
        this.titleTextView[0].setAlpha(0.0f);
        if (!crossfade) {
            SimpleTextView simpleTextView = this.titleTextView[0];
            int dp = AndroidUtilities.dp(20.0f);
            if (!fromBottom) {
                dp = -dp;
            }
            simpleTextView.setTranslationY(dp);
        }
        this.titleTextView[0].animate().alpha(1.0f).translationY(0.0f).setDuration(duration).start();
        this.titleAnimationRunning = true;
        ViewPropertyAnimator a = this.titleTextView[1].animate().alpha(0.0f);
        if (!crossfade) {
            int dp2 = AndroidUtilities.dp(20.0f);
            if (fromBottom) {
                dp2 = -dp2;
            }
            a.translationY(dp2);
        }
        a.setDuration(duration).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.6
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ActionBar.this.titleTextView[1] != null && ActionBar.this.titleTextView[1].getParent() != null) {
                    ViewGroup viewGroup2 = (ViewGroup) ActionBar.this.titleTextView[1].getParent();
                    viewGroup2.removeView(ActionBar.this.titleTextView[1]);
                }
                ActionBar.this.titleTextView[1] = null;
                ActionBar.this.titleAnimationRunning = false;
                if (crossfade && fromBottom) {
                    ActionBar.this.subtitleTextView.setVisibility(8);
                }
                ActionBar.this.requestLayout();
            }
        }).start();
        requestLayout();
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.ellipsizeSpanAnimator.onAttachedToWindow();
        if (SharedConfig.noStatusBar && this.actionModeVisible) {
            if (ColorUtils.calculateLuminance(this.actionModeColor) < 0.699999988079071d) {
                AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), false);
            } else {
                AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), true);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.ellipsizeSpanAnimator.onDetachedFromWindow();
        if (SharedConfig.noStatusBar && this.actionModeVisible) {
            int i = this.actionBarColor;
            if (i == 0) {
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needCheckSystemBarColors, new Object[0]);
            } else if (ColorUtils.calculateLuminance(i) < 0.699999988079071d) {
                AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), false);
            } else {
                AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), true);
            }
        }
    }

    public ActionBarMenu getActionMode() {
        return this.actionMode;
    }

    public void setOverlayTitleAnimation(boolean ovelayTitleAnimation) {
        this.overlayTitleAnimation = ovelayTitleAnimation;
    }

    public void beginDelayedTransition() {
        if (Build.VERSION.SDK_INT >= 19) {
            TransitionSet transitionSet = new TransitionSet();
            transitionSet.setOrdering(0);
            transitionSet.addTransition(new Fade());
            transitionSet.addTransition(new ChangeBounds() { // from class: org.telegram.ui.ActionBar.ActionBar.7
                @Override // android.transition.ChangeBounds, android.transition.Transition
                public void captureStartValues(TransitionValues transitionValues) {
                    super.captureStartValues(transitionValues);
                    if (transitionValues.view instanceof SimpleTextView) {
                        float textSize = ((SimpleTextView) transitionValues.view).getTextPaint().getTextSize();
                        transitionValues.values.put("text_size", Float.valueOf(textSize));
                    }
                }

                @Override // android.transition.ChangeBounds, android.transition.Transition
                public void captureEndValues(TransitionValues transitionValues) {
                    super.captureEndValues(transitionValues);
                    if (transitionValues.view instanceof SimpleTextView) {
                        float textSize = ((SimpleTextView) transitionValues.view).getTextPaint().getTextSize();
                        transitionValues.values.put("text_size", Float.valueOf(textSize));
                    }
                }

                @Override // android.transition.ChangeBounds, android.transition.Transition
                public Animator createAnimator(ViewGroup sceneRoot, final TransitionValues startValues, TransitionValues endValues) {
                    if (startValues != null && (startValues.view instanceof SimpleTextView)) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        if (startValues != null && endValues != null) {
                            Animator animator = super.createAnimator(sceneRoot, startValues, endValues);
                            float s = ((Float) startValues.values.get("text_size")).floatValue() / ((Float) endValues.values.get("text_size")).floatValue();
                            startValues.view.setScaleX(s);
                            startValues.view.setScaleY(s);
                            if (animator != null) {
                                animatorSet.playTogether(animator);
                            }
                        }
                        animatorSet.playTogether(ObjectAnimator.ofFloat(startValues.view, View.SCALE_X, 1.0f));
                        animatorSet.playTogether(ObjectAnimator.ofFloat(startValues.view, View.SCALE_Y, 1.0f));
                        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.7.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                startValues.view.setLayerType(2, null);
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                startValues.view.setLayerType(0, null);
                            }
                        });
                        return animatorSet;
                    }
                    return super.createAnimator(sceneRoot, startValues, endValues);
                }
            });
            this.centerScale = false;
            transitionSet.setDuration(220L);
            transitionSet.setInterpolator((TimeInterpolator) CubicBezierInterpolator.DEFAULT);
            TransitionManager.beginDelayedTransition(this, transitionSet);
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer num = null;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        if (color == null) {
            BaseFragment baseFragment = this.parentFragment;
            if (baseFragment != null) {
                num = Integer.valueOf(baseFragment.getThemedColor(key));
            }
            color = num;
        }
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void setDrawBlurBackground(SizeNotifierFrameLayout contentView) {
        this.blurredBackground = true;
        this.contentView = contentView;
        contentView.blurBehindViews.add(this);
        setBackground(null);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        if (this.blurredBackground && this.actionBarColor != 0) {
            this.rectTmp.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.blurScrimPaint.setColor(this.actionBarColor);
            this.contentView.drawBlurRect(canvas, getY(), this.rectTmp, this.blurScrimPaint, true);
        }
        super.dispatchDraw(canvas);
    }

    public void setForceSkipTouches(boolean forceSkipTouches) {
        this.forceSkipTouches = forceSkipTouches;
    }
}
