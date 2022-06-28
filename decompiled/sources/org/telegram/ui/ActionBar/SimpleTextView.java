package org.telegram.ui.ActionBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Components.EmptyStubSpan;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
/* loaded from: classes4.dex */
public class SimpleTextView extends View implements Drawable.Callback {
    private static final int DIST_BETWEEN_SCROLLING_TEXT = 16;
    private static final int PIXELS_PER_SECOND = 50;
    private static final int PIXELS_PER_SECOND_SLOW = 30;
    private static final int SCROLL_DELAY_MS = 500;
    private static final int SCROLL_SLOWDOWN_PX = 100;
    private boolean buildFullLayout;
    private boolean canHideRightDrawable;
    private int currentScrollDelay;
    private Paint fadePaint;
    private Paint fadePaintBack;
    private Layout firstLineLayout;
    private float fullAlpha;
    private Layout fullLayout;
    private int fullLayoutAdditionalWidth;
    private float fullLayoutLeftCharactersOffset;
    private int fullLayoutLeftOffset;
    private long lastUpdateTime;
    private int lastWidth;
    private Layout layout;
    private Drawable leftDrawable;
    private int leftDrawableTopPadding;
    private boolean maybeClick;
    private int minWidth;
    private int minusWidth;
    private int offsetX;
    private int offsetY;
    private Layout partLayout;
    private Drawable replacedDrawable;
    private String replacedText;
    private int replacingDrawableTextIndex;
    private float replacingDrawableTextOffset;
    private Drawable rightDrawable;
    private boolean rightDrawableHidden;
    private View.OnClickListener rightDrawableOnClickListener;
    private int rightDrawableTopPadding;
    public int rightDrawableX;
    public int rightDrawableY;
    private boolean scrollNonFitText;
    private float scrollingOffset;
    private SpannableStringBuilder spannableStringBuilder;
    private CharSequence text;
    private boolean textDoesNotFit;
    private int textHeight;
    private int textWidth;
    private int totalWidth;
    private float touchDownX;
    private float touchDownY;
    private boolean usaAlphaForEmoji;
    private boolean wasLayout;
    private Drawable wrapBackgroundDrawable;
    private int gravity = 51;
    private int maxLines = 1;
    private float rightDrawableScale = 1.0f;
    private int drawablePadding = AndroidUtilities.dp(4.0f);
    private int fullTextMaxLines = 3;
    private List<SpoilerEffect> spoilers = new ArrayList();
    private Stack<SpoilerEffect> spoilersPool = new Stack<>();
    private Path path = new Path();
    private TextPaint textPaint = new TextPaint(1);

    public SimpleTextView(Context context) {
        super(context);
        setImportantForAccessibility(1);
    }

    public void setTextColor(int color) {
        this.textPaint.setColor(color);
        invalidate();
    }

    public void setLinkTextColor(int color) {
        this.textPaint.linkColor = color;
        invalidate();
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.wasLayout = false;
    }

    public void setTextSize(int size) {
        int newSize = AndroidUtilities.dp(size);
        if (newSize == this.textPaint.getTextSize()) {
            return;
        }
        this.textPaint.setTextSize(newSize);
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
    }

    public void setBuildFullLayout(boolean value) {
        this.buildFullLayout = value;
    }

    public void setFullAlpha(float value) {
        this.fullAlpha = value;
        invalidate();
    }

    public float getFullAlpha() {
        return this.fullAlpha;
    }

    public void setScrollNonFitText(boolean value) {
        if (this.scrollNonFitText == value) {
            return;
        }
        this.scrollNonFitText = value;
        if (value) {
            this.fadePaint = new Paint();
            LinearGradient gradient = new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(6.0f), 0.0f, new int[]{-1, 0}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            this.fadePaint.setShader(gradient);
            this.fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            this.fadePaintBack = new Paint();
            LinearGradient gradient2 = new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(6.0f), 0.0f, new int[]{0, -1}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            this.fadePaintBack.setShader(gradient2);
            this.fadePaintBack.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        }
        requestLayout();
    }

