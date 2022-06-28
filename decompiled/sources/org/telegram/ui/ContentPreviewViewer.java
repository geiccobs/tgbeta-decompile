package org.telegram.ui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.util.MimeTypes;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ContentPreviewViewer;
/* loaded from: classes4.dex */
public class ContentPreviewViewer {
    private static final int CONTENT_TYPE_GIF = 1;
    private static final int CONTENT_TYPE_NONE = -1;
    private static final int CONTENT_TYPE_STICKER = 0;
    private static volatile ContentPreviewViewer Instance = null;
    private static TextPaint textPaint;
    private float blurProgress;
    private Bitmap blurrBitmap;
    private boolean clearsInputField;
    private boolean closeOnDismiss;
    private FrameLayoutDrawer containerView;
    private int currentAccount;
    private int currentContentType;
    private TLRPC.Document currentDocument;
    private float currentMoveY;
    private float currentMoveYProgress;
    private View currentPreviewCell;
    private String currentQuery;
    private TLRPC.InputStickerSet currentStickerSet;
    private ContentPreviewViewerDelegate delegate;
    private boolean drawEffect;
    private float finalMoveY;
    private SendMessagesHelper.ImportingSticker importingSticker;
    private TLRPC.BotInlineResult inlineResult;
    private boolean isRecentSticker;
    private WindowInsets lastInsets;
    private float lastTouchY;
    private long lastUpdateTime;
    private boolean menuVisible;
    private Runnable openPreviewRunnable;
    private Activity parentActivity;
    private Object parentObject;
    ActionBarPopupWindow popupWindow;
    private Theme.ResourcesProvider resourcesProvider;
    private float showProgress;
    private Drawable slideUpDrawable;
    private float startMoveY;
    private int startX;
    private int startY;
    private StaticLayout stickerEmojiLayout;
    private UnlockPremiumView unlockPremiumView;
    VibrationEffect vibrationEffect;
    private ActionBarPopupWindow visibleMenu;
    private WindowManager.LayoutParams windowLayoutParams;
    private FrameLayout windowView;
    private float moveY = 0.0f;
    private ColorDrawable backgroundDrawable = new ColorDrawable(1895825408);
    private ImageReceiver centerImage = new ImageReceiver();
    private ImageReceiver effectImage = new ImageReceiver();
    private boolean isVisible = false;
    private int keyboardHeight = AndroidUtilities.dp(200.0f);
    private Paint paint = new Paint(1);
    private Runnable showSheetRunnable = new AnonymousClass1();

    /* loaded from: classes4.dex */
    public class FrameLayoutDrawer extends FrameLayout {
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public FrameLayoutDrawer(Context context) {
            super(context);
            ContentPreviewViewer.this = r1;
            setWillNotDraw(false);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            ContentPreviewViewer.this.onDraw(canvas);
        }
    }

    /* loaded from: classes4.dex */
    public interface ContentPreviewViewerDelegate {
        boolean canSchedule();

        long getDialogId();

        String getQuery(boolean z);

        void gifAddedOrDeleted();

        boolean isInScheduleMode();

        boolean needMenu();

        boolean needOpen();

        boolean needRemove();

        boolean needSend();

        void openSet(TLRPC.InputStickerSet inputStickerSet, boolean z);

        void remove(SendMessagesHelper.ImportingSticker importingSticker);

        void sendGif(Object obj, Object obj2, boolean z, int i);

        void sendSticker(TLRPC.Document document, String str, Object obj, boolean z, int i);

        /* renamed from: org.telegram.ui.ContentPreviewViewer$ContentPreviewViewerDelegate$-CC */
        /* loaded from: classes4.dex */
        public final /* synthetic */ class CC {
            public static boolean $default$needRemove(ContentPreviewViewerDelegate _this) {
                return false;
            }

            public static void $default$remove(ContentPreviewViewerDelegate _this, SendMessagesHelper.ImportingSticker sticker) {
            }

            public static String $default$getQuery(ContentPreviewViewerDelegate _this, boolean isGif) {
                return null;
            }

            public static boolean $default$needOpen(ContentPreviewViewerDelegate _this) {
                return true;
            }

            public static void $default$sendGif(ContentPreviewViewerDelegate _this, Object gif, Object parent, boolean notify, int scheduleDate) {
            }

            public static void $default$gifAddedOrDeleted(ContentPreviewViewerDelegate _this) {
            }

            public static boolean $default$needMenu(ContentPreviewViewerDelegate _this) {
                return true;
            }
        }
    }

