package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.StateSet;
import androidx.core.graphics.ColorUtils;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import java.util.Arrays;
import java.util.HashMap;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.PremiumPreviewFragment;
/* loaded from: classes5.dex */
public class TranscribeButton {
    private static final int[] pressedState = {16842910, 16842919};
    private static HashMap<Integer, MessageObject> transcribeOperationsByDialogPosition;
    private static HashMap<Long, MessageObject> transcribeOperationsById;
    private int backgroundColor;
    private Paint backgroundPaint;
    private Path boundsPath;
    private int color;
    private int iconColor;
    private RLottieDrawable inIconDrawable;
    private boolean loading;
    private AnimatedFloat loadingFloat;
    private RLottieDrawable outIconDrawable;
    private ChatMessageCell parent;
    private boolean premium;
    private android.graphics.Rect pressBounds;
    private Path progressClipPath;
    private int rippleColor;
    private SeekBarWaveform seekBar;
    private float[] segments;
    private Drawable selectorDrawable;
    private Paint strokePaint;
    private boolean pressed = false;
    private long pressId = 0;
    private final FastOutSlowInInterpolator interpolator = new FastOutSlowInInterpolator();
    private long start = SystemClock.elapsedRealtime();
    private android.graphics.Rect bounds = new android.graphics.Rect(0, 0, AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
    private boolean isOpen = false;
    private boolean shouldBeOpen = false;

    public TranscribeButton(ChatMessageCell parent, SeekBarWaveform seekBar) {
        this.parent = parent;
        this.seekBar = seekBar;
        android.graphics.Rect rect = new android.graphics.Rect(this.bounds);
        this.pressBounds = rect;
        rect.inset(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.transcribe_out, "transcribe_out", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
        this.outIconDrawable = rLottieDrawable;
        rLottieDrawable.setCurrentFrame(0);
        this.outIconDrawable.setCallback(parent);
        this.outIconDrawable.addParentView(parent);
        this.outIconDrawable.setOnFinishCallback(new Runnable() { // from class: org.telegram.ui.Components.TranscribeButton$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                TranscribeButton.this.m3151lambda$new$0$orgtelegramuiComponentsTranscribeButton();
            }
        }, 19);
        this.outIconDrawable.setAllowDecodeSingleFrame(true);
        RLottieDrawable rLottieDrawable2 = new RLottieDrawable(R.raw.transcribe_in, "transcribe_in", AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f));
        this.inIconDrawable = rLottieDrawable2;
        rLottieDrawable2.setCurrentFrame(0);
        this.inIconDrawable.setCallback(parent);
        this.inIconDrawable.addParentView(parent);
        this.inIconDrawable.setOnFinishCallback(new Runnable() { // from class: org.telegram.ui.Components.TranscribeButton$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                TranscribeButton.this.m3152lambda$new$1$orgtelegramuiComponentsTranscribeButton();
            }
        }, 19);
        this.inIconDrawable.setAllowDecodeSingleFrame(true);
        this.premium = AccountInstance.getInstance(parent.getMessageObject().currentAccount).getUserConfig().isPremium();
        this.loadingFloat = new AnimatedFloat(parent, 250L, CubicBezierInterpolator.EASE_OUT_QUINT);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-TranscribeButton */
    public /* synthetic */ void m3151lambda$new$0$orgtelegramuiComponentsTranscribeButton() {
        this.outIconDrawable.stop();
        this.inIconDrawable.stop();
        this.shouldBeOpen = true;
        this.isOpen = true;
        this.inIconDrawable.setCurrentFrame(0);
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-TranscribeButton */
    public /* synthetic */ void m3152lambda$new$1$orgtelegramuiComponentsTranscribeButton() {
        this.inIconDrawable.stop();
        this.outIconDrawable.stop();
        this.shouldBeOpen = false;
        this.isOpen = false;
        this.outIconDrawable.setCurrentFrame(0);
    }

    public void setLoading(boolean loading, boolean animated) {
        this.loading = loading;
        this.seekBar.setLoading(loading);
        float f = 0.0f;
        if (animated) {
            if (this.loadingFloat.get() <= 0.0f) {
                this.start = SystemClock.elapsedRealtime();
            }
        } else {
            AnimatedFloat animatedFloat = this.loadingFloat;
            if (this.loading) {
                f = 1.0f;
            }
            animatedFloat.set(f, true);
        }
        ChatMessageCell chatMessageCell = this.parent;
        if (chatMessageCell != null) {
            chatMessageCell.invalidate();
        }
    }

    public void setOpen(boolean open, boolean animated) {
        boolean wasShouldBeOpen = this.shouldBeOpen;
        this.shouldBeOpen = open;
        if (animated) {
            if (open && !wasShouldBeOpen) {
                this.isOpen = false;
                this.inIconDrawable.setCurrentFrame(0);
                this.outIconDrawable.setCurrentFrame(0);
                this.outIconDrawable.start();
            } else if (!open && wasShouldBeOpen) {
                this.isOpen = true;
                this.outIconDrawable.setCurrentFrame(0);
                this.inIconDrawable.setCurrentFrame(0);
                this.inIconDrawable.start();
            }
        } else {
            this.isOpen = open;
            this.inIconDrawable.stop();
            this.outIconDrawable.stop();
            this.inIconDrawable.setCurrentFrame(0);
            this.outIconDrawable.setCurrentFrame(0);
        }
        ChatMessageCell chatMessageCell = this.parent;
        if (chatMessageCell != null) {
            chatMessageCell.invalidate();
        }
    }

    public boolean onTouch(int action, float x, float y) {
        if (action == 1 || action == 3) {
            if (this.pressed && action == 1) {
                onTap();
                return true;
            }
            this.pressed = false;
            return false;
        } else if (!this.pressBounds.contains((int) x, (int) y)) {
            return false;
        } else {
            if (action == 0) {
                this.pressed = true;
            }
            if (this.pressed && Build.VERSION.SDK_INT >= 21) {
                Drawable drawable = this.selectorDrawable;
                if (drawable instanceof RippleDrawable) {
                    drawable.setHotspot(x, y);
                    this.selectorDrawable.setState(pressedState);
                    this.parent.invalidate();
                }
            }
            return true;
        }
    }

    public void onTap() {
        boolean processClick;
        boolean z = this.shouldBeOpen;
        boolean toOpen = !z;
        if (!z) {
            processClick = !this.loading;
            if (this.premium && this.parent.getMessageObject().isSent()) {
                setLoading(true, true);
            }
        } else {
            processClick = true;
            setOpen(false, true);
            setLoading(false, true);
        }
        if (Build.VERSION.SDK_INT >= 21) {
            Drawable drawable = this.selectorDrawable;
            if (drawable instanceof RippleDrawable) {
                drawable.setState(StateSet.NOTHING);
                this.parent.invalidate();
            }
        }
        this.pressed = false;
        if (processClick) {
            if (!this.premium && toOpen) {
                if (this.parent.getDelegate() != null) {
                    this.parent.getDelegate().needShowPremiumFeatures(PremiumPreviewFragment.featureTypeToServerString(8));
                    return;
                }
                return;
            }
            transcribePressed(this.parent.getMessageObject(), toOpen);
        }
    }

    public void setColor(boolean isOut, int color, int grayColor) {
        boolean z = !this.premium;
        boolean newColor = this.color != color;
        this.color = color;
        this.iconColor = color;
        int alphaComponent = ColorUtils.setAlphaComponent(color, (int) (Color.alpha(color) * 0.156f));
        this.backgroundColor = alphaComponent;
        this.rippleColor = Theme.blendOver(alphaComponent, ColorUtils.setAlphaComponent(color, (int) (Color.alpha(color) * (Theme.isCurrentThemeDark() ? 0.3f : 0.2f))));
        if (this.backgroundPaint == null) {
            this.backgroundPaint = new Paint();
        }
        this.backgroundPaint.setColor(this.backgroundColor);
        if (newColor || this.selectorDrawable == null) {
            Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(8.0f), 0, this.rippleColor);
            this.selectorDrawable = createSimpleSelectorRoundRectDrawable;
            createSimpleSelectorRoundRectDrawable.setCallback(this.parent);
        }
        if (newColor) {
            this.inIconDrawable.beginApplyLayerColors();
            this.inIconDrawable.setLayerColor("Artboard Outlines.**", this.iconColor);
            this.inIconDrawable.commitApplyLayerColors();
            this.inIconDrawable.setAllowDecodeSingleFrame(true);
            this.inIconDrawable.updateCurrentFrame();
            this.outIconDrawable.beginApplyLayerColors();
            this.outIconDrawable.setLayerColor("Artboard Outlines.**", this.iconColor);
            this.outIconDrawable.commitApplyLayerColors();
            this.outIconDrawable.setAllowDecodeSingleFrame(true);
            this.outIconDrawable.updateCurrentFrame();
        }
        if (this.strokePaint == null) {
            Paint paint = new Paint(1);
            this.strokePaint = paint;
            paint.setStyle(Paint.Style.STROKE);
        }
        this.strokePaint.setColor(color);
    }

