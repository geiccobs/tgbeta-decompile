package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BottomPagesView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes5.dex */
public class PremiumFeatureBottomSheet extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private FrameLayout buttonContainer;
    boolean containerViewsForward;
    float containerViewsProgress;
    FrameLayout content;
    int contentHeight;
    boolean enterAnimationIsRunning;
    BaseFragment fragment;
    private final boolean onlySelectedType;
    private PremiumButtonView premiumButtonView;
    ArrayList<PremiumPreviewFragment.PremiumFeatureData> premiumFeatures = new ArrayList<>();
    private final int startType;
    SvgHelper.SvgDrawable svgIcon;
    ViewPager viewPager;

    public PremiumFeatureBottomSheet(final BaseFragment fragment, int startType, final boolean onlySelectedType) {
        super(fragment.getParentActivity(), false);
        this.fragment = fragment;
        this.startType = startType;
        this.onlySelectedType = onlySelectedType;
        String svg = RLottieDrawable.readRes(null, R.raw.star_loader);
        this.svgIcon = SvgHelper.getDrawable(svg);
        final Context context = fragment.getParentActivity();
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet.1
            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (PremiumFeatureBottomSheet.this.isPortrait) {
                    PremiumFeatureBottomSheet.this.contentHeight = View.MeasureSpec.getSize(widthMeasureSpec);
                } else {
                    PremiumFeatureBottomSheet.this.contentHeight = (int) (View.MeasureSpec.getSize(heightMeasureSpec) * 0.65f);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        PremiumPreviewFragment.fillPremiumFeaturesList(this.premiumFeatures, fragment.getCurrentAccount());
        int selectedPosition = 0;
        int i = 0;
        while (true) {
            if (i >= this.premiumFeatures.size()) {
                break;
            }
            if (this.premiumFeatures.get(i).type == 0) {
                this.premiumFeatures.remove(i);
                i--;
            } else if (this.premiumFeatures.get(i).type == startType) {
                selectedPosition = i;
                break;
            }
            i++;
        }
        if (onlySelectedType) {
            PremiumPreviewFragment.PremiumFeatureData selectedFeature = this.premiumFeatures.get(selectedPosition);
            this.premiumFeatures.clear();
            this.premiumFeatures.add(selectedFeature);
            selectedPosition = 0;
        }
        final PremiumPreviewFragment.PremiumFeatureData featureData = this.premiumFeatures.get(selectedPosition);
        setApplyBottomPadding(false);
        this.useBackgroundTopPadding = false;
        final PremiumGradient.GradientTools gradientTools = new PremiumGradient.GradientTools(Theme.key_premiumGradientBottomSheet1, Theme.key_premiumGradientBottomSheet2, Theme.key_premiumGradientBottomSheet3, null);
        gradientTools.x1 = 0.0f;
        gradientTools.y1 = 1.1f;
        gradientTools.x2 = 1.5f;
        gradientTools.y2 = -0.2f;
        gradientTools.exactly = true;
        this.content = new FrameLayout(context) { // from class: org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet.2
            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int h = PremiumFeatureBottomSheet.this.contentHeight;
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(2.0f) + h, C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), getMeasuredHeight(), 0.0f, 0.0f);
                AndroidUtilities.rectTmp.set(0.0f, AndroidUtilities.dp(2.0f), getMeasuredWidth(), getMeasuredHeight() + AndroidUtilities.dp(18.0f));
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(12.0f) - 1, AndroidUtilities.dp(12.0f) - 1, gradientTools.paint);
                canvas.restore();
                super.dispatchDraw(canvas);
            }
        };
        FrameLayout closeLayout = new FrameLayout(context);
        ImageView closeImage = new ImageView(context);
        closeImage.setImageResource(R.drawable.msg_close);
        closeImage.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(12.0f), ColorUtils.setAlphaComponent(-1, 40), ColorUtils.setAlphaComponent(-1, 100)));
        closeLayout.addView(closeImage, LayoutHelper.createFrame(24, 24, 17));
        closeLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PremiumFeatureBottomSheet.this.m2901xa132acd3(view);
            }
        });
        frameLayout.addView(this.content, LayoutHelper.createLinear(-1, -2, 1, 0, 16, 0, 0));
        ViewPager viewPager = new ViewPager(context) { // from class: org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet.3
            @Override // androidx.viewpager.widget.ViewPager, android.view.View
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int h = AndroidUtilities.dp(100.0f);
                if (getChildCount() > 0) {
                    getChildAt(0).measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, 0));
                    h = getChildAt(0).getMeasuredHeight();
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(h, C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                try {
                    return super.onInterceptTouchEvent(ev);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override // androidx.viewpager.widget.ViewPager, android.view.View
            public boolean onTouchEvent(MotionEvent ev) {
                if (PremiumFeatureBottomSheet.this.enterAnimationIsRunning) {
                    return false;
                }
                return super.onTouchEvent(ev);
            }
        };
        this.viewPager = viewPager;
        viewPager.setOffscreenPageLimit(0);
        PagerAdapter pagerAdapter = new PagerAdapter() { // from class: org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet.4
            @Override // androidx.viewpager.widget.PagerAdapter
            public int getCount() {
                return PremiumFeatureBottomSheet.this.premiumFeatures.size();
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public Object instantiateItem(ViewGroup container, int position) {
                ViewPage viewPage = new ViewPage(context, position);
                container.addView(viewPage);
                viewPage.position = position;
                viewPage.setFeatureDate(PremiumFeatureBottomSheet.this.premiumFeatures.get(position));
                return viewPage;
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override // androidx.viewpager.widget.PagerAdapter
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        };
        this.viewPager.setAdapter(pagerAdapter);
        this.viewPager.setCurrentItem(selectedPosition);
        frameLayout.addView(this.viewPager, LayoutHelper.createFrame(-1, 100.0f, 0, 0.0f, 18.0f, 0.0f, 0.0f));
        frameLayout.addView(closeLayout, LayoutHelper.createFrame(52, 52.0f, 53, 0.0f, 16.0f, 0.0f, 0.0f));
        final BottomPagesView bottomPages = new BottomPagesView(context, this.viewPager, this.premiumFeatures.size());
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet.5
            float progress;
            int selectedPosition;
            int toPosition;

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                bottomPages.setPageOffset(position, positionOffset);
                this.selectedPosition = position;
                this.toPosition = positionOffsetPixels > 0 ? position + 1 : position - 1;
                this.progress = positionOffset;
                checkPage();
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int i2) {
                checkPage();
            }

            private void checkPage() {
                for (int i2 = 0; i2 < PremiumFeatureBottomSheet.this.viewPager.getChildCount(); i2++) {
                    ViewPage page = (ViewPage) PremiumFeatureBottomSheet.this.viewPager.getChildAt(i2);
                    float offset = 0.0f;
                    if (!PremiumFeatureBottomSheet.this.enterAnimationIsRunning || !(page.topView instanceof PremiumAppIconsPreviewView)) {
                        if (page.position == this.selectedPosition) {
                            PagerHeaderView pagerHeaderView = page.topHeader;
                            float f = (-page.getMeasuredWidth()) * this.progress;
                            offset = f;
                            pagerHeaderView.setOffset(f);
                        } else if (page.position == this.toPosition) {
                            PagerHeaderView pagerHeaderView2 = page.topHeader;
                            float measuredWidth = ((-page.getMeasuredWidth()) * this.progress) + page.getMeasuredWidth();
                            offset = measuredWidth;
                            pagerHeaderView2.setOffset(measuredWidth);
                        } else {
                            page.topHeader.setOffset(page.getMeasuredWidth());
                        }
                    }
                    if (page.topView instanceof PremiumAppIconsPreviewView) {
                        page.setTranslationX(-offset);
                        page.title.setTranslationX(offset);
                        page.description.setTranslationX(offset);
                    }
                }
                PremiumFeatureBottomSheet.this.containerViewsProgress = this.progress;
                PremiumFeatureBottomSheet.this.containerViewsForward = this.toPosition > this.selectedPosition;
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int i2) {
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.addView(frameLayout);
        linearLayout.setOrientation(1);
        bottomPages.setColor(Theme.key_chats_unreadCounterMuted, Theme.key_chats_actionBackground);
        if (!onlySelectedType) {
            linearLayout.addView(bottomPages, LayoutHelper.createLinear(this.premiumFeatures.size() * 11, 5, 1, 0, 0, 0, 10));
        }
        PremiumButtonView premiumButtonView = new PremiumButtonView(context, true);
        this.premiumButtonView = premiumButtonView;
        premiumButtonView.buttonLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PremiumFeatureBottomSheet.this.m2902x35711c72(fragment, onlySelectedType, featureData, view);
            }
        });
        this.premiumButtonView.overlayTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PremiumFeatureBottomSheet.this.m2903xc9af8c11(view);
            }
        });
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.buttonContainer = frameLayout2;
        frameLayout2.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
        this.buttonContainer.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
        linearLayout.addView(this.buttonContainer, LayoutHelper.createLinear(-1, 68, 80));
        if (UserConfig.getInstance(this.currentAccount).isPremium()) {
            this.premiumButtonView.setOverlayText(LocaleController.getString("OK", R.string.OK), false, false);
        }
        ScrollView scrollView = new ScrollView(context);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
        MediaDataController.getInstance(this.currentAccount).preloadPremiumPreviewStickers();
        setButtonText();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-Premium-PremiumFeatureBottomSheet */
    public /* synthetic */ void m2901xa132acd3(View v) {
        dismiss();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-Premium-PremiumFeatureBottomSheet */
    public /* synthetic */ void m2902x35711c72(BaseFragment fragment, boolean onlySelectedType, PremiumPreviewFragment.PremiumFeatureData featureData, View v) {
        if (fragment.getVisibleDialog() != null) {
            fragment.getVisibleDialog().dismiss();
        }
        if (fragment instanceof ChatActivity) {
            ((ChatActivity) fragment).closeMenu();
        }
        if (onlySelectedType) {
            fragment.presentFragment(new PremiumPreviewFragment(PremiumPreviewFragment.featureTypeToServerString(featureData.type)));
        } else {
            PremiumPreviewFragment.buyPremium(fragment, PremiumPreviewFragment.featureTypeToServerString(featureData.type));
        }
        dismiss();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-Premium-PremiumFeatureBottomSheet */
    public /* synthetic */ void m2903xc9af8c11(View v) {
        dismiss();
    }

    private void setButtonText() {
        if (this.onlySelectedType) {
            int i = this.startType;
            if (i == 4) {
                this.premiumButtonView.buttonTextView.setText(LocaleController.getString((int) R.string.UnlockPremiumReactions));
                this.premiumButtonView.setIcon(R.raw.unlock_icon);
                return;
            } else if (i == 3) {
                this.premiumButtonView.buttonTextView.setText(LocaleController.getString((int) R.string.AboutTelegramPremium));
                return;
            } else if (i == 10) {
                this.premiumButtonView.buttonTextView.setText(LocaleController.getString((int) R.string.UnlockPremiumIcons));
                this.premiumButtonView.setIcon(R.raw.unlock_icon);
                return;
            } else {
                return;
            }
        }
        this.premiumButtonView.buttonTextView.setText(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount));
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.premiumPromoUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.premiumPromoUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.billingProductDetailsUpdated || id == NotificationCenter.premiumPromoUpdated) {
            setButtonText();
        } else if (id == NotificationCenter.currentUserPremiumStatusChanged) {
            if (UserConfig.getInstance(this.currentAccount).isPremium()) {
                this.premiumButtonView.setOverlayText(LocaleController.getString("OK", R.string.OK), false, true);
            } else {
                this.premiumButtonView.clearOverlayText();
            }
        }
    }

    /* loaded from: classes5.dex */
    public class ViewPage extends LinearLayout {
        TextView description;
        public int position;
        TextView title;
        PagerHeaderView topHeader;
        View topView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ViewPage(Context context, int p) {
            super(context);
            PremiumFeatureBottomSheet.this = r11;
            setOrientation(1);
            View viewForPosition = r11.getViewForPosition(context, p);
            this.topView = viewForPosition;
            addView(viewForPosition);
            this.topHeader = (PagerHeaderView) this.topView;
            TextView textView = new TextView(context);
            this.title = textView;
            textView.setGravity(1);
            this.title.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            this.title.setTextSize(1, 20.0f);
            this.title.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            addView(this.title, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 20.0f, 21.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.description = textView2;
            textView2.setGravity(1);
            this.description.setTextSize(1, 15.0f);
            this.description.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            if (!r11.onlySelectedType) {
                this.description.setLines(2);
            }
            addView(this.description, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 10.0f, 21.0f, 16.0f));
            setClipChildren(false);
        }

        @Override // android.widget.LinearLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.topView.getLayoutParams().height = PremiumFeatureBottomSheet.this.contentHeight;
            this.description.setVisibility(PremiumFeatureBottomSheet.this.isPortrait ? 0 : 8);
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) this.title.getLayoutParams();
            if (PremiumFeatureBottomSheet.this.isPortrait) {
                layoutParams.topMargin = AndroidUtilities.dp(20.0f);
                layoutParams.bottomMargin = 0;
            } else {
                layoutParams.topMargin = AndroidUtilities.dp(10.0f);
                layoutParams.bottomMargin = AndroidUtilities.dp(10.0f);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override // android.view.ViewGroup
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child == this.topView) {
                if (child instanceof CarouselView) {
                    return super.drawChild(canvas, child, drawingTime);
                }
                canvas.save();
                canvas.clipRect(0, 0, child.getMeasuredWidth(), child.getMeasuredHeight());
                boolean b = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return b;
            }
            boolean b2 = super.drawChild(canvas, child, drawingTime);
            return b2;
        }

        void setFeatureDate(PremiumPreviewFragment.PremiumFeatureData featureData) {
            if (PremiumFeatureBottomSheet.this.onlySelectedType) {
                if (PremiumFeatureBottomSheet.this.startType != 4) {
                    if (PremiumFeatureBottomSheet.this.startType != 3) {
                        if (PremiumFeatureBottomSheet.this.startType == 10) {
                            this.title.setText(LocaleController.getString("PremiumPreviewAppIcon", R.string.PremiumPreviewAppIcon));
                            this.description.setText(LocaleController.getString("PremiumPreviewAppIconDescription2", R.string.PremiumPreviewAppIconDescription2));
                            return;
                        }
                        return;
                    }
                    this.title.setText(LocaleController.getString("PremiumPreviewNoAds", R.string.PremiumPreviewNoAds));
                    this.description.setText(LocaleController.getString("PremiumPreviewNoAdsDescription2", R.string.PremiumPreviewNoAdsDescription2));
                    return;
                }
                this.title.setText(LocaleController.getString("AdditionalReactions", R.string.AdditionalReactions));
                this.description.setText(LocaleController.getString("AdditionalReactionsDescription", R.string.AdditionalReactionsDescription));
                return;
            }
            this.title.setText(featureData.title);
            this.description.setText(featureData.description);
        }
    }

    View getViewForPosition(Context context, int position) {
        PremiumPreviewFragment.PremiumFeatureData featureData = this.premiumFeatures.get(position);
        if (featureData.type == 4) {
            ArrayList<ReactionDrawingObject> drawingObjects = new ArrayList<>();
            List<TLRPC.TL_availableReaction> list = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
            List<TLRPC.TL_availableReaction> premiumLockedReactions = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).premium) {
                    premiumLockedReactions.add(list.get(i));
                }
            }
            for (int i2 = 0; i2 < premiumLockedReactions.size(); i2++) {
                ReactionDrawingObject drawingObject = new ReactionDrawingObject(i2);
                drawingObject.set(premiumLockedReactions.get(i2));
                drawingObjects.add(drawingObject);
            }
            final HashMap<String, Integer> sortRulesMap = new HashMap<>();
            sortRulesMap.put("👌", 1);
            sortRulesMap.put("😍", 2);
            sortRulesMap.put("🤡", 3);
            sortRulesMap.put("🕊", 4);
            sortRulesMap.put("🥱", 5);
            sortRulesMap.put("🥴", 6);
            sortRulesMap.put("🐳", 7);
            Collections.sort(drawingObjects, new Comparator() { // from class: org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet$$ExternalSyntheticLambda3
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return PremiumFeatureBottomSheet.lambda$getViewForPosition$3(sortRulesMap, (ReactionDrawingObject) obj, (ReactionDrawingObject) obj2);
                }
            });
            CarouselView carouselView = new CarouselView(context, drawingObjects);
            return carouselView;
        } else if (featureData.type == 5) {
            PremiumStickersPreviewRecycler recyclerListView = new PremiumStickersPreviewRecycler(context, this.currentAccount) { // from class: org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet.6
                @Override // org.telegram.ui.Components.Premium.PremiumStickersPreviewRecycler, org.telegram.ui.Components.Premium.PagerHeaderView
                public void setOffset(float v) {
                    setAutoPlayEnabled(v == 0.0f);
                    super.setOffset(v);
                }
            };
            return recyclerListView;
        } else if (featureData.type == 10) {
            return new PremiumAppIconsPreviewView(context);
        } else {
            VideoScreenPreview preview = new VideoScreenPreview(context, this.svgIcon, this.currentAccount, featureData.type);
            return preview;
        }
    }

    public static /* synthetic */ int lambda$getViewForPosition$3(HashMap sortRulesMap, ReactionDrawingObject o1, ReactionDrawingObject o2) {
        int i2 = Integer.MAX_VALUE;
        int i1 = sortRulesMap.containsKey(o1.reaction.reaction) ? ((Integer) sortRulesMap.get(o1.reaction.reaction)).intValue() : Integer.MAX_VALUE;
        if (sortRulesMap.containsKey(o2.reaction.reaction)) {
            i2 = ((Integer) sortRulesMap.get(o2.reaction.reaction)).intValue();
        }
        return i2 - i1;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean onCustomOpenAnimation() {
        if (this.viewPager.getChildCount() > 0) {
            ViewPage page = (ViewPage) this.viewPager.getChildAt(0);
            if (page.topView instanceof PremiumAppIconsPreviewView) {
                final PremiumAppIconsPreviewView premiumAppIconsPreviewView = (PremiumAppIconsPreviewView) page.topView;
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(page.getMeasuredWidth(), 0.0f);
                premiumAppIconsPreviewView.setOffset(page.getMeasuredWidth());
                this.enterAnimationIsRunning = true;
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet.7
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator animation) {
                        premiumAppIconsPreviewView.setOffset(((Float) animation.getAnimatedValue()).floatValue());
                    }
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet.8
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        PremiumFeatureBottomSheet.this.enterAnimationIsRunning = false;
                        premiumAppIconsPreviewView.setOffset(0.0f);
                        super.onAnimationEnd(animation);
                    }
                });
                valueAnimator.setDuration(500L);
                valueAnimator.setStartDelay(100L);
                valueAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                valueAnimator.start();
            }
        }
        return super.onCustomOpenAnimation();
    }
}
