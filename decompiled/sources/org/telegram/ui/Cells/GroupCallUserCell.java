package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
/* loaded from: classes4.dex */
public class GroupCallUserCell extends FrameLayout {
    private AccountInstance accountInstance;
    private AnimatorSet animatorSet;
    private BackupImageView avatarImageView;
    private RadialProgressView avatarProgressView;
    private AvatarWavesDrawable avatarWavesDrawable;
    private ChatObject.Call currentCall;
    private TLRPC.Chat currentChat;
    private boolean currentIconGray;
    private int currentStatus;
    private TLRPC.User currentUser;
    private Paint dividerPaint;
    private SimpleTextView fullAboutTextView;
    private boolean hasAvatar;
    private boolean isSpeaking;
    private int lastMuteColor;
    private boolean lastMuted;
    private boolean lastRaisedHand;
    private RLottieImageView muteButton;
    private RLottieDrawable muteDrawable;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private TLRPC.TL_groupCallParticipant participant;
    private float progressToAvatarPreview;
    private long selfId;
    private RLottieDrawable shakeHandDrawable;
    private Drawable speakingDrawable;
    private boolean updateRunnableScheduled;
    private boolean updateVoiceRunnableScheduled;
    private SimpleTextView[] statusTextView = new SimpleTextView[5];
    private Runnable shakeHandCallback = new Runnable() { // from class: org.telegram.ui.Cells.GroupCallUserCell$$ExternalSyntheticLambda2
        @Override // java.lang.Runnable
        public final void run() {
            GroupCallUserCell.this.m1649lambda$new$0$orgtelegramuiCellsGroupCallUserCell();
        }
    };
    private Runnable raiseHandCallback = new Runnable() { // from class: org.telegram.ui.Cells.GroupCallUserCell$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            GroupCallUserCell.this.m1650lambda$new$1$orgtelegramuiCellsGroupCallUserCell();
        }
    };
    private String grayIconColor = Theme.key_voipgroup_mutedIcon;
    private Runnable checkRaiseRunnable = new Runnable() { // from class: org.telegram.ui.Cells.GroupCallUserCell$$ExternalSyntheticLambda4
        @Override // java.lang.Runnable
        public final void run() {
            GroupCallUserCell.this.m1651lambda$new$2$orgtelegramuiCellsGroupCallUserCell();
        }
    };
    private Runnable updateRunnable = new Runnable() { // from class: org.telegram.ui.Cells.GroupCallUserCell$$ExternalSyntheticLambda5
        @Override // java.lang.Runnable
        public final void run() {
            GroupCallUserCell.this.m1652lambda$new$3$orgtelegramuiCellsGroupCallUserCell();
        }
    };
    private Runnable updateVoiceRunnable = new Runnable() { // from class: org.telegram.ui.Cells.GroupCallUserCell$$ExternalSyntheticLambda6
        @Override // java.lang.Runnable
        public final void run() {
            GroupCallUserCell.this.m1653lambda$new$4$orgtelegramuiCellsGroupCallUserCell();
        }
    };
    private AvatarDrawable avatarDrawable = new AvatarDrawable();

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-GroupCallUserCell */
    public /* synthetic */ void m1649lambda$new$0$orgtelegramuiCellsGroupCallUserCell() {
        this.shakeHandDrawable.setOnFinishCallback(null, 0);
        this.muteDrawable.setOnFinishCallback(null, 0);
        this.muteButton.setAnimation(this.muteDrawable);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-GroupCallUserCell */
    public /* synthetic */ void m1650lambda$new$1$orgtelegramuiCellsGroupCallUserCell() {
        int endFrame;
        int startFrame;
        int num = Utilities.random.nextInt(100);
        if (num < 32) {
            startFrame = 0;
            endFrame = 120;
        } else if (num < 64) {
            startFrame = 120;
            endFrame = PsExtractor.VIDEO_STREAM_MASK;
        } else if (num < 97) {
            startFrame = PsExtractor.VIDEO_STREAM_MASK;
            endFrame = 420;
        } else if (num == 98) {
            startFrame = 420;
            endFrame = 540;
        } else {
            startFrame = 540;
            endFrame = 720;
        }
        this.shakeHandDrawable.setCustomEndFrame(endFrame);
        this.shakeHandDrawable.setOnFinishCallback(this.shakeHandCallback, endFrame - 1);
        this.muteButton.setAnimation(this.shakeHandDrawable);
        this.shakeHandDrawable.setCurrentFrame(startFrame);
        this.muteButton.playAnimation();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Cells-GroupCallUserCell */
    public /* synthetic */ void m1651lambda$new$2$orgtelegramuiCellsGroupCallUserCell() {
        applyParticipantChanges(true, true);
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Cells-GroupCallUserCell */
    public /* synthetic */ void m1652lambda$new$3$orgtelegramuiCellsGroupCallUserCell() {
        this.isSpeaking = false;
        applyParticipantChanges(true, true);
        this.avatarWavesDrawable.setAmplitude(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
        this.updateRunnableScheduled = false;
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Cells-GroupCallUserCell */
    public /* synthetic */ void m1653lambda$new$4$orgtelegramuiCellsGroupCallUserCell() {
        applyParticipantChanges(true, true);
        this.updateVoiceRunnableScheduled = false;
    }

    public void setProgressToAvatarPreview(float progressToAvatarPreview) {
        this.progressToAvatarPreview = progressToAvatarPreview;
        this.nameTextView.setTranslationX((LocaleController.isRTL ? AndroidUtilities.dp(53.0f) : -AndroidUtilities.dp(53.0f)) * progressToAvatarPreview);
        boolean z = true;
        if (isSelfUser() && progressToAvatarPreview > 0.0f) {
            this.fullAboutTextView.setTranslationX((LocaleController.isRTL ? -AndroidUtilities.dp(53.0f) : AndroidUtilities.dp(53.0f)) * (1.0f - progressToAvatarPreview));
            this.fullAboutTextView.setVisibility(0);
            this.fullAboutTextView.setAlpha(progressToAvatarPreview);
            this.statusTextView[4].setAlpha(1.0f - progressToAvatarPreview);
            SimpleTextView simpleTextView = this.statusTextView[4];
            boolean z2 = LocaleController.isRTL;
            int dp = AndroidUtilities.dp(53.0f);
            if (!z2) {
                dp = -dp;
            }
            simpleTextView.setTranslationX(dp * progressToAvatarPreview);
        } else {
            this.fullAboutTextView.setVisibility(8);
            int i = 0;
            while (true) {
                SimpleTextView[] simpleTextViewArr = this.statusTextView;
                if (i >= simpleTextViewArr.length) {
                    break;
                }
                if (!TextUtils.isEmpty(simpleTextViewArr[4].getText()) && this.statusTextView[4].getLineCount() > 1) {
                    this.statusTextView[i].setFullLayoutAdditionalWidth(AndroidUtilities.dp(92.0f), LocaleController.isRTL ? AndroidUtilities.dp(48.0f) : AndroidUtilities.dp(53.0f));
                    this.statusTextView[i].setFullAlpha(progressToAvatarPreview);
                    this.statusTextView[i].setTranslationX(0.0f);
                    this.statusTextView[i].invalidate();
                } else {
                    this.statusTextView[i].setTranslationX((LocaleController.isRTL ? AndroidUtilities.dp(53.0f) : -AndroidUtilities.dp(53.0f)) * progressToAvatarPreview);
                    this.statusTextView[i].setFullLayoutAdditionalWidth(0, 0);
                }
                i++;
            }
        }
        this.avatarImageView.setAlpha(progressToAvatarPreview == 0.0f ? 1.0f : 0.0f);
        AvatarWavesDrawable avatarWavesDrawable = this.avatarWavesDrawable;
        if (!this.isSpeaking || progressToAvatarPreview != 0.0f) {
            z = false;
        }
        avatarWavesDrawable.setShowWaves(z, this);
        this.muteButton.setAlpha(1.0f - progressToAvatarPreview);
        this.muteButton.setScaleX(((1.0f - progressToAvatarPreview) * 0.4f) + 0.6f);
        this.muteButton.setScaleY(((1.0f - progressToAvatarPreview) * 0.4f) + 0.6f);
        invalidate();
    }

    public AvatarWavesDrawable getAvatarWavesDrawable() {
        return this.avatarWavesDrawable;
    }

    public void setUploadProgress(float progress, boolean animated) {
        this.avatarProgressView.setProgress(progress);
        if (progress < 1.0f) {
            AndroidUtilities.updateViewVisibilityAnimated(this.avatarProgressView, true, 1.0f, animated);
        } else {
            AndroidUtilities.updateViewVisibilityAnimated(this.avatarProgressView, false, 1.0f, animated);
        }
    }

    public void setDrawAvatar(boolean draw) {
        if (this.avatarImageView.getImageReceiver().getVisible() != draw) {
            this.avatarImageView.getImageReceiver().setVisible(draw, true);
        }
    }

    /* loaded from: classes4.dex */
    public static class VerifiedDrawable extends Drawable {
        private Drawable[] drawables;

        public VerifiedDrawable(Context context) {
            Drawable[] drawableArr = new Drawable[2];
            this.drawables = drawableArr;
            drawableArr[0] = context.getResources().getDrawable(R.drawable.verified_area).mutate();
            this.drawables[0].setColorFilter(new PorterDuffColorFilter(-9063442, PorterDuff.Mode.MULTIPLY));
            this.drawables[1] = context.getResources().getDrawable(R.drawable.verified_check).mutate();
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return this.drawables[0].getIntrinsicWidth();
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return this.drawables[0].getIntrinsicHeight();
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            int a = 0;
            while (true) {
                Drawable[] drawableArr = this.drawables;
                if (a < drawableArr.length) {
                    drawableArr[a].setBounds(getBounds());
                    this.drawables[a].draw(canvas);
                    a++;
                } else {
                    return;
                }
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            int a = 0;
            while (true) {
                Drawable[] drawableArr = this.drawables;
                if (a < drawableArr.length) {
                    drawableArr[a].setAlpha(alpha);
                    a++;
                } else {
                    return;
                }
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }
    }

    public GroupCallUserCell(Context context) {
        super(context);
        int i = 5;
        Paint paint = new Paint();
        this.dividerPaint = paint;
        paint.setColor(Theme.getColor(Theme.key_voipgroup_actionBar));
        setClipChildren(false);
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        addView(this.avatarImageView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 11.0f, 6.0f, LocaleController.isRTL ? 11.0f : 0.0f, 0.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context) { // from class: org.telegram.ui.Cells.GroupCallUserCell.1
            private Paint paint;

            {
                GroupCallUserCell.this = this;
                Paint paint2 = new Paint(1);
                this.paint = paint2;
                paint2.setColor(1426063360);
            }

            @Override // org.telegram.ui.Components.RadialProgressView, android.view.View
            public void onDraw(Canvas canvas) {
                if (GroupCallUserCell.this.avatarImageView.getImageReceiver().hasNotThumb() && GroupCallUserCell.this.avatarImageView.getAlpha() > 0.0f) {
                    this.paint.setAlpha((int) (GroupCallUserCell.this.avatarImageView.getImageReceiver().getCurrentAlpha() * 85.0f * GroupCallUserCell.this.avatarImageView.getAlpha()));
                    canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredWidth() / 2.0f, this.paint);
                }
                GroupCallUserCell.this.avatarProgressView.setProgressColor(ColorUtils.setAlphaComponent(-1, (int) (GroupCallUserCell.this.avatarImageView.getImageReceiver().getCurrentAlpha() * 255.0f * GroupCallUserCell.this.avatarImageView.getAlpha())));
                super.onDraw(canvas);
            }
        };
        this.avatarProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(26.0f));
        this.avatarProgressView.setProgressColor(-1);
        this.avatarProgressView.setNoProgress(false);
        addView(this.avatarProgressView, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 11.0f, 6.0f, LocaleController.isRTL ? 11.0f : 0.0f, 0.0f));
        AndroidUtilities.updateViewVisibilityAnimated(this.avatarProgressView, false, 1.0f, false);
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_voipgroup_nameText));
        this.nameTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.nameTextView.setTextSize(16);
        this.nameTextView.setDrawablePadding(AndroidUtilities.dp(6.0f));
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 54.0f : 67.0f, 10.0f, LocaleController.isRTL ? 67.0f : 54.0f, 0.0f));
        Drawable drawable = context.getResources().getDrawable(R.drawable.voice_volume_mini);
        this.speakingDrawable = drawable;
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_voipgroup_speakingText), PorterDuff.Mode.MULTIPLY));
        int a = 0;
        while (true) {
            SimpleTextView[] simpleTextViewArr = this.statusTextView;
            if (a >= simpleTextViewArr.length) {
                break;
            }
            final int num = a;
            simpleTextViewArr[a] = new SimpleTextView(context) { // from class: org.telegram.ui.Cells.GroupCallUserCell.2
                float originalAlpha;

                @Override // android.view.View
                public void setAlpha(float alpha) {
                    this.originalAlpha = alpha;
                    if (num == 4) {
                        float alphaOverride = GroupCallUserCell.this.statusTextView[4].getFullAlpha();
                        if (GroupCallUserCell.this.isSelfUser() && GroupCallUserCell.this.progressToAvatarPreview > 0.0f) {
                            super.setAlpha(1.0f - GroupCallUserCell.this.progressToAvatarPreview);
                            return;
                        } else if (alphaOverride > 0.0f) {
                            super.setAlpha(Math.max(alpha, alphaOverride));
                            return;
                        } else {
                            super.setAlpha(alpha);
                            return;
                        }
                    }
                    super.setAlpha(alpha * (1.0f - GroupCallUserCell.this.statusTextView[4].getFullAlpha()));
                }

                @Override // android.view.View
                public void setTranslationY(float translationY) {
                    if (num == 4 && getFullAlpha() > 0.0f) {
                        translationY = 0.0f;
                    }
                    super.setTranslationY(translationY);
                }

                @Override // android.view.View
                public float getAlpha() {
                    return this.originalAlpha;
                }

                @Override // org.telegram.ui.ActionBar.SimpleTextView
                public void setFullAlpha(float value) {
                    super.setFullAlpha(value);
                    for (int a2 = 0; a2 < GroupCallUserCell.this.statusTextView.length; a2++) {
                        GroupCallUserCell.this.statusTextView[a2].setAlpha(GroupCallUserCell.this.statusTextView[a2].getAlpha());
                    }
                }
            };
            this.statusTextView[a].setTextSize(15);
            this.statusTextView[a].setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            if (a != 4) {
                if (a == 0) {
                    this.statusTextView[a].setTextColor(Theme.getColor(Theme.key_voipgroup_listeningText));
                    this.statusTextView[a].setText(LocaleController.getString("Listening", R.string.Listening));
                } else if (a == 1) {
                    this.statusTextView[a].setTextColor(Theme.getColor(Theme.key_voipgroup_speakingText));
                    this.statusTextView[a].setText(LocaleController.getString("Speaking", R.string.Speaking));
                    this.statusTextView[a].setDrawablePadding(AndroidUtilities.dp(2.0f));
                } else if (a == 2) {
                    this.statusTextView[a].setTextColor(Theme.getColor(Theme.key_voipgroup_mutedByAdminIcon));
                    this.statusTextView[a].setText(LocaleController.getString("VoipGroupMutedForMe", R.string.VoipGroupMutedForMe));
                } else if (a == 3) {
                    this.statusTextView[a].setTextColor(Theme.getColor(Theme.key_voipgroup_listeningText));
                    this.statusTextView[a].setText(LocaleController.getString("WantsToSpeak", R.string.WantsToSpeak));
                }
                addView(this.statusTextView[a], LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 54.0f : 67.0f, 32.0f, LocaleController.isRTL ? 67.0f : 54.0f, 0.0f));
            } else {
                this.statusTextView[a].setBuildFullLayout(true);
                this.statusTextView[a].setTextColor(Theme.getColor(Theme.key_voipgroup_mutedIcon));
                addView(this.statusTextView[a], LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 54.0f : 67.0f, 32.0f, LocaleController.isRTL ? 67.0f : 54.0f, 0.0f));
            }
            a++;
        }
        SimpleTextView simpleTextView2 = new SimpleTextView(context);
        this.fullAboutTextView = simpleTextView2;
        simpleTextView2.setMaxLines(3);
        this.fullAboutTextView.setTextSize(15);
        this.fullAboutTextView.setTextColor(Theme.getColor(Theme.key_voipgroup_mutedIcon));
        this.fullAboutTextView.setVisibility(8);
        addView(this.fullAboutTextView, LayoutHelper.createFrame(-1, 60.0f, (LocaleController.isRTL ? 5 : 3) | 48, 14.0f, 32.0f, 14.0f, 0.0f));
        this.muteDrawable = new RLottieDrawable(R.raw.voice_outlined2, "2131558579", AndroidUtilities.dp(34.0f), AndroidUtilities.dp(32.0f), true, null);
        this.shakeHandDrawable = new RLottieDrawable(R.raw.hand_1, "2131558458", AndroidUtilities.dp(34.0f), AndroidUtilities.dp(32.0f), true, null);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.muteButton = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setAnimation(this.muteDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            RippleDrawable rippleDrawable = (RippleDrawable) Theme.createSelectorDrawable(Theme.getColor(this.grayIconColor) & 620756991);
            Theme.setRippleDrawableForceSoftware(rippleDrawable);
            this.muteButton.setBackground(rippleDrawable);
        }
        this.muteButton.setImportantForAccessibility(2);
        addView(this.muteButton, LayoutHelper.createFrame(48, -1.0f, (LocaleController.isRTL ? 3 : i) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        this.muteButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Cells.GroupCallUserCell$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallUserCell.this.m1654lambda$new$5$orgtelegramuiCellsGroupCallUserCell(view);
            }
        });
        this.avatarWavesDrawable = new AvatarWavesDrawable(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(29.0f));
        setWillNotDraw(false);
        setFocusable(true);
    }

    /* renamed from: onMuteClick */
    public void m1654lambda$new$5$orgtelegramuiCellsGroupCallUserCell(GroupCallUserCell cell) {
    }

    public int getClipHeight() {
        SimpleTextView aboutTextView;
        if (!TextUtils.isEmpty(this.fullAboutTextView.getText()) && this.hasAvatar) {
            aboutTextView = this.fullAboutTextView;
        } else {
            aboutTextView = this.statusTextView[4];
        }
        int lineCount = aboutTextView.getLineCount();
        if (lineCount > 1) {
            int h = aboutTextView.getTextHeight();
            return aboutTextView.getTop() + h + AndroidUtilities.dp(8.0f);
        }
        int h2 = getMeasuredHeight();
        return h2;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.updateRunnableScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            this.updateRunnableScheduled = false;
        }
        if (this.updateVoiceRunnableScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.updateVoiceRunnable);
            this.updateVoiceRunnableScheduled = false;
        }
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
    }

    public boolean isSelfUser() {
        if (this.selfId > 0) {
            TLRPC.User user = this.currentUser;
            return user != null && user.id == this.selfId;
        }
        TLRPC.Chat chat = this.currentChat;
        return chat != null && chat.id == (-this.selfId);
    }

    public boolean isHandRaised() {
        return this.lastRaisedHand;
    }

    public CharSequence getName() {
        return this.nameTextView.getText();
    }

    public boolean hasAvatarSet() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    public void setData(AccountInstance account, TLRPC.TL_groupCallParticipant groupCallParticipant, ChatObject.Call call, long self, TLRPC.FileLocation uploadingAvatar, boolean animated) {
        this.currentCall = call;
        this.accountInstance = account;
        this.selfId = self;
        this.participant = groupCallParticipant;
        long id = MessageObject.getPeerId(groupCallParticipant.peer);
        boolean z = false;
        if (id > 0) {
            TLRPC.User user = this.accountInstance.getMessagesController().getUser(Long.valueOf(id));
            this.currentUser = user;
            this.currentChat = null;
            this.avatarDrawable.setInfo(user);
            this.nameTextView.setText(UserObject.getUserName(this.currentUser));
            SimpleTextView simpleTextView = this.nameTextView;
            TLRPC.User user2 = this.currentUser;
            simpleTextView.setRightDrawable((user2 == null || !user2.verified) ? null : new VerifiedDrawable(getContext()));
            this.avatarImageView.getImageReceiver().setCurrentAccount(account.getCurrentAccount());
            if (uploadingAvatar != null) {
                this.hasAvatar = true;
                this.avatarImageView.setImage(ImageLocation.getForLocal(uploadingAvatar), "50_50", this.avatarDrawable, (Object) null);
            } else {
                ImageLocation imageLocation = ImageLocation.getForUser(this.currentUser, 1);
                if (imageLocation != null) {
                    z = true;
                }
                this.hasAvatar = z;
                this.avatarImageView.setImage(imageLocation, "50_50", this.avatarDrawable, this.currentUser);
            }
        } else {
            TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-id));
            this.currentChat = chat;
            this.currentUser = null;
            this.avatarDrawable.setInfo(chat);
            TLRPC.Chat chat2 = this.currentChat;
            if (chat2 != null) {
                this.nameTextView.setText(chat2.title);
                this.nameTextView.setRightDrawable(this.currentChat.verified ? new VerifiedDrawable(getContext()) : null);
                this.avatarImageView.getImageReceiver().setCurrentAccount(account.getCurrentAccount());
                if (uploadingAvatar != null) {
                    this.hasAvatar = true;
                    this.avatarImageView.setImage(ImageLocation.getForLocal(uploadingAvatar), "50_50", this.avatarDrawable, (Object) null);
                } else {
                    ImageLocation imageLocation2 = ImageLocation.getForChat(this.currentChat, 1);
                    if (imageLocation2 != null) {
                        z = true;
                    }
                    this.hasAvatar = z;
                    this.avatarImageView.setImage(imageLocation2, "50_50", this.avatarDrawable, this.currentChat);
                }
            }
        }
        applyParticipantChanges(animated);
    }

    public void setDrawDivider(boolean draw) {
        this.needDivider = draw;
        invalidate();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        applyParticipantChanges(false);
    }

    public TLRPC.TL_groupCallParticipant getParticipant() {
        return this.participant;
    }

    public void setAmplitude(double value) {
        if (value > 1.5d) {
            if (this.updateRunnableScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            }
            if (!this.isSpeaking) {
                this.isSpeaking = true;
                applyParticipantChanges(true);
            }
            this.avatarWavesDrawable.setAmplitude(value);
            AndroidUtilities.runOnUIThread(this.updateRunnable, 500L);
            this.updateRunnableScheduled = true;
            return;
        }
        this.avatarWavesDrawable.setAmplitude(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
    }

    public boolean clickMuteButton() {
        if (this.muteButton.isEnabled()) {
            this.muteButton.callOnClick();
            return true;
        }
        return false;
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), C.BUFFER_FLAG_ENCRYPTED));
    }

    public void applyParticipantChanges(boolean animated) {
        applyParticipantChanges(animated, false);
    }

    public void setGrayIconColor(String key, int value) {
        if (!this.grayIconColor.equals(key)) {
            if (this.currentIconGray) {
                this.lastMuteColor = Theme.getColor(key);
            }
            this.grayIconColor = key;
        }
        if (this.currentIconGray) {
            this.muteButton.setColorFilter(new PorterDuffColorFilter(value, PorterDuff.Mode.MULTIPLY));
            Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), 620756991 & value, true);
        }
    }

    public void setAboutVisibleProgress(int color, float progress) {
        if (TextUtils.isEmpty(this.statusTextView[4].getText())) {
            progress = 0.0f;
        }
        this.statusTextView[4].setFullAlpha(progress);
        this.statusTextView[4].setFullLayoutAdditionalWidth(0, 0);
        invalidate();
    }

    public void setAboutVisible(boolean visible) {
        if (visible) {
            this.statusTextView[4].setTranslationY(0.0f);
        } else {
            this.statusTextView[4].setFullAlpha(0.0f);
        }
        invalidate();
    }

    private void applyParticipantChanges(boolean animated, boolean internal) {
        boolean hasVoice;
        boolean newMuted;
        final int newStatus;
        final int newMuteColor;
        int i;
        boolean z;
        boolean changed;
        float f;
        char c;
        char c2;
        if (this.currentCall != null) {
            this.muteButton.setEnabled(!isSelfUser() || this.participant.raise_hand_rating != 0);
            if (SystemClock.elapsedRealtime() - this.participant.lastVoiceUpdateTime < 500) {
                hasVoice = this.participant.hasVoiceDelayed;
            } else {
                hasVoice = this.participant.hasVoice;
            }
            if (!internal) {
                long diff = SystemClock.uptimeMillis() - this.participant.lastSpeakTime;
                boolean newSpeaking = diff < 500;
                if (!this.isSpeaking || !newSpeaking || hasVoice) {
                    this.isSpeaking = newSpeaking;
                    if (this.updateRunnableScheduled) {
                        AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
                        this.updateRunnableScheduled = false;
                    }
                    if (this.isSpeaking) {
                        AndroidUtilities.runOnUIThread(this.updateRunnable, 500 - diff);
                        this.updateRunnableScheduled = true;
                    }
                }
            }
            TLRPC.TL_groupCallParticipant newParticipant = this.currentCall.participants.get(MessageObject.getPeerId(this.participant.peer));
            if (newParticipant != null) {
                this.participant = newParticipant;
            }
            ArrayList<Animator> animators = null;
            boolean newRaisedHand = false;
            boolean myted_by_me = this.participant.muted_by_you && !isSelfUser();
            if (isSelfUser()) {
                newMuted = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute() && (!this.isSpeaking || !hasVoice);
            } else {
                newMuted = (this.participant.muted && (!this.isSpeaking || !hasVoice)) || myted_by_me;
            }
            if (!newMuted || this.participant.can_self_unmute) {
            }
            boolean hasAbout = !TextUtils.isEmpty(this.participant.about);
            this.currentIconGray = false;
            AndroidUtilities.cancelRunOnUIThread(this.checkRaiseRunnable);
            if ((this.participant.muted && !this.isSpeaking) || myted_by_me) {
                if (!this.participant.can_self_unmute || myted_by_me) {
                    boolean z2 = !this.participant.can_self_unmute && this.participant.raise_hand_rating != 0;
                    newRaisedHand = z2;
                    if (z2) {
                        int newMuteColor2 = Theme.getColor(Theme.key_voipgroup_listeningText);
                        long time = SystemClock.elapsedRealtime() - this.participant.lastRaiseHandDate;
                        if (this.participant.lastRaiseHandDate == 0 || time > DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                            newStatus = myted_by_me ? 2 : hasAbout ? 4 : 0;
                        } else {
                            AndroidUtilities.runOnUIThread(this.checkRaiseRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS - time);
                            newStatus = 3;
                        }
                        newMuteColor = newMuteColor2;
                    } else {
                        newMuteColor = Theme.getColor(Theme.key_voipgroup_mutedByAdminIcon);
                        newStatus = myted_by_me ? 2 : hasAbout ? 4 : 0;
                    }
                } else {
                    newMuteColor = Theme.getColor(this.grayIconColor);
                    this.currentIconGray = true;
                    newStatus = hasAbout ? 4 : 0;
                }
            } else if (this.isSpeaking && hasVoice) {
                newMuteColor = Theme.getColor(Theme.key_voipgroup_speakingText);
                newStatus = 1;
            } else {
                newMuteColor = Theme.getColor(this.grayIconColor);
                newStatus = hasAbout ? 4 : 0;
                this.currentIconGray = true;
            }
            if (!isSelfUser()) {
                this.statusTextView[4].setTextColor(Theme.getColor(this.grayIconColor));
            }
            if (isSelfUser()) {
                if (!hasAbout && !this.hasAvatar) {
                    if (this.currentUser != null) {
                        c2 = 4;
                        this.statusTextView[4].setText(LocaleController.getString("TapToAddPhotoOrBio", R.string.TapToAddPhotoOrBio));
                    } else {
                        c2 = 4;
                        this.statusTextView[4].setText(LocaleController.getString("TapToAddPhotoOrDescription", R.string.TapToAddPhotoOrDescription));
                    }
                    this.statusTextView[c2].setTextColor(Theme.getColor(this.grayIconColor));
                } else if (hasAbout) {
                    if (!this.hasAvatar) {
                        this.statusTextView[4].setText(LocaleController.getString("TapToAddPhoto", R.string.TapToAddPhoto));
                        this.statusTextView[4].setTextColor(Theme.getColor(this.grayIconColor));
                    } else {
                        this.statusTextView[4].setText(LocaleController.getString("ThisIsYou", R.string.ThisIsYou));
                        this.statusTextView[4].setTextColor(Theme.getColor(Theme.key_voipgroup_listeningText));
                    }
                } else {
                    if (this.currentUser != null) {
                        c = 4;
                        this.statusTextView[4].setText(LocaleController.getString("TapToAddBio", R.string.TapToAddBio));
                    } else {
                        c = 4;
                        this.statusTextView[4].setText(LocaleController.getString("TapToAddDescription", R.string.TapToAddDescription));
                    }
                    this.statusTextView[c].setTextColor(Theme.getColor(this.grayIconColor));
                }
                if (hasAbout) {
                    this.fullAboutTextView.setText(AndroidUtilities.replaceNewLines(this.participant.about));
                    this.fullAboutTextView.setTextColor(Theme.getColor(Theme.key_voipgroup_mutedIcon));
                } else {
                    this.fullAboutTextView.setText(this.statusTextView[newStatus].getText());
                    this.fullAboutTextView.setTextColor(this.statusTextView[newStatus].getTextColor());
                }
            } else if (hasAbout) {
                this.statusTextView[4].setText(AndroidUtilities.replaceNewLines(this.participant.about));
                this.fullAboutTextView.setText("");
            } else {
                this.statusTextView[4].setText("");
                this.fullAboutTextView.setText("");
            }
            boolean somethingChanged = false;
            AnimatorSet animatorSet = this.animatorSet;
            if (animatorSet != null && (newStatus != this.currentStatus || this.lastMuteColor != newMuteColor)) {
                somethingChanged = true;
            }
            if ((!animated || somethingChanged) && animatorSet != null) {
                animatorSet.cancel();
                this.animatorSet = null;
            }
            if (animated && this.lastMuteColor == newMuteColor && !somethingChanged) {
                i = 1;
            } else if (animated) {
                animators = new ArrayList<>();
                final int oldColor = this.lastMuteColor;
                this.lastMuteColor = newMuteColor;
                ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.GroupCallUserCell$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        GroupCallUserCell.this.m1648xf10dd947(oldColor, newMuteColor, valueAnimator);
                    }
                });
                animators.add(animator);
                i = 1;
            } else {
                RLottieImageView rLottieImageView = this.muteButton;
                this.lastMuteColor = newMuteColor;
                rLottieImageView.setColorFilter(new PorterDuffColorFilter(newMuteColor, PorterDuff.Mode.MULTIPLY));
                i = 1;
                Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), 620756991 & newMuteColor, true);
            }
            if (newStatus == i) {
                int vol = ChatObject.getParticipantVolume(this.participant);
                int volume = vol / 100;
                if (volume != 100) {
                    this.statusTextView[1].setLeftDrawable(this.speakingDrawable);
                    SimpleTextView simpleTextView = this.statusTextView[1];
                    Object[] objArr = new Object[1];
                    objArr[0] = Integer.valueOf(vol < 100 ? 1 : volume);
                    simpleTextView.setText(LocaleController.formatString("SpeakingWithVolume", R.string.SpeakingWithVolume, objArr));
                } else {
                    this.statusTextView[1].setLeftDrawable((Drawable) null);
                    this.statusTextView[1].setText(LocaleController.getString("Speaking", R.string.Speaking));
                }
            }
            if (isSelfUser()) {
                applyStatus(4);
            } else if (!animated || newStatus != this.currentStatus || somethingChanged) {
                if (animated) {
                    if (animators == null) {
                        animators = new ArrayList<>();
                    }
                    if (newStatus == 0) {
                        int a = 0;
                        while (true) {
                            SimpleTextView[] simpleTextViewArr = this.statusTextView;
                            if (a >= simpleTextViewArr.length) {
                                break;
                            }
                            SimpleTextView simpleTextView2 = simpleTextViewArr[a];
                            Property property = View.TRANSLATION_Y;
                            float[] fArr = new float[1];
                            fArr[0] = a == newStatus ? 0.0f : AndroidUtilities.dp(-2.0f);
                            animators.add(ObjectAnimator.ofFloat(simpleTextView2, property, fArr));
                            SimpleTextView simpleTextView3 = this.statusTextView[a];
                            Property property2 = View.ALPHA;
                            float[] fArr2 = new float[1];
                            fArr2[0] = a == newStatus ? 1.0f : 0.0f;
                            animators.add(ObjectAnimator.ofFloat(simpleTextView3, property2, fArr2));
                            a++;
                        }
                    } else {
                        int a2 = 0;
                        while (true) {
                            SimpleTextView[] simpleTextViewArr2 = this.statusTextView;
                            if (a2 >= simpleTextViewArr2.length) {
                                break;
                            }
                            SimpleTextView simpleTextView4 = simpleTextViewArr2[a2];
                            Property property3 = View.TRANSLATION_Y;
                            float[] fArr3 = new float[1];
                            if (a2 == newStatus) {
                                f = 0.0f;
                            } else {
                                f = AndroidUtilities.dp(a2 == 0 ? 2.0f : -2.0f);
                            }
                            fArr3[0] = f;
                            animators.add(ObjectAnimator.ofFloat(simpleTextView4, property3, fArr3));
                            SimpleTextView simpleTextView5 = this.statusTextView[a2];
                            Property property4 = View.ALPHA;
                            float[] fArr4 = new float[1];
                            fArr4[0] = a2 == newStatus ? 1.0f : 0.0f;
                            animators.add(ObjectAnimator.ofFloat(simpleTextView5, property4, fArr4));
                            a2++;
                        }
                    }
                } else {
                    applyStatus(newStatus);
                }
                this.currentStatus = newStatus;
            }
            this.avatarWavesDrawable.setMuted(newStatus, animated);
            if (animators != null) {
                AnimatorSet animatorSet2 = this.animatorSet;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.animatorSet = animatorSet3;
                animatorSet3.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.GroupCallUserCell.3
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (!GroupCallUserCell.this.isSelfUser()) {
                            GroupCallUserCell.this.applyStatus(newStatus);
                        }
                        GroupCallUserCell.this.animatorSet = null;
                    }
                });
                this.animatorSet.playTogether(animators);
                this.animatorSet.setDuration(180L);
                this.animatorSet.start();
            }
            if (animated && this.lastMuted == newMuted && this.lastRaisedHand == newRaisedHand) {
                z = false;
            } else {
                if (!newRaisedHand) {
                    this.muteButton.setAnimation(this.muteDrawable);
                    this.muteDrawable.setOnFinishCallback(null, 0);
                    if (newMuted && this.lastRaisedHand) {
                        changed = this.muteDrawable.setCustomEndFrame(21);
                    } else {
                        changed = this.muteDrawable.setCustomEndFrame(newMuted ? 64 : 42);
                    }
                } else {
                    changed = this.muteDrawable.setCustomEndFrame(84);
                    if (animated) {
                        this.muteDrawable.setOnFinishCallback(this.raiseHandCallback, 83);
                    } else {
                        this.muteDrawable.setOnFinishCallback(null, 0);
                    }
                }
                if (animated) {
                    if (changed) {
                        if (newStatus == 3) {
                            this.muteDrawable.setCurrentFrame(63);
                        } else if (newMuted && this.lastRaisedHand && !newRaisedHand) {
                            this.muteDrawable.setCurrentFrame(0);
                        } else if (!newMuted) {
                            this.muteDrawable.setCurrentFrame(21);
                        } else {
                            this.muteDrawable.setCurrentFrame(43);
                        }
                    }
                    this.muteButton.playAnimation();
                    z = false;
                } else {
                    RLottieDrawable rLottieDrawable = this.muteDrawable;
                    z = false;
                    rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
                    this.muteButton.invalidate();
                }
                this.lastMuted = newMuted;
                this.lastRaisedHand = newRaisedHand;
            }
            if (!this.isSpeaking) {
                this.avatarWavesDrawable.setAmplitude(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
            }
            AvatarWavesDrawable avatarWavesDrawable = this.avatarWavesDrawable;
            if (this.isSpeaking && this.progressToAvatarPreview == 0.0f) {
                z = true;
            }
            avatarWavesDrawable.setShowWaves(z, this);
        }
    }

    /* renamed from: lambda$applyParticipantChanges$6$org-telegram-ui-Cells-GroupCallUserCell */
    public /* synthetic */ void m1648xf10dd947(int oldColor, int newMuteColor, ValueAnimator animation) {
        float value = animation.getAnimatedFraction();
        int color = AndroidUtilities.getOffsetColor(oldColor, newMuteColor, value, 1.0f);
        this.muteButton.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
        Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), 620756991 & color, true);
    }

    public void applyStatus(int newStatus) {
        float f;
        if (newStatus == 0) {
            int a = 0;
            while (true) {
                SimpleTextView[] simpleTextViewArr = this.statusTextView;
                if (a < simpleTextViewArr.length) {
                    simpleTextViewArr[a].setTranslationY(a == newStatus ? 0.0f : AndroidUtilities.dp(-2.0f));
                    this.statusTextView[a].setAlpha(a == newStatus ? 1.0f : 0.0f);
                    a++;
                } else {
                    return;
                }
            }
        } else {
            int a2 = 0;
            while (true) {
                SimpleTextView[] simpleTextViewArr2 = this.statusTextView;
                if (a2 < simpleTextViewArr2.length) {
                    SimpleTextView simpleTextView = simpleTextViewArr2[a2];
                    if (a2 == newStatus) {
                        f = 0.0f;
                    } else {
                        f = AndroidUtilities.dp(a2 == 0 ? 2.0f : -2.0f);
                    }
                    simpleTextView.setTranslationY(f);
                    this.statusTextView[a2].setAlpha(a2 == newStatus ? 1.0f : 0.0f);
                    a2++;
                } else {
                    return;
                }
            }
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.needDivider) {
            float f = this.progressToAvatarPreview;
            if (f != 0.0f) {
                this.dividerPaint.setAlpha((int) ((1.0f - f) * 255.0f));
            } else {
                this.dividerPaint.setAlpha((int) ((1.0f - this.statusTextView[4].getFullAlpha()) * 255.0f));
            }
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(68.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0), getMeasuredHeight() - 1, this.dividerPaint);
        }
        int cx = this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2);
        int cy = this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2);
        this.avatarWavesDrawable.update();
        if (this.progressToAvatarPreview == 0.0f) {
            this.avatarWavesDrawable.draw(canvas, cx, cy, this);
        }
        this.avatarImageView.setScaleX(this.avatarWavesDrawable.getAvatarScale());
        this.avatarImageView.setScaleY(this.avatarWavesDrawable.getAvatarScale());
        this.avatarProgressView.setScaleX(this.avatarWavesDrawable.getAvatarScale());
        this.avatarProgressView.setScaleY(this.avatarWavesDrawable.getAvatarScale());
        super.dispatchDraw(canvas);
    }

    public void getAvatarPosition(int[] pos) {
        this.avatarImageView.getLocationInWindow(pos);
    }

    /* loaded from: classes4.dex */
    public static class AvatarWavesDrawable {
        float amplitude;
        float animateAmplitudeDiff;
        float animateToAmplitude;
        private boolean hasCustomColor;
        private int isMuted;
        boolean showWaves;
        float wavesEnter = 0.0f;
        private float progressToMuted = 0.0f;
        boolean invalidateColor = true;
        private BlobDrawable blobDrawable = new BlobDrawable(6);
        private BlobDrawable blobDrawable2 = new BlobDrawable(8);

        public AvatarWavesDrawable(int minRadius, int maxRadius) {
            this.blobDrawable.minRadius = minRadius;
            this.blobDrawable.maxRadius = maxRadius;
            this.blobDrawable2.minRadius = minRadius;
            this.blobDrawable2.maxRadius = maxRadius;
            this.blobDrawable.generateBlob();
            this.blobDrawable2.generateBlob();
            this.blobDrawable.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_speakingText), 38));
            this.blobDrawable2.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_speakingText), 38));
        }

        public void update() {
            float f = this.animateToAmplitude;
            float f2 = this.amplitude;
            if (f != f2) {
                float f3 = this.animateAmplitudeDiff;
                float f4 = f2 + (16.0f * f3);
                this.amplitude = f4;
                if (f3 > 0.0f) {
                    if (f4 > f) {
                        this.amplitude = f;
                    }
                } else if (f4 < f) {
                    this.amplitude = f;
                }
            }
            boolean z = this.showWaves;
            if (z) {
                float f5 = this.wavesEnter;
                if (f5 != 1.0f) {
                    float f6 = f5 + 0.045714285f;
                    this.wavesEnter = f6;
                    if (f6 > 1.0f) {
                        this.wavesEnter = 1.0f;
                        return;
                    }
                    return;
                }
            }
            if (!z) {
                float f7 = this.wavesEnter;
                if (f7 != 0.0f) {
                    float f8 = f7 - 0.045714285f;
                    this.wavesEnter = f8;
                    if (f8 < 0.0f) {
                        this.wavesEnter = 0.0f;
                    }
                }
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:25:0x005f  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void draw(android.graphics.Canvas r10, float r11, float r12, android.view.View r13) {
            /*
                r9 = this;
                float r0 = r9.amplitude
                r1 = 1053609165(0x3ecccccd, float:0.4)
                float r0 = r0 * r1
                r1 = 1061997773(0x3f4ccccd, float:0.8)
                float r0 = r0 + r1
                boolean r1 = r9.showWaves
                r2 = 0
                if (r1 != 0) goto L16
                float r1 = r9.wavesEnter
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto La7
            L16:
                r10.save()
                org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
                float r3 = r9.wavesEnter
                float r1 = r1.getInterpolation(r3)
                float r3 = r0 * r1
                float r4 = r0 * r1
                r10.scale(r3, r4, r11, r12)
                boolean r3 = r9.hasCustomColor
                r4 = 1065353216(0x3f800000, float:1.0)
                if (r3 != 0) goto L86
                int r3 = r9.isMuted
                r5 = 1037726734(0x3dda740e, float:0.10666667)
                r6 = 1
                if (r3 == r6) goto L48
                float r7 = r9.progressToMuted
                int r8 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
                if (r8 == 0) goto L48
                float r7 = r7 + r5
                r9.progressToMuted = r7
                int r3 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1))
                if (r3 <= 0) goto L45
                r9.progressToMuted = r4
            L45:
                r9.invalidateColor = r6
                goto L5b
            L48:
                if (r3 != r6) goto L5b
                float r3 = r9.progressToMuted
                int r7 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
                if (r7 == 0) goto L5b
                float r3 = r3 - r5
                r9.progressToMuted = r3
                int r3 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
                if (r3 >= 0) goto L59
                r9.progressToMuted = r2
            L59:
                r9.invalidateColor = r6
            L5b:
                boolean r3 = r9.invalidateColor
                if (r3 == 0) goto L86
                java.lang.String r3 = "voipgroup_speakingText"
                int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
                int r5 = r9.isMuted
                r6 = 2
                if (r5 != r6) goto L6d
                java.lang.String r5 = "voipgroup_mutedByAdminIcon"
                goto L6f
            L6d:
                java.lang.String r5 = "voipgroup_listeningText"
            L6f:
                int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                float r6 = r9.progressToMuted
                int r3 = androidx.core.graphics.ColorUtils.blendARGB(r3, r5, r6)
                org.telegram.ui.Components.BlobDrawable r5 = r9.blobDrawable
                android.graphics.Paint r5 = r5.paint
                r6 = 38
                int r6 = androidx.core.graphics.ColorUtils.setAlphaComponent(r3, r6)
                r5.setColor(r6)
            L86:
                org.telegram.ui.Components.BlobDrawable r3 = r9.blobDrawable
                float r5 = r9.amplitude
                r3.update(r5, r4)
                org.telegram.ui.Components.BlobDrawable r3 = r9.blobDrawable
                android.graphics.Paint r5 = r3.paint
                r3.draw(r11, r12, r10, r5)
                org.telegram.ui.Components.BlobDrawable r3 = r9.blobDrawable2
                float r5 = r9.amplitude
                r3.update(r5, r4)
                org.telegram.ui.Components.BlobDrawable r3 = r9.blobDrawable2
                org.telegram.ui.Components.BlobDrawable r4 = r9.blobDrawable
                android.graphics.Paint r4 = r4.paint
                r3.draw(r11, r12, r10, r4)
                r10.restore()
            La7:
                float r1 = r9.wavesEnter
                int r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1))
                if (r1 == 0) goto Lb0
                r13.invalidate()
            Lb0:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.GroupCallUserCell.AvatarWavesDrawable.draw(android.graphics.Canvas, float, float, android.view.View):void");
        }

        public float getAvatarScale() {
            float scaleAvatar = (this.amplitude * 0.2f) + 0.9f;
            float wavesEnter = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.wavesEnter);
            return (scaleAvatar * wavesEnter) + ((1.0f - wavesEnter) * 1.0f);
        }

        public void setShowWaves(boolean show, View parentView) {
            if (this.showWaves != show) {
                parentView.invalidate();
            }
            this.showWaves = show;
        }

        public void setAmplitude(double value) {
            float amplitude = ((float) value) / 80.0f;
            if (!this.showWaves) {
                amplitude = 0.0f;
            }
            if (amplitude > 1.0f) {
                amplitude = 1.0f;
            } else if (amplitude < 0.0f) {
                amplitude = 0.0f;
            }
            this.animateToAmplitude = amplitude;
            this.animateAmplitudeDiff = (amplitude - this.amplitude) / 200.0f;
        }

        public void setColor(int color) {
            this.hasCustomColor = true;
            this.blobDrawable.paint.setColor(color);
        }

        public void setMuted(int status, boolean animated) {
            this.isMuted = status;
            if (!animated) {
                this.progressToMuted = status != 1 ? 1.0f : 0.0f;
            }
            this.invalidateColor = true;
        }
    }

    public BackupImageView getAvatarImageView() {
        return this.avatarImageView;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        String str;
        int i;
        super.onInitializeAccessibilityNodeInfo(info);
        if (info.isEnabled() && Build.VERSION.SDK_INT >= 21) {
            if (!this.participant.muted || this.participant.can_self_unmute) {
                i = R.string.VoipMute;
                str = "VoipMute";
            } else {
                i = R.string.VoipUnmute;
                str = "VoipUnmute";
            }
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, LocaleController.getString(str, i)));
        }
    }

    public long getPeerId() {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = this.participant;
        if (tL_groupCallParticipant == null) {
            return 0L;
        }
        return MessageObject.getPeerId(tL_groupCallParticipant.peer);
    }
}
