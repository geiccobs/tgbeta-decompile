package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareLocationDrawable;
import org.telegram.ui.Components.UndoView;
/* loaded from: classes4.dex */
public class PeopleNearbyActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, LocationController.LocationFetchCallback {
    private static final int SHORT_POLL_TIMEOUT = 25000;
    private AnimatorSet actionBarAnimator;
    private View actionBarBackground;
    private boolean canCreateGroup;
    private int chatsCreateRow;
    private int chatsEndRow;
    private int chatsHeaderRow;
    private int chatsSectionRow;
    private int chatsStartRow;
    private Runnable checkExpiredRunnable;
    private boolean checkingCanCreate;
    private int currentChatId;
    private String currentGroupCreateAddress;
    private String currentGroupCreateDisplayAddress;
    private Location currentGroupCreateLocation;
    private boolean expanded;
    private boolean firstLoaded;
    private ActionIntroActivity groupCreateActivity;
    private int helpRow;
    private int helpSectionRow;
    private DefaultItemAnimator itemAnimator;
    private Location lastLoadedLocation;
    private long lastLoadedLocationTime;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private AlertDialog loadingDialog;
    private int reqId;
    private int rowCount;
    private int showMeRow;
    private int showMoreRow;
    private AnimatorSet showProgressAnimation;
    private Runnable showProgressRunnable;
    private boolean showingLoadingProgress;
    private boolean showingMe;
    private UndoView undoView;
    private int usersEndRow;
    private int usersHeaderRow;
    private int usersSectionRow;
    private int usersStartRow;
    private ArrayList<View> animatingViews = new ArrayList<>();
    private Runnable shortPollRunnable = new Runnable() { // from class: org.telegram.ui.PeopleNearbyActivity.1
        @Override // java.lang.Runnable
        public void run() {
            if (PeopleNearbyActivity.this.shortPollRunnable != null) {
                PeopleNearbyActivity.this.sendRequest(true, 0);
                AndroidUtilities.cancelRunOnUIThread(PeopleNearbyActivity.this.shortPollRunnable);
                AndroidUtilities.runOnUIThread(PeopleNearbyActivity.this.shortPollRunnable, 25000L);
            }
        }
    };
    private int[] location = new int[2];
    private ArrayList<TLRPC.TL_peerLocated> users = new ArrayList<>(getLocationController().getCachedNearbyUsers());
    private ArrayList<TLRPC.TL_peerLocated> chats = new ArrayList<>(getLocationController().getCachedNearbyChats());

    public PeopleNearbyActivity() {
        checkForExpiredLocations(false);
        updateRows(null);
    }

    private void updateRows(DiffCallback diffCallback) {
        int count;
        this.rowCount = 0;
        this.usersStartRow = -1;
        this.usersEndRow = -1;
        this.showMoreRow = -1;
        this.chatsStartRow = -1;
        this.chatsEndRow = -1;
        this.chatsCreateRow = -1;
        this.showMeRow = -1;
        int i = 0 + 1;
        this.rowCount = i;
        this.helpRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.helpSectionRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.usersHeaderRow = i2;
        this.rowCount = i3 + 1;
        this.showMeRow = i3;
        if (!this.users.isEmpty()) {
            if (this.expanded) {
                count = this.users.size();
            } else {
                count = Math.min(5, this.users.size());
            }
            int i4 = this.rowCount;
            this.usersStartRow = i4;
            int i5 = i4 + count;
            this.rowCount = i5;
            this.usersEndRow = i5;
            if (count != this.users.size()) {
                int i6 = this.rowCount;
                this.rowCount = i6 + 1;
                this.showMoreRow = i6;
            }
        }
        int count2 = this.rowCount;
        int i7 = count2 + 1;
        this.rowCount = i7;
        this.usersSectionRow = count2;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.chatsHeaderRow = i7;
        this.rowCount = i8 + 1;
        this.chatsCreateRow = i8;
        if (!this.chats.isEmpty()) {
            int i9 = this.rowCount;
            this.chatsStartRow = i9;
            int size = i9 + this.chats.size();
            this.rowCount = size;
            this.chatsEndRow = size;
        }
        int i10 = this.rowCount;
        this.rowCount = i10 + 1;
        this.chatsSectionRow = i10;
        if (this.listViewAdapter != null) {
            if (diffCallback == null) {
                this.listView.setItemAnimator(null);
                this.listViewAdapter.notifyDataSetChanged();
                return;
            }
            this.listView.setItemAnimator(this.itemAnimator);
            diffCallback.fillPositions(diffCallback.newPositionToItem);
            DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo(this.listViewAdapter);
        }
    }

