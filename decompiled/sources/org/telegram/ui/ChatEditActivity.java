package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.LocationActivity;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes4.dex */
public class ChatEditActivity extends BaseFragment implements ImageUpdater.ImageUpdaterDelegate, NotificationCenter.NotificationCenterDelegate {
    private static final int done_button = 1;
    private TextCell adminCell;
    private TLRPC.FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private LinearLayout avatarContainer;
    private BackupImageView avatarImage;
    private View avatarOverlay;
    private RadialProgressView avatarProgressView;
    private TextCell blockCell;
    RLottieDrawable cameraDrawable;
    private long chatId;
    private boolean createAfterUpload;
    private TLRPC.Chat currentChat;
    private TextSettingsCell deleteCell;
    private FrameLayout deleteContainer;
    private ShadowSectionCell deleteInfoCell;
    private EditTextBoldCursor descriptionTextView;
    private View doneButton;
    private boolean donePressed;
    private TextDetailCell historyCell;
    private boolean historyHidden;
    private TLRPC.ChatFull info;
    private LinearLayout infoContainer;
    private ShadowSectionCell infoSectionCell;
    private TextCell inviteLinksCell;
    private boolean isChannel;
    private TextDetailCell linkedCell;
    private TextDetailCell locationCell;
    private TextCell logCell;
    private TextCell memberRequestsCell;
    private TextCell membersCell;
    private EditTextEmoji nameTextView;
    private AlertDialog progressDialog;
    private TextCell reactionsCell;
    private TextCell setAvatarCell;
    private LinearLayout settingsContainer;
    private ShadowSectionCell settingsSectionCell;
    private ShadowSectionCell settingsTopSectionCell;
    private TextCheckCell signCell;
    private boolean signMessages;
    private TextCell stickersCell;
    private FrameLayout stickersContainer;
    private TextInfoPrivacyCell stickersInfoCell;
    private TextDetailCell typeCell;
    private LinearLayout typeEditContainer;
    private UndoView undoView;
    private List<String> availableReactions = Collections.emptyList();
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.ChatEditActivity.1
        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            if (fileLocation == null) {
                return null;
            }
            TLRPC.FileLocation photoBig = null;
            TLRPC.Chat chat = ChatEditActivity.this.getMessagesController().getChat(Long.valueOf(ChatEditActivity.this.chatId));
            if (chat != null && chat.photo != null && chat.photo.photo_big != null) {
                photoBig = chat.photo.photo_big;
            }
            if (photoBig == null || photoBig.local_id != fileLocation.local_id || photoBig.volume_id != fileLocation.volume_id || photoBig.dc_id != fileLocation.dc_id) {
                return null;
            }
            int[] coords = new int[2];
            ChatEditActivity.this.avatarImage.getLocationInWindow(coords);
            PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
            int i = 0;
            object.viewX = coords[0];
            int i2 = coords[1];
            if (Build.VERSION.SDK_INT < 21) {
                i = AndroidUtilities.statusBarHeight;
            }
            object.viewY = i2 - i;
            object.parentView = ChatEditActivity.this.avatarImage;
            object.imageReceiver = ChatEditActivity.this.avatarImage.getImageReceiver();
            object.dialogId = -ChatEditActivity.this.chatId;
            object.thumb = object.imageReceiver.getBitmapSafe();
            object.size = -1L;
            object.radius = ChatEditActivity.this.avatarImage.getImageReceiver().getRoundRadius();
            object.scale = ChatEditActivity.this.avatarContainer.getScaleX();
            object.canEdit = true;
            return object;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willHidePhotoViewer() {
            ChatEditActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void openPhotoForEdit(String file, String thumb, boolean isVideo) {
            ChatEditActivity.this.imageUpdater.openPhotoForEdit(file, thumb, 0, isVideo);
        }
    };
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageUpdater imageUpdater = new ImageUpdater(true);

