package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;
/* loaded from: classes4.dex */
public class ContactsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int search_button = 0;
    private static final int sort_button = 1;
    private AnimatorSet bounceIconAnimator;
    private long channelId;
    private long chatId;
    private boolean createSecretChat;
    private boolean creatingChat;
    private ContactsActivityDelegate delegate;
    private boolean destroyAfterSelect;
    private boolean disableSections;
    private StickerEmptyView emptyView;
    private RLottieImageView floatingButton;
    private FrameLayout floatingButtonContainer;
    private boolean floatingHidden;
    private boolean hasGps;
    private LongSparseArray<TLRPC.User> ignoreUsers;
    private String initialSearchString;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ContactsAdapter listViewAdapter;
    private boolean needPhonebook;
    private boolean onlyUsers;
    private AlertDialog permissionDialog;
    private long permissionRequestTime;
    private int prevPosition;
    private int prevTop;
    private boolean returnAsResult;
    private boolean scrollUpdated;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private boolean sortByName;
    private ActionBarMenuItem sortItem;
    private AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private boolean allowSelf = true;
    private boolean allowBots = true;
    private boolean needForwardCount = true;
    private boolean needFinishFragment = true;
    private boolean resetDelegate = true;
    private String selectAlertString = null;
    private boolean allowUsernameSearch = true;
    private boolean askAboutContacts = true;
    private boolean checkPermission = true;
    private int animationIndex = -1;

    /* loaded from: classes4.dex */
    public interface ContactsActivityDelegate {
        void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity);
    }

    public ContactsActivity(Bundle args) {
        super(args);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        this.checkPermission = UserConfig.getInstance(this.currentAccount).syncContacts;
        if (this.arguments != null) {
            this.onlyUsers = this.arguments.getBoolean("onlyUsers", false);
            this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
            this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
            this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
            this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.allowSelf = this.arguments.getBoolean("allowSelf", true);
            this.channelId = this.arguments.getLong("channelId", 0L);
            this.needFinishFragment = this.arguments.getBoolean("needFinishFragment", true);
            this.chatId = this.arguments.getLong(ChatReactionsEditActivity.KEY_CHAT_ID, 0L);
            this.disableSections = this.arguments.getBoolean("disableSections", false);
            this.resetDelegate = this.arguments.getBoolean("resetDelegate", false);
        } else {
            this.needPhonebook = true;
        }
        if (!this.createSecretChat && !this.returnAsResult) {
            this.sortByName = SharedConfig.sortContactsByName;
        }
        getContactsController().checkInviteText();
        getContactsController().reloadContactsStatusesMaybe();
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        this.delegate = null;
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        getNotificationCenter().onAnimationFinish(this.animationIndex);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationProgress(boolean isOpen, float progress) {
        super.onTransitionAnimationProgress(isOpen, progress);
        if (this.fragmentView != null) {
            this.fragmentView.invalidate();
        }
    }

    /* JADX WARN: Type inference failed for: r0v18, types: [java.lang.Throwable] */
    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        final int inviteViaLink;
        int i;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.destroyAfterSelect) {
            if (this.returnAsResult) {
                this.actionBar.setTitle(LocaleController.getString("SelectContact", R.string.SelectContact));
            } else if (this.createSecretChat) {
                this.actionBar.setTitle(LocaleController.getString("NewSecretChat", R.string.NewSecretChat));
            } else {
                this.actionBar.setTitle(LocaleController.getString("NewMessageTitle", R.string.NewMessageTitle));
            }
        } else {
            this.actionBar.setTitle(LocaleController.getString("Contacts", R.string.Contacts));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ContactsActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    ContactsActivity.this.finishFragment();
                    return;
                }
                int i2 = 1;
                if (id == 1) {
                    SharedConfig.toggleSortContactsByName();
                    ContactsActivity.this.sortByName = SharedConfig.sortContactsByName;
                    ContactsAdapter contactsAdapter = ContactsActivity.this.listViewAdapter;
                    if (!ContactsActivity.this.sortByName) {
                        i2 = 2;
                    }
                    contactsAdapter.setSortType(i2, false);
                    ContactsActivity.this.sortItem.setIcon(ContactsActivity.this.sortByName ? R.drawable.msg_contacts_time : R.drawable.msg_contacts_name);
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem item = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.ContactsActivity.2
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                ContactsActivity.this.searching = true;
                if (ContactsActivity.this.floatingButtonContainer != null) {
                    ContactsActivity.this.floatingButtonContainer.setVisibility(8);
                }
                if (ContactsActivity.this.sortItem != null) {
                    ContactsActivity.this.sortItem.setVisibility(8);
                }
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                ContactsActivity.this.searchListViewAdapter.searchDialogs(null);
                ContactsActivity.this.searching = false;
                ContactsActivity.this.searchWas = false;
                ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
                ContactsActivity.this.listView.setSectionsType(1);
                ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
                ContactsActivity.this.listView.setFastScrollVisible(true);
                ContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
                if (ContactsActivity.this.floatingButtonContainer != null) {
                    ContactsActivity.this.floatingButtonContainer.setVisibility(0);
                    ContactsActivity.this.floatingHidden = true;
                    ContactsActivity.this.floatingButtonContainer.setTranslationY(AndroidUtilities.dp(100.0f));
                    ContactsActivity.this.hideFloatingButton(false);
                }
                if (ContactsActivity.this.sortItem != null) {
                    ContactsActivity.this.sortItem.setVisibility(0);
                }
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                if (ContactsActivity.this.searchListViewAdapter == null) {
                    return;
                }
                String text = editText.getText().toString();
                if (text.length() != 0) {
                    ContactsActivity.this.searchWas = true;
                    if (ContactsActivity.this.listView != null) {
                        ContactsActivity.this.listView.setAdapter(ContactsActivity.this.searchListViewAdapter);
                        ContactsActivity.this.listView.setSectionsType(0);
                        ContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                        ContactsActivity.this.listView.setFastScrollVisible(false);
                        ContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                    }
                    ContactsActivity.this.emptyView.showProgress(true, true);
                    ContactsActivity.this.searchListViewAdapter.searchDialogs(text);
                } else if (ContactsActivity.this.listView != null) {
                    ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
                    ContactsActivity.this.listView.setSectionsType(1);
                }
            }
        });
        item.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        item.setContentDescription(LocaleController.getString("Search", R.string.Search));
        if (!this.createSecretChat && !this.returnAsResult) {
            ActionBarMenuItem addItem = menu.addItem(1, this.sortByName ? R.drawable.msg_contacts_time : R.drawable.msg_contacts_name);
            this.sortItem = addItem;
            addItem.setContentDescription(LocaleController.getString("AccDescrContactSorting", R.string.AccDescrContactSorting));
        }
        this.searchListViewAdapter = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots, this.allowSelf, true, 0) { // from class: org.telegram.ui.ContactsActivity.3
            @Override // org.telegram.ui.Adapters.SearchAdapter
            protected void onSearchProgressChanged() {
                if (!searchInProgress() && getItemCount() == 0) {
                    ContactsActivity.this.emptyView.showProgress(false, true);
                }
                ContactsActivity.this.showItemsAnimated();
            }
        };
        int i2 = 3;
        if (this.chatId == 0) {
            if (this.channelId != 0) {
                TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.channelId));
                inviteViaLink = (!ChatObject.canUserDoAdminAction(chat, 3) || !TextUtils.isEmpty(chat.username)) ? 0 : 2;
            } else {
                inviteViaLink = 0;
            }
        } else {
            inviteViaLink = ChatObject.canUserDoAdminAction(getMessagesController().getChat(Long.valueOf(this.chatId)), 3) ? 1 : 0;
        }
        try {
            this.hasGps = ApplicationLoader.applicationContext.getPackageManager().hasSystemFeature("android.hardware.location.gps");
        } catch (Throwable th) {
            this.hasGps = false;
        }
        ContactsAdapter contactsAdapter = new ContactsAdapter(context, this.onlyUsers ? 1 : 0, this.needPhonebook, this.ignoreUsers, inviteViaLink, this.hasGps) { // from class: org.telegram.ui.ContactsActivity.4
            @Override // org.telegram.ui.Components.RecyclerListView.SectionsAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (ContactsActivity.this.listView != null && ContactsActivity.this.listView.getAdapter() == this) {
                    int count = super.getItemCount();
                    boolean z = true;
                    if (ContactsActivity.this.needPhonebook) {
                        RecyclerListView recyclerListView = ContactsActivity.this.listView;
                        if (count == 2) {
                            z = false;
                        }
                        recyclerListView.setFastScrollVisible(z);
                        return;
                    }
                    RecyclerListView recyclerListView2 = ContactsActivity.this.listView;
                    if (count == 0) {
                        z = false;
                    }
                    recyclerListView2.setFastScrollVisible(z);
                }
            }
        };
        this.listViewAdapter = contactsAdapter;
        if (this.sortItem != null) {
            i = this.sortByName ? 1 : 2;
        } else {
            i = 0;
        }
        contactsAdapter.setSortType(i, false);
        this.listViewAdapter.setDisableSections(this.disableSections);
        this.fragmentView = new FrameLayout(context) { // from class: org.telegram.ui.ContactsActivity.5
            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (ContactsActivity.this.listView.getAdapter() == ContactsActivity.this.listViewAdapter) {
                    if (ContactsActivity.this.emptyView.getVisibility() == 0) {
                        ContactsActivity.this.emptyView.setTranslationY(AndroidUtilities.dp(74.0f));
                        return;
                    }
                    return;
                }
                ContactsActivity.this.emptyView.setTranslationY(AndroidUtilities.dp(0.0f));
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        flickerLoadingView.setViewType(6);
        flickerLoadingView.showDate(false);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.addView(flickerLoadingView, 0);
        this.emptyView.setAnimateLayoutChange(true);
        this.emptyView.showProgress(true, false);
        this.emptyView.title.setText(LocaleController.getString("NoResult", R.string.NoResult));
        this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", R.string.SearchEmptyViewFilteredSubtitle2));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.ContactsActivity.6
            @Override // android.view.View
            public void setPadding(int left, int top, int right, int bottom) {
                super.setPadding(left, top, right, bottom);
                if (ContactsActivity.this.emptyView != null) {
                    ContactsActivity.this.emptyView.setPadding(left, top, right, bottom);
                }
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setSectionsType(1);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setFastScrollEnabled(0);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        this.listView.setAdapter(this.listViewAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(true, 0);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i3) {
                ContactsActivity.this.m3281lambda$createView$1$orgtelegramuiContactsActivity(inviteViaLink, view, i3);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ContactsActivity.7
            private boolean scrollingManually;

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    if (ContactsActivity.this.searching && ContactsActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(ContactsActivity.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean goingDown;
                super.onScrolled(recyclerView, dx, dy);
                if (ContactsActivity.this.floatingButtonContainer != null && ContactsActivity.this.floatingButtonContainer.getVisibility() != 8) {
                    int firstVisibleItem = ContactsActivity.this.layoutManager.findFirstVisibleItemPosition();
                    boolean z = false;
                    View topChild = recyclerView.getChildAt(0);
                    int firstViewTop = 0;
                    if (topChild != null) {
                        firstViewTop = topChild.getTop();
                    }
                    boolean changed = true;
                    if (ContactsActivity.this.prevPosition == firstVisibleItem) {
                        int topDelta = ContactsActivity.this.prevTop - firstViewTop;
                        goingDown = firstViewTop < ContactsActivity.this.prevTop;
                        if (Math.abs(topDelta) > 1) {
                            z = true;
                        }
                        changed = z;
                    } else {
                        if (firstVisibleItem > ContactsActivity.this.prevPosition) {
                            z = true;
                        }
                        goingDown = z;
                    }
                    if (changed && ContactsActivity.this.scrollUpdated && (goingDown || this.scrollingManually)) {
                        ContactsActivity.this.hideFloatingButton(goingDown);
                    }
                    ContactsActivity.this.prevPosition = firstVisibleItem;
                    ContactsActivity.this.prevTop = firstViewTop;
                    ContactsActivity.this.scrollUpdated = true;
                }
            }
        });
        if (!this.createSecretChat && !this.returnAsResult) {
            FrameLayout frameLayout2 = new FrameLayout(context);
            this.floatingButtonContainer = frameLayout2;
            int i3 = (Build.VERSION.SDK_INT >= 21 ? 56 : 60) + 20;
            float f = (Build.VERSION.SDK_INT >= 21 ? 56 : 60) + 20;
            if (!LocaleController.isRTL) {
                i2 = 5;
            }
            frameLayout.addView(frameLayout2, LayoutHelper.createFrame(i3, f, i2 | 80, LocaleController.isRTL ? 4.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 4.0f, 0.0f));
            this.floatingButtonContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ContactsActivity.this.m3282lambda$createView$2$orgtelegramuiContactsActivity(view);
                }
            });
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.floatingButton = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
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
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            boolean configAnimationsEnabled = preferences.getBoolean("view_animations", true);
            this.floatingButton.setAnimation(configAnimationsEnabled ? R.raw.write_contacts_fab_icon : R.raw.write_contacts_fab_icon_reverse, 52, 52);
            this.floatingButtonContainer.setContentDescription(LocaleController.getString("CreateNewContact", R.string.CreateNewContact));
            if (Build.VERSION.SDK_INT >= 21) {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
                this.floatingButton.setStateListAnimator(animator);
                this.floatingButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.ContactsActivity.8
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.floatingButtonContainer.addView(this.floatingButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56 : 60, 51, 10.0f, 6.0f, 10.0f, 0.0f));
        }
        if (this.initialSearchString != null) {
            this.actionBar.openSearchField(this.initialSearchString, false);
            this.initialSearchString = null;
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ContactsActivity */
    public /* synthetic */ void m3281lambda$createView$1$orgtelegramuiContactsActivity(int inviteViaLink, View view, int position) {
        Activity activity;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchListViewAdapter;
        boolean z = false;
        if (adapter == searchAdapter) {
            Object object = searchAdapter.getItem(position);
            if (object instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) object;
                if (this.searchListViewAdapter.isGlobalSearch(position)) {
                    ArrayList<TLRPC.User> users = new ArrayList<>();
                    users.add(user);
                    getMessagesController().putUsers(users, false);
                    MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, null, false, true);
                }
                if (this.returnAsResult) {
                    LongSparseArray<TLRPC.User> longSparseArray = this.ignoreUsers;
                    if (longSparseArray != null && longSparseArray.indexOfKey(user.id) >= 0) {
                        return;
                    }
                    didSelectResult(user, true, null);
                    return;
                } else if (this.createSecretChat) {
                    if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        return;
                    }
                    this.creatingChat = true;
                    SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user);
                    return;
                } else {
                    Bundle args = new Bundle();
                    args.putLong("user_id", user.id);
                    if (getMessagesController().checkCanOpenChat(args, this)) {
                        presentFragment(new ChatActivity(args), true);
                        return;
                    }
                    return;
                }
            } else if (object instanceof String) {
                String str = (String) object;
                if (!str.equals("section")) {
                    NewContactActivity activity2 = new NewContactActivity();
                    activity2.setInitialPhoneNumber(str, true);
                    presentFragment(activity2);
                    return;
                }
                return;
            } else {
                return;
            }
        }
        int section = this.listViewAdapter.getSectionForPosition(position);
        int row = this.listViewAdapter.getPositionInSectionForPosition(position);
        if (row < 0 || section < 0) {
            return;
        }
        if ((!this.onlyUsers || inviteViaLink != 0) && section == 0) {
            if (this.needPhonebook) {
                if (row == 0) {
                    presentFragment(new InviteContactsActivity());
                    return;
                } else if (row == 1 && this.hasGps) {
                    if (Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null && activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                        presentFragment(new ActionIntroActivity(1));
                        return;
                    }
                    boolean enabled = true;
                    if (Build.VERSION.SDK_INT >= 28) {
                        LocationManager lm = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
                        enabled = lm.isLocationEnabled();
                    } else if (Build.VERSION.SDK_INT >= 19) {
                        try {
                            int mode = Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0);
                            if (mode != 0) {
                                z = true;
                            }
                            enabled = z;
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                    if (!enabled) {
                        presentFragment(new ActionIntroActivity(4));
                        return;
                    } else {
                        presentFragment(new PeopleNearbyActivity());
                        return;
                    }
                } else {
                    return;
                }
            } else if (inviteViaLink != 0) {
                if (row == 0) {
                    long j = this.chatId;
                    if (j == 0) {
                        j = this.channelId;
                    }
                    presentFragment(new GroupInviteActivity(j));
                    return;
                }
                return;
            } else if (row == 0) {
                presentFragment(new GroupCreateActivity(new Bundle()), false);
                return;
            } else if (row == 1) {
                Bundle args2 = new Bundle();
                args2.putBoolean("onlyUsers", true);
                args2.putBoolean("destroyAfterSelect", true);
                args2.putBoolean("createSecretChat", true);
                args2.putBoolean("allowBots", false);
                args2.putBoolean("allowSelf", false);
                presentFragment(new ContactsActivity(args2), false);
                return;
            } else if (row == 2) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (!BuildVars.DEBUG_VERSION && preferences.getBoolean("channel_intro", false)) {
                    Bundle args3 = new Bundle();
                    args3.putInt("step", 0);
                    presentFragment(new ChannelCreateActivity(args3));
                    return;
                }
                presentFragment(new ActionIntroActivity(0));
                preferences.edit().putBoolean("channel_intro", true).commit();
                return;
            } else {
                return;
            }
        }
        Object item1 = this.listViewAdapter.getItem(section, row);
        if (item1 instanceof TLRPC.User) {
            TLRPC.User user2 = (TLRPC.User) item1;
            if (this.returnAsResult) {
                LongSparseArray<TLRPC.User> longSparseArray2 = this.ignoreUsers;
                if (longSparseArray2 != null && longSparseArray2.indexOfKey(user2.id) >= 0) {
                    return;
                }
                didSelectResult(user2, true, null);
            } else if (this.createSecretChat) {
                this.creatingChat = true;
                SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user2);
            } else {
                Bundle args4 = new Bundle();
                args4.putLong("user_id", user2.id);
                if (getMessagesController().checkCanOpenChat(args4, this)) {
                    presentFragment(new ChatActivity(args4), true);
                }
            }
        } else if (item1 instanceof ContactsController.Contact) {
            ContactsController.Contact contact = (ContactsController.Contact) item1;
            String usePhone = null;
            if (!contact.phones.isEmpty()) {
                String usePhone2 = contact.phones.get(0);
                usePhone = usePhone2;
            }
            if (usePhone == null || getParentActivity() == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setMessage(LocaleController.getString("InviteUser", R.string.InviteUser));
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            final String arg1 = usePhone;
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda1
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ContactsActivity.this.m3280lambda$createView$0$orgtelegramuiContactsActivity(arg1, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ContactsActivity */
    public /* synthetic */ void m3280lambda$createView$0$orgtelegramuiContactsActivity(String arg1, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", arg1, null));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(1));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ContactsActivity */
    public /* synthetic */ void m3282lambda$createView$2$orgtelegramuiContactsActivity(View v) {
        presentFragment(new NewContactActivity());
    }

    private void didSelectResult(final TLRPC.User user, boolean useAlert, final String param) {
        if (useAlert && this.selectAlertString != null) {
            if (getParentActivity() == null) {
                return;
            }
            if (user.bot) {
                if (user.bot_nochats) {
                    try {
                        BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("BotCantJoinGroups", R.string.BotCantJoinGroups)).show();
                        return;
                    } catch (Exception e) {
                        FileLog.e(e);
                        return;
                    }
                } else if (this.channelId != 0) {
                    TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.channelId));
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    if (ChatObject.canAddAdmins(chat)) {
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("AddBotAsAdmin", R.string.AddBotAsAdmin));
                        builder.setPositiveButton(LocaleController.getString("MakeAdmin", R.string.MakeAdmin), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda3
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                ContactsActivity.this.m3283lambda$didSelectResult$3$orgtelegramuiContactsActivity(user, param, dialogInterface, i);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    } else {
                        builder.setMessage(LocaleController.getString("CantAddBotAsAdmin", R.string.CantAddBotAsAdmin));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    }
                    showDialog(builder.create());
                    return;
                }
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
            String message = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user));
            final EditTextBoldCursor editText = null;
            if (!user.bot && this.needForwardCount) {
                message = String.format("%s\n\n%s", message, LocaleController.getString("AddToTheGroupForwardCount", R.string.AddToTheGroupForwardCount));
                editText = new EditTextBoldCursor(getParentActivity());
                editText.setTextSize(1, 18.0f);
                editText.setText("50");
                editText.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                editText.setGravity(17);
                editText.setInputType(2);
                editText.setImeOptions(6);
                editText.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
                editText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ContactsActivity.9
                    @Override // android.text.TextWatcher
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override // android.text.TextWatcher
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override // android.text.TextWatcher
                    public void afterTextChanged(Editable s) {
                        try {
                            String str = s.toString();
                            if (str.length() != 0) {
                                int value = Utilities.parseInt((CharSequence) str).intValue();
                                if (value < 0) {
                                    editText.setText("0");
                                    EditText editText2 = editText;
                                    editText2.setSelection(editText2.length());
                                } else if (value > 300) {
                                    editText.setText("300");
                                    EditText editText3 = editText;
                                    editText3.setSelection(editText3.length());
                                } else {
                                    if (!str.equals("" + value)) {
                                        EditText editText4 = editText;
                                        editText4.setText("" + value);
                                        EditText editText5 = editText;
                                        editText5.setSelection(editText5.length());
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    }
                });
                builder2.setView(editText);
            }
            builder2.setMessage(message);
            final EditText finalEditText = editText;
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ContactsActivity.this.m3284lambda$didSelectResult$4$orgtelegramuiContactsActivity(user, finalEditText, dialogInterface, i);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder2.create());
            if (editText != null) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
                if (layoutParams != null) {
                    if (layoutParams instanceof FrameLayout.LayoutParams) {
                        ((FrameLayout.LayoutParams) layoutParams).gravity = 1;
                    }
                    int dp = AndroidUtilities.dp(24.0f);
                    layoutParams.leftMargin = dp;
                    layoutParams.rightMargin = dp;
                    layoutParams.height = AndroidUtilities.dp(36.0f);
                    editText.setLayoutParams(layoutParams);
                }
                editText.setSelection(editText.getText().length());
                return;
            }
            return;
        }
        ContactsActivityDelegate contactsActivityDelegate = this.delegate;
        if (contactsActivityDelegate != null) {
            contactsActivityDelegate.didSelectContact(user, param, this);
            if (this.resetDelegate) {
                this.delegate = null;
            }
        }
        if (this.needFinishFragment) {
            finishFragment();
        }
    }

    /* renamed from: lambda$didSelectResult$3$org-telegram-ui-ContactsActivity */
    public /* synthetic */ void m3283lambda$didSelectResult$3$orgtelegramuiContactsActivity(TLRPC.User user, String param, DialogInterface dialogInterface, int i) {
        ContactsActivityDelegate contactsActivityDelegate = this.delegate;
        if (contactsActivityDelegate != null) {
            contactsActivityDelegate.didSelectContact(user, param, this);
            this.delegate = null;
        }
    }

    /* renamed from: lambda$didSelectResult$4$org-telegram-ui-ContactsActivity */
    public /* synthetic */ void m3284lambda$didSelectResult$4$orgtelegramuiContactsActivity(TLRPC.User user, EditText finalEditText, DialogInterface dialogInterface, int i) {
        didSelectResult(user, false, finalEditText != null ? finalEditText.getText().toString() : "0");
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        Activity activity;
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ContactsAdapter contactsAdapter = this.listViewAdapter;
        if (contactsAdapter != null) {
            contactsAdapter.notifyDataSetChanged();
        }
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null) {
            this.checkPermission = false;
            if (activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
                if (activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                    AlertDialog.Builder builder = AlertsCreator.createContactsPermissionDialog(activity, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda7
                        @Override // org.telegram.messenger.MessagesStorage.IntCallback
                        public final void run(int i) {
                            ContactsActivity.this.m3287lambda$onResume$5$orgtelegramuiContactsActivity(i);
                        }
                    });
                    AlertDialog create = builder.create();
                    this.permissionDialog = create;
                    showDialog(create);
                    return;
                }
                askForPermissons(true);
            }
        }
    }

    /* renamed from: lambda$onResume$5$org-telegram-ui-ContactsActivity */
    public /* synthetic */ void m3287lambda$onResume$5$orgtelegramuiContactsActivity(int param) {
        this.askAboutContacts = param != 0;
        if (param == 0) {
            return;
        }
        askForPermissons(false);
    }

    public RecyclerListView getListView() {
        return this.listView;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: org.telegram.ui.ContactsActivity.10
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    ContactsActivity.this.floatingButtonContainer.setTranslationY(ContactsActivity.this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0);
                    ContactsActivity.this.floatingButtonContainer.setClickable(!ContactsActivity.this.floatingHidden);
                    if (ContactsActivity.this.floatingButtonContainer != null) {
                        ContactsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        AlertDialog alertDialog = this.permissionDialog;
        if (alertDialog != null && dialog == alertDialog && getParentActivity() != null && this.askAboutContacts) {
            askForPermissons(false);
        }
    }

    private void askForPermissons(boolean alert) {
        Activity activity = getParentActivity();
        if (activity == null || !UserConfig.getInstance(this.currentAccount).syncContacts || activity.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
            return;
        }
        if (alert && this.askAboutContacts) {
            AlertDialog.Builder builder = AlertsCreator.createContactsPermissionDialog(activity, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda6
                @Override // org.telegram.messenger.MessagesStorage.IntCallback
                public final void run(int i) {
                    ContactsActivity.this.m3279lambda$askForPermissons$6$orgtelegramuiContactsActivity(i);
                }
            });
            showDialog(builder.create());
            return;
        }
        this.permissionRequestTime = SystemClock.elapsedRealtime();
        ArrayList<String> permissons = new ArrayList<>();
        permissons.add("android.permission.READ_CONTACTS");
        permissons.add("android.permission.WRITE_CONTACTS");
        permissons.add("android.permission.GET_ACCOUNTS");
        String[] items = (String[]) permissons.toArray(new String[0]);
        try {
            activity.requestPermissions(items, 1);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* renamed from: lambda$askForPermissons$6$org-telegram-ui-ContactsActivity */
    public /* synthetic */ void m3279lambda$askForPermissons$6$orgtelegramuiContactsActivity(int param) {
        this.askAboutContacts = param != 0;
        if (param == 0) {
            return;
        }
        askForPermissons(false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length > a && "android.permission.READ_CONTACTS".equals(permissions[a])) {
                    if (grantResults[a] == 0) {
                        ContactsController.getInstance(this.currentAccount).forceImportContacts();
                        return;
                    } else {
                        SharedPreferences.Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                        this.askAboutContacts = false;
                        edit.putBoolean("askAboutContacts", false).commit();
                        if (SystemClock.elapsedRealtime() - this.permissionRequestTime < 200) {
                            try {
                                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                                Uri uri = Uri.fromParts("package", ApplicationLoader.applicationContext.getPackageName(), null);
                                intent.setData(uri);
                                getParentActivity().startActivity(intent);
                                return;
                            } catch (Exception e) {
                                FileLog.e(e);
                                return;
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        if (this.actionBar != null) {
            this.actionBar.closeSearchField();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        ContactsAdapter contactsAdapter;
        if (id == NotificationCenter.contactsDidLoad) {
            ContactsAdapter contactsAdapter2 = this.listViewAdapter;
            if (contactsAdapter2 != null) {
                if (!this.sortByName) {
                    contactsAdapter2.setSortType(2, true);
                }
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if ((MessagesController.UPDATE_MASK_AVATAR & mask) != 0 || (MessagesController.UPDATE_MASK_NAME & mask) != 0 || (MessagesController.UPDATE_MASK_STATUS & mask) != 0) {
                updateVisibleRows(mask);
            }
            if ((MessagesController.UPDATE_MASK_STATUS & mask) != 0 && !this.sortByName && (contactsAdapter = this.listViewAdapter) != null) {
                contactsAdapter.sortOnlineContacts();
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.createSecretChat && this.creatingChat) {
                TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) args[0];
                Bundle args2 = new Bundle();
                args2.putInt("enc_id", encryptedChat.id);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args2), true);
            }
        } else if (id == NotificationCenter.closeChats && !this.creatingChat) {
            removeSelfFromStack();
        }
    }

    private void updateVisibleRows(int mask) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                }
            }
        }
    }

    public void hideFloatingButton(boolean hide) {
        if (this.floatingHidden == hide) {
            return;
        }
        this.floatingHidden = hide;
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[1];
        FrameLayout frameLayout = this.floatingButtonContainer;
        Property property = View.TRANSLATION_Y;
        float[] fArr = new float[1];
        fArr[0] = this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0;
        animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
        animatorSet.playTogether(animatorArr);
        animatorSet.setDuration(300L);
        animatorSet.setInterpolator(this.floatingInterpolator);
        this.floatingButtonContainer.setClickable(!hide);
        animatorSet.start();
    }

    public void setDelegate(ContactsActivityDelegate delegate) {
        this.delegate = delegate;
    }

    public void setIgnoreUsers(LongSparseArray<TLRPC.User> users) {
        this.ignoreUsers = users;
    }

    public void setInitialSearchString(String initialSearchString) {
        this.initialSearchString = initialSearchString;
    }

    public void showItemsAnimated() {
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        final int from = linearLayoutManager == null ? 0 : linearLayoutManager.findLastVisibleItemPosition();
        this.listView.invalidate();
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ContactsActivity.11
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                ContactsActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int n = ContactsActivity.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i = 0; i < n; i++) {
                    View child = ContactsActivity.this.listView.getChildAt(i);
                    if (ContactsActivity.this.listView.getChildAdapterPosition(child) > from) {
                        child.setAlpha(0.0f);
                        int s = Math.min(ContactsActivity.this.listView.getMeasuredHeight(), Math.max(0, child.getTop()));
                        int delay = (int) ((s / ContactsActivity.this.listView.getMeasuredHeight()) * 100.0f);
                        ObjectAnimator a = ObjectAnimator.ofFloat(child, View.ALPHA, 0.0f, 1.0f);
                        a.setStartDelay(delay);
                        a.setDuration(200L);
                        animatorSet.playTogether(a);
                    }
                }
                animatorSet.start();
                return true;
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public AnimatorSet onCustomTransitionAnimation(final boolean isOpen, final Runnable callback) {
        ValueAnimator valueAnimator;
        DialogsActivity dialogsActivity;
        float[] fArr = {0.0f, 1.0f};
        if (isOpen) {
            // fill-array-data instruction
            fArr[0] = 1.0f;
            fArr[1] = 0.0f;
            valueAnimator = ValueAnimator.ofFloat(fArr);
        } else {
            valueAnimator = ValueAnimator.ofFloat(fArr);
        }
        final ValueAnimator valueAnimator2 = valueAnimator;
        final ViewGroup parent = (ViewGroup) this.fragmentView.getParent();
        BaseFragment previousFragment = this.parentLayout.fragmentsStack.size() > 1 ? this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2) : null;
        if (!(previousFragment instanceof DialogsActivity)) {
            dialogsActivity = null;
        } else {
            dialogsActivity = (DialogsActivity) previousFragment;
        }
        if (dialogsActivity == null) {
            return null;
        }
        final RLottieImageView previousFab = dialogsActivity.getFloatingButton();
        final View previousFabContainer = previousFab.getParent() != null ? (View) previousFab.getParent() : null;
        if (this.floatingButtonContainer == null || previousFabContainer == null || previousFab.getVisibility() != 0 || Math.abs(previousFabContainer.getTranslationY()) > AndroidUtilities.dp(4.0f) || Math.abs(this.floatingButtonContainer.getTranslationY()) > AndroidUtilities.dp(4.0f)) {
            return null;
        }
        previousFabContainer.setVisibility(8);
        if (isOpen) {
            parent.setAlpha(0.0f);
        }
        valueAnimator2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                ContactsActivity.lambda$onCustomTransitionAnimation$7(valueAnimator2, parent, valueAnimator3);
            }
        });
        if (this.floatingButtonContainer != null) {
            ((ViewGroup) this.fragmentView).removeView(this.floatingButtonContainer);
            ((FrameLayout) parent.getParent()).addView(this.floatingButtonContainer);
        }
        valueAnimator2.setDuration(150L);
        valueAnimator2.setInterpolator(new DecelerateInterpolator(1.5f));
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ContactsActivity.12
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ContactsActivity.this.floatingButtonContainer != null) {
                    if (ContactsActivity.this.floatingButtonContainer.getParent() instanceof ViewGroup) {
                        ViewGroup viewParent = (ViewGroup) ContactsActivity.this.floatingButtonContainer.getParent();
                        viewParent.removeView(ContactsActivity.this.floatingButtonContainer);
                    }
                    ((ViewGroup) ContactsActivity.this.fragmentView).addView(ContactsActivity.this.floatingButtonContainer);
                    previousFabContainer.setVisibility(0);
                    if (!isOpen) {
                        previousFab.setAnimation(R.raw.write_contacts_fab_icon_reverse, 52, 52);
                        previousFab.getAnimatedDrawable().setCurrentFrame(ContactsActivity.this.floatingButton.getAnimatedDrawable().getCurrentFrame());
                        previousFab.playAnimation();
                    }
                }
                callback.run();
            }
        });
        animatorSet.playTogether(valueAnimator2);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                ContactsActivity.this.m3286xa0ce3b06(animatorSet, isOpen, previousFabContainer);
            }
        }, 50L);
        return animatorSet;
    }

    public static /* synthetic */ void lambda$onCustomTransitionAnimation$7(ValueAnimator valueAnimator, ViewGroup parent, ValueAnimator valueAnimator1) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        parent.setTranslationX(AndroidUtilities.dp(48.0f) * v);
        parent.setAlpha(1.0f - v);
    }

    /* renamed from: lambda$onCustomTransitionAnimation$8$org-telegram-ui-ContactsActivity */
    public /* synthetic */ void m3286xa0ce3b06(AnimatorSet animatorSet, boolean isOpen, final View previousFabContainer) {
        this.animationIndex = getNotificationCenter().setAnimationInProgress(this.animationIndex, new int[]{NotificationCenter.diceStickersDidLoad}, false);
        animatorSet.start();
        if (isOpen) {
            this.floatingButton.setAnimation(R.raw.write_contacts_fab_icon, 52, 52);
            this.floatingButton.playAnimation();
        } else {
            this.floatingButton.setAnimation(R.raw.write_contacts_fab_icon_reverse, 52, 52);
            this.floatingButton.playAnimation();
        }
        AnimatorSet animatorSet2 = this.bounceIconAnimator;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.bounceIconAnimator = new AnimatorSet();
        float totalDuration = (float) this.floatingButton.getAnimatedDrawable().getDuration();
        long delay = 0;
        int i = 4;
        if (isOpen) {
            for (int i2 = 0; i2 < 6; i2++) {
                AnimatorSet set = new AnimatorSet();
                if (i2 == 0) {
                    set.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.0f, 0.9f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.0f, 0.9f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_X, 1.0f, 0.9f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_Y, 1.0f, 0.9f));
                    set.setDuration(0.12765957f * totalDuration);
                    set.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                } else if (i2 == 1) {
                    set.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 0.9f, 1.06f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 0.9f, 1.06f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_X, 0.9f, 1.06f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_Y, 0.9f, 1.06f));
                    set.setDuration(0.3617021f * totalDuration);
                    set.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i2 == 2) {
                    set.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.06f, 0.9f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.06f, 0.9f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_X, 1.06f, 0.9f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_Y, 1.06f, 0.9f));
                    set.setDuration(0.21276596f * totalDuration);
                    set.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i2 == 3) {
                    set.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 0.9f, 1.03f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 0.9f, 1.03f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_X, 0.9f, 1.03f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_Y, 0.9f, 1.03f));
                    set.setDuration(totalDuration * 0.10638298f);
                    set.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i2 == 4) {
                    set.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.03f, 0.98f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.03f, 0.98f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_X, 1.03f, 0.98f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_Y, 1.03f, 0.98f));
                    set.setDuration(totalDuration * 0.10638298f);
                    set.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else {
                    set.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 0.98f, 1.0f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 0.98f, 1.0f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_X, 0.98f, 1.0f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_Y, 0.98f, 1.0f));
                    set.setDuration(0.08510638f * totalDuration);
                    set.setInterpolator(CubicBezierInterpolator.EASE_IN);
                }
                set.setStartDelay(delay);
                delay += set.getDuration();
                this.bounceIconAnimator.playTogether(set);
            }
        } else {
            for (int i3 = 0; i3 < 5; i3++) {
                AnimatorSet set2 = new AnimatorSet();
                if (i3 == 0) {
                    Animator[] animatorArr = new Animator[i];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.0f, 0.9f);
                    animatorArr[1] = ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.0f, 0.9f);
                    animatorArr[2] = ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_X, 1.0f, 0.9f);
                    animatorArr[3] = ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_Y, 1.0f, 0.9f);
                    set2.playTogether(animatorArr);
                    set2.setDuration(0.19444445f * totalDuration);
                    set2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    i = 4;
                } else if (i3 == 1) {
                    set2.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 0.9f, 1.06f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 0.9f, 1.06f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_X, 0.9f, 1.06f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_Y, 0.9f, 1.06f));
                    set2.setDuration(0.22222222f * totalDuration);
                    set2.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                    i = 4;
                } else if (i3 == 2) {
                    set2.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.06f, 0.92f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.06f, 0.92f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_X, 1.06f, 0.92f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_Y, 1.06f, 0.92f));
                    set2.setDuration(0.19444445f * totalDuration);
                    set2.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                    i = 4;
                } else if (i3 == 3) {
                    set2.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 0.92f, 1.02f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 0.92f, 1.02f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_X, 0.92f, 1.02f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_Y, 0.92f, 1.02f));
                    set2.setDuration(0.25f * totalDuration);
                    set2.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                    i = 4;
                } else {
                    i = 4;
                    set2.playTogether(ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, 1.02f, 1.0f), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, 1.02f, 1.0f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_X, 1.02f, 1.0f), ObjectAnimator.ofFloat(previousFabContainer, View.SCALE_Y, 1.02f, 1.0f));
                    set2.setDuration(totalDuration * 0.10638298f);
                    set2.setInterpolator(CubicBezierInterpolator.EASE_IN);
                }
                set2.setStartDelay(delay);
                delay += set2.getDuration();
                this.bounceIconAnimator.playTogether(set2);
            }
        }
        this.bounceIconAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ContactsActivity.13
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ContactsActivity.this.floatingButton.setScaleX(1.0f);
                ContactsActivity.this.floatingButton.setScaleY(1.0f);
                previousFabContainer.setScaleX(1.0f);
                previousFabContainer.setScaleY(1.0f);
                ContactsActivity.this.bounceIconAnimator = null;
                ContactsActivity.this.getNotificationCenter().onAnimationFinish(ContactsActivity.this.animationIndex);
            }
        });
        this.bounceIconAnimator.start();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ContactsActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ContactsActivity.this.m3285lambda$getThemeDescriptions$9$orgtelegramuiContactsActivity();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, Theme.key_windowBackgroundWhiteBlueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        themeDescriptions.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon));
        themeDescriptions.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground));
        themeDescriptions.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, Theme.key_windowBackgroundWhiteBlueText3));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint[0], Theme.dialogs_namePaint[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_name));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint[0], Theme.dialogs_nameEncryptedPaint[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_secretName));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$9$org-telegram-ui-ContactsActivity */
    public /* synthetic */ void m3285lambda$getThemeDescriptions$9$orgtelegramuiContactsActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                } else if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(0);
                }
            }
        }
    }
}
