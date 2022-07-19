package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.ReplacementSpan;
import android.util.LongSparseArray;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
/* loaded from: classes3.dex */
public class AnimatedEmojiSpan extends ReplacementSpan {
    public int cacheType;
    public TLRPC$Document document;
    public long documentId;
    private Paint.FontMetricsInt fontMetrics;
    float lastDrawnCx;
    float lastDrawnCy;
    int measuredSize;
    private float scale;
    private float size;
    boolean spanDrawn;

    public AnimatedEmojiSpan(TLRPC$Document tLRPC$Document, Paint.FontMetricsInt fontMetricsInt) {
        this(tLRPC$Document.id, 1.2f, fontMetricsInt);
        this.document = tLRPC$Document;
    }

    public AnimatedEmojiSpan(long j, Paint.FontMetricsInt fontMetricsInt) {
        this(j, 1.2f, fontMetricsInt);
    }

    public AnimatedEmojiSpan(long j, float f, Paint.FontMetricsInt fontMetricsInt) {
        this.size = AndroidUtilities.dp(20.0f);
        this.cacheType = -1;
        this.documentId = j;
        this.scale = f;
        this.fontMetrics = fontMetricsInt;
        if (fontMetricsInt != null) {
            float abs = Math.abs(fontMetricsInt.descent) + Math.abs(fontMetricsInt.ascent);
            this.size = abs;
            if (abs != 0.0f) {
                return;
            }
            this.size = AndroidUtilities.dp(20.0f);
        }
    }

    public long getDocumentId() {
        return this.documentId;
    }

    public void replaceFontMetrics(Paint.FontMetricsInt fontMetricsInt, int i, int i2) {
        this.cacheType = i2;
    }

