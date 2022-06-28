package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class GroupedPhotosListView extends View implements GestureDetector.OnGestureListener {
    private boolean animateAllLine;
    private boolean animateBackground;
    private int animateToDX;
    private int animateToDXStart;
    private int animateToItem;
    private boolean animateToItemFast;
    private boolean animationsEnabled;
    private Paint backgroundPaint;
    private long currentGroupId;
    private int currentImage;
    private float currentItemProgress;
    private ArrayList<Object> currentObjects;
    public ArrayList<ImageLocation> currentPhotos;
    private GroupedPhotosListViewDelegate delegate;
    private float drawAlpha;
    private int drawDx;
    private GestureDetector gestureDetector;
    private boolean hasPhotos;
    private ValueAnimator hideAnimator;
    private boolean ignoreChanges;
    private ArrayList<ImageReceiver> imagesToDraw;
    private int itemHeight;
    private int itemSpacing;
    private int itemWidth;
    private int itemY;
    private long lastUpdateTime;
    private float moveLineProgress;
    private boolean moving;
    private int nextImage;
    private float nextItemProgress;
    private int nextPhotoScrolling;
    private android.widget.Scroller scroll;
    private boolean scrolling;
    private ValueAnimator showAnimator;
    private boolean stopedScrolling;
    private ArrayList<ImageReceiver> unusedReceivers;

    /* loaded from: classes5.dex */
    public interface GroupedPhotosListViewDelegate {
        long getAvatarsDialogId();

        int getCurrentAccount();

        int getCurrentIndex();

        ArrayList<MessageObject> getImagesArr();

        ArrayList<ImageLocation> getImagesArrLocations();

        List<TLRPC.PageBlock> getPageBlockArr();

        Object getParentObject();

        int getSlideshowMessageId();

        void onShowAnimationStart();

        void onStopScrolling();

        void setCurrentIndex(int i);

        boolean validGroupId(long j);
    }

    public GroupedPhotosListView(Context context) {
        this(context, AndroidUtilities.dp(3.0f));
    }

    public GroupedPhotosListView(Context context, int paddingTop) {
        super(context);
        this.backgroundPaint = new Paint();
        this.unusedReceivers = new ArrayList<>();
        this.imagesToDraw = new ArrayList<>();
        this.currentPhotos = new ArrayList<>();
        this.currentObjects = new ArrayList<>();
        this.currentItemProgress = 1.0f;
        this.nextItemProgress = 0.0f;
        this.animateToItem = -1;
        this.animationsEnabled = true;
        this.nextPhotoScrolling = -1;
        this.animateBackground = true;
        this.gestureDetector = new GestureDetector(context, this);
        this.scroll = new android.widget.Scroller(context);
        this.itemWidth = AndroidUtilities.dp(42.0f);
        this.itemHeight = AndroidUtilities.dp(56.0f);
        this.itemSpacing = AndroidUtilities.dp(1.0f);
        this.itemY = paddingTop;
        this.backgroundPaint.setColor(Theme.ACTION_BAR_PHOTO_VIEWER_COLOR);
    }

    public void clear() {
        this.currentPhotos.clear();
        this.currentObjects.clear();
        this.imagesToDraw.clear();
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void fillList() {
        ArrayList<ImageLocation> imagesArrLocations;
        int oldCount;
        int i;
        boolean changed;
        int currentIndex;
        long localGroupId;
        if (this.ignoreChanges) {
            this.ignoreChanges = false;
            return;
        }
        int currentIndex2 = this.delegate.getCurrentIndex();
        ArrayList<ImageLocation> imagesArrLocations2 = this.delegate.getImagesArrLocations();
        ArrayList<MessageObject> imagesArr = this.delegate.getImagesArr();
        List<TLRPC.PageBlock> pageBlockArr = this.delegate.getPageBlockArr();
        int slideshowMessageId = this.delegate.getSlideshowMessageId();
        int currentAccount = this.delegate.getCurrentAccount();
        this.hasPhotos = false;
        boolean changed2 = false;
        int newCount = 0;
        Object currentObject = null;
        if (imagesArrLocations2 != null && !imagesArrLocations2.isEmpty()) {
            if (currentIndex2 >= imagesArrLocations2.size()) {
                currentIndex2 = imagesArrLocations2.size() - 1;
            }
            Object location = (ImageLocation) imagesArrLocations2.get(currentIndex2);
            newCount = imagesArrLocations2.size();
            currentObject = location;
            this.hasPhotos = true;
            imagesArrLocations = imagesArrLocations2;
        } else if (imagesArr == null || imagesArr.isEmpty()) {
            imagesArrLocations = imagesArrLocations2;
            if (pageBlockArr != null && !pageBlockArr.isEmpty()) {
                TLRPC.PageBlock pageBlock = pageBlockArr.get(currentIndex2);
                Object currentObject2 = pageBlock;
                if (pageBlock.groupId != this.currentGroupId) {
                    changed2 = true;
                    this.currentGroupId = pageBlock.groupId;
                }
                if (this.currentGroupId != 0) {
                    this.hasPhotos = true;
                    int a = currentIndex2;
                    int size = pageBlockArr.size();
                    while (true) {
                        if (a >= size) {
                            changed = changed2;
                            break;
                        }
                        int currentAccount2 = currentAccount;
                        changed = changed2;
                        if (pageBlockArr.get(a).groupId != this.currentGroupId) {
                            break;
                        }
                        newCount++;
                        a++;
                        currentAccount = currentAccount2;
                        changed2 = changed;
                    }
                    for (int a2 = currentIndex2 - 1; a2 >= 0 && pageBlockArr.get(a2).groupId == this.currentGroupId; a2--) {
                        newCount++;
                    }
                    changed2 = changed;
                    currentObject = currentObject2;
                } else {
                    currentObject = currentObject2;
                }
            }
        } else {
            if (currentIndex2 >= imagesArr.size()) {
                currentIndex2 = imagesArr.size() - 1;
            }
            MessageObject messageObject = imagesArr.get(currentIndex2);
            currentObject = messageObject;
            imagesArrLocations = imagesArrLocations2;
            long localGroupId2 = messageObject.getGroupIdForUse();
            if (localGroupId2 != this.currentGroupId) {
                changed2 = true;
                this.currentGroupId = localGroupId2;
            }
            if (this.currentGroupId != 0) {
                this.hasPhotos = true;
                int max = Math.min(currentIndex2 + 10, imagesArr.size());
                for (int a3 = currentIndex2; a3 < max; a3++) {
                    MessageObject object = imagesArr.get(a3);
                    if (slideshowMessageId == 0 && object.getGroupIdForUse() != this.currentGroupId) {
                        break;
                    }
                    newCount++;
                }
                int min = Math.max(currentIndex2 - 10, 0);
                int a4 = currentIndex2 - 1;
                while (true) {
                    if (a4 < min) {
                        currentIndex = currentIndex2;
                        break;
                    }
                    MessageObject object2 = imagesArr.get(a4);
                    if (slideshowMessageId == 0) {
                        currentIndex = currentIndex2;
                        localGroupId = localGroupId2;
                        if (object2.getGroupIdForUse() != this.currentGroupId) {
                            break;
                        }
                    } else {
                        currentIndex = currentIndex2;
                        localGroupId = localGroupId2;
                    }
                    newCount++;
                    a4--;
                    currentIndex2 = currentIndex;
                    localGroupId2 = localGroupId;
                }
            } else {
                currentIndex = currentIndex2;
            }
            currentIndex2 = currentIndex;
        }
        if (currentObject == null) {
            return;
        }
        if (this.animationsEnabled) {
            if (!this.hasPhotos) {
                ValueAnimator valueAnimator = this.showAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.showAnimator = null;
                }
                if (this.drawAlpha > 0.0f && this.currentPhotos.size() > 1) {
                    if (this.hideAnimator == null) {
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.drawAlpha, 0.0f);
                        this.hideAnimator = ofFloat;
                        ofFloat.setDuration(this.drawAlpha * 200.0f);
                        this.hideAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.GroupedPhotosListView.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (GroupedPhotosListView.this.hideAnimator == animation) {
                                    GroupedPhotosListView.this.hideAnimator = null;
                                    GroupedPhotosListView.this.fillList();
                                }
                            }
                        });
                        this.hideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.GroupedPhotosListView$$ExternalSyntheticLambda0
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                                GroupedPhotosListView.this.m2673x71b0d47b(valueAnimator2);
                            }
                        });
                        this.hideAnimator.start();
                        return;
                    }
                    return;
                }
            } else {
                if (this.hideAnimator != null) {
                    Animator a5 = this.hideAnimator;
                    this.hideAnimator = null;
                    a5.cancel();
                }
                float f = this.drawAlpha;
                if (f < 1.0f && this.showAnimator == null) {
                    ValueAnimator ofFloat2 = ValueAnimator.ofFloat(f, 1.0f);
                    this.showAnimator = ofFloat2;
                    ofFloat2.setDuration((1.0f - this.drawAlpha) * 200.0f);
                    this.showAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.GroupedPhotosListView.2
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animation) {
                            if (GroupedPhotosListView.this.delegate != null) {
                                GroupedPhotosListView.this.delegate.onShowAnimationStart();
                            }
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            if (GroupedPhotosListView.this.showAnimator == animation) {
                                GroupedPhotosListView.this.showAnimator = null;
                            }
                        }
                    });
                    this.showAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.GroupedPhotosListView$$ExternalSyntheticLambda1
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            GroupedPhotosListView.this.m2674x635a7a9a(valueAnimator2);
                        }
                    });
                }
            }
        }
        if (!changed2) {
            if (newCount != this.currentPhotos.size() || !this.currentObjects.contains(currentObject)) {
                changed2 = true;
            } else {
                int newImageIndex = this.currentObjects.indexOf(currentObject);
                int i2 = this.currentImage;
                if (i2 != newImageIndex && newImageIndex != -1) {
                    boolean animate = this.animateAllLine;
                    if (!animate && !this.moving && (newImageIndex == i2 - 1 || newImageIndex == i2 + 1)) {
                        animate = true;
                        this.animateToItemFast = true;
                    }
                    if (!animate) {
                        fillImages(true, (i2 - newImageIndex) * (this.itemWidth + this.itemSpacing));
                        this.currentImage = newImageIndex;
                        i = 0;
                        this.moving = false;
                    } else {
                        this.animateToItem = newImageIndex;
                        this.nextImage = newImageIndex;
                        this.animateToDX = (i2 - newImageIndex) * (this.itemWidth + this.itemSpacing);
                        this.moving = true;
                        this.animateAllLine = false;
                        this.lastUpdateTime = System.currentTimeMillis();
                        invalidate();
                        i = 0;
                    }
                    this.drawDx = i;
                }
            }
        }
        if (changed2) {
            int oldCount2 = this.currentPhotos.size();
            this.animateAllLine = false;
            this.currentPhotos.clear();
            this.currentObjects.clear();
            if (imagesArrLocations != null && !imagesArrLocations.isEmpty()) {
                ArrayList<ImageLocation> imagesArrLocations3 = imagesArrLocations;
                this.currentObjects.addAll(imagesArrLocations3);
                this.currentPhotos.addAll(imagesArrLocations3);
                this.currentImage = currentIndex2;
                this.animateToItem = -1;
                this.animateToItemFast = false;
                oldCount = oldCount2;
            } else if (imagesArr == null || imagesArr.isEmpty()) {
                oldCount = oldCount2;
                if (pageBlockArr != null && !pageBlockArr.isEmpty()) {
                    if (this.currentGroupId != 0) {
                        int a6 = currentIndex2;
                        int size2 = pageBlockArr.size();
                        while (a6 < size2) {
                            TLRPC.PageBlock object3 = pageBlockArr.get(a6);
                            int size3 = size2;
                            ArrayList<MessageObject> imagesArr2 = imagesArr;
                            if (object3.groupId != this.currentGroupId) {
                                break;
                            }
                            this.currentObjects.add(object3);
                            this.currentPhotos.add(ImageLocation.getForObject(object3.thumb, object3.thumbObject));
                            a6++;
                            imagesArr = imagesArr2;
                            size2 = size3;
                        }
                        this.currentImage = 0;
                        this.animateToItem = -1;
                        this.animateToItemFast = false;
                        int a7 = currentIndex2 - 1;
                        while (a7 >= 0) {
                            TLRPC.PageBlock object4 = pageBlockArr.get(a7);
                            List<TLRPC.PageBlock> pageBlockArr2 = pageBlockArr;
                            if (object4.groupId != this.currentGroupId) {
                                break;
                            }
                            this.currentObjects.add(0, object4);
                            this.currentPhotos.add(0, ImageLocation.getForObject(object4.thumb, object4.thumbObject));
                            this.currentImage++;
                            a7--;
                            pageBlockArr = pageBlockArr2;
                        }
                    }
                }
            } else if (this.currentGroupId == 0 && slideshowMessageId == 0) {
                oldCount = oldCount2;
            } else {
                int max2 = Math.min(currentIndex2 + 10, imagesArr.size());
                int a8 = currentIndex2;
                while (true) {
                    if (a8 >= max2) {
                        oldCount = oldCount2;
                        break;
                    }
                    MessageObject object5 = imagesArr.get(a8);
                    if (slideshowMessageId == 0) {
                        oldCount = oldCount2;
                        if (object5.getGroupIdForUse() != this.currentGroupId) {
                            break;
                        }
                    } else {
                        oldCount = oldCount2;
                    }
                    this.currentObjects.add(object5);
                    this.currentPhotos.add(ImageLocation.getForObject(FileLoader.getClosestPhotoSizeWithSize(object5.photoThumbs, 56, true), object5.photoThumbsObject));
                    a8++;
                    oldCount2 = oldCount;
                }
                this.currentImage = 0;
                this.animateToItem = -1;
                this.animateToItemFast = false;
                int a9 = currentIndex2 - 1;
                for (int min2 = Math.max(currentIndex2 - 10, 0); a9 >= min2; min2 = min2) {
                    MessageObject object6 = imagesArr.get(a9);
                    if (slideshowMessageId == 0 && object6.getGroupIdForUse() != this.currentGroupId) {
                        break;
                    }
                    this.currentObjects.add(0, object6);
                    this.currentPhotos.add(0, ImageLocation.getForObject(FileLoader.getClosestPhotoSizeWithSize(object6.photoThumbs, 56, true), object6.photoThumbsObject));
                    this.currentImage++;
                    a9--;
                }
            }
            if (this.currentPhotos.size() == 1) {
                this.currentPhotos.clear();
                this.currentObjects.clear();
            }
            if (this.currentPhotos.size() != oldCount) {
                requestLayout();
            }
            fillImages(false, 0);
        }
    }

    /* renamed from: lambda$fillList$0$org-telegram-ui-Components-GroupedPhotosListView */
    public /* synthetic */ void m2673x71b0d47b(ValueAnimator a) {
        this.drawAlpha = ((Float) a.getAnimatedValue()).floatValue();
        invalidate();
    }

    /* renamed from: lambda$fillList$1$org-telegram-ui-Components-GroupedPhotosListView */
    public /* synthetic */ void m2674x635a7a9a(ValueAnimator a) {
        this.drawAlpha = ((Float) a.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setMoveProgress(float progress) {
        if (this.scrolling || this.animateToItem >= 0) {
            return;
        }
        if (progress > 0.0f) {
            this.nextImage = this.currentImage - 1;
        } else {
            this.nextImage = this.currentImage + 1;
        }
        int i = this.nextImage;
        if (i >= 0 && i < this.currentPhotos.size()) {
            this.currentItemProgress = 1.0f - Math.abs(progress);
        } else {
            this.currentItemProgress = 1.0f;
        }
        this.nextItemProgress = 1.0f - this.currentItemProgress;
        this.moving = progress != 0.0f;
        invalidate();
        if (!this.currentPhotos.isEmpty()) {
            if (progress >= 0.0f || this.currentImage != this.currentPhotos.size() - 1) {
                if (progress > 0.0f && this.currentImage == 0) {
                    return;
                }
                int i2 = (int) ((this.itemWidth + this.itemSpacing) * progress);
                this.drawDx = i2;
                fillImages(true, i2);
            }
        }
    }

    private ImageReceiver getFreeReceiver() {
        ImageReceiver receiver;
        if (this.unusedReceivers.isEmpty()) {
            receiver = new ImageReceiver(this);
        } else {
            receiver = this.unusedReceivers.get(0);
            this.unusedReceivers.remove(0);
        }
        this.imagesToDraw.add(receiver);
        receiver.setCurrentAccount(this.delegate.getCurrentAccount());
        return receiver;
    }

    private void fillImages(boolean move, int dx) {
        int addLeftIndex;
        int addRightIndex;
        Object obj;
        Object obj2;
        int i = 0;
        if (!move && !this.imagesToDraw.isEmpty()) {
            this.unusedReceivers.addAll(this.imagesToDraw);
            this.imagesToDraw.clear();
            this.moving = false;
            this.moveLineProgress = 1.0f;
            this.currentItemProgress = 1.0f;
            this.nextItemProgress = 0.0f;
        }
        invalidate();
        if (getMeasuredWidth() == 0 || this.currentPhotos.isEmpty()) {
            return;
        }
        int width = getMeasuredWidth();
        int startX = (getMeasuredWidth() / 2) - (this.itemWidth / 2);
        if (move) {
            addRightIndex = Integer.MIN_VALUE;
            addLeftIndex = Integer.MAX_VALUE;
            int count = this.imagesToDraw.size();
            int a = 0;
            while (a < count) {
                ImageReceiver receiver = this.imagesToDraw.get(a);
                int num = receiver.getParam();
                int i2 = this.itemWidth;
                int x = ((num - this.currentImage) * (this.itemSpacing + i2)) + startX + dx;
                if (x > width || i2 + x < 0) {
                    this.unusedReceivers.add(receiver);
                    this.imagesToDraw.remove(a);
                    count--;
                    a--;
                }
                addLeftIndex = Math.min(addLeftIndex, num - 1);
                addRightIndex = Math.max(addRightIndex, num + 1);
                a++;
            }
        } else {
            addRightIndex = this.currentImage;
            addLeftIndex = this.currentImage - 1;
        }
        if (addRightIndex != Integer.MIN_VALUE) {
            int count2 = this.currentPhotos.size();
            int a2 = addRightIndex;
            while (a2 < count2) {
                int x2 = ((a2 - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX + dx;
                if (x2 >= width) {
                    break;
                }
                ImageLocation location = this.currentPhotos.get(a2);
                ImageReceiver receiver2 = getFreeReceiver();
                receiver2.setImageCoords(x2, this.itemY, this.itemWidth, this.itemHeight);
                if (this.currentObjects.get(i) instanceof MessageObject) {
                    obj2 = this.currentObjects.get(a2);
                } else if (this.currentObjects.get(i) instanceof TLRPC.PageBlock) {
                    obj2 = this.delegate.getParentObject();
                } else {
                    obj2 = "avatar_" + this.delegate.getAvatarsDialogId();
                }
                Object parent = obj2;
                receiver2.setImage(null, null, location, "80_80", 0L, null, parent, 1);
                receiver2.setParam(a2);
                a2++;
                i = 0;
            }
        }
        if (addLeftIndex != Integer.MAX_VALUE) {
            for (int a3 = addLeftIndex; a3 >= 0; a3--) {
                int i3 = this.itemWidth;
                int x3 = ((a3 - this.currentImage) * (this.itemSpacing + i3)) + startX + dx + i3;
                if (x3 <= 0) {
                    break;
                }
                ImageLocation location2 = this.currentPhotos.get(a3);
                ImageReceiver receiver3 = getFreeReceiver();
                receiver3.setImageCoords(x3, this.itemY, this.itemWidth, this.itemHeight);
                if (this.currentObjects.get(0) instanceof MessageObject) {
                    obj = this.currentObjects.get(a3);
                } else if (this.currentObjects.get(0) instanceof TLRPC.PageBlock) {
                    obj = this.delegate.getParentObject();
                } else {
                    obj = "avatar_" + this.delegate.getAvatarsDialogId();
                }
                Object parent2 = obj;
                receiver3.setImage(null, null, location2, "80_80", 0L, null, parent2, 1);
                receiver3.setParam(a3);
            }
        }
        ValueAnimator valueAnimator = this.showAnimator;
        if (valueAnimator != null && !valueAnimator.isStarted()) {
            this.showAnimator.start();
        }
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onDown(MotionEvent e) {
        if (!this.scroll.isFinished()) {
            this.scroll.abortAnimation();
        }
        this.animateToItem = -1;
        this.animateToItemFast = false;
        return true;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onShowPress(MotionEvent e) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onSingleTapUp(MotionEvent e) {
        int currentIndex = this.delegate.getCurrentIndex();
        ArrayList<ImageLocation> imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList<MessageObject> imagesArr = this.delegate.getImagesArr();
        List<TLRPC.PageBlock> pageBlockArr = this.delegate.getPageBlockArr();
        stopScrolling();
        int count = this.imagesToDraw.size();
        for (int a = 0; a < count; a++) {
            ImageReceiver receiver = this.imagesToDraw.get(a);
            if (receiver.isInsideImage(e.getX(), e.getY())) {
                int num = receiver.getParam();
                if (num < 0 || num >= this.currentObjects.size()) {
                    return true;
                }
                if (imagesArr != null && !imagesArr.isEmpty()) {
                    MessageObject messageObject = (MessageObject) this.currentObjects.get(num);
                    int idx = imagesArr.indexOf(messageObject);
                    if (currentIndex == idx) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(idx);
                    return false;
                } else if (pageBlockArr != null && !pageBlockArr.isEmpty()) {
                    TLRPC.PageBlock pageBlock = (TLRPC.PageBlock) this.currentObjects.get(num);
                    int idx2 = pageBlockArr.indexOf(pageBlock);
                    if (currentIndex == idx2) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(idx2);
                    return false;
                } else if (imagesArrLocations != null && !imagesArrLocations.isEmpty()) {
                    ImageLocation location = (ImageLocation) this.currentObjects.get(num);
                    int idx3 = imagesArrLocations.indexOf(location);
                    if (currentIndex == idx3) {
                        return true;
                    }
                    this.moveLineProgress = 1.0f;
                    this.animateAllLine = true;
                    this.delegate.setCurrentIndex(idx3);
                    return false;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    private void updateAfterScroll() {
        int dx;
        int indexChange;
        int indexChange2 = 0;
        int dx2 = this.drawDx;
        int abs = Math.abs(dx2);
        int i = this.itemWidth;
        int i2 = this.itemSpacing;
        if (abs > (i / 2) + i2) {
            if (dx2 > 0) {
                dx = dx2 - ((i / 2) + i2);
                indexChange = 0 + 1;
            } else {
                dx = dx2 + (i / 2) + i2;
                indexChange = 0 - 1;
            }
            indexChange2 = indexChange + (dx / (i + (i2 * 2)));
        }
        this.nextPhotoScrolling = this.currentImage - indexChange2;
        int currentIndex = this.delegate.getCurrentIndex();
        ArrayList<ImageLocation> imagesArrLocations = this.delegate.getImagesArrLocations();
        ArrayList<MessageObject> imagesArr = this.delegate.getImagesArr();
        List<TLRPC.PageBlock> pageBlockArr = this.delegate.getPageBlockArr();
        int i3 = this.nextPhotoScrolling;
        if (currentIndex != i3 && i3 >= 0 && i3 < this.currentPhotos.size()) {
            Object photo = this.currentObjects.get(this.nextPhotoScrolling);
            int nextPhoto = -1;
            if (imagesArr != null && !imagesArr.isEmpty()) {
                MessageObject messageObject = (MessageObject) photo;
                nextPhoto = imagesArr.indexOf(messageObject);
            } else if (pageBlockArr != null && !pageBlockArr.isEmpty()) {
                TLRPC.PageBlock pageBlock = (TLRPC.PageBlock) photo;
                nextPhoto = pageBlockArr.indexOf(pageBlock);
            } else if (imagesArrLocations != null && !imagesArrLocations.isEmpty()) {
                ImageLocation location = (ImageLocation) photo;
                nextPhoto = imagesArrLocations.indexOf(location);
            }
            if (nextPhoto >= 0) {
                this.ignoreChanges = true;
                this.delegate.setCurrentIndex(nextPhoto);
            }
        }
        if (!this.scrolling) {
            this.scrolling = true;
            this.stopedScrolling = false;
        }
        fillImages(true, this.drawDx);
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        this.drawDx = (int) (this.drawDx - distanceX);
        int min = getMinScrollX();
        int max = getMaxScrollX();
        int i = this.drawDx;
        if (i < min) {
            this.drawDx = min;
        } else if (i > max) {
            this.drawDx = max;
        }
        updateAfterScroll();
        return false;
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public void onLongPress(MotionEvent e) {
    }

    @Override // android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        this.scroll.abortAnimation();
        if (this.currentPhotos.size() >= 10) {
            this.scroll.fling(this.drawDx, 0, Math.round(velocityX), 0, getMinScrollX(), getMaxScrollX(), 0, 0);
            return false;
        }
        return false;
    }

    private void stopScrolling() {
        this.scrolling = false;
        if (!this.scroll.isFinished()) {
            this.scroll.abortAnimation();
        }
        int i = this.nextPhotoScrolling;
        if (i >= 0 && i < this.currentObjects.size()) {
            this.stopedScrolling = true;
            this.animateToItemFast = false;
            int i2 = this.nextPhotoScrolling;
            this.animateToItem = i2;
            this.nextImage = i2;
            this.animateToDX = (this.currentImage - i2) * (this.itemWidth + this.itemSpacing);
            this.animateToDXStart = this.drawDx;
            this.moveLineProgress = 1.0f;
            this.nextPhotoScrolling = -1;
            GroupedPhotosListViewDelegate groupedPhotosListViewDelegate = this.delegate;
            if (groupedPhotosListViewDelegate != null) {
                groupedPhotosListViewDelegate.onStopScrolling();
            }
        }
        invalidate();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        boolean z = false;
        if (this.currentPhotos.isEmpty() || getAlpha() != 1.0f) {
            return false;
        }
        if (this.gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)) {
            z = true;
        }
        boolean result = z;
        if (this.scrolling && event.getAction() == 1 && this.scroll.isFinished()) {
            stopScrolling();
        }
        return result;
    }

    private int getMinScrollX() {
        return (-((this.currentPhotos.size() - this.currentImage) - 1)) * (this.itemWidth + (this.itemSpacing * 2));
    }

    private int getMaxScrollX() {
        return this.currentImage * (this.itemWidth + (this.itemSpacing * 2));
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        fillImages(false, 0);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int trueWidth;
        int i;
        int nextTrueWidth;
        int i2;
        float interpolation;
        int i3;
        int i4;
        int maxItemWidth;
        int count;
        if (!this.hasPhotos && this.imagesToDraw.isEmpty()) {
            return;
        }
        float bgAlpha = this.drawAlpha;
        if (!this.animateBackground) {
            bgAlpha = this.hasPhotos ? 1.0f : 0.0f;
        }
        this.backgroundPaint.setAlpha((int) (127.0f * bgAlpha));
        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.backgroundPaint);
        if (this.imagesToDraw.isEmpty()) {
            return;
        }
        int count2 = this.imagesToDraw.size();
        int moveX = this.drawDx;
        int maxItemWidth2 = (int) (this.itemWidth * 2.0f);
        int padding = AndroidUtilities.dp(8.0f);
        ImageLocation object = this.currentPhotos.get(this.currentImage);
        if (object != null && object.photoSize != null) {
            trueWidth = Math.max(this.itemWidth, (int) (object.photoSize.w * (this.itemHeight / object.photoSize.h)));
        } else {
            trueWidth = this.itemHeight;
        }
        int trueWidth2 = Math.min(maxItemWidth2, trueWidth);
        float f = this.currentItemProgress;
        int currentPaddings = (int) (padding * 2 * f);
        int trueWidth3 = this.itemWidth + ((int) ((trueWidth2 - i) * f)) + currentPaddings;
        int trueWidth4 = this.nextImage;
        if (trueWidth4 >= 0 && trueWidth4 < this.currentPhotos.size()) {
            ImageLocation object2 = this.currentPhotos.get(this.nextImage);
            if (object2 != null && object2.photoSize != null) {
                nextTrueWidth = Math.max(this.itemWidth, (int) (object2.photoSize.w * (this.itemHeight / object2.photoSize.h)));
            } else {
                nextTrueWidth = this.itemHeight;
            }
        } else {
            nextTrueWidth = this.itemWidth;
        }
        int nextTrueWidth2 = Math.min(maxItemWidth2, nextTrueWidth);
        float f2 = this.nextItemProgress;
        int nextPaddings = (int) (padding * 2 * f2);
        float f3 = moveX;
        int moveX2 = (int) (f3 + ((((nextTrueWidth2 + nextPaddings) - i2) / 2) * f2 * (this.nextImage > this.currentImage ? -1 : 1)));
        int nextTrueWidth3 = this.itemWidth + ((int) ((nextTrueWidth2 - i2) * f2)) + nextPaddings;
        int startX = (getMeasuredWidth() - trueWidth3) / 2;
        int a = 0;
        while (a < count2) {
            ImageReceiver receiver = this.imagesToDraw.get(a);
            int num = receiver.getParam();
            int i5 = this.currentImage;
            if (num == i5) {
                receiver.setImageX(startX + moveX2 + (currentPaddings / 2));
                receiver.setImageWidth(trueWidth3 - currentPaddings);
                count = count2;
                maxItemWidth = maxItemWidth2;
            } else {
                int i6 = this.nextImage;
                if (i6 < i5) {
                    if (num < i5) {
                        if (num <= i6) {
                            int i7 = this.itemWidth;
                            count = count2;
                            int count3 = this.itemSpacing;
                            receiver.setImageX((((((receiver.getParam() - this.currentImage) + 1) * (i7 + count3)) + startX) - (count3 + nextTrueWidth3)) + moveX2);
                            maxItemWidth = maxItemWidth2;
                        } else {
                            count = count2;
                            int count4 = receiver.getParam();
                            receiver.setImageX(((count4 - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX + moveX2);
                            maxItemWidth = maxItemWidth2;
                        }
                    } else {
                        count = count2;
                        int count5 = startX + trueWidth3;
                        maxItemWidth = maxItemWidth2;
                        receiver.setImageX(count5 + this.itemSpacing + (((receiver.getParam() - this.currentImage) - 1) * (this.itemWidth + this.itemSpacing)) + moveX2);
                    }
                } else {
                    count = count2;
                    maxItemWidth = maxItemWidth2;
                    if (num < i5) {
                        receiver.setImageX(((receiver.getParam() - this.currentImage) * (this.itemWidth + this.itemSpacing)) + startX + moveX2);
                    } else if (num <= i6) {
                        receiver.setImageX(startX + trueWidth3 + this.itemSpacing + (((receiver.getParam() - this.currentImage) - 1) * (this.itemWidth + this.itemSpacing)) + moveX2);
                    } else {
                        int i8 = this.itemWidth;
                        int i9 = this.itemSpacing;
                        receiver.setImageX(startX + trueWidth3 + this.itemSpacing + (((receiver.getParam() - this.currentImage) - 2) * (i8 + i9)) + i9 + nextTrueWidth3 + moveX2);
                    }
                }
                if (num == this.nextImage) {
                    receiver.setImageWidth(nextTrueWidth3 - nextPaddings);
                    receiver.setImageX((int) (receiver.getImageX() + (nextPaddings / 2)));
                } else {
                    receiver.setImageWidth(this.itemWidth);
                }
            }
            receiver.setAlpha(this.drawAlpha);
            receiver.setRoundRadius(AndroidUtilities.dp(2.0f));
            receiver.draw(canvas);
            a++;
            count2 = count;
            maxItemWidth2 = maxItemWidth;
        }
        long newTime = System.currentTimeMillis();
        long dt = newTime - this.lastUpdateTime;
        if (dt > 17) {
            dt = 17;
        }
        this.lastUpdateTime = newTime;
        int i10 = this.animateToItem;
        if (i10 >= 0) {
            float f4 = this.moveLineProgress;
            if (f4 > 0.0f) {
                this.moveLineProgress = f4 - (((float) dt) / (this.animateToItemFast ? 100.0f : 200.0f));
                if (i10 != this.currentImage) {
                    this.nextItemProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(1.0f - this.moveLineProgress);
                    if (!this.stopedScrolling) {
                        this.currentItemProgress = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.moveLineProgress);
                        this.drawDx = (int) Math.ceil(this.nextItemProgress * this.animateToDX);
                    } else {
                        float f5 = this.currentItemProgress;
                        if (f5 > 0.0f) {
                            float f6 = f5 - (((float) dt) / 200.0f);
                            this.currentItemProgress = f6;
                            if (f6 < 0.0f) {
                                this.currentItemProgress = 0.0f;
                            }
                        }
                        this.drawDx = this.animateToDXStart + ((int) Math.ceil(interpolation * (this.animateToDX - i3)));
                    }
                } else {
                    float f7 = this.currentItemProgress;
                    if (f7 < 1.0f) {
                        float f8 = f7 + (((float) dt) / 200.0f);
                        this.currentItemProgress = f8;
                        if (f8 > 1.0f) {
                            this.currentItemProgress = 1.0f;
                        }
                    }
                    this.drawDx = this.animateToDXStart + ((int) Math.ceil(this.currentItemProgress * (this.animateToDX - i4)));
                }
                if (this.moveLineProgress <= 0.0f) {
                    this.currentImage = this.animateToItem;
                    this.moveLineProgress = 1.0f;
                    this.currentItemProgress = 1.0f;
                    this.nextItemProgress = 0.0f;
                    this.moving = false;
                    this.stopedScrolling = false;
                    this.drawDx = 0;
                    this.animateToItem = -1;
                    this.animateToItemFast = false;
                }
            }
            int moveX3 = this.drawDx;
            fillImages(true, moveX3);
            invalidate();
        }
        if (this.scrolling) {
            float f9 = this.currentItemProgress;
            if (f9 > 0.0f) {
                float f10 = f9 - (((float) dt) / 200.0f);
                this.currentItemProgress = f10;
                if (f10 < 0.0f) {
                    this.currentItemProgress = 0.0f;
                }
                invalidate();
            }
        }
        if (!this.scroll.isFinished()) {
            if (this.scroll.computeScrollOffset()) {
                this.drawDx = this.scroll.getCurrX();
                updateAfterScroll();
                invalidate();
            }
            if (this.scroll.isFinished()) {
                stopScrolling();
            }
        }
    }

    public void setDelegate(GroupedPhotosListViewDelegate groupedPhotosListViewDelegate) {
        this.delegate = groupedPhotosListViewDelegate;
    }

    public boolean hasPhotos() {
        ValueAnimator valueAnimator;
        return this.hasPhotos && this.hideAnimator == null && (this.drawAlpha > 0.0f || !this.animateBackground || ((valueAnimator = this.showAnimator) != null && valueAnimator.isStarted()));
    }

    public boolean isAnimationsEnabled() {
        return this.animationsEnabled;
    }

    public void setAnimationsEnabled(boolean animationsEnabled) {
        if (this.animationsEnabled != animationsEnabled) {
            this.animationsEnabled = animationsEnabled;
            if (!animationsEnabled) {
                ValueAnimator valueAnimator = this.showAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.showAnimator = null;
                }
                ValueAnimator valueAnimator2 = this.hideAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                    this.hideAnimator = null;
                }
                this.drawAlpha = 0.0f;
                invalidate();
            }
        }
    }

    public void setAnimateBackground(boolean animateBackground) {
        this.animateBackground = animateBackground;
    }

    public void reset() {
        this.hasPhotos = false;
        if (this.animationsEnabled) {
            this.drawAlpha = 0.0f;
        }
    }
}
