package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.firebase.messaging.Constants;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatReactionsEditActivity;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.SharingLocationsAlert;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;
/* loaded from: classes5.dex */
public class FragmentContextView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener {
    public static final int STYLE_ACTIVE_GROUP_CALL = 3;
    public static final int STYLE_AUDIO_PLAYER = 0;
    public static final int STYLE_CONNECTING_GROUP_CALL = 1;
    public static final int STYLE_IMPORTING_MESSAGES = 5;
    public static final int STYLE_INACTIVE_GROUP_CALL = 4;
    public static final int STYLE_LIVE_LOCATION = 2;
    public static final int STYLE_NOT_SET = -1;
    private static final int menu_speed_fast = 3;
    private static final int menu_speed_normal = 2;
    private static final int menu_speed_slow = 1;
    private static final int menu_speed_veryfast = 4;
    private final int account;
    private FragmentContextView additionalContextView;
    private int animationIndex;
    private AnimatorSet animatorSet;
    private View applyingView;
    private AvatarsImageView avatars;
    private ChatActivity chatActivity;
    private boolean checkCallAfterAnimation;
    private boolean checkImportAfterAnimation;
    private Runnable checkLocationRunnable;
    private boolean checkPlayerAfterAnimation;
    private ImageView closeButton;
    float collapseProgress;
    boolean collapseTransition;
    private int currentProgress;
    private int currentStyle;
    private FragmentContextViewDelegate delegate;
    boolean drawOverlay;
    float extraHeight;
    private boolean firstLocationsLoaded;
    private BaseFragment fragment;
    private FrameLayout frameLayout;
    private Paint gradientPaint;
    private TextPaint gradientTextPaint;
    private int gradientWidth;
    private RLottieImageView importingImageView;
    private boolean isLocation;
    private boolean isMusic;
    private boolean isMuted;
    private TextView joinButton;
    private CellFlickerDrawable joinButtonFlicker;
    private int lastLocationSharingCount;
    private MessageObject lastMessageObject;
    private String lastString;
    private LinearGradient linearGradient;
    private Matrix matrix;
    float micAmplitude;
    private RLottieImageView muteButton;
    private RLottieDrawable muteDrawable;
    private ImageView playButton;
    private PlayPauseDrawable playPauseDrawable;
    private ActionBarMenuItem playbackSpeedButton;
    private RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean scheduleRunnableScheduled;
    private View selector;
    private View shadow;
    float speakerAmplitude;
    private ActionBarMenuSubItem[] speedItems;
    private AudioPlayerAlert.ClippingTextViewSwitcher subtitleTextView;
    private boolean supportsCalls;
    private StaticLayout timeLayout;
    private AudioPlayerAlert.ClippingTextViewSwitcher titleTextView;
    private float topPadding;
    private final Runnable updateScheduleTimeRunnable;
    private boolean visible;
    boolean wasDraw;