    /* renamed from: org.telegram.ui.ContentPreviewViewer$1 */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
            ContentPreviewViewer.this = this$0;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean canDelete;
            int top;
            ArrayList<Integer> actions;
            ArrayList<Integer> icons;
            int top2;
            String str;
            int i;
            if (ContentPreviewViewer.this.parentActivity != null) {
                ContentPreviewViewer.this.closeOnDismiss = true;
                if (ContentPreviewViewer.this.currentContentType == 0) {
                    if (!MessageObject.isPremiumSticker(ContentPreviewViewer.this.currentDocument) || AccountInstance.getInstance(ContentPreviewViewer.this.currentAccount).getUserConfig().isPremium()) {
                        boolean inFavs = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).isStickerInFavorites(ContentPreviewViewer.this.currentDocument);
                        ArrayList<CharSequence> items = new ArrayList<>();
                        ArrayList<Integer> actions2 = new ArrayList<>();
                        ArrayList<Integer> icons2 = new ArrayList<>();
                        ContentPreviewViewer.this.menuVisible = true;
                        ContentPreviewViewer.this.containerView.invalidate();
                        if (ContentPreviewViewer.this.delegate != null) {
                            if (!ContentPreviewViewer.this.delegate.needSend() || ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                                actions = actions2;
                                icons = icons2;
                            } else {
                                items.add(LocaleController.getString("SendStickerPreview", R.string.SendStickerPreview));
                                icons = icons2;
                                icons.add(Integer.valueOf((int) R.drawable.msg_send));
                                actions = actions2;
                                actions.add(0);
                            }
                            if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                                items.add(LocaleController.getString("SendWithoutSound", R.string.SendWithoutSound));
                                icons.add(Integer.valueOf((int) R.drawable.input_notify_off));
                                actions.add(6);
                            }
                            if (ContentPreviewViewer.this.delegate.canSchedule()) {
                                items.add(LocaleController.getString("Schedule", R.string.Schedule));
                                icons.add(Integer.valueOf((int) R.drawable.msg_autodelete));
                                actions.add(3);
                            }
                            if (ContentPreviewViewer.this.currentStickerSet != null && ContentPreviewViewer.this.delegate.needOpen()) {
                                items.add(LocaleController.formatString("ViewPackPreview", R.string.ViewPackPreview, new Object[0]));
                                icons.add(Integer.valueOf((int) R.drawable.msg_media));
                                actions.add(1);
                            }
                            if (ContentPreviewViewer.this.delegate.needRemove()) {
                                items.add(LocaleController.getString("ImportStickersRemoveMenu", R.string.ImportStickersRemoveMenu));
                                icons.add(Integer.valueOf((int) R.drawable.msg_delete));
                                actions.add(5);
                            }
                        } else {
                            actions = actions2;
                            icons = icons2;
                        }
                        if (!MessageObject.isMaskDocument(ContentPreviewViewer.this.currentDocument) && (inFavs || (MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).canAddStickerToFavorites() && MessageObject.isStickerHasSet(ContentPreviewViewer.this.currentDocument)))) {
                            if (inFavs) {
                                i = R.string.DeleteFromFavorites;
                                str = "DeleteFromFavorites";
                            } else {
                                i = R.string.AddToFavorites;
                                str = "AddToFavorites";
                            }
                            items.add(LocaleController.getString(str, i));
                            icons.add(Integer.valueOf(inFavs ? R.drawable.msg_unfave : R.drawable.msg_fave));
                            actions.add(2);
                        }
                        if (ContentPreviewViewer.this.isRecentSticker) {
                            items.add(LocaleController.getString("DeleteFromRecent", R.string.DeleteFromRecent));
                            icons.add(Integer.valueOf((int) R.drawable.msg_delete));
                            actions.add(4);
                        }
                        if (items.isEmpty()) {
                            return;
                        }
                        int[] ic = new int[icons.size()];
                        for (int a = 0; a < icons.size(); a++) {
                            ic[a] = icons.get(a).intValue();
                        }
                        View.OnClickListener onItemClickListener = new View$OnClickListenerC00671(actions, inFavs);
                        ActionBarPopupWindow.ActionBarPopupWindowLayout previewMenu = new ActionBarPopupWindow.ActionBarPopupWindowLayout(ContentPreviewViewer.this.containerView.getContext(), R.drawable.popup_fixed_alert2, ContentPreviewViewer.this.resourcesProvider);
                        for (int i2 = 0; i2 < items.size(); i2++) {
                            View item = ActionBarMenuItem.addItem(previewMenu, icons.get(i2).intValue(), items.get(i2), false, ContentPreviewViewer.this.resourcesProvider);
                            item.setTag(Integer.valueOf(i2));
                            item.setOnClickListener(onItemClickListener);
                        }
                        ContentPreviewViewer.this.popupWindow = new ActionBarPopupWindow(previewMenu, -2, -2) { // from class: org.telegram.ui.ContentPreviewViewer.1.2
                            {
                                AnonymousClass1.this = this;
                            }

                            @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow, android.widget.PopupWindow
                            public void dismiss() {
                                super.dismiss();
                                ContentPreviewViewer.this.popupWindow = null;
                                ContentPreviewViewer.this.menuVisible = false;
                                if (ContentPreviewViewer.this.closeOnDismiss) {
                                    ContentPreviewViewer.this.close();
                                }
                            }
                        };
                        ContentPreviewViewer.this.popupWindow.setPauseNotifications(true);
                        ContentPreviewViewer.this.popupWindow.setDismissAnimationDuration(100);
                        ContentPreviewViewer.this.popupWindow.setScaleOut(true);
                        ContentPreviewViewer.this.popupWindow.setOutsideTouchable(true);
                        ContentPreviewViewer.this.popupWindow.setClippingEnabled(true);
                        ContentPreviewViewer.this.popupWindow.setAnimationStyle(R.style.PopupContextAnimation);
                        ContentPreviewViewer.this.popupWindow.setFocusable(true);
                        previewMenu.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                        ContentPreviewViewer.this.popupWindow.setInputMethodMode(2);
                        ContentPreviewViewer.this.popupWindow.getContentView().setFocusableInTouchMode(true);
                        int insets = 0;
                        if (Build.VERSION.SDK_INT >= 21 && ContentPreviewViewer.this.lastInsets != null) {
                            insets = ContentPreviewViewer.this.lastInsets.getStableInsetBottom() + ContentPreviewViewer.this.lastInsets.getStableInsetTop();
                            top2 = ContentPreviewViewer.this.lastInsets.getStableInsetTop();
                        } else {
                            top2 = AndroidUtilities.statusBarHeight;
                        }
                        int size = ContentPreviewViewer.this.currentContentType == 1 ? Math.min(ContentPreviewViewer.this.containerView.getWidth(), ContentPreviewViewer.this.containerView.getHeight() - insets) - AndroidUtilities.dp(40.0f) : ContentPreviewViewer.this.drawEffect ? (int) (Math.min(ContentPreviewViewer.this.containerView.getWidth(), ContentPreviewViewer.this.containerView.getHeight() - insets) - AndroidUtilities.dpf2(40.0f)) : (int) (Math.min(ContentPreviewViewer.this.containerView.getWidth(), ContentPreviewViewer.this.containerView.getHeight() - insets) / 1.8f);
                        int y = (int) (ContentPreviewViewer.this.moveY + Math.max((size / 2) + top2 + (ContentPreviewViewer.this.stickerEmojiLayout != null ? AndroidUtilities.dp(40.0f) : 0), ((ContentPreviewViewer.this.containerView.getHeight() - insets) - ContentPreviewViewer.this.keyboardHeight) / 2) + (size / 2));
                        int y2 = y + AndroidUtilities.dp(24.0f);
                        if (ContentPreviewViewer.this.drawEffect) {
                            y2 += AndroidUtilities.dp(24.0f);
                        }
                        ContentPreviewViewer.this.popupWindow.showAtLocation(ContentPreviewViewer.this.containerView, 0, (int) ((ContentPreviewViewer.this.containerView.getMeasuredWidth() - previewMenu.getMeasuredWidth()) / 2.0f), y2);
                        ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                        return;
                    }
                    ContentPreviewViewer.this.showUnlockPremiumView();
                    ContentPreviewViewer.this.menuVisible = true;
                    ContentPreviewViewer.this.containerView.invalidate();
                    ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                } else if (ContentPreviewViewer.this.delegate != null) {
                    ContentPreviewViewer.this.menuVisible = true;
                    ArrayList<CharSequence> items2 = new ArrayList<>();
                    final ArrayList<Integer> actions3 = new ArrayList<>();
                    ArrayList<Integer> icons3 = new ArrayList<>();
                    if (ContentPreviewViewer.this.delegate.needSend() && !ContentPreviewViewer.this.delegate.isInScheduleMode()) {
                        items2.add(LocaleController.getString("SendGifPreview", R.string.SendGifPreview));
                        icons3.add(Integer.valueOf((int) R.drawable.msg_send));
                        actions3.add(0);
                    }
                    if (ContentPreviewViewer.this.delegate.canSchedule()) {
                        items2.add(LocaleController.getString("Schedule", R.string.Schedule));
                        icons3.add(Integer.valueOf((int) R.drawable.msg_autodelete));
                        actions3.add(3);
                    }
                    if (ContentPreviewViewer.this.currentDocument != null) {
                        boolean hasRecentGif = MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).hasRecentGif(ContentPreviewViewer.this.currentDocument);
                        canDelete = hasRecentGif;
                        if (hasRecentGif) {
                            items2.add(LocaleController.formatString("Delete", R.string.Delete, new Object[0]));
                            icons3.add(Integer.valueOf((int) R.drawable.msg_delete));
                            actions3.add(1);
                        } else {
                            items2.add(LocaleController.formatString("SaveToGIFs", R.string.SaveToGIFs, new Object[0]));
                            icons3.add(Integer.valueOf((int) R.drawable.msg_gif_add));
                            actions3.add(2);
                        }
                    } else {
                        canDelete = false;
                    }
                    int[] ic2 = new int[icons3.size()];
                    for (int a2 = 0; a2 < icons3.size(); a2++) {
                        ic2[a2] = icons3.get(a2).intValue();
                    }
                    ActionBarPopupWindow.ActionBarPopupWindowLayout previewMenu2 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(ContentPreviewViewer.this.containerView.getContext(), R.drawable.popup_fixed_alert2, ContentPreviewViewer.this.resourcesProvider);
                    View.OnClickListener onItemClickListener2 = new View.OnClickListener() { // from class: org.telegram.ui.ContentPreviewViewer$1$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            ContentPreviewViewer.AnonymousClass1.this.m3295lambda$run$1$orgtelegramuiContentPreviewViewer$1(actions3, view);
                        }
                    };
                    for (int i3 = 0; i3 < items2.size(); i3++) {
                        ActionBarMenuSubItem item2 = ActionBarMenuItem.addItem(previewMenu2, icons3.get(i3).intValue(), items2.get(i3), false, ContentPreviewViewer.this.resourcesProvider);
                        item2.setTag(Integer.valueOf(i3));
                        item2.setOnClickListener(onItemClickListener2);
                        if (canDelete && i3 == items2.size() - 1) {
                            item2.setColors(ContentPreviewViewer.this.getThemedColor(Theme.key_dialogTextRed2), ContentPreviewViewer.this.getThemedColor(Theme.key_dialogRedIcon));
                        }
                    }
                    ContentPreviewViewer.this.popupWindow = new ActionBarPopupWindow(previewMenu2, -2, -2) { // from class: org.telegram.ui.ContentPreviewViewer.1.3
                        {
                            AnonymousClass1.this = this;
                        }

                        @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow, android.widget.PopupWindow
                        public void dismiss() {
                            super.dismiss();
                            ContentPreviewViewer.this.popupWindow = null;
                            ContentPreviewViewer.this.menuVisible = false;
                            if (ContentPreviewViewer.this.closeOnDismiss) {
                                ContentPreviewViewer.this.close();
                            }
                        }
                    };
                    ContentPreviewViewer.this.popupWindow.setPauseNotifications(true);
                    ContentPreviewViewer.this.popupWindow.setDismissAnimationDuration(150);
                    ContentPreviewViewer.this.popupWindow.setScaleOut(true);
                    ContentPreviewViewer.this.popupWindow.setOutsideTouchable(true);
                    ContentPreviewViewer.this.popupWindow.setClippingEnabled(true);
                    ContentPreviewViewer.this.popupWindow.setAnimationStyle(R.style.PopupContextAnimation);
                    ContentPreviewViewer.this.popupWindow.setFocusable(true);
                    previewMenu2.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                    ContentPreviewViewer.this.popupWindow.setInputMethodMode(2);
                    ContentPreviewViewer.this.popupWindow.getContentView().setFocusableInTouchMode(true);
                    int insets2 = 0;
                    if (Build.VERSION.SDK_INT >= 21 && ContentPreviewViewer.this.lastInsets != null) {
                        insets2 = ContentPreviewViewer.this.lastInsets.getStableInsetBottom() + ContentPreviewViewer.this.lastInsets.getStableInsetTop();
                        top = ContentPreviewViewer.this.lastInsets.getStableInsetTop();
                    } else {
                        top = AndroidUtilities.statusBarHeight;
                    }
                    int size2 = Math.min(ContentPreviewViewer.this.containerView.getWidth(), ContentPreviewViewer.this.containerView.getHeight() - insets2) - AndroidUtilities.dp(40.0f);
                    int y3 = (int) (ContentPreviewViewer.this.moveY + Math.max((size2 / 2) + top + (ContentPreviewViewer.this.stickerEmojiLayout != null ? AndroidUtilities.dp(40.0f) : 0), ((ContentPreviewViewer.this.containerView.getHeight() - insets2) - ContentPreviewViewer.this.keyboardHeight) / 2) + (size2 / 2));
                    ContentPreviewViewer.this.popupWindow.showAtLocation(ContentPreviewViewer.this.containerView, 0, (int) ((ContentPreviewViewer.this.containerView.getMeasuredWidth() - previewMenu2.getMeasuredWidth()) / 2.0f), (int) (y3 + (AndroidUtilities.dp(24.0f) - ContentPreviewViewer.this.moveY)));
                    ContentPreviewViewer.this.containerView.performHapticFeedback(0);
                    if (ContentPreviewViewer.this.moveY != 0.0f) {
                        if (ContentPreviewViewer.this.finalMoveY == 0.0f) {
                            ContentPreviewViewer.this.finalMoveY = 0.0f;
                            ContentPreviewViewer contentPreviewViewer = ContentPreviewViewer.this;
                            contentPreviewViewer.startMoveY = contentPreviewViewer.moveY;
                        }
                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ContentPreviewViewer$1$$ExternalSyntheticLambda0
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                                ContentPreviewViewer.AnonymousClass1.this.m3296lambda$run$2$orgtelegramuiContentPreviewViewer$1(valueAnimator2);
                            }
                        });
                        valueAnimator.setDuration(350L);
                        valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                        valueAnimator.start();
                    }
                }
            }
        }

        /* renamed from: org.telegram.ui.ContentPreviewViewer$1$1 */
        /* loaded from: classes4.dex */
        class View$OnClickListenerC00671 implements View.OnClickListener {
            final /* synthetic */ ArrayList val$actions;
            final /* synthetic */ boolean val$inFavs;

            View$OnClickListenerC00671(ArrayList arrayList, boolean z) {
                AnonymousClass1.this = this$1;
                this.val$actions = arrayList;
                this.val$inFavs = z;
            }

            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (ContentPreviewViewer.this.parentActivity == null) {
                    return;
                }
                int which = ((Integer) v.getTag()).intValue();
                if (((Integer) this.val$actions.get(which)).intValue() == 0 || ((Integer) this.val$actions.get(which)).intValue() == 6) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.sendSticker(ContentPreviewViewer.this.currentDocument, ContentPreviewViewer.this.currentQuery, ContentPreviewViewer.this.parentObject, ((Integer) this.val$actions.get(which)).intValue() == 0, 0);
                    }
                } else if (((Integer) this.val$actions.get(which)).intValue() == 1) {
                    if (ContentPreviewViewer.this.delegate != null) {
                        ContentPreviewViewer.this.delegate.openSet(ContentPreviewViewer.this.currentStickerSet, ContentPreviewViewer.this.clearsInputField);
                    }
                } else if (((Integer) this.val$actions.get(which)).intValue() == 2) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(2, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), this.val$inFavs);
                } else if (((Integer) this.val$actions.get(which)).intValue() == 3) {
                    final TLRPC.Document sticker = ContentPreviewViewer.this.currentDocument;
                    final Object parent = ContentPreviewViewer.this.parentObject;
                    final String query = ContentPreviewViewer.this.currentQuery;
                    final ContentPreviewViewerDelegate stickerPreviewViewerDelegate = ContentPreviewViewer.this.delegate;
                    AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, stickerPreviewViewerDelegate.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.ContentPreviewViewer$1$1$$ExternalSyntheticLambda0
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i) {
                            ContentPreviewViewer.ContentPreviewViewerDelegate.this.sendSticker(sticker, query, parent, z, i);
                        }
                    });
                } else if (((Integer) this.val$actions.get(which)).intValue() == 4) {
                    MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(0, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), true);
                } else if (((Integer) this.val$actions.get(which)).intValue() == 5) {
                    ContentPreviewViewer.this.delegate.remove(ContentPreviewViewer.this.importingSticker);
                }
                if (ContentPreviewViewer.this.popupWindow != null) {
                    ContentPreviewViewer.this.popupWindow.dismiss();
                }
            }
        }

        /* renamed from: lambda$run$1$org-telegram-ui-ContentPreviewViewer$1 */
        public /* synthetic */ void m3295lambda$run$1$orgtelegramuiContentPreviewViewer$1(ArrayList actions, View v) {
            if (ContentPreviewViewer.this.parentActivity == null) {
                return;
            }
            int which = ((Integer) v.getTag()).intValue();
            if (((Integer) actions.get(which)).intValue() == 0) {
                ContentPreviewViewer.this.delegate.sendGif(ContentPreviewViewer.this.currentDocument != null ? ContentPreviewViewer.this.currentDocument : ContentPreviewViewer.this.inlineResult, ContentPreviewViewer.this.parentObject, true, 0);
            } else if (((Integer) actions.get(which)).intValue() == 1) {
                MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).removeRecentGif(ContentPreviewViewer.this.currentDocument);
                ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
            } else if (((Integer) actions.get(which)).intValue() == 2) {
                MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentGif(ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), true);
                MessagesController.getInstance(ContentPreviewViewer.this.currentAccount).saveGif("gif", ContentPreviewViewer.this.currentDocument);
                ContentPreviewViewer.this.delegate.gifAddedOrDeleted();
            } else if (((Integer) actions.get(which)).intValue() == 3) {
                final TLRPC.Document document = ContentPreviewViewer.this.currentDocument;
                final TLRPC.BotInlineResult result = ContentPreviewViewer.this.inlineResult;
                final Object parent = ContentPreviewViewer.this.parentObject;
                final ContentPreviewViewerDelegate stickerPreviewViewerDelegate = ContentPreviewViewer.this.delegate;
                AlertsCreator.createScheduleDatePickerDialog(ContentPreviewViewer.this.parentActivity, stickerPreviewViewerDelegate.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.ContentPreviewViewer$1$$ExternalSyntheticLambda2
                    @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                    public final void didSelectDate(boolean z, int i) {
                        ContentPreviewViewer.ContentPreviewViewerDelegate.this.sendGif(document != null ? document : result, parent, z, i);
                    }
                }, ContentPreviewViewer.this.resourcesProvider);
            }
            if (ContentPreviewViewer.this.popupWindow != null) {
                ContentPreviewViewer.this.popupWindow.dismiss();
            }
        }

        /* renamed from: lambda$run$2$org-telegram-ui-ContentPreviewViewer$1 */
        public /* synthetic */ void m3296lambda$run$2$orgtelegramuiContentPreviewViewer$1(ValueAnimator animation) {
            ContentPreviewViewer.this.currentMoveYProgress = ((Float) animation.getAnimatedValue()).floatValue();
            ContentPreviewViewer contentPreviewViewer = ContentPreviewViewer.this;
            contentPreviewViewer.moveY = contentPreviewViewer.startMoveY + ((ContentPreviewViewer.this.finalMoveY - ContentPreviewViewer.this.startMoveY) * ContentPreviewViewer.this.currentMoveYProgress);
            ContentPreviewViewer.this.containerView.invalidate();
        }
    }

    public void showUnlockPremiumView() {
        if (this.unlockPremiumView == null) {
            UnlockPremiumView unlockPremiumView = new UnlockPremiumView(this.containerView.getContext(), 0, this.resourcesProvider);
            this.unlockPremiumView = unlockPremiumView;
            this.containerView.addView(unlockPremiumView, LayoutHelper.createFrame(-1, -1.0f));
            this.unlockPremiumView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ContentPreviewViewer.this.m3293x12b27cf1(view);
                }
            });
            this.unlockPremiumView.premiumButtonView.buttonLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ContentPreviewViewer.this.m3294x74051990(view);
                }
            });
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.unlockPremiumView, false, 1.0f, false);
        AndroidUtilities.updateViewVisibilityAnimated(this.unlockPremiumView, true);
        this.unlockPremiumView.setTranslationY(0.0f);
    }

    /* renamed from: lambda$showUnlockPremiumView$0$org-telegram-ui-ContentPreviewViewer */
    public /* synthetic */ void m3293x12b27cf1(View v) {
        this.menuVisible = false;
        this.containerView.invalidate();
        close();
    }

    /* renamed from: lambda$showUnlockPremiumView$1$org-telegram-ui-ContentPreviewViewer */
    public /* synthetic */ void m3294x74051990(View v) {
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            LaunchActivity activity2 = (LaunchActivity) activity;
            if (activity2.getActionBarLayout() != null && activity2.getActionBarLayout().getLastFragment() != null) {
                activity2.getActionBarLayout().getLastFragment().dismissCurrentDialog();
            }
            activity2.m3653lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new PremiumPreviewFragment(PremiumPreviewFragment.featureTypeToServerString(5)));
        }
        this.menuVisible = false;
        this.containerView.invalidate();
        close();
    }

    public static ContentPreviewViewer getInstance() {
        ContentPreviewViewer localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhotoViewer.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    ContentPreviewViewer contentPreviewViewer = new ContentPreviewViewer();
                    localInstance = contentPreviewViewer;
                    Instance = contentPreviewViewer;
                }
            }
        }
        return localInstance;
    }

    public static boolean hasInstance() {
        return Instance != null;
    }

    public void reset() {
        Runnable runnable = this.openPreviewRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.openPreviewRunnable = null;
        }
        View view = this.currentPreviewCell;
        if (view != null) {
            if (view instanceof StickerEmojiCell) {
                ((StickerEmojiCell) view).setScaled(false);
            } else if (view instanceof StickerCell) {
                ((StickerCell) view).setScaled(false);
            } else if (view instanceof ContextLinkCell) {
                ((ContextLinkCell) view).setScaled(false);
            }
            this.currentPreviewCell = null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v10 */
    /* JADX WARN: Type inference failed for: r1v9, types: [int, boolean] */
    public boolean onTouch(MotionEvent event, final RecyclerListView listView, int height, final Object listener, ContentPreviewViewerDelegate contentPreviewViewerDelegate, Theme.ResourcesProvider resourcesProvider) {
        ContextLinkCell view;
        int contentType;
        View view2;
        boolean z;
        boolean z2;
        int left;
        String str;
        this.delegate = contentPreviewViewerDelegate;
        this.resourcesProvider = resourcesProvider;
        ?? r1 = 0;
        if (this.openPreviewRunnable != null || isVisible()) {
            if (event.getAction() == 1 || event.getAction() == 3 || event.getAction() == 6) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        ContentPreviewViewer.lambda$onTouch$2(RecyclerListView.this, listener);
                    }
                }, 150L);
                Runnable runnable = this.openPreviewRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.openPreviewRunnable = null;
                    return false;
                } else if (isVisible()) {
                    close();
                    View view3 = this.currentPreviewCell;
                    if (view3 != null) {
                        if (view3 instanceof StickerEmojiCell) {
                            ((StickerEmojiCell) view3).setScaled(false);
                        } else if (view3 instanceof StickerCell) {
                            ((StickerCell) view3).setScaled(false);
                        } else if (view3 instanceof ContextLinkCell) {
                            ((ContextLinkCell) view3).setScaled(false);
                        }
                        this.currentPreviewCell = null;
                        return false;
                    }
                    return false;
                } else {
                    return false;
                }
            } else if (event.getAction() != 0) {
                if (this.isVisible) {
                    if (event.getAction() == 2) {
                        if (this.currentContentType == 1) {
                            if (!this.menuVisible && this.showProgress == 1.0f) {
                                if (this.lastTouchY == -10000.0f) {
                                    this.lastTouchY = event.getY();
                                    this.currentMoveY = 0.0f;
                                    this.moveY = 0.0f;
                                } else {
                                    float newY = event.getY();
                                    float f = this.currentMoveY + (newY - this.lastTouchY);
                                    this.currentMoveY = f;
                                    this.lastTouchY = newY;
                                    if (f > 0.0f) {
                                        this.currentMoveY = 0.0f;
                                    } else if (f < (-AndroidUtilities.dp(60.0f))) {
                                        this.currentMoveY = -AndroidUtilities.dp(60.0f);
                                    }
                                    this.moveY = rubberYPoisition(this.currentMoveY, AndroidUtilities.dp(200.0f));
                                    this.containerView.invalidate();
                                    if (this.currentMoveY <= (-AndroidUtilities.dp(55.0f))) {
                                        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                                        this.showSheetRunnable.run();
                                        return true;
                                    }
                                }
                            }
                            return true;
                        }
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        int count = listView.getChildCount();
                        int a = 0;
                        while (a < count) {
                            if (!(listView instanceof RecyclerListView)) {
                                view = null;
                            } else {
                                view = listView.getChildAt(a);
                            }
                            if (view == null) {
                                return r1;
                            }
                            int top = view.getTop();
                            int bottom = view.getBottom();
                            int left2 = view.getLeft();
                            int right = view.getRight();
                            if (top <= y && bottom >= y && left2 <= x) {
                                if (right >= x) {
                                    if (view instanceof StickerEmojiCell) {
                                        this.centerImage.setRoundRadius((int) r1);
                                        contentType = 0;
                                    } else if (view instanceof StickerCell) {
                                        this.centerImage.setRoundRadius((int) r1);
                                        contentType = 0;
                                    } else {
                                        if (view instanceof ContextLinkCell) {
                                            ContextLinkCell cell = view;
                                            if (cell.isSticker()) {
                                                ImageReceiver imageReceiver = this.centerImage;
                                                int i = r1 == true ? 1 : 0;
                                                int i2 = r1 == true ? 1 : 0;
                                                imageReceiver.setRoundRadius(i);
                                                contentType = 0;
                                            } else if (cell.isGif()) {
                                                this.centerImage.setRoundRadius(AndroidUtilities.dp(6.0f));
                                                contentType = 1;
                                            }
                                        }
                                        contentType = -1;
                                    }
                                    if (contentType != -1 && view != (view2 = this.currentPreviewCell)) {
                                        if (view2 instanceof StickerEmojiCell) {
                                            z = false;
                                            ((StickerEmojiCell) view2).setScaled(false);
                                        } else if (view2 instanceof StickerCell) {
                                            z = false;
                                            ((StickerCell) view2).setScaled(false);
                                        } else if (!(view2 instanceof ContextLinkCell)) {
                                            z = false;
                                        } else {
                                            z = false;
                                            ((ContextLinkCell) view2).setScaled(false);
                                        }
                                        this.currentPreviewCell = view;
                                        this.clearsInputField = z;
                                        this.menuVisible = z;
                                        this.closeOnDismiss = z;
                                        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
                                        if (actionBarPopupWindow != null) {
                                            actionBarPopupWindow.dismiss();
                                        }
                                        AndroidUtilities.updateViewVisibilityAnimated(this.unlockPremiumView, z);
                                        View view4 = this.currentPreviewCell;
                                        if (view4 instanceof StickerEmojiCell) {
                                            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view4;
                                            TLRPC.Document sticker = stickerEmojiCell.getSticker();
                                            SendMessagesHelper.ImportingSticker stickerPath = stickerEmojiCell.getStickerPath();
                                            String emoji = stickerEmojiCell.getEmoji();
                                            ContentPreviewViewerDelegate contentPreviewViewerDelegate2 = this.delegate;
                                            if (contentPreviewViewerDelegate2 != null) {
                                                left = left2;
                                                str = contentPreviewViewerDelegate2.getQuery(false);
                                            } else {
                                                left = left2;
                                                str = null;
                                            }
                                            open(sticker, stickerPath, emoji, str, null, contentType, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject(), resourcesProvider);
                                            stickerEmojiCell.setScaled(true);
                                            z2 = true;
                                        } else if (view4 instanceof StickerCell) {
                                            StickerCell stickerCell = (StickerCell) view4;
                                            TLRPC.Document sticker2 = stickerCell.getSticker();
                                            ContentPreviewViewerDelegate contentPreviewViewerDelegate3 = this.delegate;
                                            open(sticker2, null, null, contentPreviewViewerDelegate3 != null ? contentPreviewViewerDelegate3.getQuery(false) : null, null, contentType, false, stickerCell.getParentObject(), resourcesProvider);
                                            stickerCell.setScaled(true);
                                            this.clearsInputField = stickerCell.isClearsInputField();
                                            z2 = true;
                                        } else if (!(view4 instanceof ContextLinkCell)) {
                                            z2 = true;
                                        } else {
                                            ContextLinkCell contextLinkCell = (ContextLinkCell) view4;
                                            TLRPC.Document document = contextLinkCell.getDocument();
                                            ContentPreviewViewerDelegate contentPreviewViewerDelegate4 = this.delegate;
                                            open(document, null, null, contentPreviewViewerDelegate4 != null ? contentPreviewViewerDelegate4.getQuery(true) : null, contextLinkCell.getBotInlineResult(), contentType, false, contextLinkCell.getBotInlineResult() != null ? contextLinkCell.getInlineBot() : contextLinkCell.getParentObject(), resourcesProvider);
                                            z2 = true;
                                            if (contentType != 1) {
                                                contextLinkCell.setScaled(true);
                                            }
                                        }
                                        runSmoothHaptic();
                                        return z2;
                                    }
                                    return true;
                                }
                            }
                            a++;
                            count = count;
                            y = y;
                            r1 = 0;
                        }
                        return true;
                    }
                    return true;
                } else if (this.openPreviewRunnable != null) {
                    if (event.getAction() == 2) {
                        if (Math.hypot(this.startX - event.getX(), this.startY - event.getY()) > AndroidUtilities.dp(10.0f)) {
                            AndroidUtilities.cancelRunOnUIThread(this.openPreviewRunnable);
                            this.openPreviewRunnable = null;
                            return false;
                        }
                        return false;
                    }
                    AndroidUtilities.cancelRunOnUIThread(this.openPreviewRunnable);
                    this.openPreviewRunnable = null;
                    return false;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public static /* synthetic */ void lambda$onTouch$2(RecyclerListView listView, Object listener) {
        if (listView instanceof RecyclerListView) {
            listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) listener);
        }
    }

    protected void runSmoothHaptic() {
        if (Build.VERSION.SDK_INT >= 26) {
            Vibrator vibrator = (Vibrator) this.containerView.getContext().getSystemService("vibrator");
            if (this.vibrationEffect == null) {
                long[] vibrationWaveFormDurationPattern = {0, 2};
                this.vibrationEffect = VibrationEffect.createWaveform(vibrationWaveFormDurationPattern, -1);
            }
            vibrator.cancel();
            vibrator.vibrate(this.vibrationEffect);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v0 */
    /* JADX WARN: Type inference failed for: r5v1, types: [int, boolean] */
    /* JADX WARN: Type inference failed for: r5v2 */
    public boolean onInterceptTouchEvent(MotionEvent event, RecyclerListView listView, int height, ContentPreviewViewerDelegate contentPreviewViewerDelegate, final Theme.ResourcesProvider resourcesProvider) {
        ContentPreviewViewer contentPreviewViewer = this;
        final RecyclerListView recyclerListView = listView;
        contentPreviewViewer.delegate = contentPreviewViewerDelegate;
        contentPreviewViewer.resourcesProvider = resourcesProvider;
        ?? r5 = 0;
        if (event.getAction() == 0) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int count = listView.getChildCount();
            int a = 0;
            while (a < count) {
                View view = null;
                if (recyclerListView instanceof RecyclerListView) {
                    view = recyclerListView.getChildAt(a);
                }
                if (view == null) {
                    return r5;
                }
                int top = view.getTop();
                int bottom = view.getBottom();
                int left = view.getLeft();
                int right = view.getRight();
                if (top > y || bottom < y || left > x || right < x) {
                    a++;
                    contentPreviewViewer = this;
                    recyclerListView = listView;
                    r5 = 0;
                } else {
                    int contentType = -1;
                    if (view instanceof StickerEmojiCell) {
                        if (((StickerEmojiCell) view).showingBitmap()) {
                            contentType = 0;
                            contentPreviewViewer.centerImage.setRoundRadius((int) r5);
                        }
                    } else if (view instanceof StickerCell) {
                        if (((StickerCell) view).showingBitmap()) {
                            contentType = 0;
                            contentPreviewViewer.centerImage.setRoundRadius((int) r5);
                        }
                    } else if (view instanceof ContextLinkCell) {
                        ContextLinkCell cell = (ContextLinkCell) view;
                        if (cell.showingBitmap()) {
                            if (cell.isSticker()) {
                                contentType = 0;
                                ImageReceiver imageReceiver = contentPreviewViewer.centerImage;
                                int i = r5 == true ? 1 : 0;
                                int i2 = r5 == true ? 1 : 0;
                                imageReceiver.setRoundRadius(i);
                            } else if (cell.isGif()) {
                                contentType = 1;
                                contentPreviewViewer.centerImage.setRoundRadius(AndroidUtilities.dp(6.0f));
                            }
                        }
                    }
                    if (contentType == -1) {
                        return false;
                    }
                    contentPreviewViewer.startX = x;
                    contentPreviewViewer.startY = y;
                    contentPreviewViewer.currentPreviewCell = view;
                    final int contentTypeFinal = contentType;
                    Runnable runnable = new Runnable() { // from class: org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda6
                        @Override // java.lang.Runnable
                        public final void run() {
                            ContentPreviewViewer.this.m3290x68d28b0b(recyclerListView, contentTypeFinal, resourcesProvider);
                        }
                    };
                    contentPreviewViewer.openPreviewRunnable = runnable;
                    AndroidUtilities.runOnUIThread(runnable, 200L);
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    /* renamed from: lambda$onInterceptTouchEvent$3$org-telegram-ui-ContentPreviewViewer */
    public /* synthetic */ void m3290x68d28b0b(RecyclerListView listView, int contentTypeFinal, Theme.ResourcesProvider resourcesProvider) {
        if (this.openPreviewRunnable == null) {
            return;
        }
        String str = null;
        listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) null);
        listView.requestDisallowInterceptTouchEvent(true);
        this.openPreviewRunnable = null;
        setParentActivity((Activity) listView.getContext());
        this.clearsInputField = false;
        View view = this.currentPreviewCell;
        if (view instanceof StickerEmojiCell) {
            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
            TLRPC.Document sticker = stickerEmojiCell.getSticker();
            SendMessagesHelper.ImportingSticker stickerPath = stickerEmojiCell.getStickerPath();
            String emoji = stickerEmojiCell.getEmoji();
            ContentPreviewViewerDelegate contentPreviewViewerDelegate = this.delegate;
            if (contentPreviewViewerDelegate != null) {
                str = contentPreviewViewerDelegate.getQuery(false);
            }
            open(sticker, stickerPath, emoji, str, null, contentTypeFinal, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject(), resourcesProvider);
            stickerEmojiCell.setScaled(true);
        } else if (view instanceof StickerCell) {
            StickerCell stickerCell = (StickerCell) view;
            TLRPC.Document sticker2 = stickerCell.getSticker();
            ContentPreviewViewerDelegate contentPreviewViewerDelegate2 = this.delegate;
            if (contentPreviewViewerDelegate2 != null) {
                str = contentPreviewViewerDelegate2.getQuery(false);
            }
            open(sticker2, null, null, str, null, contentTypeFinal, false, stickerCell.getParentObject(), resourcesProvider);
            stickerCell.setScaled(true);
            this.clearsInputField = stickerCell.isClearsInputField();
        } else if (view instanceof ContextLinkCell) {
            ContextLinkCell contextLinkCell = (ContextLinkCell) view;
            TLRPC.Document document = contextLinkCell.getDocument();
            ContentPreviewViewerDelegate contentPreviewViewerDelegate3 = this.delegate;
            if (contentPreviewViewerDelegate3 != null) {
                str = contentPreviewViewerDelegate3.getQuery(true);
            }
            open(document, null, null, str, contextLinkCell.getBotInlineResult(), contentTypeFinal, false, contextLinkCell.getBotInlineResult() != null ? contextLinkCell.getInlineBot() : contextLinkCell.getParentObject(), resourcesProvider);
            if (contentTypeFinal != 1) {
                contextLinkCell.setScaled(true);
            }
        }
        this.currentPreviewCell.performHapticFeedback(0, 2);
    }

    public void setDelegate(ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
        this.delegate = contentPreviewViewerDelegate;
    }

    public void setParentActivity(Activity activity) {
        int i = UserConfig.selectedAccount;
        this.currentAccount = i;
        this.centerImage.setCurrentAccount(i);
        this.centerImage.setLayerNum(Integer.MAX_VALUE);
        this.effectImage.setCurrentAccount(this.currentAccount);
        this.effectImage.setLayerNum(Integer.MAX_VALUE);
        if (this.parentActivity == activity) {
            return;
        }
        this.parentActivity = activity;
        this.slideUpDrawable = activity.getResources().getDrawable(R.drawable.preview_arrow);
        FrameLayout frameLayout = new FrameLayout(activity);
        this.windowView = frameLayout;
        frameLayout.setFocusable(true);
        this.windowView.setFocusableInTouchMode(true);
        if (Build.VERSION.SDK_INT >= 21) {
            this.windowView.setFitsSystemWindows(true);
            this.windowView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda0
                @Override // android.view.View.OnApplyWindowInsetsListener
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    return ContentPreviewViewer.this.m3291lambda$setParentActivity$4$orgtelegramuiContentPreviewViewer(view, windowInsets);
                }
            });
        }
        FrameLayoutDrawer frameLayoutDrawer = new FrameLayoutDrawer(activity) { // from class: org.telegram.ui.ContentPreviewViewer.2
            {
                ContentPreviewViewer.this = this;
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                ContentPreviewViewer.this.centerImage.onAttachedToWindow();
                ContentPreviewViewer.this.effectImage.onAttachedToWindow();
            }

            @Override // android.view.ViewGroup, android.view.View
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                ContentPreviewViewer.this.centerImage.onDetachedFromWindow();
                ContentPreviewViewer.this.effectImage.onDetachedFromWindow();
            }
        };
        this.containerView = frameLayoutDrawer;
        frameLayoutDrawer.setFocusable(false);
        this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 51));
        this.containerView.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda3
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return ContentPreviewViewer.this.m3292lambda$setParentActivity$5$orgtelegramuiContentPreviewViewer(view, motionEvent);
            }
        });
        MessagesController.getInstance(this.currentAccount);
        SharedPreferences sharedPreferences = MessagesController.getGlobalEmojiSettings();
        this.keyboardHeight = sharedPreferences.getInt("kbd_height", AndroidUtilities.dp(200.0f));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        this.windowLayoutParams = layoutParams;
        layoutParams.height = -1;
        this.windowLayoutParams.format = -3;
        this.windowLayoutParams.width = -1;
        this.windowLayoutParams.gravity = 48;
        this.windowLayoutParams.type = 99;
        if (Build.VERSION.SDK_INT >= 21) {
            this.windowLayoutParams.flags = -2147417848;
        } else {
            this.windowLayoutParams.flags = 8;
        }
        this.centerImage.setAspectFit(true);
        this.centerImage.setInvalidateAll(true);
        this.centerImage.setParentView(this.containerView);
        this.effectImage.setAspectFit(true);
        this.effectImage.setInvalidateAll(true);
        this.effectImage.setParentView(this.containerView);
    }

    /* renamed from: lambda$setParentActivity$4$org-telegram-ui-ContentPreviewViewer */
    public /* synthetic */ WindowInsets m3291lambda$setParentActivity$4$orgtelegramuiContentPreviewViewer(View v, WindowInsets insets) {
        this.lastInsets = insets;
        return insets;
    }

    /* renamed from: lambda$setParentActivity$5$org-telegram-ui-ContentPreviewViewer */
    public /* synthetic */ boolean m3292lambda$setParentActivity$5$orgtelegramuiContentPreviewViewer(View v, MotionEvent event) {
        if (event.getAction() == 1 || event.getAction() == 6 || event.getAction() == 3) {
            close();
        }
        return true;
    }

    public void setKeyboardHeight(int height) {
        this.keyboardHeight = height;
    }

    public void open(TLRPC.Document document, SendMessagesHelper.ImportingSticker sticker, String emojiPath, String query, TLRPC.BotInlineResult botInlineResult, int contentType, boolean isRecent, Object parent, Theme.ResourcesProvider resourcesProvider) {
        ContentPreviewViewerDelegate contentPreviewViewerDelegate;
        if (this.parentActivity != null && this.windowView != null) {
            this.resourcesProvider = resourcesProvider;
            this.isRecentSticker = isRecent;
            String str = null;
            this.stickerEmojiLayout = null;
            this.backgroundDrawable.setColor(Theme.getActiveTheme().isDark() ? 1895825408 : 1692853990);
            this.drawEffect = false;
            if (contentType == 0) {
                if (document == null && sticker == null) {
                    return;
                }
                if (textPaint == null) {
                    TextPaint textPaint2 = new TextPaint(1);
                    textPaint = textPaint2;
                    textPaint2.setTextSize(AndroidUtilities.dp(24.0f));
                }
                this.effectImage.clearImage();
                this.drawEffect = false;
                if (document != null) {
                    TLRPC.InputStickerSet newSet = null;
                    int a = 0;
                    while (true) {
                        if (a >= document.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                        if (!(attribute instanceof TLRPC.TL_documentAttributeSticker) || attribute.stickerset == null) {
                            a++;
                        } else {
                            newSet = attribute.stickerset;
                            break;
                        }
                    }
                    if (newSet != null && ((contentPreviewViewerDelegate = this.delegate) == null || contentPreviewViewerDelegate.needMenu())) {
                        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 1300L);
                    }
                    this.currentStickerSet = newSet;
                    TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                    if (MessageObject.isVideoStickerDocument(document)) {
                        this.centerImage.setImage(ImageLocation.getForDocument(document), null, ImageLocation.getForDocument(thumb, document), null, null, 0L, "webp", this.currentStickerSet, 1);
                    } else {
                        this.centerImage.setImage(ImageLocation.getForDocument(document), (String) null, ImageLocation.getForDocument(thumb, document), (String) null, "webp", this.currentStickerSet, 1);
                        if (MessageObject.isPremiumSticker(document)) {
                            this.drawEffect = true;
                            this.effectImage.setImage(ImageLocation.getForDocument(MessageObject.getPremiumStickerAnimation(document), document), (String) null, (ImageLocation) null, (String) null, "tgs", this.currentStickerSet, 1);
                        }
                    }
                    int a2 = 0;
                    while (true) {
                        if (a2 >= document.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute attribute2 = document.attributes.get(a2);
                        if (!(attribute2 instanceof TLRPC.TL_documentAttributeSticker) || TextUtils.isEmpty(attribute2.alt)) {
                            a2++;
                        } else {
                            CharSequence emoji = Emoji.replaceEmoji(attribute2.alt, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false);
                            this.stickerEmojiLayout = new StaticLayout(emoji, textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                            break;
                        }
                    }
                } else if (sticker != null) {
                    ImageReceiver imageReceiver = this.centerImage;
                    String str2 = sticker.path;
                    if (sticker.animated) {
                        str = "tgs";
                    }
                    imageReceiver.setImage(str2, null, null, str, 0L);
                    if (emojiPath != null) {
                        CharSequence emoji2 = Emoji.replaceEmoji(emojiPath, textPaint.getFontMetricsInt(), AndroidUtilities.dp(24.0f), false);
                        this.stickerEmojiLayout = new StaticLayout(emoji2, textPaint, AndroidUtilities.dp(100.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                    }
                    if (this.delegate.needMenu()) {
                        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 1300L);
                    }
                }
            } else {
                if (document != null) {
                    TLRPC.PhotoSize thumb2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                    TLRPC.VideoSize videoSize = MessageObject.getDocumentVideoThumb(document);
                    ImageLocation location = ImageLocation.getForDocument(document);
                    location.imageType = 2;
                    if (videoSize != null) {
                        this.centerImage.setImage(location, null, ImageLocation.getForDocument(videoSize, document), null, ImageLocation.getForDocument(thumb2, document), "90_90_b", null, document.size, null, "gif" + document, 0);
                    } else {
                        this.centerImage.setImage(location, null, ImageLocation.getForDocument(thumb2, document), "90_90_b", document.size, null, "gif" + document, 0);
                    }
                } else if (botInlineResult == null || botInlineResult.content == null) {
                    return;
                } else {
                    if (!(botInlineResult.thumb instanceof TLRPC.TL_webDocument) || !MimeTypes.VIDEO_MP4.equals(botInlineResult.thumb.mime_type)) {
                        this.centerImage.setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult.content)), null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult.thumb)), "90_90_b", botInlineResult.content.size, null, "gif" + botInlineResult, 1);
                    } else {
                        this.centerImage.setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult.content)), null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult.thumb)), null, ImageLocation.getForWebFile(WebFile.createWithWebDocument(botInlineResult.thumb)), "90_90_b", null, botInlineResult.content.size, null, "gif" + botInlineResult, 1);
                    }
                }
                AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
                AndroidUtilities.runOnUIThread(this.showSheetRunnable, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
            }
            if (this.centerImage.getLottieAnimation() != null) {
                this.centerImage.getLottieAnimation().setCurrentFrame(0);
            }
            if (this.drawEffect && this.effectImage.getLottieAnimation() != null) {
                this.effectImage.getLottieAnimation().setCurrentFrame(0);
            }
            this.currentContentType = contentType;
            this.currentDocument = document;
            this.importingSticker = sticker;
            this.currentQuery = query;
            this.inlineResult = botInlineResult;
            this.parentObject = parent;
            this.resourcesProvider = resourcesProvider;
            this.containerView.invalidate();
            if (!this.isVisible) {
                AndroidUtilities.lockOrientation(this.parentActivity);
                try {
                    if (this.windowView.getParent() != null) {
                        WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
                        wm.removeView(this.windowView);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                WindowManager wm2 = (WindowManager) this.parentActivity.getSystemService("window");
                wm2.addView(this.windowView, this.windowLayoutParams);
                this.isVisible = true;
                this.showProgress = 0.0f;
                this.lastTouchY = -10000.0f;
                this.currentMoveYProgress = 0.0f;
                this.finalMoveY = 0.0f;
                this.currentMoveY = 0.0f;
                this.moveY = 0.0f;
                this.lastUpdateTime = System.currentTimeMillis();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 8);
            }
        }
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void closeWithMenu() {
        this.menuVisible = false;
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
            this.popupWindow = null;
        }
        close();
    }

    public void close() {
        if (this.parentActivity == null || this.menuVisible) {
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
        this.showProgress = 1.0f;
        this.lastUpdateTime = System.currentTimeMillis();
        this.containerView.invalidate();
        this.currentDocument = null;
        this.currentStickerSet = null;
        this.currentQuery = null;
        this.delegate = null;
        this.isVisible = false;
        UnlockPremiumView unlockPremiumView = this.unlockPremiumView;
        if (unlockPremiumView != null) {
            unlockPremiumView.animate().alpha(0.0f).translationY(AndroidUtilities.dp(56.0f)).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 8);
    }

    public void destroy() {
        this.isVisible = false;
        this.delegate = null;
        this.currentDocument = null;
        this.currentQuery = null;
        this.currentStickerSet = null;
        if (this.parentActivity == null || this.windowView == null) {
            return;
        }
        Bitmap bitmap = this.blurrBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.blurrBitmap = null;
        }
        this.blurProgress = 0.0f;
        this.menuVisible = false;
        try {
            if (this.windowView.getParent() != null) {
                WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
                wm.removeViewImmediate(this.windowView);
            }
            this.windowView = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
        Instance = null;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 8);
    }

    private float rubberYPoisition(float offset, float factor) {
        float delta = Math.abs(offset);
        float f = 1.0f;
        float f2 = -((1.0f - (1.0f / (((0.55f * delta) / factor) + 1.0f))) * factor);
        if (offset >= 0.0f) {
            f = -1.0f;
        }
        return f2 * f;
    }

    public void onDraw(Canvas canvas) {
        int top;
        int insets;
        int size;
        Drawable drawable;
        WindowInsets windowInsets;
        float f;
        if (this.containerView == null || this.backgroundDrawable == null) {
            return;
        }
        if (this.menuVisible && this.blurrBitmap == null) {
            prepareBlurBitmap();
        }
        if (this.blurrBitmap != null) {
            boolean z = this.menuVisible;
            if (z) {
                float f2 = this.blurProgress;
                if (f2 != 1.0f) {
                    float f3 = f2 + 0.13333334f;
                    this.blurProgress = f3;
                    if (f3 > 1.0f) {
                        this.blurProgress = 1.0f;
                    }
                    this.containerView.invalidate();
                    f = this.blurProgress;
                    if (f != 0.0f && this.blurrBitmap != null) {
                        this.paint.setAlpha((int) (f * 255.0f));
                        canvas.save();
                        canvas.scale(12.0f, 12.0f);
                        canvas.drawBitmap(this.blurrBitmap, 0.0f, 0.0f, this.paint);
                        canvas.restore();
                    }
                }
            }
            if (!z) {
                float f4 = this.blurProgress;
                if (f4 != 0.0f) {
                    float f5 = f4 - 0.13333334f;
                    this.blurProgress = f5;
                    if (f5 < 0.0f) {
                        this.blurProgress = 0.0f;
                    }
                    this.containerView.invalidate();
                }
            }
            f = this.blurProgress;
            if (f != 0.0f) {
                this.paint.setAlpha((int) (f * 255.0f));
                canvas.save();
                canvas.scale(12.0f, 12.0f);
                canvas.drawBitmap(this.blurrBitmap, 0.0f, 0.0f, this.paint);
                canvas.restore();
            }
        }
        this.backgroundDrawable.setAlpha((int) (this.showProgress * 180.0f));
        this.backgroundDrawable.setBounds(0, 0, this.containerView.getWidth(), this.containerView.getHeight());
        this.backgroundDrawable.draw(canvas);
        canvas.save();
        if (Build.VERSION.SDK_INT >= 21 && (windowInsets = this.lastInsets) != null) {
            int insets2 = windowInsets.getStableInsetBottom() + this.lastInsets.getStableInsetTop();
            top = this.lastInsets.getStableInsetTop();
            insets = insets2;
        } else {
            int top2 = AndroidUtilities.statusBarHeight;
            top = top2;
            insets = 0;
        }
        if (this.currentContentType == 1) {
            size = Math.min(this.containerView.getWidth(), this.containerView.getHeight() - insets) - AndroidUtilities.dp(40.0f);
        } else {
            size = this.drawEffect ? (int) (Math.min(this.containerView.getWidth(), this.containerView.getHeight() - insets) - AndroidUtilities.dpf2(40.0f)) : (int) (Math.min(this.containerView.getWidth(), this.containerView.getHeight() - insets) / 1.8f);
        }
        float topOffset = Math.max((size / 2) + top + (this.stickerEmojiLayout != null ? AndroidUtilities.dp(40.0f) : 0), ((this.containerView.getHeight() - insets) - this.keyboardHeight) / 2);
        if (this.drawEffect) {
            topOffset += AndroidUtilities.dp(40.0f);
        }
        canvas.translate(this.containerView.getWidth() / 2, this.moveY + topOffset);
        float f6 = this.showProgress;
        float scale = (f6 * 0.8f) / 0.8f;
        int size2 = (int) (size * scale);
        if (this.drawEffect) {
            float smallImageSize = size2 * 0.6669f;
            float padding = size2 * 0.0546875f;
            this.centerImage.setAlpha(f6);
            this.centerImage.setImageCoords(((size2 - smallImageSize) - (size2 / 2.0f)) - padding, ((size2 - smallImageSize) / 2.0f) - (size2 / 2.0f), smallImageSize, smallImageSize);
            this.centerImage.draw(canvas);
            this.effectImage.setAlpha(this.showProgress);
            this.effectImage.setImageCoords((-size2) / 2.0f, (-size2) / 2.0f, size2, size2);
            this.effectImage.draw(canvas);
        } else {
            this.centerImage.setAlpha(f6);
            this.centerImage.setImageCoords((-size2) / 2.0f, (-size2) / 2.0f, size2, size2);
            this.centerImage.draw(canvas);
        }
        if (this.currentContentType == 1 && (drawable = this.slideUpDrawable) != null) {
            int w = drawable.getIntrinsicWidth();
            int h = this.slideUpDrawable.getIntrinsicHeight();
            int y = (int) (this.centerImage.getDrawRegion().top - AndroidUtilities.dp(((this.currentMoveY / AndroidUtilities.dp(60.0f)) * 6.0f) + 17.0f));
            this.slideUpDrawable.setAlpha((int) ((1.0f - this.currentMoveYProgress) * 255.0f));
            this.slideUpDrawable.setBounds((-w) / 2, (-h) + y, w / 2, y);
            this.slideUpDrawable.draw(canvas);
        }
        if (this.stickerEmojiLayout != null) {
            if (this.drawEffect) {
                canvas.translate(-AndroidUtilities.dp(50.0f), ((-this.effectImage.getImageHeight()) / 2.0f) - AndroidUtilities.dp(30.0f));
            } else {
                canvas.translate(-AndroidUtilities.dp(50.0f), ((-this.centerImage.getImageHeight()) / 2.0f) - AndroidUtilities.dp(30.0f));
            }
            this.stickerEmojiLayout.draw(canvas);
        }
        canvas.restore();
        if (this.isVisible) {
            if (this.showProgress != 1.0f) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                this.lastUpdateTime = newTime;
                this.showProgress += ((float) dt) / 120.0f;
                this.containerView.invalidate();
                if (this.showProgress > 1.0f) {
                    this.showProgress = 1.0f;
                }
            }
        } else if (this.showProgress != 0.0f) {
            long newTime2 = System.currentTimeMillis();
            long dt2 = newTime2 - this.lastUpdateTime;
            this.lastUpdateTime = newTime2;
            this.showProgress -= ((float) dt2) / 120.0f;
            this.containerView.invalidate();
            if (this.showProgress < 0.0f) {
                this.showProgress = 0.0f;
            }
            if (this.showProgress == 0.0f) {
                this.centerImage.setImageBitmap((Drawable) null);
                AndroidUtilities.unlockOrientation(this.parentActivity);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        ContentPreviewViewer.this.m3289lambda$onDraw$6$orgtelegramuiContentPreviewViewer();
                    }
                });
                Bitmap bitmap = this.blurrBitmap;
                if (bitmap != null) {
                    bitmap.recycle();
                    this.blurrBitmap = null;
                }
                AndroidUtilities.updateViewVisibilityAnimated(this.unlockPremiumView, false, 1.0f, false);
                this.blurProgress = 0.0f;
                try {
                    if (this.windowView.getParent() != null) {
                        WindowManager wm = (WindowManager) this.parentActivity.getSystemService("window");
                        wm.removeView(this.windowView);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
    }

    /* renamed from: lambda$onDraw$6$org-telegram-ui-ContentPreviewViewer */
    public /* synthetic */ void m3289lambda$onDraw$6$orgtelegramuiContentPreviewViewer() {
        this.centerImage.setImageBitmap((Bitmap) null);
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    private void prepareBlurBitmap() {
        Activity activity = this.parentActivity;
        if (activity == null) {
            return;
        }
        View parentView = activity.getWindow().getDecorView();
        int w = (int) (parentView.getMeasuredWidth() / 12.0f);
        int h = (int) (parentView.getMeasuredHeight() / 12.0f);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.scale(0.083333336f, 0.083333336f);
        canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        parentView.draw(canvas);
        Activity activity2 = this.parentActivity;
        if ((activity2 instanceof LaunchActivity) && ((LaunchActivity) activity2).getActionBarLayout().getLastFragment().getVisibleDialog() != null) {
            ((LaunchActivity) this.parentActivity).getActionBarLayout().getLastFragment().getVisibleDialog().getWindow().getDecorView().draw(canvas);
        }
        Utilities.stackBlurBitmap(bitmap, Math.max(10, Math.max(w, h) / 180));
        this.blurrBitmap = bitmap;
    }

    public boolean showMenuFor(View view) {
        if (view instanceof StickerEmojiCell) {
            Activity activity = AndroidUtilities.findActivity(view.getContext());
            if (activity == null) {
                return true;
            }
            setParentActivity(activity);
            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
            TLRPC.Document sticker = stickerEmojiCell.getSticker();
            SendMessagesHelper.ImportingSticker stickerPath = stickerEmojiCell.getStickerPath();
            String emoji = stickerEmojiCell.getEmoji();
            ContentPreviewViewerDelegate contentPreviewViewerDelegate = this.delegate;
            open(sticker, stickerPath, emoji, contentPreviewViewerDelegate != null ? contentPreviewViewerDelegate.getQuery(false) : null, null, 0, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject(), this.resourcesProvider);
            AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
            AndroidUtilities.runOnUIThread(this.showSheetRunnable, 16L);
            stickerEmojiCell.setScaled(true);
            return true;
        }
        return false;
    }
}
