package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.GravityCompat;
import com.google.android.exoplayer2.C;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.Cells.TextSelectionHelper;
/* loaded from: classes5.dex */
public class TableLayout extends View {
    public static final int ALIGN_BOUNDS = 0;
    public static final int ALIGN_MARGINS = 1;
    public static final Alignment BOTTOM;
    private static final int CAN_STRETCH = 2;
    private static final int DEFAULT_ALIGNMENT_MODE = 1;
    private static final int DEFAULT_COUNT = Integer.MIN_VALUE;
    private static final boolean DEFAULT_ORDER_PRESERVED = true;
    private static final int DEFAULT_ORIENTATION = 0;
    private static final boolean DEFAULT_USE_DEFAULT_MARGINS = false;
    public static final Alignment END;
    public static final int HORIZONTAL = 0;
    private static final int INFLEXIBLE = 0;
    private static final Alignment LEADING;
    public static final Alignment LEFT;
    static final int MAX_SIZE = 100000;
    public static final Alignment RIGHT;
    public static final Alignment START;
    public static final Alignment TOP;
    private static final Alignment TRAILING;
    public static final int UNDEFINED = Integer.MIN_VALUE;
    static final int UNINITIALIZED_HASH = 0;
    public static final int VERTICAL = 1;
    private int colCount;
    private TableLayoutDelegate delegate;
    private boolean drawLines;
    private boolean isRtl;
    private boolean isStriped;
    private int mDefaultGap;
    private TextSelectionHelper.ArticleTextSelectionHelper textSelectionHelper;
    static final Alignment UNDEFINED_ALIGNMENT = new Alignment() { // from class: org.telegram.ui.Components.TableLayout.1
        @Override // org.telegram.ui.Components.TableLayout.Alignment
        int getGravityOffset(Child view, int cellDelta) {
            return Integer.MIN_VALUE;
        }

        @Override // org.telegram.ui.Components.TableLayout.Alignment
        public int getAlignmentValue(Child view, int viewSize) {
            return Integer.MIN_VALUE;
        }
    };
    public static final Alignment CENTER = new Alignment() { // from class: org.telegram.ui.Components.TableLayout.5
        @Override // org.telegram.ui.Components.TableLayout.Alignment
        int getGravityOffset(Child view, int cellDelta) {
            return cellDelta >> 1;
        }

        @Override // org.telegram.ui.Components.TableLayout.Alignment
        public int getAlignmentValue(Child view, int viewSize) {
            return viewSize >> 1;
        }
    };
    public static final Alignment BASELINE = new Alignment() { // from class: org.telegram.ui.Components.TableLayout.6
        @Override // org.telegram.ui.Components.TableLayout.Alignment
        int getGravityOffset(Child view, int cellDelta) {
            return 0;
        }

        @Override // org.telegram.ui.Components.TableLayout.Alignment
        public int getAlignmentValue(Child view, int viewSize) {
            return Integer.MIN_VALUE;
        }

        @Override // org.telegram.ui.Components.TableLayout.Alignment
        public Bounds getBounds() {
            return new Bounds() { // from class: org.telegram.ui.Components.TableLayout.6.1
                private int size;

                @Override // org.telegram.ui.Components.TableLayout.Bounds
                protected void reset() {
                    super.reset();
                    this.size = Integer.MIN_VALUE;
                }

                @Override // org.telegram.ui.Components.TableLayout.Bounds
                protected void include(int before, int after) {
                    super.include(before, after);
                    this.size = Math.max(this.size, before + after);
                }

                @Override // org.telegram.ui.Components.TableLayout.Bounds
                protected int size(boolean min) {
                    return Math.max(super.size(min), this.size);
                }

                @Override // org.telegram.ui.Components.TableLayout.Bounds
                protected int getOffset(TableLayout gl, Child c, Alignment a, int size, boolean hrz) {
                    return Math.max(0, super.getOffset(gl, c, a, size, hrz));
                }
            };
        }
    };
    public static final Alignment FILL = new Alignment() { // from class: org.telegram.ui.Components.TableLayout.7
        @Override // org.telegram.ui.Components.TableLayout.Alignment
        int getGravityOffset(Child view, int cellDelta) {
            return 0;
        }

        @Override // org.telegram.ui.Components.TableLayout.Alignment
        public int getAlignmentValue(Child view, int viewSize) {
            return Integer.MIN_VALUE;
        }

        @Override // org.telegram.ui.Components.TableLayout.Alignment
        public int getSizeInCell(Child view, int viewSize, int cellSize) {
            return cellSize;
        }
    };
    private final Axis mHorizontalAxis = new Axis(true);
    private final Axis mVerticalAxis = new Axis(false);
    private int mOrientation = 0;
    private boolean mUseDefaultMargins = false;
    private int mAlignmentMode = 1;
    private int mLastLayoutParamsHashCode = 0;
    private int itemPaddingTop = AndroidUtilities.dp(7.0f);
    private int itemPaddingLeft = AndroidUtilities.dp(8.0f);
    private ArrayList<Child> cellsToFixHeight = new ArrayList<>();
    private ArrayList<Point> rowSpans = new ArrayList<>();
    private Path linePath = new Path();
    private Path backgroundPath = new Path();
    private RectF rect = new RectF();
    private float[] radii = new float[8];
    private ArrayList<Child> childrens = new ArrayList<>();

    /* loaded from: classes5.dex */
    public interface TableLayoutDelegate {
        ArticleViewer.DrawingText createTextLayout(TLRPC.TL_pageTableCell tL_pageTableCell, int i);

        Paint getHalfLinePaint();

        Paint getHeaderPaint();

        Paint getLinePaint();

        Paint getStripPaint();

        void onLayoutChild(ArticleViewer.DrawingText drawingText, int i, int i2);
    }

    /* loaded from: classes5.dex */
    public class Child {
        private TLRPC.TL_pageTableCell cell;
        private int fixedHeight;
        private int index;
        private LayoutParams layoutParams;
        private int measuredHeight;
        private int measuredWidth;
        public int rowspan;
        private int selectionIndex = -1;
        public int textHeight;
        public ArticleViewer.DrawingText textLayout;
        public int textLeft;
        public int textWidth;
        public int textX;
        public int textY;
        public int x;
        public int y;

        static /* synthetic */ int access$1520(Child x0, int x1) {
            int i = x0.measuredHeight - x1;
            x0.measuredHeight = i;
            return i;
        }

        public Child(int i) {
            TableLayout.this = this$0;
            this.index = i;
        }

        public LayoutParams getLayoutParams() {
            return this.layoutParams;
        }

        public int getMeasuredWidth() {
            return this.measuredWidth;
        }

        public int getMeasuredHeight() {
            return this.measuredHeight;
        }

        public void measure(int width, int height, boolean first) {
            this.measuredWidth = width;
            this.measuredHeight = height;
            if (first) {
                this.fixedHeight = height;
            }
            TLRPC.TL_pageTableCell tL_pageTableCell = this.cell;
            if (tL_pageTableCell != null) {
                if (tL_pageTableCell.valign_middle) {
                    this.textY = (this.measuredHeight - this.textHeight) / 2;
                } else if (this.cell.valign_bottom) {
                    this.textY = (this.measuredHeight - this.textHeight) - TableLayout.this.itemPaddingTop;
                } else {
                    this.textY = TableLayout.this.itemPaddingTop;
                }
                ArticleViewer.DrawingText drawingText = this.textLayout;
                if (drawingText != null) {
                    int lineCount = drawingText.getLineCount();
                    if (!first && (lineCount > 1 || (lineCount > 0 && (this.cell.align_center || this.cell.align_right)))) {
                        setTextLayout(TableLayout.this.delegate.createTextLayout(this.cell, this.measuredWidth - (TableLayout.this.itemPaddingLeft * 2)));
                        this.fixedHeight = this.textHeight + (TableLayout.this.itemPaddingTop * 2);
                    }
                    int i = this.textLeft;
                    if (i == 0) {
                        this.textX = TableLayout.this.itemPaddingLeft;
                        return;
                    }
                    this.textX = -i;
                    if (this.cell.align_right) {
                        this.textX += (this.measuredWidth - this.textWidth) - TableLayout.this.itemPaddingLeft;
                    } else if (!this.cell.align_center) {
                        this.textX += TableLayout.this.itemPaddingLeft;
                    } else {
                        this.textX += Math.round((this.measuredWidth - this.textWidth) / 2);
                    }
                }
            }
        }

        public void setTextLayout(ArticleViewer.DrawingText layout) {
            this.textLayout = layout;
            if (layout != null) {
                this.textWidth = 0;
                this.textLeft = 0;
                int a = 0;
                int N = layout.getLineCount();
                while (a < N) {
                    float lineLeft = layout.getLineLeft(a);
                    this.textLeft = a == 0 ? (int) Math.ceil(lineLeft) : Math.min(this.textLeft, (int) Math.ceil(lineLeft));
                    this.textWidth = (int) Math.ceil(Math.max(layout.getLineWidth(a), this.textWidth));
                    a++;
                }
                int a2 = layout.getHeight();
                this.textHeight = a2;
                return;
            }
            this.textLeft = 0;
            this.textWidth = 0;
            this.textHeight = 0;
        }

