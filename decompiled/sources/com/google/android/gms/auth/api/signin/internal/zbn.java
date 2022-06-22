package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes.dex */
public final class zbn {
    private static zbn zbd;
    final Storage zba;

    private zbn(Context context) {
        Storage storage = Storage.getInstance(context);
        this.zba = storage;
        storage.getSavedDefaultGoogleSignInAccount();
        storage.getSavedDefaultGoogleSignInOptions();
    }

    public static synchronized zbn zbc(Context context) {
        zbn zbf;
        synchronized (zbn.class) {
            zbf = zbf(context.getApplicationContext());
        }
        return zbf;
    }

    private static synchronized zbn zbf(Context context) {
        synchronized (zbn.class) {
            zbn zbnVar = zbd;
            if (zbnVar != null) {
                return zbnVar;
            }
            zbn zbnVar2 = new zbn(context);
            zbd = zbnVar2;
            return zbnVar2;
        }
    }

    public final synchronized void zbd() {
        this.zba.clear();
    }

    public final synchronized void zbe(GoogleSignInOptions googleSignInOptions, GoogleSignInAccount googleSignInAccount) {
        this.zba.saveDefaultGoogleSignInAccount(googleSignInAccount, googleSignInOptions);
    }
}
