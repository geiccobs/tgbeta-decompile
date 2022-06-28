package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.messaging.Constants;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CallLogActivity;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.ContactsActivity;
/* loaded from: classes4.dex */
public class CallLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int TYPE_IN = 1;
    private static final int TYPE_MISSED = 2;
    private static final int TYPE_OUT = 0;
    private static final int delete = 2;
    private static final int delete_all_calls = 1;
    private ArrayList<Long> activeGroupCalls;
    private EmptyTextProgressView emptyView;
    private boolean endReached;
    private boolean firstLoaded;
    private FlickerLoadingView flickerLoadingView;
    private ImageView floatingButton;
    private boolean floatingHidden;
    private Drawable greenDrawable;
    private Drawable greenDrawable2;
    private ImageSpan iconIn;
    private ImageSpan iconMissed;
    private ImageSpan iconOut;
    private TLRPC.Chat lastCallChat;
    private TLRPC.User lastCallUser;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loading;
    private boolean openTransitionStarted;
    private ActionBarMenuItem otherItem;
    private int prevPosition;
    private int prevTop;
    private Drawable redDrawable;
    private boolean scrollUpdated;
    private NumberTextView selectedDialogsCountTextView;
    private Long waitingForCallChatId;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private ArrayList<CallLogRow> calls = new ArrayList<>();
    private ArrayList<Integer> selectedIds = new ArrayList<>();
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();

    /* loaded from: classes4.dex */
    public static class EmptyTextProgressView extends FrameLayout {
        private TextView emptyTextView1;
        private TextView emptyTextView2;
        private RLottieImageView imageView;
        private View progressView;

        public EmptyTextProgressView(Context context) {
            this(context, null);
        }

        public EmptyTextProgressView(Context context, View progressView) {
            super(context);
            addView(progressView, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView = progressView;
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(R.raw.utyan_call, 120, 120);
            this.imageView.setAutoRepeat(false);
            addView(this.imageView, LayoutHelper.createFrame(140, 140.0f, 17, 52.0f, 4.0f, 52.0f, 60.0f));
            this.imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CallLogActivity$EmptyTextProgressView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CallLogActivity.EmptyTextProgressView.this.m1605x52cf6592(view);
                }
            });
            TextView textView = new TextView(context);
            this.emptyTextView1 = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.emptyTextView1.setText(LocaleController.getString("NoRecentCalls", R.string.NoRecentCalls));
            this.emptyTextView1.setTextSize(1, 20.0f);
            this.emptyTextView1.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.emptyTextView1.setGravity(17);
            addView(this.emptyTextView1, LayoutHelper.createFrame(-1, -2.0f, 17, 17.0f, 40.0f, 17.0f, 0.0f));
            this.emptyTextView2 = new TextView(context);
            String help = LocaleController.getString("NoRecentCallsInfo", R.string.NoRecentCallsInfo);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                help = help.replace('\n', ' ');
            }
            this.emptyTextView2.setText(help);
            this.emptyTextView2.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
            this.emptyTextView2.setTextSize(1, 14.0f);
            this.emptyTextView2.setGravity(17);
            this.emptyTextView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.emptyTextView2, LayoutHelper.createFrame(-1, -2.0f, 17, 17.0f, 80.0f, 17.0f, 0.0f));
            progressView.setAlpha(0.0f);
            this.imageView.setAlpha(0.0f);
            this.emptyTextView1.setAlpha(0.0f);
            this.emptyTextView2.setAlpha(0.0f);
            setOnTouchListener(CallLogActivity$EmptyTextProgressView$$ExternalSyntheticLambda1.INSTANCE);
        }

        /* renamed from: lambda$new$0$org-telegram-ui-CallLogActivity$EmptyTextProgressView */
        public /* synthetic */ void m1605x52cf6592(View v) {
            if (!this.imageView.isPlaying()) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
        }

        public static /* synthetic */ boolean lambda$new$1(View v, MotionEvent event) {
            return true;
        }

        public void showProgress() {
            this.imageView.animate().alpha(0.0f).setDuration(150L).start();
            this.emptyTextView1.animate().alpha(0.0f).setDuration(150L).start();
            this.emptyTextView2.animate().alpha(0.0f).setDuration(150L).start();
            this.progressView.animate().alpha(1.0f).setDuration(150L).start();
        }

        public void showTextView() {
            this.imageView.animate().alpha(1.0f).setDuration(150L).start();
            this.emptyTextView1.animate().alpha(1.0f).setDuration(150L).start();
            this.emptyTextView2.animate().alpha(1.0f).setDuration(150L).start();
            this.progressView.animate().alpha(0.0f).setDuration(150L).start();
            this.imageView.playAnimation();
        }

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        Long l;
        ListAdapter listAdapter;
        int i = 1;
        int i2 = 0;
        if (id == NotificationCenter.didReceiveNewMessages) {
            if (!this.firstLoaded) {
                return;
            }
            boolean scheduled = ((Boolean) args[2]).booleanValue();
            if (scheduled) {
                return;
            }
            ArrayList<MessageObject> arr = (ArrayList) args[1];
            Iterator<MessageObject> it = arr.iterator();
            while (it.hasNext()) {
                MessageObject msg = it.next();
                if (msg.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall) {
                    long fromId = msg.getFromChatId();
                    long userID = fromId == getUserConfig().getClientUserId() ? msg.messageOwner.peer_id.user_id : fromId;
                    int callType = fromId == getUserConfig().getClientUserId() ? 0 : 1;
                    TLRPC.PhoneCallDiscardReason reason = msg.messageOwner.action.reason;
                    if (callType == i && ((reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed) || (reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy))) {
                        callType = 2;
                    }
                    if (this.calls.size() > 0) {
                        CallLogRow topRow = this.calls.get(0);
                        if (topRow.user.id == userID && topRow.type == callType) {
                            topRow.calls.add(0, msg.messageOwner);
                            this.listViewAdapter.notifyItemChanged(0);
                            i = 1;
                        }
                    }
                    CallLogRow row = new CallLogRow();
                    row.calls = new ArrayList<>();
                    row.calls.add(msg.messageOwner);
                    row.user = getMessagesController().getUser(Long.valueOf(userID));
                    row.type = callType;
                    row.video = msg.isVideoCall();
                    this.calls.add(0, row);
                    this.listViewAdapter.notifyItemInserted(0);
                }
                i = 1;
            }
            ActionBarMenuItem actionBarMenuItem = this.otherItem;
            if (actionBarMenuItem != null) {
                if (this.calls.isEmpty()) {
                    i2 = 8;
                }
                actionBarMenuItem.setVisibility(i2);
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            if (!this.firstLoaded) {
                return;
            }
            boolean scheduled2 = ((Boolean) args[2]).booleanValue();
            if (scheduled2) {
                return;
            }
            boolean didChange = false;
            ArrayList<Integer> ids = (ArrayList) args[0];
            Iterator<CallLogRow> itrtr = this.calls.iterator();
            while (itrtr.hasNext()) {
                CallLogRow row2 = itrtr.next();
                Iterator<TLRPC.Message> msgs = row2.calls.iterator();
                while (msgs.hasNext()) {
                    if (ids.contains(Integer.valueOf(msgs.next().id))) {
                        didChange = true;
                        msgs.remove();
                    }
                }
                if (row2.calls.size() == 0) {
                    itrtr.remove();
                }
            }
            if (didChange && (listAdapter = this.listViewAdapter) != null) {
                listAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.activeGroupCallsUpdated) {
            this.activeGroupCalls = getMessagesController().getActiveGroupCalls();
            ListAdapter listAdapter2 = this.listViewAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.chatInfoDidLoad) {
            if (this.waitingForCallChatId == null) {
                return;
            }
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) args[0];
            if (chatFull.id == this.waitingForCallChatId.longValue()) {
                ChatObject.Call groupCall = getMessagesController().getGroupCall(this.waitingForCallChatId.longValue(), true);
                if (groupCall != null) {
                    VoIPHelper.startCall(this.lastCallChat, null, null, false, getParentActivity(), this, getAccountInstance());
                    this.waitingForCallChatId = null;
                }
            }
        } else if (id != NotificationCenter.groupCallUpdated || (l = this.waitingForCallChatId) == null) {
        } else {
            Long chatId = (Long) args[0];
            if (l.equals(chatId)) {
                VoIPHelper.startCall(this.lastCallChat, null, null, false, getParentActivity(), this, getAccountInstance());
                this.waitingForCallChatId = null;
            }
        }
    }

    /* loaded from: classes4.dex */
    public class CallCell extends FrameLayout {
        private CheckBox2 checkBox;
        private ImageView imageView;
        private ProfileSearchCell profileSearchCell;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public CallCell(Context context) {
            super(context);
            CallLogActivity.this = r13;
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            ProfileSearchCell profileSearchCell = new ProfileSearchCell(context);
            this.profileSearchCell = profileSearchCell;
            profileSearchCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(32.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(32.0f), 0);
            this.profileSearchCell.setSublabelOffset(AndroidUtilities.dp(LocaleController.isRTL ? 2.0f : -2.0f), -AndroidUtilities.dp(4.0f));
            addView(this.profileSearchCell, LayoutHelper.createFrame(-1, -1.0f));
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setAlpha(214);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addButton), PorterDuff.Mode.MULTIPLY));
            this.imageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 1));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CallLogActivity$CallCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CallLogActivity.CallCell.this.m1604lambda$new$0$orgtelegramuiCallLogActivity$CallCell(view);
                }
            });
            this.imageView.setContentDescription(LocaleController.getString("Call", R.string.Call));
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 16, 8.0f, 0.0f, 8.0f, 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor(null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (!LocaleController.isRTL ? 3 : i) | 48, 42.0f, 32.0f, 42.0f, 0.0f));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-CallLogActivity$CallCell */
        public /* synthetic */ void m1604lambda$new$0$orgtelegramuiCallLogActivity$CallCell(View v) {
            CallLogRow row = (CallLogRow) v.getTag();
            TLRPC.UserFull userFull = CallLogActivity.this.getMessagesController().getUserFull(row.user.id);
            VoIPHelper.startCall(CallLogActivity.this.lastCallUser = row.user, row.video, row.video || (userFull != null && userFull.video_calls_available), CallLogActivity.this.getParentActivity(), null, CallLogActivity.this.getAccountInstance());
        }

        public void setChecked(boolean checked, boolean animated) {
            CheckBox2 checkBox2 = this.checkBox;
            if (checkBox2 == null) {
                return;
            }
            checkBox2.setChecked(checked, animated);
        }
    }

    /* loaded from: classes4.dex */
    public class GroupCallCell extends FrameLayout {
        private ProgressButton button;
        private TLRPC.Chat currentChat;
        private ProfileSearchCell profileSearchCell;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public GroupCallCell(Context context) {
            super(context);
            CallLogActivity.this = r10;
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            String text = LocaleController.getString("VoipChatJoin", R.string.VoipChatJoin);
            ProgressButton progressButton = new ProgressButton(context);
            this.button = progressButton;
            int width = (int) Math.ceil(progressButton.getPaint().measureText(text));
            ProfileSearchCell profileSearchCell = new ProfileSearchCell(context);
            this.profileSearchCell = profileSearchCell;
            profileSearchCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(44.0f) + width : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(44.0f) + width, 0);
            this.profileSearchCell.setSublabelOffset(0, -AndroidUtilities.dp(1.0f));
            addView(this.profileSearchCell, LayoutHelper.createFrame(-1, -1.0f));
            this.button.setText(text);
            this.button.setTextSize(1, 14.0f);
            this.button.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            this.button.setProgressColor(Theme.getColor(Theme.key_featuredStickers_buttonProgress));
            this.button.setBackgroundRoundRect(Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed), 16.0f);
            this.button.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
            addView(this.button, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f));
            this.button.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CallLogActivity$GroupCallCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CallLogActivity.GroupCallCell.this.m1606lambda$new$0$orgtelegramuiCallLogActivity$GroupCallCell(view);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-CallLogActivity$GroupCallCell */
        public /* synthetic */ void m1606lambda$new$0$orgtelegramuiCallLogActivity$GroupCallCell(View v) {
            Long tag = (Long) v.getTag();
            ChatObject.Call call = CallLogActivity.this.getMessagesController().getGroupCall(tag.longValue(), false);
            CallLogActivity callLogActivity = CallLogActivity.this;
            callLogActivity.lastCallChat = callLogActivity.getMessagesController().getChat(tag);
            if (call != null) {
                TLRPC.Chat chat = CallLogActivity.this.lastCallChat;
                Activity parentActivity = CallLogActivity.this.getParentActivity();
                CallLogActivity callLogActivity2 = CallLogActivity.this;
                VoIPHelper.startCall(chat, null, null, false, parentActivity, callLogActivity2, callLogActivity2.getAccountInstance());
                return;
            }
            CallLogActivity.this.waitingForCallChatId = tag;
            CallLogActivity.this.getMessagesController().loadFullChat(tag.longValue(), 0, true);
        }

        public void setChat(TLRPC.Chat chat) {
            this.currentChat = chat;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getCalls(0, 50);
        this.activeGroupCalls = getMessagesController().getActiveGroupCalls();
        getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(this, NotificationCenter.activeGroupCallsUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.groupCallUpdated);
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().removeObserver(this, NotificationCenter.activeGroupCallsUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        FrameLayout frameLayout;
        Drawable mutate = getParentActivity().getResources().getDrawable(R.drawable.ic_call_made_green_18dp).mutate();
        this.greenDrawable = mutate;
        mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), this.greenDrawable.getIntrinsicHeight());
        this.greenDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_calls_callReceivedGreenIcon), PorterDuff.Mode.MULTIPLY));
        this.iconOut = new ImageSpan(this.greenDrawable, 0);
        Drawable mutate2 = getParentActivity().getResources().getDrawable(R.drawable.ic_call_received_green_18dp).mutate();
        this.greenDrawable2 = mutate2;
        mutate2.setBounds(0, 0, mutate2.getIntrinsicWidth(), this.greenDrawable2.getIntrinsicHeight());
        this.greenDrawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_calls_callReceivedGreenIcon), PorterDuff.Mode.MULTIPLY));
        this.iconIn = new ImageSpan(this.greenDrawable2, 0);
        Drawable mutate3 = getParentActivity().getResources().getDrawable(R.drawable.ic_call_received_green_18dp).mutate();
        this.redDrawable = mutate3;
        mutate3.setBounds(0, 0, mutate3.getIntrinsicWidth(), this.redDrawable.getIntrinsicHeight());
        this.redDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_calls_callReceivedRedIcon), PorterDuff.Mode.MULTIPLY));
        this.iconMissed = new ImageSpan(this.redDrawable, 0);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Calls", R.string.Calls));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.CallLogActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (CallLogActivity.this.actionBar.isActionModeShowed()) {
                        CallLogActivity.this.hideActionMode(true);
                    } else {
                        CallLogActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    CallLogActivity.this.showDeleteAlert(true);
                } else if (id == 2) {
                    CallLogActivity.this.showDeleteAlert(false);
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem addItem = menu.addItem(10, R.drawable.ic_ab_other);
        this.otherItem = addItem;
        addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.otherItem.addSubItem(1, R.drawable.msg_delete, LocaleController.getString("DeleteAllCalls", R.string.DeleteAllCalls));
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView;
        flickerLoadingView.setViewType(8);
        this.flickerLoadingView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        this.flickerLoadingView.showDate(false);
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context, this.flickerLoadingView);
        this.emptyView = emptyTextProgressView;
        frameLayout2.addView(emptyTextProgressView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                CallLogActivity.this.m1594lambda$createView$0$orgtelegramuiCallLogActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda10
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return CallLogActivity.this.m1595lambda$createView$1$orgtelegramuiCallLogActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new AnonymousClass2());
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        ImageView imageView = new ImageView(context);
        this.floatingButton = imageView;
        imageView.setVisibility(0);
        this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(drawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.MULTIPLY));
        this.floatingButton.setImageResource(R.drawable.ic_call);
        this.floatingButton.setContentDescription(LocaleController.getString("Call", R.string.Call));
        if (Build.VERSION.SDK_INT < 21) {
            frameLayout = frameLayout2;
        } else {
            StateListAnimator animator = new StateListAnimator();
            frameLayout = frameLayout2;
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.floatingButton.setStateListAnimator(animator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.CallLogActivity.3
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        frameLayout.addView(this.floatingButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        this.floatingButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallLogActivity.this.m1597lambda$createView$3$orgtelegramuiCallLogActivity(view);
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-CallLogActivity */
    public /* synthetic */ void m1594lambda$createView$0$orgtelegramuiCallLogActivity(View view, int position) {
        if (!(view instanceof CallCell)) {
            if (view instanceof GroupCallCell) {
                GroupCallCell cell = (GroupCallCell) view;
                Bundle args = new Bundle();
                args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, cell.currentChat.id);
                getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args), true);
                return;
            }
            return;
        }
        CallLogRow row = this.calls.get(position - this.listViewAdapter.callsStartRow);
        if (this.actionBar.isActionModeShowed()) {
            addOrRemoveSelectedDialog(row.calls, (CallCell) view);
            return;
        }
        Bundle args2 = new Bundle();
        args2.putLong("user_id", row.user.id);
        args2.putInt(Constants.MessagePayloadKeys.MSGID_SERVER, row.calls.get(0).id);
        getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        presentFragment(new ChatActivity(args2), true);
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-CallLogActivity */
    public /* synthetic */ boolean m1595lambda$createView$1$orgtelegramuiCallLogActivity(View view, int position) {
        if (!(view instanceof CallCell)) {
            return false;
        }
        addOrRemoveSelectedDialog(this.calls.get(position - this.listViewAdapter.callsStartRow).calls, (CallCell) view);
        return true;
    }

    /* renamed from: org.telegram.ui.CallLogActivity$2 */
    /* loaded from: classes4.dex */
    public class AnonymousClass2 extends RecyclerView.OnScrollListener {
        AnonymousClass2() {
            CallLogActivity.this = this$0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            boolean goingDown;
            int firstVisibleItem = CallLogActivity.this.layoutManager.findFirstVisibleItemPosition();
            boolean z = false;
            int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(CallLogActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
            if (visibleItemCount > 0) {
                int totalItemCount = CallLogActivity.this.listViewAdapter.getItemCount();
                if (!CallLogActivity.this.endReached && !CallLogActivity.this.loading && !CallLogActivity.this.calls.isEmpty() && firstVisibleItem + visibleItemCount >= totalItemCount - 5) {
                    final CallLogRow row = (CallLogRow) CallLogActivity.this.calls.get(CallLogActivity.this.calls.size() - 1);
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CallLogActivity$2$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            CallLogActivity.AnonymousClass2.this.m1603lambda$onScrolled$0$orgtelegramuiCallLogActivity$2(row);
                        }
                    });
                }
            }
            if (CallLogActivity.this.floatingButton.getVisibility() != 8) {
                View topChild = recyclerView.getChildAt(0);
                int firstViewTop = 0;
                if (topChild != null) {
                    firstViewTop = topChild.getTop();
                }
                boolean changed = true;
                if (CallLogActivity.this.prevPosition == firstVisibleItem) {
                    int topDelta = CallLogActivity.this.prevTop - firstViewTop;
                    goingDown = firstViewTop < CallLogActivity.this.prevTop;
                    if (Math.abs(topDelta) > 1) {
                        z = true;
                    }
                    changed = z;
                } else {
                    if (firstVisibleItem > CallLogActivity.this.prevPosition) {
                        z = true;
                    }
                    goingDown = z;
                }
                if (changed && CallLogActivity.this.scrollUpdated) {
                    CallLogActivity.this.hideFloatingButton(goingDown);
                }
                CallLogActivity.this.prevPosition = firstVisibleItem;
                CallLogActivity.this.prevTop = firstViewTop;
                CallLogActivity.this.scrollUpdated = true;
            }
        }

        /* renamed from: lambda$onScrolled$0$org-telegram-ui-CallLogActivity$2 */
        public /* synthetic */ void m1603lambda$onScrolled$0$orgtelegramuiCallLogActivity$2(CallLogRow row) {
            CallLogActivity.this.getCalls(row.calls.get(row.calls.size() - 1).id, 100);
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-CallLogActivity */
    public /* synthetic */ void m1597lambda$createView$3$orgtelegramuiCallLogActivity(View v) {
        Bundle args = new Bundle();
        args.putBoolean("destroyAfterSelect", true);
        args.putBoolean("returnAsResult", true);
        args.putBoolean("onlyUsers", true);
        args.putBoolean("allowSelf", false);
        ContactsActivity contactsFragment = new ContactsActivity(args);
        contactsFragment.setDelegate(new ContactsActivity.ContactsActivityDelegate() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.ContactsActivity.ContactsActivityDelegate
            public final void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity) {
                CallLogActivity.this.m1596lambda$createView$2$orgtelegramuiCallLogActivity(user, str, contactsActivity);
            }
        });
        presentFragment(contactsFragment);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-CallLogActivity */
    public /* synthetic */ void m1596lambda$createView$2$orgtelegramuiCallLogActivity(TLRPC.User user, String param, ContactsActivity activity) {
        TLRPC.UserFull userFull = getMessagesController().getUserFull(user.id);
        this.lastCallUser = user;
        VoIPHelper.startCall(user, false, userFull != null && userFull.video_calls_available, getParentActivity(), null, getAccountInstance());
    }

    public void showDeleteAlert(final boolean all) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (all) {
            builder.setTitle(LocaleController.getString("DeleteAllCalls", R.string.DeleteAllCalls));
            builder.setMessage(LocaleController.getString("DeleteAllCallsText", R.string.DeleteAllCallsText));
        } else {
            builder.setTitle(LocaleController.getString("DeleteCalls", R.string.DeleteCalls));
            builder.setMessage(LocaleController.getString("DeleteSelectedCallsText", R.string.DeleteSelectedCallsText));
        }
        final boolean[] checks = {false};
        FrameLayout frameLayout = new FrameLayout(getParentActivity());
        CheckBoxCell cell = new CheckBoxCell(getParentActivity(), 1);
        cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        cell.setText(LocaleController.getString("DeleteCallsForEveryone", R.string.DeleteCallsForEveryone), "", false, false);
        cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(8.0f), 0);
        frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
        cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallLogActivity.lambda$showDeleteAlert$4(checks, view);
            }
        });
        builder.setView(frameLayout);
        builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                CallLogActivity.this.m1602lambda$showDeleteAlert$5$orgtelegramuiCallLogActivity(all, checks, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
        }
    }

    public static /* synthetic */ void lambda$showDeleteAlert$4(boolean[] checks, View v) {
        CheckBoxCell cell1 = (CheckBoxCell) v;
        checks[0] = !checks[0];
        cell1.setChecked(checks[0], true);
    }

    /* renamed from: lambda$showDeleteAlert$5$org-telegram-ui-CallLogActivity */
    public /* synthetic */ void m1602lambda$showDeleteAlert$5$orgtelegramuiCallLogActivity(boolean all, boolean[] checks, DialogInterface dialogInterface, int i) {
        if (!all) {
            getMessagesController().deleteMessages(new ArrayList<>(this.selectedIds), null, null, 0L, checks[0], false);
        } else {
            deleteAllMessages(checks[0]);
            this.calls.clear();
            this.loading = false;
            this.endReached = true;
            this.otherItem.setVisibility(8);
            this.listViewAdapter.notifyDataSetChanged();
        }
        hideActionMode(false);
    }

    private void deleteAllMessages(final boolean revoke) {
        TLRPC.TL_messages_deletePhoneCallHistory req = new TLRPC.TL_messages_deletePhoneCallHistory();
        req.revoke = revoke;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CallLogActivity.this.m1598lambda$deleteAllMessages$6$orgtelegramuiCallLogActivity(revoke, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$deleteAllMessages$6$org-telegram-ui-CallLogActivity */
    public /* synthetic */ void m1598lambda$deleteAllMessages$6$orgtelegramuiCallLogActivity(boolean revoke, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_messages_affectedFoundMessages res = (TLRPC.TL_messages_affectedFoundMessages) response;
            TLRPC.TL_updateDeleteMessages updateDeleteMessages = new TLRPC.TL_updateDeleteMessages();
            updateDeleteMessages.messages = res.messages;
            updateDeleteMessages.pts = res.pts;
            updateDeleteMessages.pts_count = res.pts_count;
            TLRPC.TL_updates updates = new TLRPC.TL_updates();
            updates.updates.add(updateDeleteMessages);
            getMessagesController().processUpdates(updates, false);
            if (res.offset != 0) {
                deleteAllMessages(revoke);
            }
        }
    }

    public void hideActionMode(boolean animated) {
        this.actionBar.hideActionMode();
        this.selectedIds.clear();
        int N = this.listView.getChildCount();
        for (int a = 0; a < N; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof CallCell) {
                ((CallCell) child).setChecked(false, animated);
            }
        }
    }

    public boolean isSelected(ArrayList<TLRPC.Message> messages) {
        int N = messages.size();
        for (int a = 0; a < N; a++) {
            if (this.selectedIds.contains(Integer.valueOf(messages.get(a).id))) {
                return true;
            }
        }
        return false;
    }

    private void createActionMode() {
        if (this.actionBar.actionModeIsExist(null)) {
            return;
        }
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
        this.selectedDialogsCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.selectedDialogsCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon));
        actionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedDialogsCountTextView.setOnTouchListener(CallLogActivity$$ExternalSyntheticLambda4.INSTANCE);
        this.actionModeViews.add(actionMode.addItemWithWidth(2, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", R.string.Delete)));
    }

    public static /* synthetic */ boolean lambda$createActionMode$7(View v, MotionEvent event) {
        return true;
    }

    private boolean addOrRemoveSelectedDialog(ArrayList<TLRPC.Message> messages, CallCell cell) {
        if (messages.isEmpty()) {
            return false;
        }
        if (isSelected(messages)) {
            int N = messages.size();
            for (int a = 0; a < N; a++) {
                this.selectedIds.remove(Integer.valueOf(messages.get(a).id));
            }
            cell.setChecked(false, true);
            showOrUpdateActionMode();
            return false;
        }
        int N2 = messages.size();
        for (int a2 = 0; a2 < N2; a2++) {
            Integer id = Integer.valueOf(messages.get(a2).id);
            if (!this.selectedIds.contains(id)) {
                this.selectedIds.add(id);
            }
        }
        cell.setChecked(true, true);
        showOrUpdateActionMode();
        return true;
    }

    private void showOrUpdateActionMode() {
        boolean updateAnimated = false;
        if (this.actionBar.isActionModeShowed()) {
            if (this.selectedIds.isEmpty()) {
                hideActionMode(true);
                return;
            }
            updateAnimated = true;
        } else {
            createActionMode();
            this.actionBar.showActionMode();
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            for (int a = 0; a < this.actionModeViews.size(); a++) {
                View view = this.actionModeViews.get(a);
                view.setPivotY(ActionBar.getCurrentActionBarHeight() / 2);
                AndroidUtilities.clearDrawableAnimation(view);
                animators.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.1f, 1.0f));
            }
            animatorSet.playTogether(animators);
            animatorSet.setDuration(200L);
            animatorSet.start();
        }
        this.selectedDialogsCountTextView.setNumber(this.selectedIds.size(), updateAnimated);
    }

    public void hideFloatingButton(boolean hide) {
        if (this.floatingHidden == hide) {
            return;
        }
        this.floatingHidden = hide;
        ImageView imageView = this.floatingButton;
        float[] fArr = new float[1];
        fArr[0] = hide ? AndroidUtilities.dp(100.0f) : 0.0f;
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "translationY", fArr).setDuration(300L);
        animator.setInterpolator(this.floatingInterpolator);
        this.floatingButton.setClickable(!hide);
        animator.start();
    }

    public void getCalls(int max_id, int count) {
        if (this.loading) {
            return;
        }
        this.loading = true;
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null && !this.firstLoaded) {
            emptyTextProgressView.showProgress();
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
        req.limit = count;
        req.peer = new TLRPC.TL_inputPeerEmpty();
        req.filter = new TLRPC.TL_inputMessagesFilterPhoneCalls();
        req.q = "";
        req.offset_id = max_id;
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                CallLogActivity.this.m1600lambda$getCalls$9$orgtelegramuiCallLogActivity(tLObject, tL_error);
            }
        }, 2);
        getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
    }

    /* renamed from: lambda$getCalls$9$org-telegram-ui-CallLogActivity */
    public /* synthetic */ void m1600lambda$getCalls$9$orgtelegramuiCallLogActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                CallLogActivity.this.m1599lambda$getCalls$8$orgtelegramuiCallLogActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$getCalls$8$org-telegram-ui-CallLogActivity */
    public /* synthetic */ void m1599lambda$getCalls$8$orgtelegramuiCallLogActivity(TLRPC.TL_error error, TLObject response) {
        CallLogRow currentRow;
        LongSparseArray<TLRPC.User> users;
        LongSparseArray<TLRPC.User> users2;
        int oldCount = Math.max(this.listViewAdapter.callsStartRow, 0) + this.calls.size();
        int i = 1;
        if (error != null) {
            this.endReached = true;
        } else {
            LongSparseArray<TLRPC.User> users3 = new LongSparseArray<>();
            TLRPC.messages_Messages msgs = (TLRPC.messages_Messages) response;
            this.endReached = msgs.messages.isEmpty();
            for (int a = 0; a < msgs.users.size(); a++) {
                TLRPC.User user = msgs.users.get(a);
                users3.put(user.id, user);
            }
            if (this.calls.size() > 0) {
                ArrayList<CallLogRow> arrayList = this.calls;
                currentRow = arrayList.get(arrayList.size() - 1);
            } else {
                currentRow = null;
            }
            int a2 = 0;
            while (a2 < msgs.messages.size()) {
                TLRPC.Message msg = msgs.messages.get(a2);
                if (msg.action == null) {
                    users = users3;
                } else if (msg.action instanceof TLRPC.TL_messageActionHistoryClear) {
                    users = users3;
                } else {
                    int callType = MessageObject.getFromChatId(msg) == getUserConfig().getClientUserId() ? 0 : 1;
                    TLRPC.PhoneCallDiscardReason reason = msg.action.reason;
                    if (callType == i && ((reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed) || (reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy))) {
                        callType = 2;
                    }
                    long fromId = MessageObject.getFromChatId(msg);
                    long userID = fromId == getUserConfig().getClientUserId() ? msg.peer_id.user_id : fromId;
                    if (currentRow != null) {
                        users2 = users3;
                        if (currentRow.user.id == userID && currentRow.type == callType) {
                            users = users2;
                            currentRow.calls.add(msg);
                        }
                    } else {
                        users2 = users3;
                    }
                    if (currentRow != null && !this.calls.contains(currentRow)) {
                        this.calls.add(currentRow);
                    }
                    CallLogRow row = new CallLogRow();
                    row.calls = new ArrayList<>();
                    users = users2;
                    row.user = users.get(userID);
                    row.type = callType;
                    row.video = msg.action != null && msg.action.video;
                    currentRow = row;
                    currentRow.calls.add(msg);
                }
                a2++;
                users3 = users;
                i = 1;
            }
            if (currentRow != null && currentRow.calls.size() > 0 && !this.calls.contains(currentRow)) {
                this.calls.add(currentRow);
            }
        }
        int i2 = 0;
        this.loading = false;
        showItemsAnimated(oldCount);
        if (!this.firstLoaded) {
            resumeDelayedFragmentAnimation();
        }
        this.firstLoaded = true;
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (this.calls.isEmpty()) {
            i2 = 8;
        }
        actionBarMenuItem.setVisibility(i2);
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.showTextView();
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101 || requestCode == 102 || requestCode == 103) {
            boolean allGranted = true;
            int a = 0;
            while (true) {
                if (a >= grantResults.length) {
                    break;
                } else if (grantResults[a] == 0) {
                    a++;
                } else {
                    allGranted = false;
                    break;
                }
            }
            int a2 = grantResults.length;
            TLRPC.UserFull userFull = null;
            if (a2 > 0 && allGranted) {
                if (requestCode == 103) {
                    VoIPHelper.startCall(this.lastCallChat, null, null, false, getParentActivity(), this, getAccountInstance());
                    return;
                }
                if (this.lastCallUser != null) {
                    userFull = getMessagesController().getUserFull(this.lastCallUser.id);
                }
                TLRPC.UserFull userFull2 = userFull;
                TLRPC.User user = this.lastCallUser;
                boolean z = true;
                boolean z2 = requestCode == 102;
                if (requestCode != 102 && (userFull2 == null || !userFull2.video_calls_available)) {
                    z = false;
                }
                VoIPHelper.startCall(user, z2, z, getParentActivity(), null, getAccountInstance());
                return;
            }
            VoIPHelper.permissionDenied(getParentActivity(), null, requestCode);
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private int activeEndRow;
        private int activeHeaderRow;
        private int activeStartRow;
        private int callsEndRow;
        private int callsHeaderRow;
        private int callsStartRow;
        private int loadingCallsRow;
        private Context mContext;
        private int rowsCount;
        private int sectionRow;

        public ListAdapter(Context context) {
            CallLogActivity.this = r1;
            this.mContext = context;
        }

        private void updateRows() {
            this.activeHeaderRow = -1;
            this.callsHeaderRow = -1;
            this.activeStartRow = -1;
            this.activeEndRow = -1;
            this.callsStartRow = -1;
            this.callsEndRow = -1;
            this.loadingCallsRow = -1;
            this.sectionRow = -1;
            this.rowsCount = 0;
            if (!CallLogActivity.this.activeGroupCalls.isEmpty()) {
                int i = this.rowsCount;
                int i2 = i + 1;
                this.rowsCount = i2;
                this.activeHeaderRow = i;
                this.activeStartRow = i2;
                int size = i2 + CallLogActivity.this.activeGroupCalls.size();
                this.rowsCount = size;
                this.activeEndRow = size;
            }
            if (!CallLogActivity.this.calls.isEmpty()) {
                if (this.activeHeaderRow != -1) {
                    int i3 = this.rowsCount;
                    int i4 = i3 + 1;
                    this.rowsCount = i4;
                    this.sectionRow = i3;
                    this.rowsCount = i4 + 1;
                    this.callsHeaderRow = i4;
                }
                int i5 = this.rowsCount;
                this.callsStartRow = i5;
                int size2 = i5 + CallLogActivity.this.calls.size();
                this.rowsCount = size2;
                this.callsEndRow = size2;
                if (!CallLogActivity.this.endReached) {
                    int i6 = this.rowsCount;
                    this.rowsCount = i6 + 1;
                    this.loadingCallsRow = i6;
                }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int position) {
            updateRows();
            super.notifyItemChanged(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int position, Object payload) {
            updateRows();
            super.notifyItemChanged(position, payload);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int positionStart, int itemCount, Object payload) {
            updateRows();
            super.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemInserted(int position) {
            updateRows();
            super.notifyItemInserted(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            super.notifyItemMoved(fromPosition, toPosition);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRemoved(int position) {
            updateRows();
            super.notifyItemRemoved(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 4;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.rowsCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new CallCell(this.mContext);
                    break;
                case 1:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(8);
                    flickerLoadingView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    flickerLoadingView.showDate(false);
                    view = flickerLoadingView;
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    break;
                case 3:
                    View view2 = new HeaderCell(this.mContext, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, 2, false, CallLogActivity.this.getResourceProvider());
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 4:
                    view = new GroupCallCell(this.mContext);
                    break;
                default:
                    view = new ShadowSectionCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof CallCell) {
                CallLogRow row = (CallLogRow) CallLogActivity.this.calls.get(holder.getAdapterPosition() - this.callsStartRow);
                ((CallCell) holder.itemView).setChecked(CallLogActivity.this.isSelected(row.calls), false);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SpannableString subtitle;
            String text;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    int position2 = position - this.callsStartRow;
                    CallLogRow row = (CallLogRow) CallLogActivity.this.calls.get(position2);
                    CallCell cell = (CallCell) holder.itemView;
                    cell.imageView.setImageResource(row.video ? R.drawable.profile_video : R.drawable.profile_phone);
                    TLRPC.Message last = row.calls.get(0);
                    String ldir = LocaleController.isRTL ? "\u202b" : "";
                    if (row.calls.size() == 1) {
                        subtitle = new SpannableString(ldir + "  " + LocaleController.formatDateCallLog(last.date));
                    } else {
                        subtitle = new SpannableString(String.format(ldir + "  (%d) %s", Integer.valueOf(row.calls.size()), LocaleController.formatDateCallLog(last.date)));
                    }
                    switch (row.type) {
                        case 0:
                            subtitle.setSpan(CallLogActivity.this.iconOut, ldir.length(), ldir.length() + 1, 0);
                            break;
                        case 1:
                            subtitle.setSpan(CallLogActivity.this.iconIn, ldir.length(), ldir.length() + 1, 0);
                            break;
                        case 2:
                            subtitle.setSpan(CallLogActivity.this.iconMissed, ldir.length(), ldir.length() + 1, 0);
                            break;
                    }
                    cell.profileSearchCell.setData(row.user, null, null, subtitle, false, false);
                    ProfileSearchCell profileSearchCell = cell.profileSearchCell;
                    if (position2 != CallLogActivity.this.calls.size() - 1 || !CallLogActivity.this.endReached) {
                        z = true;
                    }
                    profileSearchCell.useSeparator = z;
                    cell.imageView.setTag(row);
                    return;
                case 1:
                case 2:
                default:
                    return;
                case 3:
                    HeaderCell cell2 = (HeaderCell) holder.itemView;
                    if (position == this.activeHeaderRow) {
                        cell2.setText(LocaleController.getString("VoipChatActiveChats", R.string.VoipChatActiveChats));
                        return;
                    } else if (position == this.callsHeaderRow) {
                        cell2.setText(LocaleController.getString("VoipChatRecentCalls", R.string.VoipChatRecentCalls));
                        return;
                    } else {
                        return;
                    }
                case 4:
                    int position3 = position - this.activeStartRow;
                    Long chatId = (Long) CallLogActivity.this.activeGroupCalls.get(position3);
                    TLRPC.Chat chat = CallLogActivity.this.getMessagesController().getChat(chatId);
                    GroupCallCell cell3 = (GroupCallCell) holder.itemView;
                    cell3.setChat(chat);
                    cell3.button.setTag(Long.valueOf(chat.id));
                    if (ChatObject.isChannel(chat) && !chat.megagroup) {
                        if (TextUtils.isEmpty(chat.username)) {
                            text = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                        } else {
                            text = LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase();
                        }
                    } else if (chat.has_geo) {
                        text = LocaleController.getString("MegaLocation", R.string.MegaLocation);
                    } else {
                        String text2 = chat.username;
                        if (TextUtils.isEmpty(text2)) {
                            text = LocaleController.getString("MegaPrivate", R.string.MegaPrivate).toLowerCase();
                        } else {
                            text = LocaleController.getString("MegaPublic", R.string.MegaPublic).toLowerCase();
                        }
                    }
                    ProfileSearchCell profileSearchCell2 = cell3.profileSearchCell;
                    if (position3 != CallLogActivity.this.activeGroupCalls.size() - 1 && !CallLogActivity.this.endReached) {
                        z = true;
                    }
                    profileSearchCell2.useSeparator = z;
                    cell3.profileSearchCell.setData(chat, null, null, text, false, false);
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == this.activeHeaderRow || i == this.callsHeaderRow) {
                return 3;
            }
            if (i >= this.callsStartRow && i < this.callsEndRow) {
                return 0;
            }
            if (i >= this.activeStartRow && i < this.activeEndRow) {
                return 4;
            }
            if (i == this.loadingCallsRow) {
                return 1;
            }
            if (i == this.sectionRow) {
                return 5;
            }
            return 2;
        }
    }

    /* loaded from: classes4.dex */
    public static class CallLogRow {
        public ArrayList<TLRPC.Message> calls;
        public int type;
        public TLRPC.User user;
        public boolean video;

        private CallLogRow() {
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        super.onTransitionAnimationStart(isOpen, backward);
        if (isOpen) {
            this.openTransitionStarted = true;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean needDelayOpenAnimation() {
        return true;
    }

    private void showItemsAnimated(final int from) {
        if (this.isPaused || !this.openTransitionStarted) {
            return;
        }
        View progressView = null;
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View child = this.listView.getChildAt(i);
            if (child instanceof FlickerLoadingView) {
                progressView = child;
            }
        }
        final View finalProgressView = progressView;
        if (progressView != null) {
            this.listView.removeView(progressView);
        }
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.CallLogActivity.4
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                CallLogActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int n = CallLogActivity.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i2 = 0; i2 < n; i2++) {
                    View child2 = CallLogActivity.this.listView.getChildAt(i2);
                    RecyclerView.ViewHolder holder = CallLogActivity.this.listView.getChildViewHolder(child2);
                    if (child2 != finalProgressView && CallLogActivity.this.listView.getChildAdapterPosition(child2) >= from && !(child2 instanceof GroupCallCell) && (!(child2 instanceof HeaderCell) || holder.getAdapterPosition() != CallLogActivity.this.listViewAdapter.activeHeaderRow)) {
                        child2.setAlpha(0.0f);
                        int s = Math.min(CallLogActivity.this.listView.getMeasuredHeight(), Math.max(0, child2.getTop()));
                        int delay = (int) ((s / CallLogActivity.this.listView.getMeasuredHeight()) * 100.0f);
                        ObjectAnimator a = ObjectAnimator.ofFloat(child2, View.ALPHA, 0.0f, 1.0f);
                        a.setStartDelay(delay);
                        a.setDuration(200L);
                        animatorSet.playTogether(a);
                    }
                }
                View view = finalProgressView;
                if (view != null && view.getParent() == null) {
                    CallLogActivity.this.listView.addView(finalProgressView);
                    final RecyclerView.LayoutManager layoutManager = CallLogActivity.this.listView.getLayoutManager();
                    if (layoutManager != null) {
                        layoutManager.ignoreView(finalProgressView);
                        Animator animator = ObjectAnimator.ofFloat(finalProgressView, View.ALPHA, finalProgressView.getAlpha(), 0.0f);
                        animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.CallLogActivity.4.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                finalProgressView.setAlpha(1.0f);
                                layoutManager.stopIgnoringView(finalProgressView);
                                CallLogActivity.this.listView.removeView(finalProgressView);
                            }
                        });
                        animator.start();
                    }
                }
                animatorSet.start();
                return true;
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                CallLogActivity.this.m1601lambda$getThemeDescriptions$10$orgtelegramuiCallLogActivity();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LocationCell.class, CallCell.class, HeaderCell.class, GroupCallCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyTextProgressView.class}, new String[]{"emptyTextView1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyTextProgressView.class}, new String[]{"emptyTextView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_emptyListPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon));
        themeDescriptions.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground));
        themeDescriptions.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_addButton));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, Theme.dialogs_onlinePaint, null, null, Theme.key_windowBackgroundWhiteBlueText3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint[0], Theme.dialogs_namePaint[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_name));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint[0], Theme.dialogs_nameEncryptedPaint[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_secretName));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{this.greenDrawable, this.greenDrawable2, Theme.calllog_msgCallUpRedDrawable, Theme.calllog_msgCallDownRedDrawable}, null, Theme.key_calls_callReceivedGreenIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{this.redDrawable, Theme.calllog_msgCallUpGreenDrawable, Theme.calllog_msgCallDownGreenDrawable}, null, Theme.key_calls_callReceivedRedIcon));
        themeDescriptions.add(new ThemeDescription(this.flickerLoadingView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$10$org-telegram-ui-CallLogActivity */
    public /* synthetic */ void m1601lambda$getThemeDescriptions$10$orgtelegramuiCallLogActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof CallCell) {
                    CallCell cell = (CallCell) child;
                    cell.profileSearchCell.update(0);
                }
            }
        }
    }
}
