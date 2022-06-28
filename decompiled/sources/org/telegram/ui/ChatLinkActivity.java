package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.gms.location.LocationRequest;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.ChatLinkActivity;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.JoinToSendSettingsView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LoadingStickerDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.GroupCreateFinalActivity;
/* loaded from: classes4.dex */
public class ChatLinkActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int search_button = 0;
    private int chatEndRow;
    private int chatStartRow;
    private boolean chatsLoaded;
    private int createChatRow;
    private TLRPC.Chat currentChat;
    private long currentChatId;
    private int detailRow;
    private EmptyTextProgressView emptyView;
    private int helpRow;
    private TLRPC.ChatFull info;
    private boolean isChannel;
    private int joinToSendRow;
    private JoinToSendSettingsView joinToSendSettings;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loadingChats;
    private int removeChatRow;
    private int rowCount;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private boolean searchWas;
    private boolean searching;
    private boolean waitingForChatCreate;
    private TLRPC.Chat waitingForFullChat;
    private AlertDialog waitingForFullChatProgressAlert;
    private ArrayList<TLRPC.Chat> chats = new ArrayList<>();
    private boolean joinToSendProgress = false;
    private boolean joinRequestProgress = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class EmptyView extends LinearLayout implements NotificationCenter.NotificationCenterDelegate {
        private static final String stickerSetName = "tg_placeholders_android";
        private int currentAccount = UserConfig.selectedAccount;
        private LoadingStickerDrawable drawable;
        private BackupImageView stickerView;

        public EmptyView(Context context) {
            super(context);
            setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
            setOrientation(1);
            this.stickerView = new BackupImageView(context);
            LoadingStickerDrawable loadingStickerDrawable = new LoadingStickerDrawable(this.stickerView, "M476.1,397.4c25.8-47.2,0.3-105.9-50.9-120c-2.5-6.9-7.8-12.7-15-16.4l0.4-229.4c0-12.3-10-22.4-22.4-22.4H128.5c-12.3,0-22.4,10-22.4,22.4l-0.4,229.8v0c0,6.7,2.9,12.6,7.6,16.7c-51.6,15.9-79.2,77.2-48.1,116.4c-8.7,11.7-13.4,27.5-14,47.2c-1.7,34.5,21.6,45.8,55.9,45.8c52.3,0,99.1,4.6,105.1-36.2c16.5,0.9,7.1-37.3-6.5-53.3c18.4-22.4,18.3-52.9,4.9-78.2c-0.7-5.3-3.8-9.8-8.1-12.6c-1.5-2-1.6-2-2.1-2.7c0.2-1,1.2-11.8-3.4-20.9h138.5c-4.8,8.8-4.7,17-2.9,22.1c-5.3,4.8-6.8,12.3-5.2,17c-11.4,24.9-10,53.8,4.3,77.5c-6.8,9.7-11.2,21.7-12.6,31.6c-0.2-0.2-0.4-0.3-0.6-0.5c0.8-3.3,0.4-6.4-1.3-7.8c9.3-12.1-4.5-29.2-17-21.7c-3.8-2.8-10.6-3.2-18.1-0.5c-2.4-10.6-21.1-10.6-28.6-1c-1.3,0.3-2.9,0.8-4.5,1.9c-5.2-0.9-10.9,0.1-14.1,4.4c-6.9,3-9.5,10.4-7.8,17c-0.9,1.8-1.1,4-0.8,6.3c-1.6,1.2-2.3,3.1-2,4.9c0.1,0.6,10.4,56.6,11.2,62c0.3,1.8,1.5,3.2,3.1,3.9c8.7,3.4,12,3.8,30.1,9.4c2.7,0.8,2.4,0.8,6.7-0.1c16.4-3.5,30.2-8.9,30.8-9.2c1.6-0.6,2.7-2,3.1-3.7c0.1-0.4,6.8-36.5,10-53.2c0.9,4.2,3.3,7.3,7.4,7.5c1.2,7.8,4.4,14.5,9.5,19.9c16.4,17.3,44.9,15.7,64.9,16.1c38.3,0.8,74.5,1.5,84.4-24.4C488.9,453.5,491.3,421.3,476.1,397.4z", AndroidUtilities.dp(104.0f), AndroidUtilities.dp(104.0f));
            this.drawable = loadingStickerDrawable;
            this.stickerView.setImageDrawable(loadingStickerDrawable);
            addView(this.stickerView, LayoutHelper.createLinear((int) LocationRequest.PRIORITY_LOW_POWER, (int) LocationRequest.PRIORITY_LOW_POWER, 49, 0, 2, 0, 0));
        }

        private void setSticker() {
            TLRPC.messages_StickerSet set = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("tg_placeholders_android");
            if (set == null) {
                set = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("tg_placeholders_android");
            }
            if (set != null && set.documents.size() >= 3) {
                TLRPC.Document document = set.documents.get(2);
                ImageLocation imageLocation = ImageLocation.getForDocument(document);
                this.stickerView.setImage(imageLocation, "104_104", "tgs", this.drawable, set);
                return;
            }
            MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders_android", false, set == null);
            this.stickerView.setImageDrawable(this.drawable);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            setSticker();
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int id, int account, Object... args) {
            if (id == NotificationCenter.diceStickersDidLoad) {
                String name = (String) args[0];
                if ("tg_placeholders_android".equals(name)) {
                    setSticker();
                }
            }
        }
    }

    public ChatLinkActivity(long chatId) {
        boolean z = false;
        this.currentChatId = chatId;
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(chatId));
        this.currentChat = chat;
        if (ChatObject.isChannel(chat) && !this.currentChat.megagroup) {
            z = true;
        }
        this.isChannel = z;
    }

    private void updateRows() {
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.currentChatId));
        this.currentChat = chat;
        if (chat == null) {
            return;
        }
        int i = 0;
        this.rowCount = 0;
        this.helpRow = -1;
        this.createChatRow = -1;
        this.chatStartRow = -1;
        this.chatEndRow = -1;
        this.removeChatRow = -1;
        this.detailRow = -1;
        this.joinToSendRow = -1;
        int i2 = 0 + 1;
        this.rowCount = i2;
        this.helpRow = 0;
        if (this.isChannel) {
            if (this.info.linked_chat_id == 0) {
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.createChatRow = i3;
            }
            int i4 = this.rowCount;
            this.chatStartRow = i4;
            int size = i4 + this.chats.size();
            this.rowCount = size;
            this.chatEndRow = size;
            if (this.info.linked_chat_id != 0) {
                int i5 = this.rowCount;
                this.rowCount = i5 + 1;
                this.createChatRow = i5;
            }
        } else {
            this.chatStartRow = i2;
            int size2 = i2 + this.chats.size();
            this.rowCount = size2;
            this.chatEndRow = size2;
            this.rowCount = size2 + 1;
            this.createChatRow = size2;
        }
        int i6 = this.rowCount;
        this.rowCount = i6 + 1;
        this.detailRow = i6;
        if (!this.isChannel || (this.chats.size() > 0 && this.info.linked_chat_id != 0)) {
            TLRPC.Chat chat2 = this.isChannel ? this.chats.get(0) : this.currentChat;
            if (chat2 != null && ((TextUtils.isEmpty(chat2.username) || this.isChannel) && (chat2.creator || (chat2.admin_rights != null && chat2.admin_rights.ban_users)))) {
                int i7 = this.rowCount;
                this.rowCount = i7 + 1;
                this.joinToSendRow = i7;
            }
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            if (this.chats.size() <= 10) {
                i = 8;
            }
            actionBarMenuItem.setVisibility(i);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.updateInterfaces);
        loadChats();
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        JoinToSendSettingsView joinToSendSettingsView;
        TLRPC.Chat linkedChat;
        TLRPC.Chat chat = null;
        if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) args[0];
            if (chatFull.id == this.currentChatId) {
                this.info = chatFull;
                loadChats();
                updateRows();
                return;
            }
            TLRPC.Chat chat2 = this.waitingForFullChat;
            if (chat2 != null && chat2.id == chatFull.id) {
                try {
                    this.waitingForFullChatProgressAlert.dismiss();
                } catch (Throwable th) {
                }
                this.waitingForFullChatProgressAlert = null;
                showLinkAlert(this.waitingForFullChat, false);
                this.waitingForFullChat = null;
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int updateMask = ((Integer) args[0]).intValue();
            if ((MessagesController.UPDATE_MASK_CHAT & updateMask) != 0 && this.currentChat != null) {
                TLRPC.Chat newCurrentChat = getMessagesController().getChat(Long.valueOf(this.currentChat.id));
                if (newCurrentChat != null) {
                    this.currentChat = newCurrentChat;
                }
                if (this.chats.size() > 0 && (linkedChat = getMessagesController().getChat(Long.valueOf(this.chats.get(0).id))) != null) {
                    this.chats.set(0, linkedChat);
                }
                if (!this.isChannel) {
                    chat = this.currentChat;
                } else if (this.chats.size() > 0) {
                    chat = this.chats.get(0);
                }
                if (chat != null && (joinToSendSettingsView = this.joinToSendSettings) != null) {
                    if (!this.joinRequestProgress) {
                        joinToSendSettingsView.m2741lambda$new$3$orgtelegramuiComponentsJoinToSendSettingsView(chat.join_request);
                    }
                    if (!this.joinToSendProgress) {
                        this.joinToSendSettings.setJoinToSend(chat.join_to_send);
                    }
                }
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Discussion", R.string.Discussion));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChatLinkActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    ChatLinkActivity.this.finishFragment();
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.ChatLinkActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                ChatLinkActivity.this.searching = true;
                ChatLinkActivity.this.emptyView.setShowAtCenter(true);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                ChatLinkActivity.this.searchAdapter.searchDialogs(null);
                ChatLinkActivity.this.searching = false;
                ChatLinkActivity.this.searchWas = false;
                ChatLinkActivity.this.listView.setAdapter(ChatLinkActivity.this.listViewAdapter);
                ChatLinkActivity.this.listViewAdapter.notifyDataSetChanged();
                ChatLinkActivity.this.listView.setFastScrollVisible(true);
                ChatLinkActivity.this.listView.setVerticalScrollBarEnabled(false);
                ChatLinkActivity.this.emptyView.setShowAtCenter(false);
                ChatLinkActivity.this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                ChatLinkActivity.this.fragmentView.setTag(Theme.key_windowBackgroundGray);
                ChatLinkActivity.this.emptyView.showProgress();
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                if (ChatLinkActivity.this.searchAdapter == null) {
                    return;
                }
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    ChatLinkActivity.this.searchWas = true;
                    if (ChatLinkActivity.this.listView != null && ChatLinkActivity.this.listView.getAdapter() != ChatLinkActivity.this.searchAdapter) {
                        ChatLinkActivity.this.listView.setAdapter(ChatLinkActivity.this.searchAdapter);
                        ChatLinkActivity.this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        ChatLinkActivity.this.fragmentView.setTag(Theme.key_windowBackgroundWhite);
                        ChatLinkActivity.this.searchAdapter.notifyDataSetChanged();
                        ChatLinkActivity.this.listView.setFastScrollVisible(false);
                        ChatLinkActivity.this.listView.setVerticalScrollBarEnabled(true);
                        ChatLinkActivity.this.emptyView.showProgress();
                    }
                }
                ChatLinkActivity.this.searchAdapter.searchDialogs(text);
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setTag(Theme.key_windowBackgroundGray);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        RecyclerListView recyclerListView3 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView3.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                ChatLinkActivity.this.m2087lambda$createView$6$orgtelegramuiChatLinkActivity(view, i2);
            }
        });
        updateRows();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2087lambda$createView$6$orgtelegramuiChatLinkActivity(View view, int position) {
        TLRPC.Chat chat;
        String title;
        String message;
        if (getParentActivity() == null) {
            return;
        }
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchAdapter;
        if (adapter == searchAdapter) {
            chat = searchAdapter.getItem(position);
        } else {
            int i = this.chatStartRow;
            if (position >= i && position < this.chatEndRow) {
                chat = this.chats.get(position - i);
            } else {
                chat = null;
            }
        }
        if (chat != null) {
            if (this.isChannel && this.info.linked_chat_id == 0) {
                showLinkAlert(chat, true);
                return;
            }
            Bundle args = new Bundle();
            args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, chat.id);
            presentFragment(new ChatActivity(args));
        } else if (position == this.createChatRow) {
            if (this.isChannel && this.info.linked_chat_id == 0) {
                Bundle args2 = new Bundle();
                long[] array = {getUserConfig().getClientUserId()};
                args2.putLongArray("result", array);
                args2.putInt("chatType", 4);
                GroupCreateFinalActivity activity = new GroupCreateFinalActivity(args2);
                activity.setDelegate(new GroupCreateFinalActivity.GroupCreateFinalActivityDelegate() { // from class: org.telegram.ui.ChatLinkActivity.3
                    @Override // org.telegram.ui.GroupCreateFinalActivity.GroupCreateFinalActivityDelegate
                    public void didStartChatCreation() {
                    }

                    @Override // org.telegram.ui.GroupCreateFinalActivity.GroupCreateFinalActivityDelegate
                    public void didFinishChatCreation(GroupCreateFinalActivity fragment, long chatId) {
                        ChatLinkActivity chatLinkActivity = ChatLinkActivity.this;
                        chatLinkActivity.linkChat(chatLinkActivity.getMessagesController().getChat(Long.valueOf(chatId)), fragment);
                    }

                    @Override // org.telegram.ui.GroupCreateFinalActivity.GroupCreateFinalActivityDelegate
                    public void didFailChatCreation() {
                    }
                });
                presentFragment(activity);
            } else if (this.chats.isEmpty()) {
            } else {
                TLRPC.Chat c = this.chats.get(0);
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                if (this.isChannel) {
                    title = LocaleController.getString("DiscussionUnlinkGroup", R.string.DiscussionUnlinkGroup);
                    message = LocaleController.formatString("DiscussionUnlinkChannelAlert", R.string.DiscussionUnlinkChannelAlert, c.title);
                } else {
                    title = LocaleController.getString("DiscussionUnlink", R.string.DiscussionUnlinkChannel);
                    message = LocaleController.formatString("DiscussionUnlinkGroupAlert", R.string.DiscussionUnlinkGroupAlert, c.title);
                }
                builder.setTitle(title);
                builder.setMessage(AndroidUtilities.replaceTags(message));
                builder.setPositiveButton(LocaleController.getString("DiscussionUnlink", R.string.DiscussionUnlink), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda12
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i2) {
                        ChatLinkActivity.this.m2086lambda$createView$5$orgtelegramuiChatLinkActivity(dialogInterface, i2);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog dialog = builder.create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                }
            }
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2086lambda$createView$5$orgtelegramuiChatLinkActivity(DialogInterface dialogInterface, int i) {
        if (!this.isChannel || this.info.linked_chat_id != 0) {
            final AlertDialog[] progressDialog = {new AlertDialog(getParentActivity(), 3)};
            TLRPC.TL_channels_setDiscussionGroup req = new TLRPC.TL_channels_setDiscussionGroup();
            if (this.isChannel) {
                req.broadcast = MessagesController.getInputChannel(this.currentChat);
                req.group = new TLRPC.TL_inputChannelEmpty();
            } else {
                req.broadcast = new TLRPC.TL_inputChannelEmpty();
                req.group = MessagesController.getInputChannel(this.currentChat);
            }
            final int requestId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda6
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChatLinkActivity.this.m2083lambda$createView$2$orgtelegramuiChatLinkActivity(progressDialog, tLObject, tL_error);
                }
            });
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ChatLinkActivity.this.m2085lambda$createView$4$orgtelegramuiChatLinkActivity(progressDialog, requestId);
                }
            }, 500L);
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2083lambda$createView$2$orgtelegramuiChatLinkActivity(final AlertDialog[] progressDialog, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                ChatLinkActivity.this.m2082lambda$createView$1$orgtelegramuiChatLinkActivity(progressDialog);
            }
        });
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2082lambda$createView$1$orgtelegramuiChatLinkActivity(AlertDialog[] progressDialog) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        this.info.linked_chat_id = 0L;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, false, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                ChatLinkActivity.this.m2081lambda$createView$0$orgtelegramuiChatLinkActivity();
            }
        }, 1000L);
        if (!this.isChannel) {
            finishFragment();
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2081lambda$createView$0$orgtelegramuiChatLinkActivity() {
        getMessagesController().loadFullChat(this.currentChatId, 0, true);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2085lambda$createView$4$orgtelegramuiChatLinkActivity(AlertDialog[] progressDialog, final int requestId) {
        if (progressDialog[0] == null) {
            return;
        }
        progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda10
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                ChatLinkActivity.this.m2084lambda$createView$3$orgtelegramuiChatLinkActivity(requestId, dialogInterface);
            }
        });
        showDialog(progressDialog[0]);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2084lambda$createView$3$orgtelegramuiChatLinkActivity(int requestId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestId, true);
    }

    private void showLinkAlert(final TLRPC.Chat chat, boolean query) {
        String message;
        final TLRPC.ChatFull chatFull = getMessagesController().getChatFull(chat.id);
        int i = 3;
        if (chatFull == null) {
            if (query) {
                getMessagesController().loadFullChat(chat.id, 0, true);
                this.waitingForFullChat = chat;
                this.waitingForFullChatProgressAlert = new AlertDialog(getParentActivity(), 3);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda16
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatLinkActivity.this.m2098lambda$showLinkAlert$8$orgtelegramuiChatLinkActivity();
                    }
                }, 500L);
                return;
            }
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        TextView messageTextView = new TextView(getParentActivity());
        messageTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        messageTextView.setTextSize(1, 16.0f);
        messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        if (TextUtils.isEmpty(chat.username)) {
            message = LocaleController.formatString("DiscussionLinkGroupPublicPrivateAlert", R.string.DiscussionLinkGroupPublicPrivateAlert, chat.title, this.currentChat.title);
        } else if (TextUtils.isEmpty(this.currentChat.username)) {
            message = LocaleController.formatString("DiscussionLinkGroupPrivateAlert", R.string.DiscussionLinkGroupPrivateAlert, chat.title, this.currentChat.title);
        } else {
            message = LocaleController.formatString("DiscussionLinkGroupPublicAlert", R.string.DiscussionLinkGroupPublicAlert, chat.title, this.currentChat.title);
        }
        if (chatFull.hidden_prehistory) {
            message = message + "\n\n" + LocaleController.getString("DiscussionLinkGroupAlertHistory", R.string.DiscussionLinkGroupAlertHistory);
        }
        messageTextView.setText(AndroidUtilities.replaceTags(message));
        FrameLayout frameLayout2 = new FrameLayout(getParentActivity());
        builder.setView(frameLayout2);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
        BackupImageView imageView = new BackupImageView(getParentActivity());
        imageView.setRoundRadius(AndroidUtilities.dp(20.0f));
        frameLayout2.addView(imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
        TextView textView = new TextView(getParentActivity());
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(chat.title);
        int i2 = (LocaleController.isRTL ? 5 : 3) | 48;
        int i3 = 21;
        float f = LocaleController.isRTL ? 21 : 76;
        if (LocaleController.isRTL) {
            i3 = 76;
        }
        frameLayout2.addView(textView, LayoutHelper.createFrame(-1, -2.0f, i2, f, 11.0f, i3, 0.0f));
        if (LocaleController.isRTL) {
            i = 5;
        }
        frameLayout2.addView(messageTextView, LayoutHelper.createFrame(-2, -2.0f, i | 48, 24.0f, 57.0f, 24.0f, 9.0f));
        avatarDrawable.setInfo(chat);
        imageView.setForUserOrChat(chat, avatarDrawable);
        builder.setPositiveButton(LocaleController.getString("DiscussionLinkGroup", R.string.DiscussionLinkGroup), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda13
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i4) {
                ChatLinkActivity.this.m2099lambda$showLinkAlert$9$orgtelegramuiChatLinkActivity(chatFull, chat, dialogInterface, i4);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    /* renamed from: lambda$showLinkAlert$8$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2098lambda$showLinkAlert$8$orgtelegramuiChatLinkActivity() {
        AlertDialog alertDialog = this.waitingForFullChatProgressAlert;
        if (alertDialog == null) {
            return;
        }
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                ChatLinkActivity.this.m2097lambda$showLinkAlert$7$orgtelegramuiChatLinkActivity(dialogInterface);
            }
        });
        showDialog(this.waitingForFullChatProgressAlert);
    }

    /* renamed from: lambda$showLinkAlert$7$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2097lambda$showLinkAlert$7$orgtelegramuiChatLinkActivity(DialogInterface dialog) {
        this.waitingForFullChat = null;
    }

    /* renamed from: lambda$showLinkAlert$9$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2099lambda$showLinkAlert$9$orgtelegramuiChatLinkActivity(TLRPC.ChatFull chatFull, TLRPC.Chat chat, DialogInterface dialogInterface, int i) {
        if (chatFull.hidden_prehistory) {
            getMessagesController().toggleChannelInvitesHistory(chat.id, false);
        }
        linkChat(chat, null);
    }

    public void linkChat(final TLRPC.Chat chat, final BaseFragment createFragment) {
        if (chat == null) {
            return;
        }
        if (!ChatObject.isChannel(chat)) {
            getMessagesController().convertToMegaGroup(getParentActivity(), chat.id, this, new MessagesStorage.LongCallback() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda4
                @Override // org.telegram.messenger.MessagesStorage.LongCallback
                public final void run(long j) {
                    ChatLinkActivity.this.m2089lambda$linkChat$10$orgtelegramuiChatLinkActivity(createFragment, j);
                }
            });
            return;
        }
        final AlertDialog[] progressDialog = new AlertDialog[1];
        progressDialog[0] = createFragment != null ? null : new AlertDialog(getParentActivity(), 3);
        TLRPC.TL_channels_setDiscussionGroup req = new TLRPC.TL_channels_setDiscussionGroup();
        req.broadcast = MessagesController.getInputChannel(this.currentChat);
        req.group = MessagesController.getInputChannel(chat);
        final int requestId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatLinkActivity.this.m2092lambda$linkChat$13$orgtelegramuiChatLinkActivity(progressDialog, chat, createFragment, tLObject, tL_error);
            }
        }, 64);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ChatLinkActivity.this.m2094lambda$linkChat$15$orgtelegramuiChatLinkActivity(progressDialog, requestId);
            }
        }, 500L);
    }

    /* renamed from: lambda$linkChat$10$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2089lambda$linkChat$10$orgtelegramuiChatLinkActivity(BaseFragment createFragment, long param) {
        if (param != 0) {
            getMessagesController().toggleChannelInvitesHistory(param, false);
            linkChat(getMessagesController().getChat(Long.valueOf(param)), createFragment);
        }
    }

    /* renamed from: lambda$linkChat$13$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2092lambda$linkChat$13$orgtelegramuiChatLinkActivity(final AlertDialog[] progressDialog, final TLRPC.Chat chat, final BaseFragment createFragment, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ChatLinkActivity.this.m2091lambda$linkChat$12$orgtelegramuiChatLinkActivity(progressDialog, chat, createFragment);
            }
        });
    }

    /* renamed from: lambda$linkChat$12$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2091lambda$linkChat$12$orgtelegramuiChatLinkActivity(AlertDialog[] progressDialog, TLRPC.Chat chat, BaseFragment createFragment) {
        if (progressDialog[0] != null) {
            try {
                progressDialog[0].dismiss();
            } catch (Throwable th) {
            }
            progressDialog[0] = null;
        }
        this.info.linked_chat_id = chat.id;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, this.info, 0, false, false);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                ChatLinkActivity.this.m2090lambda$linkChat$11$orgtelegramuiChatLinkActivity();
            }
        }, 1000L);
        if (createFragment != null) {
            removeSelfFromStack();
            createFragment.finishFragment();
            return;
        }
        finishFragment();
    }

    /* renamed from: lambda$linkChat$11$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2090lambda$linkChat$11$orgtelegramuiChatLinkActivity() {
        getMessagesController().loadFullChat(this.currentChatId, 0, true);
    }

    /* renamed from: lambda$linkChat$15$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2094lambda$linkChat$15$orgtelegramuiChatLinkActivity(AlertDialog[] progressDialog, final int requestId) {
        if (progressDialog[0] == null) {
            return;
        }
        progressDialog[0].setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda11
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                ChatLinkActivity.this.m2093lambda$linkChat$14$orgtelegramuiChatLinkActivity(requestId, dialogInterface);
            }
        });
        showDialog(progressDialog[0]);
    }

    /* renamed from: lambda$linkChat$14$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2093lambda$linkChat$14$orgtelegramuiChatLinkActivity(int requestId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(requestId, true);
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
    }

    private void loadChats() {
        if (this.info.linked_chat_id != 0) {
            this.chats.clear();
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.info.linked_chat_id));
            if (chat != null) {
                this.chats.add(chat);
            }
            ActionBarMenuItem actionBarMenuItem = this.searchItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(8);
            }
        }
        if (this.loadingChats || !this.isChannel || this.info.linked_chat_id != 0) {
            return;
        }
        this.loadingChats = true;
        TLRPC.TL_channels_getGroupsForDiscussion req = new TLRPC.TL_channels_getGroupsForDiscussion();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatLinkActivity.this.m2096lambda$loadChats$17$orgtelegramuiChatLinkActivity(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadChats$17$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2096lambda$loadChats$17$orgtelegramuiChatLinkActivity(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                ChatLinkActivity.this.m2095lambda$loadChats$16$orgtelegramuiChatLinkActivity(response);
            }
        });
    }

    /* renamed from: lambda$loadChats$16$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2095lambda$loadChats$16$orgtelegramuiChatLinkActivity(TLObject response) {
        if (response instanceof TLRPC.messages_Chats) {
            TLRPC.messages_Chats res = (TLRPC.messages_Chats) response;
            getMessagesController().putChats(res.chats, false);
            this.chats = res.chats;
        }
        this.loadingChats = false;
        this.chatsLoaded = true;
        updateRows();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* loaded from: classes4.dex */
    public class HintInnerCell extends FrameLayout {
        private EmptyView emptyView;
        private TextView messageTextView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public HintInnerCell(Context context) {
            super(context);
            ChatLinkActivity.this = this$0;
            EmptyView emptyView = new EmptyView(context);
            this.emptyView = emptyView;
            addView(emptyView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_chats_message));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            if (this$0.isChannel) {
                if (this$0.info != null && this$0.info.linked_chat_id != 0) {
                    TLRPC.Chat chat = this$0.getMessagesController().getChat(Long.valueOf(this$0.info.linked_chat_id));
                    if (chat != null) {
                        this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("DiscussionChannelGroupSetHelp2", R.string.DiscussionChannelGroupSetHelp2, chat.title)));
                    }
                } else {
                    this.messageTextView.setText(LocaleController.getString("DiscussionChannelHelp3", R.string.DiscussionChannelHelp3));
                }
            } else {
                TLRPC.Chat chat2 = this$0.getMessagesController().getChat(Long.valueOf(this$0.info.linked_chat_id));
                if (chat2 != null) {
                    this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("DiscussionGroupHelp", R.string.DiscussionGroupHelp, chat2.title)));
                }
            }
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 143.0f, 52.0f, 18.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
        }
    }

    /* loaded from: classes4.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private ArrayList<TLRPC.Chat> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

        public SearchAdapter(Context context) {
            ChatLinkActivity.this = r1;
            this.mContext = context;
        }

        public void searchDialogs(final String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (TextUtils.isEmpty(query)) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$SearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.m2112x7729b16c(query);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }

        /* renamed from: processSearch */
        public void m2112x7729b16c(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.m2111xd276f016(query);
                }
            });
        }

        /* renamed from: lambda$processSearch$2$org-telegram-ui-ChatLinkActivity$SearchAdapter */
        public /* synthetic */ void m2111xd276f016(final String query) {
            this.searchRunnable = null;
            final ArrayList<TLRPC.Chat> chatsCopy = new ArrayList<>(ChatLinkActivity.this.chats);
            Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$SearchAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.m2110x98ac4e37(query, chatsCopy);
                }
            });
        }

        /* JADX WARN: Code restructure failed: missing block: B:34:0x00b2, code lost:
            if (r12.contains(" " + r3) != false) goto L41;
         */
        /* JADX WARN: Removed duplicated region for block: B:48:0x0105 A[LOOP:1: B:25:0x0074->B:48:0x0105, LOOP_END] */
        /* JADX WARN: Removed duplicated region for block: B:56:0x00ca A[SYNTHETIC] */
        /* renamed from: lambda$processSearch$1$org-telegram-ui-ChatLinkActivity$SearchAdapter */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void m2110x98ac4e37(java.lang.String r19, java.util.ArrayList r20) {
            /*
                Method dump skipped, instructions count: 283
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatLinkActivity.SearchAdapter.m2110x98ac4e37(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(final ArrayList<TLRPC.Chat> chats, final ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$SearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ChatLinkActivity.SearchAdapter.this.m2113x87a2ac07(chats, names);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$3$org-telegram-ui-ChatLinkActivity$SearchAdapter */
        public /* synthetic */ void m2113x87a2ac07(ArrayList chats, ArrayList names) {
            if (!ChatLinkActivity.this.searching) {
                return;
            }
            this.searchResult = chats;
            this.searchResultNames = names;
            if (ChatLinkActivity.this.listView.getAdapter() == ChatLinkActivity.this.searchAdapter) {
                ChatLinkActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.searchResult.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        public TLRPC.Chat getItem(int i) {
            return this.searchResult.get(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new ManageChatUserCell(this.mContext, 6, 2, false);
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.Chat chat = this.searchResult.get(position);
            String un = chat.username;
            CharSequence username = null;
            CharSequence name = this.searchResultNames.get(position);
            if (name != null && !TextUtils.isEmpty(un)) {
                String charSequence = name.toString();
                if (charSequence.startsWith("@" + un)) {
                    username = name;
                    name = null;
                }
            }
            ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
            userCell.setTag(Integer.valueOf(position));
            userCell.setData(chat, name, username, false);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            ChatLinkActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (!ChatLinkActivity.this.loadingChats || ChatLinkActivity.this.chatsLoaded) {
                return ChatLinkActivity.this.rowCount;
            }
            return 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ManageChatUserCell(this.mContext, 6, 2, false);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 2:
                    view = new ManageChatTextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                default:
                    view = new HintInnerCell(this.mContext);
                    break;
                case 4:
                    TLRPC.Chat chat = ChatLinkActivity.this.isChannel ? (TLRPC.Chat) ChatLinkActivity.this.chats.get(0) : ChatLinkActivity.this.currentChat;
                    view = ChatLinkActivity.this.joinToSendSettings = new AnonymousClass1(this.mContext, chat, chat);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: org.telegram.ui.ChatLinkActivity$ListAdapter$1 */
        /* loaded from: classes4.dex */
        public class AnonymousClass1 extends JoinToSendSettingsView {
            final /* synthetic */ TLRPC.Chat val$chat;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass1(Context context, TLRPC.Chat currentChat, TLRPC.Chat chat) {
                super(context, currentChat);
                ListAdapter.this = this$1;
                this.val$chat = chat;
            }

            private void migrateIfNeeded(Runnable onError, final Runnable onSuccess) {
                if (!ChatObject.isChannel(ChatLinkActivity.this.currentChat)) {
                    ChatLinkActivity.this.getMessagesController().convertToMegaGroup(ChatLinkActivity.this.getParentActivity(), this.val$chat.id, ChatLinkActivity.this, new MessagesStorage.LongCallback() { // from class: org.telegram.ui.ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda9
                        @Override // org.telegram.messenger.MessagesStorage.LongCallback
                        public final void run(long j) {
                            ChatLinkActivity.ListAdapter.AnonymousClass1.this.m2100xa6773969(onSuccess, j);
                        }
                    }, onError);
                } else {
                    onSuccess.run();
                }
            }

            /* renamed from: lambda$migrateIfNeeded$0$org-telegram-ui-ChatLinkActivity$ListAdapter$1 */
            public /* synthetic */ void m2100xa6773969(Runnable onSuccess, long param) {
                if (param != 0) {
                    if (ChatLinkActivity.this.isChannel) {
                        ChatLinkActivity.this.chats.set(0, ChatLinkActivity.this.getMessagesController().getChat(Long.valueOf(param)));
                    } else {
                        ChatLinkActivity.this.currentChatId = param;
                        ChatLinkActivity.this.currentChat = ChatLinkActivity.this.getMessagesController().getChat(Long.valueOf(param));
                    }
                    onSuccess.run();
                }
            }

            @Override // org.telegram.ui.Components.JoinToSendSettingsView
            public boolean onJoinRequestToggle(final boolean newValue, final Runnable cancel) {
                if (!ChatLinkActivity.this.joinRequestProgress) {
                    ChatLinkActivity.this.joinRequestProgress = true;
                    Runnable overrideCancel = overrideCancel(cancel);
                    final TLRPC.Chat chat = this.val$chat;
                    migrateIfNeeded(overrideCancel, new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda6
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatLinkActivity.ListAdapter.AnonymousClass1.this.m2103xe00b8a89(chat, newValue, cancel);
                        }
                    });
                    return true;
                }
                return false;
            }

            /* renamed from: lambda$onJoinRequestToggle$3$org-telegram-ui-ChatLinkActivity$ListAdapter$1 */
            public /* synthetic */ void m2103xe00b8a89(TLRPC.Chat chat, boolean newValue, final Runnable cancel) {
                chat.join_request = newValue;
                ChatLinkActivity.this.getMessagesController().toggleChatJoinRequest(chat.id, newValue, new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.m2101x6c7646cb();
                    }
                }, new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.m2102xa640e8aa(cancel);
                    }
                });
            }

            /* renamed from: lambda$onJoinRequestToggle$1$org-telegram-ui-ChatLinkActivity$ListAdapter$1 */
            public /* synthetic */ void m2101x6c7646cb() {
                ChatLinkActivity.this.joinRequestProgress = false;
            }

            /* renamed from: lambda$onJoinRequestToggle$2$org-telegram-ui-ChatLinkActivity$ListAdapter$1 */
            public /* synthetic */ void m2102xa640e8aa(Runnable cancel) {
                ChatLinkActivity.this.joinRequestProgress = false;
                cancel.run();
            }

            private Runnable overrideCancel(final Runnable cancel) {
                return new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.m2109x18321682(cancel);
                    }
                };
            }

            /* renamed from: lambda$overrideCancel$4$org-telegram-ui-ChatLinkActivity$ListAdapter$1 */
            public /* synthetic */ void m2109x18321682(Runnable cancel) {
                ChatLinkActivity.this.joinToSendProgress = false;
                ChatLinkActivity.this.joinRequestProgress = false;
                cancel.run();
            }

            @Override // org.telegram.ui.Components.JoinToSendSettingsView
            public boolean onJoinToSendToggle(final boolean newValue, final Runnable cancel) {
                if (!ChatLinkActivity.this.joinToSendProgress) {
                    ChatLinkActivity.this.joinToSendProgress = true;
                    Runnable overrideCancel = overrideCancel(cancel);
                    final TLRPC.Chat chat = this.val$chat;
                    migrateIfNeeded(overrideCancel, new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda7
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatLinkActivity.ListAdapter.AnonymousClass1.this.m2108xe91e22e3(chat, newValue, cancel);
                        }
                    });
                    return true;
                }
                return false;
            }

            /* renamed from: lambda$onJoinToSendToggle$9$org-telegram-ui-ChatLinkActivity$ListAdapter$1 */
            public /* synthetic */ void m2108xe91e22e3(final TLRPC.Chat chat, final boolean newValue, final Runnable cancel) {
                chat.join_to_send = newValue;
                ChatLinkActivity.this.getMessagesController().toggleChatJoinToSend(chat.id, newValue, new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.m2106x7588df25(newValue, chat);
                    }
                }, new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatLinkActivity.ListAdapter.AnonymousClass1.this.m2107xaf538104(cancel);
                    }
                });
            }

            /* renamed from: lambda$onJoinToSendToggle$7$org-telegram-ui-ChatLinkActivity$ListAdapter$1 */
            public /* synthetic */ void m2106x7588df25(boolean newValue, final TLRPC.Chat chat) {
                ChatLinkActivity.this.joinToSendProgress = false;
                if (!newValue && chat.join_request) {
                    chat.join_request = false;
                    ChatLinkActivity.this.joinRequestProgress = true;
                    ChatLinkActivity.this.getMessagesController().toggleChatJoinRequest(chat.id, false, new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatLinkActivity.ListAdapter.AnonymousClass1.this.m2104x1f39b67();
                        }
                    }, new Runnable() { // from class: org.telegram.ui.ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatLinkActivity.ListAdapter.AnonymousClass1.this.m2105x3bbe3d46(chat);
                        }
                    });
                }
            }

            /* renamed from: lambda$onJoinToSendToggle$5$org-telegram-ui-ChatLinkActivity$ListAdapter$1 */
            public /* synthetic */ void m2104x1f39b67() {
                ChatLinkActivity.this.joinRequestProgress = false;
            }

            /* renamed from: lambda$onJoinToSendToggle$6$org-telegram-ui-ChatLinkActivity$ListAdapter$1 */
            public /* synthetic */ void m2105x3bbe3d46(TLRPC.Chat chat) {
                chat.join_request = true;
                this.isJoinRequest = true;
                this.joinRequestCell.setChecked(true);
            }

            /* renamed from: lambda$onJoinToSendToggle$8$org-telegram-ui-ChatLinkActivity$ListAdapter$1 */
            public /* synthetic */ void m2107xaf538104(Runnable cancel) {
                ChatLinkActivity.this.joinToSendProgress = false;
                cancel.run();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String str;
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    userCell.setTag(Integer.valueOf(position));
                    TLRPC.Chat chat = (TLRPC.Chat) ChatLinkActivity.this.chats.get(position - ChatLinkActivity.this.chatStartRow);
                    if (TextUtils.isEmpty(chat.username)) {
                        str = null;
                    } else {
                        str = "@" + chat.username;
                    }
                    if (position == ChatLinkActivity.this.chatEndRow - 1 && ChatLinkActivity.this.info.linked_chat_id == 0) {
                        z = false;
                    }
                    userCell.setData(chat, null, str, z);
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == ChatLinkActivity.this.detailRow) {
                        if (ChatLinkActivity.this.isChannel) {
                            privacyCell.setText(LocaleController.getString("DiscussionChannelHelp2", R.string.DiscussionChannelHelp2));
                            return;
                        } else {
                            privacyCell.setText(LocaleController.getString("DiscussionGroupHelp2", R.string.DiscussionGroupHelp2));
                            return;
                        }
                    }
                    return;
                case 2:
                    ManageChatTextCell actionCell = (ManageChatTextCell) holder.itemView;
                    if (ChatLinkActivity.this.isChannel) {
                        if (ChatLinkActivity.this.info.linked_chat_id != 0) {
                            actionCell.setColors(Theme.key_windowBackgroundWhiteRedText5, Theme.key_windowBackgroundWhiteRedText5);
                            actionCell.setText(LocaleController.getString("DiscussionUnlinkGroup", R.string.DiscussionUnlinkGroup), null, R.drawable.msg_remove, false);
                            return;
                        }
                        actionCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                        actionCell.setText(LocaleController.getString("DiscussionCreateGroup", R.string.DiscussionCreateGroup), null, R.drawable.msg_groups, true);
                        return;
                    }
                    actionCell.setColors(Theme.key_windowBackgroundWhiteRedText5, Theme.key_windowBackgroundWhiteRedText5);
                    actionCell.setText(LocaleController.getString("DiscussionUnlinkChannel", R.string.DiscussionUnlinkChannel), null, R.drawable.msg_remove, false);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != ChatLinkActivity.this.helpRow) {
                if (position != ChatLinkActivity.this.createChatRow && position != ChatLinkActivity.this.removeChatRow) {
                    if (position < ChatLinkActivity.this.chatStartRow || position >= ChatLinkActivity.this.chatEndRow) {
                        if (position == ChatLinkActivity.this.joinToSendRow) {
                            return 4;
                        }
                        return 1;
                    }
                    return 0;
                }
                return 2;
            }
            return 3;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ChatLinkActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ChatLinkActivity.this.m2088lambda$getThemeDescriptions$18$orgtelegramuiChatLinkActivity();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_windowBackgroundWhiteBlueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_message));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$18$org-telegram-ui-ChatLinkActivity */
    public /* synthetic */ void m2088lambda$getThemeDescriptions$18$orgtelegramuiChatLinkActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
            }
        }
    }
}
