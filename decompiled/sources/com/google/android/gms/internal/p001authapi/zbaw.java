package com.google.android.gms.internal.p001authapi;

import com.google.android.gms.common.Feature;
/* compiled from: com.google.android.gms:play-services-auth@@19.2.0 */
/* renamed from: com.google.android.gms.internal.auth-api.zbaw */
/* loaded from: classes3.dex */
public final class zbaw {
    public static final Feature zba;
    public static final Feature zbb;
    public static final Feature zbc;
    public static final Feature zbd;
    public static final Feature zbe;
    public static final Feature zbf;
    public static final Feature zbg;
    public static final Feature[] zbh;

    static {
        Feature feature = new Feature("auth_api_credentials_begin_sign_in", 4L);
        zba = feature;
        Feature feature2 = new Feature("auth_api_credentials_sign_out", 2L);
        zbb = feature2;
        Feature feature3 = new Feature("auth_api_credentials_authorize", 1L);
        zbc = feature3;
        Feature feature4 = new Feature("auth_api_credentials_revoke_access", 1L);
        zbd = feature4;
        Feature feature5 = new Feature("auth_api_credentials_save_password", 3L);
        zbe = feature5;
        Feature feature6 = new Feature("auth_api_credentials_get_sign_in_intent", 4L);
        zbf = feature6;
        Feature feature7 = new Feature("auth_api_credentials_save_account_linking_token", 2L);
        zbg = feature7;
        zbh = new Feature[]{feature, feature2, feature3, feature4, feature5, feature6, feature7};
    }
}
