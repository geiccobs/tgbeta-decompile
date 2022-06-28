package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.PendingResults;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.GoogleApiManager;
import com.google.android.gms.common.logging.Logger;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public final class zbm {
    private static final Logger zba = new Logger("GoogleSignInCommon", new String[0]);

    public static Intent zba(Context context, GoogleSignInOptions googleSignInOptions) {
        zba.d("getFallbackSignInIntent()", new Object[0]);
        Intent zbc = zbc(context, googleSignInOptions);
        zbc.setAction("com.google.android.gms.auth.APPAUTH_SIGN_IN");
        return zbc;
    }

    public static Intent zbb(Context context, GoogleSignInOptions googleSignInOptions) {
        zba.d("getNoImplementationSignInIntent()", new Object[0]);
        Intent zbc = zbc(context, googleSignInOptions);
        zbc.setAction("com.google.android.gms.auth.NO_IMPL");
        return zbc;
    }

    public static Intent zbc(Context context, GoogleSignInOptions googleSignInOptions) {
        zba.d("getSignInIntent()", new Object[0]);
        SignInConfiguration signInConfiguration = new SignInConfiguration(context.getPackageName(), googleSignInOptions);
        Intent intent = new Intent("com.google.android.gms.auth.GOOGLE_SIGN_IN");
        intent.setPackage(context.getPackageName());
        intent.setClass(context, SignInHubActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("config", signInConfiguration);
        intent.putExtra("config", bundle);
        return intent;
    }

    public static GoogleSignInResult zbd(Intent intent) {
        if (intent == null) {
            return new GoogleSignInResult(null, Status.RESULT_INTERNAL_ERROR);
        }
        Status status = (Status) intent.getParcelableExtra("googleSignInStatus");
        GoogleSignInAccount googleSignInAccount = (GoogleSignInAccount) intent.getParcelableExtra("googleSignInAccount");
        if (googleSignInAccount == null) {
            if (status == null) {
                status = Status.RESULT_INTERNAL_ERROR;
            }
            return new GoogleSignInResult(null, status);
        }
        return new GoogleSignInResult(googleSignInAccount, Status.RESULT_SUCCESS);
    }

    /* JADX WARN: Code restructure failed: missing block: B:7:0x002c, code lost:
        if (r5 == null) goto L12;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static com.google.android.gms.common.api.OptionalPendingResult<com.google.android.gms.auth.api.signin.GoogleSignInResult> zbe(com.google.android.gms.common.api.GoogleApiClient r6, android.content.Context r7, com.google.android.gms.auth.api.signin.GoogleSignInOptions r8, boolean r9) {
        /*
            com.google.android.gms.common.logging.Logger r0 = com.google.android.gms.auth.api.signin.internal.zbm.zba
            r1 = 0
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r3 = "silentSignIn()"
            r0.d(r3, r2)
            java.lang.Object[] r2 = new java.lang.Object[r1]
            java.lang.String r3 = "getEligibleSavedSignInResult()"
            r0.d(r3, r2)
            com.google.android.gms.common.internal.Preconditions.checkNotNull(r8)
            com.google.android.gms.auth.api.signin.internal.zbn r2 = com.google.android.gms.auth.api.signin.internal.zbn.zbc(r7)
            com.google.android.gms.auth.api.signin.GoogleSignInOptions r2 = r2.zbb()
            r3 = 0
            if (r2 != 0) goto L22
            r4 = r3
            goto L90
        L22:
            android.accounts.Account r4 = r2.getAccount()
            android.accounts.Account r5 = r8.getAccount()
            if (r4 != 0) goto L31
            if (r5 != 0) goto L2f
            goto L39
        L2f:
            r4 = r3
            goto L90
        L31:
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto L39
            r4 = r3
            goto L90
        L39:
            boolean r4 = r8.isServerAuthCodeRequested()
            if (r4 == 0) goto L41
            r4 = r3
            goto L90
        L41:
            boolean r4 = r8.isIdTokenRequested()
            if (r4 == 0) goto L5f
            boolean r4 = r2.isIdTokenRequested()
            if (r4 != 0) goto L4f
            r4 = r3
            goto L90
        L4f:
            java.lang.String r4 = r8.getServerClientId()
            java.lang.String r5 = r2.getServerClientId()
            boolean r4 = com.google.android.gms.common.internal.Objects.equal(r4, r5)
            if (r4 != 0) goto L5f
            r4 = r3
            goto L90
        L5f:
            java.util.HashSet r4 = new java.util.HashSet
            java.util.ArrayList r2 = r2.getScopes()
            r4.<init>(r2)
            java.util.HashSet r2 = new java.util.HashSet
            java.util.ArrayList r5 = r8.getScopes()
            r2.<init>(r5)
            boolean r2 = r4.containsAll(r2)
            if (r2 != 0) goto L79
            r4 = r3
            goto L90
        L79:
            com.google.android.gms.auth.api.signin.internal.zbn r2 = com.google.android.gms.auth.api.signin.internal.zbn.zbc(r7)
            com.google.android.gms.auth.api.signin.GoogleSignInAccount r2 = r2.zba()
            if (r2 == 0) goto L2f
            boolean r4 = r2.isExpired()
            if (r4 != 0) goto L2f
            com.google.android.gms.auth.api.signin.GoogleSignInResult r4 = new com.google.android.gms.auth.api.signin.GoogleSignInResult
            com.google.android.gms.common.api.Status r5 = com.google.android.gms.common.api.Status.RESULT_SUCCESS
            r4.<init>(r2, r5)
        L90:
            if (r4 == 0) goto L9e
            java.lang.Object[] r7 = new java.lang.Object[r1]
            java.lang.String r8 = "Eligible saved sign in result found"
            r0.d(r8, r7)
            com.google.android.gms.common.api.OptionalPendingResult r6 = com.google.android.gms.common.api.PendingResults.immediatePendingResult(r4, r6)
            return r6
        L9e:
            if (r9 == 0) goto Lb0
            com.google.android.gms.auth.api.signin.GoogleSignInResult r7 = new com.google.android.gms.auth.api.signin.GoogleSignInResult
            com.google.android.gms.common.api.Status r8 = new com.google.android.gms.common.api.Status
            r9 = 4
            r8.<init>(r9)
            r7.<init>(r3, r8)
            com.google.android.gms.common.api.OptionalPendingResult r6 = com.google.android.gms.common.api.PendingResults.immediatePendingResult(r7, r6)
            return r6
        Lb0:
            java.lang.Object[] r9 = new java.lang.Object[r1]
            java.lang.String r1 = "trySilentSignIn()"
            r0.d(r1, r9)
            com.google.android.gms.auth.api.signin.internal.zbg r9 = new com.google.android.gms.auth.api.signin.internal.zbg
            r9.<init>(r6, r7, r8)
            com.google.android.gms.common.api.internal.BaseImplementation$ApiMethodImpl r6 = r6.enqueue(r9)
            com.google.android.gms.common.api.internal.OptionalPendingResultImpl r7 = new com.google.android.gms.common.api.internal.OptionalPendingResultImpl
            r7.<init>(r6)
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.auth.api.signin.internal.zbm.zbe(com.google.android.gms.common.api.GoogleApiClient, android.content.Context, com.google.android.gms.auth.api.signin.GoogleSignInOptions, boolean):com.google.android.gms.common.api.OptionalPendingResult");
    }

    public static PendingResult<Status> zbf(GoogleApiClient googleApiClient, Context context, boolean z) {
        zba.d("Revoking access", new Object[0]);
        String savedRefreshToken = Storage.getInstance(context).getSavedRefreshToken();
        zbh(context);
        if (z) {
            return zbb.zba(savedRefreshToken);
        }
        return googleApiClient.execute(new zbk(googleApiClient));
    }

    public static PendingResult<Status> zbg(GoogleApiClient googleApiClient, Context context, boolean z) {
        zba.d("Signing out", new Object[0]);
        zbh(context);
        if (z) {
            return PendingResults.immediatePendingResult(Status.RESULT_SUCCESS, googleApiClient);
        }
        return googleApiClient.execute(new zbi(googleApiClient));
    }

    private static void zbh(Context context) {
        zbn.zbc(context).zbd();
        for (GoogleApiClient googleApiClient : GoogleApiClient.getAllClients()) {
            googleApiClient.maybeSignOut();
        }
        GoogleApiManager.reportSignOut();
    }
}
