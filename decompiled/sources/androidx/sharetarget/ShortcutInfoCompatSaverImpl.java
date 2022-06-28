package androidx.sharetarget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import androidx.collection.ArrayMap;
import androidx.concurrent.futures.ResolvableFuture;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutInfoCompatSaver;
import androidx.core.graphics.drawable.IconCompat;
import androidx.sharetarget.ShortcutsInfoSerialization;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/* loaded from: classes3.dex */
public class ShortcutInfoCompatSaverImpl extends ShortcutInfoCompatSaver<ListenableFuture<Void>> {
    private static final String DIRECTORY_BITMAPS = "ShortcutInfoCompatSaver_share_targets_bitmaps";
    private static final String DIRECTORY_TARGETS = "ShortcutInfoCompatSaver_share_targets";
    private static final int EXECUTOR_KEEP_ALIVE_TIME_SECS = 20;
    private static final String FILENAME_XML = "targets.xml";
    private static final Object GET_INSTANCE_LOCK = new Object();
    static final String TAG = "ShortcutInfoCompatSaver";
    private static volatile ShortcutInfoCompatSaverImpl sInstance;
    final File mBitmapsDir;
    final ExecutorService mCacheUpdateService;
    final Context mContext;
    private final ExecutorService mDiskIoService;
    final File mTargetsXmlFile;
    final Map<String, ShortcutsInfoSerialization.ShortcutContainer> mShortcutsMap = new ArrayMap();
    final Map<String, ListenableFuture<?>> mScheduledBitmapTasks = new ArrayMap();

    public static ShortcutInfoCompatSaverImpl getInstance(Context context) {
        if (sInstance == null) {
            synchronized (GET_INSTANCE_LOCK) {
                if (sInstance == null) {
                    sInstance = new ShortcutInfoCompatSaverImpl(context, createExecutorService(), createExecutorService());
                }
            }
        }
        return sInstance;
    }

    static ExecutorService createExecutorService() {
        return new ThreadPoolExecutor(0, 1, 20L, TimeUnit.SECONDS, new LinkedBlockingQueue());
    }