        public void layout(int left, int top, int right, int bottom) {
            this.x = left;
            this.y = top;
        }

        public int getTextX() {
            return this.x + this.textX;
        }

        public int getTextY() {
            return this.y + this.textY;
        }

        public void setFixedHeight(int value) {
            this.measuredHeight = this.fixedHeight;
            if (this.cell.valign_middle) {
                this.textY = (this.measuredHeight - this.textHeight) / 2;
            } else if (this.cell.valign_bottom) {
                this.textY = (this.measuredHeight - this.textHeight) - TableLayout.this.itemPaddingTop;
            }
        }

        public void draw(Canvas canvas, View view) {
            float start;
            float start2;
            float end;
            int i;
            int i2;
            float start3;
            float end2;
            int i3;
            float start4;
            float end3;
            boolean hasCorners;
            int i4;
            int i5;
            int i6;
            int i7;
            int i8;
            int i9;
            if (this.cell != null) {
                boolean isLastX = this.x + this.measuredWidth == TableLayout.this.getMeasuredWidth();
                boolean isLastY = this.y + this.measuredHeight == TableLayout.this.getMeasuredHeight();
                int rad = AndroidUtilities.dp(3.0f);
                if (this.cell.header || (TableLayout.this.isStriped && this.layoutParams.rowSpec.span.min % 2 == 0)) {
                    boolean hasCorners2 = false;
                    if (this.x != 0 || this.y != 0) {
                        float[] fArr = TableLayout.this.radii;
                        TableLayout.this.radii[1] = 0.0f;
                        fArr[0] = 0.0f;
                    } else {
                        float[] fArr2 = TableLayout.this.radii;
                        float f = rad;
                        TableLayout.this.radii[1] = f;
                        fArr2[0] = f;
                        hasCorners2 = true;
                    }
                    if (!isLastX || this.y != 0) {
                        float[] fArr3 = TableLayout.this.radii;
                        TableLayout.this.radii[3] = 0.0f;
                        fArr3[2] = 0.0f;
                    } else {
                        float[] fArr4 = TableLayout.this.radii;
                        float f2 = rad;
                        TableLayout.this.radii[3] = f2;
                        fArr4[2] = f2;
                        hasCorners2 = true;
                    }
                    if (!isLastX || !isLastY) {
                        float[] fArr5 = TableLayout.this.radii;
                        TableLayout.this.radii[5] = 0.0f;
                        fArr5[4] = 0.0f;
                    } else {
                        float[] fArr6 = TableLayout.this.radii;
                        float f3 = rad;
                        TableLayout.this.radii[5] = f3;
                        fArr6[4] = f3;
                        hasCorners2 = true;
                    }
                    if (this.x != 0 || !isLastY) {
                        float[] fArr7 = TableLayout.this.radii;
                        TableLayout.this.radii[7] = 0.0f;
                        fArr7[6] = 0.0f;
                        hasCorners = hasCorners2;
                    } else {
                        float[] fArr8 = TableLayout.this.radii;
                        float f4 = rad;
                        TableLayout.this.radii[7] = f4;
                        fArr8[6] = f4;
                        hasCorners = true;
                    }
                    if (hasCorners) {
                        TableLayout.this.rect.set(this.x, this.y, i8 + this.measuredWidth, i9 + this.measuredHeight);
                        TableLayout.this.backgroundPath.reset();
                        TableLayout.this.backgroundPath.addRoundRect(TableLayout.this.rect, TableLayout.this.radii, Path.Direction.CW);
                        if (this.cell.header) {
                            canvas.drawPath(TableLayout.this.backgroundPath, TableLayout.this.delegate.getHeaderPaint());
                        } else {
                            canvas.drawPath(TableLayout.this.backgroundPath, TableLayout.this.delegate.getStripPaint());
                        }
                    } else if (this.cell.header) {
                        canvas.drawRect(this.x, this.y, i6 + this.measuredWidth, i7 + this.measuredHeight, TableLayout.this.delegate.getHeaderPaint());
                    } else {
                        canvas.drawRect(this.x, this.y, i4 + this.measuredWidth, i5 + this.measuredHeight, TableLayout.this.delegate.getStripPaint());
                    }
                }
                if (this.textLayout != null) {
                    canvas.save();
                    canvas.translate(getTextX(), getTextY());
                    if (this.selectionIndex >= 0) {
                        TableLayout.this.textSelectionHelper.draw(canvas, (TextSelectionHelper.ArticleSelectableView) TableLayout.this.getParent().getParent(), this.selectionIndex);
                    }
                    this.textLayout.draw(canvas, view);
                    canvas.restore();
                }
                if (TableLayout.this.drawLines) {
                    Paint linePaint = TableLayout.this.delegate.getLinePaint();
                    Paint halfLinePaint = TableLayout.this.delegate.getLinePaint();
                    float strokeWidth = linePaint.getStrokeWidth() / 2.0f;
                    float halfStrokeWidth = halfLinePaint.getStrokeWidth() / 2.0f;
                    int i10 = this.x;
                    if (i10 != 0) {
                        canvas.drawLine(i10 - halfStrokeWidth, this.y, i10 - halfStrokeWidth, i3 + this.measuredHeight, halfLinePaint);
                    } else {
                        int i11 = this.y;
                        float start5 = i11;
                        float end4 = this.measuredHeight + i11;
                        if (i11 != 0) {
                            start4 = start5;
                        } else {
                            start4 = start5 + rad;
                        }
                        if (end4 != TableLayout.this.getMeasuredHeight()) {
                            end3 = end4;
                        } else {
                            end3 = end4 - rad;
                        }
                        int i12 = this.x;
                        canvas.drawLine(i12 + strokeWidth, start4, i12 + strokeWidth, end3, linePaint);
                    }
                    int i13 = this.y;
                    if (i13 == 0) {
                        int i14 = this.x;
                        float start6 = i14;
                        float end5 = this.measuredWidth + i14;
                        if (i14 != 0) {
                            start3 = start6;
                        } else {
                            start3 = start6 + rad;
                        }
                        if (end5 != TableLayout.this.getMeasuredWidth()) {
                            end2 = end5;
                        } else {
                            end2 = end5 - rad;
                        }
                        int i15 = this.y;
                        canvas.drawLine(start3, i15 + strokeWidth, end2, i15 + strokeWidth, linePaint);
                    } else {
                        canvas.drawLine(this.x, i13 - halfStrokeWidth, i2 + this.measuredWidth, i13 - halfStrokeWidth, halfLinePaint);
                    }
                    if (isLastX && (i = this.y) == 0) {
                        start = i + rad;
                    } else {
                        start = this.y - strokeWidth;
                    }
                    float end6 = (!isLastX || !isLastY) ? (this.y + this.measuredHeight) - strokeWidth : (this.y + this.measuredHeight) - rad;
                    int i16 = this.x;
                    int i17 = this.measuredWidth;
                    canvas.drawLine((i16 + i17) - strokeWidth, start, (i16 + i17) - strokeWidth, end6, linePaint);
                    int i18 = this.x;
                    if (i18 == 0 && isLastY) {
                        start2 = i18 + rad;
                    } else {
                        float start7 = i18;
                        start2 = start7 - strokeWidth;
                    }
                    if (isLastX && isLastY) {
                        end = (i18 + this.measuredWidth) - rad;
                    } else {
                        end = (i18 + this.measuredWidth) - strokeWidth;
                    }
                    int i19 = this.y;
                    int i20 = this.measuredHeight;
                    canvas.drawLine(start2, (i19 + i20) - strokeWidth, end, (i19 + i20) - strokeWidth, linePaint);
                    if (this.x == 0 && this.y == 0) {
                        RectF rectF = TableLayout.this.rect;
                        int i21 = this.x;
                        int i22 = this.y;
                        rectF.set(i21 + strokeWidth, i22 + strokeWidth, i21 + strokeWidth + (rad * 2), i22 + strokeWidth + (rad * 2));
                        canvas.drawArc(TableLayout.this.rect, -180.0f, 90.0f, false, linePaint);
                    }
                    if (isLastX && this.y == 0) {
                        RectF rectF2 = TableLayout.this.rect;
                        int i23 = this.x;
                        int i24 = this.measuredWidth;
                        int i25 = this.y;
                        rectF2.set(((i23 + i24) - strokeWidth) - (rad * 2), i25 + strokeWidth, (i23 + i24) - strokeWidth, i25 + strokeWidth + (rad * 2));
                        canvas.drawArc(TableLayout.this.rect, 0.0f, -90.0f, false, linePaint);
                    }
                    if (this.x == 0 && isLastY) {
                        RectF rectF3 = TableLayout.this.rect;
                        int i26 = this.x;
                        int i27 = this.y;
                        int i28 = this.measuredHeight;
                        rectF3.set(i26 + strokeWidth, ((i27 + i28) - strokeWidth) - (rad * 2), i26 + strokeWidth + (rad * 2), (i27 + i28) - strokeWidth);
                        canvas.drawArc(TableLayout.this.rect, 180.0f, -90.0f, false, linePaint);
                    }
                    if (isLastX && isLastY) {
                        RectF rectF4 = TableLayout.this.rect;
                        int i29 = this.x;
                        int i30 = this.measuredWidth;
                        int i31 = this.y;
                        int i32 = this.measuredHeight;
                        rectF4.set(((i29 + i30) - strokeWidth) - (rad * 2), ((i31 + i32) - strokeWidth) - (rad * 2), (i29 + i30) - strokeWidth, (i31 + i32) - strokeWidth);
                        canvas.drawArc(TableLayout.this.rect, 0.0f, 90.0f, false, linePaint);
                    }
                }
            }
        }

