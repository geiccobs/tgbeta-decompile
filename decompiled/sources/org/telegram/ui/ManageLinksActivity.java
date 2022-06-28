package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.appindexing.builders.TimerBuilder;
import com.google.firebase.messaging.Constants;
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CreationTextCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.InviteLinkBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TimerParticles;
import org.telegram.ui.LinkEditActivity;
import org.telegram.ui.ManageLinksActivity;
/* loaded from: classes4.dex */
public class ManageLinksActivity extends BaseFragment {
    private long adminId;
    private int adminsDividerRow;
    private int adminsEndRow;
    private int adminsHeaderRow;
    boolean adminsLoaded;
    private int adminsStartRow;
    private boolean canEdit;
    private int createLinkHelpRow;
    private int createNewLinkRow;
    private int creatorDividerRow;
    private int creatorRow;
    private TLRPC.Chat currentChat;
    private long currentChatId;
    boolean deletingRevokedLinks;
    private int dividerRow;
    boolean hasMore;
    private int helpRow;
    private TLRPC.ChatFull info;
    private TLRPC.TL_chatInviteExported invite;
    private InviteLinkBottomSheet inviteLinkBottomSheet;
    private int invitesCount;
    private boolean isChannel;
    private boolean isOpened;
    private boolean isPublic;
    private int lastDivider;
    Drawable linkIcon;
    Drawable linkIconRevoked;
    private int linksEndRow;
    private int linksHeaderRow;
    boolean linksLoading;
    private int linksLoadingRow;
    private int linksStartRow;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    boolean loadAdmins;
    private int permanentLinkHeaderRow;
    private int permanentLinkRow;
    private RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
    private int revokeAllDivider;
    private int revokeAllRow;
    private int revokedDivider;
    private int revokedHeader;
    private int revokedLinksEndRow;
    private int revokedLinksStartRow;
    private int rowCount;
    long timeDif;
    private boolean transitionFinished;
    private ArrayList<TLRPC.TL_chatInviteExported> invites = new ArrayList<>();
    private ArrayList<TLRPC.TL_chatInviteExported> revokedInvites = new ArrayList<>();
    private HashMap<Long, TLRPC.User> users = new HashMap<>();
    private ArrayList<TLRPC.TL_chatAdminWithInvites> admins = new ArrayList<>();
    Runnable updateTimerRunnable = new Runnable() { // from class: org.telegram.ui.ManageLinksActivity.1
        @Override // java.lang.Runnable
        public void run() {
            if (ManageLinksActivity.this.listView == null) {
                return;
            }
            for (int i = 0; i < ManageLinksActivity.this.listView.getChildCount(); i++) {
                View child = ManageLinksActivity.this.listView.getChildAt(i);
                if (child instanceof LinkCell) {
                    LinkCell linkCell = (LinkCell) child;
                    if (linkCell.timerRunning) {
                        linkCell.setLink(linkCell.invite, linkCell.position);
                    }
                }
            }
            AndroidUtilities.runOnUIThread(this, 500L);
        }
    };
    boolean loadRevoked = false;
    private final LinkEditActivity.Callback linkEditActivityCallback = new AnonymousClass6();
    int animationIndex = -1;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class EmptyView extends LinearLayout implements NotificationCenter.NotificationCenterDelegate {
        private static final String stickerSetName = "tg_placeholders_android";
        private final int currentAccount = UserConfig.selectedAccount;
        private BackupImageView stickerView;

        public EmptyView(Context context) {
            super(context);
            setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
            setOrientation(1);
            BackupImageView backupImageView = new BackupImageView(context);
            this.stickerView = backupImageView;
            addView(backupImageView, LayoutHelper.createLinear((int) LocationRequest.PRIORITY_LOW_POWER, (int) LocationRequest.PRIORITY_LOW_POWER, 49, 0, 2, 0, 0));
        }

        private void setSticker() {
            TLRPC.TL_messages_stickerSet set = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("tg_placeholders_android");
            if (set == null) {
                set = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("tg_placeholders_android");
            }
            if (set != null && set.documents.size() >= 4) {
                TLRPC.Document document = set.documents.get(3);
                ImageLocation imageLocation = ImageLocation.getForDocument(document);
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(document, Theme.key_windowBackgroundGray, 1.0f);
                this.stickerView.setImage(imageLocation, "104_104", "tgs", svgThumb, set);
                return;
            }
            MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders_android", false, set == null);
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

    public ManageLinksActivity(long chatId, long adminId, int invitesCount) {
        boolean z = false;
        this.currentChatId = chatId;
        this.invitesCount = invitesCount;
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(chatId));
        this.currentChat = chat;
        this.isChannel = ChatObject.isChannel(chat) && !this.currentChat.megagroup;
        if (adminId == 0) {
            this.adminId = getAccountInstance().getUserConfig().clientUserId;
        } else {
            this.adminId = adminId;
        }
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.adminId));
        if (this.adminId == getAccountInstance().getUserConfig().clientUserId || (user != null && !user.bot)) {
            z = true;
        }
        this.canEdit = z;
    }

    public void loadLinks(boolean notify) {
        if (this.loadAdmins && !this.adminsLoaded) {
            this.linksLoading = true;
            TLRPC.TL_messages_getAdminsWithInvites req = new TLRPC.TL_messages_getAdminsWithInvites();
            req.peer = getMessagesController().getInputPeer(-this.currentChatId);
            int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda1
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ManageLinksActivity.this.m3892lambda$loadLinks$2$orgtelegramuiManageLinksActivity(tLObject, tL_error);
                }
            });
            getConnectionsManager().bindRequestToGuid(reqId, getClassGuid());
        } else {
            TLRPC.TL_messages_getExportedChatInvites req2 = new TLRPC.TL_messages_getExportedChatInvites();
            req2.peer = getMessagesController().getInputPeer(-this.currentChatId);
            if (this.adminId == getUserConfig().getClientUserId()) {
                req2.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
            } else {
                req2.admin_id = getMessagesController().getInputUser(this.adminId);
            }
            final boolean revoked = this.loadRevoked;
            if (this.loadRevoked) {
                req2.revoked = true;
                if (!this.revokedInvites.isEmpty()) {
                    req2.flags |= 4;
                    ArrayList<TLRPC.TL_chatInviteExported> arrayList = this.revokedInvites;
                    req2.offset_link = arrayList.get(arrayList.size() - 1).link;
                    ArrayList<TLRPC.TL_chatInviteExported> arrayList2 = this.revokedInvites;
                    req2.offset_date = arrayList2.get(arrayList2.size() - 1).date;
                }
            } else if (!this.invites.isEmpty()) {
                req2.flags |= 4;
                ArrayList<TLRPC.TL_chatInviteExported> arrayList3 = this.invites;
                req2.offset_link = arrayList3.get(arrayList3.size() - 1).link;
                ArrayList<TLRPC.TL_chatInviteExported> arrayList4 = this.invites;
                req2.offset_date = arrayList4.get(arrayList4.size() - 1).date;
            }
            this.linksLoading = true;
            final TLRPC.TL_chatInviteExported inviteFinal = this.isPublic ? null : this.invite;
            int reqId2 = getConnectionsManager().sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda5
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ManageLinksActivity.this.m3895lambda$loadLinks$5$orgtelegramuiManageLinksActivity(inviteFinal, revoked, tLObject, tL_error);
                }
            });
            getConnectionsManager().bindRequestToGuid(reqId2, getClassGuid());
        }
        if (notify) {
            updateRows(true);
        }
    }

    /* renamed from: lambda$loadLinks$1$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3891lambda$loadLinks$1$orgtelegramuiManageLinksActivity(final TLRPC.TL_error error, final TLObject response) {
        getNotificationCenter().doOnIdle(new Runnable() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                ManageLinksActivity.this.m3890lambda$loadLinks$0$orgtelegramuiManageLinksActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$loadLinks$2$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3892lambda$loadLinks$2$orgtelegramuiManageLinksActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                ManageLinksActivity.this.m3891lambda$loadLinks$1$orgtelegramuiManageLinksActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$loadLinks$0$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3890lambda$loadLinks$0$orgtelegramuiManageLinksActivity(TLRPC.TL_error error, TLObject response) {
        this.linksLoading = false;
        if (error == null) {
            TLRPC.TL_messages_chatAdminsWithInvites adminsWithInvites = (TLRPC.TL_messages_chatAdminsWithInvites) response;
            for (int i = 0; i < adminsWithInvites.admins.size(); i++) {
                TLRPC.TL_chatAdminWithInvites admin = adminsWithInvites.admins.get(i);
                if (admin.admin_id != getAccountInstance().getUserConfig().clientUserId) {
                    this.admins.add(admin);
                }
            }
            for (int i2 = 0; i2 < adminsWithInvites.users.size(); i2++) {
                TLRPC.User user = adminsWithInvites.users.get(i2);
                this.users.put(Long.valueOf(user.id), user);
            }
        }
        int oldRowsCount = this.rowCount;
        this.adminsLoaded = true;
        this.hasMore = false;
        if (this.admins.size() > 0 && this.recyclerItemsEnterAnimator != null && !this.isPaused && this.isOpened) {
            this.recyclerItemsEnterAnimator.showItemsAnimated(oldRowsCount + 1);
        }
        if (!this.hasMore || this.invites.size() + this.revokedInvites.size() + this.admins.size() >= 5) {
            resumeDelayedFragmentAnimation();
        }
        if (!this.hasMore && !this.loadRevoked) {
            this.hasMore = true;
            this.loadRevoked = true;
            loadLinks(false);
        }
        updateRows(true);
    }

    /* renamed from: lambda$loadLinks$5$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3895lambda$loadLinks$5$orgtelegramuiManageLinksActivity(TLRPC.TL_chatInviteExported inviteFinal, final boolean revoked, final TLObject response, final TLRPC.TL_error error) {
        TLRPC.TL_chatInviteExported permanentLink = null;
        if (error == null) {
            TLRPC.TL_messages_exportedChatInvites invites = (TLRPC.TL_messages_exportedChatInvites) response;
            if (invites.invites.size() > 0 && inviteFinal != null) {
                int i = 0;
                while (true) {
                    if (i >= invites.invites.size()) {
                        break;
                    } else if (!((TLRPC.TL_chatInviteExported) invites.invites.get(i)).link.equals(inviteFinal.link)) {
                        i++;
                    } else {
                        permanentLink = (TLRPC.TL_chatInviteExported) invites.invites.remove(i);
                        break;
                    }
                }
            }
        }
        final TLRPC.TL_chatInviteExported finalPermanentLink = permanentLink;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                ManageLinksActivity.this.m3894lambda$loadLinks$4$orgtelegramuiManageLinksActivity(finalPermanentLink, error, response, revoked);
            }
        });
    }

    /* renamed from: lambda$loadLinks$4$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3894lambda$loadLinks$4$orgtelegramuiManageLinksActivity(final TLRPC.TL_chatInviteExported finalPermanentLink, final TLRPC.TL_error error, final TLObject response, final boolean revoked) {
        getNotificationCenter().doOnIdle(new Runnable() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ManageLinksActivity.this.m3893lambda$loadLinks$3$orgtelegramuiManageLinksActivity(finalPermanentLink, error, response, revoked);
            }
        });
    }

    /* renamed from: lambda$loadLinks$3$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3893lambda$loadLinks$3$orgtelegramuiManageLinksActivity(TLRPC.TL_chatInviteExported finalPermanentLink, TLRPC.TL_error error, TLObject response, boolean revoked) {
        DiffCallback callback = saveListState();
        this.linksLoading = false;
        this.hasMore = false;
        if (finalPermanentLink != null) {
            this.invite = finalPermanentLink;
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null) {
                chatFull.exported_invite = finalPermanentLink;
            }
        }
        boolean updateByDiffUtils = false;
        if (error == null) {
            TLRPC.TL_messages_exportedChatInvites invites = (TLRPC.TL_messages_exportedChatInvites) response;
            if (revoked) {
                for (int i = 0; i < invites.invites.size(); i++) {
                    TLRPC.TL_chatInviteExported in = (TLRPC.TL_chatInviteExported) invites.invites.get(i);
                    fixDate(in);
                    this.revokedInvites.add(in);
                }
            } else {
                if (this.adminId != getAccountInstance().getUserConfig().clientUserId && this.invites.size() == 0 && invites.invites.size() > 0) {
                    this.invite = (TLRPC.TL_chatInviteExported) invites.invites.get(0);
                    invites.invites.remove(0);
                }
                for (int i2 = 0; i2 < invites.invites.size(); i2++) {
                    TLRPC.TL_chatInviteExported in2 = (TLRPC.TL_chatInviteExported) invites.invites.get(i2);
                    fixDate(in2);
                    this.invites.add(in2);
                }
            }
            for (int i3 = 0; i3 < invites.users.size(); i3++) {
                this.users.put(Long.valueOf(invites.users.get(i3).id), invites.users.get(i3));
            }
            int i4 = this.rowCount;
            if (invites.invites.size() == 0) {
                this.hasMore = false;
            } else if (revoked) {
                this.hasMore = this.revokedInvites.size() + 1 < invites.count;
            } else {
                this.hasMore = this.invites.size() + 1 < invites.count;
            }
            if (invites.invites.size() > 0 && this.isOpened) {
                if (this.recyclerItemsEnterAnimator != null && !this.isPaused) {
                    this.recyclerItemsEnterAnimator.showItemsAnimated(i4 + 1);
                }
            } else {
                updateByDiffUtils = true;
            }
            TLRPC.ChatFull chatFull2 = this.info;
            if (chatFull2 != null && !revoked) {
                chatFull2.invitesCount = invites.count;
                getMessagesStorage().saveChatLinksCount(this.currentChatId, this.info.invitesCount);
            }
        } else {
            this.hasMore = false;
        }
        boolean loadNext = false;
        if (!this.hasMore && !this.loadRevoked && this.adminId == getAccountInstance().getUserConfig().clientUserId) {
            this.hasMore = true;
            this.loadAdmins = true;
            loadNext = true;
        } else if (!this.hasMore && !this.loadRevoked) {
            this.hasMore = true;
            this.loadRevoked = true;
            loadNext = true;
        }
        if (!this.hasMore || this.invites.size() + this.revokedInvites.size() + this.admins.size() >= 5) {
            resumeDelayedFragmentAnimation();
        }
        if (loadNext) {
            loadLinks(false);
        }
        if (updateByDiffUtils && this.listViewAdapter != null && this.listView.getChildCount() > 0) {
            updateRecyclerViewAnimated(callback);
        } else {
            updateRows(true);
        }
    }

    public void updateRows(boolean notify) {
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.currentChatId));
        this.currentChat = chat;
        if (chat == null) {
            return;
        }
        this.creatorRow = -1;
        this.creatorDividerRow = -1;
        this.linksStartRow = -1;
        this.linksEndRow = -1;
        this.linksLoadingRow = -1;
        this.revokedLinksStartRow = -1;
        this.revokedLinksEndRow = -1;
        this.revokedHeader = -1;
        this.revokedDivider = -1;
        this.lastDivider = -1;
        this.revokeAllRow = -1;
        this.revokeAllDivider = -1;
        this.createLinkHelpRow = -1;
        this.helpRow = -1;
        this.createNewLinkRow = -1;
        this.adminsEndRow = -1;
        this.adminsStartRow = -1;
        this.adminsDividerRow = -1;
        this.adminsHeaderRow = -1;
        this.linksHeaderRow = -1;
        this.dividerRow = -1;
        boolean otherAdmin = false;
        this.rowCount = 0;
        if (this.adminId != getAccountInstance().getUserConfig().clientUserId) {
            otherAdmin = true;
        }
        if (otherAdmin) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.creatorRow = i;
            this.rowCount = i2 + 1;
            this.creatorDividerRow = i2;
        } else {
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.helpRow = i3;
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.permanentLinkHeaderRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.permanentLinkRow = i5;
        if (!otherAdmin) {
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.dividerRow = i6;
            this.rowCount = i7 + 1;
            this.createNewLinkRow = i7;
        } else if (!this.invites.isEmpty()) {
            int i8 = this.rowCount;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.dividerRow = i8;
            this.rowCount = i9 + 1;
            this.linksHeaderRow = i9;
        }
        if (!this.invites.isEmpty()) {
            int i10 = this.rowCount;
            this.linksStartRow = i10;
            int size = i10 + this.invites.size();
            this.rowCount = size;
            this.linksEndRow = size;
        }
        if (!otherAdmin && this.invites.isEmpty() && this.createNewLinkRow >= 0 && (!this.linksLoading || this.loadAdmins || this.loadRevoked)) {
            int i11 = this.rowCount;
            this.rowCount = i11 + 1;
            this.createLinkHelpRow = i11;
        }
        if (!otherAdmin && this.admins.size() > 0) {
            if ((!this.invites.isEmpty() || this.createNewLinkRow >= 0) && this.createLinkHelpRow == -1) {
                int i12 = this.rowCount;
                this.rowCount = i12 + 1;
                this.adminsDividerRow = i12;
            }
            int i13 = this.rowCount;
            int i14 = i13 + 1;
            this.rowCount = i14;
            this.adminsHeaderRow = i13;
            this.adminsStartRow = i14;
            int size2 = i14 + this.admins.size();
            this.rowCount = size2;
            this.adminsEndRow = size2;
        }
        if (!this.revokedInvites.isEmpty()) {
            if (this.adminsStartRow >= 0) {
                int i15 = this.rowCount;
                this.rowCount = i15 + 1;
                this.revokedDivider = i15;
            } else if ((!this.invites.isEmpty() || this.createNewLinkRow >= 0) && this.createLinkHelpRow == -1) {
                int i16 = this.rowCount;
                this.rowCount = i16 + 1;
                this.revokedDivider = i16;
            } else if (otherAdmin && this.linksStartRow == -1) {
                int i17 = this.rowCount;
                this.rowCount = i17 + 1;
                this.revokedDivider = i17;
            }
            int i18 = this.rowCount;
            int i19 = i18 + 1;
            this.rowCount = i19;
            this.revokedHeader = i18;
            this.revokedLinksStartRow = i19;
            int size3 = i19 + this.revokedInvites.size();
            this.rowCount = size3;
            this.revokedLinksEndRow = size3;
            int i20 = size3 + 1;
            this.rowCount = i20;
            this.revokeAllDivider = size3;
            this.rowCount = i20 + 1;
            this.revokeAllRow = i20;
        }
        if (!this.loadAdmins && !this.loadRevoked && ((this.linksLoading || this.hasMore) && !otherAdmin)) {
            int i21 = this.rowCount;
            this.rowCount = i21 + 1;
            this.linksLoadingRow = i21;
        }
        if (!this.invites.isEmpty() || !this.revokedInvites.isEmpty()) {
            int i22 = this.rowCount;
            this.rowCount = i22 + 1;
            this.lastDivider = i22;
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null && notify) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("InviteLinks", R.string.InviteLinks));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ManageLinksActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    ManageLinksActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context) { // from class: org.telegram.ui.ManageLinksActivity.3
            @Override // android.view.ViewGroup, android.view.View
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                AndroidUtilities.runOnUIThread(ManageLinksActivity.this.updateTimerRunnable, 500L);
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                AndroidUtilities.cancelRunOnUIThread(ManageLinksActivity.this.updateTimerRunnable);
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setTag(Theme.key_windowBackgroundGray);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, 1, false) { // from class: org.telegram.ui.ManageLinksActivity.4
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.listView.setLayoutManager(layoutManager);
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ManageLinksActivity.5
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (ManageLinksActivity.this.hasMore && !ManageLinksActivity.this.linksLoading) {
                    int lastPosition = layoutManager.findLastVisibleItemPosition();
                    if (ManageLinksActivity.this.rowCount - lastPosition < 10) {
                        ManageLinksActivity.this.loadLinks(true);
                    }
                }
            }
        });
        this.recyclerItemsEnterAnimator = new RecyclerItemsEnterAnimator(this.listView, false);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ManageLinksActivity.this.m3886lambda$createView$9$orgtelegramuiManageLinksActivity(context, view, i);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return ManageLinksActivity.this.m3882lambda$createView$10$orgtelegramuiManageLinksActivity(view, i);
            }
        });
        this.linkIcon = ContextCompat.getDrawable(context, R.drawable.msg_link_1);
        this.linkIconRevoked = ContextCompat.getDrawable(context, R.drawable.msg_link_2);
        this.linkIcon.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        updateRows(true);
        this.timeDif = getConnectionsManager().getCurrentTime() - (System.currentTimeMillis() / 1000);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3886lambda$createView$9$orgtelegramuiManageLinksActivity(Context context, View view, int position) {
        if (position == this.creatorRow) {
            TLRPC.User user = this.users.get(Long.valueOf(this.invite.admin_id));
            if (user != null) {
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", user.id);
                MessagesController.getInstance(UserConfig.selectedAccount).putUser(user, false);
                ProfileActivity profileActivity = new ProfileActivity(bundle);
                presentFragment(profileActivity);
            }
        } else if (position == this.createNewLinkRow) {
            LinkEditActivity linkEditActivity = new LinkEditActivity(0, this.currentChatId);
            linkEditActivity.setCallback(this.linkEditActivityCallback);
            presentFragment(linkEditActivity);
        } else {
            int i = this.linksStartRow;
            if (position >= i && position < this.linksEndRow) {
                TLRPC.TL_chatInviteExported invite = this.invites.get(position - i);
                InviteLinkBottomSheet inviteLinkBottomSheet = new InviteLinkBottomSheet(context, invite, this.info, this.users, this, this.currentChatId, false, this.isChannel);
                this.inviteLinkBottomSheet = inviteLinkBottomSheet;
                inviteLinkBottomSheet.setCanEdit(this.canEdit);
                this.inviteLinkBottomSheet.show();
                return;
            }
            int i2 = this.revokedLinksStartRow;
            if (position >= i2 && position < this.revokedLinksEndRow) {
                TLRPC.TL_chatInviteExported invite2 = this.revokedInvites.get(position - i2);
                InviteLinkBottomSheet inviteLinkBottomSheet2 = new InviteLinkBottomSheet(context, invite2, this.info, this.users, this, this.currentChatId, false, this.isChannel);
                this.inviteLinkBottomSheet = inviteLinkBottomSheet2;
                inviteLinkBottomSheet2.show();
            } else if (position == this.revokeAllRow) {
                if (this.deletingRevokedLinks) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("DeleteAllRevokedLinks", R.string.DeleteAllRevokedLinks));
                builder.setMessage(LocaleController.getString("DeleteAllRevokedLinkHelp", R.string.DeleteAllRevokedLinkHelp));
                builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        ManageLinksActivity.this.m3885lambda$createView$8$orgtelegramuiManageLinksActivity(dialogInterface, i3);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            } else {
                int i3 = this.adminsStartRow;
                if (position >= i3 && position < this.adminsEndRow) {
                    int p = position - i3;
                    TLRPC.TL_chatAdminWithInvites admin = this.admins.get(p);
                    if (this.users.containsKey(Long.valueOf(admin.admin_id))) {
                        getMessagesController().putUser(this.users.get(Long.valueOf(admin.admin_id)), false);
                    }
                    ManageLinksActivity fragment = new ManageLinksActivity(this.currentChatId, admin.admin_id, admin.invites_count);
                    fragment.setInfo(this.info, null);
                    presentFragment(fragment);
                }
            }
        }
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3885lambda$createView$8$orgtelegramuiManageLinksActivity(DialogInterface dialogInterface2, int i2) {
        TLRPC.TL_messages_deleteRevokedExportedChatInvites req = new TLRPC.TL_messages_deleteRevokedExportedChatInvites();
        req.peer = getMessagesController().getInputPeer(-this.currentChatId);
        if (this.adminId == getUserConfig().getClientUserId()) {
            req.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
        } else {
            req.admin_id = getMessagesController().getInputUser(this.adminId);
        }
        this.deletingRevokedLinks = true;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda17
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ManageLinksActivity.this.m3884lambda$createView$7$orgtelegramuiManageLinksActivity(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3884lambda$createView$7$orgtelegramuiManageLinksActivity(TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                ManageLinksActivity.this.m3883lambda$createView$6$orgtelegramuiManageLinksActivity(error);
            }
        });
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3883lambda$createView$6$orgtelegramuiManageLinksActivity(TLRPC.TL_error error) {
        this.deletingRevokedLinks = false;
        if (error == null) {
            DiffCallback callback = saveListState();
            this.revokedInvites.clear();
            updateRecyclerViewAnimated(callback);
        }
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ boolean m3882lambda$createView$10$orgtelegramuiManageLinksActivity(View view, int position) {
        if ((position < this.linksStartRow || position >= this.linksEndRow) && (position < this.revokedLinksStartRow || position >= this.revokedLinksEndRow)) {
            return false;
        }
        LinkCell cell = (LinkCell) view;
        cell.optionsView.callOnClick();
        view.performHapticFeedback(0, 2);
        return true;
    }

    public void setInfo(TLRPC.ChatFull chatFull, TLRPC.ExportedChatInvite invite) {
        this.info = chatFull;
        this.invite = (TLRPC.TL_chatInviteExported) invite;
        this.isPublic = !TextUtils.isEmpty(this.currentChat.username);
        loadLinks(true);
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
            String str;
            int i;
            ManageLinksActivity.this = this$0;
            EmptyView emptyView = new EmptyView(context);
            this.emptyView = emptyView;
            addView(emptyView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_chats_message));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            TextView textView2 = this.messageTextView;
            if (this$0.isChannel) {
                i = R.string.PrimaryLinkHelpChannel;
                str = "PrimaryLinkHelpChannel";
            } else {
                i = R.string.PrimaryLinkHelp;
                str = "PrimaryLinkHelp";
            }
            textView2.setText(LocaleController.getString(str, i));
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 143.0f, 52.0f, 18.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), heightMeasureSpec);
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            ManageLinksActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            if (ManageLinksActivity.this.creatorRow == position || ManageLinksActivity.this.createNewLinkRow == position) {
                return true;
            }
            if (position >= ManageLinksActivity.this.linksStartRow && position < ManageLinksActivity.this.linksEndRow) {
                return true;
            }
            if ((position >= ManageLinksActivity.this.revokedLinksStartRow && position < ManageLinksActivity.this.revokedLinksEndRow) || position == ManageLinksActivity.this.revokeAllRow) {
                return true;
            }
            return position >= ManageLinksActivity.this.adminsStartRow && position < ManageLinksActivity.this.adminsEndRow;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ManageLinksActivity.this.rowCount;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 1:
                    View view2 = new HeaderCell(this.mContext, 23);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 2:
                    Context context = this.mContext;
                    ManageLinksActivity manageLinksActivity = ManageLinksActivity.this;
                    final LinkActionView linkActionView = new LinkActionView(context, manageLinksActivity, null, manageLinksActivity.currentChatId, true, ManageLinksActivity.this.isChannel);
                    linkActionView.setPermanent(true);
                    linkActionView.setDelegate(new LinkActionView.Delegate() { // from class: org.telegram.ui.ManageLinksActivity.ListAdapter.1
                        @Override // org.telegram.ui.Components.LinkActionView.Delegate
                        public /* synthetic */ void editLink() {
                            LinkActionView.Delegate.CC.$default$editLink(this);
                        }

                        @Override // org.telegram.ui.Components.LinkActionView.Delegate
                        public /* synthetic */ void removeLink() {
                            LinkActionView.Delegate.CC.$default$removeLink(this);
                        }

                        @Override // org.telegram.ui.Components.LinkActionView.Delegate
                        public void revokeLink() {
                            ManageLinksActivity.this.revokePermanent();
                        }

                        @Override // org.telegram.ui.Components.LinkActionView.Delegate
                        public void showUsersForPermanentLink() {
                            ManageLinksActivity.this.inviteLinkBottomSheet = new InviteLinkBottomSheet(linkActionView.getContext(), ManageLinksActivity.this.invite, ManageLinksActivity.this.info, ManageLinksActivity.this.users, ManageLinksActivity.this, ManageLinksActivity.this.currentChatId, true, ManageLinksActivity.this.isChannel);
                            ManageLinksActivity.this.inviteLinkBottomSheet.show();
                        }
                    });
                    View view3 = linkActionView;
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view3;
                    break;
                case 3:
                    View view4 = new CreationTextCell(this.mContext);
                    view4.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view4;
                    break;
                case 4:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 5:
                    view = new LinkCell(this.mContext);
                    break;
                case 6:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(9);
                    flickerLoadingView.showDate(false);
                    View view5 = flickerLoadingView;
                    view5.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view5;
                    break;
                case 7:
                    View view6 = new ShadowSectionCell(this.mContext);
                    view6.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    view = view6;
                    break;
                case 8:
                    TextSettingsCell revokeAll = new TextSettingsCell(this.mContext);
                    revokeAll.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    revokeAll.setText(LocaleController.getString("DeleteAllRevokedLinks", R.string.DeleteAllRevokedLinks), false);
                    revokeAll.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
                    view = revokeAll;
                    break;
                case 9:
                    TextInfoPrivacyCell cell = new TextInfoPrivacyCell(this.mContext);
                    cell.setText(LocaleController.getString("CreateNewLinkHelp", R.string.CreateNewLinkHelp));
                    cell.setBackground(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    view = cell;
                    break;
                case 10:
                    ManageChatUserCell userCell = new ManageChatUserCell(this.mContext, 8, 6, false);
                    userCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = userCell;
                    break;
                default:
                    View view7 = new HintInnerCell(this.mContext);
                    view7.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundWhite));
                    view = view7;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.TL_chatInviteExported invite;
            int p;
            TLRPC.User user;
            switch (holder.getItemViewType()) {
                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == ManageLinksActivity.this.permanentLinkHeaderRow) {
                        if (!ManageLinksActivity.this.isPublic || ManageLinksActivity.this.adminId != ManageLinksActivity.this.getAccountInstance().getUserConfig().clientUserId) {
                            if (ManageLinksActivity.this.adminId == ManageLinksActivity.this.getAccountInstance().getUserConfig().clientUserId) {
                                headerCell.setText(LocaleController.getString("ChannelInviteLinkTitle", R.string.ChannelInviteLinkTitle));
                                return;
                            } else {
                                headerCell.setText(LocaleController.getString("PermanentLinkForThisAdmin", R.string.PermanentLinkForThisAdmin));
                                return;
                            }
                        }
                        headerCell.setText(LocaleController.getString("PublicLink", R.string.PublicLink));
                        return;
                    } else if (position != ManageLinksActivity.this.revokedHeader) {
                        if (position != ManageLinksActivity.this.linksHeaderRow) {
                            if (position == ManageLinksActivity.this.adminsHeaderRow) {
                                headerCell.setText(LocaleController.getString("LinksCreatedByOtherAdmins", R.string.LinksCreatedByOtherAdmins));
                                return;
                            }
                            return;
                        }
                        headerCell.setText(LocaleController.getString("LinksCreatedByThisAdmin", R.string.LinksCreatedByThisAdmin));
                        return;
                    } else {
                        headerCell.setText(LocaleController.getString("RevokedLinks", R.string.RevokedLinks));
                        return;
                    }
                case 2:
                    LinkActionView linkActionView = (LinkActionView) holder.itemView;
                    linkActionView.setCanEdit(ManageLinksActivity.this.adminId == ManageLinksActivity.this.getAccountInstance().getUserConfig().clientUserId);
                    if (!ManageLinksActivity.this.isPublic || ManageLinksActivity.this.adminId != ManageLinksActivity.this.getAccountInstance().getUserConfig().clientUserId) {
                        linkActionView.hideRevokeOption(!ManageLinksActivity.this.canEdit);
                        if (ManageLinksActivity.this.invite != null) {
                            TLRPC.TL_chatInviteExported inviteExported = ManageLinksActivity.this.invite;
                            linkActionView.setLink(inviteExported.link);
                            linkActionView.loadUsers(inviteExported, ManageLinksActivity.this.currentChatId);
                            return;
                        }
                        linkActionView.setLink(null);
                        linkActionView.loadUsers(null, ManageLinksActivity.this.currentChatId);
                        return;
                    } else if (ManageLinksActivity.this.info != null) {
                        linkActionView.setLink("https://t.me/" + ManageLinksActivity.this.currentChat.username);
                        linkActionView.setUsers(0, null);
                        linkActionView.hideRevokeOption(true);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    CreationTextCell textCell = (CreationTextCell) holder.itemView;
                    Drawable drawable1 = this.mContext.getResources().getDrawable(R.drawable.poll_add_circle);
                    Drawable drawable2 = this.mContext.getResources().getDrawable(R.drawable.poll_add_plus);
                    drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_switchTrackChecked), PorterDuff.Mode.MULTIPLY));
                    drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_checkboxCheck), PorterDuff.Mode.MULTIPLY));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(drawable1, drawable2);
                    textCell.setTextAndIcon(LocaleController.getString("CreateNewLink", R.string.CreateNewLink), combinedDrawable, true ^ ManageLinksActivity.this.invites.isEmpty());
                    return;
                case 5:
                    boolean drawDivider = true;
                    if (position < ManageLinksActivity.this.linksStartRow || position >= ManageLinksActivity.this.linksEndRow) {
                        invite = (TLRPC.TL_chatInviteExported) ManageLinksActivity.this.revokedInvites.get(position - ManageLinksActivity.this.revokedLinksStartRow);
                        if (position == ManageLinksActivity.this.revokedLinksEndRow - 1) {
                            drawDivider = false;
                        }
                    } else {
                        invite = (TLRPC.TL_chatInviteExported) ManageLinksActivity.this.invites.get(position - ManageLinksActivity.this.linksStartRow);
                        if (position == ManageLinksActivity.this.linksEndRow - 1) {
                            drawDivider = false;
                        }
                    }
                    LinkCell cell = (LinkCell) holder.itemView;
                    cell.setLink(invite, position - ManageLinksActivity.this.linksStartRow);
                    cell.drawDivider = drawDivider;
                    return;
                case 10:
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    boolean drawDivider2 = true;
                    if (position == ManageLinksActivity.this.creatorRow) {
                        user = ManageLinksActivity.this.getMessagesController().getUser(Long.valueOf(ManageLinksActivity.this.adminId));
                        p = ManageLinksActivity.this.invitesCount;
                        drawDivider2 = false;
                    } else {
                        int p2 = position - ManageLinksActivity.this.adminsStartRow;
                        TLRPC.TL_chatAdminWithInvites admin = (TLRPC.TL_chatAdminWithInvites) ManageLinksActivity.this.admins.get(p2);
                        TLRPC.User user2 = (TLRPC.User) ManageLinksActivity.this.users.get(Long.valueOf(admin.admin_id));
                        int count = admin.invites_count;
                        if (position != ManageLinksActivity.this.adminsEndRow - 1) {
                            user = user2;
                            p = count;
                        } else {
                            drawDivider2 = false;
                            user = user2;
                            p = count;
                        }
                    }
                    if (user != null) {
                        userCell.setData(user, ContactsController.formatName(user.first_name, user.last_name), LocaleController.formatPluralString("InviteLinkCount", p, new Object[0]), drawDivider2);
                        return;
                    }
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
            if (position != ManageLinksActivity.this.helpRow) {
                if (position == ManageLinksActivity.this.permanentLinkHeaderRow || position == ManageLinksActivity.this.revokedHeader || position == ManageLinksActivity.this.adminsHeaderRow || position == ManageLinksActivity.this.linksHeaderRow) {
                    return 1;
                }
                if (position != ManageLinksActivity.this.permanentLinkRow) {
                    if (position != ManageLinksActivity.this.createNewLinkRow) {
                        if (position != ManageLinksActivity.this.dividerRow && position != ManageLinksActivity.this.revokedDivider && position != ManageLinksActivity.this.revokeAllDivider && position != ManageLinksActivity.this.creatorDividerRow && position != ManageLinksActivity.this.adminsDividerRow) {
                            if (position < ManageLinksActivity.this.linksStartRow || position >= ManageLinksActivity.this.linksEndRow) {
                                if (position < ManageLinksActivity.this.revokedLinksStartRow || position >= ManageLinksActivity.this.revokedLinksEndRow) {
                                    if (position != ManageLinksActivity.this.linksLoadingRow) {
                                        if (position != ManageLinksActivity.this.lastDivider) {
                                            if (position != ManageLinksActivity.this.revokeAllRow) {
                                                if (position != ManageLinksActivity.this.createLinkHelpRow) {
                                                    if (position == ManageLinksActivity.this.creatorRow) {
                                                        return 10;
                                                    }
                                                    return (position < ManageLinksActivity.this.adminsStartRow || position >= ManageLinksActivity.this.adminsEndRow) ? 1 : 10;
                                                }
                                                return 9;
                                            }
                                            return 8;
                                        }
                                        return 7;
                                    }
                                    return 6;
                                }
                                return 5;
                            }
                            return 5;
                        }
                        return 4;
                    }
                    return 3;
                }
                return 2;
            }
            return 0;
        }
    }

    public void revokePermanent() {
        if (this.adminId == getAccountInstance().getUserConfig().clientUserId) {
            TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
            req.peer = getMessagesController().getInputPeer(-this.currentChatId);
            req.legacy_revoke_permanent = true;
            final TLRPC.TL_chatInviteExported oldInvite = this.invite;
            this.invite = null;
            this.info.exported_invite = null;
            int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ManageLinksActivity.this.m3899lambda$revokePermanent$12$orgtelegramuiManageLinksActivity(oldInvite, tLObject, tL_error);
                }
            });
            AndroidUtilities.updateVisibleRows(this.listView);
            getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
            return;
        }
        revokeLink(this.invite);
    }

    /* renamed from: lambda$revokePermanent$12$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3899lambda$revokePermanent$12$orgtelegramuiManageLinksActivity(final TLRPC.TL_chatInviteExported oldInvite, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                ManageLinksActivity.this.m3898lambda$revokePermanent$11$orgtelegramuiManageLinksActivity(error, response, oldInvite);
            }
        });
    }

    /* renamed from: lambda$revokePermanent$11$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3898lambda$revokePermanent$11$orgtelegramuiManageLinksActivity(TLRPC.TL_error error, TLObject response, TLRPC.TL_chatInviteExported oldInvite) {
        if (error == null) {
            TLRPC.TL_chatInviteExported tL_chatInviteExported = (TLRPC.TL_chatInviteExported) response;
            this.invite = tL_chatInviteExported;
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null) {
                chatFull.exported_invite = tL_chatInviteExported;
            }
            if (getParentActivity() == null) {
                return;
            }
            oldInvite.revoked = true;
            DiffCallback callback = saveListState();
            this.revokedInvites.add(0, oldInvite);
            updateRecyclerViewAnimated(callback);
            BulletinFactory.of(this).createSimpleBulletin(R.raw.linkbroken, LocaleController.getString("InviteRevokedHint", R.string.InviteRevokedHint)).show();
        }
    }

    /* loaded from: classes4.dex */
    public class LinkCell extends FrameLayout {
        private static final int LINK_STATE_BLUE = 0;
        private static final int LINK_STATE_GRAY = 4;
        private static final int LINK_STATE_GREEN = 1;
        private static final int LINK_STATE_RED = 3;
        private static final int LINK_STATE_YELLOW = 2;
        int animateFromState;
        boolean animateHideExpiring;
        boolean drawDivider;
        TLRPC.TL_chatInviteExported invite;
        float lastDrawExpringProgress;
        int lastDrawingState;
        ImageView optionsView;
        int position;
        TextView subtitleView;
        boolean timerRunning;
        TextView titleView;
        Paint paint = new Paint(1);
        Paint paint2 = new Paint(1);
        RectF rectF = new RectF();
        float animateToStateProgress = 1.0f;
        private TimerParticles timerParticles = new TimerParticles();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LinkCell(Context context) {
            super(context);
            ManageLinksActivity.this = r9;
            this.paint2.setStyle(Paint.Style.STROKE);
            this.paint2.setStrokeCap(Paint.Cap.ROUND);
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 16, 70.0f, 0.0f, 30.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 16.0f);
            this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.titleView.setLines(1);
            this.titleView.setEllipsize(TextUtils.TruncateAt.END);
            TextView textView2 = new TextView(context);
            this.subtitleView = textView2;
            textView2.setTextSize(1, 13.0f);
            this.subtitleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            linearLayout.addView(this.titleView, LayoutHelper.createLinear(-1, -2));
            linearLayout.addView(this.subtitleView, LayoutHelper.createLinear(-1, -2, 0.0f, 6.0f, 0.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.optionsView = imageView;
            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_ab_other));
            this.optionsView.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsView.setColorFilter(Theme.getColor(Theme.key_stickers_menu));
            this.optionsView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ManageLinksActivity$LinkCell$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ManageLinksActivity.LinkCell.this.m3904lambda$new$3$orgtelegramuiManageLinksActivity$LinkCell(view);
                }
            });
            this.optionsView.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 1));
            addView(this.optionsView, LayoutHelper.createFrame(40, 48, 21));
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            setWillNotDraw(false);
        }

        /* renamed from: lambda$new$3$org-telegram-ui-ManageLinksActivity$LinkCell */
        public /* synthetic */ void m3904lambda$new$3$orgtelegramuiManageLinksActivity$LinkCell(View view) {
            if (this.invite == null) {
                return;
            }
            ArrayList<String> items = new ArrayList<>();
            ArrayList<Integer> icons = new ArrayList<>();
            final ArrayList<Integer> actions = new ArrayList<>();
            boolean redLastItem = false;
            if (this.invite.revoked) {
                items.add(LocaleController.getString("Delete", R.string.Delete));
                icons.add(Integer.valueOf((int) R.drawable.msg_delete));
                actions.add(4);
                redLastItem = true;
            } else {
                items.add(LocaleController.getString("CopyLink", R.string.CopyLink));
                icons.add(Integer.valueOf((int) R.drawable.msg_copy));
                actions.add(0);
                items.add(LocaleController.getString("ShareLink", R.string.ShareLink));
                icons.add(Integer.valueOf((int) R.drawable.msg_share));
                actions.add(1);
                if (!this.invite.permanent && ManageLinksActivity.this.canEdit) {
                    items.add(LocaleController.getString("EditLink", R.string.EditLink));
                    icons.add(Integer.valueOf((int) R.drawable.msg_edit));
                    actions.add(2);
                }
                if (ManageLinksActivity.this.canEdit) {
                    items.add(LocaleController.getString("RevokeLink", R.string.RevokeLink));
                    icons.add(Integer.valueOf((int) R.drawable.msg_delete));
                    actions.add(3);
                    redLastItem = true;
                }
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(ManageLinksActivity.this.getParentActivity());
            builder.setItems((CharSequence[]) items.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(icons), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ManageLinksActivity$LinkCell$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ManageLinksActivity.LinkCell.this.m3903lambda$new$2$orgtelegramuiManageLinksActivity$LinkCell(actions, dialogInterface, i);
                }
            });
            builder.setTitle(LocaleController.getString("InviteLink", R.string.InviteLink));
            AlertDialog alert = builder.create();
            builder.show();
            if (redLastItem) {
                alert.setItemColor(items.size() - 1, Theme.getColor(Theme.key_dialogTextRed2), Theme.getColor(Theme.key_dialogRedIcon));
            }
        }

        /* renamed from: lambda$new$2$org-telegram-ui-ManageLinksActivity$LinkCell */
        public /* synthetic */ void m3903lambda$new$2$orgtelegramuiManageLinksActivity$LinkCell(ArrayList actions, DialogInterface dialogInterface, int i) {
            switch (((Integer) actions.get(i)).intValue()) {
                case 0:
                    try {
                        if (this.invite.link == null) {
                            return;
                        }
                        ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                        ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, this.invite.link);
                        clipboard.setPrimaryClip(clip);
                        BulletinFactory.createCopyLinkBulletin(ManageLinksActivity.this).show();
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        return;
                    }
                case 1:
                    try {
                        if (this.invite.link == null) {
                            return;
                        }
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType(ErrorAttachmentLog.CONTENT_TYPE_TEXT_PLAIN);
                        intent.putExtra("android.intent.extra.TEXT", this.invite.link);
                        ManageLinksActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("InviteToGroupByLink", R.string.InviteToGroupByLink)), 500);
                        return;
                    } catch (Exception e2) {
                        FileLog.e(e2);
                        return;
                    }
                case 2:
                    ManageLinksActivity.this.editLink(this.invite);
                    return;
                case 3:
                    final TLRPC.TL_chatInviteExported inviteFinal = this.invite;
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(ManageLinksActivity.this.getParentActivity());
                    builder2.setMessage(LocaleController.getString("RevokeAlert", R.string.RevokeAlert));
                    builder2.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
                    builder2.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ManageLinksActivity$LinkCell$$ExternalSyntheticLambda1
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface2, int i2) {
                            ManageLinksActivity.LinkCell.this.m3901lambda$new$0$orgtelegramuiManageLinksActivity$LinkCell(inviteFinal, dialogInterface2, i2);
                        }
                    });
                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    ManageLinksActivity.this.showDialog(builder2.create());
                    return;
                case 4:
                    final TLRPC.TL_chatInviteExported inviteFinal2 = this.invite;
                    AlertDialog.Builder builder22 = new AlertDialog.Builder(ManageLinksActivity.this.getParentActivity());
                    builder22.setTitle(LocaleController.getString("DeleteLink", R.string.DeleteLink));
                    builder22.setMessage(LocaleController.getString("DeleteLinkHelp", R.string.DeleteLinkHelp));
                    builder22.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ManageLinksActivity$LinkCell$$ExternalSyntheticLambda2
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface2, int i2) {
                            ManageLinksActivity.LinkCell.this.m3902lambda$new$1$orgtelegramuiManageLinksActivity$LinkCell(inviteFinal2, dialogInterface2, i2);
                        }
                    });
                    builder22.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    ManageLinksActivity.this.showDialog(builder22.create());
                    return;
                default:
                    return;
            }
        }

        /* renamed from: lambda$new$0$org-telegram-ui-ManageLinksActivity$LinkCell */
        public /* synthetic */ void m3901lambda$new$0$orgtelegramuiManageLinksActivity$LinkCell(TLRPC.TL_chatInviteExported inviteFinal, DialogInterface dialogInterface2, int i2) {
            ManageLinksActivity.this.revokeLink(inviteFinal);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-ManageLinksActivity$LinkCell */
        public /* synthetic */ void m3902lambda$new$1$orgtelegramuiManageLinksActivity$LinkCell(TLRPC.TL_chatInviteExported inviteFinal, DialogInterface dialogInterface2, int i2) {
            ManageLinksActivity.this.deleteLink(inviteFinal);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), C.BUFFER_FLAG_ENCRYPTED));
            this.paint2.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }

        /* JADX WARN: Removed duplicated region for block: B:48:0x00e9  */
        /* JADX WARN: Removed duplicated region for block: B:54:0x0101  */
        /* JADX WARN: Removed duplicated region for block: B:55:0x0113  */
        /* JADX WARN: Removed duplicated region for block: B:67:0x014c  */
        /* JADX WARN: Removed duplicated region for block: B:68:0x0150  */
        /* JADX WARN: Removed duplicated region for block: B:83:0x020c  */
        /* JADX WARN: Removed duplicated region for block: B:87:0x0219  */
        /* JADX WARN: Removed duplicated region for block: B:88:0x023e  */
        /* JADX WARN: Removed duplicated region for block: B:91:0x0266  */
        /* JADX WARN: Removed duplicated region for block: B:93:? A[RETURN, SYNTHETIC] */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onDraw(android.graphics.Canvas r24) {
            /*
                Method dump skipped, instructions count: 653
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ManageLinksActivity.LinkCell.onDraw(android.graphics.Canvas):void");
        }

        private boolean hasProgress(int state) {
            return state == 2 || state == 1;
        }

        private int getColor(int state, float progress) {
            if (state == 3) {
                return Theme.getColor(Theme.key_chat_attachAudioBackground);
            }
            if (state == 1) {
                if (progress > 0.5f) {
                    float p = (progress - 0.5f) / 0.5f;
                    return ColorUtils.blendARGB(Theme.getColor(Theme.key_chat_attachLocationBackground), Theme.getColor(Theme.key_chat_attachPollBackground), 1.0f - p);
                }
                float p2 = progress / 0.5f;
                return ColorUtils.blendARGB(Theme.getColor(Theme.key_chat_attachPollBackground), Theme.getColor(Theme.key_chat_attachAudioBackground), 1.0f - p2);
            } else if (state == 2) {
                return Theme.getColor(Theme.key_chat_attachPollBackground);
            } else {
                if (state == 4) {
                    return Theme.getColor(Theme.key_chats_unreadCounterMuted);
                }
                return Theme.getColor(Theme.key_featuredStickers_addButton);
            }
        }

        public void setLink(TLRPC.TL_chatInviteExported invite, int position) {
            String str;
            int i;
            this.timerRunning = false;
            TLRPC.TL_chatInviteExported tL_chatInviteExported = this.invite;
            if (tL_chatInviteExported == null || invite == null || !tL_chatInviteExported.link.equals(invite.link)) {
                this.lastDrawingState = -1;
                this.animateToStateProgress = 1.0f;
            }
            this.invite = invite;
            this.position = position;
            if (invite == null) {
                return;
            }
            if (!TextUtils.isEmpty(invite.title)) {
                SpannableStringBuilder builder = new SpannableStringBuilder(invite.title);
                Emoji.replaceEmoji(builder, this.titleView.getPaint().getFontMetricsInt(), (int) this.titleView.getPaint().getTextSize(), false);
                this.titleView.setText(builder);
            } else if (invite.link.startsWith("https://t.me/+")) {
                this.titleView.setText(invite.link.substring("https://t.me/+".length()));
            } else if (invite.link.startsWith("https://t.me/joinchat/")) {
                this.titleView.setText(invite.link.substring("https://t.me/joinchat/".length()));
            } else if (invite.link.startsWith("https://")) {
                this.titleView.setText(invite.link.substring("https://".length()));
            } else {
                this.titleView.setText(invite.link);
            }
            String joinedString = "";
            if (invite.usage == 0 && invite.usage_limit == 0 && invite.requested == 0) {
                joinedString = LocaleController.getString("NoOneJoinedYet", R.string.NoOneJoinedYet);
            } else if (invite.usage_limit > 0 && invite.usage == 0 && !invite.expired && !invite.revoked) {
                joinedString = LocaleController.formatPluralString("CanJoin", invite.usage_limit, new Object[0]);
            } else if (invite.usage_limit > 0 && invite.expired && invite.revoked) {
                joinedString = LocaleController.formatPluralString("PeopleJoined", invite.usage, new Object[0]) + ", " + LocaleController.formatPluralString("PeopleJoinedRemaining", invite.usage_limit - invite.usage, new Object[0]);
            } else {
                if (invite.usage > 0) {
                    joinedString = LocaleController.formatPluralString("PeopleJoined", invite.usage, new Object[0]);
                }
                if (invite.requested > 0) {
                    if (invite.usage > 0) {
                        joinedString = joinedString + ", ";
                    }
                    joinedString = joinedString + LocaleController.formatPluralString("JoinRequests", invite.requested, new Object[0]);
                }
            }
            if (invite.permanent && !invite.revoked) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(joinedString);
                DotDividerSpan dotDividerSpan = new DotDividerSpan();
                dotDividerSpan.setTopPadding(AndroidUtilities.dp(1.5f));
                spannableStringBuilder.append((CharSequence) "  .  ").setSpan(dotDividerSpan, spannableStringBuilder.length() - 3, spannableStringBuilder.length() - 2, 0);
                spannableStringBuilder.append((CharSequence) LocaleController.getString("Permanent", R.string.Permanent));
                this.subtitleView.setText(spannableStringBuilder);
            } else if (invite.expired || invite.revoked) {
                if (invite.revoked && invite.usage == 0) {
                    joinedString = LocaleController.getString("NoOneJoined", R.string.NoOneJoined);
                }
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(joinedString);
                DotDividerSpan dotDividerSpan2 = new DotDividerSpan();
                dotDividerSpan2.setTopPadding(AndroidUtilities.dp(1.5f));
                spannableStringBuilder2.append((CharSequence) "  .  ").setSpan(dotDividerSpan2, spannableStringBuilder2.length() - 3, spannableStringBuilder2.length() - 2, 0);
                if (!invite.revoked && invite.usage_limit > 0 && invite.usage >= invite.usage_limit) {
                    spannableStringBuilder2.append((CharSequence) LocaleController.getString("LinkLimitReached", R.string.LinkLimitReached));
                } else {
                    if (invite.revoked) {
                        i = R.string.Revoked;
                        str = "Revoked";
                    } else {
                        i = R.string.Expired;
                        str = TimerBuilder.EXPIRED;
                    }
                    spannableStringBuilder2.append((CharSequence) LocaleController.getString(str, i));
                }
                this.subtitleView.setText(spannableStringBuilder2);
            } else if (invite.expire_date > 0) {
                SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(joinedString);
                DotDividerSpan dotDividerSpan3 = new DotDividerSpan();
                dotDividerSpan3.setTopPadding(AndroidUtilities.dp(1.5f));
                spannableStringBuilder3.append((CharSequence) "  .  ").setSpan(dotDividerSpan3, spannableStringBuilder3.length() - 3, spannableStringBuilder3.length() - 2, 0);
                long currentTime = System.currentTimeMillis() + (ManageLinksActivity.this.timeDif * 1000);
                long expireTime = invite.expire_date * 1000;
                long timeLeft = expireTime - currentTime;
                if (timeLeft < 0) {
                    timeLeft = 0;
                }
                if (timeLeft > 86400000) {
                    spannableStringBuilder3.append((CharSequence) LocaleController.formatPluralString("DaysLeft", (int) (timeLeft / 86400000), new Object[0]));
                } else {
                    int s = (int) ((timeLeft / 1000) % 60);
                    int m = (int) (((timeLeft / 1000) / 60) % 60);
                    int h = (int) (((timeLeft / 1000) / 60) / 60);
                    spannableStringBuilder3.append((CharSequence) String.format(Locale.ENGLISH, "%02d", Integer.valueOf(h))).append((CharSequence) String.format(Locale.ENGLISH, ":%02d", Integer.valueOf(m))).append((CharSequence) String.format(Locale.ENGLISH, ":%02d", Integer.valueOf(s)));
                    this.timerRunning = true;
                }
                this.subtitleView.setText(spannableStringBuilder3);
            } else {
                this.subtitleView.setText(joinedString);
            }
        }
    }

    public void deleteLink(final TLRPC.TL_chatInviteExported invite) {
        TLRPC.TL_messages_deleteExportedChatInvite req = new TLRPC.TL_messages_deleteExportedChatInvite();
        req.link = invite.link;
        req.peer = getMessagesController().getInputPeer(-this.currentChatId);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ManageLinksActivity.this.m3888lambda$deleteLink$14$orgtelegramuiManageLinksActivity(invite, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$deleteLink$14$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3888lambda$deleteLink$14$orgtelegramuiManageLinksActivity(final TLRPC.TL_chatInviteExported invite, TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                ManageLinksActivity.this.m3887lambda$deleteLink$13$orgtelegramuiManageLinksActivity(error, invite);
            }
        });
    }

    /* renamed from: lambda$deleteLink$13$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3887lambda$deleteLink$13$orgtelegramuiManageLinksActivity(TLRPC.TL_error error, TLRPC.TL_chatInviteExported invite) {
        if (error == null) {
            this.linkEditActivityCallback.onLinkRemoved(invite);
        }
    }

    public void editLink(TLRPC.TL_chatInviteExported invite) {
        LinkEditActivity activity = new LinkEditActivity(1, this.currentChatId);
        activity.setCallback(this.linkEditActivityCallback);
        activity.setInviteToEdit(invite);
        presentFragment(activity);
    }

    public void revokeLink(final TLRPC.TL_chatInviteExported invite) {
        TLRPC.TL_messages_editExportedChatInvite req = new TLRPC.TL_messages_editExportedChatInvite();
        req.link = invite.link;
        req.revoked = true;
        req.peer = getMessagesController().getInputPeer(-this.currentChatId);
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ManageLinksActivity.this.m3897lambda$revokeLink$16$orgtelegramuiManageLinksActivity(invite, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$revokeLink$16$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3897lambda$revokeLink$16$orgtelegramuiManageLinksActivity(final TLRPC.TL_chatInviteExported invite, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                ManageLinksActivity.this.m3896lambda$revokeLink$15$orgtelegramuiManageLinksActivity(error, response, invite);
            }
        });
    }

    /* renamed from: lambda$revokeLink$15$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3896lambda$revokeLink$15$orgtelegramuiManageLinksActivity(TLRPC.TL_error error, TLObject response, TLRPC.TL_chatInviteExported invite) {
        if (error == null) {
            if (response instanceof TLRPC.TL_messages_exportedChatInviteReplaced) {
                TLRPC.TL_messages_exportedChatInviteReplaced replaced = (TLRPC.TL_messages_exportedChatInviteReplaced) response;
                if (!this.isPublic) {
                    this.invite = (TLRPC.TL_chatInviteExported) replaced.new_invite;
                }
                invite.revoked = true;
                DiffCallback callback = saveListState();
                if (this.isPublic && this.adminId == getAccountInstance().getUserConfig().getClientUserId()) {
                    this.invites.remove(invite);
                    this.invites.add(0, (TLRPC.TL_chatInviteExported) replaced.new_invite);
                } else if (this.invite != null) {
                    this.invite = (TLRPC.TL_chatInviteExported) replaced.new_invite;
                }
                this.revokedInvites.add(0, invite);
                updateRecyclerViewAnimated(callback);
            } else {
                this.linkEditActivityCallback.onLinkEdited(invite, response);
                TLRPC.ChatFull chatFull = this.info;
                if (chatFull != null) {
                    chatFull.invitesCount--;
                    if (this.info.invitesCount < 0) {
                        this.info.invitesCount = 0;
                    }
                    getMessagesStorage().saveChatLinksCount(this.currentChatId, this.info.invitesCount);
                }
            }
            if (getParentActivity() != null) {
                BulletinFactory.of(this).createSimpleBulletin(R.raw.linkbroken, LocaleController.getString("InviteRevokedHint", R.string.InviteRevokedHint)).show();
            }
        }
    }

    /* renamed from: org.telegram.ui.ManageLinksActivity$6 */
    /* loaded from: classes4.dex */
    public class AnonymousClass6 implements LinkEditActivity.Callback {
        AnonymousClass6() {
            ManageLinksActivity.this = this$0;
        }

        @Override // org.telegram.ui.LinkEditActivity.Callback
        public void onLinkCreated(final TLObject response) {
            if (response instanceof TLRPC.TL_chatInviteExported) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ManageLinksActivity$6$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ManageLinksActivity.AnonymousClass6.this.m3900lambda$onLinkCreated$0$orgtelegramuiManageLinksActivity$6(response);
                    }
                }, 200L);
            }
        }

        /* renamed from: lambda$onLinkCreated$0$org-telegram-ui-ManageLinksActivity$6 */
        public /* synthetic */ void m3900lambda$onLinkCreated$0$orgtelegramuiManageLinksActivity$6(TLObject response) {
            DiffCallback callback = ManageLinksActivity.this.saveListState();
            ManageLinksActivity.this.invites.add(0, (TLRPC.TL_chatInviteExported) response);
            if (ManageLinksActivity.this.info != null) {
                ManageLinksActivity.this.info.invitesCount++;
                ManageLinksActivity.this.getMessagesStorage().saveChatLinksCount(ManageLinksActivity.this.currentChatId, ManageLinksActivity.this.info.invitesCount);
            }
            ManageLinksActivity.this.updateRecyclerViewAnimated(callback);
        }

        @Override // org.telegram.ui.LinkEditActivity.Callback
        public void onLinkEdited(TLRPC.TL_chatInviteExported inviteToEdit, TLObject response) {
            if (response instanceof TLRPC.TL_messages_exportedChatInvite) {
                TLRPC.TL_chatInviteExported edited = (TLRPC.TL_chatInviteExported) ((TLRPC.TL_messages_exportedChatInvite) response).invite;
                ManageLinksActivity.this.fixDate(edited);
                for (int i = 0; i < ManageLinksActivity.this.invites.size(); i++) {
                    if (((TLRPC.TL_chatInviteExported) ManageLinksActivity.this.invites.get(i)).link.equals(inviteToEdit.link)) {
                        if (edited.revoked) {
                            DiffCallback callback = ManageLinksActivity.this.saveListState();
                            ManageLinksActivity.this.invites.remove(i);
                            ManageLinksActivity.this.revokedInvites.add(0, edited);
                            ManageLinksActivity.this.updateRecyclerViewAnimated(callback);
                            return;
                        } else {
                            ManageLinksActivity.this.invites.set(i, edited);
                            ManageLinksActivity.this.updateRows(true);
                            return;
                        }
                    }
                }
            }
        }

        @Override // org.telegram.ui.LinkEditActivity.Callback
        public void onLinkRemoved(TLRPC.TL_chatInviteExported removedInvite) {
            for (int i = 0; i < ManageLinksActivity.this.revokedInvites.size(); i++) {
                if (((TLRPC.TL_chatInviteExported) ManageLinksActivity.this.revokedInvites.get(i)).link.equals(removedInvite.link)) {
                    DiffCallback callback = ManageLinksActivity.this.saveListState();
                    ManageLinksActivity.this.revokedInvites.remove(i);
                    ManageLinksActivity.this.updateRecyclerViewAnimated(callback);
                    return;
                }
            }
        }

        @Override // org.telegram.ui.LinkEditActivity.Callback
        public void revokeLink(TLRPC.TL_chatInviteExported inviteFinal) {
            ManageLinksActivity.this.revokeLink(inviteFinal);
        }
    }

    public void updateRecyclerViewAnimated(DiffCallback callback) {
        if (this.isPaused || this.listViewAdapter == null || this.listView == null) {
            updateRows(true);
            return;
        }
        updateRows(false);
        callback.fillPositions(callback.newPositionToItem);
        DiffUtil.calculateDiff(callback).dispatchUpdatesTo(this.listViewAdapter);
        AndroidUtilities.updateVisibleRows(this.listView);
    }

    /* loaded from: classes4.dex */
    public class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        int oldAdminsEndRow;
        int oldAdminsStartRow;
        ArrayList<TLRPC.TL_chatInviteExported> oldLinks;
        int oldLinksEndRow;
        int oldLinksStartRow;
        SparseIntArray oldPositionToItem;
        ArrayList<TLRPC.TL_chatInviteExported> oldRevokedLinks;
        int oldRevokedLinksEndRow;
        int oldRevokedLinksStartRow;
        int oldRowCount;

        private DiffCallback() {
            ManageLinksActivity.this = r1;
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
            this.oldLinks = new ArrayList<>();
            this.oldRevokedLinks = new ArrayList<>();
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getOldListSize() {
            return this.oldRowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getNewListSize() {
            return ManageLinksActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            TLRPC.TL_chatInviteExported oldItem;
            if (((oldItemPosition >= this.oldLinksStartRow && oldItemPosition < this.oldLinksEndRow) || (oldItemPosition >= this.oldRevokedLinksStartRow && oldItemPosition < this.oldRevokedLinksEndRow)) && ((newItemPosition >= ManageLinksActivity.this.linksStartRow && newItemPosition < ManageLinksActivity.this.linksEndRow) || (newItemPosition >= ManageLinksActivity.this.revokedLinksStartRow && newItemPosition < ManageLinksActivity.this.revokedLinksEndRow))) {
                TLRPC.TL_chatInviteExported newItem = (newItemPosition < ManageLinksActivity.this.linksStartRow || newItemPosition >= ManageLinksActivity.this.linksEndRow) ? (TLRPC.TL_chatInviteExported) ManageLinksActivity.this.revokedInvites.get(newItemPosition - ManageLinksActivity.this.revokedLinksStartRow) : (TLRPC.TL_chatInviteExported) ManageLinksActivity.this.invites.get(newItemPosition - ManageLinksActivity.this.linksStartRow);
                int i = this.oldLinksStartRow;
                if (oldItemPosition >= i && oldItemPosition < this.oldLinksEndRow) {
                    oldItem = this.oldLinks.get(oldItemPosition - i);
                } else {
                    oldItem = this.oldRevokedLinks.get(oldItemPosition - this.oldRevokedLinksStartRow);
                }
                return oldItem.link.equals(newItem.link);
            } else if (oldItemPosition >= this.oldAdminsStartRow && oldItemPosition < this.oldAdminsEndRow && newItemPosition >= ManageLinksActivity.this.adminsStartRow && newItemPosition < ManageLinksActivity.this.adminsEndRow) {
                return oldItemPosition - this.oldAdminsStartRow == newItemPosition - ManageLinksActivity.this.adminsStartRow;
            } else {
                int oldItem2 = this.oldPositionToItem.get(oldItemPosition, -1);
                int newItem2 = this.newPositionToItem.get(newItemPosition, -1);
                return oldItem2 >= 0 && oldItem2 == newItem2;
            }
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return areItemsTheSame(oldItemPosition, newItemPosition);
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            int pointer = 0 + 1;
            put(pointer, ManageLinksActivity.this.helpRow, sparseIntArray);
            int pointer2 = pointer + 1;
            put(pointer2, ManageLinksActivity.this.permanentLinkHeaderRow, sparseIntArray);
            int pointer3 = pointer2 + 1;
            put(pointer3, ManageLinksActivity.this.permanentLinkRow, sparseIntArray);
            int pointer4 = pointer3 + 1;
            put(pointer4, ManageLinksActivity.this.dividerRow, sparseIntArray);
            int pointer5 = pointer4 + 1;
            put(pointer5, ManageLinksActivity.this.createNewLinkRow, sparseIntArray);
            int pointer6 = pointer5 + 1;
            put(pointer6, ManageLinksActivity.this.revokedHeader, sparseIntArray);
            int pointer7 = pointer6 + 1;
            put(pointer7, ManageLinksActivity.this.revokeAllRow, sparseIntArray);
            int pointer8 = pointer7 + 1;
            put(pointer8, ManageLinksActivity.this.createLinkHelpRow, sparseIntArray);
            int pointer9 = pointer8 + 1;
            put(pointer9, ManageLinksActivity.this.creatorRow, sparseIntArray);
            int pointer10 = pointer9 + 1;
            put(pointer10, ManageLinksActivity.this.creatorDividerRow, sparseIntArray);
            int pointer11 = pointer10 + 1;
            put(pointer11, ManageLinksActivity.this.adminsHeaderRow, sparseIntArray);
            int pointer12 = pointer11 + 1;
            put(pointer12, ManageLinksActivity.this.linksHeaderRow, sparseIntArray);
            put(pointer12 + 1, ManageLinksActivity.this.linksLoadingRow, sparseIntArray);
        }

        private void put(int id, int position, SparseIntArray sparseIntArray) {
            if (position >= 0) {
                sparseIntArray.put(position, id);
            }
        }
    }

    public DiffCallback saveListState() {
        DiffCallback callback = new DiffCallback();
        callback.fillPositions(callback.oldPositionToItem);
        callback.oldLinksStartRow = this.linksStartRow;
        callback.oldLinksEndRow = this.linksEndRow;
        callback.oldRevokedLinksStartRow = this.revokedLinksStartRow;
        callback.oldRevokedLinksEndRow = this.revokedLinksEndRow;
        callback.oldAdminsStartRow = this.adminsStartRow;
        callback.oldAdminsEndRow = this.adminsEndRow;
        callback.oldRowCount = this.rowCount;
        callback.oldLinks.clear();
        callback.oldLinks.addAll(this.invites);
        callback.oldRevokedLinks.clear();
        callback.oldRevokedLinks.addAll(this.revokedInvites);
        return callback;
    }

    public void fixDate(TLRPC.TL_chatInviteExported edited) {
        boolean z = true;
        if (edited.expire_date > 0) {
            if (getConnectionsManager().getCurrentTime() < edited.expire_date) {
                z = false;
            }
            edited.expired = z;
        } else if (edited.usage_limit > 0) {
            if (edited.usage < edited.usage_limit) {
                z = false;
            }
            edited.expired = z;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ManageLinksActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ManageLinksActivity.this.m3889x1210da6c();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, CreationTextCell.class, LinkActionView.class, LinkCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
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
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_unreadCounterMuted));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CreationTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText2));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{CreationTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CreationTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LinkCell.class}, new String[]{"titleView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LinkCell.class}, new String[]{"subtitleView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{LinkCell.class}, new String[]{"optionsView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_stickers_menu));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$17$org-telegram-ui-ManageLinksActivity */
    public /* synthetic */ void m3889x1210da6c() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
                if (child instanceof LinkActionView) {
                    ((LinkActionView) child).updateColors();
                }
            }
        }
        InviteLinkBottomSheet inviteLinkBottomSheet = this.inviteLinkBottomSheet;
        if (inviteLinkBottomSheet != null) {
            inviteLinkBottomSheet.updateColors();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean needDelayOpenAnimation() {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        InviteLinkBottomSheet inviteLinkBottomSheet;
        super.onTransitionAnimationEnd(isOpen, backward);
        if (isOpen) {
            this.isOpened = true;
            if (backward && (inviteLinkBottomSheet = this.inviteLinkBottomSheet) != null && inviteLinkBottomSheet.isNeedReopen) {
                this.inviteLinkBottomSheet.show();
            }
        }
        NotificationCenter.getInstance(this.currentAccount).onAnimationFinish(this.animationIndex);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        super.onTransitionAnimationStart(isOpen, backward);
        this.animationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.animationIndex, null);
    }
}
