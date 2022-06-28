package com.google.android.gms.auth.api.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.zbm;
import com.google.android.gms.auth.api.signin.internal.zbn;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.internal.ApiExceptionUtil;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public final class GoogleSignIn {
    private GoogleSignIn() {
    }

    public static GoogleSignInAccount getAccountForExtension(Context context, GoogleSignInOptionsExtension extension) {
        Preconditions.checkNotNull(context, "please provide a valid Context object");
        Preconditions.checkNotNull(extension, "please provide valid GoogleSignInOptionsExtension");
        GoogleSignInAccount lastSignedInAccount = getLastSignedInAccount(context);
        if (lastSignedInAccount == null) {
            lastSignedInAccount = GoogleSignInAccount.createDefault();
        }
        return lastSignedInAccount.requestExtraScopes(zbb(extension.getImpliedScopes()));
    }

    public static GoogleSignInAccount getAccountForScopes(Context context, Scope scope, Scope... scopes) {
        Preconditions.checkNotNull(context, "please provide a valid Context object");
        Preconditions.checkNotNull(scope, "please provide at least one valid scope");
        GoogleSignInAccount lastSignedInAccount = getLastSignedInAccount(context);
        if (lastSignedInAccount == null) {
            lastSignedInAccount = GoogleSignInAccount.createDefault();
        }
        lastSignedInAccount.requestExtraScopes(scope);
        lastSignedInAccount.requestExtraScopes(scopes);
        return lastSignedInAccount;
    }

    public static GoogleSignInClient getClient(Activity activity, GoogleSignInOptions options) {
        return new GoogleSignInClient(activity, (GoogleSignInOptions) Preconditions.checkNotNull(options));
    }

    public static GoogleSignInAccount getLastSignedInAccount(Context context) {
        return zbn.zbc(context).zba();
    }

    public static Task<GoogleSignInAccount> getSignedInAccountFromIntent(Intent data) {
        GoogleSignInResult zbd = zbm.zbd(data);
        GoogleSignInAccount signInAccount = zbd.getSignInAccount();
        if (!zbd.getStatus().isSuccess() || signInAccount == null) {
            return Tasks.forException(ApiExceptionUtil.fromStatus(zbd.getStatus()));
        }
        return Tasks.forResult(signInAccount);
    }

    public static boolean hasPermissions(GoogleSignInAccount account, GoogleSignInOptionsExtension extension) {
        Preconditions.checkNotNull(extension, "Please provide a non-null GoogleSignInOptionsExtension");
        return hasPermissions(account, zbb(extension.getImpliedScopes()));
    }

    public static void requestPermissions(Activity activity, int requestCode, GoogleSignInAccount account, GoogleSignInOptionsExtension extension) {
        Preconditions.checkNotNull(activity, "Please provide a non-null Activity");
        Preconditions.checkNotNull(extension, "Please provide a non-null GoogleSignInOptionsExtension");
        requestPermissions(activity, requestCode, account, zbb(extension.getImpliedScopes()));
    }

    private static Intent zba(Activity activity, GoogleSignInAccount googleSignInAccount, Scope... scopeArr) {
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder();
        if (scopeArr.length > 0) {
            builder.requestScopes(scopeArr[0], scopeArr);
        }
        if (googleSignInAccount != null && !TextUtils.isEmpty(googleSignInAccount.getEmail())) {
            builder.setAccountName((String) Preconditions.checkNotNull(googleSignInAccount.getEmail()));
        }
        return new GoogleSignInClient(activity, builder.build()).getSignInIntent();
    }

    private static Scope[] zbb(List<Scope> list) {
        return list == null ? new Scope[0] : (Scope[]) list.toArray(new Scope[list.size()]);
    }

    public static GoogleSignInClient getClient(Context context, GoogleSignInOptions options) {
        return new GoogleSignInClient(context, (GoogleSignInOptions) Preconditions.checkNotNull(options));
    }

    public static boolean hasPermissions(GoogleSignInAccount account, Scope... scopes) {
        if (account == null) {
            return false;
        }
        HashSet hashSet = new HashSet();
        Collections.addAll(hashSet, scopes);
        return account.getGrantedScopes().containsAll(hashSet);
    }

    public static void requestPermissions(Activity activity, int requestCode, GoogleSignInAccount account, Scope... scopes) {
        Preconditions.checkNotNull(activity, "Please provide a non-null Activity");
        Preconditions.checkNotNull(scopes, "Please provide at least one scope");
        activity.startActivityForResult(zba(activity, account, scopes), requestCode);
    }

    public static void requestPermissions(Fragment fragment, int requestCode, GoogleSignInAccount account, GoogleSignInOptionsExtension extension) {
        Preconditions.checkNotNull(fragment, "Please provide a non-null Fragment");
        Preconditions.checkNotNull(extension, "Please provide a non-null GoogleSignInOptionsExtension");
        requestPermissions(fragment, requestCode, account, zbb(extension.getImpliedScopes()));
    }

    public static void requestPermissions(Fragment fragment, int requestCode, GoogleSignInAccount account, Scope... scopes) {
        Preconditions.checkNotNull(fragment, "Please provide a non-null Fragment");
        Preconditions.checkNotNull(scopes, "Please provide at least one scope");
        fragment.startActivityForResult(zba(fragment.getActivity(), account, scopes), requestCode);
    }
}
