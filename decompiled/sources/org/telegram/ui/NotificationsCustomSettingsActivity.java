package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.upstream.cache.ContentMetadata;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextColorCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.NotificationsCustomSettingsActivity;
import org.telegram.ui.NotificationsSettingsActivity;
import org.telegram.ui.ProfileNotificationsActivity;
/* loaded from: classes4.dex */
public class NotificationsCustomSettingsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int search_button = 0;
    private ListAdapter adapter;
    private int alertRow;
    private int alertSection2Row;
    private AnimatorSet animatorSet;
    private int currentType;
    private int deleteAllRow;
    private int deleteAllSectionRow;
    private EmptyTextProgressView emptyView;
    private ArrayList<NotificationsSettingsActivity.NotificationException> exceptions;
    private int exceptionsAddRow;
    private HashMap<Long, NotificationsSettingsActivity.NotificationException> exceptionsDict;
    private int exceptionsEndRow;
    private int exceptionsSection2Row;
    private int exceptionsStartRow;
    private int groupSection2Row;
    private RecyclerListView listView;
    private int messageLedRow;
    private int messagePopupNotificationRow;
    private int messagePriorityRow;
    private int messageSectionRow;
    private int messageSoundRow;
    private int messageVibrateRow;
    private int previewRow;
    private int rowCount;
    private SearchAdapter searchAdapter;
    private boolean searchWas;
    private boolean searching;

    public NotificationsCustomSettingsActivity(int type, ArrayList<NotificationsSettingsActivity.NotificationException> notificationExceptions) {
        this(type, notificationExceptions, false);
    }

    public NotificationsCustomSettingsActivity(int type, ArrayList<NotificationsSettingsActivity.NotificationException> notificationExceptions, boolean load) {
        this.rowCount = 0;
        this.exceptionsDict = new HashMap<>();
        this.currentType = type;
        this.exceptions = notificationExceptions;
        int N = notificationExceptions.size();
        for (int a = 0; a < N; a++) {
            NotificationsSettingsActivity.NotificationException exception = this.exceptions.get(a);
            this.exceptionsDict.put(Long.valueOf(exception.did), exception);
        }
        if (load) {
            loadExceptions();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        updateRows(true);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (this.currentType == -1) {
            this.actionBar.setTitle(LocaleController.getString("NotificationsExceptions", R.string.NotificationsExceptions));
        } else {
            this.actionBar.setTitle(LocaleController.getString("Notifications", R.string.Notifications));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    NotificationsCustomSettingsActivity.this.finishFragment();
                }
            }
        });
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList = this.exceptions;
        if (arrayList != null && !arrayList.isEmpty()) {
            ActionBarMenu menu = this.actionBar.createMenu();
            ActionBarMenuItem searchItem = menu.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity.2
                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onSearchExpand() {
                    NotificationsCustomSettingsActivity.this.searching = true;
                    NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(true);
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onSearchCollapse() {
                    NotificationsCustomSettingsActivity.this.searchAdapter.searchDialogs(null);
                    NotificationsCustomSettingsActivity.this.searching = false;
                    NotificationsCustomSettingsActivity.this.searchWas = false;
                    NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoExceptions", R.string.NoExceptions));
                    NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.adapter);
                    NotificationsCustomSettingsActivity.this.adapter.notifyDataSetChanged();
                    NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(true);
                    NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(false);
                    NotificationsCustomSettingsActivity.this.emptyView.setShowAtCenter(false);
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
                public void onTextChanged(EditText editText) {
                    if (NotificationsCustomSettingsActivity.this.searchAdapter == null) {
                        return;
                    }
                    String text = editText.getText().toString();
                    if (text.length() != 0) {
                        NotificationsCustomSettingsActivity.this.searchWas = true;
                        if (NotificationsCustomSettingsActivity.this.listView != null) {
                            NotificationsCustomSettingsActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                            NotificationsCustomSettingsActivity.this.emptyView.showProgress();
                            NotificationsCustomSettingsActivity.this.listView.setAdapter(NotificationsCustomSettingsActivity.this.searchAdapter);
                            NotificationsCustomSettingsActivity.this.searchAdapter.notifyDataSetChanged();
                            NotificationsCustomSettingsActivity.this.listView.setFastScrollVisible(false);
                            NotificationsCustomSettingsActivity.this.listView.setVerticalScrollBarEnabled(true);
                        }
                    }
                    NotificationsCustomSettingsActivity.this.searchAdapter.searchDialogs(text);
                }
            });
            searchItem.setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        }
        this.searchAdapter = new SearchAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setTextSize(18);
        this.emptyView.setText(LocaleController.getString("NoExceptions", R.string.NoExceptions));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda11
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i, float f, float f2) {
                NotificationsCustomSettingsActivity.this.m3943x91af2ba3(context, view, i, f, f2);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity.4
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(NotificationsCustomSettingsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-NotificationsCustomSettingsActivity */
    public /* synthetic */ void m3943x91af2ba3(Context context, View view, final int position, float x, float y) {
        boolean newException;
        NotificationsSettingsActivity.NotificationException exception;
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList;
        boolean newException2;
        NotificationsSettingsActivity.NotificationException exception2;
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2;
        long did;
        boolean newException3;
        NotificationsSettingsActivity.NotificationException exception3;
        String key;
        boolean enabled = false;
        if (getParentActivity() != null) {
            if (this.listView.getAdapter() == this.searchAdapter || (position >= this.exceptionsStartRow && position < this.exceptionsEndRow)) {
                RecyclerView.Adapter adapter = this.listView.getAdapter();
                SearchAdapter searchAdapter = this.searchAdapter;
                if (adapter == searchAdapter) {
                    Object object = searchAdapter.getObject(position);
                    if (object instanceof NotificationsSettingsActivity.NotificationException) {
                        exception2 = (NotificationsSettingsActivity.NotificationException) object;
                        newException2 = false;
                        arrayList2 = this.searchAdapter.searchResult;
                    } else {
                        if (object instanceof TLRPC.User) {
                            TLRPC.User user = (TLRPC.User) object;
                            did = user.id;
                        } else {
                            TLRPC.Chat chat = (TLRPC.Chat) object;
                            did = -chat.id;
                        }
                        if (this.exceptionsDict.containsKey(Long.valueOf(did))) {
                            exception3 = this.exceptionsDict.get(Long.valueOf(did));
                            newException3 = false;
                        } else {
                            NotificationsSettingsActivity.NotificationException exception4 = new NotificationsSettingsActivity.NotificationException();
                            exception4.did = did;
                            if (object instanceof TLRPC.User) {
                                TLRPC.User user2 = (TLRPC.User) object;
                                exception4.did = user2.id;
                            } else {
                                TLRPC.Chat chat2 = (TLRPC.Chat) object;
                                exception4.did = -chat2.id;
                            }
                            newException3 = true;
                            exception3 = exception4;
                        }
                        exception2 = exception3;
                        newException2 = newException3;
                        arrayList2 = this.exceptions;
                    }
                    arrayList = arrayList2;
                    exception = exception2;
                    newException = newException2;
                } else {
                    ArrayList<NotificationsSettingsActivity.NotificationException> arrayList3 = this.exceptions;
                    int index = position - this.exceptionsStartRow;
                    if (index >= 0 && index < arrayList3.size()) {
                        arrayList = arrayList3;
                        exception = arrayList3.get(index);
                        newException = false;
                    }
                    return;
                }
                if (exception == null) {
                    return;
                }
                final long did2 = exception.did;
                final boolean defaultEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(did2);
                final NotificationsSettingsActivity.NotificationException notificationException = exception;
                final boolean z = newException;
                final ArrayList<NotificationsSettingsActivity.NotificationException> arrayList4 = arrayList;
                ChatNotificationsPopupWrapper chatNotificationsPopupWrapper = new ChatNotificationsPopupWrapper(context, this.currentAccount, null, true, true, new ChatNotificationsPopupWrapper.Callback() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity.3
                    @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                    public /* synthetic */ void dismiss() {
                        ChatNotificationsPopupWrapper.Callback.CC.$default$dismiss(this);
                    }

                    @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                    public void toggleSound() {
                        SharedPreferences preferences = MessagesController.getNotificationsSettings(NotificationsCustomSettingsActivity.this.currentAccount);
                        int i = 1;
                        boolean enabled2 = !preferences.getBoolean("sound_enabled_" + did2, true);
                        preferences.edit().putBoolean("sound_enabled_" + did2, enabled2).apply();
                        if (BulletinFactory.canShowBulletin(NotificationsCustomSettingsActivity.this)) {
                            NotificationsCustomSettingsActivity notificationsCustomSettingsActivity = NotificationsCustomSettingsActivity.this;
                            if (enabled2) {
                                i = 0;
                            }
                            BulletinFactory.createSoundEnabledBulletin(notificationsCustomSettingsActivity, i, notificationsCustomSettingsActivity.getResourceProvider()).show();
                        }
                    }

                    @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                    public void muteFor(int timeInSeconds) {
                        if (timeInSeconds != 0) {
                            NotificationsCustomSettingsActivity.this.getNotificationsController().muteUntil(did2, timeInSeconds);
                            if (BulletinFactory.canShowBulletin(NotificationsCustomSettingsActivity.this)) {
                                NotificationsCustomSettingsActivity notificationsCustomSettingsActivity = NotificationsCustomSettingsActivity.this;
                                BulletinFactory.createMuteBulletin(notificationsCustomSettingsActivity, 5, timeInSeconds, notificationsCustomSettingsActivity.getResourceProvider()).show();
                            }
                        } else {
                            if (NotificationsCustomSettingsActivity.this.getMessagesController().isDialogMuted(did2)) {
                                toggleMute();
                            }
                            if (BulletinFactory.canShowBulletin(NotificationsCustomSettingsActivity.this)) {
                                NotificationsCustomSettingsActivity notificationsCustomSettingsActivity2 = NotificationsCustomSettingsActivity.this;
                                BulletinFactory.createMuteBulletin(notificationsCustomSettingsActivity2, 4, timeInSeconds, notificationsCustomSettingsActivity2.getResourceProvider()).show();
                            }
                        }
                        update();
                    }

                    @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                    public void showCustomize() {
                        if (did2 != 0) {
                            Bundle args = new Bundle();
                            args.putLong("dialog_id", did2);
                            ProfileNotificationsActivity fragment = new ProfileNotificationsActivity(args);
                            fragment.setDelegate(new ProfileNotificationsActivity.ProfileNotificationsActivityDelegate() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity.3.1
                                @Override // org.telegram.ui.ProfileNotificationsActivity.ProfileNotificationsActivityDelegate
                                public void didCreateNewException(NotificationsSettingsActivity.NotificationException exception5) {
                                }

                                @Override // org.telegram.ui.ProfileNotificationsActivity.ProfileNotificationsActivityDelegate
                                public void didRemoveException(long dialog_id) {
                                    setDefault();
                                }
                            });
                            NotificationsCustomSettingsActivity.this.presentFragment(fragment);
                        }
                    }

                    @Override // org.telegram.ui.Components.ChatNotificationsPopupWrapper.Callback
                    public void toggleMute() {
                        boolean muted = NotificationsCustomSettingsActivity.this.getMessagesController().isDialogMuted(did2);
                        NotificationsCustomSettingsActivity.this.getNotificationsController().muteDialog(did2, !muted);
                        NotificationsCustomSettingsActivity notificationsCustomSettingsActivity = NotificationsCustomSettingsActivity.this;
                        BulletinFactory.createMuteBulletin(notificationsCustomSettingsActivity, notificationsCustomSettingsActivity.getMessagesController().isDialogMuted(did2), null).show();
                        update();
                    }

                    private void update() {
                        if (NotificationsCustomSettingsActivity.this.getMessagesController().isDialogMuted(did2) != defaultEnabled) {
                            setDefault();
                        } else {
                            setNotDefault();
                        }
                    }

                    private void setNotDefault() {
                        SharedPreferences preferences = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                        NotificationsSettingsActivity.NotificationException notificationException2 = notificationException;
                        notificationException2.hasCustom = preferences.getBoolean(ContentMetadata.KEY_CUSTOM_PREFIX + notificationException.did, false);
                        NotificationsSettingsActivity.NotificationException notificationException3 = notificationException;
                        notificationException3.notify = preferences.getInt("notify2_" + notificationException.did, 0);
                        if (notificationException.notify != 0) {
                            int time = preferences.getInt("notifyuntil_" + notificationException.did, -1);
                            if (time != -1) {
                                notificationException.muteUntil = time;
                            }
                        }
                        if (z) {
                            NotificationsCustomSettingsActivity.this.exceptions.add(notificationException);
                            NotificationsCustomSettingsActivity.this.exceptionsDict.put(Long.valueOf(notificationException.did), notificationException);
                            NotificationsCustomSettingsActivity.this.updateRows(true);
                        } else {
                            NotificationsCustomSettingsActivity.this.listView.getAdapter().notifyItemChanged(position);
                        }
                        NotificationsCustomSettingsActivity.this.actionBar.closeSearchField();
                    }

                    public void setDefault() {
                        int idx;
                        if (!z) {
                            if (arrayList4 != NotificationsCustomSettingsActivity.this.exceptions && (idx = NotificationsCustomSettingsActivity.this.exceptions.indexOf(notificationException)) >= 0) {
                                NotificationsCustomSettingsActivity.this.exceptions.remove(idx);
                                NotificationsCustomSettingsActivity.this.exceptionsDict.remove(Long.valueOf(notificationException.did));
                            }
                            arrayList4.remove(notificationException);
                            if (arrayList4 == NotificationsCustomSettingsActivity.this.exceptions) {
                                if (NotificationsCustomSettingsActivity.this.exceptionsAddRow != -1 && arrayList4.isEmpty()) {
                                    NotificationsCustomSettingsActivity.this.listView.getAdapter().notifyItemChanged(NotificationsCustomSettingsActivity.this.exceptionsAddRow);
                                    NotificationsCustomSettingsActivity.this.listView.getAdapter().notifyItemRemoved(NotificationsCustomSettingsActivity.this.deleteAllRow);
                                    NotificationsCustomSettingsActivity.this.listView.getAdapter().notifyItemRemoved(NotificationsCustomSettingsActivity.this.deleteAllSectionRow);
                                }
                                NotificationsCustomSettingsActivity.this.listView.getAdapter().notifyItemRemoved(position);
                                NotificationsCustomSettingsActivity.this.updateRows(false);
                                NotificationsCustomSettingsActivity.this.checkRowsEnabled();
                            } else {
                                NotificationsCustomSettingsActivity.this.updateRows(true);
                                NotificationsCustomSettingsActivity.this.searchAdapter.notifyItemChanged(position);
                            }
                            NotificationsCustomSettingsActivity.this.actionBar.closeSearchField();
                        }
                    }
                }, getResourceProvider());
                chatNotificationsPopupWrapper.m2522x80790d7d(did2);
                chatNotificationsPopupWrapper.showAsOptions(this, view, x, y);
                return;
            }
            if (position != this.exceptionsAddRow) {
                if (position != this.deleteAllRow) {
                    if (position == this.alertRow) {
                        enabled = getNotificationsController().isGlobalNotificationsEnabled(this.currentType);
                        final NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                        final RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
                        if (!enabled) {
                            getNotificationsController().setGlobalNotificationsEnabled(this.currentType, 0);
                            checkCell.setChecked(true);
                            if (holder != null) {
                                this.adapter.onBindViewHolder(holder, position);
                            }
                            checkRowsEnabled();
                        } else {
                            AlertsCreator.showCustomNotificationsDialog(this, 0L, this.currentType, this.exceptions, this.currentAccount, new MessagesStorage.IntCallback() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda9
                                @Override // org.telegram.messenger.MessagesStorage.IntCallback
                                public final void run(int i) {
                                    NotificationsCustomSettingsActivity.this.m3938xcf89b41e(checkCell, holder, position, i);
                                }
                            });
                        }
                    } else if (position != this.previewRow) {
                        if (position == this.messageSoundRow) {
                            if (!view.isEnabled()) {
                                return;
                            }
                            try {
                                Bundle bundle = new Bundle();
                                bundle.putInt(CommonProperties.TYPE, this.currentType);
                                presentFragment(new NotificationsSoundActivity(bundle, getResourceProvider()));
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        } else if (position == this.messageLedRow) {
                            if (view.isEnabled()) {
                                showDialog(AlertsCreator.createColorSelectDialog(getParentActivity(), 0L, this.currentType, new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda4
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        NotificationsCustomSettingsActivity.this.m3939x5cc4659f(position);
                                    }
                                }));
                            } else {
                                return;
                            }
                        } else if (position == this.messagePopupNotificationRow) {
                            if (view.isEnabled()) {
                                showDialog(AlertsCreator.createPopupSelectDialog(getParentActivity(), this.currentType, new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda5
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        NotificationsCustomSettingsActivity.this.m3940xe9ff1720(position);
                                    }
                                }));
                            } else {
                                return;
                            }
                        } else if (position == this.messageVibrateRow) {
                            if (!view.isEnabled()) {
                                return;
                            }
                            int i = this.currentType;
                            if (i == 1) {
                                key = "vibrate_messages";
                            } else if (i == 0) {
                                key = "vibrate_group";
                            } else {
                                key = "vibrate_channel";
                            }
                            showDialog(AlertsCreator.createVibrationSelectDialog(getParentActivity(), 0L, key, new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda6
                                @Override // java.lang.Runnable
                                public final void run() {
                                    NotificationsCustomSettingsActivity.this.m3941x7739c8a1(position);
                                }
                            }));
                        } else if (position == this.messagePriorityRow) {
                            if (view.isEnabled()) {
                                showDialog(AlertsCreator.createPrioritySelectDialog(getParentActivity(), 0L, this.currentType, new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda7
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        NotificationsCustomSettingsActivity.this.m3942x4747a22(position);
                                    }
                                }));
                            } else {
                                return;
                            }
                        }
                    } else if (!view.isEnabled()) {
                        return;
                    } else {
                        SharedPreferences preferences = getNotificationsSettings();
                        SharedPreferences.Editor editor = preferences.edit();
                        int i2 = this.currentType;
                        if (i2 == 1) {
                            boolean enabled2 = preferences.getBoolean("EnablePreviewAll", true);
                            editor.putBoolean("EnablePreviewAll", !enabled2);
                            enabled = enabled2;
                        } else if (i2 == 0) {
                            boolean enabled3 = preferences.getBoolean("EnablePreviewGroup", true);
                            editor.putBoolean("EnablePreviewGroup", !enabled3);
                            enabled = enabled3;
                        } else {
                            boolean enabled4 = preferences.getBoolean("EnablePreviewChannel", true);
                            editor.putBoolean("EnablePreviewChannel", !enabled4);
                            enabled = enabled4;
                        }
                        editor.commit();
                        getNotificationsController().updateServerNotificationsSettings(this.currentType);
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("NotificationsDeleteAllExceptionTitle", R.string.NotificationsDeleteAllExceptionTitle));
                    builder.setMessage(LocaleController.getString("NotificationsDeleteAllExceptionAlert", R.string.NotificationsDeleteAllExceptionAlert));
                    builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i3) {
                            NotificationsCustomSettingsActivity.this.m3937x424f029d(dialogInterface, i3);
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
            } else {
                Bundle args = new Bundle();
                args.putBoolean("onlySelect", true);
                args.putBoolean("checkCanWrite", false);
                int i3 = this.currentType;
                if (i3 == 0) {
                    args.putInt("dialogsType", 6);
                } else if (i3 == 2) {
                    args.putInt("dialogsType", 5);
                } else {
                    args.putInt("dialogsType", 4);
                }
                DialogsActivity activity = new DialogsActivity(args);
                activity.setDelegate(new DialogsActivity.DialogsActivityDelegate() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda1
                    @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
                    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList5, CharSequence charSequence, boolean z2) {
                        NotificationsCustomSettingsActivity.this.m3936xb514511c(dialogsActivity, arrayList5, charSequence, z2);
                    }
                });
                presentFragment(activity);
            }
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!enabled);
            }
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-NotificationsCustomSettingsActivity */
    public /* synthetic */ void m3936xb514511c(DialogsActivity fragment, ArrayList dids, CharSequence message, boolean param) {
        Bundle args2 = new Bundle();
        args2.putLong("dialog_id", ((Long) dids.get(0)).longValue());
        args2.putBoolean("exception", true);
        ProfileNotificationsActivity profileNotificationsActivity = new ProfileNotificationsActivity(args2, getResourceProvider());
        profileNotificationsActivity.setDelegate(new ProfileNotificationsActivity.ProfileNotificationsActivityDelegate() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.ProfileNotificationsActivity.ProfileNotificationsActivityDelegate
            public final void didCreateNewException(NotificationsSettingsActivity.NotificationException notificationException) {
                NotificationsCustomSettingsActivity.this.m3935x27d99f9b(notificationException);
            }

            @Override // org.telegram.ui.ProfileNotificationsActivity.ProfileNotificationsActivityDelegate
            public /* synthetic */ void didRemoveException(long j) {
                ProfileNotificationsActivity.ProfileNotificationsActivityDelegate.CC.$default$didRemoveException(this, j);
            }
        });
        presentFragment(profileNotificationsActivity, true);
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-NotificationsCustomSettingsActivity */
    public /* synthetic */ void m3935x27d99f9b(NotificationsSettingsActivity.NotificationException exception) {
        this.exceptions.add(0, exception);
        updateRows(true);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-NotificationsCustomSettingsActivity */
    public /* synthetic */ void m3937x424f029d(DialogInterface dialogInterface, int i) {
        SharedPreferences preferences = getNotificationsSettings();
        SharedPreferences.Editor editor = preferences.edit();
        int N = this.exceptions.size();
        for (int a = 0; a < N; a++) {
            NotificationsSettingsActivity.NotificationException exception = this.exceptions.get(a);
            SharedPreferences.Editor remove = editor.remove("notify2_" + exception.did);
            remove.remove(ContentMetadata.KEY_CUSTOM_PREFIX + exception.did);
            getMessagesStorage().setDialogFlags(exception.did, 0L);
            TLRPC.Dialog dialog = getMessagesController().dialogs_dict.get(exception.did);
            if (dialog != null) {
                dialog.notify_settings = new TLRPC.TL_peerNotifySettings();
            }
        }
        editor.commit();
        int N2 = this.exceptions.size();
        for (int a2 = 0; a2 < N2; a2++) {
            getNotificationsController().updateServerNotificationsSettings(this.exceptions.get(a2).did, false);
        }
        this.exceptions.clear();
        this.exceptionsDict.clear();
        updateRows(true);
        getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-NotificationsCustomSettingsActivity */
    public /* synthetic */ void m3938xcf89b41e(NotificationsCheckCell checkCell, RecyclerView.ViewHolder holder, int position, int param) {
        int offUntil;
        int iconType;
        SharedPreferences preferences = getNotificationsSettings();
        int offUntil2 = this.currentType;
        if (offUntil2 == 1) {
            offUntil = preferences.getInt("EnableAll2", 0);
        } else if (offUntil2 == 0) {
            offUntil = preferences.getInt("EnableGroup2", 0);
        } else {
            offUntil = preferences.getInt("EnableChannel2", 0);
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        if (offUntil < currentTime) {
            iconType = 0;
        } else if (offUntil - 31536000 >= currentTime) {
            iconType = 0;
        } else {
            iconType = 2;
        }
        checkCell.setChecked(getNotificationsController().isGlobalNotificationsEnabled(this.currentType), iconType);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
        checkRowsEnabled();
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-NotificationsCustomSettingsActivity */
    public /* synthetic */ void m3939x5cc4659f(int position) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-NotificationsCustomSettingsActivity */
    public /* synthetic */ void m3940xe9ff1720(int position) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-NotificationsCustomSettingsActivity */
    public /* synthetic */ void m3941x7739c8a1(int position) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-NotificationsCustomSettingsActivity */
    public /* synthetic */ void m3942x4747a22(int position) {
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            this.adapter.onBindViewHolder(holder, position);
        }
    }

    public void checkRowsEnabled() {
        if (!this.exceptions.isEmpty()) {
            return;
        }
        int count = this.listView.getChildCount();
        ArrayList<Animator> animators = new ArrayList<>();
        boolean enabled = getNotificationsController().isGlobalNotificationsEnabled(this.currentType);
        for (int a = 0; a < count; a++) {
            View child = this.listView.getChildAt(a);
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.getChildViewHolder(child);
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (holder.getAdapterPosition() == this.messageSectionRow) {
                        headerCell.setEnabled(enabled, animators);
                        break;
                    } else {
                        break;
                    }
                case 1:
                    TextCheckCell textCell = (TextCheckCell) holder.itemView;
                    textCell.setEnabled(enabled, animators);
                    break;
                case 3:
                    TextColorCell textCell2 = (TextColorCell) holder.itemView;
                    textCell2.setEnabled(enabled, animators);
                    break;
                case 5:
                    TextSettingsCell textCell3 = (TextSettingsCell) holder.itemView;
                    textCell3.setEnabled(enabled, animators);
                    break;
            }
        }
        if (!animators.isEmpty()) {
            AnimatorSet animatorSet = this.animatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animatorSet = animatorSet2;
            animatorSet2.playTogether(animators);
            this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(NotificationsCustomSettingsActivity.this.animatorSet)) {
                        NotificationsCustomSettingsActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.setDuration(150L);
            this.animatorSet.start();
        }
    }

    private void loadExceptions() {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                NotificationsCustomSettingsActivity.this.m3945x1eb530ad();
            }
        });
    }

    /* JADX WARN: Removed duplicated region for block: B:115:0x02f1  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x030b A[LOOP:3: B:120:0x0309->B:121:0x030b, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:124:0x0325  */
    /* JADX WARN: Removed duplicated region for block: B:96:0x029d  */
    /* renamed from: lambda$loadExceptions$10$org-telegram-ui-NotificationsCustomSettingsActivity */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void m3945x1eb530ad() {
        /*
            Method dump skipped, instructions count: 879
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.m3945x1eb530ad():void");
    }

    /* renamed from: lambda$loadExceptions$9$org-telegram-ui-NotificationsCustomSettingsActivity */
    public /* synthetic */ void m3946xec5027ad(ArrayList users, ArrayList chats, ArrayList encryptedChats, ArrayList usersResult, ArrayList chatsResult, ArrayList channelsResult) {
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        getMessagesController().putEncryptedChats(encryptedChats, true);
        int i = this.currentType;
        if (i == 1) {
            this.exceptions = usersResult;
        } else if (i == 0) {
            this.exceptions = chatsResult;
        } else {
            this.exceptions = channelsResult;
        }
        updateRows(true);
    }

    public void updateRows(boolean notify) {
        ListAdapter listAdapter;
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList;
        this.rowCount = 0;
        int i = this.currentType;
        if (i != -1) {
            int i2 = 0 + 1;
            this.rowCount = i2;
            this.alertRow = 0;
            int i3 = i2 + 1;
            this.rowCount = i3;
            this.alertSection2Row = i2;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.messageSectionRow = i3;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.previewRow = i4;
            int i6 = i5 + 1;
            this.rowCount = i6;
            this.messageLedRow = i5;
            int i7 = i6 + 1;
            this.rowCount = i7;
            this.messageVibrateRow = i6;
            if (i == 2) {
                this.messagePopupNotificationRow = -1;
            } else {
                this.rowCount = i7 + 1;
                this.messagePopupNotificationRow = i7;
            }
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.messageSoundRow = i8;
            if (Build.VERSION.SDK_INT >= 21) {
                int i9 = this.rowCount;
                this.rowCount = i9 + 1;
                this.messagePriorityRow = i9;
            } else {
                this.messagePriorityRow = -1;
            }
            int i10 = this.rowCount;
            int i11 = i10 + 1;
            this.rowCount = i11;
            this.groupSection2Row = i10;
            this.rowCount = i11 + 1;
            this.exceptionsAddRow = i11;
        } else {
            this.alertRow = -1;
            this.alertSection2Row = -1;
            this.messageSectionRow = -1;
            this.previewRow = -1;
            this.messageLedRow = -1;
            this.messageVibrateRow = -1;
            this.messagePopupNotificationRow = -1;
            this.messageSoundRow = -1;
            this.messagePriorityRow = -1;
            this.groupSection2Row = -1;
            this.exceptionsAddRow = -1;
        }
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList2 = this.exceptions;
        if (arrayList2 != null && !arrayList2.isEmpty()) {
            int i12 = this.rowCount;
            this.exceptionsStartRow = i12;
            int size = i12 + this.exceptions.size();
            this.rowCount = size;
            this.exceptionsEndRow = size;
        } else {
            this.exceptionsStartRow = -1;
            this.exceptionsEndRow = -1;
        }
        if (this.currentType != -1 || ((arrayList = this.exceptions) != null && !arrayList.isEmpty())) {
            int i13 = this.rowCount;
            this.rowCount = i13 + 1;
            this.exceptionsSection2Row = i13;
        } else {
            this.exceptionsSection2Row = -1;
        }
        ArrayList<NotificationsSettingsActivity.NotificationException> arrayList3 = this.exceptions;
        if (arrayList3 != null && !arrayList3.isEmpty()) {
            int i14 = this.rowCount;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.deleteAllRow = i14;
            this.rowCount = i15 + 1;
            this.deleteAllSectionRow = i15;
        } else {
            this.deleteAllRow = -1;
            this.deleteAllSectionRow = -1;
        }
        if (notify && (listAdapter = this.adapter) != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        Ringtone rng;
        if (resultCode == -1) {
            Uri ringtone = (Uri) data.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
            String name = null;
            if (ringtone != null && (rng = RingtoneManager.getRingtone(getParentActivity(), ringtone)) != null) {
                if (ringtone.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
                    name = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                } else {
                    name = rng.getTitle(getParentActivity());
                }
                rng.stop();
            }
            SharedPreferences preferences = getNotificationsSettings();
            SharedPreferences.Editor editor = preferences.edit();
            int i = this.currentType;
            if (i == 1) {
                if (name != null && ringtone != null) {
                    editor.putString("GlobalSound", name);
                    editor.putString("GlobalSoundPath", ringtone.toString());
                } else {
                    editor.putString("GlobalSound", "NoSound");
                    editor.putString("GlobalSoundPath", "NoSound");
                }
            } else if (i == 0) {
                if (name != null && ringtone != null) {
                    editor.putString("GroupSound", name);
                    editor.putString("GroupSoundPath", ringtone.toString());
                } else {
                    editor.putString("GroupSound", "NoSound");
                    editor.putString("GroupSoundPath", "NoSound");
                }
            } else if (i == 2) {
                if (name != null && ringtone != null) {
                    editor.putString("ChannelSound", name);
                    editor.putString("ChannelSoundPath", ringtone.toString());
                } else {
                    editor.putString("ChannelSound", "NoSound");
                    editor.putString("ChannelSoundPath", "NoSound");
                }
            }
            getNotificationsController().deleteNotificationChannelGlobal(this.currentType);
            editor.commit();
            getNotificationsController().updateServerNotificationsSettings(this.currentType);
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(requestCode);
            if (holder != null) {
                this.adapter.onBindViewHolder(holder, requestCode);
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        getNotificationCenter().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        getNotificationCenter().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        ListAdapter listAdapter;
        if (id == NotificationCenter.notificationsSettingsUpdated && (listAdapter = this.adapter) != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* loaded from: classes4.dex */
    public class SearchAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;
        private SearchAdapterHelper searchAdapterHelper;
        private ArrayList<NotificationsSettingsActivity.NotificationException> searchResult = new ArrayList<>();
        private ArrayList<CharSequence> searchResultNames = new ArrayList<>();
        private Runnable searchRunnable;

        public SearchAdapter(Context context) {
            NotificationsCustomSettingsActivity.this = r2;
            this.mContext = context;
            SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper(true);
            this.searchAdapterHelper = searchAdapterHelper;
            searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda4
                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ boolean canApplySearchResults(int i) {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public final void onDataSetChanged(int i) {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.m3947x1bdfa523(i);
                }

                @Override // org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate
                public /* synthetic */ void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        /* renamed from: lambda$new$0$org-telegram-ui-NotificationsCustomSettingsActivity$SearchAdapter */
        public /* synthetic */ void m3947x1bdfa523(int searchId) {
            if (this.searchRunnable == null && !this.searchAdapterHelper.isSearchInProgress()) {
                NotificationsCustomSettingsActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public void searchDialogs(final String query) {
            if (this.searchRunnable != null) {
                Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                this.searchRunnable = null;
            }
            if (query == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                this.searchAdapterHelper.mergeResults(null);
                this.searchAdapterHelper.queryServerSearch(null, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, false, 0L, false, 0, 0);
                notifyDataSetChanged();
                return;
            }
            DispatchQueue dispatchQueue = Utilities.searchQueue;
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.m3950xa1b56767(query);
                }
            };
            this.searchRunnable = runnable;
            dispatchQueue.postRunnable(runnable, 300L);
        }

        /* renamed from: processSearch */
        public void m3950xa1b56767(final String query) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.m3949xe4abc47d(query);
                }
            });
        }

        /* renamed from: lambda$processSearch$3$org-telegram-ui-NotificationsCustomSettingsActivity$SearchAdapter */
        public /* synthetic */ void m3949xe4abc47d(final String query) {
            this.searchAdapterHelper.queryServerSearch(query, true, NotificationsCustomSettingsActivity.this.currentType != 1, true, false, false, 0L, false, 0, 0);
            final ArrayList<NotificationsSettingsActivity.NotificationException> contactsCopy = new ArrayList<>(NotificationsCustomSettingsActivity.this.exceptions);
            Utilities.searchQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.m3948x8ea48bc(query, contactsCopy);
                }
            });
        }

        /* JADX WARN: Code restructure failed: missing block: B:62:0x017f, code lost:
            if (r9[0].contains(" " + r15) == false) goto L64;
         */
        /* JADX WARN: Code restructure failed: missing block: B:68:0x019f, code lost:
            if (r3.contains(" " + r15) != false) goto L69;
         */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:85:0x0201 A[LOOP:1: B:55:0x0151->B:85:0x0201, LOOP_END] */
        /* JADX WARN: Removed duplicated region for block: B:99:0x01ba A[SYNTHETIC] */
        /* JADX WARN: Type inference failed for: r5v10 */
        /* JADX WARN: Type inference failed for: r5v13 */
        /* JADX WARN: Type inference failed for: r5v6 */
        /* JADX WARN: Type inference failed for: r5v8 */
        /* renamed from: lambda$processSearch$2$org-telegram-ui-NotificationsCustomSettingsActivity$SearchAdapter */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void m3948x8ea48bc(java.lang.String r23, java.util.ArrayList r24) {
            /*
                Method dump skipped, instructions count: 553
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsCustomSettingsActivity.SearchAdapter.m3948x8ea48bc(java.lang.String, java.util.ArrayList):void");
        }

        private void updateSearchResults(final ArrayList<Object> result, final ArrayList<NotificationsSettingsActivity.NotificationException> exceptions, final ArrayList<CharSequence> names) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$SearchAdapter$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationsCustomSettingsActivity.SearchAdapter.this.m3951x5892f8ec(exceptions, names, result);
                }
            });
        }

        /* renamed from: lambda$updateSearchResults$4$org-telegram-ui-NotificationsCustomSettingsActivity$SearchAdapter */
        public /* synthetic */ void m3951x5892f8ec(ArrayList exceptions, ArrayList names, ArrayList result) {
            if (!NotificationsCustomSettingsActivity.this.searching) {
                return;
            }
            this.searchRunnable = null;
            this.searchResult = exceptions;
            this.searchResultNames = names;
            this.searchAdapterHelper.mergeResults(result);
            if (NotificationsCustomSettingsActivity.this.searching && !this.searchAdapterHelper.isSearchInProgress()) {
                NotificationsCustomSettingsActivity.this.emptyView.showTextView();
            }
            notifyDataSetChanged();
        }

        public Object getObject(int position) {
            if (position >= 0 && position < this.searchResult.size()) {
                return this.searchResult.get(position);
            }
            int position2 = position - (this.searchResult.size() + 1);
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            if (position2 >= 0 && position2 < globalSearch.size()) {
                return this.searchAdapterHelper.getGlobalSearch().get(position2);
            }
            return null;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int count = this.searchResult.size();
            ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
            if (!globalSearch.isEmpty()) {
                return count + globalSearch.size() + 1;
            }
            return count;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new UserCell(this.mContext, 4, 0, false, true);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new GraySectionCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    UserCell cell = (UserCell) holder.itemView;
                    boolean z = true;
                    if (position < this.searchResult.size()) {
                        NotificationsSettingsActivity.NotificationException notificationException = this.searchResult.get(position);
                        CharSequence charSequence = this.searchResultNames.get(position);
                        if (position == this.searchResult.size() - 1) {
                            z = false;
                        }
                        cell.setException(notificationException, charSequence, z);
                        cell.setAddButtonVisible(false);
                        return;
                    }
                    int position2 = position - (this.searchResult.size() + 1);
                    ArrayList<TLObject> globalSearch = this.searchAdapterHelper.getGlobalSearch();
                    TLObject object = globalSearch.get(position2);
                    cell.setData(object, null, LocaleController.getString("NotificationsOn", R.string.NotificationsOn), 0, position2 != globalSearch.size() - 1);
                    cell.setAddButtonVisible(true);
                    return;
                case 1:
                    ((GraySectionCell) holder.itemView).setText(LocaleController.getString("AddToExceptions", R.string.AddToExceptions));
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position == this.searchResult.size()) {
                return 1;
            }
            return 0;
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            NotificationsCustomSettingsActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return (type == 0 || type == 4) ? false : true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return NotificationsCustomSettingsActivity.this.rowCount;
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
                    view = new TextCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 2:
                    view = new UserCell(this.mContext, 6, 0, false);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new TextColorCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 5:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 6:
                    view = new NotificationsCheckCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                default:
                    view = new TextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean enabled;
            int color;
            int option;
            String value;
            int value2;
            int value3;
            long documentId;
            String value4;
            int offUntil;
            String text;
            int iconType;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                        headerCell.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                        return;
                    }
                    return;
                case 1:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    SharedPreferences preferences = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (position == NotificationsCustomSettingsActivity.this.previewRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                            if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                                enabled = preferences.getBoolean("EnablePreviewGroup", true);
                            } else {
                                enabled = preferences.getBoolean("EnablePreviewChannel", true);
                            }
                        } else {
                            enabled = preferences.getBoolean("EnablePreviewAll", true);
                        }
                        checkCell.setTextAndCheck(LocaleController.getString("MessagePreview", R.string.MessagePreview), enabled, true);
                        return;
                    }
                    return;
                case 2:
                    UserCell cell = (UserCell) holder.itemView;
                    NotificationsSettingsActivity.NotificationException exception = (NotificationsSettingsActivity.NotificationException) NotificationsCustomSettingsActivity.this.exceptions.get(position - NotificationsCustomSettingsActivity.this.exceptionsStartRow);
                    if (position != NotificationsCustomSettingsActivity.this.exceptionsEndRow - 1) {
                        z = true;
                    }
                    cell.setException(exception, null, z);
                    return;
                case 3:
                    TextColorCell textColorCell = (TextColorCell) holder.itemView;
                    SharedPreferences preferences2 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            color = preferences2.getInt("GroupLed", -16776961);
                        } else {
                            color = preferences2.getInt("ChannelLed", -16776961);
                        }
                    } else {
                        color = preferences2.getInt("MessagesLed", -16776961);
                    }
                    int a = 0;
                    while (true) {
                        if (a < 9) {
                            if (TextColorCell.colorsToSave[a] != color) {
                                a++;
                            } else {
                                color = TextColorCell.colors[a];
                            }
                        }
                    }
                    textColorCell.setTextAndColor(LocaleController.getString("LedColor", R.string.LedColor), color, true);
                    return;
                case 4:
                    if (position == NotificationsCustomSettingsActivity.this.deleteAllSectionRow || ((position == NotificationsCustomSettingsActivity.this.groupSection2Row && NotificationsCustomSettingsActivity.this.exceptionsSection2Row == -1) || (position == NotificationsCustomSettingsActivity.this.exceptionsSection2Row && NotificationsCustomSettingsActivity.this.deleteAllRow == -1))) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 5:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    SharedPreferences preferences3 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (position == NotificationsCustomSettingsActivity.this.messageSoundRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                            if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                                value4 = preferences3.getString("GroupSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                                documentId = preferences3.getLong("GroupSoundDocId", 0L);
                            } else {
                                String value5 = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                                value4 = preferences3.getString("ChannelSound", value5);
                                documentId = preferences3.getLong("ChannelDocId", 0L);
                            }
                        } else {
                            value4 = preferences3.getString("GlobalSound", LocaleController.getString("SoundDefault", R.string.SoundDefault));
                            documentId = preferences3.getLong("GlobalSoundDocId", 0L);
                        }
                        if (documentId != 0) {
                            TLRPC.Document document = NotificationsCustomSettingsActivity.this.getMediaDataController().ringtoneDataStore.getDocument(documentId);
                            if (document == null) {
                                value4 = LocaleController.getString("CustomSound", R.string.CustomSound);
                            } else {
                                value4 = NotificationsSoundActivity.trimTitle(document, FileLoader.getDocumentFileName(document));
                            }
                        } else if (value4.equals("NoSound")) {
                            value4 = LocaleController.getString("NoSound", R.string.NoSound);
                        } else if (value4.equals("Default")) {
                            value4 = LocaleController.getString("SoundDefault", R.string.SoundDefault);
                        }
                        textCell.setTextAndValue(LocaleController.getString("Sound", R.string.Sound), value4, true);
                        return;
                    } else if (position == NotificationsCustomSettingsActivity.this.messageVibrateRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                            if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                                value3 = preferences3.getInt("vibrate_group", 0);
                            } else {
                                value3 = preferences3.getInt("vibrate_channel", 0);
                            }
                        } else {
                            value3 = preferences3.getInt("vibrate_messages", 0);
                        }
                        if (value3 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDefault", R.string.VibrationDefault), true);
                            return;
                        } else if (value3 == 1) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Short", R.string.Short), true);
                            return;
                        } else if (value3 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("VibrationDisabled", R.string.VibrationDisabled), true);
                            return;
                        } else if (value3 == 3) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("Long", R.string.Long), true);
                            return;
                        } else if (value3 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("Vibrate", R.string.Vibrate), LocaleController.getString("OnlyIfSilent", R.string.OnlyIfSilent), true);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == NotificationsCustomSettingsActivity.this.messagePriorityRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                            if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                                value2 = preferences3.getInt("priority_group", 1);
                            } else {
                                value2 = preferences3.getInt("priority_channel", 1);
                            }
                        } else {
                            value2 = preferences3.getInt("priority_messages", 1);
                        }
                        if (value2 == 0) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityHigh", R.string.NotificationsPriorityHigh), false);
                            return;
                        } else if (value2 == 1 || value2 == 2) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityUrgent", R.string.NotificationsPriorityUrgent), false);
                            return;
                        } else if (value2 == 4) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityLow", R.string.NotificationsPriorityLow), false);
                            return;
                        } else if (value2 == 5) {
                            textCell.setTextAndValue(LocaleController.getString("NotificationsImportance", R.string.NotificationsImportance), LocaleController.getString("NotificationsPriorityMedium", R.string.NotificationsPriorityMedium), false);
                            return;
                        } else {
                            return;
                        }
                    } else if (position == NotificationsCustomSettingsActivity.this.messagePopupNotificationRow) {
                        if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                            if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                                option = preferences3.getInt("popupGroup", 0);
                            } else {
                                option = preferences3.getInt("popupChannel", 0);
                            }
                        } else {
                            option = preferences3.getInt("popupAll", 0);
                        }
                        if (option == 0) {
                            value = LocaleController.getString("NoPopup", R.string.NoPopup);
                        } else if (option == 1) {
                            value = LocaleController.getString("OnlyWhenScreenOn", R.string.OnlyWhenScreenOn);
                        } else if (option == 2) {
                            value = LocaleController.getString("OnlyWhenScreenOff", R.string.OnlyWhenScreenOff);
                        } else {
                            value = LocaleController.getString("AlwaysShowPopup", R.string.AlwaysShowPopup);
                        }
                        textCell.setTextAndValue(LocaleController.getString("PopupNotification", R.string.PopupNotification), value, true);
                        return;
                    } else {
                        return;
                    }
                case 6:
                    NotificationsCheckCell checkCell2 = (NotificationsCheckCell) holder.itemView;
                    checkCell2.setDrawLine(false);
                    StringBuilder builder = new StringBuilder();
                    SharedPreferences preferences4 = NotificationsCustomSettingsActivity.this.getNotificationsSettings();
                    if (NotificationsCustomSettingsActivity.this.currentType != 1) {
                        if (NotificationsCustomSettingsActivity.this.currentType == 0) {
                            String text2 = LocaleController.getString("NotificationsForGroups", R.string.NotificationsForGroups);
                            text = text2;
                            offUntil = preferences4.getInt("EnableGroup2", 0);
                        } else {
                            String text3 = LocaleController.getString("NotificationsForChannels", R.string.NotificationsForChannels);
                            text = text3;
                            offUntil = preferences4.getInt("EnableChannel2", 0);
                        }
                    } else {
                        String text4 = LocaleController.getString("NotificationsForPrivateChats", R.string.NotificationsForPrivateChats);
                        text = text4;
                        offUntil = preferences4.getInt("EnableAll2", 0);
                    }
                    int currentTime = NotificationsCustomSettingsActivity.this.getConnectionsManager().getCurrentTime();
                    boolean z2 = offUntil < currentTime;
                    boolean enabled2 = z2;
                    if (z2) {
                        builder.append(LocaleController.getString("NotificationsOn", R.string.NotificationsOn));
                        iconType = 0;
                    } else if (offUntil - 31536000 >= currentTime) {
                        builder.append(LocaleController.getString("NotificationsOff", R.string.NotificationsOff));
                        iconType = 0;
                    } else {
                        builder.append(LocaleController.formatString("NotificationsOffUntil", R.string.NotificationsOffUntil, LocaleController.stringForMessageListDate(offUntil)));
                        iconType = 2;
                    }
                    checkCell2.setTextAndValueAndCheck(text, builder, enabled2, iconType, false);
                    return;
                case 7:
                    TextCell textCell2 = (TextCell) holder.itemView;
                    if (position != NotificationsCustomSettingsActivity.this.exceptionsAddRow) {
                        if (position == NotificationsCustomSettingsActivity.this.deleteAllRow) {
                            textCell2.setText(LocaleController.getString("NotificationsDeleteAllException", R.string.NotificationsDeleteAllException), false);
                            textCell2.setColors(null, Theme.key_windowBackgroundWhiteRedText5);
                            return;
                        }
                        return;
                    }
                    String string = LocaleController.getString("NotificationsAddAnException", R.string.NotificationsAddAnException);
                    if (NotificationsCustomSettingsActivity.this.exceptionsStartRow != -1) {
                        z = true;
                    }
                    textCell2.setTextAndIcon(string, R.drawable.msg_contact_add, z);
                    textCell2.setColors(Theme.key_windowBackgroundWhiteBlueIcon, Theme.key_windowBackgroundWhiteBlueButton);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (NotificationsCustomSettingsActivity.this.exceptions != null && NotificationsCustomSettingsActivity.this.exceptions.isEmpty()) {
                boolean enabled = NotificationsCustomSettingsActivity.this.getNotificationsController().isGlobalNotificationsEnabled(NotificationsCustomSettingsActivity.this.currentType);
                switch (holder.getItemViewType()) {
                    case 0:
                        HeaderCell headerCell = (HeaderCell) holder.itemView;
                        if (holder.getAdapterPosition() == NotificationsCustomSettingsActivity.this.messageSectionRow) {
                            headerCell.setEnabled(enabled, null);
                            return;
                        } else {
                            headerCell.setEnabled(true, null);
                            return;
                        }
                    case 1:
                        TextCheckCell textCell = (TextCheckCell) holder.itemView;
                        textCell.setEnabled(enabled, null);
                        return;
                    case 2:
                    case 4:
                    default:
                        return;
                    case 3:
                        TextColorCell textCell2 = (TextColorCell) holder.itemView;
                        textCell2.setEnabled(enabled, null);
                        return;
                    case 5:
                        TextSettingsCell textCell3 = (TextSettingsCell) holder.itemView;
                        textCell3.setEnabled(enabled, null);
                        return;
                }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != NotificationsCustomSettingsActivity.this.messageSectionRow) {
                if (position != NotificationsCustomSettingsActivity.this.previewRow) {
                    if (position < NotificationsCustomSettingsActivity.this.exceptionsStartRow || position >= NotificationsCustomSettingsActivity.this.exceptionsEndRow) {
                        if (position != NotificationsCustomSettingsActivity.this.messageLedRow) {
                            if (position != NotificationsCustomSettingsActivity.this.groupSection2Row && position != NotificationsCustomSettingsActivity.this.alertSection2Row && position != NotificationsCustomSettingsActivity.this.exceptionsSection2Row && position != NotificationsCustomSettingsActivity.this.deleteAllSectionRow) {
                                if (position != NotificationsCustomSettingsActivity.this.alertRow) {
                                    if (position == NotificationsCustomSettingsActivity.this.exceptionsAddRow || position == NotificationsCustomSettingsActivity.this.deleteAllRow) {
                                        return 7;
                                    }
                                    return 5;
                                }
                                return 6;
                            }
                            return 4;
                        }
                        return 3;
                    }
                    return 2;
                }
                return 1;
            }
            return 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.NotificationsCustomSettingsActivity$$ExternalSyntheticLambda10
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                NotificationsCustomSettingsActivity.this.m3944x9649d3ce();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCheckCell.class, TextColorCell.class, TextSettingsCell.class, UserCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
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
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_graySectionText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextColorCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueButton));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText5));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueIcon));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$11$org-telegram-ui-NotificationsCustomSettingsActivity */
    public /* synthetic */ void m3944x9649d3ce() {
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
