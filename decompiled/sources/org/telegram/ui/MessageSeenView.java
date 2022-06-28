package org.telegram.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarsImageView;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes4.dex */
public class MessageSeenView extends FrameLayout {
    AvatarsImageView avatarsImageView;
    int currentAccount;
    FlickerLoadingView flickerLoadingView;
    ImageView iconView;
    boolean ignoreLayout;
    boolean isVoice;
    TextView titleView;
    ArrayList<Long> peerIds = new ArrayList<>();
    public ArrayList<TLRPC.User> users = new ArrayList<>();

    public MessageSeenView(Context context, final int currentAccount, MessageObject messageObject, final TLRPC.Chat chat) {
        super(context);
        long fromId;
        this.currentAccount = currentAccount;
        this.isVoice = messageObject.isRoundVideo() || messageObject.isVoice();
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView;
        flickerLoadingView.setColors(Theme.key_actionBarDefaultSubmenuBackground, Theme.key_listSelector, null);
        this.flickerLoadingView.setViewType(13);
        this.flickerLoadingView.setIsSingleCell(false);
        addView(this.flickerLoadingView, LayoutHelper.createFrame(-2, -1.0f));
        TextView textView = new TextView(context) { // from class: org.telegram.ui.MessageSeenView.1
            @Override // android.widget.TextView
            public void setText(CharSequence text, TextView.BufferType type) {
                super.setText(text, type);
            }
        };
        this.titleView = textView;
        textView.setTextSize(1, 16.0f);
        this.titleView.setLines(1);
        this.titleView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, 19, 40.0f, 0.0f, 62.0f, 0.0f));
        AvatarsImageView avatarsImageView = new AvatarsImageView(context, false);
        this.avatarsImageView = avatarsImageView;
        avatarsImageView.setStyle(11);
        addView(this.avatarsImageView, LayoutHelper.createFrame(56, -1.0f, 21, 0.0f, 0.0f, 0.0f, 0.0f));
        this.titleView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        TLRPC.TL_messages_getMessageReadParticipants req = new TLRPC.TL_messages_getMessageReadParticipants();
        req.msg_id = messageObject.getId();
        req.peer = MessagesController.getInstance(currentAccount).getInputPeer(messageObject.getDialogId());
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        addView(imageView, LayoutHelper.createFrame(24, 24.0f, 19, 11.0f, 0.0f, 0.0f, 0.0f));
        Drawable drawable = ContextCompat.getDrawable(context, this.isVoice ? R.drawable.msg_played : R.drawable.msg_seen).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon), PorterDuff.Mode.MULTIPLY));
        this.iconView.setImageDrawable(drawable);
        this.avatarsImageView.setAlpha(0.0f);
        this.titleView.setAlpha(0.0f);
        if (messageObject.messageOwner.from_id == null) {
            fromId = 0;
        } else {
            long fromId2 = messageObject.messageOwner.from_id.user_id;
            fromId = fromId2;
        }
        final long finalFromId = fromId;
        ConnectionsManager.getInstance(currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.MessageSeenView$$ExternalSyntheticLambda5
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessageSeenView.this.m3911lambda$new$5$orgtelegramuiMessageSeenView(finalFromId, currentAccount, chat, tLObject, tL_error);
            }
        });
        setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 6, 0));
        setEnabled(false);
    }

    /* renamed from: lambda$new$5$org-telegram-ui-MessageSeenView */
    public /* synthetic */ void m3911lambda$new$5$orgtelegramuiMessageSeenView(final long finalFromId, final int currentAccount, final TLRPC.Chat chat, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.MessageSeenView$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MessageSeenView.this.m3910lambda$new$4$orgtelegramuiMessageSeenView(error, response, finalFromId, currentAccount, chat);
            }
        });
    }

    /* renamed from: lambda$new$4$org-telegram-ui-MessageSeenView */
    public /* synthetic */ void m3910lambda$new$4$orgtelegramuiMessageSeenView(TLRPC.TL_error error, TLObject response, long finalFromId, final int currentAccount, TLRPC.Chat chat) {
        if (error == null) {
            TLRPC.Vector vector = (TLRPC.Vector) response;
            ArrayList<Long> unknownUsers = new ArrayList<>();
            final HashMap<Long, TLRPC.User> usersLocal = new HashMap<>();
            final ArrayList<Long> allPeers = new ArrayList<>();
            int n = vector.objects.size();
            for (int i = 0; i < n; i++) {
                Object object = vector.objects.get(i);
                if (object instanceof Long) {
                    Long peerId = (Long) object;
                    if (finalFromId != peerId.longValue()) {
                        MessagesController.getInstance(currentAccount).getUser(peerId);
                        allPeers.add(peerId);
                        unknownUsers.add(peerId);
                    }
                }
            }
            if (unknownUsers.isEmpty()) {
                for (int i2 = 0; i2 < allPeers.size(); i2++) {
                    this.peerIds.add(allPeers.get(i2));
                    this.users.add(usersLocal.get(allPeers.get(i2)));
                }
                updateView();
                return;
            } else if (ChatObject.isChannel(chat)) {
                TLRPC.TL_channels_getParticipants usersReq = new TLRPC.TL_channels_getParticipants();
                usersReq.limit = MessagesController.getInstance(currentAccount).chatReadMarkSizeThreshold;
                usersReq.offset = 0;
                usersReq.filter = new TLRPC.TL_channelParticipantsRecent();
                usersReq.channel = MessagesController.getInstance(currentAccount).getInputChannel(chat.id);
                ConnectionsManager.getInstance(currentAccount).sendRequest(usersReq, new RequestDelegate() { // from class: org.telegram.ui.MessageSeenView$$ExternalSyntheticLambda3
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessageSeenView.this.m3907lambda$new$1$orgtelegramuiMessageSeenView(currentAccount, usersLocal, allPeers, tLObject, tL_error);
                    }
                });
                return;
            } else {
                TLRPC.TL_messages_getFullChat usersReq2 = new TLRPC.TL_messages_getFullChat();
                usersReq2.chat_id = chat.id;
                ConnectionsManager.getInstance(currentAccount).sendRequest(usersReq2, new RequestDelegate() { // from class: org.telegram.ui.MessageSeenView$$ExternalSyntheticLambda4
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        MessageSeenView.this.m3909lambda$new$3$orgtelegramuiMessageSeenView(currentAccount, usersLocal, allPeers, tLObject, tL_error);
                    }
                });
                return;
            }
        }
        updateView();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-MessageSeenView */
    public /* synthetic */ void m3907lambda$new$1$orgtelegramuiMessageSeenView(final int currentAccount, final HashMap usersLocal, final ArrayList allPeers, final TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.MessageSeenView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                MessageSeenView.this.m3906lambda$new$0$orgtelegramuiMessageSeenView(response1, currentAccount, usersLocal, allPeers);
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-MessageSeenView */
    public /* synthetic */ void m3906lambda$new$0$orgtelegramuiMessageSeenView(TLObject response1, int currentAccount, HashMap usersLocal, ArrayList allPeers) {
        if (response1 != null) {
            TLRPC.TL_channels_channelParticipants users = (TLRPC.TL_channels_channelParticipants) response1;
            for (int i = 0; i < users.users.size(); i++) {
                TLRPC.User user = users.users.get(i);
                MessagesController.getInstance(currentAccount).putUser(user, false);
                usersLocal.put(Long.valueOf(user.id), user);
            }
            for (int i2 = 0; i2 < allPeers.size(); i2++) {
                this.peerIds.add((Long) allPeers.get(i2));
                this.users.add((TLRPC.User) usersLocal.get(allPeers.get(i2)));
            }
        }
        updateView();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-MessageSeenView */
    public /* synthetic */ void m3909lambda$new$3$orgtelegramuiMessageSeenView(final int currentAccount, final HashMap usersLocal, final ArrayList allPeers, final TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.MessageSeenView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MessageSeenView.this.m3908lambda$new$2$orgtelegramuiMessageSeenView(response1, currentAccount, usersLocal, allPeers);
            }
        });
    }

    /* renamed from: lambda$new$2$org-telegram-ui-MessageSeenView */
    public /* synthetic */ void m3908lambda$new$2$orgtelegramuiMessageSeenView(TLObject response1, int currentAccount, HashMap usersLocal, ArrayList allPeers) {
        if (response1 != null) {
            TLRPC.TL_messages_chatFull chatFull = (TLRPC.TL_messages_chatFull) response1;
            for (int i = 0; i < chatFull.users.size(); i++) {
                TLRPC.User user = chatFull.users.get(i);
                MessagesController.getInstance(currentAccount).putUser(user, false);
                usersLocal.put(Long.valueOf(user.id), user);
            }
            for (int i2 = 0; i2 < allPeers.size(); i2++) {
                this.peerIds.add((Long) allPeers.get(i2));
                this.users.add((TLRPC.User) usersLocal.get(allPeers.get(i2)));
            }
        }
        updateView();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        View parent = (View) getParent();
        if (parent != null && parent.getWidth() > 0) {
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth(), C.BUFFER_FLAG_ENCRYPTED);
        }
        if (this.flickerLoadingView.getVisibility() == 0) {
            this.ignoreLayout = true;
            this.flickerLoadingView.setVisibility(8);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            this.flickerLoadingView.getLayoutParams().width = getMeasuredWidth();
            this.flickerLoadingView.setVisibility(0);
            this.ignoreLayout = false;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void updateView() {
        setEnabled(this.users.size() > 0);
        for (int i = 0; i < 3; i++) {
            if (i < this.users.size()) {
                this.avatarsImageView.setObject(i, this.currentAccount, this.users.get(i));
            } else {
                this.avatarsImageView.setObject(i, this.currentAccount, null);
            }
        }
        if (this.users.size() == 1) {
            this.avatarsImageView.setTranslationX(AndroidUtilities.dp(24.0f));
        } else if (this.users.size() == 2) {
            this.avatarsImageView.setTranslationX(AndroidUtilities.dp(12.0f));
        } else {
            this.avatarsImageView.setTranslationX(0.0f);
        }
        int newRightMargin = AndroidUtilities.dp(this.users.size() == 0 ? 8.0f : 62.0f);
        ViewGroup.MarginLayoutParams titleViewMargins = (ViewGroup.MarginLayoutParams) this.titleView.getLayoutParams();
        if (titleViewMargins.rightMargin != newRightMargin) {
            titleViewMargins.rightMargin = newRightMargin;
            this.titleView.setLayoutParams(titleViewMargins);
        }
        this.avatarsImageView.commitTransition(false);
        if (this.peerIds.size() == 1 && this.users.get(0) != null) {
            this.titleView.setText(ContactsController.formatName(this.users.get(0).first_name, this.users.get(0).last_name));
        } else if (this.peerIds.size() == 0) {
            this.titleView.setText(LocaleController.getString("NobodyViewed", R.string.NobodyViewed));
        } else {
            this.titleView.setText(LocaleController.formatPluralString(this.isVoice ? "MessagePlayed" : "MessageSeen", this.peerIds.size(), new Object[0]));
        }
        this.titleView.animate().alpha(1.0f).setDuration(220L).start();
        this.avatarsImageView.animate().alpha(1.0f).setDuration(220L).start();
        this.flickerLoadingView.animate().alpha(0.0f).setDuration(220L).setListener(new HideViewAfterAnimation(this.flickerLoadingView)).start();
    }

    public RecyclerListView createListView() {
        RecyclerListView recyclerListView = new RecyclerListView(getContext()) { // from class: org.telegram.ui.MessageSeenView.2
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onMeasure(int widthSpec, int heightSpec) {
                int height = View.MeasureSpec.getSize(heightSpec);
                int listViewTotalHeight = AndroidUtilities.dp(8.0f) + (AndroidUtilities.dp(44.0f) * getAdapter().getItemCount());
                if (listViewTotalHeight > height) {
                    listViewTotalHeight = height;
                }
                super.onMeasure(widthSpec, View.MeasureSpec.makeMeasureSpec(listViewTotalHeight, C.BUFFER_FLAG_ENCRYPTED));
            }
        };
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerListView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.MessageSeenView.3
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int p = parent.getChildAdapterPosition(view);
                if (p == 0) {
                    outRect.top = AndroidUtilities.dp(4.0f);
                }
                if (p == MessageSeenView.this.users.size() - 1) {
                    outRect.bottom = AndroidUtilities.dp(4.0f);
                }
            }
        });
        recyclerListView.setAdapter(new RecyclerListView.SelectionAdapter() { // from class: org.telegram.ui.MessageSeenView.4
            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                UserCell userCell = new UserCell(parent.getContext());
                userCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(userCell);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                UserCell cell = (UserCell) holder.itemView;
                cell.setUser(MessageSeenView.this.users.get(position));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return MessageSeenView.this.users.size();
            }
        });
        return recyclerListView;
    }

    /* loaded from: classes4.dex */
    private static class UserCell extends FrameLayout {
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        BackupImageView avatarImageView;
        TextView nameView;

        public UserCell(Context context) {
            super(context);
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(32, 32.0f, 16, 13.0f, 0.0f, 0.0f, 0.0f));
            this.avatarImageView.setRoundRadius(AndroidUtilities.dp(16.0f));
            TextView textView = new TextView(context);
            this.nameView = textView;
            textView.setTextSize(1, 16.0f);
            this.nameView.setLines(1);
            this.nameView.setEllipsize(TextUtils.TruncateAt.END);
            this.nameView.setImportantForAccessibility(2);
            addView(this.nameView, LayoutHelper.createFrame(-2, -2.0f, 19, 59.0f, 0.0f, 13.0f, 0.0f));
            this.nameView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        public void setUser(TLRPC.User user) {
            if (user != null) {
                this.avatarDrawable.setInfo(user);
                ImageLocation imageLocation = ImageLocation.getForUser(user, 1);
                this.avatarImageView.setImage(imageLocation, "50_50", this.avatarDrawable, user);
                this.nameView.setText(ContactsController.formatName(user.first_name, user.last_name));
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setText(LocaleController.formatString("AccDescrPersonHasSeen", R.string.AccDescrPersonHasSeen, this.nameView.getText()));
        }
    }
}
