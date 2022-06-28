package org.telegram.ui;

import com.google.android.gms.internal.icing.zzby$$ExternalSyntheticBackport0;
import java.util.Comparator;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes4.dex */
public final /* synthetic */ class ThemeActivity$$ExternalSyntheticLambda10 implements Comparator {
    public static final /* synthetic */ ThemeActivity$$ExternalSyntheticLambda10 INSTANCE = new ThemeActivity$$ExternalSyntheticLambda10();

    private /* synthetic */ ThemeActivity$$ExternalSyntheticLambda10() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int m;
        m = zzby$$ExternalSyntheticBackport0.m(((Theme.ThemeInfo) obj).sortIndex, ((Theme.ThemeInfo) obj2).sortIndex);
        return m;
    }
}
