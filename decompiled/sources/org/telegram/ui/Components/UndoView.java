package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.PaymentFormActivity;
/* loaded from: classes5.dex */
public class UndoView extends FrameLayout {
    public static final int ACTION_ADDED_TO_FOLDER = 20;
    public static final int ACTION_ARCHIVE = 2;
    public static final int ACTION_ARCHIVE_FEW = 4;
    public static final int ACTION_ARCHIVE_FEW_HINT = 5;
    public static final int ACTION_ARCHIVE_HIDDEN = 6;
    public static final int ACTION_ARCHIVE_HINT = 3;
    public static final int ACTION_ARCHIVE_PINNED = 7;
    public static final int ACTION_AUTO_DELETE_OFF = 71;
    public static final int ACTION_AUTO_DELETE_ON = 70;
    public static final int ACTION_CACHE_WAS_CLEARED = 19;
    public static final int ACTION_CHAT_UNARCHIVED = 23;
    public static final int ACTION_CLEAR = 0;
    public static final int ACTION_CLEAR_DATES = 81;
    public static final int ACTION_CLEAR_FEW = 26;
    public static final int ACTION_CONTACT_ADDED = 8;
    public static final int ACTION_DELETE = 1;
    public static final int ACTION_DELETE_FEW = 27;
    public static final int ACTION_DICE_INFO = 16;
    public static final int ACTION_DICE_NO_SEND_INFO = 17;
    public static final int ACTION_EMAIL_COPIED = 80;
    public static final int ACTION_FILTERS_AVAILABLE = 15;
    public static final int ACTION_FWD_MESSAGES = 53;
    public static final int ACTION_GIGAGROUP_CANCEL = 75;
    public static final int ACTION_GIGAGROUP_SUCCESS = 76;
    public static final int ACTION_HASHTAG_COPIED = 57;
    public static final int ACTION_IMPORT_GROUP_NOT_ADMIN = 46;
    public static final int ACTION_IMPORT_INFO = 47;
    public static final int ACTION_IMPORT_NOT_MUTUAL = 45;
    public static final int ACTION_LINK_COPIED = 59;
    public static final int ACTION_MESSAGE_COPIED = 52;
    public static final int ACTION_NOTIFY_OFF = 55;
    public static final int ACTION_NOTIFY_ON = 54;
    public static final int ACTION_OWNER_TRANSFERED_CHANNEL = 9;
    public static final int ACTION_OWNER_TRANSFERED_GROUP = 10;
    public static final int ACTION_PAYMENT_SUCCESS = 77;
    public static final int ACTION_PHONE_COPIED = 60;
    public static final int ACTION_PIN_DIALOGS = 78;
    public static final int ACTION_PLAYBACK_SPEED_DISABLED = 51;
    public static final int ACTION_PLAYBACK_SPEED_ENABLED = 50;
    public static final int ACTION_PREVIEW_MEDIA_DESELECTED = 82;
    public static final int ACTION_PROFILE_PHOTO_CHANGED = 22;
    public static final int ACTION_PROXIMITY_REMOVED = 25;
    public static final int ACTION_PROXIMITY_SET = 24;
    public static final int ACTION_QR_SESSION_ACCEPTED = 11;
    public static final int ACTION_QUIZ_CORRECT = 13;
    public static final int ACTION_QUIZ_INCORRECT = 14;
    public static final int ACTION_REMOVED_FROM_FOLDER = 21;
    public static final int ACTION_REPORT_SENT = 74;
    public static int ACTION_RINGTONE_ADDED = 83;
    public static final int ACTION_SHARE_BACKGROUND = 61;
    public static final int ACTION_TEXT_COPIED = 58;
    public static final int ACTION_TEXT_INFO = 18;
    public static final int ACTION_THEME_CHANGED = 12;
    public static final int ACTION_UNPIN_DIALOGS = 79;
    public static final int ACTION_USERNAME_COPIED = 56;
    public static final int ACTION_VOIP_CAN_NOW_SPEAK = 38;
    public static final int ACTION_VOIP_INVITED = 34;
    public static final int ACTION_VOIP_INVITE_LINK_SENT = 41;
    public static final int ACTION_VOIP_LINK_COPIED = 33;
    public static final int ACTION_VOIP_MUTED = 30;
    public static final int ACTION_VOIP_MUTED_FOR_YOU = 35;
    public static final int ACTION_VOIP_RECORDING_FINISHED = 40;
    public static final int ACTION_VOIP_RECORDING_STARTED = 39;
    public static final int ACTION_VOIP_REMOVED = 32;
    public static final int ACTION_VOIP_SOUND_MUTED = 42;
    public static final int ACTION_VOIP_SOUND_UNMUTED = 43;
    public static final int ACTION_VOIP_UNMUTED = 31;
    public static final int ACTION_VOIP_UNMUTED_FOR_YOU = 36;
    public static final int ACTION_VOIP_USER_CHANGED = 37;
    public static final int ACTION_VOIP_USER_JOINED = 44;
    public static final int ACTION_VOIP_VIDEO_RECORDING_FINISHED = 101;
    public static final int ACTION_VOIP_VIDEO_RECORDING_STARTED = 100;
    private float additionalTranslationY;
    private BackupImageView avatarImageView;
    Drawable backgroundDrawable;
    private int currentAccount;
    private int currentAction;
    private Runnable currentActionRunnable;
    private Runnable currentCancelRunnable;
    private ArrayList<Long> currentDialogIds;
    private Object currentInfoObject;
    float enterOffset;
    private int enterOffsetMargin;
    private boolean fromTop;
    private int hideAnimationType;
    private CharSequence infoText;
    private TextView infoTextView;
    private boolean isShown;
    private long lastUpdateTime;
    private RLottieImageView leftImageView;
    private BaseFragment parentFragment;
    private int prevSeconds;
    private Paint progressPaint;
    private RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private TextView subinfoTextView;
    private TextPaint textPaint;
    private int textWidth;
    int textWidthOut;
    StaticLayout timeLayout;
    StaticLayout timeLayoutOut;
    private long timeLeft;
    private String timeLeftString;
    float timeReplaceProgress;
    private LinearLayout undoButton;
    private ImageView undoImageView;
    private TextView undoTextView;
    private int undoViewHeight;

