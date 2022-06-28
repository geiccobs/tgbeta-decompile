package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class ReactedHeaderView extends FrameLayout {
    private AvatarsImageView avatarsImageView;
    private int currentAccount;
    private long dialogId;
    private FlickerLoadingView flickerLoadingView;
    private ImageView iconView;
    private boolean ignoreLayout;
    private boolean isLoaded;
    private MessageObject message;
    private BackupImageView reactView;
    private Consumer<List<TLRPC.User>> seenCallback;
    private TextView titleView;
    private List<TLRPC.User> seenUsers = new ArrayList();
    private List<TLRPC.User> users = new ArrayList();

    public ReactedHeaderView(Context context, int currentAccount, MessageObject message, long dialogId) {
        super(context);
        this.currentAccount = currentAccount;
        this.message = message;
        this.dialogId = dialogId;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView;
        flickerLoadingView.setColors(Theme.key_actionBarDefaultSubmenuBackground, Theme.key_listSelector, null);
        this.flickerLoadingView.setViewType(13);
        this.flickerLoadingView.setIsSingleCell(false);
        addView(this.flickerLoadingView, LayoutHelper.createFrame(-2, -1.0f));
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem));
        this.titleView.setTextSize(1, 16.0f);
        this.titleView.setLines(1);
        this.titleView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.titleView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 40.0f, 0.0f, 62.0f, 0.0f));
        AvatarsImageView avatarsImageView = new AvatarsImageView(context, false);
        this.avatarsImageView = avatarsImageView;
        avatarsImageView.setStyle(11);
        addView(this.avatarsImageView, LayoutHelper.createFrameRelatively(56.0f, -1.0f, 8388629, 0.0f, 0.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        addView(imageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 11.0f, 0.0f, 0.0f, 0.0f));
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.msg_reactions).mutate();
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon), PorterDuff.Mode.MULTIPLY));
        this.iconView.setImageDrawable(drawable);
        this.iconView.setVisibility(8);
        BackupImageView backupImageView = new BackupImageView(context);
        this.reactView = backupImageView;
        addView(backupImageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 11.0f, 0.0f, 0.0f, 0.0f));
        this.titleView.setAlpha(0.0f);
        this.avatarsImageView.setAlpha(0.0f);
        setBackground(Theme.getSelectorDrawable(false));
    }

    public void setSeenCallback(Consumer<List<TLRPC.User>> seenCallback) {
        this.seenCallback = seenCallback;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.isLoaded) {
            MessagesController ctrl = MessagesController.getInstance(this.currentAccount);
            final TLRPC.Chat chat = ctrl.getChat(Long.valueOf(this.message.getChatId()));
            TLRPC.ChatFull chatInfo = ctrl.getChatFull(this.message.getChatId());
            boolean showSeen = chat != null && this.message.isOutOwner() && this.message.isSent() && !this.message.isEditing() && !this.message.isSending() && !this.message.isSendError() && !this.message.isContentUnread() && !this.message.isUnread() && ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.message.messageOwner.date < 604800 && (ChatObject.isMegagroup(chat) || !ChatObject.isChannel(chat)) && chatInfo != null && chatInfo.participants_count <= MessagesController.getInstance(this.currentAccount).chatReadMarkSizeThreshold && !(this.message.messageOwner.action instanceof TLRPC.TL_messageActionChatJoinedByRequest);
            if (showSeen) {
                TLRPC.TL_messages_getMessageReadParticipants req = new TLRPC.TL_messages_getMessageReadParticipants();
                req.msg_id = this.message.getId();
                req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.message.getDialogId());
                final long fromId = this.message.messageOwner.from_id != null ? this.message.messageOwner.from_id.user_id : 0L;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.ReactedHeaderView$$ExternalSyntheticLambda5
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ReactedHeaderView.this.m2938xe1027737(fromId, chat, tLObject, tL_error);
                    }
                }, 64);
                return;
            }
            loadReactions();
        }
    }

    /* renamed from: lambda$onAttachedToWindow$5$org-telegram-ui-Components-ReactedHeaderView */
    public /* synthetic */ void m2938xe1027737(long fromId, TLRPC.Chat chat, TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.Vector) {
            final List<Long> usersToRequest = new ArrayList<>();
            TLRPC.Vector v = (TLRPC.Vector) response;
            Iterator<Object> it = v.objects.iterator();
            while (it.hasNext()) {
                Object obj = it.next();
                if (obj instanceof Long) {
                    long l = ((Long) obj).longValue();
                    if (fromId != l) {
                        usersToRequest.add(Long.valueOf(l));
                    }
                }
            }
            usersToRequest.add(Long.valueOf(fromId));
            final List<TLRPC.User> usersRes = new ArrayList<>();
            final Runnable callback = new Runnable() { // from class: org.telegram.ui.Components.ReactedHeaderView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ReactedHeaderView.this.m2933x41acb31c(usersRes);
                }
            };
            if (ChatObject.isChannel(chat)) {
                TLRPC.TL_channels_getParticipants usersReq = new TLRPC.TL_channels_getParticipants();
                usersReq.limit = MessagesController.getInstance(this.currentAccount).chatReadMarkSizeThreshold;
                usersReq.offset = 0;
                usersReq.filter = new TLRPC.TL_channelParticipantsRecent();
                usersReq.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(chat.id);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(usersReq, new RequestDelegate() { // from class: org.telegram.ui.Components.ReactedHeaderView$$ExternalSyntheticLambda6
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ReactedHeaderView.this.m2935xb49bce5a(usersToRequest, usersRes, callback, tLObject, tL_error);
                    }
                });
                return;
            }
            TLRPC.TL_messages_getFullChat usersReq2 = new TLRPC.TL_messages_getFullChat();
            usersReq2.chat_id = chat.id;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(usersReq2, new RequestDelegate() { // from class: org.telegram.ui.Components.ReactedHeaderView$$ExternalSyntheticLambda7
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ReactedHeaderView.this.m2937x278ae998(usersToRequest, usersRes, callback, tLObject, tL_error);
                }
            });
        }
    }

    /* renamed from: lambda$onAttachedToWindow$0$org-telegram-ui-Components-ReactedHeaderView */
    public /* synthetic */ void m2933x41acb31c(List usersRes) {
        this.seenUsers.addAll(usersRes);
        Iterator it = usersRes.iterator();
        while (it.hasNext()) {
            TLRPC.User u = (TLRPC.User) it.next();
            boolean hasSame = false;
            int i = 0;
            while (true) {
                if (i >= this.users.size()) {
                    break;
                } else if (this.users.get(i).id != u.id) {
                    i++;
                } else {
                    hasSame = true;
                    break;
                }
            }
            if (!hasSame) {
                this.users.add(u);
            }
        }
        Consumer<List<TLRPC.User>> consumer = this.seenCallback;
        if (consumer != null) {
            consumer.accept(usersRes);
        }
        loadReactions();
    }

    /* renamed from: lambda$onAttachedToWindow$2$org-telegram-ui-Components-ReactedHeaderView */
    public /* synthetic */ void m2935xb49bce5a(final List usersToRequest, final List usersRes, final Runnable callback, final TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ReactedHeaderView$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ReactedHeaderView.this.m2934xfb2440bb(response1, usersToRequest, usersRes, callback);
            }
        });
    }

    /* renamed from: lambda$onAttachedToWindow$1$org-telegram-ui-Components-ReactedHeaderView */
    public /* synthetic */ void m2934xfb2440bb(TLObject response1, List usersToRequest, List usersRes, Runnable callback) {
        if (response1 != null) {
            TLRPC.TL_channels_channelParticipants users = (TLRPC.TL_channels_channelParticipants) response1;
            for (int i = 0; i < users.users.size(); i++) {
                TLRPC.User user = users.users.get(i);
                MessagesController.getInstance(this.currentAccount).putUser(user, false);
                if (!user.self && usersToRequest.contains(Long.valueOf(user.id))) {
                    usersRes.add(user);
                }
            }
        }
        callback.run();
    }

    /* renamed from: lambda$onAttachedToWindow$4$org-telegram-ui-Components-ReactedHeaderView */
    public /* synthetic */ void m2937x278ae998(final List usersToRequest, final List usersRes, final Runnable callback, final TLObject response1, TLRPC.TL_error error1) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ReactedHeaderView$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ReactedHeaderView.this.m2936x6e135bf9(response1, usersToRequest, usersRes, callback);
            }
        });
    }

    /* renamed from: lambda$onAttachedToWindow$3$org-telegram-ui-Components-ReactedHeaderView */
    public /* synthetic */ void m2936x6e135bf9(TLObject response1, List usersToRequest, List usersRes, Runnable callback) {
        if (response1 != null) {
            TLRPC.TL_messages_chatFull chatFull = (TLRPC.TL_messages_chatFull) response1;
            for (int i = 0; i < chatFull.users.size(); i++) {
                TLRPC.User user = chatFull.users.get(i);
                MessagesController.getInstance(this.currentAccount).putUser(user, false);
                if (!user.self && usersToRequest.contains(Long.valueOf(user.id))) {
                    usersRes.add(user);
                }
            }
        }
        callback.run();
    }

    private void loadReactions() {
        MessagesController ctrl = MessagesController.getInstance(this.currentAccount);
        TLRPC.TL_messages_getMessageReactionsList getList = new TLRPC.TL_messages_getMessageReactionsList();
        getList.peer = ctrl.getInputPeer(this.message.getDialogId());
        getList.id = this.message.getId();
        getList.limit = 3;
        getList.reaction = null;
        getList.offset = null;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(getList, new RequestDelegate() { // from class: org.telegram.ui.Components.ReactedHeaderView$$ExternalSyntheticLambda4
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ReactedHeaderView.this.m2932xd9ccebf9(tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$loadReactions$7$org-telegram-ui-Components-ReactedHeaderView */
    public /* synthetic */ void m2932xd9ccebf9(TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_messages_messageReactionsList) {
            final TLRPC.TL_messages_messageReactionsList list = (TLRPC.TL_messages_messageReactionsList) response;
            final int c = list.count;
            post(new Runnable() { // from class: org.telegram.ui.Components.ReactedHeaderView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ReactedHeaderView.this.m2931x20555e5a(c, list);
                }
            });
        }
    }

    /* renamed from: lambda$loadReactions$6$org-telegram-ui-Components-ReactedHeaderView */
    public /* synthetic */ void m2931x20555e5a(int c, TLRPC.TL_messages_messageReactionsList list) {
        String str;
        String countStr;
        if (this.seenUsers.isEmpty() || this.seenUsers.size() < c) {
            str = LocaleController.formatPluralString("ReactionsCount", c, new Object[0]);
        } else {
            if (c == this.seenUsers.size()) {
                countStr = String.valueOf(c);
            } else {
                countStr = c + "/" + this.seenUsers.size();
            }
            str = String.format(LocaleController.getPluralString("Reacted", c), countStr);
        }
        this.titleView.setText(str);
        boolean showIcon = true;
        if (this.message.messageOwner.reactions != null && this.message.messageOwner.reactions.results.size() == 1 && !list.reactions.isEmpty()) {
            Iterator<TLRPC.TL_availableReaction> it = MediaDataController.getInstance(this.currentAccount).getReactionsList().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                TLRPC.TL_availableReaction r = it.next();
                if (r.reaction.equals(list.reactions.get(0).reaction)) {
                    this.reactView.setImage(ImageLocation.getForDocument(r.center_icon), "40_40_lastframe", "webp", (Drawable) null, r);
                    this.reactView.setVisibility(0);
                    this.reactView.setAlpha(0.0f);
                    this.reactView.animate().alpha(1.0f).start();
                    this.iconView.setVisibility(8);
                    showIcon = false;
                    break;
                }
            }
        }
        if (showIcon) {
            this.iconView.setVisibility(0);
            this.iconView.setAlpha(0.0f);
            this.iconView.animate().alpha(1.0f).start();
        }
        Iterator<TLRPC.User> it2 = list.users.iterator();
        while (it2.hasNext()) {
            TLRPC.User u = it2.next();
            if (this.message.messageOwner.from_id != null && u.id != this.message.messageOwner.from_id.user_id) {
                boolean hasSame = false;
                int i = 0;
                while (true) {
                    if (i >= this.users.size()) {
                        break;
                    } else if (this.users.get(i).id != u.id) {
                        i++;
                    } else {
                        hasSame = true;
                        break;
                    }
                }
                if (!hasSame) {
                    this.users.add(u);
                }
            }
        }
        updateView();
    }

    public List<TLRPC.User> getSeenUsers() {
        return this.seenUsers;
    }

    private void updateView() {
        float tX;
        setEnabled(this.users.size() > 0);
        for (int i = 0; i < 3; i++) {
            if (i < this.users.size()) {
                this.avatarsImageView.setObject(i, this.currentAccount, this.users.get(i));
            } else {
                this.avatarsImageView.setObject(i, this.currentAccount, null);
            }
        }
        switch (this.users.size()) {
            case 1:
                tX = AndroidUtilities.dp(24.0f);
                break;
            case 2:
                tX = AndroidUtilities.dp(12.0f);
                break;
            default:
                tX = 0.0f;
                break;
        }
        this.avatarsImageView.setTranslationX(LocaleController.isRTL ? AndroidUtilities.dp(12.0f) : tX);
        this.avatarsImageView.commitTransition(false);
        this.titleView.animate().alpha(1.0f).setDuration(220L).start();
        this.avatarsImageView.animate().alpha(1.0f).setDuration(220L).start();
        this.flickerLoadingView.animate().alpha(0.0f).setDuration(220L).setListener(new HideViewAfterAnimation(this.flickerLoadingView)).start();
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
}
