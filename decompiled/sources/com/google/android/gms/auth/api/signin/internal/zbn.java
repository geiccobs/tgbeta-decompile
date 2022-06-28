package com.google.android.gms.auth.api.signin.internal;

import android.content.Context;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public final class zbn {
    private static zbn zbd = null;
    final Storage zba;
    GoogleSignInAccount zbb;
    GoogleSignInOptions zbc;

    private zbn(Context context) {
        Storage storage = Storage.getInstance(context);
        this.zba = storage;
        this.zbb = storage.getSavedDefaultGoogleSignInAccount();
        this.zbc = storage.getSavedDefaultGoogleSignInOptions();
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

    public final synchronized GoogleSignInAccount zba() {
        return this.zbb;
    }

    public final synchronized GoogleSignInOptions zbb() {
        return this.zbc;
    }

    public final synchronized void zbd() {
        this.zba.clear();
        this.zbb = null;
        this.zbc = null;
    }

    public final synchronized void zbe(GoogleSignInOptions googleSignInOptions, GoogleSignInAccount googleSignInAccount) {
        this.zba.saveDefaultGoogleSignInAccount(googleSignInAccount, googleSignInOptions);
        this.zbb = googleSignInAccount;
        this.zbc = googleSignInOptions;
    }
}
