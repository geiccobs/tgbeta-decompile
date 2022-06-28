package org.telegram.messenger;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.telegram.messenger.NotificationCenter;
/* loaded from: classes4.dex */
public class NotificationImageProvider extends ContentProvider implements NotificationCenter.NotificationCenterDelegate {
    public static final String AUTHORITY = "org.telegram.messenger.beta.notification_image_provider";
    private static final UriMatcher matcher;
    private HashSet<String> waitingForFiles = new HashSet<>();
    private final Object sync = new Object();
    private HashMap<String, Long> fileStartTimes = new HashMap<>();

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        matcher = uriMatcher;
        uriMatcher.addURI(AUTHORITY, "msg_media_raw/#/*", 1);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileLoaded);
        }
        return true;
    }

    @Override // android.content.ContentProvider
    public void shutdown() {
        for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileLoaded);
        }
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        return null;
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override // android.content.ContentProvider
    public String[] getStreamTypes(Uri uri, String mimeTypeFilter) {
        if (mimeTypeFilter.startsWith("*/") || mimeTypeFilter.startsWith("image/")) {
            return new String[]{"image/jpeg", "image/png", "image/webp"};
        }
        return null;
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:65:? -> B:48:0x00e9). Please submit an issue!!! */
    @Override // android.content.ContentProvider
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        Long _startTime;
        if (!"r".equals(mode)) {
            throw new SecurityException("Can only open files for read");
        }
        if (matcher.match(uri) == 1) {
            List<String> path = uri.getPathSegments();
            Integer.parseInt(path.get(1));
            String name = path.get(2);
            String finalPath = uri.getQueryParameter("final_path");
            String fallbackPath = uri.getQueryParameter("fallback");
            File finalFile = new File(finalPath);
            ApplicationLoader.postInitApplication();
            if (AndroidUtilities.isInternalUri(Uri.fromFile(finalFile))) {
                throw new SecurityException("trying to read internal file");
            }
            int i = 268435456;
            if (!finalFile.exists()) {
                Long _startTime2 = this.fileStartTimes.get(name);
                long startTime = _startTime2 != null ? _startTime2.longValue() : System.currentTimeMillis();
                if (_startTime2 == null) {
                    this.fileStartTimes.put(name, Long.valueOf(startTime));
                }
                while (!finalFile.exists()) {
                    if (System.currentTimeMillis() - startTime >= 3000) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.w("Waiting for " + name + " to download timed out");
                        }
                        if (TextUtils.isEmpty(fallbackPath)) {
                            throw new FileNotFoundException("Download timed out");
                        }
                        File file = new File(fallbackPath);
                        if (AndroidUtilities.isInternalUri(Uri.fromFile(file))) {
                            throw new SecurityException("trying to read internal file");
                        }
                        return ParcelFileDescriptor.open(file, i);
                    }
                    synchronized (this.sync) {
                        try {
                            this.waitingForFiles.add(name);
                            try {
                                _startTime = _startTime2;
                                try {
                                    try {
                                        this.sync.wait(1000L);
                                    } catch (Throwable th) {
                                        th = th;
                                        throw th;
                                    }
                                } catch (InterruptedException e) {
                                }
                            } catch (InterruptedException e2) {
                                _startTime = _startTime2;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            throw th;
                        }
                    }
                    _startTime2 = _startTime;
                    i = 268435456;
                }
                if (AndroidUtilities.isInternalUri(Uri.fromFile(finalFile))) {
                    throw new SecurityException("trying to read internal file");
                }
            }
            return ParcelFileDescriptor.open(finalFile, 268435456);
        }
        throw new FileNotFoundException("Invalid URI");
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileLoaded) {
            synchronized (this.sync) {
                String name = (String) args[0];
                if (this.waitingForFiles.remove(name)) {
                    this.fileStartTimes.remove(name);
                    this.sync.notifyAll();
                }
            }
        }
    }
}
