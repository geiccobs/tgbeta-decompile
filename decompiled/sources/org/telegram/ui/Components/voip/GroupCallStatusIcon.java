package org.telegram.ui.Components.voip;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.SystemClock;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
/* loaded from: classes5.dex */
public class GroupCallStatusIcon {
    Callback callback;
    RLottieImageView iconView;
    boolean isSpeaking;
    boolean lastMuted;
    boolean lastRaisedHand;
    private boolean mutedByMe;
    TLRPC.TL_groupCallParticipant participant;
    boolean updateRunnableScheduled;
    private Runnable shakeHandCallback = new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallStatusIcon$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            GroupCallStatusIcon.this.m3247lambda$new$0$orgtelegramuiComponentsvoipGroupCallStatusIcon();
        }
    };
    private Runnable raiseHandCallback = new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallStatusIcon$$ExternalSyntheticLambda1
        @Override // java.lang.Runnable
        public final void run() {
            GroupCallStatusIcon.this.m3248lambda$new$1$orgtelegramuiComponentsvoipGroupCallStatusIcon();
        }
    };
    private Runnable updateRunnable = new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallStatusIcon$$ExternalSyntheticLambda2
        @Override // java.lang.Runnable
        public final void run() {
            GroupCallStatusIcon.this.m3249lambda$new$2$orgtelegramuiComponentsvoipGroupCallStatusIcon();
        }
    };
    private Runnable checkRaiseRunnable = new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallStatusIcon$$ExternalSyntheticLambda3
        @Override // java.lang.Runnable
        public final void run() {
            GroupCallStatusIcon.this.m3250lambda$new$3$orgtelegramuiComponentsvoipGroupCallStatusIcon();
        }
    };
    RLottieDrawable micDrawable = new RLottieDrawable(R.raw.voice_mini, "2131558576", AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, null);
    RLottieDrawable shakeHandDrawable = new RLottieDrawable(R.raw.hand_2, "2131558459", AndroidUtilities.dp(15.0f), AndroidUtilities.dp(15.0f), true, null);

    /* loaded from: classes5.dex */
    public interface Callback {
        void onStatusChanged();
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-GroupCallStatusIcon */
    public /* synthetic */ void m3247lambda$new$0$orgtelegramuiComponentsvoipGroupCallStatusIcon() {
        this.shakeHandDrawable.setOnFinishCallback(null, 0);
        this.micDrawable.setOnFinishCallback(null, 0);
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setAnimation(this.micDrawable);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-voip-GroupCallStatusIcon */
    public /* synthetic */ void m3248lambda$new$1$orgtelegramuiComponentsvoipGroupCallStatusIcon() {
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
        this.shakeHandDrawable.setCurrentFrame(startFrame);
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setAnimation(this.shakeHandDrawable);
            this.iconView.playAnimation();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-voip-GroupCallStatusIcon */
    public /* synthetic */ void m3249lambda$new$2$orgtelegramuiComponentsvoipGroupCallStatusIcon() {
        this.isSpeaking = false;
        Callback callback = this.callback;
        if (callback != null) {
            callback.onStatusChanged();
        }
        this.updateRunnableScheduled = false;
    }

    public void setAmplitude(double value) {
        if (value > 1.5d) {
            if (this.updateRunnableScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            }
            if (!this.isSpeaking) {
                this.isSpeaking = true;
                Callback callback = this.callback;
                if (callback != null) {
                    callback.onStatusChanged();
                }
            }
            AndroidUtilities.runOnUIThread(this.updateRunnable, 500L);
            this.updateRunnableScheduled = true;
        }
    }

    /* renamed from: lambda$new$3$org-telegram-ui-Components-voip-GroupCallStatusIcon */
    public /* synthetic */ void m3250lambda$new$3$orgtelegramuiComponentsvoipGroupCallStatusIcon() {
        updateIcon(true);
    }

    public void setImageView(RLottieImageView iconView) {
        this.iconView = iconView;
        updateIcon(false);
    }

    public void setParticipant(TLRPC.TL_groupCallParticipant participant, boolean animated) {
        this.participant = participant;
        updateIcon(animated);
    }

    public void updateIcon(boolean animated) {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant;
        boolean hasVoice;
        boolean newMuted;
        boolean changed;
        if (this.iconView == null || (tL_groupCallParticipant = this.participant) == null || this.micDrawable == null) {
            return;
        }
        boolean newMutedByMe = tL_groupCallParticipant.muted_by_you && !this.participant.self;
        if (SystemClock.elapsedRealtime() - this.participant.lastVoiceUpdateTime < 500) {
            hasVoice = this.participant.hasVoiceDelayed;
        } else {
            hasVoice = this.participant.hasVoice;
        }
        if (this.participant.self) {
            newMuted = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute() && (!this.isSpeaking || !hasVoice);
        } else {
            newMuted = (this.participant.muted && (!this.isSpeaking || !hasVoice)) || newMutedByMe;
        }
        boolean newRaisedHand = ((this.participant.muted && !this.isSpeaking) || newMutedByMe) && (!this.participant.can_self_unmute || newMutedByMe) && !this.participant.can_self_unmute && this.participant.raise_hand_rating != 0;
        if (newRaisedHand) {
            long time = SystemClock.elapsedRealtime() - this.participant.lastRaiseHandDate;
            if (this.participant.lastRaiseHandDate == 0 || time > DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
                char c = newMutedByMe ? (char) 2 : (char) 0;
            } else {
                AndroidUtilities.runOnUIThread(this.checkRaiseRunnable, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS - time);
            }
            changed = this.micDrawable.setCustomEndFrame(136);
        } else {
            this.iconView.setAnimation(this.micDrawable);
            this.micDrawable.setOnFinishCallback(null, 0);
            if (newMuted && this.lastRaisedHand) {
                changed = this.micDrawable.setCustomEndFrame(36);
            } else {
                changed = this.micDrawable.setCustomEndFrame(newMuted ? 99 : 69);
            }
        }
        if (animated) {
            if (changed) {
                if (newRaisedHand) {
                    this.micDrawable.setCurrentFrame(99);
                    this.micDrawable.setCustomEndFrame(136);
                } else if (newMuted && this.lastRaisedHand && !newRaisedHand) {
                    this.micDrawable.setCurrentFrame(0);
                    this.micDrawable.setCustomEndFrame(36);
                } else if (!newMuted) {
                    this.micDrawable.setCurrentFrame(36);
                    this.micDrawable.setCustomEndFrame(69);
                } else {
                    this.micDrawable.setCurrentFrame(69);
                    this.micDrawable.setCustomEndFrame(99);
                }
                this.iconView.playAnimation();
                this.iconView.invalidate();
            }
        } else {
            RLottieDrawable rLottieDrawable = this.micDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
            this.iconView.invalidate();
        }
        this.iconView.setAnimation(this.micDrawable);
        this.lastMuted = newMuted;
        this.lastRaisedHand = newRaisedHand;
        if (this.mutedByMe != newMutedByMe) {
            this.mutedByMe = newMutedByMe;
            Callback callback = this.callback;
            if (callback != null) {
                callback.onStatusChanged();
            }
        }
    }

    public boolean isSpeaking() {
        return this.isSpeaking;
    }

    public boolean isMutedByMe() {
        return this.mutedByMe;
    }

    public boolean isMutedByAdmin() {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = this.participant;
        return tL_groupCallParticipant != null && tL_groupCallParticipant.muted && !this.participant.can_self_unmute;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
        if (callback == null) {
            this.isSpeaking = false;
            AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
            AndroidUtilities.cancelRunOnUIThread(this.raiseHandCallback);
            AndroidUtilities.cancelRunOnUIThread(this.checkRaiseRunnable);
            this.micDrawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        }
    }
}
