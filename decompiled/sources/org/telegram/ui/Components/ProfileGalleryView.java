package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.Components.CircularViewPager;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.PinchToZoomHelper;
import org.telegram.ui.ProfileActivity;
/* loaded from: classes5.dex */
public class ProfileGalleryView extends CircularViewPager implements NotificationCenter.NotificationCenterDelegate {
    private ViewPagerAdapter adapter;
    private final Callback callback;
    private TLRPC.ChatFull chatInfo;
    private boolean createThumbFromParent;
    ImageLocation curreantUploadingThumbLocation;
    private int currentAccount;
    ImageLocation currentUploadingImageLocation;
    private long dialogId;
    private final PointF downPoint;
    private boolean forceResetPosition;
    private boolean hasActiveVideo;
    private int imagesLayerNum;
    private ArrayList<ImageLocation> imagesLocations;
    private ArrayList<Integer> imagesLocationsSizes;
    private ArrayList<Float> imagesUploadProgress;
    private boolean invalidateWithParent;
    private boolean isDownReleased;
    private final boolean isProfileFragment;
    private boolean isScrollingListView;
    private boolean isSwipingViewPager;
    private final ActionBar parentActionBar;
    private final int parentClassGuid;
    private final RecyclerListView parentListView;
    private TLRPC.TL_groupCallParticipant participant;
    Path path;
    private ArrayList<TLRPC.Photo> photos;
    PinchToZoomHelper pinchToZoomHelper;
    private ImageLocation prevImageLocation;
    private final SparseArray<RadialProgress2> radialProgresses;
    float[] radii;
    RectF rect;
    private int roundBottomRadius;
    private int roundTopRadius;
    private boolean scrolledByUser;
    private int settingMainPhoto;
    private ArrayList<String> thumbsFileNames;
    private ArrayList<ImageLocation> thumbsLocations;
    private final int touchSlop;
    private ImageLocation uploadingImageLocation;
    private ArrayList<String> videoFileNames;
    private ArrayList<ImageLocation> videoLocations;

    /* loaded from: classes5.dex */
    public interface Callback {
        void onDown(boolean z);

        void onPhotosLoaded();

        void onRelease();

        void onVideoSet();
    }

    public void setHasActiveVideo(boolean hasActiveVideo) {
        this.hasActiveVideo = hasActiveVideo;
    }

