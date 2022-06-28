package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.GestureDetectorFixDoubleTap;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes5.dex */
public class RecyclerListView extends RecyclerView {
    public static final int SECTIONS_TYPE_DATE = 2;
    public static final int SECTIONS_TYPE_FAST_SCROLL_ONLY = 3;
    public static final int SECTIONS_TYPE_SIMPLE = 0;
    public static final int SECTIONS_TYPE_STICKY_HEADERS = 1;
    private static int[] attributes;
    private static boolean gotAttributes;
    private View.AccessibilityDelegate accessibilityDelegate;
    private boolean accessibilityEnabled;
    private boolean allowItemsInteractionDuringAnimation;
    private boolean animateEmptyView;
    private Runnable clickRunnable;
    private int currentChildPosition;
    private View currentChildView;
    private int currentFirst;
    int currentSelectedPosition;
    private int currentVisible;
    private boolean disableHighlightState;
    private boolean disallowInterceptTouchEvents;
    private boolean drawSelectorBehind;
    private View emptyView;
    int emptyViewAnimateToVisibility;
    private int emptyViewAnimationType;
    private FastScroll fastScroll;
    public boolean fastScrollAnimationRunning;
    private GestureDetectorFixDoubleTap gestureDetector;
    private ArrayList<View> headers;
    private ArrayList<View> headersCache;
    private boolean hiddenByEmptyView;
    private boolean hideIfEmpty;
    private boolean ignoreOnScroll;
    private boolean instantClick;
    private boolean interceptedByChild;
    private boolean isChildViewEnabled;
    private boolean isHidden;
    RecyclerItemsEnterAnimator itemsEnterAnimator;
    private long lastAlphaAnimationTime;
    float lastX;
    float lastY;
    int[] listPaddings;
    private boolean longPressCalled;
    boolean multiSelectionGesture;
    boolean multiSelectionGestureStarted;
    onMultiSelectionChanged multiSelectionListener;
    boolean multiselectScrollRunning;
    boolean multiselectScrollToTop;
    private RecyclerView.AdapterDataObserver observer;
    private OnInterceptTouchListener onInterceptTouchListener;
    private OnItemClickListener onItemClickListener;
    private OnItemClickListenerExtended onItemClickListenerExtended;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemLongClickListenerExtended onItemLongClickListenerExtended;
    private RecyclerView.OnScrollListener onScrollListener;
    private FrameLayout overlayContainer;
    private IntReturnCallback pendingHighlightPosition;
    private View pinnedHeader;
    private float pinnedHeaderShadowAlpha;
    private Drawable pinnedHeaderShadowDrawable;
    private float pinnedHeaderShadowTargetAlpha;
    private Runnable removeHighlighSelectionRunnable;
    private boolean resetSelectorOnChanged;
    protected final Theme.ResourcesProvider resourcesProvider;
    private boolean scrollEnabled;
    Runnable scroller;
    public boolean scrollingByUser;
    private int sectionOffset;
    private SectionsAdapter sectionsAdapter;
    private int sectionsCount;
    private int sectionsType;
    private Runnable selectChildRunnable;
    HashSet<Integer> selectedPositions;
    protected Drawable selectorDrawable;
    protected int selectorPosition;
    private int selectorRadius;
    protected android.graphics.Rect selectorRect;
    private int selectorType;
    private boolean selfOnLayout;
    private int startSection;
    int startSelectionFrom;
    private int topBottomSelectorRadius;
    private int touchSlop;
    boolean useRelativePositions;
    private boolean wasPressed;

    /* loaded from: classes5.dex */
    public interface IntReturnCallback {
        int run();
    }

    /* loaded from: classes5.dex */
    public interface OnInterceptTouchListener {
        boolean onInterceptTouchEvent(MotionEvent motionEvent);
    }

    /* loaded from: classes5.dex */
    public interface OnItemClickListener {
        void onItemClick(View view, int i);
    }