        public void setSelectionIndex(int selectionIndex) {
            this.selectionIndex = selectionIndex;
        }

        public int getRow() {
            return this.rowspan + 10;
        }
    }

    public void addChild(int x, int y, int colspan, int rowspan) {
        Child child = new Child(this.childrens.size());
        LayoutParams layoutParams = new LayoutParams();
        Interval interval = new Interval(y, y + rowspan);
        Alignment alignment = FILL;
        layoutParams.rowSpec = new Spec(false, interval, alignment, 0.0f);
        layoutParams.columnSpec = new Spec(false, new Interval(x, x + colspan), alignment, 0.0f);
        child.layoutParams = layoutParams;
        child.rowspan = y;
        this.childrens.add(child);
        invalidateStructure();
    }

    public void addChild(TLRPC.TL_pageTableCell cell, int x, int y, int colspan) {
        int colspan2;
        if (colspan != 0) {
            colspan2 = colspan;
        } else {
            colspan2 = 1;
        }
        Child child = new Child(this.childrens.size());
        child.cell = cell;
        LayoutParams layoutParams = new LayoutParams();
        Interval interval = new Interval(y, (cell.rowspan != 0 ? cell.rowspan : 1) + y);
        Alignment alignment = FILL;
        layoutParams.rowSpec = new Spec(false, interval, alignment, 0.0f);
        layoutParams.columnSpec = new Spec(false, new Interval(x, x + colspan2), alignment, 1.0f);
        child.layoutParams = layoutParams;
        child.rowspan = y;
        this.childrens.add(child);
        if (cell.rowspan > 1) {
            this.rowSpans.add(new Point(y, cell.rowspan + y));
        }
        invalidateStructure();
    }

    public void setDrawLines(boolean value) {
        this.drawLines = value;
    }

    public void setStriped(boolean value) {
        this.isStriped = value;
    }

    public void setRtl(boolean value) {
        this.isRtl = value;
    }

    public void removeAllChildrens() {
        this.childrens.clear();
        this.rowSpans.clear();
        invalidateStructure();
    }

    public int getChildCount() {
        return this.childrens.size();
    }

    public Child getChildAt(int index) {
        if (index < 0 || index >= this.childrens.size()) {
            return null;
        }
        return this.childrens.get(index);
    }

    public TableLayout(Context context, TableLayoutDelegate tableLayoutDelegate, TextSelectionHelper.ArticleTextSelectionHelper textSelectionHelper) {
        super(context);
        this.textSelectionHelper = textSelectionHelper;
        setRowCount(Integer.MIN_VALUE);
        setColumnCount(Integer.MIN_VALUE);
        setOrientation(0);
        setUseDefaultMargins(false);
        setAlignmentMode(1);
        setRowOrderPreserved(true);
        setColumnOrderPreserved(true);
        this.delegate = tableLayoutDelegate;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public void setOrientation(int orientation) {
        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            invalidateStructure();
            requestLayout();
        }
    }

    public int getRowCount() {
        return this.mVerticalAxis.getCount();
    }

    public void setRowCount(int rowCount) {
        this.mVerticalAxis.setCount(rowCount);
        invalidateStructure();
        requestLayout();
    }

    public int getColumnCount() {
        return this.mHorizontalAxis.getCount();
    }

    public void setColumnCount(int columnCount) {
        this.mHorizontalAxis.setCount(columnCount);
        invalidateStructure();
        requestLayout();
    }

    public boolean getUseDefaultMargins() {
        return this.mUseDefaultMargins;
    }

    public void setUseDefaultMargins(boolean useDefaultMargins) {
        this.mUseDefaultMargins = useDefaultMargins;
        requestLayout();
    }

    public int getAlignmentMode() {
        return this.mAlignmentMode;
    }

    public void setAlignmentMode(int alignmentMode) {
        this.mAlignmentMode = alignmentMode;
        requestLayout();
    }

    public boolean isRowOrderPreserved() {
        return this.mVerticalAxis.isOrderPreserved();
    }

    public void setRowOrderPreserved(boolean rowOrderPreserved) {
        this.mVerticalAxis.setOrderPreserved(rowOrderPreserved);
        invalidateStructure();
        requestLayout();
    }

    public boolean isColumnOrderPreserved() {
        return this.mHorizontalAxis.isOrderPreserved();
    }

    public void setColumnOrderPreserved(boolean columnOrderPreserved) {
        this.mHorizontalAxis.setOrderPreserved(columnOrderPreserved);
        invalidateStructure();
        requestLayout();
    }

    static int max2(int[] a, int valueIfEmpty) {
        int result = valueIfEmpty;
        for (int i : a) {
            result = Math.max(result, i);
        }
        return result;
    }

