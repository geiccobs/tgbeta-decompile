package androidx.core.graphics;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
/* loaded from: classes3.dex */
public class BlendModeColorFilterCompat {
    public static ColorFilter createBlendModeColorFilterCompat(int color, BlendModeCompat blendModeCompat) {
        if (Build.VERSION.SDK_INT >= 29) {
            BlendMode blendMode = BlendModeUtils.obtainBlendModeFromCompat(blendModeCompat);
            if (blendMode == null) {
                return null;
            }
            return new BlendModeColorFilter(color, blendMode);
        }
        PorterDuff.Mode porterDuffMode = BlendModeUtils.obtainPorterDuffFromCompat(blendModeCompat);
        if (porterDuffMode == null) {
            return null;
        }
        return new PorterDuffColorFilter(color, porterDuffMode);
    }

    private BlendModeColorFilterCompat() {
    }
}
