package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TooManyCommunitiesHintCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.TooManyCommunitiesActivity;
/* loaded from: classes4.dex */
public class TooManyCommunitiesActivity extends BaseFragment {
    public static final int TYPE_CREATE = 2;
    public static final int TYPE_EDIT = 1;
    public static final int TYPE_JOIN = 0;
    private Adapter adapter;
    private int buttonAnimation;
    private FrameLayout buttonLayout;
    private TextView buttonTextView;
    private EmptyTextProgressView emptyView;
    private ValueAnimator enterAnimator;
    private float enterProgress;
    private TooManyCommunitiesHintCell hintCell;
    private RecyclerListView listView;
    protected RadialProgressView progressBar;
    private SearchAdapter searchAdapter;
    private RecyclerListView searchListView;
    private FrameLayout searchViewContainer;
    int type;
    private ArrayList<TLRPC.Chat> inactiveChats = new ArrayList<>();
    private ArrayList<String> inactiveChatsSignatures = new ArrayList<>();
    private Set<Long> selectedIds = new HashSet();
    private int buttonHeight = AndroidUtilities.dp(64.0f);
    Runnable showProgressRunnable = new Runnable() { // from class: org.telegram.ui.TooManyCommunitiesActivity.1
        @Override // java.lang.Runnable
        public void run() {
            TooManyCommunitiesActivity.this.progressBar.setVisibility(0);
            TooManyCommunitiesActivity.this.progressBar.setAlpha(0.0f);
            TooManyCommunitiesActivity.this.progressBar.animate().alpha(1.0f).start();
        }
    };
    RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.TooManyCommunitiesActivity$$ExternalSyntheticLambda5
        @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
        public final void onItemClick(View view, int i) {
            TooManyCommunitiesActivity.this.m4674lambda$new$0$orgtelegramuiTooManyCommunitiesActivity(view, i);
        }
    };
    RecyclerListView.OnItemLongClickListener onItemLongClickListener = new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.TooManyCommunitiesActivity$$ExternalSyntheticLambda6
        @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
        public final boolean onItemClick(View view, int i) {
            return TooManyCommunitiesActivity.this.m4675lambda$new$1$orgtelegramuiTooManyCommunitiesActivity(view, i);
        }
    };

    /* renamed from: lambda$new$0$org-telegram-ui-TooManyCommunitiesActivity */
    public /* synthetic */ void m4674lambda$new$0$orgtelegramuiTooManyCommunitiesActivity(View view, int position) {
        if (view instanceof GroupCreateUserCell) {
            TLRPC.Chat chat = (TLRPC.Chat) ((GroupCreateUserCell) view).getObject();
            if (this.selectedIds.contains(Long.valueOf(chat.id))) {
                this.selectedIds.remove(Long.valueOf(chat.id));
                ((GroupCreateUserCell) view).setChecked(false, true);
            } else {
                this.selectedIds.add(Long.valueOf(chat.id));
                ((GroupCreateUserCell) view).setChecked(true, true);
            }
            onSelectedCountChange();
            if (!this.selectedIds.isEmpty()) {
                RecyclerListView list = this.searchViewContainer.getVisibility() == 0 ? this.searchListView : this.listView;
                int bottom = list.getHeight() - view.getBottom();
                int i = this.buttonHeight;
                if (bottom < i) {
                    list.smoothScrollBy(0, i - bottom);
                }
            }
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-TooManyCommunitiesActivity */
    public /* synthetic */ boolean m4675lambda$new$1$orgtelegramuiTooManyCommunitiesActivity(View view, int position) {
        this.onItemClickListener.onItemClick(view, position);
        return true;
    }

    public TooManyCommunitiesActivity(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(CommonProperties.TYPE, type);
        this.arguments = bundle;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.type = this.arguments.getInt(CommonProperties.TYPE, 0);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("LimitReached", R.string.LimitReached));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.TooManyCommunitiesActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    TooManyCommunitiesActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem searchItem = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.TooManyCommunitiesActivity.3
            boolean expanded = false;

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                super.onSearchCollapse();
                if (TooManyCommunitiesActivity.this.listView.getVisibility() != 0) {
                    TooManyCommunitiesActivity.this.listView.setVisibility(0);
                    TooManyCommunitiesActivity.this.listView.setAlpha(0.0f);
                }
                TooManyCommunitiesActivity.this.emptyView.setVisibility(8);
                TooManyCommunitiesActivity.this.adapter.notifyDataSetChanged();
                TooManyCommunitiesActivity.this.listView.animate().alpha(1.0f).setDuration(150L).setListener(null).start();
                TooManyCommunitiesActivity.this.searchViewContainer.animate().alpha(0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.TooManyCommunitiesActivity.3.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        TooManyCommunitiesActivity.this.searchViewContainer.setVisibility(8);
                    }
                }).start();
                this.expanded = false;
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                String query = editText.getText().toString();
                TooManyCommunitiesActivity.this.searchAdapter.search(query);
                if (!this.expanded && !TextUtils.isEmpty(query)) {
                    if (TooManyCommunitiesActivity.this.searchViewContainer.getVisibility() != 0) {
                        TooManyCommunitiesActivity.this.searchViewContainer.setVisibility(0);
                        TooManyCommunitiesActivity.this.searchViewContainer.setAlpha(0.0f);
                    }
                    TooManyCommunitiesActivity.this.listView.animate().alpha(0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.TooManyCommunitiesActivity.3.2
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            TooManyCommunitiesActivity.this.listView.setVisibility(8);
                        }
                    }).start();
                    TooManyCommunitiesActivity.this.searchAdapter.searchResultsSignatures.clear();
                    TooManyCommunitiesActivity.this.searchAdapter.searchResults.clear();
                    TooManyCommunitiesActivity.this.searchAdapter.notifyDataSetChanged();
                    TooManyCommunitiesActivity.this.searchViewContainer.animate().setListener(null).alpha(1.0f).setDuration(150L).start();
                    this.expanded = true;
                } else if (this.expanded && TextUtils.isEmpty(query)) {
                    onSearchCollapse();
                }
            }
        });
        searchItem.setContentDescription(LocaleController.getString("Search", R.string.Search));
        searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        FrameLayout contentView = new FrameLayout(context);
        this.fragmentView = contentView;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView2 = this.listView;
        Adapter adapter = new Adapter();
        this.adapter = adapter;
        recyclerListView2.setAdapter(adapter);
        this.listView.setClipToPadding(false);
        this.listView.setOnItemClickListener(this.onItemClickListener);
        this.listView.setOnItemLongClickListener(this.onItemLongClickListener);
        RecyclerListView recyclerListView3 = new RecyclerListView(context);
        this.searchListView = recyclerListView3;
        recyclerListView3.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView4 = this.searchListView;
        SearchAdapter searchAdapter = new SearchAdapter();
        this.searchAdapter = searchAdapter;
        recyclerListView4.setAdapter(searchAdapter);
        this.searchListView.setOnItemClickListener(this.onItemClickListener);
        this.searchListView.setOnItemLongClickListener(this.onItemLongClickListener);
        this.searchListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.TooManyCommunitiesActivity.4
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(TooManyCommunitiesActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyView.showTextView();
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        contentView.addView(radialProgressView, LayoutHelper.createFrame(-2, -2.0f));
        this.adapter.updateRows();
        this.progressBar.setVisibility(8);
        contentView.addView(this.listView);
        FrameLayout frameLayout = new FrameLayout(context);
        this.searchViewContainer = frameLayout;
        frameLayout.addView(this.searchListView);
        this.searchViewContainer.addView(this.emptyView);
        this.searchViewContainer.setVisibility(8);
        contentView.addView(this.searchViewContainer);
        loadInactiveChannels();
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.TooManyCommunitiesActivity.5
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), 1.0f, Theme.dividerPaint);
            }
        };
        this.buttonLayout = frameLayout2;
        frameLayout2.setWillNotDraw(false);
        TextView textView = new TextView(context);
        this.buttonTextView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.buttonTextView.setGravity(17);
        this.buttonTextView.setTextSize(1, 14.0f);
        this.buttonTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.buttonTextView.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
        contentView.addView(this.buttonLayout, LayoutHelper.createFrame(-1, 64, 80));
        this.buttonLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.buttonLayout.addView(this.buttonTextView, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 12.0f, 16.0f, 12.0f));
        this.buttonLayout.setVisibility(8);
        this.buttonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.TooManyCommunitiesActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TooManyCommunitiesActivity.this.m4669lambda$createView$2$orgtelegramuiTooManyCommunitiesActivity(view);
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-TooManyCommunitiesActivity */
    public /* synthetic */ void m4669lambda$createView$2$orgtelegramuiTooManyCommunitiesActivity(View v) {
        if (this.selectedIds.isEmpty()) {
            return;
        }
        TLRPC.User currentUser = getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId()));
        ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        for (int i = 0; i < this.inactiveChats.size(); i++) {
            if (this.selectedIds.contains(Long.valueOf(this.inactiveChats.get(i).id))) {
                chats.add(this.inactiveChats.get(i));
            }
        }
        for (int i2 = 0; i2 < chats.size(); i2++) {
            TLRPC.Chat chat = chats.get(i2);
            getMessagesController().putChat(chat, false);
            getMessagesController().deleteParticipantFromChat(chat.id, currentUser, null);
        }
        finishFragment();
    }

    private void onSelectedCountChange() {
        RecyclerView.ViewHolder holder;
        if (this.selectedIds.isEmpty() && this.buttonAnimation != -1 && this.buttonLayout.getVisibility() == 0) {
            this.buttonAnimation = -1;
            this.buttonLayout.animate().setListener(null).cancel();
            this.buttonLayout.animate().translationY(this.buttonHeight).setDuration(200L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.TooManyCommunitiesActivity.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    TooManyCommunitiesActivity.this.buttonAnimation = 0;
                    TooManyCommunitiesActivity.this.buttonLayout.setVisibility(8);
                }
            }).start();
            RecyclerListView list = this.searchViewContainer.getVisibility() == 0 ? this.searchListView : this.listView;
            list.hideSelector(false);
            int last = ((LinearLayoutManager) list.getLayoutManager()).findLastVisibleItemPosition();
            if ((last == list.getAdapter().getItemCount() - 1 || (last == list.getAdapter().getItemCount() - 2 && list == this.listView)) && (holder = list.findViewHolderForAdapterPosition(last)) != null) {
                int bottom = holder.itemView.getBottom();
                if (last == this.adapter.getItemCount() - 2) {
                    bottom += AndroidUtilities.dp(12.0f);
                }
                if (list.getMeasuredHeight() - bottom <= this.buttonHeight) {
                    int dy = -(list.getMeasuredHeight() - bottom);
                    list.setTranslationY(dy);
                    list.animate().translationY(0.0f).setDuration(200L).start();
                }
            }
            this.listView.setPadding(0, 0, 0, 0);
            this.searchListView.setPadding(0, 0, 0, 0);
        }
        if (!this.selectedIds.isEmpty() && this.buttonLayout.getVisibility() == 8 && this.buttonAnimation != 1) {
            this.buttonAnimation = 1;
            this.buttonLayout.setVisibility(0);
            this.buttonLayout.setTranslationY(this.buttonHeight);
            this.buttonLayout.animate().setListener(null).cancel();
            this.buttonLayout.animate().translationY(0.0f).setDuration(200L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.TooManyCommunitiesActivity.7
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    TooManyCommunitiesActivity.this.buttonAnimation = 0;
                }
            }).start();
            this.listView.setPadding(0, 0, 0, this.buttonHeight - AndroidUtilities.dp(12.0f));
            this.searchListView.setPadding(0, 0, 0, this.buttonHeight);
        }
        if (!this.selectedIds.isEmpty()) {
            this.buttonTextView.setText(LocaleController.formatString("LeaveChats", R.string.LeaveChats, LocaleController.formatPluralString("Chats", this.selectedIds.size(), new Object[0])));
        }
    }

    private void loadInactiveChannels() {
        this.adapter.notifyDataSetChanged();
        this.enterProgress = 0.0f;
        AndroidUtilities.runOnUIThread(this.showProgressRunnable, 500L);
        TLRPC.TL_channels_getInactiveChannels inactiveChannelsRequest = new TLRPC.TL_channels_getInactiveChannels();
        getConnectionsManager().sendRequest(inactiveChannelsRequest, new RequestDelegate() { // from class: org.telegram.ui.TooManyCommunitiesActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                TooManyCommunitiesActivity.this.m4673xa31a2b6e(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadInactiveChannels$5$org-telegram-ui-TooManyCommunitiesActivity */
    public /* synthetic */ void m4673xa31a2b6e(TLObject response, TLRPC.TL_error error) {
        String dateFormat;
        if (error == null) {
            final TLRPC.TL_messages_inactiveChats chats = (TLRPC.TL_messages_inactiveChats) response;
            final ArrayList<String> signatures = new ArrayList<>();
            for (int i = 0; i < chats.chats.size(); i++) {
                TLRPC.Chat chat = chats.chats.get(i);
                int currentDate = getConnectionsManager().getCurrentTime();
                int date = chats.dates.get(i).intValue();
                int daysDif = (currentDate - date) / 86400;
                if (daysDif < 30) {
                    dateFormat = LocaleController.formatPluralString("Days", daysDif, new Object[0]);
                } else if (daysDif < 365) {
                    dateFormat = LocaleController.formatPluralString("Months", daysDif / 30, new Object[0]);
                } else {
                    dateFormat = LocaleController.formatPluralString("Years", daysDif / 365, new Object[0]);
                }
                if (ChatObject.isMegagroup(chat)) {
                    String members = LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]);
                    signatures.add(LocaleController.formatString("InactiveChatSignature", R.string.InactiveChatSignature, members, dateFormat));
                } else if (ChatObject.isChannel(chat)) {
                    signatures.add(LocaleController.formatString("InactiveChannelSignature", R.string.InactiveChannelSignature, dateFormat));
                } else {
                    String members2 = LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]);
                    signatures.add(LocaleController.formatString("InactiveChatSignature", R.string.InactiveChatSignature, members2, dateFormat));
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TooManyCommunitiesActivity$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    TooManyCommunitiesActivity.this.m4672xe02dc20f(signatures, chats);
                }
            });
        }
    }

    /* renamed from: lambda$loadInactiveChannels$4$org-telegram-ui-TooManyCommunitiesActivity */
    public /* synthetic */ void m4672xe02dc20f(ArrayList signatures, TLRPC.TL_messages_inactiveChats chats) {
        this.inactiveChatsSignatures.clear();
        this.inactiveChats.clear();
        this.inactiveChatsSignatures.addAll(signatures);
        this.inactiveChats.addAll(chats.chats);
        this.adapter.notifyDataSetChanged();
        if (this.listView.getMeasuredHeight() > 0) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.enterAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.TooManyCommunitiesActivity$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    TooManyCommunitiesActivity.this.m4671x1d4158b0(valueAnimator);
                }
            });
            this.enterAnimator.setDuration(100L);
            this.enterAnimator.start();
        } else {
            this.enterProgress = 1.0f;
        }
        AndroidUtilities.cancelRunOnUIThread(this.showProgressRunnable);
        if (this.progressBar.getVisibility() == 0) {
            this.progressBar.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.TooManyCommunitiesActivity.8
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    TooManyCommunitiesActivity.this.progressBar.setVisibility(8);
                }
            }).start();
        }
    }

    /* renamed from: lambda$loadInactiveChannels$3$org-telegram-ui-TooManyCommunitiesActivity */
    public /* synthetic */ void m4671x1d4158b0(ValueAnimator animation) {
        this.enterProgress = ((Float) animation.getAnimatedValue()).floatValue();
        int n = this.listView.getChildCount();
        for (int i = 0; i < n; i++) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView.getChildAdapterPosition(recyclerListView.getChildAt(i)) >= this.adapter.headerPosition && this.adapter.headerPosition > 0) {
                this.listView.getChildAt(i).setAlpha(this.enterProgress);
            } else {
                this.listView.getChildAt(i).setAlpha(1.0f);
            }
        }
    }

    /* loaded from: classes4.dex */
    public class Adapter extends RecyclerListView.SelectionAdapter {
        int endPaddingPosition;
        int headerPosition;
        int hintPosition;
        int inactiveChatsEndRow;
        int inactiveChatsStartRow;
        int rowCount;
        int shadowPosition;

        Adapter() {
            TooManyCommunitiesActivity.this = this$0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        public void updateRows() {
            this.hintPosition = -1;
            this.shadowPosition = -1;
            this.headerPosition = -1;
            this.inactiveChatsStartRow = -1;
            this.inactiveChatsEndRow = -1;
            this.endPaddingPosition = -1;
            this.rowCount = 0;
            int i = 0 + 1;
            this.rowCount = i;
            this.hintPosition = 0;
            this.rowCount = i + 1;
            this.shadowPosition = i;
            if (!TooManyCommunitiesActivity.this.inactiveChats.isEmpty()) {
                int i2 = this.rowCount;
                int i3 = i2 + 1;
                this.rowCount = i3;
                this.headerPosition = i2;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.inactiveChatsStartRow = i3;
                int size = i4 + (TooManyCommunitiesActivity.this.inactiveChats.size() - 1);
                this.rowCount = size;
                this.inactiveChatsEndRow = size;
                this.rowCount = size + 1;
                this.endPaddingPosition = size;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            GroupCreateUserCell view;
            String message;
            switch (viewType) {
                case 1:
                    TooManyCommunitiesActivity.this.hintCell = new TooManyCommunitiesHintCell(parent.getContext());
                    TooManyCommunitiesHintCell tooManyCommunitiesHintCell = TooManyCommunitiesActivity.this.hintCell;
                    if (TooManyCommunitiesActivity.this.type != 0) {
                        if (TooManyCommunitiesActivity.this.type == 1) {
                            message = LocaleController.getString("TooManyCommunitiesHintEdit", R.string.TooManyCommunitiesHintEdit);
                        } else {
                            message = LocaleController.getString("TooManyCommunitiesHintCreate", R.string.TooManyCommunitiesHintCreate);
                        }
                    } else {
                        message = LocaleController.getString("TooManyCommunitiesHintJoin", R.string.TooManyCommunitiesHintJoin);
                    }
                    TooManyCommunitiesActivity.this.hintCell.setMessageText(message);
                    RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(-1, -2);
                    lp.bottomMargin = AndroidUtilities.dp(16.0f);
                    lp.topMargin = AndroidUtilities.dp(23.0f);
                    TooManyCommunitiesActivity.this.hintCell.setLayoutParams(lp);
                    view = tooManyCommunitiesHintCell;
                    break;
                case 2:
                    View view2 = new ShadowSectionCell(parent.getContext());
                    Drawable drawable = Theme.getThemedDrawable(parent.getContext(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable);
                    combinedDrawable.setFullsize(true);
                    view2.setBackground(combinedDrawable);
                    view = view2;
                    break;
                case 3:
                    HeaderCell header = new HeaderCell(parent.getContext(), Theme.key_windowBackgroundWhiteBlueHeader, 21, 8, false);
                    header.setHeight(54);
                    header.setText(LocaleController.getString("InactiveChats", R.string.InactiveChats));
                    view = header;
                    break;
                case 4:
                default:
                    view = new GroupCreateUserCell(parent.getContext(), 1, 0, false);
                    break;
                case 5:
                    view = new EmptyCell(parent.getContext(), AndroidUtilities.dp(12.0f));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int i = this.headerPosition;
            if (position >= i && i > 0) {
                holder.itemView.setAlpha(TooManyCommunitiesActivity.this.enterProgress);
            } else {
                holder.itemView.setAlpha(1.0f);
            }
            if (getItemViewType(position) == 4) {
                GroupCreateUserCell cell = (GroupCreateUserCell) holder.itemView;
                TLRPC.Chat chat = (TLRPC.Chat) TooManyCommunitiesActivity.this.inactiveChats.get(position - this.inactiveChatsStartRow);
                String signature = (String) TooManyCommunitiesActivity.this.inactiveChatsSignatures.get(position - this.inactiveChatsStartRow);
                String str = chat.title;
                boolean z = true;
                if (position == this.inactiveChatsEndRow - 1) {
                    z = false;
                }
                cell.setObject(chat, str, signature, z);
                cell.setChecked(TooManyCommunitiesActivity.this.selectedIds.contains(Long.valueOf(chat.id)), false);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == this.hintPosition) {
                return 1;
            }
            if (position == this.shadowPosition) {
                return 2;
            }
            if (position == this.headerPosition) {
                return 3;
            }
            if (position == this.endPaddingPosition) {
                return 5;
            }
            return 4;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.rowCount;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() >= this.inactiveChatsStartRow && holder.getAdapterPosition() < this.inactiveChatsEndRow) {
                return true;
            }
            return false;
        }
    }

    /* loaded from: classes4.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private int lastSearchId;
        ArrayList<TLRPC.Chat> searchResults = new ArrayList<>();
        ArrayList<String> searchResultsSignatures = new ArrayList<>();
        private Runnable searchRunnable;

        SearchAdapter() {
            TooManyCommunitiesActivity.this = this$0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new GroupCreateUserCell(parent.getContext(), 1, 0, false));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.Chat chat = this.searchResults.get(position);
            String signature = this.searchResultsSignatures.get(position);
            GroupCreateUserCell cell = (GroupCreateUserCell) holder.itemView;
            String str = chat.title;
            boolean z = true;
            if (position == this.searchResults.size() - 1) {
                z = false;
            }
            cell.setObject(chat, str, signature, z);
            cell.setChecked(TooManyCommunitiesActivity.this.selectedIds.contains(Long.valueOf(chat.id)), false);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.searchResults.size();
        }

        public void search(final String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(query)) {
                this.searchResults.clear();
                this.searchResultsSignatures.clear();
                notifyDataSetChanged();
                TooManyCommunitiesActivity.this.emptyView.setVisibility(8);
                return;
            }
            final int searchId = this.lastSearchId + 1;
            this.lastSearchId = searchId;
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.TooManyCommunitiesActivity$SearchAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    TooManyCommunitiesActivity.SearchAdapter.this.m4677x187a23af(query, searchId);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }

        /* renamed from: processSearch */
        public void m4677x187a23af(final String query, final int id) {
            Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.TooManyCommunitiesActivity$SearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    TooManyCommunitiesActivity.SearchAdapter.this.m4676x9a5e27f9(query, id);
                }
            });
        }

        /* renamed from: lambda$processSearch$1$org-telegram-ui-TooManyCommunitiesActivity$SearchAdapter */
        public /* synthetic */ void m4676x9a5e27f9(String query, int id) {
            String search1;
            String search12 = query.trim().toLowerCase();
            if (search12.length() == 0) {
                updateSearchResults(null, null, id);
                return;
            }
            String search2 = LocaleController.getInstance().getTranslitString(search12);
            if (search12.equals(search2) || search2.length() == 0) {
                search2 = null;
            }
            String[] search = new String[(search2 != null ? 1 : 0) + 1];
            search[0] = search12;
            if (search2 != null) {
                search[1] = search2;
            }
            ArrayList<TLRPC.Chat> resultArray = new ArrayList<>();
            ArrayList<String> resultArraySignatures = new ArrayList<>();
            int a = 0;
            while (a < TooManyCommunitiesActivity.this.inactiveChats.size()) {
                TLRPC.Chat chat = (TLRPC.Chat) TooManyCommunitiesActivity.this.inactiveChats.get(a);
                boolean found = false;
                int i = 0;
                while (true) {
                    if (i >= 2) {
                        search1 = search12;
                        break;
                    }
                    String name = i == 0 ? chat.title : chat.username;
                    if (name == null) {
                        search1 = search12;
                    } else {
                        String name2 = name.toLowerCase();
                        int length = search.length;
                        int i2 = 0;
                        while (i2 < length) {
                            String q = search[i2];
                            if (!name2.startsWith(q)) {
                                StringBuilder sb = new StringBuilder();
                                search1 = search12;
                                sb.append(" ");
                                sb.append(q);
                                if (!name2.contains(sb.toString())) {
                                    i2++;
                                    search12 = search1;
                                }
                            } else {
                                search1 = search12;
                            }
                            found = true;
                            break;
                        }
                        search1 = search12;
                        if (found) {
                            resultArray.add(chat);
                            resultArraySignatures.add((String) TooManyCommunitiesActivity.this.inactiveChatsSignatures.get(a));
                            break;
                        }
                    }
                    i++;
                    search12 = search1;
                }
                a++;
                search12 = search1;
            }
            updateSearchResults(resultArray, resultArraySignatures, id);
        }

        private void updateSearchResults(final ArrayList<TLRPC.Chat> chats, final ArrayList<String> signatures, final int searchId) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.TooManyCommunitiesActivity$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    TooManyCommunitiesActivity.SearchAdapter.this.m4678x4fa86aaa(searchId, chats, signatures);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$2$org-telegram-ui-TooManyCommunitiesActivity$SearchAdapter */
        public /* synthetic */ void m4678x4fa86aaa(int searchId, ArrayList chats, ArrayList signatures) {
            if (searchId != this.lastSearchId) {
                return;
            }
            this.searchResults.clear();
            this.searchResultsSignatures.clear();
            if (chats != null) {
                this.searchResults.addAll(chats);
                this.searchResultsSignatures.addAll(signatures);
            }
            notifyDataSetChanged();
            if (this.searchResults.isEmpty()) {
                TooManyCommunitiesActivity.this.emptyView.setVisibility(0);
            } else {
                TooManyCommunitiesActivity.this.emptyView.setVisibility(8);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.TooManyCommunitiesActivity$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                TooManyCommunitiesActivity.this.m4670x2ada7624();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_nameMessage_threeLines));
        themeDescriptions.add(new ThemeDescription(this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"headerTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_nameMessage_threeLines));
        themeDescriptions.add(new ThemeDescription(this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_message));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.buttonLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_sectionText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxDisabled));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{GroupCreateUserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_sectionText));
        themeDescriptions.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
        themeDescriptions.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxDisabled));
        themeDescriptions.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        themeDescriptions.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.searchListView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{GroupCreateUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(this.searchListView, 0, new Class[]{GroupCreateUserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.buttonTextView, 0, null, null, null, cellDelegate, Theme.key_featuredStickers_addButton));
        themeDescriptions.add(new ThemeDescription(this.buttonTextView, 0, null, null, null, cellDelegate, Theme.key_featuredStickers_addButtonPressed));
        themeDescriptions.add(new ThemeDescription(this.progressBar, 0, null, null, null, cellDelegate, Theme.key_featuredStickers_addButtonPressed));
        themeDescriptions.add(new ThemeDescription(this.hintCell, 0, new Class[]{TooManyCommunitiesHintCell.class}, new String[]{"imageLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogRedIcon));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$6$org-telegram-ui-TooManyCommunitiesActivity */
    public /* synthetic */ void m4670x2ada7624() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) child).update(0);
                }
            }
        }
        RecyclerListView recyclerListView2 = this.searchListView;
        if (recyclerListView2 != null) {
            int count2 = recyclerListView2.getChildCount();
            for (int a2 = 0; a2 < count2; a2++) {
                View child2 = this.searchListView.getChildAt(a2);
                if (child2 instanceof GroupCreateUserCell) {
                    ((GroupCreateUserCell) child2).update(0);
                }
            }
        }
        this.buttonTextView.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
        this.progressBar.setProgressColor(Theme.getColor(Theme.key_progressCircle));
    }
}
