package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.CheckBoxUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContentPreviewViewer;
/* loaded from: classes5.dex */
public class AdminLogFilterAlert extends BottomSheet {
    private ListAdapter adapter;
    private int adminsRow;
    private int allAdminsRow;
    private int callsRow;
    private ArrayList<TLRPC.ChannelParticipant> currentAdmins;
    private TLRPC.TL_channelAdminLogEventsFilter currentFilter;
    private AdminLogFilterAlertDelegate delegate;
    private int deleteRow;
    private int editRow;
    private boolean ignoreLayout;
    private int infoRow;
    private int invitesRow;
    private boolean isMegagroup;
    private int leavingRow;
    private RecyclerListView listView;
    private int membersRow;
    private FrameLayout pickerBottomLayout;
    private int pinnedRow;
    private int reqId;
    private int restrictionsRow;
    private BottomSheet.BottomSheetCell saveButton;
    private int scrollOffsetY;
    private LongSparseArray<TLRPC.User> selectedAdmins;
    private Drawable shadowDrawable;
    private Pattern urlPattern;

    /* loaded from: classes5.dex */
    public interface AdminLogFilterAlertDelegate {
        void didSelectRights(TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter, LongSparseArray<TLRPC.User> longSparseArray);
    }

    public AdminLogFilterAlert(Context context, TLRPC.TL_channelAdminLogEventsFilter filter, LongSparseArray<TLRPC.User> admins, boolean megagroup) {
        super(context, false);
        if (filter != null) {
            TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter = new TLRPC.TL_channelAdminLogEventsFilter();
            this.currentFilter = tL_channelAdminLogEventsFilter;
            tL_channelAdminLogEventsFilter.join = filter.join;
            this.currentFilter.leave = filter.leave;
            this.currentFilter.invite = filter.invite;
            this.currentFilter.ban = filter.ban;
            this.currentFilter.unban = filter.unban;
            this.currentFilter.kick = filter.kick;
            this.currentFilter.unkick = filter.unkick;
            this.currentFilter.promote = filter.promote;
            this.currentFilter.demote = filter.demote;
            this.currentFilter.info = filter.info;
            this.currentFilter.settings = filter.settings;
            this.currentFilter.pinned = filter.pinned;
            this.currentFilter.edit = filter.edit;
            this.currentFilter.delete = filter.delete;
            this.currentFilter.group_call = filter.group_call;
            this.currentFilter.invites = filter.invites;
        }
        if (admins != null) {
            this.selectedAdmins = admins.clone();
        }
        this.isMegagroup = megagroup;
        int rowCount = 1;
        if (megagroup) {
            int rowCount2 = 1 + 1;
            this.restrictionsRow = 1;
            rowCount = rowCount2;
        } else {
            this.restrictionsRow = -1;
        }
        int rowCount3 = rowCount + 1;
        this.adminsRow = rowCount;
        int rowCount4 = rowCount3 + 1;
        this.membersRow = rowCount3;
        int rowCount5 = rowCount4 + 1;
        this.invitesRow = rowCount4;
        int rowCount6 = rowCount5 + 1;
        this.infoRow = rowCount5;
        int rowCount7 = rowCount6 + 1;
        this.deleteRow = rowCount6;
        int rowCount8 = rowCount7 + 1;
        this.editRow = rowCount7;
        if (megagroup) {
            this.pinnedRow = rowCount8;
            rowCount8++;
        } else {
            this.pinnedRow = -1;
        }
        int rowCount9 = rowCount8 + 1;
        this.leavingRow = rowCount8;
        this.callsRow = rowCount9;
        this.allAdminsRow = rowCount9 + 1 + 1;
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.AdminLogFilterAlert.1
            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() == 0 && AdminLogFilterAlert.this.scrollOffsetY != 0 && ev.getY() < AdminLogFilterAlert.this.scrollOffsetY) {
                    AdminLogFilterAlert.this.dismiss();
                    return true;
                }
                return super.onInterceptTouchEvent(ev);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                return !AdminLogFilterAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                if (Build.VERSION.SDK_INT >= 21) {
                    height -= AndroidUtilities.statusBarHeight;
                }
                getMeasuredWidth();
                int contentSize = AndroidUtilities.dp(48.0f) + ((AdminLogFilterAlert.this.isMegagroup ? 11 : 8) * AndroidUtilities.dp(48.0f)) + AdminLogFilterAlert.this.backgroundPaddingTop + AndroidUtilities.dp(17.0f);
                if (AdminLogFilterAlert.this.currentAdmins != null) {
                    contentSize += ((AdminLogFilterAlert.this.currentAdmins.size() + 1) * AndroidUtilities.dp(48.0f)) + AndroidUtilities.dp(20.0f);
                }
                int padding = ((float) contentSize) < ((float) (height / 5)) * 3.2f ? 0 : (height / 5) * 2;
                if (padding != 0 && contentSize < height) {
                    padding -= height - contentSize;
                }
                if (padding == 0) {
                    padding = AdminLogFilterAlert.this.backgroundPaddingTop;
                }
                if (AdminLogFilterAlert.this.listView.getPaddingTop() != padding) {
                    AdminLogFilterAlert.this.ignoreLayout = true;
                    AdminLogFilterAlert.this.listView.setPadding(0, padding, 0, 0);
                    AdminLogFilterAlert.this.ignoreLayout = false;
                }
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                AdminLogFilterAlert.this.updateLayout();
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (AdminLogFilterAlert.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                AdminLogFilterAlert.this.shadowDrawable.setBounds(0, AdminLogFilterAlert.this.scrollOffsetY - AdminLogFilterAlert.this.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
                AdminLogFilterAlert.this.shadowDrawable.draw(canvas);
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.AdminLogFilterAlert.2
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent event) {
                boolean result = ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, AdminLogFilterAlert.this.listView, 0, null, this.resourcesProvider);
                return super.onInterceptTouchEvent(event) || result;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (AdminLogFilterAlert.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setClipToPadding(false);
        this.listView.setEnabled(true);
        this.listView.setGlowColor(Theme.getColor(Theme.key_dialogScrollGlow));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.AdminLogFilterAlert.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                AdminLogFilterAlert.this.updateLayout();
            }
        });
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.AdminLogFilterAlert$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                AdminLogFilterAlert.this.m2179lambda$new$0$orgtelegramuiComponentsAdminLogFilterAlert(view, i);
            }
        });
        this.containerView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        View shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        this.containerView.addView(shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        BottomSheet.BottomSheetCell bottomSheetCell = new BottomSheet.BottomSheetCell(context, 1);
        this.saveButton = bottomSheetCell;
        bottomSheetCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        this.saveButton.setTextAndIcon(LocaleController.getString("Save", R.string.Save).toUpperCase(), 0);
        this.saveButton.setTextColor(Theme.getColor(Theme.key_dialogTextBlue2));
        this.saveButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.AdminLogFilterAlert$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AdminLogFilterAlert.this.m2180lambda$new$1$orgtelegramuiComponentsAdminLogFilterAlert(view);
            }
        });
        this.containerView.addView(this.saveButton, LayoutHelper.createFrame(-1, 48, 83));
        this.adapter.notifyDataSetChanged();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-AdminLogFilterAlert */
    public /* synthetic */ void m2179lambda$new$0$orgtelegramuiComponentsAdminLogFilterAlert(View view, int position) {
        TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter;
        TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter2;
        TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter3;
        TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter4;
        TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter5;
        TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter6;
        if (view instanceof CheckBoxCell) {
            CheckBoxCell cell = (CheckBoxCell) view;
            boolean isChecked = cell.isChecked();
            cell.setChecked(!isChecked, true);
            if (position == 0) {
                if (isChecked) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter7 = new TLRPC.TL_channelAdminLogEventsFilter();
                    this.currentFilter = tL_channelAdminLogEventsFilter7;
                    tL_channelAdminLogEventsFilter7.invites = false;
                    tL_channelAdminLogEventsFilter7.group_call = false;
                    tL_channelAdminLogEventsFilter7.delete = false;
                    tL_channelAdminLogEventsFilter7.edit = false;
                    tL_channelAdminLogEventsFilter7.pinned = false;
                    tL_channelAdminLogEventsFilter7.settings = false;
                    tL_channelAdminLogEventsFilter7.info = false;
                    tL_channelAdminLogEventsFilter7.demote = false;
                    tL_channelAdminLogEventsFilter7.promote = false;
                    tL_channelAdminLogEventsFilter7.unkick = false;
                    tL_channelAdminLogEventsFilter7.kick = false;
                    tL_channelAdminLogEventsFilter7.unban = false;
                    tL_channelAdminLogEventsFilter7.ban = false;
                    tL_channelAdminLogEventsFilter7.invite = false;
                    tL_channelAdminLogEventsFilter7.leave = false;
                    tL_channelAdminLogEventsFilter7.join = false;
                } else {
                    this.currentFilter = null;
                }
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.listView.getChildAt(a);
                    RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(child);
                    int pos = holder.getAdapterPosition();
                    if (holder.getItemViewType() == 0 && pos > 0 && pos < this.allAdminsRow - 1) {
                        ((CheckBoxCell) child).setChecked(!isChecked, true);
                    }
                }
            } else if (position == this.allAdminsRow) {
                if (isChecked) {
                    this.selectedAdmins = new LongSparseArray<>();
                } else {
                    this.selectedAdmins = null;
                }
                int count2 = this.listView.getChildCount();
                for (int a2 = 0; a2 < count2; a2++) {
                    View child2 = this.listView.getChildAt(a2);
                    RecyclerView.ViewHolder holder2 = this.listView.findContainingViewHolder(child2);
                    holder2.getAdapterPosition();
                    if (holder2.getItemViewType() == 2) {
                        CheckBoxUserCell userCell = (CheckBoxUserCell) child2;
                        userCell.setChecked(!isChecked, true);
                    }
                }
            } else {
                if (this.currentFilter == null) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter8 = new TLRPC.TL_channelAdminLogEventsFilter();
                    this.currentFilter = tL_channelAdminLogEventsFilter8;
                    tL_channelAdminLogEventsFilter8.invites = true;
                    tL_channelAdminLogEventsFilter8.group_call = true;
                    tL_channelAdminLogEventsFilter8.delete = true;
                    tL_channelAdminLogEventsFilter8.edit = true;
                    tL_channelAdminLogEventsFilter8.pinned = true;
                    tL_channelAdminLogEventsFilter8.settings = true;
                    tL_channelAdminLogEventsFilter8.info = true;
                    tL_channelAdminLogEventsFilter8.demote = true;
                    tL_channelAdminLogEventsFilter8.promote = true;
                    tL_channelAdminLogEventsFilter8.unkick = true;
                    tL_channelAdminLogEventsFilter8.kick = true;
                    tL_channelAdminLogEventsFilter8.unban = true;
                    tL_channelAdminLogEventsFilter8.ban = true;
                    tL_channelAdminLogEventsFilter8.invite = true;
                    tL_channelAdminLogEventsFilter8.leave = true;
                    tL_channelAdminLogEventsFilter8.join = true;
                    RecyclerView.ViewHolder holder3 = this.listView.findViewHolderForAdapterPosition(0);
                    if (holder3 != null) {
                        ((CheckBoxCell) holder3.itemView).setChecked(false, true);
                    }
                }
                if (position == this.restrictionsRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter9 = this.currentFilter;
                    boolean z = !tL_channelAdminLogEventsFilter9.kick;
                    tL_channelAdminLogEventsFilter9.unban = z;
                    tL_channelAdminLogEventsFilter9.unkick = z;
                    tL_channelAdminLogEventsFilter9.ban = z;
                    tL_channelAdminLogEventsFilter9.kick = z;
                } else if (position == this.adminsRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter10 = this.currentFilter;
                    boolean z2 = !tL_channelAdminLogEventsFilter10.demote;
                    tL_channelAdminLogEventsFilter10.demote = z2;
                    tL_channelAdminLogEventsFilter10.promote = z2;
                } else if (position == this.membersRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter11 = this.currentFilter;
                    boolean z3 = !tL_channelAdminLogEventsFilter11.join;
                    tL_channelAdminLogEventsFilter11.join = z3;
                    tL_channelAdminLogEventsFilter11.invite = z3;
                } else if (position == this.infoRow) {
                    TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter12 = this.currentFilter;
                    boolean z4 = !tL_channelAdminLogEventsFilter12.info;
                    tL_channelAdminLogEventsFilter12.settings = z4;
                    tL_channelAdminLogEventsFilter12.info = z4;
                } else if (position == this.deleteRow) {
                    this.currentFilter.delete = !tL_channelAdminLogEventsFilter6.delete;
                } else if (position == this.editRow) {
                    this.currentFilter.edit = !tL_channelAdminLogEventsFilter5.edit;
                } else if (position == this.pinnedRow) {
                    this.currentFilter.pinned = !tL_channelAdminLogEventsFilter4.pinned;
                } else if (position == this.leavingRow) {
                    this.currentFilter.leave = !tL_channelAdminLogEventsFilter3.leave;
                } else if (position == this.callsRow) {
                    this.currentFilter.group_call = !tL_channelAdminLogEventsFilter2.group_call;
                } else if (position == this.invitesRow) {
                    this.currentFilter.invites = !tL_channelAdminLogEventsFilter.invites;
                }
            }
            TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter13 = this.currentFilter;
            if (tL_channelAdminLogEventsFilter13 != null && !tL_channelAdminLogEventsFilter13.join && !this.currentFilter.leave && !this.currentFilter.invite && !this.currentFilter.ban && !this.currentFilter.invites && !this.currentFilter.unban && !this.currentFilter.kick && !this.currentFilter.unkick && !this.currentFilter.promote && !this.currentFilter.demote && !this.currentFilter.info && !this.currentFilter.settings && !this.currentFilter.pinned && !this.currentFilter.edit && !this.currentFilter.delete && !this.currentFilter.group_call) {
                this.saveButton.setEnabled(false);
                this.saveButton.setAlpha(0.5f);
                return;
            }
            this.saveButton.setEnabled(true);
            this.saveButton.setAlpha(1.0f);
        } else if (view instanceof CheckBoxUserCell) {
            CheckBoxUserCell checkBoxUserCell = (CheckBoxUserCell) view;
            if (this.selectedAdmins == null) {
                this.selectedAdmins = new LongSparseArray<>();
                RecyclerView.ViewHolder holder4 = this.listView.findViewHolderForAdapterPosition(this.allAdminsRow);
                if (holder4 != null) {
                    ((CheckBoxCell) holder4.itemView).setChecked(false, true);
                }
                for (int a3 = 0; a3 < this.currentAdmins.size(); a3++) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(MessageObject.getPeerId(this.currentAdmins.get(a3).peer)));
                    this.selectedAdmins.put(user.id, user);
                }
            }
            boolean isChecked2 = checkBoxUserCell.isChecked();
            TLRPC.User user2 = checkBoxUserCell.getCurrentUser();
            if (isChecked2) {
                this.selectedAdmins.remove(user2.id);
            } else {
                this.selectedAdmins.put(user2.id, user2);
            }
            checkBoxUserCell.setChecked(!isChecked2, true);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-AdminLogFilterAlert */
    public /* synthetic */ void m2180lambda$new$1$orgtelegramuiComponentsAdminLogFilterAlert(View v) {
        this.delegate.didSelectRights(this.currentFilter, this.selectedAdmins);
        dismiss();
    }

    public void setCurrentAdmins(ArrayList<TLRPC.ChannelParticipant> admins) {
        this.currentAdmins = admins;
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void setAdminLogFilterAlertDelegate(AdminLogFilterAlertDelegate adminLogFilterAlertDelegate) {
        this.delegate = adminLogFilterAlertDelegate;
    }

    public void updateLayout() {
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.containerView.invalidate();
            return;
        }
        int newOffset = 0;
        View child = this.listView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(child);
        int top = child.getTop() - AndroidUtilities.dp(8.0f);
        if (top > 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        if (this.scrollOffsetY != newOffset) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = newOffset;
            recyclerListView2.setTopGlowOffset(newOffset);
            this.containerView.invalidate();
        }
    }

    /* loaded from: classes5.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;

        public ListAdapter(Context context) {
            AdminLogFilterAlert.this = r1;
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return (AdminLogFilterAlert.this.isMegagroup ? 11 : 8) + (AdminLogFilterAlert.this.currentAdmins != null ? AdminLogFilterAlert.this.currentAdmins.size() + 2 : 0);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position < AdminLogFilterAlert.this.allAdminsRow - 1 || position == AdminLogFilterAlert.this.allAdminsRow) {
                return 0;
            }
            return position == AdminLogFilterAlert.this.allAdminsRow - 1 ? 1 : 2;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() != 1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FrameLayout view = null;
            switch (viewType) {
                case 0:
                    view = new CheckBoxCell(this.context, 1, 21, AdminLogFilterAlert.this.resourcesProvider);
                    break;
                case 1:
                    ShadowSectionCell shadowSectionCell = new ShadowSectionCell(this.context, 18);
                    view = new FrameLayout(this.context);
                    view.addView(shadowSectionCell, LayoutHelper.createFrame(-1, -1.0f));
                    view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
                    break;
                case 2:
                    view = new CheckBoxUserCell(this.context, true);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    CheckBoxCell cell = (CheckBoxCell) holder.itemView;
                    if (position == 0) {
                        if (AdminLogFilterAlert.this.currentFilter != null) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.restrictionsRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && (!AdminLogFilterAlert.this.currentFilter.kick || !AdminLogFilterAlert.this.currentFilter.ban || !AdminLogFilterAlert.this.currentFilter.unkick || !AdminLogFilterAlert.this.currentFilter.unban)) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.adminsRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && (!AdminLogFilterAlert.this.currentFilter.promote || !AdminLogFilterAlert.this.currentFilter.demote)) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.membersRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && (!AdminLogFilterAlert.this.currentFilter.invite || !AdminLogFilterAlert.this.currentFilter.join)) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.infoRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.info) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.deleteRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.delete) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.editRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.edit) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.pinnedRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.pinned) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.leavingRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.leave) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.callsRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.group_call) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.invitesRow) {
                        if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.invites) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else if (position == AdminLogFilterAlert.this.allAdminsRow) {
                        if (AdminLogFilterAlert.this.selectedAdmins != null) {
                            z = false;
                        }
                        cell.setChecked(z, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                default:
                    return;
                case 2:
                    CheckBoxUserCell userCell = (CheckBoxUserCell) holder.itemView;
                    long userId = MessageObject.getPeerId(((TLRPC.ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((position - AdminLogFilterAlert.this.allAdminsRow) - 1)).peer);
                    if (AdminLogFilterAlert.this.selectedAdmins != null && AdminLogFilterAlert.this.selectedAdmins.indexOfKey(userId) < 0) {
                        z = false;
                    }
                    userCell.setChecked(z, false);
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
                case 0:
                    CheckBoxCell cell = (CheckBoxCell) holder.itemView;
                    if (position != 0) {
                        if (position != AdminLogFilterAlert.this.restrictionsRow) {
                            if (position != AdminLogFilterAlert.this.adminsRow) {
                                if (position != AdminLogFilterAlert.this.membersRow) {
                                    if (position == AdminLogFilterAlert.this.infoRow) {
                                        if (AdminLogFilterAlert.this.isMegagroup) {
                                            String string = LocaleController.getString("EventLogFilterGroupInfo", R.string.EventLogFilterGroupInfo);
                                            if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                                                z = true;
                                            }
                                            cell.setText(string, "", z, true);
                                            return;
                                        }
                                        String string2 = LocaleController.getString("EventLogFilterChannelInfo", R.string.EventLogFilterChannelInfo);
                                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.info) {
                                            z = true;
                                        }
                                        cell.setText(string2, "", z, true);
                                        return;
                                    } else if (position != AdminLogFilterAlert.this.deleteRow) {
                                        if (position != AdminLogFilterAlert.this.editRow) {
                                            if (position != AdminLogFilterAlert.this.pinnedRow) {
                                                if (position != AdminLogFilterAlert.this.leavingRow) {
                                                    if (position != AdminLogFilterAlert.this.callsRow) {
                                                        if (position != AdminLogFilterAlert.this.invitesRow) {
                                                            if (position == AdminLogFilterAlert.this.allAdminsRow) {
                                                                String string3 = LocaleController.getString("EventLogAllAdmins", R.string.EventLogAllAdmins);
                                                                if (AdminLogFilterAlert.this.selectedAdmins == null) {
                                                                    z = true;
                                                                }
                                                                cell.setText(string3, "", z, true);
                                                                return;
                                                            }
                                                            return;
                                                        }
                                                        String string4 = LocaleController.getString("EventLogFilterInvites", R.string.EventLogFilterInvites);
                                                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.invites) {
                                                            z = true;
                                                        }
                                                        cell.setText(string4, "", z, true);
                                                        return;
                                                    }
                                                    String string5 = LocaleController.getString("EventLogFilterCalls", R.string.EventLogFilterCalls);
                                                    if (AdminLogFilterAlert.this.currentFilter != null && !AdminLogFilterAlert.this.currentFilter.group_call) {
                                                        z2 = false;
                                                    }
                                                    cell.setText(string5, "", z2, false);
                                                    return;
                                                }
                                                String string6 = LocaleController.getString("EventLogFilterLeavingMembers", R.string.EventLogFilterLeavingMembers);
                                                boolean z3 = AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.leave;
                                                if (AdminLogFilterAlert.this.callsRow != -1) {
                                                    z = true;
                                                }
                                                cell.setText(string6, "", z3, z);
                                                return;
                                            }
                                            String string7 = LocaleController.getString("EventLogFilterPinnedMessages", R.string.EventLogFilterPinnedMessages);
                                            if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.pinned) {
                                                z = true;
                                            }
                                            cell.setText(string7, "", z, true);
                                            return;
                                        }
                                        String string8 = LocaleController.getString("EventLogFilterEditedMessages", R.string.EventLogFilterEditedMessages);
                                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.edit) {
                                            z = true;
                                        }
                                        cell.setText(string8, "", z, true);
                                        return;
                                    } else {
                                        String string9 = LocaleController.getString("EventLogFilterDeletedMessages", R.string.EventLogFilterDeletedMessages);
                                        if (AdminLogFilterAlert.this.currentFilter == null || AdminLogFilterAlert.this.currentFilter.delete) {
                                            z = true;
                                        }
                                        cell.setText(string9, "", z, true);
                                        return;
                                    }
                                }
                                String string10 = LocaleController.getString("EventLogFilterNewMembers", R.string.EventLogFilterNewMembers);
                                if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.invite && AdminLogFilterAlert.this.currentFilter.join)) {
                                    z = true;
                                }
                                cell.setText(string10, "", z, true);
                                return;
                            }
                            String string11 = LocaleController.getString("EventLogFilterNewAdmins", R.string.EventLogFilterNewAdmins);
                            if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.promote && AdminLogFilterAlert.this.currentFilter.demote)) {
                                z = true;
                            }
                            cell.setText(string11, "", z, true);
                            return;
                        }
                        String string12 = LocaleController.getString("EventLogFilterNewRestrictions", R.string.EventLogFilterNewRestrictions);
                        if (AdminLogFilterAlert.this.currentFilter == null || (AdminLogFilterAlert.this.currentFilter.kick && AdminLogFilterAlert.this.currentFilter.ban && AdminLogFilterAlert.this.currentFilter.unkick && AdminLogFilterAlert.this.currentFilter.unban)) {
                            z = true;
                        }
                        cell.setText(string12, "", z, true);
                        return;
                    }
                    String string13 = LocaleController.getString("EventLogFilterAll", R.string.EventLogFilterAll);
                    if (AdminLogFilterAlert.this.currentFilter == null) {
                        z = true;
                    }
                    cell.setText(string13, "", z, true);
                    return;
                case 1:
                default:
                    return;
                case 2:
                    CheckBoxUserCell userCell = (CheckBoxUserCell) holder.itemView;
                    long userId = MessageObject.getPeerId(((TLRPC.ChannelParticipant) AdminLogFilterAlert.this.currentAdmins.get((position - AdminLogFilterAlert.this.allAdminsRow) - 1)).peer);
                    TLRPC.User user = MessagesController.getInstance(AdminLogFilterAlert.this.currentAccount).getUser(Long.valueOf(userId));
                    boolean z4 = AdminLogFilterAlert.this.selectedAdmins == null || AdminLogFilterAlert.this.selectedAdmins.indexOfKey(userId) >= 0;
                    if (position != getItemCount() - 1) {
                        z = true;
                    }
                    userCell.setUser(user, z4, z);
                    return;
            }
        }
    }
}
