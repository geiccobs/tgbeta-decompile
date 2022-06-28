package org.telegram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.appindexing.builders.TimerBuilder;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;
/* loaded from: classes4.dex */
public class DataSettingsActivity extends BaseFragment {
    private int autoplayGifsRow;
    private int autoplayHeaderRow;
    private int autoplaySectionRow;
    private int autoplayVideoRow;
    private int callsSection2Row;
    private int callsSectionRow;
    private int clearDraftsRow;
    private int clearDraftsSectionRow;
    private int dataUsageRow;
    private int enableAllStreamInfoRow;
    private int enableAllStreamRow;
    private int enableCacheStreamRow;
    private int enableMkvRow;
    private int enableStreamRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int mediaDownloadSection2Row;
    private int mediaDownloadSectionRow;
    private int mobileRow;
    private int proxyRow;
    private int proxySection2Row;
    private int proxySectionRow;
    private int quickRepliesRow;
    private int resetDownloadRow;
    private int roamingRow;
    private int rowCount;
    private int saveToGalleryChannelsRow;
    private int saveToGalleryDividerRow;
    private int saveToGalleryGroupsRow;
    private int saveToGalleryPeerRow;
    private int saveToGallerySectionRow;
    private ArrayList<File> storageDirs;
    private int storageNumRow;
    private int storageUsageRow;
    private int streamSectionRow;
    private int usageSection2Row;
    private int usageSectionRow;
    private int useLessDataForCallsRow;
    private int wifiRow;

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        DownloadController.getInstance(this.currentAccount).loadAutoDownloadConfig(true);
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.usageSectionRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.storageUsageRow = i;
        this.rowCount = i2 + 1;
        this.dataUsageRow = i2;
        this.storageNumRow = -1;
        if (Build.VERSION.SDK_INT >= 19) {
            ArrayList<File> rootDirs = AndroidUtilities.getRootDirs();
            this.storageDirs = rootDirs;
            if (rootDirs.size() > 1) {
                int i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.storageNumRow = i3;
            }
        }
        int i4 = this.rowCount;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.usageSection2Row = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.mediaDownloadSectionRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.mobileRow = i6;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.wifiRow = i7;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.roamingRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.resetDownloadRow = i9;
        int i11 = i10 + 1;
        this.rowCount = i11;
        this.mediaDownloadSection2Row = i10;
        int i12 = i11 + 1;
        this.rowCount = i12;
        this.saveToGallerySectionRow = i11;
        int i13 = i12 + 1;
        this.rowCount = i13;
        this.saveToGalleryPeerRow = i12;
        int i14 = i13 + 1;
        this.rowCount = i14;
        this.saveToGalleryGroupsRow = i13;
        int i15 = i14 + 1;
        this.rowCount = i15;
        this.saveToGalleryChannelsRow = i14;
        int i16 = i15 + 1;
        this.rowCount = i16;
        this.saveToGalleryDividerRow = i15;
        int i17 = i16 + 1;
        this.rowCount = i17;
        this.autoplayHeaderRow = i16;
        int i18 = i17 + 1;
        this.rowCount = i18;
        this.autoplayGifsRow = i17;
        int i19 = i18 + 1;
        this.rowCount = i19;
        this.autoplayVideoRow = i18;
        int i20 = i19 + 1;
        this.rowCount = i20;
        this.autoplaySectionRow = i19;
        int i21 = i20 + 1;
        this.rowCount = i21;
        this.streamSectionRow = i20;
        this.rowCount = i21 + 1;
        this.enableStreamRow = i21;
        if (BuildVars.DEBUG_VERSION) {
            int i22 = this.rowCount;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.enableMkvRow = i22;
            this.rowCount = i23 + 1;
            this.enableAllStreamRow = i23;
        } else {
            this.enableAllStreamRow = -1;
            this.enableMkvRow = -1;
        }
        int i24 = this.rowCount;
        int i25 = i24 + 1;
        this.rowCount = i25;
        this.enableAllStreamInfoRow = i24;
        this.enableCacheStreamRow = -1;
        int i26 = i25 + 1;
        this.rowCount = i26;
        this.callsSectionRow = i25;
        int i27 = i26 + 1;
        this.rowCount = i27;
        this.useLessDataForCallsRow = i26;
        int i28 = i27 + 1;
        this.rowCount = i28;
        this.quickRepliesRow = i27;
        int i29 = i28 + 1;
        this.rowCount = i29;
        this.callsSection2Row = i28;
        int i30 = i29 + 1;
        this.rowCount = i30;
        this.proxySectionRow = i29;
        int i31 = i30 + 1;
        this.rowCount = i31;
        this.proxyRow = i30;
        int i32 = i31 + 1;
        this.rowCount = i32;
        this.proxySection2Row = i31;
        int i33 = i32 + 1;
        this.rowCount = i33;
        this.clearDraftsRow = i32;
        this.rowCount = i33 + 1;
        this.clearDraftsSectionRow = i33;
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString("DataSettings", R.string.DataSettings));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.DataSettingsActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    DataSettingsActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda6
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
                DataSettingsActivity.this.m3310lambda$createView$6$orgtelegramuiDataSettingsActivity(context, view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-DataSettingsActivity */
    public /* synthetic */ void m3310lambda$createView$6$orgtelegramuiDataSettingsActivity(Context context, View view, final int position, float x, float y) {
        int flag;
        int num;
        String key2;
        String key;
        DownloadController.Preset defaultPreset;
        DownloadController.Preset preset;
        int type;
        int i = this.saveToGalleryGroupsRow;
        boolean z = false;
        if (position != i && position != this.saveToGalleryChannelsRow) {
            if (position != this.saveToGalleryPeerRow) {
                if (position != this.mobileRow && position != this.roamingRow) {
                    if (position != this.wifiRow) {
                        if (position == this.resetDownloadRow) {
                            if (getParentActivity() == null || !view.isEnabled()) {
                                return;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("ResetAutomaticMediaDownloadAlertTitle", R.string.ResetAutomaticMediaDownloadAlertTitle));
                            builder.setMessage(LocaleController.getString("ResetAutomaticMediaDownloadAlert", R.string.ResetAutomaticMediaDownloadAlert));
                            builder.setPositiveButton(LocaleController.getString(TimerBuilder.RESET, R.string.Reset), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda0
                                @Override // android.content.DialogInterface.OnClickListener
                                public final void onClick(DialogInterface dialogInterface, int i2) {
                                    DataSettingsActivity.this.m3304lambda$createView$0$orgtelegramuiDataSettingsActivity(dialogInterface, i2);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            AlertDialog dialog = builder.create();
                            showDialog(dialog);
                            TextView button = (TextView) dialog.getButton(-1);
                            if (button != null) {
                                button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                            }
                            return;
                        } else if (position == this.storageUsageRow) {
                            presentFragment(new CacheControlActivity());
                            return;
                        } else if (position != this.useLessDataForCallsRow) {
                            if (position == this.dataUsageRow) {
                                presentFragment(new DataUsageActivity());
                                return;
                            } else if (position != this.storageNumRow) {
                                if (position == this.proxyRow) {
                                    presentFragment(new ProxyListActivity());
                                    return;
                                } else if (position != this.enableStreamRow) {
                                    if (position != this.enableAllStreamRow) {
                                        if (position != this.enableMkvRow) {
                                            if (position != this.enableCacheStreamRow) {
                                                if (position == this.quickRepliesRow) {
                                                    presentFragment(new QuickRepliesSettingsActivity());
                                                    return;
                                                } else if (position == this.autoplayGifsRow) {
                                                    SharedConfig.toggleAutoplayGifs();
                                                    if (view instanceof TextCheckCell) {
                                                        ((TextCheckCell) view).setChecked(SharedConfig.autoplayGifs);
                                                        return;
                                                    }
                                                    return;
                                                } else if (position == this.autoplayVideoRow) {
                                                    SharedConfig.toggleAutoplayVideo();
                                                    if (view instanceof TextCheckCell) {
                                                        ((TextCheckCell) view).setChecked(SharedConfig.autoplayVideo);
                                                        return;
                                                    }
                                                    return;
                                                } else if (position == this.clearDraftsRow) {
                                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                                                    builder2.setTitle(LocaleController.getString("AreYouSureClearDraftsTitle", R.string.AreYouSureClearDraftsTitle));
                                                    builder2.setMessage(LocaleController.getString("AreYouSureClearDrafts", R.string.AreYouSureClearDrafts));
                                                    builder2.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda1
                                                        @Override // android.content.DialogInterface.OnClickListener
                                                        public final void onClick(DialogInterface dialogInterface, int i2) {
                                                            DataSettingsActivity.this.m3309lambda$createView$5$orgtelegramuiDataSettingsActivity(dialogInterface, i2);
                                                        }
                                                    });
                                                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                                    AlertDialog alertDialog = builder2.create();
                                                    showDialog(alertDialog);
                                                    TextView button2 = (TextView) alertDialog.getButton(-1);
                                                    if (button2 != null) {
                                                        button2.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                                                        return;
                                                    }
                                                    return;
                                                } else {
                                                    return;
                                                }
                                            }
                                            SharedConfig.toggleSaveStreamMedia();
                                            TextCheckCell textCheckCell = (TextCheckCell) view;
                                            textCheckCell.setChecked(SharedConfig.saveStreamMedia);
                                            return;
                                        }
                                        SharedConfig.toggleStreamMkv();
                                        TextCheckCell textCheckCell2 = (TextCheckCell) view;
                                        textCheckCell2.setChecked(SharedConfig.streamMkv);
                                        return;
                                    }
                                    SharedConfig.toggleStreamAllVideo();
                                    TextCheckCell textCheckCell3 = (TextCheckCell) view;
                                    textCheckCell3.setChecked(SharedConfig.streamAllVideo);
                                    return;
                                } else {
                                    SharedConfig.toggleStreamMedia();
                                    TextCheckCell textCheckCell4 = (TextCheckCell) view;
                                    textCheckCell4.setChecked(SharedConfig.streamMedia);
                                    return;
                                }
                            } else {
                                final AlertDialog.Builder builder3 = new AlertDialog.Builder(getParentActivity());
                                builder3.setTitle(LocaleController.getString("StoragePath", R.string.StoragePath));
                                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                                linearLayout.setOrientation(1);
                                builder3.setView(linearLayout);
                                String dir = this.storageDirs.get(0).getAbsolutePath();
                                if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                                    int a = 0;
                                    int N = this.storageDirs.size();
                                    while (true) {
                                        if (a >= N) {
                                            break;
                                        }
                                        String path = this.storageDirs.get(a).getAbsolutePath();
                                        if (!path.startsWith(SharedConfig.storageCacheDir)) {
                                            a++;
                                        } else {
                                            dir = path;
                                            break;
                                        }
                                    }
                                }
                                int N2 = this.storageDirs.size();
                                for (int a2 = 0; a2 < N2; a2++) {
                                    final String storageDir = this.storageDirs.get(a2).getAbsolutePath();
                                    RadioColorCell cell = new RadioColorCell(context);
                                    cell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                                    cell.setTag(Integer.valueOf(a2));
                                    cell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
                                    cell.setTextAndValue(storageDir, storageDir.startsWith(dir));
                                    linearLayout.addView(cell);
                                    cell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda3
                                        @Override // android.view.View.OnClickListener
                                        public final void onClick(View view2) {
                                            DataSettingsActivity.this.m3306lambda$createView$2$orgtelegramuiDataSettingsActivity(storageDir, builder3, view2);
                                        }
                                    });
                                }
                                builder3.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                showDialog(builder3.create());
                                return;
                            }
                        } else {
                            final SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                            int selected = 0;
                            switch (preferences.getInt("VoipDataSaving", VoIPHelper.getDataSavingDefault())) {
                                case 0:
                                    selected = 0;
                                    break;
                                case 1:
                                    selected = 2;
                                    break;
                                case 2:
                                    selected = 3;
                                    break;
                                case 3:
                                    selected = 1;
                                    break;
                            }
                            Dialog dlg = AlertsCreator.createSingleChoiceDialog(getParentActivity(), new String[]{LocaleController.getString("UseLessDataNever", R.string.UseLessDataNever), LocaleController.getString("UseLessDataOnRoaming", R.string.UseLessDataOnRoaming), LocaleController.getString("UseLessDataOnMobile", R.string.UseLessDataOnMobile), LocaleController.getString("UseLessDataAlways", R.string.UseLessDataAlways)}, LocaleController.getString("VoipUseLessData", R.string.VoipUseLessData), selected, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda2
                                @Override // android.content.DialogInterface.OnClickListener
                                public final void onClick(DialogInterface dialogInterface, int i2) {
                                    DataSettingsActivity.this.m3305lambda$createView$1$orgtelegramuiDataSettingsActivity(preferences, position, dialogInterface, i2);
                                }
                            });
                            setVisibleDialog(dlg);
                            dlg.show();
                            return;
                        }
                    }
                }
                if ((LocaleController.isRTL && x <= AndroidUtilities.dp(76.0f)) || (!LocaleController.isRTL && x >= view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))) {
                    boolean wasEnabled = this.listAdapter.isRowEnabled(this.resetDownloadRow);
                    NotificationsCheckCell cell2 = (NotificationsCheckCell) view;
                    boolean checked = cell2.isChecked();
                    if (position == this.mobileRow) {
                        preset = DownloadController.getInstance(this.currentAccount).mobilePreset;
                        defaultPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
                        key = "mobilePreset";
                        key2 = "currentMobilePreset";
                        num = 0;
                    } else if (position == this.wifiRow) {
                        preset = DownloadController.getInstance(this.currentAccount).wifiPreset;
                        defaultPreset = DownloadController.getInstance(this.currentAccount).highPreset;
                        key = "wifiPreset";
                        key2 = "currentWifiPreset";
                        num = 1;
                    } else {
                        preset = DownloadController.getInstance(this.currentAccount).roamingPreset;
                        defaultPreset = DownloadController.getInstance(this.currentAccount).lowPreset;
                        key = "roamingPreset";
                        key2 = "currentRoamingPreset";
                        num = 2;
                    }
                    if (!checked && preset.enabled) {
                        preset.set(defaultPreset);
                    } else {
                        preset.enabled = true ^ preset.enabled;
                    }
                    SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
                    editor.putString(key, preset.toString());
                    editor.putInt(key2, 3);
                    editor.commit();
                    cell2.setChecked(!checked);
                    RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(view);
                    if (holder != null) {
                        this.listAdapter.onBindViewHolder(holder, position);
                    }
                    DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
                    DownloadController.getInstance(this.currentAccount).savePresetToServer(num);
                    if (wasEnabled != this.listAdapter.isRowEnabled(this.resetDownloadRow)) {
                        this.listAdapter.notifyItemChanged(this.resetDownloadRow);
                        return;
                    }
                    return;
                }
                if (position == this.mobileRow) {
                    type = 0;
                } else {
                    int type2 = this.wifiRow;
                    if (position == type2) {
                        type = 1;
                    } else {
                        type = 2;
                    }
                }
                presentFragment(new DataAutoDownloadActivity(type));
                return;
            }
        }
        if (position == i) {
            flag = 2;
        } else if (position == this.saveToGalleryChannelsRow) {
            flag = 4;
        } else {
            flag = 1;
        }
        SharedConfig.toggleSaveToGalleryFlag(flag);
        TextCheckCell textCheckCell5 = (TextCheckCell) view;
        if ((SharedConfig.saveToGalleryFlags & flag) != 0) {
            z = true;
        }
        textCheckCell5.setChecked(z);
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-DataSettingsActivity */
    public /* synthetic */ void m3304lambda$createView$0$orgtelegramuiDataSettingsActivity(DialogInterface dialogInterface, int i) {
        String key;
        DownloadController.Preset defaultPreset;
        DownloadController.Preset preset;
        SharedPreferences.Editor editor = MessagesController.getMainSettings(this.currentAccount).edit();
        for (int a = 0; a < 3; a++) {
            if (a == 0) {
                preset = DownloadController.getInstance(this.currentAccount).mobilePreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).mediumPreset;
                key = "mobilePreset";
            } else if (a == 1) {
                preset = DownloadController.getInstance(this.currentAccount).wifiPreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).highPreset;
                key = "wifiPreset";
            } else {
                preset = DownloadController.getInstance(this.currentAccount).roamingPreset;
                defaultPreset = DownloadController.getInstance(this.currentAccount).lowPreset;
                key = "roamingPreset";
            }
            preset.set(defaultPreset);
            preset.enabled = defaultPreset.isEnabled();
            DownloadController.getInstance(this.currentAccount).currentMobilePreset = 3;
            editor.putInt("currentMobilePreset", 3);
            DownloadController.getInstance(this.currentAccount).currentWifiPreset = 3;
            editor.putInt("currentWifiPreset", 3);
            DownloadController.getInstance(this.currentAccount).currentRoamingPreset = 3;
            editor.putInt("currentRoamingPreset", 3);
            editor.putString(key, preset.toString());
        }
        editor.commit();
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
        for (int a2 = 0; a2 < 3; a2++) {
            DownloadController.getInstance(this.currentAccount).savePresetToServer(a2);
        }
        this.listAdapter.notifyItemRangeChanged(this.mobileRow, 4);
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-DataSettingsActivity */
    public /* synthetic */ void m3305lambda$createView$1$orgtelegramuiDataSettingsActivity(SharedPreferences preferences, int position, DialogInterface dialog, int which) {
        int val = -1;
        switch (which) {
            case 0:
                val = 0;
                break;
            case 1:
                val = 3;
                break;
            case 2:
                val = 1;
                break;
            case 3:
                val = 2;
                break;
        }
        if (val != -1) {
            preferences.edit().putInt("VoipDataSaving", val).commit();
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(position);
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-DataSettingsActivity */
    public /* synthetic */ void m3306lambda$createView$2$orgtelegramuiDataSettingsActivity(String storageDir, AlertDialog.Builder builder, View v) {
        SharedConfig.storageCacheDir = storageDir;
        SharedConfig.saveConfig();
        ImageLoader.getInstance().checkMediaPaths();
        builder.getDismissRunnable().run();
        this.listAdapter.notifyItemChanged(this.storageNumRow);
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-DataSettingsActivity */
    public /* synthetic */ void m3309lambda$createView$5$orgtelegramuiDataSettingsActivity(DialogInterface dialogInterface, int i) {
        TLRPC.TL_messages_clearAllDrafts req = new TLRPC.TL_messages_clearAllDrafts();
        getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                DataSettingsActivity.this.m3308lambda$createView$4$orgtelegramuiDataSettingsActivity(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-DataSettingsActivity */
    public /* synthetic */ void m3307lambda$createView$3$orgtelegramuiDataSettingsActivity() {
        getMediaDataController().clearAllDrafts(true);
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-DataSettingsActivity */
    public /* synthetic */ void m3308lambda$createView$4$orgtelegramuiDataSettingsActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DataSettingsActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                DataSettingsActivity.this.m3307lambda$createView$3$orgtelegramuiDataSettingsActivity();
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onDialogDismiss(Dialog dialog) {
        DownloadController.getInstance(this.currentAccount).checkAutodownloadSettings();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            DataSettingsActivity.this = r1;
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return DataSettingsActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean enabled;
            String text;
            DownloadController.Preset preset;
            String str;
            boolean z = false;
            boolean z2 = true;
            switch (holder.getItemViewType()) {
                case 0:
                    if (position == DataSettingsActivity.this.clearDraftsSectionRow) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                case 1:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    textCell.setCanDisable(false);
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    if (position != DataSettingsActivity.this.storageUsageRow) {
                        if (position != DataSettingsActivity.this.useLessDataForCallsRow) {
                            if (position != DataSettingsActivity.this.dataUsageRow) {
                                if (position == DataSettingsActivity.this.storageNumRow) {
                                    String dir = ((File) DataSettingsActivity.this.storageDirs.get(0)).getAbsolutePath();
                                    if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                                        int a = 0;
                                        int N = DataSettingsActivity.this.storageDirs.size();
                                        while (true) {
                                            if (a < N) {
                                                String path = ((File) DataSettingsActivity.this.storageDirs.get(a)).getAbsolutePath();
                                                if (!path.startsWith(SharedConfig.storageCacheDir)) {
                                                    a++;
                                                } else {
                                                    dir = path;
                                                }
                                            }
                                        }
                                    }
                                    textCell.setTextAndValue(LocaleController.getString("StoragePath", R.string.StoragePath), dir, false);
                                    return;
                                } else if (position != DataSettingsActivity.this.proxyRow) {
                                    if (position != DataSettingsActivity.this.resetDownloadRow) {
                                        if (position != DataSettingsActivity.this.quickRepliesRow) {
                                            if (position == DataSettingsActivity.this.clearDraftsRow) {
                                                textCell.setText(LocaleController.getString("PrivacyDeleteCloudDrafts", R.string.PrivacyDeleteCloudDrafts), false);
                                                return;
                                            }
                                            return;
                                        }
                                        textCell.setText(LocaleController.getString("VoipQuickReplies", R.string.VoipQuickReplies), false);
                                        return;
                                    }
                                    textCell.setCanDisable(true);
                                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                                    textCell.setText(LocaleController.getString("ResetAutomaticMediaDownload", R.string.ResetAutomaticMediaDownload), false);
                                    return;
                                } else {
                                    textCell.setText(LocaleController.getString("ProxySettings", R.string.ProxySettings), false);
                                    return;
                                }
                            }
                            String string = LocaleController.getString("NetworkUsage", R.string.NetworkUsage);
                            if (DataSettingsActivity.this.storageNumRow != -1) {
                                z = true;
                            }
                            textCell.setText(string, z);
                            return;
                        }
                        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                        String value = null;
                        switch (preferences.getInt("VoipDataSaving", VoIPHelper.getDataSavingDefault())) {
                            case 0:
                                value = LocaleController.getString("UseLessDataNever", R.string.UseLessDataNever);
                                break;
                            case 1:
                                value = LocaleController.getString("UseLessDataOnMobile", R.string.UseLessDataOnMobile);
                                break;
                            case 2:
                                value = LocaleController.getString("UseLessDataAlways", R.string.UseLessDataAlways);
                                break;
                            case 3:
                                value = LocaleController.getString("UseLessDataOnRoaming", R.string.UseLessDataOnRoaming);
                                break;
                        }
                        textCell.setTextAndValue(LocaleController.getString("VoipUseLessData", R.string.VoipUseLessData), value, true);
                        return;
                    }
                    textCell.setText(LocaleController.getString("StorageUsage", R.string.StorageUsage), true);
                    return;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position != DataSettingsActivity.this.mediaDownloadSectionRow) {
                        if (position != DataSettingsActivity.this.usageSectionRow) {
                            if (position != DataSettingsActivity.this.callsSectionRow) {
                                if (position != DataSettingsActivity.this.proxySectionRow) {
                                    if (position != DataSettingsActivity.this.streamSectionRow) {
                                        if (position != DataSettingsActivity.this.autoplayHeaderRow) {
                                            if (position == DataSettingsActivity.this.saveToGallerySectionRow) {
                                                headerCell.setText(LocaleController.getString("SaveToGallery", R.string.SaveToGallery));
                                                return;
                                            }
                                            return;
                                        }
                                        headerCell.setText(LocaleController.getString("AutoplayMedia", R.string.AutoplayMedia));
                                        return;
                                    }
                                    headerCell.setText(LocaleController.getString("Streaming", R.string.Streaming));
                                    return;
                                }
                                headerCell.setText(LocaleController.getString("Proxy", R.string.Proxy));
                                return;
                            }
                            headerCell.setText(LocaleController.getString("Calls", R.string.Calls));
                            return;
                        }
                        headerCell.setText(LocaleController.getString("DataUsage", R.string.DataUsage));
                        return;
                    }
                    headerCell.setText(LocaleController.getString("AutomaticMediaDownload", R.string.AutomaticMediaDownload));
                    return;
                case 3:
                    TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                    if (position != DataSettingsActivity.this.enableStreamRow) {
                        if (position != DataSettingsActivity.this.enableCacheStreamRow) {
                            if (position != DataSettingsActivity.this.enableMkvRow) {
                                if (position != DataSettingsActivity.this.enableAllStreamRow) {
                                    if (position != DataSettingsActivity.this.autoplayGifsRow) {
                                        if (position != DataSettingsActivity.this.autoplayVideoRow) {
                                            if (position != DataSettingsActivity.this.saveToGalleryPeerRow) {
                                                if (position != DataSettingsActivity.this.saveToGalleryGroupsRow) {
                                                    if (position == DataSettingsActivity.this.saveToGalleryChannelsRow) {
                                                        String string2 = LocaleController.getString("SaveToGalleryChannels", R.string.SaveToGalleryChannels);
                                                        if ((4 & SharedConfig.saveToGalleryFlags) == 0) {
                                                            z2 = false;
                                                        }
                                                        checkCell.setTextAndCheck(string2, z2, false);
                                                        return;
                                                    }
                                                    return;
                                                }
                                                String string3 = LocaleController.getString("SaveToGalleryGroups", R.string.SaveToGalleryGroups);
                                                if ((SharedConfig.saveToGalleryFlags & 2) != 0) {
                                                    z = true;
                                                }
                                                checkCell.setTextAndCheck(string3, z, true);
                                                return;
                                            }
                                            String string4 = LocaleController.getString("SaveToGalleryPrivate", R.string.SaveToGalleryPrivate);
                                            if ((SharedConfig.saveToGalleryFlags & 1) != 0) {
                                                z = true;
                                            }
                                            checkCell.setTextAndCheck(string4, z, true);
                                            return;
                                        }
                                        checkCell.setTextAndCheck(LocaleController.getString("AutoplayVideo", R.string.AutoplayVideo), SharedConfig.autoplayVideo, false);
                                        return;
                                    }
                                    checkCell.setTextAndCheck(LocaleController.getString("AutoplayGIF", R.string.AutoplayGIF), SharedConfig.autoplayGifs, true);
                                    return;
                                }
                                checkCell.setTextAndCheck("(beta only) Stream All Videos", SharedConfig.streamAllVideo, false);
                                return;
                            }
                            checkCell.setTextAndCheck("(beta only) Show MKV as Video", SharedConfig.streamMkv, true);
                            return;
                        }
                        return;
                    }
                    String string5 = LocaleController.getString("EnableStreaming", R.string.EnableStreaming);
                    boolean z3 = SharedConfig.streamMedia;
                    if (DataSettingsActivity.this.enableAllStreamRow != -1) {
                        z = true;
                    }
                    checkCell.setTextAndCheck(string5, z3, z);
                    return;
                case 4:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == DataSettingsActivity.this.enableAllStreamInfoRow) {
                        cell.setText(LocaleController.getString("EnableAllStreamingInfo", R.string.EnableAllStreamingInfo));
                        return;
                    }
                    return;
                case 5:
                    NotificationsCheckCell checkCell2 = (NotificationsCheckCell) holder.itemView;
                    StringBuilder builder = new StringBuilder();
                    if (position != DataSettingsActivity.this.mobileRow) {
                        if (position == DataSettingsActivity.this.wifiRow) {
                            String text2 = LocaleController.getString("WhenConnectedOnWiFi", R.string.WhenConnectedOnWiFi);
                            boolean enabled2 = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).wifiPreset.enabled;
                            text = text2;
                            enabled = enabled2;
                            preset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentWiFiPreset();
                        } else {
                            String text3 = LocaleController.getString("WhenRoaming", R.string.WhenRoaming);
                            boolean enabled3 = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).roamingPreset.enabled;
                            text = text3;
                            enabled = enabled3;
                            preset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentRoamingPreset();
                        }
                    } else {
                        String text4 = LocaleController.getString("WhenUsingMobileData", R.string.WhenUsingMobileData);
                        boolean enabled4 = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).mobilePreset.enabled;
                        text = text4;
                        enabled = enabled4;
                        preset = DownloadController.getInstance(DataSettingsActivity.this.currentAccount).getCurrentMobilePreset();
                    }
                    boolean photos = false;
                    boolean videos = false;
                    boolean files = false;
                    int count = 0;
                    for (int a2 = 0; a2 < preset.mask.length; a2++) {
                        if (!photos && (preset.mask[a2] & 1) != 0) {
                            count++;
                            photos = true;
                        }
                        if (!videos && (preset.mask[a2] & 4) != 0) {
                            count++;
                            videos = true;
                        }
                        if (!files && (preset.mask[a2] & 8) != 0) {
                            count++;
                            files = true;
                        }
                    }
                    if (preset.enabled && count != 0) {
                        if (photos) {
                            builder.append(LocaleController.getString("AutoDownloadPhotosOn", R.string.AutoDownloadPhotosOn));
                        }
                        if (!videos) {
                            str = " (%1$s)";
                        } else {
                            if (builder.length() > 0) {
                                builder.append(", ");
                            }
                            builder.append(LocaleController.getString("AutoDownloadVideosOn", R.string.AutoDownloadVideosOn));
                            str = " (%1$s)";
                            builder.append(String.format(str, AndroidUtilities.formatFileSize(preset.sizes[DownloadController.typeToIndex(4)], true)));
                        }
                        if (files) {
                            if (builder.length() > 0) {
                                builder.append(", ");
                            }
                            builder.append(LocaleController.getString("AutoDownloadFilesOn", R.string.AutoDownloadFilesOn));
                            builder.append(String.format(str, AndroidUtilities.formatFileSize(preset.sizes[DownloadController.typeToIndex(8)], true)));
                        }
                    } else {
                        builder.append(LocaleController.getString("NoMediaAutoDownload", R.string.NoMediaAutoDownload));
                    }
                    checkCell2.setTextAndValueAndCheck(text, builder, (photos || videos || files) && enabled, 0, true, true);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int viewType = holder.getItemViewType();
            if (viewType == 3) {
                TextCheckCell checkCell = (TextCheckCell) holder.itemView;
                int position = holder.getAdapterPosition();
                if (position != DataSettingsActivity.this.enableCacheStreamRow) {
                    if (position != DataSettingsActivity.this.enableStreamRow) {
                        if (position != DataSettingsActivity.this.enableAllStreamRow) {
                            if (position != DataSettingsActivity.this.enableMkvRow) {
                                if (position != DataSettingsActivity.this.autoplayGifsRow) {
                                    if (position == DataSettingsActivity.this.autoplayVideoRow) {
                                        checkCell.setChecked(SharedConfig.autoplayVideo);
                                        return;
                                    }
                                    return;
                                }
                                checkCell.setChecked(SharedConfig.autoplayGifs);
                                return;
                            }
                            checkCell.setChecked(SharedConfig.streamMkv);
                            return;
                        }
                        checkCell.setChecked(SharedConfig.streamAllVideo);
                        return;
                    }
                    checkCell.setChecked(SharedConfig.streamMedia);
                    return;
                }
                checkCell.setChecked(SharedConfig.saveStreamMedia);
            }
        }

        public boolean isRowEnabled(int position) {
            if (position != DataSettingsActivity.this.resetDownloadRow) {
                return position == DataSettingsActivity.this.mobileRow || position == DataSettingsActivity.this.roamingRow || position == DataSettingsActivity.this.wifiRow || position == DataSettingsActivity.this.storageUsageRow || position == DataSettingsActivity.this.useLessDataForCallsRow || position == DataSettingsActivity.this.dataUsageRow || position == DataSettingsActivity.this.proxyRow || position == DataSettingsActivity.this.clearDraftsRow || position == DataSettingsActivity.this.enableCacheStreamRow || position == DataSettingsActivity.this.enableStreamRow || position == DataSettingsActivity.this.enableAllStreamRow || position == DataSettingsActivity.this.enableMkvRow || position == DataSettingsActivity.this.quickRepliesRow || position == DataSettingsActivity.this.autoplayVideoRow || position == DataSettingsActivity.this.autoplayGifsRow || position == DataSettingsActivity.this.storageNumRow || position == DataSettingsActivity.this.saveToGalleryGroupsRow || position == DataSettingsActivity.this.saveToGalleryPeerRow || position == DataSettingsActivity.this.saveToGalleryChannelsRow;
            }
            DownloadController controller = DownloadController.getInstance(DataSettingsActivity.this.currentAccount);
            return !controller.lowPreset.equals(controller.getCurrentRoamingPreset()) || controller.lowPreset.isEnabled() != controller.roamingPreset.enabled || !controller.mediumPreset.equals(controller.getCurrentMobilePreset()) || controller.mediumPreset.isEnabled() != controller.mobilePreset.enabled || !controller.highPreset.equals(controller.getCurrentWiFiPreset()) || controller.highPreset.isEnabled() != controller.wifiPreset.enabled;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return isRowEnabled(holder.getAdapterPosition());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 1:
                    View view2 = new TextSettingsCell(this.mContext);
                    view2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view2;
                    break;
                case 2:
                    View view3 = new HeaderCell(this.mContext);
                    view3.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view3;
                    break;
                case 3:
                    View view4 = new TextCheckCell(this.mContext);
                    view4.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view4;
                    break;
                case 4:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    break;
                default:
                    View view5 = new NotificationsCheckCell(this.mContext);
                    view5.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    view = view5;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position != DataSettingsActivity.this.mediaDownloadSection2Row && position != DataSettingsActivity.this.usageSection2Row && position != DataSettingsActivity.this.callsSection2Row && position != DataSettingsActivity.this.proxySection2Row && position != DataSettingsActivity.this.autoplaySectionRow && position != DataSettingsActivity.this.clearDraftsSectionRow && position != DataSettingsActivity.this.saveToGalleryDividerRow) {
                if (position != DataSettingsActivity.this.mediaDownloadSectionRow && position != DataSettingsActivity.this.streamSectionRow && position != DataSettingsActivity.this.callsSectionRow && position != DataSettingsActivity.this.usageSectionRow && position != DataSettingsActivity.this.proxySectionRow && position != DataSettingsActivity.this.autoplayHeaderRow && position != DataSettingsActivity.this.saveToGallerySectionRow) {
                    if (position != DataSettingsActivity.this.enableCacheStreamRow && position != DataSettingsActivity.this.enableStreamRow && position != DataSettingsActivity.this.enableAllStreamRow && position != DataSettingsActivity.this.enableMkvRow && position != DataSettingsActivity.this.autoplayGifsRow && position != DataSettingsActivity.this.autoplayVideoRow && position != DataSettingsActivity.this.saveToGalleryGroupsRow && position != DataSettingsActivity.this.saveToGalleryPeerRow && position != DataSettingsActivity.this.saveToGalleryChannelsRow) {
                        if (position != DataSettingsActivity.this.enableAllStreamInfoRow) {
                            if (position == DataSettingsActivity.this.mobileRow || position == DataSettingsActivity.this.wifiRow || position == DataSettingsActivity.this.roamingRow) {
                                return 5;
                            }
                            return 1;
                        }
                        return 4;
                    }
                    return 3;
                }
                return 2;
            }
            return 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, NotificationsCheckCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrack));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        return themeDescriptions;
    }
}