    static <T> T[] append(T[] a, T[] b) {
        T[] result = (T[]) ((Object[]) Array.newInstance(a.getClass().getComponentType(), a.length + b.length));
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    static Alignment getAlignment(int gravity, boolean horizontal) {
        int mask = horizontal ? 7 : 112;
        int shift = horizontal ? 0 : 4;
        int flags = (gravity & mask) >> shift;
        switch (flags) {
            case 1:
                return CENTER;
            case 3:
                return horizontal ? LEFT : TOP;
            case 5:
                return horizontal ? RIGHT : BOTTOM;
            case 7:
                return FILL;
            case GravityCompat.START /* 8388611 */:
                return START;
            case GravityCompat.END /* 8388613 */:
                return END;
            default:
                return UNDEFINED_ALIGNMENT;
        }
    }

    private int getDefaultMargin(Child c, boolean horizontal, boolean leading) {
        return this.mDefaultGap / 2;
    }

    private int getDefaultMargin(Child c, boolean isAtEdge, boolean horizontal, boolean leading) {
        return getDefaultMargin(c, horizontal, leading);
    }

    private int getDefaultMargin(Child c, LayoutParams p, boolean horizontal, boolean leading) {
        boolean isAtEdge = false;
        if (!this.mUseDefaultMargins) {
            return 0;
        }
        Spec spec = horizontal ? p.columnSpec : p.rowSpec;
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        Interval span = spec.span;
        boolean leading1 = (horizontal && this.isRtl) != leading;
        if (!leading1 ? span.max == axis.getCount() : span.min == 0) {
            isAtEdge = true;
        }
        return getDefaultMargin(c, isAtEdge, horizontal, leading);
    }

    int getMargin1(Child view, boolean horizontal, boolean leading) {
        int margin;
        LayoutParams lp = view.getLayoutParams();
        if (horizontal) {
            margin = leading ? lp.leftMargin : lp.rightMargin;
        } else {
            margin = leading ? lp.topMargin : lp.bottomMargin;
        }
        return margin == Integer.MIN_VALUE ? getDefaultMargin(view, lp, horizontal, leading) : margin;
    }

    private int getMargin(Child view, boolean horizontal, boolean leading) {
        if (this.mAlignmentMode == 1) {
            return getMargin1(view, horizontal, leading);
        }
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        int[] margins = leading ? axis.getLeadingMargins() : axis.getTrailingMargins();
        LayoutParams lp = view.getLayoutParams();
        Spec spec = horizontal ? lp.columnSpec : lp.rowSpec;
        Interval interval = spec.span;
        int index = leading ? interval.min : interval.max;
        return margins[index];
    }

    private int getTotalMargin(Child child, boolean horizontal) {
        return getMargin(child, horizontal, true) + getMargin(child, horizontal, false);
    }

    private static boolean fits(int[] a, int value, int start, int end) {
        if (end > a.length) {
            return false;
        }
        for (int i = start; i < end; i++) {
            if (a[i] > value) {
                return false;
            }
        }
        return true;
    }

    private static void procrusteanFill(int[] a, int start, int end, int value) {
        int length = a.length;
        Arrays.fill(a, Math.min(start, length), Math.min(end, length), value);
    }

    private static void setCellGroup(LayoutParams lp, int row, int rowSpan, int col, int colSpan) {
        lp.setRowSpecSpan(new Interval(row, row + rowSpan));
        lp.setColumnSpecSpan(new Interval(col, col + colSpan));
    }

    private static int clip(Interval minorRange, boolean minorWasDefined, int count) {
        int size = minorRange.size();
        if (count == 0) {
            return size;
        }
        int min = minorWasDefined ? Math.min(minorRange.min, count) : 0;
        return Math.min(size, count - min);
    }

    private void validateLayoutParams() {
        int N;
        TableLayout tableLayout = this;
        int count = 0;
        boolean horizontal = tableLayout.mOrientation == 0;
        Axis axis = horizontal ? tableLayout.mHorizontalAxis : tableLayout.mVerticalAxis;
        if (axis.definedCount != Integer.MIN_VALUE) {
            count = axis.definedCount;
        }
        int major = 0;
        int minor = 0;
        int[] maxSizes = new int[count];
        int i = 0;
        int N2 = getChildCount();
        while (i < N2) {
            LayoutParams lp = tableLayout.getChildAt(i).getLayoutParams();
            Spec majorSpec = horizontal ? lp.rowSpec : lp.columnSpec;
            Interval majorRange = majorSpec.span;
            boolean majorWasDefined = majorSpec.startDefined;
            int majorSpan = majorRange.size();
            if (majorWasDefined) {
                major = majorRange.min;
            }
            Spec minorSpec = horizontal ? lp.columnSpec : lp.rowSpec;
            Interval minorRange = minorSpec.span;
            boolean minorWasDefined = minorSpec.startDefined;
            Axis axis2 = axis;
            int minorSpan = clip(minorRange, minorWasDefined, count);
            if (minorWasDefined) {
                minor = minorRange.min;
            }
            if (count != 0) {
                if (!majorWasDefined || !minorWasDefined) {
                    while (true) {
                        N = N2;
                        int N3 = minor + minorSpan;
                        if (fits(maxSizes, major, minor, N3)) {
                            break;
                        } else if (minorWasDefined) {
                            major++;
                            N2 = N;
                        } else if (minor + minorSpan <= count) {
                            minor++;
                            N2 = N;
                        } else {
                            minor = 0;
                            major++;
                            N2 = N;
                        }
                    }
                } else {
                    N = N2;
                }
                procrusteanFill(maxSizes, minor, minor + minorSpan, major + majorSpan);
            } else {
                N = N2;
            }
            if (horizontal) {
                setCellGroup(lp, major, majorSpan, minor, minorSpan);
            } else {
                setCellGroup(lp, minor, minorSpan, major, majorSpan);
            }
            minor += minorSpan;
            i++;
            tableLayout = this;
            axis = axis2;
            N2 = N;
        }
    }

    private void invalidateStructure() {
        this.mLastLayoutParamsHashCode = 0;
        this.mHorizontalAxis.invalidateStructure();
        this.mVerticalAxis.invalidateStructure();
        invalidateValues();
    }

    private void invalidateValues() {
        Axis axis = this.mHorizontalAxis;
        if (axis != null && this.mVerticalAxis != null) {
            axis.invalidateValues();
            this.mVerticalAxis.invalidateValues();
        }
    }

    public static void handleInvalidParams(String msg) {
        throw new IllegalArgumentException(msg + ". ");
    }

    private void checkLayoutParams(LayoutParams lp, boolean horizontal) {
        String groupName = horizontal ? "column" : "row";
        Spec spec = horizontal ? lp.columnSpec : lp.rowSpec;
        Interval span = spec.span;
        if (span.min != Integer.MIN_VALUE && span.min < 0) {
            handleInvalidParams(groupName + " indices must be positive");
        }
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        int count = axis.definedCount;
        if (count != Integer.MIN_VALUE) {
            if (span.max > count) {
                handleInvalidParams(groupName + " indices (start + span) mustn't exceed the " + groupName + " count");
            }
            if (span.size() > count) {
                handleInvalidParams(groupName + " span mustn't exceed the " + groupName + " count");
            }
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            Child c = getChildAt(i);
            c.draw(canvas, this);
        }
    }

    private int computeLayoutParamsHashCode() {
        int result = 1;
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            Child c = getChildAt(i);
            LayoutParams lp = c.getLayoutParams();
            result = (result * 31) + lp.hashCode();
        }
        return result;
    }

    private void consistencyCheck() {
        int i = this.mLastLayoutParamsHashCode;
        if (i == 0) {
            validateLayoutParams();
            this.mLastLayoutParamsHashCode = computeLayoutParamsHashCode();
        } else if (i != computeLayoutParamsHashCode()) {
            invalidateStructure();
            consistencyCheck();
        }
    }

    private void measureChildWithMargins2(Child child, int parentWidthSpec, int parentHeightSpec, int childWidth, int childHeight, boolean first) {
        child.measure(getTotalMargin(child, true) + childWidth, getTotalMargin(child, false) + childHeight, first);
    }

