package org.telegram.messenger;

import android.graphics.Paint;
import android.graphics.Path;
import java.util.ArrayList;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes4.dex */
public class DocumentObject {

    /* loaded from: classes4.dex */
    public static class ThemeDocument extends TLRPC.TL_document {
        public Theme.ThemeAccent accent;
        public Theme.ThemeInfo baseTheme;
        public TLRPC.ThemeSettings themeSettings;
        public TLRPC.Document wallpaper;

        public ThemeDocument(TLRPC.ThemeSettings settings) {
            this.themeSettings = settings;
            Theme.ThemeInfo theme = Theme.getTheme(Theme.getBaseThemeKey(settings));
            this.baseTheme = theme;
            this.accent = theme.createNewAccent(settings);
            if (this.themeSettings.wallpaper instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper object = (TLRPC.TL_wallPaper) this.themeSettings.wallpaper;
                TLRPC.Document document = object.document;
                this.wallpaper = document;
                this.id = document.id;
                this.access_hash = this.wallpaper.access_hash;
                this.file_reference = this.wallpaper.file_reference;
                this.user_id = this.wallpaper.user_id;
                this.date = this.wallpaper.date;
                this.file_name = this.wallpaper.file_name;
                this.mime_type = this.wallpaper.mime_type;
                this.size = this.wallpaper.size;
                this.thumbs = this.wallpaper.thumbs;
                this.version = this.wallpaper.version;
                this.dc_id = this.wallpaper.dc_id;
                this.key = this.wallpaper.key;
                this.iv = this.wallpaper.iv;
                this.attributes = this.wallpaper.attributes;
                return;
            }
            this.id = -2147483648L;
            this.dc_id = Integer.MIN_VALUE;
        }
    }

    public static SvgHelper.SvgDrawable getSvgThumb(ArrayList<TLRPC.PhotoSize> sizes, String colorKey, float alpha) {
        int w = 0;
        int h = 0;
        TLRPC.TL_photoPathSize photoPathSize = null;
        int N = sizes.size();
        for (int a = 0; a < N; a++) {
            TLRPC.PhotoSize photoSize = sizes.get(a);
            if (photoSize instanceof TLRPC.TL_photoPathSize) {
                photoPathSize = (TLRPC.TL_photoPathSize) photoSize;
            } else {
                w = photoSize.w;
                h = photoSize.h;
            }
            if (photoPathSize != null && w != 0 && h != 0) {
                SvgHelper.SvgDrawable pathThumb = SvgHelper.getDrawableByPath(SvgHelper.decompress(photoPathSize.bytes), w, h);
                if (pathThumb != null) {
                    pathThumb.setupGradient(colorKey, alpha);
                }
                return pathThumb;
            }
        }
        return null;
    }

    public static SvgHelper.SvgDrawable getSvgThumb(TLRPC.Document document, String colorKey, float alpha) {
        return getSvgThumb(document, colorKey, alpha, 1.0f);
    }

    public static SvgHelper.SvgDrawable getSvgRectThumb(String colorKey, float alpha) {
        Path path = new Path();
        path.addRect(0.0f, 0.0f, 512.0f, 512.0f, Path.Direction.CW);
        path.close();
        SvgHelper.SvgDrawable drawable = new SvgHelper.SvgDrawable();
        drawable.commands.add(path);
        drawable.paints.put(path, new Paint(1));
        drawable.width = 512;
        drawable.height = 512;
        drawable.setupGradient(colorKey, alpha);
        return drawable;
    }

    public static SvgHelper.SvgDrawable getSvgThumb(TLRPC.Document document, String colorKey, float alpha, float zoom) {
        if (document == null) {
            return null;
        }
        SvgHelper.SvgDrawable pathThumb = null;
        int b = 0;
        int N2 = document.thumbs.size();
        while (true) {
            if (b >= N2) {
                break;
            }
            TLRPC.PhotoSize size = document.thumbs.get(b);
            if (!(size instanceof TLRPC.TL_photoPathSize)) {
                b++;
            } else {
                int w = 512;
                int h = 512;
                int a = 0;
                int N = document.attributes.size();
                while (true) {
                    if (a >= N) {
                        break;
                    }
                    TLRPC.DocumentAttribute attribute = document.attributes.get(a);
                    if (!(attribute instanceof TLRPC.TL_documentAttributeImageSize)) {
                        a++;
                    } else {
                        w = attribute.w;
                        h = attribute.h;
                        break;
                    }
                }
                if (w != 0 && h != 0 && (pathThumb = SvgHelper.getDrawableByPath(SvgHelper.decompress(size.bytes), (int) (w * zoom), (int) (h * zoom))) != null) {
                    pathThumb.setupGradient(colorKey, alpha);
                }
            }
        }
        return pathThumb;
    }
}
