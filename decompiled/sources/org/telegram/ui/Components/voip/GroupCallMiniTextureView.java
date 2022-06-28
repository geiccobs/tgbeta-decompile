package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.google.android.exoplayer2.C;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.CrossOutDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.GroupCallStatusIcon;
import org.telegram.ui.GroupCallActivity;
import org.webrtc.GlGenericDrawer;
import org.webrtc.RendererCommon;
/* loaded from: classes5.dex */
public class GroupCallMiniTextureView extends FrameLayout implements GroupCallStatusIcon.Callback {
    GroupCallActivity activity;
    boolean animateEnter;
    int animateToColor;
    public boolean animateToFullscreen;
    public boolean animateToScrimView;
    boolean attached;
    ArrayList<GroupCallMiniTextureView> attachedRenderers;
    ImageView blurredFlippingStub;
    ChatObject.Call call;
    private Drawable castingScreenDrawable;
    private boolean checkScale;
    int collapseSize;
    ValueAnimator colorAnimator;
    int currentAccount;
    public boolean drawFirst;
    ValueAnimator flipAnimator;
    boolean flipHalfReached;
    public boolean forceDetached;
    int fullSize;
    LinearGradient gradientShader;
    int gridItemsCount;
    public boolean hasVideo;
    boolean inPinchToZoom;
    FrameLayout infoContainer;
    private boolean invalidateFromChild;
    boolean isFullscreenMode;
    int lastIconColor;
    private boolean lastLandscapeMode;
    private int lastSize;
    int lastSpeakingFrameColor;
    private final RLottieImageView micIconView;
    private final SimpleTextView nameView;
    private TextView noRtmpStreamTextView;
    ValueAnimator noVideoStubAnimator;
    private NoVideoStubLayout noVideoStubLayout;
    float overlayIconAlpha;
    GroupCallRenderersContainer parentContainer;
    public ChatObject.VideoParticipant participant;
    private CrossOutDrawable pausedVideoDrawable;
    float pinchCenterX;
    float pinchCenterY;
    float pinchScale;
    float pinchTranslationX;
    float pinchTranslationY;
    private boolean postedNoRtmpStreamCallback;
    public GroupCallGridCell primaryView;
    private float progressToBackground;
    float progressToSpeaking;
    private final ImageView screencastIcon;
    public GroupCallFullscreenAdapter.GroupCallUserCell secondaryView;
    private boolean showingAsScrimView;
    public boolean showingInFullscreen;
    float spanCount;
    private GroupCallStatusIcon statusIcon;
    private TextView stopSharingTextView;
    private boolean swipeToBack;
    private float swipeToBackDy;
    public GroupCallGridCell tabletGridView;
    public VoIPTextureView textureView;
    Bitmap thumb;
    Paint thumbPaint;
    private boolean updateNextLayoutAnimated;
    boolean useSpanSize;
    private boolean videoIsPaused;
    private float videoIsPausedProgress;
    Paint gradientPaint = new Paint(1);
    Paint speakingPaint = new Paint(1);
    public float progressToNoVideoStub = 1.0f;
    ImageReceiver imageReceiver = new ImageReceiver();
    ArrayList<Runnable> onFirstFrameRunnables = new ArrayList<>();
    private Runnable noRtmpStreamCallback = new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda5
        @Override // java.lang.Runnable
        public final void run() {
            GroupCallMiniTextureView.this.m3229xc81d612d();
        }
    };
    private Rect rect = new Rect();

    static /* synthetic */ float access$116(GroupCallMiniTextureView x0, float x1) {
        float f = x0.progressToBackground + x1;
        x0.progressToBackground = f;
        return f;
    }

    static /* synthetic */ float access$716(GroupCallMiniTextureView x0, float x1) {
        float f = x0.videoIsPausedProgress + x1;
        x0.videoIsPausedProgress = f;
        return f;
    }

    static /* synthetic */ float access$724(GroupCallMiniTextureView x0, float x1) {
        float f = x0.videoIsPausedProgress - x1;
        x0.videoIsPausedProgress = f;
        return f;
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-GroupCallMiniTextureView */
    public /* synthetic */ void m3229xc81d612d() {
        if (this.textureView.renderer.isFirstFrameRendered()) {
            return;
        }
        this.textureView.animate().cancel();
        this.textureView.animate().alpha(0.0f).setDuration(150L).start();
        this.noRtmpStreamTextView.animate().cancel();
        this.noRtmpStreamTextView.animate().alpha(1.0f).setDuration(150L).start();
    }

    public GroupCallMiniTextureView(final GroupCallRenderersContainer parentContainer, ArrayList<GroupCallMiniTextureView> attachedRenderers, final ChatObject.Call call, final GroupCallActivity activity) {
        super(parentContainer.getContext());
        this.call = call;
        this.currentAccount = activity.getCurrentAccount();
        CrossOutDrawable crossOutDrawable = new CrossOutDrawable(parentContainer.getContext(), R.drawable.calls_video, null);
        this.pausedVideoDrawable = crossOutDrawable;
        crossOutDrawable.setCrossOut(true, false);
        this.pausedVideoDrawable.setOffsets(-AndroidUtilities.dp(4.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
        this.pausedVideoDrawable.setStrokeWidth(AndroidUtilities.dpf2(3.4f));
        this.castingScreenDrawable = parentContainer.getContext().getResources().getDrawable(R.drawable.screencast_big).mutate();
        final TextPaint textPaint = new TextPaint(1);
        textPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        textPaint.setColor(-1);
        final TextPaint textPaint2 = new TextPaint(1);
        textPaint2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        textPaint2.setTextSize(AndroidUtilities.dp(15.0f));
        textPaint2.setColor(-1);
        final String videoOnPauseString = LocaleController.getString("VoipVideoOnPause", R.string.VoipVideoOnPause);
        final StaticLayout staticLayout = new StaticLayout(LocaleController.getString("VoipVideoScreenSharingTwoLines", R.string.VoipVideoScreenSharingTwoLines), textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(call.chatId));
        String text = LocaleController.formatPluralString("Participants", MessagesController.getInstance(this.currentAccount).groupCallVideoMaxParticipants, new Object[0]);
        final StaticLayout noVideoLayout = new StaticLayout(LocaleController.formatString("VoipVideoNotAvailable", R.string.VoipVideoNotAvailable, text), textPaint, AndroidUtilities.dp(400.0f), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        final String sharingScreenString = LocaleController.getString("VoipVideoScreenSharing", R.string.VoipVideoScreenSharing);
        final float textW = textPaint.measureText(videoOnPauseString);
        final float textW3 = textPaint2.measureText(sharingScreenString);
        VoIPTextureView voIPTextureView = new VoIPTextureView(parentContainer.getContext(), false, false, true, true) { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView.1
            float overlayIconAlphaFrom;

            @Override // org.telegram.ui.Components.voip.VoIPTextureView
            public void animateToLayout() {
                super.animateToLayout();
                this.overlayIconAlphaFrom = GroupCallMiniTextureView.this.overlayIconAlpha;
            }

            @Override // org.telegram.ui.Components.voip.VoIPTextureView
            public void updateRendererSize() {
                super.updateRendererSize();
                if (GroupCallMiniTextureView.this.blurredFlippingStub != null && GroupCallMiniTextureView.this.blurredFlippingStub.getParent() != null) {
                    GroupCallMiniTextureView.this.blurredFlippingStub.getLayoutParams().width = GroupCallMiniTextureView.this.textureView.renderer.getMeasuredWidth();
                    GroupCallMiniTextureView.this.blurredFlippingStub.getLayoutParams().height = GroupCallMiniTextureView.this.textureView.renderer.getMeasuredHeight();
                }
            }

            @Override // org.telegram.ui.Components.voip.VoIPTextureView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                float y;
                int size;
                float smallProgress;
                float smallProgress2;
                if (!this.renderer.isFirstFrameRendered() || ((this.renderer.getAlpha() != 1.0f && this.blurRenderer.getAlpha() != 1.0f) || GroupCallMiniTextureView.this.videoIsPaused)) {
                    if (GroupCallMiniTextureView.this.progressToBackground != 1.0f) {
                        GroupCallMiniTextureView.access$116(GroupCallMiniTextureView.this, 0.10666667f);
                        if (GroupCallMiniTextureView.this.progressToBackground > 1.0f) {
                            GroupCallMiniTextureView.this.progressToBackground = 1.0f;
                        } else {
                            invalidate();
                        }
                    }
                    if (GroupCallMiniTextureView.this.thumb == null) {
                        GroupCallMiniTextureView.this.imageReceiver.setImageCoords(this.currentClipHorizontal, this.currentClipVertical, getMeasuredWidth() - (this.currentClipHorizontal * 2.0f), getMeasuredHeight() - (this.currentClipVertical * 2.0f));
                        GroupCallMiniTextureView.this.imageReceiver.setAlpha(GroupCallMiniTextureView.this.progressToBackground);
                        GroupCallMiniTextureView.this.imageReceiver.draw(canvas);
                    } else {
                        canvas.save();
                        canvas.scale(this.currentThumbScale, this.currentThumbScale, getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);
                        if (GroupCallMiniTextureView.this.thumbPaint == null) {
                            GroupCallMiniTextureView.this.thumbPaint = new Paint(1);
                            GroupCallMiniTextureView.this.thumbPaint.setFilterBitmap(true);
                        }
                        canvas.drawBitmap(GroupCallMiniTextureView.this.thumb, (getMeasuredWidth() - GroupCallMiniTextureView.this.thumb.getWidth()) / 2.0f, (getMeasuredHeight() - GroupCallMiniTextureView.this.thumb.getHeight()) / 2.0f, GroupCallMiniTextureView.this.thumbPaint);
                        canvas.restore();
                    }
                    if (GroupCallMiniTextureView.this.participant == call.videoNotAvailableParticipant) {
                        if (GroupCallMiniTextureView.this.showingInFullscreen || !parentContainer.inFullscreenMode) {
                            float iconSize = AndroidUtilities.dp(48.0f);
                            textPaint.setAlpha(255);
                            canvas.save();
                            canvas.translate((((getMeasuredWidth() - iconSize) / 2.0f) - (AndroidUtilities.dp(400.0f) / 2.0f)) + (iconSize / 2.0f), ((getMeasuredHeight() / 2) - iconSize) + iconSize + AndroidUtilities.dp(10.0f));
                            noVideoLayout.draw(canvas);
                            canvas.restore();
                        }
                        if (GroupCallMiniTextureView.this.stopSharingTextView.getVisibility() != 4) {
                            GroupCallMiniTextureView.this.stopSharingTextView.setVisibility(4);
                        }
                    } else if (!GroupCallMiniTextureView.this.participant.presentation || !GroupCallMiniTextureView.this.participant.participant.self) {
                        if (GroupCallMiniTextureView.this.stopSharingTextView.getVisibility() != 4) {
                            GroupCallMiniTextureView.this.stopSharingTextView.setVisibility(4);
                        }
                        activity.cellFlickerDrawable.draw(canvas, GroupCallMiniTextureView.this);
                    } else {
                        if (GroupCallMiniTextureView.this.stopSharingTextView.getVisibility() != 0) {
                            GroupCallMiniTextureView.this.stopSharingTextView.setVisibility(0);
                            GroupCallMiniTextureView.this.stopSharingTextView.setScaleX(1.0f);
                            GroupCallMiniTextureView.this.stopSharingTextView.setScaleY(1.0f);
                        }
                        float progressToFullscreen = GroupCallMiniTextureView.this.drawFirst ? 0.0f : parentContainer.progressToFullscreenMode;
                        int size2 = AndroidUtilities.dp(33.0f);
                        if (GroupCallMiniTextureView.this.animateToFullscreen || GroupCallMiniTextureView.this.showingInFullscreen) {
                            size = (int) (size2 + AndroidUtilities.dp(10.0f) + (AndroidUtilities.dp(39.0f) * parentContainer.progressToFullscreenMode));
                        } else {
                            size = (int) (size2 + (AndroidUtilities.dp(10.0f) * Math.max(1.0f - parentContainer.progressToFullscreenMode, (GroupCallMiniTextureView.this.showingAsScrimView || GroupCallMiniTextureView.this.animateToScrimView) ? parentContainer.progressToScrimView : 0.0f)));
                        }
                        int x = (getMeasuredWidth() - size) / 2;
                        float scrimProgress = (GroupCallMiniTextureView.this.showingAsScrimView || GroupCallMiniTextureView.this.animateToScrimView) ? parentContainer.progressToScrimView : 0.0f;
                        if (GroupCallMiniTextureView.this.showingInFullscreen) {
                            smallProgress2 = progressToFullscreen;
                            smallProgress = progressToFullscreen;
                        } else {
                            smallProgress = GroupCallMiniTextureView.this.animateToFullscreen ? parentContainer.progressToFullscreenMode : scrimProgress;
                            smallProgress2 = (GroupCallMiniTextureView.this.showingAsScrimView || GroupCallMiniTextureView.this.animateToScrimView) ? parentContainer.progressToScrimView : parentContainer.progressToFullscreenMode;
                        }
                        int y2 = (int) (((((getMeasuredHeight() - size) / 2) - AndroidUtilities.dp(28.0f)) - ((AndroidUtilities.dp(17.0f) + (AndroidUtilities.dp(74.0f) * ((GroupCallMiniTextureView.this.showingInFullscreen || GroupCallMiniTextureView.this.animateToFullscreen) ? parentContainer.progressToFullscreenMode : 0.0f))) * smallProgress)) + (AndroidUtilities.dp(17.0f) * smallProgress2));
                        GroupCallMiniTextureView.this.castingScreenDrawable.setBounds(x, y2, x + size, y2 + size);
                        GroupCallMiniTextureView.this.castingScreenDrawable.draw(canvas);
                        if (parentContainer.progressToFullscreenMode <= 0.0f && scrimProgress <= 0.0f) {
                            GroupCallMiniTextureView.this.stopSharingTextView.setAlpha(0.0f);
                        } else {
                            float alpha = Math.max(parentContainer.progressToFullscreenMode, scrimProgress) * smallProgress;
                            textPaint2.setAlpha((int) (alpha * 255.0f));
                            if (GroupCallMiniTextureView.this.animateToFullscreen || GroupCallMiniTextureView.this.showingInFullscreen) {
                                GroupCallMiniTextureView.this.stopSharingTextView.setAlpha((1.0f - scrimProgress) * alpha);
                            } else {
                                GroupCallMiniTextureView.this.stopSharingTextView.setAlpha(0.0f);
                            }
                            canvas.drawText(sharingScreenString, (x - (textW3 / 2.0f)) + (size / 2.0f), y2 + size + AndroidUtilities.dp(32.0f), textPaint2);
                        }
                        GroupCallMiniTextureView.this.stopSharingTextView.setTranslationY((((y2 + size) + AndroidUtilities.dp(72.0f)) + GroupCallMiniTextureView.this.swipeToBackDy) - this.currentClipVertical);
                        GroupCallMiniTextureView.this.stopSharingTextView.setTranslationX(((getMeasuredWidth() - GroupCallMiniTextureView.this.stopSharingTextView.getMeasuredWidth()) / 2.0f) - this.currentClipHorizontal);
                        if (parentContainer.progressToFullscreenMode < 1.0f && scrimProgress < 1.0f) {
                            TextPaint textPaint3 = textPaint;
                            double max = Math.max(parentContainer.progressToFullscreenMode, scrimProgress);
                            Double.isNaN(max);
                            textPaint3.setAlpha((int) ((1.0d - max) * 255.0d));
                            canvas.save();
                            canvas.translate((x - (AndroidUtilities.dp(400.0f) / 2.0f)) + (size / 2.0f), y2 + size + AndroidUtilities.dp(10.0f));
                            staticLayout.draw(canvas);
                            canvas.restore();
                        }
                    }
                    invalidate();
                }
                GroupCallMiniTextureView.this.noRtmpStreamTextView.setTranslationY((((getMeasuredHeight() - GroupCallMiniTextureView.this.noRtmpStreamTextView.getMeasuredHeight()) / 2.0f) + GroupCallMiniTextureView.this.swipeToBackDy) - this.currentClipVertical);
                GroupCallMiniTextureView.this.noRtmpStreamTextView.setTranslationX(((getMeasuredWidth() - GroupCallMiniTextureView.this.noRtmpStreamTextView.getMeasuredWidth()) / 2.0f) - this.currentClipHorizontal);
                if (GroupCallMiniTextureView.this.blurredFlippingStub != null && GroupCallMiniTextureView.this.blurredFlippingStub.getParent() != null) {
                    GroupCallMiniTextureView.this.blurredFlippingStub.setScaleX(GroupCallMiniTextureView.this.textureView.renderer.getScaleX());
                    GroupCallMiniTextureView.this.blurredFlippingStub.setScaleY(GroupCallMiniTextureView.this.textureView.renderer.getScaleY());
                }
                super.dispatchDraw(canvas);
                float y3 = (getMeasuredHeight() - this.currentClipVertical) - AndroidUtilities.dp(80.0f);
                if (GroupCallMiniTextureView.this.participant != call.videoNotAvailableParticipant) {
                    canvas.save();
                    if ((GroupCallMiniTextureView.this.showingInFullscreen || GroupCallMiniTextureView.this.animateToFullscreen) && !GroupCallActivity.isLandscapeMode && !GroupCallActivity.isTabletMode) {
                        y3 -= (AndroidUtilities.dp(90.0f) * parentContainer.progressToFullscreenMode) * (1.0f - parentContainer.progressToHideUi);
                    }
                    canvas.translate(0.0f, y3);
                    canvas.drawPaint(GroupCallMiniTextureView.this.gradientPaint);
                    canvas.restore();
                }
                if (GroupCallMiniTextureView.this.videoIsPaused || GroupCallMiniTextureView.this.videoIsPausedProgress != 0.0f) {
                    if (!GroupCallMiniTextureView.this.videoIsPaused || GroupCallMiniTextureView.this.videoIsPausedProgress == 1.0f) {
                        if (!GroupCallMiniTextureView.this.videoIsPaused && GroupCallMiniTextureView.this.videoIsPausedProgress != 0.0f) {
                            GroupCallMiniTextureView.access$724(GroupCallMiniTextureView.this, 0.064f);
                            if (GroupCallMiniTextureView.this.videoIsPausedProgress < 0.0f) {
                                GroupCallMiniTextureView.this.videoIsPausedProgress = 0.0f;
                            } else {
                                invalidate();
                            }
                        }
                    } else {
                        GroupCallMiniTextureView.access$716(GroupCallMiniTextureView.this, 0.064f);
                        if (GroupCallMiniTextureView.this.videoIsPausedProgress > 1.0f) {
                            GroupCallMiniTextureView.this.videoIsPausedProgress = 1.0f;
                        } else {
                            invalidate();
                        }
                    }
                    float a = GroupCallMiniTextureView.this.videoIsPausedProgress * (isInAnimation() ? (this.overlayIconAlphaFrom * (1.0f - this.animationProgress)) + (GroupCallMiniTextureView.this.overlayIconAlpha * this.animationProgress) : GroupCallMiniTextureView.this.overlayIconAlpha);
                    if (a > 0.0f) {
                        float iconSize2 = AndroidUtilities.dp(48.0f);
                        float x2 = (getMeasuredWidth() - iconSize2) / 2.0f;
                        float y4 = (getMeasuredHeight() - iconSize2) / 2.0f;
                        if (GroupCallMiniTextureView.this.participant != call.videoNotAvailableParticipant) {
                            y = y4;
                        } else {
                            y = y4 - (iconSize2 / 2.5f);
                        }
                        AndroidUtilities.rectTmp.set((int) x2, (int) y, (int) (x2 + iconSize2), (int) (y + iconSize2));
                        if (a != 1.0f) {
                            canvas.saveLayerAlpha(AndroidUtilities.rectTmp, (int) (a * 255.0f), 31);
                        } else {
                            canvas.save();
                        }
                        GroupCallMiniTextureView.this.pausedVideoDrawable.setBounds((int) AndroidUtilities.rectTmp.left, (int) AndroidUtilities.rectTmp.top, (int) AndroidUtilities.rectTmp.right, (int) AndroidUtilities.rectTmp.bottom);
                        GroupCallMiniTextureView.this.pausedVideoDrawable.draw(canvas);
                        canvas.restore();
                        float a2 = a * parentContainer.progressToFullscreenMode;
                        if (a2 > 0.0f && GroupCallMiniTextureView.this.participant != call.videoNotAvailableParticipant) {
                            textPaint.setAlpha((int) (255.0f * a2));
                            canvas.drawText(videoOnPauseString, (x2 - (textW / 2.0f)) + (iconSize2 / 2.0f), y + iconSize2 + AndroidUtilities.dp(16.0f), textPaint);
                        }
                    }
                }
            }

            @Override // android.view.ViewGroup
            protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (GroupCallMiniTextureView.this.inPinchToZoom && child == GroupCallMiniTextureView.this.textureView.renderer) {
                    canvas.save();
                    canvas.scale(GroupCallMiniTextureView.this.pinchScale, GroupCallMiniTextureView.this.pinchScale, GroupCallMiniTextureView.this.pinchCenterX, GroupCallMiniTextureView.this.pinchCenterY);
                    canvas.translate(GroupCallMiniTextureView.this.pinchTranslationX, GroupCallMiniTextureView.this.pinchTranslationY);
                    boolean b = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return b;
                }
                boolean b2 = super.drawChild(canvas, child, drawingTime);
                return b2;
            }

            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                GroupCallMiniTextureView.this.invalidateFromChild = true;
                GroupCallMiniTextureView.this.invalidate();
                GroupCallMiniTextureView.this.invalidateFromChild = false;
            }

            @Override // org.telegram.ui.Components.voip.VoIPTextureView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                if (GroupCallMiniTextureView.this.attached && GroupCallMiniTextureView.this.checkScale && this.renderer.rotatedFrameHeight != 0 && this.renderer.rotatedFrameWidth != 0) {
                    if (GroupCallMiniTextureView.this.showingAsScrimView) {
                        GroupCallMiniTextureView.this.textureView.scaleType = SCALE_TYPE_FIT;
                    } else if (GroupCallMiniTextureView.this.showingInFullscreen) {
                        GroupCallMiniTextureView.this.textureView.scaleType = SCALE_TYPE_FIT;
                    } else if (parentContainer.inFullscreenMode) {
                        GroupCallMiniTextureView.this.textureView.scaleType = SCALE_TYPE_FILL;
                    } else if (GroupCallMiniTextureView.this.participant.presentation) {
                        GroupCallMiniTextureView.this.textureView.scaleType = SCALE_TYPE_FIT;
                    } else {
                        GroupCallMiniTextureView.this.textureView.scaleType = SCALE_TYPE_ADAPTIVE;
                    }
                    GroupCallMiniTextureView.this.checkScale = false;
                }
                super.onLayout(changed, left, top, right, bottom);
                if (this.renderer.rotatedFrameHeight != 0 && this.renderer.rotatedFrameWidth != 0 && GroupCallMiniTextureView.this.participant != null) {
                    GroupCallMiniTextureView.this.participant.setAspectRatio(this.renderer.rotatedFrameWidth, this.renderer.rotatedFrameHeight, call);
                }
            }

            @Override // org.telegram.ui.Components.voip.VoIPTextureView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                GroupCallMiniTextureView.this.requestLayout();
                super.requestLayout();
            }

            @Override // org.telegram.ui.Components.voip.VoIPTextureView
            protected void onFirstFrameRendered() {
                invalidate();
                ChatObject.Call call2 = call;
                if (call2 != null && call2.call.rtmp_stream && GroupCallMiniTextureView.this.postedNoRtmpStreamCallback) {
                    AndroidUtilities.cancelRunOnUIThread(GroupCallMiniTextureView.this.noRtmpStreamCallback);
                    GroupCallMiniTextureView.this.postedNoRtmpStreamCallback = false;
                    GroupCallMiniTextureView.this.noRtmpStreamTextView.animate().cancel();
                    GroupCallMiniTextureView.this.noRtmpStreamTextView.animate().alpha(0.0f).setDuration(150L).start();
                    GroupCallMiniTextureView.this.textureView.animate().cancel();
                    GroupCallMiniTextureView.this.textureView.animate().alpha(1.0f).setDuration(150L).start();
                }
                if (!GroupCallMiniTextureView.this.videoIsPaused && this.renderer.getAlpha() != 1.0f) {
                    this.renderer.animate().setDuration(300L).alpha(1.0f);
                }
                if (this.blurRenderer != null && this.blurRenderer.getAlpha() != 1.0f) {
                    this.blurRenderer.animate().setDuration(300L).alpha(1.0f);
                }
                if (GroupCallMiniTextureView.this.blurredFlippingStub != null && GroupCallMiniTextureView.this.blurredFlippingStub.getParent() != null) {
                    if (GroupCallMiniTextureView.this.blurredFlippingStub.getAlpha() == 1.0f) {
                        GroupCallMiniTextureView.this.blurredFlippingStub.animate().alpha(0.0f).setDuration(300L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView.1.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (GroupCallMiniTextureView.this.blurredFlippingStub.getParent() != null) {
                                    GroupCallMiniTextureView.this.textureView.removeView(GroupCallMiniTextureView.this.blurredFlippingStub);
                                }
                            }
                        }).start();
                    } else if (GroupCallMiniTextureView.this.blurredFlippingStub.getParent() != null) {
                        GroupCallMiniTextureView.this.textureView.removeView(GroupCallMiniTextureView.this.blurredFlippingStub);
                    }
                }
                if (this.renderer.rotatedFrameHeight != 0 && this.renderer.rotatedFrameWidth != 0 && GroupCallMiniTextureView.this.participant != null) {
                    GroupCallMiniTextureView.this.participant.setAspectRatio(this.renderer.rotatedFrameWidth, this.renderer.rotatedFrameHeight, call);
                }
            }
        };
        this.textureView = voIPTextureView;
        voIPTextureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        this.parentContainer = parentContainer;
        this.attachedRenderers = attachedRenderers;
        this.activity = activity;
        this.textureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView.2
            @Override // org.webrtc.RendererCommon.RendererEvents
            public void onFirstFrameRendered() {
                for (int i = 0; i < GroupCallMiniTextureView.this.onFirstFrameRunnables.size(); i++) {
                    AndroidUtilities.cancelRunOnUIThread(GroupCallMiniTextureView.this.onFirstFrameRunnables.get(i));
                    GroupCallMiniTextureView.this.onFirstFrameRunnables.get(i).run();
                }
                GroupCallMiniTextureView.this.onFirstFrameRunnables.clear();
            }

            @Override // org.webrtc.RendererCommon.RendererEvents
            public void onFrameResolutionChanged(int videoWidth, int videoHeight, int rotation) {
            }
        });
        this.textureView.attachBackgroundRenderer();
        setClipChildren(false);
        this.textureView.renderer.setAlpha(0.0f);
        addView(this.textureView);
        NoVideoStubLayout noVideoStubLayout = new NoVideoStubLayout(getContext());
        this.noVideoStubLayout = noVideoStubLayout;
        addView(noVideoStubLayout);
        SimpleTextView simpleTextView = new SimpleTextView(parentContainer.getContext());
        this.nameView = simpleTextView;
        simpleTextView.setTextSize(13);
        simpleTextView.setTextColor(ColorUtils.setAlphaComponent(-1, 229));
        simpleTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        simpleTextView.setFullTextMaxLines(1);
        simpleTextView.setBuildFullLayout(true);
        FrameLayout frameLayout = new FrameLayout(parentContainer.getContext());
        this.infoContainer = frameLayout;
        frameLayout.addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, 19, 32.0f, 0.0f, 8.0f, 0.0f));
        addView(this.infoContainer, LayoutHelper.createFrame(-1, 32.0f));
        this.speakingPaint.setStyle(Paint.Style.STROKE);
        this.speakingPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.speakingPaint.setColor(Theme.getColor(Theme.key_voipgroup_speakingText));
        this.infoContainer.setClipChildren(false);
        RLottieImageView rLottieImageView = new RLottieImageView(parentContainer.getContext());
        this.micIconView = rLottieImageView;
        addView(rLottieImageView, LayoutHelper.createFrame(24, 24.0f, 0, 4.0f, 6.0f, 4.0f, 0.0f));
        ImageView imageView = new ImageView(parentContainer.getContext());
        this.screencastIcon = imageView;
        addView(imageView, LayoutHelper.createFrame(24, 24.0f, 0, 4.0f, 6.0f, 4.0f, 0.0f));
        imageView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        imageView.setImageDrawable(ContextCompat.getDrawable(parentContainer.getContext(), R.drawable.voicechat_screencast));
        imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        Drawable rippleDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(19.0f), 0, ColorUtils.setAlphaComponent(-1, 100));
        TextView textView = new TextView(parentContainer.getContext()) { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView.3
            @Override // android.widget.TextView, android.view.View
            public boolean onTouchEvent(MotionEvent event) {
                if (Math.abs(GroupCallMiniTextureView.this.stopSharingTextView.getAlpha() - 1.0f) > 0.001f) {
                    return false;
                }
                return super.onTouchEvent(event);
            }
        };
        this.stopSharingTextView = textView;
        textView.setText(LocaleController.getString("VoipVideoScreenStopSharing", R.string.VoipVideoScreenStopSharing));
        this.stopSharingTextView.setTextSize(1, 15.0f);
        this.stopSharingTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.stopSharingTextView.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        this.stopSharingTextView.setTextColor(-1);
        this.stopSharingTextView.setBackground(rippleDrawable);
        this.stopSharingTextView.setGravity(17);
        this.stopSharingTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                GroupCallMiniTextureView.this.m3230x550a784c(view);
            }
        });
        addView(this.stopSharingTextView, LayoutHelper.createFrame(-2, 38, 51));
        TextView textView2 = new TextView(parentContainer.getContext());
        this.noRtmpStreamTextView = textView2;
        textView2.setTextSize(1, 15.0f);
        this.noRtmpStreamTextView.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        this.noRtmpStreamTextView.setTextColor(Theme.getColor(Theme.key_voipgroup_lastSeenText));
        this.noRtmpStreamTextView.setBackground(rippleDrawable);
        this.noRtmpStreamTextView.setGravity(17);
        this.noRtmpStreamTextView.setAlpha(0.0f);
        if (ChatObject.canManageCalls(chat)) {
            this.noRtmpStreamTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.NoRtmpStreamFromAppOwner)));
        } else {
            this.noRtmpStreamTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("NoRtmpStreamFromAppViewer", R.string.NoRtmpStreamFromAppViewer, chat.title)));
        }
        addView(this.noRtmpStreamTextView, LayoutHelper.createFrame(-2, -2, 51));
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-voip-GroupCallMiniTextureView */
    public /* synthetic */ void m3230x550a784c(View v) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().stopScreenCapture();
        }
        this.stopSharingTextView.animate().alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setDuration(180L).start();
    }

    public boolean isInsideStopScreenButton(float x, float y) {
        this.stopSharingTextView.getHitRect(this.rect);
        return this.rect.contains((int) x, (int) y);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.attached) {
            float y = (((this.textureView.getY() + this.textureView.getMeasuredHeight()) - this.textureView.currentClipVertical) - this.infoContainer.getMeasuredHeight()) + this.swipeToBackDy;
            if (this.showingAsScrimView || this.animateToScrimView) {
                this.infoContainer.setAlpha(1.0f - this.parentContainer.progressToScrimView);
                this.micIconView.setAlpha(1.0f - this.parentContainer.progressToScrimView);
            } else if (this.showingInFullscreen || this.animateToFullscreen) {
                if (!GroupCallActivity.isLandscapeMode && !GroupCallActivity.isTabletMode) {
                    y -= (AndroidUtilities.dp(90.0f) * this.parentContainer.progressToFullscreenMode) * (1.0f - this.parentContainer.progressToHideUi);
                }
                this.infoContainer.setAlpha(1.0f);
                this.micIconView.setAlpha(1.0f);
            } else if (this.secondaryView != null) {
                this.infoContainer.setAlpha(1.0f - this.parentContainer.progressToFullscreenMode);
                this.micIconView.setAlpha(1.0f - this.parentContainer.progressToFullscreenMode);
            } else {
                this.infoContainer.setAlpha(1.0f);
                this.micIconView.setAlpha(1.0f);
            }
            if (this.showingInFullscreen || this.animateToFullscreen) {
                this.nameView.setFullAlpha(this.parentContainer.progressToFullscreenMode);
            } else {
                this.nameView.setFullAlpha(0.0f);
            }
            this.micIconView.setTranslationX(this.infoContainer.getX());
            this.micIconView.setTranslationY(y - AndroidUtilities.dp(2.0f));
            if (this.screencastIcon.getVisibility() == 0) {
                this.screencastIcon.setTranslationX((this.textureView.getMeasuredWidth() - (this.textureView.currentClipHorizontal * 2.0f)) - AndroidUtilities.dp(32.0f));
                this.screencastIcon.setTranslationY(y - AndroidUtilities.dp(2.0f));
                this.screencastIcon.setAlpha(Math.min(1.0f - this.parentContainer.progressToFullscreenMode, 1.0f - this.parentContainer.progressToScrimView));
            }
            this.infoContainer.setTranslationY(y);
            this.infoContainer.setTranslationX(this.drawFirst ? 0.0f : AndroidUtilities.dp(6.0f) * this.parentContainer.progressToFullscreenMode);
        }
        super.dispatchDraw(canvas);
        if (this.attached) {
            GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
            if (groupCallStatusIcon != null) {
                if (groupCallStatusIcon.isSpeaking) {
                    float f = this.progressToSpeaking;
                    if (f != 1.0f) {
                        float f2 = f + 0.053333335f;
                        this.progressToSpeaking = f2;
                        if (f2 > 1.0f) {
                            this.progressToSpeaking = 1.0f;
                        } else {
                            invalidate();
                        }
                    }
                }
                if (!this.statusIcon.isSpeaking) {
                    float f3 = this.progressToSpeaking;
                    if (f3 != 0.0f) {
                        float f4 = f3 - 0.053333335f;
                        this.progressToSpeaking = f4;
                        if (f4 < 0.0f) {
                            this.progressToSpeaking = 0.0f;
                        } else {
                            invalidate();
                        }
                    }
                }
            }
            float selectionProgress = this.progressToSpeaking * (1.0f - this.parentContainer.progressToFullscreenMode) * (1.0f - this.parentContainer.progressToScrimView);
            if (this.progressToSpeaking > 0.0f) {
                this.speakingPaint.setAlpha((int) (255.0f * selectionProgress));
                float scale = (Math.max(0.0f, 1.0f - (Math.abs(this.swipeToBackDy) / AndroidUtilities.dp(300.0f))) * 0.1f) + 0.9f;
                canvas.save();
                AndroidUtilities.rectTmp.set(this.textureView.getX() + this.textureView.currentClipHorizontal, this.textureView.getY() + this.textureView.currentClipVertical, (this.textureView.getX() + this.textureView.getMeasuredWidth()) - this.textureView.currentClipHorizontal, (this.textureView.getY() + this.textureView.getMeasuredHeight()) - this.textureView.currentClipVertical);
                canvas.scale(scale, scale, AndroidUtilities.rectTmp.centerX(), AndroidUtilities.rectTmp.centerY());
                canvas.translate(0.0f, this.swipeToBackDy);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, this.textureView.roundRadius, this.textureView.roundRadius, this.speakingPaint);
                canvas.restore();
            }
        }
    }

    public void getRenderBufferBitmap(GlGenericDrawer.TextureCallback callback) {
        this.textureView.renderer.getRenderBufferBitmap(callback);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (this.swipeToBack && (child == this.textureView || child == this.noVideoStubLayout)) {
            float scale = (Math.max(0.0f, 1.0f - (Math.abs(this.swipeToBackDy) / AndroidUtilities.dp(300.0f))) * 0.1f) + 0.9f;
            canvas.save();
            canvas.scale(scale, scale, child.getX() + (child.getMeasuredWidth() / 2.0f), child.getY() + (child.getMeasuredHeight() / 2.0f));
            canvas.translate(0.0f, this.swipeToBackDy);
            boolean b = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return b;
        }
        boolean b2 = super.drawChild(canvas, child, drawingTime);
        return b2;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        SimpleTextView simpleTextView;
        int spanCountTotal;
        float listSize;
        float h;
        float w;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.infoContainer.getLayoutParams();
        int i = layoutParams.leftMargin;
        float nameScale = 1.0f;
        if (this.call.call.rtmp_stream) {
            nameScale = 0.0f;
        }
        if (this.lastLandscapeMode != GroupCallActivity.isLandscapeMode) {
            this.checkScale = true;
            this.lastLandscapeMode = GroupCallActivity.isLandscapeMode;
        }
        int dp = AndroidUtilities.dp(2.0f);
        layoutParams.rightMargin = dp;
        layoutParams.leftMargin = dp;
        if (this.updateNextLayoutAnimated) {
            this.nameView.animate().scaleX(nameScale).scaleY(nameScale).start();
            this.micIconView.animate().scaleX(nameScale).scaleY(nameScale).start();
        } else {
            this.nameView.animate().cancel();
            this.nameView.setScaleX(nameScale);
            this.nameView.setScaleY(nameScale);
            this.micIconView.animate().cancel();
            this.micIconView.setScaleX(nameScale);
            this.micIconView.setScaleY(nameScale);
            this.infoContainer.animate().cancel();
        }
        int i2 = 0;
        this.updateNextLayoutAnimated = false;
        if (this.showingInFullscreen) {
            updateSize(0);
            this.overlayIconAlpha = 1.0f;
            if (GroupCallActivity.isTabletMode) {
                int w2 = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(328.0f);
                int h2 = View.MeasureSpec.getSize(heightMeasureSpec);
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(w2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(h2 - AndroidUtilities.dp(4.0f), C.BUFFER_FLAG_ENCRYPTED));
            } else if (!GroupCallActivity.isLandscapeMode) {
                int h3 = View.MeasureSpec.getSize(heightMeasureSpec);
                if (!this.call.call.rtmp_stream) {
                    h3 -= AndroidUtilities.dp(92.0f);
                }
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(h3, C.BUFFER_FLAG_ENCRYPTED));
            } else {
                int w3 = View.MeasureSpec.getSize(widthMeasureSpec);
                if (!this.call.call.rtmp_stream) {
                    w3 -= AndroidUtilities.dp(92.0f);
                }
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(w3, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), C.BUFFER_FLAG_ENCRYPTED));
            }
        } else if (!this.showingAsScrimView) {
            if (this.useSpanSize) {
                this.overlayIconAlpha = 1.0f;
                if (GroupCallActivity.isTabletMode && this.tabletGridView != null) {
                    spanCountTotal = 6;
                } else {
                    spanCountTotal = GroupCallActivity.isLandscapeMode ? 6 : 2;
                }
                if (this.tabletGridView != null) {
                    listSize = View.MeasureSpec.getSize(widthMeasureSpec) - AndroidUtilities.dp(344.0f);
                } else if (GroupCallActivity.isTabletMode) {
                    listSize = AndroidUtilities.dp(320.0f);
                } else {
                    int size = View.MeasureSpec.getSize(widthMeasureSpec) - (AndroidUtilities.dp(14.0f) * 2);
                    if (GroupCallActivity.isLandscapeMode) {
                        i2 = -AndroidUtilities.dp(90.0f);
                    }
                    listSize = size + i2;
                }
                float w4 = (this.spanCount / spanCountTotal) * listSize;
                GroupCallGridCell groupCallGridCell = this.tabletGridView;
                if (groupCallGridCell != null) {
                    h = groupCallGridCell.getItemHeight() - AndroidUtilities.dp(4.0f);
                    w = w4 - AndroidUtilities.dp(4.0f);
                } else {
                    if (GroupCallActivity.isTabletMode) {
                        h = listSize / 2.0f;
                    } else {
                        h = listSize / (GroupCallActivity.isLandscapeMode ? 3 : 2);
                    }
                    w = w4 - AndroidUtilities.dp(2.0f);
                }
                float layoutContainerW = w;
                FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.infoContainer.getLayoutParams();
                if (this.screencastIcon.getVisibility() == 0) {
                    layoutContainerW -= AndroidUtilities.dp(28.0f);
                }
                updateSize((int) layoutContainerW);
                layoutParams2.width = (int) (layoutContainerW - (layoutParams2.leftMargin * 2));
                super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) w, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec((int) h, C.BUFFER_FLAG_ENCRYPTED));
            } else {
                this.overlayIconAlpha = 0.0f;
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            this.overlayIconAlpha = 1.0f;
            int size2 = Math.min(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec)) - (AndroidUtilities.dp(14.0f) * 2);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(size2, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(getPaddingBottom() + size2, C.BUFFER_FLAG_ENCRYPTED));
        }
        int size3 = View.MeasureSpec.getSize(heightMeasureSpec) + (View.MeasureSpec.getSize(widthMeasureSpec) << 16);
        if (this.lastSize != size3) {
            this.lastSize = size3;
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(120.0f), 0, ColorUtils.setAlphaComponent(-16777216, 120), Shader.TileMode.CLAMP);
            this.gradientShader = linearGradient;
            this.gradientPaint.setShader(linearGradient);
        }
        this.nameView.setPivotX(0.0f);
        this.nameView.setPivotY(simpleTextView.getMeasuredHeight() / 2.0f);
    }

    public static GroupCallMiniTextureView getOrCreate(ArrayList<GroupCallMiniTextureView> attachedRenderers, GroupCallRenderersContainer renderersContainer, GroupCallGridCell primaryView, GroupCallFullscreenAdapter.GroupCallUserCell secondaryView, GroupCallGridCell tabletGridView, ChatObject.VideoParticipant participant, ChatObject.Call call, GroupCallActivity activity) {
        GroupCallMiniTextureView renderer = null;
        int i = 0;
        while (true) {
            if (i >= attachedRenderers.size()) {
                break;
            } else if (!participant.equals(attachedRenderers.get(i).participant)) {
                i++;
            } else {
                GroupCallMiniTextureView renderer2 = attachedRenderers.get(i);
                renderer = renderer2;
                break;
            }
        }
        if (renderer == null) {
            renderer = new GroupCallMiniTextureView(renderersContainer, attachedRenderers, call, activity);
        }
        if (primaryView != null) {
            renderer.setPrimaryView(primaryView);
        }
        if (secondaryView != null) {
            renderer.setSecondaryView(secondaryView);
        }
        if (tabletGridView != null) {
            renderer.setTabletGridView(tabletGridView);
        }
        return renderer;
    }

    public void setTabletGridView(GroupCallGridCell tabletGridView) {
        if (this.tabletGridView != tabletGridView) {
            this.tabletGridView = tabletGridView;
            updateAttachState(true);
        }
    }

    public void setPrimaryView(GroupCallGridCell primaryView) {
        if (this.primaryView != primaryView) {
            this.primaryView = primaryView;
            this.checkScale = true;
            updateAttachState(true);
        }
    }

    public void setSecondaryView(GroupCallFullscreenAdapter.GroupCallUserCell secondaryView) {
        if (this.secondaryView != secondaryView) {
            this.secondaryView = secondaryView;
            this.checkScale = true;
            updateAttachState(true);
        }
    }

    public void setShowingAsScrimView(boolean showing, boolean animated) {
        this.showingAsScrimView = showing;
        updateAttachState(animated);
    }

    public void setShowingInFullscreen(boolean showing, boolean animated) {
        if (this.showingInFullscreen != showing) {
            this.showingInFullscreen = showing;
            this.checkScale = true;
            updateAttachState(animated);
        }
    }

    public void setFullscreenMode(boolean fullscreenMode, boolean animated) {
        if (this.isFullscreenMode != fullscreenMode) {
            this.isFullscreenMode = fullscreenMode;
            updateAttachState(!(this.primaryView == null && this.tabletGridView == null) && animated);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:158:0x02e0  */
    /* JADX WARN: Removed duplicated region for block: B:161:0x02f4  */
    /* JADX WARN: Removed duplicated region for block: B:166:0x030a  */
    /* JADX WARN: Removed duplicated region for block: B:363:0x0735  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateAttachState(boolean r30) {
        /*
            Method dump skipped, instructions count: 1851
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.updateAttachState(boolean):void");
    }

    /* renamed from: lambda$updateAttachState$2$org-telegram-ui-Components-voip-GroupCallMiniTextureView */
    public /* synthetic */ void m3234x7c1937a8(View viewToRemove) {
        this.parentContainer.removeView(viewToRemove);
    }

    /* renamed from: lambda$updateAttachState$3$org-telegram-ui-Components-voip-GroupCallMiniTextureView */
    public /* synthetic */ void m3235x9064ec7(ValueAnimator valueAnimator1) {
        float floatValue = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
        this.progressToNoVideoStub = floatValue;
        this.noVideoStubLayout.setAlpha(floatValue);
        this.textureView.invalidate();
    }

    private void loadThumb() {
        if (this.thumb == null) {
            Bitmap bitmap = this.call.thumbs.get(this.participant.presentation ? this.participant.participant.presentationEndpoint : this.participant.participant.videoEndpoint);
            this.thumb = bitmap;
            this.textureView.setThumb(bitmap);
            if (this.thumb == null) {
                long peerId = MessageObject.getPeerId(this.participant.participant.peer);
                if (this.participant.participant.self && this.participant.presentation) {
                    this.imageReceiver.setImageBitmap(new MotionBackgroundDrawable(-14602694, -13935795, -14395293, -14203560, true));
                } else if (peerId > 0) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerId));
                    ImageLocation imageLocation = ImageLocation.getForUser(user, 1);
                    int color = user != null ? AvatarDrawable.getColorForId(user.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(color, -16777216, 0.2f), ColorUtils.blendARGB(color, -16777216, 0.4f)});
                    this.imageReceiver.setImage(imageLocation, "50_50_b", gradientDrawable, null, user, 0);
                } else {
                    TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-peerId));
                    ImageLocation imageLocation2 = ImageLocation.getForChat(chat, 1);
                    int color2 = chat != null ? AvatarDrawable.getColorForId(chat.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                    GradientDrawable gradientDrawable2 = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(color2, -16777216, 0.2f), ColorUtils.blendARGB(color2, -16777216, 0.4f)});
                    this.imageReceiver.setImage(imageLocation2, "50_50_b", gradientDrawable2, null, chat, 0);
                }
            }
        }
    }

    public void updateInfo() {
        if (!this.attached) {
            return;
        }
        String name = null;
        long peerId = MessageObject.getPeerId(this.participant.participant.peer);
        if (DialogObject.isUserDialog(peerId)) {
            TLRPC.User currentUser = AccountInstance.getInstance(this.currentAccount).getMessagesController().getUser(Long.valueOf(peerId));
            name = UserObject.getUserName(currentUser);
        } else {
            TLRPC.Chat currentChat = AccountInstance.getInstance(this.currentAccount).getMessagesController().getChat(Long.valueOf(-peerId));
            if (currentChat != null) {
                name = currentChat.title;
            }
        }
        this.nameView.setText(name);
    }

    public boolean hasImage() {
        return this.textureView.stubVisibleProgress == 1.0f;
    }

    public void updatePosition(ViewGroup listView, ViewGroup tabletGridListView, RecyclerListView fullscreenListView, GroupCallRenderersContainer renderersContainer) {
        ViewGroup fromListView;
        if (this.showingAsScrimView || this.animateToScrimView || this.forceDetached) {
            return;
        }
        boolean useTablet = false;
        this.drawFirst = false;
        float progressToFullscreen = renderersContainer.progressToFullscreenMode;
        if (this.animateToFullscreen || this.showingInFullscreen) {
            GroupCallGridCell callUserCell = this.primaryView;
            if (callUserCell != null || this.tabletGridView != null) {
                GroupCallGridCell groupCallGridCell = this.tabletGridView;
                if (groupCallGridCell != null) {
                    callUserCell = groupCallGridCell;
                }
                ViewGroup fromListView2 = groupCallGridCell != null ? tabletGridListView : listView;
                float fromX = ((callUserCell.getX() + fromListView2.getX()) - getLeft()) - renderersContainer.getLeft();
                float fromY = (((callUserCell.getY() + AndroidUtilities.dp(2.0f)) + fromListView2.getY()) - getTop()) - renderersContainer.getTop();
                setTranslationX(((1.0f - progressToFullscreen) * fromX) + (0.0f * progressToFullscreen));
                setTranslationY(((1.0f - progressToFullscreen) * fromY) + (0.0f * progressToFullscreen));
            } else {
                setTranslationX(0.0f);
                setTranslationY(0.0f);
            }
            this.textureView.setRoundCorners(AndroidUtilities.dp(8.0f));
            GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = this.secondaryView;
            if (groupCallUserCell != null) {
                groupCallUserCell.setAlpha(progressToFullscreen);
            }
            if (!this.showingInFullscreen && this.primaryView == null && this.tabletGridView == null) {
                setAlpha(progressToFullscreen);
                return;
            } else if (!this.animateEnter) {
                setAlpha(1.0f);
                return;
            } else {
                return;
            }
        }
        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell2 = this.secondaryView;
        if (groupCallUserCell2 != null) {
            if (groupCallUserCell2.isRemoving(fullscreenListView)) {
                setAlpha(this.secondaryView.getAlpha());
            } else if (this.primaryView == null) {
                if (this.attached && !this.animateEnter) {
                    setAlpha(progressToFullscreen);
                }
                this.secondaryView.setAlpha(progressToFullscreen);
                progressToFullscreen = 1.0f;
            } else {
                this.secondaryView.setAlpha(1.0f);
                if (this.attached && !this.animateEnter) {
                    setAlpha(1.0f);
                }
            }
            setTranslationX((this.secondaryView.getX() + fullscreenListView.getX()) - getLeft());
            setTranslationY((((AndroidUtilities.dp(2.0f) * (1.0f - progressToFullscreen)) + this.secondaryView.getY()) + fullscreenListView.getY()) - getTop());
            this.textureView.setRoundCorners((AndroidUtilities.dp(13.0f) * progressToFullscreen) + (AndroidUtilities.dp(8.0f) * (1.0f - progressToFullscreen)));
            return;
        }
        GroupCallGridCell callUserCell2 = this.primaryView;
        if (callUserCell2 != null || this.tabletGridView != null) {
            GroupCallGridCell groupCallGridCell2 = this.tabletGridView;
            if (groupCallGridCell2 != null && callUserCell2 != null) {
                if (GroupCallActivity.isTabletMode && !this.parentContainer.inFullscreenMode) {
                    useTablet = true;
                }
                callUserCell2 = useTablet ? this.tabletGridView : this.primaryView;
                fromListView = useTablet ? tabletGridListView : listView;
            } else {
                if (groupCallGridCell2 != null) {
                    callUserCell2 = groupCallGridCell2;
                }
                fromListView = groupCallGridCell2 != null ? tabletGridListView : listView;
            }
            setTranslationX(((callUserCell2.getX() + fromListView.getX()) - getLeft()) - renderersContainer.getLeft());
            setTranslationY((((callUserCell2.getY() + AndroidUtilities.dp(2.0f)) + fromListView.getY()) - getTop()) - renderersContainer.getTop());
            this.textureView.setRoundCorners(AndroidUtilities.dp(8.0f));
            if (this.attached && !this.animateEnter) {
                if (!GroupCallActivity.isTabletMode) {
                    this.drawFirst = true;
                    setAlpha((1.0f - progressToFullscreen) * callUserCell2.getAlpha());
                } else if (this.primaryView != null && this.tabletGridView == null) {
                    setAlpha(callUserCell2.getAlpha() * progressToFullscreen);
                }
            }
        }
    }

    public boolean isAttached() {
        return this.attached;
    }

    public void release() {
        this.textureView.renderer.release();
        if (this.statusIcon != null) {
            this.activity.statusIconPool.add(this.statusIcon);
            this.statusIcon.setCallback(null);
            this.statusIcon.setImageView(null);
        }
        this.statusIcon = null;
    }

    public boolean isFullyVisible() {
        return !this.showingInFullscreen && !this.animateToFullscreen && this.attached && this.textureView.renderer.isFirstFrameRendered() && getAlpha() == 1.0f;
    }

    public boolean isVisible() {
        return !this.showingInFullscreen && !this.animateToFullscreen && this.attached && this.textureView.renderer.isFirstFrameRendered();
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        if (!this.invalidateFromChild) {
            this.textureView.invalidate();
        }
        GroupCallGridCell groupCallGridCell = this.primaryView;
        if (groupCallGridCell != null) {
            groupCallGridCell.invalidate();
            if (this.activity.getScrimView() == this.primaryView) {
                this.activity.getContainerView().invalidate();
            }
        }
        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = this.secondaryView;
        if (groupCallUserCell != null) {
            groupCallUserCell.invalidate();
            if (this.secondaryView.getParent() != null) {
                ((View) this.secondaryView.getParent()).invalidate();
            }
        }
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    public void forceDetach(boolean removeSink) {
        this.forceDetached = true;
        this.attached = false;
        this.parentContainer.detach(this);
        if (removeSink) {
            if (this.participant.participant.self) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setLocalSink(null, this.participant.presentation);
                }
            } else if (VoIPService.getSharedInstance() != null && !RTMPStreamPipOverlay.isVisible()) {
                VoIPService.getSharedInstance().removeRemoteSink(this.participant.participant, this.participant.presentation);
            }
        }
        saveThumb();
        ValueAnimator valueAnimator = this.noVideoStubAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.noVideoStubAnimator.cancel();
        }
        this.textureView.renderer.release();
    }

    public void saveThumb() {
        if (this.participant != null && this.textureView.renderer.getMeasuredHeight() != 0 && this.textureView.renderer.getMeasuredWidth() != 0) {
            getRenderBufferBitmap(new GlGenericDrawer.TextureCallback() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda8
                @Override // org.webrtc.GlGenericDrawer.TextureCallback
                public final void run(Bitmap bitmap, int i) {
                    GroupCallMiniTextureView.this.m3232x5d3c7eaf(bitmap, i);
                }
            });
        }
    }

    /* renamed from: lambda$saveThumb$5$org-telegram-ui-Components-voip-GroupCallMiniTextureView */
    public /* synthetic */ void m3232x5d3c7eaf(final Bitmap bitmap, int rotation1) {
        if (bitmap != null && bitmap.getPixel(0, 0) != 0) {
            Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(bitmap.getWidth(), bitmap.getHeight()) / 180));
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCallMiniTextureView.this.m3231xd04f6790(bitmap);
                }
            });
        }
    }

    /* renamed from: lambda$saveThumb$4$org-telegram-ui-Components-voip-GroupCallMiniTextureView */
    public /* synthetic */ void m3231xd04f6790(Bitmap bitmap) {
        this.call.thumbs.put(this.participant.presentation ? this.participant.participant.presentationEndpoint : this.participant.participant.videoEndpoint, bitmap);
    }

    public void setViews(GroupCallGridCell primaryView, GroupCallFullscreenAdapter.GroupCallUserCell secondaryView, GroupCallGridCell tabletGrid) {
        this.primaryView = primaryView;
        this.secondaryView = secondaryView;
        this.tabletGridView = tabletGrid;
    }

    public void setAmplitude(double value) {
        this.statusIcon.setAmplitude(value);
        this.noVideoStubLayout.setAmplitude(value);
    }

    public void setZoom(boolean inPinchToZoom, float pinchScale, float pinchCenterX, float pinchCenterY, float pinchTranslationX, float pinchTranslationY) {
        if (this.pinchScale != pinchScale || this.pinchCenterX != pinchCenterX || this.pinchCenterY != pinchCenterY || this.pinchTranslationX != pinchTranslationX || this.pinchTranslationY != pinchTranslationY) {
            this.inPinchToZoom = inPinchToZoom;
            this.pinchScale = pinchScale;
            this.pinchCenterX = pinchCenterX;
            this.pinchCenterY = pinchCenterY;
            this.pinchTranslationX = pinchTranslationX;
            this.pinchTranslationY = pinchTranslationY;
            this.textureView.invalidate();
        }
    }

    public void setSwipeToBack(boolean swipeToBack, float swipeToBackDy) {
        if (this.swipeToBack != swipeToBack || this.swipeToBackDy != swipeToBackDy) {
            this.swipeToBack = swipeToBack;
            this.swipeToBackDy = swipeToBackDy;
            this.textureView.invalidate();
            invalidate();
        }
    }

    public void runOnFrameRendered(Runnable runnable) {
        if (this.textureView.renderer.isFirstFrameRendered()) {
            runnable.run();
            return;
        }
        AndroidUtilities.runOnUIThread(runnable, 250L);
        this.onFirstFrameRunnables.add(runnable);
    }

    @Override // org.telegram.ui.Components.voip.GroupCallStatusIcon.Callback
    public void onStatusChanged() {
        invalidate();
        updateIconColor(true);
        if (this.noVideoStubLayout.getVisibility() != 0) {
            return;
        }
        this.noVideoStubLayout.updateMuteButtonState(true);
    }

    private void updateIconColor(boolean animated) {
        final int newColor;
        final int newSpeakingFrameColor;
        GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
        if (groupCallStatusIcon == null) {
            return;
        }
        if (groupCallStatusIcon.isMutedByMe()) {
            newSpeakingFrameColor = Theme.getColor(Theme.key_voipgroup_mutedByAdminIcon);
            newColor = newSpeakingFrameColor;
        } else if (this.statusIcon.isSpeaking()) {
            newSpeakingFrameColor = Theme.getColor(Theme.key_voipgroup_speakingText);
            newColor = newSpeakingFrameColor;
        } else {
            newSpeakingFrameColor = Theme.getColor(Theme.key_voipgroup_speakingText);
            newColor = -1;
        }
        if (this.animateToColor == newColor) {
            return;
        }
        ValueAnimator valueAnimator = this.colorAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.colorAnimator.cancel();
        }
        if (!animated) {
            Paint paint = this.speakingPaint;
            this.lastSpeakingFrameColor = newSpeakingFrameColor;
            paint.setColor(newSpeakingFrameColor);
            return;
        }
        final int colorFrom = this.lastIconColor;
        final int colorFromSpeaking = this.lastSpeakingFrameColor;
        this.animateToColor = newColor;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.colorAnimator = ofFloat;
        final int i = newColor;
        final int i2 = newSpeakingFrameColor;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                GroupCallMiniTextureView.this.m3236x2cb02dc6(colorFrom, i, colorFromSpeaking, i2, valueAnimator2);
            }
        });
        this.colorAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView.7
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                int i3 = newColor;
                groupCallMiniTextureView.lastIconColor = i3;
                groupCallMiniTextureView.animateToColor = i3;
                GroupCallMiniTextureView.this.lastSpeakingFrameColor = newSpeakingFrameColor;
                GroupCallMiniTextureView.this.speakingPaint.setColor(GroupCallMiniTextureView.this.lastSpeakingFrameColor);
                if (GroupCallMiniTextureView.this.progressToSpeaking > 0.0f) {
                    GroupCallMiniTextureView.this.invalidate();
                }
            }
        });
        this.colorAnimator.start();
    }

    /* renamed from: lambda$updateIconColor$6$org-telegram-ui-Components-voip-GroupCallMiniTextureView */
    public /* synthetic */ void m3236x2cb02dc6(int colorFrom, int newColor, int colorFromSpeaking, int newSpeakingFrameColor, ValueAnimator valueAnimator) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.lastIconColor = ColorUtils.blendARGB(colorFrom, newColor, v);
        int blendARGB = ColorUtils.blendARGB(colorFromSpeaking, newSpeakingFrameColor, v);
        this.lastSpeakingFrameColor = blendARGB;
        this.speakingPaint.setColor(blendARGB);
        if (this.progressToSpeaking > 0.0f) {
            invalidate();
        }
    }

    public void runDelayedAnimations() {
        for (int i = 0; i < this.onFirstFrameRunnables.size(); i++) {
            this.onFirstFrameRunnables.get(i).run();
        }
        this.onFirstFrameRunnables.clear();
    }

    public void updateSize(int collapseSize) {
        int fullSize = this.parentContainer.getMeasuredWidth() - AndroidUtilities.dp(6.0f);
        if ((this.collapseSize != collapseSize && collapseSize > 0) || (this.fullSize != fullSize && fullSize > 0)) {
            if (collapseSize != 0) {
                this.collapseSize = collapseSize;
            }
            if (fullSize != 0) {
                this.fullSize = fullSize;
            }
            this.nameView.setFullLayoutAdditionalWidth(fullSize - collapseSize, 0);
        }
    }

    /* loaded from: classes5.dex */
    public class NoVideoStubLayout extends View {
        private static final int MUTED_BY_ADMIN = 2;
        private static final int MUTE_BUTTON_STATE_MUTE = 1;
        private static final int MUTE_BUTTON_STATE_UNMUTE = 0;
        float amplitude;
        float animateAmplitudeDiff;
        float animateToAmplitude;
        private GroupCallActivity.WeavingState currentState;
        float cx;
        float cy;
        private GroupCallActivity.WeavingState prevState;
        float speakingProgress;
        public ImageReceiver avatarImageReceiver = new ImageReceiver();
        public ImageReceiver backgroundImageReceiver = new ImageReceiver();
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        Paint paint = new Paint(1);
        Paint backgroundPaint = new Paint(1);
        float wavesEnter = 0.0f;
        private GroupCallActivity.WeavingState[] states = new GroupCallActivity.WeavingState[3];
        int muteButtonState = -1;
        float switchProgress = 1.0f;
        BlobDrawable tinyWaveDrawable = new BlobDrawable(9);
        BlobDrawable bigWaveDrawable = new BlobDrawable(12);

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public NoVideoStubLayout(Context context) {
            super(context);
            GroupCallMiniTextureView.this = r4;
            this.tinyWaveDrawable.minRadius = AndroidUtilities.dp(76.0f);
            this.tinyWaveDrawable.maxRadius = AndroidUtilities.dp(92.0f);
            this.tinyWaveDrawable.generateBlob();
            this.bigWaveDrawable.minRadius = AndroidUtilities.dp(80.0f);
            this.bigWaveDrawable.maxRadius = AndroidUtilities.dp(95.0f);
            this.bigWaveDrawable.generateBlob();
            this.paint.setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_voipgroup_listeningText), Theme.getColor(Theme.key_voipgroup_speakingText), this.speakingProgress));
            this.paint.setAlpha(102);
            this.backgroundPaint.setColor(ColorUtils.setAlphaComponent(-16777216, 127));
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            float size = AndroidUtilities.dp(157.0f);
            this.cx = getMeasuredWidth() >> 1;
            this.cy = (getMeasuredHeight() >> 1) + (GroupCallActivity.isLandscapeMode ? 0.0f : (-getMeasuredHeight()) * 0.12f);
            this.avatarImageReceiver.setRoundRadius((int) (size / 2.0f));
            this.avatarImageReceiver.setImageCoords(this.cx - (size / 2.0f), this.cy - (size / 2.0f), size, size);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float alpha;
            GroupCallActivity.WeavingState weavingState;
            GroupCallActivity.WeavingState weavingState2;
            super.onDraw(canvas);
            AndroidUtilities.rectTmp.set(GroupCallMiniTextureView.this.textureView.getX() + GroupCallMiniTextureView.this.textureView.currentClipHorizontal, GroupCallMiniTextureView.this.textureView.getY() + GroupCallMiniTextureView.this.textureView.currentClipVertical, (GroupCallMiniTextureView.this.textureView.getX() + GroupCallMiniTextureView.this.textureView.getMeasuredWidth()) - GroupCallMiniTextureView.this.textureView.currentClipHorizontal, GroupCallMiniTextureView.this.textureView.getY() + GroupCallMiniTextureView.this.textureView.getMeasuredHeight() + GroupCallMiniTextureView.this.textureView.currentClipVertical);
            this.backgroundImageReceiver.setImageCoords(AndroidUtilities.rectTmp.left, AndroidUtilities.rectTmp.top, AndroidUtilities.rectTmp.width(), AndroidUtilities.rectTmp.height());
            this.backgroundImageReceiver.setRoundRadius((int) GroupCallMiniTextureView.this.textureView.roundRadius);
            this.backgroundImageReceiver.draw(canvas);
            canvas.drawRoundRect(AndroidUtilities.rectTmp, GroupCallMiniTextureView.this.textureView.roundRadius, GroupCallMiniTextureView.this.textureView.roundRadius, this.backgroundPaint);
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
            float f5 = this.switchProgress;
            if (f5 != 1.0f) {
                if (this.prevState != null) {
                    this.switchProgress = f5 + 0.07272727f;
                }
                if (this.switchProgress >= 1.0f) {
                    this.switchProgress = 1.0f;
                    this.prevState = null;
                }
            }
            float scale = (this.amplitude * 0.8f) + 1.0f;
            canvas.save();
            canvas.scale(scale, scale, this.cx, this.cy);
            GroupCallActivity.WeavingState weavingState3 = this.currentState;
            if (weavingState3 != null) {
                weavingState3.update((int) (this.cy - AndroidUtilities.dp(100.0f)), (int) (this.cx - AndroidUtilities.dp(100.0f)), AndroidUtilities.dp(200.0f), 16L, this.amplitude);
            }
            this.bigWaveDrawable.update(this.amplitude, 1.0f);
            this.tinyWaveDrawable.update(this.amplitude, 1.0f);
            for (int i = 0; i < 2; i++) {
                if (i == 0 && (weavingState2 = this.prevState) != null) {
                    this.paint.setShader(weavingState2.shader);
                    alpha = 1.0f - this.switchProgress;
                } else {
                    if (i == 1 && (weavingState = this.currentState) != null) {
                        this.paint.setShader(weavingState.shader);
                        alpha = this.switchProgress;
                    }
                }
                this.paint.setAlpha((int) (76.0f * alpha));
                this.bigWaveDrawable.draw(this.cx, this.cy, canvas, this.paint);
                this.tinyWaveDrawable.draw(this.cx, this.cy, canvas, this.paint);
            }
            canvas.restore();
            float scale2 = (this.amplitude * 0.2f) + 1.0f;
            canvas.save();
            canvas.scale(scale2, scale2, this.cx, this.cy);
            this.avatarImageReceiver.draw(canvas);
            canvas.restore();
            invalidate();
        }

        public void updateMuteButtonState(boolean animated) {
            int newButtonState;
            if (!GroupCallMiniTextureView.this.statusIcon.isMutedByMe() && !GroupCallMiniTextureView.this.statusIcon.isMutedByAdmin()) {
                if (GroupCallMiniTextureView.this.statusIcon.isSpeaking()) {
                    newButtonState = 1;
                } else {
                    newButtonState = 0;
                }
            } else {
                newButtonState = 2;
            }
            if (newButtonState == this.muteButtonState) {
                return;
            }
            this.muteButtonState = newButtonState;
            GroupCallActivity.WeavingState[] weavingStateArr = this.states;
            if (weavingStateArr[newButtonState] == null) {
                weavingStateArr[newButtonState] = new GroupCallActivity.WeavingState(this.muteButtonState);
                int i = this.muteButtonState;
                if (i == 2) {
                    this.states[i].shader = new LinearGradient(0.0f, 400.0f, 400.0f, 0.0f, new int[]{Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient), Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient3), Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient2)}, (float[]) null, Shader.TileMode.CLAMP);
                } else if (i == 1) {
                    this.states[i].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor(Theme.key_voipgroup_muteButton), Theme.getColor(Theme.key_voipgroup_muteButton3)}, (float[]) null, Shader.TileMode.CLAMP);
                } else {
                    this.states[i].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor(Theme.key_voipgroup_unmuteButton2), Theme.getColor(Theme.key_voipgroup_unmuteButton)}, (float[]) null, Shader.TileMode.CLAMP);
                }
            }
            GroupCallActivity.WeavingState[] weavingStateArr2 = this.states;
            int i2 = this.muteButtonState;
            GroupCallActivity.WeavingState weavingState = weavingStateArr2[i2];
            GroupCallActivity.WeavingState weavingState2 = this.currentState;
            if (weavingState != weavingState2) {
                this.prevState = weavingState2;
                this.currentState = weavingStateArr2[i2];
                if (weavingState2 == null || !animated) {
                    this.switchProgress = 1.0f;
                    this.prevState = null;
                } else {
                    this.switchProgress = 0.0f;
                }
            }
            invalidate();
        }

        public void setAmplitude(double value) {
            float amplitude = ((float) value) / 80.0f;
            if (amplitude > 1.0f) {
                amplitude = 1.0f;
            } else if (amplitude < 0.0f) {
                amplitude = 0.0f;
            }
            this.animateToAmplitude = amplitude;
            this.animateAmplitudeDiff = (amplitude - this.amplitude) / 200.0f;
        }

        @Override // android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.avatarImageReceiver.onAttachedToWindow();
            this.backgroundImageReceiver.onAttachedToWindow();
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.avatarImageReceiver.onDetachedFromWindow();
            this.backgroundImageReceiver.onDetachedFromWindow();
        }
    }

    public String getName() {
        long peerId = MessageObject.getPeerId(this.participant.participant.peer);
        if (DialogObject.isUserDialog(peerId)) {
            TLRPC.User currentUser = AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController().getUser(Long.valueOf(peerId));
            return UserObject.getUserName(currentUser);
        }
        TLRPC.Chat currentChat = AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController().getChat(Long.valueOf(-peerId));
        return currentChat.title;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    public void startFlipAnimation() {
        if (this.flipAnimator != null) {
            return;
        }
        this.flipHalfReached = false;
        ImageView imageView = this.blurredFlippingStub;
        if (imageView == null) {
            this.blurredFlippingStub = new ImageView(getContext());
        } else {
            imageView.animate().cancel();
        }
        if (this.textureView.renderer.isFirstFrameRendered()) {
            Bitmap bitmap = this.textureView.blurRenderer.getBitmap(100, 100);
            if (bitmap != null) {
                Utilities.blurBitmap(bitmap, 3, 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                Drawable drawable = new BitmapDrawable(bitmap);
                this.blurredFlippingStub.setBackground(drawable);
            }
            this.blurredFlippingStub.setAlpha(0.0f);
        } else {
            this.blurredFlippingStub.setAlpha(1.0f);
        }
        if (this.blurredFlippingStub.getParent() == null) {
            this.textureView.addView(this.blurredFlippingStub);
        }
        ((FrameLayout.LayoutParams) this.blurredFlippingStub.getLayoutParams()).gravity = 17;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.flipAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GroupCallMiniTextureView.this.m3233xdd41bc97(valueAnimator);
            }
        });
        this.flipAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallMiniTextureView.8
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                GroupCallMiniTextureView.this.flipAnimator = null;
                GroupCallMiniTextureView.this.textureView.setRotationY(0.0f);
                if (!GroupCallMiniTextureView.this.flipHalfReached) {
                    GroupCallMiniTextureView.this.textureView.renderer.clearImage();
                }
            }
        });
        this.flipAnimator.setDuration(400L);
        this.flipAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.flipAnimator.start();
    }

    /* renamed from: lambda$startFlipAnimation$7$org-telegram-ui-Components-voip-GroupCallMiniTextureView */
    public /* synthetic */ void m3233xdd41bc97(ValueAnimator valueAnimator) {
        float rotation;
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        boolean halfReached = false;
        if (v < 0.5f) {
            rotation = v;
        } else {
            halfReached = true;
            rotation = v - 1.0f;
        }
        if (halfReached && !this.flipHalfReached) {
            this.blurredFlippingStub.setAlpha(1.0f);
            this.flipHalfReached = true;
            this.textureView.renderer.clearImage();
        }
        float rotation2 = rotation * 180.0f;
        this.blurredFlippingStub.setRotationY(rotation2);
        this.textureView.renderer.setRotationY(rotation2);
    }
}
