package com.google.android.gms.auth.api.identity;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.p001authapi.zbam;
import com.google.android.gms.internal.p001authapi.zbau;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public final class Identity {
    private Identity() {
    }

    public static CredentialSavingClient getCredentialSavingClient(Activity activity) {
        int i = zbc.zba;
        return new zbam((Activity) Preconditions.checkNotNull(activity), new zbc());
    }

    public static SignInClient getSignInClient(Activity activity) {
        int i = zbl.zba;
        return new zbau((Activity) Preconditions.checkNotNull(activity), new zbl());
    }

    public static CredentialSavingClient getCredentialSavingClient(Context context) {
        int i = zbc.zba;
        return new zbam((Context) Preconditions.checkNotNull(context), new zbc());
    }

    public static SignInClient getSignInClient(Context context) {
        int i = zbl.zba;
        return new zbau((Context) Preconditions.checkNotNull(context), new zbl());
    }
}
