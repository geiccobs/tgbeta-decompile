package org.telegram.ui.Components;

import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import java.lang.reflect.Constructor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
/* loaded from: classes5.dex */
public class StaticLayoutEx {
    private static final String TEXT_DIRS_CLASS = "android.text.TextDirectionHeuristics";
    private static final String TEXT_DIR_CLASS = "android.text.TextDirectionHeuristic";
    private static final String TEXT_DIR_FIRSTSTRONG_LTR = "FIRSTSTRONG_LTR";
    public static Layout.Alignment[] alignments = Layout.Alignment.values();
    private static boolean initialized;
    private static Constructor<StaticLayout> sConstructor;
    private static Object[] sConstructorArgs;
    private static Object sTextDirection;

    public static Layout.Alignment ALIGN_RIGHT() {
        Layout.Alignment[] alignmentArr = alignments;
        return alignmentArr.length >= 5 ? alignmentArr[4] : Layout.Alignment.ALIGN_OPPOSITE;
    }

    public static Layout.Alignment ALIGN_LEFT() {
        Layout.Alignment[] alignmentArr = alignments;
        return alignmentArr.length >= 5 ? alignmentArr[3] : Layout.Alignment.ALIGN_NORMAL;
    }

    public static void init() {
        Class<?> textDirClass;
        if (initialized) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= 18) {
                textDirClass = TextDirectionHeuristic.class;
                sTextDirection = TextDirectionHeuristics.FIRSTSTRONG_LTR;
            } else {
                ClassLoader loader = StaticLayoutEx.class.getClassLoader();
                Class<?> textDirClass2 = loader.loadClass(TEXT_DIR_CLASS);
                Class<?> textDirsClass = loader.loadClass(TEXT_DIRS_CLASS);
                sTextDirection = textDirsClass.getField(TEXT_DIR_FIRSTSTRONG_LTR).get(textDirsClass);
                textDirClass = textDirClass2;
            }
            Class<?>[] signature = {CharSequence.class, Integer.TYPE, Integer.TYPE, TextPaint.class, Integer.TYPE, Layout.Alignment.class, textDirClass, Float.TYPE, Float.TYPE, Boolean.TYPE, TextUtils.TruncateAt.class, Integer.TYPE, Integer.TYPE};
            Constructor<StaticLayout> declaredConstructor = StaticLayout.class.getDeclaredConstructor(signature);
            sConstructor = declaredConstructor;
            declaredConstructor.setAccessible(true);
            sConstructorArgs = new Object[signature.length];
            initialized = true;
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    public static StaticLayout createStaticLayout2(CharSequence source, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsisWidth, int maxLines) {
        if (Build.VERSION.SDK_INT >= 23) {
            StaticLayout.Builder builder = StaticLayout.Builder.obtain(source, 0, source.length(), paint, ellipsisWidth).setAlignment(align).setLineSpacing(spacingadd, spacingmult).setIncludePad(includepad).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(ellipsisWidth).setMaxLines(maxLines).setBreakStrategy(1).setHyphenationFrequency(0);
            return builder.build();
        }
        return createStaticLayout(source, 0, source.length(), paint, width, align, spacingmult, spacingadd, includepad, ellipsize, ellipsisWidth, maxLines, true);
    }

    public static StaticLayout createStaticLayout(CharSequence source, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsisWidth, int maxLines) {
        return createStaticLayout(source, 0, source.length(), paint, width, align, spacingmult, spacingadd, includepad, ellipsize, ellipsisWidth, maxLines, true);
    }

    public static StaticLayout createStaticLayout(CharSequence source, TextPaint paint, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsisWidth, int maxLines, boolean canContainUrl) {
        return createStaticLayout(source, 0, source.length(), paint, width, align, spacingmult, spacingadd, includepad, ellipsize, ellipsisWidth, maxLines, canContainUrl);
    }

    public static StaticLayout createStaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerWidth, Layout.Alignment align, float spacingMult, float spacingAdd, boolean includePad, TextUtils.TruncateAt ellipsize, int ellipsisWidth, int maxLines, boolean canContainUrl) {
        Exception e;
        int i;
        int i2;
        boolean z;
        StaticLayout layout;
        int off;
        int off2;
        int i3 = 1;
        try {
            if (maxLines == 1) {
                CharSequence text = TextUtils.ellipsize(source, paint, ellipsisWidth, TextUtils.TruncateAt.END);
                return new StaticLayout(text, 0, text.length(), paint, outerWidth, align, spacingMult, spacingAdd, includePad);
            }
            if (Build.VERSION.SDK_INT >= 23) {
                StaticLayout.Builder builder = StaticLayout.Builder.obtain(source, 0, source.length(), paint, outerWidth).setAlignment(align).setLineSpacing(spacingAdd, spacingMult).setIncludePad(includePad).setEllipsize(null).setEllipsizedWidth(ellipsisWidth).setMaxLines(maxLines).setBreakStrategy(1).setHyphenationFrequency(0);
                layout = builder.build();
                i2 = maxLines;
                i = ellipsisWidth;
                z = includePad;
            } else {
                i2 = maxLines;
                i = ellipsisWidth;
                z = includePad;
                try {
                    layout = new StaticLayout(source, paint, outerWidth, align, spacingMult, spacingAdd, includePad);
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e(e);
                    return null;
                }
            }
            if (layout.getLineCount() <= i2) {
                return layout;
            }
            float left = layout.getLineLeft(i2 - 1);
            float lineWidth = layout.getLineWidth(i2 - 1);
            if (left != 0.0f) {
                off = layout.getOffsetForHorizontal(i2 - 1, left);
            } else {
                off = layout.getOffsetForHorizontal(i2 - 1, lineWidth);
            }
            if (lineWidth >= i - AndroidUtilities.dp(10.0f)) {
                off2 = off;
            } else {
                off2 = off + 3;
            }
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(source.subSequence(0, Math.max(0, off2 - 3)));
            stringBuilder.append((CharSequence) "…");
            try {
                if (Build.VERSION.SDK_INT < 23) {
                    return new StaticLayout(stringBuilder, paint, outerWidth, align, spacingMult, spacingAdd, includePad);
                }
                try {
                } catch (Exception e3) {
                    e = e3;
                }
                try {
                } catch (Exception e4) {
                    e = e4;
                    FileLog.e(e);
                    return null;
                }
                try {
                } catch (Exception e5) {
                    e = e5;
                    FileLog.e(e);
                    return null;
                }
                try {
                    StaticLayout.Builder maxLines2 = StaticLayout.Builder.obtain(stringBuilder, 0, stringBuilder.length(), paint, outerWidth).setAlignment(align).setLineSpacing(spacingAdd, spacingMult).setIncludePad(z).setEllipsize(ellipsize).setEllipsizedWidth(i).setMaxLines(i2);
                    if (!canContainUrl) {
                        i3 = 0;
                    }
                    StaticLayout.Builder builder2 = maxLines2.setBreakStrategy(i3).setHyphenationFrequency(0);
                    return builder2.build();
                } catch (Exception e6) {
                    e = e6;
                    FileLog.e(e);
                    return null;
                }
            } catch (Exception e7) {
                e = e7;
                FileLog.e(e);
                return null;
            }
        } catch (Exception e8) {
            e = e8;
        }
    }
}
