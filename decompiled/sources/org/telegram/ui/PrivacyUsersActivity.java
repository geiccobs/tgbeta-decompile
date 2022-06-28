package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContactsActivity;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.PrivacyUsersActivity;
/* loaded from: classes4.dex */
public class PrivacyUsersActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ContactsActivity.ContactsActivityDelegate {
    public static final int TYPE_BLOCKED = 1;
    public static final int TYPE_FILTER = 2;
    public static final int TYPE_PRIVACY = 0;
    private int blockUserDetailRow;
    private int blockUserRow;
    private boolean blockedUsersActivity;
    private int currentType;
    private PrivacyActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private boolean isAlwaysShare;
    private boolean isGroup;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private int rowCount;
    private ArrayList<Long> uidArray;
    private int usersDetailRow;
    private int usersEndRow;
    private int usersHeaderRow;
    private int usersStartRow;

    /* loaded from: classes4.dex */
    public interface PrivacyActivityDelegate {
        void didUpdateUserList(ArrayList<Long> arrayList, boolean z);
    }

    public PrivacyUsersActivity() {
        this.currentType = 1;
        this.blockedUsersActivity = true;
    }

    public PrivacyUsersActivity(int type, ArrayList<Long> users, boolean group, boolean always) {
        this.uidArray = users;
        this.isAlwaysShare = always;
        this.isGroup = group;
        this.blockedUsersActivity = false;
        this.currentType = type;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        if (this.currentType == 1) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoad);
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        if (this.currentType == 1) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoad);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.currentType;
        int i2 = 2;
        if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("BlockedUsers", R.string.BlockedUsers));
        } else if (i == 2) {
            if (this.isAlwaysShare) {
                this.actionBar.setTitle(LocaleController.getString("FilterAlwaysShow", R.string.FilterAlwaysShow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("FilterNeverShow", R.string.FilterNeverShow));
            }
        } else if (this.isGroup) {
            if (this.isAlwaysShare) {
                this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", R.string.AlwaysAllow));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NeverAllow", R.string.NeverAllow));
            }
        } else if (this.isAlwaysShare) {
            this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", R.string.AlwaysShareWithTitle));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", R.string.NeverShareWithTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.PrivacyUsersActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    PrivacyUsersActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        if (this.currentType == 1) {
            emptyTextProgressView.setText(LocaleController.getString("NoBlocked", R.string.NoBlocked));
        } else {
            emptyTextProgressView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        }
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        RecyclerListView recyclerListView4 = this.listView;
        if (LocaleController.isRTL) {
            i2 = 1;
        }
        recyclerListView4.setVerticalScrollbarPosition(i2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.PrivacyUsersActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i3) {
                PrivacyUsersActivity.this.m4346lambda$createView$1$orgtelegramuiPrivacyUsersActivity(view, i3);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.PrivacyUsersActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i3) {
                return PrivacyUsersActivity.this.m4347lambda$createView$2$orgtelegramuiPrivacyUsersActivity(view, i3);
            }
        });
        if (this.currentType == 1) {
            this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.PrivacyUsersActivity.2
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (!PrivacyUsersActivity.this.getMessagesController().blockedEndReached) {
                        int firstVisibleItem = PrivacyUsersActivity.this.layoutManager.findFirstVisibleItemPosition();
                        int visibleItemCount = Math.abs(PrivacyUsersActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                        int totalItemCount = recyclerView.getAdapter().getItemCount();
                        if (visibleItemCount > 0 && PrivacyUsersActivity.this.layoutManager.findLastVisibleItemPosition() >= totalItemCount - 10) {
                            PrivacyUsersActivity.this.getMessagesController().getBlockedPeers(false);
                        }
                    }
                }
            });
            if (getMessagesController().totalBlockedCount < 0) {
                this.emptyView.showProgress();
            } else {
                this.emptyView.showTextView();
            }
        }
        updateRows();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PrivacyUsersActivity */
    public /* synthetic */ void m4346lambda$createView$1$orgtelegramuiPrivacyUsersActivity(View view, int position) {
        if (position == this.blockUserRow) {
            if (this.currentType == 1) {
                presentFragment(new DialogOrContactPickerActivity());
                return;
            }
            Bundle args = new Bundle();
            args.putBoolean(this.isAlwaysShare ? "isAlwaysShare" : "isNeverShare", true);
            if (this.isGroup) {
                args.putInt("chatAddType", 1);
            } else if (this.currentType == 2) {
                args.putInt("chatAddType", 2);
            }
            GroupCreateActivity fragment = new GroupCreateActivity(args);
            fragment.setDelegate(new GroupCreateActivity.GroupCreateActivityDelegate() { // from class: org.telegram.ui.PrivacyUsersActivity$$ExternalSyntheticLambda4
                @Override // org.telegram.ui.GroupCreateActivity.GroupCreateActivityDelegate
                public final void didSelectUsers(ArrayList arrayList) {
                    PrivacyUsersActivity.this.m4345lambda$createView$0$orgtelegramuiPrivacyUsersActivity(arrayList);
                }
            });
            presentFragment(fragment);
        } else if (position >= this.usersStartRow && position < this.usersEndRow) {
            if (this.currentType == 1) {
                Bundle args2 = new Bundle();
                args2.putLong("user_id", getMessagesController().blockePeers.keyAt(position - this.usersStartRow));
                presentFragment(new ProfileActivity(args2));
                return;
            }
            Bundle args3 = new Bundle();
            long uid = this.uidArray.get(position - this.usersStartRow).longValue();
            if (DialogObject.isUserDialog(uid)) {
                args3.putLong("user_id", uid);
            } else {
                args3.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -uid);
            }
            presentFragment(new ProfileActivity(args3));
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PrivacyUsersActivity */
    public /* synthetic */ void m4345lambda$createView$0$orgtelegramuiPrivacyUsersActivity(ArrayList ids) {
        Iterator it = ids.iterator();
        while (it.hasNext()) {
            Long id1 = (Long) it.next();
            if (!this.uidArray.contains(id1)) {
                this.uidArray.add(id1);
            }
        }
        updateRows();
        PrivacyActivityDelegate privacyActivityDelegate = this.delegate;
        if (privacyActivityDelegate != null) {
            privacyActivityDelegate.didUpdateUserList(this.uidArray, true);
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PrivacyUsersActivity */
    public /* synthetic */ boolean m4347lambda$createView$2$orgtelegramuiPrivacyUsersActivity(View view, int position) {
        int i = this.usersStartRow;
        if (position >= i && position < this.usersEndRow) {
            if (this.currentType == 1) {
                showUnblockAlert(Long.valueOf(getMessagesController().blockePeers.keyAt(position - this.usersStartRow)));
            } else {
                showUnblockAlert(this.uidArray.get(position - i));
            }
            return true;
        }
        return false;
    }

    public void setDelegate(PrivacyActivityDelegate privacyActivityDelegate) {
        this.delegate = privacyActivityDelegate;
    }

    public void showUnblockAlert(final Long uid) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        CharSequence[] items = this.currentType == 1 ? new CharSequence[]{LocaleController.getString("Unblock", R.string.Unblock)} : new CharSequence[]{LocaleController.getString("Delete", R.string.Delete)};
        builder.setItems(items, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PrivacyUsersActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PrivacyUsersActivity.this.m4349lambda$showUnblockAlert$3$orgtelegramuiPrivacyUsersActivity(uid, dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    /* renamed from: lambda$showUnblockAlert$3$org-telegram-ui-PrivacyUsersActivity */
    public /* synthetic */ void m4349lambda$showUnblockAlert$3$orgtelegramuiPrivacyUsersActivity(Long uid, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            if (this.currentType == 1) {
                getMessagesController().unblockPeer(uid.longValue());
                return;
            }
            this.uidArray.remove(uid);
            updateRows();
            PrivacyActivityDelegate privacyActivityDelegate = this.delegate;
            if (privacyActivityDelegate != null) {
                privacyActivityDelegate.didUpdateUserList(this.uidArray, false);
            }
            if (this.uidArray.isEmpty()) {
                finishFragment();
            }
        }
    }

    private void updateRows() {
        int count;
        this.rowCount = 0;
        if (!this.blockedUsersActivity || getMessagesController().totalBlockedCount >= 0) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.blockUserRow = i;
            this.rowCount = i2 + 1;
            this.blockUserDetailRow = i2;
            if (this.currentType == 1) {
                count = getMessagesController().blockePeers.size();
            } else {
                count = this.uidArray.size();
            }
            if (count != 0) {
                int i3 = this.rowCount;
                int i4 = i3 + 1;
                this.rowCount = i4;
                this.usersHeaderRow = i3;
                this.usersStartRow = i4;
                int i5 = i4 + count;
                this.rowCount = i5;
                this.usersEndRow = i5;
                this.rowCount = i5 + 1;
                this.usersDetailRow = i5;
            } else {
                this.usersHeaderRow = -1;
                this.usersStartRow = -1;
                this.usersEndRow = -1;
                this.usersDetailRow = -1;
            }
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 || (MessagesController.UPDATE_MASK_NAME & mask) != 0) {
                updateVisibleRows(mask);
            }
        } else if (id == NotificationCenter.blockedUsersDidLoad) {
            this.emptyView.showTextView();
            updateRows();
        }
    }

    private void updateVisibleRows(int mask) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null) {
            return;
        }
        int count = recyclerListView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof ManageChatUserCell) {
                ((ManageChatUserCell) child).update(mask);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ContactsActivity.ContactsActivityDelegate
    public void didSelectContact(TLRPC.User user, String param, ContactsActivity activity) {
        if (user == null) {
            return;
        }
        getMessagesController().blockPeer(user.id);
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            PrivacyUsersActivity.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return PrivacyUsersActivity.this.rowCount;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            return viewType == 0 || viewType == 2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View manageChatUserCell = new ManageChatUserCell(this.mContext, 7, 6, true);
                    manageChatUserCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    ((ManageChatUserCell) manageChatUserCell).setDelegate(new ManageChatUserCell.ManageChatUserCellDelegate() { // from class: org.telegram.ui.PrivacyUsersActivity$ListAdapter$$ExternalSyntheticLambda0
                        @Override // org.telegram.ui.Cells.ManageChatUserCell.ManageChatUserCellDelegate
                        public final boolean onOptionsButtonCheck(ManageChatUserCell manageChatUserCell2, boolean z) {
                            return PrivacyUsersActivity.ListAdapter.this.m4350xd7d3d381(manageChatUserCell2, z);
                        }
                    });
                    view = manageChatUserCell;
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    View manageChatTextCell = new ManageChatTextCell(this.mContext);
                    manageChatTextCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = manageChatTextCell;
                    break;
                default:
                    HeaderCell headerCell = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteBlueHeader, 21, 11, false);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    headerCell.setHeight(43);
                    view = headerCell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-PrivacyUsersActivity$ListAdapter */
        public /* synthetic */ boolean m4350xd7d3d381(ManageChatUserCell cell, boolean click) {
            if (click) {
                PrivacyUsersActivity.this.showUnblockAlert((Long) cell.getTag());
                return true;
            }
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String subtitle;
            String number;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    long uid = PrivacyUsersActivity.this.currentType == 1 ? PrivacyUsersActivity.this.getMessagesController().blockePeers.keyAt(position - PrivacyUsersActivity.this.usersStartRow) : ((Long) PrivacyUsersActivity.this.uidArray.get(position - PrivacyUsersActivity.this.usersStartRow)).longValue();
                    userCell.setTag(Long.valueOf(uid));
                    if (uid > 0) {
                        TLRPC.User user = PrivacyUsersActivity.this.getMessagesController().getUser(Long.valueOf(uid));
                        if (user != null) {
                            if (user.bot) {
                                number = LocaleController.getString("Bot", R.string.Bot).substring(0, 1).toUpperCase() + LocaleController.getString("Bot", R.string.Bot).substring(1);
                            } else {
                                String number2 = user.phone;
                                if (number2 != null && user.phone.length() != 0) {
                                    number = PhoneFormat.getInstance().format("+" + user.phone);
                                } else {
                                    number = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
                                }
                            }
                            if (position != PrivacyUsersActivity.this.usersEndRow - 1) {
                                z = true;
                            }
                            userCell.setData(user, null, number, z);
                            return;
                        }
                        return;
                    }
                    TLRPC.Chat chat = PrivacyUsersActivity.this.getMessagesController().getChat(Long.valueOf(-uid));
                    if (chat != null) {
                        if (chat.participants_count != 0) {
                            subtitle = LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]);
                        } else if (chat.has_geo) {
                            subtitle = LocaleController.getString("MegaLocation", R.string.MegaLocation);
                        } else {
                            String subtitle2 = chat.username;
                            if (TextUtils.isEmpty(subtitle2)) {
                                subtitle = LocaleController.getString("MegaPrivate", R.string.MegaPrivate);
                            } else {
                                subtitle = LocaleController.getString("MegaPublic", R.string.MegaPublic);
                            }
                        }
                        if (position != PrivacyUsersActivity.this.usersEndRow - 1) {
                            z = true;
                        }
                        userCell.setData(chat, null, subtitle, z);
                        return;
                    }
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == PrivacyUsersActivity.this.blockUserDetailRow) {
                        if (PrivacyUsersActivity.this.currentType == 1) {
                            privacyCell.setText(LocaleController.getString("BlockedUsersInfo", R.string.BlockedUsersInfo));
                        } else {
                            privacyCell.setText(null);
                        }
                        if (PrivacyUsersActivity.this.usersStartRow == -1) {
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                            return;
                        } else {
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                    } else if (position == PrivacyUsersActivity.this.usersDetailRow) {
                        privacyCell.setText("");
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    ManageChatTextCell actionCell = (ManageChatTextCell) holder.itemView;
                    actionCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                    if (PrivacyUsersActivity.this.currentType == 1) {
                        actionCell.setText(LocaleController.getString("BlockUser", R.string.BlockUser), null, R.drawable.msg_contact_add, false);
                        return;
                    } else {
                        actionCell.setText(LocaleController.getString("PrivacyAddAnException", R.string.PrivacyAddAnException), null, R.drawable.msg_contact_add, false);
                        return;
                    }
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == PrivacyUsersActivity.this.usersHeaderRow) {
                        if (PrivacyUsersActivity.this.currentType == 1) {
                            headerCell.setText(LocaleController.formatPluralString("BlockedUsersCount", PrivacyUsersActivity.this.getMessagesController().totalBlockedCount, new Object[0]));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("PrivacyExceptions", R.string.PrivacyExceptions));
                            return;
                        }
                    }
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != PrivacyUsersActivity.this.usersHeaderRow) {
                if (position != PrivacyUsersActivity.this.blockUserRow) {
                    if (position == PrivacyUsersActivity.this.blockUserDetailRow || position == PrivacyUsersActivity.this.usersDetailRow) {
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
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.PrivacyUsersActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                PrivacyUsersActivity.this.m4348xc4192692();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
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
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$4$org-telegram-ui-PrivacyUsersActivity */
    public /* synthetic */ void m4348xc4192692() {
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