    /* loaded from: classes4.dex */
    public class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        private final ArrayList<TLRPC.TL_peerLocated> oldChats;
        int oldChatsEndRow;
        int oldChatsStartRow;
        SparseIntArray oldPositionToItem;
        int oldRowCount;
        private final ArrayList<TLRPC.TL_peerLocated> oldUsers;
        int oldUsersEndRow;
        int oldUsersStartRow;

        private DiffCallback() {
            PeopleNearbyActivity.this = r1;
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
            this.oldUsers = new ArrayList<>();
            this.oldChats = new ArrayList<>();
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getOldListSize() {
            return this.oldRowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getNewListSize() {
            return PeopleNearbyActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            int i;
            int i2;
            if (newItemPosition >= PeopleNearbyActivity.this.usersStartRow && newItemPosition < PeopleNearbyActivity.this.usersEndRow && oldItemPosition >= (i2 = this.oldUsersStartRow) && oldItemPosition < this.oldUsersEndRow) {
                return MessageObject.getPeerId(this.oldUsers.get(oldItemPosition - i2).peer) == MessageObject.getPeerId(((TLRPC.TL_peerLocated) PeopleNearbyActivity.this.users.get(newItemPosition - PeopleNearbyActivity.this.usersStartRow)).peer);
            } else if (newItemPosition >= PeopleNearbyActivity.this.chatsStartRow && newItemPosition < PeopleNearbyActivity.this.chatsEndRow && oldItemPosition >= (i = this.oldChatsStartRow) && oldItemPosition < this.oldChatsEndRow) {
                return MessageObject.getPeerId(this.oldChats.get(oldItemPosition - i).peer) == MessageObject.getPeerId(((TLRPC.TL_peerLocated) PeopleNearbyActivity.this.chats.get(newItemPosition - PeopleNearbyActivity.this.chatsStartRow)).peer);
            } else {
                int oldIndex = this.oldPositionToItem.get(oldItemPosition, -1);
                int newIndex = this.newPositionToItem.get(newItemPosition, -1);
                return oldIndex == newIndex && oldIndex >= 0;
            }
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return areItemsTheSame(oldItemPosition, newItemPosition);
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            int pointer = 0 + 1;
            put(pointer, PeopleNearbyActivity.this.helpRow, sparseIntArray);
            int pointer2 = pointer + 1;
            put(pointer2, PeopleNearbyActivity.this.helpSectionRow, sparseIntArray);
            int pointer3 = pointer2 + 1;
            put(pointer3, PeopleNearbyActivity.this.usersHeaderRow, sparseIntArray);
            int pointer4 = pointer3 + 1;
            put(pointer4, PeopleNearbyActivity.this.showMoreRow, sparseIntArray);
            int pointer5 = pointer4 + 1;
            put(pointer5, PeopleNearbyActivity.this.usersSectionRow, sparseIntArray);
            int pointer6 = pointer5 + 1;
            put(pointer6, PeopleNearbyActivity.this.chatsHeaderRow, sparseIntArray);
            int pointer7 = pointer6 + 1;
            put(pointer7, PeopleNearbyActivity.this.chatsCreateRow, sparseIntArray);
            int pointer8 = pointer7 + 1;
            put(pointer8, PeopleNearbyActivity.this.chatsSectionRow, sparseIntArray);
            put(pointer8 + 1, PeopleNearbyActivity.this.showMeRow, sparseIntArray);
        }

        public void saveCurrentState() {
            this.oldRowCount = PeopleNearbyActivity.this.rowCount;
            this.oldUsersStartRow = PeopleNearbyActivity.this.usersStartRow;
            this.oldUsersEndRow = PeopleNearbyActivity.this.usersEndRow;
            this.oldChatsStartRow = PeopleNearbyActivity.this.chatsStartRow;
            this.oldChatsEndRow = PeopleNearbyActivity.this.chatsEndRow;
            this.oldUsers.addAll(PeopleNearbyActivity.this.users);
            this.oldChats.addAll(PeopleNearbyActivity.this.chats);
            fillPositions(this.oldPositionToItem);
        }

        private void put(int id, int position, SparseIntArray sparseIntArray) {
            if (position >= 0) {
                sparseIntArray.put(position, id);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.newLocationAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.newPeopleNearbyAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.needDeleteDialog);
        checkCanCreateGroup();
        sendRequest(false, 0);
        AndroidUtilities.runOnUIThread(this.shortPollRunnable, 25000L);
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.newLocationAvailable);
        getNotificationCenter().removeObserver(this, NotificationCenter.newPeopleNearbyAvailable);
        getNotificationCenter().removeObserver(this, NotificationCenter.needDeleteDialog);
        Runnable runnable = this.shortPollRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.shortPollRunnable = null;
        }
        Runnable runnable2 = this.checkExpiredRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.checkExpiredRunnable = null;
        }
        Runnable runnable3 = this.showProgressRunnable;
        if (runnable3 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable3);
            this.showProgressRunnable = null;
        }
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setBackgroundDrawable(null);
        this.actionBar.setTitleColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_listSelector), false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        int i = 1;
        this.actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet());
        this.actionBar.setTitle(LocaleController.getString("PeopleNearby", R.string.PeopleNearby));
        this.actionBar.getTitleTextView().setAlpha(0.0f);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.PeopleNearbyActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    PeopleNearbyActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context) { // from class: org.telegram.ui.PeopleNearbyActivity.3
            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) PeopleNearbyActivity.this.actionBarBackground.getLayoutParams();
                layoutParams.height = ActionBar.getCurrentActionBarHeight() + (PeopleNearbyActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(3.0f);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                PeopleNearbyActivity.this.checkScroll(false);
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.fragmentView.setTag(Theme.key_windowBackgroundGray);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setGlowColor(0);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        RecyclerListView recyclerListView4 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView4.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.itemAnimator = new DefaultItemAnimator() { // from class: org.telegram.ui.PeopleNearbyActivity.4
            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected long getAddAnimationDelay(long removeDuration, long moveDuration, long changeDuration) {
                return removeDuration;
            }
        };
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.PeopleNearbyActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                PeopleNearbyActivity.this.m4166lambda$createView$2$orgtelegramuiPeopleNearbyActivity(view, i2);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.PeopleNearbyActivity.5
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                PeopleNearbyActivity.this.checkScroll(true);
            }
        });
        View view = new View(context) { // from class: org.telegram.ui.PeopleNearbyActivity.6
            private Paint paint = new Paint();

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                int h = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), h, this.paint);
                PeopleNearbyActivity.this.parentLayout.drawHeaderShadow(canvas, h);
            }
        };
        this.actionBarBackground = view;
        view.setAlpha(0.0f);
        frameLayout.addView(this.actionBarBackground, LayoutHelper.createFrame(-1, -2.0f));
        frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        UndoView undoView = new UndoView(context);
        this.undoView = undoView;
        frameLayout.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateRows(null);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PeopleNearbyActivity */
    public /* synthetic */ void m4166lambda$createView$2$orgtelegramuiPeopleNearbyActivity(View view, int position) {
        long chatId;
        if (getParentActivity() == null) {
            return;
        }
        int i = this.usersStartRow;
        if (position >= i && position < this.usersEndRow) {
            if (view instanceof ManageChatUserCell) {
                ManageChatUserCell cell = (ManageChatUserCell) view;
                TLRPC.TL_peerLocated peerLocated = this.users.get(position - i);
                Bundle args1 = new Bundle();
                args1.putLong("user_id", peerLocated.peer.user_id);
                if (cell.hasAvatarSet()) {
                    args1.putBoolean("expandPhoto", true);
                }
                args1.putInt("nearby_distance", peerLocated.distance);
                MessagesController.getInstance(this.currentAccount).ensureMessagesLoaded(peerLocated.peer.user_id, 0, null);
                presentFragment(new ProfileActivity(args1));
                return;
            }
            return;
        }
        int i2 = this.chatsStartRow;
        if (position >= i2 && position < this.chatsEndRow) {
            TLRPC.TL_peerLocated peerLocated2 = this.chats.get(position - i2);
            Bundle args12 = new Bundle();
            if (peerLocated2.peer instanceof TLRPC.TL_peerChat) {
                chatId = peerLocated2.peer.chat_id;
            } else {
                chatId = peerLocated2.peer.channel_id;
            }
            args12.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, chatId);
            ChatActivity chatActivity = new ChatActivity(args12);
            presentFragment(chatActivity);
        } else if (position == this.chatsCreateRow) {
            if (this.checkingCanCreate || this.currentGroupCreateAddress == null) {
                AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                this.loadingDialog = alertDialog;
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.PeopleNearbyActivity$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnCancelListener
                    public final void onCancel(DialogInterface dialogInterface) {
                        PeopleNearbyActivity.this.m4164lambda$createView$0$orgtelegramuiPeopleNearbyActivity(dialogInterface);
                    }
                });
                this.loadingDialog.show();
                return;
            }
            openGroupCreate();
        } else if (position == this.showMeRow) {
            final UserConfig userConfig = getUserConfig();
            if (this.showingMe) {
                userConfig.sharingMyLocationUntil = 0;
                userConfig.saveConfig(false);
                sendRequest(false, 2);
                updateRows(null);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("MakeMyselfVisibleTitle", R.string.MakeMyselfVisibleTitle));
                builder.setMessage(LocaleController.getString("MakeMyselfVisibleInfo", R.string.MakeMyselfVisibleInfo));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PeopleNearbyActivity$$ExternalSyntheticLambda2
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        PeopleNearbyActivity.this.m4165lambda$createView$1$orgtelegramuiPeopleNearbyActivity(userConfig, dialogInterface, i3);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            }
            userConfig.saveConfig(false);
        } else if (position == this.showMoreRow) {
            this.expanded = true;
            DiffCallback diffCallback = new DiffCallback();
            diffCallback.saveCurrentState();
            updateRows(diffCallback);
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PeopleNearbyActivity */
    public /* synthetic */ void m4164lambda$createView$0$orgtelegramuiPeopleNearbyActivity(DialogInterface dialog) {
        this.loadingDialog = null;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PeopleNearbyActivity */
    public /* synthetic */ void m4165lambda$createView$1$orgtelegramuiPeopleNearbyActivity(UserConfig userConfig, DialogInterface dialog, int which) {
        userConfig.sharingMyLocationUntil = Integer.MAX_VALUE;
        userConfig.saveConfig(false);
        sendRequest(false, 1);
        updateRows(null);
    }

    public void checkScroll(boolean animated) {
        boolean show;
        int first = this.layoutManager.findFirstVisibleItemPosition();
        if (first != 0) {
            show = true;
        } else {
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(first);
            if (holder == null) {
                show = true;
            } else {
                HintInnerCell hintInnerCell = (HintInnerCell) holder.itemView;
                hintInnerCell.titleTextView.getLocationOnScreen(this.location);
                show = this.location[1] + hintInnerCell.titleTextView.getMeasuredHeight() < this.actionBar.getBottom();
            }
        }
        boolean visible = this.actionBarBackground.getTag() == null;
        if (show != visible) {
            this.actionBarBackground.setTag(show ? null : 1);
            AnimatorSet animatorSet = this.actionBarAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimator = null;
            }
            float f = 1.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.actionBarAnimator = animatorSet2;
                Animator[] animatorArr = new Animator[2];
                View view = this.actionBarBackground;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                SimpleTextView titleTextView = this.actionBar.getTitleTextView();
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(titleTextView, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                this.actionBarAnimator.setDuration(150L);
                this.actionBarAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PeopleNearbyActivity.7
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(PeopleNearbyActivity.this.actionBarAnimator)) {
                            PeopleNearbyActivity.this.actionBarAnimator = null;
                        }
                    }
                });
                this.actionBarAnimator.start();
                return;
            }
            this.actionBarBackground.setAlpha(show ? 1.0f : 0.0f);
            SimpleTextView titleTextView2 = this.actionBar.getTitleTextView();
            if (!show) {
                f = 0.0f;
            }
            titleTextView2.setAlpha(f);
        }
    }

    private void openGroupCreate() {
        if (!this.canCreateGroup) {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("YourLocatedChannelsTooMuch", R.string.YourLocatedChannelsTooMuch));
            return;
        }
        ActionIntroActivity actionIntroActivity = new ActionIntroActivity(2);
        this.groupCreateActivity = actionIntroActivity;
        actionIntroActivity.setGroupCreateAddress(this.currentGroupCreateAddress, this.currentGroupCreateDisplayAddress, this.currentGroupCreateLocation);
        presentFragment(this.groupCreateActivity);
    }

    private void checkCanCreateGroup() {
        if (this.checkingCanCreate) {
            return;
        }
        this.checkingCanCreate = true;
        TLRPC.TL_channels_getAdminedPublicChannels req = new TLRPC.TL_channels_getAdminedPublicChannels();
        req.by_location = true;
        req.check_limit = true;
        int reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PeopleNearbyActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PeopleNearbyActivity.this.m4162x84f957e9(tLObject, tL_error);
            }
        });
        getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
    }

    /* renamed from: lambda$checkCanCreateGroup$4$org-telegram-ui-PeopleNearbyActivity */
    public /* synthetic */ void m4162x84f957e9(TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PeopleNearbyActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                PeopleNearbyActivity.this.m4161x23a6bb4a(error);
            }
        });
    }

    /* renamed from: lambda$checkCanCreateGroup$3$org-telegram-ui-PeopleNearbyActivity */
    public /* synthetic */ void m4161x23a6bb4a(TLRPC.TL_error error) {
        this.canCreateGroup = error == null;
        this.checkingCanCreate = false;
        AlertDialog alertDialog = this.loadingDialog;
        if (alertDialog != null && this.currentGroupCreateAddress != null) {
            try {
                alertDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            this.loadingDialog = null;
            openGroupCreate();
        }
    }

    private void showLoadingProgress(boolean show) {
        if (this.showingLoadingProgress == show) {
            return;
        }
        this.showingLoadingProgress = show;
        AnimatorSet animatorSet = this.showProgressAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.showProgressAnimation = null;
        }
        if (this.listView == null) {
            return;
        }
        ArrayList<Animator> animators = new ArrayList<>();
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof HeaderCellProgress) {
                HeaderCellProgress cell = (HeaderCellProgress) child;
                this.animatingViews.add(cell);
                RadialProgressView radialProgressView = cell.progressView;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animators.add(ObjectAnimator.ofFloat(radialProgressView, property, fArr));
            }
        }
        if (animators.isEmpty()) {
            return;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.showProgressAnimation = animatorSet2;
        animatorSet2.playTogether(animators);
        this.showProgressAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PeopleNearbyActivity.8
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                PeopleNearbyActivity.this.showProgressAnimation = null;
                PeopleNearbyActivity.this.animatingViews.clear();
            }
        });
        this.showProgressAnimation.setDuration(180L);
        this.showProgressAnimation.start();
    }

    public void sendRequest(boolean shortpoll, final int share) {
        Location location;
        if (!this.firstLoaded) {
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.PeopleNearbyActivity$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    PeopleNearbyActivity.this.m4169lambda$sendRequest$5$orgtelegramuiPeopleNearbyActivity();
                }
            };
            this.showProgressRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 1000L);
            this.firstLoaded = true;
        }
        Location location2 = getLocationController().getLastKnownLocation();
        if (location2 == null) {
            return;
        }
        this.currentGroupCreateLocation = location2;
        int i = 0;
        if (!shortpoll && (location = this.lastLoadedLocation) != null) {
            float distance = location.distanceTo(location2);
            if (BuildVars.DEBUG_VERSION) {
                FileLog.d("located distance = " + distance);
            }
            if (share != 0 || (SystemClock.elapsedRealtime() - this.lastLoadedLocationTime >= 3000 && this.lastLoadedLocation.distanceTo(location2) > 20.0f)) {
                if (this.reqId != 0) {
                    getConnectionsManager().cancelRequest(this.reqId, true);
                    this.reqId = 0;
                }
            } else {
                return;
            }
        }
        if (this.reqId != 0) {
            return;
        }
        this.lastLoadedLocation = location2;
        this.lastLoadedLocationTime = SystemClock.elapsedRealtime();
        LocationController.fetchLocationAddress(this.currentGroupCreateLocation, this);
        TLRPC.TL_contacts_getLocated req = new TLRPC.TL_contacts_getLocated();
        req.geo_point = new TLRPC.TL_inputGeoPoint();
        req.geo_point.lat = location2.getLatitude();
        req.geo_point._long = location2.getLongitude();
        if (share != 0) {
            req.flags |= 1;
            if (share == 1) {
                i = Integer.MAX_VALUE;
            }
            req.self_expires = i;
        }
        this.reqId = getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PeopleNearbyActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PeopleNearbyActivity.this.m4171lambda$sendRequest$7$orgtelegramuiPeopleNearbyActivity(share, tLObject, tL_error);
            }
        });
        getConnectionsManager().bindRequestToGuid(this.reqId, this.classGuid);
    }

    /* renamed from: lambda$sendRequest$5$org-telegram-ui-PeopleNearbyActivity */
    public /* synthetic */ void m4169lambda$sendRequest$5$orgtelegramuiPeopleNearbyActivity() {
        showLoadingProgress(true);
        this.showProgressRunnable = null;
    }

    /* renamed from: lambda$sendRequest$7$org-telegram-ui-PeopleNearbyActivity */
    public /* synthetic */ void m4171lambda$sendRequest$7$orgtelegramuiPeopleNearbyActivity(final int share, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PeopleNearbyActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                PeopleNearbyActivity.this.m4170lambda$sendRequest$6$orgtelegramuiPeopleNearbyActivity(share, error, response);
            }
        });
    }

    /* renamed from: lambda$sendRequest$6$org-telegram-ui-PeopleNearbyActivity */
    public /* synthetic */ void m4170lambda$sendRequest$6$orgtelegramuiPeopleNearbyActivity(int share, TLRPC.TL_error error, TLObject response) {
        this.reqId = 0;
        Runnable runnable = this.showProgressRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.showProgressRunnable = null;
        }
        showLoadingProgress(false);
        boolean saveConfig = false;
        UserConfig userConfig = getUserConfig();
        if (share == 1 && error != null) {
            userConfig.sharingMyLocationUntil = 0;
            saveConfig = true;
            updateRows(null);
        }
        if (response != null && share != 2) {
            TLRPC.Updates updates = (TLRPC.TL_updates) response;
            getMessagesController().putUsers(updates.users, false);
            getMessagesController().putChats(updates.chats, false);
            DiffCallback diffCallback = new DiffCallback();
            diffCallback.saveCurrentState();
            this.users.clear();
            this.chats.clear();
            if (userConfig.sharingMyLocationUntil != 0) {
                userConfig.lastMyLocationShareTime = (int) (System.currentTimeMillis() / 1000);
                saveConfig = true;
            }
            boolean hasSelf = false;
            int N = updates.updates.size();
            for (int a = 0; a < N; a++) {
                TLRPC.Update baseUpdate = updates.updates.get(a);
                if (baseUpdate instanceof TLRPC.TL_updatePeerLocated) {
                    TLRPC.TL_updatePeerLocated update = (TLRPC.TL_updatePeerLocated) baseUpdate;
                    int N2 = update.peers.size();
                    for (int b = 0; b < N2; b++) {
                        TLRPC.PeerLocated object = update.peers.get(b);
                        if (object instanceof TLRPC.TL_peerLocated) {
                            TLRPC.TL_peerLocated peerLocated = (TLRPC.TL_peerLocated) object;
                            if (peerLocated.peer instanceof TLRPC.TL_peerUser) {
                                this.users.add(peerLocated);
                            } else {
                                this.chats.add(peerLocated);
                            }
                        } else if (object instanceof TLRPC.TL_peerSelfLocated) {
                            TLRPC.TL_peerSelfLocated peerSelfLocated = (TLRPC.TL_peerSelfLocated) object;
                            if (userConfig.sharingMyLocationUntil == peerSelfLocated.expires) {
                                hasSelf = true;
                            } else {
                                userConfig.sharingMyLocationUntil = peerSelfLocated.expires;
                                saveConfig = true;
                                hasSelf = true;
                            }
                        }
                    }
                }
            }
            if (!hasSelf && userConfig.sharingMyLocationUntil != 0) {
                userConfig.sharingMyLocationUntil = 0;
                saveConfig = true;
            }
            checkForExpiredLocations(true);
            updateRows(diffCallback);
        }
        if (saveConfig) {
            userConfig.saveConfig(false);
        }
        Runnable runnable2 = this.shortPollRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            AndroidUtilities.runOnUIThread(this.shortPollRunnable, 25000L);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        getLocationController().startLocationLookupForPeopleNearby(false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        getLocationController().startLocationLookupForPeopleNearby(true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        super.onBecomeFullyHidden();
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    @Override // org.telegram.messenger.LocationController.LocationFetchCallback
    public void onLocationAddressAvailable(String address, String displayAddress, Location location) {
        this.currentGroupCreateAddress = address;
        this.currentGroupCreateDisplayAddress = displayAddress;
        this.currentGroupCreateLocation = location;
        ActionIntroActivity actionIntroActivity = this.groupCreateActivity;
        if (actionIntroActivity != null) {
            actionIntroActivity.setGroupCreateAddress(address, displayAddress, location);
        }
        AlertDialog alertDialog = this.loadingDialog;
        if (alertDialog != null && !this.checkingCanCreate) {
            try {
                alertDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            this.loadingDialog = null;
            openGroupCreate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        this.groupCreateActivity = null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0068, code lost:
        if (r13.peer.user_id != r5.peer.user_id) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0081, code lost:
        if (r13.peer.chat_id == r5.peer.chat_id) goto L30;
     */
    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void didReceivedNotification(int r22, int r23, java.lang.Object... r24) {
        /*
            Method dump skipped, instructions count: 262
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PeopleNearbyActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* renamed from: lambda$didReceivedNotification$8$org-telegram-ui-PeopleNearbyActivity */
    public /* synthetic */ void m4167xa0217175(TLRPC.Chat chat, long dialogId, boolean revoke) {
        if (chat == null) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
        } else {
            getMessagesController().deleteParticipantFromChat(-dialogId, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), null, null, revoke, revoke);
        }
    }

    private void checkForExpiredLocations(boolean cache) {
        Runnable runnable = this.checkExpiredRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkExpiredRunnable = null;
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        int minExpired = Integer.MAX_VALUE;
        boolean changed = false;
        DiffCallback callback = null;
        int a = 0;
        while (a < 2) {
            ArrayList<TLRPC.TL_peerLocated> arrayList = a == 0 ? this.users : this.chats;
            int b = 0;
            int N = arrayList.size();
            while (b < N) {
                TLRPC.TL_peerLocated peer = arrayList.get(b);
                if (peer.expires <= currentTime) {
                    if (callback == null) {
                        callback = new DiffCallback();
                        callback.saveCurrentState();
                    }
                    arrayList.remove(b);
                    b--;
                    N--;
                    changed = true;
                } else {
                    minExpired = Math.min(minExpired, peer.expires);
                }
                b++;
            }
            a++;
        }
        if (changed && this.listViewAdapter != null) {
            updateRows(callback);
        }
        if (changed || cache) {
            getLocationController().setCachedNearbyUsersAndChats(this.users, this.chats);
        }
        if (minExpired != Integer.MAX_VALUE) {
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.PeopleNearbyActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    PeopleNearbyActivity.this.m4163x726f6feb();
                }
            };
            this.checkExpiredRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, (minExpired - currentTime) * 1000);
        }
    }

    /* renamed from: lambda$checkForExpiredLocations$9$org-telegram-ui-PeopleNearbyActivity */
    public /* synthetic */ void m4163x726f6feb() {
        this.checkExpiredRunnable = null;
        checkForExpiredLocations(false);
    }

    /* loaded from: classes4.dex */
    public static class HeaderCellProgress extends HeaderCell {
        private RadialProgressView progressView;

        public HeaderCellProgress(Context context) {
            super(context);
            setClipChildren(false);
            RadialProgressView radialProgressView = new RadialProgressView(context);
            this.progressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(14.0f));
            this.progressView.setStrokeWidth(2.0f);
            this.progressView.setAlpha(0.0f);
            this.progressView.setProgressColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
            addView(this.progressView, LayoutHelper.createFrame(50, 40.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 2.0f : 0.0f, 3.0f, LocaleController.isRTL ? 0.0f : 2.0f, 0.0f));
        }
    }

    /* loaded from: classes4.dex */
    public class HintInnerCell extends FrameLayout {
        private ImageView imageView;
        private TextView messageTextView;
        private TextView titleTextView;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public HintInnerCell(Context context) {
            super(context);
            PeopleNearbyActivity.this = this$0;
            int top = ((int) ((ActionBar.getCurrentActionBarHeight() + (this$0.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) / AndroidUtilities.density)) - 44;
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(74.0f), Theme.getColor(Theme.key_chats_archiveBackground)));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context, 2));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(74, 74.0f, 49, 0.0f, top + 27, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.titleTextView.setTextSize(1, 24.0f);
            this.titleTextView.setGravity(17);
            this.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PeopleNearby", R.string.PeopleNearby, new Object[0])));
            addView(this.titleTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, top + 120, 17.0f, 27.0f));
            TextView textView2 = new TextView(context);
            this.messageTextView = textView2;
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
            this.messageTextView.setTextSize(1, 15.0f);
            this.messageTextView.setGravity(17);
            this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PeopleNearbyInfo2", R.string.PeopleNearbyInfo2, new Object[0])));
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 40.0f, top + 161, 40.0f, 27.0f));
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            PeopleNearbyActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return PeopleNearbyActivity.this.rowCount;
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
                    view = new ManageChatTextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new HeaderCellProgress(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    TextView textView = new TextView(this.mContext) { // from class: org.telegram.ui.PeopleNearbyActivity.ListAdapter.1
                        @Override // android.widget.TextView, android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(67.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    textView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    textView.setPadding(0, 0, AndroidUtilities.dp(3.0f), 0);
                    textView.setTextSize(1, 14.0f);
                    textView.setGravity(17);
                    textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
                    view = textView;
                    break;
                default:
                    view = new HintInnerCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 3 && !PeopleNearbyActivity.this.animatingViews.contains(holder.itemView)) {
                HeaderCellProgress cell = (HeaderCellProgress) holder.itemView;
                cell.progressView.setAlpha(PeopleNearbyActivity.this.showingLoadingProgress ? 1.0f : 0.0f);
            }
        }

        private String formatDistance(TLRPC.TL_peerLocated located) {
            return LocaleController.formatDistance(located.distance, 0);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long chatId;
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatUserCell userCell = (ManageChatUserCell) holder.itemView;
                    userCell.setTag(Integer.valueOf(position));
                    if (position < PeopleNearbyActivity.this.usersStartRow || position >= PeopleNearbyActivity.this.usersEndRow) {
                        if (position >= PeopleNearbyActivity.this.chatsStartRow && position < PeopleNearbyActivity.this.chatsEndRow) {
                            int index = position - PeopleNearbyActivity.this.chatsStartRow;
                            TLRPC.TL_peerLocated peerLocated = (TLRPC.TL_peerLocated) PeopleNearbyActivity.this.chats.get(index);
                            if (peerLocated.peer instanceof TLRPC.TL_peerChat) {
                                chatId = peerLocated.peer.chat_id;
                            } else {
                                chatId = peerLocated.peer.channel_id;
                            }
                            TLRPC.Chat chat = PeopleNearbyActivity.this.getMessagesController().getChat(Long.valueOf(chatId));
                            if (chat != null) {
                                String subtitle = formatDistance(peerLocated);
                                if (chat.participants_count != 0) {
                                    subtitle = String.format("%1$s, %2$s", subtitle, LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]));
                                }
                                if (index == PeopleNearbyActivity.this.chats.size() - 1) {
                                    z = false;
                                }
                                userCell.setData(chat, null, subtitle, z);
                                return;
                            }
                            return;
                        }
                        return;
                    }
                    TLRPC.TL_peerLocated peerLocated2 = (TLRPC.TL_peerLocated) PeopleNearbyActivity.this.users.get(position - PeopleNearbyActivity.this.usersStartRow);
                    TLRPC.User user = PeopleNearbyActivity.this.getMessagesController().getUser(Long.valueOf(peerLocated2.peer.user_id));
                    if (user != null) {
                        String formatDistance = formatDistance(peerLocated2);
                        if (PeopleNearbyActivity.this.showMoreRow == -1 && position == PeopleNearbyActivity.this.usersEndRow - 1) {
                            z = false;
                        }
                        userCell.setData(user, null, formatDistance, z);
                        return;
                    }
                    return;
                case 1:
                    ShadowSectionCell privacyCell = (ShadowSectionCell) holder.itemView;
                    if (position != PeopleNearbyActivity.this.usersSectionRow) {
                        if (position != PeopleNearbyActivity.this.chatsSectionRow) {
                            if (position == PeopleNearbyActivity.this.helpSectionRow) {
                                privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                                return;
                            }
                            return;
                        }
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    return;
                case 2:
                    ManageChatTextCell actionCell = (ManageChatTextCell) holder.itemView;
                    actionCell.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                    if (position != PeopleNearbyActivity.this.chatsCreateRow) {
                        if (position != PeopleNearbyActivity.this.showMeRow) {
                            if (position == PeopleNearbyActivity.this.showMoreRow) {
                                actionCell.setText(LocaleController.formatPluralString("ShowVotes", PeopleNearbyActivity.this.users.size() - 5, new Object[0]), null, R.drawable.arrow_more, false);
                                return;
                            }
                            return;
                        }
                        PeopleNearbyActivity peopleNearbyActivity = PeopleNearbyActivity.this;
                        if (peopleNearbyActivity.showingMe = peopleNearbyActivity.getUserConfig().sharingMyLocationUntil > PeopleNearbyActivity.this.getConnectionsManager().getCurrentTime()) {
                            String string = LocaleController.getString("StopShowingMe", R.string.StopShowingMe);
                            if (PeopleNearbyActivity.this.usersStartRow == -1) {
                                z = false;
                            }
                            actionCell.setText(string, null, R.drawable.msg_nearby_off, z);
                            actionCell.setColors(Theme.key_windowBackgroundWhiteRedText5, Theme.key_windowBackgroundWhiteRedText5);
                            return;
                        }
                        String string2 = LocaleController.getString("MakeMyselfVisible", R.string.MakeMyselfVisible);
                        if (PeopleNearbyActivity.this.usersStartRow == -1) {
                            z = false;
                        }
                        actionCell.setText(string2, null, R.drawable.msg_nearby, z);
                        return;
                    }
                    String string3 = LocaleController.getString("NearbyCreateGroup", R.string.NearbyCreateGroup);
                    if (PeopleNearbyActivity.this.chatsStartRow == -1) {
                        z = false;
                    }
                    actionCell.setText(string3, null, R.drawable.msg_groups_create, z);
                    return;
                case 3:
                    HeaderCellProgress headerCell = (HeaderCellProgress) holder.itemView;
                    if (position != PeopleNearbyActivity.this.usersHeaderRow) {
                        if (position == PeopleNearbyActivity.this.chatsHeaderRow) {
                            headerCell.setText(LocaleController.getString("ChatsNearbyHeader", R.string.ChatsNearbyHeader));
                            return;
                        }
                        return;
                    }
                    headerCell.setText(LocaleController.getString("PeopleNearbyHeader", R.string.PeopleNearbyHeader));
                    return;
                default:
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
            if (position != PeopleNearbyActivity.this.helpRow) {
                if (position != PeopleNearbyActivity.this.chatsCreateRow && position != PeopleNearbyActivity.this.showMeRow && position != PeopleNearbyActivity.this.showMoreRow) {
                    if (position != PeopleNearbyActivity.this.usersHeaderRow && position != PeopleNearbyActivity.this.chatsHeaderRow) {
                        if (position == PeopleNearbyActivity.this.usersSectionRow || position == PeopleNearbyActivity.this.chatsSectionRow || position == PeopleNearbyActivity.this.helpSectionRow) {
                            return 1;
                        }
                        return 0;
                    }
                    return 3;
                }
                return 2;
            }
            return 5;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, null, true);
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.PeopleNearbyActivity$$ExternalSyntheticLambda10
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                PeopleNearbyActivity.this.m4168xb9b21fb();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class, TextView.class, HintInnerCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBarBackground, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{HeaderCellProgress.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
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
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{HintInnerCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_archiveBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_message));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText5));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText5));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_cancelColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        themeDescriptions.add(new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_undo_infoColor));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$10$org-telegram-ui-PeopleNearbyActivity */
    public /* synthetic */ void m4168xb9b21fb() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
            }
        }
    }
}
