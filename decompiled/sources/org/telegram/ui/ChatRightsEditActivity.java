package org.telegram.ui;

import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DialogRadioCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell2;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.TwoStepVerificationActivity;
/* loaded from: classes4.dex */
public class ChatRightsEditActivity extends BaseFragment {
    private static final int MAX_RANK_LENGTH = 16;
    public static final int TYPE_ADD_BOT = 2;
    public static final int TYPE_ADMIN = 0;
    public static final int TYPE_BANNED = 1;
    private static final int done_button = 1;
    private int addAdminsRow;
    private FrameLayout addBotButton;
    private FrameLayout addBotButtonContainer;
    private int addBotButtonRow;
    private AnimatedTextView addBotButtonText;
    private int addUsersRow;
    private TLRPC.TL_chatAdminRights adminRights;
    private int anonymousRow;
    private boolean asAdmin;
    private ValueAnimator asAdminAnimator;
    private float asAdminT;
    private int banUsersRow;
    private TLRPC.TL_chatBannedRights bannedRights;
    private String botHash;
    private boolean canEdit;
    private int cantEditInfoRow;
    private int changeInfoRow;
    private long chatId;
    private String currentBannedRights;
    private TLRPC.Chat currentChat;
    private String currentRank;
    private int currentType;
    private TLRPC.User currentUser;
    private TLRPC.TL_chatBannedRights defaultBannedRights;
    private ChatRightsEditActivityDelegate delegate;
    private int deleteMessagesRow;
    private CrossfadeDrawable doneDrawable;
    private ValueAnimator doneDrawableAnimator;
    private int editMesagesRow;
    private int embedLinksRow;
    private boolean initialAsAdmin;
    private boolean initialIsSet;
    private String initialRank;
    private boolean isAddingNew;
    private boolean isChannel;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private int manageRow;
    private TLRPC.TL_chatAdminRights myAdminRights;
    private int permissionsEndRow;
    private int permissionsStartRow;
    private int pinMessagesRow;
    private int postMessagesRow;
    private PollEditTextCell rankEditTextCell;
    private int rankHeaderRow;
    private int rankInfoRow;
    private int rankRow;
    private int removeAdminRow;
    private int removeAdminShadowRow;
    private int rightsShadowRow;
    private int rowCount;
    private int sendMediaRow;
    private int sendMessagesRow;
    private int sendPollsRow;
    private int sendStickersRow;
    private int startVoiceChatRow;
    private int transferOwnerRow;
    private int transferOwnerShadowRow;
    private int untilDateRow;
    private int untilSectionRow;
    private boolean loading = false;
    private boolean closingKeyboardAfterFinish = false;

    /* loaded from: classes4.dex */
    public interface ChatRightsEditActivityDelegate {
        void didChangeOwner(TLRPC.User user);

        void didSetRights(int i, TLRPC.TL_chatAdminRights tL_chatAdminRights, TLRPC.TL_chatBannedRights tL_chatBannedRights, String str);
    }

