package com.google.android.gms.auth.api.signin.internal;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.api.Status;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* loaded from: classes3.dex */
public class SignInHubActivity extends FragmentActivity {
    private static boolean zba = false;
    private boolean zbb = false;
    private SignInConfiguration zbc;
    private boolean zbd;
    private int zbe;
    private Intent zbf;

    private final void zbc() {
        getSupportLoaderManager().initLoader(0, null, new zbw(this, null));
        zba = false;
    }

    private final void zbd(int i) {
        Status status = new Status(i);
        Intent intent = new Intent();
        intent.putExtra("googleSignInStatus", status);
        setResult(0, intent);
        finish();
        zba = false;
    }

    private final void zbe(String str) {
        Intent intent = new Intent(str);
        if (str.equals("com.google.android.gms.auth.GOOGLE_SIGN_IN")) {
            intent.setPackage("com.google.android.gms");
        } else {
            intent.setPackage(getPackageName());
        }
        intent.putExtra("config", this.zbc);
        try {
            startActivityForResult(intent, 40962);
        } catch (ActivityNotFoundException e) {
            this.zbb = true;
            Log.w("AuthSignInClient", "Could not launch sign in Intent. Google Play Service is probably being updated...");
            zbd(17);
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public final boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        return true;
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected final void onActivityResult(int i, int i2, Intent intent) {
        if (this.zbb) {
            return;
        }
        setResult(0);
        switch (i) {
            case 40962:
                if (intent != null) {
                    SignInAccount signInAccount = (SignInAccount) intent.getParcelableExtra(GoogleSignInApi.EXTRA_SIGN_IN_ACCOUNT);
                    if (signInAccount == null || signInAccount.zba() == null) {
                        if (intent.hasExtra("errorCode")) {
                            int intExtra = intent.getIntExtra("errorCode", 8);
                            if (intExtra == 13) {
                                intExtra = GoogleSignInStatusCodes.SIGN_IN_CANCELLED;
                            }
                            zbd(intExtra);
                            return;
                        }
                    } else {
                        GoogleSignInAccount zba2 = signInAccount.zba();
                        zbn zbc = zbn.zbc(this);
                        GoogleSignInOptions zba3 = this.zbc.zba();
                        if (zba2 != null) {
                            zbc.zbe(zba3, zba2);
                            intent.removeExtra(GoogleSignInApi.EXTRA_SIGN_IN_ACCOUNT);
                            intent.putExtra("googleSignInAccount", zba2);
                            this.zbd = true;
                            this.zbe = i2;
                            this.zbf = intent;
                            zbc();
                            return;
                        }
                        throw null;
                    }
                }
                zbd(8);
                return;
            default:
                return;
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null) {
            if ("com.google.android.gms.auth.NO_IMPL".equals(action)) {
                zbd(GoogleSignInStatusCodes.SIGN_IN_FAILED);
                return;
            } else if (action.equals("com.google.android.gms.auth.GOOGLE_SIGN_IN") || action.equals("com.google.android.gms.auth.APPAUTH_SIGN_IN")) {
                Bundle bundleExtra = intent.getBundleExtra("config");
                if (bundleExtra != null) {
                    SignInConfiguration signInConfiguration = (SignInConfiguration) bundleExtra.getParcelable("config");
                    if (signInConfiguration == null) {
                        Log.e("AuthSignInClient", "Activity started with invalid configuration.");
                        setResult(0);
                        finish();
                        return;
                    }
                    this.zbc = signInConfiguration;
                    if (bundle == null) {
                        if (zba) {
                            setResult(0);
                            zbd(GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS);
                            return;
                        }
                        zba = true;
                        zbe(action);
                        return;
                    }
                    boolean z = bundle.getBoolean("signingInGoogleApiClients");
                    this.zbd = z;
                    if (z) {
                        this.zbe = bundle.getInt("signInResultCode");
                        Intent intent2 = (Intent) bundle.getParcelable("signInResultData");
                        if (intent2 != null) {
                            this.zbf = intent2;
                            zbc();
                            return;
                        }
                        throw null;
                    }
                    return;
                }
                throw null;
            } else {
                String valueOf = String.valueOf(intent.getAction());
                Log.e("AuthSignInClient", valueOf.length() != 0 ? "Unknown action: ".concat(valueOf) : new String("Unknown action: "));
                finish();
                return;
            }
        }
        throw null;
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public final void onDestroy() {
        super.onDestroy();
        zba = false;
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean("signingInGoogleApiClients", this.zbd);
        if (this.zbd) {
            bundle.putInt("signInResultCode", this.zbe);
            bundle.putParcelable("signInResultData", this.zbf);
        }
    }
}
