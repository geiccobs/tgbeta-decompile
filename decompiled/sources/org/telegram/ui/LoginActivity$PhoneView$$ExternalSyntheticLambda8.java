package org.telegram.ui;

import j$.util.function.Function;
import org.telegram.ui.CountrySelectActivity;
/* loaded from: classes4.dex */
public final /* synthetic */ class LoginActivity$PhoneView$$ExternalSyntheticLambda8 implements Function {
    public static final /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda8 INSTANCE = new LoginActivity$PhoneView$$ExternalSyntheticLambda8();

    private /* synthetic */ LoginActivity$PhoneView$$ExternalSyntheticLambda8() {
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function andThen(Function function) {
        return function.getClass();
    }

    @Override // j$.util.function.Function
    public final Object apply(Object obj) {
        String str;
        str = ((CountrySelectActivity.Country) obj).name;
        return str;
    }

    @Override // j$.util.function.Function
    public /* synthetic */ Function compose(Function function) {
        return function.getClass();
    }
}