    /* loaded from: classes5.dex */
    public interface OnItemLongClickListener {
        boolean onItemClick(View view, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SectionsType {
    }

    /* loaded from: classes5.dex */
    public interface onMultiSelectionChanged {
        boolean canSelect(int i);

        int checkPosition(int i, boolean z);

        void getPaddings(int[] iArr);

        boolean limitReached();

        void onSelectionChanged(int i, boolean z, float f, float f2);

        void scrollBy(int i);
    }

    public FastScroll getFastScroll() {
        return this.fastScroll;
    }

    /* loaded from: classes5.dex */
    public interface OnItemClickListenerExtended {
        boolean hasDoubleTap(View view, int i);

        void onDoubleTap(View view, int i, float f, float f2);

        void onItemClick(View view, int i, float f, float f2);

        /* renamed from: org.telegram.ui.Components.RecyclerListView$OnItemClickListenerExtended$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static boolean $default$hasDoubleTap(OnItemClickListenerExtended _this, View view, int position) {
                return false;
            }

            public static void $default$onDoubleTap(OnItemClickListenerExtended _this, View view, int position, float x, float y) {
            }
        }
    }

    /* loaded from: classes5.dex */
    public interface OnItemLongClickListenerExtended {
        boolean onItemClick(View view, int i, float f, float f2);

        void onLongClickRelease();

        void onMove(float f, float f2);

        /* renamed from: org.telegram.ui.Components.RecyclerListView$OnItemLongClickListenerExtended$-CC */
        /* loaded from: classes5.dex */
        public final /* synthetic */ class CC {
            public static void $default$onMove(OnItemLongClickListenerExtended _this, float dx, float dy) {
            }

            public static void $default$onLongClickRelease(OnItemLongClickListenerExtended _this) {
            }
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class SelectionAdapter extends RecyclerView.Adapter {
        public abstract boolean isEnabled(RecyclerView.ViewHolder viewHolder);

        public int getSelectionBottomPadding(View view) {
            return 0;
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class FastScrollAdapter extends SelectionAdapter {
        public abstract String getLetter(int i);

        public abstract void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr);

        public void onStartFastScroll() {
        }

        public void onFinishFastScroll(RecyclerListView listView) {
        }

        public int getTotalItemsCount() {
            return getItemCount();
        }

        public float getScrollProgress(RecyclerListView listView) {
            return listView.computeVerticalScrollOffset() / ((getTotalItemsCount() * listView.getChildAt(0).getMeasuredHeight()) - listView.getMeasuredHeight());
        }

        public boolean fastScrollIsVisible(RecyclerListView listView) {
            return true;
        }

        public void onFastScrollSingleTap() {
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class SectionsAdapter extends FastScrollAdapter {
        private int count;
        private SparseIntArray sectionCache;
        private int sectionCount;
        private SparseIntArray sectionCountCache;
        private SparseIntArray sectionPositionCache;

        public abstract int getCountForSection(int i);

        public abstract Object getItem(int i, int i2);

        public abstract int getItemViewType(int i, int i2);

        public abstract int getSectionCount();

        public abstract View getSectionHeaderView(int i, View view);

        public abstract boolean isEnabled(RecyclerView.ViewHolder viewHolder, int i, int i2);

        public abstract void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder);

        private void cleanupCache() {
            SparseIntArray sparseIntArray = this.sectionCache;
            if (sparseIntArray == null) {
                this.sectionCache = new SparseIntArray();
                this.sectionPositionCache = new SparseIntArray();
                this.sectionCountCache = new SparseIntArray();
            } else {
                sparseIntArray.clear();
                this.sectionPositionCache.clear();
                this.sectionCountCache.clear();
            }
            this.count = -1;
            this.sectionCount = -1;
        }

        public void notifySectionsChanged() {
            cleanupCache();
        }

        public SectionsAdapter() {
            cleanupCache();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            cleanupCache();
            super.notifyDataSetChanged();
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return isEnabled(holder, getSectionForPosition(position), getPositionInSectionForPosition(position));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            int i = this.count;
            if (i >= 0) {
                return i;
            }
            this.count = 0;
            int N = internalGetSectionCount();
            for (int i2 = 0; i2 < N; i2++) {
                this.count += internalGetCountForSection(i2);
            }
            int i3 = this.count;
            return i3;
        }

        public final Object getItem(int position) {
            return getItem(getSectionForPosition(position), getPositionInSectionForPosition(position));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public final int getItemViewType(int position) {
            return getItemViewType(getSectionForPosition(position), getPositionInSectionForPosition(position));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            onBindViewHolder(getSectionForPosition(position), getPositionInSectionForPosition(position), holder);
        }

        private int internalGetCountForSection(int section) {
            int cachedSectionCount = this.sectionCountCache.get(section, Integer.MAX_VALUE);
            if (cachedSectionCount != Integer.MAX_VALUE) {
                return cachedSectionCount;
            }
            int sectionCount = getCountForSection(section);
            this.sectionCountCache.put(section, sectionCount);
            return sectionCount;
        }

        private int internalGetSectionCount() {
            int i = this.sectionCount;
            if (i >= 0) {
                return i;
            }
            int sectionCount = getSectionCount();
            this.sectionCount = sectionCount;
            return sectionCount;
        }

        public final int getSectionForPosition(int position) {
            int cachedSection = this.sectionCache.get(position, Integer.MAX_VALUE);
            if (cachedSection != Integer.MAX_VALUE) {
                return cachedSection;
            }
            int sectionStart = 0;
            int N = internalGetSectionCount();
            for (int i = 0; i < N; i++) {
                int sectionCount = internalGetCountForSection(i);
                int sectionEnd = sectionStart + sectionCount;
                if (position >= sectionStart && position < sectionEnd) {
                    this.sectionCache.put(position, i);
                    return i;
                }
                sectionStart = sectionEnd;
            }
            return -1;
        }

        public int getPositionInSectionForPosition(int position) {
            int cachedPosition = this.sectionPositionCache.get(position, Integer.MAX_VALUE);
            if (cachedPosition != Integer.MAX_VALUE) {
                return cachedPosition;
            }
            int sectionStart = 0;
            int N = internalGetSectionCount();
            for (int i = 0; i < N; i++) {
                int sectionCount = internalGetCountForSection(i);
                int sectionEnd = sectionStart + sectionCount;
                if (position >= sectionStart && position < sectionEnd) {
                    int positionInSection = position - sectionStart;
                    this.sectionPositionCache.put(position, positionInSection);
                    return positionInSection;
                }
                sectionStart = sectionEnd;
            }
            return -1;
        }
    }

    /* loaded from: classes5.dex */
    public static class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    /* loaded from: classes5.dex */
    public class FastScroll extends View {
        public static final int DATE_TYPE = 1;
        public static final int LETTER_TYPE = 0;
        private int activeColor;
        private float bubbleProgress;
        private String currentLetter;
        Drawable fastScrollBackgroundDrawable;
        Drawable fastScrollShadowDrawable;
        private float floatingDateProgress;
        private boolean floatingDateVisible;
        private boolean fromTop;
        private float fromWidth;
        private StaticLayout inLetterLayout;
        private int inactiveColor;
        boolean isMoving;
        boolean isRtl;
        boolean isVisible;
        private float lastLetterY;
        private long lastUpdateTime;
        private float lastY;
        private StaticLayout letterLayout;
        private StaticLayout oldLetterLayout;
        private StaticLayout outLetterLayout;
        private boolean pressed;
        private float progress;
        private int scrollX;
        private StaticLayout stableLetterLayout;
        private float startDy;
        long startTime;
        float startY;
        private float textX;
        private float textY;
        float touchSlop;
        private int type;
        float viewAlpha;
        float visibilityAlpha;
        private RectF rect = new RectF();
        private Paint paint = new Paint(1);
        private Paint paint2 = new Paint(1);
        private float replaceLayoutProgress = 1.0f;
        private TextPaint letterPaint = new TextPaint(1);
        private Path path = new Path();
        private Path arrowPath = new Path();
        private float[] radii = new float[8];
        private int[] positionWithOffset = new int[2];
        Runnable hideFloatingDateRunnable = new Runnable() { // from class: org.telegram.ui.Components.RecyclerListView.FastScroll.1
            @Override // java.lang.Runnable
            public void run() {
                if (!FastScroll.this.pressed) {
                    FastScroll.this.floatingDateVisible = false;
                    FastScroll.this.invalidate();
                    return;
                }
                AndroidUtilities.cancelRunOnUIThread(FastScroll.this.hideFloatingDateRunnable);
                AndroidUtilities.runOnUIThread(FastScroll.this.hideFloatingDateRunnable, 4000L);
            }
        };

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public FastScroll(Context context, int type) {
            super(context);
            RecyclerListView.this = this$0;
            this.type = type;
            if (type == 0) {
                this.letterPaint.setTextSize(AndroidUtilities.dp(45.0f));
                this.isRtl = LocaleController.isRTL;
            } else {
                this.isRtl = false;
                this.letterPaint.setTextSize(AndroidUtilities.dp(13.0f));
                this.letterPaint.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                this.paint2.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                Drawable mutate = ContextCompat.getDrawable(context, R.drawable.calendar_date).mutate();
                this.fastScrollBackgroundDrawable = mutate;
                mutate.setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhite), -1, 0.1f), PorterDuff.Mode.MULTIPLY));
            }
            for (int a = 0; a < 8; a++) {
                this.radii[a] = AndroidUtilities.dp(44.0f);
            }
            this.scrollX = AndroidUtilities.dp(this.isRtl ? 10.0f : (type == 0 ? 132 : PsExtractor.VIDEO_STREAM_MASK) - 15);
            updateColors();
            setFocusableInTouchMode(true);
            ViewConfiguration vc = ViewConfiguration.get(context);
            this.touchSlop = vc.getScaledTouchSlop();
            this.fastScrollShadowDrawable = ContextCompat.getDrawable(context, R.drawable.fast_scroll_shadow);
        }

        public void updateColors() {
            this.inactiveColor = this.type == 0 ? Theme.getColor(Theme.key_fastScrollInactive) : ColorUtils.setAlphaComponent(-16777216, 102);
            this.activeColor = Theme.getColor(Theme.key_fastScrollActive);
            this.paint.setColor(this.inactiveColor);
            if (this.type == 0) {
                this.letterPaint.setColor(Theme.getColor(Theme.key_fastScrollText));
            } else {
                this.letterPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            }
            invalidate();
        }

        /* JADX WARN: Code restructure failed: missing block: B:73:0x0155, code lost:
            if (r2 <= (org.telegram.messenger.AndroidUtilities.dp(30.0f) + r3)) goto L75;
         */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onTouchEvent(android.view.MotionEvent r9) {
            /*
                Method dump skipped, instructions count: 402
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RecyclerListView.FastScroll.onTouchEvent(android.view.MotionEvent):boolean");
        }

        public void getCurrentLetter(boolean updatePosition) {
            StaticLayout staticLayout;
            RecyclerView.LayoutManager layoutManager = RecyclerListView.this.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == 1) {
                    RecyclerView.Adapter adapter = RecyclerListView.this.getAdapter();
                    if (adapter instanceof FastScrollAdapter) {
                        FastScrollAdapter fastScrollAdapter = (FastScrollAdapter) adapter;
                        fastScrollAdapter.getPositionForScrollProgress(RecyclerListView.this, this.progress, this.positionWithOffset);
                        if (updatePosition) {
                            int[] iArr = this.positionWithOffset;
                            linearLayoutManager.scrollToPositionWithOffset(iArr[0], (-iArr[1]) + RecyclerListView.this.sectionOffset);
                        }
                        String newLetter = fastScrollAdapter.getLetter(this.positionWithOffset[0]);
                        if (newLetter == null) {
                            StaticLayout staticLayout2 = this.letterLayout;
                            if (staticLayout2 != null) {
                                this.oldLetterLayout = staticLayout2;
                            }
                            this.letterLayout = null;
                        } else if (!newLetter.equals(this.currentLetter)) {
                            this.currentLetter = newLetter;
                            if (this.type == 0) {
                                this.letterLayout = new StaticLayout(newLetter, this.letterPaint, 1000, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                staticLayout = null;
                            } else {
                                this.outLetterLayout = this.letterLayout;
                                int w = ((int) this.letterPaint.measureText(newLetter)) + 1;
                                this.letterLayout = new StaticLayout(newLetter, this.letterPaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                if (this.outLetterLayout == null) {
                                    staticLayout = null;
                                } else {
                                    String[] newSplits = newLetter.split(" ");
                                    String[] oldSplits = this.outLetterLayout.getText().toString().split(" ");
                                    if (newSplits == null || oldSplits == null || newSplits.length != 2 || oldSplits.length != 2 || !newSplits[1].equals(oldSplits[1])) {
                                        this.inLetterLayout = this.letterLayout;
                                        staticLayout = null;
                                        this.stableLetterLayout = null;
                                    } else {
                                        String oldText = this.outLetterLayout.getText().toString();
                                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(oldText);
                                        spannableStringBuilder.setSpan(new EmptyStubSpan(), oldSplits[0].length(), oldText.length(), 0);
                                        int oldW = ((int) this.letterPaint.measureText(oldText)) + 1;
                                        this.outLetterLayout = new StaticLayout(spannableStringBuilder, this.letterPaint, oldW, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(newLetter);
                                        spannableStringBuilder2.setSpan(new EmptyStubSpan(), newSplits[0].length(), newLetter.length(), 0);
                                        this.inLetterLayout = new StaticLayout(spannableStringBuilder2, this.letterPaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                        SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(newLetter);
                                        spannableStringBuilder3.setSpan(new EmptyStubSpan(), 0, newSplits[0].length(), 0);
                                        this.stableLetterLayout = new StaticLayout(spannableStringBuilder3, this.letterPaint, w, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                        staticLayout = null;
                                    }
                                    this.fromWidth = this.outLetterLayout.getWidth();
                                    this.replaceLayoutProgress = 0.0f;
                                    this.fromTop = getProgress() > this.lastLetterY;
                                }
                                this.lastLetterY = getProgress();
                            }
                            this.oldLetterLayout = staticLayout;
                            if (this.letterLayout.getLineCount() > 0) {
                                this.letterLayout.getLineWidth(0);
                                this.letterLayout.getLineLeft(0);
                                if (this.isRtl) {
                                    this.textX = (AndroidUtilities.dp(10.0f) + ((AndroidUtilities.dp(88.0f) - this.letterLayout.getLineWidth(0)) / 2.0f)) - this.letterLayout.getLineLeft(0);
                                } else {
                                    this.textX = ((AndroidUtilities.dp(88.0f) - this.letterLayout.getLineWidth(0)) / 2.0f) - this.letterLayout.getLineLeft(0);
                                }
                                this.textY = (AndroidUtilities.dp(88.0f) - this.letterLayout.getHeight()) / 2;
                            }
                        }
                    }
                }
            }
        }

        @Override // android.view.View
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(AndroidUtilities.dp(this.type == 0 ? 132.0f : 240.0f), View.MeasureSpec.getSize(heightMeasureSpec));
            this.arrowPath.reset();
            this.arrowPath.setLastPoint(0.0f, 0.0f);
            this.arrowPath.lineTo(AndroidUtilities.dp(4.0f), -AndroidUtilities.dp(4.0f));
            this.arrowPath.lineTo(-AndroidUtilities.dp(4.0f), -AndroidUtilities.dp(4.0f));
            this.arrowPath.close();
        }

        /* JADX WARN: Code restructure failed: missing block: B:24:0x01e7, code lost:
            if (r13[6] == r8) goto L25;
         */
        /* JADX WARN: Code restructure failed: missing block: B:29:0x01f7, code lost:
            if (r13[4] == r8) goto L42;
         */
        /* JADX WARN: Removed duplicated region for block: B:31:0x01fb  */
        /* JADX WARN: Removed duplicated region for block: B:32:0x0207  */
        /* JADX WARN: Removed duplicated region for block: B:35:0x021e  */
        /* JADX WARN: Removed duplicated region for block: B:36:0x0224  */
        /* JADX WARN: Removed duplicated region for block: B:39:0x0229  */
        /* JADX WARN: Removed duplicated region for block: B:40:0x022c  */
        /* JADX WARN: Removed duplicated region for block: B:44:0x0252  */
        /* JADX WARN: Removed duplicated region for block: B:46:0x0256  */
        @Override // android.view.View
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        protected void onDraw(android.graphics.Canvas r20) {
            /*
                Method dump skipped, instructions count: 1384
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.RecyclerListView.FastScroll.onDraw(android.graphics.Canvas):void");
        }

        @Override // android.view.View
        public void layout(int l, int t, int r, int b) {
            if (!RecyclerListView.this.selfOnLayout) {
                return;
            }
            super.layout(l, t, r, b);
        }

        public void setProgress(float value) {
            this.progress = value;
            invalidate();
        }

        @Override // android.view.View
        public boolean isPressed() {
            return this.pressed;
        }

        public void showFloatingDate() {
            if (this.type != 1) {
                return;
            }
            if (!this.floatingDateVisible) {
                this.floatingDateVisible = true;
                invalidate();
            }
            AndroidUtilities.cancelRunOnUIThread(this.hideFloatingDateRunnable);
            AndroidUtilities.runOnUIThread(this.hideFloatingDateRunnable, AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS);
        }

        public void setIsVisible(boolean visible) {
            if (this.isVisible != visible) {
                this.isVisible = visible;
                float f = visible ? 1.0f : 0.0f;
                this.visibilityAlpha = f;
                super.setAlpha(this.viewAlpha * f);
            }
        }

        public void setVisibilityAlpha(float v) {
            if (this.visibilityAlpha != v) {
                this.visibilityAlpha = v;
                super.setAlpha(this.viewAlpha * v);
            }
        }

        @Override // android.view.View
        public void setAlpha(float alpha) {
            if (this.viewAlpha != alpha) {
                this.viewAlpha = alpha;
                super.setAlpha(this.visibilityAlpha * alpha);
            }
        }

        @Override // android.view.View
        public float getAlpha() {
            return this.viewAlpha;
        }

        public int getScrollBarY() {
            return ((int) Math.ceil((getMeasuredHeight() - AndroidUtilities.dp(54.0f)) * this.progress)) + AndroidUtilities.dp(17.0f);
        }

        public float getProgress() {
            return this.progress;
        }
    }

    /* loaded from: classes5.dex */
    public class RecyclerListViewItemClickListener implements RecyclerView.OnItemTouchListener {
        public RecyclerListViewItemClickListener(Context context) {
            RecyclerListView.this = r3;
            r3.gestureDetector = new GestureDetectorFixDoubleTap(context, new GestureDetectorFixDoubleTap.OnGestureListener() { // from class: org.telegram.ui.Components.RecyclerListView.RecyclerListViewItemClickListener.1
                private View doubleTapView;

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public boolean onSingleTapUp(MotionEvent e) {
                    if (RecyclerListView.this.currentChildView != null) {
                        if (RecyclerListView.this.onItemClickListenerExtended == null || !RecyclerListView.this.onItemClickListenerExtended.hasDoubleTap(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)) {
                            onPressItem(RecyclerListView.this.currentChildView, e);
                            return false;
                        }
                        this.doubleTapView = RecyclerListView.this.currentChildView;
                    }
                    return false;
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    if (this.doubleTapView != null && RecyclerListView.this.onItemClickListenerExtended != null && RecyclerListView.this.onItemClickListenerExtended.hasDoubleTap(this.doubleTapView, RecyclerListView.this.currentChildPosition)) {
                        onPressItem(this.doubleTapView, e);
                        this.doubleTapView = null;
                        return true;
                    }
                    return false;
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
                public boolean onDoubleTap(MotionEvent e) {
                    if (this.doubleTapView != null && RecyclerListView.this.onItemClickListenerExtended != null && RecyclerListView.this.onItemClickListenerExtended.hasDoubleTap(this.doubleTapView, RecyclerListView.this.currentChildPosition)) {
                        RecyclerListView.this.onItemClickListenerExtended.onDoubleTap(this.doubleTapView, RecyclerListView.this.currentChildPosition, e.getX(), e.getY());
                        this.doubleTapView = null;
                        return true;
                    }
                    return false;
                }

                private void onPressItem(final View cv, MotionEvent e) {
                    if (cv != null) {
                        if (RecyclerListView.this.onItemClickListener != null || RecyclerListView.this.onItemClickListenerExtended != null) {
                            final float x = e.getX();
                            final float y = e.getY();
                            RecyclerListView.this.onChildPressed(cv, x, y, true);
                            final int position = RecyclerListView.this.currentChildPosition;
                            if (RecyclerListView.this.instantClick && position != -1) {
                                cv.playSoundEffect(0);
                                cv.sendAccessibilityEvent(1);
                                if (RecyclerListView.this.onItemClickListener == null) {
                                    if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                        RecyclerListView.this.onItemClickListenerExtended.onItemClick(cv, position, x - cv.getX(), y - cv.getY());
                                    }
                                } else {
                                    RecyclerListView.this.onItemClickListener.onItemClick(cv, position);
                                }
                            }
                            AndroidUtilities.runOnUIThread(RecyclerListView.this.clickRunnable = new Runnable() { // from class: org.telegram.ui.Components.RecyclerListView.RecyclerListViewItemClickListener.1.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    if (this == RecyclerListView.this.clickRunnable) {
                                        RecyclerListView.this.clickRunnable = null;
                                    }
                                    if (cv != null) {
                                        RecyclerListView.this.onChildPressed(cv, 0.0f, 0.0f, false);
                                        if (!RecyclerListView.this.instantClick) {
                                            cv.playSoundEffect(0);
                                            cv.sendAccessibilityEvent(1);
                                            if (position != -1) {
                                                if (RecyclerListView.this.onItemClickListener != null) {
                                                    RecyclerListView.this.onItemClickListener.onItemClick(cv, position);
                                                } else if (RecyclerListView.this.onItemClickListenerExtended != null) {
                                                    OnItemClickListenerExtended onItemClickListenerExtended = RecyclerListView.this.onItemClickListenerExtended;
                                                    View view = cv;
                                                    onItemClickListenerExtended.onItemClick(view, position, x - view.getX(), y - cv.getY());
                                                }
                                            }
                                        }
                                    }
                                }
                            }, ViewConfiguration.getPressedStateDuration());
                            if (RecyclerListView.this.selectChildRunnable != null) {
                                AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                                RecyclerListView.this.selectChildRunnable = null;
                                RecyclerListView.this.currentChildView = null;
                                RecyclerListView.this.interceptedByChild = false;
                                RecyclerListView.this.removeSelection(cv, e);
                            }
                        }
                    }
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public void onLongPress(MotionEvent event) {
                    if (RecyclerListView.this.currentChildView != null && RecyclerListView.this.currentChildPosition != -1) {
                        if (RecyclerListView.this.onItemLongClickListener != null || RecyclerListView.this.onItemLongClickListenerExtended != null) {
                            View child = RecyclerListView.this.currentChildView;
                            if (RecyclerListView.this.onItemLongClickListener != null) {
                                if (RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)) {
                                    child.performHapticFeedback(0);
                                    child.sendAccessibilityEvent(2);
                                }
                            } else if (RecyclerListView.this.onItemLongClickListenerExtended.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition, event.getX() - RecyclerListView.this.currentChildView.getX(), event.getY() - RecyclerListView.this.currentChildView.getY())) {
                                child.performHapticFeedback(0);
                                child.sendAccessibilityEvent(2);
                                RecyclerListView.this.longPressCalled = true;
                            }
                        }
                    }
                }

                @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override // org.telegram.ui.Components.GestureDetectorFixDoubleTap.OnGestureListener
                public boolean hasDoubleTap() {
                    return RecyclerListView.this.onItemLongClickListenerExtended != null;
                }
            });
            r3.gestureDetector.setIsLongpressEnabled(false);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
            View v;
            int action = event.getActionMasked();
            boolean isScrollIdle = RecyclerListView.this.getScrollState() == 0;
            if (action == 0 || action == 5) {
                if (RecyclerListView.this.currentChildView == null && isScrollIdle) {
                    float ex = event.getX();
                    float ey = event.getY();
                    RecyclerListView.this.longPressCalled = false;
                    RecyclerView.ItemAnimator animator = RecyclerListView.this.getItemAnimator();
                    if ((RecyclerListView.this.allowItemsInteractionDuringAnimation || animator == null || !animator.isRunning()) && RecyclerListView.this.allowSelectChildAtPosition(ex, ey) && (v = RecyclerListView.this.findChildViewUnder(ex, ey)) != null && RecyclerListView.this.allowSelectChildAtPosition(v)) {
                        RecyclerListView.this.currentChildView = v;
                    }
                    if (RecyclerListView.this.currentChildView instanceof ViewGroup) {
                        float x = event.getX() - RecyclerListView.this.currentChildView.getLeft();
                        float y = event.getY() - RecyclerListView.this.currentChildView.getTop();
                        ViewGroup viewGroup = (ViewGroup) RecyclerListView.this.currentChildView;
                        int count = viewGroup.getChildCount();
                        int i = count - 1;
                        while (true) {
                            if (i < 0) {
                                break;
                            }
                            View child = viewGroup.getChildAt(i);
                            if (x >= child.getLeft() && x <= child.getRight() && y >= child.getTop() && y <= child.getBottom() && child.isClickable()) {
                                RecyclerListView.this.currentChildView = null;
                                break;
                            }
                            i--;
                        }
                    }
                    RecyclerListView.this.currentChildPosition = -1;
                    if (RecyclerListView.this.currentChildView != null) {
                        RecyclerListView recyclerListView = RecyclerListView.this;
                        recyclerListView.currentChildPosition = view.getChildPosition(recyclerListView.currentChildView);
                        MotionEvent childEvent = MotionEvent.obtain(0L, 0L, event.getActionMasked(), event.getX() - RecyclerListView.this.currentChildView.getLeft(), event.getY() - RecyclerListView.this.currentChildView.getTop(), 0);
                        if (RecyclerListView.this.currentChildView.onTouchEvent(childEvent)) {
                            RecyclerListView.this.interceptedByChild = true;
                        }
                        childEvent.recycle();
                    }
                }
            }
            if (RecyclerListView.this.currentChildView != null && !RecyclerListView.this.interceptedByChild) {
                try {
                    RecyclerListView.this.gestureDetector.onTouchEvent(event);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            if (action == 0 || action == 5) {
                if (!RecyclerListView.this.interceptedByChild && RecyclerListView.this.currentChildView != null) {
                    final float x2 = event.getX();
                    final float y2 = event.getY();
                    RecyclerListView.this.selectChildRunnable = new Runnable() { // from class: org.telegram.ui.Components.RecyclerListView$RecyclerListViewItemClickListener$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            RecyclerListView.RecyclerListViewItemClickListener.this.m2957x7396bf8f(x2, y2);
                        }
                    };
                    AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, ViewConfiguration.getTapTimeout());
                    if (RecyclerListView.this.currentChildView.isEnabled()) {
                        RecyclerListView recyclerListView2 = RecyclerListView.this;
                        if (recyclerListView2.canHighlightChildAt(recyclerListView2.currentChildView, x2 - RecyclerListView.this.currentChildView.getX(), y2 - RecyclerListView.this.currentChildView.getY())) {
                            RecyclerListView recyclerListView3 = RecyclerListView.this;
                            recyclerListView3.positionSelector(recyclerListView3.currentChildPosition, RecyclerListView.this.currentChildView);
                            if (RecyclerListView.this.selectorDrawable != null) {
                                Drawable d = RecyclerListView.this.selectorDrawable.getCurrent();
                                if (d instanceof TransitionDrawable) {
                                    if (RecyclerListView.this.onItemLongClickListener != null || RecyclerListView.this.onItemClickListenerExtended != null) {
                                        ((TransitionDrawable) d).startTransition(ViewConfiguration.getLongPressTimeout());
                                    } else {
                                        ((TransitionDrawable) d).resetTransition();
                                    }
                                }
                                if (Build.VERSION.SDK_INT >= 21) {
                                    RecyclerListView.this.selectorDrawable.setHotspot(event.getX(), event.getY());
                                }
                            }
                            RecyclerListView.this.updateSelectorState();
                            return false;
                        }
                    }
                    RecyclerListView.this.selectorRect.setEmpty();
                    return false;
                }
                RecyclerListView.this.selectorRect.setEmpty();
                return false;
            } else if ((action == 1 || action == 6 || action == 3 || !isScrollIdle) && RecyclerListView.this.currentChildView != null) {
                if (RecyclerListView.this.selectChildRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                    RecyclerListView.this.selectChildRunnable = null;
                }
                View pressedChild = RecyclerListView.this.currentChildView;
                RecyclerListView recyclerListView4 = RecyclerListView.this;
                recyclerListView4.onChildPressed(recyclerListView4.currentChildView, 0.0f, 0.0f, false);
                RecyclerListView.this.currentChildView = null;
                RecyclerListView.this.interceptedByChild = false;
                RecyclerListView.this.removeSelection(pressedChild, event);
                if ((action == 1 || action == 6 || action == 3) && RecyclerListView.this.onItemLongClickListenerExtended != null && RecyclerListView.this.longPressCalled) {
                    RecyclerListView.this.onItemLongClickListenerExtended.onLongClickRelease();
                    RecyclerListView.this.longPressCalled = false;
                    return false;
                }
                return false;
            } else {
                return false;
            }
        }

        /* renamed from: lambda$onInterceptTouchEvent$0$org-telegram-ui-Components-RecyclerListView$RecyclerListViewItemClickListener */
        public /* synthetic */ void m2957x7396bf8f(float x, float y) {
            if (RecyclerListView.this.selectChildRunnable != null && RecyclerListView.this.currentChildView != null) {
                RecyclerListView recyclerListView = RecyclerListView.this;
                recyclerListView.onChildPressed(recyclerListView.currentChildView, x, y, true);
                RecyclerListView.this.selectChildRunnable = null;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
        public void onTouchEvent(RecyclerView view, MotionEvent event) {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            RecyclerListView.this.cancelClickRunnables(true);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public View findChildViewUnder(float x, float y) {
        int count = getChildCount();
        int a = 0;
        while (a < 2) {
            for (int i = count - 1; i >= 0; i--) {
                View child = getChildAt(i);
                float translationY = 0.0f;
                float translationX = a == 0 ? child.getTranslationX() : 0.0f;
                if (a == 0) {
                    translationY = child.getTranslationY();
                }
                if (x >= child.getLeft() + translationX && x <= child.getRight() + translationX && y >= child.getTop() + translationY && y <= child.getBottom() + translationY) {
                    return child;
                }
            }
            a++;
        }
        return null;
    }

    public boolean canHighlightChildAt(View child, float x, float y) {
        return true;
    }

    public void setDisableHighlightState(boolean value) {
        this.disableHighlightState = value;
    }

    public View getPressedChildView() {
        return this.currentChildView;
    }

    public void onChildPressed(View child, float x, float y, boolean pressed) {
        if (this.disableHighlightState || child == null) {
            return;
        }
        child.setPressed(pressed);
    }

    protected boolean allowSelectChildAtPosition(float x, float y) {
        return true;
    }

    public boolean allowSelectChildAtPosition(View child) {
        return true;
    }

    public void removeSelection(View pressedChild, MotionEvent event) {
        if (pressedChild == null || this.selectorRect.isEmpty()) {
            return;
        }
        if (pressedChild.isEnabled()) {
            positionSelector(this.currentChildPosition, pressedChild);
            Drawable drawable = this.selectorDrawable;
            if (drawable != null) {
                Drawable d = drawable.getCurrent();
                if (d instanceof TransitionDrawable) {
                    ((TransitionDrawable) d).resetTransition();
                }
                if (event != null && Build.VERSION.SDK_INT >= 21) {
                    this.selectorDrawable.setHotspot(event.getX(), event.getY());
                }
            }
        } else {
            this.selectorRect.setEmpty();
        }
        updateSelectorState();
    }

    public void cancelClickRunnables(boolean uncheck) {
        Runnable runnable = this.selectChildRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.selectChildRunnable = null;
        }
        View view = this.currentChildView;
        if (view != null) {
            View child = this.currentChildView;
            if (uncheck) {
                onChildPressed(view, 0.0f, 0.0f, false);
            }
            this.currentChildView = null;
            removeSelection(child, null);
        }
        this.selectorRect.setEmpty();
        Runnable runnable2 = this.clickRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.clickRunnable = null;
        }
        this.interceptedByChild = false;
    }

    public void setResetSelectorOnChanged(boolean value) {
        this.resetSelectorOnChanged = value;
    }

    public int[] getResourceDeclareStyleableIntArray(String packageName, String name) {
        try {
            Field f = Class.forName(packageName + ".R$styleable").getField(name);
            if (f != null) {
                return (int[]) f.get(null);
            }
        } catch (Throwable th) {
        }
        return null;
    }

    public RecyclerListView(Context context) {
        this(context, null);
    }

    public RecyclerListView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.allowItemsInteractionDuringAnimation = true;
        this.currentFirst = -1;
        this.currentVisible = -1;
        this.hideIfEmpty = true;
        this.selectorType = 2;
        this.selectorRect = new android.graphics.Rect();
        this.scrollEnabled = true;
        this.lastX = Float.MAX_VALUE;
        this.lastY = Float.MAX_VALUE;
        this.accessibilityEnabled = true;
        this.accessibilityDelegate = new View.AccessibilityDelegate() { // from class: org.telegram.ui.Components.RecyclerListView.1
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                if (host.isEnabled()) {
                    info.addAction(16);
                }
            }
        };
        this.resetSelectorOnChanged = true;
        this.observer = new RecyclerView.AdapterDataObserver() { // from class: org.telegram.ui.Components.RecyclerListView.2
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                RecyclerListView.this.checkIfEmpty(true);
                if (RecyclerListView.this.resetSelectorOnChanged) {
                    RecyclerListView.this.currentFirst = -1;
                    if (RecyclerListView.this.removeHighlighSelectionRunnable == null) {
                        RecyclerListView.this.selectorRect.setEmpty();
                    }
                }
                RecyclerListView.this.invalidate();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                RecyclerListView.this.checkIfEmpty(true);
                if (RecyclerListView.this.pinnedHeader != null && RecyclerListView.this.pinnedHeader.getAlpha() == 0.0f) {
                    RecyclerListView.this.currentFirst = -1;
                    RecyclerListView.this.invalidateViews();
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                RecyclerListView.this.checkIfEmpty(true);
            }
        };
        this.scroller = new Runnable() { // from class: org.telegram.ui.Components.RecyclerListView.6
            @Override // java.lang.Runnable
            public void run() {
                int dy;
                RecyclerListView.this.multiSelectionListener.getPaddings(RecyclerListView.this.listPaddings);
                if (RecyclerListView.this.multiselectScrollToTop) {
                    dy = -AndroidUtilities.dp(12.0f);
                    RecyclerListView recyclerListView = RecyclerListView.this;
                    recyclerListView.chekMultiselect(0.0f, recyclerListView.listPaddings[0]);
                } else {
                    dy = AndroidUtilities.dp(12.0f);
                    RecyclerListView recyclerListView2 = RecyclerListView.this;
                    recyclerListView2.chekMultiselect(0.0f, recyclerListView2.getMeasuredHeight() - RecyclerListView.this.listPaddings[1]);
                }
                RecyclerListView.this.multiSelectionListener.scrollBy(dy);
                if (RecyclerListView.this.multiselectScrollRunning) {
                    AndroidUtilities.runOnUIThread(RecyclerListView.this.scroller);
                }
            }
        };
        this.resourcesProvider = resourcesProvider;
        setGlowColor(getThemedColor(Theme.key_actionBarDefault));
        Drawable selectorDrawable = Theme.getSelectorDrawable(getThemedColor(Theme.key_listSelector), false);
        this.selectorDrawable = selectorDrawable;
        selectorDrawable.setCallback(this);
        try {
            if (!gotAttributes) {
                int[] resourceDeclareStyleableIntArray = getResourceDeclareStyleableIntArray("com.android.internal", "View");
                attributes = resourceDeclareStyleableIntArray;
                if (resourceDeclareStyleableIntArray == null) {
                    attributes = new int[0];
                }
                gotAttributes = true;
            }
            TypedArray a = context.getTheme().obtainStyledAttributes(attributes);
            Method initializeScrollbars = View.class.getDeclaredMethod("initializeScrollbars", TypedArray.class);
            initializeScrollbars.invoke(this, a);
            a.recycle();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        super.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.Components.RecyclerListView.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                boolean z = false;
                if (newState != 0 && RecyclerListView.this.currentChildView != null) {
                    if (RecyclerListView.this.selectChildRunnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
                        RecyclerListView.this.selectChildRunnable = null;
                    }
                    MotionEvent event = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                    try {
                        RecyclerListView.this.gestureDetector.onTouchEvent(event);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    RecyclerListView.this.currentChildView.onTouchEvent(event);
                    event.recycle();
                    View child = RecyclerListView.this.currentChildView;
                    RecyclerListView recyclerListView = RecyclerListView.this;
                    recyclerListView.onChildPressed(recyclerListView.currentChildView, 0.0f, 0.0f, false);
                    RecyclerListView.this.currentChildView = null;
                    RecyclerListView.this.removeSelection(child, null);
                    RecyclerListView.this.interceptedByChild = false;
                }
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrollStateChanged(recyclerView, newState);
                }
                RecyclerListView recyclerListView2 = RecyclerListView.this;
                if (newState == 1 || newState == 2) {
                    z = true;
                }
                recyclerListView2.scrollingByUser = z;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (RecyclerListView.this.onScrollListener != null) {
                    RecyclerListView.this.onScrollListener.onScrolled(recyclerView, dx, dy);
                }
                if (RecyclerListView.this.selectorPosition != -1) {
                    RecyclerListView.this.selectorRect.offset(-dx, -dy);
                    RecyclerListView.this.selectorDrawable.setBounds(RecyclerListView.this.selectorRect);
                    RecyclerListView.this.invalidate();
                } else {
                    RecyclerListView.this.selectorRect.setEmpty();
                }
                RecyclerListView.this.checkSection(false);
                if (dy != 0 && RecyclerListView.this.fastScroll != null) {
                    RecyclerListView.this.fastScroll.showFloatingDate();
                }
            }
        });
        addOnItemTouchListener(new RecyclerListViewItemClickListener(context));
    }

    @Override // android.view.View
    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        if (attributes != null) {
            super.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (this.fastScroll != null) {
            int height = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
            this.fastScroll.getLayoutParams().height = height;
            this.fastScroll.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(132.0f), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(height, C.BUFFER_FLAG_ENCRYPTED));
        }
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (this.fastScroll != null) {
            this.selfOnLayout = true;
            int t2 = t + getPaddingTop();
            if (this.fastScroll.isRtl) {
                FastScroll fastScroll = this.fastScroll;
                fastScroll.layout(0, t2, fastScroll.getMeasuredWidth(), this.fastScroll.getMeasuredHeight() + t2);
            } else {
                int x = getMeasuredWidth() - this.fastScroll.getMeasuredWidth();
                FastScroll fastScroll2 = this.fastScroll;
                fastScroll2.layout(x, t2, fastScroll2.getMeasuredWidth() + x, this.fastScroll.getMeasuredHeight() + t2);
            }
            this.selfOnLayout = false;
        }
        checkSection(false);
        IntReturnCallback intReturnCallback = this.pendingHighlightPosition;
        if (intReturnCallback != null) {
            highlightRowInternal(intReturnCallback, false);
        }
    }

