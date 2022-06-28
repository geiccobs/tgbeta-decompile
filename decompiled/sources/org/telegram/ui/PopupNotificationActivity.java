package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayingGameDrawable;
import org.telegram.ui.Components.PopupAudioView;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.RoundStatusDrawable;
import org.telegram.ui.Components.SendingFileDrawable;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StatusDrawable;
import org.telegram.ui.Components.TypingDotsDrawable;
import org.telegram.ui.Components.UndoView;
/* loaded from: classes4.dex */
public class PopupNotificationActivity extends Activity implements NotificationCenter.NotificationCenterDelegate {
    private static final int id_chat_compose_panel = 1000;
    private ActionBar actionBar;
    private FrameLayout avatarContainer;
    private BackupImageView avatarImageView;
    private ViewGroup centerButtonsView;
    private ViewGroup centerView;
    private ChatActivityEnterView chatActivityEnterView;
    private int classGuid;
    private TextView countText;
    private TLRPC.Chat currentChat;
    private TLRPC.User currentUser;
    private boolean isReply;
    private CharSequence lastPrintString;
    private ViewGroup leftButtonsView;
    private ViewGroup leftView;
    private ViewGroup messageContainer;
    private TextView nameTextView;
    private TextView onlineTextView;
    private RelativeLayout popupContainer;
    private ViewGroup rightButtonsView;
    private ViewGroup rightView;
    private ArrayList<ViewGroup> textViews = new ArrayList<>();
    private ArrayList<ViewGroup> imageViews = new ArrayList<>();
    private ArrayList<ViewGroup> audioViews = new ArrayList<>();
    private VelocityTracker velocityTracker = null;
    private StatusDrawable[] statusDrawables = new StatusDrawable[5];
    private int lastResumedAccount = -1;
    private boolean finished = false;
    private MessageObject currentMessageObject = null;
    private MessageObject[] setMessageObjects = new MessageObject[3];
    private int currentMessageNum = 0;
    private PowerManager.WakeLock wakeLock = null;
    private boolean animationInProgress = false;
    private long animationStartTime = 0;
    private float moveStartX = -1.0f;
    private boolean startedMoving = false;
    private Runnable onAnimationEndRunnable = null;
    private ArrayList<MessageObject> popupMessages = new ArrayList<>();

