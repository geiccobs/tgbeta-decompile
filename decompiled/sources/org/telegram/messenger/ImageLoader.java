package org.telegram.messenger;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.SparseArray;
import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.Consumer;
import j$.util.stream.Stream;
import j$.wrappers.C$r8$wrapper$java$util$stream$Stream$VWRP;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputEncryptedFile;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_fileLocationToBeDeprecated;
import org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_photoCachedSize;
import org.telegram.tgnet.TLRPC$TL_photoSize_layer127;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.RLottieDrawable;
/* loaded from: classes.dex */
public class ImageLoader {
    public static final String AUTOPLAY_FILTER = "g";
    private boolean canForce8888;
    private LruCache<BitmapDrawable> lottieMemCache;
    private LruCache<BitmapDrawable> memCache;
    private LruCache<BitmapDrawable> smallImagesMemCache;
    private LruCache<BitmapDrawable> wallpaperMemCache;
    private static ThreadLocal<byte[]> bytesLocal = new ThreadLocal<>();
    private static ThreadLocal<byte[]> bytesThumbLocal = new ThreadLocal<>();
    private static byte[] header = new byte[12];
    private static byte[] headerThumb = new byte[12];
    private static volatile ImageLoader Instance = null;
    private HashMap<String, Integer> bitmapUseCounts = new HashMap<>();
    ArrayList<AnimatedFileDrawable> cachedAnimatedFileDrawables = new ArrayList<>();
    private HashMap<String, CacheImage> imageLoadingByUrl = new HashMap<>();
    private HashMap<String, CacheImage> imageLoadingByKeys = new HashMap<>();
    private SparseArray<CacheImage> imageLoadingByTag = new SparseArray<>();
    private HashMap<String, ThumbGenerateInfo> waitingForQualityThumb = new HashMap<>();
    private SparseArray<String> waitingForQualityThumbByTag = new SparseArray<>();
    private LinkedList<HttpImageTask> httpTasks = new LinkedList<>();
    private LinkedList<ArtworkLoadTask> artworkTasks = new LinkedList<>();
    private DispatchQueue cacheOutQueue = new DispatchQueue("cacheOutQueue");
    private DispatchQueue cacheThumbOutQueue = new DispatchQueue("cacheThumbOutQueue");
    private DispatchQueue thumbGeneratingQueue = new DispatchQueue("thumbGeneratingQueue");
    private DispatchQueue imageLoadQueue = new DispatchQueue("imageLoadQueue");
    private HashMap<String, String> replacedBitmaps = new HashMap<>();
    private ConcurrentHashMap<String, long[]> fileProgresses = new ConcurrentHashMap<>();
    private HashMap<String, ThumbGenerateTask> thumbGenerateTasks = new HashMap<>();
    private HashMap<String, Integer> forceLoadingImages = new HashMap<>();
    private int currentHttpTasksCount = 0;
    private int currentArtworkTasksCount = 0;
    private ConcurrentHashMap<String, WebFile> testWebFile = new ConcurrentHashMap<>();
    private LinkedList<HttpFileTask> httpFileLoadTasks = new LinkedList<>();
    private HashMap<String, HttpFileTask> httpFileLoadTasksByKeys = new HashMap<>();
    private HashMap<String, Runnable> retryHttpsTasks = new HashMap<>();
    private int currentHttpFileLoadTasksCount = 0;
    private String ignoreRemoval = null;
    private volatile long lastCacheOutTime = 0;
    private int lastImageNum = 0;
    private File telegramPath = null;

    public void moveToFront(String str) {
        if (str == null) {
            return;
        }
        if (this.memCache.get(str) != null) {
            this.memCache.moveToFront(str);
        }
        if (this.smallImagesMemCache.get(str) == null) {
            return;
        }
        this.smallImagesMemCache.moveToFront(str);
    }

