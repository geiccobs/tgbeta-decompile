package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.content.FileProvider;
import androidx.core.net.MailTo;
import androidx.recyclerview.widget.ChatListItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.firebase.messaging.Constants;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.AvatarPreviewer;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.ChannelAdminLogActivity;
import org.telegram.ui.Components.AdminLogFilterAlert;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ClearHistoryAlert;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.InviteLinkBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhonebookShareAlert;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes4.dex */
public class ChannelAdminLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList<TLRPC.ChannelParticipant> admins;
    private int allowAnimationIndex;
    private Paint aspectPaint;
    private Path aspectPath;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private ChatAvatarContainer avatarContainer;
    private FrameLayout bottomOverlayChat;
    private TextView bottomOverlayChatText;
    private ImageView bottomOverlayImage;
    private ChatActivityAdapter chatAdapter;
    private LinearLayoutManager chatLayoutManager;
    private ChatListItemAnimator chatListItemAnimator;
    private RecyclerListView chatListView;
    private boolean checkTextureViewPosition;
    private SizeNotifierFrameLayout contentView;
    protected TLRPC.Chat currentChat;
    private boolean currentFloatingDateOnScreen;
    private boolean currentFloatingTopIsNotMessage;
    private TextView emptyView;
    private FrameLayout emptyViewContainer;
    private boolean endReached;
    private AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private boolean linviteLoading;
    private boolean loading;
    private int loadsCount;
    private int minDate;
    private long minEventId;
    private boolean openAnimationEnded;
    private RadialProgressView progressBar;
    private FrameLayout progressView;
    private View progressView2;
    private FrameLayout roundVideoContainer;
    private MessageObject scrollToMessage;
    private boolean scrollingFloatingDate;
    private ImageView searchCalendarButton;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ImageView searchDownButton;
    private ActionBarMenuItem searchItem;
    private ImageView searchUpButton;
    private boolean searchWas;
    private LongSparseArray<TLRPC.User> selectedAdmins;
    private MessageObject selectedObject;
    private UndoView undoView;
    private HashMap<Long, TLRPC.User> usersMap;
    private TextureView videoTextureView;
    private ArrayList<ChatMessageCell> chatMessageCellsCache = new ArrayList<>();
    private int[] mid = {2};
    private int scrollToPositionOnRecreate = -1;
    private int scrollToOffsetOnRecreate = 0;
    private boolean paused = true;
    private boolean wasPaused = false;
    private LongSparseArray<MessageObject> messagesDict = new LongSparseArray<>();
    private HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap<>();
    protected ArrayList<MessageObject> messages = new ArrayList<>();
    private TLRPC.TL_channelAdminLogEventsFilter currentFilter = null;
    private String searchQuery = "";
    private HashMap<String, Object> invitesCache = new HashMap<>();
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.ChannelAdminLogActivity.1
        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            ChatActionCell cell;
            MessageObject message;
            ChatMessageCell cell2;
            MessageObject message2;
            int count = ChannelAdminLogActivity.this.chatListView.getChildCount();
            for (int a = 0; a < count; a++) {
                ImageReceiver imageReceiver = null;
                View view = ChannelAdminLogActivity.this.chatListView.getChildAt(a);
                if (view instanceof ChatMessageCell) {
                    if (messageObject != null && (message2 = (cell2 = (ChatMessageCell) view).getMessageObject()) != null && message2.getId() == messageObject.getId()) {
                        imageReceiver = cell2.getPhotoImage();
                    }
                } else if ((view instanceof ChatActionCell) && (message = (cell = (ChatActionCell) view).getMessageObject()) != null) {
                    if (messageObject != null) {
                        if (message.getId() == messageObject.getId()) {
                            imageReceiver = cell.getPhotoImage();
                        }
                    } else if (fileLocation != null && message.photoThumbs != null) {
                        int b = 0;
                        while (true) {
                            if (b >= message.photoThumbs.size()) {
                                break;
                            }
                            TLRPC.PhotoSize photoSize = message.photoThumbs.get(b);
                            if (photoSize.location.volume_id != fileLocation.volume_id || photoSize.location.local_id != fileLocation.local_id) {
                                b++;
                            } else {
                                imageReceiver = cell.getPhotoImage();
                                break;
                            }
                        }
                    }
                }
                if (imageReceiver != null) {
                    int[] coords = new int[2];
                    view.getLocationInWindow(coords);
                    PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                    int i = 0;
                    object.viewX = coords[0];
                    int i2 = coords[1];
                    if (Build.VERSION.SDK_INT < 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    object.viewY = i2 - i;
                    object.parentView = ChannelAdminLogActivity.this.chatListView;
                    object.imageReceiver = imageReceiver;
                    object.thumb = imageReceiver.getBitmapSafe();
                    object.radius = imageReceiver.getRoundRadius();
                    object.isEvent = true;
                    return object;
                }
            }
            return null;
        }
    };

    public ChannelAdminLogActivity(TLRPC.Chat chat) {
        this.currentChat = chat;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        loadMessages(true);
        loadAdmins();
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        getNotificationCenter().onAnimationFinish(this.allowAnimationIndex);
    }

    private void updateEmptyPlaceholder() {
        if (this.emptyView == null) {
            return;
        }
        if (!TextUtils.isEmpty(this.searchQuery)) {
            this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
            this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EventLogEmptyTextSearch", R.string.EventLogEmptyTextSearch, this.searchQuery)));
        } else if (this.selectedAdmins != null || this.currentFilter != null) {
            this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
            this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptySearch", R.string.EventLogEmptySearch)));
        } else {
            this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            if (this.currentChat.megagroup) {
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmpty", R.string.EventLogEmpty)));
            } else {
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptyChannel", R.string.EventLogEmptyChannel)));
            }
        }
    }

    public void loadMessages(boolean reset) {
        ChatActivityAdapter chatActivityAdapter;
        if (this.loading) {
            return;
        }
        if (reset) {
            this.minEventId = Long.MAX_VALUE;
            FrameLayout frameLayout = this.progressView;
            if (frameLayout != null) {
                AndroidUtilities.updateViewVisibilityAnimated(frameLayout, true, 0.3f, true);
                this.emptyViewContainer.setVisibility(4);
                this.chatListView.setEmptyView(null);
            }
            this.messagesDict.clear();
            this.messages.clear();
            this.messagesByDays.clear();
        }
        this.loading = true;
        TLRPC.TL_channels_getAdminLog req = new TLRPC.TL_channels_getAdminLog();
        req.channel = MessagesController.getInputChannel(this.currentChat);
        req.q = this.searchQuery;
        req.limit = 50;
        if (!reset && !this.messages.isEmpty()) {
            req.max_id = this.minEventId;
        } else {
            req.max_id = 0L;
        }
        req.min_id = 0L;
        if (this.currentFilter != null) {
            req.flags = 1 | req.flags;
            req.events_filter = this.currentFilter;
        }
        if (this.selectedAdmins != null) {
            req.flags |= 2;
            for (int a = 0; a < this.selectedAdmins.size(); a++) {
                req.admins.add(MessagesController.getInstance(this.currentAccount).getInputUser(this.selectedAdmins.valueAt(a)));
            }
        }
        updateEmptyPlaceholder();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelAdminLogActivity.this.m1716lambda$loadMessages$1$orgtelegramuiChannelAdminLogActivity(tLObject, tL_error);
            }
        });
        if (reset && (chatActivityAdapter = this.chatAdapter) != null) {
            chatActivityAdapter.notifyDataSetChanged();
        }
    }

    /* renamed from: lambda$loadMessages$1$org-telegram-ui-ChannelAdminLogActivity */
    public /* synthetic */ void m1716lambda$loadMessages$1$orgtelegramuiChannelAdminLogActivity(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            final TLRPC.TL_channels_adminLogResults res = (TLRPC.TL_channels_adminLogResults) response;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    ChannelAdminLogActivity.this.m1715lambda$loadMessages$0$orgtelegramuiChannelAdminLogActivity(res);
                }
            });
        }
    }

    /* renamed from: lambda$loadMessages$0$org-telegram-ui-ChannelAdminLogActivity */
    public /* synthetic */ void m1715lambda$loadMessages$0$orgtelegramuiChannelAdminLogActivity(TLRPC.TL_channels_adminLogResults res) {
        int i = 0;
        this.chatListItemAnimator.setShouldAnimateEnterFromBottom(false);
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        boolean added = false;
        int oldRowsCount = this.messages.size();
        for (int a = 0; a < res.events.size(); a++) {
            TLRPC.TL_channelAdminLogEvent event = res.events.get(a);
            if (this.messagesDict.indexOfKey(event.id) < 0) {
                if (event.action instanceof TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin) {
                    TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin action = (TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin) event.action;
                    if ((action.prev_participant instanceof TLRPC.TL_channelParticipantCreator) && !(action.new_participant instanceof TLRPC.TL_channelParticipantCreator)) {
                    }
                }
                this.minEventId = Math.min(this.minEventId, event.id);
                added = true;
                MessageObject messageObject = new MessageObject(this.currentAccount, event, this.messages, this.messagesByDays, this.currentChat, this.mid, false);
                if (messageObject.contentType >= 0) {
                    this.messagesDict.put(event.id, messageObject);
                }
            }
        }
        int newRowsCount = this.messages.size() - oldRowsCount;
        this.loading = false;
        if (!added) {
            this.endReached = true;
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.progressView, false, 0.3f, true);
        this.chatListView.setEmptyView(this.emptyViewContainer);
        if (newRowsCount != 0) {
            int i2 = 0;
            if (this.endReached) {
                i2 = 1;
                this.chatAdapter.notifyItemRangeChanged(0, 2);
            }
            int firstVisPos = this.chatLayoutManager.findLastVisibleItemPosition();
            View firstVisView = this.chatLayoutManager.findViewByPosition(firstVisPos);
            if (firstVisView != null) {
                i = firstVisView.getTop();
            }
            int top = i - this.chatListView.getPaddingTop();
            if (newRowsCount - i2 > 0) {
                int insertStart = (i2 ^ 1) + 1;
                this.chatAdapter.notifyItemChanged(insertStart);
                this.chatAdapter.notifyItemRangeInserted(insertStart, newRowsCount - i2);
            }
            if (firstVisPos != -1) {
                this.chatLayoutManager.scrollToPositionWithOffset((firstVisPos + newRowsCount) - i2, top);
            }
        } else if (this.endReached) {
            this.chatAdapter.notifyItemRemoved(0);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        ChatMessageCell cell;
        MessageObject messageObject;
        ChatMessageCell cell2;
        MessageObject playing;
        ChatMessageCell cell3;
        MessageObject messageObject1;
        if (id == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.chatListView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
        } else if (id == NotificationCenter.messagePlayingDidStart) {
            if (((MessageObject) args[0]).isRoundVideo()) {
                MediaController.getInstance().setTextureView(createTextureView(true), this.aspectRatioFrameLayout, this.roundVideoContainer, true);
                updateTextureViewPosition();
            }
            RecyclerListView recyclerListView2 = this.chatListView;
            if (recyclerListView2 != null) {
                int count = recyclerListView2.getChildCount();
                for (int a = 0; a < count; a++) {
                    View view = this.chatListView.getChildAt(a);
                    if ((view instanceof ChatMessageCell) && (messageObject1 = (cell3 = (ChatMessageCell) view).getMessageObject()) != null) {
                        if (messageObject1.isVoice() || messageObject1.isMusic()) {
                            cell3.updateButtonState(false, true, false);
                        } else if (messageObject1.isRoundVideo()) {
                            cell3.checkVideoPlayback(false, null);
                            if (!MediaController.getInstance().isPlayingMessage(messageObject1) && messageObject1.audioProgress != 0.0f) {
                                messageObject1.resetPlayingProgress();
                                cell3.invalidate();
                            }
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.messagePlayingPlayStateChanged) {
            RecyclerListView recyclerListView3 = this.chatListView;
            if (recyclerListView3 != null) {
                int count2 = recyclerListView3.getChildCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View view2 = this.chatListView.getChildAt(a2);
                    if ((view2 instanceof ChatMessageCell) && (messageObject = (cell = (ChatMessageCell) view2).getMessageObject()) != null) {
                        if (messageObject.isVoice() || messageObject.isMusic()) {
                            cell.updateButtonState(false, true, false);
                        } else if (messageObject.isRoundVideo() && !MediaController.getInstance().isPlayingMessage(messageObject)) {
                            cell.checkVideoPlayback(true, null);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer mid = (Integer) args[0];
            RecyclerListView recyclerListView4 = this.chatListView;
            if (recyclerListView4 != null) {
                int count3 = recyclerListView4.getChildCount();
                for (int a3 = 0; a3 < count3; a3++) {
                    View view3 = this.chatListView.getChildAt(a3);
                    if ((view3 instanceof ChatMessageCell) && (playing = (cell2 = (ChatMessageCell) view3).getMessageObject()) != null && playing.getId() == mid.intValue()) {
                        MessageObject player = MediaController.getInstance().getPlayingMessageObject();
                        if (player != null) {
                            playing.audioProgress = player.audioProgress;
                            playing.audioProgressSec = player.audioProgressSec;
                            playing.audioPlayerDuration = player.audioPlayerDuration;
                            cell2.updatePlayingMessageProgress();
                            return;
                        }
                        return;
                    }
                }
            }
        } else if (id == NotificationCenter.didSetNewWallpapper && this.fragmentView != null) {
            this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
            this.progressView2.invalidate();
            TextView textView = this.emptyView;
            if (textView != null) {
                textView.invalidate();
            }
            this.chatListView.invalidateViews();
        }
    }

    public void updateBottomOverlay() {
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        if (this.chatMessageCellsCache.isEmpty()) {
            for (int a = 0; a < 8; a++) {
                this.chatMessageCellsCache.add(new ChatMessageCell(context));
            }
        }
        this.searchWas = false;
        this.hasOwnBackground = true;
        Theme.createChatResources(context, false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet());
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChannelAdminLogActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    ChannelAdminLogActivity.this.finishFragment();
                }
            }
        });
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context, null, false);
        this.avatarContainer = chatAvatarContainer;
        chatAvatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItemSearchListener = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.ChannelAdminLogActivity.3
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                ChannelAdminLogActivity.this.searchQuery = "";
                ChannelAdminLogActivity.this.avatarContainer.setVisibility(0);
                if (ChannelAdminLogActivity.this.searchWas) {
                    ChannelAdminLogActivity.this.searchWas = false;
                    ChannelAdminLogActivity.this.loadMessages(true);
                }
                ChannelAdminLogActivity.this.updateBottomOverlay();
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                ChannelAdminLogActivity.this.avatarContainer.setVisibility(8);
                ChannelAdminLogActivity.this.updateBottomOverlay();
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchPressed(EditText editText) {
                ChannelAdminLogActivity.this.searchWas = true;
                ChannelAdminLogActivity.this.searchQuery = editText.getText().toString();
                ChannelAdminLogActivity.this.loadMessages(true);
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.avatarContainer.setEnabled(false);
        this.avatarContainer.setTitle(this.currentChat.title);
        this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", R.string.EventLogAllEvents));
        this.avatarContainer.setChatAvatar(this.currentChat);
        this.fragmentView = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.ChannelAdminLogActivity.4
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                if (messageObject != null && messageObject.isRoundVideo() && messageObject.eventId != 0 && messageObject.getDialogId() == (-ChannelAdminLogActivity.this.currentChat.id)) {
                    MediaController.getInstance().setTextureView(ChannelAdminLogActivity.this.createTextureView(false), ChannelAdminLogActivity.this.aspectRatioFrameLayout, ChannelAdminLogActivity.this.roundVideoContainer, true);
                }
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (child == ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.parentLayout != null) {
                    ChannelAdminLogActivity.this.parentLayout.drawHeaderShadow(canvas, ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() : 0);
                }
                return result;
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
            protected boolean isActionBarVisible() {
                return ChannelAdminLogActivity.this.actionBar.getVisibility() == 0;
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                int heightSize2 = heightSize - getPaddingTop();
                measureChildWithMargins(ChannelAdminLogActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int actionBarHeight = ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                if (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
                    heightSize2 -= actionBarHeight;
                }
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (child != null && child.getVisibility() != 8 && child != ChannelAdminLogActivity.this.actionBar) {
                        if (child != ChannelAdminLogActivity.this.chatListView && child != ChannelAdminLogActivity.this.progressView) {
                            if (child == ChannelAdminLogActivity.this.emptyViewContainer) {
                                int contentWidthSpec = View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED);
                                int contentHeightSpec = View.MeasureSpec.makeMeasureSpec(heightSize2, C.BUFFER_FLAG_ENCRYPTED);
                                child.measure(contentWidthSpec, contentHeightSpec);
                            } else {
                                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                            }
                        } else {
                            int contentWidthSpec2 = View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED);
                            int contentHeightSpec2 = View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), heightSize2 - AndroidUtilities.dp(50.0f)), C.BUFFER_FLAG_ENCRYPTED);
                            child.measure(contentWidthSpec2, contentHeightSpec2);
                        }
                    }
                }
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                int childLeft;
                int childTop;
                int count = getChildCount();
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
                                childTop = ((((b - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop();
                                if (child != ChannelAdminLogActivity.this.actionBar && ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
                                    childTop += ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                                    break;
                                }
                                break;
                            case UndoView.ACTION_EMAIL_COPIED /* 80 */:
                                childTop = ((b - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (child == ChannelAdminLogActivity.this.emptyViewContainer) {
                            childTop -= AndroidUtilities.dp(24.0f) - (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0 ? ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() / 2 : 0);
                        } else if (child == ChannelAdminLogActivity.this.actionBar) {
                            childTop -= getPaddingTop();
                        } else if (child == this.backgroundView) {
                            childTop = 0;
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                ChannelAdminLogActivity.this.updateMessagesVisisblePart();
                notifyHeightChanged();
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (AvatarPreviewer.hasVisibleInstance()) {
                    AvatarPreviewer.getInstance().onTouchEvent(ev);
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }
        };
        SizeNotifierFrameLayout sizeNotifierFrameLayout = (SizeNotifierFrameLayout) this.fragmentView;
        this.contentView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
        FrameLayout frameLayout = new FrameLayout(context);
        this.emptyViewContainer = frameLayout;
        frameLayout.setVisibility(4);
        this.contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        this.emptyViewContainer.setOnTouchListener(ChannelAdminLogActivity$$ExternalSyntheticLambda8.INSTANCE);
        TextView textView = new TextView(context);
        this.emptyView = textView;
        textView.setTextSize(1, 14.0f);
        this.emptyView.setGravity(17);
        this.emptyView.setTextColor(Theme.getColor(Theme.key_chat_serviceText));
        this.emptyView.setBackground(Theme.createServiceDrawable(AndroidUtilities.dp(6.0f), this.emptyView, this.contentView));
        this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        this.emptyViewContainer.addView(this.emptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 16.0f, 0.0f, 16.0f, 0.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.ChannelAdminLogActivity.5
            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                ChatMessageCell chatMessageCell;
                ImageReceiver imageReceiver;
                int cellBottom;
                int p;
                boolean result = super.drawChild(canvas, child, drawingTime);
                if ((child instanceof ChatMessageCell) && (imageReceiver = (chatMessageCell = (ChatMessageCell) child).getAvatarImage()) != null) {
                    if (chatMessageCell.getMessageObject().deleted) {
                        imageReceiver.setVisible(false, false);
                        return result;
                    }
                    int top = (int) child.getY();
                    if (chatMessageCell.drawPinnedBottom()) {
                        int p2 = ChannelAdminLogActivity.this.chatListView.getChildViewHolder(child).getAdapterPosition();
                        if (p2 >= 0) {
                            int nextPosition = p2 + 1;
                            if (ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(nextPosition) != null) {
                                imageReceiver.setVisible(false, false);
                                return result;
                            }
                        }
                    }
                    float tx = chatMessageCell.getSlidingOffsetX() + chatMessageCell.getCheckBoxTranslation();
                    int y = ((int) child.getY()) + chatMessageCell.getLayoutHeight();
                    int maxY = ChannelAdminLogActivity.this.chatListView.getMeasuredHeight() - ChannelAdminLogActivity.this.chatListView.getPaddingBottom();
                    if (y > maxY) {
                        y = maxY;
                    }
                    if (chatMessageCell.drawPinnedTop() && (p = ChannelAdminLogActivity.this.chatListView.getChildViewHolder(child).getAdapterPosition()) >= 0) {
                        int tries = 0;
                        while (tries < 20) {
                            tries++;
                            int prevPosition = p - 1;
                            RecyclerView.ViewHolder holder = ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(prevPosition);
                            if (holder == null) {
                                break;
                            }
                            top = holder.itemView.getTop();
                            if (!(holder.itemView instanceof ChatMessageCell)) {
                                break;
                            }
                            chatMessageCell = (ChatMessageCell) holder.itemView;
                            if (!chatMessageCell.drawPinnedTop()) {
                                break;
                            }
                            p = prevPosition;
                        }
                    }
                    if (y - AndroidUtilities.dp(48.0f) < top) {
                        y = top + AndroidUtilities.dp(48.0f);
                    }
                    if (!chatMessageCell.drawPinnedBottom() && y > (cellBottom = (int) (chatMessageCell.getY() + chatMessageCell.getMeasuredHeight()))) {
                        y = cellBottom;
                    }
                    canvas.save();
                    if (tx != 0.0f) {
                        canvas.translate(tx, 0.0f);
                    }
                    if (chatMessageCell.getCurrentMessagesGroup() != null && chatMessageCell.getCurrentMessagesGroup().transitionParams.backgroundChangeBounds) {
                        y = (int) (y - chatMessageCell.getTranslationY());
                    }
                    imageReceiver.setImageY(y - AndroidUtilities.dp(44.0f));
                    if (chatMessageCell.shouldDrawAlphaLayer()) {
                        imageReceiver.setAlpha(chatMessageCell.getAlpha());
                        canvas.scale(chatMessageCell.getScaleX(), chatMessageCell.getScaleY(), chatMessageCell.getX() + chatMessageCell.getPivotX(), chatMessageCell.getY() + (chatMessageCell.getHeight() >> 1));
                    } else {
                        imageReceiver.setAlpha(1.0f);
                    }
                    imageReceiver.setVisible(true, false);
                    imageReceiver.draw(canvas);
                    canvas.restore();
                }
                return result;
            }
        };
        this.chatListView = recyclerListView;
        recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ChannelAdminLogActivity.this.m1707lambda$createView$3$orgtelegramuiChannelAdminLogActivity(view, i);
            }
        });
        this.chatListView.setTag(1);
        this.chatListView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView2 = this.chatListView;
        ChatActivityAdapter chatActivityAdapter = new ChatActivityAdapter(context);
        this.chatAdapter = chatActivityAdapter;
        recyclerListView2.setAdapter(chatActivityAdapter);
        this.chatListView.setClipToPadding(false);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(3.0f));
        RecyclerListView recyclerListView3 = this.chatListView;
        AnonymousClass6 anonymousClass6 = new AnonymousClass6(null, this.chatListView, null);
        this.chatListItemAnimator = anonymousClass6;
        recyclerListView3.setItemAnimator(anonymousClass6);
        this.chatListItemAnimator.setReversePositions(true);
        this.chatListView.setLayoutAnimation(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) { // from class: org.telegram.ui.ChannelAdminLogActivity.7
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScrollerCustom linearSmoothScroller = new LinearSmoothScrollerCustom(recyclerView.getContext(), 0);
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.chatLayoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(1);
        this.chatLayoutManager.setStackFromEnd(true);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        this.contentView.addView(this.chatListView, LayoutHelper.createFrame(-1, -1.0f));
        this.chatListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ChannelAdminLogActivity.8
            private float totalDy = 0.0f;
            private final int scrollValue = AndroidUtilities.dp(100.0f);

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    ChannelAdminLogActivity.this.scrollingFloatingDate = true;
                    ChannelAdminLogActivity.this.checkTextureViewPosition = true;
                } else if (newState == 0) {
                    ChannelAdminLogActivity.this.scrollingFloatingDate = false;
                    ChannelAdminLogActivity.this.checkTextureViewPosition = false;
                    ChannelAdminLogActivity.this.hideFloatingDateView(true);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ChannelAdminLogActivity.this.chatListView.invalidate();
                if (dy != 0 && ChannelAdminLogActivity.this.scrollingFloatingDate && !ChannelAdminLogActivity.this.currentFloatingTopIsNotMessage && ChannelAdminLogActivity.this.floatingDateView.getTag() == null) {
                    if (ChannelAdminLogActivity.this.floatingDateAnimation != null) {
                        ChannelAdminLogActivity.this.floatingDateAnimation.cancel();
                    }
                    ChannelAdminLogActivity.this.floatingDateView.setTag(1);
                    ChannelAdminLogActivity.this.floatingDateAnimation = new AnimatorSet();
                    ChannelAdminLogActivity.this.floatingDateAnimation.setDuration(150L);
                    ChannelAdminLogActivity.this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(ChannelAdminLogActivity.this.floatingDateView, "alpha", 1.0f));
                    ChannelAdminLogActivity.this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChannelAdminLogActivity.8.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (animation.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                                ChannelAdminLogActivity.this.floatingDateAnimation = null;
                            }
                        }
                    });
                    ChannelAdminLogActivity.this.floatingDateAnimation.start();
                }
                ChannelAdminLogActivity.this.checkScrollForLoad(true);
                ChannelAdminLogActivity.this.updateMessagesVisisblePart();
            }
        });
        int i = this.scrollToPositionOnRecreate;
        if (i != -1) {
            this.chatLayoutManager.scrollToPositionWithOffset(i, this.scrollToOffsetOnRecreate);
            this.scrollToPositionOnRecreate = -1;
        }
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.progressView = frameLayout2;
        frameLayout2.setVisibility(4);
        this.contentView.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        View view = new View(context);
        this.progressView2 = view;
        view.setBackground(Theme.createServiceDrawable(AndroidUtilities.dp(18.0f), this.progressView2, this.contentView));
        this.progressView.addView(this.progressView2, LayoutHelper.createFrame(36, 36, 17));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(28.0f));
        this.progressBar.setProgressColor(Theme.getColor(Theme.key_chat_serviceText));
        this.progressView.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
        ChatActionCell chatActionCell = new ChatActionCell(context);
        this.floatingDateView = chatActionCell;
        chatActionCell.setAlpha(0.0f);
        this.floatingDateView.setImportantForAccessibility(2);
        this.contentView.addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        this.contentView.addView(this.actionBar);
        FrameLayout frameLayout3 = new FrameLayout(context) { // from class: org.telegram.ui.ChannelAdminLogActivity.9
            @Override // android.view.View
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, bottom, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.bottomOverlayChat = frameLayout3;
        frameLayout3.setWillNotDraw(false);
        this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
        this.bottomOverlayChat.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChannelAdminLogActivity.this.m1709lambda$createView$5$orgtelegramuiChannelAdminLogActivity(view2);
            }
        });
        TextView textView2 = new TextView(context);
        this.bottomOverlayChatText = textView2;
        textView2.setTextSize(1, 15.0f);
        this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.bottomOverlayChatText.setTextColor(Theme.getColor(Theme.key_chat_fieldOverlayText));
        this.bottomOverlayChatText.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS).toUpperCase());
        this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
        ImageView imageView = new ImageView(context);
        this.bottomOverlayImage = imageView;
        imageView.setImageResource(R.drawable.msg_help);
        this.bottomOverlayImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_fieldOverlayText), PorterDuff.Mode.MULTIPLY));
        this.bottomOverlayImage.setScaleType(ImageView.ScaleType.CENTER);
        this.bottomOverlayChat.addView(this.bottomOverlayImage, LayoutHelper.createFrame(48, 48.0f, 53, 3.0f, 0.0f, 0.0f, 0.0f));
        this.bottomOverlayImage.setContentDescription(LocaleController.getString("BotHelp", R.string.BotHelp));
        this.bottomOverlayImage.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChannelAdminLogActivity.this.m1710lambda$createView$6$orgtelegramuiChannelAdminLogActivity(view2);
            }
        });
        FrameLayout frameLayout4 = new FrameLayout(context) { // from class: org.telegram.ui.ChannelAdminLogActivity.10
            @Override // android.view.View
            public void onDraw(Canvas canvas) {
                int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                Theme.chat_composeShadowDrawable.draw(canvas);
                canvas.drawRect(0.0f, bottom, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
            }
        };
        this.searchContainer = frameLayout4;
        frameLayout4.setWillNotDraw(false);
        this.searchContainer.setVisibility(4);
        this.searchContainer.setFocusable(true);
        this.searchContainer.setFocusableInTouchMode(true);
        this.searchContainer.setClickable(true);
        this.searchContainer.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        this.contentView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
        ImageView imageView2 = new ImageView(context);
        this.searchCalendarButton = imageView2;
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        this.searchCalendarButton.setImageResource(R.drawable.msg_calendar);
        this.searchCalendarButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_searchPanelIcons), PorterDuff.Mode.MULTIPLY));
        this.searchContainer.addView(this.searchCalendarButton, LayoutHelper.createFrame(48, 48, 53));
        this.searchCalendarButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                ChannelAdminLogActivity.this.m1712lambda$createView$8$orgtelegramuiChannelAdminLogActivity(view2);
            }
        });
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.searchCountText = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_chat_searchPanelText));
        this.searchCountText.setTextSize(15);
        this.searchCountText.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-1, -2.0f, 19, 108.0f, 0.0f, 0.0f, 0.0f));
        this.chatAdapter.updateRows();
        if (!this.loading || !this.messages.isEmpty()) {
            AndroidUtilities.updateViewVisibilityAnimated(this.progressView, false, 0.3f, true);
            this.chatListView.setEmptyView(this.emptyViewContainer);
        } else {
            AndroidUtilities.updateViewVisibilityAnimated(this.progressView, true, 0.3f, true);
            this.chatListView.setEmptyView(null);
        }
        this.chatListView.setAnimateEmptyView(true, 1);
        UndoView undoView = new UndoView(context);
        this.undoView = undoView;
        undoView.setAdditionalTranslationY(AndroidUtilities.dp(51.0f));
        this.contentView.addView(this.undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateEmptyPlaceholder();
        return this.fragmentView;
    }

    public static /* synthetic */ boolean lambda$createView$2(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ChannelAdminLogActivity */
    public /* synthetic */ void m1707lambda$createView$3$orgtelegramuiChannelAdminLogActivity(View view, int position) {
        createMenu(view);
    }

    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$6 */
    /* loaded from: classes4.dex */
    public class AnonymousClass6 extends ChatListItemAnimator {
        Runnable finishRunnable;
        int scrollAnimationIndex = -1;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass6(ChatActivity activity, RecyclerListView listView, Theme.ResourcesProvider resourcesProvider) {
            super(activity, listView, resourcesProvider);
            ChannelAdminLogActivity.this = this$0;
        }

        @Override // androidx.recyclerview.widget.ChatListItemAnimator
        public void onAnimationStart() {
            if (this.scrollAnimationIndex == -1) {
                this.scrollAnimationIndex = ChannelAdminLogActivity.this.getNotificationCenter().setAnimationInProgress(this.scrollAnimationIndex, null, false);
            }
            Runnable runnable = this.finishRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.finishRunnable = null;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("admin logs chatItemAnimator disable notifications");
            }
        }

        @Override // androidx.recyclerview.widget.ChatListItemAnimator, androidx.recyclerview.widget.DefaultItemAnimator
        public void onAllAnimationsDone() {
            super.onAllAnimationsDone();
            Runnable runnable = this.finishRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$6$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChannelAdminLogActivity.AnonymousClass6.this.m1718xa4fb96a1();
                }
            };
            this.finishRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2);
        }

        /* renamed from: lambda$onAllAnimationsDone$0$org-telegram-ui-ChannelAdminLogActivity$6 */
        public /* synthetic */ void m1718xa4fb96a1() {
            if (this.scrollAnimationIndex != -1) {
                ChannelAdminLogActivity.this.getNotificationCenter().onAnimationFinish(this.scrollAnimationIndex);
                this.scrollAnimationIndex = -1;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("admin logs chatItemAnimator enable notifications");
            }
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ChannelAdminLogActivity */
    public /* synthetic */ void m1709lambda$createView$5$orgtelegramuiChannelAdminLogActivity(View view) {
        if (getParentActivity() == null) {
            return;
        }
        AdminLogFilterAlert adminLogFilterAlert = new AdminLogFilterAlert(getParentActivity(), this.currentFilter, this.selectedAdmins, this.currentChat.megagroup);
        adminLogFilterAlert.setCurrentAdmins(this.admins);
        adminLogFilterAlert.setAdminLogFilterAlertDelegate(new AdminLogFilterAlert.AdminLogFilterAlertDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.AdminLogFilterAlert.AdminLogFilterAlertDelegate
            public final void didSelectRights(TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, LongSparseArray longSparseArray) {
                ChannelAdminLogActivity.this.m1708lambda$createView$4$orgtelegramuiChannelAdminLogActivity(tL_channelAdminLogEventsFilter, longSparseArray);
            }
        });
        showDialog(adminLogFilterAlert);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ChannelAdminLogActivity */
    public /* synthetic */ void m1708lambda$createView$4$orgtelegramuiChannelAdminLogActivity(TLRPC.TL_channelAdminLogEventsFilter filter, LongSparseArray admins) {
        this.currentFilter = filter;
        this.selectedAdmins = admins;
        if (filter != null || admins != null) {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogSelectedEvents", R.string.EventLogSelectedEvents));
        } else {
            this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", R.string.EventLogAllEvents));
        }
        loadMessages(true);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ChannelAdminLogActivity */
    public /* synthetic */ void m1710lambda$createView$6$orgtelegramuiChannelAdminLogActivity(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (this.currentChat.megagroup) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetail", R.string.EventLogInfoDetail)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetailChannel", R.string.EventLogInfoDetailChannel)));
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        builder.setTitle(LocaleController.getString("EventLogInfoTitle", R.string.EventLogInfoTitle));
        showDialog(builder.create());
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-ChannelAdminLogActivity */
    public /* synthetic */ void m1712lambda$createView$8$orgtelegramuiChannelAdminLogActivity(View view) {
        if (getParentActivity() == null) {
            return;
        }
        AndroidUtilities.hideKeyboard(this.searchItem.getSearchField());
        showDialog(AlertsCreator.createCalendarPickerDialog(getParentActivity(), 1375315200000L, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda11
            @Override // org.telegram.messenger.MessagesStorage.IntCallback
            public final void run(int i) {
                ChannelAdminLogActivity.this.m1711lambda$createView$7$orgtelegramuiChannelAdminLogActivity(i);
            }
        }, null).create());
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-ChannelAdminLogActivity */
    public /* synthetic */ void m1711lambda$createView$7$orgtelegramuiChannelAdminLogActivity(int param) {
        loadMessages(true);
    }

    public boolean createMenu(View v) {
        MessageObject message;
        if (v instanceof ChatMessageCell) {
            message = ((ChatMessageCell) v).getMessageObject();
        } else if (!(v instanceof ChatActionCell)) {
            message = null;
        } else {
            message = ((ChatActionCell) v).getMessageObject();
        }
        if (message == null) {
            return false;
        }
        int type = getMessageType(message);
        this.selectedObject = message;
        if (getParentActivity() == null) {
            return false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        ArrayList<CharSequence> items = new ArrayList<>();
        final ArrayList<Integer> options = new ArrayList<>();
        if (this.selectedObject.type == 0 || this.selectedObject.caption != null) {
            items.add(LocaleController.getString("Copy", R.string.Copy));
            options.add(3);
        }
        if (type == 1) {
            if (this.selectedObject.currentEvent != null && (this.selectedObject.currentEvent.action instanceof TLRPC.TL_channelAdminLogEventActionChangeStickerSet)) {
                TLRPC.TL_channelAdminLogEventActionChangeStickerSet action = (TLRPC.TL_channelAdminLogEventActionChangeStickerSet) this.selectedObject.currentEvent.action;
                TLRPC.InputStickerSet stickerSet = action.new_stickerset;
                TLRPC.InputStickerSet stickerSet2 = (stickerSet == null || (stickerSet instanceof TLRPC.TL_inputStickerSetEmpty)) ? action.prev_stickerset : stickerSet;
                if (stickerSet2 != null) {
                    showDialog(new StickersAlert(getParentActivity(), this, stickerSet2, (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null));
                    return true;
                }
            } else if (this.selectedObject.currentEvent != null && (this.selectedObject.currentEvent.action instanceof TLRPC.TL_channelAdminLogEventActionChangeHistoryTTL) && ChatObject.canUserDoAdminAction(this.currentChat, 13)) {
                ClearHistoryAlert alert = new ClearHistoryAlert(getParentActivity(), null, this.currentChat, false, null);
                alert.setDelegate(new ClearHistoryAlert.ClearHistoryAlertDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity.11
                    @Override // org.telegram.ui.Components.ClearHistoryAlert.ClearHistoryAlertDelegate
                    public /* synthetic */ void onClearHistory(boolean z) {
                        ClearHistoryAlert.ClearHistoryAlertDelegate.CC.$default$onClearHistory(this, z);
                    }

                    @Override // org.telegram.ui.Components.ClearHistoryAlert.ClearHistoryAlertDelegate
                    public void onAutoDeleteHistory(int ttl, int action2) {
                        ChannelAdminLogActivity.this.getMessagesController().setDialogHistoryTTL(-ChannelAdminLogActivity.this.currentChat.id, ttl);
                        TLRPC.ChatFull chatInfo = ChannelAdminLogActivity.this.getMessagesController().getChatFull(ChannelAdminLogActivity.this.currentChat.id);
                        if (chatInfo != null) {
                            ChannelAdminLogActivity.this.undoView.showWithAction(-ChannelAdminLogActivity.this.currentChat.id, action2, (Object) null, Integer.valueOf(chatInfo.ttl_period), (Runnable) null, (Runnable) null);
                        }
                    }
                });
                showDialog(alert);
            }
        } else if (type == 3) {
            if ((this.selectedObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && MessageObject.isNewGifDocument(this.selectedObject.messageOwner.media.webpage.document)) {
                items.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                options.add(11);
            }
        } else if (type == 4) {
            if (this.selectedObject.isVideo()) {
                items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                options.add(4);
                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                options.add(6);
            } else if (this.selectedObject.isMusic()) {
                items.add(LocaleController.getString("SaveToMusic", R.string.SaveToMusic));
                options.add(10);
                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                options.add(6);
            } else if (this.selectedObject.getDocument() != null) {
                if (MessageObject.isNewGifDocument(this.selectedObject.getDocument())) {
                    items.add(LocaleController.getString("SaveToGIFs", R.string.SaveToGIFs));
                    options.add(11);
                }
                items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
                options.add(10);
                items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
                options.add(6);
            } else {
                items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                options.add(4);
            }
        } else if (type == 5) {
            items.add(LocaleController.getString("ApplyLocalizationFile", R.string.ApplyLocalizationFile));
            options.add(5);
            items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
            options.add(10);
            items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
            options.add(6);
        } else if (type == 10) {
            items.add(LocaleController.getString("ApplyThemeFile", R.string.ApplyThemeFile));
            options.add(5);
            items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
            options.add(10);
            items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
            options.add(6);
        } else if (type == 6) {
            items.add(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
            options.add(7);
            items.add(LocaleController.getString("SaveToDownloads", R.string.SaveToDownloads));
            options.add(10);
            items.add(LocaleController.getString("ShareFile", R.string.ShareFile));
            options.add(6);
        } else if (type == 7) {
            if (this.selectedObject.isMask()) {
                items.add(LocaleController.getString("AddToMasks", R.string.AddToMasks));
            } else {
                items.add(LocaleController.getString("AddToStickers", R.string.AddToStickers));
            }
            options.add(9);
        } else if (type == 8) {
            long uid = this.selectedObject.messageOwner.media.user_id;
            TLRPC.User user = null;
            if (uid != 0) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(uid));
            }
            if (user != null && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId() && ContactsController.getInstance(this.currentAccount).contactsDict.get(Long.valueOf(user.id)) == null) {
                items.add(LocaleController.getString("AddContactTitle", R.string.AddContactTitle));
                options.add(15);
            }
            if (!TextUtils.isEmpty(this.selectedObject.messageOwner.media.phone_number)) {
                items.add(LocaleController.getString("Copy", R.string.Copy));
                options.add(16);
                items.add(LocaleController.getString("Call", R.string.Call));
                options.add(17);
            }
        }
        if (options.isEmpty()) {
            return false;
        }
        CharSequence[] finalItems = (CharSequence[]) items.toArray(new CharSequence[0]);
        builder.setItems(finalItems, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda4
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelAdminLogActivity.this.m1706lambda$createMenu$9$orgtelegramuiChannelAdminLogActivity(options, dialogInterface, i);
            }
        });
        builder.setTitle(LocaleController.getString("Message", R.string.Message));
        showDialog(builder.create());
        return true;
    }

    /* renamed from: lambda$createMenu$9$org-telegram-ui-ChannelAdminLogActivity */
    public /* synthetic */ void m1706lambda$createMenu$9$orgtelegramuiChannelAdminLogActivity(ArrayList options, DialogInterface dialogInterface, int i) {
        if (this.selectedObject == null || i < 0 || i >= options.size()) {
            return;
        }
        processSelectedOption(((Integer) options.get(i)).intValue());
    }

    private String getMessageContent(MessageObject messageObject, int previousUid, boolean name) {
        TLRPC.Chat chat;
        String str = "";
        if (name) {
            long fromId = messageObject.getFromChatId();
            if (previousUid != fromId) {
                if (fromId > 0) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(fromId));
                    if (user != null) {
                        str = ContactsController.formatName(user.first_name, user.last_name) + ":\n";
                    }
                } else if (fromId < 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-fromId))) != null) {
                    str = chat.title + ":\n";
                }
            }
        }
        if (messageObject.type == 0 && messageObject.messageOwner.message != null) {
            return str + messageObject.messageOwner.message;
        } else if (messageObject.messageOwner.media != null && messageObject.messageOwner.message != null) {
            return str + messageObject.messageOwner.message;
        } else {
            return str + ((Object) messageObject.messageText);
        }
    }

    public TextureView createTextureView(boolean add) {
        if (this.parentLayout == null) {
            return null;
        }
        if (this.roundVideoContainer == null) {
            if (Build.VERSION.SDK_INT >= 21) {
                FrameLayout frameLayout = new FrameLayout(getParentActivity()) { // from class: org.telegram.ui.ChannelAdminLogActivity.12
                    @Override // android.view.View
                    public void setTranslationY(float translationY) {
                        super.setTranslationY(translationY);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }
                };
                this.roundVideoContainer = frameLayout;
                frameLayout.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.ChannelAdminLogActivity.13
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
                    }
                });
                this.roundVideoContainer.setClipToOutline(true);
            } else {
                this.roundVideoContainer = new FrameLayout(getParentActivity()) { // from class: org.telegram.ui.ChannelAdminLogActivity.14
                    @Override // android.view.View
                    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                        super.onSizeChanged(w, h, oldw, oldh);
                        ChannelAdminLogActivity.this.aspectPath.reset();
                        ChannelAdminLogActivity.this.aspectPath.addCircle(w / 2, h / 2, w / 2, Path.Direction.CW);
                        ChannelAdminLogActivity.this.aspectPath.toggleInverseFillType();
                    }

                    @Override // android.view.View
                    public void setTranslationY(float translationY) {
                        super.setTranslationY(translationY);
                        ChannelAdminLogActivity.this.contentView.invalidate();
                    }

                    @Override // android.view.View
                    public void setVisibility(int visibility) {
                        super.setVisibility(visibility);
                        if (visibility == 0) {
                            setLayerType(2, null);
                        }
                    }

                    @Override // android.view.ViewGroup, android.view.View
                    protected void dispatchDraw(Canvas canvas) {
                        super.dispatchDraw(canvas);
                        canvas.drawPath(ChannelAdminLogActivity.this.aspectPath, ChannelAdminLogActivity.this.aspectPaint);
                    }
                };
                this.aspectPath = new Path();
                Paint paint = new Paint(1);
                this.aspectPaint = paint;
                paint.setColor(-16777216);
                this.aspectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            }
            this.roundVideoContainer.setWillNotDraw(false);
            this.roundVideoContainer.setVisibility(4);
            AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(getParentActivity());
            this.aspectRatioFrameLayout = aspectRatioFrameLayout;
            aspectRatioFrameLayout.setBackgroundColor(0);
            if (add) {
                this.roundVideoContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            }
            TextureView textureView = new TextureView(getParentActivity());
            this.videoTextureView = textureView;
            textureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        if (this.roundVideoContainer.getParent() == null) {
            this.contentView.addView(this.roundVideoContainer, 1, new FrameLayout.LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize));
        }
        this.roundVideoContainer.setVisibility(4);
        this.aspectRatioFrameLayout.setDrawingReady(false);
        return this.videoTextureView;
    }

    private void destroyTextureView() {
        FrameLayout frameLayout = this.roundVideoContainer;
        if (frameLayout == null || frameLayout.getParent() == null) {
            return;
        }
        this.contentView.removeView(this.roundVideoContainer);
        this.aspectRatioFrameLayout.setDrawingReady(false);
        this.roundVideoContainer.setVisibility(4);
        if (Build.VERSION.SDK_INT < 21) {
            this.roundVideoContainer.setLayerType(0, null);
        }
    }

    private void processSelectedOption(int option) {
        MessageObject messageObject = this.selectedObject;
        if (messageObject == null) {
            return;
        }
        int i = 3;
        int i2 = 0;
        switch (option) {
            case 3:
                AndroidUtilities.addToClipboard(getMessageContent(messageObject, 0, true));
                break;
            case 4:
                String path = messageObject.messageOwner.attachPath;
                if (path != null && path.length() > 0) {
                    File temp = new File(path);
                    if (!temp.exists()) {
                        path = null;
                    }
                }
                if (path == null || path.length() == 0) {
                    path = getFileLoader().getPathToMessage(this.selectedObject.messageOwner).toString();
                }
                if (this.selectedObject.type == 3 || this.selectedObject.type == 1) {
                    if (Build.VERSION.SDK_INT >= 23 && ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0)) {
                        getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                        this.selectedObject = null;
                        return;
                    }
                    Activity parentActivity = getParentActivity();
                    if (this.selectedObject.type == 3) {
                        i2 = 1;
                    }
                    MediaController.saveFile(path, parentActivity, i2, null, null);
                    break;
                }
                break;
            case 5:
                File locFile = null;
                if (messageObject.messageOwner.attachPath != null && this.selectedObject.messageOwner.attachPath.length() != 0) {
                    File f = new File(this.selectedObject.messageOwner.attachPath);
                    if (f.exists()) {
                        locFile = f;
                    }
                }
                if (locFile == null) {
                    File f2 = getFileLoader().getPathToMessage(this.selectedObject.messageOwner);
                    if (f2.exists()) {
                        locFile = f2;
                    }
                }
                if (locFile != null) {
                    if (locFile.getName().toLowerCase().endsWith("attheme")) {
                        LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
                        if (linearLayoutManager != null) {
                            int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                            if (lastPosition < this.chatLayoutManager.getItemCount() - 1) {
                                int findFirstVisibleItemPosition = this.chatLayoutManager.findFirstVisibleItemPosition();
                                this.scrollToPositionOnRecreate = findFirstVisibleItemPosition;
                                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.chatListView.findViewHolderForAdapterPosition(findFirstVisibleItemPosition);
                                if (holder != null) {
                                    this.scrollToOffsetOnRecreate = holder.itemView.getTop();
                                } else {
                                    this.scrollToPositionOnRecreate = -1;
                                }
                            } else {
                                this.scrollToPositionOnRecreate = -1;
                            }
                        }
                        Theme.ThemeInfo themeInfo = Theme.applyThemeFile(locFile, this.selectedObject.getDocumentName(), null, true);
                        if (themeInfo != null) {
                            presentFragment(new ThemePreviewActivity(themeInfo));
                            break;
                        } else {
                            this.scrollToPositionOnRecreate = -1;
                            if (getParentActivity() == null) {
                                this.selectedObject = null;
                                return;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setMessage(LocaleController.getString("IncorrectTheme", R.string.IncorrectTheme));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                            showDialog(builder.create());
                            break;
                        }
                    } else if (LocaleController.getInstance().applyLanguageFile(locFile, this.currentAccount)) {
                        presentFragment(new LanguageSelectActivity());
                        break;
                    } else if (getParentActivity() == null) {
                        this.selectedObject = null;
                        return;
                    } else {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                        builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder2.setMessage(LocaleController.getString("IncorrectLocalization", R.string.IncorrectLocalization));
                        builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        showDialog(builder2.create());
                        break;
                    }
                }
                break;
            case 6:
                String path2 = messageObject.messageOwner.attachPath;
                if (path2 != null && path2.length() > 0) {
                    File temp2 = new File(path2);
                    if (!temp2.exists()) {
                        path2 = null;
                    }
                }
                String path3 = (path2 == null || path2.length() == 0) ? getFileLoader().getPathToMessage(this.selectedObject.messageOwner).toString() : path2;
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType(this.selectedObject.getDocument().mime_type);
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getParentActivity(), "org.telegram.messenger.beta.provider", new File(path3)));
                        intent.setFlags(1);
                    } catch (Exception e) {
                        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path3)));
                    }
                } else {
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(path3)));
                }
                getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", R.string.ShareFile)), 500);
                break;
            case 7:
                String path4 = messageObject.messageOwner.attachPath;
                if (path4 != null && path4.length() > 0) {
                    File temp3 = new File(path4);
                    if (!temp3.exists()) {
                        path4 = null;
                    }
                }
                if (path4 == null || path4.length() == 0) {
                    path4 = getFileLoader().getPathToMessage(this.selectedObject.messageOwner).toString();
                }
                if (Build.VERSION.SDK_INT >= 23 && ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0)) {
                    getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    this.selectedObject = null;
                    return;
                }
                MediaController.saveFile(path4, getParentActivity(), 0, null, null);
                break;
                break;
            case 9:
                showDialog(new StickersAlert(getParentActivity(), this, this.selectedObject.getInputStickerSet(), (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null));
                break;
            case 10:
                if (Build.VERSION.SDK_INT >= 23 && ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0)) {
                    getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    this.selectedObject = null;
                    return;
                }
                String fileName = FileLoader.getDocumentFileName(this.selectedObject.getDocument());
                if (TextUtils.isEmpty(fileName)) {
                    fileName = this.selectedObject.getFileName();
                }
                String path5 = this.selectedObject.messageOwner.attachPath;
                if (path5 != null && path5.length() > 0) {
                    File temp4 = new File(path5);
                    if (!temp4.exists()) {
                        path5 = null;
                    }
                }
                if (path5 == null || path5.length() == 0) {
                    path5 = getFileLoader().getPathToMessage(this.selectedObject.messageOwner).toString();
                }
                Activity parentActivity2 = getParentActivity();
                if (!this.selectedObject.isMusic()) {
                    i = 2;
                }
                MediaController.saveFile(path5, parentActivity2, i, fileName, this.selectedObject.getDocument() != null ? this.selectedObject.getDocument().mime_type : "");
                break;
                break;
            case 11:
                TLRPC.Document document = messageObject.getDocument();
                MessagesController.getInstance(this.currentAccount).saveGif(this.selectedObject, document);
                break;
            case 15:
                Bundle args = new Bundle();
                args.putLong("user_id", this.selectedObject.messageOwner.media.user_id);
                args.putString("phone", this.selectedObject.messageOwner.media.phone_number);
                args.putBoolean("addContact", true);
                presentFragment(new ContactAddActivity(args));
                break;
            case 16:
                AndroidUtilities.addToClipboard(messageObject.messageOwner.media.phone_number);
                break;
            case 17:
                try {
                    Intent intent2 = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + this.selectedObject.messageOwner.media.phone_number));
                    intent2.addFlags(268435456);
                    getParentActivity().startActivityForResult(intent2, 500);
                    break;
                } catch (Exception e2) {
                    FileLog.e(e2);
                    break;
                }
        }
        this.selectedObject = null;
    }

    private int getMessageType(MessageObject messageObject) {
        String mime;
        if (messageObject == null || messageObject.type == 6) {
            return -1;
        }
        if (messageObject.type == 10 || messageObject.type == 11 || messageObject.type == 16) {
            if (messageObject.getId() == 0) {
                return -1;
            }
            return 1;
        } else if (messageObject.isVoice()) {
            return 2;
        } else {
            if (messageObject.isSticker() || messageObject.isAnimatedSticker()) {
                TLRPC.InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
                if (inputStickerSet instanceof TLRPC.TL_inputStickerSetID) {
                    if (!MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.id)) {
                        return 7;
                    }
                } else if ((inputStickerSet instanceof TLRPC.TL_inputStickerSetShortName) && !MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                    return 7;
                }
            } else if ((!messageObject.isRoundVideo() || (messageObject.isRoundVideo() && BuildVars.DEBUG_VERSION)) && ((messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo())) {
                boolean canSave = false;
                if (messageObject.messageOwner.attachPath != null && messageObject.messageOwner.attachPath.length() != 0) {
                    File f = new File(messageObject.messageOwner.attachPath);
                    if (f.exists()) {
                        canSave = true;
                    }
                }
                if (!canSave) {
                    File f2 = getFileLoader().getPathToMessage(messageObject.messageOwner);
                    if (f2.exists()) {
                        canSave = true;
                    }
                }
                if (canSave) {
                    if (messageObject.getDocument() != null && (mime = messageObject.getDocument().mime_type) != null) {
                        if (messageObject.getDocumentName().toLowerCase().endsWith("attheme")) {
                            return 10;
                        }
                        if (mime.endsWith("/xml")) {
                            return 5;
                        }
                        return (mime.endsWith("/png") || mime.endsWith("/jpg") || mime.endsWith("/jpeg")) ? 6 : 4;
                    }
                    return 4;
                }
            } else if (messageObject.type == 12) {
                return 8;
            } else {
                if (messageObject.isMediaEmpty()) {
                    return 3;
                }
            }
            return 2;
        }
    }

    private void loadAdmins() {
        TLRPC.TL_channels_getParticipants req = new TLRPC.TL_channels_getParticipants();
        req.channel = MessagesController.getInputChannel(this.currentChat);
        req.filter = new TLRPC.TL_channelParticipantsAdmins();
        req.offset = 0;
        req.limit = 200;
        int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda12
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelAdminLogActivity.this.m1714lambda$loadAdmins$11$orgtelegramuiChannelAdminLogActivity(tLObject, tL_error);
            }
        });
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
    }

    /* renamed from: lambda$loadAdmins$11$org-telegram-ui-ChannelAdminLogActivity */
    public /* synthetic */ void m1714lambda$loadAdmins$11$orgtelegramuiChannelAdminLogActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                ChannelAdminLogActivity.this.m1713lambda$loadAdmins$10$orgtelegramuiChannelAdminLogActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$loadAdmins$10$org-telegram-ui-ChannelAdminLogActivity */
    public /* synthetic */ void m1713lambda$loadAdmins$10$orgtelegramuiChannelAdminLogActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            TLRPC.TL_channels_channelParticipants res = (TLRPC.TL_channels_channelParticipants) response;
            getMessagesController().putUsers(res.users, false);
            getMessagesController().putChats(res.chats, false);
            this.admins = res.participants;
            if (this.visibleDialog instanceof AdminLogFilterAlert) {
                ((AdminLogFilterAlert) this.visibleDialog).setCurrentAdmins(this.admins);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRemoveFromParent() {
        MediaController.getInstance().setTextureView(this.videoTextureView, null, null, false);
    }

    public void hideFloatingDateView(boolean animated) {
        if (this.floatingDateView.getTag() == null || this.currentFloatingDateOnScreen) {
            return;
        }
        if (!this.scrollingFloatingDate || this.currentFloatingTopIsNotMessage) {
            this.floatingDateView.setTag(null);
            if (animated) {
                AnimatorSet animatorSet = new AnimatorSet();
                this.floatingDateAnimation = animatorSet;
                animatorSet.setDuration(150L);
                this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingDateView, "alpha", 0.0f));
                this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChannelAdminLogActivity.15
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                            ChannelAdminLogActivity.this.floatingDateAnimation = null;
                        }
                    }
                });
                this.floatingDateAnimation.setStartDelay(500L);
                this.floatingDateAnimation.start();
                return;
            }
            AnimatorSet animatorSet2 = this.floatingDateAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.floatingDateAnimation = null;
            }
            this.floatingDateView.setAlpha(0.0f);
        }
    }

    public void checkScrollForLoad(boolean scroll) {
        int checkLoadCount;
        LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
        if (linearLayoutManager == null || this.paused) {
            return;
        }
        int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
        int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
        if (visibleItemCount > 0) {
            this.chatAdapter.getItemCount();
            if (scroll) {
                checkLoadCount = 25;
            } else {
                checkLoadCount = 5;
            }
            if (firstVisibleItem <= checkLoadCount && !this.loading && !this.endReached) {
                loadMessages(false);
            }
        }
    }

    public void moveScrollToLastMessage() {
        if (this.chatListView != null && !this.messages.isEmpty()) {
            this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, (-100000) - this.chatListView.getPaddingTop());
        }
    }

    private void updateTextureViewPosition() {
        boolean foundTextureViewMessage = false;
        int count = this.chatListView.getChildCount();
        int a = 0;
        while (true) {
            if (a >= count) {
                break;
            }
            View view = this.chatListView.getChildAt(a);
            if (view instanceof ChatMessageCell) {
                ChatMessageCell messageCell = (ChatMessageCell) view;
                MessageObject messageObject = messageCell.getMessageObject();
                if (this.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                    ImageReceiver imageReceiver = messageCell.getPhotoImage();
                    this.roundVideoContainer.setTranslationX(imageReceiver.getImageX());
                    this.roundVideoContainer.setTranslationY(this.fragmentView.getPaddingTop() + messageCell.getTop() + imageReceiver.getImageY());
                    this.fragmentView.invalidate();
                    this.roundVideoContainer.invalidate();
                    foundTextureViewMessage = true;
                    break;
                }
            }
            a++;
        }
        if (this.roundVideoContainer != null) {
            MessageObject messageObject2 = MediaController.getInstance().getPlayingMessageObject();
            if (!foundTextureViewMessage) {
                this.roundVideoContainer.setTranslationY((-AndroidUtilities.roundMessageSize) - 100);
                this.fragmentView.invalidate();
                if (messageObject2 != null && messageObject2.isRoundVideo()) {
                    if (this.checkTextureViewPosition || PipRoundVideoView.getInstance() != null) {
                        MediaController.getInstance().setCurrentVideoVisible(false);
                        return;
                    }
                    return;
                }
                return;
            }
            MediaController.getInstance().setCurrentVideoVisible(true);
        }
    }

    public void updateMessagesVisisblePart() {
        boolean z;
        int offset;
        float f;
        MessageObject messageObject;
        int height;
        int count;
        int viewBottom;
        RecyclerListView recyclerListView = this.chatListView;
        if (recyclerListView == null) {
            return;
        }
        int count2 = recyclerListView.getChildCount();
        int height2 = this.chatListView.getMeasuredHeight();
        int minPositionHolder = Integer.MAX_VALUE;
        int minPositionDateHolder = Integer.MAX_VALUE;
        View minDateChild = null;
        View minChild = null;
        View minMessageChild = null;
        boolean foundTextureViewMessage = false;
        int a = 0;
        while (true) {
            z = false;
            int viewTop = 0;
            z = false;
            if (a >= count2) {
                break;
            }
            View view = this.chatListView.getChildAt(a);
            if (view instanceof ChatMessageCell) {
                ChatMessageCell messageCell = (ChatMessageCell) view;
                int top = messageCell.getTop();
                messageCell.getBottom();
                if (top < 0) {
                    viewTop = -top;
                }
                int viewBottom2 = messageCell.getMeasuredHeight();
                if (viewBottom2 <= height2) {
                    viewBottom = viewBottom2;
                } else {
                    viewBottom = viewTop + height2;
                }
                messageCell.setVisiblePart(viewTop, viewBottom - viewTop, (this.contentView.getHeightWithKeyboard() - AndroidUtilities.dp(48.0f)) - this.chatListView.getTop(), 0.0f, (view.getY() + this.actionBar.getMeasuredHeight()) - this.contentView.getBackgroundTranslationY(), this.contentView.getMeasuredWidth(), this.contentView.getBackgroundSizeY(), 0, 0);
                MessageObject messageObject2 = messageCell.getMessageObject();
                if (this.roundVideoContainer == null || !messageObject2.isRoundVideo() || !MediaController.getInstance().isPlayingMessage(messageObject2)) {
                    count = count2;
                    height = height2;
                } else {
                    ImageReceiver imageReceiver = messageCell.getPhotoImage();
                    count = count2;
                    height = height2;
                    this.roundVideoContainer.setTranslationX(imageReceiver.getImageX());
                    this.roundVideoContainer.setTranslationY(this.fragmentView.getPaddingTop() + top + imageReceiver.getImageY());
                    this.fragmentView.invalidate();
                    this.roundVideoContainer.invalidate();
                    foundTextureViewMessage = true;
                }
            } else {
                count = count2;
                height = height2;
                if (view instanceof ChatActionCell) {
                    ChatActionCell cell = (ChatActionCell) view;
                    cell.setVisiblePart((view.getY() + this.actionBar.getMeasuredHeight()) - this.contentView.getBackgroundTranslationY(), this.contentView.getBackgroundSizeY());
                }
            }
            if (view.getBottom() > this.chatListView.getPaddingTop()) {
                int position = view.getBottom();
                if (position < minPositionHolder) {
                    minPositionHolder = position;
                    if ((view instanceof ChatMessageCell) || (view instanceof ChatActionCell)) {
                        minMessageChild = view;
                    }
                    minChild = view;
                }
                ChatListItemAnimator chatListItemAnimator = this.chatListItemAnimator;
                if ((chatListItemAnimator == null || (!chatListItemAnimator.willRemoved(view) && !this.chatListItemAnimator.willAddedFromAlpha(view))) && (view instanceof ChatActionCell) && ((ChatActionCell) view).getMessageObject().isDateObject) {
                    if (view.getAlpha() != 1.0f) {
                        view.setAlpha(1.0f);
                    }
                    if (position < minPositionDateHolder) {
                        minDateChild = view;
                        minPositionDateHolder = position;
                    }
                }
            }
            a++;
            count2 = count;
            height2 = height;
        }
        FrameLayout frameLayout = this.roundVideoContainer;
        if (frameLayout != null) {
            if (!foundTextureViewMessage) {
                frameLayout.setTranslationY((-AndroidUtilities.roundMessageSize) - 100);
                this.fragmentView.invalidate();
                MessageObject messageObject3 = MediaController.getInstance().getPlayingMessageObject();
                if (messageObject3 != null && messageObject3.isRoundVideo() && this.checkTextureViewPosition) {
                    MediaController.getInstance().setCurrentVideoVisible(false);
                }
            } else {
                MediaController.getInstance().setCurrentVideoVisible(true);
            }
        }
        if (minMessageChild != null) {
            if (minMessageChild instanceof ChatMessageCell) {
                messageObject = ((ChatMessageCell) minMessageChild).getMessageObject();
            } else {
                messageObject = ((ChatActionCell) minMessageChild).getMessageObject();
            }
            this.floatingDateView.setCustomDate(messageObject.messageOwner.date, false, true);
        }
        this.currentFloatingDateOnScreen = false;
        if (!(minChild instanceof ChatMessageCell) && !(minChild instanceof ChatActionCell)) {
            z = true;
        }
        this.currentFloatingTopIsNotMessage = z;
        if (minDateChild != null) {
            if (minDateChild.getTop() <= this.chatListView.getPaddingTop()) {
                if (!this.currentFloatingTopIsNotMessage) {
                    if (minDateChild.getAlpha() != 0.0f) {
                        minDateChild.setAlpha(0.0f);
                    }
                    AnimatorSet animatorSet = this.floatingDateAnimation;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                        this.floatingDateAnimation = null;
                    }
                    if (this.floatingDateView.getTag() == null) {
                        this.floatingDateView.setTag(1);
                    }
                    if (this.floatingDateView.getAlpha() != 1.0f) {
                        this.floatingDateView.setAlpha(1.0f);
                    }
                    this.currentFloatingDateOnScreen = true;
                    offset = minDateChild.getBottom() - this.chatListView.getPaddingTop();
                    if (offset > this.floatingDateView.getMeasuredHeight() || offset >= this.floatingDateView.getMeasuredHeight() * 2) {
                        this.floatingDateView.setTranslationY(0.0f);
                        return;
                    }
                    ChatActionCell chatActionCell = this.floatingDateView;
                    chatActionCell.setTranslationY(((-chatActionCell.getMeasuredHeight()) * 2) + offset);
                    return;
                }
                f = 1.0f;
            } else {
                f = 1.0f;
            }
            if (minDateChild.getAlpha() != f) {
                minDateChild.setAlpha(f);
            }
            hideFloatingDateView(true ^ this.currentFloatingTopIsNotMessage);
            offset = minDateChild.getBottom() - this.chatListView.getPaddingTop();
            if (offset > this.floatingDateView.getMeasuredHeight()) {
            }
            this.floatingDateView.setTranslationY(0.0f);
            return;
        }
        hideFloatingDateView(true);
        this.floatingDateView.setTranslationY(0.0f);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        if (isOpen) {
            this.allowAnimationIndex = getNotificationCenter().setAnimationInProgress(this.allowAnimationIndex, new int[]{NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoad, NotificationCenter.botKeyboardDidLoad});
            this.openAnimationEnded = false;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            getNotificationCenter().onAnimationFinish(this.allowAnimationIndex);
            this.openAnimationEnded = true;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onResume();
        }
        this.paused = false;
        checkScrollForLoad(false);
        if (this.wasPaused) {
            this.wasPaused = false;
            ChatActivityAdapter chatActivityAdapter = this.chatAdapter;
            if (chatActivityAdapter != null) {
                chatActivityAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onPause();
        }
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        this.paused = true;
        this.wasPaused = true;
        if (AvatarPreviewer.hasVisibleInstance()) {
            AvatarPreviewer.getInstance().close();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    public void openVCard(TLRPC.User user, String vcard, String first_name, String last_name) {
        try {
            File f = AndroidUtilities.getSharingDirectory();
            f.mkdirs();
            File f2 = new File(f, "vcard.vcf");
            BufferedWriter writer = new BufferedWriter(new FileWriter(f2));
            writer.write(vcard);
            writer.close();
            showDialog(new PhonebookShareAlert(this, null, user, null, f2, first_name, last_name));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.visibleDialog instanceof DatePickerDialog) {
            this.visibleDialog.dismiss();
        }
    }

    public void alertUserOpenError(MessageObject message) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        if (message.type == 3) {
            builder.setMessage(LocaleController.getString("NoPlayerInstalled", R.string.NoPlayerInstalled));
        } else {
            builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", R.string.NoHandleAppInstalled, message.getDocument().mime_type));
        }
        showDialog(builder.create());
    }

    public TLRPC.Chat getCurrentChat() {
        return this.currentChat;
    }

    public void addCanBanUser(Bundle bundle, long uid) {
        if (!this.currentChat.megagroup || this.admins == null || !ChatObject.canBlockUsers(this.currentChat)) {
            return;
        }
        int a = 0;
        while (true) {
            if (a >= this.admins.size()) {
                break;
            }
            TLRPC.ChannelParticipant channelParticipant = this.admins.get(a);
            if (MessageObject.getPeerId(channelParticipant.peer) != uid) {
                a++;
            } else if (!channelParticipant.can_edit) {
                return;
            }
        }
        bundle.putLong("ban_chat_id", this.currentChat.id);
    }

    public void showOpenUrlAlert(final String url, boolean ask) {
        if (Browser.isInternalUrl(url, null) || !ask) {
            Browser.openUrl((Context) getParentActivity(), url, true);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("OpenUrlTitle", R.string.OpenUrlTitle));
        builder.setMessage(LocaleController.formatString("OpenUrlAlert2", R.string.OpenUrlAlert2, url));
        builder.setPositiveButton(LocaleController.getString("Open", R.string.Open), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelAdminLogActivity.this.m1717xd8caeae1(url, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    /* renamed from: lambda$showOpenUrlAlert$12$org-telegram-ui-ChannelAdminLogActivity */
    public /* synthetic */ void m1717xd8caeae1(String url, DialogInterface dialogInterface, int i) {
        Browser.openUrl((Context) getParentActivity(), url, true);
    }

    private void removeMessageObject(MessageObject messageObject) {
        int index = this.messages.indexOf(messageObject);
        if (index == -1) {
            return;
        }
        this.messages.remove(index);
        ChatActivityAdapter chatActivityAdapter = this.chatAdapter;
        if (chatActivityAdapter == null) {
            return;
        }
        chatActivityAdapter.notifyItemRemoved(((chatActivityAdapter.messagesStartRow + this.messages.size()) - index) - 1);
    }

    /* loaded from: classes4.dex */
    public class ChatActivityAdapter extends RecyclerView.Adapter {
        private int loadingUpRow;
        private Context mContext;
        private int messagesEndRow;
        private int messagesStartRow;
        private int rowCount;

        public ChatActivityAdapter(Context context) {
            ChannelAdminLogActivity.this = this$0;
            this.mContext = context;
        }

        public void updateRows() {
            this.rowCount = 0;
            if (!ChannelAdminLogActivity.this.messages.isEmpty()) {
                if (!ChannelAdminLogActivity.this.endReached) {
                    int i = this.rowCount;
                    this.rowCount = i + 1;
                    this.loadingUpRow = i;
                } else {
                    this.loadingUpRow = -1;
                }
                int i2 = this.rowCount;
                this.messagesStartRow = i2;
                int size = i2 + ChannelAdminLogActivity.this.messages.size();
                this.rowCount = size;
                this.messagesEndRow = size;
                return;
            }
            this.loadingUpRow = -1;
            this.messagesStartRow = -1;
            this.messagesEndRow = -1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return -1L;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                if (!ChannelAdminLogActivity.this.chatMessageCellsCache.isEmpty()) {
                    view = (View) ChannelAdminLogActivity.this.chatMessageCellsCache.get(0);
                    ChannelAdminLogActivity.this.chatMessageCellsCache.remove(0);
                } else {
                    view = new ChatMessageCell(this.mContext);
                }
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.setDelegate(new AnonymousClass1());
                chatMessageCell.setAllowAssistant(true);
            } else if (viewType == 1) {
                view = new ChatActionCell(this.mContext) { // from class: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.2
                    @Override // org.telegram.ui.Cells.ChatActionCell, android.view.View
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                        super.onInitializeAccessibilityNodeInfo(info);
                        info.setVisibleToUser(true);
                    }
                };
                ((ChatActionCell) view).setDelegate(new AnonymousClass3());
            } else if (viewType == 2) {
                view = new ChatUnreadCell(this.mContext, null);
            } else {
                view = new ChatLoadingCell(this.mContext, ChannelAdminLogActivity.this.contentView, null);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1 */
        /* loaded from: classes4.dex */
        public class AnonymousClass1 implements ChatMessageCell.ChatMessageCellDelegate {
            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ boolean canDrawOutboundsContent() {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$canDrawOutboundsContent(this);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didLongPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressBotButton(this, chatMessageCell, keyboardButton);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2) {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$didLongPressChannelAvatar(this, chatMessageCell, chat, i, f, f2);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressBotButton(this, chatMessageCell, keyboardButton);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressCommentButton(ChatMessageCell chatMessageCell) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressCommentButton(this, chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressHiddenForward(ChatMessageCell chatMessageCell) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHiddenForward(this, chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressHint(ChatMessageCell chatMessageCell, int i) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressHint(this, chatMessageCell, i);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressReaction(ChatMessageCell chatMessageCell, TLRPC.TL_reactionCount tL_reactionCount, boolean z) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressReaction(this, chatMessageCell, tL_reactionCount, z);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressTime(ChatMessageCell chatMessageCell) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressTime(this, chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressViaBotNotInline(ChatMessageCell chatMessageCell, long j) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressViaBotNotInline(this, chatMessageCell, j);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList arrayList, int i, int i2, int i3) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didPressVoteButtons(this, chatMessageCell, arrayList, i, i2, i3);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void didStartVideoStream(MessageObject messageObject) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$didStartVideoStream(this, messageObject);
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
            public /* synthetic */ void setShouldNotRepeatSticker(MessageObject messageObject) {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$setShouldNotRepeatSticker(this, messageObject);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell) {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldDrawThreadProgress(this, chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ boolean shouldRepeatSticker(MessageObject messageObject) {
                return ChatMessageCell.ChatMessageCellDelegate.CC.$default$shouldRepeatSticker(this, messageObject);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public /* synthetic */ void videoTimerReached() {
                ChatMessageCell.ChatMessageCellDelegate.CC.$default$videoTimerReached(this);
            }

            AnonymousClass1() {
                ChatActivityAdapter.this = this$1;
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressSideButton(ChatMessageCell cell) {
                if (ChannelAdminLogActivity.this.getParentActivity() == null) {
                    return;
                }
                ChannelAdminLogActivity.this.showDialog(ShareAlert.createShareAlert(ChatActivityAdapter.this.mContext, cell.getMessageObject(), null, ChatObject.isChannel(ChannelAdminLogActivity.this.currentChat) && !ChannelAdminLogActivity.this.currentChat.megagroup, null, false));
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean needPlayMessage(MessageObject messageObject) {
                if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                    boolean result = MediaController.getInstance().playMessage(messageObject);
                    MediaController.getInstance().setVoiceMessagesPlaylist(null, false);
                    return result;
                } else if (!messageObject.isMusic()) {
                    return false;
                } else {
                    return MediaController.getInstance().setPlaylist(ChannelAdminLogActivity.this.messages, messageObject, 0L);
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressChannelAvatar(ChatMessageCell cell, TLRPC.Chat chat, int postId, float touchX, float touchY) {
                if (chat != null && chat != ChannelAdminLogActivity.this.currentChat) {
                    Bundle args = new Bundle();
                    args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, chat.id);
                    if (postId != 0) {
                        args.putInt(Constants.MessagePayloadKeys.MSGID_SERVER, postId);
                    }
                    if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(args, ChannelAdminLogActivity.this)) {
                        ChannelAdminLogActivity.this.presentFragment(new ChatActivity(args), true);
                    }
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressOther(ChatMessageCell cell, float x, float y) {
                ChannelAdminLogActivity.this.createMenu(cell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressUserAvatar(ChatMessageCell cell, TLRPC.User user, float touchX, float touchY) {
                if (user != null && user.id != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                    openProfile(user);
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean didLongPressUserAvatar(final ChatMessageCell cell, final TLRPC.User user, float touchX, float touchY) {
                AvatarPreviewer.Data data;
                if (user != null && user.id != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                    AvatarPreviewer.MenuItem[] menuItems = {AvatarPreviewer.MenuItem.OPEN_PROFILE, AvatarPreviewer.MenuItem.SEND_MESSAGE};
                    TLRPC.UserFull userFull = ChannelAdminLogActivity.this.getMessagesController().getUserFull(user.id);
                    if (userFull == null) {
                        data = AvatarPreviewer.Data.of(user, ChannelAdminLogActivity.this.classGuid, menuItems);
                    } else {
                        data = AvatarPreviewer.Data.of(userFull, menuItems);
                    }
                    if (AvatarPreviewer.canPreview(data)) {
                        AvatarPreviewer.getInstance().show((ViewGroup) ChannelAdminLogActivity.this.fragmentView, data, new AvatarPreviewer.Callback() { // from class: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda1
                            @Override // org.telegram.ui.AvatarPreviewer.Callback
                            public final void onMenuClick(AvatarPreviewer.MenuItem menuItem) {
                                ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass1.this.m1719xd0592d13(cell, user, menuItem);
                            }
                        });
                        return true;
                    }
                }
                return false;
            }

            /* renamed from: lambda$didLongPressUserAvatar$0$org-telegram-ui-ChannelAdminLogActivity$ChatActivityAdapter$1 */
            public /* synthetic */ void m1719xd0592d13(ChatMessageCell cell, TLRPC.User user, AvatarPreviewer.MenuItem item) {
                switch (AnonymousClass17.$SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem[item.ordinal()]) {
                    case 1:
                        openDialog(cell, user);
                        return;
                    case 2:
                        openProfile(user);
                        return;
                    default:
                        return;
                }
            }

            private void openProfile(TLRPC.User user) {
                Bundle args = new Bundle();
                args.putLong("user_id", user.id);
                ChannelAdminLogActivity.this.addCanBanUser(args, user.id);
                ProfileActivity fragment = new ProfileActivity(args);
                fragment.setPlayProfileAnimation(0);
                ChannelAdminLogActivity.this.presentFragment(fragment);
            }

            private void openDialog(ChatMessageCell cell, TLRPC.User user) {
                if (user != null) {
                    Bundle args = new Bundle();
                    args.putLong("user_id", user.id);
                    if (ChannelAdminLogActivity.this.getMessagesController().checkCanOpenChat(args, ChannelAdminLogActivity.this)) {
                        ChannelAdminLogActivity.this.presentFragment(new ChatActivity(args));
                    }
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressCancelSendButton(ChatMessageCell cell) {
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didLongPress(ChatMessageCell cell, float x, float y) {
                ChannelAdminLogActivity.this.createMenu(cell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean canPerformActions() {
                return true;
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressUrl(ChatMessageCell cell, CharacterStyle url, boolean longPress) {
                if (url == null) {
                    return;
                }
                MessageObject messageObject = cell.getMessageObject();
                if (url instanceof URLSpanMono) {
                    ((URLSpanMono) url).copyToClipboard();
                    if (Build.VERSION.SDK_INT < 31) {
                        Toast.makeText(ChannelAdminLogActivity.this.getParentActivity(), LocaleController.getString("TextCopied", R.string.TextCopied), 0).show();
                    }
                } else if (url instanceof URLSpanUserMention) {
                    long peerId = Utilities.parseLong(((URLSpanUserMention) url).getURL()).longValue();
                    if (peerId > 0) {
                        TLRPC.User user = MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).getUser(Long.valueOf(peerId));
                        if (user != null) {
                            MessagesController.openChatOrProfileWith(user, null, ChannelAdminLogActivity.this, 0, false);
                            return;
                        }
                        return;
                    }
                    TLRPC.Chat chat = MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).getChat(Long.valueOf(-peerId));
                    if (chat != null) {
                        MessagesController.openChatOrProfileWith(null, chat, ChannelAdminLogActivity.this, 0, false);
                    }
                } else if (url instanceof URLSpanNoUnderline) {
                    String str = ((URLSpanNoUnderline) url).getURL();
                    if (str.startsWith("@")) {
                        MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(str.substring(1), ChannelAdminLogActivity.this, 0);
                    } else if (str.startsWith("#")) {
                        DialogsActivity fragment = new DialogsActivity(null);
                        fragment.setSearchString(str);
                        ChannelAdminLogActivity.this.presentFragment(fragment);
                    }
                } else {
                    final String urlFinal = ((URLSpan) url).getURL();
                    if (longPress) {
                        BottomSheet.Builder builder = new BottomSheet.Builder(ChannelAdminLogActivity.this.getParentActivity());
                        builder.setTitle(urlFinal);
                        builder.setItems(new CharSequence[]{LocaleController.getString("Open", R.string.Open), LocaleController.getString("Copy", R.string.Copy)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda0
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass1.this.m1720x8aea75b9(urlFinal, dialogInterface, i);
                            }
                        });
                        ChannelAdminLogActivity.this.showDialog(builder.create());
                    } else if (url instanceof URLSpanReplacement) {
                        ChannelAdminLogActivity.this.showOpenUrlAlert(((URLSpanReplacement) url).getURL(), true);
                    } else {
                        if ((messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage) && messageObject.messageOwner.media.webpage != null && messageObject.messageOwner.media.webpage.cached_page != null) {
                            String lowerUrl = urlFinal.toLowerCase();
                            String lowerUrl2 = messageObject.messageOwner.media.webpage.url.toLowerCase();
                            if ((Browser.isTelegraphUrl(lowerUrl, false) || lowerUrl.contains("t.me/iv")) && (lowerUrl.contains(lowerUrl2) || lowerUrl2.contains(lowerUrl))) {
                                ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                                ArticleViewer.getInstance().open(messageObject);
                                return;
                            }
                        }
                        Browser.openUrl((Context) ChannelAdminLogActivity.this.getParentActivity(), urlFinal, true);
                    }
                }
            }

            /* renamed from: lambda$didPressUrl$1$org-telegram-ui-ChannelAdminLogActivity$ChatActivityAdapter$1 */
            public /* synthetic */ void m1720x8aea75b9(String urlFinal, DialogInterface dialog, int which) {
                if (which == 0) {
                    Browser.openUrl((Context) ChannelAdminLogActivity.this.getParentActivity(), urlFinal, true);
                } else if (which == 1) {
                    String url1 = urlFinal;
                    if (url1.startsWith(MailTo.MAILTO_SCHEME)) {
                        url1 = url1.substring(7);
                    } else if (url1.startsWith("tel:")) {
                        url1 = url1.substring(4);
                    }
                    AndroidUtilities.addToClipboard(url1);
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void needOpenWebView(MessageObject message, String url, String title, String description, String originalUrl, int w, int h) {
                EmbedBottomSheet.show(ChannelAdminLogActivity.this.getParentActivity(), message, ChannelAdminLogActivity.this.provider, title, description, originalUrl, url, w, h, false);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressReplyMessage(ChatMessageCell cell, int id) {
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressViaBot(ChatMessageCell cell, String username) {
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressImage(ChatMessageCell cell, float x, float y) {
                MessageObject message = cell.getMessageObject();
                if (message.getInputStickerSet() != null) {
                    ChannelAdminLogActivity.this.showDialog(new StickersAlert(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this, message.getInputStickerSet(), (TLRPC.TL_messages_stickerSet) null, (StickersAlert.StickersAlertDelegate) null));
                } else if (message.isVideo() || message.type == 1 || ((message.type == 0 && !message.isWebpageDocument()) || message.isGif())) {
                    PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity());
                    PhotoViewer.getInstance().openPhoto(message, (ChatActivity) null, 0L, 0L, ChannelAdminLogActivity.this.provider);
                } else if (message.type == 3) {
                    File f = null;
                    try {
                        if (message.messageOwner.attachPath != null && message.messageOwner.attachPath.length() != 0) {
                            f = new File(message.messageOwner.attachPath);
                        }
                        if (f == null || !f.exists()) {
                            f = ChannelAdminLogActivity.this.getFileLoader().getPathToMessage(message.messageOwner);
                        }
                        Intent intent = new Intent("android.intent.action.VIEW");
                        if (Build.VERSION.SDK_INT >= 24) {
                            intent.setFlags(1);
                            intent.setDataAndType(FileProvider.getUriForFile(ChannelAdminLogActivity.this.getParentActivity(), "org.telegram.messenger.beta.provider", f), MimeTypes.VIDEO_MP4);
                        } else {
                            intent.setDataAndType(Uri.fromFile(f), MimeTypes.VIDEO_MP4);
                        }
                        ChannelAdminLogActivity.this.getParentActivity().startActivityForResult(intent, 500);
                    } catch (Exception e) {
                        ChannelAdminLogActivity.this.alertUserOpenError(message);
                    }
                } else if (message.type == 4) {
                    if (!AndroidUtilities.isGoogleMapsInstalled(ChannelAdminLogActivity.this)) {
                        return;
                    }
                    LocationActivity fragment = new LocationActivity(0);
                    fragment.setMessageObject(message);
                    ChannelAdminLogActivity.this.presentFragment(fragment);
                } else if (message.type == 9 || message.type == 0) {
                    if (message.getDocumentName().toLowerCase().endsWith("attheme")) {
                        File locFile = null;
                        if (message.messageOwner.attachPath != null && message.messageOwner.attachPath.length() != 0) {
                            File f2 = new File(message.messageOwner.attachPath);
                            if (f2.exists()) {
                                locFile = f2;
                            }
                        }
                        if (locFile == null) {
                            File f3 = ChannelAdminLogActivity.this.getFileLoader().getPathToMessage(message.messageOwner);
                            if (f3.exists()) {
                                locFile = f3;
                            }
                        }
                        if (ChannelAdminLogActivity.this.chatLayoutManager != null) {
                            int lastPosition = ChannelAdminLogActivity.this.chatLayoutManager.findLastVisibleItemPosition();
                            if (lastPosition >= ChannelAdminLogActivity.this.chatLayoutManager.getItemCount() - 1) {
                                ChannelAdminLogActivity.this.scrollToPositionOnRecreate = -1;
                            } else {
                                ChannelAdminLogActivity.this.scrollToPositionOnRecreate = ChannelAdminLogActivity.this.chatLayoutManager.findFirstVisibleItemPosition();
                                RecyclerListView.Holder holder = (RecyclerListView.Holder) ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(ChannelAdminLogActivity.this.scrollToPositionOnRecreate);
                                if (holder == null) {
                                    ChannelAdminLogActivity.this.scrollToPositionOnRecreate = -1;
                                } else {
                                    ChannelAdminLogActivity.this.scrollToOffsetOnRecreate = holder.itemView.getTop();
                                }
                            }
                        }
                        Theme.ThemeInfo themeInfo = Theme.applyThemeFile(locFile, message.getDocumentName(), null, true);
                        if (themeInfo == null) {
                            ChannelAdminLogActivity.this.scrollToPositionOnRecreate = -1;
                        } else {
                            ChannelAdminLogActivity.this.presentFragment(new ThemePreviewActivity(themeInfo));
                            return;
                        }
                    }
                    try {
                        AndroidUtilities.openForView(message, ChannelAdminLogActivity.this.getParentActivity(), (Theme.ResourcesProvider) null);
                    } catch (Exception e2) {
                        ChannelAdminLogActivity.this.alertUserOpenError(message);
                    }
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressInstantButton(ChatMessageCell cell, int type) {
                MessageObject messageObject = cell.getMessageObject();
                if (type == 0) {
                    if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.webpage != null && messageObject.messageOwner.media.webpage.cached_page != null) {
                        ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                        ArticleViewer.getInstance().open(messageObject);
                    }
                } else if (type == 5) {
                    ChannelAdminLogActivity.this.openVCard(ChannelAdminLogActivity.this.getMessagesController().getUser(Long.valueOf(messageObject.messageOwner.media.user_id)), messageObject.messageOwner.media.vcard, messageObject.messageOwner.media.first_name, messageObject.messageOwner.media.last_name);
                } else if (messageObject.messageOwner.media != null && messageObject.messageOwner.media.webpage != null) {
                    Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), messageObject.messageOwner.media.webpage.url);
                }
            }
        }

        /* renamed from: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$3 */
        /* loaded from: classes4.dex */
        public class AnonymousClass3 implements ChatActionCell.ChatActionCellDelegate {
            AnonymousClass3() {
                ChatActivityAdapter.this = this$1;
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void didClickImage(ChatActionCell cell) {
                MessageObject message = cell.getMessageObject();
                PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity());
                TLRPC.PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(message.photoThumbs, 640);
                if (photoSize == null) {
                    PhotoViewer.getInstance().openPhoto(message, (ChatActivity) null, 0L, 0L, ChannelAdminLogActivity.this.provider);
                    return;
                }
                ImageLocation imageLocation = ImageLocation.getForPhoto(photoSize, message.messageOwner.action.photo);
                PhotoViewer.getInstance().openPhoto(photoSize.location, imageLocation, ChannelAdminLogActivity.this.provider);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public boolean didLongPress(ChatActionCell cell, float x, float y) {
                return ChannelAdminLogActivity.this.createMenu(cell);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void needOpenUserProfile(long uid) {
                if (uid >= 0) {
                    if (uid != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()) {
                        Bundle args = new Bundle();
                        args.putLong("user_id", uid);
                        ChannelAdminLogActivity.this.addCanBanUser(args, uid);
                        ProfileActivity fragment = new ProfileActivity(args);
                        fragment.setPlayProfileAnimation(0);
                        ChannelAdminLogActivity.this.presentFragment(fragment);
                        return;
                    }
                    return;
                }
                Bundle args2 = new Bundle();
                args2.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -uid);
                if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(args2, ChannelAdminLogActivity.this)) {
                    ChannelAdminLogActivity.this.presentFragment(new ChatActivity(args2), true);
                }
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void needOpenInviteLink(final TLRPC.TL_chatInviteExported invite) {
                if (!ChannelAdminLogActivity.this.linviteLoading) {
                    Object cachedInvite = ChannelAdminLogActivity.this.invitesCache.containsKey(invite.link) ? ChannelAdminLogActivity.this.invitesCache.get(invite.link) : null;
                    if (cachedInvite != null) {
                        if (cachedInvite instanceof TLRPC.TL_messages_exportedChatInvite) {
                            ChannelAdminLogActivity.this.showInviteLinkBottomSheet((TLRPC.TL_messages_exportedChatInvite) cachedInvite, ChannelAdminLogActivity.this.usersMap);
                            return;
                        } else {
                            BulletinFactory.of(ChannelAdminLogActivity.this).createSimpleBulletin(R.raw.linkbroken, LocaleController.getString("LinkHashExpired", R.string.LinkHashExpired)).show();
                            return;
                        }
                    }
                    TLRPC.TL_messages_getExportedChatInvite req = new TLRPC.TL_messages_getExportedChatInvite();
                    req.peer = ChannelAdminLogActivity.this.getMessagesController().getInputPeer(-ChannelAdminLogActivity.this.currentChat.id);
                    req.link = invite.link;
                    ChannelAdminLogActivity.this.linviteLoading = true;
                    final boolean[] canceled = new boolean[1];
                    final AlertDialog progressDialog = new AlertDialog(ChannelAdminLogActivity.this.getParentActivity(), 3);
                    progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnCancelListener
                        public final void onCancel(DialogInterface dialogInterface) {
                            ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass3.this.m1721xaf0c41cc(canceled, dialogInterface);
                        }
                    });
                    progressDialog.showDelayed(300L);
                    int reqId = ChannelAdminLogActivity.this.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda2
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass3.this.m1723x962b4a4e(invite, canceled, progressDialog, tLObject, tL_error);
                        }
                    });
                    ChannelAdminLogActivity.this.getConnectionsManager().bindRequestToGuid(reqId, ChannelAdminLogActivity.this.classGuid);
                }
            }

            /* renamed from: lambda$needOpenInviteLink$0$org-telegram-ui-ChannelAdminLogActivity$ChatActivityAdapter$3 */
            public /* synthetic */ void m1721xaf0c41cc(boolean[] canceled, DialogInterface dialogInterface) {
                ChannelAdminLogActivity.this.linviteLoading = false;
                canceled[0] = true;
            }

            /* renamed from: lambda$needOpenInviteLink$2$org-telegram-ui-ChannelAdminLogActivity$ChatActivityAdapter$3 */
            public /* synthetic */ void m1723x962b4a4e(final TLRPC.TL_chatInviteExported invite, final boolean[] canceled, final AlertDialog progressDialog, TLObject response, TLRPC.TL_error error) {
                TLRPC.TL_messages_exportedChatInvite resInvite = null;
                if (error == null) {
                    resInvite = (TLRPC.TL_messages_exportedChatInvite) response;
                    for (int i = 0; i < resInvite.users.size(); i++) {
                        TLRPC.User user = resInvite.users.get(i);
                        if (ChannelAdminLogActivity.this.usersMap == null) {
                            ChannelAdminLogActivity.this.usersMap = new HashMap();
                        }
                        ChannelAdminLogActivity.this.usersMap.put(Long.valueOf(user.id), user);
                    }
                }
                final TLRPC.TL_messages_exportedChatInvite finalInvite = resInvite;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$3$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChannelAdminLogActivity.ChatActivityAdapter.AnonymousClass3.this.m1722xa29bc60d(invite, finalInvite, canceled, progressDialog);
                    }
                });
            }

            /* renamed from: lambda$needOpenInviteLink$1$org-telegram-ui-ChannelAdminLogActivity$ChatActivityAdapter$3 */
            public /* synthetic */ void m1722xa29bc60d(TLRPC.TL_chatInviteExported invite, TLRPC.TL_messages_exportedChatInvite finalInvite, boolean[] canceled, AlertDialog progressDialog) {
                ChannelAdminLogActivity.this.linviteLoading = false;
                ChannelAdminLogActivity.this.invitesCache.put(invite.link, finalInvite == null ? 0 : finalInvite);
                if (canceled[0]) {
                    return;
                }
                progressDialog.dismiss();
                if (finalInvite != null) {
                    ChannelAdminLogActivity.this.showInviteLinkBottomSheet(finalInvite, ChannelAdminLogActivity.this.usersMap);
                } else {
                    BulletinFactory.of(ChannelAdminLogActivity.this).createSimpleBulletin(R.raw.linkbroken, LocaleController.getString("LinkHashExpired", R.string.LinkHashExpired)).show();
                }
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void didPressReplyMessage(ChatActionCell cell, int id) {
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void didPressBotButton(MessageObject messageObject, TLRPC.KeyboardButton button) {
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean pinnedBotton;
            boolean z = false;
            boolean pinnedTop = true;
            if (position == this.loadingUpRow) {
                ChatLoadingCell loadingCell = (ChatLoadingCell) holder.itemView;
                if (ChannelAdminLogActivity.this.loadsCount > 1) {
                    z = true;
                }
                loadingCell.setProgressVisible(z);
            } else if (position >= this.messagesStartRow && position < this.messagesEndRow) {
                MessageObject message = ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - (position - this.messagesStartRow)) - 1);
                View view = holder.itemView;
                if (view instanceof ChatMessageCell) {
                    ChatMessageCell messageCell = (ChatMessageCell) view;
                    messageCell.isChat = true;
                    int nextType = getItemViewType(position + 1);
                    int prevType = getItemViewType(position - 1);
                    if (!(message.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) && nextType == holder.getItemViewType()) {
                        MessageObject nextMessage = ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - ((position + 1) - this.messagesStartRow)) - 1);
                        pinnedBotton = nextMessage.isOutOwner() == message.isOutOwner() && nextMessage.getFromChatId() == message.getFromChatId() && Math.abs(nextMessage.messageOwner.date - message.messageOwner.date) <= 300;
                    } else {
                        pinnedBotton = false;
                    }
                    if (prevType == holder.getItemViewType()) {
                        MessageObject prevMessage = ChannelAdminLogActivity.this.messages.get(ChannelAdminLogActivity.this.messages.size() - (position - this.messagesStartRow));
                        if ((prevMessage.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) || prevMessage.isOutOwner() != message.isOutOwner() || prevMessage.getFromChatId() != message.getFromChatId() || Math.abs(prevMessage.messageOwner.date - message.messageOwner.date) > 300) {
                            pinnedTop = false;
                        }
                    } else {
                        pinnedTop = false;
                    }
                    messageCell.setMessageObject(message, null, pinnedBotton, pinnedTop);
                    messageCell.setHighlighted(false);
                    messageCell.setHighlightedText(null);
                } else if (view instanceof ChatActionCell) {
                    ChatActionCell actionCell = (ChatActionCell) view;
                    actionCell.setMessageObject(message);
                    actionCell.setAlpha(1.0f);
                }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position >= this.messagesStartRow && position < this.messagesEndRow) {
                return ChannelAdminLogActivity.this.messages.get((ChannelAdminLogActivity.this.messages.size() - (position - this.messagesStartRow)) - 1).contentType;
            }
            return 4;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(final RecyclerView.ViewHolder holder) {
            if ((holder.itemView instanceof ChatMessageCell) || (holder.itemView instanceof ChatActionCell)) {
                final View view = holder.itemView;
                holder.itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.4
                    @Override // android.view.ViewTreeObserver.OnPreDrawListener
                    public boolean onPreDraw() {
                        int viewBottom;
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        int height = ChannelAdminLogActivity.this.chatListView.getMeasuredHeight();
                        int top = view.getTop();
                        view.getBottom();
                        int viewTop = top >= 0 ? 0 : -top;
                        int viewBottom2 = view.getMeasuredHeight();
                        if (viewBottom2 <= height) {
                            viewBottom = viewBottom2;
                        } else {
                            viewBottom = viewTop + height;
                        }
                        if (holder.itemView instanceof ChatMessageCell) {
                            ((ChatMessageCell) view).setVisiblePart(viewTop, viewBottom - viewTop, (ChannelAdminLogActivity.this.contentView.getHeightWithKeyboard() - AndroidUtilities.dp(48.0f)) - ChannelAdminLogActivity.this.chatListView.getTop(), 0.0f, (view.getY() + ChannelAdminLogActivity.this.actionBar.getMeasuredHeight()) - ChannelAdminLogActivity.this.contentView.getBackgroundTranslationY(), ChannelAdminLogActivity.this.contentView.getMeasuredWidth(), ChannelAdminLogActivity.this.contentView.getBackgroundSizeY(), 0, 0);
                            return true;
                        } else if ((holder.itemView instanceof ChatActionCell) && ChannelAdminLogActivity.this.actionBar != null && ChannelAdminLogActivity.this.contentView != null) {
                            View view2 = view;
                            ((ChatActionCell) view2).setVisiblePart((view2.getY() + ChannelAdminLogActivity.this.actionBar.getMeasuredHeight()) - ChannelAdminLogActivity.this.contentView.getBackgroundTranslationY(), ChannelAdminLogActivity.this.contentView.getBackgroundSizeY());
                            return true;
                        } else {
                            return true;
                        }
                    }
                });
            }
            View view2 = holder.itemView;
            if (view2 instanceof ChatMessageCell) {
                ChatMessageCell messageCell = (ChatMessageCell) holder.itemView;
                messageCell.getMessageObject();
                messageCell.setBackgroundDrawable(null);
                messageCell.setCheckPressed(true, false);
                messageCell.setHighlighted(false);
            }
        }

        public void updateRowWithMessageObject(MessageObject messageObject) {
            int index = ChannelAdminLogActivity.this.messages.indexOf(messageObject);
            if (index == -1) {
                return;
            }
            notifyItemChanged(((this.messagesStartRow + ChannelAdminLogActivity.this.messages.size()) - index) - 1);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateRows();
            try {
                super.notifyDataSetChanged();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int position) {
            updateRows();
            try {
                super.notifyItemChanged(position);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeChanged(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemInserted(int position) {
            updateRows();
            try {
                super.notifyItemInserted(position);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            try {
                super.notifyItemMoved(fromPosition, toPosition);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeInserted(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRemoved(int position) {
            updateRows();
            try {
                super.notifyItemRemoved(position);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(positionStart, itemCount);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ChannelAdminLogActivity$17 */
    /* loaded from: classes4.dex */
    public static /* synthetic */ class AnonymousClass17 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem;

        static {
            int[] iArr = new int[AvatarPreviewer.MenuItem.values().length];
            $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem = iArr;
            try {
                iArr[AvatarPreviewer.MenuItem.SEND_MESSAGE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem[AvatarPreviewer.MenuItem.OPEN_PROFILE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public void showInviteLinkBottomSheet(TLRPC.TL_messages_exportedChatInvite invite, HashMap<Long, TLRPC.User> usersMap) {
        TLRPC.ChatFull chatInfo = getMessagesController().getChatFull(this.currentChat.id);
        InviteLinkBottomSheet inviteLinkBottomSheet = new InviteLinkBottomSheet(this.contentView.getContext(), (TLRPC.TL_chatInviteExported) invite.invite, chatInfo, usersMap, this, chatInfo.id, false, ChatObject.isChannel(this.currentChat));
        inviteLinkBottomSheet.setInviteDelegate(new InviteLinkBottomSheet.InviteDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity.16
            @Override // org.telegram.ui.Components.InviteLinkBottomSheet.InviteDelegate
            public void permanentLinkReplaced(TLRPC.TL_chatInviteExported oldLink, TLRPC.TL_chatInviteExported newLink) {
            }

            @Override // org.telegram.ui.Components.InviteLinkBottomSheet.InviteDelegate
            public void linkRevoked(TLRPC.TL_chatInviteExported invite2) {
                TLRPC.TL_channelAdminLogEvent event = new TLRPC.TL_channelAdminLogEvent();
                int size = ChannelAdminLogActivity.this.messages.size();
                invite2.revoked = true;
                TLRPC.TL_channelAdminLogEventActionExportedInviteRevoke revokeAction = new TLRPC.TL_channelAdminLogEventActionExportedInviteRevoke();
                revokeAction.invite = invite2;
                event.action = revokeAction;
                event.date = (int) (System.currentTimeMillis() / 1000);
                event.user_id = ChannelAdminLogActivity.this.getAccountInstance().getUserConfig().clientUserId;
                MessageObject messageObject = new MessageObject(ChannelAdminLogActivity.this.currentAccount, event, ChannelAdminLogActivity.this.messages, (HashMap<String, ArrayList<MessageObject>>) ChannelAdminLogActivity.this.messagesByDays, ChannelAdminLogActivity.this.currentChat, ChannelAdminLogActivity.this.mid, true);
                if (messageObject.contentType < 0) {
                    return;
                }
                int addCount = ChannelAdminLogActivity.this.messages.size() - size;
                if (addCount > 0) {
                    ChannelAdminLogActivity.this.chatListItemAnimator.setShouldAnimateEnterFromBottom(true);
                    ChannelAdminLogActivity.this.chatAdapter.notifyItemRangeInserted(ChannelAdminLogActivity.this.chatAdapter.messagesEndRow, addCount);
                    ChannelAdminLogActivity.this.moveScrollToLastMessage();
                }
                ChannelAdminLogActivity.this.invitesCache.remove(invite2.link);
            }

            @Override // org.telegram.ui.Components.InviteLinkBottomSheet.InviteDelegate
            public void onLinkDeleted(TLRPC.TL_chatInviteExported invite2) {
                int size = ChannelAdminLogActivity.this.messages.size();
                int unused = ChannelAdminLogActivity.this.chatAdapter.messagesEndRow;
                TLRPC.TL_channelAdminLogEvent event = new TLRPC.TL_channelAdminLogEvent();
                TLRPC.TL_channelAdminLogEventActionExportedInviteDelete deleteAction = new TLRPC.TL_channelAdminLogEventActionExportedInviteDelete();
                deleteAction.invite = invite2;
                event.action = deleteAction;
                event.date = (int) (System.currentTimeMillis() / 1000);
                event.user_id = ChannelAdminLogActivity.this.getAccountInstance().getUserConfig().clientUserId;
                MessageObject messageObject = new MessageObject(ChannelAdminLogActivity.this.currentAccount, event, ChannelAdminLogActivity.this.messages, (HashMap<String, ArrayList<MessageObject>>) ChannelAdminLogActivity.this.messagesByDays, ChannelAdminLogActivity.this.currentChat, ChannelAdminLogActivity.this.mid, true);
                if (messageObject.contentType < 0) {
                    return;
                }
                int addCount = ChannelAdminLogActivity.this.messages.size() - size;
                if (addCount > 0) {
                    ChannelAdminLogActivity.this.chatListItemAnimator.setShouldAnimateEnterFromBottom(true);
                    ChannelAdminLogActivity.this.chatAdapter.notifyItemRangeInserted(ChannelAdminLogActivity.this.chatAdapter.messagesEndRow, addCount);
                    ChannelAdminLogActivity.this.moveScrollToLastMessage();
                }
                ChannelAdminLogActivity.this.invitesCache.remove(invite2.link);
            }

            @Override // org.telegram.ui.Components.InviteLinkBottomSheet.InviteDelegate
            public void onLinkEdited(TLRPC.TL_chatInviteExported invite2) {
                TLRPC.TL_channelAdminLogEvent event = new TLRPC.TL_channelAdminLogEvent();
                TLRPC.TL_channelAdminLogEventActionExportedInviteEdit editAction = new TLRPC.TL_channelAdminLogEventActionExportedInviteEdit();
                editAction.new_invite = invite2;
                editAction.prev_invite = invite2;
                event.action = editAction;
                event.date = (int) (System.currentTimeMillis() / 1000);
                event.user_id = ChannelAdminLogActivity.this.getAccountInstance().getUserConfig().clientUserId;
                MessageObject messageObject = new MessageObject(ChannelAdminLogActivity.this.currentAccount, event, ChannelAdminLogActivity.this.messages, (HashMap<String, ArrayList<MessageObject>>) ChannelAdminLogActivity.this.messagesByDays, ChannelAdminLogActivity.this.currentChat, ChannelAdminLogActivity.this.mid, true);
                if (messageObject.contentType >= 0) {
                    ChannelAdminLogActivity.this.chatAdapter.notifyDataSetChanged();
                    ChannelAdminLogActivity.this.moveScrollToLastMessage();
                }
            }
        });
        inviteLinkBottomSheet.show();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_chat_wallpaper));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.avatarContainer.getTitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.avatarContainer.getSubtitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, new Paint[]{Theme.chat_statusPaint, Theme.chat_statusRecordPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_actionBarDefaultSubtitle, (Object) null));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundPink));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageRed));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageOrange));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageViolet));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageGreen));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageCyan));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageBlue));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessagePink));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, Theme.key_chat_inBubble));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, Theme.key_chat_inBubbleSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, Theme.key_chat_inBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutDrawable.getShadowDrawables(), null, Theme.key_chat_outBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutMediaDrawable.getShadowDrawables(), null, Theme.key_chat_outBubbleShadow));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubble));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient1));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient2));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, Theme.key_chat_outBubbleGradient3));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, Theme.key_chat_outBubbleSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, Theme.key_chat_serviceText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, Theme.chat_actionTextPaint, null, null, Theme.key_chat_serviceLink));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_botCardDrawable, Theme.chat_shareIconDrawable, Theme.chat_botInlineDrawable, Theme.chat_botLinkDrawable, Theme.chat_goIconDrawable, Theme.chat_commentStickerDrawable}, null, Theme.key_chat_serviceIcon));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackground));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackgroundSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextIn));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextOut));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageLinkIn, (Object) null));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageLinkOut, (Object) null));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, Theme.key_chat_outSentCheck));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, Theme.key_chat_outSentCheckRead));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, Theme.key_chat_outSentCheckReadSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsDrawable, Theme.chat_msgOutRepliesDrawable, Theme.chat_msgOutPinnedDrawable}, null, Theme.key_chat_outViews));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutViewsSelectedDrawable, Theme.chat_msgOutRepliesSelectedDrawable, Theme.chat_msgOutPinnedSelectedDrawable}, null, Theme.key_chat_outViewsSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsDrawable, Theme.chat_msgInRepliesDrawable, Theme.chat_msgInPinnedDrawable}, null, Theme.key_chat_inViews));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable, Theme.chat_msgInRepliesSelectedDrawable, Theme.chat_msgInPinnedSelectedDrawable}, null, Theme.key_chat_inViewsSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaViewsDrawable, Theme.chat_msgMediaRepliesDrawable, Theme.chat_msgMediaPinnedDrawable}, null, Theme.key_chat_mediaViews));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuDrawable}, null, Theme.key_chat_outMenu));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutMenuSelectedDrawable}, null, Theme.key_chat_outMenuSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuDrawable}, null, Theme.key_chat_inMenu));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, null, Theme.key_chat_inMenuSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, null, Theme.key_chat_mediaMenu));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgOutInstantDrawable}, null, Theme.key_chat_outInstant));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInInstantDrawable, Theme.chat_commentDrawable, Theme.chat_commentArrowDrawable}, null, Theme.key_chat_inInstant));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutCallDrawable, null, Theme.key_chat_outInstant));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgOutCallSelectedDrawable, null, Theme.key_chat_outInstantSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInCallDrawable, null, Theme.key_chat_inInstant));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInCallSelectedDrawable, null, Theme.key_chat_inInstantSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpGreenDrawable}, null, Theme.key_chat_outGreenCall));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallDownRedDrawable}, null, Theme.key_chat_inRedCall));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallDownGreenDrawable}, null, Theme.key_chat_inGreenCall));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, null, null, Theme.key_chat_sentError));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgErrorDrawable}, null, Theme.key_chat_sentErrorIcon));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, null, null, Theme.key_chat_previewDurationText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, null, null, Theme.key_chat_previewGameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewInstantText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewInstantText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewInstantSelectedText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewInstantSelectedText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, null, null, Theme.key_chat_secretTimeText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botButtonPaint, null, null, Theme.key_chat_botButtonText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_botProgressPaint, null, null, Theme.key_chat_botProgress));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inForwardedNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outForwardedNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inViaBotNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outViaBotNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerViaBotNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyLine));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyLine));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyLine));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMessageText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMessageText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyMessageText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewLine));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewLine));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inSiteNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outSiteNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactPhoneText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactPhoneText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaProgress));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioProgress));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioProgress));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSelectedProgress));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaTimeText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioPerformerText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioPerformerText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioTitleText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioTitleText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationSelectedText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationSelectedText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbar));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbar));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarFill));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioCacheSeekbar));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarFill));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioCacheSeekbar));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbar));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbar));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarFill));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarFill));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgress));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgress));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgressSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgressSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileNameText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoSelectedText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoSelectedText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackground));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackground));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackgroundSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackgroundSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoSelectedText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoSelectedText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaInfoText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, null, null, Theme.key_chat_linkSelectBackground));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, null, null, Theme.key_chat_textSelectBackground));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outLoader));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outMediaIcon));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outLoaderSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outMediaIconSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inLoader));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inMediaIcon));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inLoaderSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inMediaIconSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, null, Theme.key_chat_mediaLoaderPhoto));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][0], Theme.chat_photoStatesDrawables[1][0], Theme.chat_photoStatesDrawables[2][0], Theme.chat_photoStatesDrawables[3][0]}, null, Theme.key_chat_mediaLoaderPhotoIcon));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, null, Theme.key_chat_mediaLoaderPhotoSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[0][1], Theme.chat_photoStatesDrawables[1][1], Theme.chat_photoStatesDrawables[2][1], Theme.chat_photoStatesDrawables[3][1]}, null, Theme.key_chat_mediaLoaderPhotoIconSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, null, Theme.key_chat_outLoaderPhoto));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][0], Theme.chat_photoStatesDrawables[8][0]}, null, Theme.key_chat_outLoaderPhotoIcon));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, null, Theme.key_chat_outLoaderPhotoSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[7][1], Theme.chat_photoStatesDrawables[8][1]}, null, Theme.key_chat_outLoaderPhotoIconSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, null, Theme.key_chat_inLoaderPhoto));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][0], Theme.chat_photoStatesDrawables[11][0]}, null, Theme.key_chat_inLoaderPhotoIcon));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, null, Theme.key_chat_inLoaderPhotoSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_photoStatesDrawables[10][1], Theme.chat_photoStatesDrawables[11][1]}, null, Theme.key_chat_inLoaderPhotoIconSelected));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactBackground));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactIcon));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactBackground));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactIcon));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inLocationBackground));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, Theme.key_chat_inLocationIcon));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outLocationBackground));
        themeDescriptions.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, Theme.key_chat_outLocationIcon));
        themeDescriptions.add(new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, Theme.key_chat_messagePanelBackground));
        themeDescriptions.add(new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, Theme.key_chat_messagePanelShadow));
        themeDescriptions.add(new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_fieldOverlayText));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_serviceText));
        themeDescriptions.add(new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_chat_serviceText));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_unreadMessagesStartBackground));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_unreadMessagesStartArrowIcon));
        themeDescriptions.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_unreadMessagesStartText));
        themeDescriptions.add(new ThemeDescription(this.progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, Theme.key_chat_serviceBackground));
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        ImageView imageView = null;
        themeDescriptions.add(new ThemeDescription(chatAvatarContainer != null ? chatAvatarContainer.getTimeItem() : null, 0, null, null, null, null, Theme.key_chat_secretTimerBackground));
        ChatAvatarContainer chatAvatarContainer2 = this.avatarContainer;
        if (chatAvatarContainer2 != null) {
            imageView = chatAvatarContainer2.getTimeItem();
        }
        themeDescriptions.add(new ThemeDescription(imageView, 0, null, null, null, null, Theme.key_chat_secretTimerText));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        return themeDescriptions;
    }
}
