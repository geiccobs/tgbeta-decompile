package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.core.util.Preconditions;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.AvatarPreviewer;
import org.telegram.ui.Components.RadialProgress2;
/* loaded from: classes4.dex */
public class AvatarPreviewer {
    private static AvatarPreviewer INSTANCE;
    private Callback callback;
    private Context context;
    private Layout layout;
    private ViewGroup view;
    private boolean visible;
    private WindowManager windowManager;

    /* loaded from: classes4.dex */
    public interface Callback {
        void onMenuClick(MenuItem menuItem);
    }

    public static AvatarPreviewer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AvatarPreviewer();
        }
        return INSTANCE;
    }

    public static boolean hasVisibleInstance() {
        AvatarPreviewer avatarPreviewer = INSTANCE;
        return avatarPreviewer != null && avatarPreviewer.visible;
    }

    public static boolean canPreview(Data data) {
        return (data == null || (data.imageLocation == null && data.thumbImageLocation == null)) ? false : true;
    }

    public void show(ViewGroup parentContainer, Data data, Callback callback) {
        Preconditions.checkNotNull(parentContainer);
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(callback);
        Context context = parentContainer.getContext();
        if (this.view != parentContainer) {
            close();
            this.view = parentContainer;
            this.context = context;
            this.windowManager = (WindowManager) ContextCompat.getSystemService(context, WindowManager.class);
            this.layout = new Layout(context, callback) { // from class: org.telegram.ui.AvatarPreviewer.1
                @Override // org.telegram.ui.AvatarPreviewer.Layout
                protected void onHide() {
                    AvatarPreviewer.this.close();
                }
            };
        }
        this.layout.setData(data);
        if (!this.visible) {
            if (this.layout.getParent() != null) {
                this.windowManager.removeView(this.layout);
            }
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-1, -1, 99, 0, -3);
            if (Build.VERSION.SDK_INT >= 21) {
                layoutParams.flags = -2147286784;
            }
            this.windowManager.addView(this.layout, layoutParams);
            parentContainer.requestDisallowInterceptTouchEvent(true);
            this.visible = true;
        }
    }

    public void close() {
        if (this.visible) {
            this.visible = false;
            if (this.layout.getParent() != null) {
                this.windowManager.removeView(this.layout);
            }
            this.layout.recycle();
            this.layout = null;
            this.view.requestDisallowInterceptTouchEvent(false);
            this.view = null;
            this.context = null;
            this.windowManager = null;
            this.callback = null;
        }
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void onTouchEvent(MotionEvent event) {
        Layout layout = this.layout;
        if (layout != null) {
            layout.onTouchEvent(event);
        }
    }

    /* loaded from: classes4.dex */
    public enum MenuItem {
        OPEN_PROFILE("OpenProfile", R.string.OpenProfile, R.drawable.msg_openprofile),
        OPEN_CHANNEL("OpenChannel2", R.string.OpenChannel2, R.drawable.msg_channel),
        OPEN_GROUP("OpenGroup2", R.string.OpenGroup2, R.drawable.msg_discussion),
        SEND_MESSAGE("SendMessage", R.string.SendMessage, R.drawable.msg_discussion),
        MENTION("Mention", R.string.Mention, R.drawable.msg_mention);
        
        private final int iconResId;
        private final String labelKey;
        private final int labelResId;

        MenuItem(String labelKey, int labelResId, int iconResId) {
            this.labelKey = labelKey;
            this.labelResId = labelResId;
            this.iconResId = iconResId;
        }
    }

    /* loaded from: classes4.dex */
    public static class Data {
        private final String imageFilter;
        private final ImageLocation imageLocation;
        private final InfoLoadTask<?, ?> infoLoadTask;
        private final MenuItem[] menuItems;
        private final Object parentObject;
        private final String thumbImageFilter;
        private final ImageLocation thumbImageLocation;
        private final String videoFileName;
        private final String videoFilter;
        private final ImageLocation videoLocation;

        public static Data of(TLRPC.User user, int classGuid, MenuItem... menuItems) {
            ImageLocation imageLocation = ImageLocation.getForUserOrChat(user, 0);
            ImageLocation thumbImageLocation = ImageLocation.getForUserOrChat(user, 1);
            String thumbFilter = (thumbImageLocation == null || !(thumbImageLocation.photoSize instanceof TLRPC.TL_photoStrippedSize)) ? null : "b";
            return new Data(imageLocation, thumbImageLocation, null, null, thumbFilter, null, null, user, menuItems, new UserInfoLoadTask(user, classGuid));
        }

        public static Data of(TLRPC.UserFull userFull, MenuItem... menuItems) {
            ImageLocation videoLocation;
            String videoFileName;
            ImageLocation imageLocation = ImageLocation.getForUserOrChat(userFull.user, 0);
            ImageLocation thumbImageLocation = ImageLocation.getForUserOrChat(userFull.user, 1);
            String str = null;
            String thumbFilter = (thumbImageLocation == null || !(thumbImageLocation.photoSize instanceof TLRPC.TL_photoStrippedSize)) ? null : "b";
            if (userFull.profile_photo != null && !userFull.profile_photo.video_sizes.isEmpty()) {
                TLRPC.VideoSize videoSize = userFull.profile_photo.video_sizes.get(0);
                ImageLocation videoLocation2 = ImageLocation.getForPhoto(videoSize, userFull.profile_photo);
                videoFileName = FileLoader.getAttachFileName(videoSize);
                videoLocation = videoLocation2;
            } else {
                videoFileName = null;
                videoLocation = null;
            }
            if (videoLocation != null && videoLocation.imageType == 2) {
                str = ImageLoader.AUTOPLAY_FILTER;
            }
            String videoFilter = str;
            return new Data(imageLocation, thumbImageLocation, videoLocation, null, thumbFilter, videoFilter, videoFileName, userFull.user, menuItems, null);
        }

        public static Data of(TLRPC.Chat chat, int classGuid, MenuItem... menuItems) {
            ImageLocation imageLocation = ImageLocation.getForUserOrChat(chat, 0);
            ImageLocation thumbImageLocation = ImageLocation.getForUserOrChat(chat, 1);
            String thumbFilter = (thumbImageLocation == null || !(thumbImageLocation.photoSize instanceof TLRPC.TL_photoStrippedSize)) ? null : "b";
            return new Data(imageLocation, thumbImageLocation, null, null, thumbFilter, null, null, chat, menuItems, new ChatInfoLoadTask(chat, classGuid));
        }

        public static Data of(TLRPC.Chat chat, TLRPC.ChatFull chatFull, MenuItem... menuItems) {
            String videoFileName;
            ImageLocation videoLocation;
            ImageLocation imageLocation = ImageLocation.getForUserOrChat(chat, 0);
            ImageLocation thumbImageLocation = ImageLocation.getForUserOrChat(chat, 1);
            String thumbFilter = (thumbImageLocation == null || !(thumbImageLocation.photoSize instanceof TLRPC.TL_photoStrippedSize)) ? null : "b";
            if (chatFull.chat_photo != null && !chatFull.chat_photo.video_sizes.isEmpty()) {
                TLRPC.VideoSize videoSize = chatFull.chat_photo.video_sizes.get(0);
                ImageLocation videoLocation2 = ImageLocation.getForPhoto(videoSize, chatFull.chat_photo);
                String videoFileName2 = FileLoader.getAttachFileName(videoSize);
                videoFileName = videoFileName2;
                videoLocation = videoLocation2;
            } else {
                videoFileName = null;
                videoLocation = null;
            }
            String videoFilter = (videoLocation == null || videoLocation.imageType != 2) ? null : ImageLoader.AUTOPLAY_FILTER;
            return new Data(imageLocation, thumbImageLocation, videoLocation, null, thumbFilter, videoFilter, videoFileName, chat, menuItems, null);
        }

        private Data(ImageLocation imageLocation, ImageLocation thumbImageLocation, ImageLocation videoLocation, String imageFilter, String thumbImageFilter, String videoFilter, String videoFileName, Object parentObject, MenuItem[] menuItems, InfoLoadTask<?, ?> infoLoadTask) {
            this.imageLocation = imageLocation;
            this.thumbImageLocation = thumbImageLocation;
            this.videoLocation = videoLocation;
            this.imageFilter = imageFilter;
            this.thumbImageFilter = thumbImageFilter;
            this.videoFilter = videoFilter;
            this.videoFileName = videoFileName;
            this.parentObject = parentObject;
            this.menuItems = menuItems;
            this.infoLoadTask = infoLoadTask;
        }
    }

    /* loaded from: classes4.dex */
    public static class UserInfoLoadTask extends InfoLoadTask<TLRPC.User, TLRPC.UserFull> {
        public UserInfoLoadTask(TLRPC.User argument, int classGuid) {
            super(argument, classGuid, NotificationCenter.userInfoDidLoad);
        }

        @Override // org.telegram.ui.AvatarPreviewer.InfoLoadTask
        protected void load() {
            MessagesController.getInstance(UserConfig.selectedAccount).loadUserInfo((TLRPC.User) this.argument, false, this.classGuid);
        }

        @Override // org.telegram.ui.AvatarPreviewer.InfoLoadTask
        protected void onReceiveNotification(Object... args) {
            Long uid = (Long) args[0];
            if (uid.longValue() == ((TLRPC.User) this.argument).id) {
                onResult((TLRPC.UserFull) args[1]);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class ChatInfoLoadTask extends InfoLoadTask<TLRPC.Chat, TLRPC.ChatFull> {
        public ChatInfoLoadTask(TLRPC.Chat argument, int classGuid) {
            super(argument, classGuid, NotificationCenter.chatInfoDidLoad);
        }

        @Override // org.telegram.ui.AvatarPreviewer.InfoLoadTask
        protected void load() {
            MessagesController.getInstance(UserConfig.selectedAccount).loadFullChat(((TLRPC.Chat) this.argument).id, this.classGuid, false);
        }

        @Override // org.telegram.ui.AvatarPreviewer.InfoLoadTask
        protected void onReceiveNotification(Object... args) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) args[0];
            if (chatFull != null && chatFull.id == ((TLRPC.Chat) this.argument).id) {
                onResult(chatFull);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class InfoLoadTask<A, B> {
        protected final A argument;
        protected final int classGuid;
        private boolean loading;
        private final int notificationId;
        private Consumer<B> onResult;
        private final NotificationCenter.NotificationCenterDelegate observer = new NotificationCenter.NotificationCenterDelegate() { // from class: org.telegram.ui.AvatarPreviewer.InfoLoadTask.1
            @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
            public void didReceivedNotification(int id, int account, Object... args) {
                if (InfoLoadTask.this.loading && id == InfoLoadTask.this.notificationId) {
                    InfoLoadTask.this.onReceiveNotification(args);
                }
            }
        };
        private final NotificationCenter notificationCenter = NotificationCenter.getInstance(UserConfig.selectedAccount);

        protected abstract void load();

        protected abstract void onReceiveNotification(Object... objArr);

        public InfoLoadTask(A argument, int classGuid, int notificationId) {
            this.argument = argument;
            this.classGuid = classGuid;
            this.notificationId = notificationId;
        }

        public final void load(Consumer<B> onResult) {
            if (!this.loading) {
                this.loading = true;
                this.onResult = onResult;
                this.notificationCenter.addObserver(this.observer, this.notificationId);
                load();
            }
        }

        public final void cancel() {
            if (this.loading) {
                this.loading = false;
                this.notificationCenter.removeObserver(this.observer, this.notificationId);
            }
        }

        protected final void onResult(B result) {
            if (this.loading) {
                cancel();
                this.onResult.accept(result);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Layout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
        private static final float ANIM_DURATION = 150.0f;
        private final Drawable arrowDrawable;
        private final Callback callback;
        private final ImageReceiver imageReceiver;
        private InfoLoadTask<?, ?> infoLoadTask;
        private WindowInsets insets;
        private long lastUpdateTime;
        private MenuItem[] menuItems;
        private ValueAnimator moveAnimator;
        private float moveProgress;
        private float progress;
        private ValueAnimator progressHideAnimator;
        private ValueAnimator progressShowAnimator;
        private final RadialProgress2 radialProgress;
        private boolean recycled;
        private boolean showProgress;
        private boolean showing;
        private String videoFileName;
        private BottomSheet visibleSheet;
        private final int radialProgressSize = AndroidUtilities.dp(64.0f);
        private final int[] coords = new int[2];
        private final Rect rect = new Rect();
        private final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        private final ColorDrawable backgroundDrawable = new ColorDrawable(1895825408);
        private float downY = -1.0f;

        protected abstract void onHide();

        public Layout(Context context, Callback callback) {
            super(context);
            ImageReceiver imageReceiver = new ImageReceiver();
            this.imageReceiver = imageReceiver;
            this.callback = callback;
            setWillNotDraw(false);
            setFitsSystemWindows(true);
            imageReceiver.setAspectFit(true);
            imageReceiver.setInvalidateAll(true);
            imageReceiver.setRoundRadius(AndroidUtilities.dp(6.0f));
            imageReceiver.setParentView(this);
            RadialProgress2 radialProgress2 = new RadialProgress2(this);
            this.radialProgress = radialProgress2;
            radialProgress2.setOverrideAlpha(0.0f);
            radialProgress2.setIcon(10, false, false);
            radialProgress2.setColors(1107296256, 1107296256, -1, -1);
            this.arrowDrawable = ContextCompat.getDrawable(context, R.drawable.preview_arrow);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageReceiver.onAttachedToWindow();
            NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageReceiver.onDetachedFromWindow();
            NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        }

        @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
        public void didReceivedNotification(int id, int account, Object... args) {
            if (!this.showProgress || TextUtils.isEmpty(this.videoFileName)) {
                return;
            }
            if (id == NotificationCenter.fileLoaded) {
                String fileName = (String) args[0];
                if (TextUtils.equals(fileName, this.videoFileName)) {
                    this.radialProgress.setProgress(1.0f, true);
                }
            } else if (id == NotificationCenter.fileLoadProgressChanged) {
                String fileName2 = (String) args[0];
                if (TextUtils.equals(fileName2, this.videoFileName) && this.radialProgress != null) {
                    Long loadedSize = (Long) args[1];
                    Long totalSize = (Long) args[2];
                    float progress = Math.min(1.0f, ((float) loadedSize.longValue()) / ((float) totalSize.longValue()));
                    this.radialProgress.setProgress(progress, true);
                }
            }
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (!this.showing) {
                return false;
            }
            if (this.moveAnimator == null) {
                if (event.getActionMasked() == 1) {
                    this.downY = -1.0f;
                    setShowing(false);
                } else if (event.getActionMasked() == 2) {
                    if (this.downY < 0.0f) {
                        this.downY = event.getY();
                    } else {
                        float max = Math.max(-1.0f, Math.min(0.0f, (event.getY() - this.downY) / AndroidUtilities.dp(56.0f)));
                        this.moveProgress = max;
                        if (max == -1.0f) {
                            performHapticFeedback(0);
                            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.moveProgress, 0.0f);
                            this.moveAnimator = ofFloat;
                            ofFloat.setDuration(200L);
                            this.moveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.AvatarPreviewer$Layout$$ExternalSyntheticLambda2
                                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    AvatarPreviewer.Layout.this.m1567lambda$onTouchEvent$0$orgtelegramuiAvatarPreviewer$Layout(valueAnimator);
                                }
                            });
                            this.moveAnimator.start();
                            showBottomSheet();
                        }
                        invalidate();
                    }
                }
            }
            return true;
        }

        /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-AvatarPreviewer$Layout */
        public /* synthetic */ void m1567lambda$onTouchEvent$0$orgtelegramuiAvatarPreviewer$Layout(ValueAnimator a) {
            this.moveProgress = ((Float) a.getAnimatedValue()).floatValue();
            invalidate();
        }

        private void showBottomSheet() {
            MenuItem[] menuItemArr = this.menuItems;
            CharSequence[] labels = new CharSequence[menuItemArr.length];
            int[] icons = new int[menuItemArr.length];
            int i = 0;
            while (true) {
                MenuItem[] menuItemArr2 = this.menuItems;
                if (i < menuItemArr2.length) {
                    labels[i] = LocaleController.getString(menuItemArr2[i].labelKey, this.menuItems[i].labelResId);
                    icons[i] = this.menuItems[i].iconResId;
                    i++;
                } else {
                    BottomSheet dimBehind = new BottomSheet.Builder(getContext()).setItems(labels, icons, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.AvatarPreviewer$Layout$$ExternalSyntheticLambda3
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i2) {
                            AvatarPreviewer.Layout.this.m1569lambda$showBottomSheet$1$orgtelegramuiAvatarPreviewer$Layout(dialogInterface, i2);
                        }
                    }).setDimBehind(false);
                    this.visibleSheet = dimBehind;
                    dimBehind.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.AvatarPreviewer$Layout$$ExternalSyntheticLambda4
                        @Override // android.content.DialogInterface.OnDismissListener
                        public final void onDismiss(DialogInterface dialogInterface) {
                            AvatarPreviewer.Layout.this.m1570lambda$showBottomSheet$2$orgtelegramuiAvatarPreviewer$Layout(dialogInterface);
                        }
                    });
                    this.visibleSheet.show();
                    return;
                }
            }
        }

        /* renamed from: lambda$showBottomSheet$1$org-telegram-ui-AvatarPreviewer$Layout */
        public /* synthetic */ void m1569lambda$showBottomSheet$1$orgtelegramuiAvatarPreviewer$Layout(DialogInterface dialog, int which) {
            this.callback.onMenuClick(this.menuItems[which]);
            setShowing(false);
        }

        /* renamed from: lambda$showBottomSheet$2$org-telegram-ui-AvatarPreviewer$Layout */
        public /* synthetic */ void m1570lambda$showBottomSheet$2$orgtelegramuiAvatarPreviewer$Layout(DialogInterface dialog) {
            this.visibleSheet = null;
            setShowing(false);
        }

        @Override // android.view.View
        public WindowInsets onApplyWindowInsets(WindowInsets insets) {
            this.insets = insets;
            invalidateSize();
            return insets.consumeStableInsets();
        }

        @Override // android.view.View
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            invalidateSize();
        }

        public void invalidateSize() {
            int width = getWidth();
            int height = getHeight();
            if (width != 0 && height != 0) {
                this.backgroundDrawable.setBounds(0, 0, width, height);
                int padding = AndroidUtilities.dp(8.0f);
                int lPadding = padding;
                int rPadding = padding;
                int vPadding = padding;
                if (Build.VERSION.SDK_INT >= 21) {
                    lPadding += this.insets.getStableInsetLeft();
                    rPadding += this.insets.getStableInsetRight();
                    vPadding += Math.max(this.insets.getStableInsetTop(), this.insets.getStableInsetBottom());
                }
                int arrowWidth = this.arrowDrawable.getIntrinsicWidth();
                int arrowHeight = this.arrowDrawable.getIntrinsicHeight();
                int arrowPadding = AndroidUtilities.dp(24.0f);
                int w = width - (lPadding + rPadding);
                int h = height - (vPadding * 2);
                int size = Math.min(w, h);
                int vOffset = (arrowHeight / 2) + arrowPadding;
                int x = ((w - size) / 2) + lPadding;
                int y = ((h - size) / 2) + vPadding + (w > h ? vOffset : 0);
                this.imageReceiver.setImageCoords(x, y, size, size - (w > h ? vOffset : 0));
                int cx = (int) this.imageReceiver.getCenterX();
                int cy = (int) this.imageReceiver.getCenterY();
                RadialProgress2 radialProgress2 = this.radialProgress;
                int i = this.radialProgressSize;
                radialProgress2.setProgressRect(cx - (i / 2), cy - (i / 2), cx + (i / 2), (i / 2) + cy);
                int arrowX = (size / 2) + x;
                int arrowY = y - arrowPadding;
                this.arrowDrawable.setBounds(arrowX - (arrowWidth / 2), arrowY - (arrowHeight / 2), arrowX + (arrowWidth / 2), arrowY + (arrowHeight / 2));
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:19:0x0062  */
        /* JADX WARN: Removed duplicated region for block: B:22:0x0082  */
        /* JADX WARN: Removed duplicated region for block: B:23:0x0085  */
        /* JADX WARN: Removed duplicated region for block: B:26:0x008a  */
        /* JADX WARN: Removed duplicated region for block: B:27:0x0091  */
        /* JADX WARN: Removed duplicated region for block: B:30:0x00ca  */
        /* JADX WARN: Removed duplicated region for block: B:31:0x00d7  */
        /* JADX WARN: Removed duplicated region for block: B:34:0x00f2  */
        /* JADX WARN: Removed duplicated region for block: B:54:0x01b9  */
        /* JADX WARN: Removed duplicated region for block: B:57:0x01c1  */
        /* JADX WARN: Removed duplicated region for block: B:58:0x01d2  */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onDraw(android.graphics.Canvas r19) {
            /*
                Method dump skipped, instructions count: 488
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.AvatarPreviewer.Layout.onDraw(android.graphics.Canvas):void");
        }

        /* renamed from: lambda$onDraw$3$org-telegram-ui-AvatarPreviewer$Layout */
        public /* synthetic */ void m1565lambda$onDraw$3$orgtelegramuiAvatarPreviewer$Layout(ValueAnimator a) {
            invalidate();
        }

        /* renamed from: lambda$onDraw$4$org-telegram-ui-AvatarPreviewer$Layout */
        public /* synthetic */ void m1566lambda$onDraw$4$orgtelegramuiAvatarPreviewer$Layout(ValueAnimator a) {
            invalidate();
        }

        public void setData(final Data data) {
            this.menuItems = data.menuItems;
            this.showProgress = data.videoLocation != null;
            this.videoFileName = data.videoFileName;
            recycleInfoLoadTask();
            if (data.infoLoadTask != null) {
                InfoLoadTask<?, ?> infoLoadTask = data.infoLoadTask;
                this.infoLoadTask = infoLoadTask;
                infoLoadTask.load(new Consumer() { // from class: org.telegram.ui.AvatarPreviewer$Layout$$ExternalSyntheticLambda5
                    @Override // androidx.core.util.Consumer
                    public final void accept(Object obj) {
                        AvatarPreviewer.Layout.this.m1568lambda$setData$5$orgtelegramuiAvatarPreviewer$Layout(data, obj);
                    }
                });
            }
            this.imageReceiver.setCurrentAccount(UserConfig.selectedAccount);
            this.imageReceiver.setImage(data.videoLocation, data.videoFilter, data.imageLocation, data.imageFilter, data.thumbImageLocation, data.thumbImageFilter, null, 0L, null, data.parentObject, 1);
            setShowing(true);
        }

        /* renamed from: lambda$setData$5$org-telegram-ui-AvatarPreviewer$Layout */
        public /* synthetic */ void m1568lambda$setData$5$orgtelegramuiAvatarPreviewer$Layout(Data data, Object result) {
            if (!this.recycled) {
                if (result instanceof TLRPC.UserFull) {
                    setData(Data.of((TLRPC.UserFull) result, data.menuItems));
                } else if (result instanceof TLRPC.ChatFull) {
                    setData(Data.of((TLRPC.Chat) data.infoLoadTask.argument, (TLRPC.ChatFull) result, data.menuItems));
                }
            }
        }

        private void setShowing(boolean showing) {
            if (this.showing != showing) {
                this.showing = showing;
                this.lastUpdateTime = AnimationUtils.currentAnimationTimeMillis();
                invalidate();
            }
        }

        public void recycle() {
            this.recycled = true;
            ValueAnimator valueAnimator = this.moveAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            BottomSheet bottomSheet = this.visibleSheet;
            if (bottomSheet != null) {
                bottomSheet.cancel();
            }
            recycleInfoLoadTask();
        }

        private void recycleInfoLoadTask() {
            InfoLoadTask<?, ?> infoLoadTask = this.infoLoadTask;
            if (infoLoadTask != null) {
                infoLoadTask.cancel();
                this.infoLoadTask = null;
            }
        }
    }
}
