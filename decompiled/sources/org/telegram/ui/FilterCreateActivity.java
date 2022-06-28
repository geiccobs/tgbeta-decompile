package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilterCreateActivity;
import org.telegram.ui.FilterUsersActivity;
/* loaded from: classes4.dex */
public class FilterCreateActivity extends BaseFragment {
    private static final int MAX_NAME_LENGTH = 12;
    private static final int done_button = 1;
    private ListAdapter adapter;
    private boolean creatingNew;
    private ActionBarMenuItem doneItem;
    private int excludeAddRow;
    private int excludeArchivedRow;
    private int excludeEndRow;
    private boolean excludeExpanded;
    private int excludeHeaderRow;
    private int excludeMutedRow;
    private int excludeReadRow;
    private int excludeSectionRow;
    private int excludeShowMoreRow;
    private int excludeStartRow;
    private MessagesController.DialogFilter filter;
    private boolean hasUserChanged;
    private int imageRow;
    private int includeAddRow;
    private int includeBotsRow;
    private int includeChannelsRow;
    private int includeContactsRow;
    private int includeEndRow;
    private boolean includeExpanded;
    private int includeGroupsRow;
    private int includeHeaderRow;
    private int includeNonContactsRow;
    private int includeSectionRow;
    private int includeShowMoreRow;
    private int includeStartRow;
    private RecyclerListView listView;
    private boolean nameChangedManually;
    private int namePreSectionRow;
    private int nameRow;
    private int nameSectionRow;
    private ArrayList<Long> newAlwaysShow;
    private int newFilterFlags;
    private String newFilterName;
    private ArrayList<Long> newNeverShow;
    private LongSparseIntArray newPinned;
    private int removeRow;
    private int removeSectionRow;
    private int rowCount;

    /* loaded from: classes4.dex */
    public static class HintInnerCell extends FrameLayout {
        private RLottieImageView imageView;

