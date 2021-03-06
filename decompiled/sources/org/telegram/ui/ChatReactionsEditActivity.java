package org.telegram.ui;

import android.annotation.SuppressLint;
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
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
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
/* loaded from: classes3.dex */
public class ChatReactionsEditActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private long chatId;
    private LinearLayout contentView;
    private TLRPC$Chat currentChat;
    private TextCheckCell enableReactionsCell;
    private TLRPC$ChatFull info;
    private RecyclerView.Adapter listAdapter;
    private RecyclerListView listView;
    private List<String> chatReactions = new ArrayList();
    private ArrayList<TLRPC$TL_availableReaction> availableReactions = new ArrayList<>();

    public ChatReactionsEditActivity(Bundle bundle) {
        super(bundle);
        this.chatId = bundle.getLong("chat_id", 0L);
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x004c, code lost:
        if (r0 == null) goto L10;
     */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onFragmentCreate() {
        /*
            r11 = this;
            org.telegram.messenger.MessagesController r0 = r11.getMessagesController()
            long r1 = r11.chatId
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r1)
            r11.currentChat = r0
            if (r0 != 0) goto L4f
            int r0 = r11.currentAccount
            org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            long r1 = r11.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChatSync(r1)
            r11.currentChat = r0
            r1 = 0
            if (r0 == 0) goto L4e
            org.telegram.messenger.MessagesController r0 = r11.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r2 = r11.currentChat
            r3 = 1
            r0.putChat(r2, r3)
            org.telegram.tgnet.TLRPC$ChatFull r0 = r11.info
            if (r0 != 0) goto L4f
            int r0 = r11.currentAccount
            org.telegram.messenger.MessagesStorage r4 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            long r5 = r11.chatId
            org.telegram.tgnet.TLRPC$Chat r0 = r11.currentChat
            boolean r7 = org.telegram.messenger.ChatObject.isChannel(r0)
            java.util.concurrent.CountDownLatch r8 = new java.util.concurrent.CountDownLatch
            r8.<init>(r3)
            r9 = 0
            r10 = 0
            org.telegram.tgnet.TLRPC$ChatFull r0 = r4.loadChatInfo(r5, r7, r8, r9, r10)
            r11.info = r0
            if (r0 != 0) goto L4f
        L4e:
            return r1
        L4f:
            org.telegram.messenger.NotificationCenter r0 = r11.getNotificationCenter()
            int r1 = org.telegram.messenger.NotificationCenter.reactionsDidLoad
            r0.addObserver(r11, r1)
            boolean r0 = super.onFragmentCreate()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatReactionsEditActivity.onFragmentCreate():boolean");
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setTitle(LocaleController.getString("Reactions", R.string.Reactions));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChatReactionsEditActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    ChatReactionsEditActivity.this.finishFragment();
                }
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        this.availableReactions.addAll(getMediaDataController().getEnabledReactionsList());
        TextCheckCell textCheckCell = new TextCheckCell(context);
        this.enableReactionsCell = textCheckCell;
        textCheckCell.setHeight(56);
        this.enableReactionsCell.setTextAndCheck(LocaleController.getString("EnableReactions", R.string.EnableReactions), true ^ this.chatReactions.isEmpty(), false);
        TextCheckCell textCheckCell2 = this.enableReactionsCell;
        textCheckCell2.setBackgroundColor(Theme.getColor(textCheckCell2.isChecked() ? "windowBackgroundChecked" : "windowBackgroundUnchecked"));
        this.enableReactionsCell.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.enableReactionsCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatReactionsEditActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatReactionsEditActivity.this.lambda$createView$0(view);
            }
        });
        linearLayout.addView(this.enableReactionsCell, LayoutHelper.createLinear(-1, -2));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView2 = this.listView;
        RecyclerView.Adapter adapter = new RecyclerView.Adapter() { // from class: org.telegram.ui.ChatReactionsEditActivity.2
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int i) {
                if (i == 0) {
                    return 0;
                }
                return i == 1 ? 1 : 2;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                if (i != 0) {
                    if (i != 1) {
                        return new RecyclerListView.Holder(new AvailableReactionCell(context, false, false));
                    }
                    return new RecyclerListView.Holder(new HeaderCell(context, 23));
                }
                return new RecyclerListView.Holder(new TextInfoPrivacyCell(context));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                int itemViewType = getItemViewType(i);
                if (itemViewType == 0) {
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    textInfoPrivacyCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
                    textInfoPrivacyCell.setText(ChatObject.isChannelAndNotMegaGroup(ChatReactionsEditActivity.this.currentChat) ? LocaleController.getString("EnableReactionsChannelInfo", R.string.EnableReactionsChannelInfo) : LocaleController.getString("EnableReactionsGroupInfo", R.string.EnableReactionsGroupInfo));
                } else if (itemViewType == 1) {
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    headerCell.setText(LocaleController.getString("AvailableReactions", R.string.AvailableReactions));
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                } else if (itemViewType != 2) {
                } else {
                    TLRPC$TL_availableReaction tLRPC$TL_availableReaction = (TLRPC$TL_availableReaction) ChatReactionsEditActivity.this.availableReactions.get(i - 2);
                    ((AvailableReactionCell) viewHolder.itemView).bind(tLRPC$TL_availableReaction, ChatReactionsEditActivity.this.chatReactions.contains(tLRPC$TL_availableReaction.reaction), ((BaseFragment) ChatReactionsEditActivity.this).currentAccount);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return (!ChatReactionsEditActivity.this.chatReactions.isEmpty() ? ChatReactionsEditActivity.this.availableReactions.size() + 1 : 0) + 1;
            }
        };
        this.listAdapter = adapter;
        recyclerListView2.setAdapter(adapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ChatReactionsEditActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ChatReactionsEditActivity.this.lambda$createView$1(view, i);
            }
        });
        linearLayout.addView(this.listView, LayoutHelper.createLinear(-1, 0, 1.0f));
        this.contentView = linearLayout;
        this.fragmentView = linearLayout;
        updateColors();
        return this.contentView;
    }

    public /* synthetic */ void lambda$createView$0(View view) {
        setCheckedEnableReactionCell(!this.enableReactionsCell.isChecked());
    }

    public /* synthetic */ void lambda$createView$1(View view, int i) {
        if (i <= 1) {
            return;
        }
        AvailableReactionCell availableReactionCell = (AvailableReactionCell) view;
        TLRPC$TL_availableReaction tLRPC$TL_availableReaction = this.availableReactions.get(i - 2);
        boolean z = !this.chatReactions.contains(tLRPC$TL_availableReaction.reaction);
        if (z) {
            this.chatReactions.add(tLRPC$TL_availableReaction.reaction);
        } else {
            this.chatReactions.remove(tLRPC$TL_availableReaction.reaction);
            if (this.chatReactions.isEmpty()) {
                setCheckedEnableReactionCell(false);
            }
        }
        availableReactionCell.setChecked(z, true);
    }

    private void setCheckedEnableReactionCell(boolean z) {
        if (this.enableReactionsCell.isChecked() == z) {
            return;
        }
        this.enableReactionsCell.setChecked(z);
        int color = Theme.getColor(z ? "windowBackgroundChecked" : "windowBackgroundUnchecked");
        if (z) {
            this.enableReactionsCell.setBackgroundColorAnimated(z, color);
        } else {
            this.enableReactionsCell.setBackgroundColorAnimatedReverse(color);
        }
        if (z) {
            Iterator<TLRPC$TL_availableReaction> it = this.availableReactions.iterator();
            while (it.hasNext()) {
                this.chatReactions.add(it.next().reaction);
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
        TLRPC$ChatFull tLRPC$ChatFull = this.info;
        boolean z = true;
        if (tLRPC$ChatFull != null) {
            z = true ^ tLRPC$ChatFull.available_reactions.equals(this.chatReactions);
        }
        if (z) {
            getMessagesController().setChatReactions(this.chatId, this.chatReactions);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.reactionsDidLoad);
    }

    public void setInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        this.info = tLRPC$ChatFull;
        if (tLRPC$ChatFull != null) {
            if (this.currentChat == null) {
                this.currentChat = getMessagesController().getChat(Long.valueOf(this.chatId));
            }
            this.chatReactions = new ArrayList(tLRPC$ChatFull.available_reactions);
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
        }, "windowBackgroundWhite", "windowBackgroundWhiteBlackText", "windowBackgroundWhiteGrayText2", "listSelectorSDK21", "windowBackgroundGray", "windowBackgroundWhiteGrayText4", "windowBackgroundWhiteRedText4", "windowBackgroundChecked", "windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
    }

    @SuppressLint({"NotifyDataSetChanged"})
    public void updateColors() {
        this.contentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.enableReactionsCell.setColors("windowBackgroundCheckText", "switchTrackBlue", "switchTrackBlueChecked", "switchTrackBlueThumb", "switchTrackBlueThumbChecked");
        this.listAdapter.notifyDataSetChanged();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    @SuppressLint({"NotifyDataSetChanged"})
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i2 == this.currentAccount && i == NotificationCenter.reactionsDidLoad) {
            this.availableReactions.clear();
            this.availableReactions.addAll(getMediaDataController().getEnabledReactionsList());
            this.listAdapter.notifyDataSetChanged();
        }
    }
}