    public View findVideoActiveView() {
        if (!this.hasActiveVideo) {
            return null;
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextureStubView) {
                return view;
            }
        }
        return null;
    }

    /* loaded from: classes5.dex */
    public static class Item {
        private AvatarImageView imageView;
        boolean isActiveVideo;
        private View textureViewStubView;

        private Item() {
        }
    }

    public ProfileGalleryView(Context context, ActionBar parentActionBar, RecyclerListView parentListView, Callback callback) {
        super(context);
        this.downPoint = new PointF();
        this.isScrollingListView = true;
        this.isSwipingViewPager = true;
        this.currentAccount = UserConfig.selectedAccount;
        this.path = new Path();
        this.rect = new RectF();
        this.radii = new float[8];
        this.videoFileNames = new ArrayList<>();
        this.thumbsFileNames = new ArrayList<>();
        this.photos = new ArrayList<>();
        this.videoLocations = new ArrayList<>();
        this.imagesLocations = new ArrayList<>();
        this.thumbsLocations = new ArrayList<>();
        this.imagesLocationsSizes = new ArrayList<>();
        this.imagesUploadProgress = new ArrayList<>();
        this.radialProgresses = new SparseArray<>();
        this.createThumbFromParent = true;
        setOffscreenPageLimit(2);
        this.isProfileFragment = false;
        this.parentListView = parentListView;
        this.parentClassGuid = ConnectionsManager.generateClassGuid();
        this.parentActionBar = parentActionBar;
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.callback = callback;
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.Components.ProfileGalleryView.1
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ImageLocation location;
                if (positionOffsetPixels == 0) {
                    int position2 = ProfileGalleryView.this.adapter.getRealPosition(position);
                    if (ProfileGalleryView.this.hasActiveVideo) {
                        position2--;
                    }
                    ProfileGalleryView.this.getCurrentItemView();
                    int count = ProfileGalleryView.this.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = ProfileGalleryView.this.getChildAt(a);
                        if (child instanceof BackupImageView) {
                            int p = ProfileGalleryView.this.adapter.getRealPosition(ProfileGalleryView.this.adapter.imageViews.indexOf(child));
                            if (ProfileGalleryView.this.hasActiveVideo) {
                                p--;
                            }
                            BackupImageView imageView = (BackupImageView) child;
                            ImageReceiver imageReceiver = imageView.getImageReceiver();
                            boolean currentAllow = imageReceiver.getAllowStartAnimation();
                            if (p == position2) {
                                if (!currentAllow) {
                                    imageReceiver.setAllowStartAnimation(true);
                                    imageReceiver.startAnimation();
                                }
                                ImageLocation location2 = (ImageLocation) ProfileGalleryView.this.videoLocations.get(p);
                                if (location2 != null) {
                                    FileLoader.getInstance(ProfileGalleryView.this.currentAccount).setForceStreamLoadingFile(location2.location, "mp4");
                                }
                            } else if (currentAllow) {
                                AnimatedFileDrawable fileDrawable = imageReceiver.getAnimation();
                                if (fileDrawable != null && (location = (ImageLocation) ProfileGalleryView.this.videoLocations.get(p)) != null) {
                                    fileDrawable.seekTo(location.videoSeekTo, false, true);
                                }
                                imageReceiver.setAllowStartAnimation(false);
                                imageReceiver.stopAnimation();
                            }
                        }
                    }
                }
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
            }
        });
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext(), null, parentActionBar);
        this.adapter = viewPagerAdapter;
        setAdapter((CircularViewPager.Adapter) viewPagerAdapter);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadDialogPhotos);
    }

    public void setImagesLayerNum(int value) {
        this.imagesLayerNum = value;
    }

    public ProfileGalleryView(Context context, long dialogId, ActionBar parentActionBar, RecyclerListView parentListView, ProfileActivity.AvatarImageView parentAvatarImageView, int parentClassGuid, Callback callback) {
        super(context);
        this.downPoint = new PointF();
        this.isScrollingListView = true;
        this.isSwipingViewPager = true;
        this.currentAccount = UserConfig.selectedAccount;
        this.path = new Path();
        this.rect = new RectF();
        this.radii = new float[8];
        this.videoFileNames = new ArrayList<>();
        this.thumbsFileNames = new ArrayList<>();
        this.photos = new ArrayList<>();
        this.videoLocations = new ArrayList<>();
        this.imagesLocations = new ArrayList<>();
        this.thumbsLocations = new ArrayList<>();
        this.imagesLocationsSizes = new ArrayList<>();
        this.imagesUploadProgress = new ArrayList<>();
        this.radialProgresses = new SparseArray<>();
        this.createThumbFromParent = true;
        setVisibility(8);
        setOverScrollMode(2);
        setOffscreenPageLimit(2);
        this.isProfileFragment = true;
        this.dialogId = dialogId;
        this.parentListView = parentListView;
        this.parentClassGuid = parentClassGuid;
        this.parentActionBar = parentActionBar;
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext(), parentAvatarImageView, parentActionBar);
        this.adapter = viewPagerAdapter;
        setAdapter((CircularViewPager.Adapter) viewPagerAdapter);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.callback = callback;
        addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: org.telegram.ui.Components.ProfileGalleryView.2
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                ImageLocation location;
                if (positionOffsetPixels == 0) {
                    int position2 = ProfileGalleryView.this.adapter.getRealPosition(position);
                    ProfileGalleryView.this.getCurrentItemView();
                    int count = ProfileGalleryView.this.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = ProfileGalleryView.this.getChildAt(a);
                        if (child instanceof BackupImageView) {
                            int p = ProfileGalleryView.this.adapter.getRealPosition(ProfileGalleryView.this.adapter.imageViews.indexOf(child));
                            BackupImageView imageView = (BackupImageView) child;
                            ImageReceiver imageReceiver = imageView.getImageReceiver();
                            boolean currentAllow = imageReceiver.getAllowStartAnimation();
                            if (p == position2) {
                                if (!currentAllow) {
                                    imageReceiver.setAllowStartAnimation(true);
                                    imageReceiver.startAnimation();
                                }
                                ImageLocation location2 = (ImageLocation) ProfileGalleryView.this.videoLocations.get(p);
                                if (location2 != null) {
                                    FileLoader.getInstance(ProfileGalleryView.this.currentAccount).setForceStreamLoadingFile(location2.location, "mp4");
                                }
                            } else if (currentAllow) {
                                AnimatedFileDrawable fileDrawable = imageReceiver.getAnimation();
                                if (fileDrawable != null && (location = (ImageLocation) ProfileGalleryView.this.videoLocations.get(p)) != null) {
                                    fileDrawable.seekTo(location.videoSeekTo, false, true);
                                }
                                imageReceiver.setAllowStartAnimation(false);
                                imageReceiver.stopAnimation();
                            }
                        }
                    }
                }
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
            }
        });
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadDialogPhotos);
        MessagesController.getInstance(this.currentAccount).loadDialogPhotos(dialogId, 80, 0, true, parentClassGuid);
    }

    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogPhotosLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.reloadDialogPhotos);
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View child = getChildAt(a);
            if (child instanceof BackupImageView) {
                BackupImageView imageView = (BackupImageView) child;
                if (imageView.getImageReceiver().hasStaticThumb()) {
                    Drawable drawable = imageView.getImageReceiver().getDrawable();
                    if (drawable instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) drawable).removeSecondParentView(imageView);
                    }
                }
            }
        }
    }

    public void setAnimatedFileMaybe(AnimatedFileDrawable drawable) {
        if (drawable == null || this.adapter == null) {
            return;
        }
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View child = getChildAt(a);
            if (child instanceof BackupImageView) {
                ViewPagerAdapter viewPagerAdapter = this.adapter;
                int p = viewPagerAdapter.getRealPosition(viewPagerAdapter.imageViews.indexOf(child));
                if (p == 0) {
                    BackupImageView imageView = (BackupImageView) child;
                    ImageReceiver imageReceiver = imageView.getImageReceiver();
                    AnimatedFileDrawable currentDrawable = imageReceiver.getAnimation();
                    if (currentDrawable != drawable) {
                        if (currentDrawable != null) {
                            currentDrawable.removeSecondParentView(imageView);
                        }
                        imageView.setImageDrawable(drawable);
                        drawable.addSecondParentView(this);
                        drawable.setInvalidateParentViewWithSecond(true);
                    }
                }
            }
        }
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int currentItem;
        if (this.adapter == null) {
            return false;
        }
        if (this.parentListView.getScrollState() != 0 && !this.isScrollingListView && this.isSwipingViewPager) {
            this.isSwipingViewPager = false;
            MotionEvent cancelEvent = MotionEvent.obtain(ev);
            cancelEvent.setAction(3);
            super.onTouchEvent(cancelEvent);
            cancelEvent.recycle();
            return false;
        }
        int action = ev.getAction();
        if (this.pinchToZoomHelper != null && getCurrentItemView() != null) {
            if (action == 0 || !this.isDownReleased || this.pinchToZoomHelper.isInOverlayMode()) {
                if (this.pinchToZoomHelper.checkPinchToZoom(ev, this, getCurrentItemView().getImageReceiver(), null)) {
                    if (!this.isDownReleased) {
                        this.isDownReleased = true;
                        this.callback.onRelease();
                    }
                    return true;
                }
            } else {
                this.pinchToZoomHelper.checkPinchToZoom(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0), this, getCurrentItemView().getImageReceiver(), null);
            }
        }
        if (action == 0) {
            this.isScrollingListView = true;
            this.isSwipingViewPager = true;
            this.scrolledByUser = true;
            this.downPoint.set(ev.getX(), ev.getY());
            if (this.adapter.getCount() > 1) {
                this.callback.onDown(ev.getX() < ((float) getWidth()) / 3.0f);
            }
            this.isDownReleased = false;
        } else if (action == 1) {
            if (!this.isDownReleased) {
                int itemsCount = this.adapter.getCount();
                int currentItem2 = getCurrentItem();
                if (itemsCount > 1) {
                    if (ev.getX() > getWidth() / 3.0f) {
                        int extraCount = this.adapter.getExtraCount();
                        currentItem = currentItem2 + 1;
                        if (currentItem >= itemsCount - extraCount) {
                            currentItem = extraCount;
                        }
                    } else {
                        int extraCount2 = this.adapter.getExtraCount();
                        currentItem = currentItem2 - 1;
                        if (currentItem < extraCount2) {
                            currentItem = (itemsCount - extraCount2) - 1;
                        }
                    }
                    this.callback.onRelease();
                    setCurrentItem(currentItem, false);
                }
            }
        } else if (action == 2) {
            float dx = ev.getX() - this.downPoint.x;
            float dy = ev.getY() - this.downPoint.y;
            boolean move = Math.abs(dy) >= ((float) this.touchSlop) || Math.abs(dx) >= ((float) this.touchSlop);
            if (move) {
                this.isDownReleased = true;
                this.callback.onRelease();
            }
            boolean z = this.isSwipingViewPager;
            if (z && this.isScrollingListView) {
                if (move) {
                    if (Math.abs(dy) > Math.abs(dx)) {
                        this.isSwipingViewPager = false;
                        MotionEvent cancelEvent2 = MotionEvent.obtain(ev);
                        cancelEvent2.setAction(3);
                        super.onTouchEvent(cancelEvent2);
                        cancelEvent2.recycle();
                    } else {
                        this.isScrollingListView = false;
                        MotionEvent cancelEvent3 = MotionEvent.obtain(ev);
                        cancelEvent3.setAction(3);
                        this.parentListView.onTouchEvent(cancelEvent3);
                        cancelEvent3.recycle();
                    }
                }
            } else if (z && !canScrollHorizontally(-1) && dx > this.touchSlop) {
                return false;
            }
        }
        boolean result = false;
        if (this.isScrollingListView) {
            result = this.parentListView.onTouchEvent(ev);
        }
        if (this.isSwipingViewPager) {
            result |= super.onTouchEvent(ev);
        }
        if (action == 1 || action == 3) {
            this.isScrollingListView = false;
            this.isSwipingViewPager = false;
        }
        return result;
    }

    public void setChatInfo(TLRPC.ChatFull chatFull) {
        this.chatInfo = chatFull;
        if (!this.photos.isEmpty() && this.photos.get(0) == null && this.chatInfo != null && FileLoader.isSamePhoto((TLRPC.FileLocation) this.imagesLocations.get(0).location, this.chatInfo.chat_photo)) {
            this.photos.set(0, this.chatInfo.chat_photo);
            if (!this.chatInfo.chat_photo.video_sizes.isEmpty()) {
                TLRPC.VideoSize videoSize = this.chatInfo.chat_photo.video_sizes.get(0);
                this.videoLocations.set(0, ImageLocation.getForPhoto(videoSize, this.chatInfo.chat_photo));
                this.videoFileNames.set(0, FileLoader.getAttachFileName(videoSize));
                this.callback.onPhotosLoaded();
            } else {
                this.videoLocations.set(0, null);
                this.videoFileNames.add(0, null);
            }
            this.imagesUploadProgress.set(0, null);
            this.adapter.notifyDataSetChanged();
        }
    }

    public boolean initIfEmpty(ImageLocation imageLocation, ImageLocation thumbLocation, boolean reload) {
        if (imageLocation == null || thumbLocation == null || this.settingMainPhoto != 0) {
            return false;
        }
        ImageLocation imageLocation2 = this.prevImageLocation;
        if (imageLocation2 == null || imageLocation2.location.local_id != imageLocation.location.local_id) {
            if (!this.imagesLocations.isEmpty()) {
                this.prevImageLocation = imageLocation;
                if (reload) {
                    MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.dialogId, 80, 0, true, this.parentClassGuid);
                }
                return true;
            } else if (reload) {
                MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.dialogId, 80, 0, true, this.parentClassGuid);
            }
        }
        if (!this.imagesLocations.isEmpty()) {
            return false;
        }
        this.prevImageLocation = imageLocation;
        this.thumbsFileNames.add(null);
        this.videoFileNames.add(null);
        this.imagesLocations.add(imageLocation);
        this.thumbsLocations.add(thumbLocation);
        this.videoLocations.add(null);
        this.photos.add(null);
        this.imagesLocationsSizes.add(-1);
        this.imagesUploadProgress.add(null);
        getAdapter().notifyDataSetChanged();
        return true;
    }

    public void addUploadingImage(ImageLocation imageLocation, ImageLocation thumbLocation) {
        this.prevImageLocation = imageLocation;
        this.thumbsFileNames.add(0, null);
        this.videoFileNames.add(0, null);
        this.imagesLocations.add(0, imageLocation);
        this.thumbsLocations.add(0, thumbLocation);
        this.videoLocations.add(0, null);
        this.photos.add(0, null);
        this.imagesLocationsSizes.add(0, -1);
        this.imagesUploadProgress.add(0, Float.valueOf(0.0f));
        this.adapter.notifyDataSetChanged();
        resetCurrentItem();
        this.currentUploadingImageLocation = imageLocation;
        this.curreantUploadingThumbLocation = thumbLocation;
    }

    public void removeUploadingImage(ImageLocation imageLocation) {
        this.uploadingImageLocation = imageLocation;
        this.currentUploadingImageLocation = null;
        this.curreantUploadingThumbLocation = null;
    }

    public ImageLocation getImageLocation(int index) {
        if (index < 0 || index >= this.imagesLocations.size()) {
            return null;
        }
        ImageLocation location = this.videoLocations.get(index);
        if (location != null) {
            return location;
        }
        return this.imagesLocations.get(index);
    }

    public ImageLocation getRealImageLocation(int index) {
        if (index < 0 || index >= this.imagesLocations.size()) {
            return null;
        }
        return this.imagesLocations.get(index);
    }

    public ImageLocation getThumbLocation(int index) {
        if (index < 0 || index >= this.thumbsLocations.size()) {
            return null;
        }
        return this.thumbsLocations.get(index);
    }

    public boolean hasImages() {
        return !this.imagesLocations.isEmpty();
    }

    public BackupImageView getCurrentItemView() {
        ViewPagerAdapter viewPagerAdapter = this.adapter;
        if (viewPagerAdapter != null && !viewPagerAdapter.objects.isEmpty()) {
            return ((Item) this.adapter.objects.get(getCurrentItem())).imageView;
        }
        return null;
    }

    public boolean isLoadingCurrentVideo() {
        BackupImageView imageView;
        if (this.videoLocations.get(this.hasActiveVideo ? getRealPosition() - 1 : getRealPosition()) == null || (imageView = getCurrentItemView()) == null) {
            return false;
        }
        AnimatedFileDrawable drawable = imageView.getImageReceiver().getAnimation();
        return drawable == null || !drawable.hasBitmap();
    }

    public float getCurrentItemProgress() {
        AnimatedFileDrawable drawable;
        BackupImageView imageView = getCurrentItemView();
        if (imageView == null || (drawable = imageView.getImageReceiver().getAnimation()) == null) {
            return 0.0f;
        }
        return drawable.getCurrentProgress();
    }

    public boolean isCurrentItemVideo() {
        int i = getRealPosition();
        if (this.hasActiveVideo) {
            if (i == 0) {
                return false;
            }
            i--;
        }
        return this.videoLocations.get(i) != null;
    }

    public ImageLocation getCurrentVideoLocation(ImageLocation thumbLocation, ImageLocation imageLocation) {
        if (thumbLocation == null) {
            return null;
        }
        int b = 0;
        while (b < 2) {
            ArrayList<ImageLocation> arrayList = b == 0 ? this.thumbsLocations : this.imagesLocations;
            int N = arrayList.size();
            for (int a = 0; a < N; a++) {
                ImageLocation loc = arrayList.get(a);
                if (loc != null && loc.location != null && ((loc.dc_id == thumbLocation.dc_id && loc.location.local_id == thumbLocation.location.local_id && loc.location.volume_id == thumbLocation.location.volume_id) || (loc.dc_id == imageLocation.dc_id && loc.location.local_id == imageLocation.location.local_id && loc.location.volume_id == imageLocation.location.volume_id))) {
                    return this.videoLocations.get(a);
                }
            }
            b++;
        }
        return null;
    }

    public void resetCurrentItem() {
        setCurrentItem(this.adapter.getExtraCount(), false);
    }

    public int getRealCount() {
        int size = this.photos.size();
        if (this.hasActiveVideo) {
            return size + 1;
        }
        return size;
    }

    public int getRealPosition(int position) {
        return this.adapter.getRealPosition(position);
    }

    public int getRealPosition() {
        return this.adapter.getRealPosition(getCurrentItem());
    }

    public TLRPC.Photo getPhoto(int index) {
        if (index < 0 || index >= this.photos.size()) {
            return null;
        }
        return this.photos.get(index);
    }

    public void replaceFirstPhoto(TLRPC.Photo oldPhoto, TLRPC.Photo photo) {
        int idx;
        if (this.photos.isEmpty() || (idx = this.photos.indexOf(oldPhoto)) < 0) {
            return;
        }
        this.photos.set(idx, photo);
    }

    public void finishSettingMainPhoto() {
        this.settingMainPhoto--;
    }

    public void startMovePhotoToBegin(int index) {
        if (index <= 0 || index >= this.photos.size()) {
            return;
        }
        this.settingMainPhoto++;
        TLRPC.Photo photo = this.photos.get(index);
        this.photos.remove(index);
        this.photos.add(0, photo);
        String name = this.thumbsFileNames.get(index);
        this.thumbsFileNames.remove(index);
        this.thumbsFileNames.add(0, name);
        ArrayList<String> arrayList = this.videoFileNames;
        arrayList.add(0, arrayList.remove(index));
        ImageLocation location = this.videoLocations.get(index);
        this.videoLocations.remove(index);
        this.videoLocations.add(0, location);
        ImageLocation location2 = this.imagesLocations.get(index);
        this.imagesLocations.remove(index);
        this.imagesLocations.add(0, location2);
        ImageLocation location3 = this.thumbsLocations.get(index);
        this.thumbsLocations.remove(index);
        this.thumbsLocations.add(0, location3);
        Integer size = this.imagesLocationsSizes.get(index);
        this.imagesLocationsSizes.remove(index);
        this.imagesLocationsSizes.add(0, size);
        Float uploadProgress = this.imagesUploadProgress.get(index);
        this.imagesUploadProgress.remove(index);
        this.imagesUploadProgress.add(0, uploadProgress);
        this.prevImageLocation = this.imagesLocations.get(0);
    }

    public void commitMoveToBegin() {
        this.adapter.notifyDataSetChanged();
        resetCurrentItem();
    }

    public boolean removePhotoAtIndex(int index) {
        if (index < 0 || index >= this.photos.size()) {
            return false;
        }
        this.photos.remove(index);
        this.thumbsFileNames.remove(index);
        this.videoFileNames.remove(index);
        this.videoLocations.remove(index);
        this.imagesLocations.remove(index);
        this.thumbsLocations.remove(index);
        this.imagesLocationsSizes.remove(index);
        this.radialProgresses.delete(index);
        this.imagesUploadProgress.remove(index);
        if (index == 0 && !this.imagesLocations.isEmpty()) {
            this.prevImageLocation = this.imagesLocations.get(0);
        }
        this.adapter.notifyDataSetChanged();
        return this.photos.isEmpty();
    }

    @Override // androidx.viewpager.widget.ViewPager, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (this.parentListView.getScrollState() != 0) {
            return false;
        }
        if (getParent() != null && getParent().getParent() != null) {
            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
        }
        return super.onInterceptTouchEvent(e);
    }

    private void loadNeighboringThumbs() {
        int locationsCount = this.thumbsLocations.size();
        if (locationsCount > 1) {
            int i = 0;
            while (true) {
                int i2 = 2;
                if (locationsCount <= 2) {
                    i2 = 1;
                }
                if (i < i2) {
                    int pos = i == 0 ? 1 : locationsCount - 1;
                    FileLoader.getInstance(this.currentAccount).loadFile(this.thumbsLocations.get(pos), null, null, 0, 1);
                    i++;
                } else {
                    return;
                }
            }
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        RadialProgress2 radialProgress;
        RadialProgress2 radialProgress2;
        ImageLocation currentImageLocation;
        ArrayList<TLRPC.Photo> arrayList;
        int guid;
        int N;
        if (id != NotificationCenter.dialogPhotosLoaded) {
            if (id == NotificationCenter.fileLoaded) {
                String fileName = (String) args[0];
                for (int i = 0; i < this.thumbsFileNames.size(); i++) {
                    String fileName2 = this.videoFileNames.get(i);
                    if (fileName2 == null) {
                        fileName2 = this.thumbsFileNames.get(i);
                    }
                    if (fileName2 != null && TextUtils.equals(fileName, fileName2) && (radialProgress2 = this.radialProgresses.get(i)) != null) {
                        radialProgress2.setProgress(1.0f, true);
                    }
                }
                return;
            } else if (id == NotificationCenter.fileLoadProgressChanged) {
                String fileName3 = (String) args[0];
                for (int i2 = 0; i2 < this.thumbsFileNames.size(); i2++) {
                    String fileName22 = this.videoFileNames.get(i2);
                    if (fileName22 == null) {
                        fileName22 = this.thumbsFileNames.get(i2);
                    }
                    if (fileName22 != null && TextUtils.equals(fileName3, fileName22) && (radialProgress = this.radialProgresses.get(i2)) != null) {
                        Long loadedSize = (Long) args[1];
                        Long totalSize = (Long) args[2];
                        float progress = Math.min(1.0f, ((float) loadedSize.longValue()) / ((float) totalSize.longValue()));
                        radialProgress.setProgress(progress, true);
                    }
                }
                return;
            } else if (id == NotificationCenter.reloadDialogPhotos && this.settingMainPhoto == 0) {
                MessagesController.getInstance(this.currentAccount).loadDialogPhotos(this.dialogId, 80, 0, true, this.parentClassGuid);
                return;
            } else {
                return;
            }
        }
        int guid2 = ((Integer) args[3]).intValue();
        long did = ((Long) args[0]).longValue();
        if (did == this.dialogId && this.parentClassGuid == guid2 && this.adapter != null) {
            boolean fromCache = ((Boolean) args[2]).booleanValue();
            ArrayList<TLRPC.Photo> arrayList2 = (ArrayList) args[4];
            this.thumbsFileNames.clear();
            this.videoFileNames.clear();
            this.imagesLocations.clear();
            this.videoLocations.clear();
            this.thumbsLocations.clear();
            this.photos.clear();
            this.imagesLocationsSizes.clear();
            this.imagesUploadProgress.clear();
            if (!DialogObject.isChatDialog(did)) {
                currentImageLocation = null;
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-did));
                ImageLocation currentImageLocation2 = ImageLocation.getForUserOrChat(chat, 0);
                if (currentImageLocation2 != null) {
                    this.imagesLocations.add(currentImageLocation2);
                    this.thumbsLocations.add(ImageLocation.getForUserOrChat(chat, 1));
                    this.thumbsFileNames.add(null);
                    if (this.chatInfo != null && FileLoader.isSamePhoto((TLRPC.FileLocation) currentImageLocation2.location, this.chatInfo.chat_photo)) {
                        this.photos.add(this.chatInfo.chat_photo);
                        if (!this.chatInfo.chat_photo.video_sizes.isEmpty()) {
                            TLRPC.VideoSize videoSize = this.chatInfo.chat_photo.video_sizes.get(0);
                            this.videoLocations.add(ImageLocation.getForPhoto(videoSize, this.chatInfo.chat_photo));
                            this.videoFileNames.add(FileLoader.getAttachFileName(videoSize));
                        } else {
                            this.videoLocations.add(null);
                            this.videoFileNames.add(null);
                        }
                    } else {
                        this.photos.add(null);
                        this.videoFileNames.add(null);
                        this.videoLocations.add(null);
                    }
                    this.imagesLocationsSizes.add(-1);
                    this.imagesUploadProgress.add(null);
                }
                currentImageLocation = currentImageLocation2;
            }
            int a = 0;
            while (a < arrayList2.size()) {
                TLRPC.Photo photo = arrayList2.get(a);
                if (photo == null || (photo instanceof TLRPC.TL_photoEmpty)) {
                    guid = guid2;
                    arrayList = arrayList2;
                } else if (photo.sizes == null) {
                    guid = guid2;
                    arrayList = arrayList2;
                } else {
                    TLRPC.PhotoSize sizeThumb = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 50);
                    int b = 0;
                    int N2 = photo.sizes.size();
                    while (true) {
                        if (b >= N2) {
                            break;
                        }
                        TLRPC.PhotoSize photoSize = photo.sizes.get(b);
                        if (!(photoSize instanceof TLRPC.TL_photoStrippedSize)) {
                            b++;
                        } else {
                            sizeThumb = photoSize;
                            break;
                        }
                    }
                    if (currentImageLocation == null) {
                        guid = guid2;
                        arrayList = arrayList2;
                    } else {
                        boolean cont = false;
                        int b2 = 0;
                        int N3 = photo.sizes.size();
                        while (true) {
                            if (b2 >= N3) {
                                guid = guid2;
                                arrayList = arrayList2;
                                break;
                            }
                            TLRPC.PhotoSize size = photo.sizes.get(b2);
                            if (size.location != null) {
                                guid = guid2;
                                if (size.location.local_id == currentImageLocation.location.local_id) {
                                    arrayList = arrayList2;
                                    N = N3;
                                    if (size.location.volume_id == currentImageLocation.location.volume_id) {
                                        this.photos.set(0, photo);
                                        if (!photo.video_sizes.isEmpty()) {
                                            this.videoLocations.set(0, ImageLocation.getForPhoto(photo.video_sizes.get(0), photo));
                                        }
                                        cont = true;
                                    }
                                } else {
                                    arrayList = arrayList2;
                                    N = N3;
                                }
                            } else {
                                guid = guid2;
                                arrayList = arrayList2;
                                N = N3;
                            }
                            b2++;
                            guid2 = guid;
                            arrayList2 = arrayList;
                            N3 = N;
                        }
                        if (cont) {
                        }
                    }
                    TLRPC.PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 640);
                    if (sizeFull != null) {
                        if (photo.dc_id != 0) {
                            sizeFull.location.dc_id = photo.dc_id;
                            sizeFull.location.file_reference = photo.file_reference;
                        }
                        ImageLocation location = ImageLocation.getForPhoto(sizeFull, photo);
                        if (location != null) {
                            this.imagesLocations.add(location);
                            this.thumbsFileNames.add(FileLoader.getAttachFileName(sizeThumb instanceof TLRPC.TL_photoStrippedSize ? sizeFull : sizeThumb));
                            this.thumbsLocations.add(ImageLocation.getForPhoto(sizeThumb, photo));
                            if (!photo.video_sizes.isEmpty()) {
                                TLRPC.VideoSize videoSize2 = photo.video_sizes.get(0);
                                this.videoLocations.add(ImageLocation.getForPhoto(videoSize2, photo));
                                this.videoFileNames.add(FileLoader.getAttachFileName(videoSize2));
                            } else {
                                this.videoLocations.add(null);
                                this.videoFileNames.add(null);
                            }
                            this.photos.add(photo);
                            this.imagesLocationsSizes.add(Integer.valueOf(sizeFull.size));
                            this.imagesUploadProgress.add(null);
                        }
                    }
                }
                a++;
                guid2 = guid;
                arrayList2 = arrayList;
            }
            loadNeighboringThumbs();
            getAdapter().notifyDataSetChanged();
            if (this.isProfileFragment) {
                if (!this.scrolledByUser || this.forceResetPosition) {
                    resetCurrentItem();
                }
            } else if (!this.scrolledByUser || this.forceResetPosition) {
                resetCurrentItem();
                getAdapter().notifyDataSetChanged();
            }
            this.forceResetPosition = false;
            if (fromCache) {
                MessagesController.getInstance(this.currentAccount).loadDialogPhotos(did, 80, 0, false, this.parentClassGuid);
            }
            Callback callback = this.callback;
            if (callback != null) {
                callback.onPhotosLoaded();
            }
            ImageLocation imageLocation = this.currentUploadingImageLocation;
            if (imageLocation != null) {
                addUploadingImage(imageLocation, this.curreantUploadingThumbLocation);
            }
        }
    }

    /* loaded from: classes5.dex */
    public class ViewPagerAdapter extends CircularViewPager.Adapter {
        private final Context context;
        private final ActionBar parentActionBar;
        private BackupImageView parentAvatarImageView;
        private final Paint placeholderPaint;
        private final ArrayList<Item> objects = new ArrayList<>();
        private final ArrayList<BackupImageView> imageViews = new ArrayList<>();

        public ViewPagerAdapter(Context context, ProfileActivity.AvatarImageView parentAvatarImageView, ActionBar parentActionBar) {
            ProfileGalleryView.this = this$0;
            this.context = context;
            this.parentAvatarImageView = parentAvatarImageView;
            this.parentActionBar = parentActionBar;
            Paint paint = new Paint(1);
            this.placeholderPaint = paint;
            paint.setColor(-16777216);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return this.objects.size();
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object object) {
            Item item = (Item) object;
            return item.isActiveVideo ? view == item.textureViewStubView : view == item.imageView;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getItemPosition(Object object) {
            int idx = this.objects.indexOf((Item) object);
            if (idx == -1) {
                return -2;
            }
            return idx;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Item instantiateItem(ViewGroup container, int position) {
            String filter;
            Item item = this.objects.get(position);
            int realPosition = getRealPosition(position);
            boolean z = true;
            if (ProfileGalleryView.this.hasActiveVideo && realPosition == 0) {
                item.isActiveVideo = true;
                if (item.textureViewStubView == null) {
                    item.textureViewStubView = new TextureStubView(this.context);
                }
                if (item.textureViewStubView.getParent() == null) {
                    container.addView(item.textureViewStubView);
                }
                return item;
            }
            item.isActiveVideo = false;
            if (item.textureViewStubView != null && item.textureViewStubView.getParent() != null) {
                container.removeView(item.textureViewStubView);
            }
            if (item.imageView == null) {
                item.imageView = new AvatarImageView(this.context, position, this.placeholderPaint);
                this.imageViews.set(position, item.imageView);
            }
            if (item.imageView.getParent() == null) {
                container.addView(item.imageView);
            }
            item.imageView.getImageReceiver().setAllowDecodeSingleFrame(true);
            int imageLocationPosition = ProfileGalleryView.this.hasActiveVideo ? realPosition - 1 : realPosition;
            boolean needProgress = false;
            if (imageLocationPosition != 0) {
                ImageLocation videoLocation = (ImageLocation) ProfileGalleryView.this.videoLocations.get(imageLocationPosition);
                AvatarImageView avatarImageView = item.imageView;
                if (videoLocation == null) {
                    z = false;
                }
                avatarImageView.isVideo = z;
                needProgress = true;
                ImageLocation location = (ImageLocation) ProfileGalleryView.this.thumbsLocations.get(imageLocationPosition);
                String filter2 = location.photoSize instanceof TLRPC.TL_photoStrippedSize ? "b" : null;
                item.imageView.setImageMedia(videoLocation, null, (ImageLocation) ProfileGalleryView.this.imagesLocations.get(imageLocationPosition), null, (ImageLocation) ProfileGalleryView.this.thumbsLocations.get(imageLocationPosition), filter2, null, ((Integer) ProfileGalleryView.this.imagesLocationsSizes.get(imageLocationPosition)).intValue(), 1, "avatar_" + ProfileGalleryView.this.dialogId);
            } else {
                BackupImageView backupImageView = this.parentAvatarImageView;
                Drawable drawable = backupImageView == null ? null : backupImageView.getImageReceiver().getDrawable();
                if (!(drawable instanceof AnimatedFileDrawable) || !((AnimatedFileDrawable) drawable).hasBitmap()) {
                    ImageLocation videoLocation2 = (ImageLocation) ProfileGalleryView.this.videoLocations.get(imageLocationPosition);
                    AvatarImageView avatarImageView2 = item.imageView;
                    if (videoLocation2 == null) {
                        z = false;
                    }
                    avatarImageView2.isVideo = z;
                    needProgress = true;
                    if (ProfileGalleryView.this.isProfileFragment && videoLocation2 != null && videoLocation2.imageType == 2) {
                        filter = "avatar";
                    } else {
                        filter = null;
                    }
                    ImageLocation location2 = (ImageLocation) ProfileGalleryView.this.thumbsLocations.get(imageLocationPosition);
                    Bitmap thumb = (this.parentAvatarImageView == null || !ProfileGalleryView.this.createThumbFromParent) ? null : this.parentAvatarImageView.getImageReceiver().getBitmap();
                    StringBuilder sb = new StringBuilder();
                    sb.append("avatar_");
                    String filter3 = filter;
                    sb.append(ProfileGalleryView.this.dialogId);
                    String parent = sb.toString();
                    if (thumb != null) {
                        item.imageView.setImageMedia((ImageLocation) ProfileGalleryView.this.videoLocations.get(imageLocationPosition), filter3, (ImageLocation) ProfileGalleryView.this.imagesLocations.get(imageLocationPosition), null, thumb, ((Integer) ProfileGalleryView.this.imagesLocationsSizes.get(imageLocationPosition)).intValue(), 1, parent);
                    } else if (ProfileGalleryView.this.uploadingImageLocation != null) {
                        item.imageView.setImageMedia((ImageLocation) ProfileGalleryView.this.videoLocations.get(imageLocationPosition), filter3, (ImageLocation) ProfileGalleryView.this.imagesLocations.get(imageLocationPosition), null, ProfileGalleryView.this.uploadingImageLocation, null, null, ((Integer) ProfileGalleryView.this.imagesLocationsSizes.get(imageLocationPosition)).intValue(), 1, parent);
                    } else {
                        String thumbFilter = location2.photoSize instanceof TLRPC.TL_photoStrippedSize ? "b" : null;
                        item.imageView.setImageMedia(videoLocation2, null, (ImageLocation) ProfileGalleryView.this.imagesLocations.get(imageLocationPosition), null, (ImageLocation) ProfileGalleryView.this.thumbsLocations.get(imageLocationPosition), thumbFilter, null, ((Integer) ProfileGalleryView.this.imagesLocationsSizes.get(imageLocationPosition)).intValue(), 1, parent);
                    }
                } else {
                    AnimatedFileDrawable animatedFileDrawable = (AnimatedFileDrawable) drawable;
                    item.imageView.setImageDrawable(drawable);
                    animatedFileDrawable.addSecondParentView(item.imageView);
                    animatedFileDrawable.setInvalidateParentViewWithSecond(true);
                }
            }
            if (ProfileGalleryView.this.imagesUploadProgress.get(imageLocationPosition) != null) {
                needProgress = true;
            }
            if (needProgress) {
                item.imageView.radialProgress = (RadialProgress2) ProfileGalleryView.this.radialProgresses.get(imageLocationPosition);
                if (item.imageView.radialProgress == null) {
                    item.imageView.radialProgress = new RadialProgress2(item.imageView);
                    item.imageView.radialProgress.setOverrideAlpha(0.0f);
                    item.imageView.radialProgress.setIcon(10, false, false);
                    item.imageView.radialProgress.setColors(1107296256, 1107296256, -1, -1);
                    ProfileGalleryView.this.radialProgresses.append(imageLocationPosition, item.imageView.radialProgress);
                }
                if (ProfileGalleryView.this.invalidateWithParent) {
                    ProfileGalleryView.this.invalidate();
                } else {
                    ProfileGalleryView.this.postInvalidateOnAnimation();
                }
            }
            item.imageView.getImageReceiver().setDelegate(new ImageReceiver.ImageReceiverDelegate() { // from class: org.telegram.ui.Components.ProfileGalleryView.ViewPagerAdapter.1
                @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
                public void didSetImage(ImageReceiver imageReceiver, boolean set, boolean thumb2, boolean memCache) {
                }

                @Override // org.telegram.messenger.ImageReceiver.ImageReceiverDelegate
                public void onAnimationReady(ImageReceiver imageReceiver) {
                    ProfileGalleryView.this.callback.onVideoSet();
                }
            });
            item.imageView.getImageReceiver().setCrossfadeAlpha((byte) 2);
            item.imageView.setRoundRadius(ProfileGalleryView.this.roundTopRadius, ProfileGalleryView.this.roundTopRadius, ProfileGalleryView.this.roundBottomRadius, ProfileGalleryView.this.roundBottomRadius);
            return item;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup container, int position, Object object) {
            Item item = (Item) object;
            if (item.textureViewStubView != null) {
                container.removeView(item.textureViewStubView);
            }
            if (!item.isActiveVideo) {
                BackupImageView imageView = item.imageView;
                if (imageView.getImageReceiver().hasStaticThumb()) {
                    Drawable drawable = imageView.getImageReceiver().getDrawable();
                    if (drawable instanceof AnimatedFileDrawable) {
                        ((AnimatedFileDrawable) drawable).removeSecondParentView(imageView);
                    }
                }
                imageView.setRoundRadius(0);
                container.removeView(imageView);
                imageView.getImageReceiver().cancelLoadImage();
            }
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public CharSequence getPageTitle(int position) {
            return (getRealPosition(position) + 1) + "/" + (getCount() - (getExtraCount() * 2));
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void notifyDataSetChanged() {
            for (int i = 0; i < this.imageViews.size(); i++) {
                if (this.imageViews.get(i) != null) {
                    this.imageViews.get(i).getImageReceiver().cancelLoadImage();
                }
            }
            this.objects.clear();
            this.imageViews.clear();
            int size = ProfileGalleryView.this.imagesLocations.size();
            if (ProfileGalleryView.this.hasActiveVideo) {
                size++;
            }
            int N = (getExtraCount() * 2) + size;
            for (int a = 0; a < N; a++) {
                this.objects.add(new Item());
                this.imageViews.add(null);
            }
            super.notifyDataSetChanged();
        }

        @Override // org.telegram.ui.Components.CircularViewPager.Adapter
        public int getExtraCount() {
            int count = ProfileGalleryView.this.imagesLocations.size();
            if (ProfileGalleryView.this.hasActiveVideo) {
                count++;
            }
            if (count >= 2) {
                return ProfileGalleryView.this.getOffscreenPageLimit();
            }
            return 0;
        }
    }

    public void setData(long dialogId) {
        setData(dialogId, false);
    }

    public void setData(long dialogId, boolean forceReset) {
        if (this.dialogId == dialogId && !forceReset) {
            resetCurrentItem();
            return;
        }
        this.forceResetPosition = true;
        this.adapter.notifyDataSetChanged();
        reset();
        this.dialogId = dialogId;
        if (dialogId != 0) {
            MessagesController.getInstance(this.currentAccount).loadDialogPhotos(dialogId, 80, 0, true, this.parentClassGuid);
        }
    }

    private void reset() {
        this.videoFileNames.clear();
        this.thumbsFileNames.clear();
        this.photos.clear();
        this.videoLocations.clear();
        this.imagesLocations.clear();
        this.thumbsLocations.clear();
        this.imagesLocationsSizes.clear();
        this.imagesUploadProgress.clear();
        this.adapter.notifyDataSetChanged();
        this.uploadingImageLocation = null;
    }

    public void setRoundRadius(int topRadius, int bottomRadius) {
        this.roundTopRadius = topRadius;
        this.roundBottomRadius = bottomRadius;
        if (this.adapter != null) {
            for (int i = 0; i < this.adapter.objects.size(); i++) {
                if (((Item) this.adapter.objects.get(i)).imageView != null) {
                    AvatarImageView avatarImageView = ((Item) this.adapter.objects.get(i)).imageView;
                    int i2 = this.roundTopRadius;
                    int i3 = this.roundBottomRadius;
                    avatarImageView.setRoundRadius(i2, i2, i3, i3);
                }
            }
        }
    }

    public void setParentAvatarImage(BackupImageView parentImageView) {
        ViewPagerAdapter viewPagerAdapter = this.adapter;
        if (viewPagerAdapter != null) {
            viewPagerAdapter.parentAvatarImageView = parentImageView;
        }
    }

    public void setUploadProgress(ImageLocation imageLocation, float p) {
        if (imageLocation == null) {
            return;
        }
        int i = 0;
        while (true) {
            if (i >= this.imagesLocations.size()) {
                break;
            } else if (this.imagesLocations.get(i) != imageLocation) {
                i++;
            } else {
                this.imagesUploadProgress.set(i, Float.valueOf(p));
                if (this.radialProgresses.get(i) != null) {
                    this.radialProgresses.get(i).setProgress(p, true);
                }
            }
        }
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            getChildAt(i2).invalidate();
        }
    }

    public void setCreateThumbFromParent(boolean createThumbFromParent) {
        this.createThumbFromParent = createThumbFromParent;
    }

    /* loaded from: classes5.dex */
    public class AvatarImageView extends BackupImageView {
        public boolean isVideo;
        private final Paint placeholderPaint;
        private final int position;
        private RadialProgress2 radialProgress;
        private ValueAnimator radialProgressHideAnimator;
        private float radialProgressHideAnimatorStartValue;
        private final int radialProgressSize = AndroidUtilities.dp(64.0f);
        private long firstDrawTime = -1;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AvatarImageView(Context context, int position, Paint placeholderPaint) {
            super(context);
            ProfileGalleryView.this = r3;
            this.position = position;
            this.placeholderPaint = placeholderPaint;
            setLayerNum(r3.imagesLayerNum);
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            if (this.radialProgress != null) {
                int paddingTop = (ProfileGalleryView.this.parentActionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                int paddingBottom = AndroidUtilities.dp2(80.0f);
                RadialProgress2 radialProgress2 = this.radialProgress;
                int i = this.radialProgressSize;
                radialProgress2.setProgressRect((w - i) / 2, ((((h - paddingTop) - paddingBottom) - i) / 2) + paddingTop, (w + i) / 2, ((((h - paddingTop) - paddingBottom) + i) / 2) + paddingTop);
            }
        }

        @Override // org.telegram.ui.Components.BackupImageView, android.view.View
        public void onDraw(Canvas canvas) {
            boolean hideProgress;
            if (ProfileGalleryView.this.pinchToZoomHelper != null && ProfileGalleryView.this.pinchToZoomHelper.isInOverlayMode()) {
                return;
            }
            if (this.radialProgress != null) {
                int realPosition = ProfileGalleryView.this.getRealPosition(this.position);
                int realPosition2 = !ProfileGalleryView.this.hasActiveVideo ? realPosition : realPosition - 1;
                Drawable drawable = getImageReceiver().getDrawable();
                boolean z = false;
                if (realPosition2 < ProfileGalleryView.this.imagesUploadProgress.size() && ProfileGalleryView.this.imagesUploadProgress.get(realPosition2) != null) {
                    if (((Float) ProfileGalleryView.this.imagesUploadProgress.get(realPosition2)).floatValue() >= 1.0f) {
                        z = true;
                    }
                    hideProgress = z;
                } else {
                    if (drawable != null && (!this.isVideo || ((drawable instanceof AnimatedFileDrawable) && ((AnimatedFileDrawable) drawable).getDurationMs() > 0))) {
                        z = true;
                    }
                    hideProgress = z;
                }
                if (hideProgress) {
                    if (this.radialProgressHideAnimator == null) {
                        long startDelay = 0;
                        if (this.radialProgress.getProgress() < 1.0f) {
                            this.radialProgress.setProgress(1.0f, true);
                            startDelay = 100;
                        }
                        this.radialProgressHideAnimatorStartValue = this.radialProgress.getOverrideAlpha();
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                        this.radialProgressHideAnimator = ofFloat;
                        ofFloat.setStartDelay(startDelay);
                        this.radialProgressHideAnimator.setDuration(this.radialProgressHideAnimatorStartValue * 250.0f);
                        this.radialProgressHideAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        this.radialProgressHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ProfileGalleryView$AvatarImageView$$ExternalSyntheticLambda0
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ProfileGalleryView.AvatarImageView.this.m2910xe910519a(valueAnimator);
                            }
                        });
                        final int finalRealPosition = realPosition2;
                        this.radialProgressHideAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ProfileGalleryView.AvatarImageView.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                AvatarImageView.this.radialProgress = null;
                                ProfileGalleryView.this.radialProgresses.delete(finalRealPosition);
                            }
                        });
                        this.radialProgressHideAnimator.start();
                    }
                } else {
                    if (this.firstDrawTime < 0) {
                        this.firstDrawTime = System.currentTimeMillis();
                    } else {
                        long elapsedTime = System.currentTimeMillis() - this.firstDrawTime;
                        long startDelay2 = this.isVideo ? 250L : 750L;
                        if (elapsedTime <= 250 + startDelay2 && elapsedTime > startDelay2) {
                            this.radialProgress.setOverrideAlpha(CubicBezierInterpolator.DEFAULT.getInterpolation(((float) (elapsedTime - startDelay2)) / 250.0f));
                        }
                    }
                    if (ProfileGalleryView.this.invalidateWithParent) {
                        invalidate();
                    } else {
                        postInvalidateOnAnimation();
                    }
                    invalidate();
                }
                if (ProfileGalleryView.this.roundTopRadius != 0 || ProfileGalleryView.this.roundBottomRadius != 0) {
                    if (ProfileGalleryView.this.roundTopRadius == ProfileGalleryView.this.roundBottomRadius) {
                        ProfileGalleryView.this.rect.set(0.0f, 0.0f, getWidth(), getHeight());
                        canvas.drawRoundRect(ProfileGalleryView.this.rect, ProfileGalleryView.this.roundTopRadius, ProfileGalleryView.this.roundTopRadius, this.placeholderPaint);
                    } else {
                        ProfileGalleryView.this.path.reset();
                        ProfileGalleryView.this.rect.set(0.0f, 0.0f, getWidth(), getHeight());
                        for (int i = 0; i < 4; i++) {
                            ProfileGalleryView.this.radii[i] = ProfileGalleryView.this.roundTopRadius;
                            ProfileGalleryView.this.radii[i + 4] = ProfileGalleryView.this.roundBottomRadius;
                        }
                        ProfileGalleryView.this.path.addRoundRect(ProfileGalleryView.this.rect, ProfileGalleryView.this.radii, Path.Direction.CW);
                        canvas.drawPath(ProfileGalleryView.this.path, this.placeholderPaint);
                    }
                } else {
                    canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), this.placeholderPaint);
                }
            }
            super.onDraw(canvas);
            RadialProgress2 radialProgress2 = this.radialProgress;
            if (radialProgress2 != null && radialProgress2.getOverrideAlpha() > 0.0f) {
                this.radialProgress.draw(canvas);
            }
        }

        /* renamed from: lambda$onDraw$0$org-telegram-ui-Components-ProfileGalleryView$AvatarImageView */
        public /* synthetic */ void m2910xe910519a(ValueAnimator anim) {
            this.radialProgress.setOverrideAlpha(AndroidUtilities.lerp(this.radialProgressHideAnimatorStartValue, 0.0f, anim.getAnimatedFraction()));
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            if (ProfileGalleryView.this.invalidateWithParent) {
                ProfileGalleryView.this.invalidate();
            }
        }
    }

    public void setPinchToZoomHelper(PinchToZoomHelper pinchToZoomHelper) {
        this.pinchToZoomHelper = pinchToZoomHelper;
    }

    public void setInvalidateWithParent(boolean invalidateWithParent) {
        this.invalidateWithParent = invalidateWithParent;
    }

    /* loaded from: classes5.dex */
    public class TextureStubView extends View {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TextureStubView(Context context) {
            super(context);
            ProfileGalleryView.this = r1;
        }
    }
}
