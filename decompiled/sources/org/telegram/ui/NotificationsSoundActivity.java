package org.telegram.ui;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.util.MimeTypes;
import com.microsoft.appcenter.ingestion.models.CommonProperties;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.messenger.ringtone.RingtoneUploader;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CreationTextCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.NotificationsSoundActivity;
/* loaded from: classes4.dex */
public class NotificationsSoundActivity extends BaseFragment implements ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate, NotificationCenter.NotificationCenterDelegate {
    private static final int deleteId = 1;
    private static final int shareId = 2;
    Adapter adapter;
    ChatAvatarContainer avatarContainer;
    ChatAttachAlert chatAttachAlert;
    int currentType;
    long dialogId;
    int dividerRow;
    int dividerRow2;
    Ringtone lastPlayedRingtone;
    RecyclerListView listView;
    Theme.ResourcesProvider resourcesProvider;
    int rowCount;
    Tone selectedTone;
    boolean selectedToneChanged;
    SparseArray<Tone> selectedTones;
    NumberTextView selectedTonesCountTextView;
    ArrayList<Tone> serverTones;
    int serverTonesEndRow;
    int serverTonesHeaderRow;
    int serverTonesStartRow;
    private int stableIds;
    private Tone startSelectedTone;
    ArrayList<Tone> systemTones;
    int systemTonesEndRow;
    int systemTonesHeaderRow;
    int systemTonesStartRow;
    private final int tonesStreamType;
    int uploadRow;
    ArrayList<Tone> uploadingTones;

