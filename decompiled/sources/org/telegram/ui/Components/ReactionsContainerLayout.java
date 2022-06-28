package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumLockIconView;
import org.telegram.ui.Components.ReactionsContainerLayout;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class ReactionsContainerLayout extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final int ALPHA_DURATION = 150;
    private static final float CLIP_PROGRESS = 0.25f;
    private static final float SCALE_PROGRESS = 0.75f;
    private static final float SIDE_SCALE = 0.6f;
    public static final Property<ReactionsContainerLayout, Float> TRANSITION_PROGRESS_VALUE = new Property<ReactionsContainerLayout, Float>(Float.class, "transitionProgress") { // from class: org.telegram.ui.Components.ReactionsContainerLayout.1
        public Float get(ReactionsContainerLayout reactionsContainerLayout) {
            return Float.valueOf(reactionsContainerLayout.transitionProgress);
        }

        public void set(ReactionsContainerLayout object, Float value) {
            object.setTransitionProgress(value.floatValue());
        }
    };
    private final boolean animationEnabled;
    private float bigCircleRadius;
    ValueAnimator cancelPressedAnimation;
    private float cancelPressedProgress;
    private boolean clicked;
    private int currentAccount;
    private ReactionsContainerDelegate delegate;
    BaseFragment fragment;
    long lastReactionSentTime;
    private float leftAlpha;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView.Adapter listAdapter;
    private MessageObject messageObject;
    private float otherViewsScale;
    FrameLayout premiumLockContainer;
    private PremiumLockIconView premiumLockIconView;
    private float pressedProgress;
    private String pressedReaction;
    private int pressedReactionPosition;
    private float pressedViewScale;
    public final RecyclerListView recyclerListView;
    Theme.ResourcesProvider resourcesProvider;
    private float rightAlpha;
    private Drawable shadow;
    private float smallCircleRadius;
    private long waitingLoadingChatId;
    private Paint bgPaint = new Paint(1);
    private Paint leftShadowPaint = new Paint(1);
    private Paint rightShadowPaint = new Paint(1);
    private float transitionProgress = 1.0f;
    private RectF rect = new RectF();
    private Path mPath = new Path();
    private float radius = AndroidUtilities.dp(72.0f);
    private int bigCircleOffset = AndroidUtilities.dp(36.0f);
    private List<TLRPC.TL_availableReaction> reactionsList = new ArrayList(20);
    private List<TLRPC.TL_availableReaction> premiumLockedReactions = new ArrayList(10);
    private int[] location = new int[2];
    private android.graphics.Rect shadowPad = new android.graphics.Rect();
    private List<String> triggeredReactions = new ArrayList();
    HashSet<View> lastVisibleViews = new HashSet<>();
    HashSet<View> lastVisibleViewsTmp = new HashSet<>();

    /* loaded from: classes5.dex */
    public interface ReactionsContainerDelegate {
        void hideMenu();

        void onReactionClicked(View view, TLRPC.TL_availableReaction tL_availableReaction, boolean z);
    }

    public ReactionsContainerLayout(BaseFragment fragment, Context context, int currentAccount, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        boolean z = true;
        float dp = AndroidUtilities.dp(8.0f);
        this.bigCircleRadius = dp;
        this.smallCircleRadius = dp / 2.0f;
        this.resourcesProvider = resourcesProvider;
        this.currentAccount = currentAccount;
        this.fragment = fragment;
        this.animationEnabled = (!MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) || SharedConfig.getDevicePerformanceClass() == 0) ? false : z;
        this.shadow = ContextCompat.getDrawable(context, R.drawable.reactions_bubble_shadow).mutate();
        android.graphics.Rect rect = this.shadowPad;
        int dp2 = AndroidUtilities.dp(7.0f);
        rect.bottom = dp2;
        rect.right = dp2;
        rect.top = dp2;
        rect.left = dp2;
        this.shadow.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_messagePanelShadow), PorterDuff.Mode.MULTIPLY));
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.ReactionsContainerLayout.2
            @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View child, long drawingTime) {
                if (ReactionsContainerLayout.this.pressedReaction != null && (child instanceof ReactionHolderView) && ((ReactionHolderView) child).currentReaction.reaction.equals(ReactionsContainerLayout.this.pressedReaction)) {
                    return true;
                }
                return super.drawChild(canvas, child, drawingTime);
            }
        };
        this.recyclerListView = recyclerListView;
        this.linearLayoutManager = new LinearLayoutManager(context, 0, false);
        recyclerListView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.ReactionsContainerLayout.3
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int position = parent.getChildAdapterPosition(view);
                if (position == 0) {
                    outRect.left = AndroidUtilities.dp(6.0f);
                }
                outRect.right = AndroidUtilities.dp(4.0f);
                if (position == ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                    if (ReactionsContainerLayout.this.showUnlockPremiumButton()) {
                        outRect.right = AndroidUtilities.dp(2.0f);
                    } else {
                        outRect.right = AndroidUtilities.dp(6.0f);
                    }
                }
            }
        });
        recyclerListView.setLayoutManager(this.linearLayoutManager);
        recyclerListView.setOverScrollMode(2);
        AnonymousClass4 anonymousClass4 = new AnonymousClass4(context);
        this.listAdapter = anonymousClass4;
        recyclerListView.setAdapter(anonymousClass4);
        recyclerListView.addOnScrollListener(new LeftRightShadowsListener());
        recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.ReactionsContainerLayout.5
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.getChildCount() > 2) {
                    recyclerView.getLocationInWindow(ReactionsContainerLayout.this.location);
                    int rX = ReactionsContainerLayout.this.location[0];
                    View ch1 = recyclerView.getChildAt(0);
                    ch1.getLocationInWindow(ReactionsContainerLayout.this.location);
                    int ch1X = ReactionsContainerLayout.this.location[0];
                    int dX1 = ch1X - rX;
                    float s1 = ((1.0f - Math.min(1.0f, (-Math.min(dX1, 0.0f)) / ch1.getWidth())) * 0.39999998f) + 0.6f;
                    if (Float.isNaN(s1)) {
                        s1 = 1.0f;
                    }
                    ReactionsContainerLayout.this.setChildScale(ch1, s1);
                    View ch2 = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
                    ch2.getLocationInWindow(ReactionsContainerLayout.this.location);
                    int ch2X = ReactionsContainerLayout.this.location[0];
                    int dX2 = (recyclerView.getWidth() + rX) - (ch2.getWidth() + ch2X);
                    float s2 = ((1.0f - Math.min(1.0f, (-Math.min(dX2, 0.0f)) / ch2.getWidth())) * 0.39999998f) + 0.6f;
                    if (Float.isNaN(s2)) {
                        s2 = 1.0f;
                    }
                    ReactionsContainerLayout.this.setChildScale(ch2, s2);
                }
                for (int i = 1; i < ReactionsContainerLayout.this.recyclerListView.getChildCount() - 1; i++) {
                    View ch = ReactionsContainerLayout.this.recyclerListView.getChildAt(i);
                    ReactionsContainerLayout.this.setChildScale(ch, 1.0f);
                }
                ReactionsContainerLayout.this.invalidate();
            }
        });
        recyclerListView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.Components.ReactionsContainerLayout.6
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(android.graphics.Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int i = parent.getChildAdapterPosition(view);
                if (i == 0) {
                    outRect.left = AndroidUtilities.dp(8.0f);
                }
                if (i == ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                    outRect.right = AndroidUtilities.dp(8.0f);
                }
            }
        });
        recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ReactionsContainerLayout$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i) {
                ReactionsContainerLayout.this.m2947lambda$new$0$orgtelegramuiComponentsReactionsContainerLayout(view, i);
            }
        });
        recyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.ReactionsContainerLayout$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i) {
                return ReactionsContainerLayout.this.m2948lambda$new$1$orgtelegramuiComponentsReactionsContainerLayout(view, i);
            }
        });
        addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
        invalidateShaders();
        this.bgPaint.setColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, resourcesProvider));
    }

    /* renamed from: org.telegram.ui.Components.ReactionsContainerLayout$4 */
    /* loaded from: classes5.dex */
    public class AnonymousClass4 extends RecyclerView.Adapter {
        final /* synthetic */ Context val$context;

        AnonymousClass4(Context context) {
            ReactionsContainerLayout.this = this$0;
            this.val$context = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 1:
                    ReactionsContainerLayout.this.premiumLockContainer = new FrameLayout(this.val$context);
                    ReactionsContainerLayout.this.premiumLockIconView = new PremiumLockIconView(this.val$context, PremiumLockIconView.TYPE_REACTIONS);
                    ReactionsContainerLayout.this.premiumLockIconView.setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon), Theme.getColor(Theme.key_dialogBackground), 0.7f));
                    ReactionsContainerLayout.this.premiumLockIconView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
                    ReactionsContainerLayout.this.premiumLockIconView.setScaleX(0.0f);
                    ReactionsContainerLayout.this.premiumLockIconView.setScaleY(0.0f);
                    ReactionsContainerLayout.this.premiumLockIconView.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
                    ReactionsContainerLayout.this.premiumLockContainer.addView(ReactionsContainerLayout.this.premiumLockIconView, LayoutHelper.createFrame(26, 26, 17));
                    ReactionsContainerLayout.this.premiumLockIconView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ReactionsContainerLayout$4$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view2) {
                            ReactionsContainerLayout.AnonymousClass4.this.m2949x12d1183(view2);
                        }
                    });
                    view = ReactionsContainerLayout.this.premiumLockContainer;
                    break;
                default:
                    view = new ReactionHolderView(this.val$context);
                    break;
            }
            int size = (ReactionsContainerLayout.this.getLayoutParams().height - ReactionsContainerLayout.this.getPaddingTop()) - ReactionsContainerLayout.this.getPaddingBottom();
            view.setLayoutParams(new RecyclerView.LayoutParams(size - AndroidUtilities.dp(12.0f), size));
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-Components-ReactionsContainerLayout$4 */
        public /* synthetic */ void m2949x12d1183(View v) {
            int[] position = new int[2];
            v.getLocationOnScreen(position);
            ReactionsContainerLayout.this.showUnlockPremium(position[0] + (v.getMeasuredWidth() / 2.0f), position[1] + (v.getMeasuredHeight() / 2.0f));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == 0) {
                ReactionHolderView h = (ReactionHolderView) holder.itemView;
                h.setScaleX(1.0f);
                h.setScaleY(1.0f);
                h.setReaction((TLRPC.TL_availableReaction) ReactionsContainerLayout.this.reactionsList.get(position));
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ReactionsContainerLayout.this.reactionsList.size() + (ReactionsContainerLayout.this.showUnlockPremiumButton() ? 1 : 0);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            if (position >= 0 && position < ReactionsContainerLayout.this.reactionsList.size()) {
                return 0;
            }
            return 1;
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Components-ReactionsContainerLayout */
    public /* synthetic */ void m2947lambda$new$0$orgtelegramuiComponentsReactionsContainerLayout(View view, int position) {
        ReactionsContainerDelegate reactionsContainerDelegate = this.delegate;
        if (reactionsContainerDelegate != null && (view instanceof ReactionHolderView)) {
            ReactionHolderView reactionHolderView = (ReactionHolderView) view;
            reactionsContainerDelegate.onReactionClicked(this, reactionHolderView.currentReaction, false);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Components-ReactionsContainerLayout */
    public /* synthetic */ boolean m2948lambda$new$1$orgtelegramuiComponentsReactionsContainerLayout(View view, int position) {
        ReactionsContainerDelegate reactionsContainerDelegate = this.delegate;
        if (reactionsContainerDelegate != null && (view instanceof ReactionHolderView)) {
            ReactionHolderView reactionHolderView = (ReactionHolderView) view;
            reactionsContainerDelegate.onReactionClicked(this, reactionHolderView.currentReaction, true);
            return true;
        }
        return false;
    }

    public boolean showUnlockPremiumButton() {
        return !this.premiumLockedReactions.isEmpty() && !MessagesController.getInstance(this.currentAccount).premiumLocked;
    }

    public void showUnlockPremium(float x, float y) {
        PremiumFeatureBottomSheet bottomSheet = new PremiumFeatureBottomSheet(this.fragment, 4, true);
        bottomSheet.show();
    }

    public void setChildScale(View child, float scale) {
        if (child instanceof ReactionHolderView) {
            ((ReactionHolderView) child).sideScale = scale;
            return;
        }
        child.setScaleX(scale);
        child.setScaleY(scale);
    }

    public void setDelegate(ReactionsContainerDelegate delegate) {
        this.delegate = delegate;
    }

    private void setReactionsList(List<TLRPC.TL_availableReaction> reactionsList) {
        this.reactionsList.clear();
        this.reactionsList.addAll(reactionsList);
        checkPremiumReactions(this.reactionsList);
        int size = (getLayoutParams().height - getPaddingTop()) - getPaddingBottom();
        if (reactionsList.size() * size < AndroidUtilities.dp(200.0f)) {
            getLayoutParams().width = -2;
        }
        this.listAdapter.notifyDataSetChanged();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        float f;
        float f2;
        this.lastVisibleViewsTmp.clear();
        this.lastVisibleViewsTmp.addAll(this.lastVisibleViews);
        this.lastVisibleViews.clear();
        if (this.pressedReaction != null) {
            float f3 = this.pressedProgress;
            if (f3 != 1.0f) {
                float f4 = f3 + 0.010666667f;
                this.pressedProgress = f4;
                if (f4 >= 1.0f) {
                    this.pressedProgress = 1.0f;
                }
                invalidate();
            }
        }
        float cPr = (Math.max(0.25f, Math.min(this.transitionProgress, 1.0f)) - 0.25f) / 0.75f;
        float br = this.bigCircleRadius * cPr;
        float sr = this.smallCircleRadius * cPr;
        float f5 = this.pressedProgress;
        this.pressedViewScale = (f5 * 2.0f) + 1.0f;
        this.otherViewsScale = 1.0f - (f5 * 0.15f);
        int s = canvas.save();
        if (LocaleController.isRTL) {
            f2 = getWidth();
            f = 0.125f;
        } else {
            f2 = getWidth();
            f = 0.875f;
        }
        float pivotX = f2 * f;
        float f6 = this.transitionProgress;
        if (f6 <= 0.75f) {
            float sc = f6 / 0.75f;
            canvas.scale(sc, sc, pivotX, getHeight() / 2.0f);
        }
        float lt = 0.0f;
        float rt = 1.0f;
        if (LocaleController.isRTL) {
            rt = Math.max(0.25f, this.transitionProgress);
        } else {
            lt = 1.0f - Math.max(0.25f, this.transitionProgress);
        }
        this.rect.set(getPaddingLeft() + ((getWidth() - getPaddingRight()) * lt), getPaddingTop() + (this.recyclerListView.getMeasuredHeight() * (1.0f - this.otherViewsScale)), (getWidth() - getPaddingRight()) * rt, getHeight() - getPaddingBottom());
        this.radius = this.rect.height() / 2.0f;
        this.shadow.setBounds((int) ((getPaddingLeft() + (((getWidth() - getPaddingRight()) + this.shadowPad.right) * lt)) - this.shadowPad.left), getPaddingTop() - this.shadowPad.top, (int) (((getWidth() - getPaddingRight()) + this.shadowPad.right) * rt), (getHeight() - getPaddingBottom()) + this.shadowPad.bottom);
        this.shadow.draw(canvas);
        canvas.restoreToCount(s);
        int s2 = canvas.save();
        float f7 = this.transitionProgress;
        if (f7 <= 0.75f) {
            float sc2 = f7 / 0.75f;
            canvas.scale(sc2, sc2, pivotX, getHeight() / 2.0f);
        }
        RectF rectF = this.rect;
        float f8 = this.radius;
        canvas.drawRoundRect(rectF, f8, f8, this.bgPaint);
        canvas.restoreToCount(s2);
        this.mPath.rewind();
        Path path = this.mPath;
        RectF rectF2 = this.rect;
        float f9 = this.radius;
        path.addRoundRect(rectF2, f9, f9, Path.Direction.CW);
        int s3 = canvas.save();
        float f10 = this.transitionProgress;
        if (f10 <= 0.75f) {
            float sc3 = f10 / 0.75f;
            canvas.scale(sc3, sc3, pivotX, getHeight() / 2.0f);
        }
        if (this.transitionProgress != 0.0f && getAlpha() == 1.0f) {
            int delay = 0;
            for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
                View child = this.recyclerListView.getChildAt(i);
                if (child instanceof ReactionHolderView) {
                    ReactionHolderView view = (ReactionHolderView) this.recyclerListView.getChildAt(i);
                    checkPressedProgress(canvas, view);
                    if (view.backupImageView.getImageReceiver().getLottieAnimation() != null) {
                        if (view.getX() + (view.getMeasuredWidth() / 2.0f) > 0.0f && view.getX() + (view.getMeasuredWidth() / 2.0f) < this.recyclerListView.getWidth()) {
                            if (!this.lastVisibleViewsTmp.contains(view)) {
                                view.play(delay);
                                delay += 30;
                            }
                            this.lastVisibleViews.add(view);
                        } else if (!view.isEnter) {
                            view.resetAnimation();
                        }
                    }
                } else if (child == this.premiumLockContainer) {
                    if (child.getX() + (child.getMeasuredWidth() / 2.0f) > 0.0f && child.getX() + (child.getMeasuredWidth() / 2.0f) < this.recyclerListView.getWidth()) {
                        if (!this.lastVisibleViewsTmp.contains(child)) {
                            this.premiumLockIconView.play(delay);
                            delay += 30;
                        }
                        this.lastVisibleViews.add(child);
                    } else {
                        this.premiumLockIconView.resetAnimation();
                    }
                }
            }
        }
        canvas.clipPath(this.mPath);
        canvas.translate((LocaleController.isRTL ? -1 : 1) * getWidth() * (1.0f - this.transitionProgress), 0.0f);
        super.dispatchDraw(canvas);
        if (this.leftShadowPaint != null) {
            float p = Utilities.clamp(this.leftAlpha * this.transitionProgress, 1.0f, 0.0f);
            this.leftShadowPaint.setAlpha((int) (p * 255.0f));
            canvas.drawRect(this.rect, this.leftShadowPaint);
        }
        if (this.rightShadowPaint != null) {
            float p2 = Utilities.clamp(this.rightAlpha * this.transitionProgress, 1.0f, 0.0f);
            this.rightShadowPaint.setAlpha((int) (255.0f * p2));
            canvas.drawRect(this.rect, this.rightShadowPaint);
        }
        canvas.restoreToCount(s3);
        canvas.save();
        canvas.clipRect(0.0f, this.rect.bottom, getMeasuredWidth(), getMeasuredHeight());
        float cx = LocaleController.isRTL ? this.bigCircleOffset : getWidth() - this.bigCircleOffset;
        float cy = getHeight() - getPaddingBottom();
        int sPad = AndroidUtilities.dp(3.0f);
        this.shadow.setBounds((int) ((cx - br) - (sPad * cPr)), (int) ((cy - br) - (sPad * cPr)), (int) (cx + br + (sPad * cPr)), (int) (cy + br + (sPad * cPr)));
        this.shadow.draw(canvas);
        canvas.drawCircle(cx, cy, br, this.bgPaint);
        float cx2 = LocaleController.isRTL ? this.bigCircleOffset - this.bigCircleRadius : (getWidth() - this.bigCircleOffset) + this.bigCircleRadius;
        float cy2 = (getHeight() - this.smallCircleRadius) - sPad;
        int sPad2 = -AndroidUtilities.dp(1.0f);
        this.shadow.setBounds((int) ((cx2 - br) - (sPad2 * cPr)), (int) ((cy2 - br) - (sPad2 * cPr)), (int) (cx2 + br + (sPad2 * cPr)), (int) (cy2 + br + (sPad2 * cPr)));
        this.shadow.draw(canvas);
        canvas.drawCircle(cx2, cy2, sr, this.bgPaint);
        canvas.restore();
    }

    private void checkPressedProgressForOtherViews(View view) {
        int position = this.recyclerListView.getChildAdapterPosition(view);
        float translationX = ((view.getMeasuredWidth() * (this.pressedViewScale - 1.0f)) / 3.0f) - ((view.getMeasuredWidth() * (1.0f - this.otherViewsScale)) * (Math.abs(this.pressedReactionPosition - position) - 1));
        if (position < this.pressedReactionPosition) {
            view.setPivotX(0.0f);
            view.setTranslationX(-translationX);
        } else {
            view.setPivotX(view.getMeasuredWidth());
            view.setTranslationX(translationX);
        }
        view.setScaleX(this.otherViewsScale);
        view.setScaleY(this.otherViewsScale);
    }

    private void checkPressedProgress(Canvas canvas, ReactionHolderView view) {
        if (view.currentReaction.reaction.equals(this.pressedReaction)) {
            view.setPivotX(view.getMeasuredWidth() >> 1);
            view.setPivotY(view.backupImageView.getY() + view.backupImageView.getMeasuredHeight());
            view.setScaleX(this.pressedViewScale);
            view.setScaleY(this.pressedViewScale);
            if (!this.clicked) {
                if (this.cancelPressedAnimation == null) {
                    view.pressedBackupImageView.setVisibility(0);
                    view.pressedBackupImageView.setAlpha(1.0f);
                    if (view.pressedBackupImageView.getImageReceiver().hasBitmapImage()) {
                        view.backupImageView.setAlpha(0.0f);
                    }
                } else {
                    view.pressedBackupImageView.setAlpha(1.0f - this.cancelPressedProgress);
                    view.backupImageView.setAlpha(this.cancelPressedProgress);
                }
                if (this.pressedProgress == 1.0f) {
                    this.clicked = true;
                    if (System.currentTimeMillis() - this.lastReactionSentTime > 300) {
                        this.lastReactionSentTime = System.currentTimeMillis();
                        this.delegate.onReactionClicked(view, view.currentReaction, true);
                    }
                }
            }
            canvas.save();
            float x = this.recyclerListView.getX() + view.getX();
            float additionalWidth = ((view.getMeasuredWidth() * view.getScaleX()) - view.getMeasuredWidth()) / 2.0f;
            if (x - additionalWidth < 0.0f && view.getTranslationX() >= 0.0f) {
                view.setTranslationX(-(x - additionalWidth));
            } else if (view.getMeasuredWidth() + x + additionalWidth > getMeasuredWidth() && view.getTranslationX() <= 0.0f) {
                view.setTranslationX(((getMeasuredWidth() - x) - view.getMeasuredWidth()) - additionalWidth);
            } else {
                view.setTranslationX(0.0f);
            }
            canvas.translate(this.recyclerListView.getX() + view.getX(), this.recyclerListView.getY() + view.getY());
            canvas.scale(view.getScaleX(), view.getScaleY(), view.getPivotX(), view.getPivotY());
            view.draw(canvas);
            canvas.restore();
            return;
        }
        int position = this.recyclerListView.getChildAdapterPosition(view);
        float translationX = ((view.getMeasuredWidth() * (this.pressedViewScale - 1.0f)) / 3.0f) - ((view.getMeasuredWidth() * (1.0f - this.otherViewsScale)) * (Math.abs(this.pressedReactionPosition - position) - 1));
        if (position < this.pressedReactionPosition) {
            view.setPivotX(0.0f);
            view.setTranslationX(-translationX);
        } else {
            view.setPivotX(view.getMeasuredWidth());
            view.setTranslationX(translationX);
        }
        view.setPivotY(view.backupImageView.getY() + view.backupImageView.getMeasuredHeight());
        view.setScaleX(this.otherViewsScale);
        view.setScaleY(this.otherViewsScale);
        view.backupImageView.setScaleX(view.sideScale);
        view.backupImageView.setScaleY(view.sideScale);
        view.pressedBackupImageView.setVisibility(4);
        view.backupImageView.setAlpha(1.0f);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidateShaders();
    }

    private void invalidateShaders() {
        int dp = AndroidUtilities.dp(24.0f);
        float cy = getHeight() / 2.0f;
        int clr = Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground);
        this.leftShadowPaint.setShader(new LinearGradient(0.0f, cy, dp, cy, clr, 0, Shader.TileMode.CLAMP));
        this.rightShadowPaint.setShader(new LinearGradient(getWidth(), cy, getWidth() - dp, cy, clr, 0, Shader.TileMode.CLAMP));
        invalidate();
    }

    public void setTransitionProgress(float transitionProgress) {
        this.transitionProgress = transitionProgress;
        invalidate();
    }

    public void setMessage(MessageObject message, TLRPC.ChatFull chatFull) {
        List<TLRPC.TL_availableReaction> l;
        this.messageObject = message;
        TLRPC.ChatFull reactionsChat = chatFull;
        if (message.isForwardedChannelPost() && (reactionsChat = MessagesController.getInstance(this.currentAccount).getChatFull(-message.getFromChatId())) == null) {
            this.waitingLoadingChatId = -message.getFromChatId();
            MessagesController.getInstance(this.currentAccount).loadFullChat(-message.getFromChatId(), 0, true);
            setVisibility(4);
            return;
        }
        if (reactionsChat != null) {
            l = new ArrayList<>(reactionsChat.available_reactions.size());
            Iterator<String> it = reactionsChat.available_reactions.iterator();
            while (it.hasNext()) {
                String s = it.next();
                Iterator<TLRPC.TL_availableReaction> it2 = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList().iterator();
                while (true) {
                    if (it2.hasNext()) {
                        TLRPC.TL_availableReaction a = it2.next();
                        if (a.reaction.equals(s)) {
                            l.add(a);
                            break;
                        }
                    }
                }
            }
        } else {
            l = MediaDataController.getInstance(this.currentAccount).getEnabledReactionsList();
        }
        setReactionsList(l);
    }

    private void checkPremiumReactions(List<TLRPC.TL_availableReaction> reactions) {
        this.premiumLockedReactions.clear();
        if (UserConfig.getInstance(this.currentAccount).isPremium()) {
            return;
        }
        int i = 0;
        while (i < reactions.size()) {
            try {
                if (reactions.get(i).premium) {
                    this.premiumLockedReactions.add(reactions.remove(i));
                    i--;
                }
                i++;
            } catch (Exception e) {
                return;
            }
        }
    }

    public void startEnterAnimation() {
        setTransitionProgress(0.0f);
        setAlpha(1.0f);
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, TRANSITION_PROGRESS_VALUE, 0.0f, 1.0f).setDuration(400L);
        animator.setInterpolator(new OvershootInterpolator(1.004f));
        animator.start();
    }

    public int getTotalWidth() {
        return (AndroidUtilities.dp(36.0f) * this.reactionsList.size()) + AndroidUtilities.dp(16.0f);
    }

    public int getItemsCount() {
        return this.reactionsList.size();
    }

    /* loaded from: classes5.dex */
    public final class LeftRightShadowsListener extends RecyclerView.OnScrollListener {
        private ValueAnimator leftAnimator;
        private boolean leftVisible;
        private ValueAnimator rightAnimator;
        private boolean rightVisible;

        private LeftRightShadowsListener() {
            ReactionsContainerLayout.this = r1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            boolean r = false;
            boolean l = ReactionsContainerLayout.this.linearLayoutManager.findFirstVisibleItemPosition() != 0;
            float f = 1.0f;
            if (l != this.leftVisible) {
                ValueAnimator valueAnimator = this.leftAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.leftAnimator = startAnimator(ReactionsContainerLayout.this.leftAlpha, l ? 1.0f : 0.0f, new Consumer() { // from class: org.telegram.ui.Components.ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda1
                    @Override // androidx.core.util.Consumer
                    public final void accept(Object obj) {
                        ReactionsContainerLayout.LeftRightShadowsListener.this.m2950x9b48f4c4((Float) obj);
                    }
                }, new Runnable() { // from class: org.telegram.ui.Components.ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        ReactionsContainerLayout.LeftRightShadowsListener.this.m2951xb44a4663();
                    }
                });
                this.leftVisible = l;
            }
            if (ReactionsContainerLayout.this.linearLayoutManager.findLastVisibleItemPosition() != ReactionsContainerLayout.this.listAdapter.getItemCount() - 1) {
                r = true;
            }
            if (r != this.rightVisible) {
                ValueAnimator valueAnimator2 = this.rightAnimator;
                if (valueAnimator2 != null) {
                    valueAnimator2.cancel();
                }
                float f2 = ReactionsContainerLayout.this.rightAlpha;
                if (!r) {
                    f = 0.0f;
                }
                this.rightAnimator = startAnimator(f2, f, new Consumer() { // from class: org.telegram.ui.Components.ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda2
                    @Override // androidx.core.util.Consumer
                    public final void accept(Object obj) {
                        ReactionsContainerLayout.LeftRightShadowsListener.this.m2952xcd4b9802((Float) obj);
                    }
                }, new Runnable() { // from class: org.telegram.ui.Components.ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        ReactionsContainerLayout.LeftRightShadowsListener.this.m2953xe64ce9a1();
                    }
                });
                this.rightVisible = r;
            }
        }

        /* renamed from: lambda$onScrolled$0$org-telegram-ui-Components-ReactionsContainerLayout$LeftRightShadowsListener */
        public /* synthetic */ void m2950x9b48f4c4(Float aFloat) {
            ReactionsContainerLayout.this.leftShadowPaint.setAlpha((int) (ReactionsContainerLayout.this.leftAlpha = aFloat.floatValue() * 255.0f));
            ReactionsContainerLayout.this.invalidate();
        }

        /* renamed from: lambda$onScrolled$1$org-telegram-ui-Components-ReactionsContainerLayout$LeftRightShadowsListener */
        public /* synthetic */ void m2951xb44a4663() {
            this.leftAnimator = null;
        }

        /* renamed from: lambda$onScrolled$2$org-telegram-ui-Components-ReactionsContainerLayout$LeftRightShadowsListener */
        public /* synthetic */ void m2952xcd4b9802(Float aFloat) {
            ReactionsContainerLayout.this.rightShadowPaint.setAlpha((int) (ReactionsContainerLayout.this.rightAlpha = aFloat.floatValue() * 255.0f));
            ReactionsContainerLayout.this.invalidate();
        }

        /* renamed from: lambda$onScrolled$3$org-telegram-ui-Components-ReactionsContainerLayout$LeftRightShadowsListener */
        public /* synthetic */ void m2953xe64ce9a1() {
            this.rightAnimator = null;
        }

        private ValueAnimator startAnimator(float fromAlpha, float toAlpha, final Consumer<Float> callback, final Runnable onEnd) {
            ValueAnimator a = ValueAnimator.ofFloat(fromAlpha, toAlpha).setDuration(Math.abs(toAlpha - fromAlpha) * 150.0f);
            a.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ReactionsContainerLayout$LeftRightShadowsListener$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    Consumer.this.accept((Float) valueAnimator.getAnimatedValue());
                }
            });
            a.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ReactionsContainerLayout.LeftRightShadowsListener.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    onEnd.run();
                }
            });
            a.start();
            return a;
        }
    }

    /* loaded from: classes5.dex */
    public final class ReactionHolderView extends FrameLayout {
        public BackupImageView backupImageView;
        public TLRPC.TL_availableReaction currentReaction;
        private boolean isEnter;
        boolean pressed;
        public BackupImageView pressedBackupImageView;
        float pressedX;
        float pressedY;
        public float sideScale = 1.0f;
        Runnable playRunnable = new Runnable() { // from class: org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView.1
            @Override // java.lang.Runnable
            public void run() {
                if (ReactionHolderView.this.backupImageView.getImageReceiver().getLottieAnimation() != null && !ReactionHolderView.this.backupImageView.getImageReceiver().getLottieAnimation().isRunning() && !ReactionHolderView.this.backupImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                    ReactionHolderView.this.backupImageView.getImageReceiver().getLottieAnimation().start();
                }
            }
        };
        Runnable longPressRunnable = new Runnable() { // from class: org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView.4
            @Override // java.lang.Runnable
            public void run() {
                ReactionHolderView.this.performHapticFeedback(0);
                ReactionsContainerLayout.this.pressedReactionPosition = ReactionsContainerLayout.this.reactionsList.indexOf(ReactionHolderView.this.currentReaction);
                ReactionsContainerLayout.this.pressedReaction = ReactionHolderView.this.currentReaction.reaction;
                ReactionsContainerLayout.this.invalidate();
            }
        };

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            TLRPC.TL_availableReaction tL_availableReaction = this.currentReaction;
            if (tL_availableReaction != null) {
                info.setText(tL_availableReaction.reaction);
                info.setEnabled(true);
            }
        }

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        ReactionHolderView(Context context) {
            super(context);
            ReactionsContainerLayout.this = this$0;
            BackupImageView backupImageView = new BackupImageView(context) { // from class: org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView.2
                @Override // android.view.View
                public void invalidate() {
                    super.invalidate();
                    ReactionsContainerLayout.this.invalidate();
                }
            };
            this.backupImageView = backupImageView;
            backupImageView.getImageReceiver().setAutoRepeat(0);
            this.backupImageView.getImageReceiver().setAllowStartLottieAnimation(false);
            this.pressedBackupImageView = new BackupImageView(context) { // from class: org.telegram.ui.Components.ReactionsContainerLayout.ReactionHolderView.3
                @Override // android.view.View
                public void invalidate() {
                    super.invalidate();
                    ReactionsContainerLayout.this.invalidate();
                }
            };
            addView(this.backupImageView, LayoutHelper.createFrame(34, 34, 17));
            addView(this.pressedBackupImageView, LayoutHelper.createFrame(34, 34, 17));
        }

        public void setReaction(TLRPC.TL_availableReaction react) {
            TLRPC.TL_availableReaction tL_availableReaction = this.currentReaction;
            if (tL_availableReaction != null && tL_availableReaction.reaction.equals(react.reaction)) {
                return;
            }
            resetAnimation();
            this.currentReaction = react;
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(react.activate_animation, Theme.key_windowBackgroundGray, 1.0f);
            this.backupImageView.getImageReceiver().setImage(ImageLocation.getForDocument(this.currentReaction.appear_animation), "60_60_nolimit", null, null, svgThumb, 0L, "tgs", react, 0);
            this.pressedBackupImageView.getImageReceiver().setImage(ImageLocation.getForDocument(this.currentReaction.select_animation), "60_60_nolimit", null, null, svgThumb, 0L, "tgs", react, 0);
            setFocusable(true);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            resetAnimation();
        }

        public boolean play(int delay) {
            if (!ReactionsContainerLayout.this.animationEnabled) {
                resetAnimation();
                this.isEnter = true;
                return false;
            }
            AndroidUtilities.cancelRunOnUIThread(this.playRunnable);
            if (this.backupImageView.getImageReceiver().getLottieAnimation() != null && !this.backupImageView.getImageReceiver().getLottieAnimation().isGeneratingCache() && !this.isEnter) {
                this.isEnter = true;
                if (delay == 0) {
                    this.backupImageView.getImageReceiver().getLottieAnimation().stop();
                    this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                    this.playRunnable.run();
                } else {
                    this.backupImageView.getImageReceiver().getLottieAnimation().stop();
                    this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false);
                    AndroidUtilities.runOnUIThread(this.playRunnable, delay);
                }
                return true;
            }
            if (this.backupImageView.getImageReceiver().getLottieAnimation() != null && this.isEnter && !this.backupImageView.getImageReceiver().getLottieAnimation().isRunning() && !this.backupImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(this.backupImageView.getImageReceiver().getLottieAnimation().getFramesCount() - 1, false);
            }
            return false;
        }

        public void resetAnimation() {
            AndroidUtilities.cancelRunOnUIThread(this.playRunnable);
            if (this.backupImageView.getImageReceiver().getLottieAnimation() != null && !this.backupImageView.getImageReceiver().getLottieAnimation().isGeneratingCache()) {
                this.backupImageView.getImageReceiver().getLottieAnimation().stop();
                if (ReactionsContainerLayout.this.animationEnabled) {
                    this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false, true);
                } else {
                    this.backupImageView.getImageReceiver().getLottieAnimation().setCurrentFrame(this.backupImageView.getImageReceiver().getLottieAnimation().getFramesCount() - 1, false, true);
                }
            }
            this.isEnter = false;
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (ReactionsContainerLayout.this.cancelPressedAnimation != null) {
                return false;
            }
            if (event.getAction() == 0) {
                this.pressed = true;
                this.pressedX = event.getX();
                this.pressedY = event.getY();
                if (this.sideScale == 1.0f) {
                    AndroidUtilities.runOnUIThread(this.longPressRunnable, ViewConfiguration.getLongPressTimeout());
                }
            }
            float touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop() * 2.0f;
            boolean cancelByMove = event.getAction() == 2 && (Math.abs(this.pressedX - event.getX()) > touchSlop || Math.abs(this.pressedY - event.getY()) > touchSlop);
            if (cancelByMove || event.getAction() == 1 || event.getAction() == 3) {
                if (event.getAction() == 1 && this.pressed && ((ReactionsContainerLayout.this.pressedReaction == null || ReactionsContainerLayout.this.pressedProgress > 0.8f) && ReactionsContainerLayout.this.delegate != null)) {
                    ReactionsContainerLayout.this.clicked = true;
                    if (System.currentTimeMillis() - ReactionsContainerLayout.this.lastReactionSentTime > 300) {
                        ReactionsContainerLayout.this.lastReactionSentTime = System.currentTimeMillis();
                        ReactionsContainerLayout.this.delegate.onReactionClicked(this, this.currentReaction, ReactionsContainerLayout.this.pressedProgress > 0.8f);
                    }
                }
                if (!ReactionsContainerLayout.this.clicked) {
                    ReactionsContainerLayout.this.cancelPressed();
                }
                AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
                this.pressed = false;
            }
            return true;
        }
    }

    public void cancelPressed() {
        if (this.pressedReaction != null) {
            this.cancelPressedProgress = 0.0f;
            final float fromProgress = this.pressedProgress;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.cancelPressedAnimation = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ReactionsContainerLayout.7
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ReactionsContainerLayout.this.cancelPressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                    ReactionsContainerLayout reactionsContainerLayout = ReactionsContainerLayout.this;
                    reactionsContainerLayout.pressedProgress = fromProgress * (1.0f - reactionsContainerLayout.cancelPressedProgress);
                    ReactionsContainerLayout.this.invalidate();
                }
            });
            this.cancelPressedAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ReactionsContainerLayout.8
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ReactionsContainerLayout.this.cancelPressedAnimation = null;
                    ReactionsContainerLayout.this.pressedProgress = 0.0f;
                    ReactionsContainerLayout.this.pressedReaction = null;
                    ReactionsContainerLayout.this.invalidate();
                }
            });
            this.cancelPressedAnimation.setDuration(150L);
            this.cancelPressedAnimation.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.cancelPressedAnimation.start();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) args[0];
            if (chatFull.id == this.waitingLoadingChatId && getVisibility() != 0 && !chatFull.available_reactions.isEmpty()) {
                setMessage(this.messageObject, null);
                setVisibility(0);
                startEnterAnimation();
            }
        }
    }

    @Override // android.view.View
    public void setAlpha(float alpha) {
        if (getAlpha() != alpha && alpha == 0.0f) {
            this.lastVisibleViews.clear();
            for (int i = 0; i < this.recyclerListView.getChildCount(); i++) {
                if (this.recyclerListView.getChildAt(i) instanceof ReactionHolderView) {
                    ReactionHolderView view = (ReactionHolderView) this.recyclerListView.getChildAt(i);
                    view.resetAnimation();
                }
            }
        }
        super.setAlpha(alpha);
    }

    @Override // android.view.View
    public void setTranslationX(float translationX) {
        if (translationX != getTranslationX()) {
            super.setTranslationX(translationX);
        }
    }
}
