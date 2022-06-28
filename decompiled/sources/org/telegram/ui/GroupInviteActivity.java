package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.messaging.Constants;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class GroupInviteActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private long chatId;
    private int copyLinkRow;
    private EmptyTextProgressView emptyView;
    private TLRPC.TL_chatInviteExported invite;
    private int linkInfoRow;
    private int linkRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loading;
    private int revokeLinkRow;
    private int rowCount;
    private int shadowRow;
    private int shareLinkRow;

    public GroupInviteActivity(long cid) {
        this.chatId = cid;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        getMessagesController().loadFullChat(this.chatId, this.classGuid, true);
        this.loading = true;
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.linkRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.linkInfoRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.copyLinkRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.revokeLinkRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.shareLinkRow = i4;
        this.rowCount = i5 + 1;
        this.shadowRow = i5;
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteLink", R.string.InviteLink));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.GroupInviteActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    GroupInviteActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.showProgress();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.GroupInviteActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                GroupInviteActivity.this.m3554lambda$createView$1$orgtelegramuiGroupInviteActivity(view, i);
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-GroupInviteActivity */
    public /* synthetic */ void m3554lambda$createView$1$orgtelegramuiGroupInviteActivity(View view, int position) {
        if (getParentActivity() == null) {
            return;
        }
        if (position == this.copyLinkRow || position == this.linkRow) {
            if (this.invite == null) {
                return;
            }
            try {
                ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, this.invite.link);
                clipboard.setPrimaryClip(clip);
                BulletinFactory.createCopyLinkBulletin(this).show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (position == this.shareLinkRow) {
            if (this.invite == null) {
                return;
            }
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType(ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
                intent.putExtra("android.intent.extra.TEXT", this.invite.link);
                getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", R.string.InviteToGroupByLink)), 500);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        } else if (position == this.revokeLinkRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("RevokeAlert", R.string.RevokeAlert));
            builder.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
            builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.GroupInviteActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    GroupInviteActivity.this.m3553lambda$createView$0$orgtelegramuiGroupInviteActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-GroupInviteActivity */
    public /* synthetic */ void m3553lambda$createView$0$orgtelegramuiGroupInviteActivity(DialogInterface dialogInterface, int i) {
        generateLink(true);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull info = (TLRPC.ChatFull) args[0];
            int guid = ((Integer) args[1]).intValue();
            if (info.id == this.chatId && guid == this.classGuid) {
                TLRPC.TL_chatInviteExported exportedInvite = getMessagesController().getExportedInvite(this.chatId);
                this.invite = exportedInvite;
                if (exportedInvite == null) {
                    generateLink(false);
                    return;
                }
                this.loading = false;
                ListAdapter listAdapter = this.listAdapter;
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void generateLink(final boolean newRequest) {
        this.loading = true;
        TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
        req.peer = getMessagesController().getInputPeer(-this.chatId);
        int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.GroupInviteActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupInviteActivity.this.m3556lambda$generateLink$3$orgtelegramuiGroupInviteActivity(newRequest, tLObject, tL_error);
            }
        });
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* renamed from: lambda$generateLink$3$org-telegram-ui-GroupInviteActivity */
    public /* synthetic */ void m3556lambda$generateLink$3$orgtelegramuiGroupInviteActivity(final boolean newRequest, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupInviteActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                GroupInviteActivity.this.m3555lambda$generateLink$2$orgtelegramuiGroupInviteActivity(error, response, newRequest);
            }
        });
    }

    /* renamed from: lambda$generateLink$2$org-telegram-ui-GroupInviteActivity */
    public /* synthetic */ void m3555lambda$generateLink$2$orgtelegramuiGroupInviteActivity(TLRPC.TL_error error, TLObject response, boolean newRequest) {
        if (error == null) {
            this.invite = (TLRPC.TL_chatInviteExported) response;
            if (newRequest) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("RevokeAlertNewLink", R.string.RevokeAlertNewLink));
                builder.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                showDialog(builder.create());
            }
        }
        this.loading = false;
        this.listAdapter.notifyDataSetChanged();
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            GroupInviteActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == GroupInviteActivity.this.revokeLinkRow || position == GroupInviteActivity.this.copyLinkRow || position == GroupInviteActivity.this.shareLinkRow || position == GroupInviteActivity.this.linkRow;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (GroupInviteActivity.this.loading) {
                return 0;
            }
            return GroupInviteActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View view2 = new TextSettingsCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                default:
                    View view3 = new TextBlockCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view3;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position != GroupInviteActivity.this.copyLinkRow) {
                        if (position != GroupInviteActivity.this.shareLinkRow) {
                            if (position == GroupInviteActivity.this.revokeLinkRow) {
                                textCell.setText(LocaleController.getString("RevokeLink", R.string.RevokeLink), true);
                                return;
                            }
                            return;
                        }
                        textCell.setText(LocaleController.getString("ShareLink", R.string.ShareLink), false);
                        return;
                    }
                    textCell.setText(LocaleController.getString("CopyLink", R.string.CopyLink), true);
                    return;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position != GroupInviteActivity.this.shadowRow) {
                        if (position == GroupInviteActivity.this.linkInfoRow) {
                            TLRPC.Chat chat = GroupInviteActivity.this.getMessagesController().getChat(Long.valueOf(GroupInviteActivity.this.chatId));
                            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                                privacyCell.setText(LocaleController.getString("ChannelLinkInfo", R.string.ChannelLinkInfo));
                            } else {
                                privacyCell.setText(LocaleController.getString("LinkInfo", R.string.LinkInfo));
                            }
                            privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                            return;
                        }
                        return;
                    }
                    privacyCell.setText("");
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    return;
                case 2:
                    TextBlockCell textBlockCell = (TextBlockCell) holder.itemView;
                    textBlockCell.setText(GroupInviteActivity.this.invite != null ? GroupInviteActivity.this.invite.link : Constants.IPC_BUNDLE_KEY_SEND_ERROR, false);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == GroupInviteActivity.this.copyLinkRow || position == GroupInviteActivity.this.shareLinkRow || position == GroupInviteActivity.this.revokeLinkRow) {
                return 0;
            }
            if (position == GroupInviteActivity.this.shadowRow || position == GroupInviteActivity.this.linkInfoRow) {
                return 1;
            }
            return position == GroupInviteActivity.this.linkRow ? 2 : 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextBlockCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        return themeDescriptions;
    }
}
