package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.ActionBar.ThemeDescription;
/* loaded from: classes5.dex */
public class SimpleThemeDescription {
    private SimpleThemeDescription() {
    }

    public static ThemeDescription createThemeDescription(ThemeDescription.ThemeDescriptionDelegate del, String key) {
        return new ThemeDescription(null, 0, null, null, null, del, key);
    }

    public static ArrayList<ThemeDescription> createThemeDescriptions(ThemeDescription.ThemeDescriptionDelegate del, String... keys) {
        ArrayList<ThemeDescription> l = new ArrayList<>(keys.length);
        for (String k : keys) {
            l.add(createThemeDescription(del, k));
        }
        return l;
    }
}