    @Override // android.text.style.ReplacementSpan
    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        Paint.FontMetricsInt fontMetricsInt2 = this.fontMetrics;
        if (fontMetricsInt2 == null) {
            int i3 = (int) this.size;
            int dp = AndroidUtilities.dp(8.0f);
            int dp2 = AndroidUtilities.dp(10.0f);
            if (fontMetricsInt != null) {
                float f = (-dp2) - dp;
                float f2 = this.scale;
                fontMetricsInt.top = (int) (f * f2);
                float f3 = dp2 - dp;
                fontMetricsInt.bottom = (int) (f3 * f2);
                fontMetricsInt.ascent = (int) (f * f2);
                fontMetricsInt.descent = (int) (f3 * f2);
                fontMetricsInt.leading = 0;
            }
            this.measuredSize = (int) (i3 * this.scale);
        } else {
            if (fontMetricsInt != null) {
                fontMetricsInt.ascent = fontMetricsInt2.ascent;
                fontMetricsInt.descent = fontMetricsInt2.descent;
                fontMetricsInt.top = fontMetricsInt2.top;
                fontMetricsInt.bottom = fontMetricsInt2.bottom;
            }
            this.measuredSize = (int) (this.size * this.scale);
        }
        return this.measuredSize;
    }

    @Override // android.text.style.ReplacementSpan
    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        this.spanDrawn = true;
        this.lastDrawnCx = f + (this.measuredSize / 2.0f);
        this.lastDrawnCy = i3 + ((i5 - i3) / 2.0f);
    }

    public static void drawAnimatedEmojis(Canvas canvas, Layout layout, EmojiGroupedSpans emojiGroupedSpans, float f, List<SpoilerEffect> list, float f2, float f3, float f4, float f5) {
        boolean z;
        float f6;
        float f7;
        if (canvas == null || layout == null || emojiGroupedSpans == null) {
            return;
        }
        if (emojiGroupedSpans.updatePositions) {
            emojiGroupedSpans.updatePositions = false;
            if (layout.getText() instanceof Spanned) {
                Spanned spanned = (Spanned) layout.getText();
                AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spanned.getSpans(0, spanned.length(), AnimatedEmojiSpan.class);
                SpansChunk spansChunk = emojiGroupedSpans.groupedByLayout.get(layout);
                if (spansChunk != null) {
                    for (int i = 0; animatedEmojiSpanArr != null && i < animatedEmojiSpanArr.length; i++) {
                        AnimatedEmojiSpan animatedEmojiSpan = animatedEmojiSpanArr[i];
                        if (animatedEmojiSpan != null) {
                            AnimatedEmojiHolder animatedEmojiHolder = null;
                            for (int i2 = 0; i2 < spansChunk.holders.size(); i2++) {
                                if (spansChunk.holders.get(i2).span == animatedEmojiSpan) {
                                    animatedEmojiHolder = spansChunk.holders.get(i2);
                                }
                            }
                            if (animatedEmojiHolder != null) {
                                float f8 = animatedEmojiSpan.measuredSize / 2.0f;
                                if (animatedEmojiSpan.spanDrawn) {
                                    f6 = animatedEmojiSpan.lastDrawnCx;
                                    f7 = animatedEmojiSpan.lastDrawnCy;
                                } else {
                                    int spanStart = spanned.getSpanStart(animatedEmojiSpan);
                                    int spanEnd = spanned.getSpanEnd(animatedEmojiSpan);
                                    float primaryHorizontal = layout.getPrimaryHorizontal(spanStart);
                                    float primaryHorizontal2 = layout.getPrimaryHorizontal(spanEnd);
                                    int lineForOffset = layout.getLineForOffset(spanStart);
                                    if (lineForOffset != layout.getLineForOffset(spanEnd)) {
                                        primaryHorizontal2 = primaryHorizontal + (animatedEmojiSpan.size * animatedEmojiSpan.scale);
                                    }
                                    int lineBaseline = layout.getLineBaseline(lineForOffset);
                                    f6 = (primaryHorizontal + primaryHorizontal2) / 2.0f;
                                    animatedEmojiSpan.lastDrawnCx = f6;
                                    float f9 = lineBaseline - (0.4f * f8);
                                    animatedEmojiSpan.lastDrawnCy = f9;
                                    animatedEmojiSpan.spanDrawn = true;
                                    f7 = f9;
                                }
                                animatedEmojiHolder.drawableBounds.set((int) (f6 - f8), (int) (f7 - f8), (int) (f6 + f8), (int) (f7 + f8));
                            }
                        }
                    }
                }
            }
        }
        if (Emoji.emojiDrawingYOffset == 0.0f && f == 0.0f) {
            z = false;
        } else {
            canvas.save();
            canvas.translate(0.0f, Emoji.emojiDrawingYOffset + AndroidUtilities.dp(20.0f * f));
            z = true;
        }
        long currentTimeMillis = System.currentTimeMillis();
        int i3 = 0;
        while (true) {
            if (i3 >= emojiGroupedSpans.backgroundDrawingArray.size()) {
                break;
            }
            SpansChunk spansChunk2 = emojiGroupedSpans.backgroundDrawingArray.get(i3);
            if (spansChunk2.layout == layout) {
                spansChunk2.draw(canvas, list, currentTimeMillis, f2, f3, f4, f5);
                break;
            }
            i3++;
        }
        if (!z) {
            return;
        }
        canvas.restore();
    }

    /* loaded from: classes3.dex */
    public static class AnimatedEmojiHolder {
        public float alpha;
        ImageReceiver.BackgroundThreadDrawHolder backgroundDrawHolder;
        public AnimatedEmojiDrawable drawable;
        public android.graphics.Rect drawableBounds;
        public float drawingYOffset;
        private final boolean invalidateInParent;
        public Layout layout;
        public boolean skipDraw;
        public AnimatedEmojiSpan span;
        public SpansChunk spansChunk;
        private final View view;

        public AnimatedEmojiHolder(View view, boolean z) {
            this.view = view;
            this.invalidateInParent = z;
        }

        public boolean outOfBounds(float f, float f2) {
            android.graphics.Rect rect = this.drawableBounds;
            return ((float) rect.bottom) < f || ((float) rect.top) > f2;
        }

        public void prepareForBackgroundDraw(long j) {
            AnimatedEmojiDrawable animatedEmojiDrawable = this.drawable;
            if (animatedEmojiDrawable == null) {
                return;
            }
            ImageReceiver imageReceiver = animatedEmojiDrawable.getImageReceiver();
            this.drawable.update(j);
            this.drawable.setBounds(this.drawableBounds);
            if (imageReceiver == null) {
                return;
            }
            imageReceiver.setAlpha(this.alpha);
            imageReceiver.setImageCoords(this.drawableBounds);
            ImageReceiver.BackgroundThreadDrawHolder drawInBackgroundThread = imageReceiver.setDrawInBackgroundThread(this.backgroundDrawHolder);
            this.backgroundDrawHolder = drawInBackgroundThread;
            drawInBackgroundThread.time = j;
            drawInBackgroundThread.setBounds(this.drawableBounds);
        }

        public void releaseDrawInBackground() {
            ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder = this.backgroundDrawHolder;
            if (backgroundThreadDrawHolder != null) {
                backgroundThreadDrawHolder.release();
            }
        }

        public void draw(Canvas canvas, long j, float f, float f2, float f3) {
            if ((f != 0.0f || f2 != 0.0f) && outOfBounds(f, f2)) {
                this.skipDraw = true;
                return;
            }
            this.skipDraw = false;
            if (this.drawable.getImageReceiver() == null) {
                return;
            }
            this.drawable.setTime(j);
            this.drawable.draw(canvas, this.drawableBounds, f3 * this.alpha);
            this.drawable.setTime(0L);
        }

        public void invalidate() {
            View view = this.view;
            if (view != null) {
                if (this.invalidateInParent && view.getParent() != null) {
                    ((View) this.view.getParent()).invalidate();
                } else {
                    this.view.invalidate();
                }
            }
        }
    }

    public static EmojiGroupedSpans update(int i, View view, EmojiGroupedSpans emojiGroupedSpans, ArrayList<MessageObject.TextLayoutBlock> arrayList) {
        return update(i, view, false, emojiGroupedSpans, arrayList);
    }

    public static EmojiGroupedSpans update(int i, View view, boolean z, EmojiGroupedSpans emojiGroupedSpans, ArrayList<MessageObject.TextLayoutBlock> arrayList) {
        Layout[] layoutArr = new Layout[arrayList == null ? 0 : arrayList.size()];
        if (arrayList != null) {
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                layoutArr[i2] = arrayList.get(i2).textLayout;
            }
        }
        return update(i, view, z, emojiGroupedSpans, layoutArr);
    }

    public static EmojiGroupedSpans update(int i, View view, EmojiGroupedSpans emojiGroupedSpans, Layout... layoutArr) {
        return update(i, view, false, emojiGroupedSpans, layoutArr);
    }

    public static EmojiGroupedSpans update(int i, View view, boolean z, EmojiGroupedSpans emojiGroupedSpans, Layout... layoutArr) {
        boolean z2;
        AnimatedEmojiSpan[] animatedEmojiSpanArr;
        boolean z3;
        AnimatedEmojiHolder animatedEmojiHolder;
        EmojiGroupedSpans emojiGroupedSpans2 = emojiGroupedSpans;
        if (layoutArr == null || layoutArr.length <= 0) {
            if (emojiGroupedSpans2 != null) {
                emojiGroupedSpans2.holders.clear();
                emojiGroupedSpans.release();
            }
            return null;
        }
        for (Layout layout : layoutArr) {
            if (layout == null || !(layout.getText() instanceof Spanned)) {
                animatedEmojiSpanArr = null;
            } else {
                Spanned spanned = (Spanned) layout.getText();
                animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spanned.getSpans(0, spanned.length(), AnimatedEmojiSpan.class);
                for (int i2 = 0; animatedEmojiSpanArr != null && i2 < animatedEmojiSpanArr.length; i2++) {
                    AnimatedEmojiSpan animatedEmojiSpan = animatedEmojiSpanArr[i2];
                    if (animatedEmojiSpan != null) {
                        if (emojiGroupedSpans2 == null) {
                            emojiGroupedSpans2 = new EmojiGroupedSpans();
                        }
                        int i3 = 0;
                        while (true) {
                            if (i3 >= emojiGroupedSpans2.holders.size()) {
                                animatedEmojiHolder = null;
                                break;
                            } else if (emojiGroupedSpans2.holders.get(i3).span == animatedEmojiSpan && emojiGroupedSpans2.holders.get(i3).layout == layout) {
                                animatedEmojiHolder = emojiGroupedSpans2.holders.get(i3);
                                break;
                            } else {
                                i3++;
                            }
                        }
                        if (animatedEmojiHolder == null) {
                            AnimatedEmojiHolder animatedEmojiHolder2 = new AnimatedEmojiHolder(view, z);
                            animatedEmojiHolder2.layout = layout;
                            TLRPC$Document tLRPC$Document = animatedEmojiSpan.document;
                            if (tLRPC$Document != null) {
                                int i4 = animatedEmojiSpan.cacheType;
                                if (i4 < 0) {
                                    i4 = i;
                                }
                                animatedEmojiHolder2.drawable = AnimatedEmojiDrawable.make(i4, tLRPC$Document);
                            } else {
                                int i5 = animatedEmojiSpan.cacheType;
                                if (i5 < 0) {
                                    i5 = i;
                                }
                                animatedEmojiHolder2.drawable = AnimatedEmojiDrawable.make(i5, animatedEmojiSpan.documentId);
                            }
                            animatedEmojiHolder2.drawableBounds = new android.graphics.Rect();
                            animatedEmojiHolder2.span = animatedEmojiSpan;
                            emojiGroupedSpans2.add(layout, animatedEmojiHolder2);
                        }
                    }
                }
            }
            if (emojiGroupedSpans2 != null) {
                int i6 = 0;
                while (i6 < emojiGroupedSpans2.holders.size()) {
                    if (emojiGroupedSpans2.holders.get(i6).layout == layout) {
                        AnimatedEmojiSpan animatedEmojiSpan2 = emojiGroupedSpans2.holders.get(i6).span;
                        for (int i7 = 0; animatedEmojiSpanArr != null && i7 < animatedEmojiSpanArr.length; i7++) {
                            if (animatedEmojiSpanArr[i7] == animatedEmojiSpan2) {
                                z3 = true;
                                break;
                            }
                        }
                        z3 = false;
                        if (!z3) {
                            emojiGroupedSpans2.remove(i6);
                            i6--;
                        }
                    }
                    i6++;
                }
            }
        }
        if (emojiGroupedSpans2 != null) {
            int i8 = 0;
            while (i8 < emojiGroupedSpans2.holders.size()) {
                Layout layout2 = emojiGroupedSpans2.holders.get(i8).layout;
                int i9 = 0;
                while (true) {
                    if (i9 >= layoutArr.length) {
                        z2 = false;
                        break;
                    } else if (layoutArr[i9] == layout2) {
                        z2 = true;
                        break;
                    } else {
                        i9++;
                    }
                }
                if (!z2) {
                    emojiGroupedSpans2.remove(i8);
                    i8--;
                }
                i8++;
            }
            emojiGroupedSpans2.updatePositions = true;
        }
        return emojiGroupedSpans2;
    }

    public static LongSparseArray<AnimatedEmojiDrawable> update(int i, View view, AnimatedEmojiSpan[] animatedEmojiSpanArr, LongSparseArray<AnimatedEmojiDrawable> longSparseArray) {
        AnimatedEmojiDrawable animatedEmojiDrawable;
        boolean z;
        if (animatedEmojiSpanArr == null) {
            return longSparseArray;
        }
        if (longSparseArray == null) {
            longSparseArray = new LongSparseArray<>();
        }
        int i2 = 0;
        while (i2 < longSparseArray.size()) {
            long keyAt = longSparseArray.keyAt(i2);
            AnimatedEmojiDrawable animatedEmojiDrawable2 = longSparseArray.get(keyAt);
            if (animatedEmojiDrawable2 == null) {
                longSparseArray.remove(keyAt);
            } else {
                int i3 = 0;
                while (true) {
                    if (i3 >= animatedEmojiSpanArr.length) {
                        z = false;
                        break;
                    } else if (animatedEmojiSpanArr[i3] != null && animatedEmojiSpanArr[i3].getDocumentId() == keyAt) {
                        z = true;
                        break;
                    } else {
                        i3++;
                    }
                }
                if (!z) {
                    animatedEmojiDrawable2.removeView(view);
                    longSparseArray.remove(keyAt);
                } else {
                    i2++;
                }
            }
            i2--;
            i2++;
        }
        for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
            if (animatedEmojiSpan != null && longSparseArray.get(animatedEmojiSpan.getDocumentId()) == null) {
                TLRPC$Document tLRPC$Document = animatedEmojiSpan.document;
                if (tLRPC$Document != null) {
                    int i4 = animatedEmojiSpan.cacheType;
                    if (i4 < 0) {
                        i4 = i;
                    }
                    animatedEmojiDrawable = AnimatedEmojiDrawable.make(i4, tLRPC$Document);
                } else {
                    int i5 = animatedEmojiSpan.cacheType;
                    if (i5 < 0) {
                        i5 = i;
                    }
                    animatedEmojiDrawable = AnimatedEmojiDrawable.make(i5, animatedEmojiSpan.documentId);
                }
                animatedEmojiDrawable.addView(view);
                longSparseArray.put(animatedEmojiSpan.getDocumentId(), animatedEmojiDrawable);
            }
        }
        return longSparseArray;
    }

    public static void release(View view, LongSparseArray<AnimatedEmojiDrawable> longSparseArray) {
        if (longSparseArray == null) {
            return;
        }
        for (int i = 0; i < longSparseArray.size(); i++) {
            AnimatedEmojiDrawable valueAt = longSparseArray.valueAt(i);
            if (valueAt != null) {
                valueAt.removeView(view);
            }
        }
        longSparseArray.clear();
    }

    public static void release(View view, EmojiGroupedSpans emojiGroupedSpans) {
        if (emojiGroupedSpans == null) {
            return;
        }
        emojiGroupedSpans.release();
    }

    /* loaded from: classes3.dex */
    public static class EmojiGroupedSpans {
        public boolean updatePositions;
        public ArrayList<AnimatedEmojiHolder> holders = new ArrayList<>();
        HashMap<Layout, SpansChunk> groupedByLayout = new HashMap<>();
        ArrayList<SpansChunk> backgroundDrawingArray = new ArrayList<>();

        public void add(Layout layout, AnimatedEmojiHolder animatedEmojiHolder) {
            this.holders.add(animatedEmojiHolder);
            SpansChunk spansChunk = this.groupedByLayout.get(layout);
            if (spansChunk == null) {
                spansChunk = new SpansChunk(animatedEmojiHolder.view, layout, animatedEmojiHolder.invalidateInParent);
                this.groupedByLayout.put(layout, spansChunk);
                this.backgroundDrawingArray.add(spansChunk);
            }
            spansChunk.add(animatedEmojiHolder);
            animatedEmojiHolder.drawable.addView(animatedEmojiHolder);
        }

        public void remove(int i) {
            AnimatedEmojiHolder animatedEmojiHolder = this.holders.get(i);
            this.holders.remove(i);
            SpansChunk spansChunk = this.groupedByLayout.get(animatedEmojiHolder.layout);
            if (spansChunk != null) {
                spansChunk.remove(animatedEmojiHolder);
                if (spansChunk.holders.isEmpty()) {
                    this.groupedByLayout.remove(animatedEmojiHolder.layout);
                    this.backgroundDrawingArray.remove(spansChunk);
                }
                animatedEmojiHolder.drawable.removeView(animatedEmojiHolder);
                return;
            }
            throw new RuntimeException("!!!");
        }

        public void release() {
            while (this.holders.size() > 0) {
                remove(0);
            }
        }

        public EmojiGroupedSpans clone() {
            EmojiGroupedSpans emojiGroupedSpans = new EmojiGroupedSpans();
            emojiGroupedSpans.updatePositions = this.updatePositions;
            emojiGroupedSpans.holders.addAll(this.holders);
            for (Map.Entry<Layout, SpansChunk> entry : emojiGroupedSpans.groupedByLayout.entrySet()) {
                SpansChunk clone = entry.getValue().clone();
                emojiGroupedSpans.groupedByLayout.put(entry.getKey(), clone);
                emojiGroupedSpans.backgroundDrawingArray.add(clone);
            }
            return emojiGroupedSpans;
        }
    }

    /* loaded from: classes3.dex */
    public static class SpansChunk {
        private boolean allowBackgroundRendering;
        DrawingInBackgroundThreadDrawable backgroundThreadDrawable;
        ArrayList<AnimatedEmojiHolder> holders = new ArrayList<>();
        final Layout layout;
        final View view;

        public SpansChunk(View view, Layout layout, boolean z) {
            this.layout = layout;
            this.view = view;
            this.allowBackgroundRendering = z;
        }

        public void add(AnimatedEmojiHolder animatedEmojiHolder) {
            this.holders.add(animatedEmojiHolder);
            animatedEmojiHolder.spansChunk = this;
            checkBackgroundRendering();
        }

        public void remove(AnimatedEmojiHolder animatedEmojiHolder) {
            this.holders.remove(animatedEmojiHolder);
            animatedEmojiHolder.spansChunk = null;
            checkBackgroundRendering();
        }

        private void checkBackgroundRendering() {
            DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable;
            if (this.allowBackgroundRendering && this.holders.size() >= 10 && this.backgroundThreadDrawable == null) {
                DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable2 = new DrawingInBackgroundThreadDrawable() { // from class: org.telegram.ui.Components.AnimatedEmojiSpan.SpansChunk.1
                    private final ArrayList<AnimatedEmojiHolder> backgroundHolders = new ArrayList<>();

                    @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                    public void drawInBackground(Canvas canvas) {
                        ImageReceiver.BackgroundThreadDrawHolder backgroundThreadDrawHolder;
                        for (int i = 0; i < this.backgroundHolders.size(); i++) {
                            AnimatedEmojiHolder animatedEmojiHolder = this.backgroundHolders.get(i);
                            if (animatedEmojiHolder != null && (backgroundThreadDrawHolder = animatedEmojiHolder.backgroundDrawHolder) != null) {
                                animatedEmojiHolder.drawable.draw(canvas, backgroundThreadDrawHolder);
                            }
                        }
                    }

                    @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                    public void drawInUiThread(Canvas canvas) {
                        long currentTimeMillis = System.currentTimeMillis();
                        for (int i = 0; i < SpansChunk.this.holders.size(); i++) {
                            SpansChunk.this.holders.get(i).draw(canvas, currentTimeMillis, 0.0f, 0.0f, 1.0f);
                        }
                    }

                    @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                    public void prepareDraw(long j) {
                        this.backgroundHolders.clear();
                        this.backgroundHolders.addAll(SpansChunk.this.holders);
                        for (int i = 0; i < this.backgroundHolders.size(); i++) {
                            if (this.backgroundHolders.get(i) != null) {
                                this.backgroundHolders.get(i).prepareForBackgroundDraw(j);
                            }
                        }
                    }

                    @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                    public void onFrameReady() {
                        for (int i = 0; i < this.backgroundHolders.size(); i++) {
                            if (this.backgroundHolders.get(i) != null) {
                                this.backgroundHolders.get(i).releaseDrawInBackground();
                            }
                        }
                        this.backgroundHolders.clear();
                        View view = SpansChunk.this.view;
                        if (view == null || view.getParent() == null) {
                            return;
                        }
                        ((View) SpansChunk.this.view.getParent()).invalidate();
                    }

                    @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                    public void onPaused() {
                        super.onPaused();
                    }

                    @Override // org.telegram.ui.Components.DrawingInBackgroundThreadDrawable
                    public void onResume() {
                        View view = SpansChunk.this.view;
                        if (view == null || view.getParent() == null) {
                            return;
                        }
                        ((View) SpansChunk.this.view.getParent()).invalidate();
                    }
                };
                this.backgroundThreadDrawable = drawingInBackgroundThreadDrawable2;
                drawingInBackgroundThreadDrawable2.onAttachToWindow();
            } else if (this.holders.size() >= 10 || (drawingInBackgroundThreadDrawable = this.backgroundThreadDrawable) == null) {
            } else {
                drawingInBackgroundThreadDrawable.onDetachFromWindow();
                this.backgroundThreadDrawable = null;
            }
        }

        public void draw(Canvas canvas, List<SpoilerEffect> list, long j, float f, float f2, float f3, float f4) {
            for (int i = 0; i < this.holders.size(); i++) {
                AnimatedEmojiHolder animatedEmojiHolder = this.holders.get(i);
                if (animatedEmojiHolder != null && animatedEmojiHolder.drawable != null) {
                    float f5 = 1.0f;
                    if (list != null) {
                        int i2 = 0;
                        while (true) {
                            if (i2 >= list.size()) {
                                break;
                            } else if (android.graphics.Rect.intersects(list.get(i2).getBounds(), animatedEmojiHolder.drawableBounds)) {
                                f5 = Math.max(0.0f, list.get(i2).getRippleProgress());
                                break;
                            } else {
                                i2++;
                            }
                        }
                    }
                    animatedEmojiHolder.drawingYOffset = f3;
                    animatedEmojiHolder.alpha = f5;
                    if (this.backgroundThreadDrawable == null) {
                        animatedEmojiHolder.draw(canvas, j, f, f2, f4);
                    }
                }
            }
            DrawingInBackgroundThreadDrawable drawingInBackgroundThreadDrawable = this.backgroundThreadDrawable;
            if (drawingInBackgroundThreadDrawable != null) {
                drawingInBackgroundThreadDrawable.draw(canvas, j, this.layout.getWidth(), this.layout.getHeight() + AndroidUtilities.dp(2.0f), f4);
            }
        }

        public SpansChunk clone() {
            SpansChunk spansChunk = new SpansChunk(this.view, this.layout, this.allowBackgroundRendering);
            spansChunk.holders.addAll(this.holders);
            spansChunk.checkBackgroundRendering();
            return spansChunk;
        }
    }
}
