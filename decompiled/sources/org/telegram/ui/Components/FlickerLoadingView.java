package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.View;
import com.google.android.exoplayer2.C;
import java.util.Random;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class FlickerLoadingView extends View {
    public static final int AUDIO_TYPE = 4;
    public static final int BOTS_MENU_TYPE = 11;
    public static final int CALL_LOG_TYPE = 8;
    public static final int CHAT_THEMES_TYPE = 14;
    public static final int CONTACT_TYPE = 18;
    public static final int DIALOG_CELL_TYPE = 7;
    public static final int DIALOG_TYPE = 1;
    public static final int FILES_TYPE = 3;
    public static final int INVITE_LINKS_TYPE = 9;
    public static final int LIMIT_REACHED_GROUPS = 21;
    public static final int LIMIT_REACHED_LINKS = 22;
    public static final int LINKS_TYPE = 5;
    public static final int MEMBER_REQUESTS_TYPE = 15;
    public static final int MESSAGE_SEEN_TYPE = 13;
    public static final int PHOTOS_TYPE = 2;
    public static final int QR_TYPE = 17;
    public static final int REACTED_TYPE = 16;
    public static final int SHARE_ALERT_TYPE = 12;
    public static final int STICKERS_TYPE = 19;
    public static final int USERS2_TYPE = 10;
    public static final int USERS_TYPE = 6;
    private Paint backgroundPaint;
    private int color0;
    private int color1;
    private String colorKey1;
    private String colorKey2;
    private String colorKey3;
    FlickerLoadingView globalGradientView;
    private LinearGradient gradient;
    private int gradientWidth;
    private Paint headerPaint;
    private boolean ignoreHeightCheck;
    private boolean isSingleCell;
    private int itemsCount;
    private long lastUpdateTime;
    private Matrix matrix;
    private int paddingLeft;
    private int paddingTop;
    private Paint paint;
    private int parentHeight;
    private int parentWidth;
    private float parentXOffset;
    float[] randomParams;
    private RectF rectF;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean showDate;
    private int skipDrawItemsCount;
    private int totalTranslation;
    private boolean useHeaderOffset;
    private int viewType;

    public void setViewType(int type) {
        this.viewType = type;
        if (type == 11) {
            Random random = new Random();
            this.randomParams = new float[2];
            for (int i = 0; i < 2; i++) {
                this.randomParams[i] = Math.abs(random.nextInt() % 1000) / 1000.0f;
            }
        }
        invalidate();
    }

    public void setIsSingleCell(boolean b) {
        this.isSingleCell = b;
    }

    public int getViewType() {
        return this.viewType;
    }

    public int getColumnsCount() {
        return 2;
    }

    public void setColors(String key1, String key2, String key3) {
        this.colorKey1 = key1;
        this.colorKey2 = key2;
        this.colorKey3 = key3;
        invalidate();
    }

    public FlickerLoadingView(Context context) {
        this(context, null);
    }

    public FlickerLoadingView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.paint = new Paint();
        this.headerPaint = new Paint();
        this.rectF = new RectF();
        this.showDate = true;
        this.colorKey1 = Theme.key_windowBackgroundWhite;
        this.colorKey2 = Theme.key_windowBackgroundGray;
        this.itemsCount = 1;
        this.resourcesProvider = resourcesProvider;
        this.matrix = new Matrix();
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.isSingleCell) {
            int i = this.itemsCount;
            if (i > 1 && this.ignoreHeightCheck) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(getCellHeight(View.MeasureSpec.getSize(widthMeasureSpec)) * this.itemsCount, C.BUFFER_FLAG_ENCRYPTED));
                return;
            } else if (i > 1 && View.MeasureSpec.getSize(heightMeasureSpec) > 0) {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(heightMeasureSpec), getCellHeight(View.MeasureSpec.getSize(widthMeasureSpec)) * this.itemsCount), C.BUFFER_FLAG_ENCRYPTED));
                return;
            } else {
                super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(getCellHeight(View.MeasureSpec.getSize(widthMeasureSpec)), C.BUFFER_FLAG_ENCRYPTED));
                return;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Paint paint;
        int h;
        int h2;
        int h3;
        Paint paint2 = this.paint;
        if (this.globalGradientView == null) {
            paint = paint2;
        } else {
            if (getParent() != null) {
                View parent = (View) getParent();
                this.globalGradientView.setParentSize(parent.getMeasuredWidth(), parent.getMeasuredHeight(), -getX());
            }
            Paint paint3 = this.globalGradientView.paint;
            paint = paint3;
        }
        updateColors();
        updateGradient();
        int h4 = this.paddingTop;
        int r = 1107296256;
        if (this.useHeaderOffset) {
            int h5 = h4 + AndroidUtilities.dp(32.0f);
            String str = this.colorKey3;
            if (str != null) {
                this.headerPaint.setColor(getThemedColor(str));
            }
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(32.0f), this.colorKey3 != null ? this.headerPaint : paint);
            h4 = h5;
        }
        float f = 16.0f;
        float f2 = 28.0f;
        float f3 = 24.0f;
        if (getViewType() != 7) {
            float f4 = 25.0f;
            if (getViewType() == 18) {
                int h6 = h4;
                int k = 0;
                while (h6 <= getMeasuredHeight()) {
                    int r2 = AndroidUtilities.dp(25.0f);
                    canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(9.0f) + r2), AndroidUtilities.dp(r) + h6, r2, paint);
                    int firstNameWidth = k % 2 == 0 ? 52 : 72;
                    this.rectF.set(AndroidUtilities.dp(76), AndroidUtilities.dp(20.0f) + h6, AndroidUtilities.dp(76 + firstNameWidth), h6 + AndroidUtilities.dp(28.0f));
                    checkRtl(this.rectF);
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                    this.rectF.set(AndroidUtilities.dp(76 + firstNameWidth + 8), AndroidUtilities.dp(20.0f) + h6, AndroidUtilities.dp(76 + firstNameWidth + 8 + 84), h6 + AndroidUtilities.dp(28.0f));
                    checkRtl(this.rectF);
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                    this.rectF.set(AndroidUtilities.dp(76), AndroidUtilities.dp(42.0f) + h6, AndroidUtilities.dp(76 + 64), AndroidUtilities.dp(50.0f) + h6);
                    checkRtl(this.rectF);
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                    canvas.drawLine(AndroidUtilities.dp(76), getCellHeight(getMeasuredWidth()) + h6, getMeasuredWidth(), getCellHeight(getMeasuredWidth()) + h6, paint);
                    h6 += getCellHeight(getMeasuredWidth());
                    k++;
                    if (this.isSingleCell && k >= this.itemsCount) {
                        break;
                    }
                    r = 1107296256;
                }
            } else if (getViewType() == 19) {
                int h7 = h4;
                int k2 = 0;
                while (h7 <= getMeasuredHeight()) {
                    int r3 = AndroidUtilities.dp(20.0f);
                    canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(9.0f) + r3), AndroidUtilities.dp(29.0f) + h7, r3, paint);
                    int titleWidth = k2 % 2 == 0 ? 92 : 128;
                    this.rectF.set(AndroidUtilities.dp(76), AndroidUtilities.dp(16.0f) + h7, AndroidUtilities.dp(76 + titleWidth), AndroidUtilities.dp(24.0f) + h7);
                    checkRtl(this.rectF);
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                    this.rectF.set(AndroidUtilities.dp(76), AndroidUtilities.dp(38.0f) + h7, AndroidUtilities.dp(76 + 164), AndroidUtilities.dp(46.0f) + h7);
                    checkRtl(this.rectF);
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                    canvas.drawLine(AndroidUtilities.dp(76), getCellHeight(getMeasuredWidth()) + h7, getMeasuredWidth(), getCellHeight(getMeasuredWidth()) + h7, paint);
                    h7 += getCellHeight(getMeasuredWidth());
                    k2++;
                    if (this.isSingleCell && k2 >= this.itemsCount) {
                        break;
                    }
                }
            } else {
                float f5 = 260.0f;
                float f6 = 140.0f;
                float f7 = 68.0f;
                int i = 1;
                if (getViewType() == 1) {
                    int k3 = 0;
                    while (h4 <= getMeasuredHeight()) {
                        int r4 = AndroidUtilities.dp(f4);
                        canvas.drawCircle(checkRtl(AndroidUtilities.dp(9.0f) + r4), (AndroidUtilities.dp(78.0f) >> i) + h4, r4, paint);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(20.0f) + h4, AndroidUtilities.dp(f6), h4 + AndroidUtilities.dp(28.0f));
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(42.0f) + h4, AndroidUtilities.dp(260.0f), AndroidUtilities.dp(50.0f) + h4);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        if (this.showDate) {
                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(20.0f) + h4, getMeasuredWidth() - AndroidUtilities.dp(12.0f), h4 + AndroidUtilities.dp(28.0f));
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        }
                        h4 += getCellHeight(getMeasuredWidth());
                        k3++;
                        if (this.isSingleCell && k3 >= this.itemsCount) {
                            break;
                        }
                        f6 = 140.0f;
                        i = 1;
                        f4 = 25.0f;
                    }
                } else if (getViewType() == 2) {
                    int photoWidth = (getMeasuredWidth() - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount();
                    int h8 = h4;
                    int k4 = 0;
                    while (true) {
                        if (h8 >= getMeasuredHeight() && !this.isSingleCell) {
                            break;
                        }
                        for (int i2 = 0; i2 < getColumnsCount(); i2++) {
                            if (k4 != 0 || i2 >= this.skipDrawItemsCount) {
                                int x = i2 * (photoWidth + AndroidUtilities.dp(2.0f));
                                canvas.drawRect(x, h8, x + photoWidth, h8 + photoWidth, paint);
                            }
                        }
                        h8 += photoWidth + AndroidUtilities.dp(2.0f);
                        k4++;
                        if (this.isSingleCell && k4 >= 2) {
                            break;
                        }
                    }
                } else if (getViewType() == 3) {
                    int k5 = 0;
                    while (h4 <= getMeasuredHeight()) {
                        this.rectF.set(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f) + h4, AndroidUtilities.dp(52.0f), AndroidUtilities.dp(48.0f) + h4);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(12.0f) + h4, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(20.0f) + h4);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(34.0f) + h4, AndroidUtilities.dp(260.0f), AndroidUtilities.dp(42.0f) + h4);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        if (this.showDate) {
                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(12.0f) + h4, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(20.0f) + h4);
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        }
                        h4 += getCellHeight(getMeasuredWidth());
                        k5++;
                        if (this.isSingleCell && k5 >= this.itemsCount) {
                            break;
                        }
                    }
                } else if (getViewType() == 4) {
                    int k6 = 0;
                    while (h4 <= getMeasuredHeight()) {
                        int radius = AndroidUtilities.dp(44.0f) >> 1;
                        canvas.drawCircle(checkRtl(AndroidUtilities.dp(12.0f) + radius), AndroidUtilities.dp(6.0f) + h4 + radius, radius, paint);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(12.0f) + h4, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(20.0f) + h4);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(34.0f) + h4, AndroidUtilities.dp(260.0f), AndroidUtilities.dp(42.0f) + h4);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        if (this.showDate) {
                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(12.0f) + h4, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(20.0f) + h4);
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        }
                        h4 += getCellHeight(getMeasuredWidth());
                        k6++;
                        if (this.isSingleCell && k6 >= this.itemsCount) {
                            break;
                        }
                    }
                } else if (getViewType() == 5) {
                    int k7 = 0;
                    while (h4 <= getMeasuredHeight()) {
                        this.rectF.set(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(11.0f) + h4, AndroidUtilities.dp(62.0f), AndroidUtilities.dp(63.0f) + h4);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(12.0f) + h4, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(20.0f) + h4);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(34.0f) + h4, AndroidUtilities.dp(268.0f), AndroidUtilities.dp(42.0f) + h4);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        this.rectF.set(AndroidUtilities.dp(68.0f), AndroidUtilities.dp(54.0f) + h4, AndroidUtilities.dp(188.0f), AndroidUtilities.dp(62.0f) + h4);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        if (this.showDate) {
                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(12.0f) + h4, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(20.0f) + h4);
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        }
                        h4 += getCellHeight(getMeasuredWidth());
                        k7++;
                        if (this.isSingleCell && k7 >= this.itemsCount) {
                            break;
                        }
                    }
                } else {
                    if (getViewType() != 6) {
                        if (getViewType() != 10) {
                            if (getViewType() != 8) {
                                if (getViewType() != 9) {
                                    float f8 = 0.5f;
                                    if (getViewType() != 11) {
                                        if (getViewType() != 12) {
                                            if (getViewType() != 13) {
                                                if (getViewType() == 14 || getViewType() == 17) {
                                                    int x2 = AndroidUtilities.dp(12.0f);
                                                    int itemWidth = AndroidUtilities.dp(77.0f);
                                                    int INNER_RECT_SPACE = AndroidUtilities.dp(4.0f);
                                                    float BUBBLE_HEIGHT = AndroidUtilities.dp(21.0f);
                                                    float BUBBLE_WIDTH = AndroidUtilities.dp(41.0f);
                                                    while (x2 < getMeasuredWidth()) {
                                                        if (this.backgroundPaint == null) {
                                                            Paint paint4 = new Paint(1);
                                                            this.backgroundPaint = paint4;
                                                            paint4.setColor(Theme.getColor(Theme.key_dialogBackground));
                                                        }
                                                        AndroidUtilities.rectTmp.set(AndroidUtilities.dp(4.0f) + x2, AndroidUtilities.dp(4.0f), (x2 + itemWidth) - AndroidUtilities.dp(4.0f), getMeasuredHeight() - AndroidUtilities.dp(4.0f));
                                                        canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), paint);
                                                        if (getViewType() == 14) {
                                                            float bubbleTop = AndroidUtilities.dp(8.0f) + INNER_RECT_SPACE;
                                                            float bubbleLeft = AndroidUtilities.dp(22.0f) + INNER_RECT_SPACE;
                                                            this.rectF.set(x2 + bubbleLeft, bubbleTop, x2 + bubbleLeft + BUBBLE_WIDTH, bubbleTop + BUBBLE_HEIGHT);
                                                            RectF rectF = this.rectF;
                                                            canvas.drawRoundRect(rectF, rectF.height() * f8, this.rectF.height() * f8, this.backgroundPaint);
                                                            float bubbleLeft2 = AndroidUtilities.dp(5.0f) + INNER_RECT_SPACE;
                                                            float bubbleTop2 = bubbleTop + AndroidUtilities.dp(4.0f) + BUBBLE_HEIGHT;
                                                            this.rectF.set(x2 + bubbleLeft2, bubbleTop2, x2 + bubbleLeft2 + BUBBLE_WIDTH, bubbleTop2 + BUBBLE_HEIGHT);
                                                            RectF rectF2 = this.rectF;
                                                            canvas.drawRoundRect(rectF2, rectF2.height() * f8, this.rectF.height() * f8, this.backgroundPaint);
                                                            h3 = h4;
                                                        } else if (getViewType() != 17) {
                                                            h3 = h4;
                                                        } else {
                                                            float radius2 = AndroidUtilities.dp(5.0f);
                                                            float squareSize = AndroidUtilities.dp(32.0f);
                                                            float left = x2 + ((itemWidth - squareSize) / 2.0f);
                                                            int top = AndroidUtilities.dp(21.0f);
                                                            h3 = h4;
                                                            AndroidUtilities.rectTmp.set(left, top, left + squareSize, top + AndroidUtilities.dp(32.0f));
                                                            canvas.drawRoundRect(AndroidUtilities.rectTmp, radius2, radius2, this.backgroundPaint);
                                                        }
                                                        canvas.drawCircle((itemWidth / 2) + x2, getMeasuredHeight() - AndroidUtilities.dp(20.0f), AndroidUtilities.dp(8.0f), this.backgroundPaint);
                                                        x2 += itemWidth;
                                                        h4 = h3;
                                                        f8 = 0.5f;
                                                    }
                                                    h2 = h4;
                                                } else if (getViewType() != 15) {
                                                    if (getViewType() != 16) {
                                                        int i3 = this.viewType;
                                                        if (i3 != 21) {
                                                            if (i3 != 22) {
                                                                h2 = h4;
                                                            } else {
                                                                int k8 = 0;
                                                                while (h4 <= getMeasuredHeight()) {
                                                                    int r5 = AndroidUtilities.dp(48.0f) >> 1;
                                                                    canvas.drawCircle(checkRtl(AndroidUtilities.dp(20.0f) + r5), AndroidUtilities.dp(6.0f) + h4 + r5, r5, paint);
                                                                    this.rectF.set(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(16.0f) + h4, AndroidUtilities.dp(140.0f), h4 + AndroidUtilities.dp(24.0f));
                                                                    checkRtl(this.rectF);
                                                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                                                    this.rectF.set(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(38.0f) + h4, AndroidUtilities.dp(260.0f), AndroidUtilities.dp(46.0f) + h4);
                                                                    checkRtl(this.rectF);
                                                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                                                    h4 += getCellHeight(getMeasuredWidth());
                                                                    k8++;
                                                                    if (this.isSingleCell && k8 >= this.itemsCount) {
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                        } else {
                                                            int k9 = 0;
                                                            while (h4 <= getMeasuredHeight()) {
                                                                int r6 = AndroidUtilities.dp(46.0f) >> 1;
                                                                canvas.drawCircle(checkRtl(AndroidUtilities.dp(20.0f) + r6), (AndroidUtilities.dp(58.0f) >> 1) + h4, r6, paint);
                                                                this.rectF.set(AndroidUtilities.dp(74.0f), AndroidUtilities.dp(16.0f) + h4, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(24.0f) + h4);
                                                                checkRtl(this.rectF);
                                                                canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                                                this.rectF.set(AndroidUtilities.dp(74.0f), AndroidUtilities.dp(38.0f) + h4, AndroidUtilities.dp(260.0f), AndroidUtilities.dp(46.0f) + h4);
                                                                checkRtl(this.rectF);
                                                                canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                                                h4 += getCellHeight(getMeasuredWidth());
                                                                k9++;
                                                                if (this.isSingleCell && k9 >= this.itemsCount) {
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        int k10 = 0;
                                                        while (h4 <= getMeasuredHeight()) {
                                                            int r7 = AndroidUtilities.dp(18.0f);
                                                            canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(8.0f) + r7), AndroidUtilities.dp(24.0f) + h4, r7, paint);
                                                            this.rectF.set(this.paddingLeft + AndroidUtilities.dp(58.0f), AndroidUtilities.dp(20.0f) + h4, getWidth() - AndroidUtilities.dp(53.0f), AndroidUtilities.dp(28.0f) + h4);
                                                            checkRtl(this.rectF);
                                                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), paint);
                                                            if (k10 < 4) {
                                                                int r8 = AndroidUtilities.dp(12.0f);
                                                                canvas.drawCircle(checkRtl((getWidth() - AndroidUtilities.dp(12.0f)) - r8), AndroidUtilities.dp(24.0f) + h4, r8, paint);
                                                            }
                                                            h4 += getCellHeight(getMeasuredWidth());
                                                            k10++;
                                                            if (this.isSingleCell && k10 >= this.itemsCount) {
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    int count = 0;
                                                    int radius3 = AndroidUtilities.dp(23.0f);
                                                    int rectRadius = AndroidUtilities.dp(4.0f);
                                                    while (h4 <= getMeasuredHeight()) {
                                                        canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(12.0f)) + radius3, AndroidUtilities.dp(8.0f) + h4 + radius3, radius3, paint);
                                                        this.rectF.set(this.paddingLeft + AndroidUtilities.dp(74.0f), AndroidUtilities.dp(12.0f) + h4, this.paddingLeft + AndroidUtilities.dp(260.0f), AndroidUtilities.dp(20.0f) + h4);
                                                        checkRtl(this.rectF);
                                                        canvas.drawRoundRect(this.rectF, rectRadius, rectRadius, paint);
                                                        this.rectF.set(this.paddingLeft + AndroidUtilities.dp(74.0f), AndroidUtilities.dp(36.0f) + h4, this.paddingLeft + AndroidUtilities.dp(140.0f), AndroidUtilities.dp(42.0f) + h4);
                                                        checkRtl(this.rectF);
                                                        canvas.drawRoundRect(this.rectF, rectRadius, rectRadius, paint);
                                                        h4 += getCellHeight(getMeasuredWidth());
                                                        count++;
                                                        if (this.isSingleCell && count >= this.itemsCount) {
                                                            break;
                                                        }
                                                    }
                                                }
                                            } else {
                                                float cy = getMeasuredHeight() / 2.0f;
                                                AndroidUtilities.rectTmp.set(AndroidUtilities.dp(40.0f), cy - AndroidUtilities.dp(4.0f), getMeasuredWidth() - AndroidUtilities.dp(120.0f), AndroidUtilities.dp(4.0f) + cy);
                                                canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                                if (this.backgroundPaint == null) {
                                                    Paint paint5 = new Paint(1);
                                                    this.backgroundPaint = paint5;
                                                    paint5.setColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
                                                }
                                                for (int i4 = 0; i4 < 3; i4++) {
                                                    canvas.drawCircle((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i4), cy, AndroidUtilities.dp(13.0f), this.backgroundPaint);
                                                    canvas.drawCircle((getMeasuredWidth() - AndroidUtilities.dp(56.0f)) + AndroidUtilities.dp(13.0f) + (AndroidUtilities.dp(12.0f) * i4), cy, AndroidUtilities.dp(12.0f), paint);
                                                }
                                                h2 = h4;
                                            }
                                        } else {
                                            int k11 = 0;
                                            int h9 = h4 + AndroidUtilities.dp(14.0f);
                                            while (h9 <= getMeasuredHeight()) {
                                                int part = getMeasuredWidth() / 4;
                                                for (int i5 = 0; i5 < 4; i5++) {
                                                    float cx = (part * i5) + (part / 2.0f);
                                                    canvas.drawCircle(cx, AndroidUtilities.dp(7.0f) + h9 + (AndroidUtilities.dp(56.0f) / 2.0f), AndroidUtilities.dp(28.0f), paint);
                                                    float y = AndroidUtilities.dp(7.0f) + h9 + AndroidUtilities.dp(56.0f) + AndroidUtilities.dp(16.0f);
                                                    AndroidUtilities.rectTmp.set(cx - AndroidUtilities.dp(24.0f), y - AndroidUtilities.dp(4.0f), AndroidUtilities.dp(24.0f) + cx, AndroidUtilities.dp(4.0f) + y);
                                                    canvas.drawRoundRect(AndroidUtilities.rectTmp, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                                }
                                                int i6 = getMeasuredWidth();
                                                h9 += getCellHeight(i6);
                                                k11++;
                                                if (this.isSingleCell) {
                                                    break;
                                                }
                                            }
                                        }
                                    } else {
                                        int k12 = 0;
                                        while (h4 <= getMeasuredHeight()) {
                                            this.rectF.set(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(14.0f), (getMeasuredWidth() * 0.5f) + AndroidUtilities.dp(this.randomParams[0] * 40.0f), AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f));
                                            checkRtl(this.rectF);
                                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(18.0f), AndroidUtilities.dp(14.0f), (getMeasuredWidth() - (getMeasuredWidth() * 0.2f)) - AndroidUtilities.dp(this.randomParams[0] * 20.0f), AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(8.0f));
                                            checkRtl(this.rectF);
                                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                            h4 += getCellHeight(getMeasuredWidth());
                                            k12++;
                                            if (this.isSingleCell && k12 >= this.itemsCount) {
                                                break;
                                            }
                                        }
                                    }
                                } else {
                                    int k13 = 0;
                                    while (h4 <= getMeasuredHeight()) {
                                        int childH = getCellHeight(getMeasuredWidth());
                                        int r9 = AndroidUtilities.dp(32.0f) / 2;
                                        canvas.drawCircle(checkRtl(AndroidUtilities.dp(35.0f)), (childH >> 1) + h4, r9, paint);
                                        this.rectF.set(AndroidUtilities.dp(72.0f), AndroidUtilities.dp(16.0f) + h4, AndroidUtilities.dp(268.0f), h4 + AndroidUtilities.dp(24.0f));
                                        checkRtl(this.rectF);
                                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                        this.rectF.set(AndroidUtilities.dp(72.0f), AndroidUtilities.dp(38.0f) + h4, AndroidUtilities.dp(140.0f), AndroidUtilities.dp(46.0f) + h4);
                                        checkRtl(this.rectF);
                                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                        if (this.showDate) {
                                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(16.0f) + h4, getMeasuredWidth() - AndroidUtilities.dp(12.0f), h4 + AndroidUtilities.dp(24.0f));
                                            checkRtl(this.rectF);
                                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                        }
                                        h4 += getCellHeight(getMeasuredWidth());
                                        k13++;
                                        if (this.isSingleCell && k13 >= this.itemsCount) {
                                            break;
                                        }
                                    }
                                }
                            } else {
                                int k14 = 0;
                                while (h4 <= getMeasuredHeight()) {
                                    int r10 = AndroidUtilities.dp(23.0f);
                                    canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(11.0f) + r10), (AndroidUtilities.dp(64.0f) >> 1) + h4, r10, paint);
                                    this.rectF.set(this.paddingLeft + AndroidUtilities.dp(68.0f), AndroidUtilities.dp(17.0f) + h4, this.paddingLeft + AndroidUtilities.dp(140.0f), AndroidUtilities.dp(25.0f) + h4);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                    this.rectF.set(this.paddingLeft + AndroidUtilities.dp(68.0f), AndroidUtilities.dp(39.0f) + h4, this.paddingLeft + AndroidUtilities.dp(260.0f), AndroidUtilities.dp(47.0f) + h4);
                                    checkRtl(this.rectF);
                                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                    if (this.showDate) {
                                        this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(20.0f) + h4, getMeasuredWidth() - AndroidUtilities.dp(12.0f), AndroidUtilities.dp(28.0f) + h4);
                                        checkRtl(this.rectF);
                                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                                    }
                                    h4 += getCellHeight(getMeasuredWidth());
                                    k14++;
                                    if (this.isSingleCell && k14 >= this.itemsCount) {
                                        break;
                                    }
                                }
                            }
                        } else {
                            h = h4;
                        }
                    } else {
                        h = h4;
                    }
                    int k15 = 0;
                    int h10 = h;
                    while (h10 <= getMeasuredHeight()) {
                        int r11 = AndroidUtilities.dp(23.0f);
                        canvas.drawCircle(checkRtl(this.paddingLeft + AndroidUtilities.dp(9.0f) + r11), (AndroidUtilities.dp(64.0f) >> 1) + h10, r11, paint);
                        this.rectF.set(this.paddingLeft + AndroidUtilities.dp(f7), AndroidUtilities.dp(17.0f) + h10, this.paddingLeft + AndroidUtilities.dp(f5), AndroidUtilities.dp(25.0f) + h10);
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        this.rectF.set(this.paddingLeft + AndroidUtilities.dp(f7), AndroidUtilities.dp(39.0f) + h10, this.paddingLeft + AndroidUtilities.dp(140.0f), h10 + AndroidUtilities.dp(47.0f));
                        checkRtl(this.rectF);
                        canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        if (this.showDate) {
                            this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(20.0f) + h10, getMeasuredWidth() - AndroidUtilities.dp(12.0f), h10 + AndroidUtilities.dp(28.0f));
                            checkRtl(this.rectF);
                            canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                        }
                        h10 += getCellHeight(getMeasuredWidth());
                        k15++;
                        if (this.isSingleCell && k15 >= this.itemsCount) {
                            break;
                        }
                        f5 = 260.0f;
                        f7 = 68.0f;
                    }
                }
            }
        } else {
            int k16 = 0;
            while (h4 <= getMeasuredHeight()) {
                int childH2 = getCellHeight(getMeasuredWidth());
                int r12 = AndroidUtilities.dp(f2);
                canvas.drawCircle(checkRtl(AndroidUtilities.dp(10.0f) + r12), h4 + (childH2 >> 1), r12, paint);
                this.rectF.set(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(f) + h4, AndroidUtilities.dp(148.0f), h4 + AndroidUtilities.dp(f3));
                checkRtl(this.rectF);
                canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                this.rectF.set(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(38.0f) + h4, AndroidUtilities.dp(268.0f), AndroidUtilities.dp(46.0f) + h4);
                checkRtl(this.rectF);
                canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                if (SharedConfig.useThreeLinesLayout) {
                    this.rectF.set(AndroidUtilities.dp(76.0f), AndroidUtilities.dp(54.0f) + h4, AndroidUtilities.dp(220.0f), AndroidUtilities.dp(62.0f) + h4);
                    checkRtl(this.rectF);
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                }
                if (this.showDate) {
                    this.rectF.set(getMeasuredWidth() - AndroidUtilities.dp(50.0f), AndroidUtilities.dp(16.0f) + h4, getMeasuredWidth() - AndroidUtilities.dp(12.0f), h4 + AndroidUtilities.dp(24.0f));
                    checkRtl(this.rectF);
                    canvas.drawRoundRect(this.rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
                }
                h4 += getCellHeight(getMeasuredWidth());
                k16++;
                if (this.isSingleCell && k16 >= this.itemsCount) {
                    break;
                }
                f = 16.0f;
                f2 = 28.0f;
                f3 = 24.0f;
            }
        }
        invalidate();
    }

    public void updateGradient() {
        FlickerLoadingView flickerLoadingView = this.globalGradientView;
        if (flickerLoadingView != null) {
            flickerLoadingView.updateGradient();
            return;
        }
        long newUpdateTime = SystemClock.elapsedRealtime();
        long dt = Math.abs(this.lastUpdateTime - newUpdateTime);
        if (dt > 17) {
            dt = 16;
        }
        if (dt < 4) {
            dt = 0;
        }
        int width = this.parentWidth;
        if (width == 0) {
            width = getMeasuredWidth();
        }
        int height = this.parentHeight;
        if (height == 0) {
            height = getMeasuredHeight();
        }
        this.lastUpdateTime = newUpdateTime;
        if (this.isSingleCell || this.viewType == 13 || getViewType() == 14 || getViewType() == 17) {
            int i = (int) (this.totalTranslation + (((float) (width * dt)) / 400.0f));
            this.totalTranslation = i;
            if (i >= width * 2) {
                this.totalTranslation = (-this.gradientWidth) * 2;
            }
            this.matrix.setTranslate(this.totalTranslation + this.parentXOffset, 0.0f);
        } else {
            int i2 = (int) (this.totalTranslation + (((float) (height * dt)) / 400.0f));
            this.totalTranslation = i2;
            if (i2 >= height * 2) {
                this.totalTranslation = (-this.gradientWidth) * 2;
            }
            this.matrix.setTranslate(this.parentXOffset, this.totalTranslation);
        }
        LinearGradient linearGradient = this.gradient;
        if (linearGradient != null) {
            linearGradient.setLocalMatrix(this.matrix);
        }
    }

    public void updateColors() {
        int i;
        FlickerLoadingView flickerLoadingView = this.globalGradientView;
        if (flickerLoadingView == null) {
            int color0 = getThemedColor(this.colorKey1);
            int color1 = getThemedColor(this.colorKey2);
            if (this.color1 != color1 || this.color0 != color0) {
                this.color0 = color0;
                this.color1 = color1;
                if (this.isSingleCell || (i = this.viewType) == 13 || i == 14 || i == 17) {
                    int dp = AndroidUtilities.dp(200.0f);
                    this.gradientWidth = dp;
                    this.gradient = new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{color1, color0, color0, color1}, new float[]{0.0f, 0.4f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
                } else {
                    int dp2 = AndroidUtilities.dp(600.0f);
                    this.gradientWidth = dp2;
                    this.gradient = new LinearGradient(0.0f, 0.0f, 0.0f, dp2, new int[]{color1, color0, color0, color1}, new float[]{0.0f, 0.4f, 0.6f, 1.0f}, Shader.TileMode.CLAMP);
                }
                this.paint.setShader(this.gradient);
                return;
            }
            return;
        }
        flickerLoadingView.updateColors();
    }

    private float checkRtl(float x) {
        if (LocaleController.isRTL) {
            return getMeasuredWidth() - x;
        }
        return x;
    }

    private void checkRtl(RectF rectF) {
        if (LocaleController.isRTL) {
            rectF.left = getMeasuredWidth() - rectF.left;
            rectF.right = getMeasuredWidth() - rectF.right;
        }
    }

    private int getCellHeight(int width) {
        switch (getViewType()) {
            case 1:
                return AndroidUtilities.dp(78.0f) + 1;
            case 2:
                int photoWidth = (width - (AndroidUtilities.dp(2.0f) * (getColumnsCount() - 1))) / getColumnsCount();
                return AndroidUtilities.dp(2.0f) + photoWidth;
            case 3:
            case 4:
                return AndroidUtilities.dp(56.0f);
            case 5:
                return AndroidUtilities.dp(80.0f);
            case 6:
            case 18:
                return AndroidUtilities.dp(64.0f);
            case 7:
                return AndroidUtilities.dp((SharedConfig.useThreeLinesLayout ? 78 : 72) + 1);
            case 8:
                return AndroidUtilities.dp(61.0f);
            case 9:
                return AndroidUtilities.dp(66.0f);
            case 10:
                return AndroidUtilities.dp(58.0f);
            case 11:
                return AndroidUtilities.dp(36.0f);
            case 12:
                return AndroidUtilities.dp(103.0f);
            case 13:
            case 14:
            case 17:
            case 20:
            default:
                return 0;
            case 15:
                return AndroidUtilities.dp(107.0f);
            case 16:
                return AndroidUtilities.dp(48.0f);
            case 19:
                return AndroidUtilities.dp(58.0f);
            case 21:
                return AndroidUtilities.dp(58.0f);
            case 22:
                return AndroidUtilities.dp(60.0f);
        }
    }

    public void showDate(boolean showDate) {
        this.showDate = showDate;
    }

    public void setUseHeaderOffset(boolean useHeaderOffset) {
        this.useHeaderOffset = useHeaderOffset;
    }

    public void skipDrawItemsCount(int i) {
        this.skipDrawItemsCount = i;
    }

    public void setPaddingTop(int t) {
        this.paddingTop = t;
        invalidate();
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        invalidate();
    }

    public void setItemsCount(int i) {
        this.itemsCount = i;
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }

    public void setGlobalGradientView(FlickerLoadingView globalGradientView) {
        this.globalGradientView = globalGradientView;
    }

    public void setParentSize(int parentWidth, int parentHeight, float parentXOffset) {
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        this.parentXOffset = parentXOffset;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setIgnoreHeightCheck(boolean ignore) {
        this.ignoreHeightCheck = ignore;
    }
}
