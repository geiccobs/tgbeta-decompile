package org.telegram.ui;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FilesMigrationService;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SlideChooseView;
import org.telegram.ui.Components.StorageDiagramView;
import org.telegram.ui.Components.StroageUsageView;
import org.telegram.ui.Components.UndoView;
/* loaded from: classes4.dex */
public class CacheControlActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private View actionTextView;
    private BottomSheet bottomSheet;
    private View bottomSheetView;
    private int cacheInfoRow;
    private UndoView cacheRemovedTooltip;
    private int databaseInfoRow;
    private int databaseRow;
    private int deviseStorageHeaderRow;
    long fragmentCreateTime;
    private int keepMediaChooserRow;
    private int keepMediaHeaderRow;
    private int keepMediaInfoRow;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    AlertDialog progressDialog;
    private int rowCount;
    private int storageUsageRow;
    private long databaseSize = -1;
    private long cacheSize = -1;
    private long documentsSize = -1;
    private long audioSize = -1;
    private long musicSize = -1;
    private long photoSize = -1;
    private long videoSize = -1;
    private long stickersSize = -1;
    private long totalSize = -1;
    private long totalDeviceSize = -1;
    private long totalDeviceFreeSize = -1;
    private long migrateOldFolderRow = -1;
    private StorageDiagramView.ClearViewData[] clearViewData = new StorageDiagramView.ClearViewData[7];
    private boolean calculating = true;
    private volatile boolean canceled = false;

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getNotificationCenter().addObserver(this, NotificationCenter.didClearDatabase);
        this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                CacheControlActivity.this.m1581lambda$onFragmentCreate$1$orgtelegramuiCacheControlActivity();
            }
        });
        this.fragmentCreateTime = System.currentTimeMillis();
        updateRows();
        return true;
    }

    /* renamed from: lambda$onFragmentCreate$1$org-telegram-ui-CacheControlActivity */
    public /* synthetic */ void m1581lambda$onFragmentCreate$1$orgtelegramuiCacheControlActivity() {
        File path;
        long blockSize;
        long availableBlocks;
        long blocksTotal;
        this.cacheSize = getDirectorySize(FileLoader.checkDirectory(4), 0);
        if (this.canceled) {
            return;
        }
        long directorySize = getDirectorySize(FileLoader.checkDirectory(0), 0);
        this.photoSize = directorySize;
        this.photoSize = directorySize + getDirectorySize(FileLoader.checkDirectory(100), 0);
        if (this.canceled) {
            return;
        }
        long directorySize2 = getDirectorySize(FileLoader.checkDirectory(2), 0);
        this.videoSize = directorySize2;
        this.videoSize = directorySize2 + getDirectorySize(FileLoader.checkDirectory(101), 0);
        if (this.canceled) {
            return;
        }
        long directorySize3 = getDirectorySize(FileLoader.checkDirectory(3), 1);
        this.documentsSize = directorySize3;
        this.documentsSize = directorySize3 + getDirectorySize(FileLoader.checkDirectory(5), 1);
        if (!this.canceled) {
            long directorySize4 = getDirectorySize(FileLoader.checkDirectory(3), 2);
            this.musicSize = directorySize4;
            this.musicSize = directorySize4 + getDirectorySize(FileLoader.checkDirectory(5), 2);
            if (this.canceled) {
                return;
            }
            this.stickersSize = getDirectorySize(new File(FileLoader.checkDirectory(4), "acache"), 0);
            if (this.canceled) {
                return;
            }
            long directorySize5 = getDirectorySize(FileLoader.checkDirectory(1), 0);
            this.audioSize = directorySize5;
            this.totalSize = this.cacheSize + this.videoSize + directorySize5 + this.photoSize + this.documentsSize + this.musicSize + this.stickersSize;
            if (Build.VERSION.SDK_INT >= 19) {
                ArrayList<File> storageDirs = AndroidUtilities.getRootDirs();
                File file = storageDirs.get(0);
                path = file;
                file.getAbsolutePath();
                if (!TextUtils.isEmpty(SharedConfig.storageCacheDir)) {
                    int N = storageDirs.size();
                    for (int a = 0; a < N; a++) {
                        File file2 = storageDirs.get(a);
                        if (file2.getAbsolutePath().startsWith(SharedConfig.storageCacheDir)) {
                            path = file2;
                            break;
                        }
                    }
                }
            } else {
                path = new File(SharedConfig.storageCacheDir);
            }
            try {
                StatFs stat = new StatFs(path.getPath());
                if (Build.VERSION.SDK_INT >= 18) {
                    blockSize = stat.getBlockSizeLong();
                } else {
                    blockSize = stat.getBlockSize();
                }
                if (Build.VERSION.SDK_INT >= 18) {
                    availableBlocks = stat.getAvailableBlocksLong();
                } else {
                    availableBlocks = stat.getAvailableBlocks();
                }
                if (Build.VERSION.SDK_INT >= 18) {
                    blocksTotal = stat.getBlockCountLong();
                } else {
                    blocksTotal = stat.getBlockCount();
                }
                this.totalDeviceSize = blocksTotal * blockSize;
                this.totalDeviceFreeSize = availableBlocks * blockSize;
            } catch (Exception e) {
                FileLog.e(e);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    CacheControlActivity.this.m1580lambda$onFragmentCreate$0$orgtelegramuiCacheControlActivity();
                }
            });
        }
    }

    /* renamed from: lambda$onFragmentCreate$0$org-telegram-ui-CacheControlActivity */
    public /* synthetic */ void m1580lambda$onFragmentCreate$0$orgtelegramuiCacheControlActivity() {
        this.calculating = false;
        updateStorageUsageRow();
    }

    private void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.keepMediaHeaderRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.keepMediaChooserRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.keepMediaInfoRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.deviseStorageHeaderRow = i3;
        int i5 = i4 + 1;
        this.rowCount = i5;
        this.storageUsageRow = i4;
        int i6 = i5 + 1;
        this.rowCount = i6;
        this.cacheInfoRow = i5;
        int i7 = i6 + 1;
        this.rowCount = i7;
        this.databaseRow = i6;
        this.rowCount = i7 + 1;
        this.databaseInfoRow = i7;
    }

    private void updateStorageUsageRow() {
        View view = this.layoutManager.findViewByPosition(this.storageUsageRow);
        if (view instanceof StroageUsageView) {
            StroageUsageView stroageUsageView = (StroageUsageView) view;
            long currentTime = System.currentTimeMillis();
            if (Build.VERSION.SDK_INT >= 19 && currentTime - this.fragmentCreateTime > 250) {
                TransitionSet transition = new TransitionSet();
                ChangeBounds changeBounds = new ChangeBounds();
                changeBounds.setDuration(250L);
                changeBounds.excludeTarget((View) stroageUsageView.legendLayout, true);
                Fade in = new Fade(1);
                in.setDuration(290L);
                transition.addTransition(new Fade(2).setDuration(250L)).addTransition(changeBounds).addTransition(in);
                transition.setOrdering(0);
                transition.setInterpolator((TimeInterpolator) CubicBezierInterpolator.EASE_OUT);
                TransitionManager.beginDelayedTransition(this.listView, transition);
            }
            stroageUsageView.setStorageUsage(this.calculating, this.databaseSize, this.totalSize, this.totalDeviceFreeSize, this.totalDeviceSize);
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.storageUsageRow);
            if (holder != null) {
                stroageUsageView.setEnabled(this.listAdapter.isEnabled(holder));
                return;
            }
            return;
        }
        this.listAdapter.notifyDataSetChanged();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.didClearDatabase);
        try {
            AlertDialog alertDialog = this.progressDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        } catch (Exception e) {
        }
        this.progressDialog = null;
        this.canceled = true;
    }

    private long getDirectorySize(File dir, int documentsMusicType) {
        if (dir == null || this.canceled) {
            return 0L;
        }
        if (dir.isDirectory()) {
            long size = Utilities.getDirSize(dir.getAbsolutePath(), documentsMusicType, false);
            return size;
        } else if (dir.isFile()) {
            long size2 = 0 + dir.length();
            return size2;
        } else {
            return 0L;
        }
    }

    private void cleanupFolders() {
        final AlertDialog progressDialog = new AlertDialog(getParentActivity(), 3);
        progressDialog.setCanCancel(false);
        progressDialog.showDelayed(500L);
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                CacheControlActivity.this.m1574lambda$cleanupFolders$3$orgtelegramuiCacheControlActivity(progressDialog);
            }
        });
    }

    /* renamed from: lambda$cleanupFolders$3$org-telegram-ui-CacheControlActivity */
    public /* synthetic */ void m1574lambda$cleanupFolders$3$orgtelegramuiCacheControlActivity(final AlertDialog progressDialog) {
        long blockSize;
        long availableBlocks;
        long blocksTotal;
        File file;
        File file2;
        int publicDirectoryType;
        boolean imagesCleared = false;
        long clearedSize = 0;
        for (int a = 0; a < 7; a++) {
            StorageDiagramView.ClearViewData[] clearViewDataArr = this.clearViewData;
            if (clearViewDataArr[a] != null && clearViewDataArr[a].clear) {
                int type = -1;
                int documentsMusicType = 0;
                if (a == 0) {
                    type = 0;
                    clearedSize += this.photoSize;
                } else if (a == 1) {
                    type = 2;
                    clearedSize += this.videoSize;
                } else if (a == 2) {
                    type = 3;
                    documentsMusicType = 1;
                    clearedSize += this.documentsSize;
                } else if (a == 3) {
                    type = 3;
                    documentsMusicType = 2;
                    clearedSize += this.musicSize;
                } else if (a == 4) {
                    type = 1;
                    clearedSize += this.audioSize;
                } else if (a == 5) {
                    type = 100;
                    clearedSize += this.stickersSize;
                } else if (a == 6) {
                    clearedSize += this.cacheSize;
                    type = 4;
                }
                if (type != -1) {
                    if (type == 100) {
                        file = new File(FileLoader.checkDirectory(4), "acache");
                    } else {
                        file = FileLoader.checkDirectory(type);
                    }
                    if (file != null) {
                        Utilities.clearDir(file.getAbsolutePath(), documentsMusicType, Long.MAX_VALUE, false);
                    }
                    if (type == 0 || type == 2) {
                        if (type == 0) {
                            publicDirectoryType = 100;
                        } else {
                            publicDirectoryType = 101;
                        }
                        File file3 = FileLoader.checkDirectory(publicDirectoryType);
                        if (file3 != null) {
                            Utilities.clearDir(file3.getAbsolutePath(), documentsMusicType, Long.MAX_VALUE, false);
                        }
                    }
                    if (type == 3 && (file2 = FileLoader.checkDirectory(5)) != null) {
                        Utilities.clearDir(file2.getAbsolutePath(), documentsMusicType, Long.MAX_VALUE, false);
                    }
                    if (type == 4) {
                        this.cacheSize = getDirectorySize(FileLoader.checkDirectory(4), documentsMusicType);
                        imagesCleared = true;
                    } else if (type == 1) {
                        this.audioSize = getDirectorySize(FileLoader.checkDirectory(1), documentsMusicType);
                    } else if (type == 3) {
                        if (documentsMusicType == 1) {
                            long directorySize = getDirectorySize(FileLoader.checkDirectory(3), documentsMusicType);
                            this.documentsSize = directorySize;
                            this.documentsSize = directorySize + getDirectorySize(FileLoader.checkDirectory(5), documentsMusicType);
                        } else {
                            long directorySize2 = getDirectorySize(FileLoader.checkDirectory(3), documentsMusicType);
                            this.musicSize = directorySize2;
                            this.musicSize = directorySize2 + getDirectorySize(FileLoader.checkDirectory(5), documentsMusicType);
                        }
                    } else if (type == 0) {
                        long directorySize3 = getDirectorySize(FileLoader.checkDirectory(0), documentsMusicType);
                        this.photoSize = directorySize3;
                        this.photoSize = directorySize3 + getDirectorySize(FileLoader.checkDirectory(100), documentsMusicType);
                        imagesCleared = true;
                    } else if (type == 2) {
                        long directorySize4 = getDirectorySize(FileLoader.checkDirectory(2), documentsMusicType);
                        this.videoSize = directorySize4;
                        this.videoSize = directorySize4 + getDirectorySize(FileLoader.checkDirectory(101), documentsMusicType);
                    } else if (type == 100) {
                        this.stickersSize = getDirectorySize(new File(FileLoader.checkDirectory(4), "acache"), documentsMusicType);
                        imagesCleared = true;
                    }
                }
            }
        }
        final boolean imagesClearedFinal = imagesCleared;
        this.totalSize = this.cacheSize + this.videoSize + this.audioSize + this.photoSize + this.documentsSize + this.musicSize + this.stickersSize;
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        if (Build.VERSION.SDK_INT >= 18) {
            blockSize = stat.getBlockSizeLong();
        } else {
            blockSize = stat.getBlockSize();
        }
        if (Build.VERSION.SDK_INT >= 18) {
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            availableBlocks = stat.getAvailableBlocks();
        }
        if (Build.VERSION.SDK_INT >= 18) {
            blocksTotal = stat.getBlockCountLong();
        } else {
            blocksTotal = stat.getBlockCount();
        }
        this.totalDeviceSize = blocksTotal * blockSize;
        this.totalDeviceFreeSize = availableBlocks * blockSize;
        final long finalClearedSize = clearedSize;
        FileLoader.getInstance(this.currentAccount).checkCurrentDownloadsFiles();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                CacheControlActivity.this.m1573lambda$cleanupFolders$2$orgtelegramuiCacheControlActivity(imagesClearedFinal, progressDialog, finalClearedSize);
            }
        });
    }

    /* renamed from: lambda$cleanupFolders$2$org-telegram-ui-CacheControlActivity */
    public /* synthetic */ void m1573lambda$cleanupFolders$2$orgtelegramuiCacheControlActivity(boolean imagesClearedFinal, AlertDialog progressDialog, long finalClearedSize) {
        if (imagesClearedFinal) {
            ImageLoader.getInstance().clearMemory();
        }
        if (this.listAdapter != null) {
            updateStorageUsageRow();
        }
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        getMediaDataController().ringtoneDataStore.checkRingtoneSoundsLoaded();
        this.cacheRemovedTooltip.setInfoText(LocaleController.formatString("CacheWasCleared", R.string.CacheWasCleared, AndroidUtilities.formatFileSize(finalClearedSize)));
        this.cacheRemovedTooltip.showWithAction(0L, 19, null, null);
        MediaDataController.getInstance(this.currentAccount).chekAllMedia(true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("StorageUsage", R.string.StorageUsage));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.CacheControlActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    CacheControlActivity.this.finishFragment();
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                CacheControlActivity.this.m1578lambda$createView$6$orgtelegramuiCacheControlActivity(context, view, i);
            }
        });
        UndoView undoView = new UndoView(context);
        this.cacheRemovedTooltip = undoView;
        frameLayout.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-CacheControlActivity */
    public /* synthetic */ void m1578lambda$createView$6$orgtelegramuiCacheControlActivity(Context context, View view, int position) {
        long size;
        String color;
        String name;
        if (getParentActivity() != null) {
            if (position == this.migrateOldFolderRow) {
                if (Build.VERSION.SDK_INT >= 30) {
                    migrateOldFolder();
                }
            } else if (position == this.databaseRow) {
                clearDatabase();
            } else if (position == this.storageUsageRow) {
                long j = 0;
                if (this.totalSize > 0 && getParentActivity() != null) {
                    BottomSheet bottomSheet = new BottomSheet(getParentActivity(), false) { // from class: org.telegram.ui.CacheControlActivity.2
                        @Override // org.telegram.ui.ActionBar.BottomSheet
                        protected boolean canDismissWithSwipe() {
                            return false;
                        }
                    };
                    this.bottomSheet = bottomSheet;
                    bottomSheet.fixNavigationBar();
                    int i = 1;
                    this.bottomSheet.setAllowNestedScroll(true);
                    this.bottomSheet.setApplyBottomPadding(false);
                    LinearLayout linearLayout = new LinearLayout(getParentActivity());
                    this.bottomSheetView = linearLayout;
                    linearLayout.setOrientation(1);
                    StorageDiagramView circleDiagramView = new StorageDiagramView(context);
                    linearLayout.addView(circleDiagramView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 16));
                    CheckBoxCell lastCreatedCheckbox = null;
                    int a = 0;
                    while (a < 7) {
                        if (a == 0) {
                            size = this.photoSize;
                            name = LocaleController.getString("LocalPhotoCache", R.string.LocalPhotoCache);
                            color = Theme.key_statisticChartLine_blue;
                        } else if (a == i) {
                            size = this.videoSize;
                            name = LocaleController.getString("LocalVideoCache", R.string.LocalVideoCache);
                            color = Theme.key_statisticChartLine_golden;
                        } else if (a == 2) {
                            size = this.documentsSize;
                            name = LocaleController.getString("LocalDocumentCache", R.string.LocalDocumentCache);
                            color = Theme.key_statisticChartLine_green;
                        } else if (a == 3) {
                            size = this.musicSize;
                            name = LocaleController.getString("LocalMusicCache", R.string.LocalMusicCache);
                            color = Theme.key_statisticChartLine_indigo;
                        } else if (a == 4) {
                            size = this.audioSize;
                            name = LocaleController.getString("LocalAudioCache", R.string.LocalAudioCache);
                            color = Theme.key_statisticChartLine_red;
                        } else if (a == 5) {
                            size = this.stickersSize;
                            name = LocaleController.getString("AnimatedStickers", R.string.AnimatedStickers);
                            color = Theme.key_statisticChartLine_lightgreen;
                        } else {
                            size = this.cacheSize;
                            name = LocaleController.getString("LocalCache", R.string.LocalCache);
                            color = Theme.key_statisticChartLine_lightblue;
                        }
                        if (size <= j) {
                            this.clearViewData[a] = null;
                        } else {
                            this.clearViewData[a] = new StorageDiagramView.ClearViewData(circleDiagramView);
                            this.clearViewData[a].size = size;
                            this.clearViewData[a].color = color;
                            CheckBoxCell lastCreatedCheckbox2 = new CheckBoxCell(getParentActivity(), 4, 21, null);
                            lastCreatedCheckbox2.setTag(Integer.valueOf(a));
                            lastCreatedCheckbox2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            linearLayout.addView(lastCreatedCheckbox2, LayoutHelper.createLinear(-1, 50));
                            lastCreatedCheckbox2.setText(name, AndroidUtilities.formatFileSize(size), true, true);
                            lastCreatedCheckbox2.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                            lastCreatedCheckbox2.setCheckBoxColor(color, Theme.key_windowBackgroundWhiteGrayIcon, Theme.key_checkboxCheck);
                            lastCreatedCheckbox2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda1
                                @Override // android.view.View.OnClickListener
                                public final void onClick(View view2) {
                                    CacheControlActivity.this.m1576lambda$createView$4$orgtelegramuiCacheControlActivity(view2);
                                }
                            });
                            lastCreatedCheckbox = lastCreatedCheckbox2;
                        }
                        a++;
                        i = 1;
                        j = 0;
                    }
                    if (lastCreatedCheckbox != null) {
                        lastCreatedCheckbox.setNeedDivider(false);
                    }
                    circleDiagramView.setData(this.clearViewData);
                    BottomSheet.BottomSheetCell cell = new BottomSheet.BottomSheetCell(getParentActivity(), 2);
                    cell.setTextAndIcon(LocaleController.getString("ClearMediaCache", R.string.ClearMediaCache), 0);
                    this.actionTextView = cell.getTextView();
                    cell.getTextView().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            CacheControlActivity.this.m1577lambda$createView$5$orgtelegramuiCacheControlActivity(view2);
                        }
                    });
                    linearLayout.addView(cell, LayoutHelper.createLinear(-1, 50));
                    NestedScrollView scrollView = new NestedScrollView(context);
                    scrollView.setVerticalScrollBarEnabled(false);
                    scrollView.addView(linearLayout);
                    this.bottomSheet.setCustomView(scrollView);
                    showDialog(this.bottomSheet);
                }
            }
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-CacheControlActivity */
    public /* synthetic */ void m1576lambda$createView$4$orgtelegramuiCacheControlActivity(View v) {
        int enabledCount = 0;
        int i = 0;
        while (true) {
            StorageDiagramView.ClearViewData[] clearViewDataArr = this.clearViewData;
            if (i >= clearViewDataArr.length) {
                break;
            }
            if (clearViewDataArr[i] != null && clearViewDataArr[i].clear) {
                enabledCount++;
            }
            i++;
        }
        CheckBoxCell cell = (CheckBoxCell) v;
        int num = ((Integer) cell.getTag()).intValue();
        if (enabledCount == 1 && this.clearViewData[num].clear) {
            AndroidUtilities.shakeView(((CheckBoxCell) v).getCheckBoxView(), 2.0f, 0);
            return;
        }
        StorageDiagramView.ClearViewData[] clearViewDataArr2 = this.clearViewData;
        clearViewDataArr2[num].setClear(!clearViewDataArr2[num].clear);
        cell.setChecked(this.clearViewData[num].clear, true);
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-CacheControlActivity */
    public /* synthetic */ void m1577lambda$createView$5$orgtelegramuiCacheControlActivity(View v) {
        try {
            if (this.visibleDialog != null) {
                this.visibleDialog.dismiss();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        cleanupFolders();
    }

    private void migrateOldFolder() {
        FilesMigrationService.checkBottomSheet(this);
    }

    private void clearDatabase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("LocalDatabaseClearTextTitle", R.string.LocalDatabaseClearTextTitle));
        builder.setMessage(LocaleController.getString("LocalDatabaseClearText", R.string.LocalDatabaseClearText));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("CacheClear", R.string.CacheClear), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                CacheControlActivity.this.m1575lambda$clearDatabase$7$orgtelegramuiCacheControlActivity(dialogInterface, i);
            }
        });
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
        }
    }

    /* renamed from: lambda$clearDatabase$7$org-telegram-ui-CacheControlActivity */
    public /* synthetic */ void m1575lambda$clearDatabase$7$orgtelegramuiCacheControlActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        this.progressDialog = alertDialog;
        alertDialog.setCanCancel(false);
        this.progressDialog.showDelayed(500L);
        MessagesController.getInstance(this.currentAccount).clearQueryTime();
        getMessagesStorage().clearLocalDatabase();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didClearDatabase) {
            try {
                AlertDialog alertDialog = this.progressDialog;
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            this.progressDialog = null;
            if (this.listAdapter != null) {
                this.databaseSize = MessagesStorage.getInstance(this.currentAccount).getDatabaseSize();
                this.listAdapter.notifyDataSetChanged();
            }
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            CacheControlActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return ((long) position) == CacheControlActivity.this.migrateOldFolderRow || position == CacheControlActivity.this.databaseRow || (position == CacheControlActivity.this.storageUsageRow && CacheControlActivity.this.totalSize > 0 && !CacheControlActivity.this.calculating);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return CacheControlActivity.this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            int index;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                default:
                    view = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 2:
                    view = new StroageUsageView(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 3:
                    view = new HeaderCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 4:
                    SlideChooseView slideChooseView = new SlideChooseView(this.mContext);
                    view = slideChooseView;
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    MessagesController.getGlobalMainSettings();
                    slideChooseView.setCallback(CacheControlActivity$ListAdapter$$ExternalSyntheticLambda0.INSTANCE);
                    int keepMedia = SharedConfig.keepMedia;
                    if (keepMedia == 3) {
                        index = 0;
                    } else {
                        index = keepMedia + 1;
                    }
                    slideChooseView.setOptions(index, LocaleController.formatPluralString("Days", 3, new Object[0]), LocaleController.formatPluralString("Weeks", 1, new Object[0]), LocaleController.formatPluralString("Months", 1, new Object[0]), LocaleController.getString("KeepMediaForever", R.string.KeepMediaForever));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public static /* synthetic */ void lambda$onCreateViewHolder$0(int index) {
            if (index == 0) {
                SharedConfig.setKeepMedia(3);
            } else if (index == 1) {
                SharedConfig.setKeepMedia(0);
            } else if (index == 2) {
                SharedConfig.setKeepMedia(1);
            } else if (index == 3) {
                SharedConfig.setKeepMedia(2);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == CacheControlActivity.this.databaseRow) {
                        textCell.setTextAndValue(LocaleController.getString("ClearLocalDatabase", R.string.ClearLocalDatabase), AndroidUtilities.formatFileSize(CacheControlActivity.this.databaseSize), false);
                        return;
                    } else if (position == CacheControlActivity.this.migrateOldFolderRow) {
                        textCell.setTextAndValue(LocaleController.getString("MigrateOldFolder", R.string.MigrateOldFolder), null, false);
                        return;
                    } else {
                        return;
                    }
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position != CacheControlActivity.this.databaseInfoRow) {
                        if (position != CacheControlActivity.this.cacheInfoRow) {
                            if (position == CacheControlActivity.this.keepMediaInfoRow) {
                                privacyCell.setText(AndroidUtilities.replaceTags(LocaleController.getString("KeepMediaInfo", R.string.KeepMediaInfo)));
                                privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                                return;
                            }
                            return;
                        }
                        privacyCell.setText("");
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                        return;
                    }
                    privacyCell.setText(LocaleController.getString("LocalDatabaseInfo", R.string.LocalDatabaseInfo));
                    privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                    return;
                case 2:
                    StroageUsageView stroageUsageView = (StroageUsageView) holder.itemView;
                    stroageUsageView.setStorageUsage(CacheControlActivity.this.calculating, CacheControlActivity.this.databaseSize, CacheControlActivity.this.totalSize, CacheControlActivity.this.totalDeviceFreeSize, CacheControlActivity.this.totalDeviceSize);
                    return;
                case 3:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position != CacheControlActivity.this.keepMediaHeaderRow) {
                        if (position == CacheControlActivity.this.deviseStorageHeaderRow) {
                            headerCell.setText(LocaleController.getString("DeviceStorage", R.string.DeviceStorage));
                            return;
                        }
                        return;
                    }
                    headerCell.setText(LocaleController.getString("KeepMedia", R.string.KeepMedia));
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i != CacheControlActivity.this.databaseInfoRow && i != CacheControlActivity.this.cacheInfoRow && i != CacheControlActivity.this.keepMediaInfoRow) {
                if (i != CacheControlActivity.this.storageUsageRow) {
                    if (i != CacheControlActivity.this.keepMediaHeaderRow && i != CacheControlActivity.this.deviseStorageHeaderRow) {
                        if (i == CacheControlActivity.this.keepMediaChooserRow) {
                            return 4;
                        }
                        return 0;
                    }
                    return 3;
                }
                return 2;
            }
            return 1;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate deldegagte = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.CacheControlActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                CacheControlActivity.this.m1579x48b51629();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, SlideChooseView.class, StroageUsageView.class, HeaderCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"paintFill"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progressBackground));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"paintProgress"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progress));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"telegramCacheTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"freeSizeTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"calculationgTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{StroageUsageView.class}, new String[]{"paintProgress2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_player_progressBackground2));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, null, null, null, Theme.key_switchTrack));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, null, null, null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{SlideChooseView.class}, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, new Class[]{CheckBoxCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, new Class[]{CheckBoxCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteValueText));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, new Class[]{CheckBoxCell.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, new Class[]{StorageDiagramView.class}, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription((View) null, 0, new Class[]{TextCheckBoxCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, deldegagte, Theme.key_dialogBackground));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, Theme.key_statisticChartLine_blue));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, Theme.key_statisticChartLine_green));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, Theme.key_statisticChartLine_red));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, Theme.key_statisticChartLine_golden));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, Theme.key_statisticChartLine_lightblue));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, Theme.key_statisticChartLine_lightgreen));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, Theme.key_statisticChartLine_orange));
        arrayList.add(new ThemeDescription(this.bottomSheetView, 0, null, null, null, null, Theme.key_statisticChartLine_indigo));
        return arrayList;
    }

    /* renamed from: lambda$getThemeDescriptions$8$org-telegram-ui-CacheControlActivity */
    public /* synthetic */ void m1579x48b51629() {
        BottomSheet bottomSheet = this.bottomSheet;
        if (bottomSheet != null) {
            bottomSheet.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        }
        View view = this.actionTextView;
        if (view != null) {
            view.setBackground(Theme.AdaptiveRipple.filledRect(Theme.key_featuredStickers_addButton, 4.0f));
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 4) {
            boolean allGranted = true;
            int a = 0;
            while (true) {
                if (a >= grantResults.length) {
                    break;
                } else if (grantResults[a] == 0) {
                    a++;
                } else {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted && Build.VERSION.SDK_INT >= 30 && FilesMigrationService.filesMigrationBottomSheet != null) {
                FilesMigrationService.filesMigrationBottomSheet.migrateOldFolder();
            }
        }
    }
}
