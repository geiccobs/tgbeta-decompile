package com.google.android.gms.auth.api.signin;

import com.google.android.gms.common.internal.PendingResultUtil;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
final class zbb implements PendingResultUtil.ResultConverter<GoogleSignInResult, GoogleSignInAccount> {
    private zbb() {
    }

    public /* synthetic */ zbb(zba zbaVar) {
    }

    @Override // com.google.android.gms.common.internal.PendingResultUtil.ResultConverter
    public final /* bridge */ /* synthetic */ GoogleSignInAccount convert(GoogleSignInResult googleSignInResult) {
        return googleSignInResult.getSignInAccount();
    }
}
