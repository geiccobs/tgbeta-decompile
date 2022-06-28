package com.stripe.android;

import com.stripe.android.model.Token;
/* loaded from: classes3.dex */
public interface TokenCallback {
    void onError(Exception exc);

    void onSuccess(Token token);
}
