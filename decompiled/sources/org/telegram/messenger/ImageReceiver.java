package org.telegram.messenger;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import java.util.ArrayList;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.LoadingStickerDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclableDrawable;
/* loaded from: classes4.dex */
public class ImageReceiver implements NotificationCenter.NotificationCenterDelegate {
    public static final int DEFAULT_CROSSFADE_DURATION = 150;
    private static final int TYPE_CROSSFDADE = 2;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_MEDIA = 3;
    public static final int TYPE_THUMB = 1;
    private boolean allowDecodeSingleFrame;
    private boolean allowStartAnimation;
    private boolean allowStartLottieAnimation;
    private int animateFromIsPressed;
    public int animatedFileDrawableRepeatMaxCount;
    private boolean animationReadySent;
    private boolean attachedToWindow;
    private int autoRepeat;
    private RectF bitmapRect;
    private Object blendMode;
    private boolean canceledLoading;
    private boolean centerRotation;
    private ColorFilter colorFilter;
    private ComposeShader composeShader;
    private byte crossfadeAlpha;
    private int crossfadeDuration;
    private Drawable crossfadeImage;
    private String crossfadeKey;
    private BitmapShader crossfadeShader;
    private boolean crossfadeWithOldImage;
    private boolean crossfadeWithThumb;
    private boolean crossfadingWithThumb;
    private int currentAccount;
    private float currentAlpha;
    private int currentCacheType;
    private String currentExt;
    private int currentGuid;
    private Drawable currentImageDrawable;
    private String currentImageFilter;
    private String currentImageKey;
    private ImageLocation currentImageLocation;
    private boolean currentKeyQuality;
    private int currentLayerNum;
    private Drawable currentMediaDrawable;
    private String currentMediaFilter;
    private String currentMediaKey;
    private ImageLocation currentMediaLocation;
    private int currentOpenedLayerFlags;
    private Object currentParentObject;
    private long currentSize;
    private Drawable currentThumbDrawable;
    private String currentThumbFilter;
    private String currentThumbKey;
    private ImageLocation currentThumbLocation;
    private ImageReceiverDelegate delegate;
    private RectF drawRegion;
    private long endTime;
    private boolean forceCrossfade;
    private boolean forceLoding;
    private boolean forcePreview;
    private Bitmap gradientBitmap;
    private BitmapShader gradientShader;
    private boolean ignoreImageSet;
    private float imageH;
    private int imageOrientation;
    private BitmapShader imageShader;
    private int imageTag;
    private float imageW;
    private float imageX;
    private float imageY;
    private boolean invalidateAll;
    private boolean isAspectFit;
    private int isPressed;
    private boolean isRoundRect;
    private boolean isRoundVideo;
    private boolean isVisible;
    private long lastUpdateAlphaTime;
    private Bitmap legacyBitmap;
    private Canvas legacyCanvas;
    private Paint legacyPaint;
    private BitmapShader legacyShader;
    private ArrayList<Runnable> loadingOperations;
    private boolean manualAlphaAnimator;
    private BitmapShader mediaShader;
    private int mediaTag;
    private boolean needsQualityThumb;
    private float overrideAlpha;
    private int param;
    private View parentView;
    private float pressedProgress;
    private float previousAlpha;
    private TLRPC.Document qulityThumbDocument;
    private Paint roundPaint;
    private Path roundPath;
    private int[] roundRadius;
    private RectF roundRect;
    private SetImageBackup setImageBackup;
    private Matrix shaderMatrix;
    private boolean shouldGenerateQualityThumb;
    private float sideClip;
    private long startTime;
    private Drawable staticThumbDrawable;
    private ImageLocation strippedLocation;
    private int thumbOrientation;
    private BitmapShader thumbShader;
    private int thumbTag;
    private String uniqKeyPrefix;
    private boolean useRoundForThumb;
    private boolean useSharedAnimationQueue;
    private boolean videoThumbIsSame;
    private static PorterDuffColorFilter selectedColorFilter = new PorterDuffColorFilter(-2236963, PorterDuff.Mode.MULTIPLY);
    private static PorterDuffColorFilter selectedGroupColorFilter = new PorterDuffColorFilter(-4473925, PorterDuff.Mode.MULTIPLY);
    private static float[] radii = new float[8];

    /* loaded from: classes4.dex */
    public interface ImageReceiverDelegate {
        void didSetImage(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3);

        void onAnimationReady(ImageReceiver imageReceiver);

        /* renamed from: org.telegram.messenger.ImageReceiver$ImageReceiverDelegate$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static void $default$onAnimationReady(ImageReceiverDelegate _this, ImageReceiver imageReceiver) {
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class BitmapHolder {
        public Bitmap bitmap;
        public Drawable drawable;
        private String key;
        public int orientation;
        private boolean recycleOnRelease;

        public BitmapHolder(Bitmap b, String k, int o) {
            this.bitmap = b;
            this.key = k;
            this.orientation = o;
            if (k != null) {
                ImageLoader.getInstance().incrementUseCount(this.key);
            }
        }

        public BitmapHolder(Drawable d, String k, int o) {
            this.drawable = d;
            this.key = k;
            this.orientation = o;
            if (k != null) {
                ImageLoader.getInstance().incrementUseCount(this.key);
            }
        }

        public BitmapHolder(Bitmap b) {
            this.bitmap = b;
            this.recycleOnRelease = true;
        }

        public int getWidth() {
            Bitmap bitmap = this.bitmap;
            if (bitmap != null) {
                return bitmap.getWidth();
            }
            return 0;
        }

        public int getHeight() {
            Bitmap bitmap = this.bitmap;
            if (bitmap != null) {
                return bitmap.getHeight();
            }
            return 0;
        }

        public boolean isRecycled() {
            Bitmap bitmap = this.bitmap;
            return bitmap == null || bitmap.isRecycled();
        }

        public void release() {
            Bitmap bitmap;
            if (this.key == null) {
                if (this.recycleOnRelease && (bitmap = this.bitmap) != null) {
                    bitmap.recycle();
                }
                this.bitmap = null;
                this.drawable = null;
                return;
            }
            boolean canDelete = ImageLoader.getInstance().decrementUseCount(this.key);
            if (!ImageLoader.getInstance().isInMemCache(this.key, false) && canDelete) {
                Bitmap bitmap2 = this.bitmap;
                if (bitmap2 != null) {
                    bitmap2.recycle();
                } else {
                    Drawable drawable = this.drawable;
                    if (drawable != null) {
                        if (drawable instanceof RLottieDrawable) {
                            RLottieDrawable fileDrawable = (RLottieDrawable) drawable;
                            fileDrawable.recycle();
                        } else if (drawable instanceof AnimatedFileDrawable) {
                            AnimatedFileDrawable fileDrawable2 = (AnimatedFileDrawable) drawable;
                            fileDrawable2.recycle();
                        } else if (drawable instanceof BitmapDrawable) {
                            Bitmap bitmap3 = ((BitmapDrawable) drawable).getBitmap();
                            bitmap3.recycle();
                        }
                    }
                }
            }
            this.key = null;
            this.bitmap = null;
            this.drawable = null;
        }
    }

    /* loaded from: classes4.dex */
    public static class SetImageBackup {
        public int cacheType;
        public String ext;
        public String imageFilter;
        public ImageLocation imageLocation;
        public String mediaFilter;
        public ImageLocation mediaLocation;
        public Object parentObject;
        public long size;
        public Drawable thumb;
        public String thumbFilter;
        public ImageLocation thumbLocation;

        private SetImageBackup() {
        }

        public boolean isSet() {
            return (this.imageLocation == null && this.thumbLocation == null && this.mediaLocation == null && this.thumb == null) ? false : true;
        }

        public boolean isWebfileSet() {
            ImageLocation imageLocation;
            ImageLocation imageLocation2;
            ImageLocation imageLocation3 = this.imageLocation;
            return ((imageLocation3 == null || (imageLocation3.webFile == null && this.imageLocation.path == null)) && ((imageLocation = this.thumbLocation) == null || (imageLocation.webFile == null && this.thumbLocation.path == null)) && ((imageLocation2 = this.mediaLocation) == null || (imageLocation2.webFile == null && this.mediaLocation.path == null))) ? false : true;
        }

        public void clear() {
            this.imageLocation = null;
            this.thumbLocation = null;
            this.mediaLocation = null;
            this.thumb = null;
        }
    }

    public ImageReceiver() {
        this(null);
    }

    public ImageReceiver(View view) {
        this.useRoundForThumb = true;
        this.allowStartAnimation = true;
        this.allowStartLottieAnimation = true;
        this.autoRepeat = 1;
        this.drawRegion = new RectF();
        this.isVisible = true;
        this.roundRadius = new int[4];
        this.isRoundRect = true;
        this.roundRect = new RectF();
        this.bitmapRect = new RectF();
        this.shaderMatrix = new Matrix();
        this.roundPath = new Path();
        this.overrideAlpha = 1.0f;
        this.previousAlpha = 1.0f;
        this.crossfadeAlpha = (byte) 1;
        this.crossfadeDuration = 150;
        this.loadingOperations = new ArrayList<>();
        this.parentView = view;
        this.roundPaint = new Paint(3);
        this.currentAccount = UserConfig.selectedAccount;
    }

    public void cancelLoadImage() {
        this.forceLoding = false;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
        this.canceledLoading = true;
    }

    public void setForceLoading(boolean value) {
        this.forceLoding = value;
    }

    public boolean isForceLoding() {
        return this.forceLoding;
    }

    public void setStrippedLocation(ImageLocation location) {
        this.strippedLocation = location;
    }

    public void setIgnoreImageSet(boolean value) {
        this.ignoreImageSet = value;
    }

    public ImageLocation getStrippedLocation() {
        return this.strippedLocation;
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Drawable thumb, String ext, Object parentObject, int cacheType) {
        setImage(imageLocation, imageFilter, null, null, thumb, 0L, ext, parentObject, cacheType);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, Drawable thumb, long size, String ext, Object parentObject, int cacheType) {
        setImage(imageLocation, imageFilter, null, null, thumb, size, ext, parentObject, cacheType);
    }

    public void setImage(String imagePath, String imageFilter, Drawable thumb, String ext, long size) {
        setImage(ImageLocation.getForPath(imagePath), imageFilter, null, null, thumb, size, ext, null, 1);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, String ext, Object parentObject, int cacheType) {
        setImage(imageLocation, imageFilter, thumbLocation, thumbFilter, null, 0L, ext, parentObject, cacheType);
    }

    public void setImage(ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, long size, String ext, Object parentObject, int cacheType) {
        setImage(imageLocation, imageFilter, thumbLocation, thumbFilter, null, size, ext, parentObject, cacheType);
    }

    public void setForUserOrChat(TLObject object, Drawable avatarDrawable) {
        setForUserOrChat(object, avatarDrawable, null);
    }

    public void setForUserOrChat(TLObject object, Drawable avatarDrawable, Object parentObject) {
        setForUserOrChat(object, avatarDrawable, null, false);
    }

