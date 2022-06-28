package com.google.android.gms.auth.api.identity;

import android.app.PendingIntent;
import android.content.Intent;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.HasApiKey;
import com.google.android.gms.tasks.Task;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public interface SignInClient extends HasApiKey<zbl> {
    Task<BeginSignInResult> beginSignIn(BeginSignInRequest beginSignInRequest);

    SignInCredential getSignInCredentialFromIntent(Intent intent) throws ApiException;

    Task<PendingIntent> getSignInIntent(GetSignInIntentRequest getSignInIntentRequest);

    Task<Void> signOut();
}
