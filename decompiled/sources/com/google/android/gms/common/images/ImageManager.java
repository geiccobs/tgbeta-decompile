package com.google.android.gms.common.images;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;
import androidx.collection.LruCache;
import com.google.android.gms.common.internal.Asserts;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.internal.base.zak;
import com.google.android.gms.internal.base.zao;
import com.google.android.gms.internal.base.zap;
import com.google.android.gms.internal.base.zas;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
/* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
/* loaded from: classes3.dex */
public final class ImageManager {
    private static final Object zaa = new Object();
    private static HashSet<Uri> zab = new HashSet<>();
    private static ImageManager zac;
    private final Context zad;
    private final Handler zae = new zas(Looper.getMainLooper());
    private final ExecutorService zaf = zao.zaa().zaa(4, zap.zab);
    private final zaa zag = null;
    private final zak zah = new zak();
    private final Map<com.google.android.gms.common.images.zab, ImageReceiver> zai = new HashMap();
    private final Map<Uri, ImageReceiver> zaj = new HashMap();
    private final Map<Uri, Long> zak = new HashMap();

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    public interface OnImageLoadedListener {
        void onImageLoaded(Uri uri, Drawable drawable, boolean z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    public static final class zaa extends LruCache<com.google.android.gms.common.images.zaa, Bitmap> {
        @Override // androidx.collection.LruCache
        protected final /* synthetic */ int sizeOf(com.google.android.gms.common.images.zaa zaaVar, Bitmap bitmap) {
            throw new NoSuchMethodError();
        }

        @Override // androidx.collection.LruCache
        protected final /* synthetic */ void entryRemoved(boolean z, com.google.android.gms.common.images.zaa zaaVar, Bitmap bitmap, Bitmap bitmap2) {
            throw new NoSuchMethodError();
        }
    }

    public static ImageManager create(Context context) {
        if (zac == null) {
            zac = new ImageManager(context, false);
        }
        return zac;
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    public final class zab implements Runnable {
        private final com.google.android.gms.common.images.zab zaa;

        public zab(com.google.android.gms.common.images.zab zabVar) {
            ImageManager.this = r1;
            this.zaa = zabVar;
        }

        @Override // java.lang.Runnable
        public final void run() {
            Asserts.checkMainThread("LoadImageRunnable must be executed on the main thread");
            ImageReceiver imageReceiver = (ImageReceiver) ImageManager.this.zai.get(this.zaa);
            if (imageReceiver != null) {
                ImageManager.this.zai.remove(this.zaa);
                imageReceiver.zab(this.zaa);
            }
            com.google.android.gms.common.images.zaa zaaVar = this.zaa.zaa;
            if (zaaVar.zaa == null) {
                this.zaa.zaa(ImageManager.this.zad, ImageManager.this.zah, true);
                return;
            }
            Bitmap zaa = ImageManager.this.zaa(zaaVar);
            if (zaa != null) {
                this.zaa.zaa(ImageManager.this.zad, zaa, true);
                return;
            }
            Long l = (Long) ImageManager.this.zak.get(zaaVar.zaa);
            if (l != null) {
                if (SystemClock.elapsedRealtime() - l.longValue() < 3600000) {
                    this.zaa.zaa(ImageManager.this.zad, ImageManager.this.zah, true);
                    return;
                }
                ImageManager.this.zak.remove(zaaVar.zaa);
            }
            this.zaa.zaa(ImageManager.this.zad, ImageManager.this.zah);
            ImageReceiver imageReceiver2 = (ImageReceiver) ImageManager.this.zaj.get(zaaVar.zaa);
            if (imageReceiver2 == null) {
                imageReceiver2 = new ImageReceiver(zaaVar.zaa);
                ImageManager.this.zaj.put(zaaVar.zaa, imageReceiver2);
            }
            imageReceiver2.zaa(this.zaa);
            if (!(this.zaa instanceof com.google.android.gms.common.images.zac)) {
                ImageManager.this.zai.put(this.zaa, imageReceiver2);
            }
            synchronized (ImageManager.zaa) {
                if (!ImageManager.zab.contains(zaaVar.zaa)) {
                    ImageManager.zab.add(zaaVar.zaa);
                    imageReceiver2.zaa();
                }
            }
        }
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    private final class zac implements Runnable {
        private final Uri zaa;
        private final ParcelFileDescriptor zab;

        public zac(Uri uri, ParcelFileDescriptor parcelFileDescriptor) {
            ImageManager.this = r1;
            this.zaa = uri;
            this.zab = parcelFileDescriptor;
        }

        @Override // java.lang.Runnable
        public final void run() {
            boolean z;
            Bitmap bitmap;
            Asserts.checkNotMainThread("LoadBitmapFromDiskRunnable can't be executed in the main thread");
            ParcelFileDescriptor parcelFileDescriptor = this.zab;
            boolean z2 = false;
            Bitmap bitmap2 = null;
            if (parcelFileDescriptor == null) {
                bitmap = null;
                z = false;
            } else {
                try {
                    bitmap2 = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor());
                } catch (OutOfMemoryError e) {
                    String valueOf = String.valueOf(this.zaa);
                    StringBuilder sb = new StringBuilder(String.valueOf(valueOf).length() + 34);
                    sb.append("OOM while loading bitmap for uri: ");
                    sb.append(valueOf);
                    Log.e("ImageManager", sb.toString(), e);
                    z2 = true;
                }
                try {
                    this.zab.close();
                } catch (IOException e2) {
                    Log.e("ImageManager", "closed failed", e2);
                }
                z = z2;
                bitmap = bitmap2;
            }
            CountDownLatch countDownLatch = new CountDownLatch(1);
            ImageManager.this.zae.post(new zad(this.zaa, bitmap, z, countDownLatch));
            try {
                countDownLatch.await();
            } catch (InterruptedException e3) {
                String valueOf2 = String.valueOf(this.zaa);
                StringBuilder sb2 = new StringBuilder(String.valueOf(valueOf2).length() + 32);
                sb2.append("Latch interrupted while posting ");
                sb2.append(valueOf2);
                Log.w("ImageManager", sb2.toString());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    public final class ImageReceiver extends ResultReceiver {
        private final Uri zaa;
        private final ArrayList<com.google.android.gms.common.images.zab> zab = new ArrayList<>();

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        ImageReceiver(Uri uri) {
            super(new zas(Looper.getMainLooper()));
            ImageManager.this = r2;
            this.zaa = uri;
        }

        public final void zaa(com.google.android.gms.common.images.zab zabVar) {
            Asserts.checkMainThread("ImageReceiver.addImageRequest() must be called in the main thread");
            this.zab.add(zabVar);
        }

        public final void zab(com.google.android.gms.common.images.zab zabVar) {
            Asserts.checkMainThread("ImageReceiver.removeImageRequest() must be called in the main thread");
            this.zab.remove(zabVar);
        }

        public final void zaa() {
            Intent intent = new Intent(Constants.ACTION_LOAD_IMAGE);
            intent.setPackage("com.google.android.gms");
            intent.putExtra(Constants.EXTRA_URI, this.zaa);
            intent.putExtra(Constants.EXTRA_RESULT_RECEIVER, this);
            intent.putExtra(Constants.EXTRA_PRIORITY, 3);
            ImageManager.this.zad.sendBroadcast(intent);
        }

        @Override // android.os.ResultReceiver
        public final void onReceiveResult(int i, Bundle bundle) {
            ImageManager.this.zaf.execute(new zac(this.zaa, (ParcelFileDescriptor) bundle.getParcelable("com.google.android.gms.extra.fileDescriptor")));
        }
    }

    /* compiled from: com.google.android.gms:play-services-base@@17.5.0 */
    /* loaded from: classes3.dex */
    private final class zad implements Runnable {
        private final Uri zaa;
        private final Bitmap zab;
        private final CountDownLatch zac;
        private boolean zad;

        public zad(Uri uri, Bitmap bitmap, boolean z, CountDownLatch countDownLatch) {
            ImageManager.this = r1;
            this.zaa = uri;
            this.zab = bitmap;
            this.zad = z;
            this.zac = countDownLatch;
        }

        @Override // java.lang.Runnable
        public final void run() {
            Asserts.checkMainThread("OnBitmapLoadedRunnable must be executed in the main thread");
            boolean z = this.zab != null;
            ImageReceiver imageReceiver = (ImageReceiver) ImageManager.this.zaj.remove(this.zaa);
            if (imageReceiver != null) {
                ArrayList arrayList = imageReceiver.zab;
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    com.google.android.gms.common.images.zab zabVar = (com.google.android.gms.common.images.zab) arrayList.get(i);
                    if (this.zab != null && z) {
                        zabVar.zaa(ImageManager.this.zad, this.zab, false);
                    } else {
                        ImageManager.this.zak.put(this.zaa, Long.valueOf(SystemClock.elapsedRealtime()));
                        zabVar.zaa(ImageManager.this.zad, ImageManager.this.zah, false);
                    }
                    if (!(zabVar instanceof com.google.android.gms.common.images.zac)) {
                        ImageManager.this.zai.remove(zabVar);
                    }
                }
            }
            this.zac.countDown();
            synchronized (ImageManager.zaa) {
                ImageManager.zab.remove(this.zaa);
            }
        }
    }

    private ImageManager(Context context, boolean z) {
        this.zad = context.getApplicationContext();
    }

    public final void loadImage(ImageView imageView, Uri uri) {
        zaa(new com.google.android.gms.common.images.zad(imageView, uri));
    }

    public final void loadImage(ImageView imageView, int i) {
        zaa(new com.google.android.gms.common.images.zad(imageView, i));
    }

    public final void loadImage(ImageView imageView, Uri uri, int i) {
        com.google.android.gms.common.images.zad zadVar = new com.google.android.gms.common.images.zad(imageView, uri);
        zadVar.zab = i;
        zaa(zadVar);
    }

    public final void loadImage(OnImageLoadedListener onImageLoadedListener, Uri uri) {
        zaa(new com.google.android.gms.common.images.zac(onImageLoadedListener, uri));
    }

    public final void loadImage(OnImageLoadedListener onImageLoadedListener, Uri uri, int i) {
        com.google.android.gms.common.images.zac zacVar = new com.google.android.gms.common.images.zac(onImageLoadedListener, uri);
        zacVar.zab = i;
        zaa(zacVar);
    }

    private final void zaa(com.google.android.gms.common.images.zab zabVar) {
        Asserts.checkMainThread("ImageManager.loadImage() must be called in the main thread");
        new zab(zabVar).run();
    }

    public final Bitmap zaa(com.google.android.gms.common.images.zaa zaaVar) {
        return null;
    }
}
