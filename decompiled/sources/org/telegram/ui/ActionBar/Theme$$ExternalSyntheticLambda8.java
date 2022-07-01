package org.telegram.ui.ActionBar;

import java.util.Comparator;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public final /* synthetic */ class Theme$$ExternalSyntheticLambda8 implements Comparator {
    public static final /* synthetic */ Theme$$ExternalSyntheticLambda8 INSTANCE = new Theme$$ExternalSyntheticLambda8();

    private /* synthetic */ Theme$$ExternalSyntheticLambda8() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$sortThemes$1;
        lambda$sortThemes$1 = Theme.lambda$sortThemes$1((Theme.ThemeInfo) obj, (Theme.ThemeInfo) obj2);
        return lambda$sortThemes$1;
    }
}