    public void setForUserOrChat(TLObject object, Drawable avatarDrawable, Object parentObject, boolean animationEnabled) {
        Object parentObject2;
        ImageLocation videoLocation;
        boolean hasStripped;
        BitmapDrawable strippedBitmap;
        if (parentObject != null) {
            parentObject2 = parentObject;
        } else {
            parentObject2 = object;
        }
        setUseRoundForThumbDrawable(true);
        BitmapDrawable strippedBitmap2 = null;
        boolean hasStripped2 = false;
        ImageLocation videoLocation2 = null;
        boolean z = false;
        if (object instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) object;
            if (user.photo != null) {
                strippedBitmap2 = user.photo.strippedBitmap;
                hasStripped2 = user.photo.stripped_thumb != null;
                if (animationEnabled && MessagesController.getInstance(this.currentAccount).isPremiumUser(user) && user.photo.has_video) {
                    TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(user.id);
                    if (userFull == null) {
                        MessagesController.getInstance(this.currentAccount).loadFullUser(user, this.currentGuid, false);
                    } else if (userFull.profile_photo != null && userFull.profile_photo.video_sizes != null && !userFull.profile_photo.video_sizes.isEmpty()) {
                        TLRPC.VideoSize videoSize = userFull.profile_photo.video_sizes.get(0);
                        int i = 0;
                        while (true) {
                            if (i >= userFull.profile_photo.video_sizes.size()) {
                                break;
                            } else if (!TtmlNode.TAG_P.equals(userFull.profile_photo.video_sizes.get(i).type)) {
                                i++;
                            } else {
                                TLRPC.VideoSize videoSize2 = userFull.profile_photo.video_sizes.get(i);
                                videoSize = videoSize2;
                                break;
                            }
                        }
                        videoLocation2 = ImageLocation.getForPhoto(videoSize, userFull.profile_photo);
                    }
                }
            }
            strippedBitmap = strippedBitmap2;
            hasStripped = hasStripped2;
            videoLocation = videoLocation2;
        } else {
            if (object instanceof TLRPC.Chat) {
                TLRPC.Chat chat = (TLRPC.Chat) object;
                if (chat.photo != null) {
                    BitmapDrawable strippedBitmap3 = chat.photo.strippedBitmap;
                    if (chat.photo.stripped_thumb != null) {
                        z = true;
                    }
                    boolean hasStripped3 = z;
                    strippedBitmap = strippedBitmap3;
                    hasStripped = hasStripped3;
                    videoLocation = null;
                }
            }
            strippedBitmap = null;
            hasStripped = false;
            videoLocation = null;
        }
        ImageLocation location = ImageLocation.getForUserOrChat(object, 1);
        if (videoLocation != null) {
            setImage(videoLocation, "avatar", location, "50_50", null, null, strippedBitmap, 0L, null, parentObject2, 0);
            this.animatedFileDrawableRepeatMaxCount = 3;
        } else if (strippedBitmap != null) {
            setImage(location, "50_50", strippedBitmap, null, parentObject2, 0);
        } else if (hasStripped) {
            setImage(location, "50_50", ImageLocation.getForUserOrChat(object, 2), "50_50_b", avatarDrawable, parentObject2, 0);
        } else {
            setImage(location, "50_50", avatarDrawable, null, parentObject2, 0);
        }
    }

    public void setImage(ImageLocation fileLocation, String fileFilter, ImageLocation thumbLocation, String thumbFilter, Drawable thumb, Object parentObject, int cacheType) {
        setImage(null, null, fileLocation, fileFilter, thumbLocation, thumbFilter, thumb, 0L, null, parentObject, cacheType);
    }

    public void setImage(ImageLocation fileLocation, String fileFilter, ImageLocation thumbLocation, String thumbFilter, Drawable thumb, long size, String ext, Object parentObject, int cacheType) {
        setImage(null, null, fileLocation, fileFilter, thumbLocation, thumbFilter, thumb, size, ext, parentObject, cacheType);
    }