    ShortcutInfoCompatSaverImpl(Context context, ExecutorService cacheUpdateService, ExecutorService diskIoService) {
        this.mContext = context.getApplicationContext();
        this.mCacheUpdateService = cacheUpdateService;
        this.mDiskIoService = diskIoService;
        final File workingDirectory = new File(context.getFilesDir(), DIRECTORY_TARGETS);
        this.mBitmapsDir = new File(workingDirectory, DIRECTORY_BITMAPS);
        this.mTargetsXmlFile = new File(workingDirectory, FILENAME_XML);
        cacheUpdateService.submit(new Runnable() { // from class: androidx.sharetarget.ShortcutInfoCompatSaverImpl.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    ShortcutInfoCompatSaverImpl.ensureDir(workingDirectory);
                    ShortcutInfoCompatSaverImpl.ensureDir(ShortcutInfoCompatSaverImpl.this.mBitmapsDir);
                    ShortcutInfoCompatSaverImpl.this.mShortcutsMap.putAll(ShortcutsInfoSerialization.loadFromXml(ShortcutInfoCompatSaverImpl.this.mTargetsXmlFile, ShortcutInfoCompatSaverImpl.this.mContext));
                    ShortcutInfoCompatSaverImpl.this.deleteDanglingBitmaps(new ArrayList(ShortcutInfoCompatSaverImpl.this.mShortcutsMap.values()));
                } catch (Exception e) {
                    Log.w(ShortcutInfoCompatSaverImpl.TAG, "ShortcutInfoCompatSaver started with an exceptions ", e);
                }
            }
        });
    }

    @Override // androidx.core.content.pm.ShortcutInfoCompatSaver
    public ListenableFuture<Void> removeShortcuts(List<String> shortcutIds) {
        final List<String> idList = new ArrayList<>(shortcutIds);
        final ResolvableFuture<Void> result = ResolvableFuture.create();
        this.mCacheUpdateService.submit(new Runnable() { // from class: androidx.sharetarget.ShortcutInfoCompatSaverImpl.2
            @Override // java.lang.Runnable
            public void run() {
                for (String id : idList) {
                    ShortcutInfoCompatSaverImpl.this.mShortcutsMap.remove(id);
                    ListenableFuture<?> removed = ShortcutInfoCompatSaverImpl.this.mScheduledBitmapTasks.remove(id);
                    if (removed != null) {
                        removed.cancel(false);
                    }
                }
                ShortcutInfoCompatSaverImpl.this.scheduleSyncCurrentState(result);
            }
        });
        return result;
    }

    @Override // androidx.core.content.pm.ShortcutInfoCompatSaver
    public ListenableFuture<Void> removeAllShortcuts() {
        final ResolvableFuture<Void> result = ResolvableFuture.create();
        this.mCacheUpdateService.submit(new Runnable() { // from class: androidx.sharetarget.ShortcutInfoCompatSaverImpl.3
            @Override // java.lang.Runnable
            public void run() {
                ShortcutInfoCompatSaverImpl.this.mShortcutsMap.clear();
                for (ListenableFuture<?> task : ShortcutInfoCompatSaverImpl.this.mScheduledBitmapTasks.values()) {
                    task.cancel(false);
                }
                ShortcutInfoCompatSaverImpl.this.mScheduledBitmapTasks.clear();
                ShortcutInfoCompatSaverImpl.this.scheduleSyncCurrentState(result);
            }
        });
        return result;
    }

    @Override // androidx.core.content.pm.ShortcutInfoCompatSaver
    public List<ShortcutInfoCompat> getShortcuts() throws Exception {
        return (List) this.mCacheUpdateService.submit(new Callable<ArrayList<ShortcutInfoCompat>>() { // from class: androidx.sharetarget.ShortcutInfoCompatSaverImpl.4
            @Override // java.util.concurrent.Callable
            public ArrayList<ShortcutInfoCompat> call() {
                ArrayList<ShortcutInfoCompat> shortcuts = new ArrayList<>();
                for (ShortcutsInfoSerialization.ShortcutContainer item : ShortcutInfoCompatSaverImpl.this.mShortcutsMap.values()) {
                    shortcuts.add(new ShortcutInfoCompat.Builder(item.mShortcutInfo).build());
                }
                return shortcuts;
            }
        }).get();
    }

    public IconCompat getShortcutIcon(final String shortcutId) throws Exception {
        Bitmap bitmap;
        final ShortcutsInfoSerialization.ShortcutContainer container = (ShortcutsInfoSerialization.ShortcutContainer) this.mCacheUpdateService.submit(new Callable<ShortcutsInfoSerialization.ShortcutContainer>() { // from class: androidx.sharetarget.ShortcutInfoCompatSaverImpl.5
            @Override // java.util.concurrent.Callable
            public ShortcutsInfoSerialization.ShortcutContainer call() {
                return ShortcutInfoCompatSaverImpl.this.mShortcutsMap.get(shortcutId);
            }
        }).get();
        if (container == null) {
            return null;
        }
        if (!TextUtils.isEmpty(container.mResourceName)) {
            int id = 0;
            try {
                id = this.mContext.getResources().getIdentifier(container.mResourceName, null, null);
            } catch (Exception e) {
            }
            if (id != 0) {
                return IconCompat.createWithResource(this.mContext, id);
            }
        }
        if (!TextUtils.isEmpty(container.mBitmapPath) && (bitmap = (Bitmap) this.mDiskIoService.submit(new Callable<Bitmap>() { // from class: androidx.sharetarget.ShortcutInfoCompatSaverImpl.6
            @Override // java.util.concurrent.Callable
            public Bitmap call() {
                return BitmapFactory.decodeFile(container.mBitmapPath);
            }
        }).get()) != null) {
            return IconCompat.createWithBitmap(bitmap);
        }
        return null;
    }

    void deleteDanglingBitmaps(List<ShortcutsInfoSerialization.ShortcutContainer> shortcutsList) {
        File[] listFiles;
        List<String> bitmapPaths = new ArrayList<>();
        for (ShortcutsInfoSerialization.ShortcutContainer item : shortcutsList) {
            if (!TextUtils.isEmpty(item.mBitmapPath)) {
                bitmapPaths.add(item.mBitmapPath);
            }
        }
        for (File bitmap : this.mBitmapsDir.listFiles()) {
            if (!bitmapPaths.contains(bitmap.getAbsolutePath())) {
                bitmap.delete();
            }
        }
    }

    @Override // androidx.core.content.pm.ShortcutInfoCompatSaver
    public ListenableFuture<Void> addShortcuts(List<ShortcutInfoCompat> shortcuts) {
        final List<ShortcutInfoCompat> copy = new ArrayList<>(shortcuts.size());
        for (ShortcutInfoCompat infoCompat : shortcuts) {
            copy.add(new ShortcutInfoCompat.Builder(infoCompat).build());
        }
        final ResolvableFuture<Void> result = ResolvableFuture.create();
        this.mCacheUpdateService.submit(new Runnable() { // from class: androidx.sharetarget.ShortcutInfoCompatSaverImpl.7
            @Override // java.lang.Runnable
            public void run() {
                for (ShortcutInfoCompat info : copy) {
                    Set<String> categories = info.getCategories();
                    if (categories != null && !categories.isEmpty()) {
                        ShortcutsInfoSerialization.ShortcutContainer container = ShortcutInfoCompatSaverImpl.this.containerFrom(info);
                        IconCompat icon = info.getIcon();
                        Bitmap bitmap = container.mBitmapPath != null ? icon.getBitmap() : null;
                        final String id = info.getId();
                        ShortcutInfoCompatSaverImpl.this.mShortcutsMap.put(id, container);
                        if (bitmap != null) {
                            final ListenableFuture<Void> future = ShortcutInfoCompatSaverImpl.this.scheduleBitmapSaving(bitmap, container.mBitmapPath);
                            ListenableFuture<?> old = ShortcutInfoCompatSaverImpl.this.mScheduledBitmapTasks.put(id, future);
                            if (old != null) {
                                old.cancel(false);
                            }
                            future.addListener(new Runnable() { // from class: androidx.sharetarget.ShortcutInfoCompatSaverImpl.7.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    ShortcutInfoCompatSaverImpl.this.mScheduledBitmapTasks.remove(id);
                                    if (future.isCancelled()) {
                                        return;
                                    }
                                    try {
                                        future.get();
                                    } catch (Exception e) {
                                        result.setException(e);
                                    }
                                }
                            }, ShortcutInfoCompatSaverImpl.this.mCacheUpdateService);
                        }
                    }
                }
                ShortcutInfoCompatSaverImpl.this.scheduleSyncCurrentState(result);
            }
        });
        return result;
    }

    ListenableFuture<Void> scheduleBitmapSaving(final Bitmap bitmap, final String path) {
        return submitDiskOperation(new Runnable() { // from class: androidx.sharetarget.ShortcutInfoCompatSaverImpl.8
            @Override // java.lang.Runnable
            public void run() {
                ShortcutInfoCompatSaverImpl.this.saveBitmap(bitmap, path);
            }
        });
    }

    private ListenableFuture<Void> submitDiskOperation(final Runnable runnable) {
        final ResolvableFuture<Void> result = ResolvableFuture.create();
        this.mDiskIoService.submit(new Runnable() { // from class: androidx.sharetarget.ShortcutInfoCompatSaverImpl.9
            @Override // java.lang.Runnable
            public void run() {
                if (result.isCancelled()) {
                    return;
                }
                try {
                    runnable.run();
                    result.set(null);
                } catch (Exception e) {
                    result.setException(e);
                }
            }
        });
        return result;
    }

    void scheduleSyncCurrentState(final ResolvableFuture<Void> output) {
        final List<ShortcutsInfoSerialization.ShortcutContainer> containers = new ArrayList<>(this.mShortcutsMap.values());
        final ListenableFuture<Void> future = submitDiskOperation(new Runnable() { // from class: androidx.sharetarget.ShortcutInfoCompatSaverImpl.10
            @Override // java.lang.Runnable
            public void run() {
                ShortcutInfoCompatSaverImpl.this.deleteDanglingBitmaps(containers);
                ShortcutsInfoSerialization.saveAsXml(containers, ShortcutInfoCompatSaverImpl.this.mTargetsXmlFile);
            }
        });
        future.addListener(new Runnable() { // from class: androidx.sharetarget.ShortcutInfoCompatSaverImpl.11
            @Override // java.lang.Runnable
            public void run() {
                try {
                    future.get();
                    output.set(null);
                } catch (Exception e) {
                    output.setException(e);
                }
            }
        }, this.mCacheUpdateService);
    }

    ShortcutsInfoSerialization.ShortcutContainer containerFrom(ShortcutInfoCompat shortcut) {
        String resourceName = null;
        String bitmapPath = null;
        IconCompat icon = shortcut.getIcon();
        if (icon != null) {
            switch (icon.getType()) {
                case 1:
                case 5:
                    bitmapPath = new File(this.mBitmapsDir, UUID.randomUUID().toString()).getAbsolutePath();
                    break;
                case 2:
                    resourceName = this.mContext.getResources().getResourceName(icon.getResId());
                    break;
            }
        }
        ShortcutInfoCompat shortcutCopy = new ShortcutInfoCompat.Builder(shortcut).setIcon(null).build();
        return new ShortcutsInfoSerialization.ShortcutContainer(shortcutCopy, resourceName, bitmapPath);
    }

    void saveBitmap(Bitmap bitmap, String path) {
        if (bitmap == null) {
            throw new IllegalArgumentException("bitmap is null");
        }
        if (TextUtils.isEmpty(path)) {
            throw new IllegalArgumentException("path is empty");
        }
        try {
            FileOutputStream fileStream = new FileOutputStream(new File(path));
            try {
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileStream)) {
                    Log.wtf(TAG, "Unable to compress bitmap");
                    throw new RuntimeException("Unable to compress bitmap for saving " + path);
                }
                fileStream.close();
            } catch (Throwable th) {
                try {
                    fileStream.close();
                } catch (Throwable th2) {
                }
                throw th;
            }
        } catch (IOException | OutOfMemoryError | RuntimeException e) {
            Log.wtf(TAG, "Unable to write bitmap to file", e);
            throw new RuntimeException("Unable to write bitmap to file " + path, e);
        }
    }

    static boolean ensureDir(File directory) {
        if (directory.exists() && !directory.isDirectory() && !directory.delete()) {
            return false;
        }
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }
}
