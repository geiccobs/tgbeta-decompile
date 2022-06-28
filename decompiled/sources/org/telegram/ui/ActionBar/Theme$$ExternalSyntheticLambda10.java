package org.telegram.ui.ActionBar;

import java.util.Comparator;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes4.dex */
public final /* synthetic */ class Theme$$ExternalSyntheticLambda10 implements Comparator {
    public static final /* synthetic */ Theme$$ExternalSyntheticLambda10 INSTANCE = new Theme$$ExternalSyntheticLambda10();

    private /* synthetic */ Theme$$ExternalSyntheticLambda10() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return Theme.lambda$sortThemes$1((Theme.ThemeInfo) obj, (Theme.ThemeInfo) obj2);
    }
}
