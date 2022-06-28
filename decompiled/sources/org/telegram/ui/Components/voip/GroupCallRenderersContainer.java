package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarsImageView;
import org.telegram.ui.Components.CrossOutDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.GroupCallActivity;
/* loaded from: classes5.dex */
public class GroupCallRenderersContainer extends FrameLayout {
    int animationIndex;
    private final ArrayList<GroupCallMiniTextureView> attachedRenderers;
    private final ImageView backButton;
    ChatObject.Call call;
    private boolean canZoomGesture;
    private boolean drawFirst;
    private boolean drawRenderesOnly;
    ValueAnimator fullscreenAnimator;
    private final RecyclerView fullscreenListView;
    public ChatObject.VideoParticipant fullscreenParticipant;
    public long fullscreenPeerId;
    public GroupCallMiniTextureView fullscreenTextureView;
    GroupCallActivity groupCallActivity;
    public boolean hasPinnedVideo;
    boolean hideUiRunnableIsScheduled;
    public boolean inFullscreenMode;
    public boolean inLayout;
    private boolean isInPinchToZoomTouchMode;
    private boolean isTablet;
    public long lastUpdateTime;
    long lastUpdateTooltipTime;
    private final RecyclerView listView;
    public int listWidth;
    boolean maybeSwipeToBackGesture;
    private boolean notDrawRenderes;
    private GroupCallMiniTextureView outFullscreenTextureView;
    private final ImageView pinButton;
    View pinContainer;
    CrossOutDrawable pinDrawable;
    TextView pinTextView;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartCenterX;
    private float pinchStartCenterY;
    private float pinchStartDistance;
    private float pinchTranslationX;
    private float pinchTranslationY;
    public ImageView pipView;
    private int pointerId1;
    private int pointerId2;
    public float progressToFullscreenMode;
    float progressToHideUi;
    public float progressToScrimView;
    ValueAnimator replaceFullscreenViewAnimator;
    Drawable rightShadowDrawable;
    private final View rightShadowView;
    private boolean showSpeakingMembersToast;
    private float showSpeakingMembersToastProgress;
    private final AvatarsImageView speakingMembersAvatars;
    private final TextView speakingMembersText;
    private final FrameLayout speakingMembersToast;
    private float speakingMembersToastFromLeft;
    private float speakingMembersToastFromRight;
    private float speakingMembersToastFromTextLeft;
    private long speakingToastPeerId;
    ValueAnimator swipeToBackAnimator;
    float swipeToBackDy;
    boolean swipeToBackGesture;
    public boolean swipedBack;
    boolean tapGesture;
    long tapTime;
    float tapX;
    float tapY;
    Drawable topShadowDrawable;
    private final View topShadowView;
    private final int touchSlop;
    TextView unpinTextView;
    Runnable updateTooltipRunnbale;
    ValueAnimator zoomBackAnimator;
    private boolean zoomStarted;
    private LongSparseIntArray attachedPeerIds = new LongSparseIntArray();
    private float speakingMembersToastChangeProgress = 1.0f;
    private boolean animateSpeakingOnNextDraw = true;
    boolean uiVisible = true;
    Runnable hideUiRunnable = new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.1
        @Override // java.lang.Runnable
        public void run() {
            if (!GroupCallRenderersContainer.this.canHideUI()) {
                AndroidUtilities.runOnUIThread(GroupCallRenderersContainer.this.hideUiRunnable, 3000L);
                return;
            }
            GroupCallRenderersContainer.this.hideUiRunnableIsScheduled = false;
            GroupCallRenderersContainer.this.setUiVisible(false);
        }
    };
    float pinchScale = 1.0f;
    public UndoView[] undoView = new UndoView[2];

    public GroupCallRenderersContainer(Context context, RecyclerView listView, RecyclerView fullscreenListView, ArrayList<GroupCallMiniTextureView> attachedRenderers, ChatObject.Call call, final GroupCallActivity groupCallActivity) {
        super(context);
        this.listView = listView;
        this.fullscreenListView = fullscreenListView;
        this.attachedRenderers = attachedRenderers;
        this.call = call;
        this.groupCallActivity = groupCallActivity;
        ImageView imageView = new ImageView(context) { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.2
            @Override // android.widget.ImageView, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), C.BUFFER_FLAG_ENCRYPTED));
            }
        };
        this.backButton = imageView;
        BackDrawable backDrawable = new BackDrawable(false);
        backDrawable.setColor(-1);
        imageView.setImageDrawable(backDrawable);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        imageView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 55)));
        View view = new View(context);
        this.topShadowView = view;
        Drawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 114)});
        this.topShadowDrawable = gradientDrawable;
        view.setBackground(gradientDrawable);
        addView(view, LayoutHelper.createFrame(-1, 120.0f));
        View view2 = new View(context);
        this.rightShadowView = view2;
        Drawable gradientDrawable2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 114)});
        this.rightShadowDrawable = gradientDrawable2;
        view2.setBackground(gradientDrawable2);
        view2.setVisibility((call == null || !isRtmpStream()) ? 8 : 0);
        addView(view2, LayoutHelper.createFrame(160, -1, 5));
        addView(imageView, LayoutHelper.createFrame(56, -1, 51));
        imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view3) {
                GroupCallRenderersContainer.this.m3239xbc07a82d(view3);
            }
        });
        ImageView imageView2 = new ImageView(context) { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.3
            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                GroupCallRenderersContainer.this.pinContainer.invalidate();
                GroupCallRenderersContainer.this.invalidate();
            }

            @Override // android.widget.ImageView, android.view.View
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), C.BUFFER_FLAG_ENCRYPTED));
            }
        };
        this.pinButton = imageView2;
        final Drawable pinRippleDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(20.0f), 0, ColorUtils.setAlphaComponent(-1, 100));
        View view3 = new View(context) { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.4
            @Override // android.view.View
            protected void drawableStateChanged() {
                super.drawableStateChanged();
                pinRippleDrawable.setState(getDrawableState());
            }

            @Override // android.view.View
            public boolean verifyDrawable(Drawable drawable) {
                return pinRippleDrawable == drawable || super.verifyDrawable(drawable);
            }

            @Override // android.view.View
            public void jumpDrawablesToCurrentState() {
                super.jumpDrawablesToCurrentState();
                pinRippleDrawable.jumpToCurrentState();
            }

            @Override // android.view.View
            protected void dispatchDraw(Canvas canvas) {
                float w = (GroupCallRenderersContainer.this.pinTextView.getMeasuredWidth() * (1.0f - GroupCallRenderersContainer.this.pinDrawable.getProgress())) + (GroupCallRenderersContainer.this.unpinTextView.getMeasuredWidth() * GroupCallRenderersContainer.this.pinDrawable.getProgress());
                canvas.save();
                pinRippleDrawable.setBounds(0, 0, AndroidUtilities.dp(50.0f) + ((int) w), getMeasuredHeight());
                pinRippleDrawable.draw(canvas);
                super.dispatchDraw(canvas);
            }
        };
        this.pinContainer = view3;
        view3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view4) {
                GroupCallRenderersContainer.this.m3240x767d48ae(view4);
            }
        });
        pinRippleDrawable.setCallback(this.pinContainer);
        addView(this.pinContainer);
        CrossOutDrawable crossOutDrawable = new CrossOutDrawable(context, R.drawable.msg_pin_filled, null);
        this.pinDrawable = crossOutDrawable;
        crossOutDrawable.setOffsets(-AndroidUtilities.dp(1.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(1.0f));
        imageView2.setImageDrawable(this.pinDrawable);
        imageView2.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        addView(imageView2, LayoutHelper.createFrame(56, -1, 51));
        TextView textView = new TextView(context);
        this.pinTextView = textView;
        textView.setTextColor(-1);
        this.pinTextView.setTextSize(1, 15.0f);
        this.pinTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.pinTextView.setText(LocaleController.getString("CallVideoPin", R.string.CallVideoPin));
        TextView textView2 = new TextView(context);
        this.unpinTextView = textView2;
        textView2.setTextColor(-1);
        this.unpinTextView.setTextSize(1, 15.0f);
        this.unpinTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
        this.unpinTextView.setText(LocaleController.getString("CallVideoUnpin", R.string.CallVideoUnpin));
        addView(this.pinTextView, LayoutHelper.createFrame(-2, -2, 51));
        addView(this.unpinTextView, LayoutHelper.createFrame(-2, -2, 51));
        ImageView imageView3 = new ImageView(context);
        this.pipView = imageView3;
        imageView3.setVisibility(4);
        this.pipView.setAlpha(0.0f);
        this.pipView.setImageResource(R.drawable.ic_goinline);
        this.pipView.setContentDescription(LocaleController.getString((int) R.string.AccDescrPipMode));
        int padding = AndroidUtilities.dp(4.0f);
        this.pipView.setPadding(padding, padding, padding, padding);
        this.pipView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 55)));
        this.pipView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view4) {
                GroupCallRenderersContainer.this.m3241x30f2e92f(groupCallActivity, view4);
            }
        });
        addView(this.pipView, LayoutHelper.createFrame(32, 32.0f, 53, 12.0f, 12.0f, 12.0f, 12.0f));
        final Drawable toastBackgroundDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_listViewBackground), 204));
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.5
            @Override // android.view.ViewGroup, android.view.View
            protected void dispatchDraw(Canvas canvas) {
                if (GroupCallRenderersContainer.this.speakingMembersToastChangeProgress != 1.0f) {
                    float progress = CubicBezierInterpolator.DEFAULT.getInterpolation(GroupCallRenderersContainer.this.speakingMembersToastChangeProgress);
                    float offset = (GroupCallRenderersContainer.this.speakingMembersToastFromLeft - getLeft()) * (1.0f - progress);
                    float offsetText = (GroupCallRenderersContainer.this.speakingMembersToastFromTextLeft - GroupCallRenderersContainer.this.speakingMembersText.getLeft()) * (1.0f - progress);
                    toastBackgroundDrawable.setBounds((int) offset, 0, getMeasuredWidth() + ((int) ((GroupCallRenderersContainer.this.speakingMembersToastFromRight - getRight()) * (1.0f - progress))), getMeasuredHeight());
                    GroupCallRenderersContainer.this.speakingMembersAvatars.setTranslationX(offset);
                    GroupCallRenderersContainer.this.speakingMembersText.setTranslationX(-offsetText);
                } else {
                    toastBackgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    GroupCallRenderersContainer.this.speakingMembersAvatars.setTranslationX(0.0f);
                    GroupCallRenderersContainer.this.speakingMembersText.setTranslationX(0.0f);
                }
                toastBackgroundDrawable.draw(canvas);
                super.dispatchDraw(canvas);
            }
        };
        this.speakingMembersToast = frameLayout;
        AvatarsImageView avatarsImageView = new AvatarsImageView(context, true);
        this.speakingMembersAvatars = avatarsImageView;
        avatarsImageView.setStyle(10);
        frameLayout.setClipChildren(false);
        frameLayout.setClipToPadding(false);
        frameLayout.addView(avatarsImageView, LayoutHelper.createFrame(100, 32.0f, 16, 0.0f, 0.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.speakingMembersText = textView3;
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(-1);
        textView3.setLines(1);
        textView3.setEllipsize(TextUtils.TruncateAt.END);
        frameLayout.addView(textView3, LayoutHelper.createFrame(-2, -2, 16));
        addView(frameLayout, LayoutHelper.createFrame(-2, 36.0f, 1, 0.0f, 0.0f, 0.0f, 0.0f));
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        this.touchSlop = configuration.getScaledTouchSlop();
        for (int a = 0; a < 2; a++) {
            this.undoView[a] = new UndoView(context) { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.6
                @Override // org.telegram.ui.Components.UndoView, android.view.View
                public void invalidate() {
                    super.invalidate();
                    GroupCallRenderersContainer.this.invalidate();
                }
            };
            this.undoView[a].setHideAnimationType(2);
            this.undoView[a].setAdditionalTranslationY(AndroidUtilities.dp(10.0f));
            addView(this.undoView[a], LayoutHelper.createFrame(-1, -2.0f, 80, 16.0f, 0.0f, 0.0f, 8.0f));
        }
        this.pinContainer.setVisibility(8);
        setIsTablet(GroupCallActivity.isTabletMode);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-voip-GroupCallRenderersContainer */
    public /* synthetic */ void m3239xbc07a82d(View view) {
        onBackPressed();
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-voip-GroupCallRenderersContainer */
    public /* synthetic */ void m3240x767d48ae(View view) {
        if (this.inFullscreenMode) {
            boolean z = !this.hasPinnedVideo;
            this.hasPinnedVideo = z;
            this.pinDrawable.setCrossOut(z, true);
            requestLayout();
        }
    }

    /* renamed from: lambda$new$2$org-telegram-ui-Components-voip-GroupCallRenderersContainer */
    public /* synthetic */ void m3241x30f2e92f(GroupCallActivity groupCallActivity, View v) {
        if (isRtmpStream()) {
            if (AndroidUtilities.checkInlinePermissions(groupCallActivity.getParentActivity())) {
                RTMPStreamPipOverlay.show();
                groupCallActivity.dismiss();
                return;
            }
            AlertsCreator.createDrawOverlayPermissionDialog(groupCallActivity.getParentActivity(), null).show();
        } else if (AndroidUtilities.checkInlinePermissions(groupCallActivity.getParentActivity())) {
            GroupCallPip.clearForce();
            groupCallActivity.dismiss();
        } else {
            AlertsCreator.createDrawOverlayGroupCallPermissionDialog(getContext()).show();
        }
    }

    private boolean isRtmpStream() {
        ChatObject.Call call = this.call;
        return call != null && call.call.rtmp_stream;
    }

    protected void onBackPressed() {
    }

    public void setIsTablet(boolean tablet) {
        if (this.isTablet != tablet) {
            this.isTablet = tablet;
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this.backButton.getLayoutParams();
            lp.gravity = tablet ? 85 : 51;
            lp.rightMargin = tablet ? AndroidUtilities.dp(328.0f) : 0;
            lp.bottomMargin = tablet ? -AndroidUtilities.dp(8.0f) : 0;
            if (this.isTablet) {
                this.backButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.msg_calls_minimize));
                return;
            }
            BackDrawable backDrawable = new BackDrawable(false);
            backDrawable.setColor(-1);
            this.backButton.setImageDrawable(backDrawable);
        }
    }

    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (this.drawFirst) {
            if (!(child instanceof GroupCallMiniTextureView) || !((GroupCallMiniTextureView) child).drawFirst) {
                return true;
            }
            float listTop = this.listView.getY() - getTop();
            float listBottom = (this.listView.getMeasuredHeight() + listTop) - this.listView.getTranslationY();
            canvas.save();
            canvas.clipRect(0.0f, listTop, getMeasuredWidth(), listBottom);
            boolean r = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
            return r;
        }
        UndoView[] undoViewArr = this.undoView;
        if (child == undoViewArr[0] || child == undoViewArr[1]) {
            return true;
        }
        if (child instanceof GroupCallMiniTextureView) {
            GroupCallMiniTextureView textureView = (GroupCallMiniTextureView) child;
            if (textureView == this.fullscreenTextureView || textureView == this.outFullscreenTextureView || this.notDrawRenderes || textureView.drawFirst) {
                return true;
            }
            if (textureView.primaryView != null) {
                float listTop2 = this.listView.getY() - getTop();
                float listBottom2 = (this.listView.getMeasuredHeight() + listTop2) - this.listView.getTranslationY();
                float progress = this.progressToFullscreenMode;
                if (textureView.secondaryView == null) {
                    progress = 0.0f;
                }
                canvas.save();
                canvas.clipRect(0.0f, (1.0f - progress) * listTop2, getMeasuredWidth(), ((1.0f - progress) * listBottom2) + (getMeasuredHeight() * progress));
                boolean r2 = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return r2;
            }
            boolean r3 = GroupCallActivity.isTabletMode;
            if (r3) {
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                boolean r4 = super.drawChild(canvas, child, drawingTime);
                canvas.restore();
                return r4;
            }
            boolean r5 = super.drawChild(canvas, child, drawingTime);
            return r5;
        } else if (!this.drawRenderesOnly) {
            return super.drawChild(canvas, child, drawingTime);
        } else {
            return true;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:148:0x047c  */
    /* JADX WARN: Removed duplicated region for block: B:149:0x0489  */
    /* JADX WARN: Removed duplicated region for block: B:152:0x04cf  */
    /* JADX WARN: Removed duplicated region for block: B:153:0x04d9  */
    /* JADX WARN: Removed duplicated region for block: B:156:0x04e4  */
    /* JADX WARN: Removed duplicated region for block: B:165:0x053f A[ORIG_RETURN, RETURN] */
    @Override // android.view.ViewGroup, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void dispatchDraw(android.graphics.Canvas r24) {
        /*
            Method dump skipped, instructions count: 1344
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.dispatchDraw(android.graphics.Canvas):void");
    }

    public void requestFullscreen(ChatObject.VideoParticipant videoParticipant) {
        GroupCallMiniTextureView newSmallTextureView;
        ChatObject.VideoParticipant videoParticipant2;
        if (videoParticipant != null || this.fullscreenParticipant != null) {
            if (videoParticipant != null && videoParticipant.equals(this.fullscreenParticipant)) {
                return;
            }
            long peerId = videoParticipant == null ? 0L : MessageObject.getPeerId(videoParticipant.participant.peer);
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView != null) {
                groupCallMiniTextureView.runDelayedAnimations();
            }
            ValueAnimator valueAnimator = this.replaceFullscreenViewAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            VoIPService service = VoIPService.getSharedInstance();
            boolean z = false;
            if (service != null && (videoParticipant2 = this.fullscreenParticipant) != null) {
                service.requestFullScreen(videoParticipant2.participant, false, this.fullscreenParticipant.presentation);
            }
            this.fullscreenParticipant = videoParticipant;
            if (service != null && videoParticipant != null) {
                service.requestFullScreen(videoParticipant.participant, true, this.fullscreenParticipant.presentation);
            }
            this.fullscreenPeerId = peerId;
            boolean oldInFullscreen = this.inFullscreenMode;
            this.lastUpdateTime = System.currentTimeMillis();
            float f = 1.0f;
            if (videoParticipant == null) {
                if (this.inFullscreenMode) {
                    ValueAnimator valueAnimator2 = this.fullscreenAnimator;
                    if (valueAnimator2 != null) {
                        valueAnimator2.cancel();
                    }
                    this.inFullscreenMode = false;
                    if ((this.fullscreenTextureView.primaryView != null || this.fullscreenTextureView.secondaryView != null || this.fullscreenTextureView.tabletGridView != null) && ChatObject.Call.videoIsActive(this.fullscreenTextureView.participant.participant, this.fullscreenTextureView.participant.presentation, this.call)) {
                        this.fullscreenTextureView.setShowingInFullscreen(false, true);
                    } else {
                        this.fullscreenTextureView.forceDetach(true);
                        if (this.fullscreenTextureView.primaryView != null) {
                            this.fullscreenTextureView.primaryView.setRenderer(null);
                        }
                        if (this.fullscreenTextureView.secondaryView != null) {
                            this.fullscreenTextureView.secondaryView.setRenderer(null);
                        }
                        if (this.fullscreenTextureView.tabletGridView != null) {
                            this.fullscreenTextureView.tabletGridView.setRenderer(null);
                        }
                        final GroupCallMiniTextureView removingMiniView = this.fullscreenTextureView;
                        removingMiniView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.7
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (removingMiniView.getParent() != null) {
                                    GroupCallRenderersContainer.this.removeView(removingMiniView);
                                    removingMiniView.release();
                                }
                            }
                        }).setDuration(350L).start();
                    }
                }
                this.backButton.setEnabled(false);
                this.hasPinnedVideo = false;
            } else {
                GroupCallMiniTextureView textureView = null;
                int i = 0;
                while (true) {
                    if (i >= this.attachedRenderers.size()) {
                        break;
                    } else if (!this.attachedRenderers.get(i).participant.equals(videoParticipant)) {
                        i++;
                    } else {
                        GroupCallMiniTextureView textureView2 = this.attachedRenderers.get(i);
                        textureView = textureView2;
                        break;
                    }
                }
                if (textureView != null) {
                    ValueAnimator valueAnimator3 = this.fullscreenAnimator;
                    if (valueAnimator3 != null) {
                        valueAnimator3.cancel();
                    }
                    if (!this.inFullscreenMode) {
                        this.inFullscreenMode = true;
                        clearCurrentFullscreenTextureView();
                        this.fullscreenTextureView = textureView;
                        textureView.setShowingInFullscreen(true, true);
                        invalidate();
                        this.pinDrawable.setCrossOut(this.hasPinnedVideo, false);
                    } else {
                        this.hasPinnedVideo = false;
                        this.pinDrawable.setCrossOut(false, false);
                        this.fullscreenTextureView.forceDetach(false);
                        textureView.forceDetach(false);
                        final GroupCallMiniTextureView removingMiniView2 = textureView;
                        if (!this.isTablet && (this.fullscreenTextureView.primaryView != null || this.fullscreenTextureView.secondaryView != null || this.fullscreenTextureView.tabletGridView != null)) {
                            newSmallTextureView = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                            newSmallTextureView.setViews(this.fullscreenTextureView.primaryView, this.fullscreenTextureView.secondaryView, this.fullscreenTextureView.tabletGridView);
                            newSmallTextureView.setFullscreenMode(this.inFullscreenMode, false);
                            newSmallTextureView.updateAttachState(false);
                            if (this.fullscreenTextureView.primaryView != null) {
                                this.fullscreenTextureView.primaryView.setRenderer(newSmallTextureView);
                            }
                            if (this.fullscreenTextureView.secondaryView != null) {
                                this.fullscreenTextureView.secondaryView.setRenderer(newSmallTextureView);
                            }
                            if (this.fullscreenTextureView.tabletGridView != null) {
                                this.fullscreenTextureView.tabletGridView.setRenderer(newSmallTextureView);
                            }
                        } else {
                            newSmallTextureView = null;
                        }
                        final GroupCallMiniTextureView newFullscreenTextureView = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                        newFullscreenTextureView.participant = textureView.participant;
                        newFullscreenTextureView.setViews(textureView.primaryView, textureView.secondaryView, textureView.tabletGridView);
                        newFullscreenTextureView.setFullscreenMode(this.inFullscreenMode, false);
                        newFullscreenTextureView.updateAttachState(false);
                        newFullscreenTextureView.textureView.renderer.setAlpha(1.0f);
                        newFullscreenTextureView.textureView.blurRenderer.setAlpha(1.0f);
                        if (textureView.primaryView != null) {
                            textureView.primaryView.setRenderer(newFullscreenTextureView);
                        }
                        if (textureView.secondaryView != null) {
                            textureView.secondaryView.setRenderer(newFullscreenTextureView);
                        }
                        if (textureView.tabletGridView != null) {
                            textureView.tabletGridView.setRenderer(newFullscreenTextureView);
                        }
                        newFullscreenTextureView.animateEnter = true;
                        newFullscreenTextureView.setAlpha(0.0f);
                        this.outFullscreenTextureView = this.fullscreenTextureView;
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(newFullscreenTextureView, View.ALPHA, 0.0f, 1.0f);
                        this.replaceFullscreenViewAnimator = ofFloat;
                        ofFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.8
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                GroupCallRenderersContainer.this.replaceFullscreenViewAnimator = null;
                                newFullscreenTextureView.animateEnter = false;
                                if (GroupCallRenderersContainer.this.outFullscreenTextureView != null) {
                                    if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                        GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                        groupCallRenderersContainer.removeView(groupCallRenderersContainer.outFullscreenTextureView);
                                        removingMiniView2.release();
                                    }
                                    GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                                }
                            }
                        });
                        if (newSmallTextureView != null) {
                            newSmallTextureView.setAlpha(0.0f);
                            newSmallTextureView.setScaleX(0.5f);
                            newSmallTextureView.setScaleY(0.5f);
                            newSmallTextureView.animateEnter = true;
                        }
                        final GroupCallMiniTextureView finalNewSmallTextureView = newSmallTextureView;
                        newFullscreenTextureView.runOnFrameRendered(new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda9
                            @Override // java.lang.Runnable
                            public final void run() {
                                GroupCallRenderersContainer.this.m3242xe34ac4fa(removingMiniView2, finalNewSmallTextureView);
                            }
                        });
                        clearCurrentFullscreenTextureView();
                        this.fullscreenTextureView = newFullscreenTextureView;
                        newFullscreenTextureView.setShowingInFullscreen(true, false);
                        update();
                    }
                } else if (!this.inFullscreenMode) {
                    this.inFullscreenMode = true;
                    clearCurrentFullscreenTextureView();
                    GroupCallMiniTextureView groupCallMiniTextureView2 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                    this.fullscreenTextureView = groupCallMiniTextureView2;
                    groupCallMiniTextureView2.participant = videoParticipant;
                    this.fullscreenTextureView.setFullscreenMode(this.inFullscreenMode, false);
                    this.fullscreenTextureView.setShowingInFullscreen(true, false);
                    this.fullscreenTextureView.setShowingInFullscreen(true, false);
                    ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.fullscreenTextureView, View.ALPHA, 0.0f, 1.0f);
                    this.replaceFullscreenViewAnimator = ofFloat2;
                    ofFloat2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.13
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            GroupCallRenderersContainer.this.replaceFullscreenViewAnimator = null;
                            GroupCallRenderersContainer.this.fullscreenTextureView.animateEnter = false;
                            if (GroupCallRenderersContainer.this.outFullscreenTextureView != null) {
                                if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                    GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                    groupCallRenderersContainer.removeView(groupCallRenderersContainer.outFullscreenTextureView);
                                    GroupCallRenderersContainer.this.outFullscreenTextureView.release();
                                }
                                GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                            }
                        }
                    });
                    this.replaceFullscreenViewAnimator.start();
                    invalidate();
                    this.pinDrawable.setCrossOut(this.hasPinnedVideo, false);
                } else {
                    if (this.fullscreenTextureView.primaryView == null) {
                        if (!((this.fullscreenTextureView.secondaryView != null) | (this.fullscreenTextureView.tabletGridView != null))) {
                            this.fullscreenTextureView.forceDetach(true);
                            final GroupCallMiniTextureView newFullscreenTextureView2 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                            newFullscreenTextureView2.participant = videoParticipant;
                            newFullscreenTextureView2.setFullscreenMode(this.inFullscreenMode, false);
                            newFullscreenTextureView2.setShowingInFullscreen(true, false);
                            newFullscreenTextureView2.animateEnter = true;
                            newFullscreenTextureView2.setAlpha(0.0f);
                            this.outFullscreenTextureView = this.fullscreenTextureView;
                            ValueAnimator ofFloat3 = ValueAnimator.ofFloat(0.0f, 1.0f);
                            this.replaceFullscreenViewAnimator = ofFloat3;
                            ofFloat3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda3
                                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                                public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                                    GroupCallRenderersContainer.this.m3244x583605fc(newFullscreenTextureView2, valueAnimator4);
                                }
                            });
                            this.replaceFullscreenViewAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.12
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animation) {
                                    GroupCallRenderersContainer.this.replaceFullscreenViewAnimator = null;
                                    newFullscreenTextureView2.animateEnter = false;
                                    if (GroupCallRenderersContainer.this.outFullscreenTextureView != null) {
                                        if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                            GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                            groupCallRenderersContainer.removeView(groupCallRenderersContainer.outFullscreenTextureView);
                                            GroupCallRenderersContainer.this.outFullscreenTextureView.release();
                                        }
                                        GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                                    }
                                }
                            });
                            this.replaceFullscreenViewAnimator.start();
                            clearCurrentFullscreenTextureView();
                            this.fullscreenTextureView = newFullscreenTextureView2;
                            newFullscreenTextureView2.setShowingInFullscreen(true, false);
                            this.fullscreenTextureView.updateAttachState(false);
                            update();
                        }
                    }
                    this.fullscreenTextureView.forceDetach(false);
                    final GroupCallMiniTextureView newSmallTextureView2 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                    newSmallTextureView2.setViews(this.fullscreenTextureView.primaryView, this.fullscreenTextureView.secondaryView, this.fullscreenTextureView.tabletGridView);
                    newSmallTextureView2.setFullscreenMode(this.inFullscreenMode, false);
                    newSmallTextureView2.updateAttachState(false);
                    if (this.fullscreenTextureView.primaryView != null) {
                        this.fullscreenTextureView.primaryView.setRenderer(newSmallTextureView2);
                    }
                    if (this.fullscreenTextureView.secondaryView != null) {
                        this.fullscreenTextureView.secondaryView.setRenderer(newSmallTextureView2);
                    }
                    if (this.fullscreenTextureView.tabletGridView != null) {
                        this.fullscreenTextureView.tabletGridView.setRenderer(newSmallTextureView2);
                    }
                    newSmallTextureView2.setAlpha(0.0f);
                    newSmallTextureView2.setScaleX(0.5f);
                    newSmallTextureView2.setScaleY(0.5f);
                    newSmallTextureView2.animateEnter = true;
                    newSmallTextureView2.runOnFrameRendered(new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda8
                        @Override // java.lang.Runnable
                        public final void run() {
                            GroupCallRenderersContainer.this.m3243x9dc0657b(newSmallTextureView2);
                        }
                    });
                    final GroupCallMiniTextureView newFullscreenTextureView22 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                    newFullscreenTextureView22.participant = videoParticipant;
                    newFullscreenTextureView22.setFullscreenMode(this.inFullscreenMode, false);
                    newFullscreenTextureView22.setShowingInFullscreen(true, false);
                    newFullscreenTextureView22.animateEnter = true;
                    newFullscreenTextureView22.setAlpha(0.0f);
                    this.outFullscreenTextureView = this.fullscreenTextureView;
                    ValueAnimator ofFloat32 = ValueAnimator.ofFloat(0.0f, 1.0f);
                    this.replaceFullscreenViewAnimator = ofFloat32;
                    ofFloat32.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda3
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                            GroupCallRenderersContainer.this.m3244x583605fc(newFullscreenTextureView22, valueAnimator4);
                        }
                    });
                    this.replaceFullscreenViewAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.12
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animation) {
                            GroupCallRenderersContainer.this.replaceFullscreenViewAnimator = null;
                            newFullscreenTextureView22.animateEnter = false;
                            if (GroupCallRenderersContainer.this.outFullscreenTextureView != null) {
                                if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                    GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                    groupCallRenderersContainer.removeView(groupCallRenderersContainer.outFullscreenTextureView);
                                    GroupCallRenderersContainer.this.outFullscreenTextureView.release();
                                }
                                GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                            }
                        }
                    });
                    this.replaceFullscreenViewAnimator.start();
                    clearCurrentFullscreenTextureView();
                    this.fullscreenTextureView = newFullscreenTextureView22;
                    newFullscreenTextureView22.setShowingInFullscreen(true, false);
                    this.fullscreenTextureView.updateAttachState(false);
                    update();
                }
                this.backButton.setEnabled(true);
            }
            boolean z2 = this.inFullscreenMode;
            if (oldInFullscreen != z2) {
                if (z2) {
                    this.backButton.setVisibility(0);
                    this.pinButton.setVisibility(0);
                    this.unpinTextView.setVisibility(0);
                    this.pinContainer.setVisibility(0);
                } else {
                    setUiVisible(true);
                    if (this.hideUiRunnableIsScheduled) {
                        this.hideUiRunnableIsScheduled = false;
                        AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
                    }
                }
                onFullScreenModeChanged(true);
                float[] fArr = new float[2];
                fArr[0] = this.progressToFullscreenMode;
                if (!this.inFullscreenMode) {
                    f = 0.0f;
                }
                fArr[1] = f;
                ValueAnimator ofFloat4 = ValueAnimator.ofFloat(fArr);
                this.fullscreenAnimator = ofFloat4;
                ofFloat4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda1
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                        GroupCallRenderersContainer.this.m3245x12aba67d(valueAnimator4);
                    }
                });
                final GroupCallMiniTextureView textureViewFinal = this.fullscreenTextureView;
                textureViewFinal.animateToFullscreen = true;
                final int currentAccount = this.groupCallActivity.getCurrentAccount();
                this.swipedBack = this.swipeToBackGesture;
                this.animationIndex = NotificationCenter.getInstance(currentAccount).setAnimationInProgress(this.animationIndex, null);
                this.fullscreenAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.14
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        NotificationCenter.getInstance(currentAccount).onAnimationFinish(GroupCallRenderersContainer.this.animationIndex);
                        GroupCallRenderersContainer.this.fullscreenAnimator = null;
                        textureViewFinal.animateToFullscreen = false;
                        if (!GroupCallRenderersContainer.this.inFullscreenMode) {
                            GroupCallRenderersContainer.this.clearCurrentFullscreenTextureView();
                            GroupCallRenderersContainer.this.fullscreenTextureView = null;
                            GroupCallRenderersContainer.this.fullscreenPeerId = 0L;
                        }
                        GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                        groupCallRenderersContainer.progressToFullscreenMode = groupCallRenderersContainer.inFullscreenMode ? 1.0f : 0.0f;
                        GroupCallRenderersContainer.this.update();
                        GroupCallRenderersContainer.this.onFullScreenModeChanged(false);
                        if (!GroupCallRenderersContainer.this.inFullscreenMode) {
                            GroupCallRenderersContainer.this.backButton.setVisibility(8);
                            GroupCallRenderersContainer.this.pinButton.setVisibility(8);
                            GroupCallRenderersContainer.this.unpinTextView.setVisibility(8);
                            GroupCallRenderersContainer.this.pinContainer.setVisibility(8);
                        }
                    }
                });
                this.fullscreenAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.fullscreenAnimator.setDuration(350L);
                this.fullscreenTextureView.textureView.synchOrRunAnimation(this.fullscreenAnimator);
            }
            if (this.fullscreenParticipant == null) {
                z = true;
            }
            animateSwipeToBack(z);
        }
    }

    /* renamed from: lambda$requestFullscreen$3$org-telegram-ui-Components-voip-GroupCallRenderersContainer */
    public /* synthetic */ void m3242xe34ac4fa(final GroupCallMiniTextureView removingMiniView, final GroupCallMiniTextureView finalNewSmallTextureView) {
        ValueAnimator valueAnimator = this.replaceFullscreenViewAnimator;
        if (valueAnimator != null) {
            valueAnimator.start();
        }
        removingMiniView.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.9
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                if (removingMiniView.getParent() != null) {
                    GroupCallRenderersContainer.this.removeView(removingMiniView);
                    removingMiniView.release();
                }
            }
        }).setDuration(100L).start();
        if (finalNewSmallTextureView != null) {
            finalNewSmallTextureView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(100L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.10
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    finalNewSmallTextureView.animateEnter = false;
                }
            }).start();
        }
    }

    /* renamed from: lambda$requestFullscreen$4$org-telegram-ui-Components-voip-GroupCallRenderersContainer */
    public /* synthetic */ void m3243x9dc0657b(final GroupCallMiniTextureView newSmallTextureView) {
        newSmallTextureView.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.11
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                newSmallTextureView.animateEnter = false;
            }
        }).setDuration(150L).start();
    }

    /* renamed from: lambda$requestFullscreen$5$org-telegram-ui-Components-voip-GroupCallRenderersContainer */
    public /* synthetic */ void m3244x583605fc(GroupCallMiniTextureView newFullscreenTextureView, ValueAnimator valueAnimator) {
        newFullscreenTextureView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        invalidate();
    }

    /* renamed from: lambda$requestFullscreen$6$org-telegram-ui-Components-voip-GroupCallRenderersContainer */
    public /* synthetic */ void m3245x12aba67d(ValueAnimator valueAnimator) {
        this.progressToFullscreenMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.groupCallActivity.getMenuItemsContainer().setAlpha(1.0f - this.progressToFullscreenMode);
        this.groupCallActivity.invalidateActionBarAlpha();
        this.groupCallActivity.invalidateScrollOffsetY();
        update();
    }

    public void clearCurrentFullscreenTextureView() {
        GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
        if (groupCallMiniTextureView != null) {
            groupCallMiniTextureView.setSwipeToBack(false, 0.0f);
            this.fullscreenTextureView.setZoom(false, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        }
    }

    public void update() {
        invalidate();
    }

    protected void onFullScreenModeChanged(boolean startAnimaion) {
    }

    public void setUiVisible(boolean uiVisible) {
        if (this.uiVisible != uiVisible) {
            this.uiVisible = uiVisible;
            onUiVisibilityChanged();
            if (uiVisible && this.inFullscreenMode) {
                if (!this.hideUiRunnableIsScheduled) {
                    this.hideUiRunnableIsScheduled = true;
                    AndroidUtilities.runOnUIThread(this.hideUiRunnable, 3000L);
                }
            } else {
                this.hideUiRunnableIsScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
            }
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView != null) {
                groupCallMiniTextureView.requestLayout();
            }
        }
    }

    protected void onUiVisibilityChanged() {
    }

    public boolean canHideUI() {
        return this.inFullscreenMode;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return onTouchEvent(ev);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if ((this.maybeSwipeToBackGesture || this.swipeToBackGesture) && (ev.getActionMasked() == 1 || ev.getActionMasked() == 3)) {
            this.maybeSwipeToBackGesture = false;
            if (this.swipeToBackGesture) {
                if (ev.getActionMasked() == 1 && Math.abs(this.swipeToBackDy) > AndroidUtilities.dp(120.0f)) {
                    this.groupCallActivity.fullscreenFor(null);
                } else {
                    animateSwipeToBack(false);
                }
            }
            invalidate();
        }
        if (!this.inFullscreenMode || ((!this.maybeSwipeToBackGesture && !this.swipeToBackGesture && !this.tapGesture && !this.canZoomGesture && !this.isInPinchToZoomTouchMode && !this.zoomStarted && ev.getActionMasked() != 0) || this.fullscreenTextureView == null)) {
            finishZoom();
            return false;
        }
        if (ev.getActionMasked() == 0) {
            this.maybeSwipeToBackGesture = false;
            this.swipeToBackGesture = false;
            this.canZoomGesture = false;
            this.isInPinchToZoomTouchMode = false;
            this.zoomStarted = false;
        }
        if (ev.getActionMasked() == 0 && this.swipeToBackAnimator != null) {
            this.maybeSwipeToBackGesture = false;
            this.swipeToBackGesture = true;
            this.tapY = ev.getY() - this.swipeToBackDy;
            this.swipeToBackAnimator.removeAllListeners();
            this.swipeToBackAnimator.cancel();
            this.swipeToBackAnimator = null;
        } else if (this.swipeToBackAnimator != null) {
            finishZoom();
            return false;
        }
        if (this.fullscreenTextureView.isInsideStopScreenButton(ev.getX(), ev.getY())) {
            return false;
        }
        if (ev.getActionMasked() == 0 && !this.swipeToBackGesture) {
            AndroidUtilities.rectTmp.set(0.0f, ActionBar.getCurrentActionBarHeight(), this.fullscreenTextureView.getMeasuredWidth() + ((!GroupCallActivity.isLandscapeMode || !this.uiVisible) ? 0 : -AndroidUtilities.dp(90.0f)), this.fullscreenTextureView.getMeasuredHeight() + ((GroupCallActivity.isLandscapeMode || !this.uiVisible) ? 0 : -AndroidUtilities.dp(90.0f)));
            if (AndroidUtilities.rectTmp.contains(ev.getX(), ev.getY())) {
                this.tapTime = System.currentTimeMillis();
                this.tapGesture = true;
                this.maybeSwipeToBackGesture = true;
                this.tapX = ev.getX();
                this.tapY = ev.getY();
            }
        } else if ((this.maybeSwipeToBackGesture || this.swipeToBackGesture || this.tapGesture) && ev.getActionMasked() == 2) {
            if (Math.abs(this.tapX - ev.getX()) > this.touchSlop || Math.abs(this.tapY - ev.getY()) > this.touchSlop) {
                this.tapGesture = false;
            }
            if (this.maybeSwipeToBackGesture && !this.zoomStarted && Math.abs(this.tapY - ev.getY()) > this.touchSlop * 2) {
                this.tapY = ev.getY();
                this.maybeSwipeToBackGesture = false;
                this.swipeToBackGesture = true;
            } else if (this.swipeToBackGesture) {
                this.swipeToBackDy = ev.getY() - this.tapY;
                invalidate();
            }
            if (this.maybeSwipeToBackGesture && Math.abs(this.tapX - ev.getX()) > this.touchSlop * 4) {
                this.maybeSwipeToBackGesture = false;
            }
        }
        if (this.tapGesture && ev.getActionMasked() == 1 && System.currentTimeMillis() - this.tapTime < 200) {
            boolean confirmAction = false;
            this.tapGesture = false;
            if (this.showSpeakingMembersToast) {
                AndroidUtilities.rectTmp.set(this.speakingMembersToast.getX(), this.speakingMembersToast.getY(), this.speakingMembersToast.getX() + this.speakingMembersToast.getWidth(), this.speakingMembersToast.getY() + this.speakingMembersToast.getHeight());
                if (this.call != null && AndroidUtilities.rectTmp.contains(ev.getX(), ev.getY())) {
                    boolean found = false;
                    for (int i = 0; i < this.call.visibleVideoParticipants.size(); i++) {
                        if (this.speakingToastPeerId == MessageObject.getPeerId(this.call.visibleVideoParticipants.get(i).participant.peer)) {
                            found = true;
                            confirmAction = true;
                            this.groupCallActivity.fullscreenFor(this.call.visibleVideoParticipants.get(i));
                        }
                    }
                    if (!found) {
                        TLRPC.TL_groupCallParticipant participant = this.call.participants.get(this.speakingToastPeerId);
                        this.groupCallActivity.fullscreenFor(new ChatObject.VideoParticipant(participant, false, false));
                        confirmAction = true;
                    }
                }
            }
            if (!confirmAction) {
                setUiVisible(!this.uiVisible);
            }
            this.swipeToBackDy = 0.0f;
            invalidate();
        }
        if (!this.fullscreenTextureView.hasVideo || this.swipeToBackGesture) {
            finishZoom();
            return this.tapGesture || this.swipeToBackGesture || this.maybeSwipeToBackGesture;
        }
        if (ev.getActionMasked() == 0 || ev.getActionMasked() == 5) {
            if (ev.getActionMasked() == 0) {
                View renderer = this.fullscreenTextureView.textureView.renderer;
                AndroidUtilities.rectTmp.set(renderer.getX(), renderer.getY(), renderer.getX() + renderer.getMeasuredWidth(), renderer.getY() + renderer.getMeasuredHeight());
                AndroidUtilities.rectTmp.inset(((renderer.getMeasuredHeight() * this.fullscreenTextureView.textureView.scaleTextureToFill) - renderer.getMeasuredHeight()) / 2.0f, ((renderer.getMeasuredWidth() * this.fullscreenTextureView.textureView.scaleTextureToFill) - renderer.getMeasuredWidth()) / 2.0f);
                if (!GroupCallActivity.isLandscapeMode) {
                    AndroidUtilities.rectTmp.top = Math.max(AndroidUtilities.rectTmp.top, ActionBar.getCurrentActionBarHeight());
                    AndroidUtilities.rectTmp.bottom = Math.min(AndroidUtilities.rectTmp.bottom, this.fullscreenTextureView.getMeasuredHeight() - AndroidUtilities.dp(90.0f));
                } else {
                    AndroidUtilities.rectTmp.top = Math.max(AndroidUtilities.rectTmp.top, ActionBar.getCurrentActionBarHeight());
                    AndroidUtilities.rectTmp.right = Math.min(AndroidUtilities.rectTmp.right, this.fullscreenTextureView.getMeasuredWidth() - AndroidUtilities.dp(90.0f));
                }
                boolean contains = AndroidUtilities.rectTmp.contains(ev.getX(), ev.getY());
                this.canZoomGesture = contains;
                if (!contains) {
                    finishZoom();
                    return this.maybeSwipeToBackGesture;
                }
            }
            if (!this.isInPinchToZoomTouchMode && ev.getPointerCount() == 2) {
                this.pinchStartDistance = (float) Math.hypot(ev.getX(1) - ev.getX(0), ev.getY(1) - ev.getY(0));
                float x = (ev.getX(0) + ev.getX(1)) / 2.0f;
                this.pinchCenterX = x;
                this.pinchStartCenterX = x;
                float y = (ev.getY(0) + ev.getY(1)) / 2.0f;
                this.pinchCenterY = y;
                this.pinchStartCenterY = y;
                this.pinchScale = 1.0f;
                this.pointerId1 = ev.getPointerId(0);
                this.pointerId2 = ev.getPointerId(1);
                this.isInPinchToZoomTouchMode = true;
            }
        } else if (ev.getActionMasked() == 2 && this.isInPinchToZoomTouchMode) {
            int index1 = -1;
            int index2 = -1;
            for (int i2 = 0; i2 < ev.getPointerCount(); i2++) {
                if (this.pointerId1 == ev.getPointerId(i2)) {
                    index1 = i2;
                }
                if (this.pointerId2 == ev.getPointerId(i2)) {
                    index2 = i2;
                }
            }
            if (index1 == -1 || index2 == -1) {
                getParent().requestDisallowInterceptTouchEvent(false);
                finishZoom();
                return this.maybeSwipeToBackGesture;
            }
            float hypot = ((float) Math.hypot(ev.getX(index2) - ev.getX(index1), ev.getY(index2) - ev.getY(index1))) / this.pinchStartDistance;
            this.pinchScale = hypot;
            if (hypot > 1.005f && !this.zoomStarted) {
                this.pinchStartDistance = (float) Math.hypot(ev.getX(index2) - ev.getX(index1), ev.getY(index2) - ev.getY(index1));
                float x2 = (ev.getX(index1) + ev.getX(index2)) / 2.0f;
                this.pinchCenterX = x2;
                this.pinchStartCenterX = x2;
                float y2 = (ev.getY(index1) + ev.getY(index2)) / 2.0f;
                this.pinchCenterY = y2;
                this.pinchStartCenterY = y2;
                this.pinchScale = 1.0f;
                this.pinchTranslationX = 0.0f;
                this.pinchTranslationY = 0.0f;
                getParent().requestDisallowInterceptTouchEvent(true);
                this.zoomStarted = true;
                this.isInPinchToZoomTouchMode = true;
            }
            float newPinchCenterX = (ev.getX(index1) + ev.getX(index2)) / 2.0f;
            float newPinchCenterY = (ev.getY(index1) + ev.getY(index2)) / 2.0f;
            float moveDx = this.pinchStartCenterX - newPinchCenterX;
            float moveDy = this.pinchStartCenterY - newPinchCenterY;
            float f = this.pinchScale;
            this.pinchTranslationX = (-moveDx) / f;
            this.pinchTranslationY = (-moveDy) / f;
            invalidate();
        } else {
            int index12 = ev.getActionMasked();
            if (index12 == 1 || ((ev.getActionMasked() == 6 && checkPointerIds(ev)) || ev.getActionMasked() == 3)) {
                getParent().requestDisallowInterceptTouchEvent(false);
                finishZoom();
            }
        }
        return this.canZoomGesture || this.tapGesture || this.maybeSwipeToBackGesture;
    }

    private void animateSwipeToBack(boolean aplay) {
        ValueAnimator valueAnimator;
        if (this.swipeToBackGesture) {
            this.swipeToBackGesture = false;
            float[] fArr = new float[2];
            float f = this.swipeToBackDy;
            if (aplay) {
                fArr[0] = f;
                fArr[1] = 0.0f;
                valueAnimator = ValueAnimator.ofFloat(fArr);
            } else {
                fArr[0] = f;
                fArr[1] = 0.0f;
                valueAnimator = ValueAnimator.ofFloat(fArr);
            }
            this.swipeToBackAnimator = valueAnimator;
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    GroupCallRenderersContainer.this.m3237x200965e9(valueAnimator2);
                }
            });
            this.swipeToBackAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.15
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    GroupCallRenderersContainer.this.swipeToBackAnimator = null;
                    GroupCallRenderersContainer.this.swipeToBackDy = 0.0f;
                    GroupCallRenderersContainer.this.invalidate();
                }
            });
            this.swipeToBackAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.swipeToBackAnimator.setDuration(aplay ? 350L : 200L);
            this.swipeToBackAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView != null) {
                groupCallMiniTextureView.textureView.synchOrRunAnimation(this.swipeToBackAnimator);
            } else {
                this.swipeToBackAnimator.start();
            }
            this.lastUpdateTime = System.currentTimeMillis();
        }
        this.maybeSwipeToBackGesture = false;
    }

    /* renamed from: lambda$animateSwipeToBack$7$org-telegram-ui-Components-voip-GroupCallRenderersContainer */
    public /* synthetic */ void m3237x200965e9(ValueAnimator valueAnimator) {
        this.swipeToBackDy = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    private void finishZoom() {
        if (this.zoomStarted) {
            this.zoomStarted = false;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.zoomBackAnimator = ofFloat;
            final float fromScale = this.pinchScale;
            final float fromTranslateX = this.pinchTranslationX;
            final float fromTranslateY = this.pinchTranslationY;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    GroupCallRenderersContainer.this.m3238x102cdcf5(fromScale, fromTranslateX, fromTranslateY, valueAnimator);
                }
            });
            this.zoomBackAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer.16
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    GroupCallRenderersContainer.this.zoomBackAnimator = null;
                    GroupCallRenderersContainer.this.pinchScale = 1.0f;
                    GroupCallRenderersContainer.this.pinchTranslationX = 0.0f;
                    GroupCallRenderersContainer.this.pinchTranslationY = 0.0f;
                    GroupCallRenderersContainer.this.invalidate();
                }
            });
            this.zoomBackAnimator.setDuration(350L);
            this.zoomBackAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.zoomBackAnimator.start();
            this.lastUpdateTime = System.currentTimeMillis();
        }
        this.canZoomGesture = false;
        this.isInPinchToZoomTouchMode = false;
    }

    /* renamed from: lambda$finishZoom$8$org-telegram-ui-Components-voip-GroupCallRenderersContainer */
    public /* synthetic */ void m3238x102cdcf5(float fromScale, float fromTranslateX, float fromTranslateY, ValueAnimator valueAnimator) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.pinchScale = (fromScale * v) + ((1.0f - v) * 1.0f);
        this.pinchTranslationX = fromTranslateX * v;
        this.pinchTranslationY = fromTranslateY * v;
        invalidate();
    }

    private boolean checkPointerIds(MotionEvent ev) {
        if (ev.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == ev.getPointerId(0) && this.pointerId2 == ev.getPointerId(1)) {
            return true;
        }
        return this.pointerId1 == ev.getPointerId(1) && this.pointerId2 == ev.getPointerId(0);
    }

    public void hideUi() {
        if (canHideUI()) {
            if (this.hideUiRunnableIsScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
                this.hideUiRunnableIsScheduled = false;
            }
            setUiVisible(false);
        }
    }

    public void delayHideUi() {
        if (this.hideUiRunnableIsScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
        }
        AndroidUtilities.runOnUIThread(this.hideUiRunnable, 3000L);
        this.hideUiRunnableIsScheduled = true;
    }

    public boolean isUiVisible() {
        return this.uiVisible;
    }

    public void setProgressToHideUi(float progressToHideUi) {
        if (this.progressToHideUi != progressToHideUi) {
            this.progressToHideUi = progressToHideUi;
            invalidate();
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView != null) {
                groupCallMiniTextureView.invalidate();
            }
        }
    }

    public void setAmplitude(TLRPC.TL_groupCallParticipant participant, float v) {
        for (int i = 0; i < this.attachedRenderers.size(); i++) {
            if (MessageObject.getPeerId(this.attachedRenderers.get(i).participant.participant.peer) == MessageObject.getPeerId(participant.peer)) {
                this.attachedRenderers.get(i).setAmplitude(v);
            }
        }
    }

    public boolean isAnimating() {
        return this.fullscreenAnimator != null;
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (GroupCallActivity.isTabletMode) {
            ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = AndroidUtilities.dp(328.0f);
        } else if (GroupCallActivity.isLandscapeMode) {
            ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = isRtmpStream() ? 0 : AndroidUtilities.dp(90.0f);
        } else {
            ((ViewGroup.MarginLayoutParams) this.topShadowView.getLayoutParams()).rightMargin = 0;
        }
        this.rightShadowView.setVisibility((!GroupCallActivity.isLandscapeMode || GroupCallActivity.isTabletMode) ? 8 : 0);
        this.pinContainer.getLayoutParams().height = AndroidUtilities.dp(40.0f);
        this.pinTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 0), heightMeasureSpec);
        this.unpinTextView.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), 0), heightMeasureSpec);
        this.pinContainer.getLayoutParams().width = AndroidUtilities.dp(46.0f) + (!this.hasPinnedVideo ? this.pinTextView : this.unpinTextView).getMeasuredWidth();
        ((ViewGroup.MarginLayoutParams) this.speakingMembersToast.getLayoutParams()).rightMargin = GroupCallActivity.isLandscapeMode ? AndroidUtilities.dp(45.0f) : 0;
        for (int a = 0; a < 2; a++) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) this.undoView[a].getLayoutParams();
            if (this.isTablet) {
                lp.rightMargin = AndroidUtilities.dp(344.0f);
            } else {
                lp.rightMargin = GroupCallActivity.isLandscapeMode ? AndroidUtilities.dp(180.0f) : 0;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean autoPinEnabled() {
        return !this.hasPinnedVideo && System.currentTimeMillis() - this.lastUpdateTime > AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS && !this.swipeToBackGesture && !this.isInPinchToZoomTouchMode;
    }

    public void setVisibleParticipant(boolean animated) {
        boolean show;
        boolean animated2;
        int leftMargin;
        if (!this.inFullscreenMode || this.isTablet || this.fullscreenParticipant == null || this.fullscreenAnimator != null || this.call == null) {
            if (this.showSpeakingMembersToast) {
                this.showSpeakingMembersToast = false;
                this.showSpeakingMembersToastProgress = 0.0f;
                return;
            }
            return;
        }
        int speakingIndex = 0;
        int currenAccount = this.groupCallActivity.getCurrentAccount();
        long j = 500;
        if (System.currentTimeMillis() - this.lastUpdateTooltipTime >= 500) {
            this.lastUpdateTooltipTime = System.currentTimeMillis();
            SpannableStringBuilder spannableStringBuilder = null;
            int i = 0;
            while (i < this.call.currentSpeakingPeers.size()) {
                long key = this.call.currentSpeakingPeers.keyAt(i);
                TLRPC.TL_groupCallParticipant participant = this.call.currentSpeakingPeers.get(key);
                if (!participant.self && !participant.muted_by_you) {
                    if (MessageObject.getPeerId(this.fullscreenParticipant.participant.peer) == MessageObject.getPeerId(participant.peer)) {
                        continue;
                    } else {
                        long peerId = MessageObject.getPeerId(participant.peer);
                        long diff = SystemClock.uptimeMillis() - participant.lastSpeakTime;
                        boolean newSpeaking = diff < j;
                        if (!newSpeaking) {
                            continue;
                        } else {
                            if (spannableStringBuilder == null) {
                                spannableStringBuilder = new SpannableStringBuilder();
                            }
                            if (speakingIndex == 0) {
                                this.speakingToastPeerId = MessageObject.getPeerId(participant.peer);
                            }
                            if (speakingIndex < 3) {
                                TLRPC.User user = peerId > 0 ? MessagesController.getInstance(currenAccount).getUser(Long.valueOf(peerId)) : null;
                                TLRPC.Chat chat = peerId <= 0 ? MessagesController.getInstance(currenAccount).getChat(Long.valueOf(peerId)) : null;
                                if (user != null || chat != null) {
                                    this.speakingMembersAvatars.setObject(speakingIndex, currenAccount, participant);
                                    if (speakingIndex != 0) {
                                        spannableStringBuilder.append((CharSequence) ", ");
                                    }
                                    if (user != null) {
                                        if (Build.VERSION.SDK_INT >= 21) {
                                            spannableStringBuilder.append(UserObject.getFirstName(user), new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM)), 0);
                                        } else {
                                            spannableStringBuilder.append((CharSequence) UserObject.getFirstName(user));
                                        }
                                    } else if (Build.VERSION.SDK_INT >= 21) {
                                        spannableStringBuilder.append(chat.title, new TypefaceSpan(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM)), 0);
                                    } else {
                                        spannableStringBuilder.append((CharSequence) chat.title);
                                    }
                                }
                            }
                            speakingIndex++;
                            if (speakingIndex == 3) {
                                break;
                            }
                        }
                    }
                }
                i++;
                j = 500;
            }
            if (speakingIndex == 0) {
                show = false;
            } else {
                show = true;
            }
            boolean z = this.showSpeakingMembersToast;
            if (!z && show) {
                animated2 = false;
            } else if (!show && z) {
                this.showSpeakingMembersToast = show;
                invalidate();
                return;
            } else {
                if (z && show) {
                    this.speakingMembersToastFromLeft = this.speakingMembersToast.getLeft();
                    this.speakingMembersToastFromRight = this.speakingMembersToast.getRight();
                    this.speakingMembersToastFromTextLeft = this.speakingMembersText.getLeft();
                    this.speakingMembersToastChangeProgress = 0.0f;
                }
                animated2 = animated;
            }
            if (!show) {
                this.showSpeakingMembersToast = show;
                invalidate();
                return;
            }
            String s = LocaleController.getPluralString("MembersAreSpeakingToast", speakingIndex);
            int replaceIndex = s.indexOf("un1");
            SpannableStringBuilder spannableStringBuilder1 = new SpannableStringBuilder(s);
            spannableStringBuilder1.replace(replaceIndex, replaceIndex + 3, (CharSequence) spannableStringBuilder);
            this.speakingMembersText.setText(spannableStringBuilder1);
            if (speakingIndex == 0) {
                leftMargin = 0;
            } else if (speakingIndex == 1) {
                leftMargin = AndroidUtilities.dp(40.0f);
            } else if (speakingIndex == 2) {
                leftMargin = AndroidUtilities.dp(64.0f);
            } else {
                leftMargin = AndroidUtilities.dp(88.0f);
            }
            ((FrameLayout.LayoutParams) this.speakingMembersText.getLayoutParams()).leftMargin = leftMargin;
            ((FrameLayout.LayoutParams) this.speakingMembersText.getLayoutParams()).rightMargin = AndroidUtilities.dp(16.0f);
            this.showSpeakingMembersToast = show;
            invalidate();
            while (speakingIndex < 3) {
                this.speakingMembersAvatars.setObject(speakingIndex, currenAccount, null);
                speakingIndex++;
            }
            this.speakingMembersAvatars.commitTransition(animated2);
        } else if (this.updateTooltipRunnbale == null) {
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.voip.GroupCallRenderersContainer$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCallRenderersContainer.this.m3246x9f901c79();
                }
            };
            this.updateTooltipRunnbale = runnable;
            AndroidUtilities.runOnUIThread(runnable, (System.currentTimeMillis() - this.lastUpdateTooltipTime) + 50);
        }
    }

    /* renamed from: lambda$setVisibleParticipant$9$org-telegram-ui-Components-voip-GroupCallRenderersContainer */
    public /* synthetic */ void m3246x9f901c79() {
        this.updateTooltipRunnbale = null;
        setVisibleParticipant(true);
    }

    public UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView old = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = old;
            old.hide(true, 2);
            removeView(this.undoView[0]);
            addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    public boolean isVisible(TLRPC.TL_groupCallParticipant participant) {
        long peerId = MessageObject.getPeerId(participant.peer);
        return this.attachedPeerIds.get(peerId) > 0;
    }

    public void attach(GroupCallMiniTextureView view) {
        this.attachedRenderers.add(view);
        long peerId = MessageObject.getPeerId(view.participant.participant.peer);
        LongSparseIntArray longSparseIntArray = this.attachedPeerIds;
        longSparseIntArray.put(peerId, longSparseIntArray.get(peerId, 0) + 1);
    }

    public void detach(GroupCallMiniTextureView view) {
        this.attachedRenderers.remove(view);
        long peerId = MessageObject.getPeerId(view.participant.participant.peer);
        LongSparseIntArray longSparseIntArray = this.attachedPeerIds;
        longSparseIntArray.put(peerId, longSparseIntArray.get(peerId, 0) - 1);
    }

    public void setGroupCall(ChatObject.Call call) {
        this.call = call;
    }
}