    public void setSelectorType(int type) {
        this.selectorType = type;
    }

    public void setSelectorRadius(int radius) {
        this.selectorRadius = radius;
    }

    public void setTopBottomSelectorRadius(int radius) {
        this.topBottomSelectorRadius = radius;
    }

    public void setDrawSelectorBehind(boolean value) {
        this.drawSelectorBehind = value;
    }

    public void setSelectorDrawableColor(int color) {
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.setCallback(null);
        }
        int i = this.selectorType;
        if (i == 8) {
            this.selectorDrawable = Theme.createRadSelectorDrawable(color, this.selectorRadius, 0);
        } else {
            int i2 = this.topBottomSelectorRadius;
            if (i2 > 0) {
                this.selectorDrawable = Theme.createRadSelectorDrawable(color, i2, i2);
            } else {
                int i3 = this.selectorRadius;
                if (i3 > 0) {
                    this.selectorDrawable = Theme.createSimpleSelectorRoundRectDrawable(i3, 0, color, -16777216);
                } else if (i == 2) {
                    this.selectorDrawable = Theme.getSelectorDrawable(color, false);
                } else {
                    this.selectorDrawable = Theme.createSelectorDrawable(color, i);
                }
            }
        }
        this.selectorDrawable.setCallback(this);
    }

