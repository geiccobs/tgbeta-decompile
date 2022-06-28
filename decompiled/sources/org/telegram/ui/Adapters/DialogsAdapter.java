package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Cells.ArchiveHintCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogMeUrlCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;
/* loaded from: classes4.dex */
public class DialogsAdapter extends RecyclerListView.SelectionAdapter {
    public static final int VIEW_TYPE_ARCHIVE = 9;
    public static final int VIEW_TYPE_CONTACTS_FLICKER = 13;
    public static final int VIEW_TYPE_DIALOG = 0;
    public static final int VIEW_TYPE_DIVIDER = 3;
    public static final int VIEW_TYPE_EMPTY = 5;
    public static final int VIEW_TYPE_FLICKER = 1;
    public static final int VIEW_TYPE_HEADER = 7;
    public static final int VIEW_TYPE_HEADER_2 = 14;
    public static final int VIEW_TYPE_LAST_EMPTY = 10;
    public static final int VIEW_TYPE_ME_URL = 4;
    public static final int VIEW_TYPE_NEW_CHAT_HINT = 11;
    public static final int VIEW_TYPE_RECENTLY_VIEWED = 2;
    public static final int VIEW_TYPE_SHADOW = 8;
    public static final int VIEW_TYPE_TEXT = 12;
    public static final int VIEW_TYPE_USER = 6;
    private ArchiveHintCell archiveHintCell;
    private Drawable arrowDrawable;
    private int currentAccount;
    private int currentCount;
    private int dialogsCount;
    private boolean dialogsListFrozen;
    private int dialogsType;
    private int folderId;
    private boolean forceShowEmptyCell;
    private boolean forceUpdatingContacts;
    private boolean hasHints;
    private boolean isOnlySelect;
    private boolean isReordering;
    public int lastDialogsEmptyType = -1;
    private long lastSortTime;
    private Context mContext;
    private ArrayList<TLRPC.TL_contact> onlineContacts;
    private long openedDialogId;
    private DialogsActivity parentFragment;
    private DialogsPreloader preloader;
    private int prevContactsCount;
    private int prevDialogsCount;
    private PullForegroundDrawable pullForegroundDrawable;
    private ArrayList<Long> selectedDialogs;
    private boolean showArchiveHint;

