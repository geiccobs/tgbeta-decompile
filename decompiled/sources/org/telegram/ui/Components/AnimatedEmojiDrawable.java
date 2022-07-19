package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_getCustomEmojiDocuments;
import org.telegram.tgnet.TLRPC$Vector;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
/* loaded from: classes3.dex */
public class AnimatedEmojiDrawable extends Drawable {
    static int count = 0;
    private static HashMap<Integer, EmojiDocumentFetcher> fetchers = null;
    private static HashMap<Integer, HashMap<Long, AnimatedEmojiDrawable>> globalEmojiCache = null;
    private static Paint placeholderPaint = null;
    public static int sizedp = 30;
    private float alpha = 1.0f;
    private boolean attached;
    private int cacheType;
    private TLRPC$Document document;
    private long documentId;
    private ArrayList<AnimatedEmojiSpan.AnimatedEmojiHolder> holders;
    private ImageReceiver imageReceiver;
    private ArrayList<View> views;

    /* loaded from: classes3.dex */
    public interface ReceivedDocument {
        void run(TLRPC$Document tLRPC$Document);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public static AnimatedEmojiDrawable make(int i, int i2, long j) {
        if (globalEmojiCache == null) {
            globalEmojiCache = new HashMap<>();
        }
        int hashCode = Arrays.hashCode(new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        HashMap<Long, AnimatedEmojiDrawable> hashMap = globalEmojiCache.get(Integer.valueOf(hashCode));
        if (hashMap == null) {
            HashMap<Integer, HashMap<Long, AnimatedEmojiDrawable>> hashMap2 = globalEmojiCache;
            Integer valueOf = Integer.valueOf(hashCode);
            HashMap<Long, AnimatedEmojiDrawable> hashMap3 = new HashMap<>();
            hashMap2.put(valueOf, hashMap3);
            hashMap = hashMap3;
        }
        AnimatedEmojiDrawable animatedEmojiDrawable = hashMap.get(Long.valueOf(j));
        if (animatedEmojiDrawable == null) {
            Long valueOf2 = Long.valueOf(j);
            AnimatedEmojiDrawable animatedEmojiDrawable2 = new AnimatedEmojiDrawable(i2, i, j);
            hashMap.put(valueOf2, animatedEmojiDrawable2);
            return animatedEmojiDrawable2;
        }
        return animatedEmojiDrawable;
    }

    public static AnimatedEmojiDrawable make(int i, int i2, TLRPC$Document tLRPC$Document) {
        if (globalEmojiCache == null) {
            globalEmojiCache = new HashMap<>();
        }
        int hashCode = Arrays.hashCode(new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
        HashMap<Long, AnimatedEmojiDrawable> hashMap = globalEmojiCache.get(Integer.valueOf(hashCode));
        if (hashMap == null) {
            HashMap<Integer, HashMap<Long, AnimatedEmojiDrawable>> hashMap2 = globalEmojiCache;
            Integer valueOf = Integer.valueOf(hashCode);
            HashMap<Long, AnimatedEmojiDrawable> hashMap3 = new HashMap<>();
            hashMap2.put(valueOf, hashMap3);
            hashMap = hashMap3;
        }
        AnimatedEmojiDrawable animatedEmojiDrawable = hashMap.get(Long.valueOf(tLRPC$Document.id));
        if (animatedEmojiDrawable == null) {
            Long valueOf2 = Long.valueOf(tLRPC$Document.id);
            AnimatedEmojiDrawable animatedEmojiDrawable2 = new AnimatedEmojiDrawable(i2, i, tLRPC$Document);
            hashMap.put(valueOf2, animatedEmojiDrawable2);
            return animatedEmojiDrawable2;
        }
        return animatedEmojiDrawable;
    }

    public void setTime(long j) {
        ImageReceiver imageReceiver = this.imageReceiver;
        if (imageReceiver != null) {
            imageReceiver.setCurrentTime(j);
        }
    }

    public void update(long j) {
        ImageReceiver imageReceiver = this.imageReceiver;
        if (imageReceiver != null) {
            if (imageReceiver.getLottieAnimation() != null) {
                this.imageReceiver.getLottieAnimation().updateCurrentFrame(j, true);
            }
            if (this.imageReceiver.getAnimation() == null) {
                return;
            }
            this.imageReceiver.getAnimation().updateCurrentFrame(j, true);
        }
    }

    public static EmojiDocumentFetcher getDocumentFetcher(int i) {
        if (fetchers == null) {
            fetchers = new HashMap<>();
        }
        EmojiDocumentFetcher emojiDocumentFetcher = fetchers.get(Integer.valueOf(i));
        if (emojiDocumentFetcher == null) {
            HashMap<Integer, EmojiDocumentFetcher> hashMap = fetchers;
            Integer valueOf = Integer.valueOf(i);
            EmojiDocumentFetcher emojiDocumentFetcher2 = new EmojiDocumentFetcher(i);
            hashMap.put(valueOf, emojiDocumentFetcher2);
            return emojiDocumentFetcher2;
        }
        return emojiDocumentFetcher;
    }

    /* loaded from: classes3.dex */
    public static class EmojiDocumentFetcher {
        private final int currentAccount;
        private HashMap<Long, TLRPC$Document> emojiDocumentsCache;
        private Runnable fetchRunnable;
        private HashMap<Long, ArrayList<ReceivedDocument>> loadingDocuments;
        private ArrayList<Long> toFetchDocuments;

        public EmojiDocumentFetcher(int i) {
            this.currentAccount = i;
        }

        public void fetchDocument(long j, ReceivedDocument receivedDocument) {
            TLRPC$Document tLRPC$Document;
            HashMap<Long, TLRPC$Document> hashMap = this.emojiDocumentsCache;
            if (hashMap != null && (tLRPC$Document = hashMap.get(Long.valueOf(j))) != null) {
                receivedDocument.run(tLRPC$Document);
                return;
            }
            if (receivedDocument != null) {
                if (this.loadingDocuments == null) {
                    this.loadingDocuments = new HashMap<>();
                }
                ArrayList<ReceivedDocument> arrayList = this.loadingDocuments.get(Long.valueOf(j));
                if (arrayList != null) {
                    arrayList.add(receivedDocument);
                    return;
                }
                ArrayList<ReceivedDocument> arrayList2 = new ArrayList<>(1);
                arrayList2.add(receivedDocument);
                this.loadingDocuments.put(Long.valueOf(j), arrayList2);
            }
            if (this.toFetchDocuments == null) {
                this.toFetchDocuments = new ArrayList<>();
            }
            this.toFetchDocuments.add(Long.valueOf(j));
            if (this.fetchRunnable != null) {
                return;
            }
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AnimatedEmojiDrawable.EmojiDocumentFetcher.this.lambda$fetchDocument$0();
                }
            };
            this.fetchRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable);
        }