    public void putThumbsToCache(ArrayList<MessageThumb> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            putImageToCache(arrayList.get(i).drawable, arrayList.get(i).key, true);
        }
    }

    /* loaded from: classes.dex */
    public static class ThumbGenerateInfo {
        private boolean big;
        private String filter;
        private ArrayList<ImageReceiver> imageReceiverArray;
        private ArrayList<Integer> imageReceiverGuidsArray;
        private TLRPC$Document parentDocument;

        private ThumbGenerateInfo() {
            this.imageReceiverArray = new ArrayList<>();
            this.imageReceiverGuidsArray = new ArrayList<>();
        }
    }

    /* loaded from: classes.dex */
    public class HttpFileTask extends AsyncTask<Void, Void, Boolean> {
        private int currentAccount;
        private String ext;
        private int fileSize;
        private long lastProgressTime;
        private File tempFile;
        private String url;
        private RandomAccessFile fileOutputStream = null;
        private boolean canRetry = true;

        public HttpFileTask(String str, File file, String str2, int i) {
            ImageLoader.this = r1;
            this.url = str;
            this.tempFile = file;
            this.ext = str2;
            this.currentAccount = i;
        }

        private void reportProgress(final long j, final long j2) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (j != j2) {
                long j3 = this.lastProgressTime;
                if (j3 != 0 && j3 >= elapsedRealtime - 100) {
                    return;
                }
            }
            this.lastProgressTime = elapsedRealtime;
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$HttpFileTask$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.HttpFileTask.this.lambda$reportProgress$1(j, j2);
                }
            });
        }

        public /* synthetic */ void lambda$reportProgress$1(final long j, final long j2) {
            ImageLoader.this.fileProgresses.put(this.url, new long[]{j, j2});
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$HttpFileTask$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.HttpFileTask.this.lambda$reportProgress$0(j, j2);
                }
            });
        }

        public /* synthetic */ void lambda$reportProgress$0(long j, long j2) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.fileLoadProgressChanged, this.url, Long.valueOf(j), Long.valueOf(j2));
        }

        /* JADX WARN: Code restructure failed: missing block: B:76:0x0120, code lost:
            if (r5 != (-1)) goto L81;
         */
        /* JADX WARN: Code restructure failed: missing block: B:77:0x0122, code lost:
            r0 = r11.fileSize;
         */
        /* JADX WARN: Code restructure failed: missing block: B:78:0x0124, code lost:
            if (r0 == 0) goto L89;
         */
        /* JADX WARN: Code restructure failed: missing block: B:79:0x0126, code lost:
            reportProgress(r0, r0);
         */
        /* JADX WARN: Code restructure failed: missing block: B:80:0x012c, code lost:
            r0 = e;
         */
        /* JADX WARN: Code restructure failed: missing block: B:81:0x012e, code lost:
            r1 = false;
         */
        /* JADX WARN: Code restructure failed: missing block: B:84:0x0132, code lost:
            org.telegram.messenger.FileLog.e(r0);
         */
        /* JADX WARN: Code restructure failed: missing block: B:85:0x0136, code lost:
            r0 = th;
         */
        /* JADX WARN: Code restructure failed: missing block: B:88:0x013a, code lost:
            org.telegram.messenger.FileLog.e(r0);
         */
        /* JADX WARN: Removed duplicated region for block: B:103:0x014e A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:114:0x00ad A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:92:0x0142 A[Catch: all -> 0x0148, TRY_LEAVE, TryCatch #5 {all -> 0x0148, blocks: (B:90:0x013e, B:92:0x0142), top: B:110:0x013e }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public java.lang.Boolean doInBackground(java.lang.Void... r12) {
            /*
                Method dump skipped, instructions count: 347
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.HttpFileTask.doInBackground(java.lang.Void[]):java.lang.Boolean");
        }

        public void onPostExecute(Boolean bool) {
            ImageLoader.this.runHttpFileLoadTasks(this, bool.booleanValue() ? 2 : 1);
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            ImageLoader.this.runHttpFileLoadTasks(this, 2);
        }
    }

    /* loaded from: classes.dex */
    public class ArtworkLoadTask extends AsyncTask<Void, Void, String> {
        private CacheImage cacheImage;
        private boolean canRetry = true;
        private HttpURLConnection httpConnection;
        private boolean small;

        public ArtworkLoadTask(CacheImage cacheImage) {
            ImageLoader.this = r2;
            boolean z = true;
            this.cacheImage = cacheImage;
            this.small = Uri.parse(cacheImage.imageLocation.path).getQueryParameter("s") == null ? false : z;
        }

        public String doInBackground(Void... voidArr) {
            ByteArrayOutputStream byteArrayOutputStream;
            InputStream inputStream;
            Throwable th;
            int read;
            int responseCode;
            try {
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(this.cacheImage.imageLocation.path.replace("athumb://", "https://")).openConnection();
                    this.httpConnection = httpURLConnection;
                    httpURLConnection.setConnectTimeout(5000);
                    this.httpConnection.setReadTimeout(5000);
                    this.httpConnection.connect();
                    try {
                        HttpURLConnection httpURLConnection2 = this.httpConnection;
                        if (httpURLConnection2 != null && (responseCode = httpURLConnection2.getResponseCode()) != 200 && responseCode != 202 && responseCode != 304) {
                            this.canRetry = false;
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e, false);
                    }
                    InputStream inputStream2 = this.httpConnection.getInputStream();
                    try {
                        ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                        try {
                            byte[] bArr = new byte[32768];
                            while (!isCancelled() && (read = inputStream2.read(bArr)) > 0) {
                                byteArrayOutputStream2.write(bArr, 0, read);
                            }
                            this.canRetry = false;
                            JSONArray jSONArray = new JSONObject(new String(byteArrayOutputStream2.toByteArray())).getJSONArray("results");
                            if (jSONArray.length() <= 0) {
                                try {
                                    HttpURLConnection httpURLConnection3 = this.httpConnection;
                                    if (httpURLConnection3 != null) {
                                        httpURLConnection3.disconnect();
                                    }
                                } catch (Throwable unused) {
                                }
                                if (inputStream2 != null) {
                                    try {
                                        inputStream2.close();
                                    } catch (Throwable th2) {
                                        FileLog.e(th2);
                                    }
                                }
                                byteArrayOutputStream2.close();
                            }
                            String string = jSONArray.getJSONObject(0).getString("artworkUrl100");
                            if (this.small) {
                                try {
                                    HttpURLConnection httpURLConnection4 = this.httpConnection;
                                    if (httpURLConnection4 != null) {
                                        httpURLConnection4.disconnect();
                                    }
                                } catch (Throwable unused2) {
                                }
                                if (inputStream2 != null) {
                                    try {
                                        inputStream2.close();
                                    } catch (Throwable th3) {
                                        FileLog.e(th3);
                                    }
                                }
                                try {
                                    byteArrayOutputStream2.close();
                                } catch (Exception unused3) {
                                }
                                return string;
                            }
                            String replace = string.replace("100x100", "600x600");
                            try {
                                HttpURLConnection httpURLConnection5 = this.httpConnection;
                                if (httpURLConnection5 != null) {
                                    httpURLConnection5.disconnect();
                                }
                            } catch (Throwable unused4) {
                            }
                            if (inputStream2 != null) {
                                try {
                                    inputStream2.close();
                                } catch (Throwable th4) {
                                    FileLog.e(th4);
                                }
                            }
                            try {
                                byteArrayOutputStream2.close();
                            } catch (Exception unused5) {
                            }
                            return replace;
                        } catch (Throwable th5) {
                            inputStream = inputStream2;
                            th = th5;
                            byteArrayOutputStream = byteArrayOutputStream2;
                            try {
                                if (th instanceof SocketTimeoutException) {
                                    if (ApplicationLoader.isNetworkOnline()) {
                                        this.canRetry = false;
                                    }
                                } else if (th instanceof UnknownHostException) {
                                    this.canRetry = false;
                                } else if (th instanceof SocketException) {
                                    if (th.getMessage() != null && th.getMessage().contains("ECONNRESET")) {
                                        this.canRetry = false;
                                    }
                                } else if (th instanceof FileNotFoundException) {
                                    this.canRetry = false;
                                }
                                FileLog.e(th, false);
                                try {
                                    HttpURLConnection httpURLConnection6 = this.httpConnection;
                                    if (httpURLConnection6 != null) {
                                        httpURLConnection6.disconnect();
                                    }
                                } catch (Throwable unused6) {
                                }
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (Throwable th6) {
                                        FileLog.e(th6);
                                    }
                                }
                                if (byteArrayOutputStream != null) {
                                    byteArrayOutputStream.close();
                                }
                                return null;
                            } catch (Throwable th7) {
                                try {
                                    HttpURLConnection httpURLConnection7 = this.httpConnection;
                                    if (httpURLConnection7 != null) {
                                        httpURLConnection7.disconnect();
                                    }
                                } catch (Throwable unused7) {
                                }
                                if (inputStream != null) {
                                    try {
                                        inputStream.close();
                                    } catch (Throwable th8) {
                                        FileLog.e(th8);
                                    }
                                }
                                if (byteArrayOutputStream != null) {
                                    try {
                                        byteArrayOutputStream.close();
                                    } catch (Exception unused8) {
                                    }
                                }
                                throw th7;
                            }
                        }
                    } catch (Throwable th9) {
                        byteArrayOutputStream = null;
                        inputStream = inputStream2;
                        th = th9;
                    }
                } catch (Throwable th10) {
                    th = th10;
                    inputStream = null;
                    byteArrayOutputStream = null;
                }
            } catch (Exception unused9) {
            }
        }

        public void onPostExecute(final String str) {
            if (str != null) {
                ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$ArtworkLoadTask$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        ImageLoader.ArtworkLoadTask.this.lambda$onPostExecute$0(str);
                    }
                });
            } else if (this.canRetry) {
                ImageLoader.this.artworkLoadError(this.cacheImage.url);
            }
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$ArtworkLoadTask$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.ArtworkLoadTask.this.lambda$onPostExecute$1();
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$0(String str) {
            CacheImage cacheImage = this.cacheImage;
            cacheImage.httpTask = new HttpImageTask(cacheImage, 0, str);
            ImageLoader.this.httpTasks.add(this.cacheImage.httpTask);
            ImageLoader.this.runHttpTasks(false);
        }

        public /* synthetic */ void lambda$onPostExecute$1() {
            ImageLoader.this.runArtworkTasks(true);
        }

        public /* synthetic */ void lambda$onCancelled$2() {
            ImageLoader.this.runArtworkTasks(true);
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$ArtworkLoadTask$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.ArtworkLoadTask.this.lambda$onCancelled$2();
                }
            });
        }
    }

    /* loaded from: classes.dex */
    public class HttpImageTask extends AsyncTask<Void, Void, Boolean> {
        private CacheImage cacheImage;
        private boolean canRetry = true;
        private RandomAccessFile fileOutputStream;
        private HttpURLConnection httpConnection;
        private long imageSize;
        private long lastProgressTime;
        private String overrideUrl;

        public static /* synthetic */ void lambda$doInBackground$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        }

        public HttpImageTask(CacheImage cacheImage, long j) {
            ImageLoader.this = r1;
            this.cacheImage = cacheImage;
            this.imageSize = j;
        }

        public HttpImageTask(CacheImage cacheImage, int i, String str) {
            ImageLoader.this = r1;
            this.cacheImage = cacheImage;
            this.imageSize = i;
            this.overrideUrl = str;
        }

        private void reportProgress(final long j, final long j2) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (j != j2) {
                long j3 = this.lastProgressTime;
                if (j3 != 0 && j3 >= elapsedRealtime - 100) {
                    return;
                }
            }
            this.lastProgressTime = elapsedRealtime;
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$HttpImageTask$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$reportProgress$1(j, j2);
                }
            });
        }

        public /* synthetic */ void lambda$reportProgress$1(final long j, final long j2) {
            ImageLoader.this.fileProgresses.put(this.cacheImage.url, new long[]{j, j2});
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$HttpImageTask$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$reportProgress$0(j, j2);
                }
            });
        }

        public /* synthetic */ void lambda$reportProgress$0(long j, long j2) {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileLoadProgressChanged, this.cacheImage.url, Long.valueOf(j), Long.valueOf(j2));
        }

        /* JADX WARN: Can't wrap try/catch for region: R(16:2|(7:135|4|(1:14)|15|(1:17)|18|(15:20|127|21|50|(6:141|52|(1:60)|63|(3:131|67|(1:75))|(6:79|137|80|(2:81|(1:147)(3:129|83|(3:85|(3:145|87|150)(1:149)|148)(1:146)))|97|101))|133|104|(1:106)|125|109|(1:111)|(2:139|114)|(1:122)|123|124))|49|50|(0)|133|104|(0)|125|109|(0)|(0)|(3:118|120|122)|123|124|(1:(0))) */
        /* JADX WARN: Code restructure failed: missing block: B:100:0x0183, code lost:
            r1 = r2;
         */
        /* JADX WARN: Code restructure failed: missing block: B:103:0x0187, code lost:
            org.telegram.messenger.FileLog.e(r1);
         */
        /* JADX WARN: Code restructure failed: missing block: B:107:0x0194, code lost:
            r0 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:108:0x0195, code lost:
            org.telegram.messenger.FileLog.e(r0);
         */
        /* JADX WARN: Code restructure failed: missing block: B:89:0x0169, code lost:
            if (r7 != (-1)) goto L97;
         */
        /* JADX WARN: Code restructure failed: missing block: B:90:0x016b, code lost:
            r2 = r12.imageSize;
         */
        /* JADX WARN: Code restructure failed: missing block: B:91:0x016f, code lost:
            if (r2 == 0) goto L101;
         */
        /* JADX WARN: Code restructure failed: missing block: B:92:0x0171, code lost:
            reportProgress(r2, r2);
         */
        /* JADX WARN: Code restructure failed: missing block: B:93:0x0175, code lost:
            r2 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:94:0x0176, code lost:
            r1 = r2;
            r2 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:95:0x0179, code lost:
            r2 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:96:0x017a, code lost:
            r1 = r2;
            r2 = true;
         */
        /* JADX WARN: Code restructure failed: missing block: B:99:0x0180, code lost:
            org.telegram.messenger.FileLog.e(r1);
         */
        /* JADX WARN: Removed duplicated region for block: B:106:0x018e A[Catch: all -> 0x0194, TRY_LEAVE, TryCatch #4 {all -> 0x0194, blocks: (B:104:0x018a, B:106:0x018e), top: B:133:0x018a }] */
        /* JADX WARN: Removed duplicated region for block: B:111:0x019c A[Catch: all -> 0x01a0, TRY_LEAVE, TryCatch #0 {all -> 0x01a0, blocks: (B:109:0x0198, B:111:0x019c), top: B:125:0x0198 }] */
        /* JADX WARN: Removed duplicated region for block: B:118:0x01ad  */
        /* JADX WARN: Removed duplicated region for block: B:139:0x01a3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:141:0x00ee A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public java.lang.Boolean doInBackground(java.lang.Void... r13) {
            /*
                Method dump skipped, instructions count: 454
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.HttpImageTask.doInBackground(java.lang.Void[]):java.lang.Boolean");
        }

        public void onPostExecute(final Boolean bool) {
            if (!bool.booleanValue() && this.canRetry) {
                ImageLoader.this.httpFileLoadError(this.cacheImage.url);
            } else {
                ImageLoader imageLoader = ImageLoader.this;
                CacheImage cacheImage = this.cacheImage;
                imageLoader.fileDidLoaded(cacheImage.url, cacheImage.finalFilePath, 0);
            }
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$HttpImageTask$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onPostExecute$4(bool);
                }
            });
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$HttpImageTask$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onPostExecute$5();
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$4(final Boolean bool) {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$HttpImageTask$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onPostExecute$3(bool);
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$3(Boolean bool) {
            if (bool.booleanValue()) {
                NotificationCenter notificationCenter = NotificationCenter.getInstance(this.cacheImage.currentAccount);
                int i = NotificationCenter.fileLoaded;
                CacheImage cacheImage = this.cacheImage;
                notificationCenter.postNotificationName(i, cacheImage.url, cacheImage.finalFilePath);
                return;
            }
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileLoadFailed, this.cacheImage.url, 2);
        }

        public /* synthetic */ void lambda$onPostExecute$5() {
            ImageLoader.this.runHttpTasks(true);
        }

        public /* synthetic */ void lambda$onCancelled$6() {
            ImageLoader.this.runHttpTasks(true);
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$HttpImageTask$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onCancelled$6();
                }
            });
            Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$HttpImageTask$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onCancelled$8();
                }
            });
        }

        public /* synthetic */ void lambda$onCancelled$8() {
            ImageLoader.this.fileProgresses.remove(this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$HttpImageTask$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.HttpImageTask.this.lambda$onCancelled$7();
                }
            });
        }

        public /* synthetic */ void lambda$onCancelled$7() {
            NotificationCenter.getInstance(this.cacheImage.currentAccount).postNotificationName(NotificationCenter.fileLoadFailed, this.cacheImage.url, 1);
        }
    }

    /* loaded from: classes.dex */
    public class ThumbGenerateTask implements Runnable {
        private ThumbGenerateInfo info;
        private int mediaType;
        private File originalPath;

        public ThumbGenerateTask(int i, File file, ThumbGenerateInfo thumbGenerateInfo) {
            ImageLoader.this = r1;
            this.mediaType = i;
            this.originalPath = file;
            this.info = thumbGenerateInfo;
        }

        private void removeTask() {
            ThumbGenerateInfo thumbGenerateInfo = this.info;
            if (thumbGenerateInfo == null) {
                return;
            }
            final String attachFileName = FileLoader.getAttachFileName(thumbGenerateInfo.parentDocument);
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$ThumbGenerateTask$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.ThumbGenerateTask.this.lambda$removeTask$0(attachFileName);
                }
            });
        }

        public /* synthetic */ void lambda$removeTask$0(String str) {
            ImageLoader.this.thumbGenerateTasks.remove(str);
        }

        @Override // java.lang.Runnable
        public void run() {
            int i;
            Bitmap createScaledBitmap;
            try {
                if (this.info == null) {
                    removeTask();
                    return;
                }
                final String str = "q_" + this.info.parentDocument.dc_id + "_" + this.info.parentDocument.id;
                File file = new File(FileLoader.getDirectory(4), str + ".jpg");
                if (!file.exists() && this.originalPath.exists()) {
                    if (this.info.big) {
                        Point point = AndroidUtilities.displaySize;
                        i = Math.max(point.x, point.y);
                    } else {
                        Point point2 = AndroidUtilities.displaySize;
                        i = Math.min(180, Math.min(point2.x, point2.y) / 4);
                    }
                    int i2 = this.mediaType;
                    Bitmap bitmap = null;
                    if (i2 == 0) {
                        float f = i;
                        bitmap = ImageLoader.loadBitmap(this.originalPath.toString(), null, f, f, false);
                    } else {
                        int i3 = 2;
                        if (i2 == 2) {
                            String file2 = this.originalPath.toString();
                            if (!this.info.big) {
                                i3 = 1;
                            }
                            bitmap = SendMessagesHelper.createVideoThumbnail(file2, i3);
                        } else if (i2 == 3) {
                            String lowerCase = this.originalPath.toString().toLowerCase();
                            if (lowerCase.endsWith("mp4")) {
                                String file3 = this.originalPath.toString();
                                if (!this.info.big) {
                                    i3 = 1;
                                }
                                bitmap = SendMessagesHelper.createVideoThumbnail(file3, i3);
                            } else if (lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg") || lowerCase.endsWith(".png") || lowerCase.endsWith(".gif")) {
                                float f2 = i;
                                bitmap = ImageLoader.loadBitmap(lowerCase, null, f2, f2, false);
                            }
                        }
                    }
                    if (bitmap == null) {
                        removeTask();
                        return;
                    }
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    if (width != 0 && height != 0) {
                        float f3 = width;
                        float f4 = i;
                        float f5 = height;
                        float min = Math.min(f3 / f4, f5 / f4);
                        if (min > 1.0f && (createScaledBitmap = Bitmaps.createScaledBitmap(bitmap, (int) (f3 / min), (int) (f5 / min), true)) != bitmap) {
                            bitmap.recycle();
                            bitmap = createScaledBitmap;
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, this.info.big ? 83 : 60, fileOutputStream);
                        try {
                            fileOutputStream.close();
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                        final BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
                        final ArrayList arrayList = new ArrayList(this.info.imageReceiverArray);
                        final ArrayList arrayList2 = new ArrayList(this.info.imageReceiverGuidsArray);
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$ThumbGenerateTask$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                ImageLoader.ThumbGenerateTask.this.lambda$run$1(str, arrayList, bitmapDrawable, arrayList2);
                            }
                        });
                        return;
                    }
                    removeTask();
                    return;
                }
                removeTask();
            } catch (Throwable th) {
                FileLog.e(th);
                removeTask();
            }
        }

        public /* synthetic */ void lambda$run$1(String str, ArrayList arrayList, BitmapDrawable bitmapDrawable, ArrayList arrayList2) {
            removeTask();
            if (this.info.filter != null) {
                str = str + "@" + this.info.filter;
            }
            for (int i = 0; i < arrayList.size(); i++) {
                ((ImageReceiver) arrayList.get(i)).setImageBitmapByKey(bitmapDrawable, str, 0, false, ((Integer) arrayList2.get(i)).intValue());
            }
            ImageLoader.this.memCache.put(str, bitmapDrawable);
        }
    }

    public static String decompressGzip(File file) {
        StringBuilder sb = new StringBuilder();
        if (file == null) {
            return "";
        }
        try {
            GZIPInputStream gZIPInputStream = new GZIPInputStream(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gZIPInputStream, "UTF-8"));
            while (true) {
                try {
                    String readLine = bufferedReader.readLine();
                    if (readLine != null) {
                        sb.append(readLine);
                    } else {
                        String sb2 = sb.toString();
                        bufferedReader.close();
                        gZIPInputStream.close();
                        return sb2;
                    }
                } catch (Throwable th) {
                    try {
                        bufferedReader.close();
                    } catch (Throwable unused) {
                    }
                    throw th;
                }
            }
        } catch (Exception unused2) {
            return "";
        }
    }

    /* loaded from: classes.dex */
    public class CacheOutTask implements Runnable {
        private CacheImage cacheImage;
        private boolean isCancelled;
        private Thread runningThread;
        private final Object sync = new Object();

        public CacheOutTask(CacheImage cacheImage) {
            ImageLoader.this = r1;
            this.cacheImage = cacheImage;
        }

        /* JADX WARN: Can't wrap try/catch for region: R(10:735|(2:737|(8:739|761|741|(1:743)(1:744)|745|(1:749)(1:750)|751|752))|740|761|741|(0)(0)|745|(0)(0)|751|752) */
        /* JADX WARN: Can't wrap try/catch for region: R(20:814|(27:830|212|817|213|(1:215)(1:216)|217|(2:219|(20:221|775|223|244|(17:246|(3:248|(1:250)(1:251)|252)(2:254|(14:256|(1:258)(1:259)|260|265|(1:267)|268|813|269|(12:271|(5:273|274|815|275|276)(1:279)|811|280|(1:282)(2:283|(1:285)(2:286|(1:288)(1:289)))|807|290|801|291|(1:293)|(1:366)(10:773|299|763|(2:311|(12:783|313|(1:318)(1:317)|(1:320)|321|322|323|324|(3:329|331|(1:333))|330|331|(0))(3:337|(1:339)(1:340)|341))(2:(4:799|303|304|305)(1:308)|309)|342|(1:347)(1:346)|348|(1:350)|351|(1:361)(4:357|(1:358)|834|360))|367)(2:(10:378|379|(1:381)(1:382)|383|794|384|(1:386)|387|(4:389|(1:390)|835|392)(1:393)|394)(1:397)|398)|399|405|(3:778|407|6f6)(7:527|(1:529)|(3:780|531|(6:533|534|(3:770|536|(1:538))(1:540)|790|544|945))|543|790|544|945)|724|(3:(1:732)(1:733)|734|842)(3:(1:728)(1:729)|730|841))(2:261|(1:263)))|253|265|(0)|268|813|269|(0)(0)|399|405|(0)(0)|724|(0)|(0)(0)|734|842)|264|265|(0)|268|813|269|(0)(0)|399|405|(0)(0)|724|(0)|(0)(0)|734|842))|222|775|223|244|(0)|264|265|(0)|268|813|269|(0)(0)|399|405|(0)(0)|724|(0)|(0)(0)|734|842)|243|244|(0)|264|265|(0)|268|813|269|(0)(0)|399|405|(0)(0)|724|(0)|(0)(0)|734|842) */
        /* JADX WARN: Can't wrap try/catch for region: R(22:(6:830|212|817|213|(1:215)(1:216)|217)|(2:219|(20:221|775|223|244|(17:246|(3:248|(1:250)(1:251)|252)(2:254|(14:256|(1:258)(1:259)|260|265|(1:267)|268|813|269|(12:271|(5:273|274|815|275|276)(1:279)|811|280|(1:282)(2:283|(1:285)(2:286|(1:288)(1:289)))|807|290|801|291|(1:293)|(1:366)(10:773|299|763|(2:311|(12:783|313|(1:318)(1:317)|(1:320)|321|322|323|324|(3:329|331|(1:333))|330|331|(0))(3:337|(1:339)(1:340)|341))(2:(4:799|303|304|305)(1:308)|309)|342|(1:347)(1:346)|348|(1:350)|351|(1:361)(4:357|(1:358)|834|360))|367)(2:(10:378|379|(1:381)(1:382)|383|794|384|(1:386)|387|(4:389|(1:390)|835|392)(1:393)|394)(1:397)|398)|399|405|(3:778|407|6f6)(7:527|(1:529)|(3:780|531|(6:533|534|(3:770|536|(1:538))(1:540)|790|544|945))|543|790|544|945)|724|(3:(1:732)(1:733)|734|842)(3:(1:728)(1:729)|730|841))(2:261|(1:263)))|253|265|(0)|268|813|269|(0)(0)|399|405|(0)(0)|724|(0)|(0)(0)|734|842)|264|265|(0)|268|813|269|(0)(0)|399|405|(0)(0)|724|(0)|(0)(0)|734|842))|222|775|223|244|(0)|264|265|(0)|268|813|269|(0)(0)|399|405|(0)(0)|724|(0)|(0)(0)|734|842) */
        /* JADX WARN: Code restructure failed: missing block: B:104:0x01c7, code lost:
            if (r0[1] == (-117)) goto L803;
         */
        /* JADX WARN: Code restructure failed: missing block: B:224:0x03f4, code lost:
            r0 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:225:0x03f5, code lost:
            org.telegram.messenger.FileLog.e(r0);
         */
        /* JADX WARN: Code restructure failed: missing block: B:400:0x06cf, code lost:
            r0 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:401:0x06d0, code lost:
            r10 = r0;
            r5 = false;
            r6 = 0.0f;
         */
        /* JADX WARN: Code restructure failed: missing block: B:746:0x0c22, code lost:
            r0 = move-exception;
         */
        /* JADX WARN: Code restructure failed: missing block: B:747:0x0c23, code lost:
            org.telegram.messenger.FileLog.e(r0);
            r5 = null;
         */
        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Not initialized variable reg: 29, insn: 0x0625: MOVE  (r5 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r29 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:363:0x0624 */
        /* JADX WARN: Not initialized variable reg: 30, insn: 0x0627: MOVE  (r8 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r30 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:363:0x0624 */
        /* JADX WARN: Not initialized variable reg: 31, insn: 0x0629: MOVE  (r7 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r31 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:363:0x0624 */
        /* JADX WARN: Removed duplicated region for block: B:124:0x01f6  */
        /* JADX WARN: Removed duplicated region for block: B:126:0x01f9  */
        /* JADX WARN: Removed duplicated region for block: B:127:0x0217  */
        /* JADX WARN: Removed duplicated region for block: B:129:0x0232  */
        /* JADX WARN: Removed duplicated region for block: B:130:0x0237  */
        /* JADX WARN: Removed duplicated region for block: B:160:0x02aa  */
        /* JADX WARN: Removed duplicated region for block: B:180:0x02fe  */
        /* JADX WARN: Removed duplicated region for block: B:186:0x0356  */
        /* JADX WARN: Removed duplicated region for block: B:246:0x042b  */
        /* JADX WARN: Removed duplicated region for block: B:267:0x04a0  */
        /* JADX WARN: Removed duplicated region for block: B:271:0x04b2 A[Catch: all -> 0x06cf, TryCatch #36 {all -> 0x06cf, blocks: (B:269:0x04ac, B:271:0x04b2, B:273:0x04bb, B:379:0x065e, B:381:0x0662, B:382:0x0665, B:383:0x0667), top: B:813:0x04ac }] */
        /* JADX WARN: Removed duplicated region for block: B:333:0x05b0 A[Catch: all -> 0x0623, TryCatch #2 {all -> 0x0623, blocks: (B:324:0x0598, B:326:0x059e, B:331:0x05a8, B:333:0x05b0, B:339:0x05c4, B:340:0x05ce, B:341:0x05d3, B:342:0x05da, B:346:0x05e8, B:347:0x05f1, B:351:0x0602, B:358:0x0612, B:360:0x061c, B:361:0x061f), top: B:763:0x0529 }] */
        /* JADX WARN: Removed duplicated region for block: B:377:0x065b  */
        /* JADX WARN: Removed duplicated region for block: B:527:0x08f9  */
        /* JADX WARN: Removed duplicated region for block: B:57:0x0102  */
        /* JADX WARN: Removed duplicated region for block: B:635:0x0aa4  */
        /* JADX WARN: Removed duplicated region for block: B:642:0x0abc A[Catch: all -> 0x0bad, TryCatch #10 {all -> 0x0bad, blocks: (B:636:0x0aa6, B:638:0x0ab0, B:640:0x0ab6, B:642:0x0abc, B:644:0x0ac2, B:650:0x0ad8, B:656:0x0ae6, B:658:0x0aec, B:659:0x0af6, B:661:0x0afc, B:664:0x0b0b, B:667:0x0b13, B:669:0x0b21, B:671:0x0b2c, B:675:0x0b33), top: B:777:0x0aa2 }] */
        /* JADX WARN: Removed duplicated region for block: B:664:0x0b0b A[Catch: all -> 0x0bad, TryCatch #10 {all -> 0x0bad, blocks: (B:636:0x0aa6, B:638:0x0ab0, B:640:0x0ab6, B:642:0x0abc, B:644:0x0ac2, B:650:0x0ad8, B:656:0x0ae6, B:658:0x0aec, B:659:0x0af6, B:661:0x0afc, B:664:0x0b0b, B:667:0x0b13, B:669:0x0b21, B:671:0x0b2c, B:675:0x0b33), top: B:777:0x0aa2 }] */
        /* JADX WARN: Removed duplicated region for block: B:70:0x012e  */
        /* JADX WARN: Removed duplicated region for block: B:726:0x0bc5 A[ADDED_TO_REGION] */
        /* JADX WARN: Removed duplicated region for block: B:732:0x0bd7  */
        /* JADX WARN: Removed duplicated region for block: B:733:0x0bdd  */
        /* JADX WARN: Removed duplicated region for block: B:743:0x0c1a  */
        /* JADX WARN: Removed duplicated region for block: B:744:0x0c1c  */
        /* JADX WARN: Removed duplicated region for block: B:749:0x0c29  */
        /* JADX WARN: Removed duplicated region for block: B:750:0x0c30  */
        /* JADX WARN: Removed duplicated region for block: B:766:0x0b9d A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:778:0x06eb A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:779:0x0946 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:782:0x0995 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:822:0x0243 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Type inference failed for: r37v0, types: [org.telegram.messenger.ImageLoader$CacheOutTask] */
        /* JADX WARN: Type inference failed for: r7v3 */
        /* JADX WARN: Type inference failed for: r7v72 */
        /* JADX WARN: Type inference failed for: r7v73 */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 3130
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.CacheOutTask.run():void");
        }

        private void loadLastFrame(final RLottieDrawable rLottieDrawable, int i, int i2) {
            final Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(createBitmap);
            canvas.scale(2.0f, 2.0f, i / 2.0f, i2 / 2.0f);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$CacheOutTask$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.CacheOutTask.this.lambda$loadLastFrame$1(rLottieDrawable, canvas, createBitmap);
                }
            });
        }

        public /* synthetic */ void lambda$loadLastFrame$1(final RLottieDrawable rLottieDrawable, final Canvas canvas, final Bitmap bitmap) {
            rLottieDrawable.setOnFrameReadyRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$CacheOutTask$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.CacheOutTask.this.lambda$loadLastFrame$0(rLottieDrawable, canvas, bitmap);
                }
            });
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getFramesCount() - 1, true, true);
        }

        public /* synthetic */ void lambda$loadLastFrame$0(RLottieDrawable rLottieDrawable, Canvas canvas, Bitmap bitmap) {
            BitmapDrawable bitmapDrawable = null;
            rLottieDrawable.setOnFrameReadyRunnable(null);
            if (rLottieDrawable.getBackgroundBitmap() != null || rLottieDrawable.getRenderingBitmap() != null) {
                canvas.drawBitmap(rLottieDrawable.getBackgroundBitmap() != null ? rLottieDrawable.getBackgroundBitmap() : rLottieDrawable.getRenderingBitmap(), 0.0f, 0.0f, (Paint) null);
                bitmapDrawable = new BitmapDrawable(bitmap);
            }
            onPostExecute(bitmapDrawable);
            rLottieDrawable.recycle();
        }

        private void onPostExecute(final Drawable drawable) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$CacheOutTask$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.CacheOutTask.this.lambda$onPostExecute$3(drawable);
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$3(Drawable drawable) {
            final String str;
            final Drawable drawable2 = null;
            r1 = null;
            r1 = null;
            r1 = null;
            String str2 = null;
            if (drawable instanceof RLottieDrawable) {
                RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                Drawable drawable3 = (Drawable) ImageLoader.this.lottieMemCache.get(this.cacheImage.key);
                if (drawable3 == null) {
                    ImageLoader.this.lottieMemCache.put(this.cacheImage.key, rLottieDrawable);
                    drawable = rLottieDrawable;
                } else {
                    rLottieDrawable.recycle();
                    drawable = drawable3;
                }
                if (drawable != null) {
                    ImageLoader.this.incrementUseCount(this.cacheImage.key);
                    str2 = this.cacheImage.key;
                }
            } else if (drawable instanceof AnimatedFileDrawable) {
                AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) drawable;
                if (animatedFileDrawable.isWebmSticker) {
                    BitmapDrawable fromLottieCache = ImageLoader.this.getFromLottieCache(this.cacheImage.key);
                    if (fromLottieCache == null) {
                        ImageLoader.this.lottieMemCache.put(this.cacheImage.key, animatedFileDrawable);
                        drawable = animatedFileDrawable;
                    } else {
                        animatedFileDrawable.recycle();
                        drawable = fromLottieCache;
                    }
                    ImageLoader.this.incrementUseCount(this.cacheImage.key);
                    str2 = this.cacheImage.key;
                }
            } else if (!(drawable instanceof BitmapDrawable)) {
                str = null;
                ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$CacheOutTask$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ImageLoader.CacheOutTask.this.lambda$onPostExecute$2(drawable2, str);
                    }
                });
            } else {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                BitmapDrawable fromMemCache = ImageLoader.this.getFromMemCache(this.cacheImage.key);
                boolean z = true;
                if (fromMemCache == null) {
                    if (this.cacheImage.key.endsWith("_f")) {
                        ImageLoader.this.wallpaperMemCache.put(this.cacheImage.key, bitmapDrawable);
                        z = false;
                        drawable = bitmapDrawable;
                    } else if (this.cacheImage.key.endsWith("_isc") || bitmapDrawable.getBitmap().getWidth() > AndroidUtilities.density * 80.0f || bitmapDrawable.getBitmap().getHeight() > AndroidUtilities.density * 80.0f) {
                        ImageLoader.this.memCache.put(this.cacheImage.key, bitmapDrawable);
                        drawable = bitmapDrawable;
                    } else {
                        ImageLoader.this.smallImagesMemCache.put(this.cacheImage.key, bitmapDrawable);
                        drawable = bitmapDrawable;
                    }
                } else {
                    bitmapDrawable.getBitmap().recycle();
                    drawable = fromMemCache;
                }
                if (drawable != null && z) {
                    ImageLoader.this.incrementUseCount(this.cacheImage.key);
                    str2 = this.cacheImage.key;
                }
            }
            String str3 = str2;
            drawable2 = drawable;
            str = str3;
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$CacheOutTask$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.CacheOutTask.this.lambda$onPostExecute$2(drawable2, str);
                }
            });
        }

        public /* synthetic */ void lambda$onPostExecute$2(Drawable drawable, String str) {
            this.cacheImage.setImageAndClear(drawable, str);
        }

        public void cancel() {
            synchronized (this.sync) {
                try {
                    this.isCancelled = true;
                    Thread thread = this.runningThread;
                    if (thread != null) {
                        thread.interrupt();
                    }
                } catch (Exception unused) {
                }
            }
        }
    }

    public boolean isAnimatedAvatar(String str) {
        return str != null && str.endsWith("avatar");
    }

    public BitmapDrawable getFromMemCache(String str) {
        BitmapDrawable bitmapDrawable = this.memCache.get(str);
        if (bitmapDrawable == null) {
            bitmapDrawable = this.smallImagesMemCache.get(str);
        }
        if (bitmapDrawable == null) {
            bitmapDrawable = this.wallpaperMemCache.get(str);
        }
        return bitmapDrawable == null ? getFromLottieCache(str) : bitmapDrawable;
    }

    public static Bitmap getStrippedPhotoBitmap(byte[] bArr, String str) {
        int length = (bArr.length - 3) + Bitmaps.header.length + Bitmaps.footer.length;
        byte[] bArr2 = bytesLocal.get();
        if (bArr2 == null || bArr2.length < length) {
            bArr2 = null;
        }
        if (bArr2 == null) {
            bArr2 = new byte[length];
            bytesLocal.set(bArr2);
        }
        byte[] bArr3 = Bitmaps.header;
        System.arraycopy(bArr3, 0, bArr2, 0, bArr3.length);
        System.arraycopy(bArr, 3, bArr2, Bitmaps.header.length, bArr.length - 3);
        System.arraycopy(Bitmaps.footer, 0, bArr2, (Bitmaps.header.length + bArr.length) - 3, Bitmaps.footer.length);
        bArr2[164] = bArr[1];
        bArr2[166] = bArr[2];
        Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr2, 0, length);
        if (decodeByteArray != null && !TextUtils.isEmpty(str) && str.contains("b")) {
            Utilities.blurBitmap(decodeByteArray, 3, 1, decodeByteArray.getWidth(), decodeByteArray.getHeight(), decodeByteArray.getRowBytes());
        }
        return decodeByteArray;
    }

    /* loaded from: classes.dex */
    public class CacheImage {
        protected ArtworkLoadTask artworkTask;
        protected CacheOutTask cacheTask;
        protected int currentAccount;
        protected File encryptionKeyPath;
        protected String ext;
        protected String filter;
        protected ArrayList<String> filters;
        protected File finalFilePath;
        protected HttpImageTask httpTask;
        protected ImageLocation imageLocation;
        protected ArrayList<ImageReceiver> imageReceiverArray;
        protected ArrayList<Integer> imageReceiverGuidsArray;
        protected int imageType;
        protected String key;
        protected ArrayList<String> keys;
        protected Object parentObject;
        protected SecureDocument secureDocument;
        protected long size;
        protected File tempFilePath;
        protected int type;
        protected ArrayList<Integer> types;
        protected String url;

        private CacheImage() {
            ImageLoader.this = r1;
            this.imageReceiverArray = new ArrayList<>();
            this.imageReceiverGuidsArray = new ArrayList<>();
            this.keys = new ArrayList<>();
            this.filters = new ArrayList<>();
            this.types = new ArrayList<>();
        }

        public void addImageReceiver(ImageReceiver imageReceiver, String str, String str2, int i, int i2) {
            int indexOf = this.imageReceiverArray.indexOf(imageReceiver);
            if (indexOf >= 0) {
                this.imageReceiverGuidsArray.set(indexOf, Integer.valueOf(i2));
                return;
            }
            this.imageReceiverArray.add(imageReceiver);
            this.imageReceiverGuidsArray.add(Integer.valueOf(i2));
            this.keys.add(str);
            this.filters.add(str2);
            this.types.add(Integer.valueOf(i));
            ImageLoader.this.imageLoadingByTag.put(imageReceiver.getTag(i), this);
        }

        public void replaceImageReceiver(ImageReceiver imageReceiver, String str, String str2, int i, int i2) {
            int indexOf = this.imageReceiverArray.indexOf(imageReceiver);
            if (indexOf == -1) {
                return;
            }
            if (this.types.get(indexOf).intValue() != i) {
                ArrayList<ImageReceiver> arrayList = this.imageReceiverArray;
                indexOf = arrayList.subList(indexOf + 1, arrayList.size()).indexOf(imageReceiver);
                if (indexOf == -1) {
                    return;
                }
            }
            this.imageReceiverGuidsArray.set(indexOf, Integer.valueOf(i2));
            this.keys.set(indexOf, str);
            this.filters.set(indexOf, str2);
        }

        public void setImageReceiverGuid(ImageReceiver imageReceiver, int i) {
            int indexOf = this.imageReceiverArray.indexOf(imageReceiver);
            if (indexOf == -1) {
                return;
            }
            this.imageReceiverGuidsArray.set(indexOf, Integer.valueOf(i));
        }

        public void removeImageReceiver(ImageReceiver imageReceiver) {
            int i = this.type;
            int i2 = 0;
            while (i2 < this.imageReceiverArray.size()) {
                ImageReceiver imageReceiver2 = this.imageReceiverArray.get(i2);
                if (imageReceiver2 == null || imageReceiver2 == imageReceiver) {
                    this.imageReceiverArray.remove(i2);
                    this.imageReceiverGuidsArray.remove(i2);
                    this.keys.remove(i2);
                    this.filters.remove(i2);
                    i = this.types.remove(i2).intValue();
                    if (imageReceiver2 != null) {
                        ImageLoader.this.imageLoadingByTag.remove(imageReceiver2.getTag(i));
                    }
                    i2--;
                }
                i2++;
            }
            if (this.imageReceiverArray.isEmpty()) {
                if (this.imageLocation != null && !ImageLoader.this.forceLoadingImages.containsKey(this.key)) {
                    ImageLocation imageLocation = this.imageLocation;
                    if (imageLocation.location != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.location, this.ext);
                    } else if (imageLocation.document != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.document);
                    } else if (imageLocation.secureDocument != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.secureDocument);
                    } else if (imageLocation.webFile != null) {
                        FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.imageLocation.webFile);
                    }
                }
                if (this.cacheTask != null) {
                    if (i == 1) {
                        ImageLoader.this.cacheThumbOutQueue.cancelRunnable(this.cacheTask);
                    } else {
                        ImageLoader.this.cacheOutQueue.cancelRunnable(this.cacheTask);
                    }
                    this.cacheTask.cancel();
                    this.cacheTask = null;
                }
                if (this.httpTask != null) {
                    ImageLoader.this.httpTasks.remove(this.httpTask);
                    this.httpTask.cancel(true);
                    this.httpTask = null;
                }
                if (this.artworkTask != null) {
                    ImageLoader.this.artworkTasks.remove(this.artworkTask);
                    this.artworkTask.cancel(true);
                    this.artworkTask = null;
                }
                if (this.url != null) {
                    ImageLoader.this.imageLoadingByUrl.remove(this.url);
                }
                if (this.key == null) {
                    return;
                }
                ImageLoader.this.imageLoadingByKeys.remove(this.key);
            }
        }

        public void setImageAndClear(final Drawable drawable, final String str) {
            if (drawable != null) {
                final ArrayList arrayList = new ArrayList(this.imageReceiverArray);
                final ArrayList arrayList2 = new ArrayList(this.imageReceiverGuidsArray);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$CacheImage$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ImageLoader.CacheImage.this.lambda$setImageAndClear$0(drawable, arrayList, arrayList2, str);
                    }
                });
            }
            for (int i = 0; i < this.imageReceiverArray.size(); i++) {
                ImageLoader.this.imageLoadingByTag.remove(this.imageReceiverArray.get(i).getTag(this.type));
            }
            this.imageReceiverArray.clear();
            this.imageReceiverGuidsArray.clear();
            if (this.url != null) {
                ImageLoader.this.imageLoadingByUrl.remove(this.url);
            }
            if (this.key != null) {
                ImageLoader.this.imageLoadingByKeys.remove(this.key);
            }
        }

        public /* synthetic */ void lambda$setImageAndClear$0(Drawable drawable, ArrayList arrayList, ArrayList arrayList2, String str) {
            int i = 0;
            if (drawable instanceof AnimatedFileDrawable) {
                AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) drawable;
                boolean z = false;
                while (i < arrayList.size()) {
                    if (((ImageReceiver) arrayList.get(i)).setImageBitmapByKey(animatedFileDrawable, this.key, this.type, false, ((Integer) arrayList2.get(i)).intValue())) {
                        z = true;
                    }
                    i++;
                }
                if (!z) {
                    animatedFileDrawable.recycle();
                }
            } else {
                while (i < arrayList.size()) {
                    ((ImageReceiver) arrayList.get(i)).setImageBitmapByKey(drawable, this.key, this.types.get(i).intValue(), false, ((Integer) arrayList2.get(i)).intValue());
                    i++;
                }
            }
            if (str != null) {
                ImageLoader.this.decrementUseCount(str);
            }
        }
    }

    public static ImageLoader getInstance() {
        ImageLoader imageLoader = Instance;
        if (imageLoader == null) {
            synchronized (ImageLoader.class) {
                imageLoader = Instance;
                if (imageLoader == null) {
                    imageLoader = new ImageLoader();
                    Instance = imageLoader;
                }
            }
        }
        return imageLoader;
    }

    public ImageLoader() {
        boolean z = true;
        this.thumbGeneratingQueue.setPriority(1);
        int memoryClass = ((ActivityManager) ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass();
        z = memoryClass < 192 ? false : z;
        this.canForce8888 = z;
        int min = Math.min(z ? 30 : 15, memoryClass / 7) * 1024 * 1024;
        float f = min;
        this.memCache = new LruCache<BitmapDrawable>((int) (0.8f * f)) { // from class: org.telegram.messenger.ImageLoader.1
            public int sizeOf(String str, BitmapDrawable bitmapDrawable) {
                return bitmapDrawable.getBitmap().getByteCount();
            }

            public void entryRemoved(boolean z2, String str, BitmapDrawable bitmapDrawable, BitmapDrawable bitmapDrawable2) {
                if (ImageLoader.this.ignoreRemoval == null || !ImageLoader.this.ignoreRemoval.equals(str)) {
                    Integer num = (Integer) ImageLoader.this.bitmapUseCounts.get(str);
                    if (num != null && num.intValue() != 0) {
                        return;
                    }
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    if (bitmap.isRecycled()) {
                        return;
                    }
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(bitmap);
                    AndroidUtilities.recycleBitmaps(arrayList);
                }
            }
        };
        this.smallImagesMemCache = new LruCache<BitmapDrawable>((int) (f * 0.2f)) { // from class: org.telegram.messenger.ImageLoader.2
            public int sizeOf(String str, BitmapDrawable bitmapDrawable) {
                return bitmapDrawable.getBitmap().getByteCount();
            }

            public void entryRemoved(boolean z2, String str, BitmapDrawable bitmapDrawable, BitmapDrawable bitmapDrawable2) {
                if (ImageLoader.this.ignoreRemoval == null || !ImageLoader.this.ignoreRemoval.equals(str)) {
                    Integer num = (Integer) ImageLoader.this.bitmapUseCounts.get(str);
                    if (num != null && num.intValue() != 0) {
                        return;
                    }
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    if (bitmap.isRecycled()) {
                        return;
                    }
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(bitmap);
                    AndroidUtilities.recycleBitmaps(arrayList);
                }
            }
        };
        this.wallpaperMemCache = new LruCache<BitmapDrawable>(min / 4) { // from class: org.telegram.messenger.ImageLoader.3
            public int sizeOf(String str, BitmapDrawable bitmapDrawable) {
                return bitmapDrawable.getBitmap().getByteCount();
            }
        };
        this.lottieMemCache = new LruCache<BitmapDrawable>(10485760) { // from class: org.telegram.messenger.ImageLoader.4
            public int sizeOf(String str, BitmapDrawable bitmapDrawable) {
                return bitmapDrawable.getIntrinsicWidth() * bitmapDrawable.getIntrinsicHeight() * 4 * 2;
            }

            public BitmapDrawable put(String str, BitmapDrawable bitmapDrawable) {
                if (bitmapDrawable instanceof AnimatedFileDrawable) {
                    ImageLoader.this.cachedAnimatedFileDrawables.add((AnimatedFileDrawable) bitmapDrawable);
                }
                return (BitmapDrawable) super.put(str, (String) bitmapDrawable);
            }

            public void entryRemoved(boolean z2, String str, BitmapDrawable bitmapDrawable, BitmapDrawable bitmapDrawable2) {
                Integer num = (Integer) ImageLoader.this.bitmapUseCounts.get(str);
                boolean z3 = bitmapDrawable instanceof AnimatedFileDrawable;
                if (z3) {
                    ImageLoader.this.cachedAnimatedFileDrawables.remove((AnimatedFileDrawable) bitmapDrawable);
                }
                if (num == null || num.intValue() == 0) {
                    if (z3) {
                        ((AnimatedFileDrawable) bitmapDrawable).recycle();
                    }
                    if (!(bitmapDrawable instanceof RLottieDrawable)) {
                        return;
                    }
                    ((RLottieDrawable) bitmapDrawable).recycle();
                }
            }
        };
        SparseArray sparseArray = new SparseArray();
        File cacheDir = AndroidUtilities.getCacheDir();
        if (!cacheDir.isDirectory()) {
            try {
                cacheDir.mkdirs();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        AndroidUtilities.createEmptyFile(new File(cacheDir, ".nomedia"));
        sparseArray.put(4, cacheDir);
        for (int i = 0; i < 4; i++) {
            FileLoader.getInstance(i).setDelegate(new AnonymousClass5(i));
        }
        FileLoader.setMediaDirs(sparseArray);
        AnonymousClass6 anonymousClass6 = new AnonymousClass6();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_BAD_REMOVAL");
        intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
        intentFilter.addAction("android.intent.action.MEDIA_EJECT");
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_NOFS");
        intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
        intentFilter.addAction("android.intent.action.MEDIA_SHARED");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTABLE");
        intentFilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
        intentFilter.addDataScheme("file");
        try {
            ApplicationLoader.applicationContext.registerReceiver(anonymousClass6, intentFilter);
        } catch (Throwable unused) {
        }
        checkMediaPaths();
    }

    /* renamed from: org.telegram.messenger.ImageLoader$5 */
    /* loaded from: classes.dex */
    public class AnonymousClass5 implements FileLoader.FileLoaderDelegate {
        final /* synthetic */ int val$currentAccount;

        AnonymousClass5(int i) {
            ImageLoader.this = r1;
            this.val$currentAccount = i;
        }

        @Override // org.telegram.messenger.FileLoader.FileLoaderDelegate
        public void fileUploadProgressChanged(FileUploadOperation fileUploadOperation, final String str, final long j, final long j2, final boolean z) {
            ImageLoader.this.fileProgresses.put(str, new long[]{j, j2});
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j3 = fileUploadOperation.lastProgressUpdateTime;
            if (j3 == 0 || j3 < elapsedRealtime - 100 || j == j2) {
                fileUploadOperation.lastProgressUpdateTime = elapsedRealtime;
                final int i = this.val$currentAccount;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$5$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ImageLoader.AnonymousClass5.lambda$fileUploadProgressChanged$0(i, str, j, j2, z);
                    }
                });
            }
        }

        public static /* synthetic */ void lambda$fileUploadProgressChanged$0(int i, String str, long j, long j2, boolean z) {
            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.fileUploadProgressChanged, str, Long.valueOf(j), Long.valueOf(j2), Boolean.valueOf(z));
        }

        @Override // org.telegram.messenger.FileLoader.FileLoaderDelegate
        public void fileDidUploaded(final String str, final TLRPC$InputFile tLRPC$InputFile, final TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, final byte[] bArr, final byte[] bArr2, final long j) {
            DispatchQueue dispatchQueue = Utilities.stageQueue;
            final int i = this.val$currentAccount;
            dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$5$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.AnonymousClass5.this.lambda$fileDidUploaded$2(i, str, tLRPC$InputFile, tLRPC$InputEncryptedFile, bArr, bArr2, j);
                }
            });
        }

        public static /* synthetic */ void lambda$fileDidUploaded$1(int i, String str, TLRPC$InputFile tLRPC$InputFile, TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, byte[] bArr, byte[] bArr2, long j) {
            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.fileUploaded, str, tLRPC$InputFile, tLRPC$InputEncryptedFile, bArr, bArr2, Long.valueOf(j));
        }

        public /* synthetic */ void lambda$fileDidUploaded$2(final int i, final String str, final TLRPC$InputFile tLRPC$InputFile, final TLRPC$InputEncryptedFile tLRPC$InputEncryptedFile, final byte[] bArr, final byte[] bArr2, final long j) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$5$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.AnonymousClass5.lambda$fileDidUploaded$1(i, str, tLRPC$InputFile, tLRPC$InputEncryptedFile, bArr, bArr2, j);
                }
            });
            ImageLoader.this.fileProgresses.remove(str);
        }

        @Override // org.telegram.messenger.FileLoader.FileLoaderDelegate
        public void fileDidFailedUpload(final String str, final boolean z) {
            DispatchQueue dispatchQueue = Utilities.stageQueue;
            final int i = this.val$currentAccount;
            dispatchQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$5$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.AnonymousClass5.this.lambda$fileDidFailedUpload$4(i, str, z);
                }
            });
        }

        public static /* synthetic */ void lambda$fileDidFailedUpload$3(int i, String str, boolean z) {
            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.fileUploadFailed, str, Boolean.valueOf(z));
        }

        public /* synthetic */ void lambda$fileDidFailedUpload$4(final int i, final String str, final boolean z) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$5$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.AnonymousClass5.lambda$fileDidFailedUpload$3(i, str, z);
                }
            });
            ImageLoader.this.fileProgresses.remove(str);
        }

        @Override // org.telegram.messenger.FileLoader.FileLoaderDelegate
        public void fileDidLoaded(final String str, final File file, final Object obj, final int i) {
            ImageLoader.this.fileProgresses.remove(str);
            final int i2 = this.val$currentAccount;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$5$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.AnonymousClass5.this.lambda$fileDidLoaded$5(file, str, obj, i2, i);
                }
            });
        }

        public /* synthetic */ void lambda$fileDidLoaded$5(File file, String str, Object obj, int i, int i2) {
            int i3;
            if (SharedConfig.saveToGalleryFlags != 0 && file != null && ((str.endsWith(".mp4") || str.endsWith(".jpg")) && (obj instanceof MessageObject))) {
                long dialogId = ((MessageObject) obj).getDialogId();
                if (dialogId >= 0) {
                    i3 = 1;
                } else {
                    i3 = ChatObject.isChannelAndNotMegaGroup(MessagesController.getInstance(i).getChat(Long.valueOf(-dialogId))) ? 4 : 2;
                }
                if ((i3 & SharedConfig.saveToGalleryFlags) != 0) {
                    AndroidUtilities.addMediaToGallery(file.toString());
                }
            }
            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.fileLoaded, str, file);
            ImageLoader.this.fileDidLoaded(str, file, i2);
        }

        @Override // org.telegram.messenger.FileLoader.FileLoaderDelegate
        public void fileDidFailedLoad(final String str, final int i) {
            ImageLoader.this.fileProgresses.remove(str);
            final int i2 = this.val$currentAccount;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$5$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.AnonymousClass5.this.lambda$fileDidFailedLoad$6(str, i, i2);
                }
            });
        }

        public /* synthetic */ void lambda$fileDidFailedLoad$6(String str, int i, int i2) {
            ImageLoader.this.fileDidFailedLoad(str, i);
            NotificationCenter.getInstance(i2).postNotificationName(NotificationCenter.fileLoadFailed, str, Integer.valueOf(i));
        }

        @Override // org.telegram.messenger.FileLoader.FileLoaderDelegate
        public void fileLoadProgressChanged(FileLoadOperation fileLoadOperation, final String str, final long j, final long j2) {
            ImageLoader.this.fileProgresses.put(str, new long[]{j, j2});
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j3 = fileLoadOperation.lastProgressUpdateTime;
            if (j3 == 0 || j3 < elapsedRealtime - 500 || j == 0) {
                fileLoadOperation.lastProgressUpdateTime = elapsedRealtime;
                final int i = this.val$currentAccount;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$5$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ImageLoader.AnonymousClass5.lambda$fileLoadProgressChanged$7(i, str, j, j2);
                    }
                });
            }
        }

        public static /* synthetic */ void lambda$fileLoadProgressChanged$7(int i, String str, long j, long j2) {
            NotificationCenter.getInstance(i).postNotificationName(NotificationCenter.fileLoadProgressChanged, str, Long.valueOf(j), Long.valueOf(j2));
        }
    }

    /* renamed from: org.telegram.messenger.ImageLoader$6 */
    /* loaded from: classes.dex */
    public class AnonymousClass6 extends BroadcastReceiver {
        AnonymousClass6() {
            ImageLoader.this = r1;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("file system changed");
            }
            Runnable runnable = new Runnable() { // from class: org.telegram.messenger.ImageLoader$6$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.AnonymousClass6.this.lambda$onReceive$0();
                }
            };
            if ("android.intent.action.MEDIA_UNMOUNTED".equals(intent.getAction())) {
                AndroidUtilities.runOnUIThread(runnable, 1000L);
            } else {
                runnable.run();
            }
        }

        public /* synthetic */ void lambda$onReceive$0() {
            ImageLoader.this.checkMediaPaths();
        }
    }

    public void checkMediaPaths() {
        this.cacheOutQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                ImageLoader.this.lambda$checkMediaPaths$1();
            }
        });
    }

    public /* synthetic */ void lambda$checkMediaPaths$1() {
        final SparseArray<File> createMediaPaths = createMediaPaths();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FileLoader.setMediaDirs(createMediaPaths);
            }
        });
    }

    public void addTestWebFile(String str, WebFile webFile) {
        if (str == null || webFile == null) {
            return;
        }
        this.testWebFile.put(str, webFile);
    }

    public void removeTestWebFile(String str) {
        if (str == null) {
            return;
        }
        this.testWebFile.remove(str);
    }

    @TargetApi(26)
    private static void moveDirectory(File file, final File file2) {
        if (file.exists()) {
            if (!file2.exists() && !file2.mkdir()) {
                return;
            }
            try {
                Stream convert = C$r8$wrapper$java$util$stream$Stream$VWRP.convert(Files.list(file.toPath()));
                convert.forEach(new Consumer() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda13
                    @Override // j$.util.function.Consumer
                    public final void accept(Object obj) {
                        ImageLoader.lambda$moveDirectory$2(file2, (Path) obj);
                    }

                    @Override // j$.util.function.Consumer
                    public /* synthetic */ Consumer andThen(Consumer consumer) {
                        return consumer.getClass();
                    }
                });
                convert.close();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static /* synthetic */ void lambda$moveDirectory$2(File file, Path path) {
        File file2 = new File(file, path.getFileName().toString());
        if (Files.isDirectory(path, new LinkOption[0])) {
            moveDirectory(path.toFile(), file2);
            return;
        }
        try {
            Files.move(path, file2.toPath(), new CopyOption[0]);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:108:0x0264 A[Catch: Exception -> 0x0277, TRY_LEAVE, TryCatch #7 {Exception -> 0x0277, blocks: (B:102:0x0247, B:104:0x0255, B:106:0x025b, B:108:0x0264), top: B:141:0x0247, outer: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:117:0x0298 A[Catch: Exception -> 0x02ab, TRY_LEAVE, TryCatch #2 {Exception -> 0x02ab, blocks: (B:111:0x027b, B:113:0x0289, B:115:0x028f, B:117:0x0298), top: B:131:0x027b, outer: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:145:0x0114 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:153:0x0103 A[EDGE_INSN: B:153:0x0103->B:51:0x0103 ?: BREAK  , SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00e3 A[Catch: Exception -> 0x02bd, TryCatch #0 {Exception -> 0x02bd, blocks: (B:10:0x003d, B:12:0x0049, B:14:0x0054, B:16:0x005c, B:18:0x0062, B:20:0x0069, B:23:0x007d, B:24:0x0080, B:38:0x00ae, B:39:0x00b1, B:40:0x00c0, B:41:0x00c7, B:43:0x00d0, B:45:0x00d8, B:47:0x00e3, B:49:0x00f5, B:50:0x0100, B:51:0x0103, B:62:0x0145, B:71:0x0179, B:80:0x01ba, B:89:0x01fb, B:98:0x023c, B:100:0x0241, B:110:0x0278, B:119:0x02ac, B:120:0x02b0, B:122:0x02b4, B:123:0x02b9, B:111:0x027b, B:113:0x0289, B:115:0x028f, B:117:0x0298, B:90:0x01fe, B:92:0x0210, B:94:0x0217, B:96:0x0226, B:81:0x01bd, B:83:0x01cf, B:85:0x01d6, B:87:0x01e5, B:72:0x017c, B:74:0x018e, B:76:0x0195, B:78:0x01a4, B:63:0x0148, B:65:0x0158, B:67:0x015e, B:69:0x0165, B:102:0x0247, B:104:0x0255, B:106:0x025b, B:108:0x0264, B:54:0x0114, B:56:0x0124, B:58:0x012a, B:60:0x0131), top: B:127:0x003d, inners: #2, #3, #4, #5, #6, #7, #9 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.util.SparseArray<java.io.File> createMediaPaths() {
        /*
            Method dump skipped, instructions count: 706
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.createMediaPaths():android.util.SparseArray");
    }

    private boolean canMoveFiles(File file, File file2, int i) {
        Throwable th;
        Exception e;
        File file3;
        File file4;
        byte[] bArr;
        RandomAccessFile randomAccessFile;
        RandomAccessFile randomAccessFile2 = null;
        try {
            try {
                if (i == 0) {
                    file3 = new File(file, "000000000_999999_temp.f");
                    file4 = new File(file2, "000000000_999999.f");
                } else {
                    if (i != 3 && i != 5) {
                        if (i == 1) {
                            file3 = new File(file, "000000000_999999_temp.f");
                            file4 = new File(file2, "000000000_999999.f");
                        } else if (i == 2) {
                            file3 = new File(file, "000000000_999999_temp.f");
                            file4 = new File(file2, "000000000_999999.f");
                        } else {
                            file4 = null;
                            file3 = null;
                        }
                    }
                    file3 = new File(file, "000000000_999999_temp.f");
                    file4 = new File(file2, "000000000_999999.f");
                }
                bArr = new byte[1024];
                file3.createNewFile();
                randomAccessFile = new RandomAccessFile(file3, "rws");
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
        try {
            randomAccessFile.write(bArr);
            randomAccessFile.close();
            boolean renameTo = file3.renameTo(file4);
            file3.delete();
            file4.delete();
            return renameTo;
        } catch (Exception e3) {
            e = e3;
            randomAccessFile2 = randomAccessFile;
            FileLog.e(e);
            if (randomAccessFile2 == null) {
                return false;
            }
            try {
                randomAccessFile2.close();
                return false;
            } catch (Exception e4) {
                FileLog.e(e4);
                return false;
            }
        } catch (Throwable th3) {
            th = th3;
            randomAccessFile2 = randomAccessFile;
            if (randomAccessFile2 != null) {
                try {
                    randomAccessFile2.close();
                } catch (Exception e5) {
                    FileLog.e(e5);
                }
            }
            throw th;
        }
    }

    public Float getFileProgress(String str) {
        long[] jArr;
        if (str == null || (jArr = this.fileProgresses.get(str)) == null) {
            return null;
        }
        if (jArr[1] == 0) {
            return Float.valueOf(0.0f);
        }
        return Float.valueOf(Math.min(1.0f, ((float) jArr[0]) / ((float) jArr[1])));
    }

    public long[] getFileProgressSizes(String str) {
        if (str == null) {
            return null;
        }
        return this.fileProgresses.get(str);
    }

    public String getReplacedKey(String str) {
        if (str == null) {
            return null;
        }
        return this.replacedBitmaps.get(str);
    }

    private void performReplace(String str, String str2) {
        LruCache<BitmapDrawable> lruCache = this.memCache;
        BitmapDrawable bitmapDrawable = lruCache.get(str);
        if (bitmapDrawable == null) {
            lruCache = this.smallImagesMemCache;
            bitmapDrawable = lruCache.get(str);
        }
        this.replacedBitmaps.put(str, str2);
        if (bitmapDrawable != null) {
            BitmapDrawable bitmapDrawable2 = lruCache.get(str2);
            boolean z = false;
            if (bitmapDrawable2 != null && bitmapDrawable2.getBitmap() != null && bitmapDrawable.getBitmap() != null) {
                Bitmap bitmap = bitmapDrawable2.getBitmap();
                Bitmap bitmap2 = bitmapDrawable.getBitmap();
                if (bitmap.getWidth() > bitmap2.getWidth() || bitmap.getHeight() > bitmap2.getHeight()) {
                    z = true;
                }
            }
            if (!z) {
                this.ignoreRemoval = str;
                lruCache.remove(str);
                lruCache.put(str2, bitmapDrawable);
                this.ignoreRemoval = null;
            } else {
                lruCache.remove(str);
            }
        }
        Integer num = this.bitmapUseCounts.get(str);
        if (num != null) {
            this.bitmapUseCounts.put(str2, num);
            this.bitmapUseCounts.remove(str);
        }
    }

    public void incrementUseCount(String str) {
        Integer num = this.bitmapUseCounts.get(str);
        if (num == null) {
            this.bitmapUseCounts.put(str, 1);
        } else {
            this.bitmapUseCounts.put(str, Integer.valueOf(num.intValue() + 1));
        }
    }

    public boolean decrementUseCount(String str) {
        Integer num = this.bitmapUseCounts.get(str);
        if (num == null) {
            return true;
        }
        if (num.intValue() == 1) {
            this.bitmapUseCounts.remove(str);
            return true;
        }
        this.bitmapUseCounts.put(str, Integer.valueOf(num.intValue() - 1));
        return false;
    }

    public void removeImage(String str) {
        this.bitmapUseCounts.remove(str);
        this.memCache.remove(str);
        this.smallImagesMemCache.remove(str);
    }

    public boolean isInMemCache(String str, boolean z) {
        return z ? getFromLottieCache(str) != null : getFromMemCache(str) != null;
    }

    public void clearMemory() {
        this.smallImagesMemCache.evictAll();
        this.memCache.evictAll();
        this.lottieMemCache.evictAll();
    }

    private void removeFromWaitingForThumb(int i, ImageReceiver imageReceiver) {
        String str = this.waitingForQualityThumbByTag.get(i);
        if (str != null) {
            ThumbGenerateInfo thumbGenerateInfo = this.waitingForQualityThumb.get(str);
            if (thumbGenerateInfo != null) {
                int indexOf = thumbGenerateInfo.imageReceiverArray.indexOf(imageReceiver);
                if (indexOf >= 0) {
                    thumbGenerateInfo.imageReceiverArray.remove(indexOf);
                    thumbGenerateInfo.imageReceiverGuidsArray.remove(indexOf);
                }
                if (thumbGenerateInfo.imageReceiverArray.isEmpty()) {
                    this.waitingForQualityThumb.remove(str);
                }
            }
            this.waitingForQualityThumbByTag.remove(i);
        }
    }

    public void cancelLoadingForImageReceiver(final ImageReceiver imageReceiver, final boolean z) {
        if (imageReceiver == null) {
            return;
        }
        ArrayList<Runnable> loadingOperations = imageReceiver.getLoadingOperations();
        if (!loadingOperations.isEmpty()) {
            for (int i = 0; i < loadingOperations.size(); i++) {
                this.imageLoadQueue.cancelRunnable(loadingOperations.get(i));
            }
            loadingOperations.clear();
        }
        imageReceiver.addLoadingImageRunnable(null);
        this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                ImageLoader.this.lambda$cancelLoadingForImageReceiver$3(z, imageReceiver);
            }
        });
    }

    public /* synthetic */ void lambda$cancelLoadingForImageReceiver$3(boolean z, ImageReceiver imageReceiver) {
        int i = 0;
        while (true) {
            int i2 = 3;
            if (i < 3) {
                if (i > 0 && !z) {
                    return;
                }
                if (i == 0) {
                    i2 = 1;
                } else if (i == 1) {
                    i2 = 0;
                }
                int tag = imageReceiver.getTag(i2);
                if (tag != 0) {
                    if (i == 0) {
                        removeFromWaitingForThumb(tag, imageReceiver);
                    }
                    CacheImage cacheImage = this.imageLoadingByTag.get(tag);
                    if (cacheImage != null) {
                        cacheImage.removeImageReceiver(imageReceiver);
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    public BitmapDrawable getImageFromMemory(TLObject tLObject, String str, String str2) {
        String str3 = null;
        if (tLObject == null && str == null) {
            return null;
        }
        if (str != null) {
            str3 = Utilities.MD5(str);
        } else if (tLObject instanceof TLRPC$FileLocation) {
            TLRPC$FileLocation tLRPC$FileLocation = (TLRPC$FileLocation) tLObject;
            str3 = tLRPC$FileLocation.volume_id + "_" + tLRPC$FileLocation.local_id;
        } else if (tLObject instanceof TLRPC$Document) {
            TLRPC$Document tLRPC$Document = (TLRPC$Document) tLObject;
            str3 = tLRPC$Document.dc_id + "_" + tLRPC$Document.id;
        } else if (tLObject instanceof SecureDocument) {
            SecureDocument secureDocument = (SecureDocument) tLObject;
            str3 = secureDocument.secureFile.dc_id + "_" + secureDocument.secureFile.id;
        } else if (tLObject instanceof WebFile) {
            str3 = Utilities.MD5(((WebFile) tLObject).url);
        }
        if (str2 != null) {
            str3 = str3 + "@" + str2;
        }
        return getFromMemCache(str3);
    }

    /* renamed from: replaceImageInCacheInternal */
    public void lambda$replaceImageInCache$4(String str, String str2, ImageLocation imageLocation) {
        ArrayList<String> arrayList;
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                arrayList = this.memCache.getFilterKeys(str);
            } else {
                arrayList = this.smallImagesMemCache.getFilterKeys(str);
            }
            if (arrayList != null) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    String str3 = arrayList.get(i2);
                    String str4 = str + "@" + str3;
                    String str5 = str2 + "@" + str3;
                    performReplace(str4, str5);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, str4, str5, imageLocation);
                }
            } else {
                performReplace(str, str2);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, str, str2, imageLocation);
            }
        }
    }

    public void replaceImageInCache(final String str, final String str2, final ImageLocation imageLocation, boolean z) {
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    ImageLoader.this.lambda$replaceImageInCache$4(str, str2, imageLocation);
                }
            });
        } else {
            lambda$replaceImageInCache$4(str, str2, imageLocation);
        }
    }

    public void putImageToCache(BitmapDrawable bitmapDrawable, String str, boolean z) {
        if (z) {
            this.smallImagesMemCache.put(str, bitmapDrawable);
        } else {
            this.memCache.put(str, bitmapDrawable);
        }
    }

    private void generateThumb(int i, File file, ThumbGenerateInfo thumbGenerateInfo) {
        if ((i != 0 && i != 2 && i != 3) || file == null || thumbGenerateInfo == null) {
            return;
        }
        if (this.thumbGenerateTasks.get(FileLoader.getAttachFileName(thumbGenerateInfo.parentDocument)) != null) {
            return;
        }
        this.thumbGeneratingQueue.postRunnable(new ThumbGenerateTask(i, file, thumbGenerateInfo));
    }

    public void cancelForceLoadingForImageReceiver(ImageReceiver imageReceiver) {
        final String imageKey;
        if (imageReceiver == null || (imageKey = imageReceiver.getImageKey()) == null) {
            return;
        }
        this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                ImageLoader.this.lambda$cancelForceLoadingForImageReceiver$5(imageKey);
            }
        });
    }

    public /* synthetic */ void lambda$cancelForceLoadingForImageReceiver$5(String str) {
        this.forceLoadingImages.remove(str);
    }

    private void createLoadOperationForImageReceiver(final ImageReceiver imageReceiver, final String str, final String str2, final String str3, final ImageLocation imageLocation, final String str4, final long j, final int i, final int i2, final int i3, final int i4) {
        if (imageReceiver == null || str2 == null || str == null || imageLocation == null) {
            return;
        }
        int tag = imageReceiver.getTag(i2);
        if (tag == 0) {
            tag = this.lastImageNum;
            imageReceiver.setTag(tag, i2);
            int i5 = this.lastImageNum + 1;
            this.lastImageNum = i5;
            if (i5 == Integer.MAX_VALUE) {
                this.lastImageNum = 0;
            }
        }
        final int i6 = tag;
        final boolean isNeedsQualityThumb = imageReceiver.isNeedsQualityThumb();
        final Object parentObject = imageReceiver.getParentObject();
        final TLRPC$Document qualityThumbDocument = imageReceiver.getQualityThumbDocument();
        final boolean isShouldGenerateQualityThumb = imageReceiver.isShouldGenerateQualityThumb();
        final int currentAccount = imageReceiver.getCurrentAccount();
        final boolean z = i2 == 0 && imageReceiver.isCurrentKeyQuality();
        Runnable runnable = new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ImageLoader.this.lambda$createLoadOperationForImageReceiver$6(i3, str2, str, i6, imageReceiver, i4, str4, i2, imageLocation, z, parentObject, currentAccount, qualityThumbDocument, isNeedsQualityThumb, isShouldGenerateQualityThumb, str3, i, j);
            }
        };
        this.imageLoadQueue.postRunnable(runnable);
        imageReceiver.addLoadingImageRunnable(runnable);
    }

    /* JADX WARN: Code restructure failed: missing block: B:232:0x0491, code lost:
        if (r10.exists() == false) goto L234;
     */
    /* JADX WARN: Code restructure failed: missing block: B:70:0x01a9, code lost:
        if (r9.exists() == false) goto L71;
     */
    /* JADX WARN: Removed duplicated region for block: B:126:0x02a5  */
    /* JADX WARN: Removed duplicated region for block: B:240:0x04d1  */
    /* JADX WARN: Removed duplicated region for block: B:245:0x04e5  */
    /* JADX WARN: Removed duplicated region for block: B:248:0x050e  */
    /* JADX WARN: Removed duplicated region for block: B:251:0x0513  */
    /* JADX WARN: Removed duplicated region for block: B:254:0x0549 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:304:0x0637  */
    /* JADX WARN: Removed duplicated region for block: B:305:0x063f  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x019e  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x01ae  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x01b0  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x01b3  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0204  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public /* synthetic */ void lambda$createLoadOperationForImageReceiver$6(int r24, java.lang.String r25, java.lang.String r26, int r27, org.telegram.messenger.ImageReceiver r28, int r29, java.lang.String r30, int r31, org.telegram.messenger.ImageLocation r32, boolean r33, java.lang.Object r34, int r35, org.telegram.tgnet.TLRPC$Document r36, boolean r37, boolean r38, java.lang.String r39, int r40, long r41) {
        /*
            Method dump skipped, instructions count: 1607
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.lambda$createLoadOperationForImageReceiver$6(int, java.lang.String, java.lang.String, int, org.telegram.messenger.ImageReceiver, int, java.lang.String, int, org.telegram.messenger.ImageLocation, boolean, java.lang.Object, int, org.telegram.tgnet.TLRPC$Document, boolean, boolean, java.lang.String, int, long):void");
    }

    public void preloadArtwork(final String str) {
        this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                ImageLoader.this.lambda$preloadArtwork$7(str);
            }
        });
    }

    public /* synthetic */ void lambda$preloadArtwork$7(String str) {
        String httpUrlExtension = getHttpUrlExtension(str, "jpg");
        String str2 = Utilities.MD5(str) + "." + httpUrlExtension;
        File file = new File(FileLoader.getDirectory(4), str2);
        if (file.exists()) {
            return;
        }
        ImageLocation forPath = ImageLocation.getForPath(str);
        CacheImage cacheImage = new CacheImage();
        cacheImage.type = 1;
        cacheImage.key = Utilities.MD5(str);
        cacheImage.filter = null;
        cacheImage.imageLocation = forPath;
        cacheImage.ext = httpUrlExtension;
        cacheImage.parentObject = null;
        int i = forPath.imageType;
        if (i != 0) {
            cacheImage.imageType = i;
        }
        cacheImage.url = str2;
        this.imageLoadingByUrl.put(str2, cacheImage);
        String MD5 = Utilities.MD5(forPath.path);
        cacheImage.tempFilePath = new File(FileLoader.getDirectory(4), MD5 + "_temp.jpg");
        cacheImage.finalFilePath = file;
        ArtworkLoadTask artworkLoadTask = new ArtworkLoadTask(cacheImage);
        cacheImage.artworkTask = artworkLoadTask;
        this.artworkTasks.add(artworkLoadTask);
        runArtworkTasks(false);
    }

    /* JADX WARN: Code restructure failed: missing block: B:146:0x0258, code lost:
        if (r6.local_id < 0) goto L148;
     */
    /* JADX WARN: Removed duplicated region for block: B:104:0x019a  */
    /* JADX WARN: Removed duplicated region for block: B:106:0x019d  */
    /* JADX WARN: Removed duplicated region for block: B:107:0x01a0  */
    /* JADX WARN: Removed duplicated region for block: B:109:0x01a4  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x01b9  */
    /* JADX WARN: Removed duplicated region for block: B:195:0x0377  */
    /* JADX WARN: Removed duplicated region for block: B:212:0x03e2  */
    /* JADX WARN: Removed duplicated region for block: B:215:0x03ea A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:219:0x0407 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:223:0x0422 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:228:0x0443  */
    /* JADX WARN: Removed duplicated region for block: B:229:0x0458  */
    /* JADX WARN: Removed duplicated region for block: B:231:0x045b  */
    /* JADX WARN: Removed duplicated region for block: B:239:0x0499  */
    /* JADX WARN: Removed duplicated region for block: B:241:0x049e  */
    /* JADX WARN: Removed duplicated region for block: B:255:0x0506  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x007f  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x0084  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0087  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x00ba  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x00d9  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x0156  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0166  */
    /* JADX WARN: Removed duplicated region for block: B:89:0x0173  */
    /* JADX WARN: Removed duplicated region for block: B:94:0x0181  */
    /* JADX WARN: Removed duplicated region for block: B:99:0x018b  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void loadImageForImageReceiver(org.telegram.messenger.ImageReceiver r37) {
        /*
            Method dump skipped, instructions count: 1348
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadImageForImageReceiver(org.telegram.messenger.ImageReceiver):void");
    }

    public BitmapDrawable getFromLottieCache(String str) {
        BitmapDrawable bitmapDrawable = this.lottieMemCache.get(str);
        if (!(bitmapDrawable instanceof AnimatedFileDrawable) || !((AnimatedFileDrawable) bitmapDrawable).isRecycled()) {
            return bitmapDrawable;
        }
        this.lottieMemCache.remove(str);
        return null;
    }

    private boolean useLottieMemCache(ImageLocation imageLocation, String str) {
        return (imageLocation != null && (MessageObject.isAnimatedStickerDocument(imageLocation.document, true) || imageLocation.imageType == 1 || MessageObject.isVideoSticker(imageLocation.document))) || isAnimatedAvatar(str);
    }

    public void httpFileLoadError(final String str) {
        this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ImageLoader.this.lambda$httpFileLoadError$8(str);
            }
        });
    }

    public /* synthetic */ void lambda$httpFileLoadError$8(String str) {
        CacheImage cacheImage = this.imageLoadingByUrl.get(str);
        if (cacheImage == null) {
            return;
        }
        HttpImageTask httpImageTask = cacheImage.httpTask;
        if (httpImageTask != null) {
            HttpImageTask httpImageTask2 = new HttpImageTask(httpImageTask.cacheImage, httpImageTask.imageSize);
            cacheImage.httpTask = httpImageTask2;
            this.httpTasks.add(httpImageTask2);
        }
        runHttpTasks(false);
    }

    public void artworkLoadError(final String str) {
        this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                ImageLoader.this.lambda$artworkLoadError$9(str);
            }
        });
    }

    public /* synthetic */ void lambda$artworkLoadError$9(String str) {
        CacheImage cacheImage = this.imageLoadingByUrl.get(str);
        if (cacheImage == null) {
            return;
        }
        ArtworkLoadTask artworkLoadTask = cacheImage.artworkTask;
        if (artworkLoadTask != null) {
            ArtworkLoadTask artworkLoadTask2 = new ArtworkLoadTask(artworkLoadTask.cacheImage);
            cacheImage.artworkTask = artworkLoadTask2;
            this.artworkTasks.add(artworkLoadTask2);
        }
        runArtworkTasks(false);
    }

    public void fileDidLoaded(final String str, final File file, final int i) {
        this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ImageLoader.this.lambda$fileDidLoaded$10(str, i, file);
            }
        });
    }

    public /* synthetic */ void lambda$fileDidLoaded$10(String str, int i, File file) {
        ThumbGenerateInfo thumbGenerateInfo = this.waitingForQualityThumb.get(str);
        if (thumbGenerateInfo != null && thumbGenerateInfo.parentDocument != null) {
            generateThumb(i, file, thumbGenerateInfo);
            this.waitingForQualityThumb.remove(str);
        }
        CacheImage cacheImage = this.imageLoadingByUrl.get(str);
        if (cacheImage == null) {
            return;
        }
        this.imageLoadingByUrl.remove(str);
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < cacheImage.imageReceiverArray.size(); i2++) {
            String str2 = cacheImage.keys.get(i2);
            String str3 = cacheImage.filters.get(i2);
            int intValue = cacheImage.types.get(i2).intValue();
            ImageReceiver imageReceiver = cacheImage.imageReceiverArray.get(i2);
            int intValue2 = cacheImage.imageReceiverGuidsArray.get(i2).intValue();
            CacheImage cacheImage2 = this.imageLoadingByKeys.get(str2);
            if (cacheImage2 == null) {
                cacheImage2 = new CacheImage();
                cacheImage2.secureDocument = cacheImage.secureDocument;
                cacheImage2.currentAccount = cacheImage.currentAccount;
                cacheImage2.finalFilePath = file;
                cacheImage2.parentObject = cacheImage.parentObject;
                cacheImage2.key = str2;
                cacheImage2.imageLocation = cacheImage.imageLocation;
                cacheImage2.type = intValue;
                cacheImage2.ext = cacheImage.ext;
                cacheImage2.encryptionKeyPath = cacheImage.encryptionKeyPath;
                cacheImage2.cacheTask = new CacheOutTask(cacheImage2);
                cacheImage2.filter = str3;
                cacheImage2.imageType = cacheImage.imageType;
                this.imageLoadingByKeys.put(str2, cacheImage2);
                arrayList.add(cacheImage2.cacheTask);
            }
            cacheImage2.addImageReceiver(imageReceiver, str2, str3, intValue, intValue2);
        }
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            CacheOutTask cacheOutTask = (CacheOutTask) arrayList.get(i3);
            if (cacheOutTask.cacheImage.type == 1) {
                this.cacheThumbOutQueue.postRunnable(cacheOutTask);
            } else {
                this.cacheOutQueue.postRunnable(cacheOutTask);
            }
        }
    }

    public void fileDidFailedLoad(final String str, int i) {
        if (i == 1) {
            return;
        }
        this.imageLoadQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ImageLoader.this.lambda$fileDidFailedLoad$11(str);
            }
        });
    }

    public /* synthetic */ void lambda$fileDidFailedLoad$11(String str) {
        CacheImage cacheImage = this.imageLoadingByUrl.get(str);
        if (cacheImage != null) {
            cacheImage.setImageAndClear(null, null);
        }
    }

    public void runHttpTasks(boolean z) {
        if (z) {
            this.currentHttpTasksCount--;
        }
        while (this.currentHttpTasksCount < 4 && !this.httpTasks.isEmpty()) {
            HttpImageTask poll = this.httpTasks.poll();
            if (poll != null) {
                poll.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                this.currentHttpTasksCount++;
            }
        }
    }

    public void runArtworkTasks(boolean z) {
        if (z) {
            this.currentArtworkTasksCount--;
        }
        while (this.currentArtworkTasksCount < 4 && !this.artworkTasks.isEmpty()) {
            try {
                this.artworkTasks.poll().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
                this.currentArtworkTasksCount++;
            } catch (Throwable unused) {
                runArtworkTasks(false);
            }
        }
    }

    public boolean isLoadingHttpFile(String str) {
        return this.httpFileLoadTasksByKeys.containsKey(str);
    }

    public static String getHttpFileName(String str) {
        return Utilities.MD5(str);
    }

    public static File getHttpFilePath(String str, String str2) {
        String httpUrlExtension = getHttpUrlExtension(str, str2);
        File directory = FileLoader.getDirectory(4);
        return new File(directory, Utilities.MD5(str) + "." + httpUrlExtension);
    }

    public void loadHttpFile(String str, String str2, int i) {
        if (str == null || str.length() == 0 || this.httpFileLoadTasksByKeys.containsKey(str)) {
            return;
        }
        String httpUrlExtension = getHttpUrlExtension(str, str2);
        File directory = FileLoader.getDirectory(4);
        File file = new File(directory, Utilities.MD5(str) + "_temp." + httpUrlExtension);
        file.delete();
        HttpFileTask httpFileTask = new HttpFileTask(str, file, httpUrlExtension, i);
        this.httpFileLoadTasks.add(httpFileTask);
        this.httpFileLoadTasksByKeys.put(str, httpFileTask);
        runHttpFileLoadTasks(null, 0);
    }

    public void cancelLoadHttpFile(String str) {
        HttpFileTask httpFileTask = this.httpFileLoadTasksByKeys.get(str);
        if (httpFileTask != null) {
            httpFileTask.cancel(true);
            this.httpFileLoadTasksByKeys.remove(str);
            this.httpFileLoadTasks.remove(httpFileTask);
        }
        Runnable runnable = this.retryHttpsTasks.get(str);
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        runHttpFileLoadTasks(null, 0);
    }

    public void runHttpFileLoadTasks(final HttpFileTask httpFileTask, final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                ImageLoader.this.lambda$runHttpFileLoadTasks$13(httpFileTask, i);
            }
        });
    }

    public /* synthetic */ void lambda$runHttpFileLoadTasks$13(HttpFileTask httpFileTask, int i) {
        if (httpFileTask != null) {
            this.currentHttpFileLoadTasksCount--;
        }
        if (httpFileTask != null) {
            if (i == 1) {
                if (!httpFileTask.canRetry) {
                    this.httpFileLoadTasksByKeys.remove(httpFileTask.url);
                    NotificationCenter.getInstance(httpFileTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidFailedLoad, httpFileTask.url, 0);
                } else {
                    final HttpFileTask httpFileTask2 = new HttpFileTask(httpFileTask.url, httpFileTask.tempFile, httpFileTask.ext, httpFileTask.currentAccount);
                    Runnable runnable = new Runnable() { // from class: org.telegram.messenger.ImageLoader$$ExternalSyntheticLambda10
                        @Override // java.lang.Runnable
                        public final void run() {
                            ImageLoader.this.lambda$runHttpFileLoadTasks$12(httpFileTask2);
                        }
                    };
                    this.retryHttpsTasks.put(httpFileTask.url, runnable);
                    AndroidUtilities.runOnUIThread(runnable, 1000L);
                }
            } else if (i == 2) {
                this.httpFileLoadTasksByKeys.remove(httpFileTask.url);
                File file = new File(FileLoader.getDirectory(4), Utilities.MD5(httpFileTask.url) + "." + httpFileTask.ext);
                if (!httpFileTask.tempFile.renameTo(file)) {
                    file = httpFileTask.tempFile;
                }
                NotificationCenter.getInstance(httpFileTask.currentAccount).postNotificationName(NotificationCenter.httpFileDidLoad, httpFileTask.url, file.toString());
            }
        }
        while (this.currentHttpFileLoadTasksCount < 2 && !this.httpFileLoadTasks.isEmpty()) {
            this.httpFileLoadTasks.poll().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
            this.currentHttpFileLoadTasksCount++;
        }
    }

    public /* synthetic */ void lambda$runHttpFileLoadTasks$12(HttpFileTask httpFileTask) {
        this.httpFileLoadTasks.add(httpFileTask);
        runHttpFileLoadTasks(null, 0);
    }

    public static boolean shouldSendImageAsDocument(String str, Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (str == null && uri != null && uri.getScheme() != null) {
            if (uri.getScheme().contains("file")) {
                str = uri.getPath();
            } else {
                try {
                    str = AndroidUtilities.getPath(uri);
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        }
        if (str != null) {
            BitmapFactory.decodeFile(str, options);
        } else if (uri != null) {
            try {
                InputStream openInputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
                BitmapFactory.decodeStream(openInputStream, null, options);
                openInputStream.close();
            } catch (Throwable th2) {
                FileLog.e(th2);
                return false;
            }
        }
        float f = options.outWidth;
        float f2 = options.outHeight;
        return f / f2 > 10.0f || f2 / f > 10.0f;
    }

    /* JADX WARN: Can't wrap try/catch for region: R(23:2|(2:7|(1:9)(2:10|(2:145|14)))|17|(1:19)(1:(19:130|21|26|(1:28)(1:29)|30|(1:32)|33|(3:35|(2:36|(1:38)(1:152))|39)|40|(1:42)(1:43)|44|(2:149|46)(1:(4:139|48|49|(1:51)))|52|128|(2:54|(6:56|(3:59|60|61)|58|69|(2:(1:72)|73)|(3:147|75|(4:77|(1:79)|80|(3:82|83|155)(1:154))(1:153))(2:133|(7:135|101|(5:150|103|(1:105)|106|(5:108|109|113|132|124))|112|113|132|124)(1:160)))(3:62|63|64))(3:65|66|67)|127|69|(0)|(0)(0)))|25|26|(0)(0)|30|(0)|33|(0)|40|(0)(0)|44|(0)(0)|52|128|(0)(0)|127|69|(0)|(0)(0)|(1:(0))) */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:133:0x0177 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:147:0x0116 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:149:0x00a7 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:28:0x0072  */
    /* JADX WARN: Removed duplicated region for block: B:29:0x0077  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x008d  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x009e  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00a0  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00b1  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x00d6  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x00f5 A[Catch: all -> 0x00dd, TRY_ENTER, TRY_LEAVE, TryCatch #12 {all -> 0x00dd, blocks: (B:46:0x00a7, B:59:0x00df, B:62:0x00ea, B:65:0x00f5), top: B:149:0x00a7 }] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0109  */
    /* JADX WARN: Type inference failed for: r15v10 */
    /* JADX WARN: Type inference failed for: r15v11 */
    /* JADX WARN: Type inference failed for: r15v18 */
    /* JADX WARN: Type inference failed for: r15v2 */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:115:0x01a7 -> B:132:0x01bd). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.graphics.Bitmap loadBitmap(java.lang.String r11, android.net.Uri r12, float r13, float r14, boolean r15) {
        /*
            Method dump skipped, instructions count: 446
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.loadBitmap(java.lang.String, android.net.Uri, float, float, boolean):android.graphics.Bitmap");
    }

    public static void fillPhotoSizeWithBytes(TLRPC$PhotoSize tLRPC$PhotoSize) {
        if (tLRPC$PhotoSize != null) {
            byte[] bArr = tLRPC$PhotoSize.bytes;
            if (bArr != null && bArr.length != 0) {
                return;
            }
            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(tLRPC$PhotoSize, true), "r");
                if (((int) randomAccessFile.length()) >= 20000) {
                    return;
                }
                byte[] bArr2 = new byte[(int) randomAccessFile.length()];
                tLRPC$PhotoSize.bytes = bArr2;
                randomAccessFile.readFully(bArr2, 0, bArr2.length);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x009a  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x009f  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00bd  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x00cd  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00e3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static org.telegram.tgnet.TLRPC$PhotoSize scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC$PhotoSize r2, android.graphics.Bitmap r3, android.graphics.Bitmap.CompressFormat r4, boolean r5, int r6, int r7, float r8, float r9, float r10, int r11, boolean r12, boolean r13, boolean r14) throws java.lang.Exception {
        /*
            Method dump skipped, instructions count: 231
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageLoader.scaleAndSaveImageInternal(org.telegram.tgnet.TLRPC$PhotoSize, android.graphics.Bitmap, android.graphics.Bitmap$CompressFormat, boolean, int, int, float, float, float, int, boolean, boolean, boolean):org.telegram.tgnet.TLRPC$PhotoSize");
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z) {
        return scaleAndSaveImage(null, bitmap, Bitmap.CompressFormat.JPEG, false, f, f2, i, z, 0, 0, false);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(TLRPC$PhotoSize tLRPC$PhotoSize, Bitmap bitmap, float f, float f2, int i, boolean z, boolean z2) {
        return scaleAndSaveImage(tLRPC$PhotoSize, bitmap, Bitmap.CompressFormat.JPEG, false, f, f2, i, z, 0, 0, z2);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, int i, boolean z, int i2, int i3) {
        return scaleAndSaveImage(null, bitmap, Bitmap.CompressFormat.JPEG, false, f, f2, i, z, i2, i3, false);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(Bitmap bitmap, float f, float f2, boolean z, int i, boolean z2, int i2, int i3) {
        return scaleAndSaveImage(null, bitmap, Bitmap.CompressFormat.JPEG, z, f, f2, i, z2, i2, i3, false);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(Bitmap bitmap, Bitmap.CompressFormat compressFormat, float f, float f2, int i, boolean z, int i2, int i3) {
        return scaleAndSaveImage(null, bitmap, compressFormat, false, f, f2, i, z, i2, i3, false);
    }

    public static TLRPC$PhotoSize scaleAndSaveImage(TLRPC$PhotoSize tLRPC$PhotoSize, Bitmap bitmap, Bitmap.CompressFormat compressFormat, boolean z, float f, float f2, int i, boolean z2, int i2, int i3, boolean z3) {
        boolean z4;
        float f3;
        int i4;
        int i5;
        float f4;
        if (bitmap == null) {
            return null;
        }
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        if (width != 0.0f && height != 0.0f) {
            float max = Math.max(width / f, height / f2);
            if (i2 != 0 && i3 != 0) {
                float f5 = i2;
                if (width < f5 || height < i3) {
                    if (width >= f5 || height <= i3) {
                        if (width > f5) {
                            float f6 = i3;
                            if (height < f6) {
                                f4 = height / f6;
                            }
                        }
                        f4 = Math.max(width / f5, height / i3);
                    } else {
                        f4 = width / f5;
                    }
                    f3 = f4;
                    z4 = true;
                    i4 = (int) (width / f3);
                    i5 = (int) (height / f3);
                    if (i5 != 0 && i4 != 0) {
                        try {
                            return scaleAndSaveImageInternal(tLRPC$PhotoSize, bitmap, compressFormat, z, i4, i5, width, height, f3, i, z2, z4, z3);
                        } catch (Throwable th) {
                            FileLog.e(th);
                            getInstance().clearMemory();
                            System.gc();
                            try {
                                return scaleAndSaveImageInternal(tLRPC$PhotoSize, bitmap, compressFormat, z, i4, i5, width, height, f3, i, z2, z4, z3);
                            } catch (Throwable th2) {
                                FileLog.e(th2);
                            }
                        }
                    }
                }
            }
            f3 = max;
            z4 = false;
            i4 = (int) (width / f3);
            i5 = (int) (height / f3);
            if (i5 != 0) {
                return scaleAndSaveImageInternal(tLRPC$PhotoSize, bitmap, compressFormat, z, i4, i5, width, height, f3, i, z2, z4, z3);
            }
        }
        return null;
    }

    public static String getHttpUrlExtension(String str, String str2) {
        String lastPathSegment = Uri.parse(str).getLastPathSegment();
        if (!TextUtils.isEmpty(lastPathSegment) && lastPathSegment.length() > 1) {
            str = lastPathSegment;
        }
        int lastIndexOf = str.lastIndexOf(46);
        String substring = lastIndexOf != -1 ? str.substring(lastIndexOf + 1) : null;
        return (substring == null || substring.length() == 0 || substring.length() > 4) ? str2 : substring;
    }

    public static void saveMessageThumbs(TLRPC$Message tLRPC$Message) {
        TLRPC$PhotoSize findPhotoCachedSize;
        byte[] bArr;
        if (tLRPC$Message.media == null || (findPhotoCachedSize = findPhotoCachedSize(tLRPC$Message)) == null || (bArr = findPhotoCachedSize.bytes) == null || bArr.length == 0) {
            return;
        }
        TLRPC$FileLocation tLRPC$FileLocation = findPhotoCachedSize.location;
        if (tLRPC$FileLocation == null || (tLRPC$FileLocation instanceof TLRPC$TL_fileLocationUnavailable)) {
            TLRPC$TL_fileLocationToBeDeprecated tLRPC$TL_fileLocationToBeDeprecated = new TLRPC$TL_fileLocationToBeDeprecated();
            findPhotoCachedSize.location = tLRPC$TL_fileLocationToBeDeprecated;
            tLRPC$TL_fileLocationToBeDeprecated.volume_id = -2147483648L;
            tLRPC$TL_fileLocationToBeDeprecated.local_id = SharedConfig.getLastLocalId();
        }
        boolean z = true;
        File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(findPhotoCachedSize, true);
        int i = 0;
        if (MessageObject.shouldEncryptPhotoOrVideo(tLRPC$Message)) {
            pathToAttach = new File(pathToAttach.getAbsolutePath() + ".enc");
        } else {
            z = false;
        }
        if (!pathToAttach.exists()) {
            if (z) {
                try {
                    File internalCacheDir = FileLoader.getInternalCacheDir();
                    RandomAccessFile randomAccessFile = new RandomAccessFile(new File(internalCacheDir, pathToAttach.getName() + ".key"), "rws");
                    long length = randomAccessFile.length();
                    byte[] bArr2 = new byte[32];
                    byte[] bArr3 = new byte[16];
                    if (length > 0 && length % 48 == 0) {
                        randomAccessFile.read(bArr2, 0, 32);
                        randomAccessFile.read(bArr3, 0, 16);
                    } else {
                        Utilities.random.nextBytes(bArr2);
                        Utilities.random.nextBytes(bArr3);
                        randomAccessFile.write(bArr2);
                        randomAccessFile.write(bArr3);
                    }
                    randomAccessFile.close();
                    byte[] bArr4 = findPhotoCachedSize.bytes;
                    Utilities.aesCtrDecryptionByteArray(bArr4, bArr2, bArr3, 0, bArr4.length, 0);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            RandomAccessFile randomAccessFile2 = new RandomAccessFile(pathToAttach, "rws");
            randomAccessFile2.write(findPhotoCachedSize.bytes);
            randomAccessFile2.close();
        }
        TLRPC$TL_photoSize_layer127 tLRPC$TL_photoSize_layer127 = new TLRPC$TL_photoSize_layer127();
        tLRPC$TL_photoSize_layer127.w = findPhotoCachedSize.w;
        tLRPC$TL_photoSize_layer127.h = findPhotoCachedSize.h;
        tLRPC$TL_photoSize_layer127.location = findPhotoCachedSize.location;
        tLRPC$TL_photoSize_layer127.size = findPhotoCachedSize.size;
        tLRPC$TL_photoSize_layer127.type = findPhotoCachedSize.type;
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
            int size = tLRPC$MessageMedia.photo.sizes.size();
            while (i < size) {
                if (tLRPC$Message.media.photo.sizes.get(i) instanceof TLRPC$TL_photoCachedSize) {
                    tLRPC$Message.media.photo.sizes.set(i, tLRPC$TL_photoSize_layer127);
                    return;
                }
                i++;
            }
        } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
            int size2 = tLRPC$MessageMedia.document.thumbs.size();
            while (i < size2) {
                if (tLRPC$Message.media.document.thumbs.get(i) instanceof TLRPC$TL_photoCachedSize) {
                    tLRPC$Message.media.document.thumbs.set(i, tLRPC$TL_photoSize_layer127);
                    return;
                }
                i++;
            }
        } else if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage)) {
        } else {
            int size3 = tLRPC$MessageMedia.webpage.photo.sizes.size();
            while (i < size3) {
                if (tLRPC$Message.media.webpage.photo.sizes.get(i) instanceof TLRPC$TL_photoCachedSize) {
                    tLRPC$Message.media.webpage.photo.sizes.set(i, tLRPC$TL_photoSize_layer127);
                    return;
                }
                i++;
            }
        }
    }

    private static TLRPC$PhotoSize findPhotoCachedSize(TLRPC$Message tLRPC$Message) {
        TLRPC$PhotoSize tLRPC$PhotoSize;
        TLRPC$Photo tLRPC$Photo;
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        int i = 0;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
            int size = tLRPC$MessageMedia.photo.sizes.size();
            while (i < size) {
                tLRPC$PhotoSize = tLRPC$Message.media.photo.sizes.get(i);
                if (!(tLRPC$PhotoSize instanceof TLRPC$TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
            int size2 = tLRPC$MessageMedia.document.thumbs.size();
            while (i < size2) {
                tLRPC$PhotoSize = tLRPC$Message.media.document.thumbs.get(i);
                if (!(tLRPC$PhotoSize instanceof TLRPC$TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        } else if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) || (tLRPC$Photo = tLRPC$MessageMedia.webpage.photo) == null) {
            return null;
        } else {
            int size3 = tLRPC$Photo.sizes.size();
            while (i < size3) {
                tLRPC$PhotoSize = tLRPC$Message.media.webpage.photo.sizes.get(i);
                if (!(tLRPC$PhotoSize instanceof TLRPC$TL_photoCachedSize)) {
                    i++;
                }
            }
            return null;
        }
        return tLRPC$PhotoSize;
    }

    public static void saveMessagesThumbs(ArrayList<TLRPC$Message> arrayList) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            saveMessageThumbs(arrayList.get(i));
        }
    }

    public static MessageThumb generateMessageThumb(TLRPC$Message tLRPC$Message) {
        int i;
        int i2;
        Bitmap strippedPhotoBitmap;
        byte[] bArr;
        TLRPC$PhotoSize findPhotoCachedSize = findPhotoCachedSize(tLRPC$Message);
        if (findPhotoCachedSize != null && (bArr = findPhotoCachedSize.bytes) != null && bArr.length != 0) {
            File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(findPhotoCachedSize, true);
            TLRPC$TL_photoSize_layer127 tLRPC$TL_photoSize_layer127 = new TLRPC$TL_photoSize_layer127();
            tLRPC$TL_photoSize_layer127.w = findPhotoCachedSize.w;
            tLRPC$TL_photoSize_layer127.h = findPhotoCachedSize.h;
            tLRPC$TL_photoSize_layer127.location = findPhotoCachedSize.location;
            tLRPC$TL_photoSize_layer127.size = findPhotoCachedSize.size;
            tLRPC$TL_photoSize_layer127.type = findPhotoCachedSize.type;
            if (pathToAttach.exists() && tLRPC$Message.grouped_id == 0) {
                org.telegram.ui.Components.Point messageSize = ChatMessageCell.getMessageSize(findPhotoCachedSize.w, findPhotoCachedSize.h);
                String format = String.format(Locale.US, "%d_%d@%d_%d_b", Long.valueOf(findPhotoCachedSize.location.volume_id), Integer.valueOf(findPhotoCachedSize.location.local_id), Integer.valueOf((int) (messageSize.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize.y / AndroidUtilities.density)));
                if (!getInstance().isInMemCache(format, false)) {
                    String path = pathToAttach.getPath();
                    float f = messageSize.x;
                    float f2 = AndroidUtilities.density;
                    Bitmap loadBitmap = loadBitmap(path, null, (int) (f / f2), (int) (messageSize.y / f2), false);
                    if (loadBitmap != null) {
                        Utilities.blurBitmap(loadBitmap, 3, 1, loadBitmap.getWidth(), loadBitmap.getHeight(), loadBitmap.getRowBytes());
                        float f3 = messageSize.x;
                        float f4 = AndroidUtilities.density;
                        Bitmap createScaledBitmap = Bitmaps.createScaledBitmap(loadBitmap, (int) (f3 / f4), (int) (messageSize.y / f4), true);
                        if (createScaledBitmap != loadBitmap) {
                            loadBitmap.recycle();
                            loadBitmap = createScaledBitmap;
                        }
                        return new MessageThumb(format, new BitmapDrawable(loadBitmap));
                    }
                }
            }
        } else {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
                int size = tLRPC$MessageMedia.document.thumbs.size();
                for (int i3 = 0; i3 < size; i3++) {
                    TLRPC$PhotoSize tLRPC$PhotoSize = tLRPC$Message.media.document.thumbs.get(i3);
                    if (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) {
                        TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Message.media.document.thumbs, 320);
                        if (closestPhotoSizeWithSize == null) {
                            int i4 = 0;
                            while (true) {
                                if (i4 >= tLRPC$Message.media.document.attributes.size()) {
                                    i2 = 0;
                                    i = 0;
                                    break;
                                } else if (tLRPC$Message.media.document.attributes.get(i4) instanceof TLRPC$TL_documentAttributeVideo) {
                                    TLRPC$TL_documentAttributeVideo tLRPC$TL_documentAttributeVideo = (TLRPC$TL_documentAttributeVideo) tLRPC$Message.media.document.attributes.get(i4);
                                    i = tLRPC$TL_documentAttributeVideo.h;
                                    i2 = tLRPC$TL_documentAttributeVideo.w;
                                    break;
                                } else {
                                    i4++;
                                }
                            }
                        } else {
                            i = closestPhotoSizeWithSize.h;
                            i2 = closestPhotoSizeWithSize.w;
                        }
                        org.telegram.ui.Components.Point messageSize2 = ChatMessageCell.getMessageSize(i2, i);
                        String format2 = String.format(Locale.US, "%s_false@%d_%d_b", ImageLocation.getStrippedKey(tLRPC$Message, tLRPC$Message, tLRPC$PhotoSize), Integer.valueOf((int) (messageSize2.x / AndroidUtilities.density)), Integer.valueOf((int) (messageSize2.y / AndroidUtilities.density)));
                        if (!getInstance().isInMemCache(format2, false) && (strippedPhotoBitmap = getStrippedPhotoBitmap(tLRPC$PhotoSize.bytes, null)) != null) {
                            Utilities.blurBitmap(strippedPhotoBitmap, 3, 1, strippedPhotoBitmap.getWidth(), strippedPhotoBitmap.getHeight(), strippedPhotoBitmap.getRowBytes());
                            float f5 = messageSize2.x;
                            float f6 = AndroidUtilities.density;
                            Bitmap createScaledBitmap2 = Bitmaps.createScaledBitmap(strippedPhotoBitmap, (int) (f5 / f6), (int) (messageSize2.y / f6), true);
                            if (createScaledBitmap2 != strippedPhotoBitmap) {
                                strippedPhotoBitmap.recycle();
                                strippedPhotoBitmap = createScaledBitmap2;
                            }
                            return new MessageThumb(format2, new BitmapDrawable(strippedPhotoBitmap));
                        }
                    }
                }
            }
        }
        return null;
    }

    public void onFragmentStackChanged() {
        for (int i = 0; i < this.cachedAnimatedFileDrawables.size(); i++) {
            this.cachedAnimatedFileDrawables.get(i).repeatCount = 0;
        }
    }

    public DispatchQueue getCacheOutQueue() {
        return this.cacheOutQueue;
    }

    /* loaded from: classes.dex */
    public static class MessageThumb {
        BitmapDrawable drawable;
        String key;

        public MessageThumb(String str, BitmapDrawable bitmapDrawable) {
            this.key = str;
            this.drawable = bitmapDrawable;
        }
    }
}