package org.telegram.ui;

import java.util.Comparator;
import org.telegram.ui.CountrySelectActivity;
/* loaded from: classes4.dex */
public final /* synthetic */ class CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda1 implements Comparator {
    public static final /* synthetic */ CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda1 INSTANCE = new CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda1();

    private /* synthetic */ CountrySelectActivity$CountryAdapter$$ExternalSyntheticLambda1() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int compareTo;
        compareTo = ((CountrySelectActivity.Country) obj).name.compareTo(((CountrySelectActivity.Country) obj2).name);
        return compareTo;
    }
}
