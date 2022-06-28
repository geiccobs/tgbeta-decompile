package org.telegram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.messaging.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.PrivacyControlActivity;
import org.telegram.ui.PrivacyUsersActivity;
/* loaded from: classes4.dex */
public class PrivacyControlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int PRIVACY_RULES_TYPE_ADDED_BY_PHONE = 7;
    public static final int PRIVACY_RULES_TYPE_CALLS = 2;
    public static final int PRIVACY_RULES_TYPE_FORWARDS = 5;
    public static final int PRIVACY_RULES_TYPE_INVITE = 1;
    public static final int PRIVACY_RULES_TYPE_LASTSEEN = 0;
    public static final int PRIVACY_RULES_TYPE_P2P = 3;
    public static final int PRIVACY_RULES_TYPE_PHONE = 6;
    public static final int PRIVACY_RULES_TYPE_PHOTO = 4;
    public static final int TYPE_CONTACTS = 2;
    public static final int TYPE_EVERYBODY = 0;
    public static final int TYPE_NOBODY = 1;
    private static final int done_button = 1;
    private int alwaysShareRow;
    private ArrayList<Long> currentMinus;
    private ArrayList<Long> currentPlus;
    private int currentSubType;
    private int currentType;
    private int detailRow;
    private View doneButton;
    private int everybodyRow;
    private ArrayList<Long> initialMinus;
    private ArrayList<Long> initialPlus;
    private int initialRulesSubType;
    private int initialRulesType;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private MessageCell messageCell;
    private int messageRow;
    private int myContactsRow;
    private int neverShareRow;
    private int nobodyRow;
    private int p2pDetailRow;
    private int p2pRow;
    private int p2pSectionRow;
    private int phoneContactsRow;
    private int phoneDetailRow;
    private int phoneEverybodyRow;
    private int phoneSectionRow;
    private boolean prevSubtypeContacts;
    private int rowCount;
    private int rulesType;
    private int sectionRow;
    private int shareDetailRow;
    private int shareSectionRow;

    /* loaded from: classes4.dex */
    public class MessageCell extends FrameLayout {
        private Drawable backgroundDrawable;
        private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
        private ChatMessageCell cell;
        private HintView hintView;
        private final Runnable invalidateRunnable = new Runnable() { // from class: org.telegram.ui.PrivacyControlActivity$MessageCell$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PrivacyControlActivity.MessageCell.this.invalidate();
            }
        };
        private MessageObject messageObject;
        private Drawable shadowDrawable;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public MessageCell(Context context) {
            super(context);
            PrivacyControlActivity.this = r18;
            setWillNotDraw(false);
            setClipToPadding(false);
            this.shadowDrawable = Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
            setPadding(0, AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f));
            int date = ((int) (System.currentTimeMillis() / 1000)) - 3600;
            TLRPC.User currentUser = MessagesController.getInstance(r18.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(r18.currentAccount).getClientUserId()));
            TLRPC.Message message = new TLRPC.TL_message();
            message.message = LocaleController.getString("PrivacyForwardsMessageLine", R.string.PrivacyForwardsMessageLine);
            message.date = date + 60;
            message.dialog_id = 1L;
            message.flags = 261;
            message.from_id = new TLRPC.TL_peerUser();
            message.id = 1;
            message.fwd_from = new TLRPC.TL_messageFwdHeader();
            message.fwd_from.from_name = ContactsController.formatName(currentUser.first_name, currentUser.last_name);
            message.media = new TLRPC.TL_messageMediaEmpty();
            message.out = false;
            message.peer_id = new TLRPC.TL_peerUser();
            message.peer_id.user_id = UserConfig.getInstance(r18.currentAccount).getClientUserId();
            MessageObject messageObject = new MessageObject(r18.currentAccount, message, true, false);
            this.messageObject = messageObject;
            messageObject.eventId = 1L;
            this.messageObject.resetLayout();
            ChatMessageCell chatMessageCell = new ChatMessageCell(context);
            this.cell = chatMessageCell;
            chatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate() { // from class: org.telegram.ui.PrivacyControlActivity.MessageCell.1
                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean canDrawOutboundsContent() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean canPerformActions() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canPerformActions(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didLongPress(ChatMessageCell chatMessageCell2, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPress(this, chatMessageCell2, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didLongPressBotButton(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressBotButton(this, chatMessageCell2, keyboardButton);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, int i, float f, float f2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell2, chat, i, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC.User user, float f, float f2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressUserAvatar(this, chatMessageCell2, user, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell2, TLRPC.KeyboardButton keyboardButton) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell2, keyboardButton);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressCancelSendButton(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCancelSendButton(this, chatMessageCell2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressChannelAvatar(ChatMessageCell chatMessageCell2, TLRPC.Chat chat, int i, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressChannelAvatar(this, chatMessageCell2, chat, i, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressCommentButton(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressHint(ChatMessageCell chatMessageCell2, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell2, i);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressImage(ChatMessageCell chatMessageCell2, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressImage(this, chatMessageCell2, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressInstantButton(ChatMessageCell chatMessageCell2, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressInstantButton(this, chatMessageCell2, i);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressOther(ChatMessageCell chatMessageCell2, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressOther(this, chatMessageCell2, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell2, TLRPC.TL_reactionCount tL_reactionCount, boolean z) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell2, tL_reactionCount, z);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressReplyMessage(ChatMessageCell chatMessageCell2, int i) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReplyMessage(this, chatMessageCell2, i);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressSideButton(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressSideButton(this, chatMessageCell2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressTime(ChatMessageCell chatMessageCell2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressUrl(ChatMessageCell chatMessageCell2, CharacterStyle characterStyle, boolean z) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUrl(this, chatMessageCell2, characterStyle, z);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressUserAvatar(ChatMessageCell chatMessageCell2, TLRPC.User user, float f, float f2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressUserAvatar(this, chatMessageCell2, user, f, f2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressViaBot(ChatMessageCell chatMessageCell2, String str) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBot(this, chatMessageCell2, str);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressViaBotNotInline(ChatMessageCell chatMessageCell2, long j) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell2, j);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell2, ArrayList arrayList, int i, int i2, int i3) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell2, arrayList, i, i2, i3);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void didStartVideoStream(MessageObject messageObject2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ String getAdminRank(long j) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getAdminRank(this, j);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ PinchToZoomHelper getPinchToZoomHelper() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getPinchToZoomHelper(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$getTextSelectionHelper(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean hasSelectedMessages() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$hasSelectedMessages(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void invalidateBlur() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$invalidateBlur(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean isLandscape() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$isLandscape(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean keyboardIsOpened() {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$keyboardIsOpened(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void needOpenWebView(MessageObject messageObject2, String str, String str2, String str3, String str4, int i, int i2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needOpenWebView(this, messageObject2, str, str2, str3, str4, i, i2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean needPlayMessage(MessageObject messageObject2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$needPlayMessage(this, messageObject2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void needReloadPolls() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needReloadPolls(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void needShowPremiumFeatures(String str) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$needShowPremiumFeatures(this, str);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean onAccessibilityAction(int i, Bundle bundle) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$onAccessibilityAction(this, i, bundle);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void onDiceFinished() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$onDiceFinished(this);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject2) {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject2) {
                    return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject2);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public /* synthetic */ void videoTimerReached() {
                    ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
                }
            });
            this.cell.isChat = false;
            this.cell.setFullyDraw(true);
            this.cell.setMessageObject(this.messageObject, null, false, false);
            addView(this.cell, LayoutHelper.createLinear(-1, -2));
            HintView hintView = new HintView(context, 1, true);
            this.hintView = hintView;
            addView(hintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            this.hintView.showForMessageCell(this.cell, false);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            Drawable newDrawable = Theme.getCachedWallpaperNonBlocking();
            if (newDrawable != null && this.backgroundDrawable != newDrawable) {
                BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                if (disposable != null) {
                    disposable.dispose();
                    this.backgroundGradientDisposable = null;
                }
                this.backgroundDrawable = newDrawable;
            }
            Drawable drawable = this.backgroundDrawable;
            if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable) || (drawable instanceof MotionBackgroundDrawable)) {
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                Drawable drawable2 = this.backgroundDrawable;
                if (drawable2 instanceof BackgroundGradientDrawable) {
                    this.backgroundGradientDisposable = ((BackgroundGradientDrawable) drawable2).drawExactBoundsSize(canvas, this);
                } else {
                    drawable2.draw(canvas);
                }
            } else if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getTileModeX() == Shader.TileMode.REPEAT) {
                    canvas.save();
                    float scale = 2.0f / AndroidUtilities.density;
                    canvas.scale(scale, scale);
                    this.backgroundDrawable.setBounds(0, 0, (int) Math.ceil(getMeasuredWidth() / scale), (int) Math.ceil(getMeasuredHeight() / scale));
                } else {
                    int viewHeight = getMeasuredHeight();
                    float scaleX = getMeasuredWidth() / this.backgroundDrawable.getIntrinsicWidth();
                    float scaleY = viewHeight / this.backgroundDrawable.getIntrinsicHeight();
                    float scale2 = Math.max(scaleX, scaleY);
                    int width = (int) Math.ceil(this.backgroundDrawable.getIntrinsicWidth() * scale2);
                    int height = (int) Math.ceil(this.backgroundDrawable.getIntrinsicHeight() * scale2);
                    int x = (getMeasuredWidth() - width) / 2;
                    int y = (viewHeight - height) / 2;
                    canvas.save();
                    canvas.clipRect(0, 0, width, getMeasuredHeight());
                    this.backgroundDrawable.setBounds(x, y, x + width, y + height);
                }
                this.backgroundDrawable.draw(canvas);
                canvas.restore();
            } else {
                super.onDraw(canvas);
            }
            this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.shadowDrawable.draw(canvas);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
            if (disposable != null) {
                disposable.dispose();
                this.backgroundGradientDisposable = null;
            }
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return false;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent ev) {
            return false;
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchSetPressed(boolean pressed) {
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            return false;
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            this.cell.invalidate();
        }
    }

    public PrivacyControlActivity(int type) {
        this(type, false);
    }

    public PrivacyControlActivity(int type, boolean load) {
        this.initialPlus = new ArrayList<>();
        this.initialMinus = new ArrayList<>();
        this.rulesType = type;
        if (load) {
            ContactsController.getInstance(this.currentAccount).loadPrivacySettings();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        checkPrivacy();
        updateRows(false);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        if (this.rulesType == 5) {
            this.messageCell = new MessageCell(context);
        }
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        int i = this.rulesType;
        if (i == 6) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyPhone", R.string.PrivacyPhone));
        } else if (i == 5) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyForwards", R.string.PrivacyForwards));
        } else if (i == 4) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyProfilePhoto", R.string.PrivacyProfilePhoto));
        } else if (i == 3) {
            this.actionBar.setTitle(LocaleController.getString("PrivacyP2P", R.string.PrivacyP2P));
        } else if (i == 2) {
            this.actionBar.setTitle(LocaleController.getString("Calls", R.string.Calls));
        } else if (i == 1) {
            this.actionBar.setTitle(LocaleController.getString("GroupsAndChannels", R.string.GroupsAndChannels));
        } else {
            this.actionBar.setTitle(LocaleController.getString("PrivacyLastSeen", R.string.PrivacyLastSeen));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.PrivacyControlActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (PrivacyControlActivity.this.checkDiscard()) {
                        PrivacyControlActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    PrivacyControlActivity.this.processDone();
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        this.doneButton = menu.addItemWithWidth(1, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", R.string.Done));
        boolean hasChanges = hasChanges();
        float f = 1.0f;
        this.doneButton.setAlpha(hasChanges ? 1.0f : 0.0f);
        this.doneButton.setScaleX(hasChanges ? 1.0f : 0.0f);
        View view = this.doneButton;
        if (!hasChanges) {
            f = 0.0f;
        }
        view.setScaleY(f);
        this.doneButton.setEnabled(hasChanges);
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.PrivacyControlActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view2, int i2) {
                PrivacyControlActivity.this.m4328lambda$createView$2$orgtelegramuiPrivacyControlActivity(view2, i2);
            }
        });
        setMessageText();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PrivacyControlActivity */
    public /* synthetic */ void m4328lambda$createView$2$orgtelegramuiPrivacyControlActivity(View view, final int position) {
        int newType;
        int newType2;
        ArrayList<Long> createFromArray;
        int i = this.nobodyRow;
        boolean z = true;
        int i2 = 1;
        if (position == i || position == this.everybodyRow || position == this.myContactsRow) {
            if (position == i) {
                newType = 1;
            } else if (position == this.everybodyRow) {
                newType = 0;
            } else {
                newType = 2;
            }
            if (newType == this.currentType) {
                return;
            }
            this.currentType = newType;
            updateDoneButton();
            updateRows(true);
        } else if (position == this.phoneContactsRow || position == this.phoneEverybodyRow) {
            if (position == this.phoneEverybodyRow) {
                newType2 = 0;
            } else {
                newType2 = 1;
            }
            if (newType2 == this.currentSubType) {
                return;
            }
            this.currentSubType = newType2;
            updateDoneButton();
            updateRows(true);
        } else {
            int i3 = this.neverShareRow;
            if (position == i3 || position == this.alwaysShareRow) {
                if (position == i3) {
                    createFromArray = this.currentMinus;
                } else {
                    createFromArray = this.currentPlus;
                }
                if (createFromArray.isEmpty()) {
                    Bundle args = new Bundle();
                    args.putBoolean(position == this.neverShareRow ? "isNeverShare" : "isAlwaysShare", true);
                    if (this.rulesType == 0) {
                        i2 = 0;
                    }
                    args.putInt("chatAddType", i2);
                    GroupCreateActivity fragment = new GroupCreateActivity(args);
                    fragment.setDelegate(new GroupCreateActivity.GroupCreateActivityDelegate() { // from class: org.telegram.ui.PrivacyControlActivity$$ExternalSyntheticLambda8
                        @Override // org.telegram.ui.GroupCreateActivity.GroupCreateActivityDelegate
                        public final void didSelectUsers(ArrayList arrayList) {
                            PrivacyControlActivity.this.m4326lambda$createView$0$orgtelegramuiPrivacyControlActivity(position, arrayList);
                        }
                    });
                    presentFragment(fragment);
                    return;
                }
                boolean z2 = this.rulesType != 0;
                if (position != this.alwaysShareRow) {
                    z = false;
                }
                PrivacyUsersActivity fragment2 = new PrivacyUsersActivity(0, createFromArray, z2, z);
                fragment2.setDelegate(new PrivacyUsersActivity.PrivacyActivityDelegate() { // from class: org.telegram.ui.PrivacyControlActivity$$ExternalSyntheticLambda9
                    @Override // org.telegram.ui.PrivacyUsersActivity.PrivacyActivityDelegate
                    public final void didUpdateUserList(ArrayList arrayList, boolean z3) {
                        PrivacyControlActivity.this.m4327lambda$createView$1$orgtelegramuiPrivacyControlActivity(position, arrayList, z3);
                    }
                });
                presentFragment(fragment2);
            } else if (position == this.p2pRow) {
                presentFragment(new PrivacyControlActivity(3));
            }
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PrivacyControlActivity */
    public /* synthetic */ void m4326lambda$createView$0$orgtelegramuiPrivacyControlActivity(int position, ArrayList ids) {
        if (position == this.neverShareRow) {
            this.currentMinus = ids;
            for (int a = 0; a < this.currentMinus.size(); a++) {
                this.currentPlus.remove(this.currentMinus.get(a));
            }
        } else {
            this.currentPlus = ids;
            for (int a2 = 0; a2 < this.currentPlus.size(); a2++) {
                this.currentMinus.remove(this.currentPlus.get(a2));
            }
        }
        updateDoneButton();
        this.listAdapter.notifyDataSetChanged();
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PrivacyControlActivity */
    public /* synthetic */ void m4327lambda$createView$1$orgtelegramuiPrivacyControlActivity(int position, ArrayList ids, boolean added) {
        if (position == this.neverShareRow) {
            this.currentMinus = ids;
            if (added) {
                for (int a = 0; a < this.currentMinus.size(); a++) {
                    this.currentPlus.remove(this.currentMinus.get(a));
                }
            }
        } else {
            this.currentPlus = ids;
            if (added) {
                for (int a2 = 0; a2 < this.currentPlus.size(); a2++) {
                    this.currentMinus.remove(this.currentPlus.get(a2));
                }
            }
        }
        updateDoneButton();
        this.listAdapter.notifyDataSetChanged();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        MessageCell messageCell;
        if (id == NotificationCenter.privacyRulesUpdated) {
            checkPrivacy();
        } else if (id == NotificationCenter.emojiLoaded) {
            this.listView.invalidateViews();
        } else if (id == NotificationCenter.didSetNewWallpapper && (messageCell = this.messageCell) != null) {
            messageCell.invalidate();
        }
    }

    private void updateDoneButton() {
        boolean hasChanges = hasChanges();
        this.doneButton.setEnabled(hasChanges);
        float f = 1.0f;
        ViewPropertyAnimator scaleX = this.doneButton.animate().alpha(hasChanges ? 1.0f : 0.0f).scaleX(hasChanges ? 1.0f : 0.0f);
        if (!hasChanges) {
            f = 0.0f;
        }
        scaleX.scaleY(f).setDuration(180L).start();
    }

    private void applyCurrentPrivacySettings() {
        TLRPC.InputUser inputUser;
        TLRPC.InputUser inputUser2;
        TLRPC.TL_account_setPrivacy req = new TLRPC.TL_account_setPrivacy();
        int i = this.rulesType;
        if (i == 6) {
            req.key = new TLRPC.TL_inputPrivacyKeyPhoneNumber();
            if (this.currentType == 1) {
                TLRPC.TL_account_setPrivacy req2 = new TLRPC.TL_account_setPrivacy();
                req2.key = new TLRPC.TL_inputPrivacyKeyAddedByPhone();
                if (this.currentSubType == 0) {
                    req2.rules.add(new TLRPC.TL_inputPrivacyValueAllowAll());
                } else {
                    req2.rules.add(new TLRPC.TL_inputPrivacyValueAllowContacts());
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() { // from class: org.telegram.ui.PrivacyControlActivity$$ExternalSyntheticLambda5
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PrivacyControlActivity.this.m4321xd2c174b(tLObject, tL_error);
                    }
                }, 2);
            }
        } else if (i == 5) {
            req.key = new TLRPC.TL_inputPrivacyKeyForwards();
        } else if (i == 4) {
            req.key = new TLRPC.TL_inputPrivacyKeyProfilePhoto();
        } else if (i == 3) {
            req.key = new TLRPC.TL_inputPrivacyKeyPhoneP2P();
        } else if (i == 2) {
            req.key = new TLRPC.TL_inputPrivacyKeyPhoneCall();
        } else if (i == 1) {
            req.key = new TLRPC.TL_inputPrivacyKeyChatInvite();
        } else {
            req.key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
        }
        if (this.currentType != 0 && this.currentPlus.size() > 0) {
            TLRPC.TL_inputPrivacyValueAllowUsers usersRule = new TLRPC.TL_inputPrivacyValueAllowUsers();
            TLRPC.TL_inputPrivacyValueAllowChatParticipants chatsRule = new TLRPC.TL_inputPrivacyValueAllowChatParticipants();
            for (int a = 0; a < this.currentPlus.size(); a++) {
                long id = this.currentPlus.get(a).longValue();
                if (DialogObject.isUserDialog(id)) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(id));
                    if (user != null && (inputUser2 = MessagesController.getInstance(this.currentAccount).getInputUser(user)) != null) {
                        usersRule.users.add(inputUser2);
                    }
                } else {
                    chatsRule.chats.add(Long.valueOf(-id));
                }
            }
            req.rules.add(usersRule);
            req.rules.add(chatsRule);
        }
        if (this.currentType != 1 && this.currentMinus.size() > 0) {
            TLRPC.TL_inputPrivacyValueDisallowUsers usersRule2 = new TLRPC.TL_inputPrivacyValueDisallowUsers();
            TLRPC.TL_inputPrivacyValueDisallowChatParticipants chatsRule2 = new TLRPC.TL_inputPrivacyValueDisallowChatParticipants();
            for (int a2 = 0; a2 < this.currentMinus.size(); a2++) {
                long id2 = this.currentMinus.get(a2).longValue();
                if (DialogObject.isUserDialog(id2)) {
                    TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(id2));
                    if (user2 != null && (inputUser = getMessagesController().getInputUser(user2)) != null) {
                        usersRule2.users.add(inputUser);
                    }
                } else {
                    chatsRule2.chats.add(Long.valueOf(-id2));
                }
            }
            req.rules.add(usersRule2);
            req.rules.add(chatsRule2);
        }
        int i2 = this.currentType;
        if (i2 == 0) {
            req.rules.add(new TLRPC.TL_inputPrivacyValueAllowAll());
        } else if (i2 == 1) {
            req.rules.add(new TLRPC.TL_inputPrivacyValueDisallowAll());
        } else if (i2 == 2) {
            req.rules.add(new TLRPC.TL_inputPrivacyValueAllowContacts());
        }
        AlertDialog progressDialog = null;
        if (getParentActivity() != null) {
            progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCancel(false);
            progressDialog.show();
        }
        final AlertDialog progressDialogFinal = progressDialog;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PrivacyControlActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PrivacyControlActivity.this.m4323xbb67f909(progressDialogFinal, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$applyCurrentPrivacySettings$4$org-telegram-ui-PrivacyControlActivity */
    public /* synthetic */ void m4321xd2c174b(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PrivacyControlActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                PrivacyControlActivity.this.m4320xb60e266c(error, response);
            }
        });
    }

    /* renamed from: lambda$applyCurrentPrivacySettings$3$org-telegram-ui-PrivacyControlActivity */
    public /* synthetic */ void m4320xb60e266c(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            TLRPC.TL_account_privacyRules privacyRules = (TLRPC.TL_account_privacyRules) response;
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(privacyRules.rules, 7);
        }
    }

    /* renamed from: lambda$applyCurrentPrivacySettings$6$org-telegram-ui-PrivacyControlActivity */
    public /* synthetic */ void m4323xbb67f909(final AlertDialog progressDialogFinal, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PrivacyControlActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                PrivacyControlActivity.this.m4322x644a082a(progressDialogFinal, error, response);
            }
        });
    }

    /* renamed from: lambda$applyCurrentPrivacySettings$5$org-telegram-ui-PrivacyControlActivity */
    public /* synthetic */ void m4322x644a082a(AlertDialog progressDialogFinal, TLRPC.TL_error error, TLObject response) {
        if (progressDialogFinal != null) {
            try {
                progressDialogFinal.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (error == null) {
            TLRPC.TL_account_privacyRules privacyRules = (TLRPC.TL_account_privacyRules) response;
            MessagesController.getInstance(this.currentAccount).putUsers(privacyRules.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(privacyRules.chats, false);
            ContactsController.getInstance(this.currentAccount).setPrivacyRules(privacyRules.rules, this.rulesType);
            finishFragment();
            return;
        }
        showErrorAlert();
    }

    private void showErrorAlert() {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("PrivacyFloodControlError", R.string.PrivacyFloodControlError));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        showDialog(builder.create());
    }

    private void checkPrivacy() {
        this.currentPlus = new ArrayList<>();
        this.currentMinus = new ArrayList<>();
        ArrayList<TLRPC.PrivacyRule> privacyRules = ContactsController.getInstance(this.currentAccount).getPrivacyRules(this.rulesType);
        if (privacyRules == null || privacyRules.size() == 0) {
            this.currentType = 1;
        } else {
            int type = -1;
            for (int a = 0; a < privacyRules.size(); a++) {
                TLRPC.PrivacyRule rule = privacyRules.get(a);
                if (rule instanceof TLRPC.TL_privacyValueAllowChatParticipants) {
                    TLRPC.TL_privacyValueAllowChatParticipants privacyValueAllowChatParticipants = (TLRPC.TL_privacyValueAllowChatParticipants) rule;
                    int N = privacyValueAllowChatParticipants.chats.size();
                    for (int b = 0; b < N; b++) {
                        this.currentPlus.add(Long.valueOf(-privacyValueAllowChatParticipants.chats.get(b).longValue()));
                    }
                } else if (rule instanceof TLRPC.TL_privacyValueDisallowChatParticipants) {
                    TLRPC.TL_privacyValueDisallowChatParticipants privacyValueDisallowChatParticipants = (TLRPC.TL_privacyValueDisallowChatParticipants) rule;
                    int N2 = privacyValueDisallowChatParticipants.chats.size();
                    for (int b2 = 0; b2 < N2; b2++) {
                        this.currentMinus.add(Long.valueOf(-privacyValueDisallowChatParticipants.chats.get(b2).longValue()));
                    }
                } else if (rule instanceof TLRPC.TL_privacyValueAllowUsers) {
                    TLRPC.TL_privacyValueAllowUsers privacyValueAllowUsers = (TLRPC.TL_privacyValueAllowUsers) rule;
                    this.currentPlus.addAll(privacyValueAllowUsers.users);
                } else if (rule instanceof TLRPC.TL_privacyValueDisallowUsers) {
                    TLRPC.TL_privacyValueDisallowUsers privacyValueDisallowUsers = (TLRPC.TL_privacyValueDisallowUsers) rule;
                    this.currentMinus.addAll(privacyValueDisallowUsers.users);
                } else if (type == -1) {
                    if (rule instanceof TLRPC.TL_privacyValueAllowAll) {
                        type = 0;
                    } else if (rule instanceof TLRPC.TL_privacyValueDisallowAll) {
                        type = 1;
                    } else {
                        type = 2;
                    }
                }
            }
            if (type == 0 || (type == -1 && this.currentMinus.size() > 0)) {
                this.currentType = 0;
            } else if (type == 2 || (type == -1 && this.currentMinus.size() > 0 && this.currentPlus.size() > 0)) {
                this.currentType = 2;
            } else if (type == 1 || (type == -1 && this.currentPlus.size() > 0)) {
                this.currentType = 1;
            }
            View view = this.doneButton;
            if (view != null) {
                view.setAlpha(0.0f);
                this.doneButton.setScaleX(0.0f);
                this.doneButton.setScaleY(0.0f);
                this.doneButton.setEnabled(false);
            }
        }
        this.initialPlus.clear();
        this.initialMinus.clear();
        this.initialRulesType = this.currentType;
        this.initialPlus.addAll(this.currentPlus);
        this.initialMinus.addAll(this.currentMinus);
        if (this.rulesType == 6) {
            ArrayList<TLRPC.PrivacyRule> privacyRules2 = ContactsController.getInstance(this.currentAccount).getPrivacyRules(7);
            if (privacyRules2 == null || privacyRules2.size() == 0) {
                this.currentSubType = 0;
            } else {
                int a2 = 0;
                while (true) {
                    if (a2 >= privacyRules2.size()) {
                        break;
                    }
                    TLRPC.PrivacyRule rule2 = privacyRules2.get(a2);
                    if (rule2 instanceof TLRPC.TL_privacyValueAllowAll) {
                        this.currentSubType = 0;
                        break;
                    } else if (rule2 instanceof TLRPC.TL_privacyValueDisallowAll) {
                        this.currentSubType = 2;
                        break;
                    } else if (!(rule2 instanceof TLRPC.TL_privacyValueAllowContacts)) {
                        a2++;
                    } else {
                        this.currentSubType = 1;
                        break;
                    }
                }
            }
            this.initialRulesSubType = this.currentSubType;
        }
        updateRows(false);
    }

    private boolean hasChanges() {
        int i = this.initialRulesType;
        int i2 = this.currentType;
        if (i != i2) {
            return true;
        }
        if ((this.rulesType == 6 && i2 == 1 && this.initialRulesSubType != this.currentSubType) || this.initialMinus.size() != this.currentMinus.size() || this.initialPlus.size() != this.currentPlus.size()) {
            return true;
        }
        Collections.sort(this.initialPlus);
        Collections.sort(this.currentPlus);
        if (!this.initialPlus.equals(this.currentPlus)) {
            return true;
        }
        Collections.sort(this.initialMinus);
        Collections.sort(this.currentMinus);
        return !this.initialMinus.equals(this.currentMinus);
    }

    private void updateRows(boolean animated) {
        RecyclerView.ViewHolder holder;
        int checkedType;
        int checkedType2;
        int prevAlwaysShareRow = this.alwaysShareRow;
        int prevNeverShareRow = this.neverShareRow;
        int prevPhoneDetailRow = this.phoneDetailRow;
        int prevDetailRow = this.detailRow;
        int i = this.currentType;
        boolean newSubtype = i == 1 && this.currentSubType == 1;
        this.rowCount = 0;
        int i2 = this.rulesType;
        if (i2 == 5) {
            this.rowCount = 0 + 1;
            this.messageRow = 0;
        } else {
            this.messageRow = -1;
        }
        int i3 = this.rowCount;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.sectionRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.everybodyRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.myContactsRow = i5;
        if (i2 == 0 || i2 == 2 || i2 == 3 || i2 == 5 || i2 == 6) {
            this.rowCount = i6 + 1;
            this.nobodyRow = i6;
        } else {
            this.nobodyRow = -1;
        }
        if (i2 == 6 && i == 1) {
            int i7 = this.rowCount;
            int i8 = i7 + 1;
            this.rowCount = i8;
            this.phoneDetailRow = i7;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.phoneSectionRow = i8;
            int i10 = i9 + 1;
            this.rowCount = i10;
            this.phoneEverybodyRow = i9;
            this.rowCount = i10 + 1;
            this.phoneContactsRow = i10;
        } else {
            this.phoneDetailRow = -1;
            this.phoneSectionRow = -1;
            this.phoneEverybodyRow = -1;
            this.phoneContactsRow = -1;
        }
        int i11 = this.rowCount;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.detailRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.shareSectionRow = i12;
        if (i == 1 || i == 2) {
            this.rowCount = i13 + 1;
            this.alwaysShareRow = i13;
        } else {
            this.alwaysShareRow = -1;
        }
        if (i == 0 || i == 2) {
            int i14 = this.rowCount;
            this.rowCount = i14 + 1;
            this.neverShareRow = i14;
        } else {
            this.neverShareRow = -1;
        }
        int i15 = this.rowCount;
        int i16 = i15 + 1;
        this.rowCount = i16;
        this.shareDetailRow = i15;
        if (i2 == 2) {
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.p2pSectionRow = i16;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.p2pRow = i17;
            this.rowCount = i18 + 1;
            this.p2pDetailRow = i18;
        } else {
            this.p2pSectionRow = -1;
            this.p2pRow = -1;
            this.p2pDetailRow = -1;
        }
        setMessageText();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            if (animated) {
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.listView.getChildAt(a);
                    if ((child instanceof RadioCell) && (holder = this.listView.findContainingViewHolder(child)) != null) {
                        int position = holder.getAdapterPosition();
                        RadioCell radioCell = (RadioCell) child;
                        int i19 = this.everybodyRow;
                        if (position == i19 || position == this.myContactsRow || position == this.nobodyRow) {
                            if (position == i19) {
                                checkedType = 0;
                            } else {
                                int checkedType3 = this.myContactsRow;
                                if (position == checkedType3) {
                                    checkedType = 2;
                                } else {
                                    checkedType = 1;
                                }
                            }
                            radioCell.setChecked(this.currentType == checkedType, true);
                        } else {
                            if (position == this.phoneContactsRow) {
                                checkedType2 = 1;
                            } else {
                                checkedType2 = 0;
                            }
                            radioCell.setChecked(this.currentSubType == checkedType2, true);
                        }
                    }
                }
                if (this.prevSubtypeContacts != newSubtype) {
                    this.listAdapter.notifyItemChanged(prevDetailRow);
                }
                int i20 = this.alwaysShareRow;
                if ((i20 == -1 && prevAlwaysShareRow != -1 && this.neverShareRow != -1 && prevNeverShareRow == -1) || (i20 != -1 && prevAlwaysShareRow == -1 && this.neverShareRow == -1 && prevNeverShareRow != -1)) {
                    this.listAdapter.notifyItemChanged(i20 == -1 ? prevAlwaysShareRow : prevNeverShareRow);
                    int i21 = this.phoneDetailRow;
                    if (i21 == -1 && prevPhoneDetailRow != -1) {
                        this.listAdapter.notifyItemRangeRemoved(prevPhoneDetailRow, 4);
                        return;
                    } else if (i21 != -1 && prevPhoneDetailRow == -1) {
                        this.listAdapter.notifyItemRangeInserted(i21, 4);
                        return;
                    } else {
                        return;
                    }
                }
                if (i20 == -1 && prevAlwaysShareRow != -1) {
                    this.listAdapter.notifyItemRemoved(prevAlwaysShareRow);
                } else if (i20 != -1 && prevAlwaysShareRow == -1) {
                    this.listAdapter.notifyItemInserted(i20);
                }
                int i22 = this.neverShareRow;
                if (i22 == -1 && prevNeverShareRow != -1) {
                    this.listAdapter.notifyItemRemoved(prevNeverShareRow);
                    int i23 = this.phoneDetailRow;
                    if (i23 == -1 && prevPhoneDetailRow != -1) {
                        this.listAdapter.notifyItemRangeRemoved(prevPhoneDetailRow, 4);
                        return;
                    } else if (i23 != -1 && prevPhoneDetailRow == -1) {
                        this.listAdapter.notifyItemRangeInserted(i23, 4);
                        return;
                    } else {
                        return;
                    }
                } else if (i22 != -1 && prevNeverShareRow == -1) {
                    int i24 = this.phoneDetailRow;
                    if (i24 == -1 && prevPhoneDetailRow != -1) {
                        this.listAdapter.notifyItemRangeRemoved(prevPhoneDetailRow, 4);
                    } else if (i24 != -1 && prevPhoneDetailRow == -1) {
                        this.listAdapter.notifyItemRangeInserted(i24, 4);
                    }
                    this.listAdapter.notifyItemInserted(this.neverShareRow);
                    return;
                } else {
                    return;
                }
            }
            listAdapter.notifyDataSetChanged();
        }
    }

    private void setMessageText() {
        MessageCell messageCell = this.messageCell;
        if (messageCell != null) {
            messageCell.messageObject.messageOwner.fwd_from.from_id = new TLRPC.TL_peerUser();
            int i = this.currentType;
            if (i == 0) {
                this.messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsEverybody", R.string.PrivacyForwardsEverybody));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id.user_id = 1L;
            } else if (i == 1) {
                this.messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsNobody", R.string.PrivacyForwardsNobody));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id.user_id = 0L;
            } else {
                this.messageCell.hintView.setOverrideText(LocaleController.getString("PrivacyForwardsContacts", R.string.PrivacyForwardsContacts));
                this.messageCell.messageObject.messageOwner.fwd_from.from_id.user_id = 1L;
            }
            this.messageCell.cell.forceResetMessageObject();
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        return checkDiscard();
    }

    public void processDone() {
        if (getParentActivity() == null) {
            return;
        }
        if (this.currentType != 0 && this.rulesType == 0) {
            final SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            boolean showed = preferences.getBoolean("privacyAlertShowed", false);
            if (!showed) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                if (this.rulesType == 1) {
                    builder.setMessage(LocaleController.getString("WhoCanAddMeInfo", R.string.WhoCanAddMeInfo));
                } else {
                    builder.setMessage(LocaleController.getString("CustomHelp", R.string.CustomHelp));
                }
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PrivacyControlActivity$$ExternalSyntheticLambda2
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PrivacyControlActivity.this.m4329lambda$processDone$7$orgtelegramuiPrivacyControlActivity(preferences, dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
                return;
            }
        }
        applyCurrentPrivacySettings();
    }

    /* renamed from: lambda$processDone$7$org-telegram-ui-PrivacyControlActivity */
    public /* synthetic */ void m4329lambda$processDone$7$orgtelegramuiPrivacyControlActivity(SharedPreferences preferences, DialogInterface dialogInterface, int i) {
        applyCurrentPrivacySettings();
        preferences.edit().putBoolean("privacyAlertShowed", true).commit();
    }

    public boolean checkDiscard() {
        if (this.doneButton.getAlpha() == 1.0f) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
            builder.setMessage(LocaleController.getString("PrivacySettingsChangedAlert", R.string.PrivacySettingsChangedAlert));
            builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PrivacyControlActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PrivacyControlActivity.this.m4324lambda$checkDiscard$8$orgtelegramuiPrivacyControlActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PrivacyControlActivity$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PrivacyControlActivity.this.m4325lambda$checkDiscard$9$orgtelegramuiPrivacyControlActivity(dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return false;
        }
        return true;
    }

    /* renamed from: lambda$checkDiscard$8$org-telegram-ui-PrivacyControlActivity */
    public /* synthetic */ void m4324lambda$checkDiscard$8$orgtelegramuiPrivacyControlActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* renamed from: lambda$checkDiscard$9$org-telegram-ui-PrivacyControlActivity */
    public /* synthetic */ void m4325lambda$checkDiscard$9$orgtelegramuiPrivacyControlActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean canBeginSlide() {
        return checkDiscard();
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            PrivacyControlActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position == PrivacyControlActivity.this.nobodyRow || position == PrivacyControlActivity.this.everybodyRow || position == PrivacyControlActivity.this.myContactsRow || position == PrivacyControlActivity.this.neverShareRow || position == PrivacyControlActivity.this.alwaysShareRow || (position == PrivacyControlActivity.this.p2pRow && !ContactsController.getInstance(PrivacyControlActivity.this.currentAccount).getLoadingPrivicyInfo(3));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return PrivacyControlActivity.this.rowCount;
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
                case 2:
                    View view3 = new HeaderCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view3;
                    break;
                case 3:
                    View view4 = new RadioCell(this.mContext);
                    view4.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view4;
                    break;
                case 4:
                    view = PrivacyControlActivity.this.messageCell;
                    break;
                default:
                    view = new ShadowSectionCell(this.mContext);
                    Drawable drawable = Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable);
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        private int getUsersCount(ArrayList<Long> arrayList) {
            int count = 0;
            for (int a = 0; a < arrayList.size(); a++) {
                long id = arrayList.get(a).longValue();
                if (id > 0) {
                    count++;
                } else {
                    TLRPC.Chat chat = PrivacyControlActivity.this.getMessagesController().getChat(Long.valueOf(-id));
                    if (chat != null) {
                        count += chat.participants_count;
                    }
                }
            }
            return count;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String value;
            String value2;
            String value3;
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == PrivacyControlActivity.this.alwaysShareRow) {
                        if (PrivacyControlActivity.this.currentPlus.size() != 0) {
                            value3 = LocaleController.formatPluralString("Users", getUsersCount(PrivacyControlActivity.this.currentPlus), new Object[0]);
                        } else {
                            value3 = LocaleController.getString("EmpryUsersPlaceholder", R.string.EmpryUsersPlaceholder);
                        }
                        if (PrivacyControlActivity.this.rulesType != 0) {
                            String string = LocaleController.getString("AlwaysAllow", R.string.AlwaysAllow);
                            if (PrivacyControlActivity.this.neverShareRow != -1) {
                                z = true;
                            }
                            textCell.setTextAndValue(string, value3, z);
                            return;
                        }
                        String string2 = LocaleController.getString("AlwaysShareWith", R.string.AlwaysShareWith);
                        if (PrivacyControlActivity.this.neverShareRow != -1) {
                            z = true;
                        }
                        textCell.setTextAndValue(string2, value3, z);
                        return;
                    } else if (position == PrivacyControlActivity.this.neverShareRow) {
                        if (PrivacyControlActivity.this.currentMinus.size() != 0) {
                            value2 = LocaleController.formatPluralString("Users", getUsersCount(PrivacyControlActivity.this.currentMinus), new Object[0]);
                        } else {
                            value2 = LocaleController.getString("EmpryUsersPlaceholder", R.string.EmpryUsersPlaceholder);
                        }
                        if (PrivacyControlActivity.this.rulesType != 0) {
                            textCell.setTextAndValue(LocaleController.getString("NeverAllow", R.string.NeverAllow), value2, false);
                            return;
                        } else {
                            textCell.setTextAndValue(LocaleController.getString("NeverShareWith", R.string.NeverShareWith), value2, false);
                            return;
                        }
                    } else if (position == PrivacyControlActivity.this.p2pRow) {
                        if (ContactsController.getInstance(PrivacyControlActivity.this.currentAccount).getLoadingPrivicyInfo(3)) {
                            value = LocaleController.getString("Loading", R.string.Loading);
                        } else {
                            value = PrivacySettingsActivity.formatRulesString(PrivacyControlActivity.this.getAccountInstance(), 3);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PrivacyP2P2", R.string.PrivacyP2P2), value, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    int backgroundResId = 0;
                    if (position == PrivacyControlActivity.this.detailRow) {
                        if (PrivacyControlActivity.this.rulesType != 6) {
                            if (PrivacyControlActivity.this.rulesType != 5) {
                                if (PrivacyControlActivity.this.rulesType != 4) {
                                    if (PrivacyControlActivity.this.rulesType != 3) {
                                        if (PrivacyControlActivity.this.rulesType != 2) {
                                            if (PrivacyControlActivity.this.rulesType == 1) {
                                                privacyCell.setText(LocaleController.getString("WhoCanAddMeInfo", R.string.WhoCanAddMeInfo));
                                            } else {
                                                privacyCell.setText(LocaleController.getString("CustomHelp", R.string.CustomHelp));
                                            }
                                        } else {
                                            privacyCell.setText(LocaleController.getString("WhoCanCallMeInfo", R.string.WhoCanCallMeInfo));
                                        }
                                    } else {
                                        privacyCell.setText(LocaleController.getString("PrivacyCallsP2PHelp", R.string.PrivacyCallsP2PHelp));
                                    }
                                } else {
                                    privacyCell.setText(LocaleController.getString("PrivacyProfilePhotoInfo", R.string.PrivacyProfilePhotoInfo));
                                }
                            } else {
                                privacyCell.setText(LocaleController.getString("PrivacyForwardsInfo", R.string.PrivacyForwardsInfo));
                            }
                        } else {
                            PrivacyControlActivity privacyControlActivity = PrivacyControlActivity.this;
                            if (privacyControlActivity.prevSubtypeContacts = privacyControlActivity.currentType == 1 && PrivacyControlActivity.this.currentSubType == 1) {
                                privacyCell.setText(LocaleController.getString("PrivacyPhoneInfo3", R.string.PrivacyPhoneInfo3));
                            } else {
                                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                                final String phoneLinkStr = String.format(Locale.ENGLISH, "https://t.me/+%s", PrivacyControlActivity.this.getUserConfig().getClientPhone());
                                SpannableString phoneLink = new SpannableString(phoneLinkStr);
                                phoneLink.setSpan(new ClickableSpan() { // from class: org.telegram.ui.PrivacyControlActivity.ListAdapter.1
                                    @Override // android.text.style.ClickableSpan
                                    public void onClick(View view) {
                                        ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                                        ClipData clip = ClipData.newPlainText(Constants.ScionAnalytics.PARAM_LABEL, phoneLinkStr);
                                        clipboard.setPrimaryClip(clip);
                                        BulletinFactory.of(PrivacyControlActivity.this).createCopyLinkBulletin(LocaleController.getString("LinkCopied", R.string.LinkCopied), PrivacyControlActivity.this.getResourceProvider()).show();
                                    }
                                }, 0, phoneLinkStr.length(), 33);
                                spannableStringBuilder.append((CharSequence) LocaleController.getString("PrivacyPhoneInfo", R.string.PrivacyPhoneInfo)).append((CharSequence) "\n\n").append((CharSequence) LocaleController.getString("PrivacyPhoneInfo4", R.string.PrivacyPhoneInfo4)).append((CharSequence) "\n").append((CharSequence) phoneLink);
                                privacyCell.setText(spannableStringBuilder);
                            }
                        }
                        backgroundResId = R.drawable.greydivider;
                    } else if (position == PrivacyControlActivity.this.shareDetailRow) {
                        if (PrivacyControlActivity.this.rulesType != 6) {
                            if (PrivacyControlActivity.this.rulesType != 5) {
                                if (PrivacyControlActivity.this.rulesType != 4) {
                                    if (PrivacyControlActivity.this.rulesType != 3) {
                                        if (PrivacyControlActivity.this.rulesType != 2) {
                                            if (PrivacyControlActivity.this.rulesType == 1) {
                                                privacyCell.setText(LocaleController.getString("CustomShareInfo", R.string.CustomShareInfo));
                                            } else {
                                                privacyCell.setText(LocaleController.getString("CustomShareSettingsHelp", R.string.CustomShareSettingsHelp));
                                            }
                                        } else {
                                            privacyCell.setText(LocaleController.getString("CustomCallInfo", R.string.CustomCallInfo));
                                        }
                                    } else {
                                        privacyCell.setText(LocaleController.getString("CustomP2PInfo", R.string.CustomP2PInfo));
                                    }
                                } else {
                                    privacyCell.setText(LocaleController.getString("PrivacyProfilePhotoInfo2", R.string.PrivacyProfilePhotoInfo2));
                                }
                            } else {
                                privacyCell.setText(LocaleController.getString("PrivacyForwardsInfo2", R.string.PrivacyForwardsInfo2));
                            }
                        } else {
                            privacyCell.setText(LocaleController.getString("PrivacyPhoneInfo2", R.string.PrivacyPhoneInfo2));
                        }
                        if (PrivacyControlActivity.this.rulesType == 2) {
                            backgroundResId = R.drawable.greydivider;
                        } else {
                            backgroundResId = R.drawable.greydivider_bottom;
                        }
                    } else if (position == PrivacyControlActivity.this.p2pDetailRow) {
                        backgroundResId = R.drawable.greydivider_bottom;
                    }
                    if (backgroundResId != 0) {
                        Drawable drawable = Theme.getThemedDrawable(this.mContext, backgroundResId, Theme.key_windowBackgroundGrayShadow);
                        CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable);
                        combinedDrawable.setFullsize(true);
                        privacyCell.setBackgroundDrawable(combinedDrawable);
                        return;
                    }
                    return;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == PrivacyControlActivity.this.sectionRow) {
                        if (PrivacyControlActivity.this.rulesType != 6) {
                            if (PrivacyControlActivity.this.rulesType != 5) {
                                if (PrivacyControlActivity.this.rulesType != 4) {
                                    if (PrivacyControlActivity.this.rulesType != 3) {
                                        if (PrivacyControlActivity.this.rulesType != 2) {
                                            if (PrivacyControlActivity.this.rulesType == 1) {
                                                headerCell.setText(LocaleController.getString("WhoCanAddMe", R.string.WhoCanAddMe));
                                                return;
                                            } else {
                                                headerCell.setText(LocaleController.getString("LastSeenTitle", R.string.LastSeenTitle));
                                                return;
                                            }
                                        }
                                        headerCell.setText(LocaleController.getString("WhoCanCallMe", R.string.WhoCanCallMe));
                                        return;
                                    }
                                    headerCell.setText(LocaleController.getString("P2PEnabledWith", R.string.P2PEnabledWith));
                                    return;
                                }
                                headerCell.setText(LocaleController.getString("PrivacyProfilePhotoTitle", R.string.PrivacyProfilePhotoTitle));
                                return;
                            }
                            headerCell.setText(LocaleController.getString("PrivacyForwardsTitle", R.string.PrivacyForwardsTitle));
                            return;
                        }
                        headerCell.setText(LocaleController.getString("PrivacyPhoneTitle", R.string.PrivacyPhoneTitle));
                        return;
                    } else if (position != PrivacyControlActivity.this.shareSectionRow) {
                        if (position != PrivacyControlActivity.this.p2pSectionRow) {
                            if (position == PrivacyControlActivity.this.phoneSectionRow) {
                                headerCell.setText(LocaleController.getString("PrivacyPhoneTitle2", R.string.PrivacyPhoneTitle2));
                                return;
                            }
                            return;
                        }
                        headerCell.setText(LocaleController.getString("PrivacyP2PHeader", R.string.PrivacyP2PHeader));
                        return;
                    } else {
                        headerCell.setText(LocaleController.getString("AddExceptions", R.string.AddExceptions));
                        return;
                    }
                case 3:
                    RadioCell radioCell = (RadioCell) holder.itemView;
                    if (position == PrivacyControlActivity.this.everybodyRow || position == PrivacyControlActivity.this.myContactsRow || position == PrivacyControlActivity.this.nobodyRow) {
                        if (position == PrivacyControlActivity.this.everybodyRow) {
                            if (PrivacyControlActivity.this.rulesType == 3) {
                                String string3 = LocaleController.getString("P2PEverybody", R.string.P2PEverybody);
                                if (PrivacyControlActivity.this.currentType == 0) {
                                    z = true;
                                }
                                radioCell.setText(string3, z, true);
                                return;
                            }
                            String string4 = LocaleController.getString("LastSeenEverybody", R.string.LastSeenEverybody);
                            if (PrivacyControlActivity.this.currentType == 0) {
                                z = true;
                            }
                            radioCell.setText(string4, z, true);
                            return;
                        } else if (position == PrivacyControlActivity.this.myContactsRow) {
                            if (PrivacyControlActivity.this.rulesType == 3) {
                                String string5 = LocaleController.getString("P2PContacts", R.string.P2PContacts);
                                boolean z3 = PrivacyControlActivity.this.currentType == 2;
                                if (PrivacyControlActivity.this.nobodyRow != -1) {
                                    z = true;
                                }
                                radioCell.setText(string5, z3, z);
                                return;
                            }
                            String string6 = LocaleController.getString("LastSeenContacts", R.string.LastSeenContacts);
                            boolean z4 = PrivacyControlActivity.this.currentType == 2;
                            if (PrivacyControlActivity.this.nobodyRow != -1) {
                                z = true;
                            }
                            radioCell.setText(string6, z4, z);
                            return;
                        } else if (PrivacyControlActivity.this.rulesType == 3) {
                            String string7 = LocaleController.getString("P2PNobody", R.string.P2PNobody);
                            if (PrivacyControlActivity.this.currentType != 1) {
                                z2 = false;
                            }
                            radioCell.setText(string7, z2, false);
                            return;
                        } else {
                            String string8 = LocaleController.getString("LastSeenNobody", R.string.LastSeenNobody);
                            if (PrivacyControlActivity.this.currentType != 1) {
                                z2 = false;
                            }
                            radioCell.setText(string8, z2, false);
                            return;
                        }
                    } else if (position != PrivacyControlActivity.this.phoneContactsRow) {
                        if (position == PrivacyControlActivity.this.phoneEverybodyRow) {
                            String string9 = LocaleController.getString("LastSeenEverybody", R.string.LastSeenEverybody);
                            if (PrivacyControlActivity.this.currentSubType == 0) {
                                z = true;
                            }
                            radioCell.setText(string9, z, true);
                            return;
                        }
                        return;
                    } else {
                        String string10 = LocaleController.getString("LastSeenContacts", R.string.LastSeenContacts);
                        if (PrivacyControlActivity.this.currentSubType != 1) {
                            z2 = false;
                        }
                        radioCell.setText(string10, z2, false);
                        return;
                    }
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == PrivacyControlActivity.this.alwaysShareRow || position == PrivacyControlActivity.this.neverShareRow || position == PrivacyControlActivity.this.p2pRow) {
                return 0;
            }
            if (position != PrivacyControlActivity.this.shareDetailRow && position != PrivacyControlActivity.this.detailRow && position != PrivacyControlActivity.this.p2pDetailRow) {
                if (position != PrivacyControlActivity.this.sectionRow && position != PrivacyControlActivity.this.shareSectionRow && position != PrivacyControlActivity.this.p2pSectionRow && position != PrivacyControlActivity.this.phoneSectionRow) {
                    if (position != PrivacyControlActivity.this.everybodyRow && position != PrivacyControlActivity.this.myContactsRow && position != PrivacyControlActivity.this.nobodyRow && position != PrivacyControlActivity.this.phoneEverybodyRow && position != PrivacyControlActivity.this.phoneContactsRow) {
                        if (position == PrivacyControlActivity.this.messageRow) {
                            return 4;
                        }
                        return position == PrivacyControlActivity.this.phoneDetailRow ? 5 : 0;
                    }
                    return 3;
                }
                return 2;
            }
            return 1;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, HeaderCell.class, RadioCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{RadioCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, Theme.chat_msgInDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient1));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, Theme.chat_msgOutDrawable.getShadowDrawables(), null, Theme.key_chat_outBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), null, Theme.key_chat_outBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_messageTextIn));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_messageTextOut));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, Theme.key_chat_outSentCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheckRead));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckReadSelected));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inReplyLine));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outReplyLine));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inReplyNameText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outReplyNameText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inReplyMessageText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outReplyMessageText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inTimeText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outTimeText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_inTimeSelectedText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, null, null, null, null, Theme.key_chat_outTimeSelectedText));
        return themeDescriptions;
    }
}