        public HintInnerCell(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(R.raw.filter_new, 100, 100);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.playAnimation();
            addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
            this.imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$HintInnerCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    FilterCreateActivity.HintInnerCell.this.m3433lambda$new$0$orgtelegramuiFilterCreateActivity$HintInnerCell(view);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-FilterCreateActivity$HintInnerCell */
        public /* synthetic */ void m3433lambda$new$0$orgtelegramuiFilterCreateActivity$HintInnerCell(View v) {
            if (!this.imageView.isPlaying()) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(156.0f), C.BUFFER_FLAG_ENCRYPTED));
        }
    }

    public FilterCreateActivity() {
        this(null, null);
    }

    public FilterCreateActivity(MessagesController.DialogFilter dialogFilter) {
        this(dialogFilter, null);
    }

    public FilterCreateActivity(MessagesController.DialogFilter dialogFilter, ArrayList<Long> alwaysShow) {
        this.rowCount = 0;
        this.filter = dialogFilter;
        if (dialogFilter == null) {
            MessagesController.DialogFilter dialogFilter2 = new MessagesController.DialogFilter();
            this.filter = dialogFilter2;
            dialogFilter2.id = 2;
            while (getMessagesController().dialogFiltersById.get(this.filter.id) != null) {
                this.filter.id++;
            }
            this.filter.name = "";
            this.creatingNew = true;
        }
        this.newFilterName = this.filter.name;
        this.newFilterFlags = this.filter.flags;
        ArrayList<Long> arrayList = new ArrayList<>(this.filter.alwaysShow);
        this.newAlwaysShow = arrayList;
        if (alwaysShow != null) {
            arrayList.addAll(alwaysShow);
        }
        this.newNeverShow = new ArrayList<>(this.filter.neverShow);
        this.newPinned = this.filter.pinnedDialogs.clone();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        updateRows();
        return super.onFragmentCreate();
    }

    private void updateRows() {
        this.rowCount = 0;
        if (this.creatingNew) {
            this.rowCount = 0 + 1;
            this.imageRow = 0;
            this.namePreSectionRow = -1;
        } else {
            this.imageRow = -1;
            this.rowCount = 0 + 1;
            this.namePreSectionRow = 0;
        }
        int i = this.rowCount;
        int i2 = i + 1;
        this.rowCount = i2;
        this.nameRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.nameSectionRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.includeHeaderRow = i3;
        this.rowCount = i4 + 1;
        this.includeAddRow = i4;
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0) {
            int i5 = this.rowCount;
            this.rowCount = i5 + 1;
            this.includeContactsRow = i5;
        } else {
            this.includeContactsRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.includeNonContactsRow = i6;
        } else {
            this.includeNonContactsRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.includeGroupsRow = i7;
        } else {
            this.includeGroupsRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0) {
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.includeChannelsRow = i8;
        } else {
            this.includeChannelsRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0) {
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.includeBotsRow = i9;
        } else {
            this.includeBotsRow = -1;
        }
        if (!this.newAlwaysShow.isEmpty()) {
            this.includeStartRow = this.rowCount;
            int count = (this.includeExpanded || this.newAlwaysShow.size() < 8) ? this.newAlwaysShow.size() : Math.min(5, this.newAlwaysShow.size());
            int i10 = this.rowCount + count;
            this.rowCount = i10;
            this.includeEndRow = i10;
            if (count != this.newAlwaysShow.size()) {
                int i11 = this.rowCount;
                this.rowCount = i11 + 1;
                this.includeShowMoreRow = i11;
            } else {
                this.includeShowMoreRow = -1;
            }
        } else {
            this.includeStartRow = -1;
            this.includeEndRow = -1;
            this.includeShowMoreRow = -1;
        }
        int i12 = this.rowCount;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.includeSectionRow = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.excludeHeaderRow = i13;
        this.rowCount = i14 + 1;
        this.excludeAddRow = i14;
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
            int i15 = this.rowCount;
            this.rowCount = i15 + 1;
            this.excludeMutedRow = i15;
        } else {
            this.excludeMutedRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
            int i16 = this.rowCount;
            this.rowCount = i16 + 1;
            this.excludeReadRow = i16;
        } else {
            this.excludeReadRow = -1;
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0) {
            int i17 = this.rowCount;
            this.rowCount = i17 + 1;
            this.excludeArchivedRow = i17;
        } else {
            this.excludeArchivedRow = -1;
        }
        if (!this.newNeverShow.isEmpty()) {
            this.excludeStartRow = this.rowCount;
            int count2 = (this.excludeExpanded || this.newNeverShow.size() < 8) ? this.newNeverShow.size() : Math.min(5, this.newNeverShow.size());
            int i18 = this.rowCount + count2;
            this.rowCount = i18;
            this.excludeEndRow = i18;
            if (count2 != this.newNeverShow.size()) {
                int i19 = this.rowCount;
                this.rowCount = i19 + 1;
                this.excludeShowMoreRow = i19;
            } else {
                this.excludeShowMoreRow = -1;
            }
        } else {
            this.excludeStartRow = -1;
            this.excludeEndRow = -1;
            this.excludeShowMoreRow = -1;
        }
        int i20 = this.rowCount;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.excludeSectionRow = i20;
        if (!this.creatingNew) {
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.removeRow = i21;
            this.rowCount = i22 + 1;
            this.removeSectionRow = i22;
        } else {
            this.removeRow = -1;
            this.removeSectionRow = -1;
        }
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        ActionBarMenu menu = this.actionBar.createMenu();
        if (this.creatingNew) {
            this.actionBar.setTitle(LocaleController.getString("FilterNew", R.string.FilterNew));
        } else {
            TextPaint paint = new TextPaint(1);
            paint.setTextSize(AndroidUtilities.dp(20.0f));
            this.actionBar.setTitle(Emoji.replaceEmoji(this.filter.name, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.FilterCreateActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (FilterCreateActivity.this.checkDiscard()) {
                        FilterCreateActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    FilterCreateActivity.this.processDone();
                }
            }
        });
        this.doneItem = menu.addItem(1, LocaleController.getString("Save", R.string.Save).toUpperCase());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.FilterCreateActivity.2
            @Override // android.view.ViewGroup, android.view.View
            public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
                return false;
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                FilterCreateActivity.this.m3428lambda$createView$4$orgtelegramuiFilterCreateActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return FilterCreateActivity.this.m3429lambda$createView$5$orgtelegramuiFilterCreateActivity(view, i);
            }
        });
        checkDoneButton(false);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-FilterCreateActivity */
    public /* synthetic */ void m3428lambda$createView$4$orgtelegramuiFilterCreateActivity(View view, final int position) {
        if (getParentActivity() == null) {
            return;
        }
        boolean z = true;
        if (position == this.includeShowMoreRow) {
            this.includeExpanded = true;
            updateRows();
        } else if (position == this.excludeShowMoreRow) {
            this.excludeExpanded = true;
            updateRows();
        } else if (position == this.includeAddRow || position == this.excludeAddRow) {
            ArrayList<Long> arrayList = position == this.excludeAddRow ? this.newNeverShow : this.newAlwaysShow;
            if (position != this.includeAddRow) {
                z = false;
            }
            FilterUsersActivity fragment = new FilterUsersActivity(z, arrayList, this.newFilterFlags);
            fragment.setDelegate(new FilterUsersActivity.FilterUsersActivityDelegate() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda5
                @Override // org.telegram.ui.FilterUsersActivity.FilterUsersActivityDelegate
                public final void didSelectChats(ArrayList arrayList2, int i) {
                    FilterCreateActivity.this.m3424lambda$createView$0$orgtelegramuiFilterCreateActivity(position, arrayList2, i);
                }
            });
            presentFragment(fragment);
        } else if (position == this.removeRow) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("FilterDelete", R.string.FilterDelete));
            builder.setMessage(LocaleController.getString("FilterDeleteAlert", R.string.FilterDeleteAlert));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda8
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FilterCreateActivity.this.m3427lambda$createView$3$orgtelegramuiFilterCreateActivity(dialogInterface, i);
                }
            });
            AlertDialog alertDialog = builder.create();
            showDialog(alertDialog);
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
            }
        } else if (position == this.nameRow) {
            PollEditTextCell cell = (PollEditTextCell) view;
            cell.getTextView().requestFocus();
            AndroidUtilities.showKeyboard(cell.getTextView());
        } else if (view instanceof UserCell) {
            UserCell cell2 = (UserCell) view;
            CharSequence name = cell2.getName();
            Object currentObject = cell2.getCurrentObject();
            if (position >= this.includeSectionRow) {
                z = false;
            }
            showRemoveAlert(position, name, currentObject, z);
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-FilterCreateActivity */
    public /* synthetic */ void m3424lambda$createView$0$orgtelegramuiFilterCreateActivity(int position, ArrayList ids, int flags) {
        this.newFilterFlags = flags;
        if (position == this.excludeAddRow) {
            this.newNeverShow = ids;
            for (int a = 0; a < this.newNeverShow.size(); a++) {
                Long id = this.newNeverShow.get(a);
                this.newAlwaysShow.remove(id);
                this.newPinned.delete(id.longValue());
            }
        } else {
            this.newAlwaysShow = ids;
            for (int a2 = 0; a2 < this.newAlwaysShow.size(); a2++) {
                this.newNeverShow.remove(this.newAlwaysShow.get(a2));
            }
            ArrayList<Long> toRemove = new ArrayList<>();
            int N = this.newPinned.size();
            for (int a3 = 0; a3 < N; a3++) {
                Long did = Long.valueOf(this.newPinned.keyAt(a3));
                if (!DialogObject.isEncryptedDialog(did.longValue()) && !this.newAlwaysShow.contains(did)) {
                    toRemove.add(did);
                }
            }
            int N2 = toRemove.size();
            for (int a4 = 0; a4 < N2; a4++) {
                this.newPinned.delete(toRemove.get(a4).longValue());
            }
        }
        fillFilterName();
        checkDoneButton(false);
        updateRows();
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-FilterCreateActivity */
    public /* synthetic */ void m3427lambda$createView$3$orgtelegramuiFilterCreateActivity(DialogInterface dialog, int which) {
        AlertDialog progressDialog = null;
        if (getParentActivity() != null) {
            progressDialog = new AlertDialog(getParentActivity(), 3);
            progressDialog.setCanCancel(false);
            progressDialog.show();
        }
        final AlertDialog progressDialogFinal = progressDialog;
        TLRPC.TL_messages_updateDialogFilter req = new TLRPC.TL_messages_updateDialogFilter();
        req.id = this.filter.id;
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda14
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FilterCreateActivity.this.m3426lambda$createView$2$orgtelegramuiFilterCreateActivity(progressDialogFinal, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-FilterCreateActivity */
    public /* synthetic */ void m3426lambda$createView$2$orgtelegramuiFilterCreateActivity(final AlertDialog progressDialogFinal, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                FilterCreateActivity.this.m3425lambda$createView$1$orgtelegramuiFilterCreateActivity(progressDialogFinal);
            }
        });
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-FilterCreateActivity */
    public /* synthetic */ void m3425lambda$createView$1$orgtelegramuiFilterCreateActivity(AlertDialog progressDialogFinal) {
        if (progressDialogFinal != null) {
            try {
                progressDialogFinal.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        getMessagesController().removeFilter(this.filter);
        getMessagesStorage().deleteDialogFilter(this.filter);
        finishFragment();
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-FilterCreateActivity */
    public /* synthetic */ boolean m3429lambda$createView$5$orgtelegramuiFilterCreateActivity(View view, int position) {
        boolean z = false;
        if (view instanceof UserCell) {
            UserCell cell = (UserCell) view;
            CharSequence name = cell.getName();
            Object currentObject = cell.getCurrentObject();
            if (position < this.includeSectionRow) {
                z = true;
            }
            showRemoveAlert(position, name, currentObject, z);
            return true;
        }
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        return checkDiscard();
    }

    private void fillFilterName() {
        if (this.creatingNew) {
            if (!TextUtils.isEmpty(this.newFilterName) && this.nameChangedManually) {
                return;
            }
            int flags = this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
            String newName = "";
            if ((MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS & flags) == MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) {
                if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                    newName = LocaleController.getString("FilterNameUnread", R.string.FilterNameUnread);
                } else if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
                    newName = LocaleController.getString("FilterNameNonMuted", R.string.FilterNameNonMuted);
                }
            } else if ((MessagesController.DIALOG_FILTER_FLAG_CONTACTS & flags) != 0) {
                if ((flags & (MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ (-1))) == 0) {
                    newName = LocaleController.getString("FilterContacts", R.string.FilterContacts);
                }
            } else if ((MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS & flags) != 0) {
                if ((flags & (MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ (-1))) == 0) {
                    newName = LocaleController.getString("FilterNonContacts", R.string.FilterNonContacts);
                }
            } else if ((MessagesController.DIALOG_FILTER_FLAG_GROUPS & flags) != 0) {
                if ((flags & (MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ (-1))) == 0) {
                    newName = LocaleController.getString("FilterGroups", R.string.FilterGroups);
                }
            } else if ((MessagesController.DIALOG_FILTER_FLAG_BOTS & flags) != 0) {
                if ((flags & (MessagesController.DIALOG_FILTER_FLAG_BOTS ^ (-1))) == 0) {
                    newName = LocaleController.getString("FilterBots", R.string.FilterBots);
                }
            } else if ((MessagesController.DIALOG_FILTER_FLAG_CHANNELS & flags) != 0 && (flags & (MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ (-1))) == 0) {
                newName = LocaleController.getString("FilterChannels", R.string.FilterChannels);
            }
            if (newName != null && newName.length() > 12) {
                newName = "";
            }
            this.newFilterName = newName;
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.nameRow);
            if (holder != null) {
                this.adapter.onViewAttachedToWindow(holder);
            }
        }
    }

    public boolean checkDiscard() {
        if (this.doneItem.getAlpha() == 1.0f) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            if (this.creatingNew) {
                builder.setTitle(LocaleController.getString("FilterDiscardNewTitle", R.string.FilterDiscardNewTitle));
                builder.setMessage(LocaleController.getString("FilterDiscardNewAlert", R.string.FilterDiscardNewAlert));
                builder.setPositiveButton(LocaleController.getString("FilterDiscardNewSave", R.string.FilterDiscardNewSave), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        FilterCreateActivity.this.m3421lambda$checkDiscard$6$orgtelegramuiFilterCreateActivity(dialogInterface, i);
                    }
                });
            } else {
                builder.setTitle(LocaleController.getString("FilterDiscardTitle", R.string.FilterDiscardTitle));
                builder.setMessage(LocaleController.getString("FilterDiscardAlert", R.string.FilterDiscardAlert));
                builder.setPositiveButton(LocaleController.getString("ApplyTheme", R.string.ApplyTheme), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda6
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        FilterCreateActivity.this.m3422lambda$checkDiscard$7$orgtelegramuiFilterCreateActivity(dialogInterface, i);
                    }
                });
            }
            builder.setNegativeButton(LocaleController.getString("PassportDiscard", R.string.PassportDiscard), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda7
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FilterCreateActivity.this.m3423lambda$checkDiscard$8$orgtelegramuiFilterCreateActivity(dialogInterface, i);
                }
            });
            showDialog(builder.create());
            return false;
        }
        return true;
    }

    /* renamed from: lambda$checkDiscard$6$org-telegram-ui-FilterCreateActivity */
    public /* synthetic */ void m3421lambda$checkDiscard$6$orgtelegramuiFilterCreateActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* renamed from: lambda$checkDiscard$7$org-telegram-ui-FilterCreateActivity */
    public /* synthetic */ void m3422lambda$checkDiscard$7$orgtelegramuiFilterCreateActivity(DialogInterface dialogInterface, int i) {
        processDone();
    }

    /* renamed from: lambda$checkDiscard$8$org-telegram-ui-FilterCreateActivity */
    public /* synthetic */ void m3423lambda$checkDiscard$8$orgtelegramuiFilterCreateActivity(DialogInterface dialog, int which) {
        finishFragment();
    }

    private void showRemoveAlert(final int position, CharSequence name, Object object, final boolean include) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (include) {
            builder.setTitle(LocaleController.getString("FilterRemoveInclusionTitle", R.string.FilterRemoveInclusionTitle));
            if (object instanceof String) {
                builder.setMessage(LocaleController.formatString("FilterRemoveInclusionText", R.string.FilterRemoveInclusionText, name));
            } else if (object instanceof TLRPC.User) {
                builder.setMessage(LocaleController.formatString("FilterRemoveInclusionUserText", R.string.FilterRemoveInclusionUserText, name));
            } else {
                builder.setMessage(LocaleController.formatString("FilterRemoveInclusionChatText", R.string.FilterRemoveInclusionChatText, name));
            }
        } else {
            builder.setTitle(LocaleController.getString("FilterRemoveExclusionTitle", R.string.FilterRemoveExclusionTitle));
            if (object instanceof String) {
                builder.setMessage(LocaleController.formatString("FilterRemoveExclusionText", R.string.FilterRemoveExclusionText, name));
            } else if (object instanceof TLRPC.User) {
                builder.setMessage(LocaleController.formatString("FilterRemoveExclusionUserText", R.string.FilterRemoveExclusionUserText, name));
            } else {
                builder.setMessage(LocaleController.formatString("FilterRemoveExclusionChatText", R.string.FilterRemoveExclusionChatText, name));
            }
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("StickersRemove", R.string.StickersRemove), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda9
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                FilterCreateActivity.this.m3432lambda$showRemoveAlert$9$orgtelegramuiFilterCreateActivity(position, include, dialogInterface, i);
            }
        });
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
        }
    }

    /* renamed from: lambda$showRemoveAlert$9$org-telegram-ui-FilterCreateActivity */
    public /* synthetic */ void m3432lambda$showRemoveAlert$9$orgtelegramuiFilterCreateActivity(int position, boolean include, DialogInterface dialogInterface, int i) {
        if (position == this.includeContactsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_CONTACTS ^ (-1);
        } else if (position == this.includeNonContactsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS ^ (-1);
        } else if (position == this.includeGroupsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_GROUPS ^ (-1);
        } else if (position == this.includeChannelsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_CHANNELS ^ (-1);
        } else if (position == this.includeBotsRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_BOTS ^ (-1);
        } else if (position == this.excludeArchivedRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED ^ (-1);
        } else if (position == this.excludeMutedRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED ^ (-1);
        } else if (position == this.excludeReadRow) {
            this.newFilterFlags &= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ ^ (-1);
        } else if (include) {
            this.newAlwaysShow.remove(position - this.includeStartRow);
        } else {
            this.newNeverShow.remove(position - this.excludeStartRow);
        }
        fillFilterName();
        updateRows();
        checkDoneButton(true);
    }

    public void processDone() {
        saveFilterToServer(this.filter, this.newFilterFlags, this.newFilterName, this.newAlwaysShow, this.newNeverShow, this.newPinned, this.creatingNew, false, this.hasUserChanged, true, true, this, new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                FilterCreateActivity.this.m3431lambda$processDone$10$orgtelegramuiFilterCreateActivity();
            }
        });
    }

    /* renamed from: lambda$processDone$10$org-telegram-ui-FilterCreateActivity */
    public /* synthetic */ void m3431lambda$processDone$10$orgtelegramuiFilterCreateActivity() {
        getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        finishFragment();
    }

    private static void processAddFilter(MessagesController.DialogFilter filter, int newFilterFlags, String newFilterName, ArrayList<Long> newAlwaysShow, ArrayList<Long> newNeverShow, boolean creatingNew, boolean atBegin, boolean hasUserChanged, boolean resetUnreadCounter, BaseFragment fragment, Runnable onFinish) {
        if (filter.flags != newFilterFlags || hasUserChanged) {
            filter.pendingUnreadCount = -1;
            if (resetUnreadCounter) {
                filter.unreadCount = -1;
            }
        }
        filter.flags = newFilterFlags;
        filter.name = newFilterName;
        filter.neverShow = newNeverShow;
        filter.alwaysShow = newAlwaysShow;
        if (creatingNew) {
            fragment.getMessagesController().addFilter(filter, atBegin);
        } else {
            fragment.getMessagesController().onFilterUpdate(filter);
        }
        fragment.getMessagesStorage().saveDialogFilter(filter, atBegin, true);
        if (onFinish != null) {
            onFinish.run();
        }
    }

    public static void saveFilterToServer(final MessagesController.DialogFilter filter, final int newFilterFlags, final String newFilterName, final ArrayList<Long> newAlwaysShow, final ArrayList<Long> newNeverShow, final LongSparseIntArray newPinned, final boolean creatingNew, final boolean atBegin, final boolean hasUserChanged, final boolean resetUnreadCounter, final boolean progress, final BaseFragment fragment, final Runnable onFinish) {
        AlertDialog progressDialog;
        ArrayList<TLRPC.InputPeer> toArray;
        ArrayList<Long> fromArray;
        ArrayList<Long> pinArray;
        ArrayList<Long> fromArray2;
        if (fragment == null || fragment.getParentActivity() == null) {
            return;
        }
        int i = 3;
        boolean z = false;
        if (progress) {
            AlertDialog progressDialog2 = new AlertDialog(fragment.getParentActivity(), 3);
            progressDialog2.setCanCancel(false);
            progressDialog2.show();
            progressDialog = progressDialog2;
        } else {
            progressDialog = null;
        }
        TLRPC.TL_messages_updateDialogFilter req = new TLRPC.TL_messages_updateDialogFilter();
        req.id = filter.id;
        int i2 = 1;
        req.flags |= 1;
        req.filter = new TLRPC.TL_dialogFilter();
        req.filter.contacts = (newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0;
        req.filter.non_contacts = (newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0;
        req.filter.groups = (newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0;
        req.filter.broadcasts = (newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0;
        req.filter.bots = (newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0;
        req.filter.exclude_muted = (newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0;
        req.filter.exclude_read = (newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0;
        TLRPC.TL_dialogFilter tL_dialogFilter = req.filter;
        if ((newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0) {
            z = true;
        }
        tL_dialogFilter.exclude_archived = z;
        req.filter.id = filter.id;
        req.filter.title = newFilterName;
        MessagesController messagesController = fragment.getMessagesController();
        ArrayList<Long> pinArray2 = new ArrayList<>();
        if (newPinned.size() != 0) {
            int N = newPinned.size();
            for (int a = 0; a < N; a++) {
                long key = newPinned.keyAt(a);
                if (!DialogObject.isEncryptedDialog(key)) {
                    pinArray2.add(Long.valueOf(key));
                }
            }
            Collections.sort(pinArray2, new Comparator() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda13
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return FilterCreateActivity.lambda$saveFilterToServer$11(LongSparseIntArray.this, (Long) obj, (Long) obj2);
                }
            });
        }
        int b = 0;
        while (b < i) {
            if (b == 0) {
                fromArray = newAlwaysShow;
                toArray = req.filter.include_peers;
            } else if (b == i2) {
                fromArray = newNeverShow;
                toArray = req.filter.exclude_peers;
            } else {
                fromArray = pinArray2;
                toArray = req.filter.pinned_peers;
            }
            int a2 = 0;
            int N2 = fromArray.size();
            while (a2 < N2) {
                long did = fromArray.get(a2).longValue();
                if (b == 0 && newPinned.indexOfKey(did) >= 0) {
                    fromArray2 = fromArray;
                    pinArray = pinArray2;
                } else if (DialogObject.isEncryptedDialog(did)) {
                    fromArray2 = fromArray;
                    pinArray = pinArray2;
                } else if (did > 0) {
                    TLRPC.User user = messagesController.getUser(Long.valueOf(did));
                    if (user != null) {
                        TLRPC.InputPeer inputPeer = new TLRPC.TL_inputPeerUser();
                        inputPeer.user_id = did;
                        inputPeer.access_hash = user.access_hash;
                        toArray = toArray;
                        toArray.add(inputPeer);
                    }
                    fromArray2 = fromArray;
                    pinArray = pinArray2;
                } else {
                    fromArray2 = fromArray;
                    TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-did));
                    if (chat == null) {
                        pinArray = pinArray2;
                    } else if (ChatObject.isChannel(chat)) {
                        TLRPC.InputPeer inputPeer2 = new TLRPC.TL_inputPeerChannel();
                        pinArray = pinArray2;
                        inputPeer2.channel_id = -did;
                        inputPeer2.access_hash = chat.access_hash;
                        toArray.add(inputPeer2);
                    } else {
                        pinArray = pinArray2;
                        TLRPC.InputPeer inputPeer3 = new TLRPC.TL_inputPeerChat();
                        inputPeer3.chat_id = -did;
                        toArray.add(inputPeer3);
                    }
                }
                a2++;
                fromArray = fromArray2;
                pinArray2 = pinArray;
            }
            b++;
            i = 3;
            i2 = 1;
        }
        final AlertDialog alertDialog = progressDialog;
        fragment.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda12
                    @Override // java.lang.Runnable
                    public final void run() {
                        FilterCreateActivity.lambda$saveFilterToServer$12(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
                    }
                });
            }
        });
        if (!progress) {
            processAddFilter(filter, newFilterFlags, newFilterName, newAlwaysShow, newNeverShow, creatingNew, atBegin, hasUserChanged, resetUnreadCounter, fragment, onFinish);
        }
    }

    public static /* synthetic */ int lambda$saveFilterToServer$11(LongSparseIntArray newPinned, Long o1, Long o2) {
        int idx1 = newPinned.get(o1.longValue());
        int idx2 = newPinned.get(o2.longValue());
        if (idx1 > idx2) {
            return 1;
        }
        if (idx1 < idx2) {
            return -1;
        }
        return 0;
    }

    public static /* synthetic */ void lambda$saveFilterToServer$12(boolean progress, AlertDialog progressDialog, MessagesController.DialogFilter filter, int newFilterFlags, String newFilterName, ArrayList newAlwaysShow, ArrayList newNeverShow, boolean creatingNew, boolean atBegin, boolean hasUserChanged, boolean resetUnreadCounter, BaseFragment fragment, Runnable onFinish) {
        if (progress) {
            if (progressDialog != null) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            processAddFilter(filter, newFilterFlags, newFilterName, newAlwaysShow, newNeverShow, creatingNew, atBegin, hasUserChanged, resetUnreadCounter, fragment, onFinish);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean canBeginSlide() {
        return checkDiscard();
    }

    private boolean hasChanges() {
        this.hasUserChanged = false;
        if (this.filter.alwaysShow.size() != this.newAlwaysShow.size()) {
            this.hasUserChanged = true;
        }
        if (this.filter.neverShow.size() != this.newNeverShow.size()) {
            this.hasUserChanged = true;
        }
        if (!this.hasUserChanged) {
            Collections.sort(this.filter.alwaysShow);
            Collections.sort(this.newAlwaysShow);
            if (!this.filter.alwaysShow.equals(this.newAlwaysShow)) {
                this.hasUserChanged = true;
            }
            Collections.sort(this.filter.neverShow);
            Collections.sort(this.newNeverShow);
            if (!this.filter.neverShow.equals(this.newNeverShow)) {
                this.hasUserChanged = true;
            }
        }
        if (TextUtils.equals(this.filter.name, this.newFilterName) && this.filter.flags == this.newFilterFlags) {
            return this.hasUserChanged;
        }
        return true;
    }

    public void checkDoneButton(boolean animated) {
        boolean z = true;
        boolean enabled = !TextUtils.isEmpty(this.newFilterName) && this.newFilterName.length() <= 12;
        if (enabled) {
            if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == 0 && this.newAlwaysShow.isEmpty()) {
                z = false;
            }
            enabled = z;
            if (enabled && !this.creatingNew) {
                enabled = hasChanges();
            }
        }
        if (this.doneItem.isEnabled() == enabled) {
            return;
        }
        this.doneItem.setEnabled(enabled);
        float f = 1.0f;
        if (animated) {
            ViewPropertyAnimator scaleX = this.doneItem.animate().alpha(enabled ? 1.0f : 0.0f).scaleX(enabled ? 1.0f : 0.0f);
            if (!enabled) {
                f = 0.0f;
            }
            scaleX.scaleY(f).setDuration(180L).start();
            return;
        }
        this.doneItem.setAlpha(enabled ? 1.0f : 0.0f);
        this.doneItem.setScaleX(enabled ? 1.0f : 0.0f);
        ActionBarMenuItem actionBarMenuItem = this.doneItem;
        if (!enabled) {
            f = 0.0f;
        }
        actionBarMenuItem.setScaleY(f);
    }

    public void setTextLeft(View cell) {
        if (cell instanceof PollEditTextCell) {
            PollEditTextCell textCell = (PollEditTextCell) cell;
            String str = this.newFilterName;
            int left = 12 - (str != null ? str.length() : 0);
            if (left <= 3.6000004f) {
                textCell.setText2(String.format("%d", Integer.valueOf(left)));
                SimpleTextView textView = textCell.getTextView2();
                String key = left < 0 ? Theme.key_windowBackgroundWhiteRedText5 : Theme.key_windowBackgroundWhiteGrayText3;
                textView.setTextColor(Theme.getColor(key));
                textView.setTag(key);
                textView.setAlpha((((PollEditTextCell) cell).getTextView().isFocused() || left < 0) ? 1.0f : 0.0f);
                return;
            }
            textCell.setText2("");
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            FilterCreateActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return (type == 3 || type == 0 || type == 2 || type == 5) ? false : true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return FilterCreateActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    UserCell cell = new UserCell(this.mContext, 6, 0, false);
                    cell.setSelfAsSavedMessages(true);
                    cell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = cell;
                    break;
                case 2:
                    final PollEditTextCell cell2 = new PollEditTextCell(this.mContext, null);
                    cell2.createErrorTextView();
                    cell2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    cell2.addTextWatcher(new TextWatcher() { // from class: org.telegram.ui.FilterCreateActivity.ListAdapter.1
                        @Override // android.text.TextWatcher
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override // android.text.TextWatcher
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override // android.text.TextWatcher
                        public void afterTextChanged(Editable s) {
                            if (cell2.getTag() != null) {
                                return;
                            }
                            String newName = s.toString();
                            if (!TextUtils.equals(newName, FilterCreateActivity.this.newFilterName)) {
                                FilterCreateActivity.this.nameChangedManually = !TextUtils.isEmpty(newName);
                                FilterCreateActivity.this.newFilterName = newName;
                            }
                            RecyclerView.ViewHolder holder = FilterCreateActivity.this.listView.findViewHolderForAdapterPosition(FilterCreateActivity.this.nameRow);
                            if (holder != null) {
                                FilterCreateActivity.this.setTextLeft(holder.itemView);
                            }
                            FilterCreateActivity.this.checkDoneButton(true);
                        }
                    });
                    EditTextBoldCursor editText = cell2.getTextView();
                    cell2.setShowNextButton(true);
                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.FilterCreateActivity$ListAdapter$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnFocusChangeListener
                        public final void onFocusChange(View view2, boolean z) {
                            FilterCreateActivity.ListAdapter.this.m3434x999fa0b5(cell2, view2, z);
                        }
                    });
                    editText.setImeOptions(268435462);
                    view = cell2;
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    view = new TextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    view = new HintInnerCell(this.mContext);
                    break;
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-FilterCreateActivity$ListAdapter */
        public /* synthetic */ void m3434x999fa0b5(PollEditTextCell cell, View v, boolean hasFocus) {
            cell.getTextView2().setAlpha((hasFocus || FilterCreateActivity.this.newFilterName.length() > 12) ? 1.0f : 0.0f);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            if (viewType == 2) {
                FilterCreateActivity.this.setTextLeft(holder.itemView);
                PollEditTextCell textCell = (PollEditTextCell) holder.itemView;
                textCell.setTag(1);
                textCell.setTextAndHint(FilterCreateActivity.this.newFilterName != null ? FilterCreateActivity.this.newFilterName : "", LocaleController.getString("FilterNameHint", R.string.FilterNameHint), false);
                textCell.setTag(null);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 2) {
                PollEditTextCell editTextCell = (PollEditTextCell) holder.itemView;
                EditTextBoldCursor editText = editTextCell.getTextView();
                if (editText.isFocused()) {
                    editText.clearFocus();
                    AndroidUtilities.hideKeyboard(editText);
                }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean divider;
            Long id;
            String status;
            String status2;
            boolean divider2;
            String str;
            String name;
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position != FilterCreateActivity.this.includeHeaderRow) {
                        if (position == FilterCreateActivity.this.excludeHeaderRow) {
                            headerCell.setText(LocaleController.getString("FilterExclude", R.string.FilterExclude));
                            return;
                        }
                        return;
                    }
                    headerCell.setText(LocaleController.getString("FilterInclude", R.string.FilterInclude));
                    return;
                case 1:
                    UserCell userCell = (UserCell) holder.itemView;
                    if (position < FilterCreateActivity.this.includeStartRow || position >= FilterCreateActivity.this.includeEndRow) {
                        if (position < FilterCreateActivity.this.excludeStartRow || position >= FilterCreateActivity.this.excludeEndRow) {
                            if (position != FilterCreateActivity.this.includeContactsRow) {
                                if (position != FilterCreateActivity.this.includeNonContactsRow) {
                                    if (position != FilterCreateActivity.this.includeGroupsRow) {
                                        if (position != FilterCreateActivity.this.includeChannelsRow) {
                                            if (position != FilterCreateActivity.this.includeBotsRow) {
                                                if (position != FilterCreateActivity.this.excludeMutedRow) {
                                                    if (position == FilterCreateActivity.this.excludeReadRow) {
                                                        name = LocaleController.getString("FilterRead", R.string.FilterRead);
                                                        str = "read";
                                                        if (position + 1 != FilterCreateActivity.this.excludeSectionRow) {
                                                            z = true;
                                                        }
                                                        divider2 = z;
                                                    } else {
                                                        name = LocaleController.getString("FilterArchived", R.string.FilterArchived);
                                                        str = "archived";
                                                        if (position + 1 != FilterCreateActivity.this.excludeSectionRow) {
                                                            z = true;
                                                        }
                                                        divider2 = z;
                                                    }
                                                } else {
                                                    name = LocaleController.getString("FilterMuted", R.string.FilterMuted);
                                                    str = "muted";
                                                    if (position + 1 != FilterCreateActivity.this.excludeSectionRow) {
                                                        z = true;
                                                    }
                                                    divider2 = z;
                                                }
                                            } else {
                                                name = LocaleController.getString("FilterBots", R.string.FilterBots);
                                                str = "bots";
                                                if (position + 1 != FilterCreateActivity.this.includeSectionRow) {
                                                    z = true;
                                                }
                                                divider2 = z;
                                            }
                                        } else {
                                            name = LocaleController.getString("FilterChannels", R.string.FilterChannels);
                                            str = "channels";
                                            if (position + 1 != FilterCreateActivity.this.includeSectionRow) {
                                                z = true;
                                            }
                                            divider2 = z;
                                        }
                                    } else {
                                        name = LocaleController.getString("FilterGroups", R.string.FilterGroups);
                                        str = "groups";
                                        if (position + 1 != FilterCreateActivity.this.includeSectionRow) {
                                            z = true;
                                        }
                                        divider2 = z;
                                    }
                                } else {
                                    name = LocaleController.getString("FilterNonContacts", R.string.FilterNonContacts);
                                    str = "non_contacts";
                                    if (position + 1 != FilterCreateActivity.this.includeSectionRow) {
                                        z = true;
                                    }
                                    divider2 = z;
                                }
                            } else {
                                name = LocaleController.getString("FilterContacts", R.string.FilterContacts);
                                str = "contacts";
                                if (position + 1 != FilterCreateActivity.this.includeSectionRow) {
                                    z = true;
                                }
                                divider2 = z;
                            }
                            userCell.setData(str, name, null, 0, divider2);
                            return;
                        }
                        id = (Long) FilterCreateActivity.this.newNeverShow.get(position - FilterCreateActivity.this.excludeStartRow);
                        if (FilterCreateActivity.this.excludeShowMoreRow == -1 && position == FilterCreateActivity.this.excludeEndRow - 1) {
                            z2 = false;
                        }
                        divider = z2;
                    } else {
                        id = (Long) FilterCreateActivity.this.newAlwaysShow.get(position - FilterCreateActivity.this.includeStartRow);
                        if (FilterCreateActivity.this.includeShowMoreRow == -1 && position == FilterCreateActivity.this.includeEndRow - 1) {
                            z2 = false;
                        }
                        divider = z2;
                    }
                    if (id.longValue() > 0) {
                        TLRPC.User user = FilterCreateActivity.this.getMessagesController().getUser(id);
                        if (user != null) {
                            if (user.bot) {
                                status2 = LocaleController.getString("Bot", R.string.Bot);
                            } else if (user.contact) {
                                status2 = LocaleController.getString("FilterContact", R.string.FilterContact);
                            } else {
                                status2 = LocaleController.getString("FilterNonContact", R.string.FilterNonContact);
                            }
                            userCell.setData(user, null, status2, 0, divider);
                            return;
                        }
                        return;
                    }
                    TLRPC.Chat chat = FilterCreateActivity.this.getMessagesController().getChat(Long.valueOf(-id.longValue()));
                    if (chat != null) {
                        if (chat.participants_count != 0) {
                            status = LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]);
                        } else {
                            String status3 = chat.username;
                            if (TextUtils.isEmpty(status3)) {
                                if (ChatObject.isChannel(chat) && !chat.megagroup) {
                                    status = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate);
                                } else {
                                    status = LocaleController.getString("MegaPrivate", R.string.MegaPrivate);
                                }
                            } else if (ChatObject.isChannel(chat) && !chat.megagroup) {
                                status = LocaleController.getString("ChannelPublic", R.string.ChannelPublic);
                            } else {
                                status = LocaleController.getString("MegaPublic", R.string.MegaPublic);
                            }
                        }
                        userCell.setData(chat, null, status, 0, divider);
                        return;
                    }
                    return;
                case 2:
                case 5:
                default:
                    return;
                case 3:
                    if (position == FilterCreateActivity.this.removeSectionRow) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 4:
                    TextCell textCell = (TextCell) holder.itemView;
                    if (position != FilterCreateActivity.this.removeRow) {
                        if (position != FilterCreateActivity.this.includeShowMoreRow) {
                            if (position != FilterCreateActivity.this.excludeShowMoreRow) {
                                if (position != FilterCreateActivity.this.includeAddRow) {
                                    if (position == FilterCreateActivity.this.excludeAddRow) {
                                        textCell.setColors(Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhiteBlueText4);
                                        String string = LocaleController.getString("FilterRemoveChats", R.string.FilterRemoveChats);
                                        if (position + 1 != FilterCreateActivity.this.excludeSectionRow) {
                                            z = true;
                                        }
                                        textCell.setTextAndIcon(string, R.drawable.msg_chats_add, z);
                                        return;
                                    }
                                    return;
                                }
                                textCell.setColors(Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhiteBlueText4);
                                String string2 = LocaleController.getString("FilterAddChats", R.string.FilterAddChats);
                                if (position + 1 != FilterCreateActivity.this.includeSectionRow) {
                                    z = true;
                                }
                                textCell.setTextAndIcon(string2, R.drawable.msg_chats_add, z);
                                return;
                            }
                            textCell.setColors(Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhiteBlueText4);
                            textCell.setTextAndIcon(LocaleController.formatPluralString("FilterShowMoreChats", FilterCreateActivity.this.newNeverShow.size() - 5, new Object[0]), R.drawable.arrow_more, false);
                            return;
                        }
                        textCell.setColors(Theme.key_switchTrackChecked, Theme.key_windowBackgroundWhiteBlueText4);
                        textCell.setTextAndIcon(LocaleController.formatPluralString("FilterShowMoreChats", FilterCreateActivity.this.newAlwaysShow.size() - 5, new Object[0]), R.drawable.arrow_more, false);
                        return;
                    }
                    textCell.setColors(null, Theme.key_windowBackgroundWhiteRedText5);
                    textCell.setText(LocaleController.getString("FilterDelete", R.string.FilterDelete), false);
                    return;
                case 6:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position != FilterCreateActivity.this.includeSectionRow) {
                        if (position == FilterCreateActivity.this.excludeSectionRow) {
                            cell.setText(LocaleController.getString("FilterExcludeInfo", R.string.FilterExcludeInfo));
                        }
                    } else {
                        cell.setText(LocaleController.getString("FilterIncludeInfo", R.string.FilterIncludeInfo));
                    }
                    if (position == FilterCreateActivity.this.excludeSectionRow && FilterCreateActivity.this.removeSectionRow == -1) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != FilterCreateActivity.this.includeHeaderRow && position != FilterCreateActivity.this.excludeHeaderRow) {
                if (position < FilterCreateActivity.this.includeStartRow || position >= FilterCreateActivity.this.includeEndRow) {
                    if ((position < FilterCreateActivity.this.excludeStartRow || position >= FilterCreateActivity.this.excludeEndRow) && position != FilterCreateActivity.this.includeContactsRow && position != FilterCreateActivity.this.includeNonContactsRow && position != FilterCreateActivity.this.includeGroupsRow && position != FilterCreateActivity.this.includeChannelsRow && position != FilterCreateActivity.this.includeBotsRow && position != FilterCreateActivity.this.excludeReadRow && position != FilterCreateActivity.this.excludeArchivedRow && position != FilterCreateActivity.this.excludeMutedRow) {
                        if (position != FilterCreateActivity.this.nameRow) {
                            if (position != FilterCreateActivity.this.nameSectionRow && position != FilterCreateActivity.this.namePreSectionRow && position != FilterCreateActivity.this.removeSectionRow) {
                                if (position != FilterCreateActivity.this.imageRow) {
                                    if (position == FilterCreateActivity.this.includeSectionRow || position == FilterCreateActivity.this.excludeSectionRow) {
                                        return 6;
                                    }
                                    return 4;
                                }
                                return 5;
                            }
                            return 3;
                        }
                        return 2;
                    }
                    return 1;
                }
                return 1;
            }
            return 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.FilterCreateActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                FilterCreateActivity.this.m3430x50dd8b1d();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, PollEditTextCell.class, UserCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText5));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"ImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_creatorIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDelegate, Theme.key_windowBackgroundWhiteBlueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, themeDelegate, Theme.key_avatar_backgroundPink));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$14$org-telegram-ui-FilterCreateActivity */
    public /* synthetic */ void m3430x50dd8b1d() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
    }
}
