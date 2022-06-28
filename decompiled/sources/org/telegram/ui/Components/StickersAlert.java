package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionValues;
import android.util.Property;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.ContentPreviewViewer;
/* loaded from: classes5.dex */
public class StickersAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private GridAdapter adapter;
    private List<ThemeDescription> animatingDescriptions;
    private String buttonTextColorKey;
    private int checkReqId;
    private Runnable checkRunnable;
    private boolean clearsInputField;
    private StickersAlertCustomButtonDelegate customButtonDelegate;
    private StickersAlertDelegate delegate;
    private FrameLayout emptyView;
    private RecyclerListView gridView;
    private boolean ignoreLayout;
    private String importingSoftware;
    private ArrayList<Parcelable> importingStickers;
    private ArrayList<SendMessagesHelper.ImportingSticker> importingStickersPaths;
    private TLRPC.InputStickerSet inputStickerSet;
    private StickersAlertInstallDelegate installDelegate;
    private int itemSize;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private GridLayoutManager layoutManager;
    private Runnable onDismissListener;
    private ActionBarMenuItem optionsButton;
    private Activity parentActivity;
    private BaseFragment parentFragment;
    private FrameLayout pickerBottomFrameLayout;
    private TextView pickerBottomLayout;
    private ContentPreviewViewer.ContentPreviewViewerDelegate previewDelegate;
    private TextView previewSendButton;
    private View previewSendButtonShadow;
    private int reqId;
    private int scrollOffsetY;
    private TLRPC.Document selectedSticker;
    private SendMessagesHelper.ImportingSticker selectedStickerPath;
    private String setTitle;
    private View[] shadow;
    private AnimatorSet[] shadowAnimation;
    private boolean showEmoji;
    private boolean showTooltipWhenToggle;
    private TextView stickerEmojiTextView;
    private BackupImageView stickerImageView;
    private FrameLayout stickerPreviewLayout;
    private TLRPC.TL_messages_stickerSet stickerSet;
    private ArrayList<TLRPC.StickerSetCovered> stickerSetCovereds;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    private TextView titleTextView;
    private HashMap<String, SendMessagesHelper.ImportingSticker> uploadImportStickers;
    private Pattern urlPattern;

    /* loaded from: classes5.dex */
    public interface StickersAlertCustomButtonDelegate {
        String getCustomButtonColorKey();

        String getCustomButtonRippleColorKey();

        String getCustomButtonText();

        String getCustomButtonTextColorKey();

        boolean onCustomButtonPressed();
    }

    /* loaded from: classes5.dex */
    public interface StickersAlertDelegate {
        boolean canSchedule();

        boolean isInScheduleMode();

        void onStickerSelected(TLRPC.Document document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, boolean z2, int i);
    }

    /* loaded from: classes5.dex */
    public interface StickersAlertInstallDelegate {
        void onStickerSetInstalled();

        void onStickerSetUninstalled();
    }

    /* loaded from: classes5.dex */
    public static class LinkMovementMethodMy extends LinkMovementMethod {
        private LinkMovementMethodMy() {
        }

        @Override // android.text.method.LinkMovementMethod, android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod, android.text.method.MovementMethod
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            try {
                boolean result = super.onTouchEvent(widget, buffer, event);
                if (event.getAction() == 1 || event.getAction() == 3) {
                    Selection.removeSelection(buffer);
                }
                return result;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    public StickersAlert(Context context, final Object parentObject, TLObject object, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.showTooltipWhenToggle = true;
        this.previewDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() { // from class: org.telegram.ui.Components.StickersAlert.1
            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ String getQuery(boolean z) {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$getQuery(this, z);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ void gifAddedOrDeleted() {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ boolean needMenu() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needMenu(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z, int i) {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void sendSticker(TLRPC.Document sticker, String query, Object parent, boolean notify, int scheduleDate) {
                if (StickersAlert.this.delegate != null) {
                    StickersAlert.this.delegate.onStickerSelected(sticker, query, parent, null, StickersAlert.this.clearsInputField, notify, scheduleDate);
                    StickersAlert.this.dismiss();
                }
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean canSchedule() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.canSchedule();
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean isInScheduleMode() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.isInScheduleMode();
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void openSet(TLRPC.InputStickerSet set, boolean clearsInputField) {
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean needRemove() {
                return StickersAlert.this.importingStickers != null;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void remove(SendMessagesHelper.ImportingSticker importingSticker) {
                StickersAlert.this.removeSticker(importingSticker);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean needSend() {
                return StickersAlert.this.delegate != null;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean needOpen() {
                return false;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public long getDialogId() {
                if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                    return ((ChatActivity) StickersAlert.this.parentFragment).getDialogId();
                }
                return 0L;
            }
        };
        fixNavigationBar();
        this.resourcesProvider = resourcesProvider;
        this.parentActivity = (Activity) context;
        final TLRPC.TL_messages_getAttachedStickers req = new TLRPC.TL_messages_getAttachedStickers();
        if (object instanceof TLRPC.Photo) {
            TLRPC.Photo photo = (TLRPC.Photo) object;
            TLRPC.TL_inputStickeredMediaPhoto inputStickeredMediaPhoto = new TLRPC.TL_inputStickeredMediaPhoto();
            inputStickeredMediaPhoto.id = new TLRPC.TL_inputPhoto();
            inputStickeredMediaPhoto.id.id = photo.id;
            inputStickeredMediaPhoto.id.access_hash = photo.access_hash;
            inputStickeredMediaPhoto.id.file_reference = photo.file_reference;
            if (inputStickeredMediaPhoto.id.file_reference == null) {
                inputStickeredMediaPhoto.id.file_reference = new byte[0];
            }
            req.media = inputStickeredMediaPhoto;
        } else if (object instanceof TLRPC.Document) {
            TLRPC.Document document = (TLRPC.Document) object;
            TLRPC.TL_inputStickeredMediaDocument inputStickeredMediaDocument = new TLRPC.TL_inputStickeredMediaDocument();
            inputStickeredMediaDocument.id = new TLRPC.TL_inputDocument();
            inputStickeredMediaDocument.id.id = document.id;
            inputStickeredMediaDocument.id.access_hash = document.access_hash;
            inputStickeredMediaDocument.id.file_reference = document.file_reference;
            if (inputStickeredMediaDocument.id.file_reference == null) {
                inputStickeredMediaDocument.id.file_reference = new byte[0];
            }
            req.media = inputStickeredMediaDocument;
        }
        final RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda24
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StickersAlert.this.m3096lambda$new$1$orgtelegramuiComponentsStickersAlert(req, tLObject, tL_error);
            }
        };
        this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda20
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StickersAlert.this.m3097lambda$new$2$orgtelegramuiComponentsStickersAlert(parentObject, req, requestDelegate, tLObject, tL_error);
            }
        });
        init(context);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3096lambda$new$1$orgtelegramuiComponentsStickersAlert(final TLRPC.TL_messages_getAttachedStickers req, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                StickersAlert.this.m3095lambda$new$0$orgtelegramuiComponentsStickersAlert(error, response, req);
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3095lambda$new$0$orgtelegramuiComponentsStickersAlert(TLRPC.TL_error error, TLObject response, TLRPC.TL_messages_getAttachedStickers req) {
        this.reqId = 0;
        if (error != null) {
            AlertsCreator.processError(this.currentAccount, error, this.parentFragment, req, new Object[0]);
            dismiss();
            return;
        }
        TLRPC.Vector vector = (TLRPC.Vector) response;
        if (vector.objects.isEmpty()) {
            dismiss();
        } else if (vector.objects.size() == 1) {
            TLRPC.StickerSetCovered set = (TLRPC.StickerSetCovered) vector.objects.get(0);
            TLRPC.TL_inputStickerSetID tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
            this.inputStickerSet = tL_inputStickerSetID;
            tL_inputStickerSetID.id = set.set.id;
            this.inputStickerSet.access_hash = set.set.access_hash;
            loadStickerSet();
        } else {
            this.stickerSetCovereds = new ArrayList<>();
            for (int a = 0; a < vector.objects.size(); a++) {
                this.stickerSetCovereds.add((TLRPC.StickerSetCovered) vector.objects.get(a));
            }
            this.gridView.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
            this.titleTextView.setVisibility(8);
            this.shadow[0].setVisibility(8);
            this.adapter.notifyDataSetChanged();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3097lambda$new$2$orgtelegramuiComponentsStickersAlert(Object parentObject, TLRPC.TL_messages_getAttachedStickers req, RequestDelegate requestDelegate, TLObject response, TLRPC.TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text) && parentObject != null) {
            FileRefController.getInstance(this.currentAccount).requestReference(parentObject, req, requestDelegate);
        } else {
            requestDelegate.run(response, error);
        }
    }

    public StickersAlert(Context context, String software, final ArrayList<Parcelable> uris, final ArrayList<String> emoji, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.showTooltipWhenToggle = true;
        this.previewDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() { // from class: org.telegram.ui.Components.StickersAlert.1
            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ String getQuery(boolean z) {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$getQuery(this, z);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ void gifAddedOrDeleted() {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ boolean needMenu() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needMenu(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z, int i) {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void sendSticker(TLRPC.Document sticker, String query, Object parent, boolean notify, int scheduleDate) {
                if (StickersAlert.this.delegate != null) {
                    StickersAlert.this.delegate.onStickerSelected(sticker, query, parent, null, StickersAlert.this.clearsInputField, notify, scheduleDate);
                    StickersAlert.this.dismiss();
                }
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean canSchedule() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.canSchedule();
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean isInScheduleMode() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.isInScheduleMode();
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void openSet(TLRPC.InputStickerSet set, boolean clearsInputField) {
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean needRemove() {
                return StickersAlert.this.importingStickers != null;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void remove(SendMessagesHelper.ImportingSticker importingSticker) {
                StickersAlert.this.removeSticker(importingSticker);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean needSend() {
                return StickersAlert.this.delegate != null;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean needOpen() {
                return false;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public long getDialogId() {
                if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                    return ((ChatActivity) StickersAlert.this.parentFragment).getDialogId();
                }
                return 0L;
            }
        };
        fixNavigationBar();
        this.parentActivity = (Activity) context;
        this.importingStickers = uris;
        this.importingSoftware = software;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                StickersAlert.this.m3099lambda$new$4$orgtelegramuiComponentsStickersAlert(uris, emoji);
            }
        });
        init(context);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3099lambda$new$4$orgtelegramuiComponentsStickersAlert(ArrayList uris, ArrayList emoji) {
        Uri uri;
        String ext;
        final ArrayList<SendMessagesHelper.ImportingSticker> stickers = new ArrayList<>();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Boolean isAnimated = null;
        int N = uris.size();
        for (int a = 0; a < N; a++) {
            Object obj = uris.get(a);
            if ((obj instanceof Uri) && (ext = MediaController.getStickerExt((uri = (Uri) obj))) != null) {
                boolean animated = "tgs".equals(ext);
                if (isAnimated == null) {
                    isAnimated = Boolean.valueOf(animated);
                } else if (isAnimated.booleanValue() != animated) {
                    continue;
                }
                if (isDismissed()) {
                    return;
                }
                SendMessagesHelper.ImportingSticker importingSticker = new SendMessagesHelper.ImportingSticker();
                importingSticker.animated = animated;
                importingSticker.path = MediaController.copyFileToCache(uri, ext, (animated ? 64 : 512) * 1024);
                if (importingSticker.path != null) {
                    if (!animated) {
                        BitmapFactory.decodeFile(importingSticker.path, opts);
                        if ((opts.outWidth == 512 && opts.outHeight > 0 && opts.outHeight <= 512) || (opts.outHeight == 512 && opts.outWidth > 0 && opts.outWidth <= 512)) {
                            importingSticker.mimeType = "image/" + ext;
                            importingSticker.validated = true;
                        }
                    } else {
                        importingSticker.mimeType = "application/x-tgsticker";
                    }
                    if (emoji != null && emoji.size() == N && (emoji.get(a) instanceof String)) {
                        importingSticker.emoji = (String) emoji.get(a);
                    } else {
                        importingSticker.emoji = "#️⃣";
                    }
                    stickers.add(importingSticker);
                    if (stickers.size() >= 200) {
                        break;
                    }
                } else {
                    continue;
                }
            }
        }
        final Boolean isAnimatedFinal = isAnimated;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                StickersAlert.this.m3098lambda$new$3$orgtelegramuiComponentsStickersAlert(stickers, isAnimatedFinal);
            }
        });
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3098lambda$new$3$orgtelegramuiComponentsStickersAlert(ArrayList stickers, Boolean isAnimatedFinal) {
        this.importingStickersPaths = stickers;
        if (stickers.isEmpty()) {
            dismiss();
            return;
        }
        this.adapter.notifyDataSetChanged();
        if (isAnimatedFinal.booleanValue()) {
            this.uploadImportStickers = new HashMap<>();
            int N = this.importingStickersPaths.size();
            for (int a = 0; a < N; a++) {
                SendMessagesHelper.ImportingSticker sticker = this.importingStickersPaths.get(a);
                this.uploadImportStickers.put(sticker.path, sticker);
                FileLoader.getInstance(this.currentAccount).uploadFile(sticker.path, false, true, ConnectionsManager.FileTypeFile);
            }
        }
        updateFields();
    }

    public StickersAlert(Context context, BaseFragment baseFragment, TLRPC.InputStickerSet set, TLRPC.TL_messages_stickerSet loadedSet, StickersAlertDelegate stickersAlertDelegate) {
        this(context, baseFragment, set, loadedSet, stickersAlertDelegate, null);
    }

    public StickersAlert(Context context, BaseFragment baseFragment, TLRPC.InputStickerSet set, TLRPC.TL_messages_stickerSet loadedSet, StickersAlertDelegate stickersAlertDelegate, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.shadowAnimation = new AnimatorSet[2];
        this.shadow = new View[2];
        this.showTooltipWhenToggle = true;
        this.previewDelegate = new ContentPreviewViewer.ContentPreviewViewerDelegate() { // from class: org.telegram.ui.Components.StickersAlert.1
            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ String getQuery(boolean z) {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$getQuery(this, z);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ void gifAddedOrDeleted() {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$gifAddedOrDeleted(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ boolean needMenu() {
                return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needMenu(this);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public /* synthetic */ void sendGif(Object obj, Object obj2, boolean z, int i) {
                ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendGif(this, obj, obj2, z, i);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void sendSticker(TLRPC.Document sticker, String query, Object parent, boolean notify, int scheduleDate) {
                if (StickersAlert.this.delegate != null) {
                    StickersAlert.this.delegate.onStickerSelected(sticker, query, parent, null, StickersAlert.this.clearsInputField, notify, scheduleDate);
                    StickersAlert.this.dismiss();
                }
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean canSchedule() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.canSchedule();
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean isInScheduleMode() {
                return StickersAlert.this.delegate != null && StickersAlert.this.delegate.isInScheduleMode();
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void openSet(TLRPC.InputStickerSet set2, boolean clearsInputField) {
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean needRemove() {
                return StickersAlert.this.importingStickers != null;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public void remove(SendMessagesHelper.ImportingSticker importingSticker) {
                StickersAlert.this.removeSticker(importingSticker);
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean needSend() {
                return StickersAlert.this.delegate != null;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public boolean needOpen() {
                return false;
            }

            @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
            public long getDialogId() {
                if (StickersAlert.this.parentFragment instanceof ChatActivity) {
                    return ((ChatActivity) StickersAlert.this.parentFragment).getDialogId();
                }
                return 0L;
            }
        };
        fixNavigationBar();
        this.delegate = stickersAlertDelegate;
        this.inputStickerSet = set;
        this.stickerSet = loadedSet;
        this.parentFragment = baseFragment;
        loadStickerSet();
        init(context);
    }

    public void setClearsInputField(boolean value) {
        this.clearsInputField = value;
    }

    public boolean isClearsInputField() {
        return this.clearsInputField;
    }

    private void loadStickerSet() {
        if (this.inputStickerSet != null) {
            final MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
            if (this.stickerSet == null && this.inputStickerSet.short_name != null) {
                this.stickerSet = mediaDataController.getStickerSetByName(this.inputStickerSet.short_name);
            }
            if (this.stickerSet == null) {
                this.stickerSet = mediaDataController.getStickerSetById(this.inputStickerSet.id);
            }
            if (this.stickerSet == null) {
                TLRPC.TL_messages_getStickerSet req = new TLRPC.TL_messages_getStickerSet();
                req.stickerset = this.inputStickerSet;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda23
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        StickersAlert.this.m3094lambda$loadStickerSet$6$orgtelegramuiComponentsStickersAlert(mediaDataController, tLObject, tL_error);
                    }
                });
            } else {
                if (this.adapter != null) {
                    updateSendButton();
                    updateFields();
                    this.adapter.notifyDataSetChanged();
                }
                mediaDataController.preloadStickerSetThumb(this.stickerSet);
                checkPremiumStickers();
            }
        }
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickerSet;
        if (tL_messages_stickerSet != null) {
            this.showEmoji = !tL_messages_stickerSet.set.masks;
        }
        checkPremiumStickers();
    }

    /* renamed from: lambda$loadStickerSet$6$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3094lambda$loadStickerSet$6$orgtelegramuiComponentsStickersAlert(final MediaDataController mediaDataController, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                StickersAlert.this.m3093lambda$loadStickerSet$5$orgtelegramuiComponentsStickersAlert(error, response, mediaDataController);
            }
        });
    }

    /* renamed from: lambda$loadStickerSet$5$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3093lambda$loadStickerSet$5$orgtelegramuiComponentsStickersAlert(TLRPC.TL_error error, TLObject response, MediaDataController mediaDataController) {
        this.reqId = 0;
        if (error != null) {
            Toast.makeText(getContext(), LocaleController.getString("AddStickersNotFound", R.string.AddStickersNotFound), 0).show();
            dismiss();
            return;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            Transition addTarget = new AnonymousClass2();
            addTarget.addTarget(this.containerView);
            TransitionManager.beginDelayedTransition(this.container, addTarget);
        }
        this.optionsButton.setVisibility(0);
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) response;
        this.stickerSet = tL_messages_stickerSet;
        this.showEmoji = !tL_messages_stickerSet.set.masks;
        checkPremiumStickers();
        mediaDataController.preloadStickerSetThumb(this.stickerSet);
        updateSendButton();
        updateFields();
        this.adapter.notifyDataSetChanged();
    }

    /* renamed from: org.telegram.ui.Components.StickersAlert$2 */
    /* loaded from: classes5.dex */
    public class AnonymousClass2 extends Transition {
        AnonymousClass2() {
            StickersAlert.this = this$0;
        }

        @Override // android.transition.Transition
        public void captureStartValues(TransitionValues transitionValues) {
            transitionValues.values.put(TtmlNode.START, true);
            transitionValues.values.put("offset", Integer.valueOf(StickersAlert.this.containerView.getTop() + StickersAlert.this.scrollOffsetY));
        }

        @Override // android.transition.Transition
        public void captureEndValues(TransitionValues transitionValues) {
            transitionValues.values.put(TtmlNode.START, false);
            transitionValues.values.put("offset", Integer.valueOf(StickersAlert.this.containerView.getTop() + StickersAlert.this.scrollOffsetY));
        }

        @Override // android.transition.Transition
        public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
            final int scrollOffsetY = StickersAlert.this.scrollOffsetY;
            final int startValue = ((Integer) startValues.values.get("offset")).intValue() - ((Integer) endValues.values.get("offset")).intValue();
            ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
            animator.setDuration(250L);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.StickersAlert$2$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    StickersAlert.AnonymousClass2.this.m3112xe8fa752b(startValue, scrollOffsetY, valueAnimator);
                }
            });
            return animator;
        }

        /* renamed from: lambda$createAnimator$0$org-telegram-ui-Components-StickersAlert$2 */
        public /* synthetic */ void m3112xe8fa752b(int startValue, int scrollOffsetY, ValueAnimator a) {
            float fraction = a.getAnimatedFraction();
            StickersAlert.this.gridView.setAlpha(fraction);
            StickersAlert.this.titleTextView.setAlpha(fraction);
            if (startValue != 0) {
                int value = (int) (startValue * (1.0f - fraction));
                StickersAlert.this.setScrollOffsetY(scrollOffsetY + value);
                StickersAlert.this.gridView.setTranslationY(value);
            }
        }
    }

    private void checkPremiumStickers() {
        if (this.stickerSet != null) {
            TLRPC.TL_messages_stickerSet filterPremiumStickers = MessagesController.getInstance(this.currentAccount).filterPremiumStickers(this.stickerSet);
            this.stickerSet = filterPremiumStickers;
            if (filterPremiumStickers == null) {
                dismiss();
            }
        }
    }

    private void init(Context context) {
        this.containerView = new FrameLayout(context) { // from class: org.telegram.ui.Components.StickersAlert.3
            private boolean fullHeight;
            private int lastNotifyWidth;
            private RectF rect = new RectF();

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                if (ev.getAction() == 0 && StickersAlert.this.scrollOffsetY != 0 && ev.getY() < StickersAlert.this.scrollOffsetY) {
                    StickersAlert.this.dismiss();
                    return true;
                }
                return super.onInterceptTouchEvent(ev);
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent e) {
                return !StickersAlert.this.isDismissed() && super.onTouchEvent(e);
            }

            @Override // android.widget.FrameLayout, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int contentSize;
                int height = View.MeasureSpec.getSize(heightMeasureSpec);
                boolean z = true;
                if (Build.VERSION.SDK_INT >= 21) {
                    StickersAlert.this.ignoreLayout = true;
                    setPadding(StickersAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, StickersAlert.this.backgroundPaddingLeft, 0);
                    StickersAlert.this.ignoreLayout = false;
                }
                StickersAlert.this.itemSize = (View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(36.0f)) / 5;
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) StickersAlert.this.gridView.getLayoutParams();
                if (StickersAlert.this.importingStickers != null) {
                    contentSize = AndroidUtilities.dp(48.0f) + params.bottomMargin + (Math.max(3, (int) Math.ceil(StickersAlert.this.importingStickers.size() / 5.0f)) * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop + AndroidUtilities.statusBarHeight;
                } else if (StickersAlert.this.stickerSetCovereds != null) {
                    contentSize = AndroidUtilities.dp(8.0f) + params.bottomMargin + (AndroidUtilities.dp(60.0f) * StickersAlert.this.stickerSetCovereds.size()) + (StickersAlert.this.adapter.stickersRowCount * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop + AndroidUtilities.dp(24.0f);
                } else {
                    contentSize = AndroidUtilities.dp(48.0f) + params.bottomMargin + (Math.max(3, StickersAlert.this.stickerSet != null ? (int) Math.ceil(StickersAlert.this.stickerSet.documents.size() / 5.0f) : 0) * AndroidUtilities.dp(82.0f)) + StickersAlert.this.backgroundPaddingTop + AndroidUtilities.statusBarHeight;
                }
                double d = height / 5;
                Double.isNaN(d);
                int padding = ((double) contentSize) < d * 3.2d ? 0 : (height / 5) * 2;
                if (padding != 0 && contentSize < height) {
                    padding -= height - contentSize;
                }
                if (padding == 0) {
                    padding = StickersAlert.this.backgroundPaddingTop;
                }
                if (StickersAlert.this.stickerSetCovereds != null) {
                    padding += AndroidUtilities.dp(8.0f);
                }
                if (StickersAlert.this.gridView.getPaddingTop() != padding) {
                    StickersAlert.this.ignoreLayout = true;
                    StickersAlert.this.gridView.setPadding(AndroidUtilities.dp(10.0f), padding, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(8.0f));
                    StickersAlert.this.emptyView.setPadding(0, padding, 0, 0);
                    StickersAlert.this.ignoreLayout = false;
                }
                if (contentSize < height) {
                    z = false;
                }
                this.fullHeight = z;
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.min(contentSize, height), C.BUFFER_FLAG_ENCRYPTED));
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                if (this.lastNotifyWidth != right - left) {
                    this.lastNotifyWidth = right - left;
                    if (StickersAlert.this.adapter != null && StickersAlert.this.stickerSetCovereds != null) {
                        StickersAlert.this.adapter.notifyDataSetChanged();
                    }
                }
                super.onLayout(changed, left, top, right, bottom);
                StickersAlert.this.updateLayout();
            }

            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (StickersAlert.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                float radProgress;
                int statusBarHeight;
                int height;
                int top;
                int y;
                int y2 = (StickersAlert.this.scrollOffsetY - StickersAlert.this.backgroundPaddingTop) + AndroidUtilities.dp(6.0f);
                int top2 = (StickersAlert.this.scrollOffsetY - StickersAlert.this.backgroundPaddingTop) - AndroidUtilities.dp(13.0f);
                int height2 = getMeasuredHeight() + AndroidUtilities.dp(15.0f) + StickersAlert.this.backgroundPaddingTop;
                float radProgress2 = 1.0f;
                if (Build.VERSION.SDK_INT < 21) {
                    y = y2;
                    top = top2;
                    height = height2;
                    statusBarHeight = 0;
                    radProgress = 1.0f;
                } else {
                    int top3 = top2 + AndroidUtilities.statusBarHeight;
                    int y3 = y2 + AndroidUtilities.statusBarHeight;
                    int height3 = height2 - AndroidUtilities.statusBarHeight;
                    if (this.fullHeight) {
                        if (StickersAlert.this.backgroundPaddingTop + top3 < AndroidUtilities.statusBarHeight * 2) {
                            int diff = Math.min(AndroidUtilities.statusBarHeight, ((AndroidUtilities.statusBarHeight * 2) - top3) - StickersAlert.this.backgroundPaddingTop);
                            top3 -= diff;
                            height3 += diff;
                            radProgress2 = 1.0f - Math.min(1.0f, (diff * 2) / AndroidUtilities.statusBarHeight);
                        }
                        if (StickersAlert.this.backgroundPaddingTop + top3 < AndroidUtilities.statusBarHeight) {
                            y = y3;
                            top = top3;
                            height = height3;
                            statusBarHeight = Math.min(AndroidUtilities.statusBarHeight, (AndroidUtilities.statusBarHeight - top3) - StickersAlert.this.backgroundPaddingTop);
                            radProgress = radProgress2;
                        } else {
                            y = y3;
                            top = top3;
                            height = height3;
                            statusBarHeight = 0;
                            radProgress = radProgress2;
                        }
                    } else {
                        y = y3;
                        top = top3;
                        height = height3;
                        statusBarHeight = 0;
                        radProgress = 1.0f;
                    }
                }
                StickersAlert.this.shadowDrawable.setBounds(0, top, getMeasuredWidth(), height);
                StickersAlert.this.shadowDrawable.draw(canvas);
                if (radProgress != 1.0f) {
                    Theme.dialogs_onlineCirclePaint.setColor(StickersAlert.this.getThemedColor(Theme.key_dialogBackground));
                    this.rect.set(StickersAlert.this.backgroundPaddingLeft, StickersAlert.this.backgroundPaddingTop + top, getMeasuredWidth() - StickersAlert.this.backgroundPaddingLeft, StickersAlert.this.backgroundPaddingTop + top + AndroidUtilities.dp(24.0f));
                    canvas.drawRoundRect(this.rect, AndroidUtilities.dp(12.0f) * radProgress, AndroidUtilities.dp(12.0f) * radProgress, Theme.dialogs_onlineCirclePaint);
                }
                int w = AndroidUtilities.dp(36.0f);
                this.rect.set((getMeasuredWidth() - w) / 2, y, (getMeasuredWidth() + w) / 2, AndroidUtilities.dp(4.0f) + y);
                Theme.dialogs_onlineCirclePaint.setColor(StickersAlert.this.getThemedColor(Theme.key_sheet_scrollUp));
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                if (statusBarHeight > 0) {
                    int color1 = StickersAlert.this.getThemedColor(Theme.key_dialogBackground);
                    int finalColor = Color.argb(255, (int) (Color.red(color1) * 0.8f), (int) (Color.green(color1) * 0.8f), (int) (Color.blue(color1) * 0.8f));
                    Theme.dialogs_onlineCirclePaint.setColor(finalColor);
                    canvas.drawRect(StickersAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight - statusBarHeight, getMeasuredWidth() - StickersAlert.this.backgroundPaddingLeft, AndroidUtilities.statusBarHeight, Theme.dialogs_onlineCirclePaint);
                }
            }
        };
        this.containerView.setWillNotDraw(false);
        this.containerView.setPadding(this.backgroundPaddingLeft, 0, this.backgroundPaddingLeft, 0);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        frameLayoutParams.topMargin = AndroidUtilities.dp(48.0f);
        this.shadow[0] = new View(context);
        this.shadow[0].setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setVisibility(4);
        this.shadow[0].setTag(1);
        this.containerView.addView(this.shadow[0], frameLayoutParams);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.StickersAlert.4
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent event) {
                boolean result = ContentPreviewViewer.getInstance().onInterceptTouchEvent(event, StickersAlert.this.gridView, 0, StickersAlert.this.previewDelegate, this.resourcesProvider);
                return super.onInterceptTouchEvent(event) || result;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (StickersAlert.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.gridView = recyclerListView;
        recyclerListView.setTag(14);
        RecyclerListView recyclerListView2 = this.gridView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 5) { // from class: org.telegram.ui.Components.StickersAlert.5
            @Override // androidx.recyclerview.widget.LinearLayoutManager
            public boolean isLayoutRTL() {
                return StickersAlert.this.stickerSetCovereds != null && LocaleController.isRTL;
            }
        };
        this.layoutManager = gridLayoutManager;
        recyclerListView2.setLayoutManager(gridLayoutManager);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: org.telegram.ui.Components.StickersAlert.6
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int position) {
                if ((StickersAlert.this.stickerSetCovereds == null || !(StickersAlert.this.adapter.cache.get(position) instanceof Integer)) && position != StickersAlert.this.adapter.totalItems) {
                    return 1;
                }
                return StickersAlert.this.adapter.stickersPerRow;
            }
        });
        RecyclerListView recyclerListView3 = this.gridView;
        GridAdapter gridAdapter = new GridAdapter(context);
        this.adapter = gridAdapter;
        recyclerListView3.setAdapter(gridAdapter);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.StickersAlert.7
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = 0;
                outRect.right = 0;
                outRect.bottom = 0;
                outRect.top = 0;
            }
        });
        this.gridView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        this.gridView.setClipToPadding(false);
        this.gridView.setEnabled(true);
        this.gridView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        this.gridView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda4
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return StickersAlert.this.m3091lambda$init$7$orgtelegramuiComponentsStickersAlert(view, motionEvent);
            }
        });
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.StickersAlert.8
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                StickersAlert.this.updateLayout();
            }
        });
        RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda28
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                StickersAlert.this.m3092lambda$init$8$orgtelegramuiComponentsStickersAlert(view, i);
            }
        };
        this.stickersOnItemClickListener = onItemClickListener;
        this.gridView.setOnItemClickListener(onItemClickListener);
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 48.0f, 0.0f, 48.0f));
        this.emptyView = new FrameLayout(context) { // from class: org.telegram.ui.Components.StickersAlert.9
            @Override // android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (StickersAlert.this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.containerView.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.gridView.setEmptyView(this.emptyView);
        this.emptyView.setOnTouchListener(StickersAlert$$ExternalSyntheticLambda5.INSTANCE);
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        this.titleTextView.setTextSize(1, 20.0f);
        this.titleTextView.setLinkTextColor(getThemedColor(Theme.key_dialogTextLink));
        this.titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.titleTextView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.titleTextView.setGravity(16);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.containerView.addView(this.titleTextView, LayoutHelper.createFrame(-1, 50.0f, 51, 0.0f, 0.0f, 40.0f, 0.0f));
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, (ActionBarMenu) null, 0, getThemedColor(Theme.key_sheet_other), this.resourcesProvider);
        this.optionsButton = actionBarMenuItem;
        actionBarMenuItem.setLongClickEnabled(false);
        this.optionsButton.setSubMenuOpenSide(2);
        this.optionsButton.setIcon(R.drawable.ic_ab_other);
        this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_player_actionBarSelector), 1));
        this.containerView.addView(this.optionsButton, LayoutHelper.createFrame(40, 40.0f, 53, 0.0f, 5.0f, 5.0f, 0.0f));
        this.optionsButton.addSubItem(1, R.drawable.msg_share, LocaleController.getString("StickersShare", R.string.StickersShare));
        this.optionsButton.addSubItem(2, R.drawable.msg_link, LocaleController.getString("CopyLink", R.string.CopyLink));
        this.optionsButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda29
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StickersAlert.this.m3088lambda$init$10$orgtelegramuiComponentsStickersAlert(view);
            }
        });
        this.optionsButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda26
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
            public final void onItemClick(int i) {
                StickersAlert.this.onSubItemClick(i);
            }
        });
        this.optionsButton.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.optionsButton.setVisibility(this.inputStickerSet != null ? 0 : 8);
        RadialProgressView progressView = new RadialProgressView(context);
        this.emptyView.addView(progressView, LayoutHelper.createFrame(-2, -2, 17));
        FrameLayout.LayoutParams frameLayoutParams2 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        frameLayoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
        this.shadow[1] = new View(context);
        this.shadow[1].setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.containerView.addView(this.shadow[1], frameLayoutParams2);
        TextView textView2 = new TextView(context);
        this.pickerBottomLayout = textView2;
        textView2.setBackground(Theme.createSelectorWithBackgroundDrawable(getThemedColor(Theme.key_dialogBackground), getThemedColor(Theme.key_listSelector)));
        TextView textView3 = this.pickerBottomLayout;
        this.buttonTextColorKey = Theme.key_dialogTextBlue2;
        textView3.setTextColor(getThemedColor(Theme.key_dialogTextBlue2));
        this.pickerBottomLayout.setTextSize(1, 14.0f);
        this.pickerBottomLayout.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.pickerBottomLayout.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.pickerBottomLayout.setGravity(17);
        FrameLayout frameLayout = new FrameLayout(context);
        this.pickerBottomFrameLayout = frameLayout;
        frameLayout.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48.0f));
        this.containerView.addView(this.pickerBottomFrameLayout, LayoutHelper.createFrame(-1, -2, 83));
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.stickerPreviewLayout = frameLayout2;
        frameLayout2.setVisibility(8);
        this.stickerPreviewLayout.setSoundEffectsEnabled(false);
        this.containerView.addView(this.stickerPreviewLayout, LayoutHelper.createFrame(-1, -1.0f));
        this.stickerPreviewLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda30
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StickersAlert.this.m3089lambda$init$11$orgtelegramuiComponentsStickersAlert(view);
            }
        });
        BackupImageView backupImageView = new BackupImageView(context);
        this.stickerImageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.stickerImageView.setLayerNum(7);
        this.stickerPreviewLayout.addView(this.stickerImageView);
        TextView textView4 = new TextView(context);
        this.stickerEmojiTextView = textView4;
        textView4.setTextSize(1, 30.0f);
        this.stickerEmojiTextView.setGravity(85);
        this.stickerPreviewLayout.addView(this.stickerEmojiTextView);
        TextView textView5 = new TextView(context);
        this.previewSendButton = textView5;
        textView5.setTextSize(1, 14.0f);
        this.previewSendButton.setTextColor(getThemedColor(Theme.key_dialogTextBlue2));
        this.previewSendButton.setBackground(Theme.createSelectorWithBackgroundDrawable(getThemedColor(Theme.key_dialogBackground), getThemedColor(Theme.key_listSelector)));
        this.previewSendButton.setGravity(17);
        this.previewSendButton.setPadding(AndroidUtilities.dp(29.0f), 0, AndroidUtilities.dp(29.0f), 0);
        this.previewSendButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.stickerPreviewLayout.addView(this.previewSendButton, LayoutHelper.createFrame(-1, 48, 83));
        this.previewSendButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda31
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StickersAlert.this.m3090lambda$init$12$orgtelegramuiComponentsStickersAlert(view);
            }
        });
        FrameLayout.LayoutParams frameLayoutParams3 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        frameLayoutParams3.bottomMargin = AndroidUtilities.dp(48.0f);
        View view = new View(context);
        this.previewSendButtonShadow = view;
        view.setBackgroundColor(getThemedColor(Theme.key_dialogShadowLine));
        this.stickerPreviewLayout.addView(this.previewSendButtonShadow, frameLayoutParams3);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        if (this.importingStickers != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
        }
        updateFields();
        updateSendButton();
        updateColors();
        this.adapter.notifyDataSetChanged();
    }

    /* renamed from: lambda$init$7$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ boolean m3091lambda$init$7$orgtelegramuiComponentsStickersAlert(View v, MotionEvent event) {
        return ContentPreviewViewer.getInstance().onTouch(event, this.gridView, 0, this.stickersOnItemClickListener, this.previewDelegate, this.resourcesProvider);
    }

    /* renamed from: lambda$init$8$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3092lambda$init$8$orgtelegramuiComponentsStickersAlert(View view, int position) {
        if (this.stickerSetCovereds == null) {
            ArrayList<SendMessagesHelper.ImportingSticker> arrayList = this.importingStickersPaths;
            if (arrayList != null) {
                if (position < 0 || position >= arrayList.size()) {
                    return;
                }
                SendMessagesHelper.ImportingSticker importingSticker = this.importingStickersPaths.get(position);
                this.selectedStickerPath = importingSticker;
                if (importingSticker.validated) {
                    this.stickerEmojiTextView.setText(Emoji.replaceEmoji(this.selectedStickerPath.emoji, this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                    this.stickerImageView.setImage(ImageLocation.getForPath(this.selectedStickerPath.path), null, null, null, null, null, this.selectedStickerPath.animated ? "tgs" : null, 0, null);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.stickerPreviewLayout.getLayoutParams();
                    layoutParams.topMargin = this.scrollOffsetY;
                    this.stickerPreviewLayout.setLayoutParams(layoutParams);
                    this.stickerPreviewLayout.setVisibility(0);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(ObjectAnimator.ofFloat(this.stickerPreviewLayout, View.ALPHA, 0.0f, 1.0f));
                    animatorSet.setDuration(200L);
                    animatorSet.start();
                    return;
                }
                return;
            }
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickerSet;
            if (tL_messages_stickerSet == null || position < 0) {
                return;
            }
            if (position < tL_messages_stickerSet.documents.size()) {
                this.selectedSticker = this.stickerSet.documents.get(position);
                boolean set = false;
                int a = 0;
                while (true) {
                    if (a >= this.selectedSticker.attributes.size()) {
                        break;
                    }
                    TLRPC.DocumentAttribute attribute = this.selectedSticker.attributes.get(a);
                    if (!(attribute instanceof TLRPC.TL_documentAttributeSticker)) {
                        a++;
                    } else if (attribute.alt != null && attribute.alt.length() > 0) {
                        this.stickerEmojiTextView.setText(Emoji.replaceEmoji(attribute.alt, this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                        set = true;
                    }
                }
                if (!set) {
                    this.stickerEmojiTextView.setText(Emoji.replaceEmoji(MediaDataController.getInstance(this.currentAccount).getEmojiForSticker(this.selectedSticker.id), this.stickerEmojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(30.0f), false));
                }
                if (!ContentPreviewViewer.getInstance().showMenuFor(view)) {
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(this.selectedSticker.thumbs, 90);
                    this.stickerImageView.getImageReceiver().setImage(ImageLocation.getForDocument(this.selectedSticker), (String) null, ImageLocation.getForDocument(thumb, this.selectedSticker), (String) null, "webp", this.stickerSet, 1);
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.stickerPreviewLayout.getLayoutParams();
                    layoutParams2.topMargin = this.scrollOffsetY;
                    this.stickerPreviewLayout.setLayoutParams(layoutParams2);
                    this.stickerPreviewLayout.setVisibility(0);
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    animatorSet2.playTogether(ObjectAnimator.ofFloat(this.stickerPreviewLayout, View.ALPHA, 0.0f, 1.0f));
                    animatorSet2.setDuration(200L);
                    animatorSet2.start();
                    return;
                }
                return;
            }
            return;
        }
        TLRPC.StickerSetCovered pack = (TLRPC.StickerSetCovered) this.adapter.positionsToSets.get(position);
        if (pack != null) {
            dismiss();
            TLRPC.TL_inputStickerSetID inputStickerSetID = new TLRPC.TL_inputStickerSetID();
            inputStickerSetID.access_hash = pack.set.access_hash;
            inputStickerSetID.id = pack.set.id;
            StickersAlert alert = new StickersAlert(this.parentActivity, this.parentFragment, inputStickerSetID, null, null, this.resourcesProvider);
            alert.show();
        }
    }

    public static /* synthetic */ boolean lambda$init$9(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$init$10$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3088lambda$init$10$orgtelegramuiComponentsStickersAlert(View v) {
        this.optionsButton.toggleSubMenu();
    }

    /* renamed from: lambda$init$11$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3089lambda$init$11$orgtelegramuiComponentsStickersAlert(View v) {
        hidePreview();
    }

    /* renamed from: lambda$init$12$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3090lambda$init$12$orgtelegramuiComponentsStickersAlert(View v) {
        if (this.importingStickersPaths != null) {
            removeSticker(this.selectedStickerPath);
            hidePreview();
            this.selectedStickerPath = null;
            return;
        }
        this.delegate.onStickerSelected(this.selectedSticker, null, this.stickerSet, null, this.clearsInputField, true, 0);
        dismiss();
    }

    private void updateSendButton() {
        TLRPC.TL_messages_stickerSet tL_messages_stickerSet;
        int size = (int) ((Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 2) / AndroidUtilities.density);
        if (this.importingStickers != null) {
            this.previewSendButton.setText(LocaleController.getString("ImportStickersRemove", R.string.ImportStickersRemove).toUpperCase());
            this.previewSendButton.setTextColor(getThemedColor(Theme.key_dialogTextRed));
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(size, size, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(size, size, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.previewSendButton.setVisibility(0);
            this.previewSendButtonShadow.setVisibility(0);
        } else if (this.delegate != null && ((tL_messages_stickerSet = this.stickerSet) == null || !tL_messages_stickerSet.set.masks)) {
            this.previewSendButton.setText(LocaleController.getString("SendSticker", R.string.SendSticker).toUpperCase());
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(size, size, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(size, size, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.previewSendButton.setVisibility(0);
            this.previewSendButtonShadow.setVisibility(0);
        } else {
            this.previewSendButton.setText(LocaleController.getString("Close", R.string.Close).toUpperCase());
            this.stickerImageView.setLayoutParams(LayoutHelper.createFrame(size, size, 17));
            this.stickerEmojiTextView.setLayoutParams(LayoutHelper.createFrame(size, size, 17));
            this.previewSendButton.setVisibility(8);
            this.previewSendButtonShadow.setVisibility(8);
        }
    }

    public void removeSticker(SendMessagesHelper.ImportingSticker sticker) {
        int idx = this.importingStickersPaths.indexOf(sticker);
        if (idx >= 0) {
            this.importingStickersPaths.remove(idx);
            this.adapter.notifyItemRemoved(idx);
            if (this.importingStickersPaths.isEmpty()) {
                dismiss();
            } else {
                updateFields();
            }
        }
    }

    public void setInstallDelegate(StickersAlertInstallDelegate stickersAlertInstallDelegate) {
        this.installDelegate = stickersAlertInstallDelegate;
    }

    public void setCustomButtonDelegate(StickersAlertCustomButtonDelegate customButtonDelegate) {
        this.customButtonDelegate = customButtonDelegate;
        updateFields();
    }

    public void onSubItemClick(int id) {
        BaseFragment baseFragment;
        if (this.stickerSet == null) {
            return;
        }
        String stickersUrl = "https://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/addstickers/" + this.stickerSet.set.short_name;
        if (id == 1) {
            Context context = this.parentActivity;
            if (context == null && (baseFragment = this.parentFragment) != null) {
                context = baseFragment.getParentActivity();
            }
            if (context == null) {
                context = getContext();
            }
            ShareAlert alert = new ShareAlert(context, null, stickersUrl, false, stickersUrl, false, this.resourcesProvider);
            BaseFragment baseFragment2 = this.parentFragment;
            if (baseFragment2 != null) {
                baseFragment2.showDialog(alert);
            } else {
                alert.show();
            }
        } else if (id == 2) {
            try {
                AndroidUtilities.addToClipboard(stickersUrl);
                BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).createCopyLinkBulletin().show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void updateFields() {
        TextView textView = this.titleTextView;
        if (textView == null) {
            return;
        }
        if (this.stickerSet == null) {
            ArrayList<Parcelable> arrayList = this.importingStickers;
            if (arrayList != null) {
                ArrayList<SendMessagesHelper.ImportingSticker> arrayList2 = this.importingStickersPaths;
                textView.setText(LocaleController.formatPluralString("Stickers", arrayList2 != null ? arrayList2.size() : arrayList.size(), new Object[0]));
                HashMap<String, SendMessagesHelper.ImportingSticker> hashMap = this.uploadImportStickers;
                if (hashMap == null || hashMap.isEmpty()) {
                    View.OnClickListener onClickListener = new View.OnClickListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            StickersAlert.this.m3110lambda$updateFields$19$orgtelegramuiComponentsStickersAlert(view);
                        }
                    };
                    Object[] objArr = new Object[1];
                    ArrayList arrayList3 = this.importingStickersPaths;
                    if (arrayList3 == null) {
                        arrayList3 = this.importingStickers;
                    }
                    objArr[0] = LocaleController.formatPluralString("Stickers", arrayList3.size(), new Object[0]);
                    setButton(onClickListener, LocaleController.formatString("ImportStickers", R.string.ImportStickers, objArr).toUpperCase(), Theme.key_dialogTextBlue2);
                    this.pickerBottomLayout.setEnabled(true);
                    return;
                }
                setButton(null, LocaleController.getString("ImportStickersProcessing", R.string.ImportStickersProcessing).toUpperCase(), Theme.key_dialogTextGray2);
                this.pickerBottomLayout.setEnabled(false);
                return;
            }
            String text = LocaleController.getString("Close", R.string.Close).toUpperCase();
            setButton(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    StickersAlert.this.m3111lambda$updateFields$20$orgtelegramuiComponentsStickersAlert(view);
                }
            }, text, Theme.key_dialogTextBlue2);
            return;
        }
        SpannableStringBuilder stringBuilder = null;
        try {
            if (this.urlPattern == null) {
                this.urlPattern = Pattern.compile("@[a-zA-Z\\d_]{1,32}");
            }
            Matcher matcher = this.urlPattern.matcher(this.stickerSet.set.title);
            while (matcher.find()) {
                if (stringBuilder == null) {
                    stringBuilder = new SpannableStringBuilder(this.stickerSet.set.title);
                    this.titleTextView.setMovementMethod(new LinkMovementMethodMy());
                }
                int start = matcher.start();
                int end = matcher.end();
                if (this.stickerSet.set.title.charAt(start) != '@') {
                    start++;
                }
                URLSpanNoUnderline url = new URLSpanNoUnderline(this.stickerSet.set.title.subSequence(start + 1, end).toString()) { // from class: org.telegram.ui.Components.StickersAlert.10
                    @Override // org.telegram.ui.Components.URLSpanNoUnderline, android.text.style.URLSpan, android.text.style.ClickableSpan
                    public void onClick(View widget) {
                        MessagesController.getInstance(StickersAlert.this.currentAccount).openByUserName(getURL(), StickersAlert.this.parentFragment, 1);
                        StickersAlert.this.dismiss();
                    }
                };
                stringBuilder.setSpan(url, start, end, 0);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        this.titleTextView.setText(stringBuilder != null ? stringBuilder : this.stickerSet.set.title);
        if (this.customButtonDelegate != null) {
            setButton(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda32
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    StickersAlert.this.m3104lambda$updateFields$13$orgtelegramuiComponentsStickersAlert(view);
                }
            }, this.customButtonDelegate.getCustomButtonText(), this.customButtonDelegate.getCustomButtonTextColorKey(), this.customButtonDelegate.getCustomButtonColorKey(), this.customButtonDelegate.getCustomButtonRippleColorKey());
        } else if (this.stickerSet.set == null || !MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(this.stickerSet.set.id)) {
            String text2 = (this.stickerSet.set == null || !this.stickerSet.set.masks) ? LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, LocaleController.formatPluralString("Stickers", this.stickerSet.documents.size(), new Object[0])).toUpperCase() : LocaleController.formatString("AddStickersCount", R.string.AddStickersCount, LocaleController.formatPluralString("MasksCount", this.stickerSet.documents.size(), new Object[0])).toUpperCase();
            setButton(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda33
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    StickersAlert.this.m3107lambda$updateFields$16$orgtelegramuiComponentsStickersAlert(view);
                }
            }, text2, Theme.key_featuredStickers_buttonText, Theme.key_featuredStickers_addButton, Theme.key_featuredStickers_addButtonPressed);
        } else {
            String text3 = this.stickerSet.set.masks ? LocaleController.formatString("RemoveStickersCount", R.string.RemoveStickersCount, LocaleController.formatPluralString("MasksCount", this.stickerSet.documents.size(), new Object[0])).toUpperCase() : LocaleController.formatString("RemoveStickersCount", R.string.RemoveStickersCount, LocaleController.formatPluralString("Stickers", this.stickerSet.documents.size(), new Object[0])).toUpperCase();
            if (this.stickerSet.set.official) {
                setButton(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda34
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        StickersAlert.this.m3108lambda$updateFields$17$orgtelegramuiComponentsStickersAlert(view);
                    }
                }, text3, Theme.key_dialogTextRed);
            } else {
                setButton(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda35
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        StickersAlert.this.m3109lambda$updateFields$18$orgtelegramuiComponentsStickersAlert(view);
                    }
                }, text3, Theme.key_dialogTextRed);
            }
        }
        this.adapter.notifyDataSetChanged();
    }

    /* renamed from: lambda$updateFields$13$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3104lambda$updateFields$13$orgtelegramuiComponentsStickersAlert(View v) {
        if (this.customButtonDelegate.onCustomButtonPressed()) {
            dismiss();
        }
    }

    /* renamed from: lambda$updateFields$16$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3107lambda$updateFields$16$orgtelegramuiComponentsStickersAlert(View v) {
        dismiss();
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetInstalled();
        }
        if (this.inputStickerSet == null || MediaDataController.getInstance(this.currentAccount).cancelRemovingStickerSet(this.inputStickerSet.id)) {
            return;
        }
        TLRPC.TL_messages_installStickerSet req = new TLRPC.TL_messages_installStickerSet();
        req.stickerset = this.inputStickerSet;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda19
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StickersAlert.this.m3106lambda$updateFields$15$orgtelegramuiComponentsStickersAlert(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$updateFields$15$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3106lambda$updateFields$15$orgtelegramuiComponentsStickersAlert(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                StickersAlert.this.m3105lambda$updateFields$14$orgtelegramuiComponentsStickersAlert(error, response);
            }
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v2, types: [int, boolean] */
    /* renamed from: lambda$updateFields$14$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3105lambda$updateFields$14$orgtelegramuiComponentsStickersAlert(TLRPC.TL_error error, TLObject response) {
        ?? r0 = this.stickerSet.set.masks;
        try {
            if (error == null) {
                if (this.showTooltipWhenToggle) {
                    Bulletin.make(this.parentFragment, new StickerSetBulletinLayout(this.pickerBottomFrameLayout.getContext(), this.stickerSet, 2, null, this.resourcesProvider), 1500).show();
                }
                if (response instanceof TLRPC.TL_messages_stickerSetInstallResultArchive) {
                    MediaDataController.getInstance(this.currentAccount).processStickerSetInstallResultArchive(this.parentFragment, true, r0, (TLRPC.TL_messages_stickerSetInstallResultArchive) response);
                }
            } else {
                Toast.makeText(getContext(), LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred), 0).show();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
        int type = r0 == true ? 1 : 0;
        mediaDataController.loadStickers(type, false, true);
    }

    /* renamed from: lambda$updateFields$17$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3108lambda$updateFields$17$orgtelegramuiComponentsStickersAlert(View v) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).toggleStickerSet(getContext(), this.stickerSet, 1, this.parentFragment, true, this.showTooltipWhenToggle);
    }

    /* renamed from: lambda$updateFields$18$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3109lambda$updateFields$18$orgtelegramuiComponentsStickersAlert(View v) {
        StickersAlertInstallDelegate stickersAlertInstallDelegate = this.installDelegate;
        if (stickersAlertInstallDelegate != null) {
            stickersAlertInstallDelegate.onStickerSetUninstalled();
        }
        dismiss();
        MediaDataController.getInstance(this.currentAccount).toggleStickerSet(getContext(), this.stickerSet, 0, this.parentFragment, true, this.showTooltipWhenToggle);
    }

    /* renamed from: lambda$updateFields$19$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3110lambda$updateFields$19$orgtelegramuiComponentsStickersAlert(View v) {
        showNameEnterAlert();
    }

    /* renamed from: lambda$updateFields$20$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3111lambda$updateFields$20$orgtelegramuiComponentsStickersAlert(View v) {
        dismiss();
    }

    private void showNameEnterAlert() {
        Context context = getContext();
        final int[] state = {0};
        FrameLayout fieldLayout = new FrameLayout(context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("ImportStickersEnterName", R.string.ImportStickersEnterName));
        builder.setPositiveButton(LocaleController.getString("Next", R.string.Next), StickersAlert$$ExternalSyntheticLambda11.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        linearLayout.addView(fieldLayout, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
        final TextView message = new TextView(context);
        final TextView textView = new TextView(context);
        textView.setTextSize(1, 16.0f);
        textView.setTextColor(getThemedColor(Theme.key_dialogTextHint));
        textView.setMaxLines(1);
        textView.setLines(1);
        textView.setText("t.me/addstickers/");
        textView.setInputType(16385);
        textView.setGravity(51);
        textView.setSingleLine(true);
        textView.setVisibility(4);
        textView.setImeOptions(6);
        textView.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        fieldLayout.addView(textView, LayoutHelper.createFrame(-2, 36, 51));
        final EditTextBoldCursor editText = new EditTextBoldCursor(context);
        editText.setBackground(null);
        editText.setLineColors(Theme.getColor(Theme.key_dialogInputField), Theme.getColor(Theme.key_dialogInputFieldActivated), Theme.getColor(Theme.key_dialogTextRed2));
        editText.setTextSize(1, 16.0f);
        editText.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        editText.setMaxLines(1);
        editText.setLines(1);
        editText.setInputType(16385);
        editText.setGravity(51);
        editText.setSingleLine(true);
        editText.setImeOptions(5);
        editText.setCursorColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        editText.setCursorSize(AndroidUtilities.dp(20.0f));
        editText.setCursorWidth(1.5f);
        editText.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        editText.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.StickersAlert.11
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (state[0] == 2) {
                    StickersAlert.this.checkUrlAvailable(message, editText.getText().toString(), false);
                }
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable s) {
            }
        });
        fieldLayout.addView(editText, LayoutHelper.createFrame(-1, 36, 51));
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda6
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView2, int i, KeyEvent keyEvent) {
                return StickersAlert.lambda$showNameEnterAlert$22(AlertDialog.Builder.this, textView2, i, keyEvent);
            }
        });
        editText.setSelection(editText.length());
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
            }
        });
        message.setText(AndroidUtilities.replaceTags(LocaleController.getString("ImportStickersEnterNameInfo", R.string.ImportStickersEnterNameInfo)));
        message.setTextSize(1, 14.0f);
        message.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
        message.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
        linearLayout.addView(message, LayoutHelper.createLinear(-1, -2));
        AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda22
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        StickersAlert.lambda$showNameEnterAlert$24(EditTextBoldCursor.this);
                    }
                });
            }
        });
        alertDialog.show();
        editText.requestFocus();
        alertDialog.getButton(-1).setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                StickersAlert.this.m3103x12612987(state, editText, message, textView, builder, view);
            }
        });
    }

    public static /* synthetic */ void lambda$showNameEnterAlert$21(DialogInterface dialog, int which) {
    }

    public static /* synthetic */ boolean lambda$showNameEnterAlert$22(AlertDialog.Builder builder, TextView view, int i, KeyEvent keyEvent) {
        if (i == 5) {
            builder.create().getButton(-1).callOnClick();
            return true;
        }
        return false;
    }

    public static /* synthetic */ void lambda$showNameEnterAlert$24(EditTextBoldCursor editText) {
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    /* renamed from: lambda$showNameEnterAlert$29$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3103x12612987(final int[] state, final EditTextBoldCursor editText, final TextView message, final TextView textView, AlertDialog.Builder builder, View v) {
        if (state[0] == 1) {
            return;
        }
        if (state[0] == 0) {
            state[0] = 1;
            TLRPC.TL_stickers_suggestShortName req = new TLRPC.TL_stickers_suggestShortName();
            String obj = editText.getText().toString();
            this.setTitle = obj;
            req.title = obj;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda25
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StickersAlert.this.m3101x396bf49(editText, message, textView, state, tLObject, tL_error);
                }
            });
        } else if (state[0] == 2) {
            state[0] = 3;
            if (!this.lastNameAvailable) {
                AndroidUtilities.shakeView(editText, 2.0f, 0);
                editText.performHapticFeedback(3, 2);
            }
            AndroidUtilities.hideKeyboard(editText);
            SendMessagesHelper.getInstance(this.currentAccount).prepareImportStickers(this.setTitle, this.lastCheckName, this.importingSoftware, this.importingStickersPaths, new MessagesStorage.StringCallback() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda18
                @Override // org.telegram.messenger.MessagesStorage.StringCallback
                public final void run(String str) {
                    StickersAlert.this.m3102xafbf468(str);
                }
            });
            builder.getDismissRunnable().run();
            dismiss();
        }
    }

    /* renamed from: lambda$showNameEnterAlert$27$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3101x396bf49(final EditTextBoldCursor editText, final TextView message, final TextView textView, final int[] state, final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                StickersAlert.this.m3100xfc318a2a(response, editText, message, textView, state);
            }
        });
    }

    /* renamed from: lambda$showNameEnterAlert$26$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3100xfc318a2a(TLObject response, EditTextBoldCursor editText, TextView message, TextView textView, int[] state) {
        boolean set = false;
        if (response instanceof TLRPC.TL_stickers_suggestedShortName) {
            TLRPC.TL_stickers_suggestedShortName res = (TLRPC.TL_stickers_suggestedShortName) response;
            if (res.short_name != null) {
                editText.setText(res.short_name);
                editText.setSelection(0, editText.length());
                checkUrlAvailable(message, editText.getText().toString(), true);
                set = true;
            }
        }
        textView.setVisibility(0);
        editText.setPadding(textView.getMeasuredWidth(), AndroidUtilities.dp(4.0f), 0, 0);
        if (!set) {
            editText.setText("");
        }
        state[0] = 2;
    }

    /* renamed from: lambda$showNameEnterAlert$28$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3102xafbf468(String param) {
        ImportingAlert importingAlert = new ImportingAlert(getContext(), this.lastCheckName, null, this.resourcesProvider);
        importingAlert.show();
    }

    public void checkUrlAvailable(final TextView message, final String text, boolean forceAvailable) {
        if (forceAvailable) {
            message.setText(LocaleController.getString("ImportStickersLinkAvailable", R.string.ImportStickersLinkAvailable));
            message.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGreenText));
            this.lastNameAvailable = true;
            this.lastCheckName = text;
            return;
        }
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        if (TextUtils.isEmpty(text)) {
            message.setText(LocaleController.getString("ImportStickersEnterUrlInfo", R.string.ImportStickersEnterUrlInfo));
            message.setTextColor(getThemedColor(Theme.key_dialogTextGray2));
            return;
        }
        this.lastNameAvailable = false;
        if (text != null) {
            if (text.startsWith("_") || text.endsWith("_")) {
                message.setText(LocaleController.getString("ImportStickersLinkInvalid", R.string.ImportStickersLinkInvalid));
                message.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteRedText4));
                return;
            }
            int N = text.length();
            for (int a = 0; a < N; a++) {
                char ch = text.charAt(a);
                if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    message.setText(LocaleController.getString("ImportStickersEnterUrlInfo", R.string.ImportStickersEnterUrlInfo));
                    message.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteRedText4));
                    return;
                }
            }
        }
        if (text == null || text.length() < 5) {
            message.setText(LocaleController.getString("ImportStickersLinkInvalidShort", R.string.ImportStickersLinkInvalidShort));
            message.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteRedText4));
        } else if (text.length() > 32) {
            message.setText(LocaleController.getString("ImportStickersLinkInvalidLong", R.string.ImportStickersLinkInvalidLong));
            message.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteRedText4));
        } else {
            message.setText(LocaleController.getString("ImportStickersLinkChecking", R.string.ImportStickersLinkChecking));
            message.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText8));
            this.lastCheckName = text;
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    StickersAlert.this.m3086xc0251f6b(text, message);
                }
            };
            this.checkRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 300L);
        }
    }

    /* renamed from: lambda$checkUrlAvailable$32$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3086xc0251f6b(final String text, final TextView message) {
        TLRPC.TL_stickers_checkShortName req = new TLRPC.TL_stickers_checkShortName();
        req.short_name = text;
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda21
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StickersAlert.this.m3085xb8bfea4c(text, message, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$checkUrlAvailable$31$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3085xb8bfea4c(final String text, final TextView message, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                StickersAlert.this.m3084xb15ab52d(text, error, response, message);
            }
        });
    }

    /* renamed from: lambda$checkUrlAvailable$30$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3084xb15ab52d(String text, TLRPC.TL_error error, TLObject response, TextView message) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(text)) {
            if (error == null && (response instanceof TLRPC.TL_boolTrue)) {
                message.setText(LocaleController.getString("ImportStickersLinkAvailable", R.string.ImportStickersLinkAvailable));
                message.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGreenText));
                this.lastNameAvailable = true;
                return;
            }
            message.setText(LocaleController.getString("ImportStickersLinkTaken", R.string.ImportStickersLinkTaken));
            message.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteRedText4));
            this.lastNameAvailable = false;
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    protected boolean canDismissWithSwipe() {
        return false;
    }

    public void updateLayout() {
        if (this.gridView.getChildCount() <= 0) {
            setScrollOffsetY(this.gridView.getPaddingTop());
            return;
        }
        View child = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(child);
        int top = child.getTop();
        int newOffset = 0;
        if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
            newOffset = top;
            runShadowAnimation(0, false);
        } else {
            runShadowAnimation(0, true);
        }
        if (this.scrollOffsetY != newOffset) {
            setScrollOffsetY(newOffset);
        }
    }

    public void setScrollOffsetY(int newOffset) {
        this.scrollOffsetY = newOffset;
        this.gridView.setTopGlowOffset(newOffset);
        if (this.stickerSetCovereds == null) {
            this.titleTextView.setTranslationY(newOffset);
            if (this.importingStickers == null) {
                this.optionsButton.setTranslationY(newOffset);
            }
            this.shadow[0].setTranslationY(newOffset);
        }
        this.containerView.invalidate();
    }

    private void hidePreview() {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.stickerPreviewLayout, View.ALPHA, 0.0f));
        animatorSet.setDuration(200L);
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.StickersAlert.12
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                StickersAlert.this.stickerPreviewLayout.setVisibility(8);
                StickersAlert.this.stickerImageView.setImageDrawable(null);
            }
        });
        animatorSet.start();
    }

    private void runShadowAnimation(final int num, final boolean show) {
        if (this.stickerSetCovereds != null) {
            return;
        }
        if ((show && this.shadow[num].getTag() != null) || (!show && this.shadow[num].getTag() == null)) {
            this.shadow[num].setTag(show ? null : 1);
            if (show) {
                this.shadow[num].setVisibility(0);
            }
            AnimatorSet[] animatorSetArr = this.shadowAnimation;
            if (animatorSetArr[num] != null) {
                animatorSetArr[num].cancel();
            }
            this.shadowAnimation[num] = new AnimatorSet();
            AnimatorSet animatorSet = this.shadowAnimation[num];
            Animator[] animatorArr = new Animator[1];
            View view = this.shadow[num];
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
            animatorSet.playTogether(animatorArr);
            this.shadowAnimation[num].setDuration(150L);
            this.shadowAnimation[num].addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.StickersAlert.13
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    if (StickersAlert.this.shadowAnimation[num] != null && StickersAlert.this.shadowAnimation[num].equals(animation)) {
                        if (!show) {
                            StickersAlert.this.shadow[num].setVisibility(4);
                        }
                        StickersAlert.this.shadowAnimation[num] = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animation) {
                    if (StickersAlert.this.shadowAnimation[num] != null && StickersAlert.this.shadowAnimation[num].equals(animation)) {
                        StickersAlert.this.shadowAnimation[num] = null;
                    }
                }
            });
            this.shadowAnimation[num].start();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void show() {
        super.show();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 4);
    }

    public void setOnDismissListener(Runnable onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        Runnable runnable = this.onDismissListener;
        if (runnable != null) {
            runnable.run();
        }
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        if (this.importingStickers != null) {
            ArrayList<SendMessagesHelper.ImportingSticker> arrayList = this.importingStickersPaths;
            if (arrayList != null) {
                int N = arrayList.size();
                for (int a = 0; a < N; a++) {
                    SendMessagesHelper.ImportingSticker sticker = this.importingStickersPaths.get(a);
                    if (!sticker.validated) {
                        FileLoader.getInstance(this.currentAccount).cancelFileUpload(sticker.path, false);
                    }
                    if (sticker.animated) {
                        new File(sticker.path).delete();
                    }
                }
            }
            int N2 = this.currentAccount;
            NotificationCenter.getInstance(N2).removeObserver(this, NotificationCenter.fileUploaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 4);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog
    public void onStart() {
        super.onStart();
        Bulletin.addDelegate((FrameLayout) this.containerView, new Bulletin.Delegate() { // from class: org.telegram.ui.Components.StickersAlert.14
            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public /* synthetic */ void onHide(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onHide(this, bulletin);
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public /* synthetic */ void onOffsetChange(float f) {
                Bulletin.Delegate.CC.$default$onOffsetChange(this, f);
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public /* synthetic */ void onShow(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onShow(this, bulletin);
            }

            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public int getBottomOffset(int tag) {
                if (StickersAlert.this.pickerBottomFrameLayout != null) {
                    return StickersAlert.this.pickerBottomFrameLayout.getHeight();
                }
                return 0;
            }
        });
    }

    @Override // android.app.Dialog
    protected void onStop() {
        super.onStop();
        Bulletin.removeDelegate((FrameLayout) this.containerView);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        HashMap<String, SendMessagesHelper.ImportingSticker> hashMap;
        final String location;
        final SendMessagesHelper.ImportingSticker sticker;
        if (id == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.gridView;
            if (recyclerListView != null) {
                int count = recyclerListView.getChildCount();
                for (int a = 0; a < count; a++) {
                    this.gridView.getChildAt(a).invalidate();
                }
            }
        } else if (id == NotificationCenter.fileUploaded) {
            HashMap<String, SendMessagesHelper.ImportingSticker> hashMap2 = this.uploadImportStickers;
            if (hashMap2 != null && (sticker = hashMap2.get((location = (String) args[0]))) != null) {
                sticker.uploadMedia(this.currentAccount, (TLRPC.InputFile) args[1], new Runnable() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda9
                    @Override // java.lang.Runnable
                    public final void run() {
                        StickersAlert.this.m3087x1e649613(location, sticker);
                    }
                });
            }
        } else if (id == NotificationCenter.fileUploadFailed && (hashMap = this.uploadImportStickers) != null) {
            SendMessagesHelper.ImportingSticker sticker2 = hashMap.remove((String) args[0]);
            if (sticker2 != null) {
                removeSticker(sticker2);
            }
            if (this.uploadImportStickers.isEmpty()) {
                updateFields();
            }
        }
    }

    /* renamed from: lambda$didReceivedNotification$33$org-telegram-ui-Components-StickersAlert */
    public /* synthetic */ void m3087x1e649613(String location, SendMessagesHelper.ImportingSticker sticker) {
        if (isDismissed()) {
            return;
        }
        this.uploadImportStickers.remove(location);
        if (!"application/x-tgsticker".equals(sticker.mimeType)) {
            removeSticker(sticker);
        } else {
            sticker.validated = true;
            int idx = this.importingStickersPaths.indexOf(sticker);
            if (idx >= 0) {
                RecyclerView.ViewHolder holder = this.gridView.findViewHolderForAdapterPosition(idx);
                if (holder != null) {
                    ((StickerEmojiCell) holder.itemView).setSticker(sticker);
                }
            } else {
                this.adapter.notifyDataSetChanged();
            }
        }
        if (this.uploadImportStickers.isEmpty()) {
            updateFields();
        }
    }

    private void setButton(View.OnClickListener onClickListener, String title, String colorKey) {
        setButton(onClickListener, title, colorKey, null, null);
    }

    private void setButton(View.OnClickListener onClickListener, String title, String colorKey, String backgroundColorKey, String backgroundSelectorColorKey) {
        TextView textView = this.pickerBottomLayout;
        this.buttonTextColorKey = colorKey;
        textView.setTextColor(getThemedColor(colorKey));
        this.pickerBottomLayout.setText(this.customButtonDelegate == null ? title.toUpperCase() : title);
        this.pickerBottomLayout.setOnClickListener(onClickListener);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) this.pickerBottomLayout.getLayoutParams();
        ViewGroup.MarginLayoutParams shadowParams = (ViewGroup.MarginLayoutParams) this.shadow[1].getLayoutParams();
        ViewGroup.MarginLayoutParams gridParams = (ViewGroup.MarginLayoutParams) this.gridView.getLayoutParams();
        ViewGroup.MarginLayoutParams emptyParams = (ViewGroup.MarginLayoutParams) this.emptyView.getLayoutParams();
        if (backgroundColorKey == null || backgroundSelectorColorKey == null) {
            this.pickerBottomLayout.setBackground(Theme.createSelectorWithBackgroundDrawable(getThemedColor(Theme.key_dialogBackground), getThemedColor(Theme.key_listSelector)));
            this.pickerBottomFrameLayout.setBackgroundColor(0);
            params.bottomMargin = 0;
            params.rightMargin = 0;
            params.topMargin = 0;
            params.leftMargin = 0;
            int dp = AndroidUtilities.dp(48.0f);
            shadowParams.bottomMargin = dp;
            gridParams.bottomMargin = dp;
            emptyParams.bottomMargin = dp;
        } else {
            this.pickerBottomLayout.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor(backgroundColorKey), getThemedColor(backgroundSelectorColorKey)));
            this.pickerBottomFrameLayout.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
            int dp2 = AndroidUtilities.dp(8.0f);
            params.bottomMargin = dp2;
            params.rightMargin = dp2;
            params.topMargin = dp2;
            params.leftMargin = dp2;
            int dp3 = AndroidUtilities.dp(64.0f);
            shadowParams.bottomMargin = dp3;
            gridParams.bottomMargin = dp3;
            emptyParams.bottomMargin = dp3;
        }
        this.containerView.requestLayout();
    }

    public boolean isShowTooltipWhenToggle() {
        return this.showTooltipWhenToggle;
    }

    public void setShowTooltipWhenToggle(boolean showTooltipWhenToggle) {
        this.showTooltipWhenToggle = showTooltipWhenToggle;
    }

    public void updateColors() {
        updateColors(false);
    }

    public void updateColors(boolean applyDescriptions) {
        this.adapter.updateColors();
        this.titleTextView.setHighlightColor(getThemedColor(Theme.key_dialogLinkSelection));
        this.stickerPreviewLayout.setBackgroundColor(getThemedColor(Theme.key_dialogBackground) & (-536870913));
        this.optionsButton.setIconColor(getThemedColor(Theme.key_sheet_other));
        this.optionsButton.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), false);
        this.optionsButton.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon), true);
        this.optionsButton.setPopupItemsSelectorColor(getThemedColor(Theme.key_dialogButtonSelector));
        this.optionsButton.redrawPopup(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
        if (applyDescriptions) {
            if (Theme.isAnimatingColor() && this.animatingDescriptions == null) {
                ArrayList<ThemeDescription> themeDescriptions = getThemeDescriptions();
                this.animatingDescriptions = themeDescriptions;
                int N = themeDescriptions.size();
                for (int i = 0; i < N; i++) {
                    this.animatingDescriptions.get(i).setDelegateDisabled();
                }
            }
            int N2 = this.animatingDescriptions.size();
            for (int i2 = 0; i2 < N2; i2++) {
                ThemeDescription description = this.animatingDescriptions.get(i2);
                description.setColor(getThemedColor(description.getCurrentKey()), false, false);
            }
        }
        if (!Theme.isAnimatingColor() && this.animatingDescriptions != null) {
            this.animatingDescriptions = null;
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> descriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate delegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.Components.StickersAlert$$ExternalSyntheticLambda27
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                StickersAlert.this.updateColors();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        descriptions.add(new ThemeDescription(this.containerView, 0, null, null, new Drawable[]{this.shadowDrawable}, null, Theme.key_dialogBackground));
        descriptions.add(new ThemeDescription(this.containerView, 0, null, null, null, null, Theme.key_sheet_scrollUp));
        this.adapter.getThemeDescriptions(descriptions, delegate);
        descriptions.add(new ThemeDescription(this.shadow[0], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogShadowLine));
        descriptions.add(new ThemeDescription(this.shadow[1], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogShadowLine));
        descriptions.add(new ThemeDescription(this.gridView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_dialogScrollGlow));
        descriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogTextBlack));
        descriptions.add(new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_LINKCOLOR, null, null, null, null, Theme.key_dialogTextLink));
        descriptions.add(new ThemeDescription(this.optionsButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_player_actionBarSelector));
        descriptions.add(new ThemeDescription(this.pickerBottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_dialogBackground));
        descriptions.add(new ThemeDescription(this.pickerBottomLayout, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_listSelector));
        descriptions.add(new ThemeDescription(this.pickerBottomLayout, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, this.buttonTextColorKey));
        descriptions.add(new ThemeDescription(this.previewSendButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_dialogTextBlue2));
        descriptions.add(new ThemeDescription(this.previewSendButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_dialogBackground));
        descriptions.add(new ThemeDescription(this.previewSendButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_listSelector));
        descriptions.add(new ThemeDescription(this.previewSendButtonShadow, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_dialogShadowLine));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_dialogLinkSelection));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_dialogBackground));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_sheet_other));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_actionBarDefaultSubmenuItem));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_actionBarDefaultSubmenuItemIcon));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_dialogButtonSelector));
        descriptions.add(new ThemeDescription(null, 0, null, null, null, delegate, Theme.key_actionBarDefaultSubmenuBackground));
        return descriptions;
    }

    /* loaded from: classes5.dex */
    public class GridAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int stickersPerRow;
        private int stickersRowCount;
        private int totalItems;
        private SparseArray<Object> cache = new SparseArray<>();
        private SparseArray<TLRPC.StickerSetCovered> positionsToSets = new SparseArray<>();

        public GridAdapter(Context context) {
            StickersAlert.this = r1;
            this.context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.totalItems;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                Object object = this.cache.get(position);
                if (object == null) {
                    return 1;
                }
                return object instanceof TLRPC.Document ? 0 : 2;
            }
            return 0;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 0:
                    StickerEmojiCell cell = new StickerEmojiCell(this.context, false) { // from class: org.telegram.ui.Components.StickersAlert.GridAdapter.1
                        @Override // android.widget.FrameLayout, android.view.View
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(StickersAlert.this.itemSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), C.BUFFER_FLAG_ENCRYPTED));
                        }
                    };
                    cell.getImageView().setLayerNum(7);
                    view = cell;
                    break;
                case 1:
                    view = new EmptyCell(this.context);
                    break;
                case 2:
                    view = new FeaturedStickerSetInfoCell(this.context, 8, true, false, StickersAlert.this.resourcesProvider);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (StickersAlert.this.stickerSetCovereds == null) {
                if (StickersAlert.this.importingStickers != null) {
                    ((StickerEmojiCell) holder.itemView).setSticker((SendMessagesHelper.ImportingSticker) StickersAlert.this.importingStickersPaths.get(position));
                    return;
                } else {
                    ((StickerEmojiCell) holder.itemView).setSticker(StickersAlert.this.stickerSet.documents.get(position), StickersAlert.this.stickerSet, StickersAlert.this.showEmoji);
                    return;
                }
            }
            switch (holder.getItemViewType()) {
                case 0:
                    TLRPC.Document sticker = (TLRPC.Document) this.cache.get(position);
                    ((StickerEmojiCell) holder.itemView).setSticker(sticker, this.positionsToSets.get(position), false);
                    return;
                case 1:
                    ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(82.0f));
                    return;
                case 2:
                    TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(((Integer) this.cache.get(position)).intValue());
                    FeaturedStickerSetInfoCell cell = (FeaturedStickerSetInfoCell) holder.itemView;
                    cell.setStickerSet(stickerSetCovered, false);
                    return;
                default:
                    return;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            int count;
            int i;
            int i2 = 0;
            if (StickersAlert.this.stickerSetCovereds != null) {
                int width = StickersAlert.this.gridView.getMeasuredWidth();
                if (width == 0) {
                    width = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = width / AndroidUtilities.dp(72.0f);
                StickersAlert.this.layoutManager.setSpanCount(this.stickersPerRow);
                this.cache.clear();
                this.positionsToSets.clear();
                this.totalItems = 0;
                this.stickersRowCount = 0;
                for (int a = 0; a < StickersAlert.this.stickerSetCovereds.size(); a++) {
                    TLRPC.StickerSetCovered pack = (TLRPC.StickerSetCovered) StickersAlert.this.stickerSetCovereds.get(a);
                    if (!pack.covers.isEmpty() || pack.cover != null) {
                        double d = this.stickersRowCount;
                        double ceil = Math.ceil(StickersAlert.this.stickerSetCovereds.size() / this.stickersPerRow);
                        Double.isNaN(d);
                        this.stickersRowCount = (int) (d + ceil);
                        this.positionsToSets.put(this.totalItems, pack);
                        SparseArray<Object> sparseArray = this.cache;
                        int i3 = this.totalItems;
                        this.totalItems = i3 + 1;
                        sparseArray.put(i3, Integer.valueOf(a));
                        int i4 = this.totalItems / this.stickersPerRow;
                        if (!pack.covers.isEmpty()) {
                            count = (int) Math.ceil(pack.covers.size() / this.stickersPerRow);
                            for (int b = 0; b < pack.covers.size(); b++) {
                                this.cache.put(this.totalItems + b, pack.covers.get(b));
                            }
                        } else {
                            count = 1;
                            this.cache.put(this.totalItems, pack.cover);
                        }
                        int b2 = 0;
                        while (true) {
                            i = this.stickersPerRow;
                            if (b2 >= count * i) {
                                break;
                            }
                            this.positionsToSets.put(this.totalItems + b2, pack);
                            b2++;
                        }
                        int b3 = this.totalItems;
                        this.totalItems = b3 + (i * count);
                    }
                }
            } else if (StickersAlert.this.importingStickersPaths != null) {
                this.totalItems = StickersAlert.this.importingStickersPaths.size();
            } else {
                if (StickersAlert.this.stickerSet != null) {
                    i2 = StickersAlert.this.stickerSet.documents.size();
                }
                this.totalItems = i2;
            }
            super.notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRemoved(int position) {
            if (StickersAlert.this.importingStickersPaths != null) {
                this.totalItems = StickersAlert.this.importingStickersPaths.size();
            }
            super.notifyItemRemoved(position);
        }

        public void updateColors() {
            if (StickersAlert.this.stickerSetCovereds != null) {
                int size = StickersAlert.this.gridView.getChildCount();
                for (int i = 0; i < size; i++) {
                    View child = StickersAlert.this.gridView.getChildAt(i);
                    if (child instanceof FeaturedStickerSetInfoCell) {
                        ((FeaturedStickerSetInfoCell) child).updateColors();
                    }
                }
            }
        }

        public void getThemeDescriptions(List<ThemeDescription> descriptions, ThemeDescription.ThemeDescriptionDelegate delegate) {
            if (StickersAlert.this.stickerSetCovereds != null) {
                FeaturedStickerSetInfoCell.createThemeDescriptions(descriptions, StickersAlert.this.gridView, delegate);
            }
        }
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().closeWithMenu();
        } else {
            super.onBackPressed();
        }
    }
}