    public ChatEditActivity(Bundle args) {
        super(args);
        this.chatId = args.getLong(ChatReactionsEditActivity.KEY_CHAT_ID, 0L);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        this.currentChat = chat;
        boolean z = true;
        if (chat == null) {
            TLRPC.Chat chatSync = MessagesStorage.getInstance(this.currentAccount).getChatSync(this.chatId);
            this.currentChat = chatSync;
            if (chatSync == null) {
                return false;
            }
            getMessagesController().putChat(this.currentChat, true);
            if (this.info == null) {
                TLRPC.ChatFull loadChatInfo = MessagesStorage.getInstance(this.currentAccount).loadChatInfo(this.chatId, ChatObject.isChannel(this.currentChat), new CountDownLatch(1), false, false);
                this.info = loadChatInfo;
                if (loadChatInfo == null) {
                    return false;
                }
            }
        }
        this.avatarDrawable.setInfo(5L, this.currentChat.title, null);
        if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) {
            z = false;
        }
        this.isChannel = z;
        this.imageUpdater.parentFragment = this;
        this.imageUpdater.setDelegate(this);
        this.signMessages = this.currentChat.signatures;
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatAvailableReactionsUpdated);
        if (this.info != null) {
            loadLinksCount();
        }
        return super.onFragmentCreate();
    }

    private void loadLinksCount() {
        TLRPC.TL_messages_getExportedChatInvites req = new TLRPC.TL_messages_getExportedChatInvites();
        req.peer = getMessagesController().getInputPeer(-this.chatId);
        req.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
        req.limit = 0;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda21
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatEditActivity.this.m2057lambda$loadLinksCount$1$orgtelegramuiChatEditActivity(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadLinksCount$1$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2057lambda$loadLinksCount$1$orgtelegramuiChatEditActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                ChatEditActivity.this.m2056lambda$loadLinksCount$0$orgtelegramuiChatEditActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$loadLinksCount$0$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2056lambda$loadLinksCount$0$orgtelegramuiChatEditActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            TLRPC.TL_messages_exportedChatInvites invites = (TLRPC.TL_messages_exportedChatInvites) response;
            this.info.invitesCount = invites.count;
            getMessagesStorage().saveChatLinksCount(this.chatId, this.info.invitesCount);
            updateFields(false);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.clear();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatAvailableReactionsUpdated);
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
            this.nameTextView.getEditText().requestFocus();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        updateFields(true);
        this.imageUpdater.onResume();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        this.imageUpdater.onPause();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void dismissCurrentDialog() {
        if (this.imageUpdater.dismissCurrentDialog(this.visibleDialog)) {
            return;
        }
        super.dismissCurrentDialog();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean dismissDialogOnPause(Dialog dialog) {
        return this.imageUpdater.dismissDialogOnPause(dialog) && super.dismissDialogOnPause(dialog);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        this.imageUpdater.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null && editTextEmoji.isPopupShowing()) {
            this.nameTextView.hidePopup(true);
            return false;
        }
        return checkDiscard();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        TLRPC.ChatFull chatFull;
        TLRPC.ChatFull chatFull2;
        TLRPC.ChatFull chatFull3;
        TLRPC.ChatFull chatFull4;
        TLRPC.ChatFull chatFull5;
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChatEditActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (ChatEditActivity.this.checkDiscard()) {
                        ChatEditActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    ChatEditActivity.this.processDone();
                }
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.ChatEditActivity.3
            private boolean ignoreLayout;

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                int heightSize2 = heightSize - getPaddingTop();
                measureChildWithMargins(ChatEditActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int keyboardSize = measureKeyboardHeight();
                if (keyboardSize > AndroidUtilities.dp(20.0f)) {
                    this.ignoreLayout = true;
                    ChatEditActivity.this.nameTextView.hideEmojiView();
                    this.ignoreLayout = false;
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (child != null && child.getVisibility() != 8 && child != ChatEditActivity.this.actionBar) {
                        if (ChatEditActivity.this.nameTextView != null && ChatEditActivity.this.nameTextView.isPopupView(child)) {
                            if (AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) {
                                if (AndroidUtilities.isTablet()) {
                                    child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), C.BUFFER_FLAG_ENCRYPTED));
                                } else {
                                    child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((heightSize2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), C.BUFFER_FLAG_ENCRYPTED));
                                }
                            } else {
                                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, C.BUFFER_FLAG_ENCRYPTED));
                            }
                        } else {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        }
                    }
                }
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                int count = getChildCount();
                int keyboardSize = measureKeyboardHeight();
                int paddingBottom = (keyboardSize > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : ChatEditActivity.this.nameTextView.getEmojiPadding();
                setBottomClip(paddingBottom);
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
                                childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop();
                                break;
                            case UndoView.ACTION_EMAIL_COPIED /* 80 */:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (ChatEditActivity.this.nameTextView != null && ChatEditActivity.this.nameTextView.isPopupView(child)) {
                            if (AndroidUtilities.isTablet()) {
                                childTop = getMeasuredHeight() - child.getMeasuredHeight();
                            } else {
                                childTop = (getMeasuredHeight() + keyboardSize) - child.getMeasuredHeight();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        sizeNotifierFrameLayout.setOnTouchListener(ChatEditActivity$$ExternalSyntheticLambda14.INSTANCE);
        this.fragmentView = sizeNotifierFrameLayout;
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        sizeNotifierFrameLayout.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
        LinearLayout linearLayout1 = new LinearLayout(context);
        scrollView.addView(linearLayout1, new FrameLayout.LayoutParams(-1, -2));
        linearLayout1.setOrientation(1);
        this.actionBar.setTitle(LocaleController.getString("ChannelEdit", R.string.ChannelEdit));
        LinearLayout linearLayout = new LinearLayout(context);
        this.avatarContainer = linearLayout;
        linearLayout.setOrientation(1);
        this.avatarContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout1.addView(this.avatarContainer, LayoutHelper.createLinear(-1, -2));
        FrameLayout frameLayout = new FrameLayout(context);
        this.avatarContainer.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
        BackupImageView backupImageView = new BackupImageView(context) { // from class: org.telegram.ui.ChatEditActivity.4
            @Override // android.view.View
            public void invalidate() {
                if (ChatEditActivity.this.avatarOverlay != null) {
                    ChatEditActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate();
            }

            @Override // android.view.View
            public void invalidate(int l, int t, int r, int b) {
                if (ChatEditActivity.this.avatarOverlay != null) {
                    ChatEditActivity.this.avatarOverlay.invalidate();
                }
                super.invalidate(l, t, r, b);
            }
        };
        this.avatarImage = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
        if (ChatObject.canChangeChatInfo(this.currentChat)) {
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 8.0f));
            final Paint paint = new Paint(1);
            paint.setColor(1426063360);
            View view = new View(context) { // from class: org.telegram.ui.ChatEditActivity.5
                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    if (ChatEditActivity.this.avatarImage != null && ChatEditActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        paint.setAlpha((int) (ChatEditActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                        canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredWidth() / 2.0f, paint);
                    }
                }
            };
            this.avatarOverlay = view;
            frameLayout.addView(view, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 8.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context);
            this.avatarProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            this.avatarProgressView.setNoProgress(false);
            frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 8.0f));
            showAvatarProgress(false, false);
            this.avatarContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.m2047lambda$createView$3$orgtelegramuiChatEditActivity(view2);
                }
            });
        } else {
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
        }
        EditTextEmoji editTextEmoji2 = new EditTextEmoji(context, sizeNotifierFrameLayout, this, 0);
        this.nameTextView = editTextEmoji2;
        if (this.isChannel) {
            editTextEmoji2.setHint(LocaleController.getString("EnterChannelName", R.string.EnterChannelName));
        } else {
            editTextEmoji2.setHint(LocaleController.getString("GroupName", R.string.GroupName));
        }
        this.nameTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        EditTextEmoji editTextEmoji3 = this.nameTextView;
        editTextEmoji3.setFocusable(editTextEmoji3.isEnabled());
        this.nameTextView.getEditText().addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ChatEditActivity.6
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
                ChatEditActivity.this.avatarDrawable.setInfo(5L, ChatEditActivity.this.nameTextView.getText().toString(), null);
                if (ChatEditActivity.this.avatarImage != null) {
                    ChatEditActivity.this.avatarImage.invalidate();
                }
            }
        });
        InputFilter[] inputFilters = {new InputFilter.LengthFilter(128)};
        this.nameTextView.setFilters(inputFilters);
        frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.settingsContainer = linearLayout2;
        linearLayout2.setOrientation(1);
        this.settingsContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout1.addView(this.settingsContainer, LayoutHelper.createLinear(-1, -2));
        if (ChatObject.canChangeChatInfo(this.currentChat)) {
            TextCell textCell = new TextCell(context) { // from class: org.telegram.ui.ChatEditActivity.7
                @Override // org.telegram.ui.Cells.TextCell, android.view.View
                protected void onDraw(Canvas canvas) {
                    canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(20.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
                }
            };
            this.setAvatarCell = textCell;
            textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.setAvatarCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
            this.setAvatarCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda9
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.m2050lambda$createView$6$orgtelegramuiChatEditActivity(view2);
                }
            });
            this.settingsContainer.addView(this.setAvatarCell, LayoutHelper.createLinear(-1, -2));
        }
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        this.descriptionTextView = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 16.0f);
        this.descriptionTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
        this.descriptionTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
        this.descriptionTextView.setBackgroundDrawable(null);
        this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.descriptionTextView.setInputType(180225);
        this.descriptionTextView.setImeOptions(6);
        this.descriptionTextView.setEnabled(ChatObject.canChangeChatInfo(this.currentChat));
        EditTextBoldCursor editTextBoldCursor2 = this.descriptionTextView;
        editTextBoldCursor2.setFocusable(editTextBoldCursor2.isEnabled());
        InputFilter[] inputFilters2 = {new InputFilter.LengthFilter(255)};
        this.descriptionTextView.setFilters(inputFilters2);
        this.descriptionTextView.setHint(LocaleController.getString("DescriptionOptionalPlaceholder", R.string.DescriptionOptionalPlaceholder));
        this.descriptionTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
        this.descriptionTextView.setCursorWidth(1.5f);
        if (this.descriptionTextView.isEnabled()) {
            this.settingsContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 23.0f, 15.0f, 23.0f, 9.0f));
        } else {
            this.settingsContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 23.0f, 12.0f, 23.0f, 6.0f));
        }
        this.descriptionTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda15
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return ChatEditActivity.this.m2051lambda$createView$7$orgtelegramuiChatEditActivity(textView, i, keyEvent);
            }
        });
        this.descriptionTextView.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ChatEditActivity.8
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
            }
        });
        ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context);
        this.settingsTopSectionCell = shadowSectionCell;
        linearLayout1.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout3 = new LinearLayout(context);
        this.typeEditContainer = linearLayout3;
        linearLayout3.setOrientation(1);
        this.typeEditContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout1.addView(this.typeEditContainer, LayoutHelper.createLinear(-1, -2));
        if (this.currentChat.megagroup && ((chatFull5 = this.info) == null || chatFull5.can_set_location)) {
            TextDetailCell textDetailCell = new TextDetailCell(context);
            this.locationCell = textDetailCell;
            textDetailCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.locationCell, LayoutHelper.createLinear(-1, -2));
            this.locationCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda10
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.m2053lambda$createView$9$orgtelegramuiChatEditActivity(view2);
                }
            });
        }
        if (this.currentChat.creator && ((chatFull4 = this.info) == null || chatFull4.can_set_username)) {
            TextDetailCell textDetailCell2 = new TextDetailCell(context);
            this.typeCell = textDetailCell2;
            textDetailCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.typeCell, LayoutHelper.createLinear(-1, -2));
            this.typeCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda26
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.m2031lambda$createView$10$orgtelegramuiChatEditActivity(view2);
                }
            });
        }
        if (ChatObject.isChannel(this.currentChat) && ((this.isChannel && ChatObject.canUserDoAdminAction(this.currentChat, 1)) || (!this.isChannel && ChatObject.canUserDoAdminAction(this.currentChat, 0)))) {
            TextDetailCell textDetailCell3 = new TextDetailCell(context);
            this.linkedCell = textDetailCell3;
            textDetailCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.linkedCell, LayoutHelper.createLinear(-1, -2));
            this.linkedCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda27
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.m2032lambda$createView$11$orgtelegramuiChatEditActivity(view2);
                }
            });
        }
        if (!this.isChannel && ChatObject.canBlockUsers(this.currentChat) && (ChatObject.isChannel(this.currentChat) || this.currentChat.creator)) {
            TextDetailCell textDetailCell4 = new TextDetailCell(context);
            this.historyCell = textDetailCell4;
            textDetailCell4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.typeEditContainer.addView(this.historyCell, LayoutHelper.createLinear(-1, -2));
            this.historyCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda12
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.m2034lambda$createView$13$orgtelegramuiChatEditActivity(context, view2);
                }
            });
        }
        if (this.isChannel) {
            TextCheckCell textCheckCell = new TextCheckCell(context);
            this.signCell = textCheckCell;
            textCheckCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.signCell.setTextAndValueAndCheck(LocaleController.getString("ChannelSignMessages", R.string.ChannelSignMessages), LocaleController.getString("ChannelSignMessagesInfo", R.string.ChannelSignMessagesInfo), this.signMessages, true, false);
            this.typeEditContainer.addView(this.signCell, LayoutHelper.createFrame(-1, -2.0f));
            this.signCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda28
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.m2035lambda$createView$14$orgtelegramuiChatEditActivity(view2);
                }
            });
        }
        ActionBarMenu menu = this.actionBar.createMenu();
        if (ChatObject.canChangeChatInfo(this.currentChat) || this.signCell != null || this.historyCell != null) {
            ActionBarMenuItem addItemWithWidth = menu.addItemWithWidth(1, R.drawable.ic_ab_done, AndroidUtilities.dp(56.0f));
            this.doneButton = addItemWithWidth;
            addItemWithWidth.setContentDescription(LocaleController.getString("Done", R.string.Done));
        }
        if (this.locationCell != null || this.signCell != null || this.historyCell != null || this.typeCell != null || this.linkedCell != null) {
            ShadowSectionCell shadowSectionCell2 = new ShadowSectionCell(context);
            this.settingsSectionCell = shadowSectionCell2;
            linearLayout1.addView(shadowSectionCell2, LayoutHelper.createLinear(-1, -2));
        }
        LinearLayout linearLayout4 = new LinearLayout(context);
        this.infoContainer = linearLayout4;
        linearLayout4.setOrientation(1);
        this.infoContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        linearLayout1.addView(this.infoContainer, LayoutHelper.createLinear(-1, -2));
        TextCell textCell2 = new TextCell(context);
        this.blockCell = textCell2;
        textCell2.setBackground(Theme.getSelectorDrawable(false));
        this.blockCell.setVisibility((ChatObject.isChannel(this.currentChat) || this.currentChat.creator || (ChatObject.hasAdminRights(this.currentChat) && ChatObject.canChangeChatInfo(this.currentChat))) ? 0 : 8);
        this.blockCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda29
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatEditActivity.this.m2036lambda$createView$15$orgtelegramuiChatEditActivity(view2);
            }
        });
        TextCell textCell3 = new TextCell(context);
        this.inviteLinksCell = textCell3;
        textCell3.setBackground(Theme.getSelectorDrawable(false));
        this.inviteLinksCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda30
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatEditActivity.this.m2037lambda$createView$16$orgtelegramuiChatEditActivity(view2);
            }
        });
        TextCell textCell4 = new TextCell(context);
        this.reactionsCell = textCell4;
        textCell4.setBackground(Theme.getSelectorDrawable(false));
        this.reactionsCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda31
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatEditActivity.this.m2038lambda$createView$17$orgtelegramuiChatEditActivity(view2);
            }
        });
        TextCell textCell5 = new TextCell(context);
        this.adminCell = textCell5;
        textCell5.setBackground(Theme.getSelectorDrawable(false));
        this.adminCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatEditActivity.this.m2039lambda$createView$18$orgtelegramuiChatEditActivity(view2);
            }
        });
        TextCell textCell6 = new TextCell(context);
        this.membersCell = textCell6;
        textCell6.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.membersCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChatEditActivity.this.m2040lambda$createView$19$orgtelegramuiChatEditActivity(view2);
            }
        });
        if (!ChatObject.isChannelAndNotMegaGroup(this.currentChat)) {
            TextCell textCell7 = new TextCell(context);
            this.memberRequestsCell = textCell7;
            textCell7.setBackground(Theme.getSelectorDrawable(false));
            this.memberRequestsCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.m2041lambda$createView$20$orgtelegramuiChatEditActivity(view2);
                }
            });
        }
        if (ChatObject.isChannel(this.currentChat) || this.currentChat.gigagroup) {
            TextCell textCell8 = new TextCell(context);
            this.logCell = textCell8;
            textCell8.setTextAndIcon(LocaleController.getString("EventLog", R.string.EventLog), R.drawable.msg_log, false);
            this.logCell.setBackground(Theme.getSelectorDrawable(false));
            this.logCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.m2042lambda$createView$21$orgtelegramuiChatEditActivity(view2);
                }
            });
        }
        this.infoContainer.addView(this.reactionsCell, LayoutHelper.createLinear(-1, -2));
        if (!this.isChannel && !this.currentChat.gigagroup) {
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
        }
        if (!this.isChannel) {
            this.infoContainer.addView(this.inviteLinksCell, LayoutHelper.createLinear(-1, -2));
        }
        this.infoContainer.addView(this.adminCell, LayoutHelper.createLinear(-1, -2));
        this.infoContainer.addView(this.membersCell, LayoutHelper.createLinear(-1, -2));
        if (this.memberRequestsCell != null && (chatFull3 = this.info) != null && chatFull3.requests_pending > 0) {
            this.infoContainer.addView(this.memberRequestsCell, LayoutHelper.createLinear(-1, -2));
        }
        if (this.isChannel) {
            this.infoContainer.addView(this.inviteLinksCell, LayoutHelper.createLinear(-1, -2));
        }
        if (this.isChannel || this.currentChat.gigagroup) {
            this.infoContainer.addView(this.blockCell, LayoutHelper.createLinear(-1, -2));
        }
        if (!this.isChannel && (chatFull2 = this.info) != null && chatFull2.can_set_stickers) {
            FrameLayout frameLayout2 = new FrameLayout(context);
            this.stickersContainer = frameLayout2;
            frameLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            linearLayout1.addView(this.stickersContainer, LayoutHelper.createLinear(-1, -2));
            TextCell textCell9 = new TextCell(context);
            this.stickersCell = textCell9;
            textCell9.setBackground(Theme.getSelectorDrawable(false));
            this.stickersCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda5
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.m2043lambda$createView$22$orgtelegramuiChatEditActivity(view2);
                }
            });
            this.stickersCell.setPrioritizeTitleOverValue(true);
            this.stickersContainer.addView(this.stickersCell, LayoutHelper.createFrame(-1, -2.0f));
            this.stickersCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.m2044lambda$createView$23$orgtelegramuiChatEditActivity(view2);
                }
            });
        } else {
            TextCell textCell10 = this.logCell;
            if (textCell10 != null) {
                this.infoContainer.addView(textCell10, LayoutHelper.createLinear(-1, -2));
            }
        }
        if (!ChatObject.hasAdminRights(this.currentChat)) {
            this.infoContainer.setVisibility(8);
            this.settingsTopSectionCell.setVisibility(8);
        }
        if (this.stickersCell == null) {
            ShadowSectionCell shadowSectionCell3 = new ShadowSectionCell(context);
            this.infoSectionCell = shadowSectionCell3;
            linearLayout1.addView(shadowSectionCell3, LayoutHelper.createLinear(-1, -2));
        }
        if (!this.isChannel && (chatFull = this.info) != null && chatFull.can_set_stickers) {
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
            this.stickersInfoCell = textInfoPrivacyCell;
            textInfoPrivacyCell.setText(LocaleController.getString((int) R.string.GroupStickersInfo));
            linearLayout1.addView(this.stickersInfoCell, LayoutHelper.createLinear(-1, -2));
        }
        if (this.currentChat.creator) {
            FrameLayout frameLayout3 = new FrameLayout(context);
            this.deleteContainer = frameLayout3;
            frameLayout3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            linearLayout1.addView(this.deleteContainer, LayoutHelper.createLinear(-1, -2));
            TextSettingsCell textSettingsCell = new TextSettingsCell(context);
            this.deleteCell = textSettingsCell;
            textSettingsCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
            this.deleteCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (this.isChannel) {
                this.deleteCell.setText(LocaleController.getString("ChannelDelete", R.string.ChannelDelete), false);
            } else {
                this.deleteCell.setText(LocaleController.getString("DeleteAndExitButton", R.string.DeleteAndExitButton), false);
            }
            this.deleteContainer.addView(this.deleteCell, LayoutHelper.createFrame(-1, -2.0f));
            this.deleteCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChatEditActivity.this.m2046lambda$createView$25$orgtelegramuiChatEditActivity(view2);
                }
            });
            ShadowSectionCell shadowSectionCell4 = new ShadowSectionCell(context);
            this.deleteInfoCell = shadowSectionCell4;
            shadowSectionCell4.setBackground(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            linearLayout1.addView(this.deleteInfoCell, LayoutHelper.createLinear(-1, -2));
        }
        TextInfoPrivacyCell textInfoPrivacyCell2 = this.stickersInfoCell;
        if (textInfoPrivacyCell2 != null) {
            if (this.deleteInfoCell == null) {
                textInfoPrivacyCell2.setBackground(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            } else {
                textInfoPrivacyCell2.setBackground(Theme.getThemedDrawable(context, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
            }
        }
        UndoView undoView = new UndoView(context);
        this.undoView = undoView;
        sizeNotifierFrameLayout.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        this.nameTextView.setText(this.currentChat.title);
        EditTextEmoji editTextEmoji4 = this.nameTextView;
        editTextEmoji4.setSelection(editTextEmoji4.length());
        TLRPC.ChatFull chatFull6 = this.info;
        if (chatFull6 != null) {
            this.descriptionTextView.setText(chatFull6.about);
        }
        setAvatar();
        updateFields(true);
        return this.fragmentView;
    }

    public static /* synthetic */ boolean lambda$createView$2(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2047lambda$createView$3$orgtelegramuiChatEditActivity(View v) {
        ImageLocation videoLocation;
        if (this.imageUpdater.isUploadingImage()) {
            return;
        }
        TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
        if (chat.photo != null && chat.photo.photo_big != null) {
            PhotoViewer.getInstance().setParentActivity(getParentActivity());
            if (chat.photo.dc_id != 0) {
                chat.photo.photo_big.dc_id = chat.photo.dc_id;
            }
            TLRPC.ChatFull chatFull = this.info;
            if (chatFull != null && (chatFull.chat_photo instanceof TLRPC.TL_photo) && !this.info.chat_photo.video_sizes.isEmpty()) {
                videoLocation = ImageLocation.getForPhoto(this.info.chat_photo.video_sizes.get(0), this.info.chat_photo);
            } else {
                videoLocation = null;
            }
            PhotoViewer.getInstance().openPhotoWithVideo(chat.photo.photo_big, videoLocation, this.provider);
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2050lambda$createView$6$orgtelegramuiChatEditActivity(View v) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                ChatEditActivity.this.m2048lambda$createView$4$orgtelegramuiChatEditActivity();
            }
        }, new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda25
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                ChatEditActivity.this.m2049lambda$createView$5$orgtelegramuiChatEditActivity(dialogInterface);
            }
        });
        this.cameraDrawable.setCurrentFrame(0);
        this.cameraDrawable.setCustomEndFrame(43);
        this.setAvatarCell.imageView.playAnimation();
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2048lambda$createView$4$orgtelegramuiChatEditActivity() {
        this.avatar = null;
        MessagesController.getInstance(this.currentAccount).changeChatAvatar(this.chatId, null, null, null, FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE, null, null, null, null);
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, this.currentChat);
        this.cameraDrawable.setCurrentFrame(0);
        this.setAvatarCell.imageView.playAnimation();
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2049lambda$createView$5$orgtelegramuiChatEditActivity(DialogInterface dialogInterface) {
        if (!this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCustomEndFrame(86);
            this.setAvatarCell.imageView.playAnimation();
            return;
        }
        this.cameraDrawable.setCurrentFrame(0, false);
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ boolean m2051lambda$createView$7$orgtelegramuiChatEditActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i == 6 && (view = this.doneButton) != null) {
            view.performClick();
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2053lambda$createView$9$orgtelegramuiChatEditActivity(View v) {
        if (!AndroidUtilities.isGoogleMapsInstalled(this)) {
            return;
        }
        LocationActivity fragment = new LocationActivity(4);
        fragment.setDialogId(-this.chatId);
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null && (chatFull.location instanceof TLRPC.TL_channelLocation)) {
            fragment.setInitialLocation((TLRPC.TL_channelLocation) this.info.location);
        }
        fragment.setDelegate(new LocationActivity.LocationActivityDelegate() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda24
            @Override // org.telegram.ui.LocationActivity.LocationActivityDelegate
            public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                ChatEditActivity.this.m2052lambda$createView$8$orgtelegramuiChatEditActivity(messageMedia, i, z, i2);
            }
        });
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2052lambda$createView$8$orgtelegramuiChatEditActivity(TLRPC.MessageMedia location, int live, boolean notify, int scheduleDate) {
        TLRPC.TL_channelLocation channelLocation = new TLRPC.TL_channelLocation();
        channelLocation.address = location.address;
        channelLocation.geo_point = location.geo;
        this.info.location = channelLocation;
        this.info.flags |= 32768;
        updateFields(false);
        getMessagesController().loadFullChat(this.chatId, 0, true);
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2031lambda$createView$10$orgtelegramuiChatEditActivity(View v) {
        long j = this.chatId;
        TextDetailCell textDetailCell = this.locationCell;
        ChatEditTypeActivity fragment = new ChatEditTypeActivity(j, textDetailCell != null && textDetailCell.getVisibility() == 0);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2032lambda$createView$11$orgtelegramuiChatEditActivity(View v) {
        ChatLinkActivity fragment = new ChatLinkActivity(this.chatId);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$13$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2034lambda$createView$13$orgtelegramuiChatEditActivity(Context context, View v) {
        final BottomSheet.Builder builder = new BottomSheet.Builder(context);
        builder.setApplyTopPadding(false);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        HeaderCell headerCell = new HeaderCell(context, Theme.key_dialogTextBlue2, 23, 15, false);
        headerCell.setHeight(47);
        headerCell.setText(LocaleController.getString("ChatHistory", R.string.ChatHistory));
        linearLayout.addView(headerCell);
        LinearLayout linearLayoutInviteContainer = new LinearLayout(context);
        linearLayoutInviteContainer.setOrientation(1);
        linearLayout.addView(linearLayoutInviteContainer, LayoutHelper.createLinear(-1, -2));
        final RadioButtonCell[] buttons = new RadioButtonCell[2];
        int a = 0;
        for (int i = 2; a < i; i = 2) {
            buttons[a] = new RadioButtonCell(context, true);
            buttons[a].setTag(Integer.valueOf(a));
            buttons[a].setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (a == 0) {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryVisible", R.string.ChatHistoryVisible), LocaleController.getString("ChatHistoryVisibleInfo", R.string.ChatHistoryVisibleInfo), true, !this.historyHidden);
            } else if (ChatObject.isChannel(this.currentChat)) {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryHidden", R.string.ChatHistoryHidden), LocaleController.getString("ChatHistoryHiddenInfo", R.string.ChatHistoryHiddenInfo), false, this.historyHidden);
            } else {
                buttons[a].setTextAndValue(LocaleController.getString("ChatHistoryHidden", R.string.ChatHistoryHidden), LocaleController.getString("ChatHistoryHiddenInfo2", R.string.ChatHistoryHiddenInfo2), false, this.historyHidden);
            }
            linearLayoutInviteContainer.addView(buttons[a], LayoutHelper.createLinear(-1, -2));
            buttons[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda13
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatEditActivity.this.m2033lambda$createView$12$orgtelegramuiChatEditActivity(buttons, builder, view);
                }
            });
            a++;
        }
        builder.setCustomView(linearLayout);
        showDialog(builder.create());
    }

    /* renamed from: lambda$createView$12$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2033lambda$createView$12$orgtelegramuiChatEditActivity(RadioButtonCell[] buttons, BottomSheet.Builder builder, View v2) {
        Integer tag = (Integer) v2.getTag();
        boolean z = false;
        buttons[0].setChecked(tag.intValue() == 0, true);
        buttons[1].setChecked(tag.intValue() == 1, true);
        if (tag.intValue() == 1) {
            z = true;
        }
        this.historyHidden = z;
        builder.getDismissRunnable().run();
        updateFields(true);
    }

    /* renamed from: lambda$createView$14$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2035lambda$createView$14$orgtelegramuiChatEditActivity(View v) {
        boolean z = !this.signMessages;
        this.signMessages = z;
        ((TextCheckCell) v).setChecked(z);
    }

    /* renamed from: lambda$createView$15$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2036lambda$createView$15$orgtelegramuiChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
        args.putInt(CommonProperties.TYPE, (this.isChannel || this.currentChat.gigagroup) ? 0 : 3);
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$16$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2037lambda$createView$16$orgtelegramuiChatEditActivity(View v) {
        ManageLinksActivity fragment = new ManageLinksActivity(this.chatId, 0L, 0);
        TLRPC.ChatFull chatFull = this.info;
        fragment.setInfo(chatFull, chatFull.exported_invite);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$17$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2038lambda$createView$17$orgtelegramuiChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
        ChatReactionsEditActivity reactionsEditActivity = new ChatReactionsEditActivity(args);
        reactionsEditActivity.setInfo(this.info);
        presentFragment(reactionsEditActivity);
    }

    /* renamed from: lambda$createView$18$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2039lambda$createView$18$orgtelegramuiChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
        args.putInt(CommonProperties.TYPE, 1);
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$19$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2040lambda$createView$19$orgtelegramuiChatEditActivity(View v) {
        Bundle args = new Bundle();
        args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
        args.putInt(CommonProperties.TYPE, 2);
        ChatUsersActivity fragment = new ChatUsersActivity(args);
        fragment.setInfo(this.info);
        presentFragment(fragment);
    }

    /* renamed from: lambda$createView$20$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2041lambda$createView$20$orgtelegramuiChatEditActivity(View v) {
        MemberRequestsActivity activity = new MemberRequestsActivity(this.chatId);
        presentFragment(activity);
    }

    /* renamed from: lambda$createView$21$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2042lambda$createView$21$orgtelegramuiChatEditActivity(View v) {
        presentFragment(new ChannelAdminLogActivity(this.currentChat));
    }

    /* renamed from: lambda$createView$22$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2043lambda$createView$22$orgtelegramuiChatEditActivity(View v) {
        presentFragment(new ChannelAdminLogActivity(this.currentChat));
    }

    /* renamed from: lambda$createView$23$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2044lambda$createView$23$orgtelegramuiChatEditActivity(View v) {
        GroupStickersActivity groupStickersActivity = new GroupStickersActivity(this.currentChat.id);
        groupStickersActivity.setInfo(this.info);
        presentFragment(groupStickersActivity);
    }

    /* renamed from: lambda$createView$25$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2046lambda$createView$25$orgtelegramuiChatEditActivity(View v) {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, true, false, this.currentChat, null, false, true, false, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda19
            @Override // org.telegram.messenger.MessagesStorage.BooleanCallback
            public final void run(boolean z) {
                ChatEditActivity.this.m2045lambda$createView$24$orgtelegramuiChatEditActivity(z);
            }
        }, null);
    }

    /* renamed from: lambda$createView$24$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2045lambda$createView$24$orgtelegramuiChatEditActivity(boolean param) {
        if (AndroidUtilities.isTablet()) {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, Long.valueOf(-this.chatId));
        } else {
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        finishFragment();
        getNotificationCenter().postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf(-this.currentChat.id), null, this.currentChat, Boolean.valueOf(param));
    }

    private void setAvatar() {
        TLRPC.Chat chat;
        boolean hasPhoto;
        if (this.avatarImage == null || (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) == null) {
            return;
        }
        this.currentChat = chat;
        if (chat.photo != null) {
            this.avatar = this.currentChat.photo.photo_small;
            ImageLocation location = ImageLocation.getForUserOrChat(this.currentChat, 1);
            this.avatarImage.setForUserOrChat(this.currentChat, this.avatarDrawable);
            hasPhoto = location != null;
        } else {
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            hasPhoto = false;
        }
        if (this.setAvatarCell != null) {
            if (hasPhoto || this.imageUpdater.isUploadingImage()) {
                this.setAvatarCell.setTextAndIcon(LocaleController.getString("ChatSetNewPhoto", R.string.ChatSetNewPhoto), R.drawable.msg_addphoto, true);
            } else {
                this.setAvatarCell.setTextAndIcon(LocaleController.getString("ChatSetPhotoOrVideo", R.string.ChatSetPhotoOrVideo), R.drawable.msg_addphoto, true);
            }
            if (this.cameraDrawable == null) {
                this.cameraDrawable = new RLottieDrawable(R.raw.camera_outline, "2131558415", AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f), false, null);
            }
            this.setAvatarCell.imageView.setTranslationY(-AndroidUtilities.dp(9.0f));
            this.setAvatarCell.imageView.setTranslationX(-AndroidUtilities.dp(8.0f));
            this.setAvatarCell.imageView.setAnimation(this.cameraDrawable);
        }
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().checkCurrentImageVisibility();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        EditTextBoldCursor editTextBoldCursor;
        if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) args[0];
            if (chatFull.id == this.chatId) {
                if (this.info == null && (editTextBoldCursor = this.descriptionTextView) != null) {
                    editTextBoldCursor.setText(chatFull.about);
                }
                boolean z = true;
                boolean infoWasEmpty = this.info == null;
                this.info = chatFull;
                if (ChatObject.isChannel(this.currentChat) && !this.info.hidden_prehistory) {
                    z = false;
                }
                this.historyHidden = z;
                updateFields(false);
                if (infoWasEmpty) {
                    loadLinksCount();
                }
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0) {
                setAvatar();
            }
        } else {
            int mask2 = NotificationCenter.chatAvailableReactionsUpdated;
            if (id == mask2) {
                long chatId = ((Long) args[0]).longValue();
                if (chatId == this.chatId) {
                    TLRPC.ChatFull chatFull2 = getMessagesController().getChatFull(chatId);
                    this.info = chatFull2;
                    if (chatFull2 != null) {
                        this.availableReactions = chatFull2.available_reactions;
                    }
                    updateReactionsCell();
                }
            }
        }
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void onUploadProgressChanged(float progress) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(progress);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void didStartUpload(boolean isVideo) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(0.0f);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void didUploadPhoto(final TLRPC.InputFile photo, final TLRPC.InputFile video, final double videoStartTimestamp, final String videoPath, final TLRPC.PhotoSize bigSize, final TLRPC.PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                ChatEditActivity.this.m2054lambda$didUploadPhoto$26$orgtelegramuiChatEditActivity(smallSize, photo, video, videoStartTimestamp, videoPath, bigSize);
            }
        });
    }

    /* renamed from: lambda$didUploadPhoto$26$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2054lambda$didUploadPhoto$26$orgtelegramuiChatEditActivity(TLRPC.PhotoSize smallSize, TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, String videoPath, TLRPC.PhotoSize bigSize) {
        TLRPC.FileLocation fileLocation = smallSize.location;
        this.avatar = fileLocation;
        if (photo != null || video != null) {
            getMessagesController().changeChatAvatar(this.chatId, null, photo, video, videoStartTimestamp, videoPath, smallSize.location, bigSize.location, null);
            if (this.createAfterUpload) {
                try {
                    AlertDialog alertDialog = this.progressDialog;
                    if (alertDialog != null && alertDialog.isShowing()) {
                        this.progressDialog.dismiss();
                        this.progressDialog = null;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.donePressed = false;
                this.doneButton.performClick();
            }
            showAvatarProgress(false, true);
            return;
        }
        this.avatarImage.setImage(ImageLocation.getForLocal(fileLocation), "50_50", this.avatarDrawable, this.currentChat);
        this.setAvatarCell.setTextAndIcon(LocaleController.getString("ChatSetNewPhoto", R.string.ChatSetNewPhoto), R.drawable.msg_addphoto, true);
        if (this.cameraDrawable == null) {
            this.cameraDrawable = new RLottieDrawable(R.raw.camera_outline, "2131558415", AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f), false, null);
        }
        this.setAvatarCell.imageView.setTranslationY(-AndroidUtilities.dp(9.0f));
        this.setAvatarCell.imageView.setTranslationX(-AndroidUtilities.dp(8.0f));
        this.setAvatarCell.imageView.setAnimation(this.cameraDrawable);
        showAvatarProgress(true, false);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
    }

    public void showConvertTooltip() {
        this.undoView.showWithAction(0L, 76, (Runnable) null);
    }

    public boolean checkDiscard() {
        EditTextBoldCursor editTextBoldCursor;
        TLRPC.ChatFull chatFull = this.info;
        String about = (chatFull == null || chatFull.about == null) ? "" : this.info.about;
        if ((this.info != null && ChatObject.isChannel(this.currentChat) && this.info.hidden_prehistory != this.historyHidden) || ((this.nameTextView != null && !this.currentChat.title.equals(this.nameTextView.getText().toString())) || (((editTextBoldCursor = this.descriptionTextView) != null && !about.equals(editTextBoldCursor.getText().toString())) || this.signMessages != this.currentChat.signatures))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("UserRestrictionsApplyChanges", R.string.UserRestrictionsApplyChanges));
            if (this.isChannel) {
                builder.setMessage(LocaleController.getString("ChannelSettingsChangedAlert", R.string.ChannelSettingsChangedAlert));
            } else {
                builder.setMessage(LocaleController.getString("GroupSettingsChangedAlert", R.string.GroupSettingsChangedAlert));
            }
            builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda11
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatEditActivity.this.m2029lambda$checkDiscard$27$orgtelegramuiChatEditActivity(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda22
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ChatEditActivity.this.m2030lambda$checkDiscard$28$orgtelegramuiChatEditActivity(dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return false;
        }
        return true;
    }

    /* renamed from: lambda$checkDiscard$27$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2029lambda$checkDiscard$27$orgtelegramuiChatEditActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* renamed from: lambda$checkDiscard$28$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2030lambda$checkDiscard$28$orgtelegramuiChatEditActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    private int getAdminCount() {
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull == null) {
            return 1;
        }
        int count = 0;
        int N = chatFull.participants.participants.size();
        for (int a = 0; a < N; a++) {
            TLRPC.ChatParticipant chatParticipant = this.info.participants.participants.get(a);
            if ((chatParticipant instanceof TLRPC.TL_chatParticipantAdmin) || (chatParticipant instanceof TLRPC.TL_chatParticipantCreator)) {
                count++;
            }
        }
        return count;
    }

    public void processDone() {
        EditTextEmoji editTextEmoji;
        if (this.donePressed || (editTextEmoji = this.nameTextView) == null) {
            return;
        }
        if (editTextEmoji.length() == 0) {
            Vibrator v = (Vibrator) getParentActivity().getSystemService("vibrator");
            if (v != null) {
                v.vibrate(200L);
            }
            AndroidUtilities.shakeView(this.nameTextView, 2.0f, 0);
            return;
        }
        this.donePressed = true;
        if (!ChatObject.isChannel(this.currentChat) && !this.historyHidden) {
            getMessagesController().convertToMegaGroup(getParentActivity(), this.chatId, this, new MessagesStorage.LongCallback() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda20
                @Override // org.telegram.messenger.MessagesStorage.LongCallback
                public final void run(long j) {
                    ChatEditActivity.this.m2058lambda$processDone$29$orgtelegramuiChatEditActivity(j);
                }
            });
            return;
        }
        if (this.info != null && ChatObject.isChannel(this.currentChat)) {
            boolean z = this.info.hidden_prehistory;
            boolean z2 = this.historyHidden;
            if (z != z2) {
                this.info.hidden_prehistory = z2;
                getMessagesController().toggleChannelInvitesHistory(this.chatId, this.historyHidden);
            }
        }
        if (this.imageUpdater.isUploadingImage()) {
            this.createAfterUpload = true;
            AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
            this.progressDialog = alertDialog;
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    ChatEditActivity.this.m2059lambda$processDone$30$orgtelegramuiChatEditActivity(dialogInterface);
                }
            });
            this.progressDialog.show();
            return;
        }
        if (!this.currentChat.title.equals(this.nameTextView.getText().toString())) {
            getMessagesController().changeChatTitle(this.chatId, this.nameTextView.getText().toString());
        }
        TLRPC.ChatFull chatFull = this.info;
        String about = (chatFull == null || chatFull.about == null) ? "" : this.info.about;
        EditTextBoldCursor editTextBoldCursor = this.descriptionTextView;
        if (editTextBoldCursor != null && !about.equals(editTextBoldCursor.getText().toString())) {
            getMessagesController().updateChatAbout(this.chatId, this.descriptionTextView.getText().toString(), this.info);
        }
        if (this.signMessages != this.currentChat.signatures) {
            this.currentChat.signatures = true;
            getMessagesController().toggleChannelSignatures(this.chatId, this.signMessages);
        }
        finishFragment();
    }

    /* renamed from: lambda$processDone$29$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2058lambda$processDone$29$orgtelegramuiChatEditActivity(long param) {
        if (param == 0) {
            this.donePressed = false;
            return;
        }
        this.chatId = param;
        this.currentChat = getMessagesController().getChat(Long.valueOf(param));
        this.donePressed = false;
        TLRPC.ChatFull chatFull = this.info;
        if (chatFull != null) {
            chatFull.hidden_prehistory = true;
        }
        processDone();
    }

    /* renamed from: lambda$processDone$30$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2059lambda$processDone$30$orgtelegramuiChatEditActivity(DialogInterface dialog) {
        this.createAfterUpload = false;
        this.progressDialog = null;
        this.donePressed = false;
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarProgressView == null) {
            return;
        }
        AnimatorSet animatorSet = this.avatarAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.avatarAnimation = null;
        }
        if (animated) {
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.avatarAnimation = animatorSet2;
            if (show) {
                this.avatarProgressView.setVisibility(0);
                this.avatarOverlay.setVisibility(0);
                this.avatarAnimation.playTogether(ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.avatarOverlay, View.ALPHA, 1.0f));
            } else {
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.avatarOverlay, View.ALPHA, 0.0f));
            }
            this.avatarAnimation.setDuration(180L);
            this.avatarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatEditActivity.9
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (ChatEditActivity.this.avatarAnimation == null || ChatEditActivity.this.avatarProgressView == null) {
                        return;
                    }
                    if (!show) {
                        ChatEditActivity.this.avatarProgressView.setVisibility(4);
                        ChatEditActivity.this.avatarOverlay.setVisibility(4);
                    }
                    ChatEditActivity.this.avatarAnimation = null;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    ChatEditActivity.this.avatarAnimation = null;
                }
            });
            this.avatarAnimation.start();
        } else if (show) {
            this.avatarProgressView.setAlpha(1.0f);
            this.avatarProgressView.setVisibility(0);
            this.avatarOverlay.setAlpha(1.0f);
            this.avatarOverlay.setVisibility(0);
        } else {
            this.avatarProgressView.setAlpha(0.0f);
            this.avatarProgressView.setVisibility(4);
            this.avatarOverlay.setAlpha(0.0f);
            this.avatarOverlay.setVisibility(4);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        this.imageUpdater.onActivityResult(requestCode, resultCode, data);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void saveSelfArgs(Bundle args) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null && imageUpdater.currentPicturePath != null) {
            args.putString("path", this.imageUpdater.currentPicturePath);
        }
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            String text = editTextEmoji.getText().toString();
            if (text.length() != 0) {
                args.putString("nameTextView", text);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void restoreSelfArgs(Bundle args) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.currentPicturePath = args.getString("path");
        }
    }

    public void setInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        if (chatFull != null) {
            if (this.currentChat == null) {
                this.currentChat = getMessagesController().getChat(Long.valueOf(this.chatId));
            }
            this.historyHidden = !ChatObject.isChannel(this.currentChat) || this.info.hidden_prehistory;
            this.availableReactions = this.info.available_reactions;
        }
    }

    private void updateFields(boolean updateChat) {
        String str;
        int i;
        String type;
        TextDetailCell textDetailCell;
        TextDetailCell textDetailCell2;
        String str2;
        int i2;
        String str3;
        int i3;
        String link;
        TextDetailCell textDetailCell3;
        TLRPC.ChatFull chatFull;
        TextDetailCell textDetailCell4;
        TextDetailCell textDetailCell5;
        TextDetailCell textDetailCell6;
        TLRPC.ChatFull chatFull2;
        TLRPC.Chat chat;
        if (updateChat && (chat = getMessagesController().getChat(Long.valueOf(this.chatId))) != null) {
            this.currentChat = chat;
        }
        boolean isPrivate = TextUtils.isEmpty(this.currentChat.username);
        if (this.historyCell != null) {
            TLRPC.ChatFull chatFull3 = this.info;
            if (chatFull3 != null && (chatFull3.location instanceof TLRPC.TL_channelLocation)) {
                this.historyCell.setVisibility(8);
            } else {
                this.historyCell.setVisibility((!isPrivate || !((chatFull2 = this.info) == null || chatFull2.linked_chat_id == 0)) ? 8 : 0);
            }
        }
        ShadowSectionCell shadowSectionCell = this.settingsSectionCell;
        if (shadowSectionCell != null) {
            shadowSectionCell.setVisibility((this.signCell == null && this.typeCell == null && ((textDetailCell4 = this.linkedCell) == null || textDetailCell4.getVisibility() != 0) && (((textDetailCell5 = this.historyCell) == null || textDetailCell5.getVisibility() != 0) && ((textDetailCell6 = this.locationCell) == null || textDetailCell6.getVisibility() != 0))) ? 8 : 0);
        }
        TextCell textCell = this.logCell;
        if (textCell != null) {
            textCell.setVisibility((!this.currentChat.megagroup || this.currentChat.gigagroup || ((chatFull = this.info) != null && chatFull.participants_count > 200)) ? 0 : 8);
        }
        if (this.linkedCell != null) {
            TLRPC.ChatFull chatFull4 = this.info;
            if (chatFull4 == null || (!this.isChannel && chatFull4.linked_chat_id == 0)) {
                this.linkedCell.setVisibility(8);
            } else {
                this.linkedCell.setVisibility(0);
                if (this.info.linked_chat_id != 0) {
                    TLRPC.Chat chat2 = getMessagesController().getChat(Long.valueOf(this.info.linked_chat_id));
                    if (chat2 == null) {
                        this.linkedCell.setVisibility(8);
                    } else if (this.isChannel) {
                        if (TextUtils.isEmpty(chat2.username)) {
                            this.linkedCell.setTextAndValue(LocaleController.getString("Discussion", R.string.Discussion), chat2.title, true);
                        } else {
                            TextDetailCell textDetailCell7 = this.linkedCell;
                            String string = LocaleController.getString("Discussion", R.string.Discussion);
                            textDetailCell7.setTextAndValue(string, "@" + chat2.username, true);
                        }
                    } else if (TextUtils.isEmpty(chat2.username)) {
                        this.linkedCell.setTextAndValue(LocaleController.getString("LinkedChannel", R.string.LinkedChannel), chat2.title, false);
                    } else {
                        TextDetailCell textDetailCell8 = this.linkedCell;
                        String string2 = LocaleController.getString("LinkedChannel", R.string.LinkedChannel);
                        textDetailCell8.setTextAndValue(string2, "@" + chat2.username, false);
                    }
                } else {
                    this.linkedCell.setTextAndValue(LocaleController.getString("Discussion", R.string.Discussion), LocaleController.getString("DiscussionInfo", R.string.DiscussionInfo), true);
                }
            }
        }
        if (this.locationCell != null) {
            TLRPC.ChatFull chatFull5 = this.info;
            if (chatFull5 != null && chatFull5.can_set_location) {
                this.locationCell.setVisibility(0);
                if (this.info.location instanceof TLRPC.TL_channelLocation) {
                    TLRPC.TL_channelLocation location = (TLRPC.TL_channelLocation) this.info.location;
                    this.locationCell.setTextAndValue(LocaleController.getString("AttachLocation", R.string.AttachLocation), location.address, true);
                } else {
                    this.locationCell.setTextAndValue(LocaleController.getString("AttachLocation", R.string.AttachLocation), "Unknown address", true);
                }
            } else {
                this.locationCell.setVisibility(8);
            }
        }
        if (this.typeCell != null) {
            TLRPC.ChatFull chatFull6 = this.info;
            if (chatFull6 != null && (chatFull6.location instanceof TLRPC.TL_channelLocation)) {
                if (isPrivate) {
                    link = LocaleController.getString("TypeLocationGroupEdit", R.string.TypeLocationGroupEdit);
                } else {
                    link = String.format("https://" + getMessagesController().linkPrefix + "/%s", this.currentChat.username);
                }
                TextDetailCell textDetailCell9 = this.typeCell;
                String string3 = LocaleController.getString("TypeLocationGroup", R.string.TypeLocationGroup);
                TextDetailCell textDetailCell10 = this.historyCell;
                textDetailCell9.setTextAndValue(string3, link, (textDetailCell10 != null && textDetailCell10.getVisibility() == 0) || ((textDetailCell3 = this.linkedCell) != null && textDetailCell3.getVisibility() == 0));
            } else {
                boolean isRestricted = this.currentChat.noforwards;
                if (this.isChannel) {
                    if (!isPrivate) {
                        i3 = R.string.TypePublic;
                        str3 = "TypePublic";
                    } else if (isRestricted) {
                        i3 = R.string.TypePrivateRestrictedForwards;
                        str3 = "TypePrivateRestrictedForwards";
                    } else {
                        i3 = R.string.TypePrivate;
                        str3 = "TypePrivate";
                    }
                    type = LocaleController.getString(str3, i3);
                } else {
                    if (!isPrivate) {
                        i2 = R.string.TypePublicGroup;
                        str2 = "TypePublicGroup";
                    } else if (isRestricted) {
                        i2 = R.string.TypePrivateGroupRestrictedForwards;
                        str2 = "TypePrivateGroupRestrictedForwards";
                    } else {
                        i2 = R.string.TypePrivateGroup;
                        str2 = "TypePrivateGroup";
                    }
                    type = LocaleController.getString(str2, i2);
                }
                if (this.isChannel) {
                    TextDetailCell textDetailCell11 = this.typeCell;
                    String string4 = LocaleController.getString("ChannelType", R.string.ChannelType);
                    TextDetailCell textDetailCell12 = this.historyCell;
                    textDetailCell11.setTextAndValue(string4, type, (textDetailCell12 != null && textDetailCell12.getVisibility() == 0) || ((textDetailCell2 = this.linkedCell) != null && textDetailCell2.getVisibility() == 0));
                } else {
                    TextDetailCell textDetailCell13 = this.typeCell;
                    String string5 = LocaleController.getString("GroupType", R.string.GroupType);
                    TextDetailCell textDetailCell14 = this.historyCell;
                    textDetailCell13.setTextAndValue(string5, type, (textDetailCell14 != null && textDetailCell14.getVisibility() == 0) || ((textDetailCell = this.linkedCell) != null && textDetailCell.getVisibility() == 0));
                }
            }
        }
        if (this.historyCell != null) {
            if (this.historyHidden) {
                i = R.string.ChatHistoryHidden;
                str = "ChatHistoryHidden";
            } else {
                i = R.string.ChatHistoryVisible;
                str = "ChatHistoryVisible";
            }
            this.historyCell.setTextAndValue(LocaleController.getString("ChatHistory", R.string.ChatHistory), LocaleController.getString(str, i), false);
        }
        TextCell textCell2 = this.membersCell;
        if (textCell2 != null) {
            if (this.info != null) {
                TextCell textCell3 = this.memberRequestsCell;
                if (textCell3 != null) {
                    if (textCell3.getParent() == null) {
                        int position = this.infoContainer.indexOfChild(this.membersCell) + 1;
                        this.infoContainer.addView(this.memberRequestsCell, position, LayoutHelper.createLinear(-1, -2));
                    }
                    this.memberRequestsCell.setVisibility(this.info.requests_pending > 0 ? 0 : 8);
                }
                if (this.isChannel) {
                    this.membersCell.setTextAndValueAndIcon(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), String.format("%d", Integer.valueOf(this.info.participants_count)), R.drawable.msg_groups, true);
                    TextCell textCell4 = this.blockCell;
                    String string6 = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                    String format = String.format("%d", Integer.valueOf(Math.max(this.info.banned_count, this.info.kicked_count)));
                    TextCell textCell5 = this.logCell;
                    textCell4.setTextAndValueAndIcon(string6, format, R.drawable.msg_user_remove, textCell5 != null && textCell5.getVisibility() == 0);
                } else {
                    if (!ChatObject.isChannel(this.currentChat)) {
                        this.membersCell.setTextAndValueAndIcon(LocaleController.getString("ChannelMembers", R.string.ChannelMembers), String.format("%d", Integer.valueOf(this.info.participants.participants.size())), R.drawable.msg_groups, this.memberRequestsCell.getVisibility() == 0);
                    } else {
                        this.membersCell.setTextAndValueAndIcon(LocaleController.getString("ChannelMembers", R.string.ChannelMembers), String.format("%d", Integer.valueOf(this.info.participants_count)), R.drawable.msg_groups, true);
                    }
                    if (this.currentChat.gigagroup) {
                        TextCell textCell6 = this.blockCell;
                        String string7 = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                        String format2 = String.format("%d", Integer.valueOf(Math.max(this.info.banned_count, this.info.kicked_count)));
                        TextCell textCell7 = this.logCell;
                        textCell6.setTextAndValueAndIcon(string7, format2, R.drawable.msg_user_remove, textCell7 != null && textCell7.getVisibility() == 0);
                    } else {
                        int count = 0;
                        if (this.currentChat.default_banned_rights != null) {
                            if (!this.currentChat.default_banned_rights.send_stickers) {
                                count = 0 + 1;
                            }
                            if (!this.currentChat.default_banned_rights.send_media) {
                                count++;
                            }
                            if (!this.currentChat.default_banned_rights.embed_links) {
                                count++;
                            }
                            if (!this.currentChat.default_banned_rights.send_messages) {
                                count++;
                            }
                            if (!this.currentChat.default_banned_rights.pin_messages) {
                                count++;
                            }
                            if (!this.currentChat.default_banned_rights.send_polls) {
                                count++;
                            }
                            if (!this.currentChat.default_banned_rights.invite_users) {
                                count++;
                            }
                            if (!this.currentChat.default_banned_rights.change_info) {
                                count++;
                            }
                        } else {
                            count = 8;
                        }
                        this.blockCell.setTextAndValueAndIcon(LocaleController.getString("ChannelPermissions", R.string.ChannelPermissions), String.format("%d/%d", Integer.valueOf(count), 8), R.drawable.msg_permissions, true);
                    }
                    TextCell textCell8 = this.memberRequestsCell;
                    if (textCell8 != null) {
                        String string8 = LocaleController.getString("MemberRequests", R.string.MemberRequests);
                        String format3 = String.format("%d", Integer.valueOf(this.info.requests_pending));
                        TextCell textCell9 = this.logCell;
                        textCell8.setTextAndValueAndIcon(string8, format3, R.drawable.msg_requests, textCell9 != null && textCell9.getVisibility() == 0);
                    }
                }
                TextCell textCell10 = this.adminCell;
                String string9 = LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators);
                Object[] objArr = new Object[1];
                objArr[0] = Integer.valueOf(ChatObject.isChannel(this.currentChat) ? this.info.admins_count : getAdminCount());
                textCell10.setTextAndValueAndIcon(string9, String.format("%d", objArr), R.drawable.msg_admins, true);
            } else {
                if (this.isChannel) {
                    textCell2.setTextAndIcon(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), R.drawable.msg_groups, true);
                    TextCell textCell11 = this.blockCell;
                    String string10 = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                    TextCell textCell12 = this.logCell;
                    textCell11.setTextAndIcon(string10, R.drawable.msg_chats_remove, textCell12 != null && textCell12.getVisibility() == 0);
                } else {
                    String string11 = LocaleController.getString("ChannelMembers", R.string.ChannelMembers);
                    TextCell textCell13 = this.logCell;
                    textCell2.setTextAndIcon(string11, R.drawable.msg_groups, textCell13 != null && textCell13.getVisibility() == 0);
                    if (this.currentChat.gigagroup) {
                        TextCell textCell14 = this.blockCell;
                        String string12 = LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist);
                        TextCell textCell15 = this.logCell;
                        textCell14.setTextAndIcon(string12, R.drawable.msg_chats_remove, textCell15 != null && textCell15.getVisibility() == 0);
                    } else {
                        this.blockCell.setTextAndIcon(LocaleController.getString("ChannelPermissions", R.string.ChannelPermissions), R.drawable.msg_permissions, true);
                    }
                }
                this.adminCell.setTextAndIcon(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), R.drawable.msg_admins, true);
            }
            this.reactionsCell.setVisibility(ChatObject.canChangeChatInfo(this.currentChat) ? 0 : 8);
            updateReactionsCell();
            if (this.info == null || !ChatObject.canUserDoAdminAction(this.currentChat, 3) || (!isPrivate && this.currentChat.creator)) {
                this.inviteLinksCell.setVisibility(8);
            } else if (this.info.invitesCount > 0) {
                this.inviteLinksCell.setTextAndValueAndIcon(LocaleController.getString("InviteLinks", R.string.InviteLinks), Integer.toString(this.info.invitesCount), R.drawable.msg_link2, true);
            } else {
                this.inviteLinksCell.setTextAndValueAndIcon(LocaleController.getString("InviteLinks", R.string.InviteLinks), IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE, R.drawable.msg_link2, true);
            }
        }
        TextCell textCell16 = this.stickersCell;
        if (textCell16 != null && this.info != null) {
            textCell16.setTextAndValueAndIcon(LocaleController.getString((int) R.string.GroupStickers), this.info.stickerset != null ? this.info.stickerset.title : LocaleController.getString((int) R.string.Add), R.drawable.msg_sticker, false);
        }
    }

    private void updateReactionsCell() {
        int count = 0;
        for (int i = 0; i < this.availableReactions.size(); i++) {
            TLRPC.TL_availableReaction reaction = getMediaDataController().getReactionsMap().get(this.availableReactions.get(i));
            if (reaction != null && !reaction.inactive) {
                count++;
            }
        }
        int reacts = Math.min(getMediaDataController().getEnabledReactionsList().size(), count);
        this.reactionsCell.setTextAndValueAndIcon(LocaleController.getString("Reactions", R.string.Reactions), reacts == 0 ? LocaleController.getString("ReactionsOff", R.string.ReactionsOff) : LocaleController.formatString("ReactionsCount", R.string.ReactionsCount, Integer.valueOf(reacts), Integer.valueOf(getMediaDataController().getEnabledReactionsList().size())), R.drawable.msg_reactions2, true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ChatEditActivity$$ExternalSyntheticLambda23
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ChatEditActivity.this.m2055lambda$getThemeDescriptions$31$orgtelegramuiChatEditActivity();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.setAvatarCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.setAvatarCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        themeDescriptions.add(new ThemeDescription(this.setAvatarCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        themeDescriptions.add(new ThemeDescription(this.membersCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.membersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.membersCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.adminCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.adminCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.adminCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.inviteLinksCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.inviteLinksCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.inviteLinksCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        if (this.memberRequestsCell != null) {
            themeDescriptions.add(new ThemeDescription(this.memberRequestsCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
            themeDescriptions.add(new ThemeDescription(this.memberRequestsCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            themeDescriptions.add(new ThemeDescription(this.memberRequestsCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        }
        themeDescriptions.add(new ThemeDescription(this.blockCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.blockCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.blockCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.logCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.logCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.logCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.typeCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.typeCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.historyCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.historyCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.locationCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.locationCell, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        themeDescriptions.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.avatarContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.settingsContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.typeEditContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.deleteContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.stickersContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.infoContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.settingsTopSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.settingsSectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.deleteInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.signCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.signCell, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.deleteCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText5));
        themeDescriptions.add(new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.stickersCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.stickersInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.stickersInfoCell, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, Theme.avatarDrawables, cellDelegate, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.reactionsCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.reactionsCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.reactionsCell, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$31$org-telegram-ui-ChatEditActivity */
    public /* synthetic */ void m2055lambda$getThemeDescriptions$31$orgtelegramuiChatEditActivity() {
        BackupImageView backupImageView = this.avatarImage;
        if (backupImageView != null) {
            backupImageView.invalidate();
        }
    }
}