    @Override // org.telegram.ui.Components.ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate
    public /* synthetic */ void didSelectPhotos(ArrayList arrayList, boolean z, int i) {
        ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate.CC.$default$didSelectPhotos(this, arrayList, z, i);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate
    public /* synthetic */ void startMusicSelectActivity() {
        ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate.CC.$default$startMusicSelectActivity(this);
    }

    public NotificationsSoundActivity(Bundle args) {
        this(args, null);
    }

    public NotificationsSoundActivity(Bundle args, Theme.ResourcesProvider resourcesProvider) {
        super(args);
        this.serverTones = new ArrayList<>();
        this.systemTones = new ArrayList<>();
        this.uploadingTones = new ArrayList<>();
        this.stableIds = 100;
        this.selectedTones = new SparseArray<>();
        this.currentType = -1;
        this.tonesStreamType = 4;
        this.resourcesProvider = resourcesProvider;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        String prefPath;
        String prefDocId;
        if (getArguments() != null) {
            this.dialogId = getArguments().getLong("dialog_id", 0L);
            this.currentType = getArguments().getInt(CommonProperties.TYPE, -1);
        }
        if (this.dialogId != 0) {
            prefDocId = "sound_document_id_" + this.dialogId;
            prefPath = "sound_path_" + this.dialogId;
        } else {
            int i = this.currentType;
            if (i == 1) {
                prefPath = "GlobalSoundPath";
                prefDocId = "GlobalSoundDocId";
            } else if (i == 0) {
                prefPath = "GroupSoundPath";
                prefDocId = "GroupSoundDocId";
            } else if (i == 2) {
                prefPath = "ChannelSoundPath";
                prefDocId = "ChannelSoundDocId";
            } else {
                throw new RuntimeException("Unsupported type");
            }
        }
        SharedPreferences preferences = getNotificationsSettings();
        long documentId = preferences.getLong(prefDocId, 0L);
        String localUri = preferences.getString(prefPath, "NoSound");
        Tone tone = new Tone(null);
        this.startSelectedTone = tone;
        if (documentId != 0) {
            tone.document = new TLRPC.TL_document();
            this.startSelectedTone.document.id = documentId;
        } else {
            tone.uri = localUri;
        }
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_avatar_actionBarSelectorBlue, this.resourcesProvider), false);
        this.actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon, this.resourcesProvider), false);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass1(context));
        if (this.dialogId == 0) {
            int i = this.currentType;
            if (i == 1) {
                this.actionBar.setTitle(LocaleController.getString("NotificationsSoundPrivate", R.string.NotificationsSoundPrivate));
            } else if (i == 0) {
                this.actionBar.setTitle(LocaleController.getString("NotificationsSoundGroup", R.string.NotificationsSoundGroup));
            } else if (i == 2) {
                this.actionBar.setTitle(LocaleController.getString("NotificationsSoundChannels", R.string.NotificationsSoundChannels));
            }
        } else {
            ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context, null, false, this.resourcesProvider);
            this.avatarContainer = chatAvatarContainer;
            chatAvatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
            this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, !this.inPreviewMode ? 56.0f : 0.0f, 0.0f, 40.0f, 0.0f));
            if (this.dialogId < 0) {
                TLRPC.Chat chatLocal = getMessagesController().getChat(Long.valueOf(-this.dialogId));
                this.avatarContainer.setChatAvatar(chatLocal);
                this.avatarContainer.setTitle(chatLocal.title);
            } else {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.dialogId));
                if (user != null) {
                    this.avatarContainer.setUserAvatar(user);
                    this.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                }
            }
            this.avatarContainer.setSubtitle(LocaleController.getString("NotificationsSound", R.string.NotificationsSound));
        }
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
        this.selectedTonesCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedTonesCountTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.selectedTonesCountTextView.setTextColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon, this.resourcesProvider));
        actionMode.addView(this.selectedTonesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedTonesCountTextView.setOnTouchListener(NotificationsSoundActivity$$ExternalSyntheticLambda0.INSTANCE);
        actionMode.addItemWithWidth(2, R.drawable.msg_forward, AndroidUtilities.dp(54.0f), LocaleController.getString("ShareFile", R.string.ShareFile));
        actionMode.addItemWithWidth(1, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", R.string.Delete));
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray, this.resourcesProvider));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        frameLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
        Adapter adapter = new Adapter(this, null);
        this.adapter = adapter;
        adapter.setHasStableIds(true);
        this.listView.setAdapter(this.adapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setSupportsChangeAnimations(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.NotificationsSoundActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                NotificationsSoundActivity.this.m3961lambda$createView$1$orgtelegramuiNotificationsSoundActivity(context, view, i2);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.NotificationsSoundActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i2) {
                return NotificationsSoundActivity.this.m3962lambda$createView$2$orgtelegramuiNotificationsSoundActivity(view, i2);
            }
        });
        loadTones();
        updateRows();
        return this.fragmentView;
    }

    /* renamed from: org.telegram.ui.NotificationsSoundActivity$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        final /* synthetic */ Context val$context;

        AnonymousClass1(Context context) {
            NotificationsSoundActivity.this = this$0;
            this.val$context = context;
        }

        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        public void onItemClick(int id) {
            if (id == -1) {
                if (NotificationsSoundActivity.this.actionBar.isActionModeShowed()) {
                    NotificationsSoundActivity.this.hideActionMode();
                } else {
                    NotificationsSoundActivity.this.finishFragment();
                }
            } else if (id == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NotificationsSoundActivity.this.getParentActivity(), NotificationsSoundActivity.this.resourcesProvider);
                builder.setTitle(LocaleController.formatPluralString("DeleteTones", NotificationsSoundActivity.this.selectedTones.size(), new Object[0]));
                builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatPluralString("DeleteTonesMessage", NotificationsSoundActivity.this.selectedTones.size(), new Object[0])));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), NotificationsSoundActivity$1$$ExternalSyntheticLambda1.INSTANCE);
                builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.NotificationsSoundActivity$1$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        NotificationsSoundActivity.AnonymousClass1.this.m3963xe3a080d7(dialogInterface, i);
                    }
                });
                AlertDialog dialog = builder.show();
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2, NotificationsSoundActivity.this.resourcesProvider));
                }
            } else if (id == 2) {
                if (NotificationsSoundActivity.this.selectedTones.size() == 1) {
                    Intent intent = new Intent(this.val$context, LaunchActivity.class);
                    intent.setAction("android.intent.action.SEND");
                    Uri uri = NotificationsSoundActivity.this.selectedTones.valueAt(0).getUriForShare(NotificationsSoundActivity.this.currentAccount);
                    if (uri != null) {
                        intent.putExtra("android.intent.extra.STREAM", uri);
                        this.val$context.startActivity(intent);
                    }
                } else {
                    Intent intent2 = new Intent(this.val$context, LaunchActivity.class);
                    intent2.setAction("android.intent.action.SEND_MULTIPLE");
                    ArrayList<Uri> uries = new ArrayList<>();
                    for (int i = 0; i < NotificationsSoundActivity.this.selectedTones.size(); i++) {
                        Uri uri2 = NotificationsSoundActivity.this.selectedTones.valueAt(i).getUriForShare(NotificationsSoundActivity.this.currentAccount);
                        if (uri2 != null) {
                            uries.add(uri2);
                        }
                    }
                    if (!uries.isEmpty()) {
                        intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", uries);
                        this.val$context.startActivity(intent2);
                    }
                }
                NotificationsSoundActivity.this.hideActionMode();
                NotificationsSoundActivity.this.updateRows();
                NotificationsSoundActivity.this.adapter.notifyDataSetChanged();
            }
        }

        /* renamed from: lambda$onItemClick$1$org-telegram-ui-NotificationsSoundActivity$1 */
        public /* synthetic */ void m3963xe3a080d7(DialogInterface dialog, int which) {
            deleteSelectedMessages();
            dialog.dismiss();
        }

        private void deleteSelectedMessages() {
            RingtoneUploader ringtoneUploader;
            ArrayList<TLRPC.Document> documentsToRemove = new ArrayList<>();
            for (int i = 0; i < NotificationsSoundActivity.this.selectedTones.size(); i++) {
                Tone tone = NotificationsSoundActivity.this.selectedTones.valueAt(i);
                if (tone.document != null) {
                    documentsToRemove.add(tone.document);
                    NotificationsSoundActivity.this.getMediaDataController().ringtoneDataStore.remove(tone.document);
                }
                if (tone.uri != null && (ringtoneUploader = NotificationsSoundActivity.this.getMediaDataController().ringtoneUploaderHashMap.get(tone.uri)) != null) {
                    ringtoneUploader.cancel();
                }
                if (tone == NotificationsSoundActivity.this.selectedTone) {
                    NotificationsSoundActivity.this.startSelectedTone = null;
                    NotificationsSoundActivity notificationsSoundActivity = NotificationsSoundActivity.this;
                    notificationsSoundActivity.selectedTone = notificationsSoundActivity.systemTones.get(0);
                    NotificationsSoundActivity.this.selectedToneChanged = true;
                }
                NotificationsSoundActivity.this.serverTones.remove(tone);
                NotificationsSoundActivity.this.uploadingTones.remove(tone);
            }
            NotificationsSoundActivity.this.getMediaDataController().ringtoneDataStore.saveTones();
            for (int i2 = 0; i2 < documentsToRemove.size(); i2++) {
                TLRPC.Document document = documentsToRemove.get(i2);
                TLRPC.TL_account_saveRingtone req = new TLRPC.TL_account_saveRingtone();
                req.id = new TLRPC.TL_inputDocument();
                req.id.id = document.id;
                req.id.access_hash = document.access_hash;
                req.id.file_reference = document.file_reference;
                if (req.id.file_reference == null) {
                    req.id.file_reference = new byte[0];
                }
                req.unsave = true;
                NotificationsSoundActivity.this.getConnectionsManager().sendRequest(req, NotificationsSoundActivity$1$$ExternalSyntheticLambda2.INSTANCE);
            }
            NotificationsSoundActivity.this.hideActionMode();
            NotificationsSoundActivity.this.updateRows();
            NotificationsSoundActivity.this.adapter.notifyDataSetChanged();
        }

        public static /* synthetic */ void lambda$deleteSelectedMessages$2(TLObject response, TLRPC.TL_error error) {
        }
    }

    public static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-NotificationsSoundActivity */
    public /* synthetic */ void m3961lambda$createView$1$orgtelegramuiNotificationsSoundActivity(Context context, View view, int position) {
        if (position == this.uploadRow) {
            ChatAttachAlert chatAttachAlert = new ChatAttachAlert(context, this, false, false, this.resourcesProvider);
            this.chatAttachAlert = chatAttachAlert;
            chatAttachAlert.setSoundPicker();
            this.chatAttachAlert.init();
            this.chatAttachAlert.show();
        }
        if (view instanceof ToneCell) {
            ToneCell cell = (ToneCell) view;
            if (this.actionBar.isActionModeShowed() || cell.tone == null) {
                checkSelection(cell.tone);
                return;
            }
            Ringtone ringtone = this.lastPlayedRingtone;
            if (ringtone != null) {
                ringtone.stop();
            }
            try {
                if (cell.tone.isSystemDefault) {
                    Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), RingtoneManager.getDefaultUri(2));
                    r.setStreamType(4);
                    this.lastPlayedRingtone = r;
                    r.play();
                } else if (cell.tone.uri != null && !cell.tone.fromServer) {
                    Ringtone r2 = RingtoneManager.getRingtone(context.getApplicationContext(), Uri.parse(cell.tone.uri));
                    r2.setStreamType(4);
                    this.lastPlayedRingtone = r2;
                    r2.play();
                } else if (cell.tone.fromServer) {
                    File file = null;
                    if (!TextUtils.isEmpty(cell.tone.uri)) {
                        File localUriFile = new File(cell.tone.uri);
                        if (localUriFile.exists()) {
                            file = localUriFile;
                        }
                    }
                    if (file == null) {
                        file = getFileLoader().getPathToAttach(cell.tone.document);
                    }
                    if (file == null || !file.exists()) {
                        getFileLoader().loadFile(cell.tone.document, cell.tone.document, 2, 0);
                    } else {
                        Ringtone r3 = RingtoneManager.getRingtone(context.getApplicationContext(), Uri.parse(file.toString()));
                        r3.setStreamType(4);
                        this.lastPlayedRingtone = r3;
                        r3.play();
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.startSelectedTone = null;
            this.selectedTone = cell.tone;
            this.selectedToneChanged = true;
            Adapter adapter = this.adapter;
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-NotificationsSoundActivity */
    public /* synthetic */ boolean m3962lambda$createView$2$orgtelegramuiNotificationsSoundActivity(View view, int position) {
        if (view instanceof ToneCell) {
            ToneCell cell = (ToneCell) view;
            checkSelection(cell.tone);
            cell.performHapticFeedback(0);
        }
        return false;
    }

    public void hideActionMode() {
        this.selectedTones.clear();
        Adapter adapter = this.adapter;
        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        updateActionMode();
    }

    private void checkSelection(Tone tone) {
        boolean changed = false;
        if (this.selectedTones.get(tone.stableId) != null) {
            this.selectedTones.remove(tone.stableId);
            changed = true;
        } else if (tone.fromServer) {
            this.selectedTones.put(tone.stableId, tone);
            changed = true;
        }
        if (changed) {
            updateActionMode();
            Adapter adapter = this.adapter;
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
        }
    }

    private void updateActionMode() {
        if (this.selectedTones.size() > 0) {
            this.selectedTonesCountTextView.setNumber(this.selectedTones.size(), this.actionBar.isActionModeShowed());
            this.actionBar.showActionMode();
            return;
        }
        this.actionBar.hideActionMode();
    }

    private void loadTones() {
        getMediaDataController().ringtoneDataStore.m1261lambda$new$0$orgtelegrammessengerringtoneRingtoneDataStore();
        this.serverTones.clear();
        this.systemTones.clear();
        for (int i = 0; i < getMediaDataController().ringtoneDataStore.userRingtones.size(); i++) {
            RingtoneDataStore.CachedTone cachedTone = getMediaDataController().ringtoneDataStore.userRingtones.get(i);
            Tone tone = new Tone(null);
            int i2 = this.stableIds;
            this.stableIds = i2 + 1;
            tone.stableId = i2;
            tone.fromServer = true;
            tone.localId = cachedTone.localId;
            tone.title = cachedTone.document.file_name_fixed;
            tone.document = cachedTone.document;
            trimTitle(tone);
            tone.uri = cachedTone.localUri;
            Tone tone2 = this.startSelectedTone;
            if (tone2 != null && tone2.document != null && cachedTone.document != null && this.startSelectedTone.document.id == cachedTone.document.id) {
                this.startSelectedTone = null;
                this.selectedTone = tone;
            }
            this.serverTones.add(tone);
        }
        RingtoneManager manager = new RingtoneManager(ApplicationLoader.applicationContext);
        manager.setType(2);
        Cursor cursor = manager.getCursor();
        Tone noSoundTone = new Tone(null);
        int i3 = this.stableIds;
        this.stableIds = i3 + 1;
        noSoundTone.stableId = i3;
        noSoundTone.title = LocaleController.getString("NoSound", R.string.NoSound);
        noSoundTone.isSystemNoSound = true;
        this.systemTones.add(noSoundTone);
        Tone defaultTone = new Tone(null);
        int i4 = this.stableIds;
        this.stableIds = i4 + 1;
        defaultTone.stableId = i4;
        defaultTone.title = LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone);
        defaultTone.isSystemDefault = true;
        this.systemTones.add(defaultTone);
        Tone tone3 = this.startSelectedTone;
        if (tone3 != null && tone3.document == null && this.startSelectedTone.uri.equals("NoSound")) {
            this.startSelectedTone = null;
            this.selectedTone = noSoundTone;
        }
        Tone tone4 = this.startSelectedTone;
        if (tone4 != null && tone4.document == null && this.startSelectedTone.uri.equals("Default")) {
            this.startSelectedTone = null;
            this.selectedTone = defaultTone;
        }
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(1);
            String notificationUri = cursor.getString(2) + "/" + cursor.getString(0);
            Tone tone5 = new Tone(null);
            int i5 = this.stableIds;
            this.stableIds = i5 + 1;
            tone5.stableId = i5;
            tone5.title = notificationTitle;
            tone5.uri = notificationUri;
            Tone tone6 = this.startSelectedTone;
            if (tone6 != null && tone6.document == null && this.startSelectedTone.uri.equals(notificationUri)) {
                this.startSelectedTone = null;
                this.selectedTone = tone5;
            }
            this.systemTones.add(tone5);
        }
        if (getMediaDataController().ringtoneDataStore.isLoaded() && this.selectedTone == null) {
            this.selectedTone = defaultTone;
            this.selectedToneChanged = true;
        }
        updateRows();
    }

    public void updateRows() {
        this.serverTonesHeaderRow = -1;
        this.serverTonesStartRow = -1;
        this.serverTonesEndRow = -1;
        this.uploadRow = -1;
        this.dividerRow = -1;
        this.systemTonesHeaderRow = -1;
        this.systemTonesStartRow = -1;
        this.systemTonesEndRow = -1;
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.serverTonesHeaderRow = 0;
        if (!this.serverTones.isEmpty()) {
            int i = this.rowCount;
            this.serverTonesStartRow = i;
            int size = i + this.serverTones.size();
            this.rowCount = size;
            this.serverTonesEndRow = size;
        }
        int i2 = this.rowCount;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.uploadRow = i2;
        this.rowCount = i3 + 1;
        this.dividerRow = i3;
        if (!this.systemTones.isEmpty()) {
            int i4 = this.rowCount;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.systemTonesHeaderRow = i4;
            this.systemTonesStartRow = i5;
            int size2 = i5 + this.systemTones.size();
            this.rowCount = size2;
            this.systemTonesEndRow = size2;
        }
        int i6 = this.rowCount;
        this.rowCount = i6 + 1;
        this.dividerRow2 = i6;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate
    public void didSelectFiles(ArrayList<String> files, String caption, ArrayList<MessageObject> fmessages, boolean notify, int scheduleDate) {
        for (int i = 0; i < files.size(); i++) {
            getMediaDataController().uploadRingtone(files.get(i));
        }
        getNotificationCenter().postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
    }

    /* loaded from: classes4.dex */
    public class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
            NotificationsSoundActivity.this = r1;
        }

        /* synthetic */ Adapter(NotificationsSoundActivity x0, AnonymousClass1 x1) {
            this();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int position) {
            Tone tone = getTone(position);
            if (tone != null) {
                return tone.stableId;
            }
            if (position == NotificationsSoundActivity.this.serverTonesHeaderRow) {
                return 1L;
            }
            if (position == NotificationsSoundActivity.this.systemTonesHeaderRow) {
                return 2L;
            }
            if (position == NotificationsSoundActivity.this.uploadRow) {
                return 3L;
            }
            if (position == NotificationsSoundActivity.this.dividerRow) {
                return 4L;
            }
            if (position == NotificationsSoundActivity.this.dividerRow2) {
                return 5L;
            }
            throw new RuntimeException();
        }

        private Tone getTone(int position) {
            if (position >= NotificationsSoundActivity.this.systemTonesStartRow && position < NotificationsSoundActivity.this.systemTonesEndRow) {
                return NotificationsSoundActivity.this.systemTones.get(position - NotificationsSoundActivity.this.systemTonesStartRow);
            }
            if (position >= NotificationsSoundActivity.this.serverTonesStartRow && position < NotificationsSoundActivity.this.serverTonesEndRow) {
                return NotificationsSoundActivity.this.serverTones.get(position - NotificationsSoundActivity.this.serverTonesStartRow);
            }
            return null;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            switch (viewType) {
                case 0:
                    View view2 = new ToneCell(context, NotificationsSoundActivity.this.resourcesProvider);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite, NotificationsSoundActivity.this.resourcesProvider));
                    view = view2;
                    break;
                case 1:
                default:
                    View view3 = new HeaderCell(context, NotificationsSoundActivity.this.resourcesProvider);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite, NotificationsSoundActivity.this.resourcesProvider));
                    view = view3;
                    break;
                case 2:
                    CreationTextCell creationTextCell = new CreationTextCell(context, NotificationsSoundActivity.this.resourcesProvider);
                    creationTextCell.startPadding = 61;
                    creationTextCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite, NotificationsSoundActivity.this.resourcesProvider));
                    view = creationTextCell;
                    break;
                case 3:
                    view = new ShadowSectionCell(context, NotificationsSoundActivity.this.resourcesProvider);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    ToneCell toneCell = (ToneCell) holder.itemView;
                    Tone tone = null;
                    if (position >= NotificationsSoundActivity.this.systemTonesStartRow && position < NotificationsSoundActivity.this.systemTonesEndRow) {
                        tone = NotificationsSoundActivity.this.systemTones.get(position - NotificationsSoundActivity.this.systemTonesStartRow);
                    }
                    if (position >= NotificationsSoundActivity.this.serverTonesStartRow && position < NotificationsSoundActivity.this.serverTonesEndRow) {
                        tone = NotificationsSoundActivity.this.serverTones.get(position - NotificationsSoundActivity.this.serverTonesStartRow);
                    }
                    if (tone != null) {
                        boolean animated = toneCell.tone == tone;
                        boolean checked = tone == NotificationsSoundActivity.this.selectedTone;
                        boolean selected = NotificationsSoundActivity.this.selectedTones.get(tone.stableId) != null;
                        toneCell.tone = tone;
                        toneCell.textView.setText(tone.title);
                        if (position != NotificationsSoundActivity.this.systemTonesEndRow - 1) {
                            z = true;
                        }
                        toneCell.needDivider = z;
                        toneCell.radioButton.setChecked(checked, animated);
                        toneCell.checkBox.setChecked(selected, animated);
                        return;
                    }
                    return;
                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == NotificationsSoundActivity.this.serverTonesHeaderRow) {
                        headerCell.setText(LocaleController.getString("TelegramTones", R.string.TelegramTones));
                        return;
                    } else if (position == NotificationsSoundActivity.this.systemTonesHeaderRow) {
                        headerCell.setText(LocaleController.getString("SystemTones", R.string.SystemTones));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    CreationTextCell textCell = (CreationTextCell) holder.itemView;
                    Drawable drawable1 = textCell.getContext().getResources().getDrawable(R.drawable.poll_add_circle);
                    Drawable drawable2 = textCell.getContext().getResources().getDrawable(R.drawable.poll_add_plus);
                    drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_switchTrackChecked, NotificationsSoundActivity.this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
                    drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_checkboxCheck, NotificationsSoundActivity.this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(drawable1, drawable2);
                    textCell.setTextAndIcon(LocaleController.getString("UploadSound", R.string.UploadSound), combinedDrawable, false);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position >= NotificationsSoundActivity.this.systemTonesStartRow && position < NotificationsSoundActivity.this.systemTonesEndRow) {
                return 0;
            }
            if (position == NotificationsSoundActivity.this.serverTonesHeaderRow || position == NotificationsSoundActivity.this.systemTonesHeaderRow) {
                return 1;
            }
            if (position == NotificationsSoundActivity.this.uploadRow) {
                return 2;
            }
            if (position == NotificationsSoundActivity.this.dividerRow || position == NotificationsSoundActivity.this.dividerRow2) {
                return 3;
            }
            return super.getItemViewType(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return NotificationsSoundActivity.this.rowCount;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0 || holder.getItemViewType() == 2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class ToneCell extends FrameLayout {
        private CheckBox2 checkBox;
        private boolean needDivider;
        private RadioButton radioButton;
        private TextView textView;
        Tone tone;
        public TextView valueTextView;

        public ToneCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            RadioButton radioButton = new RadioButton(context);
            this.radioButton = radioButton;
            radioButton.setSize(AndroidUtilities.dp(20.0f));
            this.radioButton.setColor(Theme.getColor(Theme.key_radioBackground, resourcesProvider), Theme.getColor(Theme.key_radioBackgroundChecked, resourcesProvider));
            int i = 5;
            addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0 : 20, 0.0f, !LocaleController.isRTL ? 0 : 20, 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 24, resourcesProvider);
            this.checkBox = checkBox2;
            checkBox2.setColor(null, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(26, 26.0f, (LocaleController.isRTL ? 5 : 3) | 16, LocaleController.isRTL ? 0 : 18, 0.0f, !LocaleController.isRTL ? 0 : 18, 0.0f));
            this.checkBox.setChecked(true, false);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 16, LocaleController.isRTL ? 23 : 61, 0.0f, LocaleController.isRTL ? 61 : 23, 0.0f));
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), C.BUFFER_FLAG_ENCRYPTED));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                float f = 0.0f;
                float dp = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 60.0f);
                float height = getHeight() - 1;
                int measuredWidth = getMeasuredWidth();
                if (LocaleController.isRTL) {
                    f = 60.0f;
                }
                canvas.drawLine(dp, height, measuredWidth - AndroidUtilities.dp(f), getHeight() - 1, Theme.dividerPaint);
            }
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName("android.widget.RadioButton");
            info.setCheckable(true);
            info.setChecked(this.radioButton.isChecked());
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        getNotificationCenter().addObserver(this, NotificationCenter.onUserRingtonesUpdated);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        getNotificationCenter().removeObserver(this, NotificationCenter.onUserRingtonesUpdated);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public int getNavigationBarColor() {
        return getThemedColor(Theme.key_windowBackgroundGray);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.onUserRingtonesUpdated) {
            HashMap<Integer, Tone> currentTones = new HashMap<>();
            for (int i = 0; i < this.serverTones.size(); i++) {
                currentTones.put(Integer.valueOf(this.serverTones.get(i).localId), this.serverTones.get(i));
            }
            this.serverTones.clear();
            for (int i2 = 0; i2 < getMediaDataController().ringtoneDataStore.userRingtones.size(); i2++) {
                RingtoneDataStore.CachedTone cachedTone = getMediaDataController().ringtoneDataStore.userRingtones.get(i2);
                Tone tone = new Tone(null);
                Tone currentTone = currentTones.get(Integer.valueOf(cachedTone.localId));
                if (currentTone != null) {
                    if (currentTone == this.selectedTone) {
                        this.selectedTone = tone;
                    }
                    tone.stableId = currentTone.stableId;
                } else {
                    int i3 = this.stableIds;
                    this.stableIds = i3 + 1;
                    tone.stableId = i3;
                }
                tone.fromServer = true;
                tone.localId = cachedTone.localId;
                if (cachedTone.document != null) {
                    tone.title = cachedTone.document.file_name_fixed;
                } else {
                    tone.title = new File(cachedTone.localUri).getName();
                }
                tone.document = cachedTone.document;
                trimTitle(tone);
                tone.uri = cachedTone.localUri;
                Tone tone2 = this.startSelectedTone;
                if (tone2 != null && tone2.document != null && cachedTone.document != null && this.startSelectedTone.document.id == cachedTone.document.id) {
                    this.startSelectedTone = null;
                    this.selectedTone = tone;
                }
                this.serverTones.add(tone);
            }
            updateRows();
            this.adapter.notifyDataSetChanged();
            if (getMediaDataController().ringtoneDataStore.isLoaded() && this.selectedTone == null && this.systemTones.size() > 0) {
                this.startSelectedTone = null;
                this.selectedTone = this.systemTones.get(0);
            }
        }
    }

    private void trimTitle(Tone tone) {
        tone.title = trimTitle(tone.document, tone.title);
    }

    public static String trimTitle(TLRPC.Document document, String title) {
        int idx;
        if (title != null && (idx = title.lastIndexOf(46)) != -1) {
            title = title.substring(0, idx);
        }
        if (TextUtils.isEmpty(title) && document != null) {
            return LocaleController.formatString("SoundNameEmpty", R.string.SoundNameEmpty, LocaleController.formatDateChat(document.date, true));
        }
        return title;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        String prefDocId;
        String prefPath;
        String prefName;
        super.onFragmentDestroy();
        if (this.selectedTone != null && this.selectedToneChanged) {
            SharedPreferences preferences = getNotificationsSettings();
            SharedPreferences.Editor editor = preferences.edit();
            if (this.dialogId != 0) {
                prefName = "sound_" + this.dialogId;
                prefPath = "sound_path_" + this.dialogId;
                prefDocId = "sound_document_id_" + this.dialogId;
                editor.putBoolean("sound_enabled_" + this.dialogId, true);
            } else {
                int i = this.currentType;
                if (i == 1) {
                    prefName = "GlobalSound";
                    prefPath = "GlobalSoundPath";
                    prefDocId = "GlobalSoundDocId";
                } else if (i == 0) {
                    prefName = "GroupSound";
                    prefPath = "GroupSoundPath";
                    prefDocId = "GroupSoundDocId";
                } else if (i == 2) {
                    prefName = "ChannelSound";
                    prefPath = "ChannelSoundPath";
                    prefDocId = "ChannelSoundDocId";
                } else {
                    throw new RuntimeException("Unsupported type");
                }
            }
            if (this.selectedTone.fromServer && this.selectedTone.document != null) {
                editor.putLong(prefDocId, this.selectedTone.document.id);
                editor.putString(prefName, this.selectedTone.title);
                editor.putString(prefPath, "NoSound");
            } else if (this.selectedTone.uri != null) {
                editor.putString(prefName, this.selectedTone.title);
                editor.putString(prefPath, this.selectedTone.uri);
                editor.remove(prefDocId);
            } else if (this.selectedTone.isSystemDefault) {
                editor.putString(prefName, "Default");
                editor.putString(prefPath, "Default");
                editor.remove(prefDocId);
            } else {
                editor.putString(prefName, "NoSound");
                editor.putString(prefPath, "NoSound");
                editor.remove(prefDocId);
            }
            editor.apply();
            if (this.dialogId != 0) {
                getNotificationsController().updateServerNotificationsSettings(this.dialogId);
                return;
            }
            getNotificationsController().updateServerNotificationsSettings(this.currentType);
            getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate
    public void startDocumentSelectActivity() {
        try {
            Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
            if (Build.VERSION.SDK_INT >= 18) {
                photoPickerIntent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
            }
            photoPickerIntent.setType(MimeTypes.AUDIO_MPEG);
            startActivityForResult(photoPickerIntent, 21);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == 21 && data != null && this.chatAttachAlert != null) {
            boolean apply = false;
            if (data.getData() != null) {
                String path = AndroidUtilities.getPath(data.getData());
                if (path != null) {
                    File file = new File(path);
                    if (this.chatAttachAlert.getDocumentLayout().isRingtone(file)) {
                        apply = true;
                        getMediaDataController().uploadRingtone(path);
                        getNotificationCenter().postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
                    }
                }
            } else if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    String path2 = clipData.getItemAt(i).getUri().toString();
                    if (this.chatAttachAlert.getDocumentLayout().isRingtone(new File(path2))) {
                        apply = true;
                        getMediaDataController().uploadRingtone(path2);
                        getNotificationCenter().postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
                    }
                }
            }
            if (apply) {
                this.chatAttachAlert.dismiss();
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class Tone {
        TLRPC.Document document;
        public boolean fromServer;
        boolean isSystemDefault;
        boolean isSystemNoSound;
        int localId;
        int stableId;
        String title;
        String uri;

        private Tone() {
        }

        /* synthetic */ Tone(AnonymousClass1 x0) {
            this();
        }

        public Uri getUriForShare(int currentAccount) {
            if (!TextUtils.isEmpty(this.uri)) {
                return Uri.fromFile(new File(this.uri));
            }
            TLRPC.Document document = this.document;
            if (document != null) {
                String fileName = document.file_name_fixed;
                String ext = FileLoader.getDocumentExtension(this.document);
                if (ext != null) {
                    String ext2 = ext.toLowerCase();
                    if (!fileName.endsWith(ext2)) {
                        fileName = fileName + "." + ext2;
                    }
                    File file = new File(AndroidUtilities.getCacheDir(), fileName);
                    if (!file.exists()) {
                        try {
                            AndroidUtilities.copyFile(FileLoader.getInstance(currentAccount).getPathToAttach(this.document), file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return Uri.fromFile(file);
                }
                return null;
            }
            return null;
        }
    }
}
