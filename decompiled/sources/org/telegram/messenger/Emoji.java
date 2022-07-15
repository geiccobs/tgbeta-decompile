package org.telegram.messenger;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.Components.AnimatedEmojiSpan;
/* loaded from: classes.dex */
public class Emoji {
    private static final int MAX_RECENT_EMOJI_COUNT = 48;
    private static int bigImgSize;
    public static float emojiDrawingYOffset;
    private static Paint placeholderPaint;
    private static boolean recentEmojiLoaded;
    private static HashMap<CharSequence, DrawableInfo> rects = new HashMap<>();
    private static boolean inited = false;
    private static int[] emojiCounts = {1906, 199, 123, 332, ConnectionsManager.RequestFlagNeedQuickAck, 222, 292, 259};
    private static Bitmap[][] emojiBmp = new Bitmap[8];
    private static boolean[][] loadingEmoji = new boolean[8];
    public static HashMap<String, Integer> emojiUseHistory = new HashMap<>();
    public static ArrayList<String> recentEmoji = new ArrayList<>();
    public static HashMap<String, String> emojiColor = new HashMap<>();
    private static Runnable invalidateUiRunnable = Emoji$$ExternalSyntheticLambda1.INSTANCE;
    public static boolean emojiDrawingUseAlpha = true;
    private static int drawImgSize = AndroidUtilities.dp(20.0f);