    public void setMaxLines(int value) {
        this.maxLines = value;
    }

    public void setGravity(int value) {
        this.gravity = value;
    }

    public void setTypeface(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
    }

    public int getSideDrawablesSize() {
        int size = 0;
        Drawable drawable = this.leftDrawable;
        if (drawable != null) {
            size = 0 + drawable.getIntrinsicWidth() + this.drawablePadding;
        }
        Drawable drawable2 = this.rightDrawable;
        if (drawable2 != null) {
            int dw = (int) (drawable2.getIntrinsicWidth() * this.rightDrawableScale);
            return size + this.drawablePadding + dw;
        }
        return size;
    }

    public Paint getPaint() {
        return this.textPaint;
    }

    private void calcOffset(int width) {
        if (this.layout.getLineCount() > 0) {
            this.textWidth = (int) Math.ceil(this.layout.getLineWidth(0));
            Layout layout = this.fullLayout;
            boolean z = true;
            if (layout != null) {
                this.textHeight = layout.getLineBottom(layout.getLineCount() - 1);
            } else if (this.maxLines > 1 && this.layout.getLineCount() > 0) {
                Layout layout2 = this.layout;
                this.textHeight = layout2.getLineBottom(layout2.getLineCount() - 1);
            } else {
                this.textHeight = this.layout.getLineBottom(0);
            }
            int i = this.gravity;
            if ((i & 7) == 1) {
                this.offsetX = ((width - this.textWidth) / 2) - ((int) this.layout.getLineLeft(0));
            } else if ((i & 7) == 3) {
                Layout layout3 = this.firstLineLayout;
                if (layout3 != null) {
                    this.offsetX = -((int) layout3.getLineLeft(0));
                } else {
                    this.offsetX = -((int) this.layout.getLineLeft(0));
                }
            } else if (this.layout.getLineLeft(0) == 0.0f) {
                Layout layout4 = this.firstLineLayout;
                if (layout4 != null) {
                    this.offsetX = (int) (width - layout4.getLineWidth(0));
                } else {
                    this.offsetX = width - this.textWidth;
                }
            } else {
                this.offsetX = -AndroidUtilities.dp(8.0f);
            }
            this.offsetX += getPaddingLeft();
            if (this.textWidth <= width) {
                z = false;
            }
            this.textDoesNotFit = z;
            Layout layout5 = this.fullLayout;
            if (layout5 != null && this.fullLayoutAdditionalWidth > 0) {
                this.fullLayoutLeftCharactersOffset = layout5.getPrimaryHorizontal(0) - this.firstLineLayout.getPrimaryHorizontal(0);
            }
        }
        int i2 = this.replacingDrawableTextIndex;
        if (i2 >= 0) {
            this.replacingDrawableTextOffset = this.layout.getPrimaryHorizontal(i2);
        } else {
            this.replacingDrawableTextOffset = 0.0f;
        }
    }