    /* loaded from: classes5.dex */
    public class LinkMovementMethodMy extends LinkMovementMethod {
        public LinkMovementMethodMy() {
            UndoView.this = this$0;
        }

        @Override // android.text.method.LinkMovementMethod, android.text.method.ScrollingMovementMethod, android.text.method.BaseMovementMethod, android.text.method.MovementMethod
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            CharacterStyle[] links;
            try {
                if (event.getAction() == 0 && ((links = (CharacterStyle[]) buffer.getSpans(widget.getSelectionStart(), widget.getSelectionEnd(), CharacterStyle.class)) == null || links.length == 0)) {
                    return false;
                }
                if (event.getAction() == 1) {
                    CharacterStyle[] links2 = (CharacterStyle[]) buffer.getSpans(widget.getSelectionStart(), widget.getSelectionEnd(), CharacterStyle.class);
                    if (links2 != null && links2.length > 0) {
                        UndoView.this.didPressUrl(links2[0]);
                    }
                    Selection.removeSelection(buffer);
                    return true;
                }
                boolean result = super.onTouchEvent(widget, buffer, event);
                return result;
            } catch (Exception e) {
                FileLog.e(e);
                return false;
            }
        }
    }

    public UndoView(Context context) {
        this(context, null, false, null);
    }

    public UndoView(Context context, BaseFragment parent) {
        this(context, parent, false, null);
    }

    public UndoView(Context context, BaseFragment parent, boolean top, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.currentAction = -1;
        this.hideAnimationType = 1;
        this.enterOffsetMargin = AndroidUtilities.dp(8.0f);
        this.timeReplaceProgress = 1.0f;
        this.resourcesProvider = resourcesProvider;
        this.parentFragment = parent;
        this.fromTop = top;
        TextView textView = new TextView(context);
        this.infoTextView = textView;
        textView.setTextSize(1, 15.0f);
        this.infoTextView.setTextColor(getThemedColor(Theme.key_undo_infoColor));
        this.infoTextView.setLinkTextColor(getThemedColor(Theme.key_undo_cancelColor));
        this.infoTextView.setMovementMethod(new LinkMovementMethodMy());
        addView(this.infoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 45.0f, 13.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.subinfoTextView = textView2;
        textView2.setTextSize(1, 13.0f);
        this.subinfoTextView.setTextColor(getThemedColor(Theme.key_undo_infoColor));
        this.subinfoTextView.setLinkTextColor(getThemedColor(Theme.key_undo_cancelColor));
        this.subinfoTextView.setHighlightColor(0);
        this.subinfoTextView.setSingleLine(true);
        this.subinfoTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.subinfoTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        addView(this.subinfoTextView, LayoutHelper.createFrame(-2, -2.0f, 51, 58.0f, 27.0f, 8.0f, 0.0f));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.leftImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.leftImageView.setLayerColor("info1.**", getThemedColor(Theme.key_undo_background) | (-16777216));
        this.leftImageView.setLayerColor("info2.**", getThemedColor(Theme.key_undo_background) | (-16777216));
        this.leftImageView.setLayerColor("luc12.**", getThemedColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc11.**", getThemedColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc10.**", getThemedColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc9.**", getThemedColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc8.**", getThemedColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc7.**", getThemedColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc6.**", getThemedColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc5.**", getThemedColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc4.**", getThemedColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc3.**", getThemedColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc2.**", getThemedColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("luc1.**", getThemedColor(Theme.key_undo_infoColor));
        this.leftImageView.setLayerColor("Oval.**", getThemedColor(Theme.key_undo_infoColor));
        addView(this.leftImageView, LayoutHelper.createFrame(54, -2.0f, 19, 3.0f, 0.0f, 0.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(15.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(30, 30.0f, 19, 15.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.undoButton = linearLayout;
        linearLayout.setOrientation(0);
        this.undoButton.setBackground(Theme.createRadSelectorDrawable(getThemedColor(Theme.key_undo_cancelColor) & 587202559, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f)));
        addView(this.undoButton, LayoutHelper.createFrame(-2, -2.0f, 21, 0.0f, 0.0f, 11.0f, 0.0f));
        this.undoButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                UndoView.this.m3186lambda$new$0$orgtelegramuiComponentsUndoView(view);
            }
        });
        ImageView imageView = new ImageView(context);
        this.undoImageView = imageView;
        imageView.setImageResource(R.drawable.chats_undo);
        this.undoImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_undo_cancelColor), PorterDuff.Mode.MULTIPLY));
        this.undoButton.addView(this.undoImageView, LayoutHelper.createLinear(-2, -2, 19, 4, 4, 0, 4));
        TextView textView3 = new TextView(context);
        this.undoTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.undoTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.undoTextView.setTextColor(getThemedColor(Theme.key_undo_cancelColor));
        this.undoTextView.setText(LocaleController.getString("Undo", R.string.Undo));
        this.undoButton.addView(this.undoTextView, LayoutHelper.createLinear(-2, -2, 19, 6, 4, 8, 4));
        this.rect = new RectF(AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f), AndroidUtilities.dp(33.0f), AndroidUtilities.dp(33.0f));
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setColor(getThemedColor(Theme.key_undo_infoColor));
        TextPaint textPaint = new TextPaint(1);
        this.textPaint = textPaint;
        textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.textPaint.setColor(getThemedColor(Theme.key_undo_infoColor));
        setWillNotDraw(false);
        this.backgroundDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor(Theme.key_undo_background));
        setOnTouchListener(UndoView$$ExternalSyntheticLambda3.INSTANCE);
        setVisibility(4);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-UndoView */
    public /* synthetic */ void m3186lambda$new$0$orgtelegramuiComponentsUndoView(View v) {
        if (!canUndo()) {
            return;
        }
        hide(false, 1);
    }

    public static /* synthetic */ boolean lambda$new$1(View v, MotionEvent event) {
        return true;
    }

    public void setColors(int background, int text) {
        Theme.setDrawableColor(this.backgroundDrawable, background);
        this.infoTextView.setTextColor(text);
        this.subinfoTextView.setTextColor(text);
        this.leftImageView.setLayerColor("info1.**", background | (-16777216));
        this.leftImageView.setLayerColor("info2.**", (-16777216) | background);
    }

    private boolean isTooltipAction() {
        int i = this.currentAction;
        return i == 6 || i == 3 || i == 5 || i == 7 || i == 8 || i == 9 || i == 10 || i == 13 || i == 14 || i == 19 || i == 20 || i == 21 || i == 22 || i == 23 || i == 30 || i == 31 || i == 32 || i == 33 || i == 34 || i == 35 || i == 36 || i == 74 || i == 37 || i == 38 || i == 39 || i == 40 || i == 42 || i == 43 || i == 77 || i == 44 || i == 78 || i == 79 || i == 100 || i == 101 || i == ACTION_RINGTONE_ADDED;
    }

    private boolean hasSubInfo() {
        int i = this.currentAction;
        return i == 11 || i == 24 || i == 6 || i == 3 || i == 5 || i == 13 || i == 14 || i == 74 || (i == 7 && MessagesController.getInstance(this.currentAccount).dialogFilters.isEmpty()) || this.currentAction == ACTION_RINGTONE_ADDED;
    }

    public boolean isMultilineSubInfo() {
        int i = this.currentAction;
        return i == 12 || i == 15 || i == 24 || i == 74 || i == ACTION_RINGTONE_ADDED;
    }

    public void setAdditionalTranslationY(float value) {
        if (this.additionalTranslationY != value) {
            this.additionalTranslationY = value;
            updatePosition();
        }
    }

    public Object getCurrentInfoObject() {
        return this.currentInfoObject;
    }

    public void hide(boolean apply, int animated) {
        if (getVisibility() != 0 || !this.isShown) {
            return;
        }
        this.currentInfoObject = null;
        this.isShown = false;
        Runnable runnable = this.currentActionRunnable;
        if (runnable != null) {
            if (apply) {
                runnable.run();
            }
            this.currentActionRunnable = null;
        }
        Runnable runnable2 = this.currentCancelRunnable;
        if (runnable2 != null) {
            if (!apply) {
                runnable2.run();
            }
            this.currentCancelRunnable = null;
        }
        int i = this.currentAction;
        if (i == 0 || i == 1 || i == 26 || i == 27) {
            for (int a = 0; a < this.currentDialogIds.size(); a++) {
                long did = this.currentDialogIds.get(a).longValue();
                MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
                int i2 = this.currentAction;
                messagesController.removeDialogAction(did, i2 == 0 || i2 == 26, apply);
                onRemoveDialogAction(did, this.currentAction);
            }
        }
        int a2 = -1082130432;
        if (animated != 0) {
            AnimatorSet animatorSet = new AnimatorSet();
            if (animated == 1) {
                Animator[] animatorArr = new Animator[1];
                float[] fArr = new float[1];
                if (!this.fromTop) {
                    a2 = 1065353216;
                }
                fArr[0] = a2 * (this.enterOffsetMargin + this.undoViewHeight);
                animatorArr[0] = ObjectAnimator.ofFloat(this, "enterOffset", fArr);
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(250L);
            } else {
                animatorSet.playTogether(ObjectAnimator.ofFloat(this, View.SCALE_X, 0.8f), ObjectAnimator.ofFloat(this, View.SCALE_Y, 0.8f), ObjectAnimator.ofFloat(this, View.ALPHA, 0.0f));
                animatorSet.setDuration(180L);
            }
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.UndoView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    UndoView.this.setVisibility(4);
                    UndoView.this.setScaleX(1.0f);
                    UndoView.this.setScaleY(1.0f);
                    UndoView.this.setAlpha(1.0f);
                }
            });
            animatorSet.start();
            return;
        }
        if (!this.fromTop) {
            a2 = 1065353216;
        }
        setEnterOffset(a2 * (this.enterOffsetMargin + this.undoViewHeight));
        setVisibility(4);
    }

    protected void onRemoveDialogAction(long currentDialogId, int action) {
    }

    public void didPressUrl(CharacterStyle span) {
    }

    public void showWithAction(long did, int action, Runnable actionRunnable) {
        showWithAction(did, action, (Object) null, (Object) null, actionRunnable, (Runnable) null);
    }

    public void showWithAction(long did, int action, Object infoObject) {
        showWithAction(did, action, infoObject, (Object) null, (Runnable) null, (Runnable) null);
    }

    public void showWithAction(long did, int action, Runnable actionRunnable, Runnable cancelRunnable) {
        showWithAction(did, action, (Object) null, (Object) null, actionRunnable, cancelRunnable);
    }

    public void showWithAction(long did, int action, Object infoObject, Runnable actionRunnable, Runnable cancelRunnable) {
        showWithAction(did, action, infoObject, (Object) null, actionRunnable, cancelRunnable);
    }

    public void showWithAction(long did, int action, Object infoObject, Object infoObject2, Runnable actionRunnable, Runnable cancelRunnable) {
        ArrayList<Long> ids = new ArrayList<>();
        ids.add(Long.valueOf(did));
        showWithAction(ids, action, infoObject, infoObject2, actionRunnable, cancelRunnable);
    }

    /* JADX WARN: Removed duplicated region for block: B:300:0x0986  */
    /* JADX WARN: Removed duplicated region for block: B:314:0x09c5  */
    /* JADX WARN: Removed duplicated region for block: B:316:0x09d0  */
    /* JADX WARN: Removed duplicated region for block: B:317:0x0a14  */
    /* JADX WARN: Removed duplicated region for block: B:444:0x0f00  */
    /* JADX WARN: Removed duplicated region for block: B:620:0x18ea  */
    /* JADX WARN: Removed duplicated region for block: B:623:0x1911  */
    /* JADX WARN: Removed duplicated region for block: B:627:0x1957  */
    /* JADX WARN: Removed duplicated region for block: B:653:0x1a00  */
    /* JADX WARN: Removed duplicated region for block: B:685:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void showWithAction(java.util.ArrayList<java.lang.Long> r35, int r36, java.lang.Object r37, java.lang.Object r38, java.lang.Runnable r39, java.lang.Runnable r40) {
        /*
            Method dump skipped, instructions count: 6750
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.UndoView.showWithAction(java.util.ArrayList, int, java.lang.Object, java.lang.Object, java.lang.Runnable, java.lang.Runnable):void");
    }

    /* renamed from: lambda$showWithAction$2$org-telegram-ui-Components-UndoView */
    public /* synthetic */ void m3187lambda$showWithAction$2$orgtelegramuiComponentsUndoView(View view) {
        hide(false, 1);
    }

    public static /* synthetic */ boolean lambda$showWithAction$3(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$showWithAction$6$org-telegram-ui-Components-UndoView */
    public /* synthetic */ void m3190lambda$showWithAction$6$orgtelegramuiComponentsUndoView(TLRPC.Message message, View v) {
        hide(true, 1);
        TLRPC.TL_payments_getPaymentReceipt req = new TLRPC.TL_payments_getPaymentReceipt();
        req.msg_id = message.id;
        req.peer = this.parentFragment.getMessagesController().getInputPeer(message.peer_id);
        this.parentFragment.getConnectionsManager().sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda7
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                UndoView.this.m3189lambda$showWithAction$5$orgtelegramuiComponentsUndoView(tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$showWithAction$5$org-telegram-ui-Components-UndoView */
    public /* synthetic */ void m3189lambda$showWithAction$5$orgtelegramuiComponentsUndoView(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.UndoView$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                UndoView.this.m3188lambda$showWithAction$4$orgtelegramuiComponentsUndoView(response);
            }
        });
    }

    /* renamed from: lambda$showWithAction$4$org-telegram-ui-Components-UndoView */
    public /* synthetic */ void m3188lambda$showWithAction$4$orgtelegramuiComponentsUndoView(TLObject response) {
        if (response instanceof TLRPC.TL_payments_paymentReceipt) {
            this.parentFragment.presentFragment(new PaymentFormActivity((TLRPC.TL_payments_paymentReceipt) response));
        }
    }

    /* renamed from: lambda$showWithAction$7$org-telegram-ui-Components-UndoView */
    public /* synthetic */ void m3191lambda$showWithAction$7$orgtelegramuiComponentsUndoView() {
        this.leftImageView.performHapticFeedback(3, 2);
    }

    public void setEnterOffsetMargin(int enterOffsetMargin) {
        this.enterOffsetMargin = enterOffsetMargin;
    }

    protected boolean canUndo() {
        return true;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(this.undoViewHeight, C.BUFFER_FLAG_ENCRYPTED));
        this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.additionalTranslationY != 0.0f) {
            canvas.save();
            float bottom = (getMeasuredHeight() - this.enterOffset) + AndroidUtilities.dp(9.0f);
            if (bottom > 0.0f) {
                canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), bottom);
                super.dispatchDraw(canvas);
            }
            canvas.restore();
            return;
        }
        super.dispatchDraw(canvas);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.additionalTranslationY != 0.0f) {
            canvas.save();
            float bottom = (getMeasuredHeight() - this.enterOffset) + this.enterOffsetMargin + AndroidUtilities.dp(1.0f);
            if (bottom > 0.0f) {
                canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), bottom);
                super.dispatchDraw(canvas);
            }
            this.backgroundDrawable.draw(canvas);
            canvas.restore();
        } else {
            this.backgroundDrawable.draw(canvas);
        }
        int i = this.currentAction;
        if (i == 1 || i == 0 || i == 27 || i == 26 || i == 81) {
            long j = this.timeLeft;
            int newSeconds = j > 0 ? (int) Math.ceil(((float) j) / 1000.0f) : 0;
            if (this.prevSeconds != newSeconds) {
                this.prevSeconds = newSeconds;
                String format = String.format("%d", Integer.valueOf(Math.max(1, newSeconds)));
                this.timeLeftString = format;
                StaticLayout staticLayout = this.timeLayout;
                if (staticLayout != null) {
                    this.timeLayoutOut = staticLayout;
                    this.timeReplaceProgress = 0.0f;
                    this.textWidthOut = this.textWidth;
                }
                this.textWidth = (int) Math.ceil(this.textPaint.measureText(format));
                this.timeLayout = new StaticLayout(this.timeLeftString, this.textPaint, Integer.MAX_VALUE, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            float f = this.timeReplaceProgress;
            if (f < 1.0f) {
                float f2 = f + 0.10666667f;
                this.timeReplaceProgress = f2;
                if (f2 > 1.0f) {
                    this.timeReplaceProgress = 1.0f;
                } else {
                    invalidate();
                }
            }
            int alpha = this.textPaint.getAlpha();
            if (this.timeLayoutOut != null) {
                float f3 = this.timeReplaceProgress;
                if (f3 < 1.0f) {
                    this.textPaint.setAlpha((int) (alpha * (1.0f - f3)));
                    canvas.save();
                    canvas.translate(this.rect.centerX() - (this.textWidth / 2), AndroidUtilities.dp(17.2f) + (AndroidUtilities.dp(10.0f) * this.timeReplaceProgress));
                    this.timeLayoutOut.draw(canvas);
                    this.textPaint.setAlpha(alpha);
                    canvas.restore();
                }
            }
            if (this.timeLayout != null) {
                float f4 = this.timeReplaceProgress;
                if (f4 != 1.0f) {
                    this.textPaint.setAlpha((int) (alpha * f4));
                }
                canvas.save();
                canvas.translate(this.rect.centerX() - (this.textWidth / 2), AndroidUtilities.dp(17.2f) - (AndroidUtilities.dp(10.0f) * (1.0f - this.timeReplaceProgress)));
                this.timeLayout.draw(canvas);
                if (this.timeReplaceProgress != 1.0f) {
                    this.textPaint.setAlpha(alpha);
                }
                canvas.restore();
            }
            canvas.drawArc(this.rect, -90.0f, (((float) this.timeLeft) / 5000.0f) * (-360.0f), false, this.progressPaint);
        }
        long newTime = SystemClock.elapsedRealtime();
        long dt = newTime - this.lastUpdateTime;
        long j2 = this.timeLeft - dt;
        this.timeLeft = j2;
        this.lastUpdateTime = newTime;
        if (j2 <= 0) {
            hide(true, this.hideAnimationType);
        }
        if (this.currentAction != 82) {
            invalidate();
        }
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        this.infoTextView.invalidate();
        this.leftImageView.invalidate();
    }

    public void setInfoText(CharSequence text) {
        this.infoText = text;
    }

    public void setHideAnimationType(int hideAnimationType) {
        this.hideAnimationType = hideAnimationType;
    }

    public float getEnterOffset() {
        return this.enterOffset;
    }

    public void setEnterOffset(float enterOffset) {
        if (this.enterOffset != enterOffset) {
            this.enterOffset = enterOffset;
            updatePosition();
        }
    }

    private void updatePosition() {
        setTranslationY(((this.enterOffset - this.enterOffsetMargin) + AndroidUtilities.dp(8.0f)) - this.additionalTranslationY);
        invalidate();
    }

    @Override // android.view.View
    public Drawable getBackground() {
        return this.backgroundDrawable;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