    public ChatRightsEditActivity(long userId, long channelId, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBannedDefault, TLRPC.TL_chatBannedRights rightsBanned, String rank, int type, boolean edit, boolean addingNew, String addingNewBotHash) {
        String rank2;
        boolean z;
        TLRPC.UserFull userFull;
        TLRPC.Chat chat;
        TLRPC.TL_chatAdminRights rightsAdmin2 = rightsAdmin;
        this.asAdminT = 0.0f;
        this.asAdmin = false;
        this.initialAsAdmin = false;
        this.currentBannedRights = "";
        this.isAddingNew = addingNew;
        this.chatId = channelId;
        this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(userId));
        this.currentType = type;
        this.canEdit = edit;
        this.botHash = addingNewBotHash;
        TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.chatId));
        this.currentChat = chat2;
        if (rank != null) {
            rank2 = rank;
        } else {
            rank2 = "";
        }
        this.currentRank = rank2;
        this.initialRank = rank2;
        boolean z2 = true;
        if (chat2 != null) {
            this.isChannel = ChatObject.isChannel(chat2) && !this.currentChat.megagroup;
            this.myAdminRights = this.currentChat.admin_rights;
        }
        if (this.myAdminRights == null) {
            this.myAdminRights = emptyAdminRights(this.currentType != 2 || ((chat = this.currentChat) != null && chat.creator));
        }
        if (type == 0 || type == 2) {
            if (type == 2 && (userFull = getMessagesController().getUserFull(userId)) != null) {
                TLRPC.TL_chatAdminRights botDefaultRights = this.isChannel ? userFull.bot_broadcast_admin_rights : userFull.bot_group_admin_rights;
                if (botDefaultRights != null) {
                    if (rightsAdmin2 == null) {
                        rightsAdmin2 = botDefaultRights;
                    } else {
                        rightsAdmin2.ban_users = rightsAdmin2.ban_users || botDefaultRights.ban_users;
                        rightsAdmin2.add_admins = rightsAdmin2.add_admins || botDefaultRights.add_admins;
                        rightsAdmin2.post_messages = rightsAdmin2.post_messages || botDefaultRights.post_messages;
                        rightsAdmin2.pin_messages = rightsAdmin2.pin_messages || botDefaultRights.pin_messages;
                        rightsAdmin2.delete_messages = rightsAdmin2.delete_messages || botDefaultRights.delete_messages;
                        rightsAdmin2.change_info = rightsAdmin2.change_info || botDefaultRights.change_info;
                        rightsAdmin2.anonymous = rightsAdmin2.anonymous || botDefaultRights.anonymous;
                        rightsAdmin2.edit_messages = rightsAdmin2.edit_messages || botDefaultRights.edit_messages;
                        rightsAdmin2.manage_call = rightsAdmin2.manage_call || botDefaultRights.manage_call;
                        rightsAdmin2.other = rightsAdmin2.other || botDefaultRights.other;
                    }
                }
            }
            float f = 1.0f;
            if (rightsAdmin2 != null) {
                this.initialAsAdmin = true;
                TLRPC.TL_chatAdminRights tL_chatAdminRights = new TLRPC.TL_chatAdminRights();
                this.adminRights = tL_chatAdminRights;
                tL_chatAdminRights.change_info = rightsAdmin2.change_info;
                this.adminRights.post_messages = rightsAdmin2.post_messages;
                this.adminRights.edit_messages = rightsAdmin2.edit_messages;
                this.adminRights.delete_messages = rightsAdmin2.delete_messages;
                this.adminRights.manage_call = rightsAdmin2.manage_call;
                this.adminRights.ban_users = rightsAdmin2.ban_users;
                this.adminRights.invite_users = rightsAdmin2.invite_users;
                this.adminRights.pin_messages = rightsAdmin2.pin_messages;
                this.adminRights.add_admins = rightsAdmin2.add_admins;
                this.adminRights.anonymous = rightsAdmin2.anonymous;
                this.adminRights.other = rightsAdmin2.other;
                boolean z3 = this.adminRights.change_info || this.adminRights.post_messages || this.adminRights.edit_messages || this.adminRights.delete_messages || this.adminRights.ban_users || this.adminRights.invite_users || this.adminRights.pin_messages || this.adminRights.add_admins || this.adminRights.manage_call || this.adminRights.anonymous || this.adminRights.other;
                this.initialIsSet = z3;
                if (type == 2) {
                    boolean z4 = this.isChannel || z3;
                    this.asAdmin = z4;
                    this.asAdminT = !z4 ? 0.0f : f;
                    this.initialIsSet = false;
                }
            } else {
                this.initialAsAdmin = false;
                if (type == 2) {
                    this.adminRights = emptyAdminRights(false);
                    boolean z5 = this.isChannel;
                    this.asAdmin = z5;
                    this.asAdminT = !z5 ? 0.0f : f;
                    this.initialIsSet = false;
                } else {
                    TLRPC.TL_chatAdminRights tL_chatAdminRights2 = new TLRPC.TL_chatAdminRights();
                    this.adminRights = tL_chatAdminRights2;
                    tL_chatAdminRights2.change_info = this.myAdminRights.change_info;
                    this.adminRights.post_messages = this.myAdminRights.post_messages;
                    this.adminRights.edit_messages = this.myAdminRights.edit_messages;
                    this.adminRights.delete_messages = this.myAdminRights.delete_messages;
                    this.adminRights.manage_call = this.myAdminRights.manage_call;
                    this.adminRights.ban_users = this.myAdminRights.ban_users;
                    this.adminRights.invite_users = this.myAdminRights.invite_users;
                    this.adminRights.pin_messages = this.myAdminRights.pin_messages;
                    this.adminRights.other = this.myAdminRights.other;
                    this.initialIsSet = false;
                }
            }
            TLRPC.Chat chat3 = this.currentChat;
            if (chat3 != null) {
                this.defaultBannedRights = chat3.default_banned_rights;
            }
            if (this.defaultBannedRights != null) {
                z = true;
            } else {
                TLRPC.TL_chatBannedRights tL_chatBannedRights = new TLRPC.TL_chatBannedRights();
                this.defaultBannedRights = tL_chatBannedRights;
                z = true;
                tL_chatBannedRights.pin_messages = true;
                tL_chatBannedRights.change_info = true;
                tL_chatBannedRights.invite_users = true;
                tL_chatBannedRights.send_polls = true;
                tL_chatBannedRights.send_inline = true;
                tL_chatBannedRights.send_games = true;
                tL_chatBannedRights.send_gifs = true;
                tL_chatBannedRights.send_stickers = true;
                tL_chatBannedRights.embed_links = true;
                tL_chatBannedRights.send_messages = true;
                tL_chatBannedRights.send_media = true;
                tL_chatBannedRights.view_messages = true;
            }
            if (!this.defaultBannedRights.change_info) {
                this.adminRights.change_info = z;
            }
            if (!this.defaultBannedRights.pin_messages) {
                this.adminRights.pin_messages = z;
            }
        } else if (type == 1) {
            this.defaultBannedRights = rightsBannedDefault;
            if (rightsBannedDefault == null) {
                TLRPC.TL_chatBannedRights tL_chatBannedRights2 = new TLRPC.TL_chatBannedRights();
                this.defaultBannedRights = tL_chatBannedRights2;
                tL_chatBannedRights2.pin_messages = false;
                tL_chatBannedRights2.change_info = false;
                tL_chatBannedRights2.invite_users = false;
                tL_chatBannedRights2.send_polls = false;
                tL_chatBannedRights2.send_inline = false;
                tL_chatBannedRights2.send_games = false;
                tL_chatBannedRights2.send_gifs = false;
                tL_chatBannedRights2.send_stickers = false;
                tL_chatBannedRights2.embed_links = false;
                tL_chatBannedRights2.send_messages = false;
                tL_chatBannedRights2.send_media = false;
                tL_chatBannedRights2.view_messages = false;
            }
            TLRPC.TL_chatBannedRights tL_chatBannedRights3 = new TLRPC.TL_chatBannedRights();
            this.bannedRights = tL_chatBannedRights3;
            if (rightsBanned == null) {
                tL_chatBannedRights3.pin_messages = false;
                tL_chatBannedRights3.change_info = false;
                tL_chatBannedRights3.invite_users = false;
                tL_chatBannedRights3.send_polls = false;
                tL_chatBannedRights3.send_inline = false;
                tL_chatBannedRights3.send_games = false;
                tL_chatBannedRights3.send_gifs = false;
                tL_chatBannedRights3.send_stickers = false;
                tL_chatBannedRights3.embed_links = false;
                tL_chatBannedRights3.send_messages = false;
                tL_chatBannedRights3.send_media = false;
                tL_chatBannedRights3.view_messages = false;
            } else {
                tL_chatBannedRights3.view_messages = rightsBanned.view_messages;
                this.bannedRights.send_messages = rightsBanned.send_messages;
                this.bannedRights.send_media = rightsBanned.send_media;
                this.bannedRights.send_stickers = rightsBanned.send_stickers;
                this.bannedRights.send_gifs = rightsBanned.send_gifs;
                this.bannedRights.send_games = rightsBanned.send_games;
                this.bannedRights.send_inline = rightsBanned.send_inline;
                this.bannedRights.embed_links = rightsBanned.embed_links;
                this.bannedRights.send_polls = rightsBanned.send_polls;
                this.bannedRights.invite_users = rightsBanned.invite_users;
                this.bannedRights.change_info = rightsBanned.change_info;
                this.bannedRights.pin_messages = rightsBanned.pin_messages;
                this.bannedRights.until_date = rightsBanned.until_date;
            }
            if (this.defaultBannedRights.view_messages) {
                this.bannedRights.view_messages = true;
            }
            if (this.defaultBannedRights.send_messages) {
                this.bannedRights.send_messages = true;
            }
            if (this.defaultBannedRights.send_media) {
                this.bannedRights.send_media = true;
            }
            if (this.defaultBannedRights.send_stickers) {
                this.bannedRights.send_stickers = true;
            }
            if (this.defaultBannedRights.send_gifs) {
                this.bannedRights.send_gifs = true;
            }
            if (this.defaultBannedRights.send_games) {
                this.bannedRights.send_games = true;
            }
            if (this.defaultBannedRights.send_inline) {
                this.bannedRights.send_inline = true;
            }
            if (this.defaultBannedRights.embed_links) {
                this.bannedRights.embed_links = true;
            }
            if (this.defaultBannedRights.send_polls) {
                this.bannedRights.send_polls = true;
            }
            if (this.defaultBannedRights.invite_users) {
                this.bannedRights.invite_users = true;
            }
            if (this.defaultBannedRights.change_info) {
                this.bannedRights.change_info = true;
            }
            if (this.defaultBannedRights.pin_messages) {
                this.bannedRights.pin_messages = true;
            }
            this.currentBannedRights = ChatObject.getBannedRightsString(this.bannedRights);
            if (rightsBanned != null && rightsBanned.view_messages) {
                z2 = false;
            }
            this.initialIsSet = z2;
        }
        updateRows(false);
    }

    public static TLRPC.TL_chatAdminRights emptyAdminRights(boolean value) {
        TLRPC.TL_chatAdminRights adminRights = new TLRPC.TL_chatAdminRights();
        adminRights.manage_call = value;
        adminRights.add_admins = value;
        adminRights.pin_messages = value;
        adminRights.invite_users = value;
        adminRights.ban_users = value;
        adminRights.delete_messages = value;
        adminRights.edit_messages = value;
        adminRights.post_messages = value;
        adminRights.change_info = value;
        return adminRights;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        int i2 = this.currentType;
        if (i2 == 0) {
            this.actionBar.setTitle(LocaleController.getString("EditAdmin", R.string.EditAdmin));
        } else if (i2 == 2) {
            this.actionBar.setTitle(LocaleController.getString("AddBot", R.string.AddBot));
        } else {
            this.actionBar.setTitle(LocaleController.getString("UserRestrictions", R.string.UserRestrictions));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChatRightsEditActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (ChatRightsEditActivity.this.checkDiscard()) {
                        ChatRightsEditActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    ChatRightsEditActivity.this.onDonePressed();
                }
            }
        });
        if (this.canEdit || (!this.isChannel && this.currentChat.creator && UserObject.isUserSelf(this.currentUser))) {
            ActionBarMenu menu = this.actionBar.createMenu();
            Drawable checkmark = context.getResources().getDrawable(R.drawable.ic_ab_done).mutate();
            checkmark.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
            this.doneDrawable = new CrossfadeDrawable(checkmark, new CircularProgressDrawable(Theme.getColor(Theme.key_actionBarDefaultIcon)));
            menu.addItemWithWidth(1, 0, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", R.string.Done));
            menu.getItem(1).setIcon(this.doneDrawable);
        }
        this.fragmentView = new FrameLayout(context) { // from class: org.telegram.ui.ChatRightsEditActivity.2
            private int previousHeight = -1;

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int height = bottom - top;
                int i3 = this.previousHeight;
                if (i3 != -1 && Math.abs(i3 - height) > AndroidUtilities.dp(20.0f)) {
                    ChatRightsEditActivity.this.listView.smoothScrollToPosition(ChatRightsEditActivity.this.rowCount - 1);
                }
                this.previousHeight = height;
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.fragmentView.setFocusableInTouchMode(true);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.ChatRightsEditActivity.3
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                if (ChatRightsEditActivity.this.loading) {
                    return false;
                }
                return super.onTouchEvent(e);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent e) {
                if (ChatRightsEditActivity.this.loading) {
                    return false;
                }
                return super.onInterceptTouchEvent(e);
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setClipChildren(this.currentType != 2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false) { // from class: org.telegram.ui.ChatRightsEditActivity.4
            @Override // androidx.recyclerview.widget.LinearLayoutManager
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 5000;
            }
        };
        this.linearLayoutManager = linearLayoutManager;
        linearLayoutManager.setInitialPrefetchItemCount(100);
        this.listView.setLayoutManager(this.linearLayoutManager);
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        DefaultItemAnimator itemAnimator = new DefaultItemAnimator();
        if (this.currentType == 2) {
            this.listView.setResetSelectorOnChanged(false);
        }
        itemAnimator.setDelayAnimations(false);
        this.listView.setItemAnimator(itemAnimator);
        RecyclerListView recyclerListView3 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView3.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ChatRightsEditActivity.5
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(ChatRightsEditActivity.this.getParentActivity().getCurrentFocus());
                }
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda17
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i3) {
                ChatRightsEditActivity.this.m2128lambda$createView$6$orgtelegramuiChatRightsEditActivity(context, view, i3);
            }
        });
        return this.fragmentView;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0 */
    /* JADX WARN: Type inference failed for: r10v10 */
    /* renamed from: lambda$createView$6$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2128lambda$createView$6$orgtelegramuiChatRightsEditActivity(Context context, View view, int position) {
        TLRPC.TL_chatBannedRights tL_chatBannedRights;
        TLRPC.TL_chatBannedRights tL_chatBannedRights2;
        String text;
        if (!this.canEdit && (!this.currentChat.creator || this.currentType != 0 || position != this.anonymousRow)) {
            return;
        }
        if (position != 0) {
            int i = 0;
            if (position == this.removeAdminRow) {
                int i2 = this.currentType;
                if (i2 == 0) {
                    MessagesController.getInstance(this.currentAccount).setUserAdminRole(this.chatId, this.currentUser, new TLRPC.TL_chatAdminRights(), this.currentRank, this.isChannel, getFragmentForAlert(0), this.isAddingNew, false, null, null);
                    ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
                    if (chatRightsEditActivityDelegate != null) {
                        chatRightsEditActivityDelegate.didSetRights(0, this.adminRights, this.bannedRights, this.currentRank);
                    }
                    finishFragment();
                    return;
                } else if (i2 == 1) {
                    TLRPC.TL_chatBannedRights tL_chatBannedRights3 = new TLRPC.TL_chatBannedRights();
                    this.bannedRights = tL_chatBannedRights3;
                    tL_chatBannedRights3.view_messages = true;
                    this.bannedRights.send_media = true;
                    this.bannedRights.send_messages = true;
                    this.bannedRights.send_stickers = true;
                    this.bannedRights.send_gifs = true;
                    this.bannedRights.send_games = true;
                    this.bannedRights.send_inline = true;
                    this.bannedRights.embed_links = true;
                    this.bannedRights.pin_messages = true;
                    this.bannedRights.send_polls = true;
                    this.bannedRights.invite_users = true;
                    this.bannedRights.change_info = true;
                    this.bannedRights.until_date = 0;
                    onDonePressed();
                    return;
                } else {
                    return;
                }
            } else if (position == this.transferOwnerRow) {
                m2136lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(null, null);
                return;
            } else if (position == this.untilDateRow) {
                if (getParentActivity() == null) {
                    return;
                }
                final BottomSheet.Builder builder = new BottomSheet.Builder(context);
                builder.setApplyTopPadding(false);
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(1);
                HeaderCell headerCell = new HeaderCell(context, Theme.key_dialogTextBlue2, 23, 15, false);
                headerCell.setHeight(47);
                headerCell.setText(LocaleController.getString("UserRestrictionsDuration", R.string.UserRestrictionsDuration));
                linearLayout.addView(headerCell);
                LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
                linearLayoutInviteContainer.setOrientation(1);
                linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
                BottomSheet.BottomSheetCell[] buttons = new BottomSheet.BottomSheetCell[5];
                int a = 0;
                while (a < buttons.length) {
                    buttons[a] = new BottomSheet.BottomSheetCell(context, i);
                    buttons[a].setPadding(AndroidUtilities.dp(7.0f), i, AndroidUtilities.dp(7.0f), i);
                    buttons[a].setTag(Integer.valueOf(a));
                    buttons[a].setBackgroundDrawable(Theme.getSelectorDrawable(i));
                    switch (a) {
                        case 0:
                            text = LocaleController.getString("UserRestrictionsUntilForever", R.string.UserRestrictionsUntilForever);
                            break;
                        case 1:
                            text = LocaleController.formatPluralString("Days", 1, new Object[i]);
                            break;
                        case 2:
                            text = LocaleController.formatPluralString("Weeks", 1, new Object[i]);
                            break;
                        case 3:
                            text = LocaleController.formatPluralString("Months", 1, new Object[i]);
                            break;
                        default:
                            text = LocaleController.getString("UserRestrictionsCustom", R.string.UserRestrictionsCustom);
                            break;
                    }
                    buttons[a].setTextAndIcon(text, i);
                    linearLayoutInviteContainer.addView(buttons[a], LayoutHelper.createLinear(-1, -2));
                    buttons[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda3
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            ChatRightsEditActivity.this.m2127lambda$createView$5$orgtelegramuiChatRightsEditActivity(builder, view2);
                        }
                    });
                    a++;
                    i = 0;
                }
                builder.setCustomView(linearLayout);
                showDialog(builder.create());
                return;
            } else if (view instanceof TextCheckCell2) {
                TextCheckCell2 checkCell = (TextCheckCell2) view;
                if (checkCell.hasIcon()) {
                    if (this.currentType != 2) {
                        new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString("UserRestrictionsCantModify", R.string.UserRestrictionsCantModify)).setMessage(LocaleController.getString("UserRestrictionsCantModifyDisabled", R.string.UserRestrictionsCantModifyDisabled)).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).create().show();
                        return;
                    }
                    return;
                } else if (!checkCell.isEnabled()) {
                    int i3 = this.currentType;
                    if (i3 == 2 || i3 == 0) {
                        if ((position == this.changeInfoRow && (tL_chatBannedRights2 = this.defaultBannedRights) != null && !tL_chatBannedRights2.change_info) || (position == this.pinMessagesRow && (tL_chatBannedRights = this.defaultBannedRights) != null && !tL_chatBannedRights.pin_messages)) {
                            new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString("UserRestrictionsCantModify", R.string.UserRestrictionsCantModify)).setMessage(LocaleController.getString("UserRestrictionsCantModifyEnabled", R.string.UserRestrictionsCantModifyEnabled)).setPositiveButton(LocaleController.getString("OK", R.string.OK), null).create().show();
                            return;
                        }
                        return;
                    }
                    return;
                } else {
                    if (this.currentType != 2) {
                        checkCell.setChecked(!checkCell.isChecked());
                    }
                    boolean value = checkCell.isChecked();
                    if (position == this.manageRow) {
                        boolean z = !this.asAdmin;
                        this.asAdmin = z;
                        value = z;
                        updateAsAdmin(true);
                    } else if (position == this.changeInfoRow) {
                        int i4 = this.currentType;
                        if (i4 == 0 || i4 == 2) {
                            TLRPC.TL_chatAdminRights tL_chatAdminRights = this.adminRights;
                            boolean z2 = !tL_chatAdminRights.change_info;
                            tL_chatAdminRights.change_info = z2;
                            value = z2;
                        } else {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights4 = this.bannedRights;
                            boolean z3 = !tL_chatBannedRights4.change_info;
                            tL_chatBannedRights4.change_info = z3;
                            value = z3;
                        }
                    } else if (position == this.postMessagesRow) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights2 = this.adminRights;
                        boolean z4 = !tL_chatAdminRights2.post_messages;
                        tL_chatAdminRights2.post_messages = z4;
                        value = z4;
                    } else if (position == this.editMesagesRow) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights3 = this.adminRights;
                        boolean z5 = !tL_chatAdminRights3.edit_messages;
                        tL_chatAdminRights3.edit_messages = z5;
                        value = z5;
                    } else if (position == this.deleteMessagesRow) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights4 = this.adminRights;
                        boolean z6 = !tL_chatAdminRights4.delete_messages;
                        tL_chatAdminRights4.delete_messages = z6;
                        value = z6;
                    } else if (position == this.addAdminsRow) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights5 = this.adminRights;
                        boolean z7 = !tL_chatAdminRights5.add_admins;
                        tL_chatAdminRights5.add_admins = z7;
                        value = z7;
                    } else if (position == this.anonymousRow) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights6 = this.adminRights;
                        boolean z8 = !tL_chatAdminRights6.anonymous;
                        tL_chatAdminRights6.anonymous = z8;
                        value = z8;
                    } else if (position == this.banUsersRow) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights7 = this.adminRights;
                        boolean z9 = !tL_chatAdminRights7.ban_users;
                        tL_chatAdminRights7.ban_users = z9;
                        value = z9;
                    } else if (position == this.startVoiceChatRow) {
                        TLRPC.TL_chatAdminRights tL_chatAdminRights8 = this.adminRights;
                        boolean z10 = !tL_chatAdminRights8.manage_call;
                        tL_chatAdminRights8.manage_call = z10;
                        value = z10;
                    } else if (position == this.addUsersRow) {
                        int i5 = this.currentType;
                        if (i5 == 0 || i5 == 2) {
                            TLRPC.TL_chatAdminRights tL_chatAdminRights9 = this.adminRights;
                            boolean z11 = !tL_chatAdminRights9.invite_users;
                            tL_chatAdminRights9.invite_users = z11;
                            value = z11;
                        } else {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights5 = this.bannedRights;
                            boolean z12 = !tL_chatBannedRights5.invite_users;
                            tL_chatBannedRights5.invite_users = z12;
                            value = z12;
                        }
                    } else if (position == this.pinMessagesRow) {
                        int i6 = this.currentType;
                        if (i6 == 0 || i6 == 2) {
                            TLRPC.TL_chatAdminRights tL_chatAdminRights10 = this.adminRights;
                            boolean z13 = !tL_chatAdminRights10.pin_messages;
                            tL_chatAdminRights10.pin_messages = z13;
                            value = z13;
                        } else {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights6 = this.bannedRights;
                            boolean z14 = !tL_chatBannedRights6.pin_messages;
                            tL_chatBannedRights6.pin_messages = z14;
                            value = z14;
                        }
                    } else if (this.currentType == 1 && this.bannedRights != null) {
                        boolean disabled = !checkCell.isChecked();
                        if (position == this.sendMessagesRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights7 = this.bannedRights;
                            boolean z15 = !tL_chatBannedRights7.send_messages;
                            tL_chatBannedRights7.send_messages = z15;
                            value = z15;
                        } else if (position == this.sendMediaRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights8 = this.bannedRights;
                            boolean z16 = !tL_chatBannedRights8.send_media;
                            tL_chatBannedRights8.send_media = z16;
                            value = z16;
                        } else if (position == this.sendStickersRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights9 = this.bannedRights;
                            boolean z17 = !tL_chatBannedRights9.send_stickers;
                            tL_chatBannedRights9.send_inline = z17;
                            tL_chatBannedRights9.send_gifs = z17;
                            tL_chatBannedRights9.send_games = z17;
                            tL_chatBannedRights9.send_stickers = z17;
                            value = z17;
                        } else if (position == this.embedLinksRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights10 = this.bannedRights;
                            boolean z18 = !tL_chatBannedRights10.embed_links;
                            tL_chatBannedRights10.embed_links = z18;
                            value = z18;
                        } else if (position == this.sendPollsRow) {
                            TLRPC.TL_chatBannedRights tL_chatBannedRights11 = this.bannedRights;
                            boolean z19 = !tL_chatBannedRights11.send_polls;
                            tL_chatBannedRights11.send_polls = z19;
                            value = z19;
                        }
                        if (disabled) {
                            if (this.bannedRights.view_messages && !this.bannedRights.send_messages) {
                                this.bannedRights.send_messages = true;
                                RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                                if (holder != null) {
                                    ((TextCheckCell2) holder.itemView).setChecked(false);
                                }
                            }
                            if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.send_media) {
                                this.bannedRights.send_media = true;
                                RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(this.sendMediaRow);
                                if (holder2 != null) {
                                    ((TextCheckCell2) holder2.itemView).setChecked(false);
                                }
                            }
                            if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.send_polls) {
                                this.bannedRights.send_polls = true;
                                RecyclerView.ViewHolder holder3 = this.listView.findViewHolderForAdapterPosition(this.sendPollsRow);
                                if (holder3 != null) {
                                    ((TextCheckCell2) holder3.itemView).setChecked(false);
                                }
                            }
                            if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.send_stickers) {
                                TLRPC.TL_chatBannedRights tL_chatBannedRights12 = this.bannedRights;
                                tL_chatBannedRights12.send_inline = true;
                                tL_chatBannedRights12.send_gifs = true;
                                tL_chatBannedRights12.send_games = true;
                                tL_chatBannedRights12.send_stickers = true;
                                RecyclerView.ViewHolder holder4 = this.listView.findViewHolderForAdapterPosition(this.sendStickersRow);
                                if (holder4 != null) {
                                    ((TextCheckCell2) holder4.itemView).setChecked(false);
                                }
                            }
                            if ((this.bannedRights.view_messages || this.bannedRights.send_messages) && !this.bannedRights.embed_links) {
                                this.bannedRights.embed_links = true;
                                RecyclerView.ViewHolder holder5 = this.listView.findViewHolderForAdapterPosition(this.embedLinksRow);
                                if (holder5 != null) {
                                    ((TextCheckCell2) holder5.itemView).setChecked(false);
                                }
                            }
                        } else {
                            if ((!this.bannedRights.send_messages || !this.bannedRights.embed_links || !this.bannedRights.send_inline || !this.bannedRights.send_media || !this.bannedRights.send_polls) && this.bannedRights.view_messages) {
                                this.bannedRights.view_messages = false;
                            }
                            if ((!this.bannedRights.embed_links || !this.bannedRights.send_inline || !this.bannedRights.send_media || !this.bannedRights.send_polls) && this.bannedRights.send_messages) {
                                this.bannedRights.send_messages = false;
                                RecyclerView.ViewHolder holder6 = this.listView.findViewHolderForAdapterPosition(this.sendMessagesRow);
                                if (holder6 != null) {
                                    ((TextCheckCell2) holder6.itemView).setChecked(true);
                                }
                            }
                        }
                    }
                    if (this.currentType == 2) {
                        checkCell.setChecked(this.asAdmin && value);
                    }
                    updateRows(true);
                    return;
                }
            } else {
                return;
            }
        }
        Bundle args = new Bundle();
        args.putLong("user_id", this.currentUser.id);
        presentFragment(new ProfileActivity(args));
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2127lambda$createView$5$orgtelegramuiChatRightsEditActivity(BottomSheet.Builder builder, View v2) {
        Integer tag = (Integer) v2.getTag();
        switch (tag.intValue()) {
            case 0:
                this.bannedRights.until_date = 0;
                this.listViewAdapter.notifyItemChanged(this.untilDateRow);
                break;
            case 1:
                this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 86400;
                this.listViewAdapter.notifyItemChanged(this.untilDateRow);
                break;
            case 2:
                this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 604800;
                this.listViewAdapter.notifyItemChanged(this.untilDateRow);
                break;
            case 3:
                this.bannedRights.until_date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 2592000;
                this.listViewAdapter.notifyItemChanged(this.untilDateRow);
                break;
            case 4:
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(1);
                int monthOfYear = calendar.get(2);
                int dayOfMonth = calendar.get(5);
                try {
                    DatePickerDialog dialog = new DatePickerDialog(getParentActivity(), new DatePickerDialog.OnDateSetListener() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda19
                        @Override // android.app.DatePickerDialog.OnDateSetListener
                        public final void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
                            ChatRightsEditActivity.this.m2126lambda$createView$2$orgtelegramuiChatRightsEditActivity(datePicker, i, i2, i3);
                        }
                    }, year, monthOfYear, dayOfMonth);
                    final DatePicker datePicker = dialog.getDatePicker();
                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(System.currentTimeMillis());
                    date.set(11, date.getMinimum(11));
                    date.set(12, date.getMinimum(12));
                    date.set(13, date.getMinimum(13));
                    date.set(14, date.getMinimum(14));
                    datePicker.setMinDate(date.getTimeInMillis());
                    date.setTimeInMillis(System.currentTimeMillis() + 31536000000L);
                    date.set(11, date.getMaximum(11));
                    date.set(12, date.getMaximum(12));
                    date.set(13, date.getMaximum(13));
                    date.set(14, date.getMaximum(14));
                    datePicker.setMaxDate(date.getTimeInMillis());
                    dialog.setButton(-1, LocaleController.getString("Set", R.string.Set), dialog);
                    dialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), ChatRightsEditActivity$$ExternalSyntheticLambda1.INSTANCE);
                    if (Build.VERSION.SDK_INT >= 21) {
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda2
                            @Override // android.content.DialogInterface.OnShowListener
                            public final void onShow(DialogInterface dialogInterface) {
                                ChatRightsEditActivity.lambda$createView$4(datePicker, dialogInterface);
                            }
                        });
                    }
                    showDialog(dialog);
                    break;
                } catch (Exception e) {
                    FileLog.e(e);
                    break;
                }
        }
        builder.getDismissRunnable().run();
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2126lambda$createView$2$orgtelegramuiChatRightsEditActivity(DatePicker view1, int year1, int month, int dayOfMonth1) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.clear();
        calendar1.set(year1, month, dayOfMonth1);
        final int time = (int) (calendar1.getTime().getTime() / 1000);
        try {
            TimePickerDialog dialog13 = new TimePickerDialog(getParentActivity(), new TimePickerDialog.OnTimeSetListener() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda20
                @Override // android.app.TimePickerDialog.OnTimeSetListener
                public final void onTimeSet(TimePicker timePicker, int i, int i2) {
                    ChatRightsEditActivity.this.m2125lambda$createView$0$orgtelegramuiChatRightsEditActivity(time, timePicker, i, i2);
                }
            }, 0, 0, true);
            dialog13.setButton(-1, LocaleController.getString("Set", R.string.Set), dialog13);
            dialog13.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), ChatRightsEditActivity$$ExternalSyntheticLambda26.INSTANCE);
            showDialog(dialog13);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2125lambda$createView$0$orgtelegramuiChatRightsEditActivity(int time, TimePicker view11, int hourOfDay, int minute) {
        this.bannedRights.until_date = (hourOfDay * 3600) + time + (minute * 60);
        this.listViewAdapter.notifyItemChanged(this.untilDateRow);
    }

    public static /* synthetic */ void lambda$createView$1(DialogInterface dialog131, int which) {
    }

    public static /* synthetic */ void lambda$createView$3(DialogInterface dialog1, int which) {
    }

    public static /* synthetic */ void lambda$createView$4(DatePicker datePicker, DialogInterface dialog12) {
        int count = datePicker.getChildCount();
        for (int b = 0; b < count; b++) {
            View child = datePicker.getChildAt(b);
            ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
            layoutParams.width = -1;
            child.setLayoutParams(layoutParams);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    private boolean isDefaultAdminRights() {
        return (this.adminRights.change_info && this.adminRights.delete_messages && this.adminRights.ban_users && this.adminRights.invite_users && this.adminRights.pin_messages && this.adminRights.manage_call && !this.adminRights.add_admins && !this.adminRights.anonymous) || (!this.adminRights.change_info && !this.adminRights.delete_messages && !this.adminRights.ban_users && !this.adminRights.invite_users && !this.adminRights.pin_messages && !this.adminRights.manage_call && !this.adminRights.add_admins && !this.adminRights.anonymous);
    }

    private boolean hasAllAdminRights() {
        return this.isChannel ? this.adminRights.change_info && this.adminRights.post_messages && this.adminRights.edit_messages && this.adminRights.delete_messages && this.adminRights.invite_users && this.adminRights.add_admins && this.adminRights.manage_call : this.adminRights.change_info && this.adminRights.delete_messages && this.adminRights.ban_users && this.adminRights.invite_users && this.adminRights.pin_messages && this.adminRights.add_admins && this.adminRights.manage_call;
    }

    /* renamed from: initTransfer */
    public void m2136lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(final TLRPC.InputCheckPasswordSRP srp, final TwoStepVerificationActivity passwordFragment) {
        if (getParentActivity() == null) {
            return;
        }
        if (srp != null && !ChatObject.isChannel(this.currentChat)) {
            MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.LongCallback() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda13
                @Override // org.telegram.messenger.MessagesStorage.LongCallback
                public final void run(long j) {
                    ChatRightsEditActivity.this.m2135lambda$initTransfer$7$orgtelegramuiChatRightsEditActivity(srp, passwordFragment, j);
                }
            });
            return;
        }
        final TLRPC.TL_channels_editCreator req = new TLRPC.TL_channels_editCreator();
        if (ChatObject.isChannel(this.currentChat)) {
            req.channel = new TLRPC.TL_inputChannel();
            req.channel.channel_id = this.currentChat.id;
            req.channel.access_hash = this.currentChat.access_hash;
        } else {
            req.channel = new TLRPC.TL_inputChannelEmpty();
        }
        req.password = srp != null ? srp : new TLRPC.TL_inputCheckPasswordEmpty();
        req.user_id = getMessagesController().getInputUser(this.currentUser);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda14
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatRightsEditActivity.this.m2134lambda$initTransfer$14$orgtelegramuiChatRightsEditActivity(srp, passwordFragment, req, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$initTransfer$7$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2135lambda$initTransfer$7$orgtelegramuiChatRightsEditActivity(TLRPC.InputCheckPasswordSRP srp, TwoStepVerificationActivity passwordFragment, long param) {
        if (param != 0) {
            this.chatId = param;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(param));
            m2136lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(srp, passwordFragment);
        }
    }

    /* renamed from: lambda$initTransfer$14$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2134lambda$initTransfer$14$orgtelegramuiChatRightsEditActivity(final TLRPC.InputCheckPasswordSRP srp, final TwoStepVerificationActivity passwordFragment, final TLRPC.TL_channels_editCreator req, TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                ChatRightsEditActivity.this.m2133lambda$initTransfer$13$orgtelegramuiChatRightsEditActivity(error, srp, passwordFragment, req);
            }
        });
    }

    /* renamed from: lambda$initTransfer$13$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2133lambda$initTransfer$13$orgtelegramuiChatRightsEditActivity(TLRPC.TL_error error, TLRPC.InputCheckPasswordSRP srp, final TwoStepVerificationActivity passwordFragment, TLRPC.TL_channels_editCreator req) {
        if (error == null) {
            if (srp != null) {
                this.delegate.didChangeOwner(this.currentUser);
                removeSelfFromStack();
                passwordFragment.needHideProgress();
                passwordFragment.finishFragment();
            }
        } else if (getParentActivity() == null) {
        } else {
            if ("PASSWORD_HASH_INVALID".equals(error.text)) {
                if (srp == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    if (this.isChannel) {
                        builder.setTitle(LocaleController.getString("EditAdminChannelTransfer", R.string.EditAdminChannelTransfer));
                    } else {
                        builder.setTitle(LocaleController.getString("EditAdminGroupTransfer", R.string.EditAdminGroupTransfer));
                    }
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferReadyAlertText", R.string.EditAdminTransferReadyAlertText, this.currentChat.title, UserObject.getFirstName(this.currentUser))));
                    builder.setPositiveButton(LocaleController.getString("EditAdminTransferChangeOwner", R.string.EditAdminTransferChangeOwner), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda24
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ChatRightsEditActivity.this.m2137lambda$initTransfer$9$orgtelegramuiChatRightsEditActivity(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                    return;
                }
                return;
            }
            if (!"PASSWORD_MISSING".equals(error.text) && !error.text.startsWith("PASSWORD_TOO_FRESH_")) {
                if (!error.text.startsWith("SESSION_TOO_FRESH_")) {
                    if ("SRP_ID_INVALID".equals(error.text)) {
                        TLRPC.TL_account_getPassword getPasswordReq = new TLRPC.TL_account_getPassword();
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(getPasswordReq, new RequestDelegate() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda15
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                ChatRightsEditActivity.this.m2132lambda$initTransfer$12$orgtelegramuiChatRightsEditActivity(passwordFragment, tLObject, tL_error);
                            }
                        }, 8);
                        return;
                    } else if (error.text.equals("CHANNELS_TOO_MUCH")) {
                        if (getParentActivity() == null || AccountInstance.getInstance(this.currentAccount).getUserConfig().isPremium()) {
                            presentFragment(new TooManyCommunitiesActivity(1));
                            return;
                        } else {
                            showDialog(new LimitReachedBottomSheet(this, getParentActivity(), 5, this.currentAccount));
                            return;
                        }
                    } else {
                        if (passwordFragment != null) {
                            passwordFragment.needHideProgress();
                            passwordFragment.finishFragment();
                        }
                        AlertsCreator.showAddUserAlert(error.text, this, this.isChannel, req);
                        return;
                    }
                }
            }
            if (passwordFragment != null) {
                passwordFragment.needHideProgress();
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
            builder2.setTitle(LocaleController.getString("EditAdminTransferAlertTitle", R.string.EditAdminTransferAlertTitle));
            LinearLayout linearLayout = new LinearLayout(getParentActivity());
            linearLayout.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(24.0f), 0);
            linearLayout.setOrientation(1);
            builder2.setView(linearLayout);
            TextView messageTextView = new TextView(getParentActivity());
            messageTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            messageTextView.setTextSize(1, 16.0f);
            messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            if (this.isChannel) {
                messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EditChannelAdminTransferAlertText", R.string.EditChannelAdminTransferAlertText, UserObject.getFirstName(this.currentUser))));
            } else {
                messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EditAdminTransferAlertText", R.string.EditAdminTransferAlertText, UserObject.getFirstName(this.currentUser))));
            }
            linearLayout.addView(messageTextView, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout2 = new LinearLayout(getParentActivity());
            linearLayout2.setOrientation(0);
            linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
            ImageView dotImageView = new ImageView(getParentActivity());
            dotImageView.setImageResource(R.drawable.list_circle);
            dotImageView.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
            dotImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlack), PorterDuff.Mode.MULTIPLY));
            TextView messageTextView2 = new TextView(getParentActivity());
            messageTextView2.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            messageTextView2.setTextSize(1, 16.0f);
            messageTextView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            messageTextView2.setText(AndroidUtilities.replaceTags(LocaleController.getString("EditAdminTransferAlertText1", R.string.EditAdminTransferAlertText1)));
            if (LocaleController.isRTL) {
                linearLayout2.addView(messageTextView2, LayoutHelper.createLinear(-1, -2));
                linearLayout2.addView(dotImageView, LayoutHelper.createLinear(-2, -2, 5));
            } else {
                linearLayout2.addView(dotImageView, LayoutHelper.createLinear(-2, -2));
                linearLayout2.addView(messageTextView2, LayoutHelper.createLinear(-1, -2));
            }
            LinearLayout linearLayout22 = new LinearLayout(getParentActivity());
            linearLayout22.setOrientation(0);
            linearLayout.addView(linearLayout22, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
            ImageView dotImageView2 = new ImageView(getParentActivity());
            dotImageView2.setImageResource(R.drawable.list_circle);
            dotImageView2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(11.0f) : 0, AndroidUtilities.dp(9.0f), LocaleController.isRTL ? 0 : AndroidUtilities.dp(11.0f), 0);
            dotImageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextBlack), PorterDuff.Mode.MULTIPLY));
            TextView messageTextView3 = new TextView(getParentActivity());
            messageTextView3.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            messageTextView3.setTextSize(1, 16.0f);
            messageTextView3.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            messageTextView3.setText(AndroidUtilities.replaceTags(LocaleController.getString("EditAdminTransferAlertText2", R.string.EditAdminTransferAlertText2)));
            if (LocaleController.isRTL) {
                linearLayout22.addView(messageTextView3, LayoutHelper.createLinear(-1, -2));
                linearLayout22.addView(dotImageView2, LayoutHelper.createLinear(-2, -2, 5));
            } else {
                linearLayout22.addView(dotImageView2, LayoutHelper.createLinear(-2, -2));
                linearLayout22.addView(messageTextView3, LayoutHelper.createLinear(-1, -2));
            }
            if ("PASSWORD_MISSING".equals(error.text)) {
                builder2.setPositiveButton(LocaleController.getString("EditAdminTransferSetPassword", R.string.EditAdminTransferSetPassword), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda23
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ChatRightsEditActivity.this.m2130lambda$initTransfer$10$orgtelegramuiChatRightsEditActivity(dialogInterface, i);
                    }
                });
                builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            } else {
                TextView messageTextView4 = new TextView(getParentActivity());
                messageTextView4.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                messageTextView4.setTextSize(1, 16.0f);
                messageTextView4.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                messageTextView4.setText(LocaleController.getString("EditAdminTransferAlertText3", R.string.EditAdminTransferAlertText3));
                linearLayout.addView(messageTextView4, LayoutHelper.createLinear(-1, -2, 0.0f, 11.0f, 0.0f, 0.0f));
                builder2.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
            }
            showDialog(builder2.create());
        }
    }

    /* renamed from: lambda$initTransfer$9$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2137lambda$initTransfer$9$orgtelegramuiChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        final TwoStepVerificationActivity fragment = new TwoStepVerificationActivity();
        fragment.setDelegate(new TwoStepVerificationActivity.TwoStepVerificationActivityDelegate() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda18
            @Override // org.telegram.ui.TwoStepVerificationActivity.TwoStepVerificationActivityDelegate
            public final void didEnterPassword(TLRPC.InputCheckPasswordSRP inputCheckPasswordSRP) {
                ChatRightsEditActivity.this.m2136lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(fragment, inputCheckPasswordSRP);
            }
        });
        presentFragment(fragment);
    }

    /* renamed from: lambda$initTransfer$10$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2130lambda$initTransfer$10$orgtelegramuiChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        presentFragment(new TwoStepVerificationSetupActivity(6, null));
    }

    /* renamed from: lambda$initTransfer$12$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2132lambda$initTransfer$12$orgtelegramuiChatRightsEditActivity(final TwoStepVerificationActivity passwordFragment, final TLObject response2, final TLRPC.TL_error error2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ChatRightsEditActivity.this.m2131lambda$initTransfer$11$orgtelegramuiChatRightsEditActivity(error2, response2, passwordFragment);
            }
        });
    }

    /* renamed from: lambda$initTransfer$11$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2131lambda$initTransfer$11$orgtelegramuiChatRightsEditActivity(TLRPC.TL_error error2, TLObject response2, TwoStepVerificationActivity passwordFragment) {
        if (error2 == null) {
            TLRPC.TL_account_password currentPassword = (TLRPC.TL_account_password) response2;
            passwordFragment.setCurrentPasswordInfo(null, currentPassword);
            TwoStepVerificationActivity.initPasswordNewAlgo(currentPassword);
            m2136lambda$initTransfer$8$orgtelegramuiChatRightsEditActivity(passwordFragment.getNewSrpPassword(), passwordFragment);
        }
    }

    private void updateRows(boolean update) {
        int i;
        int transferOwnerShadowRowPrev = Math.min(this.transferOwnerShadowRow, this.transferOwnerRow);
        this.manageRow = -1;
        this.changeInfoRow = -1;
        this.postMessagesRow = -1;
        this.editMesagesRow = -1;
        this.deleteMessagesRow = -1;
        this.addAdminsRow = -1;
        this.anonymousRow = -1;
        this.banUsersRow = -1;
        this.addUsersRow = -1;
        this.pinMessagesRow = -1;
        this.rightsShadowRow = -1;
        this.removeAdminRow = -1;
        this.removeAdminShadowRow = -1;
        this.cantEditInfoRow = -1;
        this.transferOwnerShadowRow = -1;
        this.transferOwnerRow = -1;
        this.rankHeaderRow = -1;
        this.rankRow = -1;
        this.rankInfoRow = -1;
        this.sendMessagesRow = -1;
        this.sendMediaRow = -1;
        this.sendStickersRow = -1;
        this.sendPollsRow = -1;
        this.embedLinksRow = -1;
        this.startVoiceChatRow = -1;
        this.untilSectionRow = -1;
        this.untilDateRow = -1;
        this.addBotButtonRow = -1;
        this.rowCount = 3;
        this.permissionsStartRow = 3;
        int i2 = this.currentType;
        if (i2 == 0 || i2 == 2) {
            if (this.isChannel) {
                int i3 = 3 + 1;
                this.rowCount = i3;
                this.changeInfoRow = 3;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.postMessagesRow = i3;
                int i5 = i4 + 1;
                this.rowCount = i5;
                this.editMesagesRow = i4;
                int i6 = i5 + 1;
                this.rowCount = i6;
                this.deleteMessagesRow = i5;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.addUsersRow = i6;
                int i8 = i7 + 1;
                this.rowCount = i8;
                this.startVoiceChatRow = i7;
                this.rowCount = i8 + 1;
                this.addAdminsRow = i8;
            } else {
                if (i2 == 2) {
                    this.rowCount = 3 + 1;
                    this.manageRow = 3;
                }
                int i9 = this.rowCount;
                int i10 = i9 + 1;
                this.rowCount = i10;
                this.changeInfoRow = i9;
                int i11 = i10 + 1;
                this.rowCount = i11;
                this.deleteMessagesRow = i10;
                int i12 = i11 + 1;
                this.rowCount = i12;
                this.banUsersRow = i11;
                int i13 = i12 + 1;
                this.rowCount = i13;
                this.addUsersRow = i12;
                int i14 = i13 + 1;
                this.rowCount = i14;
                this.pinMessagesRow = i13;
                int i15 = i14 + 1;
                this.rowCount = i15;
                this.startVoiceChatRow = i14;
                int i16 = i15 + 1;
                this.rowCount = i16;
                this.addAdminsRow = i15;
                this.rowCount = i16 + 1;
                this.anonymousRow = i16;
            }
        } else if (i2 == 1) {
            int i17 = 3 + 1;
            this.rowCount = i17;
            this.sendMessagesRow = 3;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.sendMediaRow = i17;
            int i19 = i18 + 1;
            this.rowCount = i19;
            this.sendStickersRow = i18;
            int i20 = i19 + 1;
            this.rowCount = i20;
            this.sendPollsRow = i19;
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.embedLinksRow = i20;
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.addUsersRow = i21;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.pinMessagesRow = i22;
            int i24 = i23 + 1;
            this.rowCount = i24;
            this.changeInfoRow = i23;
            int i25 = i24 + 1;
            this.rowCount = i25;
            this.untilSectionRow = i24;
            this.rowCount = i25 + 1;
            this.untilDateRow = i25;
        }
        int i26 = this.rowCount;
        this.permissionsEndRow = i26;
        if (this.canEdit) {
            if (!this.isChannel && (i2 == 0 || (i2 == 2 && this.asAdmin))) {
                int i27 = i26 + 1;
                this.rowCount = i27;
                this.rightsShadowRow = i26;
                int i28 = i27 + 1;
                this.rowCount = i28;
                this.rankHeaderRow = i27;
                int i29 = i28 + 1;
                this.rowCount = i29;
                this.rankRow = i28;
                this.rowCount = i29 + 1;
                this.rankInfoRow = i29;
            }
            TLRPC.Chat chat = this.currentChat;
            if (chat != null && chat.creator && this.currentType == 0 && hasAllAdminRights() && !this.currentUser.bot) {
                int i30 = this.rightsShadowRow;
                if (i30 == -1) {
                    int i31 = this.rowCount;
                    this.rowCount = i31 + 1;
                    this.transferOwnerShadowRow = i31;
                }
                int i32 = this.rowCount;
                int i33 = i32 + 1;
                this.rowCount = i33;
                this.transferOwnerRow = i32;
                if (i30 != -1) {
                    this.rowCount = i33 + 1;
                    this.transferOwnerShadowRow = i33;
                }
            }
            if (this.initialIsSet) {
                if (this.rightsShadowRow == -1) {
                    int i34 = this.rowCount;
                    this.rowCount = i34 + 1;
                    this.rightsShadowRow = i34;
                }
                int i35 = this.rowCount;
                int i36 = i35 + 1;
                this.rowCount = i36;
                this.removeAdminRow = i35;
                this.rowCount = i36 + 1;
                this.removeAdminShadowRow = i36;
            }
        } else if (i2 == 0) {
            if (!this.isChannel && (!this.currentRank.isEmpty() || (this.currentChat.creator && UserObject.isUserSelf(this.currentUser)))) {
                int i37 = this.rowCount;
                int i38 = i37 + 1;
                this.rowCount = i38;
                this.rightsShadowRow = i37;
                int i39 = i38 + 1;
                this.rowCount = i39;
                this.rankHeaderRow = i38;
                this.rowCount = i39 + 1;
                this.rankRow = i39;
                if (this.currentChat.creator && UserObject.isUserSelf(this.currentUser)) {
                    int i40 = this.rowCount;
                    this.rowCount = i40 + 1;
                    this.rankInfoRow = i40;
                } else {
                    int i41 = this.rowCount;
                    this.rowCount = i41 + 1;
                    this.cantEditInfoRow = i41;
                }
            } else {
                int i42 = this.rowCount;
                this.rowCount = i42 + 1;
                this.cantEditInfoRow = i42;
            }
        } else {
            this.rowCount = i26 + 1;
            this.rightsShadowRow = i26;
        }
        if (this.currentType == 2) {
            int i43 = this.rowCount;
            this.rowCount = i43 + 1;
            this.addBotButtonRow = i43;
        }
        if (update) {
            if (transferOwnerShadowRowPrev == -1 && (i = this.transferOwnerShadowRow) != -1) {
                this.listViewAdapter.notifyItemRangeInserted(Math.min(i, this.transferOwnerRow), 2);
            } else if (transferOwnerShadowRowPrev != -1 && this.transferOwnerShadowRow == -1) {
                this.listViewAdapter.notifyItemRangeRemoved(transferOwnerShadowRowPrev, 2);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:15:0x002d, code lost:
        if (r0.codePointCount(0, r0.length()) <= 16) goto L16;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x003b, code lost:
        if (isDefaultAdminRights() == false) goto L22;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onDonePressed() {
        /*
            Method dump skipped, instructions count: 555
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatRightsEditActivity.onDonePressed():void");
    }

    /* renamed from: lambda$onDonePressed$15$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2138lambda$onDonePressed$15$orgtelegramuiChatRightsEditActivity(long param) {
        if (param != 0) {
            this.chatId = param;
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(param));
            onDonePressed();
        }
    }

    /* renamed from: lambda$onDonePressed$16$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2139lambda$onDonePressed$16$orgtelegramuiChatRightsEditActivity() {
        ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
        if (chatRightsEditActivityDelegate != null) {
            chatRightsEditActivityDelegate.didSetRights((this.adminRights.change_info || this.adminRights.post_messages || this.adminRights.edit_messages || this.adminRights.delete_messages || this.adminRights.ban_users || this.adminRights.invite_users || this.adminRights.pin_messages || this.adminRights.add_admins || this.adminRights.anonymous || this.adminRights.manage_call || this.adminRights.other) ? 1 : 0, this.adminRights, this.bannedRights, this.currentRank);
            finishFragment();
        }
    }

    /* renamed from: lambda$onDonePressed$17$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ boolean m2140lambda$onDonePressed$17$orgtelegramuiChatRightsEditActivity(TLRPC.TL_error err) {
        setLoading(false);
        return true;
    }

    /* renamed from: lambda$onDonePressed$21$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2144lambda$onDonePressed$21$orgtelegramuiChatRightsEditActivity(DialogInterface di, int i) {
        setLoading(true);
        Runnable onFinish = new Runnable() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                ChatRightsEditActivity.this.m2141lambda$onDonePressed$18$orgtelegramuiChatRightsEditActivity();
            }
        };
        if (this.asAdmin || this.initialAsAdmin) {
            getMessagesController().setUserAdminRole(this.currentChat.id, this.currentUser, this.asAdmin ? this.adminRights : emptyAdminRights(false), this.currentRank, false, this, this.isAddingNew, this.asAdmin, this.botHash, onFinish, new MessagesController.ErrorDelegate() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda9
                @Override // org.telegram.messenger.MessagesController.ErrorDelegate
                public final boolean run(TLRPC.TL_error tL_error) {
                    return ChatRightsEditActivity.this.m2142lambda$onDonePressed$19$orgtelegramuiChatRightsEditActivity(tL_error);
                }
            });
        } else {
            getMessagesController().addUserToChat(this.currentChat.id, this.currentUser, 0, this.botHash, this, true, onFinish, new MessagesController.ErrorDelegate() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda10
                @Override // org.telegram.messenger.MessagesController.ErrorDelegate
                public final boolean run(TLRPC.TL_error tL_error) {
                    return ChatRightsEditActivity.this.m2143lambda$onDonePressed$20$orgtelegramuiChatRightsEditActivity(tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$onDonePressed$18$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2141lambda$onDonePressed$18$orgtelegramuiChatRightsEditActivity() {
        ChatRightsEditActivityDelegate chatRightsEditActivityDelegate = this.delegate;
        if (chatRightsEditActivityDelegate != null) {
            chatRightsEditActivityDelegate.didSetRights(0, this.asAdmin ? this.adminRights : null, null, this.currentRank);
        }
        this.closingKeyboardAfterFinish = true;
        Bundle args1 = new Bundle();
        args1.putBoolean("scrollToTopOnResume", true);
        args1.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.currentChat.id);
        if (!getMessagesController().checkCanOpenChat(args1, this)) {
            setLoading(false);
            return;
        }
        ChatActivity chatActivity = new ChatActivity(args1);
        presentFragment(chatActivity, true);
        if (BulletinFactory.canShowBulletin(chatActivity)) {
            boolean z = this.isAddingNew;
            if (z && this.asAdmin) {
                BulletinFactory.createAddedAsAdminBulletin(chatActivity, this.currentUser.first_name).show();
            } else if (!z && !this.initialAsAdmin && this.asAdmin) {
                BulletinFactory.createPromoteToAdminBulletin(chatActivity, this.currentUser.first_name).show();
            }
        }
    }

    /* renamed from: lambda$onDonePressed$19$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ boolean m2142lambda$onDonePressed$19$orgtelegramuiChatRightsEditActivity(TLRPC.TL_error err) {
        setLoading(false);
        return true;
    }

    /* renamed from: lambda$onDonePressed$20$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ boolean m2143lambda$onDonePressed$20$orgtelegramuiChatRightsEditActivity(TLRPC.TL_error err) {
        setLoading(false);
        return true;
    }

    public void setLoading(boolean enable) {
        ValueAnimator valueAnimator = this.doneDrawableAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.loading = !enable ? 1 : 0;
        this.actionBar.getBackButton().setEnabled(!enable);
        CrossfadeDrawable crossfadeDrawable = this.doneDrawable;
        if (crossfadeDrawable != null) {
            float[] fArr = new float[2];
            fArr[0] = crossfadeDrawable.getProgress();
            fArr[1] = enable ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.doneDrawableAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ChatRightsEditActivity.this.m2145lambda$setLoading$22$orgtelegramuiChatRightsEditActivity(valueAnimator2);
                }
            });
            this.doneDrawableAnimator.setDuration(Math.abs(this.doneDrawable.getProgress() - (enable ? 1.0f : 0.0f)) * 150.0f);
            this.doneDrawableAnimator.start();
        }
    }

    /* renamed from: lambda$setLoading$22$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2145lambda$setLoading$22$orgtelegramuiChatRightsEditActivity(ValueAnimator a) {
        this.doneDrawable.setProgress(((Float) a.getAnimatedValue()).floatValue());
        this.doneDrawable.invalidateSelf();
    }

    public void setDelegate(ChatRightsEditActivityDelegate channelRightsEditActivityDelegate) {
        this.delegate = channelRightsEditActivityDelegate;
    }

    public boolean checkDiscard() {
        boolean changed;
        int i = this.currentType;
        if (i == 2) {
            return true;
        }
        if (i == 1) {
            String newBannedRights = ChatObject.getBannedRightsString(this.bannedRights);
            changed = !this.currentBannedRights.equals(newBannedRights);
        } else {
            changed = !this.initialRank.equals(this.currentRank);
        }
        if (!changed) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.chatId));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("UserRestrictionsApplyChangesText", R.string.UserRestrictionsApplyChangesText, chat.title)));
        builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda21
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                ChatRightsEditActivity.this.m2123lambda$checkDiscard$23$orgtelegramuiChatRightsEditActivity(dialogInterface, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda22
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i2) {
                ChatRightsEditActivity.this.m2124lambda$checkDiscard$24$orgtelegramuiChatRightsEditActivity(dialogInterface, i2);
            }
        });
        showDialog(builder.create());
        return false;
    }

    /* renamed from: lambda$checkDiscard$23$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2123lambda$checkDiscard$23$orgtelegramuiChatRightsEditActivity(DialogInterface dialogInterface, int i) {
        onDonePressed();
    }

    /* renamed from: lambda$checkDiscard$24$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2124lambda$checkDiscard$24$orgtelegramuiChatRightsEditActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    public void setTextLeft(View cell) {
        if (cell instanceof HeaderCell) {
            HeaderCell headerCell = (HeaderCell) cell;
            String str = this.currentRank;
            int left = 16 - (str != null ? str.codePointCount(0, str.length()) : 0);
            if (left <= 4.8f) {
                headerCell.setText2(String.format("%d", Integer.valueOf(left)));
                SimpleTextView textView = headerCell.getTextView2();
                String key = left < 0 ? Theme.key_windowBackgroundWhiteRedText5 : Theme.key_windowBackgroundWhiteGrayText3;
                textView.setTextColor(Theme.getColor(key));
                textView.setTag(key);
                return;
            }
            headerCell.setText2("");
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        return checkDiscard();
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private boolean ignoreTextChange;
        private Context mContext;
        private final int VIEW_TYPE_USER_CELL = 0;
        private final int VIEW_TYPE_INFO_CELL = 1;
        private final int VIEW_TYPE_TRANSFER_CELL = 2;
        private final int VIEW_TYPE_HEADER_CELL = 3;
        private final int VIEW_TYPE_SWITCH_CELL = 4;
        private final int VIEW_TYPE_SHADOW_CELL = 5;
        private final int VIEW_TYPE_UNTIL_DATE_CELL = 6;
        private final int VIEW_TYPE_RANK_CELL = 7;
        private final int VIEW_TYPE_ADD_BOT_CELL = 8;

        public ListAdapter(Context context) {
            ChatRightsEditActivity.this = r4;
            if (r4.currentType == 2) {
                setHasStableIds(true);
            }
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int position) {
            if (ChatRightsEditActivity.this.currentType == 2) {
                if (position == ChatRightsEditActivity.this.manageRow) {
                    return 1L;
                }
                if (position == ChatRightsEditActivity.this.changeInfoRow) {
                    return 2L;
                }
                if (position == ChatRightsEditActivity.this.postMessagesRow) {
                    return 3L;
                }
                if (position == ChatRightsEditActivity.this.editMesagesRow) {
                    return 4L;
                }
                if (position == ChatRightsEditActivity.this.deleteMessagesRow) {
                    return 5L;
                }
                if (position == ChatRightsEditActivity.this.addAdminsRow) {
                    return 6L;
                }
                if (position == ChatRightsEditActivity.this.anonymousRow) {
                    return 7L;
                }
                if (position == ChatRightsEditActivity.this.banUsersRow) {
                    return 8L;
                }
                if (position == ChatRightsEditActivity.this.addUsersRow) {
                    return 9L;
                }
                if (position == ChatRightsEditActivity.this.pinMessagesRow) {
                    return 10L;
                }
                if (position == ChatRightsEditActivity.this.rightsShadowRow) {
                    return 11L;
                }
                if (position == ChatRightsEditActivity.this.removeAdminRow) {
                    return 12L;
                }
                if (position == ChatRightsEditActivity.this.removeAdminShadowRow) {
                    return 13L;
                }
                if (position == ChatRightsEditActivity.this.cantEditInfoRow) {
                    return 14L;
                }
                if (position == ChatRightsEditActivity.this.transferOwnerShadowRow) {
                    return 15L;
                }
                if (position == ChatRightsEditActivity.this.transferOwnerRow) {
                    return 16L;
                }
                if (position == ChatRightsEditActivity.this.rankHeaderRow) {
                    return 17L;
                }
                if (position == ChatRightsEditActivity.this.rankRow) {
                    return 18L;
                }
                if (position == ChatRightsEditActivity.this.rankInfoRow) {
                    return 19L;
                }
                if (position == ChatRightsEditActivity.this.sendMessagesRow) {
                    return 20L;
                }
                if (position == ChatRightsEditActivity.this.sendMediaRow) {
                    return 21L;
                }
                if (position == ChatRightsEditActivity.this.sendStickersRow) {
                    return 22L;
                }
                if (position == ChatRightsEditActivity.this.sendPollsRow) {
                    return 23L;
                }
                if (position == ChatRightsEditActivity.this.embedLinksRow) {
                    return 24L;
                }
                if (position == ChatRightsEditActivity.this.startVoiceChatRow) {
                    return 25L;
                }
                if (position == ChatRightsEditActivity.this.untilSectionRow) {
                    return 26L;
                }
                if (position == ChatRightsEditActivity.this.untilDateRow) {
                    return 27L;
                }
                return position == ChatRightsEditActivity.this.addBotButtonRow ? 28L : 0L;
            }
            return super.getItemId(position);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (!ChatRightsEditActivity.this.currentChat.creator || !((ChatRightsEditActivity.this.currentType == 0 || (ChatRightsEditActivity.this.currentType == 2 && ChatRightsEditActivity.this.asAdmin)) && type == 4 && holder.getAdapterPosition() == ChatRightsEditActivity.this.anonymousRow)) {
                if (!ChatRightsEditActivity.this.canEdit) {
                    return false;
                }
                if ((ChatRightsEditActivity.this.currentType == 0 || ChatRightsEditActivity.this.currentType == 2) && type == 4) {
                    int position = holder.getAdapterPosition();
                    if (position == ChatRightsEditActivity.this.manageRow) {
                        if (ChatRightsEditActivity.this.myAdminRights.add_admins) {
                            return true;
                        }
                        return ChatRightsEditActivity.this.currentChat != null && ChatRightsEditActivity.this.currentChat.creator;
                    } else if (ChatRightsEditActivity.this.currentType == 2 && !ChatRightsEditActivity.this.asAdmin) {
                        return false;
                    } else {
                        if (position == ChatRightsEditActivity.this.changeInfoRow) {
                            return ChatRightsEditActivity.this.myAdminRights.change_info && (ChatRightsEditActivity.this.defaultBannedRights == null || ChatRightsEditActivity.this.defaultBannedRights.change_info);
                        } else if (position == ChatRightsEditActivity.this.postMessagesRow) {
                            return ChatRightsEditActivity.this.myAdminRights.post_messages;
                        } else {
                            if (position == ChatRightsEditActivity.this.editMesagesRow) {
                                return ChatRightsEditActivity.this.myAdminRights.edit_messages;
                            }
                            if (position == ChatRightsEditActivity.this.deleteMessagesRow) {
                                return ChatRightsEditActivity.this.myAdminRights.delete_messages;
                            }
                            if (position == ChatRightsEditActivity.this.startVoiceChatRow) {
                                return ChatRightsEditActivity.this.myAdminRights.manage_call;
                            }
                            if (position == ChatRightsEditActivity.this.addAdminsRow) {
                                return ChatRightsEditActivity.this.myAdminRights.add_admins;
                            }
                            if (position == ChatRightsEditActivity.this.anonymousRow) {
                                return ChatRightsEditActivity.this.myAdminRights.anonymous;
                            }
                            if (position == ChatRightsEditActivity.this.banUsersRow) {
                                return ChatRightsEditActivity.this.myAdminRights.ban_users;
                            }
                            if (position == ChatRightsEditActivity.this.addUsersRow) {
                                return ChatRightsEditActivity.this.myAdminRights.invite_users;
                            }
                            if (position == ChatRightsEditActivity.this.pinMessagesRow) {
                                return ChatRightsEditActivity.this.myAdminRights.pin_messages && (ChatRightsEditActivity.this.defaultBannedRights == null || ChatRightsEditActivity.this.defaultBannedRights.pin_messages);
                            }
                        }
                    }
                }
                return (type == 3 || type == 1 || type == 5 || type == 8) ? false : true;
            }
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ChatRightsEditActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            String str;
            int i;
            switch (viewType) {
                case 0:
                    View view2 = new UserCell2(this.mContext, 4, 0);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 1:
                    View view3 = new TextInfoPrivacyCell(this.mContext);
                    view3.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    view = view3;
                    break;
                case 2:
                default:
                    View textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = textSettingsCell;
                    break;
                case 3:
                    View headerCell = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, true);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = headerCell;
                    break;
                case 4:
                    View textCheckCell2 = new TextCheckCell2(this.mContext);
                    textCheckCell2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = textCheckCell2;
                    break;
                case 5:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 6:
                    View textDetailCell = new TextDetailCell(this.mContext);
                    textDetailCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = textDetailCell;
                    break;
                case 7:
                    PollEditTextCell cell = ChatRightsEditActivity.this.rankEditTextCell = new PollEditTextCell(this.mContext, null);
                    cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    cell.addTextWatcher(new TextWatcher() { // from class: org.telegram.ui.ChatRightsEditActivity.ListAdapter.1
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable s) {
                            if (ListAdapter.this.ignoreTextChange) {
                                return;
                            }
                            ChatRightsEditActivity.this.currentRank = s.toString();
                            RecyclerView.ViewHolder holder = ChatRightsEditActivity.this.listView.findViewHolderForAdapterPosition(ChatRightsEditActivity.this.rankHeaderRow);
                            if (holder != null) {
                                ChatRightsEditActivity.this.setTextLeft(holder.itemView);
                            }
                        }
                    });
                    view = cell;
                    break;
                case 8:
                    ChatRightsEditActivity.this.addBotButtonContainer = new FrameLayout(this.mContext);
                    ChatRightsEditActivity.this.addBotButtonContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    ChatRightsEditActivity.this.addBotButton = new FrameLayout(this.mContext);
                    ChatRightsEditActivity.this.addBotButtonText = new AnimatedTextView(this.mContext, true, false, false);
                    ChatRightsEditActivity.this.addBotButtonText.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    ChatRightsEditActivity.this.addBotButtonText.setTextColor(-1);
                    ChatRightsEditActivity.this.addBotButtonText.setTextSize(AndroidUtilities.dp(14.0f));
                    ChatRightsEditActivity.this.addBotButtonText.setGravity(17);
                    AnimatedTextView animatedTextView = ChatRightsEditActivity.this.addBotButtonText;
                    StringBuilder sb = new StringBuilder();
                    sb.append(LocaleController.getString("AddBotButton", R.string.AddBotButton));
                    sb.append(" ");
                    if (ChatRightsEditActivity.this.asAdmin) {
                        i = R.string.AddBotButtonAsAdmin;
                        str = "AddBotButtonAsAdmin";
                    } else {
                        i = R.string.AddBotButtonAsMember;
                        str = "AddBotButtonAsMember";
                    }
                    sb.append(LocaleController.getString(str, i));
                    animatedTextView.setText(sb.toString());
                    ChatRightsEditActivity.this.addBotButton.addView(ChatRightsEditActivity.this.addBotButtonText, LayoutHelper.createFrame(-2, -2, 17));
                    ChatRightsEditActivity.this.addBotButton.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
                    ChatRightsEditActivity.this.addBotButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatRightsEditActivity$ListAdapter$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view4) {
                            ChatRightsEditActivity.ListAdapter.this.m2147x1beb1a5a(view4);
                        }
                    });
                    ChatRightsEditActivity.this.addBotButtonContainer.addView(ChatRightsEditActivity.this.addBotButton, LayoutHelper.createFrame(-1, 48.0f, 119, 14.0f, 28.0f, 14.0f, 14.0f));
                    ChatRightsEditActivity.this.addBotButtonContainer.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    View bg = new View(this.mContext);
                    bg.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    ChatRightsEditActivity.this.addBotButtonContainer.setClipChildren(false);
                    ChatRightsEditActivity.this.addBotButtonContainer.setClipToPadding(false);
                    ChatRightsEditActivity.this.addBotButtonContainer.addView(bg, LayoutHelper.createFrame(-1, 800.0f, 87, 0.0f, 0.0f, 0.0f, -800.0f));
                    view = ChatRightsEditActivity.this.addBotButtonContainer;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-ChatRightsEditActivity$ListAdapter */
        public /* synthetic */ void m2147x1beb1a5a(View e) {
            ChatRightsEditActivity.this.onDonePressed();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String hint;
            String value;
            String hint2;
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    UserCell2 userCell2 = (UserCell2) holder.itemView;
                    String status = null;
                    if (ChatRightsEditActivity.this.currentType == 2) {
                        status = LocaleController.getString("Bot", R.string.Bot);
                    }
                    userCell2.setData(ChatRightsEditActivity.this.currentUser, null, status, 0);
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position != ChatRightsEditActivity.this.cantEditInfoRow) {
                        if (position == ChatRightsEditActivity.this.rankInfoRow) {
                            if (UserObject.isUserSelf(ChatRightsEditActivity.this.currentUser) && ChatRightsEditActivity.this.currentChat.creator) {
                                hint = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                            } else {
                                hint = LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin);
                            }
                            privacyCell.setText(LocaleController.formatString("EditAdminRankInfo", R.string.EditAdminRankInfo, hint));
                            return;
                        }
                        return;
                    }
                    privacyCell.setText(LocaleController.getString("EditAdminCantEdit", R.string.EditAdminCantEdit));
                    return;
                case 2:
                    TextSettingsCell actionCell = (TextSettingsCell) holder.itemView;
                    if (position != ChatRightsEditActivity.this.removeAdminRow) {
                        if (position == ChatRightsEditActivity.this.transferOwnerRow) {
                            actionCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                            actionCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                            if (ChatRightsEditActivity.this.isChannel) {
                                actionCell.setText(LocaleController.getString("EditAdminChannelTransfer", R.string.EditAdminChannelTransfer), false);
                                return;
                            } else {
                                actionCell.setText(LocaleController.getString("EditAdminGroupTransfer", R.string.EditAdminGroupTransfer), false);
                                return;
                            }
                        }
                        return;
                    }
                    actionCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
                    actionCell.setTag(Theme.key_windowBackgroundWhiteRedText5);
                    if (ChatRightsEditActivity.this.currentType != 0) {
                        if (ChatRightsEditActivity.this.currentType == 1) {
                            actionCell.setText(LocaleController.getString("UserRestrictionsBlock", R.string.UserRestrictionsBlock), false);
                            return;
                        }
                        return;
                    }
                    actionCell.setText(LocaleController.getString("EditAdminRemoveAdmin", R.string.EditAdminRemoveAdmin), false);
                    return;
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == 2) {
                        if (ChatRightsEditActivity.this.currentType != 2 && (ChatRightsEditActivity.this.currentUser == null || !ChatRightsEditActivity.this.currentUser.bot)) {
                            if (ChatRightsEditActivity.this.currentType != 0) {
                                if (ChatRightsEditActivity.this.currentType == 1) {
                                    headerCell.setText(LocaleController.getString("UserRestrictionsCanDo", R.string.UserRestrictionsCanDo));
                                    return;
                                }
                                return;
                            }
                            headerCell.setText(LocaleController.getString("EditAdminWhatCanDo", R.string.EditAdminWhatCanDo));
                            return;
                        }
                        headerCell.setText(LocaleController.getString("BotRestrictionsCanDo", R.string.BotRestrictionsCanDo));
                        return;
                    } else if (position == ChatRightsEditActivity.this.rankHeaderRow) {
                        headerCell.setText(LocaleController.getString("EditAdminRank", R.string.EditAdminRank));
                        return;
                    } else {
                        return;
                    }
                case 4:
                    TextCheckCell2 checkCell = (TextCheckCell2) holder.itemView;
                    boolean asAdminValue = ChatRightsEditActivity.this.currentType != 2 || ChatRightsEditActivity.this.asAdmin;
                    boolean isCreator = ChatRightsEditActivity.this.currentChat != null && ChatRightsEditActivity.this.currentChat.creator;
                    int i = ChatRightsEditActivity.this.manageRow;
                    int i2 = R.drawable.permission_locked;
                    if (position == i) {
                        checkCell.setTextAndCheck(LocaleController.getString("ManageGroup", R.string.ManageGroup), ChatRightsEditActivity.this.asAdmin, true);
                        if (ChatRightsEditActivity.this.myAdminRights.add_admins || isCreator) {
                            i2 = 0;
                        }
                        checkCell.setIcon(i2);
                    } else if (position == ChatRightsEditActivity.this.changeInfoRow) {
                        if (ChatRightsEditActivity.this.currentType == 0 || ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.isChannel) {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminChangeChannelInfo", R.string.EditAdminChangeChannelInfo), (asAdminValue && ChatRightsEditActivity.this.adminRights.change_info) || !ChatRightsEditActivity.this.defaultBannedRights.change_info, true);
                            } else {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminChangeGroupInfo", R.string.EditAdminChangeGroupInfo), (asAdminValue && ChatRightsEditActivity.this.adminRights.change_info) || !ChatRightsEditActivity.this.defaultBannedRights.change_info, true);
                            }
                            if (ChatRightsEditActivity.this.currentType == 2) {
                                if (ChatRightsEditActivity.this.myAdminRights.change_info || isCreator) {
                                    i2 = 0;
                                }
                                checkCell.setIcon(i2);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsChangeInfo", R.string.UserRestrictionsChangeInfo), !ChatRightsEditActivity.this.bannedRights.change_info && !ChatRightsEditActivity.this.defaultBannedRights.change_info, false);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.change_info) {
                                i2 = 0;
                            }
                            checkCell.setIcon(i2);
                        }
                    } else if (position == ChatRightsEditActivity.this.postMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminPostMessages", R.string.EditAdminPostMessages), asAdminValue && ChatRightsEditActivity.this.adminRights.post_messages, true);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.post_messages || isCreator) {
                                i2 = 0;
                            }
                            checkCell.setIcon(i2);
                        }
                    } else if (position == ChatRightsEditActivity.this.editMesagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminEditMessages", R.string.EditAdminEditMessages), asAdminValue && ChatRightsEditActivity.this.adminRights.edit_messages, true);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.edit_messages || isCreator) {
                                i2 = 0;
                            }
                            checkCell.setIcon(i2);
                        }
                    } else if (position == ChatRightsEditActivity.this.deleteMessagesRow) {
                        if (ChatRightsEditActivity.this.isChannel) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminDeleteMessages", R.string.EditAdminDeleteMessages), asAdminValue && ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        } else {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminGroupDeleteMessages", R.string.EditAdminGroupDeleteMessages), asAdminValue && ChatRightsEditActivity.this.adminRights.delete_messages, true);
                        }
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.delete_messages || isCreator) {
                                i2 = 0;
                            }
                            checkCell.setIcon(i2);
                        }
                    } else if (position == ChatRightsEditActivity.this.addAdminsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddAdmins", R.string.EditAdminAddAdmins), asAdminValue && ChatRightsEditActivity.this.adminRights.add_admins, ChatRightsEditActivity.this.anonymousRow != -1);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.add_admins || isCreator) {
                                i2 = 0;
                            }
                            checkCell.setIcon(i2);
                        }
                    } else if (position == ChatRightsEditActivity.this.anonymousRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminSendAnonymously", R.string.EditAdminSendAnonymously), asAdminValue && ChatRightsEditActivity.this.adminRights.anonymous, false);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.anonymous || isCreator) {
                                i2 = 0;
                            }
                            checkCell.setIcon(i2);
                        }
                    } else if (position == ChatRightsEditActivity.this.banUsersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("EditAdminBanUsers", R.string.EditAdminBanUsers), asAdminValue && ChatRightsEditActivity.this.adminRights.ban_users, true);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.ban_users || isCreator) {
                                i2 = 0;
                            }
                            checkCell.setIcon(i2);
                        }
                    } else if (position == ChatRightsEditActivity.this.startVoiceChatRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("StartVoipChatPermission", R.string.StartVoipChatPermission), asAdminValue && ChatRightsEditActivity.this.adminRights.manage_call, true);
                        if (ChatRightsEditActivity.this.currentType == 2) {
                            if (ChatRightsEditActivity.this.myAdminRights.manage_call || isCreator) {
                                i2 = 0;
                            }
                            checkCell.setIcon(i2);
                        }
                    } else if (position == ChatRightsEditActivity.this.addUsersRow) {
                        if (ChatRightsEditActivity.this.currentType == 0) {
                            if (ChatObject.isActionBannedByDefault(ChatRightsEditActivity.this.currentChat, 3)) {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddUsers", R.string.EditAdminAddUsers), ChatRightsEditActivity.this.adminRights.invite_users, true);
                            } else {
                                checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddUsersViaLink", R.string.EditAdminAddUsersViaLink), ChatRightsEditActivity.this.adminRights.invite_users, true);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsInviteUsers", R.string.UserRestrictionsInviteUsers), !ChatRightsEditActivity.this.bannedRights.invite_users && !ChatRightsEditActivity.this.defaultBannedRights.invite_users, true);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.invite_users) {
                                i2 = 0;
                            }
                            checkCell.setIcon(i2);
                        } else if (ChatRightsEditActivity.this.currentType == 2) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminAddUsersViaLink", R.string.EditAdminAddUsersViaLink), asAdminValue && ChatRightsEditActivity.this.adminRights.invite_users, true);
                            if (ChatRightsEditActivity.this.myAdminRights.invite_users || isCreator) {
                                i2 = 0;
                            }
                            checkCell.setIcon(i2);
                        }
                    } else if (position == ChatRightsEditActivity.this.pinMessagesRow) {
                        if (ChatRightsEditActivity.this.currentType == 0 || ChatRightsEditActivity.this.currentType == 2) {
                            checkCell.setTextAndCheck(LocaleController.getString("EditAdminPinMessages", R.string.EditAdminPinMessages), (asAdminValue && ChatRightsEditActivity.this.adminRights.pin_messages) || !ChatRightsEditActivity.this.defaultBannedRights.pin_messages, true);
                            if (ChatRightsEditActivity.this.currentType == 2) {
                                if (ChatRightsEditActivity.this.myAdminRights.pin_messages || isCreator) {
                                    i2 = 0;
                                }
                                checkCell.setIcon(i2);
                            }
                        } else if (ChatRightsEditActivity.this.currentType == 1) {
                            checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsPinMessages", R.string.UserRestrictionsPinMessages), !ChatRightsEditActivity.this.bannedRights.pin_messages && !ChatRightsEditActivity.this.defaultBannedRights.pin_messages, true);
                            if (!ChatRightsEditActivity.this.defaultBannedRights.pin_messages) {
                                i2 = 0;
                            }
                            checkCell.setIcon(i2);
                        }
                    } else if (position == ChatRightsEditActivity.this.sendMessagesRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSend", R.string.UserRestrictionsSend), !ChatRightsEditActivity.this.bannedRights.send_messages && !ChatRightsEditActivity.this.defaultBannedRights.send_messages, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_messages) {
                            i2 = 0;
                        }
                        checkCell.setIcon(i2);
                    } else if (position == ChatRightsEditActivity.this.sendMediaRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendMedia", R.string.UserRestrictionsSendMedia), !ChatRightsEditActivity.this.bannedRights.send_media && !ChatRightsEditActivity.this.defaultBannedRights.send_media, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_media) {
                            i2 = 0;
                        }
                        checkCell.setIcon(i2);
                    } else if (position == ChatRightsEditActivity.this.sendStickersRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendStickers", R.string.UserRestrictionsSendStickers), !ChatRightsEditActivity.this.bannedRights.send_stickers && !ChatRightsEditActivity.this.defaultBannedRights.send_stickers, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_stickers) {
                            i2 = 0;
                        }
                        checkCell.setIcon(i2);
                    } else if (position == ChatRightsEditActivity.this.embedLinksRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsEmbedLinks", R.string.UserRestrictionsEmbedLinks), !ChatRightsEditActivity.this.bannedRights.embed_links && !ChatRightsEditActivity.this.defaultBannedRights.embed_links, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.embed_links) {
                            i2 = 0;
                        }
                        checkCell.setIcon(i2);
                    } else if (position == ChatRightsEditActivity.this.sendPollsRow) {
                        checkCell.setTextAndCheck(LocaleController.getString("UserRestrictionsSendPolls", R.string.UserRestrictionsSendPolls), !ChatRightsEditActivity.this.bannedRights.send_polls && !ChatRightsEditActivity.this.defaultBannedRights.send_polls, true);
                        if (!ChatRightsEditActivity.this.defaultBannedRights.send_polls) {
                            i2 = 0;
                        }
                        checkCell.setIcon(i2);
                    }
                    if (ChatRightsEditActivity.this.currentType != 2) {
                        if (position == ChatRightsEditActivity.this.sendMediaRow || position == ChatRightsEditActivity.this.sendStickersRow || position == ChatRightsEditActivity.this.embedLinksRow || position == ChatRightsEditActivity.this.sendPollsRow) {
                            if (ChatRightsEditActivity.this.bannedRights.send_messages || ChatRightsEditActivity.this.bannedRights.view_messages || ChatRightsEditActivity.this.defaultBannedRights.send_messages || ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                                z = false;
                            }
                            checkCell.setEnabled(z);
                            return;
                        } else if (position == ChatRightsEditActivity.this.sendMessagesRow) {
                            if (ChatRightsEditActivity.this.bannedRights.view_messages || ChatRightsEditActivity.this.defaultBannedRights.view_messages) {
                                z = false;
                            }
                            checkCell.setEnabled(z);
                            return;
                        } else {
                            return;
                        }
                    }
                    return;
                case 5:
                    ShadowSectionCell shadowCell = (ShadowSectionCell) holder.itemView;
                    if (ChatRightsEditActivity.this.currentType == 2 && (position == ChatRightsEditActivity.this.rightsShadowRow || position == ChatRightsEditActivity.this.rankInfoRow)) {
                        shadowCell.setAlpha(ChatRightsEditActivity.this.asAdminT);
                    } else {
                        shadowCell.setAlpha(1.0f);
                    }
                    int i3 = ChatRightsEditActivity.this.rightsShadowRow;
                    int i4 = R.drawable.greydivider;
                    if (position != i3) {
                        if (position != ChatRightsEditActivity.this.removeAdminShadowRow) {
                            if (position == ChatRightsEditActivity.this.rankInfoRow) {
                                Context context = this.mContext;
                                if (!ChatRightsEditActivity.this.canEdit) {
                                    i4 = R.drawable.greydivider_bottom;
                                }
                                shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(context, i4, Theme.key_windowBackgroundGrayShadow));
                                return;
                            }
                            shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                        shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                    Context context2 = this.mContext;
                    if (ChatRightsEditActivity.this.removeAdminRow == -1 && ChatRightsEditActivity.this.rankRow == -1) {
                        i4 = R.drawable.greydivider_bottom;
                    }
                    shadowCell.setBackgroundDrawable(Theme.getThemedDrawable(context2, i4, Theme.key_windowBackgroundGrayShadow));
                    return;
                case 6:
                    TextDetailCell detailCell = (TextDetailCell) holder.itemView;
                    if (position == ChatRightsEditActivity.this.untilDateRow) {
                        if (ChatRightsEditActivity.this.bannedRights.until_date != 0 && Math.abs(ChatRightsEditActivity.this.bannedRights.until_date - (System.currentTimeMillis() / 1000)) <= 315360000) {
                            value = LocaleController.formatDateForBan(ChatRightsEditActivity.this.bannedRights.until_date);
                        } else {
                            value = LocaleController.getString("UserRestrictionsUntilForever", R.string.UserRestrictionsUntilForever);
                        }
                        detailCell.setTextAndValue(LocaleController.getString("UserRestrictionsDuration", R.string.UserRestrictionsDuration), value, false);
                        return;
                    }
                    return;
                case 7:
                    PollEditTextCell textCell = (PollEditTextCell) holder.itemView;
                    if (UserObject.isUserSelf(ChatRightsEditActivity.this.currentUser) && ChatRightsEditActivity.this.currentChat.creator) {
                        hint2 = LocaleController.getString("ChannelCreator", R.string.ChannelCreator);
                    } else {
                        hint2 = LocaleController.getString("ChannelAdmin", R.string.ChannelAdmin);
                    }
                    this.ignoreTextChange = true;
                    textCell.getTextView().setEnabled(ChatRightsEditActivity.this.canEdit || ChatRightsEditActivity.this.currentChat.creator);
                    textCell.getTextView().setSingleLine(true);
                    textCell.getTextView().setImeOptions(6);
                    textCell.setTextAndHint(ChatRightsEditActivity.this.currentRank, hint2, false);
                    this.ignoreTextChange = false;
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() == ChatRightsEditActivity.this.rankHeaderRow) {
                ChatRightsEditActivity.this.setTextLeft(holder.itemView);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder.getAdapterPosition() == ChatRightsEditActivity.this.rankRow && ChatRightsEditActivity.this.getParentActivity() != null) {
                AndroidUtilities.hideKeyboard(ChatRightsEditActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            }
            if (position == 1 || position == ChatRightsEditActivity.this.rightsShadowRow || position == ChatRightsEditActivity.this.removeAdminShadowRow || position == ChatRightsEditActivity.this.untilSectionRow || position == ChatRightsEditActivity.this.transferOwnerShadowRow) {
                return 5;
            }
            if (position != 2 && position != ChatRightsEditActivity.this.rankHeaderRow) {
                if (position != ChatRightsEditActivity.this.changeInfoRow && position != ChatRightsEditActivity.this.postMessagesRow && position != ChatRightsEditActivity.this.editMesagesRow && position != ChatRightsEditActivity.this.deleteMessagesRow && position != ChatRightsEditActivity.this.addAdminsRow && position != ChatRightsEditActivity.this.banUsersRow && position != ChatRightsEditActivity.this.addUsersRow && position != ChatRightsEditActivity.this.pinMessagesRow && position != ChatRightsEditActivity.this.sendMessagesRow && position != ChatRightsEditActivity.this.sendMediaRow && position != ChatRightsEditActivity.this.sendStickersRow && position != ChatRightsEditActivity.this.embedLinksRow && position != ChatRightsEditActivity.this.sendPollsRow && position != ChatRightsEditActivity.this.anonymousRow && position != ChatRightsEditActivity.this.startVoiceChatRow && position != ChatRightsEditActivity.this.manageRow) {
                    if (position != ChatRightsEditActivity.this.cantEditInfoRow && position != ChatRightsEditActivity.this.rankInfoRow) {
                        if (position != ChatRightsEditActivity.this.untilDateRow) {
                            if (position != ChatRightsEditActivity.this.rankRow) {
                                if (position != ChatRightsEditActivity.this.addBotButtonRow) {
                                    return 2;
                                }
                                return 8;
                            }
                            return 7;
                        }
                        return 6;
                    }
                    return 1;
                }
                return 4;
            }
            return 3;
        }
    }

    private void updateAsAdmin(boolean animated) {
        String str;
        int i;
        TLRPC.Chat chat;
        TLRPC.Chat chat2;
        FrameLayout frameLayout = this.addBotButton;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        int count = this.listView.getChildCount();
        int i2 = 0;
        while (true) {
            boolean z = true;
            if (i2 >= count) {
                break;
            }
            View child = this.listView.getChildAt(i2);
            int childPosition = this.listView.getChildAdapterPosition(child);
            if (child instanceof TextCheckCell2) {
                if (!this.asAdmin) {
                    if ((childPosition == this.changeInfoRow && !this.defaultBannedRights.change_info) || (childPosition == this.pinMessagesRow && !this.defaultBannedRights.pin_messages)) {
                        ((TextCheckCell2) child).setChecked(true);
                        ((TextCheckCell2) child).setEnabled(false, false);
                    } else {
                        ((TextCheckCell2) child).setChecked(false);
                        TextCheckCell2 textCheckCell2 = (TextCheckCell2) child;
                        if (childPosition != this.manageRow) {
                            z = false;
                        }
                        textCheckCell2.setEnabled(z, animated);
                    }
                } else {
                    boolean childValue = false;
                    boolean childEnabled = false;
                    if (childPosition == this.manageRow) {
                        childValue = this.asAdmin;
                        if (!this.myAdminRights.add_admins && ((chat2 = this.currentChat) == null || !chat2.creator)) {
                            z = false;
                        }
                        childEnabled = z;
                    } else if (childPosition == this.changeInfoRow) {
                        childValue = this.adminRights.change_info;
                        if (!this.myAdminRights.change_info || !this.defaultBannedRights.change_info) {
                            z = false;
                        }
                        childEnabled = z;
                    } else if (childPosition == this.postMessagesRow) {
                        childValue = this.adminRights.post_messages;
                        childEnabled = this.myAdminRights.post_messages;
                    } else if (childPosition == this.editMesagesRow) {
                        childValue = this.adminRights.edit_messages;
                        childEnabled = this.myAdminRights.edit_messages;
                    } else if (childPosition == this.deleteMessagesRow) {
                        childValue = this.adminRights.delete_messages;
                        childEnabled = this.myAdminRights.delete_messages;
                    } else if (childPosition == this.banUsersRow) {
                        childValue = this.adminRights.ban_users;
                        childEnabled = this.myAdminRights.ban_users;
                    } else if (childPosition == this.addUsersRow) {
                        childValue = this.adminRights.invite_users;
                        childEnabled = this.myAdminRights.invite_users;
                    } else if (childPosition == this.pinMessagesRow) {
                        childValue = this.adminRights.pin_messages;
                        if (!this.myAdminRights.pin_messages || !this.defaultBannedRights.pin_messages) {
                            z = false;
                        }
                        childEnabled = z;
                    } else if (childPosition == this.startVoiceChatRow) {
                        childValue = this.adminRights.manage_call;
                        childEnabled = this.myAdminRights.manage_call;
                    } else if (childPosition == this.addAdminsRow) {
                        childValue = this.adminRights.add_admins;
                        childEnabled = this.myAdminRights.add_admins;
                    } else if (childPosition == this.anonymousRow) {
                        childValue = this.adminRights.anonymous;
                        if (!this.myAdminRights.anonymous && ((chat = this.currentChat) == null || !chat.creator)) {
                            z = false;
                        }
                        childEnabled = z;
                    }
                    ((TextCheckCell2) child).setChecked(childValue);
                    ((TextCheckCell2) child).setEnabled(childEnabled, animated);
                }
            }
            i2++;
        }
        this.listViewAdapter.notifyDataSetChanged();
        AnimatedTextView animatedTextView = this.addBotButtonText;
        if (animatedTextView != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(LocaleController.getString("AddBotButton", R.string.AddBotButton));
            sb.append(" ");
            if (this.asAdmin) {
                i = R.string.AddBotButtonAsAdmin;
                str = "AddBotButtonAsAdmin";
            } else {
                i = R.string.AddBotButtonAsMember;
                str = "AddBotButtonAsMember";
            }
            sb.append(LocaleController.getString(str, i));
            animatedTextView.setText(sb.toString(), animated, this.asAdmin);
        }
        ValueAnimator valueAnimator = this.asAdminAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.asAdminAnimator = null;
        }
        float f = 1.0f;
        if (animated) {
            float[] fArr = new float[2];
            fArr[0] = this.asAdminT;
            fArr[1] = this.asAdmin ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.asAdminAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda11
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ChatRightsEditActivity.this.m2146lambda$updateAsAdmin$25$orgtelegramuiChatRightsEditActivity(valueAnimator2);
                }
            });
            ValueAnimator valueAnimator2 = this.asAdminAnimator;
            float f2 = this.asAdminT;
            if (!this.asAdmin) {
                f = 0.0f;
            }
            valueAnimator2.setDuration(Math.abs(f2 - f) * 200.0f);
            this.asAdminAnimator.start();
            return;
        }
        if (!this.asAdmin) {
            f = 0.0f;
        }
        this.asAdminT = f;
        FrameLayout frameLayout2 = this.addBotButton;
        if (frameLayout2 != null) {
            frameLayout2.invalidate();
        }
    }

    /* renamed from: lambda$updateAsAdmin$25$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2146lambda$updateAsAdmin$25$orgtelegramuiChatRightsEditActivity(ValueAnimator a) {
        this.asAdminT = ((Float) a.getAnimatedValue()).floatValue();
        FrameLayout frameLayout = this.addBotButton;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ChatRightsEditActivity$$ExternalSyntheticLambda16
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ChatRightsEditActivity.this.m2129x139757c1();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{UserCell2.class, TextSettingsCell.class, TextCheckCell2.class, HeaderCell.class, TextDetailCell.class, PollEditTextCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText5));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switch2Track));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell2.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switch2TrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText5));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{HeaderCell.class}, new String[]{"textView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{PollEditTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_windowBackgroundWhiteBlueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell2.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        themeDescriptions.add(new ThemeDescription((View) null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlack));
        themeDescriptions.add(new ThemeDescription((View) null, 0, new Class[]{DialogRadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextGray2));
        themeDescriptions.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOX, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogRadioBackground));
        themeDescriptions.add(new ThemeDescription((View) null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{DialogRadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogRadioBackgroundChecked));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$26$org-telegram-ui-ChatRightsEditActivity */
    public /* synthetic */ void m2129x139757c1() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell2) {
                    ((UserCell2) child).update(0);
                }
            }
        }
    }
}