    static {
        bigImgSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 34.0f);
        int i = 0;
        while (true) {
            Bitmap[][] bitmapArr = emojiBmp;
            if (i >= bitmapArr.length) {
                break;
            }
            int[] iArr = emojiCounts;
            bitmapArr[i] = new Bitmap[iArr[i]];
            loadingEmoji[i] = new boolean[iArr[i]];
            i++;
        }
        for (int i2 = 0; i2 < EmojiData.data.length; i2++) {
            int i3 = 0;
            while (true) {
                String[][] strArr = EmojiData.data;
                if (i3 < strArr[i2].length) {
                    rects.put(strArr[i2][i3], new DrawableInfo((byte) i2, (short) i3, i3));
                    i3++;
                }
            }
        }
        Paint paint = new Paint();
        placeholderPaint = paint;
        paint.setColor(0);
    }

    public static /* synthetic */ void lambda$static$0() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.emojiLoaded, new Object[0]);
    }

    public static void preloadEmoji(CharSequence charSequence) {
        DrawableInfo drawableInfo = getDrawableInfo(charSequence);
        if (drawableInfo != null) {
            loadEmoji(drawableInfo.page, drawableInfo.page2);
        }
    }

    public static void loadEmoji(final byte b, final short s) {
        if (emojiBmp[b][s] == null) {
            boolean[][] zArr = loadingEmoji;
            if (zArr[b][s]) {
                return;
            }
            zArr[b][s] = true;
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.Emoji$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Emoji.lambda$loadEmoji$1(b, s);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$loadEmoji$1(byte b, short s) {
        loadEmojiInternal(b, s);
        loadingEmoji[b][s] = false;
    }

    private static void loadEmojiInternal(byte b, short s) {
        try {
            int i = AndroidUtilities.density <= 1.0f ? 2 : 1;
            AssetManager assets = ApplicationLoader.applicationContext.getAssets();
            InputStream open = assets.open("emoji/" + String.format(Locale.US, "%d_%d.png", Byte.valueOf(b), Short.valueOf(s)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = i;
            Bitmap decodeStream = BitmapFactory.decodeStream(open, null, options);
            open.close();
            emojiBmp[b][s] = decodeStream;
            AndroidUtilities.cancelRunOnUIThread(invalidateUiRunnable);
            AndroidUtilities.runOnUIThread(invalidateUiRunnable);
        } catch (Throwable th) {
            if (!BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("Error loading emoji", th);
        }
    }

    public static void invalidateAll(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                invalidateAll(viewGroup.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            view.invalidate();
        }
    }

    public static String fixEmoji(String str) {
        int length = str.length();
        int i = 0;
        while (i < length) {
            char charAt = str.charAt(i);
            if (charAt >= 55356 && charAt <= 55358) {
                if (charAt != 55356 || i >= length - 1) {
                    i++;
                } else {
                    int i2 = i + 1;
                    char charAt2 = str.charAt(i2);
                    if (charAt2 == 56879 || charAt2 == 56324 || charAt2 == 56858 || charAt2 == 56703) {
                        StringBuilder sb = new StringBuilder();
                        i += 2;
                        sb.append(str.substring(0, i));
                        sb.append("️");
                        sb.append(str.substring(i));
                        str = sb.toString();
                        length++;
                    } else {
                        i = i2;
                    }
                }
                i++;
            } else if (charAt == 8419) {
                return str;
            } else {
                if (charAt >= 8252 && charAt <= 12953 && EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(charAt))) {
                    StringBuilder sb2 = new StringBuilder();
                    i++;
                    sb2.append(str.substring(0, i));
                    sb2.append("️");
                    sb2.append(str.substring(i));
                    str = sb2.toString();
                    length++;
                }
                i++;
            }
        }
        return str;
    }

    public static EmojiDrawable getEmojiDrawable(CharSequence charSequence) {
        DrawableInfo drawableInfo = getDrawableInfo(charSequence);
        if (drawableInfo == null) {
            return null;
        }
        EmojiDrawable emojiDrawable = new EmojiDrawable(drawableInfo);
        int i = drawImgSize;
        emojiDrawable.setBounds(0, 0, i, i);
        return emojiDrawable;
    }

    private static DrawableInfo getDrawableInfo(CharSequence charSequence) {
        CharSequence charSequence2;
        DrawableInfo drawableInfo = rects.get(charSequence);
        return (drawableInfo != null || (charSequence2 = EmojiData.emojiAliasMap.get(charSequence)) == null) ? drawableInfo : rects.get(charSequence2);
    }

    public static boolean isValidEmoji(CharSequence charSequence) {
        CharSequence charSequence2;
        if (TextUtils.isEmpty(charSequence)) {
            return false;
        }
        DrawableInfo drawableInfo = rects.get(charSequence);
        if (drawableInfo == null && (charSequence2 = EmojiData.emojiAliasMap.get(charSequence)) != null) {
            drawableInfo = rects.get(charSequence2);
        }
        return drawableInfo != null;
    }

    public static Drawable getEmojiBigDrawable(String str) {
        CharSequence charSequence;
        EmojiDrawable emojiDrawable = getEmojiDrawable(str);
        if (emojiDrawable == null && (charSequence = EmojiData.emojiAliasMap.get(str)) != null) {
            emojiDrawable = getEmojiDrawable(charSequence);
        }
        if (emojiDrawable == null) {
            return null;
        }
        int i = bigImgSize;
        emojiDrawable.setBounds(0, 0, i, i);
        emojiDrawable.fullSize = true;
        return emojiDrawable;
    }

    /* loaded from: classes.dex */
    public static class EmojiDrawable extends Drawable {
        private static Paint paint = new Paint(2);
        private static Rect rect = new Rect();
        private DrawableInfo info;
        private boolean fullSize = false;
        public int placeholderColor = 536870912;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public EmojiDrawable(DrawableInfo drawableInfo) {
            this.info = drawableInfo;
        }

        public DrawableInfo getDrawableInfo() {
            return this.info;
        }

        public Rect getDrawRect() {
            Rect bounds = getBounds();
            int centerX = bounds.centerX();
            int centerY = bounds.centerY();
            rect.left = centerX - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.right = centerX + ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.top = centerY - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.bottom = centerY + ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            return rect;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            Rect rect2;
            if (!isLoaded()) {
                DrawableInfo drawableInfo = this.info;
                Emoji.loadEmoji(drawableInfo.page, drawableInfo.page2);
                Emoji.placeholderPaint.setColor(this.placeholderColor);
                Rect bounds = getBounds();
                canvas.drawCircle(bounds.centerX(), bounds.centerY(), bounds.width() * 0.4f, Emoji.placeholderPaint);
                return;
            }
            if (this.fullSize) {
                rect2 = getDrawRect();
            } else {
                rect2 = getBounds();
            }
            if (canvas.quickReject(rect2.left, rect2.top, rect2.right, rect2.bottom, Canvas.EdgeType.AA)) {
                return;
            }
            Bitmap[][] bitmapArr = Emoji.emojiBmp;
            DrawableInfo drawableInfo2 = this.info;
            canvas.drawBitmap(bitmapArr[drawableInfo2.page][drawableInfo2.page2], (Rect) null, rect2, paint);
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
            paint.setAlpha(i);
        }

        public boolean isLoaded() {
            Bitmap[][] bitmapArr = Emoji.emojiBmp;
            DrawableInfo drawableInfo = this.info;
            return bitmapArr[drawableInfo.page][drawableInfo.page2] != null;
        }

        public void preload() {
            if (!isLoaded()) {
                DrawableInfo drawableInfo = this.info;
                Emoji.loadEmoji(drawableInfo.page, drawableInfo.page2);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class DrawableInfo {
        public int emojiIndex;
        public byte page;
        public short page2;

        public DrawableInfo(byte b, short s, int i) {
            this.page = b;
            this.page2 = s;
            this.emojiIndex = i;
        }
    }

    private static boolean inArray(char c, char[] cArr) {
        for (char c2 : cArr) {
            if (c2 == c) {
                return true;
            }
        }
        return false;
    }

    /* loaded from: classes.dex */
    public static class EmojiSpanRange {
        CharSequence code;
        int end;
        int start;

        public EmojiSpanRange(int i, int i2, CharSequence charSequence) {
            this.start = i;
            this.end = i2;
            this.code = charSequence;
        }
    }

    public static boolean fullyConsistsOfEmojis(CharSequence charSequence) {
        int[] iArr = new int[1];
        parseEmojis(charSequence, iArr);
        return iArr[0] > 0;
    }

    public static ArrayList<EmojiSpanRange> parseEmojis(CharSequence charSequence) {
        return parseEmojis(charSequence, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:103:0x0171  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x017a A[Catch: Exception -> 0x0244, TryCatch #0 {Exception -> 0x0244, blocks: (B:8:0x0029, B:16:0x003f, B:18:0x004a, B:27:0x0064, B:29:0x0073, B:37:0x0087, B:38:0x008a, B:42:0x0096, B:44:0x009f, B:48:0x00a9, B:56:0x00bd, B:67:0x00de, B:72:0x00ee, B:74:0x00f6, B:79:0x0108, B:81:0x010e, B:83:0x0119, B:87:0x0127, B:88:0x0134, B:90:0x013b, B:92:0x0145, B:96:0x0154, B:98:0x0165, B:102:0x016e, B:108:0x017a, B:110:0x017e, B:114:0x018b, B:116:0x0191, B:130:0x01bd, B:132:0x01c1, B:141:0x01d7, B:145:0x01e2, B:147:0x01e9, B:149:0x01ed, B:151:0x01f8, B:155:0x0206, B:158:0x0216, B:159:0x021d), top: B:170:0x0029 }] */
    /* JADX WARN: Removed duplicated region for block: B:144:0x01e0 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:147:0x01e9 A[Catch: Exception -> 0x0244, TryCatch #0 {Exception -> 0x0244, blocks: (B:8:0x0029, B:16:0x003f, B:18:0x004a, B:27:0x0064, B:29:0x0073, B:37:0x0087, B:38:0x008a, B:42:0x0096, B:44:0x009f, B:48:0x00a9, B:56:0x00bd, B:67:0x00de, B:72:0x00ee, B:74:0x00f6, B:79:0x0108, B:81:0x010e, B:83:0x0119, B:87:0x0127, B:88:0x0134, B:90:0x013b, B:92:0x0145, B:96:0x0154, B:98:0x0165, B:102:0x016e, B:108:0x017a, B:110:0x017e, B:114:0x018b, B:116:0x0191, B:130:0x01bd, B:132:0x01c1, B:141:0x01d7, B:145:0x01e2, B:147:0x01e9, B:149:0x01ed, B:151:0x01f8, B:155:0x0206, B:158:0x0216, B:159:0x021d), top: B:170:0x0029 }] */
    /* JADX WARN: Removed duplicated region for block: B:157:0x0214  */
    /* JADX WARN: Removed duplicated region for block: B:161:0x0238  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x0063  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x00f6 A[Catch: Exception -> 0x0244, TryCatch #0 {Exception -> 0x0244, blocks: (B:8:0x0029, B:16:0x003f, B:18:0x004a, B:27:0x0064, B:29:0x0073, B:37:0x0087, B:38:0x008a, B:42:0x0096, B:44:0x009f, B:48:0x00a9, B:56:0x00bd, B:67:0x00de, B:72:0x00ee, B:74:0x00f6, B:79:0x0108, B:81:0x010e, B:83:0x0119, B:87:0x0127, B:88:0x0134, B:90:0x013b, B:92:0x0145, B:96:0x0154, B:98:0x0165, B:102:0x016e, B:108:0x017a, B:110:0x017e, B:114:0x018b, B:116:0x0191, B:130:0x01bd, B:132:0x01c1, B:141:0x01d7, B:145:0x01e2, B:147:0x01e9, B:149:0x01ed, B:151:0x01f8, B:155:0x0206, B:158:0x0216, B:159:0x021d), top: B:170:0x0029 }] */
    /* JADX WARN: Removed duplicated region for block: B:75:0x0100  */
    /* JADX WARN: Removed duplicated region for block: B:79:0x0108 A[Catch: Exception -> 0x0244, TryCatch #0 {Exception -> 0x0244, blocks: (B:8:0x0029, B:16:0x003f, B:18:0x004a, B:27:0x0064, B:29:0x0073, B:37:0x0087, B:38:0x008a, B:42:0x0096, B:44:0x009f, B:48:0x00a9, B:56:0x00bd, B:67:0x00de, B:72:0x00ee, B:74:0x00f6, B:79:0x0108, B:81:0x010e, B:83:0x0119, B:87:0x0127, B:88:0x0134, B:90:0x013b, B:92:0x0145, B:96:0x0154, B:98:0x0165, B:102:0x016e, B:108:0x017a, B:110:0x017e, B:114:0x018b, B:116:0x0191, B:130:0x01bd, B:132:0x01c1, B:141:0x01d7, B:145:0x01e2, B:147:0x01e9, B:149:0x01ed, B:151:0x01f8, B:155:0x0206, B:158:0x0216, B:159:0x021d), top: B:170:0x0029 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.ArrayList<org.telegram.messenger.Emoji.EmojiSpanRange> parseEmojis(java.lang.CharSequence r24, int[] r25) {
        /*
            Method dump skipped, instructions count: 596
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Emoji.parseEmojis(java.lang.CharSequence, int[]):java.util.ArrayList");
    }

    public static CharSequence replaceEmoji(CharSequence charSequence, Paint.FontMetricsInt fontMetricsInt, int i, boolean z) {
        return replaceEmoji(charSequence, fontMetricsInt, i, z, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v0, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r7v1, types: [java.lang.CharSequence] */
    /* JADX WARN: Type inference failed for: r7v2, types: [java.lang.CharSequence, android.text.Spannable] */
    /* JADX WARN: Type inference failed for: r7v6 */
    /* JADX WARN: Type inference failed for: r7v7 */
    public static CharSequence replaceEmoji(CharSequence charSequence, Paint.FontMetricsInt fontMetricsInt, int i, boolean z, int[] iArr) {
        EmojiSpanRange emojiSpanRange;
        boolean z2;
        if (!SharedConfig.useSystemEmoji && charSequence != 0 && charSequence.length() != 0) {
            if (!z && (charSequence instanceof Spannable)) {
                charSequence = (Spannable) charSequence;
            } else {
                charSequence = Spannable.Factory.getInstance().newSpannable(charSequence.toString());
            }
            ArrayList<EmojiSpanRange> parseEmojis = parseEmojis(charSequence, iArr);
            AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) charSequence.getSpans(0, charSequence.length(), AnimatedEmojiSpan.class);
            for (int i2 = 0; i2 < parseEmojis.size(); i2++) {
                try {
                    emojiSpanRange = parseEmojis.get(i2);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (animatedEmojiSpanArr != null) {
                    int i3 = 0;
                    while (true) {
                        if (i3 >= animatedEmojiSpanArr.length) {
                            z2 = false;
                            break;
                        }
                        AnimatedEmojiSpan animatedEmojiSpan = animatedEmojiSpanArr[i3];
                        if (animatedEmojiSpan != null && charSequence.getSpanStart(animatedEmojiSpan) == emojiSpanRange.start && charSequence.getSpanEnd(animatedEmojiSpan) == emojiSpanRange.end) {
                            z2 = true;
                            break;
                        }
                        i3++;
                    }
                    if (z2) {
                    }
                }
                EmojiDrawable emojiDrawable = getEmojiDrawable(emojiSpanRange.code);
                if (emojiDrawable != null) {
                    charSequence.setSpan(new EmojiSpan(emojiDrawable, 0, i, fontMetricsInt), emojiSpanRange.start, emojiSpanRange.end, 33);
                }
                int i4 = Build.VERSION.SDK_INT;
                if ((i4 < 23 || i4 >= 29) && i2 + 1 >= 50) {
                    break;
                }
            }
        }
        return charSequence;
    }

    /* loaded from: classes.dex */
    public static class EmojiSpan extends ImageSpan {
        private Paint.FontMetricsInt fontMetrics;
        private int size;

        public EmojiSpan(Drawable drawable, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            super(drawable, i);
            this.size = AndroidUtilities.dp(20.0f);
            this.fontMetrics = fontMetricsInt;
            if (fontMetricsInt != null) {
                int abs = Math.abs(fontMetricsInt.descent) + Math.abs(this.fontMetrics.ascent);
                this.size = abs;
                if (abs != 0) {
                    return;
                }
                this.size = AndroidUtilities.dp(20.0f);
            }
        }

        public void replaceFontMetrics(Paint.FontMetricsInt fontMetricsInt, int i) {
            this.fontMetrics = fontMetricsInt;
            this.size = i;
        }

        @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            if (fontMetricsInt == null) {
                fontMetricsInt = new Paint.FontMetricsInt();
            }
            Paint.FontMetricsInt fontMetricsInt2 = this.fontMetrics;
            if (fontMetricsInt2 == null) {
                int size = super.getSize(paint, charSequence, i, i2, fontMetricsInt);
                int dp = AndroidUtilities.dp(8.0f);
                int dp2 = AndroidUtilities.dp(10.0f);
                int i3 = (-dp2) - dp;
                fontMetricsInt.top = i3;
                int i4 = dp2 - dp;
                fontMetricsInt.bottom = i4;
                fontMetricsInt.ascent = i3;
                fontMetricsInt.leading = 0;
                fontMetricsInt.descent = i4;
                return size;
            }
            fontMetricsInt.ascent = fontMetricsInt2.ascent;
            fontMetricsInt.descent = fontMetricsInt2.descent;
            fontMetricsInt.top = fontMetricsInt2.top;
            fontMetricsInt.bottom = fontMetricsInt2.bottom;
            if (getDrawable() != null) {
                Drawable drawable = getDrawable();
                int i5 = this.size;
                drawable.setBounds(0, 0, i5, i5);
            }
            return this.size;
        }

        @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            boolean z;
            boolean z2 = true;
            if (paint.getAlpha() == 255 || !Emoji.emojiDrawingUseAlpha) {
                z = false;
            } else {
                getDrawable().setAlpha(paint.getAlpha());
                z = true;
            }
            if (Emoji.emojiDrawingYOffset != 0.0f) {
                canvas.save();
                canvas.translate(0.0f, Emoji.emojiDrawingYOffset);
            } else {
                z2 = false;
            }
            super.draw(canvas, charSequence, i, i2, f, i3, i4, i5, paint);
            if (z2) {
                canvas.restore();
            }
            if (z) {
                getDrawable().setAlpha(255);
            }
        }

        @Override // android.text.style.ReplacementSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint textPaint) {
            if (getDrawable() instanceof EmojiDrawable) {
                ((EmojiDrawable) getDrawable()).placeholderColor = 553648127 & textPaint.getColor();
            }
            super.updateDrawState(textPaint);
        }
    }

    public static void addRecentEmoji(String str) {
        Integer num = emojiUseHistory.get(str);
        if (num == null) {
            num = 0;
        }
        if (num.intValue() == 0 && emojiUseHistory.size() >= MAX_RECENT_EMOJI_COUNT) {
            ArrayList<String> arrayList = recentEmoji;
            emojiUseHistory.remove(arrayList.get(arrayList.size() - 1));
            ArrayList<String> arrayList2 = recentEmoji;
            arrayList2.set(arrayList2.size() - 1, str);
        }
        emojiUseHistory.put(str, Integer.valueOf(num.intValue() + 1));
    }

    public static void sortEmoji() {
        recentEmoji.clear();
        for (Map.Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            recentEmoji.add(entry.getKey());
        }
        Collections.sort(recentEmoji, Emoji$$ExternalSyntheticLambda2.INSTANCE);
        while (recentEmoji.size() > MAX_RECENT_EMOJI_COUNT) {
            ArrayList<String> arrayList = recentEmoji;
            arrayList.remove(arrayList.size() - 1);
        }
    }

    public static /* synthetic */ int lambda$sortEmoji$2(String str, String str2) {
        Integer num = emojiUseHistory.get(str);
        Integer num2 = emojiUseHistory.get(str2);
        if (num == null) {
            num = 0;
        }
        if (num2 == null) {
            num2 = 0;
        }
        if (num.intValue() > num2.intValue()) {
            return -1;
        }
        return num.intValue() < num2.intValue() ? 1 : 0;
    }

    public static void saveRecentEmoji() {
        SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            if (sb.length() != 0) {
                sb.append(",");
            }
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }
        globalEmojiSettings.edit().putString("emojis2", sb.toString()).commit();
    }

    public static void clearRecentEmoji() {
        MessagesController.getGlobalEmojiSettings().edit().putBoolean("filled_default", true).commit();
        emojiUseHistory.clear();
        recentEmoji.clear();
        saveRecentEmoji();
    }

    public static void loadRecentEmoji() {
        if (recentEmojiLoaded) {
            return;
        }
        recentEmojiLoaded = true;
        SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
        try {
            emojiUseHistory.clear();
            int i = 4;
            if (globalEmojiSettings.contains("emojis")) {
                String string = globalEmojiSettings.getString("emojis", "");
                if (string != null && string.length() > 0) {
                    String[] split = string.split(",");
                    int length = split.length;
                    int i2 = 0;
                    while (i2 < length) {
                        String[] split2 = split[i2].split("=");
                        long longValue = Utilities.parseLong(split2[0]).longValue();
                        StringBuilder sb = new StringBuilder();
                        int i3 = 0;
                        while (i3 < i) {
                            sb.insert(0, (char) longValue);
                            longValue >>= 16;
                            if (longValue == 0) {
                                break;
                            }
                            i3++;
                            i = 4;
                        }
                        if (sb.length() > 0) {
                            emojiUseHistory.put(sb.toString(), Utilities.parseInt((CharSequence) split2[1]));
                        }
                        i2++;
                        i = 4;
                    }
                }
                globalEmojiSettings.edit().remove("emojis").commit();
                saveRecentEmoji();
            } else {
                String string2 = globalEmojiSettings.getString("emojis2", "");
                if (string2 != null && string2.length() > 0) {
                    for (String str : string2.split(",")) {
                        String[] split3 = str.split("=");
                        emojiUseHistory.put(split3[0], Utilities.parseInt((CharSequence) split3[1]));
                    }
                }
            }
            if (emojiUseHistory.isEmpty() && !globalEmojiSettings.getBoolean("filled_default", false)) {
                String[] strArr = {"😂", "😘", "❤", "😍", "😊", "😁", "👍", "☺", "😔", "😄", "😭", "💋", "😒", "😳", "😜", "🙈", "😉", "😃", "😢", "😝", "😱", "😡", "😏", "😞", "😅", "😚", "🙊", "😌", "😀", "😋", "😆", "👌", "😐", "😕"};
                for (int i4 = 0; i4 < 34; i4++) {
                    emojiUseHistory.put(strArr[i4], Integer.valueOf(34 - i4));
                }
                globalEmojiSettings.edit().putBoolean("filled_default", true).commit();
                saveRecentEmoji();
            }
            sortEmoji();
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            String string3 = globalEmojiSettings.getString("color", "");
            if (string3 == null || string3.length() <= 0) {
                return;
            }
            for (String str2 : string3.split(",")) {
                String[] split4 = str2.split("=");
                emojiColor.put(split4[0], split4[1]);
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public static void saveEmojiColors() {
        SharedPreferences globalEmojiSettings = MessagesController.getGlobalEmojiSettings();
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : emojiColor.entrySet()) {
            if (sb.length() != 0) {
                sb.append(",");
            }
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }
        globalEmojiSettings.edit().putString("color", sb.toString()).commit();
    }
}