    public void setImage(ImageLocation mediaLocation, String mediaFilter, ImageLocation imageLocation, String imageFilter, ImageLocation thumbLocation, String thumbFilter, Drawable thumb, long size, String ext, Object parentObject, int cacheType) {
        ImageLocation strippedLoc;
        boolean z;
        String str;
        String str2;
        SetImageBackup setImageBackup;
        ImageLocation mediaLocation2 = mediaLocation;
        ImageLocation imageLocation2 = imageLocation;
        if (this.ignoreImageSet) {
            return;
        }
        if (this.crossfadeWithOldImage && (setImageBackup = this.setImageBackup) != null && setImageBackup.isWebfileSet()) {
            setBackupImage();
        }
        SetImageBackup setImageBackup2 = this.setImageBackup;
        if (setImageBackup2 != null) {
            setImageBackup2.clear();
        }
        if (imageLocation2 == null && thumbLocation == null && mediaLocation2 == null) {
            for (int a = 0; a < 4; a++) {
                recycleBitmap(null, a);
            }
            this.currentImageLocation = null;
            this.currentImageFilter = null;
            this.currentImageKey = null;
            this.currentMediaLocation = null;
            this.currentMediaFilter = null;
            this.currentMediaKey = null;
            this.currentThumbLocation = null;
            this.currentThumbFilter = null;
            this.currentThumbKey = null;
            this.currentMediaDrawable = null;
            this.mediaShader = null;
            this.currentImageDrawable = null;
            this.imageShader = null;
            this.composeShader = null;
            this.thumbShader = null;
            this.crossfadeShader = null;
            this.legacyShader = null;
            this.legacyCanvas = null;
            Bitmap bitmap = this.legacyBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.legacyBitmap = null;
            }
            this.currentExt = ext;
            this.currentParentObject = null;
            this.currentCacheType = 0;
            this.roundPaint.setShader(null);
            this.staticThumbDrawable = thumb;
            this.currentAlpha = 1.0f;
            this.previousAlpha = 1.0f;
            this.currentSize = 0L;
            if (thumb instanceof SvgHelper.SvgDrawable) {
                ((SvgHelper.SvgDrawable) thumb).setParent(this);
            }
            ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
            View view = this.parentView;
            if (view != null) {
                if (this.invalidateAll) {
                    view.invalidate();
                } else {
                    float f = this.imageX;
                    float f2 = this.imageY;
                    view.invalidate((int) f, (int) f2, (int) (f + this.imageW), (int) (f2 + this.imageH));
                }
            }
            ImageReceiverDelegate imageReceiverDelegate = this.delegate;
            if (imageReceiverDelegate != null) {
                Drawable drawable = this.currentImageDrawable;
                imageReceiverDelegate.didSetImage(this, (drawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true, drawable == null && this.currentMediaDrawable == null, false);
                return;
            }
            return;
        }
        String imageKey = imageLocation2 != null ? imageLocation2.getKey(parentObject, null, false) : null;
        if (imageKey == null && imageLocation2 != null) {
            imageLocation2 = null;
        }
        this.animatedFileDrawableRepeatMaxCount = 0;
        this.currentKeyQuality = false;
        if (imageKey == null && this.needsQualityThumb && ((parentObject instanceof MessageObject) || this.qulityThumbDocument != null)) {
            TLRPC.Document document = this.qulityThumbDocument;
            if (document == null) {
                document = ((MessageObject) parentObject).getDocument();
            }
            if (document != null && document.dc_id != 0 && document.id != 0) {
                imageKey = "q_" + document.dc_id + "_" + document.id;
                this.currentKeyQuality = true;
            }
        }
        if (imageKey != null && imageFilter != null) {
            imageKey = imageKey + "@" + imageFilter;
        }
        if (this.uniqKeyPrefix != null) {
            imageKey = this.uniqKeyPrefix + imageKey;
        }
        String mediaKey = mediaLocation2 != null ? mediaLocation2.getKey(parentObject, null, false) : null;
        if (mediaKey == null && mediaLocation2 != null) {
            mediaLocation2 = null;
        }
        if (mediaKey != null && mediaFilter != null) {
            mediaKey = mediaKey + "@" + mediaFilter;
        }
        if ((mediaKey == null && (str2 = this.currentImageKey) != null && str2.equals(imageKey)) || ((str = this.currentMediaKey) != null && str.equals(mediaKey))) {
            ImageReceiverDelegate imageReceiverDelegate2 = this.delegate;
            if (imageReceiverDelegate2 != null) {
                Drawable drawable2 = this.currentImageDrawable;
                imageReceiverDelegate2.didSetImage(this, (drawable2 == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true, drawable2 == null && this.currentMediaDrawable == null, false);
            }
            if (!this.canceledLoading) {
                return;
            }
        }
        if (this.strippedLocation != null) {
            strippedLoc = this.strippedLocation;
        } else {
            strippedLoc = mediaLocation2 != null ? mediaLocation2 : imageLocation2;
        }
        if (strippedLoc == null) {
            strippedLoc = thumbLocation;
        }
        String thumbKey = thumbLocation != null ? thumbLocation.getKey(parentObject, strippedLoc, false) : null;
        if (thumbKey != null && thumbFilter != null) {
            thumbKey = thumbKey + "@" + thumbFilter;
        }
        if (!this.crossfadeWithOldImage) {
            recycleBitmap(imageKey, 0);
            recycleBitmap(thumbKey, 1);
            recycleBitmap(null, 2);
            recycleBitmap(mediaKey, 3);
            this.crossfadeShader = null;
        } else {
            Drawable drawable3 = this.currentMediaDrawable;
            if (drawable3 != null) {
                if (drawable3 instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) drawable3).stop();
                    ((AnimatedFileDrawable) this.currentMediaDrawable).removeParent(this);
                }
                recycleBitmap(thumbKey, 1);
                recycleBitmap(null, 2);
                recycleBitmap(mediaKey, 0);
                this.crossfadeImage = this.currentMediaDrawable;
                this.crossfadeShader = this.mediaShader;
                this.crossfadeKey = this.currentImageKey;
                this.crossfadingWithThumb = false;
                this.currentMediaDrawable = null;
                this.currentMediaKey = null;
            } else if (this.currentImageDrawable != null) {
                recycleBitmap(thumbKey, 1);
                recycleBitmap(null, 2);
                recycleBitmap(mediaKey, 3);
                this.crossfadeShader = this.imageShader;
                this.crossfadeImage = this.currentImageDrawable;
                this.crossfadeKey = this.currentImageKey;
                this.crossfadingWithThumb = false;
                this.currentImageDrawable = null;
                this.currentImageKey = null;
            } else if (this.currentThumbDrawable != null) {
                recycleBitmap(imageKey, 0);
                recycleBitmap(null, 2);
                recycleBitmap(mediaKey, 3);
                this.crossfadeShader = this.thumbShader;
                this.crossfadeImage = this.currentThumbDrawable;
                this.crossfadeKey = this.currentThumbKey;
                this.crossfadingWithThumb = false;
                this.currentThumbDrawable = null;
                this.currentThumbKey = null;
            } else if (this.staticThumbDrawable != null) {
                recycleBitmap(imageKey, 0);
                recycleBitmap(thumbKey, 1);
                recycleBitmap(null, 2);
                recycleBitmap(mediaKey, 3);
                this.crossfadeShader = this.thumbShader;
                this.crossfadeImage = this.staticThumbDrawable;
                this.crossfadingWithThumb = false;
                this.crossfadeKey = null;
                this.currentThumbDrawable = null;
                this.currentThumbKey = null;
            } else {
                recycleBitmap(imageKey, 0);
                recycleBitmap(thumbKey, 1);
                recycleBitmap(null, 2);
                recycleBitmap(mediaKey, 3);
                this.crossfadeShader = null;
            }
        }
        this.currentImageLocation = imageLocation2;
        this.currentImageFilter = imageFilter;
        this.currentImageKey = imageKey;
        this.currentMediaLocation = mediaLocation2;
        this.currentMediaFilter = mediaFilter;
        this.currentMediaKey = mediaKey;
        this.currentThumbLocation = thumbLocation;
        this.currentThumbFilter = thumbFilter;
        this.currentThumbKey = thumbKey;
        this.currentParentObject = parentObject;
        this.currentExt = ext;
        this.currentSize = size;
        this.currentCacheType = cacheType;
        this.staticThumbDrawable = thumb;
        this.imageShader = null;
        this.composeShader = null;
        this.thumbShader = null;
        this.mediaShader = null;
        this.legacyShader = null;
        this.legacyCanvas = null;
        this.roundPaint.setShader(null);
        Bitmap bitmap2 = this.legacyBitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
            this.legacyBitmap = null;
        }
        this.currentAlpha = 1.0f;
        this.previousAlpha = 1.0f;
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 instanceof SvgHelper.SvgDrawable) {
            ((SvgHelper.SvgDrawable) drawable4).setParent(this);
        }
        updateDrawableRadius(this.staticThumbDrawable);
        ImageReceiverDelegate imageReceiverDelegate3 = this.delegate;
        if (imageReceiverDelegate3 != null) {
            Drawable drawable5 = this.currentImageDrawable;
            if (drawable5 == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) {
                z = false;
                imageReceiverDelegate3.didSetImage(this, z, drawable5 != null && this.currentMediaDrawable == null, false);
            }
            z = true;
            imageReceiverDelegate3.didSetImage(this, z, drawable5 != null && this.currentMediaDrawable == null, false);
        }
        ImageLoader.getInstance().loadImageForImageReceiver(this);
        View view2 = this.parentView;
        if (view2 != null) {
            if (this.invalidateAll) {
                view2.invalidate();
            } else {
                float f3 = this.imageX;
                float f4 = this.imageY;
                view2.invalidate((int) f3, (int) f4, (int) (f3 + this.imageW), (int) (f4 + this.imageH));
            }
        }
        this.isRoundVideo = (parentObject instanceof MessageObject) && ((MessageObject) parentObject).isRoundVideo();
    }

    public boolean canInvertBitmap() {
        return (this.currentMediaDrawable instanceof ExtendedBitmapDrawable) || (this.currentImageDrawable instanceof ExtendedBitmapDrawable) || (this.currentThumbDrawable instanceof ExtendedBitmapDrawable) || (this.staticThumbDrawable instanceof ExtendedBitmapDrawable);
    }

    public void setColorFilter(ColorFilter filter) {
        this.colorFilter = filter;
    }

    public void setDelegate(ImageReceiverDelegate delegate) {
        this.delegate = delegate;
    }

    public void setPressed(int value) {
        this.isPressed = value;
    }

    public boolean getPressed() {
        return this.isPressed != 0;
    }

    public void setOrientation(int angle, boolean center) {
        while (angle < 0) {
            angle += 360;
        }
        while (angle > 360) {
            angle -= 360;
        }
        this.thumbOrientation = angle;
        this.imageOrientation = angle;
        this.centerRotation = center;
    }

    public void setInvalidateAll(boolean value) {
        this.invalidateAll = value;
    }

    public Drawable getStaticThumb() {
        return this.staticThumbDrawable;
    }

    public int getAnimatedOrientation() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            return animation.getOrientation();
        }
        return 0;
    }

    public int getOrientation() {
        return this.imageOrientation;
    }

    public void setLayerNum(int value) {
        this.currentLayerNum = value;
    }

    public void setImageBitmap(Bitmap bitmap) {
        BitmapDrawable bitmapDrawable = null;
        if (bitmap != null) {
            bitmapDrawable = new BitmapDrawable((Resources) null, bitmap);
        }
        setImageBitmap(bitmapDrawable);
    }

    public void setImageBitmap(Drawable bitmap) {
        boolean z = true;
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
        if (this.crossfadeWithOldImage) {
            if (this.currentImageDrawable != null) {
                recycleBitmap(null, 1);
                recycleBitmap(null, 2);
                recycleBitmap(null, 3);
                this.crossfadeShader = this.imageShader;
                this.crossfadeImage = this.currentImageDrawable;
                this.crossfadeKey = this.currentImageKey;
                this.crossfadingWithThumb = true;
            } else if (this.currentThumbDrawable != null) {
                recycleBitmap(null, 0);
                recycleBitmap(null, 2);
                recycleBitmap(null, 3);
                this.crossfadeShader = this.thumbShader;
                this.crossfadeImage = this.currentThumbDrawable;
                this.crossfadeKey = this.currentThumbKey;
                this.crossfadingWithThumb = true;
            } else if (this.staticThumbDrawable != null) {
                recycleBitmap(null, 0);
                recycleBitmap(null, 1);
                recycleBitmap(null, 2);
                recycleBitmap(null, 3);
                this.crossfadeShader = this.thumbShader;
                this.crossfadeImage = this.staticThumbDrawable;
                this.crossfadingWithThumb = true;
                this.crossfadeKey = null;
            } else {
                for (int a = 0; a < 4; a++) {
                    recycleBitmap(null, a);
                }
                this.crossfadeShader = null;
            }
        } else {
            for (int a2 = 0; a2 < 4; a2++) {
                recycleBitmap(null, a2);
            }
        }
        Drawable drawable = this.staticThumbDrawable;
        if (drawable instanceof RecyclableDrawable) {
            RecyclableDrawable drawable2 = (RecyclableDrawable) drawable;
            drawable2.recycle();
        }
        if (bitmap instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) bitmap;
            fileDrawable.setParentView(this.parentView);
            if (this.attachedToWindow) {
                fileDrawable.addParent(this);
            }
            fileDrawable.setUseSharedQueue(this.useSharedAnimationQueue || fileDrawable.isWebmSticker);
            if (this.allowStartAnimation && this.currentOpenedLayerFlags == 0) {
                fileDrawable.checkRepeat();
            }
            fileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
        } else if (bitmap instanceof RLottieDrawable) {
            RLottieDrawable fileDrawable2 = (RLottieDrawable) bitmap;
            fileDrawable2.addParentView(this.parentView);
            if (this.allowStartLottieAnimation && (!fileDrawable2.isHeavyDrawable() || this.currentOpenedLayerFlags == 0)) {
                fileDrawable2.start();
            }
            fileDrawable2.setAllowDecodeSingleFrame(true);
        }
        this.thumbShader = null;
        this.roundPaint.setShader(null);
        this.staticThumbDrawable = bitmap;
        updateDrawableRadius(bitmap);
        this.currentMediaLocation = null;
        this.currentMediaFilter = null;
        Drawable drawable3 = this.currentMediaDrawable;
        if (drawable3 instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) drawable3).removeParent(this);
        }
        this.currentMediaDrawable = null;
        this.currentMediaKey = null;
        this.mediaShader = null;
        this.currentImageLocation = null;
        this.currentImageFilter = null;
        this.currentImageDrawable = null;
        this.currentImageKey = null;
        this.imageShader = null;
        this.composeShader = null;
        this.legacyShader = null;
        this.legacyCanvas = null;
        Bitmap bitmap2 = this.legacyBitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
            this.legacyBitmap = null;
        }
        this.currentThumbLocation = null;
        this.currentThumbFilter = null;
        this.currentThumbKey = null;
        this.currentKeyQuality = false;
        this.currentExt = null;
        this.currentSize = 0L;
        this.currentCacheType = 0;
        this.currentAlpha = 1.0f;
        this.previousAlpha = 1.0f;
        SetImageBackup setImageBackup = this.setImageBackup;
        if (setImageBackup != null) {
            setImageBackup.clear();
        }
        ImageReceiverDelegate imageReceiverDelegate = this.delegate;
        if (imageReceiverDelegate != null) {
            imageReceiverDelegate.didSetImage(this, (this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true, true, false);
        }
        View view = this.parentView;
        if (view != null) {
            if (this.invalidateAll) {
                view.invalidate();
            } else {
                float f = this.imageX;
                float f2 = this.imageY;
                view.invalidate((int) f, (int) f2, (int) (f + this.imageW), (int) (f2 + this.imageH));
            }
        }
        if (this.forceCrossfade && this.crossfadeWithOldImage && this.crossfadeImage != null) {
            this.currentAlpha = 0.0f;
            this.lastUpdateAlphaTime = System.currentTimeMillis();
            if (this.currentThumbDrawable == null && this.staticThumbDrawable == null) {
                z = false;
            }
            this.crossfadeWithThumb = z;
        }
    }

    private void setDrawableShader(Drawable drawable, BitmapShader shader) {
        if (drawable == this.currentThumbDrawable || drawable == this.staticThumbDrawable) {
            this.thumbShader = shader;
        } else if (drawable == this.currentMediaDrawable) {
            this.mediaShader = shader;
        } else if (drawable == this.currentImageDrawable) {
            this.imageShader = shader;
            if (this.gradientShader != null && (drawable instanceof BitmapDrawable)) {
                if (Build.VERSION.SDK_INT >= 28) {
                    this.composeShader = new ComposeShader(this.gradientShader, this.imageShader, PorterDuff.Mode.DST_IN);
                    return;
                }
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                int w = bitmapDrawable.getBitmap().getWidth();
                int h = bitmapDrawable.getBitmap().getHeight();
                Bitmap bitmap = this.legacyBitmap;
                if (bitmap == null || bitmap.getWidth() != w || this.legacyBitmap.getHeight() != h) {
                    Bitmap bitmap2 = this.legacyBitmap;
                    if (bitmap2 != null) {
                        bitmap2.recycle();
                    }
                    this.legacyBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    this.legacyCanvas = new Canvas(this.legacyBitmap);
                    this.legacyShader = new BitmapShader(this.legacyBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    if (this.legacyPaint == null) {
                        Paint paint = new Paint();
                        this.legacyPaint = paint;
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                    }
                }
            }
        }
    }

    private boolean hasRoundRadius() {
        return true;
    }

    private void updateDrawableRadius(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        if ((hasRoundRadius() || this.gradientShader != null) && (drawable instanceof BitmapDrawable)) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (!(bitmapDrawable instanceof RLottieDrawable)) {
                if (bitmapDrawable instanceof AnimatedFileDrawable) {
                    AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) drawable;
                    animatedFileDrawable.setRoundRadius(this.roundRadius);
                    return;
                } else if (bitmapDrawable.getBitmap() != null) {
                    setDrawableShader(drawable, new BitmapShader(bitmapDrawable.getBitmap(), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
                    return;
                } else {
                    return;
                }
            }
            return;
        }
        setDrawableShader(drawable, null);
    }

    public void clearImage() {
        for (int a = 0; a < 4; a++) {
            recycleBitmap(null, a);
        }
        ImageLoader.getInstance().cancelLoadingForImageReceiver(this, true);
    }

    public void onDetachedFromWindow() {
        this.attachedToWindow = false;
        if (this.currentImageLocation != null || this.currentMediaLocation != null || this.currentThumbLocation != null || this.staticThumbDrawable != null) {
            if (this.setImageBackup == null) {
                this.setImageBackup = new SetImageBackup();
            }
            this.setImageBackup.mediaLocation = this.currentMediaLocation;
            this.setImageBackup.mediaFilter = this.currentMediaFilter;
            this.setImageBackup.imageLocation = this.currentImageLocation;
            this.setImageBackup.imageFilter = this.currentImageFilter;
            this.setImageBackup.thumbLocation = this.currentThumbLocation;
            this.setImageBackup.thumbFilter = this.currentThumbFilter;
            this.setImageBackup.thumb = this.staticThumbDrawable;
            this.setImageBackup.size = this.currentSize;
            this.setImageBackup.ext = this.currentExt;
            this.setImageBackup.cacheType = this.currentCacheType;
            this.setImageBackup.parentObject = this.currentParentObject;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startAllHeavyOperations);
        if (this.staticThumbDrawable != null) {
            this.staticThumbDrawable = null;
            this.thumbShader = null;
            this.roundPaint.setShader(null);
        }
        clearImage();
        if (this.isPressed == 0) {
            this.pressedProgress = 0.0f;
        }
        AnimatedFileDrawable animatedFileDrawable = getAnimation();
        if (animatedFileDrawable != null) {
            animatedFileDrawable.removeParent(this);
        }
    }

    private boolean setBackupImage() {
        SetImageBackup setImageBackup = this.setImageBackup;
        if (setImageBackup != null && setImageBackup.isSet()) {
            SetImageBackup temp = this.setImageBackup;
            this.setImageBackup = null;
            setImage(temp.mediaLocation, temp.mediaFilter, temp.imageLocation, temp.imageFilter, temp.thumbLocation, temp.thumbFilter, temp.thumb, temp.size, temp.ext, temp.parentObject, temp.cacheType);
            temp.clear();
            this.setImageBackup = temp;
            RLottieDrawable lottieDrawable = getLottieAnimation();
            if (lottieDrawable == null || !this.allowStartLottieAnimation) {
                return true;
            }
            if (!lottieDrawable.isHeavyDrawable() || this.currentOpenedLayerFlags == 0) {
                lottieDrawable.start();
                return true;
            }
            return true;
        }
        return false;
    }

    public boolean onAttachedToWindow() {
        this.attachedToWindow = true;
        int currentHeavyOperationFlags = NotificationCenter.getGlobalInstance().getCurrentHeavyOperationFlags();
        this.currentOpenedLayerFlags = currentHeavyOperationFlags;
        this.currentOpenedLayerFlags = currentHeavyOperationFlags & (this.currentLayerNum ^ (-1));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopAllHeavyOperations);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startAllHeavyOperations);
        if (setBackupImage()) {
            return true;
        }
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null && this.allowStartLottieAnimation && (!lottieDrawable.isHeavyDrawable() || this.currentOpenedLayerFlags == 0)) {
            lottieDrawable.start();
        }
        AnimatedFileDrawable animatedFileDrawable = getAnimation();
        if (animatedFileDrawable != null && this.parentView != null) {
            animatedFileDrawable.addParent(this);
        }
        if (animatedFileDrawable != null && this.allowStartAnimation && this.currentOpenedLayerFlags == 0) {
            animatedFileDrawable.checkRepeat();
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
        }
        if (NotificationCenter.getGlobalInstance().isAnimationInProgress()) {
            didReceivedNotification(NotificationCenter.stopAllHeavyOperations, this.currentAccount, 512);
        }
        return false;
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int alpha, BitmapShader shader, int orientation) {
        if (this.isPressed == 0) {
            float f = this.pressedProgress;
            if (f != 0.0f) {
                float f2 = f - 0.10666667f;
                this.pressedProgress = f2;
                if (f2 < 0.0f) {
                    this.pressedProgress = 0.0f;
                }
                View view = this.parentView;
                if (view != null) {
                    view.invalidate();
                }
            }
        }
        int i = this.isPressed;
        if (i != 0) {
            this.pressedProgress = 1.0f;
            this.animateFromIsPressed = i;
        }
        float f3 = this.pressedProgress;
        if (f3 == 0.0f || f3 == 1.0f) {
            drawDrawable(canvas, drawable, alpha, shader, orientation, i);
            return;
        }
        drawDrawable(canvas, drawable, alpha, shader, orientation, i);
        drawDrawable(canvas, drawable, (int) (alpha * this.pressedProgress), shader, orientation, this.animateFromIsPressed);
    }

    public void setUseRoundForThumbDrawable(boolean value) {
        this.useRoundForThumb = value;
    }

    private void drawDrawable(Canvas canvas, Drawable drawable, int alpha, BitmapShader shader, int orientation, int isPressed) {
        Paint paint;
        int bitmapW;
        int bitmapH;
        int[] iArr;
        int[] iArr2;
        int bitmapW2;
        int[] iArr3;
        int[] iArr4;
        if (!(drawable instanceof BitmapDrawable)) {
            float scaleH = 1.0f;
            if (this.isAspectFit) {
                int bitmapW3 = drawable.getIntrinsicWidth();
                int bitmapH2 = drawable.getIntrinsicHeight();
                float f = this.imageW;
                float f2 = this.sideClip;
                float realImageW = f - (f2 * 2.0f);
                float f3 = this.imageH;
                float realImageH = f3 - (f2 * 2.0f);
                float scaleW = f == 0.0f ? 1.0f : bitmapW3 / realImageW;
                if (f3 != 0.0f) {
                    scaleH = bitmapH2 / realImageH;
                }
                float scale = Math.max(scaleW, scaleH);
                int bitmapW4 = (int) (bitmapW3 / scale);
                int bitmapH3 = (int) (bitmapH2 / scale);
                RectF rectF = this.drawRegion;
                float f4 = this.imageX;
                float f5 = this.imageW;
                float f6 = this.imageY;
                float f7 = this.imageH;
                float scaleW2 = bitmapH3;
                float realImageH2 = bitmapW4;
                rectF.set(((f5 - bitmapW4) / 2.0f) + f4, ((f7 - scaleW2) / 2.0f) + f6, f4 + ((f5 + realImageH2) / 2.0f), f6 + ((f7 + bitmapH3) / 2.0f));
            } else {
                RectF rectF2 = this.drawRegion;
                float f8 = this.imageX;
                float f9 = this.imageY;
                rectF2.set(f8, f9, this.imageW + f8, this.imageH + f9);
            }
            drawable.setBounds((int) this.drawRegion.left, (int) this.drawRegion.top, (int) this.drawRegion.right, (int) this.drawRegion.bottom);
            if (this.isVisible) {
                try {
                    drawable.setAlpha(alpha);
                    drawable.draw(canvas);
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
            }
            return;
        }
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        if (shader != null) {
            paint = this.roundPaint;
        } else {
            paint = bitmapDrawable.getPaint();
        }
        if (Build.VERSION.SDK_INT >= 29) {
            Object obj = this.blendMode;
            if (obj != null && this.gradientShader == null) {
                paint.setBlendMode((BlendMode) obj);
            } else {
                paint.setBlendMode(null);
            }
        }
        boolean hasFilter = (paint == null || paint.getColorFilter() == null) ? false : true;
        if (hasFilter && isPressed == 0) {
            if (shader != null) {
                this.roundPaint.setColorFilter(null);
            } else if (this.staticThumbDrawable != drawable) {
                bitmapDrawable.setColorFilter(null);
            }
        } else if (!hasFilter && isPressed != 0) {
            if (isPressed == 1) {
                if (shader != null) {
                    this.roundPaint.setColorFilter(selectedColorFilter);
                } else {
                    bitmapDrawable.setColorFilter(selectedColorFilter);
                }
            } else if (shader != null) {
                this.roundPaint.setColorFilter(selectedGroupColorFilter);
            } else {
                bitmapDrawable.setColorFilter(selectedGroupColorFilter);
            }
        }
        ColorFilter colorFilter = this.colorFilter;
        if (colorFilter != null && this.gradientShader == null) {
            if (shader != null) {
                this.roundPaint.setColorFilter(colorFilter);
            } else {
                bitmapDrawable.setColorFilter(colorFilter);
            }
        }
        if ((bitmapDrawable instanceof AnimatedFileDrawable) || (bitmapDrawable instanceof RLottieDrawable)) {
            if (orientation % 360 == 90 || orientation % 360 == 270) {
                bitmapW = bitmapDrawable.getIntrinsicHeight();
                bitmapH = bitmapDrawable.getIntrinsicWidth();
            } else {
                bitmapW = bitmapDrawable.getIntrinsicWidth();
                bitmapH = bitmapDrawable.getIntrinsicHeight();
            }
        } else {
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && bitmap.isRecycled()) {
                return;
            }
            if (orientation % 360 == 90 || orientation % 360 == 270) {
                bitmapW = bitmap.getHeight();
                bitmapH = bitmap.getWidth();
            } else {
                bitmapW = bitmap.getWidth();
                bitmapH = bitmap.getHeight();
            }
        }
        float f10 = this.imageW;
        float f11 = this.sideClip;
        float realImageW2 = f10 - (f11 * 2.0f);
        float f12 = this.imageH;
        float realImageH3 = f12 - (f11 * 2.0f);
        float scaleW3 = f10 == 0.0f ? 1.0f : bitmapW / realImageW2;
        float scaleH2 = f12 == 0.0f ? 1.0f : bitmapH / realImageH3;
        if (shader == null) {
            if (this.isAspectFit) {
                float scale2 = Math.max(scaleW3, scaleH2);
                canvas.save();
                int bitmapW5 = (int) (bitmapW / scale2);
                int bitmapH4 = (int) (bitmapH / scale2);
                RectF rectF3 = this.drawRegion;
                float f13 = this.imageX;
                float f14 = this.imageW;
                float f15 = this.imageY;
                float f16 = this.imageH;
                rectF3.set(((f14 - bitmapW5) / 2.0f) + f13, ((f16 - bitmapH4) / 2.0f) + f15, f13 + ((f14 + bitmapW5) / 2.0f), f15 + ((f16 + bitmapH4) / 2.0f));
                bitmapDrawable.setBounds((int) this.drawRegion.left, (int) this.drawRegion.top, (int) this.drawRegion.right, (int) this.drawRegion.bottom);
                if (bitmapDrawable instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) bitmapDrawable).setActualDrawRect(this.drawRegion.left, this.drawRegion.top, this.drawRegion.width(), this.drawRegion.height());
                }
                if (this.isVisible) {
                    try {
                        bitmapDrawable.setAlpha(alpha);
                        bitmapDrawable.draw(canvas);
                    } catch (Exception e2) {
                        onBitmapException(bitmapDrawable);
                        FileLog.e(e2);
                    }
                }
                canvas.restore();
            } else if (Math.abs(scaleW3 - scaleH2) > 1.0E-5f) {
                canvas.save();
                float f17 = this.imageX;
                float f18 = this.imageY;
                canvas.clipRect(f17, f18, this.imageW + f17, this.imageH + f18);
                if (orientation % 360 != 0) {
                    if (this.centerRotation) {
                        canvas.rotate(orientation, this.imageW / 2.0f, this.imageH / 2.0f);
                    } else {
                        canvas.rotate(orientation, 0.0f, 0.0f);
                    }
                }
                float f19 = bitmapW / scaleH2;
                float f20 = this.imageW;
                if (f19 > f20) {
                    int bitmapW6 = (int) (bitmapW / scaleH2);
                    RectF rectF4 = this.drawRegion;
                    float f21 = this.imageX;
                    float f22 = this.imageY;
                    rectF4.set(f21 - ((bitmapW6 - f20) / 2.0f), f22, f21 + ((bitmapW6 + f20) / 2.0f), this.imageH + f22);
                } else {
                    int bitmapH5 = (int) (bitmapH / scaleW3);
                    RectF rectF5 = this.drawRegion;
                    float f23 = this.imageX;
                    float f24 = this.imageY;
                    float f25 = this.imageH;
                    float scaleW4 = bitmapH5;
                    rectF5.set(f23, f24 - ((bitmapH5 - f25) / 2.0f), f20 + f23, f24 + ((scaleW4 + f25) / 2.0f));
                }
                if (bitmapDrawable instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) bitmapDrawable).setActualDrawRect(this.imageX, this.imageY, this.imageW, this.imageH);
                }
                if (orientation % 360 != 90 && orientation % 360 != 270) {
                    bitmapDrawable.setBounds((int) this.drawRegion.left, (int) this.drawRegion.top, (int) this.drawRegion.right, (int) this.drawRegion.bottom);
                } else {
                    float width = this.drawRegion.width() / 2.0f;
                    float height = this.drawRegion.height() / 2.0f;
                    float centerX = this.drawRegion.centerX();
                    float centerY = this.drawRegion.centerY();
                    bitmapDrawable.setBounds((int) (centerX - height), (int) (centerY - width), (int) (centerX + height), (int) (centerY + width));
                }
                if (this.isVisible) {
                    try {
                        if (Build.VERSION.SDK_INT >= 29) {
                            if (this.blendMode != null) {
                                bitmapDrawable.getPaint().setBlendMode((BlendMode) this.blendMode);
                            } else {
                                bitmapDrawable.getPaint().setBlendMode(null);
                            }
                        }
                        bitmapDrawable.setAlpha(alpha);
                        bitmapDrawable.draw(canvas);
                    } catch (Exception e3) {
                        onBitmapException(bitmapDrawable);
                        FileLog.e(e3);
                    }
                }
                canvas.restore();
            } else {
                canvas.save();
                if (orientation % 360 != 0) {
                    if (this.centerRotation) {
                        canvas.rotate(orientation, this.imageW / 2.0f, this.imageH / 2.0f);
                    } else {
                        canvas.rotate(orientation, 0.0f, 0.0f);
                    }
                }
                RectF rectF6 = this.drawRegion;
                float f26 = this.imageX;
                float f27 = this.imageY;
                rectF6.set(f26, f27, this.imageW + f26, this.imageH + f27);
                if (this.isRoundVideo) {
                    this.drawRegion.inset(-AndroidUtilities.roundMessageInset, -AndroidUtilities.roundMessageInset);
                }
                if (bitmapDrawable instanceof AnimatedFileDrawable) {
                    ((AnimatedFileDrawable) bitmapDrawable).setActualDrawRect(this.imageX, this.imageY, this.imageW, this.imageH);
                }
                if (orientation % 360 != 90 && orientation % 360 != 270) {
                    bitmapDrawable.setBounds((int) this.drawRegion.left, (int) this.drawRegion.top, (int) this.drawRegion.right, (int) this.drawRegion.bottom);
                } else {
                    float width2 = this.drawRegion.width() / 2.0f;
                    float height2 = this.drawRegion.height() / 2.0f;
                    float centerX2 = this.drawRegion.centerX();
                    float centerY2 = this.drawRegion.centerY();
                    bitmapDrawable.setBounds((int) (centerX2 - height2), (int) (centerY2 - width2), (int) (centerX2 + height2), (int) (centerY2 + width2));
                }
                if (this.isVisible) {
                    try {
                        if (Build.VERSION.SDK_INT >= 29) {
                            if (this.blendMode != null) {
                                bitmapDrawable.getPaint().setBlendMode((BlendMode) this.blendMode);
                            } else {
                                bitmapDrawable.getPaint().setBlendMode(null);
                            }
                        }
                        bitmapDrawable.setAlpha(alpha);
                        bitmapDrawable.draw(canvas);
                    } catch (Exception e4) {
                        onBitmapException(bitmapDrawable);
                        FileLog.e(e4);
                    }
                }
                canvas.restore();
            }
        } else if (!this.isAspectFit) {
            if (this.legacyCanvas != null) {
                this.roundRect.set(0.0f, 0.0f, this.legacyBitmap.getWidth(), this.legacyBitmap.getHeight());
                this.legacyCanvas.drawBitmap(this.gradientBitmap, (Rect) null, this.roundRect, (Paint) null);
                this.legacyCanvas.drawBitmap(bitmapDrawable.getBitmap(), (Rect) null, this.roundRect, this.legacyPaint);
            }
            if (shader == this.imageShader && this.gradientShader != null) {
                ComposeShader composeShader = this.composeShader;
                if (composeShader == null) {
                    this.roundPaint.setShader(this.legacyShader);
                } else {
                    this.roundPaint.setShader(composeShader);
                }
            } else {
                this.roundPaint.setShader(shader);
            }
            float scale3 = 1.0f / Math.min(scaleW3, scaleH2);
            RectF rectF7 = this.roundRect;
            float f28 = this.imageX;
            float f29 = this.sideClip;
            float f30 = this.imageY;
            rectF7.set(f28 + f29, f30 + f29, (f28 + this.imageW) - f29, (f30 + this.imageH) - f29);
            if (Math.abs(scaleW3 - scaleH2) > 5.0E-4f) {
                if (bitmapW / scaleH2 > realImageW2) {
                    int bitmapW7 = (int) (bitmapW / scaleH2);
                    RectF rectF8 = this.drawRegion;
                    float f31 = this.imageX;
                    float f32 = this.imageY;
                    rectF8.set(f31 - ((bitmapW7 - realImageW2) / 2.0f), f32, f31 + ((bitmapW7 + realImageW2) / 2.0f), f32 + realImageH3);
                } else {
                    int bitmapH6 = (int) (bitmapH / scaleW3);
                    RectF rectF9 = this.drawRegion;
                    float f33 = this.imageX;
                    float f34 = this.imageY;
                    rectF9.set(f33, f34 - ((bitmapH6 - realImageH3) / 2.0f), f33 + realImageW2, f34 + ((bitmapH6 + realImageH3) / 2.0f));
                }
            } else {
                RectF rectF10 = this.drawRegion;
                float f35 = this.imageX;
                float f36 = this.imageY;
                rectF10.set(f35, f36, f35 + realImageW2, f36 + realImageH3);
            }
            if (this.isVisible) {
                this.shaderMatrix.reset();
                this.shaderMatrix.setTranslate((int) (this.drawRegion.left + this.sideClip), (int) (this.drawRegion.top + this.sideClip));
                if (orientation != 90) {
                    if (orientation != 180) {
                        if (orientation == 270) {
                            this.shaderMatrix.preRotate(270.0f);
                            this.shaderMatrix.preTranslate(-this.drawRegion.height(), 0.0f);
                        }
                    } else {
                        this.shaderMatrix.preRotate(180.0f);
                        this.shaderMatrix.preTranslate(-this.drawRegion.width(), -this.drawRegion.height());
                    }
                } else {
                    this.shaderMatrix.preRotate(90.0f);
                    this.shaderMatrix.preTranslate(0.0f, -this.drawRegion.width());
                }
                this.shaderMatrix.preScale(scale3, scale3);
                if (this.isRoundVideo) {
                    float postScale = (realImageW2 + (AndroidUtilities.roundMessageInset * 2)) / realImageW2;
                    this.shaderMatrix.postScale(postScale, postScale, this.drawRegion.centerX(), this.drawRegion.centerY());
                }
                BitmapShader bitmapShader = this.legacyShader;
                if (bitmapShader != null) {
                    bitmapShader.setLocalMatrix(this.shaderMatrix);
                }
                shader.setLocalMatrix(this.shaderMatrix);
                if (this.composeShader != null) {
                    int bitmapW22 = this.gradientBitmap.getWidth();
                    int bitmapH22 = this.gradientBitmap.getHeight();
                    float scaleW22 = this.imageW == 0.0f ? 1.0f : bitmapW22 / realImageW2;
                    float scaleH22 = this.imageH == 0.0f ? 1.0f : bitmapH22 / realImageH3;
                    if (Math.abs(scaleW22 - scaleH22) <= 5.0E-4f) {
                        RectF rectF11 = this.drawRegion;
                        float f37 = this.imageX;
                        float f38 = this.imageY;
                        rectF11.set(f37, f38, f37 + realImageW2, f38 + realImageH3);
                        bitmapW2 = bitmapW22;
                    } else if (bitmapW22 / scaleH22 > realImageW2) {
                        bitmapW2 = (int) (bitmapW22 / scaleH22);
                        RectF rectF12 = this.drawRegion;
                        float f39 = this.imageX;
                        float scale4 = this.imageY;
                        rectF12.set(f39 - ((bitmapW2 - realImageW2) / 2.0f), scale4, f39 + ((bitmapW2 + realImageW2) / 2.0f), scale4 + realImageH3);
                    } else {
                        bitmapH22 = (int) (bitmapH22 / scaleW22);
                        RectF rectF13 = this.drawRegion;
                        float f40 = this.imageX;
                        float f41 = this.imageY;
                        rectF13.set(f40, f41 - ((bitmapH22 - realImageH3) / 2.0f), f40 + realImageW2, f41 + ((bitmapH22 + realImageH3) / 2.0f));
                        bitmapW2 = bitmapW22;
                    }
                    float scale5 = 1.0f / Math.min(this.imageW == 0.0f ? 1.0f : bitmapW2 / realImageW2, this.imageH == 0.0f ? 1.0f : bitmapH22 / realImageH3);
                    this.shaderMatrix.reset();
                    this.shaderMatrix.setTranslate(this.drawRegion.left + this.sideClip, this.drawRegion.top + this.sideClip);
                    this.shaderMatrix.preScale(scale5, scale5);
                    this.gradientShader.setLocalMatrix(this.shaderMatrix);
                }
                this.roundPaint.setAlpha(alpha);
                if (this.isRoundRect) {
                    try {
                        if (this.roundRadius[0] == 0) {
                            canvas.drawRect(this.roundRect, this.roundPaint);
                        } else {
                            canvas.drawRoundRect(this.roundRect, iArr[0], iArr[0], this.roundPaint);
                        }
                    } catch (Exception e5) {
                        onBitmapException(bitmapDrawable);
                        FileLog.e(e5);
                    }
                } else {
                    int a = 0;
                    while (true) {
                        if (a >= this.roundRadius.length) {
                            break;
                        }
                        float[] fArr = radii;
                        fArr[a * 2] = iArr2[a];
                        fArr[(a * 2) + 1] = iArr2[a];
                        a++;
                    }
                    this.roundPath.reset();
                    this.roundPath.addRoundRect(this.roundRect, radii, Path.Direction.CW);
                    this.roundPath.close();
                    canvas.drawPath(this.roundPath, this.roundPaint);
                }
            }
        } else {
            float scale6 = Math.max(scaleW3, scaleH2);
            int bitmapW8 = (int) (bitmapW / scale6);
            int bitmapH7 = (int) (bitmapH / scale6);
            RectF rectF14 = this.drawRegion;
            float f42 = this.imageX;
            float f43 = this.imageW;
            float f44 = this.imageY;
            float f45 = this.imageH;
            float scaleH3 = bitmapH7;
            float scaleW5 = bitmapW8;
            rectF14.set(((f43 - bitmapW8) / 2.0f) + f42, ((f45 - scaleH3) / 2.0f) + f44, f42 + ((f43 + scaleW5) / 2.0f), f44 + ((f45 + bitmapH7) / 2.0f));
            if (this.isVisible) {
                this.shaderMatrix.reset();
                this.shaderMatrix.setTranslate((int) this.drawRegion.left, (int) this.drawRegion.top);
                float f46 = 1.0f / scale6;
                this.shaderMatrix.preScale(1.0f / scale6, 1.0f / scale6);
                shader.setLocalMatrix(this.shaderMatrix);
                this.roundPaint.setShader(shader);
                this.roundPaint.setAlpha(alpha);
                this.roundRect.set(this.drawRegion);
                if (this.isRoundRect) {
                    try {
                        if (this.roundRadius[0] == 0) {
                            canvas.drawRect(this.roundRect, this.roundPaint);
                        } else {
                            canvas.drawRoundRect(this.roundRect, iArr3[0], iArr3[0], this.roundPaint);
                        }
                    } catch (Exception e6) {
                        onBitmapException(bitmapDrawable);
                        FileLog.e(e6);
                    }
                } else {
                    int a2 = 0;
                    while (true) {
                        if (a2 >= this.roundRadius.length) {
                            break;
                        }
                        float[] fArr2 = radii;
                        fArr2[a2 * 2] = iArr4[a2];
                        fArr2[(a2 * 2) + 1] = iArr4[a2];
                        a2++;
                    }
                    this.roundPath.reset();
                    this.roundPath.addRoundRect(this.roundRect, radii, Path.Direction.CW);
                    this.roundPath.close();
                    canvas.drawPath(this.roundPath, this.roundPaint);
                }
            }
        }
    }

    public void setBlendMode(Object mode) {
        this.blendMode = mode;
        invalidate();
    }

    public void setGradientBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            if (this.gradientShader == null || this.gradientBitmap != bitmap) {
                this.gradientShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                updateDrawableRadius(this.currentImageDrawable);
            }
            this.isRoundRect = true;
        } else {
            this.gradientShader = null;
            this.composeShader = null;
            this.legacyShader = null;
            this.legacyCanvas = null;
            Bitmap bitmap2 = this.legacyBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
                this.legacyBitmap = null;
            }
        }
        this.gradientBitmap = bitmap;
    }

    private void onBitmapException(Drawable bitmapDrawable) {
        if (bitmapDrawable == this.currentMediaDrawable && this.currentMediaKey != null) {
            ImageLoader.getInstance().removeImage(this.currentMediaKey);
            this.currentMediaKey = null;
        } else if (bitmapDrawable == this.currentImageDrawable && this.currentImageKey != null) {
            ImageLoader.getInstance().removeImage(this.currentImageKey);
            this.currentImageKey = null;
        } else if (bitmapDrawable == this.currentThumbDrawable && this.currentThumbKey != null) {
            ImageLoader.getInstance().removeImage(this.currentThumbKey);
            this.currentThumbKey = null;
        }
        setImage(this.currentMediaLocation, this.currentMediaFilter, this.currentImageLocation, this.currentImageFilter, this.currentThumbLocation, this.currentThumbFilter, this.currentThumbDrawable, this.currentSize, this.currentExt, this.currentParentObject, this.currentCacheType);
    }

    private void checkAlphaAnimation(boolean skip) {
        if (!this.manualAlphaAnimator && this.currentAlpha != 1.0f) {
            if (!skip) {
                long currentTime = System.currentTimeMillis();
                long dt = currentTime - this.lastUpdateAlphaTime;
                if (dt > 18) {
                    dt = 18;
                }
                float f = this.currentAlpha + (((float) dt) / this.crossfadeDuration);
                this.currentAlpha = f;
                if (f > 1.0f) {
                    this.currentAlpha = 1.0f;
                    this.previousAlpha = 1.0f;
                    if (this.crossfadeImage != null) {
                        recycleBitmap(null, 2);
                        this.crossfadeShader = null;
                    }
                }
            }
            this.lastUpdateAlphaTime = System.currentTimeMillis();
            View view = this.parentView;
            if (view != null) {
                if (this.invalidateAll) {
                    view.invalidate();
                    return;
                }
                float f2 = this.imageX;
                float f3 = this.imageY;
                view.invalidate((int) f2, (int) f3, (int) (f2 + this.imageW), (int) (f3 + this.imageH));
            }
        }
    }

    public void skipDraw() {
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null) {
            lottieDrawable.setCurrentParentView(this.parentView);
            lottieDrawable.updateCurrentFrame();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:122:0x019e A[Catch: Exception -> 0x0238, TryCatch #0 {Exception -> 0x0238, blocks: (B:8:0x0023, B:10:0x0030, B:13:0x0038, B:18:0x0043, B:20:0x004a, B:24:0x0055, B:26:0x0059, B:28:0x005f, B:29:0x0062, B:31:0x0068, B:34:0x006e, B:36:0x007d, B:39:0x0083, B:41:0x0087, B:42:0x0094, B:44:0x0098, B:46:0x009c, B:47:0x00a8, B:49:0x00ae, B:51:0x00b3, B:53:0x00b7, B:54:0x00ba, B:55:0x00c5, B:57:0x00c9, B:61:0x00dd, B:63:0x00e1, B:65:0x00e9, B:67:0x00ed, B:69:0x00f1, B:71:0x00f5, B:73:0x00f9, B:75:0x00fd, B:76:0x0100, B:77:0x0111, B:80:0x0117, B:82:0x012b, B:84:0x0131, B:86:0x0137, B:89:0x013c, B:91:0x0140, B:94:0x0145, B:97:0x014b, B:98:0x0153, B:100:0x0157, B:102:0x015c, B:104:0x0160, B:105:0x0163, B:106:0x016a, B:108:0x016e, B:109:0x0176, B:111:0x017a, B:112:0x0182, B:114:0x0186, B:116:0x018b, B:118:0x018f, B:119:0x0192, B:122:0x019e, B:124:0x01a2, B:127:0x01a7, B:128:0x01b2, B:129:0x01be, B:131:0x01d5, B:133:0x01d9, B:135:0x01de, B:136:0x01f3, B:138:0x0205, B:142:0x020c, B:143:0x0211, B:145:0x0215, B:146:0x0229, B:149:0x0230, B:151:0x0234), top: B:160:0x0023 }] */
    /* JADX WARN: Removed duplicated region for block: B:134:0x01dd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean draw(android.graphics.Canvas r21) {
        /*
            Method dump skipped, instructions count: 584
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.ImageReceiver.draw(android.graphics.Canvas):boolean");
    }

    public void setManualAlphaAnimator(boolean value) {
        this.manualAlphaAnimator = value;
    }

    public float getCurrentAlpha() {
        return this.currentAlpha;
    }

    public void setCurrentAlpha(float value) {
        this.currentAlpha = value;
    }

    public Drawable getDrawable() {
        Drawable drawable = this.currentMediaDrawable;
        if (drawable != null) {
            return drawable;
        }
        Drawable drawable2 = this.currentImageDrawable;
        if (drawable2 != null) {
            return drawable2;
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if (drawable3 != null) {
            return drawable3;
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 != null) {
            return drawable4;
        }
        return null;
    }

    public Bitmap getBitmap() {
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null && lottieDrawable.hasBitmap()) {
            return lottieDrawable.getAnimatedBitmap();
        }
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null && animation.hasBitmap()) {
            return animation.getAnimatedBitmap();
        }
        Drawable drawable = this.currentMediaDrawable;
        if ((drawable instanceof BitmapDrawable) && !(drawable instanceof AnimatedFileDrawable) && !(drawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Drawable drawable2 = this.currentImageDrawable;
        if ((drawable2 instanceof BitmapDrawable) && !(drawable2 instanceof AnimatedFileDrawable) && !(drawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable2).getBitmap();
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if ((drawable3 instanceof BitmapDrawable) && !(drawable3 instanceof AnimatedFileDrawable) && !(drawable instanceof RLottieDrawable)) {
            return ((BitmapDrawable) drawable3).getBitmap();
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable4).getBitmap();
        }
        return null;
    }

    public BitmapHolder getBitmapSafe() {
        Bitmap bitmap = null;
        String key = null;
        AnimatedFileDrawable animation = getAnimation();
        RLottieDrawable lottieDrawable = getLottieAnimation();
        int orientation = 0;
        if (lottieDrawable != null && lottieDrawable.hasBitmap()) {
            bitmap = lottieDrawable.getAnimatedBitmap();
        } else if (animation != null && animation.hasBitmap()) {
            bitmap = animation.getAnimatedBitmap();
            orientation = animation.getOrientation();
            if (orientation != 0) {
                return new BitmapHolder(Bitmap.createBitmap(bitmap), (String) null, orientation);
            }
        } else {
            Drawable drawable = this.currentMediaDrawable;
            if ((drawable instanceof BitmapDrawable) && !(drawable instanceof AnimatedFileDrawable) && !(drawable instanceof RLottieDrawable)) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
                key = this.currentMediaKey;
            } else {
                Drawable drawable2 = this.currentImageDrawable;
                if ((drawable2 instanceof BitmapDrawable) && !(drawable2 instanceof AnimatedFileDrawable) && !(drawable instanceof RLottieDrawable)) {
                    bitmap = ((BitmapDrawable) drawable2).getBitmap();
                    key = this.currentImageKey;
                } else {
                    Drawable drawable3 = this.currentThumbDrawable;
                    if ((drawable3 instanceof BitmapDrawable) && !(drawable3 instanceof AnimatedFileDrawable) && !(drawable instanceof RLottieDrawable)) {
                        bitmap = ((BitmapDrawable) drawable3).getBitmap();
                        key = this.currentThumbKey;
                    } else {
                        Drawable drawable4 = this.staticThumbDrawable;
                        if (drawable4 instanceof BitmapDrawable) {
                            bitmap = ((BitmapDrawable) drawable4).getBitmap();
                        }
                    }
                }
            }
        }
        if (bitmap == null) {
            return null;
        }
        return new BitmapHolder(bitmap, key, orientation);
    }

    public BitmapHolder getDrawableSafe() {
        Drawable drawable = null;
        String key = null;
        Drawable drawable2 = this.currentMediaDrawable;
        if ((drawable2 instanceof BitmapDrawable) && !(drawable2 instanceof AnimatedFileDrawable) && !(drawable2 instanceof RLottieDrawable)) {
            drawable = this.currentMediaDrawable;
            key = this.currentMediaKey;
        } else {
            Drawable drawable3 = this.currentImageDrawable;
            if ((drawable3 instanceof BitmapDrawable) && !(drawable3 instanceof AnimatedFileDrawable) && !(drawable2 instanceof RLottieDrawable)) {
                drawable = this.currentImageDrawable;
                key = this.currentImageKey;
            } else {
                Drawable drawable4 = this.currentThumbDrawable;
                if ((drawable4 instanceof BitmapDrawable) && !(drawable4 instanceof AnimatedFileDrawable) && !(drawable2 instanceof RLottieDrawable)) {
                    drawable = this.currentThumbDrawable;
                    key = this.currentThumbKey;
                } else if (this.staticThumbDrawable instanceof BitmapDrawable) {
                    drawable = this.staticThumbDrawable;
                }
            }
        }
        if (drawable != null) {
            return new BitmapHolder(drawable, key, 0);
        }
        return null;
    }

    public Bitmap getThumbBitmap() {
        Drawable drawable = this.currentThumbDrawable;
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Drawable drawable2 = this.staticThumbDrawable;
        if (drawable2 instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable2).getBitmap();
        }
        return null;
    }

    public BitmapHolder getThumbBitmapSafe() {
        Bitmap bitmap = null;
        String key = null;
        Drawable drawable = this.currentThumbDrawable;
        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            key = this.currentThumbKey;
        } else {
            Drawable drawable2 = this.staticThumbDrawable;
            if (drawable2 instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable2).getBitmap();
            }
        }
        if (bitmap != null) {
            return new BitmapHolder(bitmap, key, 0);
        }
        return null;
    }

    public int getBitmapWidth() {
        getDrawable();
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            int i = this.imageOrientation;
            return (i % 360 == 0 || i % 360 == 180) ? animation.getIntrinsicWidth() : animation.getIntrinsicHeight();
        }
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null) {
            return lottieDrawable.getIntrinsicWidth();
        }
        Bitmap bitmap = getBitmap();
        if (bitmap == null) {
            Drawable drawable = this.staticThumbDrawable;
            if (drawable != null) {
                return drawable.getIntrinsicWidth();
            }
            return 1;
        }
        int i2 = this.imageOrientation;
        return (i2 % 360 == 0 || i2 % 360 == 180) ? bitmap.getWidth() : bitmap.getHeight();
    }

    public int getBitmapHeight() {
        getDrawable();
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            int i = this.imageOrientation;
            return (i % 360 == 0 || i % 360 == 180) ? animation.getIntrinsicHeight() : animation.getIntrinsicWidth();
        }
        RLottieDrawable lottieDrawable = getLottieAnimation();
        if (lottieDrawable != null) {
            return lottieDrawable.getIntrinsicHeight();
        }
        Bitmap bitmap = getBitmap();
        if (bitmap == null) {
            Drawable drawable = this.staticThumbDrawable;
            if (drawable != null) {
                return drawable.getIntrinsicHeight();
            }
            return 1;
        }
        int i2 = this.imageOrientation;
        return (i2 % 360 == 0 || i2 % 360 == 180) ? bitmap.getHeight() : bitmap.getWidth();
    }

    public void setVisible(boolean value, boolean invalidate) {
        if (this.isVisible == value) {
            return;
        }
        this.isVisible = value;
        if (invalidate) {
            invalidate();
        }
    }

    public void invalidate() {
        View view = this.parentView;
        if (view == null) {
            return;
        }
        if (this.invalidateAll) {
            view.invalidate();
            return;
        }
        float f = this.imageX;
        float f2 = this.imageY;
        view.invalidate((int) f, (int) f2, (int) (f + this.imageW), (int) (f2 + this.imageH));
    }

    public void getParentPosition(int[] position) {
        View view = this.parentView;
        if (view == null) {
            return;
        }
        view.getLocationInWindow(position);
    }

    public boolean getVisible() {
        return this.isVisible;
    }

    public void setAlpha(float value) {
        this.overrideAlpha = value;
    }

    public float getAlpha() {
        return this.overrideAlpha;
    }

    public void setCrossfadeAlpha(byte value) {
        this.crossfadeAlpha = value;
    }

    public boolean hasImageSet() {
        return (this.currentImageDrawable == null && this.currentMediaDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentImageKey == null && this.currentMediaKey == null) ? false : true;
    }

    public boolean hasBitmapImage() {
        return (this.currentImageDrawable == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true;
    }

    public boolean hasNotThumb() {
        return (this.currentImageDrawable == null && this.currentMediaDrawable == null) ? false : true;
    }

    public boolean hasStaticThumb() {
        return this.staticThumbDrawable != null;
    }

    public void setAspectFit(boolean value) {
        this.isAspectFit = value;
    }

    public boolean isAspectFit() {
        return this.isAspectFit;
    }

    public void setParentView(View view) {
        this.parentView = view;
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.setParentView(this.parentView);
        }
    }

    public void setImageX(int x) {
        this.imageX = x;
    }

    public void setImageY(float y) {
        this.imageY = y;
    }

    public void setImageWidth(int width) {
        this.imageW = width;
    }

    public void setImageCoords(float x, float y, float width, float height) {
        this.imageX = x;
        this.imageY = y;
        this.imageW = width;
        this.imageH = height;
    }

    public void setImageCoords(Rect bounds) {
        if (bounds != null) {
            this.imageX = bounds.left;
            this.imageY = bounds.top;
            this.imageW = bounds.width();
            this.imageH = bounds.height();
        }
    }

    public void setSideClip(float value) {
        this.sideClip = value;
    }

    public float getCenterX() {
        return this.imageX + (this.imageW / 2.0f);
    }

    public float getCenterY() {
        return this.imageY + (this.imageH / 2.0f);
    }

    public float getImageX() {
        return this.imageX;
    }

    public float getImageX2() {
        return this.imageX + this.imageW;
    }

    public float getImageY() {
        return this.imageY;
    }

    public float getImageY2() {
        return this.imageY + this.imageH;
    }

    public float getImageWidth() {
        return this.imageW;
    }

    public float getImageHeight() {
        return this.imageH;
    }

    public float getImageAspectRatio() {
        float f;
        float f2;
        if (this.imageOrientation % 180 != 0) {
            f2 = this.drawRegion.height();
            f = this.drawRegion.width();
        } else {
            f2 = this.drawRegion.width();
            f = this.drawRegion.height();
        }
        return f2 / f;
    }

    public String getExt() {
        return this.currentExt;
    }

    public boolean isInsideImage(float x, float y) {
        float f = this.imageX;
        if (x >= f && x <= f + this.imageW) {
            float f2 = this.imageY;
            if (y >= f2 && y <= f2 + this.imageH) {
                return true;
            }
        }
        return false;
    }

    public RectF getDrawRegion() {
        return this.drawRegion;
    }

    public int getNewGuid() {
        int i = this.currentGuid + 1;
        this.currentGuid = i;
        return i;
    }

    public String getImageKey() {
        return this.currentImageKey;
    }

    public String getMediaKey() {
        return this.currentMediaKey;
    }

    public String getThumbKey() {
        return this.currentThumbKey;
    }

    public long getSize() {
        return this.currentSize;
    }

    public ImageLocation getMediaLocation() {
        return this.currentMediaLocation;
    }

    public ImageLocation getImageLocation() {
        return this.currentImageLocation;
    }

    public ImageLocation getThumbLocation() {
        return this.currentThumbLocation;
    }

    public String getMediaFilter() {
        return this.currentMediaFilter;
    }

    public String getImageFilter() {
        return this.currentImageFilter;
    }

    public String getThumbFilter() {
        return this.currentThumbFilter;
    }

    public int getCacheType() {
        return this.currentCacheType;
    }

    public void setForcePreview(boolean value) {
        this.forcePreview = value;
    }

    public void setForceCrossfade(boolean value) {
        this.forceCrossfade = value;
    }

    public boolean isForcePreview() {
        return this.forcePreview;
    }

    public void setRoundRadius(int value) {
        setRoundRadius(new int[]{value, value, value, value});
    }

    public void setRoundRadius(int tl, int tr, int br, int bl) {
        setRoundRadius(new int[]{tl, tr, br, bl});
    }

    public void setRoundRadius(int[] value) {
        boolean changed = false;
        int firstValue = value[0];
        this.isRoundRect = true;
        int a = 0;
        while (true) {
            int[] iArr = this.roundRadius;
            if (a >= iArr.length) {
                break;
            }
            if (iArr[a] != value[a]) {
                changed = true;
            }
            if (firstValue != value[a]) {
                this.isRoundRect = false;
            }
            iArr[a] = value[a];
            a++;
        }
        if (changed) {
            Drawable drawable = this.currentImageDrawable;
            if (drawable != null && this.imageShader == null) {
                updateDrawableRadius(drawable);
            }
            Drawable drawable2 = this.currentMediaDrawable;
            if (drawable2 != null && this.mediaShader == null) {
                updateDrawableRadius(drawable2);
            }
            Drawable drawable3 = this.currentThumbDrawable;
            if (drawable3 != null) {
                updateDrawableRadius(drawable3);
                return;
            }
            Drawable drawable4 = this.staticThumbDrawable;
            if (drawable4 != null) {
                updateDrawableRadius(drawable4);
            }
        }
    }

    public void setCurrentAccount(int value) {
        this.currentAccount = value;
    }

    public int[] getRoundRadius() {
        return this.roundRadius;
    }

    public Object getParentObject() {
        return this.currentParentObject;
    }

    public void setNeedsQualityThumb(boolean value) {
        this.needsQualityThumb = value;
    }

    public void setQualityThumbDocument(TLRPC.Document document) {
        this.qulityThumbDocument = document;
    }

    public TLRPC.Document getQualityThumbDocument() {
        return this.qulityThumbDocument;
    }

    public void setCrossfadeWithOldImage(boolean value) {
        this.crossfadeWithOldImage = value;
    }

    public boolean isNeedsQualityThumb() {
        return this.needsQualityThumb;
    }

    public boolean isCurrentKeyQuality() {
        return this.currentKeyQuality;
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    public void setShouldGenerateQualityThumb(boolean value) {
        this.shouldGenerateQualityThumb = value;
    }

    public boolean isShouldGenerateQualityThumb() {
        return this.shouldGenerateQualityThumb;
    }

    public void setAllowStartAnimation(boolean value) {
        this.allowStartAnimation = value;
    }

    public boolean getAllowStartAnimation() {
        return this.allowStartAnimation;
    }

    public void setAllowStartLottieAnimation(boolean value) {
        this.allowStartLottieAnimation = value;
    }

    public void setAllowDecodeSingleFrame(boolean value) {
        this.allowDecodeSingleFrame = value;
    }

    public void setAutoRepeat(int value) {
        this.autoRepeat = value;
        RLottieDrawable drawable = getLottieAnimation();
        if (drawable != null) {
            drawable.setAutoRepeat(value);
        }
    }

    public void setUseSharedAnimationQueue(boolean value) {
        this.useSharedAnimationQueue = value;
    }

    public boolean isAllowStartAnimation() {
        return this.allowStartAnimation;
    }

    public void startAnimation() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.setUseSharedQueue(this.useSharedAnimationQueue);
            animation.start();
            return;
        }
        RLottieDrawable rLottieDrawable = getLottieAnimation();
        if (rLottieDrawable != null && !rLottieDrawable.isRunning()) {
            rLottieDrawable.restart();
        }
    }

    public void stopAnimation() {
        AnimatedFileDrawable animation = getAnimation();
        if (animation != null) {
            animation.stop();
            return;
        }
        RLottieDrawable rLottieDrawable = getLottieAnimation();
        if (rLottieDrawable != null && !rLottieDrawable.isRunning()) {
            rLottieDrawable.stop();
        }
    }

    public boolean isAnimationRunning() {
        AnimatedFileDrawable animation = getAnimation();
        return animation != null && animation.isRunning();
    }

    public AnimatedFileDrawable getAnimation() {
        Drawable drawable = this.currentMediaDrawable;
        if (drawable instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable;
        }
        Drawable drawable2 = this.currentImageDrawable;
        if (drawable2 instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable2;
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if (drawable3 instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable3;
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 instanceof AnimatedFileDrawable) {
            return (AnimatedFileDrawable) drawable4;
        }
        return null;
    }

    public RLottieDrawable getLottieAnimation() {
        Drawable drawable = this.currentMediaDrawable;
        if (drawable instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable;
        }
        Drawable drawable2 = this.currentImageDrawable;
        if (drawable2 instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable2;
        }
        Drawable drawable3 = this.currentThumbDrawable;
        if (drawable3 instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable3;
        }
        Drawable drawable4 = this.staticThumbDrawable;
        if (drawable4 instanceof RLottieDrawable) {
            return (RLottieDrawable) drawable4;
        }
        return null;
    }

    public int getTag(int type) {
        if (type == 1) {
            return this.thumbTag;
        }
        if (type == 3) {
            return this.mediaTag;
        }
        return this.imageTag;
    }

    public void setTag(int value, int type) {
        if (type == 1) {
            this.thumbTag = value;
        } else if (type == 3) {
            this.mediaTag = value;
        } else {
            this.imageTag = value;
        }
    }

    public void setParam(int value) {
        this.param = value;
    }

    public int getParam() {
        return this.param;
    }

    public boolean setImageBitmapByKey(Drawable drawable, String key, int type, boolean memCache, int guid) {
        Drawable drawable2;
        Drawable drawable3;
        if (drawable == null || key == null || this.currentGuid != guid) {
            return false;
        }
        if (type == 0) {
            if (!key.equals(this.currentImageKey)) {
                return false;
            }
            boolean allowCrossFade = true;
            if (!(drawable instanceof AnimatedFileDrawable)) {
                ImageLoader.getInstance().incrementUseCount(this.currentImageKey);
            } else {
                AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) drawable;
                animatedFileDrawable.setStartEndTime(this.startTime, this.endTime);
                if (animatedFileDrawable.isWebmSticker) {
                    ImageLoader.getInstance().incrementUseCount(this.currentImageKey);
                }
                if (this.videoThumbIsSame) {
                    allowCrossFade = !animatedFileDrawable.hasBitmap();
                }
            }
            this.currentImageDrawable = drawable;
            if (drawable instanceof ExtendedBitmapDrawable) {
                this.imageOrientation = ((ExtendedBitmapDrawable) drawable).getOrientation();
            }
            updateDrawableRadius(drawable);
            if (allowCrossFade && this.isVisible && (((!memCache && !this.forcePreview) || this.forceCrossfade) && this.crossfadeDuration != 0)) {
                boolean allowCrossfade = true;
                Drawable drawable4 = this.currentMediaDrawable;
                if ((drawable4 instanceof AnimatedFileDrawable) && ((AnimatedFileDrawable) drawable4).hasBitmap()) {
                    allowCrossfade = false;
                } else if (this.currentImageDrawable instanceof RLottieDrawable) {
                    Drawable drawable5 = this.staticThumbDrawable;
                    allowCrossfade = (drawable5 instanceof LoadingStickerDrawable) || (drawable5 instanceof SvgHelper.SvgDrawable) || (drawable5 instanceof Emoji.EmojiDrawable);
                }
                if (allowCrossfade && ((drawable3 = this.currentThumbDrawable) != null || this.staticThumbDrawable != null || this.forceCrossfade)) {
                    if (drawable3 != null && this.staticThumbDrawable != null) {
                        this.previousAlpha = this.currentAlpha;
                    } else {
                        this.previousAlpha = 1.0f;
                    }
                    this.currentAlpha = 0.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    this.crossfadeWithThumb = (this.crossfadeImage == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true;
                }
            } else {
                this.currentAlpha = 1.0f;
                this.previousAlpha = 1.0f;
            }
        } else if (type == 3) {
            if (!key.equals(this.currentMediaKey)) {
                return false;
            }
            if (!(drawable instanceof AnimatedFileDrawable)) {
                ImageLoader.getInstance().incrementUseCount(this.currentMediaKey);
            } else {
                AnimatedFileDrawable animatedFileDrawable2 = (AnimatedFileDrawable) drawable;
                animatedFileDrawable2.setStartEndTime(this.startTime, this.endTime);
                if (animatedFileDrawable2.isWebmSticker) {
                    ImageLoader.getInstance().incrementUseCount(this.currentMediaKey);
                }
                if (this.videoThumbIsSame) {
                    Drawable drawable6 = this.currentThumbDrawable;
                    if ((drawable6 instanceof AnimatedFileDrawable) || (this.currentImageDrawable instanceof AnimatedFileDrawable)) {
                        long currentTimestamp = 0;
                        if (drawable6 instanceof AnimatedFileDrawable) {
                            currentTimestamp = ((AnimatedFileDrawable) drawable6).getLastFrameTimestamp();
                        } else {
                            Drawable drawable7 = this.currentImageDrawable;
                            if (drawable7 instanceof AnimatedFileDrawable) {
                                currentTimestamp = ((AnimatedFileDrawable) drawable7).getLastFrameTimestamp();
                            }
                        }
                        animatedFileDrawable2.seekTo(currentTimestamp, true, true);
                    }
                }
            }
            this.currentMediaDrawable = drawable;
            updateDrawableRadius(drawable);
            if (this.currentImageDrawable == null) {
                if ((!memCache && !this.forcePreview) || this.forceCrossfade) {
                    Drawable drawable8 = this.currentThumbDrawable;
                    if ((drawable8 == null && this.staticThumbDrawable == null) || this.currentAlpha == 1.0f || this.forceCrossfade) {
                        if (drawable8 != null && this.staticThumbDrawable != null) {
                            this.previousAlpha = this.currentAlpha;
                        } else {
                            this.previousAlpha = 1.0f;
                        }
                        this.currentAlpha = 0.0f;
                        this.lastUpdateAlphaTime = System.currentTimeMillis();
                        this.crossfadeWithThumb = (this.crossfadeImage == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null) ? false : true;
                    }
                } else {
                    this.currentAlpha = 1.0f;
                    this.previousAlpha = 1.0f;
                }
            }
        } else if (type == 1) {
            if (this.currentThumbDrawable != null) {
                return false;
            }
            if (!this.forcePreview) {
                AnimatedFileDrawable animation = getAnimation();
                if (animation != null && animation.hasBitmap()) {
                    return false;
                }
                Drawable drawable9 = this.currentImageDrawable;
                if ((drawable9 != null && !(drawable9 instanceof AnimatedFileDrawable)) || ((drawable2 = this.currentMediaDrawable) != null && !(drawable2 instanceof AnimatedFileDrawable))) {
                    return false;
                }
            }
            if (!key.equals(this.currentThumbKey)) {
                return false;
            }
            ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
            this.currentThumbDrawable = drawable;
            if (drawable instanceof ExtendedBitmapDrawable) {
                this.thumbOrientation = ((ExtendedBitmapDrawable) drawable).getOrientation();
            }
            updateDrawableRadius(drawable);
            if (!memCache && this.crossfadeAlpha != 2) {
                Object obj = this.currentParentObject;
                if ((obj instanceof MessageObject) && ((MessageObject) obj).isRoundVideo() && ((MessageObject) this.currentParentObject).isSending()) {
                    this.currentAlpha = 1.0f;
                    this.previousAlpha = 1.0f;
                } else {
                    this.currentAlpha = 0.0f;
                    this.previousAlpha = 1.0f;
                    this.lastUpdateAlphaTime = System.currentTimeMillis();
                    this.crossfadeWithThumb = this.staticThumbDrawable != null;
                }
            } else {
                this.currentAlpha = 1.0f;
                this.previousAlpha = 1.0f;
            }
        }
        ImageReceiverDelegate imageReceiverDelegate = this.delegate;
        if (imageReceiverDelegate != null) {
            Drawable drawable10 = this.currentImageDrawable;
            imageReceiverDelegate.didSetImage(this, (drawable10 == null && this.currentThumbDrawable == null && this.staticThumbDrawable == null && this.currentMediaDrawable == null) ? false : true, drawable10 == null && this.currentMediaDrawable == null, memCache);
        }
        if (drawable instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable fileDrawable = (AnimatedFileDrawable) drawable;
            fileDrawable.setUseSharedQueue(this.useSharedAnimationQueue);
            if (this.attachedToWindow) {
                fileDrawable.addParent(this);
            }
            if (this.allowStartAnimation && this.currentOpenedLayerFlags == 0) {
                fileDrawable.checkRepeat();
            }
            fileDrawable.setAllowDecodeSingleFrame(this.allowDecodeSingleFrame);
            this.animationReadySent = false;
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
        } else if (drawable instanceof RLottieDrawable) {
            RLottieDrawable fileDrawable2 = (RLottieDrawable) drawable;
            fileDrawable2.addParentView(this.parentView);
            if (this.allowStartLottieAnimation && (!fileDrawable2.isHeavyDrawable() || this.currentOpenedLayerFlags == 0)) {
                fileDrawable2.start();
            }
            fileDrawable2.setAllowDecodeSingleFrame(true);
            fileDrawable2.setAutoRepeat(this.autoRepeat);
            this.animationReadySent = false;
        }
        View view2 = this.parentView;
        if (view2 != null) {
            if (this.invalidateAll) {
                view2.invalidate();
            } else {
                float f = this.imageX;
                float f2 = this.imageY;
                view2.invalidate((int) f, (int) f2, (int) (f + this.imageW), (int) (f2 + this.imageH));
            }
        }
        return true;
    }

    public void setMediaStartEndTime(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        Drawable drawable = this.currentMediaDrawable;
        if (drawable instanceof AnimatedFileDrawable) {
            ((AnimatedFileDrawable) drawable).setStartEndTime(startTime, endTime);
        }
    }

    private void recycleBitmap(String newKey, int type) {
        Drawable image;
        String key;
        String replacedKey;
        if (type == 3) {
            key = this.currentMediaKey;
            image = this.currentMediaDrawable;
        } else if (type == 2) {
            key = this.crossfadeKey;
            image = this.crossfadeImage;
        } else if (type == 1) {
            key = this.currentThumbKey;
            image = this.currentThumbDrawable;
        } else {
            key = this.currentImageKey;
            image = this.currentImageDrawable;
        }
        if (key != null && ((key.startsWith("-") || key.startsWith("strippedmessage-")) && (replacedKey = ImageLoader.getInstance().getReplacedKey(key)) != null)) {
            key = replacedKey;
        }
        if (image instanceof RLottieDrawable) {
            RLottieDrawable lottieDrawable = (RLottieDrawable) image;
            lottieDrawable.removeParentView(this.parentView);
        }
        if (image instanceof AnimatedFileDrawable) {
            AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) image;
            animatedFileDrawable.removeParent(this);
        }
        if (key != null && ((newKey == null || !newKey.equals(key)) && image != null)) {
            if (image instanceof RLottieDrawable) {
                RLottieDrawable fileDrawable = (RLottieDrawable) image;
                boolean canDelete = ImageLoader.getInstance().decrementUseCount(key);
                if (!ImageLoader.getInstance().isInMemCache(key, true) && canDelete) {
                    fileDrawable.recycle();
                }
            } else if (image instanceof AnimatedFileDrawable) {
                AnimatedFileDrawable fileDrawable2 = (AnimatedFileDrawable) image;
                if (fileDrawable2.isWebmSticker) {
                    boolean canDelete2 = ImageLoader.getInstance().decrementUseCount(key);
                    if (!ImageLoader.getInstance().isInMemCache(key, true)) {
                        if (canDelete2) {
                            fileDrawable2.recycle();
                        }
                    } else if (canDelete2) {
                        fileDrawable2.stop();
                    }
                } else if (fileDrawable2.getParents().isEmpty()) {
                    fileDrawable2.recycle();
                }
            } else if (image instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
                boolean canDelete3 = ImageLoader.getInstance().decrementUseCount(key);
                if (!ImageLoader.getInstance().isInMemCache(key, false) && canDelete3) {
                    ArrayList<Bitmap> bitmapToRecycle = new ArrayList<>();
                    bitmapToRecycle.add(bitmap);
                    AndroidUtilities.recycleBitmaps(bitmapToRecycle);
                }
            }
        }
        if (type == 3) {
            this.currentMediaKey = null;
            this.currentMediaDrawable = null;
        } else if (type == 2) {
            this.crossfadeKey = null;
            this.crossfadeImage = null;
        } else if (type == 1) {
            this.currentThumbDrawable = null;
            this.currentThumbKey = null;
        } else {
            this.currentImageDrawable = null;
            this.currentImageKey = null;
        }
    }

    public void setCrossfadeDuration(int duration) {
        this.crossfadeDuration = duration;
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        int i;
        if (id == NotificationCenter.didReplacedPhotoInMemCache) {
            String oldKey = (String) args[0];
            String str = this.currentMediaKey;
            if (str != null && str.equals(oldKey)) {
                this.currentMediaKey = (String) args[1];
                this.currentMediaLocation = (ImageLocation) args[2];
                SetImageBackup setImageBackup = this.setImageBackup;
                if (setImageBackup != null) {
                    setImageBackup.mediaLocation = (ImageLocation) args[2];
                }
            }
            String str2 = this.currentImageKey;
            if (str2 != null && str2.equals(oldKey)) {
                this.currentImageKey = (String) args[1];
                this.currentImageLocation = (ImageLocation) args[2];
                SetImageBackup setImageBackup2 = this.setImageBackup;
                if (setImageBackup2 != null) {
                    setImageBackup2.imageLocation = (ImageLocation) args[2];
                }
            }
            String str3 = this.currentThumbKey;
            if (str3 != null && str3.equals(oldKey)) {
                this.currentThumbKey = (String) args[1];
                this.currentThumbLocation = (ImageLocation) args[2];
                SetImageBackup setImageBackup3 = this.setImageBackup;
                if (setImageBackup3 != null) {
                    setImageBackup3.thumbLocation = (ImageLocation) args[2];
                }
            }
        } else if (id == NotificationCenter.stopAllHeavyOperations) {
            Integer layer = (Integer) args[0];
            if (this.currentLayerNum >= layer.intValue()) {
                return;
            }
            int intValue = this.currentOpenedLayerFlags | layer.intValue();
            this.currentOpenedLayerFlags = intValue;
            if (intValue != 0) {
                RLottieDrawable lottieDrawable = getLottieAnimation();
                if (lottieDrawable != null && lottieDrawable.isHeavyDrawable()) {
                    lottieDrawable.stop();
                }
                AnimatedFileDrawable animatedFileDrawable = getAnimation();
                if (animatedFileDrawable != null) {
                    animatedFileDrawable.stop();
                }
            }
        } else if (id == NotificationCenter.startAllHeavyOperations) {
            Integer layer2 = (Integer) args[0];
            if (this.currentLayerNum >= layer2.intValue() || (i = this.currentOpenedLayerFlags) == 0) {
                return;
            }
            int intValue2 = i & (layer2.intValue() ^ (-1));
            this.currentOpenedLayerFlags = intValue2;
            if (intValue2 == 0) {
                RLottieDrawable lottieDrawable2 = getLottieAnimation();
                if (this.allowStartLottieAnimation && lottieDrawable2 != null && lottieDrawable2.isHeavyDrawable()) {
                    lottieDrawable2.start();
                }
                AnimatedFileDrawable animatedFileDrawable2 = getAnimation();
                if (this.allowStartAnimation && animatedFileDrawable2 != null) {
                    animatedFileDrawable2.checkRepeat();
                    View view = this.parentView;
                    if (view != null) {
                        view.invalidate();
                    }
                }
            }
        }
    }

    public void startCrossfadeFromStaticThumb(Bitmap thumb) {
        this.currentThumbKey = null;
        this.currentThumbDrawable = null;
        this.thumbShader = null;
        this.roundPaint.setShader(null);
        BitmapDrawable bitmapDrawable = new BitmapDrawable((Resources) null, thumb);
        this.staticThumbDrawable = bitmapDrawable;
        this.crossfadeWithThumb = true;
        this.currentAlpha = 0.0f;
        updateDrawableRadius(bitmapDrawable);
    }

    public void setUniqKeyPrefix(String prefix) {
        this.uniqKeyPrefix = prefix;
    }

    public String getUniqKeyPrefix() {
        return this.uniqKeyPrefix;
    }

    public void addLoadingImageRunnable(Runnable loadOperationRunnable) {
        this.loadingOperations.add(loadOperationRunnable);
    }

    public ArrayList<Runnable> getLoadingOperations() {
        return this.loadingOperations;
    }

    public void moveImageToFront() {
        ImageLoader.getInstance().moveToFront(this.currentImageKey);
        ImageLoader.getInstance().moveToFront(this.currentThumbKey);
    }

    public View getParentView() {
        return this.parentView;
    }

    public boolean isAttachedToWindow() {
        return this.attachedToWindow;
    }

    public void setVideoThumbIsSame(boolean b) {
        this.videoThumbIsSame = b;
    }
}
