package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.microsoft.appcenter.Constants;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.camera.CameraView;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoAttachCameraCell;
import org.telegram.ui.Cells.PhotoAttachPermissionCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertPhotoLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerViewItemRangeSelector;
import org.telegram.ui.Components.ShutterButton;
import org.telegram.ui.Components.ZoomControlView;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes5.dex */
public class ChatAttachAlertPhotoLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    public static final int compress = 1;
    public static final int group = 0;
    private static boolean mediaFromExternalCamera = false;
    public static final int open_in = 2;
    public static final int preview = 3;
    private PhotoAttachAdapter adapter;
    float additionCloseCameraY;
    private int alertOnlyOnce;
    private int animateToPadding;
    float animationClipBottom;
    float animationClipLeft;
    float animationClipRight;
    float animationClipTop;
    private boolean cameraAnimationInProgress;
    private PhotoAttachAdapter cameraAttachAdapter;
    protected PhotoAttachCameraCell cameraCell;
    private Drawable cameraDrawable;
    boolean cameraExpanded;
    protected FrameLayout cameraIcon;
    private AnimatorSet cameraInitAnimation;
    private float cameraOpenProgress;
    private boolean cameraOpened;
    private FrameLayout cameraPanel;
    private LinearLayoutManager cameraPhotoLayoutManager;
    private RecyclerListView cameraPhotoRecyclerView;
    private boolean cameraPhotoRecyclerViewIgnoreLayout;
    protected CameraView cameraView;
    private float cameraViewOffsetBottomY;
    private float cameraViewOffsetX;
    private float cameraViewOffsetY;
    private float cameraZoom;
    private boolean canSaveCameraPreview;
    private boolean cancelTakingPhotos;
    private boolean checkCameraWhenShown;
    private TextView counterTextView;
    private float currentPanTranslationY;
    private int currentSelectedCount;
    private boolean deviceHasGoodCamera;
    private boolean dragging;
    public TextView dropDown;
    private ArrayList<MediaController.AlbumEntry> dropDownAlbums;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    private boolean flashAnimationInProgress;
    boolean forceDarkTheme;
    private MediaController.AlbumEntry galleryAlbumEntry;
    private int gridExtraSpace;
    public RecyclerListView gridView;
    private ViewPropertyAnimator headerAnimator;
    private boolean ignoreLayout;
    private boolean isHidden;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    private int itemSize;
    private int lastItemSize;
    private int lastNotifyWidth;
    private float lastY;
    private GridLayoutManager layoutManager;
    private boolean maybeStartDraging;
    private boolean mediaEnabled;
    private boolean noCameraPermissions;
    private boolean noGalleryPermissions;
    ValueAnimator paddingAnimator;
    private float pinchStartDistance;
    private boolean pressed;
    private EmptyTextProgressView progressView;
    private TextView recordTime;
    private boolean requestingPermissions;
    private MediaController.AlbumEntry selectedAlbumEntry;
    private boolean shouldSelect;
    private ShutterButton shutterButton;
    private ImageView switchCameraButton;
    private boolean takingPhoto;
    private TextView tooltipTextView;
    private Runnable videoRecordRunnable;
    private int videoRecordTime;
    private AnimatorSet zoomControlAnimation;
    private Runnable zoomControlHideRunnable;
    private ZoomControlView zoomControlView;
    private boolean zoomWas;
    private boolean zooming;
    private static ArrayList<Object> cameraPhotos = new ArrayList<>();
    private static HashMap<Object, Object> selectedPhotos = new HashMap<>();
    private static ArrayList<Object> selectedPhotosOrder = new ArrayList<>();
    private static int lastImageId = -1;
    private ImageView[] flashModeButton = new ImageView[2];
    private float[] cameraViewLocation = new float[2];
    private int[] viewPosition = new int[2];
    private int[] animateCameraValues = new int[5];
    private DecelerateInterpolator interpolator = new DecelerateInterpolator(1.5f);
    private android.graphics.Rect hitRect = new android.graphics.Rect();
    private int itemsPerRow = 3;
    private boolean loading = true;
    private int animationIndex = -1;
    private PhotoViewer.PhotoViewerProvider photoViewerProvider = new BasePhotoProvider() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.1
        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onOpen() {
            ChatAttachAlertPhotoLayout.this.pauseCameraPreview();
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onClose() {
            ChatAttachAlertPhotoLayout.this.resumeCameraPreview();
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            PhotoAttachPhotoCell cell = ChatAttachAlertPhotoLayout.this.getCellForIndex(index);
            if (cell != null) {
                int[] coords = new int[2];
                cell.getImageView().getLocationInWindow(coords);
                if (Build.VERSION.SDK_INT < 26) {
                    coords[0] = coords[0] - ChatAttachAlertPhotoLayout.this.parentAlert.getLeftInset();
                }
                PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                object.viewX = coords[0];
                object.viewY = coords[1];
                object.parentView = ChatAttachAlertPhotoLayout.this.gridView;
                object.imageReceiver = cell.getImageView().getImageReceiver();
                object.thumb = object.imageReceiver.getBitmapSafe();
                object.scale = cell.getScale();
                object.clipBottomAddition = (int) ChatAttachAlertPhotoLayout.this.parentAlert.getClipLayoutBottom();
                cell.showCheck(false);
                return object;
            }
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void updatePhotoAtIndex(int index) {
            PhotoAttachPhotoCell cell = ChatAttachAlertPhotoLayout.this.getCellForIndex(index);
            if (cell != null) {
                cell.getImageView().setOrientation(0, true);
                MediaController.PhotoEntry photoEntry = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(index);
                if (photoEntry == null) {
                    return;
                }
                if (photoEntry.thumbPath != null) {
                    cell.getImageView().setImage(photoEntry.thumbPath, null, Theme.chat_attachEmptyDrawable);
                } else if (photoEntry.path != null) {
                    cell.getImageView().setOrientation(photoEntry.orientation, true);
                    if (photoEntry.isVideo) {
                        BackupImageView imageView = cell.getImageView();
                        imageView.setImage("vthumb://" + photoEntry.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + photoEntry.path, null, Theme.chat_attachEmptyDrawable);
                        return;
                    }
                    BackupImageView imageView2 = cell.getImageView();
                    imageView2.setImage("thumb://" + photoEntry.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + photoEntry.path, null, Theme.chat_attachEmptyDrawable);
                } else {
                    cell.getImageView().setImageDrawable(Theme.chat_attachEmptyDrawable);
                }
            }
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            PhotoAttachPhotoCell cell = ChatAttachAlertPhotoLayout.this.getCellForIndex(index);
            if (cell != null) {
                return cell.getImageView().getImageReceiver().getBitmapSafe();
            }
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            PhotoAttachPhotoCell cell = ChatAttachAlertPhotoLayout.this.getCellForIndex(index);
            if (cell != null) {
                cell.showCheck(true);
            }
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willHidePhotoViewer() {
            int count = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(a);
                if (view instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                    cell.showCheck(true);
                }
            }
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onApplyCaption(CharSequence caption) {
            if (ChatAttachAlertPhotoLayout.selectedPhotos.size() > 0 && ChatAttachAlertPhotoLayout.selectedPhotosOrder.size() > 0) {
                Object o = ChatAttachAlertPhotoLayout.selectedPhotos.get(ChatAttachAlertPhotoLayout.selectedPhotosOrder.get(0));
                CharSequence firstPhotoCaption = null;
                if (o instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry1 = (MediaController.PhotoEntry) o;
                    firstPhotoCaption = photoEntry1.caption;
                }
                if (o instanceof MediaController.SearchImage) {
                    MediaController.SearchImage photoEntry12 = (MediaController.SearchImage) o;
                    firstPhotoCaption = photoEntry12.caption;
                }
                ChatAttachAlertPhotoLayout.this.parentAlert.commentTextView.setText(firstPhotoCaption);
            }
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean cancelButtonPressed() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean forceDocument) {
            MediaController.PhotoEntry photoEntry = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(index);
            if (photoEntry != null) {
                photoEntry.editedInfo = videoEditedInfo;
            }
            if (ChatAttachAlertPhotoLayout.selectedPhotos.isEmpty() && photoEntry != null) {
                ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(photoEntry, -1);
            }
            ChatAttachAlertPhotoLayout.this.parentAlert.applyCaption();
            if (PhotoViewer.getInstance().hasCaptionForAllMedia) {
                HashMap<Object, Object> selectedPhotos2 = getSelectedPhotos();
                ArrayList<Object> selectedPhotosOrder2 = getSelectedPhotosOrder();
                if (!selectedPhotos2.isEmpty()) {
                    for (int a = 0; a < selectedPhotosOrder2.size(); a++) {
                        Object o = selectedPhotos2.get(selectedPhotosOrder2.get(a));
                        if (o instanceof MediaController.PhotoEntry) {
                            MediaController.PhotoEntry photoEntry1 = (MediaController.PhotoEntry) o;
                            if (a == 0) {
                                photoEntry1.caption = PhotoViewer.getInstance().captionForAllMedia;
                            } else {
                                photoEntry1.caption = null;
                            }
                        }
                    }
                }
            }
            ChatAttachAlertPhotoLayout.this.parentAlert.delegate.didPressedButton(7, true, notify, scheduleDate, forceDocument);
        }
    };
    public int currentItemTop = 0;

    static /* synthetic */ int access$2408(ChatAttachAlertPhotoLayout x0) {
        int i = x0.videoRecordTime;
        x0.videoRecordTime = i + 1;
        return i;
    }

    static /* synthetic */ int access$3010() {
        int i = lastImageId;
        lastImageId = i - 1;
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class BasePhotoProvider extends PhotoViewer.EmptyPhotoViewerProvider {
        private BasePhotoProvider() {
            ChatAttachAlertPhotoLayout.this = r1;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean isPhotoChecked(int index) {
            MediaController.PhotoEntry photoEntry = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(index);
            return photoEntry != null && ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            MediaController.PhotoEntry photoEntry;
            if ((ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos < 0 || ChatAttachAlertPhotoLayout.selectedPhotos.size() < ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos || isPhotoChecked(index)) && (photoEntry = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(index)) != null) {
                boolean add = true;
                int addToSelectedPhotos = ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(photoEntry, -1);
                int num = addToSelectedPhotos;
                if (addToSelectedPhotos == -1) {
                    num = ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                } else {
                    add = false;
                    photoEntry.editedInfo = null;
                }
                photoEntry.editedInfo = videoEditedInfo;
                int count = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
                int a = 0;
                while (true) {
                    if (a >= count) {
                        break;
                    }
                    View view = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(a);
                    if (view instanceof PhotoAttachPhotoCell) {
                        int tag = ((Integer) view.getTag()).intValue();
                        if (tag == index) {
                            if ((ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) && ChatAttachAlertPhotoLayout.this.parentAlert.allowOrder) {
                                ((PhotoAttachPhotoCell) view).setChecked(num, add, false);
                            } else {
                                ((PhotoAttachPhotoCell) view).setChecked(-1, add, false);
                            }
                        }
                    }
                    a++;
                }
                int count2 = ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.getChildCount();
                int a2 = 0;
                while (true) {
                    if (a2 >= count2) {
                        break;
                    }
                    View view2 = ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.getChildAt(a2);
                    if (view2 instanceof PhotoAttachPhotoCell) {
                        int tag2 = ((Integer) view2.getTag()).intValue();
                        if (tag2 == index) {
                            if ((ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) && ChatAttachAlertPhotoLayout.this.parentAlert.allowOrder) {
                                ((PhotoAttachPhotoCell) view2).setChecked(num, add, false);
                            } else {
                                ((PhotoAttachPhotoCell) view2).setChecked(-1, add, false);
                            }
                        }
                    }
                    a2++;
                }
                ChatAttachAlertPhotoLayout.this.parentAlert.updateCountButton(add ? 1 : 2);
                return num;
            }
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getSelectedCount() {
            return ChatAttachAlertPhotoLayout.selectedPhotos.size();
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public ArrayList<Object> getSelectedPhotosOrder() {
            return ChatAttachAlertPhotoLayout.selectedPhotosOrder;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public HashMap<Object, Object> getSelectedPhotos() {
            return ChatAttachAlertPhotoLayout.selectedPhotos;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getPhotoIndex(int index) {
            MediaController.PhotoEntry photoEntry = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(index);
            if (photoEntry != null) {
                return ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
            }
            return -1;
        }
    }

    protected void updateCheckedPhotoIndices() {
        if (!(this.parentAlert.baseFragment instanceof ChatActivity)) {
            return;
        }
        int count = this.gridView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.gridView.getChildAt(a);
            if (view instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                MediaController.PhotoEntry photoEntry = getPhotoEntryAtPosition(((Integer) cell.getTag()).intValue());
                if (photoEntry != null) {
                    cell.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)));
                }
            }
        }
        int count2 = this.cameraPhotoRecyclerView.getChildCount();
        for (int a2 = 0; a2 < count2; a2++) {
            View view2 = this.cameraPhotoRecyclerView.getChildAt(a2);
            if (view2 instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell cell2 = (PhotoAttachPhotoCell) view2;
                MediaController.PhotoEntry photoEntry2 = getPhotoEntryAtPosition(((Integer) cell2.getTag()).intValue());
                if (photoEntry2 != null) {
                    cell2.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry2.imageId)));
                }
            }
        }
    }

    public MediaController.PhotoEntry getPhotoEntryAtPosition(int position) {
        if (position < 0) {
            return null;
        }
        int cameraCount = cameraPhotos.size();
        if (position < cameraCount) {
            return (MediaController.PhotoEntry) cameraPhotos.get(position);
        }
        int position2 = position - cameraCount;
        if (position2 >= this.selectedAlbumEntry.photos.size()) {
            return null;
        }
        return this.selectedAlbumEntry.photos.get(position2);
    }

    protected ArrayList<Object> getAllPhotosArray() {
        if (this.selectedAlbumEntry != null) {
            if (!cameraPhotos.isEmpty()) {
                ArrayList<Object> arrayList = new ArrayList<>(this.selectedAlbumEntry.photos.size() + cameraPhotos.size());
                arrayList.addAll(cameraPhotos);
                arrayList.addAll(this.selectedAlbumEntry.photos);
                return arrayList;
            }
            ArrayList<Object> arrayList2 = this.selectedAlbumEntry.photos;
            return arrayList2;
        }
        ArrayList<Object> arrayList3 = cameraPhotos;
        if (!arrayList3.isEmpty()) {
            ArrayList<Object> arrayList4 = cameraPhotos;
            return arrayList4;
        }
        ArrayList<Object> arrayList5 = new ArrayList<>(0);
        return arrayList5;
    }

    public ChatAttachAlertPhotoLayout(ChatAttachAlert alert, Context context, boolean forceDarkTheme, final Theme.ResourcesProvider resourcesProvider) {
        super(alert, context, resourcesProvider);
        int dp = AndroidUtilities.dp(80.0f);
        this.itemSize = dp;
        this.lastItemSize = dp;
        this.forceDarkTheme = forceDarkTheme;
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.cameraInitied);
        FrameLayout container = alert.getContainer();
        this.cameraDrawable = context.getResources().getDrawable(R.drawable.instant_camera).mutate();
        ActionBarMenu menu = this.parentAlert.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, menu, 0, 0, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.2
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem, android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(info);
                info.setText(ChatAttachAlertPhotoLayout.this.dropDown.getText());
            }
        };
        this.dropDownContainer = actionBarMenuItem;
        actionBarMenuItem.setSubMenuOpenSide(1);
        this.parentAlert.actionBar.addView(this.dropDownContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, AndroidUtilities.isTablet() ? 64.0f : 56.0f, 0.0f, 40.0f, 0.0f));
        this.dropDownContainer.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatAttachAlertPhotoLayout.this.m2478x62c70849(view);
            }
        });
        TextView textView = new TextView(context);
        this.dropDown = textView;
        textView.setImportantForAccessibility(2);
        this.dropDown.setGravity(3);
        this.dropDown.setSingleLine(true);
        this.dropDown.setLines(1);
        this.dropDown.setMaxLines(1);
        this.dropDown.setEllipsize(TextUtils.TruncateAt.END);
        this.dropDown.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        this.dropDown.setText(LocaleController.getString("ChatGallery", R.string.ChatGallery));
        this.dropDown.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        Drawable mutate = context.getResources().getDrawable(R.drawable.ic_arrow_drop_down).mutate();
        this.dropDownDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogTextBlack), PorterDuff.Mode.MULTIPLY));
        this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0f), 0);
        this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0f, 16, 16.0f, 0.0f, 0.0f, 0.0f));
        checkCamera(false);
        this.parentAlert.selectedMenuItem.addSubItem(0, LocaleController.getString("SendWithoutGrouping", R.string.SendWithoutGrouping));
        this.parentAlert.selectedMenuItem.addSubItem(1, LocaleController.getString("SendWithoutCompression", R.string.SendWithoutCompression));
        this.parentAlert.selectedMenuItem.addSubItem(2, R.drawable.msg_openin, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
        this.parentAlert.selectedMenuItem.addSubItem(3, LocaleController.getString("AttachMediaPreviewButton", R.string.AttachMediaPreviewButton));
        RecyclerListView recyclerListView = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.3
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                if (e.getAction() != 0 || e.getY() >= ChatAttachAlertPhotoLayout.this.parentAlert.scrollOffsetY[0] - AndroidUtilities.dp(80.0f)) {
                    return super.onTouchEvent(e);
                }
                return false;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent e) {
                if (e.getAction() != 0 || e.getY() >= ChatAttachAlertPhotoLayout.this.parentAlert.scrollOffsetY[0] - AndroidUtilities.dp(80.0f)) {
                    return super.onInterceptTouchEvent(e);
                }
                return false;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int l, int t, int r, int b) {
                super.onLayout(changed, l, t, r, b);
                PhotoViewer.getInstance().checkCurrentImageVisibility();
            }
        };
        this.gridView = recyclerListView;
        PhotoAttachAdapter photoAttachAdapter = new PhotoAttachAdapter(context, true);
        this.adapter = photoAttachAdapter;
        recyclerListView.setAdapter(photoAttachAdapter);
        this.adapter.createCache();
        this.gridView.setClipToPadding(false);
        this.gridView.setItemAnimator(null);
        this.gridView.setLayoutAnimation(null);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f));
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.4
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (ChatAttachAlertPhotoLayout.this.gridView.getChildCount() <= 0) {
                    return;
                }
                ChatAttachAlertPhotoLayout.this.parentAlert.updateLayout(ChatAttachAlertPhotoLayout.this, true, dy);
                if (dy != 0) {
                    ChatAttachAlertPhotoLayout.this.checkCameraViewPosition();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                RecyclerListView.Holder holder;
                if (newState == 0) {
                    int offset = AndroidUtilities.dp(13.0f) + (ChatAttachAlertPhotoLayout.this.parentAlert.selectedMenuItem != null ? AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.selectedMenuItem.getAlpha() * 26.0f) : 0);
                    int backgroundPaddingTop = ChatAttachAlertPhotoLayout.this.parentAlert.getBackgroundPaddingTop();
                    int top = (ChatAttachAlertPhotoLayout.this.parentAlert.scrollOffsetY[0] - backgroundPaddingTop) - offset;
                    if (top + backgroundPaddingTop < ActionBar.getCurrentActionBarHeight() && (holder = (RecyclerListView.Holder) ChatAttachAlertPhotoLayout.this.gridView.findViewHolderForAdapterPosition(0)) != null && holder.itemView.getTop() > AndroidUtilities.dp(7.0f)) {
                        ChatAttachAlertPhotoLayout.this.gridView.smoothScrollBy(0, holder.itemView.getTop() - AndroidUtilities.dp(7.0f));
                    }
                }
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, this.itemSize) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.5
            @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.5.1
                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateDyToMakeVisible(View view, int snapPreference) {
                        int dy = super.calculateDyToMakeVisible(view, snapPreference);
                        return dy - (ChatAttachAlertPhotoLayout.this.gridView.getPaddingTop() - AndroidUtilities.dp(7.0f));
                    }

                    @Override // androidx.recyclerview.widget.LinearSmoothScroller
                    public int calculateTimeForDeceleration(int dx) {
                        return super.calculateTimeForDeceleration(dx) * 2;
                    }
                };
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.layoutManager = gridLayoutManager;
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.6
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                if (position == ChatAttachAlertPhotoLayout.this.adapter.itemsCount - 1) {
                    return ChatAttachAlertPhotoLayout.this.layoutManager.getSpanCount();
                }
                return ChatAttachAlertPhotoLayout.this.itemSize + (position % ChatAttachAlertPhotoLayout.this.itemsPerRow != ChatAttachAlertPhotoLayout.this.itemsPerRow + (-1) ? AndroidUtilities.dp(5.0f) : 0);
            }
        });
        this.gridView.setLayoutManager(this.layoutManager);
        this.gridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda6
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ChatAttachAlertPhotoLayout.this.m2479x8c1b5d8a(resourcesProvider, view, i);
            }
        });
        this.gridView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return ChatAttachAlertPhotoLayout.this.m2480xb56fb2cb(view, i);
            }
        });
        RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = new RecyclerViewItemRangeSelector(new RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.7
            @Override // org.telegram.ui.Components.RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate
            public int getItemCount() {
                return ChatAttachAlertPhotoLayout.this.adapter.getItemCount();
            }

            @Override // org.telegram.ui.Components.RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate
            public void setSelected(View view, int index, boolean selected) {
                if (selected != ChatAttachAlertPhotoLayout.this.shouldSelect || !(view instanceof PhotoAttachPhotoCell)) {
                    return;
                }
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                cell.callDelegate();
            }

            @Override // org.telegram.ui.Components.RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate
            public boolean isSelected(int index) {
                MediaController.PhotoEntry entry = ChatAttachAlertPhotoLayout.this.adapter.getPhoto(index);
                return entry != null && ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(entry.imageId));
            }

            @Override // org.telegram.ui.Components.RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate
            public boolean isIndexSelectable(int index) {
                return ChatAttachAlertPhotoLayout.this.adapter.getItemViewType(index) == 0;
            }

            @Override // org.telegram.ui.Components.RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate
            public void onStartStopSelection(boolean start) {
                ChatAttachAlertPhotoLayout.this.alertOnlyOnce = start ? 1 : 0;
                ChatAttachAlertPhotoLayout.this.gridView.hideSelector(true);
            }
        });
        this.itemRangeSelector = recyclerViewItemRangeSelector;
        this.gridView.addOnItemTouchListener(recyclerViewItemRangeSelector);
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context, null, resourcesProvider);
        this.progressView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoPhotos", R.string.NoPhotos));
        this.progressView.setOnTouchListener(null);
        this.progressView.setTextSize(16);
        addView(this.progressView, LayoutHelper.createFrame(-1, -2.0f));
        if (this.loading) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
        final Paint recordPaint = new Paint(1);
        recordPaint.setColor(-2468275);
        TextView textView2 = new TextView(context) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.8
            float alpha = 0.0f;
            boolean isIncr;

            @Override // android.widget.TextView, android.view.View
            protected void onDraw(Canvas canvas) {
                recordPaint.setAlpha((int) ((this.alpha * 130.0f) + 125.0f));
                if (!this.isIncr) {
                    float f = this.alpha - 0.026666667f;
                    this.alpha = f;
                    if (f <= 0.0f) {
                        this.alpha = 0.0f;
                        this.isIncr = true;
                    }
                } else {
                    float f2 = this.alpha + 0.026666667f;
                    this.alpha = f2;
                    if (f2 >= 1.0f) {
                        this.alpha = 1.0f;
                        this.isIncr = false;
                    }
                }
                super.onDraw(canvas);
                canvas.drawCircle(AndroidUtilities.dp(14.0f), getMeasuredHeight() / 2, AndroidUtilities.dp(4.0f), recordPaint);
                invalidate();
            }
        };
        this.recordTime = textView2;
        AndroidUtilities.updateViewVisibilityAnimated(textView2, false, 1.0f, false);
        this.recordTime.setBackgroundResource(R.drawable.system);
        this.recordTime.getBackground().setColorFilter(new PorterDuffColorFilter(1711276032, PorterDuff.Mode.MULTIPLY));
        this.recordTime.setTextSize(1, 15.0f);
        this.recordTime.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.recordTime.setAlpha(0.0f);
        this.recordTime.setTextColor(-1);
        this.recordTime.setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f));
        container.addView(this.recordTime, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 16.0f, 0.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.9
            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                int cy3;
                int cy2;
                int cx2;
                int cy22;
                int cy;
                int cx;
                if (getMeasuredWidth() == AndroidUtilities.dp(126.0f)) {
                    cx = getMeasuredWidth() / 2;
                    cy = getMeasuredHeight() / 2;
                    cy22 = getMeasuredWidth() / 2;
                    cx2 = cy22;
                    cy2 = (cy / 2) + cy + AndroidUtilities.dp(17.0f);
                    cy3 = (cy / 2) - AndroidUtilities.dp(17.0f);
                } else {
                    cx = getMeasuredWidth() / 2;
                    cy = (getMeasuredHeight() / 2) - AndroidUtilities.dp(13.0f);
                    cx2 = (cx / 2) + cx + AndroidUtilities.dp(17.0f);
                    int cx3 = (cx / 2) - AndroidUtilities.dp(17.0f);
                    cy2 = (getMeasuredHeight() / 2) - AndroidUtilities.dp(13.0f);
                    cy3 = cy2;
                    cy22 = cx3;
                }
                int cx32 = getMeasuredHeight();
                int y = (cx32 - ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredHeight()) - AndroidUtilities.dp(12.0f);
                if (getMeasuredWidth() == AndroidUtilities.dp(126.0f)) {
                    ChatAttachAlertPhotoLayout.this.tooltipTextView.layout(cx - (ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredWidth() / 2), getMeasuredHeight(), (ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredWidth() / 2) + cx, getMeasuredHeight() + ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredHeight());
                } else {
                    ChatAttachAlertPhotoLayout.this.tooltipTextView.layout(cx - (ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredWidth() / 2), y, (ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredWidth() / 2) + cx, ChatAttachAlertPhotoLayout.this.tooltipTextView.getMeasuredHeight() + y);
                }
                ChatAttachAlertPhotoLayout.this.shutterButton.layout(cx - (ChatAttachAlertPhotoLayout.this.shutterButton.getMeasuredWidth() / 2), cy - (ChatAttachAlertPhotoLayout.this.shutterButton.getMeasuredHeight() / 2), (ChatAttachAlertPhotoLayout.this.shutterButton.getMeasuredWidth() / 2) + cx, (ChatAttachAlertPhotoLayout.this.shutterButton.getMeasuredHeight() / 2) + cy);
                ChatAttachAlertPhotoLayout.this.switchCameraButton.layout(cx2 - (ChatAttachAlertPhotoLayout.this.switchCameraButton.getMeasuredWidth() / 2), cy2 - (ChatAttachAlertPhotoLayout.this.switchCameraButton.getMeasuredHeight() / 2), (ChatAttachAlertPhotoLayout.this.switchCameraButton.getMeasuredWidth() / 2) + cx2, (ChatAttachAlertPhotoLayout.this.switchCameraButton.getMeasuredHeight() / 2) + cy2);
                for (int a = 0; a < 2; a++) {
                    ChatAttachAlertPhotoLayout.this.flashModeButton[a].layout(cy22 - (ChatAttachAlertPhotoLayout.this.flashModeButton[a].getMeasuredWidth() / 2), cy3 - (ChatAttachAlertPhotoLayout.this.flashModeButton[a].getMeasuredHeight() / 2), (ChatAttachAlertPhotoLayout.this.flashModeButton[a].getMeasuredWidth() / 2) + cy22, (ChatAttachAlertPhotoLayout.this.flashModeButton[a].getMeasuredHeight() / 2) + cy3);
                }
            }
        };
        this.cameraPanel = frameLayout;
        frameLayout.setVisibility(8);
        this.cameraPanel.setAlpha(0.0f);
        container.addView(this.cameraPanel, LayoutHelper.createFrame(-1, 126, 83));
        TextView textView3 = new TextView(context);
        this.counterTextView = textView3;
        textView3.setBackgroundResource(R.drawable.photos_rounded);
        this.counterTextView.setVisibility(8);
        this.counterTextView.setTextColor(-1);
        this.counterTextView.setGravity(17);
        this.counterTextView.setPivotX(0.0f);
        this.counterTextView.setPivotY(0.0f);
        this.counterTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.counterTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.photos_arrow, 0);
        this.counterTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
        this.counterTextView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        container.addView(this.counterTextView, LayoutHelper.createFrame(-2, 38.0f, 51, 0.0f, 0.0f, 0.0f, 116.0f));
        this.counterTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatAttachAlertPhotoLayout.this.m2481xdec4080c(view);
            }
        });
        ZoomControlView zoomControlView = new ZoomControlView(context);
        this.zoomControlView = zoomControlView;
        zoomControlView.setVisibility(8);
        this.zoomControlView.setAlpha(0.0f);
        container.addView(this.zoomControlView, LayoutHelper.createFrame(-2, 50.0f, 51, 0.0f, 0.0f, 0.0f, 116.0f));
        this.zoomControlView.setDelegate(new ZoomControlView.ZoomControlViewDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda9
            @Override // org.telegram.ui.Components.ZoomControlView.ZoomControlViewDelegate
            public final void didSetZoom(float f) {
                ChatAttachAlertPhotoLayout.this.m2482x8185d4d(f);
            }
        });
        ShutterButton shutterButton = new ShutterButton(context);
        this.shutterButton = shutterButton;
        this.cameraPanel.addView(shutterButton, LayoutHelper.createFrame(84, 84, 17));
        this.shutterButton.setDelegate(new AnonymousClass10(container));
        this.shutterButton.setFocusable(true);
        this.shutterButton.setContentDescription(LocaleController.getString("AccDescrShutter", R.string.AccDescrShutter));
        ImageView imageView = new ImageView(context);
        this.switchCameraButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.cameraPanel.addView(this.switchCameraButton, LayoutHelper.createFrame(48, 48, 21));
        this.switchCameraButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda11
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ChatAttachAlertPhotoLayout.this.m2483x316cb28e(view);
            }
        });
        this.switchCameraButton.setContentDescription(LocaleController.getString("AccDescrSwitchCamera", R.string.AccDescrSwitchCamera));
        for (int a = 0; a < 2; a++) {
            this.flashModeButton[a] = new ImageView(context);
            this.flashModeButton[a].setScaleType(ImageView.ScaleType.CENTER);
            this.flashModeButton[a].setVisibility(4);
            this.cameraPanel.addView(this.flashModeButton[a], LayoutHelper.createFrame(48, 48, 51));
            this.flashModeButton[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda12
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChatAttachAlertPhotoLayout.this.m2484x5ac107cf(view);
                }
            });
            ImageView imageView2 = this.flashModeButton[a];
            imageView2.setContentDescription("flash mode " + a);
        }
        TextView textView4 = new TextView(context);
        this.tooltipTextView = textView4;
        textView4.setTextSize(1, 15.0f);
        this.tooltipTextView.setTextColor(-1);
        this.tooltipTextView.setText(LocaleController.getString("TapForVideo", R.string.TapForVideo));
        this.tooltipTextView.setShadowLayer(AndroidUtilities.dp(3.33333f), 0.0f, AndroidUtilities.dp(0.666f), 1275068416);
        this.tooltipTextView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
        this.cameraPanel.addView(this.tooltipTextView, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 16.0f));
        RecyclerListView recyclerListView2 = new RecyclerListView(context, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.13
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerViewIgnoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.cameraPhotoRecyclerView = recyclerListView2;
        recyclerListView2.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView3 = this.cameraPhotoRecyclerView;
        PhotoAttachAdapter photoAttachAdapter2 = new PhotoAttachAdapter(context, false);
        this.cameraAttachAdapter = photoAttachAdapter2;
        recyclerListView3.setAdapter(photoAttachAdapter2);
        this.cameraAttachAdapter.createCache();
        this.cameraPhotoRecyclerView.setClipToPadding(false);
        this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.cameraPhotoRecyclerView.setItemAnimator(null);
        this.cameraPhotoRecyclerView.setLayoutAnimation(null);
        this.cameraPhotoRecyclerView.setOverScrollMode(2);
        this.cameraPhotoRecyclerView.setVisibility(4);
        this.cameraPhotoRecyclerView.setAlpha(0.0f);
        container.addView(this.cameraPhotoRecyclerView, LayoutHelper.createFrame(-1, 80.0f));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 0, false) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.14
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.cameraPhotoLayoutManager = linearLayoutManager;
        this.cameraPhotoRecyclerView.setLayoutManager(linearLayoutManager);
        this.cameraPhotoRecyclerView.setOnItemClickListener(ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda7.INSTANCE);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2478x62c70849(View view) {
        this.dropDownContainer.toggleSubMenu();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2479x8c1b5d8a(Theme.ResourcesProvider resourcesProvider, View view, int position) {
        int type;
        ChatActivity chatActivity;
        if (!this.mediaEnabled || this.parentAlert.baseFragment == null || this.parentAlert.baseFragment.getParentActivity() == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.adapter.needCamera && this.selectedAlbumEntry == this.galleryAlbumEntry && position == 0 && this.noCameraPermissions) {
                try {
                    this.parentAlert.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 18);
                    return;
                } catch (Exception e) {
                    return;
                }
            } else if (this.noGalleryPermissions) {
                try {
                    this.parentAlert.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                    return;
                } catch (Exception e2) {
                    return;
                }
            }
        }
        if (position != 0 || this.selectedAlbumEntry != this.galleryAlbumEntry) {
            if (this.selectedAlbumEntry == this.galleryAlbumEntry) {
                position--;
            }
            ArrayList<Object> arrayList = getAllPhotosArray();
            if (position < 0 || position >= arrayList.size()) {
                return;
            }
            PhotoViewer.getInstance().setParentActivity(this.parentAlert.baseFragment.getParentActivity(), resourcesProvider);
            PhotoViewer.getInstance().setParentAlert(this.parentAlert);
            PhotoViewer.getInstance().setMaxSelectedPhotos(this.parentAlert.maxSelectedPhotos, this.parentAlert.allowOrder);
            if (this.parentAlert.avatarPicker != 0) {
                chatActivity = null;
                type = 1;
            } else if (this.parentAlert.baseFragment instanceof ChatActivity) {
                chatActivity = (ChatActivity) this.parentAlert.baseFragment;
                type = 0;
            } else {
                chatActivity = null;
                type = 4;
            }
            if (!this.parentAlert.delegate.needEnterComment()) {
                AndroidUtilities.hideKeyboard(this.parentAlert.baseFragment.getFragmentView().findFocus());
                AndroidUtilities.hideKeyboard(this.parentAlert.getContainer().findFocus());
            }
            if (selectedPhotos.size() > 0 && selectedPhotosOrder.size() > 0) {
                Object o = selectedPhotos.get(selectedPhotosOrder.get(0));
                if (o instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry1 = (MediaController.PhotoEntry) o;
                    photoEntry1.caption = this.parentAlert.getCommentTextView().getText();
                }
                if (o instanceof MediaController.SearchImage) {
                    MediaController.SearchImage photoEntry12 = (MediaController.SearchImage) o;
                    photoEntry12.caption = this.parentAlert.getCommentTextView().getText();
                }
            }
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, type, false, this.photoViewerProvider, chatActivity);
            if (captionForAllMedia()) {
                PhotoViewer.getInstance().setCaption(this.parentAlert.getCommentTextView().getText());
            }
        } else if (SharedConfig.inappCamera) {
            openCamera(true);
        } else if (this.parentAlert.delegate != null) {
            this.parentAlert.delegate.didPressedButton(0, false, true, 0, false);
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ boolean m2480xb56fb2cb(View view, int position) {
        if (position == 0 && this.selectedAlbumEntry == this.galleryAlbumEntry) {
            if (this.parentAlert.delegate != null) {
                this.parentAlert.delegate.didPressedButton(0, false, true, 0, false);
            }
            return true;
        } else if (view instanceof PhotoAttachPhotoCell) {
            PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
            RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = this.itemRangeSelector;
            boolean z = !cell.isChecked();
            this.shouldSelect = z;
            recyclerViewItemRangeSelector.setIsActive(view, true, position, z);
            return false;
        } else {
            return false;
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2481xdec4080c(View v) {
        if (this.cameraView == null) {
            return;
        }
        openPhotoViewer(null, false, false);
        CameraController.getInstance().stopPreview(this.cameraView.getCameraSession());
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2482x8185d4d(float zoom) {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            this.cameraZoom = zoom;
            cameraView.setZoom(zoom);
        }
        showZoomControls(true, true);
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$10 */
    /* loaded from: classes5.dex */
    public class AnonymousClass10 implements ShutterButton.ShutterButtonDelegate {
        private File outputFile;
        final /* synthetic */ FrameLayout val$container;
        private boolean zoomingWas;

        AnonymousClass10(FrameLayout frameLayout) {
            ChatAttachAlertPhotoLayout.this = this$0;
            this.val$container = frameLayout;
        }

        @Override // org.telegram.ui.Components.ShutterButton.ShutterButtonDelegate
        public boolean shutterLongPressed() {
            if ((ChatAttachAlertPhotoLayout.this.parentAlert.avatarPicker != 2 && !(ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity)) || ChatAttachAlertPhotoLayout.this.takingPhoto || ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment == null || ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment.getParentActivity() == null || ChatAttachAlertPhotoLayout.this.cameraView == null) {
                return false;
            }
            if (Build.VERSION.SDK_INT >= 23 && ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                ChatAttachAlertPhotoLayout.this.requestingPermissions = true;
                ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 21);
                return false;
            }
            for (int a = 0; a < 2; a++) {
                ChatAttachAlertPhotoLayout.this.flashModeButton[a].animate().alpha(0.0f).translationX(AndroidUtilities.dp(30.0f)).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            }
            ChatAttachAlertPhotoLayout.this.switchCameraButton.animate().alpha(0.0f).translationX(-AndroidUtilities.dp(30.0f)).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            ChatAttachAlertPhotoLayout.this.tooltipTextView.animate().alpha(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.outputFile = AndroidUtilities.generateVideoPath((ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) && ((ChatActivity) ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment).isSecretChat());
            AndroidUtilities.updateViewVisibilityAnimated(ChatAttachAlertPhotoLayout.this.recordTime, true);
            ChatAttachAlertPhotoLayout.this.recordTime.setText(AndroidUtilities.formatLongDuration(0));
            ChatAttachAlertPhotoLayout.this.videoRecordTime = 0;
            ChatAttachAlertPhotoLayout.this.videoRecordRunnable = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$10$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertPhotoLayout.AnonymousClass10.this.m2492xddcde7e7();
                }
            };
            AndroidUtilities.lockOrientation(ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment.getParentActivity());
            CameraController.getInstance().recordVideo(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession(), this.outputFile, ChatAttachAlertPhotoLayout.this.parentAlert.avatarPicker != 0, new CameraController.VideoTakeCallback() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$10$$ExternalSyntheticLambda3
                @Override // org.telegram.messenger.camera.CameraController.VideoTakeCallback
                public final void onFinishVideoRecording(String str, long j) {
                    ChatAttachAlertPhotoLayout.AnonymousClass10.this.m2493x6abaff06(str, j);
                }
            }, new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$10$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertPhotoLayout.AnonymousClass10.this.m2494xf7a81625();
                }
            }, ChatAttachAlertPhotoLayout.this.cameraView);
            ChatAttachAlertPhotoLayout.this.shutterButton.setState(ShutterButton.State.RECORDING, true);
            ChatAttachAlertPhotoLayout.this.cameraView.runHaptic();
            return true;
        }

        /* renamed from: lambda$shutterLongPressed$0$org-telegram-ui-Components-ChatAttachAlertPhotoLayout$10 */
        public /* synthetic */ void m2492xddcde7e7() {
            if (ChatAttachAlertPhotoLayout.this.videoRecordRunnable == null) {
                return;
            }
            ChatAttachAlertPhotoLayout.access$2408(ChatAttachAlertPhotoLayout.this);
            ChatAttachAlertPhotoLayout.this.recordTime.setText(AndroidUtilities.formatLongDuration(ChatAttachAlertPhotoLayout.this.videoRecordTime));
            AndroidUtilities.runOnUIThread(ChatAttachAlertPhotoLayout.this.videoRecordRunnable, 1000L);
        }

        /* renamed from: lambda$shutterLongPressed$1$org-telegram-ui-Components-ChatAttachAlertPhotoLayout$10 */
        public /* synthetic */ void m2493x6abaff06(String thumbPath, long duration) {
            if (this.outputFile != null && ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment != null) {
                if (ChatAttachAlertPhotoLayout.this.cameraView != null) {
                    boolean unused = ChatAttachAlertPhotoLayout.mediaFromExternalCamera = false;
                    int width = 0;
                    int height = 0;
                    try {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(new File(thumbPath).getAbsolutePath(), options);
                        width = options.outWidth;
                        height = options.outHeight;
                    } catch (Exception e) {
                    }
                    MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, ChatAttachAlertPhotoLayout.access$3010(), 0L, this.outputFile.getAbsolutePath(), 0, true, width, height, 0L);
                    photoEntry.duration = (int) duration;
                    photoEntry.thumbPath = thumbPath;
                    if (ChatAttachAlertPhotoLayout.this.parentAlert.avatarPicker != 0 && ChatAttachAlertPhotoLayout.this.cameraView.isFrontface()) {
                        photoEntry.cropState = new MediaController.CropState();
                        photoEntry.cropState.mirrored = true;
                        photoEntry.cropState.freeform = false;
                        photoEntry.cropState.lockedAspectRatio = 1.0f;
                    }
                    ChatAttachAlertPhotoLayout.this.openPhotoViewer(photoEntry, false, false);
                }
            }
        }

        /* renamed from: lambda$shutterLongPressed$2$org-telegram-ui-Components-ChatAttachAlertPhotoLayout$10 */
        public /* synthetic */ void m2494xf7a81625() {
            AndroidUtilities.runOnUIThread(ChatAttachAlertPhotoLayout.this.videoRecordRunnable, 1000L);
        }

        @Override // org.telegram.ui.Components.ShutterButton.ShutterButtonDelegate
        public void shutterCancel() {
            File file = this.outputFile;
            if (file != null) {
                file.delete();
                this.outputFile = null;
            }
            ChatAttachAlertPhotoLayout.this.resetRecordState();
            CameraController.getInstance().stopVideoRecording(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession(), true);
        }

        @Override // org.telegram.ui.Components.ShutterButton.ShutterButtonDelegate
        public void shutterReleased() {
            if (!ChatAttachAlertPhotoLayout.this.takingPhoto && ChatAttachAlertPhotoLayout.this.cameraView != null && ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession() != null) {
                boolean z = true;
                if (ChatAttachAlertPhotoLayout.this.shutterButton.getState() == ShutterButton.State.RECORDING) {
                    ChatAttachAlertPhotoLayout.this.resetRecordState();
                    CameraController.getInstance().stopVideoRecording(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession(), false);
                    ChatAttachAlertPhotoLayout.this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
                    return;
                }
                final File cameraFile = AndroidUtilities.generatePicturePath((ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) && ((ChatActivity) ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment).isSecretChat(), null);
                final boolean sameTakePictureOrientation = ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().isSameTakePictureOrientation();
                CameraSession cameraSession = ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession();
                if (!(ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) && ChatAttachAlertPhotoLayout.this.parentAlert.avatarPicker != 2) {
                    z = false;
                }
                cameraSession.setFlipFront(z);
                ChatAttachAlertPhotoLayout.this.takingPhoto = CameraController.getInstance().takePicture(cameraFile, ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession(), new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$10$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlertPhotoLayout.AnonymousClass10.this.m2495x47b7454d(cameraFile, sameTakePictureOrientation);
                    }
                });
                ChatAttachAlertPhotoLayout.this.cameraView.startTakePictureAnimation();
            }
        }

        /* renamed from: lambda$shutterReleased$3$org-telegram-ui-Components-ChatAttachAlertPhotoLayout$10 */
        public /* synthetic */ void m2495x47b7454d(File cameraFile, boolean sameTakePictureOrientation) {
            ChatAttachAlertPhotoLayout.this.takingPhoto = false;
            if (cameraFile != null && ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment != null) {
                int orientation = 0;
                try {
                    ExifInterface ei = new ExifInterface(cameraFile.getAbsolutePath());
                    int exif = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    switch (exif) {
                        case 3:
                            orientation = 180;
                            break;
                        case 6:
                            orientation = 90;
                            break;
                        case 8:
                            orientation = 270;
                            break;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                boolean unused = ChatAttachAlertPhotoLayout.mediaFromExternalCamera = false;
                int width = 0;
                int height = 0;
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(new File(cameraFile.getAbsolutePath()).getAbsolutePath(), options);
                    width = options.outWidth;
                    height = options.outHeight;
                } catch (Exception e2) {
                }
                MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, ChatAttachAlertPhotoLayout.access$3010(), 0L, cameraFile.getAbsolutePath(), orientation, false, width, height, 0L);
                photoEntry.canDeleteAfter = true;
                ChatAttachAlertPhotoLayout.this.openPhotoViewer(photoEntry, sameTakePictureOrientation, false);
            }
        }

        @Override // org.telegram.ui.Components.ShutterButton.ShutterButtonDelegate
        public boolean onTranslationChanged(float x, float y) {
            boolean isPortrait = this.val$container.getWidth() < this.val$container.getHeight();
            float val1 = isPortrait ? x : y;
            float val2 = isPortrait ? y : x;
            if (!this.zoomingWas && Math.abs(val1) > Math.abs(val2)) {
                return ChatAttachAlertPhotoLayout.this.zoomControlView.getTag() == null;
            } else if (val2 < 0.0f) {
                ChatAttachAlertPhotoLayout.this.showZoomControls(true, true);
                ChatAttachAlertPhotoLayout.this.zoomControlView.setZoom((-val2) / AndroidUtilities.dp(200.0f), true);
                this.zoomingWas = true;
                return false;
            } else {
                if (this.zoomingWas) {
                    ChatAttachAlertPhotoLayout.this.zoomControlView.setZoom(0.0f, true);
                }
                if (x == 0.0f && y == 0.0f) {
                    this.zoomingWas = false;
                }
                if (this.zoomingWas) {
                    return false;
                }
                return (x == 0.0f && y == 0.0f) ? false : true;
            }
        }
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2483x316cb28e(View v) {
        CameraView cameraView;
        if (this.takingPhoto || (cameraView = this.cameraView) == null || !cameraView.isInited()) {
            return;
        }
        this.canSaveCameraPreview = false;
        this.cameraView.switchCamera();
        this.cameraView.startSwitchingAnimation();
        ObjectAnimator animator = ObjectAnimator.ofFloat(this.switchCameraButton, View.SCALE_X, 0.0f).setDuration(100L);
        animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.11
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator2) {
                ChatAttachAlertPhotoLayout.this.switchCameraButton.setImageResource((ChatAttachAlertPhotoLayout.this.cameraView == null || !ChatAttachAlertPhotoLayout.this.cameraView.isFrontface()) ? R.drawable.camera_revert2 : R.drawable.camera_revert1);
                ObjectAnimator.ofFloat(ChatAttachAlertPhotoLayout.this.switchCameraButton, View.SCALE_X, 1.0f).setDuration(100L).start();
            }
        });
        animator.start();
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2484x5ac107cf(final View currentImage) {
        CameraView cameraView;
        if (this.flashAnimationInProgress || (cameraView = this.cameraView) == null || !cameraView.isInited() || !this.cameraOpened) {
            return;
        }
        String current = this.cameraView.getCameraSession().getCurrentFlashMode();
        String next = this.cameraView.getCameraSession().getNextFlashMode();
        if (current.equals(next)) {
            return;
        }
        this.cameraView.getCameraSession().setCurrentFlashMode(next);
        this.flashAnimationInProgress = true;
        ImageView[] imageViewArr = this.flashModeButton;
        final ImageView nextImage = imageViewArr[0] == currentImage ? imageViewArr[1] : imageViewArr[0];
        nextImage.setVisibility(0);
        setCameraFlashModeIcon(nextImage, next);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(currentImage, View.TRANSLATION_Y, 0.0f, AndroidUtilities.dp(48.0f)), ObjectAnimator.ofFloat(nextImage, View.TRANSLATION_Y, -AndroidUtilities.dp(48.0f), 0.0f), ObjectAnimator.ofFloat(currentImage, View.ALPHA, 1.0f, 0.0f), ObjectAnimator.ofFloat(nextImage, View.ALPHA, 0.0f, 1.0f));
        animatorSet.setDuration(220L);
        animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.12
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ChatAttachAlertPhotoLayout.this.flashAnimationInProgress = false;
                currentImage.setVisibility(4);
                nextImage.sendAccessibilityEvent(8);
            }
        });
        animatorSet.start();
    }

    public static /* synthetic */ void lambda$new$7(View view, int position) {
        if (view instanceof PhotoAttachPhotoCell) {
            ((PhotoAttachPhotoCell) view).callDelegate();
        }
    }

    public int addToSelectedPhotos(MediaController.PhotoEntry object, int index) {
        Object key = Integer.valueOf(object.imageId);
        if (selectedPhotos.containsKey(key)) {
            selectedPhotos.remove(key);
            int position = selectedPhotosOrder.indexOf(key);
            if (position >= 0) {
                selectedPhotosOrder.remove(position);
            }
            updatePhotosCounter(false);
            updateCheckedPhotoIndices();
            if (index >= 0) {
                object.reset();
                this.photoViewerProvider.updatePhotoAtIndex(index);
            }
            return position;
        }
        selectedPhotos.put(key, object);
        selectedPhotosOrder.add(key);
        updatePhotosCounter(true);
        return -1;
    }

    private void clearSelectedPhotos() {
        if (!selectedPhotos.isEmpty()) {
            for (Map.Entry<Object, Object> entry : selectedPhotos.entrySet()) {
                ((MediaController.PhotoEntry) entry.getValue()).reset();
            }
            selectedPhotos.clear();
            selectedPhotosOrder.clear();
        }
        if (!cameraPhotos.isEmpty()) {
            int size = cameraPhotos.size();
            for (int a = 0; a < size; a++) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) cameraPhotos.get(a);
                new File(photoEntry.path).delete();
                if (photoEntry.imagePath != null) {
                    new File(photoEntry.imagePath).delete();
                }
                if (photoEntry.thumbPath != null) {
                    new File(photoEntry.thumbPath).delete();
                }
            }
            cameraPhotos.clear();
        }
        this.adapter.notifyDataSetChanged();
        this.cameraAttachAdapter.notifyDataSetChanged();
    }

    private void updateAlbumsDropDown() {
        final ArrayList<MediaController.AlbumEntry> albums;
        this.dropDownContainer.removeAllSubItems();
        if (this.mediaEnabled) {
            if ((this.parentAlert.baseFragment instanceof ChatActivity) || this.parentAlert.avatarPicker == 2) {
                albums = MediaController.allMediaAlbums;
            } else {
                albums = MediaController.allPhotoAlbums;
            }
            ArrayList<MediaController.AlbumEntry> arrayList = new ArrayList<>(albums);
            this.dropDownAlbums = arrayList;
            Collections.sort(arrayList, new Comparator() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda3
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return ChatAttachAlertPhotoLayout.lambda$updateAlbumsDropDown$8(albums, (MediaController.AlbumEntry) obj, (MediaController.AlbumEntry) obj2);
                }
            });
        } else {
            this.dropDownAlbums = new ArrayList<>();
        }
        if (this.dropDownAlbums.isEmpty()) {
            this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            return;
        }
        this.dropDown.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.dropDownDrawable, (Drawable) null);
        int N = this.dropDownAlbums.size();
        for (int a = 0; a < N; a++) {
            this.dropDownContainer.addSubItem(a + 10, this.dropDownAlbums.get(a).bucketName);
        }
    }

    public static /* synthetic */ int lambda$updateAlbumsDropDown$8(ArrayList albums, MediaController.AlbumEntry o1, MediaController.AlbumEntry o2) {
        int index1;
        int index2;
        if (o1.bucketId != 0 || o2.bucketId == 0) {
            if ((o1.bucketId != 0 && o2.bucketId == 0) || (index1 = albums.indexOf(o1)) > (index2 = albums.indexOf(o2))) {
                return 1;
            }
            return index1 < index2 ? -1 : 0;
        }
        return -1;
    }

    private boolean processTouchEvent(MotionEvent event) {
        CameraView cameraView;
        if (event == null) {
            return false;
        }
        if ((!this.pressed && event.getActionMasked() == 0) || event.getActionMasked() == 5) {
            this.zoomControlView.getHitRect(this.hitRect);
            if (this.zoomControlView.getTag() != null && this.hitRect.contains((int) event.getX(), (int) event.getY())) {
                return false;
            }
            if (!this.takingPhoto && !this.dragging) {
                if (event.getPointerCount() == 2) {
                    this.pinchStartDistance = (float) Math.hypot(event.getX(1) - event.getX(0), event.getY(1) - event.getY(0));
                    this.zooming = true;
                } else {
                    this.maybeStartDraging = true;
                    this.lastY = event.getY();
                    this.zooming = false;
                }
                this.zoomWas = false;
                this.pressed = true;
            }
        } else if (this.pressed) {
            if (event.getActionMasked() == 2) {
                if (this.zooming && event.getPointerCount() == 2 && !this.dragging) {
                    float newDistance = (float) Math.hypot(event.getX(1) - event.getX(0), event.getY(1) - event.getY(0));
                    if (!this.zoomWas) {
                        if (Math.abs(newDistance - this.pinchStartDistance) >= AndroidUtilities.getPixelsInCM(0.4f, false)) {
                            this.pinchStartDistance = newDistance;
                            this.zoomWas = true;
                        }
                    } else if (this.cameraView != null) {
                        float diff = (newDistance - this.pinchStartDistance) / AndroidUtilities.dp(100.0f);
                        this.pinchStartDistance = newDistance;
                        float f = this.cameraZoom + diff;
                        this.cameraZoom = f;
                        if (f < 0.0f) {
                            this.cameraZoom = 0.0f;
                        } else if (f > 1.0f) {
                            this.cameraZoom = 1.0f;
                        }
                        this.zoomControlView.setZoom(this.cameraZoom, false);
                        this.parentAlert.getSheetContainer().invalidate();
                        this.cameraView.setZoom(this.cameraZoom);
                        showZoomControls(true, true);
                    }
                } else {
                    float newY = event.getY();
                    float dy = newY - this.lastY;
                    if (this.maybeStartDraging) {
                        if (Math.abs(dy) > AndroidUtilities.getPixelsInCM(0.4f, false)) {
                            this.maybeStartDraging = false;
                            this.dragging = true;
                        }
                    } else if (this.dragging && (cameraView = this.cameraView) != null) {
                        cameraView.setTranslationY(cameraView.getTranslationY() + dy);
                        this.lastY = newY;
                        this.zoomControlView.setTag(null);
                        Runnable runnable = this.zoomControlHideRunnable;
                        if (runnable != null) {
                            AndroidUtilities.cancelRunOnUIThread(runnable);
                            this.zoomControlHideRunnable = null;
                        }
                        if (this.cameraPanel.getTag() == null) {
                            this.cameraPanel.setTag(1);
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.playTogether(ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.zoomControlView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.flashModeButton[0], View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.flashModeButton[1], View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, 0.0f));
                            animatorSet.setDuration(220L);
                            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                            animatorSet.start();
                        }
                    }
                }
            } else if (event.getActionMasked() == 3 || event.getActionMasked() == 1 || event.getActionMasked() == 6) {
                this.pressed = false;
                this.zooming = false;
                if (this.dragging) {
                    this.dragging = false;
                    CameraView cameraView2 = this.cameraView;
                    if (cameraView2 != null) {
                        if (Math.abs(cameraView2.getTranslationY()) > this.cameraView.getMeasuredHeight() / 6.0f) {
                            closeCamera(true);
                        } else {
                            AnimatorSet animatorSet2 = new AnimatorSet();
                            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.cameraView, View.TRANSLATION_Y, 0.0f), ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.flashModeButton[0], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.flashModeButton[1], View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, 1.0f));
                            animatorSet2.setDuration(250L);
                            animatorSet2.setInterpolator(this.interpolator);
                            animatorSet2.start();
                            this.cameraPanel.setTag(null);
                        }
                    }
                } else {
                    CameraView cameraView3 = this.cameraView;
                    if (cameraView3 != null && !this.zoomWas) {
                        cameraView3.getLocationOnScreen(this.viewPosition);
                        float viewX = event.getRawX() - this.viewPosition[0];
                        float viewY = event.getRawY() - this.viewPosition[1];
                        this.cameraView.focusToPoint((int) viewX, (int) viewY);
                    }
                }
            }
        }
        return true;
    }

    public void resetRecordState() {
        if (this.parentAlert.baseFragment == null) {
            return;
        }
        for (int a = 0; a < 2; a++) {
            this.flashModeButton[a].animate().alpha(1.0f).translationX(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
        this.switchCameraButton.animate().alpha(1.0f).translationX(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        this.tooltipTextView.animate().alpha(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        AndroidUtilities.updateViewVisibilityAnimated(this.recordTime, false);
        AndroidUtilities.cancelRunOnUIThread(this.videoRecordRunnable);
        this.videoRecordRunnable = null;
        AndroidUtilities.unlockOrientation(this.parentAlert.baseFragment.getParentActivity());
    }

    protected void openPhotoViewer(MediaController.PhotoEntry entry, boolean sameTakePictureOrientation, boolean external) {
        ChatActivity chatActivity;
        int type;
        int index;
        ArrayList<Object> arrayList;
        if (entry != null) {
            cameraPhotos.add(entry);
            selectedPhotos.put(Integer.valueOf(entry.imageId), entry);
            selectedPhotosOrder.add(Integer.valueOf(entry.imageId));
            this.parentAlert.updateCountButton(0);
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
        if (entry != null && !external && cameraPhotos.size() > 1) {
            updatePhotosCounter(false);
            if (this.cameraView != null) {
                this.zoomControlView.setZoom(0.0f, false);
                this.cameraZoom = 0.0f;
                this.cameraView.setZoom(0.0f);
                CameraController.getInstance().startPreview(this.cameraView.getCameraSession());
            }
        } else if (cameraPhotos.isEmpty()) {
        } else {
            this.cancelTakingPhotos = true;
            PhotoViewer.getInstance().setParentActivity(this.parentAlert.baseFragment.getParentActivity(), this.resourcesProvider);
            PhotoViewer.getInstance().setParentAlert(this.parentAlert);
            PhotoViewer.getInstance().setMaxSelectedPhotos(this.parentAlert.maxSelectedPhotos, this.parentAlert.allowOrder);
            if (this.parentAlert.avatarPicker != 0) {
                type = 1;
                chatActivity = null;
            } else if (this.parentAlert.baseFragment instanceof ChatActivity) {
                chatActivity = (ChatActivity) this.parentAlert.baseFragment;
                type = 2;
            } else {
                chatActivity = null;
                type = 5;
            }
            if (this.parentAlert.avatarPicker != 0) {
                arrayList = new ArrayList<>();
                arrayList.add(entry);
                index = 0;
            } else {
                ArrayList<Object> arrayList2 = getAllPhotosArray();
                index = cameraPhotos.size() - 1;
                arrayList = arrayList2;
            }
            PhotoViewer.getInstance().openPhotoForSelect(arrayList, index, type, false, new AnonymousClass15(sameTakePictureOrientation), chatActivity);
        }
    }

    /* renamed from: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$15 */
    /* loaded from: classes5.dex */
    public class AnonymousClass15 extends BasePhotoProvider {
        final /* synthetic */ boolean val$sameTakePictureOrientation;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass15(boolean z) {
            super();
            ChatAttachAlertPhotoLayout.this = this$0;
            this.val$sameTakePictureOrientation = z;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onOpen() {
            ChatAttachAlertPhotoLayout.this.pauseCameraPreview();
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onClose() {
            ChatAttachAlertPhotoLayout.this.resumeCameraPreview();
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean cancelButtonPressed() {
            if (ChatAttachAlertPhotoLayout.this.cameraOpened && ChatAttachAlertPhotoLayout.this.cameraView != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$15$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlertPhotoLayout.AnonymousClass15.this.m2496xf7e45609();
                    }
                }, 1000L);
                ChatAttachAlertPhotoLayout.this.zoomControlView.setZoom(0.0f, false);
                ChatAttachAlertPhotoLayout.this.cameraZoom = 0.0f;
                ChatAttachAlertPhotoLayout.this.cameraView.setZoom(0.0f);
                CameraController.getInstance().startPreview(ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession());
            }
            if (ChatAttachAlertPhotoLayout.this.cancelTakingPhotos && ChatAttachAlertPhotoLayout.cameraPhotos.size() == 1) {
                int size = ChatAttachAlertPhotoLayout.cameraPhotos.size();
                for (int a = 0; a < size; a++) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) ChatAttachAlertPhotoLayout.cameraPhotos.get(a);
                    new File(photoEntry.path).delete();
                    if (photoEntry.imagePath != null) {
                        new File(photoEntry.imagePath).delete();
                    }
                    if (photoEntry.thumbPath != null) {
                        new File(photoEntry.thumbPath).delete();
                    }
                }
                ChatAttachAlertPhotoLayout.cameraPhotos.clear();
                ChatAttachAlertPhotoLayout.selectedPhotosOrder.clear();
                ChatAttachAlertPhotoLayout.selectedPhotos.clear();
                ChatAttachAlertPhotoLayout.this.counterTextView.setVisibility(4);
                ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.setVisibility(8);
                ChatAttachAlertPhotoLayout.this.adapter.notifyDataSetChanged();
                ChatAttachAlertPhotoLayout.this.cameraAttachAdapter.notifyDataSetChanged();
                ChatAttachAlertPhotoLayout.this.parentAlert.updateCountButton(0);
            }
            return true;
        }

        /* renamed from: lambda$cancelButtonPressed$0$org-telegram-ui-Components-ChatAttachAlertPhotoLayout$15 */
        public /* synthetic */ void m2496xf7e45609() {
            if (ChatAttachAlertPhotoLayout.this.cameraView != null && !ChatAttachAlertPhotoLayout.this.parentAlert.isDismissed() && Build.VERSION.SDK_INT >= 21) {
                ChatAttachAlertPhotoLayout.this.cameraView.setSystemUiVisibility(1028);
            }
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void needAddMorePhotos() {
            ChatAttachAlertPhotoLayout.this.cancelTakingPhotos = false;
            if (!ChatAttachAlertPhotoLayout.mediaFromExternalCamera) {
                if (!ChatAttachAlertPhotoLayout.this.cameraOpened) {
                    ChatAttachAlertPhotoLayout.this.openCamera(false);
                }
                ChatAttachAlertPhotoLayout.this.counterTextView.setVisibility(0);
                ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.setVisibility(0);
                ChatAttachAlertPhotoLayout.this.counterTextView.setAlpha(1.0f);
                ChatAttachAlertPhotoLayout.this.updatePhotosCounter(false);
                return;
            }
            ChatAttachAlertPhotoLayout.this.parentAlert.delegate.didPressedButton(0, true, true, 0, false);
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean forceDocument) {
            if (ChatAttachAlertPhotoLayout.cameraPhotos.isEmpty() || ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment == null) {
                return;
            }
            if (videoEditedInfo != null && index >= 0 && index < ChatAttachAlertPhotoLayout.cameraPhotos.size()) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) ChatAttachAlertPhotoLayout.cameraPhotos.get(index);
                photoEntry.editedInfo = videoEditedInfo;
            }
            if (!(ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) || !((ChatActivity) ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment).isSecretChat()) {
                int size = ChatAttachAlertPhotoLayout.cameraPhotos.size();
                for (int a = 0; a < size; a++) {
                    AndroidUtilities.addMediaToGallery(((MediaController.PhotoEntry) ChatAttachAlertPhotoLayout.cameraPhotos.get(a)).path);
                }
            }
            ChatAttachAlertPhotoLayout.this.parentAlert.applyCaption();
            ChatAttachAlertPhotoLayout.this.closeCamera(false);
            ChatAttachAlertPhotoLayout.this.parentAlert.delegate.didPressedButton(forceDocument ? 4 : 8, true, notify, scheduleDate, forceDocument);
            ChatAttachAlertPhotoLayout.cameraPhotos.clear();
            ChatAttachAlertPhotoLayout.selectedPhotosOrder.clear();
            ChatAttachAlertPhotoLayout.selectedPhotos.clear();
            ChatAttachAlertPhotoLayout.this.adapter.notifyDataSetChanged();
            ChatAttachAlertPhotoLayout.this.cameraAttachAdapter.notifyDataSetChanged();
            ChatAttachAlertPhotoLayout.this.parentAlert.dismiss(true);
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean scaleToFill() {
            if (ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment == null || ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment.getParentActivity() == null) {
                return false;
            }
            int locked = Settings.System.getInt(ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment.getParentActivity().getContentResolver(), "accelerometer_rotation", 0);
            return this.val$sameTakePictureOrientation || locked == 1;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willHidePhotoViewer() {
            int count = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(a);
                if (view instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                    cell.showImage();
                    cell.showCheck(true);
                }
            }
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canScrollAway() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canCaptureMorePhotos() {
            return ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos != 1;
        }
    }

    public void showZoomControls(boolean show, boolean animated) {
        if ((this.zoomControlView.getTag() != null && show) || (this.zoomControlView.getTag() == null && !show)) {
            if (show) {
                Runnable runnable = this.zoomControlHideRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda17
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlertPhotoLayout.this.m2491x6cfb1ad2();
                    }
                };
                this.zoomControlHideRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
                return;
            }
            return;
        }
        AnimatorSet animatorSet = this.zoomControlAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.zoomControlView.setTag(show ? 1 : null);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.zoomControlAnimation = animatorSet2;
        animatorSet2.setDuration(180L);
        AnimatorSet animatorSet3 = this.zoomControlAnimation;
        Animator[] animatorArr = new Animator[1];
        ZoomControlView zoomControlView = this.zoomControlView;
        Property property = View.ALPHA;
        float[] fArr = new float[1];
        fArr[0] = show ? 1.0f : 0.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(zoomControlView, property, fArr);
        animatorSet3.playTogether(animatorArr);
        this.zoomControlAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.16
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                ChatAttachAlertPhotoLayout.this.zoomControlAnimation = null;
            }
        });
        this.zoomControlAnimation.start();
        if (show) {
            Runnable runnable3 = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertPhotoLayout.this.m2490x9d102a5a();
                }
            };
            this.zoomControlHideRunnable = runnable3;
            AndroidUtilities.runOnUIThread(runnable3, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }
    }

    /* renamed from: lambda$showZoomControls$9$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2491x6cfb1ad2() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    /* renamed from: lambda$showZoomControls$10$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2490x9d102a5a() {
        showZoomControls(false, true);
        this.zoomControlHideRunnable = null;
    }

    protected void updatePhotosCounter(boolean added) {
        if (this.counterTextView == null || this.parentAlert.avatarPicker != 0) {
            return;
        }
        boolean hasVideo = false;
        boolean hasPhotos = false;
        for (Map.Entry<Object, Object> entry : selectedPhotos.entrySet()) {
            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) entry.getValue();
            if (photoEntry.isVideo) {
                hasVideo = true;
            } else {
                hasPhotos = true;
            }
            if (hasVideo && hasPhotos) {
                break;
            }
        }
        boolean z = true;
        int newSelectedCount = Math.max(1, selectedPhotos.size());
        if (hasVideo && hasPhotos) {
            this.counterTextView.setText(LocaleController.formatPluralString("Media", selectedPhotos.size(), new Object[0]).toUpperCase());
            if (newSelectedCount != this.currentSelectedCount || added) {
                this.parentAlert.selectedTextView.setText(LocaleController.formatPluralString("MediaSelected", newSelectedCount, new Object[0]));
            }
        } else if (hasVideo) {
            this.counterTextView.setText(LocaleController.formatPluralString("Videos", selectedPhotos.size(), new Object[0]).toUpperCase());
            if (newSelectedCount != this.currentSelectedCount || added) {
                this.parentAlert.selectedTextView.setText(LocaleController.formatPluralString("VideosSelected", newSelectedCount, new Object[0]));
            }
        } else {
            this.counterTextView.setText(LocaleController.formatPluralString("Photos", selectedPhotos.size(), new Object[0]).toUpperCase());
            if (newSelectedCount != this.currentSelectedCount || added) {
                this.parentAlert.selectedTextView.setText(LocaleController.formatPluralString("PhotosSelected", newSelectedCount, new Object[0]));
            }
        }
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        if (newSelectedCount <= 1) {
            z = false;
        }
        chatAttachAlert.setCanOpenPreview(z);
        this.currentSelectedCount = newSelectedCount;
    }

    public PhotoAttachPhotoCell getCellForIndex(int index) {
        int count = this.gridView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.gridView.getChildAt(a);
            if (view.getTop() < this.gridView.getMeasuredHeight() - this.parentAlert.getClipLayoutBottom() && (view instanceof PhotoAttachPhotoCell)) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                if (((Integer) cell.getImageView().getTag()).intValue() == index) {
                    return cell;
                }
            }
        }
        return null;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void setCameraFlashModeIcon(ImageView imageView, String mode) {
        char c;
        switch (mode.hashCode()) {
            case 3551:
                if (mode.equals("on")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 109935:
                if (mode.equals("off")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 3005871:
                if (mode.equals("auto")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                imageView.setImageResource(R.drawable.flash_off);
                imageView.setContentDescription(LocaleController.getString("AccDescrCameraFlashOff", R.string.AccDescrCameraFlashOff));
                return;
            case 1:
                imageView.setImageResource(R.drawable.flash_on);
                imageView.setContentDescription(LocaleController.getString("AccDescrCameraFlashOn", R.string.AccDescrCameraFlashOn));
                return;
            case 2:
                imageView.setImageResource(R.drawable.flash_auto);
                imageView.setContentDescription(LocaleController.getString("AccDescrCameraFlashAuto", R.string.AccDescrCameraFlashAuto));
                return;
            default:
                return;
        }
    }

    public void checkCamera(boolean request) {
        PhotoAttachAdapter photoAttachAdapter;
        if (this.parentAlert.baseFragment == null || this.parentAlert.baseFragment.getParentActivity() == null) {
            return;
        }
        boolean old = this.deviceHasGoodCamera;
        boolean old2 = this.noCameraPermissions;
        if (!SharedConfig.inappCamera) {
            this.deviceHasGoodCamera = false;
        } else if (Build.VERSION.SDK_INT >= 23) {
            boolean z = this.parentAlert.baseFragment.getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0;
            this.noCameraPermissions = z;
            if (z) {
                if (request) {
                    try {
                        this.parentAlert.baseFragment.getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE"}, 17);
                    } catch (Exception e) {
                    }
                }
                this.deviceHasGoodCamera = false;
            } else {
                if (request || SharedConfig.hasCameraCache) {
                    CameraController.getInstance().initCamera(null);
                }
                this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
            }
        } else {
            if (request || SharedConfig.hasCameraCache) {
                CameraController.getInstance().initCamera(null);
            }
            this.deviceHasGoodCamera = CameraController.getInstance().isCameraInitied();
        }
        if ((old != this.deviceHasGoodCamera || old2 != this.noCameraPermissions) && (photoAttachAdapter = this.adapter) != null) {
            photoAttachAdapter.notifyDataSetChanged();
        }
        if (this.parentAlert.isShowing() && this.deviceHasGoodCamera && this.parentAlert.baseFragment != null && this.parentAlert.getBackDrawable().getAlpha() != 0 && !this.cameraOpened) {
            showCamera();
        }
    }

    public void openCamera(boolean animated) {
        CameraView cameraView = this.cameraView;
        if (cameraView == null || this.cameraInitAnimation != null || !cameraView.isInited() || this.parentAlert.isDismissed()) {
            return;
        }
        if (this.parentAlert.avatarPicker == 2 || (this.parentAlert.baseFragment instanceof ChatActivity)) {
            this.tooltipTextView.setVisibility(0);
        } else {
            this.tooltipTextView.setVisibility(8);
        }
        if (cameraPhotos.isEmpty()) {
            this.counterTextView.setVisibility(4);
            this.cameraPhotoRecyclerView.setVisibility(8);
        } else {
            this.counterTextView.setVisibility(0);
            this.cameraPhotoRecyclerView.setVisibility(0);
        }
        if (this.parentAlert.commentTextView.isKeyboardVisible() && isFocusable()) {
            this.parentAlert.commentTextView.closeKeyboard();
        }
        this.zoomControlView.setVisibility(0);
        this.zoomControlView.setAlpha(0.0f);
        this.cameraPanel.setVisibility(0);
        this.cameraPanel.setTag(null);
        int[] iArr = this.animateCameraValues;
        iArr[0] = 0;
        int i = this.itemSize;
        iArr[1] = i;
        iArr[2] = i;
        this.additionCloseCameraY = 0.0f;
        this.cameraExpanded = true;
        this.cameraView.setFpsLimit(-1);
        AndroidUtilities.hideKeyboard(this);
        AndroidUtilities.setLightNavigationBar(this.parentAlert.getWindow(), false);
        if (animated) {
            setCameraOpenProgress(0.0f);
            this.cameraAnimationInProgress = true;
            this.animationIndex = NotificationCenter.getInstance(this.parentAlert.currentAccount).setAnimationInProgress(this.animationIndex, null);
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", 0.0f, 1.0f));
            animators.add(ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, 1.0f));
            animators.add(ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, 1.0f));
            animators.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, 1.0f));
            int a = 0;
            while (true) {
                if (a >= 2) {
                    break;
                } else if (this.flashModeButton[a].getVisibility() == 0) {
                    animators.add(ObjectAnimator.ofFloat(this.flashModeButton[a], View.ALPHA, 1.0f));
                    break;
                } else {
                    a++;
                }
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);
            animatorSet.setDuration(350L);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.17
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    NotificationCenter.getInstance(ChatAttachAlertPhotoLayout.this.parentAlert.currentAccount).onAnimationFinish(ChatAttachAlertPhotoLayout.this.animationIndex);
                    ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress = false;
                    if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlertPhotoLayout.this.cameraView != null) {
                        ChatAttachAlertPhotoLayout.this.cameraView.invalidateOutline();
                    } else if (ChatAttachAlertPhotoLayout.this.cameraView != null) {
                        ChatAttachAlertPhotoLayout.this.cameraView.invalidate();
                    }
                    if (ChatAttachAlertPhotoLayout.this.cameraOpened) {
                        ChatAttachAlertPhotoLayout.this.parentAlert.delegate.onCameraOpened();
                    }
                    if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlertPhotoLayout.this.cameraView != null) {
                        ChatAttachAlertPhotoLayout.this.cameraView.setSystemUiVisibility(1028);
                    }
                }
            });
            animatorSet.start();
        } else {
            setCameraOpenProgress(1.0f);
            this.cameraPanel.setAlpha(1.0f);
            this.counterTextView.setAlpha(1.0f);
            this.cameraPhotoRecyclerView.setAlpha(1.0f);
            int a2 = 0;
            while (true) {
                if (a2 >= 2) {
                    break;
                } else if (this.flashModeButton[a2].getVisibility() == 0) {
                    this.flashModeButton[a2].setAlpha(1.0f);
                    break;
                } else {
                    a2++;
                }
            }
            this.parentAlert.delegate.onCameraOpened();
            if (Build.VERSION.SDK_INT >= 21) {
                this.cameraView.setSystemUiVisibility(1028);
            }
        }
        this.cameraOpened = true;
        this.cameraView.setImportantForAccessibility(2);
        if (Build.VERSION.SDK_INT >= 19) {
            this.gridView.setImportantForAccessibility(4);
        }
    }

    public void loadGalleryPhotos() {
        MediaController.AlbumEntry albumEntry;
        if ((this.parentAlert.baseFragment instanceof ChatActivity) || this.parentAlert.avatarPicker == 2) {
            albumEntry = MediaController.allMediaAlbumEntry;
        } else {
            albumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (albumEntry == null && Build.VERSION.SDK_INT >= 21) {
            MediaController.loadGalleryPhotosAlbums(0);
        }
    }

    public void showCamera() {
        if (this.parentAlert.paused || !this.mediaEnabled) {
            return;
        }
        if (this.cameraView == null) {
            CameraView cameraView = new CameraView(this.parentAlert.baseFragment.getParentActivity(), this.parentAlert.openWithFrontFaceCamera) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.18
                @Override // org.telegram.messenger.camera.CameraView, android.view.ViewGroup, android.view.View
                public void dispatchDraw(Canvas canvas) {
                    if (Build.VERSION.SDK_INT < 21) {
                        if (ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress) {
                            AndroidUtilities.rectTmp.set(ChatAttachAlertPhotoLayout.this.animationClipLeft + (ChatAttachAlertPhotoLayout.this.cameraViewOffsetX * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress)), ChatAttachAlertPhotoLayout.this.animationClipTop + (ChatAttachAlertPhotoLayout.this.cameraViewOffsetY * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress)), ChatAttachAlertPhotoLayout.this.animationClipRight, ChatAttachAlertPhotoLayout.this.animationClipBottom);
                        } else if (!ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress && !ChatAttachAlertPhotoLayout.this.cameraOpened) {
                            AndroidUtilities.rectTmp.set(ChatAttachAlertPhotoLayout.this.cameraViewOffsetX, ChatAttachAlertPhotoLayout.this.cameraViewOffsetY, getMeasuredWidth(), getMeasuredHeight());
                        } else {
                            AndroidUtilities.rectTmp.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                        }
                        canvas.save();
                        canvas.clipRect(AndroidUtilities.rectTmp);
                        super.dispatchDraw(canvas);
                        canvas.restore();
                        return;
                    }
                    super.dispatchDraw(canvas);
                }
            };
            this.cameraView = cameraView;
            cameraView.setRecordFile(AndroidUtilities.generateVideoPath((this.parentAlert.baseFragment instanceof ChatActivity) && ((ChatActivity) this.parentAlert.baseFragment).isSecretChat()));
            this.cameraView.setFocusable(true);
            this.cameraView.setFpsLimit(30);
            if (Build.VERSION.SDK_INT >= 21) {
                this.cameraView.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.19
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        if (ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress) {
                            AndroidUtilities.rectTmp.set(ChatAttachAlertPhotoLayout.this.animationClipLeft + (ChatAttachAlertPhotoLayout.this.cameraViewOffsetX * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress)), ChatAttachAlertPhotoLayout.this.animationClipTop + (ChatAttachAlertPhotoLayout.this.cameraViewOffsetY * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress)), ChatAttachAlertPhotoLayout.this.animationClipRight, ChatAttachAlertPhotoLayout.this.animationClipBottom);
                            outline.setRect((int) AndroidUtilities.rectTmp.left, (int) AndroidUtilities.rectTmp.top, (int) AndroidUtilities.rectTmp.right, (int) AndroidUtilities.rectTmp.bottom);
                        } else if (!ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress && !ChatAttachAlertPhotoLayout.this.cameraOpened) {
                            int rad = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                            outline.setRoundRect((int) ChatAttachAlertPhotoLayout.this.cameraViewOffsetX, (int) ChatAttachAlertPhotoLayout.this.cameraViewOffsetY, view.getMeasuredWidth() + rad, view.getMeasuredHeight() + rad, rad);
                        } else {
                            outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                        }
                    }
                });
                this.cameraView.setClipToOutline(true);
            }
            this.cameraView.setContentDescription(LocaleController.getString("AccDescrInstantCamera", R.string.AccDescrInstantCamera));
            FrameLayout container = this.parentAlert.getContainer();
            CameraView cameraView2 = this.cameraView;
            int i = this.itemSize;
            container.addView(cameraView2, 1, new FrameLayout.LayoutParams(i, i));
            this.cameraView.setDelegate(new CameraView.CameraViewDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.20
                @Override // org.telegram.messenger.camera.CameraView.CameraViewDelegate
                public void onCameraInit() {
                    String current = ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().getCurrentFlashMode();
                    String next = ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().getNextFlashMode();
                    int i2 = 4;
                    if (current.equals(next)) {
                        for (int a = 0; a < 2; a++) {
                            ChatAttachAlertPhotoLayout.this.flashModeButton[a].setVisibility(4);
                            ChatAttachAlertPhotoLayout.this.flashModeButton[a].setAlpha(0.0f);
                            ChatAttachAlertPhotoLayout.this.flashModeButton[a].setTranslationY(0.0f);
                        }
                    } else {
                        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                        chatAttachAlertPhotoLayout.setCameraFlashModeIcon(chatAttachAlertPhotoLayout.flashModeButton[0], ChatAttachAlertPhotoLayout.this.cameraView.getCameraSession().getCurrentFlashMode());
                        int a2 = 0;
                        while (a2 < 2) {
                            ChatAttachAlertPhotoLayout.this.flashModeButton[a2].setVisibility(a2 == 0 ? 0 : 4);
                            ChatAttachAlertPhotoLayout.this.flashModeButton[a2].setAlpha((a2 != 0 || !ChatAttachAlertPhotoLayout.this.cameraOpened) ? 0.0f : 1.0f);
                            ChatAttachAlertPhotoLayout.this.flashModeButton[a2].setTranslationY(0.0f);
                            a2++;
                        }
                    }
                    ChatAttachAlertPhotoLayout.this.switchCameraButton.setImageResource(ChatAttachAlertPhotoLayout.this.cameraView.isFrontface() ? R.drawable.camera_revert1 : R.drawable.camera_revert2);
                    ImageView imageView = ChatAttachAlertPhotoLayout.this.switchCameraButton;
                    if (ChatAttachAlertPhotoLayout.this.cameraView.hasFrontFaceCamera()) {
                        i2 = 0;
                    }
                    imageView.setVisibility(i2);
                    if (!ChatAttachAlertPhotoLayout.this.cameraOpened) {
                        ChatAttachAlertPhotoLayout.this.cameraInitAnimation = new AnimatorSet();
                        ChatAttachAlertPhotoLayout.this.cameraInitAnimation.playTogether(ObjectAnimator.ofFloat(ChatAttachAlertPhotoLayout.this.cameraView, View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(ChatAttachAlertPhotoLayout.this.cameraIcon, View.ALPHA, 0.0f, 1.0f));
                        ChatAttachAlertPhotoLayout.this.cameraInitAnimation.setDuration(180L);
                        ChatAttachAlertPhotoLayout.this.cameraInitAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.20.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (animation.equals(ChatAttachAlertPhotoLayout.this.cameraInitAnimation)) {
                                    ChatAttachAlertPhotoLayout.this.canSaveCameraPreview = true;
                                    ChatAttachAlertPhotoLayout.this.cameraInitAnimation = null;
                                    if (!ChatAttachAlertPhotoLayout.this.isHidden) {
                                        int count = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
                                        for (int a3 = 0; a3 < count; a3++) {
                                            View child = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(a3);
                                            if (child instanceof PhotoAttachCameraCell) {
                                                child.setVisibility(4);
                                                return;
                                            }
                                        }
                                    }
                                }
                            }

                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationCancel(Animator animation) {
                                ChatAttachAlertPhotoLayout.this.cameraInitAnimation = null;
                            }
                        });
                        ChatAttachAlertPhotoLayout.this.cameraInitAnimation.start();
                    }
                }
            });
            if (this.cameraIcon == null) {
                FrameLayout frameLayout = new FrameLayout(this.parentAlert.baseFragment.getParentActivity()) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.21
                    @Override // android.view.View
                    protected void onDraw(Canvas canvas) {
                        int w = ChatAttachAlertPhotoLayout.this.cameraDrawable.getIntrinsicWidth();
                        int h = ChatAttachAlertPhotoLayout.this.cameraDrawable.getIntrinsicHeight();
                        int x = (ChatAttachAlertPhotoLayout.this.itemSize - w) / 2;
                        int y = (ChatAttachAlertPhotoLayout.this.itemSize - h) / 2;
                        if (ChatAttachAlertPhotoLayout.this.cameraViewOffsetY != 0.0f) {
                            y = (int) (y - ChatAttachAlertPhotoLayout.this.cameraViewOffsetY);
                        }
                        ChatAttachAlertPhotoLayout.this.cameraDrawable.setBounds(x, y, x + w, y + h);
                        ChatAttachAlertPhotoLayout.this.cameraDrawable.draw(canvas);
                    }
                };
                this.cameraIcon = frameLayout;
                frameLayout.setWillNotDraw(false);
                this.cameraIcon.setClipChildren(true);
            }
            FrameLayout container2 = this.parentAlert.getContainer();
            FrameLayout frameLayout2 = this.cameraIcon;
            int i2 = this.itemSize;
            container2.addView(frameLayout2, 2, new FrameLayout.LayoutParams(i2, i2));
            float f = 1.0f;
            this.cameraView.setAlpha(this.mediaEnabled ? 1.0f : 0.2f);
            this.cameraView.setEnabled(this.mediaEnabled);
            FrameLayout frameLayout3 = this.cameraIcon;
            if (!this.mediaEnabled) {
                f = 0.2f;
            }
            frameLayout3.setAlpha(f);
            this.cameraIcon.setEnabled(this.mediaEnabled);
            if (this.isHidden) {
                this.cameraView.setVisibility(8);
                this.cameraIcon.setVisibility(8);
            }
            checkCameraViewPosition();
            invalidate();
        }
        ZoomControlView zoomControlView = this.zoomControlView;
        if (zoomControlView != null) {
            zoomControlView.setZoom(0.0f, false);
            this.cameraZoom = 0.0f;
        }
        this.cameraView.setTranslationX(this.cameraViewLocation[0]);
        this.cameraView.setTranslationY(this.cameraViewLocation[1] + this.currentPanTranslationY);
        this.cameraIcon.setTranslationX(this.cameraViewLocation[0]);
        this.cameraIcon.setTranslationY(this.cameraViewLocation[1] + this.cameraViewOffsetY + this.currentPanTranslationY);
    }

    public void hideCamera(boolean async) {
        if (!this.deviceHasGoodCamera || this.cameraView == null) {
            return;
        }
        saveLastCameraBitmap();
        int count = this.gridView.getChildCount();
        int a = 0;
        while (true) {
            if (a >= count) {
                break;
            }
            View child = this.gridView.getChildAt(a);
            if (!(child instanceof PhotoAttachCameraCell)) {
                a++;
            } else {
                child.setVisibility(0);
                ((PhotoAttachCameraCell) child).updateBitmap();
                break;
            }
        }
        this.cameraView.destroy(async, null);
        AnimatorSet animatorSet = this.cameraInitAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.cameraInitAnimation = null;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertPhotoLayout.this.m2477x2e5b293a();
            }
        }, 300L);
        this.canSaveCameraPreview = false;
    }

    /* renamed from: lambda$hideCamera$11$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2477x2e5b293a() {
        this.parentAlert.getContainer().removeView(this.cameraView);
        this.parentAlert.getContainer().removeView(this.cameraIcon);
        this.cameraView = null;
        this.cameraIcon = null;
    }

    private void saveLastCameraBitmap() {
        if (!this.canSaveCameraPreview) {
            return;
        }
        try {
            TextureView textureView = this.cameraView.getTextureView();
            Bitmap bitmap = textureView.getBitmap();
            if (bitmap != null) {
                Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), this.cameraView.getMatrix(), true);
                bitmap.recycle();
                Bitmap lastBitmap = Bitmap.createScaledBitmap(newBitmap, 80, (int) (newBitmap.getHeight() / (newBitmap.getWidth() / 80.0f)), true);
                if (lastBitmap != null) {
                    if (lastBitmap != newBitmap) {
                        newBitmap.recycle();
                    }
                    Utilities.blurBitmap(lastBitmap, 7, 1, lastBitmap.getWidth(), lastBitmap.getHeight(), lastBitmap.getRowBytes());
                    File file = new File(ApplicationLoader.getFilesDirFixed(), "cthumb.jpg");
                    FileOutputStream stream = new FileOutputStream(file);
                    lastBitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                    lastBitmap.recycle();
                }
            }
        } catch (Throwable th) {
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(18:25|(1:27)(1:28)|(1:34)(1:33)|(5:36|(5:38|(1:40)|41|(1:43)|(1:47))(1:48)|49|(1:53)|54)|(2:57|(13:59|61|92|90|62|(2:64|65)|66|91|75|94|76|79|103))|60|61|92|90|62|(0)|66|91|75|94|76|79|103) */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x018c, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:68:0x018d, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:71:0x0197, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:72:0x0198, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:73:0x019b, code lost:
        if (r7 != null) goto L74;
     */
    /* JADX WARN: Code restructure failed: missing block: B:74:0x019d, code lost:
        r7.release();
     */
    /* JADX WARN: Code restructure failed: missing block: B:77:0x01d8, code lost:
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:78:0x01d9, code lost:
        org.telegram.messenger.FileLog.e(r0);
     */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0178 A[Catch: all -> 0x0193, Exception -> 0x0197, TRY_LEAVE, TryCatch #3 {Exception -> 0x0197, blocks: (B:62:0x0167, B:64:0x0178), top: B:90:0x0167, outer: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onActivityResultFragment(int r29, android.content.Intent r30, java.lang.String r31) {
        /*
            Method dump skipped, instructions count: 562
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.onActivityResultFragment(int, android.content.Intent, java.lang.String):void");
    }

    public void closeCamera(boolean animated) {
        if (this.takingPhoto || this.cameraView == null) {
            return;
        }
        int[] iArr = this.animateCameraValues;
        int i = this.itemSize;
        iArr[1] = i;
        iArr[2] = i;
        Runnable runnable = this.zoomControlHideRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.zoomControlHideRunnable = null;
        }
        AndroidUtilities.setLightNavigationBar(this.parentAlert.getWindow(), ((double) AndroidUtilities.computePerceivedBrightness(getThemedColor(Theme.key_windowBackgroundGray))) > 0.721d);
        if (animated) {
            this.additionCloseCameraY = this.cameraView.getTranslationY();
            this.cameraAnimationInProgress = true;
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofFloat(this, "cameraOpenProgress", 0.0f));
            animators.add(ObjectAnimator.ofFloat(this.cameraPanel, View.ALPHA, 0.0f));
            animators.add(ObjectAnimator.ofFloat(this.zoomControlView, View.ALPHA, 0.0f));
            animators.add(ObjectAnimator.ofFloat(this.counterTextView, View.ALPHA, 0.0f));
            animators.add(ObjectAnimator.ofFloat(this.cameraPhotoRecyclerView, View.ALPHA, 0.0f));
            int a = 0;
            while (true) {
                if (a >= 2) {
                    break;
                } else if (this.flashModeButton[a].getVisibility() == 0) {
                    animators.add(ObjectAnimator.ofFloat(this.flashModeButton[a], View.ALPHA, 0.0f));
                    break;
                } else {
                    a++;
                }
            }
            this.animationIndex = NotificationCenter.getInstance(this.parentAlert.currentAccount).setAnimationInProgress(this.animationIndex, null);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animators);
            animatorSet.setDuration(220L);
            animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.22
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    NotificationCenter.getInstance(ChatAttachAlertPhotoLayout.this.parentAlert.currentAccount).onAnimationFinish(ChatAttachAlertPhotoLayout.this.animationIndex);
                    ChatAttachAlertPhotoLayout.this.cameraExpanded = false;
                    ChatAttachAlertPhotoLayout.this.setCameraOpenProgress(0.0f);
                    ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress = false;
                    if (Build.VERSION.SDK_INT >= 21 && ChatAttachAlertPhotoLayout.this.cameraView != null) {
                        ChatAttachAlertPhotoLayout.this.cameraView.invalidateOutline();
                    } else if (ChatAttachAlertPhotoLayout.this.cameraView != null) {
                        ChatAttachAlertPhotoLayout.this.cameraView.invalidate();
                    }
                    ChatAttachAlertPhotoLayout.this.cameraOpened = false;
                    if (ChatAttachAlertPhotoLayout.this.cameraPanel != null) {
                        ChatAttachAlertPhotoLayout.this.cameraPanel.setVisibility(8);
                    }
                    if (ChatAttachAlertPhotoLayout.this.zoomControlView != null) {
                        ChatAttachAlertPhotoLayout.this.zoomControlView.setVisibility(8);
                        ChatAttachAlertPhotoLayout.this.zoomControlView.setTag(null);
                    }
                    if (ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView != null) {
                        ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.setVisibility(8);
                    }
                    if (ChatAttachAlertPhotoLayout.this.cameraView != null) {
                        ChatAttachAlertPhotoLayout.this.cameraView.setFpsLimit(30);
                        if (Build.VERSION.SDK_INT >= 21) {
                            ChatAttachAlertPhotoLayout.this.cameraView.setSystemUiVisibility(1024);
                        }
                    }
                }
            });
            animatorSet.start();
        } else {
            this.cameraExpanded = false;
            setCameraOpenProgress(0.0f);
            this.animateCameraValues[0] = 0;
            setCameraOpenProgress(0.0f);
            this.cameraPanel.setAlpha(0.0f);
            this.cameraPanel.setVisibility(8);
            this.zoomControlView.setAlpha(0.0f);
            this.zoomControlView.setTag(null);
            this.zoomControlView.setVisibility(8);
            this.cameraPhotoRecyclerView.setAlpha(0.0f);
            this.counterTextView.setAlpha(0.0f);
            this.cameraPhotoRecyclerView.setVisibility(8);
            int a2 = 0;
            while (true) {
                if (a2 >= 2) {
                    break;
                } else if (this.flashModeButton[a2].getVisibility() == 0) {
                    this.flashModeButton[a2].setAlpha(0.0f);
                    break;
                } else {
                    a2++;
                }
            }
            this.cameraOpened = false;
            this.cameraView.setFpsLimit(30);
            if (Build.VERSION.SDK_INT >= 21) {
                this.cameraView.setSystemUiVisibility(1024);
            }
        }
        this.cameraView.setImportantForAccessibility(0);
        if (Build.VERSION.SDK_INT >= 19) {
            this.gridView.setImportantForAccessibility(0);
        }
    }

    public void setCameraOpenProgress(float value) {
        int cameraViewW;
        int cameraViewH;
        if (this.cameraView == null) {
            return;
        }
        this.cameraOpenProgress = value;
        int[] iArr = this.animateCameraValues;
        float startWidth = iArr[1];
        float startHeight = iArr[2];
        boolean isPortrait = AndroidUtilities.displaySize.x < AndroidUtilities.displaySize.y;
        float endWidth = (this.parentAlert.getContainer().getWidth() - this.parentAlert.getLeftInset()) - this.parentAlert.getRightInset();
        float endHeight = this.parentAlert.getContainer().getHeight();
        float[] fArr = this.cameraViewLocation;
        float fromX = fArr[0];
        float fromY = fArr[1];
        float toY = this.additionCloseCameraY;
        if (value == 0.0f) {
            this.cameraIcon.setTranslationX(fArr[0]);
            this.cameraIcon.setTranslationY(this.cameraViewLocation[1] + this.cameraViewOffsetY);
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
        float textureStartHeight = this.cameraView.getTextureHeight(startWidth, startHeight);
        float textureEndHeight = this.cameraView.getTextureHeight(endWidth, endHeight);
        float fromScale = textureStartHeight / textureEndHeight;
        float fromScaleY = startHeight / endHeight;
        float fromScaleX = startWidth / endWidth;
        if (this.cameraExpanded) {
            cameraViewW = (int) endWidth;
            float s = ((1.0f - value) * fromScale) + value;
            this.cameraView.getTextureView().setScaleX(s);
            this.cameraView.getTextureView().setScaleY(s);
            float sX = ((1.0f - value) * fromScaleX) + value;
            float sY = ((1.0f - value) * fromScaleY) + value;
            float scaleOffsetY = ((1.0f - sY) * endHeight) / 2.0f;
            float scaleOffsetX = ((1.0f - sX) * endWidth) / 2.0f;
            this.cameraView.setTranslationX((((1.0f - value) * fromX) + (0.0f * value)) - scaleOffsetX);
            this.cameraView.setTranslationY((((1.0f - value) * fromY) + (toY * value)) - scaleOffsetY);
            this.animationClipTop = ((1.0f - value) * fromY) - this.cameraView.getTranslationY();
            this.animationClipBottom = (((fromY + startHeight) * (1.0f - value)) - this.cameraView.getTranslationY()) + (endHeight * value);
            this.animationClipLeft = ((1.0f - value) * fromX) - this.cameraView.getTranslationX();
            this.animationClipRight = (((fromX + startWidth) * (1.0f - value)) - this.cameraView.getTranslationX()) + (endWidth * value);
            cameraViewH = (int) endHeight;
        } else {
            cameraViewW = (int) startWidth;
            cameraViewH = (int) startHeight;
            this.cameraView.getTextureView().setScaleX(1.0f);
            this.cameraView.getTextureView().setScaleY(1.0f);
            this.animationClipTop = 0.0f;
            this.animationClipBottom = endHeight;
            this.animationClipLeft = 0.0f;
            this.animationClipRight = endWidth;
            this.cameraView.setTranslationX(fromX);
            this.cameraView.setTranslationY(fromY);
        }
        if (value <= 0.5f) {
            this.cameraIcon.setAlpha(1.0f - (value / 0.5f));
        } else {
            this.cameraIcon.setAlpha(0.0f);
        }
        if (layoutParams.width != cameraViewW || layoutParams.height != cameraViewH) {
            layoutParams.width = cameraViewW;
            layoutParams.height = cameraViewH;
            this.cameraView.requestLayout();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.cameraView.invalidateOutline();
        } else {
            this.cameraView.invalidate();
        }
    }

    public float getCameraOpenProgress() {
        return this.cameraOpenProgress;
    }

    protected void checkCameraViewPosition() {
        float newCameraViewOffsetY;
        TextView textView;
        RecyclerView.ViewHolder holder;
        if (Build.VERSION.SDK_INT >= 21) {
            CameraView cameraView = this.cameraView;
            if (cameraView != null) {
                cameraView.invalidateOutline();
            }
            RecyclerView.ViewHolder holder2 = this.gridView.findViewHolderForAdapterPosition(this.itemsPerRow - 1);
            if (holder2 != null) {
                holder2.itemView.invalidateOutline();
            }
            if ((!this.adapter.needCamera || !this.deviceHasGoodCamera || this.selectedAlbumEntry != this.galleryAlbumEntry) && (holder = this.gridView.findViewHolderForAdapterPosition(0)) != null) {
                holder.itemView.invalidateOutline();
            }
        }
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != null) {
            cameraView2.invalidate();
        }
        if (Build.VERSION.SDK_INT >= 23 && (textView = this.recordTime) != null) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
            params.topMargin = getRootWindowInsets() == null ? AndroidUtilities.dp(16.0f) : getRootWindowInsets().getSystemWindowInsetTop() + AndroidUtilities.dp(2.0f);
        }
        if (!this.deviceHasGoodCamera) {
            return;
        }
        int count = this.gridView.getChildCount();
        int a = 0;
        while (true) {
            if (a >= count) {
                break;
            }
            View child = this.gridView.getChildAt(a);
            if (!(child instanceof PhotoAttachCameraCell)) {
                a++;
            } else if (Build.VERSION.SDK_INT < 19 || child.isAttachedToWindow()) {
                float topLocal = child.getY() + this.gridView.getY() + getY();
                float top = this.parentAlert.getSheetContainer().getY() + topLocal;
                float left = child.getX() + this.gridView.getX() + getX() + this.parentAlert.getSheetContainer().getX();
                if (Build.VERSION.SDK_INT >= 23) {
                    left -= getRootWindowInsets().getSystemWindowInsetLeft();
                }
                float maxY = ((Build.VERSION.SDK_INT < 21 || this.parentAlert.inBubbleMode) ? 0 : AndroidUtilities.statusBarHeight) + ActionBar.getCurrentActionBarHeight();
                if (topLocal < maxY) {
                    newCameraViewOffsetY = maxY - topLocal;
                } else {
                    newCameraViewOffsetY = 0.0f;
                }
                if (newCameraViewOffsetY != this.cameraViewOffsetY) {
                    this.cameraViewOffsetY = newCameraViewOffsetY;
                    if (this.cameraView != null) {
                        if (Build.VERSION.SDK_INT >= 21) {
                            this.cameraView.invalidateOutline();
                        } else {
                            this.cameraView.invalidate();
                        }
                    }
                    FrameLayout frameLayout = this.cameraIcon;
                    if (frameLayout != null) {
                        frameLayout.invalidate();
                    }
                }
                int containerHeight = this.parentAlert.getSheetContainer().getMeasuredHeight();
                float maxY2 = (int) ((containerHeight - this.parentAlert.buttonsRecyclerView.getMeasuredHeight()) + this.parentAlert.buttonsRecyclerView.getTranslationY());
                if (child.getMeasuredHeight() + topLocal > maxY2) {
                    this.cameraViewOffsetBottomY = (child.getMeasuredHeight() + topLocal) - maxY2;
                } else {
                    this.cameraViewOffsetBottomY = 0.0f;
                }
                float[] fArr = this.cameraViewLocation;
                fArr[0] = left;
                fArr[1] = top;
                applyCameraViewPosition();
                return;
            }
        }
        if (this.cameraViewOffsetY != 0.0f || this.cameraViewOffsetX != 0.0f) {
            this.cameraViewOffsetX = 0.0f;
            this.cameraViewOffsetY = 0.0f;
            if (this.cameraView != null) {
                if (Build.VERSION.SDK_INT >= 21) {
                    this.cameraView.invalidateOutline();
                } else {
                    this.cameraView.invalidate();
                }
            }
            FrameLayout frameLayout2 = this.cameraIcon;
            if (frameLayout2 != null) {
                frameLayout2.invalidate();
            }
        }
        this.cameraViewLocation[0] = AndroidUtilities.dp(-400.0f);
        this.cameraViewLocation[1] = 0.0f;
        applyCameraViewPosition();
    }

    private void applyCameraViewPosition() {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            if (!this.cameraOpened) {
                cameraView.setTranslationX(this.cameraViewLocation[0]);
                this.cameraView.setTranslationY(this.cameraViewLocation[1] + this.currentPanTranslationY);
            }
            this.cameraIcon.setTranslationX(this.cameraViewLocation[0]);
            this.cameraIcon.setTranslationY(this.cameraViewLocation[1] + this.cameraViewOffsetY + this.currentPanTranslationY);
            int finalWidth = this.itemSize;
            int finalHeight = this.itemSize;
            if (!this.cameraOpened) {
                this.cameraView.setClipTop((int) this.cameraViewOffsetY);
                this.cameraView.setClipBottom((int) this.cameraViewOffsetBottomY);
                final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.cameraView.getLayoutParams();
                if (layoutParams.height != finalHeight || layoutParams.width != finalWidth) {
                    layoutParams.width = finalWidth;
                    layoutParams.height = finalHeight;
                    this.cameraView.setLayoutParams(layoutParams);
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda18
                        @Override // java.lang.Runnable
                        public final void run() {
                            ChatAttachAlertPhotoLayout.this.m2475x2e05399b(layoutParams);
                        }
                    });
                }
            }
            int i = this.itemSize;
            int finalWidth2 = (int) (i - this.cameraViewOffsetX);
            int finalHeight2 = (int) ((i - this.cameraViewOffsetY) - this.cameraViewOffsetBottomY);
            final FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.cameraIcon.getLayoutParams();
            if (layoutParams2.height != finalHeight2 || layoutParams2.width != finalWidth2) {
                layoutParams2.width = finalWidth2;
                layoutParams2.height = finalHeight2;
                this.cameraIcon.setLayoutParams(layoutParams2);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatAttachAlertPhotoLayout.this.m2476x57598edc(layoutParams2);
                    }
                });
            }
        }
    }

    /* renamed from: lambda$applyCameraViewPosition$12$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2475x2e05399b(FrameLayout.LayoutParams layoutParamsFinal) {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            cameraView.setLayoutParams(layoutParamsFinal);
        }
    }

    /* renamed from: lambda$applyCameraViewPosition$13$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2476x57598edc(FrameLayout.LayoutParams layoutParamsFinal) {
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.setLayoutParams(layoutParamsFinal);
        }
    }

    public HashMap<Object, Object> getSelectedPhotos() {
        return selectedPhotos;
    }

    public ArrayList<Object> getSelectedPhotosOrder() {
        return selectedPhotosOrder;
    }

    public void updateSelected(HashMap<Object, Object> newSelectedPhotos, ArrayList<Object> newPhotosOrder, boolean updateLayout) {
        selectedPhotos.clear();
        selectedPhotos.putAll(newSelectedPhotos);
        selectedPhotosOrder.clear();
        selectedPhotosOrder.addAll(newPhotosOrder);
        if (updateLayout) {
            updatePhotosCounter(false);
            updateCheckedPhotoIndices();
            int count = this.gridView.getChildCount();
            for (int i = 0; i < count; i++) {
                View child = this.gridView.getChildAt(i);
                if (child instanceof PhotoAttachPhotoCell) {
                    int position = this.gridView.getChildAdapterPosition(child);
                    if (this.adapter.needCamera && this.selectedAlbumEntry == this.galleryAlbumEntry) {
                        position--;
                    }
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) child;
                    if (this.parentAlert.avatarPicker != 0) {
                        cell.getCheckBox().setVisibility(8);
                    }
                    MediaController.PhotoEntry photoEntry = getPhotoEntryAtPosition(position);
                    if (photoEntry != null) {
                        boolean z = true;
                        boolean z2 = this.adapter.needCamera && this.selectedAlbumEntry == this.galleryAlbumEntry;
                        if (position != this.adapter.getItemCount() - 1) {
                            z = false;
                        }
                        cell.setPhotoEntry(photoEntry, z2, z);
                        if (!(this.parentAlert.baseFragment instanceof ChatActivity) || !this.parentAlert.allowOrder) {
                            cell.setChecked(-1, selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                        } else {
                            cell.setChecked(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)), selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                        }
                    }
                }
            }
        }
    }

    public void checkStorage() {
        if (this.noGalleryPermissions && Build.VERSION.SDK_INT >= 23) {
            boolean z = this.parentAlert.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0;
            this.noGalleryPermissions = z;
            if (!z) {
                loadGalleryPhotos();
            }
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void scrollToTop() {
        this.gridView.smoothScrollToPosition(0);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int needsActionBar() {
        return 1;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onMenuItemClick(int id) {
        if ((id == 0 || id == 1) && this.parentAlert.maxSelectedPhotos > 0 && selectedPhotosOrder.size() > 1 && (this.parentAlert.baseFragment instanceof ChatActivity)) {
            ChatActivity chatActivity = (ChatActivity) this.parentAlert.baseFragment;
            TLRPC.Chat chat = chatActivity.getCurrentChat();
            if (chat != null && !ChatObject.hasAdminRights(chat) && chat.slowmode_enabled) {
                AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSendError", R.string.SlowmodeSendError), this.resourcesProvider).show();
                return;
            }
        }
        if (id == 0) {
            if (this.parentAlert.editingMessageObject == null && (this.parentAlert.baseFragment instanceof ChatActivity) && ((ChatActivity) this.parentAlert.baseFragment).isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.parentAlert.baseFragment).getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda4
                    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlertPhotoLayout.this.m2486x320ee4a7(z, i);
                    }
                }, this.resourcesProvider);
                return;
            }
            this.parentAlert.applyCaption();
            this.parentAlert.delegate.didPressedButton(7, false, true, 0, false);
        } else if (id == 1) {
            if (this.parentAlert.editingMessageObject == null && (this.parentAlert.baseFragment instanceof ChatActivity) && ((ChatActivity) this.parentAlert.baseFragment).isInScheduleMode()) {
                AlertsCreator.createScheduleDatePickerDialog(getContext(), ((ChatActivity) this.parentAlert.baseFragment).getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda5
                    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                    public final void didSelectDate(boolean z, int i) {
                        ChatAttachAlertPhotoLayout.this.m2487x5b6339e8(z, i);
                    }
                }, this.resourcesProvider);
                return;
            }
            this.parentAlert.applyCaption();
            this.parentAlert.delegate.didPressedButton(4, true, true, 0, false);
        } else if (id == 2) {
            try {
                if (!(this.parentAlert.baseFragment instanceof ChatActivity) && this.parentAlert.avatarPicker != 2) {
                    Intent photoPickerIntent = new Intent("android.intent.action.PICK");
                    photoPickerIntent.setType("image/*");
                    if (this.parentAlert.avatarPicker == 0) {
                        this.parentAlert.baseFragment.startActivityForResult(photoPickerIntent, 1);
                    } else {
                        this.parentAlert.baseFragment.startActivityForResult(photoPickerIntent, 14);
                    }
                    this.parentAlert.dismiss(true);
                }
                Intent videoPickerIntent = new Intent();
                videoPickerIntent.setType("video/*");
                videoPickerIntent.setAction("android.intent.action.GET_CONTENT");
                videoPickerIntent.putExtra("android.intent.extra.sizeLimit", FileLoader.DEFAULT_MAX_FILE_SIZE);
                Intent photoPickerIntent2 = new Intent("android.intent.action.PICK");
                photoPickerIntent2.setType("image/*");
                Intent chooserIntent = Intent.createChooser(photoPickerIntent2, null);
                chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", new Intent[]{videoPickerIntent});
                if (this.parentAlert.avatarPicker == 0) {
                    this.parentAlert.baseFragment.startActivityForResult(chooserIntent, 1);
                } else {
                    this.parentAlert.baseFragment.startActivityForResult(chooserIntent, 14);
                }
                this.parentAlert.dismiss(true);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (id == 3) {
            this.parentAlert.updatePhotoPreview(true);
        } else if (id >= 10) {
            MediaController.AlbumEntry albumEntry = this.dropDownAlbums.get(id - 10);
            this.selectedAlbumEntry = albumEntry;
            if (albumEntry == this.galleryAlbumEntry) {
                this.dropDown.setText(LocaleController.getString("ChatGallery", R.string.ChatGallery));
            } else {
                this.dropDown.setText(albumEntry.bucketName);
            }
            this.adapter.notifyDataSetChanged();
            this.cameraAttachAdapter.notifyDataSetChanged();
            this.layoutManager.scrollToPositionWithOffset(0, (-this.gridView.getPaddingTop()) + AndroidUtilities.dp(7.0f));
        }
    }

    /* renamed from: lambda$onMenuItemClick$14$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2486x320ee4a7(boolean notify, int scheduleDate) {
        this.parentAlert.applyCaption();
        this.parentAlert.delegate.didPressedButton(7, false, notify, scheduleDate, false);
    }

    /* renamed from: lambda$onMenuItemClick$15$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2487x5b6339e8(boolean notify, int scheduleDate) {
        this.parentAlert.applyCaption();
        this.parentAlert.delegate.didPressedButton(4, true, notify, scheduleDate, false);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getSelectedItemsCount() {
        return selectedPhotosOrder.size();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onSelectedItemsCountChanged(int count) {
        if (count <= 1 || this.parentAlert.editingMessageObject != null) {
            this.parentAlert.selectedMenuItem.hideSubItem(0);
            if (count == 0) {
                this.parentAlert.selectedMenuItem.showSubItem(2);
                this.parentAlert.selectedMenuItem.hideSubItem(1);
            } else {
                this.parentAlert.selectedMenuItem.showSubItem(1);
            }
        } else {
            this.parentAlert.selectedMenuItem.showSubItem(0);
        }
        if (count != 0) {
            this.parentAlert.selectedMenuItem.hideSubItem(2);
        }
        if (count > 1) {
            this.parentAlert.selectedMenuItem.showSubItem(3);
        } else {
            this.parentAlert.selectedMenuItem.hideSubItem(3);
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void applyCaption(CharSequence text) {
        for (int a = 0; a < selectedPhotosOrder.size(); a++) {
            if (a == 0) {
                Object o = selectedPhotos.get(selectedPhotosOrder.get(a));
                if (o instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry1 = (MediaController.PhotoEntry) o;
                    photoEntry1.caption = text;
                    photoEntry1.entities = MediaDataController.getInstance(UserConfig.selectedAccount).getEntities(new CharSequence[]{text}, false);
                } else if (o instanceof MediaController.SearchImage) {
                    MediaController.SearchImage photoEntry12 = (MediaController.SearchImage) o;
                    photoEntry12.caption = text;
                    photoEntry12.entities = MediaDataController.getInstance(UserConfig.selectedAccount).getEntities(new CharSequence[]{text}, false);
                }
            }
        }
    }

    public boolean captionForAllMedia() {
        int captionCount = 0;
        for (int a = 0; a < selectedPhotosOrder.size(); a++) {
            Object o = selectedPhotos.get(selectedPhotosOrder.get(a));
            CharSequence caption = null;
            if (o instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry1 = (MediaController.PhotoEntry) o;
                caption = photoEntry1.caption;
            } else if (o instanceof MediaController.SearchImage) {
                MediaController.SearchImage photoEntry12 = (MediaController.SearchImage) o;
                caption = photoEntry12.caption;
            }
            if (!TextUtils.isEmpty(caption)) {
                captionCount++;
            }
        }
        return captionCount <= 1;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.cameraInitied);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.albumsDidLoad);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onPause() {
        ShutterButton shutterButton = this.shutterButton;
        if (shutterButton == null) {
            return;
        }
        if (!this.requestingPermissions) {
            if (this.cameraView != null && shutterButton.getState() == ShutterButton.State.RECORDING) {
                resetRecordState();
                CameraController.getInstance().stopVideoRecording(this.cameraView.getCameraSession(), false);
                this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
            }
            if (this.cameraOpened) {
                closeCamera(false);
            }
            hideCamera(true);
            return;
        }
        if (this.cameraView != null && shutterButton.getState() == ShutterButton.State.RECORDING) {
            this.shutterButton.setState(ShutterButton.State.DEFAULT, true);
        }
        this.requestingPermissions = false;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onResume() {
        if (this.parentAlert.isShowing() && !this.parentAlert.isDismissed()) {
            checkCamera(false);
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getListTopPadding() {
        return this.gridView.getPaddingTop();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int getCurrentItemTop() {
        if (this.gridView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.gridView;
            int paddingTop = recyclerListView.getPaddingTop();
            this.currentItemTop = paddingTop;
            recyclerListView.setTopGlowOffset(paddingTop);
            this.progressView.setTranslationY(0.0f);
            return Integer.MAX_VALUE;
        }
        View child = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = AndroidUtilities.dp(7.0f);
        if (top >= AndroidUtilities.dp(7.0f) && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
        }
        this.progressView.setTranslationY(((((getMeasuredHeight() - newOffset) - AndroidUtilities.dp(50.0f)) - this.progressView.getMeasuredHeight()) / 2) + newOffset);
        this.gridView.setTopGlowOffset(newOffset);
        this.currentItemTop = newOffset;
        return newOffset;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(56.0f);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void checkColors() {
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
        boolean z = this.forceDarkTheme;
        String str = Theme.key_voipgroup_actionBarItems;
        String textColor = z ? str : Theme.key_dialogTextBlack;
        Theme.setDrawableColor(this.cameraDrawable, getThemedColor(Theme.key_dialogCameraIcon));
        this.progressView.setTextColor(getThemedColor(Theme.key_emptyListPlaceholder));
        this.gridView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        RecyclerView.ViewHolder holder = this.gridView.findViewHolderForAdapterPosition(0);
        if (holder != null && (holder.itemView instanceof PhotoAttachCameraCell)) {
            ((PhotoAttachCameraCell) holder.itemView).getImageView().setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogCameraIcon), PorterDuff.Mode.MULTIPLY));
        }
        this.dropDown.setTextColor(getThemedColor(textColor));
        this.dropDownContainer.setPopupItemsColor(getThemedColor(this.forceDarkTheme ? str : Theme.key_actionBarDefaultSubmenuItem), false);
        ActionBarMenuItem actionBarMenuItem = this.dropDownContainer;
        if (!this.forceDarkTheme) {
            str = Theme.key_actionBarDefaultSubmenuItem;
        }
        actionBarMenuItem.setPopupItemsColor(getThemedColor(str), true);
        this.dropDownContainer.redrawPopup(getThemedColor(this.forceDarkTheme ? Theme.key_voipgroup_actionBarUnscrolled : Theme.key_actionBarDefaultSubmenuBackground));
        Theme.setDrawableColor(this.dropDownDrawable, getThemedColor(textColor));
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onInit(boolean hasMedia) {
        this.mediaEnabled = hasMedia;
        CameraView cameraView = this.cameraView;
        float f = 1.0f;
        if (cameraView != null) {
            cameraView.setAlpha(hasMedia ? 1.0f : 0.2f);
            this.cameraView.setEnabled(this.mediaEnabled);
        }
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            if (!this.mediaEnabled) {
                f = 0.2f;
            }
            frameLayout.setAlpha(f);
            this.cameraIcon.setEnabled(this.mediaEnabled);
        }
        boolean z = true;
        if ((this.parentAlert.baseFragment instanceof ChatActivity) && this.parentAlert.avatarPicker == 0) {
            this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
            if (this.mediaEnabled) {
                this.progressView.setText(LocaleController.getString("NoPhotos", R.string.NoPhotos));
                this.progressView.setLottie(0, 0, 0);
            } else {
                TLRPC.Chat chat = ((ChatActivity) this.parentAlert.baseFragment).getCurrentChat();
                this.progressView.setLottie(R.raw.media_forbidden, 150, 150);
                if (ChatObject.isActionBannedByDefault(chat, 7)) {
                    this.progressView.setText(LocaleController.getString("GlobalAttachMediaRestricted", R.string.GlobalAttachMediaRestricted));
                } else if (AndroidUtilities.isBannedForever(chat.banned_rights)) {
                    this.progressView.setText(LocaleController.formatString("AttachMediaRestrictedForever", R.string.AttachMediaRestrictedForever, new Object[0]));
                } else {
                    this.progressView.setText(LocaleController.formatString("AttachMediaRestricted", R.string.AttachMediaRestricted, LocaleController.formatDateForBan(chat.banned_rights.until_date)));
                }
            }
        } else if (this.parentAlert.avatarPicker == 2) {
            this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
        } else {
            this.galleryAlbumEntry = MediaController.allPhotosAlbumEntry;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.parentAlert.baseFragment.getParentActivity().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
                z = false;
            }
            this.noGalleryPermissions = z;
        }
        if (this.galleryAlbumEntry != null) {
            for (int a = 0; a < Math.min(100, this.galleryAlbumEntry.photos.size()); a++) {
                MediaController.PhotoEntry photoEntry = this.galleryAlbumEntry.photos.get(a);
                photoEntry.reset();
            }
        }
        clearSelectedPhotos();
        updatePhotosCounter(false);
        this.cameraPhotoLayoutManager.scrollToPositionWithOffset(0, MediaController.VIDEO_BITRATE_480);
        this.layoutManager.scrollToPositionWithOffset(0, MediaController.VIDEO_BITRATE_480);
        this.dropDown.setText(LocaleController.getString("ChatGallery", R.string.ChatGallery));
        MediaController.AlbumEntry albumEntry = this.galleryAlbumEntry;
        this.selectedAlbumEntry = albumEntry;
        if (albumEntry != null) {
            this.loading = false;
            EmptyTextProgressView emptyTextProgressView = this.progressView;
            if (emptyTextProgressView != null) {
                emptyTextProgressView.showTextView();
            }
        }
        updateAlbumsDropDown();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    boolean canScheduleMessages() {
        boolean hasTtl = false;
        Iterator<Map.Entry<Object, Object>> it = selectedPhotos.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Map.Entry<Object, Object> entry = it.next();
            Object object = entry.getValue();
            if (object instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) object;
                if (photoEntry.ttl != 0) {
                    hasTtl = true;
                    break;
                }
            } else if (object instanceof MediaController.SearchImage) {
                MediaController.SearchImage searchImage = (MediaController.SearchImage) object;
                if (searchImage.ttl != 0) {
                    hasTtl = true;
                    break;
                }
            } else {
                continue;
            }
        }
        if (hasTtl) {
            return false;
        }
        return true;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onButtonsTranslationYUpdated() {
        checkCameraViewPosition();
        invalidate();
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        if (this.parentAlert.getSheetAnimationType() == 1) {
            float scale = (translationY / 40.0f) * (-0.1f);
            int N = this.gridView.getChildCount();
            for (int a = 0; a < N; a++) {
                View child = this.gridView.getChildAt(a);
                if (child instanceof PhotoAttachCameraCell) {
                    PhotoAttachCameraCell cell = (PhotoAttachCameraCell) child;
                    cell.getImageView().setScaleX(scale + 1.0f);
                    cell.getImageView().setScaleY(1.0f + scale);
                } else if (child instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell cell2 = (PhotoAttachPhotoCell) child;
                    cell2.getCheckBox().setScaleX(scale + 1.0f);
                    cell2.getCheckBox().setScaleY(1.0f + scale);
                }
            }
        }
        super.setTranslationY(translationY);
        this.parentAlert.getSheetContainer().invalidate();
        invalidate();
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onShow(final ChatAttachAlert.AttachAlertLayout previousLayout) {
        ViewPropertyAnimator viewPropertyAnimator = this.headerAnimator;
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.cancel();
        }
        this.dropDownContainer.setVisibility(0);
        if (!(previousLayout instanceof ChatAttachAlertPhotoLayoutPreview)) {
            clearSelectedPhotos();
            this.dropDown.setAlpha(1.0f);
        } else {
            ViewPropertyAnimator interpolator = this.dropDown.animate().alpha(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            this.headerAnimator = interpolator;
            interpolator.start();
        }
        this.parentAlert.actionBar.setTitle("");
        this.layoutManager.scrollToPositionWithOffset(0, 0);
        if (previousLayout instanceof ChatAttachAlertPhotoLayoutPreview) {
            Runnable setScrollY = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertPhotoLayout.this.m2489x9282ac2a(previousLayout);
                }
            };
            this.gridView.post(setScrollY);
        }
        checkCameraViewPosition();
        resumeCameraPreview();
    }

    /* renamed from: lambda$onShow$16$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2489x9282ac2a(ChatAttachAlert.AttachAlertLayout previousLayout) {
        int currentItemTop = previousLayout.getCurrentItemTop();
        int paddingTop = previousLayout.getListTopPadding();
        this.gridView.scrollBy(0, currentItemTop > AndroidUtilities.dp(8.0f) ? paddingTop - currentItemTop : paddingTop);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onShown() {
        this.isHidden = false;
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            cameraView.setVisibility(0);
        }
        FrameLayout frameLayout = this.cameraIcon;
        if (frameLayout != null) {
            frameLayout.setVisibility(0);
        }
        if (this.cameraView != null) {
            int count = this.gridView.getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View child = this.gridView.getChildAt(a);
                if (!(child instanceof PhotoAttachCameraCell)) {
                    a++;
                } else {
                    child.setVisibility(4);
                    break;
                }
            }
        }
        if (this.checkCameraWhenShown) {
            this.checkCameraWhenShown = false;
            checkCamera(true);
        }
    }

    public void setCheckCameraWhenShown(boolean checkCameraWhenShown) {
        this.checkCameraWhenShown = checkCameraWhenShown;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onHideShowProgress(float progress) {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            cameraView.setAlpha(progress);
            this.cameraIcon.setAlpha(progress);
            if (progress != 0.0f && this.cameraView.getVisibility() != 0) {
                this.cameraView.setVisibility(0);
                this.cameraIcon.setVisibility(0);
            } else if (progress == 0.0f && this.cameraView.getVisibility() != 4) {
                this.cameraView.setVisibility(4);
                this.cameraIcon.setVisibility(4);
            }
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onHide() {
        this.isHidden = true;
        int count = this.gridView.getChildCount();
        int a = 0;
        while (true) {
            if (a >= count) {
                break;
            }
            View child = this.gridView.getChildAt(a);
            if (!(child instanceof PhotoAttachCameraCell)) {
                a++;
            } else {
                PhotoAttachCameraCell cell = (PhotoAttachCameraCell) child;
                child.setVisibility(0);
                saveLastCameraBitmap();
                cell.updateBitmap();
                break;
            }
        }
        ViewPropertyAnimator viewPropertyAnimator = this.headerAnimator;
        if (viewPropertyAnimator != null) {
            viewPropertyAnimator.cancel();
        }
        ViewPropertyAnimator withEndAction = this.dropDown.animate().alpha(0.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.EASE_BOTH).withEndAction(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                ChatAttachAlertPhotoLayout.this.m2485x1e517ac6();
            }
        });
        this.headerAnimator = withEndAction;
        withEndAction.start();
        pauseCameraPreview();
    }

    /* renamed from: lambda$onHide$17$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2485x1e517ac6() {
        this.dropDownContainer.setVisibility(8);
    }

    public void pauseCameraPreview() {
        CameraSession cameraSession;
        try {
            CameraView cameraView = this.cameraView;
            if (cameraView != null && (cameraSession = cameraView.getCameraSession()) != null) {
                CameraController.getInstance().stopPreview(cameraSession);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void resumeCameraPreview() {
        CameraSession cameraSession;
        try {
            CameraView cameraView = this.cameraView;
            if (cameraView != null && (cameraSession = cameraView.getCameraSession()) != null) {
                CameraController.getInstance().startPreview(cameraSession);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onHidden() {
        CameraView cameraView = this.cameraView;
        if (cameraView != null) {
            cameraView.setVisibility(8);
            this.cameraIcon.setVisibility(8);
        }
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.lastNotifyWidth != right - left) {
            this.lastNotifyWidth = right - left;
            PhotoAttachAdapter photoAttachAdapter = this.adapter;
            if (photoAttachAdapter != null) {
                photoAttachAdapter.notifyDataSetChanged();
            }
        }
        super.onLayout(changed, left, top, right, bottom);
        checkCameraViewPosition();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onPreMeasure(int availableWidth, int availableHeight) {
        int paddingTop;
        this.ignoreLayout = true;
        if (AndroidUtilities.isTablet()) {
            this.itemsPerRow = 4;
        } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            this.itemsPerRow = 4;
        } else {
            this.itemsPerRow = 3;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
        int dp = ((availableWidth - AndroidUtilities.dp(12.0f)) - AndroidUtilities.dp(10.0f)) / this.itemsPerRow;
        this.itemSize = dp;
        if (this.lastItemSize != dp) {
            this.lastItemSize = dp;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    ChatAttachAlertPhotoLayout.this.m2488x4439fb2e();
                }
            });
        }
        this.layoutManager.setSpanCount(Math.max(1, (this.itemSize * this.itemsPerRow) + (AndroidUtilities.dp(5.0f) * (this.itemsPerRow - 1))));
        int rows = (int) Math.ceil((this.adapter.getItemCount() - 1) / this.itemsPerRow);
        int contentSize = (this.itemSize * rows) + ((rows - 1) * AndroidUtilities.dp(5.0f));
        int newSize = Math.max(0, ((availableHeight - contentSize) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(60.0f));
        if (this.gridExtraSpace != newSize) {
            this.gridExtraSpace = newSize;
            this.adapter.notifyDataSetChanged();
        }
        if (!AndroidUtilities.isTablet() && AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
            paddingTop = (int) (availableHeight / 3.5f);
        } else {
            int paddingTop2 = availableHeight / 5;
            paddingTop = paddingTop2 * 2;
        }
        int paddingTop3 = paddingTop - AndroidUtilities.dp(52.0f);
        if (paddingTop3 < 0) {
            paddingTop3 = 0;
        }
        if (this.gridView.getPaddingTop() != paddingTop3) {
            this.gridView.setPadding(AndroidUtilities.dp(6.0f), paddingTop3, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(48.0f));
        }
        this.dropDown.setTextSize((AndroidUtilities.isTablet() || AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y) ? 20.0f : 18.0f);
        this.ignoreLayout = false;
    }

    /* renamed from: lambda$onPreMeasure$18$org-telegram-ui-Components-ChatAttachAlertPhotoLayout */
    public /* synthetic */ void m2488x4439fb2e() {
        this.adapter.notifyDataSetChanged();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    boolean canDismissWithTouchOutside() {
        return !this.cameraOpened;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public void onContainerTranslationUpdated(float currentPanTranslationY) {
        this.currentPanTranslationY = currentPanTranslationY;
        checkCameraViewPosition();
        invalidate();
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onOpenAnimationEnd() {
        checkCamera(true);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    void onDismissWithButtonClick(int item) {
        hideCamera((item == 0 || item == 2) ? false : true);
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public boolean onDismiss() {
        if (this.cameraAnimationInProgress) {
            return true;
        }
        if (this.cameraOpened) {
            closeCamera(true);
            return true;
        }
        hideCamera(true);
        return false;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public boolean onSheetKeyDown(int keyCode, KeyEvent event) {
        if (this.cameraOpened) {
            if (keyCode == 24 || keyCode == 25 || keyCode == 79 || keyCode == 85) {
                this.shutterButton.getDelegate().shutterReleased();
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public boolean onContainerViewTouchEvent(MotionEvent event) {
        if (this.cameraAnimationInProgress) {
            return true;
        }
        if (this.cameraOpened) {
            return processTouchEvent(event);
        }
        return false;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public boolean onCustomMeasure(View view, int width, int height) {
        boolean isPortrait = width < height;
        FrameLayout frameLayout = this.cameraIcon;
        if (view == frameLayout) {
            frameLayout.measure(View.MeasureSpec.makeMeasureSpec(this.itemSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) ((this.itemSize - this.cameraViewOffsetBottomY) - this.cameraViewOffsetY), C.BUFFER_FLAG_ENCRYPTED));
            return true;
        }
        CameraView cameraView = this.cameraView;
        if (view == cameraView) {
            if (this.cameraOpened && !this.cameraAnimationInProgress) {
                cameraView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(this.parentAlert.getBottomInset() + height, C.BUFFER_FLAG_ENCRYPTED));
                return true;
            }
        } else {
            FrameLayout frameLayout2 = this.cameraPanel;
            if (view == frameLayout2) {
                if (isPortrait) {
                    frameLayout2.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), C.BUFFER_FLAG_ENCRYPTED));
                } else {
                    frameLayout2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(126.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
                }
                return true;
            }
            ZoomControlView zoomControlView = this.zoomControlView;
            if (view == zoomControlView) {
                if (isPortrait) {
                    zoomControlView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), C.BUFFER_FLAG_ENCRYPTED));
                } else {
                    zoomControlView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
                }
                return true;
            }
            RecyclerListView recyclerListView = this.cameraPhotoRecyclerView;
            if (view == recyclerListView) {
                this.cameraPhotoRecyclerViewIgnoreLayout = true;
                if (isPortrait) {
                    recyclerListView.measure(View.MeasureSpec.makeMeasureSpec(width, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), C.BUFFER_FLAG_ENCRYPTED));
                    if (this.cameraPhotoLayoutManager.getOrientation() != 0) {
                        this.cameraPhotoRecyclerView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
                        this.cameraPhotoLayoutManager.setOrientation(0);
                        this.cameraAttachAdapter.notifyDataSetChanged();
                    }
                } else {
                    recyclerListView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
                    if (this.cameraPhotoLayoutManager.getOrientation() != 1) {
                        this.cameraPhotoRecyclerView.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
                        this.cameraPhotoLayoutManager.setOrientation(1);
                        this.cameraAttachAdapter.notifyDataSetChanged();
                    }
                }
                this.cameraPhotoRecyclerViewIgnoreLayout = false;
                return true;
            }
        }
        return false;
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public boolean onCustomLayout(View view, int left, int top, int right, int bottom) {
        int cx;
        int cy;
        int width = right - left;
        int height = bottom - top;
        boolean isPortrait = width < height;
        if (view == this.cameraPanel) {
            if (isPortrait) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.cameraPanel.layout(0, bottom - AndroidUtilities.dp(222.0f), width, bottom - AndroidUtilities.dp(96.0f));
                } else {
                    this.cameraPanel.layout(0, bottom - AndroidUtilities.dp(126.0f), width, bottom);
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.cameraPanel.layout(right - AndroidUtilities.dp(222.0f), 0, right - AndroidUtilities.dp(96.0f), height);
            } else {
                this.cameraPanel.layout(right - AndroidUtilities.dp(126.0f), 0, right, height);
            }
            return true;
        } else if (view == this.zoomControlView) {
            if (isPortrait) {
                if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                    this.zoomControlView.layout(0, bottom - AndroidUtilities.dp(310.0f), width, bottom - AndroidUtilities.dp(260.0f));
                } else {
                    this.zoomControlView.layout(0, bottom - AndroidUtilities.dp(176.0f), width, bottom - AndroidUtilities.dp(126.0f));
                }
            } else if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                this.zoomControlView.layout(right - AndroidUtilities.dp(310.0f), 0, right - AndroidUtilities.dp(260.0f), height);
            } else {
                this.zoomControlView.layout(right - AndroidUtilities.dp(176.0f), 0, right - AndroidUtilities.dp(126.0f), height);
            }
            return true;
        } else {
            TextView textView = this.counterTextView;
            if (view == textView) {
                if (isPortrait) {
                    cx = (width - textView.getMeasuredWidth()) / 2;
                    cy = bottom - AndroidUtilities.dp(167.0f);
                    this.counterTextView.setRotation(0.0f);
                    if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                        cy -= AndroidUtilities.dp(96.0f);
                    }
                } else {
                    cx = right - AndroidUtilities.dp(167.0f);
                    cy = (height / 2) + (this.counterTextView.getMeasuredWidth() / 2);
                    this.counterTextView.setRotation(-90.0f);
                    if (this.cameraPhotoRecyclerView.getVisibility() == 0) {
                        cx -= AndroidUtilities.dp(96.0f);
                    }
                }
                TextView textView2 = this.counterTextView;
                textView2.layout(cx, cy, textView2.getMeasuredWidth() + cx, this.counterTextView.getMeasuredHeight() + cy);
                return true;
            } else if (view != this.cameraPhotoRecyclerView) {
                return false;
            } else {
                if (!isPortrait) {
                    int cx2 = (left + width) - AndroidUtilities.dp(88.0f);
                    view.layout(cx2, 0, view.getMeasuredWidth() + cx2, view.getMeasuredHeight());
                } else {
                    int cy2 = height - AndroidUtilities.dp(88.0f);
                    view.layout(0, cy2, view.getMeasuredWidth(), view.getMeasuredHeight() + cy2);
                }
                return true;
            }
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.albumsDidLoad) {
            if (this.adapter != null) {
                if ((this.parentAlert.baseFragment instanceof ChatActivity) || this.parentAlert.avatarPicker == 2) {
                    this.galleryAlbumEntry = MediaController.allMediaAlbumEntry;
                } else {
                    this.galleryAlbumEntry = MediaController.allPhotosAlbumEntry;
                }
                if (this.selectedAlbumEntry == null) {
                    this.selectedAlbumEntry = this.galleryAlbumEntry;
                } else {
                    int a = 0;
                    while (true) {
                        if (a >= MediaController.allMediaAlbums.size()) {
                            break;
                        }
                        MediaController.AlbumEntry entry = MediaController.allMediaAlbums.get(a);
                        if (entry.bucketId != this.selectedAlbumEntry.bucketId || entry.videoOnly != this.selectedAlbumEntry.videoOnly) {
                            a++;
                        } else {
                            this.selectedAlbumEntry = entry;
                            break;
                        }
                    }
                }
                this.loading = false;
                this.progressView.showTextView();
                this.adapter.notifyDataSetChanged();
                this.cameraAttachAdapter.notifyDataSetChanged();
                if (!selectedPhotosOrder.isEmpty() && this.galleryAlbumEntry != null) {
                    int N = selectedPhotosOrder.size();
                    for (int a2 = 0; a2 < N; a2++) {
                        Integer imageId = (Integer) selectedPhotosOrder.get(a2);
                        Object currentEntry = selectedPhotos.get(imageId);
                        MediaController.PhotoEntry entry2 = this.galleryAlbumEntry.photosByIds.get(imageId.intValue());
                        if (entry2 != null) {
                            if (currentEntry instanceof MediaController.PhotoEntry) {
                                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) currentEntry;
                                entry2.copyFrom(photoEntry);
                            }
                            selectedPhotos.put(imageId, entry2);
                        }
                    }
                }
                updateAlbumsDropDown();
            }
        } else if (id == NotificationCenter.cameraInitied) {
            checkCamera(false);
        }
    }

    /* loaded from: classes5.dex */
    public class PhotoAttachAdapter extends RecyclerListView.SelectionAdapter {
        private int itemsCount;
        private Context mContext;
        private boolean needCamera;
        private ArrayList<RecyclerListView.Holder> viewsCache = new ArrayList<>(8);

        public PhotoAttachAdapter(Context context, boolean camera) {
            ChatAttachAlertPhotoLayout.this = r2;
            this.mContext = context;
            this.needCamera = camera;
        }

        public void createCache() {
            for (int a = 0; a < 8; a++) {
                this.viewsCache.add(createHolder());
            }
        }

        public RecyclerListView.Holder createHolder() {
            PhotoAttachPhotoCell cell = new PhotoAttachPhotoCell(this.mContext, ChatAttachAlertPhotoLayout.this.resourcesProvider);
            if (Build.VERSION.SDK_INT >= 21 && this == ChatAttachAlertPhotoLayout.this.adapter) {
                cell.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.PhotoAttachAdapter.1
                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view, Outline outline) {
                        PhotoAttachPhotoCell photoCell = (PhotoAttachPhotoCell) view;
                        int position = ((Integer) photoCell.getTag()).intValue();
                        if (PhotoAttachAdapter.this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                            position++;
                        }
                        if (position != 0) {
                            if (position == ChatAttachAlertPhotoLayout.this.itemsPerRow - 1) {
                                int rad = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                                outline.setRoundRect(-rad, 0, view.getMeasuredWidth(), view.getMeasuredHeight() + rad, rad);
                                return;
                            }
                            outline.setRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                            return;
                        }
                        int rad2 = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                        outline.setRoundRect(0, 0, view.getMeasuredWidth() + rad2, view.getMeasuredHeight() + rad2, rad2);
                    }
                });
                cell.setClipToOutline(true);
            }
            cell.setDelegate(new PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter$$ExternalSyntheticLambda0
                @Override // org.telegram.ui.Cells.PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate
                public final void onCheckClick(PhotoAttachPhotoCell photoAttachPhotoCell) {
                    ChatAttachAlertPhotoLayout.PhotoAttachAdapter.this.m2497xc0873f11(photoAttachPhotoCell);
                }
            });
            return new RecyclerListView.Holder(cell);
        }

        /* renamed from: lambda$createHolder$0$org-telegram-ui-Components-ChatAttachAlertPhotoLayout$PhotoAttachAdapter */
        public /* synthetic */ void m2497xc0873f11(PhotoAttachPhotoCell v) {
            if (!ChatAttachAlertPhotoLayout.this.mediaEnabled || ChatAttachAlertPhotoLayout.this.parentAlert.avatarPicker != 0) {
                return;
            }
            int index = ((Integer) v.getTag()).intValue();
            MediaController.PhotoEntry photoEntry = v.getPhotoEntry();
            int i = 1;
            boolean added = !ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
            if (!added || ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos < 0 || ChatAttachAlertPhotoLayout.selectedPhotos.size() < ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos) {
                int num = added ? ChatAttachAlertPhotoLayout.selectedPhotosOrder.size() : -1;
                if ((ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) && ChatAttachAlertPhotoLayout.this.parentAlert.allowOrder) {
                    v.setChecked(num, added, true);
                } else {
                    v.setChecked(-1, added, true);
                }
                ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(photoEntry, index);
                int updateIndex = index;
                if (this == ChatAttachAlertPhotoLayout.this.cameraAttachAdapter) {
                    if (ChatAttachAlertPhotoLayout.this.adapter.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                        updateIndex++;
                    }
                    ChatAttachAlertPhotoLayout.this.adapter.notifyItemChanged(updateIndex);
                } else {
                    ChatAttachAlertPhotoLayout.this.cameraAttachAdapter.notifyItemChanged(updateIndex);
                }
                ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                if (!added) {
                    i = 2;
                }
                chatAttachAlert.updateCountButton(i);
            } else if (ChatAttachAlertPhotoLayout.this.parentAlert.allowOrder && (ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity)) {
                ChatActivity chatActivity = (ChatActivity) ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment;
                TLRPC.Chat chat = chatActivity.getCurrentChat();
                if (chat != null && !ChatObject.hasAdminRights(chat) && chat.slowmode_enabled && ChatAttachAlertPhotoLayout.this.alertOnlyOnce != 2) {
                    AlertsCreator.createSimpleAlert(ChatAttachAlertPhotoLayout.this.getContext(), LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSelectSendError", R.string.SlowmodeSelectSendError), ChatAttachAlertPhotoLayout.this.resourcesProvider).show();
                    if (ChatAttachAlertPhotoLayout.this.alertOnlyOnce == 1) {
                        ChatAttachAlertPhotoLayout.this.alertOnlyOnce = 2;
                    }
                }
            }
        }

        public MediaController.PhotoEntry getPhoto(int position) {
            if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                position--;
            }
            return ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(position);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = true;
            r1 = 1;
            r1 = 1;
            int i = 1;
            switch (holder.getItemViewType()) {
                case 0:
                    if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                        position--;
                    }
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) holder.itemView;
                    if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                        cell.setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
                    } else {
                        cell.setIsVertical(ChatAttachAlertPhotoLayout.this.cameraPhotoLayoutManager.getOrientation() == 1);
                    }
                    if (ChatAttachAlertPhotoLayout.this.parentAlert.avatarPicker != 0) {
                        cell.getCheckBox().setVisibility(8);
                    }
                    MediaController.PhotoEntry photoEntry = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(position);
                    boolean z2 = this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry;
                    if (position != getItemCount() - 1) {
                        z = false;
                    }
                    cell.setPhotoEntry(photoEntry, z2, z);
                    if (!(ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment instanceof ChatActivity) || !ChatAttachAlertPhotoLayout.this.parentAlert.allowOrder) {
                        cell.setChecked(-1, ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                    } else {
                        cell.setChecked(ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId)), ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                    }
                    cell.getImageView().setTag(Integer.valueOf(position));
                    cell.setTag(Integer.valueOf(position));
                    return;
                case 1:
                    ChatAttachAlertPhotoLayout.this.cameraCell = (PhotoAttachCameraCell) holder.itemView;
                    if (ChatAttachAlertPhotoLayout.this.cameraView != null && ChatAttachAlertPhotoLayout.this.cameraView.isInited() && !ChatAttachAlertPhotoLayout.this.isHidden) {
                        ChatAttachAlertPhotoLayout.this.cameraCell.setVisibility(4);
                    } else {
                        ChatAttachAlertPhotoLayout.this.cameraCell.setVisibility(0);
                    }
                    ChatAttachAlertPhotoLayout.this.cameraCell.setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
                    return;
                case 2:
                default:
                    return;
                case 3:
                    PhotoAttachPermissionCell cell2 = (PhotoAttachPermissionCell) holder.itemView;
                    cell2.setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
                    if (this.needCamera && ChatAttachAlertPhotoLayout.this.noCameraPermissions && position == 0) {
                        i = 0;
                    }
                    cell2.setType(i);
                    return;
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    if (!this.viewsCache.isEmpty()) {
                        RecyclerListView.Holder holder = this.viewsCache.get(0);
                        this.viewsCache.remove(0);
                        return holder;
                    }
                    RecyclerListView.Holder holder2 = createHolder();
                    return holder2;
                case 1:
                    ChatAttachAlertPhotoLayout.this.cameraCell = new PhotoAttachCameraCell(this.mContext, ChatAttachAlertPhotoLayout.this.resourcesProvider);
                    if (Build.VERSION.SDK_INT >= 21) {
                        ChatAttachAlertPhotoLayout.this.cameraCell.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.PhotoAttachAdapter.2
                            @Override // android.view.ViewOutlineProvider
                            public void getOutline(View view, Outline outline) {
                                int rad = AndroidUtilities.dp(ChatAttachAlertPhotoLayout.this.parentAlert.cornerRadius * 8.0f);
                                outline.setRoundRect(0, 0, view.getMeasuredWidth() + rad, view.getMeasuredHeight() + rad, rad);
                            }
                        });
                        ChatAttachAlertPhotoLayout.this.cameraCell.setClipToOutline(true);
                    }
                    RecyclerListView.Holder holder3 = new RecyclerListView.Holder(ChatAttachAlertPhotoLayout.this.cameraCell);
                    return holder3;
                case 2:
                    RecyclerListView.Holder holder4 = new RecyclerListView.Holder(new View(this.mContext) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.PhotoAttachAdapter.3
                        @Override // android.view.View
                        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(ChatAttachAlertPhotoLayout.this.gridExtraSpace, C.BUFFER_FLAG_ENCRYPTED));
                        }
                    });
                    return holder4;
                default:
                    RecyclerListView.Holder holder5 = new RecyclerListView.Holder(new PhotoAttachPermissionCell(this.mContext, ChatAttachAlertPhotoLayout.this.resourcesProvider));
                    return holder5;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof PhotoAttachCameraCell) {
                PhotoAttachCameraCell cell = (PhotoAttachCameraCell) holder.itemView;
                cell.updateBitmap();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (!ChatAttachAlertPhotoLayout.this.mediaEnabled) {
                return 1;
            }
            int count = 0;
            if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                count = 0 + 1;
            }
            if (ChatAttachAlertPhotoLayout.this.noGalleryPermissions && this == ChatAttachAlertPhotoLayout.this.adapter) {
                count++;
            }
            int count2 = count + ChatAttachAlertPhotoLayout.cameraPhotos.size();
            if (ChatAttachAlertPhotoLayout.this.selectedAlbumEntry != null) {
                count2 += ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.size();
            }
            if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                count2++;
            }
            this.itemsCount = count2;
            return count2;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (!ChatAttachAlertPhotoLayout.this.mediaEnabled) {
                return 2;
            }
            if (this.needCamera && position == 0 && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                return ChatAttachAlertPhotoLayout.this.noCameraPermissions ? 3 : 1;
            } else if (this == ChatAttachAlertPhotoLayout.this.adapter && position == this.itemsCount - 1) {
                return 2;
            } else {
                return ChatAttachAlertPhotoLayout.this.noGalleryPermissions ? 3 : 0;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                ChatAttachAlertPhotoLayout.this.progressView.setVisibility((!(getItemCount() == 1 && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == null) && ChatAttachAlertPhotoLayout.this.mediaEnabled) ? 4 : 0);
            }
        }
    }
}
