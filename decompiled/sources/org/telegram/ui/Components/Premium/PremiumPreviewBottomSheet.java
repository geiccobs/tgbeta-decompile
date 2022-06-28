package org.telegram.ui.Components.Premium;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.gms.common.Scopes;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PremiumFeatureCell;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes5.dex */
public class PremiumPreviewBottomSheet extends BottomSheetWithRecyclerListView {
    int buttonRow;
    int currentAccount;
    ValueAnimator enterAnimator;
    boolean enterTransitionInProgress;
    int featuresEndRow;
    int featuresStartRow;
    BaseFragment fragment;
    PremiumGradient.GradientTools gradientTools;
    int helpUsRow;
    ViewGroup iconContainer;
    GLIconTextureView iconTextureView;
    int paddingRow;
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
    TLRPC.User user;
    ArrayList<PremiumPreviewFragment.PremiumFeatureData> premiumFeatures = new ArrayList<>();
    int[] coords = new int[2];
    float enterTransitionProgress = 0.0f;
    PremiumFeatureCell dummyCell = new PremiumFeatureCell(getContext());

    public PremiumPreviewBottomSheet(final BaseFragment fragment, final int currentAccount, TLRPC.User user) {
        super(fragment, false, false);
        this.fragment = fragment;
        this.topPadding = 0.26f;
        this.user = user;
        this.currentAccount = currentAccount;
        PremiumPreviewFragment.fillPremiumFeaturesList(this.premiumFeatures, currentAccount);
        PremiumGradient.GradientTools gradientTools = new PremiumGradient.GradientTools(Theme.key_premiumGradient1, Theme.key_premiumGradient2, Theme.key_premiumGradient3, Theme.key_premiumGradient4);
        this.gradientTools = gradientTools;
        gradientTools.exactly = true;
        this.gradientTools.x1 = 0.0f;
        this.gradientTools.y1 = 1.0f;
        this.gradientTools.x2 = 0.0f;
        this.gradientTools.y2 = 0.0f;
        this.gradientTools.cx = 0.0f;
        this.gradientTools.cy = 0.0f;
        int i = this.rowCount;
        int i2 = i + 1;
        this.rowCount = i2;
        this.paddingRow = i;
        this.featuresStartRow = i2;
        int size = i2 + this.premiumFeatures.size();
        this.rowCount = size;
        this.featuresEndRow = size;
        this.rowCount = size + 1;
        this.sectionRow = size;
        if (!UserConfig.getInstance(currentAccount).isPremium()) {
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.buttonRow = i3;
        }
        this.recyclerListView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public void onItemClick(View view, int position) {
                if (view instanceof PremiumFeatureCell) {
                    PremiumFeatureCell cell = (PremiumFeatureCell) view;
                    PremiumPreviewFragment.sentShowFeaturePreview(currentAccount, cell.data.type);
                    if (cell.data.type != 0) {
                        PremiumPreviewBottomSheet.this.showDialog(new PremiumFeatureBottomSheet(fragment, cell.data.type, false));
                        return;
                    }
                    DoubledLimitsBottomSheet bottomSheet = new DoubledLimitsBottomSheet(fragment, currentAccount);
                    PremiumPreviewBottomSheet.this.showDialog(bottomSheet);
                }
            }
        });
        MediaDataController.getInstance(currentAccount).preloadPremiumPreviewStickers();
        PremiumPreviewFragment.sentShowScreenStat(Scopes.PROFILE);
    }

    public void showDialog(Dialog dialog) {
        this.iconTextureView.setDialogVisible(true);
        this.starParticlesView.setPaused(true);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                PremiumPreviewBottomSheet.this.m2904xbc2e2c82(dialogInterface);
            }
        });
        dialog.show();
    }

    /* renamed from: lambda$showDialog$0$org-telegram-ui-Components-Premium-PremiumPreviewBottomSheet */
    public /* synthetic */ void m2904xbc2e2c82(DialogInterface dialog1) {
        this.iconTextureView.setDialogVisible(false);
        this.starParticlesView.setPaused(false);
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    public void onViewCreated(FrameLayout containerView) {
        super.onViewCreated(containerView);
        PremiumButtonView premiumButtonView = new PremiumButtonView(getContext(), false);
        premiumButtonView.setButton(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount), new View.OnClickListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                PremiumPreviewFragment.sentPremiumButtonClick();
                PremiumPreviewFragment.buyPremium(PremiumPreviewBottomSheet.this.fragment, Scopes.PROFILE);
            }
        });
        FrameLayout buttonContainer = new FrameLayout(getContext());
        View buttonDivider = new View(getContext());
        buttonDivider.setBackgroundColor(Theme.getColor(Theme.key_divider));
        buttonContainer.addView(buttonDivider, LayoutHelper.createFrame(-1, 1.0f));
        buttonDivider.getLayoutParams().height = 1;
        AndroidUtilities.updateViewVisibilityAnimated(buttonDivider, true, 1.0f, false);
        if (!UserConfig.getInstance(this.currentAccount).isPremium()) {
            buttonContainer.addView(premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
            buttonContainer.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
            containerView.addView(buttonContainer, LayoutHelper.createFrame(-1, 68, 80));
        }
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    public void onPreMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onPreMeasure(widthMeasureSpec, heightMeasureSpec);
        measureGradient(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
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

    /* loaded from: classes5.dex */
    public class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
            PremiumPreviewBottomSheet.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            switch (viewType) {
                case 0:
                    LinearLayout linearLayout = new LinearLayout(context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.1
                        @Override // android.view.ViewGroup
                        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                            if (child == PremiumPreviewBottomSheet.this.iconTextureView && PremiumPreviewBottomSheet.this.enterTransitionInProgress) {
                                return true;
                            }
                            return super.drawChild(canvas, child, drawingTime);
                        }
                    };
                    PremiumPreviewBottomSheet.this.iconContainer = linearLayout;
                    linearLayout.setOrientation(1);
                    PremiumPreviewBottomSheet.this.iconTextureView = new GLIconTextureView(context, 1) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.2
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
                    Bitmap bitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    canvas.drawColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_premiumGradient2), Theme.getColor(Theme.key_dialogBackground), 0.5f));
                    PremiumPreviewBottomSheet.this.iconTextureView.setBackgroundBitmap(bitmap);
                    PremiumPreviewBottomSheet.this.iconTextureView.mRenderer.colorKey1 = Theme.key_premiumGradient1;
                    PremiumPreviewBottomSheet.this.iconTextureView.mRenderer.colorKey2 = Theme.key_premiumGradient2;
                    PremiumPreviewBottomSheet.this.iconTextureView.mRenderer.updateColors();
                    linearLayout.addView(PremiumPreviewBottomSheet.this.iconTextureView, LayoutHelper.createLinear(160, 160, 1));
                    TextView titleView = new TextView(context);
                    titleView.setTextSize(1, 16.0f);
                    titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    titleView.setGravity(1);
                    titleView.setText(LocaleController.getString("TelegramPremium", R.string.TelegramPremium));
                    titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    titleView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
                    linearLayout.addView(titleView, LayoutHelper.createLinear(-2, -2, 0.0f, 1, 40, 0, 40, 0));
                    TextView subtitleView = new TextView(context);
                    subtitleView.setTextSize(1, 14.0f);
                    subtitleView.setGravity(1);
                    subtitleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    linearLayout.addView(subtitleView, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 9, 16, 20));
                    titleView.setText(AndroidUtilities.replaceSingleTag(LocaleController.formatString("TelegramPremiumUserDialogTitle", R.string.TelegramPremiumUserDialogTitle, ContactsController.formatName(PremiumPreviewBottomSheet.this.user.first_name, PremiumPreviewBottomSheet.this.user.last_name)), PremiumPreviewBottomSheet$Adapter$$ExternalSyntheticLambda0.INSTANCE));
                    subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.getString("TelegramPremiumUserDialogSubtitle", R.string.TelegramPremiumUserDialogSubtitle)));
                    PremiumPreviewBottomSheet.this.starParticlesView = new StarParticlesView(context);
                    FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.3
                        @Override // android.widget.FrameLayout, android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                            PremiumPreviewBottomSheet.this.starParticlesView.setTranslationY((PremiumPreviewBottomSheet.this.iconTextureView.getTop() + (PremiumPreviewBottomSheet.this.iconTextureView.getMeasuredHeight() / 2.0f)) - (PremiumPreviewBottomSheet.this.starParticlesView.getMeasuredHeight() / 2.0f));
                        }
                    };
                    frameLayout.setClipChildren(false);
                    frameLayout.addView(PremiumPreviewBottomSheet.this.starParticlesView);
                    frameLayout.addView(linearLayout);
                    PremiumPreviewBottomSheet.this.starParticlesView.drawable.useGradient = true;
                    PremiumPreviewBottomSheet.this.starParticlesView.drawable.init();
                    PremiumPreviewBottomSheet.this.iconTextureView.setStarParticlesView(PremiumPreviewBottomSheet.this.starParticlesView);
                    view = frameLayout;
                    break;
                case 1:
                default:
                    view = new PremiumFeatureCell(context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.4
                        @Override // org.telegram.ui.PremiumFeatureCell, android.view.ViewGroup, android.view.View
                        public void dispatchDraw(Canvas canvas2) {
                            AndroidUtilities.rectTmp.set(this.imageView.getLeft(), this.imageView.getTop(), this.imageView.getRight(), this.imageView.getBottom());
                            PremiumPreviewBottomSheet.this.gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), PremiumPreviewBottomSheet.this.totalGradientHeight, 0.0f, -this.data.yOffset);
                            canvas2.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), PremiumPreviewBottomSheet.this.gradientTools.paint);
                            super.dispatchDraw(canvas2);
                        }
                    };
                    break;
                case 2:
                    view = new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_windowBackgroundGray));
                    break;
                case 3:
                    view = new View(context) { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.Adapter.5
                        @Override // android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(68.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    break;
                case 4:
                    view = new AboutPremiumView(context);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public static /* synthetic */ void lambda$onCreateViewHolder$0() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position >= PremiumPreviewBottomSheet.this.featuresStartRow && position < PremiumPreviewBottomSheet.this.featuresEndRow) {
                PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) holder.itemView;
                PremiumPreviewFragment.PremiumFeatureData premiumFeatureData = PremiumPreviewBottomSheet.this.premiumFeatures.get(position - PremiumPreviewBottomSheet.this.featuresStartRow);
                boolean z = true;
                if (position == PremiumPreviewBottomSheet.this.featuresEndRow - 1) {
                    z = false;
                }
                premiumFeatureCell.setData(premiumFeatureData, z);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return PremiumPreviewBottomSheet.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == PremiumPreviewBottomSheet.this.paddingRow) {
                return 0;
            }
            if (position >= PremiumPreviewBottomSheet.this.featuresStartRow && position < PremiumPreviewBottomSheet.this.featuresEndRow) {
                return 1;
            }
            if (position == PremiumPreviewBottomSheet.this.sectionRow) {
                return 2;
            }
            if (position == PremiumPreviewBottomSheet.this.buttonRow) {
                return 3;
            }
            if (position == PremiumPreviewBottomSheet.this.helpUsRow) {
                return 4;
            }
            return super.getItemViewType(position);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 1;
        }
    }

    private void measureGradient(int w, int h) {
        int yOffset = 0;
        for (int i = 0; i < this.premiumFeatures.size(); i++) {
            this.dummyCell.setData(this.premiumFeatures.get(i), false);
            this.dummyCell.measure(View.MeasureSpec.makeMeasureSpec(w, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(h, Integer.MIN_VALUE));
            this.premiumFeatures.get(i).yOffset = yOffset;
            yOffset += this.dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = yOffset;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
        ValueAnimator valueAnimator = this.enterAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void mainContainerDispatchDraw(Canvas canvas) {
        super.mainContainerDispatchDraw(canvas);
        if (this.startEnterFromView != null && this.enterTransitionInProgress) {
            canvas.save();
            float[] points = {this.startEnterFromX, this.startEnterFromY};
            this.startEnterFromView.getMatrix().mapPoints(points);
            Drawable startEnterFromDrawable = this.startEnterFromView.getRightDrawable();
            int[] iArr = this.coords;
            float cxFrom = (-iArr[0]) + this.startEnterFromX1 + points[0];
            float cyFrom = (-iArr[1]) + this.startEnterFromY1 + points[1];
            float fromSize = this.startEnterFromScale * startEnterFromDrawable.getIntrinsicWidth();
            float toSize = this.iconTextureView.getMeasuredHeight() * 0.8f;
            float toSclale = toSize / fromSize;
            float bigIconFromScale = fromSize / toSize;
            float cxTo = this.iconTextureView.getMeasuredWidth() / 2.0f;
            for (View view = this.iconTextureView; view != this.container; view = (View) view.getParent()) {
                cxTo += view.getX();
            }
            float cyTo = this.iconTextureView.getY() + ((View) this.iconTextureView.getParent()).getY() + ((View) this.iconTextureView.getParent().getParent()).getY() + (this.iconTextureView.getMeasuredHeight() / 2.0f);
            float x = AndroidUtilities.lerp(cxFrom, cxTo, CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.enterTransitionProgress));
            float y = AndroidUtilities.lerp(cyFrom, cyTo, this.enterTransitionProgress);
            if (startEnterFromDrawable != null) {
                float f = this.startEnterFromScale;
                float f2 = this.enterTransitionProgress;
                float s = (f * (1.0f - f2)) + (f2 * toSclale);
                canvas.save();
                canvas.scale(s, s, x, y);
                startEnterFromDrawable.setBounds(((int) x) - (startEnterFromDrawable.getIntrinsicWidth() / 2), ((int) y) - (startEnterFromDrawable.getIntrinsicHeight() / 2), ((int) x) + (startEnterFromDrawable.getIntrinsicWidth() / 2), ((int) y) + (startEnterFromDrawable.getIntrinsicHeight() / 2));
                startEnterFromDrawable.setAlpha((int) ((1.0f - Utilities.clamp(this.enterTransitionProgress, 1.0f, 0.0f)) * 255.0f));
                startEnterFromDrawable.draw(canvas);
                startEnterFromDrawable.setAlpha(0);
                canvas.restore();
                float s2 = AndroidUtilities.lerp(bigIconFromScale, 1.0f, this.enterTransitionProgress);
                canvas.scale(s2, s2, x, y);
                canvas.translate(x - (this.iconTextureView.getMeasuredWidth() / 2.0f), y - (this.iconTextureView.getMeasuredHeight() / 2.0f));
                this.iconTextureView.draw(canvas);
            }
            canvas.restore();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean onCustomOpenAnimation() {
        if (this.startEnterFromView == null) {
            return true;
        }
        this.enterAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.enterTransitionProgress = 0.0f;
        this.enterTransitionInProgress = true;
        this.iconContainer.invalidate();
        this.startEnterFromView.getRightDrawable().setAlpha(0);
        this.startEnterFromView.invalidate();
        this.iconTextureView.startEnterAnimation(-360, 100L);
        this.enterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animation) {
                PremiumPreviewBottomSheet.this.enterTransitionProgress = ((Float) animation.getAnimatedValue()).floatValue();
                PremiumPreviewBottomSheet.this.container.invalidate();
            }
        });
        this.enterAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PremiumPreviewBottomSheet.this.enterTransitionInProgress = false;
                PremiumPreviewBottomSheet.this.enterTransitionProgress = 1.0f;
                PremiumPreviewBottomSheet.this.iconContainer.invalidate();
                ValueAnimator iconAlphaBack = ValueAnimator.ofInt(0, 255);
                final Drawable drawable = PremiumPreviewBottomSheet.this.startEnterFromView.getRightDrawable();
                iconAlphaBack.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet.4.1
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator animation2) {
                        drawable.setAlpha(((Integer) animation2.getAnimatedValue()).intValue());
                        PremiumPreviewBottomSheet.this.startEnterFromView.invalidate();
                    }
                });
                iconAlphaBack.start();
                super.onAnimationEnd(animation);
            }
        });
        this.enterAnimator.setDuration(600L);
        this.enterAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.enterAnimator.start();
        return super.onCustomOpenAnimation();
    }
}
