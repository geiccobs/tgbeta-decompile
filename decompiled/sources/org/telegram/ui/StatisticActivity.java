package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.ArraySet;
import androidx.core.graphics.ColorUtils;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.firebase.messaging.Constants;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LruCache;
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
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StatisticPostInfoCell;
import org.telegram.ui.Charts.BarChartView;
import org.telegram.ui.Charts.BaseChartView;
import org.telegram.ui.Charts.DoubleLinearChartView;
import org.telegram.ui.Charts.LinearChartView;
import org.telegram.ui.Charts.PieChartView;
import org.telegram.ui.Charts.StackBarChartView;
import org.telegram.ui.Charts.StackLinearChartView;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.DoubleLinearChartData;
import org.telegram.ui.Charts.data.StackBarChartData;
import org.telegram.ui.Charts.data.StackLinearChartData;
import org.telegram.ui.Charts.view_data.ChartHeaderView;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.TransitionParams;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FlatCheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PeopleNearbyActivity;
import org.telegram.ui.StatisticActivity;
/* loaded from: classes4.dex */
public class StatisticActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ChartViewData actionsData;
    private Adapter adapter;
    private RecyclerView.ItemAnimator animator;
    ChatAvatarContainer avatarContainer;
    private final TLRPC.ChatFull chat;
    private DiffUtilsCallback diffUtilsCallback;
    private ChartViewData followersData;
    private ChartViewData groupMembersData;
    private ChartViewData growthData;
    private RLottieImageView imageView;
    private ChartViewData interactionsData;
    private final boolean isMegagroup;
    private ChartViewData ivInteractionsData;
    private ChartViewData languagesData;
    private ZoomCancelable lastCancelable;
    private LinearLayoutManager layoutManager;
    private long maxDateOverview;
    private ChartViewData membersLanguageData;
    private ChartViewData messagesData;
    private boolean messagesIsLoading;
    private long minDateOverview;
    private ChartViewData newFollowersBySourceData;
    private ChartViewData newMembersBySourceData;
    private ChartViewData notificationsData;
    private OverviewChannelData overviewChannelData;
    private OverviewChatData overviewChatData;
    private LinearLayout progressLayout;
    private RecyclerListView recyclerListView;
    private BaseChartView.SharedUiComponents sharedUi;
    private ChartViewData topDayOfWeeksData;
    private ChartViewData topHoursData;
    private ChartViewData viewsBySourceData;
    private ArrayList<MemberData> topMembersAll = new ArrayList<>();
    private ArrayList<MemberData> topMembersVisible = new ArrayList<>();
    private ArrayList<MemberData> topInviters = new ArrayList<>();
    private ArrayList<MemberData> topAdmins = new ArrayList<>();
    private LruCache<ChartData> childDataCache = new LruCache<>(50);
    private AlertDialog[] progressDialog = new AlertDialog[1];
    private int loadFromId = -1;
    private final SparseIntArray recentPostIdtoIndexMap = new SparseIntArray();
    private final ArrayList<RecentPostInfo> recentPostsAll = new ArrayList<>();
    private final ArrayList<RecentPostInfo> recentPostsLoaded = new ArrayList<>();
    private boolean initialLoading = true;
    private final Runnable showProgressbar = new Runnable() { // from class: org.telegram.ui.StatisticActivity.1
        @Override // java.lang.Runnable
        public void run() {
            StatisticActivity.this.progressLayout.animate().alpha(1.0f).setDuration(230L);
        }
    };

    /* loaded from: classes4.dex */
    public static class RecentPostInfo {
        public TLRPC.TL_messageInteractionCounters counters;
        public MessageObject message;
    }

    /* loaded from: classes4.dex */
    public static class ZoomCancelable {
        int adapterPosition;
        boolean canceled;
    }

    public StatisticActivity(Bundle args) {
        super(args);
        long chatId = args.getLong(ChatReactionsEditActivity.KEY_CHAT_ID);
        this.isMegagroup = args.getBoolean("is_megagroup", false);
        this.chat = getMessagesController().getChatFull(chatId);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        TLRPC.TL_stats_getBroadcastStats tL_stats_getBroadcastStats;
        getNotificationCenter().addObserver(this, NotificationCenter.messagesDidLoad);
        if (this.isMegagroup) {
            TLRPC.TL_stats_getMegagroupStats getMegagroupStats = new TLRPC.TL_stats_getMegagroupStats();
            tL_stats_getBroadcastStats = getMegagroupStats;
            getMegagroupStats.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat.id);
        } else {
            TLRPC.TL_stats_getBroadcastStats getBroadcastStats = new TLRPC.TL_stats_getBroadcastStats();
            tL_stats_getBroadcastStats = getBroadcastStats;
            getBroadcastStats.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat.id);
        }
        int reqId = getConnectionsManager().sendRequest(tL_stats_getBroadcastStats, new RequestDelegate() { // from class: org.telegram.ui.StatisticActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StatisticActivity.this.m4584lambda$onFragmentCreate$2$orgtelegramuiStatisticActivity(tLObject, tL_error);
            }
        }, null, null, 0, this.chat.stats_dc, 1, true);
        getConnectionsManager().bindRequestToGuid(reqId, this.classGuid);
        return super.onFragmentCreate();
    }

    /* renamed from: lambda$onFragmentCreate$2$org-telegram-ui-StatisticActivity */
    public /* synthetic */ void m4584lambda$onFragmentCreate$2$orgtelegramuiStatisticActivity(TLObject response, TLRPC.TL_error error) {
        if (response instanceof TLRPC.TL_stats_broadcastStats) {
            TLRPC.TL_stats_broadcastStats stats = (TLRPC.TL_stats_broadcastStats) response;
            final ChartViewData[] chartsViewData = {createViewData(stats.iv_interactions_graph, LocaleController.getString("IVInteractionsChartTitle", R.string.IVInteractionsChartTitle), 1), createViewData(stats.followers_graph, LocaleController.getString("FollowersChartTitle", R.string.FollowersChartTitle), 0), createViewData(stats.top_hours_graph, LocaleController.getString("TopHoursChartTitle", R.string.TopHoursChartTitle), 0), createViewData(stats.interactions_graph, LocaleController.getString("InteractionsChartTitle", R.string.InteractionsChartTitle), 1), createViewData(stats.growth_graph, LocaleController.getString("GrowthChartTitle", R.string.GrowthChartTitle), 0), createViewData(stats.views_by_source_graph, LocaleController.getString("ViewsBySourceChartTitle", R.string.ViewsBySourceChartTitle), 2), createViewData(stats.new_followers_by_source_graph, LocaleController.getString("NewFollowersBySourceChartTitle", R.string.NewFollowersBySourceChartTitle), 2), createViewData(stats.languages_graph, LocaleController.getString("LanguagesChartTitle", R.string.LanguagesChartTitle), 4, true), createViewData(stats.mute_graph, LocaleController.getString("NotificationsChartTitle", R.string.NotificationsChartTitle), 0)};
            if (chartsViewData[2] != null) {
                chartsViewData[2].useHourFormat = true;
            }
            this.overviewChannelData = new OverviewChannelData(stats);
            this.maxDateOverview = stats.period.max_date * 1000;
            this.minDateOverview = stats.period.min_date * 1000;
            this.recentPostsAll.clear();
            for (int i = 0; i < stats.recent_message_interactions.size(); i++) {
                RecentPostInfo recentPostInfo = new RecentPostInfo();
                recentPostInfo.counters = stats.recent_message_interactions.get(i);
                this.recentPostsAll.add(recentPostInfo);
                this.recentPostIdtoIndexMap.put(recentPostInfo.counters.msg_id, i);
            }
            if (this.recentPostsAll.size() > 0) {
                int lastPostId = this.recentPostsAll.get(0).counters.msg_id;
                int count = this.recentPostsAll.size();
                getMessagesStorage().getMessages(-this.chat.id, 0L, false, count, lastPostId, 0, 0, this.classGuid, 0, false, 0, 0, true);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.StatisticActivity$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    StatisticActivity.this.m4582lambda$onFragmentCreate$0$orgtelegramuiStatisticActivity(chartsViewData);
                }
            });
        }
        if (response instanceof TLRPC.TL_stats_megagroupStats) {
            TLRPC.TL_stats_megagroupStats stats2 = (TLRPC.TL_stats_megagroupStats) response;
            final ChartViewData[] chartsViewData2 = {createViewData(stats2.growth_graph, LocaleController.getString("GrowthChartTitle", R.string.GrowthChartTitle), 0), createViewData(stats2.members_graph, LocaleController.getString("GroupMembersChartTitle", R.string.GroupMembersChartTitle), 0), createViewData(stats2.new_members_by_source_graph, LocaleController.getString("NewMembersBySourceChartTitle", R.string.NewMembersBySourceChartTitle), 2), createViewData(stats2.languages_graph, LocaleController.getString("MembersLanguageChartTitle", R.string.MembersLanguageChartTitle), 4, true), createViewData(stats2.messages_graph, LocaleController.getString("MessagesChartTitle", R.string.MessagesChartTitle), 2), createViewData(stats2.actions_graph, LocaleController.getString("ActionsChartTitle", R.string.ActionsChartTitle), 1), createViewData(stats2.top_hours_graph, LocaleController.getString("TopHoursChartTitle", R.string.TopHoursChartTitle), 0), createViewData(stats2.weekdays_graph, LocaleController.getString("TopDaysOfWeekChartTitle", R.string.TopDaysOfWeekChartTitle), 4)};
            if (chartsViewData2[6] != null) {
                chartsViewData2[6].useHourFormat = true;
            }
            if (chartsViewData2[7] != null) {
                chartsViewData2[7].useWeekFormat = true;
            }
            this.overviewChatData = new OverviewChatData(stats2);
            this.maxDateOverview = stats2.period.max_date * 1000;
            this.minDateOverview = stats2.period.min_date * 1000;
            if (stats2.top_posters != null && !stats2.top_posters.isEmpty()) {
                for (int i2 = 0; i2 < stats2.top_posters.size(); i2++) {
                    MemberData data = MemberData.from(stats2.top_posters.get(i2), stats2.users);
                    if (this.topMembersVisible.size() < 10) {
                        this.topMembersVisible.add(data);
                    }
                    this.topMembersAll.add(data);
                }
                if (this.topMembersAll.size() - this.topMembersVisible.size() < 2) {
                    this.topMembersVisible.clear();
                    this.topMembersVisible.addAll(this.topMembersAll);
                }
            }
            if (stats2.top_admins != null && !stats2.top_admins.isEmpty()) {
                for (int i3 = 0; i3 < stats2.top_admins.size(); i3++) {
                    this.topAdmins.add(MemberData.from(stats2.top_admins.get(i3), stats2.users));
                }
            }
            if (stats2.top_inviters != null && !stats2.top_inviters.isEmpty()) {
                for (int i4 = 0; i4 < stats2.top_inviters.size(); i4++) {
                    this.topInviters.add(MemberData.from(stats2.top_inviters.get(i4), stats2.users));
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.StatisticActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    StatisticActivity.this.m4583lambda$onFragmentCreate$1$orgtelegramuiStatisticActivity(chartsViewData2);
                }
            });
        }
    }

    /* renamed from: lambda$onFragmentCreate$0$org-telegram-ui-StatisticActivity */
    public /* synthetic */ void m4582lambda$onFragmentCreate$0$orgtelegramuiStatisticActivity(ChartViewData[] chartsViewData) {
        this.ivInteractionsData = chartsViewData[0];
        this.followersData = chartsViewData[1];
        this.topHoursData = chartsViewData[2];
        this.interactionsData = chartsViewData[3];
        this.growthData = chartsViewData[4];
        this.viewsBySourceData = chartsViewData[5];
        this.newFollowersBySourceData = chartsViewData[6];
        this.languagesData = chartsViewData[7];
        this.notificationsData = chartsViewData[8];
        dataLoaded(chartsViewData);
    }

    /* renamed from: lambda$onFragmentCreate$1$org-telegram-ui-StatisticActivity */
    public /* synthetic */ void m4583lambda$onFragmentCreate$1$orgtelegramuiStatisticActivity(ChartViewData[] chartsViewData) {
        this.growthData = chartsViewData[0];
        this.groupMembersData = chartsViewData[1];
        this.newMembersBySourceData = chartsViewData[2];
        this.membersLanguageData = chartsViewData[3];
        this.messagesData = chartsViewData[4];
        this.actionsData = chartsViewData[5];
        this.topHoursData = chartsViewData[6];
        this.topDayOfWeeksData = chartsViewData[7];
        dataLoaded(chartsViewData);
    }

    private void dataLoaded(ChartViewData[] chartsViewData) {
        Adapter adapter = this.adapter;
        if (adapter != null) {
            adapter.update();
            this.recyclerListView.setItemAnimator(null);
            this.adapter.notifyDataSetChanged();
        }
        this.initialLoading = false;
        LinearLayout linearLayout = this.progressLayout;
        if (linearLayout != null && linearLayout.getVisibility() == 0) {
            AndroidUtilities.cancelRunOnUIThread(this.showProgressbar);
            this.progressLayout.animate().alpha(0.0f).setDuration(230L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.StatisticActivity.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    StatisticActivity.this.progressLayout.setVisibility(8);
                }
            });
            this.recyclerListView.setVisibility(0);
            this.recyclerListView.setAlpha(0.0f);
            this.recyclerListView.animate().alpha(1.0f).setDuration(230L).start();
            for (ChartViewData data : chartsViewData) {
                if (data != null && data.chartData == null && data.token != null) {
                    data.load(this.currentAccount, this.classGuid, this.chat.stats_dc, this.recyclerListView, this.adapter, this.diffUtilsCallback);
                }
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.messagesDidLoad);
        AlertDialog[] alertDialogArr = this.progressDialog;
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].dismiss();
            this.progressDialog[0] = null;
        }
        super.onFragmentDestroy();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.messagesDidLoad) {
            int guid = ((Integer) args[10]).intValue();
            if (guid == this.classGuid) {
                ArrayList<MessageObject> messArr = (ArrayList) args[2];
                ArrayList<RecentPostInfo> deletedMessages = new ArrayList<>();
                int n = messArr.size();
                for (int i = 0; i < n; i++) {
                    MessageObject messageObjectFormCache = messArr.get(i);
                    int index = this.recentPostIdtoIndexMap.get(messageObjectFormCache.getId(), -1);
                    if (index >= 0 && this.recentPostsAll.get(index).counters.msg_id == messageObjectFormCache.getId()) {
                        if (messageObjectFormCache.deleted) {
                            deletedMessages.add(this.recentPostsAll.get(index));
                        } else {
                            this.recentPostsAll.get(index).message = messageObjectFormCache;
                        }
                    }
                }
                this.recentPostsAll.removeAll(deletedMessages);
                this.recentPostsLoaded.clear();
                int n2 = this.recentPostsAll.size();
                int i2 = 0;
                while (true) {
                    if (i2 >= n2) {
                        break;
                    }
                    RecentPostInfo postInfo = this.recentPostsAll.get(i2);
                    if (postInfo.message == null) {
                        this.loadFromId = postInfo.counters.msg_id;
                        break;
                    } else {
                        this.recentPostsLoaded.add(postInfo);
                        i2++;
                    }
                }
                if (this.recentPostsLoaded.size() < 20) {
                    loadMessages();
                }
                if (this.adapter != null) {
                    this.recyclerListView.setItemAnimator(null);
                    this.diffUtilsCallback.update();
                }
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.sharedUi = new BaseChartView.SharedUiComponents();
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        this.recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.StatisticActivity.3
            int lastH;

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onMeasure(int widthSpec, int heightSpec) {
                super.onMeasure(widthSpec, heightSpec);
                if (this.lastH != getMeasuredHeight() && StatisticActivity.this.adapter != null) {
                    StatisticActivity.this.adapter.notifyDataSetChanged();
                }
                this.lastH = getMeasuredHeight();
            }
        };
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
        frameLayout.addView(this.progressLayout, LayoutHelper.createFrame(PsExtractor.VIDEO_STREAM_MASK, -2.0f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
        if (this.adapter == null) {
            this.adapter = new Adapter();
        }
        this.recyclerListView.setAdapter(this.adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        this.recyclerListView.setLayoutManager(linearLayoutManager);
        this.animator = new DefaultItemAnimator() { // from class: org.telegram.ui.StatisticActivity.4
            @Override // androidx.recyclerview.widget.DefaultItemAnimator
            protected long getAddAnimationDelay(long removeDuration, long moveDuration, long changeDuration) {
                return removeDuration;
            }
        };
        this.recyclerListView.setItemAnimator(null);
        this.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.StatisticActivity.5
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (StatisticActivity.this.recentPostsAll.size() != StatisticActivity.this.recentPostsLoaded.size() && !StatisticActivity.this.messagesIsLoading && StatisticActivity.this.layoutManager.findLastVisibleItemPosition() > StatisticActivity.this.adapter.getItemCount() - 20) {
                    StatisticActivity.this.loadMessages();
                }
            }
        });
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.StatisticActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                StatisticActivity.this.m4576lambda$createView$3$orgtelegramuiStatisticActivity(view, i);
            }
        });
        this.recyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.StatisticActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return StatisticActivity.this.m4578lambda$createView$5$orgtelegramuiStatisticActivity(view, i);
            }
        });
        frameLayout.addView(this.recyclerListView);
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context, null, false);
        this.avatarContainer = chatAvatarContainer;
        chatAvatarContainer.setOccupyStatusBar(true ^ AndroidUtilities.isTablet());
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, !this.inPreviewMode ? 56.0f : 0.0f, 0.0f, 40.0f, 0.0f));
        TLRPC.Chat chatLocal = getMessagesController().getChat(Long.valueOf(this.chat.id));
        this.avatarContainer.setChatAvatar(chatLocal);
        this.avatarContainer.setTitle(chatLocal.title);
        this.avatarContainer.setSubtitle(LocaleController.getString("Statistics", R.string.Statistics));
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.StatisticActivity.6
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    StatisticActivity.this.finishFragment();
                }
            }
        });
        this.avatarContainer.setTitleColors(Theme.getColor(Theme.key_player_actionBarTitle), Theme.getColor(Theme.key_player_actionBarSubtitle));
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarActionModeDefaultSelector), false);
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        if (this.initialLoading) {
            this.progressLayout.setAlpha(0.0f);
            AndroidUtilities.runOnUIThread(this.showProgressbar, 500L);
            this.progressLayout.setVisibility(0);
            this.recyclerListView.setVisibility(8);
        } else {
            AndroidUtilities.cancelRunOnUIThread(this.showProgressbar);
            this.progressLayout.setVisibility(8);
            this.recyclerListView.setVisibility(0);
        }
        this.diffUtilsCallback = new DiffUtilsCallback(this.adapter, this.layoutManager);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-StatisticActivity */
    public /* synthetic */ void m4576lambda$createView$3$orgtelegramuiStatisticActivity(View view, int position) {
        if (position >= this.adapter.recentPostsStartRow && position <= this.adapter.recentPostsEndRow) {
            MessageObject messageObject = this.recentPostsLoaded.get(position - this.adapter.recentPostsStartRow).message;
            MessageStatisticActivity activity = new MessageStatisticActivity(messageObject);
            presentFragment(activity);
        } else if (position >= this.adapter.topAdminsStartRow && position <= this.adapter.topAdminsEndRow) {
            int i = position - this.adapter.topAdminsStartRow;
            this.topAdmins.get(i).onClick(this);
        } else if (position >= this.adapter.topMembersStartRow && position <= this.adapter.topMembersEndRow) {
            int i2 = position - this.adapter.topMembersStartRow;
            this.topMembersVisible.get(i2).onClick(this);
        } else if (position >= this.adapter.topInviterStartRow && position <= this.adapter.topInviterEndRow) {
            int i3 = position - this.adapter.topInviterStartRow;
            this.topInviters.get(i3).onClick(this);
        } else if (position == this.adapter.expandTopMembersRow) {
            int newCount = this.topMembersAll.size() - this.topMembersVisible.size();
            int p = this.adapter.expandTopMembersRow;
            this.topMembersVisible.clear();
            this.topMembersVisible.addAll(this.topMembersAll);
            Adapter adapter = this.adapter;
            if (adapter != null) {
                adapter.update();
                this.recyclerListView.setItemAnimator(this.animator);
                this.adapter.notifyItemRangeInserted(p + 1, newCount);
                this.adapter.notifyItemRemoved(p);
            }
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-StatisticActivity */
    public /* synthetic */ boolean m4578lambda$createView$5$orgtelegramuiStatisticActivity(View view, int position) {
        if (position >= this.adapter.recentPostsStartRow && position <= this.adapter.recentPostsEndRow) {
            final MessageObject messageObject = this.recentPostsLoaded.get(position - this.adapter.recentPostsStartRow).message;
            ArrayList<String> items = new ArrayList<>();
            ArrayList<Integer> actions = new ArrayList<>();
            ArrayList<Integer> icons = new ArrayList<>();
            items.add(LocaleController.getString("ViewMessageStatistic", R.string.ViewMessageStatistic));
            actions.add(0);
            icons.add(Integer.valueOf((int) R.drawable.msg_stats));
            items.add(LocaleController.getString("ViewMessage", R.string.ViewMessage));
            actions.add(1);
            icons.add(Integer.valueOf((int) R.drawable.msg_msgbubble3));
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setItems((CharSequence[]) items.toArray(new CharSequence[actions.size()]), AndroidUtilities.toIntArray(icons), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.StatisticActivity$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    StatisticActivity.this.m4577lambda$createView$4$orgtelegramuiStatisticActivity(messageObject, dialogInterface, i);
                }
            });
            showDialog(builder.create());
        } else if (position >= this.adapter.topAdminsStartRow && position <= this.adapter.topAdminsEndRow) {
            int i = position - this.adapter.topAdminsStartRow;
            this.topAdmins.get(i).onLongClick(this.chat, this, this.progressDialog);
            return true;
        } else if (position >= this.adapter.topMembersStartRow && position <= this.adapter.topMembersEndRow) {
            int i2 = position - this.adapter.topMembersStartRow;
            this.topMembersVisible.get(i2).onLongClick(this.chat, this, this.progressDialog);
            return true;
        } else if (position >= this.adapter.topInviterStartRow && position <= this.adapter.topInviterEndRow) {
            int i3 = position - this.adapter.topInviterStartRow;
            this.topInviters.get(i3).onLongClick(this.chat, this, this.progressDialog);
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-StatisticActivity */
    public /* synthetic */ void m4577lambda$createView$4$orgtelegramuiStatisticActivity(MessageObject messageObject, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            MessageStatisticActivity activity = new MessageStatisticActivity(messageObject);
            presentFragment(activity);
        } else if (i == 1) {
            Bundle bundle = new Bundle();
            bundle.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, this.chat.id);
            bundle.putInt(Constants.MessagePayloadKeys.MSGID_SERVER, messageObject.getId());
            bundle.putBoolean("need_remove_previous_same_chat_activity", false);
            ChatActivity chatActivity = new ChatActivity(bundle);
            presentFragment(chatActivity, false);
        }
    }

    public static ChartViewData createViewData(TLRPC.StatsGraph graph, String title, int graphType, boolean isLanguages) {
        if (graph == null || (graph instanceof TLRPC.TL_statsGraphError)) {
            return null;
        }
        ChartViewData viewData = new ChartViewData(title, graphType);
        viewData.isLanguages = isLanguages;
        if (graph instanceof TLRPC.TL_statsGraph) {
            String json = ((TLRPC.TL_statsGraph) graph).json.data;
            try {
                viewData.chartData = createChartData(new JSONObject(json), graphType, isLanguages);
                viewData.zoomToken = ((TLRPC.TL_statsGraph) graph).zoom_token;
                if (viewData.chartData == null || viewData.chartData.x == null || viewData.chartData.x.length < 2) {
                    viewData.isEmpty = true;
                }
                if (graphType == 4 && viewData.chartData != null && viewData.chartData.x != null && viewData.chartData.x.length > 0) {
                    long x = viewData.chartData.x[viewData.chartData.x.length - 1];
                    viewData.childChartData = new StackLinearChartData(viewData.chartData, x);
                    viewData.activeZoom = x;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else if (graph instanceof TLRPC.TL_statsGraphAsync) {
            viewData.token = ((TLRPC.TL_statsGraphAsync) graph).token;
        }
        return viewData;
    }

    private static ChartViewData createViewData(TLRPC.StatsGraph graph, String title, int graphType) {
        return createViewData(graph, title, graphType, false);
    }

    public static ChartData createChartData(JSONObject jsonObject, int graphType, boolean isLanguages) throws JSONException {
        if (graphType == 0) {
            return new ChartData(jsonObject);
        }
        if (graphType == 1) {
            return new DoubleLinearChartData(jsonObject);
        }
        if (graphType == 2) {
            return new StackBarChartData(jsonObject);
        }
        if (graphType == 4) {
            return new StackLinearChartData(jsonObject, isLanguages);
        }
        return null;
    }

    /* loaded from: classes4.dex */
    public class Adapter extends RecyclerListView.SelectionAdapter {
        int count;
        int overviewCell;
        int overviewHeaderCell = -1;
        int growCell = -1;
        int progressCell = -1;
        int folowersCell = -1;
        int topHourseCell = -1;
        int interactionsCell = -1;
        int ivInteractionsCell = -1;
        int viewsBySourceCell = -1;
        int newFollowersBySourceCell = -1;
        int languagesCell = -1;
        int notificationsCell = -1;
        int recentPostsHeaderCell = -1;
        int recentPostsStartRow = -1;
        int recentPostsEndRow = -1;
        int groupMembersCell = -1;
        int newMembersBySourceCell = -1;
        int membersLanguageCell = -1;
        int messagesCell = -1;
        int actionsCell = -1;
        int topDayOfWeeksCell = -1;
        int topMembersHeaderCell = -1;
        int topMembersStartRow = -1;
        int topMembersEndRow = -1;
        int topAdminsHeaderCell = -1;
        int topAdminsStartRow = -1;
        int topAdminsEndRow = -1;
        int topInviterHeaderCell = -1;
        int topInviterStartRow = -1;
        int topInviterEndRow = -1;
        int expandTopMembersRow = -1;
        ArraySet<Integer> shadowDivideCells = new ArraySet<>();
        ArraySet<Integer> emptyCells = new ArraySet<>();

        Adapter() {
            StatisticActivity.this = this$0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == this.growCell || position == this.folowersCell || position == this.topHourseCell || position == this.notificationsCell || position == this.actionsCell || position == this.groupMembersCell) {
                return 0;
            }
            if (position == this.interactionsCell || position == this.ivInteractionsCell) {
                return 1;
            }
            if (position == this.viewsBySourceCell || position == this.newFollowersBySourceCell || position == this.newMembersBySourceCell || position == this.messagesCell) {
                return 2;
            }
            if (position == this.languagesCell || position == this.membersLanguageCell || position == this.topDayOfWeeksCell) {
                return 4;
            }
            if (position >= this.recentPostsStartRow && position <= this.recentPostsEndRow) {
                return 9;
            }
            if (position == this.progressCell) {
                return 11;
            }
            if (this.emptyCells.contains(Integer.valueOf(position))) {
                return 12;
            }
            if (position == this.recentPostsHeaderCell || position == this.overviewHeaderCell || position == this.topAdminsHeaderCell || position == this.topMembersHeaderCell || position == this.topInviterHeaderCell) {
                return 13;
            }
            if (position == this.overviewCell) {
                return 14;
            }
            if ((position >= this.topAdminsStartRow && position <= this.topAdminsEndRow) || ((position >= this.topMembersStartRow && position <= this.topMembersEndRow) || (position >= this.topInviterStartRow && position <= this.topInviterEndRow))) {
                return 9;
            }
            if (position == this.expandTopMembersRow) {
                return 15;
            }
            return 10;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int position) {
            if (position >= this.recentPostsStartRow && position < this.recentPostsEndRow) {
                return ((RecentPostInfo) StatisticActivity.this.recentPostsLoaded.get(position - this.recentPostsStartRow)).counters.msg_id;
            }
            if (position == this.growCell) {
                return 1L;
            }
            if (position == this.folowersCell) {
                return 2L;
            }
            if (position == this.topHourseCell) {
                return 3L;
            }
            if (position == this.interactionsCell) {
                return 4L;
            }
            if (position == this.notificationsCell) {
                return 5L;
            }
            if (position == this.ivInteractionsCell) {
                return 6L;
            }
            if (position == this.viewsBySourceCell) {
                return 7L;
            }
            if (position == this.newFollowersBySourceCell) {
                return 8L;
            }
            if (position == this.languagesCell) {
                return 9L;
            }
            if (position == this.groupMembersCell) {
                return 10L;
            }
            if (position == this.newMembersBySourceCell) {
                return 11L;
            }
            if (position == this.membersLanguageCell) {
                return 12L;
            }
            if (position == this.messagesCell) {
                return 13L;
            }
            if (position == this.actionsCell) {
                return 14L;
            }
            if (position == this.topDayOfWeeksCell) {
                return 15L;
            }
            return super.getItemId(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            if (viewType >= 0 && viewType <= 4) {
                View view = new ChartCell(parent.getContext(), viewType, StatisticActivity.this.sharedUi) { // from class: org.telegram.ui.StatisticActivity.Adapter.1
                    {
                        Adapter.this = this;
                        StatisticActivity statisticActivity = StatisticActivity.this;
                    }

                    @Override // android.view.View
                    protected void onDraw(Canvas canvas) {
                        if (getTranslationY() != 0.0f) {
                            canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        }
                        super.onDraw(canvas);
                    }
                };
                view.setWillNotDraw(false);
                v = view;
            } else if (viewType == 9) {
                View view2 = new StatisticPostInfoCell(parent.getContext(), StatisticActivity.this.chat) { // from class: org.telegram.ui.StatisticActivity.Adapter.2
                    @Override // android.view.View
                    protected void onDraw(Canvas canvas) {
                        if (getTranslationY() != 0.0f) {
                            canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        }
                        super.onDraw(canvas);
                    }
                };
                view2.setWillNotDraw(false);
                v = view2;
            } else if (viewType == 11) {
                View v2 = new LoadingCell(parent.getContext());
                v2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                v = v2;
            } else if (viewType == 12) {
                v = new EmptyCell(parent.getContext(), AndroidUtilities.dp(15.0f));
            } else if (viewType == 13) {
                ChartHeaderView headerCell = new ChartHeaderView(parent.getContext()) { // from class: org.telegram.ui.StatisticActivity.Adapter.3
                    @Override // android.view.View
                    protected void onDraw(Canvas canvas) {
                        if (getTranslationY() != 0.0f) {
                            canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        }
                        super.onDraw(canvas);
                    }
                };
                headerCell.setWillNotDraw(false);
                headerCell.setPadding(headerCell.getPaddingLeft(), AndroidUtilities.dp(16.0f), headerCell.getRight(), AndroidUtilities.dp(16.0f));
                v = headerCell;
            } else if (viewType == 14) {
                v = new OverviewCell(parent.getContext());
            } else if (viewType == 15) {
                View v3 = new ManageChatTextCell(parent.getContext());
                v3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                ((ManageChatTextCell) v3).setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                v = v3;
            } else {
                v = new ShadowSectionCell(parent.getContext(), 12, Theme.getColor(Theme.key_windowBackgroundGray));
            }
            v.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(v);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ChartViewData data;
            int type = getItemViewType(position);
            if (type >= 0 && type <= 4) {
                if (this.growCell == position) {
                    data = StatisticActivity.this.growthData;
                } else {
                    data = this.folowersCell == position ? StatisticActivity.this.followersData : this.interactionsCell == position ? StatisticActivity.this.interactionsData : this.viewsBySourceCell == position ? StatisticActivity.this.viewsBySourceData : this.newFollowersBySourceCell == position ? StatisticActivity.this.newFollowersBySourceData : this.ivInteractionsCell == position ? StatisticActivity.this.ivInteractionsData : this.topHourseCell == position ? StatisticActivity.this.topHoursData : this.notificationsCell == position ? StatisticActivity.this.notificationsData : this.groupMembersCell == position ? StatisticActivity.this.groupMembersData : this.newMembersBySourceCell == position ? StatisticActivity.this.newMembersBySourceData : this.membersLanguageCell == position ? StatisticActivity.this.membersLanguageData : this.messagesCell == position ? StatisticActivity.this.messagesData : this.actionsCell == position ? StatisticActivity.this.actionsData : this.topDayOfWeeksCell == position ? StatisticActivity.this.topDayOfWeeksData : StatisticActivity.this.languagesData;
                }
                ((ChartCell) holder.itemView).updateData(data, false);
            } else if (type == 9) {
                if (StatisticActivity.this.isMegagroup) {
                    int i = this.topAdminsStartRow;
                    if (position >= i && position <= this.topAdminsEndRow) {
                        int i2 = position - i;
                        ((StatisticPostInfoCell) holder.itemView).setData((MemberData) StatisticActivity.this.topAdmins.get(i2));
                        return;
                    }
                    int i3 = this.topMembersStartRow;
                    if (position >= i3 && position <= this.topMembersEndRow) {
                        int i4 = position - i3;
                        ((StatisticPostInfoCell) holder.itemView).setData((MemberData) StatisticActivity.this.topMembersVisible.get(i4));
                        return;
                    }
                    int i5 = this.topInviterStartRow;
                    if (position >= i5 && position <= this.topInviterEndRow) {
                        int i6 = position - i5;
                        ((StatisticPostInfoCell) holder.itemView).setData((MemberData) StatisticActivity.this.topInviters.get(i6));
                        return;
                    }
                    return;
                }
                int i7 = position - this.recentPostsStartRow;
                ((StatisticPostInfoCell) holder.itemView).setData((RecentPostInfo) StatisticActivity.this.recentPostsLoaded.get(i7));
            } else if (type != 13) {
                if (type == 14) {
                    OverviewCell overviewCell = (OverviewCell) holder.itemView;
                    if (StatisticActivity.this.isMegagroup) {
                        overviewCell.setData(StatisticActivity.this.overviewChatData);
                    } else {
                        overviewCell.setData(StatisticActivity.this.overviewChannelData);
                    }
                } else if (type == 15) {
                    ManageChatTextCell manageChatTextCell = (ManageChatTextCell) holder.itemView;
                    manageChatTextCell.setText(LocaleController.formatPluralString("ShowVotes", StatisticActivity.this.topMembersAll.size() - StatisticActivity.this.topMembersVisible.size(), new Object[0]), null, R.drawable.arrow_more, false);
                }
            } else {
                ChartHeaderView headerCell = (ChartHeaderView) holder.itemView;
                headerCell.setDates(StatisticActivity.this.minDateOverview, StatisticActivity.this.maxDateOverview);
                if (position == this.overviewHeaderCell) {
                    headerCell.setTitle(LocaleController.getString("StatisticOverview", R.string.StatisticOverview));
                } else if (position == this.topAdminsHeaderCell) {
                    headerCell.setTitle(LocaleController.getString("TopAdmins", R.string.TopAdmins));
                } else if (position == this.topInviterHeaderCell) {
                    headerCell.setTitle(LocaleController.getString("TopInviters", R.string.TopInviters));
                } else if (position == this.topMembersHeaderCell) {
                    headerCell.setTitle(LocaleController.getString("TopMembers", R.string.TopMembers));
                } else {
                    headerCell.setTitle(LocaleController.getString("RecentPosts", R.string.RecentPosts));
                }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.count;
        }

        public void update() {
            this.growCell = -1;
            this.folowersCell = -1;
            this.interactionsCell = -1;
            this.viewsBySourceCell = -1;
            this.newFollowersBySourceCell = -1;
            this.languagesCell = -1;
            this.recentPostsStartRow = -1;
            this.recentPostsEndRow = -1;
            this.progressCell = -1;
            this.recentPostsHeaderCell = -1;
            this.ivInteractionsCell = -1;
            this.topHourseCell = -1;
            this.notificationsCell = -1;
            this.groupMembersCell = -1;
            this.newMembersBySourceCell = -1;
            this.membersLanguageCell = -1;
            this.messagesCell = -1;
            this.actionsCell = -1;
            this.topDayOfWeeksCell = -1;
            this.topMembersHeaderCell = -1;
            this.topMembersStartRow = -1;
            this.topMembersEndRow = -1;
            this.topAdminsHeaderCell = -1;
            this.topAdminsStartRow = -1;
            this.topAdminsEndRow = -1;
            this.topInviterHeaderCell = -1;
            this.topInviterStartRow = -1;
            this.topInviterEndRow = -1;
            this.expandTopMembersRow = -1;
            this.count = 0;
            this.emptyCells.clear();
            this.shadowDivideCells.clear();
            if (StatisticActivity.this.isMegagroup) {
                if (StatisticActivity.this.overviewChatData != null) {
                    int i = this.count;
                    int i2 = i + 1;
                    this.count = i2;
                    this.overviewHeaderCell = i;
                    this.count = i2 + 1;
                    this.overviewCell = i2;
                }
                if (StatisticActivity.this.growthData != null && !StatisticActivity.this.growthData.isEmpty) {
                    int i3 = this.count;
                    if (i3 > 0) {
                        ArraySet<Integer> arraySet = this.shadowDivideCells;
                        this.count = i3 + 1;
                        arraySet.add(Integer.valueOf(i3));
                    }
                    int i4 = this.count;
                    this.count = i4 + 1;
                    this.growCell = i4;
                }
                if (StatisticActivity.this.groupMembersData != null && !StatisticActivity.this.groupMembersData.isEmpty) {
                    int i5 = this.count;
                    if (i5 > 0) {
                        ArraySet<Integer> arraySet2 = this.shadowDivideCells;
                        this.count = i5 + 1;
                        arraySet2.add(Integer.valueOf(i5));
                    }
                    int i6 = this.count;
                    this.count = i6 + 1;
                    this.groupMembersCell = i6;
                }
                if (StatisticActivity.this.newMembersBySourceData != null && !StatisticActivity.this.newMembersBySourceData.isEmpty && !StatisticActivity.this.newMembersBySourceData.isError) {
                    int i7 = this.count;
                    if (i7 > 0) {
                        ArraySet<Integer> arraySet3 = this.shadowDivideCells;
                        this.count = i7 + 1;
                        arraySet3.add(Integer.valueOf(i7));
                    }
                    int i8 = this.count;
                    this.count = i8 + 1;
                    this.newMembersBySourceCell = i8;
                }
                if (StatisticActivity.this.membersLanguageData != null && !StatisticActivity.this.membersLanguageData.isEmpty && !StatisticActivity.this.membersLanguageData.isError) {
                    int i9 = this.count;
                    if (i9 > 0) {
                        ArraySet<Integer> arraySet4 = this.shadowDivideCells;
                        this.count = i9 + 1;
                        arraySet4.add(Integer.valueOf(i9));
                    }
                    int i10 = this.count;
                    this.count = i10 + 1;
                    this.membersLanguageCell = i10;
                }
                if (StatisticActivity.this.messagesData != null && !StatisticActivity.this.messagesData.isEmpty && !StatisticActivity.this.messagesData.isError) {
                    int i11 = this.count;
                    if (i11 > 0) {
                        ArraySet<Integer> arraySet5 = this.shadowDivideCells;
                        this.count = i11 + 1;
                        arraySet5.add(Integer.valueOf(i11));
                    }
                    int i12 = this.count;
                    this.count = i12 + 1;
                    this.messagesCell = i12;
                }
                if (StatisticActivity.this.actionsData != null && !StatisticActivity.this.actionsData.isEmpty && !StatisticActivity.this.actionsData.isError) {
                    int i13 = this.count;
                    if (i13 > 0) {
                        ArraySet<Integer> arraySet6 = this.shadowDivideCells;
                        this.count = i13 + 1;
                        arraySet6.add(Integer.valueOf(i13));
                    }
                    int i14 = this.count;
                    this.count = i14 + 1;
                    this.actionsCell = i14;
                }
                if (StatisticActivity.this.topHoursData != null && !StatisticActivity.this.topHoursData.isEmpty && !StatisticActivity.this.topHoursData.isError) {
                    int i15 = this.count;
                    if (i15 > 0) {
                        ArraySet<Integer> arraySet7 = this.shadowDivideCells;
                        this.count = i15 + 1;
                        arraySet7.add(Integer.valueOf(i15));
                    }
                    int i16 = this.count;
                    this.count = i16 + 1;
                    this.topHourseCell = i16;
                }
                if (StatisticActivity.this.topDayOfWeeksData != null && !StatisticActivity.this.topDayOfWeeksData.isEmpty && !StatisticActivity.this.topDayOfWeeksData.isError) {
                    int i17 = this.count;
                    if (i17 > 0) {
                        ArraySet<Integer> arraySet8 = this.shadowDivideCells;
                        this.count = i17 + 1;
                        arraySet8.add(Integer.valueOf(i17));
                    }
                    int i18 = this.count;
                    this.count = i18 + 1;
                    this.topDayOfWeeksCell = i18;
                }
                if (StatisticActivity.this.topMembersVisible.size() > 0) {
                    int i19 = this.count;
                    if (i19 > 0) {
                        ArraySet<Integer> arraySet9 = this.shadowDivideCells;
                        this.count = i19 + 1;
                        arraySet9.add(Integer.valueOf(i19));
                    }
                    int i20 = this.count;
                    int i21 = i20 + 1;
                    this.count = i21;
                    this.topMembersHeaderCell = i20;
                    this.count = i21 + 1;
                    this.topMembersStartRow = i21;
                    int size = (i21 + StatisticActivity.this.topMembersVisible.size()) - 1;
                    this.topMembersEndRow = size;
                    this.count = size;
                    this.count = size + 1;
                    if (StatisticActivity.this.topMembersVisible.size() != StatisticActivity.this.topMembersAll.size()) {
                        int i22 = this.count;
                        this.count = i22 + 1;
                        this.expandTopMembersRow = i22;
                    } else {
                        ArraySet<Integer> arraySet10 = this.emptyCells;
                        int i23 = this.count;
                        this.count = i23 + 1;
                        arraySet10.add(Integer.valueOf(i23));
                    }
                }
                if (StatisticActivity.this.topAdmins.size() > 0) {
                    int i24 = this.count;
                    if (i24 > 0) {
                        ArraySet<Integer> arraySet11 = this.shadowDivideCells;
                        this.count = i24 + 1;
                        arraySet11.add(Integer.valueOf(i24));
                    }
                    int i25 = this.count;
                    int i26 = i25 + 1;
                    this.count = i26;
                    this.topAdminsHeaderCell = i25;
                    this.count = i26 + 1;
                    this.topAdminsStartRow = i26;
                    int size2 = (i26 + StatisticActivity.this.topAdmins.size()) - 1;
                    this.topAdminsEndRow = size2;
                    this.count = size2;
                    int i27 = size2 + 1;
                    this.count = i27;
                    ArraySet<Integer> arraySet12 = this.emptyCells;
                    this.count = i27 + 1;
                    arraySet12.add(Integer.valueOf(i27));
                }
                if (StatisticActivity.this.topInviters.size() > 0) {
                    int i28 = this.count;
                    if (i28 > 0) {
                        ArraySet<Integer> arraySet13 = this.shadowDivideCells;
                        this.count = i28 + 1;
                        arraySet13.add(Integer.valueOf(i28));
                    }
                    int i29 = this.count;
                    int i30 = i29 + 1;
                    this.count = i30;
                    this.topInviterHeaderCell = i29;
                    this.count = i30 + 1;
                    this.topInviterStartRow = i30;
                    int size3 = (i30 + StatisticActivity.this.topInviters.size()) - 1;
                    this.topInviterEndRow = size3;
                    this.count = size3;
                    this.count = size3 + 1;
                }
                int i31 = this.count;
                if (i31 > 0) {
                    ArraySet<Integer> arraySet14 = this.emptyCells;
                    this.count = i31 + 1;
                    arraySet14.add(Integer.valueOf(i31));
                    ArraySet<Integer> arraySet15 = this.shadowDivideCells;
                    int i32 = this.count;
                    this.count = i32 + 1;
                    arraySet15.add(Integer.valueOf(i32));
                    return;
                }
                return;
            }
            if (StatisticActivity.this.overviewChannelData != null) {
                int i33 = this.count;
                int i34 = i33 + 1;
                this.count = i34;
                this.overviewHeaderCell = i33;
                this.count = i34 + 1;
                this.overviewCell = i34;
            }
            if (StatisticActivity.this.growthData != null && !StatisticActivity.this.growthData.isEmpty) {
                int i35 = this.count;
                if (i35 > 0) {
                    ArraySet<Integer> arraySet16 = this.shadowDivideCells;
                    this.count = i35 + 1;
                    arraySet16.add(Integer.valueOf(i35));
                }
                int i36 = this.count;
                this.count = i36 + 1;
                this.growCell = i36;
            }
            if (StatisticActivity.this.followersData != null && !StatisticActivity.this.followersData.isEmpty) {
                int i37 = this.count;
                if (i37 > 0) {
                    ArraySet<Integer> arraySet17 = this.shadowDivideCells;
                    this.count = i37 + 1;
                    arraySet17.add(Integer.valueOf(i37));
                }
                int i38 = this.count;
                this.count = i38 + 1;
                this.folowersCell = i38;
            }
            if (StatisticActivity.this.notificationsData != null && !StatisticActivity.this.notificationsData.isEmpty) {
                int i39 = this.count;
                if (i39 > 0) {
                    ArraySet<Integer> arraySet18 = this.shadowDivideCells;
                    this.count = i39 + 1;
                    arraySet18.add(Integer.valueOf(i39));
                }
                int i40 = this.count;
                this.count = i40 + 1;
                this.notificationsCell = i40;
            }
            if (StatisticActivity.this.topHoursData != null && !StatisticActivity.this.topHoursData.isEmpty) {
                int i41 = this.count;
                if (i41 > 0) {
                    ArraySet<Integer> arraySet19 = this.shadowDivideCells;
                    this.count = i41 + 1;
                    arraySet19.add(Integer.valueOf(i41));
                }
                int i42 = this.count;
                this.count = i42 + 1;
                this.topHourseCell = i42;
            }
            if (StatisticActivity.this.viewsBySourceData != null && !StatisticActivity.this.viewsBySourceData.isEmpty) {
                int i43 = this.count;
                if (i43 > 0) {
                    ArraySet<Integer> arraySet20 = this.shadowDivideCells;
                    this.count = i43 + 1;
                    arraySet20.add(Integer.valueOf(i43));
                }
                int i44 = this.count;
                this.count = i44 + 1;
                this.viewsBySourceCell = i44;
            }
            if (StatisticActivity.this.newFollowersBySourceData != null && !StatisticActivity.this.newFollowersBySourceData.isEmpty) {
                int i45 = this.count;
                if (i45 > 0) {
                    ArraySet<Integer> arraySet21 = this.shadowDivideCells;
                    this.count = i45 + 1;
                    arraySet21.add(Integer.valueOf(i45));
                }
                int i46 = this.count;
                this.count = i46 + 1;
                this.newFollowersBySourceCell = i46;
            }
            if (StatisticActivity.this.languagesData != null && !StatisticActivity.this.languagesData.isEmpty) {
                int i47 = this.count;
                if (i47 > 0) {
                    ArraySet<Integer> arraySet22 = this.shadowDivideCells;
                    this.count = i47 + 1;
                    arraySet22.add(Integer.valueOf(i47));
                }
                int i48 = this.count;
                this.count = i48 + 1;
                this.languagesCell = i48;
            }
            if (StatisticActivity.this.interactionsData != null && !StatisticActivity.this.interactionsData.isEmpty) {
                int i49 = this.count;
                if (i49 > 0) {
                    ArraySet<Integer> arraySet23 = this.shadowDivideCells;
                    this.count = i49 + 1;
                    arraySet23.add(Integer.valueOf(i49));
                }
                int i50 = this.count;
                this.count = i50 + 1;
                this.interactionsCell = i50;
            }
            if (StatisticActivity.this.ivInteractionsData != null && !StatisticActivity.this.ivInteractionsData.loading && !StatisticActivity.this.ivInteractionsData.isError) {
                int i51 = this.count;
                if (i51 > 0) {
                    ArraySet<Integer> arraySet24 = this.shadowDivideCells;
                    this.count = i51 + 1;
                    arraySet24.add(Integer.valueOf(i51));
                }
                int i52 = this.count;
                this.count = i52 + 1;
                this.ivInteractionsCell = i52;
            }
            ArraySet<Integer> arraySet25 = this.shadowDivideCells;
            int i53 = this.count;
            this.count = i53 + 1;
            arraySet25.add(Integer.valueOf(i53));
            if (StatisticActivity.this.recentPostsAll.size() > 0) {
                int i54 = this.count;
                int i55 = i54 + 1;
                this.count = i55;
                this.recentPostsHeaderCell = i54;
                this.count = i55 + 1;
                this.recentPostsStartRow = i55;
                int size4 = (i55 + StatisticActivity.this.recentPostsLoaded.size()) - 1;
                this.recentPostsEndRow = size4;
                this.count = size4;
                this.count = size4 + 1;
                if (StatisticActivity.this.recentPostsLoaded.size() != StatisticActivity.this.recentPostsAll.size()) {
                    int i56 = this.count;
                    this.count = i56 + 1;
                    this.progressCell = i56;
                } else {
                    ArraySet<Integer> arraySet26 = this.emptyCells;
                    int i57 = this.count;
                    this.count = i57 + 1;
                    arraySet26.add(Integer.valueOf(i57));
                }
                ArraySet<Integer> arraySet27 = this.shadowDivideCells;
                int i58 = this.count;
                this.count = i58 + 1;
                arraySet27.add(Integer.valueOf(i58));
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 9 || holder.getItemViewType() == 15;
        }
    }

    /* loaded from: classes4.dex */
    public class ChartCell extends BaseChartCell {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public ChartCell(Context context, int type, BaseChartView.SharedUiComponents sharedUi) {
            super(context, type, sharedUi);
            StatisticActivity.this = r1;
        }

        @Override // org.telegram.ui.StatisticActivity.BaseChartCell
        public void zoomCanceled() {
            StatisticActivity.this.cancelZoom();
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
            } else if (this.data.zoomToken != null) {
                StatisticActivity.this.cancelZoom();
                final String cacheKey = this.data.zoomToken + "_" + x;
                ChartData dataFromCache = (ChartData) StatisticActivity.this.childDataCache.get(cacheKey);
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
                StatisticActivity statisticActivity = StatisticActivity.this;
                final ZoomCancelable finalCancelabel = new ZoomCancelable();
                statisticActivity.lastCancelable = finalCancelabel;
                finalCancelabel.adapterPosition = StatisticActivity.this.recyclerListView.getChildAdapterPosition(this);
                this.chartView.legendSignatureView.showProgress(true, false);
                int reqId = ConnectionsManager.getInstance(StatisticActivity.this.currentAccount).sendRequest(request, new RequestDelegate() { // from class: org.telegram.ui.StatisticActivity$ChartCell$$ExternalSyntheticLambda1
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        StatisticActivity.ChartCell.this.m4594lambda$onZoomed$1$orgtelegramuiStatisticActivity$ChartCell(cacheKey, finalCancelabel, tLObject, tL_error);
                    }
                }, null, null, 0, StatisticActivity.this.chat.stats_dc, 1, true);
                ConnectionsManager.getInstance(StatisticActivity.this.currentAccount).bindRequestToGuid(reqId, StatisticActivity.this.classGuid);
            }
        }

        /* renamed from: lambda$onZoomed$1$org-telegram-ui-StatisticActivity$ChartCell */
        public /* synthetic */ void m4594lambda$onZoomed$1$orgtelegramuiStatisticActivity$ChartCell(final String cacheKey, final ZoomCancelable finalCancelabel, TLObject response, TLRPC.TL_error error) {
            ChartData childData = null;
            boolean z = true;
            if (response instanceof TLRPC.TL_statsGraph) {
                String json = ((TLRPC.TL_statsGraph) response).json.data;
                try {
                    JSONObject jSONObject = new JSONObject(json);
                    int i = this.data.graphType;
                    if (this.data != StatisticActivity.this.languagesData) {
                        z = false;
                    }
                    childData = StatisticActivity.createChartData(jSONObject, i, z);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (response instanceof TLRPC.TL_statsGraphError) {
                Toast.makeText(getContext(), ((TLRPC.TL_statsGraphError) response).error, 1).show();
            }
            final ChartData finalChildData = childData;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.StatisticActivity$ChartCell$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    StatisticActivity.ChartCell.this.m4593lambda$onZoomed$0$orgtelegramuiStatisticActivity$ChartCell(finalChildData, cacheKey, finalCancelabel);
                }
            });
        }

        /* renamed from: lambda$onZoomed$0$org-telegram-ui-StatisticActivity$ChartCell */
        public /* synthetic */ void m4593lambda$onZoomed$0$orgtelegramuiStatisticActivity$ChartCell(ChartData finalChildData, String cacheKey, ZoomCancelable finalCancelabel) {
            if (finalChildData != null) {
                StatisticActivity.this.childDataCache.put(cacheKey, finalChildData);
            }
            if (finalChildData != null && !finalCancelabel.canceled && finalCancelabel.adapterPosition >= 0) {
                View view = StatisticActivity.this.layoutManager.findViewByPosition(finalCancelabel.adapterPosition);
                if (view instanceof ChartCell) {
                    this.data.childChartData = finalChildData;
                    ((ChartCell) view).chartView.legendSignatureView.showProgress(false, false);
                    ((ChartCell) view).zoomChart(false);
                }
            }
            StatisticActivity.this.cancelZoom();
        }

        @Override // org.telegram.ui.StatisticActivity.BaseChartCell
        public void loadData(ChartViewData viewData) {
            viewData.load(StatisticActivity.this.currentAccount, StatisticActivity.this.classGuid, StatisticActivity.this.chat.stats_dc, StatisticActivity.this.recyclerListView, StatisticActivity.this.adapter, StatisticActivity.this.diffUtilsCallback);
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class BaseChartCell extends FrameLayout {
        ChartHeaderView chartHeaderView;
        int chartType;
        BaseChartView chartView;
        ArrayList<CheckBoxHolder> checkBoxes = new ArrayList<>();
        ViewGroup checkboxContainer;
        ChartViewData data;
        TextView errorTextView;
        RadialProgressView progressView;
        BaseChartView zoomedChartView;

        abstract void loadData(ChartViewData chartViewData);

        public abstract void onZoomed();

        public abstract void zoomCanceled();

        public BaseChartCell(Context context, int type, BaseChartView.SharedUiComponents sharedUi) {
            super(context);
            setWillNotDraw(false);
            this.chartType = type;
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            this.checkboxContainer = new FrameLayout(context) { // from class: org.telegram.ui.StatisticActivity.BaseChartCell.1
                @Override // android.widget.FrameLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                    int currentW = 0;
                    int currentH = 0;
                    int n = getChildCount();
                    int firstH = 0;
                    if (n > 0) {
                        firstH = getChildAt(0).getMeasuredHeight();
                    }
                    for (int i = 0; i < n; i++) {
                        if (getChildAt(i).getMeasuredWidth() + currentW > getMeasuredWidth()) {
                            currentW = 0;
                            currentH += getChildAt(i).getMeasuredHeight();
                        }
                        currentW += getChildAt(i).getMeasuredWidth();
                    }
                    int i2 = getMeasuredWidth();
                    setMeasuredDimension(i2, firstH + currentH + AndroidUtilities.dp(16.0f));
                }

                @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    int currentW = 0;
                    int currentH = 0;
                    int n = getChildCount();
                    for (int i = 0; i < n; i++) {
                        if (getChildAt(i).getMeasuredWidth() + currentW > getMeasuredWidth()) {
                            currentW = 0;
                            currentH += getChildAt(i).getMeasuredHeight();
                        }
                        getChildAt(i).layout(currentW, currentH, getChildAt(i).getMeasuredWidth() + currentW, getChildAt(i).getMeasuredHeight() + currentH);
                        currentW += getChildAt(i).getMeasuredWidth();
                    }
                }
            };
            ChartHeaderView chartHeaderView = new ChartHeaderView(getContext());
            this.chartHeaderView = chartHeaderView;
            chartHeaderView.back.setOnTouchListener(new RecyclerListView.FoucsableOnTouchListener());
            this.chartHeaderView.back.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.StatisticActivity$BaseChartCell$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    StatisticActivity.BaseChartCell.this.m4586lambda$new$0$orgtelegramuiStatisticActivity$BaseChartCell(view);
                }
            });
            switch (type) {
                case 1:
                    this.chartView = new DoubleLinearChartView(getContext());
                    DoubleLinearChartView doubleLinearChartView = new DoubleLinearChartView(getContext());
                    this.zoomedChartView = doubleLinearChartView;
                    doubleLinearChartView.legendSignatureView.useHour = true;
                    break;
                case 2:
                    this.chartView = new StackBarChartView(getContext());
                    StackBarChartView stackBarChartView = new StackBarChartView(getContext());
                    this.zoomedChartView = stackBarChartView;
                    stackBarChartView.legendSignatureView.useHour = true;
                    break;
                case 3:
                    this.chartView = new BarChartView(getContext());
                    LinearChartView linearChartView = new LinearChartView(getContext());
                    this.zoomedChartView = linearChartView;
                    linearChartView.legendSignatureView.useHour = true;
                    break;
                case 4:
                    StackLinearChartView stackLinearChartView = new StackLinearChartView(getContext());
                    this.chartView = stackLinearChartView;
                    stackLinearChartView.legendSignatureView.showPercentage = true;
                    this.zoomedChartView = new PieChartView(getContext());
                    break;
                default:
                    this.chartView = new LinearChartView(getContext());
                    LinearChartView linearChartView2 = new LinearChartView(getContext());
                    this.zoomedChartView = linearChartView2;
                    linearChartView2.legendSignatureView.useHour = true;
                    break;
            }
            FrameLayout frameLayout = new FrameLayout(context);
            this.chartView.sharedUiComponents = sharedUi;
            this.zoomedChartView.sharedUiComponents = sharedUi;
            this.progressView = new RadialProgressView(context);
            frameLayout.addView(this.chartView);
            frameLayout.addView(this.chartView.legendSignatureView, -2, -2);
            frameLayout.addView(this.zoomedChartView);
            frameLayout.addView(this.zoomedChartView.legendSignatureView, -2, -2);
            frameLayout.addView(this.progressView, LayoutHelper.createFrame(44, 44.0f, 17, 0.0f, 0.0f, 0.0f, 60.0f));
            TextView textView = new TextView(context);
            this.errorTextView = textView;
            textView.setTextSize(1, 15.0f);
            frameLayout.addView(this.errorTextView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.progressView.setVisibility(8);
            this.errorTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray4));
            this.chartView.setDateSelectionListener(new BaseChartView.DateSelectionListener() { // from class: org.telegram.ui.StatisticActivity$BaseChartCell$$ExternalSyntheticLambda5
                @Override // org.telegram.ui.Charts.BaseChartView.DateSelectionListener
                public final void onDateSelected(long j) {
                    StatisticActivity.BaseChartCell.this.m4587lambda$new$1$orgtelegramuiStatisticActivity$BaseChartCell(j);
                }
            });
            this.chartView.legendSignatureView.showProgress(false, false);
            this.chartView.legendSignatureView.setOnTouchListener(new RecyclerListView.FoucsableOnTouchListener());
            this.chartView.legendSignatureView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.StatisticActivity$BaseChartCell$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    StatisticActivity.BaseChartCell.this.m4588lambda$new$2$orgtelegramuiStatisticActivity$BaseChartCell(view);
                }
            });
            this.zoomedChartView.legendSignatureView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.StatisticActivity$BaseChartCell$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    StatisticActivity.BaseChartCell.this.m4589lambda$new$3$orgtelegramuiStatisticActivity$BaseChartCell(view);
                }
            });
            this.chartView.setVisibility(0);
            this.zoomedChartView.setVisibility(4);
            this.chartView.setHeader(this.chartHeaderView);
            linearLayout.addView(this.chartHeaderView, LayoutHelper.createFrame(-1, 52.0f));
            linearLayout.addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f));
            linearLayout.addView(this.checkboxContainer, LayoutHelper.createFrame(-1, -2.0f, 0, 16.0f, 0.0f, 16.0f, 0.0f));
            if (this.chartType == 4) {
                frameLayout.setClipChildren(false);
                frameLayout.setClipToPadding(false);
                linearLayout.setClipChildren(false);
                linearLayout.setClipToPadding(false);
            }
            addView(linearLayout);
        }

        /* renamed from: lambda$new$0$org-telegram-ui-StatisticActivity$BaseChartCell */
        public /* synthetic */ void m4586lambda$new$0$orgtelegramuiStatisticActivity$BaseChartCell(View v) {
            zoomOut(true);
        }

        /* renamed from: lambda$new$1$org-telegram-ui-StatisticActivity$BaseChartCell */
        public /* synthetic */ void m4587lambda$new$1$orgtelegramuiStatisticActivity$BaseChartCell(long date) {
            zoomCanceled();
            this.chartView.legendSignatureView.showProgress(false, false);
        }

        /* renamed from: lambda$new$2$org-telegram-ui-StatisticActivity$BaseChartCell */
        public /* synthetic */ void m4588lambda$new$2$orgtelegramuiStatisticActivity$BaseChartCell(View v) {
            onZoomed();
        }

        /* renamed from: lambda$new$3$org-telegram-ui-StatisticActivity$BaseChartCell */
        public /* synthetic */ void m4589lambda$new$3$orgtelegramuiStatisticActivity$BaseChartCell(View v) {
            this.zoomedChartView.animateLegend(false);
        }

        public void zoomChart(boolean skipTransition) {
            long d = this.chartView.getSelectedDate();
            ChartData childData = this.data.childChartData;
            if (!skipTransition || this.zoomedChartView.getVisibility() != 0) {
                this.zoomedChartView.updatePicker(childData, d);
            }
            this.zoomedChartView.setData(childData);
            if (this.data.chartData.lines.size() > 1) {
                int enabledCount = 0;
                for (int i = 0; i < this.data.chartData.lines.size(); i++) {
                    boolean found = false;
                    int j = 0;
                    while (true) {
                        if (j >= childData.lines.size()) {
                            break;
                        }
                        ChartData.Line line = childData.lines.get(j);
                        if (!line.id.equals(this.data.chartData.lines.get(i).id)) {
                            j++;
                        } else {
                            boolean check = this.checkBoxes.get(i).checkBox.checked;
                            ((LineViewData) this.zoomedChartView.lines.get(j)).enabled = check;
                            ((LineViewData) this.zoomedChartView.lines.get(j)).alpha = check ? 1.0f : 0.0f;
                            this.checkBoxes.get(i).checkBox.enabled = true;
                            this.checkBoxes.get(i).checkBox.animate().alpha(1.0f).start();
                            if (check) {
                                enabledCount++;
                            }
                            found = true;
                        }
                    }
                    if (!found) {
                        this.checkBoxes.get(i).checkBox.enabled = false;
                        this.checkBoxes.get(i).checkBox.animate().alpha(0.0f).start();
                    }
                }
                if (enabledCount == 0) {
                    for (int i2 = 0; i2 < this.data.chartData.lines.size(); i2++) {
                        this.checkBoxes.get(i2).checkBox.enabled = true;
                        this.checkBoxes.get(i2).checkBox.animate().alpha(1.0f).start();
                    }
                    return;
                }
            }
            this.data.activeZoom = d;
            this.chartView.legendSignatureView.setAlpha(0.0f);
            this.chartView.selectionA = 0.0f;
            this.chartView.legendShowing = false;
            this.chartView.animateLegentTo = false;
            this.zoomedChartView.updateColors();
            if (!skipTransition) {
                this.zoomedChartView.clearSelection();
                this.chartHeaderView.zoomTo(this.zoomedChartView, d, true);
            }
            this.zoomedChartView.setHeader(this.chartHeaderView);
            this.chartView.setHeader(null);
            if (skipTransition) {
                this.chartView.setVisibility(4);
                this.zoomedChartView.setVisibility(0);
                this.chartView.transitionMode = 0;
                this.zoomedChartView.transitionMode = 0;
                this.chartView.enabled = false;
                this.zoomedChartView.enabled = true;
                this.chartHeaderView.zoomTo(this.zoomedChartView, d, false);
                return;
            }
            ValueAnimator animator = createTransitionAnimator(d, true);
            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.StatisticActivity.BaseChartCell.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    BaseChartCell.this.chartView.setVisibility(4);
                    BaseChartCell.this.chartView.enabled = false;
                    BaseChartCell.this.zoomedChartView.enabled = true;
                    BaseChartCell.this.chartView.transitionMode = 0;
                    BaseChartCell.this.zoomedChartView.transitionMode = 0;
                    ((Activity) BaseChartCell.this.getContext()).getWindow().clearFlags(16);
                }
            });
            animator.start();
        }

        private void zoomOut(boolean animated) {
            if (this.data.chartData.x == null) {
                return;
            }
            this.chartHeaderView.zoomOut(this.chartView, animated);
            this.chartView.legendSignatureView.chevron.setAlpha(1.0f);
            this.zoomedChartView.setHeader(null);
            long d = this.chartView.getSelectedDate();
            this.data.activeZoom = 0L;
            this.chartView.setVisibility(0);
            this.zoomedChartView.clearSelection();
            this.zoomedChartView.setHeader(null);
            this.chartView.setHeader(this.chartHeaderView);
            if (!animated) {
                this.zoomedChartView.setVisibility(4);
                this.chartView.enabled = true;
                this.zoomedChartView.enabled = false;
                this.chartView.invalidate();
                ((Activity) getContext()).getWindow().clearFlags(16);
                Iterator<CheckBoxHolder> it = this.checkBoxes.iterator();
                while (it.hasNext()) {
                    CheckBoxHolder checkbox = it.next();
                    checkbox.checkBox.setAlpha(1.0f);
                    checkbox.checkBox.enabled = true;
                }
                return;
            }
            ValueAnimator animator = createTransitionAnimator(d, false);
            animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.StatisticActivity.BaseChartCell.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    BaseChartCell.this.zoomedChartView.setVisibility(4);
                    BaseChartCell.this.chartView.transitionMode = 0;
                    BaseChartCell.this.zoomedChartView.transitionMode = 0;
                    BaseChartCell.this.chartView.enabled = true;
                    BaseChartCell.this.zoomedChartView.enabled = false;
                    if (!(BaseChartCell.this.chartView instanceof StackLinearChartView)) {
                        BaseChartCell.this.chartView.legendShowing = true;
                        BaseChartCell.this.chartView.moveLegend();
                        BaseChartCell.this.chartView.animateLegend(true);
                        BaseChartCell.this.chartView.invalidate();
                    } else {
                        BaseChartCell.this.chartView.legendShowing = false;
                        BaseChartCell.this.chartView.clearSelection();
                    }
                    ((Activity) BaseChartCell.this.getContext()).getWindow().clearFlags(16);
                }
            });
            Iterator<CheckBoxHolder> it2 = this.checkBoxes.iterator();
            while (it2.hasNext()) {
                CheckBoxHolder checkbox2 = it2.next();
                checkbox2.checkBox.animate().alpha(1.0f).start();
                checkbox2.checkBox.enabled = true;
            }
            animator.start();
        }

        private ValueAnimator createTransitionAnimator(long d, boolean in) {
            ((Activity) getContext()).getWindow().setFlags(16, 16);
            this.chartView.enabled = false;
            this.zoomedChartView.enabled = false;
            this.chartView.transitionMode = 2;
            this.zoomedChartView.transitionMode = 1;
            final TransitionParams params = new TransitionParams();
            params.pickerEndOut = this.chartView.pickerDelegate.pickerEnd;
            params.pickerStartOut = this.chartView.pickerDelegate.pickerStart;
            params.date = d;
            int dateIndex = Arrays.binarySearch(this.data.chartData.x, d);
            if (dateIndex < 0) {
                dateIndex = this.data.chartData.x.length - 1;
            }
            params.xPercentage = this.data.chartData.xPercentage[dateIndex];
            this.zoomedChartView.setVisibility(0);
            this.zoomedChartView.transitionParams = params;
            this.chartView.transitionParams = params;
            int max = 0;
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < this.data.chartData.lines.size(); i++) {
                if (this.data.chartData.lines.get(i).y[dateIndex] > max) {
                    max = this.data.chartData.lines.get(i).y[dateIndex];
                }
                if (this.data.chartData.lines.get(i).y[dateIndex] < min) {
                    min = this.data.chartData.lines.get(i).y[dateIndex];
                }
            }
            final float pYPercentage = ((min + (max - min)) - this.chartView.currentMinHeight) / (this.chartView.currentMaxHeight - this.chartView.currentMinHeight);
            this.chartView.fillTransitionParams(params);
            this.zoomedChartView.fillTransitionParams(params);
            float[] fArr = new float[2];
            float f = 0.0f;
            fArr[0] = in ? 0.0f : 1.0f;
            if (in) {
                f = 1.0f;
            }
            fArr[1] = f;
            ValueAnimator animator = ValueAnimator.ofFloat(fArr);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.StatisticActivity$BaseChartCell$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    StatisticActivity.BaseChartCell.this.m4585x8c3e0af5(params, pYPercentage, valueAnimator);
                }
            });
            animator.setDuration(400L);
            animator.setInterpolator(new FastOutSlowInInterpolator());
            return animator;
        }

        /* renamed from: lambda$createTransitionAnimator$4$org-telegram-ui-StatisticActivity$BaseChartCell */
        public /* synthetic */ void m4585x8c3e0af5(TransitionParams params, float pYPercentage, ValueAnimator animation) {
            float fullWidth = this.chartView.chartWidth / (this.chartView.pickerDelegate.pickerEnd - this.chartView.pickerDelegate.pickerStart);
            float offset = (this.chartView.pickerDelegate.pickerStart * fullWidth) - BaseChartView.HORIZONTAL_PADDING;
            params.pY = this.chartView.chartArea.top + ((1.0f - pYPercentage) * this.chartView.chartArea.height());
            params.pX = (this.chartView.chartFullWidth * params.xPercentage) - offset;
            params.progress = ((Float) animation.getAnimatedValue()).floatValue();
            this.zoomedChartView.invalidate();
            this.zoomedChartView.fillTransitionParams(params);
            this.chartView.invalidate();
        }

        public void updateData(ChartViewData viewData, boolean enterTransition) {
            if (viewData == null) {
                return;
            }
            this.chartHeaderView.setTitle(viewData.title);
            Configuration configuration = getContext().getResources().getConfiguration();
            boolean land = configuration.orientation == 2;
            this.chartView.setLandscape(land);
            viewData.viewShowed = true;
            this.zoomedChartView.setLandscape(land);
            this.data = viewData;
            if (viewData.isEmpty || viewData.isError) {
                this.progressView.setVisibility(8);
                if (viewData.errorMessage != null) {
                    this.errorTextView.setText(viewData.errorMessage);
                    if (this.errorTextView.getVisibility() == 8) {
                        this.errorTextView.setAlpha(0.0f);
                        this.errorTextView.animate().alpha(1.0f);
                    }
                    this.errorTextView.setVisibility(0);
                }
                this.chartView.setData(null);
                return;
            }
            this.errorTextView.setVisibility(8);
            this.chartView.legendSignatureView.isTopHourChart = viewData.useHourFormat;
            this.chartHeaderView.showDate(!viewData.useHourFormat);
            if (viewData.chartData == null && viewData.token != null) {
                this.progressView.setAlpha(1.0f);
                this.progressView.setVisibility(0);
                loadData(viewData);
                this.chartView.setData(null);
                return;
            }
            if (!enterTransition) {
                this.progressView.setVisibility(8);
            }
            this.chartView.setData(viewData.chartData);
            this.chartHeaderView.setUseWeekInterval(viewData.useWeekFormat);
            this.chartView.legendSignatureView.setUseWeek(viewData.useWeekFormat);
            this.chartView.legendSignatureView.zoomEnabled = this.data.zoomToken != null || this.chartType == 4;
            this.zoomedChartView.legendSignatureView.zoomEnabled = false;
            this.chartView.legendSignatureView.setEnabled(this.chartView.legendSignatureView.zoomEnabled);
            this.zoomedChartView.legendSignatureView.setEnabled(this.zoomedChartView.legendSignatureView.zoomEnabled);
            int n = this.chartView.lines.size();
            this.checkboxContainer.removeAllViews();
            this.checkBoxes.clear();
            if (n > 1) {
                for (int i = 0; i < n; i++) {
                    LineViewData l = (LineViewData) this.chartView.lines.get(i);
                    new CheckBoxHolder(i).setData(l);
                }
            }
            if (this.data.activeZoom > 0) {
                this.chartView.selectDate(this.data.activeZoom);
                zoomChart(true);
            } else {
                zoomOut(false);
                this.chartView.invalidate();
            }
            recolor();
            if (enterTransition) {
                this.chartView.transitionMode = 3;
                ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.chartView.transitionParams = new TransitionParams();
                this.chartView.transitionParams.progress = 0.0f;
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.StatisticActivity$BaseChartCell$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        StatisticActivity.BaseChartCell.this.m4590x59519991(valueAnimator);
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.StatisticActivity.BaseChartCell.4
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        BaseChartCell.this.chartView.transitionMode = 0;
                        BaseChartCell.this.progressView.setVisibility(8);
                    }
                });
                animator.start();
            }
        }

        /* renamed from: lambda$updateData$5$org-telegram-ui-StatisticActivity$BaseChartCell */
        public /* synthetic */ void m4590x59519991(ValueAnimator animation) {
            float a = ((Float) animation.getAnimatedValue()).floatValue();
            this.progressView.setAlpha(1.0f - a);
            this.chartView.transitionParams.progress = a;
            this.zoomedChartView.invalidate();
            this.chartView.invalidate();
        }

        public void recolor() {
            int color;
            this.chartView.updateColors();
            this.chartView.invalidate();
            this.zoomedChartView.updateColors();
            this.zoomedChartView.invalidate();
            this.chartHeaderView.recolor();
            this.chartHeaderView.invalidate();
            ChartViewData chartViewData = this.data;
            if (chartViewData != null && chartViewData.chartData != null && this.data.chartData.lines != null && this.data.chartData.lines.size() > 1) {
                for (int i = 0; i < this.data.chartData.lines.size(); i++) {
                    if (this.data.chartData.lines.get(i).colorKey != null && Theme.hasThemeKey(this.data.chartData.lines.get(i).colorKey)) {
                        color = Theme.getColor(this.data.chartData.lines.get(i).colorKey);
                    } else {
                        boolean darkBackground = ColorUtils.calculateLuminance(Theme.getColor(Theme.key_windowBackgroundWhite)) < 0.5d;
                        color = darkBackground ? this.data.chartData.lines.get(i).colorDark : this.data.chartData.lines.get(i).color;
                    }
                    if (i < this.checkBoxes.size()) {
                        this.checkBoxes.get(i).recolor(color);
                    }
                }
            }
            this.progressView.setProgressColor(Theme.getColor(Theme.key_progressCircle));
            this.errorTextView.setTextColor(Theme.getColor(Theme.key_dialogTextGray4));
        }

        /* loaded from: classes4.dex */
        public class CheckBoxHolder {
            final FlatCheckBox checkBox;
            LineViewData line;
            final int position;

            CheckBoxHolder(int position) {
                BaseChartCell.this = this$0;
                this.position = position;
                FlatCheckBox flatCheckBox = new FlatCheckBox(this$0.getContext());
                this.checkBox = flatCheckBox;
                flatCheckBox.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
                this$0.checkboxContainer.addView(flatCheckBox);
                this$0.checkBoxes.add(this);
            }

            public void setData(final LineViewData l) {
                this.line = l;
                this.checkBox.setText(l.line.name);
                this.checkBox.setChecked(l.enabled, false);
                this.checkBox.setOnTouchListener(new RecyclerListView.FoucsableOnTouchListener());
                this.checkBox.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.StatisticActivity$BaseChartCell$CheckBoxHolder$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        StatisticActivity.BaseChartCell.CheckBoxHolder.this.m4591x59b53508(l, view);
                    }
                });
                this.checkBox.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.StatisticActivity$BaseChartCell$CheckBoxHolder$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnLongClickListener
                    public final boolean onLongClick(View view) {
                        return StatisticActivity.BaseChartCell.CheckBoxHolder.this.m4592xd81638e7(l, view);
                    }
                });
            }

            /* renamed from: lambda$setData$0$org-telegram-ui-StatisticActivity$BaseChartCell$CheckBoxHolder */
            public /* synthetic */ void m4591x59b53508(LineViewData l, View v) {
                if (!this.checkBox.enabled) {
                    return;
                }
                boolean allDisabled = true;
                int n = BaseChartCell.this.checkBoxes.size();
                int i = 0;
                while (true) {
                    if (i < n) {
                        if (i == this.position || !BaseChartCell.this.checkBoxes.get(i).checkBox.enabled || !BaseChartCell.this.checkBoxes.get(i).checkBox.checked) {
                            i++;
                        } else {
                            allDisabled = false;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                BaseChartCell.this.zoomCanceled();
                if (allDisabled) {
                    this.checkBox.denied();
                    return;
                }
                FlatCheckBox flatCheckBox = this.checkBox;
                flatCheckBox.setChecked(!flatCheckBox.checked);
                l.enabled = this.checkBox.checked;
                BaseChartCell.this.chartView.onCheckChanged();
                if (BaseChartCell.this.data.activeZoom > 0 && this.position < BaseChartCell.this.zoomedChartView.lines.size()) {
                    ((LineViewData) BaseChartCell.this.zoomedChartView.lines.get(this.position)).enabled = this.checkBox.checked;
                    BaseChartCell.this.zoomedChartView.onCheckChanged();
                }
            }

            /* renamed from: lambda$setData$1$org-telegram-ui-StatisticActivity$BaseChartCell$CheckBoxHolder */
            public /* synthetic */ boolean m4592xd81638e7(LineViewData l, View v) {
                if (!this.checkBox.enabled) {
                    return false;
                }
                BaseChartCell.this.zoomCanceled();
                int n = BaseChartCell.this.checkBoxes.size();
                for (int i = 0; i < n; i++) {
                    BaseChartCell.this.checkBoxes.get(i).checkBox.setChecked(false);
                    BaseChartCell.this.checkBoxes.get(i).line.enabled = false;
                    if (BaseChartCell.this.data.activeZoom > 0 && i < BaseChartCell.this.zoomedChartView.lines.size()) {
                        ((LineViewData) BaseChartCell.this.zoomedChartView.lines.get(i)).enabled = false;
                    }
                }
                this.checkBox.setChecked(true);
                l.enabled = true;
                BaseChartCell.this.chartView.onCheckChanged();
                if (BaseChartCell.this.data.activeZoom > 0) {
                    ((LineViewData) BaseChartCell.this.zoomedChartView.lines.get(this.position)).enabled = true;
                    BaseChartCell.this.zoomedChartView.onCheckChanged();
                }
                return true;
            }

            public void recolor(int c) {
                this.checkBox.recolor(c);
            }
        }
    }

    public void cancelZoom() {
        ZoomCancelable zoomCancelable = this.lastCancelable;
        if (zoomCancelable != null) {
            zoomCancelable.canceled = true;
        }
        int n = this.recyclerListView.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = this.recyclerListView.getChildAt(i);
            if (child instanceof ChartCell) {
                ((ChartCell) child).chartView.legendSignatureView.showProgress(false, true);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class ChartViewData {
        public long activeZoom;
        ChartData chartData;
        ChartData childChartData;
        public String errorMessage;
        final int graphType;
        boolean isEmpty;
        public boolean isError;
        boolean isLanguages;
        boolean loading;
        final String title;
        String token;
        boolean useHourFormat;
        boolean useWeekFormat;
        public boolean viewShowed;
        String zoomToken;

        public ChartViewData(String title, int grahType) {
            this.title = title;
            this.graphType = grahType;
        }

        public void load(int accountId, int classGuid, int dc, final RecyclerListView recyclerListView, Adapter adapter, final DiffUtilsCallback difCallback) {
            if (!this.loading) {
                this.loading = true;
                TLRPC.TL_stats_loadAsyncGraph request = new TLRPC.TL_stats_loadAsyncGraph();
                request.token = this.token;
                int reqId = ConnectionsManager.getInstance(accountId).sendRequest(request, new RequestDelegate() { // from class: org.telegram.ui.StatisticActivity$ChartViewData$$ExternalSyntheticLambda1
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        StatisticActivity.ChartViewData.this.m4596lambda$load$1$orgtelegramuiStatisticActivity$ChartViewData(recyclerListView, difCallback, tLObject, tL_error);
                    }
                }, null, null, 0, dc, 1, true);
                ConnectionsManager.getInstance(accountId).bindRequestToGuid(reqId, classGuid);
            }
        }

        /* renamed from: lambda$load$1$org-telegram-ui-StatisticActivity$ChartViewData */
        public /* synthetic */ void m4596lambda$load$1$orgtelegramuiStatisticActivity$ChartViewData(final RecyclerListView recyclerListView, final DiffUtilsCallback difCallback, TLObject response, TLRPC.TL_error error) {
            ChartData chartData = null;
            String zoomToken = null;
            if (error == null) {
                if (response instanceof TLRPC.TL_statsGraph) {
                    String json = ((TLRPC.TL_statsGraph) response).json.data;
                    try {
                        chartData = StatisticActivity.createChartData(new JSONObject(json), this.graphType, this.isLanguages);
                        zoomToken = ((TLRPC.TL_statsGraph) response).zoom_token;
                        if (this.graphType == 4 && chartData.x != null && chartData.x.length > 0) {
                            long x = chartData.x[chartData.x.length - 1];
                            this.childChartData = new StackLinearChartData(chartData, x);
                            this.activeZoom = x;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (response instanceof TLRPC.TL_statsGraphError) {
                    this.isEmpty = false;
                    this.isError = true;
                    this.errorMessage = ((TLRPC.TL_statsGraphError) response).error;
                }
            }
            final ChartData finalChartData = chartData;
            final String finalZoomToken = zoomToken;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.StatisticActivity$ChartViewData$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    StatisticActivity.ChartViewData.this.m4595lambda$load$0$orgtelegramuiStatisticActivity$ChartViewData(finalChartData, finalZoomToken, recyclerListView, difCallback);
                }
            });
        }

        /* renamed from: lambda$load$0$org-telegram-ui-StatisticActivity$ChartViewData */
        public /* synthetic */ void m4595lambda$load$0$orgtelegramuiStatisticActivity$ChartViewData(ChartData finalChartData, String finalZoomToken, RecyclerListView recyclerListView, DiffUtilsCallback difCallback) {
            this.loading = false;
            this.chartData = finalChartData;
            this.zoomToken = finalZoomToken;
            int n = recyclerListView.getChildCount();
            boolean found = false;
            int i = 0;
            while (true) {
                if (i >= n) {
                    break;
                }
                View child = recyclerListView.getChildAt(i);
                if (!(child instanceof ChartCell) || ((ChartCell) child).data != this) {
                    i++;
                } else {
                    ((ChartCell) child).updateData(this, true);
                    found = true;
                    break;
                }
            }
            if (!found) {
                recyclerListView.setItemAnimator(null);
                difCallback.update();
            }
        }
    }

    public void loadMessages() {
        TLRPC.TL_channels_getMessages req = new TLRPC.TL_channels_getMessages();
        req.id = new ArrayList<>();
        int index = this.recentPostIdtoIndexMap.get(this.loadFromId);
        int n = this.recentPostsAll.size();
        int count = 0;
        for (int i = index; i < n; i++) {
            if (this.recentPostsAll.get(i).message == null) {
                req.id.add(Integer.valueOf(this.recentPostsAll.get(i).counters.msg_id));
                count++;
                if (count > 50) {
                    break;
                }
            }
        }
        int i2 = this.currentAccount;
        req.channel = MessagesController.getInstance(i2).getInputChannel(this.chat.id);
        this.messagesIsLoading = true;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.StatisticActivity$$ExternalSyntheticLambda4
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StatisticActivity.this.m4581lambda$loadMessages$7$orgtelegramuiStatisticActivity(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadMessages$7$org-telegram-ui-StatisticActivity */
    public /* synthetic */ void m4581lambda$loadMessages$7$orgtelegramuiStatisticActivity(TLObject response, TLRPC.TL_error error) {
        final ArrayList<MessageObject> messageObjects = new ArrayList<>();
        if (response instanceof TLRPC.messages_Messages) {
            ArrayList<TLRPC.Message> messages = ((TLRPC.messages_Messages) response).messages;
            for (int i = 0; i < messages.size(); i++) {
                messageObjects.add(new MessageObject(this.currentAccount, messages.get(i), false, true));
            }
            getMessagesStorage().putMessages(messages, false, true, true, 0, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.StatisticActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                StatisticActivity.this.m4580lambda$loadMessages$6$orgtelegramuiStatisticActivity(messageObjects);
            }
        });
    }

    /* renamed from: lambda$loadMessages$6$org-telegram-ui-StatisticActivity */
    public /* synthetic */ void m4580lambda$loadMessages$6$orgtelegramuiStatisticActivity(ArrayList messageObjects) {
        this.messagesIsLoading = false;
        if (messageObjects.isEmpty()) {
            return;
        }
        int size = messageObjects.size();
        for (int i = 0; i < size; i++) {
            MessageObject messageObjectFormCache = (MessageObject) messageObjects.get(i);
            int localIndex = this.recentPostIdtoIndexMap.get(messageObjectFormCache.getId(), -1);
            if (localIndex >= 0 && this.recentPostsAll.get(localIndex).counters.msg_id == messageObjectFormCache.getId()) {
                this.recentPostsAll.get(localIndex).message = messageObjectFormCache;
            }
        }
        this.recentPostsLoaded.clear();
        int size2 = this.recentPostsAll.size();
        int i2 = 0;
        while (true) {
            if (i2 >= size2) {
                break;
            }
            RecentPostInfo postInfo = this.recentPostsAll.get(i2);
            if (postInfo.message == null) {
                this.loadFromId = postInfo.counters.msg_id;
                break;
            } else {
                this.recentPostsLoaded.add(postInfo);
                i2++;
            }
        }
        this.recyclerListView.setItemAnimator(null);
        this.diffUtilsCallback.update();
    }

    private void recolorRecyclerItem(View child) {
        if (child instanceof ChartCell) {
            ((ChartCell) child).recolor();
        } else if (child instanceof ShadowSectionCell) {
            Drawable shadowDrawable = Theme.getThemedDrawable(ApplicationLoader.applicationContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
            Drawable background = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray));
            CombinedDrawable combinedDrawable = new CombinedDrawable(background, shadowDrawable, 0, 0);
            combinedDrawable.setFullsize(true);
            child.setBackground(combinedDrawable);
        } else if (child instanceof ChartHeaderView) {
            ((ChartHeaderView) child).recolor();
        } else if (!(child instanceof OverviewCell)) {
        } else {
            ((OverviewCell) child).updateColors();
        }
    }

    /* loaded from: classes4.dex */
    public static class DiffUtilsCallback extends DiffUtil.Callback {
        int actionsCell;
        private final Adapter adapter;
        int count;
        int endPosts;
        int folowersCell;
        int groupMembersCell;
        int growCell;
        int interactionsCell;
        int ivInteractionsCell;
        int languagesCell;
        private final LinearLayoutManager layoutManager;
        int membersLanguageCell;
        int messagesCell;
        int newFollowersBySourceCell;
        int newMembersBySourceCell;
        int notificationsCell;
        SparseIntArray positionToTypeMap;
        int startPosts;
        int topDayOfWeeksCell;
        int topHourseCell;
        int viewsBySourceCell;

        private DiffUtilsCallback(Adapter adapter, LinearLayoutManager layoutManager) {
            this.positionToTypeMap = new SparseIntArray();
            this.growCell = -1;
            this.folowersCell = -1;
            this.interactionsCell = -1;
            this.ivInteractionsCell = -1;
            this.viewsBySourceCell = -1;
            this.newFollowersBySourceCell = -1;
            this.languagesCell = -1;
            this.topHourseCell = -1;
            this.notificationsCell = -1;
            this.groupMembersCell = -1;
            this.newMembersBySourceCell = -1;
            this.membersLanguageCell = -1;
            this.messagesCell = -1;
            this.actionsCell = -1;
            this.topDayOfWeeksCell = -1;
            this.startPosts = -1;
            this.endPosts = -1;
            this.adapter = adapter;
            this.layoutManager = layoutManager;
        }

        public void saveOldState() {
            this.positionToTypeMap.clear();
            this.count = this.adapter.getItemCount();
            for (int i = 0; i < this.count; i++) {
                this.positionToTypeMap.put(i, this.adapter.getItemViewType(i));
            }
            this.growCell = this.adapter.growCell;
            this.folowersCell = this.adapter.folowersCell;
            this.interactionsCell = this.adapter.interactionsCell;
            this.ivInteractionsCell = this.adapter.ivInteractionsCell;
            this.viewsBySourceCell = this.adapter.viewsBySourceCell;
            this.newFollowersBySourceCell = this.adapter.newFollowersBySourceCell;
            this.languagesCell = this.adapter.languagesCell;
            this.topHourseCell = this.adapter.topHourseCell;
            this.notificationsCell = this.adapter.notificationsCell;
            this.startPosts = this.adapter.recentPostsStartRow;
            this.endPosts = this.adapter.recentPostsEndRow;
            this.groupMembersCell = this.adapter.groupMembersCell;
            this.newMembersBySourceCell = this.adapter.newMembersBySourceCell;
            this.membersLanguageCell = this.adapter.membersLanguageCell;
            this.messagesCell = this.adapter.messagesCell;
            this.actionsCell = this.adapter.actionsCell;
            this.topDayOfWeeksCell = this.adapter.topDayOfWeeksCell;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getOldListSize() {
            return this.count;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public int getNewListSize() {
            return this.adapter.count;
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (this.positionToTypeMap.get(oldItemPosition) == 13 && this.adapter.getItemViewType(newItemPosition) == 13) {
                return true;
            }
            if (this.positionToTypeMap.get(oldItemPosition) == 10 && this.adapter.getItemViewType(newItemPosition) == 10) {
                return true;
            }
            int i = this.startPosts;
            if (oldItemPosition >= i && oldItemPosition <= this.endPosts) {
                return oldItemPosition - i == newItemPosition - this.adapter.recentPostsStartRow;
            } else if (oldItemPosition == this.growCell && newItemPosition == this.adapter.growCell) {
                return true;
            } else {
                if (oldItemPosition == this.folowersCell && newItemPosition == this.adapter.folowersCell) {
                    return true;
                }
                if (oldItemPosition == this.interactionsCell && newItemPosition == this.adapter.interactionsCell) {
                    return true;
                }
                if (oldItemPosition == this.ivInteractionsCell && newItemPosition == this.adapter.ivInteractionsCell) {
                    return true;
                }
                if (oldItemPosition == this.viewsBySourceCell && newItemPosition == this.adapter.viewsBySourceCell) {
                    return true;
                }
                if (oldItemPosition == this.newFollowersBySourceCell && newItemPosition == this.adapter.newFollowersBySourceCell) {
                    return true;
                }
                if (oldItemPosition == this.languagesCell && newItemPosition == this.adapter.languagesCell) {
                    return true;
                }
                if (oldItemPosition == this.topHourseCell && newItemPosition == this.adapter.topHourseCell) {
                    return true;
                }
                if (oldItemPosition == this.notificationsCell && newItemPosition == this.adapter.notificationsCell) {
                    return true;
                }
                if (oldItemPosition == this.groupMembersCell && newItemPosition == this.adapter.groupMembersCell) {
                    return true;
                }
                if (oldItemPosition == this.newMembersBySourceCell && newItemPosition == this.adapter.newMembersBySourceCell) {
                    return true;
                }
                if (oldItemPosition == this.membersLanguageCell && newItemPosition == this.adapter.membersLanguageCell) {
                    return true;
                }
                if (oldItemPosition == this.messagesCell && newItemPosition == this.adapter.messagesCell) {
                    return true;
                }
                if (oldItemPosition == this.actionsCell && newItemPosition == this.adapter.actionsCell) {
                    return true;
                }
                return oldItemPosition == this.topDayOfWeeksCell && newItemPosition == this.adapter.topDayOfWeeksCell;
            }
        }

        @Override // androidx.recyclerview.widget.DiffUtil.Callback
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            int oldType = this.positionToTypeMap.get(oldItemPosition);
            return oldType == this.adapter.getItemViewType(newItemPosition);
        }

        public void update() {
            View v;
            saveOldState();
            this.adapter.update();
            int start = this.layoutManager.findFirstVisibleItemPosition();
            int end = this.layoutManager.findLastVisibleItemPosition();
            long scrollToItemId = -1;
            int offset = 0;
            int i = start;
            while (true) {
                if (i <= end) {
                    if (this.adapter.getItemId(i) == -1 || (v = this.layoutManager.findViewByPosition(i)) == null) {
                        i++;
                    } else {
                        scrollToItemId = this.adapter.getItemId(i);
                        offset = v.getTop();
                        break;
                    }
                } else {
                    break;
                }
            }
            DiffUtil.calculateDiff(this).dispatchUpdatesTo(this.adapter);
            if (scrollToItemId != -1) {
                int position = -1;
                int i2 = 0;
                while (true) {
                    if (i2 >= this.adapter.getItemCount()) {
                        break;
                    } else if (this.adapter.getItemId(i2) != scrollToItemId) {
                        i2++;
                    } else {
                        position = i2;
                        break;
                    }
                }
                if (position > 0) {
                    this.layoutManager.scrollToPositionWithOffset(position, offset);
                }
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ChartViewData chartViewData;
        ChartViewData chartViewData2;
        ThemeDescription.ThemeDescriptionDelegate themeDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.StatisticActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                StatisticActivity.this.m4579lambda$getThemeDescriptions$8$orgtelegramuiStatisticActivity();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{StatisticPostInfoCell.class}, new String[]{"message"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlack));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{StatisticPostInfoCell.class}, new String[]{"views"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlack));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{StatisticPostInfoCell.class}, new String[]{"shares"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{StatisticPostInfoCell.class}, new String[]{"date"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
        arrayList.add(new ThemeDescription(this.recyclerListView, 0, new Class[]{ChartHeaderView.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_dialogTextBlack));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_dialogTextBlack));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_statisticChartSignature));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_statisticChartSignatureAlpha));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_statisticChartHintLine));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_statisticChartActiveLine));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_statisticChartInactivePickerChart));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_statisticChartActivePickerChart));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_dialogBackground));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_windowBackgroundWhiteGrayText2));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_actionBarActionModeDefaultSelector));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_windowBackgroundWhiteGreenText2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_windowBackgroundWhiteRedText5));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        SimpleTextView simpleTextView = null;
        arrayList.add(new ThemeDescription(chatAvatarContainer != null ? chatAvatarContainer.getTitleTextView() : null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_player_actionBarTitle));
        ChatAvatarContainer chatAvatarContainer2 = this.avatarContainer;
        if (chatAvatarContainer2 != null) {
            simpleTextView = chatAvatarContainer2.getSubtitleTextView();
        }
        arrayList.add(new ThemeDescription(simpleTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_actionBarSubtitle, (Object) null));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_statisticChartLineEmpty));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText5));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText5));
        arrayList.add(new ThemeDescription(this.recyclerListView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class, TextView.class, PeopleNearbyActivity.HintInnerCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        if (this.isMegagroup) {
            for (int i = 0; i < 6; i++) {
                if (i == 0) {
                    chartViewData2 = this.growthData;
                } else if (i == 1) {
                    chartViewData2 = this.groupMembersData;
                } else if (i == 2) {
                    chartViewData2 = this.newMembersBySourceData;
                } else if (i == 3) {
                    chartViewData2 = this.membersLanguageData;
                } else if (i == 4) {
                    chartViewData2 = this.messagesData;
                } else {
                    chartViewData2 = this.actionsData;
                }
                putColorFromData(chartViewData2, arrayList, themeDelegate);
            }
        } else {
            for (int i2 = 0; i2 < 9; i2++) {
                if (i2 == 0) {
                    chartViewData = this.growthData;
                } else if (i2 == 1) {
                    chartViewData = this.followersData;
                } else if (i2 == 2) {
                    chartViewData = this.interactionsData;
                } else if (i2 == 3) {
                    chartViewData = this.ivInteractionsData;
                } else if (i2 == 4) {
                    chartViewData = this.viewsBySourceData;
                } else if (i2 == 5) {
                    chartViewData = this.newFollowersBySourceData;
                } else if (i2 == 6) {
                    chartViewData = this.notificationsData;
                } else if (i2 == 7) {
                    chartViewData = this.topHoursData;
                } else {
                    chartViewData = this.languagesData;
                }
                putColorFromData(chartViewData, arrayList, themeDelegate);
            }
        }
        return arrayList;
    }

    /* renamed from: lambda$getThemeDescriptions$8$org-telegram-ui-StatisticActivity */
    public /* synthetic */ void m4579lambda$getThemeDescriptions$8$orgtelegramuiStatisticActivity() {
        RecyclerListView recyclerListView = this.recyclerListView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                recolorRecyclerItem(this.recyclerListView.getChildAt(a));
            }
            int count2 = this.recyclerListView.getHiddenChildCount();
            for (int a2 = 0; a2 < count2; a2++) {
                recolorRecyclerItem(this.recyclerListView.getHiddenChildAt(a2));
            }
            int count3 = this.recyclerListView.getCachedChildCount();
            for (int a3 = 0; a3 < count3; a3++) {
                recolorRecyclerItem(this.recyclerListView.getCachedChildAt(a3));
            }
            int count4 = this.recyclerListView.getAttachedScrapChildCount();
            for (int a4 = 0; a4 < count4; a4++) {
                recolorRecyclerItem(this.recyclerListView.getAttachedScrapChildAt(a4));
            }
            this.recyclerListView.getRecycledViewPool().clear();
        }
        BaseChartView.SharedUiComponents sharedUiComponents = this.sharedUi;
        if (sharedUiComponents != null) {
            sharedUiComponents.invalidate();
        }
    }

    public static void putColorFromData(ChartViewData chartViewData, ArrayList<ThemeDescription> arrayList, ThemeDescription.ThemeDescriptionDelegate themeDelegate) {
        if (chartViewData != null && chartViewData.chartData != null) {
            Iterator<ChartData.Line> it = chartViewData.chartData.lines.iterator();
            while (it.hasNext()) {
                ChartData.Line l = it.next();
                if (l.colorKey != null) {
                    if (!Theme.hasThemeKey(l.colorKey)) {
                        Theme.setColor(l.colorKey, Theme.isCurrentThemeNight() ? l.colorDark : l.color, false);
                        Theme.setDefaultColor(l.colorKey, l.color);
                    }
                    arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, l.colorKey));
                }
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class OverviewChannelData {
        String followersPrimary;
        String followersSecondary;
        String followersTitle;
        boolean followersUp;
        String notificationsPrimary;
        String notificationsTitle;
        String sharesPrimary;
        String sharesSecondary;
        String sharesTitle;
        boolean sharesUp;
        String viewsPrimary;
        String viewsSecondary;
        String viewsTitle;
        boolean viewsUp;

        public OverviewChannelData(TLRPC.TL_stats_broadcastStats stats) {
            int dif = (int) (stats.followers.current - stats.followers.previous);
            float difPercent = stats.followers.previous == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? 0.0f : Math.abs((dif / ((float) stats.followers.previous)) * 100.0f);
            this.followersTitle = LocaleController.getString("FollowersChartTitle", R.string.FollowersChartTitle);
            this.followersPrimary = AndroidUtilities.formatWholeNumber((int) stats.followers.current, 0);
            String str = "+";
            if (dif == 0 || difPercent == 0.0f) {
                this.followersSecondary = "";
            } else if (difPercent == ((int) difPercent)) {
                Locale locale = Locale.ENGLISH;
                Object[] objArr = new Object[3];
                StringBuilder sb = new StringBuilder();
                sb.append(dif > 0 ? str : "");
                sb.append(AndroidUtilities.formatWholeNumber(dif, 0));
                objArr[0] = sb.toString();
                objArr[1] = Integer.valueOf((int) difPercent);
                objArr[2] = "%";
                this.followersSecondary = String.format(locale, "%s (%d%s)", objArr);
            } else {
                Locale locale2 = Locale.ENGLISH;
                Object[] objArr2 = new Object[3];
                StringBuilder sb2 = new StringBuilder();
                sb2.append(dif > 0 ? str : "");
                sb2.append(AndroidUtilities.formatWholeNumber(dif, 0));
                objArr2[0] = sb2.toString();
                objArr2[1] = Float.valueOf(difPercent);
                objArr2[2] = "%";
                this.followersSecondary = String.format(locale2, "%s (%.1f%s)", objArr2);
            }
            this.followersUp = dif >= 0;
            int dif2 = (int) (stats.shares_per_post.current - stats.shares_per_post.previous);
            float difPercent2 = stats.shares_per_post.previous == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? 0.0f : Math.abs((dif2 / ((float) stats.shares_per_post.previous)) * 100.0f);
            this.sharesTitle = LocaleController.getString("SharesPerPost", R.string.SharesPerPost);
            this.sharesPrimary = AndroidUtilities.formatWholeNumber((int) stats.shares_per_post.current, 0);
            if (dif2 == 0 || difPercent2 == 0.0f) {
                this.sharesSecondary = "";
            } else if (difPercent2 == ((int) difPercent2)) {
                Locale locale3 = Locale.ENGLISH;
                Object[] objArr3 = new Object[3];
                StringBuilder sb3 = new StringBuilder();
                sb3.append(dif2 > 0 ? str : "");
                sb3.append(AndroidUtilities.formatWholeNumber(dif2, 0));
                objArr3[0] = sb3.toString();
                objArr3[1] = Integer.valueOf((int) difPercent2);
                objArr3[2] = "%";
                this.sharesSecondary = String.format(locale3, "%s (%d%s)", objArr3);
            } else {
                Locale locale4 = Locale.ENGLISH;
                Object[] objArr4 = new Object[3];
                StringBuilder sb4 = new StringBuilder();
                sb4.append(dif2 > 0 ? str : "");
                sb4.append(AndroidUtilities.formatWholeNumber(dif2, 0));
                objArr4[0] = sb4.toString();
                objArr4[1] = Float.valueOf(difPercent2);
                objArr4[2] = "%";
                this.sharesSecondary = String.format(locale4, "%s (%.1f%s)", objArr4);
            }
            this.sharesUp = dif2 >= 0;
            int dif3 = (int) (stats.views_per_post.current - stats.views_per_post.previous);
            float difPercent3 = stats.views_per_post.previous == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? 0.0f : Math.abs((dif3 / ((float) stats.views_per_post.previous)) * 100.0f);
            this.viewsTitle = LocaleController.getString("ViewsPerPost", R.string.ViewsPerPost);
            this.viewsPrimary = AndroidUtilities.formatWholeNumber((int) stats.views_per_post.current, 0);
            if (dif3 == 0 || difPercent3 == 0.0f) {
                this.viewsSecondary = "";
            } else if (difPercent3 == ((int) difPercent3)) {
                Locale locale5 = Locale.ENGLISH;
                Object[] objArr5 = new Object[3];
                StringBuilder sb5 = new StringBuilder();
                sb5.append(dif3 <= 0 ? "" : str);
                sb5.append(AndroidUtilities.formatWholeNumber(dif3, 0));
                objArr5[0] = sb5.toString();
                objArr5[1] = Integer.valueOf((int) difPercent3);
                objArr5[2] = "%";
                this.viewsSecondary = String.format(locale5, "%s (%d%s)", objArr5);
            } else {
                Locale locale6 = Locale.ENGLISH;
                Object[] objArr6 = new Object[3];
                StringBuilder sb6 = new StringBuilder();
                sb6.append(dif3 <= 0 ? "" : str);
                sb6.append(AndroidUtilities.formatWholeNumber(dif3, 0));
                objArr6[0] = sb6.toString();
                objArr6[1] = Float.valueOf(difPercent3);
                objArr6[2] = "%";
                this.viewsSecondary = String.format(locale6, "%s (%.1f%s)", objArr6);
            }
            this.viewsUp = dif3 >= 0;
            float difPercent4 = (float) ((stats.enabled_notifications.part / stats.enabled_notifications.total) * 100.0d);
            this.notificationsTitle = LocaleController.getString("EnabledNotifications", R.string.EnabledNotifications);
            if (difPercent4 == ((int) difPercent4)) {
                this.notificationsPrimary = String.format(Locale.ENGLISH, "%d%s", Integer.valueOf((int) difPercent4), "%");
            } else {
                this.notificationsPrimary = String.format(Locale.ENGLISH, "%.2f%s", Float.valueOf(difPercent4), "%");
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class OverviewChatData {
        String membersPrimary;
        String membersSecondary;
        String membersTitle;
        boolean membersUp;
        String messagesPrimary;
        String messagesSecondary;
        String messagesTitle;
        boolean messagesUp;
        String postingMembersPrimary;
        String postingMembersSecondary;
        String postingMembersTitle;
        boolean postingMembersUp;
        String viewingMembersPrimary;
        String viewingMembersSecondary;
        String viewingMembersTitle;
        boolean viewingMembersUp;

        public OverviewChatData(TLRPC.TL_stats_megagroupStats stats) {
            int dif = (int) (stats.members.current - stats.members.previous);
            float difPercent = stats.members.previous == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? 0.0f : Math.abs((dif / ((float) stats.members.previous)) * 100.0f);
            this.membersTitle = LocaleController.getString("MembersOverviewTitle", R.string.MembersOverviewTitle);
            boolean z = false;
            this.membersPrimary = AndroidUtilities.formatWholeNumber((int) stats.members.current, 0);
            String str = "+";
            if (dif == 0 || difPercent == 0.0f) {
                this.membersSecondary = "";
            } else if (difPercent == ((int) difPercent)) {
                Locale locale = Locale.ENGLISH;
                Object[] objArr = new Object[3];
                StringBuilder sb = new StringBuilder();
                sb.append(dif > 0 ? str : "");
                sb.append(AndroidUtilities.formatWholeNumber(dif, 0));
                objArr[0] = sb.toString();
                objArr[1] = Integer.valueOf((int) difPercent);
                objArr[2] = "%";
                this.membersSecondary = String.format(locale, "%s (%d%s)", objArr);
            } else {
                Locale locale2 = Locale.ENGLISH;
                Object[] objArr2 = new Object[3];
                StringBuilder sb2 = new StringBuilder();
                sb2.append(dif > 0 ? str : "");
                sb2.append(AndroidUtilities.formatWholeNumber(dif, 0));
                objArr2[0] = sb2.toString();
                objArr2[1] = Float.valueOf(difPercent);
                objArr2[2] = "%";
                this.membersSecondary = String.format(locale2, "%s (%.1f%s)", objArr2);
            }
            this.membersUp = dif >= 0;
            int dif2 = (int) (stats.viewers.current - stats.viewers.previous);
            float difPercent2 = stats.viewers.previous == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? 0.0f : Math.abs((dif2 / ((float) stats.viewers.previous)) * 100.0f);
            this.viewingMembersTitle = LocaleController.getString("ViewingMembers", R.string.ViewingMembers);
            this.viewingMembersPrimary = AndroidUtilities.formatWholeNumber((int) stats.viewers.current, 0);
            if (dif2 == 0 || difPercent2 == 0.0f) {
                this.viewingMembersSecondary = "";
            } else {
                Locale locale3 = Locale.ENGLISH;
                Object[] objArr3 = new Object[1];
                StringBuilder sb3 = new StringBuilder();
                sb3.append(dif2 > 0 ? str : "");
                sb3.append(AndroidUtilities.formatWholeNumber(dif2, 0));
                objArr3[0] = sb3.toString();
                this.viewingMembersSecondary = String.format(locale3, "%s", objArr3);
            }
            this.viewingMembersUp = dif2 >= 0;
            int dif3 = (int) (stats.posters.current - stats.posters.previous);
            float difPercent3 = stats.posters.previous == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? 0.0f : Math.abs((dif3 / ((float) stats.posters.previous)) * 100.0f);
            this.postingMembersTitle = LocaleController.getString("PostingMembers", R.string.PostingMembers);
            this.postingMembersPrimary = AndroidUtilities.formatWholeNumber((int) stats.posters.current, 0);
            if (dif3 == 0 || difPercent3 == 0.0f) {
                this.postingMembersSecondary = "";
            } else {
                Locale locale4 = Locale.ENGLISH;
                Object[] objArr4 = new Object[1];
                StringBuilder sb4 = new StringBuilder();
                sb4.append(dif3 > 0 ? str : "");
                sb4.append(AndroidUtilities.formatWholeNumber(dif3, 0));
                objArr4[0] = sb4.toString();
                this.postingMembersSecondary = String.format(locale4, "%s", objArr4);
            }
            this.postingMembersUp = dif3 >= 0;
            int dif4 = (int) (stats.messages.current - stats.messages.previous);
            float difPercent4 = stats.messages.previous == FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE ? 0.0f : Math.abs((dif4 / ((float) stats.messages.previous)) * 100.0f);
            this.messagesTitle = LocaleController.getString("MessagesOverview", R.string.MessagesOverview);
            this.messagesPrimary = AndroidUtilities.formatWholeNumber((int) stats.messages.current, 0);
            if (dif4 == 0 || difPercent4 == 0.0f) {
                this.messagesSecondary = "";
            } else {
                Locale locale5 = Locale.ENGLISH;
                Object[] objArr5 = new Object[1];
                StringBuilder sb5 = new StringBuilder();
                sb5.append(dif4 <= 0 ? "" : str);
                sb5.append(AndroidUtilities.formatWholeNumber(dif4, 0));
                objArr5[0] = sb5.toString();
                this.messagesSecondary = String.format(locale5, "%s", objArr5);
            }
            this.messagesUp = dif4 >= 0 ? true : z;
        }
    }

    /* loaded from: classes4.dex */
    public static class OverviewCell extends LinearLayout {
        TextView[] primary = new TextView[4];
        TextView[] secondary = new TextView[4];
        TextView[] title = new TextView[4];

        public OverviewCell(Context context) {
            super(context);
            setOrientation(1);
            setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            int i = 0;
            while (i < 2) {
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(0);
                for (int j = 0; j < 2; j++) {
                    LinearLayout contentCell = new LinearLayout(context);
                    contentCell.setOrientation(1);
                    LinearLayout infoLayout = new LinearLayout(context);
                    infoLayout.setOrientation(0);
                    this.primary[(i * 2) + j] = new TextView(context);
                    this.secondary[(i * 2) + j] = new TextView(context);
                    this.title[(i * 2) + j] = new TextView(context);
                    this.primary[(i * 2) + j].setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    this.primary[(i * 2) + j].setTextSize(1, 17.0f);
                    this.title[(i * 2) + j].setTextSize(1, 13.0f);
                    this.secondary[(i * 2) + j].setTextSize(1, 13.0f);
                    this.secondary[(i * 2) + j].setPadding(AndroidUtilities.dp(4.0f), 0, 0, 0);
                    infoLayout.addView(this.primary[(i * 2) + j]);
                    infoLayout.addView(this.secondary[(i * 2) + j]);
                    contentCell.addView(infoLayout);
                    contentCell.addView(this.title[(i * 2) + j]);
                    linearLayout.addView(contentCell, LayoutHelper.createLinear(-1, -2, 1.0f));
                }
                addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 0.0f, 0.0f, i == 0 ? 16.0f : 0.0f));
                i++;
            }
        }

        public void setData(OverviewChannelData data) {
            this.primary[0].setText(data.followersPrimary);
            this.primary[1].setText(data.notificationsPrimary);
            this.primary[2].setText(data.viewsPrimary);
            this.primary[3].setText(data.sharesPrimary);
            this.secondary[0].setText(data.followersSecondary);
            TextView textView = this.secondary[0];
            boolean z = data.followersUp;
            String str = Theme.key_windowBackgroundWhiteGreenText2;
            textView.setTag(z ? str : Theme.key_windowBackgroundWhiteRedText5);
            this.secondary[1].setText("");
            this.secondary[2].setText(data.viewsSecondary);
            this.secondary[2].setTag(data.viewsUp ? str : Theme.key_windowBackgroundWhiteRedText5);
            this.secondary[3].setText(data.sharesSecondary);
            TextView textView2 = this.secondary[3];
            if (!data.sharesUp) {
                str = Theme.key_windowBackgroundWhiteRedText5;
            }
            textView2.setTag(str);
            this.title[0].setText(data.followersTitle);
            this.title[1].setText(data.notificationsTitle);
            this.title[2].setText(data.viewsTitle);
            this.title[3].setText(data.sharesTitle);
            updateColors();
        }

        public void setData(OverviewChatData data) {
            this.primary[0].setText(data.membersPrimary);
            this.primary[1].setText(data.messagesPrimary);
            this.primary[2].setText(data.viewingMembersPrimary);
            this.primary[3].setText(data.postingMembersPrimary);
            this.secondary[0].setText(data.membersSecondary);
            TextView textView = this.secondary[0];
            boolean z = data.membersUp;
            String str = Theme.key_windowBackgroundWhiteGreenText2;
            textView.setTag(z ? str : Theme.key_windowBackgroundWhiteRedText5);
            this.secondary[1].setText(data.messagesSecondary);
            this.secondary[1].setTag(data.messagesUp ? str : Theme.key_windowBackgroundWhiteRedText5);
            this.secondary[2].setText(data.viewingMembersSecondary);
            this.secondary[2].setTag(data.viewingMembersUp ? str : Theme.key_windowBackgroundWhiteRedText5);
            this.secondary[3].setText(data.postingMembersSecondary);
            TextView textView2 = this.secondary[3];
            if (!data.postingMembersUp) {
                str = Theme.key_windowBackgroundWhiteRedText5;
            }
            textView2.setTag(str);
            this.title[0].setText(data.membersTitle);
            this.title[1].setText(data.messagesTitle);
            this.title[2].setText(data.viewingMembersTitle);
            this.title[3].setText(data.postingMembersTitle);
            updateColors();
        }

        public void updateColors() {
            for (int i = 0; i < 4; i++) {
                this.primary[i].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                this.title[i].setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
                String colorKey = (String) this.secondary[i].getTag();
                if (colorKey != null) {
                    this.secondary[i].setTextColor(Theme.getColor(colorKey));
                }
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class MemberData {
        public String description;
        public TLRPC.User user;
        long user_id;

        public static MemberData from(TLRPC.TL_statsGroupTopPoster poster, ArrayList<TLRPC.User> users) {
            MemberData data = new MemberData();
            long j = poster.user_id;
            data.user_id = j;
            data.user = find(j, users);
            StringBuilder stringBuilder = new StringBuilder();
            if (poster.messages > 0) {
                stringBuilder.append(LocaleController.formatPluralString("messages", poster.messages, new Object[0]));
            }
            if (poster.avg_chars > 0) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(LocaleController.formatString("CharactersPerMessage", R.string.CharactersPerMessage, LocaleController.formatPluralString("Characters", poster.avg_chars, new Object[0])));
            }
            data.description = stringBuilder.toString();
            return data;
        }

        public static MemberData from(TLRPC.TL_statsGroupTopAdmin admin, ArrayList<TLRPC.User> users) {
            MemberData data = new MemberData();
            long j = admin.user_id;
            data.user_id = j;
            data.user = find(j, users);
            StringBuilder stringBuilder = new StringBuilder();
            if (admin.deleted > 0) {
                stringBuilder.append(LocaleController.formatPluralString("Deletions", admin.deleted, new Object[0]));
            }
            if (admin.banned > 0) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(LocaleController.formatPluralString("Bans", admin.banned, new Object[0]));
            }
            if (admin.kicked > 0) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(", ");
                }
                stringBuilder.append(LocaleController.formatPluralString("Restrictions", admin.kicked, new Object[0]));
            }
            data.description = stringBuilder.toString();
            return data;
        }

        public static MemberData from(TLRPC.TL_statsGroupTopInviter inviter, ArrayList<TLRPC.User> users) {
            MemberData data = new MemberData();
            long j = inviter.user_id;
            data.user_id = j;
            data.user = find(j, users);
            if (inviter.invitations > 0) {
                data.description = LocaleController.formatPluralString("Invitations", inviter.invitations, new Object[0]);
            } else {
                data.description = "";
            }
            return data;
        }

        public static TLRPC.User find(long user_id, ArrayList<TLRPC.User> users) {
            Iterator<TLRPC.User> it = users.iterator();
            while (it.hasNext()) {
                TLRPC.User user = it.next();
                if (user.id == user_id) {
                    return user;
                }
            }
            return null;
        }

        public void onClick(BaseFragment fragment) {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", this.user.id);
            MessagesController.getInstance(UserConfig.selectedAccount).putUser(this.user, false);
            fragment.presentFragment(new ProfileActivity(bundle));
        }

        public void onLongClick(TLRPC.ChatFull chat, StatisticActivity fragment, AlertDialog[] progressDialog) {
            onLongClick(chat, fragment, progressDialog, true);
        }

        private void onLongClick(final TLRPC.ChatFull chat, final StatisticActivity fragment, final AlertDialog[] progressDialog, boolean userIsPracticant) {
            TLRPC.TL_chatChannelParticipant currentUser;
            TLRPC.TL_chatChannelParticipant currentParticipant;
            boolean isAdmin;
            String str;
            int i;
            MessagesController.getInstance(UserConfig.selectedAccount).putUser(this.user, false);
            ArrayList<String> items = new ArrayList<>();
            final ArrayList<Integer> actions = new ArrayList<>();
            ArrayList<Integer> icons = new ArrayList<>();
            TLRPC.TL_chatChannelParticipant currentParticipant2 = null;
            TLRPC.TL_chatChannelParticipant currentUser2 = null;
            if (userIsPracticant && chat.participants.participants != null) {
                int n = chat.participants.participants.size();
                int i2 = 0;
                while (i2 < n) {
                    TLRPC.ChatParticipant participant = chat.participants.participants.get(i2);
                    int n2 = n;
                    if (participant.user_id == this.user.id && (participant instanceof TLRPC.TL_chatChannelParticipant)) {
                        currentParticipant2 = (TLRPC.TL_chatChannelParticipant) participant;
                    }
                    if (participant.user_id == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId && (participant instanceof TLRPC.TL_chatChannelParticipant)) {
                        currentUser2 = (TLRPC.TL_chatChannelParticipant) participant;
                    }
                    i2++;
                    n = n2;
                }
                currentParticipant = currentParticipant2;
                currentUser = currentUser2;
            } else {
                currentParticipant = null;
                currentUser = null;
            }
            items.add(LocaleController.getString("StatisticOpenProfile", R.string.StatisticOpenProfile));
            icons.add(Integer.valueOf((int) R.drawable.msg_openprofile));
            actions.add(2);
            items.add(LocaleController.getString("StatisticSearchUserHistory", R.string.StatisticSearchUserHistory));
            icons.add(Integer.valueOf((int) R.drawable.msg_msgbubble3));
            boolean z = true;
            actions.add(1);
            if (userIsPracticant && currentParticipant == null) {
                if (progressDialog[0] == null) {
                    progressDialog[0] = new AlertDialog(fragment.getFragmentView().getContext(), 3);
                    progressDialog[0].showDelayed(300L);
                }
                TLRPC.TL_channels_getParticipant request = new TLRPC.TL_channels_getParticipant();
                request.channel = MessagesController.getInstance(UserConfig.selectedAccount).getInputChannel(chat.id);
                request.participant = MessagesController.getInputPeer(this.user);
                ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(request, new RequestDelegate() { // from class: org.telegram.ui.StatisticActivity$MemberData$$ExternalSyntheticLambda3
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        StatisticActivity.MemberData.this.m4598xf92eb046(fragment, progressDialog, chat, tLObject, tL_error);
                    }
                });
            } else if (userIsPracticant && currentUser == null) {
                if (progressDialog[0] == null) {
                    progressDialog[0] = new AlertDialog(fragment.getFragmentView().getContext(), 3);
                    progressDialog[0].showDelayed(300L);
                }
                TLRPC.TL_channels_getParticipant request2 = new TLRPC.TL_channels_getParticipant();
                request2.channel = MessagesController.getInstance(UserConfig.selectedAccount).getInputChannel(chat.id);
                request2.participant = MessagesController.getInstance(UserConfig.selectedAccount).getInputPeer(UserConfig.getInstance(UserConfig.selectedAccount).clientUserId);
                ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(request2, new RequestDelegate() { // from class: org.telegram.ui.StatisticActivity$MemberData$$ExternalSyntheticLambda4
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        StatisticActivity.MemberData.this.m4600x6c1dcb84(fragment, progressDialog, chat, tLObject, tL_error);
                    }
                });
            } else {
                if (progressDialog[0] != null) {
                    progressDialog[0].dismiss();
                    progressDialog[0] = null;
                }
                if (currentUser != null && currentParticipant != null && currentUser.user_id != currentParticipant.user_id) {
                    TLRPC.ChannelParticipant channelParticipant = currentParticipant.channelParticipant;
                    boolean canEditAdmin = currentUser.channelParticipant.admin_rights != null && currentUser.channelParticipant.admin_rights.add_admins;
                    if (canEditAdmin && ((channelParticipant instanceof TLRPC.TL_channelParticipantCreator) || ((channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) && !channelParticipant.can_edit))) {
                        canEditAdmin = false;
                    }
                    if (canEditAdmin) {
                        if (channelParticipant.admin_rights != null) {
                            z = false;
                        }
                        boolean isAdmin2 = z;
                        if (isAdmin2) {
                            i = R.string.SetAsAdmin;
                            str = "SetAsAdmin";
                        } else {
                            i = R.string.EditAdminRights;
                            str = "EditAdminRights";
                        }
                        items.add(LocaleController.getString(str, i));
                        icons.add(Integer.valueOf(isAdmin2 ? R.drawable.msg_admins : R.drawable.msg_permissions));
                        actions.add(0);
                        isAdmin = isAdmin2;
                        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getParentActivity());
                        final TLRPC.TL_chatChannelParticipant finalCurrentParticipant = currentParticipant;
                        final boolean finalIsAdmin = isAdmin;
                        builder.setItems((CharSequence[]) items.toArray(new CharSequence[actions.size()]), AndroidUtilities.toIntArray(icons), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.StatisticActivity$MemberData$$ExternalSyntheticLambda0
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i3) {
                                StatisticActivity.MemberData.this.m4601x25955923(actions, chat, finalCurrentParticipant, finalIsAdmin, fragment, dialogInterface, i3);
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        fragment.showDialog(alertDialog);
                    }
                }
                isAdmin = false;
                AlertDialog.Builder builder2 = new AlertDialog.Builder(fragment.getParentActivity());
                final TLRPC.TL_chatChannelParticipant finalCurrentParticipant2 = currentParticipant;
                final boolean finalIsAdmin2 = isAdmin;
                builder2.setItems((CharSequence[]) items.toArray(new CharSequence[actions.size()]), AndroidUtilities.toIntArray(icons), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.StatisticActivity$MemberData$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        StatisticActivity.MemberData.this.m4601x25955923(actions, chat, finalCurrentParticipant2, finalIsAdmin2, fragment, dialogInterface, i3);
                    }
                });
                AlertDialog alertDialog2 = builder2.create();
                fragment.showDialog(alertDialog2);
            }
        }

        /* renamed from: lambda$onLongClick$1$org-telegram-ui-StatisticActivity$MemberData */
        public /* synthetic */ void m4598xf92eb046(final StatisticActivity fragment, final AlertDialog[] progressDialog, final TLRPC.ChatFull chat, final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.StatisticActivity$MemberData$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    StatisticActivity.MemberData.this.m4597x3fb722a7(fragment, progressDialog, error, response, chat);
                }
            });
        }

        /* renamed from: lambda$onLongClick$0$org-telegram-ui-StatisticActivity$MemberData */
        public /* synthetic */ void m4597x3fb722a7(StatisticActivity fragment, AlertDialog[] progressDialog, TLRPC.TL_error error, TLObject response, TLRPC.ChatFull chat) {
            if (fragment.isFinishing() || fragment.getFragmentView() == null || progressDialog[0] == null) {
                return;
            }
            if (error == null) {
                TLRPC.TL_channels_channelParticipant participant = (TLRPC.TL_channels_channelParticipant) response;
                TLRPC.TL_chatChannelParticipant chatChannelParticipant = new TLRPC.TL_chatChannelParticipant();
                chatChannelParticipant.channelParticipant = participant.participant;
                chatChannelParticipant.user_id = this.user.id;
                chat.participants.participants.add(0, chatChannelParticipant);
                onLongClick(chat, fragment, progressDialog);
                return;
            }
            onLongClick(chat, fragment, progressDialog, false);
        }

        /* renamed from: lambda$onLongClick$3$org-telegram-ui-StatisticActivity$MemberData */
        public /* synthetic */ void m4600x6c1dcb84(final StatisticActivity fragment, final AlertDialog[] progressDialog, final TLRPC.ChatFull chat, final TLObject response, final TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.StatisticActivity$MemberData$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    StatisticActivity.MemberData.this.m4599xb2a63de5(fragment, progressDialog, error, response, chat);
                }
            });
        }

        /* renamed from: lambda$onLongClick$2$org-telegram-ui-StatisticActivity$MemberData */
        public /* synthetic */ void m4599xb2a63de5(StatisticActivity fragment, AlertDialog[] progressDialog, TLRPC.TL_error error, TLObject response, TLRPC.ChatFull chat) {
            if (fragment.isFinishing() || fragment.getFragmentView() == null || progressDialog[0] == null) {
                return;
            }
            if (error == null) {
                TLRPC.TL_channels_channelParticipant participant = (TLRPC.TL_channels_channelParticipant) response;
                TLRPC.TL_chatChannelParticipant chatChannelParticipant = new TLRPC.TL_chatChannelParticipant();
                chatChannelParticipant.channelParticipant = participant.participant;
                chatChannelParticipant.user_id = UserConfig.getInstance(UserConfig.selectedAccount).clientUserId;
                chat.participants.participants.add(0, chatChannelParticipant);
                onLongClick(chat, fragment, progressDialog);
                return;
            }
            onLongClick(chat, fragment, progressDialog, false);
        }

        /* renamed from: lambda$onLongClick$4$org-telegram-ui-StatisticActivity$MemberData */
        public /* synthetic */ void m4601x25955923(ArrayList actions, TLRPC.ChatFull chat, final TLRPC.TL_chatChannelParticipant finalCurrentParticipant, final boolean finalIsAdmin, final StatisticActivity fragment, DialogInterface dialogInterface, int i) {
            if (((Integer) actions.get(i)).intValue() == 0) {
                final boolean[] needShowBulletin = new boolean[1];
                ChatRightsEditActivity newFragment = new ChatRightsEditActivity(this.user.id, chat.id, finalCurrentParticipant.channelParticipant.admin_rights, null, finalCurrentParticipant.channelParticipant.banned_rights, finalCurrentParticipant.channelParticipant.rank, 0, true, finalIsAdmin, null) { // from class: org.telegram.ui.StatisticActivity.MemberData.1
                    @Override // org.telegram.ui.ActionBar.BaseFragment
                    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
                        if (!isOpen && backward && needShowBulletin[0] && BulletinFactory.canShowBulletin(fragment)) {
                            BulletinFactory.createPromoteToAdminBulletin(fragment, MemberData.this.user.first_name).show();
                        }
                    }
                };
                newFragment.setDelegate(new ChatRightsEditActivity.ChatRightsEditActivityDelegate() { // from class: org.telegram.ui.StatisticActivity.MemberData.2
                    @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                    public void didSetRights(int rights, TLRPC.TL_chatAdminRights rightsAdmin, TLRPC.TL_chatBannedRights rightsBanned, String rank) {
                        if (rights == 0) {
                            finalCurrentParticipant.channelParticipant.admin_rights = null;
                            finalCurrentParticipant.channelParticipant.rank = "";
                            return;
                        }
                        finalCurrentParticipant.channelParticipant.admin_rights = rightsAdmin;
                        finalCurrentParticipant.channelParticipant.rank = rank;
                        if (finalIsAdmin) {
                            needShowBulletin[0] = true;
                        }
                    }

                    @Override // org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate
                    public void didChangeOwner(TLRPC.User user) {
                    }
                });
                fragment.presentFragment(newFragment);
            } else if (((Integer) actions.get(i)).intValue() == 2) {
                onClick(fragment);
            } else {
                Bundle bundle = new Bundle();
                bundle.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, chat.id);
                bundle.putLong("search_from_user_id", this.user.id);
                fragment.presentFragment(new ChatActivity(bundle));
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        int color = Theme.getColor(Theme.key_windowBackgroundWhite);
        return ColorUtils.calculateLuminance(color) > 0.699999988079071d;
    }
}
