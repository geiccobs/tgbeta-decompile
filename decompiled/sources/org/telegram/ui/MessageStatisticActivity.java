package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.ArraySet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.gms.common.Scopes;
import com.google.firebase.messaging.Constants;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LruCache;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Charts.BaseChartView;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.StackLinearChartData;
import org.telegram.ui.Charts.view_data.ChartHeaderView;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.MessageStatisticActivity;
import org.telegram.ui.StatisticActivity;
/* loaded from: classes4.dex */
public class MessageStatisticActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ChatAvatarContainer avatarContainer;
    private TLRPC.ChatFull chat;
    private final long chatId;
    boolean drawPlay;
    private int emptyRow;
    private EmptyTextProgressView emptyView;
    private boolean endReached;
    private int endRow;
    private boolean firstLoaded;
    private int headerRow;
    private RLottieImageView imageView;
    private int interactionsChartRow;
    private StatisticActivity.ChartViewData interactionsViewData;
    private StatisticActivity.ZoomCancelable lastCancelable;
    private LinearLayoutManager layoutManager;
    private FrameLayout listContainer;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loading;
    private int loadingRow;
    private final int messageId;
    private MessageObject messageObject;
    private int nextRate;
    private int overviewHeaderRow;
    private int overviewRow;
    private LinearLayout progressLayout;
    private int publicChats;
    private int rowCount;
    private BaseChartView.SharedUiComponents sharedUi;
    private int startRow;
    private boolean statsLoaded;
    ImageReceiver thumbImage;
    private LruCache<ChartData> childDataCache = new LruCache<>(15);
    private ArrayList<TLRPC.Message> messages = new ArrayList<>();
    ArraySet<Integer> shadowDivideCells = new ArraySet<>();
    private final Runnable showProgressbar = new Runnable() { // from class: org.telegram.ui.MessageStatisticActivity.1
        @Override // java.lang.Runnable
        public void run() {
            MessageStatisticActivity.this.progressLayout.animate().alpha(1.0f).setDuration(230L);
        }
    };

    public MessageStatisticActivity(MessageObject message) {
        this.messageObject = message;
        if (message.messageOwner.fwd_from == null) {
            this.chatId = this.messageObject.getChatId();
            this.messageId = this.messageObject.getId();
        } else {
            this.chatId = -this.messageObject.getFromChatId();
            this.messageId = this.messageObject.messageOwner.fwd_msg_id;
        }
        this.chat = getMessagesController().getChatFull(this.chatId);
    }

    private void updateRows() {
        this.shadowDivideCells.clear();
        this.headerRow = -1;
        this.startRow = -1;
        this.endRow = -1;
        this.loadingRow = -1;
        this.interactionsChartRow = -1;
        this.overviewHeaderRow = -1;
        this.overviewRow = -1;
        this.rowCount = 0;
        if (this.firstLoaded && this.statsLoaded) {
            AndroidUtilities.cancelRunOnUIThread(this.showProgressbar);
            if (this.listContainer.getVisibility() == 8) {
                this.progressLayout.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.MessageStatisticActivity.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        MessageStatisticActivity.this.progressLayout.setVisibility(8);
                    }
                });
                this.listContainer.setVisibility(0);
                this.listContainer.setAlpha(0.0f);
                this.listContainer.animate().alpha(1.0f).start();
            }
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.overviewHeaderRow = i;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.overviewRow = i2;
            ArraySet<Integer> arraySet = this.shadowDivideCells;
            this.rowCount = i3 + 1;
            arraySet.add(Integer.valueOf(i3));
            if (this.interactionsViewData != null) {
                int i4 = this.rowCount;
                int i5 = i4 + 1;
                this.rowCount = i5;
                this.interactionsChartRow = i4;
                ArraySet<Integer> arraySet2 = this.shadowDivideCells;
                this.rowCount = i5 + 1;
                arraySet2.add(Integer.valueOf(i5));
            }
            if (!this.messages.isEmpty()) {
                int i6 = this.rowCount;
                int i7 = i6 + 1;
                this.rowCount = i7;
                this.headerRow = i6;
                this.startRow = i7;
                int size = i7 + this.messages.size();
                this.rowCount = size;
                this.endRow = size;
                int i8 = size + 1;
                this.rowCount = i8;
                this.emptyRow = size;
                ArraySet<Integer> arraySet3 = this.shadowDivideCells;
                this.rowCount = i8 + 1;
                arraySet3.add(Integer.valueOf(i8));
                if (!this.endReached) {
                    int i9 = this.rowCount;
                    this.rowCount = i9 + 1;
                    this.loadingRow = i9;
                }
            }
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (this.chat != null) {
            loadStat();
            loadChats(100);
        } else {
            MessagesController.getInstance(this.currentAccount).loadFullChat(this.chatId, this.classGuid, true);
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) args[0];
            if (this.chat == null && chatFull.id == this.chatId) {
                TLRPC.Chat chatLocal = getMessagesController().getChat(Long.valueOf(this.chatId));
                if (chatLocal != null) {
                    this.avatarContainer.setChatAvatar(chatLocal);
                    this.avatarContainer.setTitle(chatLocal.title);
                }
                this.chat = chatFull;
                loadStat();
                loadChats(100);
                updateMenu();
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        CharSequence message;
        boolean z;
        int size;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyView.setVisibility(8);
        LinearLayout linearLayout = new LinearLayout(context);
        this.progressLayout = linearLayout;
        linearLayout.setOrientation(1);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setAutoRepeat(true);
        this.imageView.setAnimation(R.raw.statistic_preload, 120, 120);
        this.imageView.playAnimation();
        TextView loadingTitle = new TextView(context);
        loadingTitle.setTextSize(1, 20.0f);
        loadingTitle.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        loadingTitle.setTextColor(Theme.getColor(Theme.key_player_actionBarTitle));
        loadingTitle.setTag(Theme.key_player_actionBarTitle);
        loadingTitle.setText(LocaleController.getString("LoadingStats", R.string.LoadingStats));
        loadingTitle.setGravity(1);
        TextView loadingSubtitle = new TextView(context);
        loadingSubtitle.setTextSize(1, 15.0f);
        loadingSubtitle.setTextColor(Theme.getColor(Theme.key_player_actionBarSubtitle));
        loadingSubtitle.setTag(Theme.key_player_actionBarSubtitle);
        loadingSubtitle.setText(LocaleController.getString("LoadingStatsDescription", R.string.LoadingStatsDescription));
        loadingSubtitle.setGravity(1);
        this.progressLayout.addView(this.imageView, LayoutHelper.createLinear(120, 120, 1, 0, 0, 0, 20));
        this.progressLayout.addView(loadingTitle, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 10));
        this.progressLayout.addView(loadingSubtitle, LayoutHelper.createLinear(-2, -2, 1));
        this.progressLayout.setAlpha(0.0f);
        frameLayout.addView(this.progressLayout, LayoutHelper.createFrame(PsExtractor.VIDEO_STREAM_MASK, -2.0f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        ((SimpleItemAnimator) this.listView.getItemAnimator()).setSupportsChangeAnimations(false);
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.MessageStatisticActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                MessageStatisticActivity.this.m3912lambda$createView$0$orgtelegramuiMessageStatisticActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.MessageStatisticActivity.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = MessageStatisticActivity.this.layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(MessageStatisticActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                if (visibleItemCount > 0 && !MessageStatisticActivity.this.endReached && !MessageStatisticActivity.this.loading && !MessageStatisticActivity.this.messages.isEmpty() && firstVisibleItem + visibleItemCount >= totalItemCount - 5 && MessageStatisticActivity.this.statsLoaded) {
                    MessageStatisticActivity.this.loadChats(100);
                }
            }
        });
        this.emptyView.showTextView();
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.listContainer = frameLayout2;
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listContainer.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listContainer.setVisibility(8);
        frameLayout.addView(this.listContainer, LayoutHelper.createFrame(-1, -1.0f));
        AndroidUtilities.runOnUIThread(this.showProgressbar, 300L);
        updateRows();
        this.listView.setEmptyView(this.emptyView);
        this.avatarContainer = new ChatAvatarContainer(context, null, false) { // from class: org.telegram.ui.MessageStatisticActivity.4
            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                MessageStatisticActivity.this.thumbImage.setImageCoords(MessageStatisticActivity.this.avatarContainer.getSubtitleTextView().getX(), MessageStatisticActivity.this.avatarContainer.getSubtitleTextView().getY(), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f));
                MessageStatisticActivity.this.thumbImage.draw(canvas);
                if (MessageStatisticActivity.this.drawPlay) {
                    int x = (int) (MessageStatisticActivity.this.thumbImage.getCenterX() - (Theme.dialogs_playDrawable.getIntrinsicWidth() / 2));
                    int y = (int) (MessageStatisticActivity.this.thumbImage.getCenterY() - (Theme.dialogs_playDrawable.getIntrinsicHeight() / 2));
                    Theme.dialogs_playDrawable.setBounds(x, y, Theme.dialogs_playDrawable.getIntrinsicWidth() + x, Theme.dialogs_playDrawable.getIntrinsicHeight() + y);
                    Theme.dialogs_playDrawable.draw(canvas);
                }
            }

            @Override // org.telegram.ui.Components.ChatAvatarContainer, android.view.ViewGroup, android.view.View
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                MessageStatisticActivity.this.thumbImage.onAttachedToWindow();
            }

            @Override // org.telegram.ui.Components.ChatAvatarContainer, android.view.ViewGroup, android.view.View
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                MessageStatisticActivity.this.thumbImage.onDetachedFromWindow();
            }
        };
        ImageReceiver imageReceiver = new ImageReceiver();
        this.thumbImage = imageReceiver;
        imageReceiver.setParentView(this.avatarContainer);
        this.thumbImage.setRoundRadius(AndroidUtilities.dp(2.0f));
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, !this.inPreviewMode ? 56.0f : 0.0f, 0.0f, 40.0f, 0.0f));
        TLRPC.Chat chatLocal = getMessagesController().getChat(Long.valueOf(this.chatId));
        if (chatLocal != null) {
            this.avatarContainer.setChatAvatar(chatLocal);
            this.avatarContainer.setTitle(chatLocal.title);
        }
        boolean hasThumb = false;
        if (!this.messageObject.needDrawBluredPreview() && (this.messageObject.isPhoto() || this.messageObject.isNewGif() || this.messageObject.isVideo())) {
            String type = this.messageObject.isWebpage() ? this.messageObject.messageOwner.media.webpage.type : null;
            if (!"app".equals(type) && !Scopes.PROFILE.equals(type) && !"article".equals(type)) {
                if (type == null || !type.startsWith("telegram_")) {
                    TLRPC.PhotoSize smallThumb = FileLoader.getClosestPhotoSizeWithSize(this.messageObject.photoThumbs, 40);
                    TLRPC.PhotoSize bigThumb = FileLoader.getClosestPhotoSizeWithSize(this.messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                    if (smallThumb == bigThumb) {
                        bigThumb = null;
                    }
                    if (smallThumb != null) {
                        hasThumb = true;
                        this.drawPlay = this.messageObject.isVideo();
                        String fileName = FileLoader.getAttachFileName(bigThumb);
                        if (!this.messageObject.mediaExists && !DownloadController.getInstance(this.currentAccount).canDownloadMedia(this.messageObject) && !FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName)) {
                            this.thumbImage.setImage((ImageLocation) null, (String) null, ImageLocation.getForObject(smallThumb, this.messageObject.photoThumbsObject), "20_20", (Drawable) null, this.messageObject, 0);
                        } else {
                            if (this.messageObject.type == 1) {
                                size = bigThumb != null ? bigThumb.size : 0;
                            } else {
                                size = 0;
                            }
                            this.thumbImage.setImage(ImageLocation.getForObject(bigThumb, this.messageObject.photoThumbsObject), "20_20", ImageLocation.getForObject(smallThumb, this.messageObject.photoThumbsObject), "20_20", size, null, this.messageObject, 0);
                        }
                    }
                }
            }
        }
        if (!TextUtils.isEmpty(this.messageObject.caption)) {
            message = this.messageObject.caption;
        } else if (!TextUtils.isEmpty(this.messageObject.messageOwner.message)) {
            CharSequence message2 = this.messageObject.messageText;
            if (message2.length() <= 150) {
                z = false;
            } else {
                z = false;
                message2 = message2.subSequence(0, 150);
            }
            message = Emoji.replaceEmoji(message2, this.avatarContainer.getSubtitleTextView().getTextPaint().getFontMetricsInt(), AndroidUtilities.dp(17.0f), z);
        } else {
            message = this.messageObject.messageText;
        }
        if (!hasThumb) {
            this.avatarContainer.setSubtitle(this.messageObject.messageText);
        } else {
            SpannableStringBuilder builder = new SpannableStringBuilder(message);
            builder.insert(0, (CharSequence) " ");
            builder.setSpan(new DialogCell.FixedWidthSpan(AndroidUtilities.dp(24.0f)), 0, 1, 33);
            this.avatarContainer.setSubtitle(builder);
        }
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.MessageStatisticActivity.5
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    MessageStatisticActivity.this.finishFragment();
                } else if (id == 1) {
                    Bundle args = new Bundle();
                    if (MessageStatisticActivity.this.messageObject.messageOwner.fwd_from == null) {
                        args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, MessageStatisticActivity.this.messageObject.getChatId());
                    } else {
                        args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -MessageStatisticActivity.this.messageObject.getFromChatId());
                    }
                    MessageStatisticActivity.this.presentFragment(new StatisticActivity(args));
                }
            }
        });
        this.avatarContainer.setTitleColors(Theme.getColor(Theme.key_player_actionBarTitle), Theme.getColor(Theme.key_player_actionBarSubtitle));
        this.avatarContainer.getSubtitleTextView().setLinkTextColor(Theme.getColor(Theme.key_player_actionBarSubtitle));
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), false);
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.avatarContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.MessageStatisticActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MessageStatisticActivity.this.m3913lambda$createView$1$orgtelegramuiMessageStatisticActivity(view);
            }
        });
        updateMenu();
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-MessageStatisticActivity */
    public /* synthetic */ void m3912lambda$createView$0$orgtelegramuiMessageStatisticActivity(View view, int position) {
        int i = this.startRow;
        if (position >= i && position < this.endRow) {
            TLRPC.Message message = this.messages.get(position - i);
            long did = MessageObject.getDialogId(message);
            Bundle args = new Bundle();
            if (DialogObject.isUserDialog(did)) {
                args.putLong("user_id", did);
            } else {
                args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -did);
            }
            args.putInt(Constants.MessagePayloadKeys.MSGID_SERVER, message.id);
            args.putBoolean("need_remove_previous_same_chat_activity", false);
            if (getMessagesController().checkCanOpenChat(args, this)) {
                presentFragment(new ChatActivity(args));
            }
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-MessageStatisticActivity */
    public /* synthetic */ void m3913lambda$createView$1$orgtelegramuiMessageStatisticActivity(View view) {
        if (getParentLayout().fragmentsStack.size() > 1) {
            BaseFragment previousFragemnt = getParentLayout().fragmentsStack.get(getParentLayout().fragmentsStack.size() - 2);
            if ((previousFragemnt instanceof ChatActivity) && ((ChatActivity) previousFragemnt).getCurrentChat().id == this.chatId) {
                finishFragment();
                return;
            }
        }
        Bundle args = new Bundle();
        args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chatId);
        args.putInt(Constants.MessagePayloadKeys.MSGID_SERVER, this.messageId);
        args.putBoolean("need_remove_previous_same_chat_activity", false);
        ChatActivity a = new ChatActivity(args);
        presentFragment(a);
    }

    private void updateMenu() {
        TLRPC.ChatFull chatFull = this.chat;
        if (chatFull != null && chatFull.can_view_stats) {
            ActionBarMenu menu = this.actionBar.createMenu();
            menu.clearItems();
            ActionBarMenuItem headerItem = menu.addItem(0, R.drawable.ic_ab_other);
            headerItem.addSubItem(1, R.drawable.msg_stats, LocaleController.getString("ViewChannelStats", R.string.ViewChannelStats));
        }
    }

    public void loadChats(int count) {
        if (this.loading) {
            return;
        }
        this.loading = true;
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        TLRPC.TL_stats_getMessagePublicForwards req = new TLRPC.TL_stats_getMessagePublicForwards();
        req.limit = count;
        if (this.messageObject.messageOwner.fwd_from != null) {
            req.msg_id = this.messageObject.messageOwner.fwd_from.saved_from_msg_id;
            req.channel = getMessagesController().getInputChannel(-this.messageObject.getFromChatId());
        } else {
            req.msg_id = this.messageObject.getId();
            req.channel = getMessagesController().getInputChannel(-this.messageObject.getDialogId());
        }
        if (!this.messages.isEmpty()) {
            ArrayList<TLRPC.Message> arrayList = this.messages;
            TLRPC.Message message = arrayList.get(arrayList.size() - 1);
            req.offset_id = message.id;
            req.offset_peer = getMessagesController().getInputPeer(MessageObject.getDialogId(message));
            req.offset_rate = this.nextRate;
        } else {
            req.offset_peer = new TLRPC.TL_inputPeerEmpty();
        }
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.MessageStatisticActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessageStatisticActivity.this.m3916lambda$loadChats$3$orgtelegramuiMessageStatisticActivity(tLObject, tL_error);
            }
        }, null, null, 0, this.chat.stats_dc, 1, true);
        getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
    }

    /* renamed from: lambda$loadChats$3$org-telegram-ui-MessageStatisticActivity */
    public /* synthetic */ void m3916lambda$loadChats$3$orgtelegramuiMessageStatisticActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.MessageStatisticActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MessageStatisticActivity.this.m3915lambda$loadChats$2$orgtelegramuiMessageStatisticActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$loadChats$2$org-telegram-ui-MessageStatisticActivity */
    public /* synthetic */ void m3915lambda$loadChats$2$orgtelegramuiMessageStatisticActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            TLRPC.messages_Messages res = (TLRPC.messages_Messages) response;
            if ((res.flags & 1) != 0) {
                this.nextRate = res.next_rate;
            }
            if (res.count != 0) {
                this.publicChats = res.count;
            } else if (this.publicChats == 0) {
                this.publicChats = res.messages.size();
            }
            this.endReached = !(res instanceof TLRPC.TL_messages_messagesSlice);
            getMessagesController().putChats(res.chats, false);
            getMessagesController().putUsers(res.users, false);
            this.messages.addAll(res.messages);
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
        }
        this.firstLoaded = true;
        this.loading = false;
        updateRows();
    }

    private void loadStat() {
        TLRPC.TL_stats_getMessageStats req = new TLRPC.TL_stats_getMessageStats();
        if (this.messageObject.messageOwner.fwd_from != null) {
            req.msg_id = this.messageObject.messageOwner.fwd_from.saved_from_msg_id;
            req.channel = getMessagesController().getInputChannel(-this.messageObject.getFromChatId());
        } else {
            req.msg_id = this.messageObject.getId();
            req.channel = getMessagesController().getInputChannel(-this.messageObject.getDialogId());
        }
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.MessageStatisticActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessageStatisticActivity.this.m3921lambda$loadStat$8$orgtelegramuiMessageStatisticActivity(tLObject, tL_error);
            }
        }, null, null, 0, this.chat.stats_dc, 1, true);
    }

    /* renamed from: lambda$loadStat$8$org-telegram-ui-MessageStatisticActivity */
    public /* synthetic */ void m3921lambda$loadStat$8$orgtelegramuiMessageStatisticActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.MessageStatisticActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                MessageStatisticActivity.this.m3920lambda$loadStat$7$orgtelegramuiMessageStatisticActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$loadStat$7$org-telegram-ui-MessageStatisticActivity */
    public /* synthetic */ void m3920lambda$loadStat$7$orgtelegramuiMessageStatisticActivity(TLRPC.TL_error error, TLObject response) {
        this.statsLoaded = true;
        if (error != null) {
            updateRows();
            return;
        }
        TLRPC.TL_stats_messageStats res = (TLRPC.TL_stats_messageStats) response;
        StatisticActivity.ChartViewData createViewData = StatisticActivity.createViewData(res.views_graph, LocaleController.getString("InteractionsChartTitle", R.string.InteractionsChartTitle), 1, false);
        this.interactionsViewData = createViewData;
        if (createViewData != null && createViewData.chartData.x.length <= 5) {
            this.statsLoaded = false;
            final TLRPC.TL_stats_loadAsyncGraph request = new TLRPC.TL_stats_loadAsyncGraph();
            request.token = this.interactionsViewData.zoomToken;
            request.x = this.interactionsViewData.chartData.x[this.interactionsViewData.chartData.x.length - 1];
            request.flags = 1 | request.flags;
            final String cacheKey = this.interactionsViewData.zoomToken + "_" + request.x;
            int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new RequestDelegate() { // from class: org.telegram.ui.MessageStatisticActivity$$ExternalSyntheticLambda7
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessageStatisticActivity.this.m3919lambda$loadStat$6$orgtelegramuiMessageStatisticActivity(cacheKey, request, tLObject, tL_error);
                }
            }, null, null, 0, this.chat.stats_dc, 1, true);
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, this.classGuid);
            return;
        }
        updateRows();
    }

    /* renamed from: lambda$loadStat$6$org-telegram-ui-MessageStatisticActivity */
    public /* synthetic */ void m3919lambda$loadStat$6$orgtelegramuiMessageStatisticActivity(final String cacheKey, final TLRPC.TL_stats_loadAsyncGraph request, final TLObject response1, final TLRPC.TL_error error1) {
        ChartData childData = null;
        if (response1 instanceof TLRPC.TL_statsGraph) {
            String json = ((TLRPC.TL_statsGraph) response1).json.data;
            try {
                childData = StatisticActivity.createChartData(new JSONObject(json), 1, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (response1 instanceof TLRPC.TL_statsGraphError) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.MessageStatisticActivity$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MessageStatisticActivity.this.m3917lambda$loadStat$4$orgtelegramuiMessageStatisticActivity(response1);
                }
            });
        }
        final ChartData finalChildData = childData;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.MessageStatisticActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                MessageStatisticActivity.this.m3918lambda$loadStat$5$orgtelegramuiMessageStatisticActivity(error1, finalChildData, cacheKey, request);
            }
        });
    }

    /* renamed from: lambda$loadStat$4$org-telegram-ui-MessageStatisticActivity */
    public /* synthetic */ void m3917lambda$loadStat$4$orgtelegramuiMessageStatisticActivity(TLObject response1) {
        if (getParentActivity() != null) {
            Toast.makeText(getParentActivity(), ((TLRPC.TL_statsGraphError) response1).error, 1).show();
        }
    }

    /* renamed from: lambda$loadStat$5$org-telegram-ui-MessageStatisticActivity */
    public /* synthetic */ void m3918lambda$loadStat$5$orgtelegramuiMessageStatisticActivity(TLRPC.TL_error error1, ChartData finalChildData, String cacheKey, TLRPC.TL_stats_loadAsyncGraph request) {
        this.statsLoaded = true;
        if (error1 != null || finalChildData == null) {
            updateRows();
            return;
        }
        this.childDataCache.put(cacheKey, finalChildData);
        this.interactionsViewData.childChartData = finalChildData;
        this.interactionsViewData.activeZoom = request.x;
        updateRows();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            MessageStatisticActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 0) {
                ManageChatUserCell cell = (ManageChatUserCell) holder.itemView;
                return cell.getCurrentObject() instanceof TLObject;
            }
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return MessageStatisticActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ManageChatUserCell(this.mContext, 6, 2, false);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    HeaderCell headerCell = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteBlueHeader, 16, 11, false);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    headerCell.setHeight(43);
                    view = headerCell;
                    break;
                case 3:
                default:
                    view = new LoadingCell(this.mContext, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(120.0f));
                    break;
                case 4:
                    view = new AnonymousClass1(this.mContext, 1, MessageStatisticActivity.this.sharedUi = new BaseChartView.SharedUiComponents());
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new OverviewCell(this.mContext);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 6:
                    view = new EmptyCell(this.mContext, 16);
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, 16));
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: org.telegram.ui.MessageStatisticActivity$ListAdapter$1 */
        /* loaded from: classes4.dex */
        public class AnonymousClass1 extends StatisticActivity.BaseChartCell {
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            AnonymousClass1(Context context, int type, BaseChartView.SharedUiComponents sharedUi) {
                super(context, type, sharedUi);
                ListAdapter.this = this$1;
            }

            @Override // org.telegram.ui.StatisticActivity.BaseChartCell
            public void onZoomed() {
                if (this.data.activeZoom > 0) {
                    return;
                }
                performClick();
                if (!this.chartView.legendSignatureView.canGoZoom) {
                    return;
                }
                long x = this.chartView.getSelectedDate();
                if (this.chartType == 4) {
                    this.data.childChartData = new StackLinearChartData(this.data.chartData, x);
                    zoomChart(false);
                } else if (this.data.zoomToken == null) {
                } else {
                    zoomCanceled();
                    final String cacheKey = this.data.zoomToken + "_" + x;
                    ChartData dataFromCache = (ChartData) MessageStatisticActivity.this.childDataCache.get(cacheKey);
                    if (dataFromCache != null) {
                        this.data.childChartData = dataFromCache;
                        zoomChart(false);
                        return;
                    }
                    TLRPC.TL_stats_loadAsyncGraph request = new TLRPC.TL_stats_loadAsyncGraph();
                    request.token = this.data.zoomToken;
                    if (x != 0) {
                        request.x = x;
                        request.flags |= 1;
                    }
                    MessageStatisticActivity messageStatisticActivity = MessageStatisticActivity.this;
                    final StatisticActivity.ZoomCancelable finalCancelabel = new StatisticActivity.ZoomCancelable();
                    messageStatisticActivity.lastCancelable = finalCancelabel;
                    finalCancelabel.adapterPosition = MessageStatisticActivity.this.listView.getChildAdapterPosition(this);
                    this.chartView.legendSignatureView.showProgress(true, false);
                    int reqId = ConnectionsManager.getInstance(MessageStatisticActivity.this.currentAccount).sendRequest(request, new RequestDelegate() { // from class: org.telegram.ui.MessageStatisticActivity$ListAdapter$1$$ExternalSyntheticLambda1
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            MessageStatisticActivity.ListAdapter.AnonymousClass1.this.m3923x966074d1(cacheKey, finalCancelabel, tLObject, tL_error);
                        }
                    }, null, null, 0, MessageStatisticActivity.this.chat.stats_dc, 1, true);
                    ConnectionsManager.getInstance(MessageStatisticActivity.this.currentAccount).bindRequestToGuid(reqId, MessageStatisticActivity.this.classGuid);
                }
            }

            /* renamed from: lambda$onZoomed$1$org-telegram-ui-MessageStatisticActivity$ListAdapter$1 */
            public /* synthetic */ void m3923x966074d1(final String cacheKey, final StatisticActivity.ZoomCancelable finalCancelabel, TLObject response, TLRPC.TL_error error) {
                ChartData childData = null;
                if (response instanceof TLRPC.TL_statsGraph) {
                    String json = ((TLRPC.TL_statsGraph) response).json.data;
                    try {
                        childData = StatisticActivity.createChartData(new JSONObject(json), this.data.graphType, false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (response instanceof TLRPC.TL_statsGraphError) {
                    Toast.makeText(getContext(), ((TLRPC.TL_statsGraphError) response).error, 1).show();
                }
                final ChartData finalChildData = childData;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.MessageStatisticActivity$ListAdapter$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessageStatisticActivity.ListAdapter.AnonymousClass1.this.m3922x952a21f2(finalChildData, cacheKey, finalCancelabel);
                    }
                });
            }

            /* renamed from: lambda$onZoomed$0$org-telegram-ui-MessageStatisticActivity$ListAdapter$1 */
            public /* synthetic */ void m3922x952a21f2(ChartData finalChildData, String cacheKey, StatisticActivity.ZoomCancelable finalCancelabel) {
                if (finalChildData != null) {
                    MessageStatisticActivity.this.childDataCache.put(cacheKey, finalChildData);
                }
                if (finalChildData != null && !finalCancelabel.canceled && finalCancelabel.adapterPosition >= 0) {
                    View view = MessageStatisticActivity.this.layoutManager.findViewByPosition(finalCancelabel.adapterPosition);
                    if (view instanceof StatisticActivity.BaseChartCell) {
                        this.data.childChartData = finalChildData;
                        ((StatisticActivity.BaseChartCell) view).chartView.legendSignatureView.showProgress(false, false);
                        ((StatisticActivity.BaseChartCell) view).zoomChart(false);
                    }
                }
                zoomCanceled();
            }

            @Override // org.telegram.ui.StatisticActivity.BaseChartCell
            public void zoomCanceled() {
                if (MessageStatisticActivity.this.lastCancelable != null) {
                    MessageStatisticActivity.this.lastCancelable.canceled = true;
                }
                int n = MessageStatisticActivity.this.listView.getChildCount();
                for (int i = 0; i < n; i++) {
                    View child = MessageStatisticActivity.this.listView.getChildAt(i);
                    if (child instanceof StatisticActivity.BaseChartCell) {
                        ((StatisticActivity.BaseChartCell) child).chartView.legendSignatureView.showProgress(false, true);
                    }
                }
            }

            @Override // org.telegram.ui.StatisticActivity.BaseChartCell
            void loadData(StatisticActivity.ChartViewData viewData) {
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLObject object;
            String status;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    TLRPC.Message item = getItem(position);
                    long did = MessageObject.getDialogId(item);
                    String status2 = null;
                    if (DialogObject.isUserDialog(did)) {
                        object = MessageStatisticActivity.this.getMessagesController().getUser(Long.valueOf(did));
                    } else {
                        object = MessageStatisticActivity.this.getMessagesController().getChat(Long.valueOf(-did));
                        TLRPC.Chat chat = (TLRPC.Chat) object;
                        if (chat.participants_count != 0) {
                            if (ChatObject.isChannel(chat) && !chat.megagroup) {
                                status = LocaleController.formatPluralString("Subscribers", chat.participants_count, new Object[0]);
                            } else {
                                status = LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]);
                            }
                            status2 = String.format("%1$s, %2$s", status, LocaleController.formatPluralString("Views", item.views, new Object[0]));
                        }
                    }
                    if (object != null) {
                        if (position != MessageStatisticActivity.this.endRow - 1) {
                            z = true;
                        }
                        userCell.setData(object, null, status2, z);
                        return;
                    }
                    return;
                case 1:
                    holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    return;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position != MessageStatisticActivity.this.overviewHeaderRow) {
                        headerCell.setText(LocaleController.formatPluralString("PublicSharesCount", MessageStatisticActivity.this.publicChats, new Object[0]));
                        return;
                    } else {
                        headerCell.setText(LocaleController.formatString("StatisticOverview", R.string.StatisticOverview, new Object[0]));
                        return;
                    }
                case 3:
                default:
                    return;
                case 4:
                    StatisticActivity.BaseChartCell chartCell = (StatisticActivity.BaseChartCell) holder.itemView;
                    chartCell.updateData(MessageStatisticActivity.this.interactionsViewData, false);
                    chartCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return;
                case 5:
                    OverviewCell overviewCell = (OverviewCell) holder.itemView;
                    overviewCell.setData();
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
            if (!MessageStatisticActivity.this.shadowDivideCells.contains(Integer.valueOf(position))) {
                if (position != MessageStatisticActivity.this.headerRow && position != MessageStatisticActivity.this.overviewHeaderRow) {
                    if (position != MessageStatisticActivity.this.loadingRow) {
                        if (position != MessageStatisticActivity.this.interactionsChartRow) {
                            if (position != MessageStatisticActivity.this.overviewRow) {
                                if (position == MessageStatisticActivity.this.emptyRow) {
                                    return 6;
                                }
                                return 0;
                            }
                            return 5;
                        }
                        return 4;
                    }
                    return 3;
                }
                return 2;
            }
            return 1;
        }

        public TLRPC.Message getItem(int position) {
            if (position >= MessageStatisticActivity.this.startRow && position < MessageStatisticActivity.this.endRow) {
                return (TLRPC.Message) MessageStatisticActivity.this.messages.get(position - MessageStatisticActivity.this.startRow);
            }
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public class OverviewCell extends LinearLayout {
        TextView[] primary = new TextView[3];
        TextView[] title = new TextView[3];
        View[] cell = new View[3];

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public OverviewCell(Context context) {
            super(context);
            MessageStatisticActivity.this = this$0;
            setOrientation(1);
            setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            for (int j = 0; j < 3; j++) {
                LinearLayout contentCell = new LinearLayout(context);
                this.cell[j] = contentCell;
                contentCell.setOrientation(1);
                this.primary[j] = new TextView(context);
                this.title[j] = new TextView(context);
                this.primary[j].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.primary[j].setTextSize(1, 17.0f);
                this.title[j].setTextSize(1, 13.0f);
                contentCell.addView(this.primary[j]);
                contentCell.addView(this.title[j]);
                linearLayout.addView(contentCell, LayoutHelper.createLinear(-1, -2, 1.0f));
            }
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f));
        }

        public void setData() {
            this.primary[0].setText(AndroidUtilities.formatWholeNumber(MessageStatisticActivity.this.messageObject.messageOwner.views, 0));
            this.title[0].setText(LocaleController.getString("StatisticViews", R.string.StatisticViews));
            if (MessageStatisticActivity.this.publicChats > 0) {
                this.cell[1].setVisibility(0);
                this.primary[1].setText(AndroidUtilities.formatWholeNumber(MessageStatisticActivity.this.publicChats, 0));
                this.title[1].setText(LocaleController.formatString("PublicShares", R.string.PublicShares, new Object[0]));
            } else {
                this.cell[1].setVisibility(8);
            }
            int privateChats = MessageStatisticActivity.this.messageObject.messageOwner.forwards - MessageStatisticActivity.this.publicChats;
            if (privateChats > 0) {
                this.cell[2].setVisibility(0);
                this.primary[2].setText(AndroidUtilities.formatWholeNumber(privateChats, 0));
                this.title[2].setText(LocaleController.formatString("PrivateShares", R.string.PrivateShares, new Object[0]));
            } else {
                this.cell[2].setVisibility(8);
            }
            updateColors();
        }

        public void updateColors() {
            for (int i = 0; i < 3; i++) {
                this.primary[i].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.title[i].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.MessageStatisticActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                MessageStatisticActivity.this.m3914x8921ff56();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, ManageChatUserCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        SimpleTextView simpleTextView = null;
        themeDescriptions.add(new ThemeDescription(chatAvatarContainer != null ? chatAvatarContainer.getTitleTextView() : null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_player_actionBarTitle));
        ChatAvatarContainer chatAvatarContainer2 = this.avatarContainer;
        if (chatAvatarContainer2 != null) {
            simpleTextView = chatAvatarContainer2.getSubtitleTextView();
        }
        themeDescriptions.add(new ThemeDescription(simpleTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBarSubtitle, (Object) null));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_statisticChartLineEmpty));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
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
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubmenuItemIcon));
        StatisticActivity.putColorFromData(this.interactionsViewData, themeDescriptions, cellDelegate);
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$9$org-telegram-ui-MessageStatisticActivity */
    public /* synthetic */ void m3914x8921ff56() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                recolorRecyclerItem(this.listView.getChildAt(a));
            }
            int count2 = this.listView.getHiddenChildCount();
            for (int a2 = 0; a2 < count2; a2++) {
                recolorRecyclerItem(this.listView.getHiddenChildAt(a2));
            }
            int count3 = this.listView.getCachedChildCount();
            for (int a3 = 0; a3 < count3; a3++) {
                recolorRecyclerItem(this.listView.getCachedChildAt(a3));
            }
            int count4 = this.listView.getAttachedScrapChildCount();
            for (int a4 = 0; a4 < count4; a4++) {
                recolorRecyclerItem(this.listView.getAttachedScrapChildAt(a4));
            }
            this.listView.getRecycledViewPool().clear();
        }
        BaseChartView.SharedUiComponents sharedUiComponents = this.sharedUi;
        if (sharedUiComponents != null) {
            sharedUiComponents.invalidate();
        }
        this.avatarContainer.getSubtitleTextView().setLinkTextColor(Theme.getColor(Theme.key_player_actionBarSubtitle));
    }

    private void recolorRecyclerItem(View child) {
        if (child instanceof ManageChatUserCell) {
            ((ManageChatUserCell) child).update(0);
        } else if (child instanceof StatisticActivity.BaseChartCell) {
            ((StatisticActivity.BaseChartCell) child).recolor();
            child.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        } else if (child instanceof ShadowSectionCell) {
            Drawable shadowDrawable = Theme.getThemedDrawable(ApplicationLoader.applicationContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
            Drawable background = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray));
            CombinedDrawable combinedDrawable = new CombinedDrawable(background, shadowDrawable, 0, 0);
            combinedDrawable.setFullsize(true);
            child.setBackground(combinedDrawable);
        } else if (child instanceof ChartHeaderView) {
            ((ChartHeaderView) child).recolor();
        } else if (child instanceof OverviewCell) {
            ((OverviewCell) child).updateColors();
            child.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
        if (child instanceof EmptyCell) {
            child.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        }
    }
}
