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
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes4.dex */
public class Emoji {
    private static final int MAX_RECENT_EMOJI_COUNT = 48;
    private static int bigImgSize;
    public static float emojiDrawingYOffset;
    private static Paint placeholderPaint;
    private static boolean recentEmojiLoaded;
    private static HashMap<CharSequence, DrawableInfo> rects = new HashMap<>();
    private static boolean inited = false;
    private static int[] emojiCounts = {1906, 199, 123, 332, 128, 222, 292, 259};
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
        int a = 0;
        while (true) {
            Bitmap[][] bitmapArr = emojiBmp;
            if (a >= bitmapArr.length) {
                break;
            }
            int[] iArr = emojiCounts;
            bitmapArr[a] = new Bitmap[iArr[a]];
            loadingEmoji[a] = new boolean[iArr[a]];
            a++;
        }
        for (int j = 0; j < EmojiData.data.length; j++) {
            for (int i = 0; i < EmojiData.data[j].length; i++) {
                rects.put(EmojiData.data[j][i], new DrawableInfo((byte) j, (short) i, i));
            }
        }
        Paint paint = new Paint();
        placeholderPaint = paint;
        paint.setColor(0);
    }

    public static void preloadEmoji(CharSequence code) {
        DrawableInfo info = getDrawableInfo(code);
        if (info != null) {
            loadEmoji(info.page, info.page2);
        }
    }

    public static void loadEmoji(final byte page, final short page2) {
        if (emojiBmp[page][page2] == null) {
            boolean[][] zArr = loadingEmoji;
            if (zArr[page][page2]) {
                return;
            }
            zArr[page][page2] = true;
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.messenger.Emoji$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Emoji.lambda$loadEmoji$1(page, page2);
                }
            });
        }
    }

    public static /* synthetic */ void lambda$loadEmoji$1(byte page, short page2) {
        loadEmojiInternal(page, page2);
        loadingEmoji[page][page2] = false;
    }

    private static void loadEmojiInternal(byte page, short page2) {
        int imageResize;
        try {
            if (AndroidUtilities.density <= 1.0f) {
                imageResize = 2;
            } else {
                imageResize = 1;
            }
            AssetManager assets = ApplicationLoader.applicationContext.getAssets();
            InputStream is = assets.open("emoji/" + String.format(Locale.US, "%d_%d.png", Byte.valueOf(page), Short.valueOf(page2)));
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = false;
            opts.inSampleSize = imageResize;
            Bitmap bitmap = BitmapFactory.decodeStream(is, null, opts);
            is.close();
            emojiBmp[page][page2] = bitmap;
            AndroidUtilities.cancelRunOnUIThread(invalidateUiRunnable);
            AndroidUtilities.runOnUIThread(invalidateUiRunnable);
        } catch (Throwable x) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.e("Error loading emoji", x);
            }
        }
    }

    public static void invalidateAll(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) view;
            for (int i = 0; i < g.getChildCount(); i++) {
                invalidateAll(g.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            view.invalidate();
        }
    }

    public static String fixEmoji(String emoji) {
        int length = emoji.length();
        int a = 0;
        while (a < length) {
            char ch = emoji.charAt(a);
            if (ch >= 55356 && ch <= 55358) {
                if (ch == 55356 && a < length - 1) {
                    char ch2 = emoji.charAt(a + 1);
                    if (ch2 == 56879 || ch2 == 56324 || ch2 == 56858 || ch2 == 56703) {
                        emoji = emoji.substring(0, a + 2) + "️" + emoji.substring(a + 2);
                        length++;
                        a += 2;
                    } else {
                        a++;
                    }
                } else {
                    a++;
                }
            } else if (ch == 8419) {
                return emoji;
            } else {
                if (ch >= 8252 && ch <= 12953 && EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(ch))) {
                    emoji = emoji.substring(0, a + 1) + "️" + emoji.substring(a + 1);
                    length++;
                    a++;
                }
            }
            a++;
        }
        return emoji;
    }

    public static EmojiDrawable getEmojiDrawable(CharSequence code) {
        DrawableInfo info = getDrawableInfo(code);
        if (info == null) {
            return null;
        }
        EmojiDrawable ed = new EmojiDrawable(info);
        int i = drawImgSize;
        ed.setBounds(0, 0, i, i);
        return ed;
    }

    private static DrawableInfo getDrawableInfo(CharSequence code) {
        CharSequence newCode;
        DrawableInfo info = rects.get(code);
        if (info == null && (newCode = EmojiData.emojiAliasMap.get(code)) != null) {
            return rects.get(newCode);
        }
        return info;
    }

    public static boolean isValidEmoji(CharSequence code) {
        CharSequence newCode;
        if (TextUtils.isEmpty(code)) {
            return false;
        }
        DrawableInfo info = rects.get(code);
        if (info == null && (newCode = EmojiData.emojiAliasMap.get(code)) != null) {
            info = rects.get(newCode);
        }
        return info != null;
    }

    public static Drawable getEmojiBigDrawable(String code) {
        CharSequence newCode;
        EmojiDrawable ed = getEmojiDrawable(code);
        if (ed == null && (newCode = EmojiData.emojiAliasMap.get(code)) != null) {
            ed = getEmojiDrawable(newCode);
        }
        if (ed == null) {
            return null;
        }
        int i = bigImgSize;
        ed.setBounds(0, 0, i, i);
        ed.fullSize = true;
        return ed;
    }

    /* loaded from: classes4.dex */
    public static class EmojiDrawable extends Drawable {
        private static Paint paint = new Paint(2);
        private static Rect rect = new Rect();
        private DrawableInfo info;
        private boolean fullSize = false;
        public int placeholderColor = 536870912;

        public EmojiDrawable(DrawableInfo i) {
            this.info = i;
        }

        public DrawableInfo getDrawableInfo() {
            return this.info;
        }

        public Rect getDrawRect() {
            Rect original = getBounds();
            int cX = original.centerX();
            int cY = original.centerY();
            rect.left = cX - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.right = ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2) + cX;
            rect.top = cY - ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2);
            rect.bottom = ((this.fullSize ? Emoji.bigImgSize : Emoji.drawImgSize) / 2) + cY;
            return rect;
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            Rect b;
            if (!isLoaded()) {
                Emoji.loadEmoji(this.info.page, this.info.page2);
                Emoji.placeholderPaint.setColor(this.placeholderColor);
                Rect bounds = getBounds();
                canvas.drawCircle(bounds.centerX(), bounds.centerY(), bounds.width() * 0.4f, Emoji.placeholderPaint);
                return;
            }
            if (this.fullSize) {
                b = getDrawRect();
            } else {
                b = getBounds();
            }
            if (!canvas.quickReject(b.left, b.top, b.right, b.bottom, Canvas.EdgeType.AA)) {
                canvas.drawBitmap(Emoji.emojiBmp[this.info.page][this.info.page2], (Rect) null, b, paint);
            }
        }

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter cf) {
        }

        public boolean isLoaded() {
            return Emoji.emojiBmp[this.info.page][this.info.page2] != null;
        }

        public void preload() {
            if (!isLoaded()) {
                Emoji.loadEmoji(this.info.page, this.info.page2);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static class DrawableInfo {
        public int emojiIndex;
        public byte page;
        public short page2;

        public DrawableInfo(byte p, short p2, int index) {
            this.page = p;
            this.page2 = p2;
            this.emojiIndex = index;
        }
    }

    private static boolean inArray(char c, char[] a) {
        for (char cc : a) {
            if (cc == c) {
                return true;
            }
        }
        return false;
    }

    /* loaded from: classes4.dex */
    public static class EmojiSpanRange {
        CharSequence code;
        int end;
        int start;

        public EmojiSpanRange(int start, int end, CharSequence code) {
            this.start = start;
            this.end = end;
            this.code = code;
        }
    }

    public static boolean fullyConsistsOfEmojis(CharSequence cs) {
        int[] emojiOnly = new int[1];
        parseEmojis(cs, emojiOnly);
        return emojiOnly[0] > 0;
    }

    public static ArrayList<EmojiSpanRange> parseEmojis(CharSequence cs) {
        return parseEmojis(cs, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:188:0x0295  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.util.ArrayList<org.telegram.messenger.Emoji.EmojiSpanRange> parseEmojis(java.lang.CharSequence r25, int[] r26) {
        /*
            Method dump skipped, instructions count: 672
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Emoji.parseEmojis(java.lang.CharSequence, int[]):java.util.ArrayList");
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, int size, boolean createNew) {
        return replaceEmoji(cs, fontMetrics, size, createNew, null, false, null);
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, int size, boolean createNew, boolean allowAnimated, AtomicReference<WeakReference<View>> viewRef) {
        return replaceEmoji(cs, fontMetrics, size, createNew, null, allowAnimated, viewRef);
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, int size, boolean createNew, int[] emojiOnly) {
        return replaceEmoji(cs, fontMetrics, size, createNew, emojiOnly, false, null);
    }

    public static CharSequence replaceEmoji(CharSequence cs, Paint.FontMetricsInt fontMetrics, int size, boolean createNew, int[] emojiOnly, boolean allowAnimated, AtomicReference<WeakReference<View>> viewRef) {
        Spannable s;
        if (SharedConfig.useSystemEmoji || cs == null || cs.length() == 0) {
            return cs;
        }
        if (!createNew && (cs instanceof Spannable)) {
            s = (Spannable) cs;
        } else {
            s = Spannable.Factory.getInstance().newSpannable(cs.toString());
        }
        ArrayList<EmojiSpanRange> emojis = parseEmojis(s, emojiOnly);
        for (int i = 0; i < emojis.size(); i++) {
            EmojiSpanRange emojiRange = emojis.get(i);
            try {
                Drawable drawable = getEmojiDrawable(emojiRange.code);
                if (drawable != null) {
                    EmojiSpan span = new EmojiSpan(drawable, 0, size, fontMetrics);
                    s.setSpan(span, emojiRange.start, emojiRange.end, 33);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if ((Build.VERSION.SDK_INT < 23 || Build.VERSION.SDK_INT >= 29) && !BuildVars.DEBUG_PRIVATE_VERSION && i + 1 >= 50) {
                break;
            }
        }
        return s;
    }

    /* loaded from: classes4.dex */
    public static class EmojiSpan extends ImageSpan {
        private Paint.FontMetricsInt fontMetrics;
        private int size;

        public EmojiSpan(Drawable d, int verticalAlignment, int s, Paint.FontMetricsInt original) {
            super(d, verticalAlignment);
            this.size = AndroidUtilities.dp(20.0f);
            this.fontMetrics = original;
            if (original != null) {
                int abs = Math.abs(original.descent) + Math.abs(this.fontMetrics.ascent);
                this.size = abs;
                if (abs == 0) {
                    this.size = AndroidUtilities.dp(20.0f);
                }
            }
        }

        public void replaceFontMetrics(Paint.FontMetricsInt newMetrics, int newSize) {
            this.fontMetrics = newMetrics;
            this.size = newSize;
        }

        @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            if (fm == null) {
                fm = new Paint.FontMetricsInt();
            }
            Paint.FontMetricsInt fontMetricsInt = this.fontMetrics;
            if (fontMetricsInt == null) {
                int sz = super.getSize(paint, text, start, end, fm);
                int offset = AndroidUtilities.dp(8.0f);
                int w = AndroidUtilities.dp(10.0f);
                fm.top = (-w) - offset;
                fm.bottom = w - offset;
                fm.ascent = (-w) - offset;
                fm.leading = 0;
                fm.descent = w - offset;
                return sz;
            }
            if (fm != null) {
                fm.ascent = fontMetricsInt.ascent;
                fm.descent = this.fontMetrics.descent;
                fm.top = this.fontMetrics.top;
                fm.bottom = this.fontMetrics.bottom;
            }
            if (getDrawable() != null) {
                Drawable drawable = getDrawable();
                int i = this.size;
                drawable.setBounds(0, 0, i, i);
            }
            return this.size;
        }

        @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            boolean restoreAlpha = false;
            if (paint.getAlpha() != 255 && Emoji.emojiDrawingUseAlpha) {
                restoreAlpha = true;
                getDrawable().setAlpha(paint.getAlpha());
            }
            boolean needRestore = false;
            if (Emoji.emojiDrawingYOffset != 0.0f) {
                needRestore = true;
                canvas.save();
                canvas.translate(0.0f, Emoji.emojiDrawingYOffset);
            }
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
            if (needRestore) {
                canvas.restore();
            }
            if (restoreAlpha) {
                getDrawable().setAlpha(255);
            }
        }

        @Override // android.text.style.ReplacementSpan, android.text.style.CharacterStyle
        public void updateDrawState(TextPaint ds) {
            if (getDrawable() instanceof EmojiDrawable) {
                ((EmojiDrawable) getDrawable()).placeholderColor = 553648127 & ds.getColor();
            }
            super.updateDrawState(ds);
        }
    }

    public static void addRecentEmoji(String code) {
        Integer count = emojiUseHistory.get(code);
        if (count == null) {
            count = 0;
        }
        if (count.intValue() == 0 && emojiUseHistory.size() >= 48) {
            ArrayList<String> arrayList = recentEmoji;
            String emoji = arrayList.get(arrayList.size() - 1);
            emojiUseHistory.remove(emoji);
            ArrayList<String> arrayList2 = recentEmoji;
            arrayList2.set(arrayList2.size() - 1, code);
        }
        emojiUseHistory.put(code, Integer.valueOf(count.intValue() + 1));
    }

    public static void sortEmoji() {
        recentEmoji.clear();
        for (Map.Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            recentEmoji.add(entry.getKey());
        }
        Collections.sort(recentEmoji, Emoji$$ExternalSyntheticLambda2.INSTANCE);
        while (recentEmoji.size() > 48) {
            ArrayList<String> arrayList = recentEmoji;
            arrayList.remove(arrayList.size() - 1);
        }
    }

    public static /* synthetic */ int lambda$sortEmoji$2(String lhs, String rhs) {
        Integer count1 = emojiUseHistory.get(lhs);
        Integer count2 = emojiUseHistory.get(rhs);
        if (count1 == null) {
            count1 = 0;
        }
        if (count2 == null) {
            count2 = 0;
        }
        if (count1.intValue() > count2.intValue()) {
            return -1;
        }
        if (count1.intValue() >= count2.intValue()) {
            return 0;
        }
        return 1;
    }

    public static void saveRecentEmoji() {
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : emojiUseHistory.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString("emojis2", stringBuilder.toString()).commit();
    }

    public static void clearRecentEmoji() {
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        preferences.edit().putBoolean("filled_default", true).commit();
        emojiUseHistory.clear();
        recentEmoji.clear();
        saveRecentEmoji();
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:57:0x01f2
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:92)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:44)
        */
    public static void loadRecentEmoji() {
        /*
            Method dump skipped, instructions count: 556
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.Emoji.loadRecentEmoji():void");
    }

    public static void saveEmojiColors() {
        SharedPreferences preferences = MessagesController.getGlobalEmojiSettings();
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : emojiColor.entrySet()) {
            if (stringBuilder.length() != 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append("=");
            stringBuilder.append(entry.getValue());
        }
        preferences.edit().putString(TtmlNode.ATTR_TTS_COLOR, stringBuilder.toString()).commit();
    }
}
