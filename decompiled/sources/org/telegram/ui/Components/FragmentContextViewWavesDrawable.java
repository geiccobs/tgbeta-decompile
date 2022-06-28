package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class FragmentContextViewWavesDrawable {
    public static final int MUTE_BUTTON_STATE_CONNECTING = 2;
    public static final int MUTE_BUTTON_STATE_MUTE = 1;
    public static final int MUTE_BUTTON_STATE_MUTED_BY_ADMIN = 3;
    public static final int MUTE_BUTTON_STATE_UNMUTE = 0;
    private float amplitude;
    private float amplitude2;
    private float animateAmplitudeDiff;
    private float animateAmplitudeDiff2;
    private float animateToAmplitude;
    WeavingState currentState;
    private long lastUpdateTime;
    WeavingState pausedState;
    float pressedProgress;
    float pressedRemoveProgress;
    WeavingState previousState;
    WeavingState[] states = new WeavingState[4];
    float progressToState = 1.0f;
    ArrayList<View> parents = new ArrayList<>();
    Paint paint = new Paint(1);
    LineBlobDrawable lineBlobDrawable = new LineBlobDrawable(5);
    LineBlobDrawable lineBlobDrawable1 = new LineBlobDrawable(7);
    LineBlobDrawable lineBlobDrawable2 = new LineBlobDrawable(8);
    RectF rect = new RectF();
    Path path = new Path();
    private final Paint selectedPaint = new Paint(1);

    public FragmentContextViewWavesDrawable() {
        for (int i = 0; i < 4; i++) {
            this.states[i] = new WeavingState(i);
        }
    }

    public void draw(float left, float top, float right, float bottom, Canvas canvas, FragmentContextView parentView, float progress) {
        boolean update;
        long dt;
        boolean update2;
        int i;
        float alpha;
        checkColors();
        boolean z = false;
        int i2 = 1;
        if (parentView == null) {
            update = false;
        } else {
            update = this.parents.size() > 0 && parentView == this.parents.get(0);
        }
        if (top > bottom) {
            return;
        }
        WeavingState weavingState = this.currentState;
        if (weavingState != null && this.previousState != null && ((weavingState.currentState == 1 && this.previousState.currentState == 0) || (this.previousState.currentState == 1 && this.currentState.currentState == 0))) {
            z = true;
        }
        boolean rippleTransition = z;
        if (!update) {
            update2 = update;
            dt = 0;
        } else {
            long newTime = SystemClock.elapsedRealtime();
            long dt2 = newTime - this.lastUpdateTime;
            this.lastUpdateTime = newTime;
            if (dt2 > 20) {
                dt2 = 17;
            }
            if (dt2 >= 3) {
                update2 = update;
                dt = dt2;
            } else {
                update2 = false;
                dt = dt2;
            }
        }
        float f = 1.0f;
        float f2 = 0.0f;
        if (update2) {
            float f3 = this.animateToAmplitude;
            float f4 = this.amplitude;
            if (f3 != f4) {
                float f5 = this.animateAmplitudeDiff;
                float f6 = f4 + (((float) dt) * f5);
                this.amplitude = f6;
                if (f5 > 0.0f) {
                    if (f6 > f3) {
                        this.amplitude = f3;
                    }
                } else if (f6 < f3) {
                    this.amplitude = f3;
                }
                parentView.invalidate();
            }
            float f7 = this.animateToAmplitude;
            float f8 = this.amplitude2;
            if (f7 != f8) {
                float f9 = this.animateAmplitudeDiff2;
                float f10 = f8 + (((float) dt) * f9);
                this.amplitude2 = f10;
                if (f9 > 0.0f) {
                    if (f10 > f7) {
                        this.amplitude2 = f7;
                    }
                } else if (f10 < f7) {
                    this.amplitude2 = f7;
                }
                parentView.invalidate();
            }
            if (this.previousState != null) {
                float f11 = this.progressToState + (((float) dt) / 250.0f);
                this.progressToState = f11;
                if (f11 > 1.0f) {
                    this.progressToState = 1.0f;
                    this.previousState = null;
                }
                parentView.invalidate();
            }
        }
        int i3 = 0;
        while (i3 < 2) {
            if (i3 == 0 && this.previousState == null) {
                i = i3;
            } else {
                if (i3 == 0) {
                    this.previousState.setToPaint(this.paint);
                    alpha = f - this.progressToState;
                } else {
                    WeavingState weavingState2 = this.currentState;
                    if (weavingState2 == null) {
                        return;
                    }
                    alpha = this.previousState != null ? this.progressToState : 1.0f;
                    if (update2) {
                        weavingState2.update((int) (bottom - top), (int) (right - left), dt, this.amplitude);
                    }
                    this.currentState.setToPaint(this.paint);
                }
                this.lineBlobDrawable.minRadius = f2;
                this.lineBlobDrawable.maxRadius = AndroidUtilities.dp(2.0f) + (AndroidUtilities.dp(2.0f) * this.amplitude);
                this.lineBlobDrawable1.minRadius = AndroidUtilities.dp(f2);
                this.lineBlobDrawable1.maxRadius = AndroidUtilities.dp(3.0f) + (AndroidUtilities.dp(9.0f) * this.amplitude);
                this.lineBlobDrawable2.minRadius = AndroidUtilities.dp(f2);
                this.lineBlobDrawable2.maxRadius = AndroidUtilities.dp(3.0f) + (AndroidUtilities.dp(9.0f) * this.amplitude);
                if (i3 == i2 && update2) {
                    this.lineBlobDrawable.update(this.amplitude, 0.3f);
                    this.lineBlobDrawable1.update(this.amplitude, 0.7f);
                    this.lineBlobDrawable2.update(this.amplitude, 0.7f);
                }
                this.paint.setAlpha((int) (76.0f * alpha));
                float top1 = AndroidUtilities.dp(6.0f) * this.amplitude2;
                float top2 = AndroidUtilities.dp(6.0f) * this.amplitude2;
                i = i3;
                this.lineBlobDrawable1.draw(left, top - top1, right, bottom, canvas, this.paint, top, progress);
                this.lineBlobDrawable2.draw(left, top - top2, right, bottom, canvas, this.paint, top, progress);
                if (i == 1 && rippleTransition) {
                    this.paint.setAlpha(255);
                } else if (i != 1) {
                    this.paint.setAlpha(255);
                } else {
                    this.paint.setAlpha((int) (255.0f * alpha));
                }
                if (i != 1 || !rippleTransition) {
                    this.lineBlobDrawable.draw(left, top, right, bottom, canvas, this.paint, top, progress);
                } else {
                    this.path.reset();
                    float cx = right - AndroidUtilities.dp(18.0f);
                    float cy = top + ((bottom - top) / 2.0f);
                    float r = (right - left) * 1.1f * alpha;
                    this.path.addCircle(cx, cy, r, Path.Direction.CW);
                    canvas.save();
                    canvas.clipPath(this.path);
                    this.lineBlobDrawable.draw(left, top, right, bottom, canvas, this.paint, top, progress);
                    canvas.restore();
                }
            }
            i3 = i + 1;
            f2 = 0.0f;
            f = 1.0f;
            i2 = 1;
        }
    }

    private void checkColors() {
        int i = 0;
        while (true) {
            WeavingState[] weavingStateArr = this.states;
            if (i < weavingStateArr.length) {
                weavingStateArr[i].checkColor();
                i++;
            } else {
                return;
            }
        }
    }

    private void setState(int state, boolean animated) {
        WeavingState weavingState = this.currentState;
        if (weavingState != null && weavingState.currentState == state) {
            return;
        }
        if (VoIPService.getSharedInstance() == null && this.currentState == null) {
            this.currentState = this.pausedState;
            return;
        }
        WeavingState weavingState2 = animated ? this.currentState : null;
        this.previousState = weavingState2;
        this.currentState = this.states[state];
        if (weavingState2 != null) {
            this.progressToState = 0.0f;
        } else {
            this.progressToState = 1.0f;
        }
    }

    public void setAmplitude(float value) {
        this.animateToAmplitude = value;
        float f = this.amplitude;
        this.animateAmplitudeDiff = (value - f) / 250.0f;
        this.animateAmplitudeDiff2 = (value - f) / 120.0f;
    }

    public void addParent(View parent) {
        if (!this.parents.contains(parent)) {
            this.parents.add(parent);
        }
    }

    public void removeParent(View parent) {
        this.parents.remove(parent);
        if (this.parents.isEmpty()) {
            this.pausedState = this.currentState;
            this.currentState = null;
            this.previousState = null;
        }
    }

    public void updateState(boolean animated) {
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService != null) {
            int currentCallState = voIPService.getCallState();
            if (!voIPService.isSwitchingStream() && (currentCallState == 1 || currentCallState == 2 || currentCallState == 6 || currentCallState == 5)) {
                setState(2, animated);
            } else if (voIPService.groupCall != null) {
                TLRPC.TL_groupCallParticipant participant = voIPService.groupCall.participants.get(voIPService.getSelfId());
                if ((participant != null && !participant.can_self_unmute && participant.muted && !ChatObject.canManageCalls(voIPService.getChat())) || voIPService.groupCall.call.rtmp_stream) {
                    voIPService.setMicMute(true, false, false);
                    setState(3, animated);
                    return;
                }
                setState(voIPService.isMicMute() ? 1 : 0, animated);
            } else {
                setState(voIPService.isMicMute() ? 1 : 0, animated);
            }
        }
    }

    public int getState() {
        WeavingState weavingState = this.currentState;
        if (weavingState != null) {
            return weavingState.currentState;
        }
        return 0;
    }

    public long getRippleFinishedDelay() {
        float f = this.pressedProgress;
        if (f != 0.0f && f != 1.0f) {
            return (1.0f - f) * 150.0f;
        }
        return 0L;
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
        String greenKey1 = Theme.key_voipgroup_topPanelGreen1;
        String greenKey2 = Theme.key_voipgroup_topPanelGreen2;
        String blueKey1 = Theme.key_voipgroup_topPanelBlue1;
        String blueKey2 = Theme.key_voipgroup_topPanelBlue2;
        String mutedByAdmin = Theme.key_voipgroup_mutedByAdminGradient;
        String mutedByAdmin2 = Theme.key_voipgroup_mutedByAdminGradient2;
        String mutedByAdmin3 = Theme.key_voipgroup_mutedByAdminGradient3;

        public WeavingState(int state) {
            this.currentState = state;
            createGradients();
        }

        private void createGradients() {
            int i = this.currentState;
            if (i == 0) {
                int color = Theme.getColor(this.greenKey1);
                this.color1 = color;
                int color2 = Theme.getColor(this.greenKey2);
                this.color2 = color2;
                this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color, color2}, (float[]) null, Shader.TileMode.CLAMP);
            } else if (i == 1) {
                int color3 = Theme.getColor(this.blueKey1);
                this.color1 = color3;
                int color4 = Theme.getColor(this.blueKey2);
                this.color2 = color4;
                this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color3, color4}, (float[]) null, Shader.TileMode.CLAMP);
            } else if (i == 3) {
                int color5 = Theme.getColor(this.mutedByAdmin);
                this.color1 = color5;
                int color6 = Theme.getColor(this.mutedByAdmin3);
                this.color3 = color6;
                int color7 = Theme.getColor(this.mutedByAdmin2);
                this.color2 = color7;
                this.shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{color5, color6, color7}, new float[]{0.0f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
            }
        }

        public void update(int height, int width, long dt, float amplitude) {
            if (this.currentState == 2) {
                return;
            }
            float f = this.duration;
            if (f == 0.0f || this.time >= f) {
                this.duration = Utilities.random.nextInt(700) + 500;
                this.time = 0.0f;
                if (this.targetX == -1.0f) {
                    int i = this.currentState;
                    if (i == 3) {
                        this.targetX = ((Utilities.random.nextInt(100) * 0.05f) / 100.0f) - 0.3f;
                        this.targetY = ((Utilities.random.nextInt(100) * 0.05f) / 100.0f) + 0.7f;
                    } else if (i == 0) {
                        this.targetX = ((Utilities.random.nextInt(100) * 0.2f) / 100.0f) - 0.3f;
                        this.targetY = ((Utilities.random.nextInt(100) * 0.3f) / 100.0f) + 0.7f;
                    } else {
                        this.targetX = ((Utilities.random.nextInt(100) / 100.0f) * 0.2f) + 1.1f;
                        this.targetY = (Utilities.random.nextInt(100) * 4.0f) / 100.0f;
                    }
                }
                this.startX = this.targetX;
                this.startY = this.targetY;
                int i2 = this.currentState;
                if (i2 == 3) {
                    this.targetX = ((Utilities.random.nextInt(100) * 0.05f) / 100.0f) - 0.3f;
                    this.targetY = ((Utilities.random.nextInt(100) * 0.05f) / 100.0f) + 0.7f;
                } else if (i2 == 0) {
                    this.targetX = ((Utilities.random.nextInt(100) * 0.2f) / 100.0f) - 0.3f;
                    this.targetY = ((Utilities.random.nextInt(100) * 0.3f) / 100.0f) + 0.7f;
                } else {
                    this.targetX = ((Utilities.random.nextInt(100) / 100.0f) * 0.2f) + 1.1f;
                    this.targetY = (Utilities.random.nextInt(100) * 4.0f) / 100.0f;
                }
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
            float y = (height * (f5 + ((this.targetY - f5) * interpolation))) - 200.0f;
            float f6 = width / 400.0f;
            int i3 = this.currentState;
            float scale = f6 * ((i3 == 0 || i3 == 3) ? 3.0f : 1.5f);
            this.matrix.reset();
            this.matrix.postTranslate(x, y);
            this.matrix.postScale(scale, scale, x + 200.0f, 200.0f + y);
            this.shader.setLocalMatrix(this.matrix);
        }

        public void checkColor() {
            int i = this.currentState;
            if (i == 0) {
                if (this.color1 != Theme.getColor(this.greenKey1) || this.color2 != Theme.getColor(this.greenKey2)) {
                    createGradients();
                }
            } else if (i == 1) {
                if (this.color1 != Theme.getColor(this.blueKey1) || this.color2 != Theme.getColor(this.blueKey2)) {
                    createGradients();
                }
            } else if (i == 3) {
                if (this.color1 != Theme.getColor(this.mutedByAdmin) || this.color2 != Theme.getColor(this.mutedByAdmin2)) {
                    createGradients();
                }
            }
        }

        public void setToPaint(Paint paint) {
            int i = this.currentState;
            if (i == 0 || i == 1 || i == 3) {
                paint.setShader(this.shader);
                return;
            }
            paint.setShader(null);
            paint.setColor(Theme.getColor(Theme.key_voipgroup_topPanelGray));
        }
    }
}
