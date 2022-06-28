package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.FixedHeightEmptyCell;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes5.dex */
public class DoubledLimitsBottomSheet extends BottomSheetWithRecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    private BaseFragment baseFragment;
    private View divider;
    PremiumGradient.GradientTools gradientTools;
    int lastViewRow;
    final ArrayList<Limit> limits;
    int limitsStartEnd;
    int limitsStartRow;
    PremiumButtonView premiumButtonView;
    PremiumPreviewFragment premiumPreviewFragment;
    int rowCount;
    ImageView titleImage;
    float titleProgress;
    TextView titleView;
    private int totalGradientHeight;
    int headerRow = 0;
    FrameLayout titleLayout = new FrameLayout(getContext());

    public DoubledLimitsBottomSheet(final BaseFragment fragment, final int currentAccount) {
        super(fragment, false, false);
        ArrayList<Limit> arrayList = new ArrayList<>();
        this.limits = arrayList;
        this.baseFragment = fragment;
        PremiumGradient.GradientTools gradientTools = new PremiumGradient.GradientTools(Theme.key_premiumGradient1, Theme.key_premiumGradient2, Theme.key_premiumGradient3, Theme.key_premiumGradient4);
        this.gradientTools = gradientTools;
        gradientTools.x1 = 0.0f;
        this.gradientTools.y1 = 0.0f;
        this.gradientTools.x2 = 0.0f;
        this.gradientTools.y2 = 1.0f;
        this.clipToActionBar = true;
        MessagesController messagesController = MessagesController.getInstance(currentAccount);
        arrayList.add(new Limit(LocaleController.getString("GroupsAndChannelsLimitTitle", R.string.GroupsAndChannelsLimitTitle), LocaleController.formatString("GroupsAndChannelsLimitSubtitle", R.string.GroupsAndChannelsLimitSubtitle, Integer.valueOf(messagesController.channelsLimitPremium)), messagesController.channelsLimitDefault, messagesController.channelsLimitPremium));
        arrayList.add(new Limit(LocaleController.getString("PinChatsLimitTitle", R.string.PinChatsLimitTitle), LocaleController.formatString("PinChatsLimitSubtitle", R.string.PinChatsLimitSubtitle, Integer.valueOf(messagesController.dialogFiltersPinnedLimitPremium)), messagesController.dialogFiltersPinnedLimitDefault, messagesController.dialogFiltersPinnedLimitPremium));
        arrayList.add(new Limit(LocaleController.getString("PublicLinksLimitTitle", R.string.PublicLinksLimitTitle), LocaleController.formatString("PublicLinksLimitSubtitle", R.string.PublicLinksLimitSubtitle, Integer.valueOf(messagesController.publicLinksLimitPremium)), messagesController.publicLinksLimitDefault, messagesController.publicLinksLimitPremium));
        arrayList.add(new Limit(LocaleController.getString("SavedGifsLimitTitle", R.string.SavedGifsLimitTitle), LocaleController.formatString("SavedGifsLimitSubtitle", R.string.SavedGifsLimitSubtitle, Integer.valueOf(messagesController.savedGifsLimitPremium)), messagesController.savedGifsLimitDefault, messagesController.savedGifsLimitPremium));
        arrayList.add(new Limit(LocaleController.getString("FavoriteStickersLimitTitle", R.string.FavoriteStickersLimitTitle), LocaleController.formatString("FavoriteStickersLimitSubtitle", R.string.FavoriteStickersLimitSubtitle, Integer.valueOf(messagesController.stickersFavedLimitPremium)), messagesController.stickersFavedLimitDefault, messagesController.stickersFavedLimitPremium));
        arrayList.add(new Limit(LocaleController.getString("BioLimitTitle", R.string.BioLimitTitle), LocaleController.formatString("BioLimitSubtitle", R.string.BioLimitSubtitle, Integer.valueOf(messagesController.stickersFavedLimitPremium)), messagesController.aboutLengthLimitDefault, messagesController.aboutLengthLimitPremium));
        arrayList.add(new Limit(LocaleController.getString("CaptionsLimitTitle", R.string.CaptionsLimitTitle), LocaleController.formatString("CaptionsLimitSubtitle", R.string.CaptionsLimitSubtitle, Integer.valueOf(messagesController.stickersFavedLimitPremium)), messagesController.captionLengthLimitDefault, messagesController.captionLengthLimitPremium));
        arrayList.add(new Limit(LocaleController.getString("FoldersLimitTitle", R.string.FoldersLimitTitle), LocaleController.formatString("FoldersLimitSubtitle", R.string.FoldersLimitSubtitle, Integer.valueOf(messagesController.dialogFiltersLimitPremium)), messagesController.dialogFiltersLimitDefault, messagesController.dialogFiltersLimitPremium));
        arrayList.add(new Limit(LocaleController.getString("ChatPerFolderLimitTitle", R.string.ChatPerFolderLimitTitle), LocaleController.formatString("ChatPerFolderLimitSubtitle", R.string.ChatPerFolderLimitSubtitle, Integer.valueOf(messagesController.dialogFiltersChatsLimitPremium)), messagesController.dialogFiltersChatsLimitDefault, messagesController.dialogFiltersChatsLimitPremium));
        arrayList.add(new Limit(LocaleController.getString("ConnectedAccountsLimitTitle", R.string.ConnectedAccountsLimitTitle), LocaleController.formatString("ConnectedAccountsLimitSubtitle", R.string.ConnectedAccountsLimitSubtitle, 4), 3, 4));
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.limitsStartRow = i;
        int size = i + arrayList.size();
        this.rowCount = size;
        this.limitsStartEnd = size;
        TextView textView = new TextView(getContext());
        this.titleView = textView;
        textView.setText(LocaleController.getString("DoubledLimits", R.string.DoubledLimits));
        this.titleView.setGravity(17);
        this.titleView.setTextSize(1, 20.0f);
        this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.titleView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.titleLayout.addView(this.titleView, LayoutHelper.createFrame(-2, -2, 16));
        ImageView imageView = new ImageView(getContext());
        this.titleImage = imageView;
        imageView.setImageDrawable(PremiumGradient.getInstance().createGradientDrawable(ContextCompat.getDrawable(getContext(), R.drawable.other_2x_large)));
        this.titleLayout.addView(this.titleImage, LayoutHelper.createFrame(40, 28, 16));
        this.containerView.addView(this.titleLayout, LayoutHelper.createFrame(-1, 40.0f));
        View view = new View(getContext()) { // from class: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.1
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), 1.0f, Theme.dividerPaint);
            }
        };
        this.divider = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.containerView.addView(this.divider, LayoutHelper.createFrame(-1, 72.0f, 80, 0.0f, 0.0f, 0.0f, 0.0f));
        PremiumButtonView premiumButtonView = new PremiumButtonView(getContext(), true);
        this.premiumButtonView = premiumButtonView;
        premiumButtonView.buttonTextView.setText(PremiumPreviewFragment.getPremiumButtonText(currentAccount));
        this.containerView.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 0.0f, 16.0f, 12.0f));
        this.premiumButtonView.buttonLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                DoubledLimitsBottomSheet.this.m2880x68c65b8b(currentAccount, fragment, view2);
            }
        });
        this.premiumButtonView.overlayTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                DoubledLimitsBottomSheet.this.m2881x233bfc0c(view2);
            }
        });
        this.recyclerListView.setPadding(0, 0, 0, AndroidUtilities.dp(72.0f));
        bindPremium(UserConfig.getInstance(getCurrentAccount()).isPremium());
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-Premium-DoubledLimitsBottomSheet */
    public /* synthetic */ void m2880x68c65b8b(int currentAccount, BaseFragment fragment, View view) {
        if (!UserConfig.getInstance(currentAccount).isPremium()) {
            PremiumPreviewFragment.buyPremium(fragment, "double_limits");
        }
        dismiss();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-Premium-DoubledLimitsBottomSheet */
    public /* synthetic */ void m2881x233bfc0c(View v) {
        dismiss();
    }

    private void bindPremium(boolean hasPremium) {
        if (hasPremium) {
            this.premiumButtonView.setOverlayText(LocaleController.getString("OK", R.string.OK), false, false);
        }
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    public void onPreMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onPreMeasure(widthMeasureSpec, heightMeasureSpec);
        measureGradient(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    protected void onPreDraw(Canvas canvas, int top, float progressToFullView) {
        float minTop = AndroidUtilities.statusBarHeight + (((this.actionBar.getMeasuredHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(40.0f)) / 2.0f);
        float fromIconX = (((this.titleLayout.getMeasuredWidth() - this.titleView.getMeasuredWidth()) - this.titleImage.getMeasuredWidth()) - AndroidUtilities.dp(6.0f)) / 2.0f;
        float toIconX = (AndroidUtilities.dp(72.0f) - this.titleImage.getMeasuredWidth()) - AndroidUtilities.dp(6.0f);
        float fromX = this.titleImage.getMeasuredWidth() + fromIconX + AndroidUtilities.dp(6.0f);
        float toX = AndroidUtilities.dp(72.0f);
        float fromY = Math.max(AndroidUtilities.dp(24.0f) + top, minTop);
        if (progressToFullView > 0.0f) {
            float f = this.titleProgress;
            if (f != 1.0f) {
                float f2 = f + 0.10666667f;
                this.titleProgress = f2;
                if (f2 > 1.0f) {
                    this.titleProgress = 1.0f;
                }
                this.containerView.invalidate();
                FrameLayout frameLayout = this.titleLayout;
                float f3 = this.titleProgress;
                frameLayout.setTranslationY(((1.0f - f3) * fromY) + (f3 * minTop));
                TextView textView = this.titleView;
                float f4 = this.titleProgress;
                textView.setTranslationX(((1.0f - f4) * fromX) + (f4 * toX));
                ImageView imageView = this.titleImage;
                float f5 = this.titleProgress;
                imageView.setTranslationX(((1.0f - f5) * fromIconX) + (f5 * toIconX));
                this.titleImage.setAlpha(1.0f - this.titleProgress);
                float s = ((1.0f - this.titleProgress) * 0.4f) + 0.6f;
                this.titleImage.setScaleX(s);
                this.titleImage.setScaleY(s);
            }
        }
        if (progressToFullView == 0.0f) {
            float f6 = this.titleProgress;
            if (f6 != 0.0f) {
                float f7 = f6 - 0.10666667f;
                this.titleProgress = f7;
                if (f7 < 0.0f) {
                    this.titleProgress = 0.0f;
                }
                this.containerView.invalidate();
            }
        }
        FrameLayout frameLayout2 = this.titleLayout;
        float f32 = this.titleProgress;
        frameLayout2.setTranslationY(((1.0f - f32) * fromY) + (f32 * minTop));
        TextView textView2 = this.titleView;
        float f42 = this.titleProgress;
        textView2.setTranslationX(((1.0f - f42) * fromX) + (f42 * toX));
        ImageView imageView2 = this.titleImage;
        float f52 = this.titleProgress;
        imageView2.setTranslationX(((1.0f - f52) * fromIconX) + (f52 * toIconX));
        this.titleImage.setAlpha(1.0f - this.titleProgress);
        float s2 = ((1.0f - this.titleProgress) * 0.4f) + 0.6f;
        this.titleImage.setScaleX(s2);
        this.titleImage.setScaleY(s2);
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    protected CharSequence getTitle() {
        return null;
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    protected RecyclerListView.SelectionAdapter createAdapter() {
        return new RecyclerListView.SelectionAdapter() { // from class: org.telegram.ui.Components.Premium.DoubledLimitsBottomSheet.2
            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return false;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view;
                Context context = parent.getContext();
                switch (viewType) {
                    case 1:
                        view = new FixedHeightEmptyCell(context, 64);
                        break;
                    case 2:
                        view = new FixedHeightEmptyCell(context, 16);
                        break;
                    default:
                        LimitCell limitCell = new LimitCell(context);
                        limitCell.previewView.setParentViewForGradien(DoubledLimitsBottomSheet.this.containerView);
                        limitCell.previewView.setStaticGradinet(DoubledLimitsBottomSheet.this.gradientTools);
                        view = limitCell;
                        break;
                }
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(view);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                if (holder.getItemViewType() == 0) {
                    LimitCell limitCell = (LimitCell) holder.itemView;
                    limitCell.setData(DoubledLimitsBottomSheet.this.limits.get(position - DoubledLimitsBottomSheet.this.limitsStartRow));
                    limitCell.previewView.gradientYOffset = DoubledLimitsBottomSheet.this.limits.get(position - DoubledLimitsBottomSheet.this.limitsStartRow).yOffset;
                    limitCell.previewView.gradientTotalHeight = DoubledLimitsBottomSheet.this.totalGradientHeight;
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return DoubledLimitsBottomSheet.this.rowCount;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int position) {
                if (position == DoubledLimitsBottomSheet.this.headerRow) {
                    return 1;
                }
                if (position == DoubledLimitsBottomSheet.this.lastViewRow) {
                    return 2;
                }
                return 0;
            }
        };
    }

    public void setParentFragment(PremiumPreviewFragment premiumPreviewFragment) {
        this.premiumPreviewFragment = premiumPreviewFragment;
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
            this.premiumButtonView.buttonTextView.setText(PremiumPreviewFragment.getPremiumButtonText(this.currentAccount));
        } else if (id == NotificationCenter.currentUserPremiumStatusChanged) {
            bindPremium(UserConfig.getInstance(this.currentAccount).isPremium());
        }
    }

    /* loaded from: classes5.dex */
    public class LimitCell extends LinearLayout {
        LimitPreviewView previewView;
        TextView subtitle;
        TextView title;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LimitCell(Context context) {
            super(context);
            DoubledLimitsBottomSheet.this = r12;
            setOrientation(1);
            setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
            TextView textView = new TextView(context);
            this.title = textView;
            textView.setTextSize(1, 15.0f);
            this.title.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.title.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(this.title, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 0, 16, 0));
            TextView textView2 = new TextView(context);
            this.subtitle = textView2;
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            this.subtitle.setTextSize(1, 14.0f);
            addView(this.subtitle, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 16, 1, 16, 0));
            LimitPreviewView limitPreviewView = new LimitPreviewView(context, 0, 10, 20);
            this.previewView = limitPreviewView;
            addView(limitPreviewView, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 8, 0, 21));
        }

        public void setData(Limit limit) {
            this.title.setText(limit.title);
            this.subtitle.setText(limit.subtitle);
            this.previewView.premiumCount.setText(Integer.toString(limit.premiumLimit));
            this.previewView.defaultCount.setText(Integer.toString(limit.defaultLimit));
        }
    }

    private void measureGradient(int w, int h) {
        int yOffset = 0;
        LimitCell dummyCell = new LimitCell(getContext());
        for (int i = 0; i < this.limits.size(); i++) {
            dummyCell.setData(this.limits.get(i));
            dummyCell.measure(View.MeasureSpec.makeMeasureSpec(w, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(h, Integer.MIN_VALUE));
            this.limits.get(i).yOffset = yOffset;
            yOffset += dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = yOffset;
    }

    /* loaded from: classes5.dex */
    public static class Limit {
        final int current;
        final int defaultLimit;
        final int premiumLimit;
        final String subtitle;
        final String title;
        public int yOffset;

        private Limit(String title, String subtitle, int defaultLimit, int premiumLimit) {
            this.current = -1;
            this.title = title;
            this.subtitle = subtitle;
            this.defaultLimit = defaultLimit;
            this.premiumLimit = premiumLimit;
        }
    }
}
