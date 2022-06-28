package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.microsoft.appcenter.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationBadge;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.Cells.SharedDocumentCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerViewItemRangeSelector;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoViewer;
/* loaded from: classes4.dex */
public class PhotoPickerActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int change_sort = 1;
    private static final int open_in = 2;
    private int alertOnlyOnce;
    private boolean allowCaption;
    private boolean allowIndices;
    private AnimatorSet animatorSet;
    private CharSequence caption;
    private ChatActivity chatActivity;
    protected EditTextEmoji commentTextView;
    private PhotoPickerActivityDelegate delegate;
    private final String dialogBackgroundKey;
    private StickerEmptyView emptyView;
    private FlickerLoadingView flickerView;
    private final boolean forceDarckTheme;
    protected FrameLayout frameLayout2;
    private int imageReqId;
    private String initialSearchString;
    private boolean isDocumentsPicker;
    private ActionBarMenuSubItem[] itemCells;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    private String lastSearchImageString;
    private String lastSearchString;
    private int lastSearchToken;
    private GridLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private boolean listSort;
    private RecyclerListView listView;
    private int maxSelectedPhotos;
    private String nextImagesSearchOffset;
    private PhotoPickerActivitySearchDelegate searchDelegate;
    private ActionBarMenuItem searchItem;
    private boolean searching;
    private boolean searchingUser;
    private int selectPhotoType;
    private MediaController.AlbumEntry selectedAlbum;
    protected View selectedCountView;
    private HashMap<Object, Object> selectedPhotos;
    private ArrayList<Object> selectedPhotosOrder;
    private final String selectorKey;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private boolean sendPressed;
    protected View shadow;
    private boolean shouldSelect;
    private ActionBarMenuSubItem showAsListItem;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private final String textKey;
    private int type;
    private ImageView writeButton;
    protected FrameLayout writeButtonContainer;
    private Drawable writeButtonDrawable;
    private ArrayList<MediaController.SearchImage> searchResult = new ArrayList<>();
    private HashMap<String, MediaController.SearchImage> searchResultKeys = new HashMap<>();
    private HashMap<String, MediaController.SearchImage> searchResultUrls = new HashMap<>();
    private ArrayList<String> recentSearches = new ArrayList<>();
    private boolean imageSearchEndReached = true;
    private boolean allowOrder = true;
    private int itemSize = 100;
    private int itemsPerRow = 3;
    private TextPaint textPaint = new TextPaint(1);
    private RectF rect = new RectF();
    private Paint paint = new Paint(1);
    private boolean needsBottomLayout = true;
    private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.PhotoPickerActivity.1
        {
            PhotoPickerActivity.this = this;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean scaleToFill() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index, boolean needPreview) {
            PhotoAttachPhotoCell cell = PhotoPickerActivity.this.getCellForIndex(index);
            if (cell != null) {
                BackupImageView imageView = cell.getImageView();
                int[] coords = new int[2];
                imageView.getLocationInWindow(coords);
                PhotoViewer.PlaceProviderObject object = new PhotoViewer.PlaceProviderObject();
                object.viewX = coords[0];
                object.viewY = coords[1] - (Build.VERSION.SDK_INT >= 21 ? 0 : AndroidUtilities.statusBarHeight);
                object.parentView = PhotoPickerActivity.this.listView;
                object.imageReceiver = imageView.getImageReceiver();
                object.thumb = object.imageReceiver.getBitmapSafe();
                object.scale = cell.getScale();
                cell.showCheck(false);
                return object;
            }
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void updatePhotoAtIndex(int index) {
            PhotoAttachPhotoCell cell = PhotoPickerActivity.this.getCellForIndex(index);
            if (cell != null) {
                if (PhotoPickerActivity.this.selectedAlbum == null) {
                    cell.setPhotoEntry((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index), true, false);
                    return;
                }
                BackupImageView imageView = cell.getImageView();
                imageView.setOrientation(0, true);
                MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                if (photoEntry.thumbPath != null) {
                    imageView.setImage(photoEntry.thumbPath, null, Theme.chat_attachEmptyDrawable);
                } else if (photoEntry.path != null) {
                    imageView.setOrientation(photoEntry.orientation, true);
                    if (photoEntry.isVideo) {
                        imageView.setImage("vthumb://" + photoEntry.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + photoEntry.path, null, Theme.chat_attachEmptyDrawable);
                        return;
                    }
                    imageView.setImage("thumb://" + photoEntry.imageId + Constants.COMMON_SCHEMA_PREFIX_SEPARATOR + photoEntry.path, null, Theme.chat_attachEmptyDrawable);
                } else {
                    imageView.setImageDrawable(Theme.chat_attachEmptyDrawable);
                }
            }
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean allowCaption() {
            return PhotoPickerActivity.this.allowCaption;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            PhotoAttachPhotoCell cell = PhotoPickerActivity.this.getCellForIndex(index);
            if (cell != null) {
                return cell.getImageView().getImageReceiver().getBitmapSafe();
            }
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int index) {
            int count = PhotoPickerActivity.this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = PhotoPickerActivity.this.listView.getChildAt(a);
                if (view.getTag() != null) {
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                    int num = ((Integer) view.getTag()).intValue();
                    if (PhotoPickerActivity.this.selectedAlbum == null ? !(num < 0 || num >= PhotoPickerActivity.this.searchResult.size()) : !(num < 0 || num >= PhotoPickerActivity.this.selectedAlbum.photos.size())) {
                        if (num == index) {
                            cell.showCheck(true);
                            return;
                        }
                    }
                }
            }
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willHidePhotoViewer() {
            int count = PhotoPickerActivity.this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View view = PhotoPickerActivity.this.listView.getChildAt(a);
                if (view instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                    cell.showCheck(true);
                }
            }
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean isPhotoChecked(int index) {
            return PhotoPickerActivity.this.selectedAlbum != null ? index >= 0 && index < PhotoPickerActivity.this.selectedAlbum.photos.size() && PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(PhotoPickerActivity.this.selectedAlbum.photos.get(index).imageId)) : index >= 0 && index < PhotoPickerActivity.this.searchResult.size() && PhotoPickerActivity.this.selectedPhotos.containsKey(((MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index)).id);
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int setPhotoUnchecked(Object object) {
            Object key = null;
            if (object instanceof MediaController.PhotoEntry) {
                key = Integer.valueOf(((MediaController.PhotoEntry) object).imageId);
            } else if (object instanceof MediaController.SearchImage) {
                key = ((MediaController.SearchImage) object).id;
            }
            if (key != null && PhotoPickerActivity.this.selectedPhotos.containsKey(key)) {
                PhotoPickerActivity.this.selectedPhotos.remove(key);
                int position = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(key);
                if (position >= 0) {
                    PhotoPickerActivity.this.selectedPhotosOrder.remove(position);
                }
                if (PhotoPickerActivity.this.allowIndices) {
                    PhotoPickerActivity.this.updateCheckedPhotoIndices();
                }
                return position;
            }
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int setPhotoChecked(int index, VideoEditedInfo videoEditedInfo) {
            int num;
            boolean add = true;
            int i = -1;
            if (PhotoPickerActivity.this.selectedAlbum != null) {
                if (index < 0 || index >= PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                    return -1;
                }
                MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                int addToSelectedPhotos = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                num = addToSelectedPhotos;
                if (addToSelectedPhotos == -1) {
                    photoEntry.editedInfo = videoEditedInfo;
                    num = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                } else {
                    add = false;
                    photoEntry.editedInfo = null;
                }
            } else if (index < 0 || index >= PhotoPickerActivity.this.searchResult.size()) {
                return -1;
            } else {
                MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index);
                int addToSelectedPhotos2 = PhotoPickerActivity.this.addToSelectedPhotos(photoEntry2, -1);
                num = addToSelectedPhotos2;
                if (addToSelectedPhotos2 == -1) {
                    photoEntry2.editedInfo = videoEditedInfo;
                    num = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(photoEntry2.id);
                } else {
                    add = false;
                    photoEntry2.editedInfo = null;
                }
            }
            int count = PhotoPickerActivity.this.listView.getChildCount();
            int a = 0;
            while (true) {
                if (a >= count) {
                    break;
                }
                View view = PhotoPickerActivity.this.listView.getChildAt(a);
                int tag = ((Integer) view.getTag()).intValue();
                if (tag != index) {
                    a++;
                } else {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) view;
                    if (PhotoPickerActivity.this.allowIndices) {
                        i = num;
                    }
                    photoAttachPhotoCell.setChecked(i, add, false);
                }
            }
            PhotoPickerActivity.this.updatePhotosButton(add ? 1 : 2);
            PhotoPickerActivity.this.delegate.selectedPhotosChanged();
            return num;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean cancelButtonPressed() {
            PhotoPickerActivity.this.delegate.actionButtonPressed(true, true, 0);
            PhotoPickerActivity.this.finishFragment();
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getSelectedCount() {
            return PhotoPickerActivity.this.selectedPhotos.size();
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void sendButtonPressed(int index, VideoEditedInfo videoEditedInfo, boolean notify, int scheduleDate, boolean forceDocument) {
            if (PhotoPickerActivity.this.selectedPhotos.isEmpty()) {
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    if (index >= 0 && index < PhotoPickerActivity.this.selectedAlbum.photos.size()) {
                        MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                        photoEntry.editedInfo = videoEditedInfo;
                        PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, -1);
                    } else {
                        return;
                    }
                } else if (index >= 0 && index < PhotoPickerActivity.this.searchResult.size()) {
                    MediaController.SearchImage searchImage = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index);
                    searchImage.editedInfo = videoEditedInfo;
                    PhotoPickerActivity.this.addToSelectedPhotos(searchImage, -1);
                } else {
                    return;
                }
            }
            PhotoPickerActivity.this.sendSelectedPhotos(notify, scheduleDate);
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public ArrayList<Object> getSelectedPhotosOrder() {
            return PhotoPickerActivity.this.selectedPhotosOrder;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public HashMap<Object, Object> getSelectedPhotos() {
            return PhotoPickerActivity.this.selectedPhotos;
        }
    };

    /* loaded from: classes4.dex */
    public interface PhotoPickerActivitySearchDelegate {
        void shouldClearRecentSearch();

        void shouldSearchText(String str);
    }

    /* loaded from: classes4.dex */
    public interface PhotoPickerActivityDelegate {
        void actionButtonPressed(boolean z, boolean z2, int i);

        void onCaptionChanged(CharSequence charSequence);

        void onOpenInPressed();

        void selectedPhotosChanged();

        /* renamed from: org.telegram.ui.PhotoPickerActivity$PhotoPickerActivityDelegate$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static void $default$onOpenInPressed(PhotoPickerActivityDelegate _this) {
            }
        }
    }

    public PhotoPickerActivity(int type, MediaController.AlbumEntry selectedAlbum, HashMap<Object, Object> selectedPhotos, ArrayList<Object> selectedPhotosOrder, int selectPhotoType, boolean allowCaption, ChatActivity chatActivity, boolean forceDarkTheme) {
        this.selectedAlbum = selectedAlbum;
        this.selectedPhotos = selectedPhotos;
        this.selectedPhotosOrder = selectedPhotosOrder;
        this.type = type;
        this.selectPhotoType = selectPhotoType;
        this.chatActivity = chatActivity;
        this.allowCaption = allowCaption;
        this.forceDarckTheme = forceDarkTheme;
        if (selectedAlbum == null) {
            loadRecentSearch();
        }
        if (forceDarkTheme) {
            this.dialogBackgroundKey = Theme.key_voipgroup_dialogBackground;
            this.textKey = Theme.key_voipgroup_actionBarItems;
            this.selectorKey = Theme.key_voipgroup_actionBarItemsSelector;
            return;
        }
        this.dialogBackgroundKey = Theme.key_dialogBackground;
        this.textKey = Theme.key_dialogTextBlack;
        this.selectorKey = Theme.key_dialogButtonSelector;
    }

    public void setDocumentsPicker(boolean value) {
        this.isDocumentsPicker = value;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        if (this.imageReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
            this.imageReqId = 0;
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        super.onFragmentDestroy();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        int i;
        this.listSort = false;
        this.actionBar.setBackgroundColor(Theme.getColor(this.dialogBackgroundKey));
        this.actionBar.setTitleColor(Theme.getColor(this.textKey));
        this.actionBar.setItemsColor(Theme.getColor(this.textKey), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(this.selectorKey), false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        if (this.selectedAlbum != null) {
            this.actionBar.setTitle(this.selectedAlbum.bucketName);
        } else {
            int i2 = this.type;
            if (i2 == 0) {
                this.actionBar.setTitle(LocaleController.getString("SearchImagesTitle", R.string.SearchImagesTitle));
            } else if (i2 == 1) {
                this.actionBar.setTitle(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
            }
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.PhotoPickerActivity.2
            {
                PhotoPickerActivity.this = this;
            }

            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    PhotoPickerActivity.this.finishFragment();
                } else if (id == 1) {
                    PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                    photoPickerActivity.listSort = true ^ photoPickerActivity.listSort;
                    if (PhotoPickerActivity.this.listSort) {
                        PhotoPickerActivity.this.listView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
                    } else {
                        PhotoPickerActivity.this.listView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(50.0f));
                    }
                    PhotoPickerActivity.this.listView.stopScroll();
                    PhotoPickerActivity.this.layoutManager.scrollToPositionWithOffset(0, 0);
                    PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                } else if (id == 2) {
                    if (PhotoPickerActivity.this.delegate != null) {
                        PhotoPickerActivity.this.delegate.onOpenInPressed();
                    }
                    PhotoPickerActivity.this.finishFragment();
                }
            }
        });
        if (this.isDocumentsPicker) {
            ActionBarMenu menu = this.actionBar.createMenu();
            ActionBarMenuItem menuItem = menu.addItem(0, R.drawable.ic_ab_other);
            menuItem.setSubMenuDelegate(new ActionBarMenuItem.ActionBarSubMenuItemDelegate() { // from class: org.telegram.ui.PhotoPickerActivity.3
                {
                    PhotoPickerActivity.this = this;
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarSubMenuItemDelegate
                public void onShowSubMenu() {
                    String str;
                    int i3;
                    ActionBarMenuSubItem actionBarMenuSubItem = PhotoPickerActivity.this.showAsListItem;
                    if (PhotoPickerActivity.this.listSort) {
                        i3 = R.string.ShowAsGrid;
                        str = "ShowAsGrid";
                    } else {
                        i3 = R.string.ShowAsList;
                        str = "ShowAsList";
                    }
                    actionBarMenuSubItem.setText(LocaleController.getString(str, i3));
                    PhotoPickerActivity.this.showAsListItem.setIcon(PhotoPickerActivity.this.listSort ? R.drawable.msg_media : R.drawable.msg_list);
                }

                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarSubMenuItemDelegate
                public void onHideSubMenu() {
                }
            });
            this.showAsListItem = menuItem.addSubItem(1, R.drawable.msg_list, LocaleController.getString("ShowAsList", R.string.ShowAsList));
            menuItem.addSubItem(2, R.drawable.msg_openin, LocaleController.getString("OpenInExternalApp", R.string.OpenInExternalApp));
        }
        if (this.selectedAlbum == null) {
            ActionBarMenu menu2 = this.actionBar.createMenu();
            ActionBarMenuItem actionBarMenuItemSearchListener = menu2.addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new AnonymousClass4());
            this.searchItem = actionBarMenuItemSearchListener;
            EditTextBoldCursor editText = actionBarMenuItemSearchListener.getSearchField();
            editText.setTextColor(Theme.getColor(this.textKey));
            editText.setCursorColor(Theme.getColor(this.textKey));
            editText.setHintTextColor(Theme.getColor(Theme.key_chat_messagePanelHint));
        }
        if (this.selectedAlbum == null) {
            int i3 = this.type;
            if (i3 == 0) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("SearchImagesTitle", R.string.SearchImagesTitle));
            } else if (i3 == 1) {
                this.searchItem.setSearchFieldHint(LocaleController.getString("SearchGifsTitle", R.string.SearchGifsTitle));
            }
        }
        AnonymousClass5 anonymousClass5 = new AnonymousClass5(context);
        this.sizeNotifierFrameLayout = anonymousClass5;
        anonymousClass5.setBackgroundColor(Theme.getColor(this.dialogBackgroundKey));
        this.fragmentView = this.sizeNotifierFrameLayout;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(50.0f));
        this.listView.setClipToPadding(false);
        this.listView.setHorizontalScrollBarEnabled(false);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        RecyclerListView recyclerListView2 = this.listView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4) { // from class: org.telegram.ui.PhotoPickerActivity.6
            {
                PhotoPickerActivity.this = this;
            }

            @Override // androidx.recyclerview.widget.GridLayoutManager, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager = gridLayoutManager;
        recyclerListView2.setLayoutManager(gridLayoutManager);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.PhotoPickerActivity.7
            {
                PhotoPickerActivity.this = this;
            }

            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                if (PhotoPickerActivity.this.listAdapter.getItemViewType(position) == 1 || PhotoPickerActivity.this.listSort || (PhotoPickerActivity.this.selectedAlbum == null && TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString))) {
                    return PhotoPickerActivity.this.layoutManager.getSpanCount();
                }
                return PhotoPickerActivity.this.itemSize + (position % PhotoPickerActivity.this.itemsPerRow != PhotoPickerActivity.this.itemsPerRow - 1 ? AndroidUtilities.dp(5.0f) : 0);
            }
        });
        this.sizeNotifierFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setGlowColor(Theme.getColor(this.dialogBackgroundKey));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.PhotoPickerActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i4) {
                PhotoPickerActivity.this.m4181lambda$createView$1$orgtelegramuiPhotoPickerActivity(view, i4);
            }
        });
        if (this.maxSelectedPhotos != 1) {
            this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.PhotoPickerActivity$$ExternalSyntheticLambda3
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
                public final boolean onItemClick(View view, int i4) {
                    return PhotoPickerActivity.this.m4182lambda$createView$2$orgtelegramuiPhotoPickerActivity(view, i4);
                }
            });
        }
        RecyclerViewItemRangeSelector recyclerViewItemRangeSelector = new RecyclerViewItemRangeSelector(new RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate() { // from class: org.telegram.ui.PhotoPickerActivity.8
            {
                PhotoPickerActivity.this = this;
            }

            @Override // org.telegram.ui.Components.RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate
            public int getItemCount() {
                return PhotoPickerActivity.this.listAdapter.getItemCount();
            }

            @Override // org.telegram.ui.Components.RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate
            public void setSelected(View view, int index, boolean selected) {
                if (selected != PhotoPickerActivity.this.shouldSelect || !(view instanceof PhotoAttachPhotoCell)) {
                    return;
                }
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                cell.callDelegate();
            }

            @Override // org.telegram.ui.Components.RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate
            public boolean isSelected(int index) {
                Object key;
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                    key = Integer.valueOf(photoEntry.imageId);
                } else {
                    MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index);
                    key = photoEntry2.id;
                }
                return PhotoPickerActivity.this.selectedPhotos.containsKey(key);
            }

            @Override // org.telegram.ui.Components.RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate
            public boolean isIndexSelectable(int index) {
                return PhotoPickerActivity.this.listAdapter.getItemViewType(index) == 0;
            }

            @Override // org.telegram.ui.Components.RecyclerViewItemRangeSelector.RecyclerViewItemRangeSelectorDelegate
            public void onStartStopSelection(boolean start) {
                PhotoPickerActivity.this.alertOnlyOnce = start ? 1 : 0;
                if (start) {
                    PhotoPickerActivity.this.parentLayout.requestDisallowInterceptTouchEvent(true);
                }
                PhotoPickerActivity.this.listView.hideSelector(true);
            }
        });
        this.itemRangeSelector = recyclerViewItemRangeSelector;
        if (this.maxSelectedPhotos != 1) {
            this.listView.addOnItemTouchListener(recyclerViewItemRangeSelector);
        }
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context, getResourceProvider()) { // from class: org.telegram.ui.PhotoPickerActivity.9
            {
                PhotoPickerActivity.this = this;
            }

            @Override // org.telegram.ui.Components.FlickerLoadingView
            public int getViewType() {
                return 2;
            }

            @Override // org.telegram.ui.Components.FlickerLoadingView
            public int getColumnsCount() {
                return 3;
            }
        };
        this.flickerView = flickerLoadingView;
        flickerLoadingView.setAlpha(0.0f);
        this.flickerView.setVisibility(8);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, this.flickerView, 1, getResourceProvider());
        this.emptyView = stickerEmptyView;
        stickerEmptyView.setAnimateLayoutChange(true);
        this.emptyView.title.setTypeface(Typeface.DEFAULT);
        this.emptyView.title.setTextSize(1, 16.0f);
        this.emptyView.title.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
        this.emptyView.addView(this.flickerView, 0);
        if (this.selectedAlbum != null) {
            this.emptyView.title.setText(LocaleController.getString("NoPhotos", R.string.NoPhotos));
        } else {
            this.emptyView.title.setText(LocaleController.getString("NoRecentSearches", R.string.NoRecentSearches));
        }
        this.emptyView.showProgress(false, false);
        this.sizeNotifierFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 126.0f, 0.0f, 0.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.PhotoPickerActivity.10
            {
                PhotoPickerActivity.this = this;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (PhotoPickerActivity.this.selectedAlbum == null) {
                    int firstVisibleItem = PhotoPickerActivity.this.layoutManager.findFirstVisibleItemPosition();
                    boolean z = false;
                    int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(PhotoPickerActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                    if (visibleItemCount > 0) {
                        int totalItemCount = PhotoPickerActivity.this.layoutManager.getItemCount();
                        if (firstVisibleItem + visibleItemCount > totalItemCount - 2 && !PhotoPickerActivity.this.searching && !PhotoPickerActivity.this.imageSearchEndReached) {
                            PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                            if (photoPickerActivity.type == 1) {
                                z = true;
                            }
                            photoPickerActivity.searchImages(z, PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.nextImagesSearchOffset, true);
                        }
                    }
                }
            }
        });
        if (this.selectedAlbum == null) {
            updateSearchInterface();
        }
        if (this.needsBottomLayout) {
            View view = new View(context);
            this.shadow = view;
            view.setBackgroundResource(R.drawable.header_shadow_reverse);
            this.shadow.setTranslationY(AndroidUtilities.dp(48.0f));
            this.sizeNotifierFrameLayout.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
            FrameLayout frameLayout = new FrameLayout(context);
            this.frameLayout2 = frameLayout;
            frameLayout.setBackgroundColor(Theme.getColor(this.dialogBackgroundKey));
            this.frameLayout2.setVisibility(4);
            this.frameLayout2.setTranslationY(AndroidUtilities.dp(48.0f));
            this.sizeNotifierFrameLayout.addView(this.frameLayout2, LayoutHelper.createFrame(-1, 48, 83));
            this.frameLayout2.setOnTouchListener(PhotoPickerActivity$$ExternalSyntheticLambda7.INSTANCE);
            EditTextEmoji editTextEmoji = this.commentTextView;
            if (editTextEmoji != null) {
                editTextEmoji.onDestroy();
            }
            this.commentTextView = new EditTextEmoji(context, this.sizeNotifierFrameLayout, null, 1);
            InputFilter[] inputFilters = {new InputFilter.LengthFilter(MessagesController.getInstance(UserConfig.selectedAccount).maxCaptionLength)};
            this.commentTextView.setFilters(inputFilters);
            this.commentTextView.setHint(LocaleController.getString("AddCaption", R.string.AddCaption));
            this.commentTextView.onResume();
            EditTextBoldCursor editText2 = this.commentTextView.getEditText();
            editText2.setMaxLines(1);
            editText2.setSingleLine(true);
            this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 84.0f, 0.0f));
            CharSequence charSequence = this.caption;
            if (charSequence != null) {
                this.commentTextView.setText(charSequence);
            }
            this.commentTextView.getEditText().addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.PhotoPickerActivity.11
                {
                    PhotoPickerActivity.this = this;
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable s) {
                    if (PhotoPickerActivity.this.delegate != null) {
                        PhotoPickerActivity.this.delegate.onCaptionChanged(s);
                    }
                }
            });
            FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.PhotoPickerActivity.12
                {
                    PhotoPickerActivity.this = this;
                }

                @Override // android.view.View
                public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
                    super.onInitializeAccessibilityNodeInfo(info);
                    info.setText(LocaleController.formatPluralString("AccDescrSendPhotos", PhotoPickerActivity.this.selectedPhotos.size(), new Object[0]));
                    info.setClassName(Button.class.getName());
                    info.setLongClickable(true);
                    info.setClickable(true);
                }
            };
            this.writeButtonContainer = frameLayout2;
            frameLayout2.setFocusable(true);
            this.writeButtonContainer.setFocusableInTouchMode(true);
            this.writeButtonContainer.setVisibility(4);
            this.writeButtonContainer.setScaleX(0.2f);
            this.writeButtonContainer.setScaleY(0.2f);
            this.writeButtonContainer.setAlpha(0.0f);
            this.sizeNotifierFrameLayout.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 12.0f, 10.0f));
            this.writeButton = new ImageView(context);
            int dp = AndroidUtilities.dp(56.0f);
            String str = Theme.key_dialogFloatingButton;
            int color = Theme.getColor(str);
            if (Build.VERSION.SDK_INT >= 21) {
                str = Theme.key_dialogFloatingButtonPressed;
            }
            this.writeButtonDrawable = Theme.createSimpleSelectorCircleDrawable(dp, color, Theme.getColor(str));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, this.writeButtonDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                this.writeButtonDrawable = combinedDrawable;
            }
            this.writeButton.setBackgroundDrawable(this.writeButtonDrawable);
            this.writeButton.setImageResource(R.drawable.attach_send);
            this.writeButton.setImportantForAccessibility(2);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.MULTIPLY));
            this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
            if (Build.VERSION.SDK_INT >= 21) {
                this.writeButton.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.PhotoPickerActivity.13
                    {
                        PhotoPickerActivity.this = this;
                    }

                    @Override // android.view.ViewOutlineProvider
                    public void getOutline(View view2, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 51, Build.VERSION.SDK_INT >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
            this.writeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoPickerActivity$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PhotoPickerActivity.this.m4183lambda$createView$4$orgtelegramuiPhotoPickerActivity(view2);
                }
            });
            this.writeButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.PhotoPickerActivity$$ExternalSyntheticLambda6
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view2) {
                    return PhotoPickerActivity.this.m4186lambda$createView$7$orgtelegramuiPhotoPickerActivity(view2);
                }
            });
            this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
            this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            View view2 = new View(context) { // from class: org.telegram.ui.PhotoPickerActivity.15
                {
                    PhotoPickerActivity.this = this;
                }

                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    String text = String.format("%d", Integer.valueOf(Math.max(1, PhotoPickerActivity.this.selectedPhotosOrder.size())));
                    int textSize = (int) Math.ceil(PhotoPickerActivity.this.textPaint.measureText(text));
                    int size = Math.max(AndroidUtilities.dp(16.0f) + textSize, AndroidUtilities.dp(24.0f));
                    int cx = getMeasuredWidth() / 2;
                    int measuredHeight = getMeasuredHeight() / 2;
                    PhotoPickerActivity.this.textPaint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBoxCheck));
                    PhotoPickerActivity.this.paint.setColor(Theme.getColor(PhotoPickerActivity.this.dialogBackgroundKey));
                    PhotoPickerActivity.this.rect.set(cx - (size / 2), 0.0f, (size / 2) + cx, getMeasuredHeight());
                    canvas.drawRoundRect(PhotoPickerActivity.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), PhotoPickerActivity.this.paint);
                    PhotoPickerActivity.this.paint.setColor(Theme.getColor(Theme.key_dialogRoundCheckBox));
                    PhotoPickerActivity.this.rect.set((cx - (size / 2)) + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), ((size / 2) + cx) - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
                    canvas.drawRoundRect(PhotoPickerActivity.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), PhotoPickerActivity.this.paint);
                    canvas.drawText(text, cx - (textSize / 2), AndroidUtilities.dp(16.2f), PhotoPickerActivity.this.textPaint);
                }
            };
            this.selectedCountView = view2;
            view2.setAlpha(0.0f);
            this.selectedCountView.setScaleX(0.2f);
            this.selectedCountView.setScaleY(0.2f);
            this.sizeNotifierFrameLayout.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -2.0f, 9.0f));
            if (this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_ALL) {
                this.commentTextView.setVisibility(8);
            }
        }
        this.allowIndices = (this.selectedAlbum != null || (i = this.type) == 0 || i == 1) && this.allowOrder;
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(true, 0);
        updatePhotosButton(0);
        return this.fragmentView;
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$4 */
    /* loaded from: classes4.dex */
    public class AnonymousClass4 extends ActionBarMenuItem.ActionBarMenuItemSearchListener {
        Runnable updateSearch = new Runnable() { // from class: org.telegram.ui.PhotoPickerActivity$4$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PhotoPickerActivity.AnonymousClass4.this.m4191lambda$$0$orgtelegramuiPhotoPickerActivity$4();
            }
        };

        AnonymousClass4() {
            PhotoPickerActivity.this = this$0;
        }

        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
        public void onSearchExpand() {
        }

        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
        public boolean canCollapseSearch() {
            PhotoPickerActivity.this.finishFragment();
            return false;
        }

        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
        public void onTextChanged(EditText editText) {
            if (editText.getText().length() == 0) {
                PhotoPickerActivity.this.searchResult.clear();
                PhotoPickerActivity.this.searchResultKeys.clear();
                PhotoPickerActivity.this.lastSearchString = null;
                PhotoPickerActivity.this.imageSearchEndReached = true;
                PhotoPickerActivity.this.searching = false;
                if (PhotoPickerActivity.this.imageReqId != 0) {
                    ConnectionsManager.getInstance(PhotoPickerActivity.this.currentAccount).cancelRequest(PhotoPickerActivity.this.imageReqId, true);
                    PhotoPickerActivity.this.imageReqId = 0;
                }
                PhotoPickerActivity.this.emptyView.title.setText(LocaleController.getString("NoRecentSearches", R.string.NoRecentSearches));
                PhotoPickerActivity.this.emptyView.showProgress(false);
                PhotoPickerActivity.this.updateSearchInterface();
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.updateSearch);
            AndroidUtilities.runOnUIThread(this.updateSearch, 1200L);
        }

        /* renamed from: lambda$$0$org-telegram-ui-PhotoPickerActivity$4 */
        public /* synthetic */ void m4191lambda$$0$orgtelegramuiPhotoPickerActivity$4() {
            PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
            photoPickerActivity.processSearch(photoPickerActivity.searchItem.getSearchField());
        }

        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
        public void onSearchPressed(EditText editText) {
            PhotoPickerActivity.this.processSearch(editText);
        }
    }

    /* renamed from: org.telegram.ui.PhotoPickerActivity$5 */
    /* loaded from: classes4.dex */
    public class AnonymousClass5 extends SizeNotifierFrameLayout {
        private boolean ignoreLayout;
        private int lastItemSize;
        private int lastNotifyWidth;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass5(Context context) {
            super(context);
            PhotoPickerActivity.this = this$0;
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int totalHeight = View.MeasureSpec.getSize(heightMeasureSpec);
            int availableWidth = View.MeasureSpec.getSize(widthMeasureSpec);
            if (AndroidUtilities.isTablet()) {
                PhotoPickerActivity.this.itemsPerRow = 4;
            } else if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y) {
                PhotoPickerActivity.this.itemsPerRow = 4;
            } else {
                PhotoPickerActivity.this.itemsPerRow = 3;
            }
            this.ignoreLayout = true;
            PhotoPickerActivity.this.itemSize = ((availableWidth - AndroidUtilities.dp(12.0f)) - AndroidUtilities.dp(10.0f)) / PhotoPickerActivity.this.itemsPerRow;
            if (this.lastItemSize != PhotoPickerActivity.this.itemSize) {
                this.lastItemSize = PhotoPickerActivity.this.itemSize;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoPickerActivity$5$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        PhotoPickerActivity.AnonymousClass5.this.m4192lambda$onMeasure$0$orgtelegramuiPhotoPickerActivity$5();
                    }
                });
            }
            if (PhotoPickerActivity.this.listSort) {
                PhotoPickerActivity.this.layoutManager.setSpanCount(1);
            } else {
                PhotoPickerActivity.this.layoutManager.setSpanCount((PhotoPickerActivity.this.itemSize * PhotoPickerActivity.this.itemsPerRow) + (AndroidUtilities.dp(5.0f) * (PhotoPickerActivity.this.itemsPerRow - 1)));
            }
            this.ignoreLayout = false;
            onMeasureInternal(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(totalHeight, C.BUFFER_FLAG_ENCRYPTED));
        }

        /* renamed from: lambda$onMeasure$0$org-telegram-ui-PhotoPickerActivity$5 */
        public /* synthetic */ void m4192lambda$onMeasure$0$orgtelegramuiPhotoPickerActivity$5() {
            PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
        }

        private void onMeasureInternal(int widthMeasureSpec, int heightMeasureSpec) {
            int heightSize;
            int heightMeasureSpec2;
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            int heightSize2 = View.MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(widthSize, heightSize2);
            int kbHeight = measureKeyboardHeight();
            int keyboardSize = SharedConfig.smoothKeyboard ? 0 : kbHeight;
            if (keyboardSize <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow && PhotoPickerActivity.this.commentTextView != null && PhotoPickerActivity.this.frameLayout2.getParent() == this) {
                int heightSize3 = heightSize2 - PhotoPickerActivity.this.commentTextView.getEmojiPadding();
                heightSize = heightSize3;
                heightMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(heightSize3, C.BUFFER_FLAG_ENCRYPTED);
            } else {
                heightMeasureSpec2 = heightMeasureSpec;
                heightSize = heightSize2;
            }
            if (kbHeight > AndroidUtilities.dp(20.0f) && PhotoPickerActivity.this.commentTextView != null) {
                this.ignoreLayout = true;
                PhotoPickerActivity.this.commentTextView.hideEmojiView();
                this.ignoreLayout = false;
            }
            if (SharedConfig.smoothKeyboard && PhotoPickerActivity.this.commentTextView != null && PhotoPickerActivity.this.commentTextView.isPopupShowing()) {
                PhotoPickerActivity.this.fragmentView.setTranslationY(0.0f);
                PhotoPickerActivity.this.listView.setTranslationY(0.0f);
                PhotoPickerActivity.this.emptyView.setTranslationY(0.0f);
            }
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child != null && child.getVisibility() != 8) {
                    if (PhotoPickerActivity.this.commentTextView != null && PhotoPickerActivity.this.commentTextView.isPopupView(child)) {
                        if (AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) {
                            if (AndroidUtilities.isTablet()) {
                                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop()), C.BUFFER_FLAG_ENCRYPTED));
                            } else {
                                child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((heightSize - AndroidUtilities.statusBarHeight) + getPaddingTop(), C.BUFFER_FLAG_ENCRYPTED));
                            }
                        } else {
                            child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, C.BUFFER_FLAG_ENCRYPTED));
                        }
                    } else {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec2, 0);
                    }
                }
            }
        }

        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        public void onLayout(boolean changed, int l, int t, int r, int b) {
            int childLeft;
            int childTop;
            if (this.lastNotifyWidth != r - l) {
                this.lastNotifyWidth = r - l;
                if (PhotoPickerActivity.this.listAdapter != null) {
                    PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                }
                if (PhotoPickerActivity.this.sendPopupWindow != null && PhotoPickerActivity.this.sendPopupWindow.isShowing()) {
                    PhotoPickerActivity.this.sendPopupWindow.dismiss();
                }
            }
            int count = getChildCount();
            int paddingBottom = 0;
            int keyboardSize = SharedConfig.smoothKeyboard ? 0 : measureKeyboardHeight();
            if (PhotoPickerActivity.this.commentTextView != null && PhotoPickerActivity.this.frameLayout2.getParent() == this && keyboardSize <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                paddingBottom = PhotoPickerActivity.this.commentTextView.getEmojiPadding();
            }
            setBottomClip(paddingBottom);
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != 8) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) child.getLayoutParams();
                    int width = child.getMeasuredWidth();
                    int height = child.getMeasuredHeight();
                    int gravity = lp.gravity;
                    if (gravity == -1) {
                        gravity = 51;
                    }
                    int absoluteGravity = gravity & 7;
                    int verticalGravity = gravity & 112;
                    switch (absoluteGravity & 7) {
                        case 1:
                            childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                            break;
                        case 5:
                            childLeft = (((r - l) - width) - lp.rightMargin) - getPaddingRight();
                            break;
                        default:
                            childLeft = lp.leftMargin + getPaddingLeft();
                            break;
                    }
                    switch (verticalGravity) {
                        case 16:
                            childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                            break;
                        case 48:
                            childTop = lp.topMargin + getPaddingTop();
                            break;
                        case UndoView.ACTION_EMAIL_COPIED /* 80 */:
                            childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                            break;
                        default:
                            childTop = lp.topMargin;
                            break;
                    }
                    if (PhotoPickerActivity.this.commentTextView != null && PhotoPickerActivity.this.commentTextView.isPopupView(child)) {
                        if (AndroidUtilities.isTablet()) {
                            childTop = getMeasuredHeight() - child.getMeasuredHeight();
                        } else {
                            childTop = (getMeasuredHeight() + keyboardSize) - child.getMeasuredHeight();
                        }
                    }
                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
            }
            notifyHeightChanged();
        }

        @Override // android.view.View, android.view.ViewParent
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PhotoPickerActivity */
    public /* synthetic */ void m4181lambda$createView$1$orgtelegramuiPhotoPickerActivity(View view, int position) {
        ArrayList<Object> arrayList;
        int type;
        if (this.selectedAlbum == null && this.searchResult.isEmpty()) {
            if (position < this.recentSearches.size()) {
                String text = this.recentSearches.get(position);
                PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate = this.searchDelegate;
                if (photoPickerActivitySearchDelegate != null) {
                    photoPickerActivitySearchDelegate.shouldSearchText(text);
                    return;
                }
                this.searchItem.getSearchField().setText(text);
                this.searchItem.getSearchField().setSelection(text.length());
                processSearch(this.searchItem.getSearchField());
                return;
            } else if (position == this.recentSearches.size() + 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("ClearSearchAlertTitle", R.string.ClearSearchAlertTitle));
                builder.setMessage(LocaleController.getString("ClearSearchAlert", R.string.ClearSearchAlert));
                builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.PhotoPickerActivity$$ExternalSyntheticLambda0
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        PhotoPickerActivity.this.m4180lambda$createView$0$orgtelegramuiPhotoPickerActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog dialog = builder.create();
                showDialog(dialog);
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dialogTextRed2));
                    return;
                }
                return;
            } else {
                return;
            }
        }
        MediaController.AlbumEntry albumEntry = this.selectedAlbum;
        if (albumEntry != null) {
            arrayList = albumEntry.photos;
        } else {
            arrayList = this.searchResult;
        }
        if (position < 0 || position >= arrayList.size()) {
            return;
        }
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            AndroidUtilities.hideKeyboard(actionBarMenuItem.getSearchField());
        }
        if (this.listSort) {
            onListItemClick(view, arrayList.get(position));
            return;
        }
        if (this.selectPhotoType == PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR || this.selectPhotoType == PhotoAlbumPickerActivity.SELECT_TYPE_AVATAR_VIDEO) {
            type = 1;
        } else if (this.selectPhotoType == PhotoAlbumPickerActivity.SELECT_TYPE_WALLPAPER) {
            type = 3;
        } else {
            int type2 = this.selectPhotoType;
            if (type2 == PhotoAlbumPickerActivity.SELECT_TYPE_QR) {
                type = 10;
            } else if (this.chatActivity == null) {
                type = 4;
            } else {
                type = 0;
            }
        }
        PhotoViewer.getInstance().setParentActivity(getParentActivity());
        PhotoViewer.getInstance().setMaxSelectedPhotos(this.maxSelectedPhotos, this.allowOrder);
        PhotoViewer.getInstance().openPhotoForSelect(arrayList, position, type, this.isDocumentsPicker, this.provider, this.chatActivity);
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PhotoPickerActivity */
    public /* synthetic */ void m4180lambda$createView$0$orgtelegramuiPhotoPickerActivity(DialogInterface dialogInterface, int i) {
        PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate = this.searchDelegate;
        if (photoPickerActivitySearchDelegate != null) {
            photoPickerActivitySearchDelegate.shouldClearRecentSearch();
        } else {
            clearRecentSearch();
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PhotoPickerActivity */
    public /* synthetic */ boolean m4182lambda$createView$2$orgtelegramuiPhotoPickerActivity(View view, int position) {
        if (this.listSort) {
            onListItemClick(view, this.selectedAlbum.photos.get(position));
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

    public static /* synthetic */ boolean lambda$createView$3(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-PhotoPickerActivity */
    public /* synthetic */ void m4183lambda$createView$4$orgtelegramuiPhotoPickerActivity(View v) {
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity != null && chatActivity.isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new PhotoPickerActivity$$ExternalSyntheticLambda1(this));
        } else {
            sendSelectedPhotos(true, 0);
        }
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-PhotoPickerActivity */
    public /* synthetic */ boolean m4186lambda$createView$7$orgtelegramuiPhotoPickerActivity(View view) {
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity == null || this.maxSelectedPhotos == 1) {
            return false;
        }
        chatActivity.getCurrentChat();
        TLRPC.User user = this.chatActivity.getCurrentUser();
        if (this.sendPopupLayout == null) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity());
            this.sendPopupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setAnimationEnabled(false);
            this.sendPopupLayout.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.PhotoPickerActivity.14
                private Rect popupRect = new Rect();

                {
                    PhotoPickerActivity.this = this;
                }

                @Override // android.view.View.OnTouchListener
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == 0 && PhotoPickerActivity.this.sendPopupWindow != null && PhotoPickerActivity.this.sendPopupWindow.isShowing()) {
                        v.getHitRect(this.popupRect);
                        if (!this.popupRect.contains((int) event.getX(), (int) event.getY())) {
                            PhotoPickerActivity.this.sendPopupWindow.dismiss();
                            return false;
                        }
                        return false;
                    }
                    return false;
                }
            });
            this.sendPopupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() { // from class: org.telegram.ui.PhotoPickerActivity$$ExternalSyntheticLambda12
                @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow.OnDispatchKeyEventListener
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    PhotoPickerActivity.this.m4184lambda$createView$5$orgtelegramuiPhotoPickerActivity(keyEvent);
                }
            });
            this.sendPopupLayout.setShownFromBottom(false);
            this.itemCells = new ActionBarMenuSubItem[2];
            int a = 0;
            while (a < 2) {
                if ((a != 0 || this.chatActivity.canScheduleMessage()) && (a != 1 || !UserObject.isUserSelf(user))) {
                    final int num = a;
                    this.itemCells[a] = new ActionBarMenuSubItem(getParentActivity(), a == 0, a == 1);
                    if (num == 0) {
                        if (UserObject.isUserSelf(user)) {
                            this.itemCells[a].setTextAndIcon(LocaleController.getString("SetReminder", R.string.SetReminder), R.drawable.msg_calendar2);
                        } else {
                            this.itemCells[a].setTextAndIcon(LocaleController.getString("ScheduleMessage", R.string.ScheduleMessage), R.drawable.msg_calendar2);
                        }
                    } else {
                        this.itemCells[a].setTextAndIcon(LocaleController.getString("SendWithoutSound", R.string.SendWithoutSound), R.drawable.input_notify_off);
                    }
                    this.itemCells[a].setMinimumWidth(AndroidUtilities.dp(196.0f));
                    this.sendPopupLayout.addView((View) this.itemCells[a], LayoutHelper.createLinear(-1, 48));
                    this.itemCells[a].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoPickerActivity$$ExternalSyntheticLambda5
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            PhotoPickerActivity.this.m4185lambda$createView$6$orgtelegramuiPhotoPickerActivity(num, view2);
                        }
                    });
                }
                a++;
            }
            this.sendPopupLayout.setupRadialSelectors(Theme.getColor(this.selectorKey));
            ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(this.sendPopupLayout, -2, -2);
            this.sendPopupWindow = actionBarPopupWindow;
            actionBarPopupWindow.setAnimationEnabled(false);
            this.sendPopupWindow.setAnimationStyle(R.style.PopupContextAnimation2);
            this.sendPopupWindow.setOutsideTouchable(true);
            this.sendPopupWindow.setClippingEnabled(true);
            this.sendPopupWindow.setInputMethodMode(2);
            this.sendPopupWindow.setSoftInputMode(0);
            this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        }
        this.sendPopupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        this.sendPopupWindow.showAtLocation(view, 51, ((location[0] + view.getMeasuredWidth()) - this.sendPopupLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (location[1] - this.sendPopupLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f));
        this.sendPopupWindow.dimBehind();
        view.performHapticFeedback(3, 2);
        return false;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-PhotoPickerActivity */
    public /* synthetic */ void m4184lambda$createView$5$orgtelegramuiPhotoPickerActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-PhotoPickerActivity */
    public /* synthetic */ void m4185lambda$createView$6$orgtelegramuiPhotoPickerActivity(int num, View v) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        if (num == 0) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.chatActivity.getDialogId(), new PhotoPickerActivity$$ExternalSyntheticLambda1(this));
        } else {
            sendSelectedPhotos(true, 0);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    protected void onPanTranslationUpdate(float y) {
        if (this.listView == null) {
            return;
        }
        if (this.commentTextView.isPopupShowing()) {
            this.fragmentView.setTranslationY(y);
            this.listView.setTranslationY(0.0f);
            return;
        }
        this.listView.setTranslationY(y);
    }

    public void setLayoutViews(FrameLayout f2, FrameLayout button, View count, View s, EditTextEmoji emoji) {
        this.frameLayout2 = f2;
        this.writeButtonContainer = button;
        this.commentTextView = emoji;
        this.selectedCountView = count;
        this.shadow = s;
        this.needsBottomLayout = false;
    }

    private void applyCaption() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || editTextEmoji.length() <= 0) {
            return;
        }
        Object imageId = this.selectedPhotosOrder.get(0);
        Object entry = this.selectedPhotos.get(imageId);
        if (entry instanceof MediaController.PhotoEntry) {
            MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) entry;
            photoEntry.caption = this.commentTextView.getText().toString();
        } else if (entry instanceof MediaController.SearchImage) {
            MediaController.SearchImage searchImage = (MediaController.SearchImage) entry;
            searchImage.caption = this.commentTextView.getText().toString();
        }
    }

    private void onListItemClick(View view, Object item) {
        boolean add;
        if (addToSelectedPhotos(item, -1) == -1) {
            add = true;
        } else {
            add = false;
        }
        int i = 1;
        if (view instanceof SharedDocumentCell) {
            Integer index = (Integer) view.getTag();
            MediaController.PhotoEntry photoEntry = this.selectedAlbum.photos.get(index.intValue());
            SharedDocumentCell cell = (SharedDocumentCell) view;
            cell.setChecked(this.selectedPhotosOrder.contains(Integer.valueOf(photoEntry.imageId)), true);
        }
        if (!add) {
            i = 2;
        }
        updatePhotosButton(i);
        this.delegate.selectedPhotosChanged();
    }

    public void clearRecentSearch() {
        this.recentSearches.clear();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        this.emptyView.showProgress(false);
        saveRecentSearch();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.openSearch(true);
            if (!TextUtils.isEmpty(this.initialSearchString)) {
                this.searchItem.setSearchFieldText(this.initialSearchString, false);
                this.initialSearchString = null;
                processSearch(this.searchItem.getSearchField());
            }
            getParentActivity().getWindow().setSoftInputMode(SharedConfig.smoothKeyboard ? 32 : 16);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public RecyclerListView getListView() {
        return this.listView;
    }

    public void setCaption(CharSequence text) {
        this.caption = text;
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.setText(text);
        }
    }

    public void setInitialSearchString(String text) {
        this.initialSearchString = text;
    }

    private void saveRecentSearch() {
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("web_recent_search", 0).edit();
        editor.clear();
        editor.putInt(NotificationBadge.NewHtcHomeBadger.COUNT, this.recentSearches.size());
        int N = this.recentSearches.size();
        for (int a = 0; a < N; a++) {
            editor.putString("recent" + a, this.recentSearches.get(a));
        }
        editor.commit();
    }

    private void loadRecentSearch() {
        SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("web_recent_search", 0);
        int count = preferences.getInt(NotificationBadge.NewHtcHomeBadger.COUNT, 0);
        for (int a = 0; a < count; a++) {
            String str = preferences.getString("recent" + a, null);
            if (str != null) {
                this.recentSearches.add(str);
            } else {
                return;
            }
        }
    }

    private void addToRecentSearches(String query) {
        int a = 0;
        int N = this.recentSearches.size();
        while (true) {
            if (a >= N) {
                break;
            }
            String str = this.recentSearches.get(a);
            if (!str.equalsIgnoreCase(query)) {
                a++;
            } else {
                this.recentSearches.remove(a);
                break;
            }
        }
        this.recentSearches.add(0, query);
        while (this.recentSearches.size() > 20) {
            ArrayList<String> arrayList = this.recentSearches;
            arrayList.remove(arrayList.size() - 1);
        }
        saveRecentSearch();
    }

    public void processSearch(EditText editText) {
        if (editText.getText().length() == 0) {
            return;
        }
        String text = editText.getText().toString();
        this.searchResult.clear();
        this.searchResultKeys.clear();
        this.imageSearchEndReached = true;
        searchImages(this.type == 1, text, "", true);
        this.lastSearchString = text;
        if (text.length() == 0) {
            this.lastSearchString = null;
            this.emptyView.title.setText(LocaleController.getString("NoRecentSearches", R.string.NoRecentSearches));
        } else {
            this.emptyView.title.setText(LocaleController.formatString("NoResultFoundFor", R.string.NoResultFoundFor, this.lastSearchString));
        }
        updateSearchInterface();
    }

    private boolean showCommentTextView(final boolean show, boolean animated) {
        if (this.commentTextView == null) {
            return false;
        }
        if (show == (this.frameLayout2.getTag() != null)) {
            return false;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.frameLayout2.setTag(show ? 1 : null);
        if (this.commentTextView.getEditText().isFocused()) {
            AndroidUtilities.hideKeyboard(this.commentTextView.getEditText());
        }
        this.commentTextView.hidePopup(true);
        if (show) {
            this.frameLayout2.setVisibility(0);
            this.writeButtonContainer.setVisibility(0);
        }
        float f = 0.0f;
        float f2 = 0.2f;
        float f3 = 1.0f;
        if (animated) {
            this.animatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            FrameLayout frameLayout = this.writeButtonContainer;
            Property property = View.SCALE_X;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(frameLayout, property, fArr));
            FrameLayout frameLayout2 = this.writeButtonContainer;
            Property property2 = View.SCALE_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(frameLayout2, property2, fArr2));
            FrameLayout frameLayout3 = this.writeButtonContainer;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = show ? 1.0f : 0.0f;
            animators.add(ObjectAnimator.ofFloat(frameLayout3, property3, fArr3));
            View view = this.selectedCountView;
            Property property4 = View.SCALE_X;
            float[] fArr4 = new float[1];
            fArr4[0] = show ? 1.0f : 0.2f;
            animators.add(ObjectAnimator.ofFloat(view, property4, fArr4));
            View view2 = this.selectedCountView;
            Property property5 = View.SCALE_Y;
            float[] fArr5 = new float[1];
            if (show) {
                f2 = 1.0f;
            }
            fArr5[0] = f2;
            animators.add(ObjectAnimator.ofFloat(view2, property5, fArr5));
            View view3 = this.selectedCountView;
            Property property6 = View.ALPHA;
            float[] fArr6 = new float[1];
            if (!show) {
                f3 = 0.0f;
            }
            fArr6[0] = f3;
            animators.add(ObjectAnimator.ofFloat(view3, property6, fArr6));
            FrameLayout frameLayout4 = this.frameLayout2;
            Property property7 = View.TRANSLATION_Y;
            float[] fArr7 = new float[1];
            fArr7[0] = show ? 0.0f : AndroidUtilities.dp(48.0f);
            animators.add(ObjectAnimator.ofFloat(frameLayout4, property7, fArr7));
            View view4 = this.shadow;
            Property property8 = View.TRANSLATION_Y;
            float[] fArr8 = new float[1];
            if (!show) {
                f = AndroidUtilities.dp(48.0f);
            }
            fArr8[0] = f;
            animators.add(ObjectAnimator.ofFloat(view4, property8, fArr8));
            this.animatorSet.playTogether(animators);
            this.animatorSet.setInterpolator(new DecelerateInterpolator());
            this.animatorSet.setDuration(180L);
            this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoPickerActivity.16
                {
                    PhotoPickerActivity.this = this;
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (animation.equals(PhotoPickerActivity.this.animatorSet)) {
                        if (!show) {
                            PhotoPickerActivity.this.frameLayout2.setVisibility(4);
                            PhotoPickerActivity.this.writeButtonContainer.setVisibility(4);
                        }
                        PhotoPickerActivity.this.animatorSet = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (animation.equals(PhotoPickerActivity.this.animatorSet)) {
                        PhotoPickerActivity.this.animatorSet = null;
                    }
                }
            });
            this.animatorSet.start();
        } else {
            this.writeButtonContainer.setScaleX(show ? 1.0f : 0.2f);
            this.writeButtonContainer.setScaleY(show ? 1.0f : 0.2f);
            this.writeButtonContainer.setAlpha(show ? 1.0f : 0.0f);
            this.selectedCountView.setScaleX(show ? 1.0f : 0.2f);
            View view5 = this.selectedCountView;
            if (show) {
                f2 = 1.0f;
            }
            view5.setScaleY(f2);
            View view6 = this.selectedCountView;
            if (!show) {
                f3 = 0.0f;
            }
            view6.setAlpha(f3);
            this.frameLayout2.setTranslationY(show ? 0.0f : AndroidUtilities.dp(48.0f));
            View view7 = this.shadow;
            if (!show) {
                f = AndroidUtilities.dp(48.0f);
            }
            view7.setTranslationY(f);
            if (!show) {
                this.frameLayout2.setVisibility(4);
                this.writeButtonContainer.setVisibility(4);
            }
        }
        return true;
    }

    public void setMaxSelectedPhotos(int value, boolean order) {
        this.maxSelectedPhotos = value;
        this.allowOrder = order;
        if (value > 0 && this.type == 1) {
            this.maxSelectedPhotos = 1;
        }
    }

    public void updateCheckedPhotoIndices() {
        if (!this.allowIndices) {
            return;
        }
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.listView.getChildAt(a);
            if (view instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                Integer index = (Integer) view.getTag();
                MediaController.AlbumEntry albumEntry = this.selectedAlbum;
                int i = -1;
                if (albumEntry != null) {
                    MediaController.PhotoEntry photoEntry = albumEntry.photos.get(index.intValue());
                    if (this.allowIndices) {
                        i = this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                    }
                    cell.setNum(i);
                } else {
                    MediaController.SearchImage photoEntry2 = this.searchResult.get(index.intValue());
                    if (this.allowIndices) {
                        i = this.selectedPhotosOrder.indexOf(photoEntry2.id);
                    }
                    cell.setNum(i);
                }
            } else if (view instanceof SharedDocumentCell) {
                MediaController.PhotoEntry photoEntry3 = this.selectedAlbum.photos.get(((Integer) view.getTag()).intValue());
                ((SharedDocumentCell) view).setChecked(this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry3.imageId)) != 0, false);
            }
        }
    }

    public PhotoAttachPhotoCell getCellForIndex(int index) {
        int count = this.listView.getChildCount();
        for (int a = 0; a < count; a++) {
            View view = this.listView.getChildAt(a);
            if (view instanceof PhotoAttachPhotoCell) {
                PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) view;
                int num = ((Integer) cell.getTag()).intValue();
                MediaController.AlbumEntry albumEntry = this.selectedAlbum;
                if (albumEntry == null ? !(num < 0 || num >= this.searchResult.size()) : !(num < 0 || num >= albumEntry.photos.size())) {
                    if (num == index) {
                        return cell;
                    }
                }
            }
        }
        return null;
    }

    public int addToSelectedPhotos(Object object, int index) {
        Object key = null;
        if (object instanceof MediaController.PhotoEntry) {
            key = Integer.valueOf(((MediaController.PhotoEntry) object).imageId);
        } else if (object instanceof MediaController.SearchImage) {
            key = ((MediaController.SearchImage) object).id;
        }
        if (key == null) {
            return -1;
        }
        if (this.selectedPhotos.containsKey(key)) {
            this.selectedPhotos.remove(key);
            int position = this.selectedPhotosOrder.indexOf(key);
            if (position >= 0) {
                this.selectedPhotosOrder.remove(position);
            }
            if (this.allowIndices) {
                updateCheckedPhotoIndices();
            }
            if (index >= 0) {
                if (object instanceof MediaController.PhotoEntry) {
                    ((MediaController.PhotoEntry) object).reset();
                } else if (object instanceof MediaController.SearchImage) {
                    ((MediaController.SearchImage) object).reset();
                }
                this.provider.updatePhotoAtIndex(index);
            }
            return position;
        }
        this.selectedPhotos.put(key, object);
        this.selectedPhotosOrder.add(key);
        return -1;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        ActionBarMenuItem actionBarMenuItem;
        if (isOpen && (actionBarMenuItem = this.searchItem) != null) {
            AndroidUtilities.showKeyboard(actionBarMenuItem.getSearchField());
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null && editTextEmoji.isPopupShowing()) {
            this.commentTextView.hidePopup(true);
            return false;
        }
        return super.onBackPressed();
    }

    public void updatePhotosButton(int animated) {
        int count = this.selectedPhotos.size();
        boolean z = true;
        if (count == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            if (animated == 0) {
                z = false;
            }
            showCommentTextView(false, z);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true, animated != 0) || animated == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            return;
        }
        this.selectedCountView.setPivotX(AndroidUtilities.dp(21.0f));
        this.selectedCountView.setPivotY(AndroidUtilities.dp(12.0f));
        AnimatorSet animatorSet = new AnimatorSet();
        Animator[] animatorArr = new Animator[2];
        View view = this.selectedCountView;
        Property property = View.SCALE_X;
        float[] fArr = new float[2];
        float f = 1.1f;
        fArr[0] = animated == 1 ? 1.1f : 0.9f;
        fArr[1] = 1.0f;
        animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
        View view2 = this.selectedCountView;
        Property property2 = View.SCALE_Y;
        float[] fArr2 = new float[2];
        if (animated != 1) {
            f = 0.9f;
        }
        fArr2[0] = f;
        fArr2[1] = 1.0f;
        animatorArr[1] = ObjectAnimator.ofFloat(view2, property2, fArr2);
        animatorSet.playTogether(animatorArr);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(180L);
        animatorSet.start();
    }

    public void updateSearchInterface() {
        String str;
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        if (this.searching || (this.recentSearches.size() > 0 && ((str = this.lastSearchString) == null || TextUtils.isEmpty(str)))) {
            this.emptyView.showProgress(true);
        } else {
            this.emptyView.showProgress(false);
        }
    }

    private void searchBotUser(final boolean gif) {
        if (this.searchingUser) {
            return;
        }
        this.searchingUser = true;
        TLRPC.TL_contacts_resolveUsername req = new TLRPC.TL_contacts_resolveUsername();
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        req.username = gif ? messagesController.gifSearchBot : messagesController.imageSearchBot;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PhotoPickerActivity$$ExternalSyntheticLambda11
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PhotoPickerActivity.this.m4188lambda$searchBotUser$9$orgtelegramuiPhotoPickerActivity(gif, tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$searchBotUser$9$org-telegram-ui-PhotoPickerActivity */
    public /* synthetic */ void m4188lambda$searchBotUser$9$orgtelegramuiPhotoPickerActivity(final boolean gif, final TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoPickerActivity$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    PhotoPickerActivity.this.m4187lambda$searchBotUser$8$orgtelegramuiPhotoPickerActivity(response, gif);
                }
            });
        }
    }

    /* renamed from: lambda$searchBotUser$8$org-telegram-ui-PhotoPickerActivity */
    public /* synthetic */ void m4187lambda$searchBotUser$8$orgtelegramuiPhotoPickerActivity(TLObject response, boolean gif) {
        TLRPC.TL_contacts_resolvedPeer res = (TLRPC.TL_contacts_resolvedPeer) response;
        MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(res.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
        String str = this.lastSearchImageString;
        this.lastSearchImageString = null;
        searchImages(gif, str, "", false);
    }

    public void searchImages(final boolean gif, final String query, String offset, boolean searchUser) {
        if (this.searching) {
            this.searching = false;
            if (this.imageReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.imageReqId, true);
                this.imageReqId = 0;
            }
        }
        this.lastSearchImageString = query;
        this.searching = true;
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        MessagesController messagesController2 = MessagesController.getInstance(this.currentAccount);
        TLObject object = messagesController.getUserOrChat(gif ? messagesController2.gifSearchBot : messagesController2.imageSearchBot);
        if (!(object instanceof TLRPC.User)) {
            if (searchUser) {
                searchBotUser(gif);
                return;
            }
            return;
        }
        final TLRPC.User user = (TLRPC.User) object;
        TLRPC.TL_messages_getInlineBotResults req = new TLRPC.TL_messages_getInlineBotResults();
        req.query = query == null ? "" : query;
        req.bot = MessagesController.getInstance(this.currentAccount).getInputUser(user);
        req.offset = offset;
        ChatActivity chatActivity = this.chatActivity;
        if (chatActivity != null) {
            long dialogId = chatActivity.getDialogId();
            if (DialogObject.isEncryptedDialog(dialogId)) {
                req.peer = new TLRPC.TL_inputPeerEmpty();
            } else {
                req.peer = getMessagesController().getInputPeer(dialogId);
            }
        } else {
            req.peer = new TLRPC.TL_inputPeerEmpty();
        }
        final int token = this.lastSearchToken + 1;
        this.lastSearchToken = token;
        this.imageReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.PhotoPickerActivity$$ExternalSyntheticLambda10
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PhotoPickerActivity.this.m4190lambda$searchImages$11$orgtelegramuiPhotoPickerActivity(query, token, gif, user, tLObject, tL_error);
            }
        });
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(this.imageReqId, this.classGuid);
    }

    /* renamed from: lambda$searchImages$11$org-telegram-ui-PhotoPickerActivity */
    public /* synthetic */ void m4190lambda$searchImages$11$orgtelegramuiPhotoPickerActivity(final String query, final int token, final boolean gif, final TLRPC.User user, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoPickerActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                PhotoPickerActivity.this.m4189lambda$searchImages$10$orgtelegramuiPhotoPickerActivity(query, token, response, gif, user);
            }
        });
    }

    /* renamed from: lambda$searchImages$10$org-telegram-ui-PhotoPickerActivity */
    public /* synthetic */ void m4189lambda$searchImages$10$orgtelegramuiPhotoPickerActivity(String query, int token, TLObject response, boolean gif, TLRPC.User user) {
        TLRPC.PhotoSize size;
        addToRecentSearches(query);
        if (token != this.lastSearchToken) {
            return;
        }
        int addedCount = 0;
        int oldCount = this.searchResult.size();
        if (response != null) {
            TLRPC.messages_BotResults res = (TLRPC.messages_BotResults) response;
            this.nextImagesSearchOffset = res.next_offset;
            int count = res.results.size();
            for (int a = 0; a < count; a++) {
                TLRPC.BotInlineResult result = res.results.get(a);
                if ((gif || "photo".equals(result.type)) && ((!gif || "gif".equals(result.type)) && !this.searchResultKeys.containsKey(result.id))) {
                    MediaController.SearchImage image = new MediaController.SearchImage();
                    if (gif && result.document != null) {
                        for (int b = 0; b < result.document.attributes.size(); b++) {
                            TLRPC.DocumentAttribute attribute = result.document.attributes.get(b);
                            if ((attribute instanceof TLRPC.TL_documentAttributeImageSize) || (attribute instanceof TLRPC.TL_documentAttributeVideo)) {
                                image.width = attribute.w;
                                image.height = attribute.h;
                                break;
                            }
                        }
                        image.document = result.document;
                        image.size = 0;
                        if (result.photo != null && (size = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, this.itemSize, true)) != null) {
                            result.document.thumbs.add(size);
                            result.document.flags |= 1;
                        }
                    } else {
                        if (!gif && result.photo != null) {
                            TLRPC.PhotoSize size2 = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, AndroidUtilities.getPhotoSize());
                            TLRPC.PhotoSize size22 = FileLoader.getClosestPhotoSizeWithSize(result.photo.sizes, GroupCallActivity.TABLET_LIST_SIZE);
                            if (size2 != null) {
                                image.width = size2.w;
                                image.height = size2.h;
                                image.photoSize = size2;
                                image.photo = result.photo;
                                image.size = size2.size;
                                image.thumbPhotoSize = size22;
                            }
                        } else if (result.content != null) {
                            int b2 = 0;
                            while (true) {
                                if (b2 >= result.content.attributes.size()) {
                                    break;
                                }
                                TLRPC.DocumentAttribute attribute2 = result.content.attributes.get(b2);
                                if (!(attribute2 instanceof TLRPC.TL_documentAttributeImageSize)) {
                                    b2++;
                                } else {
                                    image.width = attribute2.w;
                                    image.height = attribute2.h;
                                    break;
                                }
                            }
                            if (result.thumb != null) {
                                image.thumbUrl = result.thumb.url;
                            } else {
                                image.thumbUrl = null;
                            }
                            image.imageUrl = result.content.url;
                            image.size = gif ? 0 : result.content.size;
                        }
                    }
                    image.id = result.id;
                    image.type = gif ? 1 : 0;
                    image.inlineResult = result;
                    image.params = new HashMap<>();
                    image.params.put("id", result.id);
                    image.params.put("query_id", "" + res.query_id);
                    image.params.put("bot_name", user.username);
                    this.searchResult.add(image);
                    this.searchResultKeys.put(image.id, image);
                    addedCount++;
                }
            }
            this.imageSearchEndReached = oldCount == this.searchResult.size() || this.nextImagesSearchOffset == null;
        }
        this.searching = false;
        if (addedCount != 0) {
            this.listAdapter.notifyItemRangeInserted(oldCount, addedCount);
        } else if (this.imageSearchEndReached) {
            this.listAdapter.notifyItemRemoved(this.searchResult.size() - 1);
        }
        if (this.searchResult.size() <= 0) {
            this.emptyView.showProgress(false);
        }
    }

    public void setDelegate(PhotoPickerActivityDelegate photoPickerActivityDelegate) {
        this.delegate = photoPickerActivityDelegate;
    }

    public void setSearchDelegate(PhotoPickerActivitySearchDelegate photoPickerActivitySearchDelegate) {
        this.searchDelegate = photoPickerActivitySearchDelegate;
    }

    public void sendSelectedPhotos(boolean notify, int scheduleDate) {
        if (this.selectedPhotos.isEmpty() || this.delegate == null || this.sendPressed) {
            return;
        }
        applyCaption();
        this.sendPressed = true;
        this.delegate.actionButtonPressed(false, notify, scheduleDate);
        if (this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_WALLPAPER) {
            finishFragment();
        }
    }

    /* loaded from: classes4.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            PhotoPickerActivity.this = r1;
            this.mContext = context;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            if (PhotoPickerActivity.this.selectedAlbum == null) {
                return TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString) ? holder.getItemViewType() == 3 : holder.getAdapterPosition() < PhotoPickerActivity.this.searchResult.size();
            }
            return true;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (PhotoPickerActivity.this.selectedAlbum == null) {
                if (PhotoPickerActivity.this.searchResult.isEmpty()) {
                    if (TextUtils.isEmpty(PhotoPickerActivity.this.lastSearchString) && !PhotoPickerActivity.this.recentSearches.isEmpty()) {
                        return PhotoPickerActivity.this.recentSearches.size() + 2;
                    }
                    return 0;
                }
                return PhotoPickerActivity.this.searchResult.size() + (!PhotoPickerActivity.this.imageSearchEndReached ? 1 : 0);
            }
            return PhotoPickerActivity.this.selectedAlbum.photos.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    PhotoAttachPhotoCell cell = new PhotoAttachPhotoCell(this.mContext, null);
                    cell.setDelegate(new PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate() { // from class: org.telegram.ui.PhotoPickerActivity.ListAdapter.1
                        {
                            ListAdapter.this = this;
                        }

                        private void checkSlowMode() {
                            TLRPC.Chat chat;
                            if (PhotoPickerActivity.this.allowOrder && PhotoPickerActivity.this.chatActivity != null && (chat = PhotoPickerActivity.this.chatActivity.getCurrentChat()) != null && !ChatObject.hasAdminRights(chat) && chat.slowmode_enabled && PhotoPickerActivity.this.alertOnlyOnce != 2) {
                                AlertsCreator.showSimpleAlert(PhotoPickerActivity.this, LocaleController.getString("Slowmode", R.string.Slowmode), LocaleController.getString("SlowmodeSelectSendError", R.string.SlowmodeSelectSendError));
                                if (PhotoPickerActivity.this.alertOnlyOnce == 1) {
                                    PhotoPickerActivity.this.alertOnlyOnce = 2;
                                }
                            }
                        }

                        @Override // org.telegram.ui.Cells.PhotoAttachPhotoCell.PhotoAttachPhotoCellDelegate
                        public void onCheckClick(PhotoAttachPhotoCell v) {
                            boolean added;
                            int index = ((Integer) v.getTag()).intValue();
                            int num = -1;
                            int i = 1;
                            if (PhotoPickerActivity.this.selectedAlbum != null) {
                                MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(index);
                                added = !PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
                                if (!added || PhotoPickerActivity.this.maxSelectedPhotos <= 0 || PhotoPickerActivity.this.selectedPhotos.size() < PhotoPickerActivity.this.maxSelectedPhotos) {
                                    if (PhotoPickerActivity.this.allowIndices && added) {
                                        num = PhotoPickerActivity.this.selectedPhotosOrder.size();
                                    }
                                    v.setChecked(num, added, true);
                                    PhotoPickerActivity.this.addToSelectedPhotos(photoEntry, index);
                                } else {
                                    checkSlowMode();
                                    return;
                                }
                            } else {
                                AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
                                MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(index);
                                added = !PhotoPickerActivity.this.selectedPhotos.containsKey(photoEntry2.id);
                                if (!added || PhotoPickerActivity.this.maxSelectedPhotos <= 0 || PhotoPickerActivity.this.selectedPhotos.size() < PhotoPickerActivity.this.maxSelectedPhotos) {
                                    if (PhotoPickerActivity.this.allowIndices && added) {
                                        num = PhotoPickerActivity.this.selectedPhotosOrder.size();
                                    }
                                    v.setChecked(num, added, true);
                                    PhotoPickerActivity.this.addToSelectedPhotos(photoEntry2, index);
                                } else {
                                    checkSlowMode();
                                    return;
                                }
                            }
                            PhotoPickerActivity photoPickerActivity = PhotoPickerActivity.this;
                            if (!added) {
                                i = 2;
                            }
                            photoPickerActivity.updatePhotosButton(i);
                            PhotoPickerActivity.this.delegate.selectedPhotosChanged();
                        }
                    });
                    cell.getCheckFrame().setVisibility(PhotoPickerActivity.this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_ALL ? 8 : 0);
                    view = cell;
                    break;
                case 1:
                    FrameLayout frameLayout = new FrameLayout(this.mContext);
                    frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    RadialProgressView progressBar = new RadialProgressView(this.mContext);
                    progressBar.setProgressColor(-11371101);
                    frameLayout.addView(progressBar, LayoutHelper.createFrame(-1, -1.0f));
                    view = frameLayout;
                    break;
                case 2:
                    view = new SharedDocumentCell(this.mContext, 1);
                    break;
                case 3:
                    View view2 = new TextCell(this.mContext, 23, true);
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    view = view2;
                    if (PhotoPickerActivity.this.forceDarckTheme) {
                        TextCell textCell = (TextCell) view2;
                        textCell.textView.setTextColor(Theme.getColor(PhotoPickerActivity.this.textKey));
                        textCell.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_voipgroup_mutedIcon), PorterDuff.Mode.MULTIPLY));
                        view = view2;
                        break;
                    }
                    break;
                default:
                    View view3 = new DividerCell(this.mContext);
                    ((DividerCell) view3).setForceDarkTheme(PhotoPickerActivity.this.forceDarckTheme);
                    view = view3;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean showing;
            int i = -1;
            int i2 = 0;
            switch (holder.getItemViewType()) {
                case 0:
                    PhotoAttachPhotoCell cell = (PhotoAttachPhotoCell) holder.itemView;
                    cell.setItemSize(PhotoPickerActivity.this.itemSize);
                    BackupImageView imageView = cell.getImageView();
                    cell.setTag(Integer.valueOf(position));
                    imageView.setOrientation(0, true);
                    if (PhotoPickerActivity.this.selectedAlbum != null) {
                        MediaController.PhotoEntry photoEntry = PhotoPickerActivity.this.selectedAlbum.photos.get(position);
                        cell.setPhotoEntry(photoEntry, true, false);
                        if (PhotoPickerActivity.this.allowIndices) {
                            i = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntry.imageId));
                        }
                        cell.setChecked(i, PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId)), false);
                        showing = PhotoViewer.isShowingImage(photoEntry.path);
                    } else {
                        MediaController.SearchImage photoEntry2 = (MediaController.SearchImage) PhotoPickerActivity.this.searchResult.get(position);
                        cell.setPhotoEntry(photoEntry2, true, false);
                        cell.getVideoInfoContainer().setVisibility(4);
                        if (PhotoPickerActivity.this.allowIndices) {
                            i = PhotoPickerActivity.this.selectedPhotosOrder.indexOf(photoEntry2.id);
                        }
                        cell.setChecked(i, PhotoPickerActivity.this.selectedPhotos.containsKey(photoEntry2.id), false);
                        showing = PhotoViewer.isShowingImage(photoEntry2.getPathToAttach());
                    }
                    imageView.getImageReceiver().setVisible(!showing, true);
                    CheckBox2 checkBox = cell.getCheckBox();
                    if (PhotoPickerActivity.this.selectPhotoType != PhotoAlbumPickerActivity.SELECT_TYPE_ALL || showing) {
                        i2 = 8;
                    }
                    checkBox.setVisibility(i2);
                    return;
                case 1:
                    ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                    if (params != null) {
                        params.width = -1;
                        params.height = PhotoPickerActivity.this.itemSize;
                        holder.itemView.setLayoutParams(params);
                        return;
                    }
                    return;
                case 2:
                    MediaController.PhotoEntry photoEntry3 = PhotoPickerActivity.this.selectedAlbum.photos.get(position);
                    SharedDocumentCell documentCell = (SharedDocumentCell) holder.itemView;
                    documentCell.setPhotoEntry(photoEntry3);
                    documentCell.setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(photoEntry3.imageId)), false);
                    documentCell.setTag(Integer.valueOf(position));
                    return;
                case 3:
                    TextCell cell2 = (TextCell) holder.itemView;
                    if (position < PhotoPickerActivity.this.recentSearches.size()) {
                        cell2.setTextAndIcon((String) PhotoPickerActivity.this.recentSearches.get(position), R.drawable.msg_recent, false);
                        return;
                    } else {
                        cell2.setTextAndIcon(LocaleController.getString("ClearRecentHistory", R.string.ClearRecentHistory), R.drawable.msg_clear_recent, false);
                        return;
                    }
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (!PhotoPickerActivity.this.listSort) {
                if (PhotoPickerActivity.this.selectedAlbum != null) {
                    return 0;
                }
                return PhotoPickerActivity.this.searchResult.isEmpty() ? position == PhotoPickerActivity.this.recentSearches.size() ? 4 : 3 : position < PhotoPickerActivity.this.searchResult.size() ? 0 : 1;
            }
            return 2;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.sizeNotifierFrameLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, this.dialogBackgroundKey));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, this.dialogBackgroundKey));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, this.textKey));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, this.textKey));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, this.selectorKey));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, this.textKey));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_chat_messagePanelHint));
        ActionBarMenuItem actionBarMenuItem = this.searchItem;
        themeDescriptions.add(new ThemeDescription(actionBarMenuItem != null ? actionBarMenuItem.getSearchField() : null, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, this.textKey));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, this.dialogBackgroundKey));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{Theme.chat_attachEmptyDrawable}, null, Theme.key_chat_attachEmptyImage));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, null, null, Theme.key_chat_attachPhotoBackground));
        return themeDescriptions;
    }
}