    public void draw(Canvas canvas) {
        this.bounds.set(0, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(30.0f), AndroidUtilities.dp(27.0f));
        this.pressBounds.set(this.bounds.left - AndroidUtilities.dp(8.0f), this.bounds.top - AndroidUtilities.dp(8.0f), this.bounds.right + AndroidUtilities.dp(8.0f), this.bounds.bottom + AndroidUtilities.dp(8.0f));
        if (this.boundsPath == null) {
            this.boundsPath = new Path();
            AndroidUtilities.rectTmp.set(this.bounds);
            this.boundsPath.addRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), Path.Direction.CW);
        }
        canvas.save();
        canvas.clipPath(this.boundsPath);
        Paint paint = this.backgroundPaint;
        if (paint != null) {
            canvas.drawRect(this.bounds, paint);
        }
        Drawable drawable = this.selectorDrawable;
        if (drawable != null && this.premium) {
            drawable.setBounds(this.bounds);
            this.selectorDrawable.draw(canvas);
        }
        canvas.restore();
        float f = 1.0f;
        float loadingT = this.loadingFloat.set(this.loading ? 1.0f : 0.0f);
        if (loadingT > 0.0f) {
            float[] segments = getSegments(((float) (SystemClock.elapsedRealtime() - this.start)) * 0.75f);
            canvas.save();
            if (this.progressClipPath == null) {
                this.progressClipPath = new Path();
            }
            this.progressClipPath.reset();
            AndroidUtilities.rectTmp.set(this.pressBounds);
            float segmentLength = Math.max(40.0f * loadingT, segments[1] - segments[0]);
            Path path = this.progressClipPath;
            RectF rectF = AndroidUtilities.rectTmp;
            float f2 = segments[0];
            float f3 = (1.0f - loadingT) * segmentLength;
            if (this.loading) {
                f = 0.0f;
            }
            path.addArc(rectF, f2 + (f3 * f), segmentLength * loadingT);
            this.progressClipPath.lineTo(AndroidUtilities.rectTmp.centerX(), AndroidUtilities.rectTmp.centerY());
            this.progressClipPath.close();
            canvas.clipPath(this.progressClipPath);
            AndroidUtilities.rectTmp.set(this.bounds);
            this.strokePaint.setStrokeWidth(AndroidUtilities.dp(1.5f));
            canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), this.strokePaint);
            canvas.restore();
            this.parent.invalidate();
        }
        canvas.save();
        canvas.translate(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
        if (this.isOpen) {
            this.inIconDrawable.draw(canvas);
        } else {
            this.outIconDrawable.draw(canvas);
        }
        canvas.restore();
    }

    private float[] getSegments(long d) {
        if (this.segments == null) {
            this.segments = new float[2];
        }
        long t = d % 5400;
        float[] fArr = this.segments;
        fArr[0] = (((float) (t * 1520)) / 5400.0f) - 20.0f;
        fArr[1] = ((float) (1520 * t)) / 5400.0f;
        for (int i = 0; i < 4; i++) {
            float fraction = ((float) (t - (i * 1350))) / 667.0f;
            float[] fArr2 = this.segments;
            fArr2[1] = fArr2[1] + (this.interpolator.getInterpolation(fraction) * 250.0f);
            float fraction2 = ((float) (t - ((i * 1350) + 667))) / 667.0f;
            float[] fArr3 = this.segments;
            fArr3[0] = fArr3[0] + (this.interpolator.getInterpolation(fraction2) * 250.0f);
        }
        return this.segments;
    }

    /* loaded from: classes5.dex */
    public static class LoadingPointsSpan extends ImageSpan {
        private static LoadingPointsDrawable drawable;

        /* JADX WARN: Illegal instructions before constructor call */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public LoadingPointsSpan() {
            /*
                r6 = this;
                org.telegram.ui.Components.TranscribeButton$LoadingPointsDrawable r0 = org.telegram.ui.Components.TranscribeButton.LoadingPointsSpan.drawable
                if (r0 != 0) goto Ld
                org.telegram.ui.Components.TranscribeButton$LoadingPointsDrawable r0 = new org.telegram.ui.Components.TranscribeButton$LoadingPointsDrawable
                android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
                r0.<init>(r1)
                org.telegram.ui.Components.TranscribeButton.LoadingPointsSpan.drawable = r0
            Ld:
                r1 = 0
                r6.<init>(r0, r1)
                android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
                float r0 = r0.getTextSize()
                r2 = 1063507722(0x3f63d70a, float:0.89)
                float r0 = r0 * r2
                r2 = 1017370378(0x3ca3d70a, float:0.02)
                float r2 = r2 * r0
                int r2 = (int) r2
                android.graphics.drawable.Drawable r3 = r6.getDrawable()
                int r4 = (int) r0
                r5 = 1067450368(0x3fa00000, float:1.25)
                float r5 = r5 * r0
                int r5 = (int) r5
                int r5 = r5 + r2
                r3.setBounds(r1, r2, r4, r5)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.TranscribeButton.LoadingPointsSpan.<init>():void");
        }

        @Override // android.text.style.ReplacementSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint textPaint) {
            float fontSize = textPaint.getTextSize() * 0.89f;
            int yoff = (int) (0.02f * fontSize);
            getDrawable().setBounds(0, yoff, (int) fontSize, ((int) (1.25f * fontSize)) + yoff);
            super.updateDrawState(textPaint);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class LoadingPointsDrawable extends Drawable {
        private int lastColor;
        private RLottieDrawable lottie;
        private Paint paint;

        public LoadingPointsDrawable(TextPaint textPaint) {
            this.paint = textPaint;
            float fontSize = textPaint.getTextSize() * 0.89f;
            RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.dots_loading, "dots_loading", (int) fontSize, (int) (1.25f * fontSize)) { // from class: org.telegram.ui.Components.TranscribeButton.LoadingPointsDrawable.1
                @Override // org.telegram.ui.Components.RLottieDrawable
                public boolean hasParentView() {
                    return true;
                }
            };
            this.lottie = rLottieDrawable;
            rLottieDrawable.setAutoRepeat(1);
            this.lottie.setCurrentFrame((int) ((((float) SystemClock.elapsedRealtime()) / 16.0f) % 60.0f));
            this.lottie.setAllowDecodeSingleFrame(true);
            this.lottie.start();
        }

        public void setColor(int color) {
            this.lottie.beginApplyLayerColors();
            this.lottie.setLayerColor("Comp 1.**", color);
            this.lottie.commitApplyLayerColors();
            this.lottie.setAllowDecodeSingleFrame(true);
            this.lottie.updateCurrentFrame();
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            int color = this.paint.getColor();
            if (color != this.lastColor) {
                setColor(color);
                this.lastColor = color;
            }
            this.lottie.draw(canvas);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }
    }

    private static int reqInfoHash(MessageObject messageObject) {
        if (messageObject == null) {
            return 0;
        }
        return Arrays.hashCode(new Object[]{Integer.valueOf(messageObject.currentAccount), Long.valueOf(messageObject.getDialogId()), Integer.valueOf(messageObject.getId())});
    }

    public static boolean isTranscribing(MessageObject messageObject) {
        HashMap<Integer, MessageObject> hashMap = transcribeOperationsByDialogPosition;
        return (hashMap != null && (hashMap.containsValue(messageObject) || transcribeOperationsByDialogPosition.containsKey(Integer.valueOf(reqInfoHash(messageObject))))) || !(transcribeOperationsById == null || messageObject == null || messageObject.messageOwner == null || !transcribeOperationsById.containsKey(Long.valueOf(messageObject.messageOwner.voiceTranscriptionId)));
    }

    private static void transcribePressed(final MessageObject messageObject, boolean open) {
        if (messageObject == null || messageObject.messageOwner == null || !messageObject.isSent()) {
            return;
        }
        final int account = messageObject.currentAccount;
        final long start = SystemClock.elapsedRealtime();
        TLRPC.InputPeer peer = MessagesController.getInstance(account).getInputPeer(messageObject.messageOwner.peer_id);
        final long dialogId = DialogObject.getPeerDialogId(peer);
        final int messageId = messageObject.messageOwner.id;
        if (open) {
            if (messageObject.messageOwner.voiceTranscription != null && messageObject.messageOwner.voiceTranscriptionFinal) {
                messageObject.messageOwner.voiceTranscriptionOpen = true;
                MessagesStorage.getInstance(account).updateMessageVoiceTranscriptionOpen(dialogId, messageId, messageObject.messageOwner);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranscribeButton$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.voiceTranscriptionUpdate, messageObject, null, null, true, true);
                    }
                });
                return;
            }
            TLRPC.TL_messages_transcribeAudio req = new TLRPC.TL_messages_transcribeAudio();
            req.peer = peer;
            req.msg_id = messageId;
            if (transcribeOperationsByDialogPosition == null) {
                transcribeOperationsByDialogPosition = new HashMap<>();
            }
            transcribeOperationsByDialogPosition.put(Integer.valueOf(reqInfoHash(messageObject)), messageObject);
            ConnectionsManager.getInstance(account).sendRequest(req, new RequestDelegate() { // from class: org.telegram.ui.Components.TranscribeButton$$ExternalSyntheticLambda6
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    TranscribeButton.lambda$transcribePressed$4(MessageObject.this, start, account, dialogId, messageId, tLObject, tL_error);
                }
            });
            return;
        }
        HashMap<Integer, MessageObject> hashMap = transcribeOperationsByDialogPosition;
        if (hashMap != null) {
            hashMap.remove(Integer.valueOf(reqInfoHash(messageObject)));
        }
        messageObject.messageOwner.voiceTranscriptionOpen = false;
        MessagesStorage.getInstance(account).updateMessageVoiceTranscriptionOpen(dialogId, messageId, messageObject.messageOwner);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranscribeButton$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                NotificationCenter.getInstance(account).postNotificationName(NotificationCenter.voiceTranscriptionUpdate, messageObject, null, null, false, null);
            }
        });
    }

    public static /* synthetic */ void lambda$transcribePressed$4(final MessageObject messageObject, long start, int account, long dialogId, int messageId, TLObject res, TLRPC.TL_error err) {
        String text;
        boolean isFinal;
        long id = 0;
        if (res instanceof TLRPC.TL_messages_transcribedAudio) {
            TLRPC.TL_messages_transcribedAudio r = (TLRPC.TL_messages_transcribedAudio) res;
            text = r.text;
            id = r.transcription_id;
            isFinal = !r.pending;
            if (TextUtils.isEmpty(text)) {
                text = !isFinal ? null : "";
            }
            if (transcribeOperationsById == null) {
                transcribeOperationsById = new HashMap<>();
            }
            transcribeOperationsById.put(Long.valueOf(id), messageObject);
            messageObject.messageOwner.voiceTranscriptionId = id;
        } else {
            text = "";
            isFinal = true;
        }
        final String finalText = text;
        final long finalId = id;
        long duration = SystemClock.elapsedRealtime() - start;
        messageObject.messageOwner.voiceTranscriptionOpen = true;
        messageObject.messageOwner.voiceTranscriptionFinal = isFinal;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("Transcription request sent, received final=" + isFinal + " id=" + finalId + " text=" + finalText);
        }
        MessagesStorage.getInstance(account).updateMessageVoiceTranscription(dialogId, messageId, finalText, messageObject.messageOwner);
        if (isFinal) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranscribeButton$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    TranscribeButton.finishTranscription(MessageObject.this, finalId, finalText);
                }
            }, Math.max(0L, 350 - duration));
        }
    }

    public static boolean finishTranscription(MessageObject messageObject, final long transcription_id, final String text) {
        MessageObject messageObjectByTranscriptionId = null;
        try {
            HashMap<Long, MessageObject> hashMap = transcribeOperationsById;
            if (hashMap != null && hashMap.containsKey(Long.valueOf(transcription_id))) {
                messageObjectByTranscriptionId = transcribeOperationsById.remove(Long.valueOf(transcription_id));
            }
            if (messageObject == null) {
                messageObject = messageObjectByTranscriptionId;
            }
            if (messageObject != null && messageObject.messageOwner != null) {
                final MessageObject finalMessageObject = messageObject;
                HashMap<Integer, MessageObject> hashMap2 = transcribeOperationsByDialogPosition;
                if (hashMap2 != null) {
                    hashMap2.remove(Integer.valueOf(reqInfoHash(messageObject)));
                }
                messageObject.messageOwner.voiceTranscriptionFinal = true;
                MessagesStorage.getInstance(messageObject.currentAccount).updateMessageVoiceTranscription(messageObject.getDialogId(), messageObject.getId(), text, messageObject.messageOwner);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.TranscribeButton$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        NotificationCenter.getInstance(r0.currentAccount).postNotificationName(NotificationCenter.voiceTranscriptionUpdate, MessageObject.this, Long.valueOf(transcription_id), text, true, true);
                    }
                });
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
