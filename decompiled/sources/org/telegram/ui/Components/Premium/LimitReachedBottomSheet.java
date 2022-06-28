package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes5.dex */
public class LimitReachedBottomSheet extends BottomSheetWithRecyclerListView {
    public static final int TYPE_ACCOUNTS = 7;
    public static final int TYPE_CAPTION = 8;
    public static final int TYPE_CHATS_IN_FOLDER = 4;
    public static final int TYPE_FOLDERS = 3;
    public static final int TYPE_GIFS = 9;
    public static final int TYPE_LARGE_FILE = 6;
    public static final int TYPE_PIN_DIALOGS = 0;
    public static final int TYPE_PUBLIC_LINKS = 2;
    public static final int TYPE_STICKERS = 10;
    public static final int TYPE_TO_MANY_COMMUNITIES = 5;
    View divider;
    RecyclerItemsEnterAnimator enterAnimator;
    private boolean isVeryLargeFile;
    LimitParams limitParams;
    LimitPreviewView limitPreviewView;
    boolean loadingAdminedChannels;
    public Runnable onShowPremiumScreenRunnable;
    public Runnable onSuccessRunnable;
    BaseFragment parentFragment;
    public boolean parentIsChannel;
    PremiumButtonView premiumButtonView;
    int rowCount;
    final int type;
    ArrayList<TLRPC.Chat> chats = new ArrayList<>();
    int headerRow = -1;
    int dividerRow = -1;
    int chatsTitleRow = -1;
    int chatStartRow = -1;
    int loadingRow = -1;
    private int currentValue = -1;
    HashSet<TLRPC.Chat> selectedChats = new HashSet<>();
    private ArrayList<TLRPC.Chat> inactiveChats = new ArrayList<>();
    private ArrayList<String> inactiveChatsSignatures = new ArrayList<>();
    private boolean loading = false;

    /* loaded from: classes5.dex */
    public static class LimitParams {
        int icon = 0;
        String descriptionStr = null;
        String descriptionStrPremium = null;
        String descriptionStrLocked = null;
        int defaultLimit = 0;
        int premiumLimit = 0;
    }

    public static String limitTypeToServerString(int type) {
        switch (type) {
            case 0:
                return "double_limits__dialog_pinned";
            case 1:
            case 7:
            default:
                return null;
            case 2:
                return "double_limits__channels_public";
            case 3:
                return "double_limits__dialog_filters";
            case 4:
                return "double_limits__dialog_filters_chats";
            case 5:
                return "double_limits__channels";
            case 6:
                return "double_limits__upload_max_fileparts";
            case 8:
                return "double_limits__caption_length";
            case 9:
                return "double_limits__saved_gifs";
            case 10:
                return "double_limits__stickers_faved";
        }
    }