    /* loaded from: classes5.dex */
    public interface FragmentContextViewDelegate {
        void onAnimation(boolean z, boolean z2);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Style {
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onCameraSwitch(boolean z) {
        VoIPService.StateListener.CC.$default$onCameraSwitch(this, z);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onAudioSettingsChanged() {
        boolean newMuted = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
        if (this.isMuted != newMuted) {
            this.isMuted = newMuted;
            this.muteDrawable.setCustomEndFrame(newMuted ? 15 : 29);
            RLottieDrawable rLottieDrawable = this.muteDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
            this.muteButton.invalidate();
            Theme.getFragmentContextViewWavesDrawable().updateState(this.visible);
        }
        if (this.isMuted) {
            this.micAmplitude = 0.0f;
            Theme.getFragmentContextViewWavesDrawable().setAmplitude(0.0f);
        }
    }

    public boolean drawOverlayed() {
        return this.currentStyle == 3;
    }

    public FragmentContextView(Context context, BaseFragment parentFragment, boolean location) {
        this(context, parentFragment, null, location, null);
    }

    public FragmentContextView(Context context, BaseFragment parentFragment, boolean location, Theme.ResourcesProvider resourcesProvider) {
        this(context, parentFragment, null, location, resourcesProvider);
    }

    public FragmentContextView(final Context context, final BaseFragment parentFragment, View paddingView, boolean location, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        SizeNotifierFrameLayout sizeNotifierFrameLayout;
        float f;
        this.speedItems = new ActionBarMenuSubItem[4];
        this.currentProgress = -1;
        this.currentStyle = -1;
        this.supportsCalls = true;
        this.rect = new RectF();
        this.updateScheduleTimeRunnable = new Runnable() { // from class: org.telegram.ui.Components.FragmentContextView.1
            @Override // java.lang.Runnable
            public void run() {
                String str;
                if (FragmentContextView.this.gradientTextPaint == null || !(FragmentContextView.this.fragment instanceof ChatActivity)) {
                    FragmentContextView.this.scheduleRunnableScheduled = false;
                    return;
                }
                ChatObject.Call call = ((ChatActivity) FragmentContextView.this.fragment).getGroupCall();
                if (call == null || !call.isScheduled()) {
                    FragmentContextView.this.timeLayout = null;
                    FragmentContextView.this.scheduleRunnableScheduled = false;
                    return;
                }
                int currentTime = FragmentContextView.this.fragment.getConnectionsManager().getCurrentTime();
                int diff = call.call.schedule_date - currentTime;
                if (diff >= 86400) {
                    str = LocaleController.formatPluralString("Days", Math.round(diff / 86400.0f), new Object[0]);
                } else {
                    str = AndroidUtilities.formatFullDuration(call.call.schedule_date - currentTime);
                }
                int width = (int) Math.ceil(FragmentContextView.this.gradientTextPaint.measureText(str));
                FragmentContextView.this.timeLayout = new StaticLayout(str, FragmentContextView.this.gradientTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                AndroidUtilities.runOnUIThread(FragmentContextView.this.updateScheduleTimeRunnable, 1000L);
                FragmentContextView.this.frameLayout.invalidate();
            }
        };
        this.account = UserConfig.selectedAccount;
        this.lastLocationSharingCount = -1;
        this.checkLocationRunnable = new Runnable() { // from class: org.telegram.ui.Components.FragmentContextView.2
            @Override // java.lang.Runnable
            public void run() {
                FragmentContextView.this.checkLocationString();
                AndroidUtilities.runOnUIThread(FragmentContextView.this.checkLocationRunnable, 1000L);
            }
        };
        this.animationIndex = -1;
        this.resourcesProvider = resourcesProvider;
        this.fragment = parentFragment;
        if (!(parentFragment.getFragmentView() instanceof SizeNotifierFrameLayout)) {
            sizeNotifierFrameLayout = null;
        } else {
            SizeNotifierFrameLayout sizeNotifierFrameLayout2 = (SizeNotifierFrameLayout) this.fragment.getFragmentView();
            sizeNotifierFrameLayout = sizeNotifierFrameLayout2;
        }
        this.applyingView = paddingView;
        this.visible = true;
        this.isLocation = location;
        if (paddingView == null) {
            ((ViewGroup) this.fragment.getFragmentView()).setClipToPadding(false);
        }
        setTag(1);
        BlurredFrameLayout blurredFrameLayout = new BlurredFrameLayout(context, sizeNotifierFrameLayout) { // from class: org.telegram.ui.Components.FragmentContextView.3
            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                if (FragmentContextView.this.avatars != null && FragmentContextView.this.avatars.getVisibility() == 0) {
                    FragmentContextView.this.avatars.invalidate();
                }
            }

            @Override // org.telegram.ui.Components.BlurredFrameLayout, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (FragmentContextView.this.currentStyle == 4 && FragmentContextView.this.timeLayout != null) {
                    int width = ((int) Math.ceil(FragmentContextView.this.timeLayout.getLineWidth(0))) + AndroidUtilities.dp(24.0f);
                    if (width != FragmentContextView.this.gradientWidth) {
                        FragmentContextView.this.linearGradient = new LinearGradient(0.0f, 0.0f, 1.7f * width, 0.0f, new int[]{-10187532, -7575089, -2860679, -2860679}, new float[]{0.0f, 0.294f, 0.588f, 1.0f}, Shader.TileMode.CLAMP);
                        FragmentContextView.this.gradientPaint.setShader(FragmentContextView.this.linearGradient);
                        FragmentContextView.this.gradientWidth = width;
                    }
                    ChatObject.Call call = ((ChatActivity) FragmentContextView.this.fragment).getGroupCall();
                    float moveProgress = 0.0f;
                    if (FragmentContextView.this.fragment != null && call != null && call.isScheduled()) {
                        long diff = (call.call.schedule_date * 1000) - FragmentContextView.this.fragment.getConnectionsManager().getCurrentTimeMillis();
                        if (diff < 0) {
                            moveProgress = 1.0f;
                        } else if (diff < DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                            moveProgress = 1.0f - (((float) diff) / 5000.0f);
                        }
                        if (diff < 6000) {
                            invalidate();
                        }
                    }
                    FragmentContextView.this.matrix.reset();
                    FragmentContextView.this.matrix.postTranslate((-FragmentContextView.this.gradientWidth) * 0.7f * moveProgress, 0.0f);
                    FragmentContextView.this.linearGradient.setLocalMatrix(FragmentContextView.this.matrix);
                    int x = (getMeasuredWidth() - width) - AndroidUtilities.dp(10.0f);
                    int y = AndroidUtilities.dp(10.0f);
                    FragmentContextView.this.rect.set(0.0f, 0.0f, width, AndroidUtilities.dp(28.0f));
                    canvas.save();
                    canvas.translate(x, y);
                    canvas.drawRoundRect(FragmentContextView.this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), FragmentContextView.this.gradientPaint);
                    canvas.translate(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(6.0f));
                    FragmentContextView.this.timeLayout.draw(canvas);
                    canvas.restore();
                }
            }
        };
        this.frameLayout = blurredFrameLayout;
        addView(blurredFrameLayout, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view = new View(context);
        this.selector = view;
        this.frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f));
        View view2 = new View(context);
        this.shadow = view2;
        view2.setBackgroundResource(R.drawable.blockpanel_shadow);
        addView(this.shadow, LayoutHelper.createFrame(-1, 2.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.playButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.playButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_inappPlayerPlayPause), PorterDuff.Mode.MULTIPLY));
        ImageView imageView2 = this.playButton;
        PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable(14);
        this.playPauseDrawable = playPauseDrawable;
        imageView2.setImageDrawable(playPauseDrawable);
        if (Build.VERSION.SDK_INT >= 21) {
            this.playButton.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_inappPlayerPlayPause) & 436207615, 1, AndroidUtilities.dp(14.0f)));
        }
        addView(this.playButton, LayoutHelper.createFrame(36, 36, 51));
        this.playButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                FragmentContextView.this.m2637lambda$new$0$orgtelegramuiComponentsFragmentContextView(view3);
            }
        });
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.importingImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.importingImageView.setAutoRepeat(true);
        this.importingImageView.setAnimation(R.raw.import_progress, 30, 30);
        this.importingImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(22.0f), getThemedColor(Theme.key_inappPlayerPlayPause)));
        addView(this.importingImageView, LayoutHelper.createFrame(22, 22.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = new AudioPlayerAlert.ClippingTextViewSwitcher(context) { // from class: org.telegram.ui.Components.FragmentContextView.4
            @Override // org.telegram.ui.Components.AudioPlayerAlert.ClippingTextViewSwitcher
            protected TextView createTextView() {
                TextView textView = new TextView(context);
                textView.setMaxLines(1);
                textView.setLines(1);
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setTextSize(1, 15.0f);
                textView.setGravity(19);
                if (FragmentContextView.this.currentStyle != 0 && FragmentContextView.this.currentStyle != 2) {
                    if (FragmentContextView.this.currentStyle != 4) {
                        if (FragmentContextView.this.currentStyle == 1 || FragmentContextView.this.currentStyle == 3) {
                            textView.setGravity(19);
                            textView.setTextColor(FragmentContextView.this.getThemedColor(Theme.key_returnToCallText));
                            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                            textView.setTextSize(1, 14.0f);
                        }
                    } else {
                        textView.setGravity(51);
                        textView.setTextColor(FragmentContextView.this.getThemedColor(Theme.key_inappPlayerPerformer));
                        textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                        textView.setTextSize(1, 15.0f);
                    }
                } else {
                    textView.setGravity(19);
                    textView.setTypeface(Typeface.DEFAULT);
                    textView.setTextSize(1, 15.0f);
                }
                return textView;
            }
        };
        this.titleTextView = clippingTextViewSwitcher;
        addView(clippingTextViewSwitcher, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher2 = new AudioPlayerAlert.ClippingTextViewSwitcher(context) { // from class: org.telegram.ui.Components.FragmentContextView.5
            @Override // org.telegram.ui.Components.AudioPlayerAlert.ClippingTextViewSwitcher
            protected TextView createTextView() {
                TextView textView = new TextView(context);
                textView.setMaxLines(1);
                textView.setLines(1);
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setGravity(3);
                textView.setTextSize(1, 13.0f);
                textView.setTextColor(FragmentContextView.this.getThemedColor(Theme.key_inappPlayerClose));
                return textView;
            }
        };
        this.subtitleTextView = clippingTextViewSwitcher2;
        addView(clippingTextViewSwitcher2, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 10.0f, 36.0f, 0.0f));
        CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
        this.joinButtonFlicker = cellFlickerDrawable;
        cellFlickerDrawable.setProgress(2.0f);
        this.joinButtonFlicker.repeatEnabled = false;
        TextView textView = new TextView(context) { // from class: org.telegram.ui.Components.FragmentContextView.6
            @Override // android.view.View
            public void draw(Canvas canvas) {
                super.draw(canvas);
                int halfOutlineWidth = AndroidUtilities.dp(1.0f);
                AndroidUtilities.rectTmp.set(halfOutlineWidth, halfOutlineWidth, getWidth() - halfOutlineWidth, getHeight() - halfOutlineWidth);
                FragmentContextView.this.joinButtonFlicker.draw(canvas, AndroidUtilities.rectTmp, AndroidUtilities.dp(16.0f), this);
                if (FragmentContextView.this.joinButtonFlicker.getProgress() < 1.0f && !FragmentContextView.this.joinButtonFlicker.repeatEnabled) {
                    invalidate();
                }
            }

            @Override // android.view.View
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                FragmentContextView.this.joinButtonFlicker.setParentWidth(getWidth());
            }
        };
        this.joinButton = textView;
        textView.setText(LocaleController.getString("VoipChatJoin", R.string.VoipChatJoin));
        this.joinButton.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
        this.joinButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), getThemedColor(Theme.key_featuredStickers_addButton), getThemedColor(Theme.key_featuredStickers_addButtonPressed)));
        this.joinButton.setTextSize(1, 14.0f);
        this.joinButton.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.joinButton.setGravity(17);
        this.joinButton.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
        addView(this.joinButton, LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 10.0f, 14.0f, 0.0f));
        this.joinButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                FragmentContextView.this.m2638lambda$new$1$orgtelegramuiComponentsFragmentContextView(view3);
            }
        });
        if (location) {
            f = 14.0f;
        } else {
            f = 14.0f;
            ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, (ActionBarMenu) null, 0, getThemedColor(Theme.key_dialogTextBlack), resourcesProvider);
            this.playbackSpeedButton = actionBarMenuItem;
            actionBarMenuItem.setLongClickEnabled(false);
            this.playbackSpeedButton.setShowSubmenuByMove(false);
            this.playbackSpeedButton.setContentDescription(LocaleController.getString("AccDescrPlayerSpeed", R.string.AccDescrPlayerSpeed));
            this.playbackSpeedButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda2
                @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
                public final void onItemClick(int i) {
                    FragmentContextView.this.m2640lambda$new$2$orgtelegramuiComponentsFragmentContextView(i);
                }
            });
            this.speedItems[0] = this.playbackSpeedButton.addSubItem(1, R.drawable.msg_speed_0_5, LocaleController.getString("SpeedSlow", R.string.SpeedSlow));
            this.speedItems[1] = this.playbackSpeedButton.addSubItem(2, R.drawable.msg_speed_1, LocaleController.getString("SpeedNormal", R.string.SpeedNormal));
            this.speedItems[2] = this.playbackSpeedButton.addSubItem(3, R.drawable.msg_speed_1_5, LocaleController.getString("SpeedFast", R.string.SpeedFast));
            this.speedItems[3] = this.playbackSpeedButton.addSubItem(4, R.drawable.msg_speed_2, LocaleController.getString("SpeedVeryFast", R.string.SpeedVeryFast));
            if (AndroidUtilities.density >= 3.0f) {
                this.playbackSpeedButton.setPadding(0, 1, 0, 0);
            }
            this.playbackSpeedButton.setAdditionalXOffset(AndroidUtilities.dp(8.0f));
            addView(this.playbackSpeedButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 36.0f, 0.0f));
            this.playbackSpeedButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda8
                @Override // android.view.View.OnClickListener
                public final void onClick(View view3) {
                    FragmentContextView.this.m2641lambda$new$3$orgtelegramuiComponentsFragmentContextView(view3);
                }
            });
            this.playbackSpeedButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda12
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view3) {
                    return FragmentContextView.this.m2642lambda$new$4$orgtelegramuiComponentsFragmentContextView(view3);
                }
            });
            updatePlaybackButton();
        }
        AvatarsImageView avatarsImageView = new AvatarsImageView(context, false);
        this.avatars = avatarsImageView;
        avatarsImageView.setDelegate(new Runnable() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                FragmentContextView.this.m2643lambda$new$5$orgtelegramuiComponentsFragmentContextView();
            }
        });
        this.avatars.setVisibility(8);
        addView(this.avatars, LayoutHelper.createFrame(108, 36, 51));
        this.muteDrawable = new RLottieDrawable(R.raw.voice_muted, "2131558577", AndroidUtilities.dp(16.0f), AndroidUtilities.dp(20.0f), true, null);
        AnonymousClass7 anonymousClass7 = new AnonymousClass7(context);
        this.muteButton = anonymousClass7;
        anonymousClass7.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_returnToCallText), PorterDuff.Mode.MULTIPLY));
        if (Build.VERSION.SDK_INT >= 21) {
            this.muteButton.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_inappPlayerClose) & 436207615, 1, AndroidUtilities.dp(f)));
        }
        this.muteButton.setAnimation(this.muteDrawable);
        this.muteButton.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setVisibility(8);
        addView(this.muteButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
        this.muteButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                FragmentContextView.this.m2644lambda$new$6$orgtelegramuiComponentsFragmentContextView(view3);
            }
        });
        ImageView imageView3 = new ImageView(context);
        this.closeButton = imageView3;
        imageView3.setImageResource(R.drawable.miniplayer_close);
        this.closeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_inappPlayerClose), PorterDuff.Mode.MULTIPLY));
        if (Build.VERSION.SDK_INT >= 21) {
            this.closeButton.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_inappPlayerClose) & 436207615, 1, AndroidUtilities.dp(f)));
        }
        this.closeButton.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.closeButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
        this.closeButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                FragmentContextView.this.m2646lambda$new$8$orgtelegramuiComponentsFragmentContextView(resourcesProvider, view3);
            }
        });
        setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda11
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                FragmentContextView.this.m2639lambda$new$10$orgtelegramuiComponentsFragmentContextView(resourcesProvider, parentFragment, view3);
            }
        });
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-FragmentContextView */
    public /* synthetic */ void m2637lambda$new$0$orgtelegramuiComponentsFragmentContextView(View v) {
        if (this.currentStyle == 0) {
            if (MediaController.getInstance().isMessagePaused()) {
                MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
            } else {
                MediaController.getInstance().m383lambda$startAudioAgain$7$orgtelegrammessengerMediaController(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-FragmentContextView */
    public /* synthetic */ void m2638lambda$new$1$orgtelegramuiComponentsFragmentContextView(View v) {
        callOnClick();
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-FragmentContextView */
    public /* synthetic */ void m2640lambda$new$2$orgtelegramuiComponentsFragmentContextView(int id) {
        float oldSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
        if (id == 1) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 0.5f);
        } else if (id == 2) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.0f);
        } else if (id == 3) {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.5f);
        } else {
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.8f);
        }
        float newSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
        if (oldSpeed != newSpeed) {
            playbackSpeedChanged(newSpeed);
        }
        updatePlaybackButton();
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-FragmentContextView */
    public /* synthetic */ void m2641lambda$new$3$orgtelegramuiComponentsFragmentContextView(View v) {
        float newSpeed;
        float currentPlaybackSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
        if (Math.abs(currentPlaybackSpeed - 1.0f) > 0.001f) {
            newSpeed = 1.0f;
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, 1.0f);
        } else {
            MediaController mediaController = MediaController.getInstance();
            boolean z = this.isMusic;
            float fastPlaybackSpeed = MediaController.getInstance().getFastPlaybackSpeed(this.isMusic);
            newSpeed = fastPlaybackSpeed;
            mediaController.setPlaybackSpeed(z, fastPlaybackSpeed);
        }
        playbackSpeedChanged(newSpeed);
    }

    /* renamed from: lambda$new$4$org-telegram-ui-Components-FragmentContextView */
    public /* synthetic */ boolean m2642lambda$new$4$orgtelegramuiComponentsFragmentContextView(View view) {
        this.playbackSpeedButton.toggleSubMenu();
        return true;
    }

    /* renamed from: lambda$new$5$org-telegram-ui-Components-FragmentContextView */
    public /* synthetic */ void m2643lambda$new$5$orgtelegramuiComponentsFragmentContextView() {
        updateAvatars(true);
    }

    /* renamed from: org.telegram.ui.Components.FragmentContextView$7 */
    /* loaded from: classes5.dex */
    public class AnonymousClass7 extends RLottieImageView {
        boolean pressed;
        boolean scheduled;
        private final Runnable toggleMicRunnable = new Runnable() { // from class: org.telegram.ui.Components.FragmentContextView$7$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FragmentContextView.AnonymousClass7.this.m2649lambda$$0$orgtelegramuiComponentsFragmentContextView$7();
            }
        };
        private final Runnable pressRunnable = new Runnable() { // from class: org.telegram.ui.Components.FragmentContextView$7$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FragmentContextView.AnonymousClass7.this.m2650lambda$$1$orgtelegramuiComponentsFragmentContextView$7();
            }
        };

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass7(Context context) {
            super(context);
            FragmentContextView.this = this$0;
        }

        /* renamed from: lambda$$0$org-telegram-ui-Components-FragmentContextView$7 */
        public /* synthetic */ void m2649lambda$$0$orgtelegramuiComponentsFragmentContextView$7() {
            if (VoIPService.getSharedInstance() == null) {
                return;
            }
            VoIPService.getSharedInstance().setMicMute(false, true, false);
            if (FragmentContextView.this.muteDrawable.setCustomEndFrame(FragmentContextView.this.isMuted ? 15 : 29)) {
                if (FragmentContextView.this.isMuted) {
                    FragmentContextView.this.muteDrawable.setCurrentFrame(0);
                } else {
                    FragmentContextView.this.muteDrawable.setCurrentFrame(14);
                }
            }
            FragmentContextView.this.muteButton.playAnimation();
            Theme.getFragmentContextViewWavesDrawable().updateState(true);
        }

        /* renamed from: lambda$$1$org-telegram-ui-Components-FragmentContextView$7 */
        public /* synthetic */ void m2650lambda$$1$orgtelegramuiComponentsFragmentContextView$7() {
            if (!this.scheduled || VoIPService.getSharedInstance() == null) {
                return;
            }
            this.scheduled = false;
            this.pressed = true;
            FragmentContextView.this.isMuted = false;
            AndroidUtilities.runOnUIThread(this.toggleMicRunnable, 90L);
            FragmentContextView.this.muteButton.performHapticFeedback(3, 2);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (FragmentContextView.this.currentStyle == 3 || FragmentContextView.this.currentStyle == 1) {
                VoIPService service = VoIPService.getSharedInstance();
                if (service == null) {
                    AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
                    AndroidUtilities.cancelRunOnUIThread(this.toggleMicRunnable);
                    this.scheduled = false;
                    this.pressed = false;
                    return true;
                }
                if (event.getAction() == 0 && service.isMicMute()) {
                    AndroidUtilities.runOnUIThread(this.pressRunnable, 300L);
                    this.scheduled = true;
                } else if (event.getAction() == 1 || event.getAction() == 3) {
                    AndroidUtilities.cancelRunOnUIThread(this.toggleMicRunnable);
                    if (this.scheduled) {
                        AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
                        this.scheduled = false;
                    } else if (this.pressed) {
                        FragmentContextView.this.isMuted = true;
                        if (FragmentContextView.this.muteDrawable.setCustomEndFrame(15)) {
                            if (!FragmentContextView.this.isMuted) {
                                FragmentContextView.this.muteDrawable.setCurrentFrame(14);
                            } else {
                                FragmentContextView.this.muteDrawable.setCurrentFrame(0);
                            }
                        }
                        FragmentContextView.this.muteButton.playAnimation();
                        if (VoIPService.getSharedInstance() != null) {
                            VoIPService.getSharedInstance().setMicMute(true, true, false);
                            FragmentContextView.this.muteButton.performHapticFeedback(3, 2);
                        }
                        this.pressed = false;
                        Theme.getFragmentContextViewWavesDrawable().updateState(true);
                        MotionEvent cancel = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                        super.onTouchEvent(cancel);
                        cancel.recycle();
                        return true;
                    }
                }
                return super.onTouchEvent(event);
            }
            return super.onTouchEvent(event);
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            String str;
            int i;
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName(Button.class.getName());
            if (FragmentContextView.this.isMuted) {
                i = R.string.VoipUnmute;
                str = "VoipUnmute";
            } else {
                i = R.string.VoipMute;
                str = "VoipMute";
            }
            info.setText(LocaleController.getString(str, i));
        }
    }

    /* renamed from: lambda$new$6$org-telegram-ui-Components-FragmentContextView */
    public /* synthetic */ void m2644lambda$new$6$orgtelegramuiComponentsFragmentContextView(View v) {
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService == null) {
            return;
        }
        if (voIPService.groupCall != null) {
            AccountInstance.getInstance(voIPService.getAccount());
            ChatObject.Call call = voIPService.groupCall;
            TLRPC.Chat chat = voIPService.getChat();
            TLRPC.TL_groupCallParticipant participant = call.participants.get(voIPService.getSelfId());
            if (participant != null && !participant.can_self_unmute && participant.muted && !ChatObject.canManageCalls(chat)) {
                return;
            }
        }
        boolean z = !voIPService.isMicMute();
        this.isMuted = z;
        voIPService.setMicMute(z, false, true);
        if (this.muteDrawable.setCustomEndFrame(this.isMuted ? 15 : 29)) {
            if (this.isMuted) {
                this.muteDrawable.setCurrentFrame(0);
            } else {
                this.muteDrawable.setCurrentFrame(14);
            }
        }
        this.muteButton.playAnimation();
        Theme.getFragmentContextViewWavesDrawable().updateState(true);
        this.muteButton.performHapticFeedback(3, 2);
    }

    /* renamed from: lambda$new$8$org-telegram-ui-Components-FragmentContextView */
    public /* synthetic */ void m2646lambda$new$8$orgtelegramuiComponentsFragmentContextView(Theme.ResourcesProvider resourcesProvider, View v) {
        if (this.currentStyle == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.fragment.getParentActivity(), resourcesProvider);
            builder.setTitle(LocaleController.getString("StopLiveLocationAlertToTitle", R.string.StopLiveLocationAlertToTitle));
            BaseFragment baseFragment = this.fragment;
            if (baseFragment instanceof DialogsActivity) {
                builder.setMessage(LocaleController.getString("StopLiveLocationAlertAllText", R.string.StopLiveLocationAlertAllText));
            } else {
                ChatActivity activity = (ChatActivity) baseFragment;
                TLRPC.Chat chat = activity.getCurrentChat();
                TLRPC.User user = activity.getCurrentUser();
                if (chat != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("StopLiveLocationAlertToGroupText", R.string.StopLiveLocationAlertToGroupText, chat.title)));
                } else if (user != null) {
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("StopLiveLocationAlertToUserText", R.string.StopLiveLocationAlertToUserText, UserObject.getFirstName(user))));
                } else {
                    builder.setMessage(LocaleController.getString("AreYouSure", R.string.AreYouSure));
                }
            }
            builder.setPositiveButton(LocaleController.getString("Stop", R.string.Stop), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FragmentContextView.this.m2645lambda$new$7$orgtelegramuiComponentsFragmentContextView(dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            AlertDialog alertDialog = builder.create();
            builder.show();
            TextView button = (TextView) alertDialog.getButton(-1);
            if (button != null) {
                button.setTextColor(getThemedColor(Theme.key_dialogTextRed2));
                return;
            }
            return;
        }
        MediaController.getInstance().cleanupPlayer(true, true);
    }

    /* renamed from: lambda$new$7$org-telegram-ui-Components-FragmentContextView */
    public /* synthetic */ void m2645lambda$new$7$orgtelegramuiComponentsFragmentContextView(DialogInterface dialogInterface, int i) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof DialogsActivity) {
            for (int a = 0; a < 4; a++) {
                LocationController.getInstance(a).removeAllLocationSharings();
            }
            return;
        }
        LocationController.getInstance(baseFragment.getCurrentAccount()).removeSharingLocation(((ChatActivity) this.fragment).getDialogId());
    }

    /* renamed from: lambda$new$10$org-telegram-ui-Components-FragmentContextView */
    public /* synthetic */ void m2639lambda$new$10$orgtelegramuiComponentsFragmentContextView(Theme.ResourcesProvider resourcesProvider, BaseFragment parentFragment, View v) {
        ChatActivity chatActivity;
        ChatObject.Call call;
        int i = this.currentStyle;
        if (i == 0) {
            MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
            if (this.fragment != null && messageObject != null) {
                if (messageObject.isMusic()) {
                    if (getContext() instanceof LaunchActivity) {
                        this.fragment.showDialog(new AudioPlayerAlert(getContext(), resourcesProvider));
                        return;
                    }
                    return;
                }
                long dialogId = 0;
                BaseFragment baseFragment = this.fragment;
                if (baseFragment instanceof ChatActivity) {
                    dialogId = ((ChatActivity) baseFragment).getDialogId();
                }
                if (messageObject.getDialogId() == dialogId) {
                    ((ChatActivity) this.fragment).scrollToMessageId(messageObject.getId(), 0, false, 0, true, 0);
                    return;
                }
                long dialogId2 = messageObject.getDialogId();
                Bundle args = new Bundle();
                if (DialogObject.isEncryptedDialog(dialogId2)) {
                    args.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId2));
                } else if (DialogObject.isUserDialog(dialogId2)) {
                    args.putLong("user_id", dialogId2);
                } else {
                    args.putLong(ChatReactionsEditActivity.KEY_CHAT_ID, -dialogId2);
                }
                args.putInt(Constants.MessagePayloadKeys.MSGID_SERVER, messageObject.getId());
                this.fragment.presentFragment(new ChatActivity(args), this.fragment instanceof ChatActivity);
                return;
            }
            return;
        }
        boolean z = true;
        if (i == 1) {
            Intent intent = new Intent(getContext(), LaunchActivity.class).setAction("voip");
            getContext().startActivity(intent);
        } else if (i == 2) {
            long did = 0;
            int account = UserConfig.selectedAccount;
            BaseFragment baseFragment2 = this.fragment;
            if (baseFragment2 instanceof ChatActivity) {
                did = ((ChatActivity) baseFragment2).getDialogId();
                account = this.fragment.getCurrentAccount();
            } else if (LocationController.getLocationsCount() == 1) {
                int a = 0;
                while (true) {
                    if (a >= 4) {
                        break;
                    }
                    ArrayList<LocationController.SharingLocationInfo> arrayList = LocationController.getInstance(a).sharingLocationsUI;
                    if (arrayList.isEmpty()) {
                        a++;
                    } else {
                        LocationController.SharingLocationInfo info = LocationController.getInstance(a).sharingLocationsUI.get(0);
                        did = info.did;
                        account = info.messageObject.currentAccount;
                        break;
                    }
                }
            }
            if (did != 0) {
                openSharingLocation(LocationController.getInstance(account).getSharingLocationInfo(did));
            } else {
                this.fragment.showDialog(new SharingLocationsAlert(getContext(), new SharingLocationsAlert.SharingLocationsAlertDelegate() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda3
                    @Override // org.telegram.ui.Components.SharingLocationsAlert.SharingLocationsAlertDelegate
                    public final void didSelectLocation(LocationController.SharingLocationInfo sharingLocationInfo) {
                        FragmentContextView.this.openSharingLocation(sharingLocationInfo);
                    }
                }, resourcesProvider));
            }
        } else if (i == 3) {
            if (VoIPService.getSharedInstance() != null && (getContext() instanceof LaunchActivity)) {
                GroupCallActivity.create((LaunchActivity) getContext(), AccountInstance.getInstance(VoIPService.getSharedInstance().getAccount()), null, null, false, null);
            }
        } else if (i == 4) {
            if (this.fragment.getParentActivity() == null || (call = (chatActivity = (ChatActivity) this.fragment).getGroupCall()) == null) {
                return;
            }
            TLRPC.Chat chat = chatActivity.getMessagesController().getChat(Long.valueOf(call.chatId));
            if (call.call == null || call.call.rtmp_stream) {
                z = false;
            }
            Boolean valueOf = Boolean.valueOf(z);
            Activity parentActivity = this.fragment.getParentActivity();
            BaseFragment baseFragment3 = this.fragment;
            VoIPHelper.startCall(chat, null, null, false, valueOf, parentActivity, baseFragment3, baseFragment3.getAccountInstance());
        } else if (i == 5) {
            SendMessagesHelper.ImportingHistory importingHistory = parentFragment.getSendMessagesHelper().getImportingHistory(((ChatActivity) parentFragment).getDialogId());
            if (importingHistory == null) {
                return;
            }
            ImportingAlert importingAlert = new ImportingAlert(getContext(), null, (ChatActivity) this.fragment, resourcesProvider);
            importingAlert.setOnHideListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda5
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    FragmentContextView.this.m2647lambda$new$9$orgtelegramuiComponentsFragmentContextView(dialogInterface);
                }
            });
            this.fragment.showDialog(importingAlert);
            checkImport(false);
        }
    }

    /* renamed from: lambda$new$9$org-telegram-ui-Components-FragmentContextView */
    public /* synthetic */ void m2647lambda$new$9$orgtelegramuiComponentsFragmentContextView(DialogInterface dialog) {
        checkImport(false);
    }

    public void setSupportsCalls(boolean value) {
        this.supportsCalls = value;
    }

    public void setDelegate(FragmentContextViewDelegate fragmentContextViewDelegate) {
        this.delegate = fragmentContextViewDelegate;
    }

    private void updatePlaybackButton() {
        if (this.playbackSpeedButton == null) {
            return;
        }
        float currentPlaybackSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
        float speed = MediaController.getInstance().getFastPlaybackSpeed(this.isMusic);
        if (Math.abs(speed - 1.8f) < 0.001f) {
            this.playbackSpeedButton.setIcon(R.drawable.voice_mini_2_0);
        } else if (Math.abs(speed - 1.5f) < 0.001f) {
            this.playbackSpeedButton.setIcon(R.drawable.voice_mini_1_5);
        } else {
            this.playbackSpeedButton.setIcon(R.drawable.voice_mini_0_5);
        }
        updateColors();
        for (int a = 0; a < this.speedItems.length; a++) {
            if ((a == 0 && Math.abs(currentPlaybackSpeed - 0.5f) < 0.001f) || ((a == 1 && Math.abs(currentPlaybackSpeed - 1.0f) < 0.001f) || ((a == 2 && Math.abs(currentPlaybackSpeed - 1.5f) < 0.001f) || (a == 3 && Math.abs(currentPlaybackSpeed - 1.8f) < 0.001f)))) {
                this.speedItems[a].setColors(getThemedColor(Theme.key_inappPlayerPlayPause), getThemedColor(Theme.key_inappPlayerPlayPause));
            } else {
                this.speedItems[a].setColors(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon));
            }
        }
    }

    public void updateColors() {
        String key;
        if (this.playbackSpeedButton != null) {
            float currentPlaybackSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
            if (Math.abs(currentPlaybackSpeed - 1.0f) > 0.001f) {
                key = Theme.key_inappPlayerPlayPause;
            } else {
                key = Theme.key_inappPlayerClose;
            }
            this.playbackSpeedButton.setIconColor(getThemedColor(key));
            if (Build.VERSION.SDK_INT >= 21) {
                this.playbackSpeedButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(key) & 436207615, 1, AndroidUtilities.dp(14.0f)));
            }
        }
    }

    public void setAdditionalContextView(FragmentContextView contextView) {
        this.additionalContextView = contextView;
    }

    public void openSharingLocation(final LocationController.SharingLocationInfo info) {
        if (info == null || !(this.fragment.getParentActivity() instanceof LaunchActivity)) {
            return;
        }
        LaunchActivity launchActivity = (LaunchActivity) this.fragment.getParentActivity();
        launchActivity.switchToAccount(info.messageObject.currentAccount, true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(info.messageObject);
        final long dialog_id = info.messageObject.getDialogId();
        locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda4
            @Override // org.telegram.ui.LocationActivity.LocationActivityDelegate
            public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                SendMessagesHelper.getInstance(LocationController.SharingLocationInfo.this.messageObject.currentAccount).sendMessage(messageMedia, dialog_id, (MessageObject) null, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i2);
            }
        });
        launchActivity.m3653lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(locationActivity);
    }

    public float getTopPadding() {
        return this.topPadding;
    }

    private void checkVisibility() {
        boolean show = false;
        int i = 0;
        if (this.isLocation) {
            BaseFragment baseFragment = this.fragment;
            if (baseFragment instanceof DialogsActivity) {
                show = LocationController.getLocationsCount() != 0;
            } else {
                show = LocationController.getInstance(baseFragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
            }
        } else if (VoIPService.getSharedInstance() != null && !VoIPService.getSharedInstance().isHangingUp() && VoIPService.getSharedInstance().getCallState() != 15) {
            show = true;
            startJoinFlickerAnimation();
        } else {
            BaseFragment baseFragment2 = this.fragment;
            if ((baseFragment2 instanceof ChatActivity) && baseFragment2.getSendMessagesHelper().getImportingHistory(((ChatActivity) this.fragment).getDialogId()) != null && !isPlayingVoice()) {
                show = true;
            } else {
                BaseFragment baseFragment3 = this.fragment;
                if ((baseFragment3 instanceof ChatActivity) && ((ChatActivity) baseFragment3).getGroupCall() != null && ((ChatActivity) this.fragment).getGroupCall().shouldShowPanel() && !GroupCallPip.isShowing() && !isPlayingVoice()) {
                    show = true;
                    startJoinFlickerAnimation();
                } else {
                    MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
                    if (messageObject != null && messageObject.getId() != 0) {
                        show = true;
                    }
                }
            }
        }
        if (!show) {
            i = 8;
        }
        setVisibility(i);
    }

    public void setTopPadding(float value) {
        this.topPadding = value;
        if (this.fragment != null && getParent() != null) {
            View view = this.applyingView;
            if (view == null) {
                view = this.fragment.getFragmentView();
            }
            int additionalPadding = 0;
            FragmentContextView fragmentContextView = this.additionalContextView;
            if (fragmentContextView != null && fragmentContextView.getVisibility() == 0 && this.additionalContextView.getParent() != null) {
                additionalPadding = AndroidUtilities.dp(this.additionalContextView.getStyleHeight());
            }
            if (view == null || getParent() == null) {
                return;
            }
            view.setPadding(0, ((int) (getVisibility() == 0 ? this.topPadding : 0.0f)) + additionalPadding, 0, 0);
        }
    }

    protected void playbackSpeedChanged(float value) {
    }

    private void updateStyle(int style) {
        int i = this.currentStyle;
        if (i == style) {
            return;
        }
        boolean z = true;
        if (i == 3 || i == 1) {
            Theme.getFragmentContextViewWavesDrawable().removeParent(this);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().unregisterStateListener(this);
            }
        }
        this.currentStyle = style;
        this.frameLayout.setWillNotDraw(style != 4);
        if (style != 4) {
            this.timeLayout = null;
        }
        AvatarsImageView avatarsImageView = this.avatars;
        if (avatarsImageView != null) {
            avatarsImageView.setStyle(this.currentStyle);
            this.avatars.setLayoutParams(LayoutHelper.createFrame(108, getStyleHeight(), 51));
        }
        this.frameLayout.setLayoutParams(LayoutHelper.createFrame(-1, getStyleHeight(), 51, 0.0f, 0.0f, 0.0f, 0.0f));
        this.shadow.setLayoutParams(LayoutHelper.createFrame(-1, 2.0f, 51, 0.0f, getStyleHeight(), 0.0f, 0.0f));
        float f = this.topPadding;
        if (f > 0.0f && f != AndroidUtilities.dp2(getStyleHeight())) {
            updatePaddings();
            setTopPadding(AndroidUtilities.dp2(getStyleHeight()));
        }
        if (style == 5) {
            this.selector.setBackground(Theme.getSelectorDrawable(false));
            this.frameLayout.setBackgroundColor(getThemedColor(Theme.key_inappPlayerBackground));
            this.frameLayout.setTag(Theme.key_inappPlayerBackground);
            int i2 = 0;
            while (i2 < 2) {
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
                TextView textView = i2 == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                if (textView != null) {
                    textView.setGravity(19);
                    textView.setTextColor(getThemedColor(Theme.key_inappPlayerTitle));
                    textView.setTypeface(Typeface.DEFAULT);
                    textView.setTextSize(1, 15.0f);
                }
                i2++;
            }
            this.titleTextView.setTag(Theme.key_inappPlayerTitle);
            this.subtitleTextView.setVisibility(8);
            this.joinButton.setVisibility(8);
            this.closeButton.setVisibility(8);
            this.playButton.setVisibility(8);
            this.muteButton.setVisibility(8);
            this.avatars.setVisibility(8);
            this.importingImageView.setVisibility(0);
            this.importingImageView.playAnimation();
            this.closeButton.setContentDescription(LocaleController.getString("AccDescrClosePlayer", R.string.AccDescrClosePlayer));
            ActionBarMenuItem actionBarMenuItem = this.playbackSpeedButton;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setVisibility(8);
            }
            this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        } else if (style == 0 || style == 2) {
            this.selector.setBackground(Theme.getSelectorDrawable(false));
            this.frameLayout.setBackgroundColor(getThemedColor(Theme.key_inappPlayerBackground));
            this.frameLayout.setTag(Theme.key_inappPlayerBackground);
            this.subtitleTextView.setVisibility(8);
            this.joinButton.setVisibility(8);
            this.closeButton.setVisibility(0);
            this.playButton.setVisibility(0);
            this.muteButton.setVisibility(8);
            this.importingImageView.setVisibility(8);
            this.importingImageView.stopAnimation();
            this.avatars.setVisibility(8);
            int i3 = 0;
            while (i3 < 2) {
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher2 = this.titleTextView;
                TextView textView2 = i3 == 0 ? clippingTextViewSwitcher2.getTextView() : clippingTextViewSwitcher2.getNextTextView();
                if (textView2 != null) {
                    textView2.setGravity(19);
                    textView2.setTextColor(getThemedColor(Theme.key_inappPlayerTitle));
                    textView2.setTypeface(Typeface.DEFAULT);
                    textView2.setTextSize(1, 15.0f);
                }
                i3++;
            }
            this.titleTextView.setTag(Theme.key_inappPlayerTitle);
            if (style == 0) {
                this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
                ActionBarMenuItem actionBarMenuItem2 = this.playbackSpeedButton;
                if (actionBarMenuItem2 != null) {
                    actionBarMenuItem2.setVisibility(0);
                }
                this.closeButton.setContentDescription(LocaleController.getString("AccDescrClosePlayer", R.string.AccDescrClosePlayer));
                return;
            }
            this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 8.0f, 0.0f, 0.0f, 0.0f));
            this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 51.0f, 0.0f, 36.0f, 0.0f));
            this.closeButton.setContentDescription(LocaleController.getString("AccDescrStopLiveLocation", R.string.AccDescrStopLiveLocation));
        } else if (style == 4) {
            this.selector.setBackground(Theme.getSelectorDrawable(false));
            this.frameLayout.setBackgroundColor(getThemedColor(Theme.key_inappPlayerBackground));
            this.frameLayout.setTag(Theme.key_inappPlayerBackground);
            this.muteButton.setVisibility(8);
            this.subtitleTextView.setVisibility(0);
            int i4 = 0;
            while (i4 < 2) {
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher3 = this.titleTextView;
                TextView textView3 = i4 == 0 ? clippingTextViewSwitcher3.getTextView() : clippingTextViewSwitcher3.getNextTextView();
                if (textView3 != null) {
                    textView3.setGravity(51);
                    textView3.setTextColor(getThemedColor(Theme.key_inappPlayerPerformer));
                    textView3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    textView3.setTextSize(1, 15.0f);
                }
                i4++;
            }
            this.titleTextView.setTag(Theme.key_inappPlayerPerformer);
            this.titleTextView.setPadding(0, 0, 0, 0);
            this.importingImageView.setVisibility(8);
            this.importingImageView.stopAnimation();
            boolean isRtmpStream = false;
            BaseFragment baseFragment = this.fragment;
            if (baseFragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) baseFragment;
                if (chatActivity.getGroupCall() == null || chatActivity.getGroupCall().call == null || !chatActivity.getGroupCall().call.rtmp_stream) {
                    z = false;
                }
                isRtmpStream = z;
            }
            this.avatars.setVisibility(!isRtmpStream ? 0 : 8);
            if (this.avatars.getVisibility() != 8) {
                updateAvatars(false);
            } else {
                this.titleTextView.setTranslationX(-AndroidUtilities.dp(36.0f));
                this.subtitleTextView.setTranslationX(-AndroidUtilities.dp(36.0f));
            }
            this.closeButton.setVisibility(8);
            this.playButton.setVisibility(8);
            ActionBarMenuItem actionBarMenuItem3 = this.playbackSpeedButton;
            if (actionBarMenuItem3 != null) {
                actionBarMenuItem3.setVisibility(8);
            }
        } else if (style == 1 || style == 3) {
            this.selector.setBackground(null);
            updateCallTitle();
            boolean isRtmpStream2 = VoIPService.hasRtmpStream();
            this.avatars.setVisibility(!isRtmpStream2 ? 0 : 8);
            if (style == 3 && VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().registerStateListener(this);
            }
            if (this.avatars.getVisibility() != 8) {
                updateAvatars(false);
            } else {
                this.titleTextView.setTranslationX(0.0f);
                this.subtitleTextView.setTranslationX(0.0f);
            }
            this.muteButton.setVisibility(!isRtmpStream2 ? 0 : 8);
            boolean z2 = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
            this.isMuted = z2;
            this.muteDrawable.setCustomEndFrame(z2 ? 15 : 29);
            RLottieDrawable rLottieDrawable = this.muteDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
            this.muteButton.invalidate();
            this.frameLayout.setBackground(null);
            this.frameLayout.setBackgroundColor(0);
            this.importingImageView.setVisibility(8);
            this.importingImageView.stopAnimation();
            Theme.getFragmentContextViewWavesDrawable().addParent(this);
            invalidate();
            int i5 = 0;
            while (i5 < 2) {
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher4 = this.titleTextView;
                TextView textView4 = i5 == 0 ? clippingTextViewSwitcher4.getTextView() : clippingTextViewSwitcher4.getNextTextView();
                if (textView4 != null) {
                    textView4.setGravity(19);
                    textView4.setTextColor(getThemedColor(Theme.key_returnToCallText));
                    textView4.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                    textView4.setTextSize(1, 14.0f);
                }
                i5++;
            }
            this.titleTextView.setTag(Theme.key_returnToCallText);
            this.closeButton.setVisibility(8);
            this.playButton.setVisibility(8);
            this.subtitleTextView.setVisibility(8);
            this.joinButton.setVisibility(8);
            this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 2.0f));
            this.titleTextView.setPadding(AndroidUtilities.dp(112.0f), 0, AndroidUtilities.dp(112.0f), 0);
            ActionBarMenuItem actionBarMenuItem4 = this.playbackSpeedButton;
            if (actionBarMenuItem4 != null) {
                actionBarMenuItem4.setVisibility(8);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        if (this.scheduleRunnableScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.updateScheduleTimeRunnable);
            this.scheduleRunnableScheduled = false;
        }
        this.visible = false;
        NotificationCenter.getInstance(this.account).onAnimationFinish(this.animationIndex);
        this.topPadding = 0.0f;
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsCacheChanged);
        } else {
            for (int a = 0; a < 4; a++) {
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.groupCallUpdated);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.groupCallTypingsUpdated);
                NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.historyImportProgressChanged);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.messagePlayingSpeedChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didStartedCall);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.groupCallVisibilityChanged);
        }
        int i = this.currentStyle;
        if (i == 3 || i == 1) {
            Theme.getFragmentContextViewWavesDrawable().removeParent(this);
        }
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
        }
        this.wasDraw = false;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int i = 15;
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsCacheChanged);
            FragmentContextView fragmentContextView = this.additionalContextView;
            if (fragmentContextView != null) {
                fragmentContextView.checkVisibility();
            }
            checkLiveLocation(true);
        } else {
            for (int a = 0; a < 4; a++) {
                NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.groupCallUpdated);
                NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.groupCallTypingsUpdated);
                NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.historyImportProgressChanged);
            }
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.messagePlayingSpeedChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didStartedCall);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndCall);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.groupCallVisibilityChanged);
            FragmentContextView fragmentContextView2 = this.additionalContextView;
            if (fragmentContextView2 != null) {
                fragmentContextView2.checkVisibility();
            }
            if (VoIPService.getSharedInstance() != null && !VoIPService.getSharedInstance().isHangingUp() && VoIPService.getSharedInstance().getCallState() != 15 && !GroupCallPip.isShowing()) {
                checkCall(true);
            } else {
                BaseFragment baseFragment = this.fragment;
                if ((baseFragment instanceof ChatActivity) && baseFragment.getSendMessagesHelper().getImportingHistory(((ChatActivity) this.fragment).getDialogId()) != null && !isPlayingVoice()) {
                    checkImport(true);
                } else {
                    BaseFragment baseFragment2 = this.fragment;
                    if ((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).getGroupCall() != null && ((ChatActivity) this.fragment).getGroupCall().shouldShowPanel() && !GroupCallPip.isShowing() && !isPlayingVoice()) {
                        checkCall(true);
                    } else {
                        checkCall(true);
                        checkPlayer(true);
                        updatePlaybackButton();
                    }
                }
            }
        }
        int i2 = this.currentStyle;
        if (i2 == 3 || i2 == 1) {
            Theme.getFragmentContextViewWavesDrawable().addParent(this);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().registerStateListener(this);
            }
            boolean newMuted = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
            if (this.isMuted != newMuted) {
                this.isMuted = newMuted;
                RLottieDrawable rLottieDrawable = this.muteDrawable;
                if (!newMuted) {
                    i = 29;
                }
                rLottieDrawable.setCustomEndFrame(i);
                RLottieDrawable rLottieDrawable2 = this.muteDrawable;
                rLottieDrawable2.setCurrentFrame(rLottieDrawable2.getCustomEndFrame() - 1, false, true);
                this.muteButton.invalidate();
            }
        } else if (i2 == 4 && !this.scheduleRunnableScheduled) {
            this.scheduleRunnableScheduled = true;
            this.updateScheduleTimeRunnable.run();
        }
        if (this.visible && this.topPadding == 0.0f) {
            updatePaddings();
            setTopPadding(AndroidUtilities.dp2(getStyleHeight()));
        }
        this.speakerAmplitude = 0.0f;
        this.micAmplitude = 0.0f;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, AndroidUtilities.dp2(getStyleHeight() + 2));
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        VoIPService sharedInstance;
        TLRPC.TL_groupCallParticipant participant;
        if (id == NotificationCenter.liveLocationsChanged) {
            checkLiveLocation(false);
        } else if (id == NotificationCenter.liveLocationsCacheChanged) {
            if (this.fragment instanceof ChatActivity) {
                long did = ((Long) args[0]).longValue();
                if (((ChatActivity) this.fragment).getDialogId() == did) {
                    checkLocationString();
                }
            }
        } else if (id == NotificationCenter.messagePlayingDidStart || id == NotificationCenter.messagePlayingPlayStateChanged || id == NotificationCenter.messagePlayingDidReset || id == NotificationCenter.didEndCall) {
            int i = this.currentStyle;
            if (i == 1 || i == 3 || i == 4) {
                checkCall(false);
            }
            checkPlayer(false);
        } else if (id == NotificationCenter.didStartedCall || id == NotificationCenter.groupCallUpdated || id == NotificationCenter.groupCallVisibilityChanged) {
            checkCall(false);
            if (this.currentStyle == 3 && (sharedInstance = VoIPService.getSharedInstance()) != null && sharedInstance.groupCall != null) {
                if (id == NotificationCenter.didStartedCall) {
                    sharedInstance.registerStateListener(this);
                }
                int currentCallState = sharedInstance.getCallState();
                if (currentCallState != 1 && currentCallState != 2 && currentCallState != 6 && currentCallState != 5 && (participant = sharedInstance.groupCall.participants.get(sharedInstance.getSelfId())) != null && !participant.can_self_unmute && participant.muted && !ChatObject.canManageCalls(sharedInstance.getChat())) {
                    sharedInstance.setMicMute(true, false, false);
                    long now = SystemClock.uptimeMillis();
                    MotionEvent e = MotionEvent.obtain(now, now, 3, 0.0f, 0.0f, 0);
                    this.muteButton.dispatchTouchEvent(e);
                }
            }
        } else if (id == NotificationCenter.groupCallTypingsUpdated) {
            if (this.visible && this.currentStyle == 4) {
                ChatObject.Call call = ((ChatActivity) this.fragment).getGroupCall();
                if (call != null) {
                    if (call.isScheduled()) {
                        this.subtitleTextView.setText(LocaleController.formatStartsTime(call.call.schedule_date, 4), false);
                    } else if (call.call.participants_count == 0) {
                        this.subtitleTextView.setText(LocaleController.getString(call.call.rtmp_stream ? R.string.ViewersWatchingNobody : R.string.MembersTalkingNobody), false);
                    } else {
                        this.subtitleTextView.setText(LocaleController.formatPluralString(call.call.rtmp_stream ? "ViewersWatching" : "Participants", call.call.participants_count, new Object[0]), false);
                    }
                }
                updateAvatars(true);
            }
        } else if (id == NotificationCenter.historyImportProgressChanged) {
            int i2 = this.currentStyle;
            if (i2 == 1 || i2 == 3 || i2 == 4) {
                checkCall(false);
            }
            checkImport(false);
        } else if (id == NotificationCenter.messagePlayingSpeedChanged) {
            updatePlaybackButton();
        } else if (id == NotificationCenter.webRtcMicAmplitudeEvent) {
            if (VoIPService.getSharedInstance() != null && !VoIPService.getSharedInstance().isMicMute()) {
                this.micAmplitude = Math.min(8500.0f, ((Float) args[0]).floatValue() * 4000.0f) / 8500.0f;
            } else {
                this.micAmplitude = 0.0f;
            }
            if (VoIPService.getSharedInstance() != null) {
                Theme.getFragmentContextViewWavesDrawable().setAmplitude(Math.max(this.speakerAmplitude, this.micAmplitude));
            }
        } else if (id == NotificationCenter.webRtcSpeakerAmplitudeEvent) {
            float a = (((Float) args[0]).floatValue() * 15.0f) / 80.0f;
            this.speakerAmplitude = Math.max(0.0f, Math.min(a, 1.0f));
            if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
                this.micAmplitude = 0.0f;
            }
            if (VoIPService.getSharedInstance() != null) {
                Theme.getFragmentContextViewWavesDrawable().setAmplitude(Math.max(this.speakerAmplitude, this.micAmplitude));
            }
            this.avatars.invalidate();
        }
    }

    public int getStyleHeight() {
        return this.currentStyle == 4 ? 48 : 36;
    }

    public boolean isCallTypeVisible() {
        int i = this.currentStyle;
        return (i == 1 || i == 3) && this.visible;
    }

    private void checkLiveLocation(boolean create) {
        boolean show;
        String param;
        String str;
        View fragmentView = this.fragment.getFragmentView();
        if (!create && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            create = true;
        }
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof DialogsActivity) {
            show = LocationController.getLocationsCount() != 0;
        } else {
            show = LocationController.getInstance(baseFragment.getCurrentAccount()).isSharingLocation(((ChatActivity) this.fragment).getDialogId());
        }
        if (!show) {
            this.lastLocationSharingCount = -1;
            AndroidUtilities.cancelRunOnUIThread(this.checkLocationRunnable);
            if (this.visible) {
                this.visible = false;
                if (create) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                AnimatorSet animatorSet = this.animatorSet;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animatorSet = animatorSet2;
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this, "topPadding", 0.0f));
                this.animatorSet.setDuration(200L);
                this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FragmentContextView.8
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                            FragmentContextView.this.setVisibility(8);
                            FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
                return;
            }
            return;
        }
        updateStyle(2);
        this.playButton.setImageDrawable(new ShareLocationDrawable(getContext(), 1));
        if (create && this.topPadding == 0.0f) {
            setTopPadding(AndroidUtilities.dp2(getStyleHeight()));
        }
        if (!this.visible) {
            if (!create) {
                AnimatorSet animatorSet3 = this.animatorSet;
                if (animatorSet3 != null) {
                    animatorSet3.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet4 = new AnimatorSet();
                this.animatorSet = animatorSet4;
                animatorSet4.playTogether(ObjectAnimator.ofFloat(this, "topPadding", AndroidUtilities.dp2(getStyleHeight())));
                this.animatorSet.setDuration(200L);
                this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FragmentContextView.9
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                            FragmentContextView.this.animatorSet = null;
                        }
                    }
                });
                this.animatorSet.start();
            }
            this.visible = true;
            setVisibility(0);
        }
        if (this.fragment instanceof DialogsActivity) {
            String liveLocation = LocaleController.getString("LiveLocationContext", R.string.LiveLocationContext);
            ArrayList<LocationController.SharingLocationInfo> infos = new ArrayList<>();
            for (int a = 0; a < 4; a++) {
                infos.addAll(LocationController.getInstance(a).sharingLocationsUI);
            }
            int a2 = infos.size();
            if (a2 == 1) {
                LocationController.SharingLocationInfo info = infos.get(0);
                long dialogId = info.messageObject.getDialogId();
                if (DialogObject.isUserDialog(dialogId)) {
                    TLRPC.User user = MessagesController.getInstance(info.messageObject.currentAccount).getUser(Long.valueOf(dialogId));
                    param = UserObject.getFirstName(user);
                    str = LocaleController.getString("AttachLiveLocationIsSharing", R.string.AttachLiveLocationIsSharing);
                } else {
                    TLRPC.Chat chat = MessagesController.getInstance(info.messageObject.currentAccount).getChat(Long.valueOf(-dialogId));
                    if (chat != null) {
                        param = chat.title;
                    } else {
                        param = "";
                    }
                    str = LocaleController.getString("AttachLiveLocationIsSharingChat", R.string.AttachLiveLocationIsSharingChat);
                }
            } else {
                param = LocaleController.formatPluralString("Chats", infos.size(), new Object[0]);
                str = LocaleController.getString("AttachLiveLocationIsSharingChats", R.string.AttachLiveLocationIsSharingChats);
            }
            String fullString = String.format(str, liveLocation, param);
            int start = fullString.indexOf(liveLocation);
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(fullString);
            int i = 0;
            while (i < 2) {
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
                TextView textView = i == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                if (textView != null) {
                    textView.setEllipsize(TextUtils.TruncateAt.END);
                }
                i++;
            }
            TypefaceSpan span = new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM), 0, getThemedColor(Theme.key_inappPlayerPerformer));
            stringBuilder.setSpan(span, start, liveLocation.length() + start, 18);
            this.titleTextView.setText(stringBuilder, false);
            return;
        }
        this.checkLocationRunnable.run();
        checkLocationString();
    }

    public void checkLocationString() {
        String fullString;
        ChatActivity chatActivity;
        BaseFragment baseFragment = this.fragment;
        if (!(baseFragment instanceof ChatActivity) || this.titleTextView == null) {
            return;
        }
        ChatActivity chatActivity2 = (ChatActivity) baseFragment;
        long dialogId = chatActivity2.getDialogId();
        int currentAccount = chatActivity2.getCurrentAccount();
        ArrayList<TLRPC.Message> messages = LocationController.getInstance(currentAccount).locationsCache.get(dialogId);
        if (!this.firstLocationsLoaded) {
            LocationController.getInstance(currentAccount).loadLiveLocations(dialogId);
            this.firstLocationsLoaded = true;
        }
        int locationSharingCount = 0;
        TLRPC.User notYouUser = null;
        if (messages != null) {
            long currentUserId = UserConfig.getInstance(currentAccount).getClientUserId();
            int date = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
            int a = 0;
            while (a < messages.size()) {
                TLRPC.Message message = messages.get(a);
                if (message.media == null) {
                    chatActivity = chatActivity2;
                } else if (message.date + message.media.period <= date) {
                    chatActivity = chatActivity2;
                } else {
                    long fromId = MessageObject.getFromChatId(message);
                    if (notYouUser != null || fromId == currentUserId) {
                        chatActivity = chatActivity2;
                    } else {
                        chatActivity = chatActivity2;
                        notYouUser = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(fromId));
                    }
                    locationSharingCount++;
                }
                a++;
                chatActivity2 = chatActivity;
            }
        }
        if (this.lastLocationSharingCount == locationSharingCount) {
            return;
        }
        this.lastLocationSharingCount = locationSharingCount;
        String liveLocation = LocaleController.getString("LiveLocationContext", R.string.LiveLocationContext);
        if (locationSharingCount == 0) {
            fullString = liveLocation;
        } else {
            int otherSharingCount = locationSharingCount - 1;
            if (!LocationController.getInstance(currentAccount).isSharingLocation(dialogId)) {
                fullString = otherSharingCount != 0 ? String.format("%1$s - %2$s %3$s", liveLocation, UserObject.getFirstName(notYouUser), LocaleController.formatPluralString("AndOther", otherSharingCount, new Object[0])) : String.format("%1$s - %2$s", liveLocation, UserObject.getFirstName(notYouUser));
            } else if (otherSharingCount != 0) {
                fullString = (otherSharingCount != 1 || notYouUser == null) ? String.format("%1$s - %2$s %3$s", liveLocation, LocaleController.getString("ChatYourSelfName", R.string.ChatYourSelfName), LocaleController.formatPluralString("AndOther", otherSharingCount, new Object[0])) : String.format("%1$s - %2$s", liveLocation, LocaleController.formatString("SharingYouAndOtherName", R.string.SharingYouAndOtherName, UserObject.getFirstName(notYouUser)));
            } else {
                fullString = String.format("%1$s - %2$s", liveLocation, LocaleController.getString("ChatYourSelfName", R.string.ChatYourSelfName));
            }
        }
        if (fullString.equals(this.lastString)) {
            return;
        }
        this.lastString = fullString;
        int start = fullString.indexOf(liveLocation);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(fullString);
        int i = 0;
        while (i < 2) {
            AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
            TextView textView = i == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
            if (textView != null) {
                textView.setEllipsize(TextUtils.TruncateAt.END);
            }
            i++;
        }
        if (start >= 0) {
            TypefaceSpan span = new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM), 0, getThemedColor(Theme.key_inappPlayerPerformer));
            stringBuilder.setSpan(span, start, liveLocation.length() + start, 18);
        }
        this.titleTextView.setText(stringBuilder, false);
    }

    public void checkPlayer(boolean create) {
        SpannableStringBuilder stringBuilder;
        boolean z = true;
        if (this.visible) {
            int i = this.currentStyle;
            if (i == 1 || i == 3) {
                return;
            }
            if ((i == 4 || i == 5) && !isPlayingVoice()) {
                return;
            }
        }
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        View fragmentView = this.fragment.getFragmentView();
        if (!create && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            create = true;
        }
        boolean wasVisible = this.visible;
        if (messageObject == null || messageObject.getId() == 0 || messageObject.isVideo()) {
            this.lastMessageObject = null;
            boolean callAvailable = this.supportsCalls && VoIPService.getSharedInstance() != null && !VoIPService.getSharedInstance().isHangingUp() && VoIPService.getSharedInstance().getCallState() != 15 && !GroupCallPip.isShowing();
            if (!isPlayingVoice() && !callAvailable && (this.fragment instanceof ChatActivity) && !GroupCallPip.isShowing()) {
                ChatObject.Call call = ((ChatActivity) this.fragment).getGroupCall();
                callAvailable = call != null && call.shouldShowPanel();
            }
            if (callAvailable) {
                checkCall(false);
            } else if (this.visible) {
                ActionBarMenuItem actionBarMenuItem = this.playbackSpeedButton;
                if (actionBarMenuItem != null && actionBarMenuItem.isSubMenuShowing()) {
                    this.playbackSpeedButton.toggleSubMenu();
                }
                this.visible = false;
                if (create) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                AnimatorSet animatorSet = this.animatorSet;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.animatorSet = null;
                }
                this.animationIndex = NotificationCenter.getInstance(this.account).setAnimationInProgress(this.animationIndex, null);
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animatorSet = animatorSet2;
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this, "topPadding", 0.0f));
                this.animatorSet.setDuration(200L);
                FragmentContextViewDelegate fragmentContextViewDelegate = this.delegate;
                if (fragmentContextViewDelegate != null) {
                    fragmentContextViewDelegate.onAnimation(true, false);
                }
                this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FragmentContextView.10
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        NotificationCenter.getInstance(FragmentContextView.this.account).onAnimationFinish(FragmentContextView.this.animationIndex);
                        if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                            FragmentContextView.this.setVisibility(8);
                            if (FragmentContextView.this.delegate != null) {
                                FragmentContextView.this.delegate.onAnimation(false, false);
                            }
                            FragmentContextView.this.animatorSet = null;
                            if (!FragmentContextView.this.checkCallAfterAnimation) {
                                if (FragmentContextView.this.checkPlayerAfterAnimation) {
                                    FragmentContextView.this.checkPlayer(false);
                                } else if (FragmentContextView.this.checkImportAfterAnimation) {
                                    FragmentContextView.this.checkImport(false);
                                }
                            } else {
                                FragmentContextView.this.checkCall(false);
                            }
                            FragmentContextView.this.checkCallAfterAnimation = false;
                            FragmentContextView.this.checkPlayerAfterAnimation = false;
                            FragmentContextView.this.checkImportAfterAnimation = false;
                        }
                    }
                });
                this.animatorSet.start();
            } else {
                setVisibility(8);
            }
        } else if (this.currentStyle != 0 && this.animatorSet != null && !create) {
            this.checkPlayerAfterAnimation = true;
        } else {
            int prevStyle = this.currentStyle;
            updateStyle(0);
            if (create && this.topPadding == 0.0f) {
                updatePaddings();
                setTopPadding(AndroidUtilities.dp2(getStyleHeight()));
                FragmentContextViewDelegate fragmentContextViewDelegate2 = this.delegate;
                if (fragmentContextViewDelegate2 != null) {
                    fragmentContextViewDelegate2.onAnimation(true, true);
                    this.delegate.onAnimation(false, true);
                }
            }
            if (!this.visible) {
                if (!create) {
                    AnimatorSet animatorSet3 = this.animatorSet;
                    if (animatorSet3 != null) {
                        animatorSet3.cancel();
                        this.animatorSet = null;
                    }
                    this.animationIndex = NotificationCenter.getInstance(this.account).setAnimationInProgress(this.animationIndex, null);
                    this.animatorSet = new AnimatorSet();
                    FragmentContextView fragmentContextView = this.additionalContextView;
                    if (fragmentContextView != null && fragmentContextView.getVisibility() == 0) {
                        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(getStyleHeight() + this.additionalContextView.getStyleHeight());
                    } else {
                        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(getStyleHeight());
                    }
                    FragmentContextViewDelegate fragmentContextViewDelegate3 = this.delegate;
                    if (fragmentContextViewDelegate3 != null) {
                        fragmentContextViewDelegate3.onAnimation(true, true);
                    }
                    this.animatorSet.playTogether(ObjectAnimator.ofFloat(this, "topPadding", AndroidUtilities.dp2(getStyleHeight())));
                    this.animatorSet.setDuration(200L);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FragmentContextView.11
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            NotificationCenter.getInstance(FragmentContextView.this.account).onAnimationFinish(FragmentContextView.this.animationIndex);
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                                if (FragmentContextView.this.delegate != null) {
                                    FragmentContextView.this.delegate.onAnimation(false, true);
                                }
                                FragmentContextView.this.animatorSet = null;
                                if (!FragmentContextView.this.checkCallAfterAnimation) {
                                    if (FragmentContextView.this.checkPlayerAfterAnimation) {
                                        FragmentContextView.this.checkPlayer(false);
                                    } else if (FragmentContextView.this.checkImportAfterAnimation) {
                                        FragmentContextView.this.checkImport(false);
                                    }
                                } else {
                                    FragmentContextView.this.checkCall(false);
                                }
                                FragmentContextView.this.checkCallAfterAnimation = false;
                                FragmentContextView.this.checkPlayerAfterAnimation = false;
                                FragmentContextView.this.checkImportAfterAnimation = false;
                            }
                        }
                    });
                    this.animatorSet.start();
                }
                this.visible = true;
                setVisibility(0);
            }
            if (MediaController.getInstance().isMessagePaused()) {
                this.playPauseDrawable.setPause(false, !create);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPlay", R.string.AccActionPlay));
            } else {
                this.playPauseDrawable.setPause(true, !create);
                this.playButton.setContentDescription(LocaleController.getString("AccActionPause", R.string.AccActionPause));
            }
            if (this.lastMessageObject != messageObject || prevStyle != 0) {
                this.lastMessageObject = messageObject;
                if (messageObject.isVoice() || this.lastMessageObject.isRoundVideo()) {
                    this.isMusic = false;
                    ActionBarMenuItem actionBarMenuItem2 = this.playbackSpeedButton;
                    if (actionBarMenuItem2 != null) {
                        actionBarMenuItem2.setAlpha(1.0f);
                        this.playbackSpeedButton.setEnabled(true);
                    }
                    this.titleTextView.setPadding(0, 0, AndroidUtilities.dp(44.0f), 0);
                    stringBuilder = new SpannableStringBuilder(String.format("%s %s", messageObject.getMusicAuthor(), messageObject.getMusicTitle()));
                    int i2 = 0;
                    while (i2 < 2) {
                        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
                        TextView textView = i2 == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                        if (textView != null) {
                            textView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                        }
                        i2++;
                    }
                    updatePlaybackButton();
                } else {
                    this.isMusic = true;
                    if (this.playbackSpeedButton != null) {
                        if (messageObject.getDuration() >= 600) {
                            this.playbackSpeedButton.setAlpha(1.0f);
                            this.playbackSpeedButton.setEnabled(true);
                            this.titleTextView.setPadding(0, 0, AndroidUtilities.dp(44.0f), 0);
                            updatePlaybackButton();
                        } else {
                            this.playbackSpeedButton.setAlpha(0.0f);
                            this.playbackSpeedButton.setEnabled(false);
                            this.titleTextView.setPadding(0, 0, 0, 0);
                        }
                    } else {
                        this.titleTextView.setPadding(0, 0, 0, 0);
                    }
                    stringBuilder = new SpannableStringBuilder(String.format("%s - %s", messageObject.getMusicAuthor(), messageObject.getMusicTitle()));
                    int i3 = 0;
                    while (i3 < 2) {
                        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher2 = this.titleTextView;
                        TextView textView2 = i3 == 0 ? clippingTextViewSwitcher2.getTextView() : clippingTextViewSwitcher2.getNextTextView();
                        if (textView2 != null) {
                            textView2.setEllipsize(TextUtils.TruncateAt.END);
                        }
                        i3++;
                    }
                }
                TypefaceSpan span = new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM), 0, getThemedColor(Theme.key_inappPlayerPerformer));
                stringBuilder.setSpan(span, 0, messageObject.getMusicAuthor().length(), 18);
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher3 = this.titleTextView;
                if (create || !wasVisible || !this.isMusic) {
                    z = false;
                }
                clippingTextViewSwitcher3.setText(stringBuilder, z);
            }
        }
    }

    public void checkImport(boolean create) {
        int i;
        BaseFragment baseFragment = this.fragment;
        if (baseFragment instanceof ChatActivity) {
            if (this.visible && ((i = this.currentStyle) == 1 || i == 3)) {
                return;
            }
            ChatActivity chatActivity = (ChatActivity) baseFragment;
            SendMessagesHelper.ImportingHistory importingHistory = chatActivity.getSendMessagesHelper().getImportingHistory(chatActivity.getDialogId());
            View fragmentView = this.fragment.getFragmentView();
            if (!create && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
                create = true;
            }
            Dialog dialog = chatActivity.getVisibleDialog();
            if ((isPlayingVoice() || chatActivity.shouldShowImport() || ((dialog instanceof ImportingAlert) && !((ImportingAlert) dialog).isDismissed())) && importingHistory != null) {
                importingHistory = null;
            }
            if (importingHistory == null) {
                if (this.visible && ((create && this.currentStyle == -1) || this.currentStyle == 5)) {
                    this.visible = false;
                    if (create) {
                        if (getVisibility() != 8) {
                            setVisibility(8);
                        }
                        setTopPadding(0.0f);
                        return;
                    }
                    AnimatorSet animatorSet = this.animatorSet;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                        this.animatorSet = null;
                    }
                    final int currentAccount = this.account;
                    this.animationIndex = NotificationCenter.getInstance(currentAccount).setAnimationInProgress(this.animationIndex, null);
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.animatorSet = animatorSet2;
                    animatorSet2.playTogether(ObjectAnimator.ofFloat(this, "topPadding", 0.0f));
                    this.animatorSet.setDuration(220L);
                    this.animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FragmentContextView.12
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            NotificationCenter.getInstance(currentAccount).onAnimationFinish(FragmentContextView.this.animationIndex);
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                                FragmentContextView.this.setVisibility(8);
                                FragmentContextView.this.animatorSet = null;
                                if (!FragmentContextView.this.checkCallAfterAnimation) {
                                    if (FragmentContextView.this.checkPlayerAfterAnimation) {
                                        FragmentContextView.this.checkPlayer(false);
                                    } else if (FragmentContextView.this.checkImportAfterAnimation) {
                                        FragmentContextView.this.checkImport(false);
                                    }
                                } else {
                                    FragmentContextView.this.checkCall(false);
                                }
                                FragmentContextView.this.checkCallAfterAnimation = false;
                                FragmentContextView.this.checkPlayerAfterAnimation = false;
                                FragmentContextView.this.checkImportAfterAnimation = false;
                            }
                        }
                    });
                    this.animatorSet.start();
                    return;
                }
                int i2 = this.currentStyle;
                if (i2 == -1 || i2 == 5) {
                    this.visible = false;
                    setVisibility(8);
                }
            } else if (this.currentStyle != 5 && this.animatorSet != null && !create) {
                this.checkImportAfterAnimation = true;
            } else {
                updateStyle(5);
                if (create && this.topPadding == 0.0f) {
                    updatePaddings();
                    setTopPadding(AndroidUtilities.dp2(getStyleHeight()));
                    FragmentContextViewDelegate fragmentContextViewDelegate = this.delegate;
                    if (fragmentContextViewDelegate != null) {
                        fragmentContextViewDelegate.onAnimation(true, true);
                        this.delegate.onAnimation(false, true);
                    }
                }
                if (!this.visible) {
                    if (!create) {
                        AnimatorSet animatorSet3 = this.animatorSet;
                        if (animatorSet3 != null) {
                            animatorSet3.cancel();
                            this.animatorSet = null;
                        }
                        this.animationIndex = NotificationCenter.getInstance(this.account).setAnimationInProgress(this.animationIndex, null);
                        this.animatorSet = new AnimatorSet();
                        FragmentContextView fragmentContextView = this.additionalContextView;
                        if (fragmentContextView != null && fragmentContextView.getVisibility() == 0) {
                            ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(getStyleHeight() + this.additionalContextView.getStyleHeight());
                        } else {
                            ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(getStyleHeight());
                        }
                        FragmentContextViewDelegate fragmentContextViewDelegate2 = this.delegate;
                        if (fragmentContextViewDelegate2 != null) {
                            fragmentContextViewDelegate2.onAnimation(true, true);
                        }
                        this.animatorSet.playTogether(ObjectAnimator.ofFloat(this, "topPadding", AndroidUtilities.dp2(getStyleHeight())));
                        this.animatorSet.setDuration(200L);
                        this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FragmentContextView.13
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                NotificationCenter.getInstance(FragmentContextView.this.account).onAnimationFinish(FragmentContextView.this.animationIndex);
                                if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                                    if (FragmentContextView.this.delegate != null) {
                                        FragmentContextView.this.delegate.onAnimation(false, true);
                                    }
                                    FragmentContextView.this.animatorSet = null;
                                    if (!FragmentContextView.this.checkCallAfterAnimation) {
                                        if (FragmentContextView.this.checkPlayerAfterAnimation) {
                                            FragmentContextView.this.checkPlayer(false);
                                        } else if (FragmentContextView.this.checkImportAfterAnimation) {
                                            FragmentContextView.this.checkImport(false);
                                        }
                                    } else {
                                        FragmentContextView.this.checkCall(false);
                                    }
                                    FragmentContextView.this.checkCallAfterAnimation = false;
                                    FragmentContextView.this.checkPlayerAfterAnimation = false;
                                    FragmentContextView.this.checkImportAfterAnimation = false;
                                }
                            }
                        });
                        this.animatorSet.start();
                    }
                    this.visible = true;
                    setVisibility(0);
                }
                if (this.currentProgress != importingHistory.uploadProgress) {
                    this.currentProgress = importingHistory.uploadProgress;
                    this.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ImportUploading", R.string.ImportUploading, Integer.valueOf(importingHistory.uploadProgress))), false);
                }
            }
        }
    }

    private boolean isPlayingVoice() {
        MessageObject messageObject = MediaController.getInstance().getPlayingMessageObject();
        return messageObject != null && messageObject.isVoice();
    }

    public void checkCall(boolean create) {
        boolean create2;
        boolean groupActive;
        boolean callAvailable;
        int newStyle;
        boolean z;
        int[] iArr;
        int i;
        int[] iArr2;
        int i2;
        ChatObject.Call call;
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (this.visible && this.currentStyle == 5 && (voIPService == null || voIPService.isHangingUp())) {
            return;
        }
        View fragmentView = this.fragment.getFragmentView();
        if (!create && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            create2 = true;
        } else {
            create2 = create;
        }
        if (GroupCallPip.isShowing()) {
            callAvailable = false;
            groupActive = false;
        } else {
            boolean callAvailable2 = GroupCallActivity.groupCallUiVisible;
            callAvailable = !callAvailable2 && this.supportsCalls && voIPService != null && !voIPService.isHangingUp();
            if (voIPService != null && voIPService.groupCall != null && (voIPService.groupCall.call instanceof TLRPC.TL_groupCallDiscarded)) {
                callAvailable = false;
            }
            groupActive = false;
            if (!isPlayingVoice() && !GroupCallActivity.groupCallUiVisible && this.supportsCalls && !callAvailable) {
                BaseFragment baseFragment = this.fragment;
                if ((baseFragment instanceof ChatActivity) && (call = ((ChatActivity) baseFragment).getGroupCall()) != null && call.shouldShowPanel()) {
                    callAvailable = true;
                    groupActive = true;
                }
            }
        }
        if (!callAvailable) {
            boolean z2 = this.visible;
            if (z2 && ((create2 && this.currentStyle == -1) || (i2 = this.currentStyle) == 4 || i2 == 3 || i2 == 1)) {
                this.visible = false;
                if (create2) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                } else {
                    AnimatorSet animatorSet = this.animatorSet;
                    if (animatorSet == null) {
                        iArr2 = null;
                    } else {
                        animatorSet.cancel();
                        iArr2 = null;
                        this.animatorSet = null;
                    }
                    final int currentAccount = this.account;
                    this.animationIndex = NotificationCenter.getInstance(currentAccount).setAnimationInProgress(this.animationIndex, iArr2);
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.animatorSet = animatorSet2;
                    animatorSet2.playTogether(ObjectAnimator.ofFloat(this, "topPadding", 0.0f));
                    this.animatorSet.setDuration(220L);
                    this.animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FragmentContextView.14
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            NotificationCenter.getInstance(currentAccount).onAnimationFinish(FragmentContextView.this.animationIndex);
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                                FragmentContextView.this.setVisibility(8);
                                FragmentContextView.this.animatorSet = null;
                                if (!FragmentContextView.this.checkCallAfterAnimation) {
                                    if (FragmentContextView.this.checkPlayerAfterAnimation) {
                                        FragmentContextView.this.checkPlayer(false);
                                    } else if (FragmentContextView.this.checkImportAfterAnimation) {
                                        FragmentContextView.this.checkImport(false);
                                    }
                                } else {
                                    FragmentContextView.this.checkCall(false);
                                }
                                FragmentContextView.this.checkCallAfterAnimation = false;
                                FragmentContextView.this.checkPlayerAfterAnimation = false;
                                FragmentContextView.this.checkImportAfterAnimation = false;
                            }
                        }
                    });
                    this.animatorSet.start();
                }
            } else if (z2 && ((i = this.currentStyle) == -1 || i == 4 || i == 3 || i == 1)) {
                this.visible = false;
                setVisibility(8);
            }
            if (create2) {
                BaseFragment baseFragment2 = this.fragment;
                if ((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).openedWithLivestream() && !GroupCallPip.isShowing()) {
                    BulletinFactory.of(this.fragment).createSimpleBulletin(R.raw.linkbroken, LocaleController.getString("InviteExpired", R.string.InviteExpired)).show();
                    return;
                }
            }
            return;
        }
        if (groupActive) {
            newStyle = 4;
        } else if (voIPService.groupCall != null) {
            newStyle = 3;
        } else {
            newStyle = 1;
        }
        int i3 = this.currentStyle;
        if (newStyle != i3 && this.animatorSet != null && !create2) {
            this.checkCallAfterAnimation = true;
        } else if (newStyle != i3 && this.visible && !create2) {
            AnimatorSet animatorSet3 = this.animatorSet;
            if (animatorSet3 == null) {
                iArr = null;
            } else {
                animatorSet3.cancel();
                iArr = null;
                this.animatorSet = null;
            }
            final int currentAccount2 = this.account;
            this.animationIndex = NotificationCenter.getInstance(currentAccount2).setAnimationInProgress(this.animationIndex, iArr);
            AnimatorSet animatorSet4 = new AnimatorSet();
            this.animatorSet = animatorSet4;
            animatorSet4.playTogether(ObjectAnimator.ofFloat(this, "topPadding", 0.0f));
            this.animatorSet.setDuration(220L);
            this.animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FragmentContextView.15
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    NotificationCenter.getInstance(currentAccount2).onAnimationFinish(FragmentContextView.this.animationIndex);
                    if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                        FragmentContextView.this.visible = false;
                        FragmentContextView.this.animatorSet = null;
                        FragmentContextView.this.checkCall(false);
                    }
                }
            });
            this.animatorSet.start();
        } else {
            if (groupActive) {
                boolean updateAnimated = i3 == 4 && this.visible;
                updateStyle(4);
                ChatObject.Call call2 = ((ChatActivity) this.fragment).getGroupCall();
                TLRPC.Chat chat = ((ChatActivity) this.fragment).getCurrentChat();
                if (!call2.isScheduled()) {
                    this.timeLayout = null;
                    this.joinButton.setVisibility(0);
                    if (call2.call.rtmp_stream) {
                        this.titleTextView.setText(LocaleController.getString((int) R.string.VoipChannelVoiceChat), false);
                    } else if (ChatObject.isChannelOrGiga(chat)) {
                        this.titleTextView.setText(LocaleController.getString("VoipChannelVoiceChat", R.string.VoipChannelVoiceChat), false);
                    } else {
                        this.titleTextView.setText(LocaleController.getString("VoipGroupVoiceChat", R.string.VoipGroupVoiceChat), false);
                    }
                    if (call2.call.participants_count == 0) {
                        this.subtitleTextView.setText(LocaleController.getString(call2.call.rtmp_stream ? R.string.ViewersWatchingNobody : R.string.MembersTalkingNobody), false);
                    } else {
                        this.subtitleTextView.setText(LocaleController.formatPluralString(call2.call.rtmp_stream ? "ViewersWatching" : "Participants", call2.call.participants_count, new Object[0]), false);
                    }
                    this.frameLayout.invalidate();
                } else {
                    if (this.gradientPaint == null) {
                        TextPaint textPaint = new TextPaint(1);
                        this.gradientTextPaint = textPaint;
                        textPaint.setColor(-1);
                        this.gradientTextPaint.setTextSize(AndroidUtilities.dp(14.0f));
                        this.gradientTextPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                        Paint paint = new Paint(1);
                        this.gradientPaint = paint;
                        paint.setColor(-1);
                        this.matrix = new Matrix();
                    }
                    this.joinButton.setVisibility(8);
                    if (!TextUtils.isEmpty(call2.call.title)) {
                        z = false;
                        this.titleTextView.setText(call2.call.title, false);
                    } else {
                        z = false;
                        if (ChatObject.isChannelOrGiga(chat)) {
                            this.titleTextView.setText(LocaleController.getString("VoipChannelScheduledVoiceChat", R.string.VoipChannelScheduledVoiceChat), false);
                        } else {
                            this.titleTextView.setText(LocaleController.getString("VoipGroupScheduledVoiceChat", R.string.VoipGroupScheduledVoiceChat), false);
                        }
                    }
                    this.subtitleTextView.setText(LocaleController.formatStartsTime(call2.call.schedule_date, 4), z);
                    if (!this.scheduleRunnableScheduled) {
                        this.scheduleRunnableScheduled = true;
                        this.updateScheduleTimeRunnable.run();
                    }
                }
                updateAvatars(this.avatars.avatarsDarawable.wasDraw && updateAnimated);
            } else if (voIPService == null || voIPService.groupCall == null) {
                updateAvatars(this.currentStyle == 1);
                updateStyle(1);
            } else {
                updateAvatars(this.currentStyle == 3);
                updateStyle(3);
            }
            if (!this.visible) {
                if (!create2) {
                    AnimatorSet animatorSet5 = this.animatorSet;
                    if (animatorSet5 != null) {
                        animatorSet5.cancel();
                        this.animatorSet = null;
                    }
                    this.animatorSet = new AnimatorSet();
                    FragmentContextView fragmentContextView = this.additionalContextView;
                    if (fragmentContextView != null && fragmentContextView.getVisibility() == 0) {
                        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(getStyleHeight() + this.additionalContextView.getStyleHeight());
                    } else {
                        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = -AndroidUtilities.dp(getStyleHeight());
                    }
                    final int currentAccount3 = this.account;
                    this.animationIndex = NotificationCenter.getInstance(currentAccount3).setAnimationInProgress(this.animationIndex, new int[]{NotificationCenter.messagesDidLoad});
                    this.animatorSet.playTogether(ObjectAnimator.ofFloat(this, "topPadding", AndroidUtilities.dp2(getStyleHeight())));
                    this.animatorSet.setDuration(220L);
                    this.animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.FragmentContextView.16
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            NotificationCenter.getInstance(currentAccount3).onAnimationFinish(FragmentContextView.this.animationIndex);
                            if (FragmentContextView.this.animatorSet != null && FragmentContextView.this.animatorSet.equals(animation)) {
                                FragmentContextView.this.animatorSet = null;
                            }
                            if (!FragmentContextView.this.checkCallAfterAnimation) {
                                if (FragmentContextView.this.checkPlayerAfterAnimation) {
                                    FragmentContextView.this.checkPlayer(false);
                                } else if (FragmentContextView.this.checkImportAfterAnimation) {
                                    FragmentContextView.this.checkImport(false);
                                }
                            } else {
                                FragmentContextView.this.checkCall(false);
                            }
                            FragmentContextView.this.checkCallAfterAnimation = false;
                            FragmentContextView.this.checkPlayerAfterAnimation = false;
                            FragmentContextView.this.checkImportAfterAnimation = false;
                            FragmentContextView.this.startJoinFlickerAnimation();
                        }
                    });
                    this.animatorSet.start();
                } else {
                    updatePaddings();
                    setTopPadding(AndroidUtilities.dp2(getStyleHeight()));
                    startJoinFlickerAnimation();
                }
                this.visible = true;
                setVisibility(0);
            }
        }
    }

    public void startJoinFlickerAnimation() {
        if (this.joinButtonFlicker.getProgress() > 1.0f) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.FragmentContextView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    FragmentContextView.this.m2648x92aab17d();
                }
            }, 150L);
        }
    }

    /* renamed from: lambda$startJoinFlickerAnimation$12$org-telegram-ui-Components-FragmentContextView */
    public /* synthetic */ void m2648x92aab17d() {
        this.joinButtonFlicker.setProgress(0.0f);
        this.joinButton.invalidate();
    }

    private void updateAvatars(boolean animated) {
        TLRPC.User userCall;
        ChatObject.Call call;
        int currentAccount;
        if (!animated && this.avatars.avatarsDarawable.transitionProgressAnimator != null) {
            this.avatars.avatarsDarawable.transitionProgressAnimator.cancel();
            this.avatars.avatarsDarawable.transitionProgressAnimator = null;
        }
        if (this.avatars.avatarsDarawable.transitionProgressAnimator == null) {
            if (this.currentStyle == 4) {
                BaseFragment baseFragment = this.fragment;
                if (baseFragment instanceof ChatActivity) {
                    ChatActivity chatActivity = (ChatActivity) baseFragment;
                    call = chatActivity.getGroupCall();
                    currentAccount = chatActivity.getCurrentAccount();
                } else {
                    call = null;
                    currentAccount = this.account;
                }
                userCall = null;
            } else if (VoIPService.getSharedInstance() != null) {
                call = VoIPService.getSharedInstance().groupCall;
                userCall = this.fragment instanceof ChatActivity ? null : VoIPService.getSharedInstance().getUser();
                currentAccount = VoIPService.getSharedInstance().getAccount();
            } else {
                call = null;
                userCall = null;
                currentAccount = this.account;
            }
            int i = 0;
            if (call != null) {
                int N = call.sortedParticipants.size();
                for (int a = 0; a < 3; a++) {
                    if (a < N) {
                        this.avatars.setObject(a, currentAccount, call.sortedParticipants.get(a));
                    } else {
                        this.avatars.setObject(a, currentAccount, null);
                    }
                }
            } else if (userCall != null) {
                this.avatars.setObject(0, currentAccount, userCall);
                for (int a2 = 1; a2 < 3; a2++) {
                    this.avatars.setObject(a2, currentAccount, null);
                }
            } else {
                for (int a3 = 0; a3 < 3; a3++) {
                    this.avatars.setObject(a3, currentAccount, null);
                }
            }
            this.avatars.commitTransition(animated);
            if (this.currentStyle == 4 && call != null) {
                if (!call.call.rtmp_stream) {
                    i = Math.min(3, call.sortedParticipants.size());
                }
                int N2 = i;
                int x = 10;
                if (N2 != 0) {
                    x = 10 + ((N2 - 1) * 24) + 10 + 32;
                }
                if (animated) {
                    int leftMargin = ((FrameLayout.LayoutParams) this.titleTextView.getLayoutParams()).leftMargin;
                    if (AndroidUtilities.dp(x) != leftMargin) {
                        float dx = (this.titleTextView.getTranslationX() + leftMargin) - AndroidUtilities.dp(x);
                        this.titleTextView.setTranslationX(dx);
                        this.subtitleTextView.setTranslationX(dx);
                        this.titleTextView.animate().translationX(0.0f).setDuration(220L).setInterpolator(CubicBezierInterpolator.DEFAULT);
                        this.subtitleTextView.animate().translationX(0.0f).setDuration(220L).setInterpolator(CubicBezierInterpolator.DEFAULT);
                    }
                } else {
                    this.titleTextView.animate().cancel();
                    this.subtitleTextView.animate().cancel();
                    this.titleTextView.setTranslationX(0.0f);
                    this.subtitleTextView.setTranslationX(0.0f);
                }
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, 51, x, 5.0f, call.isScheduled() ? 90.0f : 36.0f, 0.0f));
                this.subtitleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, 51, x, 25.0f, call.isScheduled() ? 90.0f : 36.0f, 0.0f));
                return;
            }
            return;
        }
        this.avatars.updateAfterTransitionEnd();
    }

    public void setCollapseTransition(boolean show, float extraHeight, float progress) {
        this.collapseTransition = show;
        this.extraHeight = extraHeight;
        this.collapseProgress = progress;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.drawOverlay && getVisibility() != 0) {
            return;
        }
        boolean clipped = false;
        int i = this.currentStyle;
        if (i == 3 || i == 1) {
            if (GroupCallActivity.groupCallInstance != null || Theme.getFragmentContextViewWavesDrawable().getState() != 3) {
            }
            Theme.getFragmentContextViewWavesDrawable().updateState(this.wasDraw);
            float progress = this.topPadding / AndroidUtilities.dp(getStyleHeight());
            if (this.collapseTransition) {
                Theme.getFragmentContextViewWavesDrawable().draw(0.0f, this.extraHeight + (AndroidUtilities.dp(getStyleHeight()) - this.topPadding), getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(2.0f), canvas, null, Math.min(progress, 1.0f - this.collapseProgress));
            } else {
                Theme.getFragmentContextViewWavesDrawable().draw(0.0f, AndroidUtilities.dp(getStyleHeight()) - this.topPadding, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(2.0f), canvas, this, progress);
            }
            float clipTop = AndroidUtilities.dp(getStyleHeight()) - this.topPadding;
            if (this.collapseTransition) {
                clipTop += this.extraHeight;
            }
            if (clipTop > getMeasuredHeight()) {
                return;
            }
            clipped = true;
            canvas.save();
            canvas.clipRect(0.0f, clipTop, getMeasuredWidth(), getMeasuredHeight());
            invalidate();
        }
        super.dispatchDraw(canvas);
        if (clipped) {
            canvas.restore();
        }
        this.wasDraw = true;
    }

    public void setDrawOverlay(boolean drawOverlay) {
        this.drawOverlay = drawOverlay;
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        int i = this.currentStyle;
        if ((i == 3 || i == 1) && getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    public boolean isCallStyle() {
        int i = this.currentStyle;
        return i == 3 || i == 1;
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        updatePaddings();
        setTopPadding(this.topPadding);
        if (visibility == 8) {
            this.wasDraw = false;
        }
    }

    private void updatePaddings() {
        int margin = 0;
        if (getVisibility() == 0) {
            margin = 0 - AndroidUtilities.dp(getStyleHeight());
        }
        FragmentContextView fragmentContextView = this.additionalContextView;
        if (fragmentContextView != null && fragmentContextView.getVisibility() == 0) {
            int margin2 = margin - AndroidUtilities.dp(this.additionalContextView.getStyleHeight());
            ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = margin2;
            ((FrameLayout.LayoutParams) this.additionalContextView.getLayoutParams()).topMargin = margin2;
            return;
        }
        ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = margin;
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onStateChanged(int state) {
        updateCallTitle();
    }

    private void updateCallTitle() {
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            int i = this.currentStyle;
            if (i == 1 || i == 3) {
                int currentCallState = service.getCallState();
                if (!service.isSwitchingStream() && (currentCallState == 1 || currentCallState == 2 || currentCallState == 6 || currentCallState == 5)) {
                    this.titleTextView.setText(LocaleController.getString("VoipGroupConnecting", R.string.VoipGroupConnecting), false);
                } else if (service.getChat() != null) {
                    if (!TextUtils.isEmpty(service.groupCall.call.title)) {
                        this.titleTextView.setText(service.groupCall.call.title, false);
                        return;
                    }
                    BaseFragment baseFragment = this.fragment;
                    if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).getCurrentChat() != null && ((ChatActivity) this.fragment).getCurrentChat().id == service.getChat().id) {
                        TLRPC.Chat chat = ((ChatActivity) this.fragment).getCurrentChat();
                        if (VoIPService.hasRtmpStream()) {
                            this.titleTextView.setText(LocaleController.getString((int) R.string.VoipChannelViewVoiceChat), false);
                            return;
                        } else if (ChatObject.isChannelOrGiga(chat)) {
                            this.titleTextView.setText(LocaleController.getString("VoipChannelViewVoiceChat", R.string.VoipChannelViewVoiceChat), false);
                            return;
                        } else {
                            this.titleTextView.setText(LocaleController.getString("VoipGroupViewVoiceChat", R.string.VoipGroupViewVoiceChat), false);
                            return;
                        }
                    }
                    this.titleTextView.setText(service.getChat().title, false);
                } else if (service.getUser() != null) {
                    TLRPC.User user = service.getUser();
                    BaseFragment baseFragment2 = this.fragment;
                    if ((baseFragment2 instanceof ChatActivity) && ((ChatActivity) baseFragment2).getCurrentUser() != null && ((ChatActivity) this.fragment).getCurrentUser().id == user.id) {
                        this.titleTextView.setText(LocaleController.getString("ReturnToCall", R.string.ReturnToCall));
                    } else {
                        this.titleTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                    }
                }
            }
        }
    }

    private int getTitleTextColor() {
        int i = this.currentStyle;
        if (i == 4) {
            return getThemedColor(Theme.key_inappPlayerPerformer);
        }
        if (i == 1 || i == 3) {
            return getThemedColor(Theme.key_returnToCallText);
        }
        return getThemedColor(Theme.key_inappPlayerTitle);
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