    public Drawable getSelectorDrawable() {
        return this.selectorDrawable;
    }

    public void checkSection(boolean force) {
        FastScroll fastScroll;
        RecyclerView.ViewHolder holder;
        int childCount;
        int minBottom;
        int maxBottom;
        FastScroll fastScroll2;
        RecyclerView.ViewHolder holder2;
        int firstVisibleItem;
        int startSection;
        if (((this.scrollingByUser || force) && this.fastScroll != null) || (this.sectionsType != 0 && this.sectionsAdapter != null)) {
            RecyclerView.LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == 1) {
                    if (this.sectionsAdapter != null) {
                        int paddingTop = getPaddingTop();
                        int i = this.sectionsType;
                        if (i != 1 && i != 3) {
                            if (i == 2) {
                                this.pinnedHeaderShadowTargetAlpha = 0.0f;
                                if (this.sectionsAdapter.getItemCount() == 0) {
                                    return;
                                }
                                int childCount2 = getChildCount();
                                int minBottomSection = 0;
                                int minBottom2 = Integer.MAX_VALUE;
                                View minChild = null;
                                int minBottomSection2 = Integer.MAX_VALUE;
                                View minChildSection = null;
                                for (int a = 0; a < childCount2; a++) {
                                    View child = getChildAt(a);
                                    int bottom = child.getBottom();
                                    if (bottom > this.sectionOffset + paddingTop) {
                                        if (bottom < minBottom2) {
                                            minBottom2 = bottom;
                                            minChild = child;
                                        }
                                        int maxBottom2 = Math.max(minBottomSection, bottom);
                                        int maxBottom3 = this.sectionOffset;
                                        if (bottom < maxBottom3 + paddingTop + AndroidUtilities.dp(32.0f) || bottom >= minBottomSection2) {
                                            minBottomSection = maxBottom2;
                                        } else {
                                            minChildSection = child;
                                            minBottomSection2 = bottom;
                                            minBottomSection = maxBottom2;
                                        }
                                    }
                                }
                                if (minChild == null || (holder2 = getChildViewHolder(minChild)) == null || (startSection = this.sectionsAdapter.getSectionForPosition((firstVisibleItem = holder2.getAdapterPosition()))) < 0) {
                                    return;
                                }
                                if (this.currentFirst != startSection || this.pinnedHeader == null) {
                                    View sectionHeaderView = getSectionHeaderView(startSection, this.pinnedHeader);
                                    this.pinnedHeader = sectionHeaderView;
                                    sectionHeaderView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 0));
                                    View view = this.pinnedHeader;
                                    view.layout(0, 0, view.getMeasuredWidth(), this.pinnedHeader.getMeasuredHeight());
                                    this.currentFirst = startSection;
                                }
                                if (this.pinnedHeader != null && minChildSection != null && minChildSection.getClass() != this.pinnedHeader.getClass()) {
                                    this.pinnedHeaderShadowTargetAlpha = 1.0f;
                                }
                                int count = this.sectionsAdapter.getCountForSection(startSection);
                                int pos = this.sectionsAdapter.getPositionInSectionForPosition(firstVisibleItem);
                                int sectionOffsetY = (minBottomSection == 0 || minBottomSection >= getMeasuredHeight() - getPaddingBottom()) ? this.sectionOffset : 0;
                                if (pos != count - 1) {
                                    this.pinnedHeader.setTag(Integer.valueOf(paddingTop + sectionOffsetY));
                                } else {
                                    int headerHeight = this.pinnedHeader.getHeight();
                                    int headerTop = paddingTop;
                                    if (minChild != null) {
                                        int available = ((minChild.getTop() - paddingTop) - this.sectionOffset) + minChild.getHeight();
                                        if (available < headerHeight) {
                                            headerTop = available - headerHeight;
                                        }
                                    } else {
                                        headerTop = -AndroidUtilities.dp(100.0f);
                                    }
                                    if (headerTop >= 0) {
                                        this.pinnedHeader.setTag(Integer.valueOf(paddingTop + sectionOffsetY));
                                    } else {
                                        this.pinnedHeader.setTag(Integer.valueOf(paddingTop + sectionOffsetY + headerTop));
                                    }
                                }
                                invalidate();
                                return;
                            }
                            return;
                        }
                        int childCount3 = getChildCount();
                        int maxBottom4 = 0;
                        int minBottom3 = Integer.MAX_VALUE;
                        View minChild2 = null;
                        int minBottomSection3 = Integer.MAX_VALUE;
                        for (int a2 = 0; a2 < childCount3; a2++) {
                            View child2 = getChildAt(a2);
                            int bottom2 = child2.getBottom();
                            if (bottom2 > this.sectionOffset + paddingTop) {
                                if (bottom2 < minBottom3) {
                                    minBottom3 = bottom2;
                                    minChild2 = child2;
                                }
                                maxBottom4 = Math.max(maxBottom4, bottom2);
                                if (bottom2 >= this.sectionOffset + paddingTop + AndroidUtilities.dp(32.0f) && bottom2 < minBottomSection3) {
                                    minBottomSection3 = bottom2;
                                }
                            }
                        }
                        if (minChild2 == null || (holder = getChildViewHolder(minChild2)) == null) {
                            return;
                        }
                        int firstVisibleItem2 = holder.getAdapterPosition();
                        int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        int visibleItemCount = Math.abs(lastVisibleItem - firstVisibleItem2) + 1;
                        if ((this.scrollingByUser || force) && (fastScroll2 = this.fastScroll) != null && !fastScroll2.isPressed() && (getAdapter() instanceof FastScrollAdapter)) {
                            this.fastScroll.setProgress(Math.min(1.0f, firstVisibleItem2 / ((this.sectionsAdapter.getTotalItemsCount() - visibleItemCount) + 1)));
                        }
                        this.headersCache.addAll(this.headers);
                        this.headers.clear();
                        if (this.sectionsAdapter.getItemCount() == 0) {
                            return;
                        }
                        if (this.currentFirst != firstVisibleItem2 || this.currentVisible != visibleItemCount) {
                            this.currentFirst = firstVisibleItem2;
                            this.currentVisible = visibleItemCount;
                            this.sectionsCount = 1;
                            int sectionForPosition = this.sectionsAdapter.getSectionForPosition(firstVisibleItem2);
                            this.startSection = sectionForPosition;
                            int itemNum = (this.sectionsAdapter.getCountForSection(sectionForPosition) + firstVisibleItem2) - this.sectionsAdapter.getPositionInSectionForPosition(firstVisibleItem2);
                            while (itemNum < firstVisibleItem2 + visibleItemCount) {
                                itemNum += this.sectionsAdapter.getCountForSection(this.startSection + this.sectionsCount);
                                this.sectionsCount++;
                            }
                        }
                        int itemNum2 = this.sectionsType;
                        if (itemNum2 != 3) {
                            int itemNum3 = firstVisibleItem2;
                            int a3 = this.startSection;
                            while (a3 < this.startSection + this.sectionsCount) {
                                View header = null;
                                if (this.headersCache.isEmpty()) {
                                    childCount = childCount3;
                                } else {
                                    childCount = childCount3;
                                    View header2 = this.headersCache.get(0);
                                    header = header2;
                                    this.headersCache.remove(0);
                                }
                                View header3 = getSectionHeaderView(a3, header);
                                this.headers.add(header3);
                                int count2 = this.sectionsAdapter.getCountForSection(a3);
                                if (a3 == this.startSection) {
                                    int pos2 = this.sectionsAdapter.getPositionInSectionForPosition(itemNum3);
                                    maxBottom = maxBottom4;
                                    int maxBottom5 = count2 - 1;
                                    if (pos2 == maxBottom5) {
                                        header3.setTag(Integer.valueOf((-header3.getHeight()) + paddingTop));
                                        minBottom = minBottom3;
                                    } else if (pos2 == count2 - 2) {
                                        View child3 = getChildAt(itemNum3 - firstVisibleItem2);
                                        int headerTop2 = child3 != null ? child3.getTop() + paddingTop : -AndroidUtilities.dp(100.0f);
                                        minBottom = minBottom3;
                                        header3.setTag(Integer.valueOf(Math.min(headerTop2, 0)));
                                    } else {
                                        minBottom = minBottom3;
                                        header3.setTag(0);
                                    }
                                    itemNum3 += count2 - this.sectionsAdapter.getPositionInSectionForPosition(firstVisibleItem2);
                                } else {
                                    maxBottom = maxBottom4;
                                    minBottom = minBottom3;
                                    int maxBottom6 = itemNum3 - firstVisibleItem2;
                                    View child4 = getChildAt(maxBottom6);
                                    if (child4 != null) {
                                        header3.setTag(Integer.valueOf(child4.getTop() + paddingTop));
                                    } else {
                                        header3.setTag(Integer.valueOf(-AndroidUtilities.dp(100.0f)));
                                    }
                                    itemNum3 += count2;
                                }
                                a3++;
                                childCount3 = childCount;
                                maxBottom4 = maxBottom;
                                minBottom3 = minBottom;
                            }
                            return;
                        }
                        return;
                    }
                    int firstVisibleItem3 = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItem2 = linearLayoutManager.findLastVisibleItemPosition();
                    int abs = Math.abs(lastVisibleItem2 - firstVisibleItem3) + 1;
                    if (firstVisibleItem3 == -1) {
                        return;
                    }
                    if ((this.scrollingByUser || force) && (fastScroll = this.fastScroll) != null && !fastScroll.isPressed()) {
                        RecyclerView.Adapter adapter = getAdapter();
                        if (adapter instanceof FastScrollAdapter) {
                            float p = ((FastScrollAdapter) adapter).getScrollProgress(this);
                            boolean visible = ((FastScrollAdapter) adapter).fastScrollIsVisible(this);
                            this.fastScroll.setIsVisible(visible);
                            this.fastScroll.setProgress(Math.min(1.0f, p));
                            this.fastScroll.getCurrentLetter(false);
                        }
                    }
                }
            }
        }
    }

    public void setListSelectorColor(int color) {
        Theme.setSelectorDrawableColor(this.selectorDrawable, color, true);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListenerExtended listener) {
        this.onItemClickListenerExtended = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return this.onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
        this.gestureDetector.setIsLongpressEnabled(listener != null);
    }

    public void setOnItemLongClickListener(OnItemLongClickListenerExtended listener) {
        this.onItemLongClickListenerExtended = listener;
        this.gestureDetector.setIsLongpressEnabled(listener != null);
    }

    public void setEmptyView(View view) {
        View view2 = this.emptyView;
        if (view2 == view) {
            return;
        }
        if (view2 != null) {
            view2.animate().setListener(null).cancel();
        }
        this.emptyView = view;
        if (this.animateEmptyView && view != null) {
            view.setVisibility(8);
        }
        if (this.isHidden) {
            View view3 = this.emptyView;
            if (view3 != null) {
                this.emptyViewAnimateToVisibility = 8;
                view3.setVisibility(8);
                return;
            }
            return;
        }
        this.emptyViewAnimateToVisibility = -1;
        checkIfEmpty(updateEmptyViewAnimated());
    }

    protected boolean updateEmptyViewAnimated() {
        return isAttachedToWindow();
    }

    public View getEmptyView() {
        return this.emptyView;
    }

    public void invalidateViews() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            getChildAt(a).invalidate();
        }
    }

    public void updateFastScrollColors() {
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll != null) {
            fastScroll.updateColors();
        }
    }

    public void setPinnedHeaderShadowDrawable(Drawable drawable) {
        this.pinnedHeaderShadowDrawable = drawable;
    }

    @Override // android.view.View
    public boolean canScrollVertically(int direction) {
        return this.scrollEnabled && super.canScrollVertically(direction);
    }

    public void setScrollEnabled(boolean value) {
        this.scrollEnabled = value;
    }

    public void highlightRow(IntReturnCallback callback) {
        highlightRowInternal(callback, true);
    }

    private void highlightRowInternal(IntReturnCallback callback, boolean canHighlightLater) {
        Runnable runnable = this.removeHighlighSelectionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.removeHighlighSelectionRunnable = null;
        }
        RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(callback.run());
        if (holder != null) {
            positionSelector(holder.getLayoutPosition(), holder.itemView);
            Drawable drawable = this.selectorDrawable;
            if (drawable != null) {
                Drawable d = drawable.getCurrent();
                if (d instanceof TransitionDrawable) {
                    if (this.onItemLongClickListener != null || this.onItemClickListenerExtended != null) {
                        ((TransitionDrawable) d).startTransition(ViewConfiguration.getLongPressTimeout());
                    } else {
                        ((TransitionDrawable) d).resetTransition();
                    }
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    this.selectorDrawable.setHotspot(holder.itemView.getMeasuredWidth() / 2, holder.itemView.getMeasuredHeight() / 2);
                }
            }
            Drawable d2 = this.selectorDrawable;
            if (d2 != null && d2.isStateful() && this.selectorDrawable.setState(getDrawableStateForSelector())) {
                invalidateDrawable(this.selectorDrawable);
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.RecyclerListView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RecyclerListView.this.m2956x6956124b();
                }
            };
            this.removeHighlighSelectionRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 700L);
        } else if (canHighlightLater) {
            this.pendingHighlightPosition = callback;
        }
    }

    /* renamed from: lambda$highlightRowInternal$0$org-telegram-ui-Components-RecyclerListView */
    public /* synthetic */ void m2956x6956124b() {
        this.removeHighlighSelectionRunnable = null;
        this.pendingHighlightPosition = null;
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            Drawable d = drawable.getCurrent();
            if (d instanceof TransitionDrawable) {
                ((TransitionDrawable) d).resetTransition();
            }
        }
        Drawable d2 = this.selectorDrawable;
        if (d2 != null && d2.isStateful()) {
            this.selectorDrawable.setState(StateSet.NOTHING);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!isEnabled()) {
            return false;
        }
        if (this.disallowInterceptTouchEvents) {
            requestDisallowInterceptTouchEvent(true);
        }
        OnInterceptTouchListener onInterceptTouchListener = this.onInterceptTouchListener;
        return (onInterceptTouchListener != null && onInterceptTouchListener.onInterceptTouchEvent(e)) || super.onInterceptTouchEvent(e);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view;
        FastScroll fastScroll = getFastScroll();
        if (fastScroll != null && fastScroll.isVisible && fastScroll.isMoving && ev.getActionMasked() != 1 && ev.getActionMasked() != 3) {
            return true;
        }
        if (this.sectionsAdapter != null && (view = this.pinnedHeader) != null && view.getAlpha() != 0.0f && this.pinnedHeader.dispatchTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void checkIfEmpty(boolean animated) {
        if (this.isHidden) {
            return;
        }
        int i = 0;
        if (getAdapter() == null || this.emptyView == null) {
            if (this.hiddenByEmptyView && getVisibility() != 0) {
                setVisibility(0);
                this.hiddenByEmptyView = false;
                return;
            }
            return;
        }
        boolean emptyViewVisible = emptyViewIsVisible();
        int newVisibility = emptyViewVisible ? 0 : 8;
        if (!this.animateEmptyView) {
            animated = false;
        }
        if (animated) {
            if (this.emptyViewAnimateToVisibility != newVisibility) {
                this.emptyViewAnimateToVisibility = newVisibility;
                if (newVisibility != 0) {
                    if (this.emptyView.getVisibility() != 8) {
                        ViewPropertyAnimator animator = this.emptyView.animate().alpha(0.0f);
                        if (this.emptyViewAnimationType == 1) {
                            animator.scaleY(0.7f).scaleX(0.7f);
                        }
                        animator.setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.RecyclerListView.4
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                if (RecyclerListView.this.emptyView != null) {
                                    RecyclerListView.this.emptyView.setVisibility(8);
                                }
                            }
                        }).start();
                    }
                } else {
                    this.emptyView.animate().setListener(null).cancel();
                    if (this.emptyView.getVisibility() == 8) {
                        this.emptyView.setVisibility(0);
                        this.emptyView.setAlpha(0.0f);
                        if (this.emptyViewAnimationType == 1) {
                            this.emptyView.setScaleX(0.7f);
                            this.emptyView.setScaleY(0.7f);
                        }
                    }
                    this.emptyView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150L).start();
                }
            }
        } else {
            this.emptyViewAnimateToVisibility = newVisibility;
            this.emptyView.setVisibility(newVisibility);
            this.emptyView.setAlpha(1.0f);
        }
        if (this.hideIfEmpty) {
            if (emptyViewVisible) {
                i = 4;
            }
            int newVisibility2 = i;
            if (getVisibility() != newVisibility2) {
                setVisibility(newVisibility2);
            }
            this.hiddenByEmptyView = true;
        }
    }

    public boolean emptyViewIsVisible() {
        return getAdapter() != null && !isFastScrollAnimationRunning() && getAdapter().getItemCount() == 0;
    }

    public void hide() {
        if (this.isHidden) {
            return;
        }
        this.isHidden = true;
        if (getVisibility() != 8) {
            setVisibility(8);
        }
        View view = this.emptyView;
        if (view != null && view.getVisibility() != 8) {
            this.emptyView.setVisibility(8);
        }
    }

    public void show() {
        if (!this.isHidden) {
            return;
        }
        this.isHidden = false;
        checkIfEmpty(false);
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != 0) {
            this.hiddenByEmptyView = false;
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        this.onScrollListener = listener;
    }

    public void setHideIfEmpty(boolean value) {
        this.hideIfEmpty = value;
    }

    public RecyclerView.OnScrollListener getOnScrollListener() {
        return this.onScrollListener;
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener listener) {
        this.onInterceptTouchListener = listener;
    }

    public void setInstantClick(boolean value) {
        this.instantClick = value;
    }

    public void setDisallowInterceptTouchEvents(boolean value) {
        this.disallowInterceptTouchEvents = value;
    }

    public void setFastScrollEnabled(int type) {
        this.fastScroll = new FastScroll(getContext(), type);
        if (getParent() != null) {
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    public void setFastScrollVisible(boolean value) {
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll == null) {
            return;
        }
        fastScroll.setVisibility(value ? 0 : 8);
        this.fastScroll.isVisible = value;
    }

    public void setSectionsType(int type) {
        this.sectionsType = type;
        if (type == 1 || type == 3) {
            this.headers = new ArrayList<>();
            this.headersCache = new ArrayList<>();
        }
    }

    public void setPinnedSectionOffsetY(int offset) {
        this.sectionOffset = offset;
        invalidate();
    }

    public void positionSelector(int position, View sel) {
        positionSelector(position, sel, false, -1.0f, -1.0f);
    }

    private void positionSelector(int position, View sel, boolean manageHotspot, float x, float y) {
        int bottomPadding;
        Runnable runnable = this.removeHighlighSelectionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.removeHighlighSelectionRunnable = null;
            this.pendingHighlightPosition = null;
        }
        if (this.selectorDrawable == null) {
            return;
        }
        boolean positionChanged = position != this.selectorPosition;
        if (getAdapter() instanceof SelectionAdapter) {
            bottomPadding = ((SelectionAdapter) getAdapter()).getSelectionBottomPadding(sel);
        } else {
            bottomPadding = 0;
        }
        if (position != -1) {
            this.selectorPosition = position;
        }
        if (this.selectorType == 8) {
            Theme.setMaskDrawableRad(this.selectorDrawable, this.selectorRadius, 0);
        } else if (this.topBottomSelectorRadius > 0 && getAdapter() != null) {
            Theme.setMaskDrawableRad(this.selectorDrawable, position == 0 ? this.topBottomSelectorRadius : 0, position == getAdapter().getItemCount() + (-2) ? this.topBottomSelectorRadius : 0);
        }
        this.selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom() - bottomPadding);
        this.selectorRect.offset((int) sel.getTranslationX(), (int) sel.getTranslationY());
        boolean enabled = sel.isEnabled();
        if (this.isChildViewEnabled != enabled) {
            this.isChildViewEnabled = enabled;
        }
        if (positionChanged) {
            this.selectorDrawable.setVisible(false, false);
            this.selectorDrawable.setState(StateSet.NOTHING);
        }
        this.selectorDrawable.setBounds(this.selectorRect);
        if (positionChanged && getVisibility() == 0) {
            this.selectorDrawable.setVisible(true, false);
        }
        if (Build.VERSION.SDK_INT >= 21 && manageHotspot) {
            this.selectorDrawable.setHotspot(x, y);
        }
    }

    public void setAllowItemsInteractionDuringAnimation(boolean value) {
        this.allowItemsInteractionDuringAnimation = value;
    }

    public void hideSelector(boolean animated) {
        View view = this.currentChildView;
        if (view != null) {
            View child = this.currentChildView;
            onChildPressed(view, 0.0f, 0.0f, false);
            this.currentChildView = null;
            if (animated) {
                removeSelection(child, null);
            }
        }
        if (!animated) {
            this.selectorDrawable.setState(StateSet.NOTHING);
            this.selectorRect.setEmpty();
        }
    }

    public void updateSelectorState() {
        Drawable drawable = this.selectorDrawable;
        if (drawable != null && drawable.isStateful()) {
            if (this.currentChildView != null) {
                if (this.selectorDrawable.setState(getDrawableStateForSelector())) {
                    invalidateDrawable(this.selectorDrawable);
                }
            } else if (this.removeHighlighSelectionRunnable == null) {
                this.selectorDrawable.setState(StateSet.NOTHING);
            }
        }
    }

    private int[] getDrawableStateForSelector() {
        int[] state = onCreateDrawableState(1);
        state[state.length - 1] = 16842919;
        return state;
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void onChildAttachedToWindow(View child) {
        if (getAdapter() instanceof SelectionAdapter) {
            RecyclerView.ViewHolder holder = findContainingViewHolder(child);
            if (holder != null) {
                child.setEnabled(((SelectionAdapter) getAdapter()).isEnabled(holder));
                if (this.accessibilityEnabled) {
                    child.setAccessibilityDelegate(this.accessibilityDelegate);
                }
            }
        } else {
            child.setEnabled(false);
            child.setAccessibilityDelegate(null);
        }
        super.onChildAttachedToWindow(child);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }

    @Override // android.view.View
    public boolean verifyDrawable(Drawable drawable) {
        return this.selectorDrawable == drawable || super.verifyDrawable(drawable);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.selectorDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll != null && fastScroll.getParent() != getParent()) {
            ViewGroup parent = (ViewGroup) this.fastScroll.getParent();
            if (parent != null) {
                parent.removeView(this.fastScroll);
            }
            ((ViewGroup) getParent()).addView(this.fastScroll);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void setAdapter(RecyclerView.Adapter adapter) {
        RecyclerView.Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(this.observer);
        }
        ArrayList<View> arrayList = this.headers;
        if (arrayList != null) {
            arrayList.clear();
            this.headersCache.clear();
        }
        this.currentFirst = -1;
        this.selectorPosition = -1;
        this.selectorRect.setEmpty();
        this.pinnedHeader = null;
        if (adapter instanceof SectionsAdapter) {
            this.sectionsAdapter = (SectionsAdapter) adapter;
        } else {
            this.sectionsAdapter = null;
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.observer);
        }
        checkIfEmpty(false);
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void stopScroll() {
        try {
            super.stopScroll();
        } catch (NullPointerException e) {
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, androidx.core.view.NestedScrollingChild2
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        if (this.longPressCalled) {
            OnItemLongClickListenerExtended onItemLongClickListenerExtended = this.onItemLongClickListenerExtended;
            if (onItemLongClickListenerExtended != null) {
                onItemLongClickListenerExtended.onMove(dx, dy);
            }
            consumed[0] = dx;
            consumed[1] = dy;
            return true;
        }
        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    private View getSectionHeaderView(int section, View oldView) {
        boolean shouldLayout = oldView == null;
        View view = this.sectionsAdapter.getSectionHeaderView(section, oldView);
        if (shouldLayout) {
            ensurePinnedHeaderLayout(view, false);
        }
        return view;
    }

    private void ensurePinnedHeaderLayout(View header, boolean forceLayout) {
        if (header == null) {
            return;
        }
        if (header.isLayoutRequested() || forceLayout) {
            int i = this.sectionsType;
            if (i == 1) {
                ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
                int heightSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.height, C.BUFFER_FLAG_ENCRYPTED);
                int widthSpec = View.MeasureSpec.makeMeasureSpec(layoutParams.width, C.BUFFER_FLAG_ENCRYPTED);
                try {
                    header.measure(widthSpec, heightSpec);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else if (i == 2) {
                int widthSpec2 = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), C.BUFFER_FLAG_ENCRYPTED);
                int heightSpec2 = View.MeasureSpec.makeMeasureSpec(0, 0);
                try {
                    header.measure(widthSpec2, heightSpec2);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        View view;
        super.onSizeChanged(w, h, oldw, oldh);
        FrameLayout frameLayout = this.overlayContainer;
        if (frameLayout != null) {
            frameLayout.requestLayout();
        }
        int i = this.sectionsType;
        if (i == 1) {
            if (this.sectionsAdapter == null || this.headers.isEmpty()) {
                return;
            }
            for (int a = 0; a < this.headers.size(); a++) {
                View header = this.headers.get(a);
                ensurePinnedHeaderLayout(header, true);
            }
        } else if (i == 2 && this.sectionsAdapter != null && (view = this.pinnedHeader) != null) {
            ensurePinnedHeaderLayout(view, true);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        View view;
        RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = this.itemsEnterAnimator;
        if (recyclerItemsEnterAnimator != null) {
            recyclerItemsEnterAnimator.dispatchDraw();
        }
        if (this.drawSelectorBehind && !this.selectorRect.isEmpty()) {
            this.selectorDrawable.setBounds(this.selectorRect);
            this.selectorDrawable.draw(canvas);
        }
        super.dispatchDraw(canvas);
        if (!this.drawSelectorBehind && !this.selectorRect.isEmpty()) {
            this.selectorDrawable.setBounds(this.selectorRect);
            this.selectorDrawable.draw(canvas);
        }
        FrameLayout frameLayout = this.overlayContainer;
        if (frameLayout != null) {
            frameLayout.draw(canvas);
        }
        int i = this.sectionsType;
        float f = 0.0f;
        if (i == 1) {
            if (this.sectionsAdapter != null && !this.headers.isEmpty()) {
                for (int a = 0; a < this.headers.size(); a++) {
                    View header = this.headers.get(a);
                    int saveCount = canvas.save();
                    int top = ((Integer) header.getTag()).intValue();
                    canvas.translate(LocaleController.isRTL ? getWidth() - header.getWidth() : 0.0f, top);
                    canvas.clipRect(0, 0, getWidth(), header.getMeasuredHeight());
                    header.draw(canvas);
                    canvas.restoreToCount(saveCount);
                }
            }
        } else if (i == 2 && this.sectionsAdapter != null && (view = this.pinnedHeader) != null && view.getAlpha() != 0.0f) {
            int saveCount2 = canvas.save();
            int top2 = ((Integer) this.pinnedHeader.getTag()).intValue();
            if (LocaleController.isRTL) {
                f = getWidth() - this.pinnedHeader.getWidth();
            }
            canvas.translate(f, top2);
            Drawable drawable = this.pinnedHeaderShadowDrawable;
            if (drawable != null) {
                drawable.setBounds(0, this.pinnedHeader.getMeasuredHeight(), getWidth(), this.pinnedHeader.getMeasuredHeight() + this.pinnedHeaderShadowDrawable.getIntrinsicHeight());
                this.pinnedHeaderShadowDrawable.setAlpha((int) (this.pinnedHeaderShadowAlpha * 255.0f));
                this.pinnedHeaderShadowDrawable.draw(canvas);
                long newTime = SystemClock.elapsedRealtime();
                long dt = Math.min(20L, newTime - this.lastAlphaAnimationTime);
                this.lastAlphaAnimationTime = newTime;
                float f2 = this.pinnedHeaderShadowAlpha;
                float f3 = this.pinnedHeaderShadowTargetAlpha;
                if (f2 < f3) {
                    float f4 = f2 + (((float) dt) / 180.0f);
                    this.pinnedHeaderShadowAlpha = f4;
                    if (f4 > f3) {
                        this.pinnedHeaderShadowAlpha = f3;
                    }
                    invalidate();
                } else if (f2 > f3) {
                    float f5 = f2 - (((float) dt) / 180.0f);
                    this.pinnedHeaderShadowAlpha = f5;
                    if (f5 < f3) {
                        this.pinnedHeaderShadowAlpha = f3;
                    }
                    invalidate();
                }
            }
            canvas.clipRect(0, 0, getWidth(), this.pinnedHeader.getMeasuredHeight());
            this.pinnedHeader.draw(canvas);
            canvas.restoreToCount(saveCount2);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.selectorPosition = -1;
        this.selectorRect.setEmpty();
        RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = this.itemsEnterAnimator;
        if (recyclerItemsEnterAnimator != null) {
            recyclerItemsEnterAnimator.onDetached();
        }
    }

    public void addOverlayView(View view, FrameLayout.LayoutParams layoutParams) {
        if (this.overlayContainer == null) {
            this.overlayContainer = new FrameLayout(getContext()) { // from class: org.telegram.ui.Components.RecyclerListView.5
                @Override // android.view.View, android.view.ViewParent
                public void requestLayout() {
                    super.requestLayout();
                    try {
                        int measuredWidth = RecyclerListView.this.getMeasuredWidth();
                        int measuredHeight = RecyclerListView.this.getMeasuredHeight();
                        measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, C.BUFFER_FLAG_ENCRYPTED), View.MeasureSpec.makeMeasureSpec(measuredHeight, C.BUFFER_FLAG_ENCRYPTED));
                        layout(0, 0, RecyclerListView.this.overlayContainer.getMeasuredWidth(), RecyclerListView.this.overlayContainer.getMeasuredHeight());
                    } catch (Exception e) {
                    }
                }
            };
        }
        this.overlayContainer.addView(view, layoutParams);
    }

    public void removeOverlayView(View view) {
        FrameLayout frameLayout = this.overlayContainer;
        if (frameLayout != null) {
            frameLayout.removeView(view);
        }
    }

    public ArrayList<View> getHeaders() {
        return this.headers;
    }

    public ArrayList<View> getHeadersCache() {
        return this.headersCache;
    }

    public View getPinnedHeader() {
        return this.pinnedHeader;
    }

    public boolean isFastScrollAnimationRunning() {
        return this.fastScrollAnimationRunning;
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.fastScrollAnimationRunning) {
            return;
        }
        super.requestLayout();
    }

    public void setAnimateEmptyView(boolean animate, int emptyViewAnimationType) {
        this.animateEmptyView = animate;
        this.emptyViewAnimationType = emptyViewAnimationType;
    }

    /* loaded from: classes5.dex */
    public static class FoucsableOnTouchListener implements View.OnTouchListener {
        private boolean onFocus;
        private float x;
        private float y;

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            ViewParent parent = v.getParent();
            if (parent == null) {
                return false;
            }
            if (event.getAction() == 0) {
                this.x = event.getX();
                this.y = event.getY();
                this.onFocus = true;
                parent.requestDisallowInterceptTouchEvent(true);
            }
            if (event.getAction() == 2) {
                float dx = this.x - event.getX();
                float dy = this.y - event.getY();
                float touchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
                if (this.onFocus && Math.sqrt((dx * dx) + (dy * dy)) > touchSlop) {
                    this.onFocus = false;
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                this.onFocus = false;
                parent.requestDisallowInterceptTouchEvent(false);
            }
            return false;
        }
    }

    @Override // android.view.View
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll != null) {
            fastScroll.setTranslationY(translationY);
        }
    }

    public void startMultiselect(int positionFrom, boolean useRelativePositions, onMultiSelectionChanged multiSelectionListener) {
        if (!this.multiSelectionGesture) {
            this.listPaddings = new int[2];
            this.selectedPositions = new HashSet<>();
            getParent().requestDisallowInterceptTouchEvent(true);
            this.multiSelectionListener = multiSelectionListener;
            this.multiSelectionGesture = true;
            this.currentSelectedPosition = positionFrom;
            this.startSelectionFrom = positionFrom;
        }
        this.useRelativePositions = useRelativePositions;
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public boolean onTouchEvent(MotionEvent e) {
        FastScroll fastScroll = this.fastScroll;
        if (fastScroll == null || !fastScroll.pressed) {
            if (this.multiSelectionGesture && e.getAction() != 0 && e.getAction() != 1 && e.getAction() != 3) {
                if (this.lastX == Float.MAX_VALUE && this.lastY == Float.MAX_VALUE) {
                    this.lastX = e.getX();
                    this.lastY = e.getY();
                }
                if (!this.multiSelectionGestureStarted && Math.abs(e.getY() - this.lastY) > this.touchSlop) {
                    this.multiSelectionGestureStarted = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (this.multiSelectionGestureStarted) {
                    chekMultiselect(e.getX(), e.getY());
                    this.multiSelectionListener.getPaddings(this.listPaddings);
                    if (e.getY() > (getMeasuredHeight() - AndroidUtilities.dp(56.0f)) - this.listPaddings[1] && (this.currentSelectedPosition >= this.startSelectionFrom || !this.multiSelectionListener.limitReached())) {
                        startMultiselectScroll(false);
                    } else if (e.getY() < AndroidUtilities.dp(56.0f) + this.listPaddings[0] && (this.currentSelectedPosition <= this.startSelectionFrom || !this.multiSelectionListener.limitReached())) {
                        startMultiselectScroll(true);
                    } else {
                        cancelMultiselectScroll();
                    }
                }
                return true;
            }
            this.lastX = Float.MAX_VALUE;
            this.lastY = Float.MAX_VALUE;
            this.multiSelectionGesture = false;
            this.multiSelectionGestureStarted = false;
            getParent().requestDisallowInterceptTouchEvent(false);
            cancelMultiselectScroll();
            return super.onTouchEvent(e);
        }
        return false;
    }

    public boolean chekMultiselect(float x, float y) {
        int measuredHeight = getMeasuredHeight();
        int[] iArr = this.listPaddings;
        float y2 = Math.min(measuredHeight - iArr[1], Math.max(y, iArr[0]));
        float x2 = Math.min(getMeasuredWidth(), Math.max(x, 0.0f));
        int i = 0;
        while (true) {
            if (i >= getChildCount()) {
                break;
            }
            this.multiSelectionListener.getPaddings(this.listPaddings);
            if (!this.useRelativePositions) {
                View child = getChildAt(i);
                AndroidUtilities.rectTmp.set(child.getLeft(), child.getTop(), child.getLeft() + child.getMeasuredWidth(), child.getTop() + child.getMeasuredHeight());
                if (AndroidUtilities.rectTmp.contains(x2, y2)) {
                    int position = getChildLayoutPosition(child);
                    int i2 = this.currentSelectedPosition;
                    if (i2 != position) {
                        int i3 = this.startSelectionFrom;
                        boolean selectionFromTop = i2 > i3 || position > i3;
                        position = this.multiSelectionListener.checkPosition(position, selectionFromTop);
                        if (selectionFromTop) {
                            if (position > this.currentSelectedPosition) {
                                if (!this.multiSelectionListener.limitReached()) {
                                    for (int k = this.currentSelectedPosition + 1; k <= position; k++) {
                                        if (k != this.startSelectionFrom && this.multiSelectionListener.canSelect(k)) {
                                            this.multiSelectionListener.onSelectionChanged(k, true, x2, y2);
                                        }
                                    }
                                }
                            } else {
                                for (int k2 = this.currentSelectedPosition; k2 > position; k2--) {
                                    if (k2 != this.startSelectionFrom && this.multiSelectionListener.canSelect(k2)) {
                                        this.multiSelectionListener.onSelectionChanged(k2, false, x2, y2);
                                    }
                                }
                            }
                        } else if (position > this.currentSelectedPosition) {
                            for (int k3 = this.currentSelectedPosition; k3 < position; k3++) {
                                if (k3 != this.startSelectionFrom && this.multiSelectionListener.canSelect(k3)) {
                                    this.multiSelectionListener.onSelectionChanged(k3, false, x2, y2);
                                }
                            }
                        } else if (!this.multiSelectionListener.limitReached()) {
                            for (int k4 = this.currentSelectedPosition - 1; k4 >= position; k4--) {
                                if (k4 != this.startSelectionFrom && this.multiSelectionListener.canSelect(k4)) {
                                    this.multiSelectionListener.onSelectionChanged(k4, true, x2, y2);
                                }
                            }
                        }
                    }
                    if (!this.multiSelectionListener.limitReached()) {
                        this.currentSelectedPosition = position;
                    }
                }
            }
            i++;
        }
        return true;
    }

    private void cancelMultiselectScroll() {
        this.multiselectScrollRunning = false;
        AndroidUtilities.cancelRunOnUIThread(this.scroller);
    }

    private void startMultiselectScroll(boolean top) {
        this.multiselectScrollToTop = top;
        if (!this.multiselectScrollRunning) {
            this.multiselectScrollRunning = true;
            AndroidUtilities.cancelRunOnUIThread(this.scroller);
            AndroidUtilities.runOnUIThread(this.scroller);
        }
    }

    public boolean isMultiselect() {
        return this.multiSelectionGesture;
    }

    public int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public Drawable getThemedDrawable(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Drawable drawable = resourcesProvider != null ? resourcesProvider.getDrawable(key) : null;
        return drawable != null ? drawable : Theme.getThemeDrawable(key);
    }

    public Paint getThemedPaint(String paintKey) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(paintKey) : null;
        return paint != null ? paint : Theme.getThemePaint(paintKey);
    }

    public void setItemsEnterAnimator(RecyclerItemsEnterAnimator itemsEnterAnimator) {
        this.itemsEnterAnimator = itemsEnterAnimator;
    }

    public void setAccessibilityEnabled(boolean accessibilityEnabled) {
        this.accessibilityEnabled = accessibilityEnabled;
    }
}
