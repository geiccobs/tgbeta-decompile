package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.UndoView;
/* loaded from: classes4.dex */
public class ChannelCreateActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private static final int done_button = 1;
    private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList<>();
    private TextInfoPrivacyCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private TLRPC.FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private AvatarDrawable avatarDrawable;
    private RLottieImageView avatarEditor;
    private BackupImageView avatarImage;
    private View avatarOverlay;
    private RadialProgressView avatarProgressView;
    private RLottieDrawable cameraDrawable;
    private boolean canCreatePublic;
    private AlertDialog cancelDialog;
    private long chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private boolean createAfterUpload;
    private int currentStep;
    private EditTextBoldCursor descriptionTextView;
    private View doneButton;
    private CrossfadeDrawable doneButtonDrawable;
    private ValueAnimator doneButtonDrawableAnimator;
    private boolean donePressed;
    private Integer doneRequestId;
    private EditTextBoldCursor editText;
    private HeaderCell headerCell;
    private HeaderCell headerCell2;
    private TextView helpTextView;
    private ImageUpdater imageUpdater;
    private TLRPC.InputFile inputPhoto;
    private TLRPC.InputFile inputVideo;
    private String inputVideoPath;
    private TLRPC.TL_chatInviteExported invite;
    private boolean isPrivate;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout2;
    private LinearLayout linkContainer;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    private EditTextEmoji nameTextView;
    private String nameToSet;
    private LinkActionView permanentLinkView;
    private LinearLayout privateContainer;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private ShadowSectionCell sectionCell;
    private TextInfoPrivacyCell typeInfoCell;
    private double videoTimestamp;

    public ChannelCreateActivity(Bundle args) {
        super(args);
        this.canCreatePublic = true;
        int i = args.getInt("step", 0);
        this.currentStep = i;
        if (i == 0) {
            this.avatarDrawable = new AvatarDrawable();
            this.imageUpdater = new ImageUpdater(true);
            TLRPC.TL_channels_checkUsername req = new TLRPC.TL_channels_checkUsername();
            req.username = IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE;
            req.channel = new TLRPC.TL_inputChannelEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda15
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChannelCreateActivity.this.m1745lambda$new$1$orgtelegramuiChannelCreateActivity(tLObject, tL_error);
                }
            });
            return;
        }
        if (i == 1) {
            boolean z = args.getBoolean("canCreatePublic", true);
            this.canCreatePublic = z;
            this.isPrivate = !z;
            if (!z) {
                loadAdminedChannels();
            }
        }
        this.chatId = args.getLong(ChatReactionsEditActivity.KEY_CHAT_ID, 0L);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1744lambda$new$0$orgtelegramuiChannelCreateActivity(TLRPC.TL_error error) {
        this.canCreatePublic = error == null || !error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
    }

    /* renamed from: lambda$new$1$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1745lambda$new$1$orgtelegramuiChannelCreateActivity(TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ChannelCreateActivity.this.m1744lambda$new$0$orgtelegramuiChannelCreateActivity(error);
            }
        });
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
        if (this.currentStep == 1) {
            generateLink();
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.parentFragment = this;
            this.imageUpdater.setDelegate(this);
        }
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.doneRequestId != null) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.doneRequestId.intValue(), true);
            this.doneRequestId = null;
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidFailCreate);
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.clear();
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onResume();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onPause();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void dismissCurrentDialog() {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null && imageUpdater.dismissCurrentDialog(this.visibleDialog)) {
            return;
        }
        super.dismissCurrentDialog();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean dismissDialogOnPause(Dialog dialog) {
        ImageUpdater imageUpdater = this.imageUpdater;
        return (imageUpdater == null || imageUpdater.dismissDialogOnPause(dialog)) && super.dismissDialogOnPause(dialog);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onRequestPermissionsResultFragment(requestCode, permissions, grantResults);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return true;
        }
        this.nameTextView.hidePopup(true);
        return false;
    }

    public void showDoneCancelDialog() {
        if (this.cancelDialog != null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setMessage(LocaleController.getString("StopLoading", R.string.StopLoading));
        builder.setPositiveButton(LocaleController.getString("WaitMore", R.string.WaitMore), null);
        builder.setNegativeButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda11
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelCreateActivity.this.m1746x1cf40960(dialogInterface, i);
            }
        });
        this.cancelDialog = builder.show();
    }

    /* renamed from: lambda$showDoneCancelDialog$2$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1746x1cf40960(DialogInterface dialogInterface, int i) {
        this.donePressed = false;
        this.createAfterUpload = false;
        if (this.doneRequestId != null) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.doneRequestId.intValue(), true);
            this.doneRequestId = null;
        }
        updateDoneProgress(false);
        dialogInterface.dismiss();
    }

    public void updateDoneProgress(boolean loading) {
        if (this.doneButtonDrawable != null) {
            ValueAnimator valueAnimator = this.doneButtonDrawableAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float[] fArr = new float[2];
            fArr[0] = this.doneButtonDrawable.getProgress();
            float f = 1.0f;
            fArr[1] = loading ? 1.0f : 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            this.doneButtonDrawableAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ChannelCreateActivity.this.m1748xa4259f8(valueAnimator2);
                }
            });
            ValueAnimator valueAnimator2 = this.doneButtonDrawableAnimator;
            float progress = this.doneButtonDrawable.getProgress();
            if (!loading) {
                f = 0.0f;
            }
            valueAnimator2.setDuration(Math.abs(progress - f) * 200.0f);
            this.doneButtonDrawableAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.doneButtonDrawableAnimator.start();
        }
    }

    /* renamed from: lambda$updateDoneProgress$3$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1748xa4259f8(ValueAnimator a) {
        this.doneButtonDrawable.setProgress(((Float) a.getAnimatedValue()).floatValue());
        this.doneButtonDrawable.invalidateSelf();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.ChannelCreateActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int id) {
                if (id == -1) {
                    if (ChannelCreateActivity.this.donePressed) {
                        ChannelCreateActivity.this.showDoneCancelDialog();
                    } else {
                        ChannelCreateActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    if (ChannelCreateActivity.this.currentStep != 0) {
                        if (ChannelCreateActivity.this.currentStep == 1) {
                            if (!ChannelCreateActivity.this.isPrivate) {
                                if (ChannelCreateActivity.this.descriptionTextView.length() != 0) {
                                    if (ChannelCreateActivity.this.lastNameAvailable) {
                                        MessagesController.getInstance(ChannelCreateActivity.this.currentAccount).updateChannelUserName(ChannelCreateActivity.this.chatId, ChannelCreateActivity.this.lastCheckName);
                                    } else {
                                        Vibrator v = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                                        if (v != null) {
                                            v.vibrate(200L);
                                        }
                                        AndroidUtilities.shakeView(ChannelCreateActivity.this.checkTextView, 2.0f, 0);
                                        return;
                                    }
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChannelCreateActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("ChannelPublicEmptyUsernameTitle", R.string.ChannelPublicEmptyUsernameTitle));
                                    builder.setMessage(LocaleController.getString("ChannelPublicEmptyUsername", R.string.ChannelPublicEmptyUsername));
                                    builder.setPositiveButton(LocaleController.getString("Close", R.string.Close), null);
                                    ChannelCreateActivity.this.showDialog(builder.create());
                                    return;
                                }
                            }
                            Bundle args = new Bundle();
                            args.putInt("step", 2);
                            args.putLong("chatId", ChannelCreateActivity.this.chatId);
                            args.putInt("chatType", 2);
                            ChannelCreateActivity.this.presentFragment(new GroupCreateActivity(args), true);
                        }
                    } else if (ChannelCreateActivity.this.getParentActivity() != null) {
                        if (ChannelCreateActivity.this.donePressed) {
                            ChannelCreateActivity.this.showDoneCancelDialog();
                        } else if (ChannelCreateActivity.this.nameTextView.length() != 0) {
                            ChannelCreateActivity.this.donePressed = true;
                            ChannelCreateActivity.this.updateDoneProgress(true);
                            if (ChannelCreateActivity.this.imageUpdater.isUploadingImage()) {
                                ChannelCreateActivity.this.createAfterUpload = true;
                                return;
                            }
                            ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                            channelCreateActivity.doneRequestId = Integer.valueOf(MessagesController.getInstance(channelCreateActivity.currentAccount).createChat(ChannelCreateActivity.this.nameTextView.getText().toString(), new ArrayList<>(), ChannelCreateActivity.this.descriptionTextView.getText().toString(), 2, false, null, null, ChannelCreateActivity.this));
                        } else {
                            Vibrator v2 = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                            if (v2 != null) {
                                v2.vibrate(200L);
                            }
                            AndroidUtilities.shakeView(ChannelCreateActivity.this.nameTextView, 2.0f, 0);
                        }
                    }
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        Drawable checkmark = context.getResources().getDrawable(R.drawable.ic_ab_done).mutate();
        checkmark.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_actionBarDefaultIcon), PorterDuff.Mode.MULTIPLY));
        CrossfadeDrawable crossfadeDrawable = new CrossfadeDrawable(checkmark, new CircularProgressDrawable(Theme.getColor(Theme.key_actionBarDefaultIcon)));
        this.doneButtonDrawable = crossfadeDrawable;
        this.doneButton = menu.addItemWithWidth(1, crossfadeDrawable, AndroidUtilities.dp(56.0f), LocaleController.getString("Done", R.string.Done));
        int i = this.currentStep;
        if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("NewChannel", R.string.NewChannel));
            SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) { // from class: org.telegram.ui.ChannelCreateActivity.2
                private boolean ignoreLayout;

                @Override // android.widget.FrameLayout, android.view.View
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
                    int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
                    setMeasuredDimension(widthSize, heightSize);
                    int heightSize2 = heightSize - getPaddingTop();
                    measureChildWithMargins(ChannelCreateActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    int keyboardSize = measureKeyboardHeight();
                    if (keyboardSize > AndroidUtilities.dp(20.0f)) {
                        this.ignoreLayout = true;
                        ChannelCreateActivity.this.nameTextView.hideEmojiView();
                        this.ignoreLayout = false;
                    }
                    int childCount = getChildCount();
                    for (int i2 = 0; i2 < childCount; i2++) {
                        View child = getChildAt(i2);
                        if (child != null && child.getVisibility() != 8 && child != ChannelCreateActivity.this.actionBar) {
                            if (ChannelCreateActivity.this.nameTextView != null && ChannelCreateActivity.this.nameTextView.isPopupView(child)) {
                                if (AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) {
                                    if (AndroidUtilities.isTablet()) {
                                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (heightSize2 - AndroidUtilities.statusBarHeight) + getPaddingTop()), C.BUFFER_FLAG_ENCRYPTED));
                                    } else {
                                        child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((heightSize2 - AndroidUtilities.statusBarHeight) + getPaddingTop(), C.BUFFER_FLAG_ENCRYPTED));
                                    }
                                } else {
                                    child.measure(View.MeasureSpec.makeMeasureSpec(widthSize, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, C.BUFFER_FLAG_ENCRYPTED));
                                }
                            } else {
                                measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                            }
                        }
                    }
                }

                @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                public void onLayout(boolean changed, int l, int t, int r, int b) {
                    int childLeft;
                    int childTop;
                    int count = getChildCount();
                    int keyboardSize = measureKeyboardHeight();
                    int paddingBottom = (keyboardSize > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow || AndroidUtilities.isTablet()) ? 0 : ChannelCreateActivity.this.nameTextView.getEmojiPadding();
                    setBottomClip(paddingBottom);
                    for (int i2 = 0; i2 < count; i2++) {
                        View child = getChildAt(i2);
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
                                    childLeft = (r - width) - lp.rightMargin;
                                    break;
                                default:
                                    childLeft = lp.leftMargin;
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
                            if (ChannelCreateActivity.this.nameTextView != null && ChannelCreateActivity.this.nameTextView.isPopupView(child)) {
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
            };
            sizeNotifierFrameLayout.setOnTouchListener(ChannelCreateActivity$$ExternalSyntheticLambda24.INSTANCE);
            this.fragmentView = sizeNotifierFrameLayout;
            this.fragmentView.setTag(Theme.key_windowBackgroundWhite);
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            LinearLayout linearLayout = new LinearLayout(context);
            this.linearLayout = linearLayout;
            linearLayout.setOrientation(1);
            sizeNotifierFrameLayout.addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
            FrameLayout frameLayout = new FrameLayout(context);
            this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            BackupImageView backupImageView = new BackupImageView(context) { // from class: org.telegram.ui.ChannelCreateActivity.3
                @Override // android.view.View
                public void invalidate() {
                    if (ChannelCreateActivity.this.avatarOverlay != null) {
                        ChannelCreateActivity.this.avatarOverlay.invalidate();
                    }
                    super.invalidate();
                }

                @Override // android.view.View
                public void invalidate(int l, int t, int r, int b) {
                    if (ChannelCreateActivity.this.avatarOverlay != null) {
                        ChannelCreateActivity.this.avatarOverlay.invalidate();
                    }
                    super.invalidate(l, t, r, b);
                }
            };
            this.avatarImage = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
            this.avatarDrawable.setInfo(5L, null, null);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            final Paint paint = new Paint(1);
            paint.setColor(1426063360);
            View view = new View(context) { // from class: org.telegram.ui.ChannelCreateActivity.4
                @Override // android.view.View
                protected void onDraw(Canvas canvas) {
                    if (ChannelCreateActivity.this.avatarImage != null && ChannelCreateActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        paint.setAlpha((int) (ChannelCreateActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f));
                        canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredWidth() / 2.0f, paint);
                    }
                }
            };
            this.avatarOverlay = view;
            view.setContentDescription(LocaleController.getString("ChatSetPhotoOrVideo", R.string.ChatSetPhotoOrVideo));
            frameLayout.addView(this.avatarOverlay, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            this.avatarOverlay.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda22
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChannelCreateActivity.this.m1731lambda$createView$7$orgtelegramuiChannelCreateActivity(view2);
                }
            });
            this.cameraDrawable = new RLottieDrawable(R.raw.camera, "2131558413", AndroidUtilities.dp(60.0f), AndroidUtilities.dp(60.0f), false, null);
            RLottieImageView rLottieImageView = new RLottieImageView(context) { // from class: org.telegram.ui.ChannelCreateActivity.5
                @Override // android.view.View
                public void invalidate(int l, int t, int r, int b) {
                    super.invalidate(l, t, r, b);
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }

                @Override // android.view.View
                public void invalidate() {
                    super.invalidate();
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }
            };
            this.avatarEditor = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.avatarEditor.setEnabled(false);
            this.avatarEditor.setClickable(false);
            this.avatarEditor.setPadding(AndroidUtilities.dp(2.0f), 0, 0, AndroidUtilities.dp(1.0f));
            frameLayout.addView(this.avatarEditor, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context);
            this.avatarProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            this.avatarProgressView.setNoProgress(false);
            frameLayout.addView(this.avatarProgressView, LayoutHelper.createFrame(64, 64.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 16.0f, 12.0f, LocaleController.isRTL ? 16.0f : 0.0f, 12.0f));
            showAvatarProgress(false, false);
            EditTextEmoji editTextEmoji2 = new EditTextEmoji(context, sizeNotifierFrameLayout, this, 0);
            this.nameTextView = editTextEmoji2;
            editTextEmoji2.setHint(LocaleController.getString("EnterChannelName", R.string.EnterChannelName));
            String str = this.nameToSet;
            if (str != null) {
                this.nameTextView.setText(str);
                this.nameToSet = null;
            }
            InputFilter[] inputFilters = {new InputFilter.LengthFilter(100)};
            this.nameTextView.setFilters(inputFilters);
            this.nameTextView.getEditText().setSingleLine(true);
            this.nameTextView.getEditText().setImeOptions(5);
            this.nameTextView.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda25
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                    return ChannelCreateActivity.this.m1732lambda$createView$8$orgtelegramuiChannelCreateActivity(textView, i2, keyEvent);
                }
            });
            frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 16, LocaleController.isRTL ? 5.0f : 96.0f, 0.0f, LocaleController.isRTL ? 96.0f : 5.0f, 0.0f));
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
            this.descriptionTextView = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.descriptionTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.descriptionTextView.setBackgroundDrawable(null);
            this.descriptionTextView.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_windowBackgroundWhiteRedText3));
            this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.descriptionTextView.setInputType(180225);
            this.descriptionTextView.setImeOptions(6);
            InputFilter[] inputFilters2 = {new InputFilter.LengthFilter(120)};
            this.descriptionTextView.setFilters(inputFilters2);
            this.descriptionTextView.setHint(LocaleController.getString("DescriptionPlaceholder", R.string.DescriptionPlaceholder));
            this.descriptionTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            this.descriptionTextView.setCursorWidth(1.5f);
            this.linearLayout.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 18.0f, 24.0f, 0.0f));
            this.descriptionTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda1
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                    return ChannelCreateActivity.this.m1733lambda$createView$9$orgtelegramuiChannelCreateActivity(textView, i2, keyEvent);
                }
            });
            this.descriptionTextView.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ChannelCreateActivity.6
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                }
            });
            TextView textView = new TextView(context);
            this.helpTextView = textView;
            textView.setTextSize(1, 15.0f);
            this.helpTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
            this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.helpTextView.setText(LocaleController.getString("DescriptionInfo", R.string.DescriptionInfo));
            this.linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 20));
        } else if (i == 1) {
            this.fragmentView = new ScrollView(context);
            ScrollView scrollView = (ScrollView) this.fragmentView;
            scrollView.setFillViewport(true);
            LinearLayout linearLayout2 = new LinearLayout(context);
            this.linearLayout = linearLayout2;
            linearLayout2.setOrientation(1);
            scrollView.addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
            this.actionBar.setTitle(LocaleController.getString("ChannelSettingsTitle", R.string.ChannelSettingsTitle));
            this.fragmentView.setTag(Theme.key_windowBackgroundGray);
            this.fragmentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            HeaderCell headerCell = new HeaderCell(context, 23);
            this.headerCell2 = headerCell;
            headerCell.setHeight(46);
            this.headerCell2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.headerCell2.setText(LocaleController.getString("ChannelTypeHeader", R.string.ChannelTypeHeader));
            this.linearLayout.addView(this.headerCell2);
            LinearLayout linearLayout3 = new LinearLayout(context);
            this.linearLayout2 = linearLayout3;
            linearLayout3.setOrientation(1);
            this.linearLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(-1, -2));
            RadioButtonCell radioButtonCell = new RadioButtonCell(context);
            this.radioButtonCell1 = radioButtonCell;
            radioButtonCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.radioButtonCell1.setTextAndValue(LocaleController.getString("ChannelPublic", R.string.ChannelPublic), LocaleController.getString("ChannelPublicInfo", R.string.ChannelPublicInfo), false, !this.isPrivate);
            this.linearLayout2.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell1.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda20
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChannelCreateActivity.this.m1727lambda$createView$10$orgtelegramuiChannelCreateActivity(view2);
                }
            });
            RadioButtonCell radioButtonCell2 = new RadioButtonCell(context);
            this.radioButtonCell2 = radioButtonCell2;
            radioButtonCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate), LocaleController.getString("ChannelPrivateInfo", R.string.ChannelPrivateInfo), false, this.isPrivate);
            this.linearLayout2.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
            this.radioButtonCell2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda21
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ChannelCreateActivity.this.m1728lambda$createView$11$orgtelegramuiChannelCreateActivity(view2);
                }
            });
            ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context);
            this.sectionCell = shadowSectionCell;
            this.linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout4 = new LinearLayout(context);
            this.linkContainer = linearLayout4;
            linearLayout4.setOrientation(1);
            this.linkContainer.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
            HeaderCell headerCell2 = new HeaderCell(context);
            this.headerCell = headerCell2;
            this.linkContainer.addView(headerCell2);
            LinearLayout linearLayout5 = new LinearLayout(context);
            this.publicContainer = linearLayout5;
            linearLayout5.setOrientation(0);
            this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 21.0f, 7.0f, 21.0f, 0.0f));
            EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context);
            this.editText = editTextBoldCursor2;
            editTextBoldCursor2.setText(MessagesController.getInstance(this.currentAccount).linkPrefix + "/");
            this.editText.setTextSize(1, 18.0f);
            this.editText.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.editText.setMaxLines(1);
            this.editText.setLines(1);
            this.editText.setEnabled(false);
            this.editText.setBackgroundDrawable(null);
            this.editText.setPadding(0, 0, 0, 0);
            this.editText.setSingleLine(true);
            this.editText.setInputType(163840);
            this.editText.setImeOptions(6);
            this.publicContainer.addView(this.editText, LayoutHelper.createLinear(-2, 36));
            EditTextBoldCursor editTextBoldCursor3 = new EditTextBoldCursor(context);
            this.descriptionTextView = editTextBoldCursor3;
            editTextBoldCursor3.setTextSize(1, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            this.descriptionTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.descriptionTextView.setMaxLines(1);
            this.descriptionTextView.setLines(1);
            this.descriptionTextView.setBackgroundDrawable(null);
            this.descriptionTextView.setPadding(0, 0, 0, 0);
            this.descriptionTextView.setSingleLine(true);
            this.descriptionTextView.setInputType(163872);
            this.descriptionTextView.setImeOptions(6);
            this.descriptionTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", R.string.ChannelUsernamePlaceholder));
            this.descriptionTextView.setCursorColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            this.descriptionTextView.setCursorWidth(1.5f);
            this.publicContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, 36));
            this.descriptionTextView.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.ChannelCreateActivity.7
                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
                }

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
                    ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                    channelCreateActivity.checkUserName(channelCreateActivity.descriptionTextView.getText().toString());
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                }
            });
            LinearLayout linearLayout6 = new LinearLayout(context);
            this.privateContainer = linearLayout6;
            linearLayout6.setOrientation(1);
            this.linkContainer.addView(this.privateContainer, LayoutHelper.createLinear(-1, -2));
            LinkActionView linkActionView = new LinkActionView(context, this, null, this.chatId, true, ChatObject.isChannel(getMessagesController().getChat(Long.valueOf(this.chatId))));
            this.permanentLinkView = linkActionView;
            linkActionView.hideRevokeOption(true);
            this.permanentLinkView.setUsers(0, null);
            this.privateContainer.addView(this.permanentLinkView);
            TextView textView2 = new TextView(context);
            this.checkTextView = textView2;
            textView2.setTextSize(1, 15.0f);
            this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.checkTextView.setVisibility(8);
            this.linkContainer.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 17, 3, 17, 7));
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
            this.typeInfoCell = textInfoPrivacyCell;
            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
            LoadingCell loadingCell = new LoadingCell(context);
            this.loadingAdminedCell = loadingCell;
            this.linearLayout.addView(loadingCell, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout7 = new LinearLayout(context);
            this.adminnedChannelsLayout = linearLayout7;
            linearLayout7.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.adminnedChannelsLayout.setOrientation(1);
            this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
            this.adminedInfoCell = textInfoPrivacyCell2;
            textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(context, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
            updatePrivatePublic();
        }
        return this.fragmentView;
    }

    public static /* synthetic */ boolean lambda$createView$4(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$7$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1731lambda$createView$7$orgtelegramuiChannelCreateActivity(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ChannelCreateActivity.this.m1729lambda$createView$5$orgtelegramuiChannelCreateActivity();
            }
        }, new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda19
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                ChannelCreateActivity.this.m1730lambda$createView$6$orgtelegramuiChannelCreateActivity(dialogInterface);
            }
        });
        this.cameraDrawable.setCurrentFrame(0);
        this.cameraDrawable.setCustomEndFrame(43);
        this.avatarEditor.playAnimation();
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1729lambda$createView$5$orgtelegramuiChannelCreateActivity() {
        this.avatar = null;
        this.avatarBig = null;
        this.inputPhoto = null;
        this.inputVideo = null;
        this.inputVideoPath = null;
        this.videoTimestamp = FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE;
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (Object) null);
        this.avatarEditor.setAnimation(this.cameraDrawable);
        this.cameraDrawable.setCurrentFrame(0);
    }

    /* renamed from: lambda$createView$6$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1730lambda$createView$6$orgtelegramuiChannelCreateActivity(DialogInterface dialog) {
        if (!this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCustomEndFrame(86);
            this.avatarEditor.playAnimation();
            return;
        }
        this.cameraDrawable.setCurrentFrame(0, false);
    }

    /* renamed from: lambda$createView$8$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ boolean m1732lambda$createView$8$orgtelegramuiChannelCreateActivity(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 5 && !TextUtils.isEmpty(this.nameTextView.getEditText().getText())) {
            this.descriptionTextView.requestFocus();
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$9$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ boolean m1733lambda$createView$9$orgtelegramuiChannelCreateActivity(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i == 6 && (view = this.doneButton) != null) {
            view.performClick();
            return true;
        }
        return false;
    }

    /* renamed from: lambda$createView$10$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1727lambda$createView$10$orgtelegramuiChannelCreateActivity(View v) {
        if (!this.canCreatePublic) {
            showPremiumIncreaseLimitDialog();
        } else if (!this.isPrivate) {
        } else {
            this.isPrivate = false;
            updatePrivatePublic();
        }
    }

    /* renamed from: lambda$createView$11$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1728lambda$createView$11$orgtelegramuiChannelCreateActivity(View v) {
        if (this.isPrivate) {
            return;
        }
        this.isPrivate = true;
        updatePrivatePublic();
    }

    private void generateLink() {
        if (this.loadingInvite || this.invite != null) {
            return;
        }
        TLRPC.ChatFull chatFull = getMessagesController().getChatFull(this.chatId);
        if (chatFull != null) {
            this.invite = chatFull.exported_invite;
        }
        if (this.invite != null) {
            return;
        }
        this.loadingInvite = true;
        TLRPC.TL_messages_getExportedChatInvites req = new TLRPC.TL_messages_getExportedChatInvites();
        req.peer = getMessagesController().getInputPeer(-this.chatId);
        req.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
        req.limit = 1;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda12
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelCreateActivity.this.m1736lambda$generateLink$13$orgtelegramuiChannelCreateActivity(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$generateLink$13$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1736lambda$generateLink$13$orgtelegramuiChannelCreateActivity(final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                ChannelCreateActivity.this.m1735lambda$generateLink$12$orgtelegramuiChannelCreateActivity(error, response);
            }
        });
    }

    /* renamed from: lambda$generateLink$12$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1735lambda$generateLink$12$orgtelegramuiChannelCreateActivity(TLRPC.TL_error error, TLObject response) {
        if (error == null) {
            TLRPC.TL_messages_exportedChatInvites invites = (TLRPC.TL_messages_exportedChatInvites) response;
            this.invite = (TLRPC.TL_chatInviteExported) invites.invites.get(0);
        }
        this.loadingInvite = false;
        LinkActionView linkActionView = this.permanentLinkView;
        TLRPC.TL_chatInviteExported tL_chatInviteExported = this.invite;
        linkActionView.setLink(tL_chatInviteExported != null ? tL_chatInviteExported.link : null);
    }

    private void updatePrivatePublic() {
        String str;
        int i;
        String str2;
        int i2;
        if (this.sectionCell == null) {
            return;
        }
        int i3 = 8;
        if (!this.isPrivate && !this.canCreatePublic) {
            this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", R.string.ChangePublicLimitReached));
            this.typeInfoCell.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            this.linkContainer.setVisibility(8);
            this.sectionCell.setVisibility(8);
            if (this.loadingAdminedChannels) {
                this.loadingAdminedCell.setVisibility(0);
                this.adminnedChannelsLayout.setVisibility(8);
                TextInfoPrivacyCell textInfoPrivacyCell = this.typeInfoCell;
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell.getContext(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                this.adminedInfoCell.setVisibility(8);
            } else {
                TextInfoPrivacyCell textInfoPrivacyCell2 = this.typeInfoCell;
                textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell2.getContext(), (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                this.loadingAdminedCell.setVisibility(8);
                this.adminnedChannelsLayout.setVisibility(0);
                this.adminedInfoCell.setVisibility(0);
            }
        } else {
            this.typeInfoCell.setTag(Theme.key_windowBackgroundWhiteGrayText4);
            this.typeInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
            this.sectionCell.setVisibility(0);
            this.adminedInfoCell.setVisibility(8);
            this.adminnedChannelsLayout.setVisibility(8);
            TextInfoPrivacyCell textInfoPrivacyCell3 = this.typeInfoCell;
            textInfoPrivacyCell3.setBackgroundDrawable(Theme.getThemedDrawable(textInfoPrivacyCell3.getContext(), (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            this.linkContainer.setVisibility(0);
            this.loadingAdminedCell.setVisibility(8);
            TextInfoPrivacyCell textInfoPrivacyCell4 = this.typeInfoCell;
            if (this.isPrivate) {
                i = R.string.ChannelPrivateLinkHelp;
                str = "ChannelPrivateLinkHelp";
            } else {
                i = R.string.ChannelUsernameHelp;
                str = "ChannelUsernameHelp";
            }
            textInfoPrivacyCell4.setText(LocaleController.getString(str, i));
            HeaderCell headerCell = this.headerCell;
            if (this.isPrivate) {
                i2 = R.string.ChannelInviteLinkTitle;
                str2 = "ChannelInviteLinkTitle";
            } else {
                i2 = R.string.ChannelLinkTitle;
                str2 = "ChannelLinkTitle";
            }
            headerCell.setText(LocaleController.getString(str2, i2));
            this.publicContainer.setVisibility(this.isPrivate ? 8 : 0);
            this.privateContainer.setVisibility(this.isPrivate ? 0 : 8);
            this.linkContainer.setPadding(0, 0, 0, this.isPrivate ? 0 : AndroidUtilities.dp(7.0f));
            LinkActionView linkActionView = this.permanentLinkView;
            TLRPC.TL_chatInviteExported tL_chatInviteExported = this.invite;
            linkActionView.setLink(tL_chatInviteExported != null ? tL_chatInviteExported.link : null);
            TextView textView = this.checkTextView;
            if (!this.isPrivate && textView.length() != 0) {
                i3 = 0;
            }
            textView.setVisibility(i3);
        }
        this.radioButtonCell1.setChecked(!this.isPrivate, true);
        this.radioButtonCell2.setChecked(this.isPrivate, true);
        this.descriptionTextView.clearFocus();
        AndroidUtilities.hideKeyboard(this.descriptionTextView);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void onUploadProgressChanged(float progress) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(progress);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void didStartUpload(boolean isVideo) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(0.0f);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public void didUploadPhoto(final TLRPC.InputFile photo, final TLRPC.InputFile video, final double videoStartTimestamp, final String videoPath, final TLRPC.PhotoSize bigSize, final TLRPC.PhotoSize smallSize) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ChannelCreateActivity.this.m1734lambda$didUploadPhoto$14$orgtelegramuiChannelCreateActivity(photo, video, videoPath, videoStartTimestamp, smallSize, bigSize);
            }
        });
    }

    /* renamed from: lambda$didUploadPhoto$14$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1734lambda$didUploadPhoto$14$orgtelegramuiChannelCreateActivity(TLRPC.InputFile photo, TLRPC.InputFile video, String videoPath, double videoStartTimestamp, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
        if (photo != null || video != null) {
            this.inputPhoto = photo;
            this.inputVideo = video;
            this.inputVideoPath = videoPath;
            this.videoTimestamp = videoStartTimestamp;
            if (this.createAfterUpload) {
                AlertDialog alertDialog = this.cancelDialog;
                if (alertDialog != null) {
                    try {
                        alertDialog.dismiss();
                        this.cancelDialog = null;
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                updateDoneProgress(false);
                this.donePressed = false;
                this.doneButton.performClick();
            }
            showAvatarProgress(false, true);
            return;
        }
        this.avatar = smallSize.location;
        this.avatarBig = bigSize.location;
        this.avatarImage.setImage(ImageLocation.getForLocal(this.avatar), "50_50", this.avatarDrawable, (Object) null);
        showAvatarProgress(true, false);
    }

    @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
    }

    private void showAvatarProgress(final boolean show, boolean animated) {
        if (this.avatarEditor == null) {
            return;
        }
        AnimatorSet animatorSet = this.avatarAnimation;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.avatarAnimation.cancel();
            this.avatarAnimation = null;
        }
        if (!animated) {
            if (show) {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(4);
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
                return;
            }
            this.avatarEditor.setAlpha(1.0f);
            this.avatarEditor.setVisibility(0);
            this.avatarProgressView.setAlpha(0.0f);
            this.avatarProgressView.setVisibility(4);
            return;
        }
        this.avatarAnimation = new AnimatorSet();
        if (show) {
            this.avatarProgressView.setVisibility(0);
            this.avatarAnimation.playTogether(ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 1.0f));
        } else {
            if (this.avatarEditor.getVisibility() != 0) {
                this.avatarEditor.setAlpha(0.0f);
            }
            this.avatarEditor.setVisibility(0);
            this.avatarAnimation.playTogether(ObjectAnimator.ofFloat(this.avatarEditor, View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.avatarProgressView, View.ALPHA, 0.0f));
        }
        this.avatarAnimation.setDuration(180L);
        this.avatarAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChannelCreateActivity.8
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (ChannelCreateActivity.this.avatarAnimation == null || ChannelCreateActivity.this.avatarEditor == null) {
                    return;
                }
                if (show) {
                    ChannelCreateActivity.this.avatarEditor.setVisibility(4);
                } else {
                    ChannelCreateActivity.this.avatarProgressView.setVisibility(4);
                }
                ChannelCreateActivity.this.avatarAnimation = null;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animation) {
                ChannelCreateActivity.this.avatarAnimation = null;
            }
        });
        this.avatarAnimation.start();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void saveSelfArgs(Bundle args) {
        if (this.currentStep == 0) {
            ImageUpdater imageUpdater = this.imageUpdater;
            if (imageUpdater != null && imageUpdater.currentPicturePath != null) {
                args.putString("path", this.imageUpdater.currentPicturePath);
            }
            EditTextEmoji editTextEmoji = this.nameTextView;
            if (editTextEmoji != null) {
                String text = editTextEmoji.getText().toString();
                if (text.length() != 0) {
                    args.putString("nameTextView", text);
                }
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void restoreSelfArgs(Bundle args) {
        if (this.currentStep == 0) {
            ImageUpdater imageUpdater = this.imageUpdater;
            if (imageUpdater != null) {
                imageUpdater.currentPicturePath = args.getString("path");
            }
            String text = args.getString("nameTextView");
            if (text != null) {
                EditTextEmoji editTextEmoji = this.nameTextView;
                if (editTextEmoji != null) {
                    editTextEmoji.setText(text);
                } else {
                    this.nameToSet = text;
                }
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && this.currentStep != 1) {
            this.nameTextView.requestFocus();
            this.nameTextView.openKeyboard();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatDidFailCreate) {
            AlertDialog alertDialog = this.cancelDialog;
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                    this.cancelDialog = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            updateDoneProgress(false);
            this.donePressed = false;
        } else if (id == NotificationCenter.chatDidCreated) {
            AlertDialog alertDialog2 = this.cancelDialog;
            if (alertDialog2 != null) {
                try {
                    alertDialog2.dismiss();
                    this.cancelDialog = null;
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            long chat_id = ((Long) args[0]).longValue();
            Bundle bundle = new Bundle();
            bundle.putInt("step", 1);
            bundle.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, chat_id);
            bundle.putBoolean("canCreatePublic", this.canCreatePublic);
            if (this.inputPhoto != null || this.inputVideo != null) {
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(chat_id, null, this.inputPhoto, this.inputVideo, this.videoTimestamp, this.inputVideoPath, this.avatar, this.avatarBig, null);
            }
            presentFragment(new ChannelCreateActivity(bundle), true);
        }
    }

    private void loadAdminedChannels() {
        if (this.loadingAdminedChannels) {
            return;
        }
        this.loadingAdminedChannels = true;
        updatePrivatePublic();
        TLRPC.TL_channels_getAdminedPublicChannels req = new TLRPC.TL_channels_getAdminedPublicChannels();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda14
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelCreateActivity.this.m1743x3a0330bb(tLObject, tL_error);
            }
        });
    }

    /* renamed from: lambda$loadAdminedChannels$20$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1743x3a0330bb(final TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                ChannelCreateActivity.this.m1742xf3edf125(response);
            }
        });
    }

    /* renamed from: lambda$loadAdminedChannels$19$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1742xf3edf125(TLObject response) {
        this.loadingAdminedChannels = false;
        if (response == null || getParentActivity() == null) {
            return;
        }
        for (int a = 0; a < this.adminedChannelCells.size(); a++) {
            this.linearLayout.removeView(this.adminedChannelCells.get(a));
        }
        this.adminedChannelCells.clear();
        TLRPC.TL_messages_chats res = (TLRPC.TL_messages_chats) response;
        for (int a2 = 0; a2 < res.chats.size(); a2++) {
            AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new View.OnClickListener() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda23
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    ChannelCreateActivity.this.m1741x2aecf9e4(view);
                }
            }, false, 0);
            TLRPC.Chat chat = res.chats.get(a2);
            boolean z = true;
            if (a2 != res.chats.size() - 1) {
                z = false;
            }
            adminedChannelCell.setChannel(chat, z);
            this.adminedChannelCells.add(adminedChannelCell);
            this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
        }
        updatePrivatePublic();
    }

    /* renamed from: lambda$loadAdminedChannels$18$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1741x2aecf9e4(View view) {
        AdminedChannelCell cell = (AdminedChannelCell) view.getParent();
        final TLRPC.Chat channel = cell.getCurrentChannel();
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        if (channel.megagroup) {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", R.string.RevokeLinkAlert, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
        } else {
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", R.string.RevokeLinkAlertChannel, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + channel.username, channel.title)));
        }
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("RevokeButton", R.string.RevokeButton), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda18
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelCreateActivity.this.m1740x61ec02a3(channel, dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    /* renamed from: lambda$loadAdminedChannels$17$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1740x61ec02a3(TLRPC.Chat channel, DialogInterface dialogInterface, int i) {
        TLRPC.TL_channels_updateUsername req1 = new TLRPC.TL_channels_updateUsername();
        req1.channel = MessagesController.getInputChannel(channel);
        req1.username = "";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, new RequestDelegate() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda13
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelCreateActivity.this.m1739x98eb0b62(tLObject, tL_error);
            }
        }, 64);
    }

    /* renamed from: lambda$loadAdminedChannels$16$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1739x98eb0b62(TLObject response1, TLRPC.TL_error error1) {
        if (response1 instanceof TLRPC.TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ChannelCreateActivity.this.m1738xcfea1421();
                }
            });
        }
    }

    /* renamed from: lambda$loadAdminedChannels$15$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1738xcfea1421() {
        this.canCreatePublic = true;
        if (this.descriptionTextView.length() > 0) {
            checkUserName(this.descriptionTextView.getText().toString());
        }
        updatePrivatePublic();
    }

    public boolean checkUserName(final String name) {
        if (name != null && name.length() > 0) {
            this.checkTextView.setVisibility(0);
        } else {
            this.checkTextView.setVisibility(8);
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
        this.lastNameAvailable = false;
        if (name != null) {
            if (name.startsWith("_") || name.endsWith("_")) {
                this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                return false;
            }
            for (int a = 0; a < name.length(); a++) {
                char ch = name.charAt(a);
                if (a == 0 && ch >= '0' && ch <= '9') {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", R.string.LinkInvalidStartNumber));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    return false;
                } else if ((ch < '0' || ch > '9') && ((ch < 'a' || ch > 'z') && ((ch < 'A' || ch > 'Z') && ch != '_'))) {
                    this.checkTextView.setText(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                    this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
                    return false;
                }
            }
        }
        if (name == null || name.length() < 5) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", R.string.LinkInvalidShort));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        } else if (name.length() > 32) {
            this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", R.string.LinkInvalidLong));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            return false;
        } else {
            this.checkTextView.setText(LocaleController.getString("LinkChecking", R.string.LinkChecking));
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGrayText8);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
            this.lastCheckName = name;
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ChannelCreateActivity.this.m1726lambda$checkUserName$23$orgtelegramuiChannelCreateActivity(name);
                }
            };
            this.checkRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 300L);
            return true;
        }
    }

    /* renamed from: lambda$checkUserName$23$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1726lambda$checkUserName$23$orgtelegramuiChannelCreateActivity(final String name) {
        TLRPC.TL_channels_checkUsername req = new TLRPC.TL_channels_checkUsername();
        req.username = name;
        req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda16
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelCreateActivity.this.m1725lambda$checkUserName$22$orgtelegramuiChannelCreateActivity(name, tLObject, tL_error);
            }
        }, 2);
    }

    /* renamed from: lambda$checkUserName$22$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1725lambda$checkUserName$22$orgtelegramuiChannelCreateActivity(final String name, final TLObject response, final TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ChannelCreateActivity.this.m1724lambda$checkUserName$21$orgtelegramuiChannelCreateActivity(name, error, response);
            }
        });
    }

    /* renamed from: lambda$checkUserName$21$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1724lambda$checkUserName$21$orgtelegramuiChannelCreateActivity(String name, TLRPC.TL_error error, TLObject response) {
        this.checkReqId = 0;
        String str = this.lastCheckName;
        if (str != null && str.equals(name)) {
            if (error == null && (response instanceof TLRPC.TL_boolTrue)) {
                this.checkTextView.setText(LocaleController.formatString("LinkAvailable", R.string.LinkAvailable, name));
                this.checkTextView.setTag(Theme.key_windowBackgroundWhiteGreenText);
                this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
                this.lastNameAvailable = true;
                return;
            }
            if (error != null && error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")) {
                this.canCreatePublic = false;
                showPremiumIncreaseLimitDialog();
            } else {
                this.checkTextView.setText(LocaleController.getString("LinkInUse", R.string.LinkInUse));
            }
            this.checkTextView.setTag(Theme.key_windowBackgroundWhiteRedText4);
            this.checkTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText4));
            this.lastNameAvailable = false;
        }
    }

    private void showErrorAlert(String error) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        char c = 65535;
        switch (error.hashCode()) {
            case 288843630:
                if (error.equals("USERNAME_INVALID")) {
                    c = 0;
                    break;
                }
                break;
            case 533175271:
                if (error.equals("USERNAME_OCCUPIED")) {
                    c = 1;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                builder.setMessage(LocaleController.getString("LinkInvalid", R.string.LinkInvalid));
                break;
            case 1:
                builder.setMessage(LocaleController.getString("LinkInUse", R.string.LinkInUse));
                break;
            default:
                builder.setMessage(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                break;
        }
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
        showDialog(builder.create());
    }

    private void showPremiumIncreaseLimitDialog() {
        if (getParentActivity() == null) {
            return;
        }
        LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(this, getParentActivity(), 2, this.currentAccount);
        limitReachedBottomSheet.parentIsChannel = true;
        limitReachedBottomSheet.onSuccessRunnable = new Runnable() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                ChannelCreateActivity.this.m1747x98bb9116();
            }
        };
        showDialog(limitReachedBottomSheet);
    }

    /* renamed from: lambda$showPremiumIncreaseLimitDialog$24$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1747x98bb9116() {
        this.canCreatePublic = true;
        updatePrivatePublic();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ChannelCreateActivity$$ExternalSyntheticLambda17
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ChannelCreateActivity.this.m1737xd508ee14();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundGray));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        themeDescriptions.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        themeDescriptions.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        themeDescriptions.add(new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8));
        themeDescriptions.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.headerCell2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
        themeDescriptions.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteRedText4));
        themeDescriptions.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGrayText8));
        themeDescriptions.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGreenText));
        themeDescriptions.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        themeDescriptions.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteRedText4));
        themeDescriptions.add(new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        themeDescriptions.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.privateContainer, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackground));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_radioBackgroundChecked));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        themeDescriptions.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        themeDescriptions.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
        themeDescriptions.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, Theme.avatarDrawables, cellDelegate, Theme.key_avatar_text));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$25$org-telegram-ui-ChannelCreateActivity */
    public /* synthetic */ void m1737xd508ee14() {
        LinearLayout linearLayout = this.adminnedChannelsLayout;
        if (linearLayout != null) {
            int count = linearLayout.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.adminnedChannelsLayout.getChildAt(a);
                if (child instanceof AdminedChannelCell) {
                    ((AdminedChannelCell) child).update();
                }
            }
        }
    }
}
