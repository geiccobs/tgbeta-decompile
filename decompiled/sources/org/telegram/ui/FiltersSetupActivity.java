package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FiltersSetupActivity;
/* loaded from: classes4.dex */
public class FiltersSetupActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ListAdapter adapter;
    private int createFilterRow;
    private int createSectionRow;
    private int filterHelpRow;
    private int filtersEndRow;
    private int filtersHeaderRow;
    private int filtersStartRow;
    private boolean ignoreUpdates;
    private ItemTouchHelper itemTouchHelper;
    private RecyclerListView listView;
    private boolean orderChanged;
    private int recommendedEndRow;
    private int recommendedHeaderRow;
    private int recommendedSectionRow;
    private int recommendedStartRow;
    private int rowCount = 0;
    private boolean showAllChats;

    /* loaded from: classes4.dex */
    public static class TextCell extends FrameLayout {
        private ImageView imageView;
        private SimpleTextView textView;

        public TextCell(Context context) {
            super(context);
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.textView = simpleTextView;
            simpleTextView.setTextSize(16);
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2));
            this.textView.setTag(Theme.key_windowBackgroundWhiteBlueText2);
            addView(this.textView);
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            AndroidUtilities.dp(48.0f);
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(94.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), C.BUFFER_FLAG_ENCRYPTED));
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), C.BUFFER_FLAG_ENCRYPTED));
            setMeasuredDimension(width, AndroidUtilities.dp(50.0f));
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int viewLeft;
            int height = bottom - top;
            int width = right - left;
            int viewTop = (height - this.textView.getTextHeight()) / 2;
            float f = 64.0f;
            if (LocaleController.isRTL) {
                int measuredWidth = getMeasuredWidth() - this.textView.getMeasuredWidth();
                if (this.imageView.getVisibility() != 0) {
                    f = 23.0f;
                }
                viewLeft = measuredWidth - AndroidUtilities.dp(f);
            } else {
                if (this.imageView.getVisibility() != 0) {
                    f = 23.0f;
                }
                viewLeft = AndroidUtilities.dp(f);
            }
            SimpleTextView simpleTextView = this.textView;
            simpleTextView.layout(viewLeft, viewTop, simpleTextView.getMeasuredWidth() + viewLeft, this.textView.getMeasuredHeight() + viewTop);
            int viewLeft2 = !LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(20.0f);
            ImageView imageView = this.imageView;
            imageView.layout(viewLeft2, 0, imageView.getMeasuredWidth() + viewLeft2, this.imageView.getMeasuredHeight());
        }

        public void setTextAndIcon(String text, Drawable icon, boolean divider) {
            this.textView.setText(text);
            this.imageView.setImageDrawable(icon);
        }
    }

    /* loaded from: classes4.dex */
    public static class SuggestedFilterCell extends FrameLayout {
        private ProgressButton addButton;
        private boolean needDivider;
        private TLRPC.TL_dialogFilterSuggested suggestedFilter;
        private TextView textView;
        private TextView valueTextView;

        public SuggestedFilterCell(Context context) {
            super(context);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 22.0f, 10.0f, 22.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.valueTextView = textView2;
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 22.0f, 35.0f, 22.0f, 0.0f));
            ProgressButton progressButton = new ProgressButton(context);
            this.addButton = progressButton;
            progressButton.setText(LocaleController.getString("Add", R.string.Add));
            this.addButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            this.addButton.setProgressColor(Theme.getColor(Theme.key_featuredStickers_buttonProgress));
            this.addButton.setBackgroundRoundRect(Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed));
            addView(this.addButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(64.0f));
            measureChildWithMargins(this.addButton, widthMeasureSpec, 0, heightMeasureSpec, 0);
            measureChildWithMargins(this.textView, widthMeasureSpec, this.addButton.getMeasuredWidth(), heightMeasureSpec, 0);
            measureChildWithMargins(this.valueTextView, widthMeasureSpec, this.addButton.getMeasuredWidth(), heightMeasureSpec, 0);
        }

        public void setFilter(TLRPC.TL_dialogFilterSuggested filter, boolean divider) {
            this.needDivider = divider;
            this.suggestedFilter = filter;
            setWillNotDraw(!divider);
            this.textView.setText(filter.filter.title);
            this.valueTextView.setText(filter.description);
        }

        public TLRPC.TL_dialogFilterSuggested getSuggestedFilter() {
            return this.suggestedFilter;
        }

        public void setAddOnClickListener(View.OnClickListener onClickListener) {
            this.addButton.setOnClickListener(onClickListener);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(0.0f, getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setEnabled(true);
            info.setText(this.addButton.getText());
            info.setClassName("android.widget.Button");
        }
    }

    /* loaded from: classes4.dex */
    public static class HintInnerCell extends FrameLayout {
        private RLottieImageView imageView;
        private TextView messageTextView;

        public HintInnerCell(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(R.raw.filters, 90, 90);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.playAnimation();
            this.imageView.setImportantForAccessibility(2);
            addView(this.imageView, LayoutHelper.createFrame(90, 90.0f, 49, 0.0f, 14.0f, 0.0f, 0.0f));
            this.imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.FiltersSetupActivity$HintInnerCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FiltersSetupActivity.HintInnerCell.this.m3453lambda$new$0$orgtelegramuiFiltersSetupActivity$HintInnerCell(view);
                }
            });
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("CreateNewFilterInfo", R.string.CreateNewFilterInfo, new Object[0])));
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 49, 40.0f, 121.0f, 40.0f, 24.0f));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-FiltersSetupActivity$HintInnerCell */
        public /* synthetic */ void m3453lambda$new$0$orgtelegramuiFiltersSetupActivity$HintInnerCell(View v) {
            if (!this.imageView.isPlaying()) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
        }
    }

    /* loaded from: classes4.dex */
    public static class FilterCell extends FrameLayout {
        private MessagesController.DialogFilter currentFilter;
        private ImageView moveImageView;
        private boolean needDivider;
        private ImageView optionsImageView;
        float progressToLock;
        private SimpleTextView textView;
        private TextView valueTextView;

        public FilterCell(Context context) {
            super(context);
            setWillNotDraw(false);
            ImageView imageView = new ImageView(context);
            this.moveImageView = imageView;
            imageView.setFocusable(false);
            this.moveImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.moveImageView.setImageResource(R.drawable.list_reorder);
            this.moveImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), PorterDuff.Mode.MULTIPLY));
            this.moveImageView.setContentDescription(LocaleController.getString("FilterReorder", R.string.FilterReorder));
            this.moveImageView.setClickable(true);
            int i = 5;
            addView(this.moveImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.textView = simpleTextView;
            simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(16);
            this.textView.setMaxLines(1);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.other_lockedfolders2);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), PorterDuff.Mode.MULTIPLY));
            this.textView.setRightDrawable(drawable);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 80.0f : 64.0f, 14.0f, LocaleController.isRTL ? 64.0f : 80.0f, 0.0f));
            TextView textView = new TextView(context);
            this.valueTextView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setPadding(0, 0, 0, 0);
            this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 80.0f : 64.0f, 35.0f, LocaleController.isRTL ? 64.0f : 80.0f, 0.0f));
            this.valueTextView.setVisibility(8);
            ImageView imageView2 = new ImageView(context);
            this.optionsImageView = imageView2;
            imageView2.setFocusable(false);
            this.optionsImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            this.optionsImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), PorterDuff.Mode.MULTIPLY));
            this.optionsImageView.setImageResource(R.drawable.msg_actions);
            this.optionsImageView.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            addView(this.optionsImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 3 : i) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setFilter(MessagesController.DialogFilter filter, boolean divider) {
            MessagesController.DialogFilter dialogFilter = this.currentFilter;
            int newId = -1;
            int oldId = dialogFilter == null ? -1 : dialogFilter.id;
            this.currentFilter = filter;
            if (filter != null) {
                newId = filter.id;
            }
            boolean animated = oldId != newId;
            StringBuilder info = new StringBuilder();
            if (filter.isDefault() || (filter.flags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) {
                info.append(LocaleController.getString("FilterAllChats", R.string.FilterAllChats));
            } else {
                if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0) {
                    if (info.length() != 0) {
                        info.append(", ");
                    }
                    info.append(LocaleController.getString("FilterContacts", R.string.FilterContacts));
                }
                if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0) {
                    if (info.length() != 0) {
                        info.append(", ");
                    }
                    info.append(LocaleController.getString("FilterNonContacts", R.string.FilterNonContacts));
                }
                if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0) {
                    if (info.length() != 0) {
                        info.append(", ");
                    }
                    info.append(LocaleController.getString("FilterGroups", R.string.FilterGroups));
                }
                if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0) {
                    if (info.length() != 0) {
                        info.append(", ");
                    }
                    info.append(LocaleController.getString("FilterChannels", R.string.FilterChannels));
                }
                if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0) {
                    if (info.length() != 0) {
                        info.append(", ");
                    }
                    info.append(LocaleController.getString("FilterBots", R.string.FilterBots));
                }
            }
            if (!filter.alwaysShow.isEmpty() || !filter.neverShow.isEmpty()) {
                if (info.length() != 0) {
                    info.append(", ");
                }
                info.append(LocaleController.formatPluralString("Exception", filter.alwaysShow.size() + filter.neverShow.size(), new Object[0]));
            }
            if (info.length() == 0) {
                info.append(LocaleController.getString("FilterNoChats", R.string.FilterNoChats));
            }
            String name = filter.name;
            if (filter.isDefault()) {
                name = LocaleController.getString("FilterAllChats", R.string.FilterAllChats);
            }
            if (!animated) {
                this.progressToLock = this.currentFilter.locked ? 1.0f : 0.0f;
            }
            SimpleTextView simpleTextView = this.textView;
            simpleTextView.setText(Emoji.replaceEmoji(name, simpleTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
            this.valueTextView.setText(info);
            this.needDivider = divider;
            if (!filter.isDefault()) {
                this.optionsImageView.setVisibility(0);
            } else {
                this.optionsImageView.setVisibility(8);
            }
            invalidate();
        }

        public MessagesController.DialogFilter getCurrentFilter() {
            return this.currentFilter;
        }

        public void setOnOptionsClick(View.OnClickListener listener) {
            this.optionsImageView.setOnClickListener(listener);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(62.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(62.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
            MessagesController.DialogFilter dialogFilter = this.currentFilter;
            if (dialogFilter != null) {
                if (dialogFilter.locked) {
                    float f = this.progressToLock;
                    if (f != 1.0f) {
                        this.progressToLock = f + 0.10666667f;
                        invalidate();
                    }
                }
                if (!this.currentFilter.locked) {
                    float f2 = this.progressToLock;
                    if (f2 != 0.0f) {
                        this.progressToLock = f2 - 0.10666667f;
                        invalidate();
                    }
                }
            }
            float clamp = Utilities.clamp(this.progressToLock, 1.0f, 0.0f);
            this.progressToLock = clamp;
            this.textView.setRightDrawableScale(clamp);
            this.textView.invalidate();
        }

        public void setOnReorderButtonTouchListener(View.OnTouchListener listener) {
            this.moveImageView.setOnTouchListener(listener);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        updateRows(true);
        getMessagesController().loadRemoteFilters(true);
        getNotificationCenter().addObserver(this, NotificationCenter.dialogFiltersUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.suggestedFiltersLoaded);
        if (getMessagesController().suggestedFilters.isEmpty()) {
            getMessagesController().loadSuggestedFilters();
        }
        return super.onFragmentCreate();
    }

    public void updateRows(boolean notify) {
        ListAdapter listAdapter;
        this.recommendedHeaderRow = -1;
        this.recommendedStartRow = -1;
        this.recommendedEndRow = -1;
        this.recommendedSectionRow = -1;
        ArrayList<TLRPC.TL_dialogFilterSuggested> suggestedFilters = getMessagesController().suggestedFilters;
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.filterHelpRow = 0;
        int count = getMessagesController().dialogFilters.size();
        if (!getUserConfig().isPremium()) {
            count--;
            this.showAllChats = false;
        } else {
            this.showAllChats = true;
        }
        if (!suggestedFilters.isEmpty() && count < 10) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.recommendedHeaderRow = i;
            this.recommendedStartRow = i2;
            int size = i2 + suggestedFilters.size();
            this.rowCount = size;
            this.recommendedEndRow = size;
            this.rowCount = size + 1;
            this.recommendedSectionRow = size;
        }
        if (count != 0) {
            int i3 = this.rowCount;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.filtersHeaderRow = i3;
            this.filtersStartRow = i4;
            int i5 = i4 + count;
            this.rowCount = i5;
            this.filtersEndRow = i5;
        } else {
            this.filtersHeaderRow = -1;
            this.filtersStartRow = -1;
            this.filtersEndRow = -1;
        }
        if (count < getMessagesController().dialogFiltersLimitPremium) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.createFilterRow = i6;
        } else {
            this.createFilterRow = -1;
        }
        int i7 = this.rowCount;
        this.rowCount = i7 + 1;
        this.createSectionRow = i7;
        if (notify && (listAdapter = this.adapter) != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.dialogFiltersUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.suggestedFiltersLoaded);
        if (this.orderChanged) {
            getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
            getMessagesStorage().saveDialogFiltersOrder();
            TLRPC.TL_messages_updateDialogFiltersOrder req = new TLRPC.TL_messages_updateDialogFiltersOrder();
            ArrayList<MessagesController.DialogFilter> filters = getMessagesController().dialogFilters;
            int N = filters.size();
            for (int a = 0; a < N; a++) {
                MessagesController.DialogFilter filter = filters.get(a);
                req.order.add(Integer.valueOf(filter.id));
            }
            getConnectionsManager().sendRequest(req, FiltersSetupActivity$$ExternalSyntheticLambda0.INSTANCE);
        }
        super.onFragmentDestroy();
    }

    public static /* synthetic */ void lambda$onFragmentDestroy$0(TLObject response, TLRPC.TL_error error) {
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Filters", R.string.Filters));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.FiltersSetupActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    FiltersSetupActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        AnonymousClass2 anonymousClass2 = new AnonymousClass2(context);
        this.listView = anonymousClass2;
        ((DefaultItemAnimator) anonymousClass2.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelperCallback());
        this.itemTouchHelper = itemTouchHelper;
        itemTouchHelper.attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.FiltersSetupActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i, float f, float f2) {
                FiltersSetupActivity.this.m3451lambda$createView$1$orgtelegramuiFiltersSetupActivity(context, view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    /* renamed from: org.telegram.ui.FiltersSetupActivity$2 */
    /* loaded from: classes4.dex */
    public class AnonymousClass2 extends RecyclerListView {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass2(Context context) {
            super(context);
            FiltersSetupActivity.this = this$0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
        public boolean onTouchEvent(MotionEvent e) {
            if (e.getAction() == 1 || e.getAction() == 3) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FiltersSetupActivity$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        FiltersSetupActivity.AnonymousClass2.this.m3452lambda$onTouchEvent$0$orgtelegramuiFiltersSetupActivity$2();
                    }
                }, 250L);
            }
            return super.onTouchEvent(e);
        }

        /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-FiltersSetupActivity$2 */
        public /* synthetic */ void m3452lambda$onTouchEvent$0$orgtelegramuiFiltersSetupActivity$2() {
            FiltersSetupActivity.this.getMessagesController().lockFiltersInternal();
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-FiltersSetupActivity */
    public /* synthetic */ void m3451lambda$createView$1$orgtelegramuiFiltersSetupActivity(Context context, View view, int position, float x, float y) {
        int i = this.filtersStartRow;
        if (position >= i && position < this.filtersEndRow) {
            int filterPosition = position - i;
            if (!this.showAllChats) {
                filterPosition++;
            }
            if (getMessagesController().dialogFilters.get(filterPosition).isDefault()) {
                return;
            }
            MessagesController.DialogFilter filter = getMessagesController().dialogFilters.get(filterPosition);
            if (filter.locked) {
                showDialog(new LimitReachedBottomSheet(this, context, 3, this.currentAccount));
            } else {
                presentFragment(new FilterCreateActivity(getMessagesController().dialogFilters.get(filterPosition)));
            }
        } else if (position == this.createFilterRow) {
            if ((getMessagesController().dialogFilters.size() - 1 >= getMessagesController().dialogFiltersLimitDefault && !getUserConfig().isPremium()) || getMessagesController().dialogFilters.size() >= getMessagesController().dialogFiltersLimitPremium) {
                showDialog(new LimitReachedBottomSheet(this, context, 3, this.currentAccount));
            } else {
                presentFragment(new FilterCreateActivity());
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.dialogFiltersUpdated) {
            if (this.ignoreUpdates) {
                return;
            }
            int rowCount = this.rowCount;
            updateRows(false);
            if (rowCount == this.rowCount) {
                this.adapter.notifyItemRangeChanged(0, rowCount);
            } else {
                this.adapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.suggestedFiltersLoaded) {
            updateRows(true);
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            FiltersSetupActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return (type == 3 || type == 0 || type == 5 || type == 1) ? false : true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return FiltersSetupActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = headerCell;
                    break;
                case 1:
                    View view2 = new HintInnerCell(this.mContext);
                    view2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
                    view = view2;
                    break;
                case 2:
                    final FilterCell filterCell = new FilterCell(this.mContext);
                    filterCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    filterCell.setOnReorderButtonTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda4
                        @Override // android.view.View.OnTouchListener
                        public final boolean onTouch(View view3, MotionEvent motionEvent) {
                            return FiltersSetupActivity.ListAdapter.this.m3454x834c8a63(filterCell, view3, motionEvent);
                        }
                    });
                    filterCell.setOnOptionsClick(new View.OnClickListener() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view3) {
                            FiltersSetupActivity.ListAdapter.this.m3459x3b9cc8fe(view3);
                        }
                    });
                    view = filterCell;
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    View textCell = new TextCell(this.mContext);
                    textCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = textCell;
                    break;
                default:
                    final SuggestedFilterCell suggestedFilterCell = new SuggestedFilterCell(this.mContext);
                    suggestedFilterCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    suggestedFilterCell.setAddOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda3
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view3) {
                            FiltersSetupActivity.ListAdapter.this.m3461x1ef0153c(suggestedFilterCell, view3);
                        }
                    });
                    view = suggestedFilterCell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-FiltersSetupActivity$ListAdapter */
        public /* synthetic */ boolean m3454x834c8a63(FilterCell filterCell, View v, MotionEvent event) {
            if (event.getAction() == 0) {
                FiltersSetupActivity.this.itemTouchHelper.startDrag(FiltersSetupActivity.this.listView.getChildViewHolder(filterCell));
                return false;
            }
            return false;
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-FiltersSetupActivity$ListAdapter */
        public /* synthetic */ void m3459x3b9cc8fe(View v) {
            FilterCell cell = (FilterCell) v.getParent();
            final MessagesController.DialogFilter filter = cell.getCurrentFilter();
            AlertDialog.Builder builder1 = new AlertDialog.Builder(FiltersSetupActivity.this.getParentActivity());
            TextPaint paint = new TextPaint(1);
            paint.setTextSize(AndroidUtilities.dp(20.0f));
            builder1.setTitle(Emoji.replaceEmoji(filter.name, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
            CharSequence[] items = {LocaleController.getString("FilterEditItem", R.string.FilterEditItem), LocaleController.getString("FilterDeleteItem", R.string.FilterDeleteItem)};
            int[] icons = {R.drawable.msg_edit, R.drawable.msg_delete};
            builder1.setItems(items, icons, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FiltersSetupActivity.ListAdapter.this.m3458x49f322df(filter, dialogInterface, i);
                }
            });
            AlertDialog dialog = builder1.create();
            FiltersSetupActivity.this.showDialog(dialog);
            dialog.setItemColor(items.length - 1, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
        }

        /* renamed from: lambda$onCreateViewHolder$4$org-telegram-ui-FiltersSetupActivity$ListAdapter */
        public /* synthetic */ void m3458x49f322df(final MessagesController.DialogFilter filter, DialogInterface dialog, int which) {
            if (which == 0) {
                if (filter.locked) {
                    FiltersSetupActivity filtersSetupActivity = FiltersSetupActivity.this;
                    FiltersSetupActivity filtersSetupActivity2 = FiltersSetupActivity.this;
                    filtersSetupActivity.showDialog(new LimitReachedBottomSheet(filtersSetupActivity2, this.mContext, 3, filtersSetupActivity2.currentAccount));
                    return;
                }
                FiltersSetupActivity.this.presentFragment(new FilterCreateActivity(filter));
            } else if (which == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(FiltersSetupActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("FilterDelete", R.string.FilterDelete));
                builder.setMessage(LocaleController.getString("FilterDeleteAlert", R.string.FilterDeleteAlert));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        FiltersSetupActivity.ListAdapter.this.m3457x58497cc0(filter, dialogInterface, i);
                    }
                });
                AlertDialog alertDialog = builder.create();
                FiltersSetupActivity.this.showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            }
        }

        /* renamed from: lambda$onCreateViewHolder$3$org-telegram-ui-FiltersSetupActivity$ListAdapter */
        public /* synthetic */ void m3457x58497cc0(final MessagesController.DialogFilter filter, DialogInterface dialog2, int which2) {
            AlertDialog progressDialog = null;
            if (FiltersSetupActivity.this.getParentActivity() != null) {
                progressDialog = new AlertDialog(FiltersSetupActivity.this.getParentActivity(), 3);
                progressDialog.setCanCancel(false);
                progressDialog.show();
            }
            final AlertDialog progressDialogFinal = progressDialog;
            TLRPC.TL_messages_updateDialogFilter req = new TLRPC.TL_messages_updateDialogFilter();
            req.id = filter.id;
            FiltersSetupActivity.this.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda7
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FiltersSetupActivity.ListAdapter.this.m3456x669fd6a1(progressDialogFinal, filter, tLObject, tL_error);
                }
            });
        }

        /* renamed from: lambda$onCreateViewHolder$2$org-telegram-ui-FiltersSetupActivity$ListAdapter */
        public /* synthetic */ void m3456x669fd6a1(final AlertDialog progressDialogFinal, final MessagesController.DialogFilter filter, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    FiltersSetupActivity.ListAdapter.this.m3455x74f63082(progressDialogFinal, filter);
                }
            });
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-FiltersSetupActivity$ListAdapter */
        public /* synthetic */ void m3455x74f63082(AlertDialog progressDialogFinal, MessagesController.DialogFilter filter) {
            if (progressDialogFinal != null) {
                try {
                    progressDialogFinal.dismiss();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            int idx = FiltersSetupActivity.this.getMessagesController().dialogFilters.indexOf(filter);
            if (idx >= 0) {
                idx += FiltersSetupActivity.this.filtersStartRow;
            }
            if (!FiltersSetupActivity.this.showAllChats) {
                idx--;
            }
            FiltersSetupActivity.this.ignoreUpdates = true;
            FiltersSetupActivity.this.getMessagesController().removeFilter(filter);
            FiltersSetupActivity.this.getMessagesStorage().deleteDialogFilter(filter);
            boolean z = false;
            FiltersSetupActivity.this.ignoreUpdates = false;
            int prevAddRow = FiltersSetupActivity.this.createFilterRow;
            int prevRecommendedHeaderRow = FiltersSetupActivity.this.recommendedHeaderRow;
            FiltersSetupActivity filtersSetupActivity = FiltersSetupActivity.this;
            if (idx == -1) {
                z = true;
            }
            filtersSetupActivity.updateRows(z);
            if (idx != -1) {
                if (FiltersSetupActivity.this.filtersStartRow == -1) {
                    FiltersSetupActivity.this.adapter.notifyItemRangeRemoved(idx - 1, 2);
                } else {
                    FiltersSetupActivity.this.adapter.notifyItemRemoved(idx);
                }
                if (prevRecommendedHeaderRow == -1 && FiltersSetupActivity.this.recommendedHeaderRow != -1) {
                    FiltersSetupActivity.this.adapter.notifyItemRangeInserted(prevRecommendedHeaderRow, (FiltersSetupActivity.this.recommendedSectionRow - FiltersSetupActivity.this.recommendedHeaderRow) + 1);
                }
                if (prevAddRow == -1 && FiltersSetupActivity.this.createFilterRow != -1) {
                    FiltersSetupActivity.this.adapter.notifyItemInserted(FiltersSetupActivity.this.createFilterRow);
                }
            }
        }

        /* renamed from: lambda$onCreateViewHolder$7$org-telegram-ui-FiltersSetupActivity$ListAdapter */
        public /* synthetic */ void m3461x1ef0153c(SuggestedFilterCell suggestedFilterCell, View v) {
            long lowerId;
            final TLRPC.TL_dialogFilterSuggested suggested = suggestedFilterCell.getSuggestedFilter();
            MessagesController.DialogFilter filter = new MessagesController.DialogFilter();
            filter.name = suggested.filter.title;
            filter.id = 2;
            while (FiltersSetupActivity.this.getMessagesController().dialogFiltersById.get(filter.id) != null) {
                filter.id++;
            }
            filter.unreadCount = -1;
            filter.pendingUnreadCount = -1;
            int b = 0;
            while (b < 2) {
                TLRPC.DialogFilter dialogFilter = suggested.filter;
                ArrayList<TLRPC.InputPeer> fromArray = b == 0 ? dialogFilter.include_peers : dialogFilter.exclude_peers;
                ArrayList<Long> toArray = b == 0 ? filter.alwaysShow : filter.neverShow;
                int N = fromArray.size();
                for (int a = 0; a < N; a++) {
                    TLRPC.InputPeer peer = fromArray.get(a);
                    if (peer.user_id != 0) {
                        lowerId = peer.user_id;
                    } else {
                        long lowerId2 = peer.chat_id;
                        if (lowerId2 != 0) {
                            lowerId = -peer.chat_id;
                        } else {
                            long lowerId3 = peer.channel_id;
                            lowerId = -lowerId3;
                        }
                    }
                    toArray.add(Long.valueOf(lowerId));
                }
                b++;
            }
            if (suggested.filter.groups) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_GROUPS;
            }
            if (suggested.filter.bots) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_BOTS;
            }
            if (suggested.filter.contacts) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
            }
            if (suggested.filter.non_contacts) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
            }
            if (suggested.filter.broadcasts) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
            }
            if (suggested.filter.exclude_archived) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
            }
            if (suggested.filter.exclude_read) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
            }
            if (suggested.filter.exclude_muted) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
            }
            FiltersSetupActivity.this.ignoreUpdates = true;
            FilterCreateActivity.saveFilterToServer(filter, filter.flags, filter.name, filter.alwaysShow, filter.neverShow, filter.pinnedDialogs, true, true, true, true, false, FiltersSetupActivity.this, new Runnable() { // from class: org.telegram.ui.FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    FiltersSetupActivity.ListAdapter.this.m3460x2d466f1d(suggested);
                }
            });
        }

        /* renamed from: lambda$onCreateViewHolder$6$org-telegram-ui-FiltersSetupActivity$ListAdapter */
        public /* synthetic */ void m3460x2d466f1d(TLRPC.TL_dialogFilterSuggested suggested) {
            FiltersSetupActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
            FiltersSetupActivity.this.ignoreUpdates = false;
            ArrayList<TLRPC.TL_dialogFilterSuggested> suggestedFilters = FiltersSetupActivity.this.getMessagesController().suggestedFilters;
            int index = suggestedFilters.indexOf(suggested);
            if (index != -1) {
                boolean wasEmpty = FiltersSetupActivity.this.filtersStartRow == -1;
                suggestedFilters.remove(index);
                int index2 = index + FiltersSetupActivity.this.recommendedStartRow;
                int prevAddRow = FiltersSetupActivity.this.createFilterRow;
                int prevRecommendedHeaderRow = FiltersSetupActivity.this.recommendedHeaderRow;
                int prevRecommendedSectionRow = FiltersSetupActivity.this.recommendedSectionRow;
                FiltersSetupActivity.this.updateRows(false);
                if (prevAddRow != -1 && FiltersSetupActivity.this.createFilterRow == -1) {
                    FiltersSetupActivity.this.adapter.notifyItemRemoved(prevAddRow);
                }
                if (prevRecommendedHeaderRow == -1 || FiltersSetupActivity.this.recommendedHeaderRow != -1) {
                    FiltersSetupActivity.this.adapter.notifyItemRemoved(index2);
                } else {
                    FiltersSetupActivity.this.adapter.notifyItemRangeRemoved(prevRecommendedHeaderRow, (prevRecommendedSectionRow - prevRecommendedHeaderRow) + 1);
                }
                if (wasEmpty) {
                    FiltersSetupActivity.this.adapter.notifyItemInserted(FiltersSetupActivity.this.filtersHeaderRow);
                }
                FiltersSetupActivity.this.adapter.notifyItemInserted(FiltersSetupActivity.this.filtersStartRow);
                return;
            }
            FiltersSetupActivity.this.updateRows(true);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position != FiltersSetupActivity.this.filtersHeaderRow) {
                        if (position == FiltersSetupActivity.this.recommendedHeaderRow) {
                            headerCell.setText(LocaleController.getString("FilterRecommended", R.string.FilterRecommended));
                            return;
                        }
                        return;
                    }
                    headerCell.setText(LocaleController.getString("Filters", R.string.Filters));
                    return;
                case 1:
                default:
                    return;
                case 2:
                    FilterCell filterCell = (FilterCell) holder.itemView;
                    int filterPosition = position - FiltersSetupActivity.this.filtersStartRow;
                    if (!FiltersSetupActivity.this.showAllChats) {
                        filterPosition++;
                    }
                    filterCell.setFilter(FiltersSetupActivity.this.getMessagesController().dialogFilters.get(filterPosition), true);
                    return;
                case 3:
                    if (position == FiltersSetupActivity.this.createSectionRow) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 4:
                    TextCell textCell = (TextCell) holder.itemView;
                    MessagesController.getNotificationsSettings(FiltersSetupActivity.this.currentAccount);
                    if (position == FiltersSetupActivity.this.createFilterRow) {
                        Drawable drawable1 = this.mContext.getResources().getDrawable(R.drawable.poll_add_circle);
                        Drawable drawable2 = this.mContext.getResources().getDrawable(R.drawable.poll_add_plus);
                        drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_switchTrackChecked), PorterDuff.Mode.MULTIPLY));
                        drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_checkboxCheck), PorterDuff.Mode.MULTIPLY));
                        CombinedDrawable combinedDrawable = new CombinedDrawable(drawable1, drawable2);
                        textCell.setTextAndIcon(LocaleController.getString("CreateNewFilter", R.string.CreateNewFilter), combinedDrawable, false);
                        return;
                    }
                    return;
                case 5:
                    SuggestedFilterCell filterCell2 = (SuggestedFilterCell) holder.itemView;
                    TLRPC.TL_dialogFilterSuggested tL_dialogFilterSuggested = FiltersSetupActivity.this.getMessagesController().suggestedFilters.get(position - FiltersSetupActivity.this.recommendedStartRow);
                    if (FiltersSetupActivity.this.recommendedStartRow != FiltersSetupActivity.this.recommendedEndRow - 1) {
                        z = true;
                    }
                    filterCell2.setFilter(tL_dialogFilterSuggested, z);
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != FiltersSetupActivity.this.filtersHeaderRow && position != FiltersSetupActivity.this.recommendedHeaderRow) {
                if (position != FiltersSetupActivity.this.filterHelpRow) {
                    if (position < FiltersSetupActivity.this.filtersStartRow || position >= FiltersSetupActivity.this.filtersEndRow) {
                        if (position != FiltersSetupActivity.this.createSectionRow && position != FiltersSetupActivity.this.recommendedSectionRow) {
                            if (position == FiltersSetupActivity.this.createFilterRow) {
                                return 4;
                            }
                            return 5;
                        }
                        return 3;
                    }
                    return 2;
                }
                return 1;
            }
            return 0;
        }

        public void swapElements(int fromIndex, int toIndex) {
            int idx1 = fromIndex - FiltersSetupActivity.this.filtersStartRow;
            int idx2 = toIndex - FiltersSetupActivity.this.filtersStartRow;
            int count = FiltersSetupActivity.this.filtersEndRow - FiltersSetupActivity.this.filtersStartRow;
            if (!FiltersSetupActivity.this.showAllChats) {
                idx1++;
                idx2++;
                count++;
            }
            if (idx1 < 0 || idx2 < 0 || idx1 >= count || idx2 >= count) {
                return;
            }
            ArrayList<MessagesController.DialogFilter> filters = FiltersSetupActivity.this.getMessagesController().dialogFilters;
            MessagesController.DialogFilter filter1 = filters.get(idx1);
            MessagesController.DialogFilter filter2 = filters.get(idx2);
            int temp = filter1.order;
            filter1.order = filter2.order;
            filter2.order = temp;
            filters.set(idx1, filter2);
            filters.set(idx2, filter1);
            FiltersSetupActivity.this.orderChanged = true;
            notifyItemMoved(fromIndex, toIndex);
        }
    }

    /* loaded from: classes4.dex */
    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
            FiltersSetupActivity.this = this$0;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            boolean canMove = FiltersSetupActivity.this.getUserConfig().isPremium() || !(viewHolder.itemView instanceof FilterCell) || !((FilterCell) viewHolder.itemView).currentFilter.isDefault();
            if (viewHolder.getItemViewType() != 2 || !canMove) {
                return makeMovementFlags(0, 0);
            }
            return makeMovementFlags(3, 0);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            boolean canMove = FiltersSetupActivity.this.getUserConfig().isPremium() || !(target.itemView instanceof FilterCell) || !((FilterCell) target.itemView).currentFilter.isDefault();
            if (source.getItemViewType() != target.getItemViewType() || !canMove) {
                return false;
            }
            FiltersSetupActivity.this.adapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                FiltersSetupActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, FilterCell.class, SuggestedFilterCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{FilterCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{FilterCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{FilterCell.class}, new String[]{"moveImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_stickers_menu));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{FilterCell.class}, new String[]{"optionsImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_stickers_menu));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FilterCell.class}, new String[]{"optionsImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_stickers_menuSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText2));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        return themeDescriptions;
    }
}
