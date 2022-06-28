package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.JoinSheetUserCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatReactionsEditActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class JoinGroupAlert extends BottomSheet {
    private TLRPC.ChatInvite chatInvite;
    private TLRPC.Chat currentChat;
    private BaseFragment fragment;
    private String hash;
    private RadialProgressView requestProgressView;
    private TextView requestTextView;

    public JoinGroupAlert(Context context, TLObject obj, String group, BaseFragment parentFragment, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        int participants_count;
        String str;
        int i;
        String str2;
        int i2;
        int participants_count2;
        setApplyBottomPadding(false);
        setApplyTopPadding(false);
        fixNavigationBar(getThemedColor(Theme.key_windowBackgroundWhite));
        this.fragment = parentFragment;
        if (obj instanceof TLRPC.ChatInvite) {
            this.chatInvite = (TLRPC.ChatInvite) obj;
        } else if (obj instanceof TLRPC.Chat) {
            this.currentChat = (TLRPC.Chat) obj;
        }
        this.hash = group;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setClickable(true);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.addView(linearLayout);
        NestedScrollView scrollView = new NestedScrollView(context);
        scrollView.addView(frameLayout);
        setCustomView(scrollView);
        ImageView closeView = new ImageView(context);
        closeView.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
        closeView.setColorFilter(getThemedColor(Theme.key_sheet_other));
        closeView.setImageResource(R.drawable.ic_layer_close);
        closeView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                JoinGroupAlert.this.m2726lambda$new$0$orgtelegramuiComponentsJoinGroupAlert(view);
            }
        });
        int closeViewPadding = AndroidUtilities.dp(8.0f);
        closeView.setPadding(closeViewPadding, closeViewPadding, closeViewPadding, closeViewPadding);
        frameLayout.addView(closeView, LayoutHelper.createFrame(36, 36.0f, 8388661, 6.0f, 8.0f, 6.0f, 0.0f));
        String title = null;
        String about = null;
        BackupImageView avatarImageView = new BackupImageView(context);
        avatarImageView.setRoundRadius(AndroidUtilities.dp(35.0f));
        linearLayout.addView(avatarImageView, LayoutHelper.createLinear(70, 70, 49, 0, 29, 0, 0));
        TLRPC.ChatInvite chatInvite = this.chatInvite;
        String str3 = null;
        if (chatInvite != null) {
            if (chatInvite.chat != null) {
                AvatarDrawable avatarDrawable = new AvatarDrawable(this.chatInvite.chat);
                String title2 = this.chatInvite.chat.title;
                int participants_count3 = this.chatInvite.chat.participants_count;
                avatarImageView.setForUserOrChat(this.chatInvite.chat, avatarDrawable, this.chatInvite);
                participants_count2 = participants_count3;
                title = title2;
            } else {
                AvatarDrawable avatarDrawable2 = new AvatarDrawable();
                avatarDrawable2.setInfo(0L, this.chatInvite.title, null);
                String title3 = this.chatInvite.title;
                int participants_count4 = this.chatInvite.participants_count;
                TLRPC.PhotoSize size = FileLoader.getClosestPhotoSizeWithSize(this.chatInvite.photo.sizes, 50);
                avatarImageView.setImage(ImageLocation.getForPhoto(size, this.chatInvite.photo), "50_50", avatarDrawable2, this.chatInvite);
                participants_count2 = participants_count4;
                title = title3;
            }
            about = this.chatInvite.about;
            participants_count = participants_count2;
        } else if (this.currentChat == null) {
            participants_count = 0;
        } else {
            AvatarDrawable avatarDrawable3 = new AvatarDrawable(this.currentChat);
            title = this.currentChat.title;
            TLRPC.ChatFull chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(this.currentChat.id);
            about = chatFull != null ? chatFull.about : str3;
            int participants_count5 = Math.max(this.currentChat.participants_count, chatFull != null ? chatFull.participants_count : 0);
            TLRPC.Chat chat = this.currentChat;
            avatarImageView.setForUserOrChat(chat, avatarDrawable3, chat);
            participants_count = participants_count5;
        }
        TextView textView = new TextView(context);
        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textView.setTextSize(1, 17.0f);
        textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        textView.setText(title);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 49, 10, 9, 10, participants_count > 0 ? 0 : 20));
        TLRPC.ChatInvite chatInvite2 = this.chatInvite;
        final boolean isChannel = (chatInvite2 != null && ((chatInvite2.channel && !this.chatInvite.megagroup) || ChatObject.isChannelAndNotMegaGroup(this.chatInvite.chat))) || (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup);
        boolean hasAbout = !TextUtils.isEmpty(about);
        if (participants_count > 0) {
            TextView textView2 = new TextView(context);
            textView2.setTextSize(1, 14.0f);
            textView2.setTextColor(getThemedColor(Theme.key_dialogTextGray3));
            textView2.setSingleLine(true);
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            if (isChannel) {
                textView2.setText(LocaleController.formatPluralString("Subscribers", participants_count, new Object[0]));
            } else {
                textView2.setText(LocaleController.formatPluralString("Members", participants_count, new Object[0]));
            }
            linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 49, 10, 3, 10, hasAbout ? 0 : 20));
        }
        if (hasAbout) {
            TextView aboutTextView = new TextView(context);
            aboutTextView.setGravity(17);
            aboutTextView.setText(about);
            aboutTextView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
            aboutTextView.setTextSize(1, 15.0f);
            linearLayout.addView(aboutTextView, LayoutHelper.createLinear(-1, -2, 48, 24, 10, 24, 20));
        }
        TLRPC.ChatInvite chatInvite3 = this.chatInvite;
        if (chatInvite3 == null || chatInvite3.request_needed) {
            FrameLayout requestFrameLayout = new FrameLayout(getContext());
            linearLayout.addView(requestFrameLayout, LayoutHelper.createLinear(-1, -2));
            RadialProgressView radialProgressView = new RadialProgressView(getContext(), resourcesProvider);
            this.requestProgressView = radialProgressView;
            radialProgressView.setProgressColor(getThemedColor(Theme.key_featuredStickers_addButton));
            this.requestProgressView.setSize(AndroidUtilities.dp(32.0f));
            this.requestProgressView.setVisibility(4);
            requestFrameLayout.addView(this.requestProgressView, LayoutHelper.createFrame(48, 48, 17));
            TextView textView3 = new TextView(getContext());
            this.requestTextView = textView3;
            textView3.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor(Theme.key_featuredStickers_addButton), getThemedColor(Theme.key_featuredStickers_addButtonPressed)));
            this.requestTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.requestTextView.setGravity(17);
            this.requestTextView.setSingleLine(true);
            TextView textView4 = this.requestTextView;
            if (isChannel) {
                i = R.string.RequestToJoinChannel;
                str = "RequestToJoinChannel";
            } else {
                i = R.string.RequestToJoinGroup;
                str = "RequestToJoinGroup";
            }
            textView4.setText(LocaleController.getString(str, i));
            this.requestTextView.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
            this.requestTextView.setTextSize(1, 15.0f);
            this.requestTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.requestTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    JoinGroupAlert.this.m2735lambda$new$7$orgtelegramuiComponentsJoinGroupAlert(isChannel, view);
                }
            });
            requestFrameLayout.addView(this.requestTextView, LayoutHelper.createLinear(-1, 48, (int) GravityCompat.START, 16, 0, 16, 0));
            TextView descriptionTextView = new TextView(getContext());
            descriptionTextView.setGravity(17);
            descriptionTextView.setTextSize(1, 14.0f);
            if (isChannel) {
                i2 = R.string.RequestToJoinChannelDescription;
                str2 = "RequestToJoinChannelDescription";
            } else {
                i2 = R.string.RequestToJoinGroupDescription;
                str2 = "RequestToJoinGroupDescription";
            }
            descriptionTextView.setText(LocaleController.getString(str2, i2));
            descriptionTextView.setTextColor(getThemedColor(Theme.key_dialogTextGray3));
            linearLayout.addView(descriptionTextView, LayoutHelper.createLinear(-1, -2, 48, 24, 17, 24, 15));
            return;
        }
        TLRPC.ChatInvite chatInvite4 = this.chatInvite;
        if (chatInvite4 != null) {
            if (!chatInvite4.participants.isEmpty()) {
                RecyclerListView listView = new RecyclerListView(context);
                listView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
                listView.setNestedScrollingEnabled(false);
                listView.setClipToPadding(false);
                listView.setLayoutManager(new LinearLayoutManager(getContext(), 0, false));
                listView.setHorizontalScrollBarEnabled(false);
                listView.setVerticalScrollBarEnabled(false);
                listView.setAdapter(new UsersAdapter(context));
                listView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
                linearLayout.addView(listView, LayoutHelper.createLinear(-2, 90, 49, 0, 0, 0, 7));
            }
            View shadow = new View(context);
            shadow.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
            linearLayout.addView(shadow, new LinearLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight()));
            PickerBottomLayout pickerBottomLayout = new PickerBottomLayout(context, false, resourcesProvider);
            linearLayout.addView(pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 83));
            pickerBottomLayout.cancelButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            pickerBottomLayout.cancelButton.setTextColor(getThemedColor(Theme.key_dialogTextBlue2));
            pickerBottomLayout.cancelButton.setText(LocaleController.getString("Cancel", R.string.Cancel).toUpperCase());
            pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    JoinGroupAlert.this.m2736lambda$new$8$orgtelegramuiComponentsJoinGroupAlert(view);
                }
            });
            pickerBottomLayout.doneButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            pickerBottomLayout.doneButton.setVisibility(0);
            pickerBottomLayout.doneButtonBadgeTextView.setVisibility(8);
            pickerBottomLayout.doneButtonTextView.setTextColor(getThemedColor(Theme.key_dialogTextBlue2));
            if ((this.chatInvite.channel && !this.chatInvite.megagroup) || (ChatObject.isChannel(this.chatInvite.chat) && !this.chatInvite.chat.megagroup)) {
                pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString("ProfileJoinChannel", R.string.ProfileJoinChannel).toUpperCase());
            } else {
                pickerBottomLayout.doneButtonTextView.setText(LocaleController.getString("JoinGroup", R.string.JoinGroup));
            }
            pickerBottomLayout.doneButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    JoinGroupAlert.this.m2729lambda$new$11$orgtelegramuiComponentsJoinGroupAlert(view);
                }
            });
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-JoinGroupAlert */
    public /* synthetic */ void m2726lambda$new$0$orgtelegramuiComponentsJoinGroupAlert(View view) {
        dismiss();
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-JoinGroupAlert */
    public /* synthetic */ void m2735lambda$new$7$orgtelegramuiComponentsJoinGroupAlert(final boolean isChannel, View view) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                JoinGroupAlert.this.m2727lambda$new$1$orgtelegramuiComponentsJoinGroupAlert();
            }
        }, 400L);
        if (this.chatInvite == null && this.currentChat != null) {
            MessagesController.getInstance(this.currentAccount).addUserToChat(this.currentChat.id, UserConfig.getInstance(this.currentAccount).getCurrentUser(), 0, null, null, true, new Runnable() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    JoinGroupAlert.this.dismiss();
                }
            }, new MessagesController.ErrorDelegate() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda1
                @Override // org.telegram.messenger.MessagesController.ErrorDelegate
                public final boolean run(TLRPC.TL_error tL_error) {
                    return JoinGroupAlert.this.m2731lambda$new$3$orgtelegramuiComponentsJoinGroupAlert(isChannel, tL_error);
                }
            });
            return;
        }
        final TLRPC.TL_messages_importChatInvite request = new TLRPC.TL_messages_importChatInvite();
        request.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new RequestDelegate() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda3
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                JoinGroupAlert.this.m2734lambda$new$6$orgtelegramuiComponentsJoinGroupAlert(isChannel, request, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-JoinGroupAlert */
    public /* synthetic */ void m2727lambda$new$1$orgtelegramuiComponentsJoinGroupAlert() {
        if (!isDismissed()) {
            this.requestTextView.setVisibility(4);
            this.requestProgressView.setVisibility(0);
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-JoinGroupAlert */
    public /* synthetic */ boolean m2731lambda$new$3$orgtelegramuiComponentsJoinGroupAlert(final boolean isChannel, TLRPC.TL_error err) {
        if (err != null && "INVITE_REQUEST_SENT".equals(err.text)) {
            setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    JoinGroupAlert.this.m2730lambda$new$2$orgtelegramuiComponentsJoinGroupAlert(isChannel, dialogInterface);
                }
            });
        }
        dismiss();
        return false;
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-JoinGroupAlert */
    public /* synthetic */ void m2730lambda$new$2$orgtelegramuiComponentsJoinGroupAlert(boolean isChannel, DialogInterface di) {
        showBulletin(getContext(), this.fragment, isChannel);
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-JoinGroupAlert */
    public /* synthetic */ void m2734lambda$new$6$orgtelegramuiComponentsJoinGroupAlert(final boolean isChannel, final TLRPC.TL_messages_importChatInvite request, TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                JoinGroupAlert.this.m2733lambda$new$5$orgtelegramuiComponentsJoinGroupAlert(error, isChannel, request);
            }
        });
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-JoinGroupAlert */
    public /* synthetic */ void m2733lambda$new$5$orgtelegramuiComponentsJoinGroupAlert(TLRPC.TL_error error, final boolean isChannel, TLRPC.TL_messages_importChatInvite request) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        if (error != null) {
            if ("INVITE_REQUEST_SENT".equals(error.text)) {
                setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda4
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        JoinGroupAlert.this.m2732lambda$new$4$orgtelegramuiComponentsJoinGroupAlert(isChannel, dialogInterface);
                    }
                });
            } else {
                AlertsCreator.processError(this.currentAccount, error, this.fragment, request, new Object[0]);
            }
        }
        dismiss();
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-JoinGroupAlert */
    public /* synthetic */ void m2732lambda$new$4$orgtelegramuiComponentsJoinGroupAlert(boolean isChannel, DialogInterface di) {
        showBulletin(getContext(), this.fragment, isChannel);
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-JoinGroupAlert */
    public /* synthetic */ void m2736lambda$new$8$orgtelegramuiComponentsJoinGroupAlert(View view) {
        dismiss();
    }

    /* renamed from: lambda$new$11$org-telegram-ui-Components-JoinGroupAlert */
    public /* synthetic */ void m2729lambda$new$11$orgtelegramuiComponentsJoinGroupAlert(View v) {
        dismiss();
        final TLRPC.TL_messages_importChatInvite req = new TLRPC.TL_messages_importChatInvite();
        req.hash = this.hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda2
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                JoinGroupAlert.this.m2728lambda$new$10$orgtelegramuiComponentsJoinGroupAlert(req, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$new$10$org-telegram-ui-Components-JoinGroupAlert */
    public /* synthetic */ void m2728lambda$new$10$orgtelegramuiComponentsJoinGroupAlert(final TLRPC.TL_messages_importChatInvite req, final TLObject response, final TLRPC.TL_error error) {
        if (error == null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                JoinGroupAlert.this.m2737lambda$new$9$orgtelegramuiComponentsJoinGroupAlert(error, response, req);
            }
        });
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-JoinGroupAlert */
    public /* synthetic */ void m2737lambda$new$9$orgtelegramuiComponentsJoinGroupAlert(TLRPC.TL_error error, TLObject response, TLRPC.TL_messages_importChatInvite req) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        if (error != null) {
            AlertsCreator.processError(this.currentAccount, error, this.fragment, req, new Object[0]);
            return;
        }
        TLRPC.Updates updates = (TLRPC.Updates) response;
        if (!updates.chats.isEmpty()) {
            TLRPC.Chat chat = updates.chats.get(0);
            chat.left = false;
            chat.kicked = false;
            MessagesController.getInstance(this.currentAccount).putUsers(updates.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(updates.chats, false);
            Bundle args = new Bundle();
            args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, chat.id);
            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this.fragment)) {
                ChatActivity chatActivity = new ChatActivity(args);
                BaseFragment baseFragment2 = this.fragment;
                baseFragment2.presentFragment(chatActivity, baseFragment2 instanceof ChatActivity);
            }
        }
    }

    public static void showBulletin(Context context, BaseFragment fragment, boolean isChannel) {
        String subTitle;
        Bulletin.TwoLineLottieLayout layout = new Bulletin.TwoLineLottieLayout(context, fragment.getResourceProvider());
        layout.imageView.setAnimation(R.raw.timer_3, 28, 28);
        layout.titleTextView.setText(LocaleController.getString("RequestToJoinSent", R.string.RequestToJoinSent));
        if (isChannel) {
            subTitle = LocaleController.getString("RequestToJoinChannelSentDescription", R.string.RequestToJoinChannelSentDescription);
        } else {
            subTitle = LocaleController.getString("RequestToJoinGroupSentDescription", R.string.RequestToJoinGroupSentDescription);
        }
        layout.subtitleTextView.setText(subTitle);
        Bulletin.make(fragment, layout, (int) Bulletin.DURATION_LONG).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class UsersAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public UsersAdapter(Context context) {
            JoinGroupAlert.this = r1;
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int count = JoinGroupAlert.this.chatInvite.participants.size();
            int participants_count = JoinGroupAlert.this.chatInvite.chat != null ? JoinGroupAlert.this.chatInvite.chat.participants_count : JoinGroupAlert.this.chatInvite.participants_count;
            if (count != participants_count) {
                return count + 1;
            }
            return count;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = new JoinSheetUserCell(this.context);
            view.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(100.0f), AndroidUtilities.dp(90.0f)));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            JoinSheetUserCell cell = (JoinSheetUserCell) holder.itemView;
            if (position < JoinGroupAlert.this.chatInvite.participants.size()) {
                cell.setUser(JoinGroupAlert.this.chatInvite.participants.get(position));
                return;
            }
            int participants_count = JoinGroupAlert.this.chatInvite.chat != null ? JoinGroupAlert.this.chatInvite.chat.participants_count : JoinGroupAlert.this.chatInvite.participants_count;
            cell.setCount(participants_count - JoinGroupAlert.this.chatInvite.participants.size());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return 0;
        }
    }
}
