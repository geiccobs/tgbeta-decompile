package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FireworksOverlay;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PremiumFeatureCell;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes3.dex */
public class PremiumPreviewBottomSheet extends BottomSheetWithRecyclerListView {
    boolean animateConfetti;
    FrameLayout buttonContainer;
    int buttonRow;
    int[] coords;
    int currentAccount;
    PremiumFeatureCell dummyCell;
    ValueAnimator enterAnimator;
    boolean enterTransitionInProgress;
    float enterTransitionProgress;
    int featuresEndRow;
    int featuresStartRow;
    FireworksOverlay fireworksOverlay;
    BaseFragment fragment;
    GiftPremiumBottomSheet.GiftTier giftTier;
    PremiumGradient.GradientTools gradientTools;
    int helpUsRow;
    ViewGroup iconContainer;
    GLIconTextureView iconTextureView;
    boolean isOutboundGift;
    int paddingRow;
    ArrayList<PremiumPreviewFragment.PremiumFeatureData> premiumFeatures;
    int rowCount;
    int sectionRow;
    StarParticlesView starParticlesView;
    public float startEnterFromScale;
    public SimpleTextView startEnterFromView;
    public float startEnterFromX;
    public float startEnterFromX1;
    public float startEnterFromY;
    public float startEnterFromY1;
    int totalGradientHeight;
    TLRPC$User user;

    public PremiumPreviewBottomSheet(BaseFragment baseFragment, int i, TLRPC$User tLRPC$User) {
        this(baseFragment, i, tLRPC$User, null);
    }

