package org.telegram.ui.Components;

import android.content.Context;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
/* loaded from: classes3.dex */
public class ChatActivityEnterViewAnimatedIconView extends RLottieImageView {
    private State currentState;
    private Runnable lastCallback;

    /* loaded from: classes3.dex */
    public enum State {
        VOICE,
        VIDEO,
        STICKER,
        KEYBOARD,
        SMILE,
        GIF
    }

    public ChatActivityEnterViewAnimatedIconView(Context context) {
        super(context);
    }

    public void setState(State state, boolean z) {
        setState(state, z, false);
    }

    private void setState(final State state, final boolean z, boolean z2) {
        if (!z || state != this.currentState) {
            if (getAnimatedDrawable() != null && getAnimatedDrawable().isRunning() && !z2) {
                this.lastCallback = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        ChatActivityEnterViewAnimatedIconView.this.lambda$setState$0(state, z);
                    }
                };
                return;
            }
            State state2 = this.currentState;
            this.currentState = state;
            if (!z || state2 == null || getState(state2, state) == null) {
                int i = getAnyState(this.currentState).resource;
                RLottieDrawable rLottieDrawable = new RLottieDrawable(i, String.valueOf(i), AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
                rLottieDrawable.setProgress(0.0f, false);
                rLottieDrawable.stop();
                setAnimation(rLottieDrawable);
                return;
            }
            int i2 = getState(state2, this.currentState).resource;
            RLottieDrawable rLottieDrawable2 = new RLottieDrawable(i2, String.valueOf(i2), AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f));
            rLottieDrawable2.setProgress(0.0f, false);
            rLottieDrawable2.setAutoRepeat(0);
            rLottieDrawable2.setOnAnimationEndListener(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivityEnterViewAnimatedIconView.this.lambda$setState$1();
                }
            });
            rLottieDrawable2.start();
            setAnimation(rLottieDrawable2);
        }
    }

    public /* synthetic */ void lambda$setState$0(State state, boolean z) {
        setState(state, z, true);
    }

    public /* synthetic */ void lambda$setState$1() {
        Runnable runnable = this.lastCallback;
        if (runnable != null) {
            runnable.run();
            this.lastCallback = null;
        }
    }

    private TransitState getAnyState(State state) {
        TransitState[] values;
        for (TransitState transitState : TransitState.values()) {
            if (transitState.firstState == state) {
                return transitState;
            }
        }
        return null;
    }

    private TransitState getState(State state, State state2) {
        TransitState[] values;
        for (TransitState transitState : TransitState.values()) {
            if (transitState.firstState == state && transitState.secondState == state2) {
                return transitState;
            }
        }
        return null;
    }

    /* JADX WARN: Init of enum GIF_TO_KEYBOARD can be incorrect */
    /* JADX WARN: Init of enum KEYBOARD_TO_GIF can be incorrect */
    /* JADX WARN: Init of enum KEYBOARD_TO_SMILE can be incorrect */
    /* JADX WARN: Init of enum KEYBOARD_TO_STICKER can be incorrect */
    /* JADX WARN: Init of enum SMILE_TO_KEYBOARD can be incorrect */
    /* JADX WARN: Init of enum STICKER_TO_KEYBOARD can be incorrect */
    /* JADX WARN: Init of enum VIDEO_TO_VOICE can be incorrect */
    /* JADX WARN: Init of enum VOICE_TO_VIDEO can be incorrect */
    /* loaded from: classes3.dex */
    public enum TransitState {
        VOICE_TO_VIDEO(r7, r8, R.raw.voice_to_video),
        STICKER_TO_KEYBOARD(r16, r17, R.raw.sticker_to_keyboard),
        SMILE_TO_KEYBOARD(r10, r17, R.raw.smile_to_keyboard),
        VIDEO_TO_VOICE(r8, r7, R.raw.video_to_voice),
        KEYBOARD_TO_STICKER(r17, r16, R.raw.keyboard_to_sticker),
        KEYBOARD_TO_GIF(r17, r12, R.raw.keyboard_to_gif),
        KEYBOARD_TO_SMILE(r17, r10, R.raw.keyboard_to_smile),
        GIF_TO_KEYBOARD(r12, r17, R.raw.gif_to_keyboard);
        
        final State firstState;
        final int resource;
        final State secondState;

        static {
            State state = State.VOICE;
            State state2 = State.VIDEO;
            State state3 = State.STICKER;
            State state4 = State.KEYBOARD;
            State state5 = State.SMILE;
            State state6 = State.GIF;
        }

        TransitState(State state, State state2, int i) {
            this.firstState = state;
            this.secondState = state2;
            this.resource = i;
        }
    }
}
