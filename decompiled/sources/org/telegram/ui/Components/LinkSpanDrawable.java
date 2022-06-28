package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Components.LinkSpanDrawable;
/* loaded from: classes5.dex */
public class LinkSpanDrawable<S extends CharacterStyle> {
    private static final long mReleaseDelay = 75;
    private static final long mReleaseDuration = 100;
    private static final ArrayList<LinkPath> pathCache = new ArrayList<>();
    private final Path circlePath;
    private int color;
    private int cornerRadius;
    private android.graphics.Rect mBounds;
    private final long mDuration;
    private final long mLongPressDuration;
    private float mMaxRadius;
    private final ArrayList<LinkPath> mPathes;
    private int mPathesCount;
    private long mReleaseStart;
    private final Theme.ResourcesProvider mResourcesProvider;
    private int mRippleAlpha;
    private Paint mRipplePaint;
    private int mSelectionAlpha;
    private Paint mSelectionPaint;
    private final S mSpan;
    private long mStart;
    private final boolean mSupportsLongPress;
    private final float mTouchX;
    private final float mTouchY;
    private final float rippleAlpha;
    private final float selectionAlpha;

    public LinkSpanDrawable(S span, Theme.ResourcesProvider resourcesProvider, float touchX, float touchY) {
        this(span, resourcesProvider, touchX, touchY, true);
    }

    public LinkSpanDrawable(S span, Theme.ResourcesProvider resourcesProvider, float touchX, float touchY, boolean supportsLongPress) {
        long j;
        this.mPathes = new ArrayList<>();
        this.mPathesCount = 0;
        this.circlePath = new Path();
        this.mStart = -1L;
        this.mReleaseStart = -1L;
        this.selectionAlpha = 0.2f;
        this.rippleAlpha = 0.8f;
        this.mSpan = span;
        this.mResourcesProvider = resourcesProvider;
        setColor(getThemedColor(Theme.key_chat_linkSelectBackground));
        this.mTouchX = touchX;
        this.mTouchY = touchY;
        long tapTimeout = ViewConfiguration.getTapTimeout();
        this.mLongPressDuration = ViewConfiguration.getLongPressTimeout();
        this.mDuration = Math.min(((float) tapTimeout) * 1.8f, ((float) j) * 0.8f);
        this.mSupportsLongPress = false;
    }

    public void setColor(int color) {
        this.color = color;
        Paint paint = this.mSelectionPaint;
        if (paint != null) {
            paint.setColor(color);
            this.mSelectionAlpha = this.mSelectionPaint.getAlpha();
        }
        Paint paint2 = this.mRipplePaint;
        if (paint2 != null) {
            paint2.setColor(color);
            this.mRippleAlpha = this.mRipplePaint.getAlpha();
        }
    }

    public void release() {
        this.mReleaseStart = Math.max(this.mStart + this.mDuration, SystemClock.elapsedRealtime());
    }

    public LinkPath obtainNewPath() {
        LinkPath linkPath;
        ArrayList<LinkPath> arrayList = pathCache;
        if (!arrayList.isEmpty()) {
            linkPath = arrayList.remove(0);
        } else {
            linkPath = new LinkPath(true);
        }
        linkPath.reset();
        this.mPathes.add(linkPath);
        this.mPathesCount = this.mPathes.size();
        return linkPath;
    }

    public void reset() {
        if (this.mPathes.isEmpty()) {
            return;
        }
        pathCache.addAll(this.mPathes);
        this.mPathes.clear();
        this.mPathesCount = 0;
    }

    public S getSpan() {
        return this.mSpan;
    }