    private void measureChildrenWithMargins(int widthSpec, int heightSpec, boolean firstPass) {
        int maxCellWidth;
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            Child c = getChildAt(i);
            LayoutParams lp = c.getLayoutParams();
            boolean z = false;
            if (firstPass) {
                int width = View.MeasureSpec.getSize(widthSpec);
                if (this.colCount == 2) {
                    maxCellWidth = ((int) (width / 2.0f)) - (this.itemPaddingLeft * 4);
                } else {
                    maxCellWidth = (int) (width / 1.5f);
                }
                c.setTextLayout(this.delegate.createTextLayout(c.cell, maxCellWidth));
                if (c.textLayout != null) {
                    lp.width = c.textWidth + (this.itemPaddingLeft * 2);
                    lp.height = c.textHeight + (this.itemPaddingTop * 2);
                } else {
                    lp.width = 0;
                    lp.height = 0;
                }
                measureChildWithMargins2(c, widthSpec, heightSpec, lp.width, lp.height, true);
            } else {
                if (this.mOrientation == 0) {
                    z = true;
                }
                boolean horizontal = z;
                Spec spec = horizontal ? lp.columnSpec : lp.rowSpec;
                if (spec.getAbsoluteAlignment(horizontal) == FILL) {
                    Interval span = spec.span;
                    Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
                    int[] locations = axis.getLocations();
                    int cellSize = locations[span.max] - locations[span.min];
                    int viewSize = cellSize - getTotalMargin(c, horizontal);
                    if (horizontal) {
                        measureChildWithMargins2(c, widthSpec, heightSpec, viewSize, lp.height, false);
                    } else {
                        measureChildWithMargins2(c, widthSpec, heightSpec, lp.width, viewSize, false);
                    }
                }
            }
        }
    }

    static int adjust(int measureSpec, int delta) {
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec + delta), View.MeasureSpec.getMode(measureSpec));
    }

    @Override // android.view.View
    protected void onMeasure(int widthSpec, int heightSpec) {
        int heightSansPadding;
        int widthSansPadding;
        int a;
        Point p;
        consistencyCheck();
        invalidateValues();
        this.colCount = 0;
        int N = getChildCount();
        for (int a2 = 0; a2 < N; a2++) {
            this.colCount = Math.max(this.colCount, getChildAt(a2).layoutParams.columnSpec.span.max);
        }
        measureChildrenWithMargins(widthSpec, heightSpec, true);
        if (this.mOrientation == 0) {
            int widthSansPadding2 = this.mHorizontalAxis.getMeasure(widthSpec);
            measureChildrenWithMargins(widthSpec, heightSpec, false);
            widthSansPadding = widthSansPadding2;
            heightSansPadding = this.mVerticalAxis.getMeasure(heightSpec);
        } else {
            int heightSansPadding2 = this.mVerticalAxis.getMeasure(heightSpec);
            measureChildrenWithMargins(widthSpec, heightSpec, false);
            widthSansPadding = this.mHorizontalAxis.getMeasure(widthSpec);
            heightSansPadding = heightSansPadding2;
        }
        int measuredWidth = Math.max(widthSansPadding, View.MeasureSpec.getSize(widthSpec));
        int measuredHeight = Math.max(heightSansPadding, getSuggestedMinimumHeight());
        setMeasuredDimension(measuredWidth, measuredHeight);
        this.mHorizontalAxis.layout(measuredWidth);
        this.mVerticalAxis.layout(measuredHeight);
        int[] hLocations = this.mHorizontalAxis.getLocations();
        int[] vLocations = this.mVerticalAxis.getLocations();
        this.cellsToFixHeight.clear();
        int measuredWidth2 = hLocations[hLocations.length - 1];
        int N2 = getChildCount();
        int height = 0;
        while (height < N2) {
            Child c = getChildAt(height);
            LayoutParams lp = c.getLayoutParams();
            Spec columnSpec = lp.columnSpec;
            int measuredWidth3 = measuredWidth2;
            Spec rowSpec = lp.rowSpec;
            Interval colSpan = columnSpec.span;
            Interval rowSpan = rowSpec.span;
            int x1 = hLocations[colSpan.min];
            int y1 = vLocations[rowSpan.min];
            int x2 = hLocations[colSpan.max];
            int y2 = vLocations[rowSpan.max];
            int cellWidth = x2 - x1;
            int cellHeight = y2 - y1;
            int pWidth = getMeasurement(c, true);
            int pHeight = getMeasurement(c, false);
            Alignment hAlign = columnSpec.getAbsoluteAlignment(true);
            Alignment vAlign = rowSpec.getAbsoluteAlignment(false);
            Bounds boundsX = this.mHorizontalAxis.getGroupBounds().getValue(height);
            Bounds boundsY = this.mVerticalAxis.getGroupBounds().getValue(height);
            int i = height;
            int gravityOffsetX = hAlign.getGravityOffset(c, cellWidth - boundsX.size(true));
            int gravityOffsetY = vAlign.getGravityOffset(c, cellHeight - boundsY.size(true));
            int leftMargin = getMargin(c, true, true);
            int topMargin = getMargin(c, false, true);
            int rightMargin = getMargin(c, true, false);
            int bottomMargin = getMargin(c, false, false);
            int sumMarginsX = leftMargin + rightMargin;
            int sumMarginsY = topMargin + bottomMargin;
            int N3 = N2;
            int widthSansPadding3 = widthSansPadding;
            int alignmentOffsetX = boundsX.getOffset(this, c, hAlign, pWidth + sumMarginsX, true);
            int alignmentOffsetY = boundsY.getOffset(this, c, vAlign, pHeight + sumMarginsY, false);
            int width = hAlign.getSizeInCell(c, pWidth, cellWidth - sumMarginsX);
            int height2 = vAlign.getSizeInCell(c, pHeight, cellHeight - sumMarginsY);
            int dx = x1 + gravityOffsetX + alignmentOffsetX;
            int cx = !this.isRtl ? leftMargin + dx : ((measuredWidth3 - width) - rightMargin) - dx;
            int alignmentOffsetY2 = y1 + gravityOffsetY + alignmentOffsetY + topMargin;
            if (c.cell != null) {
                if (width != c.getMeasuredWidth() || height2 != c.getMeasuredHeight()) {
                    c.measure(width, height2, false);
                }
                if (c.fixedHeight != 0 && c.fixedHeight != height2) {
                    if (c.layoutParams.rowSpec.span.max - c.layoutParams.rowSpec.span.min <= 1) {
                        int a3 = 0;
                        int size = this.rowSpans.size();
                        while (true) {
                            if (a3 >= size) {
                                p = null;
                                break;
                            }
                            int size2 = size;
                            Point p2 = this.rowSpans.get(a3);
                            int pHeight2 = pHeight;
                            Alignment hAlign2 = hAlign;
                            if (p2.x > c.layoutParams.rowSpec.span.min || p2.y <= c.layoutParams.rowSpec.span.min) {
                                a3++;
                                size = size2;
                                pHeight = pHeight2;
                                hAlign = hAlign2;
                            } else {
                                p = 1;
                                break;
                            }
                        }
                        if (p == null) {
                            this.cellsToFixHeight.add(c);
                        }
                    }
                }
            }
            c.layout(cx, alignmentOffsetY2, cx + width, alignmentOffsetY2 + height2);
            height = i + 1;
            measuredWidth2 = measuredWidth3;
            widthSansPadding = widthSansPadding3;
            N2 = N3;
        }
        int widthSansPadding4 = measuredWidth2;
        int a4 = 0;
        int N4 = this.cellsToFixHeight.size();
        int fixedHeight = measuredHeight;
        while (a4 < N4) {
            Child child = this.cellsToFixHeight.get(a4);
            boolean skip = false;
            int heightDiff = child.measuredHeight - child.fixedHeight;
            int i2 = child.index + 1;
            int size3 = this.childrens.size();
            while (true) {
                if (i2 >= size3) {
                    a = a4;
                    break;
                }
                Child next = this.childrens.get(i2);
                a = a4;
                if (child.layoutParams.rowSpec.span.min == next.layoutParams.rowSpec.span.min) {
                    if (child.fixedHeight >= next.fixedHeight) {
                        int diff = next.measuredHeight - next.fixedHeight;
                        if (diff > 0) {
                            heightDiff = Math.min(heightDiff, diff);
                        }
                        i2++;
                        a4 = a;
                    } else {
                        skip = true;
                        break;
                    }
                } else {
                    break;
                }
            }
            if (!skip) {
                int i3 = child.index - 1;
                while (true) {
                    if (i3 < 0) {
                        break;
                    }
                    Child next2 = this.childrens.get(i3);
                    if (child.layoutParams.rowSpec.span.min == next2.layoutParams.rowSpec.span.min) {
                        if (child.fixedHeight >= next2.fixedHeight) {
                            int diff2 = next2.measuredHeight - next2.fixedHeight;
                            if (diff2 > 0) {
                                heightDiff = Math.min(heightDiff, diff2);
                            }
                            i3--;
                        } else {
                            skip = true;
                            break;
                        }
                    } else {
                        break;
                    }
                }
            }
            if (!skip) {
                child.setFixedHeight(child.fixedHeight);
                fixedHeight -= heightDiff;
                int size4 = this.childrens.size();
                for (int i4 = 0; i4 < size4; i4++) {
                    Child next3 = this.childrens.get(i4);
                    if (child != next3) {
                        if (child.layoutParams.rowSpec.span.min == next3.layoutParams.rowSpec.span.min) {
                            if (next3.fixedHeight != next3.measuredHeight) {
                                this.cellsToFixHeight.remove(next3);
                                if (next3.index < child.index) {
                                    a--;
                                }
                                N4--;
                            }
                            Child.access$1520(next3, heightDiff);
                            next3.measure(next3.measuredWidth, next3.measuredHeight, true);
                            N4 = N4;
                        } else if (child.layoutParams.rowSpec.span.min < next3.layoutParams.rowSpec.span.min) {
                            next3.y -= heightDiff;
                        }
                    }
                }
            }
            int i5 = a;
            a4 = i5 + 1;
        }
        int N5 = getChildCount();
        for (int i6 = 0; i6 < N5; i6++) {
            Child c2 = getChildAt(i6);
            this.delegate.onLayoutChild(c2.textLayout, c2.getTextX(), c2.getTextY());
        }
        setMeasuredDimension(widthSansPadding4, fixedHeight);
    }

    private int getMeasurement(Child c, boolean horizontal) {
        return horizontal ? c.getMeasuredWidth() : c.getMeasuredHeight();
    }

    final int getMeasurementIncludingMargin(Child c, boolean horizontal) {
        return getMeasurement(c, horizontal) + getTotalMargin(c, horizontal);
    }

    @Override // android.view.View
    public void requestLayout() {
        super.requestLayout();
        invalidateValues();
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        consistencyCheck();
    }

    /* loaded from: classes5.dex */
    public final class Axis {
        private static final int COMPLETE = 2;
        private static final int NEW = 0;
        private static final int PENDING = 1;
        public Arc[] arcs;
        public boolean arcsValid;
        PackedMap<Interval, MutableInt> backwardLinks;
        public boolean backwardLinksValid;
        public int definedCount;
        public int[] deltas;
        PackedMap<Interval, MutableInt> forwardLinks;
        public boolean forwardLinksValid;
        PackedMap<Spec, Bounds> groupBounds;
        public boolean groupBoundsValid;
        public boolean hasWeights;
        public boolean hasWeightsValid;
        public final boolean horizontal;
        public int[] leadingMargins;
        public boolean leadingMarginsValid;
        public int[] locations;
        public boolean locationsValid;
        private int maxIndex;
        boolean orderPreserved;
        private MutableInt parentMax;
        private MutableInt parentMin;
        public int[] trailingMargins;
        public boolean trailingMarginsValid;

        private Axis(boolean horizontal) {
            TableLayout.this = this$0;
            this.definedCount = Integer.MIN_VALUE;
            this.maxIndex = Integer.MIN_VALUE;
            this.groupBoundsValid = false;
            this.forwardLinksValid = false;
            this.backwardLinksValid = false;
            this.leadingMarginsValid = false;
            this.trailingMarginsValid = false;
            this.arcsValid = false;
            this.locationsValid = false;
            this.hasWeightsValid = false;
            this.orderPreserved = true;
            this.parentMin = new MutableInt(0);
            this.parentMax = new MutableInt(-100000);
            this.horizontal = horizontal;
        }

        private int calculateMaxIndex() {
            int result = -1;
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                Child c = TableLayout.this.getChildAt(i);
                LayoutParams params = c.getLayoutParams();
                Spec spec = this.horizontal ? params.columnSpec : params.rowSpec;
                Interval span = spec.span;
                result = Math.max(Math.max(Math.max(result, span.min), span.max), span.size());
            }
            if (result == -1) {
                return Integer.MIN_VALUE;
            }
            return result;
        }

        private int getMaxIndex() {
            if (this.maxIndex == Integer.MIN_VALUE) {
                this.maxIndex = Math.max(0, calculateMaxIndex());
            }
            return this.maxIndex;
        }

        public int getCount() {
            return Math.max(this.definedCount, getMaxIndex());
        }

        public void setCount(int count) {
            if (count != Integer.MIN_VALUE && count < getMaxIndex()) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.horizontal ? "column" : "row");
                sb.append("Count must be greater than or equal to the maximum of all grid indices (and spans) defined in the LayoutParams of each child");
                TableLayout.handleInvalidParams(sb.toString());
            }
            this.definedCount = count;
        }

        public boolean isOrderPreserved() {
            return this.orderPreserved;
        }

        public void setOrderPreserved(boolean orderPreserved) {
            this.orderPreserved = orderPreserved;
            invalidateStructure();
        }

        private PackedMap<Spec, Bounds> createGroupBounds() {
            Assoc<Spec, Bounds> assoc = Assoc.of(Spec.class, Bounds.class);
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                Child c = TableLayout.this.getChildAt(i);
                LayoutParams lp = c.getLayoutParams();
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                Bounds bounds = spec.getAbsoluteAlignment(this.horizontal).getBounds();
                assoc.put(spec, bounds);
            }
            return assoc.pack();
        }

        private void computeGroupBounds() {
            Bounds[] values = this.groupBounds.values;
            for (Bounds bounds : values) {
                bounds.reset();
            }
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                Child c = TableLayout.this.getChildAt(i);
                LayoutParams lp = c.getLayoutParams();
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                int size = TableLayout.this.getMeasurementIncludingMargin(c, this.horizontal) + (spec.weight == 0.0f ? 0 : this.deltas[i]);
                this.groupBounds.getValue(i).include(TableLayout.this, c, spec, this, size);
            }
        }

        public PackedMap<Spec, Bounds> getGroupBounds() {
            if (this.groupBounds == null) {
                this.groupBounds = createGroupBounds();
            }
            if (!this.groupBoundsValid) {
                computeGroupBounds();
                this.groupBoundsValid = true;
            }
            return this.groupBounds;
        }

        private PackedMap<Interval, MutableInt> createLinks(boolean min) {
            Assoc<Interval, MutableInt> result = Assoc.of(Interval.class, MutableInt.class);
            Spec[] keys = getGroupBounds().keys;
            int N = keys.length;
            for (int i = 0; i < N; i++) {
                Interval span = min ? keys[i].span : keys[i].span.inverse();
                result.put(span, new MutableInt());
            }
            return result.pack();
        }

        private void computeLinks(PackedMap<Interval, MutableInt> links, boolean min) {
            MutableInt[] spans = links.values;
            for (MutableInt mutableInt : spans) {
                mutableInt.reset();
            }
            Bounds[] bounds = getGroupBounds().values;
            for (int i = 0; i < bounds.length; i++) {
                int size = bounds[i].size(min);
                MutableInt valueHolder = links.getValue(i);
                valueHolder.value = Math.max(valueHolder.value, min ? size : -size);
            }
        }

        private PackedMap<Interval, MutableInt> getForwardLinks() {
            if (this.forwardLinks == null) {
                this.forwardLinks = createLinks(true);
            }
            if (!this.forwardLinksValid) {
                computeLinks(this.forwardLinks, true);
                this.forwardLinksValid = true;
            }
            return this.forwardLinks;
        }

        private PackedMap<Interval, MutableInt> getBackwardLinks() {
            if (this.backwardLinks == null) {
                this.backwardLinks = createLinks(false);
            }
            if (!this.backwardLinksValid) {
                computeLinks(this.backwardLinks, false);
                this.backwardLinksValid = true;
            }
            return this.backwardLinks;
        }

        private void include(List<Arc> arcs, Interval key, MutableInt size, boolean ignoreIfAlreadyPresent) {
            if (key.size() == 0) {
                return;
            }
            if (ignoreIfAlreadyPresent) {
                for (Arc arc : arcs) {
                    Interval span = arc.span;
                    if (span.equals(key)) {
                        return;
                    }
                }
            }
            arcs.add(new Arc(key, size));
        }

        private void include(List<Arc> arcs, Interval key, MutableInt size) {
            include(arcs, key, size, true);
        }

        Arc[][] groupArcsByFirstVertex(Arc[] arcs) {
            int N = getCount() + 1;
            Arc[][] result = new Arc[N];
            int[] sizes = new int[N];
            for (Arc arc : arcs) {
                int i = arc.span.min;
                sizes[i] = sizes[i] + 1;
            }
            for (int i2 = 0; i2 < sizes.length; i2++) {
                result[i2] = new Arc[sizes[i2]];
            }
            Arrays.fill(sizes, 0);
            for (Arc arc2 : arcs) {
                int i3 = arc2.span.min;
                Arc[] arcArr = result[i3];
                int i4 = sizes[i3];
                sizes[i3] = i4 + 1;
                arcArr[i4] = arc2;
            }
            return result;
        }

        /* JADX WARN: Type inference failed for: r0v0, types: [org.telegram.ui.Components.TableLayout$Axis$1] */
        private Arc[] topologicalSort(Arc[] arcs) {
            return new Object(arcs) { // from class: org.telegram.ui.Components.TableLayout.Axis.1
                Arc[][] arcsByVertex;
                int cursor;
                Arc[] result;
                final /* synthetic */ Arc[] val$arcs;
                int[] visited;

                {
                    Axis.this = this;
                    this.val$arcs = arcs;
                    Arc[] arcArr = new Arc[arcs.length];
                    this.result = arcArr;
                    this.cursor = arcArr.length - 1;
                    this.arcsByVertex = this.groupArcsByFirstVertex(arcs);
                    this.visited = new int[this.getCount() + 1];
                }

                void walk(int loc) {
                    Arc[] arcArr;
                    int[] iArr = this.visited;
                    switch (iArr[loc]) {
                        case 0:
                            iArr[loc] = 1;
                            for (Arc arc : this.arcsByVertex[loc]) {
                                walk(arc.span.max);
                                Arc[] arcArr2 = this.result;
                                int i = this.cursor;
                                this.cursor = i - 1;
                                arcArr2[i] = arc;
                            }
                            this.visited[loc] = 2;
                            return;
                        case 1:
                        default:
                            return;
                    }
                }

                Arc[] sort() {
                    int N = this.arcsByVertex.length;
                    for (int loc = 0; loc < N; loc++) {
                        walk(loc);
                    }
                    return this.result;
                }
            }.sort();
        }

        private Arc[] topologicalSort(List<Arc> arcs) {
            return topologicalSort((Arc[]) arcs.toArray(new Arc[0]));
        }

        private void addComponentSizes(List<Arc> result, PackedMap<Interval, MutableInt> links) {
            for (int i = 0; i < links.keys.length; i++) {
                Interval key = links.keys[i];
                include(result, key, links.values[i], false);
            }
        }

        private Arc[] createArcs() {
            List<Arc> mins = new ArrayList<>();
            List<Arc> maxs = new ArrayList<>();
            addComponentSizes(mins, getForwardLinks());
            addComponentSizes(maxs, getBackwardLinks());
            if (this.orderPreserved) {
                for (int i = 0; i < getCount(); i++) {
                    include(mins, new Interval(i, i + 1), new MutableInt(0));
                }
            }
            int N = getCount();
            include(mins, new Interval(0, N), this.parentMin, false);
            include(maxs, new Interval(N, 0), this.parentMax, false);
            Arc[] sMins = topologicalSort(mins);
            Arc[] sMaxs = topologicalSort(maxs);
            return (Arc[]) TableLayout.append(sMins, sMaxs);
        }

        private void computeArcs() {
            getForwardLinks();
            getBackwardLinks();
        }

        public Arc[] getArcs() {
            if (this.arcs == null) {
                this.arcs = createArcs();
            }
            if (!this.arcsValid) {
                computeArcs();
                this.arcsValid = true;
            }
            return this.arcs;
        }

        private boolean relax(int[] locations, Arc entry) {
            if (!entry.valid) {
                return false;
            }
            Interval span = entry.span;
            int u = span.min;
            int v = span.max;
            int value = entry.value.value;
            int candidate = locations[u] + value;
            if (candidate <= locations[v]) {
                return false;
            }
            locations[v] = candidate;
            return true;
        }

        private void init(int[] locations) {
            Arrays.fill(locations, 0);
        }

        private boolean solve(Arc[] arcs, int[] locations) {
            return solve(arcs, locations, true);
        }

        private boolean solve(Arc[] arcs, int[] locations, boolean modifyOnError) {
            int N = getCount() + 1;
            for (int p = 0; p < arcs.length; p++) {
                init(locations);
                for (int i = 0; i < N; i++) {
                    boolean changed = false;
                    for (Arc arc : arcs) {
                        changed |= relax(locations, arc);
                    }
                    if (!changed) {
                        return true;
                    }
                }
                if (!modifyOnError) {
                    return false;
                }
                boolean[] culprits = new boolean[arcs.length];
                for (int i2 = 0; i2 < N; i2++) {
                    int length = arcs.length;
                    for (int j = 0; j < length; j++) {
                        culprits[j] = culprits[j] | relax(locations, arcs[j]);
                    }
                }
                int i3 = 0;
                while (true) {
                    if (i3 >= arcs.length) {
                        break;
                    }
                    if (culprits[i3]) {
                        Arc arc2 = arcs[i3];
                        if (arc2.span.min >= arc2.span.max) {
                            arc2.valid = false;
                            break;
                        }
                    }
                    i3++;
                }
            }
            return true;
        }

        private void computeMargins(boolean leading) {
            int[] margins = leading ? this.leadingMargins : this.trailingMargins;
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                Child c = TableLayout.this.getChildAt(i);
                LayoutParams lp = c.getLayoutParams();
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                Interval span = spec.span;
                int index = leading ? span.min : span.max;
                margins[index] = Math.max(margins[index], TableLayout.this.getMargin1(c, this.horizontal, leading));
            }
        }

        public int[] getLeadingMargins() {
            if (this.leadingMargins == null) {
                this.leadingMargins = new int[getCount() + 1];
            }
            if (!this.leadingMarginsValid) {
                computeMargins(true);
                this.leadingMarginsValid = true;
            }
            return this.leadingMargins;
        }

        public int[] getTrailingMargins() {
            if (this.trailingMargins == null) {
                this.trailingMargins = new int[getCount() + 1];
            }
            if (!this.trailingMarginsValid) {
                computeMargins(false);
                this.trailingMarginsValid = true;
            }
            return this.trailingMargins;
        }

        private boolean solve(int[] a) {
            return solve(getArcs(), a);
        }

        private boolean computeHasWeights() {
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                Child child = TableLayout.this.getChildAt(i);
                LayoutParams lp = child.getLayoutParams();
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                if (spec.weight != 0.0f) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasWeights() {
            if (!this.hasWeightsValid) {
                this.hasWeights = computeHasWeights();
                this.hasWeightsValid = true;
            }
            return this.hasWeights;
        }

        public int[] getDeltas() {
            if (this.deltas == null) {
                this.deltas = new int[TableLayout.this.getChildCount()];
            }
            return this.deltas;
        }

        private void shareOutDelta(int totalDelta, float totalWeight) {
            Arrays.fill(this.deltas, 0);
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                Child c = TableLayout.this.getChildAt(i);
                LayoutParams lp = c.getLayoutParams();
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                float weight = spec.weight;
                if (weight != 0.0f) {
                    int delta = Math.round((totalDelta * weight) / totalWeight);
                    this.deltas[i] = delta;
                    totalDelta -= delta;
                    totalWeight -= weight;
                }
            }
        }

        private void solveAndDistributeSpace(int[] a) {
            Arrays.fill(getDeltas(), 0);
            solve(a);
            int deltaMax = (this.parentMin.value * TableLayout.this.getChildCount()) + 1;
            if (deltaMax < 2) {
                return;
            }
            int deltaMin = 0;
            float totalWeight = calculateTotalWeight();
            int validDelta = -1;
            boolean validSolution = true;
            while (deltaMin < deltaMax) {
                int delta = (int) ((deltaMin + deltaMax) / 2);
                invalidateValues();
                shareOutDelta(delta, totalWeight);
                validSolution = solve(getArcs(), a, false);
                if (validSolution) {
                    validDelta = delta;
                    deltaMin = delta + 1;
                } else {
                    deltaMax = delta;
                }
            }
            if (validDelta > 0 && !validSolution) {
                invalidateValues();
                shareOutDelta(validDelta, totalWeight);
                solve(a);
            }
        }

        private float calculateTotalWeight() {
            float totalWeight = 0.0f;
            int N = TableLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                Child c = TableLayout.this.getChildAt(i);
                LayoutParams lp = c.getLayoutParams();
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                totalWeight += spec.weight;
            }
            return totalWeight;
        }

        private void computeLocations(int[] a) {
            if (!hasWeights()) {
                solve(a);
            } else {
                solveAndDistributeSpace(a);
            }
            if (!this.orderPreserved) {
                int a0 = a[0];
                int N = a.length;
                for (int i = 0; i < N; i++) {
                    a[i] = a[i] - a0;
                }
            }
        }

        public int[] getLocations() {
            if (this.locations == null) {
                int N = getCount() + 1;
                this.locations = new int[N];
            }
            if (!this.locationsValid) {
                computeLocations(this.locations);
                this.locationsValid = true;
            }
            return this.locations;
        }

        private int size(int[] locations) {
            return locations[getCount()];
        }

        private void setParentConstraints(int min, int max) {
            this.parentMin.value = min;
            this.parentMax.value = -max;
            this.locationsValid = false;
        }

        private int getMeasure(int min, int max) {
            setParentConstraints(min, max);
            return size(getLocations());
        }

        public int getMeasure(int measureSpec) {
            int mode = View.MeasureSpec.getMode(measureSpec);
            int size = View.MeasureSpec.getSize(measureSpec);
            switch (mode) {
                case Integer.MIN_VALUE:
                    return getMeasure(0, size);
                case 0:
                    return getMeasure(0, TableLayout.MAX_SIZE);
                case C.BUFFER_FLAG_ENCRYPTED /* 1073741824 */:
                    return getMeasure(size, size);
                default:
                    return 0;
            }
        }

        public void layout(int size) {
            setParentConstraints(size, size);
            getLocations();
        }

        public void invalidateStructure() {
            this.maxIndex = Integer.MIN_VALUE;
            this.groupBounds = null;
            this.forwardLinks = null;
            this.backwardLinks = null;
            this.leadingMargins = null;
            this.trailingMargins = null;
            this.arcs = null;
            this.locations = null;
            this.deltas = null;
            this.hasWeightsValid = false;
            invalidateValues();
        }

        public void invalidateValues() {
            this.groupBoundsValid = false;
            this.forwardLinksValid = false;
            this.backwardLinksValid = false;
            this.leadingMarginsValid = false;
            this.trailingMarginsValid = false;
            this.arcsValid = false;
            this.locationsValid = false;
        }
    }

    /* loaded from: classes5.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final int DEFAULT_HEIGHT = -2;
        private static final int DEFAULT_MARGIN = Integer.MIN_VALUE;
        private static final Interval DEFAULT_SPAN;
        private static final int DEFAULT_SPAN_SIZE;
        private static final int DEFAULT_WIDTH = -2;
        public Spec columnSpec;
        public Spec rowSpec;

        static {
            Interval interval = new Interval(Integer.MIN_VALUE, -2147483647);
            DEFAULT_SPAN = interval;
            DEFAULT_SPAN_SIZE = interval.size();
        }

        private LayoutParams(int width, int height, int left, int top, int right, int bottom, Spec rowSpec, Spec columnSpec) {
            super(width, height);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
            setMargins(left, top, right, bottom);
            this.rowSpec = rowSpec;
            this.columnSpec = columnSpec;
        }

        public LayoutParams(Spec rowSpec, Spec columnSpec) {
            this(-2, -2, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, rowSpec, columnSpec);
        }

        public LayoutParams() {
            this(Spec.UNDEFINED, Spec.UNDEFINED);
        }

        public LayoutParams(ViewGroup.LayoutParams params) {
            super(params);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams params) {
            super(params);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.MarginLayoutParams) source);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
            this.rowSpec = source.rowSpec;
            this.columnSpec = source.columnSpec;
        }

        public void setGravity(int gravity) {
            this.rowSpec = this.rowSpec.copyWriteAlignment(TableLayout.getAlignment(gravity, false));
            this.columnSpec = this.columnSpec.copyWriteAlignment(TableLayout.getAlignment(gravity, true));
        }

        final void setRowSpecSpan(Interval span) {
            this.rowSpec = this.rowSpec.copyWriteSpan(span);
        }

        final void setColumnSpecSpan(Interval span) {
            this.columnSpec = this.columnSpec.copyWriteSpan(span);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            LayoutParams that = (LayoutParams) o;
            if (this.columnSpec.equals(that.columnSpec) && this.rowSpec.equals(that.rowSpec)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = this.rowSpec.hashCode();
            return (result * 31) + this.columnSpec.hashCode();
        }
    }

    /* loaded from: classes5.dex */
    public static final class Arc {
        public final Interval span;
        public boolean valid = true;
        public final MutableInt value;

        public Arc(Interval span, MutableInt value) {
            this.span = span;
            this.value = value;
        }
    }

    /* loaded from: classes5.dex */
    public static final class MutableInt {
        public int value;

        public MutableInt() {
            reset();
        }

        public MutableInt(int value) {
            this.value = value;
        }

        public void reset() {
            this.value = Integer.MIN_VALUE;
        }
    }

    /* loaded from: classes5.dex */
    public static final class Assoc<K, V> extends ArrayList<Pair<K, V>> {
        private final Class<K> keyType;
        private final Class<V> valueType;

        private Assoc(Class<K> keyType, Class<V> valueType) {
            this.keyType = keyType;
            this.valueType = valueType;
        }

        public static <K, V> Assoc<K, V> of(Class<K> keyType, Class<V> valueType) {
            return new Assoc<>(keyType, valueType);
        }

        public void put(K key, V value) {
            add(Pair.create(key, value));
        }

        public PackedMap<K, V> pack() {
            int N = size();
            Object[] objArr = (Object[]) Array.newInstance((Class<?>) this.keyType, N);
            Object[] objArr2 = (Object[]) Array.newInstance((Class<?>) this.valueType, N);
            for (int i = 0; i < N; i++) {
                objArr[i] = get(i).first;
                objArr2[i] = get(i).second;
            }
            return new PackedMap<>(objArr, objArr2);
        }
    }

    /* loaded from: classes5.dex */
    public static final class PackedMap<K, V> {
        public final int[] index;
        public final K[] keys;
        public final V[] values;

        private PackedMap(K[] keys, V[] values) {
            int[] createIndex = createIndex(keys);
            this.index = createIndex;
            this.keys = (K[]) compact(keys, createIndex);
            this.values = (V[]) compact(values, createIndex);
        }

        public V getValue(int i) {
            return this.values[this.index[i]];
        }

        private static <K> int[] createIndex(K[] keys) {
            int size = keys.length;
            int[] result = new int[size];
            Map<K, Integer> keyToIndex = new HashMap<>();
            for (int i = 0; i < size; i++) {
                K key = keys[i];
                Integer index = keyToIndex.get(key);
                if (index == null) {
                    index = Integer.valueOf(keyToIndex.size());
                    keyToIndex.put(key, index);
                }
                result[i] = index.intValue();
            }
            return result;
        }

        private static <K> K[] compact(K[] a, int[] index) {
            int size = a.length;
            Class<?> componentType = a.getClass().getComponentType();
            K[] result = (K[]) ((Object[]) Array.newInstance(componentType, TableLayout.max2(index, -1) + 1));
            for (int i = 0; i < size; i++) {
                result[index[i]] = a[i];
            }
            return result;
        }
    }

    /* loaded from: classes5.dex */
    public static class Bounds {
        public int after;
        public int before;
        public int flexibility;

        private Bounds() {
            reset();
        }

        protected void reset() {
            this.before = Integer.MIN_VALUE;
            this.after = Integer.MIN_VALUE;
            this.flexibility = 2;
        }

        protected void include(int before, int after) {
            this.before = Math.max(this.before, before);
            this.after = Math.max(this.after, after);
        }

        protected int size(boolean min) {
            if (!min && TableLayout.canStretch(this.flexibility)) {
                return TableLayout.MAX_SIZE;
            }
            return this.before + this.after;
        }

        protected int getOffset(TableLayout gl, Child c, Alignment a, int size, boolean horizontal) {
            return this.before - a.getAlignmentValue(c, size);
        }

        protected final void include(TableLayout gl, Child c, Spec spec, Axis axis, int size) {
            this.flexibility &= spec.getFlexibility();
            boolean z = axis.horizontal;
            Alignment alignment = spec.getAbsoluteAlignment(axis.horizontal);
            int before = alignment.getAlignmentValue(c, size);
            include(before, size - before);
        }
    }

    /* loaded from: classes5.dex */
    public static final class Interval {
        public final int max;
        public final int min;

        public Interval(int min, int max) {
            this.min = min;
            this.max = max;
        }

        int size() {
            return this.max - this.min;
        }

        Interval inverse() {
            return new Interval(this.max, this.min);
        }

        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null || getClass() != that.getClass()) {
                return false;
            }
            Interval interval = (Interval) that;
            if (this.max == interval.max && this.min == interval.min) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = this.min;
            return (result * 31) + this.max;
        }
    }

    /* loaded from: classes5.dex */
    public static class Spec {
        static final float DEFAULT_WEIGHT = 0.0f;
        static final Spec UNDEFINED = TableLayout.spec(Integer.MIN_VALUE);
        final Alignment alignment;
        final Interval span;
        final boolean startDefined;
        float weight;

        private Spec(boolean startDefined, Interval span, Alignment alignment, float weight) {
            this.startDefined = startDefined;
            this.span = span;
            this.alignment = alignment;
            this.weight = weight;
        }

        private Spec(boolean startDefined, int start, int size, Alignment alignment, float weight) {
            this(startDefined, new Interval(start, start + size), alignment, weight);
        }

        public Alignment getAbsoluteAlignment(boolean horizontal) {
            if (this.alignment != TableLayout.UNDEFINED_ALIGNMENT) {
                return this.alignment;
            }
            if (this.weight != 0.0f) {
                return TableLayout.FILL;
            }
            return horizontal ? TableLayout.START : TableLayout.BASELINE;
        }

        final Spec copyWriteSpan(Interval span) {
            return new Spec(this.startDefined, span, this.alignment, this.weight);
        }

        final Spec copyWriteAlignment(Alignment alignment) {
            return new Spec(this.startDefined, this.span, alignment, this.weight);
        }

        final int getFlexibility() {
            return (this.alignment == TableLayout.UNDEFINED_ALIGNMENT && this.weight == 0.0f) ? 0 : 2;
        }

        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null || getClass() != that.getClass()) {
                return false;
            }
            Spec spec = (Spec) that;
            if (this.alignment.equals(spec.alignment) && this.span.equals(spec.span)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = this.span.hashCode();
            return (result * 31) + this.alignment.hashCode();
        }
    }

    public static Spec spec(int start, int size, Alignment alignment, float weight) {
        return new Spec(start != Integer.MIN_VALUE, start, size, alignment, weight);
    }

    public static Spec spec(int start, Alignment alignment, float weight) {
        return spec(start, 1, alignment, weight);
    }

    public static Spec spec(int start, int size, float weight) {
        return spec(start, size, UNDEFINED_ALIGNMENT, weight);
    }

    public static Spec spec(int start, float weight) {
        return spec(start, 1, weight);
    }

    public static Spec spec(int start, int size, Alignment alignment) {
        return spec(start, size, alignment, 0.0f);
    }

    public static Spec spec(int start, Alignment alignment) {
        return spec(start, 1, alignment);
    }

    public static Spec spec(int start, int size) {
        return spec(start, size, UNDEFINED_ALIGNMENT);
    }

    public static Spec spec(int start) {
        return spec(start, 1);
    }

    /* loaded from: classes5.dex */
    public static abstract class Alignment {
        abstract int getAlignmentValue(Child child, int i);

        abstract int getGravityOffset(Child child, int i);

        Alignment() {
        }

        int getSizeInCell(Child view, int viewSize, int cellSize) {
            return viewSize;
        }

        Bounds getBounds() {
            return new Bounds();
        }
    }

    static {
        Alignment alignment = new Alignment() { // from class: org.telegram.ui.Components.TableLayout.2
            @Override // org.telegram.ui.Components.TableLayout.Alignment
            int getGravityOffset(Child view, int cellDelta) {
                return 0;
            }

            @Override // org.telegram.ui.Components.TableLayout.Alignment
            public int getAlignmentValue(Child view, int viewSize) {
                return 0;
            }
        };
        LEADING = alignment;
        Alignment alignment2 = new Alignment() { // from class: org.telegram.ui.Components.TableLayout.3
            @Override // org.telegram.ui.Components.TableLayout.Alignment
            int getGravityOffset(Child view, int cellDelta) {
                return cellDelta;
            }

            @Override // org.telegram.ui.Components.TableLayout.Alignment
            public int getAlignmentValue(Child view, int viewSize) {
                return viewSize;
            }
        };
        TRAILING = alignment2;
        TOP = alignment;
        BOTTOM = alignment2;
        START = alignment;
        END = alignment2;
        LEFT = createSwitchingAlignment(alignment);
        RIGHT = createSwitchingAlignment(alignment2);
    }

    private static Alignment createSwitchingAlignment(final Alignment ltr) {
        return new Alignment() { // from class: org.telegram.ui.Components.TableLayout.4
            @Override // org.telegram.ui.Components.TableLayout.Alignment
            int getGravityOffset(Child view, int cellDelta) {
                return ltr.getGravityOffset(view, cellDelta);
            }

            @Override // org.telegram.ui.Components.TableLayout.Alignment
            public int getAlignmentValue(Child view, int viewSize) {
                return ltr.getAlignmentValue(view, viewSize);
            }
        };
    }

    static boolean canStretch(int flexibility) {
        return (flexibility & 2) != 0;
    }
}