        public /* synthetic */ void lambda$fetchDocument$0() {
            this.fetchRunnable = null;
            loadFromDatabase(new ArrayList<>(this.toFetchDocuments));
        }

        private void loadFromDatabase(final ArrayList<Long> arrayList) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Components.AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AnimatedEmojiDrawable.EmojiDocumentFetcher.this.lambda$loadFromDatabase$2(arrayList);
                }
            });
        }

        public /* synthetic */ void lambda$loadFromDatabase$2(ArrayList arrayList) {
            SQLiteDatabase database = MessagesStorage.getInstance(this.currentAccount).getDatabase();
            try {
                SQLiteCursor queryFinalized = database.queryFinalized(String.format(Locale.US, "SELECT data FROM animated_emoji WHERE document_id IN (%s)", TextUtils.join(",", arrayList)), new Object[0]);
                final ArrayList arrayList2 = new ArrayList();
                final ArrayList arrayList3 = new ArrayList(arrayList);
                while (queryFinalized.next()) {
                    NativeByteBuffer byteBufferValue = queryFinalized.byteBufferValue(0);
                    try {
                        TLRPC$Document TLdeserialize = TLRPC$Document.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(true), true);
                        if (TLdeserialize != null && TLdeserialize.id != 0) {
                            arrayList2.add(TLdeserialize);
                            arrayList3.remove(Long.valueOf(TLdeserialize.id));
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    if (byteBufferValue != null) {
                        byteBufferValue.reuse();
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        AnimatedEmojiDrawable.EmojiDocumentFetcher.this.lambda$loadFromDatabase$1(arrayList2, arrayList3);
                    }
                });
                queryFinalized.dispose();
            } catch (SQLiteException e2) {
                FileLog.e(e2);
            }
        }

        public /* synthetic */ void lambda$loadFromDatabase$1(ArrayList arrayList, ArrayList arrayList2) {
            processDocuments(arrayList);
            if (!arrayList2.isEmpty()) {
                loadFromServer(arrayList2);
            }
        }

        private void loadFromServer(ArrayList<Long> arrayList) {
            final TLRPC$TL_messages_getCustomEmojiDocuments tLRPC$TL_messages_getCustomEmojiDocuments = new TLRPC$TL_messages_getCustomEmojiDocuments();
            tLRPC$TL_messages_getCustomEmojiDocuments.document_id = arrayList;
            this.toFetchDocuments.clear();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_messages_getCustomEmojiDocuments, new RequestDelegate() { // from class: org.telegram.ui.Components.AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda5
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AnimatedEmojiDrawable.EmojiDocumentFetcher.this.lambda$loadFromServer$4(tLRPC$TL_messages_getCustomEmojiDocuments, tLObject, tLRPC$TL_error);
                }
            });
        }

        public /* synthetic */ void lambda$loadFromServer$4(final TLRPC$TL_messages_getCustomEmojiDocuments tLRPC$TL_messages_getCustomEmojiDocuments, final TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    AnimatedEmojiDrawable.EmojiDocumentFetcher.this.lambda$loadFromServer$3(tLObject, tLRPC$TL_messages_getCustomEmojiDocuments);
                }
            });
        }

        public /* synthetic */ void lambda$loadFromServer$3(TLObject tLObject, TLRPC$TL_messages_getCustomEmojiDocuments tLRPC$TL_messages_getCustomEmojiDocuments) {
            if (tLObject instanceof TLRPC$Vector) {
                ArrayList<Object> arrayList = ((TLRPC$Vector) tLObject).objects;
                putToStorage(arrayList);
                processDocuments(arrayList);
                if (tLRPC$TL_messages_getCustomEmojiDocuments.document_id.size() <= arrayList.size()) {
                    return;
                }
                for (Long l : this.loadingDocuments.keySet()) {
                    fetchDocument(l.longValue(), null);
                }
            }
        }

        private void putToStorage(final ArrayList<Object> arrayList) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Components.AnimatedEmojiDrawable$EmojiDocumentFetcher$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    AnimatedEmojiDrawable.EmojiDocumentFetcher.this.lambda$putToStorage$5(arrayList);
                }
            });
        }

        /* JADX WARN: Removed duplicated region for block: B:17:0x004d A[Catch: SQLiteException -> 0x0057, TryCatch #2 {SQLiteException -> 0x0057, blocks: (B:3:0x000a, B:4:0x0011, B:6:0x0017, B:8:0x001f, B:15:0x0047, B:17:0x004d, B:18:0x0050, B:19:0x0053), top: B:27:0x000a }] */
        /* JADX WARN: Removed duplicated region for block: B:32:0x0050 A[SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void lambda$putToStorage$5(java.util.ArrayList r7) {
            /*
                r6 = this;
                int r0 = r6.currentAccount
                org.telegram.messenger.MessagesStorage r0 = org.telegram.messenger.MessagesStorage.getInstance(r0)
                org.telegram.SQLite.SQLiteDatabase r0 = r0.getDatabase()
                java.lang.String r1 = "REPLACE INTO animated_emoji VALUES(?, ?)"
                org.telegram.SQLite.SQLitePreparedStatement r0 = r0.executeFast(r1)     // Catch: org.telegram.SQLite.SQLiteException -> L57
                r1 = 0
            L11:
                int r2 = r7.size()     // Catch: org.telegram.SQLite.SQLiteException -> L57
                if (r1 >= r2) goto L53
                java.lang.Object r2 = r7.get(r1)     // Catch: org.telegram.SQLite.SQLiteException -> L57
                boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$Document     // Catch: org.telegram.SQLite.SQLiteException -> L57
                if (r2 == 0) goto L50
                java.lang.Object r2 = r7.get(r1)     // Catch: org.telegram.SQLite.SQLiteException -> L57
                org.telegram.tgnet.TLRPC$Document r2 = (org.telegram.tgnet.TLRPC$Document) r2     // Catch: org.telegram.SQLite.SQLiteException -> L57
                r3 = 0
                org.telegram.tgnet.NativeByteBuffer r4 = new org.telegram.tgnet.NativeByteBuffer     // Catch: java.lang.Exception -> L46
                int r5 = r2.getObjectSize()     // Catch: java.lang.Exception -> L46
                r4.<init>(r5)     // Catch: java.lang.Exception -> L46
                r2.serializeToStream(r4)     // Catch: java.lang.Exception -> L43
                r0.requery()     // Catch: java.lang.Exception -> L43
                long r2 = r2.id     // Catch: java.lang.Exception -> L43
                r5 = 1
                r0.bindLong(r5, r2)     // Catch: java.lang.Exception -> L43
                r2 = 2
                r0.bindByteBuffer(r2, r4)     // Catch: java.lang.Exception -> L43
                r0.step()     // Catch: java.lang.Exception -> L43
                goto L4b
            L43:
                r2 = move-exception
                r3 = r4
                goto L47
            L46:
                r2 = move-exception
            L47:
                r2.printStackTrace()     // Catch: org.telegram.SQLite.SQLiteException -> L57
                r4 = r3
            L4b:
                if (r4 == 0) goto L50
                r4.reuse()     // Catch: org.telegram.SQLite.SQLiteException -> L57
            L50:
                int r1 = r1 + 1
                goto L11
            L53:
                r0.dispose()     // Catch: org.telegram.SQLite.SQLiteException -> L57
                goto L5b
            L57:
                r7 = move-exception
                org.telegram.messenger.FileLog.e(r7)
            L5b:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AnimatedEmojiDrawable.EmojiDocumentFetcher.lambda$putToStorage$5(java.util.ArrayList):void");
        }

        public void processDocuments(ArrayList<?> arrayList) {
            ArrayList<ReceivedDocument> remove;
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i) instanceof TLRPC$Document) {
                    TLRPC$Document tLRPC$Document = (TLRPC$Document) arrayList.get(i);
                    if (this.emojiDocumentsCache == null) {
                        this.emojiDocumentsCache = new HashMap<>();
                    }
                    this.emojiDocumentsCache.put(Long.valueOf(tLRPC$Document.id), tLRPC$Document);
                    HashMap<Long, ArrayList<ReceivedDocument>> hashMap = this.loadingDocuments;
                    if (hashMap != null && (remove = hashMap.remove(Long.valueOf(tLRPC$Document.id))) != null) {
                        for (int i2 = 0; i2 < remove.size(); i2++) {
                            remove.get(i2).run(tLRPC$Document);
                        }
                        remove.clear();
                    }
                }
            }
        }
    }

    public static TLRPC$Document findDocument(int i, long j) {
        EmojiDocumentFetcher documentFetcher = getDocumentFetcher(i);
        if (documentFetcher == null || documentFetcher.emojiDocumentsCache == null) {
            return null;
        }
        return (TLRPC$Document) documentFetcher.emojiDocumentsCache.get(Long.valueOf(j));
    }

    public AnimatedEmojiDrawable(int i, int i2, long j) {
        this.cacheType = i;
        if (i == 0) {
            TextPaint textPaint = Theme.chat_msgTextPaint;
            sizedp = (int) (((Math.abs(textPaint.ascent()) + Math.abs(textPaint.descent())) * 1.15f) / AndroidUtilities.density);
        } else if (i == 1 || i == 3 || i == 2) {
            sizedp = 34;
        }
        this.documentId = j;
        getDocumentFetcher(i2).fetchDocument(j, new ReceivedDocument() { // from class: org.telegram.ui.Components.AnimatedEmojiDrawable$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.AnimatedEmojiDrawable.ReceivedDocument
            public final void run(TLRPC$Document tLRPC$Document) {
                AnimatedEmojiDrawable.this.lambda$new$0(tLRPC$Document);
            }
        });
    }

    public /* synthetic */ void lambda$new$0(TLRPC$Document tLRPC$Document) {
        this.document = tLRPC$Document;
        initDocument();
    }

    public AnimatedEmojiDrawable(int i, int i2, TLRPC$Document tLRPC$Document) {
        this.cacheType = i;
        this.document = tLRPC$Document;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.AnimatedEmojiDrawable$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AnimatedEmojiDrawable.this.initDocument();
            }
        });
    }

    public long getDocumentId() {
        return this.documentId;
    }

    public void initDocument() {
        String str;
        if (this.document == null || this.imageReceiver != null) {
            return;
        }
        ImageReceiver imageReceiver = new ImageReceiver() { // from class: org.telegram.ui.Components.AnimatedEmojiDrawable.1
            @Override // org.telegram.messenger.ImageReceiver
            public void invalidate() {
                super.invalidate();
                AnimatedEmojiDrawable.this.invalidate();
            }

            @Override // org.telegram.messenger.ImageReceiver
            public boolean setImageBitmapByKey(Drawable drawable, String str2, int i, boolean z, int i2) {
                AnimatedEmojiDrawable.this.invalidate();
                return super.setImageBitmapByKey(drawable, str2, i, z, i2);
            }
        };
        this.imageReceiver = imageReceiver;
        if (this.cacheType != 0) {
            imageReceiver.setUniqKeyPrefix(this.cacheType + "_");
        }
        if ("video/webm".equals(this.document.mime_type)) {
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.document.thumbs, 90);
            this.imageReceiver.setParentView(new View(ApplicationLoader.applicationContext) { // from class: org.telegram.ui.Components.AnimatedEmojiDrawable.2
                @Override // android.view.View
                public void invalidate() {
                    AnimatedEmojiDrawable.this.invalidate();
                }
            });
            ImageLocation forDocument = ImageLocation.getForDocument(closestPhotoSizeWithSize, this.document);
            TLRPC$Document tLRPC$Document = this.document;
            this.imageReceiver.setImage(ImageLocation.getForDocument(this.document), sizedp + "_" + sizedp + "_pcache_" + ImageLoader.AUTOPLAY_FILTER, forDocument, null, null, tLRPC$Document.size, null, tLRPC$Document, 1);
        } else {
            SvgHelper.SvgDrawable svgDrawable = null;
            StringBuilder sb = new StringBuilder();
            if (this.cacheType != 0) {
                str = this.cacheType + "_";
            } else {
                str = "";
            }
            sb.append(str);
            sb.append(this.documentId);
            sb.append("@");
            sb.append(sizedp);
            sb.append("_");
            sb.append(sizedp);
            sb.append("_pcache");
            if (!ImageLoader.getInstance().hasLottieMemCache(sb.toString()) && (svgDrawable = DocumentObject.getSvgThumb(this.document.thumbs, "windowBackgroundWhiteGrayIcon", 0.2f)) != null) {
                svgDrawable.overrideWidthAndHeight(512, 512);
            }
            SvgHelper.SvgDrawable svgDrawable2 = svgDrawable;
            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(this.document.thumbs, 90);
            this.imageReceiver.setImage(ImageLocation.getForDocument(this.document), sizedp + "_" + sizedp + "_pcache", ImageLocation.getForDocument(closestPhotoSizeWithSize2, this.document), null, null, null, svgDrawable2, 0L, null, this.document, 1);
        }
        if (this.cacheType == 2) {
            this.imageReceiver.setLayerNum(7);
        }
        this.imageReceiver.setAspectFit(true);
        this.imageReceiver.setAllowStartLottieAnimation(true);
        this.imageReceiver.setAllowStartAnimation(true);
        this.imageReceiver.setAutoRepeat(1);
        this.imageReceiver.setAllowDecodeSingleFrame(true);
        updateAttachState();
    }

    void invalidate() {
        if (this.views != null) {
            for (int i = 0; i < this.views.size(); i++) {
                View view = this.views.get(i);
                if (view != null) {
                    view.invalidate();
                }
            }
        }
        if (this.holders != null) {
            for (int i2 = 0; i2 < this.holders.size(); i2++) {
                AnimatedEmojiSpan.AnimatedEmojiHolder animatedEmojiHolder = this.holders.get(i2);
                if (animatedEmojiHolder != null) {
                    animatedEmojiHolder.invalidate();
                }
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        ImageReceiver imageReceiver = this.imageReceiver;
        if (imageReceiver != null) {
            imageReceiver.setImageCoords(getBounds());
            this.imageReceiver.draw(canvas);
            return;
        }
        if (placeholderPaint == null) {
            Paint paint = new Paint(1);
            placeholderPaint = paint;
            paint.setColor(Theme.isCurrentThemeDark() ? 268435455 : AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY);
        }
        int alpha = placeholderPaint.getAlpha();
        placeholderPaint.setAlpha((int) (alpha * this.alpha));
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(getBounds());
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), rectF.width() * 0.4f, placeholderPaint);
        placeholderPaint.setAlpha(alpha);
    }

    public void draw(Canvas canvas, android.graphics.Rect rect, float f) {
        ImageReceiver imageReceiver = this.imageReceiver;
        if (imageReceiver != null) {
            imageReceiver.setImageCoords(rect);
            this.imageReceiver.setAlpha(f);
            this.imageReceiver.draw(canvas);
            return;
        }
        if (placeholderPaint == null) {
            Paint paint = new Paint(1);
            placeholderPaint = paint;
            paint.setColor(Theme.isCurrentThemeDark() ? 268435455 : AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY);
        }
        int alpha = placeholderPaint.getAlpha();
        placeholderPaint.setAlpha((int) (alpha * f));
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(rect);
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), rectF.width() * 0.4f, placeholderPaint);
        placeholderPaint.setAlpha(alpha);
    }

    public void draw(Canvas canvas, ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder) {
        ImageReceiver imageReceiver = this.imageReceiver;
        if (imageReceiver != null) {
            imageReceiver.draw(canvas, backgroundThreadDrawHolder);
            return;
        }
        if (placeholderPaint == null) {
            Paint paint = new Paint(1);
            placeholderPaint = paint;
            paint.setColor(Theme.isCurrentThemeDark() ? 268435455 : AndroidUtilities.LIGHT_STATUS_BAR_OVERLAY);
        }
        int alpha = placeholderPaint.getAlpha();
        placeholderPaint.setAlpha((int) (alpha * this.alpha));
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(getBounds());
        canvas.drawCircle(rectF.centerX(), rectF.centerY(), rectF.width() * 0.4f, placeholderPaint);
        placeholderPaint.setAlpha(alpha);
    }

    public void addView(View view) {
        if (this.views == null) {
            this.views = new ArrayList<>(10);
        }
        if (!this.views.contains(view)) {
            this.views.add(view);
        }
        updateAttachState();
    }

    public void addView(AnimatedEmojiSpan.AnimatedEmojiHolder animatedEmojiHolder) {
        if (this.holders == null) {
            this.holders = new ArrayList<>(10);
        }
        if (!this.holders.contains(animatedEmojiHolder)) {
            this.holders.add(animatedEmojiHolder);
        }
        updateAttachState();
    }

    public void removeView(AnimatedEmojiSpan.AnimatedEmojiHolder animatedEmojiHolder) {
        ArrayList<AnimatedEmojiSpan.AnimatedEmojiHolder> arrayList = this.holders;
        if (arrayList != null) {
            arrayList.remove(animatedEmojiHolder);
        }
        updateAttachState();
    }

    public void removeView(View view) {
        ArrayList<View> arrayList = this.views;
        if (arrayList != null) {
            arrayList.remove(view);
        }
        updateAttachState();
    }

    private void updateAttachState() {
        ArrayList<AnimatedEmojiSpan.AnimatedEmojiHolder> arrayList;
        if (this.imageReceiver == null) {
            return;
        }
        ArrayList<View> arrayList2 = this.views;
        boolean z = (arrayList2 != null && arrayList2.size() > 0) || ((arrayList = this.holders) != null && arrayList.size() > 0);
        if (z == this.attached) {
            return;
        }
        this.attached = z;
        if (z) {
            count++;
            this.imageReceiver.onAttachedToWindow();
            return;
        }
        count--;
        this.imageReceiver.onDetachedFromWindow();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        ImageReceiver imageReceiver = this.imageReceiver;
        if (imageReceiver != null) {
            float f = i / 255.0f;
            this.alpha = f;
            imageReceiver.setAlpha(f);
        }
    }

    public ImageReceiver getImageReceiver() {
        return this.imageReceiver;
    }
}
