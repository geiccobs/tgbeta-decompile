package androidx.core.graphics;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.graphics.fonts.FontStyle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import androidx.core.content.res.FontResourcesParserCompat;
import androidx.core.provider.FontsContractCompat;
import java.io.IOException;
import java.io.InputStream;
/* loaded from: classes3.dex */
public class TypefaceCompatApi29Impl extends TypefaceCompatBaseImpl {
    @Override // androidx.core.graphics.TypefaceCompatBaseImpl
    public FontsContractCompat.FontInfo findBestInfo(FontsContractCompat.FontInfo[] fonts, int style) {
        throw new RuntimeException("Do not use this function in API 29 or later.");
    }

    @Override // androidx.core.graphics.TypefaceCompatBaseImpl
    public Typeface createFromInputStream(Context context, InputStream is) {
        throw new RuntimeException("Do not use this function in API 29 or later.");
    }

    @Override // androidx.core.graphics.TypefaceCompatBaseImpl
    public Typeface createFromFontInfo(Context context, CancellationSignal cancellationSignal, FontsContractCompat.FontInfo[] fonts, int style) {
        FontFamily.Builder familyBuilder = null;
        ContentResolver resolver = context.getContentResolver();
        try {
            int length = fonts.length;
            int i = 0;
            int i2 = 0;
            while (true) {
                int i3 = 1;
                if (i2 < length) {
                    FontsContractCompat.FontInfo font = fonts[i2];
                    try {
                        ParcelFileDescriptor pfd = resolver.openFileDescriptor(font.getUri(), "r", cancellationSignal);
                        if (pfd != null) {
                            try {
                                Font.Builder weight = new Font.Builder(pfd).setWeight(font.getWeight());
                                if (!font.isItalic()) {
                                    i3 = 0;
                                }
                                Font platformFont = weight.setSlant(i3).setTtcIndex(font.getTtcIndex()).build();
                                if (familyBuilder == null) {
                                    familyBuilder = new FontFamily.Builder(platformFont);
                                } else {
                                    familyBuilder.addFont(platformFont);
                                }
                                if (pfd != null) {
                                    pfd.close();
                                }
                            } catch (Throwable th) {
                                if (pfd != null) {
                                    try {
                                        pfd.close();
                                    } catch (Throwable th2) {
                                    }
                                }
                                throw th;
                                break;
                            }
                        } else if (pfd != null) {
                            pfd.close();
                        }
                    } catch (IOException e) {
                    }
                    i2++;
                } else if (familyBuilder == null) {
                    return null;
                } else {
                    int i4 = (style & 1) != 0 ? 700 : 400;
                    if ((style & 2) != 0) {
                        i = 1;
                    }
                    FontStyle defaultStyle = new FontStyle(i4, i);
                    return new Typeface.CustomFallbackBuilder(familyBuilder.build()).setStyle(defaultStyle).build();
                }
            }
        } catch (Exception e2) {
            return null;
        }
    }

    @Override // androidx.core.graphics.TypefaceCompatBaseImpl
    public Typeface createFromFontFamilyFilesResourceEntry(Context context, FontResourcesParserCompat.FontFamilyFilesResourceEntry familyEntry, Resources resources, int style) {
        FontFamily.Builder familyBuilder = null;
        try {
            FontResourcesParserCompat.FontFileResourceEntry[] entries = familyEntry.getEntries();
            int length = entries.length;
            int i = 0;
            int i2 = 0;
            while (true) {
                int i3 = 1;
                if (i2 >= length) {
                    break;
                }
                FontResourcesParserCompat.FontFileResourceEntry entry = entries[i2];
                try {
                    Font.Builder weight = new Font.Builder(resources, entry.getResourceId()).setWeight(entry.getWeight());
                    if (!entry.isItalic()) {
                        i3 = 0;
                    }
                    Font platformFont = weight.setSlant(i3).setTtcIndex(entry.getTtcIndex()).setFontVariationSettings(entry.getVariationSettings()).build();
                    if (familyBuilder == null) {
                        familyBuilder = new FontFamily.Builder(platformFont);
                    } else {
                        familyBuilder.addFont(platformFont);
                    }
                } catch (IOException e) {
                }
                i2++;
            }
            if (familyBuilder == null) {
                return null;
            }
            int i4 = (style & 1) != 0 ? 700 : 400;
            if ((style & 2) != 0) {
                i = 1;
            }
            FontStyle defaultStyle = new FontStyle(i4, i);
            return new Typeface.CustomFallbackBuilder(familyBuilder.build()).setStyle(defaultStyle).build();
        } catch (Exception e2) {
            return null;
        }
    }

    @Override // androidx.core.graphics.TypefaceCompatBaseImpl
    public Typeface createFromResourcesFontFile(Context context, Resources resources, int id, String path, int style) {
        try {
            Font font = new Font.Builder(resources, id).build();
            FontFamily family = new FontFamily.Builder(font).build();
            return new Typeface.CustomFallbackBuilder(family).setStyle(font.getStyle()).build();
        } catch (Exception e) {
            return null;
        }
    }
}