    public PremiumPreviewBottomSheet(final BaseFragment baseFragment, final int i, TLRPC$User tLRPC$User, GiftPremiumBottomSheet.GiftTier giftTier) {
        super(baseFragment, false, false);
        this.premiumFeatures = new ArrayList<>();
        this.coords = new int[2];
        this.enterTransitionProgress = 0.0f;
        this.fragment = baseFragment;
        this.topPadding = 0.26f;
        this.user = tLRPC$User;
        this.currentAccount = i;
        this.giftTier = giftTier;
        this.dummyCell = new PremiumFeatureCell(getContext());
        PremiumPreviewFragment.fillPremiumFeaturesList(this.premiumFeatures, i);
        if (this.giftTier != null) {
            this.buttonContainer.setVisibility(8);
        }
        PremiumGradient.GradientTools gradientTools = new PremiumGradient.GradientTools("premiumGradient1", "premiumGradient2", "premiumGradient3", "premiumGradient4");
        this.gradientTools = gradientTools;
        gradientTools.exactly = true;
        gradientTools.x1 = 0.0f;
        gradientTools.y1 = 1.0f;
        gradientTools.x2 = 0.0f;
        gradientTools.y2 = 0.0f;
        gradientTools.cx = 0.0f;
        gradientTools.cy = 0.0f;
        int i2 = this.rowCount;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.paddingRow = i2;
        this.featuresStartRow = i3;
        int size = i3 + this.premiumFeatures.size();
        this.rowCount = size;
        this.featuresEndRow = size;
        this.rowCount = size + 1;
        this.sectionRow = size;
        if (!UserConfig.getInstance(i).isPremium() && giftTier == null) {
            int i4 = this.rowCount;
            this.rowCount = i4 + 1;
            this.buttonRow = i4;
        }
        this.recyclerListView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i5) {
                PremiumPreviewBottomSheet.this.lambda$new$0(i, baseFragment, view, i5);
            }
        });
        MediaDataController.getInstance(i).preloadPremiumPreviewStickers();
        PremiumPreviewFragment.sentShowScreenStat("profile");
        FireworksOverlay fireworksOverlay = new FireworksOverlay(getContext());
        this.fireworksOverlay = fireworksOverlay;
        this.container.addView(fireworksOverlay, LayoutHelper.createFrame(-1, -1.0f));
    }

    public /* synthetic */ void lambda$new$0(int i, BaseFragment baseFragment, View view, int i2) {
        if (view instanceof PremiumFeatureCell) {
            PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) view;
            PremiumPreviewFragment.sentShowFeaturePreview(i, premiumFeatureCell.data.type);
            int i3 = premiumFeatureCell.data.type;
            if (i3 == 0) {
                showDialog(new DoubledLimitsBottomSheet(baseFragment, i));
            } else {
                showDialog(new PremiumFeatureBottomSheet(baseFragment, i3, false));
            }
        }
    }

    public PremiumPreviewBottomSheet setOutboundGift(boolean z) {
        this.isOutboundGift = z;
        return this;
    }

    public PremiumPreviewBottomSheet setAnimateConfetti(boolean z) {
        this.animateConfetti = z;
        return this;
    }

    private void showDialog(Dialog dialog) {
        this.iconTextureView.setDialogVisible(true);
        this.starParticlesView.setPaused(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                PremiumPreviewBottomSheet.this.lambda$showDialog$1(dialogInterface);
            }
        });
        dialog.show();
    }

    public /* synthetic */ void lambda$showDialog$1(DialogInterface dialogInterface) {
        this.iconTextureView.setDialogVisible(false);
        this.starParticlesView.setPaused(false);
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    public void onViewCreated(FrameLayout frameLayout) {
        super.onViewCreated(frameLayout);
        PremiumButtonView premiumButtonView = new PremiumButtonView(getContext(), false);
        premiumButtonView.setButton(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount), new View.OnClickListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                PremiumPreviewFragment.sentPremiumButtonClick();
                PremiumPreviewFragment.buyPremium(PremiumPreviewBottomSheet.this.fragment, "profile");
            }
        });
        this.buttonContainer = new FrameLayout(getContext());
        View view = new View(getContext());
        view.setBackgroundColor(Theme.getColor("divider"));
        this.buttonContainer.addView(view, LayoutHelper.createFrame(-1, 1.0f));
        view.getLayoutParams().height = 1;
        AndroidUtilities.updateViewVisibilityAnimated(view, true, 1.0f, false);
        if (!UserConfig.getInstance(this.currentAccount).isPremium()) {
            this.buttonContainer.addView(premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
            this.buttonContainer.setBackgroundColor(getThemedColor("dialogBackground"));
            frameLayout.addView(this.buttonContainer, LayoutHelper.createFrame(-1, 68, 80));
        }
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    public void onPreMeasure(int i, int i2) {
        super.onPreMeasure(i, i2);
        measureGradient(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
        this.container.getLocationOnScreen(this.coords);
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    protected CharSequence getTitle() {
        return LocaleController.getString("TelegramPremium", R.string.TelegramPremium);
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    protected RecyclerListView.SelectionAdapter createAdapter() {
        return new Adapter();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
            PremiumPreviewBottomSheet.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            Context context = viewGroup.getContext();
            if (i == 0) {
                LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.1
                    @Override // android.view.ViewGroup
                    protected boolean drawChild(Canvas canvas, View view2, long j) {
                        PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
                        if (view2 != premiumPreviewBottomSheet.iconTextureView || !premiumPreviewBottomSheet.enterTransitionInProgress) {
                            return super.drawChild(canvas, view2, j);
                        }
                        return true;
                    }
                };
                PremiumPreviewBottomSheet.this.iconContainer = linearLayout;
                linearLayout.setOrientation(1);
                PremiumPreviewBottomSheet.this.iconTextureView = new GLIconTextureView(this, context, 1) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.2
                    @Override // org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView, android.view.TextureView, android.view.View
                    public void onAttachedToWindow() {
                        super.onAttachedToWindow();
                        setPaused(false);
                    }

                    @Override // org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView, android.view.View
                    public void onDetachedFromWindow() {
                        super.onDetachedFromWindow();
                        setPaused(true);
                    }
                };
                Bitmap createBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                new Canvas(createBitmap).drawColor(ColorUtils.blendARGB(Theme.getColor("premiumGradient2"), Theme.getColor("dialogBackground"), 0.5f));
                PremiumPreviewBottomSheet.this.iconTextureView.setBackgroundBitmap(createBitmap);
                GLIconRenderer gLIconRenderer = PremiumPreviewBottomSheet.this.iconTextureView.mRenderer;
                gLIconRenderer.colorKey1 = "premiumGradient1";
                gLIconRenderer.colorKey2 = "premiumGradient2";
                gLIconRenderer.updateColors();
                linearLayout.addView(PremiumPreviewBottomSheet.this.iconTextureView, LayoutHelper.createLinear(160, 160, 1));
                TextView textView = new TextView(context);
                textView.setTextSize(1, 16.0f);
                textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                textView.setGravity(1);
                textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
                linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 0.0f, 1, 40, 0, 40, 0));
                TextView textView2 = new TextView(context);
                textView2.setTextSize(1, 14.0f);
                textView2.setGravity(1);
                textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                textView2.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
                linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 9, 16, 20));
                PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
                GiftPremiumBottomSheet.GiftTier giftTier = premiumPreviewBottomSheet.giftTier;
                if (giftTier != null) {
                    String str = "";
                    if (premiumPreviewBottomSheet.isOutboundGift) {
                        Object[] objArr = new Object[2];
                        TLRPC$User tLRPC$User = premiumPreviewBottomSheet.user;
                        objArr[0] = tLRPC$User != null ? tLRPC$User.first_name : str;
                        objArr[1] = Integer.valueOf(giftTier.getMonths());
                        textView.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString(R.string.TelegramPremiumUserGiftedPremiumOutboundDialogTitle, objArr), null));
                        Object[] objArr2 = new Object[1];
                        TLRPC$User tLRPC$User2 = PremiumPreviewBottomSheet.this.user;
                        if (tLRPC$User2 != null) {
                            str = tLRPC$User2.first_name;
                        }
                        objArr2[0] = str;
                        textView2.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString(R.string.TelegramPremiumUserGiftedPremiumOutboundDialogSubtitle, objArr2), null));
                    } else {
                        Object[] objArr3 = new Object[2];
                        TLRPC$User tLRPC$User3 = premiumPreviewBottomSheet.user;
                        if (tLRPC$User3 != null) {
                            str = tLRPC$User3.first_name;
                        }
                        objArr3[0] = str;
                        objArr3[1] = Integer.valueOf(giftTier.getMonths());
                        textView.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString(R.string.TelegramPremiumUserGiftedPremiumDialogTitle, objArr3), null));
                        textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.TelegramPremiumUserGiftedPremiumDialogSubtitle)));
                    }
                } else {
                    TLRPC$User tLRPC$User4 = premiumPreviewBottomSheet.user;
                    textView.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString(R.string.TelegramPremiumUserDialogTitle, ContactsController.formatName(tLRPC$User4.first_name, tLRPC$User4.last_name)), null));
                    textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.TelegramPremiumUserDialogSubtitle)));
                }
                PremiumPreviewBottomSheet.this.starParticlesView = new StarParticlesView(context);
                FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.3
                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int i2, int i3) {
                        super.onMeasure(i2, i3);
                        PremiumPreviewBottomSheet premiumPreviewBottomSheet2 = PremiumPreviewBottomSheet.this;
                        premiumPreviewBottomSheet2.starParticlesView.setTranslationY((premiumPreviewBottomSheet2.iconTextureView.getTop() + (PremiumPreviewBottomSheet.this.iconTextureView.getMeasuredHeight() / 2.0f)) - (PremiumPreviewBottomSheet.this.starParticlesView.getMeasuredHeight() / 2.0f));
                    }
                };
                frameLayout.setClipChildren(false);
                frameLayout.addView(PremiumPreviewBottomSheet.this.starParticlesView);
                frameLayout.addView(linearLayout);
                StarParticlesView.Drawable drawable = PremiumPreviewBottomSheet.this.starParticlesView.drawable;
                drawable.useGradient = true;
                drawable.init();
                PremiumPreviewBottomSheet premiumPreviewBottomSheet2 = PremiumPreviewBottomSheet.this;
                premiumPreviewBottomSheet2.iconTextureView.setStarParticlesView(premiumPreviewBottomSheet2.starParticlesView);
                view = frameLayout;
            } else if (i == 2) {
                view = new ShadowSectionCell(context, 12, Theme.getColor("windowBackgroundGray"));
            } else if (i == 3) {
                view = new View(this, context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.5
                    @Override // android.view.View
                    protected void onMeasure(int i2, int i3) {
                        super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(68.0f), 1073741824));
                    }
                };
            } else if (i != 4) {
                view = new PremiumFeatureCell(context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.4
                    @Override // org.telegram.ui.PremiumFeatureCell, android.view.ViewGroup, android.view.View
                    public void dispatchDraw(Canvas canvas) {
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(this.imageView.getLeft(), this.imageView.getTop(), this.imageView.getRight(), this.imageView.getBottom());
                        PremiumPreviewBottomSheet.this.gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), PremiumPreviewBottomSheet.this.totalGradientHeight, 0.0f, -this.data.yOffset);
                        canvas.drawRoundRect(rectF, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), PremiumPreviewBottomSheet.this.gradientTools.paint);
                        super.dispatchDraw(canvas);
                    }
                };
            } else {
                view = new AboutPremiumView(context);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
            int i2 = premiumPreviewBottomSheet.featuresStartRow;
            if (i < i2 || i >= premiumPreviewBottomSheet.featuresEndRow) {
                return;
            }
            PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) viewHolder.itemView;
            PremiumPreviewFragment.PremiumFeatureData premiumFeatureData = premiumPreviewBottomSheet.premiumFeatures.get(i - i2);
            boolean z = true;
            if (i == PremiumPreviewBottomSheet.this.featuresEndRow - 1) {
                z = false;
            }
            premiumFeatureCell.setData(premiumFeatureData, z);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return PremiumPreviewBottomSheet.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
            if (i == premiumPreviewBottomSheet.paddingRow) {
                return 0;
            }
            if (i >= premiumPreviewBottomSheet.featuresStartRow && i < premiumPreviewBottomSheet.featuresEndRow) {
                return 1;
            }
            if (i == premiumPreviewBottomSheet.sectionRow) {
                return 2;
            }
            if (i == premiumPreviewBottomSheet.buttonRow) {
                return 3;
            }
            if (i != premiumPreviewBottomSheet.helpUsRow) {
                return super.getItemViewType(i);
            }
            return 4;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1;
        }
    }

    private void measureGradient(int i, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 < this.premiumFeatures.size(); i4++) {
            this.dummyCell.setData(this.premiumFeatures.get(i4), false);
            this.dummyCell.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
            this.premiumFeatures.get(i4).yOffset = i3;
            i3 += this.dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = i3;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
        if (this.animateConfetti) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    PremiumPreviewBottomSheet.this.lambda$show$2();
                }
            }, 200L);
        }
    }

    public /* synthetic */ void lambda$show$2() {
        try {
            this.container.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        this.fireworksOverlay.start();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
        ValueAnimator valueAnimator = this.enterAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (this.fireworksOverlay.isStarted()) {
            this.fireworksOverlay.animate().alpha(0.0f).setDuration(150L).start();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void mainContainerDispatchDraw(Canvas canvas) {
        super.mainContainerDispatchDraw(canvas);
        if (this.startEnterFromView == null || !this.enterTransitionInProgress) {
            return;
        }
        canvas.save();
        float[] fArr = {this.startEnterFromX, this.startEnterFromY};
        this.startEnterFromView.getMatrix().mapPoints(fArr);
        Drawable rightDrawable = this.startEnterFromView.getRightDrawable();
        int[] iArr = this.coords;
        float f = (-iArr[0]) + this.startEnterFromX1 + fArr[0];
        float f2 = (-iArr[1]) + this.startEnterFromY1 + fArr[1];
        float intrinsicWidth = this.startEnterFromScale * rightDrawable.getIntrinsicWidth();
        float measuredHeight = this.iconTextureView.getMeasuredHeight() * 0.8f;
        float f3 = measuredHeight / intrinsicWidth;
        float f4 = intrinsicWidth / measuredHeight;
        float measuredWidth = this.iconTextureView.getMeasuredWidth() / 2.0f;
        for (View view = this.iconTextureView; view != this.container; view = (View) view.getParent()) {
            measuredWidth += view.getX();
        }
        float y = this.iconTextureView.getY() + ((View) this.iconTextureView.getParent()).getY() + ((View) this.iconTextureView.getParent().getParent()).getY() + (this.iconTextureView.getMeasuredHeight() / 2.0f);
        float lerp = AndroidUtilities.lerp(f, measuredWidth, CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.enterTransitionProgress));
        float lerp2 = AndroidUtilities.lerp(f2, y, this.enterTransitionProgress);
        float f5 = this.startEnterFromScale;
        float f6 = this.enterTransitionProgress;
        float f7 = (f5 * (1.0f - f6)) + (f3 * f6);
        canvas.save();
        canvas.scale(f7, f7, lerp, lerp2);
        int i = (int) lerp;
        int i2 = (int) lerp2;
        rightDrawable.setBounds(i - (rightDrawable.getIntrinsicWidth() / 2), i2 - (rightDrawable.getIntrinsicHeight() / 2), i + (rightDrawable.getIntrinsicWidth() / 2), i2 + (rightDrawable.getIntrinsicHeight() / 2));
        rightDrawable.setAlpha((int) ((1.0f - Utilities.clamp(this.enterTransitionProgress, 1.0f, 0.0f)) * 255.0f));
        rightDrawable.draw(canvas);
        rightDrawable.setAlpha(0);
        canvas.restore();
        float lerp3 = AndroidUtilities.lerp(f4, 1.0f, this.enterTransitionProgress);
        canvas.scale(lerp3, lerp3, lerp, lerp2);
        canvas.translate(lerp - (this.iconTextureView.getMeasuredWidth() / 2.0f), lerp2 - (this.iconTextureView.getMeasuredHeight() / 2.0f));
        this.iconTextureView.draw(canvas);
        canvas.restore();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean onCustomOpenAnimation() {
        if (this.startEnterFromView == null) {
            return false;
        }
        this.enterAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.enterTransitionProgress = 0.0f;
        this.enterTransitionInProgress = true;
        this.iconContainer.invalidate();
        this.startEnterFromView.getRightDrawable().setAlpha(0);
        this.startEnterFromView.invalidate();
        this.iconTextureView.startEnterAnimation(-360, 100L);
        this.enterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                PremiumPreviewBottomSheet.this.lambda$onCustomOpenAnimation$3(valueAnimator);
            }
        });
        this.enterAnimator.addListener(new AnonymousClass2());
        this.enterAnimator.setDuration(600L);
        this.enterAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.enterAnimator.start();
        return super.onCustomOpenAnimation();
    }

    public /* synthetic */ void lambda$onCustomOpenAnimation$3(ValueAnimator valueAnimator) {
        this.enterTransitionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.container.invalidate();
    }

    /* renamed from: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$2 */
    /* loaded from: classes3.dex */
    public class AnonymousClass2 extends AnimatorListenerAdapter {
        AnonymousClass2() {
            PremiumPreviewBottomSheet.this = r1;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            PremiumPreviewBottomSheet premiumPreviewBottomSheet = PremiumPreviewBottomSheet.this;
            premiumPreviewBottomSheet.enterTransitionInProgress = false;
            premiumPreviewBottomSheet.enterTransitionProgress = 1.0f;
            premiumPreviewBottomSheet.iconContainer.invalidate();
            ValueAnimator ofInt = ValueAnimator.ofInt(0, 255);
            final Drawable rightDrawable = PremiumPreviewBottomSheet.this.startEnterFromView.getRightDrawable();
            ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$2$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    PremiumPreviewBottomSheet.AnonymousClass2.this.lambda$onAnimationEnd$0(rightDrawable, valueAnimator);
                }
            });
            ofInt.start();
            super.onAnimationEnd(animator);
        }

        public /* synthetic */ void lambda$onAnimationEnd$0(Drawable drawable, ValueAnimator valueAnimator) {
            drawable.setAlpha(((Integer) valueAnimator.getAnimatedValue()).intValue());
            PremiumPreviewBottomSheet.this.startEnterFromView.invalidate();
        }
    }
}