    public LimitReachedBottomSheet(BaseFragment fragment, Context context, int type, int currentAccount) {
        super(fragment, false, hasFixedSize(type));
        fixNavigationBar();
        this.parentFragment = fragment;
        this.type = type;
        this.currentAccount = currentAccount;
        updateRows();
        if (type == 2) {
            loadAdminedChannels();
        } else if (type == 5) {
            loadInactiveChannels();
        }
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    public void onViewCreated(FrameLayout containerView) {
        super.onViewCreated(containerView);
        Context context = containerView.getContext();
        this.premiumButtonView = new PremiumButtonView(context, true);
        updatePremiumButtonText();
        if (!this.hasFixedSize) {
            View view = new View(context) { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet.1
                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), 1.0f, Theme.dividerPaint);
                }
            };
            this.divider = view;
            view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
            containerView.addView(this.divider, LayoutHelper.createFrame(-1, 72.0f, 80, 0.0f, 0.0f, 0.0f, 0.0f));
        }
        containerView.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 0.0f, 16.0f, 12.0f));
        this.recyclerListView.setPadding(0, 0, 0, AndroidUtilities.dp(72.0f));
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet$$ExternalSyntheticLambda10
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view2, int i) {
                LimitReachedBottomSheet.this.m2894x98a97397(view2, i);
            }
        });
        this.recyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view2, int i) {
                return LimitReachedBottomSheet.this.m2895x9ead3ef6(view2, i);
            }
        });
        this.premiumButtonView.buttonLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                LimitReachedBottomSheet.this.m2896xa4b10a55(view2);
            }
        });
        this.premiumButtonView.overlayTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                LimitReachedBottomSheet.this.m2897xaab4d5b4(view2);
            }
        });
        this.enterAnimator = new RecyclerItemsEnterAnimator(this.recyclerListView, true);
    }

    /* renamed from: lambda$onViewCreated$0$org-telegram-ui-Components-Premium-LimitReachedBottomSheet */
    public /* synthetic */ void m2894x98a97397(View view, int position) {
        if (view instanceof AdminedChannelCell) {
            AdminedChannelCell adminedChannelCell = (AdminedChannelCell) view;
            TLRPC.Chat chat = adminedChannelCell.getCurrentChannel();
            if (this.selectedChats.contains(chat)) {
                this.selectedChats.remove(chat);
            } else {
                this.selectedChats.add(chat);
            }
            adminedChannelCell.setChecked(this.selectedChats.contains(chat), true);
            updateButton();
        } else if (view instanceof GroupCreateUserCell) {
            GroupCreateUserCell cell = (GroupCreateUserCell) view;
            TLRPC.Chat chat2 = (TLRPC.Chat) cell.getObject();
            if (this.selectedChats.contains(chat2)) {
                this.selectedChats.remove(chat2);
            } else {
                this.selectedChats.add(chat2);
            }
            cell.setChecked(this.selectedChats.contains(chat2), true);
            updateButton();
        }
    }

    /* renamed from: lambda$onViewCreated$1$org-telegram-ui-Components-Premium-LimitReachedBottomSheet */
    public /* synthetic */ boolean m2895x9ead3ef6(View view, int position) {
        this.recyclerListView.getOnItemClickListener().onItemClick(view, position);
        view.performHapticFeedback(0);
        return false;
    }

    /* renamed from: lambda$onViewCreated$2$org-telegram-ui-Components-Premium-LimitReachedBottomSheet */
    public /* synthetic */ void m2896xa4b10a55(View v) {
        if (UserConfig.getInstance(this.currentAccount).isPremium() || MessagesController.getInstance(this.currentAccount).premiumLocked || this.isVeryLargeFile) {
            dismiss();
            return;
        }
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment == null) {
            return;
        }
        if (baseFragment.getVisibleDialog() != null) {
            this.parentFragment.getVisibleDialog().dismiss();
        }
        this.parentFragment.presentFragment(new PremiumPreviewFragment(limitTypeToServerString(this.type)));
        Runnable runnable = this.onShowPremiumScreenRunnable;
        if (runnable != null) {
            runnable.run();
        }
        dismiss();
    }

    /* renamed from: lambda$onViewCreated$3$org-telegram-ui-Components-Premium-LimitReachedBottomSheet */
    public /* synthetic */ void m2897xaab4d5b4(View v) {
        if (this.selectedChats.isEmpty()) {
            return;
        }
        int i = this.type;
        if (i == 2) {
            revokeSelectedLinks();
        } else if (i == 5) {
            leaveFromSelectedGroups();
        }
    }

    public void updatePremiumButtonText() {
        if (UserConfig.getInstance(this.currentAccount).isPremium() || MessagesController.getInstance(this.currentAccount).premiumLocked || this.isVeryLargeFile) {
            this.premiumButtonView.buttonTextView.setText(LocaleController.getString((int) R.string.OK));
            this.premiumButtonView.hideIcon();
            return;
        }
        this.premiumButtonView.buttonTextView.setText(LocaleController.getString("IncreaseLimit", R.string.IncreaseLimit));
        this.premiumButtonView.setIcon(this.type == 7 ? R.raw.addone_icon : R.raw.double_icon);
    }

    private void leaveFromSelectedGroups() {
        final TLRPC.User currentUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        final ArrayList<TLRPC.Chat> chats = new ArrayList<>(this.selectedChats);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.formatPluralString("LeaveCommunities", chats.size(), new Object[0]));
        if (chats.size() == 1) {
            TLRPC.Chat channel = chats.get(0);
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChannelLeaveAlertWithName", R.string.ChannelLeaveAlertWithName, channel.title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChatsLeaveAlert", R.string.ChatsLeaveAlert, new Object[0])));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LimitReachedBottomSheet.this.m2889xe2652fc7(chats, currentUser, dialogInterface, i);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
        }
    }

    /* renamed from: lambda$leaveFromSelectedGroups$4$org-telegram-ui-Components-Premium-LimitReachedBottomSheet */
    public /* synthetic */ void m2889xe2652fc7(ArrayList chats, TLRPC.User currentUser, DialogInterface dialogInterface, int interface2) {
        dismiss();
        for (int i = 0; i < chats.size(); i++) {
            TLRPC.Chat chat = (TLRPC.Chat) chats.get(i);
            MessagesController.getInstance(this.currentAccount).putChat(chat, false);
            MessagesController.getInstance(this.currentAccount).deleteParticipantFromChat(chat.id, currentUser, null);
        }
    }

    private void updateButton() {
        if (this.selectedChats.size() > 0) {
            String str = null;
            int i = this.type;
            if (i == 2) {
                str = LocaleController.formatPluralString("RevokeLinks", this.selectedChats.size(), new Object[0]);
            } else if (i == 5) {
                str = LocaleController.formatPluralString("LeaveCommunities", this.selectedChats.size(), new Object[0]);
            }
            this.premiumButtonView.setOverlayText(str, true, true);
            return;
        }
        this.premiumButtonView.clearOverlayText();
    }

    private static boolean hasFixedSize(int type) {
        if (type == 0 || type == 3 || type == 4 || type == 6 || type == 7) {
            return true;
        }
        return false;
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    public CharSequence getTitle() {
        return LocaleController.getString("LimitReached", R.string.LimitReached);
    }

    @Override // org.telegram.ui.Components.BottomSheetWithRecyclerListView
    public RecyclerListView.SelectionAdapter createAdapter() {
        return new RecyclerListView.SelectionAdapter() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet.2
            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder holder) {
                return holder.getItemViewType() == 1 || holder.getItemViewType() == 4;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view;
                Context context = parent.getContext();
                switch (viewType) {
                    case 1:
                        view = new AdminedChannelCell(context, new View.OnClickListener() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet.2.1
                            @Override // android.view.View.OnClickListener
                            public void onClick(View v) {
                                AdminedChannelCell cell = (AdminedChannelCell) v.getParent();
                                ArrayList<TLRPC.Chat> channels = new ArrayList<>();
                                channels.add(cell.getCurrentChannel());
                                LimitReachedBottomSheet.this.revokeLinks(channels);
                            }
                        }, true, 9);
                        break;
                    case 2:
                        view = new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_windowBackgroundGray));
                        break;
                    case 3:
                        view = new HeaderCell(context);
                        view.setPadding(0, 0, 0, AndroidUtilities.dp(8.0f));
                        break;
                    case 4:
                        view = new GroupCreateUserCell(context, 1, 8, false);
                        break;
                    case 5:
                        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, null);
                        flickerLoadingView.setViewType(LimitReachedBottomSheet.this.type == 2 ? 22 : 21);
                        flickerLoadingView.setIsSingleCell(true);
                        flickerLoadingView.setIgnoreHeightCheck(true);
                        flickerLoadingView.setItemsCount(10);
                        view = flickerLoadingView;
                        break;
                    default:
                        view = new HeaderView(context);
                        break;
                }
                view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                return new RecyclerListView.Holder(view);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                boolean z = false;
                if (holder.getItemViewType() == 4) {
                    TLRPC.Chat chat = (TLRPC.Chat) LimitReachedBottomSheet.this.inactiveChats.get(position - LimitReachedBottomSheet.this.chatStartRow);
                    GroupCreateUserCell cell = (GroupCreateUserCell) holder.itemView;
                    String signature = (String) LimitReachedBottomSheet.this.inactiveChatsSignatures.get(position - LimitReachedBottomSheet.this.chatStartRow);
                    cell.setObject(chat, chat.title, signature, true);
                    cell.setChecked(LimitReachedBottomSheet.this.selectedChats.contains(chat), false);
                } else if (holder.getItemViewType() == 1) {
                    TLRPC.Chat chat2 = LimitReachedBottomSheet.this.chats.get(position - LimitReachedBottomSheet.this.chatStartRow);
                    AdminedChannelCell adminedChannelCell = (AdminedChannelCell) holder.itemView;
                    TLRPC.Chat oldChat = adminedChannelCell.getCurrentChannel();
                    adminedChannelCell.setChannel(chat2, false);
                    boolean contains = LimitReachedBottomSheet.this.selectedChats.contains(chat2);
                    if (oldChat == chat2) {
                        z = true;
                    }
                    adminedChannelCell.setChecked(contains, z);
                } else if (holder.getItemViewType() == 3) {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (LimitReachedBottomSheet.this.type == 2) {
                        headerCell.setText(LocaleController.getString("YourPublicCommunities", R.string.YourPublicCommunities));
                    } else {
                        headerCell.setText(LocaleController.getString("LastActiveCommunities", R.string.LastActiveCommunities));
                    }
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemViewType(int position) {
                if (LimitReachedBottomSheet.this.headerRow == position) {
                    return 0;
                }
                if (LimitReachedBottomSheet.this.dividerRow == position) {
                    return 2;
                }
                if (LimitReachedBottomSheet.this.chatsTitleRow == position) {
                    return 3;
                }
                if (LimitReachedBottomSheet.this.loadingRow == position) {
                    return 5;
                }
                if (LimitReachedBottomSheet.this.type == 5) {
                    return 4;
                }
                return 1;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return LimitReachedBottomSheet.this.rowCount;
            }
        };
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public void setVeryLargeFile(boolean b) {
        this.isVeryLargeFile = b;
        updatePremiumButtonText();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class HeaderView extends LinearLayout {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public HeaderView(Context context) {
            super(context);
            String descriptionStr;
            LimitReachedBottomSheet.this = r26;
            setOrientation(1);
            setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
            r26.limitParams = LimitReachedBottomSheet.getLimitParams(r26.type, r26.currentAccount);
            int icon = r26.limitParams.icon;
            boolean premiumLocked = MessagesController.getInstance(r26.currentAccount).premiumLocked;
            if (!premiumLocked) {
                descriptionStr = (UserConfig.getInstance(r26.currentAccount).isPremium() || r26.isVeryLargeFile) ? r26.limitParams.descriptionStrPremium : r26.limitParams.descriptionStr;
            } else {
                descriptionStr = r26.limitParams.descriptionStrLocked;
            }
            int defaultLimit = r26.limitParams.defaultLimit;
            int premiumLimit = r26.limitParams.premiumLimit;
            int currentValue = r26.currentValue;
            float position = 0.5f;
            if (r26.type == 3) {
                currentValue = MessagesController.getInstance(r26.currentAccount).dialogFilters.size() - 1;
            } else if (r26.type == 7) {
                currentValue = UserConfig.getActivatedAccountsCount();
            }
            if (r26.type == 0) {
                int pinnedCount = 0;
                ArrayList<TLRPC.Dialog> dialogs = MessagesController.getInstance(r26.currentAccount).getDialogs(0);
                int N = dialogs.size();
                for (int a = 0; a < N; a++) {
                    TLRPC.Dialog dialog = dialogs.get(a);
                    if (!(dialog instanceof TLRPC.TL_dialogFolder) && dialog.pinned) {
                        pinnedCount++;
                    }
                }
                currentValue = pinnedCount;
            }
            if (UserConfig.getInstance(r26.currentAccount).isPremium() || r26.isVeryLargeFile) {
                currentValue = premiumLimit;
                position = 1.0f;
            } else {
                currentValue = currentValue < 0 ? defaultLimit : currentValue;
                if (r26.type == 7) {
                    if (currentValue > defaultLimit) {
                        position = (currentValue - defaultLimit) / (premiumLimit - defaultLimit);
                    }
                } else {
                    position = currentValue / premiumLimit;
                }
            }
            r26.limitPreviewView = new LimitPreviewView(context, icon, currentValue, premiumLimit);
            r26.limitPreviewView.setBagePosition(position);
            r26.limitPreviewView.setType(r26.type);
            r26.limitPreviewView.defaultCount.setVisibility(8);
            if (!premiumLocked) {
                if (UserConfig.getInstance(r26.currentAccount).isPremium() || r26.isVeryLargeFile) {
                    r26.limitPreviewView.premiumCount.setVisibility(8);
                    if (r26.type == 6) {
                        r26.limitPreviewView.defaultCount.setText("2 GB");
                    } else {
                        r26.limitPreviewView.defaultCount.setText(Integer.toString(defaultLimit));
                    }
                    r26.limitPreviewView.defaultCount.setVisibility(0);
                }
            } else {
                r26.limitPreviewView.setPremiumLocked();
            }
            if (r26.type == 2 || r26.type == 5) {
                r26.limitPreviewView.setDelayedAnimation();
            }
            addView(r26.limitPreviewView, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 0, 0, 0));
            TextView title = new TextView(context);
            title.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            if (r26.type == 6) {
                title.setText(LocaleController.getString("FileTooLarge", R.string.FileTooLarge));
            } else {
                title.setText(LocaleController.getString("LimitReached", R.string.LimitReached));
            }
            title.setTextSize(1, 20.0f);
            title.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(title, LayoutHelper.createLinear(-2, -2, 1, 0, 22, 0, 10));
            TextView description = new TextView(context);
            description.setText(AndroidUtilities.replaceTags(descriptionStr));
            description.setTextSize(1, 14.0f);
            description.setGravity(1);
            description.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(description, LayoutHelper.createLinear(-2, -2, 0, 24, 0, 24, 24));
        }
    }

    public static LimitParams getLimitParams(int type, int currentAccount) {
        LimitParams limitParams = new LimitParams();
        if (type == 0) {
            limitParams.defaultLimit = MessagesController.getInstance(currentAccount).dialogFiltersPinnedLimitDefault;
            limitParams.premiumLimit = MessagesController.getInstance(currentAccount).dialogFiltersPinnedLimitPremium;
            limitParams.icon = R.drawable.msg_limit_pin;
            limitParams.descriptionStr = LocaleController.formatString("LimitReachedPinDialogs", R.string.LimitReachedPinDialogs, Integer.valueOf(limitParams.defaultLimit), Integer.valueOf(limitParams.premiumLimit));
            limitParams.descriptionStrPremium = LocaleController.formatString("LimitReachedPinDialogsPremium", R.string.LimitReachedPinDialogsPremium, Integer.valueOf(limitParams.premiumLimit));
            limitParams.descriptionStrLocked = LocaleController.formatString("LimitReachedPinDialogsLocked", R.string.LimitReachedPinDialogsLocked, Integer.valueOf(limitParams.defaultLimit));
        } else if (type == 2) {
            limitParams.defaultLimit = MessagesController.getInstance(currentAccount).publicLinksLimitDefault;
            limitParams.premiumLimit = MessagesController.getInstance(currentAccount).publicLinksLimitPremium;
            limitParams.icon = R.drawable.msg_limit_links;
            limitParams.descriptionStr = LocaleController.formatString("LimitReachedPublicLinks", R.string.LimitReachedPublicLinks, Integer.valueOf(limitParams.defaultLimit), Integer.valueOf(limitParams.premiumLimit));
            limitParams.descriptionStrPremium = LocaleController.formatString("LimitReachedPublicLinksPremium", R.string.LimitReachedPublicLinksPremium, Integer.valueOf(limitParams.premiumLimit));
            limitParams.descriptionStrLocked = LocaleController.formatString("LimitReachedPublicLinksLocked", R.string.LimitReachedPublicLinksLocked, Integer.valueOf(limitParams.defaultLimit));
        } else if (type == 3) {
            limitParams.defaultLimit = MessagesController.getInstance(currentAccount).dialogFiltersLimitDefault;
            limitParams.premiumLimit = MessagesController.getInstance(currentAccount).dialogFiltersLimitPremium;
            limitParams.icon = R.drawable.msg_limit_folder;
            limitParams.descriptionStr = LocaleController.formatString("LimitReachedFolders", R.string.LimitReachedFolders, Integer.valueOf(limitParams.defaultLimit), Integer.valueOf(limitParams.premiumLimit));
            limitParams.descriptionStrPremium = LocaleController.formatString("LimitReachedFoldersPremium", R.string.LimitReachedFoldersPremium, Integer.valueOf(limitParams.premiumLimit));
            limitParams.descriptionStrLocked = LocaleController.formatString("LimitReachedFoldersLocked", R.string.LimitReachedFoldersLocked, Integer.valueOf(limitParams.defaultLimit));
        } else if (type == 4) {
            limitParams.defaultLimit = MessagesController.getInstance(currentAccount).dialogFiltersChatsLimitDefault;
            limitParams.premiumLimit = MessagesController.getInstance(currentAccount).dialogFiltersChatsLimitPremium;
            limitParams.icon = R.drawable.msg_limit_chats;
            limitParams.descriptionStr = LocaleController.formatString("LimitReachedChatInFolders", R.string.LimitReachedChatInFolders, Integer.valueOf(limitParams.defaultLimit), Integer.valueOf(limitParams.premiumLimit));
            limitParams.descriptionStrPremium = LocaleController.formatString("LimitReachedChatInFoldersPremium", R.string.LimitReachedChatInFoldersPremium, Integer.valueOf(limitParams.premiumLimit));
            limitParams.descriptionStrLocked = LocaleController.formatString("LimitReachedChatInFoldersLocked", R.string.LimitReachedChatInFoldersLocked, Integer.valueOf(limitParams.defaultLimit));
        } else if (type == 5) {
            limitParams.defaultLimit = MessagesController.getInstance(currentAccount).channelsLimitDefault;
            limitParams.premiumLimit = MessagesController.getInstance(currentAccount).channelsLimitPremium;
            limitParams.icon = R.drawable.msg_limit_groups;
            limitParams.descriptionStr = LocaleController.formatString("LimitReachedCommunities", R.string.LimitReachedCommunities, Integer.valueOf(limitParams.defaultLimit), Integer.valueOf(limitParams.premiumLimit));
            limitParams.descriptionStrPremium = LocaleController.formatString("LimitReachedCommunitiesPremium", R.string.LimitReachedCommunitiesPremium, Integer.valueOf(limitParams.premiumLimit));
            limitParams.descriptionStrLocked = LocaleController.formatString("LimitReachedCommunitiesLocked", R.string.LimitReachedCommunitiesLocked, Integer.valueOf(limitParams.defaultLimit));
        } else if (type == 6) {
            limitParams.defaultLimit = 100;
            limitParams.premiumLimit = 200;
            limitParams.icon = R.drawable.msg_limit_folder;
            limitParams.descriptionStr = LocaleController.formatString("LimitReachedFileSize", R.string.LimitReachedFileSize, "2 GB", "4 GB");
            limitParams.descriptionStrPremium = LocaleController.formatString("LimitReachedFileSizePremium", R.string.LimitReachedFileSizePremium, "4 GB");
            limitParams.descriptionStrLocked = LocaleController.formatString("LimitReachedFileSizeLocked", R.string.LimitReachedFileSizeLocked, "2 GB");
        } else if (type == 7) {
            limitParams.defaultLimit = 3;
            limitParams.premiumLimit = 4;
            limitParams.icon = R.drawable.msg_limit_accounts;
            limitParams.descriptionStr = LocaleController.formatString("LimitReachedAccounts", R.string.LimitReachedAccounts, Integer.valueOf(limitParams.defaultLimit), Integer.valueOf(limitParams.premiumLimit));
            limitParams.descriptionStrPremium = LocaleController.formatString("LimitReachedAccountsPremium", R.string.LimitReachedAccountsPremium, Integer.valueOf(limitParams.premiumLimit));
            limitParams.descriptionStrLocked = LocaleController.formatString("LimitReachedAccountsPremium", R.string.LimitReachedAccountsPremium, Integer.valueOf(limitParams.defaultLimit));
        }
        return limitParams;
    }

    private void loadAdminedChannels() {
        this.loadingAdminedChannels = true;
        this.loading = true;
        updateRows();
        TLRPC.TL_channels_getAdminedPublicChannels req = new TLRPC.TL_channels_getAdminedPublicChannels();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet$$ExternalSyntheticLambda7
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LimitReachedBottomSheet.this.m2891xba41a75d(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadAdminedChannels$6$org-telegram-ui-Components-Premium-LimitReachedBottomSheet */
    public /* synthetic */ void m2891xba41a75d(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                LimitReachedBottomSheet.this.m2890xb43ddbfe(response);
            }
        });
    }

    /* renamed from: lambda$loadAdminedChannels$5$org-telegram-ui-Components-Premium-LimitReachedBottomSheet */
    public /* synthetic */ void m2890xb43ddbfe(TLObject response) {
        this.loadingAdminedChannels = false;
        if (response != null) {
            TLRPC.TL_messages_chats res = (TLRPC.TL_messages_chats) response;
            this.chats.clear();
            this.chats.addAll(res.chats);
            this.loading = false;
            this.enterAnimator.showItemsAnimated(this.chatsTitleRow + 4);
            int savedTop = 0;
            int i = 0;
            while (true) {
                if (i >= this.recyclerListView.getChildCount()) {
                    break;
                } else if (!(this.recyclerListView.getChildAt(i) instanceof HeaderView)) {
                    i++;
                } else {
                    savedTop = this.recyclerListView.getChildAt(i).getTop();
                    break;
                }
            }
            updateRows();
            if (this.headerRow >= 0 && savedTop != 0) {
                ((LinearLayoutManager) this.recyclerListView.getLayoutManager()).scrollToPositionWithOffset(this.headerRow + 1, savedTop);
            }
        }
        int currentValue = Math.max(this.chats.size(), this.limitParams.defaultLimit);
        this.limitPreviewView.setIconValue(currentValue);
        this.limitPreviewView.setBagePosition(currentValue / this.limitParams.premiumLimit);
        this.limitPreviewView.startDelayedAnimation();
    }

    private void updateRows() {
        this.rowCount = 0;
        this.dividerRow = -1;
        this.chatStartRow = -1;
        this.loadingRow = -1;
        this.rowCount = 0 + 1;
        this.headerRow = 0;
        if (!hasFixedSize(this.type)) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.dividerRow = i;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.chatsTitleRow = i2;
            if (this.loading) {
                this.rowCount = i3 + 1;
                this.loadingRow = i3;
            } else {
                this.chatStartRow = i3;
                if (this.type == 5) {
                    this.rowCount = i3 + this.inactiveChats.size();
                } else {
                    this.rowCount = i3 + this.chats.size();
                }
            }
        }
        notifyDataSetChanged();
    }

    private void revokeSelectedLinks() {
        ArrayList<TLRPC.Chat> channels = new ArrayList<>(this.selectedChats);
        revokeLinks(channels);
    }

    public void revokeLinks(final ArrayList<TLRPC.Chat> channels) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(LocaleController.formatPluralString("RevokeLinks", channels.size(), new Object[0]));
        if (channels.size() == 1) {
            TLRPC.Chat channel = channels.get(0);
            if (this.parentIsChannel) {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", R.string.RevokeLinkAlertChannel, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
            } else {
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", R.string.RevokeLinkAlert, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
            }
        } else if (this.parentIsChannel) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinksAlertChannel", R.string.RevokeLinksAlertChannel, new Object[0])));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinksAlert", R.string.RevokeLinksAlert, new Object[0])));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                LimitReachedBottomSheet.this.m2899xcbc3b480(channels, dialogInterface, i);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
        }
    }

    /* renamed from: lambda$revokeLinks$8$org-telegram-ui-Components-Premium-LimitReachedBottomSheet */
    public /* synthetic */ void m2899xcbc3b480(ArrayList channels, DialogInterface dialogInterface, int interface2) {
        dismiss();
        for (int i = 0; i < channels.size(); i++) {
            TLRPC.TL_channels_updateUsername req1 = new TLRPC.TL_channels_updateUsername();
            TLRPC.Chat channel = (TLRPC.Chat) channels.get(i);
            req1.channel = MessagesController.getInputChannel(channel);
            req1.username = "";
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, new RequestDelegate() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet$$ExternalSyntheticLambda9
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    LimitReachedBottomSheet.this.m2898xc5bfe921(tLObject, tL_error);
                }
            }, 64);
        }
    }

    /* renamed from: lambda$revokeLinks$7$org-telegram-ui-Components-Premium-LimitReachedBottomSheet */
    public /* synthetic */ void m2898xc5bfe921(TLObject response1, TLRPC.TL_error error1) {
        if (response1 instanceof TLRPC.TL_boolTrue) {
            AndroidUtilities.runOnUIThread(this.onSuccessRunnable);
        }
    }

    private void loadInactiveChannels() {
        this.loading = true;
        updateRows();
        TLRPC.TL_channels_getInactiveChannels inactiveChannelsRequest = new TLRPC.TL_channels_getInactiveChannels();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(inactiveChannelsRequest, new RequestDelegate() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet$$ExternalSyntheticLambda8
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                LimitReachedBottomSheet.this.m2892x3f9e20c5(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadInactiveChannels$10$org-telegram-ui-Components-Premium-LimitReachedBottomSheet */
    public /* synthetic */ void m2892x3f9e20c5(TLObject response, TLRPC.TL_error error) {
        String dateFormat;
        if (error == null) {
            final TLRPC.TL_messages_inactiveChats chats = (TLRPC.TL_messages_inactiveChats) response;
            final ArrayList<String> signatures = new ArrayList<>();
            for (int i = 0; i < chats.chats.size(); i++) {
                TLRPC.Chat chat = chats.chats.get(i);
                int currentDate = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                int date = chats.dates.get(i).intValue();
                int daysDif = (currentDate - date) / 86400;
                if (daysDif < 30) {
                    dateFormat = LocaleController.formatPluralString("Days", daysDif, new Object[0]);
                } else if (daysDif < 365) {
                    dateFormat = LocaleController.formatPluralString("Months", daysDif / 30, new Object[0]);
                } else {
                    dateFormat = LocaleController.formatPluralString("Years", daysDif / 365, new Object[0]);
                }
                if (ChatObject.isMegagroup(chat)) {
                    String members = LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]);
                    signatures.add(LocaleController.formatString("InactiveChatSignature", R.string.InactiveChatSignature, members, dateFormat));
                } else if (ChatObject.isChannel(chat)) {
                    signatures.add(LocaleController.formatString("InactiveChannelSignature", R.string.InactiveChannelSignature, dateFormat));
                } else {
                    String members2 = LocaleController.formatPluralString("Members", chat.participants_count, new Object[0]);
                    signatures.add(LocaleController.formatString("InactiveChatSignature", R.string.InactiveChatSignature, members2, dateFormat));
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Premium.LimitReachedBottomSheet$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    LimitReachedBottomSheet.this.m2893x39064d37(signatures, chats);
                }
            });
        }
    }

    /* renamed from: lambda$loadInactiveChannels$9$org-telegram-ui-Components-Premium-LimitReachedBottomSheet */
    public /* synthetic */ void m2893x39064d37(ArrayList signatures, TLRPC.TL_messages_inactiveChats chats) {
        this.inactiveChatsSignatures.clear();
        this.inactiveChats.clear();
        this.inactiveChatsSignatures.addAll(signatures);
        this.inactiveChats.addAll(chats.chats);
        this.loading = false;
        this.enterAnimator.showItemsAnimated(this.chatsTitleRow + 4);
        int savedTop = 0;
        int i = 0;
        while (true) {
            if (i >= this.recyclerListView.getChildCount()) {
                break;
            } else if (!(this.recyclerListView.getChildAt(i) instanceof HeaderView)) {
                i++;
            } else {
                savedTop = this.recyclerListView.getChildAt(i).getTop();
                break;
            }
        }
        updateRows();
        if (this.headerRow >= 0 && savedTop != 0) {
            ((LinearLayoutManager) this.recyclerListView.getLayoutManager()).scrollToPositionWithOffset(this.headerRow + 1, savedTop);
        }
        int currentValue = Math.max(this.inactiveChats.size(), this.limitParams.defaultLimit);
        this.limitPreviewView.setIconValue(currentValue);
        this.limitPreviewView.setBagePosition(currentValue / this.limitParams.premiumLimit);
        this.limitPreviewView.startDelayedAnimation();
    }
}