    public boolean draw(Canvas canvas) {
        float longPress;
        float longPress2;
        boolean cornerRadiusUpdate = this.cornerRadius != AndroidUtilities.dp(4.0f);
        if (this.mSelectionPaint == null || cornerRadiusUpdate) {
            Paint paint = new Paint(1);
            this.mSelectionPaint = paint;
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            this.mSelectionPaint.setColor(this.color);
            this.mSelectionAlpha = this.mSelectionPaint.getAlpha();
            Paint paint2 = this.mSelectionPaint;
            int dp = AndroidUtilities.dp(4.0f);
            this.cornerRadius = dp;
            paint2.setPathEffect(new CornerPathEffect(dp));
        }
        if (this.mRipplePaint == null || cornerRadiusUpdate) {
            Paint paint3 = new Paint(1);
            this.mRipplePaint = paint3;
            paint3.setStyle(Paint.Style.FILL_AND_STROKE);
            this.mRipplePaint.setColor(this.color);
            this.mRippleAlpha = this.mRipplePaint.getAlpha();
            Paint paint4 = this.mRipplePaint;
            int dp2 = AndroidUtilities.dp(4.0f);
            this.cornerRadius = dp2;
            paint4.setPathEffect(new CornerPathEffect(dp2));
        }
        if (this.mBounds == null && this.mPathesCount > 0) {
            this.mPathes.get(0).computeBounds(AndroidUtilities.rectTmp, false);
            this.mBounds = new android.graphics.Rect((int) AndroidUtilities.rectTmp.left, (int) AndroidUtilities.rectTmp.top, (int) AndroidUtilities.rectTmp.right, (int) AndroidUtilities.rectTmp.bottom);
            for (int i = 1; i < this.mPathesCount; i++) {
                this.mPathes.get(i).computeBounds(AndroidUtilities.rectTmp, false);
                android.graphics.Rect rect = this.mBounds;
                rect.left = Math.min(rect.left, (int) AndroidUtilities.rectTmp.left);
                android.graphics.Rect rect2 = this.mBounds;
                rect2.top = Math.min(rect2.top, (int) AndroidUtilities.rectTmp.top);
                android.graphics.Rect rect3 = this.mBounds;
                rect3.right = Math.max(rect3.right, (int) AndroidUtilities.rectTmp.right);
                android.graphics.Rect rect4 = this.mBounds;
                rect4.bottom = Math.max(rect4.bottom, (int) AndroidUtilities.rectTmp.bottom);
            }
            this.mMaxRadius = (float) Math.sqrt(Math.max(Math.max(Math.pow(this.mBounds.left - this.mTouchX, 2.0d) + Math.pow(this.mBounds.top - this.mTouchY, 2.0d), Math.pow(this.mBounds.right - this.mTouchX, 2.0d) + Math.pow(this.mBounds.top - this.mTouchY, 2.0d)), Math.max(Math.pow(this.mBounds.left - this.mTouchX, 2.0d) + Math.pow(this.mBounds.bottom - this.mTouchY, 2.0d), Math.pow(this.mBounds.right - this.mTouchX, 2.0d) + Math.pow(this.mBounds.bottom - this.mTouchY, 2.0d))));
        }
        long now = SystemClock.elapsedRealtime();
        if (this.mStart < 0) {
            this.mStart = now;
        }
        float pressT = CubicBezierInterpolator.DEFAULT.getInterpolation(Math.min(1.0f, ((float) (now - this.mStart)) / ((float) this.mDuration)));
        long j = this.mReleaseStart;
        float releaseT = j < 0 ? 0.0f : Math.min(1.0f, Math.max(0.0f, ((float) ((now - mReleaseDelay) - j)) / 100.0f));
        if (this.mSupportsLongPress) {
            long j2 = this.mDuration;
            float longPress3 = Math.max(0.0f, ((float) ((now - this.mStart) - (j2 * 2))) / ((float) (this.mLongPressDuration - (j2 * 2))));
            if (longPress3 > 1.0f) {
                longPress2 = 1.0f - (((float) ((now - this.mStart) - this.mLongPressDuration)) / ((float) this.mDuration));
            } else {
                longPress2 = longPress3 * 0.5f;
            }
            longPress = longPress2 * (1.0f - releaseT);
        } else {
            longPress = 1.0f;
        }
        this.mSelectionPaint.setAlpha((int) (this.mSelectionAlpha * 0.2f * Math.min(1.0f, pressT * 5.0f) * (1.0f - releaseT)));
        this.mSelectionPaint.setStrokeWidth(Math.min(1.0f, 1.0f - longPress) * AndroidUtilities.dp(5.0f));
        for (int i2 = 0; i2 < this.mPathesCount; i2++) {
            canvas.drawPath(this.mPathes.get(i2), this.mSelectionPaint);
        }
        this.mRipplePaint.setAlpha((int) (this.mRippleAlpha * 0.8f * (1.0f - releaseT)));
        this.mRipplePaint.setStrokeWidth(Math.min(1.0f, 1.0f - longPress) * AndroidUtilities.dp(5.0f));
        if (pressT < 1.0f) {
            float r = this.mMaxRadius * pressT;
            canvas.save();
            this.circlePath.reset();
            this.circlePath.addCircle(this.mTouchX, this.mTouchY, r, Path.Direction.CW);
            canvas.clipPath(this.circlePath);
            for (int i3 = 0; i3 < this.mPathesCount; i3++) {
                canvas.drawPath(this.mPathes.get(i3), this.mRipplePaint);
            }
            canvas.restore();
        } else {
            for (int i4 = 0; i4 < this.mPathesCount; i4++) {
                canvas.drawPath(this.mPathes.get(i4), this.mRipplePaint);
            }
        }
        int i5 = (pressT > 1.0f ? 1 : (pressT == 1.0f ? 0 : -1));
        return i5 < 0 || this.mReleaseStart >= 0 || (this.mSupportsLongPress && now - this.mStart < this.mLongPressDuration + this.mDuration);
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.mResourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    /* loaded from: classes5.dex */
    public static class LinkCollector {
        private ArrayList<Pair<LinkSpanDrawable, Object>> mLinks = new ArrayList<>();
        private int mLinksCount = 0;
        private View mParent;

        public LinkCollector() {
        }

        public LinkCollector(View parentView) {
            this.mParent = parentView;
        }

        public void addLink(LinkSpanDrawable link) {
            addLink(link, null);
        }

        public void addLink(LinkSpanDrawable link, Object obj) {
            this.mLinks.add(new Pair<>(link, obj));
            this.mLinksCount++;
            invalidate(obj);
        }

        public void removeLink(LinkSpanDrawable link) {
            removeLink(link, true);
        }

        public void removeLink(final LinkSpanDrawable link, boolean animated) {
            if (link == null) {
                return;
            }
            Pair<LinkSpanDrawable, Object> pair = null;
            int i = 0;
            while (true) {
                if (i >= this.mLinksCount) {
                    break;
                } else if (this.mLinks.get(i).first != link) {
                    i++;
                } else {
                    Pair<LinkSpanDrawable, Object> pair2 = this.mLinks.get(i);
                    pair = pair2;
                    break;
                }
            }
            if (pair == null) {
                return;
            }
            if (animated) {
                if (link.mReleaseStart < 0) {
                    link.release();
                    invalidate(pair.second);
                    long now = SystemClock.elapsedRealtime();
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.LinkSpanDrawable$LinkCollector$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            LinkSpanDrawable.LinkCollector.this.m2758xc5242755(link);
                        }
                    }, Math.max(0L, (link.mReleaseStart - now) + LinkSpanDrawable.mReleaseDelay + LinkSpanDrawable.mReleaseDuration));
                    return;
                }
                return;
            }
            this.mLinks.remove(pair);
            link.reset();
            this.mLinksCount = this.mLinks.size();
            invalidate(pair.second);
        }

        /* renamed from: lambda$removeLink$0$org-telegram-ui-Components-LinkSpanDrawable$LinkCollector */
        public /* synthetic */ void m2758xc5242755(LinkSpanDrawable link) {
            removeLink(link, false);
        }

        private void removeLink(int index, boolean animated) {
            if (index < 0 || index >= this.mLinksCount) {
                return;
            }
            if (animated) {
                Pair<LinkSpanDrawable, Object> pair = this.mLinks.get(index);
                final LinkSpanDrawable link = (LinkSpanDrawable) pair.first;
                if (link.mReleaseStart < 0) {
                    link.release();
                    invalidate(pair.second);
                    long now = SystemClock.elapsedRealtime();
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.LinkSpanDrawable$LinkCollector$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            LinkSpanDrawable.LinkCollector.this.m2759xd5d9f416(link);
                        }
                    }, Math.max(0L, (link.mReleaseStart - now) + LinkSpanDrawable.mReleaseDelay + LinkSpanDrawable.mReleaseDuration));
                    return;
                }
                return;
            }
            Pair<LinkSpanDrawable, Object> pair2 = this.mLinks.remove(index);
            ((LinkSpanDrawable) pair2.first).reset();
            this.mLinksCount = this.mLinks.size();
            invalidate(pair2.second);
        }

        /* renamed from: lambda$removeLink$1$org-telegram-ui-Components-LinkSpanDrawable$LinkCollector */
        public /* synthetic */ void m2759xd5d9f416(LinkSpanDrawable link) {
            removeLink(link, false);
        }

        public void clear() {
            clear(true);
        }

        public void clear(boolean animated) {
            if (animated) {
                for (int i = 0; i < this.mLinksCount; i++) {
                    removeLink(i, true);
                }
            } else if (this.mLinksCount > 0) {
                for (int i2 = 0; i2 < this.mLinksCount; i2++) {
                    ((LinkSpanDrawable) this.mLinks.get(i2).first).reset();
                    invalidate(this.mLinks.get(i2).second, false);
                }
                this.mLinks.clear();
                this.mLinksCount = 0;
                invalidate();
            }
        }

        public void removeLinks(Object obj) {
            removeLinks(obj, true);
        }

        public void removeLinks(Object obj, boolean animated) {
            for (int i = 0; i < this.mLinksCount; i++) {
                if (this.mLinks.get(i).second == obj) {
                    removeLink(i, animated);
                }
            }
        }

        public boolean draw(Canvas canvas) {
            boolean invalidate = false;
            for (int i = 0; i < this.mLinksCount; i++) {
                invalidate = ((LinkSpanDrawable) this.mLinks.get(i).first).draw(canvas) || invalidate;
            }
            return invalidate;
        }

        public boolean draw(Canvas canvas, Object obj) {
            boolean invalidate = false;
            int i = 0;
            while (true) {
                boolean z = false;
                if (i < this.mLinksCount) {
                    if (this.mLinks.get(i).second == obj) {
                        if (((LinkSpanDrawable) this.mLinks.get(i).first).draw(canvas) || invalidate) {
                            z = true;
                        }
                        invalidate = z;
                    }
                    i++;
                } else {
                    invalidate(obj, false);
                    return invalidate;
                }
            }
        }

        public boolean isEmpty() {
            return this.mLinksCount <= 0;
        }

        private void invalidate() {
            invalidate(null, true);
        }

        private void invalidate(Object obj) {
            invalidate(obj, true);
        }

        private void invalidate(Object obj, boolean tryParent) {
            View view;
            if (obj instanceof View) {
                ((View) obj).invalidate();
            } else if (obj instanceof ArticleViewer.DrawingText) {
                ArticleViewer.DrawingText text = (ArticleViewer.DrawingText) obj;
                if (text.latestParentView != null) {
                    text.latestParentView.invalidate();
                }
            } else if (tryParent && (view = this.mParent) != null) {
                view.invalidate();
            }
        }
    }

    /* loaded from: classes5.dex */
    public static class LinksTextView extends TextView {
        private boolean isCustomLinkCollector;
        private LinkCollector links;
        private OnLinkPress onLongPressListener;
        private OnLinkPress onPressListener;
        private LinkSpanDrawable<ClickableSpan> pressedLink;
        private Theme.ResourcesProvider resourcesProvider;

        /* loaded from: classes5.dex */
        public interface OnLinkPress {
            void run(ClickableSpan clickableSpan);
        }

        public LinksTextView(Context context) {
            this(context, null);
        }

        public LinksTextView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.isCustomLinkCollector = false;
            this.links = new LinkCollector(this);
            this.resourcesProvider = resourcesProvider;
        }

        public LinksTextView(Context context, LinkCollector customLinkCollector, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.isCustomLinkCollector = true;
            this.links = customLinkCollector;
            this.resourcesProvider = resourcesProvider;
        }

        public void setOnLinkPressListener(OnLinkPress listener) {
            this.onPressListener = listener;
        }

        public void setOnLinkLongPressListener(OnLinkPress listener) {
            this.onLongPressListener = listener;
        }

        @Override // android.widget.TextView, android.view.View
        public boolean onTouchEvent(MotionEvent event) {
            if (this.links != null) {
                Layout textLayout = getLayout();
                int x = (int) (event.getX() - getPaddingLeft());
                int y = (int) (event.getY() - getPaddingTop());
                int line = textLayout.getLineForVertical(y);
                int off = textLayout.getOffsetForHorizontal(line, x);
                float left = getLayout().getLineLeft(line);
                ClickableSpan span = null;
                if (left <= x && textLayout.getLineWidth(line) + left >= x && y >= 0 && y <= textLayout.getHeight()) {
                    Spannable buffer = new SpannableString(textLayout.getText());
                    final ClickableSpan[] spans = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                    if (spans.length != 0 && !AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
                        span = spans[0];
                        if (event.getAction() == 0) {
                            LinkSpanDrawable<ClickableSpan> linkSpanDrawable = new LinkSpanDrawable<>(span, this.resourcesProvider, event.getX(), event.getY());
                            this.pressedLink = linkSpanDrawable;
                            this.links.addLink(linkSpanDrawable);
                            int start = buffer.getSpanStart(this.pressedLink.getSpan());
                            int end = buffer.getSpanEnd(this.pressedLink.getSpan());
                            LinkPath path = this.pressedLink.obtainNewPath();
                            path.setCurrentLayout(textLayout, start, getPaddingTop());
                            textLayout.getSelectionPath(start, end, path);
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.LinkSpanDrawable$LinksTextView$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    LinkSpanDrawable.LinksTextView.this.m2760x6c90bbe9(spans);
                                }
                            }, ViewConfiguration.getLongPressTimeout());
                            return true;
                        }
                    }
                }
                if (event.getAction() == 1) {
                    this.links.clear();
                    LinkSpanDrawable<ClickableSpan> linkSpanDrawable2 = this.pressedLink;
                    if (linkSpanDrawable2 != null && linkSpanDrawable2.getSpan() == span) {
                        OnLinkPress onLinkPress = this.onPressListener;
                        if (onLinkPress != null) {
                            onLinkPress.run(this.pressedLink.getSpan());
                        } else if (this.pressedLink.getSpan() != null) {
                            this.pressedLink.getSpan().onClick(this);
                        }
                    }
                    this.pressedLink = null;
                    return true;
                } else if (event.getAction() == 3) {
                    this.links.clear();
                    this.pressedLink = null;
                    return true;
                }
            }
            return this.pressedLink != null || super.onTouchEvent(event);
        }

        /* renamed from: lambda$onTouchEvent$0$org-telegram-ui-Components-LinkSpanDrawable$LinksTextView */
        public /* synthetic */ void m2760x6c90bbe9(ClickableSpan[] spans) {
            OnLinkPress onLinkPress = this.onLongPressListener;
            if (onLinkPress != null) {
                onLinkPress.run(spans[0]);
                this.pressedLink = null;
                this.links.clear();
            }
        }

        @Override // android.widget.TextView, android.view.View
        public void onDraw(Canvas canvas) {
            if (!this.isCustomLinkCollector && this.links.draw(canvas)) {
                invalidate();
            }
            super.onDraw(canvas);
        }
    }
}
