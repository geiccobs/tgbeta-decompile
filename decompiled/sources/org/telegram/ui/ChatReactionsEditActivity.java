package org.telegram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AvailableReactionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SimpleThemeDescription;
/* loaded from: classes4.dex */
public class ChatReactionsEditActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final String KEY_CHAT_ID = "chat_id";
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_INFO = 0;
    private static final int TYPE_REACTION = 2;
    private long chatId;
    private LinearLayout contentView;
    private TLRPC.Chat currentChat;
    private TextCheckCell enableReactionsCell;
    private TLRPC.ChatFull info;
    private RecyclerView.Adapter listAdapter;
    private RecyclerListView listView;
    private List<String> chatReactions = new ArrayList();
    private ArrayList<TLRPC.TL_availableReaction> availableReactions = new ArrayList<>();

    public ChatReactionsEditActivity(Bundle args) {
        super(args);
        this.chatId = args.getLong(KEY_CHAT_ID, 0L);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        if (chat == null) {
            TLRPC.Chat chatSync = MessagesStorage.getInstance(this.currentAccount).getChatSync(this.chatId);
            this.currentChat = chatSync;
            if (chatSync == null) {
                return false;
            }
            getMessagesController().putChat(this.currentChat, true);
            if (this.info == null) {
                TLRPC.ChatFull loadChatInfo = MessagesStorage.getInstance(this.currentAccount).loadChatInfo(this.chatId, ChatObject.isChannel(this.currentChat), new CountDownLatch(1), false, false);
                this.info = loadChatInfo;
                if (loadChatInfo == null) {
                    return false;
                }
            }
        }
        getNotificationCenter().addObserver(this, NotificationCenter.reactionsDidLoad);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setTitle(LocaleController.getString("Reactions", R.string.Reactions));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChatReactionsEditActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    ChatReactionsEditActivity.this.finishFragment();
                }
            }
        });
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(1);
        this.availableReactions.addAll(getMediaDataController().getEnabledReactionsList());
        TextCheckCell textCheckCell = new TextCheckCell(context);
        this.enableReactionsCell = textCheckCell;
        textCheckCell.setHeight(56);
        this.enableReactionsCell.setTextAndCheck(LocaleController.getString("EnableReactions", R.string.EnableReactions), true ^ this.chatReactions.isEmpty(), false);
        TextCheckCell textCheckCell2 = this.enableReactionsCell;
        textCheckCell2.setBackgroundColor(Theme.getColor(textCheckCell2.isChecked() ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked));
        this.enableReactionsCell.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.enableReactionsCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatReactionsEditActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatReactionsEditActivity.this.m2121lambda$createView$0$orgtelegramuiChatReactionsEditActivity(view);
            }
        });
        ll.addView(this.enableReactionsCell, LayoutHelper.createLinear(-1, -2));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView2 = this.listView;
        RecyclerView.Adapter adapter = new RecyclerView.Adapter() { // from class: org.telegram.ui.ChatReactionsEditActivity.2
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case 0:
                        TextInfoPrivacyCell infoCell = new TextInfoPrivacyCell(context);
                        return new RecyclerListView.Holder(infoCell);
                    case 1:
                        return new RecyclerListView.Holder(new HeaderCell(context, 23));
                    default:
                        return new RecyclerListView.Holder(new AvailableReactionCell(context, false));
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                switch (getItemViewType(position)) {
                    case 0:
                        TextInfoPrivacyCell infoCell = (TextInfoPrivacyCell) holder.itemView;
                        infoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
                        infoCell.setText(ChatObject.isChannelAndNotMegaGroup(ChatReactionsEditActivity.this.currentChat) ? LocaleController.getString("EnableReactionsChannelInfo", R.string.EnableReactionsChannelInfo) : LocaleController.getString("EnableReactionsGroupInfo", R.string.EnableReactionsGroupInfo));
                        return;
                    case 1:
                        HeaderCell headerCell = (HeaderCell) holder.itemView;
                        headerCell.setText(LocaleController.getString("AvailableReactions", R.string.AvailableReactions));
                        headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        return;
                    case 2:
                        AvailableReactionCell reactionCell = (AvailableReactionCell) holder.itemView;
                        TLRPC.TL_availableReaction react = (TLRPC.TL_availableReaction) ChatReactionsEditActivity.this.availableReactions.get(position - 2);
                        reactionCell.bind(react, ChatReactionsEditActivity.this.chatReactions.contains(react.reaction));
                        return;
                    default:
                        return;
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return (!ChatReactionsEditActivity.this.chatReactions.isEmpty() ? ChatReactionsEditActivity.this.availableReactions.size() + 1 : 0) + 1;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int position) {
                if (position == 0) {
                    return 0;
                }
                return position == 1 ? 1 : 2;
            }
        };
        this.listAdapter = adapter;
        recyclerListView2.setAdapter(adapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ChatReactionsEditActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ChatReactionsEditActivity.this.m2122lambda$createView$1$orgtelegramuiChatReactionsEditActivity(view, i);
            }
        });
        ll.addView(this.listView, LayoutHelper.createLinear(-1, 0, 1.0f));
        this.contentView = ll;
        this.fragmentView = ll;
        updateColors();
        return this.contentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ChatReactionsEditActivity */
    public /* synthetic */ void m2121lambda$createView$0$orgtelegramuiChatReactionsEditActivity(View v) {
        setCheckedEnableReactionCell(!this.enableReactionsCell.isChecked());
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ChatReactionsEditActivity */
    public /* synthetic */ void m2122lambda$createView$1$orgtelegramuiChatReactionsEditActivity(View view, int position) {
        if (position <= 1) {
            return;
        }
        AvailableReactionCell cell = (AvailableReactionCell) view;
        TLRPC.TL_availableReaction react = this.availableReactions.get(position - 2);
        boolean nc = !this.chatReactions.contains(react.reaction);
        if (nc) {
            this.chatReactions.add(react.reaction);
        } else {
            this.chatReactions.remove(react.reaction);
            if (this.chatReactions.isEmpty()) {
                setCheckedEnableReactionCell(false);
            }
        }
        cell.setChecked(nc, true);
    }

    private void setCheckedEnableReactionCell(boolean c) {
        if (this.enableReactionsCell.isChecked() == c) {
            return;
        }
        this.enableReactionsCell.setChecked(c);
        int clr = Theme.getColor(c ? Theme.key_windowBackgroundChecked : Theme.key_windowBackgroundUnchecked);
        if (c) {
            this.enableReactionsCell.setBackgroundColorAnimated(c, clr);
        } else {
            this.enableReactionsCell.setBackgroundColorAnimatedReverse(clr);
        }
        if (c) {
            Iterator<TLRPC.TL_availableReaction> it = this.availableReactions.iterator();
            while (it.hasNext()) {
                TLRPC.TL_availableReaction a = it.next();
                this.chatReactions.add(a.reaction);
            }
            this.listAdapter.notifyItemRangeInserted(1, this.availableReactions.size() + 1);
            return;
        }
        this.chatReactions.clear();
        this.listAdapter.notifyItemRangeRemoved(1, this.availableReactions.size() + 1);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        boolean changed = true;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null) {
            changed = !chatFull.available_reactions.equals(this.chatReactions);
        }
        if (changed) {
            getMessagesController().setChatReactions(this.chatId, this.chatReactions);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.reactionsDidLoad);
    }

    public void setInfo(TLRPC.ChatFull info) {
        this.info = info;
        if (info != null) {
            if (this.currentChat == null) {
                this.currentChat = getMessagesController().getChat(Long.valueOf(this.chatId));
            }
            this.chatReactions = new ArrayList(info.available_reactions);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ChatReactionsEditActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ChatReactionsEditActivity.this.updateColors();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhiteBlackText, Theme.key_windowBackgroundWhiteGrayText2, Theme.key_listSelector, Theme.key_windowBackgroundGray, Theme.key_windowBackgroundWhiteGrayText4, Theme.key_windowBackgroundWhiteRedText4, Theme.key_windowBackgroundChecked, Theme.key_windowBackgroundCheckText, Theme.key_switchTrackBlue, Theme.key_switchTrackBlueChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
    }

    public void updateColors() {
        this.contentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.enableReactionsCell.setColors(Theme.key_windowBackgroundCheckText, Theme.key_switchTrackBlue, Theme.key_switchTrackBlueChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
        this.listAdapter.notifyDataSetChanged();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (account == this.currentAccount && id == NotificationCenter.reactionsDidLoad) {
            this.availableReactions.clear();
            this.availableReactions.addAll(getMediaDataController().getEnabledReactionsList());
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
