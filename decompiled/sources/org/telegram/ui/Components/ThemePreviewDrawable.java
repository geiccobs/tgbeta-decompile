package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import java.io.File;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.beta.R;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes5.dex */
public class ThemePreviewDrawable extends BitmapDrawable {
    private DocumentObject.ThemeDocument themeDocument;

    public ThemePreviewDrawable(File pattern, DocumentObject.ThemeDocument document) {
        super(createPreview(pattern, document));
        this.themeDocument = document;
    }

    public DocumentObject.ThemeDocument getThemeDocument() {
        return this.themeDocument;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v19, types: [android.graphics.drawable.ColorDrawable] */
    private static Bitmap createPreview(File pattern, DocumentObject.ThemeDocument themeDocument) {
        Integer gradientRotation;
        Drawable otherDrawable;
        Drawable emojiDrawable;
        Drawable micDrawable;
        Theme.MessageDrawable[] messageDrawable;
        boolean hasBackground;
        int patternColor;
        float scaleFactor;
        new RectF();
        Paint paint = new Paint();
        Bitmap bitmap = Bitmaps.createBitmap(560, 678, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        HashMap<String, Integer> baseColors = Theme.getThemeFileValues(null, themeDocument.baseTheme.assetName, null);
        final HashMap<String, Integer> colors = new HashMap<>(baseColors);
        themeDocument.accent.fillAccentColors(baseColors, colors);
        int actionBarColor = Theme.getPreviewColor(colors, Theme.key_actionBarDefault);
        int actionBarIconColor = Theme.getPreviewColor(colors, Theme.key_actionBarDefaultIcon);
        int messageFieldColor = Theme.getPreviewColor(colors, Theme.key_chat_messagePanelBackground);
        int messageFieldIconColor = Theme.getPreviewColor(colors, Theme.key_chat_messagePanelIcons);
        int messageInColor = Theme.getPreviewColor(colors, Theme.key_chat_inBubble);
        int messageOutColor = Theme.getPreviewColor(colors, Theme.key_chat_outBubble);
        Integer backgroundColor = colors.get(Theme.key_chat_wallpaper);
        Integer gradientToColor1 = colors.get(Theme.key_chat_wallpaper_gradient_to1);
        Integer gradientToColor2 = colors.get(Theme.key_chat_wallpaper_gradient_to2);
        Integer gradientToColor3 = colors.get(Theme.key_chat_wallpaper_gradient_to3);
        Integer gradientRotation2 = colors.get(Theme.key_chat_wallpaper_gradient_rotation);
        if (gradientRotation2 != null) {
            gradientRotation = gradientRotation2;
        } else {
            gradientRotation = 45;
        }
        Drawable backDrawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.preview_back).mutate();
        Theme.setDrawableColor(backDrawable, actionBarIconColor);
        Drawable otherDrawable2 = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.preview_dots).mutate();
        Theme.setDrawableColor(otherDrawable2, actionBarIconColor);
        Drawable emojiDrawable2 = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.preview_smile).mutate();
        Theme.setDrawableColor(emojiDrawable2, messageFieldIconColor);
        Drawable micDrawable2 = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.preview_mic).mutate();
        Theme.setDrawableColor(micDrawable2, messageFieldIconColor);
        Theme.MessageDrawable[] messageDrawable2 = new Theme.MessageDrawable[2];
        int a = 0;
        while (true) {
            otherDrawable = otherDrawable2;
            emojiDrawable = emojiDrawable2;
            boolean z = true;
            if (a >= 2) {
                break;
            }
            if (a != 1) {
                z = false;
            }
            Drawable backDrawable2 = backDrawable;
            int messageFieldColor2 = messageFieldColor;
            messageDrawable2[a] = new Theme.MessageDrawable(2, z, false) { // from class: org.telegram.ui.Components.ThemePreviewDrawable.1
                @Override // org.telegram.ui.ActionBar.Theme.MessageDrawable
                protected int getColor(String key) {
                    Integer color = (Integer) colors.get(key);
                    if (color == null) {
                        return Theme.getColor(key);
                    }
                    return color.intValue();
                }

                @Override // org.telegram.ui.ActionBar.Theme.MessageDrawable
                protected Integer getCurrentColor(String key) {
                    return (Integer) colors.get(key);
                }
            };
            Theme.setDrawableColor(messageDrawable2[a], a == 1 ? messageOutColor : messageInColor);
            a++;
            otherDrawable2 = otherDrawable;
            emojiDrawable2 = emojiDrawable;
            backDrawable = backDrawable2;
            messageFieldColor = messageFieldColor2;
        }
        Drawable backDrawable3 = backDrawable;
        int messageFieldColor3 = messageFieldColor;
        if (backgroundColor == null) {
            micDrawable = micDrawable2;
            messageDrawable = messageDrawable2;
            hasBackground = false;
        } else {
            BitmapDrawable bitmapDrawable = null;
            MotionBackgroundDrawable motionBackgroundDrawable = null;
            if (gradientToColor1 == null) {
                bitmapDrawable = new ColorDrawable(backgroundColor.intValue());
                patternColor = AndroidUtilities.getPatternColor(backgroundColor.intValue());
            } else {
                int patternColor2 = gradientToColor2.intValue();
                if (patternColor2 != 0) {
                    motionBackgroundDrawable = new MotionBackgroundDrawable(backgroundColor.intValue(), gradientToColor1.intValue(), gradientToColor2.intValue(), gradientToColor3.intValue(), true);
                } else {
                    int[] gradientColors = {backgroundColor.intValue(), gradientToColor1.intValue()};
                    bitmapDrawable = BackgroundGradientDrawable.createDitheredGradientBitmapDrawable(gradientRotation.intValue(), gradientColors, bitmap.getWidth(), bitmap.getHeight() - 120);
                }
                patternColor = AndroidUtilities.getPatternColor(AndroidUtilities.getAverageColor(backgroundColor.intValue(), gradientToColor1.intValue()));
            }
            if (bitmapDrawable == null) {
                messageDrawable = messageDrawable2;
            } else {
                messageDrawable = messageDrawable2;
                bitmapDrawable.setBounds(0, 120, bitmap.getWidth(), bitmap.getHeight() - 120);
                bitmapDrawable.draw(canvas);
            }
            Bitmap patternBitmap = null;
            if (pattern == null) {
                micDrawable = micDrawable2;
            } else {
                if ("application/x-tgwallpattern".equals(themeDocument.mime_type)) {
                    micDrawable = micDrawable2;
                    patternBitmap = SvgHelper.getBitmap(pattern, 560, 678, false);
                } else {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 1;
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(pattern.getAbsolutePath(), opts);
                    float photoW = opts.outWidth;
                    float photoH = opts.outHeight;
                    if (560 >= 678 && photoW > photoH) {
                        micDrawable = micDrawable2;
                        scaleFactor = Math.max(photoW / 560, photoH / 678);
                    } else {
                        micDrawable = micDrawable2;
                        scaleFactor = Math.min(photoW / 560, photoH / 678);
                    }
                    if (scaleFactor < 1.2f) {
                        scaleFactor = 1.0f;
                    }
                    opts.inJustDecodeBounds = false;
                    if (scaleFactor > 1.0f && (photoW > 560 || photoH > 678)) {
                        int sample = 1;
                        while (true) {
                            sample *= 2;
                            float photoH2 = photoH;
                            if (sample * 2 >= scaleFactor) {
                                break;
                            }
                            photoH = photoH2;
                        }
                        opts.inSampleSize = sample;
                        patternBitmap = BitmapFactory.decodeFile(pattern.getAbsolutePath(), opts);
                    }
                    opts.inSampleSize = (int) scaleFactor;
                    patternBitmap = BitmapFactory.decodeFile(pattern.getAbsolutePath(), opts);
                }
                if (patternBitmap != null) {
                    if (motionBackgroundDrawable != null) {
                        motionBackgroundDrawable.setPatternBitmap((int) (themeDocument.accent.patternIntensity * 100.0f), patternBitmap);
                        motionBackgroundDrawable.setBounds(0, 120, bitmap.getWidth(), bitmap.getHeight() - 120);
                        motionBackgroundDrawable.draw(canvas);
                    } else {
                        Paint backgroundPaint = new Paint(2);
                        if (themeDocument.accent.patternIntensity >= 0.0f) {
                            backgroundPaint.setColorFilter(new PorterDuffColorFilter(patternColor, PorterDuff.Mode.SRC_IN));
                        }
                        backgroundPaint.setAlpha(255);
                        float scale = Math.max(560 / patternBitmap.getWidth(), 678 / patternBitmap.getHeight());
                        int w = (int) (patternBitmap.getWidth() * scale);
                        int h = (int) (patternBitmap.getHeight() * scale);
                        canvas.save();
                        canvas.translate((560 - w) / 2, (678 - h) / 2);
                        canvas.scale(scale, scale);
                        canvas.drawBitmap(patternBitmap, 0.0f, 0.0f, backgroundPaint);
                        canvas.restore();
                    }
                }
            }
            if (patternBitmap == null && motionBackgroundDrawable != null) {
                motionBackgroundDrawable.setBounds(0, 120, bitmap.getWidth(), bitmap.getHeight() - 120);
                motionBackgroundDrawable.draw(canvas);
            }
            hasBackground = true;
        }
        if (!hasBackground) {
            Drawable catsDrawable = Theme.createDefaultWallpaper(bitmap.getWidth(), bitmap.getHeight() - 120);
            catsDrawable.setBounds(0, 120, bitmap.getWidth(), bitmap.getHeight() - 120);
            catsDrawable.draw(canvas);
        }
        paint.setColor(actionBarColor);
        Theme.MessageDrawable[] messageDrawable3 = messageDrawable;
        canvas.drawRect(0.0f, 0.0f, bitmap.getWidth(), 120.0f, paint);
        if (backDrawable3 != null) {
            int y = (120 - backDrawable3.getIntrinsicHeight()) / 2;
            backDrawable3.setBounds(13, y, backDrawable3.getIntrinsicWidth() + 13, backDrawable3.getIntrinsicHeight() + y);
            backDrawable3.draw(canvas);
        }
        if (otherDrawable != null) {
            int x = (bitmap.getWidth() - otherDrawable.getIntrinsicWidth()) - 10;
            int y2 = (120 - otherDrawable.getIntrinsicHeight()) / 2;
            otherDrawable.setBounds(x, y2, otherDrawable.getIntrinsicWidth() + x, otherDrawable.getIntrinsicHeight() + y2);
            otherDrawable.draw(canvas);
        }
        messageDrawable3[1].setBounds(161, 216, bitmap.getWidth() - 20, 308);
        messageDrawable3[1].setTop(0, 560, 522, false, false);
        messageDrawable3[1].draw(canvas);
        messageDrawable3[1].setBounds(161, 430, bitmap.getWidth() - 20, 522);
        messageDrawable3[1].setTop(430, 560, 522, false, false);
        messageDrawable3[1].draw(canvas);
        messageDrawable3[0].setBounds(20, 323, 399, 415);
        messageDrawable3[0].setTop(323, 560, 522, false, false);
        messageDrawable3[0].draw(canvas);
        paint.setColor(messageFieldColor3);
        canvas.drawRect(0.0f, bitmap.getHeight() - 120, bitmap.getWidth(), bitmap.getHeight(), paint);
        if (emojiDrawable != null) {
            int y3 = (bitmap.getHeight() - 120) + ((120 - emojiDrawable.getIntrinsicHeight()) / 2);
            emojiDrawable.setBounds(22, y3, emojiDrawable.getIntrinsicWidth() + 22, emojiDrawable.getIntrinsicHeight() + y3);
            emojiDrawable.draw(canvas);
        }
        if (micDrawable != null) {
            int x2 = (bitmap.getWidth() - micDrawable.getIntrinsicWidth()) - 22;
            int y4 = (bitmap.getHeight() - 120) + ((120 - micDrawable.getIntrinsicHeight()) / 2);
            Drawable micDrawable3 = micDrawable;
            micDrawable3.setBounds(x2, y4, micDrawable.getIntrinsicWidth() + x2, micDrawable.getIntrinsicHeight() + y4);
            micDrawable3.draw(canvas);
        }
        return bitmap;
    }
}
