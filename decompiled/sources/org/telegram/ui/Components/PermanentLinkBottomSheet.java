package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.widget.NestedScrollView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.ManageLinksActivity;
/* loaded from: classes5.dex */
public class PermanentLinkBottomSheet extends BottomSheet {
    private long chatId;
    private BaseFragment fragment;
    private final RLottieImageView imageView;
    TLRPC.ChatFull info;
    TLRPC.TL_chatInviteExported invite;
    private boolean isChannel;
    private final LinkActionView linkActionView;
    boolean linkGenerating;
    RLottieDrawable linkIcon;
    private final TextView manage;
    private final TextView subtitle;
    private final TextView titleView;

    public PermanentLinkBottomSheet(Context context, boolean needFocus, final BaseFragment fragment, final TLRPC.ChatFull info, long chatId, boolean isChannel) {
        super(context, needFocus);
        String str;
        int i;
        this.info = info;
        this.chatId = chatId;
        this.isChannel = isChannel;
        setAllowNestedScroll(true);
        setApplyBottomPadding(false);
        LinkActionView linkActionView = new LinkActionView(context, fragment, this, chatId, true, isChannel);
        this.linkActionView = linkActionView;
        linkActionView.setPermanent(true);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.shared_link_enter, "2131558527", AndroidUtilities.dp(90.0f), AndroidUtilities.dp(90.0f), false, null);
        this.linkIcon = rLottieDrawable;
        rLottieDrawable.setCustomEndFrame(42);
        rLottieImageView.setAnimation(this.linkIcon);
        linkActionView.setUsers(0, null);
        linkActionView.hideRevokeOption(true);
        linkActionView.setDelegate(new LinkActionView.Delegate() { // from class: org.telegram.ui.Components.PermanentLinkBottomSheet$$ExternalSyntheticLambda5
            @Override // org.telegram.ui.Components.LinkActionView.Delegate
            public /* synthetic */ void editLink() {
                LinkActionView.Delegate.CC.$default$editLink(this);
            }

            @Override // org.telegram.ui.Components.LinkActionView.Delegate
            public /* synthetic */ void removeLink() {
                LinkActionView.Delegate.CC.$default$removeLink(this);
            }

            @Override // org.telegram.ui.Components.LinkActionView.Delegate
            public final void revokeLink() {
                PermanentLinkBottomSheet.this.m2805lambda$new$0$orgtelegramuiComponentsPermanentLinkBottomSheet();
            }

            @Override // org.telegram.ui.Components.LinkActionView.Delegate
            public /* synthetic */ void showUsersForPermanentLink() {
                LinkActionView.Delegate.CC.$default$showUsersForPermanentLink(this);
            }
        });
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setText(LocaleController.getString("InviteLink", R.string.InviteLink));
        textView.setTextSize(24.0f);
        textView.setGravity(1);
        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        TextView textView2 = new TextView(context);
        this.subtitle = textView2;
        if (isChannel) {
            i = R.string.LinkInfoChannel;
            str = "LinkInfoChannel";
        } else {
            i = R.string.LinkInfo;
            str = "LinkInfo";
        }
        textView2.setText(LocaleController.getString(str, i));
        textView2.setTextSize(14.0f);
        textView2.setGravity(1);
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        TextView textView3 = new TextView(context);
        this.manage = textView3;
        textView3.setText(LocaleController.getString("ManageInviteLinks", R.string.ManageInviteLinks));
        textView3.setTextSize(14.0f);
        textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        textView3.setBackground(Theme.createRadSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText), 76), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f)));
        textView3.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f));
        textView3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.PermanentLinkBottomSheet$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PermanentLinkBottomSheet.this.m2806lambda$new$1$orgtelegramuiComponentsPermanentLinkBottomSheet(info, fragment, view);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.addView(rLottieImageView, LayoutHelper.createLinear(90, 90, 1, 0, 24, 0, 0));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 1, 60, 16, 60, 0));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 1, 60, 16, 60, 0));
        linearLayout.addView(linkActionView, LayoutHelper.createLinear(-1, -2));
        linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 1, 60, 26, 60, 26));
        NestedScrollView scrollView = new NestedScrollView(context);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
        TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(chatId));
        if (chat != null && chat.username != null) {
            linkActionView.setLink("https://t.me/" + chat.username);
            textView3.setVisibility(8);
        } else if (info == null || info.exported_invite == null) {
            generateLink(false);
        } else {
            linkActionView.setLink(info.exported_invite.link);
        }
        updateColors();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-PermanentLinkBottomSheet */
    public /* synthetic */ void m2805lambda$new$0$orgtelegramuiComponentsPermanentLinkBottomSheet() {
        generateLink(true);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-PermanentLinkBottomSheet */
    public /* synthetic */ void m2806lambda$new$1$orgtelegramuiComponentsPermanentLinkBottomSheet(TLRPC.ChatFull info, BaseFragment fragment, View view) {
        ManageLinksActivity manageFragment = new ManageLinksActivity(info.id, 0L, 0);
        manageFragment.setInfo(info, info.exported_invite);
        fragment.presentFragment(manageFragment);
        dismiss();
    }

    private void generateLink(final boolean showDialog) {
        if (this.linkGenerating) {
            return;
        }
        this.linkGenerating = true;
        TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
        req.legacy_revoke_permanent = true;
        req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(-this.chatId);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.PermanentLinkBottomSheet$$ExternalSyntheticLambda3
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PermanentLinkBottomSheet.this.m2804xdc0454c4(showDialog, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$generateLink$3$org-telegram-ui-Components-PermanentLinkBottomSheet */
    public /* synthetic */ void m2804xdc0454c4(final boolean showDialog, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PermanentLinkBottomSheet$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                PermanentLinkBottomSheet.this.m2803x4ec9a343(error, response, showDialog);
            }
        });
    }

    /* renamed from: lambda$generateLink$2$org-telegram-ui-Components-PermanentLinkBottomSheet */
    public /* synthetic */ void m2803x4ec9a343(TLRPC.TL_error error, TLObject response, boolean showDialog) {
        if (error == null) {
            this.invite = (TLRPC.TL_chatInviteExported) response;
            TLRPC.ChatFull chatInfo = MessagesController.getInstance(this.currentAccount).getChatFull(this.chatId);
            if (chatInfo != null) {
                chatInfo.exported_invite = this.invite;
            }
            this.linkActionView.setLink(this.invite.link);
            if (showDialog && this.fragment != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(LocaleController.getString("RevokeAlertNewLink", R.string.RevokeAlertNewLink));
                builder.setTitle(LocaleController.getString("RevokeLink", R.string.RevokeLink));
                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                this.fragment.showDialog(builder.create());
            }
        }
        this.linkGenerating = false;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.PermanentLinkBottomSheet$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                PermanentLinkBottomSheet.this.m2807x3915b8b3();
            }
        }, 50L);
    }

    /* renamed from: lambda$show$4$org-telegram-ui-Components-PermanentLinkBottomSheet */
    public /* synthetic */ void m2807x3915b8b3() {
        this.linkIcon.start();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.Components.PermanentLinkBottomSheet$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                PermanentLinkBottomSheet.this.updateColors();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.titleView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.subtitle, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(this.manage, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueText));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_featuredStickers_addButton));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_featuredStickers_buttonText));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, descriptionDelegate, Theme.key_windowBackgroundWhiteBlueText));
        return arrayList;
    }

    public void updateColors() {
        this.imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(90.0f), Theme.getColor(Theme.key_featuredStickers_addButton)));
        this.manage.setBackground(Theme.createRadSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText), 76), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f)));
        int color = Theme.getColor(Theme.key_featuredStickers_buttonText);
        this.linkIcon.setLayerColor("Top.**", color);
        this.linkIcon.setLayerColor("Bottom.**", color);
        this.linkIcon.setLayerColor("Center.**", color);
        this.linkActionView.updateColors();
        setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        super.dismissInternal();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
    }
}
