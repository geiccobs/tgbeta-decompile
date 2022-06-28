package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class GroupCallPipButton extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener {
    public static final float MAX_AMPLITUDE = 8500.0f;
    public static final int MUTE_BUTTON_STATE_MUTE = 1;
    public static final int MUTE_BUTTON_STATE_MUTED_BY_ADMIN = 3;
    public static final int MUTE_BUTTON_STATE_RECONNECT = 2;
    public static final int MUTE_BUTTON_STATE_UNMUTE = 0;
    float amplitude;
    float animateAmplitudeDiff;
    float animateToAmplitude;
    private RLottieDrawable bigMicDrawable;
    private final int currentAccount;
    WeavingState currentState;
    long lastStubUpdateAmplitude;
    private RLottieImageView muteButton;
    float pinnedProgress;
    boolean prepareToRemove;
    private final LinearGradient prepareToRemoveShader;
    float pressedProgress;
    boolean pressedState;
    WeavingState previousState;
    float progressToPrepareRemove;
    float removeAngle;
    public boolean removed;
    private boolean stub;
    Paint paint = new Paint(1);
    BlobDrawable blobDrawable = new BlobDrawable(8);
    BlobDrawable blobDrawable2 = new BlobDrawable(9);
    float progressToState = 1.0f;
    Matrix matrix = new Matrix();
    float wavesEnter = 0.0f;
    Random random = new Random();
    WeavingState[] states = new WeavingState[4];
    OvershootInterpolator overshootInterpolator = new OvershootInterpolator();

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

    public GroupCallPipButton(Context context, int currentAccount, boolean stub) {
        super(context);
        this.stub = stub;
        this.currentAccount = currentAccount;
        for (int i = 0; i < 4; i++) {
            this.states[i] = new WeavingState(i);
        }
        this.blobDrawable.maxRadius = AndroidUtilities.dp(37.0f);
        this.blobDrawable.minRadius = AndroidUtilities.dp(32.0f);
        this.blobDrawable2.maxRadius = AndroidUtilities.dp(37.0f);
        this.blobDrawable2.minRadius = AndroidUtilities.dp(32.0f);
        this.blobDrawable.generateBlob();
        this.blobDrawable2.generateBlob();
        this.bigMicDrawable = new RLottieDrawable(R.raw.voice_outlined, "2131558578", AndroidUtilities.dp(22.0f), AndroidUtilities.dp(30.0f), true, null);
        setWillNotDraw(false);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.muteButton = rLottieImageView;
        rLottieImageView.setAnimation(this.bigMicDrawable);
        this.muteButton.setScaleType(ImageView.ScaleType.CENTER);
        addView(this.muteButton);
        this.prepareToRemoveShader = new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(350.0f), 0.0f, new int[]{-2801343, -561538, 0}, new float[]{0.0f, 0.4f, 1.0f}, Shader.TileMode.CLAMP);
        if (stub) {
            setState(0);
        }
    }

    public void setPressedState(boolean pressedState) {
        this.pressedState = pressedState;
    }

    public void setPinnedProgress(float pinnedProgress) {
        this.pinnedProgress = pinnedProgress;
    }

    /* loaded from: classes5.dex */
    public static class WeavingState {
        int color1;
        int color2;
        int color3;
        private final int currentState;
        private float duration;
        public Shader shader;
        private float startX;
        private float startY;
        private float time;
        private float targetX = -1.0f;
        private float targetY = -1.0f;
        private final Matrix matrix = new Matrix();

        public WeavingState(int state) {
            this.currentState = state;
        }

        public void update(long dt, float amplitude) {
            float s;
            int i = this.currentState;
            if (i == 0) {
                if (this.color1 != Theme.getColor(Theme.key_voipgroup_overlayGreen1) || this.color2 != Theme.getColor(Theme.key_voipgroup_overlayGreen2)) {
                    int color = Theme.getColor(Theme.key_voipgroup_overlayGreen1);
                    this.color1 = color;
                    int color2 = Theme.getColor(Theme.key_voipgroup_overlayGreen2);
                    this.color2 = color2;
                    this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color, color2}, (float[]) null, Shader.TileMode.CLAMP);
                }
            } else if (i == 1) {
                if (this.color1 != Theme.getColor(Theme.key_voipgroup_overlayBlue1) || this.color2 != Theme.getColor(Theme.key_voipgroup_overlayBlue2)) {
                    int color3 = Theme.getColor(Theme.key_voipgroup_overlayBlue1);
                    this.color1 = color3;
                    int color4 = Theme.getColor(Theme.key_voipgroup_overlayBlue2);
                    this.color2 = color4;
                    this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color3, color4}, (float[]) null, Shader.TileMode.CLAMP);
                }
            } else if (i == 3) {
                if (this.color1 != Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient) || this.color2 != Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient2) || this.color3 != Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient3)) {
                    int color5 = Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient2);
                    this.color2 = color5;
                    int color6 = Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient3);
                    this.color3 = color6;
                    int color7 = Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient);
                    this.color1 = color7;
                    this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color5, color6, color7}, (float[]) null, Shader.TileMode.CLAMP);
                }
            } else {
                return;
            }
            int width = AndroidUtilities.dp(130.0f);
            float f = this.duration;
            if (f == 0.0f || this.time >= f) {
                this.duration = Utilities.random.nextInt(700) + 500;
                this.time = 0.0f;
                if (this.targetX == -1.0f) {
                    updateTargets();
                }
                this.startX = this.targetX;
                this.startY = this.targetY;
                updateTargets();
            }
            float f2 = this.time + (((float) dt) * (BlobDrawable.GRADIENT_SPEED_MIN + 0.5f)) + (((float) dt) * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * amplitude);
            this.time = f2;
            float f3 = this.duration;
            if (f2 > f3) {
                this.time = f3;
            }
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / this.duration);
            float f4 = this.startX;
            float x = (width * (f4 + ((this.targetX - f4) * interpolation))) - 200.0f;
            float f5 = this.startY;
            float y = (width * (f5 + ((this.targetY - f5) * interpolation))) - 200.0f;
            int i2 = this.currentState;
            if (i2 == 3) {
                s = 2.0f;
            } else if (i2 == 0) {
                s = 1.5f;
            } else {
                s = 1.5f;
            }
            float scale = (width / 400.0f) * s;
            this.matrix.reset();
            this.matrix.postTranslate(x, y);
            this.matrix.postScale(scale, scale, x + 200.0f, 200.0f + y);
            this.shader.setLocalMatrix(this.matrix);
        }

        private void updateTargets() {
            int i = this.currentState;
            if (i == 0) {
                this.targetX = ((Utilities.random.nextInt(100) * 0.1f) / 100.0f) + 0.2f;
                this.targetY = ((Utilities.random.nextInt(100) * 0.1f) / 100.0f) + 0.7f;
            } else if (i == 3) {
                this.targetX = ((Utilities.random.nextInt(100) * 0.1f) / 100.0f) + 0.6f;
                this.targetY = (Utilities.random.nextInt(100) * 0.1f) / 100.0f;
            } else {
                this.targetX = ((Utilities.random.nextInt(100) / 100.0f) * 0.2f) + 0.8f;
                this.targetY = Utilities.random.nextInt(100) / 100.0f;
            }
        }

        public void setToPaint(Paint paint) {
            if (this.currentState == 2) {
                paint.setShader(null);
                paint.setColor(Theme.getColor(Theme.key_voipgroup_topPanelGray));
                return;
            }
            paint.setShader(this.shader);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:109:0x023f  */
    /* JADX WARN: Removed duplicated region for block: B:110:0x024e  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x0260  */
    /* JADX WARN: Removed duplicated region for block: B:116:0x02bd  */
    /* JADX WARN: Removed duplicated region for block: B:117:0x02c8  */
    /* JADX WARN: Removed duplicated region for block: B:124:0x0304  */
    /* JADX WARN: Removed duplicated region for block: B:135:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0067  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x00a4  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00c2  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00da  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00f3  */
    /* JADX WARN: Removed duplicated region for block: B:56:0x010d  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0118  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x012a  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x0155  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x0159  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x0167  */
    /* JADX WARN: Removed duplicated region for block: B:78:0x016b  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x0174  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onDraw(android.graphics.Canvas r19) {
        /*
            Method dump skipped, instructions count: 783
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallPipButton.onDraw(android.graphics.Canvas):void");
    }

    private void setAmplitude(double value) {
        float min = (float) (Math.min(8500.0d, value) / 8500.0d);
        this.animateToAmplitude = min;
        this.animateAmplitudeDiff = (min - this.amplitude) / ((BlobDrawable.AMPLITUDE_SPEED * 500.0f) + 100.0f);
    }

    public void setState(int state) {
        String contentDescription;
        WeavingState weavingState = this.currentState;
        if (weavingState != null && weavingState.currentState == state) {
            return;
        }
        WeavingState weavingState2 = this.currentState;
        this.previousState = weavingState2;
        WeavingState weavingState3 = this.states[state];
        this.currentState = weavingState3;
        float f = 0.0f;
        if (weavingState2 != null) {
            this.progressToState = 0.0f;
        } else {
            this.progressToState = 1.0f;
            boolean showWaves = true;
            if (weavingState3.currentState == 3 || this.currentState.currentState == 2) {
                showWaves = false;
            }
            if (showWaves) {
                f = 1.0f;
            }
            this.wavesEnter = f;
        }
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService != null && ChatObject.isChannelOrGiga(voIPService.getChat())) {
            contentDescription = LocaleController.getString("VoipChannelVoiceChat", R.string.VoipChannelVoiceChat);
        } else {
            contentDescription = LocaleController.getString("VoipGroupVoiceChat", R.string.VoipGroupVoiceChat);
        }
        if (state == 0) {
            contentDescription = contentDescription + ", " + LocaleController.getString("VoipTapToMute", R.string.VoipTapToMute);
        } else if (state == 2) {
            contentDescription = contentDescription + ", " + LocaleController.getString("Connecting", R.string.Connecting);
        } else if (state == 3) {
            contentDescription = contentDescription + ", " + LocaleController.getString("VoipMutedByAdmin", R.string.VoipMutedByAdmin);
        }
        setContentDescription(contentDescription);
        invalidate();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        String str;
        int i;
        super.onInitializeAccessibilityNodeInfo(info);
        if (Build.VERSION.SDK_INT >= 21 && GroupCallPip.getInstance() != null) {
            if (GroupCallPip.getInstance().showAlert) {
                i = R.string.AccDescrCloseMenu;
                str = "AccDescrCloseMenu";
            } else {
                i = R.string.AccDescrOpenMenu2;
                str = "AccDescrOpenMenu2";
            }
            String label = LocaleController.getString(str, i);
            info.addAction(new AccessibilityNodeInfo.AccessibilityAction(16, label));
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.stub) {
            setAmplitude(FirebaseRemoteConfig.DEFAULT_VALUE_FOR_DOUBLE);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupCallUpdated);
            boolean isMuted = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().registerStateListener(this);
            }
            this.bigMicDrawable.setCustomEndFrame(isMuted ? 13 : 24);
            RLottieDrawable rLottieDrawable = this.bigMicDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
            updateButtonState();
        }
    }

    private void updateButtonState() {
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService != null && voIPService.groupCall != null) {
            int currentCallState = voIPService.getCallState();
            if (currentCallState == 1 || currentCallState == 2 || currentCallState == 6 || currentCallState == 5) {
                setState(2);
                return;
            }
            TLRPC.TL_groupCallParticipant participant = voIPService.groupCall.participants.get(voIPService.getSelfId());
            if (participant != null && !participant.can_self_unmute && participant.muted && !ChatObject.canManageCalls(voIPService.getChat())) {
                if (!voIPService.isMicMute()) {
                    voIPService.setMicMute(true, false, false);
                }
                setState(3);
                long now = SystemClock.uptimeMillis();
                MotionEvent e = MotionEvent.obtain(now, now, 3, 0.0f, 0.0f, 0);
                if (getParent() != null) {
                    View parentView = (View) getParent();
                    parentView.dispatchTouchEvent(e);
                    return;
                }
                return;
            }
            setState(voIPService.isMicMute() ? 1 : 0);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (!this.stub) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().unregisterStateListener(this);
            }
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.webRtcMicAmplitudeEvent) {
            float amplitude = ((Float) args[0]).floatValue();
            setAmplitude(4000.0f * amplitude);
        } else if (id == NotificationCenter.groupCallUpdated) {
            updateButtonState();
        }
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onAudioSettingsChanged() {
        boolean isMuted = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
        boolean changed = this.bigMicDrawable.setCustomEndFrame(isMuted ? 13 : 24);
        if (changed) {
            if (isMuted) {
                this.bigMicDrawable.setCurrentFrame(0);
            } else {
                this.bigMicDrawable.setCurrentFrame(12);
            }
        }
        this.muteButton.playAnimation();
        updateButtonState();
    }

    @Override // org.telegram.messenger.voip.VoIPService.StateListener
    public void onStateChanged(int state) {
        updateButtonState();
    }

    public void setRemoveAngle(double angle) {
        this.removeAngle = (float) angle;
    }

    public void prepareToRemove(boolean prepare) {
        if (this.prepareToRemove != prepare) {
            invalidate();
        }
        this.prepareToRemove = prepare;
    }
}