    /* loaded from: classes4.dex */
    private class FrameLayoutTouch extends FrameLayout {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public FrameLayoutTouch(Context context) {
            super(context);
            PopupNotificationActivity.this = r1;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public FrameLayoutTouch(Context context, AttributeSet attrs) {
            super(context, attrs);
            PopupNotificationActivity.this = r1;
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public FrameLayoutTouch(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            PopupNotificationActivity.this = r1;
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(ev);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent ev) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(ev);
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            ((PopupNotificationActivity) getContext()).onTouchEventMy(null);
            super.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.createDialogsResources(this);
        Theme.createChatResources(this, false);
        AndroidUtilities.fillStatusBarHeight(this);
        for (int a = 0; a < 4; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.contactsDidLoad);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.classGuid = ConnectionsManager.generateClassGuid();
        this.statusDrawables[0] = new TypingDotsDrawable(false);
        this.statusDrawables[1] = new RecordStatusDrawable(false);
        this.statusDrawables[2] = new SendingFileDrawable(false);
        this.statusDrawables[3] = new PlayingGameDrawable(false, null);
        this.statusDrawables[4] = new RoundStatusDrawable(false);
        SizeNotifierFrameLayout contentView = new SizeNotifierFrameLayout(this) { // from class: org.telegram.ui.PopupNotificationActivity.1
            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int heightSize;
                View.MeasureSpec.getMode(widthMeasureSpec);
                View.MeasureSpec.getMode(heightMeasureSpec);
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize2 = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize2);
                int keyboardSize = measureKeyboardHeight();
                if (keyboardSize <= AndroidUtilities.dp(20.0f)) {
                    heightSize = heightSize2 - PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
                } else {
                    heightSize = heightSize2;
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        if (!PopupNotificationActivity.this.chatActivityEnterView.isPopupView(child)) {
                            if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(child)) {
                                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                            } else {
                                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f) + heightSize), C.BUFFER_FLAG_ENCRYPTED));
                            }
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, C.BUFFER_FLAG_ENCRYPTED));
                        }
                    }
                }
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                int count = getChildCount();
                int paddingBottom = measureKeyboardHeight() <= AndroidUtilities.dp(20.0f) ? PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding() : 0;
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int absoluteGravity = gravity & 7;
                        int verticalGravity = gravity & 112;
                        switch (absoluteGravity & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (r - width) - lp.rightMargin;
                                break;
                            default:
                                childLeft = lp.leftMargin;
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                int childTop2 = b - paddingBottom;
                                childTop = ((((childTop2 - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case UndoView.ACTION_EMAIL_COPIED /* 80 */:
                                int childTop3 = b - paddingBottom;
                                childTop = ((childTop3 - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (!PopupNotificationActivity.this.chatActivityEnterView.isPopupView(child)) {
                            if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(child)) {
                                childTop = ((PopupNotificationActivity.this.popupContainer.getTop() + PopupNotificationActivity.this.popupContainer.getMeasuredHeight()) - child.getMeasuredHeight()) - lp.bottomMargin;
                                childLeft = ((PopupNotificationActivity.this.popupContainer.getLeft() + PopupNotificationActivity.this.popupContainer.getMeasuredWidth()) - child.getMeasuredWidth()) - lp.rightMargin;
                            }
                        } else {
                            int measuredHeight = getMeasuredHeight();
                            if (paddingBottom != 0) {
                                measuredHeight -= paddingBottom;
                            }
                            childTop = measuredHeight;
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }
        };
        setContentView(contentView);
        contentView.setBackgroundColor(-1728053248);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        contentView.addView(relativeLayout, LayoutHelper.createFrame(-1, -1.0f));
        RelativeLayout relativeLayout2 = new RelativeLayout(this) { // from class: org.telegram.ui.PopupNotificationActivity.2
            @Override // android.widget.RelativeLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                int w = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredWidth();
                int h = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredHeight();
                for (int a2 = 0; a2 < getChildCount(); a2++) {
                    View v = getChildAt(a2);
                    if (v.getTag() instanceof String) {
                        v.measure(View.MeasureSpec.makeMeasureSpec(w, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(h - AndroidUtilities.dp(3.0f), C.BUFFER_FLAG_ENCRYPTED));
                    }
                }
            }

            @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                for (int a2 = 0; a2 < getChildCount(); a2++) {
                    View v = getChildAt(a2);
                    if (v.getTag() instanceof String) {
                        v.layout(v.getLeft(), PopupNotificationActivity.this.chatActivityEnterView.getTop() + AndroidUtilities.dp(3.0f), v.getRight(), PopupNotificationActivity.this.chatActivityEnterView.getBottom());
                    }
                }
            }
        };
        this.popupContainer = relativeLayout2;
        relativeLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        relativeLayout.addView(this.popupContainer, LayoutHelper.createRelative(-1, PsExtractor.VIDEO_STREAM_MASK, 12, 0, 12, 0, 13));
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        ChatActivityEnterView chatActivityEnterView2 = new ChatActivityEnterView(this, contentView, null, false);
        this.chatActivityEnterView = chatActivityEnterView2;
        chatActivityEnterView2.setId(1000);
        this.popupContainer.addView(this.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
        this.chatActivityEnterView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate() { // from class: org.telegram.ui.PopupNotificationActivity.3
            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void bottomPanelTranslationYChanged(float f) {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$bottomPanelTranslationYChanged(this, f);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ int getContentViewHeight() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getContentViewHeight(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ TLRPC.TL_channels_sendAsPeers getSendAsPeers() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getSendAsPeers(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ boolean hasForwardingMessages() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasForwardingMessages(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ boolean hasScheduledMessages() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasScheduledMessages(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ int measureKeyboardHeight() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$measureKeyboardHeight(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void onTrendingStickersShowed(boolean z) {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onTrendingStickersShowed(this, z);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void openScheduledMessages() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$openScheduledMessages(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void prepareMessageSending() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$prepareMessageSending(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public /* synthetic */ void scrollToSendingMessage() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$scrollToSendingMessage(this);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onMessageSend(CharSequence message, boolean notify, int scheduleDate) {
                if (PopupNotificationActivity.this.currentMessageObject != null) {
                    if (PopupNotificationActivity.this.currentMessageNum >= 0 && PopupNotificationActivity.this.currentMessageNum < PopupNotificationActivity.this.popupMessages.size()) {
                        PopupNotificationActivity.this.popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
                    }
                    MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(0, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, 0, 0, true, 0);
                    PopupNotificationActivity.this.currentMessageObject = null;
                    PopupNotificationActivity.this.getNewMessage();
                }
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onTextChanged(CharSequence text, boolean big) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onTextSelectionChanged(int start, int end) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onTextSpansChanged(CharSequence text) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onStickersExpandedChange() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onSwitchRecordMode(boolean video) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onPreAudioVideoRecord() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onMessageEditEnd(boolean loading) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void needSendTyping() {
                if (PopupNotificationActivity.this.currentMessageObject != null) {
                    MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).sendTyping(PopupNotificationActivity.this.currentMessageObject.getDialogId(), 0, 0, PopupNotificationActivity.this.classGuid);
                }
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onAttachButtonHidden() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onAttachButtonShow() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onWindowSizeChanged(int size) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onStickersTab(boolean opened) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void didPressAttachButton() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void needStartRecordVideo(int state, boolean notify, int scheduleDate) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void needStartRecordAudio(int state) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void needChangeVideoPreviewState(int state, float seekProgress) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void needShowMediaBanHint() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onUpdateSlowModeButton(View button, boolean show, CharSequence time) {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onSendLongClick() {
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
            public void onAudioVideoInterfaceUpdated() {
            }
        });
        FrameLayoutTouch frameLayoutTouch = new FrameLayoutTouch(this);
        this.messageContainer = frameLayoutTouch;
        this.popupContainer.addView(frameLayoutTouch, 0);
        ActionBar actionBar = new ActionBar(this);
        this.actionBar = actionBar;
        actionBar.setOccupyStatusBar(false);
        this.actionBar.setBackButtonImage(R.drawable.ic_close_white);
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        this.popupContainer.addView(this.actionBar);
        ViewGroup.LayoutParams layoutParams = this.actionBar.getLayoutParams();
        layoutParams.width = -1;
        this.actionBar.setLayoutParams(layoutParams);
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem view = menu.addItemWithWidth(2, 0, AndroidUtilities.dp(56.0f));
        TextView textView = new TextView(this);
        this.countText = textView;
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        this.countText.setTextSize(1, 14.0f);
        this.countText.setGravity(17);
        view.addView(this.countText, LayoutHelper.createFrame(56, -1.0f));
        FrameLayout frameLayout = new FrameLayout(this);
        this.avatarContainer = frameLayout;
        frameLayout.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.actionBar.addView(this.avatarContainer);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
        layoutParams2.height = -1;
        layoutParams2.width = -2;
        layoutParams2.rightMargin = AndroidUtilities.dp(48.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(60.0f);
        layoutParams2.gravity = 51;
        this.avatarContainer.setLayoutParams(layoutParams2);
        BackupImageView backupImageView = new BackupImageView(this);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarContainer.addView(this.avatarImageView);
        FrameLayout.LayoutParams layoutParams22 = (FrameLayout.LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams22.width = AndroidUtilities.dp(42.0f);
        layoutParams22.height = AndroidUtilities.dp(42.0f);
        layoutParams22.topMargin = AndroidUtilities.dp(3.0f);
        this.avatarImageView.setLayoutParams(layoutParams22);
        TextView textView2 = new TextView(this);
        this.nameTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.avatarContainer.addView(this.nameTextView);
        FrameLayout.LayoutParams layoutParams23 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams23.width = -2;
        layoutParams23.height = -2;
        layoutParams23.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams23.bottomMargin = AndroidUtilities.dp(22.0f);
        layoutParams23.gravity = 80;
        this.nameTextView.setLayoutParams(layoutParams23);
        TextView textView3 = new TextView(this);
        this.onlineTextView = textView3;
        textView3.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubtitle));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.onlineTextView.setGravity(3);
        this.avatarContainer.addView(this.onlineTextView);
        FrameLayout.LayoutParams layoutParams24 = (FrameLayout.LayoutParams) this.onlineTextView.getLayoutParams();
        layoutParams24.width = -2;
        layoutParams24.height = -2;
        layoutParams24.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams24.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams24.gravity = 80;
        this.onlineTextView.setLayoutParams(layoutParams24);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.PopupNotificationActivity.4
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    PopupNotificationActivity.this.onFinish();
                    PopupNotificationActivity.this.finish();
                } else if (id == 1) {
                    PopupNotificationActivity.this.openCurrentMessage();
                } else if (id == 2) {
                    PopupNotificationActivity.this.switchToNextMessage();
                }
            }
        });
        PowerManager pm = (PowerManager) ApplicationLoader.applicationContext.getSystemService("power");
        PowerManager.WakeLock newWakeLock = pm.newWakeLock(268435462, "screen");
        this.wakeLock = newWakeLock;
        newWakeLock.setReferenceCounted(false);
        handleIntent(getIntent());
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AndroidUtilities.checkDisplaySize(this, newConfig);
        fixLayout();
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 3 || grantResults[0] == 0) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("PermissionNoAudioWithHint", R.string.PermissionNoAudioWithHint));
        builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                PopupNotificationActivity.this.m4313xac2d2ac6(dialogInterface, i);
            }
        });
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.show();
    }

    /* renamed from: lambda$onRequestPermissionsResult$0$org-telegram-ui-PopupNotificationActivity */
    public /* synthetic */ void m4313xac2d2ac6(DialogInterface dialog, int which) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void switchToNextMessage() {
        if (this.popupMessages.size() > 1) {
            if (this.currentMessageNum < this.popupMessages.size() - 1) {
                this.currentMessageNum++;
            } else {
                this.currentMessageNum = 0;
            }
            this.currentMessageObject = this.popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(2);
            this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
        }
    }

    private void switchToPreviousMessage() {
        if (this.popupMessages.size() > 1) {
            int i = this.currentMessageNum;
            if (i > 0) {
                this.currentMessageNum = i - 1;
            } else {
                this.currentMessageNum = this.popupMessages.size() - 1;
            }
            this.currentMessageObject = this.popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(1);
            this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
        }
    }

    public boolean checkTransitionAnimation() {
        if (this.animationInProgress && this.animationStartTime < System.currentTimeMillis() - 400) {
            this.animationInProgress = false;
            Runnable runnable = this.onAnimationEndRunnable;
            if (runnable != null) {
                runnable.run();
                this.onAnimationEndRunnable = null;
            }
        }
        return this.animationInProgress;
    }

    public boolean onTouchEventMy(MotionEvent motionEvent) {
        if (checkTransitionAnimation()) {
            return false;
        }
        if (motionEvent != null && motionEvent.getAction() == 0) {
            this.moveStartX = motionEvent.getX();
        } else if (motionEvent != null && motionEvent.getAction() == 2) {
            float x = motionEvent.getX();
            float f = this.moveStartX;
            int diff = (int) (x - f);
            if (f != -1.0f && !this.startedMoving && Math.abs(diff) > AndroidUtilities.dp(10.0f)) {
                this.startedMoving = true;
                this.moveStartX = x;
                AndroidUtilities.lockOrientation(this);
                diff = 0;
                VelocityTracker velocityTracker = this.velocityTracker;
                if (velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
            }
            if (this.startedMoving) {
                if (this.leftView == null && diff > 0) {
                    diff = 0;
                }
                if (this.rightView == null && diff < 0) {
                    diff = 0;
                }
                VelocityTracker velocityTracker2 = this.velocityTracker;
                if (velocityTracker2 != null) {
                    velocityTracker2.addMovement(motionEvent);
                }
                applyViewsLayoutParams(diff);
            }
        } else if (motionEvent == null || motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent != null && this.startedMoving) {
                int diff2 = (int) (motionEvent.getX() - this.moveStartX);
                int width = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
                float moveDiff = 0.0f;
                int forceMove = 0;
                View otherView = null;
                View otherButtonsView = null;
                VelocityTracker velocityTracker3 = this.velocityTracker;
                if (velocityTracker3 != null) {
                    velocityTracker3.computeCurrentVelocity(1000);
                    if (this.velocityTracker.getXVelocity() >= 3500.0f) {
                        forceMove = 1;
                    } else if (this.velocityTracker.getXVelocity() <= -3500.0f) {
                        forceMove = 2;
                    }
                }
                if ((forceMove == 1 || diff2 > width / 3) && this.leftView != null) {
                    moveDiff = width - this.centerView.getTranslationX();
                    otherView = this.leftView;
                    otherButtonsView = this.leftButtonsView;
                    this.onAnimationEndRunnable = new Runnable() { // from class: org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda6
                        @Override // java.lang.Runnable
                        public final void run() {
                            PopupNotificationActivity.this.m4314xe54846bc();
                        }
                    };
                } else if ((forceMove == 2 || diff2 < (-width) / 3) && this.rightView != null) {
                    moveDiff = (-width) - this.centerView.getTranslationX();
                    otherView = this.rightView;
                    otherButtonsView = this.rightButtonsView;
                    this.onAnimationEndRunnable = new Runnable() { // from class: org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda7
                        @Override // java.lang.Runnable
                        public final void run() {
                            PopupNotificationActivity.this.m4315xca89b57d();
                        }
                    };
                } else if (this.centerView.getTranslationX() != 0.0f) {
                    moveDiff = -this.centerView.getTranslationX();
                    otherView = diff2 > 0 ? this.leftView : this.rightView;
                    otherButtonsView = diff2 > 0 ? this.leftButtonsView : this.rightButtonsView;
                    this.onAnimationEndRunnable = new Runnable() { // from class: org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda8
                        @Override // java.lang.Runnable
                        public final void run() {
                            PopupNotificationActivity.this.m4316xafcb243e();
                        }
                    };
                }
                if (moveDiff != 0.0f) {
                    int time = (int) (Math.abs(moveDiff / width) * 200.0f);
                    ArrayList<Animator> animators = new ArrayList<>();
                    ViewGroup viewGroup = this.centerView;
                    animators.add(ObjectAnimator.ofFloat(viewGroup, "translationX", viewGroup.getTranslationX() + moveDiff));
                    ViewGroup viewGroup2 = this.centerButtonsView;
                    if (viewGroup2 != null) {
                        animators.add(ObjectAnimator.ofFloat(viewGroup2, "translationX", viewGroup2.getTranslationX() + moveDiff));
                    }
                    if (otherView != null) {
                        animators.add(ObjectAnimator.ofFloat(otherView, "translationX", otherView.getTranslationX() + moveDiff));
                    }
                    if (otherButtonsView != null) {
                        animators.add(ObjectAnimator.ofFloat(otherButtonsView, "translationX", otherButtonsView.getTranslationX() + moveDiff));
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(animators);
                    animatorSet.setDuration(time);
                    animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PopupNotificationActivity.5
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (PopupNotificationActivity.this.onAnimationEndRunnable != null) {
                                PopupNotificationActivity.this.onAnimationEndRunnable.run();
                                PopupNotificationActivity.this.onAnimationEndRunnable = null;
                            }
                        }
                    });
                    animatorSet.start();
                    this.animationInProgress = true;
                    this.animationStartTime = System.currentTimeMillis();
                }
            } else {
                applyViewsLayoutParams(0);
            }
            VelocityTracker velocityTracker4 = this.velocityTracker;
            if (velocityTracker4 != null) {
                velocityTracker4.recycle();
                this.velocityTracker = null;
            }
            this.startedMoving = false;
            this.moveStartX = -1.0f;
        }
        return this.startedMoving;
    }

    /* renamed from: lambda$onTouchEventMy$1$org-telegram-ui-PopupNotificationActivity */
    public /* synthetic */ void m4314xe54846bc() {
        this.animationInProgress = false;
        switchToPreviousMessage();
        AndroidUtilities.unlockOrientation(this);
    }

    /* renamed from: lambda$onTouchEventMy$2$org-telegram-ui-PopupNotificationActivity */
    public /* synthetic */ void m4315xca89b57d() {
        this.animationInProgress = false;
        switchToNextMessage();
        AndroidUtilities.unlockOrientation(this);
    }

    /* renamed from: lambda$onTouchEventMy$3$org-telegram-ui-PopupNotificationActivity */
    public /* synthetic */ void m4316xafcb243e() {
        this.animationInProgress = false;
        applyViewsLayoutParams(0);
        AndroidUtilities.unlockOrientation(this);
    }

    public void applyViewsLayoutParams(int xOffset) {
        int widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        ViewGroup viewGroup = this.leftView;
        if (viewGroup != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
            if (layoutParams.width != widht) {
                layoutParams.width = widht;
                this.leftView.setLayoutParams(layoutParams);
            }
            this.leftView.setTranslationX((-widht) + xOffset);
        }
        ViewGroup viewGroup2 = this.leftButtonsView;
        if (viewGroup2 != null) {
            viewGroup2.setTranslationX((-widht) + xOffset);
        }
        ViewGroup viewGroup3 = this.centerView;
        if (viewGroup3 != null) {
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewGroup3.getLayoutParams();
            if (layoutParams2.width != widht) {
                layoutParams2.width = widht;
                this.centerView.setLayoutParams(layoutParams2);
            }
            this.centerView.setTranslationX(xOffset);
        }
        ViewGroup viewGroup4 = this.centerButtonsView;
        if (viewGroup4 != null) {
            viewGroup4.setTranslationX(xOffset);
        }
        ViewGroup viewGroup5 = this.rightView;
        if (viewGroup5 != null) {
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) viewGroup5.getLayoutParams();
            if (layoutParams3.width != widht) {
                layoutParams3.width = widht;
                this.rightView.setLayoutParams(layoutParams3);
            }
            this.rightView.setTranslationX(widht + xOffset);
        }
        ViewGroup viewGroup6 = this.rightButtonsView;
        if (viewGroup6 != null) {
            viewGroup6.setTranslationX(widht + xOffset);
        }
        this.messageContainer.invalidate();
    }

    private LinearLayout getButtonsViewForMessage(int num, boolean applyOffset) {
        TLRPC.ReplyMarkup markup;
        int num2 = num;
        if (this.popupMessages.size() == 1 && (num2 < 0 || num2 >= this.popupMessages.size())) {
            return null;
        }
        if (num2 == -1) {
            num2 = this.popupMessages.size() - 1;
        } else if (num2 == this.popupMessages.size()) {
            num2 = 0;
        }
        LinearLayout view = null;
        final MessageObject messageObject = this.popupMessages.get(num2);
        int buttonsCount = 0;
        TLRPC.ReplyMarkup markup2 = messageObject.messageOwner.reply_markup;
        if (messageObject.getDialogId() == 777000 && markup2 != null) {
            ArrayList<TLRPC.TL_keyboardButtonRow> rows = markup2.rows;
            int size = rows.size();
            for (int a = 0; a < size; a++) {
                TLRPC.TL_keyboardButtonRow row = rows.get(a);
                int size2 = row.buttons.size();
                for (int b = 0; b < size2; b++) {
                    if (row.buttons.get(b) instanceof TLRPC.TL_keyboardButtonCallback) {
                        buttonsCount++;
                    }
                }
            }
        }
        final int account = messageObject.currentAccount;
        if (buttonsCount > 0) {
            ArrayList<TLRPC.TL_keyboardButtonRow> rows2 = markup2.rows;
            int size3 = rows2.size();
            for (int a2 = 0; a2 < size3; a2++) {
                TLRPC.TL_keyboardButtonRow row2 = rows2.get(a2);
                int b2 = 0;
                int size22 = row2.buttons.size();
                while (b2 < size22) {
                    TLRPC.KeyboardButton button = row2.buttons.get(b2);
                    if (button instanceof TLRPC.TL_keyboardButtonCallback) {
                        if (view == null) {
                            view = new LinearLayout(this);
                            view.setOrientation(0);
                            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                            view.setWeightSum(100.0f);
                            view.setTag("b");
                            view.setOnTouchListener(PopupNotificationActivity$$ExternalSyntheticLambda5.INSTANCE);
                        }
                        TextView textView = new TextView(this);
                        markup = markup2;
                        textView.setTextSize(1, 16.0f);
                        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
                        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                        textView.setText(button.text.toUpperCase());
                        textView.setTag(button);
                        textView.setGravity(17);
                        textView.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        view.addView(textView, LayoutHelper.createLinear(-1, -1, 100.0f / buttonsCount));
                        textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda1
                            @Override // android.view.View.OnClickListener
                            public final void onClick(View view2) {
                                PopupNotificationActivity.lambda$getButtonsViewForMessage$5(account, messageObject, view2);
                            }
                        });
                    } else {
                        markup = markup2;
                    }
                    b2++;
                    markup2 = markup;
                }
            }
        }
        if (view != null) {
            int widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
            layoutParams.addRule(12);
            if (applyOffset) {
                int i = this.currentMessageNum;
                if (num2 == i) {
                    view.setTranslationX(0.0f);
                } else if (num2 == i - 1) {
                    view.setTranslationX(-widht);
                } else if (num2 == i + 1) {
                    view.setTranslationX(widht);
                }
            }
            this.popupContainer.addView(view, layoutParams);
        }
        return view;
    }

    public static /* synthetic */ boolean lambda$getButtonsViewForMessage$4(View v, MotionEvent event) {
        return true;
    }

    public static /* synthetic */ void lambda$getButtonsViewForMessage$5(int account, MessageObject messageObject, View v) {
        TLRPC.KeyboardButton button1 = (TLRPC.KeyboardButton) v.getTag();
        if (button1 != null) {
            SendMessagesHelper.getInstance(account).sendNotificationCallback(messageObject.getDialogId(), messageObject.getId(), button1.data);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x0137  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0186  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0379  */
    /* JADX WARN: Removed duplicated region for block: B:80:0x0384  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private android.view.ViewGroup getViewForMessage(int r30, boolean r31) {
        /*
            Method dump skipped, instructions count: 960
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.getViewForMessage(int, boolean):android.view.ViewGroup");
    }

    /* renamed from: lambda$getViewForMessage$6$org-telegram-ui-PopupNotificationActivity */
    public /* synthetic */ void m4310x2b03983a(View v) {
        openCurrentMessage();
    }

    /* renamed from: lambda$getViewForMessage$7$org-telegram-ui-PopupNotificationActivity */
    public /* synthetic */ void m4311x104506fb(View v) {
        openCurrentMessage();
    }

    /* renamed from: lambda$getViewForMessage$8$org-telegram-ui-PopupNotificationActivity */
    public /* synthetic */ void m4312xf58675bc(View v) {
        openCurrentMessage();
    }

    private void reuseButtonsView(ViewGroup view) {
        if (view == null) {
            return;
        }
        this.popupContainer.removeView(view);
    }

    private void reuseView(ViewGroup view) {
        if (view == null) {
            return;
        }
        int tag = ((Integer) view.getTag()).intValue();
        view.setVisibility(8);
        if (tag == 1) {
            this.textViews.add(view);
        } else if (tag == 2) {
            this.imageViews.add(view);
        } else if (tag == 3) {
            this.audioViews.add(view);
        }
    }

    private void prepareLayouts(int move) {
        MessageObject messageObject;
        int widht = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        if (move == 0) {
            reuseView(this.centerView);
            reuseView(this.leftView);
            reuseView(this.rightView);
            reuseButtonsView(this.centerButtonsView);
            reuseButtonsView(this.leftButtonsView);
            reuseButtonsView(this.rightButtonsView);
            int a = this.currentMessageNum - 1;
            while (true) {
                int i = this.currentMessageNum;
                if (a >= i + 2) {
                    break;
                }
                if (a == i - 1) {
                    this.leftView = getViewForMessage(a, true);
                    this.leftButtonsView = getButtonsViewForMessage(a, true);
                } else if (a == i) {
                    this.centerView = getViewForMessage(a, true);
                    this.centerButtonsView = getButtonsViewForMessage(a, true);
                } else if (a == i + 1) {
                    this.rightView = getViewForMessage(a, true);
                    this.rightButtonsView = getButtonsViewForMessage(a, true);
                }
                a++;
            }
        } else if (move == 1) {
            reuseView(this.rightView);
            reuseButtonsView(this.rightButtonsView);
            this.rightView = this.centerView;
            this.centerView = this.leftView;
            this.leftView = getViewForMessage(this.currentMessageNum - 1, true);
            this.rightButtonsView = this.centerButtonsView;
            this.centerButtonsView = this.leftButtonsView;
            this.leftButtonsView = getButtonsViewForMessage(this.currentMessageNum - 1, true);
        } else if (move != 2) {
            if (move == 3) {
                ViewGroup viewGroup = this.rightView;
                if (viewGroup != null) {
                    float offset = viewGroup.getTranslationX();
                    reuseView(this.rightView);
                    ViewGroup viewForMessage = getViewForMessage(this.currentMessageNum + 1, false);
                    this.rightView = viewForMessage;
                    if (viewForMessage != null) {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewForMessage.getLayoutParams();
                        layoutParams.width = widht;
                        this.rightView.setLayoutParams(layoutParams);
                        this.rightView.setTranslationX(offset);
                        this.rightView.invalidate();
                    }
                }
                ViewGroup viewGroup2 = this.rightButtonsView;
                if (viewGroup2 != null) {
                    float offset2 = viewGroup2.getTranslationX();
                    reuseButtonsView(this.rightButtonsView);
                    LinearLayout buttonsViewForMessage = getButtonsViewForMessage(this.currentMessageNum + 1, false);
                    this.rightButtonsView = buttonsViewForMessage;
                    if (buttonsViewForMessage != null) {
                        buttonsViewForMessage.setTranslationX(offset2);
                    }
                }
            } else if (move == 4) {
                ViewGroup viewGroup3 = this.leftView;
                if (viewGroup3 != null) {
                    float offset3 = viewGroup3.getTranslationX();
                    reuseView(this.leftView);
                    ViewGroup viewForMessage2 = getViewForMessage(0, false);
                    this.leftView = viewForMessage2;
                    if (viewForMessage2 != null) {
                        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewForMessage2.getLayoutParams();
                        layoutParams2.width = widht;
                        this.leftView.setLayoutParams(layoutParams2);
                        this.leftView.setTranslationX(offset3);
                        this.leftView.invalidate();
                    }
                }
                ViewGroup viewGroup4 = this.leftButtonsView;
                if (viewGroup4 != null) {
                    float offset4 = viewGroup4.getTranslationX();
                    reuseButtonsView(this.leftButtonsView);
                    LinearLayout buttonsViewForMessage2 = getButtonsViewForMessage(0, false);
                    this.leftButtonsView = buttonsViewForMessage2;
                    if (buttonsViewForMessage2 != null) {
                        buttonsViewForMessage2.setTranslationX(offset4);
                    }
                }
            }
        } else {
            reuseView(this.leftView);
            reuseButtonsView(this.leftButtonsView);
            this.leftView = this.centerView;
            this.centerView = this.rightView;
            this.rightView = getViewForMessage(this.currentMessageNum + 1, true);
            this.leftButtonsView = this.centerButtonsView;
            this.centerButtonsView = this.rightButtonsView;
            this.rightButtonsView = getButtonsViewForMessage(this.currentMessageNum + 1, true);
        }
        for (int a2 = 0; a2 < 3; a2++) {
            int num = (this.currentMessageNum - 1) + a2;
            if (this.popupMessages.size() == 1 && (num < 0 || num >= this.popupMessages.size())) {
                messageObject = null;
            } else {
                if (num == -1) {
                    num = this.popupMessages.size() - 1;
                } else if (num == this.popupMessages.size()) {
                    num = 0;
                }
                messageObject = this.popupMessages.get(num);
            }
            this.setMessageObjects[a2] = messageObject;
        }
    }

    private void fixLayout() {
        FrameLayout frameLayout = this.avatarContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.PopupNotificationActivity.6
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    if (PopupNotificationActivity.this.avatarContainer != null) {
                        PopupNotificationActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    int padding = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0f)) / 2;
                    PopupNotificationActivity.this.avatarContainer.setPadding(PopupNotificationActivity.this.avatarContainer.getPaddingLeft(), padding, PopupNotificationActivity.this.avatarContainer.getPaddingRight(), padding);
                    return true;
                }
            });
        }
        ViewGroup viewGroup = this.messageContainer;
        if (viewGroup != null) {
            viewGroup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.PopupNotificationActivity.7
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    PopupNotificationActivity.this.messageContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (!PopupNotificationActivity.this.checkTransitionAnimation() && !PopupNotificationActivity.this.startedMoving) {
                        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) PopupNotificationActivity.this.messageContainer.getLayoutParams();
                        layoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
                        layoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
                        layoutParams.width = -1;
                        layoutParams.height = -1;
                        PopupNotificationActivity.this.messageContainer.setLayoutParams(layoutParams);
                        PopupNotificationActivity.this.applyViewsLayoutParams(0);
                        return true;
                    }
                    return true;
                }
            });
        }
    }

    private void handleIntent(Intent intent) {
        this.isReply = intent != null && intent.getBooleanExtra("force", false);
        this.popupMessages.clear();
        if (this.isReply) {
            int account = UserConfig.selectedAccount;
            if (intent != null) {
                account = intent.getIntExtra("currentAccount", account);
            }
            if (!UserConfig.isValidAccount(account)) {
                return;
            }
            this.popupMessages.addAll(NotificationsController.getInstance(account).popupReplyMessages);
        } else {
            for (int a = 0; a < 4; a++) {
                if (UserConfig.getInstance(a).isClientActivated()) {
                    this.popupMessages.addAll(NotificationsController.getInstance(a).popupMessages);
                }
            }
        }
        KeyguardManager km = (KeyguardManager) getSystemService("keyguard");
        if (km.inKeyguardRestrictedInputMode() || !ApplicationLoader.isScreenOn) {
            getWindow().addFlags(2623490);
        } else {
            getWindow().addFlags(2623488);
            getWindow().clearFlags(2);
        }
        if (this.currentMessageObject == null) {
            this.currentMessageNum = 0;
        }
        getNewMessage();
    }

    public void getNewMessage() {
        if (this.popupMessages.isEmpty()) {
            onFinish();
            finish();
            return;
        }
        boolean found = false;
        if ((this.currentMessageNum != 0 || this.chatActivityEnterView.hasText() || this.startedMoving) && this.currentMessageObject != null) {
            int a = 0;
            int size = this.popupMessages.size();
            while (true) {
                if (a >= size) {
                    break;
                }
                MessageObject messageObject = this.popupMessages.get(a);
                if (messageObject.currentAccount != this.currentMessageObject.currentAccount || messageObject.getDialogId() != this.currentMessageObject.getDialogId() || messageObject.getId() != this.currentMessageObject.getId()) {
                    a++;
                } else {
                    this.currentMessageNum = a;
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            this.currentMessageNum = 0;
            this.currentMessageObject = this.popupMessages.get(0);
            updateInterfaceForCurrentMessage(0);
        } else if (this.startedMoving) {
            if (this.currentMessageNum == this.popupMessages.size() - 1) {
                prepareLayouts(3);
            } else if (this.currentMessageNum == 1) {
                prepareLayouts(4);
            }
        }
        this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
    }

    public void openCurrentMessage() {
        if (this.currentMessageObject == null) {
            return;
        }
        Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        long dialogId = this.currentMessageObject.getDialogId();
        if (DialogObject.isEncryptedDialog(dialogId)) {
            intent.putExtra("encId", DialogObject.getEncryptedChatId(dialogId));
        } else if (DialogObject.isUserDialog(dialogId)) {
            intent.putExtra("userId", dialogId);
        } else if (DialogObject.isChatDialog(dialogId)) {
            intent.putExtra("chatId", -dialogId);
        }
        intent.putExtra("currentAccount", this.currentMessageObject.currentAccount);
        intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        intent.setFlags(32768);
        startActivity(intent);
        onFinish();
        finish();
    }

    private void updateInterfaceForCurrentMessage(int move) {
        if (this.actionBar == null) {
            return;
        }
        if (this.lastResumedAccount != this.currentMessageObject.currentAccount) {
            int i = this.lastResumedAccount;
            if (i >= 0) {
                ConnectionsManager.getInstance(i).setAppPaused(true, false);
            }
            int i2 = this.currentMessageObject.currentAccount;
            this.lastResumedAccount = i2;
            ConnectionsManager.getInstance(i2).setAppPaused(false, false);
        }
        this.currentChat = null;
        this.currentUser = null;
        long dialogId = this.currentMessageObject.getDialogId();
        this.chatActivityEnterView.setDialogId(dialogId, this.currentMessageObject.currentAccount);
        if (DialogObject.isEncryptedDialog(dialogId)) {
            TLRPC.EncryptedChat encryptedChat = MessagesController.getInstance(this.currentMessageObject.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId)));
            this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(encryptedChat.user_id));
        } else if (DialogObject.isUserDialog(dialogId)) {
            this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(dialogId));
        } else if (DialogObject.isChatDialog(dialogId)) {
            this.currentChat = MessagesController.getInstance(this.currentMessageObject.currentAccount).getChat(Long.valueOf(-dialogId));
            if (this.currentMessageObject.isFromUser()) {
                this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(this.currentMessageObject.messageOwner.from_id.user_id));
            }
        }
        TLRPC.Chat chat = this.currentChat;
        if (chat != null) {
            this.nameTextView.setText(chat.title);
            TLRPC.User user = this.currentUser;
            if (user == null) {
                this.onlineTextView.setText((CharSequence) null);
            } else {
                this.onlineTextView.setText(UserObject.getUserName(user));
            }
            this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            this.nameTextView.setCompoundDrawablePadding(0);
        } else {
            TLRPC.User user2 = this.currentUser;
            if (user2 != null) {
                this.nameTextView.setText(UserObject.getUserName(user2));
                if (DialogObject.isEncryptedDialog(dialogId)) {
                    this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_white, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                } else {
                    this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(0);
                }
            }
        }
        prepareLayouts(move);
        updateSubtitle();
        checkAndUpdateAvatar();
        applyViewsLayoutParams(0);
    }

    private void updateSubtitle() {
        TLRPC.User user;
        if (this.actionBar == null || this.currentMessageObject == null || this.currentChat != null || (user = this.currentUser) == null) {
            return;
        }
        if (user.id / 1000 != 777 && this.currentUser.id / 1000 != 333 && ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.get(Long.valueOf(this.currentUser.id)) == null && (ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.size() != 0 || !ContactsController.getInstance(this.currentMessageObject.currentAccount).isLoadingContacts())) {
            if (this.currentUser.phone != null && this.currentUser.phone.length() != 0) {
                TextView textView = this.nameTextView;
                PhoneFormat phoneFormat = PhoneFormat.getInstance();
                textView.setText(phoneFormat.format("+" + this.currentUser.phone));
            } else {
                this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            }
        } else {
            this.nameTextView.setText(UserObject.getUserName(this.currentUser));
        }
        TLRPC.User user2 = this.currentUser;
        if (user2 != null && user2.id == 777000) {
            this.onlineTextView.setText(LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications));
            return;
        }
        CharSequence printString = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingString(this.currentMessageObject.getDialogId(), 0, false);
        if (printString == null || printString.length() == 0) {
            this.lastPrintString = null;
            setTypingAnimation(false);
            TLRPC.User user3 = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(this.currentUser.id));
            if (user3 != null) {
                this.currentUser = user3;
            }
            this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentMessageObject.currentAccount, this.currentUser));
            return;
        }
        this.lastPrintString = printString;
        this.onlineTextView.setText(printString);
        setTypingAnimation(true);
    }

    private void checkAndUpdateAvatar() {
        TLRPC.User user;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        if (this.currentChat != null) {
            TLRPC.Chat chat = MessagesController.getInstance(messageObject.currentAccount).getChat(Long.valueOf(this.currentChat.id));
            if (chat == null) {
                return;
            }
            this.currentChat = chat;
            if (this.avatarImageView != null) {
                AvatarDrawable avatarDrawable = new AvatarDrawable(this.currentChat);
                this.avatarImageView.setForUserOrChat(chat, avatarDrawable);
            }
        } else if (this.currentUser == null || (user = MessagesController.getInstance(messageObject.currentAccount).getUser(Long.valueOf(this.currentUser.id))) == null) {
        } else {
            this.currentUser = user;
            if (this.avatarImageView != null) {
                AvatarDrawable avatarDrawable2 = new AvatarDrawable(this.currentUser);
                this.avatarImageView.setForUserOrChat(user, avatarDrawable2);
            }
        }
    }

    private void setTypingAnimation(boolean start) {
        if (this.actionBar == null) {
            return;
        }
        if (start) {
            try {
                Integer type = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingStringType(this.currentMessageObject.getDialogId(), 0);
                this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.statusDrawables[type.intValue()], (Drawable) null, (Drawable) null, (Drawable) null);
                this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                for (int a = 0; a < this.statusDrawables.length; a++) {
                    if (a == type.intValue()) {
                        this.statusDrawables[a].start();
                    } else {
                        this.statusDrawables[a].stop();
                    }
                }
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
        this.onlineTextView.setCompoundDrawablePadding(0);
        int a2 = 0;
        while (true) {
            StatusDrawable[] statusDrawableArr = this.statusDrawables;
            if (a2 < statusDrawableArr.length) {
                statusDrawableArr[a2].stop();
                a2++;
            } else {
                return;
            }
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.chatActivityEnterView.isPopupShowing()) {
            this.chatActivityEnterView.hidePopup(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, true);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.setFieldFocused(true);
        }
        fixLayout();
        checkAndUpdateAvatar();
        this.wakeLock.acquire(7000L);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.hidePopup(false);
            this.chatActivityEnterView.setFieldFocused(false);
        }
        int i = this.lastResumedAccount;
        if (i >= 0) {
            ConnectionsManager.getInstance(i).setAppPaused(true, false);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        TextView textView;
        PopupAudioView cell;
        MessageObject messageObject;
        PopupAudioView cell2;
        MessageObject messageObject2;
        MessageObject messageObject3;
        if (id == NotificationCenter.appDidLogout) {
            if (account == this.lastResumedAccount) {
                onFinish();
                finish();
            }
        } else if (id == NotificationCenter.pushMessagesUpdated) {
            if (!this.isReply) {
                this.popupMessages.clear();
                for (int a = 0; a < 4; a++) {
                    if (UserConfig.getInstance(a).isClientActivated()) {
                        this.popupMessages.addAll(NotificationsController.getInstance(a).popupMessages);
                    }
                }
                getNewMessage();
                if (!this.popupMessages.isEmpty()) {
                    for (int a2 = 0; a2 < 3; a2++) {
                        int num = (this.currentMessageNum - 1) + a2;
                        if (this.popupMessages.size() == 1 && (num < 0 || num >= this.popupMessages.size())) {
                            messageObject3 = null;
                        } else {
                            if (num == -1) {
                                num = this.popupMessages.size() - 1;
                            } else if (num == this.popupMessages.size()) {
                                num = 0;
                            }
                            messageObject3 = this.popupMessages.get(num);
                        }
                        if (this.setMessageObjects[a2] != messageObject3) {
                            updateInterfaceForCurrentMessage(0);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            if (this.currentMessageObject == null || account != this.lastResumedAccount) {
                return;
            }
            int updateMask = ((Integer) args[0]).intValue();
            if ((MessagesController.UPDATE_MASK_NAME & updateMask) != 0 || (MessagesController.UPDATE_MASK_STATUS & updateMask) != 0 || (MessagesController.UPDATE_MASK_CHAT_NAME & updateMask) != 0 || (MessagesController.UPDATE_MASK_CHAT_MEMBERS & updateMask) != 0) {
                updateSubtitle();
            }
            if ((MessagesController.UPDATE_MASK_AVATAR & updateMask) != 0 || (MessagesController.UPDATE_MASK_CHAT_AVATAR & updateMask) != 0) {
                checkAndUpdateAvatar();
            }
            if ((MessagesController.UPDATE_MASK_USER_PRINT & updateMask) != 0) {
                CharSequence printString = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingString(this.currentMessageObject.getDialogId(), 0, false);
                CharSequence charSequence = this.lastPrintString;
                if ((charSequence != null && printString == null) || ((charSequence == null && printString != null) || (charSequence != null && !charSequence.equals(printString)))) {
                    updateSubtitle();
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidReset) {
            Integer mid = (Integer) args[0];
            ViewGroup viewGroup = this.messageContainer;
            if (viewGroup != null) {
                int count = viewGroup.getChildCount();
                for (int a3 = 0; a3 < count; a3++) {
                    View view = this.messageContainer.getChildAt(a3);
                    if (((Integer) view.getTag()).intValue() == 3 && (messageObject2 = (cell2 = (PopupAudioView) view.findViewWithTag(300)).getMessageObject()) != null && messageObject2.currentAccount == account && messageObject2.getId() == mid.intValue()) {
                        cell2.updateButtonState();
                        return;
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer mid2 = (Integer) args[0];
            ViewGroup viewGroup2 = this.messageContainer;
            if (viewGroup2 != null) {
                int count2 = viewGroup2.getChildCount();
                for (int a4 = 0; a4 < count2; a4++) {
                    View view2 = this.messageContainer.getChildAt(a4);
                    if (((Integer) view2.getTag()).intValue() == 3 && (messageObject = (cell = (PopupAudioView) view2.findViewWithTag(300)).getMessageObject()) != null && messageObject.currentAccount == account && messageObject.getId() == mid2.intValue()) {
                        cell.updateProgress();
                        return;
                    }
                }
            }
        } else if (id == NotificationCenter.emojiLoaded) {
            ViewGroup viewGroup3 = this.messageContainer;
            if (viewGroup3 != null) {
                int count3 = viewGroup3.getChildCount();
                for (int a5 = 0; a5 < count3; a5++) {
                    View view3 = this.messageContainer.getChildAt(a5);
                    if (((Integer) view3.getTag()).intValue() == 1 && (textView = (TextView) view3.findViewWithTag(301)) != null) {
                        textView.invalidate();
                    }
                }
            }
        } else if (id == NotificationCenter.contactsDidLoad && account == this.lastResumedAccount) {
            updateSubtitle();
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        onFinish();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, false);
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        BackupImageView backupImageView = this.avatarImageView;
        if (backupImageView != null) {
            backupImageView.setImageDrawable(null);
        }
    }

    protected void onFinish() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        if (this.isReply) {
            this.popupMessages.clear();
        }
        for (int a = 0; a < 4; a++) {
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.contactsDidLoad);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
    }
}