    public boolean createLayout(int width) {
        int width2;
        Drawable drawable;
        CharSequence string;
        CharSequence string2;
        CharSequence part;
        CharSequence part2;
        CharSequence text = this.text;
        this.replacingDrawableTextIndex = -1;
        this.rightDrawableHidden = false;
        if (text != null) {
            try {
                Drawable drawable2 = this.leftDrawable;
                if (drawable2 == null) {
                    width2 = width;
                } else {
                    try {
                        width2 = (width - drawable2.getIntrinsicWidth()) - this.drawablePadding;
                    } catch (Exception e) {
                    }
                }
                int rightDrawableWidth = 0;
                if (this.rightDrawable != null) {
                    rightDrawableWidth = (int) (drawable.getIntrinsicWidth() * this.rightDrawableScale);
                    width2 = (width2 - rightDrawableWidth) - this.drawablePadding;
                }
                if (this.replacedText != null && this.replacedDrawable != null) {
                    int indexOf = text.toString().indexOf(this.replacedText);
                    this.replacingDrawableTextIndex = indexOf;
                    if (indexOf >= 0) {
                        SpannableStringBuilder builder = SpannableStringBuilder.valueOf(text);
                        DialogCell.FixedWidthSpan fixedWidthSpan = new DialogCell.FixedWidthSpan(this.replacedDrawable.getIntrinsicWidth());
                        int i = this.replacingDrawableTextIndex;
                        builder.setSpan(fixedWidthSpan, i, this.replacedText.length() + i, 0);
                        text = builder;
                    } else {
                        width2 = (width2 - this.replacedDrawable.getIntrinsicWidth()) - this.drawablePadding;
                    }
                }
                if (this.canHideRightDrawable && rightDrawableWidth != 0 && !text.equals(TextUtils.ellipsize(text, this.textPaint, width2, TextUtils.TruncateAt.END))) {
                    this.rightDrawableHidden = true;
                    width2 = width2 + rightDrawableWidth + this.drawablePadding;
                }
                if (!this.buildFullLayout) {
                    if (this.maxLines > 1) {
                        this.layout = StaticLayoutEx.createStaticLayout(text, 0, text.length(), this.textPaint, width2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, width2, this.maxLines, false);
                    } else {
                        if (this.scrollNonFitText) {
                            string = text;
                        } else {
                            string = TextUtils.ellipsize(text, this.textPaint, width2, TextUtils.TruncateAt.END);
                        }
                        this.layout = new StaticLayout(string, 0, string.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + width2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    }
                } else {
                    CharSequence string3 = TextUtils.ellipsize(text, this.textPaint, width2, TextUtils.TruncateAt.END);
                    if (string3.equals(text)) {
                        this.layout = new StaticLayout(string3, 0, string3.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + width2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        this.fullLayout = null;
                        this.partLayout = null;
                        this.firstLineLayout = null;
                    } else {
                        StaticLayout createStaticLayout = StaticLayoutEx.createStaticLayout(text, 0, text.length(), this.textPaint, width2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, width2, this.fullTextMaxLines, false);
                        this.fullLayout = createStaticLayout;
                        if (createStaticLayout != null) {
                            int end = createStaticLayout.getLineEnd(0);
                            int start = this.fullLayout.getLineStart(1);
                            CharSequence substr = text.subSequence(0, end);
                            SpannableStringBuilder full = SpannableStringBuilder.valueOf(text);
                            full.setSpan(new EmptyStubSpan(), 0, start, 0);
                            if (end < string3.length()) {
                                string2 = string3;
                                part = string2.subSequence(end, string3.length());
                            } else {
                                string2 = string3;
                                part = "…";
                            }
                            CharSequence part3 = part;
                            this.firstLineLayout = new StaticLayout(string2, 0, string2.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + width2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            StaticLayout staticLayout = new StaticLayout(substr, 0, substr.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + width2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            this.layout = staticLayout;
                            if (staticLayout.getLineLeft(0) == 0.0f) {
                                part2 = part3;
                            } else {
                                part2 = "\u200f" + ((Object) part3);
                            }
                            this.partLayout = new StaticLayout(part2, 0, part2.length(), this.textPaint, this.scrollNonFitText ? AndroidUtilities.dp(2000.0f) : AndroidUtilities.dp(8.0f) + width2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            this.fullLayout = StaticLayoutEx.createStaticLayout(full, 0, full.length(), this.textPaint, AndroidUtilities.dp(8.0f) + width2 + this.fullLayoutAdditionalWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, width2 + this.fullLayoutAdditionalWidth, this.fullTextMaxLines, false);
                        }
                    }
                }
                this.spoilersPool.addAll(this.spoilers);
                this.spoilers.clear();
                Layout layout = this.layout;
                if (layout != null && (layout.getText() instanceof Spannable)) {
                    SpoilerEffect.addSpoilers(this, this.layout, this.spoilersPool, this.spoilers);
                }
                calcOffset(width2);
            } catch (Exception e2) {
            }
        } else {
            this.layout = null;
            this.textWidth = 0;
            this.textHeight = 0;
        }
        invalidate();
        return true;
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int finalHeight;
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        if (this.lastWidth != AndroidUtilities.displaySize.x) {
            this.lastWidth = AndroidUtilities.displaySize.x;
            this.scrollingOffset = 0.0f;
            this.currentScrollDelay = 500;
        }
        createLayout(((width - getPaddingLeft()) - getPaddingRight()) - this.minusWidth);
        if (View.MeasureSpec.getMode(heightMeasureSpec) == 1073741824) {
            finalHeight = height;
        } else {
            finalHeight = this.textHeight;
        }
        setMeasuredDimension(width, finalHeight);
        if ((this.gravity & 112) == 16) {
            this.offsetY = ((getMeasuredHeight() - this.textHeight) / 2) + getPaddingTop();
        } else {
            this.offsetY = getPaddingTop();
        }
    }

    @Override // android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        this.wasLayout = true;
    }

    public int getTextWidth() {
        return this.textWidth;
    }

    public int getTextHeight() {
        return this.textHeight;
    }

    public void setLeftDrawableTopPadding(int value) {
        this.leftDrawableTopPadding = value;
    }

    public void setRightDrawableTopPadding(int value) {
        this.rightDrawableTopPadding = value;
    }

    public void setLeftDrawable(int resId) {
        setLeftDrawable(resId == 0 ? null : getContext().getResources().getDrawable(resId));
    }

    public Drawable getLeftDrawable() {
        return this.leftDrawable;
    }

    public void setRightDrawable(int resId) {
        setRightDrawable(resId == 0 ? null : getContext().getResources().getDrawable(resId));
    }

    public void setMinWidth(int width) {
        this.minWidth = width;
    }

    @Override // android.view.View
    public void setBackgroundDrawable(Drawable background) {
        if (this.maxLines > 1) {
            super.setBackgroundDrawable(background);
        } else {
            this.wrapBackgroundDrawable = background;
        }
    }

    @Override // android.view.View
    public Drawable getBackground() {
        Drawable drawable = this.wrapBackgroundDrawable;
        if (drawable != null) {
            return drawable;
        }
        return super.getBackground();
    }

    public void setLeftDrawable(Drawable drawable) {
        Drawable drawable2 = this.leftDrawable;
        if (drawable2 == drawable) {
            return;
        }
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        this.leftDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
    }

    public void replaceTextWithDrawable(Drawable drawable, String replacedText) {
        Drawable drawable2 = this.replacedDrawable;
        if (drawable2 == drawable) {
            return;
        }
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        this.replacedDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
        this.replacedText = replacedText;
    }

    public void setMinusWidth(int value) {
        if (value == this.minusWidth) {
            return;
        }
        this.minusWidth = value;
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
    }

    public Drawable getRightDrawable() {
        return this.rightDrawable;
    }

    public void setRightDrawable(Drawable drawable) {
        Drawable drawable2 = this.rightDrawable;
        if (drawable2 == drawable) {
            return;
        }
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        this.rightDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
    }

    public void setRightDrawableScale(float scale) {
        this.rightDrawableScale = scale;
    }

    public void setSideDrawablesColor(int color) {
        Theme.setDrawableColor(this.rightDrawable, color);
        Theme.setDrawableColor(this.leftDrawable, color);
    }

    public boolean setText(CharSequence value) {
        return setText(value, false);
    }

    public boolean setText(CharSequence value, boolean force) {
        CharSequence charSequence = this.text;
        if (charSequence == null && value == null) {
            return false;
        }
        if (!force && charSequence != null && charSequence.equals(value)) {
            return false;
        }
        this.text = value;
        this.scrollingOffset = 0.0f;
        this.currentScrollDelay = 500;
        recreateLayoutMaybe();
        return true;
    }

    public void setDrawablePadding(int value) {
        if (this.drawablePadding == value) {
            return;
        }
        this.drawablePadding = value;
        if (!recreateLayoutMaybe()) {
            invalidate();
        }
    }

    private boolean recreateLayoutMaybe() {
        if (this.wasLayout && getMeasuredHeight() != 0 && !this.buildFullLayout) {
            boolean result = createLayout(((getMeasuredWidth() - getPaddingLeft()) - getPaddingRight()) - this.minusWidth);
            if ((this.gravity & 112) == 16) {
                this.offsetY = ((getMeasuredHeight() - this.textHeight) / 2) + getPaddingTop();
            } else {
                this.offsetY = getPaddingTop();
            }
            return result;
        }
        requestLayout();
        return true;
    }

    public CharSequence getText() {
        CharSequence charSequence = this.text;
        if (charSequence == null) {
            return "";
        }
        return charSequence;
    }

    public int getLineCount() {
        int count = 0;
        Layout layout = this.layout;
        if (layout != null) {
            count = 0 + layout.getLineCount();
        }
        Layout layout2 = this.fullLayout;
        if (layout2 != null) {
            return count + layout2.getLineCount();
        }
        return count;
    }

    public int getTextStartX() {
        if (this.layout == null) {
            return 0;
        }
        int textOffsetX = 0;
        Drawable drawable = this.leftDrawable;
        if (drawable != null && (this.gravity & 7) == 3) {
            textOffsetX = 0 + this.drawablePadding + drawable.getIntrinsicWidth();
        }
        Drawable drawable2 = this.replacedDrawable;
        if (drawable2 != null && this.replacingDrawableTextIndex < 0 && (this.gravity & 7) == 3) {
            textOffsetX += this.drawablePadding + drawable2.getIntrinsicWidth();
        }
        return ((int) getX()) + this.offsetX + textOffsetX;
    }

    public TextPaint getTextPaint() {
        return this.textPaint;
    }

    public int getTextStartY() {
        if (this.layout == null) {
            return 0;
        }
        return (int) getY();
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Drawable drawable;
        int y;
        int y2;
        super.onDraw(canvas);
        int textOffsetX = 0;
        boolean fade = this.scrollNonFitText && (this.textDoesNotFit || this.scrollingOffset != 0.0f);
        int restore = Integer.MIN_VALUE;
        if (fade) {
            restore = canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), 255, 31);
        }
        this.totalWidth = this.textWidth;
        Drawable drawable2 = this.leftDrawable;
        if (drawable2 != null) {
            int x = (int) (-this.scrollingOffset);
            int i = this.gravity;
            if ((i & 7) == 1) {
                x += this.offsetX;
            }
            if ((i & 112) == 16) {
                y2 = ((getMeasuredHeight() - this.leftDrawable.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding;
            } else {
                y2 = this.leftDrawableTopPadding + ((this.textHeight - drawable2.getIntrinsicHeight()) / 2);
            }
            Drawable drawable3 = this.leftDrawable;
            drawable3.setBounds(x, y2, drawable3.getIntrinsicWidth() + x, this.leftDrawable.getIntrinsicHeight() + y2);
            this.leftDrawable.draw(canvas);
            int i2 = this.gravity;
            if ((i2 & 7) == 3 || (i2 & 7) == 1) {
                textOffsetX = 0 + this.drawablePadding + this.leftDrawable.getIntrinsicWidth();
            }
            this.totalWidth += this.drawablePadding + this.leftDrawable.getIntrinsicWidth();
        }
        Drawable drawable4 = this.replacedDrawable;
        if (drawable4 != null && this.replacedText != null) {
            int x2 = (int) ((-this.scrollingOffset) + this.replacingDrawableTextOffset);
            int i3 = this.gravity;
            if ((i3 & 7) == 1) {
                x2 += this.offsetX;
            }
            if ((i3 & 112) == 16) {
                y = ((getMeasuredHeight() - this.replacedDrawable.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding;
            } else {
                y = this.leftDrawableTopPadding + ((this.textHeight - drawable4.getIntrinsicHeight()) / 2);
            }
            Drawable drawable5 = this.replacedDrawable;
            drawable5.setBounds(x2, y, drawable5.getIntrinsicWidth() + x2, this.replacedDrawable.getIntrinsicHeight() + y);
            this.replacedDrawable.draw(canvas);
            if (this.replacingDrawableTextIndex < 0) {
                int i4 = this.gravity;
                if ((i4 & 7) == 3 || (i4 & 7) == 1) {
                    textOffsetX += this.drawablePadding + this.replacedDrawable.getIntrinsicWidth();
                }
                this.totalWidth += this.drawablePadding + this.replacedDrawable.getIntrinsicWidth();
            }
        }
        if (this.rightDrawable != null && !this.rightDrawableHidden && this.rightDrawableScale > 0.0f) {
            int x3 = this.textWidth + textOffsetX + this.drawablePadding + ((int) (-this.scrollingOffset));
            int i5 = this.gravity;
            if ((i5 & 7) == 1) {
                x3 += this.offsetX;
            } else if ((i5 & 7) == 5) {
                x3 += this.offsetX;
            }
            int dw = (int) (drawable.getIntrinsicWidth() * this.rightDrawableScale);
            int dh = (int) (this.rightDrawable.getIntrinsicHeight() * this.rightDrawableScale);
            int y3 = ((this.textHeight - dh) / 2) + this.rightDrawableTopPadding;
            this.rightDrawable.setBounds(x3, y3, x3 + dw, y3 + dh);
            this.rightDrawableX = (dw >> 1) + x3;
            this.rightDrawableY = (dh >> 1) + y3;
            this.rightDrawable.draw(canvas);
            this.totalWidth += this.drawablePadding + dw;
        }
        int nextScrollX = this.totalWidth + AndroidUtilities.dp(16.0f);
        float f = this.scrollingOffset;
        if (f != 0.0f) {
            Drawable drawable6 = this.leftDrawable;
            if (drawable6 != null) {
                int x4 = ((int) (-f)) + nextScrollX;
                int y4 = ((this.textHeight - drawable6.getIntrinsicHeight()) / 2) + this.leftDrawableTopPadding;
                Drawable drawable7 = this.leftDrawable;
                drawable7.setBounds(x4, y4, drawable7.getIntrinsicWidth() + x4, this.leftDrawable.getIntrinsicHeight() + y4);
                this.leftDrawable.draw(canvas);
            }
            Drawable drawable8 = this.rightDrawable;
            if (drawable8 != null) {
                int x5 = this.textWidth + textOffsetX + this.drawablePadding + ((int) (-this.scrollingOffset)) + nextScrollX;
                int y5 = ((this.textHeight - drawable8.getIntrinsicHeight()) / 2) + this.rightDrawableTopPadding;
                Drawable drawable9 = this.rightDrawable;
                drawable9.setBounds(x5, y5, drawable9.getIntrinsicWidth() + x5, this.rightDrawable.getIntrinsicHeight() + y5);
                this.rightDrawable.draw(canvas);
            }
        }
        if (this.layout != null) {
            Emoji.emojiDrawingUseAlpha = this.usaAlphaForEmoji;
            if (this.wrapBackgroundDrawable != null) {
                int i6 = this.textWidth;
                int cx = ((int) ((this.offsetX + textOffsetX) - this.scrollingOffset)) + (i6 / 2);
                int w = Math.max(i6 + getPaddingLeft() + getPaddingRight(), this.minWidth);
                int x6 = cx - (w / 2);
                this.wrapBackgroundDrawable.setBounds(x6, 0, x6 + w, getMeasuredHeight());
                this.wrapBackgroundDrawable.draw(canvas);
            }
            int cx2 = this.offsetX;
            if (cx2 + textOffsetX != 0 || this.offsetY != 0 || this.scrollingOffset != 0.0f) {
                canvas.save();
                canvas.translate((this.offsetX + textOffsetX) - this.scrollingOffset, this.offsetY);
            }
            drawLayout(canvas);
            if (this.partLayout != null && this.fullAlpha < 1.0f) {
                int prevAlpha = this.textPaint.getAlpha();
                this.textPaint.setAlpha((int) ((1.0f - this.fullAlpha) * 255.0f));
                canvas.save();
                float partOffset = 0.0f;
                if (this.partLayout.getText().length() == 1) {
                    partOffset = AndroidUtilities.dp(this.fullTextMaxLines == 1 ? 0.5f : 4.0f);
                }
                if (this.layout.getLineLeft(0) != 0.0f) {
                    canvas.translate((-this.layout.getLineWidth(0)) + partOffset, 0.0f);
                } else {
                    canvas.translate(this.layout.getLineWidth(0) - partOffset, 0.0f);
                }
                float f2 = this.fullAlpha;
                canvas.translate(((-this.fullLayoutLeftOffset) * f2) + (this.fullLayoutLeftCharactersOffset * f2), 0.0f);
                this.partLayout.draw(canvas);
                canvas.restore();
                this.textPaint.setAlpha(prevAlpha);
            }
            if (this.fullLayout != null && this.fullAlpha > 0.0f) {
                int prevAlpha2 = this.textPaint.getAlpha();
                this.textPaint.setAlpha((int) (this.fullAlpha * 255.0f));
                float f3 = this.fullAlpha;
                float f4 = this.fullLayoutLeftCharactersOffset;
                canvas.translate((((-this.fullLayoutLeftOffset) * f3) + (f3 * f4)) - f4, 0.0f);
                this.fullLayout.draw(canvas);
                this.textPaint.setAlpha(prevAlpha2);
            }
            if (this.scrollingOffset != 0.0f) {
                canvas.translate(nextScrollX, 0.0f);
                drawLayout(canvas);
            }
            if (this.offsetX + textOffsetX != 0 || this.offsetY != 0 || this.scrollingOffset != 0.0f) {
                canvas.restore();
            }
            if (fade) {
                if (this.scrollingOffset < AndroidUtilities.dp(10.0f)) {
                    this.fadePaint.setAlpha((int) ((this.scrollingOffset / AndroidUtilities.dp(10.0f)) * 255.0f));
                } else if (this.scrollingOffset > (this.totalWidth + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(10.0f)) {
                    float dist = this.scrollingOffset - ((this.totalWidth + AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(10.0f));
                    this.fadePaint.setAlpha((int) ((1.0f - (dist / AndroidUtilities.dp(10.0f))) * 255.0f));
                } else {
                    this.fadePaint.setAlpha(255);
                }
                canvas.drawRect(0.0f, 0.0f, AndroidUtilities.dp(6.0f), getMeasuredHeight(), this.fadePaint);
                canvas.save();
                canvas.translate(getMeasuredWidth() - AndroidUtilities.dp(6.0f), 0.0f);
                canvas.drawRect(0.0f, 0.0f, AndroidUtilities.dp(6.0f), getMeasuredHeight(), this.fadePaintBack);
                canvas.restore();
            }
            updateScrollAnimation();
            Emoji.emojiDrawingUseAlpha = true;
        }
        if (fade) {
            canvas.restoreToCount(restore);
        }
    }

    private void drawLayout(Canvas canvas) {
        if (this.fullAlpha > 0.0f && this.fullLayoutLeftOffset != 0) {
            canvas.save();
            float f = this.fullAlpha;
            canvas.translate(((-this.fullLayoutLeftOffset) * f) + (this.fullLayoutLeftCharactersOffset * f), 0.0f);
            canvas.save();
            clipOutSpoilers(canvas);
            this.layout.draw(canvas);
            canvas.restore();
            drawSpoilers(canvas);
            canvas.restore();
            return;
        }
        canvas.save();
        clipOutSpoilers(canvas);
        this.layout.draw(canvas);
        canvas.restore();
        drawSpoilers(canvas);
    }

    private void clipOutSpoilers(Canvas canvas) {
        this.path.rewind();
        for (SpoilerEffect eff : this.spoilers) {
            Rect b = eff.getBounds();
            this.path.addRect(b.left, b.top, b.right, b.bottom, Path.Direction.CW);
        }
        canvas.clipPath(this.path, Region.Op.DIFFERENCE);
    }

    private void drawSpoilers(Canvas canvas) {
        for (SpoilerEffect eff : this.spoilers) {
            eff.draw(canvas);
        }
    }

    private void updateScrollAnimation() {
        float pixelsPerSecond;
        if (this.scrollNonFitText) {
            if (!this.textDoesNotFit && this.scrollingOffset == 0.0f) {
                return;
            }
            long newUpdateTime = SystemClock.elapsedRealtime();
            long dt = newUpdateTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            int i = this.currentScrollDelay;
            if (i > 0) {
                this.currentScrollDelay = (int) (i - dt);
            } else {
                int totalDistance = this.totalWidth + AndroidUtilities.dp(16.0f);
                if (this.scrollingOffset < AndroidUtilities.dp(100.0f)) {
                    pixelsPerSecond = ((this.scrollingOffset / AndroidUtilities.dp(100.0f)) * 20.0f) + 30.0f;
                } else if (this.scrollingOffset >= totalDistance - AndroidUtilities.dp(100.0f)) {
                    float dist = this.scrollingOffset - (totalDistance - AndroidUtilities.dp(100.0f));
                    pixelsPerSecond = 50.0f - ((dist / AndroidUtilities.dp(100.0f)) * 20.0f);
                } else {
                    pixelsPerSecond = 50.0f;
                }
                float dp = this.scrollingOffset + ((((float) dt) / 1000.0f) * AndroidUtilities.dp(pixelsPerSecond));
                this.scrollingOffset = dp;
                this.lastUpdateTime = newUpdateTime;
                if (dp > totalDistance) {
                    this.scrollingOffset = 0.0f;
                    this.currentScrollDelay = 500;
                }
            }
            invalidate();
        }
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable who) {
        Drawable drawable = this.leftDrawable;
        if (who == drawable) {
            invalidate(drawable.getBounds());
            return;
        }
        Drawable drawable2 = this.rightDrawable;
        if (who == drawable2) {
            invalidate(drawable2.getBounds());
            return;
        }
        Drawable drawable3 = this.replacedDrawable;
        if (who == drawable3) {
            invalidate(drawable3.getBounds());
        }
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setVisibleToUser(true);
        info.setClassName("android.widget.TextView");
        info.setText(this.text);
    }

    public void setFullLayoutAdditionalWidth(int fullLayoutAdditionalWidth, int fullLayoutLeftOffset) {
        if (this.fullLayoutAdditionalWidth != fullLayoutAdditionalWidth || this.fullLayoutLeftOffset != fullLayoutLeftOffset) {
            this.fullLayoutAdditionalWidth = fullLayoutAdditionalWidth;
            this.fullLayoutLeftOffset = fullLayoutLeftOffset;
            createLayout(getMeasuredWidth() - this.minusWidth);
        }
    }

    public void setFullTextMaxLines(int fullTextMaxLines) {
        this.fullTextMaxLines = fullTextMaxLines;
    }

    public int getTextColor() {
        return this.textPaint.getColor();
    }

    public void setCanHideRightDrawable(boolean b) {
        this.canHideRightDrawable = b;
    }

    public void setRightDrawableOnClick(View.OnClickListener onClickListener) {
        this.rightDrawableOnClickListener = onClickListener;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        if (this.rightDrawableOnClickListener != null && this.rightDrawable != null) {
            AndroidUtilities.rectTmp.set(this.rightDrawableX - AndroidUtilities.dp(16.0f), this.rightDrawableY - AndroidUtilities.dp(16.0f), this.rightDrawableX + AndroidUtilities.dp(16.0f), this.rightDrawableY + AndroidUtilities.dp(16.0f));
            if (event.getAction() == 0 && AndroidUtilities.rectTmp.contains((int) event.getX(), (int) event.getY())) {
                this.maybeClick = true;
                this.touchDownX = event.getX();
                this.touchDownY = event.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
            } else if (event.getAction() == 2 && this.maybeClick) {
                if (Math.abs(event.getX() - this.touchDownX) >= AndroidUtilities.touchSlop || Math.abs(event.getY() - this.touchDownY) >= AndroidUtilities.touchSlop) {
                    this.maybeClick = false;
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
            } else if (event.getAction() == 1 || event.getAction() == 3) {
                if (this.maybeClick && event.getAction() == 1) {
                    this.rightDrawableOnClickListener.onClick(this);
                }
                this.maybeClick = false;
                getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return super.onTouchEvent(event) || this.maybeClick;
    }
}