    public DialogsAdapter(DialogsActivity fragment, Context context, int type, int folder, boolean onlySelect, ArrayList<Long> selected, int account) {
        this.mContext = context;
        this.parentFragment = fragment;
        this.dialogsType = type;
        this.folderId = folder;
        this.isOnlySelect = onlySelect;
        this.hasHints = folder == 0 && type == 0 && !onlySelect;
        this.selectedDialogs = selected;
        this.currentAccount = account;
        if (folder == 1) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            this.showArchiveHint = preferences.getBoolean("archivehint", true);
            preferences.edit().putBoolean("archivehint", false).commit();
        }
        if (folder == 0) {
            this.preloader = new DialogsPreloader();
        }
    }

    public void setOpenedDialogId(long id) {
        this.openedDialogId = id;
    }

    public void onReorderStateChanged(boolean reordering) {
        this.isReordering = reordering;
    }

    public int fixPosition(int position) {
        if (this.hasHints) {
            position -= MessagesController.getInstance(this.currentAccount).hintDialogs.size() + 2;
        }
        if (this.showArchiveHint) {
            return position - 2;
        }
        int i = this.dialogsType;
        if (i == 11 || i == 13) {
            return position - 2;
        }
        if (i == 12) {
            return position - 1;
        }
        return position;
    }

    public boolean isDataSetChanged() {
        int current = this.currentCount;
        return current != getItemCount() || current == 1;
    }

    public void setDialogsType(int type) {
        this.dialogsType = type;
        notifyDataSetChanged();
    }

    public int getDialogsType() {
        return this.dialogsType;
    }

    public int getDialogsCount() {
        return this.dialogsCount;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        int i;
        int i2;
        int i3;
        int i4;
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        ArrayList<TLRPC.Dialog> array = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen);
        int size = array.size();
        this.dialogsCount = size;
        boolean z = true;
        if (!this.forceUpdatingContacts && !this.forceShowEmptyCell && (i3 = this.dialogsType) != 7 && i3 != 8 && i3 != 11 && size == 0 && ((i4 = this.folderId) != 0 || messagesController.isLoadingDialogs(i4) || !MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId))) {
            this.onlineContacts = null;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("DialogsAdapter dialogsCount=" + this.dialogsCount + " dialogsType=" + this.dialogsType + " isLoadingDialogs=" + messagesController.isLoadingDialogs(this.folderId) + " isDialogsEndReached=" + MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId));
            }
            if (this.folderId == 1 && this.showArchiveHint) {
                this.currentCount = 2;
                return 2;
            }
            this.currentCount = 0;
            return 0;
        } else if (this.dialogsCount == 0 && messagesController.isLoadingDialogs(this.folderId)) {
            this.currentCount = 0;
            return 0;
        } else {
            int count = this.dialogsCount;
            int i5 = this.dialogsType;
            if (i5 == 7 || i5 == 8) {
                if (this.dialogsCount == 0) {
                    count++;
                }
            } else if (!messagesController.isDialogsEndReached(this.folderId) || this.dialogsCount == 0) {
                count++;
            }
            boolean hasContacts = false;
            if (this.hasHints) {
                count += messagesController.hintDialogs.size() + 2;
            } else if (this.dialogsType == 0 && (i2 = this.folderId) == 0 && messagesController.isDialogsEndReached(i2)) {
                if (ContactsController.getInstance(this.currentAccount).contacts.isEmpty() && !ContactsController.getInstance(this.currentAccount).doneLoadingContacts && !this.forceUpdatingContacts) {
                    this.onlineContacts = null;
                    if (BuildVars.LOGS_ENABLED) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("DialogsAdapter loadingContacts=");
                        if (!ContactsController.getInstance(this.currentAccount).contacts.isEmpty() || ContactsController.getInstance(this.currentAccount).doneLoadingContacts) {
                            z = false;
                        }
                        sb.append(z);
                        sb.append("dialogsCount=");
                        sb.append(this.dialogsCount);
                        sb.append(" dialogsType=");
                        sb.append(this.dialogsType);
                        FileLog.d(sb.toString());
                    }
                    this.currentCount = 0;
                    return 0;
                } else if (messagesController.getAllFoldersDialogsCount() <= 10 && ContactsController.getInstance(this.currentAccount).doneLoadingContacts && !ContactsController.getInstance(this.currentAccount).contacts.isEmpty()) {
                    if (this.onlineContacts == null || this.prevDialogsCount != this.dialogsCount || this.prevContactsCount != ContactsController.getInstance(this.currentAccount).contacts.size()) {
                        ArrayList<TLRPC.TL_contact> arrayList = new ArrayList<>(ContactsController.getInstance(this.currentAccount).contacts);
                        this.onlineContacts = arrayList;
                        this.prevContactsCount = arrayList.size();
                        this.prevDialogsCount = messagesController.dialogs_dict.size();
                        long selfId = UserConfig.getInstance(this.currentAccount).clientUserId;
                        int a = 0;
                        int N = this.onlineContacts.size();
                        while (a < N) {
                            long userId = this.onlineContacts.get(a).user_id;
                            if (userId == selfId || messagesController.dialogs_dict.get(userId) != null) {
                                this.onlineContacts.remove(a);
                                a--;
                                N--;
                            }
                            a++;
                        }
                        if (this.onlineContacts.isEmpty()) {
                            this.onlineContacts = null;
                        }
                        sortOnlineContacts(false);
                        if (this.parentFragment.getContactsAlpha() == 0.0f) {
                            registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() { // from class: org.telegram.ui.Adapters.DialogsAdapter.1
                                @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                                public void onChanged() {
                                    DialogsAdapter.this.parentFragment.setContactsAlpha(0.0f);
                                    DialogsAdapter.this.parentFragment.animateContactsAlpha(1.0f);
                                    DialogsAdapter.this.unregisterAdapterDataObserver(this);
                                }
                            });
                        }
                    }
                    ArrayList<TLRPC.TL_contact> arrayList2 = this.onlineContacts;
                    if (arrayList2 != null) {
                        count += arrayList2.size() + 2;
                        hasContacts = true;
                    }
                }
            }
            int i6 = this.folderId;
            if (i6 == 0 && !hasContacts && this.dialogsCount == 0 && this.forceUpdatingContacts) {
                count += 3;
            }
            if (i6 == 0 && this.onlineContacts != null && !hasContacts) {
                this.onlineContacts = null;
            }
            if (i6 == 1 && this.showArchiveHint) {
                count += 2;
            }
            if (i6 == 0 && (i = this.dialogsCount) != 0) {
                count++;
                if (i > 10 && this.dialogsType == 0) {
                    count++;
                }
            }
            int i7 = this.dialogsType;
            if (i7 == 11 || i7 == 13) {
                count += 2;
            } else if (i7 == 12) {
                count++;
            }
            this.currentCount = count;
            return count;
        }
    }

    public TLObject getItem(int i) {
        int i2;
        int i3;
        ArrayList<TLRPC.TL_contact> arrayList = this.onlineContacts;
        if (arrayList != null && ((i2 = this.dialogsCount) == 0 || i >= i2)) {
            if (i2 == 0) {
                i3 = i - 3;
            } else {
                i3 = i - (i2 + 2);
            }
            if (i3 >= 0 && i3 < arrayList.size()) {
                return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.onlineContacts.get(i3).user_id));
            }
            return null;
        }
        if (this.showArchiveHint) {
            i -= 2;
        } else {
            int i4 = this.dialogsType;
            if (i4 == 11 || i4 == 13) {
                i -= 2;
            } else if (i4 == 12) {
                i--;
            }
        }
        ArrayList<TLRPC.Dialog> arrayList2 = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, this.dialogsListFrozen);
        if (this.hasHints) {
            int count = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
            if (i < count + 2) {
                return MessagesController.getInstance(this.currentAccount).hintDialogs.get(i - 1);
            }
            i -= count + 2;
        }
        if (i >= 0 && i < arrayList2.size()) {
            return arrayList2.get(i);
        }
        return null;
    }

    public void sortOnlineContacts(boolean notify) {
        if (this.onlineContacts != null) {
            if (notify && SystemClock.elapsedRealtime() - this.lastSortTime < AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS) {
                return;
            }
            this.lastSortTime = SystemClock.elapsedRealtime();
            try {
                final int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
                Collections.sort(this.onlineContacts, new Comparator() { // from class: org.telegram.ui.Adapters.DialogsAdapter$$ExternalSyntheticLambda3
                    @Override // java.util.Comparator
                    public final int compare(Object obj, Object obj2) {
                        return DialogsAdapter.lambda$sortOnlineContacts$0(MessagesController.this, currentTime, (TLRPC.TL_contact) obj, (TLRPC.TL_contact) obj2);
                    }
                });
                if (notify) {
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static /* synthetic */ int lambda$sortOnlineContacts$0(MessagesController messagesController, int currentTime, TLRPC.TL_contact o1, TLRPC.TL_contact o2) {
        TLRPC.User user1 = messagesController.getUser(Long.valueOf(o2.user_id));
        TLRPC.User user2 = messagesController.getUser(Long.valueOf(o1.user_id));
        int status1 = 0;
        int status2 = 0;
        if (user1 != null) {
            if (user1.self) {
                status1 = currentTime + 50000;
            } else if (user1.status != null) {
                status1 = user1.status.expires;
            }
        }
        if (user2 != null) {
            if (user2.self) {
                status2 = currentTime + 50000;
            } else if (user2.status != null) {
                status2 = user2.status.expires;
            }
        }
        if (status1 > 0 && status2 > 0) {
            if (status1 > status2) {
                return 1;
            }
            return status1 < status2 ? -1 : 0;
        } else if (status1 < 0 && status2 < 0) {
            if (status1 > status2) {
                return 1;
            }
            return status1 < status2 ? -1 : 0;
        } else if ((status1 < 0 && status2 > 0) || (status1 == 0 && status2 != 0)) {
            return -1;
        } else {
            return (status2 < 0 || status1 != 0) ? 1 : 0;
        }
    }

    public void setDialogsListFrozen(boolean frozen) {
        this.dialogsListFrozen = frozen;
    }

    public ViewPager getArchiveHintCellPager() {
        ArchiveHintCell archiveHintCell = this.archiveHintCell;
        if (archiveHintCell != null) {
            return archiveHintCell.getViewPager();
        }
        return null;
    }

    public void updateHasHints() {
        this.hasHints = this.folderId == 0 && this.dialogsType == 0 && !this.isOnlySelect && !MessagesController.getInstance(this.currentAccount).hintDialogs.isEmpty();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void notifyDataSetChanged() {
        updateHasHints();
        super.notifyDataSetChanged();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder.itemView instanceof DialogCell) {
            DialogCell dialogCell = (DialogCell) holder.itemView;
            dialogCell.onReorderStateChanged(this.isReordering, false);
            int position = fixPosition(holder.getAdapterPosition());
            dialogCell.setDialogIndex(position);
            dialogCell.checkCurrentDialogIndex(this.dialogsListFrozen);
            dialogCell.setChecked(this.selectedDialogs.contains(Long.valueOf(dialogCell.getDialogId())), false);
        }
    }

    @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
    public boolean isEnabled(RecyclerView.ViewHolder holder) {
        int viewType = holder.getItemViewType();
        return (viewType == 1 || viewType == 5 || viewType == 3 || viewType == 8 || viewType == 7 || viewType == 9 || viewType == 10 || viewType == 11 || viewType == 13) ? false : true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
            case 0:
                if (this.dialogsType == 2) {
                    view = new ProfileSearchCell(this.mContext);
                    break;
                } else {
                    DialogCell dialogCell = new DialogCell(this.parentFragment, this.mContext, true, false, this.currentAccount, null);
                    dialogCell.setArchivedPullAnimation(this.pullForegroundDrawable);
                    dialogCell.setPreloader(this.preloader);
                    view = dialogCell;
                    break;
                }
            case 1:
            case 13:
                FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                flickerLoadingView.setIsSingleCell(true);
                int flickerType = viewType == 13 ? 18 : 7;
                flickerLoadingView.setViewType(flickerType);
                if (flickerType == 18) {
                    flickerLoadingView.setIgnoreHeightCheck(true);
                }
                if (viewType == 13) {
                    flickerLoadingView.setItemsCount((int) ((AndroidUtilities.displaySize.y * 0.5f) / AndroidUtilities.dp(64.0f)));
                }
                view = flickerLoadingView;
                break;
            case 2:
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setText(LocaleController.getString("RecentlyViewed", R.string.RecentlyViewed));
                TextView textView = new TextView(this.mContext);
                textView.setTextSize(1, 15.0f);
                textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader));
                textView.setText(LocaleController.getString("RecentlyViewedHide", R.string.RecentlyViewedHide));
                int i = 3;
                textView.setGravity((LocaleController.isRTL ? 3 : 5) | 16);
                if (!LocaleController.isRTL) {
                    i = 5;
                }
                headerCell.addView(textView, LayoutHelper.createFrame(-1, -1.0f, i | 48, 17.0f, 15.0f, 17.0f, 0.0f));
                textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Adapters.DialogsAdapter$$ExternalSyntheticLambda0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        DialogsAdapter.this.m1452x3c8bd2a9(view2);
                    }
                });
                view = headerCell;
                break;
            case 3:
                FrameLayout frameLayout = new FrameLayout(this.mContext) { // from class: org.telegram.ui.Adapters.DialogsAdapter.2
                    @Override // android.widget.FrameLayout, android.view.View
                    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(12.0f), C.BUFFER_FLAG_ENCRYPTED));
                    }
                };
                frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                View v = new View(this.mContext);
                v.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                frameLayout.addView(v, LayoutHelper.createFrame(-1, -1.0f));
                view = frameLayout;
                break;
            case 4:
                view = new DialogMeUrlCell(this.mContext);
                break;
            case 5:
                view = new DialogsEmptyCell(this.mContext);
                break;
            case 6:
                view = new UserCell(this.mContext, 8, 0, false);
                break;
            case 7:
                view = new HeaderCell(this.mContext);
                view.setPadding(0, 0, 0, AndroidUtilities.dp(12.0f));
                break;
            case 8:
                View view2 = new ShadowSectionCell(this.mContext);
                Drawable drawable = Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable);
                combinedDrawable.setFullsize(true);
                view2.setBackgroundDrawable(combinedDrawable);
                view = view2;
                break;
            case 9:
                this.archiveHintCell = new ArchiveHintCell(this.mContext);
                view = this.archiveHintCell;
                break;
            case 10:
                view = new LastEmptyView(this.mContext);
                break;
            case 11:
                View view3 = new TextInfoPrivacyCell(this.mContext) { // from class: org.telegram.ui.Adapters.DialogsAdapter.3
                    private long lastUpdateTime;
                    private float moveProgress;
                    private int movement;
                    private int originalX;
                    private int originalY;

                    @Override // org.telegram.ui.Cells.TextInfoPrivacyCell
                    protected void afterTextDraw() {
                        if (DialogsAdapter.this.arrowDrawable != null) {
                            Rect bounds = DialogsAdapter.this.arrowDrawable.getBounds();
                            Drawable drawable2 = DialogsAdapter.this.arrowDrawable;
                            int i2 = this.originalX;
                            drawable2.setBounds(i2, this.originalY, bounds.width() + i2, this.originalY + bounds.height());
                        }
                    }

                    @Override // org.telegram.ui.Cells.TextInfoPrivacyCell
                    protected void onTextDraw() {
                        if (DialogsAdapter.this.arrowDrawable != null) {
                            Rect bounds = DialogsAdapter.this.arrowDrawable.getBounds();
                            int dx = (int) (this.moveProgress * AndroidUtilities.dp(3.0f));
                            this.originalX = bounds.left;
                            this.originalY = bounds.top;
                            DialogsAdapter.this.arrowDrawable.setBounds(this.originalX + dx, this.originalY + AndroidUtilities.dp(1.0f), this.originalX + dx + bounds.width(), this.originalY + AndroidUtilities.dp(1.0f) + bounds.height());
                            long newUpdateTime = SystemClock.elapsedRealtime();
                            long dt = newUpdateTime - this.lastUpdateTime;
                            if (dt > 17) {
                                dt = 17;
                            }
                            this.lastUpdateTime = newUpdateTime;
                            if (this.movement == 0) {
                                float f = this.moveProgress + (((float) dt) / 664.0f);
                                this.moveProgress = f;
                                if (f >= 1.0f) {
                                    this.movement = 1;
                                    this.moveProgress = 1.0f;
                                }
                            } else {
                                float f2 = this.moveProgress - (((float) dt) / 664.0f);
                                this.moveProgress = f2;
                                if (f2 <= 0.0f) {
                                    this.movement = 0;
                                    this.moveProgress = 0.0f;
                                }
                            }
                            getTextView().invalidate();
                        }
                    }
                };
                Drawable drawable2 = Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
                CombinedDrawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable2);
                combinedDrawable2.setFullsize(true);
                view3.setBackgroundDrawable(combinedDrawable2);
                view = view3;
                break;
            case 12:
            default:
                view = new TextCell(this.mContext);
                break;
            case 14:
                HeaderCell cell = new HeaderCell(this.mContext, Theme.key_graySectionText, 16, 0, false);
                cell.setHeight(32);
                view = cell;
                view.setClickable(false);
                break;
        }
        view.setLayoutParams(new RecyclerView.LayoutParams(-1, viewType == 5 ? -1 : -2));
        return new RecyclerListView.Holder(view);
    }

    /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-Adapters-DialogsAdapter */
    public /* synthetic */ void m1452x3c8bd2a9(View view1) {
        MessagesController.getInstance(this.currentAccount).hintDialogs.clear();
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        preferences.edit().remove("installReferer").commit();
        notifyDataSetChanged();
    }

    public int dialogsEmptyType() {
        int i = this.dialogsType;
        if (i != 7 && i != 8) {
            return this.onlineContacts != null ? 1 : 0;
        } else if (MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId)) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        TLRPC.Chat chat;
        CharSequence title;
        CharSequence subtitle;
        TLRPC.Chat chat2;
        int position;
        boolean z = false;
        switch (holder.getItemViewType()) {
            case 0:
                TLRPC.Dialog dialog = (TLRPC.Dialog) getItem(i);
                TLRPC.Dialog nextDialog = (TLRPC.Dialog) getItem(i + 1);
                if (this.dialogsType == 2) {
                    ProfileSearchCell cell = (ProfileSearchCell) holder.itemView;
                    long oldDialogId = cell.getDialogId();
                    if (dialog.id == 0) {
                        chat = null;
                    } else {
                        TLRPC.Chat chat3 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialog.id));
                        if (chat3 != null && chat3.migrated_to != null && (chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(chat3.migrated_to.channel_id))) != null) {
                            chat = chat2;
                        } else {
                            chat = chat3;
                        }
                    }
                    if (chat != null) {
                        CharSequence title2 = chat.title;
                        if (ChatObject.isChannel(chat) && !chat.megagroup) {
                            if (chat.participants_count != 0) {
                                subtitle = LocaleController.formatPluralStringComma("Subscribers", chat.participants_count);
                                title = title2;
                            } else {
                                CharSequence subtitle2 = chat.username;
                                if (TextUtils.isEmpty(subtitle2)) {
                                    subtitle = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                                    title = title2;
                                } else {
                                    subtitle = LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase();
                                    title = title2;
                                }
                            }
                        } else if (chat.participants_count != 0) {
                            subtitle = LocaleController.formatPluralStringComma("Members", chat.participants_count);
                            title = title2;
                        } else if (chat.has_geo) {
                            subtitle = LocaleController.getString("MegaLocation", R.string.MegaLocation);
                            title = title2;
                        } else {
                            CharSequence subtitle3 = chat.username;
                            if (TextUtils.isEmpty(subtitle3)) {
                                subtitle = LocaleController.getString("MegaPrivate", R.string.MegaPrivate).toLowerCase();
                                title = title2;
                            } else {
                                subtitle = LocaleController.getString("MegaPublic", R.string.MegaPublic).toLowerCase();
                                title = title2;
                            }
                        }
                    } else {
                        subtitle = "";
                        title = null;
                    }
                    cell.useSeparator = nextDialog != null;
                    cell.setData(chat, null, title, subtitle, false, false);
                    boolean contains = this.selectedDialogs.contains(Long.valueOf(cell.getDialogId()));
                    if (oldDialogId == cell.getDialogId()) {
                        z = true;
                    }
                    cell.setChecked(contains, z);
                    break;
                } else {
                    DialogCell cell2 = (DialogCell) holder.itemView;
                    cell2.useSeparator = nextDialog != null;
                    cell2.fullSeparator = dialog.pinned && nextDialog != null && !nextDialog.pinned;
                    if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                        cell2.setDialogSelected(dialog.id == this.openedDialogId);
                    }
                    cell2.setChecked(this.selectedDialogs.contains(Long.valueOf(dialog.id)), false);
                    cell2.setDialog(dialog, this.dialogsType, this.folderId);
                    DialogsPreloader dialogsPreloader = this.preloader;
                    if (dialogsPreloader != null && i < 10) {
                        dialogsPreloader.add(dialog.id);
                        break;
                    }
                }
                break;
            case 4:
                ((DialogMeUrlCell) holder.itemView).setRecentMeUrl((TLRPC.RecentMeUrl) getItem(i));
                break;
            case 5:
                DialogsEmptyCell cell3 = (DialogsEmptyCell) holder.itemView;
                int fromDialogsEmptyType = this.lastDialogsEmptyType;
                int dialogsEmptyType = dialogsEmptyType();
                this.lastDialogsEmptyType = dialogsEmptyType;
                cell3.setType(dialogsEmptyType);
                int i2 = this.dialogsType;
                if (i2 != 7 && i2 != 8) {
                    cell3.setOnUtyanAnimationEndListener(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsAdapter$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            DialogsAdapter.this.m1450x3d1e216b();
                        }
                    });
                    cell3.setOnUtyanAnimationUpdateListener(new Consumer() { // from class: org.telegram.ui.Adapters.DialogsAdapter$$ExternalSyntheticLambda1
                        @Override // androidx.core.util.Consumer
                        public final void accept(Object obj) {
                            DialogsAdapter.this.m1451xc9be4c6c((Float) obj);
                        }
                    });
                    if (!cell3.isUtyanAnimationTriggered() && this.dialogsCount == 0) {
                        this.parentFragment.setContactsAlpha(0.0f);
                        this.parentFragment.setScrollDisabled(true);
                    }
                    if (this.onlineContacts != null && fromDialogsEmptyType == 0) {
                        if (!cell3.isUtyanAnimationTriggered()) {
                            cell3.startUtyanCollapseAnimation(true);
                            break;
                        }
                    } else if (this.forceUpdatingContacts) {
                        if (this.dialogsCount == 0) {
                            cell3.startUtyanCollapseAnimation(false);
                            break;
                        }
                    } else if (cell3.isUtyanAnimationTriggered() && this.lastDialogsEmptyType == 0) {
                        cell3.startUtyanExpandAnimation();
                        break;
                    }
                }
                break;
            case 6:
                UserCell cell4 = (UserCell) holder.itemView;
                int i3 = this.dialogsCount;
                if (i3 == 0) {
                    position = i - 3;
                } else {
                    position = (i - i3) - 2;
                }
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.onlineContacts.get(position).user_id));
                cell4.setData(user, null, null, 0);
                break;
            case 7:
                HeaderCell cell5 = (HeaderCell) holder.itemView;
                int i4 = this.dialogsType;
                if (i4 != 11 && i4 != 12 && i4 != 13) {
                    cell5.setText(LocaleController.getString((this.dialogsCount != 0 || !this.forceUpdatingContacts) ? R.string.YourContacts : R.string.ConnectingYourContacts));
                    break;
                } else if (i == 0) {
                    cell5.setText(LocaleController.getString("ImportHeader", R.string.ImportHeader));
                    break;
                } else {
                    cell5.setText(LocaleController.getString("ImportHeaderContacts", R.string.ImportHeaderContacts));
                    break;
                }
                break;
            case 11:
                TextInfoPrivacyCell cell6 = (TextInfoPrivacyCell) holder.itemView;
                cell6.setText(LocaleController.getString("TapOnThePencil", R.string.TapOnThePencil));
                if (this.arrowDrawable == null) {
                    Drawable drawable = this.mContext.getResources().getDrawable(R.drawable.arrow_newchat);
                    this.arrowDrawable = drawable;
                    drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4), PorterDuff.Mode.MULTIPLY));
                }
                TextView textView = cell6.getTextView();
                textView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.arrowDrawable, (Drawable) null);
                textView.getLayoutParams().width = -2;
                break;
            case 12:
                TextCell cell7 = (TextCell) holder.itemView;
                cell7.setColors(Theme.key_windowBackgroundWhiteBlueText4, Theme.key_windowBackgroundWhiteBlueText4);
                String string = LocaleController.getString("CreateGroupForImport", R.string.CreateGroupForImport);
                if (this.dialogsCount != 0) {
                    z = true;
                }
                cell7.setTextAndIcon(string, R.drawable.msg_groups_create, z);
                cell7.setIsInDialogs();
                cell7.setOffsetFromImage(75);
                break;
            case 14:
                HeaderCell cell8 = (HeaderCell) holder.itemView;
                cell8.setTextSize(14.0f);
                cell8.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
                cell8.setBackgroundColor(Theme.getColor(Theme.key_graySection));
                switch (((DialogsActivity.DialogsHeader) getItem(i)).headerType) {
                    case 0:
                        cell8.setText(LocaleController.getString("MyChannels", R.string.MyChannels));
                        break;
                    case 1:
                        cell8.setText(LocaleController.getString("MyGroups", R.string.MyGroups));
                        break;
                    case 2:
                        cell8.setText(LocaleController.getString("FilterGroups", R.string.FilterGroups));
                        break;
                }
        }
        if (i >= this.dialogsCount + 1) {
            holder.itemView.setAlpha(1.0f);
        }
    }

    /* renamed from: lambda$onBindViewHolder$2$org-telegram-ui-Adapters-DialogsAdapter */
    public /* synthetic */ void m1450x3d1e216b() {
        this.parentFragment.setScrollDisabled(false);
    }

    /* renamed from: lambda$onBindViewHolder$3$org-telegram-ui-Adapters-DialogsAdapter */
    public /* synthetic */ void m1451xc9be4c6c(Float progress) {
        this.parentFragment.setContactsAlpha(progress.floatValue());
    }

    public void setForceUpdatingContacts(boolean forceUpdatingContacts) {
        this.forceUpdatingContacts = forceUpdatingContacts;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        int i2;
        int i3 = this.dialogsCount;
        if (i3 == 0 && this.forceUpdatingContacts) {
            switch (i) {
                case 0:
                    return 5;
                case 1:
                    return 8;
                case 2:
                    return 7;
                case 3:
                    return 13;
            }
        } else if (this.onlineContacts != null) {
            if (i3 == 0) {
                if (i == 0) {
                    return 5;
                }
                if (i == 1) {
                    return 8;
                }
                return i == 2 ? 7 : 6;
            } else if (i < i3) {
                return 0;
            } else {
                if (i == i3) {
                    return 8;
                }
                if (i == i3 + 1) {
                    return 7;
                }
                return i == this.currentCount - 1 ? 10 : 6;
            }
        } else if (this.hasHints) {
            int count = MessagesController.getInstance(this.currentAccount).hintDialogs.size();
            if (i < count + 2) {
                if (i == 0) {
                    return 2;
                }
                if (i == count + 1) {
                    return 3;
                }
                return 4;
            }
            i -= count + 2;
        } else if (this.showArchiveHint) {
            if (i == 0) {
                return 9;
            }
            if (i == 1) {
                return 8;
            }
            i -= 2;
        } else {
            int i4 = this.dialogsType;
            if (i4 == 11 || i4 == 13) {
                if (i == 0) {
                    return 7;
                }
                if (i == 1) {
                    return 12;
                }
                i -= 2;
            } else if (i4 == 12) {
                if (i == 0) {
                    return 7;
                }
                i--;
            }
        }
        int i5 = this.folderId;
        if (i5 == 0 && this.dialogsCount > 10 && i == this.currentCount - 2 && this.dialogsType == 0) {
            return 11;
        }
        int size = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, i5, this.dialogsListFrozen).size();
        if (i == size) {
            if (!this.forceShowEmptyCell && (i2 = this.dialogsType) != 7 && i2 != 8 && !MessagesController.getInstance(this.currentAccount).isDialogsEndReached(this.folderId)) {
                return 1;
            }
            return size == 0 ? 5 : 10;
        } else if (i > size) {
            return 10;
        } else {
            return (this.dialogsType != 2 || !(getItem(i) instanceof DialogsActivity.DialogsHeader)) ? 0 : 14;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void notifyItemMoved(int fromPosition, int toPosition) {
        char c = 0;
        ArrayList<TLRPC.Dialog> dialogs = this.parentFragment.getDialogsArray(this.currentAccount, this.dialogsType, this.folderId, false);
        int fromIndex = fixPosition(fromPosition);
        int toIndex = fixPosition(toPosition);
        TLRPC.Dialog fromDialog = dialogs.get(fromIndex);
        TLRPC.Dialog toDialog = dialogs.get(toIndex);
        int i = this.dialogsType;
        if (i == 7 || i == 8) {
            MessagesController.DialogFilter[] dialogFilterArr = MessagesController.getInstance(this.currentAccount).selectedDialogFilter;
            if (this.dialogsType == 8) {
                c = 1;
            }
            MessagesController.DialogFilter filter = dialogFilterArr[c];
            int idx1 = filter.pinnedDialogs.get(fromDialog.id);
            int idx2 = filter.pinnedDialogs.get(toDialog.id);
            filter.pinnedDialogs.put(fromDialog.id, idx2);
            filter.pinnedDialogs.put(toDialog.id, idx1);
        } else {
            int oldNum = fromDialog.pinnedNum;
            fromDialog.pinnedNum = toDialog.pinnedNum;
            toDialog.pinnedNum = oldNum;
        }
        Collections.swap(dialogs, fromIndex, toIndex);
        super.notifyItemMoved(fromPosition, toPosition);
    }

    public void setArchivedPullDrawable(PullForegroundDrawable drawable) {
        this.pullForegroundDrawable = drawable;
    }

    public void didDatabaseCleared() {
        DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.clear();
        }
    }

    public void resume() {
        DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.resume();
        }
    }

    public void pause() {
        DialogsPreloader dialogsPreloader = this.preloader;
        if (dialogsPreloader != null) {
            dialogsPreloader.pause();
        }
    }

    /* loaded from: classes4.dex */
    public static class DialogsPreloader {
        int currentRequestCount;
        int networkRequestCount;
        boolean resumed;
        private final int MAX_REQUEST_COUNT = 4;
        private final int MAX_NETWORK_REQUEST_COUNT = 6;
        private final int NETWORK_REQUESTS_RESET_TIME = 60000;
        HashSet<Long> dialogsReadyMap = new HashSet<>();
        HashSet<Long> preloadedErrorMap = new HashSet<>();
        HashSet<Long> loadingDialogs = new HashSet<>();
        ArrayList<Long> preloadDialogsPool = new ArrayList<>();
        Runnable clearNetworkRequestCount = new Runnable() { // from class: org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DialogsAdapter.DialogsPreloader.this.m1453x8b23716d();
            }
        };

        /* renamed from: lambda$new$0$org-telegram-ui-Adapters-DialogsAdapter$DialogsPreloader */
        public /* synthetic */ void m1453x8b23716d() {
            this.networkRequestCount = 0;
            start();
        }

        public void add(long dialog_id) {
            if (isReady(dialog_id) || this.preloadedErrorMap.contains(Long.valueOf(dialog_id)) || this.loadingDialogs.contains(Long.valueOf(dialog_id)) || this.preloadDialogsPool.contains(Long.valueOf(dialog_id))) {
                return;
            }
            this.preloadDialogsPool.add(Long.valueOf(dialog_id));
            start();
        }

        public void start() {
            if (!preloadIsAvilable() || !this.resumed || this.preloadDialogsPool.isEmpty() || this.currentRequestCount >= 4 || this.networkRequestCount > 6) {
                return;
            }
            long dialog_id = this.preloadDialogsPool.remove(0).longValue();
            this.currentRequestCount++;
            this.loadingDialogs.add(Long.valueOf(dialog_id));
            MessagesController.getInstance(UserConfig.selectedAccount).ensureMessagesLoaded(dialog_id, 0, new AnonymousClass1(dialog_id));
        }

        /* renamed from: org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1 */
        /* loaded from: classes4.dex */
        public class AnonymousClass1 implements MessagesController.MessagesLoadedCallback {
            final /* synthetic */ long val$dialog_id;

            AnonymousClass1(long j) {
                DialogsPreloader.this = this$0;
                this.val$dialog_id = j;
            }

            @Override // org.telegram.messenger.MessagesController.MessagesLoadedCallback
            public void onMessagesLoaded(final boolean fromCache) {
                final long j = this.val$dialog_id;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        DialogsAdapter.DialogsPreloader.AnonymousClass1.this.m1455xc49b96d0(fromCache, j);
                    }
                });
            }

            /* renamed from: lambda$onMessagesLoaded$0$org-telegram-ui-Adapters-DialogsAdapter$DialogsPreloader$1 */
            public /* synthetic */ void m1455xc49b96d0(boolean fromCache, long dialog_id) {
                if (!fromCache) {
                    DialogsPreloader.this.networkRequestCount++;
                    if (DialogsPreloader.this.networkRequestCount >= 6) {
                        AndroidUtilities.cancelRunOnUIThread(DialogsPreloader.this.clearNetworkRequestCount);
                        AndroidUtilities.runOnUIThread(DialogsPreloader.this.clearNetworkRequestCount, DefaultLoadErrorHandlingPolicy.DEFAULT_TRACK_BLACKLIST_MS);
                    }
                }
                if (DialogsPreloader.this.loadingDialogs.remove(Long.valueOf(dialog_id))) {
                    DialogsPreloader.this.dialogsReadyMap.add(Long.valueOf(dialog_id));
                    DialogsPreloader.this.updateList();
                    DialogsPreloader dialogsPreloader = DialogsPreloader.this;
                    dialogsPreloader.currentRequestCount--;
                    DialogsPreloader.this.start();
                }
            }

            @Override // org.telegram.messenger.MessagesController.MessagesLoadedCallback
            public void onError() {
                final long j = this.val$dialog_id;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Adapters.DialogsAdapter$DialogsPreloader$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        DialogsAdapter.DialogsPreloader.AnonymousClass1.this.m1454x11937a70(j);
                    }
                });
            }

            /* renamed from: lambda$onError$1$org-telegram-ui-Adapters-DialogsAdapter$DialogsPreloader$1 */
            public /* synthetic */ void m1454x11937a70(long dialog_id) {
                if (DialogsPreloader.this.loadingDialogs.remove(Long.valueOf(dialog_id))) {
                    DialogsPreloader.this.preloadedErrorMap.add(Long.valueOf(dialog_id));
                    DialogsPreloader dialogsPreloader = DialogsPreloader.this;
                    dialogsPreloader.currentRequestCount--;
                    DialogsPreloader.this.start();
                }
            }
        }

        private boolean preloadIsAvilable() {
            return false;
        }

        public void updateList() {
        }

        public boolean isReady(long currentDialogId) {
            return this.dialogsReadyMap.contains(Long.valueOf(currentDialogId));
        }

        public boolean preloadedError(long currendDialogId) {
            return this.preloadedErrorMap.contains(Long.valueOf(currendDialogId));
        }

        public void remove(long currentDialogId) {
            this.preloadDialogsPool.remove(Long.valueOf(currentDialogId));
        }

        public void clear() {
            this.dialogsReadyMap.clear();
            this.preloadedErrorMap.clear();
            this.loadingDialogs.clear();
            this.preloadDialogsPool.clear();
            this.currentRequestCount = 0;
            this.networkRequestCount = 0;
            AndroidUtilities.cancelRunOnUIThread(this.clearNetworkRequestCount);
            updateList();
        }

        public void resume() {
            this.resumed = true;
            start();
        }

        public void pause() {
            this.resumed = false;
        }
    }

    public int getCurrentCount() {
        return this.currentCount;
    }

    public void setForceShowEmptyCell(boolean forceShowEmptyCell) {
        this.forceShowEmptyCell = forceShowEmptyCell;
    }

    /* loaded from: classes4.dex */
    public class LastEmptyView extends View {
        public boolean moving;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public LastEmptyView(Context context) {
            super(context);
            DialogsAdapter.this = this$0;
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height;
            int size = DialogsAdapter.this.parentFragment.getDialogsArray(DialogsAdapter.this.currentAccount, DialogsAdapter.this.dialogsType, DialogsAdapter.this.folderId, DialogsAdapter.this.dialogsListFrozen).size();
            int archiveHeight = 0;
            boolean hasArchive = DialogsAdapter.this.dialogsType == 0 && MessagesController.getInstance(DialogsAdapter.this.currentAccount).dialogs_dict.get(DialogObject.makeFolderDialogId(1)) != null;
            View parent = (View) getParent();
            int blurOffset = 0;
            if (parent instanceof BlurredRecyclerView) {
                blurOffset = ((BlurredRecyclerView) parent).blurTopPadding;
            }
            int paddingTop = parent.getPaddingTop() - blurOffset;
            if (size == 0 || (paddingTop == 0 && !hasArchive)) {
                height = 0;
            } else {
                int height2 = View.MeasureSpec.getSize(heightMeasureSpec);
                if (height2 == 0) {
                    height2 = parent.getMeasuredHeight();
                }
                if (height2 == 0) {
                    height2 = (AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0);
                }
                int height3 = height2 - blurOffset;
                int cellHeight = AndroidUtilities.dp(SharedConfig.useThreeLinesLayout ? 78.0f : 72.0f);
                int dialogsHeight = (size * cellHeight) + (size - 1);
                if (DialogsAdapter.this.onlineContacts != null) {
                    dialogsHeight += (DialogsAdapter.this.onlineContacts.size() * AndroidUtilities.dp(58.0f)) + (DialogsAdapter.this.onlineContacts.size() - 1) + AndroidUtilities.dp(52.0f);
                }
                if (hasArchive) {
                    archiveHeight = cellHeight + 1;
                }
                if (dialogsHeight < height3) {
                    height = (height3 - dialogsHeight) + archiveHeight;
                    if (paddingTop != 0 && (height = height - AndroidUtilities.statusBarHeight) < 0) {
                        height = 0;
                    }
                } else if (dialogsHeight - height3 < archiveHeight) {
                    height = archiveHeight - (dialogsHeight - height3);
                    if (paddingTop != 0) {
                        height -= AndroidUtilities.statusBarHeight;
                    }
                    if (height < 0) {
                        height = 0;
                    }
                } else {
                    height = 0;
                }
            }
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), height);
        }
    }
}
